package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Employee extends ResizeComposite implements ContextMenuHandler {

	// ------------------------------------------ GOOGLE MAP ADDRESS INFO (INACTIVO) --------------------------------------------------

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

	interface EmployeeDraftUiBinder extends UiBinder<Widget, Employee> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String nssWidht();
		String retaTopLabel();
		String warning();
		String journeyDurationWarning();
		String journeyDuration();
		String marginTop();
	}

	// TABLA DATOS CONTRATO
	
	@UiField
	TableElement contractDataTable;
	
	@UiField
	Button clear_employee;
	
	@UiField
	Label document_type;
	
	@UiField
	SuggestBox document;
	
	@UiField
	Label documentStatus;

	@UiField
	TableCellElement nationalityLabelCell;

	@UiField
	TableCellElement nationalityCell;

	@UiField(provided = true)
	SuggestBox nationality;
	
	@UiField
	SuggestBox security_social_num;
	
	@UiField
	Label ssNumberStatus;

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
	ListBox activityCCC;
	
	@UiField
	ListBox workplace;
	
	@UiField
	TableCellElement contractTypeNode;

	@UiField
	ListBox contractType;
	
	@UiField
	TableCellElement contractFreelancerNode;

	@UiField
	Label contractTypeFreelance;

	@UiField
	ListBox modality;

	@UiField
	DateBoxEx start_date;

	@UiField
	DateBoxEx end_date;

	@UiField
	Label seniority_date_label;

	@UiField
	DateBoxEx seniority_date;
	
	@UiField
	Label seniority_dateStatus;

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

	// TABLA DATOS EMPLEADO

	@UiField
	TableElement employeeDataTable;

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
	TextBox account;
	
	@UiField
	Label journeyDuration;

	// ------------------------------------------------------ VARIABLES DE LA CLASE -------------------------------------------------

	private EmployeeDraftObject employeeDraftObject;
	private ContractType contract_type;
	private EmployeeInfoDataBase employeeInfo;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public Employee() {
		//Initialize Nationality SuggestBox
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);

		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();

		// Init Google Maps Places API (INACTIVO)
		// initGoogleMapsPlaces();
	}

	private void initGoogleMapsPlaces() {
		loadMapsPlacesAPI(() -> {
			initializeAutocomplete(this.address.getElement(), (js) -> {
				Place place = js.cast();

				employeeDraftObject.setEmployeeAddress(place.get("route").getLongName());
				employeeDraftObject.setEmployeeAddressNumber(place.get("street_number").getShortName());
				employeeDraftObject.setEmployeeAddressZip(place.get("postal_code").getLongName());
//				employeeDraftObject.setEmployeeAddressLacality(place.get("locality").getLongName());
				employeeDraftObject.setEmployeeAddressProvince(place.get("administrative_area_level_2").getLongName());

				// Calle
				// Window.alert("route :"+place.get("route").getLongName());
				// Window.alert("route :"+place.get("route").getShortName());
				
				// Numero domicilio
				// Window.alert("street_number :"+place.get("street_number").getShortName()); 
				
				// Localidad
				// Window.alert("locality :"+place.get("locality").getLongName()); 
				
				// Provincia
				// Window.alert("administrative_area_level_2 :"+place.get("administrative_area_level_2").getLongName());
				
				// Comunidad autonoma
				// Window.alert("administrative_area_level_1 :"+place.get("administrative_area_level_1").getLongName());
				
				// Codigo pais
				// Window.alert("country :"+place.get("country").getShortName());
				
				// Pais
				// Window.alert("country :"+place.get("country").getLongName());
				
				// Codigo postal
				// Window.alert("postal_code :"+place.get("postal_code").getLongName()); 
			});
		});
	}

	
	// ------------------------------------------------------------------------
	//								UiHandlers
	// ------------------------------------------------------------------------
	
	// TABLA DATOS CONTRATO
	
	@UiHandler("clear_employee")
	void onClearEmployeeClick(ClickEvent event) {
		onClearEmployeeClick();
	}
	
	@UiHandler("document")
	void onDocumentChangeValue(SelectionEvent<Suggestion> event) {
		onEmployeeDocumentSuggestionChange();
	}
	
	@UiHandler("document")
	void onDocumentChangeValue(ValueChangeEvent<String> event) {
		onEmployeeDocumentChange();
	}
	
	@UiHandler("nationality")
	void onNationalityChangeValue(ValueChangeEvent<String> event) {
		onEmployeeNationalityChange();
	}
	
	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(SelectionEvent<Suggestion> event) {
		onEmployeeSSNumSuggestionChange(); 
	}
	
	@UiHandler("security_social_num")
	void onSocialSecurityNumChangeValue(ValueChangeEvent<String> event) {
		onEmployeeSSNumChange(); 
	}
	
	@UiHandler("name")
	void onNameChangeValue(SelectionEvent<Suggestion> event) {
		onEmployeeNameSuggestionChange();
	}
	
	@UiHandler("name")
	void onNameChangeValue(ValueChangeEvent<String> event) {
		onEmployeeNameChange();
	}
	
	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(SelectionEvent<Suggestion> event) {
		onEmployeeFirstSurnameSuggestionChange();
	}
	
	@UiHandler("first_surname")
	void onFirstSurnameChangeValue(ValueChangeEvent<String> event) {
		onEmployeeFirstSurnameChange();
	}
	
	@UiHandler("second_surname")
	void onSecondSurnameChangeValue(ChangeEvent event) {
		onEmployeeSecondSurnameChange();
	}
	
	@UiHandler("ssRegimeType")
	void onContractSSRegimenChangeValue(ChangeEvent event) {
		onContractSSRegimenChange();
	}

	@UiHandler("activityCCC")
	void onContractActivityCCCChangeValue(ChangeEvent event) {
		onContractActiviesCCCChange();
	}
	
	@UiHandler("workplace")
	void onContractWorkplaceChangeValue(ChangeEvent event) {
		onContractWorkplaceChange();
	}
	
	@UiHandler("contractType")
	void onContractTypeChangeValue(ChangeEvent event) {
		onContractTypeChange();
	}

	@UiHandler("modality")
	void onContractModelChangeValue(ChangeEvent event) {
		onContractModalityChange();
	}

	@UiHandler("start_date")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		onContractStartDateChange();
	}

	@UiHandler("end_date")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		onContractEndDateChange();
	}

	@UiHandler("seniority_date")
	void onSeniorityDateChangeValue(ValueChangeEvent<Date> event) {
		onContractSeniorityDateChange();
	}

	@UiHandler("agreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		onContractAgreementChange();
	}

	@UiHandler("level")
	void onContractAgreementLevelChangeValue(ChangeEvent event) {
		onContractAgreementLevelChange();
	}

	@UiHandler("category")
	void onCategoryChangeValue(ChangeEvent event) {
		onContractCategoryChange();
	}

	@UiHandler("quote_group")
	void onQuoteGroupChangeValue(ChangeEvent event) {
		onContractQuoteGroupChange();
	}

	@UiHandler("occupation")
	void onContractOccupationChangeValue(ChangeEvent event) {
		onContractOccupationChange();
	}

	@UiHandler("journeyType")
	void onContractJourneyTypeChangeValue(ChangeEvent event) {
		onContractJourneyTypeChange();
	}
	
	@UiHandler("journeyDuration")
	void onContractJourneyDurationClick(ClickEvent event) {
		onContractJourneyDurationClick();
	}
	
	// TABLA DATOS EMPLEADO
	
	@UiHandler("birth_date")
	void onBithDateChangeValue(ValueChangeEvent<Date> event) {
		onEmployeeBirthDateChange();
	}

	@UiHandler("gender")
	void onGenderChangeValue(ChangeEvent event) {
		onEmployeeGenderChange();
	}
	
	@UiHandler("street_type")
	void onStreetTypeChangeValue(ChangeEvent event) {
		onEmployeeStreetTypeChange();
	}

	@UiHandler("address")
	void onAddressChangeValue(ValueChangeEvent<String> event) {
		onEmployeeAddressChange();
	}

	@UiHandler("addressNum")
	void onAddressNumChangeValue(ChangeEvent event) {
		onEmployeeAddressNumChange();
	}

	@UiHandler("addressZip")
	void onAddressZipChangeValue(ChangeEvent event) {
		onEmployeeAddressZipChange();
	}

	@UiHandler("addressCity")
	void onAddressCityChangeValue(ChangeEvent event) {
		onEmployeeAddressCityChange();
	}

	@UiHandler("addressProvince")
	void onAddressProvinceChangeValue(ChangeEvent event) {
		onEmployeeAddressProvinceChange();
	}

	@UiHandler("mobile")
	void onMobileChangeValue(ChangeEvent event) {
		onEmployeeMobileChange();
	}
	
	@UiHandler("phone")
	void onPhoneChangeValue(ChangeEvent event) {
		onEmployeePhoneChange();
	}

	@UiHandler("email")
	void onEmailChangeValue(ChangeEvent event) {
		onEmployeeEmailChange();
	}

	@UiHandler("payMethod")
	void onPayMethodChangeValue(ChangeEvent event) {
		onEmployeePayMethodChange();
	}

	@UiHandler("bic")
	void onBIClChangeValue(ChangeEvent event) {
		onEmployeeBICChange();
	}
	
	@UiHandler("account")
	void onAccountChangeValue(ChangeEvent event) {
		onEmployeeAccountChange();
	}

	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS CONTRATO
	
	public abstract void onClearEmployeeClick();
	public abstract void onEmployeeDocumentSuggestionChange();
	public abstract void onEmployeeDocumentChange();
	public abstract void onEmployeeNationalityChange();
	public abstract void onEmployeeSSNumSuggestionChange();
	public abstract void onEmployeeSSNumChange();
	public abstract void onEmployeeNameSuggestionChange();
	public abstract void onEmployeeNameChange();
	public abstract void onEmployeeFirstSurnameSuggestionChange();
	public abstract void onEmployeeFirstSurnameChange();
	public abstract void onEmployeeSecondSurnameChange();
	public abstract void onContractSSRegimenChange();
	public abstract void onContractActiviesCCCChange();
	public abstract void onContractWorkplaceChange();
	public abstract void onContractTypeChange();
	public abstract void onContractModalityChange();
	public abstract void onContractStartDateChange();
	public abstract void onContractEndDateChange();
	public abstract void onContractSeniorityDateChange();
	public abstract void onContractAgreementChange();
	public abstract void onContractAgreementLevelChange();
	public abstract void onContractCategoryChange();
	public abstract void onContractQuoteGroupChange();
	public abstract void onContractOccupationChange();
	public abstract void onContractJourneyTypeChange();
	public abstract void onContractJourneyDurationClick();
	
	// TABLA DATOS EMPLEADO
	
	public abstract void onEmployeeBirthDateChange();
	public abstract void onEmployeeGenderChange();
	public abstract void onEmployeeStreetTypeChange();
	public abstract void onEmployeeAddressChange();
	public abstract void onEmployeeAddressNumChange();
	public abstract void onEmployeeAddressZipChange();
	public abstract void onEmployeeAddressCityChange();
	public abstract void onEmployeeAddressProvinceChange();
	public abstract void onEmployeeMobileChange();
	public abstract void onEmployeePhoneChange();
	public abstract void onEmployeeEmailChange();
	public abstract void onEmployeePayMethodChange();
	public abstract void onEmployeeBICChange();
	public abstract void onEmployeeAccountChange();


	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------

	private void initializeView() {
		resetElements();
		initializeListBox();
		initDisplayElements();
	}

	public void resetElements() {
		// TABLA DATOS CONTRATO
		
		this.document.setValue("");
		this.security_social_num.setValue("");
		this.name.setValue("");
		this.first_surname.setValue("");
		this.second_surname.setValue("");
		this.ssRegimeType.clear();
		this.activityCCC.clear();
		this.workplace.clear();
		this.contractType.clear();
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
		this.journeyDuration.setText("");

		// TABLA DATOS EMPLEADO
		
		this.birth_date.setValue(null);
		this.gender.clear();
		this.street_type.clear();
		this.address.setValue("");
		this.addressNum.setValue("");
		this.addressZip.setValue("");
		this.addressCity.setValue("");
		this.addressProvince.clear();
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
		this.ssRegimeType.addItem("COMUN");
		this.ssRegimeType.addItem("RETA");
		this.ssRegimeType.addItem("SOCIOS COOP");
		this.ssRegimeType.addItem("JUBILACION ACTIVA");
		this.ssRegimeType.addItem("GARANTIA JUVENIL");
		
		// MODALIDAD
		this.modality.addItem("-");

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
		
		// TABLA DATOS EMPLEADO
		
		// SEXO
		this.gender.addItem("Hombre");
		this.gender.addItem("Mujer");
		this.gender.addItem("Desconocido");
		
		//TIPO DE VIA
		for(int i=0; i<StreetType.values().length; i++){
			this.street_type.addItem(StreetType.values()[i].getDescription());
		}
		
		//PROVINCIA
		this.addressProvince.addItem("-");
		for(String province : ProvinceContract.getProvinces().values())
			this.addressProvince.addItem(province);

		
		// TIPO DE PAGO
		this.payMethod.addItem("-");
		this.payMethod.addItem("EFECTIVO");
		this.payMethod.addItem("GIRO");
		this.payMethod.addItem("CHEQUE");
		this.payMethod.addItem("TRANSFERENCIA");	
	}
	
	private void initDisplayElements() {
		this.contractDataTable.getRows().getItem(4).getStyle().clearDisplay();
		
		this.contractTypeNode.getStyle().clearDisplay();
		this.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(7).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(11).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(12).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
	}

	public void showElementsFreelancerTable() {
		this.contractDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		
		this.contractTypeNode.getStyle().setDisplay(Display.NONE);
		this.contractFreelancerNode.getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(7).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		this.contractDataTable.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(13).getStyle().clearDisplay();
	}
	
	public void hideElementsFreelancerTable() {
		this.contractDataTable.getRows().getItem(4).getStyle().clearDisplay();
		
		this.contractTypeNode.getStyle().clearDisplay();
		this.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
		
		this.contractDataTable.getRows().getItem(7).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(11).getStyle().clearDisplay();
		this.contractDataTable.getRows().getItem(12).getStyle().clearDisplay();
		
		this.contractDataTable.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
	}
	
	public void showElementsPartialTimeContract() {
		this.contractDataTable.getRows().getItem(14).getStyle().clearDisplay();	
	}
	
	public void showElementsFullTimeContract() {
		this.contractDataTable.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

	// ---------------------------------------------- GOOGLE MAPS PLACES (INACTIVO) -------------------------------------------------

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

}
