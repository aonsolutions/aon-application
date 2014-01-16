package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.IrpfData;
import com.esferalia.aon.gwt.fiscal.shared.IrpfResult;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.FsModel190Columns;
import com.esferalia.aon.payroll.sql.SQLConstants.FsModel190DetailColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceDetailColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceTaxColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RaddressColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;

public class SQLMod190 {
	private static Logger LOGGER = Logger.getLogger(SQLMod190.class.getName());

	//@formatter:off
	private static String VALIDATE_MOD190 = "SELECT 1 FROM " + SQLConstants.FS_MODEL190
			+ " WHERE " + SQLConstants.FS_MODEL190 + "." + FsModel190Columns.YEAR + " = ? "
			+ " AND " + SQLConstants.FS_MODEL190 + "." + FsModel190Columns.ENTERPRISE + " = ? "
			+ " AND " + SQLConstants.FS_MODEL190 + "." + FsModel190Columns.REPLACEMENT + " = ? ";
			
	private static String SELECT_INVOICE = "SELECT "
			+ SQLConstants.INVOICE + "." + InvoiceColumns.RDOCUMENT + ","
			+ SQLConstants.INVOICE + "." + InvoiceColumns.RNAME + ","
			+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.WITHHOLDING_TYPE + " " + InvoiceTaxColumns.WITHHOLDING_TYPE +","
			+ " MIN( " + SQLConstants.INVOICE + "." + InvoiceColumns.REGISTRY + ") " + InvoiceColumns.REGISTRY+ ","
			+ " SUM( " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.BASE + ") " + InvoiceTaxColumns.BASE + ","
			+ " SUM(IF( " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.QUOTA + "!= 0," 
					+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.QUOTA
					+ ",ROUND(" + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.BASE
						+ " * " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.PERCENTAGE
						+ " / 100, 2) ) ) " + InvoiceTaxColumns.QUOTA 
			+" FROM " + SQLConstants.INVOICE
			+" INNER JOIN "+SQLConstants.INVOICE_DETAIL
			+ " ON " + SQLConstants.INVOICE + "." + InvoiceColumns.ID 
			+ " = " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.INVOICE 
			+" INNER JOIN "+SQLConstants.INVOICE_TAX
			+" ON " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.ID
			+ " = " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.INVOICE_DETAIL
			+" WHERE "+ SQLConstants.INVOICE + "." + InvoiceColumns.DOMAIN +" = ?"
			// No Ventas
			+" AND "+ SQLConstants.INVOICE + "." + InvoiceColumns.TYPE +" != 1 "
			// IRPF
			+" AND "+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.TAX_TYPE + " = 2" 
			// IRPF de profesionales, agricultores y transportistas
			+" AND "+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.WITHHOLDING_TYPE + " IN (0,3,4)"
			+" AND "+ SQLConstants.INVOICE + "." + InvoiceColumns.ISSUE_DATE +" BETWEEN ? AND ?"
			+" GROUP BY "
				+ SQLConstants.INVOICE + "." + InvoiceColumns.RDOCUMENT + ","
				+ SQLConstants.INVOICE + "." + InvoiceColumns.RNAME + ","
				+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.WITHHOLDING_TYPE;
	
	private static String GEOZONE_SELECT = "SELECT "
		+ SQLConstants.GEOZONE + "." + GeozoneColumns.CODE + " " + GeozoneColumns.CODE
		+ " FROM " + SQLConstants.RADDRESS + "," + SQLConstants.GEOZONE 
		+ " WHERE "
			+ SQLConstants.RADDRESS + "." + RaddressColumns.GEOZONE + " = "
			+ SQLConstants.GEOZONE + "." + GeozoneColumns.ID	
			+" AND " + SQLConstants.RADDRESS + "." + RaddressColumns.REGISTRY + " =  ?"
			+" AND " + SQLConstants.RADDRESS + "." + RaddressColumns.TYPE + " =  0";

	private static String MOD190_SALARY_SELECT = "SELECT "
			+ SQLConstants.SALARY + "." + SalaryColumns.EMPLOYEE_DOCUMENT 
			+ ", " + SQLConstants.SALARY + "." + SalaryColumns.EMPLOYEE_NAME 
			+ ", " + "SUM(" + SQLConstants.SALARY + "." + SalaryColumns.MONEY_IRPF_BASE + ") " + SalaryColumns.MONEY_IRPF_BASE 
			+ ", " + "SUM(" + SQLConstants.SALARY + "." + SalaryColumns.INKIND_IRPF_BASE + ") " + SalaryColumns.INKIND_IRPF_BASE 
			+ ", " + "SUM(" + SQLConstants.SALARY + "." + SalaryColumns.IRPF_BASE + ") " + SalaryColumns.IRPF_BASE 
			+ ", " + "SUM(" + SQLConstants.SALARY + "." + SalaryColumns.TOTAL_IRPF + ") " + SalaryColumns.TOTAL_IRPF
			+ ", " + SQLConstants.PERSON + "." + PersonColumns.REGISTRY + " " + PersonColumns.REGISTRY
			+ ", YEAR(" + SQLConstants.PERSON + "." + PersonColumns.BIRTH_DATE + ") " + PersonColumns.BIRTH_DATE
			+ " FROM " + SQLConstants.SALARY 
			+ " INNER JOIN " + SQLConstants.CONTRACT  + " ON " 
				+ SQLConstants.SALARY + "." + SalaryColumns.CONTRACT 
				+ " = " + SQLConstants.CONTRACT + "." + ContractColumns.ID
			+ " INNER JOIN " + SQLConstants.WORKPLACE + " ON " 
				+ SQLConstants.CONTRACT + "." + ContractColumns.WORKPLACE
				+ " = " + SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID
			+ " INNER JOIN " + SQLConstants.PERSON + " ON " 
				+ SQLConstants.CONTRACT + "." + ContractColumns.PERSON
				+ " = " + SQLConstants.PERSON + "." + PersonColumns.REGISTRY
			+ " WHERE " + SQLConstants.SALARY + "." + SalaryColumns.ISSUE_DATE + " BETWEEN ? AND ?"
			+ " AND " + SQLConstants.WORKPLACE + "." + WorkplaceColumns.ENTERPRISE + " = ?"
			+ " AND " + SQLConstants.WORKPLACE  + "." + WorkplaceColumns.ECONOMICAGREEMENT + " = ?"
			+ " GROUP BY " + SQLConstants.SALARY + "." + SalaryColumns.EMPLOYEE_DOCUMENT;

	private static final String MOD190_DELETE = "DELETE FROM " + SQLConstants.FS_MODEL190 
			+ " WHERE " + FsModel190Columns.ID + " = ?"; 
	private static final String MOD190_DETAIL_DELETE_BY_MOD190 = "DELETE FROM " + SQLConstants.FS_MODEL190_DETAIL 
			+ " WHERE " + FsModel190DetailColumns.FS_MODEL190 + " = ?"; 
	private static final String MOD190_DETAIL_DELETE = "DELETE FROM " + SQLConstants.FS_MODEL190_DETAIL 
			+ " WHERE " + FsModel190DetailColumns.ID + " = ?"; 

