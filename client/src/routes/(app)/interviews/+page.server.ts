import { api } from "@src/lib/utils/api";
import type { PageServerLoad } from "./$types";
import type { Interview } from "@src/lib/types/interview";
export const load: PageServerLoad = async ({ fetch }) => {
  const [allInterview, upComingInterview] = await Promise.all([
    api.get<PageResponse<Interview>>("/interviews", fetch),
    api.get<Interview[]>("/interviews/upcoming", fetch),
  ]);
  return {
    allInterview: allInterview.content,
    upCommingInterview: upComingInterview,
  };
};
