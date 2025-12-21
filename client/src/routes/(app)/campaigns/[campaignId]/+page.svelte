<script lang="ts">
  import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
  } from "$lib/components/ui/table";
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import * as Sheet from "$lib/components/ui/sheet";
  import {
    Search,
    Filter,
    Download,
    Sparkles,
    Eye,
    ArrowUpDown,
    MoreHorizontal,
    Calendar,
    SquarePen,
    CircleAlert,
  } from "lucide-svelte";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu";
  import { goto } from "$app/navigation";
  import { getMappedAnswers } from "@src/lib/utils/FormMapper.js";
  import { useDebounce } from "@src/lib/hooks/useDebounce.svelte.js";
  import { api } from "@src/lib/utils/api.js";
  import type { CampaignWithSubmission } from "@src/lib/types/campaign.js";

  let { data } = $props();
  let submissions = $state(data.submissions || []);
  let campaign = $state(data.campaign);
  $inspect(submissions.map((s) => s.aiAssessment?.highlights));
  $inspect(submissions);
  let selectedSubmission = $state<any>(null);
  let isSheetOpen = $state(false);
  const getRespondentName = (answerMap: any) => {
    if (!answerMap) return "Ẩn danh";
    const values = Object.values(answerMap);
    return values.length > 0 ? String(values[0]) : "Không tên";
  };

  let search = $state("");
  const debouncedSearch = useDebounce(() => search, 500);
  $effect(() => {
    const query = debouncedSearch.current;
    const fetchSubmissions = async () => {
      try {
        const res = await api.get<CampaignWithSubmission>(
          `/campaigns/${campaign.id}/submissions?search=${query}`
        );
        submissions = Array.isArray(res)
          ? res.submissions
          : res.submissions || [];
      } catch (err) {
        console.error("Lỗi tìm kiếm:", err);
      }
    };

    fetchSubmissions();
  });
  const getScoreColor = (score: number) => {
    if (!score) return "text-slate-500 bg-slate-100 border-slate-200";
    if (score >= 8) return "text-green-700 bg-green-50 border-green-200";
    if (score >= 5) return "text-yellow-700 bg-yellow-50 border-yellow-200";
    return "text-red-700 bg-red-50 border-red-200";
  };

  function openDetail(sub: any) {
    selectedSubmission = sub;
    isSheetOpen = true;
  }
</script>

