import { useDebounce } from "@src/lib/hooks/useDebounce.svelte";
import type { CampaignWithSubmission } from "@src/lib/types/campaign";
import type { GeminiComparisonResponse } from "@src/lib/types/Gemini";
import type { Submission } from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
import { toast } from "svelte-sonner";

interface Search {
  params: string;
  filter: {
    minScore: number | null;
    maxScore: number | null;
    from: string | null;
    to: string | null;
  };
}

type BulkAction = "MOVE_TO_STAGE" | "ARCHIVE";
export class SubmissionState {
  items = $state<Submission[]>([]);
  search = $state<Search>({
    params: "",
    filter: {
      minScore: null,
      maxScore: null,
      from: null,
      to: null,
    },
  });
  selectedSubmissions = $derived(
    this.items.filter((s) => this.selectedSubmissionIds.has(s.id)),
  );
  tempFilter = $state({
    minScore: null,
    maxScore: null,
    from: null,
    to: null,
  });
  currentPage = $state(0);
  pageSize = 10;
  totalPages = $state(0);
  totalElements = $state(0);
  isComparisonOpen = $state(false);
  debouncedSearch = useDebounce(() => this.search, 500);
  private isInitialized = false;
  private campaignId: string;
  constructor(initialItems: Submission[], campaignId: string) {
    this.items = initialItems;
    this.campaignId = campaignId;
    $effect(() => {
      const query = this.debouncedSearch.current;
      if (query !== undefined) {
        if (!this.isInitialized && query.params === "") {
          this.isInitialized = true;
          return;
        }

        this.isInitialized = true;
        this.fetchSubmissions(0, query);
      }
    });
  }

  getSubmissions = () => {
    return this.items;
  };

  fetchSubmissions = async (page: number, query: Search) => {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: this.pageSize.toString(),
        search: query.params,
        sort: "submittedAt,desc",
      });
      if (query.filter.minScore !== null)
        params.append("minScore", query.filter.minScore.toString());
      if (query.filter.maxScore !== null)
        params.append("maxScore", query.filter.maxScore.toString());
      if (query.filter.from) {
        params.append("from", `${query.filter.from}T00:00:00`);
      }
      if (query.filter.to) {
        params.append("to", `${query.filter.to}T23:59:59`);
      }
      const res = await api.get<CampaignWithSubmission>(
        `/campaigns/${this.campaignId}/submissions?${params.toString()}`,
      );

      this.items = res.submissions.content;
      this.totalPages = res.submissions.totalPages;
      this.totalElements = res.submissions.totalElements;
      this.currentPage = res.submissions.number;
    } catch (err) {
      console.error("Lỗi tải dữ liệu:", err);
      toast.error("Không thể tải danh sách ứng viên");
    }
  };

  showComparison = () => {
    this.isComparisonOpen = true;
  };

  isProcessing = $state(false);
  comparisonResult = $state<GeminiComparisonResponse | null>(null);
  onComparisonSubmit = async (
    stateComparison: any,
  ): Promise<GeminiComparisonResponse | undefined> => {
    this.isProcessing = true;
    try {
      const res = await api.post<GeminiComparisonResponse, null>(
        `/gemini/compare`,
        stateComparison,
      );
      if (res) {
        this.comparisonResult = res;
        return res;
      }
    } catch (error) {
      console.error("Lỗi khi so sánh:", error);
      return undefined;
    } finally {
      this.isProcessing = false;
    }
  };

  selectedSubmission = $state<Submission>();
  isSheetOpen = $state(false);
  openDetail = (sub: Submission) => {
    this.selectedSubmission = sub;
    this.isSheetOpen = true;
  };

  isSummaryOpen = $state(false);
  openSummary = (sub: Submission) => {
    this.selectedSubmission = sub;
    this.isSummaryOpen = true;
  };

  isEmailOpen = $state(false);
  showEmail = (s: Submission) => {
    this.selectedSubmission = s;
    this.isEmailOpen = true;
  };

  handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.fetchSubmissions(newPage, this.debouncedSearch.current);
    }
  };

  getSubmissionByColumn = (colId: string) => {
    return this.items.filter((s) => s.stageId === colId && !s.deletedAt);
  };

  handleArchive = async (submissionId: string) => {
    try {
      await api.post(`/submissions/${submissionId}/archive`, fetch);
      this.items = this.items.filter((s) => s.id !== submissionId);
      toast.success("Archive Submission Success!");
    } catch (error) {
      toast.error("Something wrong");
      console.log(error);
    }
  };

  handleStarred = async (submissionId: string) => {
    const oldItems = [...this.items];

    const updatedItems = this.items.map((s) => {
      if (s.id === submissionId) {
        return { ...s, starred: !s.starred };
      }
      return s;
    });
    this.items = updatedItems;
    try {
      const target = updatedItems.find((s) => s.id === submissionId);
      await api.patch(
        `/submissions/${submissionId}/star`,
        {
          starred: target?.starred,
        },
        fetch,
      );
    } catch (error) {
      this.items = oldItems;
      toast.error("Lỗi cập nhật");
    }
  };

  isInterviewOpen = $state(false);
  interviewLoading = $state(false);
  interviewForm = $state({
    schedule: "",
    location: "",
    notes: "",
  });

  handleInterview = (sub: Submission | undefined) => {
    this.selectedSubmission = sub;
    this.interviewForm = { schedule: "", location: "", notes: "" };
    this.isInterviewOpen = true;
    this.isSummaryOpen = false;
  };

  executeInterview = async () => {
    if (!this.selectedSubmission) return;
    if (!this.interviewForm.schedule || !this.interviewForm.location) {
      toast.warning("Vui lòng điền đầy đủ thông tin!");
      return;
    }

    this.interviewLoading = true;
    try {
      await api.post("/interviews", {
        submissionId: this.selectedSubmission.id,
        ...this.interviewForm,
        schedule: this.interviewForm.schedule + ":00",
      });
      toast.success("Đặt lịch phỏng vấn thành công!");
      this.isInterviewOpen = false;
    } catch (error) {
      toast.error("Đặt lịch phỏng vấn thất bại!");
      console.error(error);
    } finally {
      this.interviewLoading = false;
    }
  };

  applyFilter = () => {
    this.search.filter = $state.snapshot(this.tempFilter);
  };

  resetFilter = () => {
    this.tempFilter = { minScore: null, maxScore: null, from: null, to: null };
    this.applyFilter();
  };

  isBulkMoveDialogOpen = $state(false);
  selectedSubmissionIds = $state<Set<string>>(new Set());

  selectedCount = $derived(this.selectedSubmissionIds.size);
  selectedIds = $derived(this.selectedSubmissionIds);

  showBulk = $state(false);
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
          action: SubmissionState.BULK_ACTIONS.MOVE,
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
      this.items = updatedSubmissions;
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
          action: SubmissionState.BULK_ACTIONS.ARCHIVE,
        },
        fetch,
      );
      const updatedSubmissions = this.getSubmissions().map((s) => {
        if (selectedIds.includes(s.id)) {
          return { ...s, archived: true, deletedAt: new Date().toISOString() };
        }
        return s;
      });
      this.items = updatedSubmissions;
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
      this.items = updatedSubmissions;
      toast.success(`Đã xóa ${selectedIds.length} ứng viên`);
      this.clearSelection();
      this.exitBulkMode();
    } catch (error) {
      console.error(error);
      toast.error("Không thể xóa ứng viên");
    }
  };
}