	private static final String MOD190_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.ID + ", " 
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.YEAR + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.ADMINISTRATION + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.REPLACEMENT + ", " 
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.DOCUMENT + ", " 
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.NAME + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.RECEIPT + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.REPLACED_RECEIPT + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.DOMAIN + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.ENTERPRISE + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.CONTACT_PERSON + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.CONTACT_PHONE + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.RECEIVER_COUNT_TOTAL + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.RECEIPT_TOTAL + ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.RETENTION_TOTAL+ ", "
			+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.COMMENTS
			+ " FROM " + SQLConstants.FS_MODEL190;

	private static final String MOD190_SELECT_ID = MOD190_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL190 + "." +FsModel190Columns.ID + " = ?";
	private static final String MOD190_SELECT_DOMAIN = MOD190_SELECT
			+ " INNER JOIN " + SQLConstants.DOMAIN
			+ " ON " + SQLConstants.FS_MODEL190 + "." +SQLConstants.FsModel190Columns.DOMAIN
			+ "=" + SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.ID
			+ " WHERE  ( " 
			+ SQLConstants.FsModel190Columns.DOMAIN + " = ? OR ( "
			+ SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " IS NOT NULL AND " +
			SQLConstants.FS_MODEL190 + "." +SQLConstants.FsModel190Columns.DOMAIN + " IN (SELECT " +
			SQLConstants.DomainColumns.ID + " FROM " + SQLConstants.DOMAIN + " WHERE " +
			SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " =  ? )))"
			+ " ORDER BY "
				+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.YEAR + " DESC, "
				+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.NAME + ", "
				+ SQLConstants.FS_MODEL190 + "." + FsModel190Columns.REPLACEMENT;

	private static final String MOD190_INSERT = "INSERT INTO " + SQLConstants.FS_MODEL190 + "( " 
			+ FsModel190Columns.DOMAIN + ", "
			+ FsModel190Columns.ENTERPRISE + ", " 
			+ FsModel190Columns.YEAR + ", " 
			+ FsModel190Columns.ADMINISTRATION + ", "
			+ FsModel190Columns.STATUS + ", "
			+ FsModel190Columns.SECURITY_LEVEL + ", "
			+ FsModel190Columns.DOCUMENT + ", " 
			+ FsModel190Columns.NAME + ", "
			+ FsModel190Columns.CONTACT_PERSON + ", "
			+ FsModel190Columns.CONTACT_PHONE + ", "
			+ FsModel190Columns.COMPLEMENTARY + ", "
			+ FsModel190Columns.REPLACEMENT + ", " 
			+ FsModel190Columns.RECEIPT + ", "
			+ FsModel190Columns.REPLACED_RECEIPT + ", "
			+ FsModel190Columns.COMMENTS + ", " 
			+ FsModel190Columns.RECEIVER_COUNT_TOTAL + ", "
			+ FsModel190Columns.RECEIPT_TOTAL + ", "
			+ FsModel190Columns.RETENTION_TOTAL
			+ " ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String MOD190_UPDATE = "UPDATE " + SQLConstants.FS_MODEL190 + " SET " 
			+ FsModel190Columns.YEAR + " = ?, " 
			+ FsModel190Columns.ADMINISTRATION + " = ?, "
			+ FsModel190Columns.STATUS + " = ?, "
			+ FsModel190Columns.SECURITY_LEVEL + " = ?, "
			+ FsModel190Columns.DOCUMENT + " = ?, " 
			+ FsModel190Columns.NAME + " = ?, " 
			+ FsModel190Columns.CONTACT_PERSON + " = ?, "
			+ FsModel190Columns.CONTACT_PHONE + " = ?, "
			+ FsModel190Columns.COMPLEMENTARY + " = ?, "
			+ FsModel190Columns.REPLACEMENT + " = ?, "
			+ FsModel190Columns.RECEIPT + " = ?, "
			+ FsModel190Columns.REPLACED_RECEIPT + " = ?, "
			+ FsModel190Columns.COMMENTS + " = ?, " 
			+ FsModel190Columns.RECEIVER_COUNT_TOTAL + " = ?, "
			+ FsModel190Columns.RECEIPT_TOTAL + " = ?, "
			+ FsModel190Columns.RETENTION_TOTAL + " = ? " + " WHERE "
			+ FsModel190Columns.ID + " = ?";

	private static final String MOD190_DETAIL_ID_NAME_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.ID + ", " 
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.NAME 
			+ " FROM " + SQLConstants.FS_MODEL190_DETAIL;
	private static final String MOD190_DETAIL_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.ID + ", " 
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.NAME + ", " 
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.PROVINCE + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DOCUMENT + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.REPRESENTATIVE_DOCUMENT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.KEY + ", " 
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.SUBKEY + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.PERCEPTION + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.RETENTION + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.IN_KIND_PERCEPTION + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.IN_KIND_DEPOSIT + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.IN_KIND_OUTPUT_DEPOSIT + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.ACCRUAL_YEAR + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.CEUTA_MELILLA + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.BIRTH_YEAR + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.FAMILY_SITUATION + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.SPOUSE_DOCUMENT + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.CONTRACT + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.LABOUR_PROLONGATION + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.GEOGRAPHIC_MOBILITY + ", "
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.APPLICABLE_REDUCTION + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DEDUCIBLE_EXPENSES + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.SPOUSAL_SUPPORT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.FOOD_ANNUITY + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.LESS_THAN_3_DESCENDENT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.LESS_THAN_3_DESCENDENT_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.OTHER_DESCENDENT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.OTHER_DESCENDENT_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_33 + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_33_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_65 + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_DESCENDENT_65_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.LESS_THAN_75_ASCENDANT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.LESS_THAN_75_ASCENDANT_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.ASCENDANT + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.ASCENDANT_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_33 + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_33_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_65 + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.DISABILITY_ASCENDANT_65_RATIO + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.FIRST_CHILD_CALCULATION + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.SECOND_CHILD_CALCULATION + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.THIRD_CHILD_CALCULATION + ", "	
			+ SQLConstants.FS_MODEL190_DETAIL + "." + FsModel190DetailColumns.HOME_LOAN_COMMUNNICATION 	
			+ " FROM " + SQLConstants.FS_MODEL190_DETAIL;
	private static final String MOD190_DETAIL_SELECT_ID = MOD190_DETAIL_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL190_DETAIL + "." +FsModel190DetailColumns.ID + " = ?";
	private static final String MOD190_RECEIVER_SELECT_ID = MOD190_DETAIL_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL190_DETAIL + "." +FsModel190DetailColumns.FS_MODEL190 + " = ?";

