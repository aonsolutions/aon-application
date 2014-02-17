package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchAttachment;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FDIWriter;


public class LeaveBatchController extends BasicController {
	
	private FDIWriter fdiWriter;
	private FileOutput fileOutput;
	private boolean recorded;
	
	private FDIWriter getFDIWriter() {
		if (fdiWriter == null) {
			fdiWriter = new FDIWriter();
		}
		return fdiWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public boolean isRecorded() {
		return recorded;
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}

	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
        LeaveListController leaveController = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = leaveController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			ContractLeaveDetail detail = (ContractLeaveDetail) iterator.next();
            detail.setStatus(ContractLeaveStatus.BATCHED);
            contractLeaveDetailBean.update(detail);
            LeaveBatchDetail leaveBatchDetail = new LeaveBatchDetail();
			leaveBatchDetail.setContractLeaveDetail(detail);
			leaveBatchDetail.setLeaveBatch((LeaveBatch) getTo());
			leaveBatchDetailBean.insert(leaveBatchDetail);
        }
        leaveController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchLeaves(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean leaveBatchDetailBean = BeanManager.getManagerBean(LeaveBatchDetail.class);
		IManagerBean contractLeaveDetailBean = BeanManager.getManagerBean(ContractLeaveDetail.class);
        BatchDetailController leaveBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = leaveBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	LeaveBatchDetail leaveBatchDetail = (LeaveBatchDetail) iterator.next();
        	leaveBatchDetail.getContractLeaveDetail().setStatus(ContractLeaveStatus.PENDING);
        	contractLeaveDetailBean.update(leaveBatchDetail.getContractLeaveDetail());
        	leaveBatchDetailBean.remove(leaveBatchDetail);
        }
		leaveBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchLeaves(event);
    }
	
	private void loadDetails() {
        LinesController fBatchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.onSearch(null);
    }
	
	public void onSearchLeaves(ActionEvent event) {
		LeaveListController leaveList = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		leaveList.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		LeaveListController leaveList = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		leaveList.onEditSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onInit(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		LeaveBatch b = (LeaveBatch) getTo();
		b.setStatus(FileStatus.PENDING);
		super.onAccept(event);
		onSearchLeaves(event);
	}
	
	public void onInit(ActionEvent event) {
		onSearchLeaves(event);
		try {
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}

	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		LeaveBatch b = (LeaveBatch) getTo();
		b.setStatus(FileStatus.PENDING);
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			File file = getFDIWriter().createFDI(getLeaveDetailList(), loggedUser).getFile();
			IManagerBean bean = BeanManager.getManagerBean(LeaveBatchAttachment.class);
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				LeaveBatchAttachment attach;
				attach = new LeaveBatchAttachment();
				attach.setLeaveBatch((LeaveBatch) getTo());
				attach.setMimeType(null);
				attach.setDescription(getFDIWriter().getEti().getFichero()+".FDI");
				attach.setSize(null);
				attach.setAttachmentType(PayrollBatchAttachmentType.GENERATED_DOCUMENT);
				attach.setScope(null);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
				setRecorded(true);
				changeBatchStatus(FileStatus.GENERATED);
				LeaveBatchAttachController controller = (LeaveBatchAttachController) FormUtil.getController("leaveBatchAttach");
				controller.initializeModel();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateFdiFile ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		LeaveBatch b = (LeaveBatch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}

	private List<ContractLeaveDetail> getLeaveDetailList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.LEAVE_BATCH_DETAIL_CONTROLLER_NAME);
		List<ContractLeaveDetail> list = new LinkedList<ContractLeaveDetail>();
		for(ITransferObject to: controller.getWrappedList()){
			LeaveBatchDetail detail = (LeaveBatchDetail) to;
			list.add(detail.getContractLeaveDetail());
		}
		return list;
	}

}
