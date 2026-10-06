"use client";

import { useEffect, useState } from "react";
import type { AdminAgentUser } from "@/lib/api/types";
import { adminUserInputClassName, adminUserRowClassName } from "@/lib/admin-user-display";
import { msg } from "@/lib/messages";

type AdminAgentRowProps = {
  item: AdminAgentUser;
  pending: boolean;
  onSave: (payload: { name: string; email: string; employeeNo: string }) => void;
  onApprove: () => void;
  onToggleSuspend: () => void;
  onToggleWithdraw: () => void;
  onGradeChange: (grade: "ADMIN" | "COUNSELOR" | "TEAM_LEAD") => void;
};

export function AdminAgentRow({
  item,
  pending,
  onSave,
  onApprove,
  onToggleSuspend,
  onToggleWithdraw,
  onGradeChange
}: AdminAgentRowProps) {
  const [employeeNo, setEmployeeNo] = useState(item.employeeNo);
  const [name, setName] = useState(item.name);
  const [email, setEmail] = useState(item.email);

  useEffect(() => {
    setEmployeeNo(item.employeeNo);
    setName(item.name);
    setEmail(item.email);
  }, [item]);

  const fieldClass = adminUserInputClassName(item.withdrawnYn, item.suspendedYn);
  const rowClass = adminUserRowClassName(item.withdrawnYn, item.suspendedYn);

  return (
    <tr className={rowClass}>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setEmployeeNo(e.target.value)} value={employeeNo} />
      </td>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setName(e.target.value)} value={name} />
      </td>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setEmail(e.target.value)} type="email" value={email} />
      </td>
      <td className="px-3 py-2">{msg.label("approvalStatus", item.approvalStatus)}</td>
      <td className="px-3 py-2">{msg.label("grade", item.grade)}</td>
      <td className="px-3 py-2">{msg.label("yn", item.suspendedYn)}</td>
      <td className="px-3 py-2">{msg.label("yn", item.withdrawnYn)}</td>
      <td className="px-3 py-2">
        <div className="flex flex-wrap gap-2">
          <button
            className="rounded border px-2 py-1 disabled:opacity-50"
            disabled={pending}
            onClick={() => onSave({ name, email, employeeNo })}
            type="button"
          >
            {pending ? msg.ui("common.saving") : msg.ui("common.save")}
          </button>
          {item.approvalStatus === "PENDING" ? (
            <button className="rounded border px-2 py-1" onClick={onApprove} type="button">
              {msg.ui("common.approve")}
            </button>
          ) : null}
          <button className="rounded border px-2 py-1" onClick={onToggleSuspend} type="button">
            {msg.ui("admin.toggleSuspend")}
          </button>
          <button className="rounded border px-2 py-1" onClick={onToggleWithdraw} type="button">
            {msg.ui("admin.toggleWithdraw")}
          </button>
          <select
            className="rounded border px-2 py-1"
            defaultValue={item.grade}
            onChange={(event) => onGradeChange(event.target.value as "ADMIN" | "COUNSELOR" | "TEAM_LEAD")}
          >
            <option value="ADMIN">{msg.label("grade", "ADMIN")}</option>
            <option value="COUNSELOR">{msg.label("grade", "COUNSELOR")}</option>
            <option value="TEAM_LEAD">{msg.label("grade", "TEAM_LEAD")}</option>
          </select>
        </div>
      </td>
    </tr>
  );
}
