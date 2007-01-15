package org.mundau.market.client;


import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mundau.market.event.RequestType;
import org.mundau.market.resource.Resource;
import org.mundau.market.util.Config;

public class ClientTest {

	protected Resource resourceDefault;
	protected Resource resourceAuction;
	protected Resource resourceLeasing;
	protected Client client;
	
	@Before
	public void setUp() throws Exception {
		
		Config.loadProperties();
		
		resourceDefault = new Resource();
		resourceAuction = new Resource(1.0, RequestType.AUCTION);
		resourceLeasing = new Resource(2.0, RequestType.LEASING);
		
		client = new Client();
		client.setBalance(10.0);
	}
	
	@Test public void testAddRemoveResource(){
				
		client.addResource(resourceDefault);

		client.releaseResource(resourceDefault);
		
	}	

	@Test (expected= NoSuchElementException.class) public void testRemoveResourceFailed() throws Exception {

		client.releaseResource(resourceDefault );
		
	}	
	
	@Test public void testGetUtility(){
		Assert.assertTrue( client.getUtility() >= 0 );
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	
	
}
