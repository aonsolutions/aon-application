package com.code.aon.ui.accounting.check.modules.account.invoice;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class NoRecordedInvoiceCheck implements ICheckModule {

	private static final String LABEL = "Chequeo de facturas contabilizadas sin apunte contable.";
	private static final String NO_RECORDED_INVOICE = "Factura contabilizada sin apunte contable.";
	private boolean enabled;
	private List <ICheckEntry> list;

	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntryInvoice.class.getName());
			String select = 
				"SELECT id"
				+" FROM invoice i"
				+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
				+" AND i.status = 1 "
				+" AND i.id not in ("
				+"   SELECT aei.invoice " 
				+"     FROM account_entry_invoice aei " 
				+"     WHERE aei.invoice = i.id)";
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
			List<?> queryList = query
					.addScalar("id", Hibernate.INTEGER)
					.list();
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			for (Object o : queryList) {
				Integer id = (Integer) o;
				NoRecordedInvoiceCheckEntry e = new NoRecordedInvoiceCheckEntry();
				Invoice invoice = (Invoice) invoiceBean.get(id);
				e.setMessage( "[" + invoice.getDocumentNumber() + "] " + NO_RECORDED_INVOICE );
				e.setTo(invoice);
				list.add(e);
			}
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(),e);
		}
	}

	@Override
	public List<ICheckEntry> getCheckList() {
		return list;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public CheckCategory getCategory() {
		return CheckCategory.ACCOUNTING;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

}
