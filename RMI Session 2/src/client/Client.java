package client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rental.CarType;
import rental.ManagerSession;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationSession;

public class Client extends AbstractTestManagement<ReservationSession, ManagerSession>{

	public Client(String scriptFile) {
		super(scriptFile);
	}
	
	public static void main(String[] args) throws Exception{
		Client client = new Client("src/trips");
        client.run();
    }

	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws Exception {
		return new HashSet<String>(ms.getBestClients());
	}

	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region)
			throws Exception {
		return session.getCheapestCarType(start, end, region).getName();
	}

	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year)
			throws Exception {
		return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
	}

	@Override
	protected ReservationSession getNewReservationSession(String name) throws Exception {
		return new ReservationSession(name);
	}

	@Override
	protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
		return new ManagerSession(name, carRentalName);
	}

	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
		session.checkForAvailableCarTypes(start, end);
	}

	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws Exception {
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
		session.createQuote(constraints, name);
		
	}

	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {
		return session.confirmQuotes(name);
	}

	@Override
	protected int getNumberOfReservationsBy(ManagerSession ms, String clientName) throws Exception {
		return ms.numberOfReservationsByRenter(clientName);
	}

	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
			throws Exception {
		return ms.numberOfReservationsByCarType(carRentalName, carType);
	}
	
}
