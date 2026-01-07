import type { FormQuestion } from "@src/lib/types/FormQuestion";
import type { SubmissionWithStage } from "@src/lib/types/submission";

export interface Campaign {
  id: string;
  name: string;
  description: string;
  status: CampaignStatus;
  createdAt: string;
}

export type CampaignStatus = "ACTIVE" | "DRAFT" | "NEW" | "INACTIVE";

export const STATUS_LABELS: Record<CampaignStatus, string> = {
  NEW: "Mới",
  DRAFT: "Bản nháp",
  ACTIVE: "Đang hoạt động",
  INACTIVE: "Đã ngừng",
};

export const STATUS_COLORS: Record<CampaignStatus, string> = {
  ACTIVE: "bg-green-700 hover:bg-green-600",
  DRAFT: "bg-gray-500 hover:bg-gray-600",
  NEW: "bg-primary hover:bg-primary-600",
  INACTIVE: "bg-status-canceled hover:bg-destructive-600",
};

export interface CampaignDetail {
  id: string;
  name: string;
  description: string;
  status: CampaignStatus;
  formSchema: FormQuestion[];
  aiSystemPrompt: string;
  createdAt: string;
  updatedAt: string;
  totalSubmissions: number;
}

export interface CampaignWithSubmission {
  campaign: CampaignDetail;
  submissions: PageResponse<SubmissionWithStage>;
}

export interface CampaignPublic {
  campaignName: string;
  formSchema: FormQuestion[];
}

export interface CampaignStage {
  id: string;
  stageName: string;
  campaignId: string;
  position: number;
}

export interface CreateCampaignRequest {
  name: string;
  description: string;
  aiSystemPrompt: string;
  formSchema: FormQuestion[];
}

export interface CreateCampaignResponse {
  id: string;
  name: string;
  description: string;
  status: string;
  createdAt: string;
}

export interface CampaignSetting {
  automations: Array<{
    campaignId: string;
    eventCode: string;
    fromStage: string;
    toStage: string;
    status: boolean;
  }>;
}
