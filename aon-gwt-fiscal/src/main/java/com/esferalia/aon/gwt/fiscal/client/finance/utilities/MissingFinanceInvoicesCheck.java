package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class MissingFinanceInvoicesCheck extends OptionBase {

	private DockLayoutPanel dockPanel;
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	private ListBox typeBox = new ListBox();
	private AonDateBox fromBox = new AonDateBox();
	private AonDateBox toBox = new AonDateBox();
	
	protected MissingFinanceInvoicesCheck(FinanceUtilitiesModuleOptions options, Domain domain) {
		super(options, domain);
		dockPanel = new  DockLayoutPanel(Unit.PX);
		setContent(dockPanel);
		
		FlowPanel northPanel = new FlowPanel();
		northPanel.setStyleName(AON.CSS.aonSearchPanel());
		northPanel.addStyleName(AON.CSS.aonMarginLeft());
		northPanel.addStyleName(AON.CSS.aonMarginRight());
		northPanel.addStyleName(AON.CSS.aonBlockCenter());
		northPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		typeBox.addItem(" --- ", "");
		for ( InvoiceType t : InvoiceType.values()) {
			typeBox.addItem( t.getDescription(), AonNumberUtils.toString( t.ordinal() ));	
		}
		
		FlowPanel datePanel = new FlowPanel();
		InlineLabel fromLabel = new InlineLabel(AON.MSG.from());
		fromLabel.setStyleName(AON.CSS.aonInnerLabel());
		fromLabel.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(fromLabel);
		datePanel.add(fromBox);
		
		InlineLabel toLabel = new InlineLabel(AON.MSG.from());
		toLabel.setStyleName(AON.CSS.aonInnerLabel());
		toLabel.addStyleName(AON.CSS.aonMarginLeft());
		toLabel.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(toLabel);
		datePanel.add(toBox);
		
		AonSearchPanelButton run = new AonSearchPanelButton(AON.MSG.searchAction(), AON.CSS.aonIconSearch());
		run.setText(AON.MSG.searchAction());
		run.addClickHandler(event -> run());
		
		tab.addRow()
			.addCell(new Label(AON.MSG.invoiceType()))
			.addCell(typeBox)
			.addCell(new Label(AON.MSG.issueDate()))
			.addCell(datePanel)
			.addCell(run)
		;
		
		northPanel.add(tab);
		
		dockPanel.addNorth(northPanel, 50);
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonBorderTop());
		
		container = new ScrollPanel();
		container.setStyleName(AON.CSS.aonScrollArea());
		content.add(container);
		dockPanel.add(content);
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Buscador de facturas sin vencimientos.";
	}

	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
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
		
		FinanceUtilitiesModule.SERVICE.missingFinanceInvoices( getOptions().getOccam(), getDomain(), params, new AsyncCallback<FinanceUtilitiesResult>(){

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
		if (result.getItems() != null && !result.getItems().isEmpty()) {
			AonDisplayGrid grid = new AonDisplayGrid(); 
			grid.addStyleName(AON.CSS.aonBlockCenter());
			
			grid.addHeaderRow()
			 .addCell(new Label("Tipo"), AON.CSS.aonWidth100())
			 .addCell(new Label("Fecha"), AON.CSS.aonWidth100())
			 .addCell(new Label("N. Factura"), AON.CSS.aonWidth200())
			 .addCell(new Label("Titular"), AON.CSS.aonWidthAuto())
			 .addCell(new Label("Importe"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			 .addCell(new Label(""), AON.CSS.aonWidth80())
			 ;
			for ( IFinanceUtilitiesItem it : result.getItems() ) {
				MissingFinanceInvoiceItem item = (MissingFinanceInvoiceItem) it;
				Invoice invoice = item.getInvoice();
				FlowPanel fixPanel = new FlowPanel();
				item.getType().visit( new MissingFinanceInvoicesVisitor(fixPanel, item) );
				grid.addRow()
				 .addCell(new Label(invoice.getType().getDescription()))
				 .addCell(new Label( AON.DATE_FORMAT.format( invoice.getIssueDate())))
				 .addCell(new Label(invoice.isSales()?invoice.getDocumentNumber():invoice.getReferenceCode()))
				 .addCell(new Label(invoice.getRegistryName()))
				 .addCell(new Label(AON.FMT.format( invoice.getTotal())))
				 .addCell(fixPanel)
				 ;
			}
			tabContainer.add(grid);
		} else {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.CSS.aonTextCenter());
			noData.addStyleName(AON.CSS.aonBold());
			noData.addStyleName(AON.CSS.aonMarginTop());
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
		
		@Override 
		public void visitErrorMessage(FinanceUtilitiesItemType type) { 
			// nothing
		}
		@Override 
		public void visitInfoMessage(FinanceUtilitiesItemType type) {
			// nothing
		}
		@Override 
		public void visitOther(FinanceUtilitiesItemType type) {
			// nothing
		}
		@Override 
		public void visitFinanceInvoiceIntegrityCheck(FinanceUtilitiesItemType type) {
			// nothing
		}
		
		@Override
		public void visitMissingFinanceInvoice(FinanceUtilitiesItemType type) {
			AonTableButton viewButton = new AonTableButton(AON.MSG.show(), AON.CSS.aonIconSearch());
			viewButton .setVisible(false);

			AonTableButton addButton = new AonTableButton(AON.MSG.financeGenerate(), AON.CSS.aonIconAdd()); 
			addButton.addClickHandler( event -> FinanceUtilitiesModule.SERVICE.missingFinanceInvoicesFix(getOptions().getOccam(), item.getInvoice().getId(), new AsyncCallback<Invoice>() {

				@Override
				public void onFailure(Throwable caught) {
					openFootPanelIfNeeded();
					showErrorPanel(caught.getMessage());
				}

				@Override
				public void onSuccess(Invoice result) {
					viewButton.addClickHandler(event1 -> showResults(new AonInvoiceViewer(result)));
					addButton.setVisible(false);
					viewButton.setVisible(true);
				}
			}));
			domainPanel.add(viewButton);
			domainPanel.add(addButton);
		}
	}
}
