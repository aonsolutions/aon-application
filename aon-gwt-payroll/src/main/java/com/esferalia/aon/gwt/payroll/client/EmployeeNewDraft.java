package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeNewDraft extends Composite implements ContextMenuHandler {

	// ------------------------------------------------ GOOGLE MAP ADDRESS INFO --------------------------------------------------

	private static class Place extends JavaScriptObject {

		protected Place() {
		}

		public final AddressComponent get(String type) {
			for (int i = 0; i < getAddressComponents().length(); i++) {
				if (getAddressComponents().get(i).getTypes()[0] == type)
					return getAddressComponents().get(i);
			}
			return null;
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)
		public final native JsArray<AddressComponent> getAddressComponents() /*-{
			return this.address_components;
		}-*/;

	}

	private static class AddressComponent extends JavaScriptObject {

		protected AddressComponent() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)
		public final native String[] getTypes() /*-{
			return this.types;
		}-*/;

		public final native String getLongName() /*-{
			return this.long_name;
		}-*/;

		public final native String getShortName() /*-{
			return this.short_name;
		}-*/;
	}

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeNewDraft> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String topLabelContract();
		String nssWidht();
	}

	@UiField
	Button saveButton;

	// TABLA DATOS EMPLEADO

	@UiField
	TableElement employeeDataTable;

	@UiField
	TextBox personId;

	@UiField
	Button newPerson;

	@UiField
	SuggestBox findPerson;

	@UiField
	TextBox name;

	@UiField
	Label firstSurnameLabel;

	@UiField
	TextBox first_surname;

	@UiField
	TextBox second_surname;

	@UiField
	TextBox document;

	@UiField
	Label document_type;

	@UiField
	TableCellElement documentTypeCell;

	@UiField
	TableCellElement nationalityLabelCell;

	@UiField
	TableCellElement nationalityCell;

	@UiField(provided = true)
	SuggestBox nationality;

	@UiField
	TextBox security_social_num;

	@UiField
	DateBoxEx birth_date;

	@UiField
	Label age;

	@UiField
	ListBox gender;
	
	@UiField
	ListBox street_type;

	@UiField
	SuggestBox address;

	@UiField
	TextBox addressNum;

	@UiField
	TextBox addressZip;

	@UiField
	TextBox addressCity;

	@UiField
	TextBox addressProvince;

	@UiField
	TextBox phone;

	@UiField
	TextBox mobile;

	@UiField
	TextBox email;

	@UiField
	ListBox payMethod;

	@UiField
	TextBox account;

	@UiField
	TextBox bic;

	// TABLA DATOS CONTRATO

	@UiField
	Label labelContractDataTable;

	@UiField
	ListBox ssRegimeType;

	@UiField
	TableElement contractDataTable;

	@UiField
	TextBox activity;

	@UiField
	TextBox quotationAccount;

	@UiField
	TextBox workplace;

	@UiField
	TableCellElement contractTypeNode;

	@UiField
	ListBox contractType;

	@UiField
	TextBox contractTypeFreelance;

	@UiField
	ListBox modality;

	@UiField
	DateBoxEx strat_date;

	@UiField
	DateBoxEx end_date;

	@UiField
	Label seniority_date_label;

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

	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private EmployeeNewDraftObject employeeNewDraftObject;
	private ContractType contract_type;
	private EmployeeInfoDataBase employeeInfo;

	// --------------------------------------------------------- CONSTRUCTOR ---------------------------------------------------------

	public EmployeeNewDraft() {
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);

		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));

		// Init Google Maps Places API
		// initGoogleMapsPlaces();

		this.document.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> document) {
				String document_type_str = checkDocumentType(document.getValue());
				documentTypeCell.getStyle().clearDisplay();
				document_type.setText(document_type_str);

				showNationality(document_type_str);
			}
		});

		this.agreement.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				level.clear();
				String agreementName = agreement.getSelectedItemText();
				List<Agreement> agreements = employeeNewDraftObject.getAgreements();
				level.addItem("-");
				for (Agreement a : agreements) {
					if (a.getId() > 0 && a.getDescription() == agreementName) {
						Set<Level> levels = a.getLevels();
						for (Level levelRecord : levels) {
							Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
							for (String categoryRecord : categories) {
								level.addItem(levelRecord.getDescription() + " - " + categoryRecord);
							}
						}
					}
				}
			}
		});

		this.contractType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				modality.clear();
				modality.addItem("-");
				Integer contractTypeId = -1;
				if (contractType.getSelectedIndex() != 0) {
					String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
					contractTypeId = Integer.parseInt(contract_type_id_str);
				}

				List<ModelRecord> contractTypeModels = EmployeeNewDraft.this.contract_type
						.getModelsContractType(contractTypeId);
				for (ModelRecord m : contractTypeModels) {
					modality.addItem(m.getModelDescription());
				}

			}
		});

		this.findPerson.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				int c = event.getUnicodeCharCode();
				if (c == 32)
					findPerson.showSuggestionList();
			}
		});

	}

	private void initGoogleMapsPlaces() {
		loadMapsPlacesAPI(() -> {
			initializeAutocomplete(this.address.getElement(), (js) -> {
				Place place = js.cast();

				employeeNewDraftObject.setEmployeeAddress(place.get("route").getLongName());
				employeeNewDraftObject.setEmployeeAddressNumber(place.get("street_number").getShortName());
				employeeNewDraftObject.setEmployeeAddressZip(place.get("postal_code").getLongName());
				employeeNewDraftObject.setEmployeeAddressLacality(place.get("locality").getLongName());
				employeeNewDraftObject.setEmployeeAddressProvince(place.get("administrative_area_level_2").getLongName());

				// //Calle
				// Window.alert("route :"+place.get("route").getLongName());
				// Window.alert("route :"+place.get("route").getShortName());
				
				//Numero domicilio
				// Window.alert("street_number :"+place.get("street_number").getShortName()); 
			
				// //Localidad
				// Window.alert("locality :"+place.get("locality").getLongName()); 
				
				// //Provincia
				// Window.alert("administrative_area_level_2 :"+place.get("administrative_area_level_2").getLongName());
				
				// //Comunidad autonoma
				// Window.alert("administrative_area_level_1 :"+place.get("administrative_area_level_1").getLongName());
				
				// //Codigo pais
				// Window.alert("country :"+place.get("country").getShortName()); 
				
				// //Pais
				// Window.alert("country :"+place.get("country").getLongName());
				
				//Codigo postal
				// Window.alert("postal_code :"+place.get("postal_code").getLongName()); 
			});
		});

	}

	// -----------------------------------------------------------------
	// UiHandlers ----------------------------------------------------------

	@UiHandler("findPerson")
	void onFindPersonChangeValue(ValueChangeEvent<String> event) {
		String person = findPerson.getValue();
		Integer personIdNum = Integer.parseInt(person.split(" ")[0]);
		this.personId.setValue(personIdNum.toString());
		employeeNewDraftObject.setContractTableId(personIdNum);
	}

	@UiHandler("newPerson")
	void onNewPersonClick(ClickEvent event) {
		showPersonForm();
	}

	@UiHandler("document")
	void onDocumentChangeValue(ChangeEvent event) {
		String document_type_str = checkDocumentType(document.getValue());
		document_type.setText(document_type_str);

		employeeNewDraftObject.setEmployeeDocument(document.getValue());
		employeeNewDraftObject.setEmployeeDocumentType(document_type_str);

		showNationality(document_type_str);
	}

	@UiHandler("name")
	void onNameChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeName(name.getValue());
	}

	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeFirstSurname(first_surname.getValue());
	}

	@UiHandler("second_surname")
	void onSecondSurnameChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeSecondSurname(second_surname.getValue());
	}

	@UiHandler("birth_date")
	void onBithDateChangeValue(ValueChangeEvent<Date> event) {
		employeeNewDraftObject.setEmployeeBirthDate(birth_date.getValue());
	}

	@UiHandler("gender")
	void onGenderChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeGender(gender.getSelectedIndex());
	}

	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeSocialSecurityNum(security_social_num.getValue());
	}
	
	@UiHandler("address")
	void onAddressChangeValue(ValueChangeEvent<String> event) {
		employeeNewDraftObject.setEmployeeAddress(address.getValue());
	}

	@UiHandler("addressNum")
	void onAddressNumChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeAddressNumber(addressNum.getValue());
	}

	@UiHandler("addressZip")
	void onAddressZipChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeAddressZip(addressZip.getValue());
	}

	@UiHandler("addressCity")
	void onAddressCityChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeAddressLacality(addressCity.getValue());
	}

	@UiHandler("addressProvince")
	void onAddressProvinceChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeAddressProvince(addressProvince.getValue());
	}

	@UiHandler("phone")
	void onPhoneChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeePhone(phone.getValue());
	}

	@UiHandler("mobile")
	void onMobileChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeMobile(mobile.getValue());
	}

	@UiHandler("email")
	void onEmailChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeEmail(email.getValue());
	}

	@UiHandler("payMethod")
	void onPayMethodChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeePayMethod(payMethod.getSelectedItemText());
	}

	@UiHandler("account")
	void onAccountChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeAccount(account.getValue());
	}

	@UiHandler("bic")
	void onBIClChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setEmployeeBIC(bic.getValue());
	}

	@UiHandler("ssRegimeType")
	void onssRegimeTypeChangeValue(ChangeEvent event) {
		if (this.ssRegimeType.getSelectedIndex() == 1) {
			showRetaForm();
			employeeNewDraftObject.setSSRegime(3);
		} else {
			hideRetaForm();
			employeeNewDraftObject.setSSRegime(0);
		}
	}

	@UiHandler("activity")
	void onActivityChangeValue(ValueChangeEvent<String> event) {
		employeeNewDraftObject.setEnterprise_Activity(activity.getValue());
	}

	@UiHandler("quotationAccount")
	void onQuoteAccountChangeValue(ValueChangeEvent<String> event) {
		employeeNewDraftObject.setContractQuoteAccount(quotationAccount.getValue());
	}

	@UiHandler("workplace")
	void onWorkplaceChangeValue(ValueChangeEvent<String> event) {
		employeeNewDraftObject.setWorkplace(workplace.getValue());
	}

	@UiHandler("contractType")
	void onContractTypeChangeValue(ChangeEvent event) {
		if (contractType.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractType(null);
			employeeNewDraftObject.setContractModel(null);
		} else {
			String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
			employeeNewDraftObject.setContractType(contract_type_id_str);
		}
	}

	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		if (contractType.getSelectedIndex() == 0 || modality.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractModel(null);
		} else {
			String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
			Integer contractTypeId = Integer.parseInt(contract_type_id_str);

			String contractModelDescription = modality.getSelectedItemText();
			Integer contractModelEnum = this.contract_type.getContractModelId(contractTypeId, contractModelDescription);
			employeeNewDraftObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
		}
	}

	@UiHandler("strat_date")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		employeeNewDraftObject.setContractStartDate(strat_date.getValue());
	}

	@UiHandler("end_date")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		employeeNewDraftObject.setContractEndDate(end_date.getValue());
	}

	@UiHandler("seniority_date")
	void onSeniorityDateChangeValue(ValueChangeEvent<Date> event) {
		employeeNewDraftObject.setContractSeniorityDate(seniority_date.getValue());
	}

	@UiHandler("agreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		if (this.agreement.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractAgreementId(null);
			employeeNewDraftObject.setContractAgreementDescription(null);
			employeeNewDraftObject.setContractAgreementLevelId(null);
			employeeNewDraftObject.setContractAgreementLevelDescription(null);
		} else {
			Integer agreementId = employeeNewDraftObject.getAgreementId(this.agreement.getSelectedItemText());
			employeeNewDraftObject.setContractAgreementId(agreementId);
			employeeNewDraftObject.setContractAgreementDescription(this.agreement.getSelectedItemText());
		}
	}

	@UiHandler("level")
	void onContractAgreementLevelChangeValue(ChangeEvent event) {
		if (this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractAgreementLevelId(null);
			employeeNewDraftObject.setContractAgreementLevelDescription(null);
		} else {
			Integer agreementLevelId = employeeNewDraftObject.getAgreementLevelId(this.agreement.getSelectedItemText(), this.level.getSelectedItemText());
			employeeNewDraftObject.setContractAgreementLevelId(agreementLevelId);
			employeeNewDraftObject.setContractAgreementLevelDescription(this.level.getSelectedItemText());
			String levelDescription = (this.level.getSelectedItemText() == null || this.level.getSelectedItemText() == "-") ? null : this.level.getSelectedItemText().split("- ")[1];
			this.category.setValue(levelDescription);
			employeeNewDraftObject.setContractCategory(levelDescription);
		}
	}

	@UiHandler("category")
	void onCategoryChangeValue(ChangeEvent event) {
		employeeNewDraftObject.setContractCategory(category.getValue());
	}

	@UiHandler("quote_group")
	void onQuoteGroupChangeValue(ChangeEvent event) {
		if (quote_group.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractQuoteGroup(null);
		} else {
			String quoteGroup = getQuoteByIndex(quote_group.getSelectedIndex());
			employeeNewDraftObject.setContractQuoteGroup(quoteGroup);
		}
	}

	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		if (occupation.getSelectedIndex() == 0) {
			employeeNewDraftObject.setContractOccupation(null);
		} else {
			String contractOccupation = getOcupationByIndex(occupation.getSelectedIndex());
			employeeNewDraftObject.setContractOccupation(contractOccupation);
		}
	}

	@UiHandler("journeyType")
	void onContractJourneyTypeChangeValue(ChangeEvent event) {
		Integer journeyTypeIndex = this.journeyType.getSelectedIndex();
		Boolean journey_type = (journeyTypeIndex == 0) ? true : false;
		employeeNewDraftObject.setContractJourneyType(journey_type);
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (personId.getValue() == "") {
			// Nationality
			String countryIso2 = getIso2(nationality.getValue());
			employeeNewDraftObject.setNationality(countryIso2);

			// Gender
			employeeNewDraftObject.setEmployeeGender(gender.getSelectedIndex());
		}

		if (payMethod.getSelectedItemText() == "TRANSFERENCIA" && (account.getValue() == "" || bic.getValue() == "")) {
			WarningDialog dialog = new WarningDialog("Aviso", "HAY QUE RELLENAR LA CUENTA Y EL BIC");
			dialog.center();
			dialog.show();
		} else if (!checkIfUpdateIsPossible()) {
			WarningDialog dialog = new WarningDialog("Aviso", "LOS CAMPOS EN COLOR AZUL SON OBLIGATORIOS");
			dialog.center();
			dialog.show();
		} else {
			Integer journeyTypeIndex = this.journeyType.getSelectedIndex();
			Boolean journey_type = (journeyTypeIndex == 0) ? true : false;
			employeeNewDraftObject.setContractJourneyType(journey_type);
			
			Integer streetTypeIdx = street_type.getSelectedIndex();
			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
			employeeNewDraftObject.setEmployeeStreetType(shortCode);

			employeeNewDraftObject.createEmployeeContract(r -> {
				setEmployeeNewDraftObject(employeeNewDraftObject);
			}, t -> {
			});
		}
	}

	private boolean checkIfUpdateIsPossible() {
		if (personId.getValue() == "") {
			if (name.getValue() == "" || first_surname.getValue() == "" || document.getValue() == ""
					|| security_social_num.getValue() == "" || address.getValue() == "" || addressNum.getValue() == ""
					|| addressZip.getValue() == "" || addressCity.getValue() == ""
					|| addressProvince.getValue() == "") {
				return false;
			}
		}

		if (ssRegimeType.getSelectedIndex() == 1) {
			if (strat_date.getValue() == null) {
				return false;
			}
		} else {
			if (/*quotationAccount.getValue() == "" || activity.getValue() == "" ||*/ workplace.getValue() == ""
					|| contractType.getSelectedIndex() == 0 || strat_date.getValue() == null || modality.getSelectedIndex() == 0) {
				return false;
			}
		}

		return true;
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setEmployeeNewDraftObject(EmployeeNewDraftObject employeeDraft) {
		this.contract_type = new ContractType();
		this.employeeNewDraftObject = employeeDraft;

		this.employeeNewDraftObject.getWorkplaceEmployees(
			s -> { initializeView();}
			, f -> {}
		);
	}

	private void initializeView() {
		resetElements();
		initializeListBox();
		initializeRestElements();
		hidePersonForm();
		hideRetaForm();
	}

	private void resetElements() {
		// Clear employee elements
		this.personId.setValue("");
		this.document.setValue("");
		this.name.setValue("");
		this.first_surname.setValue("");
		this.second_surname.setValue("");
		this.birth_date.setValue(null);
		this.gender.clear();
		this.security_social_num.setValue("");
		this.street_type.clear();
		this.address.setValue("");
		this.phone.setValue("");
		this.mobile.setValue("");
		this.email.setValue("");
		this.payMethod.clear();
		this.account.setValue("");
		this.bic.setValue("");

		// Clear contract elements
		this.ssRegimeType.clear();
		this.activity.setValue("");
		this.quotationAccount.setValue("");
		this.contractType.clear();
		this.modality.clear();
		this.strat_date.setValue(null);
		this.end_date.setValue(null);
		this.seniority_date.setValue(null);
		this.agreement.clear();
		this.level.clear();
		this.category.setValue("");
		this.quote_group.clear();
		this.occupation.clear();
		this.journeyType.clear();
	}

	private void initializeListBox() {
		WorkplaceEmployees workplaceEmployees = employeeNewDraftObject.getWorkplaceEmployees();
		List<String> employees = new ArrayList<>();
		for (EmployeeInfo e : workplaceEmployees.getWorkplaceEmployees())
			employees.add(e.getEmployeeId() + " - " + e.getSurName() + ", " + e.getName());

		MultiWordSuggestOracle orcl = (MultiWordSuggestOracle) findPerson.getSuggestOracle();
		orcl.addAll(employees);

		// SEXO
		this.gender.addItem("Hombre");
		this.gender.addItem("Mujer");
		this.gender.addItem("Desconocido");
		
		//TIPO DE VIA
		for(int i=0; i<StreetType.values().length; i++){
			this.street_type.addItem(StreetType.values()[i].getDescription());
		}
		this.street_type.setSelectedIndex(14); //Calle por defecto

		// TIPO DE PAGO
		this.payMethod.addItem("-");
		this.payMethod.addItem("EFECTIVO");
		this.payMethod.addItem("GIRO");
		this.payMethod.addItem("CHEQUE");
		this.payMethod.addItem("TRANSFERENCIA");

		// TIPO DE COTIZACIÓN
		this.ssRegimeType.addItem("COMUN");
		this.ssRegimeType.addItem("RETA");
		this.ssRegimeType.addItem("SOCIOS COOP");
		this.ssRegimeType.addItem("JUBILACION ACTIVA");
		this.ssRegimeType.addItem("GARANTIA JUVENIL");

		// TIPO DE CONTRATO
		this.contractType.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : this.contract_type.getContractTypes().entrySet()) {
			this.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());
		}

		// MODALIDAD
		this.modality.addItem("-");

		// CONVENIO
		this.agreement.addItem("-");
		List<Agreement> agreements = employeeNewDraftObject.getActiveAgreements();
		for (Agreement a : agreements) {
			this.agreement.addItem(a.getDescription());
		}

		// NIVELES/CATEGORIA
		this.level.addItem("-");

		// GRUPO DE COTIZACION
		this.quote_group.addItem("-");
		this.quote_group.addItem("01. Alta direcci" + String.valueOf("\u00F3") + "n y personal no incluido en el E.T.");
		this.quote_group.addItem("02. Ingenieros t" + String.valueOf("\u00E9") + "cnicos, peritos y ayudantes titulados");
		this.quote_group.addItem("03. Jefes administrativos y de taller");
		this.quote_group.addItem("04. Ayudantes no titulados");
		this.quote_group.addItem("05. Oficiales administrativos");
		this.quote_group.addItem("06. Subalternos");
		this.quote_group.addItem("07. Axiliares administrativos");
		this.quote_group.addItem("08. Oficiales de primera y segunda");
		this.quote_group.addItem("09. Oficiales de tercera y especialista");
		this.quote_group.addItem("10. Peones");
		this.quote_group.addItem("11. Trabajadores menos de dieciocho a" + String.valueOf("\u00F1") + "os");

		// OCUPACION
		this.occupation.addItem("-");
		this.occupation.addItem("a. Personal en trabajos exclusivos de oficina");
		this.occupation.addItem("b. Tipo de cotizaci" + String.valueOf("\u00F3") + "n para todos los trabajadores que deban desplazarse habitalmente");
		this.occupation.addItem("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci" + String.valueOf("\u00F3") + "n en general");
		this.occupation.addItem("e. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de pasajeros en general (taxis, autom" + String.valueOf("\u00F3") + "viles, autobuses, etc)");
		this.occupation.addItem("f. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de mercanc" + String.valueOf("\u00ED") + "as que tengan una capacidad de carga " + String.valueOf("\u00FA") + "til superior a 3,5 Tm.");
		this.occupation.addItem("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles");
		this.occupation.addItem("h. Vigilantes, guardas, guardas jurados y personal de seguridad");

		// TIPO DE JORNADA
		this.journeyType.addItem("Tiempo Completo");
		this.journeyType.addItem("Tiempo Parcial");
	}

	private void initializeRestElements() {
		this.workplace.setValue(this.employeeNewDraftObject.getWorkplaceName());
		employeeNewDraftObject.setWorkplace(workplace.getValue());
		employeeNewDraftObject.setSSRegime(0);
		this.workplace.setEnabled(false);

		this.activity.setValue(this.employeeNewDraftObject.getWorkplaceActivity());
		employeeNewDraftObject.setEnterprise_Activity(activity.getValue());
		this.activity.setEnabled(false);

		this.quotationAccount.setValue(this.employeeNewDraftObject.getWorkplaceCCC());
		employeeNewDraftObject.setContractQuoteAccount(quotationAccount.getValue());
		this.quotationAccount.setEnabled(false);

		Integer agreementIndex = this.employeeNewDraftObject.getAgreementIndex(this.employeeNewDraftObject.getWorkplaceAgreement());
		this.agreement.setSelectedIndex(agreementIndex + 1);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.agreement);

		contractDataTable.getStyle().setTop(80, Unit.PX);
		labelContractDataTable.removeStyleName(style.topLabelContract());
	}

	private void showPersonForm() {
		this.employeeDataTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(2).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(3).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(4).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(5).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(6).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(7).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(8).getStyle().clearDisplay();

		this.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
		this.nationalityCell.getStyle().setDisplay(Display.NONE);

		contractDataTable.getStyle().setTop(260, Unit.PX);
		labelContractDataTable.addStyleName(style.topLabelContract());
	}

	private void hidePersonForm() {
		this.employeeDataTable.getRows().getItem(1).getStyle().clearDisplay();
		this.employeeDataTable.getRows().getItem(2).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(5).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(6).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(7).getStyle().setDisplay(Display.NONE);
		this.employeeDataTable.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		documentTypeCell.getStyle().setDisplay(Display.NONE);
	}

	private void showRetaForm() {
		contractDataTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		contractDataTable.getRows().getItem(2).getStyle().setDisplay(Display.NONE);
		contractTypeNode.getStyle().setDisplay(Display.NONE);
		this.contractTypeFreelance.removeStyleName(style.hide());
		contractDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		this.seniority_date_label.addStyleName(style.hide());
		this.seniority_date.addStyleName(style.hide());
		contractDataTable.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		contractDataTable.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		contractDataTable.getRows().getItem(10).getStyle().clearDisplay();
	}

	private void hideRetaForm() {
		contractDataTable.getRows().getItem(1).getStyle().clearDisplay();
		contractDataTable.getRows().getItem(2).getStyle().clearDisplay();
		contractTypeNode.getStyle().clearDisplay();
		this.contractTypeFreelance.addStyleName(style.hide());
		contractDataTable.getRows().getItem(4).getStyle().clearDisplay();
		this.seniority_date_label.removeStyleName(style.hide());
		this.seniority_date.removeStyleName(style.hide());
		contractDataTable.getRows().getItem(8).getStyle().clearDisplay();
		contractDataTable.getRows().getItem(9).getStyle().clearDisplay();
		contractDataTable.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
	}

	private String getQuoteByIndex(Integer index) {
		switch (index) {
		case 1:
			return "\"01\"";
		case 2:
			return "\"02\"";
		case 3:
			return "\"03\"";
		case 4:
			return "\"04\"";
		case 5:
			return "\"05\"";
		case 6:
			return "\"06\"";
		case 7:
			return "\"07\"";
		case 8:
			return "\"08\"";
		case 9:
			return "\"09\"";
		case 10:
			return "\"10\"";
		case 11:
			return "\"11\"";
		default:
			return null;
		}
	}

	private String getOcupationByIndex(Integer index) {
		switch (index) {
		case 1:
			return "\"a\"";
		case 2:
			return "\"b\"";
		case 3:
			return "\"d\"";
		case 4:
			return "\"e\"";
		case 5:
			return "\"f\"";
		case 6:
			return "\"g\"";
		case 7:
			return "\"h\"";
		default:
			return null;
		}
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

	// -------------------------------------------------- CHECK DOCUMENT TYPE -------------------------------------------------------

	private static String checkDocumentType(String document) {

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

	private void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			nationalityLabelCell.getStyle().clearDisplay();
			nationalityCell.getStyle().clearDisplay();
			security_social_num.addStyleName(style.nssWidht());
		} else {
			security_social_num.removeStyleName(style.nssWidht());
			nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			nationalityCell.getStyle().setDisplay(Display.NONE);
			nationality.setValue("ESPA\u00D1A");
		}
	}

	// -------------------------------------------------- GOOGLE MAPS PLACES -------------------------------------------------------

	private static void loadMapsPlacesAPI(Runnable onLoad) {
		String version = "3";

		String otherParms = "&key=AIzaSyDpG4n4z6L7xP_pRmeelNfSb-StmZOHBR4&libraries=places&lenguage=es";

		AjaxLoaderOptions settings = AjaxLoaderOptions.newInstance();
		settings.setOtherParms(otherParms);
		AjaxLoader.init();
		AjaxLoader.loadApi("maps", version, onLoad, settings);
	}

	public final native void initializeAutocomplete(Element element, Consumer callback) /*-{
		var autocomplete;
	
		// Create the autocomplete object, restricting the search to geographical location types.
		autocomplete = new $wnd.google.maps.places.Autocomplete(element, {types: ['geocode']});
	
		// When the user selects an address from the dropdown, accept this address.
		listener = function() {
			callback.@com.esferalia.aon.gwt.payroll.client.Consumer::accept(Lcom/google/gwt/core/client/JavaScriptObject;)(autocomplete.getPlace());
		}
	
		autocomplete.addListener('place_changed', listener);	
	
	}-*/;

	private String getIso2(String country) {
		for (int i = 0; i < Country.values().length; i++) {
			if (Country.values()[i].getName() == country)
				return Country.values()[i].getIso2();
		}
		return null;
	}

}
