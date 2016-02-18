package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_GROUPED;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.finance.controller.FinanceGroupListController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class FinanceControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean refreshTotalAmount;

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		controller.setListTotal(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		controller.setPurchase(false);
		controller.setRegistryBank(null);
		controller.setShowBankManualInput(false);
		controller.setFinanceGroup(false);
		Finance finance = (Finance)controller.getTo();
		finance.setPayment(controller.isPayment());
		finance.setPayroll(controller.isPayroll());
		finance.setManual(true);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(SecurityLevel.OFFICIAL);
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		groupListController.init();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		Finance finance = (Finance)controller.getTo(); 
		controller.setPayment(finance.isPayment());
		controller.setPayroll(finance.isPayroll());
		try {
			controller.setPurchase(BeanManager.getManagerBean(Creditor.class).get(finance.getRegistry().getId()) == null);

			RegistryBank rBank = null;
			if (StringUtils.isNotEmpty(finance.getBankAccount().getBban())) {
				for (SelectItem item : controller.getAllBanks()) {
					RegistryBank tmpRBank = (RegistryBank)item.getValue();
					if (StringUtils.equals(finance.getBankAccount().getIban(), tmpRBank.getBankAccount().getIban())) {
						rBank = tmpRBank;
						if (StringUtils.equals(finance.getBankAlias(), tmpRBank.getAlias())) {
							break;
						}
					}
				}
			}
			controller.setRegistryBank(rBank);
			controller.setShowBankManualInput(controller.getRegistryBank() == null && !StringUtils.isEmpty(finance.getBankAccount().getBban()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		try {
			controller.setFinanceGroup(isFinanceGroup(finance));
			if(controller.isFinanceGroup()){
				controller.buildFinanceGroupList(finance);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		if (controller.isFinanceGroup()) {
			updateGroupedFinances(event, (Finance) controller.getTo());
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Finance finance = (Finance)event.getController().getTo();
		Date dueDate = finance.getDueDate();
		Integer payMethod = (finance.getPayMethod() != null) ? finance.getPayMethod().getId() : null;
		String bankAccount = (finance.getBankAccount() != null) ? finance.getBankAccount().getIban() : null;
		Double amount = finance.getAmount();
		Double expenses = finance.getExpenses();

		String select = "SELECT finance.due_date dueDate, finance.pay_method payMethod, finance.bank_account bankAccount, " +
							"finance.amount amount, finance.expenses expenses " +
    						"FROM finance as finance " +
    						"WHERE finance.id = " + finance.getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query.addScalar("dueDate", Hibernate.DATE).addScalar("payMethod", Hibernate.INTEGER).addScalar("bankAccount", Hibernate.STRING).
        					addScalar("amount", Hibernate.DOUBLE).addScalar("expenses", Hibernate.DOUBLE).list();
        if (!list.isEmpty()) {
        	Object[] obj = (Object[])list.get(0);
            Date savedDueDate = (Date)obj[0];
            Integer savedPayMethod = (Integer)obj[1];
            String savedBankAccount = (String)obj[2];
            Double savedAmount = (Double)obj[3];
            Double savedExpenses = (Double)obj[4];
			if (!finance.isManual()) {
	            if (!ObjectUtils.equals(savedDueDate, dueDate) || !ObjectUtils.equals(savedPayMethod, payMethod) ||
	            		!ObjectUtils.equals(savedBankAccount, bankAccount) || !ObjectUtils.equals(savedAmount, amount) ||
	            		!ObjectUtils.equals(savedExpenses, expenses)) {
	            	finance.setManual(true);
	            }
	        }
            refreshTotalAmount = (!ObjectUtils.equals(savedAmount, amount) || !ObjectUtils.equals(savedExpenses, expenses));
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		if (refreshTotalAmount) {
			FinanceController controller = (FinanceController)event.getController();
			controller.setListTotal(null);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		try {
			controller.ungroupSelected(getGroupedFinances((Finance) controller.getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private boolean isFinanceGroup(Finance finance) throws ManagerBeanException {
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
				String message = AonUtil.getMessage(FINANCE_TRACKING_GROUPED);
				FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, message);
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
