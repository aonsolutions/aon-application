package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface InvoiceFilter extends Serializable {
	
	Filter filter(InvoiceProperties properties);

}
