package com.esferalia.aon.payroll.core.calc;

import java.io.Serializable;

public interface INominaCalculator extends Serializable{
	
	INominaDAO getSalaryDAO();
	

}
