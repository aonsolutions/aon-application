package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPaymentHandler;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.IVariablesHandler;

public class AgreementPaymentController extends LinesController implements IVariablesHandler, IPaymentHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementPaymentController.class.getName());

	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	
	private boolean quoteExpressionEdition;
	private boolean irpfExpressionEdition;
	private boolean enableExpressionEdition;
	private TaxationType taxation;
	private QuoteType quote;
	
	private List<String> paymentConcepts;
	private DataModel paymentsModel;
	
	private AgreementExtra agreementExtra;
	private List<SelectItem> daysList;
	private Month issueMonth;
	
	private AgreementPaymentVariablesHandler handler;
	
	// PERIOD FOR DATA FILTER
	private boolean searchCurrent;
	private Date inactiveDate;
	
	
	@Override
	public TaxationType getTaxation() {
		if(taxation==null){
			taxation = obtainTaxationType();
		}
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	@Override
	public QuoteType getQuote() {
		if(quote==null){
			quote = obtainQuoteType();
		}
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		if(quote==QuoteType.QUOTE){
			this.setQuoteExpression(this.getExpression());
		}else if(quote==QuoteType.NO_QUOTE){
			this.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			this.setQuoteExpression(IPayrollConstants.IPREM_FORMMULA);
		} else {
			this.setQuoteExpression(null);
		}
	}

	private void changeIrpfExpression() {
		if(taxation==TaxationType.TAXED){
			this.setIrpfExpression(this.getExpression());
		}else if(taxation==TaxationType.NO_TAXED){
			this.setIrpfExpression(IPayrollConstants.ZERO_VALUE);
		} else {
			this.setIrpfExpression(null);
		}
	}
	
	private QuoteType obtainQuoteType() {
		if(this.getQuoteExpression()==null) {
			return null;
		} else if(this.getQuoteExpression().equals(this.getExpression())){
			return QuoteType.QUOTE;
		} else if(this.getQuoteExpression().equals(IPayrollConstants.ZERO_VALUE)){
			return QuoteType.NO_QUOTE;
		} else if(this.getQuoteExpression().equals(IPayrollConstants.IPREM_FORMMULA)){
			return QuoteType.IPREM_EXCESS;
		} else {
			return QuoteType.MANUAL;
		}
	}
	
	private TaxationType obtainTaxationType() {
		if(this.getIrpfExpression()==null){
			return null;
		} else if(this.getIrpfExpression().equals(this.getExpression())){
			return TaxationType.TAXED;
		} else if(this.getIrpfExpression().equals(IPayrollConstants.ZERO_VALUE)){
			return TaxationType.NO_TAXED;
		} else {
			return TaxationType.MANUAL;
		}
	}
	
	public boolean isQuoteExpressionEdition() {
		return quoteExpressionEdition;
	}
	public void setQuoteExpressionEdition(boolean quoteExpressionEdition) {
		this.quoteExpressionEdition = quoteExpressionEdition;
	}
	public boolean isIrpfExpressionEdition() {
		return irpfExpressionEdition;
	}
	public void setIrpfExpressionEdition(boolean irpfExpressionEdition) {
		this.irpfExpressionEdition = irpfExpressionEdition;
	}
	public boolean isEnableExpressionEdition() {
		return enableExpressionEdition;
	}
	public void setEnableExpressionEdition(boolean enableExpressionEdition) {
		this.enableExpressionEdition = enableExpressionEdition;
	}
	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	
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
				this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), a.getId());
				this.getCriteria().addOrder(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_START_DATE), false);
				Expression expr1 = null;
				Expression expr2 = null;
				if(isSearchCurrent()){
					expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
					expr2 = ExpressionUtilities.getNullExpression(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE));
					this.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				} else {
					if(getInactiveDate()!=null){
						expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE), getInactiveDate());
						expr2 = ExpressionUtilities.getNullExpression(this.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE));
						this.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
					}
				}
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
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_TYPE), alp.getType());
			criteria.addOrder(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_CODE));
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
			setAgreementExtra(null);
			if(isSalaryExtra()){
				searchAgreementExtra();
			}
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
		super.accept(event);
		try {
			if(getAgreementExtra()!=null){
				saveAgreementExtra();
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al guardar los datos de la EXTRA";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		reset(false);
		setPaymentsModel(null);
	}

	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	public void onRemove(ActionEvent event) {
		try {
			removeAgreementExtra();
		} catch (ManagerBeanException e) {
			String msg = "Error al borrar los datos de la EXTRA";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		super.onRemove(event);
		reset(false);
		setPaymentsModel(null);
	}
	
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
		setAgreementExtra(null);
		getHandler().setVariablesModel(null);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		
	}
	
	public void onSelectExpressionEdition(ActionEvent event){
		setEnableExpressionEdition( !isEnableExpressionEdition() );
	}
	
	private void saveAgreementExtra() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
		bean.restoreNullSubPOJOs(getAgreementExtra());
		setAgreementExtra((AgreementExtra) bean.insertOrUpdate(getAgreementExtra()));
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
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), level.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	private void searchAgreementExtra(){
		setIssueMonth(null);
		AgreementPayment ap = (AgreementPayment) this.getTo();
		if(ap.getSalaryType()==SalaryType.EXTRA){
			try {
				IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_EXTRA_AGREEMENT_ID), ap.getAgreement().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_EXTRA_AGREEMENT_PAYMENT_ID), ap.getId());
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
		AgreementPayment payment = (AgreementPayment) this.getTo();
		setEnableExpressionEdition( StringUtils.isNotBlank(payment.getExpression()) );
	}
	@Override
	public void resetVariable() {
		getHandler().resetVariable();
	}
	
	@Override
	public String getExpression() {
		AgreementPayment p = ((AgreementPayment) getTo());
		return StringUtils.isNotBlank(p.getExpression()) ? p.getExpression() : p.getPaymentConcept().getExpression();
	}
	@Override
	public String getQuoteExpression() {
		AgreementPayment p = ((AgreementPayment) getTo());
		return p.getQuoteExpression();
	}
	@Override
	public String getIrpfExpression() {
		AgreementPayment p = ((AgreementPayment) getTo());
		return p.getIrpfExpression();
	}
	@Override
	public void setExpression(String expression) {
		AgreementPayment p = ((AgreementPayment) getTo());
		p.setExpression(expression);
	}
	@Override
	public void setQuoteExpression(String expression) {
		AgreementPayment p = ((AgreementPayment) getTo());
		p.setQuoteExpression(expression);
	}
	@Override
	public void setIrpfExpression(String expression) {
		AgreementPayment p = ((AgreementPayment) getTo());
		p.setIrpfExpression(expression);
	}

	@Override
	public Month getMonth() {
		
		return null;
	}

	@Override
	public Integer getYear() {

		return null;
	}
	
	
}
