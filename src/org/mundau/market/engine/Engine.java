package org.mundau.market.engine;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mundau.market.client.Client;
import org.mundau.market.event.AbstractRequestEvent;
import org.mundau.market.event.EventBuilder;
import org.mundau.market.event.RequestType;
import org.mundau.market.resource.ResourcePool;
import org.mundau.market.resource.Resource;
import org.mundau.market.statistics.Poisson;
import org.mundau.market.statistics.Statistics_obj;
import org.mundau.market.util.Config;


/**
 * 
 * This is the bootstrap class of the simulator 
 * 
 * @author elizeu
 *
 */
public class Engine {

	/* Singleton instance */
	private static Engine engine = null;
	
	/* Keep track of Statistics */
	Statistics_obj stat;
	
	/* Attributes  */
	protected int numberOfClients;
	protected int numberOfResources;
	protected double fundingRate;
	protected int duration;
	
	protected int failedRequests;
	protected int successfulRequests;
	protected int leasingSuccessfulRequests;
	protected int leasingFailedRequests;
	protected int auctionSuccessfulRequests;
	protected int auctionFailedRequests;

	
	protected static int tick;

	/* Distributions */
	protected Random randomNumber;	
	protected Poisson requests;
	protected Poisson jobSizes;
	protected Poisson jobRunTime;
	protected Poisson latencies;
	
	/* Contains a queue of events to be processed */
	protected PriorityQueue<AbstractRequestEvent> eventQueue;
	
	/* Score board - clients x resources */
	List<Client> clients;
	
	/* Resources to be scheduled */
	//TODO: to simulate several providers, I think we only need to have a collection of resource pools
	ResourcePool auctionPool;
	ResourcePool leasingPool;
	
	/* Logger */
	protected static Logger logger;
		
	/**
	 * 
	 * Default constructor
	 * 
	 * @param properties path of a properties file with simulation parameters 
	 */
	protected Engine(String properties) {
		
		logger = Logger.getLogger(Engine.class);
		
		BasicConfigurator.configure();
		
		logger.setLevel((Level) Level.INFO);
				
		logger.info("Loading properties from: "+ properties);
			
		Config.loadProperties(properties);

		logger.info("Properties loaded: ");
			
		logger.info("Initalizing random number generator: seed = "+ Config.getSeed());		
		
		/* Init Random Number Generator */
		this.randomNumber = new Random(Config.getSeed());

		/* Distributions */		
		this.requests = new Poisson(Config.getSeed());
		this.jobSizes = new Poisson(Config.getSeed());
		this.jobRunTime = new Poisson (Config.getSeed());
		this.latencies = new Poisson(Config.getSeed());
		
		/* Configure simulation */
		this.numberOfClients = Config.getNumberOfClients();
		this.numberOfResources = Config.getNumberOfResources();
		this.fundingRate = Config.getFundingRate();
		
		this.duration = Config.getSimulationTime(); 
		logger.info("Initalizing event queue: EMPTY");
		
		/* Initialize the EventQueue */
		eventQueue = new PriorityQueue<AbstractRequestEvent>();
		
		createResources();
		
		createClients();
	}
	
	/**
	 * This is a
	 * @return
	 */
	public static Engine getInstance(String properties){
				
		if ( engine == null ) {
			engine = new Engine(properties);			
		}
		
		return engine;
	}

	public static Engine getInstance(){
		
		if ( engine == null ) {
			engine = new Engine(Config.DEFAULT_CONFIG_FILE);			
		}
		
		return engine;
	}
	
	
	private void createClients() {
		this.clients = new LinkedList<Client>();
		
		int i;
		
		logger.debug("Initalizing"+ this.numberOfClients + " clients");
		
		for (i=0; i < this.numberOfClients; i++) {
			this.clients.add(new Client());
		}
		
		logger.debug("Clients created.");

	}

