package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
//import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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

public class EmployeeDraft extends Composite implements ContextMenuHandler {
	
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

	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeDraft> {
	}
	
	// -------------------------------------------------- UiFields --------------------------------------------------
			
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String backgroundColorFirstColumn();
		String hide();
	}
	
	@UiField
	Button saveButton;
	
	//TABLA DATOS CONTRATO
	
	@UiField
	Grid contractDataTable;
	
	@UiField
	TextBox workplace;
	
	@UiField
	HorizontalPanel quoteAccountPanel;
	
	@UiField
	TextBox quotationAccount;
	
	@UiField
	HorizontalPanel activityPanel;
	
	@UiField
	TextBox activity;
	
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
	
	// TABLA DATOS EMPLEADO
	
	@UiField
	Grid employeeDataTable;
	
	@UiField
	TextBox document;
	
	@UiField
	Label document_type;
	
	@UiField
	HorizontalPanel nationalityPanel;
	
	@UiField(provided = true)
	SuggestBox nationality;
	
	@UiField
	TextBox name;
	
	@UiField
	Label firstSurnameLabel;
	
	@UiField
	TextBox first_surname;
	
	@UiField
	TextBox second_surname;
	
	@UiField
	DateBoxEx birth_date;
	
	@UiField
	Label age;
	
	@UiField
	ListBox gender;
	
	@UiField
	TextBox security_social_num;
	
	@UiField
	TextBox address;
	
	@UiField
	TextBox phone;
	
	@UiField
	TextBox mobile;
	
	@UiField
	TextBox email;
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
	private EmployeeDraftObject employeeDraftObject;
	private ContractType contract_type;
	private EmployeeInfoDataBase employeeInfo;
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	
	public EmployeeDraft() {
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for(Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);
		
		//Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		//Init Google Maps Places API
		loadMapsPlacesAPI(()->{initializeAutocomplete(this.address.getElement(), (js)->{
				Place place = js.cast();
				
				employeeDraftObject.setEmployeeAddress(place.get("route").getLongName());
				employeeDraftObject.setEmployeeAddressNumber(place.get("street_number").getShortName());
				employeeDraftObject.setEmployeeAddressZip(place.get("postal_code").getLongName());
				employeeDraftObject.setEmployeeAddressLacality(place.get("locality").getLongName());
				employeeDraftObject.setEmployeeAddressProvince(place.get("administrative_area_level_2").getLongName());
				
//				Window.alert("route :"+place.get("route").getLongName()); //Calle
//				Window.alert("route :"+place.get("route").getShortName());
//				Window.alert("street_number :"+place.get("street_number").getShortName()); //Numero domicilio
//				Window.alert("locality :"+place.get("locality").getLongName()); //Localidad
//				Window.alert("administrative_area_level_2 :"+place.get("administrative_area_level_2").getLongName()); //Provincia
//				Window.alert("administrative_area_level_1 :"+place.get("administrative_area_level_1").getLongName()); //Comunidad autonoma
//				Window.alert("country :"+place.get("country").getShortName()); //Codigo pais
//				Window.alert("country :"+place.get("country").getLongName()); //Pais
//				Window.alert("postal_code :"+place.get("postal_code").getLongName()); //Codigo postal
			}); 
		});
		
		this.document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> document) {
				String document_type_str = checkDocumentType(document.getValue());
				document_type.setText(document_type_str);
				
				showNationality(document_type_str);
			}
		});
		
