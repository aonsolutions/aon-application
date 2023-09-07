package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.IRPFReportFilterPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.IrpfReportModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdown;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParamsGroupedBy;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

class WithholdingTypeCheck extends OptionBase {

	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/IRPFReportStream");
	
	private AonLayoutPanel dockPanel;
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	
	private IrpfReportModuleOptions irpfOptions;
	private IRPFReportFilterPanel filterPanel;
	
	protected WithholdingTypeCheck(FinanceUtilitiesModuleOptions options, Domain domain) {
		super(options, domain);
		dockPanel = new  AonLayoutPanel(Unit.PX);
		setContent(dockPanel);
		
		FinanceUtilitiesModule.COMMON_SERVICE.getAonConfiguration(options.getOccam(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				irpfOptions = new IrpfReportModuleOptions()
					.setParentWidget(options.getParentWidget())
					.setDomainName(options.getDomainName())
					.setDomain(options.getDomain())
					.setUser(options.getUser())
					.setConfiguration(config)
				;
				
				filterPanel = new IRPFReportFilterPanel(irpfOptions, true);
				filterPanel.addValueChangeHandler(e -> run(e.getValue()) );
				SimpleLayoutPanel filterPanelContainer = new SimpleLayoutPanel(  );
				filterPanelContainer.setWidget(filterPanel);
				dockPanel.addNorth( filterPanelContainer, 115);
				
				content = new SimpleLayoutPanel();
				content.setStyleName(AON.CSS.aonBorderTop());
				
				container = new ScrollPanel();
				container.setStyleName(AON.CSS.aonScrollArea());
				content.add(container);
				dockPanel.add(content);
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				dockPanel.showErrorPanel(AON.MSG.loadError( getOptionDescription() ));
			}
		});
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de tipos de retenci\u00F3n en facturas.";
	}

	@Override
	protected AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = super.getToolbarPanel();
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refresh.addClickHandler(event -> run( filterPanel.getParams(irpfOptions)) );
		toolbarPanel.add(refresh);

		return toolbarPanel;
	}

	public void run( IRPFParams params ) {
		content.clear();
		filterPanel.setValue( params );
		content.setWidget(new WithholdingTypeCheckPanel( irpfOptions, params));		

	}

	class WithholdingTypeCheckPanel extends ScrollPanel{
		
		WithholdingTypeCheckPanel(IrpfReportModuleOptions options, IRPFParams params) {
			setStyleName(AON.CSS.aonScrollArea());
			JsIRPFBreakdownInvoiceGridPanel grid = new JsIRPFBreakdownInvoiceGridPanel();
			grid.addSelectionHandler(event -> showInvoice(options, event.getSelectedItem()));
			setWidget(grid);
			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open(FormPanel.METHOD_POST, REPORT_URL);
			xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
				
				private int loaded = 0;
				
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();
					if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
						String text = xhr.getResponseText();
						try {
							for (JsIRPFBreakdown irpf = read(text); text != null; irpf = read(text)) {
								grid.addRow(irpf);
							}
						} catch (IndexOutOfBoundsException e) {
							// Nothing
						}
					}
					if (state == XMLHttpRequest.DONE) {
						grid.addFooterRow();
					}
				}

				private JsIRPFBreakdown read(String text) {
					for (int begin = loaded; begin < text.length(); begin++) {
						if (text.charAt(begin) == '{') {
							loaded = findEnd(text, begin + 1) + 1;
							String json = text.substring(begin, loaded);
							return JsonUtils.safeEval(json);
						}
					}
					throw new IndexOutOfBoundsException();
				}

				private int findEnd(String text, int start) {
					for (int end = start; end < text.length(); end++) {
						if (text.charAt(end) == '}') {
							return end;
						} else if (text.charAt(end) == '{') {
							end = findEnd(text, end + 1);
						}
					}
					throw new IndexOutOfBoundsException();
				}
			});
			params.setGroupedBy(IRPFParamsGroupedBy.INVOICE);
			StringBuilder requestData = new StringBuilder();
			requestData.append("&"+IRequestParamsNames.DOMAIN_NAME+"=" + options.getDomainName()  );
			requestData.append("&"+IRequestParamsNames.DOMAIN_ID+"=" + options.getDomain() );
			requestData.append("&"+IRequestParamsNames.USER+"=" + options.getUser() );
			requestData.append("&"+IRequestParamsNames.IRPF_PARAMS+"=" + JsonParams.convert( params ));
			xhr.send(requestData.toString());

		}

		private void showInvoice(IrpfReportModuleOptions options, JsIRPFBreakdown br) {
			int invoiceId = br.getInvoice();
			FinanceUtilitiesModule.SERVICE.getInvoice(options.getOccam(), invoiceId,new AsyncCallback<Invoice>() {
				@Override
				public void onSuccess(Invoice inv) {
					AonCustomPopup dialog = new AonCustomPopup();
					dialog.setWidth((Window.getClientWidth() - 100) + "px");
					dialog.setHeight((Window.getClientHeight() - 100) + "px");
					dialog.setAnimationEnabled(true);
					dialog.setGlassEnabled(true);
					dialog.setModal(true);
					dialog.setCaption(AON.MSG.invoice());
					dialog.add(new AonInvoiceViewer(inv));
					dialog.center();
					dialog.show();
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert( AON.MSG.loadError("Error inesperado: " + caught.getMessage()));
				}
			});
		}
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		return null;
	}
	
	class JsIRPFBreakdownInvoiceGridPanel extends FlowPanel implements HasSelectionHandlers<JsIRPFBreakdown>{
		
		private final Label title;
		private final Label subTitle;
		private final AonDisplayGrid grid;
		private double sumBase = 0.0;
		private double sumQuota = 0.0;
		
		JsIRPFBreakdownInvoiceGridPanel() {
			title = new Label();
			title.setStyleName(AON.CSS.aonMarginTop());
			title.addStyleName(AON.CSS.aonBold());
			title.addStyleName(AON.CSS.aonWidthAll());
			title.addStyleName(AON.CSS.aonTextCenter());
			title.addStyleName(AON.CSS.aonTextUppercase());
			title.addStyleName(AON.CSS.aonFontLarger());
			add( title );
			subTitle = new Label();	
			subTitle.setStyleName(AON.CSS.aonMarginTop());
			subTitle.addStyleName(AON.CSS.aonBold());
			subTitle.addStyleName(AON.CSS.aonWidthAll());
			subTitle.addStyleName(AON.CSS.aonTextCenter());
			subTitle.addStyleName(AON.CSS.aonFontLarger());
			add( subTitle );
			grid = new AonDisplayGrid();
			grid.addStyleName(AON.CSS.aonMarginTop());
			grid.addStyleName(AON.CSS.aonBlockCenter());
			paintHeader();
			add( grid );
		}
		
		@Override
		public void setTitle( String title) {
			super.setTitle(title);
			this.title.setText(title);	
		}
		public void setSubTitle( String subTitle) {
			this.subTitle.setText(subTitle);	
		}
		
		private void paintHeader() {
			grid.addHeaderRow()
				.addCell(new Label(""),AON.CSS.aonWidth20())
				.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
				.addCell(new Label("Tipo Ret."),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
				.addCell(new Label("N\u00BA Documento"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
				.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto(), AON.CSS.aonNowrap())
				.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidthAuto())
				.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
				.addCell(new Label("Base Imp."),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())		
				.addCell(new Label("% IRPF"),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(), AON.CSS.aonNowrap())
				.addCell(new Label("Cuota"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
				.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			;
		}
		
		private String ensure(Object nullable, Supplier<String>  supplier) {
			return ensure(nullable, supplier, "---");
		}
		private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
			return (nullable == null) 
				? defaultValue
				: supplier.get();
		}
		
		
		public final native boolean hasInvoiceId(int invoice ) /*-{
			return !isNaN(invoice) && invoice != 0;
		}-*/;
		
		public void addRow(JsIRPFBreakdown br) {
			InvoiceType invoiceType = InvoiceType.safeValueOf(br.getInvoiceType());
			WithholdingType withholdingType = WithholdingType.safeValueOf(br.getWithholdingType());
			
			FlowPanel withholdingTypePanel = new FlowPanel(); 
			WithholdingTypeListBox wtb = new WithholdingTypeListBox();
			final InlineLabel msg = new InlineLabel("");
			msg.setStyleName(AON.CSS.aonTabIcon());
			msg.addStyleName(AON.CSS.aonMarginLeft());
			withholdingTypePanel.add(wtb);
			withholdingTypePanel.add(msg);
			
			wtb.setValue(withholdingType);
			wtb.addChangeHandler(e -> {
				WithholdingType wt = wtb.getValue();
				if (wt == null) {
					AonMessageDialog.error("El tipo de retenci\u00F3n es un dato requerido");
					wtb.setValue(withholdingType);	
				} else {
					FinanceUtilitiesModule.SERVICE.updateWithholdingType(getOptions().getOccam(), br.getInvoice() , wt, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							wtb.setValue(withholdingType);
							AonMessageDialog.error("Modificación no realizada. ["+ caught.getMessage() +"]");
						}

						@Override
						public void onSuccess(Void result) {
							msg.addStyleName(AON.CSS.aonIconValid());
						}
					});
				}
				
			});
			
			AonTableButton selectButton = new AonTableButton(AON.MSG.viewInvoice(), AON.CSS.aonIconSearch());
			selectButton.addClickHandler(event -> SelectionEvent.fire(this, br));
			
			AonDisplayGridRow row = grid.addRow();
			row
				.addCell(selectButton)
				.addCell(new Label(ensure(invoiceType,invoiceType::getAbbrDescription)))
				.addCell(withholdingTypePanel)
				.addCell(new Label(ensure(br.getDocumentNumber(), br::getDocumentNumber, AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(br.getRegistryDocument(), br::getRegistryDocument, AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(br.getRegistryName(), () -> AonStringUtils.abbreviate(br.getRegistryName(),25), AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(br.getIssueDate(), () -> AON.DATE_FORMAT.format(br.getIssueDate()), AonStringUtils.EMPTY)))
				.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getBase())),AON.CSS.aonTextRight())
				.addCell(new Label(AonMathUtils.isLessThanZero( br.getPercent())
						?"------" 
						:AON.CURRENCY_FORMAT.format(br.getPercent()) + "%"),AON.CSS.aonTextRight())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getQuota())),AON.CSS.aonTextRight())
				.addCell(new Label(ensure(br.getReferenceCode(), br::getReferenceCode, AonStringUtils.EMPTY)))
			;
			sumBase += br.getBase();
			sumQuota += br.getQuota();
		}

		public void addFooterRow() {
			grid.addFooterRow()
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label())
				.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(sumBase)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label())
				.addCell(new Label(AON.CURRENCY_FORMAT.format(sumQuota)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
				.addCell(new Label())
			;
		}
		public void setReportTitle( String title) {
			this.title.setText(title);
		}

		@Override
		public HandlerRegistration addSelectionHandler(SelectionHandler<JsIRPFBreakdown> handler) {
			return super.addHandler(handler, SelectionEvent.getType());
		}
	}
	
}
