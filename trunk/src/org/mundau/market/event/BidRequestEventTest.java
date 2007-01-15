package org.mundau.market.event;


import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mundau.market.client.Client;
import org.mundau.market.resource.Resource;
import org.mundau.market.util.Config;

public class BidRequestEventTest {

	protected Resource resourceDefault;
	protected Resource resourceAuction;
	protected Resource resourceLeasing;
	protected Client client;
	protected AbstractRequestEvent request;
	
	@Before
	public void setUp() throws Exception {
		
		Config.loadProperties();
		
		resourceDefault = new Resource();
		resourceAuction = new Resource(1.0, RequestType.AUCTION);
		resourceLeasing = new Resource(2.0, RequestType.LEASING);
		
		request = EventBuilder.createEvent(RequestType.AUCTION);
				
		client = new Client();
		client.setBalance(10.0);
	}
	

	@Test public void testReleaseAllResources() throws Exception {

		request.setClient(client);
		
		client.addResource(resourceDefault);
		resourceDefault.addClient(client, 5.0);
		
		client.releaseAllResources();
		
	}	
	
	
	@After
	public void tearDown() throws Exception {
		BasicConfigurator.resetConfiguration();
	}
	
	
	
}
