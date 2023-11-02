package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;

public interface IIngenet {

	Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options);

}
