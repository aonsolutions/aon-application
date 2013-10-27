package com.esferalia.aon.payroll.calculator.test;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.AbstractUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.analysis.solvers.NewtonSolver;
import org.apache.commons.math3.analysis.solvers.RiddersSolver;
import org.apache.commons.math3.analysis.solvers.SecantSolver;

public class SolversTests {

	public static void main(String[] args) {

		AbstractUnivariateSolver solver = new RiddersSolver(0.005);
		solver.solve(1000, new UnivariateFunction() {
			
			@Override
			public double value(double x) {
				// TODO Auto-generated method stub
				return x*x +2*x -10;
			}
		}, 
		
		-1000,
		1000,
		
		0);
		
	}

}
