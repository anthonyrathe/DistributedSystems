package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestAgency<CarRentalSessionRemote, ManagerSessionRemote>{
    
    @EJB
    static CarRentalSessionRemote session;
    @EJB
    static ManagerSessionRemote manager_session;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        System.out.println("found rental companies: "+session.getAllRentalCompanies());
        Main main = new Main("simpleTrips");
        main.run();
    }

    public Main(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        CarRentalSessionRemote remote = session;
        session.setName(name);
        return session;
    }

    @Override
    protected ManagerSessionRemote getNewsManagerSession(String name, String carRentalName) throws Exception {
        ManagerSessionRemote remote = manager_session;
        manager_session.setName(name);
        return manager_session;
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        Set<CarType> availableTypes = session.checkForAvailableCarTypes(start, end);
        System.out.print("The available car types are: ");
        for (CarType type:availableTypes){
            System.out.print(type.toString());
            System.out.print(",");
        }
        System.out.println();
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        session.setName(name);
        session.createQuote(new ReservationConstraints(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        session.setName(name);
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.numberOfReservationsByRenter(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.numberOfReservationsByCarType(carRentalName, carType);
    }

}
