package net.aonsolutions.aon.gwt.commercial.client.commission;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.commercial.JsCommission;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CommissionCalculate extends AonTemplate2{

	private AonData aonData;
	private API API;
	private CommissionCalculate me = this;

	public CommissionCalculate(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getId());
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
		CommissionCalculateToolbar toolbar = new CommissionCalculateToolbar("Comisiones") {
			
			@Override
			protected void clean() {
				content();
			}
			
			@Override
			protected void calculate() {
				CommissionCalculateContent ccc = (CommissionCalculateContent) getContent().getWidget();
				JSONObject json = new JSONObject();
				if(ccc.getSeller() != null) json.put("seller", new JSONNumber(ccc.getSeller()));
				if(ccc.getFromDate() != null) json.put("from_date", new JSONString(ccc.getFromDate().toString()));
				if(ccc.getToDate() != null) json.put("to_date", new JSONString(ccc.getToDate().toString()));
				if(ccc.getSeries() != null) json.put("series", new JSONString(ccc.getSeries()));
				if(ccc.getFromNumber() != null) json.put("from_number", new JSONNumber(ccc.getFromNumber()));
				if(ccc.getToNumber() != null) json.put("to_number", new JSONNumber(ccc.getToNumber()));
				if(ccc.getConfidential() != null) json.put("confidential", new JSONNumber(ccc.getConfidential() ? 1 : 0));
				if(ccc.getWorkplace() != null) json.put("workplace", new JSONNumber(ccc.getWorkplace()));
				String requestData = JsonUtils.stringify(json.getJavaScriptObject());

				getAPI().getCommission().commissionCalculate(requestData);
				
				content(new CommissionCalculatePrincipal(me));
			}
		};
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
		
		Button commissionCalculate = new Button("C\u00e1lculo de Comisiones");
		commissionCalculate.setStyleName("aon-editDataTable-button");
		commissionCalculate.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		Button commissionCalculateGrid = new Button("Control de Comisiones Calculadas");
		commissionCalculateGrid.setStyleName("aon-editDataTable-button");
		commissionCalculateGrid.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		Button commissionType = new Button("Tipos de Comisi\u00f3n");
		commissionType.setStyleName("aon-editDataTable-button");
		commissionType.addStyleName(AON.AON_CSS.aonDocumentalTitle());
	
		Button commissionSection = new Button("Definici\u00f3n de tramos de comisiones");
		commissionSection.setStyleName("aon-editDataTable-button");
		commissionSection.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		commissionCalculate.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(true);
				toolbar.setCalculateVisible(true);
				
				content();
			}
		});
		
		commissionCalculateGrid.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(false);
				toolbar.setCalculateVisible(false);
				
				content(new CommissionCalculatePrincipal(me));
			}
		});
		
		commissionType.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		commissionSection.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			}
		});

		menuPanel.add(commissionCalculate);
		menuPanel.add(commissionCalculateGrid);
		menuPanel.add(commissionType);
		menuPanel.add(commissionSection);
		
		setWestContent(menuPanel);
	}
	
	public AonData getAonData() {
		return aonData;
	}

	public API getAPI() {
		return API;
	}

}
