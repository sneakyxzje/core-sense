<script lang="ts">
    import Navbar from '$lib/components/Navbar.svelte'; 
    let { children } = $props();
</script>

<Navbar /> 

<main class="container mx-auto py-8">
    {@render children()}
</main>