package com.example.pestacle_app.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment") // Changé de /api/payments à /api/payment
@CrossOrigin("http://localhost:3000")
public class PaymentController {

    @PostMapping("/create-checkout-session")
    public Map<String, Object> createCheckoutSession(@RequestBody Map<String, Object> request) throws StripeException {

        // Récupérer le montant depuis le body de la requête
        Long amount = ((Number) request.get("amount")).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.ALIPAY)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/panier")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Réservation de spectacles")
                                                                .build()
                                                )
                                                .build()
                                ).setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId());
        return result;
    }

    @GetMapping("/success")
    public String getSuccess(){
        return "payment successful";
    }

    @GetMapping("/cancel")
    public String cancel(){
        return "payment canceled";
    }
}