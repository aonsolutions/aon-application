package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IncomeInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.INCOME);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());

				IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
				Income income = incomeDetail.getIncome();
				if (income.getStatus() != IncomeStatus.PENDING) {
					income.setStatus(IncomeStatus.PENDING);
					incomeBean.update(income);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}