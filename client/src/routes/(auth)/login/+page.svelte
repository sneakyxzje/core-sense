<script lang="ts">
  import { goto } from "$app/navigation";
  import { Button } from "$lib/components/ui/button";
  import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
    CardFooter,
  } from "$lib/components/ui/card";
  import { Input } from "$lib/components/ui/input";
  import { Label } from "$lib/components/ui/label";
  import type { LoginPayload } from "@src/lib/types/auth";
  import { api } from "@src/lib/utils/api";
  let payload = $state<LoginPayload>({
    email: "",
    password: "",
  });
  const handleLogin = async (e: Event) => {
    e.preventDefault();
    try {
      const ok = await api.post("/auth/login", payload);
      if (ok) {
        await goto("/campaigns", { invalidateAll: true });
      }
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      alert("Đăng nhập thất bại, kiểm tra lại tài khoản!");
    }
  };
</script>

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
          bind:value={payload.email}
        />
      </div>

      <div class="grid gap-2">
        <div class="flex items-center justify-between">
          <Label for="password">Password</Label>
          <a
            href="/forgot-password"
            class="text-sm underline text-muted-foreground hover:text-primary"
          >
            Forgot password?
          </a>
        </div>
        <Input
          id="password"
          type="password"
          required
          bind:value={payload.password}
        />
      </div>

      <Button type="submit" class="w-full cursor-pointer mt-2">Login</Button>
    </form>
  </CardContent>

  <CardFooter class="flex justify-center text-sm text-muted-foreground">
    Don't have an account?
    <a href="/register" class="ml-1 text-primary hover:underline font-medium"
      >Register</a
    >
  </CardFooter>
</Card>
