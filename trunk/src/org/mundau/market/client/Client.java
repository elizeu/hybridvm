package org.mundau.market.client;


import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mundau.market.event.RequestType;
import org.mundau.market.resource.Resource;
import org.mundau.market.util.Config;

/**
 * This class represents a client. The idea is to maintains its balance, 
 * allocation and so on.
 * 
 * @author elizeu
 *
 */
public class Client {
	
	/* A logger */
	protected static Logger logger;
	
	/* maintaind the id of each client */
	private static int clientId = 0;
	
	double balance;
	int id;
	Queue<Resource> allocatedResources;

	private double utility;

	private boolean preallocated;
	
	/**
	 * Default constructor for a client entity
	 *
	 */
	public Client(){
		
		logger = Logger.getLogger(Client.class);
		
		logger.setLevel((Level) Level.INFO);
		
		this.id = Client.clientId++;

		this.allocatedResources = new PriorityQueue<Resource>();
		
		this.balance = Config.getFundingRate();
		
		this.utility = 0.0;
		
		this.preallocated = false;
	}

	/**
	 * Determines if this client is idle 
	 * 
	 * @return true if the client has no active allocation, false otherwise.
	 */
	public boolean isIdle() {
		
		return ( this.allocatedResources.isEmpty() && ! this.preallocated );
	}

	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void addResource(Resource r) {
		
		this.allocatedResources.add(r);		
	}

	public void withdraw(double d) {
		this.balance -=  d;
		
		if ( this.balance < 0 ) {
			this.balance = 0;			
		}
	}
	
	public void deposit(double d) {
		this.balance += d; 
	}
	
	@Override
	public String toString(){
		return "Client-"+this.id;
	}

	public int getId() {
		return id;
	}

	/**
	 * This methode remove the resource from the collection of allocated resources. It is important to note that 
	 * the resource has also this client removed from its records (shares and price). 
	 *  
	 * @param resource Resource that must be removed from the collection
	 * @throws NoSuchElementException This exception will occurs if the parameter was not  previously allocated to this client.
	 */
	public void releaseResource(Resource resource) throws NoSuchElementException {

		if ( ! this.allocatedResources.remove(resource) ) {
			throw new NoSuchElementException("Resource ("+ resource +") is not being used by " + this);
		}
		
	}

	/**
	 * This method remove all resources currently allocated to this client from the list of allocated resources.
	 * Moreover, this client is also removed from the resources' records that keep track of shares and prices 
	 * 
	 * @throws NoSuchElementException
	 */
	public void releaseAllResources() throws NoSuchElementException {
		
		logger.debug(this + ": releasing all resources.");
						
		this.allocatedResources.clear();
		
		this.preallocated = false;
	}	
	
	/**
	 * Utility Function!!!
	 */
	public double getUtility(){				
		return this.utility;
	}
	 
	public void setUtility(double util) {
		this.utility = util;
	}

	public void preAllocate() {
		this.preallocated = true;
	}

	/**
	 * This method calculate the average resource share allocated to this client considering all resources
	 * used by her. A share is a proportional to the total resource capability. For example, if a client has a 
	 * resource reserved to her, the resource share in this case is `1`. In other case, if only 30% of the
	 * resource capability is reserved to the client the resource share is `0.3`. Thus, the resource share should be
	 * 0 <= share <= 1.
	 *
	 * @return the average resource share over all resources allocated to this client.
	 * 
	 * TODO: This method is returning values greater than 1. :-0
	 */
	public double getAverageShare() {
		
		double averageShare = 0.0;
		
		for ( Resource r : this.allocatedResources) {
				
			averageShare = averageShare + r.getClientShare(this);
				
		}
		
		if ( !isIdle() ) {
			averageShare = averageShare / this.allocatedResources.size();
		} 
		
		return averageShare;
	}

	/**
	 * Set the client utlity based on how long the client used the allocated resources and on the acquired share.
	 * 
	 * @param interval how long this client used the resources
	 */
	public void setUtility(long interval) {
		
		this.utility = this.allocatedResources.size() * interval * this.getAverageShare();
	}

	//TODO: Use a better exception 
	public RequestType getContractType() throws Exception {

		if ( this.allocatedResources.isEmpty() ) {
			throw new Exception();
		}
		
		return this.allocatedResources.element().getContractType();
	}
	 
}
