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
    Zap,
    ArrowRight,
  } from "lucide-svelte";
  import * as Select from "$lib/components/ui/select";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import ComparisonModal from "@src/routes/(app)/campaigns/[campaignId]/components/ComparisonModal.svelte";
  import SubmissionDetail from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionDetail.svelte";
  import StatsCard from "@src/routes/(app)/campaigns/[campaignId]/components/StatsCard.svelte";
  import SubmissionTable from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionTable.svelte";
  import { fly } from "svelte/transition";
  import X from "@lucide/svelte/icons/x";
  import Check from "@lucide/svelte/icons/check";
  import SubmissionKanban from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionKanban.svelte";
  import * as Card from "$lib/components/ui/card";
  import * as Collapsible from "$lib/components/ui/collapsible";
  import {
    CampaignDetailState,
    setCampaignState,
  } from "@src/routes/(app)/campaigns/[campaignId]/page.svelte.js";
  import Checkbox from "@src/lib/components/ui/checkbox/checkbox.svelte";
  import Info from "@lucide/svelte/icons/info";
  import ChevronDown from "@lucide/svelte/icons/chevron-down";
  import type { CampaignSetting } from "@src/lib/types/campaign";

  let { data } = $props();
  const states = new CampaignDetailState({
    campaign: data.campaign,
    submissions: data.submissions,
    columns: data.stages,
  });
  setCampaignState(states);
  let draftSettings: CampaignSetting = $state({
    automations: [
      {
        campaignId: states.campaignId ?? "",
        eventCode: data.automations?.[0]?.eventCode ?? "AI_FILTER",
        fromStage: data.automations?.[0]?.fromStageId ?? "",
        toStage: data.automations?.[0]?.toStageId ?? "",
        status: data.automations?.[0]?.isActive ?? false,
      },
    ],
  });

  const display = $derived({
    source:
      states.columns.find(
        (c) => c.id === draftSettings.automations[0].fromStage
      )?.stageName ?? "Chọn cột",
    target:
      states.columns.find((c) => c.id === draftSettings.automations[0].toStage)
        ?.stageName ?? "Chọn cột đích",
    isInvalid:
      draftSettings.automations[0].status &&
      draftSettings.automations[0].fromStage ===
        draftSettings.automations[0].toStage,
  });
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
          <span class="text-base-fg-4">{states.totalElements} bài nộp</span>
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
            onclick={states.addColumn}
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
        bind:value={states.search}
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

  {#if states.stateComparison.length >= 2}
    <div
      transition:fly={{ y: 20, duration: 300 }}
      class="fixed bottom-10 left-1/2 -translate-x-1/2 z-50 flex items-center gap-4 bg-info-1 px-6 py-3"
    >
      <div class="flex items-center gap-2 border-r border-base-border-1 pr-4">
        <span class="text-sm text-primary-fg-1 font-medium"
          >{states.stateComparison.length} ứng viên được chọn</span
        >
      </div>
      <button
        onclick={states.showComparison}
        class="flex items-center gap-2 text-sm text-primary-fg-1 cursor-pointer bg-info-1 font-bold transition-colors"
      >
        So sánh ngay
      </button>
      <button
        onclick={() => (states.stateComparison = [])}
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
        Trang {states.currentPage + 1} / {states.totalPages || 1}
      </div>
      <div class="space-x-2 flex items-center">
        <Button
          variant="outline"
          size="sm"
          onclick={() => states.handlePageChange(states.currentPage - 1)}
          disabled={states.currentPage === 0}
        >
          <ChevronLeft class="h-4 w-4 mr-1" /> Trước
        </Button>
        <Button
          variant="outline"
          size="sm"
          onclick={() => states.handlePageChange(states.currentPage + 1)}
          disabled={states.currentPage >= states.totalPages - 1}
        >
          Sau <ChevronRight class="h-4 w-4 ml-1" />
        </Button>
      </div>
    </div>
  {/if}
</div>

<SubmissionDetail />
<ComparisonModal />

<Dialog.Root bind:open={states.isCampaignSettingOpen}>
  <Dialog.Content
    class="sm:max-w-[900px] h-[80vh] bg-base-3 border-base-border-1 p-0 overflow-hidden flex flex-col gap-0"
  >
    <div
      class="px-6 py-4 bg-base-2/80 border-b border-base-border-1 flex justify-between items-center shrink-0 w-full"
    >
      <div class="text-left">
        <Dialog.Title class="text-xl font-bold flex items-center gap-2">
          Cài đặt chiến dịch
        </Dialog.Title>
        <p
          class="text-[10px] text-muted-foreground uppercase tracking-widest mt-1"
        >
          {states.campaign?.name || "Campaign Management"}
        </p>
      </div>
      <div class="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          onclick={() => (states.isCampaignSettingOpen = false)}
        >
          Đóng
        </Button>
        <Button
          size="sm"
          onclick={() => states.onSaveSetting(draftSettings)}
          class="bg-primary-1 text-white hover:bg-primary-1/90"
        >
          Lưu thay đổi
        </Button>
      </div>
    </div>

    <div class="flex flex-1 overflow-hidden">
      <aside
        class="w-[220px] bg-base-2/50 border-r border-base-border-1 p-4 flex flex-col gap-1 h-full shrink-0"
      >
        <h4
          class="px-3 text-[10px] font-bold text-muted-foreground uppercase mb-2"
        >
          Cấu hình
        </h4>

        <button
          class="flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'general'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "general")}
        >
          <Settings class="size-4" /> Cài đặt chung
        </button>

        <button
          class="flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'automation'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "automation")}
        >
          <Zap class="size-4" /> Tự động hóa
        </button>

        <div class="my-2 border-t border-base-border-1"></div>

        <h4
          class="px-3 text-[10px] font-bold text-muted-foreground uppercase mb-2"
        >
          Kết nối
        </h4>

        <button
          class="flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'sharing'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "sharing")}
        >
          <Share2 class="size-4" /> Chia sẻ & Link
        </button>
      </aside>

      <main class="flex-1 overflow-y-auto p-4 custom-scrollbar bg-base-3">
        {#if states.activeTab === "general"}
          <div
            class="max-w-xl space-y-8 animate-in fade-in slide-in-from-bottom-2"
          >
            <section>
              <h3 class="text-lg font-semibold mb-4">
                Thông tin phỏng vấn mặc định
              </h3>
              <div class="space-y-4">
                <div class="space-y-2">
                  <label class="text-xs font-medium text-muted-foreground"
                    >Địa điểm / Link họp</label
                  >
                  <input
                    type="text"
                    bind:value={states.interviewForm.location}
                    class="w-full p-2.5 rounded-md bg-base-2 border border-base-border-2 text-sm focus:ring-1 ring-primary-1 outline-none transition-all"
                    placeholder="Nhập link Google Meet hoặc địa chỉ..."
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-medium text-muted-foreground"
                    >Ghi chú mặc định</label
                  >
                  <textarea
                    rows="4"
                    bind:value={states.interviewForm.notes}
                    class="w-full p-2.5 rounded-md bg-base-2 border border-base-border-2 text-sm resize-none outline-none focus:ring-1 ring-primary-1 transition-all"
                    placeholder="Ghi chú sẽ gửi cho ứng viên..."
                  ></textarea>
                </div>
              </div>
            </section>
          </div>
        {/if}

        {#if states.activeTab === "automation"}
          <div class="animate-in fade-in slide-in-from-right-4 space-y-2">
            <div>
              <h3 class="text-lg font-semibold mb-4">Tự động hoá Kanban</h3>
              <div
                class="flex items-start gap-2 p-3 rounded-md bg-blue-500/5 border border-blue-500/20"
              >
                <Info class="size-4 text-blue-500 mt-0.5" />
                <p class="text-[11px] text-base-fg-1 leading-relaxed">
                  <strong>Lưu ý:</strong> Khi kích hoạt, ứng viên sẽ tự động được
                  chuyển cột dựa trên kết quả đánh giá từ AI.
                </p>
              </div>
            </div>

            <div class="grid gap-4">
              <Card.Root
                class="overflow-hidden border-none shadow-none transition-all duration-300 "
              >
                <Collapsible.Root
                  bind:open={draftSettings.automations[0].status}
                >
                  <div class=" flex items-center justify-between">
                    <div class="flex items-center gap-4">
                      <div class="grid gap-0.5 text-left">
                        <h4 class="font-bold text-sm tracking-tight">
                          Tự động chuyển cột khi AI đánh giá
                        </h4>
                        <p class="text-[11px] text-muted-foreground">
                          Chuyển ứng viên vào cột chỉ định sau khi hoàn tất phân
                          tích
                        </p>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 px-3 py-1.5">
                      <Checkbox
                        id="ai-auto"
                        bind:checked={draftSettings.automations[0].status}
                      />
                    </div>
                  </div>
                  <Collapsible.Content>
                    <div
                      class=" pt-2 border-t mt-4 border-base-border-1/30 bg-base-3/30 rounded-b-xl"
                    >
                      <div class="flex items-end gap-3 mt-4">
                        <div class="flex-1 space-y-2">
                          <div class="flex items-center gap-2 ml-1">
                            <span
                              class="text-[10px] font-bold text-muted-foreground uppercase tracking-widest"
                            >
                              Nguồn
                            </span>
                          </div>
                          <Select.Root
                            type="single"
                            bind:value={draftSettings.automations[0].fromStage}
                          >
                            <Select.Trigger
                              class="h-10 bg-base-2 border-base-border-2 w-full hover:border-base-border-hover transition-colors text-xs font-medium shadow-none "
                              >{display.source}</Select.Trigger
                            >
                            <Select.Content
                              class="bg-base-2 border-base-border-2"
                            >
                              {#each states.columns as stage}
                                <Select.Item value={stage.id} class="text-xs"
                                  >{stage.stageName}</Select.Item
                                >
                              {/each}
                            </Select.Content>
                          </Select.Root>
                        </div>

                        <div class="flex items-center justify-center h-10">
                          <div
                            class="p-1.5 rounded-full bg-base-3 border border-base-border-1 shadow-inner"
                          >
                            <ArrowRight class="size-3.5 text-primary-1/60" />
                          </div>
                        </div>

                        <div class="flex-1 space-y-2">
                          <div class="flex items-center gap-2 ml-1">
                            <span
                              class="text-[10px] font-bold uppercase tracking-widest"
                            >
                              Đích
                            </span>
                          </div>
                          <Select.Root
                            type="single"
                            bind:value={draftSettings.automations[0].toStage}
                          >
                            <Select.Trigger
                              class="h-10 bg-base-2 w-full border border-base-border-2 hover:border-base-border-hover transition-colors text-xs font-bold shadow-none "
                            >
                              {display.target}
                            </Select.Trigger>
                            <Select.Content
                              class="bg-base-2 border-base-border-2"
                            >
                              {#each states.columns as stage}
                                <Select.Item value={stage.id} class="text-xs"
                                  >{stage.stageName}</Select.Item
                                >
                              {/each}
                            </Select.Content>
                          </Select.Root>
                        </div>
                      </div>
                    </div>
                    {#if display.isInvalid}
                      <p
                        class="text-[10px] text-negative-1 text-center mt-[20px]"
                      >
                        Cột nguồn và đích không được trùng nhau!
                      </p>
                    {/if}
                  </Collapsible.Content>
                </Collapsible.Root>
              </Card.Root>
            </div>
          </div>
        {/if}

        {#if states.activeTab === "sharing"}
          <div class="animate-in fade-in slide-in-from-bottom-2 space-y-6">
            <h3 class="text-lg font-semibold mb-4">Chia sẻ chiến dịch</h3>
            <p class="text-sm text-muted-foreground italic">
              Tính năng này đang được phát triển...
            </p>
          </div>
        {/if}
      </main>
    </div>
  </Dialog.Content>
</Dialog.Root>
