package net.aonsolutions.aon.gwt.communication.client;

import java.util.Arrays;
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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.communication.client.ingenet.ContentGridIngenet;
import net.aonsolutions.aon.gwt.communication.client.seres.ContentGrid;
import net.aonsolutions.aon.gwt.communication.shared.CommunicationTarget;

public class CommunicationMain extends AonTemplate2{

	private API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
	private Button sendAll;
	private Button retrieveAll;
	private Button processAll;
	private Button reopenAll;
	
	
	public CommunicationMain(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
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
	
	public Button getReopenAll() {
		return reopenAll;
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
		filterMap.put("communication",list);
		
		Date date = new Date();
		list = new LinkedList<>();
		list.add(Long.toString(date.getTime()));
		filterMap.put("from",list);
		
		filterMap.put("pending", new LinkedList<>(Arrays.asList(new String[] {"true"})));
		filterMap.put("processed", new LinkedList<>(Arrays.asList(new String[] {"false"})));
		filterMap.put("error", new LinkedList<>(Arrays.asList(new String[] {"true"})));
		
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Consola de comunicaciones") {};
		
		sendAll = toolbar.addButton("Enviar", AON.AON_CSS.aonIconSave());
		retrieveAll = toolbar.addButton("Recuperar", AON.AON_CSS.aonIconImport());
		processAll = toolbar.addButton("Procesar", AON.AON_CSS.aonIconSave());
		reopenAll = toolbar.addButton("Reabrir", AON.AON_CSS.aonIconRefresh());
		
		cleanToolbarButtons();
		
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CommunicationPrincipal p = (CommunicationPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid grid = (ContentGrid) slp.getWidget();
				grid.send(getFilterMap().get("communication").get(0));
			}
		});
		
		retrieveAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CommunicationPrincipal p = (CommunicationPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid grid = (ContentGrid) slp.getWidget();
				grid.retrieve(getFilterMap().get("communication").get(0));
			}
		});
		
		processAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CommunicationPrincipal p = (CommunicationPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGridIngenet grid = (ContentGridIngenet) slp.getWidget();
				grid.process(getFilterMap().get("communication").get(0));
			}
		});
		
		reopenAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CommunicationPrincipal p = (CommunicationPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				ContentGrid grid = (ContentGrid) slp.getWidget();
				grid.reopen(getFilterMap().get("communication").get(0));
			}
		});
	
		setToolbar(toolbar);
	}
	
	public void cleanToolbarButtons() {
		sendAll.setVisible(false);
		retrieveAll.setVisible(false);
		processAll.setVisible(false);
		reopenAll.setVisible(false);
	}
	
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		
		VerticalPanel menuPanel = new VerticalPanel();
		menuPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		
		menuPanel.add(createMenuPanelButton("Visi\u00F3n global", "Visi\u00F3n global", null, false, false));
		
		// TODO remove from aon-gwt-seres and implement here
//		menuPanel.add(createMenuPanelLabel("SERES - Env\u00EDo de Ficheros"));
//		menuPanel.add(createMenuPanelButton("Albaranes", "Env\u00EDo de Albaranes", CommunicationTarget.OUTCOME_DELIVERY));
//		menuPanel.add(createMenuPanelButton("Facturas", "Env\u00EDo de Facturas", CommunicationTarget.OUTCOME_INVOICE));
		
		// TODO file retrieve options for sales and invoices
//		menuPanel.add(createMenuPanelLabel("SERES - Recepci\u00F3n de Ficheros"));
//		menuPanel.add(createMenuPanelButton("Pedidos", "Recepci\u00F3n de Pedidos", CommunicationTarget.INCOME_SALES, true, true));
//		menuPanel.add(createMenuPanelButton("Facturas", "Recepci\u00F3n de Facturas", CommunicationTarget.INCOME_INVOICE, true, true));
		
		menuPanel.add(createMenuPanelLabel("Ingenet - Env\u00EDo de Ficheros"));
		menuPanel.add(createMenuPanelButton("Pedidos", "Env\u00EDo de Pedidos Ingenet", CommunicationTarget.INGENET_SALES));
		
		menuPanel.add(createMenuPanelLabel("Ingenet - Recepci\u00F3n de Ficheros"));
		menuPanel.add(createMenuPanelButton("Albaranes", "Recepci\u00F3n de Albaranes Ingenet", CommunicationTarget.INGENET_DELIVERY));
		
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
	
	private Button createMenuPanelButton(String buttonName, String title, CommunicationTarget action){
		return createMenuPanelButton(buttonName, title, action, true, false);
	}
	
	private Button createMenuPanelButton(String buttonName, String title, CommunicationTarget action, boolean padding, boolean disabled){
		Button button = new Button(buttonName);
		button.setStyleName("aon-editDataTable-button");
		button.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		button.addStyleName(AON.AON_CSS.aonWidthAll());
		button.addStyleName(AON.AON_CSS.aonTextLeft());
		button.addStyleName(AON.AON_CSS.aonMarginLeft10());
		button.addStyleName(AON.AON_CSS.aonMarginRight10());
		if(disabled)
			button.addStyleName(AON.AON_CSS.aonTextLineThrough());
		if(padding)
			button.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		else {
			button.addStyleName(AON.AON_CSS.aonBorderBottom());
			button.addStyleName(AON.AON_CSS.aonNopadding());
		}
		button.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				button.addStyleName(AON.AON_CSS.aonBackgroundWhite());
			}
		});
		button.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				button.removeStyleName(AON.AON_CSS.aonBackgroundWhite());
			}
		});
		
		button.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				cleanMenuPanelButtons();
				button.addStyleName(AON.AON_CSS.aonBold());
				cleanToolbarButtons();
				initializeFilterMap();
				
				CommunicationPrincipal p = (CommunicationPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setTitle(title);
				p.gridContent(action);
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
		setContent(new CommunicationPrincipal(this));
	}

	public static native void consoleLog( String message) 
	/*-{
	    console.log( message );
	}-*/;

}
