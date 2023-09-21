package com.esferalia.aon.payroll.calculator.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static org.jooq.impl.DSL.cast;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.decode;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.sum;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.transaction.NotSupportedException;

import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record11;
import org.jooq.Record9;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.Workplace;

public class JooqGPSReports extends JooqCommon {

	public static interface Line {

	}

	public static interface A3Line extends Line {

		String getNIF();

		String getPerson();

		double getTurnPlus();

		double getDelays();

		double getIncentives();

		double getEmbargos();

		String getComments();

		Map<String, Double> getCECOs();

	}

	public static interface FTELine extends Line {

		String getHotel();

		String getSection();

		String getJob();

		int getWeek();

		Date getDay();

		String getPerson();

		boolean isClosed();

		int getHours();

		int getStaff();

		double getRealStaff();
	}

	public static interface HolidayLine extends Line {

		String getHotel();

		String getSection();

		String getJob();

		int getWeek();

		Date getDay();

		String getPerson();

		boolean isClosed();

		int getV();

		int getFT();

		int getFR();

		int getLT();

		int getLL();

		/*
		 * Real hours at current year = Sum of worked hours (values : 4/8/10/12)
		 */
		int getHE();

		/*
		 * HFD = Min hours for contracts fixed and not continous
		 */
		int getHFD();

	}

	public static interface Report<L extends Line> extends Iterable<L> {
	}

	// ------------------------------------------------------------------------

	private static final Calendar CALENDAR = Calendar.getInstance();

	private static abstract class AbstractReport<R extends Record, L extends Line>
			implements Report<L> {

		private Cursor<R> cursor;

		AbstractReport(Cursor<R> cursor) {
			this.cursor = cursor;
		}

		@Override
		public Iterator<L> iterator() {
			return iterator(cursor.iterator());
		}

		abstract Iterator<L> iterator(Iterator<R> recordIterator);

	}

