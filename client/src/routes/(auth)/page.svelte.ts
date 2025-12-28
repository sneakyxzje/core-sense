import { goto } from "$app/navigation";
import { api } from "@src/lib/utils/api";

export class AuthState {
  logout = async () => {
    const ok = await api.post("/auth/logout", {});
    if (ok) {
      goto("/login");
    }
  };
}
