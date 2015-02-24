package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;

public class DBCalendar {
	
	public static Domain getDomain(String domainCon, Integer domainId) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domainCon);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record2<String, String>> data = dslContext.select(DOMAIN.NAME, DOMAIN.DESCRIPTION)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId))
				.fetch();

			Domain domain = new Domain();
			domain.setId(domainId);
			if(data.get(0).value1()!= null)
				domain.setName(data.get(0).value1());
			if(data.get(0).value2()!= null)
				domain.setDescription(data.get(0).value2());
						
			return domain;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
}
