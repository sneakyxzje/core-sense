<script lang="ts">
  import { formatRelativeTime } from "@src/lib/utils/dateUtils";
  import {
    CalendarDays,
    Download,
    GripVertical,
    MoveRight,
    Star,
    UserRound,
    X,
    CircleCheck,
    Check,
  } from "lucide-svelte";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import type { SubmissionWithStage } from "@src/lib/types/submission";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/page.svelte";

  let { s }: { s: SubmissionWithStage } = $props();
  const state = useCampaignState();
</script>

<button
  class="group relative flex flex-col cursor-pointer w-full text-left bg-base-3 border border-transparent hover:border-base-border-1 rounded-lg p-4 shadow-sm transition-all hover:shadow-md flex-shrink-0"
  onclick={() => state.openSummary(s)}
>
  {#if s.starred}
    <div
      class="absolute -top-[1px] -left-[1px] w-9 h-9 pointer-events-none overflow-hidden rounded-tl-lg z-10"
    >
      <div
        class="absolute top-0 left-0 w-0 h-0 border-r-[36px] border-r-transparent border-t-[36px] border-t-yellow-400 shadow-sm"
      ></div>
      <Star class="absolute top-1 left-1 w-3.5 h-3.5 text-white fill-white" />
    </div>
  {/if}

  <div
    class="absolute right-2 top-2 opacity-0 group-hover:opacity-100 transition-opacity"
  >
    <GripVertical class="w-4 h-4 text-muted-foreground" />
  </div>

  <div class="mb-2">
    <span
      class="text-xs font-mono text-muted-foreground uppercase tracking-tighter"
    >
      {state.campaign?.name}
    </span>
    <h4 class="font-semibold text-sm text-base-fg-1 mt-0.5 leading-snug pr-4">
      Ứng viên: {s.fullName}
    </h4>
  </div>

  <p class="text-xs text-muted-foreground line-clamp-2 mb-3 italic">
    {s.aiAssessment?.summary || "Chưa có đánh giá từ AI..."}
  </p>

  <div class="flex items-center gap-2 mb-3">
    <span
      class="bg-blue-500/10 text-blue-500 text-[10px] px-1.5 py-0.5 rounded font-medium"
      >New</span
    >
    {#if s.aiAssessment}
      <span
        class="bg-green-500/10 text-green-500 text-[10px] px-1.5 py-0.5 rounded font-medium"
        >AI Rated</span
      >
    {/if}
    {#if s.score}
      <span
        class="text-[10px] px-1.5 py-0.5 rounded font-medium
        {s.score <= 5
          ? 'bg-canceled text-base-1'
          : s.score < 8
            ? 'bg-priority-medium text-base-1'
            : 'bg-green-500/10 text-green-500'}"
      >
        {s.score}/10
      </span>
    {/if}
  </div>

  <div
    class="mt-auto flex items-center justify-between pt-2 border-t border-base-border-1/50 w-full"
  >
    <span class="text-primary-1 text-[11px] font-bold group-hover:underline"
      >Chi tiết</span
    >
    <span class="text-[10px] text-muted-foreground">
      {formatRelativeTime(s.submittedAt)}
    </span>
  </div>
</button>

<Dialog.Root bind:open={state.isSummaryOpen}>
  <Dialog.Content
    class="sm:max-w-[800px] bg-base-3 border-base-border-1 p-0 overflow-hidden"
  >
    <div class="px-6 py-4 bg-base-2/50 border-b border-base-border-1">
      <Dialog.Header>
        <div class="flex items-start gap-3">
          <UserRound class="w-5 h-5 mt-1 text-muted-foreground" />
          <div class="text-left">
            <Dialog.Title class="text-xl font-bold">{s.fullName}</Dialog.Title>
          </div>
        </div>
      </Dialog.Header>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-[1fr_200px] gap-0">
      <div class="p-6 space-y-8 max-h-[65vh] overflow-y-auto custom-scrollbar">
        <section>
          <div class="flex items-center gap-3 mb-3">
            <h3 class="font-bold text-sm uppercase tracking-wider">
              AI Phân tích tổng quan
            </h3>
          </div>
          <div
            class="ml-8 p-4 rounded-md bg-base-2 border border-base-border-2 text-sm leading-relaxed opacity-90"
          >
            {s.aiAssessment?.summary || "Chưa có dữ liệu đánh giá..."}
          </div>
        </section>

        <div class="grid grid-cols-1 gap-6 ml-8">
          <section>
            <div class="flex items-center gap-2 mb-2">
              <CircleCheck class="w-4 h-4 text-positive-1" />
              <h3 class="font-bold text-xs uppercase tracking-tight">
                Điểm mạnh nổi bật
              </h3>
            </div>
            <div class="space-y-2">
              <div
                class="flex items-center gap-3 p-2 rounded text-sm transition-colors border border-transparent"
              >
                <div
                  class="w-4 h-4 rounded-sm flex items-center justify-center"
                >
                  <div class="w-1.5 h-1.5 rounded-full bg-positive-1"></div>
                </div>
                {s.aiAssessment?.positive
                  ? s.aiAssessment.positive
                  : "Chưa có đánh giá"}
              </div>
            </div>
          </section>

          <section>
            <div class="flex items-center gap-2 mb-2">
              <h3 class="font-bold text-xs uppercase tracking-tight">
                Cần lưu ý
              </h3>
            </div>
            <div class="space-y-2">
              <div
                class="flex items-center gap-3 p-2 rounded text-sm transition-colors border border-transparent"
              >
                <div
                  class="w-4 h-4 rounded-sm flex items-center justify-center"
                >
                  <div class="w-1.5 h-1.5 rounded-full bg-negative-1"></div>
                </div>
                {s.aiAssessment?.negative
                  ? s.aiAssessment.negative
                  : "Chưa có đánh giá"}
              </div>
            </div>
          </section>
        </div>
      </div>

      <div class="bg-base-2/30 p-4 border-l border-base-border-1 space-y-6">
        <div>
          <h4
            class="text-[10px] font-bold uppercase text-muted-foreground mb-2"
          >
            Thông số
          </h4>
          <div class="space-y-2">
            <div
              class="p-2 bg-base-3 border border-base-border-2 rounded text-center"
            >
              <span class="block text-[10px] uppercase opacity-50"
                >AI Score</span
              >
              <span class="text-xl font-black text-positive-1">{s.score}</span>
            </div>
          </div>
        </div>

        <div>
          <h4
            class="text-[10px] font-bold uppercase text-muted-foreground mb-2"
          >
            Thao tác nhanh
          </h4>
          <div class="flex flex-col gap-2">
            <Button
              variant="secondary"
              class="justify-start gap-2 h-8 text-xs bg-base-3 hover:bg-base-border-2"
              onclick={() => state.handleInterview(state.selectedSubmission)}
            >
              <CalendarDays class="w-3.5 h-3.5" /> Phỏng vấn
            </Button>
            <Button
              variant="secondary"
              class="justify-start gap-2 h-8 text-xs bg-base-3 hover:bg-base-border-2"
            >
              <MoveRight class="w-3.5 h-3.5" /> Di chuyển
            </Button>
            <Button
              variant="secondary"
              class="justify-start gap-2 h-8 text-xs bg-base-3 hover:bg-base-border-2"
            >
              <Download class="w-3.5 h-3.5" /> Tải CV
            </Button>
          </div>
        </div>

        <div>
          <h4
            class="text-[10px] font-bold uppercase text-muted-foreground mb-2"
          >
            Hệ thống
          </h4>
          <Button
            variant="ghost"
            class="w-full justify-start gap-2 h-8 text-xs text-negative-1 hover:bg-negative-1/10"
            onclick={() => (state.isSummaryOpen = false)}
          >
            <X class="w-3.5 h-3.5" /> Đóng lại
          </Button>
        </div>
      </div>
    </div>
  </Dialog.Content>
</Dialog.Root>

<Dialog.Root bind:open={state.isInterviewOpen}>
  <Dialog.Content
    class="sm:max-w-[800px] bg-base-3 border-base-border-1 p-0 overflow-hidden"
  >
    {#if state.selectedSubmission}
      <div class="px-6 py-4 bg-base-2/50 border-b border-base-border-1">
        <Dialog.Header>
          <div class="text-left">
            <Dialog.Title class="text-xl font-bold">Phỏng vấn</Dialog.Title>
            <p class="text-xs text-muted-foreground">
              Ứng viên: {state.selectedSubmission.fullName}
            </p>
          </div>
        </Dialog.Header>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-[1fr_200px] gap-0">
        <div
          class="p-6 space-y-8 max-h-[65vh] overflow-y-auto custom-scrollbar"
        >
          <section>
            <h3 class="font-bold text-sm uppercase tracking-wider mb-4">
              Chọn thời gian
            </h3>
            <div class="ml-7">
              <input
                type="datetime-local"
                bind:value={state.interviewForm.schedule}
                class="w-full p-3 rounded-md bg-base-2 border border-base-border-2 text-sm"
              />
            </div>
          </section>

          <section>
            <h3 class="font-bold text-sm uppercase tracking-wider mb-4">
              Địa điểm / Link họp
            </h3>
            <div class="ml-7">
              <input
                type="text"
                placeholder="Google Meet link..."
                bind:value={state.interviewForm.location}
                class="w-full p-3 rounded-md bg-base-2 border border-base-border-2 text-sm"
              />
            </div>
          </section>

          <section>
            <h3 class="font-bold text-sm uppercase tracking-wider mb-4">
              Ghi chú cho ứng viên
            </h3>
            <div class="ml-7">
              <textarea
                rows="4"
                placeholder="Nhắc nhở ứng viên..."
                bind:value={state.interviewForm.notes}
                class="w-full p-3 rounded-md bg-base-2 border border-base-border-2 text-sm resize-none"
              ></textarea>
            </div>
          </section>
        </div>

        <div class="bg-base-2/30 p-4 border-l border-base-border-1 space-y-6">
          <div>
            <h4
              class="text-[10px] font-bold uppercase text-muted-foreground mb-2"
            >
              Xác nhận
            </h4>
            <div class="flex flex-col gap-2">
              <Button
                variant="default"
                class="bg-primary-1 text-white"
                onclick={() => state.executeInterview()}
                disabled={state.interviewLoading}
              >
                {#if state.interviewLoading}<span
                    class="loading loading-spinner loading-xs"
                  ></span>{/if}
                <Check class="w-3.5 h-3.5" /> Lưu lịch hẹn
              </Button>
              <Button
                variant="secondary"
                onclick={() => (state.isInterviewOpen = false)}
              >
                <X class="w-3.5 h-3.5" /> Hủy bỏ
              </Button>
            </div>
          </div>
        </div>
      </div>
    {/if}
  </Dialog.Content>
</Dialog.Root>
