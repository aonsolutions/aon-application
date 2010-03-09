package com.esferalia.aon.payroll.core.it;


public interface ITemporaryDisabilityCalculator {

	boolean accept(ITemporalDisability td);
	
	Double calculatePrevPeriodBaseSalary(ITemporalDisability td);

	Integer getPrevDaysCount(ITemporalDisability td);

	Double getDailyRegulatoryBase(ITemporalDisability td);

	Double getDailyCommonContingencyBase(ITemporalDisability td);

	Double getDailyAccidentBase(ITemporalDisability td);

	Double getDailyAssistance60(ITemporalDisability td);

	Double getDailyAssistance75(ITemporalDisability td);

}
