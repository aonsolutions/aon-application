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
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchAttachment;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FDIWriter;


public class LeaveBatchController extends BasicController {
	
	private FDIWriter fdiWriter;
	private FileOutput fileOutput;
	
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
        processFdi();
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
	
	private void processFdi() {
		try {
			generateFdiFile();
			IManagerBean bean = BeanManager.getManagerBean(LeaveBatchAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.LEAVE_BATCH_ATTACHMENT_LEAVE_BATCH_ID), ((LeaveBatch)getTo()).getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.LEAVE_BATCH_ATTACHMENT_ATTACHMENT_TYPE), LeaveBatchAttachmentType.FDI_DOCUMENT);
			List<ITransferObject> list = bean.getList(criteria);
			LeaveBatchAttachment attach;
			if(!list.isEmpty()){
				attach = (LeaveBatchAttachment) list.get(0);
			} else {
				attach = new LeaveBatchAttachment();
				attach.setLeaveBatch((LeaveBatch) getTo());
				attach.setMimeType(MimeType.MIME_TXT);
				attach.setDescription(getFDIWriter().getEti().getFichero());
				attach.setSize(null);
				attach.setAttachmentType(LeaveBatchAttachmentType.FDI_DOCUMENT);
				attach.setScope(null);
			}
			
			File file = getFileOutput().getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				attach.setData(data);
				attach.setAttachDate(new Date());
				bean.insertOrUpdate(attach);
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
	
	private void generateFdiFile() throws ManagerBeanException {
		String loggedUser = AonUtil.getRemoteUser();
		loggedUser = StringUtils.substringBefore(loggedUser, "@");
		setFileOutput(getFDIWriter().createFDI(getLeaveDetailList(), loggedUser));
		if (getFileOutput() != null) {
			if (getFileOutput().getErrors().size() > 0) {
				AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
			}
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
