package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemTagPrintController;
import com.code.aon.ui.purchase.util.PurchaseManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ManufacturingOrderController extends PurchaseController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean showSalesTransferWindow;
	
	private SalesTransferManager salesTransferManager;
	
	
	public boolean isShowSalesTransferWindow() {
		return showSalesTransferWindow;
	}

	public void setShowSalesTransferWindow(boolean showSalesTransferWindow) {
		this.showSalesTransferWindow = showSalesTransferWindow;
	}

	public SalesTransferManager getSalesTransferManager() {
		if (salesTransferManager == null) {
			salesTransferManager = new SalesTransferManager(); 
		}
		return salesTransferManager;
	}

	public void setSalesTransferManager(SalesTransferManager salesTransferManager) {
		this.salesTransferManager = salesTransferManager;
	}

	@Override
	protected String getTableName() {
		return "Purchase";
	}
	
	public void onSalesTransferShow(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();

		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Criteria criteria = new Criteria();
		if (to.getProject() != null && to.getProject().getId() != null) {
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PROJECT_ID), to.getProject().getId());
		}
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_STATUS), SalesStatus.PENDING);
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_SECURITY_LEVEL), to.getSecurityLevel());
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_WORK_PLACE_ID), to.getWorkPlace().getId());
		criteria.addEqualExpression("Sales.lines.item.product.manufactured", Boolean.TRUE);
		
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_ISSUE_DATE));
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_SERIES));
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_NUMBER));
		getSalesTransferManager().setSalesList(salesBean.getList(criteria));

	}

	public void onSalesTransfer(ActionEvent event) {
		Purchase to = (Purchase)this.getTo();
		try {
			PurchaseManager purchaseManager = new PurchaseManager();
			purchaseManager.transferSalesDetails((Purchase)this.getTo(), getSalesTransferManager().getCheckedDetails(), to.getWarehouse());

			refresh(null);
			IController detailController = FormUtil.getController(IPurchaseConstants.MANUFACTURING_ORDER_DETAIL_CONTROLLER_NAME);
			detailController.onSearch(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}
	
	public void onItemTagPrintShow(ActionEvent event) throws ManagerBeanException {
		List<Integer> idList = new LinkedList<Integer>();
		IController detailController = FormUtil.getController(IPurchaseConstants.MANUFACTURING_ORDER_DETAIL_CONTROLLER_NAME);
		List<ITransferObject> list = detailController.getManagerBean().getList(detailController.getCriteria());
		list.forEach(to -> {idList.add(((PurchaseDetail)to).getItem().getId());});
		ItemTagPrintController itemTagController = (ItemTagPrintController) FormUtil.getController(IItemConstants.ITEM_TAG_PRINT_CONTROLLER_NAME);
		itemTagController.onItemTagPrintShow(event, idList);
	}
	
	
	public static class SalesTransferManager extends com.code.aon.sales.bridge.SalesTransferManager {
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		@Override
		protected List<ITransferObject> obtainSalesDetailList(Sales sales) {
			return super.obtainSalesDetailList(sales).stream()
					.map(to -> (SalesDetail)to)
					.filter(detail -> isValidManufactureItem(detail.getItem()))
					.collect(Collectors.toList());
		}

		public boolean isValidManufactureItem(Item item){
			try {
				return item.getProduct().isManufactured()
						&& item.getItemCompositionList().size()>0;
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
}