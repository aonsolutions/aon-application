package com.code.aon.accounting.event;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class AmortizationVetoableBeanListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {

			Amortization to = (Amortization) evt.getTo();

			AmortizationType at = to.getAmortizationType();
			to.setPercentage(at.getPercentage());

			AccountUtil util = new AccountUtil();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

			Account account = new Account();
			account.setId(util.obtainNextAccountId(at.getFixedAssetAccount().getId()));
			account.setDescription(to.getDescription());
			to.setFixedAssetAccount((Account) accountBean.insert(account));

			account = new Account();
			account.setId(util.obtainNextAccountId(at.getAccumulatedAccount().getId()));
			account.setDescription("Amortización Acumulada " + to.getDescription());
			to.setAccumulatedAccount((Account) accountBean.insert(account));

			account = new Account();
			account.setId(util.obtainNextAccountId(at.getAllocationAccount().getId()));
			account.setDescription("Amortización " + to.getDescription());
			to.setAllocationAccount((Account) accountBean.insert(account));
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		} catch (ExpressionException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			Amortization a = (Amortization) evt.getTo();
			IManagerBean bean = BeanManager.getManagerBean(Amortization.class);
			Amortization bd =  (Amortization) bean.get(a.getId());
			HibernateUtil.getSession(HibernateUtil.getSessionFactoryName()).evict(bd);
			if (!bd.getAmount().equals(a.getAmount()) || !bd.getInitialDate().equals(a.getInitialDate())) {
				IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
				Criteria c = new Criteria();
				c.addEqualExpression(detailBean.getFieldName(IAccountingAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID),a.getId());
				List<ITransferObject> details = detailBean.getList(c);
				for (ITransferObject detail: details) {
					detailBean.remove(detail);	
				}
			} else {
				if (a.getDeadline() != null && a.getSaleAmount() != null) {
					cancelAmortization(a);
				}
			}
			if (!StringUtils.equals(a.getDescription(), a.getFixedAssetAccount().getDescription())) {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				
				a.getFixedAssetAccount().setDescription(a.getDescription());
				accountBean.update(a.getFixedAssetAccount());
				
				a.getAccumulatedAccount().setDescription("Amortización Acumulada " + a.getDescription());
				accountBean.update(a.getAccumulatedAccount());
				
				a.getAllocationAccount().setDescription("Amortización " + a.getDescription());
				accountBean.update(a.getAllocationAccount());
			}
			
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void cancelAmortization(Amortization a) throws ManagerBeanException, ManagerBeanVetoListenerException {
		Date cancelDate =  DateUtils.addDays(a.getDeadline(), -1);
		checkCancelDate(a,cancelDate);
	}

	private void checkCancelDate(Amortization a,Date cancelDate) throws ManagerBeanException, ManagerBeanVetoListenerException {
		IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
		Criteria c = new Criteria();
		c.addEqualExpression(detailBean.getFieldName(IAccountingAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID),a.getId());
		c.addOrder(detailBean.getFieldName(IAccountingAlias.AMORTIZATION_DETAIL_FROM_DATE));
		List<ITransferObject> details = detailBean.getList(c);
		for (ITransferObject tro: details) {
			AmortizationDetail detail = (AmortizationDetail) tro;
			Date from = detail.getFromDate();
			Date to =  detail.getToDate();
			if (from.before(cancelDate) || from.equals(cancelDate)) {
				if (to.equals(cancelDate)) {
					// Nada, la fecha de cancelación coincide con la fecha final del periodo de amortización.
				} else {
					if (to.before(cancelDate)) {
						//Nada. Es un periodo anterior.
					} else {
						if (detail.getStatus() == AmortizationDetailStatus.BLOCKED || 
							detail.getStatus() == AmortizationDetailStatus.SCORED) {
							throw new ManagerBeanVetoListenerException("La cuota de amortización está bloqueada o contabilizada.");
						}
						int days = (int) CommonUtil.getDaysBetweenDates(from, to);
						int newDays = (int) CommonUtil.getDaysBetweenDates(from, cancelDate );
						double newAllocation = CommonUtil.round( detail.getAllocation() * newDays / days );
						detail.setAllocation(newAllocation);
						detail.setToDate(cancelDate);
						detailBean.update(detail);					
					}
				}
			} else {
				// El periodo es posterior.
				if (detail.getStatus() == AmortizationDetailStatus.BLOCKED || 
						detail.getStatus() == AmortizationDetailStatus.SCORED) {
						throw new ManagerBeanVetoListenerException("Existe una cuota posterior a la fecha de cancelación bloqueada o contabilizada.");
				}
				detailBean.remove(detail);
			}
		}
	}

}