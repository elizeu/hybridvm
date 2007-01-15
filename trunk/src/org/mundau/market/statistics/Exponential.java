package org.mundau.market.statistics;

import java.util.Random;
import java.lang.Math;

/** Exponential.java
 * 
 *   This class generates various random variables for exponential distributions
 *   which not directly supported in Java
 *   
 *   @author http://www.math.csusb.edu/faculty/stanton/probstat/poisson.html
 *   @author elizeu (modified little things )
 */
public class Exponential extends Random {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Generates 
	 * 
	 * @param b
	 * @return
	 */
	public  double nextExponential(double b) {
		double randx;
		double result;
		
		randx = nextDouble();
		result = -1*b*Math.log(randx);
		
		return result;
	}

}
