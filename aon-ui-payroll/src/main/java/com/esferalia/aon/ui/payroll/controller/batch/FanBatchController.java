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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.FanBatchAttachment;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.FANWriter;


public class FanBatchController extends BasicController {
	
	private FANWriter fanWriter;
	private FileOutput fileOutput;
	private boolean recorded;

	private FANWriter getFANWriter() {
		if (fanWriter == null) {
			fanWriter = new FANWriter();
		}
		return fanWriter;
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
		IManagerBean fanBatchDetailBean = BeanManager.getManagerBean(FanBatchDetail.class);
        FanListController listController = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
        Iterator<Object> iterator = listController.getCheckHandler().getCheckedList().iterator();
        while (iterator.hasNext()) {
			EnterpriseCCC ccc = (EnterpriseCCC) iterator.next();
            FanBatchDetail fanBatchDetail = new FanBatchDetail();
			fanBatchDetail.setCcc(ccc);
			fanBatchDetail.setFanBatch((FanBatch) getTo());
			fanBatchDetailBean.insert(fanBatchDetail);
        }
        listController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		IManagerBean fanBatchDetailBean = BeanManager.getManagerBean(FanBatchDetail.class);
        BatchDetailController fanBatchDetailController = (BatchDetailController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator<Object> iterator = fanBatchDetailController.getCheckHandler().getCheckedList().iterator();
        while(iterator.hasNext()){
        	FanBatchDetail fanBatchDetail = (FanBatchDetail) iterator.next();
        	fanBatchDetailBean.remove(fanBatchDetail);
        }
		fanBatchDetailController.getCheckHandler().clearCheckedList();
        loadDetails();
        onSearchCCCs(event);
    }
	
	private void loadDetails() {
        LinesController batchDetailController = (LinesController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
        batchDetailController.onSearch(null);
    }
	
	public void onSearchCCCs(ActionEvent event) throws ManagerBeanException {
		FanListController list = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
		list.onSearch(event);
	}
	
	public void onEditSearchList(ActionEvent event) throws ManagerBeanException {
		FanListController list = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
		list.onEditSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		onInit(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		try {
			FanBatch b = (FanBatch) getTo();
			b.setStatus(FileStatus.PENDING);
			super.onAccept(event);
			onSearchCCCs(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onAccept ["+e.getMessage()+"]");
		}
	}
	
	public void onInit(ActionEvent event) {
		try {
			onSearchCCCs(event);
			checkDiskCreated();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onInit ["+e.getMessage()+"]");
		}
	}
	
	@Override
	public void onReset(ActionEvent event) {
		setRecorded(false);
		super.onReset(event);
		FanBatch b = (FanBatch) getTo();
		b.setStatus(FileStatus.PENDING);
		b.setDate(new Date());
		b.setLiquidationType(LiquidationType.L00);
	}

	public void onCreateDisk(ActionEvent event) {
		try {
			FanBatch batch = (FanBatch) getTo();
//			File file = getFANWriter().createFAN(getEnterpriseCCCList(),((FanBatch)getTo()).getLiquidationType(), batch.getYear(), batch.getMonth(), batch.getMonth()).getFile();
			File file = null;
			IManagerBean bean = BeanManager.getManagerBean(FanBatchAttachment.class);
//			if (file != null) {
				FileInputStream in = new FileInputStream(file);
//				byte[] data = IOUtils.toByteArray(in);
//				FanBatchAttachment attach;
//				attach = new FanBatchAttachment();
//				attach.setFanBatch( (FanBatch) getTo());
//				attach.setMimeType(MimeType.MIME_TXT);
//				attach.setDescription(getFANWriter().getEti().getFichero());
//				attach.setSize(null);
//				attach.setAttachmentType(FanBatchAttachmentType.FAN_DOCUMENT);
//				attach.setScope(null);
//				attach.setData(data);
//				attach.setAttachDate(new Date());
//				bean.insertOrUpdate(attach);
//				setRecorded(true);
//				changeBatchStatus(FileStatus.GENERATED);
//				FanBatchAttachController controller = (FanBatchAttachController) FormUtil.getController("fanBatchAttach");
//				controller.initializeModel();
//			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on generateFanFile ["+e.getMessage()+"]");
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("error on generateFanFile ["+e.getMessage()+"]");
		} catch (IOException e) {
			AonUtil.addErrorMessage("error on generateFanFile ["+e.getMessage()+"]");
		}
	}
	
	public void changeBatchStatus(FileStatus status) {
		FanBatch b = (FanBatch) getTo();
		if(b != null){
			b.setStatus(status);
			super.accept(null);
		}
	}

	private void checkDiskCreated() throws ManagerBeanException {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		if(controller.getRowCount()>0){
			setRecorded(true);
		} else {
			setRecorded(false);
		}
	}

	private List<EnterpriseCCC> getEnterpriseCCCList() {
		LinesController controller = (LinesController)FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		List<EnterpriseCCC> list = new LinkedList<EnterpriseCCC>();
		for(ITransferObject to: controller.getWrappedList()){
			FanBatchDetail detail = (FanBatchDetail) to;
			list.add(detail.getCcc());
		}
		return list;
	}

}
