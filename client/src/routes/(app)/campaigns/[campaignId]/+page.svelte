<script lang="ts">
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import { Search, Download, Sparkles } from "lucide-svelte";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu";
  import { useDebounce } from "@src/lib/hooks/useDebounce.svelte.js";
  import { api } from "@src/lib/utils/api.js";
  import type {
    CampaignWithSubmission,
    Submission,
  } from "@src/lib/types/campaign.js";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import { toast } from "svelte-sonner";
  import ComparisonModal from "@src/routes/(app)/campaigns/[campaignId]/components/ComparisonModal.svelte";
  import SubmissionDetail from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionDetail.svelte";
  import StatsCard from "@src/routes/(app)/campaigns/[campaignId]/components/StatsCard.svelte";
  import SubmissionTable from "@src/routes/(app)/campaigns/[campaignId]/components/SubmissionTable.svelte";
  import Ellipsis from "@lucide/svelte/icons/ellipsis";
  import { fly } from "svelte/transition";
  import X from "@lucide/svelte/icons/x";
  let { data } = $props();
  let submissions = $state(data.submissions || []);
  let campaign = $state(data.campaign);
  let isSheetOpen = $state(false);
  const formUrl = `http://localhost:5173/p`;
  const sharedLink = `${formUrl}/${campaign.id}`;
  const copyLink = () => {
    navigator.clipboard.writeText(sharedLink);
    toast.success("Đã copy link thành công!");
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

  let selectedSubmission = $state<any>(null);
  function openDetail(sub: any) {
    selectedSubmission = sub;
    isSheetOpen = true;
  }

  let stateComparison = $state<Submission[]>([]);
  let isComparisonOpen = $state(false);
  const checkComparison = (checked: boolean, sub: Submission) => {
    if (checked) {
      if (stateComparison.length < 2) {
        stateComparison.push(sub);
      } else {
        toast.warning("Chỉ được chọn tối đa 2 ứng viên để so sánh");
      }
    } else {
      stateComparison = stateComparison.filter((item) => item.id !== sub.id);
    }
  };
  const handleComparison = () => {
    isComparisonOpen = true;
  };
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
        <Dialog.Root>
          <Dialog.Trigger>
            <Button variant="outline">Share</Button>
          </Dialog.Trigger>

          <Dialog.Content class="sm:max-w-md">
            <Dialog.Header>
              <Dialog.Title>Chia sẻ đường dẫn</Dialog.Title>
              <Dialog.Description>
                Bất kỳ ai có đường link này đều có thể truy cập biểu mẫu.
              </Dialog.Description>
            </Dialog.Header>

            <div class="flex items-center gap-2">
              <div class="grid flex-1 gap-1">
                <Label for="link">Link</Label>
                <Input
                  id="link"
                  readonly
                  value={sharedLink}
                  class="cursor-text"
                />
              </div>
              <Button
                type="button"
                variant="secondary"
                class="mt-6"
                onclick={copyLink}
              >
                Copy
              </Button>
            </div>
            <Dialog.Footer class="mt-6">
              <Dialog.Close>
                <Button variant="ghost">Đóng</Button>
              </Dialog.Close>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Root>
        <Button variant="outline" size="sm">
          <Download class="w-4 h-4 mr-2" /> Xuất Excel
        </Button>
      </div>
    </div>
    <StatsCard {campaign} {submissions} />
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
      <DropdownMenu.Root>
        <DropdownMenu.Trigger>
          {#snippet child({ props })}
            <Button {...props} variant="ghost" size="icon" class="h-9 w-9">
              <Ellipsis class="w-4 h-4" />
            </Button>
          {/snippet}
        </DropdownMenu.Trigger>
        <DropdownMenu.Content align="end">
          <DropdownMenu.Item>Làm mới dữ liệu</DropdownMenu.Item>
        </DropdownMenu.Content>
      </DropdownMenu.Root>
    </div>
  </div>
  {#if stateComparison.length >= 2}
    <div
      transition:fly={{ y: 20, duration: 300 }}
      class="fixed bottom-10 left-1/2 -translate-x-1/2 z-50 flex items-center gap-4 bg-slate-900 text-white px-6 py-3 shadow-2xl border border-white/10"
    >
      <div class="flex items-center gap-2 border-r border-white/20 pr-4">
        <div
          class="w-6 h-6 bg-primary flex rounded-full items-center justify-center text-[10px] font-bold"
        >
          {stateComparison.length}
        </div>
        <span class="text-sm font-medium">Ứng viên được chọn</span>
      </div>

      <button
        onclick={handleComparison}
        class="flex items-center gap-2 text-sm font-bold hover:text-primary transition-colors"
      >
        So sánh ngay
      </button>

      <button
        onclick={() => (stateComparison = [])}
        class="p-1 hover:bg-white/10 rounded-full transition-colors"
      >
        <X class="w-4 h-4" />
      </button>
    </div>
  {/if}
  <SubmissionTable
    {submissions}
    {campaign}
    {stateComparison}
    {checkComparison}
    {openDetail}
  />
</div>

<SubmissionDetail bind:isSheetOpen {selectedSubmission} {campaign} />
<ComparisonModal bind:isComparisonOpen {stateComparison} {campaign} />
