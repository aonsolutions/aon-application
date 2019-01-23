package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CommissionCalculate extends AonTemplate2{

	private AonData aonData;
	private API API;
	private CommissionCalculate me = this;
	JSONObject calcJson = new JSONObject();
	private String type = "offer";
	
	public JSONObject getCalcJson() {
		return calcJson;
	}
	
	public Boolean isOffer() {
		return "offer".equalsIgnoreCase(type);
	}
	
	public void setType(String type){ 
		this.type = type;
	}
	
	public Boolean isInvoice() {
		return "invoice".equalsIgnoreCase(type);
	}

	public CommissionCalculate(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(), aonData.getDomain().getName(),
				aonData.getDomain().getId(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}

	private void startApplication() {
		toolbar();
		content();
		westContent();
	}

	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		
		Toolbar toolbar = new Toolbar("Comisiones");
		toolbar.addButton("Limpiar", AON.AON_CSS.aonIconRubber()).addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				content();
			}
		});
		
		toolbar.addButton("Calcular", AON.AON_CSS.aonIconSave()).addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				String requestData = JsonUtils.stringify(getCalcJson().getJavaScriptObject());
				if(isOffer()) {
					getAPI().getCommission().offerCommissionCalculate(requestData);
				} else if(isInvoice()) {
					getAPI().getCommission().invoiceCommissionCalculate(requestData);
				}
				HashMap<String, LinkedList<String>> filterMap = new HashMap<String, LinkedList<String>>();
				
			/*
				LinkedList<String> list = new LinkedList<>();
				if(getCalcJson().containsKey("seller")) {
					list.add(getCalcJson().get("seller") + "");
					filterMap.put("seller", list);
				}
				
				if(getCalcJson().containsKey("from_date")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("from_date") + "");
					filterMap.put("from", list);
				}
				
				if(getCalcJson().containsKey("to_date")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("to_date") + "");
					filterMap.put("to", list);
				}
				
				if(getCalcJson().containsKey("series")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("series") + "");
					filterMap.put("series", list);
				}

				if(getCalcJson().containsKey("from_number")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("from_number") + "");
					filterMap.put("number_from", list);
				}
			
				if(getCalcJson().containsKey("to_number")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("to_number") + "");
					filterMap.put("number_to", list);
				}
				
				if(getCalcJson().containsKey("target")) {
					list = new LinkedList<>();
					list.add(getCalcJson().get("target") + "");
					filterMap.put("target", list);
				}
			*/
				content(new CommissionCalculatePrincipal(me, filterMap));				
			}
		});

		setToolbar(toolbar);
	}
	
	private void content() {
		setContent(new CommissionCalculateContent(this));
	}
	
	protected void content(Widget widget) {
		setContent(widget);
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 302);
	
		VerticalPanel menuPanel = new VerticalPanel();

		Button invoiceCommissionCalculate = new Button("C\u00e1lculo de Comisiones");
		invoiceCommissionCalculate.setStyleName("aon-editDataTable-button");
		invoiceCommissionCalculate.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		Button invoiceCommissionCalculateGrid = new Button("Control de Comisiones Calculadas");
		invoiceCommissionCalculateGrid.setStyleName("aon-editDataTable-button");
		invoiceCommissionCalculateGrid.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		Button commissionType = new Button("Tipos de Comisi\u00f3n");
		commissionType.setStyleName("aon-editDataTable-button");
		commissionType.addStyleName(AON.AON_CSS.aonDocumentalTitle());
	
		Button commissionSection = new Button("Definici\u00f3n de tramos de comisiones");
		commissionSection.setStyleName("aon-editDataTable-button");
		commissionSection.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		invoiceCommissionCalculate.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				Toolbar toolbar = (Toolbar) getToolbar().getWidget();
				toolbar.getButtonPanel().getWidget(0).setVisible(true);// CLEAN BUTTON
				toolbar.getButtonPanel().getWidget(1).setVisible(true);// CALCULATE BUTTON
				//type = "invoice";
				content();
			}
		});
		
		invoiceCommissionCalculateGrid.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				Toolbar toolbar = (Toolbar) getToolbar().getWidget();
				toolbar.getButtonPanel().getWidget(0).setVisible(false);// CLEAN BUTTON
				toolbar.getButtonPanel().getWidget(1).setVisible(false);// CALCULATE BUTTON
				//type = "invoice";
				content(new CommissionCalculatePrincipal(me));
			}
		});
		
		commissionType.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		commissionSection.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			}
		});
		
		menuPanel.add(invoiceCommissionCalculate);
		menuPanel.add(invoiceCommissionCalculateGrid);
	//	menuPanel.add(commissionType);
	//	menuPanel.add(commissionSection);
		
		setWestContent(menuPanel);
	}
	
	public AonData getAonData() {
		return aonData;
	}

	public API getAPI() {
		return API;
	}

}
