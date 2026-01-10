<script lang="ts">
  import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
  } from "$lib/components/ui/table";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { getScoreColor } from "@src/lib/utils/styleUtils";
  import { RotateCcw, Trash2 } from "lucide-svelte";
  import * as Tooltip from "$lib/components/ui/tooltip";
  import ChevronLeft from "@lucide/svelte/icons/chevron-left";
  import ChevronRight from "@lucide/svelte/icons/chevron-right";
  import { api } from "@src/lib/utils/api.js";
  import type { Submission } from "@src/lib/types/submission.js";
  import { toast } from "svelte-sonner";

  let { data } = $props();
  let submissions = $state(data.archivedSubmissions || []);
  const pageSize = 10;
  let totalPages = $state(data.totalPages || 0);
  let totalElements = $state(data.totalElements || 0);
  const fetchArchive = async (page: number) => {
    try {
      const res = await api.get<PageResponse<Submission>>(
        `/submissions/archived?page=${page}&size=${pageSize}&sort=deleted_at,desc`
      );

      submissions = res.content;
      totalPages = res.totalPages;
      totalElements = res.totalElements;
      currentPage = res.number;
    } catch (err) {
      console.error("Lỗi tải dữ liệu:", err);
    }
  };
  let currentPage = $state(0);

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < data.totalPages) {
      fetchArchive(newPage);
    }
  };

  const handleRestore = async (subId: string) => {
    try {
      await api.post(`/submissions/${subId}/restore`, fetch);
      toast.success("Restore success");
      submissions = submissions.filter((s) => s.id !== subId);
    } catch (error) {
      console.error("Error", error);
    }
  };

  const handleDelete = async (subId: string) => {
    try {
      await api.delete(`/submissions/${subId}/delete`, fetch);
      toast.success("Delete success");
      submissions = submissions.filter((s) => s.id !== subId);
    } catch (error) {
      console.error("Error", error);
    }
  };
</script>

<div
  class="h-full w-full rounded-xl border border-base-border-1 bg-base-3 shadow-sm flex flex-col overflow-hidden"
