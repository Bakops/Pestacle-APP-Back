package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.CreatePaymentRequest;
import com.example.pestacle_app.dto.CreatePaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public CreatePaymentResponse createPaymentIntent(CreatePaymentRequest request) throws StripeException {

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())           // 1099 pour 10,99 EUR
                .setCurrency(request.getCurrency())       // "eur"
                .setDescription(request.getDescription())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }
}
