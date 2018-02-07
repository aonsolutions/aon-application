package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
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
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
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
	Button send;
	Button baja;
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Suministro Inmediato de Informacion") {};
		sendAll = toolbar.addButton("Enviar Todo", AON.AON_CSS.aonIconSave());
		sendAll.setVisible(true);
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendAllSii(getFilterMap().get("sii").get(0));
			}
		});
		
		send = toolbar.addButton("Enviar", AON.AON_CSS.aonIconSave());
		send.setVisible(false);
		send.addClickHandler(new ClickHandler() {
			
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
	
	public Button getSend() {
		return send;
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
		
		Button feGeneralButton = new Button("Generales");
		feGeneralButton.setStyleName("aon-editDataTable-button");
		feGeneralButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feGeneralButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);

		Button feSimpleButton = new Button("Simplificadas");
		feSimpleButton.setStyleName("aon-editDataTable-button");
		feSimpleButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feSimpleButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);

		Button feRectButton = new Button("Rectificativas");
		feRectButton.setStyleName("aon-editDataTable-button");
		feRectButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feRectButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		
		Button feIntraButton = new Button("Intracomunitarias");
		feIntraButton.setStyleName("aon-editDataTable-button");
		feIntraButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		feIntraButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);

		Button facturasRecibidasButton = new Button("Facturas Recibidas");
		facturasRecibidasButton.setStyleName("aon-editDataTable-button");
		facturasRecibidasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		Button frComprasButton = new Button("Compras");
		frComprasButton.setStyleName("aon-editDataTable-button");
		frComprasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frComprasButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		
		Button frGastosButton = new Button("Gastos");
		frGastosButton.setStyleName("aon-editDataTable-button");
		frGastosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frGastosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);

		Button frRectButton = new Button("Rectificativas");
		frRectButton.setStyleName("aon-editDataTable-button");
		frRectButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frRectButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		
		Button frIntraButton = new Button("Intracomunitarias");
		frIntraButton.setStyleName("aon-editDataTable-button");
		frIntraButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		frIntraButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);

		Button bienesInversionButton = new Button("Bienes de Inversion");
		bienesInversionButton.setStyleName("aon-editDataTable-button");
		bienesInversionButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
	
		Button operacionesIntracomunitariasButton = new Button("Operaciones Intracomunitarias");
		operacionesIntracomunitariasButton.setStyleName("aon-editDataTable-button");
		operacionesIntracomunitariasButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
			
		Button operacionesCobrosPagosButton = new Button("Operaciones Cobros/Pagos");
		operacionesCobrosPagosButton.setStyleName("aon-editDataTable-button");
		operacionesCobrosPagosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		
		Button cobrosButton = new Button("Cobros");
		cobrosButton.setStyleName("aon-editDataTable-button");
		cobrosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		cobrosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		
		Button pagosButton = new Button("Pagos");
		pagosButton.setStyleName("aon-editDataTable-button");
		pagosButton.addStyleName(AON.AON_CSS.aonDocumentalTitle());
		pagosButton.getElement().getStyle().setPaddingLeft(50, Unit.PX);
		
		facturasEmitidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_emitidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		feGeneralButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_generales");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Generales");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		feSimpleButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_simplificadas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Simplificadas");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		feRectButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_rectificativas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Rectificativas");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		feIntraButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fe_intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Emitidas - Intracomunitarias");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		facturasRecibidasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_recibidas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		frComprasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_compras");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Compras");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		frGastosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_gastos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Gastos");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		frRectButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_rectificativas");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Rectificativas");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		frIntraButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("fr_intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Facturas Recibidas - Intracomunitarias");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		bienesInversionButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("bienes");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Bienes de Inversion");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		operacionesIntracomunitariasButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("intracomunitarias");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(true);
				fp.setTitle("Operaciones Intracomunitarias");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		operacionesCobrosPagosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_cobros_pagos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Cobros/Pagos");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		cobrosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_cobros");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Cobros");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
		
		pagosButton.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				sendAll.setVisible(true);
				send.setVisible(false);
				baja.setVisible(false);
				LinkedList<String> list = new LinkedList<>();
				list.add("cp_pagos");
				getFilterMap().put("sii",list);
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
				fp.setCheckVisible(false);
				fp.setTitle("Operaciones Pagos");
				p.gridContent();
				
				facturasEmitidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feGeneralButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feSimpleButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				feIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				facturasRecibidasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frComprasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frGastosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frRectButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				frIntraButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				bienesInversionButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesIntracomunitariasButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				operacionesCobrosPagosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				cobrosButton.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pagosButton.getElement().getStyle().setFontWeight(FontWeight.BOLD);

				
			}
		});
		
		menuPanel.add(facturasEmitidasButton);
		menuPanel.add(feGeneralButton);
		menuPanel.add(feSimpleButton);
		menuPanel.add(feRectButton);
		menuPanel.add(feIntraButton);
		menuPanel.add(facturasRecibidasButton);
		menuPanel.add(frComprasButton);
		menuPanel.add(frGastosButton);
		menuPanel.add(frRectButton);
		menuPanel.add(frIntraButton);
		menuPanel.add(bienesInversionButton);
		menuPanel.add(operacionesIntracomunitariasButton);
		menuPanel.add(operacionesCobrosPagosButton);
		menuPanel.add(cobrosButton);
		menuPanel.add(pagosButton);
		
		setWestContent(menuPanel);
	}
	

	private void content() {
		setContent(new SiiPrincipal(this));
	}

	
	public void sendAllSii(String sii){
		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.add(new Label("Tipo de Operacion"));
		ListBox lb0 = new ListBox();
		lb0.addItem("Articulo 70, apartado uno, n\u00famero 7\u00BA, Ley del Impuesto(Ley 37/1992)", "A");
		lb0.addItem("Articulo 16, apartado 2\u00BA, Ley del Impuesto(Ley 37/1992)", "B");
		hp0.add(lb0);
		if("intracomunitarias".equalsIgnoreCase(sii)){
			vp.add(hp0);
		}
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l = new Label("NIF");
		l.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l.setVisible(false);
		TextBox t = new TextBox();t.setStyleName(AON.AON_CSS.aonInputText());
		t.setVisible(false);
		hp3.add(l);
		hp3.add(t);
		
		HorizontalPanel hp4 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l4 = new Label("Autorizaci\u00f3n");
		l4.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l4.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l4.setVisible(false);
		TextBox t4 = new TextBox();t4.setStyleName(AON.AON_CSS.aonInputText());
		t4.setVisible(false);
		hp4.add(l4);
		hp4.add(t4);
		
		CheckBox cb = new CheckBox("Por terceros");
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				l.setVisible(cb.getValue());
				t.setVisible(cb.getValue());	
				l4.setVisible(cb.getValue());
				t4.setVisible(cb.getValue());	
			}
		});
		vp.add(cb);
		vp.add(hp3);
	//	vp.add(hp4);
		
		AonDialog dialog = new AonDialog("Enviar Facturas", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> sendMap =  new HashMap<>();
				LinkedList<String> list = new LinkedList<>();
				
				sendMap.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("suministro");
		    	sendMap.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	sendMap.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	sendMap.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add(sii);
		    	sendMap.put("option", list);
		    	hide();
		    	
		    	list = new LinkedList<>();
		    	list.add(lb0.getSelectedValue());
		    	sendMap.put("tipo_operacion", list);
	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t.getValue() : "false");
		    	sendMap.put("terceros", list);
		  /*  	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t4.getValue() : "");
		    	map.put("auth", list);
		    */
		    	
		    	
		    	resultPanel(sendMap);
