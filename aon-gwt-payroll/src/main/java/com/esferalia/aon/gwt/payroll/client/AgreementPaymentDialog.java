package com.esferalia.aon.gwt.payroll.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementPaymentDialog extends AonCustomDialog {
	
	enum AgreementPayment implements Serializable {
		SALARIO_BASE_ANUAL("0001  SALARIO BASE ANUAL ( SALARIO_BASE )", "SALARIO_ANUAL / PAGAS * DIAS_TRABAJADOS / DIAS_MES"),
		SALARIO_BASE_MENSUAL("0001 SALARIO BASE MENSUAL ( SALARIO_BASE )", "SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"),
		SALARIO_BASE_DIARIO("0001 SALARIO BASE DIARIO  ( SALARIO_BASE )", "SALARIO_DIARIO * DIAS_TRABAJADOS"),
		SALARIO_BASE_HORAS("0001 SALARIO BASE HORA  ( SALARIO_BASE )", "SALARIO_HORAS * HORAS_TRABAJADAS"),
		
		PLUS_SALARIAL_MENSUAL("0001 PLUS SALARIAL MENSUAL ( PLUS_SALARIAL )", "PLUS_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"),
		PLUS_SALARIAL_DIARIO("0001 PLUS SALARIAL DIARIO  ( PLUS_SALARIAL )", "PLUS_DIARIO * DIAS_TRABAJADOS"),
		PLUS_SALARIAL_DIARIO_LABORABLES("0001 PLUS SALARIAL DIAS REALES  ( PLUS_SALARIAL )", "PLUS_DIARIO * DIAS_EFECTIVOS"),
		PLUS_SALARIAL_FIJO("0001 PLUS SALARIAL FIJO ( PLUS_SALARIAL )", "FRACCIONAR(PLUS_FIJO)"),
		
		PLUS_EXTRA_SALARIAL_MENSUAL(null, "PLUS_XS_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"),
		PLUS_EXTRA_SALARIAL_DIARIO(null, "PLUS_XS_DIARIO * DIAS_TRABAJADOS"),
		PLUS_EXTRA_SALARIAL_DIARIO_LABORABLES(null, "PLUS_XS_DIARIO * DIAS_EFECTIVOS"),
		PLUS_EXTRA_SALARIAL_FIJO(null, "FRACCIONAR(PLUS_XS_FIJO)"),
		
		RETRIBUCION_EN_ESPECIE("0013 RETRIBUCI\u00D3N EN ESPECIE", "IMPORTE_ESPECIE"),
		COMPLEMENTO_PERSONAL_DE_ANTIGUEDAD(null, "IMPORTE_ANTIGUEDAD"),
		GASTOS_PERNOCTA_DIARIO("0043 GASTOS PERNOCTA ", "G_PERNOCTA * DIAS_PERNOCTA"),
		GASTOS_MANUTENCION_DIARIO("0045 GASTOS MANUTENCI\u00D3N ", "G_MANUTENCION * DIAS_MANUNTECION"),
		GASTOS_MANUTENCION_EXTRANJERO_DIARIO("0046 GASTOS MANUTENCI\u00D3N EXTRANJERO ", "G_MANUTENCION_EXT * DIAS_MANUNTECION_EXT"),
		GASTOS_LOCOMOCION_SIN_JUSTIFICANTE("0050 GASTOS LOCOMOCI\u00D3N SIN JUSTIFICANTE ", "IMPORTE_KMS * KMS"),
		
		PAGA_EXTRA_VERANO_NAVIDAD("0004 PAGA EXTRAORDINARIA ( PAGA_EXTRA )", "SALARIO_BASE + PLUS_SALARIAL"),
		PAGA_EXTRA_BENEFICIOS("0004 PAGA EXTRAORDINARIA ( PAGA_EXTRA )", "SALARIO_BASE + PLUS_SALARIAL")
		;
		
		private String suggestName;
		private String expression;
		
		private AgreementPayment(String suggestName, String expression) {
			this.suggestName = suggestName;
			this.expression = expression;
		}
		
		public static AgreementPayment safeValueOf(String name) {
			if (AonStringUtils.isBlank(name)) {
				return null;
			}
			try {
				return AgreementPayment.valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		public String getSuggestName() {
			return suggestName;
		}
		
		public String getExpression() {
			return expression;
		}
	}
	
	interface AgreementPaymentDialogBinder extends UiBinder<Widget, AgreementPaymentDialog> {}

	private static final AgreementPaymentDialogBinder binder = GWT.create(AgreementPaymentDialogBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	HTMLPanel salaryBasePanel;
	
	@UiField
	CheckBox salaryBaseAnualCB;
	
	@UiField
	CheckBox salaryBaseMensualCB;
	
	@UiField
	CheckBox salaryBaseDailyCB;
	
	@UiField
	CheckBox salaryBaseHoursCB;
	
	@UiField
	HTMLPanel salaryPlusesPanel;
	
	@UiField
	CheckBox salaryPlusMensualCB;
	
	@UiField
	CheckBox salaryPlusDailyCB;
	
	@UiField
	CheckBox salaryPlusWorkDailyCB;
	
	@UiField
	CheckBox salaryBasePermanentCB;
	
	@UiField
	HTMLPanel extraSalaryPlusesPanel;
	
	@UiField
	CheckBox extraSalaryPlusMensualCB;
	
	@UiField
	CheckBox extraSalaryPlusDailyCB;
	
	@UiField
	CheckBox extraSalaryPlusWorkDailyCB;
	
	@UiField
	CheckBox extraSalaryBasePermanentCB;
	
	@UiField
	HTMLPanel salaryComplementsPanel;
	
	@UiField
	CheckBox spiceComplementCB;
	
	@UiField
	CheckBox antiquityComplementCB;
	
	@UiField
	CheckBox overnightComplementCB;
	
	@UiField
	CheckBox maintenanceComplementCB;
	
	@UiField
	CheckBox maintenanceForeingComplementCB;
	
	@UiField
	CheckBox locomotionComplementCB;
	
	@UiField
	HTMLPanel salaryExtrasPanel;
	
	@UiField
	CheckBox salaryExtraCB;
	
	@UiField
	Button apportionExtraB;
	
	@UiField
	CheckBox salaryBenefitsCB;
	
	@UiField
	Button apportionBenefitsB;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	private int widgetIndex;
	private int nextDraftPaymentId = 0;
	private int nextDraftExtraId = 0;
	
	private List<HTMLPanel> panelList;
	
	private List<Payment> paymentResultList;
	private List<Extra> extraResultList;
	private List<Payment> availablePaymens;
	
	private List<Pair<CheckBox, String>> salaryBaseCBs;
	private List<Pair<CheckBox, String>> salaryPlusesCBs;
	private List<Pair<CheckBox, String>> extraSalaryPlusesCBs;
	private List<Pair<CheckBox, String>> salaryComplementsCBs;
	private List<Pair<CheckBox, String>> salaryExtrasCBs;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	public AgreementPaymentDialog(int widgetIndex) {
		
		this.widgetIndex = widgetIndex;
		
		setCaption(getCustomCaption());
		setWidget(binder.createAndBindUi(this));
		
		this.paymentResultList = new ArrayList<Payment>();
		this.extraResultList = new ArrayList<Extra>();
		this.availablePaymens = new ArrayList<Payment>();
		
		initializePanelList();
		initializeApportionButtons();
		initializeCheckBoxesList();
		getButtonsPanel();
		
		deckPanel.showWidget(widgetIndex);
	}

	private String getCustomCaption() {
		switch (widgetIndex) {
		case 0:
			return "Salario Base";
		case 1:
			return "Plus Salarial";
		case 2:
			return "Plus Extra Salarial";
		case 3:
			return "Complementos Salariales";
		default:
			return "Pagas Extras";
		}
	}

	private void initializePanelList() {
		this.panelList = new ArrayList<HTMLPanel>();
		
		this.salaryBasePanel.setVisible(false);
		this.salaryPlusesPanel.setVisible(false);
		this.extraSalaryPlusesPanel.setVisible(false);
		this.salaryComplementsPanel.setVisible(false);
		this.salaryExtrasPanel.setVisible(false);
		
		this.panelList.add(salaryBasePanel);
		this.panelList.add(salaryPlusesPanel);
		this.panelList.add(extraSalaryPlusesPanel);
		this.panelList.add(salaryComplementsPanel);
		this.panelList.add(salaryExtrasPanel);
	}
	
	private void initializeApportionButtons() {
		getEnableDisableButton(apportionExtraB, false);
		apportionExtraB.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(apportionExtraB);
			Boolean value = !oldValue;
			getEnableDisableButton(apportionExtraB, value);
		});
		
		getEnableDisableButton(apportionBenefitsB, false);
		apportionBenefitsB.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(apportionBenefitsB);
			Boolean value = !oldValue;
			getEnableDisableButton(apportionBenefitsB, value);
		});
	}
	
	private void initializeCheckBoxesList() {
		
		
		this.salaryBaseCBs = new ArrayList<Pair<CheckBox, String>>();
		this.salaryBaseCBs.add(new Pair<CheckBox, String>(salaryBaseAnualCB, "[01] SALARIO BASE ANUAL"));
		this.salaryBaseCBs.add(new Pair<CheckBox, String>(salaryBaseMensualCB, "[02] SALARIO BASE MENSUAL"));
		this.salaryBaseCBs.add(new Pair<CheckBox, String>(salaryBaseDailyCB, "[03] SALARIO BASE DIARIO"));
		this.salaryBaseCBs.add(new Pair<CheckBox, String>(salaryBaseHoursCB, "[04] SALARIO BASE HORAS"));
		
		this.salaryPlusesCBs = new ArrayList<Pair<CheckBox, String>>();
		this.salaryPlusesCBs.add(new Pair<CheckBox, String>(salaryPlusMensualCB, "[10] PLUS SALARIAL MENSUAL"));
		this.salaryPlusesCBs.add(new Pair<CheckBox, String>(salaryPlusDailyCB, "[11] PLUS SALARIAL DIARIO"));
		this.salaryPlusesCBs.add(new Pair<CheckBox, String>(salaryPlusWorkDailyCB, "[12] PLUS SALARIAL DIARIO LABORABLES"));
		this.salaryPlusesCBs.add(new Pair<CheckBox, String>(salaryBasePermanentCB, "[13] PLUS SALARIAL FIJO"));
		
		this.extraSalaryPlusesCBs = new ArrayList<Pair<CheckBox, String>>();
		this.extraSalaryPlusesCBs.add(new Pair<CheckBox, String>(extraSalaryPlusMensualCB,"[20] PLUS EXTRA SALARIAL MENSUAL"));
		this.extraSalaryPlusesCBs.add(new Pair<CheckBox, String>(extraSalaryPlusDailyCB, "[21] PLUS EXTRA SALARIAL DIARIO"));
		this.extraSalaryPlusesCBs.add(new Pair<CheckBox, String>(extraSalaryPlusWorkDailyCB, "[22] PLUS EXTRA SALARIAL DIARIO LABORABLES"));
		this.extraSalaryPlusesCBs.add(new Pair<CheckBox, String>(extraSalaryBasePermanentCB, "[23] PLUS EXTRA SALARIAL FIJO"));
		
		this.salaryComplementsCBs = new ArrayList<Pair<CheckBox, String>>();
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(spiceComplementCB, "[30] RETRIBUCION EN ESPECIE"));
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(antiquityComplementCB, "[40] COMPLEMENTO PERSONAL DE ANTIGUEDAD"));
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(overnightComplementCB, "[43] GASTOS PERNOCTA DIARIO"));
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(maintenanceComplementCB, "[45] GASTOS MANUTENCION DIARIO"));
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(maintenanceForeingComplementCB, "[46] GASTOS MANUTENCION EXTRANJERO DIARIO"));
		this.salaryComplementsCBs.add(new Pair<CheckBox, String>(locomotionComplementCB, "[50] GASTOS LOCOMOCION SIN JUSTIFICANTE"));
		
		this.salaryExtrasCBs = new ArrayList<Pair<CheckBox, String>>();
		this.salaryExtrasCBs.add(new Pair<CheckBox, String>(salaryExtraCB, "[90-91] PAGA EXTRA VERANO NAVIDAD"));
		this.salaryExtrasCBs.add(new Pair<CheckBox, String>(salaryBenefitsCB, "[92] PAGA EXTRA BENEFICIOS"));
	}
	
	protected abstract void onAcceptExtra(List<Payment> paymentResultList, List<Extra> extraResultList);
	protected abstract void onAccept(List<Payment> paymentResultList);

	public int getNextDraftPaymentId() {
		return this.nextDraftPaymentId;
	}
	
	public void setNextDraftPaymentId(int nextDraftPaymentId) {
		this.nextDraftPaymentId = nextDraftPaymentId;
	}
	
	public int getNextDraftExtraId() {
		return this.nextDraftExtraId;
	}
	
	public void setNextDraftExtraId(int nextDraftExtraId) {
		this.nextDraftExtraId = nextDraftExtraId;
	}
	
	public void setAvailablePaymens(List<Payment> availablePaymens) {
		this.availablePaymens = availablePaymens;
	}
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> {
			onCloseDialog(e);
		});
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.addClickHandler(e -> {
			onAcceptDialog(e);
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		generatePaymentResultList();
	}

	private void generatePaymentResultList() {
		switch (widgetIndex) {
		case 0:
			createSalaryBasePayments();
			onAccept(paymentResultList);
			hide();
			break;
		case 1:
			createSalaryPlusesPayments();
			onAccept(paymentResultList);
			hide();
			break;
		case 2:
			createExtraSalaryBasePayments();
			onAccept(paymentResultList);
			hide();
			break;
		case 3:
			createSalaryComplementsPayments();
			onAccept(paymentResultList);
			hide();
			break;
		default:
			createSalaryExtrasPayments();
			onAcceptExtra(paymentResultList, extraResultList);
			hide();
			break;
		}
	}

	private void createSalaryBasePayments() {
		for(Pair<CheckBox, String> pair : this.salaryBaseCBs)
			addPaymentToResultList(pair);
	}

	private void createSalaryPlusesPayments() {
		for(Pair<CheckBox, String> pair : this.salaryPlusesCBs)
			addPaymentToResultList(pair);
	}

	private void createExtraSalaryBasePayments() {
		for(Pair<CheckBox, String> pair : this.extraSalaryPlusesCBs)
			addPaymentToResultList(pair);
	}

	private void createSalaryComplementsPayments() {
		for(Pair<CheckBox, String> pair : this.salaryComplementsCBs)
			addPaymentToResultList(pair);
	}

	private void createSalaryExtrasPayments() {
		for(Pair<CheckBox, String> pair : this.salaryExtrasCBs)
			addPaymentExtraToResultList(pair);
	}
	
	private void addPaymentToResultList(Pair<CheckBox, String> pair) {
		CheckBox checkBox = pair.getKey();
		String name = pair.getValue();
		
		AgreementPayment agreementPayment = AgreementPayment.safeValueOf(normalizeName(name));
		String suggestName = null == agreementPayment ? "" : agreementPayment.getSuggestName();
		
		if(checkBox.getValue()) {
			Payment newPayment = new Payment();
			newPayment.setId(--nextDraftPaymentId);
			
			Payment concept = getPayment(suggestName);
			
			if (concept == null) {
				newPayment.setDescription(name);
				newPayment.setExpression(agreementPayment.getExpression());
				newPayment.setIrpfExpression("_P");
				newPayment.setQuoteExpression("_P");
				newPayment.setType(Payment.Type.DEFAULT);
				newPayment.setSalaryType(Salary.Type.SALARY);
			} else {
				newPayment.setType(concept.getType());
				newPayment.setName(concept.getName());
				newPayment.setConceptId(concept.getId());
				newPayment.setDescription(name);
				newPayment.setIrpfExpression(concept.getIrpfExpression());
				newPayment.setQuoteExpression(concept.getQuoteExpression());
				newPayment.setSalaryType(Salary.Type.SALARY);
				
				if (concept.getExpression() != null && !AonStringUtils.containsIgnoreCase(name, "PLUS"))
					newPayment.setExpression(getExpression4Payment(concept));
				else
					newPayment.setExpression(agreementPayment.getExpression());
					
			}
				
			paymentResultList.add(newPayment);
		}
	}
	
	private void addPaymentExtraToResultList(Pair<CheckBox, String> pair) {
		CheckBox checkBox = pair.getKey();
		String name = pair.getValue();
		
		AgreementPayment agreementPayment = AgreementPayment.safeValueOf(normalizeName(name));
		String suggestName = null == agreementPayment ? "" : agreementPayment.getSuggestName();
		
		if(checkBox.getValue() && AonStringUtils.equalsIgnoreCase(name, "[90-91] PAGA EXTRA VERANO NAVIDAD")) {
			
			Boolean apportionExtra = isActiveToggleButton(apportionExtraB);
			
			// Sumer payment and extra
			
			Payment newPaymentSummer = new Payment();
			newPaymentSummer.setId(--nextDraftPaymentId);
			
			Payment conceptSummer = getPayment(suggestName);
			
			if (conceptSummer == null) {
				newPaymentSummer.setDescription("[90] PAGA EXTRA VERANO");
				newPaymentSummer.setExpression(agreementPayment.getExpression());
				newPaymentSummer.setIrpfExpression("_P");
				newPaymentSummer.setQuoteExpression("_P");
				newPaymentSummer.setType(Payment.Type.CRA_0004);
				newPaymentSummer.setSalaryType(Salary.Type.SALARY);
			} else {
				newPaymentSummer.setType(conceptSummer.getType());
				newPaymentSummer.setName(conceptSummer.getName());
				newPaymentSummer.setConceptId(conceptSummer.getId());
				newPaymentSummer.setDescription("[90] PAGA EXTRA VERANO");
				newPaymentSummer.setExpression(agreementPayment.getExpression());
				newPaymentSummer.setIrpfExpression(conceptSummer.getIrpfExpression());
				newPaymentSummer.setQuoteExpression(conceptSummer.getQuoteExpression());
				newPaymentSummer.setSalaryType(Salary.Type.SALARY);	
			}
				
			paymentResultList.add(newPaymentSummer);
			
			if(!apportionExtra) {
				
				Extra newExtraSummer = new Extra();
				newExtraSummer.setId(--nextDraftExtraId);
				newExtraSummer.setIssueDate("31/07");
				newExtraSummer.setStartDate("01/01");
				newExtraSummer.setEndDate("30/06");
				newExtraSummer.setDomain(newPaymentSummer.getDomain());
				newExtraSummer.setPaymentId(newPaymentSummer.getId());
				newExtraSummer.setPaymentDescription(newPaymentSummer.getDescription());
				newExtraSummer.setAgreementDescription(newPaymentSummer.getDescription());
				
				extraResultList.add(newExtraSummer);
			
			}
			
			// Winter payment and extra
			
			Payment newPaymentWinter = new Payment();
			newPaymentWinter.setId(--nextDraftPaymentId);
			
			Payment conceptWinter = getPayment(suggestName);
			
			if (conceptWinter == null) {
				newPaymentWinter.setDescription("[91] PAGA EXTRA NAVIDAD");
				newPaymentWinter.setExpression(agreementPayment.getExpression());
				newPaymentWinter.setIrpfExpression("_P");
				newPaymentWinter.setQuoteExpression("_P");
				newPaymentWinter.setType(Payment.Type.CRA_0004);
				newPaymentWinter.setSalaryType(Salary.Type.SALARY);
			} else {
				newPaymentWinter.setType(conceptWinter.getType());
				newPaymentWinter.setName(conceptWinter.getName());
				newPaymentWinter.setConceptId(conceptWinter.getId());
				newPaymentWinter.setDescription("[91] PAGA EXTRA NAVIDAD");
				newPaymentWinter.setExpression(agreementPayment.getExpression());
				newPaymentWinter.setIrpfExpression(conceptWinter.getIrpfExpression());
				newPaymentWinter.setQuoteExpression(conceptWinter.getQuoteExpression());
				newPaymentWinter.setSalaryType(Salary.Type.SALARY);
			}
				
			paymentResultList.add(newPaymentWinter);
			
			if(!apportionExtra) {
			
				Extra newExtraWinter = new Extra();
				newExtraWinter.setId(--nextDraftExtraId);
				newExtraWinter.setIssueDate("31/12");
				newExtraWinter.setStartDate("01/07");
				newExtraWinter.setEndDate("31/12");
				newExtraWinter.setDomain(newPaymentWinter.getDomain());
				newExtraWinter.setPaymentId(newPaymentWinter.getId());
				newExtraWinter.setPaymentDescription(newPaymentWinter.getDescription());
				newExtraWinter.setAgreementDescription(newPaymentWinter.getDescription());
				
				extraResultList.add(newExtraWinter);
			
			}
			
		} else if (checkBox.getValue() && AonStringUtils.equalsIgnoreCase(name, "[92] PAGA EXTRA BENEFICIOS")) {
			
			// Apportion Benefits
			Boolean apportionBenefits = isActiveToggleButton(apportionBenefitsB);
			
			// Benefits payment and extra
			
			Payment newPaymentBenefit = new Payment();
			newPaymentBenefit.setId(--nextDraftPaymentId);
			
			Payment conceptBenefit = getPayment(suggestName);
			
			if (conceptBenefit == null) {
				newPaymentBenefit.setDescription("[92] PAGA BENEFICIOS");
				newPaymentBenefit.setExpression(agreementPayment.getExpression());
				newPaymentBenefit.setIrpfExpression("_P");
				newPaymentBenefit.setQuoteExpression("_P");
				newPaymentBenefit.setType(Payment.Type.CRA_0004);
				newPaymentBenefit.setSalaryType(Salary.Type.SALARY);
			} else {
				newPaymentBenefit.setType(conceptBenefit.getType());
				newPaymentBenefit.setName(conceptBenefit.getName());
				newPaymentBenefit.setConceptId(conceptBenefit.getId());
				newPaymentBenefit.setDescription("[92] PAGA BENEFICIOS");
				newPaymentBenefit.setExpression(agreementPayment.getExpression());
				newPaymentBenefit.setIrpfExpression(conceptBenefit.getIrpfExpression());
				newPaymentBenefit.setQuoteExpression(conceptBenefit.getQuoteExpression());
				newPaymentBenefit.setSalaryType(Salary.Type.SALARY);	
			}
				
			paymentResultList.add(newPaymentBenefit);
			
			if(!apportionBenefits) {
				
				Extra newExtraBenefits = new Extra();
				newExtraBenefits.setId(--nextDraftExtraId);
				newExtraBenefits.setIssueDate("31/03");
				newExtraBenefits.setStartDate("01/01 -1");
				newExtraBenefits.setEndDate("31/12 -1");
				newExtraBenefits.setDomain(newPaymentBenefit.getDomain());
				newExtraBenefits.setPaymentId(newPaymentBenefit.getId());
				newExtraBenefits.setPaymentDescription(newPaymentBenefit.getDescription());
				newExtraBenefits.setAgreementDescription(newPaymentBenefit.getDescription());
				
				extraResultList.add(newExtraBenefits);
				
			}
		}
	}
	
	private String normalizeName(String name) {
		String nameNormalized = name.split("]")[1].trim().replaceAll(" ", "_");
		return nameNormalized;
	}
	
	private Payment getPayment(String suggestionPaymentString) {
		for (Payment payment : availablePaymens) {
			if (AonStringUtils.equalsIgnoreCase(suggestionPaymentString, getSuggestionString(payment)))
				return payment;
		}
		return null;
	}
	
	private static String getSuggestionString(Payment payment) {
		return SalaryDraft.getSuggestionString(payment);
	}
	
	private static String getExpression4Payment(Payment concept) {
		String expression = concept.getExpression();
		
		Payment.Type type = concept.getType(); 
		if ( type == Payment.Type.CRA_0055 
			|| type ==  Payment.Type.CRA_0056 ) 
			expression = expression.replaceAll("REMOVE", "HIDE");
		
		return expression;
	}
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
}
