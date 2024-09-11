package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
@Deprecated
public interface InvoiceFilterOLD extends Serializable {
	
	Filter filter(InvoicePropertiesOLD properties);

}
