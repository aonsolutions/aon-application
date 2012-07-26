package com.code.aon.ui.finance.event;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.finance.controller.FinanceGroupListController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);	
			Criteria criteria = new Criteria();
			String idAlias = financeBean.getFieldName(IEntityAlias.FINANCE_ID);
			ProjectionList idPL = new ProjectionList(Projection.property(idAlias));
			Expression exp = ExpressionUtilities.getSubQueryExpression(Finance.class, controller.getCriteria(), idPL);
			criteria.addInExpression(idAlias, exp);
			Projection amountProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
			Projection expensesProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_EXPENSES));
			ProjectionList pl = new ProjectionList(amountProjection, expensesProjection);
			Object[] result = (Object[]) financeBean.getUniqueResult(pl, criteria);
			Double amount = CommonUtil.round(result[0]==null?0:(Double)result[0]);
			Double expenses = result[1]==null?0:(Double) result[1];
			controller.setTotalFinanceAmount(amount + expenses);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}		
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		controller.setPurchase(false);
		controller.setShowBankManualInput(false);
		controller.setFinanceGroup(false);
		Finance finance = (Finance)controller.getTo();
		finance.setPayment(controller.isPayment());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(SecurityLevel.OFFICIAL);
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		groupListController.init();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		if(controller.isFinanceGroup()){
			updateGroupedFinances(event, (Finance) controller.getTo());
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		try {
			controller.ungroupSelected(getGroupedFinances((Finance) controller.getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		Finance finance = (Finance) controller.getTo(); 
		controller.setShowBankManualInput(finance.getBank()!=null && finance.getBank().getId()!=null);
		try {
			controller.setFinanceGroup(isFinanceGroup(finance));
			if(controller.isFinanceGroup()){
				controller.buildFinanceGroupList(finance);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	private boolean isFinanceGroup(Finance finance) throws ManagerBeanException{
		if( finance.getInvoice()==null || finance.getInvoice().getId()==null){
			if( containsGroupedFinances(finance) ){
				return true;
			}
		}
		return false;
	}

	private boolean containsGroupedFinances(Finance finance) throws ManagerBeanException {
		return !getGroupedFinances(finance).isEmpty();
	}

	private List<ITransferObject> getGroupedFinances(Finance finance) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);	
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("Finance.financeGroup.id", finance.getId());
		return financeBean.getList(criteria);
	}
	
	private void updateGroupedFinances(ControllerEvent event, Finance financeGroup) {
		FinanceController financeController = (FinanceController)event.getController();
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(OfferDetail.class.getName());
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);

			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			for(Finance finance: groupListController.getGroupList()){
				finance.setFinanceGroup(financeGroup);
				finance.setFinanceStatus(FinanceStatus.SETTLED);
				financeBean.update(finance);
				String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_GROUPED);
				financeController.createFinanceTracking(finance, message);
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg =  "Unable to rollback transaction!";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			String msg =  "Error grouping finances. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
}
