package com.code.aon.ui.accounting.check.modules.account.invoice;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;
import com.code.aon.ui.util.AonUtil;

public class DuplicatedInvoicesCheck implements ICheckModule {

	private static final String LABEL = "Chequeo de posibles facturas duplicadas.";
	private static final String DUPLICATED_INVOICE = "Existen {0} facturas de \"{1}\" de {2} [{3}], de fecha {4} e importe {5}.";
	private boolean enabled;
	private List <ICheckEntry> list;

	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntryInvoice.class.getName());
			String select =
				"SELECT type,issue_date,total,rdocument document,MIN(rname) name,count(*) count" 
				+" FROM invoice i"
				+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
				+" AND issue_date BETWEEN ? AND ?"
				+" GROUP BY type,domain,issue_date,total,document" 
				+" HAVING count(*) > 1";
			Date start = params.getPeriod().getInitiationDate();
			Date end = params.getPeriod().getDeadline();
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
			query.setDate(0, start);
			query.setDate(1, end);
			List<?> queryList = query
					.addScalar("type", Hibernate.INTEGER)
					.addScalar("issue_date", Hibernate.DATE)
					.addScalar("total", Hibernate.DOUBLE)
					.addScalar("document", Hibernate.STRING)
					.addScalar("name", Hibernate.STRING)
					.addScalar("count", Hibernate.INTEGER)
					.list();
			DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			MessageFormat mf = new MessageFormat(DUPLICATED_INVOICE);
			StringBuffer buf = null;
			Locale locale = AonUtil.getCurrentLocale();
			for (Object o : queryList) {
				Object[] obj = (Object[]) o;
				Integer type = (Integer) obj[0];
				Date date = (Date) obj[1];
				Double total = (Double) obj[2];
				String document = (String) obj[3];
				String name = (String) obj[4];
				Integer count = (Integer) obj[5];
				InvoiceType invoiceType = InvoiceType.values()[type];
				Object[] array = new Object[]{
						count
						,invoiceType.getName(locale)
						,name
						,document
						,dateFormatter.format(date)
						,total
				}; 
				buf = new StringBuffer();
				mf.format(array,buf,null);
				DuplicatedInvoicesCheckEntry e = new DuplicatedInvoicesCheckEntry(invoiceType,date,document,total);
				e.setMessage( buf.toString() );
				list.add(e);
			}
		} catch (Exception e) {
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
