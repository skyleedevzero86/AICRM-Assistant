import type { NextConfig } from "next";

const coreApiOrigin = process.env.CORE_API_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  experimental: {
    typedRoutes: true
  },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${coreApiOrigin}/api/:path*`
      }
    ];
  }
};

export default nextConfig;
