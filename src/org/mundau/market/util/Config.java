package org.mundau.market.util;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author elizeu
 *
 */
public class Config {

	public static final double UNITIALIZED_PRICE = -1.0;
		
	public static final String NUMBER_OF_CLIENTS = "number.clients";
	public static final String NUMBER_OF_RESOURCES = "number.resources";
	public static final String FUNDING_RATE = "funding.rate";
	public static final String SIMULATION_TIME = "simulation.time";
	public static final String INITIAL_PRICE = "price.initial";
	public static final String PRICE_FACTOR = "price.factor";
	public static final String SEED = "simulation.seed";
	public static final String AVG_REQUEST_RATE = "request.rate";
	public static final String AVG_JOB_SIZE = "job.size";
	public static final String AVG_JOB_RUN_TIME = "job.runtime";
	public static final String LEASING_AVG_LATENCY_TOLERANCE = "leasing.avg.latency.tolerance"; 
	public static final String AUCTION_AVG_LATENCY_TOLERANCE = "auction.avg.latency.tolerance";

	public static final String DEFAULT_CONFIG_FILE = System.getenv("HOME") + File.separator + "workspace" + File.separator + "hybrid-vm"
														+ File.separator + "etc/simulation.properties" ;
	
	protected static Logger logger = Logger.getRootLogger();
	
	protected static boolean loggerInitilized = false;
	
	public static double getFundingRate() {
		return Double.parseDouble(System.getProperty( FUNDING_RATE));
	}

	public static double getInitialPrice() {
		return Double.parseDouble(System.getProperty( INITIAL_PRICE));
	}
	
	public static int getNumberOfClients() {
		return Integer.parseInt(System.getProperty( NUMBER_OF_CLIENTS));
	}
	
	public static int getNumberOfResources() {
		return Integer.parseInt(System.getProperty( NUMBER_OF_RESOURCES));
	}
	
	public static double getPriceFactor() {
		return Double.parseDouble(System.getProperty( PRICE_FACTOR));
	}
	
	public static int getSimulationTime() {
		return Integer.parseInt(System.getProperty( SIMULATION_TIME));
	}

	public static long getSeed() {
		return Long.parseLong(System.getProperty( SEED));
	}

//	public static final Logger getLogger(Class class0) {
//
//		logger = Logger.getLogger(class0);
//			
//		BasicConfigurator.configure();
//
//		// Now set its level. Normally you do not need to set the
//		// level of a logger programmatically. This is usually done
//		// in configuration files.
//		logger.setLevel(Level.INFO);
//			
//		return logger;
//	}
	
	public static Logger getLogger() {
	
		if ( ! loggerInitilized ) {
			loggerInitilized = true;
			//BasicConfigurator.configure();
		}
		
		logger.setLevel((Level) Level.INFO);
		
		return logger;
	}
		
		
	public static void loadProperties(){
		
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(Config.DEFAULT_CONFIG_FILE)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.setProperties(props);
		
		System.getProperties().list(System.out);
	}
	
	public static void loadProperties(String properties){
		
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(properties)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.setProperties(props);
		
		System.getProperties().list(System.out);
	}

	/**
	 * 
	 * @return the average request rate to be used in a Poisson distribution by the Engine
	 */
	public static double getAverageRequestRate() {

		return Double.parseDouble(System.getProperty( AVG_REQUEST_RATE ));
	}

	public static int getAverageJobSize() {

		return Integer.parseInt(System.getProperty( AVG_JOB_SIZE ));

	}

	public static double getAuctionLatencyTolerance() {
	
		return Double.parseDouble(System.getProperty( AUCTION_AVG_LATENCY_TOLERANCE ));
	}

	public static double getLeasingLatencyTolerance() {
		
		return Double.parseDouble(System.getProperty( LEASING_AVG_LATENCY_TOLERANCE ));
	}

	public static int getAverageJobRunTime() {

		return Integer.parseInt(System.getProperty( AVG_JOB_RUN_TIME ));
	}

	
}
