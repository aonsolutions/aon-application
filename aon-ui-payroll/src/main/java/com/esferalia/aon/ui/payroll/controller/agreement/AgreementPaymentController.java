package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.IVariablesHandler;

public class AgreementPaymentController extends LinesController implements IVariablesHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementPaymentController.class.getName());

	private boolean modalPanelVisible;
	private List<SelectItem> concepts;
	
	private List<String> paymentConcepts;
	private DataModel paymentsModel;
	
	private AgreementExtra agreementExtra;
	private List<SelectItem> daysList;
	private Month issueMonth;
	
	private AgreementPaymentVariablesHandler handler;
	
	public AgreementPaymentVariablesHandler getHandler() {
		if(handler==null){
			handler = new AgreementPaymentVariablesHandler(this);
		}
		return handler;
	}
	public void setHandler(AgreementPaymentVariablesHandler handler) {
		this.handler = handler;
	}
	public Month getIssueMonth() {
		if(issueMonth==null){
			issueMonth = getAgreementExtra().getIssueDateMonth();
		}
		return issueMonth;
	}
	public void setIssueMonth(Month issueMonth) {
		daysList = null;
		this.issueMonth = issueMonth;
		if(getAgreementExtra()!=null){
			getAgreementExtra().setIssueDateMonth(issueMonth);
		}
	}

	public AgreementExtra getAgreementExtra() {
		checkPaymentExtraStatus();
		return agreementExtra;
	}
	public void setAgreementExtra(AgreementExtra agreementExtra) {
		this.agreementExtra = agreementExtra;
	}
	private void checkPaymentExtraStatus() {
		if(agreementExtra==null && this.getTo()!=null && ((AgreementPayment) this.getTo()).getSalaryType()==SalaryType.EXTRA){
			AgreementPayment ap = (AgreementPayment) this.getTo();
			agreementExtra = new AgreementExtra();
			agreementExtra.setAgreement(ap.getAgreement());
			agreementExtra.setAgreementPayment(ap);
		}
	}

	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public void onTypeChange(ActionEvent event) {
		setConcepts(null);
	}
	
	public List<SelectItem> getConcepts() {
		if (concepts == null) {
			initialiceConcepts();
		}
		return concepts;
	}
	
	public void initialize(){
		setPaymentsModel(null);
	}
	
	public void initialize(ActionEvent event) {
		initialize();
	}
		
	public DataModel getPaymentsModel() {
		if (paymentsModel == null) {
			initializePaymentModel();
		}
		return paymentsModel;
	}
	public void setPaymentsModel(DataModel paymentsModel) {
		this.paymentsModel = paymentsModel;
	}
	private void initializePaymentModel() {
		IController controller = FormUtil.getController(IPayrollConstants.AGREEMENT_CONTROLLER_NAME);
		Agreement a = (Agreement) controller.getTo();
		if(a.getId()!=null){
			try {
				this.clearCriteria();
				this.getCriteria().addEqualExpression(this.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), a.getId());
				this.onSearch(null);
				paymentsModel = new ListDataModel((List<AgreementData>) this.getModel().getWrappedData());
			} catch (ManagerBeanException e) {
				String msg = "Imposible inicializar lar percepciones";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
	}
	
	public boolean isReadOnly(){
		return false;
	}
	
	private void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
			AgreementPayment alp = (AgreementPayment) getTo();
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_TYPE), alp.getType());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				PaymentConcept pc = (PaymentConcept) to;
				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}

	public void setConcepts(List<SelectItem> concepts) {
		this.concepts = concepts;
	}
	
	

	
	
	private List<String> getPaymentConcetps() {
		if (paymentConcepts == null) {
			paymentConcepts = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
				Criteria criteria = new Criteria();
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					PaymentConcept pc = (PaymentConcept) to;
					paymentConcepts.add(pc.getDescription());					
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return paymentConcepts;
	}

	
	
	public List<?> conceptContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		for (String concept :getPaymentConcetps()){
			if (concept.startsWith(filter)) {
				list.add(concept);
			}
		}
		Collections.sort(list);
		return list;
	}
	
	public void onConceptCodeChange(ActionEvent event){
		((AgreementPayment)this.getTo()).getPaymentConcept();
	}

	public void onConceptDescriptionChange(ActionEvent event){
		
	}
	
	public void onEdit(ActionEvent event) {
		IContractPayment row = (IContractPayment) getPaymentsModel().getRowData();
		try {
			this.select(event, (ITransferObject) row);
			initializeVariables(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible seleccionar la percepcion";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		reset(true);
	}

	public void onSave(ActionEvent event) {
		AgreementPayment alp = (AgreementPayment) this.getTo();
		if(alp.getDescription().isEmpty()){
			alp.setDescription(null);
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				super.onAccept(event);
				saveAgreementExtra();
				reset(false);
				setPaymentsModel(null);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	public void onRemove(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				removeAgreementExtra();
				super.onRemove(event);
				reset(false);
				setPaymentsModel(null);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
		setAgreementExtra(null);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		setAgreementExtra(null);
		if(isSalaryExtra()){
			searchAgreementExtra();
		}
	}
	
	private void saveAgreementExtra() throws ManagerBeanException {
		if(getAgreementExtra()!=null){
			IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
			setAgreementExtra((AgreementExtra) bean.insertOrUpdate(getAgreementExtra()));
		}
	}
	
	private void removeAgreementExtra() throws ManagerBeanException {
		if(getAgreementExtra()!=null){
			IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
			bean.remove(getAgreementExtra());
		}
	}
	
	
	private List<ITransferObject> existingAgreementLevelData(String name, AgreementLevel level) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), level.getId());
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	private void searchAgreementExtra(){
		setIssueMonth(null);
		AgreementPayment ap = (AgreementPayment) this.getTo();
		if(ap.getSalaryType()==SalaryType.EXTRA){
			try {
				IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_EXTRA_AGREEMENT_ID), ap.getAgreement().getId());
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_EXTRA_AGREEMENT_PAYMENT_ID), ap.getId());
				List<ITransferObject> list = bean.getList(criteria);
				if(list.isEmpty()){
					AgreementExtra ae = new AgreementExtra();
					ae.setAgreement(ap.getAgreement());
					ae.setAgreementPayment(ap);
					setAgreementExtra(ae);
				} else {
					setAgreementExtra((AgreementExtra) list.get(0));
				}
			} catch (ManagerBeanException e) {
				String msg = "error on searchAgreementExtra";
				LOGGER.error(msg);
			}
		} else {
			setAgreementExtra(null);
		}
	}
	
	public boolean isSalaryExtra(){
		if(this.getTo()!=null && ((AgreementPayment)this.getTo()).getSalaryType()==SalaryType.EXTRA){
			return true;
		}
		return false;
	}
	
	public List<SelectItem> getMonthDays() {
		if(daysList==null){
			daysList = new LinkedList<SelectItem>();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, getIssueMonth().ordinal());
			for(int i=1; i<=cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
				SelectItem item = new SelectItem(i, String.valueOf(i));
				daysList.add(item);			
			}
		}
		return daysList;
	}
	
	@Override
	public List<?> expressionContext(Object suggest) {
		return getHandler().expressionContext(suggest);
	}
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return getHandler().getVariableManagerBean();
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		getHandler().initializeVariables(event);
	}
	@Override
	public void resetVariable() {
		getHandler().resetVariable();
	}
	

	
	
}
