package org.mundau.market.resource;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.mundau.market.client.Client;
import org.mundau.market.event.RequestType;
import org.mundau.market.util.Config;

/**
 * This class represents a resource. Moreover, it keeps track the price and availability of a particular resource.
 * Yet, the clients who are using the resource, their respective shares, payments, and deadlines. 
 * 
 * @author elizeu
 *
 */
public class Resource implements Comparable<Resource> { 
		
	private static int resourceId = 0;
	
	int id;
	double price = 0.0;
	List<Client> clients; 
	RequestType contractType;
	Map<Client,Double> shares;
	Map<Client,Double> payments;
	
	int allocationStart;
	int allocationEnd;
		
	public Resource () { 
				
		this.id = Resource.newId();
		
		this.clients = new LinkedList<Client>();
		
		this.shares = new HashMap<Client, Double>();
		this.payments = new HashMap<Client, Double>();
		
		this.contractType = RequestType.LEASING;
		
		this.price = Config.getInitialPrice();
	}

	public Resource(double _price, RequestType _newContract){
		
		this();

		this.contractType = _newContract;
			
		this.price = _price;
		
		if ( this.contractType.isLeasing()) {
			this.price = Config.getInitialPrice()  * Config.getPriceFactor();			
		} 	
	}
	
	private void updatePrice(double _price){
		
		if ( (this.clients.size() == 1) || this.contractType.isLeasing() ) {
			
				this.price =  _price;
			
		} else if ( this.contractType.isAuction() ) {
			
			this.price = this.price + _price;
			
		} 
		
	}
	
	private void updateShares(Client _client, double _payment) {
	 
		this.shares.put(_client, new Double(_payment / this.price ));
		
		for (Client c :  this.shares.keySet() ) {
			
			this.shares.put(c, new Double(_payment / this.price ));
			
		}			
	}	
	
	public double getPrice(){
		return this.price;
	}
	
	/**
	 * Add a new client, update shares and price.
	 * 
	 * @param newClient the new client that will use this resource
	 * @param payment the amout of credits this client is paying
	 */
	public void addClient(Client newClient, double _payment){
				
		if ( this.contractType.isLeasing()  ) {

			//System.err.print("Leasing type!"); 
			
			/* Just make sure that there is no clients here */
			this.clients.clear();
			this.payments.clear();
			this.shares.clear();
			
		}
		
		//System.err.println(newClient); //TODO STOPPED HERE
		this.clients.add(newClient);
		
		this.payments.put(newClient, _payment);
		
		this.updatePrice( _payment ); 
		
		this.updateShares(newClient, _payment);		
	}

	public void setAllocationLimits(int start, int end){
	
		/* set the start and end of allocation */
		if ( this.contractType.isLeasing() ) {
			this.allocationStart = start;
			this.allocationEnd = end;
		} else {
			this.allocationStart = 0;
			this.allocationEnd = 0;			
		}
		
	}
	
	/**
	 * Generate a new id for this resource 
	 * 
	 * @return a new ID
	 */
	private static int newId() {

		return resourceId++;
	}

	/**
	 * Determine if this resource is not being used by any client or 
	 * if it is being allocated under AUCTION contracts (so multiple clients can 
	 * be using this resource at the same time)
	 * 
	 * @return true if there is no client or the contract is AUCTION, false otherwise.
	 */
	public boolean isIdle() {
		return ( this.clients.isEmpty() || (this.contractType.isAuction()) );
	}

	/**
	 * Determines if this client is using this resource 
	 * @param _client
	 * @return
	 */
	public boolean isClientUsingIt(Client _client ){
		
		return this.clients.contains(_client);
		
	}
	
	/**
	 * Determines the percentage of this resource has been allocated to the client.
	 * 
	 * @return a value in the interval [1,0] which represents the resource share used by the client.
	 */
	public double getClientShare(Client _client){
		
		if (this.isClientUsingIt(_client)) {
			return this.shares.get(_client);
		} 
		
		return 0.0;
	}

	public void removeClient(Client client) throws NoSuchElementException {
		
		if ( ! this.clients.remove(client) ) {
			throw new NoSuchElementException("Client ("+client+") is not using this resource");
		}
		
		/* Update shares */
		if ( this.contractType.isAuction() ) {

			this.price -= this.payments.get(client);
						
			for (Client c : this.clients) {
				
				this.shares.put(c, (this.payments.get(c) / this.price ) );
			}
			
			/* Remove the client from shares record */
			this.shares.remove(client);

			/* Finally removes the client from payments */
			this.payments.remove(client);
						
		} else {

			System.err.println("NOT AUCTION!");			
		}
		
	}

	public int compareTo(Resource o) {

		double residue = this.price - o.getPrice();
		
		if ( residue > 0 ) {
			return 1;
		}
		
		if ( residue < 0 ) {
			return -1;
		}
		
		return 0;
	}

	@Override
	public String toString(){
		return "Resource-"+this.id+"("+this.contractType+")";
	}

	public RequestType getContractType() {
		return this.contractType;
	}
	
}
