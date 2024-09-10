package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.json.JsonGWTUtils;
import com.esferalia.aon.gwt.common.shared.DocumentValidator;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonMarketingActionTargetCreationPanel extends SimplePanel {
	
	public static interface AonMarketingActionTargetCreationPanelCallback {
		void onAccept(String domainName, String login, Integer domainId, String sessionId, JSONObject json);
		void onCancel();
	}
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private TextBox name = new TextBox();
	private ListBox documentType = new ListBox();
	private ListBox documentCountry = new ListBox();
	private TextBox document = new TextBox();
	
	private ListBox streetType = new ListBox();
	private TextBox address = new TextBox();
	private TextBox number = new TextBox();
	private TextBox zip = new TextBox();
	private ListBox province = new ListBox();
	private TextBox city = new TextBox();
	
	private TextBox phone = new TextBox();
	private TextBox email = new TextBox();
	
	private TextArea comments = new TextArea();
	
	private HTMLPanel jsonPanel = new HTMLPanel(EMPTY_STRING);
	
	private LinkedList<GeoZone> aviableGeozones;
	
	private  MarketingAction marketingAction;
	
	private static String EMPTY_STRING = "";
	private HTMLPanel request = new HTMLPanel(EMPTY_STRING);
	
	private String domainName;
	private Integer domainId;
	private String user;
	private Seller seller;
	
	private String domainNameValueStr;
	private Integer domainIdValueInt;
	private String userValueStr;
	private String sessionIdValueStr;
	
	public AonMarketingActionTargetCreationPanel(final String domainName,final int domain, final String user, LinkedList<Scope> aviableScopes, LinkedList<GeoZone> aviableGeozones, final MarketingAction marketingAction, Seller seller, final AonMarketingActionTargetCreationPanelCallback aonMarketingActionTargetCreationPanelCallback) {
		this.aviableGeozones = aviableGeozones;
		this.marketingAction = marketingAction;
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		this.seller = seller;
		initializeCommonService();
		show(aonMarketingActionTargetCreationPanelCallback);
	}

	public void show(final AonMarketingActionTargetCreationPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		addTableStyle(tablePanel.getElement());
		
		tablePanel.add( createForm(callback) );
		
		createJson();
		tablePanel.add( request );
		
		rootPanel.add( tablePanel );
		
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	name.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void addTableStyle(Element el) {
		el.getStyle().setProperty("display", "flex");
		el.getStyle().setProperty("align-items", "flex-start");
		el.getStyle().setProperty("gap", "1rem");
	}

	private HTMLPanel createForm(AonMarketingActionTargetCreationPanelCallback callback) {
		HTMLPanel form = new HTMLPanel(EMPTY_STRING);
		addFormStyle(form.getElement());
		
		form.add(messagePanel);
		
		// Campaña / Accion
		HTMLPanel campaignActionPanel = new HTMLPanel(EMPTY_STRING);
		campaignActionPanel.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel campaignGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(campaignGroup.getElement());
		
		Label campaignTitle = new Label("Campa\u00f1a");
		addInputTitleStyle(campaignTitle.getElement());
		
		Label campaignLabel = new Label(marketingAction.getMarketingCampaign().getDescription());
		addInputStyle(campaignLabel.getElement());
		
		campaignGroup.add(campaignTitle);
		campaignGroup.add(campaignLabel);
		campaignActionPanel.add(campaignGroup);
		
		HTMLPanel actionGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(actionGroup.getElement());
		
		Label actionTitle = new Label("Acci\u00f3n");
		addInputTitleStyle(actionTitle.getElement());
		
		Label actionLabel = new Label(marketingAction.getDescription());
		addInputStyle(actionLabel.getElement());
		
		actionGroup.add(actionTitle);
		actionGroup.add(actionLabel);
		campaignActionPanel.add(actionGroup);
		
		form.add(campaignActionPanel);
		
		// Nombre/Razón Social	
		HTMLPanel nameGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(nameGroup.getElement());
		
		Label nameTitle = new Label("Nombre / Raz\u00f3n Social *");
		addInputTitleStyle(nameTitle.getElement());
		
		name.addValueChangeHandler(e -> loadJSONBody());
		addInputStyle(name.getElement());
		
		nameGroup.add(nameTitle);
		nameGroup.add(name);
		form.add(nameGroup);
		
		// Telefono	/ Email
		HTMLPanel phoneEmailPanel = new HTMLPanel(EMPTY_STRING);
		phoneEmailPanel.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel phoneGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(phoneGroup.getElement());
		
		Label phoneTitle = new Label("Tel\u00e9fono");
		addInputTitleStyle(phoneTitle.getElement());
		
		addInputStyle(phone.getElement());
		phone.addValueChangeHandler(e -> loadJSONBody());
		
		phoneGroup.add(phoneTitle);
		phoneGroup.add(phone);
		phoneEmailPanel.add(phoneGroup);
			
		HTMLPanel emailGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(emailGroup.getElement());
		
		Label emailTitle = new Label("Email *");
		addInputTitleStyle(emailTitle.getElement());
		
		addInputStyle(email.getElement());
		email.addValueChangeHandler(e -> loadJSONBody());
		
		emailGroup.add(emailTitle);
		emailGroup.add(email);
		phoneEmailPanel.add(emailGroup);
		
		form.add(phoneEmailPanel);
		
		// Commentarios	
		HTMLPanel commentsGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(commentsGroup.getElement());
		
		Label commentsTitle = new Label("Comentarios");
		addInputTitleStyle(commentsTitle.getElement());
		
		comments.setVisibleLines(5);
		comments.addValueChangeHandler(e -> loadJSONBody());
		addInputStyle(comments.getElement());
		
		commentsGroup.add(commentsTitle);
		commentsGroup.add(comments);
		form.add(commentsGroup);
		
		// Documento	
		HTMLPanel documentPanel = new HTMLPanel(EMPTY_STRING);
		documentPanel.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel documentGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(documentGroup.getElement());
		
		Label documentTitle = new Label("Documento");
		addInputTitleStyle(documentTitle.getElement());
		
		document.addValueChangeHandler(e -> {
			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
			setSelectedValueLB(documentType, documentTypeValidator.ordinal() + "");
			loadJSONBody();
		});
		addInputStyle(document.getElement());
		
		documentGroup.add(documentTitle);
		documentGroup.add(document);
		documentPanel.add(documentGroup);
		
		HTMLPanel documentTypeGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(documentTypeGroup.getElement());
		
		Label documentTypeTitle = new Label("Tipo");
		addInputTitleStyle(documentTypeTitle.getElement());
		
		for(int i=0; i<DocumentType.values().length; i++) {
			DocumentType documentTypeValue = DocumentType.values()[i];
			documentType.addItem(documentTypeValue.getDescription(), documentTypeValue.ordinal() + "");
		}
		documentType.getElement().getStyle().setWidth(60.0, Unit.PX);
		addInputStyle(documentType.getElement());
		documentType.addChangeHandler(e -> loadJSONBody());
		
		documentTypeGroup.add(documentTypeTitle);
		documentTypeGroup.add(documentType);
		documentPanel.add(documentTypeGroup);
		
		HTMLPanel documentCountryGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(documentCountryGroup.getElement());
		
		Label documentCountryTitle = new Label("Pa\u00eds Emisi\u00f3n");
		addInputTitleStyle(documentCountryTitle.getElement());
		
		for(int i=0; i<Country.values().length; i++) {
			Country countryValue = Country.values()[i];
			documentCountry.addItem(capitalizeFirstLetterOfEachWord(countryValue.getName()), countryValue.getIso2() + "");
		}
		setSelectedValueLB(documentCountry, "ES");
		addInputStyle(documentCountry.getElement());
		documentCountry.addChangeHandler(e -> loadJSONBody());
		
		documentCountryGroup.add(documentCountryTitle);
		documentCountryGroup.add(documentCountry);
		documentPanel.add(documentCountryGroup);
		
		form.add(documentPanel);
		
		// Dirección
		
		HTMLPanel addressPanel = new HTMLPanel(EMPTY_STRING);
		addressPanel.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel streetTypeGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(streetTypeGroup.getElement());
		
		Label streeTitle = new Label("Tipo de v\u00eda");
		addInputTitleStyle(streeTitle.getElement());
		
		StreetType.getSpanishTypes().forEach(streetTypeValue -> streetType.addItem(capitalizeFirstLetterOfEachWord(streetTypeValue.getDescription()), streetTypeValue.getAeatCode()));
		setSelectedValueLB(streetType, "CL");
		streetType.getElement().getStyle().setWidth(60.0, Unit.PX);
		addInputStyle(streetType.getElement());
		streetType.addChangeHandler(e -> loadJSONBody());
		
		streetTypeGroup.add(streeTitle);
		streetTypeGroup.add(streetType);
		addressPanel.add(streetTypeGroup);
		
		HTMLPanel addressGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(addressGroup.getElement());
		
		Label addressTitle = new Label("Direcci\u00f3n");
		addInputTitleStyle(addressTitle.getElement());
		
		addInputStyle(address.getElement());
		address.addValueChangeHandler(e -> loadJSONBody());
		
		addressGroup.add(addressTitle);
		addressGroup.add(address);
		addressPanel.add(addressGroup);
		
		HTMLPanel addressNumberGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(addressNumberGroup.getElement());
		
		Label addressNumberTitle = new Label("N\u00famero");
		addInputTitleStyle(addressNumberTitle.getElement());
		
		number.getElement().getStyle().setWidth(60.0, Unit.PX);
		addInputStyle(number.getElement());
		number.addValueChangeHandler(e -> loadJSONBody());
		
		addressNumberGroup.add(addressNumberTitle);
		addressNumberGroup.add(number);
		addressPanel.add(addressNumberGroup);
		
		form.add(addressPanel);
		
		// Dirección II
		
		HTMLPanel address2Panel = new HTMLPanel(EMPTY_STRING);
		address2Panel.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel zipGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(zipGroup.getElement());
		
		Label zipTitle = new Label("C\u00f3digo Postal");
		addInputTitleStyle(zipTitle.getElement());
		
		zip.addValueChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(zip.getValue()) && zip.getValue().length() >= 2) {
				setSelectedValueLB(province, AonStringUtils.substring(zip.getValue(), 0, 2));
			}
			loadJSONBody();
		});
		zip.getElement().getStyle().setWidth(60.0, Unit.PX);
		addInputStyle(zip.getElement());
		
		zipGroup.add(zipTitle);
		zipGroup.add(zip);
		address2Panel.add(zipGroup);
		
		HTMLPanel provinceGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(provinceGroup.getElement());
		
		Label provinceTitle = new Label("Provincia");
		addInputTitleStyle(provinceTitle.getElement());
		
		aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).forEach(geozone -> province.addItem(capitalizeFirstLetterOfEachWord(geozone.getName()), geozone.getCode()));
		addInputStyle(province.getElement());
		province.addChangeHandler(e -> loadJSONBody());
		
		provinceGroup.add(provinceTitle);
		provinceGroup.add(province);
		address2Panel.add(provinceGroup);
		
		HTMLPanel cityGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(cityGroup.getElement());
		
		Label cityTitle = new Label("Localidad");
		addInputTitleStyle(cityTitle.getElement());
		
		addInputStyle(city.getElement());
		city.addValueChangeHandler(e -> loadJSONBody());
		
		cityGroup.add(cityTitle);
		cityGroup.add(city);
		address2Panel.add(cityGroup);
		
		form.add(address2Panel);
		
		Button sendButton = new Button();
		sendButton.setStyleName(AON.CSS.aonButton());
		sendButton.setText("Enviar");
		addButtonStyle(sendButton.getElement());
		sendButton.addClickHandler(e -> {
			if(canSendRequest(messagePanel)) {
				sendButton.setEnabled(false);
				
				callback.onAccept(domainNameValueStr, userValueStr, domainIdValueInt, sessionIdValueStr, createActionTargetJSON());
			}
		});
		form.add(sendButton);
		
		return form;
	}

	private boolean canSendRequest(HTMLPanel messagePanel) {
		if(AonStringUtils.isBlank(name.getValue())) {
			AonMessagePanel.showWarning(messagePanel, "El campo 'Nombre / Raz\u00f3n Social *' es requerido");
			messagePanel.getWidget(0).getElement().getStyle().setMargin(0.00, Unit.PX);
			return false;
		}
		
		if(AonStringUtils.isBlank(email.getValue())) {
			AonMessagePanel.showWarning(messagePanel, "El campo 'Email' es requerido");
			messagePanel.getWidget(0).getElement().getStyle().setMargin(0.00, Unit.PX);
			return false;
		}
		
		return true;
	}

	private boolean canBeCastToInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	private static String capitalizeFirstLetterOfEachWord(String str) {
		if(AonStringUtils.isBlank(str)) return str;
		
	    StringBuilder sb = new StringBuilder();
	    boolean capitalizeNext = true;

	    for (char c : str.toCharArray()) {
	        if (Character.isWhitespace(c)) {
	            capitalizeNext = true;
	        } else if (capitalizeNext) {
	            sb.append(Character.toUpperCase(c));
	            capitalizeNext = false;
	        } else {
	            sb.append(Character.toLowerCase(c));
	        }
	    }

	    return sb.toString();
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
	
	private void addFormStyle(Element el) {
		el.getStyle().setProperty("display", "flex");
		el.getStyle().setProperty("flex-direction", "column");
		el.getStyle().setProperty("gap", "1rem");
		el.getStyle().setProperty("border-right", "1px solid rgb(221, 227, 236)");
		el.getStyle().setProperty("padding-right", "1rem");
	}
	
	private void addInputGroupStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("display", "flex");
		el.getStyle().setProperty("flex-direction", "column");
		el.getStyle().setProperty("gap", ".5rem");
	}
	
	private void addInputTitleStyle(Element el) {
		el.getStyle().setProperty("font-weight", "bold");
	}
	
	private void addInputStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("padding", ".5rem 1rem");
		el.getStyle().setProperty("border", "1px solid #dde3ec");
	}
	
	private void addButtonStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("padding", ".5rem 1rem");
		el.getStyle().setProperty("border", "1px solid #f6f8fc");
		el.getStyle().setProperty("border-radius", "5px");
		el.getStyle().setProperty("font-size", "15px");
		el.getStyle().setProperty("cursor", "pointer");
		el.getStyle().setProperty("background-color", "#2152a5");
		el.getStyle().setProperty("color", "white");
	}
	
	private void createJson() {
		request.clear();
		addRequestStyle(request.getElement());
		
		// Header
		Label headerTitle = new Label("HEADER");
		addRequestTitleStyle(headerTitle.getElement());
		request.add(headerTitle);
		
		// Session Id Header
		HTMLPanel serviceUserGroup = new HTMLPanel(EMPTY_STRING);
		addInputGroupStyle(serviceUserGroup.getElement());
		
		Label serviceUserTitle = new Label("Usuario Servicios");
		addInputTitleStyle(serviceUserTitle.getElement());
		
		request.add(serviceUserGroup);
		
		// Domain Name Header
		HTMLPanel domainNameGroup = new HTMLPanel(EMPTY_STRING);
		domainNameGroup.addStyleName(AON.CSS.aonItemFlex());
		addRequestGroupStyle(domainNameGroup.getElement());
		
		Label domainNameTitle = new Label("domain_name");
		addRequestSubTitleStyle(domainNameTitle.getElement());
		Label domainNameValue = new Label("Seleccione un usuario de servicio");
		domainNameGroup.add(domainNameTitle);
		domainNameGroup.add(domainNameValue);
		request.add(domainNameGroup);
		
		// Domain Id Header
		HTMLPanel domainIdGroup = new HTMLPanel(EMPTY_STRING);
		domainIdGroup.addStyleName(AON.CSS.aonItemFlex());
		addRequestGroupStyle(domainIdGroup.getElement());
		
		Label domainIdTitle = new Label("domain_id");
		addRequestSubTitleStyle(domainIdTitle.getElement());
		Label domainIdValue = new Label("Seleccione un usuario de servicio");
		domainIdGroup.add(domainIdTitle);
		domainIdGroup.add(domainIdValue);
		request.add(domainIdGroup);
		
		// Domain Id Header
		HTMLPanel userGroup = new HTMLPanel(EMPTY_STRING);
		userGroup.addStyleName(AON.CSS.aonItemFlex());
		addRequestGroupStyle(userGroup.getElement());
		
		Label userTitle = new Label("domain_login");
		addRequestSubTitleStyle(userTitle.getElement());
		Label userValue = new Label("Seleccione un usuario de servicio");
		userGroup.add(userTitle);
		userGroup.add(userValue);
		request.add(userGroup);
		
		// Session Id Header
		HTMLPanel sessionIdGroup = new HTMLPanel(EMPTY_STRING);
		sessionIdGroup.addStyleName(AON.CSS.aonItemFlex());
		addRequestGroupStyle(sessionIdGroup.getElement());
		
		Label sessionIdTitle = new Label("session_id");
		addRequestSubTitleStyle(sessionIdTitle.getElement());
		
		HTMLPanel sessionIdValueGroup = new HTMLPanel(EMPTY_STRING);
		sessionIdValueGroup.addStyleName(AON.CSS.aonItemFlex());
		Label sessionIdValue = new Label("Seleccione un usuario de servicio");
		sessionIdValue.getElement().getStyle().setProperty("max-width", "15rem");
		sessionIdValue.getElement().getStyle().setProperty("white-space", "nowrap");
		sessionIdValue.getElement().getStyle().setProperty("overflow", "hidden");
		sessionIdValue.getElement().getStyle().setProperty("text-overflow", "ellipsis");
		sessionIdValueGroup.add(sessionIdValue);
		
		AonTableButton copySessionIdValue = new AonTableButton("Copiar Body JSON", AON.CSS.aonIconCopy());
		copySessionIdValue.setVisible(false);
		copySessionIdValue.addClickHandler(e -> {
			copyToClipboard(sessionIdValue.getText());
			new Timer() {
				
				@Override
				public void run() {
					AonMessagePanel.showSuccess(messagePanel, "'session_id' copiado");
					messagePanel.getWidget(0).getElement().getStyle().setMargin(0.00, Unit.PX);
				}
				
			}.schedule(1000);
		});
		sessionIdValueGroup.add(copySessionIdValue);
		
		sessionIdGroup.add(sessionIdTitle);
		sessionIdGroup.add(sessionIdValueGroup);
		request.add(sessionIdGroup);
		
		// User Id Handler
		getServiceUsers(serviceUsers -> {
			ListBox serviceUsersLB = new ListBox();
			addInputStyle(serviceUsersLB.getElement());
			if(serviceUsers.isEmpty()) {
				serviceUsersLB.addItem("No existen usuarios de servicios");
			} else {
				serviceUsersLB.addItem("-", "");
				serviceUsers.forEach(user -> serviceUsersLB.addItem(user.getName(), user.getId().toString()));
				serviceUsersLB.addChangeHandler(e -> {
					if(AonStringUtils.isBlank(serviceUsersLB.getSelectedValue())) {
						domainNameValue.setText("Seleccione un usuario de servicio");
						domainIdValue.setText("Seleccione un usuario de servicio");
						userValue.setText("Seleccione un usuario de servicio");
						sessionIdValue.setText("Seleccione un usuario de servicio");
						copySessionIdValue.setVisible(false);
					} else {
						AonMessagePanel.showLoading(messagePanel, "Obteniendo credenciales del usuario '" + serviceUsersLB.getSelectedItemText() + "'");
						messagePanel.getWidget(0).getElement().getStyle().setMargin(0.00, Unit.PX);
						
						// Create a URL builder and add query parameters
						UrlBuilder urlBuilder = new UrlBuilder();
						urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
						urlBuilder.setHost(Window.Location.getHost()); 
						urlBuilder.setPath("/ms/api/generate_token/json/");
						
						HashMap<String, String> headers = new HashMap<>();
						headers.put("domainName", domainName);
						headers.put("domainLogin", user);
						headers.put("domainId", String.valueOf(domainId));
						headers.put("id", serviceUsersLB.getSelectedValue());
						headers.put("time", "3");
						
						// Create the request builder with the complete URL
						RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
						headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
						
						try {
						    // Send the request
						    requestBuilder.sendRequest(null, new RequestCallback() {
						        public void onResponseReceived(Request request, Response response) {
						            if (response.getStatusCode() == 200) {
						            	JSONObject userTokenJson = JSONParser.parseStrict(response.getText()).isObject();
						            	domainNameValue.setText(JsonGWTUtils.getString(userTokenJson, "domain_name"));
						            	domainNameValueStr = JsonGWTUtils.getString(userTokenJson, "domain_name");
						            	
						            	domainIdValue.setText(JsonGWTUtils.getInteger(userTokenJson, "domain_id").toString());
						            	domainIdValueInt = JsonGWTUtils.getInteger(userTokenJson, "domain_id");
						            	
						            	userValue.setText(JsonGWTUtils.getString(userTokenJson, "domain_login"));
						            	userValueStr = JsonGWTUtils.getString(userTokenJson, "domain_login");
						            	
						            	sessionIdValue.setText(JsonGWTUtils.getString(userTokenJson, "session_id"));
						            	sessionIdValueStr = JsonGWTUtils.getString(userTokenJson, "session_id");
						            	
						            	copySessionIdValue.setVisible(true);
						            	AonMessagePanel.hideMessage(messagePanel);
						            } 
						        }

								public void onError(Request request, Throwable exception) {
									Window.alert("Error sendRequestCB getting token : " + exception.getMessage());
						        }
						    });
						} catch (RequestException exception) {
							Window.alert("Error getting token : " + exception.getMessage());
						}
					}
				});
			}
			serviceUserGroup.add(serviceUserTitle);
			serviceUserGroup.add(serviceUsersLB);
		});
		
		// Body
		HTMLPanel bodyGroup = new HTMLPanel(EMPTY_STRING);
		bodyGroup.addStyleName(AON.CSS.aonItemFlex());
		Label bodyTitle = new Label("BODY");
		addRequestTitleStyle(bodyTitle.getElement());
		bodyGroup.add(bodyTitle);
		AonTableButton copy = new AonTableButton("Copiar Body JSON", AON.CSS.aonIconCopy());
		copy.addClickHandler(e -> {
			copyToClipboard(createActionTargetJSON().toString());
			new Timer() {
				
				@Override
				public void run() {
					AonMessagePanel.showSuccess(messagePanel, "JSON del body copiado");
					messagePanel.getWidget(0).getElement().getStyle().setMargin(0.00, Unit.PX);
				}
				
			}.schedule(1000);
		});
		bodyGroup.add(copy);
		request.add(bodyGroup);
		
		loadJSONBody();
		request.add(jsonPanel);
		
	}
	
	private void loadJSONBody() {
		jsonPanel.clear();
		
		String json = 
		"{\n"
		+ "<br>"
		+ "  &ensp;\"marketingAction\":{\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"id\":\"" + marketingAction.getId() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"sellerDistribution\":\"" + marketingAction.getSellerDistribution().getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"workgroup\":\"" + (null == marketingAction.getWorkgroup() ? "" : marketingAction.getWorkgroup().getId()) + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"seller\":\"" + (null == seller ? "" : seller.getId()) + "\",\n"
		+ "<br>"
		+ "   &ensp;},\n"
		+ "\n"
		+ "   &ensp;\"target\":{\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"name\":\"" + name.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"phone\":\"" + phone.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"email\":\"" + email.getValue() + "\"\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"comments\":\"" + comments.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"documentType\":\"" + documentType.getSelectedValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"documentCountry\":\"" + documentCountry.getSelectedValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"document\":\"" + document.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"streetType\":\"" + streetType.getSelectedValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"address\":\"" + address.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"number\":\"" + number.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"zip\":\"" + zip.getValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"geozoneCode\":\"" + province.getSelectedValue() + "\",\n"
		+ "<br>"
		+ "      &ensp; &ensp;\"city\":\"" + city.getValue() + "\",\n"
		+ "<br>"
		+ "   &ensp;}\n"
		+ "<br>"
		+ "}"
		;
		
		HTMLPanel jsonValuePanel = new HTMLPanel(json);
		jsonValuePanel.getElement().getStyle().setProperty("max-width", "22rem");
		jsonPanel.add(jsonValuePanel);
	}
	
	private void getServiceUsers(Consumer<List<User>> consumer) {
		commonService.getAviableServiceUsers(domainName, domainId, user, new AsyncCallback<List<User>>() {
			
			@Override
			public void onSuccess(List<User> serviceUsers) {
				consumer.accept(serviceUsers);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	private void addRequestStyle(Element el) {
		el.getStyle().setProperty("display", "flex");
		el.getStyle().setProperty("flex-direction", "column");
		el.getStyle().setProperty("gap", "1rem");
	}

	private void addRequestTitleStyle(Element el) {
		el.getStyle().setProperty("font-weight", "bold");
	}

	private void addRequestGroupStyle(Element el) {
		el.getStyle().setProperty("border", "1px solid rgb(221, 227, 236)");
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("padding", "0.4rem 1rem");
	}

	private void addRequestSubTitleStyle(Element el) {
		el.getStyle().setProperty("border-right", "1px solid rgb(221, 227, 236)");
		el.getStyle().setProperty("padding-right", ".5rem");
		el.getStyle().setProperty("font-weight", "bold");
	}
	
	private final native void copyToClipboard(String text) /*-{
		var textField = $doc.createElement('textarea');
	    textField.value = text;
	    $doc.body.appendChild(textField);
	    textField.select();
	    $doc.execCommand('copy');
	    $doc.body.removeChild(textField);
	}-*/;

	private JSONObject createActionTargetJSON() {
		JSONObject actionTarget = new JSONObject();
		
		JSONObject action = new JSONObject();
		action.put("id", new JSONString(marketingAction.getId().toString()));
		action.put("workgroup", new JSONString(null == marketingAction.getWorkgroup() ? "" : marketingAction.getWorkgroup().getId().toString()));
		action.put("sellerDistribution", new JSONString(marketingAction.getSellerDistribution().getValue().toString()));
		action.put("seller", new JSONString(null == seller ? "" : seller.getId().toString()));
		actionTarget.put("marketingAction", action);
		
		JSONObject target = new JSONObject();
		target.put("name", new JSONString(name.getValue()));
		target.put("documentType", new JSONString(documentType.getSelectedValue())); // DNI, CIF, NIE, OTROS
		target.put("documentCountry", new JSONString(documentCountry.getSelectedValue())); 
		target.put("document", new JSONString(document.getValue()));
		target.put("streetType", new JSONString(streetType.getSelectedValue()));
		target.put("address", new JSONString(address.getValue()));
		target.put("number", new JSONString(number.getValue()));
		target.put("zip", new JSONString(zip.getValue()));
		target.put("geozoneCode", new JSONString(province.getSelectedValue()));
		target.put("city", new JSONString(city.getValue()));
		target.put("phone", new JSONString(phone.getValue()));
		target.put("email", new JSONString(email.getValue()));
		target.put("comments", new JSONString(comments.getValue()));
		actionTarget.put("target", target);
		return actionTarget;
	}
	
	protected abstract void onResize();

}
