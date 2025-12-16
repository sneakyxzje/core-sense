import type { FormQuestion } from "@src/lib/types/FormQuestion";

export interface Campaign {
  id: string;
  name: string;
  description: string;
  status: CampaignStatus;
  createdAt: string;
}

export type CampaignStatus = "ACTIVE" | "DRAFT" | "NEW";

export interface CampaignDetail {
  id: string;
  name: string;
  description: string;
  status: CampaignStatus;
  formSchema: FormQuestion[];
  aiSystemPrompt: string;
  createdAt: string;
  updatedAt: string;
}
