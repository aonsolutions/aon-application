package com.esferalia.aon.payroll.core.calc;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.it.ITemporalDisability;

public interface ISalaryCalculator extends Serializable{
	
	ISalaryDAO getSalaryDAO();
	

}
