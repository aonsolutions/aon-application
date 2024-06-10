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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public abstract class Employee extends ResizeComposite {

	// ------------------------------------------------- UiBinder

	private static EmployeeUiBinder uiBinder = GWT.create(EmployeeUiBinder.class);

	interface EmployeeUiBinder extends UiBinder<Widget, Employee> {
	}

	// ------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String journeyDurationWarning();

		String warningTB();

		String errorBorder();
		
		String uploaded();
		
		String uploadedFail();
	}

	// TABLA DATOS CONTRATO

	@UiField
	HTMLPanel contractDataTable;

	@UiField
	Label documentType;

	@UiField
	SuggestBox document;

	@UiField
	HTMLPanel nationalityPanel;

	@UiField(provided = true)
	SuggestBox nationality;

	@UiField
	SuggestBox securitySocialNum;

	@UiField
	SuggestBox name;

	@UiField
	Label firstSurnameLabel;

	@UiField
	SuggestBox firstSurname;

	@UiField
	TextBox secondSurname;

	@UiField
	ListBox ssRegimeType;

	@UiField
	ListBox mdTBTLB;

	@UiField
	ListBox activityCCC;

	@UiField
	ListBox mdCTZLB;

	@UiField
	ListBox workplace;

	@UiField
	ListBox contractTypeLB;

	@UiField
	Label contractTypeFreelance;

	@UiField
	ListBox modality;

	@UiField
	DateBoxEx startDate;

	@UiField
	DateBoxEx endDate;

	@UiField
	DateBoxEx seniorityDate;

	@UiField
	SuggestBox agreement;

	@UiField
	ListBox level;

	@UiField
	TextBox category;

	@UiField
	ListBox quoteGroup;

	@UiField
	Button quoteGroupCotizB;

	@UiField
	ListBox occupation;

	@UiField
	ListBox rlce;

	@UiField
	ListBox employeesColective;

	@UiField
	ListBox journeyType;

	@UiField
	DoubleBox partialityCoef;

	@UiField
	SuggestBox cnoSB;

	// TABLA DATOS EMPLEADO

	@UiField
	HTMLPanel employeeTablePanel;

	@UiField
	DateBoxEx birthDate;

	@UiField
	Label age;

	@UiField
	ListBox gender;

	@UiField
	ListBox civilStatus;

	@UiField
	ListBox streetType;

	@UiField
	SuggestBox address;

	@UiField
	TextBox addressNum;

	@UiField
	TextBox addressInfo;

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
	SuggestBox account;

	@UiField
	HTMLPanel journeyDuration;

//	@UiField
//	FormPanel formPanel;
//
//	@UiField
//	FileUpload fileUpload;
//
//	@UiField
//	Button uploadButton;
	

	// ------------------------------------------------- Class variables

	private ContractType contractType;
	private Municipalities municipalities;

	private List<Agreement> agreements;

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<com.esferalia.aon.gwt.payroll.shared.Country> countries;

	private Map<String, CNO> cnoMap;

	// ------------------------------------------------- Constructor

	private static final String [] ALLOWED_DOMAINS = {".aonsolutions.org" ,"ayudat.aonsolutions.net."};

	
	protected Employee() {
		// Initialize Nationality SuggestBox
		providedNationality();

		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));

		this.contractType = new ContractType();
		this.municipalities = new Municipalities();

		initializeView();
		addReformatAccount();
		
//		checkDomain();
		
		impl.getCountries(new AsyncCallback<List<com.esferalia.aon.gwt.payroll.shared.Country>>() {

			@Override
			public void onSuccess(List<com.esferalia.aon.gwt.payroll.shared.Country> countriesResult) {
				countries = countriesResult;
				initializeProvince();

				impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

					@Override
					public void onSuccess(Map<String, CNO> cnoMapIn) {
						cnoMap = cnoMapIn;
						initializeCNOSuggest();
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
		
		((TextBox) securitySocialNum.getTextBox()).setMaxLength(12);
		((TextBox) document.getTextBox()).setMaxLength(9);

	}

	private void initializeCNOSuggest() {
		List<String> cnoSuggest = new ArrayList<>();
		for (Entry<String, CNO> entry : cnoMap.entrySet())
			cnoSuggest.add(entry.getKey() + " - " + entry.getValue().getTitle());

		cnoSuggest.sort((o1, o2) -> o1.compareTo(o2));

		MultiWordSuggestOracle orclCno = (MultiWordSuggestOracle) cnoSB.getSuggestOracle();
		orclCno.addAll(cnoSuggest);
		orclCno.setDefaultSuggestionsFromText(cnoSuggest);
		cnoSB.setAutoSelectEnabled(false);
		cnoSB.getElement().setPropertyString("placeholder", "C\u00f3digo CNO... (Ctrl + espacio para ver sugerencias)");

		cnoSB.getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cnoSB.setText("");
				cnoSB.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cnoSB.hideSuggestionList();
		});

		cnoSB.addSelectionHandler(e -> {
			String cnoValue = cnoSB.getValue();
			if (!AonStringUtils.isBlank(cnoValue))
				cnoValue = cnoValue.split(" -")[0];

			// Do something with cnoValue
			onEmployeeCnoSuggestionChange(cnoValue);
		});

		cnoSB.addValueChangeHandler(e -> {
			if (AonStringUtils.isBlank(e.getValue()))
				onEmployeeCnoSuggestionChange(null);
		});
	}

	public CNO getCNOByCode(String cnoCode) {
		return this.cnoMap.get(cnoCode);
	}

	private void providedNationality() {
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);
		this.nationality.setAutoSelectEnabled(true);
	}
	
