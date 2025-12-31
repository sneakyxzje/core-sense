import { browser } from "$app/environment";

const BASE_URL = "http://localhost:8080/api";
export type Fetch = typeof fetch;
async function handleResponse<T>(response: Response): Promise<T> {
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
}

async function refreshAccessToken(customFetch: Fetch): Promise<boolean> {
  try {
    const res = await customFetch("http://localhost:8080/api/auth/refresh", {
      method: "POST",
      credentials: "include",
    });
    return res.ok;
  } catch (error) {
    return false;
  }
}

async function request<T>(
  endpoint: String,
  options: RequestInit,
  customFetch: Fetch = fetch,
  isRetry = false
): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;
  const finalOptions = {
    ...options,
    credentials: "include" as RequestCredentials,
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  };

  const response = await customFetch(url, finalOptions);
  if ((response.status === 401 || response.status === 403) && !isRetry) {
    const refreshSuccess = await refreshAccessToken(customFetch);

    if (refreshSuccess) {
      return request<T>(endpoint, options, customFetch, true);
    } else {
      if (browser) {
        window.location.href = "/login";
      }
    }
  }
  return handleResponse<T>(response);
}

export const api = {
  get: async <T>(endpoint: string, customFetch: Fetch = fetch): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    return request<T>(endpoint, { method: "GET" }, customFetch);
  },

  post: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "POST", body: JSON.stringify(data) },
      customFetch
    );
  },

  put: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "PUT", body: JSON.stringify(data) },
      customFetch
    );
  },

  patch: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "PATCH", body: JSON.stringify(data) },
      customFetch
    );
  },

  delete: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    return request<T>(
      endpoint,
      { method: "DELETE", body: JSON.stringify(data) },
      customFetch
    );
  },
};
