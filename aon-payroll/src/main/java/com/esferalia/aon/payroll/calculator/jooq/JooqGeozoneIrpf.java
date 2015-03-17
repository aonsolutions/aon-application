package com.esferalia.aon.payroll.calculator.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SortOrder;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class JooqGeozoneIrpf extends org.jooq.impl.AbstractKeys {

	private static final org.jooq.ForeignKey<com.esferalia.aon.jooq.tables.records.GeozoneIrpfHandicapRecord, com.esferalia.aon.jooq.tables.records.GeozoneIrpfRecord> FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF = org.jooq.impl.AbstractKeys
			.createForeignKey(
					com.esferalia.aon.jooq.Keys.KEY_GEOZONE_IRPF_PRIMARY,
					com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP,
					com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP.GEOZONE_IRPF);
	private static final org.jooq.ForeignKey<com.esferalia.aon.jooq.tables.records.GeozoneIrpfDescendantRecord, com.esferalia.aon.jooq.tables.records.GeozoneIrpfRecord> FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF = org.jooq.impl.AbstractKeys
			.createForeignKey(
					com.esferalia.aon.jooq.Keys.KEY_GEOZONE_IRPF_PRIMARY,
					com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT,
					com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT.GEOZONE_IRPF);

	public static Double getPercent(Connection conn, String geozone_code,
			double amount, int descendants, int handicap, Date date) {
		return getPercent(DSL.using(conn, getDefaultSettings()), geozone_code,
				amount, (byte) descendants, (byte) handicap, date);
	}

	public static Double getPercent(Connection conn, String geozone_code,
			double amount, byte descendants, byte handicap, Date date) {
		return getPercent(DSL.using(conn, getDefaultSettings()), geozone_code,
				amount, descendants, handicap, date);
	}

	// ------------------------------------------------------------------------

	private static Double getPercent(DSLContext dslContext,
			String geozone_code, double amount, byte descendants,
			byte handicap, Date date) {
		
		SelectConditionStep<Record1<Byte>> availDescendant = dslContext
				.select(DSL.least(DSL.abs(descendants),DSL.max(GEOZONE_IRPF_DESCENDANT.DESCENDANT)))
				.from(GEOZONE_IRPF_DESCENDANT)
				.where(GEOZONE_IRPF_DESCENDANT.GEOZONE_IRPF.eq(GEOZONE_IRPF.ID))
				;
		
		//@formatter:off
		Result<Record3<Double, Double, Double>> result = dslContext.select(
				GEOZONE_IRPF.AMOUNT
				,GEOZONE_IRPF_DESCENDANT.PERCENT
				,GEOZONE_IRPF_HANDICAP.PERCENT 
				)
		.from(GEOZONE_IRPF)
		.join(GEOZONE_IRPF_DESCENDANT).onKey(FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF)
		.leftOuterJoin(GEOZONE_IRPF_HANDICAP).on(GEOZONE_IRPF.ID.eq(GEOZONE_IRPF_HANDICAP.GEOZONE_IRPF)
				.and(GEOZONE_IRPF_HANDICAP.HANDICAP.eq((byte)(handicap -1))))
		.where(GEOZONE_IRPF.AMOUNT.lessOrEqual(amount))
		.and(GEOZONE_IRPF.START_DATE.lessOrEqual(date)
		.and(GEOZONE_IRPF.END_DATE.isNull()
				.or(GEOZONE_IRPF.END_DATE.greaterOrEqual(date))))
		.and(GEOZONE_IRPF.GEOZONE_CODE.eq(geozone_code))
//		.and(GEOZONE_IRPF_HANDICAP.HANDICAP.eq((byte)(handicap -1))
//				.or(GEOZONE_IRPF_HANDICAP.HANDICAP.isNull()))
		.and(GEOZONE_IRPF_DESCENDANT.DESCENDANT.eq(availDescendant))
		.orderBy(GEOZONE_IRPF.AMOUNT.sort(SortOrder.DESC))
		.fetch();
		//@formatter:on


		Double percent = null;

		for (Record3<Double, Double, Double> record : result) {
			Double handicap_percent = record
					.getValue(GEOZONE_IRPF_HANDICAP.PERCENT);
			Double descendat_percent = record
					.getValue(GEOZONE_IRPF_DESCENDANT.PERCENT);

			if (descendat_percent != null && percent == null)
				percent = descendat_percent;

			if (handicap_percent != null && percent != null)
				return (percent - handicap_percent);

		}

		return percent;

	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net", "aon",
				"40n");
		/*
		 * Calendar current = Calendar.getInstance();
		 * 
		 * Date date = new Date(current.getTimeInMillis());
		 * 
		 * System.out.println(getPercent(connection, "01", 30000, (byte)1,
		 * (byte)0, date));
		 */
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		DSLContext dslContext = DSL.using(connection, settings);

		//@formatter:off
		Cursor<Record> cursor = 
				dslContext.select()
				.from(RATTACH)
				.join(DOMAIN).onKey()
				.where(DOMAIN.NAME.like("cemerida%")).fetchLazy();
		//@formatter:on

		for (Record record : cursor) {
			String description = record.getValue(RATTACH.DESCRIPTION);
			System.out.println(description);
		}

		// System.out.println(dslContext.select().from(RATTACH).where(RATTACH.DOMAIN.eq(1)).toString());
	}
}
