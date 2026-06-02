import { NextResponse } from "next/server";

const coreApiOrigin = process.env.CORE_API_URL ?? "http://localhost:8080";

export async function POST(request: Request) {
  const response = await fetch(`${coreApiOrigin}/api/auth/signup/agent`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: await request.text(),
    cache: "no-store"
  });

  const body = await response.text();

  return new NextResponse(body, {
    status: response.status,
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/json"
    }
  });
}