	private void createResources() {
		
		int underLeasingResources, underAuctionResources;
		
		//TODO: this may be defined in the configuration properties
		/* Calc the percentage of resources under each contract */
		underLeasingResources = underAuctionResources = this.numberOfResources / 2;
		
		this.auctionPool = new ResourcePool(underAuctionResources, RequestType.AUCTION);
				
		this.leasingPool = new ResourcePool(underLeasingResources, RequestType.LEASING);
		
	}

	/**
	 * Engine main loop.
	 *
	 */
	public void mainLoop() {
		
		/* request index */
		int r = 0;
		
		/* Pick the number of requests */
		int numberOfRequests, numberOfLeasingRequests, numberOfAuctionRequests;		
								
		/* a counter for available clients */
		int numberOfIdleClients;
		
		Engine.tick = 1;
		
		logger.info("Engine started.");
		
		/* main loop */
		while ( Engine.tick <= this.duration ) {
			
			logger.info("Tick "+ Engine.tick);
			
			/* reset the request index & counters */
			r = 0;
			numberOfLeasingRequests = 0;
			numberOfAuctionRequests = 0;
			failedRequests = 0;
			successfulRequests = 0;						
			
			/* Pick the number of request - Poisson distribution */
			//numberOfRequests = 10;			
			numberOfRequests = requests.nextPoisson(Config.getAverageRequestRate());
			
			logger.debug("Number of requests: "+ numberOfRequests);
			
			/* Determine the number of available clients */
			numberOfIdleClients = this.getIdleClients().size();
			
			//logger.info("Available clients: "+ numberOfIdleClients);
			
			//if ( numberOfRequests > numberOfIdleClients ) {				
				//numberOfRequests = 	numberOfIdleClients;
				//logger.warn("Number of requests exceeded the number of available clients [fixing it].");
			//}
			
			//TODO: Why does this number of idle clients only decrease?
			
			for ( r = 0; (r < numberOfRequests) && (numberOfIdleClients > 0) ; r++) {
								
				/* Pick one available client */
				Client idleClient = getRandomIdleClient();
				
				/* new event to be processed */
				AbstractRequestEvent newEvent; 			
				
				/* Create request [factory method] */
					/* Pick one type of request */

				/* We are assuming a uniform distribution of request types */
				//TODO: implement this in a more flexible way (we should be able to vary according to a distribution)
				if ( this.randomNumber.nextBoolean()  ) {
					
					newEvent = EventBuilder.createEvent(RequestType.AUCTION);
					newEvent.setResourcePool(this.auctionPool);
					
					newEvent.setLatency(latencies.nextPoisson( Config.getAuctionLatencyTolerance() ) );					
					
					numberOfAuctionRequests++;
					
				} else {
					
					newEvent = EventBuilder.createEvent(RequestType.LEASING);
					newEvent.setResourcePool(this.leasingPool);
					
					newEvent.setLatency(latencies.nextPoisson( Config.getLeasingLatencyTolerance() ) );					
					
					numberOfLeasingRequests++;
				}
				
				/* Pick # of nodes */		
				int jobSize = this.jobSizes.nextInt( Config.getAverageJobSize() );
				newEvent.setNumberOfNodes( jobSize );
				logger.debug("Job Size (# Nodes): "+  jobSize );					

				/* Define request size */
				int runtime = jobRunTime.nextPoisson( Config.getAverageJobRunTime() + 1);
				newEvent.setHowLong( runtime );
				logger.debug("Job Runtime (# ticks): "+  runtime );	
								
				/* Set the client */
				newEvent.setClient(idleClient);
						
				/* end */
								
				/* Enqueue the request */
				this.eventQueue.add(newEvent);
			
				/* Since one event was created, a client became busy */
				numberOfIdleClients--;
			}
			
			/* Process one tick of each event in the queue */
			this.processEvents();
			
			/* There will only have new events if there are idle clients to be associated to them */ 
			numberOfIdleClients = this.getIdleClients().size();
			
			if ( successfulRequests != 0 || failedRequests != 0 ) {
				
				/* Print the summary of the turn */
				logger.info("NumberOfRequests: "+ numberOfRequests);
				logger.info("NumberOfSuccessfulRequests: "+ successfulRequests);
				logger.info("NumberOfFailedRequests: "+ failedRequests);
				
				logger.info("NumberOfLeasingRequests: "+ numberOfLeasingRequests);
				logger.info("NumberOfLeasingSuccessfulRequests: "+ leasingSuccessfulRequests);
				logger.info("NumberOfLeasingFailedRequests: "+ leasingFailedRequests);
				
				logger.info("NumberOfAuctionRequests: "+ numberOfAuctionRequests);
				logger.info("NumberOfAuctionSuccessfulRequests: "+ auctionSuccessfulRequests);
				logger.info("NumberOfAuctionFailedRequests: "+ auctionFailedRequests);
				
				logger.info("IdleClients: "+ numberOfIdleClients );
				
				logger.info("IdleResources: ");
				logger.info("LeasingIdleResources: ");
				logger.info("AuctionIdleResources: ");
				
				logger.info("AverageShare: "+ this.getAverageShare());
				logger.info("LeasingAverageShare: "+ this.getAverageShare( RequestType.LEASING));			
				logger.info("AuctionAverageShare: "+ this.getAverageShare( RequestType.AUCTION)  );
				
				logger.info("AveragePrice: " + this.getAveragePrice() );
				logger.info("LeasingAveragePrice: "+ this.getAveragePrice(RequestType.LEASING) );
				logger.info("AuctionAveragePrice: "+ this.getAveragePrice(RequestType.AUCTION)   );
				
				logger.info("AverageJobSize: " );
				
				logger.info("FailedRequests: ");
				logger.info("InsufficientFunds: ");
				logger.info("UnavailableResources: ");
	
				logger.info("AverageClientUtility: "+ this.getAverageClientUtility() );
				logger.info("AverageLeasingClientUtility: "+ this.getAverageClientUtility(RequestType.LEASING));
				logger.info("AverageAuctionClientUtility: "+ this.getAverageClientUtility(RequestType.AUCTION));
				
				logger.info("AverageProviderProfit: ");
				logger.info("AverageLeasingProviderProfit: ");
				logger.info("AverageAuctionProviderProfit: ");
			}
			
			/* One more tick! Time is passing... */
			Engine.tick =  Engine.tick + 1;			
		}
		
		logger.info("Simulation DONE!!");
	}


