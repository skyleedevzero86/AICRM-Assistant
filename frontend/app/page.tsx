import { Bot, MessageSquareText, ShieldCheck, Ticket } from "lucide-react";

const tickets = [
  { id: "T-1042", customer: "김민서", topic: "배송 지연", priority: "높음", status: "진행" },
  { id: "T-1041", customer: "박준호", topic: "멤버십 환불", priority: "보통", status: "검토" },
  { id: "T-1039", customer: "이서연", topic: "쿠폰 미적용", priority: "낮음", status: "대기" }
];

export default function Home() {
  return (
    <main className="min-h-screen">
      <div className="grid min-h-screen grid-cols-[280px_1fr_360px]">
        <aside className="border-r border-zinc-200 bg-white p-5">
          <div className="mb-8 flex items-center gap-2 text-lg font-semibold">
            <Bot className="h-5 w-5 text-teal-600" />
            CallMind AI
          </div>
          <nav className="space-y-1 text-sm">
            {["상담 티켓", "고객", "지식 문서", "CRM 액션", "관리자 대시보드"].map((item) => (
              <button
                className="flex w-full items-center rounded-md px-3 py-2 text-left text-zinc-700 hover:bg-zinc-100"
                key={item}
              >
                {item}
              </button>
            ))}
          </nav>
        </aside>

        <section className="p-6">
          <div className="mb-5 flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-semibold">상담원 Copilot</h1>
              <p className="text-sm text-zinc-500">티켓, 상담 메시지, RAG 답변 초안을 한 화면에서 처리합니다.</p>
            </div>
            <button className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white">새 티켓</button>
          </div>

          <div className="grid grid-cols-3 gap-3">
            {[
              ["오늘 상담", "128", Ticket],
              ["AI 초안 채택률", "71%", MessageSquareText],
              ["정책 위반 감지", "6", ShieldCheck]
            ].map(([label, value, Icon]) => (
              <div className="rounded-lg border border-zinc-200 bg-white p-4" key={label as string}>
                <Icon className="mb-3 h-5 w-5 text-teal-600" />
                <div className="text-2xl font-semibold">{value as string}</div>
                <div className="text-sm text-zinc-500">{label as string}</div>
              </div>
            ))}
          </div>

          <div className="mt-5 rounded-lg border border-zinc-200 bg-white">
            <div className="border-b border-zinc-200 px-4 py-3 font-medium">상담 티켓</div>
            <div className="divide-y divide-zinc-100">
              {tickets.map((ticket) => (
                <div className="grid grid-cols-5 items-center px-4 py-3 text-sm" key={ticket.id}>
                  <span className="font-medium">{ticket.id}</span>
                  <span>{ticket.customer}</span>
                  <span>{ticket.topic}</span>
                  <span>{ticket.priority}</span>
                  <span className="text-teal-700">{ticket.status}</span>
                </div>
              ))}
            </div>
          </div>
        </section>

        <aside className="border-l border-zinc-200 bg-white p-5">
          <h2 className="mb-4 text-base font-semibold">AI 추천 패널</h2>
          <div className="space-y-4 text-sm">
            <section>
              <h3 className="mb-2 font-medium">현재 상담 요약</h3>
              <p className="rounded-lg bg-zinc-50 p-3 text-zinc-600">
                고객은 배송 지연으로 불만을 제기했고, 이전 문의에서도 동일 상품 배송 문제를 경험했습니다.
              </p>
            </section>
            <section>
              <h3 className="mb-2 font-medium">추천 답변</h3>
              <p className="rounded-lg bg-teal-50 p-3 text-teal-900">
                불편을 드린 점 사과드리며, 현재 배송 상태를 확인한 뒤 보상 쿠폰 제공 가능 여부를 안내하겠습니다.
              </p>
            </section>
            <section>
              <h3 className="mb-2 font-medium">관련 지식</h3>
              <ul className="space-y-2 text-zinc-600">
                <li>배송 지연 보상 정책 v2</li>
                <li>VIP 고객 쿠폰 지급 기준</li>
                <li>반복 문의 escalation 규칙</li>
              </ul>
            </section>
          </div>
        </aside>
      </div>
    </main>
  );
}
