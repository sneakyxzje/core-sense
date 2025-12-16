import { type CampaignDetail, type Campaign } from "@src/lib/types/campaign";
import type { CreateCampaignRequest } from "@src/lib/types/CreateCampaignRequest";
import type { CreateCampaignResponse } from "@src/lib/types/CreateCampaignResponse";
import { api } from "@src/lib/utils/api";

export const getCampaigns = async () => {
  const data = await api.get<Campaign[]>("/campaign");
  return data;
};

export const createCampaign = async (payload: CreateCampaignRequest) => {
  const data = await api.post<CreateCampaignResponse, CreateCampaignRequest>(
    "/campaign",
    payload
  );
  return data;
};

export const getCampaignById = async (campaignId: string) => {
  const data = await api.get<CampaignDetail>(`/campaign/${campaignId}`);
  return data;
};
