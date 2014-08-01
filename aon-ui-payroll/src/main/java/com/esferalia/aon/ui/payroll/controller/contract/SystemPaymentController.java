package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;
import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.SystemPayment;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.IPaymentHandler;

public class SystemPaymentController extends ContractDetailVariableController
		implements IPaymentHandler {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SystemPaymentController.class.getName());

	private boolean typeOverrideEnabled;
	private boolean expressionOverrideEnabled;
	private boolean descriptionOverrideEnabled;
	private boolean irpfExpressionOverrideEnabled;
	private boolean quoteExpressionOverrideEnabled;

	private Throwable expressionException;
	private Throwable irpfExpressionException;
	private Throwable quoteExpressionException;

	private DataModel paymentsModel;

	public DataModel getPaymentsModel() {
		return paymentsModel;
	}

	public void setPaymentsModel(DataModel paymentsModel) {
		this.paymentsModel = paymentsModel;
	}

	public boolean isReadOnly() {
		if (this.getPaymentsModel().isRowAvailable()
				&& this.getPaymentsModel().getRowCount() > 0) {
			return ((IContractPayment) this.getPaymentsModel().getRowData())
					.getScope() != ExpressionScope.CONTRACT;
		}
		return true;
	}

	public void initialize() {
		setPaymentsModel(null);
	}

	public void initialize(ActionEvent event) {
		initialize();
	}

	public void onPaymentConceptChange(ActionEvent event) {
		SystemPayment sp = (SystemPayment) getTo();
		if (sp.getType() != null && StringUtils.isEmpty(sp.getDescription())) {
			sp.setDescription(sp.getPaymentConcept().getDescription());
		}
	}

	public boolean isTypeOverrideEnabled() {
		// Explicitly enable ?
		if (typeOverrideEnabled)
			return true;

		// It's my own description ?
		if (getPaymentType() != null)
			return true;
		// Has parent 'meta' concept ?
		if (getPaymentConcept() == null)
			return true;

		return false;
	}

	public void onEnableTypeOverride(ActionEvent event) {
		typeOverrideEnabled = true;
	}

	public void onDisableTypeOverride(ActionEvent event) {
		typeOverrideEnabled = false;
		((SystemPayment) getTo()).setType(null);
	}

	public boolean isDescriptionOverrideEnabled() {
		// Explicitly enable ?
		if (descriptionOverrideEnabled)
			return true;

		// It's my own description ?
		if (getPaymentDescription() != null)
			return true;
		// Has parent 'meta' concept ?
		if (getPaymentConcept() == null)
			return true;

		return false;
	}

	public void onEnableDescriptionOverride(ActionEvent event) {
		descriptionOverrideEnabled = true;
	}

	public void onDisableDescriptionOverride(ActionEvent event) {
		descriptionOverrideEnabled = false;
		((SystemPayment) getTo()).setDescription(null);
	}

	public String getConceptTypeName() {
		PaymentConcept concept = getPaymentConcept();
		if (concept == null)
			return null;
		PaymentType type = concept.getType();
		if (type == null)
			return null;
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		return type.getName(locale);
	}

	public boolean isExpressionOverrideEnabled() {
		// Explicitly enable ?
		if (expressionOverrideEnabled)
			return true;

		// It's my own description ?
		if (getPaymentExpression() != null)
			return true;
		// Has parent 'meta' concept ?
		if (getPaymentConcept() == null)
			return true;

		return false;
	}

	public void onEnableExpressionOverride(ActionEvent event) {
		expressionOverrideEnabled = true;
	}

	public void onDisableExpressionOverride(ActionEvent event) {
		expressionOverrideEnabled = false;
		((SystemPayment) getTo()).setExpression(null);
	}

	public void onEnableIrpfExpressionOverride(ActionEvent event) {
		irpfExpressionOverrideEnabled = true;
	}

	public void onDisableIrpfExpressionOverride(ActionEvent event) {
		irpfExpressionOverrideEnabled = false;
		((SystemPayment) getTo()).setExpression(null);
	}

	public boolean isIrpfExpressionOverrideEnabled() {
		// Explicitly enable ?
		if (irpfExpressionOverrideEnabled)
			return true;

		// It's my own description ?
		if (getPaymentExpression() != null)
			return true;
		// Has parent 'meta' concept ?
		if (getPaymentConcept() == null)
			return true;

		return false;
	}

	public void onEnableQuoteExpressionOverride(ActionEvent event) {
		quoteExpressionOverrideEnabled = true;
	}

	public void onDisableQuoteExpressionOverride(ActionEvent event) {
		quoteExpressionOverrideEnabled = false;
		((SystemPayment) getTo()).setExpression(null);
	}

	public boolean isQuoteExpressionOverrideEnabled() {
		// Explicitly enable ?
		if (quoteExpressionOverrideEnabled)
			return true;

		// It's my own description ?
		if (getPaymentExpression() != null)
			return true;
		// Has parent 'meta' concept ?
		if (getPaymentConcept() == null)
			return true;

		return false;
	}

	// -------------------------------------------------------------------------

	public boolean isExpressionValid() {
		try {
			analyze(getPaymentExpression());
			return true;
		} catch (Throwable e) {
			expressionException = e;
			return false;
		}
	}

	public String getExpressionErrorMessage() {
		return expressionException.getMessage();
	}

	public boolean isIrpfExpressionValid() {
		try {
			analyze(getPaymentIrpfExpression());
			return true;
		} catch (Throwable e) {
			irpfExpressionException = e;
			return false;
		}
	}

	public String getIrpfExpressionErrorMessage() {
		return irpfExpressionException.getMessage();
	}

	public boolean isQuoteExpressionValid() {
		try {
			analyze(getPaymentQuoteExpression());
			return true;
		} catch (Throwable e) {
			quoteExpressionException = e;
			return false;
		}
	}

	public String getQuoteExpressionErrorMessage() {
		return quoteExpressionException.getMessage();
	}

	public void onChangeExpression(ActionEvent event) {
	}

	public TaxationType getConceptTaxationType() {
		return getTaxationType(getConceptIrpfExpression(),
				getConceptExpression(), getPaymentConcept());
	}

	public QuoteType getConceptQuoteType() {
		return getQuoteType(getConceptQuoteExpression(),
				getConceptExpression(), getPaymentConcept());
	}
	// -------------------------------------------------------------------------
	@Override
	public void onEdit(ActionEvent event) {
		IContractPayment row = (IContractPayment) getPaymentsModel()
				.getRowData();
		try {
			if (row.getScope() == ExpressionScope.CONTRACT) {
				this.select(event, (ITransferObject) row);
				onReloadExpression(event);
			} else {
				super.onReset(event);
				// IController master = FormUtil.getController("contract");
				// Contract contract = (Contract) master.getTo();
				SystemPayment payment = (SystemPayment) this.getTo();
				payment.setType(row.getType());
				payment.setDescription(row.getDescription());
				payment.setExpression(row.getExpression());
				payment.setIrpfExpression(row.getIrpfExpression());
				payment.setQuoteExpression(row.getQuoteExpression());
				payment.setStartDate(row.getStartDate());
				payment.setEndDate(row.getEndDate());
				payment.setMonth(row.getMonth());
				payment.setDescriptionDecorable(row.isDescriptionDecorable());
				payment.setSalaryType(row.getSalaryType());
				if (row.getScope() == ExpressionScope.AGREEMENT) {
					AgreementPayment alp = (AgreementPayment) row;
					payment.setPaymentConcept(alp.getPaymentConcept());
				} else if (row.getScope() == ExpressionScope.SYSTEM) {
					SystemPayment sp = (SystemPayment) row;
					payment.setPaymentConcept(sp.getPaymentConcept());
				}
				onReloadExpression(event);
			}
			reset(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible seleccionar la percepcion";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@Override
	public TaxationType getTaxation() {
		return getTaxationType(getIrpfExpression(), getExpression(),
				getPaymentConcept());
	}

	public void setTaxation(TaxationType taxation) {
		
	}

	@Override
	public QuoteType getQuote() {
		return getQuoteType(getPaymentQuoteExpression(), getExpression(),
				getPaymentConcept());
	}

	public void setQuote(QuoteType quote) {
	}

	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
	}

	@Override
	public void select(ActionEvent arg0) {
		super.select(arg0);
	}

	@Override
	public void onSave(ActionEvent event) {
	}

	@Override
	protected void completeCiteria() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initialiceConcepts() {
	}

	// -------------------------------------------------------------------------

	@Override
	public SalaryType getSalaryType() {
		return ((SystemPayment) getTo()).getSalaryType();
	}

	@Override
	public String getExpression() {
		SystemPayment cp = ((SystemPayment) getTo());
		return StringUtils.isNotBlank(cp.getExpression()) ? cp.getExpression()
				: cp.getPaymentConcept().getExpression();
	}

	@Override
	public String getQuoteExpression() {
		SystemPayment cp = ((SystemPayment) getTo());
		return cp.getQuoteExpression();
	}

	@Override
	public String getIrpfExpression() {
		SystemPayment cp = ((SystemPayment) getTo());
		return cp.getIrpfExpression();
	}

	@Override
	public void setExpression(String expression) {
		SystemPayment cp = ((SystemPayment) getTo());
		cp.setExpression(expression);
	}

	@Override
	public void setQuoteExpression(String expression) {
		SystemPayment cp = ((SystemPayment) getTo());
		cp.setQuoteExpression(expression);
	}

	@Override
	public void setIrpfExpression(String expression) {
		SystemPayment cp = ((SystemPayment) getTo());
		cp.setIrpfExpression(expression);
	}

	// --------------------------------------------------------- Private Methods

	private void analyze(String expression) {
		try {
			ExpressionContext.analyze(expression);
		} catch (PropertyAccessException e) {

		} catch (UnresolveablePropertyException e) {
		}
	}

	private PaymentType getPaymentType() {
		return ((SystemPayment) getTo()).getType();
	}

	private String getPaymentDescription() {
		return ((SystemPayment) getTo()).getDescription();
	}

	private String getPaymentExpression() {
		return ((SystemPayment) getTo()).getExpression();
	}

	private String getPaymentIrpfExpression() {
		return ((SystemPayment) getTo()).getIrpfExpression();
	}

	private String getPaymentQuoteExpression() {
		return ((SystemPayment) getTo()).getQuoteExpression();
	}

	private PaymentConcept getPaymentConcept() {
		return ((SystemPayment) getTo()).getPaymentConcept();
	}

	private String getConceptExpression() {
		return getPaymentConcept().getExpression();
	}

	private String getConceptIrpfExpression() {
		return getPaymentConcept().getIrpfExpression();
	}

	private String getConceptQuoteExpression() {
		return getPaymentConcept().getQuoteExpression();
	}

	private TaxationType getTaxationType(String tax, String pay,
			PaymentConcept concept) {
		if (StringUtils.isBlank(tax))
			return TaxationType.NO_TAXED;

		try {
			if (Double.valueOf(tax) == 0.00)
				return TaxationType.NO_TAXED;
		} catch (NumberFormatException nan) {
		}

		if (tax.equals(ContextVariable.ALL))
			return TaxationType.TAXED;

		if (tax.equals(pay))
			return TaxationType.TAXED;

		if (concept == null)
			return TaxationType.MANUAL;

		if (tax.equals(concept.getCode()))
			return TaxationType.TAXED;

		return TaxationType.MANUAL;

	}

	private QuoteType getQuoteType(String quote, String pay,
			PaymentConcept concept) {

		if (StringUtils.isBlank(quote))
			return QuoteType.NO_QUOTE;

		try {
			if (Double.valueOf(quote) == 0.00)
				return QuoteType.NO_QUOTE;
		} catch (NumberFormatException nan) {
		}

		if (quote.equals(pay))
			return QuoteType.QUOTE;

		if (quote.equals(ContextVariable.ALL))
			return QuoteType.QUOTE;

		if (concept == null)
			return QuoteType.MANUAL;

		if (quote.equals(concept.getCode()))
			return QuoteType.QUOTE;

		return QuoteType.MANUAL;

	}

}
