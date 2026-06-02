"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import {
  approveAgent,
  fetchAdminAgentAttendance,
  fetchAdminAgents,
  fetchAdminCustomers,
  updateAgentGrade,
  updateSuspension,
  updateWithdrawal
} from "@/lib/api/admin";

export default function AdminAgentsPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

  const agentsQuery = useQuery({
    queryKey: ["admin-agents", submittedKeyword],
    queryFn: () => fetchAdminAgents(submittedKeyword)
  });

  useEffect(() => {
    void queryClient.prefetchQuery({
      queryKey: ["admin-customers", ""],
      queryFn: () => fetchAdminCustomers("")
    });
    void queryClient.prefetchQuery({
      queryKey: ["admin-attendance", ""],
      queryFn: () => fetchAdminAgentAttendance("")
    });
  }, [queryClient]);

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
    <PageShell title="관리자 - 상담사 회원 관리" description="상담사 승인, 등급 조회, 검색, 정지, 탈퇴 상태를 관리합니다.">
      <div className="mb-4 flex gap-2">
        <input className="rounded border px-3 py-2 text-sm" onChange={(e) => setKeyword(e.target.value)} placeholder="이름/이메일/사원번호 검색" value={keyword} />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">검색</button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/customers" as Route}>고객 관리</Link>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/attendance" as Route}>근태 관리</Link>
      </div>
      {agentsQuery.isError ? <AlertBanner message="상담사 목록을 불러오지 못했습니다." variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">사원번호</th>
            <th className="px-3 py-2 text-left">이름</th>
            <th className="px-3 py-2 text-left">이메일</th>
            <th className="px-3 py-2 text-left">승인상태</th>
            <th className="px-3 py-2 text-left">직급</th>
            <th className="px-3 py-2 text-left">정지</th>
            <th className="px-3 py-2 text-left">탈퇴</th>
            <th className="px-3 py-2 text-left">작업</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {agentsQuery.data?.map((item) => (
            <tr key={item.agentId}>
              <td className="px-3 py-2">{item.employeeNo}</td>
              <td className="px-3 py-2">{item.name}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{item.approvalStatus}</td>
              <td className="px-3 py-2">{item.grade}</td>
              <td className="px-3 py-2">{item.suspendedYn}</td>
              <td className="px-3 py-2">{item.withdrawnYn}</td>
              <td className="px-3 py-2">
                <div className="flex gap-2">
                  {item.approvalStatus === "PENDING" ? (
                    <button className="rounded border px-2 py-1" onClick={() => approveMutation.mutate(item.agentId)} type="button">승인</button>
                  ) : null}
                  <button className="rounded border px-2 py-1" onClick={() => suspendMutation.mutate({ userId: item.userId, value: item.suspendedYn === "Y" ? "N" : "Y" })} type="button">정지토글</button>
                  <button className="rounded border px-2 py-1" onClick={() => withdrawMutation.mutate({ userId: item.userId, value: item.withdrawnYn === "Y" ? "N" : "Y" })} type="button">탈퇴토글</button>
                  <select
                    className="rounded border px-2 py-1"
                    defaultValue={item.grade}
                    onChange={(event) => gradeMutation.mutate({ agentId: item.agentId, grade: event.target.value as "ADMIN" | "COUNSELOR" | "TEAM_LEAD" })}
                  >
                    <option value="ADMIN">관리자상담자</option>
                    <option value="COUNSELOR">일반상담자</option>
                    <option value="TEAM_LEAD">팀장상담자</option>
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