<div class="container h-[calc(100vh-60px)] flex flex-col py-6 space-y-6">
  <div class="flex flex-col gap-6">
    <div class="flex justify-between items-start">
      <div>
        <h1 class="text-2xl font-bold tracking-tight">Phân tích phản hồi</h1>
        <div class="flex items-center gap-2 text-muted-foreground mt-1 text-sm">
          <span>Chiến dịch:</span>
          <Badge variant="outline" class="font-normal text-foreground">
            {campaign.name}
          </Badge>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" size="sm">
          <Download class="w-4 h-4 mr-2" /> Xuất Excel
        </Button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div
        class="p-4 rounded-xl border bg-card shadow-sm flex items-center justify-between"
      >
        <div class="space-y-1">
          <span
            class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
            >Tổng phản hồi</span
          >
          <div class="text-2xl font-black">{campaign.totalSubmissions}</div>
        </div>
      </div>
      <div
        class="p-4 rounded-xl border bg-card shadow-sm flex items-center justify-between"
      >
        <div class="space-y-1">
          <span
            class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
            >Điểm AI TB</span
          >
          <div class="text-2xl font-black text-green-600">
            {(
              submissions.reduce((acc, curr) => acc + (curr.score || 0), 0) /
              (submissions.length || 1)
            ).toFixed(1)}/10
          </div>
        </div>
      </div>
      <div
        class="p-4 rounded-xl border bg-card shadow-sm flex items-center justify-between"
      >
        <div class="space-y-1">
          <span
            class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
            >Cần Review</span
          >
          <div class="text-2xl font-black text-red-500">
            {submissions.filter((s) => s.score < 5).length}
          </div>
        </div>
        <div
          class="h-10 w-10 rounded-full bg-red-100 flex items-center justify-center text-red-500"
        >
          <CircleAlert class="w-5 h-5" />
        </div>
      </div>
    </div>
  </div>

  <div class="flex items-center justify-between py-2">
    <div class="relative w-72">
      <Search class="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
      <Input
        bind:value={search}
        type="search"
        placeholder="Tìm kiếm ứng viên..."
        class="pl-9 h-9 rounded-lg bg-white"
      />
    </div>
    <div class="flex gap-2">
      <Button variant="outline" size="sm" class="h-9 gap-2">
        <Filter class="w-3.5 h-3.5" /> Bộ lọc
      </Button>
      <DropdownMenu.Root>
        <DropdownMenu.Trigger>
          {#snippet child({ props })}
            <Button {...props} variant="ghost" size="icon" class="h-9 w-9">
              <MoreHorizontal class="w-4 h-4" />
            </Button>
          {/snippet}
        </DropdownMenu.Trigger>
        <DropdownMenu.Content align="end">
          <DropdownMenu.Item>Làm mới dữ liệu</DropdownMenu.Item>
        </DropdownMenu.Content>
      </DropdownMenu.Root>
    </div>
  </div>

  <div
    class="flex-1 rounded-xl border bg-background shadow-sm overflow-hidden flex flex-col"
  >
    <div class="overflow-auto">
      <Table>
        <TableHeader class="bg-muted/40 sticky top-0 z-10">
          <TableRow>
            <TableHead class="w-[200px]">Người gửi</TableHead>
            <TableHead class="w-[120px]">
              <Button variant="ghost" size="sm" class="-ml-3 h-8 font-bold">
                AI Score <ArrowUpDown class="ml-2 h-3 w-3" />
              </Button>
            </TableHead>
            <TableHead class="min-w-[400px]">
              <div class="flex items-center gap-2">AI Tóm tắt & Nhận định</div>
            </TableHead>
            <TableHead class="w-[180px] text-right">Thời gian nộp</TableHead>
            <TableHead class="w-[50px]"></TableHead>
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
                    {getRespondentName(sub.answer)}
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
                  <!-- {#each sub.aiAssessment.highlights as h}
                    {#if h.type === "negative"}
                      <div
                        class="flex items-center gap-1 text-[10px] font-bold text-red-600"
                      >
                        <CircleAlert class="w-3 h-3" /> Cảnh báo tiêu cực
                      </div>
                    {/if}
                  {/each} -->
                </div>
              </TableCell>
              <TableCell
                class="text-right text-muted-foreground text-xs align-top py-4"
              >
                <div class="flex flex-col items-end gap-1">
                  <span
                    >{new Date(sub.submittedAt).toLocaleDateString(
                      "vi-VN"
                    )}</span
                  >
                  <span class="opacity-70"
                    >{new Date(sub.submittedAt).toLocaleTimeString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}</span
                  >
                </div>
              </TableCell>
              <TableCell class="align-top py-4">
                <Button
                  variant="ghost"
                  size="icon"
                  class="h-8 w-8 text-muted-foreground group-hover:text-primary transition-colors"
                >
                  <Eye class="w-4 h-4" />
                </Button>
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
</div>

<Sheet.Root bind:open={isSheetOpen}>
  <Sheet.Content
    class="sm:max-w-xl w-[95vw] p-0 flex flex-col h-full border-l shadow-2xl overflow-hidden sm:duration-500"
  >
    {#if selectedSubmission}
      <div class="relative overflow-hidden border-b bg-background px-6 py-8">
        <div class="relative space-y-4">
          <div class="flex items-start justify-between gap-4">
            <div class="space-y-1.5">
              <Sheet.Title
                class="text-2xl font-extrabold tracking-tight text-primary-900 leading-tight"
              >
                {getRespondentName(selectedSubmission.answer)}
              </Sheet.Title>
              <div class="flex items-center gap-3 text-slate-500">
                <div
                  class="flex items-center gap-1.5 bg-white px-2 py-1 rounded-md border border-slate-200 shadow-sm text-xs font-medium"
                >
                  <Calendar class="w-3.5 h-3.5 text-indigo-500" />
                  {new Date(selectedSubmission.submittedAt).toLocaleDateString(
                    "vi-VN"
                  )}
                </div>
                <span
                  class="text-[10px] uppercase tracking-wider font-bold opacity-40"
                  >•</span
                >
                <span class="text-xs font-medium italic">
                  {new Date(selectedSubmission.submittedAt).toLocaleTimeString(
                    "vi-VN",
                    { hour: "2-digit", minute: "2-digit" }
                  )}
                </span>
              </div>
            </div>

            <div
              class={`flex flex-col items-center justify-center min-w-[70px] p-2  border-2 shadow-sm ${getScoreColor(selectedSubmission.score)} bg-white`}
            >
              <span
                class="text-[10px] uppercase font-bold opacity-70 leading-none mb-1"
                >Điểm số</span
              >
              <span class="text-xl font-black leading-none"
                >{selectedSubmission.score}<span class="text-xs opacity-60"
                  >/10</span
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
              class="absolute -inset-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-2xl blur opacity-10 group-hover:opacity-20 transition duration-500"
            ></div>
            <div
              class="relative space-y-3 bg-white border border-indigo-100 rounded-xl p-5 shadow-sm"
            >
              <div class="flex items-center justify-between">
                <h4
                  class="text-[11px] font-black text-indigo-600 uppercase tracking-[0.2em] flex items-center gap-2"
                >
                  <div class="p-1 bg-indigo-100 rounded-lg">
                    <Sparkles class="w-3.5 h-3.5" />
                  </div>
                  Tóm tắt phân tích của AI
                </h4>
              </div>

              <div class="text-sm leading-7 text-slate-700">
                {#if selectedSubmission.aiAssessment}
                  <p>
                    {selectedSubmission.aiAssessment?.summary}
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
              <div class="h-[1px] w-full bg-slate-100"></div>
            </div>

            <div class="space-y-6">
              {#each getMappedAnswers(selectedSubmission.answer) as item, i}
                <div
                  class="relative pl-6 border-l-2 border-slate-100 hover:border-indigo-200 transition-colors"
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
                      class="text-sm text-primary-900 leading-relaxed bg-background p-4 rounded-xl border border-slate-100/50 whitespace-pre-wrap"
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
        class="absolute bottom-0 left-0 right-0 p-5 bg-background backdrop-blur-md border-t flex justify-end gap-3 z-30 shadow-[0_-10px_20px_rgba(0,0,0,0,0.02)]"
      >
        <Button
          variant="ghost"
          class="font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
          onclick={() => (isSheetOpen = false)}
        >
          Đóng
        </Button>
        <Button
          class="bg-slate-900  hover:bg-slate-800 text-white px-6 rounded-xl transition-all active:scale-95"
          onclick={() =>
            goto(`${campaign.id}/submissions/${selectedSubmission.id}`)}
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
