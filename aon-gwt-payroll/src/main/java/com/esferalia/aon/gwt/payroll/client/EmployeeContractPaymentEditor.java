package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeContractPaymentEditor extends AonCustomDialog {
	
	// ----------------------------------------- UiBinder

	interface Binder extends UiBinder<Widget, EmployeeContractPaymentEditor> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------- UiFields

	@UiField
	ListBox paymentTypeLB;
	
	@UiField
	HTMLPanel paymentConceptPanel;
	
	@UiField
	SuggestBox paymentConceptSB;
	
	@UiField
	TextBox paymentDescriptionTB;
	
	@UiField
	ExpressionArea paymentExpressionTB;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	DateBoxEx endDateBx;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ----------------------------------------- Variables
	
//	private static final String PAYMENT = "PAGOS";
	private static final String DEDUCTION = "DEDUCIONES";
	private static final String COST = "COSTES";
	private static final String BONUS = "BONUS";
	
	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	
	// ----------------------------------------- Constructor
	
	protected EmployeeContractPaymentEditor(String paymentType) {
		setCaption("Creador Pagos");
		setWidget(binder.createAndBindUi(this));
		this.showCloseButton(true);
		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {
			
			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;
				paymentExpressionTB.setReadOnly(false);
				createPaymentType();
				getFooterButtons();
				setSelectedValueLB(paymentTypeLB, paymentType);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTypeLB);
				showDialog();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}
			
		});
	}
	
	// ----------------------------------------- ViewMethods
	
	private void createPaymentType() {
		paymentTypeLB.clear();
//		paymentTypeLB.addItem(PAYMENT);
		paymentTypeLB.addItem(DEDUCTION);
		paymentTypeLB.addItem(COST);
		paymentTypeLB.addItem(BONUS);
		
		paymentTypeLB.addChangeHandler(changeEvent -> redrawView());
	}
	
	private void redrawView() {
		clearView();
		switch (paymentTypeLB.getSelectedValue()) {
//			case PAYMENT:
//				createPaymentConcept(contractConcepts.getPaymentConcepts());
//				break;
			case DEDUCTION:
				createPaymentConcept(contractConcepts.getDeductionConcepts());	
				break;
			case COST:
				createPaymentConcept(contractConcepts.getBonusConcepts());
				break;
			case BONUS:
				hidePaymenteConcept();
				break;
			default:
				break;
		}
	}

	private void clearView() {
		paymentConceptSB.setValue(null);
		paymentDescriptionTB.setValue(null);
		paymentExpressionTB.setValue(null);
		startDateBx.setValue(null);
		endDateBx.setValue(null);
	}

	private void createPaymentConcept(Set<ContractConcept> bonusConcepts) {
		paymentConceptPanel.setVisible(true);
		
		List<String> conceptsSuggest = new ArrayList<>();
		for(ContractConcept bonusConcept : bonusConcepts) {
			String suggestDisplay = AonStringUtils.isBlank(bonusConcept.getCode()) ? "" : bonusConcept.getCode() + " - ";
			suggestDisplay += bonusConcept.getDescription();
			conceptsSuggest.add(suggestDisplay);
		}
		MultiWordSuggestOracle orclConcepts = (MultiWordSuggestOracle) paymentConceptSB.getSuggestOracle();
		orclConcepts.clear();
		orclConcepts.addAll(conceptsSuggest);
		paymentConceptSB.setAutoSelectEnabled(false);
		paymentConceptSB.addValueChangeHandler(value -> {
			selectedConcept = getConcept();
			
			if(null != selectedConcept) {
				paymentDescriptionTB.setValue(selectedConcept.getDescription());
				paymentExpressionTB.setValue(selectedConcept.getExpression());
			}
		});
	}

	private void hidePaymenteConcept() {
		paymentConceptPanel.setVisible(false);
		paymentConceptSB.setValue(null);
	}
	
	private ContractConcept getConcept() {
		Set<ContractConcept> concepts = getConcepts();
		String code = null;
		String description = null;
		
		if(paymentConceptSB.getValue().contains(" - ")) {
			code = paymentConceptSB.getValue().split(" - ")[0];
			description = paymentConceptSB.getValue().split(" - ")[1];
			
			for(ContractConcept contractConcept : concepts)
				if(isIdentical(contractConcept, code, description))
					return contractConcept;
		} else {
			code = paymentConceptSB.getValue();
			description = paymentConceptSB.getValue();
			
			for(ContractConcept contractConcept : concepts)
				if(isSimilar(contractConcept, code, description))
					return contractConcept;
		}
		
		return null;
	}

	private boolean isIdentical(ContractConcept contractConcept, String code, String description) {
		return AonStringUtils.equalsIgnoreCase(code, contractConcept.getCode()) && AonStringUtils.equalsIgnoreCase(description, contractConcept.getDescription());
	}
	
	private boolean isSimilar(ContractConcept contractConcept, String code, String description) {
		return (AonStringUtils.equalsIgnoreCase(code, contractConcept.getCode()) && AonStringUtils.isBlank(contractConcept.getDescription())) ||
				(AonStringUtils.equalsIgnoreCase(description, contractConcept.getDescription()) && AonStringUtils.isBlank(contractConcept.getCode()));
	}

	private Set<ContractConcept> getConcepts() {
		switch (paymentTypeLB.getSelectedValue()) {
//			case PAYMENT:
//				return contractConcepts.getPaymentConcepts();
			case DEDUCTION:
				return contractConcepts.getDeductionConcepts();
			case COST:
				return contractConcepts.getBonusConcepts();
			case BONUS:
				return new HashSet<>();
			default:
				return new HashSet<>();
		}
	}

	// ----------------------------------------- ShowDialog

	private void showDialog() {
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ----------------------------------------- FooterButtons
	
	private void getFooterButtons() {
		buttonsPanel.clear();
		
		Button acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText( AON.MSG.accept());
		acceptDialog.addClickHandler(e -> {
			hide();
			onAccept(createContractConceptCalc());
		});
		
		buttonsPanel.add(acceptDialog);
	}

	private ContractConceptCalc createContractConceptCalc() {
		ContractConceptCalc contractConceptCalc = new ContractConceptCalc();
		
		if(null != selectedConcept) { 
			contractConceptCalc.setConceptId(selectedConcept.getId());
			contractConceptCalc.setType(Payment.Type.valueOf(selectedConcept.getType()+""));
			contractConceptCalc.setContractConceptCalcType(getContractConceptCalcType());
			contractConceptCalc.setDescription(paymentDescriptionTB.getValue());
			contractConceptCalc.setExpression(paymentExpressionTB.getValue());
			contractConceptCalc.setStartDate(startDateBx.getValue());
			contractConceptCalc.setEndDate(endDateBx.getValue());
		} else {
			contractConceptCalc.setCodeType(paymentConceptSB.getValue());
			contractConceptCalc.setType(getType());
			contractConceptCalc.setContractConceptCalcType(getContractConceptCalcType());
			contractConceptCalc.setDescription(paymentDescriptionTB.getValue());
			contractConceptCalc.setExpression(paymentExpressionTB.getValue());
			contractConceptCalc.setStartDate(startDateBx.getValue());
			contractConceptCalc.setEndDate(endDateBx.getValue());
		}
		
		return contractConceptCalc;
	}

	private Payment.Type getType() {
		switch (paymentTypeLB.getSelectedValue()) {
//		case PAYMENT:
//			return Payment.Type.CRA_0001;
		case DEDUCTION:
			return Payment.Type.CRA_0009;
		case COST:
			return Payment.Type.CRA_0001;
		case BONUS:
			return Payment.Type.CRA_0001;
		default:
			return null;
	}
	}

	private ContractConceptCalcType getContractConceptCalcType() {
		switch (paymentTypeLB.getSelectedValue()) {
//			case PAYMENT:
//				return ContractConceptCalcType.PAYMENT;
			case DEDUCTION:
				return ContractConceptCalcType.DEDUCTION;
			case COST:
				return ContractConceptCalcType.COST;
			case BONUS:
				return ContractConceptCalcType.BONUS;
			default:
				return null;
		}
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	// ----------------------------------------- AbstractMehtods
	
	protected abstract void onAccept(ContractConceptCalc contractConceptCalc);

}
