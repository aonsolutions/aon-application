package com.code.aon.finance.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;

public class FinanceUtil {

	public static String getDocumentNumber(InvoiceType type, String series, int number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(number), 6, "0");
		return documentNumber;
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

	public static boolean isValidLimitRectificationDate(Invoice invoice) {
		Date deadline = getRectificationLimitDate();
		return deadline == null || !deadline.after(invoice.getIssueDate());
	}

	public static Date getLimitDate() {
		Date limitDate = AppParamUtil.getValueAsDate(AppParam.ACC_OPERATIONS_DEADLINE);
		if (limitDate == null) {
			Integer parentDomain = DomainManager.getParentDomain();
			if (parentDomain != null) {
				limitDate = AppParamUtil.getValueAsDate(AppParam.ACC_OPERATIONS_DEADLINE, parentDomain);
			}
		}
		return limitDate;
	}

	public static Date getRectificationLimitDate() {
		return (AppParamUtil.getValueAsBoolean(AppParam.ACC_DEADLINE_INCL_RECTIFICATIONS)) ? getLimitDate() : null;
	}

}
