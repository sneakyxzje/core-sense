<script lang="ts">
  import type {
    CampaignDetail,
    SubmissionWithStage,
  } from "@src/lib/types/campaign";
  import { formatRelativeTime } from "@src/lib/utils/FormatDate";
  import { GripVertical } from "lucide-svelte";
  let {
    campaign,
    s,
  }: {
    campaign: CampaignDetail;
    s: SubmissionWithStage;
  } = $props();
</script>

<button
  class="group relative flex flex-col cursor-pointer w-full text-left bg-base-3 border border-transparent hover:border-base-border-1 rounded-lg p-4 shadow-sm transition-all hover:shadow-md flex-shrink-0"
>
  <div
    class="absolute right-2 top-2 opacity-0 group-hover:opacity-100 transition-opacity"
  >
    <GripVertical class="w-4 h-4 text-muted-foreground" />
  </div>

  <div class="mb-2">
    <span
      class="text-xs font-mono text-muted-foreground uppercase tracking-tighter"
    >
      {campaign.name}
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
