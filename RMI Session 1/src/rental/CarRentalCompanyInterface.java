package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CarRentalCompanyInterface extends Remote{
	
	String getName() throws RemoteException;
	List<String> getRegions() throws RemoteException;
	
	boolean hasRegion(String region) throws RemoteException;
	Collection<CarType> getAllCarTypes() throws RemoteException;
	CarType getCarType(String carTypeName) throws RemoteException;
	List<Car> getAllCars() throws RemoteException;
	
	boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	Quote createQuote(ReservationConstraints constraints, String client) throws RemoteException, ReservationException;
	Reservation confirmQuote(Quote quote) throws RemoteException, ReservationException;
	void cancelReservation(Reservation res) throws RemoteException;
	
	//@Override
	//String toString();
	
}
