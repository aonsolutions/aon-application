package com.code.aon.accounting.amortization;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.util.CommonUtil;

public class AmortizationManager {

	public void deleteDetails(Amortization a) {
		a.getDetails().clear(); 
	}

	public void generateDetails(Amortization a) {
		if (a.getInitialDate() == null) {
			throw new IllegalArgumentException("Initial Date can not be null");
		}
		if (a.getAmortizationType() == null) {
			throw new IllegalArgumentException("Amortization Type can not be null");
		}
		if (a.getAmount() == null) {
			throw new IllegalArgumentException("Amount can not be null");
		}
		double percent = a.getAmortizationType().getPercentage();
		int years = (int) Math.floor(100 / percent);
		Date first = a.getInitialDate();
		Date last = null;
		if (a.getDeadline() == null) {
			int currentYear = CommonUtil.getYear(a.getInitialDate());
			last = DateUtils.setYears(first, (currentYear + years));
			last = DateUtils.addDays(last, -1);
		} else {
			last = a.getDeadline();
		}
		long days = CommonUtil.getDaysBetweenDates(first, last);
		double dayAllocation = a.getAmount() / days; // No se redondea a posta.
		Date periodFirst = a.getInitialDate();
		Date periodLast = CommonUtil.getYearLastDay(periodFirst);
		double pending = a.getAmount();
		boolean lastFee = false;
		while (periodFirst.before(last)) {
			AmortizationDetail detail = new AmortizationDetail();
			double allocation;
			if (!lastFee) {
				long periodDays = CommonUtil.getDaysBetweenDates(periodFirst, periodLast) + 1;
				if (periodDays > 365) {
					periodDays = 365;
				}
				if (periodDays == 365) {
					allocation = CommonUtil.round(a.getAmount() * percent / 100);
				} else {
					allocation = CommonUtil.round(periodDays * dayAllocation);
				}

			} else {
				if (a.getSaleAmount() == null) {
					allocation = pending;	
				} else {
					allocation = CommonUtil.round(pending - a.getSaleAmount());
				}
				
			}
			pending = CommonUtil.round(pending - allocation);
			detail.setAllocation(allocation);
			detail.setCoefficient(CommonUtil.round(allocation * 100 / a.getAmount()));
			detail.setFromDate(periodFirst);
			detail.setToDate(periodLast);
			detail.setAmortization(a);
			detail.setPending(pending);
			detail.setStatus(AmortizationDetailStatus.PENDING);

			a.getDetails().add(detail);

			periodFirst = DateUtils.addDays(periodLast, 1);
			periodLast = CommonUtil.getYearLastDay(periodFirst);
			if (periodLast.after(last)) {
				periodLast = last;
			}
			if (DateUtils.isSameDay(periodLast, last)) {
				lastFee = true;
			}
		}

	}
}
