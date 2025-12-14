<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
  import { QueryClient, QueryClientProvider } from '@tanstack/svelte-query';
  import { browser } from '$app/environment';
  import { SvelteQueryDevtools } from '@tanstack/svelte-query-devtools'; 
	let { children } = $props();
	const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        enabled: browser, 
      },
    },
  });
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<QueryClientProvider client={queryClient}>
	{@render children()}
  <SvelteQueryDevtools buttonPosition="bottom-right" />
</QueryClientProvider>
