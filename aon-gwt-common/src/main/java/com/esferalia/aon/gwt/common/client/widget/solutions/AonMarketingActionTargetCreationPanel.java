package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DocumentValidator;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonMarketingActionTargetCreationPanel extends SimplePanel {
	
	public static interface AonMarketingActionTargetCreationPanelCallback {
		void onAccept(JSONObject json);
		void onCancel();
	}

//	private Label marketingActionId = new Label();
//	private Label marketingActionDescription = new Label();
//	
//	private TextBox name = new TextBox();
//	private ListBox documentType = new ListBox();
//	private ListBox documentCountry = new ListBox();
//	private TextBox document = new TextBox();
//	
//	private ListBox streetType = new ListBox();
//	private TextBox address = new TextBox();
//	private TextBox number = new TextBox();
//	private TextBox zip = new TextBox();
//	private ListBox province = new ListBox();
//	private TextBox city = new TextBox();
//	
//	private TextBox phone = new TextBox();
//	private TextBox email = new TextBox();
	
	private LinkedList<GeoZone> aviableGeozones;
	
	private boolean isLocalDev = false;
	
	public AonMarketingActionTargetCreationPanel(final String domainName,final int domain, final String user, LinkedList<Scope> aviableScopes, LinkedList<GeoZone> aviableGeozones, final MarketingAction marketingAction, final AonMarketingActionTargetCreationPanelCallback aonMarketingActionTargetCreationPanelCallback) {
		this.aviableGeozones = aviableGeozones;
		showForm(domainName, domain, user, marketingAction);
		
		// GWT FORM
//		show(domainName, domain, user, aviableScopes, marketingAction, aonMarketingActionTargetCreationPanelCallback);
	}

	private void showForm(String domainName, int domain, String user, MarketingAction marketingAction) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("770px");
		String htmlFormCode = 
							"<div class=\"formbold-main-wrapper\">\n"
					+ "        <div class=\"formbold-form-wrapper\">\n"
					+ "            <form action=\"" + (isLocalDev ? "http://" : "https://") + Window.Location.getHost() + "/ms/api/action-target/" + "\" method=\"POST\" target=\"_blank\" id=\"actionTargetForm\">\n"
					+ "\n"
					+ "				   <input type=\"hidden\" id=\"domainName\" name=\"domainName\" value=\"" + domainName + "\">\n"
					+ "				   <input type=\"hidden\" id=\"domainId\" name=\"domainId\" value=\"" + domain + "\">\n"
					+ "				   <input type=\"hidden\" id=\"login\" name=\"login\" value=\"" + user + "\">\n"
					+ "				   <input type=\"hidden\" id=\"id\" name=\"id\" value=\"" + marketingAction.getId() + "\">\n"
					+ "\n"
					+ "                <div class=\"formbold-mb-3 formbold-mt-3 formbold-input-wrapp\">\n"
					+ "                    <label for=\"phone\" class=\"formbold-form-label\"> Acci\u00f3n </label>\n"
					+ "\n"
					+ "                    <div>\n"
					+ "                        <input type=\"text\" name=\"description\" id=\"description\" value=\"" + marketingAction.getDescription() + "\" class=\"formbold-form-input disabled\" />\n"
					+ "                    </div>\n"
					+ "\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-mb-3\">\n"
					+ "                    <label for=\"age\" class=\"formbold-form-label\"> Nombre / Raz\u00f3n Social * </label>\n"
					+ "                    <input type=\"text\" name=\"name\" id=\"name\" placeholder=\"Nombre\" class=\"formbold-form-input\" required />\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-input-wrapp formbold-mb-3\">\n"
					+ "                    <label for=\"firstname\" class=\"formbold-form-label\"> Documento </label>\n"
					+ "\n"
					+ "                    <div>\n"
					+ "                        <input type=\"text\" name=\"document\" id=\"document\" placeholder=\"ej: 00000000A\" class=\"formbold-form-input\" />\n"
					+ "\n"
					+ "                        <select class=\"formbold-form-input formbold-w-30\" name=\"documentType\" id=\"documentType\">\n";
					
					for(int i=0; i<DocumentType.values().length; i++) {
						DocumentType documentTypeValue = DocumentType.values()[i];
						htmlFormCode +=	"		  <option value=\"" + documentTypeValue.ordinal() + "\">" + documentTypeValue.getDescription() + "</option>\n";
					}
					
		htmlFormCode+= "                        </select>\n"
					+ "\n"
					+ "                        <select class=\"formbold-form-input formbold-w-55\" name=\"documentCountry\" id=\"documentCountry\">\n"
					+ "                            <option value=\"\">País Emisi\u00f3n</option>\n";
		
					for(int i=0; i<Country.values().length; i++) {
						Country country = Country.values()[i];
						htmlFormCode += "			<option value=\"" + country.getIso2() + "\" " + (AonStringUtils.equals(country.getIso2(), "ES") ? "selected" : "") + ">" + country.getName() + "</option>\n";
					}
					
		htmlFormCode+= "                        </select>\n"
					+ "\n"
					+ "                    </div>\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-input-wrapp formbold-mb-3\">\n"
					+ "                    <label for=\"firstname\" class=\"formbold-form-label\"> Direcci\u00f3n </label>\n"
					+ "\n"
					+ "                    <div>\n"
					+ "                        <select class=\"formbold-form-input formbold-w-45\" name=\"streetType\" id=\"streetType\">\n";
		
					for(StreetType streetTypeValue : StreetType.getSpanishTypes()) {
							htmlFormCode +=  "		<option value=\"" + streetTypeValue.getAeatCode() + "\" " + (AonStringUtils.equals(streetTypeValue.getAeatCode(), "CL") ? "selected" : "") + ">" + streetTypeValue.getDescription() + "</option>\n";
					}
		
		htmlFormCode+= "                        </select>\n"
					+ "\n"
					+ "                        <input type=\"text\" name=\"address\" id=\"address\" placeholder=\"Info direcci\u00f3n\" class=\"formbold-form-input\" />\n"
					+ "\n"
					+ "						<input type=\"text\" name=\"number\" id=\"number\" placeholder=\"N\u00ba\" class=\"formbold-form-input formbold-w-30\" />\n"
					+ "\n"
					+ "                    </div>\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-input-flex\">\n"
					+ "                    <div>\n"
					+ "                        <label for=\"post\" class=\"formbold-form-label\"> C\u00f3digo Postal </label>\n"
					+ "                        <input type=\"text\" name=\"zip\" id=\"zip\" placeholder=\"00000\" class=\"formbold-form-input\" />\n"
					+ "                    </div>\n"
					+ "                    <div>\n"
					+ "                        <label for=\"city\" class=\"formbold-form-label\"> Provincia </label>\n"
					+ "                        <select class=\"formbold-form-input\" name=\"geozone\" id=\"geozone\">\n";
		
					for(GeoZone geozone : aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).collect(Collectors.toList())) {
						htmlFormCode += "			<option value=\"" + geozone.getCode() + "\">" + geozone.getName() + "</option>\n";
					}
					
		htmlFormCode+= "                        </select>\n"
					+ "                    </div>\n"
					+ "                  	<div class=\"formbold-w-100\">\n"
					+ "                        <label for=\"post\" class=\"formbold-form-label\"> Localidad </label>\n"
					+ "                  	<input type=\"text\" name=\"city\" id=\"city\" placeholder=\"Localidad\" class=\"formbold-form-input\" />\n"
					+ "                  </div>\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-mb-3\">\n"
					+ "                    <label for=\"email\" class=\"formbold-form-label\"> Email * </label>\n"
					+ "                    <input type=\"email\" name=\"email\" id=\"email\" placeholder=\"example@email.com\" class=\"formbold-form-input\" required />\n"
					+ "                </div>\n"
					+ "\n"
					+ "                <div class=\"formbold-mb-3 formbold-input-wrapp\">\n"
					+ "                    <label for=\"phone\" class=\"formbold-form-label\"> Phone </label>\n"
					+ "\n"
					+ "                    <div>\n"
					+ "                        <input type=\"text\" name=\"phone\" id=\"phone\" placeholder=\"Phone number\" class=\"formbold-form-input\" />\n"
					+ "                    </div>\n"
					+ "                </div>\n"
					+ "\n"
					+ "                \n"
					+ "\n"
					+ "                <button class=\"formbold-btn\" type=\"submit\">Enviar</button>\n"
					+ "            </form>\n"
					+ "        </div>\n"
					+ "    </div>\n"
					+ "    <style>\n"
					+ "        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');\n"
					+ "\n"
					+ "        * {\n"
					+ "            margin: 0;\n"
					+ "            padding: 0;\n"
					+ "            box-sizing: border-box;\n"
					+ "        }\n"
					+ "\n"
					+ "        body {\n"
					+ "            font-family: 'Inter', sans-serif;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-mb-3 {\n"
					+ "            margin-bottom: 15px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-mt-3 {\n"
					+ "            margin-top: 15px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-opacity-0 {\n"
					+ "            opacity: 0;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-stroke-current {\n"
					+ "            stroke: #ffffff;\n"
					+ "            z-index: 999;\n"
					+ "        }\n"
					+ "\n"
					+ "        #supportCheckbox:checked~div span {\n"
					+ "            opacity: 1;\n"
					+ "        }\n"
					+ "\n"
					+ "        #supportCheckbox:checked~div {\n"
					+ "            background: #6a64f1;\n"
					+ "            border-color: #6a64f1;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-main-wrapper {\n"
					+ "            display: flex;\n"
					+ "            align-items: center;\n"
					+ "            justify-content: center;\n"
					+ "            padding: 0 48px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-wrapper {\n"
					+ "            margin: 0 auto;\n"
					+ "            max-width: 570px;\n"
					+ "            width: 100%;\n"
					+ "            background: white;\n"
					+ "            padding: 0 40px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-img {\n"
					+ "            display: block;\n"
					+ "            margin: 0 auto 45px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-input-wrapp>div {\n"
					+ "            display: flex;\n"
					+ "            gap: 20px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-input-flex {\n"
					+ "            display: flex;\n"
					+ "            gap: 20px;\n"
					+ "            margin-bottom: 15px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-input-flex>div {\n"
					+ "            width: 50%;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-input {\n"
					+ "            width: 100%;\n"
					+ "            padding: 13px 22px;\n"
					+ "            border-radius: 5px;\n"
					+ "            border: 1px solid #dde3ec;\n"
					+ "            background: #ffffff;\n"
					+ "            font-weight: 500;\n"
					+ "            font-size: 16px;\n"
					+ "            color: #536387;\n"
					+ "            outline: none;\n"
					+ "            resize: none;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-input::placeholder,\n"
					+ "        select.formbold-form-input,\n"
					+ "        .formbold-form-input[type='date']::-webkit-datetime-edit-text,\n"
					+ "        .formbold-form-input[type='date']::-webkit-datetime-edit-month-field,\n"
					+ "        .formbold-form-input[type='date']::-webkit-datetime-edit-day-field,\n"
					+ "        .formbold-form-input[type='date']::-webkit-datetime-edit-year-field {\n"
					+ "            color: rgba(83, 99, 135, 0.5);\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-input:focus {\n"
					+ "            border-color: #6a64f1;\n"
					+ "            box-shadow: 0px 3px 8px rgba(0, 0, 0, 0.05);\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-label {\n"
					+ "            color: #536387;\n"
					+ "            font-size: 14px;\n"
					+ "            line-height: 24px;\n"
					+ "            display: block;\n"
					+ "            margin-bottom: 10px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-checkbox-label {\n"
					+ "            display: flex;\n"
					+ "            cursor: pointer;\n"
					+ "            user-select: none;\n"
					+ "            font-size: 16px;\n"
					+ "            line-height: 24px;\n"
					+ "            color: #536387;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-checkbox-label a {\n"
					+ "            margin-left: 5px;\n"
					+ "            color: #6a64f1;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-input-checkbox {\n"
					+ "            position: absolute;\n"
					+ "            width: 1px;\n"
					+ "            height: 1px;\n"
					+ "            padding: 0;\n"
					+ "            margin: -1px;\n"
					+ "            overflow: hidden;\n"
					+ "            clip: rect(0, 0, 0, 0);\n"
					+ "            white-space: nowrap;\n"
					+ "            border-width: 0;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-checkbox-inner {\n"
					+ "            display: flex;\n"
					+ "            align-items: center;\n"
					+ "            justify-content: center;\n"
					+ "            width: 20px;\n"
					+ "            height: 20px;\n"
					+ "            margin-right: 16px;\n"
					+ "            margin-top: 2px;\n"
					+ "            border: 0.7px solid #dde3ec;\n"
					+ "            border-radius: 3px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-file {\n"
					+ "            padding: 12px;\n"
					+ "            font-size: 14px;\n"
					+ "            line-height: 24px;\n"
					+ "            color: rgba(83, 99, 135, 0.5);\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-file::-webkit-file-upload-button {\n"
					+ "            display: none;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-form-file:before {\n"
					+ "            content: 'Upload';\n"
					+ "            display: inline-block;\n"
					+ "            background: #EEEEEE;\n"
					+ "            border: 0.5px solid #E7E7E7;\n"
					+ "            border-radius: 3px;\n"
					+ "            padding: 3px 12px;\n"
					+ "            outline: none;\n"
					+ "            white-space: nowrap;\n"
					+ "            -webkit-user-select: none;\n"
					+ "            cursor: pointer;\n"
					+ "            color: #637381;\n"
					+ "            font-weight: 500;\n"
					+ "            font-size: 12px;\n"
					+ "            line-height: 16px;\n"
					+ "            margin-right: 10px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-btn {\n"
					+ "            font-size: 16px;\n"
					+ "            border-radius: 5px;\n"
					+ "            padding: 14px 25px;\n"
					+ "            border: none;\n"
					+ "            font-weight: 500;\n"
					+ "            background-color: #6a64f1;\n"
					+ "            color: white;\n"
					+ "            cursor: pointer;\n"
					+ "            margin-top: 25px;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-btn:hover {\n"
					+ "            box-shadow: 0px 3px 8px rgba(0, 0, 0, 0.05);\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-w-45 {\n"
					+ "            width: 45%;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-w-30 {\n"
					+ "            width: 30%;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-w-55 {\n"
					+ "            width: 55%;\n"
					+ "        }\n"
					+ "\n"
					+ "        .formbold-w-100 {\n"
					+ "            width: 100%;\n"
					+ "        }\n"
					+"\n"
					+ "		   .disabled {\n"
					+ "				pointer-events: none;\n"
					+ "			}\n"
					+ "    </style>\n"
					;
		
		HTMLPanel formHtml = new HTMLPanel(htmlFormCode);
		scrollPanel.add(formHtml);
		setWidget(scrollPanel);
	}

	// GWT FORM
