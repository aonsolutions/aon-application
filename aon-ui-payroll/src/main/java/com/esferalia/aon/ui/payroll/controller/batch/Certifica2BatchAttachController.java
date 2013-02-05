package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class Certifica2BatchAttachController extends BatchAttachController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Certifica2BatchAttachController.class);
	private final static String QUERY = "select sum(length(data)) from Certifica2BatchAttachment";
	
	@Override
	public void onRemove(ActionEvent event) {
		BatchDetailController detailController = (BatchDetailController) FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME);
		Certifica2BatchAttachment attach = (Certifica2BatchAttachment)this.getTo();
		if(attach.getAttachmentType() == PayrollBatchAttachmentType.GENERATED_DOCUMENT){
			Certifica2BatchController batchController = (Certifica2BatchController) FormUtil.getController(IPayrollConstants.CERTIFICA2_BATCH_CONTROLLER_NAME);
			batchController.changeBatchStatus(FileStatus.PENDING);
			batchController.setRecorded(false);
			try {
				removeDetailData(attach.getCertifica2Batch());
			} catch (ManagerBeanException e) {
				LOGGER.error("Error removing Certifica2BatchData when removing Certifica2BatchAttach");
			}
		}
		super.onRemove(event);
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	protected String getQuery() {
		return QUERY;
	}
	
	private void removeDetailData(Certifica2Batch certifica2Batch) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
		Criteria criteria = new Criteria();
		criteria.addInExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATA_CERTIFICA2BATCH_DETAIL_ID), getDetailIds(certifica2Batch));
		for(ITransferObject to : bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	private List<Integer> getDetailIds(Certifica2Batch certifica2Batch) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), certifica2Batch.getId());
		List<Integer> list = new LinkedList<Integer>();
		for(ITransferObject to : bean.getList(criteria)){
			list.add(((Certifica2BatchDetail)to).getId());
		}
		return list;
	}
	
}
