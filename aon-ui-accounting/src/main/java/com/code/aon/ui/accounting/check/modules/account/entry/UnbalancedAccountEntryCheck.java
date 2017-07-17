package com.code.aon.ui.accounting.check.modules.account.entry;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AccountEntry;
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

public class UnbalancedAccountEntryCheck implements ICheckModule, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String label = "Chequeo de apuntes descuadrados.";
	private boolean enabled;
	private String message = "Apunte descuadrado.";
	private List <ICheckEntry> list;

	/**
	 * Comprueba que los apuntes no esten descuadrados
	 */
	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		Connection connection = null;
		
		try {
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);

			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, getDefaultSettings());
			
			Field<Double> roundFunc = DSL.round(ACCOUNT_ENTRY_DETAIL.DEBIT.sub(ACCOUNT_ENTRY_DETAIL.CREDIT),2);
			AggregateFunction<BigDecimal> sumFunc = DSL.sum(roundFunc);
			
			Result<Record2<Integer,BigDecimal>> record = ctx.select(ACCOUNT_ENTRY.ID,sumFunc)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_ENTRY_DETAIL).onKey()
				.where(ACCOUNT_ENTRY.DOMAIN.equal(params.getDomainId()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(params.getPeriod().getId()))
				.groupBy(ACCOUNT_ENTRY.ID)
				.having(sumFunc.notEqual(new BigDecimal(0))).fetch();
			for (Record2<Integer,BigDecimal> step : record) {
				Integer id = step.value1();
				AccountEntry entry = (AccountEntry) entryBean.get(id);
				UnbalancedAccountEntryCheckEntry e = new UnbalancedAccountEntryCheckEntry();
				e.setMessage( message );
				e.setTo(entry);
				list.add(e);
			}
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(), e);
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
	
	private static Settings SETTINGS = null;

	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}
	

}