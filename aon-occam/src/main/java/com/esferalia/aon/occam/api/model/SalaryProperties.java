package com.esferalia.aon.occam.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface SalaryProperties{
	
	Property<Integer> getIdProperty();
	
	Property<String> getCCCProperty();
	
	Property<String> getSSProperty();

	Property<Integer> getContractProperty();
	
	Property<Date> getStartDateProperty();

	Property<Date> getEndDateProperty();
	
	Property<Date> getIssueDateProperty();
	
	Property<Boolean> getIsSalaryProperty();

	Property<Boolean> getIsExtraProperty();

	Property<Boolean> getIsDelayProperty();

	Property<Boolean> getIsSettlementProperty();
}