	private static abstract class AbstractLineIterator<R extends Record, L extends Line>
			implements Iterator<L> {

		private Iterator<R> recordIterator;

		AbstractLineIterator(Iterator<R> recordIterator) {
			this.recordIterator = recordIterator;
		}

		@Override
		public boolean hasNext() {
			return recordIterator.hasNext();
		}

		@Override
		public L next() {
			return next(recordIterator);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		L next(Iterator<R> recordIterator) {
			return next(recordIterator.next());
		}

		abstract L next(R record);

	}

	private static class FTELineImpl implements FTELine {

		private String hotel;
		private String section;
		private String job;
		private int week;
		private Date day;
		private String person;
		private boolean closed;
		private int hours;
		private int staff;
		private double realStaff;

		public String getHotel() {
			return hotel;
		}

		@Override
		public String getSection() {
			return section;
		}

		@Override
		public String getJob() {
			return job;
		}

		@Override
		public int getWeek() {
			return week;
		}

		@Override
		public Date getDay() {
			return day;
		}

		@Override
		public String getPerson() {
			return person;
		}

		@Override
		public boolean isClosed() {
			return closed;
		}

		@Override
		public int getHours() {
			return hours;
		}

		@Override
		public int getStaff() {
			return staff;
		}

		@Override
		public double getRealStaff() {
			return realStaff;
		}

	}

	private static class HolidayLineImpl implements HolidayLine {

		private String hotel;
		private String section;
		private String job;
		private int week;
		private Date day;
		private String person;
		private boolean closed;
		private int v;
		private int ft;
		private int fr;
		private int lt;
		private int ll;
		private int he;
		private int hfd;

		@Override
		public String getHotel() {
			return hotel;
		}

		@Override
		public String getSection() {
			return section;
		}

		@Override
		public String getJob() {
			return job;
		}

		@Override
		public int getWeek() {
			return week;
		}

		@Override
		public Date getDay() {
			return day;
		}

		@Override
		public String getPerson() {
			return person;
		}

		@Override
		public boolean isClosed() {
			return closed;
		}

		@Override
		public int getV() {
			return v;
		}

		@Override
		public int getFT() {
			return ft;
		}

		@Override
		public int getFR() {
			return fr;
		}

		@Override
		public int getLT() {
			return lt;
		}

		@Override
		public int getHE() {
			return he;
		}

		@Override
		public int getLL() {
			return ll;
		}

		@Override
		public int getHFD() {
			return hfd;
		}
	}

	private static class A3LineImpl implements A3Line {

		private String nif;
		private String person;
		private double turnPlus;
		private double delays;
		private double incentives;
		private double embargos;
		private String comments;
		private Map<String, Double> cecos = new HashMap<String, Double>();

		@Override
		public String getNIF() {
			return nif;
		}

		@Override
		public String getPerson() {
			return person;
		}

		@Override
		public double getTurnPlus() {
			return turnPlus;
		}

		@Override
		public double getDelays() {
			return delays;
		}

		@Override
		public double getIncentives() {
			return incentives;
		}

		@Override
		public double getEmbargos() {
			return embargos;
		}

		@Override
		public String getComments() {
			return comments;
		}

		@Override
		public Map<String, Double> getCECOs() {
			return cecos;
		}

	}

	// ------------------------------------------------------------------------

	public static Report<A3Line> getA3Report(Connection connection, Date start,
			Date end, int workplaces[]) throws SQLException {
		return getA3Report(connection, start, end, box(workplaces));
	}

	public static Report<A3Line> getA3Report(Connection connection, Date start,
			Date end, Integer workplaces[]) throws SQLException {
		return getA3Report(DSL.using(connection, getDefaultSettings()), start,
				end, workplaces);
	}

	public static Report<FTELine> getFTEReport(Connection connection,
			Date start, Date end, int workplaces[]) throws SQLException {

		return getFTEReport(DSL.using(connection, getDefaultSettings()), start,
				end, box(workplaces));
	}

	public static Report<HolidayLine> getHolidayReport(Connection connection,
			Date start, Date end, Integer workplaces[]) throws SQLException {
		return getHolidayReport(DSL.using(connection, getDefaultSettings()),
				start, end, workplaces);
	}

	public static Report<HolidayLine> getHolidayReport(Connection connection,
			Date start, Date end, int workplaces[]) throws SQLException {

		return getHolidayReport(DSL.using(connection, getDefaultSettings()),
				start, end, box(workplaces));
	}

	// ------------------------------------------------------------------------

	static Report<A3Line> getA3Report(DSLContext dslContext, Date start,
			Date end, Integer workplaces[]) throws SQLException {

		final ContractData LTA = CONTRACT_DATA.as("lta");
		final ContractData CLT = CONTRACT_DATA.as("clt");

		final ContractData LT = CONTRACT_DATA.as("lt");
		final ContractData LR = CONTRACT_DATA.as("lr");
		final ContractData ATRASOS = CONTRACT_DATA.as("atrasos");
		final ContractData EMBARGOS = CONTRACT_DATA.as("embargos");
		final ContractData INCENTIVOS = CONTRACT_DATA.as("incentivos");
		final ContractData CECO = CONTRACT_DATA.as("ceco");
		final ContractData DESEMPENO = CONTRACT_DATA.as("desempeno");

		// final ContractData OBSERVACIONES = CONTRACT_DATA.as("observaciones");

		final Field<BigDecimal> SUM_ATRASOS = sum(cast(ATRASOS.EXPRESSION,
				BigDecimal.class));
		final Field<BigDecimal> SUM_EMBARGOS = sum(cast(EMBARGOS.EXPRESSION,
				BigDecimal.class));
		final Field<BigDecimal> SUM_INCENTIVOS = sum(cast(
				INCENTIVOS.EXPRESSION, BigDecimal.class));
		final Field<BigDecimal> SUM_LTA = sum(cast(LTA.EXPRESSION,
				BigDecimal.class));
		final Field<BigDecimal> MAX_CLT = max(cast(CLT.EXPRESSION,
				BigDecimal.class));
		final Field<Integer> COUNT_LT = count(LT.EXPRESSION);
		final Field<Integer> COUNT_LR = count(LR.EXPRESSION);

		//@formatter:off
		final Field<BigDecimal> SUM_DESEMPENO = sum(decode()
				.when(DESEMPENO.EXPRESSION.eq("4"), 4)
				.when(DESEMPENO.EXPRESSION.eq("8"), 8)
				.when(DESEMPENO.EXPRESSION.eq("10"), 10)
				.when(DESEMPENO.EXPRESSION.eq("12"), 12)
				.when(DESEMPENO.EXPRESSION.eq("LT"), 8)
				.when(DESEMPENO.EXPRESSION.eq("LR"), 8)
				.when(DESEMPENO.EXPRESSION.eq("FT"), 8)
				.when(DESEMPENO.EXPRESSION.eq("FR"), 8)
				.when(DESEMPENO.EXPRESSION.eq("B"), 8)
				.when(DESEMPENO.EXPRESSION.eq("P"), 8)
				//.when(DESEMPENO.EXPRESSION.eq("AI"), 8)
				.otherwise(0));
		//@formatter:on

		//@formatter:off
		Cursor<Record11<String,String,String,BigDecimal,BigDecimal,BigDecimal,BigDecimal,BigDecimal, Integer, Integer,BigDecimal>> cursor =  dslContext.select(
				REGISTRY.DOCUMENT
				,REGISTRY.NAME
				,CECO.EXPRESSION
				,SUM_ATRASOS
				,SUM_EMBARGOS
				,SUM_INCENTIVOS
				,SUM_LTA
				,MAX_CLT
				,COUNT_LT
				,COUNT_LR
				,SUM_DESEMPENO
				)
			.from(REGISTRY)
			.join(CONTRACT).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.join(CECO).on(CONTRACT.ID.eq(CECO.CONTRACT)
					.and(CECO.NAME.eq("CODIGO RRHH")))
			.leftOuterJoin(ATRASOS).on(CONTRACT.ID.eq(ATRASOS.CONTRACT).
					and(ATRASOS.NAME.eq("ATRASOS")).
					and(ATRASOS.START_DATE.between(start, end)))
			.leftOuterJoin(EMBARGOS).on(CONTRACT.ID.eq(EMBARGOS.CONTRACT).
					and(EMBARGOS.NAME.eq("EMBARGOS")).
					and(EMBARGOS.START_DATE.between(start, end)))
			.leftOuterJoin(INCENTIVOS).on(CONTRACT.ID.eq(INCENTIVOS.CONTRACT).
					and(INCENTIVOS.NAME.eq("INCENTIVOS")).
					and(INCENTIVOS.START_DATE.between(start, end)))
			.leftOuterJoin(LTA).on(CONTRACT.ID.eq(LTA.CONTRACT).
					and(LTA.NAME.eq("LTA")).
					and(LTA.START_DATE.between(start, end)))
			.leftOuterJoin(CLT).on(CONTRACT.ID.eq(CLT.CONTRACT).
					and(CLT.NAME.eq("CLT")).
					and(CLT.START_DATE.between(start, end)))
			.leftOuterJoin(LT).on(CONTRACT.ID.eq(LT.CONTRACT).
					and(LT.NAME.like("DESEMPE%O")).
					and(LT.EXPRESSION.eq("LT")).
					and(LT.START_DATE.between(start, end)))
			.leftOuterJoin(LR).on(CONTRACT.ID.eq(LR.CONTRACT).
					and(LR.NAME.like("DESEMPE%O")).
					and(LR.EXPRESSION.eq("LT")).
					and(LR.START_DATE.between(start, end)))
			.leftOuterJoin(DESEMPENO).on(CONTRACT.ID.eq(DESEMPENO.CONTRACT).
					and(LR.NAME.like("DESEMPE%O")).
					and(LR.START_DATE.between(start, end)))
			.where(WORKPLACE.ID.in(workplaces))
			.groupBy(REGISTRY.DOCUMENT, REGISTRY.NAME, CECO.EXPRESSION)
			.fetchLazy();
		//@formatter:on
		Report<A3Line> a3Report = new AbstractReport<Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal>, A3Line>(
				cursor) {

			@Override
			Iterator<A3Line> iterator(
					Iterator<Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal>> recordIterator) {

				return new AbstractLineIterator<Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal>, A3Line>(
						recordIterator) {

					Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal> record;

					@Override
					public boolean hasNext() {
						return super.hasNext() || record != null;
					}

					@Override
					A3Line next(
							Iterator<Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal>> recordIterator) {
						A3LineImpl a3Line = new A3LineImpl();

						if (record == null)
							record = recordIterator.next();

						a3Line.nif = record.getValue(REGISTRY.DOCUMENT);
						a3Line.person = record.getValue(REGISTRY.NAME);

						a3Line.delays = doubleValue(record
								.getValue(SUM_ATRASOS));
						a3Line.embargos = doubleValue(record
								.getValue(SUM_EMBARGOS));
						a3Line.incentives = doubleValue(record
								.getValue(SUM_INCENTIVOS));

						a3Line.turnPlus = (record.getValue(COUNT_LT)
								- record.getValue(COUNT_LR) - doubleValue(record
									.getValue(SUM_LTA)))
								* (doubleValue(record.getValue(MAX_CLT)));

						Map<String, Double> cecos = new HashMap<String, Double>();

						double total = 0.00;
						while (StringUtils.equals(a3Line.nif,
								record.getValue(REGISTRY.DOCUMENT))) {
							double ceco = doubleValue(record
									.getValue(SUM_DESEMPENO));
							total += ceco;
							cecos.put(record.getValue(CECO.EXPRESSION), ceco);

							if ( !recordIterator.hasNext() ) {
								record = null;
								break;
							}
							
							record = recordIterator.next();
						}

						for (Entry<String, Double> ceco : cecos.entrySet())
							a3Line.cecos.put(ceco.getKey(), total == 0 ?  0.00 : ceco.getValue() / total );

						return a3Line;
					}

					@Override
					A3Line next(
							Record11<String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, BigDecimal> record) {
						throw new UnsupportedOperationException();
					}
				};
			}
		};

		return a3Report;
	}

	static Report<FTELine> getFTEReport(DSLContext dslContext, Date start,
			Date end, Integer workplaces[]) throws SQLException {
		final Workplace HOTEL = WORKPLACE.as("hotel");
		final ContractData HOURS = CONTRACT_DATA.as("hours");
		final ContractData RRHH_CODE = CONTRACT_DATA.as("rrhh_code");

		//@formatter:off
		Cursor<Record> cursor =  dslContext.select()
			.from(REGISTRY)
			.join(CONTRACT).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.join(HOTEL).on(CONTRACT.WORKPLACE.eq(HOTEL.ID))
			.join(HOURS).on(CONTRACT.ID.eq(HOURS.CONTRACT)
					.and(HOURS.NAME.like("DESEMPE%O"))
					.and(HOURS.START_DATE.between(start, end)))
					.and(HOURS.EXPRESSION.in(new String []{"4","8","10","12"}))
			.join(RRHH_CODE).on(CONTRACT.ID.eq(RRHH_CODE.CONTRACT)
					.and(RRHH_CODE.NAME.eq("CODIGO RRHH")))
			.where(HOTEL.ID.in(workplaces))
			.fetchLazy();
		//@formatter:on

		return new AbstractReport<Record, FTELine>(cursor) {

			@Override
			Iterator<FTELine> iterator(Iterator<Record> recordIterator) {

				return new AbstractLineIterator<Record, FTELine>(recordIterator) {

					@Override
					FTELine next(Record record) {
						FTELineImpl fteLine = new FTELineImpl();
						fteLine.hotel = record.getValue(HOTEL.DESCRIPTION);

						String rrhhCode = record.getValue(RRHH_CODE.EXPRESSION);

						fteLine.section = rrhhCode.substring(5, 8);

						fteLine.job = rrhhCode.substring(8);

						fteLine.day = record.getValue(HOURS.START_DATE);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(fteLine.day);
						fteLine.week = calendar.get(Calendar.WEEK_OF_YEAR);
						fteLine.person = record.getValue(REGISTRY.NAME);
						fteLine.closed = true;
						fteLine.hours = Short.valueOf(record
								.getValue(HOURS.EXPRESSION));
						fteLine.staff = 1;
						fteLine.realStaff = fteLine.hours / 8;

						return fteLine;
					}

				};
			}

		};

	}

	static Report<HolidayLine> getHolidayReport(DSLContext dslContext,
			Date start, Date end, Integer workplaces[]) throws SQLException {
		final Workplace HOTEL = WORKPLACE.as("hotel");
		final ContractData RRHH_CODE = CONTRACT_DATA.as("rrhh_code");
		final ContractData PERFORMANCE = CONTRACT_DATA.as("performance");

		//@formatter:off
		Cursor<Record> cursor =  dslContext.select()
			.from(REGISTRY)
			.join(CONTRACT).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.join(HOTEL).on(CONTRACT.WORKPLACE.eq(HOTEL.ID))
			.join(PERFORMANCE).on(CONTRACT.ID.eq(PERFORMANCE.CONTRACT)
					.and(PERFORMANCE.NAME.like("DESEMPE%O"))
					.and(PERFORMANCE.START_DATE.between(start, end)))
			.join(RRHH_CODE).on(CONTRACT.ID.eq(RRHH_CODE.CONTRACT)
					.and(RRHH_CODE.NAME.eq("CODIGO RRHH")))
			.where(HOTEL.ID.in(workplaces))
			.fetchLazy();
		//@formatter:on

		return new AbstractReport<Record, HolidayLine>(cursor) {

			@Override
			Iterator<HolidayLine> iterator(Iterator<Record> recordIterator) {

				return new AbstractLineIterator<Record, HolidayLine>(
						recordIterator) {

					@Override
					HolidayLine next(Record record) {
						HolidayLineImpl holidayLine = new HolidayLineImpl();
						holidayLine.hotel = record.getValue(HOTEL.DESCRIPTION);

						String rrhhCode = record.getValue(RRHH_CODE.EXPRESSION);

						holidayLine.section = rrhhCode.substring(5, 8);

						holidayLine.job = rrhhCode.substring(8);

						holidayLine.day = record
								.getValue(PERFORMANCE.START_DATE);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(holidayLine.day);
						holidayLine.week = calendar.get(Calendar.WEEK_OF_YEAR);
						holidayLine.person = record.getValue(REGISTRY.NAME);
						holidayLine.closed = true;

						String performance = record
								.getValue(PERFORMANCE.EXPRESSION);
						if (StringUtils.equalsIgnoreCase("V", performance))
							holidayLine.v = 1;
						else if (StringUtils
								.equalsIgnoreCase("FT", performance))
							holidayLine.ft = 1;
						else if (StringUtils
								.equalsIgnoreCase("FR", performance))
							holidayLine.fr = 1;
						else if (StringUtils
								.equalsIgnoreCase("LT", performance))
							holidayLine.lt = 1;
						else if (StringUtils
								.equalsIgnoreCase("LL", performance))
							holidayLine.ll = 1;
						else if (StringUtils.equals("4", performance))
							holidayLine.he = 4;
						else if (StringUtils.equals("8", performance))
							holidayLine.he = 8;
						else if (StringUtils.equals("10", performance))
							holidayLine.he = 10;
						else if (StringUtils.equals("12", performance))
							holidayLine.he = 12;

						return holidayLine;
					}

				};
			}

		};

	}

	// ------------------------------------------------------------------------

	private static Integer[] box(int original[]) {
		Integer target[] = new Integer[original.length];
		for (int i = 0; i < target.length; i++)
			target[i] = original[i];
		return target;
	}

	private static double doubleValue(BigDecimal bigDecimal) {
		return bigDecimal == null ? 0.00 : bigDecimal.doubleValue();
	}

	// ------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/grupoplayasol-com", "aon", "40n");
		Calendar current = Calendar.getInstance();
		current.set(Calendar.DAY_OF_MONTH, 1);

		Date start = new Date(current.getTimeInMillis());

		current.set(Calendar.DAY_OF_MONTH,
				current.getMaximum(Calendar.DAY_OF_MONTH));
		Date end = new Date(current.getTimeInMillis());

		Integer workplacesIds[] = DSL.using(connection, getDefaultSettings())
				.select().from(WORKPLACE).fetchArray(WORKPLACE.ID);

		Report<A3Line> a3Report = getA3Report(connection, start, end,
				workplacesIds);

		for (A3Line line : a3Report) {
			System.out.printf("%s,%s,%f,%f,%f,%f", line.getNIF(),
					line.getPerson(), line.getDelays(), line.getEmbargos(),
					line.getIncentives(), line.getTurnPlus());
			for ( Entry<String, Double> ceco : line.getCECOs().entrySet() ) 
				System.out.printf(",%s,%f",ceco.getKey(), ceco.getValue() );
			System.out.println();
		}

	}
}
