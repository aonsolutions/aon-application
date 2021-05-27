package com.esferalia.aon.ui.sepe.event;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.SepeBatchAttachment;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.batch.Certifica2BatchController;
import com.esferalia.aon.ui.sepe.controller.batch.Certifica2ListController;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

/**
 * Listener added to the Certifica2BatchController
 * 
 */
public class Certifica2BatchControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		controller.setRecorded(false);
		Certifica2Batch batch = (Certifica2Batch) controller.getTo();
		batch.setStatus(FileStatus.PENDING);
		batch.setDate(new Date());
		batch.setEnterprise(SEPEUtils.getInstance().getCurrentDomainEnterprise());
		controller.setNewBatchWizard( null );
		controller.getNewBatchWizard().init();
		Certifica2ListController list = (Certifica2ListController) FormUtil.getController(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		list.setSearchPanelExpanded(true);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		try {
			controller.onSearchContracts(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de contratos");
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		controller.onInit(null);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeLines(event);
	}

	private void removeLines(ControllerEvent event) {
		Certifica2Batch batch = (Certifica2Batch) event.getController().getTo();
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			// batch attachment
			IManagerBean bean = BeanManager.getManagerBean(SepeBatchAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_SOURCE_BATCH), batch.getId());
			SEPEUtils.getInstance().completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SEPE_BATCH_ATTACHMENT_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
				bean.remove(to);
			}
			// batch detail
			bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), batch.getId());
			SEPEUtils.getInstance().completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DETAIL_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
				bean.remove(to);
			}
			
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				throw new AbortProcessingException(msg  + e.getMessage());
			}
			String msg = "Error durante el borrado de datos. ";
			throw new AbortProcessingException(msg  + e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
	    }
	}
	
}
