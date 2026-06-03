"use client";

import { useEffect, useState } from "react";
import type { AdminCustomerUser } from "@/lib/api/types";
import { adminUserInputClassName, adminUserRowClassName } from "@/lib/admin-user-display";
import { msg } from "@/lib/messages";

type AdminCustomerRowProps = {
  item: AdminCustomerUser;
  pending: boolean;
  onSave: (payload: { name: string; email: string; phone: string }) => void;
  onToggleSuspend: () => void;
  onToggleWithdraw: () => void;
};

export function AdminCustomerRow({ item, pending, onSave, onToggleSuspend, onToggleWithdraw }: AdminCustomerRowProps) {
  const [name, setName] = useState(item.name);
  const [email, setEmail] = useState(item.email);
  const [phone, setPhone] = useState(item.phone);

  useEffect(() => {
    setName(item.name);
    setEmail(item.email);
    setPhone(item.phone);
  }, [item]);

  const fieldClass = adminUserInputClassName(item.withdrawnYn, item.suspendedYn);
  const rowClass = adminUserRowClassName(item.withdrawnYn, item.suspendedYn);

  return (
    <tr className={rowClass}>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setName(e.target.value)} value={name} />
      </td>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setEmail(e.target.value)} type="email" value={email} />
      </td>
      <td className="px-3 py-2">
        <input className={fieldClass} onChange={(e) => setPhone(e.target.value)} type="tel" value={phone} />
      </td>
      <td className="px-3 py-2">{msg.label("yn", item.suspendedYn)}</td>
      <td className="px-3 py-2">{msg.label("yn", item.withdrawnYn)}</td>
      <td className="px-3 py-2">
        <div className="flex flex-wrap gap-2">
          <button
            className="rounded border px-2 py-1 disabled:opacity-50"
            disabled={pending}
            onClick={() => onSave({ name, email, phone })}
            type="button"
          >
            {pending ? msg.ui("common.saving") : msg.ui("common.save")}
          </button>
          <button className="rounded border px-2 py-1" onClick={onToggleSuspend} type="button">
            {msg.ui("admin.toggleSuspend")}
          </button>
          <button className="rounded border px-2 py-1" onClick={onToggleWithdraw} type="button">
            {msg.ui("admin.toggleWithdraw")}
          </button>
        </div>
      </td>
    </tr>
  );
}
