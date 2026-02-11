package com.lauzon.stackOverflow.service.impl;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.CreateSmtpEmail;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import com.lauzon.stackOverflow.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
            apiKey.setApiKey(brevoApiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi();

            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(fromEmail);

            SendSmtpEmailTo recipient = new SendSmtpEmailTo();
            recipient.setEmail(to);

            SendSmtpEmail email = new SendSmtpEmail();
            email.setSender(sender);
            email.setTo(List.of(recipient));
            email.setSubject(subject);
            email.setHtmlContent("<html><body><p>" + body.replace("\n", "<br>") + "</p></body></html>");

            CreateSmtpEmail result = api.sendTransacEmail(email);
            log.info("✅ Email sent successfully to: {}. Message ID: {}", to, result.getMessageId());

        } catch (ApiException e) {
            log.error("❌ Brevo API error. Status: {}, Response: {}", e.getCode(), e.getResponseBody());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Failed to send email to: {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}