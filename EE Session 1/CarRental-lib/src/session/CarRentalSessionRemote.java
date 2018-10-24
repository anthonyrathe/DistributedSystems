package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {
    
    // Returns all registered car rental companies.
    Set<String> getAllRentalCompanies();
    
    // Tries to make a quote for a given constraint.
    Quote createQuote(ReservationConstraints constraint) throws ReservationException;
    
    // Returns all current quotes in the session.
    Set<Quote> getCurrentQuotes();
    
    // 
    Set<CarType> checkForAvailableCarTypes(Date start, Date end);
    
    // Effectively ties these quotes to a car and updates the reservations corresponding to that car.
    List<Reservation> confirmQuotes() throws ReservationException;
    
    void setName(String name);
    
    String getName();
    
    
}
