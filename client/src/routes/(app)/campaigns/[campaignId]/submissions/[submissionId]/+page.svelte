<script lang="ts">
  import type { PageData } from "./$types";
  import * as Card from "$lib/components/ui/card";
  import { Badge } from "$lib/components/ui/badge";
  import { ScrollArea } from "$lib/components/ui/scroll-area";
  import { Separator } from "$lib/components/ui/separator";
  import {
    User,
    Calendar,
    BrainCircuit,
    MessageSquareQuote,
    CircleAlert,
    Loader,
    CircleCheck,
  } from "lucide-svelte";
  import { formatDateTime } from "$lib/utils/FormatDate";
  import { getMappedAnswers } from "$lib/utils/FormMapper";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { api } from "@src/lib/utils/api";
  import { renderHighlightedText } from "@src/lib/utils/HiglightText";
  import type { AiAssessment } from "@src/lib/types/campaign";

  let { data }: { data: PageData } = $props();
  let isAnalyzing = $state(false);
  let errorMessage = $state("");
  let analysisResult = $state<AiAssessment | null>(null);
  const submission = $derived(data.submission);
  const mappedAnswers = $derived(
    submission
      ? getMappedAnswers(submission.answer, submission.snapshotSchema)
      : []
  );
  const displayAnswers = $derived(
    mappedAnswers
      .filter((item) => item.type !== "files" && item.id !== "files")
      .map((item) => {
        const highlights = analysisResult?.highlights || [];
        return {
          ...item,
          htmlValue: renderHighlightedText(item.value, highlights),
        };
      })
  );
  $effect(() => {
    if (submission && submission.aiAssessment) {
      analysisResult = submission.aiAssessment as AiAssessment;
    } else {
      analysisResult = null;
    }
  });
  const analyze = async () => {
    if (isAnalyzing) return;

    isAnalyzing = true;
    errorMessage = "";
    try {
      const payload = {
        userPrompt: submission.userPrompts,
      };
      const res = await api.post(`/gemini/${submission.id}/analyze`, payload);
      analysisResult = res as AiAssessment;
    } catch (e: any) {
      console.error(e);
      errorMessage = "Lỗi khi gọi AI: " + (e.message || "Không xác định");
    } finally {
      isAnalyzing = false;
    }
  };
</script>

