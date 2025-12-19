<script lang="ts">
  import type { PageData } from ".svelte-kit/types/src/routes/(app)/campaigns/[campaignId]/submissions/$types";
  import * as Card from "$lib/components/ui/card";
  import { ClipboardList, MessageSquareText, User } from "lucide-svelte";
  import Badge from "@src/lib/components/ui/badge/badge.svelte";
  import ScrollArea from "@src/lib/components/ui/scroll-area/scroll-area.svelte";
  import Separator from "@src/lib/components/ui/separator/separator.svelte";
  import { getMappedAnswers } from "@src/lib/utils/FormMapper";
  import { formatDateTime } from "@src/lib/utils/FormatDate";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { goto } from "$app/navigation";
  let { data }: { data: PageData } = $props();
</script>

<div class="container mx-auto space-y-6">
  <div class="flex items-center gap-2 mb-8">
    <ClipboardList class="size-8 text-primary" />
    <div>
      <h1 class="text-3xl font-bold tracking-tight">
        Thống kê các câu trả lời
      </h1>
      <p class="text-muted-foreground">Xem chi tiết các phản hồi từ hệ thống</p>
    </div>
  </div>

  {#if data.submissions && data.submissions.length > 0}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {#each data.submissions as s, i}
        <Card.Root
          class="overflow-hidden flex flex-col border-t-4 border-t-primary shadow-md"
        >
          <Card.Header>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <div class="bg-primary/10 p-2 rounded-full">
                  <User class="size-4 text-primary" />
                </div>
                <Card.Title class="text-sm">
                  <div class="mb-2">
                    <span class="font-bold">
                      Phản hồi {i + 1}
                    </span>
                  </div>
                  <span>{formatDateTime(s.submittedAt)}</span>
                </Card.Title>
              </div>
              <Badge variant="outline" class="font-mono text-[10px]">
                ID: {s.id?.toString().slice(-6) || "N/A"}
              </Badge>
            </div>
          </Card.Header>

          <Card.Content class="flex-1 ">
            <ScrollArea class="h-[250px] pr-4">
              <div class="space-y-4">
                {#each getMappedAnswers(s.answer, data.campaign.formSchema) as item, idx}
                  <div class="group">
                    <div class="flex items-start gap-3">
                      <MessageSquareText
                        class="size-4 mt-1 text-muted-foreground group-hover:text-primary transition-colors"
                      />
                      <div class="space-y-1">
                        <p
                          class="text-xs font-semibold uppercase tracking-wider text-muted-foreground"
                        >
                          {item.label}
                        </p>
                        <p
                          class="text-sm leading-relaxed text-foreground bg-accent/30 p-2 rounded-md border border-accent"
                        >
                          {item.value}
                        </p>
                      </div>
                    </div>
                    {#if idx < Object.entries(s.answer).length - 1}
                      <Separator class="mt-4 opacity-50" />
                    {/if}
                  </div>
                {/each}
              </div>
            </ScrollArea>
          </Card.Content>

          <Card.Footer
            class="bg-muted/10 py-3 text-[11px] text-muted-foreground flex justify-between border-t"
          >
            <span>CoreSense AI Evaluation</span>
            <Button
              class="cursor-pointer"
              onclick={() =>
                goto(`/campaigns/${data.campaignId}/submissions/${s.id}`)}
              >Xem chi tiết</Button
            >
          </Card.Footer>
        </Card.Root>
      {/each}
    </div>
  {:else}
    <Card.Root class="border-dashed py-20">
      <Card.Content
        class="flex flex-col items-center justify-center text-center space-y-3"
      >
        <div class="bg-muted p-4 rounded-full">
          <ClipboardList class="size-10 text-muted-foreground/50" />
        </div>
        <div class="space-y-1">
          <p class="text-xl font-medium">Chưa có câu trả lời nào</p>
          <p class="text-muted-foreground text-sm">
            Dữ liệu sẽ xuất hiện khi có người thực hiện khảo sát.
          </p>
        </div>
      </Card.Content>
    </Card.Root>
  {/if}
</div>
