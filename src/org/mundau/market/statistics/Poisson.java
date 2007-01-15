package org.mundau.market.statistics;

import java.util.Random;
import java.lang.Math;

/** Poisson.java
 * 
 *   This class generates various random variables for a Poisson distribution
 *   which is not directly supported in Java
 *   
 *   @author http://www.math.csusb.edu/faculty/stanton/probstat/poisson.html
 *   @author elizeu (modified little things )
 */
public class Poisson extends Random {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Poisson(long seed){
		super(seed);
	}
	
	public  int nextPoisson(double lambda) {
		
		double elambda = Math.exp(-1*lambda);
		double product = 1;
		int count =  0;
		int result=0;
	
		while (product >= elambda) {
			product *= nextDouble();
			result = count;
			count++; // keep result one behind
		}
	
		return result;
	}

}