//	public void checkDomain() {
//	    String currentDomain = Window.Location.getHostName();
//	    boolean isAllowedDomain = false;
//
//	    for (String allowedDomain : ALLOWED_DOMAINS) {
//	        if (currentDomain.endsWith(allowedDomain)) {
//	            isAllowedDomain = true;
//	            break;
//	        }
//	    }
//
//	    uploadButton.setVisible(isAllowedDomain);
//	}
//	
//	public void fillFormDniData(){
//		AsyncCallback<EmployeeDataResult> callback = new AsyncCallback<EmployeeDataResult>() {
//			
//			@Override
//			public void onSuccess(EmployeeDataResult e) {
//				document.setValue(e.getDni(),true);
//				nationality.setValue(e.getNationality(),true);
//				name.setValue(e.getName(),true);
//				firstSurname.setValue(e.getFirstSurname(),true);
//				secondSurname.setValue(e.getSecondSurname(),true);
//			}
//			@Override
//			public void onFailure(Throwable f) {
//				
//			}
//		};
//		formPanel.addSubmitCompleteHandler(event -> {
//				EmployeeDataResult eps = JsonUtils.safeEval(event.getResults());
//				callback.onSuccess(eps);
//			});
//		
//		formPanel.submit();
//	}

	// ------------------------------------------------- UiHandlers

	// TABLA DATOS CONTRATO

	@UiHandler("document")
	void onDocumentChangeValue(SelectionEvent<Suggestion> event) {
		String documentStr = this.document.getValue().trim();
		if (AonStringUtils.isNotBlank(documentStr))
			onEmployeeDocumentSuggestionChange(documentStr);

	}

	@UiHandler("document")
	void onDocumentChangeValue(ValueChangeEvent<String> event) {
		String documentStr = this.document.getValue().trim();

		if (AonStringUtils.isNotBlank(documentStr)) {

			String documentTypeStr = checkDocumentType(documentStr);
			this.documentType.setText(documentTypeStr);

			if (checkDocumentValidation(documentStr))
				removeErrorBorder(this.document);
			else
				addErrorBorder(this.document);

//			showNationality(documentTypeStr);	

			onEmployeeDocumentChange(documentStr, documentTypeStr);
		} else
			removeErrorBorder(this.document);
	}
	

