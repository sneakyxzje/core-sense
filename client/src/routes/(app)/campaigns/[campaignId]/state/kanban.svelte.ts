import type { CampaignStage } from "@src/lib/types/campaign";
import type { Submission } from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
import type { DndEvent } from "svelte-dnd-action";
import { toast } from "svelte-sonner";

type BulkAction = "MOVE_TO_STAGE" | "ARCHIVE";
export class KanbanState {
  columns = $state<CampaignStage[]>([]);
  private campaignId: string;
  open = $state(false);

  showBulk = $state(false);
  private getSubmissions: () => Submission[];
  private onSubmissionsUpdate: (newItems: Submission[]) => void;
  isDeletePopupOpen = $state(false);
  stageToDelete = $state<string | null>(null);
  targetStageId = $state<string | null>(null);
  selectedSubmissionIds = $state<Set<string>>(new Set());

  selectedCount = $derived(this.selectedSubmissionIds.size);
  selectedIds = $derived(this.selectedSubmissionIds);

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
          stageName: "Cột mới",
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

  // Bulk action
  clearSelection = () => {
    this.selectedSubmissionIds = new Set();
  };

  toggleBulkMode = () => {
    this.showBulk = !this.showBulk;
    if (!this.showBulk) {
      this.clearSelection();
    }
  };
  onShowBulk = () => {
    this.toggleBulkMode();
  };

  exitBulkMode = () => {
    this.showBulk = false;
    this.clearSelection();
  };

  toggleSelection = (submissionId: string) => {
    if (this.selectedSubmissionIds.has(submissionId)) {
      this.selectedSubmissionIds.delete(submissionId);
    } else {
      this.selectedSubmissionIds.add(submissionId);
    }
    this.selectedSubmissionIds = new Set(this.selectedSubmissionIds);
    if (this.selectedSubmissionIds.size === 0) {
      this.showBulk = false;
    }
  };

  selectAll = (submissions: Submission[]) => {
    this.selectedSubmissionIds = new Set(submissions.map((s) => s.id));
  };

  selectAllInColumn = (columnId: string) => {
    const columns = this.getSubmissions().filter((s) => s.stageId === columnId);
    columns.forEach((s) => this.selectedSubmissionIds.add(s.id));
    this.selectedSubmissionIds = new Set(this.selectedSubmissionIds);
  };

  isBulkMoveDialogOpen = $state(false);

  openBulkMoveDialog = () => {
    if (this.selectedSubmissionIds.size === 0) {
      toast.error("Vui lòng chọn ít nhất một ứng viên");
      return;
    }
    this.isBulkMoveDialogOpen = true;
  };

  closeBulkMoveDialog = () => {
    this.isBulkMoveDialogOpen = false;
  };

  static readonly BULK_ACTIONS = {
    MOVE: "MOVE_TO_STAGE",
    ARCHIVE: "ARCHIVE",
  } as const;

  bulkMove = async (targetStageId: string) => {
    const selectedIds = Array.from(this.selectedSubmissionIds);
    if (selectedIds.length === 0) {
      toast.error("Vui lòng chọn ít nhất một ứng viên");
      return;
    }
    try {
      toast.success(`Đang di chuyển ${selectedIds.length} ứng viên...`);
      await api.post(
        `/campaigns/${this.campaignId}/submissions:bulk-move`,
        {
          submissionIds: selectedIds,
          action: KanbanState.BULK_ACTIONS.MOVE,
          targetStageId: targetStageId,
        },
        fetch,
      );

      const updatedSubmissions = this.getSubmissions().map((s) => {
        if (selectedIds.includes(s.id)) {
          return { ...s, stageId: targetStageId };
        }
        return s;
      });
      this.onSubmissionsUpdate(updatedSubmissions);

      toast.success(`Đã di chuyển ${selectedIds.length} ứng viên thành công`);
      this.clearSelection();
      this.exitBulkMode();
      this.isBulkMoveDialogOpen = false;
    } catch (error) {
      console.error(error);
      toast.error("Không thể di chuyển ứng viên");
    }
  };

  bulkArchive = async () => {
    const selectedIds = Array.from(this.selectedSubmissionIds);
    if (selectedIds.length === 0) {
      toast.error("Vui lòng chọn ít nhất một ứng viên");
      return;
    }
    try {
      toast.success(`Đang lưu trữ ${selectedIds.length} ứng viên...`);
      await api.post(
        `/campaigns/${this.campaignId}/submissions:bulk-archive`,
        {
          submissionIds: selectedIds,
          action: KanbanState.BULK_ACTIONS.ARCHIVE,
        },
        fetch,
      );
      const updatedSubmissions = this.getSubmissions().map((s) => {
        if (selectedIds.includes(s.id)) {
          return { ...s, archived: true, deletedAt: new Date().toISOString() };
        }
        return s;
      });
      this.onSubmissionsUpdate(updatedSubmissions);

      toast.success(`Đã lưu trữ ${selectedIds.length} ứng viên thành công`);
      this.clearSelection();
      this.exitBulkMode();
    } catch (error) {
      console.error(error);
      toast.error("Không thể loại bỏ ứng viên");
    }
  };
  bulkDownload = async () => {
    const selectedIds = Array.from(this.selectedSubmissionIds);
    if (selectedIds.length === 0) {
      toast.error("Vui lòng chọn ít nhất một ứng viên");
      return;
    }

    try {
      toast.success(`Đang tải ${selectedIds.length} CV...`);

      const response = await api.post(
        `/campaigns/${this.campaignId}/submissions/bulk-download`,
        { submissionIds: selectedIds },
        fetch,
      );

      toast.success(`Đã tải ${selectedIds.length} CV thành công`);
      this.clearSelection();
    } catch (error) {
      console.error(error);
      toast.error("Không thể tải CV");
    }
  };

  bulkDelete = async () => {
    const selectedIds = Array.from(this.selectedSubmissionIds);
    if (selectedIds.length === 0) {
      toast.error("Vui lòng chọn ít nhất một ứng viên");
      return;
    }

    if (!confirm(`Bạn có chắc muốn xóa ${selectedIds.length} ứng viên?`)) {
      return;
    }

    try {
      await api.post(
        `/campaigns/${this.campaignId}/submissions/bulk-delete`,
        { submissionIds: selectedIds },
        fetch,
      );

      const updatedSubmissions = this.getSubmissions().filter(
        (s) => !selectedIds.includes(s.id),
      );
      this.onSubmissionsUpdate(updatedSubmissions);

      toast.success(`Đã xóa ${selectedIds.length} ứng viên`);
      this.clearSelection();
      this.exitBulkMode();
    } catch (error) {
      console.error(error);
      toast.error("Không thể xóa ứng viên");
    }
  };

  getSelectedSubmissions = (): Submission[] => {
    const selectedIds = Array.from(this.selectedSubmissionIds);
    return this.getSubmissions().filter((s) => selectedIds.includes(s.id));
  };
}
