package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonInvestAssetPanel extends SimplePanel {
	
	public static interface AonInvestAssetPanelCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private TextBox description = new TextBox();
	private ListBox activity = new ListBox();
	private ListBox type = new ListBox();
	private ListBox regimen = new ListBox();
	private DoubleBox iva = new DoubleBox();
	private DoubleBox retention = new DoubleBox();
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	
	private FlexTable propertiesTable = new FlexTable();
	
	private ListBox countries = new ListBox();
	private ListBox streetTypes = new ListBox();
	private TextBox address = new TextBox();
	private TextBox number = new TextBox();
	private TextBox letter = new TextBox();
	private TextBox floor = new TextBox();
	private TextBox hand = new TextBox();
	private TextBox municipality = new TextBox();
	private TextBox zip = new TextBox();
	private ListBox provinces = new ListBox();
	private CheckBox catastralInex = new CheckBox();
	private TextBox catastral = new TextBox();
	private DoubleBox floorPrice = new DoubleBox();
	private DoubleBox constructionPrice = new DoubleBox();
	
	private ListBox carType = new ListBox();
	private TextBox carRegistration = new TextBox();
	
	private TextBox phoneNumber = new TextBox();
	
	private TextBox aditonialDescription = new TextBox();
	
	public AonInvestAssetPanel(final String domainName,final int domain, final String user,final AonInvestAssetPanelCallback callback) {
		initializeCommonService();
		show(domainName,domain, user,new InvestAsset(),callback);
	}
	
	public AonInvestAssetPanel(final String domainName,final int domain,final String user, Integer id, final AonInvestAssetPanelCallback callback) {
		initializeCommonService();
		commonService.getInvestAsset(domainName, domain, user, id, new AsyncCallback<InvestAsset>() {
			
			@Override
			public void onSuccess(InvestAsset result) {
				if (result == null) result = new InvestAsset();
				show(domainName,domain, user,result,callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				show(domainName,domain, user,new InvestAsset(),callback);
			}
		});
	}
	
	public void show(final String domainName,final int domain, final String user,final InvestAsset investAsset, final AonInvestAssetPanelCallback callback) {
		setWidth("650px");
		setHeight("440px");
		
		FlowPanel rootPanel = new FlowPanel();
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
	
		table.setWidget(0,0,new InlineLabel(AON.MSG.description()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 0).setPropertyString("min-width", "135px");
		description.setValue(investAsset.getDescription());
		description.setMaxLength(128);
		description.setStyleName(AON.CSS.aonInputText());
		table.setWidget(0,1,description);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.activity()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(1, 0).setPropertyString("min-width", "135px");
		activity.addItem( "-", "");
		getActivities(domainName, domain, user, activities -> {
			activities.forEach(activityIt -> activity.addItem( activityIt.getDescription(), activityIt.getId().toString()));
			setSelectedValueLB(activity, null != investAsset.getActivity() && null != investAsset.getActivity().getId() ? investAsset.getActivity().getId().toString() : null);
		});
		activity.setStyleName(AON.CSS.aonInputText());
		table.setWidget(1,1,activity);
		
		table.setWidget(2,0,new InlineLabel(AON.MSG.type()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(2, 0).setPropertyString("min-width", "135px");
		for(int i=0; i < InvestAssetType.values().length; i++)
			type.addItem(InvestAssetType.values()[i].description(), InvestAssetType.values()[i].ordinal() + "");
		type.setStyleName(AON.CSS.aonInputText());
		type.addChangeHandler(e -> {
			createPropertiesWidget(investAsset);
			onResize();
		});
		setSelectedValueLB(type, null != investAsset.getType() ? investAsset.getType().ordinal() + "" : null);
		table.setWidget(2,1,type);
		
		table.setWidget(3,0,new InlineLabel("Regimen"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(3, 0).setPropertyString("min-width", "135px");
		for(int i=0; i < InvestAssetRegime.values().length; i++)
			regimen.addItem(InvestAssetRegime.values()[i].description(), i + "");
		regimen.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(regimen, null != investAsset.getRegime() ? investAsset.getRegime().ordinal() + "" : null);
		table.setWidget(3,1,regimen);
		
		table.setWidget(4,0,new InlineLabel("% IVA"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(4, 0).setPropertyString("min-width", "135px");
		iva.setValue(investAsset.getVatPercent());
		iva.setStyleName(AON.CSS.aonInputText());
		table.setWidget(4,1,iva);
		
		table.setWidget(5,0,new InlineLabel("% Imp. Directa"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(5, 0).setPropertyString("min-width", "135px");
		retention.setValue(investAsset.getRetentionPercent());
		retention.setStyleName(AON.CSS.aonInputText());
		table.setWidget(5,1,retention);
		
		table.setWidget(6,0,new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(6, 0).setPropertyString("min-width", "135px");
		startDate.setValue(investAsset.getStartDate());
		startDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(6,1,startDate);
		
		table.setWidget(7,0,new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(7, 0).setPropertyString("min-width", "135px");
		endDate.setValue(investAsset.getEndDate());
		endDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(7,1,endDate);
		
		tablePanel.add( table );
		
		createPropertiesWidget(investAsset);
		tablePanel.add( propertiesTable );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				investAsset.setDescription(description.getValue());
				investAsset.setActivity(activity.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activity.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimen.getSelectedValue())));
				investAsset.setVatPercent(iva.getValue());
				investAsset.setRetentionPercent(retention.getValue());
				investAsset.setStartDate(startDate.getValue());
				investAsset.setEndDate(endDate.getValue());
				
				if (investAsset.getId() == null) {
					investAsset.setDomain(domain);
					investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
					investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimen.getSelectedValue())));
				}
				
				investAsset.setProperties(createPropertiesJSON(investAsset.getType()));
				
				commonService.saveInvestAsset(domainName, domain, user, investAsset, new AsyncCallback<InvestAsset>() {

					@Override
					public void onSuccess(InvestAsset result) {
						callback.onAccept();
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
					}
				});
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	protected abstract void onResize();

	private String createPropertiesJSON(InvestAssetType investAssetType) {
		if(investAssetType.equals(InvestAssetType.PREMISES) || investAssetType.equals(InvestAssetType.OTHER_BUILDING) || investAssetType.equals(InvestAssetType.BUILDING_PLOT))
			return createBuildingJSON();
		else if(investAssetType.equals(InvestAssetType.MEANS_OF_TRANSPORT))
			return createTransportJSON();
		else if(investAssetType.equals(InvestAssetType.FIXED_PHONE) || investAssetType.equals(InvestAssetType.CELLULAR_PHONE) || investAssetType.equals(InvestAssetType.FAX))
			return createPhoneJSON();
		else
			return createOtherJSON();
	}

	private String createBuildingJSON() {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("country", new JSONString(countries.getSelectedValue()));
        jsonObject.put("streetType", new JSONString(streetTypes.getSelectedValue()));
        jsonObject.put("address", new JSONString(address.getValue()));
        jsonObject.put("number", new JSONString(number.getValue()));
        jsonObject.put("letter", new JSONString(letter.getValue()));
        jsonObject.put("floor", new JSONString(floor.getValue()));
        jsonObject.put("hand", new JSONString(hand.getValue()));
        jsonObject.put("municipality", new JSONString(municipality.getValue()));
        jsonObject.put("zip", new JSONString(zip.getValue()));
        jsonObject.put("province", new JSONString(provinces.getSelectedValue()));
        jsonObject.put("catastralInex", new JSONString(catastralInex.getValue() ? "S" : "N"));
        jsonObject.put("catastral", new JSONString(catastral.getValue()));
        jsonObject.put("floorPrice", new JSONNumber(floorPrice.getValue()));
        jsonObject.put("constructionPrice", new JSONNumber(constructionPrice.getValue()));

        return jsonObject.toString();
	}

	private String createTransportJSON() {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("carType", new JSONString(carType.getSelectedValue()));
        jsonObject.put("carRegistration", new JSONString(carRegistration.getValue()));

        return jsonObject.toString();
	}

	private String createPhoneJSON() {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("phoneNumber", new JSONString(phoneNumber.getValue()));

        return jsonObject.toString();
	}

	private String createOtherJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aditionalDescription", new JSONString(aditonialDescription.getValue()));

        return jsonObject.toString();
	}

	private void createPropertiesWidget(InvestAsset investAsset) {
		InvestAssetType invesAssetType = InvestAssetType.safeValueOf(Integer.parseInt(type.getSelectedValue()));
		
		propertiesTable.clear();
		propertiesTable.getElement().getStyle().setMarginTop(1, Unit.EM);
		propertiesTable.setStyleName(AON.CSS.aonTable());
		propertiesTable.addStyleName(AON.CSS.aonWidthAll());
		
		propertiesTable.setWidget(0,0,new InlineLabel("Propiedades"));
		propertiesTable.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		propertiesTable.getFlexCellFormatter().setColSpan(0, 0, 6);
		
		if(invesAssetType.equals(InvestAssetType.PREMISES) || invesAssetType.equals(InvestAssetType.OTHER_BUILDING) || invesAssetType.equals(InvestAssetType.BUILDING_PLOT))
			createBuildingWidget(investAsset);
		else if(invesAssetType.equals(InvestAssetType.MEANS_OF_TRANSPORT))
			createTransportWidget(investAsset);
		else if(invesAssetType.equals(InvestAssetType.FIXED_PHONE) || invesAssetType.equals(InvestAssetType.CELLULAR_PHONE) || invesAssetType.equals(InvestAssetType.FAX))
			createPhoneWidget(investAsset);
		else
			createOtherWidget(investAsset);
	}
	
	private void createPropertiesWidget(int row, int col, Integer colspan, String title, Widget widget) {
		propertiesTable.setWidget(row, col, new InlineLabel(title));
		propertiesTable.getCellFormatter().setStyleName(row, col, AON.CSS.aonTableLabel());
		propertiesTable.getCellFormatter().getElement(row, col).setPropertyString("min-width", "135px");
		
		if(!(widget instanceof CheckBox)) widget.setStyleName(AON.CSS.aonInputText());
		propertiesTable.setWidget(row, col + 1, widget);
		
		if(null != colspan) propertiesTable.getFlexCellFormatter().setColSpan(row, col + 1, colspan);
	}

	private void createBuildingWidget(InvestAsset investAsset) {
		setHeight("440px");
		setWidth("900px");
		
		initializeCountries();
		setSelectedValueLB(countries, parsePropertiesJSON(investAsset, "country"));
		createPropertiesWidget(1, 0, null, "Pais", countries);
		
		initializeStreetTypes();
		setSelectedValueLB(streetTypes, parsePropertiesJSON(investAsset, "streetType"));
		createPropertiesWidget(1, 2, 3, "Tipo Via", streetTypes);
		
		address.setValue(parsePropertiesJSON(investAsset, "address"));
		address.setMaxLength(30);
		address.setWidth("100%");
		createPropertiesWidget(2, 0, 5, "Via", address);
		
		number.setValue(parsePropertiesJSON(investAsset, "number"));
		number.setMaxLength(5);
		createPropertiesWidget(3, 0, null, "Numero", number);
		
		letter.setValue(parsePropertiesJSON(investAsset, "letter"));
		letter.setMaxLength(1);
		createPropertiesWidget(3, 2, 3, "Letra", letter);
		
		floor.setValue(parsePropertiesJSON(investAsset, "floor"));
		floor.setMaxLength(5);
		createPropertiesWidget(4, 0, null, "Piso", floor);
		
		hand.setValue(parsePropertiesJSON(investAsset, "hand"));
		hand.setMaxLength(1);
		createPropertiesWidget(4, 2, 3, "Mano", hand);
		
		municipality.setValue(parsePropertiesJSON(investAsset, "municipality"));
		municipality.setMaxLength(25);
		createPropertiesWidget(5, 0, null, "Municipio", municipality);
		
		zip.setValue(parsePropertiesJSON(investAsset, "zip"));
		zip.setMaxLength(5);
		createPropertiesWidget(5, 2, null, "C.P.", zip);
		
		initializeProvinces();
		setSelectedValueLB(provinces, parsePropertiesJSON(investAsset, "province"));
		createPropertiesWidget(5, 4, null, "Provincia", provinces);
		
		String catastralInexB = parsePropertiesJSON(investAsset, "catastralInex");
		catastralInex.setValue(AonStringUtils.isNotBlank(catastralInexB) || AonStringUtils.equalsIgnoreCase(catastralInexB, "S"));
		createPropertiesWidget(6, 0, null, "M.R. Catastral Inex.", catastralInex);
		
		catastral.setValue(parsePropertiesJSON(investAsset, "catastral"));
		catastral.setMaxLength(25);
		createPropertiesWidget(6, 2, 3, "Ref. Catastral", catastral);
		
		floorPrice.setValue(parsePropertiesNumberJSON(investAsset, "floorPrice"));
		createPropertiesWidget(7, 0, null, "Importe Suelo", floorPrice);
		
		constructionPrice.setValue(parsePropertiesNumberJSON(investAsset, "constructionPrice"));
		createPropertiesWidget(7, 2, 3, "Importe Contruccion", constructionPrice);
	}

	private void initializeCountries() {
		countries.clear();
		for(int i=0; i < Country.values().length; i++)
			countries.addItem(Country.values()[i].getName(), Country.values()[i].getIso2());
		countries.setWidth("200px");
	}

	private void initializeStreetTypes() {
		streetTypes.clear();
		StreetType.getSpanishTypes().forEach(st -> streetTypes.addItem(st.getDescription(), st.getAeatCode()));
	}

	private void initializeProvinces() {
		provinces.clear();
		Arrays.stream(Province.values()).forEach(pr -> provinces.addItem(pr.getName(), AonStringUtils.leftPad(pr.ordinal() + "", 2, '0')));
	}

	private void createTransportWidget(InvestAsset investAsset) {
		setHeight("330px");
		setWidth("650px");
		
		initializeCarType();
		setSelectedValueLB(carType, parsePropertiesJSON(investAsset, "carType"));
		createPropertiesWidget(1, 0, 3, "Tipo Vehiculo", carType);
		
		carRegistration.setValue(parsePropertiesJSON(investAsset, "carRegistration"));
		carRegistration.setMaxLength(12);
		createPropertiesWidget(2, 0, 3, "Matricula", carRegistration);
	}

	private void initializeCarType() {
		carType.clear();
		carType.addItem("Turismo", "T");
		carType.addItem("Camion", "C");
		carType.addItem("Otros", "O");
	}

	private void createPhoneWidget(InvestAsset investAsset) {
		setHeight("300px");
		setWidth("650px");
		
		phoneNumber.setValue(parsePropertiesJSON(investAsset, "phoneNumber"));
		phoneNumber.setMaxLength(9);
		createPropertiesWidget(1, 0, 3, "Numero Telefono", phoneNumber);
	}

	private void createOtherWidget(InvestAsset investAsset) {
		setHeight("300px");
		setWidth("650px");
		
		aditonialDescription.setValue(parsePropertiesJSON(investAsset, "aditionalDescription"));
		aditonialDescription.setMaxLength(50);
		createPropertiesWidget(1, 0, 3, "Descripci\\u00f3n Adicional", aditonialDescription);
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
	
	private String parsePropertiesJSON(InvestAsset investAsset, String fieldName) {
		if(null == investAsset || AonStringUtils.isBlank(investAsset.getProperties()))
			return null;
		
		// Parse the JSON string into a JSONValue
        JSONValue jsonValue = JSONParser.parseStrict(investAsset.getProperties());

        // Convert the JSONValue into a JSONObject
        JSONObject jsonObject = jsonValue.isObject();
        
        if(null == jsonObject) return null;
        
        JSONValue fieldNameValue = jsonObject.get(fieldName);
        
        return null == fieldNameValue ? null : fieldNameValue.isString().stringValue();
	}
	
	private Double parsePropertiesNumberJSON(InvestAsset investAsset, String fieldName) {
		if(null == investAsset || AonStringUtils.isBlank(investAsset.getProperties()))
			return null;
		
		// Parse the JSON string into a JSONValue
        JSONValue jsonValue = JSONParser.parseStrict(investAsset.getProperties());

        // Convert the JSONValue into a JSONObject
        JSONObject jsonObject = jsonValue.isObject();
        
        if(null == jsonObject) return null;
        
        JSONValue fieldNameValue = jsonObject.get(fieldName);
        
        return null == fieldNameValue ? null : fieldNameValue.isNumber().doubleValue();
	}
	
	private void getActivities(String domainName, Integer domain, String user, Consumer<List<Activity>> success) {
		commonService.getActivities(domainName, domain, user, new AsyncCallback<List<Activity>>() {
			
			@Override
			public void onSuccess(List<Activity> activities) {
				success.accept(activities);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

}
