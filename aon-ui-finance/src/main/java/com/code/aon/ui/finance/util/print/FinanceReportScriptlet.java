package com.code.aon.ui.finance.util.print;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceReportScriptlet.class.getName());
	
	private static final String FIELD_ID = "id";
	
	private static final String FIELD_INVOICE = "invoice";
	
	
	public Integer getInvoiceFinanceOrder() throws JRScriptletException{
		try {
			Invoice invoice = (Invoice) super.getFieldValue(FIELD_INVOICE);
			if(invoice!=null && invoice.getId()!=null){
				IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
				criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_ID), (Integer)super.getFieldValue(FIELD_ID));
				return financeBean.getCount(criteria);
			}
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
	public Integer getInvoiceFinancesCount() throws JRScriptletException{
		Invoice invoice = (Invoice) super.getFieldValue(FIELD_INVOICE);
		if(invoice!=null && invoice.getId()!=null){
			return invoice.getFinances().size();
		}
		return null;
	}
	
}