/*		    	
				*/
			}
		};
		dialog.center();
	}
	
	private Boolean isSendAllCancel = false;
	private void resultPanel(HashMap<String, LinkedList<String>> sendMap) {
		ScrollPanel sp = new ScrollPanel();
		sp.setHeight("400px");
		sp.setWidth("350px");
		VerticalPanel vp = new VerticalPanel();
		sp.add(vp);
		AonDialog dialog = new AonDialog("Resultado", sp) {
			
			@Override
			protected void onCancel() {
				isSendAllCancel = true;
				hide();
				content();
			}
			
			@Override
			protected void onAccept() {
				hide();
				content();
			}
		};
		dialog.setAutoHideEnabled(false);
		dialog.getAccept().setVisible(false);
		dialog.center();
		HashMap<String , LinkedList<String>> map = getFilterMap();
		resultPanel(map, 1, vp, sendMap, dialog);
	}
	
	private void resultPanel(HashMap<String, LinkedList<String>> map, Integer page, VerticalPanel vp, HashMap<String, LinkedList<String>> sendMap, AonDialog d) {
		LinkedList<String> list = new LinkedList<>();
		list.add(page +"");
		map.put("page", list);
		
		list = new LinkedList<>();
		list.add("1");
		map.put("per_page", list);
		getAPI().getFinance().getInvoices(map, new AsyncCallback<JSON<JsInvoice>>() {
			
			@Override
			public void onSuccess(JSON<JsInvoice> result) {
				LinkedList<String> list = new LinkedList<>();
				list.add(result.getData().get(0).getId() + "");
				sendMap.put("id", list);
	
				getAPI().getFinance().sendSii(sendMap, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result2) {	
						result2.getData().stream().forEach(r -> {
							Label label = new Label(r.getName());
							String str = r.getId() + "";
							String color = "red";
							if(str.equals("200")) color = "green";
							else if(str.substring(0, 1).equals("2")) color = "orange";
							label.getElement().getStyle().setColor(color);
							vp.add(label);
						});
						if(isSendAllCancel) {
							isSendAllCancel = false;
						} else if(result.getData().length() < 1) {
							d.getAccept().setVisible(true);
						} else resultPanel(map, page, vp, sendMap, d);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
		
	}
}
