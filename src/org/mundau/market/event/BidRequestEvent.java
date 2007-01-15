package org.mundau.market.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mundau.market.client.Client;
import org.mundau.market.engine.Engine;
import org.mundau.market.resource.Resource;
import org.mundau.market.util.Config;

/**
 * A bidding request 
 * 
 * @author elizeu
 *
 */
public class BidRequestEvent extends AbstractRequestEvent {
	
	public BidRequestEvent(){
		
		logger = Logger.getLogger(BidRequestEvent.class);	
		logger.setLevel((Level) Level.INFO);
				
		this.id = _id++;
		
		/* Default requirements */
		this.submissionTime = Engine.currentTick();
		this.latency = 0;
		this.numberOfNodes = 1;
		
		this.requestType = RequestType.AUCTION;
		
		this.changeStateToReady();
	}
	
	public BidRequestEvent(double _budget){
		this();
		this.budget = _budget;
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
			
					r.addClient(this.client, payment );
			
					this.client.addResource(r);
			
				}
				
				this.changeStateToRunning();
				
			} else {
				
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
				
				this.changeStateToDone();
				
				logger.debug(this.toString() + ": DONE.");
			}
		} 
		
	}
			
	public String toString(){
		return "BidRequest-"+this.id;
	}

	/**
	 * Test
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 *
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

		Properties prop = new Properties();
		
		prop.load(new FileInputStream(new File(Config.DEFAULT_CONFIG_FILE)));
		
		System.setProperties(prop);
		
		Client c = new Client();
		BidRequestEvent event = new BidRequestEvent(1);
		
		c.setBalance(1000);
		
		System.out.println("C = " + c.getBalance());
		
		event.setClient(c);
		
		c.deposit(13);
		
		System.out.println("C' = "+ event.getClient().getBalance());
		
	}
	
}
