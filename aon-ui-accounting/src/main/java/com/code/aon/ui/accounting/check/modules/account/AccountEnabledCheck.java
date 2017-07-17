package com.code.aon.ui.accounting.check.modules.account;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.io.Serializable;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class AccountEnabledCheck implements ICheckModule, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String label = "Chequeo de integridad de cuentas contables.";
	private String accountEntryEnabled = "Cuenta de nivel inferior, permite apuntes. [{0} - {1}]";
	private String accountNotEntryEnabled = "Cuenta de nivel superior, no permite apuntes. [{0} - {1}]";
	private String notEntryEnabledFixLabel = "Permitir Apuntes";
	private String entryEnabledFixLabel = "No Permitir Apuntes";
	
	private List<ICheckEntry> list;
	private boolean enabled;
	
	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		Connection connection = null; 
		try {
			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			list = getNotEnabledAccounts(ctx,params);
			list.addAll( getEnabledAccounts(ctx,params) );
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AonCheckException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}

	private Collection<? extends ICheckEntry> getEnabledAccounts(DSLContext ctx, CheckParams params) throws ManagerBeanException {
		List<ICheckEntry> list = new LinkedList<ICheckEntry>();
		Result<Record3<Integer,String,String>> record = ctx.select(ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DESCRIPTION)
				.from(ACCOUNT)
				.where(ACCOUNT.DOMAIN.equal(params.getDomainId()))
				.and(ACCOUNT.ENTRYENABLED.equal((byte) 1))
				.and(ACCOUNT.LEVEL.lessThan((byte) 5))
				.fetch();
		for (Record3<Integer,String,String> step : record) {
			Integer id = step.getValue(ACCOUNT.ID);
			AccountEnabledCheckEntry e = new AccountEnabledCheckEntry();
			e.setMessage( MessageFormat.format(accountEntryEnabled, 
					step.getValue(ACCOUNT.CODE),step.getValue(ACCOUNT.DESCRIPTION)));
			e.setId(id);
			e.setFixActionLabel(entryEnabledFixLabel);
			list.add(e);
		}
		return list;
	}

	private List<ICheckEntry> getNotEnabledAccounts(DSLContext ctx, CheckParams params) throws ManagerBeanException {
		List<ICheckEntry> list = new LinkedList<ICheckEntry>();
		Result<Record3<Integer,String,String>> record = ctx.select(ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DESCRIPTION)
				.from(ACCOUNT)
				.where(ACCOUNT.DOMAIN.equal(params.getDomainId()))
				.and(ACCOUNT.ENTRYENABLED.equal((byte) 0))
				.and(ACCOUNT.LEVEL.equal((byte) 5))
				.fetch();
		for (Record3<Integer,String,String> step : record) {
			Integer id = step.value1();
			AccountEnabledCheckEntry e = new AccountEnabledCheckEntry();
			e.setMessage( MessageFormat.format(accountNotEntryEnabled , 
					step.getValue(ACCOUNT.CODE),step.getValue(ACCOUNT.DESCRIPTION)));
			e.setFixActionLabel(notEntryEnabledFixLabel);
			e.setId(id);
			list.add(e);
		}
		return list;
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
		return CheckCategory.ACCOUNT;
	}

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}

}