	private static final String MOD190_DETAIL_INSERT = "INSERT INTO "
			+ SQLConstants.FS_MODEL190_DETAIL + "( "
			+FsModel190DetailColumns.DOMAIN + ", "	
			+FsModel190DetailColumns.FS_MODEL190 + ", "	
			+FsModel190DetailColumns.DOCUMENT + ", "	
			+FsModel190DetailColumns.NAME + ", "	
			+FsModel190DetailColumns.REPRESENTATIVE_DOCUMENT + ", "	
			+FsModel190DetailColumns.PROVINCE + ", "	
			+"`"+FsModel190DetailColumns.KEY + "`, "	
			+FsModel190DetailColumns.SUBKEY + ", "	
			+FsModel190DetailColumns.PERCEPTION + ", "	
			+FsModel190DetailColumns.RETENTION + ", "	
			+FsModel190DetailColumns.IN_KIND_PERCEPTION + ", "	
			+FsModel190DetailColumns.IN_KIND_DEPOSIT + ", "	
			+FsModel190DetailColumns.IN_KIND_OUTPUT_DEPOSIT + ", "	
			+FsModel190DetailColumns.ACCRUAL_YEAR + ", "	
			+FsModel190DetailColumns.CEUTA_MELILLA + ", "	
			+FsModel190DetailColumns.BIRTH_YEAR + ", "	
			+FsModel190DetailColumns.FAMILY_SITUATION + ", "	
			+FsModel190DetailColumns.SPOUSE_DOCUMENT + ", "	
			+FsModel190DetailColumns.DISABILITY + ", "	
			+FsModel190DetailColumns.CONTRACT + ", "	
			+FsModel190DetailColumns.LABOUR_PROLONGATION + ", "	
			+FsModel190DetailColumns.GEOGRAPHIC_MOBILITY + ", "	
			+FsModel190DetailColumns.APPLICABLE_REDUCTION + ", "	
			+FsModel190DetailColumns.DEDUCIBLE_EXPENSES + ", "	
			+FsModel190DetailColumns.SPOUSAL_SUPPORT + ", "	
			+FsModel190DetailColumns.FOOD_ANNUITY + ", "	
			+FsModel190DetailColumns.LESS_THAN_3_DESCENDENT + ", "	
			+FsModel190DetailColumns.LESS_THAN_3_DESCENDENT_RATIO + ", "	
			+FsModel190DetailColumns.OTHER_DESCENDENT + ", "	
			+FsModel190DetailColumns.OTHER_DESCENDENT_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_33 + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_33_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_65 + ", "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_65_RATIO + ", "	
			+FsModel190DetailColumns.LESS_THAN_75_ASCENDANT + ", "	
			+FsModel190DetailColumns.LESS_THAN_75_ASCENDANT_RATIO + ", "	
			+FsModel190DetailColumns.ASCENDANT + ", "	
			+FsModel190DetailColumns.ASCENDANT_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_33 + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_33_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE_RATIO + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_65 + ", "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_65_RATIO + ", "	
			+FsModel190DetailColumns.FIRST_CHILD_CALCULATION + ", "	
			+FsModel190DetailColumns.SECOND_CHILD_CALCULATION + ", "	
			+FsModel190DetailColumns.THIRD_CHILD_CALCULATION + ", "	
			+FsModel190DetailColumns.HOME_LOAN_COMMUNNICATION 	
			+ " ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
			+ ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String MOD190_DETAIL_UPDATE = 
			"UPDATE " + SQLConstants.FS_MODEL190_DETAIL + " SET "
			+FsModel190DetailColumns.DOCUMENT + "=?, "	
			+FsModel190DetailColumns.NAME + "=?, "	
			+FsModel190DetailColumns.REPRESENTATIVE_DOCUMENT + "=?, "	
			+FsModel190DetailColumns.PROVINCE + "=?, "	
			+"`"+FsModel190DetailColumns.KEY + "`=?, "	
			+FsModel190DetailColumns.SUBKEY + "=?, "	
			+FsModel190DetailColumns.PERCEPTION + "=?, "	
			+FsModel190DetailColumns.RETENTION + "=?, "	
			+FsModel190DetailColumns.IN_KIND_PERCEPTION + "=?, "	
			+FsModel190DetailColumns.IN_KIND_DEPOSIT + "=?, "	
			+FsModel190DetailColumns.IN_KIND_OUTPUT_DEPOSIT + "=?, "	
			+FsModel190DetailColumns.ACCRUAL_YEAR + "=?, "	
			+FsModel190DetailColumns.CEUTA_MELILLA + "=?, "	
			+FsModel190DetailColumns.BIRTH_YEAR + "=?, "	
			+FsModel190DetailColumns.FAMILY_SITUATION + "=?, "	
			+FsModel190DetailColumns.SPOUSE_DOCUMENT + "=?, "	
			+FsModel190DetailColumns.DISABILITY + "=?, "	
			+FsModel190DetailColumns.CONTRACT + "=?, "	
			+FsModel190DetailColumns.LABOUR_PROLONGATION + "=?, "	
			+FsModel190DetailColumns.GEOGRAPHIC_MOBILITY + "=?, "	
			+FsModel190DetailColumns.APPLICABLE_REDUCTION + "=?, "	
			+FsModel190DetailColumns.DEDUCIBLE_EXPENSES + "=?, "	
			+FsModel190DetailColumns.SPOUSAL_SUPPORT + "=?, "	
			+FsModel190DetailColumns.FOOD_ANNUITY + "=?, "	
			+FsModel190DetailColumns.LESS_THAN_3_DESCENDENT + "=?, "	
			+FsModel190DetailColumns.LESS_THAN_3_DESCENDENT_RATIO + "=?, "	
			+FsModel190DetailColumns.OTHER_DESCENDENT + "=?, "	
			+FsModel190DetailColumns.OTHER_DESCENDENT_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_33 + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_33_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_65 + "=?, "	
			+FsModel190DetailColumns.DISABILITY_DESCENDENT_65_RATIO + "=?, "	
			+FsModel190DetailColumns.LESS_THAN_75_ASCENDANT + "=?, "	
			+FsModel190DetailColumns.LESS_THAN_75_ASCENDANT_RATIO + "=?, "	
			+FsModel190DetailColumns.ASCENDANT + "=?, "	
			+FsModel190DetailColumns.ASCENDANT_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_33 + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_33_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE_RATIO + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_65 + "=?, "	
			+FsModel190DetailColumns.DISABILITY_ASCENDANT_65_RATIO + "=?, "	
			+FsModel190DetailColumns.FIRST_CHILD_CALCULATION + "=?, "	
			+FsModel190DetailColumns.SECOND_CHILD_CALCULATION + "=?, "	
			+FsModel190DetailColumns.THIRD_CHILD_CALCULATION + "=?, "	
			+FsModel190DetailColumns.HOME_LOAN_COMMUNNICATION + "=? " 	
			+ " WHERE "
			+ SQLConstants.FS_MODEL190_DETAIL + "." +FsModel190DetailColumns.ID + " = ?";

	//@formatter:on

	public static Mod190 save(Connection conn, Mod190 mod190)
			throws AonSQLException {
		if (mod190.getId() == null) {
			LOGGER.log(Level.INFO, "INSERTING Mod190");
			return insert(conn, mod190);
		} else {
			LOGGER.log(Level.INFO, "UPDATING Mod190");
			return update(conn, mod190);
		}
	}