	private double getAverageClientUtility(RequestType type) {

		double averageUtility = 0.0;
		int size = this.clients.size();
		
		for ( Client c : this.clients ) {
		
			try {
				if ( c.getContractType() == type ) {
					averageUtility += c.getUtility(); 
				}
			} catch (Exception e) {
				logger.debug("Client is idle");
			}
		}
		
		return averageUtility / size;		
	}

	private double getAveragePrice(RequestType type) {

		double averagePrice = 0.0;
		int size = 0;
		
		
		if ( type.isAuction() ) {
		
			size = this.auctionPool.getAllResources().size();		

			for (Resource r : this.auctionPool.getAllResources() ) {
			
				averagePrice += r.getPrice();
			}
		} else if ( type.isLeasing() ) {

			size = this.leasingPool.getAllResources().size();		

			for (Resource r : this.leasingPool.getAllResources() ) {
			
				averagePrice += r.getPrice();
			}						
		}
						
		return averagePrice / size;
	}

	//TODO: refactor all these getAverage* methods to have one getAverage(Collection c) method which determine the average of anything.
	private double getAverageClientUtility() {
		
		double averageUtility = 0.0;
		int size = this.clients.size();
		
		for ( Client c : this.clients ) {
			averageUtility += c.getUtility(); 
		}
		
		return averageUtility / size;
	}

	protected double getAveragePrice() {

		double averagePrice = this.getAveragePrice(RequestType.AUCTION) + this.getAveragePrice(RequestType.LEASING);
		int size = 2;
		
		return averagePrice / size;
	}

