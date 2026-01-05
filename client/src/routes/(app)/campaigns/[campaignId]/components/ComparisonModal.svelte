<script lang="ts">
  import { X, Sparkles, Download } from "lucide-svelte";
  import { Button } from "$lib/components/ui/button";
  import {
    getMappedAnswers,
    getRespondentName,
  } from "@src/lib/utils/formMapper";
  import Badge from "@src/lib/components/ui/badge/badge.svelte";
  import type { GeminiComparisonResponse } from "@src/lib/types/Gemini";
  import { api } from "@src/lib/utils/api";

  let { isComparisonOpen = $bindable(), stateComparison, campaign } = $props();
  let compareResult = $state<GeminiComparisonResponse | null>(null);
  let isProcessing = $state(false);
  const onSubmit = async (
    stateComparison: any
  ): Promise<GeminiComparisonResponse | undefined> => {
    isProcessing = true;
    try {
      const res = await api.post<GeminiComparisonResponse, null>(
        `/gemini/compare`,
        stateComparison
      );
      if (res) {
        compareResult = res;
        return res;
      }
    } catch (error) {
      console.error("Lỗi khi so sánh:", error);
      return undefined;
    } finally {
      isProcessing = false;
    }
  };
</script>

{#if isComparisonOpen}
  <div
    class="fixed inset-0 z-50 bg-background/80 backdrop-blur-sm flex items-center justify-center p-4"
  >
    <div
      class="bg-base-2 border border-base-border-1 shadow-2xl rounded-2xl w-full max-w-6xl max-h-[90vh] overflow-hidden flex flex-col relative"
    >
      <button
        onclick={() => (isComparisonOpen = false)}
        class="cursor-pointer absolute right-4 top-4 p-2 hover:bg-muted rounded-full transition-colors z-20"
      >
        <X class="w-5 h-5" />
      </button>

      <div class="p-6 border-b border-base-border-1 text-center bg-muted/10">
        <h2 class="text-2xl font-bold tracking-tight">So sánh phản hồi</h2>
        <p class="text-muted-foreground">
          Phân tích sự khác biệt giữa các ứng viên
        </p>
      </div>

      <div class="flex-1 overflow-y-auto p-6 bg-dot-pattern">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 relative">
          <div
            class="hidden lg:flex absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-12 h-12 bg-background border border-base-border-1 border-primary text-primary rounded-full items-center justify-center font-black shadow-xl z-10 italic"
          >
            VS
          </div>

          {#if stateComparison}
            {#each stateComparison as s, id}
              <div
                class="flex flex-col h-full space-y-4 border border-base-border-1 rounded-lg p-6 bg-card shadow-sm hover:border-primary/50 transition-colors"
              >
                <div
                  class="flex items-start justify-between border-b border-base-border-1 pb-4"
                >
                  <div class="space-y-1">
                    <span
                      class="text-xs font-medium text-muted-foreground uppercase tracking-widest"
                      >Ứng viên {id + 1}</span
                    >
                    <h3 class="font-bold text-xl text-foreground">
                      {getRespondentName(s.answer, campaign.formSchema)}
                    </h3>
                  </div>
                  <Badge
                    variant="secondary"
                    class="px-3 py-1 text-sm font-bold bg-primary/10 text-primary border-primary/20"
                  >
                    {s.score} / 10
                  </Badge>
                </div>

                <div class="flex-1 space-y-4 py-2">
                  <h4
                    class="text-xs font-bold text-muted-foreground uppercase flex items-center gap-2"
                  >
                    Chi tiết câu trả lời
                  </h4>
                  <div class="grid gap-4">
                    {#each getMappedAnswers(s.answer, campaign.formSchema) as item}
                      <div
                        class="group border-l border-base-border-1 border-muted hover:border-primary pl-3 py-1 transition-all"
                      >
                        <p
                          class="text-[11px] font-bold text-muted-foreground uppercase leading-none mb-1.5"
                        >
                          {item.label}
                        </p>
                        <p
                          class="text-sm bg-base-3 rounded-md p-2 text-foreground/90 leading-relaxed font-medium"
                        >
                          {item.value || "—"}
                        </p>
                      </div>
                    {/each}
                  </div>
                </div>

                <div class="mt-4 pt-4 border-t border-base-border-1">
                  <div class="bg-base-3 rounded-xl p-4">
                    <p
                      class="text-xs font-bold text-muted-foreground mb-2 flex items-center gap-2"
                    >
                      Tóm tắt của AI
                    </p>
                    <p
                      class="text-sm italic text-foreground/80 leading-relaxed"
                    >
                      "{s.aiAssessment?.summary || "Đang cập nhật tóm tắt..."}"
                    </p>
                  </div>
                </div>
              </div>
            {/each}
          {/if}
        </div>

        <div class="mt-12 flex flex-col items-center space-y-4">
          <Button
            disabled={isProcessing}
            onclick={() => onSubmit(stateComparison)}
            class="px-10 py-7 text-lg font-bold bg-primary-1 text-primary-fg-1 hover:bg-primary-hover rounded-xl transition-all gap-3"
          >
            {isProcessing ? "Đang phân tích..." : "Bắt đầu so sánh bằng AI"}
          </Button>
        </div>

        <div class="mt-10 border-t border-base-border-1 pt-10">
          <div class=" p-8 border border-base-border-1 rounded-md shadow-inner">
            <div class="flex items-center justify-between mb-6">
              <div class="flex items-center gap-3">
                <h3 class="text-2xl font-bold italic tracking-tight">
                  Phân tích so sánh
                </h3>
              </div>

              {#if compareResult}
                <Badge
                  variant="outline"
                  class="font-bold border-primary/50 text-primary"
                >
                  Đánh giá: {compareResult.score}
                </Badge>
              {/if}
            </div>

            <div class="space-y-6">
              {#if compareResult}
                <div
                  class="prose prose-blue max-w-none bg-base-2 text-foreground/80 leading-relaxed p-6 rounded-2xl border border-base-border-1"
                >
                  <p
                    class="text-sm font-medium text-base-fg-1 whitespace-pre-line"
                  >
                    {compareResult.aiAssesment}
                  </p>

                  <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {#each compareResult.highlights as highlight}
                      <div
                        class="flex flex-col gap-1.5 p-3 rounded-xl border border-base-border-1 transition-all hover:shadow-sm"
                      >
                        <div class="flex items-center gap-2">
                          <span
                            class="text-xs font-black uppercase tracking-tighter opacity-70"
                          >
                            {highlight.type === "positive" ? "[+]" : "[-]"}
                          </span>
                          <span
                            class="text-sm font-bold text-base-fg-1 leading-tight"
                          >
                            {highlight.text}
                          </span>
                        </div>

                        {#if highlight.comment}
                          <p
                            class="text-xs opacity-80 text-base-fg-3 leading-snug pl-6 border-l border-current/20"
                          >
                            {highlight.comment}
                          </p>
                        {/if}
                      </div>
                    {/each}
                  </div>
                </div>
              {:else}
                <div
                  class="min-h-[200px] flex flex-col items-center justify-center border border-base-border-1 rounded-md gap-3 text-muted-foreground"
                >
                  <p class="text-sm font-medium italic">
                    Chưa có dữ liệu phân tích chi tiết. Nhấn nút phía trên để
                    bắt đầu.
                  </p>
                </div>
              {/if}
            </div>
          </div>
        </div>
      </div>

      <div class="p-4 border-t bg-muted/20 flex justify-end gap-3 px-8">
        <Button variant="ghost" onclick={() => (isComparisonOpen = false)}
          >Hủy bỏ</Button
        >
        <Button variant="default" class="gap-2">
          <Download class="w-4 h-4" /> Xuất báo cáo chi tiết
        </Button>
      </div>
    </div>
  </div>
{/if}
