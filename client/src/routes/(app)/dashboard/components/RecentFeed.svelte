<script lang="ts">
  import { ArrowUpRight, Clock, FileText } from "lucide-svelte";
  import * as Card from "$lib/components/ui/card";
  import { Button } from "$lib/components/ui/button";
  import { formatRelativeTime } from "@src/lib/utils/dateUtils";
  import { goto } from "$app/navigation";

  let { items } = $props<{ items: any[] }>();
</script>

<Card.Root
  class="col-span-3 border-base-border-1 bg-base-2/50 shadow-sm flex flex-col"
>
  <Card.Header>
    <div class="flex items-center justify-between">
      <div>
        <Card.Title>Phản hồi mới nhất</Card.Title>
        <Card.Description>Ghi nhận realtime từ hệ thống.</Card.Description>
      </div>
    </div>
  </Card.Header>
  <Card.Content class="flex-1 overflow-auto p-0">
    <div class="flex flex-col">
      {#each items as sub}
        <div
          class="flex items-center gap-4 p-4 border-b border-base-border-1 last:border-0 hover:bg-base-2/50 transition-colors cursor-default group"
        >
          <div
            class="flex h-9 w-9 items-center justify-center rounded-full bg-primary-1/10 text-primary-1 border border-primary-1/20 group-hover:bg-primary-1 group-hover:text-white transition-colors"
          >
            <FileText class="h-4 w-4" />
          </div>
          <div class="flex-1 space-y-1 min-w-0">
            <p
              class="text-sm font-medium leading-none text-foreground truncate"
            >
              {sub.campaignName}
            </p>
            <p class="text-xs text-muted-foreground flex items-center gap-1.5">
              <span>Vừa nhận được phản hồi mới</span>
            </p>
          </div>
          <div
            class="flex items-center text-xs text-muted-foreground whitespace-nowrap bg-base-3 px-2 py-1 rounded-md border border-base-border-1"
          >
            <Clock class="w-3 h-3 mr-1.5" />
            {formatRelativeTime(sub.submittedAt)}
          </div>
        </div>
      {/each}
      {#if items.length === 0}
        <div class="p-8 text-center text-muted-foreground text-sm">
          Chưa có hoạt động mới nào.
        </div>
      {/if}
    </div>
  </Card.Content>
  <Card.Footer class="border-t border-base-border-1 pt-4">
    <Button
      variant="ghost"
      class="w-full text-xs text-muted-foreground hover:text-foreground h-8"
      onclick={() => goto("/campaigns")}
    >
      Xem tất cả phản hồi <ArrowUpRight class="ml-2 w-3 h-3" />
    </Button>
  </Card.Footer>
</Card.Root>
