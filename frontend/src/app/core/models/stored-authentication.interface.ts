import { LoginResponse } from "./login-response.interface";

export interface StoredAuthentication extends LoginResponse {
  email: string;
  expiresAt: number;
}
