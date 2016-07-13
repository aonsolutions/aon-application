//package com.esferalia.aon.occam.api.model.management;
//
//import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
//
//@FunctionalInterface
//public interface SalesFilter{
//	
//	Filter filter(SalesProperties properties);
//
//}

package com.esferalia.aon.occam.api.model.management;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface SalesFilter{
	
	Filter filter(SalesProperties properties);

}
