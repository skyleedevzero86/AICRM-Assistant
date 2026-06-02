"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { AdminCustomerRow } from "@/components/admin-customer-row";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminCustomers, updateAdminCustomer, updateSuspension, updateWithdrawal } from "@/lib/api/admin";
import { msg, resolveApiError } from "@/lib/messages";

export default function AdminCustomersPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");
  const [notice, setNotice] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [savingUserId, setSavingUserId] = useState<number | null>(null);

  const customersQuery = useQuery({
    queryKey: ["admin-customers", submittedKeyword],
    queryFn: () => fetchAdminCustomers(submittedKeyword)
  });

  const suspendMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateSuspension(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-customers"] })
  });

  const withdrawMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateWithdrawal(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-customers"] })
  });

  async function saveCustomer(userId: number, payload: { name: string; email: string; phone: string }) {
    setSavingUserId(userId);
    setNotice(null);
    setErrorMessage(null);
    try {
      await updateAdminCustomer(userId, payload);
      setNotice(msg.ui("admin.profileSaved"));
      await queryClient.invalidateQueries({ queryKey: ["admin-customers"] });
    } catch (error) {
      setErrorMessage(resolveApiError(error, "admin.updateProfileFailed"));
    } finally {
      setSavingUserId(null);
    }
  }

  return (
    <PageShell title={msg.ui("admin.customersTitle")} description={msg.ui("admin.customersDescription")}>
      <div className="mb-4 flex gap-2">
        <input
          className="rounded border px-3 py-2 text-sm"
          onChange={(e) => setKeyword(e.target.value)}
          placeholder={msg.ui("admin.searchCustomers")}
          value={keyword}
        />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">
          {msg.ui("common.search")}
        </button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/agents" as Route}>
          {msg.ui("admin.manageAgents")}
        </Link>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/attendance" as Route}>
          {msg.ui("admin.manageAttendance")}
        </Link>
      </div>
      {notice ? <AlertBanner message={notice} variant="success" /> : null}
      {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
      {customersQuery.isError ? <AlertBanner message={msg.ui("admin.loadCustomersFailed")} variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">{msg.ui("common.name")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("common.email")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("common.phone")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.suspended")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.withdrawn")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.actions")}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {customersQuery.data?.map((item) => (
            <AdminCustomerRow
              item={item}
              key={item.userId}
              onSave={(payload) => void saveCustomer(item.userId, payload)}
              onToggleSuspend={() =>
                suspendMutation.mutate({ userId: item.userId, value: item.suspendedYn === "Y" ? "N" : "Y" })
              }
              onToggleWithdraw={() =>
                withdrawMutation.mutate({ userId: item.userId, value: item.withdrawnYn === "Y" ? "N" : "Y" })
              }
              pending={savingUserId === item.userId}
            />
          ))}
        </tbody>
      </table>
    </PageShell>
  );
}
