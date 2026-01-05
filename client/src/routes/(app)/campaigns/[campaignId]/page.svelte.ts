import { useDebounce } from "@src/lib/hooks/useDebounce.svelte";
import type {
  CampaignDetail,
  CampaignStage,
  CampaignWithSubmission,
} from "@src/lib/types/campaign";
import type { GeminiComparisonResponse } from "@src/lib/types/Gemini";
import type {
  Submission,
  SubmissionWithStage,
} from "@src/lib/types/submission";
import { api } from "@src/lib/utils/api";
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
  submissions = $state<SubmissionWithStage[]>([]);
  campaign = $state<CampaignDetail>();
  isInterviewOpen = $state(false);
  interviewLoading = $state(false);
  interviewForm = $state({
    schedule: "",
    location: "",
    notes: "",
  });
  comparisonResult = $state<GeminiComparisonResponse | null>(null);
  isProcessing = $state(false);
  // Page & Search State
  currentPage = $state(0);
  search = $state("");
  pageSize = 10;
  totalPages = $state(0);
  totalElements = $state(0);
  debouncedSearch = useDebounce(() => this.search, 500);

  //   UI State
  private isFirstRender = true;
  viewMode = $state("kanban");
  isSheetOpen = $state(false);
  isComparisonOpen = $state(false);
  isSummaryOpen = $state(false);
  columns = $state<CampaignStage[]>([]);
  selectedSubmission = $state<Submission | null>(null);

  // Comparison
  stateComparison = $state<Submission[]>([]);
  // Copy
  copied = $state(false);

  constructor(initialData: {
    campaign: CampaignDetail;
    submissions: PageResponse<SubmissionWithStage>;
    columns: CampaignStage[];
  }) {
    this.campaign = initialData.campaign;
    this.submissions = initialData.submissions.content || [];
    this.totalPages = initialData.submissions.totalPages || 0;
    this.columns = initialData.columns || [];
    this.totalElements = initialData.submissions.totalElements || 0;
    this.currentPage = initialData.submissions.number || 0;

    $effect(() => {
      const query = this.debouncedSearch.current;
      if (this.isFirstRender) {
        this.isFirstRender = false;
        return;
      }
      this.fetchSubmissions(0, query);
    });
  }

  //   Getters
  get campaignId() {
    return this.campaign?.id;
  }
  get sharedLink() {
    return `http://localhost:5173/p/${this.campaignId}`;
  }

  fetchSubmissions = async (page: number, query: string) => {
    try {
      const res = await api.get<CampaignWithSubmission>(
        `/campaigns/${this.campaignId}/submissions?page=${page}&size=${this.pageSize}&search=${query}&sort=submittedAt,desc`
      );

      this.submissions = res.submissions.content;
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
        (item) => item.id !== sub.id
      );
    }
  };

  onComparisonSubmit = async (
    stateComparison: any
  ): Promise<GeminiComparisonResponse | undefined> => {
    this.isProcessing = true;
    try {
      const res = await api.post<GeminiComparisonResponse, null>(
        `/gemini/compare`,
        stateComparison
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

  openDetail = (sub: any) => {
    this.selectedSubmission = sub;
    this.isSheetOpen = true;
  };

  openSummary = (sub: Submission) => {
    this.selectedSubmission = sub;
    this.isSummaryOpen = true;
  };

  copyLink = () => {
    navigator.clipboard.writeText(this.sharedLink);
    toast.success("Sao chép đường dẫn thành công");
    this.copied = true;
    setTimeout(() => (this.copied = false), 2000);
  };

  handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.fetchSubmissions(newPage, this.debouncedSearch.current);
    }
  };

  addColumn = async () => {
    try {
      const response = await api.post<CampaignStage, {}>(
        `/campaigns/${this.campaignId}/stages`,
        {
          stageName: "Cột mới",
          position: this.columns.length,
        },
        fetch
      );
      const newStage = response;
      this.columns = [...this.columns, newStage];
      toast.success("Đã tạo cột mới thành công");
    } catch (error) {
      toast.error("Không thể tạo cột, vui lòng thử lại");
    }
  };

  handleInterview = (sub: Submission | null) => {
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
