package com.example.task_manager_api;

import com.example.task_manager_api.security.JwtAuthenticationFilter;
import com.example.task_manager_api.security.JwtService;
import com.example.task_manager_api.security.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);

        jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnForbidden_whenUserIsBlocked() throws ServletException, IOException {
        UserDetails userDetails = mock(UserDetails.class);

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(userDetails.isAccountNonLocked())
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertEquals(403, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChain_whenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateUser_whenTokenIsValidAndUserIsNotBlocked() throws ServletException, IOException {
        UserDetails userDetails = mock(UserDetails.class);

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(userDetails.isAccountNonLocked()).thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorized_whenJwtIsInvalid() throws ServletException, IOException {
        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        when(jwtService.extractUsername("invalid-token"))
                .thenThrow(new JwtException("Invalid token"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
    }

    @Test
    void shouldReturnUnauthorized_whenUserNotFound() throws ServletException, IOException {
        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        when(jwtService.extractUsername("valid-token"))
                .thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenThrow(new UsernameNotFoundException("Username not found"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
    }

    @Test
    void shouldContinueFilterChain_whenHeaderIsNotBearer() throws ServletException, IOException {
        request.addHeader(
                "Authorization",
                "Basic valid-token"
        );

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }
}
