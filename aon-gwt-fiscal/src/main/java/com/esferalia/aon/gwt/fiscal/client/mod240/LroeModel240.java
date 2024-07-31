package com.esferalia.aon.gwt.fiscal.client.mod240;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIcon;
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
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LroeModel240 extends DockLayoutPanel {
	
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
					.setDisabled(true));
	
	private final AonMenuItem chapter2 = new AonMenuItem()
			.setTitle("2. Gastos y facturas recibidas")
			.setHandler(chapter2Handler());
	
	private final AonMenuItem chapter3 = new AonMenuItem()
			.setDisabled(true)
			.setTitle("3. Bienes de inversi\u00f3n");
	
	private final AonMenuItem chapter4 = new AonMenuItem()
			.setTitle("4. Determinadas operaciones intracomunitarias")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("4.1 Transferencias, informes y otros"))
			.addItem(new AonMenuItem().setTitle("4.2 Venta de bienes en consigna"));
	
	private final AonMenuItem chapter5 = new AonMenuItem()
			.setTitle("5. Criterio de caja/Criterio de cobros y pagos")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("5.1 Cobros"))
			.addItem(new AonMenuItem().setTitle("5.2 Pagos"));
	
	private final AonMenuItem chapter6 = new AonMenuItem()
			.setTitle("6. Otra informaci\u00f3n con trascendencia tributaria")
			.setDisabled(true)
			.addItem(new AonMenuItem().setTitle("6.1 Importes superiores a 6000 euros en met\u00e1lico"))
			.addItem(new AonMenuItem().setTitle("6.2 Operaciones de seguros"))
			.addItem(new AonMenuItem().setTitle("6.3 Agencias de viajes"));
	
	private final AonMenuItem utilities = new AonMenuItem()
			.setTitle("Utilidades")
			.addItem(new AonMenuItem().setTitle("Anular Factura Emitida")
					.setHandler(cancelInvoiceUtilityHandler()));
	
	private FiscalModel model;
	private FiscalModelModuleOptions<FiscalModel> options;
	InvoiceGrid invoiceGrid;
	InvoiceParams filterParams;
	
	List<Invoice> selectedInvoices;
	Model240 model240;
	
	protected LroeModel240(Model240 parent, FiscalModelModuleOptions<FiscalModel> options) {
		super(Unit.PX);
		this.model240 = parent;
		FiscalModel mod240 = new FiscalModel();
		mod240.setAdministration(Administration.BIZKAIA);
		mod240.setModel(FiscalModelType.M240);
		mod240.setName(options.getConfiguration().getCompany().getName());
		mod240.setDocument(options.getConfiguration().getCompany().getDocument());
		mod240.setYear(AonDateUtils.getCurrentYear());
		setModel(mod240);
		setOptions(options);
		addNorth(new AonFiscalModelHeader(mod240), AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbar(), AonToolbar.HEIGTH);
		addWest(getMenu(), 250);
		initializeFilter();
		invoiceGrid = new InvoiceGrid(options, getFilterParams()) {
			
			@Override
			public void info(Integer invoice, String reference) {
				SII_SERVICE.getInvoiceCommunicationTrackingList(options.getDomainName(), options.getDomain(), options.getUser(), invoice, new AsyncCallback<List<InvoiceCommunicationTracking>>() {
					
					@Override
					public void onSuccess(List<InvoiceCommunicationTracking> result) {
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
				bajaButton.setEnabled(false);
				
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
//			.setCommunicationStatus(InvoiceCommunicationStatus.PENDING);
	}
	
	private AonMenu getMenu() {
		AonMenu aonMenu = new AonMenu();
		aonMenu.addItem(chapter1);
		aonMenu.addItem(chapter2);
		aonMenu.addItem(chapter3);
		aonMenu.addItem(chapter4);
		aonMenu.addItem(chapter5);
		aonMenu.addItem(chapter6);
//		aonMenu.addItem(utilities);
		return aonMenu;
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
	
	private ClickHandler cancelInvoiceUtilityHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				VerticalPanel vp = new VerticalPanel();
				
				HorizontalPanel hp1 = new HorizontalPanel();
				Label label1 = new Label(AON.MSG.series());
				label1.setWidth("125px");
				label1.getElement().getStyle().setPaddingBottom(10, Unit.PX);
				hp1.add(label1);
				TextBox series = new TextBox();
				series.setStyleName("aon-inputText");
				series.setWidth("80px");
				hp1.add(series);
				vp.add(hp1);
				
				HorizontalPanel hp2 = new HorizontalPanel();
				Label label2 = new Label(AON.MSG.number());
				label2.setWidth("125px");
				label2.getElement().getStyle().setPaddingBottom(10, Unit.PX);
				hp2.add(label2);
				TextBox number = new TextBox();
				number.setStyleName("aon-inputText");
				number.setWidth("80px");
				hp2.add(number);
				vp.add(hp2);
				
				HorizontalPanel hp3 = new HorizontalPanel();
				Label label3 = new Label(AON.MSG.expeditionDate());
				label3.setWidth("125px");
				label3.getElement().getStyle().setPaddingBottom(10, Unit.PX);
				hp3.add(label3);
				DateBoxEx expeditionDate = new DateBoxEx();
				hp3.add(expeditionDate);
				vp.add(hp3);
				
				AonDialog cancelInvoicedialog = new AonDialog("Anular Factura Emitida (No existente en AON)", vp);

				cancelInvoicedialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						cancelInvoicedialog.hide();
					}
					
					@Override
					public void onAccept() {
						
						cancelInvoicedialog.hide();
						AonCertificationPopupParams params = new AonCertificationPopupParams()
								.setShowDocument(false)
								.setShowName(false);

						AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
								
							@Override
							protected void onCancel() {
								hide();
							}
								
							@Override
							protected void onAccept( AEATParams params) {
								hide();
									
							}
						};
						certPopup.center();
					}
				});
			}
		};
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
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {
				getFilterParams().setValue(value).setPage(1).setPerPage(30);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
		searchBox.setAdvancedSearch(advancedSearchPanel());
		toolbarPanel.showSearchPanel(searchBox);
		
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
	
	public InvoiceParams getFilterParams() {
		if(filterParams == null) filterParams = new InvoiceParams(); 
		return filterParams;
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
					getModel240().openFootPanelIfNeeded();
					VerticalPanel vp = new VerticalPanel();
					getModel240().getBreakdownPanel().setWidget(vp);
					selectedInvoices.stream().forEach(invoice -> {
						SII_SERVICE.refresh240(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<Boolean>() {

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
					getModel240().openFootPanelIfNeeded();
					VerticalPanel vp = new VerticalPanel();
					getModel240().getBreakdownPanel().setWidget(vp);
					if(alta) {
						selectedInvoices.stream().forEach(invoice -> {
							if(invoice.getInvoiceInfo().getStatus().isAccepted() && invoice.isSales()) {
								String message = "La factura " + invoice.getReferenceCode() + " ya est\u00e1 enviada.";
								vp.add(getErrorMessage(message));
							} else {
								SII_SERVICE.altaLroe240(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<ICResponse>() {
									
									@Override
									public void onSuccess(ICResponse result) {
										String reference = AonStringUtils.isBlank(invoice.getSeries()) ? Integer.toString(invoice.getNumber()) : invoice.getSeries() + "/" + invoice.getNumber();
										reference = AonStringUtils.isBlank(invoice.getReferenceCode()) || "null".equalsIgnoreCase(invoice.getReferenceCode())
											? reference : invoice.getReferenceCode();
										if(!result.isError()) { 	
											String message = "La factura " + reference + " se ha enviado correctamente.";
											vp.add(getOkMessage(message));
										} else {
											if(result.getErrorCode().equals("AON_001")
												|| result.getErrorCode().equals("B4_2000013")) {
												vp.add(getActionErrorMessage(result, invoice));
											} else vp.add(getErrorMessage("Factura " + reference + ": " + result.getErrorMessage()));
										} 
										
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
									
									@Override
									public void onFailure(Throwable caught) {
										String reference = AonStringUtils.isBlank(invoice.getSeries()) ? Integer.toString(invoice.getNumber()) : invoice.getSeries() + "/" + invoice.getNumber();
										reference = AonStringUtils.isBlank(invoice.getReferenceCode()) || "null".equalsIgnoreCase(invoice.getReferenceCode())
											? reference : invoice.getReferenceCode();
										vp.add(getErrorMessage("Factura " + reference + ": " + caught.getMessage()));
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
								});
							}
						});
					} else {
						selectedInvoices.stream().forEach(invoice -> {
							if(!invoice.getInvoiceInfo().getStatus().isAccepted()) {
								String message = "La factura " + invoice.getReferenceCode() + " no est\u00e1 enviada. No se puede anular.";
								vp.add(getErrorMessage(message));
							} else if(invoice.getInvoiceInfo().getStatus().isAnnulled()) {
								String message = "La factura " + invoice.getReferenceCode() + " ya est\u00e1 anulada.";
								vp.add(getErrorMessage(message));
							} else {
								SII_SERVICE.bajaLroe240(options.getDomainName(), options.getDomain(), options.getUser(), invoice, params, new AsyncCallback<String>() {
									
									@Override
									public void onSuccess(String result) {
										String message = "La factura " + invoice.getReferenceCode() + " se ha enviado correctamente.";
										vp.add(getOkMessage(message));
										if(selectedInvoices.size() >= vp.getWidgetCount()) {
											invoiceGrid.setFilterParams(getFilterParams());
										}
									}
									
									@Override
									public void onFailure(Throwable caught) {
										String reference = AonStringUtils.isBlank(invoice.getSeries()) ? Integer.toString(invoice.getNumber()) : invoice.getSeries() + "/" + invoice.getNumber();
										reference = AonStringUtils.isBlank(invoice.getReferenceCode()) || "null".equalsIgnoreCase(invoice.getReferenceCode())
											? reference : invoice.getReferenceCode();
										vp.add(getErrorMessage("Factura " + reference + ": " + caught.getMessage()));
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
	
	public Label getWarningMessage(String message ){
		return getMessage(message, "orange");
	}
	
	public Model240 getModel240() {
		return model240;
	}
	
	public HorizontalPanel getActionErrorMessage(ICResponse resp, Invoice invoice){
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(getMessage("Factura "+ invoice.getReferenceCode() + ": " + resp.getErrorMessage(), "red"));
		if("AON_001".equals(resp.getErrorCode())
				|| "B4_2000013".equals(resp.getErrorCode())) {
			Label l = new Label("Pulse aqui para a\u00f1adir el Documento");
			l.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
			l.getElement().getStyle().setColor("#0069c2");
			l.getElement().getStyle().setCursor(Cursor.POINTER);
			l.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					FlexTable documentTable = new FlexTable();

					documentTable.setWidget(0, 0, new Label("Tipo de Documento"));

					DocumentTypeListBox dtlb = new DocumentTypeListBox();
					dtlb.setValue(invoice.getRegistryDocumentType());

					documentTable.setWidget(0, 1, dtlb);

					
					documentTable.setWidget(1, 0, new Label("Pa\u00eds de Documento"));

					CountryListBox clb = new CountryListBox();
					clb.setValue(invoice.getRegistryDocumentCountry());
					documentTable.setWidget(1, 1, clb);
					
					documentTable.setWidget(2, 0, new Label("Documento"));
					
					AonDocumentTextBox tb = new AonDocumentTextBox();
					tb.setValue(invoice.getRegistryDocument());
					documentTable.setWidget(2, 1, tb);
					
					AonDialog dialog = new AonDialog("A\u00f1adir Documento", documentTable);

					dialog.confirm(new AonAcceptDialogCallback() {
							
							@Override
							public void onCancel() {
								dialog.hide();
							}
								
							@Override
							public void onAccept() {
								dialog.hide();
								invoice.setRegistryDocumentCountry(clb.getValue());
								invoice.setRegistryDocumentType(dtlb.getValue());
								invoice.setRegistryDocument(tb.getValue());
								SII_SERVICE.addDocumentInvoice(options.getDomainName(), options.getDomain(), options.getUser(), invoice, new AsyncCallback<Void>() {
										
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
			});
			
			hp.add(l);
		}
		return hp;
	}
}
