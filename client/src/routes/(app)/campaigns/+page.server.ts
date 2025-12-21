import { api } from "@src/lib/utils/api.js";
import type { PageServerLoad } from "./$types";
import type { Campaign } from "@src/lib/types/campaign";

export const load: PageServerLoad = async ({ url, fetch }) => {
  const page = url.searchParams.get("page") || "0";
  const size = url.searchParams.get("size") || "5";
  const res = await api.get<PageResponse<Campaign>>(
    `/campaigns?page=${page}&size=${size}`,
    fetch
  );

  return {
    campaigns: res.content,
    currentPage: parseInt(page),
    size: parseInt(size),
    totalElements: res.totalElements,
    totalPages: res.totalPages,
  };
};
