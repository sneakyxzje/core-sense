<script lang="ts">
  import { goto } from "$app/navigation";
  import * as Card from "$lib/components/ui/card";
  import Button from "@src/lib/components/ui/button/button.svelte";

  let { items } = $props<{
    items: { id: string; title: string; value: string | number }[];
  }>();
</script>

<div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
  {#each items as item (item.id)}
    <Card.Root
      class="border-base-border-1 bg-base-2/50 shadow-sm hover:border-base-border-hover transition-colors"
    >
      <Card.Header class="flex flex-row items-center justify-between ">
        <Card.Title class="text-sm font-medium text-base-fg-1"
          >{item.title}</Card.Title
        >
      </Card.Header>
      <Card.Content>
        <div class="text-2xl font-bold text-base-fg-1">{item.value}</div>
        {#if item.subValue}
          <div
            class="text-sm mt-1"
            class:text-green-500={item.subValue >= 0}
            class:text-red-500={item.subValue < 0}
          >
            {item.subValue >= 0 ? "+" : ""}
            {item.subValue}% so với tháng trước
          </div>
        {/if}
        {#if item.action}
          <div class="mt-2">
            <Button
              onclick={() => goto("/campaigns")}
              class="text-sm h-6  font-medium bg-primary-1 text-base-1 hover:bg-primary-hover "
              >{item.action}
            </Button>
          </div>
        {/if}
      </Card.Content>
    </Card.Root>
  {/each}
</div>
