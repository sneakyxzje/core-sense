<script lang="ts">
  import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
  } from "$lib/components/ui/table";
  import Badge from "@src/lib/components/ui/badge/badge.svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import Checkbox from "@src/lib/components/ui/checkbox/checkbox.svelte";
  import { getScoreColor } from "@src/lib/utils/styleUtils";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/page.svelte";
  import { ArrowUpDown, Eye } from "lucide-svelte";

  const state = useCampaignState();
</script>

<div
  class="h-full w-full rounded-lg border border-base-border-1 bg-base-3 shadow-sm flex flex-col overflow-hidden"
>
  <div class="overflow-auto">
    <Table>
      <TableHeader class="sticky top-0 z-10 border-b shadow-sm bg-base-3">
        <TableRow class="border-base-border-1 hover:bg-transparent">
          <TableHead class="w-[250px] align-top py-4">Người gửi</TableHead>
          <TableHead class="w-[120px] align-top py-4">
            <Button
              variant="ghost"
              size="sm"
              class="-ml-3 h-auto p-0 font-bold hover:bg-transparent"
            >
              AI Score <ArrowUpDown class="ml-2 h-3 w-3" />
            </Button>
          </TableHead>
          <TableHead class="min-w-[400px] align-top py-4"
            >AI Tóm tắt & Nhận định</TableHead
          >
          <TableHead class="w-[180px] text-right align-top py-4"
            >Thời gian nộp</TableHead
          >
          <TableHead class="w-[60px] align-top py-4 text-center"></TableHead>
          <TableHead class="w-[60px] align-top py-4 text-center"></TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {#each state.submissions as sub}
          <TableRow
            class="hover:bg-base-hover cursor-pointer border-base-border-1 transition-colors group"
            onclick={() => state.openDetail(sub)}
          >
            <TableCell class="font-medium align-top py-4">
              <div class="flex flex-col gap-1">
                <span class="text-sm font-bold text-foreground">
                  {sub.fullName}
                </span>
                <code
                  class="text-[10px] text-muted-foreground bg-muted px-1.5 py-0.5 rounded w-fit"
                >
                  #{sub.id.substring(0, 8)}
                </code>
              </div>
            </TableCell>

            <TableCell class="align-top py-4">
              <Badge
                variant="outline"
                class={`font-bold px-2.5 py-0.5 text-xs whitespace-nowrap ${getScoreColor(sub.score)}`}
              >
                {sub.score ? sub.score + " / 10" : "N/A"}
              </Badge>
            </TableCell>

            <TableCell class="align-top py-4">
              <div class="flex flex-col gap-1.5 max-w-2xl">
                <p
                  class="text-sm text-foreground/80 line-clamp-2 leading-relaxed"
                >
                  {sub.aiAssessment?.summary || "Chưa có tóm tắt từ AI..."}
                </p>
              </div>
            </TableCell>

            <TableCell
              class="text-right text-muted-foreground text-xs align-top py-4"
            >
              <div class="flex flex-col items-end gap-1">
                <span>
                  {new Date(sub.submittedAt).toLocaleDateString("vi-VN")}
                </span>
                <span class="opacity-70">
                  {new Date(sub.submittedAt).toLocaleTimeString("vi-VN", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              </div>
            </TableCell>

            <TableCell class="align-top py-4">
              <div class="flex justify-center">
                <Button
                  variant="ghost"
                  size="icon"
                  class="h-8 w-8 text-muted-foreground group-hover:text-primary transition-colors"
                  onclick={(e) => e.stopPropagation()}
                >
                  <Eye class="w-4 h-4" />
                </Button>
              </div>
            </TableCell>

            <TableCell
              class="align-top py-4"
              onclick={(e) => e.stopPropagation()}
            >
              <div class="flex justify-center items-center h-8">
                <Checkbox
                  class="border-base-border-2 bg-base-2"
                  onCheckedChange={(checked) =>
                    state.checkComparison(checked, sub)}
                  checked={state.stateComparison.some(
                    (item) => item.id === sub.id
                  )}
                />
              </div>
            </TableCell>
          </TableRow>
        {/each}

        {#if state.campaign?.totalSubmissions === 0}
          <TableRow>
            <TableCell
              colspan={6}
              class="h-24 text-center text-muted-foreground"
            >
              Chưa có phản hồi nào được ghi nhận.
            </TableCell>
          </TableRow>
        {/if}
      </TableBody>
    </Table>
  </div>
</div>
