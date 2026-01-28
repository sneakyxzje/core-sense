import { browser } from "$app/environment";
import { getBaseUrl } from "@src/lib/config/_api";

export type Fetch = typeof fetch;
let refreshPromise: Promise<boolean> | null = null;
const handleResponse = async <T>(response: Response): Promise<T> => {
  const text = await response.text();
  let data;
  try {
    data = text ? JSON.parse(text) : {};
  } catch (err) {
    data = { message: text };
  }

  if (!response.ok) {
    const errorMessage =
      (data && data.message) || `HTTP Error: ${response.status}`;

    throw {
      message: errorMessage,
      status: response.status,
      data: data,
    };
  }

  return data as T;
};

const refreshAccessToken = async (
  customFetch: Fetch,
  headers: Record<string, string>,
): Promise<boolean> => {
  if (refreshPromise) return refreshPromise;
  refreshPromise = (async () => {
    try {
      const res = await customFetch(`${getBaseUrl()}/auth/refresh`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          ...headers,
        },
      });
      return res.ok;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      refreshPromise = null;
    }
  })();
  return refreshPromise;
};

const request = async <T>(
  endpoint: string,
  options: Omit<RequestInit, "body"> & { body?: any },
  customFetch: Fetch = fetch,
  isRetry = false,
): Promise<T> => {
  const url = `${getBaseUrl()}${endpoint}`;
  const headers = { ...options.headers } as Record<string, string>;

  let finalBody = options.body;
  if (
    options.body &&
    !(options.body instanceof FormData) &&
    typeof options.body === "object"
  ) {
    headers["Content-Type"] = "application/json";
    finalBody = JSON.stringify(options.body);
  }
  const finalOptions = {
    ...options,
    credentials: "include" as RequestCredentials,
    headers: headers,
    body: finalBody as BodyInit,
  };

  const response = await customFetch(url, finalOptions);
  if ((response.status === 401 || response.status === 403) && !isRetry) {
    const refreshSuccess = await refreshAccessToken(customFetch, headers);

    if (refreshSuccess) {
      return request<T>(endpoint, options, customFetch, true);
    } else {
      if (browser) {
        window.location.href = "/login";
      }
    }
  }
  return handleResponse<T>(response);
};

export const api = {
  get: async <T>(
    endpoint: string,
    customFetch: Fetch = fetch,
    extraHeaders?: Record<string, string>,
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "GET", headers: extraHeaders },
      customFetch,
    );
  },

  post: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch,
    extraHeaders?: Record<string, string>,
  ): Promise<T> => {
    const isFormData = data instanceof FormData;
    return request<T>(
      endpoint,
      {
        method: "POST",
        body: isFormData ? (data as BodyInit) : (data as BodyInit),
        headers: extraHeaders,
      },
      customFetch,
    );
  },

  put: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch,
    extraHeaders?: Record<string, string>,
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "PUT", body: data as BodyInit, headers: extraHeaders },
      customFetch,
    );
  },

  patch: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch,
    extraHeaders?: Record<string, string>,
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "PATCH", body: data as BodyInit, headers: extraHeaders },
      customFetch,
    );
  },

  delete: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch,
    extraHeaders?: Record<string, string>,
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "DELETE", body: data as BodyInit, headers: extraHeaders },
      customFetch,
    );
  },
};
