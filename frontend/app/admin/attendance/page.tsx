"use client";

import { useQuery } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminAgentAttendance } from "@/lib/api/admin";
import { msg } from "@/lib/messages";

export default function AdminAttendancePage() {
  const router = useRouter();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

  const attendanceQuery = useQuery({
    queryKey: ["admin-attendance", submittedKeyword],
    queryFn: () => fetchAdminAgentAttendance(submittedKeyword)
  });

  return (
    <PageShell title={msg.ui("admin.attendanceTitle")} description={msg.ui("admin.attendanceDescription")}>
      <div className="mb-4 flex gap-2">
        <input
          className="rounded border px-3 py-2 text-sm"
          onChange={(e) => setKeyword(e.target.value)}
          placeholder={msg.ui("admin.searchAttendance")}
          value={keyword}
        />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">
          {msg.ui("common.search")}
        </button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/agents" as Route}>
          {msg.ui("admin.manageAgents")}
        </Link>
        <button className="rounded border px-3 py-2 text-sm" onClick={() => router.back()} type="button">
          {msg.ui("common.previousPage")}
        </button>
      </div>
      {attendanceQuery.isError ? <AlertBanner message={msg.ui("admin.loadAttendanceFailed")} variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">{msg.ui("admin.workDate")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.agentName")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("common.email")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.grade")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.loginMark")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.loginCount")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.breakMinutes")}</th>
            <th className="px-3 py-2 text-left">{msg.ui("admin.workMinutes")}</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {attendanceQuery.data?.map((item) => (
            <tr key={`${item.agentId}-${item.workDate}`}>
              <td className="px-3 py-2">{item.workDate}</td>
              <td className="px-3 py-2">{item.agentName}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{msg.label("grade", item.grade)}</td>
              <td className="px-3 py-2">{item.loginMark}</td>
              <td className="px-3 py-2">{item.loginCount}</td>
              <td className="px-3 py-2">{item.breakMinutes}</td>
              <td className="px-3 py-2">{item.workMinutes}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </PageShell>
  );
}
