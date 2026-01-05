<script lang="ts">
  import { goto } from "$app/navigation";
  import * as Sheet from "$lib/components/ui/sheet";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import {
    getMappedAnswers,
    getRespondentName,
  } from "@src/lib/utils/formMapper";
  import { getScoreColor } from "@src/lib/utils/styleUtils";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/page.svelte";
  import { Calendar, SquarePen } from "lucide-svelte";

  const detailState = useCampaignState();
</script>

<Sheet.Root bind:open={detailState.isSheetOpen}>
  <Sheet.Content
    class="sm:max-w-xl w-[95vw] p-0 flex flex-col h-full border-l border-base-border-1 bg-base-3 shadow-2xl overflow-hidden sm:duration-500"
  >
    {#if detailState.selectedSubmission}
      <div
        class="relative overflow-hidden border-b border-base-border-1 bg-background px-6 py-8"
      >
        <div class="relative space-y-4">
          <div class="flex items-start justify-between gap-4">
            <div class="space-y-1.5">
              <Sheet.Title
                class="text-2xl font-extrabold tracking-tight text-primary-900 leading-tight"
              >
                {getRespondentName(
                  detailState.selectedSubmission.answer,
                  detailState.campaign?.formSchema
                )}
              </Sheet.Title>
              <div class="flex items-center gap-3 text-slate-500">
                <div
                  class="flex items-center gap-1.5 bg-base-2 px-2 py-1 rounded-md border border-base-border-1 shadow-sm text-xs font-medium"
                >
                  <Calendar class="w-3.5 h-3.5 " />
                  {new Date(
                    detailState.selectedSubmission.submittedAt
                  ).toLocaleDateString("vi-VN")}
                </div>
                <span
                  class="text-[10px] uppercase tracking-wider font-bold opacity-40"
                  >•</span
                >
                <span class="text-xs font-medium italic">
                  {new Date(
                    detailState.selectedSubmission.submittedAt
                  ).toLocaleTimeString("vi-VN", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              </div>
            </div>

            <div
              class={`flex flex-col items-center justify-center min-w-[70px] p-2  border-2 shadow-sm ${getScoreColor(detailState.selectedSubmission.score)} rounded-md `}
            >
              <span
                class="text-[10px] uppercase font-bold opacity-70 leading-none mb-1"
                >Điểm số</span
              >
              <span class="text-xl font-black leading-none"
                >{detailState.selectedSubmission.score}<span
                  class="text-xs opacity-60">/10</span
                ></span
              >
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto custom-scrollbar">
        <div class="p-6 space-y-10 pb-32">
          <div class="group relative">
            <div
              class="absolute -inset-0.5 rounded-2xl blur opacity-10 group-hover:opacity-20 transition duration-500"
            ></div>
            <div
              class="relative space-y-3 bg-base-3 border border-base-border-1 rounded-xl p-5 shadow-sm"
            >
              <div class="flex items-center justify-between">
                <h4
                  class="text-[12px] text-base-fg-1 font-bold uppercase flex items-center gap-2"
                >
                  Tóm tắt phân tích của AI
                </h4>
              </div>

              <div class="text-sm leading-7 text-base-fg-2">
                {#if detailState.selectedSubmission.aiAssessment}
                  <p>
                    {detailState.selectedSubmission.aiAssessment?.summary}
                  </p>
                {:else}
                  <div
                    class="flex items-center gap-3 py-2 text-slate-400 italic"
                  >
                    <div
                      class="w-2 h-2 bg-slate-200 rounded-full animate-pulse"
                    ></div>
                    Chưa có đánh giá chi tiết...
                  </div>
                {/if}
              </div>
            </div>
          </div>

          <div class="space-y-6">
            <div class="flex items-center gap-4">
              <h4
                class="text-[11px] font-black text-slate-400 uppercase tracking-[0.2em] whitespace-nowrap"
              >
                Chi tiết câu trả lời
              </h4>
            </div>

            <div class="space-y-6">
              {#each getMappedAnswers(detailState.selectedSubmission.answer, detailState.campaign?.formSchema) as item, i}
                <div
                  class="relative pl-6 border-l-2 border-base-border-2 hover:border-base-border-hover transition-colors"
                >
                  <span
                    class="absolute -left-[9px] top-0 w-4 h-4 rounded-full bg-white border-2 border-slate-200 text-[9px] font-bold flex items-center justify-center text-slate-400"
                  >
                    {i + 1}
                  </span>

                  <div class="space-y-2">
                    <span
                      class="text-[11px] font-bold text-slate-500 uppercase tracking-wide"
                    >
                      {item.label}
                    </span>
                    <div
                      class="text-sm text-primary-900 leading-relaxed bg-background p-4 rounded-xl border border-base-border-2 whitespace-pre-wrap"
                    >
                      {item.value}
                    </div>
                  </div>
                </div>
              {/each}
            </div>
          </div>
        </div>
      </div>

      <div
        class="absolute bottom-0 left-0 right-0 p-5 bg-background backdrop-blur-md border-t border-base-border-2 flex justify-end gap-3 z-30 shadow-[0_-10px_20px_rgba(0,0,0,0,0.02)]"
      >
        <Button
          variant="ghost"
          class="font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
          onclick={() => (detailState.isSheetOpen = false)}
        >
          Đóng
        </Button>
        <Button
          class="bg-primary-1  hover:bg-primary-hover text-primary-fg-1 px-6 rounded-xl transition-all active:scale-95"
          onclick={() =>
            goto(
              `${detailState.campaign?.id}/submissions/${detailState.selectedSubmission?.id}`
            )}
        >
          <SquarePen class="w-4 h-4 mr-2" />
          Chi tiết
        </Button>
      </div>
    {/if}
  </Sheet.Content>
</Sheet.Root>

<style>
  .custom-scrollbar::-webkit-scrollbar {
    width: 4px;
  }
  .custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 10px;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #cbd5e1;
  }
</style>
