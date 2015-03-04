package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;

public class DBCalendar {
	
	public static Vector<String> getCommercial(String domain,
			Integer id) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(REGISTRY.NAME)
					.from(COMMERCIAL_TRACKING).join(REGISTRY).on(COMMERCIAL_TRACKING.SELLER.eq(REGISTRY.ID))
					.where(COMMERCIAL_TRACKING.ID.eq(id))
					.fetch();
			Vector<String> vector = new Vector<String>();
			for (Record1<String> record1 : data) {
				vector.add(record1.value1());
			}
			return vector;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
}
