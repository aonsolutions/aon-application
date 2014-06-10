package com.esferalia.aon.payroll.calculator.jooq;

import static com.esferalia.aon.jooq.Keys.FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF;
import static com.esferalia.aon.jooq.Keys.FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpf.GEOZONE_IRPF;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant.GEOZONE_IRPF_DESCENDANT;
import static com.esferalia.aon.jooq.tables.GeozoneIrpfHandicap.GEOZONE_IRPF_HANDICAP;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.GeozoneIrpfDescendant;

public class JooqGeozoneIrpf {

	public static double getPercent(Connection conn, String geozone_code,
			double amount, int descendants, int handicap, Date date) {
		return getPercent(DSL.using(conn, getDefaultSettings()), geozone_code,
				amount, (byte) descendants, (byte)handicap, date);
	}

	public static double getPercent(Connection conn, String geozone_code,
			double amount, byte descendants, byte handicap, Date date) {
		return getPercent(DSL.using(conn, getDefaultSettings()), geozone_code,
				amount, descendants, handicap, date);
	}

	// ------------------------------------------------------------------------

	private static double getPercent(DSLContext dslContext,
			String geozone_code, double amount, byte descendants, byte handicap, Date date) {

		GeozoneIrpfDescendant MIN_GEOZONE_IRPF_DESCENDANT = new GeozoneIrpfDescendant(
				"max_geaozone_irpf_descendant");

		Field<Double> MIN_PERCENT = DSL
				.min(MIN_GEOZONE_IRPF_DESCENDANT.PERCENT);
		//@formatter:off
		Result<Record4<Double, Double, Double, Double >> result = dslContext.select(
				GEOZONE_IRPF.AMOUNT
				,GEOZONE_IRPF_DESCENDANT.PERCENT
				,GEOZONE_IRPF_HANDICAP.PERCENT 
				,MIN_PERCENT 
				)
		.from(GEOZONE_IRPF)
		.leftOuterJoin(GEOZONE_IRPF_HANDICAP).onKey(FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF)
		.leftOuterJoin(GEOZONE_IRPF_DESCENDANT).onKey(FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF)
		.leftOuterJoin(MIN_GEOZONE_IRPF_DESCENDANT).onKey(FK_GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF)
		.where(GEOZONE_IRPF.AMOUNT.lessOrEqual(amount))
		.and(GEOZONE_IRPF.START_DATE.lessOrEqual(date)
		.and(GEOZONE_IRPF.END_DATE.isNull()
				.or(GEOZONE_IRPF.END_DATE.greaterOrEqual(date))))
		.and(GEOZONE_IRPF.GEOZONE_CODE.eq(geozone_code))
		.and(GEOZONE_IRPF_HANDICAP.HANDICAP.eq(handicap))
		.and(GEOZONE_IRPF_DESCENDANT.DESCENDANT.eq(descendants))
		.groupBy(GEOZONE_IRPF.AMOUNT, GEOZONE_IRPF_DESCENDANT.PERCENT, GEOZONE_IRPF_HANDICAP.PERCENT)
		.orderBy(GEOZONE_IRPF.AMOUNT.sort(SortOrder.DESC))
		.fetch();
		//@formatter:on
		
		for (Record4<Double, Double, Double, Double > record : result) {
			Double handicap_percent = record
					.getValue(GEOZONE_IRPF_HANDICAP.PERCENT);
			Double descendat_percent = record
					.getValue(GEOZONE_IRPF_DESCENDANT.PERCENT);
			Double min_descendat_percent = record.getValue(MIN_PERCENT);

			double percent = 0.00;
			if (handicap_percent != null)
				percent += handicap_percent;

			if (descendat_percent != null)
				percent += descendat_percent;
			else if (min_descendat_percent != null)
				percent += min_descendat_percent;

			return percent;
		}

		return 0;

	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net", "aon",
				"40n");
		Calendar current = Calendar.getInstance();

		Date date = new Date(current.getTimeInMillis());

		System.out.println(getPercent(connection, "01", 30000, (byte)1, (byte)0, date));

	}
}
