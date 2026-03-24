package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.BankSwift;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.Geozone;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.ContractType.ModelRecord;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Occupation;
import com.esferalia.aon.occam.api.model.type.QuoteGroup;
import com.esferalia.aon.occam.api.model.type.RLCE;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;


public abstract class EmployeeWidget extends FlowPanel {
	
	// Contract Card
	private AonCustomCard contractDataTable;
	
	AonCustomTextBox documentType = new AonCustomTextBox("Tipo");
	
	AonTableButton clearEmployee = new AonTableButton("Resetear", AON.CSS.aonIconClear());
	AonCustomSuggestBox document = new AonCustomSuggestBox("Documento");
	AonCustomSuggestBox nationality = new AonCustomSuggestBox("Nacionalidad");
	AonCustomSuggestBox securitySocialNum = new AonCustomSuggestBox("NAF");
	
	AonCustomSuggestBox name = new AonCustomSuggestBox("Nombre");
	AonCustomSuggestBox firstSurname = new AonCustomSuggestBox("1er Apellido");
	AonCustomSuggestBox secondSurname = new AonCustomSuggestBox("2\u00ba Apellido");

	AonCustomListBox ssRegimeType = new AonCustomListBox("Tipo Cotizaci\u00f3n");
	AonCustomListBox mdTBTLB = new AonCustomListBox("Tipo Tributaci\u00f3n");
	AonCustomListBox mdCTZLB = new AonCustomListBox("Modelo Cotizaci\u00f3n");

	AonCustomListBox activityCCC = new AonCustomListBox("Actividad / CCC");
	AonCustomListBox workplace = new AonCustomListBox("Centro Trabajo");
	
	AonCustomListBox contractTypeLB = new AonCustomListBox("TC2");
	AonCustomTextBox contractTypeFreelance = new AonCustomTextBox("TC2");
	AonCustomListBox modality = new AonCustomListBox("Modalidad TC2");
	AonCustomCheckBox quoteGroupCotizB = new AonCustomCheckBox("I. Cotizaci\u00f3nn Mensual");

	AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	AonCustomDateBox endDate = new AonCustomDateBox("F. Fin");
	AonCustomDateBox seniorityDate = new AonCustomDateBox("F. Antig\u00fcedad");

	AonCustomSuggestBox agreement = new AonCustomSuggestBox("Convenio");
	AonCustomListBox level = new AonCustomListBox("Nivel Retributivo / Categor\u00eda");
	AonCustomTextBox category = new AonCustomTextBox("Mostrar Como");
	
	AonCustomListBox quoteGroup = new AonCustomListBox("Grupo Cotizaci\u00f3n");
	AonCustomListBox occupation = new AonCustomListBox("Ocupaci\u00f3n");
	
	AonCustomSuggestBox cnoSB = new AonCustomSuggestBox("CNO");
	AonCustomListBox rlce = new AonCustomListBox("RLCE");
	AonCustomListBox employeesColective = new AonCustomListBox("Colectivo Trabajadores");
	
	AonCustomListBox journeyType = new AonCustomListBox("Tipo Jornada");
	HTMLPanel journeyDuration = new HTMLPanel(AonStringUtils.EMPTY);
	AonCustomNumberBox partialityCoef = new AonCustomNumberBox("Coeficiente Parcialidad");

	// TABLA DATOS EMPLEADO
	private AonCustomCard employeeDataTable;
	
	AonCustomDateBox birthDate = new AonCustomDateBox("F. Nacimiento");
	AonCustomTextBox age = new AonCustomTextBox("Edad");
	AonCustomListBox gender = new AonCustomListBox("G\u00e9nero");
	AonCustomListBox civilStatus = new AonCustomListBox("Estado Civil");
	
	AonCustomListBox streetType = new AonCustomListBox("Tipo V\u00eda");
	AonCustomSuggestBox address = new AonCustomSuggestBox("Direcci\u00f3n");
	AonCustomTextBox addressNum = new AonCustomTextBox("N\u00famero");
	AonCustomTextBox addressInfo = new AonCustomTextBox("Rest. Direcci\u00f3n");
	
	AonCustomTextBox addressZip = new AonCustomTextBox("C\u00f3digo Postal");
	AonCustomListBox addressProvince = new AonCustomListBox("Provincia");
	AonCustomSuggestBox addressMunicipality = new AonCustomSuggestBox("Localidad");
	
	AonCustomTextBox mobile = new AonCustomTextBox("M\u00f3vil");
	AonCustomTextBox phone = new AonCustomTextBox("Tel\u00e9fono");
	AonCustomTextBox email = new AonCustomTextBox("Email");
	
	AonCustomListBox payMethod = new AonCustomListBox("Forma Pago");
	AonCustomSuggestBox account = new AonCustomSuggestBox("IBAN");
	AonCustomTextBox bic = new AonCustomTextBox("BIC");
	
	// ------------------------------------------------- Class variables

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<com.esferalia.aon.gwt.payroll.shared.Country> countries;

	private ContractType contractType;
	private Municipalities municipalities;

	private List<Agreement> agreements;
	private Map<String, CNO> cnoMap;
	
	private boolean selectionAgreementInProgress = false;
	
	private HandlerRegistration selectionHandlerAgreement;
	private HandlerRegistration valueChangeHandlerAgreement;
	private HandlerRegistration keyUpHandlerAgreement;

	// ------------------------------------------------- Constructor

