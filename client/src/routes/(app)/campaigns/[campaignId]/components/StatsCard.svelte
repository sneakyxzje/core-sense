<script lang="ts">
  let { campaign } = $props();
</script>

<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
  <div
    class="p-4 rounded-md border border-base-border-1 bg-base-3 shadow-sm flex items-center justify-between"
  >
    <div class="space-y-1">
      <span
        class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
        >Tổng phản hồi</span
      >
      <div class="text-2xl font-black">{campaign.totalSubmissions}</div>
    </div>
  </div>
</div>
