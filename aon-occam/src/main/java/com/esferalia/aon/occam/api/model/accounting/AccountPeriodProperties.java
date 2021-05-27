package com.esferalia.aon.occam.api.model.accounting;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AccountPeriodProperties {
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Date> getInitiationDateProperty();
	Property<Date> getDeadlineProperty();
	Property<Byte> getStatusProperty();

}
