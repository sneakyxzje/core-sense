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

  let isLoading = $state(false);
  let showPassword = $state(false);

  const handleLogin = async (e: Event) => {
    e.preventDefault();
    isLoading = true;

    try {
      const ok = await api.post("/auth/login", payload);
      if (ok) {
        await goto("/dashboard", { invalidateAll: true });
      }
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      alert("Đăng nhập thất bại, kiểm tra lại tài khoản!");
    } finally {
      isLoading = false;
    }
  };
</script>

<div class="w-full flex items-center justify-center p-4">
  <Card
    class="w-full max-w-md shadow-2xl border border-[var(--color-base-border-1)] bg-[var(--color-base-1)] animate-in fade-in-0 zoom-in-95 duration-500"
  >
    <div
      class="absolute top-0 left-0 right-0 h-1 bg-[var(--color-primary-1)]"
    ></div>

    <CardHeader class="text-center space-y-3 pt-8">
      <CardTitle class="text-3xl font-bold text-[var(--color-base-fg-1)]">
        Welcome Back
      </CardTitle>
      <CardDescription class="text-base text-[var(--color-base-fg-3)]">
        Reconnect to InsightPulse to continue monitoring your data.
      </CardDescription>
    </CardHeader>

    <CardContent class="px-6 pb-6">
      <form onsubmit={handleLogin} class="grid gap-5">
        <!-- Email Input -->
        <div class="grid gap-2">
          <Label
            for="email"
            class="text-sm font-medium text-[var(--color-base-fg-2)]"
          >
            Email
          </Label>
          <div class="relative">
            <div
              class="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--color-base-fg-4)]"
            >
              <svg
                class="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                />
              </svg>
            </div>
            <Input
              id="email"
              type="email"
              placeholder="email@example.com"
              required
              bind:value={payload.email}
              class="pl-11 h-12 border-[var(--color-base-border-1)] bg-[var(--color-base-2)] text-[var(--color-base-fg-1)] focus:border-[var(--color-primary-1)] focus:ring-2 focus:ring-[var(--color-primary-1)]/20 transition-all duration-200"
            />
          </div>
        </div>

        <!-- Password Input -->
        <div class="grid gap-2">
          <div class="flex items-center justify-between">
            <Label
              for="password"
              class="text-sm font-medium text-[var(--color-base-fg-2)]"
            >
              Password
            </Label>
            <a
              href="/forgot-password"
              class="text-sm text-[var(--color-primary-1)] hover:text-[var(--color-primary-hover)] font-medium transition-colors"
            >
              Forgot password?
            </a>
          </div>
          <div class="relative">
            <div
              class="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--color-base-fg-4)]"
            >
              <svg
                class="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </div>
            <Input
              id="password"
              type={showPassword ? "text" : "password"}
              required
              bind:value={payload.password}
              class="pl-11 pr-11 h-12 border-[var(--color-base-border-1)] bg-[var(--color-base-2)] text-[var(--color-base-fg-1)] focus:border-[var(--color-primary-1)] focus:ring-2 focus:ring-[var(--color-primary-1)]/20 transition-all duration-200"
            />
            <button
              type="button"
              onclick={() => (showPassword = !showPassword)}
              class="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--color-base-fg-4)] hover:text-[var(--color-base-fg-2)] transition-colors"
            >
              {#if showPassword}
                <svg
                  class="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
                  />
                </svg>
              {:else}
                <svg
                  class="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                  />
                </svg>
              {/if}
            </button>
          </div>
        </div>

        <!-- Login Button -->
        <Button
          type="submit"
          class="w-full h-12 mt-2 bg-[var(--color-primary-1)] hover:bg-[var(--color-primary-hover)] active:bg-[var(--color-primary-active)] text-[var(--color-primary-fg-1)] font-semibold shadow-lg transition-all duration-200 hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed"
          disabled={isLoading}
        >
          {#if isLoading}
            <svg
              class="animate-spin -ml-1 mr-3 h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            Signing in...
          {:else}
            Sign In
          {/if}
        </Button>

        <div class="relative my-2">
          <div class="absolute inset-0 flex items-center">
            <div
              class="w-full border-t border-[var(--color-base-border-2)]"
            ></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span
              class="px-4 bg-[var(--color-base-1)] text-[var(--color-base-fg-4)]"
              >Or continue with</span
            >
          </div>
        </div>

        <Button
          type="button"
          variant="outline"
          class="w-full h-12 flex items-center justify-center gap-3 border-[var(--color-base-border-1)] bg-[var(--color-base-2)] hover:bg-[var(--color-base-hover)] text-[var(--color-base-fg-1)] transition-all duration-200 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
          onclick={() => {
            window.location.href =
              "http://localhost:8080/oauth2/authorization/google";
          }}
          disabled={isLoading}
        >
          <img
            src="https://developers.google.com/identity/images/g-logo.png"
            alt="Google"
            class="w-5 h-5"
          />
          <span class="font-medium">Continue with Google</span>
        </Button>
      </form>
    </CardContent>

    <CardFooter
      class="flex justify-center text-sm text-[var(--color-base-fg-3)] pb-8"
    >
      Don't have an account?
      <a
        href="/register"
        class="ml-1 text-[var(--color-primary-1)] hover:text-[var(--color-primary-hover)] font-semibold hover:underline transition-colors"
      >
        Create account
      </a>
    </CardFooter>
  </Card>
</div>

<style>
  @keyframes fade-in-0 {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes zoom-in-95 {
    from {
      transform: scale(0.95);
    }
    to {
      transform: scale(1);
    }
  }

  .animate-in {
    animation:
      fade-in-0 0.5s ease-out,
      zoom-in-95 0.5s ease-out;
  }
</style>
