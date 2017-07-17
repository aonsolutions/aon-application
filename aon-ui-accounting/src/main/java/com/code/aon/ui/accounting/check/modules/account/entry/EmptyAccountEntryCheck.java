package com.code.aon.ui.accounting.check.modules.account.entry;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.io.Serializable;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class EmptyAccountEntryCheck implements ICheckModule, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String label = "Chequeo de apuntes sin líneas.";
	private String emptyAccountEntry = "Apunte sin líneas.";
	private boolean enabled;
	private List <ICheckEntry> list;
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute(CheckParams params) throws AonCheckException{
		list = new LinkedList<ICheckEntry>();
		Connection connection = null; 
		try {
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);

			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			
			AggregateFunction<Integer> countFunc = DSL.countDistinct(ACCOUNT_ENTRY_DETAIL.ID);
			Result<Record2<Integer,Integer>> record = ctx.select(ACCOUNT_ENTRY.ID,countFunc)
					.from(ACCOUNT_ENTRY)
					.leftOuterJoin(ACCOUNT_ENTRY_DETAIL).onKey()
					.where(ACCOUNT_ENTRY.DOMAIN.equal(params.getDomainId()))
					.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(params.getPeriod().getId()))
					.groupBy(ACCOUNT_ENTRY.ID)
					.having(countFunc.equal(0)).fetch();
			for (Record2<Integer,Integer> step : record) {
				Integer id = step.value1();
				AccountEntry entry = (AccountEntry) entryBean.get(id);
				UnbalancedAccountEntryCheckEntry e = new UnbalancedAccountEntryCheckEntry();
				e.setMessage( emptyAccountEntry );
				e.setTo(entry);
				list.add(e);
			}
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
	public String getLabel() {
		return label;
	}

	@Override
	public CheckCategory getCategory() {
		return CheckCategory.ACCOUNTING;
	}

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}
	
}