<script lang="ts">
  import * as Card from "$lib/components/ui/card/index.js";
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Plus, Calendar, FileText, ArrowRight } from "lucide-svelte";
  import { goto } from "$app/navigation";
  import { STATUS_COLORS, STATUS_LABELS } from "@src/lib/types/campaign";
  import Switch from "@src/lib/components/ui/switch/switch.svelte";
  import { api } from "@src/lib/utils/api";
  import { navigating } from "$app/state";
  import Skeleton from "@src/lib/components/ui/skeleton/skeleton.svelte";
  import * as Pagination from "$lib/components/ui/pagination/index.js";
  import { formatDate } from "@src/lib/utils/FormatDate.js";
  let { data } = $props();

  let campaigns = $state(data.campaigns);
  let totalElements = $derived(data.totalElements);
  $effect(() => {
    campaigns = data.campaigns;
    totalElements = data.totalElements;
  });

  let isToggleStatus = $state(false);
  let isLoading = $state(false);
  const handleCheckedChange = async (campaignId: string, newValue: boolean) => {
    const index = campaigns.findIndex((c) => c.id === campaignId);
    if (index === -1) return;

    const oldStatus = campaigns[index].status;
    const newStatus = newValue ? "ACTIVE" : "INACTIVE";

    campaigns[index].status = newStatus;
    isToggleStatus = newValue;
    isLoading = true;
    try {
      await api.post(`/campaigns/${campaignId}/toggle-status`, {
        enabled: newValue,
      });
    } catch (error) {
      isToggleStatus = !newValue;
      campaigns[index].status = oldStatus;
      console.error(error);
    } finally {
      isLoading = false;
    }
  };
  const isLoadingScreen = $derived(navigating.to !== null);
</script>

<div class="space-y-6">
  <div class="flex items-center justify-between">
    <div>
      <h2 class="text-3xl font-bold tracking-tight">Chiến dịch</h2>
      <p class="text-base-fg-4 mt-1">
        Quản lý và theo dõi các chiến dịch khảo sát của bạn.
      </p>
    </div>
    <Button
      class="cursor-pointer border bg-primary-1 text-primary-fg-1 hover:bg-primary-hover "
      onclick={() => goto("/campaigns/new")}
    >
      <Plus class="mr-2 h-4 w-4" />
      Tạo chiến dịch
    </Button>
  </div>

  {#if isLoadingScreen}
    <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      {#each Array(3) as _}
        <div class="flex flex-col space-y-3">
          <Skeleton class="h-[180px] w-full rounded-xl" />
          <div class="space-y-2">
            <Skeleton class="h-4 w-[250px]" />
            <Skeleton class="h-4 w-[200px]" />
          </div>
        </div>
      {/each}
    </div>
  {:else if campaigns.length === 0 && !isLoadingScreen}
    <div
      class="flex min-h-[400px] flex-col items-center justify-center rounded-lg border border-base-border-1 text-center animate-in fade-in-50"
    >
      <div
        class="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-muted"
      >
        <FileText class="h-10 w-10 text-muted-foreground" />
      </div>
      <h3 class="mt-4 text-lg font-semibold">Chưa có chiến dịch nào</h3>
      <p class="mb-4 mt-2 text-sm text-muted-foreground max-w-sm">
        Bạn chưa tạo chiến dịch nào. Hãy bắt đầu thu thập dữ liệu ngay hôm nay.
      </p>
      <Button
        class="cursor-pointer bg-primary-1 text-primary-fg-1 hover:bg-primary-hover"
        onclick={() => goto("/campaigns/new")}
      >
        <Plus class="mr-2 h-4 w-4" /> Tạo chiến dịch đầu tiên
      </Button>
    </div>
  {:else}
    <div class="relative">
      <div
        class="grid gap-2 md:grid-cols-2 lg:grid-cols-3 transition-all duration-300
        {isLoadingScreen
          ? 'opacity-40 grayscale-[0.3] pointer-events-none'
          : 'opacity-100'}"
      >
        {#each campaigns as item (item.id)}
          <Card.Root
            class="group transition-shadow border-base-border-3 hover:shadow-md"
          >
            <Card.Header>
              <div class="flex items-start justify-between">
                <Badge
                  class="{STATUS_COLORS[item.status] ||
                    'bg-primary'} text-white border-0"
                >
                  {STATUS_LABELS[item.status] || item.status}
                </Badge>
                <Switch
                  id="toggle-{item.id}"
                  checked={item.status === "ACTIVE"}
                  onCheckedChange={(e) => handleCheckedChange(item.id, e)}
                  disabled={isLoading}
                  class="cursor-pointer "
                />
              </div>
              <Card.Title class="text-xl pt-2 truncate" title={item.name}>
                {item.name}
              </Card.Title>
            </Card.Header>

            <Card.Content class="flex-1 pb-4">
              <p
                class="text-sm text-muted-foreground line-clamp-2 min-h-[2.5rem]"
              >
                {item.description || "Không có mô tả"}
              </p>
              <div class="mt-4 flex items-center text-xs text-muted-foreground">
                <Calendar class="mr-1 h-3 w-3" />
                <span>Tạo ngày: {formatDate(item.createdAt)}</span>
              </div>
            </Card.Content>

            <Card.Footer>
              <Button
                class="w-full bg-primary-1 text-primary-fg-1 hover:bg-primary-hover font-medium cursor-pointer"
                onclick={() => goto(`/campaigns/${item.id}`)}
              >
                Chi tiết <ArrowRight class="ml-2 h-4 w-4" />
              </Button>
            </Card.Footer>
          </Card.Root>
        {/each}
      </div>

      <div class="mt-8">
        {#if data.totalElements > data.size}
          <Pagination.Root
            count={totalElements}
            perPage={data.size}
            siblingCount={1}
            page={data.currentPage + 1}
            onPageChange={(p) =>
              goto(`/campaigns?page=${p - 1}&size=${data.size}`)}
          >
            {#snippet children({ pages, currentPage })}
              <Pagination.Content>
                <Pagination.Item><Pagination.Previous /></Pagination.Item>
                {#each pages as page (page.key)}
                  {#if page.type === "ellipsis"}
                    <Pagination.Item><Pagination.Ellipsis /></Pagination.Item>
                  {:else}
                    <Pagination.Item>
                      <Pagination.Link
                        {page}
                        isActive={currentPage === page.value}
                      >
                        {page.value}
                      </Pagination.Link>
                    </Pagination.Item>
                  {/if}
                {/each}
                <Pagination.Item><Pagination.Next /></Pagination.Item>
              </Pagination.Content>
            {/snippet}
          </Pagination.Root>
        {/if}
      </div>
    </div>
  {/if}
</div>
