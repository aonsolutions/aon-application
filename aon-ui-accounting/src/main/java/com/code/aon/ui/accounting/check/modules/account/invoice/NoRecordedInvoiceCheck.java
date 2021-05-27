package com.code.aon.ui.accounting.check.modules.account.invoice;

import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.io.Serializable;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class NoRecordedInvoiceCheck implements ICheckModule, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String LABEL = "Chequeo de facturas contabilizadas sin apunte contable.";
	private static final String NO_RECORDED_INVOICE = "Factura contabilizada sin apunte contable.";
	private boolean enabled;
	private List <ICheckEntry> list;

	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		Connection connection = null;
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			Result<Record1<Integer>> record = ctx.select(INVOICE.ID)
					.from(INVOICE)
					.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).onKey()
					.where(INVOICE.DOMAIN.equal(params.getDomainId()))
					.and(INVOICE.STATUS.equal((byte) InvoiceStatus.SCORED.ordinal()))
					.and(ACCOUNT_ENTRY_INVOICE.ID.isNull())
					.fetch();
			for (Record1<Integer> step : record) {
				Integer id = step.value1();
				NoRecordedInvoiceCheckEntry e = new NoRecordedInvoiceCheckEntry();
				Invoice invoice = (Invoice) invoiceBean.get(id);
				e.setMessage( "[" + invoice.getDocumentNumber() + "] " + NO_RECORDED_INVOICE );
				e.setTo(invoice);
				list.add(e);
			}
/*			
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
*/			
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(),e);
		} catch (AonConnectionException e) {
			throw new AonCheckException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
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

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}

}
