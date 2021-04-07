package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.text.DecimalFormat;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        //TODO: Remplacer les heures par des millisecondes
        long inHour = ticket.getInTime().getTime();
         long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long  duration = outHour - inHour;
        float hours = duration/1000f/60f/60f;
        long freeTime = 30l*60l*1000l;
        TicketDAO regNumberDAO = new TicketDAO();
        double coef = 1d;

        if (regNumberDAO.getRegNumberCount(ticket.getVehicleRegNumber()) >=1 ) {
            coef = 0.95d;
        }

        if (duration <= freeTime) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(hours * Fare.CAR_RATE_PER_HOUR * coef);
                    double arrondi = ticket.getPrice();
                    arrondi = Math.rint(arrondi * 100)/100;
                    ticket.setPrice(arrondi);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(hours * Fare.BIKE_RATE_PER_HOUR * coef);
                    double arrondi = ticket.getPrice();
                    arrondi = Math.rint(arrondi * 100)/100;
                    ticket.setPrice(arrondi);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}