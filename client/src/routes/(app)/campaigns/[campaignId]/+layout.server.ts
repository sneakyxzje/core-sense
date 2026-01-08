import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";
import {
  type CampaignStage,
  type CampaignWithSubmission,
} from "@src/lib/types/campaign";
import type { Automation } from "@src/lib/types/automation";

export const load: LayoutServerLoad = async ({ fetch, params, url }) => {
  const campaignId = params.campaignId;
  const page = url.searchParams.get("page") || "0";
  const size = url.searchParams.get("size") || "10";
  const search = url.searchParams.get("search") || "";
  const [campaignStages, campaignWithSubmission, automations] =
    await Promise.all([
      api.get<CampaignStage[]>(`/campaigns/${campaignId}/stages`, fetch),
      api.get<CampaignWithSubmission>(
        `/campaigns/${campaignId}/submissions?page=${page}&size=${size}&search=${search}&sort=submittedAt,desc`,
        fetch
      ),
      api.get<Automation[]>(`/campaigns/${campaignId}/automations`, fetch),
    ]);

  const campaignName = campaignWithSubmission.campaign?.name;
  return {
    campaign: campaignWithSubmission.campaign,
    submissions: campaignWithSubmission.submissions,
    campaignName,
    campaignId,
    stages: campaignStages,
    automations,
  };
};
