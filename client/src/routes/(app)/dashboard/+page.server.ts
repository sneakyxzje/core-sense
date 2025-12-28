import { api } from "@src/lib/utils/api";
import type { PageServerLoad } from "./$types";
import {
  type SubmissionSummary,
  type SubmissionChart,
  type TotalSubmissions,
} from "@src/lib/types/submission";

export const load: PageServerLoad = async ({ fetch }) => {
  const [stats, chart, summary] = await Promise.all([
    api.get<TotalSubmissions>("/campaigns/campaign-info", fetch),
    api.get<SubmissionChart[]>("/campaigns/submission-chart", fetch),
    api.get<PageResponse<SubmissionSummary>>("/submission", fetch),
  ]);

  return {
    campaignStats: stats,
    submissionChart: chart,
    submissionSummary: summary.content,
  };
};
