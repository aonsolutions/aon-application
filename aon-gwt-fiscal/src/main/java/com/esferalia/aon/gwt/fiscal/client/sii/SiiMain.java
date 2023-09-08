package com.esferalia.aon.gwt.fiscal.client.sii;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationService;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceGrid;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SiiMain extends DockLayoutPanel {
	
	private final AonMenuItem chapter1 = new AonMenuItem()
			.setTitle("Facturas emitidas")
			.addItem(new AonMenuItem().setTitle("Generales"))
			.addItem(new AonMenuItem().setTitle("Simplificadas"))
			.addItem(new AonMenuItem().setTitle("Rectificativas"))
			.addItem(new AonMenuItem().setTitle("Intracomunitarias"));
	
	private final AonMenuItem chapter2 = new AonMenuItem()
			.setTitle("Facturas recibidas")
			.addItem(new AonMenuItem().setTitle("Compras"))
			.addItem(new AonMenuItem().setTitle("Gastos"))
			.addItem(new AonMenuItem().setTitle("Rectificativas"))
			.addItem(new AonMenuItem().setTitle("Intracomunitarias"));
	
	private final AonMenuItem chapter3 = new AonMenuItem()
			.setTitle("Bienes de inversi\u00f3n");
	
	private final AonMenuItem chapter4 = new AonMenuItem()
			.setTitle("Determinadas operaciones intracomunitarias");
	
	private final AonMenuItem chapter5 = new AonMenuItem()
			.setTitle("Criterio de caja/Criterio de cobros y pagos")
			.addItem(new AonMenuItem().setTitle("Cobros"))
			.addItem(new AonMenuItem().setTitle("Pagos"));
	
	private static final InvoiceCommunicationServiceAsync SII_SERVICE;
	static {
		InvoiceCommunicationServiceAsync siiServiceRaw = GWT.create(InvoiceCommunicationService.class);
		SII_SERVICE = new InvoiceCommunicationServiceAsyncDecorator(siiServiceRaw); 
	}
	
	private API api;
	private FiscalModel model;
	private FiscalModelModuleOptions<FiscalModel> options;
	private SiiConfiguration siiConfiguration;
	InvoiceGrid invoiceGrid;

	protected SiiMain(API api, FiscalModelModuleOptions<FiscalModel> options) {
		super(Unit.PX);
		this.api = api;
		SII_SERVICE.getSiiConfiguration(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<SiiConfiguration>() {
			
			@Override
			public void onSuccess(SiiConfiguration result) {
				setSiiConfiguration(result);
				FiscalModel sii = new FiscalModel();
				sii.setAdministration(result.getAdministration());
				sii.setModel(FiscalModelType.SII);
				sii.setName(options.getConfiguration().getCompany().getName());
				sii.setDocument(options.getConfiguration().getCompany().getDocument());
				sii.setYear(AonDateUtils.getCurrentYear());
				setModel(sii);
				setOptions(options);
				addNorth(new AonFiscalModelHeader(sii), AonFiscalModelHeader.HEIGTH);
				addNorth(getToolbar(), AonToolbar.HEIGTH);
				addWest(getMenu(), 250);
				initializeFilter();
				
				
				invoiceGrid = new InvoiceGrid(options, getFilterParams()) {
					
					@Override
					public void info(Integer invoice, String reference) {
						siiInfo(invoice, reference);
					}

					@Override
					public void download(Invoice object) {
						siiDownload(object);
					}

					@Override
					public void select(LinkedList<Invoice> selFiles) {
						boolean visible = !selFiles.isEmpty();
						sendButton.setVisible(visible);
						bajaButton.setVisible(visible);
					}
				};
			
				add(invoiceGrid);
				setStyleName(AON.CSS.aonSelector());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	InvoiceParams filterParams;
	
	public InvoiceParams getFilterParams() {
		if(filterParams == null) filterParams = new InvoiceParams(); 
		return filterParams;
	}
	
	private AonMenu getMenu() {
		AonMenu aonMenu = new AonMenu();
		aonMenu.addItem(chapter1);
		aonMenu.addItem(chapter2);
		aonMenu.addItem(chapter3);
		aonMenu.addItem(chapter4);
		aonMenu.addItem(chapter5);
		return aonMenu;
	}
	
	public API getApi() {
		return api;
	}
	
	public SiiConfiguration getSiiConfiguration() {
		return siiConfiguration;
	}
	
	public void setSiiConfiguration(SiiConfiguration siiConfiguration) {
		this.siiConfiguration = siiConfiguration;
	}
	
	public FiscalModel getModel() {
		return model;
	}
	
	public void setModel(FiscalModel model) {
		this.model = model;
	}
	
	public FiscalModelModuleOptions<FiscalModel> getOptions() {
		return options;
	}
	
	public void setOptions(FiscalModelModuleOptions<FiscalModel> options) {
		this.options = options;
	}
	
	
	AonToolbarButton sendButton;
	AonToolbarButton bajaButton;
	
	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE, getModel().getFullName()));
		
		sendButton = new AonToolbarButton("Enviar", AON.CSS.aonIconSend());
		sendButton.addClickHandler(event -> Window.alert("send"));
		sendButton.setVisible(false);
		toolbarPanel.add(sendButton);
		
		bajaButton = new AonToolbarButton("Anular", AON.CSS.aonIconSendCancel());
		bajaButton.addClickHandler(event -> Window.alert("send"));
		bajaButton.setVisible(false);
		toolbarPanel.add(bajaButton);
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {
				getFilterParams().setValue(value).setPage(1).setPerPage(30);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
		toolbarPanel.showSearchPanel(searchBox);
		
		return toolbarPanel;
	}

	public void initializeFilter() {
		this.filterParams = new InvoiceParams()
			.setDomain(getOptions().getDomain())
			.setType(InvoiceType.SALES);
	}
	
	private void siiInfo(Integer invoice, String reference) {
		getApi().getSii().getSiiInvoiceHistory(invoice, new AsyncCallback<JSON<JsObject>>() {

			@Override
			public void onSuccess(JSON<JsObject> result) {

				VerticalPanel vp = new VerticalPanel();
				if(result.getData().length() > 0)
					result.getData().stream().forEach(r -> {
						vp.add(buildHistory(r));
					});
				else vp.add(new Label("No se ha realizado ning\u00fan env\u00edo a la Agencia Tributaria de la factura " + reference +" a partir del 01/07/2018."));

				AonDialog dialog = new AonDialog("Informaci\u00f3n SII", vp) {

					@Override
					protected void onCancel() {
						hide();
					}

					@Override
					protected void onAccept() {
						hide();
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getCancel().setVisible(false);
				dialog.center();
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}
	
	private void siiDownload(Invoice object) {
		HashMap<String, LinkedList<String>> map =  new HashMap<>();
		LinkedList<String> list = new LinkedList<>(); //.stream().map(s -> s.getId() + "").collect(Collectors.toCollection(LinkedList::new));
		list.add(object.getId() + "");
		map.put("id", list);
    	list = new LinkedList<>();
    	list.add("suministro");
    	map.put("action", list);
    	String sii = ""; //this.parent.getFilterMap().get("sii").get(0);
    	list = new LinkedList<>();
    	list.add(sii);
    	map.put("option", list);
    	
		getApi().getSii().downloadGenerateSiiXml(map);
	}

    public HorizontalPanel buildHistory(JsObject js){
    	HorizontalPanel hp = new HorizontalPanel();
//    	PaperItem pi = new PaperItem();
//    	IronIcon ironIcon = new IronIcon();
//    	ironIcon.setIcon("schedule");
//    	pi.add(ironIcon);
//    	String str = AonDateUtils.formatDate(AonDateUtils.parseDateTime(js.getDate())) + " - " + js.getName();
//    	Label label = new Label(str);
//    	pi.add(label);
//
//    	pi.setStyle("min-height:24px;height:24px;font-size:12px;padding:0px;");
//
//    	hp.add(pi);
//    	PaperIconButton downloadButton = new PaperIconButton();
//    	downloadButton.setIcon("file-download");
//    	downloadButton.setTitle("Descargar");
//    	downloadButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				getAPI().getSii().downloadSiiXml(js.getId());
//			}
//		});
//
//    	downloadButton.setStyle("min-height:24px;height:24px;font-size:12px;padding:0px;");
//
//    	hp.add(downloadButton);
    	return hp;
    }
}
