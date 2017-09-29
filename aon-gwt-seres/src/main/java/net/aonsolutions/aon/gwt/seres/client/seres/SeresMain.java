package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SeresMain extends AonTemplate2{

	private API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
	private Button sendAll;
	
	final String OUTCOME_DELIVERY = "outcome_delivery";
	final String OUTCOME_INVOICE = "outcome_invoice";
	final String INCOME_SALES = "income_sales";
	final String INCOME_INVOICE = "income_invoice";
	final String INGENET_DELIVERY = "ign_delivery";
	final String[] AVAILABLE_COMMANDS = { OUTCOME_DELIVERY, OUTCOME_INVOICE, INCOME_SALES, INCOME_INVOICE,
			INGENET_DELIVERY };

	
	public SeresMain(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public Button getSendAll() {
		return sendAll;
	}
	
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}
	
	private void startApplication() {
		initializeFilterMap();
		toolbar();
		westContent();
		content();
	}
	
	public void initializeFilterMap() {
		filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("summary");
		filterMap.put("seres",list);
		
		Date date = new Date();
		list = new LinkedList<>();
		list.add(Long.toString(date.getTime()));
		filterMap.put("from",list);
		
		list = new LinkedList<>();
		list.add("true");
		filterMap.put("pending", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("sent", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("error", list);
		
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Comunicaciones SERES") {};
		sendAll = toolbar.addButton("Enviar", AON.AON_CSS.aonIconSave());
		sendAll.setVisible(false);
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid ig = (ContentGrid) slp.getWidget();
				ig.send(getFilterMap().get("seres").get(0)); // TODO
			}
		});
	
		setToolbar(toolbar);
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		
		menuPanel.add(createMenuPanelButton("Vision global", "Vision global", null, false));
		
		menuPanel.add(createMenuPanelLabel("Ficheros Enviados"));
		menuPanel.add(createMenuPanelButton("Albaranes", "Albaranes enviados", OUTCOME_DELIVERY));
		menuPanel.add(createMenuPanelButton("Facturas", "Facturas enviadas", OUTCOME_INVOICE));
		
		menuPanel.add(createMenuPanelLabel("Ficheros Recibidos"));
		menuPanel.add(createMenuPanelButton("Pedidos", "Pedidos recibidos", INCOME_SALES));
		menuPanel.add(createMenuPanelButton("Facturas", "Facturas recibidas", INCOME_INVOICE));
		
		menuPanel.add(createMenuPanelLabel("Ingenet"));
		menuPanel.add(createMenuPanelButton("Albaranes Ingenet", "Albaranes Ingenet", INGENET_DELIVERY));
		
		setWestContent(menuPanel);
	}
	
	private Label createMenuPanelLabel(String text){
		Label label = new Label(text);
		label.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		label.addStyleName(AON.AON_CSS.aonBorderBottom());
		label.addStyleName(AON.AON_CSS.aonMarginLeft10());
		label.addStyleName(AON.AON_CSS.aonMarginRight10());
		return label;
	}
	
	private Button createMenuPanelButton(String buttonName, String title, String action){
		return createMenuPanelButton(buttonName, title, action, true);
	}
	
	private Button createMenuPanelButton(String buttonName, String title, String action, boolean padding){
		Button pagosButton = new Button(buttonName);
		pagosButton.setStyleName("aon-editDataTable-button");
		pagosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		if(padding){
			pagosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		}
		pagosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add(action);
				getFilterMap().put("seres",list);
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle(title);
				p.gridContent(action);
			}
		});
		return pagosButton;
	}
	
	private void content() {
		setContent(new SeresPrincipal(this));
	}

}
