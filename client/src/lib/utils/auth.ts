import type { RequestEvent } from "@sveltejs/kit";
import { parse } from "cookie";

export function syncAuthCookie(response: Response, event: RequestEvent) {
  const setCookieHeader = response.headers.getSetCookie()
    ? response.headers.getSetCookie()
    : response.headers.get("set-cookie");
  if (!setCookieHeader) return;
  const cookieArray = Array.isArray(setCookieHeader)
    ? setCookieHeader
    : [setCookieHeader];
  cookieArray.forEach((cookie) => {
    const parsed = parse(cookie);

    const [name, value] = Object.entries(parsed)[0];

    if (!name || value === undefined) return;

    event.cookies.set(name, value, {
      path: parsed.path || "/",
      httpOnly: true,
      secure: false,
      sameSite: (parsed.sameSite as any) || "strict",
      maxAge: parsed["max-age"]
        ? parseInt(parsed["max-age"])
        : 60 * 60 * 24 * 7,
    });
    event.request.headers.append("cookie", `${name}=${value}`);
  });
}
