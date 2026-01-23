import { browser } from "$app/environment";
import { env } from "$env/dynamic/public";

import { getMe } from "@src/lib/api/user";
import { syncAuthCookie } from "@src/lib/utils/auth";
import { redirect } from "@sveltejs/kit";
const rawBaseUrl = browser
  ? env.PUBLIC_API_URL
  : env.PUBLIC_INTERNAL_API_URL ||
    env.PUBLIC_API_URL ||
    "http://backend:8080/api";
const BASE_URL = rawBaseUrl
  ? rawBaseUrl.trim().replace(/(\r\n|\n|\r)/gm, "")
  : "";
export const handle = async ({ event, resolve }) => {
  const originalFetch = event.fetch;
  event.fetch = async (input, init) => {
    const url = input.toString();
    if (
      url.startsWith(BASE_URL) ||
      url.includes("backend:8080") ||
      url.includes("localhost:8080")
    ) {
      const options = { ...init };
      const headers = new Headers(options.headers);
      if (!headers.has("cookie") && cookie) {
        headers.set("cookie", cookie);
      }
      options.headers = headers;
      return originalFetch(input, options);
    }

    return originalFetch(input, init);
  };
  const cookie = event.request.headers.get("cookie") || "";
  const jwt = event.cookies.get("jwt");
  const refreshToken = event.cookies.get("refresh_token");

  if (jwt) {
    try {
      const user = await getMe(event.fetch);
      console.log(user);
      event.locals.user = user;
    } catch (error: any) {
      if ((error.status === 401 || error.status === 403) && refreshToken) {
        await tryToRefresh(event);
      }
    }
  } else if (refreshToken) {
    await tryToRefresh(event);
  }
  const isAppRoute = event.route.id?.includes("(app)");

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
  const cookie = event.request.headers.get("cookie") || "";
  try {
    const refreshRes = await event.fetch(`${BASE_URL}/auth/refresh`, {
      method: "POST",
      headers: {
        cookie: cookie,
      },
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
