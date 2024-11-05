package com.kyawgyi.role.config;

import com.kyawgyi.role.service.JWTUtils;
import com.kyawgyi.role.service.UsersDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    private final UsersDetailsService userDetailsService;

    public JWTAuthFilter (JWTUtils jwtUtils, UsersDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if(authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authorizationHeader.substring(7);
        userEmail = jwtUtils.extractUsername(jwtToken);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if(jwtUtils.validateToken(jwtToken, userDetails)) {
                SecurityContext securityContext= SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);

//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            final String token = authorizationHeader.substring(7);
//        }
    }
}