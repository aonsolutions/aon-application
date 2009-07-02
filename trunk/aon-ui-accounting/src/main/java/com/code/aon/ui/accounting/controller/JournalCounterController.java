package com.code.aon.ui.accounting.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class JournalCounterController implements IProgression {

	private static final Logger LOGGER = Logger.getLogger(JournalCounterController.class.getName()); 

	
	private Period period;
	private SecurityLevel securityLevel;
	
	private Long progressionCurrentValue = -1L;
	private boolean progressionEnabled;

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void onEditSearch(ActionEvent event) {
		this.setPeriod(null);
	}

	public void regenerateJournalCounter(ActionEvent event) {
		setProgressionCurrentValue(0L);
		regenerateJournalCounter(period);
		setProgressionCurrentValue(-1L);
		AonUtil.addInfoMessage("El número de diario se han regenerado correctamente.");
	}

	@Override
	public Long getProgressionCurrentValue() {
		return progressionCurrentValue;
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
		progressionCurrentValue = currentValue;
	}

	@Override
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}

	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}

	private void regenerateJournalCounter(Period period) {
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
				
				AccountingUtil util = new AccountingUtil();
				boolean opening = util.existsEntry(getPeriod(), AccountEntryType.OPENING, getSecurityLevel());
				boolean operating = util.existsEntry(getPeriod(), AccountEntryType.OPERATING, getSecurityLevel());
				boolean closing = util.existsEntry(getPeriod(), AccountEntryType.CLOSING, getSecurityLevel());
					
				IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period.getId());
				criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel );
				criteria.addOrder(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE));
				criteria.addOrder(bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID));
				
				List<ITransferObject> list = bean.getList(criteria); 
				int count = list.size();
		        int i = 0;
		        int journal = 2;
		        for (ITransferObject to : list ) {
		        	AccountEntry entry = (AccountEntry) to;
		        	if (opening && entry.getType() == AccountEntryType.OPENING ) {
		        		entry.setJournal(1);
		        	} else  if (operating && entry.getType() == AccountEntryType.OPERATING ) {
		        		entry.setJournal(count - (closing?1:0));
		        	} else if (closing && entry.getType() == AccountEntryType.CLOSING ) {
		        		entry.setJournal(count);
		        	} else {
		        		entry.setJournal(journal);	
		        	}
		        	entry.setDateDirty(false);
			        bean.update(entry);    	
		        	setProgressionCurrentValue((long) ( i * 100 / count));
		        	i++;
		        	journal++;
		        }
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "No se han podido regenerar el número de diario. Causa: " + e.getMessage();
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		
	}
}
