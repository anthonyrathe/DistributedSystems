package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rental.Car;
import rental.CarRentalCompany;
import rental.CarRentalCompanyInterface;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;

public class Client extends AbstractTestBooking {
	
	/********
	 * MAIN *
	 ********/
	
	public static void main(String[] args) throws Exception {
		
		String carRentalCompanyName = "Hertz";
		
		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName);
		client.run();
	}
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	private CarRentalCompanyInterface rentalCompanyStub;
	
	public Client(String scriptFile, String carRentalCompanyName) {
		super(scriptFile);
		
		try {
			System.setSecurityManager(null);
            Registry registry = LocateRegistry.getRegistry(null);
            rentalCompanyStub = (CarRentalCompanyInterface) registry.lookup("CarRentalCompanyInterface");
        } catch (Exception e) {
            System.err.println("Exception on client side: " + e.toString());
            e.printStackTrace();
        }
	}
	
	/**
	 * Check which car types are available in the given period
	 * and print this list of car types.
	 *
	 * @param 	start
	 * 			start time of the period
	 * @param 	end
	 * 			end time of the period
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {

		rentalCompanyStub.getAvailableCarTypes(start, end);
		
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param	clientName 
	 * 			name of the client 
	 * @param 	start 
	 * 			start time for the quote
	 * @param 	end 
	 * 			end time for the quote
	 * @param 	carType 
	 * 			type of car to be reserved
	 * @param 	region
	 * 			region in which car must be available
	 * @return	the newly created quote
	 *  
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end,
			String carType, String region) throws Exception {
		
		return rentalCompanyStub.createQuote(new ReservationConstraints(start, end, carType, region), clientName);
		
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param 	quote 
	 * 			the quote to be confirmed
	 * @return	the final reservation of a car
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		
		return rentalCompanyStub.confirmQuote(quote);
		
	}
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param 	clientName
	 * 			name of the client
	 * @return	the list of reservations of the given client
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {

		List<Car> cars = rentalCompanyStub.getAllCars();
		List<Reservation> reservationsByRenter = new ArrayList<Reservation>();
		for (Car car : cars){
			for (Reservation reservation : car.getAllReservations()){
				if (reservation.getCarRenter().equals(clientName)){
					reservationsByRenter.add(reservation);
					System.out.println(reservation);
				}
			}
		}
		
		return reservationsByRenter;
		
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param 	carType 
	 * 			name of the car type
	 * @return 	number of reservations for the given car type
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {

		List<Car> cars = rentalCompanyStub.getAllCars();
		int numberOfReservations = 0;
		for (Car car : cars){
			if (car.getType().getName().equals(carType)){
				numberOfReservations += car.getAllReservations().size();
			}
		}
		
		return numberOfReservations;
	}
}