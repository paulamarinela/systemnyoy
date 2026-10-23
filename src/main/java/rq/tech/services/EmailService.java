package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import rq.tech.models.Repair;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@rqtech.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendRepairFinishedEmail(Repair repair) {
        if (repair.getCustomerEmail() == null || repair.getCustomerEmail().isEmpty()) {
            return;
        }

        String subject = "RQ Tech - Repair Ready for Pickup: " + repair.getDevice();
        String body = String.format(
                "Hello %s,\n\n" +
                "Good news! Your %s (SN: %s) is ready for pickup.\n\n" +
                "Repair Issue: %s\n" +
                "Total Bill: P %.2f\n\n" +
                "Please visit us to claim your device.\n\n" +
                "Best regards,\n" +
                "RQ Tech Team",
                repair.getCustomerName(),
                repair.getDevice(),
                repair.getSerialNumber() != null ? repair.getSerialNumber() : "N/A",
                repair.getIssue(),
                repair.getBill()
        );

        sendEmail(repair.getCustomerEmail(), subject, body);
    }
}
