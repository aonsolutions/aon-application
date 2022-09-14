package com.esferalia.aon.occam.impl.jooq.dao.console;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConsoleDAO {

	private static final String INFORMATION_SCHEMA = "information_schema";
	private static final String MYSQL = "mysql";
	private static final String PERFORMANCE_SCHEMA = "performance_schema";
	
	protected ConsoleDAO() {
	}
	
	public static Stream<Schema> getSchemas(AONContext ctx) {
		return  ctx.getDslContext()
			.meta()
			.getSchemas()
			.stream();
	}
	
	public static Stream<Schema> getAONSchemas(AONContext ctx) {
		return getSchemas(ctx)
			.filter(schema -> !INFORMATION_SCHEMA.equals(schema.getName()))
			.filter(schema -> !MYSQL.equals(schema.getName()))
			.filter(schema -> !PERFORMANCE_SCHEMA.equals(schema.getName()))
			;			
	}

	public static LinkedList<Domain> getDomains(CloseableAONContext ctx, DomainParams params) {
		return DomainDAO.getDomainList(ctx, p-> getFilter(p,params));
	}

	private static Filter getFilter(DomainProperties p, DomainParams params) {
		Filter filter = p.getIdProperty().isNotNull();
		if (AonStringUtils.isNotBlank(params.getQuery())) {
			String q = "%" + params.getQuery() + "%";
			filter = filter.and( p.getNameProperty().like(q)
				.or(p.getDescriptionProperty().like(q)));
		}
		
		if (params.getParent() != null ) {
			filter = filter.and(p.getParentProperty().eq(params.getParent()) );
		}

		if (params.getType() != null ) {
			filter = filter.and(p.getTypeProperty().eq(params.getType().byteValue()) ); 
		}
		if (params.getActive() != null ) {
			filter = filter.and(p.getTypeProperty().eq((byte) (params.getActive().booleanValue()?1:0)));
		}
		if (params.getEnableHeredity() != null ) {
			filter = filter.and(p.getEnableheredityProperty().eq((byte) (params.getEnableHeredity().booleanValue()?1:0)));
		}
		if (params.getDomainManagement() != null ) {
			filter = filter.and(p.getDomainmanagementProperty().eq((byte) (params.getDomainManagement().booleanValue()?1:0)));
		}
		if (params.getFromLastAccess() != null ) {
			filter = filter.and(p.getLastaccessDateProperty().ge(
				new Timestamp( params.getFromLastAccess().getTime())));
		}
		if (params.getToLastAccess() != null ) {
			filter = filter.and(p.getLastaccessDateProperty().le(
				new Timestamp( params.getToLastAccess().getTime())));
		}
		
		if (params.getFromExpirationDate() != null ) {
			filter = filter.and(p.getExpirationdateProperty().ge(
				AonDateUtils.toSql(params.getFromExpirationDate())));
		}
		if (params.getToExpirationDate() != null ) {
			filter = filter.and(p.getExpirationdateProperty().le(
				AonDateUtils.toSql(params.getToExpirationDate())));
		}
		return filter;
	}

	public static boolean deleteDomain(CloseableAONContext ctx, DomainParams params) {
		return true;
	}

}
