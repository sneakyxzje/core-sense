import { getMe } from "@src/lib/api/user";
import { redirect } from "@sveltejs/kit";

export const handle = async ({ event, resolve }) => {
  const session = event.cookies.get("jwt");

  if (session) {
    try {
      const user = await getMe(event.fetch);
      event.locals.user = user;
    } catch (error) {
      event.locals.user = null;
    }
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
