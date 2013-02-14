package com.esferalia.aon.ui.pms.print;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;

public class PmsInvoicePrinter {
	
	public PmsInvoicePrinter getInstance() {
		return new PmsInvoicePrinter();
	}
	
	public ProjectReservation getProjectReservation(Integer projectId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_PROJECT_ID), projectId);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return ((ProjectReservation)list.get(0));
		}
		return null;
	}
	public Hotel getHotel(Integer invoiceId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
		criteria.addOrder(bean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return obtainHotel( ((InvoiceDetail)list.get(0)).getWorkPlace() );
		}
		return null;
	}
	
	private Hotel obtainHotel(WorkPlace workPlace) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), workPlace.getId());
		return (Hotel) bean.getList(criteria).get(0);
	}


}