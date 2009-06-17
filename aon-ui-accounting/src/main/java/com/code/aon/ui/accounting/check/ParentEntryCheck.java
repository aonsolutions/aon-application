package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;

public class ParentEntryCheck implements IAccountCheck {

	private String label = "Chequeo de cuentas contables sin niveles inferiores.";
	private boolean enabled;
	private List<ICheckEntry> list;
	private String parentCheckEntryMsg = "Cuenta sin niveles inferiores.";

	@Override
	public void onExecute(AccountingCheckParams params) throws AccountingCheckException {
		list = new LinkedList<ICheckEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			List<ITransferObject> toList = accountBean.getList(criteria);
			
			for (ITransferObject to: toList) {
				Account account = (Account) to;
				if(account.getLevel()!=1){
					String id;
					int lenght = calculateLevel(account);
					id = account.getId().substring(0, lenght);
					ITransferObject parent = accountBean.get(id);
					if(parent == null){
						ParentCheckEntry e = new ParentCheckEntry();
						e.setMessage( parentCheckEntryMsg );
						e.setTo(account);
						list.add(e);
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(), e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	private int calculateLevel(Account account){
		//return account.getLevel()==5?account.getLevel():account.getLevel()-1;
		return account.getLevel()-1;
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

}