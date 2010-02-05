package com.code.aon.accounting.amortization;

import java.util.Date;
import java.util.GregorianCalendar;

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
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;

public class AmortizationManager {

	public void deleteDetails(Amortization a) {
		a.getDetails().clear();
	}

	public void generateDetails(Amortization a) throws ManagerBeanException {
		if (a.getInitialDate() == null) {
			throw new IllegalArgumentException("Initial Date can not be null");
		}
		if (a.getAmortizationType() == null) {
			throw new IllegalArgumentException("Amortization Type can not be null");
		}
		if (a.getAmount() == null) {
			throw new IllegalArgumentException("Amount can not be null");
		}
		IManagerBean bean = BeanManager.getManagerBean(AmortizationDetail.class);
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
		Date periodLast = CommonUtil.getYearLastDay(periodFirst);
		double pending = a.getAmount();
		boolean lastFee = false;
		while (periodFirst.before(amortizationLastDay)) {
			AmortizationDetail detail = new AmortizationDetail();
			double allocation;
			if (!lastFee) {
				long periodDays = CommonUtil.getDaysBetweenDates(periodFirst, periodLast) + 1;
				// Se desprecia el hecho de que el año sea bisiesto, puesto que el 
				// porcentaje de cuota indicado es anual no diario.
				periodDays = periodDays > 365 ? 365 : periodDays;
				if (periodDays == 365) {
					allocation = CommonUtil.round(a.getAmount() * percent / 100);
				} else {
					// En caso de año no completo, se resuelve multiplicando por la cuota diaria.
					allocation = CommonUtil.round(periodDays * dayAllocation);
				}
			} else {
				// La última cuota se cuadra por diferencia.
				allocation = CommonUtil.round(pending
						- ((a.getSaleAmount() == null) ? 0 : a.getSaleAmount()));
			}
			pending = CommonUtil.round(pending - allocation);
			detail.setAllocation(allocation);
			detail.setCoefficient(CommonUtil.round(allocation * 100 / a.getAmount()));
			detail.setFromDate(periodFirst);
			detail.setToDate(periodLast);
			detail.setAmortization(a);
			detail.setPending(pending);
			detail.setStatus(AmortizationDetailStatus.PENDING);
			detail.setFiscalAllocation(0.0);
			detail.setFiscalAccumulated(0.0);

			bean.insert(detail);

			periodFirst = DateUtils.addDays(periodLast, 1);
			periodLast = CommonUtil.getYearLastDay(periodFirst);
			if (periodLast.after(amortizationLastDay)) {
				periodLast = amortizationLastDay;
			}
			if (DateUtils.isSameDay(periodLast, amortizationLastDay)) {
				lastFee = true;
			}
		}

	}

	private int getAmortizationDays(int currentYear,double years) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
		int days = 0;
		int intYears = (int) Math.floor(years);
		for (int i = 0; i < years-1; i++) {
			days += c.isLeapYear(currentYear + i)?366:365;
		}
		double modYears = CommonUtil.round( years - intYears);
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
		entry.setAccountPeriod(period.getId());
		entry.setEntryDate(accountEntryDate);
		entry.setSecurityLevel(SecurityLevel.OFFICIAL);
		entry.setType(AccountEntryType.AMORTIZATION);
		entry = (AccountEntry) accountEntryBean.insert(entry);
		
		IManagerBean detailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account accumulated = a.getAccumulatedAccount();
		Account allocation  = a.getAllocationAccount();

		AccountEntryDetail all = new AccountEntryDetail();
		all.setAccountEntry(entry);
		all.setAccount(allocation);
		all.setBalancingAccount(accumulated);
		String concept = "Amort. F: " + a.getId()+ " - " + a.getFixedAssetAccount().getDescription() + ")";
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
}
