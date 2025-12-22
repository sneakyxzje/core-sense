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
  import type { CampaignDetail, Submission } from "@src/lib/types/campaign";
  import { getRespondentName } from "@src/lib/utils/FormMapper";
  import { getScoreColor } from "@src/lib/utils/GetScoreColor";
  import { ArrowUpDown, Eye } from "lucide-svelte";
  let {
    submissions,
    openDetail = $bindable(),
    campaign,
    checkComparison,
    stateComparison,
  }: {
    stateComparison: any[];
    submissions: Submission[];
    campaign: CampaignDetail;
    openDetail: (sub: Submission) => void;
    checkComparison: (checked: boolean, sub: Submission) => void;
  } = $props();
</script>

<div
  class="flex-1 rounded-xl border bg-background shadow-sm overflow-hidden flex flex-col"
>
  <div class="overflow-auto">
    <Table>
      <TableHeader class="bg-muted sticky top-0 z-10 border-b shadow-sm">
        <TableRow>
          <TableHead class="w-[250px]">Người gửi</TableHead>
          <TableHead class="w-[120px]">
            <Button variant="ghost" size="sm" class="-ml-3 h-8 font-bold">
              AI Score <ArrowUpDown class="ml-2 h-3 w-3" />
            </Button>
          </TableHead>
          <TableHead class="min-w-[400px]">AI Tóm tắt & Nhận định</TableHead>
          <TableHead class="w-[180px] text-right">Thời gian nộp</TableHead>
          <TableHead class="w-[60px]"></TableHead>
          <TableHead class="w-[60px] px-4"></TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {#each submissions as sub}
          <TableRow
            class="hover:bg-muted/50 cursor-pointer transition-colors group"
            onclick={() => openDetail(sub)}
          >
            <TableCell class="font-medium align-top py-4">
              <div class="flex flex-col gap-1">
                <span class="text-sm font-bold text-foreground">
                  {getRespondentName(sub.answer, campaign.formSchema)}
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
                class={`font-bold px-2.5 py-0.5 text-xs ${getScoreColor(sub.score)}`}
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
              <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8 text-muted-foreground group-hover:text-primary transition-colors"
                onclick={(e) => e.stopPropagation()}
              >
                <Eye class="w-4 h-4" />
              </Button>
            </TableCell>

            <TableCell
              class="align-top py-4"
              onclick={(e) => e.stopPropagation()}
            >
              <Checkbox
                onCheckedChange={(checked) => checkComparison(checked, sub)}
                checked={stateComparison.some((item) => item.id === sub.id)}
              />
            </TableCell>
          </TableRow>
        {/each}

        {#if campaign.totalSubmissions === 0}
          <TableRow>
            <TableCell
              colspan={5}
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
