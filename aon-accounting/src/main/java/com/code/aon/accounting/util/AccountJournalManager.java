package com.code.aon.accounting.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;

public class AccountJournalManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountJournalManager.class.getName()); 
	private IManagerBean bean;

	public IManagerBean getBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AccountHelper.class);
		}
		return bean;
	}

	public void regenerateJournalCounter(Period period,SecurityLevel securityLevel,IProgression progressionBean) throws ManagerBeanException {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				AccountPeriodStatus originalStatus;
				boolean periodChanged=false;
				originalStatus=AccountPeriodStatus.ACTIVE;
				
				if(period.getStatus()!=AccountPeriodStatus.ACTIVE){
					IManagerBean bean = BeanManager.getManagerBean(Period.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.PERIOD_ID), period.getId());
					List<ITransferObject> list = bean.getList(criteria);
					   for (ITransferObject to : list ) {
				        	Period  per = (Period) to;
				        	originalStatus=per.getStatus();
				        	periodChanged=true;
				        	per.setStatus(AccountPeriodStatus.ACTIVE);
					        bean.update(per);    	
				        
				        }
				}
				
				AccountingUtil util = new AccountingUtil();
				boolean opening = util.existsEntry(period, AccountEntryType.OPENING, securityLevel);
				boolean operating = util.existsEntry(period, AccountEntryType.OPERATING, securityLevel);
				boolean closing = util.existsEntry(period, AccountEntryType.CLOSING, securityLevel);
					
				IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period.getId());
				criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel );
				criteria.addOrder(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE));
				criteria.addOrder(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID));
				
				List<ITransferObject> list = bean.getList(criteria); 
				int count = list.size();
		        int i = 0;
		        int journal = (opening?2:1);
		        for (ITransferObject to : list ) {
		        	boolean mustAdd = false;
		        	AccountEntry entry = (AccountEntry) to;
		        	if (opening && entry.getType() == AccountEntryType.OPENING ) {
		        		entry.setJournal(1);
		        	} else  if (operating && entry.getType() == AccountEntryType.OPERATING ) {
		        		entry.setJournal(count - (closing?1:0));
		        	} else if (closing && entry.getType() == AccountEntryType.CLOSING ) {
		        		entry.setJournal(count);
		        	} else {
		        		entry.setJournal(journal);	
		        		mustAdd = true;
		        	}
		        	entry.setDateDirty(false);
			        bean.update(entry);    	
		        	progressionBean.setProgressionCurrentValue((long) ( i * 100 / count));
		        	i++;
		        	if (mustAdd) {
		        		journal++;
		        	}
		        }
		        
				if(periodChanged){
					IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
					Criteria criteria2 = new Criteria();
					criteria2.addEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), period.getId());
					List<ITransferObject> list2 = periodBean.getList(criteria2);
					   for (ITransferObject to : list2 ) {
				        	Period  per = (Period) to;
				        	per.setStatus(originalStatus);
				        	periodBean.update(per);    	
				        
				        }
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		
	}
}
