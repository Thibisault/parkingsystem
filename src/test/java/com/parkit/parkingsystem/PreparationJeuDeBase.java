package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class PreparationJeuDeBase {

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();


    public void updateAllDataParking() throws SQLException, ClassNotFoundException {

        Connection con = null;
        con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_TEST);
        ps.execute();
        dataBaseConfig.closeConnection(con);
    }
    public void deleteAllTicket() throws SQLException, ClassNotFoundException {

        Connection con = null;
        con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.DELETE_TICKET_TEST);
        ps.execute();
        dataBaseConfig.closeConnection(con);
    }

    public void setAvaialbleFalseTwoFirstSpotInDB() {
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

        int freePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(freePlace, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(parkingSpot);

        int freePlace1 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot1 = new ParkingSpot(freePlace1, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(parkingSpot1);
    }
    public Ticket createTicketWithRegNumber150Number1 (String RegNumber) {
        Ticket ticket = new Ticket();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

        ticket.setVehicleRegNumber(RegNumber);
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(inTime);
        Date outTime = new Date();
        outTime.setTime(System.currentTimeMillis());
        ticket.setOutTime(outTime);


        int freePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(freePlace, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);

        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(ticket);
        ticketDAO.saveTicket(ticket);

        return ticket;
    }
}