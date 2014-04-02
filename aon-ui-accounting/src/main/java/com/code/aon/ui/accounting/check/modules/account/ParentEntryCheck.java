package com.code.aon.ui.accounting.check.modules.account;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class ParentEntryCheck implements ICheckModule {

	private String label = "Chequeo de cuentas contables sin niveles inferiores.";
	private boolean enabled;
	private List<ICheckEntry> list;
	private String parentCheckEntryMsg = "Cuenta sin niveles inferiores [{0} - {1}].";
	
	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		Connection connection = null;
		try {
			Stack<String> stack = null;
			connection = DatabaseUtil.getConnection(params.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			Result<Record3<String,Byte,String>> record = 
				ctx.select(ACCOUNT.CODE,ACCOUNT.LEVEL,ACCOUNT.DESCRIPTION)
					.from(ACCOUNT)
					.where(ACCOUNT.DOMAIN.equal(params.getDomainId()))
					.orderBy(ACCOUNT.CODE)
					.fetch();
			for (Record3<String,Byte,String> step : record) {
				String code = step.getValue(ACCOUNT.CODE);
				byte level = step.getValue(ACCOUNT.LEVEL);
				if (level == 1) {
					stack = new Stack<String>();
					stack.push(code);
				} else {
					if (stack == null) {
						// Por si falta el nivel 1, cosa casi imposible..
						stack = new Stack<String>();	
					}
					String parent = stack.peek();
					while (parent.length() >= code.length()) {
						stack.pop();
						parent = stack.peek();
					}
					if (!StringUtils.startsWith(code, parent)) {
						ParentCheckEntry e = new ParentCheckEntry();
						e.setMessage( MessageFormat.format(parentCheckEntryMsg,code,step.getValue(ACCOUNT.DESCRIPTION)));
						list.add(e);
					} else {
						if (( level - parent.length()) > 1) {
							ParentCheckEntry e = new ParentCheckEntry();
							e.setMessage( MessageFormat.format(parentCheckEntryMsg,code,step.getValue(ACCOUNT.DESCRIPTION)));
							list.add(e);
						}
					}
					if (level < 5 ) {
						stack.push(code);		
					}
				}
			}
			stack = null;
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
		return CheckCategory.ACCOUNT;
	}

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}

}