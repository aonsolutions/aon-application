package com.code.aon.ui.config.event;

import static com.code.aon.ui.common.ICommonMessages.CONFIG_INVALID_START_DATE;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TaxControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax) event.getController().getTo();
		tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		tax.setWithholdingType(WithholdingType.PROFESSIONAL);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax) event.getController().getTo();
		if (tax.getType() != TaxType.VAT) {
			tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		}
		if (tax.getType() != TaxType.RETENTION) {
			tax.setWithholdingType(WithholdingType.PROFESSIONAL);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax) event.getController().getTo();
		if (tax.getType() != TaxType.VAT) {
			tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		}
		if (tax.getType() != TaxType.RETENTION) {
			tax.setWithholdingType(WithholdingType.PROFESSIONAL);
		}

		insertTaxDetail(tax, searchOldTax(event, tax));
	}

	public Tax searchOldTax(ControllerEvent event, Tax tax) throws ControllerListenerException {
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(event.getController().getFieldName(IEntityAlias.TAX_ID), tax.getId());
			return (Tax)event.getController().getManagerBean().getList(criteria).get(0);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	public void insertTaxDetail(Tax tax, Tax oldTax) throws ControllerListenerException {
		if (!tax.getStartDate().equals(oldTax.getStartDate())) {
			if (hasOverlap(tax)) {
				tax.setStartDate(oldTax.getStartDate());
				throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INVALID_START_DATE));
			}

			if (tax.getStartDate().after(oldTax.getStartDate())) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(tax.getStartDate());
				calendar.add(Calendar.DATE, -1);

				TaxDetail taxDetail = new TaxDetail();
				taxDetail.setTax(tax);
				taxDetail.setValue(oldTax.getPercentage());
				taxDetail.setSurcharge(oldTax.getSurcharge());
				taxDetail.setStartDate(oldTax.getStartDate());
				taxDetail.setEndDate(calendar.getTime());
				try {
					IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
					taxDetailBean.insert(taxDetail);

					LinesController linesController = (LinesController)FormUtil.getController(ConfigConstants.TAX_DETAIL);
					linesController.onSearch(null);
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}
		}
	}
	
	public boolean hasOverlap(Tax tax) throws ControllerListenerException {
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), tax.getId());
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), tax.getStartDate());
			if (taxDetailBean.getCount(criteria) > 0) {
				return true;
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		return false;
	}
	
}