package com.esferalia.aon.gwt.fiscal.client.mod140;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIcon;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
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
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceTracking;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LroeModel140 extends DockLayoutPanel {
	
	private static final InvoiceCommunicationServiceAsync SII_SERVICE;
	static {
		InvoiceCommunicationServiceAsync siiServiceRaw = GWT.create(InvoiceCommunicationService.class);
		SII_SERVICE = new InvoiceCommunicationServiceAsyncDecorator(siiServiceRaw); 
	}
	
	private final AonMenuItem chapter1 = new AonMenuItem()
			.setTitle("1. Ingresos y facturas emitidas")
			.addItem(new AonMenuItem().setTitle("1.1 Con software garante")
					.setHandler(chapter1_1Handler()))
			.addItem(new AonMenuItem().setTitle("1.2 Sin software garante")
					.setHandler(emptyHandler())
					.setDisabled(true))
			.addItem(new AonMenuItem().setTitle("1.3 Ingresos sin factura")
					.setDisabled(true)
					.setHandler(emptyHandler()));
	
	private final AonMenuItem chapter2 = new AonMenuItem()
			.setTitle("2. Gastos y facturas recibidas")
			.addItem(new AonMenuItem().setTitle("2.1 Gastos con factura")
					.setHandler(chapter2Handler()))
			.addItem(new AonMenuItem().setTitle("2.2 Gastos sin factura")
					.setDisabled(true)
					.setHandler(emptyHandler()));
	
	private final AonMenuItem chapter3 = new AonMenuItem()
			.setTitle("3. Bienes afectos o de inversi\u00f3n")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("3.1 Alta"))
			.addItem(new AonMenuItem().setTitle("3.2 Mejora"))
			.addItem(new AonMenuItem().setTitle("3.3 Baja"))
			.addItem(new AonMenuItem().setTitle("3.4 Regularizaci\u00f3n Anual"));
	
	private final AonMenuItem chapter4 = new AonMenuItem()
			.setTitle("4. Determinadas operaciones intracomunitarias")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("4.1 Transferencias, informes y otros"))
			.addItem(new AonMenuItem().setTitle("4.2 Venta de bienes"));

	private final AonMenuItem chapter5 = new AonMenuItem()
			.setTitle("5. Criterio de caja/Criterio de cobros y pagos")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("5.1 Cobros"))
			.addItem(new AonMenuItem().setTitle("5.2 Pagos"));
	
	private final AonMenuItem chapter6 = new AonMenuItem()
			.setDisabled(true)
			.setTitle("6. Provisiones y suplidos");
	
	private final AonMenuItem chapter7 = new AonMenuItem()
			.setTitle("7. Otra informaci\u00f3n con trascendencia tributaria")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("7.1 Variaci\u00f3n de existencias"))
			.addItem(new AonMenuItem().setTitle("7.2 Arrendamientos de locales de negocios"))
			.addItem(new AonMenuItem().setTitle("7.3 Transmisiones de inmuebles sujetas a IVA"))
			.addItem(new AonMenuItem().setTitle("7.4 Importes superiores a 6000 euros en met\u00e1lico"));

	private final AonMenuItem chapter8 = new AonMenuItem()
			.setTitle("8. Agrupaciones de bienes")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("8.1 Alta"))
			.addItem(new AonMenuItem().setTitle("8.2 Baja"));
	
	private final AonMenuItem utilities = new AonMenuItem()
			.setTitle("Utilidades")
			.addItem(new AonMenuItem().setTitle("Anular Factura Emitida"));
	
	protected FormPanel diskForm = new FormPanel("_blank");
	private FiscalModel model;
	private FiscalModelModuleOptions<FiscalModel> options;
	InvoiceGrid invoiceGrid;
	InvoiceParams filterParams;
	
	List<Invoice> selectedInvoices;
	Model140 model140;


	protected LroeModel140(Model140 parent, FiscalModelModuleOptions<FiscalModel> options) {
		super(Unit.PX);
		this.model140 = parent;
		FiscalModel mod140 = new FiscalModel();
		mod140.setAdministration(Administration.BIZKAIA);
		mod140.setModel(FiscalModelType.M140);
		mod140.setName(options.getConfiguration().getCompany().getName());
		mod140.setDocument(options.getConfiguration().getCompany().getDocument());
		mod140.setYear(AonDateUtils.getCurrentYear());
		setModel(mod140);
		setOptions(options);
		addNorth(new AonFiscalModelHeader(mod140), AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbar(), AonToolbar.HEIGTH);
		addWest(getMenu(), 250);
		initializeFilter();

		invoiceGrid = new InvoiceGrid(options, getFilterParams()) {
			
			@Override
			public void info(Integer invoice, String reference) {
				SII_SERVICE.getInvoiceTrackingList(options.getDomainName(), options.getDomain(), options.getUser(), invoice, new AsyncCallback<List<InvoiceTracking>>() {
					
					@Override
					public void onSuccess(List<InvoiceTracking> result) {
						FlexTable table = new FlexTable();
						table.setWidth("100%");
						if(result.isEmpty()) {
							table.setWidget(0, 0, new Label("No se ha realizado ning\u00fan env\u00edo."));
						} else {
							result.stream().forEach(r -> {
								Integer row = table.getRowCount();
								table.setWidget(row, 0, new Label(AonDateUtils.formatDate(r.getInvoiceBatch().getDate())));
								table.setWidget(row, 1, new Label(r.getInvoiceBatch().getOperation().getDescription()));
								table.setWidget(row, 2, new Label(r.getInvoiceBatchDetail().getStatus().getDescription()));
								
								HorizontalPanel hp = new HorizontalPanel();
								AonIcon downloadRequest = new AonIcon("download");
								downloadRequest.setTitle("Descargar Petici\u00f3n");
								downloadRequest.getElement().getStyle().setCursor(Cursor.POINTER);
								downloadRequest.onClick(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										SII_SERVICE.getRequestUrl(options.getDomainName(), options.getDomain(), options.getUser(), r.getInvoiceBatch().getDataResponse(), new AsyncCallback<String>() {
											
											@Override
											public void onSuccess(String url) {
												Window.open(GWT.getModuleBaseURL() + url, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");	
											}
											
											@Override
											public void onFailure(Throwable arg0) {}
										});
									}
								});
								
								hp.add(downloadRequest);
								
								AonIcon downloadResponse = new AonIcon("download");
								downloadResponse.setTitle("Descargar Respuesta");
								downloadResponse.getElement().getStyle().setCursor(Cursor.POINTER);
								downloadResponse.onClick(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										SII_SERVICE.getResponseUrl(options.getDomainName(), options.getDomain(), options.getUser(), r.getInvoiceBatch().getDataResponse(), new AsyncCallback<String>() {
											
											@Override
											public void onSuccess(String url) {
												Window.open(GWT.getModuleBaseURL() + url, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");	
											}
											
											@Override
											public void onFailure(Throwable arg0) {}
										});
									}
								});
								
								hp.add(downloadResponse);
								table.setWidget(row, 3, hp);
							});
						}
						AonDialog dialog = new AonDialog("Informaci\u00f3n", table);
						dialog.info();
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						AonDialog dialog = new AonDialog("Informaci\u00f3n", new Label("No se ha realizado ning\u00fan env\u00edo."));
						dialog.info();
					}
				});
			}

			@Override
			public void download(Invoice object) {
				Window.alert("En desarrollo...");
			}

			@Override
			public void select(LinkedList<Invoice> selFiles) {
				selectedInvoices = selFiles;
				boolean visible = !selFiles.isEmpty();
				sendButton.setVisible(visible);
				bajaButton.setVisible(visible);
				
				refreshButton.setVisible(selFiles.size() == 1
					&& !selFiles.getFirst().getInvoiceInfo().isAccepted()
					&& !selFiles.getFirst().getInvoiceInfo().isAcceptedWithErrors());
			}
		};
	
		add(invoiceGrid);
		setStyleName(AON.CSS.aonSelector());
	}
	
	public void initializeFilter() {
		this.filterParams = new InvoiceParams()
			.setDomain(getOptions().getDomain())
			.setType(InvoiceType.SALES)
			.setCommunicationType(InvoiceCommunicationType.LROE);
	}
	
	private AonMenu getMenu() {
		AonMenu aonMenu = new AonMenu();
		aonMenu.addItem(chapter1);
		aonMenu.addItem(chapter2);
		aonMenu.addItem(chapter3);
		aonMenu.addItem(chapter4);
		aonMenu.addItem(chapter5);
		aonMenu.addItem(chapter6);
		aonMenu.addItem(chapter7);
		aonMenu.addItem(chapter8);
//		aonMenu.addItem(utilities);
		return aonMenu;
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
	AonToolbarButton refreshButton;
	
	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE, getModel().getFullName()));
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {
				getFilterParams().setValue(value).setPage(1).setPerPage(30);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
		searchBox.setAdvancedSearch(advancedSearchPanel());
		toolbarPanel.showSearchPanel(searchBox);

		sendButton = new AonToolbarButton("Enviar", AON.CSS.aonIconSend());
		sendButton.addClickHandler(event -> send(true));
		sendButton.setVisible(false);
		toolbarPanel.add(sendButton);

		bajaButton = new AonToolbarButton("Anular", AON.CSS.aonIconSendCancel());
		bajaButton.addClickHandler(event -> send(false));
		bajaButton.setVisible(false);
		bajaButton.setEnabled(false);
		toolbarPanel.add(bajaButton);
		
		refreshButton = new AonToolbarButton("Actualizar", AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(event -> refresh());
		refreshButton.setVisible(false);
		toolbarPanel.add(refreshButton);
		
		AonToolbarButton draftButton = new AonToolbarButton(AON.MSG.generateFile(), AON.CSS.aonIconDownload());
		draftButton.addClickHandler(event -> draft());
		toolbarPanel.add(draftButton);
		
		toolbarPanel.add(diskForm);
		
		return toolbarPanel;
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
				getFilterParams().setFrom(from.getValue()).setPage(1).setPerPage(30);
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
				getFilterParams().setTo(to.getValue()).setPage(1).setPerPage(30);
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
				getFilterParams().setCommunicationStatus(st).setPage(1).setPerPage(30);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		});
		hp4.add(status);
		vp.add(hp4);
		return vp;
	}
	
	private String getStatusName(InvoiceCommunicationStatus st) {
		if(InvoiceCommunicationStatus.ACCEPTED.equals(st)) return "Aceptada";
		else if(InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS.equals(st)) return "Aceptada con Errores";
		else if(InvoiceCommunicationStatus.CANCELLED.equals(st)) return "Anulada";
		else if(InvoiceCommunicationStatus.WRONG.equals(st)) return "Incorrecta";
		else return "Pendiente";
	}
	
	private void draft() {
		VerticalPanel vp = new VerticalPanel();

		HorizontalPanel hp1 = new HorizontalPanel();
		Label label1 = new Label(AON.MSG.epigraph());
		label1.setWidth("50px");
		label1.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp1.add(label1);
		TextBox epigraph = new TextBox();
		epigraph.setStyleName("aon-inputText");
		hp1.add(epigraph);
		vp.add(hp1);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		Label label2 = new Label(AON.MSG.from());
		label2.setWidth("50px");
		label2.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp2.add(label2);
		DateBoxEx from = new DateBoxEx();
		hp2.add(from);
		vp.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		Label label3 = new Label(AON.MSG.to());
		label3.setWidth("50px");
		label3.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp3.add(label3);
		DateBoxEx to = new DateBoxEx();
		hp3.add(to);
		vp.add(hp3);
		
		HorizontalPanel hp4 = new HorizontalPanel();
		CheckBox cb1 = new CheckBox();
		cb1.setText("S\u00f3lo Emitidas");
		hp4.add(cb1);
		vp.add(hp4);
		
		AonDialog draftDialog = new AonDialog("Descargar fichero", vp);

		draftDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				draftDialog.hide();
			}
			
			@Override
			public void onAccept() {
				submitForm(epigraph.getValue(), from.format(), to.format(), cb1.getValue());
			}
		});
	}
	
	
	private void submitForm(String epigraph, String from, String to, boolean onlyEmitidas) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Model140File");
		diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
		
		HTMLPanel html = new HTMLPanel("");
		
		Hidden domainId = new Hidden();
		domainId.setName("domainId");
		domainId.setValue(String.valueOf(getOptions().getDomain()));
		html.add(domainId);
		
		Hidden domainName = new Hidden();
		domainName.setName("domainName");
		domainName.setValue(String.valueOf(getOptions().getDomainName()));
		html.add(domainName);
		
		Hidden user = new Hidden();
		user.setName("user");
		user.setValue(String.valueOf(getOptions().getUser()));
		html.add(user);
		
		Hidden epigraphHidden = new Hidden();
		epigraphHidden.setName("epigraph");
		epigraphHidden.setValue(epigraph);
		html.add(epigraphHidden);
		
		Hidden fromHidden = new Hidden();
		fromHidden.setName("fromDate");
		fromHidden.setValue(from);
		html.add(fromHidden);
		
		Hidden toHidden = new Hidden();
		toHidden.setName("toDate");
		toHidden.setValue(to);
		html.add(toHidden);
		
		Hidden onlyEmitidasHidden = new Hidden();
		onlyEmitidasHidden.setName("onlyEmitidas");
		onlyEmitidasHidden.setValue(Boolean.toString(onlyEmitidas));
		html.add(onlyEmitidasHidden);

		diskForm.setWidget(html);
		diskForm.submit();
	}
	
	public InvoiceParams getFilterParams() {
		if(filterParams == null) filterParams = new InvoiceParams(); 
		return filterParams;
	}

	
	
	private ClickHandler chapter1_1Handler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFilterParams()
				.setCommunicationType(InvoiceCommunicationType.LROE)
				.setType(InvoiceType.SALES);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
	}
	
	private ClickHandler chapter2Handler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFilterParams()
				.setCommunicationType(InvoiceCommunicationType.LROE)
				.setType(InvoiceType.PURCHASE)
				.addType(InvoiceType.EXPENSES);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
	}
	
	private ClickHandler emptyHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				invoiceGrid.setList(new LinkedList<>());
			}
		};
	}
	
	private API getAPI() {
		return new API(GWT.getHostPageBaseURL(), 
				options.getConfiguration().getMd5(),
				options.getConfiguration().getDomain().getName(), 
				options.getConfiguration().getDomain().getId(),
				options.getConfiguration().getUser().getLogin());
	}
	
	private void refresh() {
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
					getModel140().openFootPanelIfNeeded();
					VerticalPanel vp = new VerticalPanel();
					getModel140().getBreakdownPanel().setWidget(vp);
					selectedInvoices.stream().forEach(invoice -> {
						SII_SERVICE.refresh140(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								
							}

							@Override
							public void onSuccess(Boolean result) {
								if(result) { 	
									String message = "La factura " + invoice.getReferenceCode() + " est\u00e1 en el registro y se ha actualizado correctamente.";
									vp.add(getOkMessage(message));
								} else {
									String message = "La factura " + invoice.getReferenceCode() + " no est\u00e1 en el registro.";
									vp.add(getErrorMessage(message));
								}
								
								if(selectedInvoices.size() >= vp.getWidgetCount()) {
									invoiceGrid.setFilterParams(getFilterParams());
								}
							}
						});
					});
					
				}
		};
		certPopup.center();
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
					getModel140().openFootPanelIfNeeded();
					VerticalPanel vp = new VerticalPanel();
					getModel140().getBreakdownPanel().setWidget(vp);
					if(alta) {
						selectedInvoices.stream().forEach(invoice -> {
							if(invoice.getInvoiceInfo().getStatus().isAccepted() && invoice.isSales()) {
								String message = "La factura " + invoice.getReferenceCode() + " ya est\u00e1 enviada.";
								vp.add(getErrorMessage(message));
							} else {
								SII_SERVICE.altaLroe140(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<ICResponse>() {
									
									@Override
									public void onSuccess(ICResponse result) {
										if(!result.isError()) { 	
											String message = "La factura " + invoice.getReferenceCode() + " se ha enviado correctamente.";
											vp.add(getOkMessage(message));
										} else {
											if(result.getErrorMessage().contains("B4_2000116")) {
												vp.add(getActionErrorMessage(result.getErrorMessage(), invoice));
											} else vp.add(getErrorMessage(result.getErrorMessage()));
										}
										
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
						Window.alert("BAJA");						
					}
				}
			};
			certPopup.center();
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
	
	public HorizontalPanel getActionErrorMessage(String message, Invoice invoice){
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(getMessage(message, "red"));
		Label l = new Label("Pulse aqui para a\u00f1adir Bien");
		l.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		l.getElement().getStyle().setColor("#0069c2");
		l.getElement().getStyle().setCursor(Cursor.POINTER);
		l.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				SII_SERVICE.getInvestAssets(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<InvestAsset>>() {
					
					@Override
					public void onSuccess(List<InvestAsset> result) {
						ListBox lb = new ListBox();
						for (InvestAsset ia : result) {
							lb.addItem(ia.getDescription(), ia.getId().toString());
						}

						AonDialog dialog = new AonDialog("A\u00f1adir Bien", lb);

						dialog.confirm(new AonAcceptDialogCallback() {
							
							@Override
							public void onCancel() {
								dialog.hide();
							}
							
							@Override
							public void onAccept() {
								dialog.hide();
								SII_SERVICE.assignInvestAsset2Invoice(options.getDomainName(), options.getDomain(), options.getUser(), 
										lb.getValue(lb.getSelectedIndex()), invoice, new AsyncCallback<Void>() {
											
											@Override
											public void onSuccess(Void result) {
												
											}
											
											@Override
											public void onFailure(Throwable caught) {
												
											}
										});

							}
						});	
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});

			}
		});
		hp.add(l);
		return hp;
	}
	
	public Label getWarningMessage(String message ){
		return getMessage(message, "orange");
	}
	
	public Model140 getModel140() {
		return model140;
	}
}
