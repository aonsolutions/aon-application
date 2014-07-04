package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.IrpfData;
import com.esferalia.aon.gwt.fiscal.shared.IrpfResult;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;

public class SQLIrpf {

	//@formatter:off
	private static final String LAST_IRPF_DATA_BY_PERSON_SELECT = "SELECT "
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.FAMILY_SITUATION + "," 
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.SPOUSE_DOCUMENT + "," 
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.DISABILITY_LEVEL + ","
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.MOVING_DATE + ","
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.LABOUR_PROLONGATION + "," 
			+ " IFNULL(" + SQLConstants.IRPF_DATA + "." + IrpfDataColumns.END_DATE + ",?) " + IrpfDataColumns.END_DATE + "," 
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.CONTRACT_TYPE + "," 
			+ SQLConstants.IRPF_DATA + "." + IrpfDataColumns.CEUTA_MELILLA 
			+ " FROM " + SQLConstants.IRPF_DATA 
			+ " INNER JOIN " + SQLConstants.CONTRACT + " ON " 
				+ SQLConstants.CONTRACT + "." + ContractColumns.ID + " = " + SQLConstants.IRPF_DATA + "." + IrpfDataColumns.CONTRACT
			+ " WHERE (" + SQLConstants.IRPF_DATA + "." + IrpfDataColumns.END_DATE + " IS NULL" 
				+ " OR " + SQLConstants.IRPF_DATA + "." + IrpfDataColumns.END_DATE + " BETWEEN ? AND ?)" 
			+ " AND " + IrpfDataColumns.CONTRACT + "." + ContractColumns.PERSON + "=?" 
			+ " ORDER BY " + IrpfDataColumns.END_DATE + " DESC" + " LIMIT 1";

	private static final String LAST_IRPF_RESULT_BY_PERSON_SELECT =
			"SELECT " + SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ID 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DOMAIN 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.CONTRACT 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.EFFECTIVE_DATE 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.BASE_IRPF 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.MINIMUN_PERSONAL_FAMILY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DEDUCT_HOME_LOAN_AMOUNT 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DEDUCT_80_BIS 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.IRPF 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ANNUAL_IRPF 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ANNUAL_REMUNERATION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.IRREGULAR_18_2_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.IRREGULAR_18_3_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DEDUCCIBLES_EXPENSES 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.WORK_REMUNERATION_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.WORK_PROLONGATION_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.WORK_MOVING_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.WORK_DISABILITY_REDUCTION 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.SOCIAL_SECURITY_PENSIONER 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.TWO_OR_MORE_DESCENDENTS_MIN 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.SPOUSAL_SUPPORT 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.FOOD_ANNUITY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.MINIMUN_PERSONAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.MINIMUN_ASCENDENTS 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.MINIMUN_DESCENDENTS 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.MINIMUN_DISABILITY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_MINOR_3_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_MINOR_3_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_REMAINDER_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_REMAINDER_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_33_65_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_33_65_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_MOVING_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_MOVING_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_65_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_65_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_FIRST 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_SECOND 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_THIRD 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_FOURTH_SUBSEQUENT_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.DESCENDENTS_FOURTH_SUBSEQUENT_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MINOR_75_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MINOR_75_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MAYOR_75_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MAYOR_75_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_33_65_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_33_65_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MOVING_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_MOVING_ENTIRELY 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_65_TOTAL 	
			+","+ SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.ASCENDENTS_65_ENTIRELY 	
			+ " FROM " + SQLConstants.IRPF_RESULT
			+ " INNER JOIN " + SQLConstants.CONTRACT  + " ON " 
				+ SQLConstants.CONTRACT + "." + ContractColumns.ID 
				+ " = " + SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.CONTRACT
			+ " WHERE " + SQLConstants.IRPF_RESULT + "." + IrpfResultColumns.EFFECTIVE_DATE + " BETWEEN ? AND ?"
			+ " AND " + IrpfResultColumns.CONTRACT + "." + ContractColumns.PERSON + "=?"
			+ " ORDER BY " + IrpfResultColumns.EFFECTIVE_DATE + " DESC"
			+ " LIMIT 1";
	//@formatter:on

