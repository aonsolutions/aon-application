package com.code.aon.account.amortization;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.code.aon.account.Amortization;
import com.code.aon.account.AmortizationDetail;
import com.code.aon.account.enumeration.AmortizationDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;

public class AmortizationManager {

	public void deleteDetails(Amortization a) throws ManagerBeanException {
		String delete = "delete from AmortizationDetail as detail where detail.amortization.id = ?";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(delete);
		query.setInteger(0,a.getId());
		query.executeUpdate();
	}

	public void generateDetails(Amortization a) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AmortizationDetail.class);
		if (a.getInitialDate() == null) {
			throw new ManagerBeanException("Initial Date can not be null");
		}
		if (a.getAmortizationType() == null) {
			throw new ManagerBeanException("Amortization Type can not be null");
		}
		if (a.getAmount() == null) {
			throw new ManagerBeanException("Amount can not be null");
		}
		double percent = a.getAmortizationType().getPercentage();
		int years = (int) Math.floor(100 / percent);
		Date first = a.getInitialDate();
		int currentYear = CommonUtil.getYear(a.getInitialDate());
		Date last = DateUtils.setYears(first, (currentYear + years));
		last = DateUtils.addDays(last, -1);
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
				allocation = pending;
			}
			pending = CommonUtil.round(pending - allocation);
			detail.setAllocation(allocation);
			detail.setCoefficient(CommonUtil.round(allocation * 100 / a.getAmount()));
			detail.setFromDate(periodFirst);
			detail.setToDate(periodLast);
			detail.setAmortization(a);
			detail.setPending(pending);
			detail.setStatus(AmortizationDetailStatus.PENDING);

			bean.insert(detail);

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
