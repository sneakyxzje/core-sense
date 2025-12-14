<script lang="ts">
    import { goto } from "$app/navigation";
    import Button from "@src/lib/components/ui/button/button.svelte";
    import CardContent from "@src/lib/components/ui/card/card-content.svelte";
    import CardDescription from "@src/lib/components/ui/card/card-description.svelte";
    import CardFooter from "@src/lib/components/ui/card/card-footer.svelte";
    import CardHeader from "@src/lib/components/ui/card/card-header.svelte";
    import CardTitle from "@src/lib/components/ui/card/card-title.svelte";
    import Card from "@src/lib/components/ui/card/card.svelte";
    import Input from "@src/lib/components/ui/input/input.svelte";
    import Label from "@src/lib/components/ui/label/label.svelte";
    import type { RegisterPayload } from "@src/lib/types/auth";
    import { api } from "@src/lib/utils/api";
    import { createMutation } from "@tanstack/svelte-query";


    let fullname = $state("");
    let email = $state("")
    let password = $state("");
    const mutation = createMutation(() => ({
        mutationFn: async (payload: RegisterPayload) => api.post('/auth/register', payload),

        onSuccess: (data: any) => {
            goto("/login")
        },

        onError: (err: any) => {
            console.error(err.response?.data?.message || 'Register failed failed. Please try again.');
        }
    }))
    const {isPending} = mutation;
    const handleRegister = () => {
        mutation.mutate({fullname, email, password});
        console.log(fullname, email, password)
    }

</script>

<Card class="w-[400px] shadow-2xl">
    
    <CardHeader class="text-center">
        <CardTitle class="text-2xl font-bold text-primary">Join InsightPulse</CardTitle>
        <CardDescription class="text-muted-foreground">
            Create your account to start monitoring your data instantly.
        </CardDescription>
    </CardHeader>
    
    <CardContent>
        <form onsubmit={handleRegister} class="grid gap-4">
            
            <div class="grid gap-2">
                <Label for="fullName">Full Name</Label>
                <Input 
                    id="fullName" 
                    type="text" 
                    placeholder="Your Full Name" 
                    required 
                    bind:value={fullname}
                />
            </div>
            
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
                <Label for="password">Password</Label>
                <Input 
                    id="password" 
                    type="password" 
                    required 
                    bind:value={password}
                />
            </div>
            
            <Button
                type="submit" 
                class="w-full mt-2"
                disabled={isPending}
            >
                {#if isPending}
                    <svg class="animate-spin h-5 w-5 mr-3 text-white" viewBox="0 0 24 24"></svg>
                    Đang đăng ký...
                {:else}
                    Register
                {/if}
            </Button>
        </form>
    </CardContent>

    
    <CardFooter class="flex justify-center text-sm text-muted-foreground">
        Already have an account? 
        <a href="/login" class="ml-1 text-primary hover:underline font-medium">Login</a>
    </CardFooter>
</Card>