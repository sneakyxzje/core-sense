import type { FormQuestion } from "@src/lib/types/FormQuestion";

export type TotalSubmissions = {
  campaignId: string;
  campaignName: string;
  totalSubmissions: number;
  activeCampaigns: number;
  highQualityRatio: number;
};

export type SubmissionChart = {
  timePoint: string;
  value: number;
};

export type SubmissionEvent = {
  campaignName: string;
  submissionId: string;
  message: string;
  totalSubmissions: number;
  submittedAt: string;
};

export type SubmissionSummary = {
  campaignName: string;
  submittedAt: string;
};

export interface Submission {
  userPrompts: string;
  id: string;
  fullName: string;
  aiAssessment: Record<string, any>;
  answer: Record<string, any>;
  score: number;
  submittedAt: string;
  summary: string;
  deletedAt: string;
  campaignName: string;
}

export interface SubmissionWithStage extends Submission {
  stageId: string;
  starred: boolean;
}

export interface SubmissionDetail extends Submission {
  snapshotSchema: FormQuestion[];
  cvUrl: string;
}

export interface AiAssessment {
  score: number;
  aiAssesment: string;
  highlights: {
    text: string;
    type: "positive" | "negative" | "neutral";
    comment: string;
  }[];
}
