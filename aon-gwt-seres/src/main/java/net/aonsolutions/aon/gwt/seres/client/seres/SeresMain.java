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
import com.google.gwt.user.client.ui.Widget;

public class SeresMain extends AonTemplate2{

	private API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
	private Button sendAll;
	private Button retrieveAll;
	private Button processAll;
	
	final String OUTCOME_DELIVERY = "outcome_delivery";
	final String OUTCOME_INVOICE = "outcome_invoice";
	final String INCOME_SALES = "income_sales";
	final String INCOME_INVOICE = "income_invoice";
	final String INGENET_DELIVERY = "ingenet_delivery";

	
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

	public Button getRetrieveAll() {
		return retrieveAll;
	}
	
	public Button getProcessAll() {
		return processAll;
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
		retrieveAll = toolbar.addButton("Recuperar", AON.AON_CSS.aonIconImport());
		processAll = toolbar.addButton("Procesar", AON.AON_CSS.aonIconSave());
		
		cleanToolbarButtons();
		
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid grid = (ContentGrid) slp.getWidget();
				grid.send(getFilterMap().get("seres").get(0));
			}
		});
		
		retrieveAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid grid = (ContentGrid) slp.getWidget();
				grid.retrieve(getFilterMap().get("seres").get(0));
			}
		});
		
		processAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGridIngenet grid = (ContentGridIngenet) slp.getWidget();
				grid.process(getFilterMap().get("seres").get(0));
			}
		});
	
		setToolbar(toolbar);
	}
	
	protected void cleanToolbarButtons() {
		sendAll.setVisible(false);
		retrieveAll.setVisible(false);
		processAll.setVisible(false);
	}
	
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		
		menuPanel.add(createMenuPanelButton("Visi\u00F3n global", "Visi\u00F3n global", null, false, false));
		
		menuPanel.add(createMenuPanelLabel("Env\u00EDo de Ficheros"));
		menuPanel.add(createMenuPanelButton("Albaranes", "Env\u00EDo de Albaranes", OUTCOME_DELIVERY, true, true));
		menuPanel.add(createMenuPanelButton("Facturas", "Env\u00EDo de Facturas", OUTCOME_INVOICE));
		
		menuPanel.add(createMenuPanelLabel("Recepci\u00F3n de Ficheros"));
		menuPanel.add(createMenuPanelButton("Pedidos", "Recepci\u00F3n de Pedidos", INCOME_SALES, true, true));
		menuPanel.add(createMenuPanelButton("Facturas", "Recepci\u00F3n de Facturas", INCOME_INVOICE, true, true));
		
		menuPanel.add(createMenuPanelLabel("Ingenet"));
		menuPanel.add(createMenuPanelButton("Albaranes", "Recepci\u00F3n de Albaranes Ingenet", INGENET_DELIVERY));
		
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
		return createMenuPanelButton(buttonName, title, action, true, false);
	}
	
	private Button createMenuPanelButton(String buttonName, String title, String action, boolean padding, boolean disabled){
		Button button = new Button(buttonName);
		button.setStyleName("aon-editDataTable-button");
		button.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		if(disabled)
			button.addStyleName(AON.AON_CSS.aonTextLineThrough());
		if(padding)
			button.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		button.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				cleanMenuPanelButtons();
				button.addStyleName(AON.AON_CSS.aonBold());
				cleanToolbarButtons();
				LinkedList<String> list = new LinkedList<>();
				list.add(action);
				getFilterMap().put("seres",list);
				SeresPrincipal p = (SeresPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				// TODO fp.setCheckVisible(true); 
				fp.setCheckVisible(false);
				fp.setTitle(title);
				p.gridContent(action);
				p.closeFootPanel();
			}
		});
		return button;
	}
	
	private void cleanMenuPanelButtons() {
		VerticalPanel menuPanel = (VerticalPanel) getWestContent().getWidget();
		for(int i=0; i<menuPanel.getWidgetCount(); i++){
			Widget widget = menuPanel.getWidget(i);
			widget.removeStyleName(AON.AON_CSS.aonBold());
		}
	}
	
	private void content() {
		setContent(new SeresPrincipal(this));
	}

	public static native void consoleLog( String message) 
	/*-{
	    console.log( message );
	}-*/;

}