	public static Mod190 save(Connection conn, Mod190 mod190,
			ArrayList<Mod190Receiver> perceptors) throws AonSQLException {
		PreparedStatement insertStmt = null;
		PreparedStatement updateStmt = null;
		PreparedStatement deleteStmt = null;
		try {
			mod190 = save(conn, mod190);
			insertStmt = conn.prepareStatement(MOD190_DETAIL_INSERT);
			updateStmt = conn.prepareStatement(MOD190_DETAIL_UPDATE);
			deleteStmt = conn.prepareStatement(MOD190_DETAIL_DELETE);
			for (Mod190Receiver perceptor : perceptors) {
				if (perceptor.getId() == null || perceptor.getId() < 0) {
					if (!perceptor.isDeleted()) {
						perceptor.setDomain(mod190.getDomain());
						perceptor.setMod190(mod190.getId());
						insert(insertStmt, perceptor);
						LOGGER.log(Level.INFO, "INSERTING Mod190Detail");
					}
				} else {
					if (perceptor.isDeleted()) {
						delete(deleteStmt, perceptor);
						LOGGER.log(Level.INFO, "DELETING Mod190Detail");
					} else {
						update(updateStmt, perceptor);
						LOGGER.log(Level.INFO, "UPDATING Mod190Detail");
					}
				}
			}
			insertStmt.executeBatch();
			return mod190;
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(insertStmt);
			SQLUtils.closeQuietly(updateStmt);
			SQLUtils.closeQuietly(deleteStmt);
		}

	}

	private static Mod190 insert(Connection conn, Mod190 mod190)
			throws AonSQLException {
		PreparedStatement insertStmt = null;
		ResultSet rs = null;
		try {
			validate(conn,mod190);
			insertStmt = conn.prepareStatement(MOD190_INSERT,
					Statement.RETURN_GENERATED_KEYS);
			SQLUtils.setInt(insertStmt, 1, mod190.getDomain());
			SQLUtils.setInt(insertStmt, 2, mod190.getEnterprise());
			SQLUtils.setInt(insertStmt, 3, mod190.getYear());
			SQLUtils.setInt(insertStmt, 4, mod190.getAdministration());
			SQLUtils.setInt(insertStmt, 5, 0);
			SQLUtils.setInt(insertStmt, 6, mod190.isConfidential() ? 1 : 0);
			SQLUtils.setString(insertStmt, 7, mod190.getDocument());
			SQLUtils.setString(insertStmt, 8, mod190.getName());
			SQLUtils.setString(insertStmt, 9, mod190.getContactPerson());
			SQLUtils.setString(insertStmt, 10, mod190.getContactPhone());
			SQLUtils.setInt(insertStmt, 11, 0);
			SQLUtils.setInt(insertStmt, 12,mod190.isReplacement() ? 1 : 0);
			SQLUtils.setString(insertStmt, 13, mod190.getReceipt());
			SQLUtils.setString(insertStmt, 14, mod190.getReplacedReceipt());
			SQLUtils.setString(insertStmt, 15, mod190.getComments());
			SQLUtils.setInt(insertStmt, 16, mod190.getReceiverCountTotal());
			SQLUtils.setDouble(insertStmt, 17, mod190.getReceiptTotal());
			SQLUtils.setDouble(insertStmt, 18, mod190.getRetentionTotal());
			insertStmt.execute();
			rs = insertStmt.getGeneratedKeys();
			if (!rs.next()) {
				throw new AonSQLException("Unable to recover last inserted id");
			}
			mod190.setId(rs.getInt(1));
			insertModel190Detail(conn, mod190);
			return getById(mod190.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(insertStmt);
		}
	}

	private static void validate(Connection conn, Mod190 mod190) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_MOD190,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1,mod190.getYear());
			stmt.setInt(2,mod190.getEnterprise());
			
			if (mod190.isReplacement()) {
				// Se busca que exista una declaración a la que sustituir.
				stmt.setInt(3,0);
				rs = stmt.executeQuery();
				if (!rs.next()) {
					throw new AonSQLException("No existe una declaraci\u00F3n a la que sustituir.");
				}
				rs.close();
				
				// Se busca que no exista una declaración sustitutiva.
				stmt.setInt(3,1);
				rs = stmt.executeQuery();
				if (rs.next()) {
					throw new AonSQLException("Ya existe una declaraci\u00F3n sustitutiva.");
				}
				rs.close();
			} else {
				// Se busca que no exista ya una declaración.
				stmt.setInt(3,0);
				rs = stmt.executeQuery();
				if (rs.next()) {
					throw new AonSQLException("Ya existe una declaraci\u00F3n en el ejercicio.");
				}
				rs.close();
			}
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
				
	}

	private static Mod190 update(Connection conn, Mod190 mod190)
			throws AonSQLException {
		PreparedStatement updateStmt = null;
		try {
			updateStmt = conn.prepareStatement(MOD190_UPDATE);
			SQLUtils.setInt(updateStmt, 1, mod190.getYear());
			SQLUtils.setInt(updateStmt, 2, mod190.getAdministration());
			SQLUtils.setShort(updateStmt, 3, (short) 0);
			SQLUtils.setShort(updateStmt, 4,
					(short) (mod190.isConfidential() ? 1 : 0));
			SQLUtils.setString(updateStmt, 5, mod190.getDocument());
			SQLUtils.setString(updateStmt, 6, mod190.getName());
			SQLUtils.setString(updateStmt, 7, mod190.getContactPerson());
			SQLUtils.setString(updateStmt, 8, mod190.getContactPhone());
			SQLUtils.setShort(updateStmt, 9, (short) 0);
			SQLUtils.setShort(updateStmt, 10,
					(short) (mod190.isReplacement() ? 1 : 0));
			SQLUtils.setString(updateStmt,11, mod190.getReceipt());
			SQLUtils.setString(updateStmt,12, mod190.getReplacedReceipt());			
			SQLUtils.setString(updateStmt, 13, mod190.getComments());
			SQLUtils.setInt(updateStmt, 14, mod190.getReceiverCountTotal());
			SQLUtils.setDouble(updateStmt, 15, mod190.getReceiptTotal());
			SQLUtils.setDouble(updateStmt, 16, mod190.getRetentionTotal());
			SQLUtils.setInt(updateStmt, 17, mod190.getId());
			updateStmt.execute();
			return getById(mod190.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(updateStmt);
		}
	}

	public static void delete(Connection conn, Mod190 mod190)
			throws AonSQLException {
		PreparedStatement deleteStmt = null;
		PreparedStatement deleteDetailStmt = null;
		try {
			LOGGER.log(Level.INFO,
					"DELETING DECLARATION DETAIL (" + mod190.getId() + ")");
			deleteDetailStmt = conn
					.prepareStatement(MOD190_DETAIL_DELETE_BY_MOD190);
			SQLUtils.setInt(deleteDetailStmt, 1, mod190.getId());
			deleteDetailStmt.execute();

			LOGGER.log(Level.INFO, "DELETING DECLARATION(" + mod190.getId()
					+ ")");
			deleteStmt = conn.prepareStatement(MOD190_DELETE);
			SQLUtils.setInt(deleteStmt, 1, mod190.getId());
			deleteStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteDetailStmt);
			SQLUtils.closeQuietly(deleteStmt);
		}
	}
