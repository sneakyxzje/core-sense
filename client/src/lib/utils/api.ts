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

export const api = {
  get: async <T>(endpoint: string, customFetch: Fetch = fetch): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await customFetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
    return handleResponse<T>(response);
  },

  post: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await customFetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      credentials: "include",
    });
    return handleResponse<T>(response);
  },

  put: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await customFetch(url, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      credentials: "include",
    });
    return handleResponse<T>(response);
  },

  patch: async <T, D>(
    endpoint: string,
    data: D,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await customFetch(url, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      credentials: "include",
    });
    return handleResponse<T>(response);
  },

  delete: async <T>(
    endpoint: string,
    customFetch: Fetch = fetch
  ): Promise<T> => {
    const url = `${BASE_URL}${endpoint}`;
    const response = await customFetch(url, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
    return handleResponse<T>(response);
  },
};
