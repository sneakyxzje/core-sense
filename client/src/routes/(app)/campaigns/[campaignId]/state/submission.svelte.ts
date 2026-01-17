import { useDebounce } from "@src/lib/hooks/useDebounce.svelte";
import type { CampaignWithSubmission } from "@src/lib/types/campaign";
import type { GeminiComparisonResponse } from "@src/lib/types/Gemini";
import type {
  Submission,
  SubmissionWithStage,
} from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
import { toast } from "svelte-sonner";

export class SubmissionState {
  items = $state<SubmissionWithStage[]>([]);
  search = $state("");
  currentPage = $state(0);
  pageSize = 10;
  totalPages = $state(0);
  totalElements = $state(0);
  isComparisonOpen = $state(false);
  stateComparison = $state<Submission[]>([]);
  debouncedSearch = useDebounce(() => this.search, 500);
  private isInitialized = false;
  private campaignId: string;
  constructor(initialItems: SubmissionWithStage[], campaignId: string) {
    this.items = initialItems;
    this.campaignId = campaignId;
    $effect(() => {
      const query = this.debouncedSearch.current;
      if (query !== undefined) {
        if (!this.isInitialized && query === "") {
          this.isInitialized = true;
          return;
        }

        this.isInitialized = true;
        this.fetchSubmissions(0, query);
      }
    });
  }

  fetchSubmissions = async (page: number, query: string) => {
    try {
      const res = await api.get<CampaignWithSubmission>(
        `/campaigns/${this.campaignId}/submissions?page=${page}&size=${this.pageSize}&search=${query}&sort=submittedAt,desc`,
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

  checkComparison = (checked: boolean, sub: Submission) => {
    if (checked) {
      if (this.stateComparison.length < 2) {
        this.stateComparison.push(sub);
      } else {
        toast.warning("Chỉ được chọn tối đa 2 ứng viên để so sánh");
      }
    } else {
      this.stateComparison = this.stateComparison.filter(
        (item) => item.id !== sub.id,
      );
    }
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
    return this.items.filter((s) => s.stageId === colId);
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
      console.error(error);
    } finally {
      this.interviewLoading = false;
    }
  };
}
