package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();

        int bill = reservation.getSpot().getPricePerHour() * reservation.getNumberOfHours();

        if (amountSent < bill) {
            throw new Exception("Insufficient Amount");
        }


        mode = mode.toUpperCase();
        boolean isModePresent = false;

        Payment payment = new Payment();

        if (PaymentMode.CASH.equals(mode)) {
            isModePresent = true;
            payment.setPaymentMode(PaymentMode.CASH);
        } else if (PaymentMode.UPI.equals(mode)) {
            isModePresent = true;
            payment.setPaymentMode(PaymentMode.UPI);
        } else if (PaymentMode.CARD.equals(mode)) {
            isModePresent = true;
            payment.setPaymentMode(PaymentMode.CARD);
        }

        if (isModePresent == false) throw new Exception("Payment mode not detected");

        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);

        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return payment;
    }
}
