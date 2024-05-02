package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonMarketingActionTargetCreationPanel extends SimplePanel {
	
	public static interface AonMarketingActionTargetCreationPanelCallback {
		void onAccept(JSONObject json);
		void onCancel();
	}

	private Label marketingActionId = new Label();
	private Label marketingActionDescription = new Label();
	
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
	
	private LinkedList<GeoZone> aviableGeozones;
	
	public AonMarketingActionTargetCreationPanel(final String domainName,final int domain, final String user, LinkedList<Scope> aviableScopes, LinkedList<GeoZone> aviableGeozones, final MarketingAction marketingAction, final AonMarketingActionTargetCreationPanelCallback aonMarketingActionTargetCreationPanelCallback) {
		this.aviableGeozones = aviableGeozones;
		show(domainName, domain, user, aviableScopes, marketingAction, aonMarketingActionTargetCreationPanelCallback);
	}

	public void show(final String domainName, final int domain, final String user, LinkedList<Scope> aviableScopes, final MarketingAction marketingAction, final AonMarketingActionTargetCreationPanelCallback callback) {
		setWidth("800px");
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
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
		table.addStyleName(AON.CSS.aonWidthAll());
		
		table.setWidget(0, 0, new InlineLabel("C\u00f3digo"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		marketingActionId.setText(marketingAction.getId().toString());
		table.setWidget(0, 1, marketingActionId);
		
		table.setWidget(0, 2, new InlineLabel("Acci\u00f3n"));
		table.getCellFormatter().setStyleName(0, 2, AON.CSS.aonTableLabel());
		marketingActionDescription.setText(marketingAction.getDescription());
		table.setWidget(0, 3, marketingActionDescription);
		
		table.setWidget(1, 0, new InlineLabel("Nombre/Raz\u00f3n Social"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		table.setWidget(1, 1, name);
		name.addStyleName(AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(1, 1, 4);
		
		table.setWidget(2, 0, new InlineLabel("Documento"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		for(int i=0; i<DocumentType.values().length; i++) {
			DocumentType documentTypeValue = DocumentType.values()[i];
			documentType.addItem(documentTypeValue.getDescription(), documentTypeValue.ordinal() + "");
		}
		document.addValueChangeHandler(e -> {
			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
			setSelectedValueLB(documentType, documentTypeValidator.ordinal() + "");
		});
		table.setWidget(2, 1, document);
		table.setWidget(2, 2, documentType);
		
		table.setWidget(2, 3, new InlineLabel("Pa\u00eds Emisi\u00f3n"));
		table.getCellFormatter().setStyleName(2, 3, AON.CSS.aonTableLabel());
		for(int i=0; i<Country.values().length; i++) {
			Country country = Country.values()[i];
			documentCountry.addItem(country.getName(), country.getIso2());
		}
		setSelectedValueLB(documentCountry, "ES");
		documentCountry.getElement().getStyle().setProperty("max-width", "120px");
		table.setWidget(2, 4, documentCountry);
		
		table.setWidget(3, 0, new InlineLabel("Direcci\u00f3n"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		StreetType.getSpanishTypes().forEach(streetTypeValue -> streetType.addItem(streetTypeValue.getDescription(), streetTypeValue.getAeatCode()));
		setSelectedValueLB(streetType, "CL");
		table.setWidget(3, 1, streetType);
		table.setWidget(3, 2, address);
		table.setWidget(3, 3, new InlineLabel("N\u00famero"));
		table.getCellFormatter().setStyleName(3, 3, AON.CSS.aonTableLabel());
		table.setWidget(3, 4, number);
		
		table.setWidget(4, 0, new InlineLabel("C\u00f3digo Postal"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		zip.addValueChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(zip.getValue()) && zip.getValue().length() >= 2) {
				setSelectedValueLB(province, AonStringUtils.substring(zip.getValue(), 0, 2));
			}
		});
		table.setWidget(4, 1, zip);
		table.setWidget(4, 2, new InlineLabel("Provincia"));
		table.getCellFormatter().setStyleName(4, 2, AON.CSS.aonTableLabel());
		aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).forEach(geozone -> province.addItem(geozone.getName(), geozone.getCode()));
		table.setWidget(4, 3, province);
		
		table.setWidget(5, 0, new InlineLabel("Localidad"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		table.setWidget(5, 1, city);
		
		table.setWidget(6, 0, new InlineLabel("M\u00f3vil"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		table.setWidget(6, 1, phone);
		
		table.setWidget(7, 0, new InlineLabel("Email"));
		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		table.setWidget(7, 1, email);
		email.addStyleName(AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(7, 1, 4);
		
		tablePanel.add( table );
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
				callback.onAccept(createActionTargetJSON());
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
	        	name.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private boolean canBeCastToInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
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

	private JSONObject createActionTargetJSON() {
		JSONObject actionTarget = new JSONObject();
		
		JSONObject action = new JSONObject();
		action.put("id", new JSONString(marketingActionId.getText()));
		action.put("description", new JSONString(marketingActionDescription.getText()));
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
		target.put("geozone", new JSONString(aviableGeozones.stream().filter(geozone -> geozone.getCode().equals(province.getSelectedValue())).findFirst().get().getId().toString()));
		target.put("geozoneCode", new JSONString(province.getSelectedValue()));
		target.put("geozoneName", new JSONString(aviableGeozones.stream().filter(geozone -> geozone.getCode().equals(province.getSelectedValue())).findFirst().get().getName()));
		target.put("city", new JSONString(city.getValue()));
		target.put("phone", new JSONString(phone.getValue()));
		target.put("email", new JSONString(email.getValue()));
		actionTarget.put("target", target);
		
		return actionTarget;
	}
	
	protected abstract void onResize();

}
