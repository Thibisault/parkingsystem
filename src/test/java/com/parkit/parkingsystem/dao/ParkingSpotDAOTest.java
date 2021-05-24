package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.PreparationJeuDeBase;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParkingSpotDAOTest {

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @Test
    void getNextAvailableSlot() throws SQLException, ClassNotFoundException {

        Connection con = null;
        con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_TEST);
        ps.execute();

        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

        int freePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        PreparedStatement ps1 = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        ps1.setBoolean(1, false);
        ps1.setInt(2, freePlace);
        ParkingSpot parkingSpot = new ParkingSpot(freePlace, ParkingType.CAR, ps1.execute());

        int freePlace1 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        PreparedStatement ps2 = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        ps2.setBoolean(1, false);
        ps2.setInt(2, freePlace1);
        ParkingSpot parkingSpot1 = new ParkingSpot(freePlace1, ParkingType.CAR, ps2.execute());

        assertEquals(3,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));

        dataBaseConfig.closeConnection(con);
    }

    @Test
    void updateParking() throws SQLException, ClassNotFoundException {

        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.updateAllDataParking();
        preparationJeuDeBase.deleteAllTicket();

        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        int freePlace = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(freePlace, ParkingType.CAR, false);
        boolean testUpdateParking = parkingSpotDAO.updateParking(parkingSpot);
        assertEquals(true,testUpdateParking);

    }
}