package com.esferalia.aon.gwt.payroll.server.gps;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formattable;
import java.util.Formatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.esferalia.aon.gwt.payroll.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.gps.ReportConstants;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.google.gwt.rpc.server.Pair;

public class ReportServlet extends HttpServlet implements ReportConstants {

	private static final char DEFAULT_SEP = '|';
	private static final String DEFAULT_CHARSET = "ISO-8859-1";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			DATE_FORMAT_PATTERN);

	static class StringFormattable implements Formattable{
		private String str;
		
		@Override
		public void formatTo(Formatter formatter, int flags, int width,
				int precision) {
		}		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		printSessionNames(req.getSession());
		OutputStreamWriter out = null;
		try {
			AonServletUtils.initFacesContext(getServletContext(), req, resp);
			Date startDate = getDateParam(req, START_DATE_PARAM);
			Date endDate = getDateParam(req, END_DATE_PARAM);
			int workplaces[] = getIntParams(req, WORKPLACE_PARAM);

			resp.setContentType("text/csv");
			resp.setCharacterEncoding(DEFAULT_CHARSET);

			out = new OutputStreamWriter(resp.getOutputStream(),
					Charset.forName(DEFAULT_CHARSET));

			doFTE(startDate, endDate, workplaces, out);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (ParseException e) {
			throw new ServletException(e);
		} finally {
			if (out != null)
				out.close();
			AonServletUtils.releaseFacesContext();
		}
	}

	private void doFTE(Date startDate, Date endDate, int workplaces[],
			Writer out) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		PrintWriter printOut = null;
		Connection conn  = null;
		

		try {
			conn = AonServletUtils.getConnection();

			String sql = "SELECT" + " " 
					+ SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID + ", "
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID + ", "
					+ SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.START_DATE + ", "
					
					+ SQLConstants.WORKPLACE + "." + WorkplaceColumns.DESCRIPTION + ", "
					+ SQLConstants.PERSON + "." + PersonColumns.FIRST_SURNAME + ", "
					+ SQLConstants.PERSON + "." + PersonColumns.SECOND_SURNAME + ", " 
					+ SQLConstants.PERSON + "." + PersonColumns.NAME + ", " 
					+ SQLConstants.CONTRACT + "." + ContractColumns.DESCRIPTION + ", " 
					+ SQLConstants.CONTRACT + "." + ContractColumns.CATEGORY_DESCRIPTION + ", "
					+ SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.END_DATE + ", "
					+ SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.EXPRESSION 

					+ " FROM "
					+ SQLConstants.WORKPLACE + ", " 
					+ SQLConstants.CONTRACT + ", " 
					+ SQLConstants.PERSON + ", "
					+ SQLConstants.CONTRACT_DATA 
					+ " WHERE " + SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID + " = " + SQLConstants.CONTRACT + "." + ContractColumns.WORKPLACE
					+ " AND " + SQLConstants.CONTRACT + "." + ContractColumns.ID + " = " + SQLConstants.CONTRACT_DATA + "." +  ContractDataColumns.CONTRACT 
					+ " AND " + SQLConstants.PERSON + "." + PersonColumns.REGISTRY + " = " + SQLConstants.CONTRACT + "." + ContractColumns.PERSON
					+ " AND " + SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.NAME + " = ?" 
					+ " AND " + SQLConstants.CONTRACT_DATA + "." +  ContractDataColumns.EXPRESSION + " IN ( 4, 8, 10, 12 )"
					+ " AND " + SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.START_DATE + " <= ?"
					+ " AND ( " + SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.END_DATE + " IS NULL "
					+ " OR " + SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.END_DATE + " >= ? )"
					+ " AND " + SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID + " IN ( " + "?" + StringUtils.repeat(",?", workplaces.length - 1) + ")"
					+ " ORDER BY 1,2,3";

			
			System.out.println(sql);
			
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, "FTE");
			stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			stmt.setDate(3, new java.sql.Date(startDate.getTime()));
			for (int i = 0; i < workplaces.length; i++) {
				stmt.setInt(i + 4, workplaces[i]);
			}

			rs = stmt.executeQuery();

			printOut = new PrintWriter(out, true);
			printOut.printf(
					"HOTEL%1$cDEPARTAMENTO%1$cPUESTO%1$cSEMANA%1$cFECHA%1$cPERSONA%1$cCIERRE%1$cHORAS%1$cPERSONAL%1$cPERSONAL EFECTIVO\r\n",
					DEFAULT_SEP);
			while (rs.next()) {

				String hotel = rs.getString(SQLConstants.WORKPLACE + "."
						+ WorkplaceColumns.DESCRIPTION);
				String section = rs.getString(SQLConstants.CONTRACT + "."
						+ ContractColumns.DESCRIPTION);
				String category = rs.getString(SQLConstants.CONTRACT + "."
						+ ContractColumns.CATEGORY_DESCRIPTION);

				String firstSurname = rs.getString(SQLConstants.PERSON + "."
						+ PersonColumns.FIRST_SURNAME);
				String secondSurname = rs.getString(SQLConstants.PERSON + "."
						+ PersonColumns.SECOND_SURNAME);
				String name = rs.getString(SQLConstants.PERSON + "."
						+ PersonColumns.NAME);
				StringBuffer fullName = new StringBuffer();
				if (firstSurname != null && !firstSurname.isEmpty())
					fullName.append(firstSurname);
				if (secondSurname != null && !secondSurname.isEmpty())
					fullName.append(secondSurname);
				if (name != null && !name.isEmpty())
					fullName.append((fullName.length() > 0 ? "," : "") + name);

				Date fteStartDate = rs.getDate(SQLConstants.CONTRACT_DATA + "."
						+ ContractDataColumns.START_DATE);
				Date fteEndDate = rs.getDate(SQLConstants.CONTRACT_DATA + "."
						+ ContractDataColumns.END_DATE);

				List<Pair<Date, Integer>> days = getDays(fteStartDate,
						fteEndDate);

				String fte = rs.getString(SQLConstants.CONTRACT_DATA + "."
						+ ContractDataColumns.EXPRESSION);

				int hours = Integer.parseInt(fte);

				for (Pair<Date, Integer> day : days) {

					Date date = day.getA();
					int week = day.getB();

					printOut.printf("%2$s%1$c" + "%3$s%1$c" + "%4$s%1$c"
							+ "%5$d%1$c" + "%6$td/%6$tm/%6$tY%1$c" + "%7$s%1$c"
							+ "%8$s%1$c" + "%9$d%1$c" + "%10$d%1$c" + "%11$f"
							+ "\r\n", DEFAULT_SEP, 
							hotel != null ? hotel : "", 
							section != null ? section : "", 
							category != null ? category : "",
							week, 
							date, 
							fullName, 
							"SI", 
							hours, 
							1, 
							(float)(hours / 8.00)  );

				}

			}

		} finally {
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
		}
	}

	private void doCTRL(Date startDate, Date endDate, int workplaces[], Writer out) throws SQLException{

	}

	private void doA3(Date startDate, Date endDate, int workplaces[], Writer out) throws SQLException{

	}

	private int[] getIntParams(HttpServletRequest req, String name) {
		String values[] = req.getParameterValues(name);
		int integers[] = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			integers[i] = Integer.parseInt(values[i]);
		}
		return integers;
	}

	private Date getDateParam(HttpServletRequest req, String name)
			throws ParseException {
		return DATE_FORMAT.parse(req.getParameter(name));
	}

	private boolean getBoolParam(HttpServletRequest req, String name) {
		String value = req.getParameter(name);
		return value != null && value.equalsIgnoreCase(Boolean.TRUE.toString());
	}

	private static List<Pair<Date, Integer>> getDays(Date startDate,
			Date endDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		resetTime(calendar);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		resetTime(endCalendar);

		List<Pair<Date, Integer>> days = new ArrayList<Pair<Date, Integer>>();
		while (calendar.compareTo(endCalendar) <= 0) {
			Date date = calendar.getTime();
			int week = calendar.get(Calendar.WEEK_OF_YEAR);
			// For us week starts at MONDAY...
			if ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
				week =- 1;
			
			days.add(new Pair<Date, Integer>(date, week));

			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		return days;
	}

	private static void resetTime(Calendar calendar) {
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	private static void printSessionNames (HttpSession session){
		Enumeration<String> attrs = session.getAttributeNames();
		
		while ( attrs.hasMoreElements() ) {
			String name = attrs.nextElement();
			Object attr =  session.getAttribute(name);
			System.out.printf(" %s = %s\r\n", name, attr != null ? attr.getClass().getName(): "NULL");
		}
		
	}

}
