package com.pranjal.grpc.client.filter;

import com.pranjal.grpc.common.AppContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.pranjal.grpc.common.GrpcConstants.REQUEST_ID;

@Slf4j
@Component
public class AppFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            AppContext.setRequestId(UUID.randomUUID());
            MDC.put(REQUEST_ID, String.valueOf(AppContext.getRequestId()));
            filterChain.doFilter(request, response);
        } finally {
            AppContext.clean();
            MDC.clear();
        }
    }
}
