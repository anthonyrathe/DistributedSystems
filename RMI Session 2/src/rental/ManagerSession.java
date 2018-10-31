package rental;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import rental.CarRentalCompany;
import rental.CarRentalCompanyInterface;

public class ManagerSession{
	
	private String name;
	private String companyName;
	private Set<String> rentalCompanyNames = new HashSet<String>();
	private Map<String, CarRentalCompanyInterface> rentalCompanies= new HashMap<String,CarRentalCompanyInterface>();
	
	public ManagerSession(String name, String companyName){
		this.name = name;
		this.companyName = companyName;
	}
	
	public String getManagerName(){
		return this.name;
	}
	
	public String getCompanyName(){
		return this.companyName;
	}
    
	public void registerCompany() throws NumberFormatException, ReservationException, IOException{
		String companyName = getCompanyName();
		CrcData data  = loadData("src/"+companyName.toLowerCase()+".csv");
		CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
		
		System.setSecurityManager(null);
		
		try {
			CarRentalCompanyInterface carRentalCompanyStub = (CarRentalCompanyInterface) UnicastRemoteObject.exportObject(company, 0);
		    Registry registry = LocateRegistry.getRegistry();
		    registry.bind(companyName, carRentalCompanyStub);
		} catch (Exception e) {
		    System.err.println("Cannot connect to RMI Registry: " + e.toString());
		    e.printStackTrace();
		}
		
	}
	
	public void unregisterCompany() throws Exception{
		System.setSecurityManager(null);
		
		try {Registry registry = LocateRegistry.getRegistry();
		    registry.unbind(companyName);
		} catch (Exception e) {
		    System.err.println("Cannot connect to RMI Registry: " + e.toString());
		    e.printStackTrace();
		}
		
		this.rentalCompanyNames = new HashSet<String>();
		this.rentalCompanies= new HashMap<String,CarRentalCompanyInterface>();
	}
	
    public Set<String> getAllRentalCompanies() throws RemoteException {
    	if (this.rentalCompanyNames.isEmpty()){
	    	Registry registry = LocateRegistry.getRegistry();
	    	this.rentalCompanyNames = new HashSet<String>(Arrays.asList(registry.list()));
    	}
    	return this.rentalCompanyNames;
    	
    }
    
    public Set<CarType> getAllCarTypes() throws RemoteException, NotBoundException {
    	Set<CarType> carTypes = new HashSet<CarType>();
    	for (String companyName : getAllRentalCompanies()){
	    	Collection<CarType> types = this.getCarTypes(companyName);
	    	carTypes.addAll(types);
    	}
    	return carTypes;
    	
    }
    
    private CarRentalCompanyInterface getCompany(String companyName) throws AccessException, RemoteException, NotBoundException{
    	if (!this.rentalCompanies.containsKey(companyName)){
    		System.setSecurityManager(null);
    		Registry registry = LocateRegistry.getRegistry(null);
            CarRentalCompanyInterface company = (CarRentalCompanyInterface) registry.lookup(companyName);
            this.rentalCompanies.put(companyName, company);
    	}
    	return this.rentalCompanies.get(companyName);
        
    }
    
    public Collection<CarType> getCarTypes(String companyName) throws RemoteException, NotBoundException{
        CarRentalCompanyInterface company = getCompany(companyName);
        return company.getAllCarTypes();
    }
    
    public HashMap<CarType, Integer> numberOfReservations(String companyName) throws RemoteException, NotBoundException{
        HashMap<CarType, Integer> numberOfReservations = new HashMap<CarType, Integer>();
        CarRentalCompanyInterface company = getCompany(companyName);
        for (CarType type : company.getAllCarTypes()){
            int number = 0;
            for (Car car :company.getAllCars()){
                number += car.getAllReservations().size();
            }
            
            numberOfReservations.put(type, number);
        }
        
        return numberOfReservations;
    }
    
