package net.aonsolutions.aon.gwt.commercial.client.commission;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
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
	
	public Boolean isInvoice() {
		return "invoice".equalsIgnoreCase(type);
	}

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
				String requestData = JsonUtils.stringify(getCalcJson().getJavaScriptObject());
				if(isOffer()) {
					getAPI().getCommission().offerCommissionCalculate(requestData);
				} else if(isInvoice()) {
					getAPI().getCommission().invoiceCommissionCalculate(requestData);
				}
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
		commissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		
		Button offerCommissionCalculate = new Button("Presupuestos");
		offerCommissionCalculate.setStyleName("aon-editDataTable-button");
		offerCommissionCalculate.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		offerCommissionCalculate.getElement().getStyle().setPaddingLeft(30, Unit.PX);

		Button invoiceCommissionCalculate = new Button("Facturas");
		invoiceCommissionCalculate.setStyleName("aon-editDataTable-button");
		invoiceCommissionCalculate.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		invoiceCommissionCalculate.getElement().getStyle().setPaddingLeft(30, Unit.PX);
		
		Button commissionCalculateGrid = new Button("Control de Comisiones Calculadas");
		commissionCalculateGrid.setStyleName("aon-editDataTable-button");
		commissionCalculateGrid.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		Button offerCommissionCalculateGrid = new Button("Presupuestos");
		offerCommissionCalculateGrid.setStyleName("aon-editDataTable-button");
		offerCommissionCalculateGrid.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		offerCommissionCalculateGrid.getElement().getStyle().setPaddingLeft(30, Unit.PX);

		Button invoiceCommissionCalculateGrid = new Button("Facturas");
		invoiceCommissionCalculateGrid.setStyleName("aon-editDataTable-button");
		invoiceCommissionCalculateGrid.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		invoiceCommissionCalculateGrid.getElement().getStyle().setPaddingLeft(30, Unit.PX);
		
		Button commissionType = new Button("Tipos de Comisi\u00f3n");
		commissionType.setStyleName("aon-editDataTable-button");
		commissionType.addStyleName(AON.AON_CSS.aonDocumentalTitle());
	
		Button commissionSection = new Button("Definici\u00f3n de tramos de comisiones");
		commissionSection.setStyleName("aon-editDataTable-button");
		commissionSection.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		offerCommissionCalculate.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(true);
				toolbar.setCalculateVisible(true);
				type = "offer";
				content();
			}
		});
		
		invoiceCommissionCalculate.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(true);
				toolbar.setCalculateVisible(true);
				type = "invoice";
				content();
			}
		});
		
		offerCommissionCalculateGrid.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(false);
				toolbar.setCalculateVisible(false);
				type = "offer";
				content(new CommissionCalculatePrincipal(me));
			}
		});
		
		invoiceCommissionCalculateGrid.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				
				CommissionCalculateToolbar toolbar = (CommissionCalculateToolbar) getToolbar().getWidget();
				toolbar.setCleanVisible(false);
				toolbar.setCalculateVisible(false);
				type = "invoice";
				content(new CommissionCalculatePrincipal(me));
			}
		});
		
		commissionType.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);		
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		commissionSection.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				invoiceCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculate.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				offerCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				invoiceCommissionCalculateGrid.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionType.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				commissionSection.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			}
		});
		
		menuPanel.add(commissionCalculate);
		menuPanel.add(offerCommissionCalculate);
		menuPanel.add(invoiceCommissionCalculate);
		menuPanel.add(commissionCalculateGrid);
		menuPanel.add(offerCommissionCalculateGrid);
		menuPanel.add(invoiceCommissionCalculateGrid);
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
