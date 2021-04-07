package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        double calcul = 0.75 * Fare.CAR_RATE_PER_HOUR;
        calcul = Math.rint(calcul * 100) / 100;
        assertEquals(calcul, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    // TODO : Rajouter le assertEquals pour mesurer la réussite du test en comparant le prix attendu
    public void calculateFareReudctionCarEntryOneTime() {
        ticket.setVehicleRegNumber("150");
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(date);
        Date date2 = new Date();
        date2.setTime(System.currentTimeMillis());
        ticket.setOutTime(date2);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(1.5d, ticket.getPrice());
    }
    // TODO : Rajouter le test pour le réduction de 5 % si le véhicule entre 2 fois

    /**
     * Vérifier la réduction de 5% à partie du 2ème passage
     * @throws SQLException
     * @throws ClassNotFoundException
     * TODO - Présentation
     */
    @Test
    public void calculateFareReductionCareEntryTwice() throws SQLException, ClassNotFoundException {
        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();

        Ticket ticket = preparationJeuDeBase.createTicketWithRegNumber150Number1("150");
        Ticket ticket1 = preparationJeuDeBase.createTicketWithRegNumber150Number1("150");

        double coef = 0.95d;
        double arrondi = ticket.getPrice() * coef;
        arrondi = Math.rint(arrondi * 100)/100 ;

        assertEquals(ticket1.getPrice(),arrondi);
    }

    // TODO : Rajouter le test pour les 30 minutes gratuites (FreeTime)

    @ Test
    public void freeTimeParkingSpotLessThan30Minutes() throws SQLException, ClassNotFoundException {
        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();


        Ticket ticket = new Ticket();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

        ticket.setVehicleRegNumber("150");
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
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

        assertEquals(ticket.getPrice(), 0);
    }

    @Test
    public void freeParkingSpotMoreThan30Minutes() throws SQLException, ClassNotFoundException {
        PreparationJeuDeBase preparationJeuDeBase = new PreparationJeuDeBase();
        preparationJeuDeBase.deleteAllTicket();
        preparationJeuDeBase.updateAllDataParking();


        Ticket ticket = new Ticket();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

        ticket.setVehicleRegNumber("150");
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

        assertEquals(ticket.getPrice(), 1.5);
    }
}
