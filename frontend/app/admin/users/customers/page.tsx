"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminCustomers, updateSuspension, updateWithdrawal } from "@/lib/api/admin";
import { msg } from "@/lib/messages";

export default function AdminCustomersPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

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
            <tr key={item.userId}>
              <td className="px-3 py-2">{item.name}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{item.phone || "-"}</td>
              <td className="px-3 py-2">{msg.label("yn", item.suspendedYn)}</td>
              <td className="px-3 py-2">{msg.label("yn", item.withdrawnYn)}</td>
              <td className="px-3 py-2">
                <div className="flex gap-2">
                  <button
                    className="rounded border px-2 py-1"
                    onClick={() => suspendMutation.mutate({ userId: item.userId, value: item.suspendedYn === "Y" ? "N" : "Y" })}
                    type="button"
                  >
                    {msg.ui("admin.toggleSuspend")}
                  </button>
                  <button
                    className="rounded border px-2 py-1"
                    onClick={() => withdrawMutation.mutate({ userId: item.userId, value: item.withdrawnYn === "Y" ? "N" : "Y" })}
                    type="button"
                  >
                    {msg.ui("admin.toggleWithdraw")}
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </PageShell>
  );
}