>
  <div class="overflow-auto custom-scrollbar flex-1">
    <Table class="relative w-full  border-collapse">
      <TableHeader
        class="sticky top-0 z-20 rounded-xl   border-b shadow-sm bg-base-3 backdrop-blur-sm border-b "
      >
        <TableRow class="border-base-border-1 hover:bg-transparent">
          <TableHead
            class="w-[220px] py-4 pl-6 text-xs font-bold uppercase tracking-wider"
            >Ứng viên</TableHead
          >
          <TableHead
            class="w-[180px] py-4 text-xs font-bold uppercase tracking-wider text-center"
            >Chiến dịch</TableHead
          >
          <TableHead
            class="w-[100px] py-4 text-xs font-bold uppercase tracking-wider text-center"
            >AI Score</TableHead
          >
          <TableHead
            class="min-w-[50px] py-4 text-xs  text-center font-bold uppercase tracking-wider"
            >Nhận định AI</TableHead
          >
          <TableHead
            class="w-[140px] py-4 text-xs font-bold uppercase tracking-wider text-center"
            >Thời gian nộp</TableHead
          >
          <TableHead
            class="w-[140px] py-4 text-xs font-bold uppercase tracking-wider text-center"
            >Thời gian xóa</TableHead
          >

          <TableHead
            class="w-[100px] py-4 pr-6 text-xs font-bold uppercase tracking-wider text-right"
            >Thao tác</TableHead
          >
        </TableRow>
      </TableHeader>

      <TableBody>
        {#each submissions as sub (sub.id)}
          <TableRow
            class="hover:bg-base-hover/50 cursor-pointer border-base-border-1 transition-all group"
          >
            <TableCell class="py-4 pl-6">
              <div class="flex flex-col gap-0.5">
                <span
                  class="text-sm font-semibold text-foreground group-hover:text-primary-1 transition-colors"
                >
                  {sub.fullName}
                </span>
                <span
                  class="text-[10px] text-muted-foreground font-mono bg-base-2 px-1.5 py-0.5 rounded w-fit"
                >
                  #{sub.id.substring(0, 8)}
                </span>
              </div>
            </TableCell>

            <TableCell class="py-4 text-center">
              <span
                class="text-xs text-muted-foreground px-2 py-1 rounded-md border border-base-border-1"
              >
                {sub.campaignName || "N/A"}
              </span>
            </TableCell>

            <TableCell class="py-4 text-center">
              <div
                class="inline-flex items-center justify-center px-2.5 py-1 rounded-md font-bold text-xs border {getScoreColor(
                  sub.score
                )} bg-opacity-10"
              >
                {sub.score ? sub.score + "/10" : "—"}
              </div>
            </TableCell>

            <TableCell class="py-4 align-top">
              <div class="">
                <p
                  class="text-xs text-muted-foreground/80 whitespace-normal break-words leading-relaxed italic"
                >
                  "{sub.aiAssessment?.summary || "Đang chờ AI cập nhật..."}"
                </p>
              </div>
            </TableCell>

            <TableCell class="py-4 text-center">
              <div
                class="flex flex-col text-[11px] gap-0.5 text-muted-foreground"
              >
                <span class="font-medium text-foreground/70"
                  >{new Date(sub.submittedAt).toLocaleDateString("vi-VN")}</span
                >
                <span class="opacity-60"
                  >{new Date(sub.submittedAt).toLocaleTimeString("vi-VN", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}</span
                >
              </div>
            </TableCell>

            <TableCell class="py-4 text-center">
              {#if sub.deletedAt}
                <div
                  class="flex flex-col text-[11px] gap-0.5 text-negative-1/80"
                >
                  <span class="font-medium"
                    >{new Date(sub.deletedAt).toLocaleDateString("vi-VN")}</span
                  >
                  <span class="opacity-60"
                    >{new Date(sub.deletedAt).toLocaleTimeString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}</span
                  >
                </div>
              {:else}
                <span class="text-muted-foreground opacity-30">—</span>
              {/if}
            </TableCell>

            <TableCell
              class="py-4 pr-6 text-right"
              onclick={(e) => e.stopPropagation()}
            >
              <div class="flex justify-end gap-0.5">
                <Tooltip.Root>
                  <Tooltip.Trigger>
                    <Button
                      variant="ghost"
                      size="icon"
                      class="h-8 w-8 rounded-full hover:text-primary-1 hover:bg-primary-1/10"
                      onclick={() => handleRestore(sub.id)}
                    >
                      <RotateCcw class="size-4" />
                    </Button>
                  </Tooltip.Trigger>
                  <Tooltip.Content
                    class="bg-primary-1 text-white border-none shadow-lg text-[11px]"
                    >Khôi phục ứng viên</Tooltip.Content
                  >
                </Tooltip.Root>

                <Tooltip.Root>
                  <Tooltip.Trigger>
                    <Button
                      variant="ghost"
                      size="icon"
                      class="h-8 w-8 rounded-full hover:text-negative-1 hover:bg-negative-1/10"
                      onclick={() => handleDelete(sub.id)}
                    >
                      <Trash2 class="size-4" />
                    </Button>
                  </Tooltip.Trigger>
                  <Tooltip.Content
                    class="bg-negative-1 text-white border-none shadow-lg text-[11px]"
                    >Xóa vĩnh viễn</Tooltip.Content
                  >
                </Tooltip.Root>
              </div>
            </TableCell>
          </TableRow>
        {/each}
      </TableBody>
    </Table>
  </div>
  <div
    class="flex items-center justify-end space-x-2 py-4 border-t border-base-border-1"
  >
    <div class="flex-1 text-sm text-muted-foreground">
      Trang {currentPage + 1} / {data.totalPages || 1}
    </div>
    <div class="space-x-2 flex items-center">
      <Button
        variant="outline"
        size="sm"
        onclick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 0}
      >
        <ChevronLeft class="h-4 w-4 mr-1" /> Trước
      </Button>
      <Button
        variant="outline"
        size="sm"
        onclick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage >= data.totalPages - 1}
      >
        Sau <ChevronRight class="h-4 w-4 ml-1" />
      </Button>
    </div>
  </div>
</div>

<style>
  .custom-scrollbar::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background: rgba(120, 120, 120, 0.2);
    border-radius: 10px;
  }
  :global(.line-clamp-2) {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
</style>
