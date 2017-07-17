package com.code.aon.ui.accounting.check.modules.account.invoice;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;
import com.code.aon.ui.util.AonUtil;

public class DuplicatedInvoicesCheck implements ICheckModule, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String LABEL = "Chequeo de posibles facturas duplicadas.";
	private static final String DUPLICATED_INVOICE = "Existen {0} facturas de \"{1}\" de {2} [{3}], de fecha {4} e importe {5}.";
	private boolean enabled;
	private List <ICheckEntry> list;

	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			AggregateFunction<String> minRname = DSL.min(INVOICE.RNAME);
			AggregateFunction<Integer> countFunc= DSL.count();
			java.sql.Date start = new java.sql.Date(params.getPeriod().getInitiationDate().getTime());
			java.sql.Date end = new java.sql.Date(params.getPeriod().getDeadline().getTime());
			Result<Record6<Byte,java.sql.Date,Double,String,String,Integer>> record = 
				ctx.select(INVOICE.TYPE
					,INVOICE.ISSUE_DATE
					,INVOICE.TOTAL
					,INVOICE.RDOCUMENT
					,minRname
					,countFunc)
					.from(INVOICE)
					.where(INVOICE.DOMAIN.equal(params.getDomainId()))
					.and(INVOICE.ISSUE_DATE.between(start, end))
					.groupBy(INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.TOTAL,INVOICE.RDOCUMENT)
					.having(countFunc.greaterThan(1))
					.fetch();
			DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			Locale locale = AonUtil.getCurrentLocale();
			for (Record6<Byte,java.sql.Date,Double,String,String,Integer> step : record) {
				byte type = step.getValue(INVOICE.TYPE);
				Date date = step.getValue(INVOICE.ISSUE_DATE);
				Double total = step.getValue(INVOICE.TOTAL);
				String document = step.getValue(INVOICE.RDOCUMENT);
				String name = step.getValue(minRname);
				Integer count = step.getValue(countFunc);
				InvoiceType invoiceType = InvoiceType.values()[type];
				DuplicatedInvoicesCheckEntry e = new DuplicatedInvoicesCheckEntry(invoiceType,date,document,total);
				e.setMessage( MessageFormat.format(DUPLICATED_INVOICE,
						count,invoiceType.getName(locale),name
						,document,dateFormatter.format(date),total) );
				list.add(e);
			}
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
