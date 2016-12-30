package com.esferalia.aon.occam.api.model.accounting;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AccountEntryProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getAccountPeriodProperty();
	Property<Date> getEntryDateProperty();
	Property<Byte> getEntryTypeProperty();
	Property<Integer> getJournalProperty();
	Property<Byte> getConfidentialProperty();

}
