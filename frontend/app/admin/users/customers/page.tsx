"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminAgentAttendance, fetchAdminAgents, fetchAdminCustomers, updateSuspension, updateWithdrawal } from "@/lib/api/admin";

export default function AdminCustomersPage() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

  const customersQuery = useQuery({
    queryKey: ["admin-customers", submittedKeyword],
    queryFn: () => fetchAdminCustomers(submittedKeyword)
  });

  useEffect(() => {
    void queryClient.prefetchQuery({
      queryKey: ["admin-agents", ""],
      queryFn: () => fetchAdminAgents("")
    });
    void queryClient.prefetchQuery({
      queryKey: ["admin-attendance", ""],
      queryFn: () => fetchAdminAgentAttendance("")
    });
  }, [queryClient]);

  const suspendMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateSuspension(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-customers"] })
  });

  const withdrawMutation = useMutation({
    mutationFn: ({ userId, value }: { userId: number; value: "Y" | "N" }) => updateWithdrawal(userId, value),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-customers"] })
  });

  return (
    <PageShell title="관리자 - 고객 회원 관리" description="고객 회원 조회, 검색, 정지, 탈퇴 상태를 관리합니다.">
      <div className="mb-4 flex gap-2">
        <input className="rounded border px-3 py-2 text-sm" onChange={(e) => setKeyword(e.target.value)} placeholder="이름/이메일 검색" value={keyword} />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">검색</button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/agents" as Route}>상담사 관리</Link>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/attendance" as Route}>근태 관리</Link>
      </div>
      {customersQuery.isError ? <AlertBanner message="고객 목록을 불러오지 못했습니다." variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">이름</th>
            <th className="px-3 py-2 text-left">이메일</th>
            <th className="px-3 py-2 text-left">연락처</th>
            <th className="px-3 py-2 text-left">정지</th>
            <th className="px-3 py-2 text-left">탈퇴</th>
            <th className="px-3 py-2 text-left">작업</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {customersQuery.data?.map((item) => (
            <tr key={item.userId}>
              <td className="px-3 py-2">{item.name}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{item.phone || "-"}</td>
              <td className="px-3 py-2">{item.suspendedYn}</td>
              <td className="px-3 py-2">{item.withdrawnYn}</td>
              <td className="px-3 py-2">
                <div className="flex gap-2">
                  <button className="rounded border px-2 py-1" onClick={() => suspendMutation.mutate({ userId: item.userId, value: item.suspendedYn === "Y" ? "N" : "Y" })} type="button">정지토글</button>
                  <button className="rounded border px-2 py-1" onClick={() => withdrawMutation.mutate({ userId: item.userId, value: item.withdrawnYn === "Y" ? "N" : "Y" })} type="button">탈퇴토글</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </PageShell>
  );
}
