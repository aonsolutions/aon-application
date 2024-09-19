package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface InvoiceFlatFilter extends Serializable {
	
	Filter filter(InvoiceFlatProperties properties);

}