    public List<String> getBestClients() throws AccessException, RemoteException, NotBoundException{
        HashMap<String, Integer> numberOfReservations = new HashMap<String, Integer>();
        
        for (String companyName : getAllRentalCompanies()){
        	CarRentalCompanyInterface company = getCompany(companyName);
            for (Car car :company.getAllCars()){
                for (Reservation res : car.getAllReservations()){
                    String renter = res.getCarRenter();
                    if (numberOfReservations.keySet().contains(renter)){
                        numberOfReservations.replace(renter, numberOfReservations.get(renter)+1);
                    }else{
                        numberOfReservations.put(renter, 1);
                    }
                }
            }
        }
        
        List<String> bestCustomers = new ArrayList<String>();
        int maxReservations = 0;
        for (String renter: numberOfReservations.keySet()){
            if (numberOfReservations.get(renter) > maxReservations || bestCustomers.isEmpty()){
                bestCustomers.clear();
                bestCustomers.add(renter);
                maxReservations = numberOfReservations.get(renter);
            }else if (numberOfReservations.get(renter) == maxReservations){
            	bestCustomers.add(renter);
            }
        }
        
        return bestCustomers;
    }
    
    public int numberOfReservationsByRenter(String renter) throws AccessException, RemoteException, NotBoundException {
        int numberOfReservations = 0;
        for (String companyName : getAllRentalCompanies()){
        	CarRentalCompanyInterface company = getCompany(companyName);
            numberOfReservations += company.getNumberOfReservationsBy(renter);
        }
        return numberOfReservations;
    }

    public int numberOfReservationsByCarType(String carRentalName, String carType) throws RemoteException, NotBoundException {
        CarRentalCompanyInterface company = getCompany(this.companyName);
        CarType type = company.getCarType(carType);
        int numberOfReservations = 0;
        for (Car car: company.getAllCars()){
            if (car.getType().equals(type)){
                numberOfReservations += car.getAllReservations().size();
            }
        }
        return numberOfReservations;
    }
    
    public static CrcData loadData(String datafile) throws ReservationException, NumberFormatException, IOException {
    	
		CrcData out = new CrcData();
		int nextuid = 0;
		
		// open file
		BufferedReader in = new BufferedReader(new FileReader(datafile));
		StringTokenizer csvReader;
		
		try {
			// while next line exists
			while (in.ready()) {
				String line = in.readLine();
				
				if (line.startsWith("#")) {
					// comment -> skip					
				} else if (line.startsWith("-")) {
					csvReader = new StringTokenizer(line.substring(1), ",");
					out.name = csvReader.nextToken();
					out.regions = Arrays.asList(csvReader.nextToken().split(":"));
				} else {
					// tokenize on ,
					csvReader = new StringTokenizer(line, ",");
					// create new car type from first 5 fields
					CarType type = new CarType(csvReader.nextToken(),
							Integer.parseInt(csvReader.nextToken()),
							Float.parseFloat(csvReader.nextToken()),
							Double.parseDouble(csvReader.nextToken()),
							Boolean.parseBoolean(csvReader.nextToken()));
					System.out.println(type);
					// create N new cars with given type, where N is the 5th field
					for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
						out.cars.add(new Car(nextuid++, type));
					}
				}
			}
		} finally {
			in.close();
		}
		
		return out;
	}
	
	static class CrcData {
		public List<Car> cars = new LinkedList<Car>();
		public String name;
		public List<String> regions =  new LinkedList<String>();
	}

	public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws AccessException, RemoteException, NotBoundException {
		CarRentalCompanyInterface company = getCompany(carRentalCompanyName);
		
		HashMap<String, Integer> numberOfReservations = new HashMap<String, Integer>();
		Calendar calendar = new GregorianCalendar();
        
		for(Car car : company.getAllCars()) {
            for(Reservation reservation : car.getAllReservations()) {
            	calendar.setTime(reservation.getStartDate());
            	int reservationYear = calendar.get(Calendar.YEAR);
            	if (reservationYear == year){
            		String type = reservation.getCarType();
                    if (numberOfReservations.keySet().contains(type)){
                        numberOfReservations.replace(type, numberOfReservations.get(type)+1);
                    }else{
                        numberOfReservations.put(type, 1);
                    }
            	}
            	
            }
        }
		
		String mostPopularCarType = null;
		int highestNumberOfReservations = 0;
		for (String type : numberOfReservations.keySet()){
			if (mostPopularCarType == null || numberOfReservations.get(type) > highestNumberOfReservations){
				highestNumberOfReservations = numberOfReservations.get(type);
				mostPopularCarType = type;
			}
		}
		
		return company.getCarType(mostPopularCarType);
		
	}


    
}