//	@UiHandler("uploadButton")
//	void onUploadButtonClick(ClickEvent event) {
//		fileUpload.click();
//	}
//
//	@UiHandler("fileUpload")
//	void onFileUpload(ChangeEvent event) {
//		fileUpload.setName("archivo");
//		if (fileUpload.getFilename().contains(".jpg") || fileUpload.getFilename().contains(".pdf")) {
//			addUploadedBorder(this.uploadButton);
//			fillFormDniData();
//		}else {
//			addUploadedFail(this.uploadButton);
//			Window.alert("El archivo no es valido");
//		}
//	}

	@UiHandler("nationality")
	void onNationalitySelectionValue(SelectionEvent<Suggestion> event) {
		String countryIso2 = getIso2(this.nationality.getValue());
		onEmployeeNationalityChange(countryIso2);
	}

	@UiHandler("nationality")
	void onNationalityChangeValue(ValueChangeEvent<String> event) {
		if (AonStringUtils.isBlank(event.getValue()))
			onEmployeeNationalityChange(null);
	}

	@UiHandler("securitySocialNum")
	void onSocialSecurityNumChangeValue(SelectionEvent<Suggestion> event) {
		String ssNum = this.securitySocialNum.getValue().trim();
		if (AonStringUtils.isNotBlank(ssNum))
			onEmployeeSSNumSuggestionChange(ssNum);
	}

	@UiHandler("securitySocialNum")
	void onSocialSecurityNumChangeValue(ValueChangeEvent<String> event) {
		String ssNum = this.securitySocialNum.getValue().trim();
		if (AonStringUtils.isNotBlank(ssNum)) {
			if (checkSSNumValidation(ssNum)) {
				removeErrorBorder(this.securitySocialNum);
				this.securitySocialNum.setTitle(null);
			} else {
				addErrorBorder(this.securitySocialNum);
				this.securitySocialNum.setTitle("El numero es err\u00F3neo");
			}

			onEmployeeSSNumChange(ssNum);
		} else {
			removeErrorBorder(this.securitySocialNum);
			this.securitySocialNum.setTitle(null);
		}
	}

	@UiHandler("name")
	void onNameChangeValue(SelectionEvent<Suggestion> event) {
		String nameSurname = this.name.getValue().trim();
		if (AonStringUtils.isNotBlank(nameSurname))
			onEmployeeNameSuggestionChange(nameSurname);
	}

	@UiHandler("name")
	void onNameChangeValue(ValueChangeEvent<String> event) {
		String nameStr = this.name.getValue().trim();
		if (AonStringUtils.isNotBlank(nameStr))
			onEmployeeNameChange(nameStr);
	}

	@UiHandler("firstSurname")
	void onFirstSurnameChangeValue(SelectionEvent<Suggestion> event) {
		String nameSurname = this.firstSurname.getValue().trim();
		if (AonStringUtils.isNotBlank(nameSurname))
			onEmployeeFirstSurnameSuggestionChange(nameSurname);
	}

	@UiHandler("firstSurname")
	void onFirstSurnameChangeValue(ValueChangeEvent<String> event) {
		String surname = this.firstSurname.getValue().trim();
		if (AonStringUtils.isNotBlank(surname))
			onEmployeeFirstSurnameChange(surname);
	}

	@UiHandler("secondSurname")
	void onSecondSurnameChangeValue(ChangeEvent event) {
		String secondSurnameStr = this.secondSurname.getValue().trim();
		if (AonStringUtils.isNotBlank(secondSurnameStr))
			onEmployeeSecondSurnameChange(secondSurnameStr);
	}

	@UiHandler("ssRegimeType")
	void onContractSSRegimenChangeValue(ChangeEvent event) {
		byte ssRegime = Byte.parseByte(this.ssRegimeType.getSelectedValue());

		if (ssRegime == (byte) 3) {
			showElementsFreelancerTable();
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.journeyType);
		} else
			this.hideElementsFreelancerTable();

		onContractSSRegimenChange(ssRegime);
	}

	@UiHandler("mdTBTLB")
	void onContractMdTBTChangeValue(ChangeEvent event) {
		String mdTbt = mdTBTLB.getSelectedValue();
		onContractMdTBThange(mdTbt);
	}

	@UiHandler("activityCCC")
	void onContractActivityCCCChangeValue(ChangeEvent event) {
		String activityCCCStr = String.valueOf(this.activityCCC.getSelectedValue());

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
		String contractTypeStr = String.valueOf(this.contractTypeLB.getSelectedValue());

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
	}

	public void contractFireEventsWithOutValue() {
		String contractTypeStr = String.valueOf(this.contractTypeLB.getSelectedValue());

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

	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		Integer contractModel = Integer.valueOf(this.modality.getSelectedValue());

		if (AonNumberUtils.equals(contractModel, -1))
			onContractModalityChange(null);
		else
			onContractModalityChange(contractModel);
	}

	@UiHandler("startDate")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		Date startDateStr = this.startDate.getValue();
		onContractStartDateChange(startDateStr);

		if (null != startDate)
			this.seniorityDate.setValue(startDateStr, true);

		String contractTypeStr = String.valueOf(this.contractTypeLB.getSelectedValue());
		Integer contractTypeInt = Integer.parseInt(contractTypeStr);
		checkContracts401And501(contractTypeInt);

	}

	@UiHandler("endDate")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		Date endDateStr = this.endDate.getValue();
		onContractEndDateChange(endDateStr);
	}

	@UiHandler("seniorityDate")
	void onSeniorityDateChangeValue(ValueChangeEvent<Date> event) {
		Date startDateStr = this.startDate.getValue();
		Date seniorityDateStr = this.seniorityDate.getValue();

		if (null == startDateStr) {
			addWarnBorder(this.seniorityDate);
			this.seniorityDate.setTitle("La fecha de inicio no coincide con la de antig\u00FCedad.");
		} else if (null != seniorityDateStr) {
			DateUtils.resetTime(startDateStr);
			DateUtils.resetTime(seniorityDateStr);

			if (DateUtils.equals(startDateStr, seniorityDateStr)) {
				removeWarnBorder(this.seniorityDate);
				this.seniorityDate.setTitle("");
			} else {
				addWarnBorder(this.seniorityDate);
				this.seniorityDate.setTitle("La fecha de inicio no coincide con la de antig\u00FCedad.");
			}
		} else {
			removeWarnBorder(this.seniorityDate);
			this.seniorityDate.setTitle("");
		}

		onContractSeniorityDateChange(seniorityDateStr);
	}

	@UiHandler("agreement")
	void onContractAgreementSelection(SelectionEvent<Suggestion> event) {
		String agreementDescription = agreement.getValue();
		for (Agreement agreementIt : this.agreements)
			if (AonStringUtils.equalsIgnoreCase(agreementIt.getDescription(), agreementDescription))
				onContractAgreementChange(agreementIt.getId(), agreementIt.getSSNumber());
	}

	@UiHandler("agreement")
	void onEnterpriseAgreementValueChange(ValueChangeEvent<String> event) {
		String agreementDescription = agreement.getValue();
		if (AonStringUtils.isBlank(agreementDescription))
			onContractAgreementChange(null, null);
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
		String categoryStr = this.category.getValue();
		if (AonStringUtils.isNotBlank(categoryStr))
			onContractCategoryChange(categoryStr);
	}

	@UiHandler("quoteGroup")
	void onQuoteGroupChangeValue(ChangeEvent event) {
		String quoteGroupStr = String.valueOf(this.quoteGroup.getSelectedValue());
		quoteGroupStr = AonStringUtils.equalsIgnoreCase(quoteGroupStr, "-1") ? null : quoteGroupStr;

		if (AonStringUtils.equalsIgnoreCase(quoteGroupStr, "-1"))
			onContractQuoteGroupChange(null);
		else
			onContractQuoteGroupChange(quoteGroupStr);

		showHideQuoteIdx(null == quoteGroupStr ? null : Integer.parseInt(quoteGroupStr));
	}

	@UiHandler("quoteGroupCotizB")
	void onQuoteGroupCotizBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(quoteGroupCotizB);
		Boolean value = !oldValue;
		getEnableDisableButton(quoteGroupCotizB, value);
		onContractQuoteGroupIdx(value);
	}

	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		String occupationStr = String.valueOf(this.occupation.getSelectedValue());
		if (AonStringUtils.equalsIgnoreCase(occupationStr, "-1"))
			onContractOccupationChange(null);
		else
			onContractOccupationChange(occupationStr);
	}

	@UiHandler("rlce")
	void onContractRLCEChangeValue(ChangeEvent event) {
		onContractRLCEChange(this.rlce.getSelectedValue());
	}

	@UiHandler("employeesColective")
	void onContractEmployeesColectiveChangeValue(ChangeEvent event) {
		String value = this.employeesColective.getSelectedValue();
		onContractEmployeesColectiveChange(AonStringUtils.isBlank(value) ? null : value);
	}

	@UiHandler("journeyType")
	void onContractJourneyTypeChangeValue(ChangeEvent event) {
		Boolean journeyTypeStr = Boolean.valueOf(this.journeyType.getSelectedValue());
		if (Boolean.TRUE.equals(journeyTypeStr))
			showElementsFullTimeJourneyTypeContract();
		else
			showPartialTimeContract();

		onContractJourneyTypeChange(journeyTypeStr);
	}

	@UiHandler("partialityCoef")
	void onContractPartialityCoefChangeValue(ValueChangeEvent<Double> event) {
		Double partialityCoefStr = this.partialityCoef.getValue();
		onContractPartialityChange(partialityCoefStr);
	}

	// TABLA DATOS EMPLEADO

	@UiHandler("birthDate")
	void onBithDateChangeValue(ValueChangeEvent<Date> event) {
		Date birthDateStr = this.birthDate.getValue();

		if (null != birthDateStr) {
			Date actualDay = new Date();
			Integer ageStr = getYears(actualDay, birthDateStr);
			this.age.setText("( " + (ageStr) + " a\u00F1os )");
		} else
			this.age.setText("");

		onEmployeeBirthDateChange(birthDateStr);
	}

	@UiHandler("gender")
	void onGenderChangeValue(ChangeEvent event) {
		byte genderStr = Byte.parseByte(this.gender.getSelectedValue());
		onEmployeeGenderChange(genderStr);
	}

	@UiHandler("civilStatus")
	void onCivilStatusChangeValue(ChangeEvent event) {
		byte civilStatusStr = Byte.parseByte(this.civilStatus.getSelectedValue());
		onEmployeeCivilStatusChange(civilStatusStr);
	}

	@UiHandler("streetType")
	void onStreetTypeChangeValue(ChangeEvent event) {
		String streetTypeStr = String.valueOf(this.streetType.getSelectedValue());
		onEmployeeStreetTypeChange(streetTypeStr);
	}

	@UiHandler("address")
	void onAddressChangeValue(ValueChangeEvent<String> event) {
		String addressStr = this.address.getValue();
		onEmployeeAddressChange(addressStr);
	}

	@UiHandler("addressNum")
	void onAddressNumChangeValue(ChangeEvent event) {
		String addressNumStr = this.addressNum.getValue();
		onEmployeeAddressNumChange(addressNumStr);
	}

	@UiHandler("addressInfo")
	void onAddressInfoChangeValue(ChangeEvent event) {
		String addressInfoStr = this.addressInfo.getValue();
		onEmployeeAddressInfoChange(addressInfoStr);
	}

	@UiHandler("addressZip")
	void onAddressZipChangeValue(ChangeEvent event) {
		String addressZipStr = this.addressZip.getValue();
		onEmployeeAddressZipChange(addressZipStr);

		if (addressZipStr.length() == 5) {
			String zip = this.addressZip.getValue().substring(0, 2);
			setSelectedValueLB(addressProvince, zip);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
		}
	}

	@UiHandler("addressProvince")
	void onAddressProvinceChangeValue(ChangeEvent event) {
		String provinceCode = String.valueOf(this.addressProvince.getSelectedValue());
		Integer geozoneId = getProvincesGeozone(provinceCode);
		updateMunicipalities();
		onEmployeeAddressProvinceChange(geozoneId);
	}

	private Integer getProvincesGeozone(String provinceCode) {
		for (com.esferalia.aon.gwt.payroll.shared.Country country : countries)
			for (Geozone province : country.getProvinces())
				if (AonStringUtils.equalsIgnoreCase(province.getCode(), provinceCode))
					return province.getId();

		return null;
	}

	@UiHandler("addressMunicipality")
	void onAddressMunicipalityChangeValue(ChangeEvent event) {
//		String addressMunicipalityStr = municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString();
		onEmployeeAddressMunicipalityChange(addressMunicipality.getSelectedValue());
	}

	@UiHandler("mobile")
	void onMobileChangeValue(ChangeEvent event) {
		String mobileStr = this.mobile.getValue();
		onEmployeeMobileChange(mobileStr);
	}

	@UiHandler("phone")
	void onPhoneChangeValue(ChangeEvent event) {
		String phoneStr = this.phone.getValue();
		onEmployeePhoneChange(phoneStr);
	}

	@UiHandler("email")
	void onEmailChangeValue(ChangeEvent event) {
		String emailStr = this.email.getValue();
		onEmployeeEmailChange(emailStr);
	}

	@UiHandler("payMethod")
	void onPayMethodChangeValue(ChangeEvent event) {
		Integer payMethodId = Integer.parseInt(this.payMethod.getSelectedValue());
		if (AonNumberUtils.equals(payMethodId, -1))
			onEmployeePayMethodChange(null);
		else
			onEmployeePayMethodChange(payMethodId);
	}

	@UiHandler("bic")
	void onBIClChangeValue(ChangeEvent event) {
		String bicStr = this.bic.getValue();
		onEmployeeBICChange(bicStr);
	}

	@UiHandler("account")
	void onAccountChangeValue(ValueChangeEvent<String> event) {
		String accountStr = this.account.getValue();
		accountStr = accountStr.replaceAll("\\W+", "");
		accountStr = accountStr.toUpperCase();

		if (accountStr.length() > 0) {
			if (Iban.validateIBAN(accountStr)) {
				removeErrorBorder(this.account);
				this.account.setTitle(null);
			} else {
				addErrorBorder(this.account);
				this.account.setTitle("IBAN no valido");
			}
		}

		String bankAlias = getBankAlias(accountStr);
		String bankSwift = getBankSwift(accountStr);
		this.bic.setValue(bankSwift);
		onEmployeeAccountChange(accountStr, bankAlias, bankSwift);
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

	public abstract void onEmployeeAddressMunicipalityChange(String addressMunicipality);

	public abstract void onEmployeeMobileChange(String mobile);

	public abstract void onEmployeePhoneChange(String phone);

	public abstract void onEmployeeEmailChange(String email);

	public abstract void onEmployeePayMethodChange(Integer payMethodId);

	public abstract void onEmployeeBICChange(String bic);

	public abstract void onEmployeeAccountChange(String account, String bankAlias, String bankSwift);

	// SHOW ERROR

	public abstract void fireError(String title, String message);

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
		this.securitySocialNum.setValue("");
		this.name.setValue("");
		this.firstSurname.setValue("");
		this.secondSurname.setValue("");
		this.ssRegimeType.clear();
		this.activityCCC.clear();
		this.mdCTZLB.clear();
		this.mdTBTLB.clear();
		this.workplace.clear();
		this.contractTypeLB.clear();
		this.modality.clear();
		this.startDate.setValue(null);
		this.endDate.setValue(null);
		this.seniorityDate.setValue(null);
		this.agreement.setValue(null);
		this.level.clear();
		this.category.setValue("");
		this.quoteGroup.clear();
		getEnableDisableButton(this.quoteGroupCotizB, false);
		this.occupation.clear();
		this.rlce.clear();
		this.employeesColective.clear();
		this.journeyType.clear();
		this.partialityCoef.setValue(null);
		this.journeyDuration.clear();
		this.cnoSB.setValue(null);

		// TABLA DATOS EMPLEADO

		this.birthDate.setValue(null);
		this.gender.clear();
		this.civilStatus.clear();
		this.streetType.clear();
		this.address.setValue("");
		this.addressNum.setValue("");
		this.addressInfo.setValue("");
		this.addressZip.setValue("");
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
				.forEach(entry -> this.quoteGroup.addItem(entry.getKey(), entry.getValue()));

		// OCUPACION
		Occupation.getOccupation().entrySet()
				.forEach(entry -> this.occupation.addItem(entry.getKey(), entry.getValue()));

		// RLCE
		RLCE.getRLCE().entrySet()
				.forEach(entry -> rlce.addItem(entry.getKey() + " - " + entry.getValue(), entry.getKey()));

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
			this.streetType.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getShortCode());

	}
	
	public void checkCCCType(Byte cccType) {
		if(null != cccType && cccType == (byte)4)
			enable(this.ssRegimeType, "5");
		else
			disable(this.ssRegimeType, "5");
	}

	private void initializeProvince() {
		// PROVINCIA
		this.addressProvince.clear();
		this.addressProvince.addItem("-", "-1");

		for (com.esferalia.aon.gwt.payroll.shared.Country country : countries) {
			this.addressProvince.addItem(country.getCountry().getName(), country.getCountry().getCode());
			this.addressProvince.getElement().getElementsByTagName("option").getItem(addressProvince.getItemCount() - 1)
					.setAttribute("disabled", "disabled");
			for (Geozone province : country.getProvinces())
				this.addressProvince.addItem(province.getName(), province.getCode());
		}
	}

	private void initDisplayElements() {
		this.contractDataTable.getWidget(3).getElement().getStyle().clearDisplay();

		this.contractTypeLB.getElement().getStyle().clearDisplay();
		this.contractTypeFreelance.getElement().getStyle().setDisplay(Display.NONE);

		this.contractDataTable.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);

		this.contractDataTable.getWidget(7).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(10).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(12).getElement().getStyle().clearDisplay();

		hideEmployeesColective();

		this.contractDataTable.getWidget(15).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(16).getElement().getStyle().setDisplay(Display.NONE);
	}

	// ------------------------------------------------- Fill default fields

	public void fillDefaultFields() {
		// SS REGIME
		ssRegimeType.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), ssRegimeType);

		// GENDER
		gender.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gender);

		// CIVIL STATUS
		civilStatus.setSelectedIndex(5);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), civilStatus);

		// STREET_TYPE
		streetType.setSelectedIndex(14); // Calle
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), streetType);
	}

	// ------------------------------------------------- Initialize SuggestBox

	public void initSuggestBox(WorkplaceEmployees workplaceEmployees) {
		// DOCUMENT
		List<String> employeesDocuments = workplaceEmployees.getWorkplaceEmployeesDocument();
		List<String> employeesDocumentsSuggest = new ArrayList<>();
		employeesDocuments.forEach(documentValue -> employeesDocumentsSuggest.add(documentValue + ""));
		MultiWordSuggestOracle orclDocuments = (MultiWordSuggestOracle) document.getSuggestOracle();
		orclDocuments.addAll(employeesDocumentsSuggest);
		document.setAutoSelectEnabled(false);

		// SS_NUMBER
		List<String> employeesSSNumbers = workplaceEmployees.getWorkplaceEmployeesSSNumber();
		List<String> employeesSSNumbersSuggest = new ArrayList<>();
		employeesSSNumbers.forEach(ssNum -> employeesSSNumbersSuggest.add(ssNum + ""));
		MultiWordSuggestOracle orclSSNumbers = (MultiWordSuggestOracle) securitySocialNum.getSuggestOracle();
		orclSSNumbers.addAll(employeesSSNumbersSuggest);
		securitySocialNum.setAutoSelectEnabled(false);

		// NAMES
		List<String> employeesNames = workplaceEmployees.getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<>();
		employeesNames.forEach(nameValue -> employeesNamesSuggest.add(nameValue + ""));
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) name.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		name.setAutoSelectEnabled(false);

		// SURNAME
		List<String> employeesSurNames = workplaceEmployees.getWorkplaceEmployeesSurName();
		List<String> employeesSurNamesSuggest = new ArrayList<>();
		employeesSurNames.forEach(surName -> employeesSurNamesSuggest.add(surName + ""));
		MultiWordSuggestOracle orclSurNames = (MultiWordSuggestOracle) firstSurname.getSuggestOracle();
		orclSurNames.addAll(employeesSurNamesSuggest);
		firstSurname.setAutoSelectEnabled(false);
	}

	public void initActivitiesCCC(Map<Integer, String> activities, Map<Integer, CCCInfo> cccs) {
		// ACTIVITY - CCC
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
		workplace.addItem("-", "-1");
		for (Workplace workplaceInfo : workplaces)
			workplace.addItem(workplaceInfo.getDescription(), workplaceInfo.getId().toString());
	}

	public void initContractType() {
		// TIPO DE CONTRATO
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

		MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) agreement.getSuggestOracle();
		orclAgreements.addAll(agreementDescriptions);
		orclAgreements.setDefaultSuggestionsFromText(agreementDescriptions);
		agreement.setAutoSelectEnabled(true);
		agreement.getElement().setPropertyString("placeholder",
				"Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");

		agreement.getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				agreement.setText("");
				agreement.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				agreement.hideSuggestionList();
		});
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
		employeeTablePanel.getElement().getStyle().setDisplay(Display.NONE);
	}

	// ------------------------------------------------- Show/hide methods
	// freelancer

	public void showElementsFreelancerTable() {
		this.contractDataTable.getWidget(3).getElement().getStyle().setDisplay(Display.NONE);

		this.contractTypeLB.getElement().getStyle().setDisplay(Display.NONE);
		this.contractTypeFreelance.getElement().getStyle().clearDisplay();

		this.contractDataTable.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);

		this.contractDataTable.getWidget(7).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(10).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(12).getElement().getStyle().setDisplay(Display.NONE);

		this.contractDataTable.getWidget(15).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(16).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(17).getElement().getStyle().setDisplay(Display.NONE);
	}

	public void hideElementsFreelancerTable() {
		this.contractDataTable.getWidget(3).getElement().getStyle().clearDisplay();

		this.contractTypeLB.getElement().getStyle().clearDisplay();
		this.contractTypeFreelance.getElement().getStyle().setDisplay(Display.NONE);

		this.contractDataTable.getWidget(4).getElement().getStyle().clearDisplay();

		this.contractDataTable.getWidget(7).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(10).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(11).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(12).getElement().getStyle().clearDisplay();

		this.contractDataTable.getWidget(15).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(16).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(17).getElement().getStyle().clearDisplay();
	}

	// ------------------------------------------------- Show/hide methods
	// partial/full time

	public void showElementsFullTimeContract() {
		this.contractDataTable.getWidget(15).getElement().getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getWidget(16).getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showElementsFullTimeJourneyTypeContract() {
		this.contractDataTable.getWidget(16).getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showPartialTimeContract() {
		showElementsPartialTimeContract();
		journeyDuration.clear();
		AonToolbarSmallButton calendarBtn = new AonToolbarSmallButton("Abrir calendario",
				AON.CSS.aonIconEditCalendar());
		Label message = new Label("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		journeyDuration.add(calendarBtn);
		journeyDuration.add(message);
		journeyDuration.setTitle("Las horas se deben definir en el calendario del empleado.");

		calendarBtn.addClickHandler(e -> onContractJourneyDurationClick());
		message.addClickHandler(e -> onContractJourneyDurationClick());
	}

	private void showElementsPartialTimeContract() {
		this.contractDataTable.getWidget(15).getElement().getStyle().clearDisplay();
		this.contractDataTable.getWidget(16).getElement().getStyle().clearDisplay();
	}

	// ------------------------------------------------- Show/hide mdCtz methods

	public void showMdCtzContract() {
		this.contractDataTable.getWidget(4).getElement().getStyle().clearDisplay();
	}

	public void hideMdCtzContract() {
		this.contractDataTable.getWidget(4).getElement().getStyle().setDisplay(Display.NONE);
		this.mdCTZLB.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.mdCTZLB);

	}

	// ------------------------------------------------- Show/hide EmployeeColective
	// methods

	public void showEmployeesColective() {
		this.contractDataTable.getWidget(14).getElement().getStyle().clearDisplay();
	}

	public void hideEmployeesColective() {
		this.contractDataTable.getWidget(14).getElement().getStyle().setDisplay(Display.NONE);
	}

	// ------------------------------------------------- Show/hide Quote Idx

	public void showHideQuoteIdx(Integer quoteGroup) {
		if (AonNumberUtils.equals(quoteGroup, 8) || AonNumberUtils.equals(quoteGroup, 9)
				|| AonNumberUtils.equals(quoteGroup, 10) || AonNumberUtils.equals(quoteGroup, 11))
			this.contractDataTable.getWidget(11).getElement().getStyle().clearDisplay();
		else {
			this.contractDataTable.getWidget(11).getElement().getStyle().setDisplay(Display.NONE);
			getEnableDisableButton(quoteGroupCotizB, false);
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
			occupation.setSelectedIndex(1);
			break;
		case "b":
			occupation.setSelectedIndex(2);
			break;
		case "d":
			occupation.setSelectedIndex(3);
			break;
		case "e":
			occupation.setSelectedIndex(4);
			break;
		case "f":
			occupation.setSelectedIndex(5);
			break;
		case "g":
			occupation.setSelectedIndex(6);
			break;
		case "h":
			occupation.setSelectedIndex(7);
			break;
		default:
			occupation.setSelectedIndex(0);
			break;
		}
		onContractOccupationChange(str);

	}

	// ------------------------------------------------- Account methods

	private void addReformatAccount() {
		account.getElement().setAttribute("style", "text-transform:uppercase");
		account.getElement().setPropertyString("pattern", "[A-Z0-9]*");
		account.addValueChangeHandler(e -> reformatAccount(account));
	}

	public void reformatAccount(SuggestBox accountField) {
		String accountText = accountField.getText();
		accountText = accountText.replaceAll("\\W+", "");
		if (accountText.length() == 24) {
			accountField.setText(accountText.substring(0, 4) + "  " + accountText.substring(4, 8) + "  "
					+ accountText.substring(8, 12) + "  " + accountText.substring(12, 16) + "  "
					+ accountText.substring(16, 20) + "  " + accountText.substring(20, 24));
		} else
			accountField.setText(accountText);
	}

	public void initIbans(ArrayList<String> employeeIbans) {
		List<String> employeesIbanSuggest = new ArrayList<>();
		employeeIbans.forEach(employeesIbanSuggest::add);
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) account.getSuggestOracle();
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

	public void resetEmployeeInfo() {
		document.setValue(null);
		nationality.setValue(null);
		securitySocialNum.setValue(null);
		name.setValue(null);
		firstSurname.setValue(null);
		secondSurname.setValue(null);

		birthDate.setValue(null);
		gender.setSelectedIndex(0);

		setSelectedValueLB(streetType, "CL");
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

	public void blockVariablesExistingContract() {
//		String documentStr = this.document.getValue().trim();
//		if (AonStringUtils.isNotBlank(documentStr)) {
//			document.setEnabled(!checkDocumentValidation(documentStr));
//		} else
//			document.setEnabled(true);

		nationality.setEnabled(true);

		String ssNum = this.securitySocialNum.getValue().trim();
		if (AonStringUtils.isNotBlank(ssNum))
			securitySocialNum.setEnabled(!checkSSNumValidation(ssNum));
		else
			securitySocialNum.setEnabled(true);
	}

	public void unblockVariablesExistingContract() {
//		document.setEnabled(true);
		nationality.setEnabled(true);
		securitySocialNum.setEnabled(true);
	}

	public void blockFieldsExistingPayroll() {
		this.contractTypeLB.setEnabled(false);
		this.quoteGroup.setEnabled(false);
		this.occupation.setEnabled(false);
		this.partialityCoef.setEnabled(false);
	}

	public void unblockFieldsExistingPayroll() {
		this.contractTypeLB.setEnabled(true);
		this.quoteGroup.setEnabled(true);
		this.occupation.setEnabled(true);
		this.partialityCoef.setEnabled(true);
	}

	// ------------------------------------------------- Auxiliar methods

	public void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);

		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
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

	public String checkDocumentType(String document) {

		if (null == document)
			return "( Pasaporte )";

		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(document.toUpperCase()))
			return "( DNI )";
		else if (niePattern.test(document.toUpperCase()))
			return "( NIE )";
		else if (cifPattern.test(document.toUpperCase()))
			return "( CIF) ";
		else
			return "( Pasaporte )";
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
			nationalityPanel.getElement().getStyle().clearDisplay();
		else {
			nationalityPanel.getElement().getStyle().setDisplay(Display.NONE);
			nationality.setValue("");
		}
	}

	public boolean checkSSNumValidation(String ssNumStr) {
		SocialSecurity ss = new SocialSecurity(ssNumStr);
		
		return ss.checkSS();
	}

	public void updateModality(Integer contractTypeInt) {
		this.modality.clear();
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
		String provinceCode = addressProvince.getSelectedValue();
		addressMunicipality.clear();
		addressMunicipality.addItem("-", "-1");
		HashMap<String, String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.entrySet().forEach(e -> addressMunicipality.addItem(e.getValue(), e.getKey()));
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
		Byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue());

		if (ssRegime == (byte) 3) { // RETA
			if (!isNotNameBlank())
				messageMap.put("Nombre", "Campo obligatorio");
			if (!isWokplaceSelected())
				messageMap.put("Centro de trabajo", "Campo obligatorio");
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
		String addressProvinceValue = addressProvince.getSelectedValue();

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
		Byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue());

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
			addErrorBorder(name);
			return false;
		} else
			return true;
	}

	private boolean isWokplaceSelected() {
		String workplaceValue = workplace.getSelectedValue();
		if (AonStringUtils.equalsIgnoreCase(workplaceValue, "-1")) {
			addErrorBorder(workplace);
			return false;
		} else
			return true;
	}

	private boolean isActivityCCCSelected() {
		String activityValue = activityCCC.getSelectedValue();
		if (AonStringUtils.equalsIgnoreCase(activityValue, "-1")) {
			addErrorBorder(activityCCC);
			return false;
		} else
			return true;
	}

	private boolean isContractTypeSelected() {
		String contractTypeValue = contractTypeLB.getSelectedValue();
		if (AonStringUtils.equalsIgnoreCase(contractTypeValue, "-1")) {
			addErrorBorder(contractTypeLB);
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
			return true;
		} else {
			String agreementLevelValue = level.getSelectedValue();
			if (AonStringUtils.equalsIgnoreCase(agreementLevelValue, "-1")) {
				addErrorBorder(level);
				return false;
			} else
				return true;
		}
	}

	private boolean checkDates() {
		Date startDateValue = null == startDate.getValue() ? null : DateUtils.copyDateOnly(startDate.getValue());
		Date endDateValue = null == endDate.getValue() ? null : DateUtils.copyDateOnly(endDate.getValue());

		if (null == startDateValue) {
			addErrorBorder(startDate);
			return false;
		}

		if (null == endDateValue || endDateValue.after(startDateValue) || endDateValue.equals(startDateValue))
			return true;
		else {
			addErrorBorder(startDate);
			return false;
		}

	}

	// ------------------------------------------------- journeyDuration.Methods

	public void createJourneyDurationWarning() {
		journeyDuration.clear();
		AonToolbarSmallButton calendarBtn = new AonToolbarSmallButton("Abrir calendario",
				AON.CSS.aonIconEditCalendar());
		Label message = new Label("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		journeyDuration.add(calendarBtn);
		journeyDuration.add(message);
		journeyDuration.setTitle("Las horas se deben definir en el calendario del empleado.");

		calendarBtn.addClickHandler(e -> onContractJourneyDurationClick());
		message.addClickHandler(e -> onContractJourneyDurationClick());
	}

	public void createJourneyDurationInfo(String messageStr) {
		journeyDuration.clear();
		Label message = new Label(messageStr);
		journeyDuration.add(message);

		message.addClickHandler(e -> onContractJourneyDurationClick());
	}

	// ------------------------------------------------- Add and remove styles

	private void addErrorBorder(Widget widget) {
		widget.addStyleName(style.errorBorder());
	}

	private void removeErrorBorder(Widget widget) {
		widget.removeStyleName(style.errorBorder());
	}

	private void addWarnBorder(Widget widget) {
		widget.addStyleName(style.errorBorder());
	}

	private void removeWarnBorder(Widget widget) {
		widget.removeStyleName(style.errorBorder());
	}
	
	private void addUploadedBorder(Widget widget) {
		widget.addStyleName(style.uploaded());
	}

	private void addUploadedFail(Widget widget) {
		widget.addStyleName(style.uploadedFail());
	}
	
	private void disable(ListBox listBox, String value) {
		NodeList<OptionElement> options = ((SelectElement) listBox.getElement().cast()).getOptions();
		for (int i = 0; i < options.getLength(); i++) {
			OptionElement option = options.getItem(i);
			if (AonStringUtils.equals(option.getValue(), value)) {
				option.setDisabled(true);
			}
		}
	}
	
	private void enable(ListBox listBox, String value) {
		NodeList<OptionElement> options = ((SelectElement) listBox.getElement().cast()).getOptions();
		for (int i = 0; i < options.getLength(); i++) {
			OptionElement option = options.getItem(i);
			if (AonStringUtils.equals(option.getValue(), value)) {
				option.setDisabled(false);
			}
		}
	}

	// ------------------------------------------------- cleanWarningIcons

	public void cleanErrorStyles() {
		removeErrorBorder(name);
		removeErrorBorder(activityCCC);
		removeErrorBorder(workplace);
		removeErrorBorder(contractTypeLB);
		removeErrorBorder(startDate);
		removeErrorBorder(addressZip);
		removeErrorBorder(addressProvince);
		removeErrorBorder(addressMunicipality);
		removeErrorBorder(level);
	}

	public void selectProvince(Integer geozoneId) {
		setSelectedValueLB(addressProvince, getProvinceCode(geozoneId));
	}

	private String getProvinceCode(Integer geozoneId) {
		for (com.esferalia.aon.gwt.payroll.shared.Country country : countries)
			for (Geozone province : country.getProvinces())
				if (province.getId().equals(geozoneId))
					return province.getCode();
		return null;
	}

}
