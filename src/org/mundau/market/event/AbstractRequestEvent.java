package org.mundau.market.event;


import java.util.PriorityQueue;

import org.apache.log4j.Logger;
import org.mundau.market.client.Client;
import org.mundau.market.engine.Engine;
import org.mundau.market.resource.Resource;
import org.mundau.market.resource.ResourcePool;

/**
 * This is an abstract class that represents an event that
 *  must be processed on a specific simulation time. 
 *  
 * @author elizeu 
 *
 */
public abstract class AbstractRequestEvent implements Event, Comparable<AbstractRequestEvent> {
	
	/* a logger */
	protected static Logger logger;
	
	/* Just a counter */
	protected static int _id;
	
	/* Attributes */
	protected int id;
	protected Client client;
	protected int deadline;
	protected int submissionTime;
	protected int howLong;
	protected int latency;
	protected int numberOfNodes;
	protected double budget;
	
	/* Resource Pool that this event will act over */
	protected ResourcePool resourcePool;
	protected PriorityQueue<Resource> resources;
	
	protected EventState state;
	protected RequestType requestType;

	// ?????
	protected int time;
	
	/* Abstract methods */	
	public abstract void process();

	
	public boolean checkPreConditions() {
						
		if ( this.client.getBalance() <= 0 ) {
			logger.debug(this.toString() +": Client has insufficient balance: "+ this.client.getBalance() );			
			return false;			
		}
		
		if ( (this.latency+this.submissionTime) < Engine.currentTick() ) {
			logger.debug(this.toString() +": Maximum latency has expired: " + (this.latency+this.submissionTime) + " < " + Engine.currentTick() );
			return false;
		}
						
		try {

			//System.err.println( this.resourcePool.getSize() + " ~ " + this.numberOfNodes);
						
			this.resources = this.resourcePool.getCheapestIdleResources(this.numberOfNodes);										
			
		} catch (IndexOutOfBoundsException e) {
			logger.warn(e.getMessage());
			return false;
		} 	
		
		
		if ( resources.size() == 0 ) {
			
			logger.debug(this.toString() +": There is not enough resources idle.");
			
			return false;
			
		} 
		
		return true;
	}
	
		
	public int getDueTime() {
		return this.time;
	}
	
	public void setDueTime(int t){
		this.time = t;
		logger.debug("Event-"+ this.id + ": due time = " + t );
	}
	
	public int compareTo(AbstractRequestEvent e){
		return (this.time - e.getDueTime());
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int _numberOfNodes) {		
		this.numberOfNodes = _numberOfNodes;
		logger.debug("Event-"+ this.id + ": " + _numberOfNodes + " nodes.");
	}

	public int getHowLong() {
		return howLong;
	}

	public void setHowLong(int _size) {
		this.howLong = _size;
		this.deadline = _size;
		logger.debug("Event-"+ this.id + ": " + _size + " ticks.");
	}

	public void setClient(Client _idleClient) {
		this.client = _idleClient;
		logger.debug("Event-"+ this.id + ": " + _idleClient + " ");
	}

	public Client getClient() {
		return this.client;
		
	}

	public EventState getState() {
		return this.state;
	}

	public void setState(EventState state) {
		this.state = state;
	}

	public int getId() {
		return this.id;
	}	
	
	public RequestType getType(){
		return this.requestType;
	}
	
	public void setResourcePool(ResourcePool resourcePool) {
		this.resourcePool = resourcePool;
	}
	
	protected void changeStateToRunning() {
		this.setState(EventState.RUNNING);		
	}

	/**
	 * Perform the necessary changes to the allocated resources and change the Request status to DONE!
	 *
	 */
	protected void changeStateToDone() {				
		
		/* Relase all reasources allocated to this client */
		this.client.releaseAllResources();

		/* If so, add the client to this resource */
		for (Resource r : this.resources) {
				
			r.removeClient(this.client);			
		}				
		
		this.setState(EventState.DONE);
	}

	protected void changeStateToReady() {
		this.setState(EventState.READY);		
	}

	protected void changeStateToFailed() {
				
		/* Relase all reasources allocated to this client */
		this.client.releaseAllResources();				
		
		this.client.setUtility(0.0);
		
		this.setState(EventState.FAILED);		
	}
	
	public int getLatency() {
		return latency;
	}
	
	public void setLatency(int latency) {
		this.latency = latency;
	}
	
	
}

