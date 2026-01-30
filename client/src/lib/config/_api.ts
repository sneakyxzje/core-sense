import { browser } from "$app/environment";
import { env } from "$env/dynamic/public";

export const getBaseUrl = () => {
  const rawBaseUrl = browser
    ? env.PUBLIC_API_URL
    : env.PUBLIC_INTERNAL_API_URL ||
      env.PUBLIC_API_URL ||
      "http://backend:8080/api";
  return rawBaseUrl ? rawBaseUrl.trim().replace(/(\r\n|\n|\r)/gm, "") : "";
};
