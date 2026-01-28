import { getMe } from "@src/lib/api/user";
import { getBaseUrl } from "@src/lib/config/_api";
import { syncAuthCookie } from "@src/lib/utils/auth";
import { redirect } from "@sveltejs/kit";

interface AppError {
  status: number;
  message: string;
}

const isAppError = (error: unknown): error is AppError => {
  return (
    typeof error === "object" &&
    error !== null &&
    "status" in error &&
    typeof (error as any).status === "number"
  );
};
export const handle = async ({ event, resolve }) => {
  const originalFetch = event.fetch;
  const APP_ROUTE = "(app)";

  event.fetch = async (input, init) => {
    const url = input.toString();
    if (
      url.startsWith(getBaseUrl()) ||
      url.includes("backend:8080") ||
      url.includes("localhost:8080")
    ) {
      const options = { ...init };
      const headers = new Headers(options.headers);
      const currentCookie = event.cookies
        .getAll()
        .map((c) => `${c.name}=${c.value}`)
        .join("; ");
      if (!headers.has("cookie") && currentCookie) {
        headers.set("cookie", currentCookie);
      }
      options.headers = headers;
      return originalFetch(input, options);
    }

    return originalFetch(input, init);
  };
  const jwt = event.cookies.get("jwt");
  const refreshToken = event.cookies.get("refresh_token");

  if (jwt) {
    try {
      const user = await getMe(event.fetch);
      event.locals.user = user;
    } catch (error: unknown) {
      if (isAppError(error)) {
        const { status } = error;
        if ((status === 401 || status === 403) && refreshToken) {
          await tryToRefresh(event);
        }
      } else {
        console.error("Unexpected error:", error);
      }
    }
  } else if (refreshToken) {
    await tryToRefresh(event);
  }
  const isAppRoute = event.route.id?.includes(APP_ROUTE);

  if (isAppRoute && !event.locals.user) {
    throw redirect(303, "/login");
  }
  if (
    event.locals.user &&
    (event.url.pathname === "/login" || event.url.pathname === "/register")
  ) {
    throw redirect(303, "/campaigns");
  }
  if (event.locals.user && event.url.pathname === "/") {
    throw redirect(303, "/campaigns");
  }
  return resolve(event);
};

async function tryToRefresh(event: any) {
  try {
    const refreshRes = await event.fetch(`${getBaseUrl()}/auth/refresh`, {
      method: "POST",
    });

    if (refreshRes.ok) {
      syncAuthCookie(refreshRes, event);
      const user = await getMe(event.fetch);
      event.locals.user = user;
    } else {
      event.locals.user = null;
    }
  } catch (err) {
    event.locals.user = null;
  }
}
