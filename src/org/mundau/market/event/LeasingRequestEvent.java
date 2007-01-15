package org.mundau.market.event;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mundau.market.engine.Engine;
import org.mundau.market.resource.Resource;

/**
 * This class represents a concrete event which is a leasing request for resources.
 * 
 * @author elizeu
 *
 */
public class LeasingRequestEvent extends AbstractRequestEvent {
	 
	public LeasingRequestEvent () {
		
		logger = Logger.getLogger(LeasingRequestEvent.class);	
		logger.setLevel((Level) Level.INFO);
				
		this.id = _id++;
		
		/* Default requirements */
		this.submissionTime = Engine.currentTick();
		this.latency = 0;
		this.numberOfNodes = 1;
		
		this.requestType = RequestType.LEASING;
		
		this.changeStateToReady();		
	}
	

	@Override
	public void process() {
		
		if ( this.state.isReady() ) {

			logger.debug("Checking "+ this.toString() + " preconditions.");						
			
			if ( this.checkPreConditions() ) {				
				
				logger.debug(this.toString() + " preconditions: OK");				
									
				double payment = this.client.getBalance() / resources.size();
			
				/* If so, add the client to this resource */
				for (Resource r : this.resources) {
			
					this.client.withdraw( payment );
			
					//System.err.println(r + "\t" + this.client);
					
					r.addClient(this.client, payment );
			
					this.client.addResource(r);			
				}
				
				this.changeStateToRunning();
				
			} else {
				
				//System.err.println("DOES IT EVER FAIL ????");
				
				/* set the utility for a failed request */
				this.client.setUtility( 0.0 );
				
				this.changeStateToFailed();
			}
			
		} else if ( this.state.isRunning() ) {
			
			/* Minus one tick on the deadline */
			this.deadline--;
			
			/* if deadline is  0 change state to Done */
			if ( this.deadline == 0 ) {				
		
				/* set the utility for a failed request */
				this.client.setUtility( this.howLong );
				
				//System.err.println("TEM um client!"+ howLong);
								
				this.changeStateToDone();
				
				logger.debug(this.toString() + ": DONE.");
			}
		} 

	}

}
