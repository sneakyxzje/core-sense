import type { CampaignStage } from "@src/lib/types/campaign";
import type { Submission } from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
import type { DndEvent } from "svelte-dnd-action";
import { toast } from "svelte-sonner";

export class KanbanState {
  static readonly DEFAULT_STAGE_NAME = "Cột mới";
  columns = $state<CampaignStage[]>([]);
  private campaignId: string;
  open = $state(false);
  private getSubmissions: () => Submission[];
  private onSubmissionsUpdate: (newItems: Submission[]) => void;
  isDeletePopupOpen = $state(false);
  stageToDelete = $state<string | null>(null);
  targetStageId = $state<string | null>(null);
  selectedSubmissionIds = $state<Set<string>>(new Set());

  availableStages = $derived(
    this.columns.filter((s) => s.id !== this.stageToDelete),
  );
  constructor(initialData: {
    columns: CampaignStage[];
    getSubmissions: () => Submission[];
    onSubmissionsUpdate: (items: Submission[]) => void;
    campaignId: string;
  }) {
    this.columns = initialData.columns ?? [];
    this.getSubmissions = initialData.getSubmissions;
    this.onSubmissionsUpdate = initialData.onSubmissionsUpdate;
    this.campaignId = initialData.campaignId;
  }

  addColumn = async () => {
    try {
      const response = await api.post<CampaignStage, {}>(
        `/campaigns/${this.campaignId}/stages`,
        {
          stageName: KanbanState.DEFAULT_STAGE_NAME,
          position: this.columns.length,
        },
        fetch,
      );
      const newStage = response;
      this.columns = [...this.columns, newStage];
      toast.success("Đã tạo cột mới thành công");
    } catch (error) {
      toast.error("Không thể tạo cột, vui lòng thử lại");
    }
  };

  handleDelete = (colId: string) => {
    this.stageToDelete = colId;
    if (this.getSubmissions().some((s) => s.stageId === colId)) {
      this.isDeletePopupOpen = true;
    } else {
      this.deleteColumn(colId, null);
    }
  };

  deleteColumn = async (
    stageToDelete: string | null,
    targetStage: string | null,
  ) => {
    try {
      await api.delete(
        `/campaigns/${this.campaignId}/stages/delete`,
        { stageToDelete: stageToDelete, targetStage: targetStage },
        fetch,
      );
      this.isDeletePopupOpen = false;
      this.columns = this.columns.filter((c) => c.id !== stageToDelete);
      if (targetStage) {
        const updatedSubmissions = this.getSubmissions().map((s) =>
          s.stageId === stageToDelete ? { ...s, stageId: targetStage } : s,
        );
        this.onSubmissionsUpdate(updatedSubmissions);
      }
    } catch (error) {
      console.error(error);
    }
  };

  private updateSubmissionLocal = (
    newColumnsItem: Submission[],
    columnId: string,
  ) => {
    const otherSubmissions = this.getSubmissions().filter(
      (s) => s.stageId !== columnId,
    );

    this.onSubmissionsUpdate([...otherSubmissions, ...newColumnsItem]);
  };

  dragSourceColumnId = $state<string | null>(null);
  onConsider = (columnId: string, e: CustomEvent<DndEvent<Submission>>) => {
    const { items, info } = e.detail;
    if (!this.dragSourceColumnId) {
      const movedItem = this.getSubmissions().find((s) => s.id === info.id);
      this.dragSourceColumnId = movedItem?.stageId || null;
    }
    items.forEach((item) => {
      item.stageId = columnId;
    });
  };

  onFinalize = async (
    columnId: string,
    e: CustomEvent<DndEvent<Submission>>,
  ) => {
    const { items, info } = e.detail;
    items.forEach((item) => {
      item.stageId = columnId;
    });
    const isChangingColumn =
      this.dragSourceColumnId !== null && this.dragSourceColumnId !== columnId;
    this.updateSubmissionLocal(items, columnId);
    if (isChangingColumn) {
      try {
        await api.patch(
          `/campaigns/${this.campaignId}/submissions/${info.id}/stage`,
          {
            stageId: columnId,
          },
          fetch,
        );
      } catch (error) {
        toast.error("Lỗi cập nhật server, đang hoàn tác...");
      }
    }
    this.dragSourceColumnId = null;
  };
}
