package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    private String name;
    private HashMap<Quote,CarRentalCompany> quotes;
    
    public CarRentalSession() {
    }
       
    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Quote createQuote(ReservationConstraints constraint) throws ReservationException {
        for (CarRentalCompany company: RentalStore.getRentals().values()){
            try {
                Quote quote = company.createQuote(constraint, this.getName());                
                quotes.put(quote, company);
                return quote;
            }catch (ReservationException e) {
            }
        }
        throw new ReservationException("No company can fullfill the constraints");
    }

    @Override
    public Set<Quote> getCurrentQuotes() {
        return this.quotes.keySet();
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        HashMap<Reservation, CarRentalCompany> reservations = new HashMap<Reservation, CarRentalCompany>();
        try {
            for (Quote quote: quotes.keySet()){
                CarRentalCompany company = quotes.get(quote);
                Reservation res = company.confirmQuote(quote);
                reservations.put(res, company);
            }
        } catch (ReservationException e){
            for (Reservation res: reservations.keySet()){
                reservations.get(res).cancelReservation(res);
            }
            throw new ReservationException("Failed confirming quote, cancelled all reservations");
        }
        return (List<Reservation>)reservations.keySet();
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableTypes = new HashSet<CarType>();
        for (CarRentalCompany company: RentalStore.getRentals().values()){
            availableTypes.addAll(company.getAvailableCarTypes(start, end));
        }
        return availableTypes;
    }

    
    
    
}
