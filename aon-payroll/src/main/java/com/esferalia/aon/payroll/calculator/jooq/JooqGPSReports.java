package com.esferalia.aon.payroll.calculator.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.Workplace;

public class JooqGPSReports extends JooqCommon {

	public static interface Line {

	}

	public static interface A3Line extends Line {
		String getNIF();
		String getPerson();
		int getTurnPlus();
		int getDelays();
		int getIncentives();
		int getEmbargos();
		String getComments();
		
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
			return next(recordIterator.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
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
		private int turnPlus;
		private int delays;
		private int incentives;
		private int embargos;
		private String comments;

		@Override
		public String getNIF() {
			return nif;
		}

		@Override
		public String getPerson() {
			return person;
		}

		@Override
		public int getTurnPlus() {
			return turnPlus;
		}

		@Override
		public int getDelays() {
			return delays;
		}

		@Override
		public int getIncentives() {
			return incentives;
		}

		@Override
		public int getEmbargos() {
			return embargos;
		}

		@Override
		public String getComments() {
			return comments;
		}
		
	}


	// ------------------------------------------------------------------------

	public static Report<A3Line> getA3Report(Connection connection, Date month,
			int workplaces[]) throws SQLException {
		return getA3Report(connection, month,
				box(workplaces));
	}

	public static Report<A3Line>  getA3Report(Connection connection, Date month,
			Integer workplaces[]) throws SQLException {
		return getA3Report(DSL.using(connection, getDefaultSettings()), month,
				workplaces);
	}

	public static Report<FTELine> getFTEReport(Connection connection,
			Date start, Date end, int workplaces[]) throws SQLException {

		return getFTEReport(DSL.using(connection, getDefaultSettings()), start,
				end, box(workplaces));
	}

	public static Report<HolidayLine> getHolidayReport(Connection connection,
			Date start, Date end, Integer workplaces[]) throws SQLException {
		return getHolidayReport(DSL.using(connection, getDefaultSettings()), start,
				end, workplaces);
	}

	public static Report<HolidayLine> getHolidayReport(Connection connection,
			Date start, Date end, int workplaces[]) throws SQLException {

		return getHolidayReport(DSL.using(connection, getDefaultSettings()), start,
				end, box(workplaces));
	}

	// ------------------------------------------------------------------------

	static Report<A3Line> getA3Report(DSLContext dslContext, Date month,
			Integer workplaces[]) throws SQLException {

		//@formatter:off
		Cursor<Record> cursor =  dslContext.select()
			.from(REGISTRY)
			.join(CONTRACT).on(REGISTRY.ID.eq(CONTRACT.PERSON))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.where(WORKPLACE.ID.in(workplaces))
			.groupBy(REGISTRY.DOCUMENT, REGISTRY.NAME)
			.fetchLazy();
		//@formatter:on
		return new AbstractReport<Record, A3Line>(cursor) {

			@Override
			Iterator<A3Line> iterator(Iterator<Record> recordIterator) {

				return new AbstractLineIterator<Record, A3Line>(recordIterator) {

					@Override
					A3Line next(Record record) {
						A3LineImpl a3Line = new A3LineImpl();
						
						a3Line.nif = record.getValue(REGISTRY.DOCUMENT);
						a3Line.person = record.getValue(REGISTRY.NAME);
						
						return a3Line;
					}

				};
			}

		};
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

	static Report<HolidayLine> getHolidayReport(DSLContext dslContext, Date start,
			Date end, Integer workplaces[]) throws SQLException {
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

				return new AbstractLineIterator<Record, HolidayLine>(recordIterator) {

					@Override
					HolidayLine next(Record record) {
						HolidayLineImpl holidayLine = new HolidayLineImpl();
						holidayLine.hotel = record.getValue(HOTEL.DESCRIPTION);

						String rrhhCode = record.getValue(RRHH_CODE.EXPRESSION);

						holidayLine.section = rrhhCode.substring(5, 8);

						holidayLine.job = rrhhCode.substring(8);

						holidayLine.day = record.getValue(PERFORMANCE.START_DATE);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(holidayLine.day);
						holidayLine.week = calendar.get(Calendar.WEEK_OF_YEAR);
						holidayLine.person = record.getValue(REGISTRY.NAME);
						holidayLine.closed = true;
						
						String performance = record.getValue(PERFORMANCE.EXPRESSION);
						if ( StringUtils.equalsIgnoreCase("V", performance))
							holidayLine.v = 1;
						else if ( StringUtils.equalsIgnoreCase("FT", performance))
							holidayLine.ft = 1;
						else if ( StringUtils.equalsIgnoreCase("FR", performance))
							holidayLine.fr = 1;
						else if ( StringUtils.equalsIgnoreCase("LT", performance))
							holidayLine.lt = 1;
						else if ( StringUtils.equalsIgnoreCase("LL", performance))
							holidayLine.ll = 1;
						else if ( StringUtils.equals("4", performance) )
							holidayLine.he = 4;
						else if ( StringUtils.equals("8", performance) )
							holidayLine.he = 8;
						else if ( StringUtils.equals("10", performance) )
							holidayLine.he = 10;
						else if ( StringUtils.equals("12", performance) )
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

}
