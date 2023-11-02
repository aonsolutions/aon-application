package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO.SalesPropertiesDAO;

public class IngenetDAO {
	
	private IngenetDAO() {

	}
	
	public static Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options){
		Integer[] salesDetailIds = ElaborationDAO.getElaborationStream(ctx, f -> 
		f.getSourceProperty().eq(ElaborationSource.SALES_INGENET.value())
			.and(f.getStatusProperty().eq(ElaborationStatus.PENDING.value()))
			.and(f.getSourceIdProperty().isNotNull()))
		.map(Elaboration::getSourceId).toArray(Integer[]::new);
	
		return SalesDAO.getStream(ctx, f -> filter.filter(new SalesPropertiesDAO())
			.and(f.getSalesDetailIdProperty().in(salesDetailIds)), options);
	}
}




