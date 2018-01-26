package com.code.aon.finance.util;

import static com.code.aon.common.enumeration.AppParam.APP_DOCUMENT_NUMBER_LENGTH_PARAM;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Enterprise;
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
	private static final String WP_EXTENSION = "_WP_";
	
	public static String getDocumentNumber(InvoiceType type, String series, int number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		Integer size = AppParamUtil.getValueAsInteger(APP_DOCUMENT_NUMBER_LENGTH_PARAM);
		size = size != null ? size : 6;
		documentNumber += StringUtils.leftPad(Integer.toString(number), size, "0");
		return documentNumber;
	}

	public static boolean isValidLimitDate(Invoice invoice) {
		Date deadline = getLimitDate();
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
				Enterprise oldEnterprise = getOldEnterprise(enterprise.getId(), OLD_COMPANY + WP_EXTENSION + invoiceDetail.getWorkPlace().getId());
				if (oldEnterprise != null) {
					enterprise = oldEnterprise;
				} else {
					oldEnterprise = getOldEnterprise(enterprise.getId(), OLD_COMPANY);
					if (oldEnterprise != null) {
						enterprise = oldEnterprise;
					}
				}
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return enterprise;
	}

	private static Enterprise getOldEnterprise(Integer currentEnterpriseId, String oldEnterpriseParam) throws ManagerBeanException {
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), currentEnterpriseId);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), oldEnterpriseParam);
		for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
			Integer oldEnterpriseId = null;
			try {
				oldEnterpriseId = Integer.valueOf(((RegistryAddInfo)ito).getValue());
			} catch (NumberFormatException ex) {
			}
			if (oldEnterpriseId != null) {
				return (Enterprise)BeanManager.getManagerBean(Enterprise.class).get(oldEnterpriseId);
			}
		}
		return null;
	}

}
