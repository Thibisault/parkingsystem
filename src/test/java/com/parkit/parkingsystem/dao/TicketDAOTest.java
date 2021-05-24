package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.PreparationJeuDeBase;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TicketDAOTest {

    @Test
    void saveTicket() throws SQLException, ClassNotFoundException {

        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();

        Ticket ticket = new Ticket();

        ticket.setVehicleRegNumber("140");
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(date);
        Date date2 = new Date();
        date2.setTime(System.currentTimeMillis());
        ticket.setOutTime(date2);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(ticket);

        TicketDAO saveTicketDAO = new TicketDAO();
        boolean returnMethode = saveTicketDAO.saveTicket(ticket);
        assertEquals(returnMethode,true);
    }

    @Test
    void getTicket() throws SQLException, ClassNotFoundException {

        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();

        TicketDAO ticketDAO = new TicketDAO();

        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("150");
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(date);
        Date date2 = new Date();
        date2.setTime(System.currentTimeMillis());
        ticket.setOutTime(date2);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(ticket);

        ticketDAO.saveTicket(ticket);

        // TODO : Faire la bonne conversion de date pour la comparaison,
        Ticket ticketInDB = ticketDAO.getTicket("150");
        boolean result = ticketInDB.getVehicleRegNumber().equals(ticket.getVehicleRegNumber())
                && ticketInDB.getPrice() == ticket.getPrice()
                && ticketInDB.getParkingSpot().equals(ticket.getParkingSpot());
        assertTrue(result);
    }

    @Test
    void updateTicket() throws SQLException, ClassNotFoundException {

        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();

        TicketDAO ticketDAO = new TicketDAO();

        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("160");
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(date);
        Date date2 = new Date();
        date2.setTime(System.currentTimeMillis());
        ticket.setOutTime(date2);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(ticket);
        ticketDAO.saveTicket(ticket);

        boolean updateValues = ticketDAO.updateTicket(ticket);
        assertEquals(updateValues, true);
    }

    @Test
    void getRegNumberCount() throws SQLException, ClassNotFoundException {

        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();

        TicketDAO ticketDAO = new TicketDAO();

        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("170");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(date);
        ticketDAO.saveTicket(ticket);

        Ticket ticket1 = new Ticket();
        ticket1.setVehicleRegNumber("170");
        ticketDAO.saveTicket(ticket1);
        ParkingSpot parkingSpot1 = new ParkingSpot(2, ParkingType.CAR, false);
        ticket1.setParkingSpot(parkingSpot1);
        Date date1 = new Date();
        date1.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket1.setInTime(date1);
        ticketDAO.saveTicket(ticket1);

        int countSameReg = ticketDAO.getRegNumberCount("170");
        assertEquals(2, countSameReg);


    }
}