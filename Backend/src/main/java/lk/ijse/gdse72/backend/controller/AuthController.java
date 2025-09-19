package lk.ijse.gdse72.backend.controller;


import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse72.backend.dto.ApiResponse;
import lk.ijse.gdse72.backend.dto.AuthDTO;
import lk.ijse.gdse72.backend.dto.AuthResponseDTO;
import lk.ijse.gdse72.backend.dto.RegisterDTO;
import lk.ijse.gdse72.backend.entity.Role;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.repository.UserRepository;
import lk.ijse.gdse72.backend.service.AuthService;
import lk.ijse.gdse72.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin

public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private static final String CLIENT_ID = "694201785861-9bbchrt7h676jpv676rmu2q78iq9grc1.apps.googleusercontent.com";
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(new ApiResponse(
                200,
                "OK",
                authService.register(registerDTO)));
    }
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(
//            @RequestBody AuthDTO authDTO) {
//        return ResponseEntity.ok(new ApiResponse(
//                200,
//                "OK",
//                authService.authenticate(authDTO)));
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthDTO authDTO) {
        try {
            AuthResponseDTO authResponse = authService.authenticate(authDTO);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", authResponse.getAccessToken());
            data.put("role", authResponse.getRole());
            data.put("username", authResponse.getUsername());
            data.put("userId", authResponse.getUserId());

            return ResponseEntity.ok(new ApiResponse(200, "Login SuccessFull ", data));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(401, "Authentication Failed", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Handle logout logic (invalidate session, clear cookies, etc.)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestParam String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String googleId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");

                // 🔎 1. Check if user exists
                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    // 🆕 2. Register new user
                    user = new User();
                    user.setEmail(email);
                    user.setNumber("N/A");
                    String password = generateRandomPassword(12);
                    user.setRole(Role.valueOf("USER")); // assign default role
                    user.setPassword(passwordEncoder.encode(password)); // generate random 12-char password
                    user.setUsername(name);
                    // user.setGoogleId(googleId); // optional
                    userRepository.save(user);
                }

                // 🔑 3. Generate app JWT token
                String jwt = jwtUtil.generateToken(user.getEmail());

                // ✅ 4. Return response
                return ResponseEntity.ok(new AuthResponseDTO(
                        jwt,
                        user.getRole().toString(),
                        user.getUsername(),
                        user.getId()
                ));
            } else {
                return ResponseEntity.badRequest().body("Invalid ID token.");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying token: " + e.getMessage());
        }
    }

    public String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%!&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
