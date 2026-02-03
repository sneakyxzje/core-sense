import { socketClient, socketStatus } from "@src/lib/services/Socket.svelte";
import type {
  CampaignDetail,
  CampaignSetting,
  CampaignStage,
} from "@src/lib/types/campaign";
import type { Submission } from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
import { KanbanState } from "@src/routes/(app)/campaigns/[campaignId]/state/kanban.svelte";
import { SubmissionState } from "@src/routes/(app)/campaigns/[campaignId]/state/submission.svelte";
import { getContext, setContext } from "svelte";
import { toast } from "svelte-sonner";

const CONTEXT_KEY = Symbol("CAMPAIGN_STATE");
export const setCampaignState = (state: CampaignDetailState) => {
  setContext(CONTEXT_KEY, state);
};
export const useCampaignState = () => {
  return getContext<CampaignDetailState>(CONTEXT_KEY);
};

export class CampaignDetailState {
  campaign = $state<CampaignDetail>();
  submissions: SubmissionState;

  kanban: KanbanState;
  copied = $state(false);
  isSaving = $state(false);
  isCampaignSettingOpen = $state(false);
  viewMode = $state("kanban");
  activeTab = $state("general");
  constructor(initialData: {
    columns: CampaignStage[];
    submissions: Submission[];
    campaign: CampaignDetail;
    user: any;
  }) {
    this.campaign = initialData.campaign;
    const { user } = initialData;
    this.submissions = new SubmissionState(
      initialData.submissions,
      initialData.campaign.id,
    );
    this.kanban = new KanbanState({
      columns: initialData.columns,
      getSubmissions: () => this.submissions.items,
      onSubmissionsUpdate: (newItems) => {
        this.submissions.items = newItems;
      },
      campaignId: initialData.campaign.id,
    });

    $effect(() => {
      if (!socketStatus.connected || !initialData.user?.id) {
        return;
      }
      const subscription = socketClient.subscribe(
        `/topic/${user.id}/campaign/${this.campaignId}`,
        (message) => {
          const data = JSON.parse(message.body);
          console.log(data.type);
          if (data.type === "AI_AUTOMATION_MOVE") {
            const targetSub = this.submissions.items.find(
              (s) => s.id === data.submissionId,
            );
            if (targetSub) {
              targetSub.stageId = data.toStageId;
              toast.success(data.message);
            } else {
              toast.error("Đã có lỗi xảy ra");
            }
          } else if (data.type === "AI_MAIL_AUTO") {
            const target = this.submissions.items.find(
              (s) => s.id === data.submissionId,
            );
            if (target) {
              toast.success(data.message);
            } else {
              toast.error("Đã có lỗi xảy ra");
            }
          }
        },
      );
      return () => subscription.unsubscribe();
    });
  }

  onSaveSetting = async (draftSetting: CampaignSetting) => {
    if (
      draftSetting.automations[0].fromStage ===
        draftSetting.automations[0].toStage &&
      draftSetting.automations[0].status
    ) {
      toast.error("Không được để 2 cột trùng nhau");
      return;
    }
    if (this.isSaving) return;
    this.isSaving = true;

    try {
      const response = await api.put(
        `/campaigns/${this.campaignId}/settings`,
        draftSetting,
      );
      if (!response) {
        console.error("Error");
      }
      this.isCampaignSettingOpen = false;
      toast.success("Đã lưu cài đặt chiến dịch!");
    } catch (error) {
      console.error("Save failed:", error);
    } finally {
      this.isSaving = false;
    }
  };

  openCampaignSetting = () => {
    this.isCampaignSettingOpen = true;
  };

  copyLink = () => {
    navigator.clipboard.writeText(this.sharedLink);
    toast.success("Sao chép đường dẫn thành công");
    this.copied = true;
    setTimeout(() => (this.copied = false), 2000);
  };

  get campaignId() {
    return this.campaign?.id;
  }

  get sharedLink() {
    return `http://localhost:5173/p/${this.campaignId}`;
  }
}
