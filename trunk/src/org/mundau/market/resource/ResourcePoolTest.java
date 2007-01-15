package org.mundau.market.resource;


import java.util.PriorityQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mundau.market.event.RequestType;
import org.mundau.market.util.Config;


public class ResourcePoolTest {

	protected ResourcePool resourceDefault;
	
	@Before
	public void setUp() throws Exception {
				
		Config.loadProperties();
		
		resourceDefault = new ResourcePool(10, RequestType.LEASING);
	}
	
	@Test public void testGetCheapestIdleResourcesAllEqual() throws Exception {
		
		PriorityQueue<Resource> resources = this.resourceDefault.getCheapestIdleResources(5);
		
		Assert.assertEquals(resources.size(), 5);
	}

	@Test public void testGetCheapestIdleResourcesOutOfOrder() throws Exception {
		
		ResourcePool pool = new ResourcePool();
		
		Resource r1 = new Resource(10.0, RequestType.AUCTION);
		Resource r2 = new Resource(3.0, RequestType.AUCTION);
		Resource r3 = new Resource(1.0, RequestType.AUCTION);
		Resource r4 = new Resource(2.0, RequestType.AUCTION);
		Resource r5 = new Resource(15.0, RequestType.AUCTION);
		
		Assert.assertEquals( 10.0, r1.getPrice());
		Assert.assertEquals(  3.0, r2.getPrice());
		Assert.assertEquals( 1.0, r3.getPrice());
		Assert.assertEquals( 2.0, r4.getPrice());
		Assert.assertEquals( 15.0, r5.getPrice());
		
		pool.addResource(r1);
		pool.addResource(r2);
		pool.addResource(r3);
		pool.addResource(r4);
		pool.addResource(r5);
				
		PriorityQueue<Resource> resources = pool.getCheapestIdleResources(5);
		
		for (Resource r : resources) {
			System.out.println(r + "\t" + r.getPrice());
		}
		
		Assert.assertEquals(0,r3.compareTo(resources.poll()));
		Assert.assertEquals(0,r4.compareTo(resources.poll()));
		Assert.assertEquals(0,r2.compareTo(resources.poll()));
		Assert.assertEquals(0,r1.compareTo(resources.poll()));
		Assert.assertEquals(0,r5.compareTo(resources.poll()));
		
	}
	
	
	@After
	public void tearDown() throws Exception {
	
		this.resourceDefault = null;
	
	}

}
