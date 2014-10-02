package com.esferalia.aon.ui.pms.print;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class PmsInvoicePrinter implements IReservationConstants {

	public PmsInvoicePrinter getInstance() {
		return new PmsInvoicePrinter();
	}

	public ProjectReservation getProjectReservation(Integer projectId) throws ManagerBeanException {
		return (ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).get(projectId);
	}

	public Enterprise getEnterprise(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		Enterprise enterprise = invoiceDetail.getWorkPlace().getEnterprise();
		if (!FinanceUtil.isValidLimitDate(invoiceDetail.getInvoice())) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), enterprise.getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), OLD_COMPANY);
			for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
				Integer oldCompanyId = null;
				try {
					oldCompanyId = Integer.valueOf(((RegistryAddInfo)ito).getValue());
				} catch (NumberFormatException ex) {
				}
				if (oldCompanyId != null) {
					Enterprise oldEnterprise = (Enterprise)BeanManager.getManagerBean(Enterprise.class).get(oldCompanyId);
					if (oldEnterprise != null) {
						enterprise = oldEnterprise;
					}
				}
			}
		}
		return enterprise;
	}

	public Hotel getHotel(Integer invoiceId) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> invoiceDetailList = invoiceDetailBean.getList(criteria);
		if (!invoiceDetailList.isEmpty()) {
			return obtainHotel(((InvoiceDetail)invoiceDetailList.get(0)).getWorkPlace());
		}
		return null;
	}

	private Hotel obtainHotel(WorkPlace workPlace) throws ManagerBeanException{
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), workPlace.getId());
		List<ITransferObject> hotelList = hotelBean.getList(criteria);
		if (!hotelList.isEmpty()) {
			return (Hotel)hotelList.get(0);
		}
		return null;
	}

}