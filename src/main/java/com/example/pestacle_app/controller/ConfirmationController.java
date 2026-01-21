package com.example.pestacle_app.controller;

import com.example.pestacle_app.service.EmailService;
import com.example.pestacle_app.service.QrCodeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/confirmation")
@CrossOrigin("http://localhost:3000")
public class ConfirmationController {

    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    public ConfirmationController(QrCodeService qrCodeService, EmailService emailService) {
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @PostMapping("/send-confirmation")
    public Map<String, Object> sendConfirmation(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");

        if (email == null || email.isBlank()) {
            return Map.of("ok", false, "reason", "missing_email");
        }

        // QR factice (contenu libre)
        String fakePayload = "FAKE_TICKET:" + email + ":" + System.currentTimeMillis();
        byte[] qr = qrCodeService.generatePng(fakePayload);

        String html = """
            <p>Bonjour,</p>
            <p>Merci pour votre commande ✅</p>
            <p>Voici votre QR code en pièce jointe.</p>
        """;

        emailService.sendWithQr(email, "Confirmation", html, qr);

        return Map.of("ok", true);
    }
}