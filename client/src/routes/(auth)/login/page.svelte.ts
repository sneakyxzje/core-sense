import { goto } from "$app/navigation";
import { api } from "$lib/utils/api";
import type { LoginPayload } from "@src/lib/types/auth";

export class LoginState {
  form = $state<LoginPayload>({ email: "", password: "" });
  isLoading = $state(false);
  showPassword = $state(false);
  error = $state<string | null>(null);

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  submit = async (e: Event) => {
    e.preventDefault();
    this.isLoading = true;
    this.error = null;
    const payload = { ...this.form };
    console.log(payload);
    try {
      await api.post("/auth/login", payload);
      await goto("/dashboard", { invalidateAll: true });
    } catch (err: any) {
      this.error = err.message || "Đăng nhập thất bại";
    } finally {
      this.isLoading = false;
    }
  };
}