/*
	public static void delete(Connection conn, Mod190Detail detail)
			throws Mod190Exception {
		PreparedStatement deleteStmt = null;
		try {
			deleteStmt = conn.prepareStatement(MOD190_DETAIL_DELETE);
			SQLUtils.setInt(deleteStmt, 1, detail.getId());
		} catch (Throwable e) {
			throw new Mod190Exception(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteStmt);
		}
	}
*/
	public static ArrayList<Mod190> getByDomain(int domain, Connection conn)
			throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD190_SELECT_DOMAIN,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, domain);
			LOGGER.log(Level.INFO, "GETTING BY DOMAIN (" + domain + ")");
			rs = stmt.executeQuery();
			ArrayList<Mod190> list = new ArrayList<Mod190>();
			Mod190 mod190 = null;
			while (rs.next()) {
				mod190 = new Mod190();
				populateResultSet(rs, mod190);
				list.add(mod190);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	private static void populateResultSet(ResultSet rs, Mod190 mod190)
			throws SQLException {

		mod190.setId(rs.getInt(FsModel190Columns.ID));
		mod190.setDomain(rs.getInt(FsModel190Columns.DOMAIN));
		mod190.setEnterprise(rs.getInt(FsModel190Columns.ENTERPRISE));
		mod190.setYear(rs.getInt(FsModel190Columns.YEAR));
		mod190.setAdministration(rs.getInt(FsModel190Columns.ADMINISTRATION));
		mod190.setReplacement(rs.getBoolean(FsModel190Columns.REPLACEMENT));
		mod190.setDocument(rs.getString(FsModel190Columns.DOCUMENT));
		mod190.setName(rs.getString(FsModel190Columns.NAME));
		mod190.setContactPerson(rs.getString(FsModel190Columns.CONTACT_PERSON));
		mod190.setContactPhone(rs.getString(FsModel190Columns.CONTACT_PHONE));
		mod190.setReceipt(rs.getString(FsModel190Columns.RECEIPT));
		mod190.setReplacedReceipt(rs.getString(FsModel190Columns.REPLACED_RECEIPT));
		mod190.setReceiverCountTotal(rs
				.getInt(FsModel190Columns.RECEIVER_COUNT_TOTAL));
		mod190.setReceiptTotal(rs.getDouble(FsModel190Columns.RECEIPT_TOTAL));
		mod190.setRetentionTotal(rs
				.getDouble(FsModel190Columns.RETENTION_TOTAL));
		mod190.setComments(rs.getString(FsModel190Columns.COMMENTS));
	}

	public static Mod190 getById(int id, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD190_SELECT_ID,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			LOGGER.log(Level.INFO, "GETTING BY ID (" + id + ")");
			rs = stmt.executeQuery();
			Mod190 mod190 = null;
			if (rs.next()) {
				mod190 = new Mod190();
				populateResultSet(rs, mod190);
			}
			return mod190;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static Mod190Receiver getDetailById(int id, Connection conn)
			throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD190_DETAIL_SELECT_ID,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			LOGGER.log(Level.INFO, "GETTING DETAILS BY ID (" + id + ")");
			rs = stmt.executeQuery();
			Mod190Receiver receiver = null;
			if (rs.next()) {
				receiver = new Mod190Receiver();
				populateResultSet(rs, receiver);
			}
			return receiver;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static ArrayList<Mod190Detail> getDetailsByMod190(int mod190,
			int offset, int limit, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String select = MOD190_DETAIL_ID_NAME_SELECT + " WHERE "
					+ SQLConstants.FS_MODEL190_DETAIL + "."
					+ FsModel190DetailColumns.FS_MODEL190 + " = ?"
					+ " ORDER BY " + SQLConstants.FS_MODEL190_DETAIL + "."
					+ FsModel190DetailColumns.NAME + " LIMIT ?  OFFSET ?";
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, mod190);
			stmt.setInt(2, limit);
			stmt.setInt(3, offset);
			LOGGER.log(Level.INFO, "GETTING DETAILS BY MOD190 (" + mod190
					+ ", off: " + offset + ",lim: " + limit + " )");
			rs = stmt.executeQuery();
			ArrayList<Mod190Detail> list = new ArrayList<Mod190Detail>();
			Mod190Detail mod190Detail = null;
			while (rs.next()) {
				mod190Detail = new Mod190Detail();
				mod190Detail.setId(rs.getInt(FsModel190DetailColumns.ID));
				mod190Detail
						.setName(rs.getString(FsModel190DetailColumns.NAME));
				list.add(mod190Detail);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static ArrayList<Mod190Receiver> getReceiversByMod190(int mod190, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD190_RECEIVER_SELECT_ID, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, mod190);
			LOGGER.log(Level.INFO, "GETTING RECEIVERS BY MOD190 (" + mod190 + " )");
			rs = stmt.executeQuery();
			ArrayList<Mod190Receiver> list = new ArrayList<Mod190Receiver>();
			Mod190Receiver receiver = null;
			while (rs.next()) {
				receiver = new Mod190Receiver();
				populateResultSet(rs, receiver);
				list.add(receiver);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	private static void populateResultSet(ResultSet rs, Mod190Receiver receiver)
			throws SQLException {
		receiver.setId(rs.getInt(FsModel190DetailColumns.ID));
		receiver.setDocument(rs.getString(FsModel190DetailColumns.DOCUMENT));
		receiver.setName(rs.getString(FsModel190DetailColumns.NAME));
		receiver.setRepresentativeDocument(rs
				.getString(FsModel190DetailColumns.REPRESENTATIVE_DOCUMENT));
		receiver.setProvince(rs.getInt(FsModel190DetailColumns.PROVINCE));
		receiver.setKey(rs.getString(FsModel190DetailColumns.KEY));
		receiver.setSubKey(rs.getString(FsModel190DetailColumns.SUBKEY));
		receiver.setPerception(rs
				.getDouble(FsModel190DetailColumns.PERCEPTION));
		receiver.setInKindPerception(rs
				.getDouble(FsModel190DetailColumns.IN_KIND_PERCEPTION));
		receiver.setInKindDeposit(rs
				.getDouble(FsModel190DetailColumns.IN_KIND_DEPOSIT));
		receiver.setInKindOutputDeposit(rs
				.getDouble(FsModel190DetailColumns.IN_KIND_OUTPUT_DEPOSIT));
		receiver.setAccrualYear(rs
				.getInt(FsModel190DetailColumns.ACCRUAL_YEAR));
		receiver.setRetention(rs.getDouble(FsModel190DetailColumns.RETENTION));
		
		IrpfData irpfData = new IrpfData();
		irpfData.setBirthYear(rs.getInt(FsModel190DetailColumns.BIRTH_YEAR));
		irpfData.setCeutaMelilla(rs
				.getBoolean(FsModel190DetailColumns.CEUTA_MELILLA));
		irpfData.setFamilySituation(rs
				.getInt(FsModel190DetailColumns.FAMILY_SITUATION));
		irpfData.setSpouseDocument(rs
				.getString(FsModel190DetailColumns.SPOUSE_DOCUMENT));
		irpfData.setDisability(rs.getInt(FsModel190DetailColumns.DISABILITY));
		irpfData.setContract(rs.getInt(FsModel190DetailColumns.CONTRACT));
		irpfData.setWorkActivityExtension(rs
				.getBoolean(FsModel190DetailColumns.LABOUR_PROLONGATION));
		irpfData.setGeographicMobility(rs
				.getBoolean(FsModel190DetailColumns.GEOGRAPHIC_MOBILITY));
		receiver.setIrpfData(irpfData);
		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setApplicableReduction(rs
				.getDouble(FsModel190DetailColumns.APPLICABLE_REDUCTION));
		irpfResult.setDeducibleExpense(rs
				.getDouble(FsModel190DetailColumns.DEDUCIBLE_EXPENSES));
		irpfResult.setCompensatoryPension(rs
				.getDouble(FsModel190DetailColumns.SPOUSAL_SUPPORT));
		irpfResult.setFoodAnnuality(rs
				.getDouble(FsModel190DetailColumns.FOOD_ANNUITY));
		irpfResult.setHomeLoanCommunnication(rs
				.getBoolean(FsModel190DetailColumns.HOME_LOAN_COMMUNNICATION));
		irpfResult.setLessThan3Descendent(rs
				.getInt(FsModel190DetailColumns.LESS_THAN_3_DESCENDENT));
		irpfResult.setLessThan3DescendentRatio(rs
				.getInt(FsModel190DetailColumns.LESS_THAN_3_DESCENDENT_RATIO));
		irpfResult.setOtherDescendent(rs
				.getInt(FsModel190DetailColumns.OTHER_DESCENDENT));
		irpfResult.setOtherDescendentRatio(rs
				.getInt(FsModel190DetailColumns.OTHER_DESCENDENT_RATIO));
		irpfResult.setDisabilityDescendent33(rs
				.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_33));
		irpfResult
				.setDisabilityDescendent33Ratio(rs
						.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_33_RATIO));
		irpfResult
				.setDisabilityDescendentDependence(rs
						.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE));
		irpfResult
				.setDisabilityDescendentDependenceRatio(rs
						.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_DEPENDENCE_RATIO));
		irpfResult.setDisabilityDescendent65(rs
				.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_65));
		irpfResult
				.setDisabilityDescendent65Ratio(rs
						.getInt(FsModel190DetailColumns.DISABILITY_DESCENDENT_65_RATIO));
		irpfResult.setLessThan75Ascendant(rs
				.getInt(FsModel190DetailColumns.LESS_THAN_75_ASCENDANT));
		irpfResult.setLessThan75AscendantRatio(rs
				.getInt(FsModel190DetailColumns.LESS_THAN_75_ASCENDANT_RATIO));
		irpfResult.setAscendant(rs.getInt(FsModel190DetailColumns.ASCENDANT));
		irpfResult.setAscendantRatio(rs
				.getInt(FsModel190DetailColumns.ASCENDANT_RATIO));
		irpfResult.setDisabilityAscendant33(rs
				.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_33));
		irpfResult.setDisabilityAscendant33Ratio(rs
				.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_33_RATIO));
		irpfResult
				.setDisabilityAscendantDependence(rs
						.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE));
		irpfResult
				.setDisabilityAscendantDependenceRatio(rs
						.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_DEPENDENCE_RATIO));
		irpfResult.setDisabilityAscendant65(rs
				.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_65));
		irpfResult.setDisabilityAscendant65Ratio(rs
				.getInt(FsModel190DetailColumns.DISABILITY_ASCENDANT_65_RATIO));
		irpfResult.setFirstChildCalculation(rs
				.getInt(FsModel190DetailColumns.FIRST_CHILD_CALCULATION));
		irpfResult.setSecondChildCalculation(rs
				.getInt(FsModel190DetailColumns.SECOND_CHILD_CALCULATION));
		irpfResult.setThirdChildCalculation(rs
				.getInt(FsModel190DetailColumns.THIRD_CHILD_CALCULATION));
		receiver.setIrpfResult(irpfResult);
	}

	private static void insertModel190Detail(Connection conn, Mod190 mod190)
			throws AonSQLException {
		Date firstDay = SQLUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = SQLUtils.getYearLastDay(mod190.getYear());
		PreparedStatement ps = null;
		PreparedStatement insertStmt = null;
		ResultSet rs = null;
		PreparedStatement irpfDataStmt = null;
		ResultSet irpfDataRs = null;
		PreparedStatement irpfResultStmt = null;
		ResultSet irpfResultRs = null;
		PreparedStatement geozoneStmt = null;
		ResultSet geozoneRs = null;
		PreparedStatement invoiceStmt = null;
		ResultSet invoiceRs = null;
		
		try {
			geozoneStmt = conn.prepareStatement(GEOZONE_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			irpfDataStmt = SQLIrpf.getLastIrpfDataByPersonStatement(conn);
			irpfResultStmt = SQLIrpf.getLastIrpfResultByPersonStatement(conn);
			ps = conn.prepareStatement(MOD190_SALARY_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			insertStmt = conn.prepareStatement(MOD190_DETAIL_INSERT);
			ps.setDate(1, new java.sql.Date(firstDay.getTime()));
			ps.setDate(2, new java.sql.Date(lastDay.getTime()));
			ps.setInt(3, mod190.getEnterprise());
			ps.setInt(4, mod190.getAdministration());
			rs = ps.executeQuery();
			Mod190Receiver detail = null;
			boolean batched = false;
			while (rs.next()) {
				detail = new Mod190Receiver();
				detail.setDomain(mod190.getDomain());
				detail.setMod190(mod190.getId());
				detail.setDocument(rs
						.getString(SalaryColumns.EMPLOYEE_DOCUMENT));
				detail.setName(rs.getString(SalaryColumns.EMPLOYEE_NAME));
				detail.setKey("A");
				detail.setPerception(rs
						.getDouble(SalaryColumns.MONEY_IRPF_BASE));
				detail.setInKindPerception(rs
						.getDouble(SalaryColumns.INKIND_IRPF_BASE));
				detail.setRetention(rs.getDouble(SalaryColumns.TOTAL_IRPF));

				int personId = rs.getInt(PersonColumns.REGISTRY);
				irpfDataRs = SQLIrpf.getLastIrpfDataByPersonResultSet(
						irpfDataStmt, personId, firstDay, lastDay);
				detail.setIrpfData(new IrpfData());
				SQLIrpf.populateIrpfData(irpfDataRs, detail.getIrpfData());
				irpfDataRs.close();
				
				detail.getIrpfData().setBirthYear(rs.getInt(PersonColumns.BIRTH_DATE));

				irpfResultRs = SQLIrpf.getLastIrpfResultByPersonResultSet(
						irpfResultStmt, personId, firstDay, lastDay);
				detail.setIrpfResult(new IrpfResult());
				SQLIrpf.populateIrpfResult(irpfResultRs, detail.getIrpfResult());
				irpfResultRs.close();
				
				geozoneStmt.setInt(1, personId);
				geozoneRs = geozoneStmt.executeQuery();
				if (geozoneRs.next()) {
					String code = geozoneRs.getString(GeozoneColumns.CODE);
					try {
						int province = Integer.parseInt(code);
						detail.setProvince(province);
					} catch (NumberFormatException e) {
						// nothing. Si la clave no es numero, no es provicncia válida.
					}
				}
				geozoneRs.close();
				insert(insertStmt, detail);
				batched = true;
			}
			if (batched) {
				insertStmt.executeBatch();
				insertStmt.clearBatch();
			}
			
			invoiceStmt = conn.prepareStatement(SELECT_INVOICE,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			invoiceStmt.setInt(1, mod190.getDomain());
			invoiceStmt.setDate(2, new java.sql.Date(firstDay.getTime()));
			invoiceStmt.setDate(3, new java.sql.Date(lastDay.getTime()));
			invoiceRs = invoiceStmt.executeQuery();
			batched = false;
			while (invoiceRs.next()) {
				detail = new Mod190Receiver();
				detail.setDomain(mod190.getDomain());
				detail.setMod190(mod190.getId());
				detail.setDocument(invoiceRs.getString(InvoiceColumns.RDOCUMENT));
				detail.setName(invoiceRs.getString(InvoiceColumns.RNAME));
				int withholding = invoiceRs.getInt(InvoiceTaxColumns.WITHHOLDING_TYPE);
				if (withholding == 0) {				/** PROFESIONALES - PROFESSIONAL*/
					detail.setKey("G");
					detail.setSubKey("01");
				} else if (withholding == 1) {		/** ARRENDAMIENTO - RENTING*/
					// Ignore for 190 --> 180
				} else if (withholding == 2) {		/** CAPITAL MOBILIARIO - MOVABLE_CAPITAL*/
					// Ignore for 190 --> 184
				} else if (withholding == 3) {		/** AGRICULTOR - FARMER*/
					detail.setKey("H");
					detail.setSubKey("01");
				} else if (withholding == 4) {		/** TRANSPORTISTAS Y ASIMILADOS - TRANSPORT_OPERATOR*/
					detail.setKey("H");
					detail.setSubKey("04");
				}
				detail.setPerception(invoiceRs.getDouble(InvoiceTaxColumns.BASE));
				detail.setRetention(invoiceRs.getDouble(InvoiceTaxColumns.QUOTA));
				int registryId = invoiceRs.getInt(InvoiceColumns.REGISTRY);
				geozoneStmt.setInt(1, registryId);
				geozoneRs = geozoneStmt.executeQuery();
				if (geozoneRs.next()) {
					String code = geozoneRs.getString(GeozoneColumns.CODE);
					try {
						int province = Integer.parseInt(code);
						detail.setProvince(province);
					} catch (NumberFormatException e) {
						// nothing. Si la clave no es numero, no es provicncia válida.
					}
				}
				geozoneRs.close();
				insert(insertStmt, detail);
				batched = true;
			}
			if (batched) {
				insertStmt.executeBatch();
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(ps);
			SQLUtils.closeQuietly(insertStmt);
			SQLUtils.closeQuietly(irpfDataStmt);
			SQLUtils.closeQuietly(irpfDataRs);
			SQLUtils.closeQuietly(irpfResultStmt);
			SQLUtils.closeQuietly(irpfResultRs);
			SQLUtils.closeQuietly(geozoneStmt);
			SQLUtils.closeQuietly(geozoneRs);
			SQLUtils.closeQuietly(invoiceStmt);
			SQLUtils.closeQuietly(invoiceRs);
		}
	}

	private static void insert(PreparedStatement insertStmt, Mod190Receiver perceptor)
			throws SQLException {
		SQLUtils.setInt(insertStmt, 1, perceptor.getDomain());
		SQLUtils.setInt(insertStmt, 2, perceptor.getMod190());
		if (perceptor.getDocument() != null && perceptor.getDocument().length() > 9) {
			perceptor.setDocument( perceptor.getDocument().substring(0, 8) );
		}
		SQLUtils.setString(insertStmt, 3, perceptor.getDocument());
		SQLUtils.setString(insertStmt, 4, perceptor.getName());
		SQLUtils.setString(insertStmt, 5, perceptor.getRepresentativeDocument());
		SQLUtils.setInt(insertStmt, 6, perceptor.getProvince());
		SQLUtils.setString(insertStmt, 7, perceptor.getKey());
		SQLUtils.setString(insertStmt, 8, perceptor.getSubKey());
		SQLUtils.setDouble(insertStmt, 9, perceptor.getPerception());
		SQLUtils.setDouble(insertStmt, 10, perceptor.getRetention());
		SQLUtils.setDouble(insertStmt, 11, perceptor.getInKindPerception());
		SQLUtils.setDouble(insertStmt, 12, perceptor.getInKindDeposit());
		SQLUtils.setDouble(insertStmt, 13, perceptor.getInKindOutputDeposit());
		SQLUtils.setInt(insertStmt, 14, perceptor.getAccrualYear());

		IrpfData irpfData = perceptor.getIrpfData();
		if (irpfData == null) {
			irpfData = new IrpfData();
		}
		SQLUtils.setInt(insertStmt, 15, irpfData.isCeutaMelilla() ? 1 : 0);
		SQLUtils.setInt(insertStmt, 16, irpfData.getBirthYear());
		SQLUtils.setInt(insertStmt, 17, irpfData.getFamilySituation());
		SQLUtils.setString(insertStmt, 18, irpfData.getSpouseDocument());
		SQLUtils.setInt(insertStmt, 19, irpfData.getDisability());
		SQLUtils.setInt(insertStmt, 20, irpfData.getContract());
		SQLUtils.setInt(insertStmt, 21, irpfData.isWorkActivityExtension() ? 1
				: 0);
		SQLUtils.setInt(insertStmt, 22, irpfData.isGeographicMobility() ? 1 : 0);

		IrpfResult irpfResult = perceptor.getIrpfResult();
		if (irpfResult == null) {
			irpfResult = new IrpfResult();
		}
		SQLUtils.setDouble(insertStmt, 23, irpfResult.getApplicableReduction());
		SQLUtils.setDouble(insertStmt, 24, irpfResult.getDeducibleExpense());
		SQLUtils.setDouble(insertStmt, 25, irpfResult.getCompensatoryPension());
		SQLUtils.setDouble(insertStmt, 26, irpfResult.getFoodAnnuality());
		SQLUtils.setInt(insertStmt, 27, irpfResult.getLessThan3Descendent());
		SQLUtils.setInt(insertStmt, 28,
				irpfResult.getLessThan3DescendentRatio());
		SQLUtils.setInt(insertStmt, 29, irpfResult.getOtherDescendent());
		SQLUtils.setInt(insertStmt, 30, irpfResult.getOtherDescendentRatio());
		SQLUtils.setInt(insertStmt, 31, irpfResult.getDisabilityDescendent33());
		SQLUtils.setInt(insertStmt, 32,
				irpfResult.getDisabilityDescendent33Ratio());
		SQLUtils.setInt(insertStmt, 33,
				irpfResult.getDisabilityDescendentDependence());
		SQLUtils.setInt(insertStmt, 34,
				irpfResult.getDisabilityDescendentDependenceRatio());
		SQLUtils.setInt(insertStmt, 35, irpfResult.getDisabilityDescendent65());
		SQLUtils.setInt(insertStmt, 36,
				irpfResult.getDisabilityDescendent65Ratio());
		SQLUtils.setInt(insertStmt, 37, irpfResult.getLessThan75Ascendant());
		SQLUtils.setInt(insertStmt, 38,
				irpfResult.getLessThan75AscendantRatio());
		SQLUtils.setInt(insertStmt, 39, irpfResult.getAscendant());
		SQLUtils.setInt(insertStmt, 40, irpfResult.getAscendantRatio());
		SQLUtils.setInt(insertStmt, 41, irpfResult.getDisabilityAscendant33());
		SQLUtils.setInt(insertStmt, 42,
				irpfResult.getDisabilityAscendant33Ratio());
		SQLUtils.setInt(insertStmt, 43,
				irpfResult.getDisabilityAscendantDependence());
		SQLUtils.setInt(insertStmt, 44,
				irpfResult.getDisabilityAscendantDependenceRatio());
		SQLUtils.setInt(insertStmt, 45, irpfResult.getDisabilityAscendant65());
		SQLUtils.setInt(insertStmt, 46,
				irpfResult.getDisabilityAscendant65Ratio());
		SQLUtils.setInt(insertStmt, 47, irpfResult.getFirstChildCalculation());
		SQLUtils.setInt(insertStmt, 48, irpfResult.getSecondChildCalculation());
		SQLUtils.setInt(insertStmt, 49, irpfResult.getThirdChildCalculation());

		SQLUtils.setInt(insertStmt, 50,
				irpfResult.isHomeLoanCommunnication() ? 1 : 0);
		insertStmt.addBatch();
	}

	private static void update(PreparedStatement updateStmt, Mod190Receiver perceptor)
			throws SQLException {
		SQLUtils.setString(updateStmt, 1, perceptor.getDocument());
		SQLUtils.setString(updateStmt, 2, perceptor.getName());
		SQLUtils.setString(updateStmt, 3, perceptor.getRepresentativeDocument());
		SQLUtils.setInt(updateStmt, 4, perceptor.getProvince());
		SQLUtils.setString(updateStmt, 5, perceptor.getKey());
		SQLUtils.setString(updateStmt, 6, perceptor.getSubKey());
		SQLUtils.setDouble(updateStmt, 7, perceptor.getPerception());
		SQLUtils.setDouble(updateStmt, 8, perceptor.getRetention());
		SQLUtils.setDouble(updateStmt, 9, perceptor.getInKindPerception());
		SQLUtils.setDouble(updateStmt, 10, perceptor.getInKindDeposit());
		SQLUtils.setDouble(updateStmt, 11, perceptor.getInKindOutputDeposit());
		SQLUtils.setInt(updateStmt, 12, perceptor.getAccrualYear());

		IrpfData irpfData = perceptor.getIrpfData();
		SQLUtils.setInt(updateStmt, 13, irpfData.isCeutaMelilla() ? 1 : 0);
		SQLUtils.setInt(updateStmt, 14, irpfData.getBirthYear());
		SQLUtils.setInt(updateStmt, 15, irpfData.getFamilySituation());
		SQLUtils.setString(updateStmt, 16, irpfData.getSpouseDocument());
		SQLUtils.setInt(updateStmt, 17, irpfData.getDisability());
		SQLUtils.setInt(updateStmt, 18, irpfData.getContract());
		SQLUtils.setInt(updateStmt, 19, irpfData.isWorkActivityExtension() ? 1
				: 0);
		SQLUtils.setInt(updateStmt, 20, irpfData.isGeographicMobility() ? 1 : 0);

		IrpfResult irpfResult = perceptor.getIrpfResult();
		SQLUtils.setDouble(updateStmt, 21, irpfResult.getApplicableReduction());
		SQLUtils.setDouble(updateStmt, 22, irpfResult.getDeducibleExpense());
		SQLUtils.setDouble(updateStmt, 23, irpfResult.getCompensatoryPension());
		SQLUtils.setDouble(updateStmt, 24, irpfResult.getFoodAnnuality());
		SQLUtils.setInt(updateStmt, 25, irpfResult.getLessThan3Descendent());
		SQLUtils.setInt(updateStmt, 26,
				irpfResult.getLessThan3DescendentRatio());
		SQLUtils.setInt(updateStmt, 27, irpfResult.getOtherDescendent());
		SQLUtils.setInt(updateStmt, 28, irpfResult.getOtherDescendentRatio());
		SQLUtils.setInt(updateStmt, 29, irpfResult.getDisabilityDescendent33());
		SQLUtils.setInt(updateStmt, 30,
				irpfResult.getDisabilityDescendent33Ratio());
		SQLUtils.setInt(updateStmt, 31,
				irpfResult.getDisabilityDescendentDependence());
		SQLUtils.setInt(updateStmt, 32,
				irpfResult.getDisabilityDescendentDependenceRatio());
		SQLUtils.setInt(updateStmt, 33, irpfResult.getDisabilityDescendent65());
		SQLUtils.setInt(updateStmt, 34,
				irpfResult.getDisabilityDescendent65Ratio());
		SQLUtils.setInt(updateStmt, 35, irpfResult.getLessThan75Ascendant());
		SQLUtils.setInt(updateStmt, 36,
				irpfResult.getLessThan75AscendantRatio());
		SQLUtils.setInt(updateStmt, 37, irpfResult.getAscendant());
		SQLUtils.setInt(updateStmt, 38, irpfResult.getAscendantRatio());
		SQLUtils.setInt(updateStmt, 39, irpfResult.getDisabilityAscendant33());
		SQLUtils.setInt(updateStmt, 40,
				irpfResult.getDisabilityAscendant33Ratio());
		SQLUtils.setInt(updateStmt, 41,
				irpfResult.getDisabilityAscendantDependence());
		SQLUtils.setInt(updateStmt, 42,
				irpfResult.getDisabilityAscendantDependenceRatio());
		SQLUtils.setInt(updateStmt, 43, irpfResult.getDisabilityAscendant65());
		SQLUtils.setInt(updateStmt, 44,
				irpfResult.getDisabilityAscendant65Ratio());
		SQLUtils.setInt(updateStmt, 45, irpfResult.getFirstChildCalculation());
		SQLUtils.setInt(updateStmt, 46, irpfResult.getSecondChildCalculation());
		SQLUtils.setInt(updateStmt, 47, irpfResult.getThirdChildCalculation());
		SQLUtils.setInt(updateStmt, 48,
				irpfResult.isHomeLoanCommunnication() ? 1 : 0);

		SQLUtils.setInt(updateStmt, 49, perceptor.getId());
		updateStmt.execute();
	}

	private static void delete(PreparedStatement deleteStmt, Mod190Receiver perceptor)
			throws SQLException {
		SQLUtils.setInt(deleteStmt, 1, perceptor.getId());
		deleteStmt.execute();
	}

}
