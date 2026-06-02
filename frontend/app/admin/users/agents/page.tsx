"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import {
  approveAgent,
  fetchAdminAgents,
  updateAgentGrade,
  updateSuspension,
  updateWithdrawal
} from "@/lib/api/admin";
import { msg } from "@/lib/messages";

export default function AdminAgentsPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

  const agentsQuery = useQuery({
    queryKey: ["admin-agents", submittedKeyword],
    queryFn: () => fetchAdminAgents(submittedKeyword)
  });

  const approveMutation = useMutation({
    mutationFn: (agentId: number) => approveAgent(agentId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-agents"] })
  });

  const suspendMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateSuspension(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-agents"] })
  });
  const gradeMutation = useMutation({
    mutationFn: ({ agentId, grade }: { agentId: number; grade: "ADMIN" | "COUNSELOR" | "TEAM_LEAD" }) =>
      updateAgentGrade(agentId, grade),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-agents"] })
  });

  const withdrawMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateWithdrawal(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-agents"] })
  });

  return (
    <PageShell title={msg.ui("admin.agentsTitle")} description={msg.ui("admin.agentsDescription")}>
      <div className="mb-4 flex gap-2">
        <input
          className="rounded border px-3 py-2 text-sm"
          onChange={(e) => setKeyword(e.target.value)}
          placeholder={msg.ui("admin.searchAgents")}
          value={keyword}
        />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">
          {msg.ui("common.search")}
        </button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/customers" as Route}>
          {msg.ui("admin.manageCustomers")}
        </Link>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/attendance" as Route}>
          {msg.ui("admin.manageAttendance")}
        </Link>
      </div>
      {agentsQuery.isError ? <AlertBanner message={msg.ui("admin.loadAgentsFailed")} variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">{msg.ui("admin.employeeNo")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("common.name")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("common.email")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.approvalStatus")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.grade")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.suspended")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.withdrawn")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.actions")}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {agentsQuery.data?.map((item) => (
            <tr key={item.agentId}>
              <td className="px-3 py-2">{item.employeeNo}</td>
              <td className="px-3 py-2">{item.name}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{msg.label("approvalStatus", item.approvalStatus)}</td>
              <td className="px-3 py-2">{msg.label("grade", item.grade)}</td>
              <td className="px-3 py-2">{msg.label("yn", item.suspendedYn)}</td>
              <td className="px-3 py-2">{msg.label("yn", item.withdrawnYn)}</td>
              <td className="px-3 py-2">
                <div className="flex gap-2">
                  {item.approvalStatus === "PENDING" ? (
                    <button className="rounded border px-2 py-1" onClick={() => approveMutation.mutate(item.agentId)} type="button">
                      {msg.ui("common.approve")}
                    </button>
                  ) : null}
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
                  <select
                    className="rounded border px-2 py-1"
                    defaultValue={item.grade}
                    onChange={(event) =>
                      gradeMutation.mutate({ agentId: item.agentId, grade: event.target.value as "ADMIN" | "COUNSELOR" | "TEAM_LEAD" })
                    }
                  >
                    <option value="ADMIN">{msg.label("grade", "ADMIN")}</option>
                    <option value="COUNSELOR">{msg.label("grade", "COUNSELOR")}</option>
                    <option value="TEAM_LEAD">{msg.label("grade", "TEAM_LEAD")}</option>
                  </select>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </PageShell>
  );
}
