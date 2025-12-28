<script lang="ts">
  import { page } from "$app/state";
  import { onMount, onDestroy } from "svelte";

  import { DashboardState } from "@src/routes/(app)/dashboard/page.svelte.js";
  import StatsCard from "@src/routes/(app)/dashboard/components/StatsCard.svelte";
  import SubmissionChart from "@src/routes/(app)/dashboard/components/SubmissionChart.svelte";
  import RecentFeed from "@src/routes/(app)/dashboard/components/RecentFeed.svelte";

  let { data } = $props();

  const dashboard = new DashboardState({
    stats: data.campaignStats,
    chart: data.submissionChart,
    summary: data.submissionSummary,
  });

  onMount(() => dashboard.connectSocket());
  onDestroy(() => dashboard.disconnectSocket());
</script>

<div class="flex-1 space-y-6 p-4 md:p-8 pt-6">
  <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
    <div>
      <h2 class="text-3xl font-bold tracking-tight text-foreground">
        Xin chào, {page.data?.user.fullname}
      </h2>
      <p class="text-muted-foreground mt-1">
        Tổng quan hiệu suất các chiến dịch của bạn.
      </p>
    </div>
  </div>

  <StatsCard items={dashboard.statsCards} />

  <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
    <SubmissionChart items={dashboard.chartItems} />
    <RecentFeed items={dashboard.summary} />
  </div>

  <div class="grid gap-4 md:grid-cols-1"></div>
</div>
