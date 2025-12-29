<script lang="ts">
  import type {
    CampaignDetail,
    CampaignStage,
    Submission,
    SubmissionWithStage,
  } from "@src/lib/types/campaign";
  import { formatRelativeTime } from "@src/lib/utils/FormatDate";
  import EditableHeader from "@src/routes/(app)/campaigns/[campaignId]/components/EditableHeader.svelte";
  import { Ellipsis } from "lucide-svelte";

  let {
    submissions,
    campaign,
    columns,
  }: {
    submissions: SubmissionWithStage[];
    campaign: CampaignDetail;
    columns: CampaignStage[];
  } = $props();
</script>

<div
  class="custom-scrollbar flex h-full w-full gap-4 overflow-x-auto px-2 pb-2 items-start"
>
  {#each columns as column (column.id)}
    <div class="flex h-full w-[320px] min-w-[320px] flex-col flex-shrink-0">
      <div class="mb-2 px-1 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <EditableHeader {column} {campaign} />
        </div>
        <Ellipsis class="w-4 h-4 text-muted-foreground cursor-pointer" />
      </div>

      <div
        class="custom-scrollbar flex-1 flex flex-col gap-3 p-3 border border-base-border-2 bg-base-2 rounded-xl overflow-y-auto shadow-sm"
      >
        {#each submissions.filter((s) => s.stageId === column.id) as s (s.id)}
          <button
            class="group flex flex-col cursor-pointer w-full text-left bg-base-3 border border-transparent hover:border-base-border-1 rounded-lg p-4 shadow-sm transition-all hover:shadow-md flex-shrink-0"
          >
            <div class="mb-2">
              <span
                class="text-xs font-mono text-muted-foreground uppercase tracking-tighter"
              >
                {campaign.name}
              </span>
              <h4
                class="font-semibold text-sm text-base-fg-1 mt-0.5 leading-snug"
              >
                Ứng viên: {s.fullName}
              </h4>
            </div>

            <p class="text-xs text-muted-foreground line-clamp-3 mb-3 italic">
              {s.aiAssessment || "Chưa có đánh giá từ AI..."}
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
            </div>

            <div
              class="mt-auto flex items-center justify-between pt-2 border-t border-base-border-1/50 w-full"
            >
              <span
                class="text-primary-1 text-[11px] font-bold group-hover:underline"
                >Chi tiết</span
              >
              <span class="text-[10px] text-muted-foreground">
                {formatRelativeTime(s.submittedAt)}
              </span>
            </div>
          </button>
        {:else}
          <div
            class="flex flex-col items-center justify-center py-10 opacity-40"
          >
            <p class="text-xs italic">Trống</p>
          </div>
        {/each}
      </div>
    </div>
  {/each}
</div>

<style>
  .custom-scrollbar::-webkit-scrollbar {
    width: 6px;
    height: 8px;
  }
  .custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: rgba(120, 120, 120, 0.3);
    border-radius: 10px;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background-color: rgba(120, 120, 120, 0.5);
  }
</style>