//	public void show(final String domainName, final int domain, final String user, LinkedList<Scope> aviableScopes, final MarketingAction marketingAction, final AonMarketingActionTargetCreationPanelCallback callback) {
//		setWidth("800px");
//		getElement().getStyle().setProperty("padding", "1rem 0");
//		
//		FlowPanel rootPanel = new FlowPanel();
//		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
//		
//		final AonErrorPanel errorPanel = new AonErrorPanel();
//		errorPanel.addStyleName(AON.CSS.aonMarginTop());
//		rootPanel.add(errorPanel);
//		
//		FlowPanel tablePanel = new FlowPanel();
//		tablePanel.setStyleName(AON.CSS.aonScrollArea());
//		
//		KeyUpHandler keyUpHandler = new KeyUpHandler() {
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
//					callback.onCancel();	
//				}
//			}
//		};
//
//		FlexTable table = new FlexTable();
//		table.setStyleName(AON.CSS.aonTable());
//		table.addStyleName(AON.CSS.aonWidthAll());
//		
//		table.setWidget(0, 0, new InlineLabel("C\u00f3digo"));
//		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
//		marketingActionId.setText(marketingAction.getId().toString());
//		table.setWidget(0, 1, marketingActionId);
//		
//		table.setWidget(0, 2, new InlineLabel("Acci\u00f3n"));
//		table.getCellFormatter().setStyleName(0, 2, AON.CSS.aonTableLabel());
//		marketingActionDescription.setText(marketingAction.getDescription());
//		table.setWidget(0, 3, marketingActionDescription);
//		
//		table.setWidget(1, 0, new InlineLabel("Nombre/Raz\u00f3n Social"));
//		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
//		table.setWidget(1, 1, name);
//		name.addStyleName(AON.CSS.aonWidthAll());
//		table.getFlexCellFormatter().setColSpan(1, 1, 4);
//		
//		table.setWidget(2, 0, new InlineLabel("Documento"));
//		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
//		for(int i=0; i<DocumentType.values().length; i++) {
//			DocumentType documentTypeValue = DocumentType.values()[i];
//			documentType.addItem(documentTypeValue.getDescription(), documentTypeValue.ordinal() + "");
//		}
//		document.addValueChangeHandler(e -> {
//			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
//			setSelectedValueLB(documentType, documentTypeValidator.ordinal() + "");
//		});
//		table.setWidget(2, 1, document);
//		table.setWidget(2, 2, documentType);
//		
//		table.setWidget(2, 3, new InlineLabel("Pa\u00eds Emisi\u00f3n"));
//		table.getCellFormatter().setStyleName(2, 3, AON.CSS.aonTableLabel());
//		for(int i=0; i<Country.values().length; i++) {
//			Country country = Country.values()[i];
//			documentCountry.addItem(country.getName(), country.getIso2());
//		}
//		setSelectedValueLB(documentCountry, "ES");
//		documentCountry.getElement().getStyle().setProperty("max-width", "120px");
//		table.setWidget(2, 4, documentCountry);
//		
//		table.setWidget(3, 0, new InlineLabel("Direcci\u00f3n"));
//		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
//		StreetType.getSpanishTypes().forEach(streetTypeValue -> streetType.addItem(streetTypeValue.getDescription(), streetTypeValue.getAeatCode()));
//		setSelectedValueLB(streetType, "CL");
//		table.setWidget(3, 1, streetType);
//		table.setWidget(3, 2, address);
//		table.setWidget(3, 3, new InlineLabel("N\u00famero"));
//		table.getCellFormatter().setStyleName(3, 3, AON.CSS.aonTableLabel());
//		table.setWidget(3, 4, number);
//		
//		table.setWidget(4, 0, new InlineLabel("C\u00f3digo Postal"));
//		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
//		zip.addValueChangeHandler(e -> {
//			if(AonStringUtils.isNotBlank(zip.getValue()) && zip.getValue().length() >= 2) {
//				setSelectedValueLB(province, AonStringUtils.substring(zip.getValue(), 0, 2));
//			}
//		});
//		table.setWidget(4, 1, zip);
//		table.setWidget(4, 2, new InlineLabel("Provincia"));
//		table.getCellFormatter().setStyleName(4, 2, AON.CSS.aonTableLabel());
//		aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).forEach(geozone -> province.addItem(geozone.getName(), geozone.getCode()));
//		table.setWidget(4, 3, province);
//		
//		table.setWidget(5, 0, new InlineLabel("Localidad"));
//		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
//		table.setWidget(5, 1, city);
//		
//		table.setWidget(6, 0, new InlineLabel("M\u00f3vil"));
//		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
//		table.setWidget(6, 1, phone);
//		
//		table.setWidget(7, 0, new InlineLabel("Email"));
//		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
//		table.setWidget(7, 1, email);
//		email.addStyleName(AON.CSS.aonWidthAll());
//		table.getFlexCellFormatter().setColSpan(7, 1, 4);
//		
//		tablePanel.add( table );
//		rootPanel.add( tablePanel );
//		
//		FlowPanel buttons = new FlowPanel();
//    	buttons.setStyleName(AON.CSS.aonTextCenter());
//    	
//    	final Button okButton = new Button();
//    	okButton.setStyleName(AON.CSS.aonOkButton());
//    	okButton.setText( AON.MSG.accept());
//    	okButton.addKeyUpHandler( keyUpHandler);
//    	
//    	okButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				okButton.setEnabled(false);
//				callback.onAccept(createActionTargetJSON());
//			}
//			
//		});
//    	
//    	buttons.add(okButton);
//    	
//    	final Button cancelButton = new Button();
//    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
//    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
//    	cancelButton.setText( AON.MSG.cancelAction());
//    	cancelButton.addKeyUpHandler( keyUpHandler);
//    	cancelButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				cancelButton.setEnabled(false);
//				callback.onCancel();
//			}
//		});
//    	buttons.add(cancelButton);
//    	rootPanel.add(buttons);
//		setWidget(rootPanel);
//		
//		Scheduler.get().scheduleDeferred(new Command() {
//	        public void execute() {
//	        	name.setFocus(true);
//	        	onResize();
//	        }
//	    });		
//		
//	}
	
	private boolean canBeCastToInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

