package org.mundau.market;

import org.mundau.market.engine.Engine;
import org.mundau.market.util.Config;

/**
 * 
 * This is the bootstrap class of the simulator 
 * 
 * @author elizeu
 *
 */
public class Main {

	public static void main(String[] args){
	
		/* The simualtor kernel */
		Engine engine;
		
		/* Load configuration */
		if ( args.length < 1 ) {
			engine = Engine.getInstance( Config.DEFAULT_CONFIG_FILE );
		} else {
			engine = Engine.getInstance( args[0] );
		}
		
		/* start simulation engine */ 		
		engine.mainLoop();
		
	}	
		
}
