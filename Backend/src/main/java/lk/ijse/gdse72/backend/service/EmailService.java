package lk.ijse.gdse72.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPaymentSuccessEmail(String to, String username, String referenceNumber, Double amount) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("🎉 Payment Successful - EduWings");

        // Attractive HTML email
        String content = """
            <div style="font-family: Arial, sans-serif; padding:20px; background:#f9f9f9; border-radius:8px;">
                <h2 style="color:#4CAF50;">✅ Payment Successful!</h2>
                <p>Dear <b>%s</b>,</p>
                <p>We’re excited to let you know that your payment was successful.</p>
                <p><b>Reference:</b> %s</p>
                <p><b>Amount:</b> Rs. %.2f</p>
                <p>Thank you for trusting <b>EduWings</b>. We look forward to serving you!</p>
                <br>
                <p style="font-size:14px; color:#555;">If you have any questions, contact us at support@eduwings.com</p>
            </div>
            """.formatted(username, referenceNumber, amount);

        helper.setText(content, true);

        mailSender.send(message);
        System.out.println("Email sent successfully to: " + to); // Debug



    }
}
