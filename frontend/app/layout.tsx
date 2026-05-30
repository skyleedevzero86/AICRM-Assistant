import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "CallMind AI",
  description: "Call center copilot and CRM automation platform"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
