package com.givemefive.gmfcontroller.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class WxAppOpenidAuthenticationFilter extends OncePerRequestFilter {

    private static final String OPENID_HEADER = "X-Wx-Openid";
    private static final String APP_API_PREFIX = "/api/app/";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (isAppApi(request) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String openid = request.getHeader(OPENID_HEADER);
            if (StringUtils.isNotBlank(openid)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                StringUtils.trim(openid),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_WX_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAppApi(HttpServletRequest request) {
        return request.getRequestURI().startsWith(APP_API_PREFIX);
    }
}