//	private void setSelectedValueLB(ListBox lBox, String str) {
//	    String text = str;
//	    int indexToFind = 0;
//	    for (int i = 0; i < lBox.getItemCount(); i++) {
//	        if (lBox.getValue(i).equals(text)) {
//	            indexToFind = i;
//	            break;
//	        }
//	    }
//	    lBox.setSelectedIndex(indexToFind);
//	}

//	private JSONObject createActionTargetJSON() {
//		JSONObject actionTarget = new JSONObject();
//		
//		JSONObject action = new JSONObject();
//		action.put("id", new JSONString(marketingActionId.getText()));
//		action.put("description", new JSONString(marketingActionDescription.getText()));
//		actionTarget.put("marketingAction", action);
//		
//		JSONObject target = new JSONObject();
//		target.put("name", new JSONString(name.getValue()));
//		target.put("documentType", new JSONString(documentType.getSelectedValue())); // DNI, CIF, NIE, OTROS
//		target.put("documentCountry", new JSONString(documentCountry.getSelectedValue())); 
//		target.put("document", new JSONString(document.getValue()));
//		
//		target.put("streetType", new JSONString(streetType.getSelectedValue()));
//		target.put("address", new JSONString(address.getValue()));
//		target.put("number", new JSONString(number.getValue()));
//		target.put("zip", new JSONString(zip.getValue()));
//		target.put("geozone", new JSONString(aviableGeozones.stream().filter(geozone -> geozone.getCode().equals(province.getSelectedValue())).findFirst().get().getId().toString()));
//		target.put("geozoneCode", new JSONString(province.getSelectedValue()));
//		target.put("geozoneName", new JSONString(aviableGeozones.stream().filter(geozone -> geozone.getCode().equals(province.getSelectedValue())).findFirst().get().getName()));
//		target.put("city", new JSONString(city.getValue()));
//		target.put("phone", new JSONString(phone.getValue()));
//		target.put("email", new JSONString(email.getValue()));
//		actionTarget.put("target", target);
//		
//		return actionTarget;
//	}
	
	protected abstract void onResize();

}
