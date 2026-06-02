"use client";

import { useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminAgentAttendance, fetchAdminAgents } from "@/lib/api/admin";

export default function AdminAttendancePage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");

  const attendanceQuery = useQuery({
    queryKey: ["admin-attendance", submittedKeyword],
    queryFn: () => fetchAdminAgentAttendance(submittedKeyword)
  });

  useEffect(() => {
    void queryClient.prefetchQuery({
      queryKey: ["admin-agents", ""],
      queryFn: () => fetchAdminAgents("")
    });
  }, [queryClient]);

  return (
    <PageShell title="관리자 - 상담사 근태" description="로그인 기반 근태 데이터를 조회하고 검색합니다.">
      <div className="mb-4 flex gap-2">
        <input className="rounded border px-3 py-2 text-sm" onChange={(e) => setKeyword(e.target.value)} placeholder="이름/이메일 검색" value={keyword} />
        <button className="rounded bg-zinc-900 px-3 py-2 text-sm text-white" onClick={() => setSubmittedKeyword(keyword)} type="button">검색</button>
        <Link className="rounded border px-3 py-2 text-sm" href={"/admin/users/agents" as Route}>상담사 관리</Link>
        <button className="rounded border px-3 py-2 text-sm" onClick={() => router.back()} type="button">이전 페이지</button>
      </div>
      {attendanceQuery.isError ? <AlertBanner message="근태 목록을 불러오지 못했습니다." variant="error" /> : null}
      <table className="min-w-full divide-y divide-zinc-200 rounded border bg-white text-sm">
        <thead className="bg-zinc-50">
          <tr>
            <th className="px-3 py-2 text-left">일자</th>
            <th className="px-3 py-2 text-left">상담사</th>
            <th className="px-3 py-2 text-left">이메일</th>
            <th className="px-3 py-2 text-left">직급</th>
            <th className="px-3 py-2 text-left">로그인마크</th>
            <th className="px-3 py-2 text-left">로그인횟수</th>
            <th className="px-3 py-2 text-left">휴게분</th>
            <th className="px-3 py-2 text-left">근무분</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {attendanceQuery.data?.map((item) => (
            <tr key={`${item.agentId}-${item.workDate}`}>
              <td className="px-3 py-2">{item.workDate}</td>
              <td className="px-3 py-2">{item.agentName}</td>
              <td className="px-3 py-2">{item.email}</td>
              <td className="px-3 py-2">{item.grade}</td>
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
