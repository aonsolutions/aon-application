package com.code.aon.finance.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;

public class FinanceUtil {

	public static String UPDATE_INVOICE_TOTALS = "UPDATE invoice SET service = ?, taxable_base = ?, vat_quota = ?, retention_quota = ?, total = ?" +
													" WHERE id = ?";

	public static String getDocumentNumber(InvoiceType type, String series, int number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(number), 6, "0");
		return documentNumber;
	}

	public static Date getLimitDate() {
		return AppParamUtil.getValueAsDate(AppParam.ACC_OPERATIONS_DEADLINE, DomainManager.getCurrentDomain());
	}

	public static boolean isValidLimitDate(Invoice invoice) {
		Integer type = AdminUtil.getDomainType(DomainManager.getCurrentDomain());
		DomainType domainType = (type!=null) ? DomainType.values()[type] : null;

		Date deadline = getLimitDate();
		if (deadline != null) {
			if (domainType != null && domainType.equals(DomainType.HOTEL) && !invoice.isSales() && DateUtils.addWeeks(deadline, 1).after(new Date())) {
				return deadline.after(invoice.getIssueDate());
			}
		}
		return deadline == null || !deadline.after(invoice.getIssueDate());
	}

	public static void updateInvoiceTotals(Invoice invoice) throws AonSQLException {
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(invoice.getDomain()));
			updateStmt = connection.prepareStatement(UPDATE_INVOICE_TOTALS);
			updateStmt.setInt(1, invoice.isService() ? 1 : 0);
			updateStmt.setDouble(2, invoice.getTaxableBase());
			updateStmt.setDouble(3, invoice.getVatQuota());
			updateStmt.setDouble(4, invoice.getRetentionQuota());
			updateStmt.setDouble(5, invoice.getTotal());
			updateStmt.setInt(6, invoice.getId());
			updateStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
            if (updateStmt != null) {
            	try {
            		updateStmt.close();
                } catch (SQLException e) { 
                }
            }
            if (connection != null) {
            	try {
                	connection.close();
            		
            	} catch (SQLException e) { 
                }
            }
		}
	}

}