<div class="container mx-auto p-6 space-y-6">
  <div class="flex items-center justify-between">
    <div>
      <h1 class="text-2xl font-bold tracking-tight">Chi tiết phản hồi</h1>
      <p class="text-muted-foreground text-sm">ID: {submission?.id}</p>
    </div>
    <Badge variant="secondary" class="px-3 py-1">
      <Calendar class="size-3 mr-2" />
      {submission ? formatDateTime(submission.submittedAt) : "..."}
    </Badge>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
    <div class="space-y-6">
      <Card.Root class="border border-base-border-1">
        <Card.Header>
          <Card.Title class="flex items-center gap-2 text-lg">
            <User class="size-5 text-primary" />
            Thông tin ứng viên
          </Card.Title>
        </Card.Header>
        <Card.Content class="p-0">
          <ScrollArea class="h-[600px] p-6">
            <div class="space-y-6">
              {#each displayAnswers as item}
                <div class="space-y-2">
                  <p
                    class="text-xs font-bold text-muted-foreground uppercase tracking-wider"
                  >
                    {item.label}
                  </p>

                  <div
                    class="p-3 rounded-lg bg-accent/20 border border-base-border-1 text-sm leading-relaxed"
                  >
                    {#if item.value}
                      {@html item.htmlValue}
                    {:else}
                      <span class="text-muted-foreground italic"
                        >Không có câu trả lời</span
                      >
                    {/if}
                  </div>
                </div>
              {/each}
              {#if submission?.cvUrl}
                <div class="space-y-2 mb-6">
                  <p
                    class="text-xs font-bold text-muted-foreground uppercase tracking-wider"
                  >
                    Resume
                  </p>
                  <div
                    class="flex items-center justify-between p-4 rounded-xl bg-base-3 border border-base-border-1 group"
                  >
                    <div class="flex items-center gap-3">
                      <div>
                        <p class="text-sm font-semibold">
                          Resume của ứng viên: {submission.fullName || "null"}
                        </p>
                        <p class="text-xs text-muted-foreground uppercase">
                          .PDF
                        </p>
                      </div>
                    </div>

                    <Button
                      variant="outline"
                      size="sm"
                      href={submission.cvUrl}
                      target="_blank"
                      class="bg-primary-1 hover:bg-primary-hover text-base-1  transition-all"
                    >
                      Mở CV
                    </Button>
                  </div>
                </div>
                <Separator class="my-6" />
              {/if}
            </div>
          </ScrollArea>
        </Card.Content>
      </Card.Root>
    </div>

    <div class="space-y-6">
      {#if !analysisResult}
        <Card.Root class="border border-base-border-1">
          <Card.Content class="pt-6">
            <div
              class="flex flex-col items-center justify-center space-y-4 text-center"
            >
              <div class="space-y-1">
                <h3 class="font-semibold">Chưa có đánh giá</h3>
                <p class="text-sm text-muted-foreground">
                  Sử dụng AI để phân tích hồ sơ ứng viên tự động.
                </p>
              </div>

              {#if errorMessage}
                <p class="text-sm text-destructive flex items-center gap-2">
                  <CircleAlert class="size-4" />
                  {errorMessage}
                </p>
              {/if}

              <Button
                onclick={analyze}
                disabled={isAnalyzing}
                class="w-full sm:w-auto bg-primary-1 text-primary-fg-1 hover:bg-primary-hover min-w-[200px]"
              >
                {#if isAnalyzing}
                  <Loader class="size-4 mr-2 animate-spin" />
                  Đang phân tích...
                {:else}
                  Bắt đầu đánh giá ngay
                {/if}
              </Button>
            </div>
          </Card.Content>
        </Card.Root>
      {/if}

      {#if analysisResult}
        <Card.Root
          class="border border-base-border-1 shadow-lg animate-in fade-in slide-in-from-bottom-4 duration-500"
        >
          <Card.Header>
            <Card.Title
              class="flex items-center justify-between text-lg text-primary"
            >
              <div class="flex items-center gap-2">
                <BrainCircuit class="size-5" />
                CoreSense AI Result
              </div>
              <Button
                variant="ghost"
                size="sm"
                onclick={analyze}
                disabled={isAnalyzing}
              >
                Làm mới
              </Button>
            </Card.Title>
            <Card.Description>Phân tích tự động bởi Gemini 2.0</Card.Description
            >
          </Card.Header>
          <Card.Content class="space-y-6">
            <div
              class="flex items-center justify-between p-4 bg-primary/10 rounded-xl border border-base-border-1"
            >
              <span class="font-medium">Đánh giá chung:</span>
              <div class="flex items-center gap-2">
                <span class="text-3xl font-bold text-primary"
                  >{analysisResult.score}</span
                >
                <span class="text-muted-foreground text-sm">/ 10</span>
              </div>
            </div>

            <div class="space-y-4">
              <div class="flex items-start gap-3">
                <MessageSquareQuote class="size-5 text-blue-500 mt-1" />
                <div>
                  <p class="font-semibold text-sm">Nhận xét chi tiết</p>
                  <p
                    class="text-sm text-muted-foreground leading-relaxed italic"
                  >
                    "{analysisResult.aiAssesment}"
                  </p>
                </div>
              </div>

              <Separator />

              {#if analysisResult.aiAssesment && analysisResult.highlights.length > 0}
                <div class="space-y-3">
                  <p class="font-semibold text-sm flex items-center gap-2">
                    <CircleCheck class="size-4 text-green-500" /> Đánh giá tổng quan
                  </p>
                  <ul
                    class="list-disc list-inside text-sm text-muted-foreground space-y-1"
                  >
                    {#each analysisResult.highlights as highlight}
                      <li>
                        <span
                          class={highlight?.type === "positive"
                            ? "text-green-600 font-medium"
                            : "text-red-500 font-medium"}
                        >
                          [{highlight?.type === "positive" ? "+" : "-"}]
                        </span>
                        {highlight?.text}
                        <span class="text-xs text-muted-foreground"
                          >({highlight?.comment})</span
                        >
                      </li>
                    {/each}
                  </ul>
                </div>
              {/if}
            </div>
          </Card.Content>
        </Card.Root>
      {/if}
    </div>
  </div>
</div>
