import type { FormQuestion } from "@src/lib/types/FormQuestion";

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
  INACTIVE: "bg-destructive hover:bg-destructive-600",
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

export interface Submission {
  userPrompts: string;
  id: string;
  aiAssessment: Record<string, any>;
  answer: Record<string, any>;
  score: number;
  submittedAt: string;
  summary: string;
}

export interface CampaignWithSubmission {
  campaign: CampaignDetail;
  submissions: Submission[];
}

export interface SubmissionDetail extends Submission {
  snapshotSchema: FormQuestion[];
}

export interface CampaignPublic {
  campaignName: string;
  formSchema: FormQuestion[];
}

export type AiAssessment = {
  score: number;
  aiAssesment: string;
  highlights: {
    text: string;
    type: "positive" | "negative" | "neutral";
    comment: string;
  }[];
};
