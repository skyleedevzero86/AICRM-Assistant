"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { FormField } from "@/components/form-field";
import { PageShell } from "@/components/page-shell";
import { fetchMe, updateMe, withdrawMe } from "@/lib/api/auth";
import { ApiError } from "@/lib/api/client";
import { clearAccessToken } from "@/lib/auth-storage";
import { msg } from "@/lib/messages";
import { useRequireAuth } from "@/lib/use-require-auth";

const inputClassName =
  "w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600";

export default function AccountPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { status } = useRequireAuth();
  const [name, setName] = useState("");
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
    setName(meQuery.data.name);
    setPhone(meQuery.data.phone ?? "");
  }, [meQuery.data]);

  const saveMutation = useMutation({
    mutationFn: () =>
      updateMe({
        name,
        ...(password ? { password } : {}),
        ...(meQuery.data?.role === "CUSTOMER" ? { phone } : {})
      }),
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

  if (status !== "allowed") {
    return null;
  }

  const isCustomer = meQuery.data?.role === "CUSTOMER";

  return (
    <PageShell description={msg.ui("account.description")} title={msg.ui("account.title")}>
      <div className="space-y-4">
        {notice ? <AlertBanner message={notice} variant="success" /> : null}
        {error ? <AlertBanner message={error} variant="error" /> : null}
        {meQuery.isLoading ? <p className="text-sm text-zinc-500">{msg.ui("common.loading")}</p> : null}

        {meQuery.data ? (
          <form
            className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6"
            onSubmit={(event) => {
              event.preventDefault();
              saveMutation.mutate();
            }}
          >
            <FormField htmlFor="account-email" label={msg.ui("common.email")}>
              <input
                className={inputClassName}
                disabled
                id="account-email"
                readOnly
                type="email"
                value={meQuery.data.email}
              />
            </FormField>
            <FormField htmlFor="account-name" label={msg.ui("common.name")} required>
              <input
                className={inputClassName}
                id="account-name"
                onChange={(event) => setName(event.target.value)}
                required
                type="text"
                value={name}
              />
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
