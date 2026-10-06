"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { FormField } from "@/components/form-field";
import { PageShell } from "@/components/page-shell";
import { fetchMe, updateMe, withdrawMe } from "@/lib/api/auth";
import type { UpdateMeRequest, UserRole } from "@/lib/api/types";
import { ApiError } from "@/lib/api/client";
import { clearAccessToken } from "@/lib/auth-storage";
import { msg } from "@/lib/messages";
import { useRequireAuth } from "@/lib/use-require-auth";

const inputClassName =
  "w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600";
const readOnlyClassName =
  "w-full rounded-md border border-zinc-300 bg-zinc-50 px-3 py-2 text-sm text-zinc-600";

function accountDescription(role: UserRole | undefined): string {
  if (role === "CUSTOMER") {
    return msg.ui("account.descriptionCustomer");
  }
  if (role === "AGENT") {
    return msg.ui("account.descriptionAgent");
  }
  if (role === "ADMIN") {
    return msg.ui("account.descriptionAdmin");
  }
  return msg.ui("account.description");
}

function buildUpdatePayload(
  role: UserRole,
  password: string,
  phone: string,
  adminName: string
): UpdateMeRequest {
  const payload: UpdateMeRequest = {};
  if (password) {
    payload.password = password;
  }
  if (role === "CUSTOMER") {
    payload.phone = phone;
  }
  if (role === "ADMIN" && adminName.trim()) {
    payload.name = adminName.trim();
  }
  return payload;
}

export default function AccountPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { status } = useRequireAuth();
  const [adminName, setAdminName] = useState("");
  const [password, setPassword] = useState("");
  const [phone, setPhone] = useState("");
  const [notice, setNotice] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const meQuery = useQuery({
    queryKey: ["me"],
    queryFn: fetchMe,
    enabled: status === "allowed"
  });

  useEffect(() => {
    if (!meQuery.data) {
      return;
    }
    setAdminName(meQuery.data.name);
    setPhone(meQuery.data.phone ?? "");
  }, [meQuery.data]);

  const saveMutation = useMutation({
    mutationFn: () => {
      if (!meQuery.data) {
        throw new Error("missing profile");
      }
      return updateMe(buildUpdatePayload(meQuery.data.role, password, phone, adminName));
    },
    onSuccess: (data) => {
      setPassword("");
      setNotice(msg.ui("account.saved"));
      setError(null);
      queryClient.setQueryData(["me"], data);
    },
    onError: (err) => {
      setNotice(null);
      setError(err instanceof ApiError ? err.message : msg.ui("account.saveFailed"));
    }
  });

  const withdrawMutation = useMutation({
    mutationFn: withdrawMe,
    onSuccess: () => {
      clearAccessToken();
      router.push("/auth/login" as Route);
    },
    onError: (err) => {
      setError(err instanceof ApiError ? err.message : msg.ui("account.saveFailed"));
    }
  });

  function logout() {
    clearAccessToken();
    router.push("/auth/login" as Route);
  }

  function handleWithdraw() {
    if (window.confirm(msg.ui("account.withdrawConfirm"))) {
      withdrawMutation.mutate();
    }
  }

  const role = meQuery.data?.role;
  const isCustomer = role === "CUSTOMER";
  const isAgent = role === "AGENT";
  const isAdmin = role === "ADMIN";
  const showForm = status === "allowed";

  return (
    <PageShell description={accountDescription(role)} title={msg.ui("account.title")}>
      <div className="space-y-4">
        {notice ? <AlertBanner message={notice} variant="success" /> : null}
        {error ? <AlertBanner message={error} variant="error" /> : null}
        {showForm && meQuery.isError ? (
          <AlertBanner
            message={
              meQuery.error instanceof ApiError ? meQuery.error.message : msg.ui("account.saveFailed")
            }
            variant="error"
          />
        ) : null}
        {!showForm || meQuery.isLoading ? (
          <p className="text-sm text-zinc-500">{msg.ui("common.loading")}</p>
        ) : null}

        {showForm && meQuery.data ? (
          <form
            className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6"
            onSubmit={(event) => {
              event.preventDefault();
              saveMutation.mutate();
            }}
          >
            <FormField htmlFor="account-email" label={msg.ui("common.email")}>
              <input
                className={readOnlyClassName}
                disabled
                id="account-email"
                readOnly
                type="email"
                value={meQuery.data.email}
              />
            </FormField>
            {isAgent ? (
              <FormField htmlFor="account-employee-no" label={msg.ui("admin.employeeNo")}>
                <input
                  className={readOnlyClassName}
                  disabled
                  id="account-employee-no"
                  readOnly
                  type="text"
                  value={meQuery.data.employeeNo}
                />
              </FormField>
            ) : null}
            <FormField htmlFor="account-name" label={msg.ui("common.name")} required={isAdmin}>
              {isAdmin ? (
                <input
                  className={inputClassName}
                  id="account-name"
                  onChange={(event) => setAdminName(event.target.value)}
                  required
                  type="text"
                  value={adminName}
                />
              ) : (
                <input
                  className={readOnlyClassName}
                  disabled
                  id="account-name"
                  readOnly
                  type="text"
                  value={meQuery.data.name}
                />
              )}
            </FormField>
            {isCustomer ? (
              <FormField htmlFor="account-phone" label={msg.ui("common.phone")}>
                <input
                  className={inputClassName}
                  id="account-phone"
                  onChange={(event) => setPhone(event.target.value)}
                  type="tel"
                  value={phone}
                />
              </FormField>
            ) : null}
            <FormField htmlFor="account-password" label={msg.ui("common.passwordMin")}>
              <input
                autoComplete="new-password"
                className={inputClassName}
                id="account-password"
                onChange={(event) => setPassword(event.target.value)}
                placeholder={msg.ui("account.passwordOptional")}
                type="password"
                value={password}
              />
            </FormField>
            <div className="flex flex-wrap gap-2">
              <button
                className="rounded-md bg-teal-700 px-4 py-2 text-sm font-medium text-white hover:bg-teal-800 disabled:opacity-50"
                disabled={saveMutation.isPending}
                type="submit"
              >
                {saveMutation.isPending ? msg.ui("common.saving") : msg.ui("common.save")}
              </button>
              <button
                className="rounded-md border border-zinc-300 bg-white px-4 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-50"
                onClick={logout}
                type="button"
              >
                {msg.ui("common.logout")}
              </button>
              <button
                className="rounded-md border border-red-300 bg-red-50 px-4 py-2 text-sm font-medium text-red-700 hover:bg-red-100 disabled:opacity-50"
                disabled={withdrawMutation.isPending}
                onClick={handleWithdraw}
                type="button"
              >
                {msg.ui("common.withdraw")}
              </button>
            </div>
          </form>
        ) : null}
      </div>
    </PageShell>
  );
}