//		this.agreement.addChangeHandler(new ChangeHandler() {
//			
//			@Override
//			public void onChange(ChangeEvent event) {
//				level.clear();
//				String agreementName = agreement.getSelectedItemText();
//				List<Agreement> agreements = employeeDraftObject.getAgreements();
//				level.addItem("-");
//				for(Agreement a : agreements){
//					if(a.getId() > 0 && a.getDescription() == agreementName){
//						Set<Level> levels = a.getLevels();
//						for(Level levelRecord : levels){
//							Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
//							for(String categoryRecord : categories){
//								level.addItem(levelRecord.getDescription() + " - " + categoryRecord);
//							}
//						}
//					}
//				}
//			}
//		});
		
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
				
				List<ModelRecord> contractTypeModels = EmployeeDraft.this.contract_type.getModelsContractType(contractTypeId);
				for(ModelRecord m : contractTypeModels){
					modality.addItem(m.getModelDescription());
				}
				
			}
		});
		
		

	}

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("quotationAccount")
	void onQuoteAccountChangeValue(ValueChangeEvent<String> event) {
		employeeDraftObject.setContractQuoteAccount(quotationAccount.getValue());
	}
	
	@UiHandler("contractType")
	void onContractTypeChangeValue(ChangeEvent event) {
		if (contractType.getSelectedIndex() == 0){
			employeeDraftObject.setContractType(null);
			employeeDraftObject.setContractModel(null);
		}else{
			String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
			employeeDraftObject.setContractType(contract_type_id_str);
		}
	}
	
	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		if (contractType.getSelectedIndex() == 0 || modality.getSelectedIndex() == 0){
			employeeDraftObject.setContractModel(null);
		}else{
			String contract_type_id_str = contractType.getSelectedItemText().split(" -")[0];
			Integer contractTypeId = Integer.parseInt(contract_type_id_str);
			
			String contractModelDescription = modality.getSelectedItemText();
			Integer contractModelEnum = this.contract_type.getContractModelId(contractTypeId, contractModelDescription);
			employeeDraftObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
		}
	}
	
	@UiHandler("strat_date")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		employeeDraftObject.setContractStartDate(strat_date.getValue());
	}
	
	@UiHandler("end_date")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		employeeDraftObject.setContractEndDate(end_date.getValue());
	}
	
	@UiHandler("seniority_date")
	void onSeniorityDateChangeValue(ValueChangeEvent<Date> event) {
		employeeDraftObject.setContractSeniorityDate(seniority_date.getValue());
	}
	
//	@UiHandler("agreement")
//	void onContractAgreementChangeValue(ChangeEvent event) {
//		if(this.agreement.getSelectedIndex() == 0){
//			employeeDraftObject.setContractAgreementId(null);
//			employeeDraftObject.setContractAgreementDescription(null);
//			employeeDraftObject.setContractAgreementLevelId(null);
//			employeeDraftObject.setContractAgreementLevelDescription(null);
//		}else{
//			Integer agreementId = employeeDraftObject.getAgreementId(this.agreement.getSelectedItemText());
//			employeeDraftObject.setContractAgreementId(agreementId);
//			employeeDraftObject.setContractAgreementDescription(this.agreement.getSelectedItemText());
//		}
//	}
	
//	@UiHandler("level")
//	void onContractAgreementLevelChangeValue(ChangeEvent event) {
//		if(this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0){
//			employeeDraftObject.setContractAgreementLevelId(null);
//			employeeDraftObject.setContractAgreementLevelDescription(null);
//		}else{
//			Integer agreementLevelId = employeeDraftObject.getAgreementLevelId(this.agreement.getSelectedItemText(), this.level.getSelectedItemText());
//			employeeDraftObject.setContractAgreementLevelId(agreementLevelId);
//			employeeDraftObject.setContractAgreementLevelDescription(this.level.getSelectedItemText());
//			String levelDescription = (this.level.getSelectedItemText() == null || this.level.getSelectedItemText() == "-") ? null : this.level.getSelectedItemText().split("- ")[1];
//			this.category.setValue(levelDescription);
//			employeeDraftObject.setContractCategory(levelDescription);
//		}
//	}
	
	@UiHandler("category")
	void onCategoryChangeValue(ChangeEvent event) {
		employeeDraftObject.setContractCategory(category.getValue());
	}
	
	@UiHandler("quote_group")
	void onQuoteGroupChangeValue(ChangeEvent event) {
		if (quote_group.getSelectedIndex() == 0){
			employeeDraftObject.setContractQuoteGroup(null);
		}else{
			String quoteGroup = getQuoteByIndex(quote_group.getSelectedIndex());
			employeeDraftObject.setContractQuoteGroup(quoteGroup);
		}
	}
	
	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		if (occupation.getSelectedIndex() == 0){
			employeeDraftObject.setContractOccupation(null);
		}else{
			String contractOccupation = getOcupationByIndex(occupation.getSelectedIndex());
			employeeDraftObject.setContractOccupation(contractOccupation);
		}
	}
	
	@UiHandler("document")
	void onDocumentChangeValue(ChangeEvent event) {
		String document_type_str = checkDocumentType(document.getValue());
		document_type.setText(document_type_str);
		
		employeeDraftObject.setEmployeeDocument(document.getValue());
		employeeDraftObject.setEmployeeDocumentType(document_type_str);
		
		showNationality(document_type_str);
	}

	@UiHandler("name")
	void onNameChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeName(name.getValue());
	}
	
	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeFirstSurname(first_surname.getValue());
	}
	
	@UiHandler("second_surname")
	void onSecondSurnameChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeSecondSurname(second_surname.getValue());
	}
	
	@UiHandler("birth_date")
	void onBithDateChangeValue(ValueChangeEvent<Date> event) {
		employeeDraftObject.setEmployeeBirthDate(birth_date.getValue());
	}
	
	@UiHandler("gender")
	void onGenderChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeGender(gender.getSelectedIndex());
	}
	
	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeSocialSecurityNum(security_social_num.getValue());
	}
	
	@UiHandler("phone")
	void onPhoneChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeePhone(phone.getValue());
	}
	
	@UiHandler("mobile")
	void onMobileChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeMobile(mobile.getValue());
	}
	
	@UiHandler("email")
	void onEmailChangeValue(ChangeEvent event) {
		employeeDraftObject.setEmployeeEmail(email.getValue());
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		String countryIso2 = getIso2(nationality.getValue());
		employeeDraftObject.setNationality(countryIso2);
		
		employeeDraftObject.updateEmployee(
			r -> {
				setEmployeeDraftObject(employeeDraftObject);
			},
			t -> {}
		);
	}

