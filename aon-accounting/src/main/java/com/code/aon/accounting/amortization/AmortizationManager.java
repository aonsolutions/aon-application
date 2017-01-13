package com.code.aon.accounting.amortization;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AmortizationManager {

	private boolean brokenPeriod;
	private int brokenPeriodFirstDay;
	private int brokenPeriodFirstMonth;
	private int brokenPeriodLastDay;
	private int brokenPeriodLastMonth;
	
	
	public void deleteDetails(Amortization a) {
		a.getDetails().clear();
	}

	public void generateDetails(Amortization a) throws ManagerBeanException {
		ensureParams(a);
		
		IManagerBean bean = BeanManager.getManagerBean(AmortizationDetail.class);
		deletePendingDetails(a);
		
		Date amortizationFirstDay = a.getInitialDate();
		Date amortizationLastDay = null;
		double percent = a.getPercentage();
		double years = CommonUtil.round(100 / percent);
		int currentYear = CommonUtil.getYear(a.getInitialDate());
		int days = getAmortizationDays(currentYear,years);

		if (a.getDeadline() == null) {
			if (Math.floor(years) == years) {
				amortizationLastDay = DateUtils.setYears(amortizationFirstDay, (currentYear + (int) years));
				amortizationLastDay = DateUtils.addDays(amortizationLastDay, -1);
			} else {
				amortizationLastDay = DateUtils.addDays(amortizationFirstDay, days);
			}
		} else {
			amortizationLastDay = a.getDeadline();
			days = (int) CommonUtil.getDaysBetweenDates(amortizationFirstDay, amortizationLastDay); 
		}

		double dayAllocation = a.getAmount() / days; // No se redondea a posta.
		
		Date periodFirst = a.getInitialDate();
		Date periodLast;
		double pending = a.getAmount();
		boolean lastFee = false;
		while (periodFirst.before(amortizationLastDay)) {
			periodLast = getPeriodLastDay(periodFirst,a.getFeePeriod());
			if (periodLast.after(amortizationLastDay)) {
				periodLast = amortizationLastDay;
			}
			if (DateUtils.isSameDay(periodLast, amortizationLastDay)) {
				lastFee = true;
			}
			periodLast = ensurePeriodLast(bean, a, periodLast);
			
			AmortizationDetail detail  = insertable(bean, a, periodFirst);
			if (detail == null) {
				detail = new AmortizationDetail();
				detail.setStatus(AmortizationDetailStatus.PENDING);
				double allocation;
				if (!lastFee) {
					long periodDays =  CommonUtil.getDaysBetweenDates(periodFirst, periodLast) + 1;
					if (isPeriodComplete(periodFirst,periodLast,a.getFeePeriod())) {
						allocation = CommonUtil.round((a.getAmount() * percent / 100) / a.getFeePeriod().getYearFraction());
					} else {
						// En caso de periodo no completo, se resuelve multiplicando 
						// el número de dias por la cuota diaria.
						allocation = CommonUtil.round(periodDays * dayAllocation);
					}
				} else {
					// La última cuota se cuadra por diferencia.
					allocation = CommonUtil.round(pending - ((a.getSaleAmount() == null) ? 0 : a.getSaleAmount()));
				}
				pending = CommonUtil.round(pending - allocation);
				detail.setAllocation(allocation);
				detail.setCoefficient(CommonUtil.round(allocation * 100 / a.getAmount()));
				detail.setFromDate(periodFirst);
				detail.setToDate(periodLast);
				detail.setAmortization(a);
				detail.setFiscalAllocation(allocation);
				detail.setPending(pending);
				detail.setFiscalAccumulated(0.0);
				detail = (AmortizationDetail) bean.insertOrUpdate(detail);	
			} else {
				periodLast = detail.getToDate();
				pending = CommonUtil.round(pending - detail.getAllocation());

			}
			periodFirst = DateUtils.addDays(periodLast, 1);
		}

	}

	private void deletePendingDetails(Amortization a) throws ManagerBeanException {
		IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
		Criteria c = new Criteria();
		c.addEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID), a.getId());
		c.addEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_STATUS),AmortizationDetailStatus.PENDING);
		List<ITransferObject> details = detailBean.getList(c);
		for (ITransferObject tro: details) {
			AmortizationDetail detail = (AmortizationDetail) tro;
			detailBean.remove(detail);
		}
	}

	private boolean isPeriodComplete(Date first, Date last, AmortizationPeriod feePeriod) {
		Date periodFirstDay = getPeriodFirstDay(first,feePeriod);
		Date periodLastDay = getPeriodLastDay(first,feePeriod);
		return DateUtils.isSameDay(periodFirstDay,first) && DateUtils.isSameDay(periodLastDay,last);
	}

	private Date getPeriodLastDay(Date date, AmortizationPeriod feePeriod) {
		Date lastDay;
		if (feePeriod == AmortizationPeriod.YEARLY) {
			if (brokenPeriod) {
				lastDay = getBrokenPeriodLastDay(date);
			} else {
				lastDay = CommonUtil.getYearLastDay(date);
			}
		} else {
			if (feePeriod == AmortizationPeriod.MONTHLY) {
				lastDay = CommonUtil.getMonthLastDay(date);
			} else  if (feePeriod == AmortizationPeriod.BI_MONTHLY) {
				lastDay = CommonUtil.getBiMonthLastDay(date);	
			} else  if (feePeriod == AmortizationPeriod.QUARTERLY) {
				lastDay = CommonUtil.getQuarterLastDay(date);	
			} else  if (feePeriod == AmortizationPeriod.FOUR_MONTHLY) {
				lastDay = CommonUtil.getFourMonthLastDay(date);	
			} else  { // SEMESTRE
				lastDay = CommonUtil.getHalfYearLastDay(date);	
			} 
			if (brokenPeriod) {
				Date brokenPeriodLastDay = getBrokenPeriodLastDay(date);
				if ( lastDay.after( brokenPeriodLastDay) ) {
					lastDay = brokenPeriodLastDay;
				}
			}
		}
		return lastDay; 
	}
	private Date getBrokenPeriodLastDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, brokenPeriodLastDay);
		c.set(Calendar.MONTH, brokenPeriodLastMonth);
		if ( date.after(c.getTime())) {
			c.set(Calendar.YEAR , c.get(Calendar.YEAR ) + 1);	
		}
		return c.getTime();
	}

	private Date getPeriodFirstDay(Date date,AmortizationPeriod feePeriod) {
		if (feePeriod == AmortizationPeriod.MONTHLY) {
			return  CommonUtil.getMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.BI_MONTHLY) {
			return  CommonUtil.getBiMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.QUARTERLY) {
			return  CommonUtil.getQuarterFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.FOUR_MONTHLY) {
			return  CommonUtil.getFourMonthFirstDay(date);	
		} else  if (feePeriod == AmortizationPeriod.HALF_YEARLY) {
			return  CommonUtil.getHalfYearFirstDay(date);	
		}
		if (brokenPeriod) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.DAY_OF_MONTH, brokenPeriodFirstDay);
			c.set(Calendar.MONTH, brokenPeriodFirstMonth);
			return c.getTime();
		}
		return CommonUtil.getYearFirstDay(date);
	}

	private AmortizationDetail insertable(IManagerBean bean, Amortization a, Date first) throws ManagerBeanException {
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID), a.getId());
		c.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE), first);
		c.addOrder(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE));
		List<ITransferObject> list = bean.getList(c);
		AmortizationDetail exists = null;	
		if (list != null && list.size() > 0) {
			exists = (AmortizationDetail) list.get(0);
		}
		return exists;
	}

	private Date ensurePeriodLast(IManagerBean bean, Amortization a, Date last) throws ManagerBeanException {
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID), a.getId());
		c.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE), last);
		c.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_TO_DATE), last);
		c.addOrder(bean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE));
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			AmortizationDetail exists = (AmortizationDetail) list.get(0);
			last = DateUtils.addDays(exists.getFromDate(), -1); 	
		}
		return last;
	}

	private void ensureParams(Amortization a) throws ManagerBeanException {
		if (a.getInitialDate() == null) {
			throw new IllegalArgumentException("Initial Date can not be null");
		}
		if (a.getAmount() == null) {
			throw new IllegalArgumentException("Amount can not be null");
		}
		brokenPeriod = false;
		brokenPeriodFirstDay = -1;
		brokenPeriodFirstMonth = -1;
		brokenPeriodLastDay = -1;
		brokenPeriodLastMonth = -1;
		IManagerBean bean = BeanManager.getManagerBean(Period.class);
		List<ITransferObject> list = bean.getList(null);
		if ( list != null && list.size() > 0 ) {
			Period period = (Period) list.get(0);
			Date first = period.getInitiationDate();
			if (!DateUtils.isSameDay(CommonUtil.getYearFirstDay(first),first)) {
				brokenPeriod = true;
				Calendar c = Calendar.getInstance();
				c.setTime(first);
				brokenPeriodFirstDay = c.get(Calendar.DAY_OF_MONTH);
				brokenPeriodFirstMonth = c.get(Calendar.MONTH);
				c.setTime(period.getDeadline());
				brokenPeriodLastDay = c.get(Calendar.DAY_OF_MONTH);
				brokenPeriodLastMonth = c.get(Calendar.MONTH);
			}
		}
		
	}

	private int getAmortizationDays(int currentYear,double years) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
		int days = 0;
		int intYears = (int) Math.floor(years);
		double modYears = CommonUtil.round( years - intYears);
		for (int i = 0; i < years-(modYears==0?0:1); i++) {
			days += c.isLeapYear(currentYear + i)?366:365;
		}
		if (modYears > 0) {
			int lastYearDays = c.isLeapYear(currentYear + intYears)?366:365;
			lastYearDays = (int) Math.ceil(lastYearDays * modYears );
			days += lastYearDays;
		}
		return days;
	}

	public AccountEntry recordAllocation(AmortizationDetail detail) throws ManagerBeanException {
		Amortization a = detail.getAmortization();
		AccountingUtil util = new AccountingUtil();
		Date accountEntryDate = detail.getToDate();
		Period period = util.getPeriod(accountEntryDate);
		if (period == null) {
			throw new ManagerBeanException("No existe periodo definido para la fecha del apunte.");
		}
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		if ( a.getInvestAsset() != null && a.getInvestAsset().getActivity() != null) {
			entry.setActivity(a.getInvestAsset().getActivity()); 
		} else {
			entry.setActivity(null);
		}
		entry.setAccountPeriod(period);
		entry.setEntryDate(accountEntryDate);
		entry.setSecurityLevel( a.getSecurityLevel() );
		entry.setType(AccountEntryType.AMORTIZATION);
		entry = (AccountEntry) accountEntryBean.insert(entry);
		
		IManagerBean detailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account accumulated = a.getAccumulatedAccount();
		Account allocation  = a.getAllocationAccount();

		AccountEntryDetail all = new AccountEntryDetail();
		all.setAccountEntry(entry);
		all.setAccount(allocation);
		all.setBalancingAccount(accumulated);
		String concept = "Amort. - " + a.getFixedAssetAccount().getDescription();
		concept = StringUtils.abbreviate(concept, 32);
		all.setConcept(concept);
		all.setLine(0);
		all.setDebit(detail.getAllocation());
		detailBean.insert(all);

		AccountEntryDetail acc = new AccountEntryDetail();
		acc.setAccountEntry(entry);
		acc.setAccount(accumulated);
		acc.setBalancingAccount(allocation);
		acc.setConcept(concept);
		acc.setLine(1);
		acc.setCredit(detail.getAllocation());
		detailBean.insert(acc);
		
		
		return entry;
	}

	public void unrecordAllocation(Integer acccountEntryId) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntryBean.get(acccountEntryId));
	}
	
	
	public void checkSale(Amortization a) throws ManagerBeanException {
		IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
		Criteria c = new Criteria();
		c.addEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID),a.getId());
		Date cancelDate = DateUtils.addDays(a.getDeadline(), -1);
		c.addGreaterThanExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_TO_DATE),cancelDate);
		c.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_STATUS), AmortizationDetailStatus.PENDING); 
		int count = detailBean.getCount(c);
		if (count > 0) {
			throw new ManagerBeanException("Existe una cuota posterior a la fecha de cancelación, bloqueada o contabilizada.");
		}
	}

	public void sale(Amortization a) throws ManagerBeanException {
		IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
		Criteria c = new Criteria();
		Date cancelDate = DateUtils.addDays(a.getDeadline(), -1);
		c.addEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID),a.getId());
		c.addGreaterThanExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_TO_DATE),cancelDate);
		c.addOrder(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE));
		List<ITransferObject> details = detailBean.getList(c);
		for (ITransferObject tro: details) {
			AmortizationDetail detail = (AmortizationDetail) tro;
			Date from = detail.getFromDate();
			Date to =  detail.getToDate();
			if (from.equals(cancelDate) || from.before(cancelDate)) {
					int days = (int) CommonUtil.getDaysBetweenDates(from, to);
					int newDays = (int) CommonUtil.getDaysBetweenDates(from, cancelDate );
					double newAllocation = CommonUtil.round( detail.getAllocation() * newDays / days );
					detail.setAllocation(newAllocation);
					detail.setToDate(cancelDate);
					detailBean.update(detail);
			} else {		
				detailBean.remove(detail);
			}
		}
	}
	
}
