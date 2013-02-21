package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Pos;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceController extends SaleInvoiceController {

	private boolean showFinishTicketWindow;

	public PosInvoiceController() {
		setInvoiceAddressControllerName(POS_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(POS_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(POS_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	public boolean isShowFinishTicketWindow() {
		return showFinishTicketWindow;
	}

	public void setShowFinishTicketWindow(boolean value) {
		this.showFinishTicketWindow = value;
	}

	public void onShowFinishTicket(ActionEvent event) {
		accept(event);

		InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		financeController.onReset(event);
	}

	public void onFinishTicket(ActionEvent event) {
		InvoiceDetailController detailController = (InvoiceDetailController)FormUtil.getController(getInvoiceDetailControllerName());
		detailController.onCancel(event);

		InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		if (financeController.getPaidAmount() < getInvoice().getTotal() && financeController.getPaidAmount() > 0) {
			((Finance)financeController.getTo()).setAmount(financeController.getPaidAmount());
		}
		financeController.onAccept(event);
	}

	public List<SelectItem> getPosList() throws ManagerBeanException {
		List<SelectItem> posList = new LinkedList<SelectItem>();
		IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(posBean.getFieldName(IEntityAlias.POS_NAME));
		for (ITransferObject ito : posBean.getList(criteria)) {
			Pos pos = (Pos)ito;
			SelectItem item = new SelectItem(pos, pos.getName());
			posList.add(item);
		}
		return posList;
	}

}