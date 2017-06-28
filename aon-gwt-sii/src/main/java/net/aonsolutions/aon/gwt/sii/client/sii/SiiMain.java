package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SiiMain extends AonTemplate2{

	private API API;
	HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
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
	
	public SiiMain(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
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
		list.add("emitidas");
		filterMap.put("sii",list);
		
		Date date = new Date(2017-1900, 5, 1);
		list = new LinkedList<>();
		list.add(Long.toString(date.getTime()));
		filterMap.put("from",list);
		
		list = new LinkedList<>();
		list.add("true");
		filterMap.put("pending", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("sent", list);
		
	}
	
	Button sendAll;
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Suministro Inmediato de Informacion") {};
		sendAll = toolbar.addButton("Enviar", AON.AON_CSS.aonIconSave());
		sendAll.setVisible(false);
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				InvoiceGrid ig = (InvoiceGrid) slp.getWidget();
				ig.sendSii();
			}
		});
	
		setToolbar(toolbar);
	}
	
	public Button getSendAll() {
		return sendAll;
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 350);
		
		VerticalPanel menuPanel = new VerticalPanel();
		
		Button facturasEmitidasButton = new Button("Facturas Emitidas");
		facturasEmitidasButton.setStyleName("aon-editDataTable-button");
		facturasEmitidasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		facturasEmitidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {				
				LinkedList<String> list = new LinkedList<>();
				list.add("emitidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				p.gridContent();
			}
		});
		menuPanel.add(facturasEmitidasButton);
		
		Button facturasRecibidasButton = new Button("Facturas Recibidas");
		facturasRecibidasButton.setStyleName("aon-editDataTable-button");
		facturasRecibidasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		facturasRecibidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add("recibidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				p.gridContent();
			}
		});
		menuPanel.add(facturasRecibidasButton);

	/*	Button bienesInversionButton = new Button("Bienes de Inversion");
		bienesInversionButton.setStyleName("aon-editDataTable-button");
		bienesInversionButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		bienesInversionButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add("bienes");
				getFilterMap().put("sii",list);
				content();
			}
		});
		menuPanel.add(bienesInversionButton);
	*/	
		Button operacionesIntracomunitariasButton = new Button("Operaciones Intracomunitarias");
		operacionesIntracomunitariasButton.setStyleName("aon-editDataTable-button");
		operacionesIntracomunitariasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		operacionesIntracomunitariasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add("intracomunitarias");
				getFilterMap().put("sii",list);
				content();
			}
		});
		menuPanel.add(operacionesIntracomunitariasButton);
		
		setWestContent(menuPanel);
	}
	
	private void content() {
		setContent(new SiiPrincipal(this));
	}

}
