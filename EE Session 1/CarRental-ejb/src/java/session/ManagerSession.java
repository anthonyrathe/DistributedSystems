/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateful;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

/**
 *
 * @author r0596433
 */
@Stateful
public class ManagerSession implements ManagerSessionRemote {
    
    String name;
    
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }
    
    @Override
    public Collection<CarType> getCarTypes(String companyName){
        CarRentalCompany company = RentalStore.getRentals().get(companyName);
        return company.getCarTypes();
    }
    
    @Override
    public HashMap<CarType, Integer> numberOfReservations(String companyName){
        HashMap<CarType, Integer> numberOfReservations = new HashMap<CarType, Integer>();
        CarRentalCompany company = RentalStore.getRentals().get(companyName);
        for (CarType type : company.getCarTypes()){
            int number = 0;
            for (Car car :company.getCars()){
                number += car.getAllReservations().size();
            }
            
            numberOfReservations.put(type, number);
        }
        
        return numberOfReservations;
    }
    
    @Override
    public String getBestCustomer(){
        HashMap<String, Integer> numberOfReservations = new HashMap<String, Integer>();
        
        for (CarRentalCompany company : RentalStore.getRentals().values()){
            for (Car car :company.getCars()){
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
        
        String bestCustomer = null;
        int maxReservations = 0;
        for (String renter: numberOfReservations.keySet()){
            if (numberOfReservations.get(renter) > maxReservations){
                bestCustomer = renter;
                maxReservations = numberOfReservations.get(renter);
            }
        }
        
        return bestCustomer;
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
    public int numberOfReservationsByRenter(String renter) {
        int numberOfReservations = 0;
        for (CarRentalCompany company : RentalStore.getRentals().values()){
            numberOfReservations += company.getReservationsBy(renter).size();
        }
        return numberOfReservations;
    }

    @Override
    public int numberOfReservationsByCarType(String carRentalName, String carType) {
        CarRentalCompany company = RentalStore.getRentals().get(carRentalName);
        CarType type = company.getType(carType);
        int numberOfReservations = 0;
        for (Car car: company.getCars()){
            if (car.getType().equals(type)){
                numberOfReservations += car.getAllReservations().size();
            }
        }
        return numberOfReservations;
    }

    
}