	protected double getAverageShare() {

		double averageShare = 0.0;
		int numberOfBusyClients = 0;
		
		for (Client c : this.clients ) {
			
			if ( ! c.isIdle() ) {
				averageShare = averageShare + c.getAverageShare();
				numberOfBusyClients++;
			}
		}		
		
		if ( numberOfBusyClients != 0 ) {
			return (averageShare / numberOfBusyClients);
		} else {
			return 0.0;
		}
	}

	protected double getAverageShare(RequestType type) {

		double averageShare = 0.0;
		int numberOfBusyClients = 0;
		
		for (Client c : this.clients ) {
		
			try {
				if ( ! c.isIdle() ) {					
					if ( c.getContractType() == type ) {
						averageShare = averageShare + c.getAverageShare();
						numberOfBusyClients++;
					}
					
				}
			} catch ( Exception e ) {
				//NOthing to do, the resource is not idle 
			}
		}		
		
		if ( numberOfBusyClients != 0 ) {
			return (averageShare / numberOfBusyClients);
		} else {
			return 0.0;
		}
	}	
	
	
	/**
	 * Finds an idle client  
	 * 
	 * @return a randomly chosen idle client
	 */
	private Client getRandomIdleClient() {
		
		//Random random = new Random();
		List<Client> idleClients;
		
		idleClients = this.getIdleClients();
				
		if ( ! idleClients.isEmpty() ) {
			
			int index = this.randomNumber.nextInt(idleClients.size());
			
			logger.debug("Client found: Client-"+ index);
			
			idleClients.get(index).preAllocate();
			
			return idleClients.get(index);
		}
		
		return null;
	}

	private List<Client> getIdleClients() {		
		
		List<Client> idleClients = new LinkedList<Client>();
	
		for (Client c : this.clients ) {
			
			if ( c.isIdle() ) {
				idleClients.add(c);
			}
		}
	
		logger.debug("Idle clients found: " + idleClients.size());
		
		return idleClients;
	}

	/**
	 * Check the event queue and process the events due at the time tick
	 * 
	 * @param tick time tick used to select events to be processed.
	 *
	 */
	public void processEvents() {
				
		logger.info("Start processing events...");
		
		logger.info( this.eventQueue.size() + " events in the queue.");
		
		for (AbstractRequestEvent event : this.eventQueue ) {
				
			event.process();			
		}
		
		this.updateEventQueue();
	}

	/**
	 * Simply check what happened with the events and update the queue accordingly.
	 * For example, if the event was a request for leasing resources, the event will 
	 * be enqueued again to be processed in the future. 
	 *
	 */
	private void updateEventQueue() {
				
		boolean shallDelete;
		
		/* A temporary place to store those removeable events */
		Collection<AbstractRequestEvent> deleteThem = new PriorityQueue<AbstractRequestEvent>();
		
		for ( AbstractRequestEvent event : this.eventQueue ) {
		
			shallDelete = false;
			
			if ( event.getState().isDone() ) {
							
				/* Update statistis */
				this.successfulRequests++;
				
				if ( event.getType().isAuction() ) {
					this.auctionSuccessfulRequests++;
				} else if ( event.getType().isLeasing() ) {
					this.leasingSuccessfulRequests++;
				}						
				
				shallDelete = true;
				
			} else if ( event.getState().hasFailed() ) {
				
				this.failedRequests++;			
				
				if ( event.getType().isAuction() ) {
					this.auctionFailedRequests++;
				} else if ( event.getType().isLeasing() ) {
					this.leasingFailedRequests++;
				}				
				
				shallDelete = true;
				
			} 	
			
			if ( shallDelete ) {
				/* It's done (or failed)! Remove it! */
				deleteThem.add(event);			
			}
		}
		
		/* Remove all failed and done events! */
		this.eventQueue.removeAll(deleteThem);
		
	}

	/**
	 * 
	 * @return
	 */
	public static int currentTick() {
		return Engine.tick;
	}
		
}
