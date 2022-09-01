package com.esferalia.aon.occam.impl.jooq.dao.console;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

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

	public static LinkedList<Domain> getDomains(CloseableAONContext ctx, String schema, String query) {
		String q = "%" + query + "%";
		return DomainDAO.getDomainList(ctx, p -> 
			p.getNameProperty().like(q)
			.or(p.getDescriptionProperty().like(q))
		);
	}

}
