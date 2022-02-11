package com.esferalia.aon.gwt.fiscal.client.mod140;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSearchBox;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.SiiService;
import com.esferalia.aon.gwt.fiscal.client.SiiServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.SiiServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceGrid;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LroeModel140 extends DockLayoutPanel {
	
	private static final SiiServiceAsync SII_SERVICE;
	static {
		SiiServiceAsync siiServiceRaw = GWT.create(SiiService.class);
		SII_SERVICE = new SiiServiceAsyncDecorator(siiServiceRaw); 
	}
	
	private final AonMenuItem chapter1 = new AonMenuItem()
			.setTitle("1. Ingresos y facturas emitidas")
			.addItem(new AonMenuItem().setTitle("1.1 Con software garante")
					.setHandler(chapter1_1Handler()))
			.addItem(new AonMenuItem().setTitle("1.2 Sin software garante")
					.setHandler(chapter1_2Handler()))
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
				Window.alert("En desarrollo...");
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
			}
		};
	
		add(invoiceGrid);
		setStyleName(AON.CSS.aonSelector());
	}
	
	public void initializeFilter() {
		this.filterParams = new InvoiceParams()
			.setDomain(getOptions().getDomain())
			.setType(InvoiceType.SALES)
			.setCommunicationType(InvoiceCommunicationType.LROE_1_1);
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

	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE, getModel().getFullName()));
		
		AonToolbarSearchBox searchBox = new AonToolbarSearchBox() {
			
			@Override
			public void onValueChange(String value) {
				getFilterParams().setValue(value).setPage(1).setPerPage(30);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
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
		
		AonToolbarButton draftButton = new AonToolbarButton(AON.MSG.generateFile(), AON.CSS.aonIconDownload());
		draftButton.addClickHandler(event -> submitForm());
		toolbarPanel.add(draftButton);
		
		toolbarPanel.add(diskForm);
		
		return toolbarPanel;
	}
	
	private void submitForm() {
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
		diskForm.add(html);
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
				.setCommunicationType(InvoiceCommunicationType.LROE_1_1)
				.setType(InvoiceType.SALES);
				invoiceGrid.setFilterParams(getFilterParams());
			}
		};
	}
	
	private ClickHandler chapter1_2Handler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getFilterParams()
				.setCommunicationType(InvoiceCommunicationType.LROE_1_2)
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
				.setCommunicationType(InvoiceCommunicationType.LROE_1_2)
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
							if(invoice.getInvoiceInfo().getStatus().isAccepted()) {
								String message = "La factura " + invoice.getReferenceCode() + " ya est\u00e1 enviada.";
								vp.add(getErrorMessage(message));
							} else {
								SII_SERVICE.altaLroe140(options.getDomainName(), options.getDomain(), options.getUser(), getFilterParams().getCommunicationType(), invoice, params, new AsyncCallback<ICResponse>() {
									
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
	
	public Label getWarningMessage(String message ){
		return getMessage(message, "orange");
	}
	
	public Model140 getModel140() {
		return model140;
	}
}
