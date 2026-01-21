package com.example.pestacle_app.controller;

import com.example.pestacle_app.service.EmailService;
import com.example.pestacle_app.service.QrCodeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("http://localhost:3000")
public class PaymentController {

    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    public PaymentController(QrCodeService qrCodeService, EmailService emailService) {
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @PostMapping("/create-checkout-session")
    public Map<String, Object> createCheckoutSession(@RequestBody Map<String, Object> request) throws StripeException {

        // Récupérer le montant et l'email depuis le body de la requête
        Long amount = ((Number) request.get("amount")).longValue();
        String email = (String) request.get("email");

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.ALIPAY)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/panier")
                .setCustomerEmail(email)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Réservation de spectacles")
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                .build();

        Session session = Session.create(params);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId());
        return result;
    }

    @PostMapping("/send-ticket")
    public Map<String, Object> sendTicket(@RequestBody Map<String, Object> body) throws StripeException {
        String sessionId = (String) body.get("sessionId");
        if (sessionId == null || sessionId.isBlank()) {
            return Map.of("ok", false, "reason", "missing_session_id");
        }

        Session session = Session.retrieve(sessionId);

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            return Map.of("ok", false, "reason", "not_paid");
        }

        String email = null;
        if (session.getCustomerDetails() != null) {
            email = session.getCustomerDetails().getEmail();
        }
        if (email == null || email.isBlank()) {
            email = session.getCustomerEmail();
        }

        if (email == null || email.isBlank()) {
            return Map.of("ok", false, "reason", "missing_email_in_session");
        }

        String payload = "TICKET:" + email + ":" + sessionId + ":" + System.currentTimeMillis();
        byte[] qr = qrCodeService.generatePng(payload);

        String html = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmation de réservation</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                    <table role="presentation" style="width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                        <!-- Header -->
                        <tr>
                            <td style="background: linear-gradient(135deg, #4ECDC4 0%%, #44B3B0 100%%); padding: 40px 30px; text-align: center;">
                                <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: bold; letter-spacing: -0.5px;">
                                    🎭 Pestacle
                                </h1>
                                <p style="margin: 10px 0 0 0; color: rgba(255, 255, 255, 0.9); font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">
                                    Billetterie en ligne
                                </p>
                            </td>
                        </tr>

                        <!-- Success Badge -->
                        <tr>
                            <td style="padding: 0; text-align: center; transform: translateY(-25px);">
                                <div style="display: inline-block; background-color: #ffffff; border-radius: 50px; padding: 12px 24px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
                                    <span style="color: #4ECDC4; font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;">
                                        ✅ Paiement confirmé
                                    </span>
                                </div>
                            </td>
                        </tr>

                       
                        <tr>
                            <td style="padding: 0 40px 40px 40px;">
                                <h2 style="margin: 0 0 20px 0; color: #1a1a1a; font-size: 24px; font-weight: bold;">
                                    Merci pour votre réservation ! 🎉
                                </h2>

                                <p style="margin: 0 0 20px 0; color: #555555; font-size: 16px; line-height: 1.6;">
                                    Bonjour,
                                </p>

                                <p style="margin: 0 0 30px 0; color: #555555; font-size: 16px; line-height: 1.6;">
                                    Nous avons bien reçu votre paiement et sommes ravis de vous accueillir à notre spectacle !
                                    Votre billet électronique avec QR code est joint à cet email.
                                </p>

                                <!-- Info Box -->
                                <div style="background: linear-gradient(135deg, rgba(78, 205, 196, 0.1) 0%%, rgba(78, 205, 196, 0.05) 100%%); border-left: 4px solid #4ECDC4; border-radius: 8px; padding: 20px; margin: 0 0 30px 0;">
                                    <p style="margin: 0 0 12px 0; color: #1a1a1a; font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;">
                                        📋 Détails de votre réservation
                                    </p>
                                    <p style="margin: 0; color: #555555; font-size: 14px; line-height: 1.6;">
                                        <strong>Référence :</strong> %s<br>
                                        <strong>Email :</strong> %s
                                    </p>
                                </div>

                                <!-- Instructions -->
                                <div style="background-color: #f9f9f9; border-radius: 8px; padding: 20px; margin: 0 0 30px 0;">
                                    <p style="margin: 0 0 15px 0; color: #1a1a1a; font-size: 16px; font-weight: 600;">
                                        📱 Comment utiliser votre billet :
                                    </p>
                                    <ul style="margin: 0; padding-left: 20px; color: #555555; font-size: 14px; line-height: 1.8;">
                                        <li>Téléchargez le QR code joint à cet email</li>
                                        <li>Présentez-vous 15 minutes avant le début du spectacle</li>
                                        <li>Montrez votre QR code à l'entrée de la salle</li>
                                    </ul>
                                </div>

                                <!-- CTA Button -->
                                <div style="text-align: center; margin: 0 0 30px 0;">
                                    <a href="http://localhost:3000" style="display: inline-block; background: linear-gradient(135deg, #4ECDC4 0%%, #44B3B0 100%%); color: #ffffff; text-decoration: none; padding: 14px 32px; border-radius: 50px; font-size: 16px; font-weight: 600; box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3);">
                                        Découvrir d'autres spectacles
                                    </a>
                                </div>

                                <p style="margin: 0; color: #888888; font-size: 14px; line-height: 1.6; text-align: center;">
                                    Nous vous souhaitons un excellent spectacle ! 🎭
                                </p>
                            </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                            <td style="background-color: #1a1a1a; padding: 30px 40px; text-align: center;">
                                <p style="margin: 0 0 10px 0; color: rgba(255, 255, 255, 0.9); font-size: 16px; font-weight: 600;">
                                    Pestacle - Billetterie
                                </p>
                                <p style="margin: 0 0 15px 0; color: rgba(255, 255, 255, 0.6); font-size: 13px;">
                                    Besoin d'aide ? Contactez-nous à<br>
                                    <a href="mailto:support@pestacle.com" style="color: #4ECDC4; text-decoration: none;">support@pestacle.com</a>
                                </p>
                                <p style="margin: 0; color: rgba(255, 255, 255, 0.4); font-size: 12px;">
                                    © 2026 Pestacle. Tous droits réservés.
                                </p>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(sessionId, email);

        emailService.sendWithQr(email, "🎭 Confirmation de votre réservation Pestacle", html, qr);

        return Map.of("ok", true);
    }

    @GetMapping("/success")
    public String getSuccess() {
        return "payment successful";
    }

    @GetMapping("/failed")
    public String getFailed() {
        return "payement failed";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "payment canceled";
    }
}