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
    LayoutPanelLeft,
    Trash,
    Trash2,
  } from "lucide-svelte";
  import { AuthState } from "@src/routes/(auth)/page.svelte";
  import { goto } from "$app/navigation";
  const auth = new AuthState();

  const items = [
    {
      label: "Tổng quan",
      items: [
        { title: "Dashboard", url: "/dashboard", icon: LayoutDashboard },
        { title: "Thông báo", url: "/notifications", icon: Bell },
      ],
    },
    {
      label: "Tuyển dụng",
      items: [
        { title: "Chiến dịch", url: "/campaigns", icon: FileText },
        { title: "Ứng viên", url: "/candidates", icon: Users },
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
                <Sidebar.MenuButton isActive={page.url.pathname === item.url}>
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
                <DropdownMenu.Item onclick={() => goto("/subscription")}>
                  <Sparkles class="mr-2 size-4" />
                  Nâng cấp Pro
                </DropdownMenu.Item>
              </DropdownMenu.Group>
              <DropdownMenu.Separator />
              <DropdownMenu.Item onclick={auth.logout}>
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
