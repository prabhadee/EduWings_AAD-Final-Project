package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;  // Fixed import
import jakarta.mail.internet.MimeMessage; // Fixed import

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final InvoicePDFService invoicePDFService;

    @Value("${app.email:noreply@eduwings.com}")
    private String fromEmail;

    @Value("${app.name:EduWings}")
    private String appName;

    @Override
    public void sendPaymentSuccessEmail(String toEmail, String studentName, String paymentReference, Double amount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("Payment Successful - " + appName);

            // Generate HTML content
            String htmlContent = generateEmailContent(studentName, paymentReference, amount);
            helper.setText(htmlContent, true);

            // Generate and attach invoice PDF
            try {
                byte[] invoicePdf = invoicePDFService.generateInvoicePDF(paymentReference);
                helper.addAttachment("Invoice_" + paymentReference + ".pdf",
                        new ByteArrayResource(invoicePdf));
                log.info("Invoice PDF attached to email");
            } catch (Exception e) {
                log.error("Failed to attach PDF to email: {}", e.getMessage());
                // Continue sending email without attachment
            }

            mailSender.send(message);
            log.info("Payment success email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send payment success email to {}: {}", toEmail, e.getMessage());
            // Don't throw exception to avoid breaking payment flow
        }
    }

    private String generateEmailContent(String studentName, String paymentReference, Double amount) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px;">
                        <h1 style="margin: 0; font-size: 28px;">%s</h1>
                        <p style="margin: 10px 0 0; font-size: 16px;">Payment Confirmation</p>
                    </div>
                    
                    <div style="padding: 30px; background: #f8f9fa; border-radius: 10px; margin-top: 20px;">
                        <h2 style="color: #28a745; margin-top: 0;">Payment Successful!</h2>
                        <p>Dear %s,</p>
                        <p>We're pleased to confirm that your payment has been successfully processed.</p>
                        
                        <div style="background: white; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <h3 style="margin-top: 0; color: #333;">Payment Details:</h3>
                            <ul style="list-style: none; padding: 0;">
                                <li style="margin: 10px 0;"><strong>Reference Number:</strong> %s</li>
                                <li style="margin: 10px 0;"><strong>Amount:</strong> LKR %.2f</li>
                                <li style="margin: 10px 0;"><strong>Status:</strong> <span style="color: #28a745; font-weight: bold;">COMPLETED</span></li>
                            </ul>
                        </div>
                        
                        <p>Your invoice has been attached to this email for your records.</p>
                        <p>You can now access your enrolled courses through your student dashboard.</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="http://localhost:3000/pages/student/courses.html" 
                               style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                      color: white; text-decoration: none; padding: 12px 30px; 
                                      border-radius: 25px; font-weight: bold; display: inline-block;">
                                Access My Courses
                            </a>
                        </div>
                    </div>
                    
                    <div style="text-align: center; margin-top: 30px; color: #666; font-size: 14px;">
                        <p>Thank you for choosing %s!</p>
                        <p>If you have any questions, please contact us at support@eduwings.com</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                appName, studentName, paymentReference, amount, appName
        );
    }
}