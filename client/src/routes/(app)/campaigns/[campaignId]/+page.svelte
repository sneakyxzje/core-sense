<script lang="ts">
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import {
    Search,
    Download,
    Copy,
    Share2,
    ChevronLeft,
    ChevronRight,
  } from "lucide-svelte";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu";
  import { useDebounce } from "@src/lib/hooks/useDebounce.svelte.js";
  import { api } from "@src/lib/utils/api.js";
  import type {
    CampaignWithSubmission,
    Submission,
  } from "@src/lib/types/campaign.js";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import { toast } from "svelte-sonner";
  import ComparisonModal from "@src/routes/(app)/campaigns/[campaignId]/components/ComparisonModal.svelte";
  import SubmissionDetail from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionDetail.svelte";
  import StatsCard from "@src/routes/(app)/campaigns/[campaignId]/components/StatsCard.svelte";
  import SubmissionTable from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionTable.svelte";
  import Ellipsis from "@lucide/svelte/icons/ellipsis";
  import { fly } from "svelte/transition";
  import X from "@lucide/svelte/icons/x";
  import Check from "@lucide/svelte/icons/check";

  let { data } = $props();

  let submissions = $state(data.submissions.content || []);
  let campaign = $state(data.campaign);

  let currentPage = $state(data.submissions.number || 0);
  let totalPages = $state(data.submissions.totalPages || 0);
  let totalElements = $state(data.submissions.totalElements || 0);
  let pageSize = 10;

  let isSheetOpen = $state(false);
  const formUrl = `http://localhost:5173/p`;
  const sharedLink = `${formUrl}/${campaign.id}`;
  let copied = $state(false);

  const copyLink = () => {
    navigator.clipboard.writeText(sharedLink);
    toast.success("Đã copy link thành công!");
    copied = true;
    setTimeout(() => (copied = false), 2000);
  };

  let search = $state("");
  const debouncedSearch = useDebounce(() => search, 500);

  const fetchSubmissions = async (page: number, query: string) => {
    try {
      const res = await api.get<CampaignWithSubmission>(
        `/campaigns/${campaign.id}/submissions?page=${page}&size=${pageSize}&search=${query}&sort=submittedAt,desc`
      );

      submissions = res.submissions.content;
      totalPages = res.submissions.totalPages;
      totalElements = res.submissions.totalElements;
      currentPage = res.submissions.number;
    } catch (err) {
      console.error("Lỗi tải dữ liệu:", err);
      toast.error("Không thể tải danh sách ứng viên");
    }
  };
  let isFirstRender = true;
  $effect(() => {
    const query = debouncedSearch.current;
    if (isFirstRender) {
      isFirstRender = false;
      return;
    }
    fetchSubmissions(0, query);
  });

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchSubmissions(newPage, debouncedSearch.current);
    }
  };

  let selectedSubmission = $state<any>(null);
  function openDetail(sub: any) {
    selectedSubmission = sub;
    isSheetOpen = true;
  }

  let stateComparison = $state<Submission[]>([]);
  let isComparisonOpen = $state(false);
  const checkComparison = (checked: boolean, sub: Submission) => {
    if (checked) {
      if (stateComparison.length < 2) {
        stateComparison.push(sub);
      } else {
        toast.warning("Chỉ được chọn tối đa 2 ứng viên để so sánh");
      }
    } else {
      stateComparison = stateComparison.filter((item) => item.id !== sub.id);
    }
  };
  const handleComparison = () => {
    isComparisonOpen = true;
  };
</script>

