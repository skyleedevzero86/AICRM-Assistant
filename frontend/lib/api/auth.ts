import { apiRequest } from "./client";
import type {
  LoginRequest,
  LoginResponse,
  MeResponse,
  SignUpRequest,
  SignUpResponse
} from "./types";

export function login(request: LoginRequest): Promise<LoginResponse> {
  return apiRequest<LoginResponse>("/api/auth/login", {
    method: "POST",
    body: request
  });
}

export function signupCustomer(request: SignUpRequest): Promise<SignUpResponse> {
  return apiRequest<SignUpResponse>("/api/auth/signup/customer", {
    method: "POST",
    body: request
  });
}

export function signupAgent(request: SignUpRequest): Promise<SignUpResponse> {
  return apiRequest<SignUpResponse>("/api/auth/signup/agent", {
    method: "POST",
    body: request
  });
}

export function fetchMe(): Promise<MeResponse> {
  return apiRequest<MeResponse>("/api/auth/me");
}

export function withdrawMe(): Promise<void> {
  return apiRequest<void>("/api/auth/withdraw", {
    method: "POST"
  });
}
