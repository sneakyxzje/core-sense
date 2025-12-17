import type { CampaignWithSubmission } from "@src/lib/types/campaign.js";
import { api } from "@src/lib/utils/api.js";

export const load = async ({ params, fetch }) => {
  const { campaignId } = params;
  const response = await api.get<CampaignWithSubmission>(
    `/campaigns/${campaignId}/submissions`,
    fetch
  );
  return {
    campaign: response.campaign,
    submissions: response.submissions,
    campaignId,
  };
};