	protected EmployeeWidget() {
		addStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("gap", "1rem");
		getElement().getStyle().setProperty("margin", "1rem 0");
		
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		this.countries = new ArrayList<>();
		this.agreements = new ArrayList<>();
		this.cnoMap = new HashMap<>();
		
		impl.getCountries(new AsyncCallback<List<com.esferalia.aon.gwt.payroll.shared.Country>>() {

			@Override
			public void onSuccess(List<com.esferalia.aon.gwt.payroll.shared.Country> countriesResult) {
				countries = countriesResult;
				
				impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

					@Override
					public void onSuccess(Map<String, CNO> cnoMapIn) {
						cnoMap = cnoMapIn;
						
						initializeView();
						
						onLoadEnd();
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO: show error
			}
		});
		
	}

	public abstract void onLoadEnd();
	
	public void initializeView() {
		initializeCards();
		resetElements();
		initializeComponents();
		initDisplayElements();
		cleanErrorStyles();
	}

	private void initializeCards() {
		clear();
		
		contractDataTable = new AonCustomCard("Contrato");
		contractDataTable.getElement().getStyle().setProperty("max-width", "56rem");
		contractDataTable.getElement().getStyle().setProperty("min-height", "150px");
		contractDataTable.getElement().getStyle().setProperty("width", "100%");
		contractDataTable.add(createContractDataTable());
		addContractDataTableHadlers();
		
		add( contractDataTable );
		
		employeeDataTable = new AonCustomCard("Trabajador");
		employeeDataTable.getElement().getStyle().setProperty("max-width", "56rem");
		employeeDataTable.getElement().getStyle().setProperty("min-height", "150px");
		employeeDataTable.getElement().getStyle().setProperty("width", "100%");
		employeeDataTable.add(createEmployeeDataTable());
		addEmployeeDataTableHadlers();
		
		add( employeeDataTable );
	}

	private Widget createContractDataTable() {
		
		HTMLPanel table = new HTMLPanel(AonStringUtils.EMPTY);
		table.addStyleName(AON.CSS.aonItemFlex());
		table.addStyleName(AON.CSS.aonFlexColumn());
		table.setWidth("100%");
		
		HTMLPanel row1 = new HTMLPanel(AonStringUtils.EMPTY);
		row1.addStyleName(AON.CSS.aonItemFlex());
		row1.setWidth("100%");
		
		HTMLPanel nationalityNssPanel = new HTMLPanel(AonStringUtils.EMPTY);
		nationalityNssPanel.addStyleName(AON.CSS.aonItemFlex());
		nationalityNssPanel.setWidth("100%");
		
		nationality.setMinWidth("18rem");
		securitySocialNum.setMinWidth("15rem");
		nationalityNssPanel.add(nationality);
		nationalityNssPanel.add(securitySocialNum);
		
		documentType.setEnable(false);
		documentType.setMaxWidth("8rem");
		
		clearEmployee.setVisible(false);
		document.addButton(clearEmployee);
		
		securitySocialNum.setMaxLength(12);
		document.setMaxLength(9);
		
		row1.add(document);
		row1.add(documentType);
		row1.add(nationalityNssPanel);
		table.add(row1);
		
		HTMLPanel row2 = new HTMLPanel(AonStringUtils.EMPTY);
		row2.addStyleName(AON.CSS.aonItemFlex());
		row2.setWidth("100%");
		
		row2.add(name);
		row2.add(firstSurname);
		row2.add(secondSurname);
		table.add(row2);
		
		HTMLPanel row3 = new HTMLPanel(AonStringUtils.EMPTY);
		row3.addStyleName(AON.CSS.aonItemFlex());
		row3.setWidth("100%");
		
		HTMLPanel modalityQuotePanel = new HTMLPanel(AonStringUtils.EMPTY);
		modalityQuotePanel.addStyleName(AON.CSS.aonItemFlex());
		modalityQuotePanel.setWidth("100%");
		
		modalityQuotePanel.add(mdTBTLB);
		modalityQuotePanel.add(mdCTZLB);
		
		row3.add(ssRegimeType);
		row3.add(modalityQuotePanel);
		table.add(row3);
		
		HTMLPanel row4 = new HTMLPanel(AonStringUtils.EMPTY);
		row4.addStyleName(AON.CSS.aonItemFlex());
		row4.setWidth("100%");
		
		activityCCC.setMaxWidth("28rem");
		
		row4.add(activityCCC);
		row4.add(workplace);
		table.add(row4);
		
		HTMLPanel contractTypePanel = new HTMLPanel(AonStringUtils.EMPTY);
		contractTypePanel.addStyleName(AON.CSS.aonItemFlex());
		contractTypePanel.setWidth("100%");
		
		contractTypeFreelance.setValue("R\u00e9gimen especial de trabajadores aut\u00f3nomos");
		
		contractTypePanel.add(contractTypeLB);
		contractTypePanel.add(contractTypeFreelance);
		
		contractTypeFreelance.setEnable(false);
		
		HTMLPanel row20 = new HTMLPanel(AonStringUtils.EMPTY);
		row20.addStyleName(AON.CSS.aonItemFlex());
		row20.setWidth("100%");
		
		row20.add(contractTypePanel);
		table.add(row20);
		
		HTMLPanel row5 = new HTMLPanel(AonStringUtils.EMPTY);
		row5.addStyleName(AON.CSS.aonItemFlex());
		row5.setWidth("100%");
		
		quoteGroupCotizB.setMaxWidth("10rem");
		quoteGroupCotizB.getElement().getStyle().setDisplay(Display.NONE);
		
		row5.add(modality);
		row5.add(quoteGroupCotizB);
		table.add(row5);
		
		HTMLPanel row6 = new HTMLPanel(AonStringUtils.EMPTY);
		row6.addStyleName(AON.CSS.aonItemFlex());
		row6.setWidth("100%");
		
		row6.add(startDate);
		row6.add(endDate);
		row6.add(seniorityDate);
		table.add(row6);
		
		HTMLPanel row7 = new HTMLPanel(AonStringUtils.EMPTY);
		row7.addStyleName(AON.CSS.aonItemFlex());
		row7.setWidth("100%");
		
		level.setMaxWidth("22rem");
		category.setMaxWidth("10rem");
		
		row7.add(agreement);
		row7.add(level);
		row7.add(category);
		table.add(row7);
		
		HTMLPanel row8 = new HTMLPanel(AonStringUtils.EMPTY);
		row8.addStyleName(AON.CSS.aonItemFlex());
		row8.setWidth("100%");
		
		occupation.setMaxWidth("28rem");
		
		row8.add(quoteGroup);
		row8.add(occupation);
		table.add(row8);
		
		HTMLPanel rlceColectivePanel = new HTMLPanel(AonStringUtils.EMPTY);
		rlceColectivePanel.addStyleName(AON.CSS.aonItemFlex());
		rlceColectivePanel.setWidth("100%");
		
		rlceColectivePanel.add(rlce);
		rlceColectivePanel.add(employeesColective);
		
		HTMLPanel row9 = new HTMLPanel(AonStringUtils.EMPTY);
		row9.addStyleName(AON.CSS.aonItemFlex());
		row9.setWidth("100%");
		
		row9.add(cnoSB);
		row9.add(rlceColectivePanel);
		table.add(row9);
		
		HTMLPanel row10 = new HTMLPanel(AonStringUtils.EMPTY);
		row10.addStyleName(AON.CSS.aonItemFlex());
		row10.setWidth("100%");
		
		partialityCoef.hideNearBy();
		partialityCoef.setMaxWidth("9rem");
		
		journeyDuration.setWidth("200rem");
		
		row10.add(journeyType);
		row10.add(journeyDuration);
		row10.add(partialityCoef);
		table.add(row10);
		
		return table;
	}

	private Widget createEmployeeDataTable() {
		HTMLPanel table = new HTMLPanel(AonStringUtils.EMPTY);
		table.addStyleName(AON.CSS.aonItemFlex());
		table.addStyleName(AON.CSS.aonFlexColumn());
		table.setWidth("100%");
		
		age.setEnable(false);
		
		HTMLPanel row1 = new HTMLPanel(AonStringUtils.EMPTY);
		row1.addStyleName(AON.CSS.aonItemFlex());
		row1.setWidth("100%");
		
		row1.add(birthDate);
		row1.add(age);
		row1.add(gender);
		row1.add(civilStatus);
		table.add(row1);
		
		HTMLPanel row2 = new HTMLPanel(AonStringUtils.EMPTY);
		row2.addStyleName(AON.CSS.aonItemFlex());
		row2.setWidth("100%");
		
		streetType.setMaxWidth("10rem");
		addressNum.setMaxWidth("5rem");
		
		row2.add(streetType);
		row2.add(address);
		row2.add(addressNum);
		row2.add(addressInfo);
		table.add(row2);
		
		HTMLPanel row3 = new HTMLPanel(AonStringUtils.EMPTY);
		row3.addStyleName(AON.CSS.aonItemFlex());
		row3.setWidth("100%");
		
		row3.add(addressZip);
		row3.add(addressProvince);
		row3.add(addressMunicipality);
		table.add(row3);
		
		HTMLPanel row4 = new HTMLPanel(AonStringUtils.EMPTY);
		row4.addStyleName(AON.CSS.aonItemFlex());
		row4.setWidth("100%");
		
		row4.add(mobile);
		row4.add(phone);
		row4.add(email);
		table.add(row4);
		
		account.getElement().setAttribute("style", "text-transform:uppercase");
		account.getElement().setPropertyString("pattern", "[A-Z0-9]*");

		HTMLPanel row5 = new HTMLPanel(AonStringUtils.EMPTY);
		row5.addStyleName(AON.CSS.aonItemFlex());
		row5.setWidth("100%");
		
		row5.add(payMethod);
		row5.add(account);
		row5.add(bic);
		table.add(row5);
		
		return table;
	}
	
	private void addContractDataTableHadlers() {
		clearEmployee.addClickHandler(e -> onClearEmployeeClick());
		
		document.getSuggestBox().addSelectionHandler(e -> {
			String documentValue = this.document.getValue().trim();
			if (AonStringUtils.isNotBlank(documentValue)) {
				onEmployeeDocumentSuggestionChange(documentValue); 
				clearEmployee.setVisible(true);
			}
		});
		document.getSuggestBox().addValueChangeHandler(e -> {
			String documentValue = this.document.getValue().trim();
			if (AonStringUtils.isNotBlank(documentValue)) {
				String documentTypeValue = checkDocumentType(documentValue);
				this.documentType.setValue(documentTypeValue);
				
				if (!checkDocumentValidation(documentValue))
					this.document.addError();
				else
					this.document.removeError();

				onEmployeeDocumentChange(documentValue, documentTypeValue);
			} else {
				this.document.removeError();
				clearEmployee.setVisible(false);
			}
		});
		
		nationality.getSuggestBox().addSelectionHandler(e -> {
			String countryIso2 = getIso2(this.nationality.getValue());
			onEmployeeNationalityChange(countryIso2);
		});
		nationality.getSuggestBox().addValueChangeHandler(e -> {
			if (AonStringUtils.isBlank(this.nationality.getValue()))
				onEmployeeNationalityChange(null);
		});
		
		securitySocialNum.getSuggestBox().addSelectionHandler(e -> {
			String ssNum = this.securitySocialNum.getValue().trim();
			if (AonStringUtils.isNotBlank(ssNum))
				onEmployeeSSNumSuggestionChange(ssNum);
		});
		securitySocialNum.getSuggestBox().addValueChangeHandler(e -> {
			String ssNum = this.securitySocialNum.getValue().trim();
			if (AonStringUtils.isNotBlank(ssNum)) {
				if (checkSSNumValidation(ssNum)) {
					this.securitySocialNum.removeError();
					this.securitySocialNum.setTitle(null);
				} else {
					this.securitySocialNum.addError();
					this.securitySocialNum.setTitle("El numero es err\u00F3neo");
				}

				onEmployeeSSNumChange(ssNum);
			} else {
				this.securitySocialNum.removeError();
				this.securitySocialNum.setTitle(null);
			}
		});
		
		name.getSuggestBox().addSelectionHandler(e -> {
			String nameValue = this.name.getValue().trim();
			if (AonStringUtils.isNotBlank(nameValue))
				onEmployeeNameSuggestionChange(nameValue);
		});
		name.getSuggestBox().addValueChangeHandler(e -> {
			String nameValue = this.name.getValue().trim();
			if (AonStringUtils.isNotBlank(nameValue))
				onEmployeeNameChange(nameValue);
		});
		
		firstSurname.getSuggestBox().addSelectionHandler(e -> {
			String surnameValue = this.firstSurname.getValue().trim();
			if (AonStringUtils.isNotBlank(surnameValue))
				onEmployeeFirstSurnameSuggestionChange(surnameValue);
		});
		firstSurname.getSuggestBox().addValueChangeHandler(e -> {
			String surnameValue = this.firstSurname.getValue().trim();
			if (AonStringUtils.isNotBlank(surnameValue))
				onEmployeeFirstSurnameChange(surnameValue);
		});
		
		secondSurname.getSuggestBox().addValueChangeHandler(e -> {
			String secondSurnameValue = this.secondSurname.getValue().trim();
			onEmployeeSecondSurnameChange(secondSurnameValue);
		});
		
		ssRegimeType.addChangeHandler(e -> {
			byte ssRegimeValue = Byte.parseByte(this.ssRegimeType.getValue());

			if (ssRegimeValue == (byte) 3) {
				showElementsFreelancerTable();
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.journeyType.getListBox());
			} else
				this.hideElementsFreelancerTable();

			onContractSSRegimenChange(ssRegimeValue);
		});
		
		mdTBTLB.addChangeHandler(e -> onContractMdTBThange(mdTBTLB.getValue()));
		
		mdCTZLB.addChangeHandler(e -> {
			String mdCtz = String.valueOf(mdCTZLB.getValue());
			onContractMdCTZhange(mdCtz);
		});
		
		activityCCC.addChangeHandler(e -> {
			String activityCCCStr = String.valueOf(this.activityCCC.getValue());

			if (AonStringUtils.equalsIgnoreCase(activityCCCStr, "-1"))
				onContractActiviesCCCChange(null);
			else {
				onContractActiviesCCCChange(activityCCCStr);

				Byte cccType = Byte.parseByte(activityCCCStr.split("/")[2]);
				if (cccType == (byte) 7)
					showMdCtzContract();
				else
					hideMdCtzContract();
			}
		});
		
		workplace.addChangeHandler(e -> {
			Integer workplaceId = Integer.parseInt(this.workplace.getValue());
			onContractWorkplaceChange(workplaceId);
		});
		
		contractTypeLB.addChangeHandler(e -> {
			String contractTypeStr = String.valueOf(this.contractTypeLB.getValue());

			if (AonStringUtils.equalsIgnoreCase(contractTypeStr, "-1"))
				onContractTypeChange(null);
			else {
				Integer contractTypeInt = Integer.parseInt(contractTypeStr);
				if (AonNumberUtils.between(contractTypeInt, 200, 400) || AonNumberUtils.between(contractTypeInt, 500, 599)
						|| AonNumberUtils.equals(contractTypeInt, 0))
					showPartialTimeContract();
				else
					showElementsFullTimeContract();

				if (AonNumberUtils.equals(contractTypeInt, 402) || AonNumberUtils.equals(contractTypeInt, 502)) {
					showEmployeesColective();
				} else {
					hideEmployeesColective();
					onContractEmployeesColectiveChange(null);
				}

				checkContracts401And501(contractTypeInt);

				updateModality(contractTypeInt);

				onContractTypeChange(contractTypeStr);
			}
		});
		
		modality.addChangeHandler(e -> {
			Integer contractModel = Integer.valueOf(this.modality.getValue());
			if (AonNumberUtils.equals(contractModel, -1))
				onContractModalityChange(null);
			else
				onContractModalityChange(contractModel);
		});
		
		startDate.addValueChangeHandler(e -> {
			Date startDateValue = this.startDate.getValue();
			onContractStartDateChange(startDateValue);

			if (null != startDate)
				this.seniorityDate.setValue(startDateValue, true);

			String contractTypeStr = String.valueOf(this.contractTypeLB.getValue());
			Integer contractTypeInt = Integer.parseInt(contractTypeStr);
			checkContracts401And501(contractTypeInt);
		});
		
		endDate.addValueChangeHandler(e -> onContractEndDateChange(this.endDate.getValue()));
		
		seniorityDate.addValueChangeHandler(e -> {
			Date startDateValue = this.startDate.getValue();
			Date seniorityDateValue = this.seniorityDate.getValue();

			if (null == startDateValue) {
				this.seniorityDate.addWarning();
				this.seniorityDate.setTitle("La fecha de inicio no coincide con la de antig\u00FCedad.");
			} else if (null != seniorityDateValue) {
				DateUtils.resetTime(startDateValue);
				DateUtils.resetTime(seniorityDateValue);

				if (DateUtils.equals(startDateValue, seniorityDateValue)) {
					this.seniorityDate.removeWarning();
					this.seniorityDate.setTitle("");
				} else {
					this.seniorityDate.addWarning();
					this.seniorityDate.setTitle("La fecha de inicio no coincide con la de antig\u00FCedad.");
				}
			} else {
				this.seniorityDate.removeWarning();
				this.seniorityDate.setTitle("");
			}

			onContractSeniorityDateChange(seniorityDateValue);
		});
		
		if(null != selectionHandlerAgreement) selectionHandlerAgreement.removeHandler();
		if(null != valueChangeHandlerAgreement) valueChangeHandlerAgreement.removeHandler();
		if(null != keyUpHandlerAgreement) keyUpHandlerAgreement.removeHandler();
		
		selectionHandlerAgreement = agreement.getSuggestBox().addSelectionHandler(e -> {
			selectionAgreementInProgress = true;

		    String agreementDescription = agreement.getValue();
			for (Agreement agreementIt : this.agreements)
				if (AonStringUtils.equalsIgnoreCase(agreementIt.getDescription(), agreementDescription))
					onContractAgreementChange(agreementIt.getId(), agreementIt.getSSNumber());
			isAgreementAndLevelSelected();

		    Scheduler.get().scheduleDeferred(() -> selectionAgreementInProgress = false);
		});

		valueChangeHandlerAgreement = agreement.getSuggestBox().addValueChangeHandler(e -> {
		    if (selectionAgreementInProgress) {
		    	return;
		    }

		    String agreementDescription = agreement.getValue();
			if (AonStringUtils.isBlank(agreementDescription)) {
				onContractAgreementChange(null, null);
				level.clearItems();
				level.addItem("-", "-1");
				isAgreementAndLevelSelected();
			}
		});
		
		keyUpHandlerAgreement = agreement.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				agreement.getSuggestBox().setText("");
				agreement.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				agreement.hideSuggestionList();
		});
		
		level.addChangeHandler(e -> {
			Integer agreementLevelId = Integer.parseInt(this.level.getValue());
			String levelDescription = this.level.getListBox().getSelectedItemText().split("- ")[1];
			this.category.setValue(levelDescription);
			onContractAgreementLevelChange(agreementLevelId);
			onContractCategoryChange(levelDescription);
			isAgreementAndLevelSelected();
		});
		
		category.addValueChangeHandler(e -> {
			String categoryStr = this.category.getValue();
			if (AonStringUtils.isNotBlank(categoryStr))
				onContractCategoryChange(categoryStr);
		});
		
		quoteGroup.addChangeHandler(e -> {
			String quoteGroupStr = String.valueOf(this.quoteGroup.getValue());
			quoteGroupStr = AonStringUtils.equalsIgnoreCase(quoteGroupStr, "-1") ? null : quoteGroupStr;

			if (AonStringUtils.equalsIgnoreCase(quoteGroupStr, "-1"))
				onContractQuoteGroupChange(null);
			else
				onContractQuoteGroupChange(quoteGroupStr);

			showHideQuoteIdx(null == quoteGroupStr ? null : Integer.parseInt(quoteGroupStr));
		});
		
		quoteGroupCotizB.addValueChangeHandler(e -> {
			onContractQuoteGroupIdx(e.getValue());
		});
		
		occupation.addChangeHandler(e -> {
			String occupationStr = String.valueOf(this.occupation.getValue());
			if (AonStringUtils.equalsIgnoreCase(occupationStr, "-1"))
				onContractOccupationChange(null);
			else
				onContractOccupationChange(occupationStr);
		});
		
		cnoSB.getSuggestBox().addSelectionHandler(e -> {
			String cnoValue = cnoSB.getValue();
			if (!AonStringUtils.isBlank(cnoValue))
				cnoValue = cnoValue.split(" -")[0];

			// Do something with cnoValue
			onEmployeeCnoSuggestionChange(cnoValue);
		});

		cnoSB.getSuggestBox().addValueChangeHandler(e -> {
			if (AonStringUtils.isBlank(e.getValue()))
				onEmployeeCnoSuggestionChange(null);
		});

		cnoSB.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cnoSB.setValue("");
				cnoSB.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cnoSB.hideSuggestionList();
		});
		
		rlce.addChangeHandler(e -> {
			onContractRLCEChange(this.rlce.getValue());
		});
		
		employeesColective.addChangeHandler(e -> {
			String value = this.employeesColective.getValue();
			onContractEmployeesColectiveChange(AonStringUtils.isBlank(value) ? null : value);
		});
		
		journeyType.addChangeHandler(e -> {
			Boolean journeyTypeStr = Boolean.valueOf(this.journeyType.getValue());
			if (Boolean.TRUE.equals(journeyTypeStr))
				showElementsFullTimeJourneyTypeContract();
			else
				showPartialTimeContract();

			onContractJourneyTypeChange(journeyTypeStr);
		});
		
		partialityCoef.addValueChangeHandler(e -> {
			Double partialityCoefStr = this.partialityCoef.getValue();
			onContractPartialityChange(partialityCoefStr);
		});
		
	}
	
	private void addEmployeeDataTableHadlers() {
		birthDate.addValueChangeHandler(e -> {
			Date birthDateValue = this.birthDate.getValue();

			if (null != birthDateValue) {
				Date actualDay = new Date();
				Integer ageValue = getYears(actualDay, birthDateValue);
				this.age.setValue("( " + (ageValue) + " a\u00F1os )");
			} else
				this.age.setValue("");

			onEmployeeBirthDateChange(birthDateValue);
		});
		
		gender.addChangeHandler(e -> onEmployeeGenderChange(Byte.parseByte(this.gender.getValue())));
		
		civilStatus.addChangeHandler(e -> onEmployeeCivilStatusChange( Byte.parseByte(this.civilStatus.getValue())));
		
		streetType.addChangeHandler(e -> onEmployeeStreetTypeChange(String.valueOf(this.streetType.getValue())));
		
		address.getSuggestBox().addValueChangeHandler(e -> onEmployeeAddressChange(this.address.getValue()));
		
		addressNum.addValueChangeHandler(e -> onEmployeeAddressNumChange(this.addressNum.getValue()));
		
		addressInfo.addValueChangeHandler(e -> onEmployeeAddressInfoChange(this.addressInfo.getValue()));
		
		addressZip.addValueChangeHandler(e -> {
			String addressZipValue = this.addressZip.getValue();
			onEmployeeAddressZipChange(addressZipValue);

			if (addressZipValue.length() == 5) {
				String zip = this.addressZip.getValue().substring(0, 2);
				Optional<com.esferalia.aon.gwt.payroll.shared.Country> country = countries.stream().filter(c ->  AonStringUtils.equals(c.getCountry().getCode(), zip) || c.getProvinces().stream().filter(p -> AonStringUtils.equals(p.getCode(), zip)).findFirst().isPresent() ).findFirst();
				if(country.isEmpty()) addressProvince.setValue(null);
				else {
					if(AonStringUtils.equals(zip, country.get().getCountry().getCode()))
						addressProvince.setValue(country.get().getCountry().getId().toString());
					else {
						Optional<Geozone> provice = country.get().getProvinces().stream().filter(p -> AonStringUtils.equals(p.getCode(), zip)).findFirst();
						if(provice.isEmpty()) addressProvince.setValue(null);
						else addressProvince.setValue(provice.get().getId().toString());
					}
				}
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince.getListBox());
			}
		});
		
		addressProvince.addChangeHandler(e -> {
			Integer geozoneId = AonStringUtils.isBlank(this.addressProvince.getValue()) ? null : Integer.parseInt(this.addressProvince.getValue());
			updateMunicipalities();
			onEmployeeAddressProvinceChange(geozoneId);
		});
		
		addressMunicipality.getSuggestBox().addSelectionHandler(e -> {
			if(AonStringUtils.isBlank(addressMunicipality.getValue()))
				onEmployeeAddressMunicipalityChange(null, null);
			else {
				Entry<String, String> city = municipalities.getZipByMunicipalityName(addressMunicipality.getValue());
				
				addressMunicipality.setValue(city.getValue(), false);
				onEmployeeAddressMunicipalityChange(city.getValue(), city.getKey());
			}
			
		});
		
		addressMunicipality.getSuggestBox().addValueChangeHandler(e -> {
			if(AonStringUtils.isBlank(addressMunicipality.getValue()))
				onEmployeeAddressMunicipalityChange(null, null);
			else {
				Entry<String, String> city = municipalities.getZipByMunicipalityName(addressMunicipality.getValue());
				
				addressMunicipality.setValue(city.getValue(), false);
				onEmployeeAddressMunicipalityChange(city.getValue(), city.getKey());
			}
		});
		
		mobile.addValueChangeHandler(e -> onEmployeeMobileChange(this.mobile.getValue()));
		phone.addValueChangeHandler(e -> onEmployeePhoneChange(this.phone.getValue()));
		email.addValueChangeHandler(e -> onEmployeeEmailChange(this.email.getValue()));
		
		payMethod.addChangeHandler(e -> {
			Integer payMethodId = Integer.parseInt(this.payMethod.getValue());
			if (AonNumberUtils.equals(payMethodId, -1))
				onEmployeePayMethodChange(null);
			else
				onEmployeePayMethodChange(payMethodId);
		});
		
		account.getSuggestBox().addValueChangeHandler(e -> {
			String accountStr = this.account.getValue();
			accountStr = accountStr.replaceAll("\\W+", "");
			accountStr = accountStr.toUpperCase();

			if (accountStr.length() > 0) {
				if (Iban.validateIBAN(accountStr)) {
					this.account.removeError();
					this.account.setTitle(null);
				} else {
					this.account.addError();
					this.account.setTitle("IBAN no valido");
				}
			}

			String bankAlias = getBankAlias(accountStr);
			String bankSwift = getBankSwift(accountStr);
			this.bic.setValue(bankSwift);
			onEmployeeAccountChange(accountStr, bankAlias, bankSwift);
			
			reformatAccount(account);
		});
		
		bic.addValueChangeHandler(e -> onEmployeeBICChange(this.bic.getValue()));
		
	}
	
	private void resetElements() {

		// TABLA DATOS CONTRATO

		this.document.setValue(AonStringUtils.EMPTY);
		this.documentType.setValue(AonStringUtils.EMPTY);
		this.nationality.setValue(AonStringUtils.EMPTY);
		this.securitySocialNum.setValue(AonStringUtils.EMPTY);
		this.name.setValue(AonStringUtils.EMPTY);
		this.firstSurname.setValue(AonStringUtils.EMPTY);
		this.secondSurname.setValue(AonStringUtils.EMPTY);
		this.ssRegimeType.clearItems();
		this.activityCCC.clearItems();
		this.mdCTZLB.clearItems();
		this.mdTBTLB.clearItems();
		this.workplace.clearItems();
		this.contractTypeLB.clearItems();
		this.modality.clearItems();
		
		this.startDate.setValue(null);
		this.endDate.setValue(null);
		this.seniorityDate.setValue(null);
		
		this.agreement.setValue(AonStringUtils.EMPTY);
		this.level.clearItems();
		this.category.setValue("");
		
		this.quoteGroup.clearItems();
		this.quoteGroupCotizB.setValue(false);
		this.occupation.clearItems();
		this.rlce.clearItems();
		this.employeesColective.clearItems();
		this.journeyType.clearItems();
		this.partialityCoef.setValue(null);
		this.journeyDuration.clear();
		this.cnoSB.setValue(AonStringUtils.EMPTY);
		
		// TABLA DATOS EMPLEADO

		this.birthDate.setValue(null);
		this.gender.clearItems();
		this.civilStatus.clearItems();
		this.streetType.clearItems();
		this.address.setValue(AonStringUtils.EMPTY);
		this.addressNum.setValue(AonStringUtils.EMPTY);
		this.addressInfo.setValue(AonStringUtils.EMPTY);
		this.addressZip.setValue(AonStringUtils.EMPTY);
		this.addressMunicipality.setValue(AonStringUtils.EMPTY);
		this.mobile.setValue(AonStringUtils.EMPTY);
		this.phone.setValue(AonStringUtils.EMPTY);
		this.email.setValue(AonStringUtils.EMPTY);
		this.payMethod.clearItems();
		this.account.setValue(AonStringUtils.EMPTY);
		this.bic.setValue(AonStringUtils.EMPTY);
		
	}
	
	private void initializeComponents() {
		// TABLA DATOS CONTRATO
		
		// Nacionalidad
		MultiWordSuggestOracle oracleCountries = (MultiWordSuggestOracle) nationality.getSuggestBox().getSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		
		this.nationality.setAutoSelectEnabled(true);

		// TIPO DE COTIZACIÓN
		this.ssRegimeType.addItem("COM\u00daN", "0");
		this.ssRegimeType.addItem("RETA", "3");
		this.ssRegimeType.addItem("SOCIOS COOP", "1");
		this.ssRegimeType.addItem("JUBILACION ACTIVA", "2");
		this.ssRegimeType.addItem("GARANTIA JUVENIL", "4");
		this.ssRegimeType.addItem("ASIMILADO AL R\u00C9GIMEN GENERAL", "5");

		// MODALIDAD DE COTIZACION
		this.mdTBTLB.addItem("COM\u00daN", "0");
		this.mdTBTLB.addItem("Adm./Consejero Negocio < 100.000 \u20ac", "1");
		this.mdTBTLB.addItem("Adm./Consejero Negocio > 100.000 \u20ac", "2");
		this.mdTBTLB.addItem("No Residente", "3");

		// MODALIDAD DE COTIZACION
		this.mdCTZLB.addItem("-", "-1");
		this.mdCTZLB.addItem("Cotizaci\u00F3n mensual", "1");
		this.mdCTZLB.addItem("Jornadas reales", "2");

		// MODALIDAD
		this.modality.addItem("-", "-1");

		// GRUPO DE COTIZACION
		QuoteGroup.getQuoteGroup().entrySet()
				.forEach(entry -> quoteGroup.addItem(entry.getKey(), entry.getValue()));

		// OCUPACION
		Occupation.getOccupation().entrySet()
				.forEach(entry -> occupation.addItem(entry.getKey(), entry.getValue()));

		// RLCE
		RLCE.getRLCE().entrySet()
				.forEach(entry -> rlce.addItem(entry.getKey() + " - " + entry.getValue(), entry.getKey()));

		List<String> cnoSuggest = new ArrayList<>();
		for (Entry<String, CNO> entry : cnoMap.entrySet())
			cnoSuggest.add(entry.getKey() + " - " + entry.getValue().getTitle());

		cnoSuggest.sort((o1, o2) -> o1.compareTo(o2));

		MultiWordSuggestOracle orclCno = (MultiWordSuggestOracle) cnoSB.getSuggestBox().getSuggestOracle();
		orclCno.addAll(cnoSuggest);
		orclCno.setDefaultSuggestionsFromText(cnoSuggest);
		cnoSB.setAutoSelectEnabled(false);
		cnoSB.getElement().setPropertyString("placeholder", "C\u00f3digo CNO... (Ctrl + espacio para ver sugerencias)");
		
		// EMPLOYEES COLECTIVE
		this.employeesColective.addItem("-", "");
		this.employeesColective.addItem("CT CIRCUNSTANCIAS PRODUCCI\u00d3N", "967");
		this.employeesColective.addItem("CT CIRCUNSTANCIAS PRODUCCI\u00d3N PREVISIBLES", "968");
		
		// TIPO DE JORNADA
		this.journeyType.addItem("-", "");
		this.journeyType.addItem("Tiempo Completo", "true");
		this.journeyType.addItem("Tiempo Parcial", "false");

		// TABLA DATOS EMPLEADO

		// SEXO
		this.gender.addItem("Hombre", "0");
		this.gender.addItem("Mujer", "1");
		this.gender.addItem("Desconocido", "2");

		// CIVIL STATUS
		this.civilStatus.addItem("SOLTERO", "0");
		this.civilStatus.addItem("CASADO", "1");
		this.civilStatus.addItem("DIVORCIADO", "2");
		this.civilStatus.addItem("SEPARADO", "3");
		this.civilStatus.addItem("VIUDO", "4");
		this.civilStatus.addItem("DESCONOCIDO", "5");

		// TIPO DE VIA
		for (int i = 0; i < StreetType.values().length; i++)
			streetType.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getShortCode());

		// PROVINCIA
		this.addressProvince.clearItems();
		this.addressProvince.addItem("-", "-1");

		for (com.esferalia.aon.gwt.payroll.shared.Country country : this.countries) {
			addressProvince.addItem(country.getCountry().getName(), country.getCountry().getId().toString());
			addressProvince.getElement().getElementsByTagName("option").getItem(addressProvince.getListBox().getItemCount() - 1)
					.setAttribute("disabled", "disabled");
			for (Geozone province : country.getProvinces())
				addressProvince.addItem(province.getName(), province.getId().toString());
		}
		
	}

	private void initDisplayElements() {
		activityCCC.getElement().getStyle().clearDisplay();

		this.contractTypeLB.getElement().getStyle().clearDisplay();
		this.contractTypeFreelance.getElement().getStyle().setDisplay(Display.NONE);

		mdCTZLB.getElement().getStyle().setDisplay(Display.NONE);

		modality.getElement().getStyle().clearDisplay();
		quoteGroup.getElement().getStyle().clearDisplay();
		occupation.getElement().getStyle().clearDisplay();

		hideEmployeesColective();

		journeyType.getElement().getStyle().setDisplay(Display.NONE);
		journeyDuration.getElement().getStyle().setDisplay(Display.NONE);
		partialityCoef.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------- Auxiliar Methods

	public CNO getCNOByCode(String cnoCode) {
		return this.cnoMap.get(cnoCode);
	}

	public void contractFireEventsWithOutValue() {
		String contractTypeStr = String.valueOf(this.contractTypeLB.getValue());

		if (!AonStringUtils.equalsIgnoreCase(contractTypeStr, "-1")) {
			Integer contractTypeInt = Integer.parseInt(contractTypeStr);
			if (AonNumberUtils.between(contractTypeInt, 200, 400) || AonNumberUtils.between(contractTypeInt, 500, 599)
					|| AonNumberUtils.equals(contractTypeInt, 0))
				showPartialTimeContract();
			else
				showElementsFullTimeContract();

			if (AonNumberUtils.equals(contractTypeInt, 402) || AonNumberUtils.equals(contractTypeInt, 502)) {
				showEmployeesColective();
			} else {
				hideEmployeesColective();
				onContractEmployeesColectiveChange(null);
			}

			checkContracts401And501(contractTypeInt);

			updateModality(contractTypeInt);
		}
	}

	private Integer getProvincesGeozone(String provinceCode) {
		for (com.esferalia.aon.gwt.payroll.shared.Country country : countries)
			for (Geozone province : country.getProvinces())
				if (AonStringUtils.equalsIgnoreCase(province.getCode(), provinceCode.substring(0, 2)))
					return province.getId();

		return null;
	}
	
	public void checkCCCType(Byte cccType) {
		if(null != cccType && cccType == (byte)4)
			enable(this.ssRegimeType, "5");
		else
			disable(this.ssRegimeType, "5");
	}

	// ------------------------------------------------- Fill default fields

	public void fillDefaultFields() {
		// SS REGIME
		ssRegimeType.getListBox().setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), ssRegimeType.getListBox());

		// GENDER
		gender.getListBox().setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gender.getListBox());

		// CIVIL STATUS
		civilStatus.getListBox().setSelectedIndex(5);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), civilStatus.getListBox());

		// STREET_TYPE
		streetType.getListBox().setSelectedIndex(14); // Calle
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), streetType.getListBox());
	}

	// ------------------------------------------------- Initialize SuggestBox

	public void initSuggestBox(WorkplaceEmployees workplaceEmployees) {
		// DOCUMENT
		List<String> employeesDocuments = workplaceEmployees.getWorkplaceEmployeesDocument();
		List<String> employeesDocumentsSuggest = new ArrayList<>();
		employeesDocuments.forEach(documentValue -> employeesDocumentsSuggest.add(documentValue + ""));
		MultiWordSuggestOracle orclDocuments = (MultiWordSuggestOracle) document.getSuggestBox().getSuggestOracle();
		orclDocuments.addAll(employeesDocumentsSuggest);
		document.setAutoSelectEnabled(false);

		// SS_NUMBER
		List<String> employeesSSNumbers = workplaceEmployees.getWorkplaceEmployeesSSNumber();
		List<String> employeesSSNumbersSuggest = new ArrayList<>();
		employeesSSNumbers.forEach(ssNum -> employeesSSNumbersSuggest.add(ssNum + ""));
		MultiWordSuggestOracle orclSSNumbers = (MultiWordSuggestOracle) securitySocialNum.getSuggestBox().getSuggestOracle();
		orclSSNumbers.addAll(employeesSSNumbersSuggest);
		securitySocialNum.setAutoSelectEnabled(false);

		// NAMES
		List<String> employeesNames = workplaceEmployees.getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<>();
		employeesNames.forEach(nameValue -> employeesNamesSuggest.add(nameValue + ""));
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) name.getSuggestBox().getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		name.setAutoSelectEnabled(false);

		// SURNAME
		List<String> employeesSurNames = workplaceEmployees.getWorkplaceEmployeesSurName();
		List<String> employeesSurNamesSuggest = new ArrayList<>();
		employeesSurNames.forEach(surName -> employeesSurNamesSuggest.add(surName + ""));
		MultiWordSuggestOracle orclSurNames = (MultiWordSuggestOracle) firstSurname.getSuggestBox().getSuggestOracle();
		orclSurNames.addAll(employeesSurNamesSuggest);
		firstSurname.setAutoSelectEnabled(false);
	}

	public void initActivitiesCCC(Map<Integer, String> activities, Map<Integer, CCCInfo> cccs) {
		// ACTIVITY - CCC
		activityCCC.clearItems();
		activityCCC.addItem("-", "-1");
		if (null != activities)
			for (Entry<Integer, String> entry : activities.entrySet())
				for (CCCInfo cccInfo : cccs.values())
					if (cccInfo.getActivityId().equals(entry.getKey()))
						activityCCC.addItem(
								entry.getValue() + " - " + getCCCType(cccInfo.getType()) + "[" + cccInfo.getCcc()
										+ "] - " + cccInfo.getGeozone(),
								cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
	}

	public void initWorkplaces(List<Workplace> workplaces) {
		// WORKPLACE
		workplace.clearItems();
		workplace.addItem("-", "-1");
		for (Workplace workplaceInfo : workplaces)
			workplace.addItem(workplaceInfo.getDescription(), workplaceInfo.getId().toString());
	}

	public void initContractType() {
		// TIPO DE CONTRATO
		contractTypeLB.clearItems();
		contractTypeLB.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			contractTypeLB.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(),
					AonStringUtils.leftPad(entry.getKey().toString(), 3, '0'));
	}

	public void initAgreements(List<Agreement> activeAgreements) {
		// CONVENIO
		this.agreements = activeAgreements;

		List<String> agreementDescriptions = new ArrayList<>();

		for (Agreement agreementIt : this.agreements)
			agreementDescriptions.add(agreementIt.getDescription());

		MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) agreement.getSuggestBox().getSuggestOracle();
		orclAgreements.addAll(agreementDescriptions);
		orclAgreements.setDefaultSuggestionsFromText(agreementDescriptions);
		agreement.setAutoSelectEnabled(true);
		agreement.getElement().setPropertyString("placeholder",
				"Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");
	}

	public void initPayMethods(Map<String, String> payMethods) {
		// PAY METHODS
		payMethod.addItem("-", "-1");
		for (Entry<String, String> entry : payMethods.entrySet()) {
			payMethod.addItem(entry.getValue(), entry.getKey());
		}
	}

	// ------------------------------------------------- Initialize SuggestBox
	// (Auxiliar methods)

	private String getCCCType(Byte type) {
		switch (type) {
		case (byte) 0:
			return "PRINCIPAL";
		case (byte) 1:
			return "FORMACION Y APRENDIZAJE";
		case (byte) 3:
			return "REPRESENTANTES DE COMERCIO";
		case (byte) 4:
			return "ASIMILADOS R.GENERAL";
		case (byte) 5:
			return "BECARIOS";
		case (byte) 6:
			return "EMPLEADOS DE HOGAR";
		case (byte) 7:
			return "TRABAJADOR CUENTA AJENA";
		case (byte) 8:
			return "ARTISTA";
		default:
			return "PRINCIPAL";
		}
	}

	// ------------------------------------------------- Show/hide employee table

	public void hideEmployeeTable() {
		if(null != employeeDataTable)
			employeeDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	// ------------------------------------------------- Show/hide methods
	// freelancer

	public void showElementsFreelancerTable() {
		activityCCC.getElement().getStyle().setDisplay(Display.NONE);

		contractTypeLB.getElement().getStyle().setDisplay(Display.NONE);
		contractTypeFreelance.getElement().getStyle().clearDisplay();

		mdCTZLB.getElement().getStyle().setDisplay(Display.NONE);

		modality.getElement().getStyle().setDisplay(Display.NONE);
		quoteGroup.getElement().getStyle().setDisplay(Display.NONE);
		occupation.getElement().getStyle().setDisplay(Display.NONE);

		journeyType.getElement().getStyle().clearDisplay();
		journeyDuration.getElement().getStyle().setDisplay(Display.NONE);
		partialityCoef.getElement().getStyle().setDisplay(Display.NONE);
		cnoSB.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void hideElementsFreelancerTable() {
		activityCCC.getElement().getStyle().clearDisplay();

		contractTypeLB.getElement().getStyle().clearDisplay();
		contractTypeFreelance.getElement().getStyle().setDisplay(Display.NONE);

		mdCTZLB.getElement().getStyle().clearDisplay();

		modality.getElement().getStyle().clearDisplay();
		quoteGroup.getElement().getStyle().clearDisplay();
		quoteGroupCotizB.getElement().getStyle().setDisplay(Display.NONE);
		occupation.getElement().getStyle().clearDisplay();

		journeyType.getElement().getStyle().setDisplay(Display.NONE);
		journeyDuration.getElement().getStyle().setDisplay(Display.NONE);
		partialityCoef.getElement().getStyle().setDisplay(Display.NONE);
		cnoSB.getElement().getStyle().clearDisplay();
	}

	// ------------------------------------------------- Show/hide methods
	// partial/full time

	public void showElementsFullTimeContract() {
		journeyType.getElement().getStyle().setDisplay(Display.NONE);
		journeyDuration.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showElementsFullTimeJourneyTypeContract() {
		journeyDuration.getElement().getStyle().setDisplay(Display.NONE);
		partialityCoef.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showPartialTimeContract() {
		showElementsPartialTimeContract();
		journeyDuration.clear();
		
		AonTableButton calendarBtn = new AonTableButton("Abrir calendario", AON.CSS.aonIconEditCalendar());
		
		AonCustomTextBox calendar = new AonCustomTextBox("Horas Jornada");
		calendar.setValue("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		calendar.setTitle("Las horas se deben definir en el calendario del empleado");
		calendar.setEnable(false);
		calendar.addButton(calendarBtn);
		
		calendarBtn.addClickHandler(e -> {
			e.stopPropagation();
			onContractJourneyDurationClick();
		});
		calendar.getTextBox().addClickHandler(e -> onContractJourneyDurationClick());
		
		journeyDuration.add(calendar);
	}

	void showElementsPartialTimeContract() {
		journeyType.getElement().getStyle().clearDisplay();
		journeyDuration.getElement().getStyle().clearDisplay();
		partialityCoef.getElement().getStyle().clearDisplay();
	}

	// ------------------------------------------------- Show/hide mdCtz methods

	public void showMdCtzContract() {
		mdCTZLB.getElement().getStyle().clearDisplay();
	}

	public void hideMdCtzContract() {
		mdCTZLB.getElement().getStyle().setDisplay(Display.NONE);
		this.mdCTZLB.getListBox().setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.mdCTZLB.getListBox());

	}

	// ------------------------------------------------- Show/hide EmployeeColective
	// methods

	public void showEmployeesColective() {
		employeesColective.getElement().getStyle().clearDisplay();
		rlce.setMaxWidth("16rem");
	}

	public void hideEmployeesColective() {
		employeesColective.getElement().getStyle().setDisplay(Display.NONE);
		rlce.setMaxWidth("100%");
	}

	// ------------------------------------------------- Show/hide Quote Idx

	public void showHideQuoteIdx(Integer quoteGroup) {
		if (AonNumberUtils.equals(quoteGroup, 8) || AonNumberUtils.equals(quoteGroup, 9)
				|| AonNumberUtils.equals(quoteGroup, 10) || AonNumberUtils.equals(quoteGroup, 11))
			quoteGroupCotizB.getElement().getStyle().clearDisplay();
		else {
			quoteGroupCotizB.getElement().getStyle().setDisplay(Display.NONE);
			
			quoteGroupCotizB.setValue(false);
			onContractQuoteGroupIdx(false);
		}
	}

	// -------------------------------------------------
	// CheckStatus(EmployeeDraftObject) - EmployeeTree

	public void setEndDate(Date endDateValue) {
		endDate.setValue(endDateValue, false);
		onContractEndDateChange(endDateValue);
	}

	public void setStartDate(Date startDateValeu) {
		startDate.setValue(startDateValeu, false);
		onContractStartDateChange(startDateValeu);
	}

	public void setOcupation(String str) {
		switch (str) {
		case "a":
			occupation.getListBox().setSelectedIndex(1);
			break;
		case "b":
			occupation.getListBox().setSelectedIndex(2);
			break;
		case "d":
			occupation.getListBox().setSelectedIndex(3);
			break;
		case "e":
			occupation.getListBox().setSelectedIndex(4);
			break;
		case "f":
			occupation.getListBox().setSelectedIndex(5);
			break;
		case "g":
			occupation.getListBox().setSelectedIndex(6);
			break;
		case "h":
			occupation.getListBox().setSelectedIndex(7);
			break;
		default:
			occupation.getListBox().setSelectedIndex(0);
			break;
		}
		onContractOccupationChange(str);

	}

	// ------------------------------------------------- Account methods

	public void reformatAccount(AonCustomSuggestBox accountField) {
		String accountText = accountField.getSuggestBox().getText();
		accountText = accountText.replaceAll("\\W+", "");
		if (accountText.length() == 24) {
			accountField.getSuggestBox().setText(accountText.substring(0, 4) + "  " + accountText.substring(4, 8) + "  "
					+ accountText.substring(8, 12) + "  " + accountText.substring(12, 16) + "  "
					+ accountText.substring(16, 20) + "  " + accountText.substring(20, 24));
		} else
			accountField.getSuggestBox().setText(accountText);
	}

	public void initIbans(ArrayList<String> employeeIbans) {
		List<String> employeesIbanSuggest = new ArrayList<>();
		employeeIbans.forEach(employeesIbanSuggest::add);
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) account.getSuggestBox().getSuggestOracle();
		orclIbans.addAll(employeesIbanSuggest);
		account.setAutoSelectEnabled(false);
	}

	private String getBankSwift(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getSwift();
		}
		return null;
	}

	private String getBankAlias(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getBankName();
		}
		return null;
	}

	// ------------------------------------------------- Employee table methods
	
	public void blockVariablesExistingContract() {
		nationality.setEnable(true);

		String ssNum = this.securitySocialNum.getValue().trim();
		if (AonStringUtils.isNotBlank(ssNum))
			securitySocialNum.setEnable(!checkSSNumValidation(ssNum));
		else
			securitySocialNum.setEnable(true);
	}

	public void unblockVariablesExistingContract() {
//		document.setEnable(true);
		nationality.setEnable(true);
		securitySocialNum.setEnable(true);
	}

	public void blockFieldsExistingPayroll() {
		this.contractTypeLB.setEnable(false);
		this.quoteGroup.setEnable(false);
		this.occupation.setEnable(false);
		this.partialityCoef.setEnable(false);
	}

	public void unblockFieldsExistingPayroll() {
		this.contractTypeLB.setEnable(true);
		this.quoteGroup.setEnable(true);
		this.occupation.setEnable(true);
		this.partialityCoef.setEnable(true);
	}

	// ------------------------------------------------- Auxiliar methods

	public String checkDocumentType(String document) {

		if (null == document)
			return "Pasaporte";

		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(document.toUpperCase()))
			return "DNI";
		else if (niePattern.test(document.toUpperCase()))
			return "NIE";
		else if (cifPattern.test(document.toUpperCase()))
			return "CIF";
		else
			return "Pasaporte";
	}

	public boolean checkDocumentValidation(String document) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		
		if (dniPattern.test(document.toUpperCase()) && document.length() == 9) {
			return Dni.checkDNI(document);
		} else if (niePattern.test(document.toUpperCase()) && document.length() == 9) {
			return Dni.checkNIE(document);
		}	else
			return AonStringUtils.isBlank(document);
	}

	public void showNationality(String documentTypeStr) {
		if (documentTypeStr.equals("CIF") || documentTypeStr.equals("Pasaporte") || documentTypeStr.equals("NIE"))
			nationality.getElement().getStyle().clearDisplay();
		else {
			nationality.getElement().getStyle().setDisplay(Display.NONE);
			nationality.setValue("");
		}
	}

	public boolean checkSSNumValidation(String ssNumStr) {
		SocialSecurity ss = new SocialSecurity(ssNumStr);
		
		return ss.checkSS();
	}

	public void updateModality(Integer contractTypeInt) {
		this.modality.clearItems();
		this.modality.addItem("-", "-1");

		List<ModelRecord> contractTypeModels = this.contractType.getModelsContractType(contractTypeInt);
		for (ModelRecord model : contractTypeModels)
			this.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
	}

	private String getIso2(String countryName) {
		for (Country country : Country.values())
			if (AonStringUtils.equalsIgnoreCase(country.getName(), countryName))
				return country.getIso2();

		return null;
	}

	public void updateMunicipalities() {
		
		String provinceCode = getProvinceCode();
		
		HashMap<String, String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		List<String> municipalitySuggest = new ArrayList<>();
		municipalitiesOfProvince.entrySet().forEach(e -> municipalitySuggest.add(e.getValue()));
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) addressMunicipality.getSuggestBox().getSuggestOracle();
		orclNames.addAll(municipalitySuggest);
		
		addressMunicipality.setAutoSelectEnabled(false);
	}
	
	private String getProvinceCode() {
		if(AonStringUtils.isBlank(addressProvince.getValue()) || AonStringUtils.equalsIgnoreCase(addressProvince.getValue(), "-1")) return null;
		
		com.esferalia.aon.gwt.payroll.shared.Country country = countries.stream().filter(c -> c.getCountry().getId().equals(Integer.parseInt(addressProvince.getValue())) ||  c.getProvinces().stream().filter(p -> p.getId().equals(Integer.parseInt(addressProvince.getValue()))).findAny().isPresent() ).findFirst().get();
		
		if(country.getCountry().getId().equals(Integer.parseInt(addressProvince.getValue())))
			return country.getCountry().getCode();
		else {
			Geozone geozone = country.getProvinces().stream().filter(p -> p.getId().equals(Integer.parseInt(addressProvince.getValue()))).findFirst().get();
			return geozone.getCode();
		}
	}

	private Integer getYears(Date actualDay, Date birthDate) {
		DateUtils.resetTime(actualDay);
		DateUtils.resetTime(birthDate);

		DateTimeFormat formatter = DateTimeFormat.getFormat("yyyyMMdd");
		int d1 = Integer.parseInt(formatter.format(birthDate));
		int d2 = Integer.parseInt(formatter.format(actualDay));
		return (d2 - d1) / 10000;
	}

	private void checkContracts401And501(Integer contractType) {
		if (AonNumberUtils.equals(contractType, 401) || AonNumberUtils.equals(contractType, 501)) {
			Date marchEnd = new Date();
			marchEnd.setMonth(2);
			marchEnd = DateUtils.getLastDayOfMonth(marchEnd);

			if (null != startDate.getValue() && DateUtils.isAfterOrEquals(startDate.getValue(), marchEnd))
				fireError("Error Contrato 401/501",
						"A partir del 31/03/2022 (incluido) no se pueden crear contratos 401/501, estos han sido reemplazados por 402/502 eligiendo uno de sus Colectivos Trabajadores");
		}
	}

	// ------------------------------------------------- Save methods

	public Map<String, String> checkSaveAndGetErrors() {
		cleanErrorStyles();
		Map<String, String> messageMap = new HashMap<>();
		Byte ssRegime = Byte.valueOf(this.ssRegimeType.getValue());

		if (ssRegime == (byte) 3) { // RETA
			if (!isNotNameBlank())
				messageMap.put("Nombre", "Campo obligatorio");
			if (!isWokplaceSelected())
				messageMap.put("Centro de trabajo", "Campo obligatorio");
			if (!isAgreementAndLevelSelected())
				messageMap.put("Convenio", "Para poder asigar un convenio se debe seleccionar un nivel/categoria");
		} else {
			if (!isNotNameBlank())
				messageMap.put("Nombre", "Campo obligatorio");
			if (!isWokplaceSelected())
				messageMap.put("Centro de trabajo", "Campo obligatorio");
			if (!isActivityCCCSelected())
				messageMap.put("Actividad", "Campo obligatorio");
			if (!isContractTypeSelected())
				messageMap.put("Tipo de contrato", "Campo obligatorio");
			if (!isAgreementAndLevelSelected())
				messageMap.put("Convenio", "Para poder asigar un convenio se debe seleccionar un nivel/categoria");
		}

		Date startDateValue = null == startDate.getValue() ? null : DateUtils.copyDateOnly(startDate.getValue());
		Date endDateValue = null == endDate.getValue() ? null : DateUtils.copyDateOnly(endDate.getValue());

		if (null == startDateValue)
			messageMap.put("Fecha inicio", "La fecha debe estar definida");
		if (null != endDateValue && endDateValue.before(startDateValue))
			messageMap.put("Fecha fin", "La fecha de inicio no puede ser posterior a la fecha de fin");

		String addressZipValue = addressZip.getValue();
		String addressProvinceValue = addressProvince.getValue();

		if ((AonStringUtils.isNotBlank(addressZipValue) || !AonStringUtils.equalsIgnoreCase(addressProvinceValue, "-1"))
				&& (AonStringUtils.isBlank(addressZipValue)
						|| AonStringUtils.equalsIgnoreCase(addressProvinceValue, "-1")))
			messageMap.put("Direcci\u00F3n",
					"Si rellena la direccion del trabajador, debera rellenar los campos azules correcta y obligatoriamente");

		return messageMap;
	}

	public boolean checkIfNewEmployeeIsPossible() {
		cleanErrorStyles();

		if (checkIfSaveIsPossible())
			return checkDates();
		else
			return false;
	}

	private boolean checkIfSaveIsPossible() {
		Byte ssRegime = Byte.valueOf(this.ssRegimeType.getValue());

		if (ssRegime == (byte) 3) { // RETA
			boolean isNotNameBlank = isNotNameBlank();
			boolean isWokplaceSelected = isWokplaceSelected();

			return isNotNameBlank && isWokplaceSelected;
		} else {
			boolean isNotNameBlank = isNotNameBlank();
			boolean isWokplaceSelected = isWokplaceSelected();
			boolean isActivityCCCSelected = isActivityCCCSelected();
			boolean isContractTypeSelected = isContractTypeSelected();

			return isNotNameBlank && isWokplaceSelected && isActivityCCCSelected && isContractTypeSelected;
		}
	}

	// -------------------------------------------------
	// SaveMethods.checkIfSaveIsPossible

	private boolean isNotNameBlank() {
		String nameValue = name.getValue();
		if (AonStringUtils.isBlank(nameValue)) {
			name.addError();
			return false;
		} else
			return true;
	}

	private boolean isWokplaceSelected() {
		String workplaceValue = workplace.getValue();
		if (AonStringUtils.equalsIgnoreCase(workplaceValue, "-1")) {
			workplace.addError();
			return false;
		} else
			return true;
	}

	private boolean isActivityCCCSelected() {
		String activityValue = activityCCC.getValue();
		if (AonStringUtils.equalsIgnoreCase(activityValue, "-1")) {
			activityCCC.addError();
			return false;
		} else
			return true;
	}

	private boolean isContractTypeSelected() {
		String contractTypeValue = contractTypeLB.getValue();
		if (AonStringUtils.equalsIgnoreCase(contractTypeValue, "-1")) {
			contractTypeLB.addError();
			return false;
		} else
			return true;
	}

	public boolean isCnoSelected() {
		return AonStringUtils.isNotBlank(cnoSB.getValue());
	}

	private boolean isAgreementAndLevelSelected() {
		String agreementValue = agreement.getValue();
		if (AonStringUtils.isBlank(agreementValue)) {
			level.removeWarning();
			return true;
		} else {
			String agreementLevelValue = level.getValue();
			if (AonStringUtils.equalsIgnoreCase(agreementLevelValue, "-1")) {
				level.addWarning();;
				return false;
			} else
				level.removeWarning();
				return true;
		}
	}

	private boolean checkDates() {
		Date startDateValue = null == startDate.getValue() ? null : DateUtils.copyDateOnly(startDate.getValue());
		Date endDateValue = null == endDate.getValue() ? null : DateUtils.copyDateOnly(endDate.getValue());

		if (null == startDateValue) {
			startDate.addError();
			return false;
		}

		if (null == endDateValue || endDateValue.after(startDateValue) || endDateValue.equals(startDateValue))
			return true;
		else {
			startDate.removeError();
			return false;
		}

	}

	// ------------------------------------------------- journeyDuration.Methods

	public void createJourneyDurationWarning() {
		journeyDuration.clear();
		AonTableButton calendarBtn = new AonTableButton("Abrir calendario", AON.CSS.aonIconEditCalendar());
		AonCustomTextBox calendar = new AonCustomTextBox("Horas Jornada");
		calendar.setValue("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		calendar.setTitle("Las horas se deben definir en el calendario del empleado");
		calendar.setEnable(false);
		calendar.addButton(calendarBtn);
		
		calendarBtn.addClickHandler(e -> {
			e.stopPropagation();
			onContractJourneyDurationClick();
		});
		calendar.getTextBox().addClickHandler(e -> onContractJourneyDurationClick());
		
		journeyDuration.add(calendar);
	}

	public void createJourneyDurationInfo(String messageStr) {
		journeyDuration.clear();
		AonCustomTextBox message = new AonCustomTextBox("Duraci\u00f3n Jornada");
		message.setValue(messageStr);
		message.setEnable(false);
		journeyDuration.add(message);

		message.getTextBox().addClickHandler(e -> onContractJourneyDurationClick());
	}

	// ------------------------------------------------- Add and remove styles
	
	private void disable(AonCustomListBox listBox, String value) {
		NodeList<OptionElement> options = ((SelectElement) listBox.getListBox().getElement().cast()).getOptions();
		for (int i = 0; i < options.getLength(); i++) {
			OptionElement option = options.getItem(i);
			if (AonStringUtils.equals(option.getValue(), value)) {
				option.setDisabled(true);
			}
		}
	}
	
	private void enable(AonCustomListBox listBox, String value) {
		NodeList<OptionElement> options = ((SelectElement) listBox.getListBox().getElement().cast()).getOptions();
		for (int i = 0; i < options.getLength(); i++) {
			OptionElement option = options.getItem(i);
			if (AonStringUtils.equals(option.getValue(), value)) {
				option.setDisabled(false);
			}
		}
	}

	// ------------------------------------------------- cleanWarningIcons

	public void cleanErrorStyles() {
		name.removeError();
		activityCCC.removeError();
		workplace.removeError();
		contractTypeLB.removeError();
		startDate.removeError();
		addressZip.removeError();
		addressProvince.removeError();
		addressMunicipality.removeError();
		level.removeError();
	}

	public void selectProvince(Integer geozoneId) {
		addressProvince.setValue(null == geozoneId ? "" : geozoneId.toString());
	}
	
	// ------------------------------------------------- Abstract methods

	// TABLA DATOS CONTRATO

	public abstract void onClearEmployeeClick();

	public abstract void onEmployeeDocumentSuggestionChange(String document);

	public abstract void onEmployeeDocumentChange(String document, String documentType);

	public abstract void onEmployeeNationalityChange(String countryIso2);

	public abstract void onEmployeeSSNumSuggestionChange(String ssNumber);

	public abstract void onEmployeeSSNumChange(String ssNumber);

	public abstract void onEmployeeNameSuggestionChange(String nameSurname);

	public abstract void onEmployeeNameChange(String name);

	public abstract void onEmployeeFirstSurnameSuggestionChange(String nameSurname);

	public abstract void onEmployeeFirstSurnameChange(String surname);

	public abstract void onEmployeeSecondSurnameChange(String secondSurname);

	public abstract void onContractSSRegimenChange(byte ssRegime);

	public abstract void onContractMdTBThange(String tbtType);

	public abstract void onContractActiviesCCCChange(String activityCCC);

	public abstract void onContractMdCTZhange(String mdCtz);

	public abstract void onContractWorkplaceChange(Integer workplaceId);

	public abstract void onContractTypeChange(String contractType);

	public abstract void onContractModalityChange(Integer contractModel);

	public abstract void onContractStartDateChange(Date startDate);

	public abstract void onContractEndDateChange(Date endDate);

	public abstract void onContractSeniorityDateChange(Date seniorityDate);

	public abstract void onContractAgreementChange(Integer agreementId, String agreementSSNumber);

	public abstract void onContractAgreementLevelChange(Integer levelId);

	public abstract void onContractCategoryChange(String category);

	public abstract void onContractQuoteGroupChange(String quoteGroup);

	public abstract void onContractQuoteGroupIdx(boolean quoteGroupMonth);

	public abstract void onContractOccupationChange(String occupation);

	public abstract void onContractRLCEChange(String rlce);

	public abstract void onContractEmployeesColectiveChange(String employeesColective);

	public abstract void onContractJourneyTypeChange(Boolean journeyType);

	public abstract void onContractPartialityChange(Double partialityCoef);

	public abstract void onContractJourneyDurationClick();

	public abstract void onEmployeeCnoSuggestionChange(String cno);

	public abstract void onUploadDni();

	// TABLA DATOS EMPLEADO

	public abstract void onEmployeeBirthDateChange(Date birthDate);

	public abstract void onEmployeeGenderChange(byte gender);

	public abstract void onEmployeeCivilStatusChange(byte civilStatus);

	public abstract void onEmployeeStreetTypeChange(String streetType);

	public abstract void onEmployeeAddressChange(String address);

	public abstract void onEmployeeAddressNumChange(String addressNum);

	public abstract void onEmployeeAddressInfoChange(String addressInfo);

	public abstract void onEmployeeAddressZipChange(String addressZip);

	public abstract void onEmployeeAddressProvinceChange(Integer geozoneId);

	public abstract void onEmployeeAddressMunicipalityChange(String cityName, String cityCode);

	public abstract void onEmployeeMobileChange(String mobile);

	public abstract void onEmployeePhoneChange(String phone);

	public abstract void onEmployeeEmailChange(String email);

	public abstract void onEmployeePayMethodChange(Integer payMethodId);

	public abstract void onEmployeeBICChange(String bic);

	public abstract void onEmployeeAccountChange(String account, String bankAlias, String bankSwift);

	// SHOW ERROR

	public abstract void fireError(String title, String message);

}
