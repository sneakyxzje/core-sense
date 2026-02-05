import { api } from "@src/lib/utils/api";
import type { PageServerLoad } from "./$types";
import {
  type SubmissionSummary,
  type SubmissionChart,
  type TotalSubmissions,
} from "@src/lib/types/submission";
import { redirect } from "@sveltejs/kit";

export const load: PageServerLoad = async ({ fetch, url }) => {
  const responseCode = url.searchParams.get("vnp_ResponseCode");

  if (responseCode === "00") {
    throw redirect(303, "/dashboard?payment=success");
  } else if (responseCode && responseCode === "00") {
    throw redirect(303, "/dashboard?payment=error");
  }
  const [stats, chart, summary] = await Promise.all([
    api.get<TotalSubmissions>("/campaigns/stats/info", fetch),
    api.get<SubmissionChart[]>("/campaigns/stats/submission-chart", fetch),
    api.get<PageResponse<SubmissionSummary>>("/submissions", fetch),
  ]);

  return {
    campaignStats: stats,
    submissionChart: chart,
    submissionSummary: summary.content,
  };
};
