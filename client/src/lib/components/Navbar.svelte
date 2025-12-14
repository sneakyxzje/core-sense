<script lang="ts">
  import { goto } from "$app/navigation";
  import DropdownMenuContent from "@src/lib/components/ui/dropdown-menu/dropdown-menu-content.svelte";
  import DropdownMenuItem from "@src/lib/components/ui/dropdown-menu/dropdown-menu-item.svelte";
  import DropdownMenuLabel from "@src/lib/components/ui/dropdown-menu/dropdown-menu-label.svelte";
  import DropdownMenuSeparator from "@src/lib/components/ui/dropdown-menu/dropdown-menu-separator.svelte";
  import DropdownMenuTrigger from "@src/lib/components/ui/dropdown-menu/dropdown-menu-trigger.svelte";
  import DropdownMenu from "@src/lib/components/ui/dropdown-menu/dropdown-menu.svelte";
  import { useUser } from "@src/lib/queries/auth";
  import { api } from "@src/lib/utils/api";
  import { createMutation, useQueryClient } from "@tanstack/svelte-query";

    const user = useUser();
    const queryClient = useQueryClient();
    const mutation = createMutation(() => ({
        mutationFn: async () => api.post('/auth/logout', {}),
        onSuccess: () => {
            queryClient.setQueryData(['me'], null);
            goto('/login')
        },

        onError: (error: any) => {
            console.error(error);
        }
    }))

    const handleLogout = () => {
        mutation.mutate();
    }
</script>

<header class="border-b border-border bg-background/95 backdrop-blur-sm sticky top-0 z-50">
    <div class="container flex h-16 items-center max-w-7xl mx-auto justify-between px-4 sm:px-6 lg:px-8">
        
        <a href="/" class="text-2xl font-extrabold text-primary tracking-tight transition-colors hover:text-primary/80">
            InsightPulse
        </a>
        
        {#if !user.data}
        <div class="flex items-center gap-4">
            <a 
                href="/login" 
                class="
                    text-sm font-semibold 
                    bg-primary text-primary-foreground 
                    px-4 py-2 
                    shadow-md 
                    transition-all duration-200 
                    hover:bg-primary/90 hover:shadow-lg 
                    focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2
                "
            >
                Get started
            </a>
            </div>
        {:else}
        <DropdownMenu>
        <DropdownMenuTrigger class="cursor-pointer flex items-center gap-2 px-4 py-2 hover:bg-muted rounded-md transition-colors">
            Hello, {user.data?.fullname}
        </DropdownMenuTrigger>
        
        <DropdownMenuContent class="cursor-pointer">
            <DropdownMenuLabel>Tài khoản của tôi</DropdownMenuLabel>
            <DropdownMenuSeparator />
            
            <div class="px-2 py-1.5 text-sm text-muted-foreground">
                {user.data?.email}
            </div>
            
            <DropdownMenuSeparator />
            <DropdownMenuItem class="cursor-pointer">Hồ sơ</DropdownMenuItem>
            <DropdownMenuItem class="cursor-pointer">Cài đặt</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onclick={handleLogout} class="text-red-500 cursor-pointer">Đăng xuất</DropdownMenuItem>
        </DropdownMenuContent>
        </DropdownMenu>
        {/if}
    </div>
</header>