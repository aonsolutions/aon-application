package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.code.aon.common.enumeration.Month;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.payroll.shared.Statistics;

public class SQLStatistics {

	private static String[] months = new String[12];

	/**
	 * Lleno el objeto de la clase Statistics llamando a metodos privados de la
	 * clase
	 * 
	 * @param conn
	 *            La conexion a la base de datos
	 * @param enterpriseId
	 *            El Id de la empresa
	 * @return La instancia statistics
	 */
	public static Statistics getEnterpriseStats(Connection conn,
			int enterpriseId) {
		Statistics statistics = new Statistics();


		try {

			Locale locale = AonUtil.getCurrentLocale();

			for (int i = 0; i < 12; i++) {
				// pStatistics.setMonthName(i,
				// Month.getMonthByValue(i).getName(locale));
				months[i] = new String(Month.getMonthByValue(i).getName(locale));
			}

			getNumYearsEnterprise(conn, enterpriseId, statistics);

			// Obtengo todos los años donde hay registros de la empresa
			getYearsEnterprise(conn, enterpriseId, statistics);

			for (int x = 0; x < statistics.getStatisticYears().size(); x++) {

				int year = statistics.getStatisticYears().get(x).getYear();
				getStatisticsIterator(conn, statistics, year, enterpriseId, x);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}

		return statistics;
	}

	/**
	 * Obtengo los datos de la empresa pasandole el año
	 * 
	 * @param pConn
	 *            La conexion
	 * @param pStatistics
	 *            La instancia a la clase Statistics
	 * @param pYear
	 *            el año a buscar
	 * @param pEnterpriseId
	 *            ID de la Empresa
	 * @param pIndex
	 *            La posicion donde guardar el dato en el array Years de
	 *            Statistics
	 * @return
	 */
	private static void getStatisticsIterator(Connection pConn,
			Statistics pStatistics, int pYear, int pEnterpriseId, int pIndex) {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String select = "SELECT month(salary.end_date),"
					+ " year(salary.end_date),"
					+ " sum(salary.total_liquid), "
					+ " sum(salary.total_irpf), "
					+ "sum(salary.social_security_contributions), "
					+ "sum(salary.total_payment), "
					+ "sum(salary.total_enterprise), "
					+ "salary.contract, "
					+ "contract.workplace, "
					+ "workplace.enterprise "
					+ "FROM salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "
					+ "inner join enterprise on workplace.enterprise = enterprise.registry "
					+ "WHERE workplace.enterprise = ? "
					+ "and year(salary.end_date)=? " + "group by 1";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pEnterpriseId);
			stmt.setInt(2, pYear);

			rs = stmt.executeQuery();

			while (rs.next()) {
				int month = rs.getInt(1) - 1;
				double liquid = rs.getDouble(3);
				double irpf = rs.getDouble(4);
				double ssEmployee = rs.getDouble(5);
				double totalPayment = rs.getDouble(6);
				double ssEnterprise = rs.getDouble(7);
				double concepts = totalPayment - (liquid + irpf + ssEmployee);

				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setMonth(month);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setMonthName(months[month]);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setLiquid(liquid);
				pStatistics.getStatisticYears().get(pIndex).setTotalLiquid(liquid);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setIrpf(irpf);
				pStatistics.getStatisticYears().get(pIndex).setTotalIRPF(irpf);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setSSEmployee(ssEmployee);
				pStatistics.getStatisticYears().get(pIndex).setTotalSSEmployee(ssEmployee);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setTotalPayment(totalPayment);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setSSEnterprise(ssEnterprise);
				pStatistics.getStatisticYears().get(pIndex).setTotalSSEnterprise(ssEnterprise);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setOtros(concepts);
				pStatistics.getStatisticYears().get(pIndex).setTotalConcepts(concepts);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
		// return pStatistics;
	}

	/**
	 * Obtengo todos los años donde hay datos de la empresa e inicializo el
	 * LinkedList con el numero de años que hay en la empresa
	 * 
	 * @param pConn
	 *            La conexion a la base de datos
	 * @param pEnterpriseId
	 *            El Id de la empresa
	 * @param statistics
	 *            La instancia a la clase Statistics
	 */
	private static void getYearsEnterprise(Connection pConn, int pEnterpriseId,
			Statistics statistics) {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		int contador = 0;

		try {

			/*
			 * Obtengo los años donde hay datos
			 */
			String select = "SELECT year(salary.end_date) as year "
					+ "From salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "
					+ "inner join enterprise on workplace.enterprise = enterprise.registry "
					+ "WHERE workplace.enterprise = ? " + "group by year "
					+ "order by year asc";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pEnterpriseId);

			rs = stmt.executeQuery();

			while (rs.next()) {
				int year = rs.getInt(1);
				statistics.addYear(contador, year);
				contador++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
	}

	/**
	 * Obtengo el numero de años en los que hay datos en la empresa
	 * 
	 * @param pConn
	 *            La conexion a la base de datos
	 * @param pEnterpriseId
	 *            El Id de la empresa
	 * @param statistics
	 *            La instancia a la clase Statistics
	 */
	private static void getNumYearsEnterprise(Connection pConn,
			int pEnterpriseId, Statistics statistics) {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {

			String select = "Select "
					+ "Count(distinct year(salary.end_date)) "
					+ "From salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "
					+ "inner join enterprise on workplace.enterprise = enterprise.registry "
					+ "WHERE workplace.enterprise = ?";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pEnterpriseId);

			rs = stmt.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
		statistics.initializedListYears(count);
	}
	
	//******************* WORKPLACE *********************
	//***************************************************

	public static Statistics getWorkplaceStats(Connection conn, int workplaceId) {

		Statistics statistics = new Statistics();

		try {

			Locale locale = AonUtil.getCurrentLocale();

			for (int i = 0; i < 12; i++) {
				// pStatistics.setMonthName(i,
				// Month.getMonthByValue(i).getName(locale));
				months[i] = new String(Month.getMonthByValue(i).getName(locale));
			}

			// Numero de años donde hay registros
			getNumYearsWorkplace(conn, workplaceId, statistics);

			// Obtengo todos los años donde hay registros de la empresa
			getYearsWorkplace(conn, workplaceId, statistics);

			for (int x = 0; x < statistics.getStatisticYears().size(); x++) {

				int year = statistics.getStatisticYears().get(x).getYear();
				getWorkplaceStatisticsIterator(conn, statistics, year, workplaceId, x);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}

		return statistics;

	}

	private static void getNumYearsWorkplace(Connection pConn,
			int pWorkplaceId, Statistics statistics) {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {

			String select = "Select "
					+ "Count(distinct year(salary.end_date)) "
					+ "From salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "
					+ "WHERE workplace.id = ?";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pWorkplaceId);

			rs = stmt.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
		statistics.initializedListYears(count);
	}

	private static void getYearsWorkplace(Connection pConn, int pWorkplaceId,
			Statistics statistics) {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		int contador = 0;

		try {

			/*
			 * Obtengo los años donde hay datos
			 */
			String select = "SELECT year(salary.end_date) as year "
					+ "From salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "
					+ "WHERE workplace.id = ? " + "group by year "
					+ "order by year asc";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pWorkplaceId);

			rs = stmt.executeQuery();

			while (rs.next()) {
				int year = rs.getInt(1);
				statistics.addYear(contador, year);
				contador++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
	}
	
	private static void getWorkplaceStatisticsIterator(Connection pConn,
			Statistics pStatistics, int pYear, int pWorkplaceId, int pIndex) {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		

		try {

			String select = "SELECT month(salary.end_date),"
					+ " year(salary.end_date),"
					+ " sum(salary.total_liquid), "
					+ " sum(salary.total_irpf), "
					+ "sum(salary.social_security_contributions), "
					+ "sum(salary.total_payment), "
					+ "sum(salary.total_enterprise) "					
					+ "FROM salary "
					+ "inner join contract on salary.contract = contract.id "
					+ "inner join workplace on contract.workplace = workplace.id "					
					+ "WHERE workplace.id = ? "
					+ "and year(salary.end_date)=? " + "group by 1";

			stmt = pConn.prepareStatement(select);
			stmt.setInt(1, pWorkplaceId);
			stmt.setInt(2, pYear);

			rs = stmt.executeQuery();

			while (rs.next()) {
				int month = rs.getInt(1) - 1;
				double liquid = rs.getDouble(3);
				double irpf = rs.getDouble(4);
				double ssEmployee = rs.getDouble(5);
				double totalPayment = rs.getDouble(6);
				double ssEnterprise = rs.getDouble(7);
				double concepts = totalPayment - (liquid + irpf + ssEmployee);

				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setMonth(month);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setMonthName(months[month]);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setLiquid(liquid);
				pStatistics.getStatisticYears().get(pIndex).setTotalLiquid(liquid);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setIrpf(irpf);
				pStatistics.getStatisticYears().get(pIndex).setTotalIRPF(irpf);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setSSEmployee(ssEmployee);
				pStatistics.getStatisticYears().get(pIndex).setTotalSSEmployee(ssEmployee);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setTotalPayment(totalPayment);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setSSEnterprise(ssEnterprise);
				pStatistics.getStatisticYears().get(pIndex).setTotalSSEnterprise(ssEnterprise);
				pStatistics.getStatisticYears().get(pIndex).getStatsData(month)
						.setOtros(concepts);
				pStatistics.getStatisticYears().get(pIndex).setTotalConcepts(concepts);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
		// return pStatistics;
	}

}
