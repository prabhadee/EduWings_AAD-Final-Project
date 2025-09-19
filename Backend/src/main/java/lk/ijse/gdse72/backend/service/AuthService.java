package lk.ijse.gdse72.backend.service;



import lk.ijse.gdse72.backend.dto.AuthDTO;
import lk.ijse.gdse72.backend.dto.AuthResponseDTO;
import lk.ijse.gdse72.backend.dto.RegisterDTO;
import lk.ijse.gdse72.backend.entity.Role;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.repository.UserRepository;
import lk.ijse.gdse72.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO authenticate(AuthDTO authDTO) {
        User user=userRepository.findByEmail(authDTO.getEmail())
                        .orElseThrow(
                                ()->new UsernameNotFoundException
                                        ("Email not found"));
        if (!passwordEncoder.matches(
                authDTO.getPassword(),
                user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        String token=jwtUtil.generateToken(user.getEmail());
//        return  new AuthResponseDTO(token,user.getRole().name());
        return new AuthResponseDTO(token,user.getRole().name(), user.getUsername(), user.getId());
    }

    public String   register(RegisterDTO registerDTO) {
        if(userRepository.findByUsername(
                registerDTO.getEmail()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User user=User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(
                        registerDTO.getPassword()))
                .number(registerDTO.getNumber())
                .role(Role.valueOf(registerDTO.getRole()))
                .build();
        userRepository.save(user);
        return  "User Registration Success";
    }
}