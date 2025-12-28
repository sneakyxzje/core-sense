<script lang="ts">
  import * as Card from "$lib/components/ui/card";

  let { items } = $props<{
    items: { id: string; title: string; value: string | number }[];
  }>();
</script>

<div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
  {#each items as item (item.id)}
    <Card.Root
      class="border-base-border-1 bg-base-2/50 shadow-sm hover:border-base-border-hover transition-colors"
    >
      <Card.Header
        class="flex flex-row items-center justify-between space-y-0 pb-2"
      >
        <Card.Title class="text-sm font-medium text-muted-foreground"
          >{item.title}</Card.Title
        >
      </Card.Header>
      <Card.Content>
        <div class="text-2xl font-bold text-foreground">{item.value}</div>
      </Card.Content>
    </Card.Root>
  {/each}
</div>
