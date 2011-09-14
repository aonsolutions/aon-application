package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.PayrollVariablesCollectionsController;
import com.esferalia.aon.ui.payroll.controller.contract.VariablesAbstractController.SimpleVariable;

public class AgreementPaymentController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementPaymentController.class.getName());

	private boolean modalPanelVisible;
	private List<SelectItem> concepts;
	private List<String> systemDataVariables;
	private List<String> paymentConcepts;
	private DataModel variablesModel;
	private DataModel undefinedVariablesModel;
	private boolean searchCurrent;
	private AgreementExtra agreementExtra;
	private List<SelectItem> daysList;
	private Month issueMonth;
	
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
	
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	
	private AgreementLevelData data;
	
	public AgreementLevelData getData() {
		return data;
	}
	public void setData(AgreementLevelData data) {
		this.data = data;
	}
	
	public DataModel getUndefinedVariablesModel() {
		return undefinedVariablesModel;
	}
	public void setUndefinedVariablesModel(DataModel undefinedVariablesModel) {
		this.undefinedVariablesModel = undefinedVariablesModel;
	}
	
	public DataModel getVariablesModel() {
		return variablesModel;
	}
	public void setVariablesModel(DataModel variablesModel) {
		this.variablesModel = variablesModel;
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
	
	public DataModel getPaymentsModel(){
		try {
			return this.getModel();
		} catch (ManagerBeanException e) {
			return null;
		}
	}
	public void setPaymentsModel(DataModel paymentsModel) {
		this.setModel(paymentsModel);
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
	
	public List<String> getSystemDataVariables() {
		if (systemDataVariables == null) {
			systemDataVariables = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				
				//TODO ¿Utilizar las fechas del pojo activo?
				Date date = new Date();
				
				String alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					SystemData sd = (SystemData) to;
					systemDataVariables.add(sd.getName());					
				}
			} catch (ManagerBeanException e) {
				// TODO como tratar esto?
			}
		}
		return systemDataVariables;
	}

	public void setSystemDataVariables(List<String> systemDataVariables) {
		this.systemDataVariables = systemDataVariables;
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

	public List<?> expressionContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		for (String systemDataVariable :getSystemDataVariables()){
			if (systemDataVariable.startsWith(filter)) {
				list.add(systemDataVariable);
			}
		}
		for (ContractVariables cv :ContractVariables.values() ){
			if (cv.getName().startsWith(filter)) {
				list.add(cv.getName());		
			}
		}
		Collections.sort(list);
		return list;
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
		reset(true);
		this.onSelect(event);
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
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNewVariableList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(AgreementLevelData data: (List<AgreementLevelData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(AgreementLevelData data: (List<AgreementLevelData>)getUndefinedVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		return list;
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
	
	//******************************************************
	// VARIABLEs EDITOR
	//******************************************************
	private boolean modalHelperPanelVisible;
	
	public boolean isModalHelperPanelVisible() {
		return modalHelperPanelVisible;
	}
	public void setModalHelperPanelVisible(boolean modalHelperPanelVisible) {
		this.modalHelperPanelVisible = modalHelperPanelVisible;
	}
	public Object getExpression() {
		return getData()!=null?getObjectExpression(getData().getExpression()):null;
	}
	public void setExpression(Object expression) {
		getData().setExpression(getStringExpression(expression));
	} 
	
	public List<?> getVariablesCollection() {
		PayrollVariablesCollectionsController c = new PayrollVariablesCollectionsController();
		if(getData().getVariable()==ContractVariables.CNO){
			return c.getCnoList();
		}else if(getData().getVariable()==ContractVariables.TC2){
			return c.getTc2List();
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			return c.getCategoryList();
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			 return c.getQuoteGroupList();
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			return c.getOccupationList();
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			return c.getQuoteItList();
		}
		return null;
	}
	
	private String getStringExpression(Object expression) {
		if (expression instanceof Enum<?>) {
			if (expression instanceof IResourceable) {
//				Enum<?> v = (Enum<?>) expression;
//				return v.toString();
				if (expression instanceof IStringEnum) {
					IStringEnum v = (IStringEnum) expression;
					return "\""+v.getValue()+"\"";
				} else {
					Enum<?> v = (Enum<?>) expression;
					return "\""+v.toString()+"\"";
				}
			}
		}
		return expression.toString();
	}
	
	private Object getObjectExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		return expression;
	}
	
	private CNO cno;
	private ContractCode contractCode;
	private QuoteGroup quoteGroup;
	private OccupationType occupationType;
	private DataModel variableHelperModel;

	public CNO getCno() {
//		if(getData()!=null){
//			handleEditorExpression(getData().getExpression());
//		}
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
	public ContractCode getContractCode() {
//		if(getData()!=null){
//			handleEditorExpression(getData().getExpression());
//		}
//		handleEditorExpression(((ContractData) getVariablesModel().getRowData()).getExpression());
		return contractCode;
	}
	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}
	public QuoteGroup getQuoteGroup() {
//		if(getData()!=null){
//			handleEditorExpression(getData().getExpression());
//		}
//		handleEditorExpression(((ContractData) getVariablesModel().getRowData()).getExpression());
		return quoteGroup;
	}
	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}
	
	
	
	public OccupationType getOccupationType() {
		return occupationType;
	}
	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}
	public DataModel getVariableHelperModel() {
		if(variableHelperModel == null){
			variableHelperModel = new ListDataModel(getVariableHelpList());
		}
		return variableHelperModel;
	}
	public void setVariableHelperModel(DataModel variableHelperModel) {
		this.variableHelperModel = variableHelperModel;
	}
	private List<SimpleVariable> getVariableHelpList() {
		List<SimpleVariable> list = new LinkedList<SimpleVariable>();
		if(isExpressionHelp()){
			// system data variables
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				
				//TODO ¿Utilizar las fechas del pojo activo?
				Date date = new Date();
				
				String alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				for (ITransferObject to: bean.getList(criteria)) {
					SystemData data = (SystemData) to;
					SimpleVariable sv = new SimpleVariable();
					sv.setName(data.getName());
					sv.setDescription(data.getComments());
					list.add(sv);		
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible mostrar las variables del sistema(" + e.getMessage() +")";
				LOGGER.error(msg);
			}
		}
		for(ContractVariables v: ContractVariables.values()){
			SimpleVariable sv = new SimpleVariable();
			sv.setName(v.getName());
			sv.setDescription(v.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
			list.add(sv);
		}
		return list;
	}
	private boolean expressionHelp;
	public boolean isExpressionHelp() {
		return expressionHelp;
	}
	public void setExpressionHelp(boolean expressionHelp) {
		this.expressionHelp = expressionHelp;
	}
	public void onShowVariableNameHelp(ActionEvent event) {
		setVariableHelperModel(null);
		setModalHelperPanelVisible(true);
		setExpressionHelp(false);
	}
	public void onShowVariableExpressionHelp(ActionEvent event) {
		setVariableHelperModel(null);
		setModalHelperPanelVisible(true);
		setExpressionHelp(true);
	}
	public void onCancelVariableHelp(ActionEvent event) {
		setModalHelperPanelVisible(false);
	}
	public void onSelectVariableHelp(ActionEvent event) {
		onCancelVariableHelp(event);
		String var = ((SimpleVariable) getVariableHelperModel().getRowData()).getName();
		if(isExpressionHelp()){
			getData().setExpression((getData().getExpression()==null?"":getData().getExpression()) +" "+ var);
		} else {
			getData().setName(var);
		}
	}
	
	private void initEditor(){
		setData(null);
		setCno(null);
		setQuoteGroup(null);
		setContractCode(null);
	}
	private void handleEditorExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		if(getData().getVariable()==ContractVariables.CNO){
			setCno(CNO.getCnoByValue(expression));
		}else if(getData().getVariable()==ContractVariables.TC2){
			setContractCode(ContractCode.getContractCodeByValue(expression));
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			;
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			setQuoteGroup(QuoteGroup.getQuoteGroupByValue(expression));
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			setOccupationType(OccupationType.getOccupationTypeByValue(expression));
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			;
		}
		
	}
	private void handleDataExpression() {
		if(getData().getVariable()==ContractVariables.CNO){
			getData().setExpression("\""+String.valueOf(getCno().ordinal())+"\"");
		}else if(getData().getVariable()==ContractVariables.TC2){
			getData().setExpression("\""+getContractCode().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			;
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			getData().setExpression("\""+getQuoteGroup().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			getData().setExpression("\""+getOccupationType().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			;
		}
	}
	
	public class SimpleVariable {
		private String name;
		private String description;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
}
