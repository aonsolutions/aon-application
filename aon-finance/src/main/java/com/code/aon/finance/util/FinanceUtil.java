package com.code.aon.finance.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Enterprise;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class FinanceUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceUtil.class.getName());

	private static final String OLD_COMPANY = "OLD_COMPANY";
	private static final String WP_EXTENSION = "_WP_";
	
	public static String getDocumentNumber(InvoiceType type, String series, int number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		if(number > 0) {
			documentNumber += StringUtils.leftPad(Integer.toString(number), SeriesNumberUtil.getNumberMinimumLength(), "0");
		} else documentNumber += "PROFORMA";
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

	public static void checkAlcatraz(Invoice invoice) throws ManagerBeanVetoListenerException {
		if (invoice != null && invoice.getId() != null) {
			String select = "SELECT alcatraz.id id " +
					",fs_model.year year " +
					",fs_model.period period " +
					",fs_model.model model " +
					",fs_model.administration admon " +
					"FROM alcatraz " +
					"INNER JOIN fs_model ON fs_model.id = alcatraz.fs_model " +
					"WHERE " + DomainManager.getSQLWhereClause("alcatraz.domain") + " " +
					"AND alcatraz.invoice = " + invoice.getId();
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			SQLQuery query = session.createSQLQuery(select);
			List<?> list = query.addScalar("id", Hibernate.INTEGER)
					.addScalar("year", Hibernate.INTEGER)
					.addScalar("period", Hibernate.BYTE)
					.addScalar("model", Hibernate.STRING)
					.addScalar("admon", Hibernate.BYTE)
					.list();
			if (!list.isEmpty()) {
				StringBuilder buf = new StringBuilder();
				buf.append("La factura ha sido declarada en algún modelo fiscal.");
				for ( int i = 0; i < list.size(); i++ ) {
					Object[] obj = (Object[])list.get(i);
					Integer year = (Integer)obj[1];
					Byte period = (Byte)obj[2];
					String model = (String)obj[3];
					Byte admon = (Byte)obj[4];
					buf.append("  [Modelo ");
					buf.append(model);
					buf.append(" - ");
					buf.append(year);
					buf.append(" - ");
					buf.append(Period.safeValueOf(period).getName());
					buf.append(" (");
					buf.append(Administration.safeValueOf(admon).getDescription());
					buf.append(")]");
				}
				throw new ManagerBeanVetoListenerException(buf.toString());
			}
		}
	}

}
