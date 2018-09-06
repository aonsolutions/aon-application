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
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeNewDraft extends Composite implements ContextMenuHandler {
	
	// ------------------------------------------------ GOOGLE MAP ADDRESS INFO --------------------------------------------------
	
	private static class Place extends JavaScriptObject{
		
		protected Place() {
		}
		
		public final AddressComponent get(String type){
			for (int i=0; i<getAddressComponents().length(); i++) {
				if(getAddressComponents().get(i).getTypes()[0] == type)
					return getAddressComponents().get(i);
			}
			return null;
		} 
		
		// ----------------------------------- JSNI (Native JavaScript Methods)
		public final native JsArray<AddressComponent> getAddressComponents() /*-{
			return this.address_components;
		}-*/;
		
	}
	
	private static class AddressComponent extends JavaScriptObject{
		
		protected AddressComponent() {
		}
		
		
		// ----------------------------------- JSNI (Native JavaScript Methods)
		public final native String[] getTypes() /*-{
			return this.types;
		}-*/;

		public final native String  getLongName() /*-{
			return this.long_name;
		}-*/;
	
		public final native String  getShortName() /*-{
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
		String backgroundColorFirstColumn();
		String hide();
		String passDocumentStyle();
		String passNSSStyle();
		String passNationalityStyle();
		String nieDocumentStyle();
		String nieNSSStyle();
		String nieNationalityStyle();
		String dniDocumentStyle();
		String dniNSSStyle();
	}
	
	@UiField
	Button saveButton;
	
	// TABLA DATOS EMPLEADO
	
	@UiField
	Grid employeeDataTable;
	
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
	HorizontalPanel nationalityPanel;
	
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
	
	//TABLA DATOS CONTRATO
	
	@UiField
	ListBox ssRegimeType;
	
	@UiField
	Grid contractDataTable;
	
	@UiField
	TextBox activity;
	
	@UiField
	HorizontalPanel quoteAccountPanel;
	
	@UiField
	TextBox quotationAccount;
	
	@UiField
	TextBox workplace;
	
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
	HorizontalPanel seniorityDatePanel;
	
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
	
	
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
	private EmployeeNewDraftObject employeeNewDraftObject;
	private ContractType contract_type;
	private EmployeeInfoDataBase employeeInfo;
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	
	public EmployeeNewDraft() {
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for(Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);
		
		//Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		//Init Google Maps Places API
//		loadMapsPlacesAPI(()->{initializeAutocomplete(this.address.getElement(), (js)->{
//				Place place = js.cast();
//				
//				employeeNewDraftObject.setEmployeeAddress(place.get("route").getLongName());
//				employeeNewDraftObject.setEmployeeAddressNumber(place.get("street_number").getShortName());
//				employeeNewDraftObject.setEmployeeAddressZip(place.get("postal_code").getLongName());
//				employeeNewDraftObject.setEmployeeAddressLacality(place.get("locality").getLongName());
//				employeeNewDraftObject.setEmployeeAddressProvince(place.get("administrative_area_level_2").getLongName());
//				
////				Window.alert("route :"+place.get("route").getLongName()); //Calle
////				Window.alert("route :"+place.get("route").getShortName());
////				Window.alert("street_number :"+place.get("street_number").getShortName()); //Numero domicilio
////				Window.alert("locality :"+place.get("locality").getLongName()); //Localidad
////				Window.alert("administrative_area_level_2 :"+place.get("administrative_area_level_2").getLongName()); //Provincia
////				Window.alert("administrative_area_level_1 :"+place.get("administrative_area_level_1").getLongName()); //Comunidad autonoma
////				Window.alert("country :"+place.get("country").getShortName()); //Codigo pais
////				Window.alert("country :"+place.get("country").getLongName()); //Pais
////				Window.alert("postal_code :"+place.get("postal_code").getLongName()); //Codigo postal
//			}); 
//		});
		
		this.document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> document) {
				String document_type_str = checkDocumentType(document.getValue());
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
				for(Agreement a : agreements){
					if(a.getId() > 0 && a.getDescription() == agreementName){
						Set<Level> levels = a.getLevels();
						for(Level levelRecord : levels){
							Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
							for(String categoryRecord : categories){
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
				if(contractType.getSelectedIndex() != 0){
					String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
					contractTypeId = Integer.parseInt(contract_type_id_str);
				}
				
				List<ModelRecord> contractTypeModels = EmployeeNewDraft.this.contract_type.getModelsContractType(contractTypeId);
				for(ModelRecord m : contractTypeModels){
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

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("findPerson")
	void onFindPersonChangeValue(ValueChangeEvent<String> event) {
//	void onFindPersonChangeValue(ChangeEvent event) {
//		if(findPerson.getSelectedIndex() != 0){
//			String person = findPerson.getSelectedItemText();
//			Integer personIdNum = Integer.parseInt(person.split(" ")[0]);
//			this.personId.setValue(personIdNum.toString());
//			employeeNewDraftObject.setContractTableId(personIdNum);
//		}
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
		if(this.ssRegimeType.getSelectedIndex() == 1){
			showRetaForm();
			employeeNewDraftObject.setSSRegime(3);
		}else{
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
		if (contractType.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractType(null);
			employeeNewDraftObject.setContractModel(null);
		}else{
			String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
			employeeNewDraftObject.setContractType(contract_type_id_str);
		}
	}
	
	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		if (contractType.getSelectedIndex() == 0 || modality.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractModel(null);
		}else{
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
		if(this.agreement.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractAgreementId(null);
			employeeNewDraftObject.setContractAgreementDescription(null);
			employeeNewDraftObject.setContractAgreementLevelId(null);
			employeeNewDraftObject.setContractAgreementLevelDescription(null);
		}else{
			Integer agreementId = employeeNewDraftObject.getAgreementId(this.agreement.getSelectedItemText());
			employeeNewDraftObject.setContractAgreementId(agreementId);
			employeeNewDraftObject.setContractAgreementDescription(this.agreement.getSelectedItemText());
		}
	}
	
	@UiHandler("level")
	void onContractAgreementLevelChangeValue(ChangeEvent event) {
		if(this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractAgreementLevelId(null);
			employeeNewDraftObject.setContractAgreementLevelDescription(null);
		}else{
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
		if (quote_group.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractQuoteGroup(null);
		}else{
			String quoteGroup = getQuoteByIndex(quote_group.getSelectedIndex());
			employeeNewDraftObject.setContractQuoteGroup(quoteGroup);
		}
	}
	
	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		if (occupation.getSelectedIndex() == 0){
			employeeNewDraftObject.setContractOccupation(null);
		}else{
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
		if(personId.getValue() == ""){
			//Nationality
			String countryIso2 = getIso2(nationality.getValue());
			employeeNewDraftObject.setNationality(countryIso2);
			
			//Gender
			employeeNewDraftObject.setEmployeeGender(gender.getSelectedIndex());
		}
		
		if(payMethod.getSelectedItemText() == "TRANSFERENCIA" && (account.getValue() == "" || bic.getValue() == "")){
			WarningDialog dialog = new WarningDialog("Aviso", "HAY QUE RELLENAR LA CUENTA Y EL BIC");
			dialog.center();
			dialog.show();
		}else if(!checkIfUpdateIsPossible()){
			WarningDialog dialog = new WarningDialog("Aviso", "LOS CAMPOS EN COLOR AZUL SON OBLIGATORIOS");
			dialog.center();
			dialog.show();
		}else{
			//Window.alert("GUARDAR!");
			
			Integer journeyTypeIndex = this.journeyType.getSelectedIndex();
			Boolean journey_type = (journeyTypeIndex == 0) ? true : false;
			employeeNewDraftObject.setContractJourneyType(journey_type);
			
//			if(personId.getValue() == "")
//				Window.alert(employeeNewDraftObject.getNewEmployeeInfo());
//			else{
//				Window.alert(personId.getValue() + " -> " + findPerson.getSelectedItemText());
//			}
			
//			Window.alert(employeeNewDraftObject.getNewEmployeeContractInfo());
			
			employeeNewDraftObject.createEmployeeContract(
					r -> {
						setEmployeeNewDraftObject(employeeNewDraftObject);
					},
					t -> {}
				);
		}
	}

	private boolean checkIfUpdateIsPossible() {
		if(personId.getValue() == ""){
			if(name.getValue() == "" || first_surname.getValue() == "" || 
					document.getValue() == "" || security_social_num.getValue() == "" ||
					address.getValue() == "" || addressNum.getValue() == "" ||
					addressZip.getValue() == "" || addressCity.getValue() == "" || addressProvince.getValue() == ""){
				//Window.alert("Empleado nuevo faltan campos");
				return false;
			}
		}
		
		if(ssRegimeType.getSelectedIndex() == 1){
			if(strat_date.getValue() == null){
				//Window.alert("Existe empleado, pero faltan campos contrato en contrato RETA");
				return false;
			}
		}else{
			if(quotationAccount.getValue() == "" || activity.getValue() == "" || workplace.getValue() == "" || contractType.getSelectedIndex() == 0 ||
					strat_date.getValue() == null){
				//Window.alert("Existe empleado, pero faltan campos contrato");
				return false;
			}
		}
		
		
		return true;
	}

// -------------------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------
	
	/**
	 * Metodo al que se llama cada vez que se quiere iniciar el calendario.
	 * @param calendar : objeto que contiene la informacion que debe mostrar el calendario
	 */
	public void setEmployeeNewDraftObject(EmployeeNewDraftObject employeeDraft) {
		this.contract_type = new ContractType();
		this.employeeNewDraftObject = employeeDraft;
		
		this.employeeNewDraftObject.getWorkplaceEmployees(
				s -> {
					initializeView();
				}, 
				f ->{}
		);
	}

	private void initializeView() {
		resetElements();
		initializeListBox();
		initializeRestElements();
		hidePersonForm();
		hideRetaForm();
		addStyleFirstColumnTable();
		
		//TODO: BORRAR CUANDO FUNCIONE PLACES DE GOOGLE. DE MOMENTO METER AUTOMATICAMENTE MISMA DIRECCION PARA PRUEBAS
//		initializeAddresss();
	}

//	private void initializeAddresss() {
//		employeeNewDraftObject.setEmployeeAddress("Jose Maria Llanos");
//		employeeNewDraftObject.setEmployeeAddressNumber("36");
//		employeeNewDraftObject.setEmployeeAddressZip("28822");
//		employeeNewDraftObject.setEmployeeAddressLacality("Coslada");
//		employeeNewDraftObject.setEmployeeAddressProvince("Madrid");
//	}

	private void resetElements() {
		//Clear employee elements
		this.personId.setValue("");
//		this.findPerson.clear();
		this.document.setValue("");
		this.name.setValue("");
		this.first_surname.setValue("");
		this.second_surname.setValue("");
		this.birth_date.setValue(null);
		this.gender.clear();
		this.security_social_num.setValue("");
		this.address.setValue("");
		this.phone.setValue("");
		this.mobile.setValue("");
		this.email.setValue("");
		this.payMethod.clear();
		this.account.setValue("");
		this.bic.setValue("");
		
		//Clear contract elements
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
		//BUSCAR EMPLEADO
//		WorkplaceEmployees workplaceEmployees = employeeNewDraftObject.getWorkplaceEmployees();
//		this.findPerson.addItem("-");
//		for(EmployeeInfo e : workplaceEmployees.getWorkplaceEmployees())
//			this.findPerson.addItem(e.getEmployeeId() + " - " + e.getSurName() + ", " + e.getName());
		
		WorkplaceEmployees workplaceEmployees = employeeNewDraftObject.getWorkplaceEmployees();
		List<String> employees = new ArrayList<>();
		for(EmployeeInfo e : workplaceEmployees.getWorkplaceEmployees())
			employees.add(e.getEmployeeId() + " - " + e.getSurName() + ", " + e.getName());
		
		MultiWordSuggestOracle orcl = (MultiWordSuggestOracle) findPerson.getSuggestOracle();
		orcl.addAll(employees);
		
		//SEXO
		this.gender.addItem("Hombre");
		this.gender.addItem("Mujer");
		this.gender.addItem("Desconocido");
		
		//TIPO DE PAGO
		this.payMethod.addItem("-");
		this.payMethod.addItem("EFECTIVO");
		this.payMethod.addItem("GIRO");
		this.payMethod.addItem("CHEQUE");
		this.payMethod.addItem("TRANSFERENCIA");
		
		//TIPO DE COTIZACIÓN
		this.ssRegimeType.addItem("COMUN");
		this.ssRegimeType.addItem("RETA");
		this.ssRegimeType.addItem("SOCIOS COOP");
		this.ssRegimeType.addItem("JUBILACION ACTIVA");
		this.ssRegimeType.addItem("GARANTIA JUVENIL");
		
		//TIPO DE CONTRATO
		this.contractType.addItem("-");
		for(Entry<Integer, ContractTypeRecord> entry : this.contract_type.getContractTypes().entrySet()){
			this.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());
		}
		
		//MODALIDAD
		this.modality.addItem("-");
		
		//CONVENIO
		this.agreement.addItem("-");
		List<Agreement> agreements = employeeNewDraftObject.getActiveAgreements();
		for(Agreement a : agreements){
				this.agreement.addItem(a.getDescription());
		}
		
		//NIVELES/CATEGORIA
		this.level.addItem("-");
		
		//GRUPO DE COTIZACION
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
		
		//OCUPACION
		this.occupation.addItem("-");
		this.occupation.addItem("a. Personal en trabajos exclusivos de oficina");
		this.occupation.addItem("b. Tipo de cotizaci" + String.valueOf("\u00F3") + "n para todos los trabajadores que deban desplazarse habitalmente");
		this.occupation.addItem("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci" + String.valueOf("\u00F3") + "n en general");
		this.occupation.addItem("e. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de pasajeros en general (taxis, autom" + String.valueOf("\u00F3") + "viles, autobuses, etc)");
		this.occupation.addItem("f. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de mercanc" + String.valueOf("\u00ED") + "as que tengan una capacidad de carga " + String.valueOf("\u00FA") + "til superior a 3,5 Tm.");
		this.occupation.addItem("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles");
		this.occupation.addItem("h. Vigilantes, guardas, guardas jurados y personal de seguridad");
		
		//TIPO DE JORNADA
		this.journeyType.addItem("Tiempo Completo");
		this.journeyType.addItem("Tiempo Parcial");
	}
	
	private void initializeRestElements() {
		this.workplace.setValue(this.employeeNewDraftObject.getWorkplaceName());
		employeeNewDraftObject.setWorkplace(workplace.getValue());
		employeeNewDraftObject.setSSRegime(0);
		
		this.activity.setValue(this.employeeNewDraftObject.getWorkplaceActivity());
		employeeNewDraftObject.setEnterprise_Activity(activity.getValue());
		
		this.quotationAccount.setValue(this.employeeNewDraftObject.getWorkplaceCCC());
		employeeNewDraftObject.setContractQuoteAccount(quotationAccount.getValue());
		
		Integer agreementIndex = this.employeeNewDraftObject.getAgreementIndex(this.employeeNewDraftObject.getWorkplaceAgreement());
		this.agreement.setSelectedIndex(agreementIndex + 1);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.agreement);
	}

	private void showPersonForm() {
		this.employeeDataTable.getRowFormatter().addStyleName(0, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(1, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(2, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(3, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(4, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(5, style.hide());
		this.employeeDataTable.getRowFormatter().removeStyleName(6, style.hide());
	}
	
	private void hidePersonForm() {
		this.employeeDataTable.getRowFormatter().removeStyleName(0, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(1, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(2, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(3, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(4, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(5, style.hide());
		this.employeeDataTable.getRowFormatter().addStyleName(6, style.hide());
	}
	
	private void showRetaForm() {
		this.contractDataTable.getRowFormatter().addStyleName(1, style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(2, style.hide());
		this.contractType.addStyleName(style.hide());
		this.contractTypeFreelance.removeStyleName(style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(4, style.hide());
		this.seniorityDatePanel.addStyleName(style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(7, style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(8, style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(9, style.hide());
	}
	
	private void hideRetaForm() {
		this.contractDataTable.getRowFormatter().removeStyleName(1, style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(2, style.hide());
		this.contractType.removeStyleName(style.hide());
		this.contractTypeFreelance.addStyleName(style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(4, style.hide());
		this.seniorityDatePanel.removeStyleName(style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(7, style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(8, style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(9, style.hide());
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

	private void addStyleFirstColumnTable() {
		contractDataTable.getColumnFormatter().addStyleName(0, style.backgroundColorFirstColumn());
		employeeDataTable.getColumnFormatter().addStyleName(0, style.backgroundColorFirstColumn());
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub	
	}
	
	// --------------------------------------------------- CHECK DOCUMENT TYPE -------------------------------------------------------
	
	private static String checkDocumentType(String document){
		
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");
		
		if(dniPattern.test(document.toUpperCase()))
			return "DNI";
		else if(niePattern.test(document.toUpperCase()))
			return "NIE";
		else if(cifPattern.test(document.toUpperCase()))
			return"CIF";
		else
			return "Pasaporte";
	}
	
	private void showNationality(String document_type_str) {
		if(document_type_str == "Pasaporte"){
			this.document.addStyleName(style.passDocumentStyle());
			this.nationality.addStyleName(style.passNationalityStyle());
			this.security_social_num.addStyleName(style.passNSSStyle());
		}else{
			this.document.removeStyleName(style.passDocumentStyle());
			this.nationality.removeStyleName(style.passNationalityStyle());
			this.security_social_num.removeStyleName(style.passNSSStyle());
		}
		
		if(document_type_str == "NIE"){
			this.document.addStyleName(style.nieDocumentStyle());
			this.nationality.addStyleName(style.nieNationalityStyle());
			this.security_social_num.addStyleName(style.nieNSSStyle());
		}else{
			this.document.removeStyleName(style.nieDocumentStyle());
			this.nationality.removeStyleName(style.nieNationalityStyle());
			this.security_social_num.removeStyleName(style.nieNSSStyle());
		}
		
		if(document_type_str == "DNI"){
			this.document.addStyleName(style.dniDocumentStyle());
			this.security_social_num.addStyleName(style.dniNSSStyle());
		}else{
			this.document.removeStyleName(style.dniDocumentStyle());
			this.security_social_num.removeStyleName(style.dniNSSStyle());
		}
		
		if(document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE")
			nationalityPanel.removeStyleName(style.hide());
		else{
			nationalityPanel.addStyleName(style.hide());
			nationality.setValue("ESPA\u00D1A");
		}
	}
	
	// ---------------------------------------------------- GOOGLE MAPS PLACES -------------------------------------------------------
	
	private static void loadMapsPlacesAPI(Runnable onLoad){
		String version = "3";

		//Maps JavaScript API_KEY = AIzaSyA_4HinrQ1eWKFwyCJFmD6VE8uzMxtQS3Q
		//Place API_KEY = AIzaSyA_4HinrQ1eWKFwyCJFmD6VE8uzMxtQS3Q
		String otherParms = "&key=AIzaSyDpG4n4z6L7xP_pRmeelNfSb-StmZOHBR4&libraries=places&lenguage=es";
		
		AjaxLoaderOptions settings = AjaxLoaderOptions.newInstance();
		settings.setOtherParms(otherParms);
		AjaxLoader.init();
		AjaxLoader.loadApi("maps", version, onLoad, settings);
	}
	
	public final native  void initializeAutocomplete(Element element, Consumer callback) /*-{
		var autocomplete;
		
		// Create the autocomplete object, restricting the search to geographical location types.
	    autocomplete = new $wnd.google.maps.places.Autocomplete(
	        element,
			{types: ['geocode']}
		);
	
		// When the user selects an address from the dropdown, accept this address.
		listener = function() {
	  		callback.@com.esferalia.aon.gwt.payroll.client.Consumer::accept(Lcom/google/gwt/core/client/JavaScriptObject;)(autocomplete.getPlace());
	  	}
		
		autocomplete.addListener('place_changed', listener);	
		
	}-*/;
	
	private String getIso2(String country) {
		for(int i=0; i<Country.values().length; i++){
			if(Country.values()[i].getName() == country)
				return Country.values()[i].getIso2();
		}
		return null;		
	}
	
}
