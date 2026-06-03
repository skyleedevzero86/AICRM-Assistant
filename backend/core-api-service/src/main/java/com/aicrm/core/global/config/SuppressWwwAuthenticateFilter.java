package com.aicrm.core.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SuppressWwwAuthenticateFilter extends OncePerRequestFilter {

    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, new HttpServletResponseWrapper(response) {
            @Override
            public void setHeader(String name, String value) {
                if (!isWwwAuthenticate(name)) {
                    super.setHeader(name, value);
                }
            }

            @Override
            public void addHeader(String name, String value) {
                if (!isWwwAuthenticate(name)) {
                    super.addHeader(name, value);
                }
            }

            private boolean isWwwAuthenticate(String name) {
                return WWW_AUTHENTICATE.equalsIgnoreCase(name);
            }
        });
    }
}
