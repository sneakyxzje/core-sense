import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";

import type { Submission } from "@src/lib/types/submission";

export const load: LayoutServerLoad = async ({ fetch, url }) => {
  const page = url.searchParams.get("page") || "0";
  const size = url.searchParams.get("size") || "10";
  const search = url.searchParams.get("search") || "";
  const [archivedSubmission] = await Promise.all([
    api.get<PageResponse<Submission>>(
      `/submissions/archived?page=${page}&size=${size}&search=${search}&sort=deleted_at,desc`,
      fetch
    ),
  ]);

  return {
    archivedSubmissions: archivedSubmission.content,
    totalPages: archivedSubmission.totalPages,
    totalElements: archivedSubmission.totalElements,
  };
};
