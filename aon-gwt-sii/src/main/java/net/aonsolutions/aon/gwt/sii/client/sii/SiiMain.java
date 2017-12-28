package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public class SiiMain extends AonTemplate2{

	final ISiiAsync impl = GWT.create(ISii.class);
	private API API;
	HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	private Administration administration;

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
		impl.getAdministration(aonData.getDomain(), aonData.getUser().getLogin(), new AsyncCallback<Administration>() {
			
			@Override
			public void onSuccess(Administration result) {
				administration = result;
				initializeFilterMap();
				toolbar();
				westContent();
				content();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void initializeFilterMap() {
		filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("fe_emitidas");
		filterMap.put("sii",list);
		
		Date date = new Date(2017-1900, 6, 1);
		if(administration.equals(Administration.ALAVA) || administration.equals(Administration.BIZKAIA)
				|| administration.equals(Administration.GIPUZKOA) || administration.equals(Administration.NAVARRA)) {
			date = new Date(2018-1900, 0, 1);
		}
		
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
		filterMap.put("sent_error", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("error", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("anulada", list);
	}
	
	Button sendAll;
	Button baja;
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
				ig.sendSii(getFilterMap().get("sii").get(0)); // TODO
			}
		});
		
		baja = toolbar.addButton("Anular", "aon-icon-removed");
		baja.setVisible(false);
		baja.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				
				SimpleLayoutPanel slp = p.getContent();
				InvoiceGrid ig = (InvoiceGrid) slp.getWidget();
				ig.anular(getFilterMap().get("sii").get(0)); // TODO
			}
		});
	
		setToolbar(toolbar);
	}
	
	public Button getSendAll() {
		return sendAll;
	}
	
	public Button getBaja() {
		return baja;
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		
		VerticalPanel menuPanel = new VerticalPanel();
		
		Button facturasEmitidasButton = new Button("Facturas Emitidas");
		facturasEmitidasButton.setStyleName("aon-editDataTable-button");
		facturasEmitidasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		facturasEmitidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_emitidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas");
				p.gridContent();
			}
		});
		menuPanel.add(facturasEmitidasButton);
		
		Button feGeneralButton = new Button("Generales");
		feGeneralButton.setStyleName("aon-editDataTable-button");
		feGeneralButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feGeneralButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		feGeneralButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_generales");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Generales");
				p.gridContent();
			}
		});
		menuPanel.add(feGeneralButton);
		
		Button feSimpleButton = new Button("Simplificadas");
		feSimpleButton.setStyleName("aon-editDataTable-button");
		feSimpleButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feSimpleButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		feSimpleButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_simplificadas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Simplificadas");
				p.gridContent();
			}
		});
		menuPanel.add(feSimpleButton);
		
		Button feRectButton = new Button("Rectificativas");
		feRectButton.setStyleName("aon-editDataTable-button");
		feRectButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feRectButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		feRectButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_rectificativas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Rectificativas");
				p.gridContent();
			}
		});
		menuPanel.add(feRectButton);
		
		Button feIntraButton = new Button("Intracomunitarias");
		feIntraButton.setStyleName("aon-editDataTable-button");
		feIntraButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feIntraButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		feIntraButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Intracomunitarias");
				p.gridContent();
			}
		});
		menuPanel.add(feIntraButton);
		
		Button facturasRecibidasButton = new Button("Facturas Recibidas");
		facturasRecibidasButton.setStyleName("aon-editDataTable-button");
		facturasRecibidasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		facturasRecibidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_recibidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas");
				p.gridContent();
			}
		});
		menuPanel.add(facturasRecibidasButton);
		
		Button frComprasButton = new Button("Compras");
		frComprasButton.setStyleName("aon-editDataTable-button");
		frComprasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frComprasButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		frComprasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_compras");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Compras");
				p.gridContent();
			}
		});
		menuPanel.add(frComprasButton);
		
		Button frGastosButton = new Button("Gastos");
		frGastosButton.setStyleName("aon-editDataTable-button");
		frGastosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frGastosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		frGastosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_gastos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Gastos");
				p.gridContent();
			}
		});
		menuPanel.add(frGastosButton);
		
		Button frRectButton = new Button("Rectificativas");
		frRectButton.setStyleName("aon-editDataTable-button");
		frRectButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frRectButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		frRectButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_rectificativas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Rectificativas");
				p.gridContent();
			}
		});
		menuPanel.add(frRectButton);
		
		Button frIntraButton = new Button("Intracomunitarias");
		frIntraButton.setStyleName("aon-editDataTable-button");
		frIntraButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frIntraButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		frIntraButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Intracomunitarias");
				p.gridContent();
			}
		});
		menuPanel.add(frIntraButton);

		Button bienesInversionButton = new Button("Bienes de Inversion");
		bienesInversionButton.setStyleName("aon-editDataTable-button");
		bienesInversionButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		bienesInversionButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("bienes");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Bienes de Inversion");
				p.gridContent();
			}
		});
		menuPanel.add(bienesInversionButton);
	
		Button operacionesIntracomunitariasButton = new Button("Operaciones Intracomunitarias");
		operacionesIntracomunitariasButton.setStyleName("aon-editDataTable-button");
		operacionesIntracomunitariasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		operacionesIntracomunitariasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Operaciones Intracomunitarias");
				p.gridContent();
			}
		});
		menuPanel.add(operacionesIntracomunitariasButton);
		
		Button operacionesCobrosPagosButton = new Button("Operaciones Cobros/Pagos");
		operacionesCobrosPagosButton.setStyleName("aon-editDataTable-button");
		operacionesCobrosPagosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		operacionesCobrosPagosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_cobros_pagos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Cobros/Pagos");
				p.gridContent();
			}
		});
		menuPanel.add(operacionesCobrosPagosButton);
		
		Button cobrosButton = new Button("Cobros");
		cobrosButton.setStyleName("aon-editDataTable-button");
		cobrosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		cobrosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		cobrosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_cobros");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Cobros");
				p.gridContent();
			}
		});
		menuPanel.add(cobrosButton);
		
		Button pagosButton = new Button("Pagos");
		pagosButton.setStyleName("aon-editDataTable-button");
		pagosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		pagosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		pagosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_pagos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Pagos");
				p.gridContent();
			}
		});
		menuPanel.add(pagosButton);
		
		setWestContent(menuPanel);
	}
	
	private void content() {
		setContent(new SiiPrincipal(this));
	}

}
