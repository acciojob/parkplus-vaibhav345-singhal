package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;

    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        User user;

        try {
            user = userRepository3.findById(userId).get();
        } catch (Exception e) {
            throw new Exception("User is not found");
        }

        ParkingLot parkingLot;
        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw new Exception("ParkingLot is not found");
        }

        Spot spot = null;
        int minTime = Integer.MAX_VALUE;
        for (Spot spot1 : parkingLot.getSpotList()) {

            if (numberOfWheels == 2 && (spot1.getPricePerHour() * timeInHours) < (minTime * timeInHours)) {
                spot = spot1;
                minTime = spot1.getPricePerHour();
            } else if (numberOfWheels == 4 && (SpotType.FOUR_WHEELER == spot1.getSpotType() || SpotType.OTHERS == spot1.getSpotType() && (spot1.getPricePerHour() * timeInHours) < (minTime * timeInHours))) {
                spot = spot1;
                minTime = spot1.getPricePerHour();
            } else if (numberOfWheels > 4 && SpotType.OTHERS == spot1.getSpotType() && (spot1.getPricePerHour() * timeInHours) < (minTime * timeInHours)) {
                spot = spot1;
                minTime = spot1.getPricePerHour();
            }
        }

        if (spot == null) throw new Exception("Spot is not available");

        Reservation reservation = new Reservation();
        reservation.setSpot(spot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        spot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

        spotRepository3.save(spot);
        userRepository3.save(user);
        return reservation;
    }
}
