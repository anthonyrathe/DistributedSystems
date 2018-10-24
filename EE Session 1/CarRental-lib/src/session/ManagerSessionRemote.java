/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Collection;
import java.util.HashMap;
import javax.ejb.Remote;
import rental.CarType;

/**
 *
 * @author r0596433
 */
@Remote
public interface ManagerSessionRemote {
    
    Collection<CarType> getCarTypes(String companyName);
    
    HashMap<CarType, Integer> numberOfReservations(String companyName);
    
    String getBestCustomer();
    
    int numberOfReservationsByRenter(String renter);
    
    int numberOfReservationsByCarType(String carRentalName, String carType);
        
    void setName(String name);
    
    String getName();
    
    
}
