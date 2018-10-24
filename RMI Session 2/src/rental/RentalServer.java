package rental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RentalServer {
	
	private List<String> companyNames;
	
	public RentalServer(List<String> names){
		this.companyNames = names;
	}
	
	public static void main(String[] args) throws Exception{
		List<String> companyNames = new ArrayList<String>();
		companyNames.add("Dockx");
		companyNames.add("Hertz");
		RentalServer server = new RentalServer(companyNames);
        server.run();
    }
	
	public void run() throws ReservationException, NumberFormatException, IOException {
		// Create ManagerSession for each company name
		// Make the ManagerSession register the companies
		for (String companyName : this.companyNames){
			ManagerSession session = getNewManagerSession("Toon & Anthony", companyName);
			session.registerCompany();
		}
		

	}
	
	public ManagerSession getNewManagerSession(String name, String companyName){
		return new ManagerSession(name, companyName);
	}

	

}
