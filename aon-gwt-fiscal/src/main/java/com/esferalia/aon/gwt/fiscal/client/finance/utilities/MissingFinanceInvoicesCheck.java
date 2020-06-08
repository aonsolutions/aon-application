package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoiceViewer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem.FinanceUtilitiesItemType;
import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem.IFinanceUtilitiesItemTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.utilities.MissingFinanceInvoiceItem;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class MissingFinanceInvoicesCheck extends OptionBase {

	private static FinanceUtilitiesServiceAsync SERVICE;
	private String domainName;
	private String user;
	private Domain domain;
	
	private DockLayoutPanel dockPanel = new  DockLayoutPanel(Unit.PX);
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	private ListBox typeBox = new ListBox();
	private DateBoxEx fromBox = new DateBoxEx();
	private DateBoxEx toBox = new DateBoxEx();
	
	protected MissingFinanceInvoicesCheck(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		FinanceUtilitiesServiceAsync serviceRaw = GWT.create(FinanceUtilitiesService.class);
		SERVICE = new FinanceUtilitiesServiceAsyncDecorator(serviceRaw);

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		Label title = new Label("Introduzca los datos para filtrar facturas");
		title.setStyleName(AON.AON_CSS.aonMarginBottom());
		tab.setWidget(0, 0, title);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBold());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		tab.setWidget(1, 0, new Label(AON.MSG.invoiceType()));
		tab.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonPanelGridOdd());
		
		typeBox.addItem(" --- ", "");
		for ( InvoiceType t : InvoiceType.values()) {
			typeBox.addItem( t.getDescription(), AonNumberUtils.toString( t.ordinal() ));	
		}
		tab.setWidget(1, 1, typeBox);
		tab.getCellFormatter().setStyleName(1, 1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(2, 0, new Label(AON.MSG.issueDate()));
		tab.getCellFormatter().setStyleName(2, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel datePanel = new FlowPanel();
		InlineLabel fromLabel = new InlineLabel(AON.MSG.from());
		fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		fromLabel.addStyleName(AON.AON_CSS.aonMarginRight());
		datePanel.add(fromLabel);
		datePanel.add(fromBox);
		
		InlineLabel toLabel = new InlineLabel(AON.MSG.from());
		toLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		toLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
		toLabel.addStyleName(AON.AON_CSS.aonMarginRight());
		datePanel.add(toLabel);
		datePanel.add(toBox);
		tab.setWidget(2, 1, datePanel);
		tab.getCellFormatter().setStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());
		
		Button run = new Button();
		run.setText("Buscar");
		run.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		run.addStyleName(AON.AON_CSS.aonSimpleBorder());
		run.addStyleName(AON.AON_CSS.aonIconLoupe());
		run.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().setStyleName(3, 0, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(3, 0, 2);
		run.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		tab.setWidget(3, 0, run);
		
		setContent(dockPanel);
		
		dockPanel.addNorth(tab, 150);
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.AON_CSS.aonBorderTop());
		
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		dockPanel.add(content);
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Buscador de facturas sin vencimientos.";
	}

	protected Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(getOptionDescription()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	
	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		InvoiceType invoiceType = null;
		int i = typeBox.getSelectedIndex();
		if (i > 0) {
			invoiceType = InvoiceType.values()[i-1];
		}
		FinanceUtilitiesParams params = new FinanceUtilitiesParams()
				.setInvoiceType(invoiceType)
				.setFromDate(fromBox.getValue())
				.setToDate(toBox.getValue());
		
		SERVICE.missingFinanceInvoices(domainName, user, domain, params, new AsyncCallback<FinanceUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(FinanceUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		FlowPanel tabContainer = new FlowPanel();
		if (result.getItems() != null && result.getItems().size() > 0) {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			
			tab.getColumnFormatter().setWidth(0, "100px");
			tab.getColumnFormatter().setWidth(1, "100px");
			tab.getColumnFormatter().setWidth(2, "180px");
			tab.getColumnFormatter().setWidth(3, "auto");
			tab.getColumnFormatter().setWidth(4, "100px");
			tab.getColumnFormatter().setWidth(5, "70px");
			
	
			tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().setStyleName(0,4, AON.AON_CSS.aonDataTableHeader());
			tab.getCellFormatter().addStyleName(0,4, AON.AON_CSS.aonTextRight());
			tab.getCellFormatter().setStyleName(0,5, AON.AON_CSS.aonDataTableHeader());
			
			tab.setWidget(0, 0, new Label("Tipo"));
			tab.setWidget(0, 1, new Label("Fecha"));
			tab.setWidget(0, 2, new Label("N. Factura"));
			tab.setWidget(0, 3, new Label("Titular"));
			tab.setWidget(0, 4, new Label("Importe"));
			tab.setWidget(0, 5, new Label(""));
			
			int row = 1;
			for ( IFinanceUtilitiesItem it : result.getItems() ) {
				MissingFinanceInvoiceItem item = (MissingFinanceInvoiceItem) it;
				Invoice invoice = item.getInvoice();
				tab.setWidget(row, 0, new Label(invoice.getType().getDescription()));
				tab.setWidget(row, 1, new Label( AON.DATE_FORMAT.format( invoice.getIssueDate())));
				tab.setWidget(row, 2, new Label(invoice.isSales()?invoice.getDocumentNumber():invoice.getReferenceCode()));
				tab.setWidget(row, 3, new Label(invoice.getRegistryName()));
				tab.getCellFormatter().setStyleName(row,4, AON.AON_CSS.aonTextRight());
				tab.setWidget(row, 4, new Label(AON.FMT.format( invoice.getTotal())));
				
				FlowPanel fixPanel = new FlowPanel();
				item.getType().visit( new MissingFinanceInvoicesVisitor(fixPanel,(MissingFinanceInvoiceItem) item) );
				tab.setWidget(row, 5, fixPanel);
				row++;
			}
			
			tabContainer.add(tab);
		} else {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonBold());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			tabContainer.add(noData);	
		}
		
		return tabContainer;
	}
	
	private class MissingFinanceInvoicesVisitor implements IFinanceUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private MissingFinanceInvoiceItem item;
		
		public MissingFinanceInvoicesVisitor(FlowPanel domainPanel, MissingFinanceInvoiceItem item) {
			this.domainPanel = domainPanel;
			this.item = item;
		}
		
		@Override public void visitErrorMessage(FinanceUtilitiesItemType type) {}
		@Override public void visitInfoMessage(FinanceUtilitiesItemType type) {}
		@Override public void visitOther(FinanceUtilitiesItemType type) {}
		@Override public void visitFinanceInvoiceIntegrityCheck(FinanceUtilitiesItemType type) {}
		
		@Override
		public void visitMissingFinanceInvoice(FinanceUtilitiesItemType type) {
			InlineLabel viewLabel = new InlineLabel("Ver");
			viewLabel.setVisible(false);
			viewLabel.setTitle("Ver");
			viewLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			viewLabel.addStyleName(AON.AON_CSS.aonIconDelete());
			viewLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			viewLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			
			InlineLabel removeLabel = new InlineLabel("Crear");
			removeLabel.setTitle("Arreglar");
			removeLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			removeLabel.addStyleName(AON.AON_CSS.aonIconReset());
			removeLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			removeLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			removeLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					SERVICE.missingFinanceInvoicesFix(domainName, user, item.getDomain(), item.getInvoice().getId(), new AsyncCallback<Invoice>() {

						@Override
						public void onFailure(Throwable caught) {
							openFootPanelIfNeeded();
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(Invoice result) {
							viewLabel.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									InvoiceViewer viewer = new InvoiceViewer(result);
									showResults(viewer);
								}
							});
							removeLabel.setVisible(false);
							viewLabel.setVisible(true);
						}
					});
				}
			});
			domainPanel.add(viewLabel);
			domainPanel.add(removeLabel);
		}
	}
}
