<script lang="ts">
  import * as Sidebar from "$lib/components/ui/sidebar/index.js";
  import { page } from "$app/state";
  import { createMutation } from "@tanstack/svelte-query";
  import { api } from "$lib/utils/api";
  import { goto } from "$app/navigation";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu/index.js";
  import {
    FileText,
    Inbox,
    LayoutDashboard,
    Settings,
    Sparkles,
    UserIcon,
    LogOut,
    ChevronsUpDown,
    SunIcon,
    MoonIcon,
  } from "lucide-svelte";
  import SidebarMenuButton from "@src/lib/components/ui/sidebar/sidebar-menu-button.svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { toggleMode } from "mode-watcher";
  const logoutMutation = createMutation(() => ({
    mutationFn: async () => api.post("/auth/logout", {}),
    onSuccess: () => {
      goto("/login");
    },
  }));

  const items = [
    {
      title: "Tổng quan",
      url: "/dashboard",
      icon: LayoutDashboard,
    },
    {
      title: "Chiến dịch",
      url: "/campaigns",
      icon: FileText,
    },
    {
      title: "Kết quả (Submissions)",
      url: "/submissions",
      icon: Inbox,
    },
    {
      title: "Cài đặt",
      url: "/settings",
      icon: Settings,
    },
  ];
</script>

<Sidebar.Root collapsible="icon">
  <Sidebar.Header>
    <Sidebar.Menu>
      <Sidebar.MenuItem>
        <Sidebar.MenuButton
          size="lg"
          class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
        >
          <div
            class="flex aspect-square size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground"
          >
            <Sparkles class="size-4" />
          </div>
          <div class="grid flex-1 text-left text-sm leading-tight">
            <span class="truncate font-semibold">CoreSense</span>
            <span class="truncate text-xs">AI Evaluation</span>
          </div>
          <Button onclick={toggleMode} variant="outline" size="icon">
            <SunIcon
              class="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 !transition-all dark:scale-0 dark:-rotate-90"
            />
            <MoonIcon
              class="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 !transition-all dark:scale-100 dark:rotate-0"
            />
          </Button>
        </Sidebar.MenuButton>
      </Sidebar.MenuItem>
    </Sidebar.Menu>
  </Sidebar.Header>

  <Sidebar.Content>
    <Sidebar.Group>
      <Sidebar.GroupLabel>Platform</Sidebar.GroupLabel>
      <Sidebar.GroupContent>
        <Sidebar.Menu>
          {#each items as item (item.title)}
            <Sidebar.MenuItem>
              <SidebarMenuButton isActive={page.url.pathname === item.url}>
                {#snippet child({ props })}
                  <a href={item.url} {...props}>
                    <item.icon />
                    <span>{item.title}</span>
                  </a>
                {/snippet}
              </SidebarMenuButton>
            </Sidebar.MenuItem>
          {/each}
        </Sidebar.Menu>
      </Sidebar.GroupContent>
    </Sidebar.Group>
  </Sidebar.Content>

  <Sidebar.Footer>
    {#if page.data.user}
      <Sidebar.Menu>
        <Sidebar.MenuItem>
          <DropdownMenu.Root>
            <DropdownMenu.Trigger>
              {#snippet child({ props })}
                <Sidebar.MenuButton
                  size="lg"
                  class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                  {...props}
                >
                  <div
                    class="flex aspect-square size-8 items-center justify-center rounded-lg bg-muted text-sidebar-foreground"
                  >
                    <UserIcon class="size-4" />
                  </div>
                  <div class="grid flex-1 text-left text-sm leading-tight">
                    <span class="truncate font-semibold"
                      >{page.data?.user.fullname}</span
                    >
                    <span class="truncate text-xs">{page.data.user.email}</span>
                  </div>
                  <ChevronsUpDown class="ml-auto size-4" />
                </Sidebar.MenuButton>
              {/snippet}
            </DropdownMenu.Trigger>
            <DropdownMenu.Content
              class="w-[--bits-dropdown-menu-anchor-width] min-w-56 rounded-lg"
              side="top"
              align="end"
              sideOffset={4}
            >
              <DropdownMenu.Label class="p-0 font-normal">
                <div
                  class="flex items-center gap-2 px-1 py-1.5 text-left text-sm"
                >
                  <div
                    class="flex h-8 w-8 items-center justify-center rounded-lg bg-muted"
                  >
                    <UserIcon class="size-4" />
                  </div>
                  <div class="grid flex-1 text-left text-sm leading-tight">
                    <span class="truncate font-semibold"
                      >{page.data.user.fullname}</span
                    >
                    <span class="truncate text-xs">{page.data.user.email}</span>
                  </div>
                </div>
              </DropdownMenu.Label>
              <DropdownMenu.Separator />
              <DropdownMenu.Group>
                <DropdownMenu.Item>
                  <Sparkles class="mr-2 size-4" />
                  Nâng cấp Pro
                </DropdownMenu.Item>
              </DropdownMenu.Group>
              <DropdownMenu.Separator />
              <DropdownMenu.Item onclick={() => logoutMutation.mutate()}>
                <LogOut class="mr-2 size-4" />
                Đăng xuất
              </DropdownMenu.Item>
            </DropdownMenu.Content>
          </DropdownMenu.Root>
        </Sidebar.MenuItem>
      </Sidebar.Menu>
    {/if}
  </Sidebar.Footer>
</Sidebar.Root>
