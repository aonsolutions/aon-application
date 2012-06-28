package com.esferalia.aon.ui.pms.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.ui.pms.event.PosFinanceSearchListener;

public class PosFinanceController extends BasicController implements IPmsConstants {
	
	private boolean showFinanceBatchWindow;
	private boolean newBatch;
	private FinanceBatch financeBatch;
	private ArrayList<Finance> checks = new ArrayList<Finance>();
	
	public boolean isShowFinanceBatchWindow() {
		return showFinanceBatchWindow;
	}

	public void setShowFinanceBatchWindow(boolean showFinanceBatchWindow) {
		this.showFinanceBatchWindow = showFinanceBatchWindow;
	}
	
	public boolean isNewBatch() {
		return newBatch;
	}

	public void setNewBatch(boolean newBatch) {
		this.newBatch = newBatch;
	}
	
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}

	public void onSearch(ActionEvent event) {
		clearCheckedFinances();
		super.onSearch(event);
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Finance to = (Finance) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			Finance finance = (Finance)ito;
			if (!checks.contains(finance)) {
				checks.add(finance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}
	
	public int getCheckedCount() {
		return getCheckedFinances().size();
	}

	public Double getSelectedAmount(){
		Double total = 0.0;
		for (Finance finance : getCheckedFinances()) {
			total += CommonUtil.round(finance.getTotalAmount());
		}
		return CommonUtil.round(total);
	}


	public PosShift getFinancePosShift() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData();
			PosFinanceSearchListener searchListener = (PosFinanceSearchListener)AonUtil.getRegisteredBean(POS_FINANCE_SEARCH_LISTENER_NAME);

			IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), searchListener.getHotel().getWorkPlace().getId());
			criteria.addLessThanOrEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME), finance.getInvoice().getCreationDate());
			criteria.addGreaterThanOrEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME), finance.getInvoice().getCreationDate());
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USER_LOGIN), finance.getInvoice().getCreationUser());
			for (ITransferObject ito : posShiftBean.getList(criteria)) {
				return (PosShift)ito;
			}
		}
		return null;
	}

	public void onFinanceBatchShow(ActionEvent event) throws ManagerBeanException {
		setNewBatch(true);
		setFinanceBatch(createFinanceBatch());
	}

	public void onBatchModeChanged(ActionEvent event) {
		if (isNewBatch()) {
			setFinanceBatch(createFinanceBatch());
		}
	}

	private FinanceBatch createFinanceBatch() {
		PosFinanceSearchListener searchListener = (PosFinanceSearchListener)AonUtil.getRegisteredBean(POS_FINANCE_SEARCH_LISTENER_NAME);
		String date = new SimpleDateFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_date_pattern")).format(searchListener.getEndDate());
		String hotel = searchListener.getHotel().getWorkPlace().getDescription();
		String payMethod = searchListener.getPayMethod().getName();
		if ((hotel+ payMethod).length() > 20) {
			payMethod = payMethod.substring(0, 8);
			hotel = hotel.substring(0, 20 - payMethod.length());
		}

		FinanceBatch fBatch = new FinanceBatch();
		fBatch.setIssueDate(searchListener.getEndDate());
		fBatch.setDescription(date + " " + hotel + " " + payMethod);
		return fBatch;
	}

	public List<SelectItem> getFinanceBatchList() throws ManagerBeanException {
		PosFinanceSearchListener searchListener = (PosFinanceSearchListener)AonUtil.getRegisteredBean(POS_FINANCE_SEARCH_LISTENER_NAME);
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), false);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.TODO);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		criteria.addGreaterThanOrEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), searchListener.getEndDate());
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE));
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_DESCRIPTION));
		List<SelectItem> fBatchList = new LinkedList<SelectItem>();
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			String date = new SimpleDateFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_date_pattern")).format(fBatch.getIssueDate());
			String amount = new DecimalFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_price_pattern")).format(fBatch.getFinanceBatchTotalAmount());
			
			SelectItem item = new SelectItem(fBatch, date + " " + StringUtils.leftPad(amount, 10, "·") + "EUR. - " +fBatch.getDescription());
			fBatchList.add(item);
		}
		return fBatchList;
	}

	public void onFinanceBatch(ActionEvent event) throws ManagerBeanException {
		FinanceBatch financeBatch = getFinanceBatch();
		if (isNewBatch()) {
			IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
			financeBatch.setPayment(false);
			financeBatch.setFinanceBatchType(FinanceBatchType.NONE);
			financeBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
			financeBatch.setConfidential(false);
			financeBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
			financeBatch = (FinanceBatch)fBatchBean.insert(getFinanceBatch());
			setFinanceBatch(financeBatch);
		}

		if (financeBatch.getFinanceBatchStatus() == FinanceBatchStatus.TODO) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			for (Finance finance : getCheckedFinances()) {
				finance = (Finance)financeBean.get(finance.getId());
				if (finance.getFinanceStatus() == FinanceStatus.PENDING || finance.getFinanceStatus() == FinanceStatus.RETURNED) {
					FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinance(finance);
					fBatchDetail.setFinanceBatch(financeBatch);
					fBatchDetail.setAmount(finance.getTotalAmount());
					fBatchDetail.setStatus(FinanceStatus.BATCHED);
					fBatchDetailBean.insert(fBatchDetail);
				}
			}
		}

		onLoadFinanceBatch(event);
	}

	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch() != null && getFinanceBatch().getId() != null) {
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(FINANCE_BATCH_CONTROLLER_NAME);
			controller.onLoad(event, getFinanceBatch().getId(), POS_FINANCE_LIST_NAME, POS_FINANCE_CONTROLLER_NAME + ".onSearch");
		}
	}

}
