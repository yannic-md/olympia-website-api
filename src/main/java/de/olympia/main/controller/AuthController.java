package de.olympia.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import de.olympia.main.service.AuthService;
import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        try {
            LoginResponse response = authService.login(request);
            establishSession(request.getUsername(), request.getPassword(), httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request,
                                                  HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.register(request);
            establishSession(request.getUsername(), request.getPassword(), httpRequest);
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest request,
                                                    HttpServletRequest httpRequest) {
        try {
            LoginResponse response = authService.adminLogin(request);
            establishSession(request.getUsername(), request.getPassword(), httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    /**
     * Returns current authenticated user info based on the active session cookie.
     * Used by the frontend to restore auth state on page reload.
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, "Not authenticated"));
        }
        LoginResponse response = authService.getUserInfo(auth.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Invalidates the current session and clears the security context.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    /**
     * Authenticates the user via Spring Security's AuthenticationManager and binds
     * the resulting Authentication to the HTTP session for cookie-based auth.
     */
    private void establishSession(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setMaxInactiveInterval(86400); // 24 hours
    }
}
