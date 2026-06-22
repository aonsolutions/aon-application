package com.esferalia.aon.gwt.fiscal.client.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationService;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.InvoiceCommunicationServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceGrid;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SiiMain extends DockLayoutPanel {
	
	private final AonMenuItem chapter1 = new AonMenuItem()
			.setTitle("Facturas emitidas")
			.setHandler(issuedInvoiceHandler())
//			.addItem(new AonMenuItem().setTitle("Generales"))
//			.addItem(new AonMenuItem().setTitle("Simplificadas"))
//			.addItem(new AonMenuItem().setTitle("Rectificativas"))
//			.addItem(new AonMenuItem().setTitle("Intracomunitarias"))
			;
	
	private final AonMenuItem chapter2 = new AonMenuItem()
			.setTitle("Facturas recibidas")
			.setHandler(receivedInvoiceHandler())
//			.addItem(new AonMenuItem().setTitle("Compras"))
//			.addItem(new AonMenuItem().setTitle("Gastos"))
//			.addItem(new AonMenuItem().setTitle("Rectificativas"))
//			.addItem(new AonMenuItem().setTitle("Intracomunitarias"))
			;
	
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
	private InvoiceCommunicationConfiguration configuration;
	InvoiceGrid invoiceGrid;
	
	List<Invoice> selectedInvoices;
	Sii sii;

	protected SiiMain(Sii sii, API api, FiscalModelModuleOptions<FiscalModel> options) {
		super(Unit.PX);
		this.api = api;
		this.sii = sii;
		
		SII_SERVICE.getConfiguration(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<InvoiceCommunicationConfiguration>() {
			
			@Override
			public void onSuccess(InvoiceCommunicationConfiguration result) {
				setConfiguration(result);
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
				
				invoiceGrid = new InvoiceGrid(options, getFilterParams(), result.isRegistryTaxDate()) {
					
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
						selectedInvoices = selFiles;
						
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
	
	InvoiceCommunicationParams filterParams;
	
	public InvoiceCommunicationParams getFilterParams() {
		if(filterParams == null) filterParams = new InvoiceCommunicationParams(); 
		return filterParams;
	}
	
	private ClickHandler issuedInvoiceHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFilterParams()
				.setCommunicationType(InvoiceCommunicationType.SII)
				.setType(InvoiceType.SALES);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
	}
	
	private ClickHandler receivedInvoiceHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFilterParams()
					.setCommunicationType(InvoiceCommunicationType.SII)
					.setType(InvoiceType.PURCHASE, InvoiceType.EXPENSES);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
	}
	
	private AonMenu getMenu() {
		AonMenu aonMenu = new AonMenu();
		aonMenu.addItem(chapter1);
		aonMenu.addItem(chapter2);
//		aonMenu.addItem(chapter3);
//		aonMenu.addItem(chapter4);
//		aonMenu.addItem(chapter5);
		return aonMenu;
	}
	
	public API getApi() {
		return api;
	}
	
	public InvoiceCommunicationConfiguration getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(InvoiceCommunicationConfiguration configuration) {
		this.configuration = configuration;
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
	
	public Sii getSii() {
		return sii;
	}
	
	AonToolbarButton sendButton;
	AonToolbarButton bajaButton;
//	AonToolbarButton prepareButton;
	
	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE, getModel().getFullName()));
		
		sendButton = new AonToolbarButton("Enviar", AON.CSS.aonIconSend());
		sendButton.addClickHandler(event -> send(true));
		sendButton.setVisible(false);
		toolbarPanel.add(sendButton);
		
		bajaButton = new AonToolbarButton("Anular", AON.CSS.aonIconSendCancel());
		bajaButton.addClickHandler(event -> {
			if(selectedInvoices.get(0).isSales()) {
				Window.alert("La anulaci\u00f3n de facturas emitidas hay que realizarla desde la pantalla de facturas.");
			} else send(false);
		});
		bajaButton.setVisible(false);
		toolbarPanel.add(bajaButton);
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {
				getFilterParams().setQuery(value).setPage(1).setPerPage(50);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
		searchBox.setAdvancedSearch(advancedSearchPanel());
		toolbarPanel.showSearchPanel(searchBox);
		
		return toolbarPanel;
	}
	
	private API getAPI() {
		return new API(GWT.getHostPageBaseURL(), 
				options.getConfiguration().getMd5(),
				options.getConfiguration().getDomain().getName(), 
				options.getConfiguration().getDomain().getId(),
				options.getConfiguration().getUser().getLogin());
	}
	
	private void send(boolean alta) {		
		AonCertificationPopupParams params = new AonCertificationPopupParams()
				.setShowDocument(false)
				.setShowName(false);
		AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
				
				@Override
				protected void onCancel() {

				}
				
				@Override
				protected void onAccept( AEATParams params) {
					hide();
					getSii().openFootPanelIfNeeded();

					VerticalPanel vp = new VerticalPanel();
					getSii().getBreakdownPanel().setWidget(vp);
					if(alta) {
						selectedInvoices.stream().forEach(invoice -> {
							if(invoice.isSales() && invoice.getSiiInfo().map(i -> i.isAccepted()).orElse(false)) {
								String message = "La factura " + invoice.getReferenceCode() + " ya est\u00e1 enviada.";
								vp.add(getErrorMessage(message));
							} else {
								SII_SERVICE.altaSii(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<ICResponse>() {
									
									@Override
									public void onSuccess(ICResponse result) {
										if(!result.isError()) { 	
											String message = "La factura " + invoice.getReferenceCode() + " se ha enviado correctamente.";
											vp.add(getOkMessage(message));
										} else vp.add(getErrorMessage(result.getErrorMessage()));
										
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
									
									@Override
									public void onFailure(Throwable caught) {
										vp.add(getErrorMessage(caught.getMessage()));
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
								});
							}
						});
					} else {
						selectedInvoices.stream().forEach(invoice -> {
							if(!invoice.isSales()) {
								SII_SERVICE.bajaSii(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<ICResponse>() {
									
									@Override
									public void onSuccess(ICResponse result) {
										if(!result.isError()) { 	
											String message = "La factura " + invoice.getReferenceCode() + " se ha anulado correctamente.";
											vp.add(getOkMessage(message));
										} else vp.add(getErrorMessage(result.getErrorMessage()));
										
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
									
									@Override
									public void onFailure(Throwable caught) {
										vp.add(getErrorMessage(caught.getMessage()));
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
								});
							}	
						});
					}
				}
			};
			certPopup.center();
	}
	
	private void prepareNewSii() {
		ListBox year = new ListBox();
		year.addItem("2026", "2026");
		year.addItem("2025", "2025");		
		year.addItem("2024", "2024");
		year.addItem("2023", "2023");
		year.addItem("2022", "2022");
		year.addItem("2021", "2021");
		year.addItem("2020", "2020");
		year.addItem("2019", "2019");
		year.addItem("2018", "2018");
		year.addItem("2017", "2017");
		year.addItem("2016", "2016");
		year.setSelectedIndex(0);
		
		AonDialog dialog = new AonDialog("Informaci\u00f3n SII", year) {

			@Override
			protected void onCancel() {
				hide();
			}

			@Override
			protected void onAccept() {
				SII_SERVICE.prepareNewSii(options.getDomainName(), options.getDomain(), options.getUser(), Integer.valueOf(year.getSelectedValue()), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						Window.alert("SII preparado para el a\u00f1o " + year.getSelectedValue());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error preparando el SII para el a\u00f1o " + year.getSelectedValue() + ": " + caught.getMessage());
					}
				});
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getCancel().setVisible(false);
		dialog.center();
	}

	private VerticalPanel advancedSearchPanel() {
		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp2 = new HorizontalPanel();
		Label label2 = new Label(AON.MSG.from());
		label2.setWidth("50px");
		label2.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp2.add(label2);
		DateBoxEx from = new DateBoxEx();
		from.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				getFilterParams().setFrom(from.getValue()).setPage(1).setPerPage(50);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		});
		hp2.add(from);
		vp.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		Label label3 = new Label(AON.MSG.to());
		label3.setWidth("50px");
		label3.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp3.add(label3);
		DateBoxEx to = new DateBoxEx();
		to.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				getFilterParams().setTo(to.getValue()).setPage(1).setPerPage(50);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		});
		hp3.add(to);
		vp.add(hp3);
		
		HorizontalPanel hp4 = new HorizontalPanel();
		Label label4 = new Label(AON.MSG.status());
		label4.setWidth("50px");
		label4.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp4.add(label4);

		ListBox status = new ListBox();
		status.addItem("-", "-");
		for(InvoiceCommunicationStatus st : InvoiceCommunicationStatus.values()) {
			status.addItem(getStatusName(st), st.name());
		}

		status.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				InvoiceCommunicationStatus st = InvoiceCommunicationStatus.safeValueOf(status.getSelectedValue());
				getFilterParams().setCommunicationStatus(st).setPage(1).setPerPage(50);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		});
		hp4.add(status);
		vp.add(hp4);
		return vp;
	}
	
	private String getStatusName(InvoiceCommunicationStatus st) {
		if (st == null) return InvoiceCommunicationStatus.PENDING.getDescription();
		return st.getDescription();
	}
	
	public void initializeFilter() {
		this.filterParams = new InvoiceCommunicationParams()
			.setDomain(getOptions().getDomain())
			.setCommunicationType(InvoiceCommunicationType.SII)
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
    
	public Label getMessage(String message, String color){
		Label label = new Label(message);
		label.getElement().getStyle().setColor(color);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		return label;
	}
	 
	public Label getOkMessage(String message ){
		return getMessage(message, "green");
	}
	
	public Label getErrorMessage(String message ){
		return getMessage(message, "red");
	}
}
