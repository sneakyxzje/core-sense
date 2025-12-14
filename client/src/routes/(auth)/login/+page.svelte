<script lang="ts">
  import { goto } from "$app/navigation";
    import { Button } from "$lib/components/ui/button"; 
    import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "$lib/components/ui/card";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
  import { useUser } from "@src/lib/queries/auth";
  import type { LoginPayload } from "@src/lib/types/auth";
  import { api } from "@src/lib/utils/api";
  import { createMutation, useQueryClient} from "@tanstack/svelte-query";
    let email = $state('');
    let password = $state('');
    const queryClient = useQueryClient();
     const user = useUser();
    const mutation = createMutation(() => ({
        mutationFn: async (payload: LoginPayload) => {
            return api.post('/auth/login', payload);
        },
        onSuccess: (data: any) => {
            goto('/');
            queryClient.invalidateQueries({queryKey: ['me']})
        },
        onError: (error: any) => {
            console.error(error.response?.data?.message || 'Login failed. Please try again.');
        },
    }))
    
    const handleLogin = (e: Event) => {
        e.preventDefault();
        mutation.mutate({ email, password });
    }
</script>

{#if user.data}
<h1>Error</h1>
{:else}
<Card class="w-[380px] shadow-2xl">
    
    <CardHeader class="text-center">
        <CardTitle class="text-2xl font-bold text-primary">Welcome Back</CardTitle>
        <CardDescription class="text-muted-foreground">
            Reconnect to InsightPulse to continue monitoring your data.
        </CardDescription>
    </CardHeader>
    
    <CardContent>
        <form onsubmit={handleLogin} class="grid gap-4">
            <div class="grid gap-2">
                <Label for="email">Email</Label>
                <Input 
                    id="email" 
                    type="email" 
                    placeholder="email@type.com" 
                    required 
                    bind:value={email}
                />
            </div>
            
            <div class="grid gap-2">
                <div class="flex items-center justify-between">
                    <Label for="password">Password</Label>
                    <a href="/forgot-password" class="text-sm underline text-muted-foreground hover:text-primary">
                        Forgot password?
                    </a>
                </div>
                <Input 
                    id="password" 
                    type="password" 
                    required 
                    bind:value={password}
                />
            </div>
            
            <Button type="submit" class="w-full mt-2">
                Login
            </Button>
        </form>
    </CardContent>

    <CardFooter class="flex justify-center text-sm text-muted-foreground">
        Don't have an account? 
        <a href="/register" class="ml-1 text-primary hover:underline font-medium">Register</a>
    </CardFooter>
</Card>
{/if}