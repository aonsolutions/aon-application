package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialTracking;

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
	
	public static Vector<String> getSellerEmails(CommercialTracking ct, String domain) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(RMEDIA.VALUE)
					.from(RMEDIA).join(COMMERCIAL_TRACKING).on(COMMERCIAL_TRACKING.SELLER.eq(RMEDIA.REGISTRY))
					.where(RMEDIA.MEDIA.eq((byte)4))
						.and(COMMERCIAL_TRACKING.ID.eq(ct.getId()))
						.and(RMEDIA.COMMERCIAL.eq((byte)1))
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
	
	public static String getSellerEmail(CommercialTracking ct, String domain) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(RADDINFO.VALUE)
					.from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(ct.getSeller()))
					.and(RADDINFO.ATTRIBUTE.eq("GOOGLEMAIL"))
					.fetch();
			String email = null;
			for (Record1<String> record1 : data) {
				email = record1.value1();
			}
			return email;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void setSellerEmail(CommercialTracking ct, String domain, String email) throws AonConnectionException,
			SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.insertInto(RADDINFO, RADDINFO.DOMAIN, RADDINFO.REGISTRY, RADDINFO.ATTRIBUTE, RADDINFO.VALUE, RADDINFO.VALUE_DATE)
					.values(ct.getDomain(),ct.getSeller(),"GOOGLEMAIL",email,new Date(new java.util.Date().getTime())).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
}
