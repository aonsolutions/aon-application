package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
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
	
	public static Domain getDomain(String domainCon) throws SQLException{
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domainCon);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record4<Integer, String, String, Byte>> data = dslContext.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION, DOMAIN.TYPE)
				.from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainCon))
				.fetch();

			Domain domain = new Domain();
			if(data.get(0).value1()!= null)
			domain.setId(data.get(0).value1());
			if(data.get(0).value2()!= null)
				domain.setName(data.get(0).value2());
			if(data.get(0).value3()!= null)
				domain.setDescription(data.get(0).value3());
			if(data.get(0).value4() != null){
				domain.setType(data.get(0).value4().shortValue());
			}
			return domain;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
}
