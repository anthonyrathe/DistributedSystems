package rental;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReservationSession implements Serializable{

	private static final long serialVersionUID = 6350765299502890039L;
	
	private String name;
    private HashMap<Quote,CarRentalCompanyInterface> quotes = new HashMap<Quote,CarRentalCompanyInterface>();
	private Set<String> rentalCompanyNames = new HashSet<String>();
	private Map<String, CarRentalCompanyInterface> rentalCompanies= new HashMap<String,CarRentalCompanyInterface>();
    
	
	public ReservationSession(String name){
		this.name = name;
	}
       
	public Set<String> getAllRentalCompanies() throws RemoteException {
    	if (this.rentalCompanyNames.isEmpty()){
	    	Registry registry = LocateRegistry.getRegistry();
	    	this.rentalCompanyNames = new HashSet<String>(Arrays.asList(registry.list()));
    	}
    	return this.rentalCompanyNames;
    	
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

    public Quote createQuote(ReservationConstraints constraint, String name) throws ReservationException, AccessException, RemoteException, NotBoundException {
    	for (String companyName : getAllRentalCompanies()){
        	CarRentalCompanyInterface company = getCompany(companyName);
            try {
                Quote quote = company.createQuote(constraint, name);
                this.quotes.put(quote, company);
                return quote;
            }catch (ReservationException e) {}
        }
        throw new ReservationException("No company can fullfill the constraints");
    }

    public Set<Quote> getCurrentQuotes() {
        return this.quotes.keySet();
    }

    public List<Reservation> confirmQuotes(String name) throws ReservationException, RemoteException {
        HashMap<Reservation, CarRentalCompanyInterface> reservations = new HashMap<Reservation, CarRentalCompanyInterface>();
        List<Quote> confirmedQuotes = new ArrayList<Quote>();
        try{
            for (Quote quote: quotes.keySet()){
                if (quote.getCarRenter().equals(name)){
                    CarRentalCompanyInterface company = quotes.get(quote);
                    Reservation res = company.confirmQuote(quote);
                    reservations.put(res, company);
                    confirmedQuotes.add(quote);
                }
            }
        } catch (ReservationException e){
            for (Reservation res: reservations.keySet()){
                reservations.get(res).cancelReservation(res);
            }
            for (Quote quote: confirmedQuotes){
                quotes.remove(quote);
            }            
            throw new ReservationException("Failed confirming quote, cancelled all reservations");
        }
        for (Quote quote: confirmedQuotes){
            quotes.remove(quote);
        } 
        
        
        return new ArrayList<Reservation>(reservations.keySet());
    }
    
    public String getName(){
        return this.name;
    }

    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws AccessException, RemoteException, NotBoundException {
        Set<CarType> availableTypes = new HashSet<CarType>();
        for (String companyName : getAllRentalCompanies()){
        	CarRentalCompanyInterface company = getCompany(companyName);
            availableTypes.addAll(company.getAvailableCarTypes(start, end));
        }
        return availableTypes;
    }

	public CarType getCheapestCarType(Date start, Date end, String region) throws AccessException, RemoteException, NotBoundException {
		CarType cheapestType = null;
		for (String companyName : getAllRentalCompanies()){
        	CarRentalCompanyInterface company = getCompany(companyName);
        	if (company.hasRegion(region)){
        		for (CarType type : company.getAvailableCarTypes(start, end)){
        			if (cheapestType == null || type.getRentalPricePerDay() < cheapestType.getRentalPricePerDay()){
        				cheapestType = type;
        			}
        		}
        	}
		}
		return cheapestType;
	}

    
    
    
}
