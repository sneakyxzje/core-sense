import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";
import type { CampaignWithSubmission } from "@src/lib/types/campaign";

export const load: LayoutServerLoad = async ({ fetch, params }) => {
  const campaignId = params.campaignId;
  const res = await api.get<CampaignWithSubmission>(
    `/campaigns/${campaignId}/submissions`,
    fetch
  );
  const campaignName = res.campaign.name;
  return {
    campaign: res.campaign,
    submissions: res.submissions,
    campaignName,
    campaignId,
  };
};
