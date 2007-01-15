package org.mundau.market.event;

public enum RequestType { 
	
	AUCTION, LEASING;
	
	public boolean isAuction() {
		return (this.equals(AUCTION));
	}
	
	public boolean isLeasing() {
		return (this.equals(LEASING));
	}	
	
	public String toString(){

		String description = "LEASING";
		
		if ( this.isAuction() ) {
			description = "AUCTION";
		}
		
		return description; 
	}
}
