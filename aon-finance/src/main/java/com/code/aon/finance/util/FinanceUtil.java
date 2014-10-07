package com.code.aon.finance.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceUtil.class.getName());

	private static final String OLD_COMPANY = "OLD_COMPANY";
	
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
	
	public static Enterprise getEnterprise(InvoiceDetail invoiceDetail) {
		Enterprise enterprise = invoiceDetail.getWorkPlace().getEnterprise();
		if (!FinanceUtil.isValidLimitDate(invoiceDetail.getInvoice())) {
			try {
				IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), enterprise.getId());
				criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), OLD_COMPANY);
				for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
					Integer oldCompanyId = null;
					try {
						oldCompanyId = Integer.valueOf(((RegistryAddInfo)ito).getValue());
					} catch (NumberFormatException ex) {
					}
					if (oldCompanyId != null) {
						Enterprise oldEnterprise = (Enterprise) BeanManager.getManagerBean(Enterprise.class).get(oldCompanyId);
						if (oldEnterprise != null) {
							enterprise = oldEnterprise;
						}
					}
				}				
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return enterprise;
	}

}
