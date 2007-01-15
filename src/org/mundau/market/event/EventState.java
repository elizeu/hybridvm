package org.mundau.market.event;

public enum EventState { 
	
	DONE, READY, FAILED, RUNNING;
	
	public boolean hasFailed() {
		return (this.equals(FAILED));
	}
	
	public boolean isReady() {
		return (this.equals(READY));
	}
	
	public boolean isDone() {
		return (this.equals(DONE));
	}	

	public boolean isRunning() {
		return (this.equals(RUNNING));
	}		
	
	public boolean wasSuccessful(){
		return ( this.isDone() || this.isReady() );
	}
	
	@Override
	public String toString(){
		
		String description = "READY";
		
		if (this.isDone()) {
			description = "DONE";
		} else if ( this.hasFailed() ) {
			description = "FAILED";
		}
		
		return description;
	}
}
