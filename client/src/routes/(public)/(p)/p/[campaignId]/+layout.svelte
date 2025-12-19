<script lang="ts">
  let { children } = $props();
</script>

<main class="container mx-auto py-8">
  {#if children}
    {@render children()}
  {/if}
</main>
