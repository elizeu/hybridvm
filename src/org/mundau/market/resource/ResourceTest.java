package org.mundau.market.resource;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mundau.market.client.Client;
import org.mundau.market.event.RequestType;
import org.mundau.market.util.Config;


public class ResourceTest {

	protected Resource resourceDefault;
	protected Resource resourceAuction;
	protected Resource resourceLeasing;
	
	@Before
	public void setUp() throws Exception {
		
		Config.loadProperties();
		
		resourceDefault = new Resource();
		resourceAuction = new Resource(1.0, RequestType.AUCTION);
		resourceLeasing = new Resource(2.0, RequestType.LEASING);
	}
	
	@Test public void testIsIdle(){
		
		Assert.assertTrue( resourceDefault.isIdle() );
	}

	@Test public void testAddClient(){
		
		Client client = new Client();
		
		resourceDefault.addClient(client, 2.0);
		
		Assert.assertFalse( resourceDefault.isIdle() );
		
	}	

	@Test public void testAddRemoveClient() throws Exception {
		
		Client client = new Client();
		
		resourceDefault.addClient(client, 1.0);
		
		Assert.assertFalse( resourceDefault.isIdle() );
		
		resourceDefault.removeClient(client);
		
		Assert.assertTrue(resourceDefault.isIdle());
		
	}	
	
	
	@Test public void testIsIdleAuction(){
						
		Assert.assertTrue( resourceAuction.isIdle() );
		
	}
	
	@Test public void testIsIdleLeasing(){
		
		Assert.assertTrue( resourceLeasing.isIdle() );
		
	}
	
	@Test public void testLeasingAverageShare(){
		
		Client c1 = new Client();
		Client c2 = new Client();
		Client c3 = new Client();
		
		resourceDefault.addClient(c1, 1.0);
				
		Assert.assertEquals( 1.0, resourceDefault.getClientShare(c1) );		
		
		resourceDefault.addClient(c2, 2.0);

		Assert.assertEquals( 0.0, resourceDefault.getClientShare(c1) );
		Assert.assertEquals( 1.0, resourceDefault.getClientShare(c2) );
		
		resourceDefault.addClient(c3, 2.0);

		Assert.assertEquals( 0.0, resourceDefault.getClientShare(c1) );
		Assert.assertEquals( 0.0, resourceDefault.getClientShare(c2) );
		Assert.assertEquals( 1.0, resourceDefault.getClientShare(c3) );
		
	}

	@Test public void testAuctionAverageShare(){
		
		Client c1 = new Client();
		Client c2 = new Client();
		Client c3 = new Client();

		resourceAuction.addClient(c1, 1.0);
		
		Assert.assertEquals( 1.0, resourceAuction.getClientShare(c1) );		
				
		resourceAuction.addClient(c2, 1.0);
		
		Assert.assertEquals( 0.5, resourceAuction.getClientShare(c1) );
		Assert.assertEquals( 0.5, resourceAuction.getClientShare(c2) );
	
		resourceAuction.addClient(c3, 1.0);
		
		Assert.assertEquals( 1.0/3.0, resourceAuction.getClientShare(c1) );
		Assert.assertEquals( 1.0/3.0, resourceAuction.getClientShare(c2) );
		Assert.assertEquals( 1.0/3.0, resourceAuction.getClientShare(c3) );		
		
		resourceAuction.removeClient(c3);

		Assert.assertEquals( 0.5, resourceAuction.getClientShare(c1) );
		Assert.assertEquals( 0.5, resourceAuction.getClientShare(c2) );
				
	}	
	
	@After
	public void tearDown() throws Exception {
	}

}
