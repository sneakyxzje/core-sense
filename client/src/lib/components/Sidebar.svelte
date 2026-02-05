<script lang="ts">
  import * as Sidebar from "$lib/components/ui/sidebar/index.js";
  import { page } from "$app/state";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu/index.js";
  import {
    FileText,
    LayoutDashboard,
    Settings,
    Sparkles,
    UserIcon,
    LogOut,
    ChevronsUpDown,
    Bell,
    Users,
    Trash,
    Trash2,
    Calendar,
    LayoutPanelTop,
  } from "lucide-svelte";
  import { AuthState } from "@src/routes/(auth)/page.svelte";
  import { goto } from "$app/navigation";
  const auth = new AuthState();

  const items = [
    {
      label: "Tổng quan",
      items: [{ title: "Dashboard", url: "/dashboard", icon: LayoutDashboard }],
    },
    {
      label: "Tuyển dụng",
      items: [
        { title: "Chiến dịch", url: "/campaigns", icon: FileText },
        { title: "Templates", url: "/templates", icon: LayoutPanelTop },
        { title: "Phỏng vấn", url: "/interviews", icon: Calendar },
      ],
    },
    {
      label: "Hệ thống",
      items: [
        { title: "Cài đặt", url: "/settings", icon: Settings },
        { title: "Lưu trữ", url: "/archive", icon: Trash2 },
      ],
    },
  ];
</script>

<Sidebar.Root collapsible="icon" class="bg-base-2">
  <Sidebar.Header>
    <Sidebar.Menu>
      <Sidebar.MenuItem>
        <Sidebar.MenuButton
          size="lg"
          class="data-[state=open]:bg-sidebar-accent bg-primary-1 data-[state=open]:text-sidebar-accent-foreground"
        >
          <div class="grid flex-1 justify-center leading-tight">
            <span class="truncate font-semibold text-primary-fg-1 text-xl"
              >CoreSense</span
            >
          </div>
        </Sidebar.MenuButton>
      </Sidebar.MenuItem>
    </Sidebar.Menu>
  </Sidebar.Header>

  <Sidebar.Content>
    {#each items as group}
      <Sidebar.Group>
        <Sidebar.GroupLabel>{group.label}</Sidebar.GroupLabel>
        <Sidebar.GroupContent>
          <Sidebar.Menu>
            {#each group.items as item (item.url)}
              <Sidebar.MenuItem>
                <Sidebar.MenuButton
                  class="hover:bg-base-4"
                  isActive={page.url.pathname === item.url}
                >
                  {#snippet child({ props })}
                    <a href={item.url} {...props}>
                      <item.icon />
                      <span>{item.title}</span>
                    </a>
                  {/snippet}
                </Sidebar.MenuButton>
              </Sidebar.MenuItem>
            {/each}
          </Sidebar.Menu>
        </Sidebar.GroupContent>
      </Sidebar.Group>
    {/each}
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
                    <div class="flex items-center gap-2">
                      <span class="truncate font-semibold"
                        >{page.data?.user.fullname}</span
                      >
                      <span
                        class="px-1.5 py-0.5 text-[10px] font-bold rounded uppercase
                        {page.data.user.subscriptionId === 'PRO'
                          ? 'bg-primary-1 text-primary-fg-1'
                          : 'bg-base-3 text-base-fg-3'}"
                      >
                        {page.data.user.subscriptionId || "FREE"}
                      </span>
                    </div>
                    <span class="truncate text-xs">{page.data.user.email}</span>
                  </div>
                  <ChevronsUpDown class="ml-auto size-4" />
                </Sidebar.MenuButton>
              {/snippet}
            </DropdownMenu.Trigger>
            <DropdownMenu.Content
              class="w-[--bits-dropdown-menu-anchor-width] border border-base-border-1 min-w-56 rounded-lg"
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
              <div class="px-2 py-3 space-y-2">
                <div class="flex justify-between text-xs font-medium">
                  <span class="text-base-fg-3">AI Usage</span>
                  <span class="text-base-fg-2"
                    >{page.data.user.usedCount}/{page.data.user.aiLimit ||
                      0}</span
                  >
                </div>
                <div
                  class="h-1.5 w-full bg-base-3 rounded-full overflow-hidden"
                >
                  <div
                    class="h-full transition-all duration-500 rounded-full
                    {(page.data.user.usedCount /
                      (page.data.user.aiLimit || 1)) *
                      100 >
                    90
                      ? 'bg-negative-1'
                      : (page.data.user.usedCount /
                            (page.data.user.aiLimit || 1)) *
                            100 >
                          70
                        ? 'bg-priority-medium'
                        : 'bg-positive-1'}"
                    style="width: {Math.min(
                      (page.data.user.usedCount /
                        (page.data.user.aiLimit || 1)) *
                        100,
                      100,
                    )}%"
                  ></div>
                </div>
              </div>
              <DropdownMenu.Separator />
              <DropdownMenu.Group>
                {#if page.data.user.subscriptionId !== "PRO"}
                  <DropdownMenu.Item
                    class="cursor-pointer hover:bg-base-3 text-primary-1 font-medium"
                    onclick={() => goto("/subscription")}
                  >
                    <Sparkles class="mr-2 size-4" />
                    Nâng cấp Pro
                  </DropdownMenu.Item>
                {:else}
                  <DropdownMenu.Item
                    class="cursor-pointer hover:bg-base-3"
                    onclick={() => goto("/subscription")}
                  >
                    <Settings class="mr-2 size-4" />
                    Quản lý gói cước
                  </DropdownMenu.Item>
                {/if}
              </DropdownMenu.Group>
              <DropdownMenu.Separator />
              <DropdownMenu.Item
                class="cursor-pointer hover:bg-base-3"
                onclick={auth.logout}
              >
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
