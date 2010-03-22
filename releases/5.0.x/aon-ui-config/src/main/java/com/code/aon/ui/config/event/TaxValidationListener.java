package com.code.aon.ui.config.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class TaxValidationListener extends ControllerAdapter {

	private static final String CONFIG_BUNDLE = "configBundle";
	private static final String ERROR_MESSAGE = "config_invalid_startDate";
	private static final String OVERLAP_ERROR_MESSAGE = "config_date_overlap";
	private Tax oldTax;
	private TaxDetail taxDetail;

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax) event.getController().getTo();
		searchOldTax(tax);
		insertTaxDetail(tax);
	}

	public void insertTaxDetail(Tax tax) throws ControllerListenerException {
		if(!oldTax.getStartDate().equals(tax.getStartDate()) && hasOverlap(tax)){
			tax.setStartDate(oldTax.getStartDate());
			String msg = AonUtil.getMessage(CONFIG_BUNDLE, OVERLAP_ERROR_MESSAGE);
			throw new ControllerListenerException(msg);			
		}
		if((oldTax.getPercentage() != tax.getPercentage() && !oldTax.getStartDate().equals(tax.getStartDate()))||(oldTax.getStartDate().before(tax.getStartDate()))){
				taxDetail = new TaxDetail();
				taxDetail.setTax(tax);
				taxDetail.setValue(oldTax.getPercentage());
				taxDetail.setSurcharge(oldTax.getSurcharge());
				taxDetail.setStartDate(oldTax.getStartDate());
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(tax.getStartDate());
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				taxDetail.setEndDate(calendar.getTime());
				try {
					IManagerBean bean = BeanManager.getManagerBean(TaxDetail.class);
					bean.insert(taxDetail);
					LinesController linesController = (LinesController)FormUtil.getController("taxDetail");
					linesController.onSearch(null);
				} catch (ManagerBeanException e) {
					String msg = "ManagerBean insert error";
					throw new ControllerListenerException(msg);
				}
			}
	}
	
	public void searchOldTax(Tax tax) throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IConfigAlias.TAX_ID), tax.getId());
			oldTax = (Tax)bean.getList(criteria).get(0);
		} catch (ManagerBeanException e) {
			String msg = "ManagerBean search error";
			throw new ControllerListenerException(msg);
		}
	}
	
	public boolean hasOverlap(Tax tax) throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID), tax.getId());
			TaxDetail detail; 
			for(ITransferObject to: bean.getList(criteria)){
				detail = (TaxDetail)to;
				if((tax.getStartDate().after(detail.getStartDate()) || tax.getStartDate().equals(detail.getStartDate()))
						&& (tax.getStartDate().before(detail.getEndDate()) || tax.getStartDate().equals(detail.getEndDate()))){
					tax.setStartDate(oldTax.getStartDate());
					String msg = AonUtil.getMessage(CONFIG_BUNDLE, OVERLAP_ERROR_MESSAGE);
					throw new ControllerListenerException(msg);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "ManagerBean error";
			throw new ControllerListenerException(msg);
		}
		return false;
	}
	
}