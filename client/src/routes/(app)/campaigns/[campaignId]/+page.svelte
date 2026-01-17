<script lang="ts">
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import {
    Search,
    Copy,
    Share2,
    ChevronLeft,
    ChevronRight,
    Kanban,
    Table,
    Plus,
    Settings,
  } from "lucide-svelte";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import ComparisonModal from "@src/routes/(app)/campaigns/[campaignId]/components/ComparisonModal.svelte";
  import SubmissionDetail from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionDetail.svelte";
  import StatsCard from "@src/routes/(app)/campaigns/[campaignId]/components/StatsCard.svelte";
  import SubmissionTable from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionTable.svelte";
  import { fly } from "svelte/transition";
  import X from "@lucide/svelte/icons/x";
  import Check from "@lucide/svelte/icons/check";
  import SubmissionKanban from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionKanban.svelte";

  import {
    CampaignDetailState,
    setCampaignState,
  } from "@src/routes/(app)/campaigns/[campaignId]/state/index.svelte.js";

  import { page } from "$app/state";
  import Setting from "@src/routes/(app)/campaigns/[campaignId]/components/Setting.svelte";
  const user = page.data.user;

  let { data } = $props();
  const states = new CampaignDetailState({
    columns: data.stages ?? [],
    campaign: data.campaign,
    submissions: data.submissions?.content ?? data.submissions ?? [],
    user,
  });
  setCampaignState(states);
</script>

<div class="h-[calc(100vh-110px)] flex flex-col space-y-6">
  <div class="flex flex-col gap-6">
    <div class="flex justify-between items-start">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">Phân tích phản hồi</h1>
        <div class="flex items-center gap-2 text-muted-foreground mt-1 text-sm">
          <span class="text-base-fg-4">Chiến dịch: {states.campaign?.name}</span
          >
          <span class="text-base-fg-4">•</span>
          <span class="text-base-fg-4"
            >{states.submissions.totalElements} bài nộp</span
          >
        </div>
      </div>
      <div class="flex items-center gap-2">
        {#if states.viewMode === "table"}
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
                    value={states.sharedLink}
                    class="flex-1 bg-base-2 border-base-border-1 text-base-fg-1 focus-visible:ring-primary-1"
                  />
                  <Button
                    type="button"
                    size="icon"
                    variant="secondary"
                    class="shrink-0 border border-base-border-1 bg-base-2 hover:bg-base-3 text-base-fg-1"
                    onclick={states.copyLink}
                  >
                    {#if states.copied}
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
        {:else}
          <Button
            onclick={states.kanban.addColumn}
            class="h-9  bg-primary-1 text-base-1"
          >
            <Plus class="mr-2 h-4 w-4" /> Tạo cột
          </Button>
        {/if}

        <div
          class="flex items-center gap-2 bg-base-2 p-1 rounded-lg border border-base-border-2 shadow-sm"
        >
          <Button
            onclick={() => (states.viewMode = "table")}
            class="h-9   hover:bg-primary-hover {states.viewMode === 'table'
              ? 'bg-background text-base-1 shadow-sm bg-primary-3'
              : 'text-muted-foreground hover:text-base-1'}"
          >
            <Table class="mr-2 h-4 w-4" /> Table
          </Button>
          <Button
            onclick={() => (states.viewMode = "kanban")}
            class="h-9   hover:bg-primary-hover {states.viewMode === 'kanban'
              ? 'bg-background text-base-1 shadow-sm bg-primary-3'
              : 'text-muted-foreground hover:text-base-1'}"
          >
            <Kanban class="mr-2 h-4 w-4" /> Kanban
          </Button>
        </div>
      </div>
    </div>
    {#if states.viewMode === "table"}
      <StatsCard />
    {/if}
  </div>

  <div class="flex items-center justify-between py-2">
    <div class="relative w-72">
      <Search class="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
      <Input
        bind:value={states.submissions.search}
        type="search"
        placeholder="Tìm kiếm ứng viên..."
        class="pl-9 h-9 border border-base-border-1 bg-base-3 rounded-md focus-visible:ring-0 focus-visible:ring-offset-0"
      />
    </div>
    <div class="flex gap-2">
      {#if states.viewMode === "kanban"}
        <Button onclick={states.openCampaignSetting}>
          <Settings class="w-4 h-4" />
        </Button>
      {/if}
    </div>
  </div>

  {#if states.submissions.stateComparison.length >= 2}
    <div
      transition:fly={{ y: 20, duration: 300 }}
      class="fixed bottom-10 left-1/2 -translate-x-1/2 z-50 flex items-center gap-4 bg-info-1 px-6 py-3"
    >
      <div class="flex items-center gap-2 border-r border-base-border-1 pr-4">
        <span class="text-sm text-primary-fg-1 font-medium"
          >{states.submissions.stateComparison.length} ứng viên được chọn</span
        >
      </div>
      <button
        onclick={states.submissions.showComparison}
        class="flex items-center gap-2 text-sm text-primary-fg-1 cursor-pointer bg-info-1 font-bold transition-colors"
      >
        So sánh ngay
      </button>
      <button
        onclick={() => (states.submissions.stateComparison = [])}
        class="p-1 hover:bg-white/10 rounded-full transition-colors"
      >
        <X class="w-4 h-4" />
      </button>
    </div>
  {/if}

  <div class="flex-1 min-h-0 min-w-0 w-full relative border-base-border-1">
    {#if states.viewMode === "table"}
      <div class="absolute inset-0 h-full overflow-auto">
        <SubmissionTable />
      </div>
    {:else}
      <div class="absolute inset-0 overflow-auto">
        <SubmissionKanban />
      </div>
    {/if}
  </div>

  {#if states.viewMode === "table"}
    <div
      class="flex items-center justify-end space-x-2 py-4 border-t border-base-border-1"
    >
      <div class="flex-1 text-sm text-muted-foreground">
        Trang {states.submissions.currentPage + 1} / {states.submissions
          .totalPages || 1}
      </div>
      <div class="space-x-2 flex items-center">
        <Button
          variant="outline"
          size="sm"
          onclick={() =>
            states.submissions.handlePageChange(
              states.submissions.currentPage - 1,
            )}
          disabled={states.submissions.currentPage === 0}
        >
          <ChevronLeft class="h-4 w-4 mr-1" /> Trước
        </Button>
        <Button
          variant="outline"
          size="sm"
          onclick={() =>
            states.submissions.handlePageChange(
              states.submissions.currentPage + 1,
            )}
          disabled={states.submissions.currentPage >=
            states.submissions.totalPages - 1}
        >
          Sau <ChevronRight class="h-4 w-4 ml-1" />
        </Button>
      </div>
    </div>
  {/if}
</div>

<SubmissionDetail />
<ComparisonModal />
<Setting {data} />
