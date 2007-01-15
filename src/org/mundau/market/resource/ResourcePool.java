package org.mundau.market.resource;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

import org.mundau.market.event.RequestType;
import org.mundau.market.util.Config;

public class ResourcePool {
	
	/* A collection of resources */
	protected Queue<Resource> pool;
	protected RequestType type;
		
	public ResourcePool(){
			
		this.pool = new PriorityQueue<Resource>();
		
		this.type = RequestType.LEASING;
	}
	
	public ResourcePool(int size, RequestType _type){
		
		this();
				
		this.type = _type;
		
		Resource r;
		int i;			
		
		/* Create resources in the Auction Pool */
		for ( i=0; i < size; i++) {
			
			r = new Resource( Config.getInitialPrice(), this.type);
			
			this.pool.add(r);
		}				
	}
	
	public PriorityQueue<Resource> getCheapestIdleResources(int n) throws IndexOutOfBoundsException {
		
		/* If n is greater than resources.size() */
		if ( this.pool.size() < n ) {
			throw new IndexOutOfBoundsException("The number of resources requested is greater than the pool size.");
		}
		
		int picked = 0;
		PriorityQueue<Resource> cheapestResources = new PriorityQueue<Resource>();
		
		Iterator it = this.pool.iterator();
		
		while ( (picked < n) &&  it.hasNext()) {
							
			Resource r = (Resource) it.next();
			
			if ( r.isIdle() ) {
				cheapestResources.add( r );
			
				picked++;
			}
		}
			
		/* If there is not enough resources idle */
		if ( picked < n ) {
			throw new IndexOutOfBoundsException("The number of idle resources ("+ picked 
												+") are not enough to answer this request ("+ n +".");
		}		
		
		return cheapestResources;		
	}

	//TODO: refactor it!!
	public PriorityQueue<Resource> getCheapestIdleResources(int n, int start, int end) throws IndexOutOfBoundsException {
		
		/* If n is greater than resources.size() */
		if ( this.pool.size() < n ) {
			throw new IndexOutOfBoundsException("The number of resources requested is greater than the pool size.");
		}
		
		int picked = 0;
		double price = Double.MAX_VALUE;
		double lastPrice = 0;
		Resource pickedResource ;
		PriorityQueue<Resource> cheapestResources = new PriorityQueue<Resource>();
		
		while ( picked < n ) {
			
			pickedResource = null;
				
			for (Resource r : this.pool ){				
				
				if ( (r.getPrice() <= price) && (r.getPrice() >= lastPrice ) && r.isIdle() ) {
					pickedResource = r;
					price = r.getPrice();					
				}				
			}
			
			cheapestResources.add(pickedResource);
			lastPrice = pickedResource.getPrice();
			picked++;
		}
			
		return cheapestResources;
		
	}

	
	/** teste */
	public static void main(String[] args) {
		
		TreeSet<Integer> A = new TreeSet<Integer>();
		int i; int size = 10;
		
		/* Create resources in the Auction Pool */
		for ( i=0; i < size; i++) {
			
			A.add(new Integer(i));
		}
		
		for (Integer a : A) {
			System.out.println(a);
		}
		
		Integer U = new Integer(-1);
		A.add(U);
		
		System.out.println( "aaaaaaaaaa");
		
		for (Integer a : A) {
			System.out.println(a);
		}
		
		
	}

	public void addResource(Resource r1) {
		this.pool.add(r1);		
	}

	public Queue<Resource> getAllResources() {		
		return this.pool;
	}

	public int getSize() {
		return this.pool.size();
	}
	
}
