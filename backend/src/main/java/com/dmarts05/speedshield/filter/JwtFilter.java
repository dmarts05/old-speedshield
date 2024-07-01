package com.dmarts05.speedshield.filter;

import com.dmarts05.speedshield.exception.JwtNotFoundException;
import com.dmarts05.speedshield.service.JwtService;
import com.dmarts05.speedshield.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT filter for handling authentication and authorization.
 * This filter extracts JWT from the request, validates it, and sets up Spring Security's authentication context.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructs a JwtFilter with required dependencies.
     *
     * @param jwtService         Service for JWT operations.
     * @param userDetailsService Service for loading user details.
     */
    public JwtFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests to authenticate based on JWT.
     *
     * @param request     HTTP request.
     * @param response    HTTP response.
     * @param filterChain Filter chain for additional filters.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an I/O error occurs during filter chain processing.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token;
        try {
            token = jwtService.extractTokenFromHeader(request);
        } catch (JwtNotFoundException e) {
            // Proceed to next filter if JWT is not found
            filterChain.doFilter(request, response);
            return;
        }

        // Extract username from token
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            // Proceed to next filter if JWT is expired
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create authentication token and set it in security context
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authenticationToken);

        // Proceed with filter chain after setting authentication
        filterChain.doFilter(request, response);
    }
}
