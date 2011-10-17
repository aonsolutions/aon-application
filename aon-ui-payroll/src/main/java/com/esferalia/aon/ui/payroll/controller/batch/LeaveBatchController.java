package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;


public class LeaveBatchController extends BasicController {
	
	
	 @SuppressWarnings("unchecked")
		public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
	        IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
	        ContractLeaveDetailListController leaveController = (ContractLeaveDetailListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
	        Iterator iterator = leaveController.getCheckedLeaves().iterator();
	        while (iterator.hasNext()) {
				ContractLeaveDetail detail = (ContractLeaveDetail) iterator.next();
	            detail.setStatus(ContractLeaveStatus.BATCHED);
	            contractLeaveDetailBean.update(detail);
	            LeaveBatchDetail leaveBatchDetail = new LeaveBatchDetail();
				leaveBatchDetail.setContractLeaveDetail(detail);
				leaveBatchDetail.setLeaveBatch((LeaveBatch) getTo());
				leaveBatchDetailBean.insert(leaveBatchDetail);
	        }
	        leaveController.clearCheckedLeaves();
	        loadDetails();
	        onSearchLeaves(event);
		}
	
	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
		IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
        LeaveBatchDetailController leaveBatchDetailController = (LeaveBatchDetailController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator iterator = leaveBatchDetailController.getCheckedLeaveBatchDetails().iterator();
        while(iterator.hasNext()){
        	LeaveBatchDetail leaveBatchDetail = (LeaveBatchDetail) iterator.next();
        	leaveBatchDetail.getContractLeaveDetail().setStatus(ContractLeaveStatus.PENDING);
        	contractLeaveDetailBean.update(leaveBatchDetail.getContractLeaveDetail());
        	leaveBatchDetailBean.remove(leaveBatchDetail);
        }
		leaveBatchDetailController.clearCheckedLeaveBatchDetails();
        loadDetails();
        onSearchLeaves(event);
    }
	
	private void loadDetails() {
        LinesController fBatchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.onSearch(null);
    }
	
	public void onSearchLeaves(ActionEvent event) {
		ContractLeaveDetailListController leaveList = (ContractLeaveDetailListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		leaveList.onSearch(event);
	}
	
	public void onEditSearchLeave(ActionEvent event) throws ManagerBeanException {
		ContractLeaveDetailListController leaveList = (ContractLeaveDetailListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		leaveList.onEditSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onSearchLeaves(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		super.onAccept(event);
		onSearchLeaves(event);
	}

}
