package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.BankSwift;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Employee extends ResizeComposite {

	// ------------------------------------------------- UiBinder

	private static EmployeeUiBinder uiBinder = GWT.create(EmployeeUiBinder.class);

	interface EmployeeUiBinder extends UiBinder<Widget, Employee> {}

	// ------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String journeyDurationWarning();
		String warningTB();
		String flexGrow();
	}

	// TABLA DATOS CONTRATO
	@UiField
	HTMLPanel horizontalPanel;
	
	@UiField
	TableElement contractDataTable;
	
	@UiField
	Label document_type;
	
	@UiField
	HTMLPanel documentPanel;
	
	@UiField
	SuggestBox document;
	
	@UiField
	TableCellElement nationalityLabelCell;

	@UiField
	TableCellElement nationalityCell;

	@UiField(provided = true)
	SuggestBox nationality;
	
	@UiField
	HTMLPanel ssNumberPanel;
	
	@UiField
	SuggestBox security_social_num;
	
	@UiField
	HTMLPanel namePanel;

	@UiField
	SuggestBox name;

	@UiField
	Label firstSurnameLabel;

	@UiField
	SuggestBox first_surname;

	@UiField
	TextBox second_surname;
	
	@UiField
	ListBox ssRegimeType;
	
	@UiField
	HTMLPanel activityCCCPanel;

	@UiField
	ListBox activityCCC;
	
	@UiField
	ListBox mdCTZLB;
	
	@UiField
	HTMLPanel workplacePanel;
	
	@UiField
	ListBox workplace;
	
	@UiField
	HTMLPanel contractTypePanel;
	
	@UiField
	TableCellElement contractTypeNode;

	@UiField
	ListBox contractTypeLB;
	
	@UiField
	TableCellElement contractFreelancerNode;

	@UiField
	Label contractTypeFreelance;

	@UiField
	ListBox modality;
	
	@UiField
	HTMLPanel startDatePanel;

	@UiField
	DateBoxEx start_date;

	@UiField
	DateBoxEx end_date;

	@UiField
	Label seniority_date_label;
	
	@UiField
	HTMLPanel seniorityDatePanel;

	@UiField
	DateBoxEx seniority_date;
	
	@UiField
	ListBox agreement;

	@UiField
	ListBox level;

	@UiField
	TextBox category;

	@UiField
	ListBox quote_group;

	@UiField
	ListBox occupation;

	@UiField
	ListBox journeyType;
	
	@UiField
	DoubleBox partiality_coef;

	// TABLA DATOS EMPLEADO
	
	@UiField
	VerticalPanel employeeTablePanel;

	@UiField
	TableElement employeeDataTable;

	@UiField
	DateBoxEx birth_date;

	@UiField
	Label age;

	@UiField
	ListBox gender;
	
	@UiField
	ListBox civilStatus;
	
	@UiField
	ListBox street_type;

	@UiField
	SuggestBox address;

	@UiField
	TextBox addressNum;
	
	@UiField
	TextBox addressInfo;
	
	@UiField
	HTMLPanel addressZipPanel;

	@UiField
	TextBox addressZip;

	@UiField
	ListBox addressMunicipality;

	@UiField
	ListBox addressProvince;

	@UiField
	TextBox mobile;
	
	@UiField
	TextBox phone;

	@UiField
	TextBox email;

	@UiField
	ListBox payMethod;

	@UiField
	TextBox bic;
	
	@UiField
	HTMLPanel accountPanel;
	
	@UiField
	SuggestBox account;
	
	@UiField
	HTMLPanel journeyDuration;
	
	// ------------------------------------------------- Class variables
	
	private ContractType contractType;
	private Municipalities municipalities;
	
	private AonToolbarSmallButton clearEmployee;

	// ------------------------------------------------- Constructor

	public Employee() {
		//Initialize Nationality SuggestBox
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);
		this.nationality.setAutoSelectEnabled(true);
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		
		initializeView();
		addReformatAccount();
		
		clearEmployee = new AonToolbarSmallButton("Limpiar empleado", AON.CSS.aonIconRefresh());
		clearEmployee.addClickHandler(e -> {
			onClearEmployeeClick();
		});
		horizontalPanel.clear();
		horizontalPanel.add(clearEmployee);
		horizontalPanel.add(document_type);	
	}
	
	// ------------------------------------------------- UiHandlers
	
	// TABLA DATOS CONTRATO
	
	@UiHandler("document")
	void onDocumentChangeValue(SelectionEvent<Suggestion> event) {
		String document = this.document.getValue().trim();
		if(AonStringUtils.isNotBlank(document))
			onEmployeeDocumentSuggestionChange(document);
	}
	
	@UiHandler("document")
	void onDocumentChangeValue(ValueChangeEvent<String> event) {
		String document = this.document.getValue().trim();
		
		if(AonStringUtils.isNotBlank(document)) {
			
			String document_type = checkDocumentType(document);
			this.document_type.setText(document_type);
			
			if(checkDocumentValidation(document_type, document))
				addSuccessIconTB(documentPanel, this.document);
			else
				addWarningIcon(documentPanel, this.document, "El documento de identidad es err\u00F3neo");
				
			showNationality(document_type);	
			
			onEmployeeDocumentChange(document, document_type);
		} else {
			removeWarningIconTB(documentPanel, this.document);
		}	
	}
	
	@UiHandler("nationality")
	void onNationalityChangeValue(ValueChangeEvent<String> event) {
		String countryIso2 = getIso2(this.nationality.getValue());
		onEmployeeNationalityChange(countryIso2);
	}
	
	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(SelectionEvent<Suggestion> event) {
		String ssNum = this.security_social_num.getValue().trim();
		if(AonStringUtils.isNotBlank(ssNum))
			onEmployeeSSNumSuggestionChange(ssNum); 
	}
	
	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(ValueChangeEvent<String> event) {
		String ssNum = this.security_social_num.getValue().trim();
		if(AonStringUtils.isNotBlank(ssNum)) {
			if(checkSSNumValidation(ssNum))
				addSuccessIconTB(ssNumberPanel, this.security_social_num);
			else
				addWarningIcon(ssNumberPanel, this.security_social_num, "El numero es err\u00F3neo");
			
			onEmployeeSSNumChange(ssNum); 
		} else {
			removeWarningIconTB(ssNumberPanel, this.security_social_num);
		}
	}
	
	@UiHandler("name")
	void onNameChangeValue(SelectionEvent<Suggestion> event) {
		String nameSurname = this.name.getValue().trim();
		if(AonStringUtils.isNotBlank(nameSurname))
			onEmployeeNameSuggestionChange(nameSurname);
	}
	
	@UiHandler("name")
	void onNameChangeValue(ValueChangeEvent<String> event) {
		String name = this.name.getValue().trim();
		if(AonStringUtils.isNotBlank(name))
			onEmployeeNameChange(name);
	}
	
	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(SelectionEvent<Suggestion> event) {
		String nameSurname = this.first_surname.getValue().trim();
		if(AonStringUtils.isNotBlank(nameSurname))
			onEmployeeFirstSurnameSuggestionChange(nameSurname);
	}
	
	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(ValueChangeEvent<String> event) {
		String surname = this.first_surname.getValue().trim();
		if(AonStringUtils.isNotBlank(surname))
			onEmployeeFirstSurnameChange(surname);
	}
	
	@UiHandler("second_surname")
	void onSecondSurnameChangeValue(ChangeEvent event) {
		String secondSurname = this.second_surname.getValue().trim();
		if(AonStringUtils.isNotBlank(secondSurname))
			onEmployeeSecondSurnameChange(secondSurname);
	}
	
	@UiHandler("ssRegimeType")
	void onContractSSRegimenChangeValue(ChangeEvent event) {
		byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue()).byteValue();
		
		if(ssRegime == (byte)3){
			showElementsFreelancerTable();
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.journeyType);
		} else
			this.hideElementsFreelancerTable();
		
		onContractSSRegimenChange(ssRegime);
	}

	@UiHandler("activityCCC")
	void onContractActivityCCCChangeValue(ChangeEvent event) {
		String activityCCC = String.valueOf(this.activityCCC.getSelectedValue());
		
		if(AonStringUtils.equalsIgnoreCase(activityCCC, "-1"))
			onContractActiviesCCCChange(null);
		else {
			onContractActiviesCCCChange(activityCCC);
			
			Byte cccType = Byte.parseByte(activityCCC.split("/")[2]);
			if(cccType == (byte)7)
				showMdCtzContract();
			else
				hideMdCtzContract();
		}	
	}
	
	@UiHandler("mdCTZLB")
	void onContractMdCTZhangeValue(ChangeEvent event) {
		String mdCtz = String.valueOf(mdCTZLB.getSelectedValue());
		onContractMdCTZhange(mdCtz);
	}
	
	@UiHandler("workplace")
	void onContractWorkplaceChangeValue(ChangeEvent event) {
		Integer workplaceId = Integer.parseInt(this.workplace.getSelectedValue());
		onContractWorkplaceChange(workplaceId);
	}
	
	@UiHandler("contractTypeLB")
	void onContractTypeChangeValue(ChangeEvent event) {
		String contractType = String.valueOf(this.contractTypeLB.getSelectedValue());
		
		if(AonStringUtils.equalsIgnoreCase(contractType, "-1"))
			onContractTypeChange(null);
		else {
			Integer contractTypeInt = Integer.parseInt(contractType);
			if(AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0))
				showPartialTimeContract();
			else
				showElementsFullTimeContract();
			
			updateModality(contractTypeInt);
			
			onContractTypeChange(contractType);
		}
	}

	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		Integer contractModel = Integer.valueOf(this.modality.getSelectedValue());
		
		if(AonNumberUtils.equals(contractModel, -1))
			onContractModalityChange(null);
		else 
			onContractModalityChange(contractModel);
	}

	@UiHandler("start_date")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		Date startDate = this.start_date.getValue();
		onContractStartDateChange(startDate);
		
		if(null != startDate)
			this.seniority_date.setValue(startDate, true);
		
	}

	@UiHandler("end_date")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		Date endDate = this.end_date.getValue();
		onContractEndDateChange(endDate);
	}

	@UiHandler("seniority_date")
	void onSeniorityDateChangeValue(ValueChangeEvent<Date> event) {
		Date startDate = this.start_date.getValue();
		Date seniorityDate = this.seniority_date.getValue();
		
		if(null == startDate)
			addInfoIcon(seniorityDatePanel, this.seniority_date, "La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
		else if(null != seniorityDate) {
			DateUtils.resetTime(startDate);
			DateUtils.resetTime(seniorityDate);
			
			if(DateUtils.equals(startDate, seniorityDate))
				removeInfoIcon(seniorityDatePanel, this.seniority_date);
			else
				addInfoIcon(seniorityDatePanel, this.seniority_date, "La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
		} else
			removeInfoIcon(seniorityDatePanel, this.seniority_date);
		
		onContractSeniorityDateChange(seniorityDate);
	}

	@UiHandler("agreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		String agreementValue = String.valueOf(this.agreement.getSelectedValue());
		
		if(AonStringUtils.equalsIgnoreCase(agreementValue, "-1")) {
			this.level.clear();
			this.category.setValue("");
			onContractAgreementChange(null, null);
		} else {
			Integer agreementId = Integer.valueOf(this.agreement.getSelectedValue().split("/")[0]); 
			String ssNumber = this.agreement.getSelectedValue().split("/")[1];
			onContractAgreementChange(agreementId, ssNumber);
		}
	}

	@UiHandler("level")
	void onContractAgreementLevelChangeValue(ChangeEvent event) {
		Integer agreementLevelId = Integer.parseInt(this.level.getSelectedValue());
		String levelDescription = this.level.getSelectedItemText().split("- ")[1];
		this.category.setValue(levelDescription);
		onContractAgreementLevelChange(agreementLevelId);
		onContractCategoryChange(levelDescription);
	}

	@UiHandler("category")
	void onCategoryChangeValue(ChangeEvent event) {
		String category = this.category.getValue();
		if(AonStringUtils.isNotBlank(category))
			onContractCategoryChange(category);
	}

	@UiHandler("quote_group")
	void onQuoteGroupChangeValue(ChangeEvent event) {
		String quoteGroup = String.valueOf(this.quote_group.getSelectedValue());
		if(AonStringUtils.equalsIgnoreCase(quoteGroup, "-1"))
			onContractQuoteGroupChange(null);
		else
			onContractQuoteGroupChange(quoteGroup);
	}

	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		String occupation = String.valueOf(this.occupation.getSelectedValue());
		if(AonStringUtils.equalsIgnoreCase(occupation, "-1"))
			onContractOccupationChange(null);
		else
			onContractOccupationChange(occupation);
	}
	
	@UiHandler("journeyType")
	void onContractJourneyTypeChangeValue(ChangeEvent event) {
		Boolean journey_type = Boolean.valueOf(this.journeyType.getSelectedValue());
		if(journey_type)
			showElementsFullTimeContract();
		else
			showPartialTimeContract();
		
		onContractJourneyTypeChange(journey_type);
	}
	
	@UiHandler("partiality_coef")
	void onContractPartialityCoefChangeValue(ValueChangeEvent<Double> event) {
		Double partialityCoef = this.partiality_coef.getValue();
		onContractPartialityChange(partialityCoef);
	}
	
	// TABLA DATOS EMPLEADO
	
	@UiHandler("birth_date")
	void onBithDateChangeValue(ValueChangeEvent<Date> event) {
		Date birthDate = this.birth_date.getValue();
		
		if(null != birthDate) {
			Date actualDay = new Date();
			Integer age = getYears(actualDay, birthDate);
			this.age.setText("( " + (age) + " a" + String.valueOf("\u00F1") + "os )");
		} else
			this.age.setText("");
		
		onEmployeeBirthDateChange(birthDate);
	}

	@UiHandler("gender")
	void onGenderChangeValue(ChangeEvent event) {
		byte gender = Byte.valueOf(this.gender.getSelectedValue()).byteValue();
		onEmployeeGenderChange(gender);
	}
	
	@UiHandler("civilStatus")
	void onCivilStatusChangeValue(ChangeEvent event) {
		byte civilStatus = Byte.valueOf(this.civilStatus.getSelectedValue()).byteValue();
		onEmployeeCivilStatusChange(civilStatus);
	}
	
	@UiHandler("street_type")
	void onStreetTypeChangeValue(ChangeEvent event) {
		String streetType = String.valueOf(this.street_type.getSelectedValue());
		onEmployeeStreetTypeChange(streetType);
	}

	@UiHandler("address")
	void onAddressChangeValue(ValueChangeEvent<String> event) {
		String address = this.address.getValue();
		onEmployeeAddressChange(address);
	}

	@UiHandler("addressNum")
	void onAddressNumChangeValue(ChangeEvent event) {
		String addressNum = this.addressNum.getValue();
		onEmployeeAddressNumChange(addressNum);
	}
	
	@UiHandler("addressInfo")
	void onAddressInfoChangeValue(ChangeEvent event) {
		String addressInfo = this.addressInfo.getValue();
		onEmployeeAddressInfoChange(addressInfo);
	}

	@UiHandler("addressZip")
	void onAddressZipChangeValue(ChangeEvent event) {
		String addressZip = this.addressZip.getValue();
		onEmployeeAddressZipChange(addressZip);
		
		if(addressZip.length() >= 2) {
			String zip = this.addressZip.getValue().substring(0, 2);
			setSelectedValueLB(addressProvince, zip); 
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
		}
	}
	
	@UiHandler("addressProvince")
	void onAddressProvinceChangeValue(ChangeEvent event) {
		String addressProvinceCode = String.valueOf(this.addressProvince.getSelectedValue());
		updateMunicipalities();
		onEmployeeAddressProvinceChange(addressProvinceCode);
	}

	@UiHandler("addressMunicipality")
	void onAddressMunicipalityChangeValue(ChangeEvent event) {
		String addressMunicipality = municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString();
		onEmployeeAddressMunicipalityChange(addressMunicipality);
	}

	@UiHandler("mobile")
	void onMobileChangeValue(ChangeEvent event) {
		String mobile = this.mobile.getValue();
		onEmployeeMobileChange(mobile);
	}
	
	@UiHandler("phone")
	void onPhoneChangeValue(ChangeEvent event) {
		String phone = this.phone.getValue();
		onEmployeePhoneChange(phone);
	}

	@UiHandler("email")
	void onEmailChangeValue(ChangeEvent event) {
		String email = this.email.getValue();
		onEmployeeEmailChange(email);
	}

	@UiHandler("payMethod")
	void onPayMethodChangeValue(ChangeEvent event) {
		Integer payMethodId = Integer.parseInt(this.payMethod.getSelectedValue());
		if(AonNumberUtils.equals(payMethodId, -1))
			onEmployeePayMethodChange(null);
		else
			onEmployeePayMethodChange(payMethodId);
	}

	@UiHandler("bic")
	void onBIClChangeValue(ChangeEvent event) {
		String bic = this.bic.getValue();
		onEmployeeBICChange(bic);
	}
	
	@UiHandler("account")
	void onAccountChangeValue(ValueChangeEvent<String> event) {
		String account = this.account.getValue();
		account = account.replaceAll("\\W+", "");
		
		if(account.length() > 0)
			if(Iban.validateIBAN(account))
				addSuccessIconTB(accountPanel, this.account);
			else
				addWarningIcon(accountPanel, this.account, "IBAN no valido");
		
		String bankAlias = getBankAlias(account);
		String bankSwift = getBankSwift(account);
		this.bic.setValue(bankSwift);
		onEmployeeAccountChange(account, bankAlias, bankSwift);
	}

	// ------------------------------------------------- Abstract methods
	
	// TABLA DATOS CONTRATO
	
	public abstract void onClearEmployeeClick();
	public abstract void onEmployeeDocumentSuggestionChange(String document);
	public abstract void onEmployeeDocumentChange(String document, String document_type);
	public abstract void onEmployeeNationalityChange(String countryIso2);
	public abstract void onEmployeeSSNumSuggestionChange(String ssNumber);
	public abstract void onEmployeeSSNumChange(String ssNumber);
	public abstract void onEmployeeNameSuggestionChange(String nameSurname);
	public abstract void onEmployeeNameChange(String name);
	public abstract void onEmployeeFirstSurnameSuggestionChange(String nameSurname);
	public abstract void onEmployeeFirstSurnameChange(String surname);
	public abstract void onEmployeeSecondSurnameChange(String secondSurname);
	public abstract void onContractSSRegimenChange(byte ssRegime);
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
	public abstract void onContractOccupationChange(String occupation);
	public abstract void onContractJourneyTypeChange(Boolean journey_type);
	public abstract void onContractPartialityChange(Double partialityCoef);
	public abstract void onContractJourneyDurationClick();
	
	// TABLA DATOS EMPLEADO
	
	public abstract void onEmployeeBirthDateChange(Date birthDate);
	public abstract void onEmployeeGenderChange(byte gender);
	public abstract void onEmployeeCivilStatusChange(byte civilStatus);
	public abstract void onEmployeeStreetTypeChange(String streetType);
	public abstract void onEmployeeAddressChange(String address);
	public abstract void onEmployeeAddressNumChange(String addressNum);
	public abstract void onEmployeeAddressInfoChange(String addressInfo);
	public abstract void onEmployeeAddressZipChange(String addressZip);
	public abstract void onEmployeeAddressProvinceChange(String addressProvinceCode);
	public abstract void onEmployeeAddressMunicipalityChange(String addressMunicipality);
	public abstract void onEmployeeMobileChange(String mobile);
	public abstract void onEmployeePhoneChange(String phone);
	public abstract void onEmployeeEmailChange(String email);
	public abstract void onEmployeePayMethodChange(Integer payMethodId);
	public abstract void onEmployeeBICChange(String bic);
	public abstract void onEmployeeAccountChange(String account, String bankAlias, String bankSwift);

	// ------------------------------------------------- Methods preview
	
	public void initializeView() {
		resetElements();
		initializeListBox();
		initDisplayElements();
	}

	private void resetElements() {
		
		// TABLA DATOS CONTRATO
		
		this.document.setValue("");
		this.nationality.setValue("");
		this.security_social_num.setValue("");
		this.name.setValue("");
		this.first_surname.setValue("");
		this.second_surname.setValue("");
		this.ssRegimeType.clear();
		this.activityCCC.clear();
		this.mdCTZLB.clear();
		this.workplace.clear();
		this.contractTypeLB.clear();
		this.modality.clear();
		this.start_date.setValue(null);
		this.end_date.setValue(null);
		this.seniority_date.setValue(null);
		this.agreement.clear();
		this.level.clear();
		this.category.setValue("");
		this.quote_group.clear();
		this.occupation.clear();
		this.journeyType.clear();
		this.partiality_coef.setValue(null);
		this.journeyDuration.clear();

		// TABLA DATOS EMPLEADO
		
		this.birth_date.setValue(null);
		this.gender.clear();
		this.civilStatus.clear();
		this.street_type.clear();
		this.address.setValue("");
		this.addressNum.setValue("");
		this.addressInfo.setValue("");
		this.addressZip.setValue("");
		this.addressProvince.clear();
		this.addressMunicipality.clear();
		this.mobile.setValue("");
		this.phone.setValue("");
		this.email.setValue("");
		this.payMethod.clear();
		this.account.setValue("");
		this.bic.setValue("");
	}
	
	private void initializeListBox() {
		// TABLA DATOS CONTRATO
		
		// TIPO DE COTIZACIÓN
		this.ssRegimeType.addItem("COMUN", "0");
		this.ssRegimeType.addItem("RETA", "3");
		this.ssRegimeType.addItem("SOCIOS COOP", "1");
		this.ssRegimeType.addItem("JUBILACION ACTIVA", "2");
		this.ssRegimeType.addItem("GARANTIA JUVENIL", "4");
		
		// MODALIDAD DE COTIZACION
		this.mdCTZLB.addItem("-", "-1");
		this.mdCTZLB.addItem("Cotizaci" + String.valueOf("\u00F3") + "n mensual", "1");
		this.mdCTZLB.addItem("Jornadas reales", "2");
		
		// MODALIDAD
		this.modality.addItem("-", "-1");

		// GRUPO DE COTIZACION
		this.quote_group.addItem("-", "-1");
		this.quote_group.addItem("01. Alta direcci" + String.valueOf("\u00F3") + "n y personal no incluido en el E.T.", "01");
		this.quote_group.addItem("02. Ingenieros t" + String.valueOf("\u00E9") + "cnicos, peritos y ayudantes titulados", "02");
		this.quote_group.addItem("03. Jefes administrativos y de taller", "03");
		this.quote_group.addItem("04. Ayudantes no titulados", "04");
		this.quote_group.addItem("05. Oficiales administrativos", "05");
		this.quote_group.addItem("06. Subalternos", "06");
		this.quote_group.addItem("07. Axiliares administrativos", "07");
		this.quote_group.addItem("08. Oficiales de primera y segunda", "08");
		this.quote_group.addItem("09. Oficiales de tercera y especialista", "09");
		this.quote_group.addItem("10. Peones", "10");
		this.quote_group.addItem("11. Trabajadores menos de dieciocho a" + String.valueOf("\u00F1") + "os", "11");

		// OCUPACION
		this.occupation.addItem("-", "-1");
		this.occupation.addItem("a. Personal en trabajos exclusivos de oficina", "a");
		this.occupation.addItem("b. Tipo de cotizaci" + String.valueOf("\u00F3") + "n para todos los trabajadores que deban desplazarse habitalmente", "b");
		this.occupation.addItem("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci" + String.valueOf("\u00F3") + "n en general", "d");
		this.occupation.addItem("e. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de pasajeros en general (taxis, autom" + String.valueOf("\u00F3") + "viles, autobuses, etc)", "e");
		this.occupation.addItem("f. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de mercanc" + String.valueOf("\u00ED") + "as que tengan una capacidad de carga " + String.valueOf("\u00FA") + "til superior a 3,5 Tm.", "f");
		this.occupation.addItem("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles", "g");
		this.occupation.addItem("h. Vigilantes, guardas, guardas jurados y personal de seguridad", "h");

		// TIPO DE JORNADA
		this.journeyType.addItem("Tiempo Completo", "true");
		this.journeyType.addItem("Tiempo Parcial", "false");
		
		// TABLA DATOS EMPLEADO
		
		// SEXO
		this.gender.addItem("Hombre", "0");
		this.gender.addItem("Mujer", "1");
		this.gender.addItem("Desconocido", "2");
		
		//CIVIL STATUS
		this.civilStatus.addItem("SOLTERO", "0");
		this.civilStatus.addItem("CASADO", "1");
		this.civilStatus.addItem("DIVORCIADO", "2");
		this.civilStatus.addItem("SEPARADO", "3");
		this.civilStatus.addItem("VIUDO", "4");
		this.civilStatus.addItem("DESCONOCIDO", "5");
		
		//TIPO DE VIA
		for(int i=0; i<StreetType.values().length; i++){
			this.street_type.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getShortCode());
		}
		
		//PROVINCIA
		this.addressProvince.addItem("-", "-1");
		for(Entry<String, String> provinces : ProvinceContract.getProvinces().entrySet())
			this.addressProvince.addItem(provinces.getValue(), provinces.getKey());

	}
	
	private void initDisplayElements() {
		this.contractDataTable.getRows().getItem(4).getStyle().clearDisplay();
		
		this.contractTypeNode.getStyle().clearDisplay();
		this.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(5).getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(8).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(12).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(13).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(15).getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------------- Fill default fields
	
	public void fillDefaultFields() {
		//SS REGIME
		ssRegimeType.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), ssRegimeType);
		
		//GENDER
		gender.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gender);
		
		//CIVIL STATUS
		civilStatus.setSelectedIndex(5);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), civilStatus);
		
		//STREET_TYPE
		street_type.setSelectedIndex(14); //Calle
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), street_type);
	}
	
	// ------------------------------------------------- Initialize SuggestBox
	
	public void initSuggestBox(WorkplaceEmployees workplaceEmployees) {
		//DOCUMENT
		List<String> employeesDocuments = workplaceEmployees.getWorkplaceEmployeesDocument();
		List<String> employeesDocumentsSuggest = new ArrayList<String>();
		for(String document : employeesDocuments)
			employeesDocumentsSuggest.add(document+"");
		MultiWordSuggestOracle orclDocuments = (MultiWordSuggestOracle) document.getSuggestOracle();
		orclDocuments.addAll(employeesDocumentsSuggest);
		document.setAutoSelectEnabled(false);
		
		//SS_NUMBER
		List<String> employeesSSNumbers = workplaceEmployees.getWorkplaceEmployeesSSNumber();
		List<String> employeesSSNumbersSuggest = new ArrayList<String>();
		for(String ssNumber : employeesSSNumbers)
			employeesSSNumbersSuggest.add(ssNumber+"");
		MultiWordSuggestOracle orclSSNumbers = (MultiWordSuggestOracle) security_social_num.getSuggestOracle();
		orclSSNumbers.addAll(employeesSSNumbersSuggest);
		security_social_num.setAutoSelectEnabled(false);
		
		//NAMES
		List<String> employeesNames = workplaceEmployees.getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) name.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		name.setAutoSelectEnabled(false);
		
		//SURNAME
		List<String> employeesSurNames = workplaceEmployees.getWorkplaceEmployeesSurName();
		List<String> employeesSurNamesSuggest = new ArrayList<String>();
		for(String surName : employeesSurNames)
			employeesSurNamesSuggest.add(surName+"");
		MultiWordSuggestOracle orclSurNames = (MultiWordSuggestOracle) first_surname.getSuggestOracle();
		orclSurNames.addAll(employeesSurNamesSuggest);
		first_surname.setAutoSelectEnabled(false);
	}

	public void initActivitiesCCC(Map<Integer, String> activities, Map<Integer, CCCInfo> cccs) {
		//ACTIVITY - CCC
		activityCCC.addItem("-", "-1");
		if(null != activities)
		for(Entry<Integer,String> entry : activities.entrySet())
			for(CCCInfo cccInfo :  cccs.values())
				if(cccInfo.getActivityId() == entry.getKey())
					activityCCC.addItem(entry.getValue() + " - " + getCCCType(cccInfo.getType()) + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
	}
	
	public void initWorkplaces(List<Workplace> workplaces) {
		//WORKPLACE
		workplace.addItem("-", "-1");
		for(Workplace workplaceInfo : workplaces)
			workplace.addItem(workplaceInfo.getDescription(), workplaceInfo.getId().toString());
	}

	public void initContractType() {
		// TIPO DE CONTRATO
		contractTypeLB.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			contractTypeLB.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), entry.getKey().toString());		
	}

	public void initAgreements(List<Agreement> activeAgreements) {
		// CONVENIO
		agreement.addItem("-", "-1");
		for (Agreement agreementInfo : activeAgreements)
			agreement.addItem(agreementInfo.getDescription(), String.valueOf(agreementInfo.getId()) + "/" + agreementInfo.getSSNumber());	
	}

	public void initPayMethods(Map<String, String> payMethods) {
		// PAY METHODS
		payMethod.addItem("-", "-1");
		for(Entry<String, String> entry : payMethods.entrySet()) {
			payMethod.addItem(entry.getKey(), entry.getValue());
		}
	}
	
	// ------------------------------------------------- Initialize SuggestBox (Auxiliar methods)
	
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
		employeeTablePanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------------- Show/hide clearEmployee

	public void hideClearEmployee() {
		clearEmployee.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	public void showClearEmployee() {
		clearEmployee.getElement().getStyle().clearDisplay();
	}
	
	// ------------------------------------------------- Show/hide methods freelancer

	public void showElementsFreelancerTable() {
		this.contractDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		
		this.contractTypeNode.getStyle().setDisplay(Display.NONE);
		this.contractFreelancerNode.getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(5).getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(14).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(15).getStyle().setDisplay(Display.NONE);
	}
	
	public void hideElementsFreelancerTable() {
		this.contractDataTable.getRows().getItem(4).getStyle().clearDisplay();
		
		this.contractTypeNode.getStyle().clearDisplay();
		this.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(5).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(8).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(12).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(13).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(15).getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------------- Show/hide methods partial/full time
	
	public void showElementsFullTimeContract() {
		this.contractDataTable.getRows().getItem(15).getStyle().setDisplay(Display.NONE);
	}
	
	public void showPartialTimeContract() {
		showElementsPartialTimeContract();
		journeyDuration.clear();
		AonToolbarSmallButton calendarBtn = new AonToolbarSmallButton("Abrir calendario", AON.CSS.aonIconEditCalendar());
		Label message = new Label("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		journeyDuration.add(calendarBtn);
		journeyDuration.add(message);
		journeyDuration.setTitle("Las horas se deben definir en el calendario del empleado.");
		
		calendarBtn.addClickHandler(e -> {
			onContractJourneyDurationClick();
		});
		
		message.addClickHandler(e -> {
			onContractJourneyDurationClick();
		});
	}
	
	private void showElementsPartialTimeContract() {
		this.contractDataTable.getRows().getItem(15).getStyle().clearDisplay();	
	}
	
	// ------------------------------------------------- Show/hide mdCtz methods
	
	public void showMdCtzContract() {
		this.contractDataTable.getRows().getItem(5).getStyle().clearDisplay();	
	}
	
	public void hideMdCtzContract() {
		this.contractDataTable.getRows().getItem(5).getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------------- CheckStatus(EmployeeDraftObject) - EmployeeTree

	public void setEndDate(Date endDate) {
		end_date.setValue(endDate, false);
		onContractEndDateChange(endDate);
	}
	
	public void setStartDate(Date endDate) {
		start_date.setValue(endDate, false);
		onContractStartDateChange(endDate);
	}

	public void setOcupation(String str) {
		switch (str) {
		case "a":
			occupation.setSelectedIndex(1);
		case "b":
			occupation.setSelectedIndex(2);
		case "d":
			occupation.setSelectedIndex(3);
		case "e":
			occupation.setSelectedIndex(4);
		case "f":
			occupation.setSelectedIndex(5);
		case "g":
			occupation.setSelectedIndex(6);
		case "h":
			occupation.setSelectedIndex(7);
		default:
			occupation.setSelectedIndex(0);
		}
		onContractOccupationChange(str);
		
	}
	
	// ------------------------------------------------- Account methods

	private void addReformatAccount() {
		account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reformatAccount(account);
			}
		});
	}
	
	public void reformatAccount(SuggestBox accountField) {
	    String accountText = accountField.getText();
	    accountText = accountText.replaceAll("\\W+", "");
	    if (accountText.length() >= 24) {
	    	accountField.setText(accountText.substring(0, 4) + "  " + accountText.substring(4, 8) + "  " + accountText.substring(8, 12) + "  " + accountText.substring(12, 16)
	    	+ "  " + accountText.substring(16, 20) + "  " + accountText.substring(20, 24));
	    }
	}
	
	public void initIbans(ArrayList<String> employeeIbans) {
		List<String> employeesIbanSuggest = new ArrayList<String>();
		for(String iban : employeeIbans)
			employeesIbanSuggest.add(iban);
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) account.getSuggestOracle();
		orclIbans.addAll(employeesIbanSuggest);
		account.setAutoSelectEnabled(false);
	}

	private String getBankSwift(String account) {
		if(AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return bankSwiftEntry.getSwift();
		}
		return null;
	}

	private String getBankAlias(String account) {
		if(AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return bankSwiftEntry.getBankName();
		}
		return null;
	}
	
	// ------------------------------------------------- Employee table methods
	
	public void resetEmployeeInfo() {
		document.setValue(null);
		nationality.setValue(null);
		security_social_num.setValue(null);
		name.setValue(null);
		first_surname.setValue(null);
		second_surname.setValue(null);
		
		birth_date.setValue(null);
		gender.setSelectedIndex(0);
		
		setSelectedValueLB(street_type, "CL");
		address.setValue(null);
		addressNum.setValue(null);
		addressZip.setValue(null);
		addressProvince.setSelectedIndex(0);
		addressMunicipality.clear();
		
		mobile.setValue(null);
		phone.setValue(null);
		email.setValue(null);
		
		payMethod.setSelectedIndex(0);
		account.setValue(null);
		bic.setValue(null);
	}
	
	public void blockVariablesExistingContract(){
		String documentStr = this.document.getValue().trim();
		if(AonStringUtils.isNotBlank(documentStr)) {
			String document_type = checkDocumentType(documentStr);
			document.setEnabled(!checkDocumentValidation(document_type, documentStr));
		} else
			document.setEnabled(true);
		
		nationality.setEnabled(false);
		
		String ssNum = this.security_social_num.getValue().trim();
		if(AonStringUtils.isNotBlank(ssNum))
			security_social_num.setEnabled(!checkSSNumValidation(ssNum));
		else
			security_social_num.setEnabled(false);
	}
	
	public void unblockVariablesExistingContract(){
		document.setEnabled(true);
		nationality.setEnabled(true);
		security_social_num.setEnabled(true);
	}
	
	// ------------------------------------------------- Auxiliar methods
	
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
	
	public String checkDocumentType(String document) {
		
		if(null == document)
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
	
	public boolean checkDocumentValidation(String document_type_string, String document_string) {
		if("DNI".equals(document_type_string)){
			Dni dni = new Dni(document_string);
			if(dni.checkDNI())
				return true;
			else
				return false;
		}else if("" == document_string) {
			return true;
		}else
			return true;
	}
	
	public void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			nationalityLabelCell.getStyle().clearDisplay();
			nationalityCell.getStyle().clearDisplay();
		} else {
			nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			nationalityCell.getStyle().setDisplay(Display.NONE);
			nationality.setValue("ESPA" + String.valueOf("\u00D1") + "A");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), nationality);
		}
	}
	
	public boolean checkSSNumValidation(String ssNum_string) {
		if(null == ssNum_string)
			return false;
		
		SocialSecurity ss = new SocialSecurity(ssNum_string);
		if(ss.checkSS())
			return true;
		else
			return false;
	}
	
	public void updateModality(Integer contractTypeInt) {
		this.modality.clear();
		this.modality.addItem("-", "-1");
		
		List<ModelRecord> contractTypeModels = this.contractType.getModelsContractType(contractTypeInt);
		for (ModelRecord model : contractTypeModels)
			this.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
	}
	
	private String getIso2(String country) {
		for (int i = 0; i < Country.values().length; i++)
			if (Country.values()[i].getName() == country)
				return Country.values()[i].getIso2();
		
		return null;
	}
	
	public void updateMunicipalities() {
		String provinceCode = addressProvince.getSelectedValue();
		addressMunicipality.clear();
		addressMunicipality.addItem("-" , "-1");;
		HashMap<String, String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.entrySet().forEach(e -> {addressMunicipality.addItem(e.getValue(), e.getKey());});
	}
	
	private Integer getYears(Date actualDay, Date birthDate) {
		DateUtils.resetTime(actualDay);
		DateUtils.resetTime(birthDate);
		
		DateTimeFormat formatter = DateTimeFormat.getFormat("yyyyMMdd");                         
	    int d1 = Integer.parseInt(formatter.format(birthDate));                            
	    int d2 = Integer.parseInt(formatter.format(actualDay));                          
	    int age = (d2 - d1) / 10000;                                                       
	    return age;                   
	}
	
	// ------------------------------------------------- Save methods
	
	public boolean checkIfSaveEmployeeIsPossible() {
		return checkIfNewEmployeeIsPossible() && checkAddress();
	}
	
	public boolean checkIfNewEmployeeIsPossible() {
		cleanWarningIcons();
		
		if(checkIfSaveIsPossible())
			if(checkDates())
				return true;	
			else 
				return false;
		else 	
			return false;
		
	}
	
	private boolean checkIfSaveIsPossible() {
		Byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue());
		
		if(ssRegime == (byte) 3) { // RETA
			boolean isNotNameBlank = isNotNameBlank();
			boolean isWokplaceSelected = isWokplaceSelected();
			
			return 	isNotNameBlank && isWokplaceSelected;
		} else {
			boolean isNotNameBlank = isNotNameBlank();
			boolean isWokplaceSelected = isWokplaceSelected();
			boolean isActivityCCCSelected = isActivityCCCSelected();
			boolean isContractTypeSelected = isContractTypeSelected();
			
			return  isNotNameBlank && isWokplaceSelected && isActivityCCCSelected && isContractTypeSelected;
		}
	}
	
	// ------------------------------------------------- SaveMethods.checkIfSaveIsPossible
	
	private boolean isNotNameBlank() {
		String nameValue = name.getValue();
		if(AonStringUtils.isBlank(nameValue)) {
			addWarningIcon(namePanel, name, null);
			return false;
		} else
			return true;
	}
	
	private boolean isWokplaceSelected() {
		String workplaceValue = workplace.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(workplaceValue, "-1")) {
			addWarningIcon(workplacePanel, workplace, null);
			return false;
		} else
			return true;
	}
	
	private boolean isActivityCCCSelected() {
		String activityValue = activityCCC.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(activityValue, "-1")) {
			addWarningIcon(activityCCCPanel, activityCCC, null);
			return false;
		} else
			return true;
	}
	
	private boolean isContractTypeSelected() {
		removeWarningIconLB(contractTypePanel, contractTypeLB);
		
		String contractTypeValue = contractTypeLB.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(contractTypeValue, "-1")) {
			addWarningIcon(contractTypePanel, contractTypeLB, null);
			return false;
		} else
			return true;
	}
	
	private boolean checkDates() {
		Date startDate = null == start_date.getValue() ? null : DateUtils.copyDateOnly(start_date.getValue());
		Date endDate = null == end_date.getValue() ? null : DateUtils.copyDateOnly(end_date.getValue());
		
		if(null == startDate) {
			addWarningIcon(startDatePanel, start_date, "La fecha de inicio no puede estar sin definir.");
			return false;
		}
		
		if(null == endDate || endDate.after(startDate) || endDate.equals(startDate))
			return true;
		else {
			addWarningIcon(startDatePanel, start_date, "La fecha de inicio no puede ser posterior a la fecha de fin.");
			return false;
		}

	}
	
	// ------------------------------------------------- SaveMethods.checkAddress
	
	private boolean checkAddress() {
		String addressZipValue = addressZip.getValue();
		String addressProvinceValue = addressProvince.getSelectedValue();
		String addressMunicipalityValue = addressMunicipality.getSelectedValue();
		
		if(AonStringUtils.isNotBlank(addressZipValue) || !AonStringUtils.equalsIgnoreCase(addressProvinceValue, "-1") || !AonStringUtils.equalsIgnoreCase(addressMunicipalityValue, "-1")) {
			if(AonStringUtils.isNotBlank(addressZipValue) && !AonStringUtils.equalsIgnoreCase(addressProvinceValue, "-1") && !AonStringUtils.equalsIgnoreCase(addressMunicipalityValue, "-1"))
				return true;
			else {	
				addWarningIcon(addressZipPanel, addressZip, "Si rellena la direccion del trabajador, debera rellenar los campos azules correcta y obligatoriamente.");
				return false;
			}
		} else
			return true;
	}
	
	// ------------------------------------------------- journeyDuration.Methods
	
	public void createJourneyDurationWarning(){
		journeyDuration.clear();
		AonToolbarSmallButton calendarBtn = new AonToolbarSmallButton("Abrir calendario", AON.CSS.aonIconEditCalendar());
		Label message = new Label("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		journeyDuration.add(calendarBtn);
		journeyDuration.add(message);
		journeyDuration.setTitle("Las horas se deben definir en el calendario del empleado.");
		
		calendarBtn.addClickHandler(e -> {
			onContractJourneyDurationClick();
		});
		
		message.addClickHandler(e -> {
			onContractJourneyDurationClick();
		});
	}
	
	public void createJourneyDurationInfo(String messageStr){
		journeyDuration.clear();
		Label message = new Label(messageStr);
		journeyDuration.add(message);
		
		message.addClickHandler(e -> {
			onContractJourneyDurationClick();
		});
	}
	
	// ------------------------------------------------- Add and remove styles
	
	private void addWarningIcon(HTMLPanel panel, Widget widget, String message) {
		panel.clear();
		panel.add(widget);
		message = AonStringUtils.isBlank(message) ? "Este campo es obligatorio" : message;
		panel.add(new AonToolbarSmallButton(message, AON.CSS.aonIconWarning()));
		widget.addStyleName(style.warningTB());
		widget.addStyleName(style.flexGrow());
	}

	private void removeWarningIconTB(HTMLPanel panel, Widget widget) {
		panel.clear();
		panel.add(widget);
		widget.addStyleName("aon-inputText");
		widget.getElement().getStyle().setWidth(99, Unit.PCT);
		widget.removeStyleName(style.warningTB());
	}
	
	private void removeWarningIconLB(HTMLPanel panel, Widget widget) {
		panel.clear();
		panel.add(widget);
		widget.addStyleName("aon-selectOneMenu");
		widget.getElement().getStyle().setWidth(100, Unit.PCT);
		widget.removeStyleName(style.warningTB());
	}
	
	private void removeWarningIconAddressTB(HTMLPanel panel, Widget widget) {
		panel.clear();
		panel.add(widget);
		widget.addStyleName("aon-inputText");
		widget.getElement().getStyle().setWidth(97, Unit.PCT);
		widget.removeStyleName(style.warningTB());
	}
	
	private void addSuccessIconTB(HTMLPanel panel, Widget widget) {
		panel.clear();
		panel.add(widget);
		panel.add(new AonToolbarSmallButton("", AON.CSS.aonIconAccept()));
		widget.addStyleName(style.flexGrow());
		widget.removeStyleName(style.warningTB());
	}
	
	private void addInfoIcon(HTMLPanel panel, Widget widget, String message) {
		panel.clear();
		panel.add(widget);
		message = AonStringUtils.isBlank(message) ? "Info" : message;
		panel.add(new AonToolbarSmallButton(message, AON.CSS.aonIconInfo()));
		widget.addStyleName(style.flexGrow());
	}
	
	private void removeInfoIcon(HTMLPanel panel, Widget widget) {
		panel.clear();
		panel.add(widget);
		widget.addStyleName("rich-calendar-input aon-selectInputDate-inputClass");
		widget.getElement().getStyle().setWidth(96, Unit.PCT);
		widget.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		widget.removeStyleName(style.warningTB());
	}

	// ------------------------------------------------- cleanWarningIcons
	
	public void cleanWarningIcons() {
		removeWarningIconTB(namePanel, name);
		removeWarningIconLB(workplacePanel, workplace);
		removeWarningIconLB(activityCCCPanel, activityCCC);
		removeWarningIconLB(contractTypePanel, contractTypeLB);
		removeWarningIconTB(startDatePanel, start_date);
		removeWarningIconAddressTB(addressZipPanel, addressZip);
	}
	
}
