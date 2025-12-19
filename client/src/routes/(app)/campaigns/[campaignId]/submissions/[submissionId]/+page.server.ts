import type { SubmissionDetail } from "@src/lib/types/campaign.js";
import { api } from "@src/lib/utils/api.js";

export const load = async ({ params, fetch }) => {
  const { campaignId, submissionId } = params;
  const response = await api.get<SubmissionDetail>(
    `/campaigns/${campaignId}/submissions/${submissionId}`,
    fetch
  );
  return {
    submission: response,
  };
};