// -------------------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------
	
	/**
	 * Metodo al que se llama cada vez que se quiere iniciar el calendario.
	 * @param calendar : objeto que contiene la informacion que debe mostrar el calendario
	 */
	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraft) {
		this.employeeDraftObject = employeeDraft;
		this.contract_type = new ContractType();
		
		
		this.employeeDraftObject.initializeEmployee(
				r -> { 
					employeeInfo = employeeDraftObject.getEmployeeInfo();
					initializeView(); 
				},
				t -> {});
	}

	private void initializeView() {
		resetElements();
		initializeListBox();
		initializeSuugestBox();
		if(employeeInfo.getContract_type() == null){
			fillContractFreelancerTable();
			hideElementsFreelancerTable();
		}else{
			fillContractTable();
			showElementsContractTable();
		}
		fillEmployeeTable();
		addStyleFirstColumnTable();
	}

	private void resetElements() {
		//Clear contract elements
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
		
		//Clear employee elements
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
	}

	private void initializeListBox() {
		//TIPO DE CONTRATO
		this.contractType.addItem("-");
		for(Entry<Integer, ContractTypeRecord> entry : this.contract_type.getContractTypes().entrySet()){
			this.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());
		}
		
		//MODALIDAD
		this.modality.addItem("-");
		
		//CONVENIO
		this.agreement.addItem("-");
		
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
		
		//SEXO
		this.gender.addItem("Hombre");
		this.gender.addItem("Mujer");
		this.gender.addItem("Desconocido");
	}
	
	private void initializeSuugestBox() {
		List<Agreement> agreements = employeeDraftObject.getActiveAgreements();
		for(Agreement a : agreements){
				this.agreement.addItem(a.getDescription());
		}	
	}
	
	private void hideElementsFreelancerTable() {
		this.quoteAccountPanel.addStyleName(style.hide());
		this.activityPanel.addStyleName(style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(2, style.hide());
		this.seniorityDatePanel.addStyleName(style.hide());
		this.contractType.addStyleName(style.hide());
		this.contractTypeFreelance.removeStyleName(style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(5, style.hide());
		this.contractDataTable.getRowFormatter().addStyleName(6, style.hide());
	}

	private void fillContractFreelancerTable() {
		String workplace_description =  (employeeInfo.getWorkplace() == null) ? "" : employeeInfo.getWorkplace();
		this.workplace.setValue(workplace_description);
		this.workplace.setEnabled(false);
		
		this.contractTypeFreelance.setValue("R"+String.valueOf("\u00E9")+"gimen especial de trabajadores aut"+String.valueOf("\u00F3")+"nomos");
		this.contractTypeFreelance.setEnabled(false);
		
		Date start_date = (employeeInfo.getStart_date() == null) ? null : employeeInfo.getStart_date();
		if(start_date != null) this.strat_date.setValue(start_date);
		
		Date end_date = (employeeInfo.getEnd_date() == null) ? null : employeeInfo.getEnd_date();
		if(end_date != null) this.end_date.setValue(end_date);
		
		Date seniority_date = (employeeInfo.getSeniority_date() == null) ? null : employeeInfo.getSeniority_date();
		if(seniority_date != null) this.seniority_date.setValue(seniority_date);
		
//		Integer agreementIndex = this.employeeDraftObject.getAgreementIndex(employeeInfo.getAgreement());
//		this.agreement.setSelectedIndex(agreementIndex+1);
//		
//		String category_description = (employeeInfo.getCategory_description() == null) ? "" : employeeInfo.getCategory_description();
//		this.category.setText(category_description);
//		
//		String agreementName = agreement.getSelectedItemText();
//		Integer agremeentLevelCategoyIndex = getAgreementLevelCategoryIndex(agreementName, employeeInfo.getAgreement_level(), category_description);
//		getAgreementLevels(agreementName);
//		this.level.setSelectedIndex(agremeentLevelCategoyIndex+1);
	}
	
	private void showElementsContractTable() {
		this.quoteAccountPanel.removeStyleName(style.hide());
		this.activityPanel.removeStyleName(style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(2, style.hide());
		this.seniorityDatePanel.removeStyleName(style.hide());
		this.contractType.removeStyleName(style.hide());
		this.contractTypeFreelance.addStyleName(style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(5, style.hide());
		this.contractDataTable.getRowFormatter().removeStyleName(6, style.hide());
	}
	
	private void fillContractTable() {
		String workplace_description =  (employeeInfo.getWorkplace() == null) ? "" : employeeInfo.getWorkplace();
		this.workplace.setValue(workplace_description);
		this.workplace.setEnabled(false);
		
		String quote_account = (employeeInfo.getQuote_account() == null) ? "" : employeeInfo.getQuote_account();
		this.quotationAccount.setText(quote_account);
		
		String activity = (employeeInfo.getEnterprise_activity() == null) ? "" : employeeInfo.getEnterprise_activity();
		this.activity.setText(activity);
		this.activity.setEnabled(false);
		
		Integer contractTypeInt = (employeeInfo.getContract_type() == null) ? -1 : Integer.parseInt(employeeInfo.getContract_type());
		if(contractTypeInt != -1){
			Integer contractTypeIndex = this.contract_type.getContractTypeIndex(contractTypeInt);
			this.contractType.setSelectedIndex(contractTypeIndex+1);
			
			//fill list
			modality.clear();
			modality.addItem("-");
			List<ModelRecord> contractTypeModels = EmployeeDraft.this.contract_type.getModelsContractType(contractTypeInt);
			for(ModelRecord m : contractTypeModels)
				modality.addItem(m.getModelDescription());
			
			Integer modelIndex = this.contract_type.getContractModelIndex(contractTypeInt, employeeInfo.getContract_model());
			if(modelIndex != -1){
				this.modality.setSelectedIndex(modelIndex+1);
				this.category.setEnabled(true);
			}else{
				this.modality.setSelectedIndex(0);
				this.category.setEnabled(false);
			}
			
		}else{
			this.contractType.setSelectedIndex(0);
			this.category.setEnabled(false);
		}
		
		Date start_date = (employeeInfo.getStart_date() == null) ? null : employeeInfo.getStart_date();
		if(start_date != null) this.strat_date.setValue(start_date);
		
		Date end_date = (employeeInfo.getEnd_date() == null) ? null : employeeInfo.getEnd_date();
		if(end_date != null) this.end_date.setValue(end_date);
		
		Date seniority_date = (employeeInfo.getSeniority_date() == null) ? null : employeeInfo.getSeniority_date();
		if(seniority_date != null) this.seniority_date.setValue(seniority_date);
		
//		Integer agreementIndex = this.employeeDraftObject.getAgreementIndex(employeeInfo.getAgreement());
//		this.agreement.setSelectedIndex(agreementIndex+1);
//		
//		String category_description = (employeeInfo.getCategory_description() == null) ? "" : employeeInfo.getCategory_description();
//		this.category.setText(category_description);
//		
//		String agreementName = agreement.getSelectedItemText();
//		Integer agremeentLevelCategoyIndex = getAgreementLevelCategoryIndex(agreementName, employeeInfo.getAgreement_level(), category_description);
//		getAgreementLevels(agreementName);
//		this.level.setSelectedIndex(agremeentLevelCategoyIndex+1);
		
		Integer quote_groupIndex = (employeeInfo.getQuote_group() == null) ? 0 : Integer.parseInt(employeeInfo.getQuote_group());
		this.quote_group.setSelectedIndex(quote_groupIndex);
		
		Integer ocupationIndex = (employeeInfo.getOcupation() == null) ? 0 : getCharIndex(employeeInfo.getOcupation());
		this.occupation.setSelectedIndex(ocupationIndex);
	}
	
//	private Integer getAgreementLevelCategoryIndex(String agreementName, String agreement_level,
//			String category_description) {
//		if(category_description == null || category_description == "")
//			return -1;
//		
//		Integer result = 0;
//		List<Agreement> agreements = employeeDraftObject.getAgreements();
//		for(Agreement a : agreements){
//			if(a.getId() > 0 && a.getDescription() == agreementName){
//				Set<Level> levels = a.getLevels();
//				for(Level levelRecord : levels){
//					Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
//					for(String categoryRecord : categories){
//						if(levelRecord.getDescription() == agreement_level && categoryRecord == category_description)
//							return result;
//						else
//							result++;
//					}	
//				}
//			}
//		}
//		
//		result = 0;
//		for(Agreement a : agreements){
//			if(a.getId() > 0 && a.getDescription() == agreementName){
//				Set<Level> levels = a.getLevels();
//				for(Level levelRecord : levels){
//					Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
//					for(String categoryRecord : categories){
//						if(levelRecord.getDescription() == agreement_level)
//							return result;
//						else
//							result++;
//					}	
//				}
//			}
//		}
//		return -1;
//	}

//	private void getAgreementLevels(String agreementName) {
//		level.clear();
//		List<Agreement> agreements = employeeDraftObject.getAgreements();
//		level.addItem("-");
//		for(Agreement a : agreements){
//			if(a.getId() > 0 && a.getDescription() == agreementName){
//				Set<Level> levels = a.getLevels();
//				for(Level levelRecord : levels){
//					Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
//					for(String categoryRecord : categories){
//						level.addItem(levelRecord.getDescription() + " - " + categoryRecord);
//					}
//				}
//			}
//		}	
//	}

	private int getCharIndex(String ocupation) {
		switch (ocupation) {
		case "a":
			return 1;
		case "b":
			return 2;
		case "d":
			return 3;
		case "e":
			return 4;
		case "f":
			return 5;
		case "g":
			return 6;
		case "h":
			return 7;
		default:
			return 0;
		}
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

	private void fillEmployeeTable() {
		String document = (employeeInfo.getDocument() == null) ? "" : employeeInfo.getDocument();
		this.document.setText(document);
		String document_type_str = checkDocumentType(document);
		document_type.setText(document_type_str);
		showNationality(document_type_str);
		this.nationality.setText(Country.valueOf(employeeInfo.getNationality()).getName());

		String name = (employeeInfo.getName() == null) ? "" : employeeInfo.getName();
		this.name.setText(name);
		String first_surname = (employeeInfo.getFirst_surname() == null) ? "" : employeeInfo.getFirst_surname();
		this.first_surname.setText(first_surname);
		String second_surname = (employeeInfo.getSecond_surname() == null) ? "" : employeeInfo.getSecond_surname();
		this.second_surname.setText(second_surname);
		
		Date birth_date = (employeeInfo.getBirth_date() == null) ? null : employeeInfo.getBirth_date();
		this.birth_date.setValue(birth_date);
		Integer age = 0;
		if(birth_date != null)
			age = DateUtils.getYears(new Date(), birth_date);
		this.age.setText("("+ age.toString() + " a"+ String.valueOf("\u00F1") + "os)");
		
		Byte genderIndex = (employeeInfo.getGender() == null) ? 0 : employeeInfo.getGender();
		this.gender.setSelectedIndex(genderIndex);
		
		String security_social_num = (employeeInfo.getSocial_security_num() == null) ? "" : employeeInfo.getSocial_security_num();
		this.security_social_num.setText(security_social_num);
		
		String address = (employeeInfo.getAddress() == null) ? "" : employeeInfo.getAddress();
		String address_number = (employeeInfo.getAddress_number() == null) ? "" : employeeInfo.getAddress_number();
		String zip = (employeeInfo.getZip_code() == null) ? "" : employeeInfo.getZip_code();
		String locality = (employeeInfo.getLocality() == null) ? "" : employeeInfo.getLocality();
		String province = (employeeInfo.getProvince() == null) ? "" : employeeInfo.getProvince();
		this.address.setText(address + ", " + address_number + ", " + zip + ", " + locality + ", " + province);
		
		String phone =  (employeeInfo.getPhone() == null) ? "" : employeeInfo.getPhone();
		this.phone.setText(phone);
		String mobile =  (employeeInfo.getMobile() == null) ? "" : employeeInfo.getMobile();
		this.mobile.setText(mobile);
		String email =  (employeeInfo.getEmail() == null) ? "" : employeeInfo.getEmail();
		this.email.setText(email);
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

		String otherParms = "&key=AIzaSyCGKYI5xWMYvCUK59jwJbIWnTX5kEQn1mY&libraries=places&lenguage=es";
		
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