<div class="h-[calc(100vh-110px)] flex flex-col space-y-6">
  <div class="flex flex-col gap-6">
    <div class="flex justify-between items-start">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">Phân tích phản hồi</h1>
        <div class="flex items-center gap-2 text-muted-foreground mt-1 text-sm">
          <span class="text-base-fg-4">Chiến dịch: {campaign.name}</span>
          <span class="text-base-fg-4">•</span>
          <span class="text-base-fg-4">{totalElements} bài nộp</span>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <Dialog.Root>
          <Dialog.Trigger>
            <Button
              class="h-9 bg-primary-1 text-primary-fg-1 shadow-sm hover:bg-primary-hover border border-transparent"
            >
              <Share2 class="mr-2 h-4 w-4" /> Share
            </Button>
          </Dialog.Trigger>
          <Dialog.Content class="sm:max-w-md bg-bg border-base-border-1">
            <Dialog.Header>
              <Dialog.Title class="text-base-fg-1"
                >Chia sẻ đường dẫn</Dialog.Title
              >
              <Dialog.Description class="text-base-fg-3"
                >Bất kỳ ai có đường link này đều có thể truy cập.</Dialog.Description
              >
            </Dialog.Header>
            <div class="flex flex-col gap-3 py-4">
              <Label for="link" class="text-base-fg-2">Liên kết</Label>
              <div class="flex items-center gap-2">
                <Input
                  id="link"
                  readonly
                  value={sharedLink}
                  class="flex-1 bg-base-2 border-base-border-1 text-base-fg-1 focus-visible:ring-primary-1"
                />
                <Button
                  type="button"
                  size="icon"
                  variant="secondary"
                  class="shrink-0 border border-base-border-1 bg-base-2 hover:bg-base-3 text-base-fg-1"
                  onclick={copyLink}
                >
                  {#if copied}
                    <Check class="h-4 w-4 text-green-500" />
                  {:else}
                    <Copy class="h-4 w-4" />
                  {/if}
                </Button>
              </div>
            </div>
            <Dialog.Footer class="sm:justify-start">
              <Dialog.Close>
                <Button
                  variant="ghost"
                  class="text-base-fg-3 hover:text-base-fg-1">Đóng</Button
                >
              </Dialog.Close>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Root>

        <Button
          variant="outline"
          class="h-9 border-base-border-1 text-base-fg-2 hover:bg-base-2 hover:text-primary-1"
        >
          <Download class="mr-2 h-4 w-4" /> Xuất Excel
        </Button>
      </div>
    </div>
    <StatsCard {campaign} submissions={data.submissions.content} />
  </div>

  <div class="flex items-center justify-between py-2">
    <div class="relative w-72">
      <Search class="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
      <Input
        bind:value={search}
        type="search"
        placeholder="Tìm kiếm ứng viên..."
        class="pl-9 h-9 border border-base-border-1 bg-base-3 rounded-md focus-visible:ring-0 focus-visible:ring-offset-0"
      />
    </div>
    <div class="flex gap-2">
      <Button
        variant="ghost"
        size="icon"
        class="h-9 w-9"
        onclick={() => fetchSubmissions(currentPage, search)}
      >
        <DropdownMenu.Root>
          <DropdownMenu.Trigger>
            <Ellipsis class="w-4 h-4" />
          </DropdownMenu.Trigger>
          <DropdownMenu.Content align="end">
            <DropdownMenu.Item
              onclick={() => fetchSubmissions(currentPage, search)}
              >Làm mới dữ liệu</DropdownMenu.Item
            >
          </DropdownMenu.Content>
        </DropdownMenu.Root>
      </Button>
    </div>
  </div>

  {#if stateComparison.length >= 2}
    <div
      transition:fly={{ y: 20, duration: 300 }}
      class="fixed bottom-10 left-1/2 -translate-x-1/2 z-50 flex items-center gap-4 bg-info-1 px-6 py-3"
    >
      <div class="flex items-center gap-2 border-r border-base-border-1 pr-4">
        <span class="text-sm text-primary-fg-1 font-medium"
          >{stateComparison.length} ứng viên được chọn</span
        >
      </div>
      <button
        onclick={handleComparison}
        class="flex items-center gap-2 text-sm text-primary-fg-1 cursor-pointer bg-info-1 font-bold transition-colors"
      >
        So sánh ngay
      </button>
      <button
        onclick={() => (stateComparison = [])}
        class="p-1 hover:bg-white/10 rounded-full transition-colors"
      >
        <X class="w-4 h-4" />
      </button>
    </div>
  {/if}

  <div class="flex-1 overflow-auto">
    <SubmissionTable
      {submissions}
      {campaign}
      {stateComparison}
      {checkComparison}
      {openDetail}
    />
  </div>

  <div
    class="flex items-center justify-end space-x-2 py-4 border-t border-base-border-1"
  >
    <div class="flex-1 text-sm text-muted-foreground">
      Trang {currentPage + 1} / {totalPages || 1}
    </div>
    <div class="space-x-2 flex items-center">
      <Button
        variant="outline"
        size="sm"
        onclick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 0}
      >
        <ChevronLeft class="h-4 w-4 mr-1" /> Trước
      </Button>
      <Button
        variant="outline"
        size="sm"
        onclick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage >= totalPages - 1}
      >
        Sau <ChevronRight class="h-4 w-4 ml-1" />
      </Button>
    </div>
  </div>
</div>

<SubmissionDetail bind:isSheetOpen {selectedSubmission} {campaign} />
<ComparisonModal bind:isComparisonOpen {stateComparison} {campaign} />
