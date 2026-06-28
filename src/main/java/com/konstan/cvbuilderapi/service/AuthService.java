package com.konstan.cvbuilderapi.service;

import com.konstan.cvbuilderapi.document.User;
import com.konstan.cvbuilderapi.dto.AuthResponse;
import com.konstan.cvbuilderapi.dto.RegisterRequest;
import com.konstan.cvbuilderapi.exceptions.ResourceExistsException;
import com.konstan.cvbuilderapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request){
    log.info("Inside AuthService: register(){}", request);

    if(userRepository.existsByEmail(request.getEmail())){
        throw new ResourceExistsException("Usuario con este email ya existe");
        }

       User newUser =toDocument(request);

        userRepository.save(newUser);

       sendVerificationEmail(newUser);


       return toResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {
        try{
            String link = appBaseUrl+"/api/auth/verify-email?token=" + newUser.getVerificationToken();
            String html = """
                <div style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#1f2937;">
                  <div style="max-width:620px;margin:0 auto;padding:32px 16px;">
                
                    <div style="background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 8px 30px rgba(15,23,42,0.12);">
                
                      <div style="background:#0f172a;padding:28px;text-align:center;">
                        <h1 style="margin:0;color:#ffffff;font-size:28px;">
                          <span style="color:#3b82f6;">CV</span>Builder
                        </h1>
                      </div>
                
                      <div style="padding:40px 32px;text-align:center;">
                
                        <div style="font-size:48px;margin-bottom:16px;">✉️</div>
                
                        <h2 style="margin:0 0 12px;color:#0f172a;font-size:30px;">
                          Verify your email
                        </h2>
                
                        <p style="margin:0 0 24px;font-size:16px;line-height:1.6;color:#4b5563;">
                          Hi <strong>%s</strong>,<br>
                          Thank you for signing up for <strong>CVBuilder</strong>.
                          Please confirm your email address to activate your account and start building your professional CV.
                        </p>
                
                        <a href="%s"
                           style="display:inline-block;background:#2563eb;color:#ffffff;text-decoration:none;
                                  padding:14px 32px;border-radius:10px;font-weight:bold;font-size:16px;">
                          Verify my email
                        </a>
                
                        <div style="margin:32px 0 18px;border-top:1px solid #e5e7eb;"></div>
                
                        <p style="margin:0 0 10px;font-size:14px;color:#6b7280;">
                          Or copy this link:
                        </p>
                
                        <p style="word-break:break-all;background:#eff6ff;padding:14px;border-radius:8px;
                                  font-size:14px;color:#2563eb;margin:0 0 24px;">
                          %s
                        </p>
                
                        <p style="margin:0;font-size:14px;color:#6b7280;">
                          This link expires in <strong style="color:#ef4444;">24 hours</strong>.
                        </p>
                
                      </div>
                
                      <div style="background:#f8fafc;padding:24px 32px;border-top:1px solid #e5e7eb;">
                        <p style="margin:0;font-size:14px;line-height:1.6;color:#475569;">
                          <strong>Security tip:</strong><br>
                          If you did not create an account with CVBuilder, you can safely ignore this email.
                        </p>
                      </div>
                
                      <div style="background:#0f172a;padding:22px;text-align:center;">
                        <p style="margin:0;color:#cbd5e1;font-size:13px;">
                          © 2026 CVBuilder. All rights reserved.
                        </p>
                      </div>
                
                    </div>
                
                  </div>
                </div>
                """.formatted(
                                    newUser.getName(),
                                    link,
                                    link
                            );

            emailService.sendHtmlEmail(newUser.getEmail(),"Verifica tu email",html);
        }catch (Exception e){
            throw  new RuntimeException("Failed to send verification email:" + e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toDocument(RegisterRequest request){
       return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic").emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }
}