	public static PreparedStatement getLastIrpfDataByPersonStatement(
			Connection c) throws SQLException {
		return c.prepareStatement(LAST_IRPF_DATA_BY_PERSON_SELECT,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	public static ResultSet getLastIrpfDataByPersonResultSet(
			PreparedStatement stmt, int person, Date fromDate, Date toDate)
			throws SQLException {
		stmt.setDate(1, new java.sql.Date(fromDate.getTime()));
		stmt.setDate(2, new java.sql.Date(fromDate.getTime()));
		stmt.setDate(3, new java.sql.Date(toDate.getTime()));
		stmt.setInt(4, person);
		return stmt.executeQuery();
	}

	public static void populateIrpfData(ResultSet irpfDataRs, IrpfData irpfData)
			throws SQLException {
		if (irpfData == null) {
			throw new IllegalArgumentException("IrpfData no puede ser null");
		}
		if (irpfDataRs.next()) {
			irpfData.setCeutaMelilla(irpfDataRs
					.getBoolean(IrpfDataColumns.CEUTA_MELILLA));
			irpfData.setFamilySituation(irpfDataRs
					.getInt(IrpfDataColumns.FAMILY_SITUATION));
			irpfData.setSpouseDocument(irpfDataRs
					.getString(IrpfDataColumns.SPOUSE_DOCUMENT));
			int disabilityLevel = irpfDataRs
					.getInt(IrpfDataColumns.DISABILITY_LEVEL);
			if (!irpfDataRs.wasNull()) {
				disabilityLevel += 1;
			}
			irpfData.setDisability(disabilityLevel);
			irpfData.setContract(irpfDataRs
					.getInt(IrpfDataColumns.CONTRACT_TYPE) + 1);
			irpfData.setWorkActivityExtension(irpfDataRs
					.getBoolean(IrpfDataColumns.LABOUR_PROLONGATION));
			irpfData.setGeographicMobility(irpfDataRs
					.getDate(IrpfDataColumns.MOVING_DATE) != null);
		}
	}

	public static PreparedStatement getLastIrpfResultByPersonStatement(
			Connection c) throws SQLException {
		return c.prepareStatement(LAST_IRPF_RESULT_BY_PERSON_SELECT,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	public static ResultSet getLastIrpfResultByPersonResultSet(
			PreparedStatement stmt, int person, Date fromDate, Date toDate)
			throws SQLException {
		stmt.setDate(1, new java.sql.Date(fromDate.getTime()));
		stmt.setDate(2, new java.sql.Date(toDate.getTime()));
		stmt.setInt(3, person);
		return stmt.executeQuery();
	}

	public static void populateIrpfResult(ResultSet irpfResultRs,
			IrpfResult irpfResult) throws SQLException {
		if (irpfResult == null) {
			throw new IllegalArgumentException("IrpfResult no puede ser null");
		}
		if (irpfResultRs.next()) {
			double value = irpfResultRs
					.getDouble(IrpfResultColumns.IRREGULAR_18_2_REDUCTION)
					+ irpfResultRs
							.getDouble(IrpfResultColumns.IRREGULAR_18_3_REDUCTION)
					+ irpfResultRs
							.getDouble(IrpfResultColumns.WORK_REMUNERATION_REDUCTION)
					+ irpfResultRs
							.getDouble(IrpfResultColumns.WORK_PROLONGATION_REDUCTION)
					+ irpfResultRs
							.getDouble(IrpfResultColumns.WORK_MOVING_REDUCTION)
					+ irpfResultRs
							.getDouble(IrpfResultColumns.WORK_DISABILITY_REDUCTION);
			irpfResult.setApplicableReduction(AonUtil.round(value));
			irpfResult.setDeducibleExpense(irpfResultRs
					.getDouble(IrpfResultColumns.DEDUCCIBLES_EXPENSES));
			irpfResult.setCompensatoryPension(irpfResultRs
					.getDouble(IrpfResultColumns.SPOUSAL_SUPPORT));
			irpfResult.setFoodAnnuality(irpfResultRs
					.getDouble(IrpfResultColumns.FOOD_ANNUITY));
			irpfResult.setHomeLoanCommunnication(irpfResultRs
					.getDouble(IrpfResultColumns.DEDUCT_HOME_LOAN_AMOUNT) == 0);

			irpfResult.setLessThan3Descendent(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_MINOR_3_TOTAL));
			irpfResult.setLessThan3DescendentRatio(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_MINOR_3_ENTIRELY));
			irpfResult.setOtherDescendent(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_REMAINDER_TOTAL));
			irpfResult.setOtherDescendentRatio(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_REMAINDER_ENTIRELY));
			irpfResult.setDisabilityDescendent33(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_33_65_TOTAL));
			irpfResult.setDisabilityDescendent33Ratio(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_33_65_ENTIRELY));
			irpfResult.setDisabilityDescendentDependence(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_MOVING_TOTAL));
			irpfResult.setDisabilityDescendentDependenceRatio(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_MOVING_ENTIRELY));
			irpfResult.setDisabilityDescendent65(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_65_TOTAL));
			irpfResult.setDisabilityDescendent65Ratio(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_65_ENTIRELY));
			irpfResult.setLessThan75Ascendant(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MINOR_75_TOTAL));
			irpfResult.setLessThan75AscendantRatio(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MINOR_75_ENTIRELY));
			irpfResult.setAscendant(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MAYOR_75_TOTAL));
			irpfResult.setAscendantRatio(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MAYOR_75_ENTIRELY));
			irpfResult.setDisabilityAscendant33(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_33_65_TOTAL));
			irpfResult.setDisabilityAscendant33Ratio(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_33_65_ENTIRELY));
			irpfResult.setDisabilityAscendantDependence(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MOVING_TOTAL));
			irpfResult.setDisabilityAscendantDependenceRatio(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_MOVING_ENTIRELY));
			irpfResult.setDisabilityAscendant65(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_65_TOTAL));
			irpfResult.setDisabilityAscendant65Ratio(irpfResultRs
					.getInt(IrpfResultColumns.ASCENDENTS_65_ENTIRELY));
			irpfResult.setFirstChildCalculation(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_FIRST));
			irpfResult.setSecondChildCalculation(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_SECOND));
			irpfResult.setThirdChildCalculation(irpfResultRs
					.getInt(IrpfResultColumns.DESCENDENTS_THIRD));
		}
	}

}