package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesInvoiceIntegrityItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class InvoiceIntegrityCheck extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	 
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected InvoiceIntegrityCheck(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		content = new SimpleLayoutPanel();
		container = new ScrollPanel();
		container.setStyleName(AON.CSS.aonScrollArea());
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de integridad de tipos de facturas.";
	}

	@Override
	protected Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar(getOptionDescription());
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		toolbar.add(refresh);
		return toolbar;
	}
	
	
	public void run() {
		final PopupPanel popup = new PopupPanel(false, true);
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName(AON.CSS.aonBlockCenter());
		hp.addStyleName(AON.CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label(AON.MSG.processing());
		textWaitLabel.setStyleName(AON.CSS.aonMargin());
		textWaitLabel.addStyleName(AON.CSS.aonBold());
		hp.add(textWaitLabel);
		popup.add(hp);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		SERVICE.invoiceIntegrity(domainName, user, domain, new AsyncCallback<AccUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		AonDisplayGrid log = new AonDisplayGrid();
		log.addStyleName(AON.CSS.aonWidthAlmostAll());
		log.addStyleName(AON.CSS.aonMarginTop());
		log.addStyleName(AON.CSS.aonMarginBottom());
		log.addStyleName(AON.CSS.aonFixedFont());
		
		if (result != null && !result.isEmpty()) {
			for (IAccUtilitiesItem item : result.getItems()) {
				item.getType().visit( new InvoiceIntegrityVisitor(log,(AccUtilitiesInvoiceIntegrityItem) item) );
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.addRow().addCell(label);
		}
		return log;
	}
	
	private class InvoiceIntegrityVisitor implements IAccUtilitiesItemTypeVisitor {
		private AonDisplayGrid log;
		private AccUtilitiesInvoiceIntegrityItem item;
		
		public InvoiceIntegrityVisitor(AonDisplayGrid log, AccUtilitiesInvoiceIntegrityItem item) {
			this.log = log;
			this.item = item;
		}
		
		@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
		@Override public void visitParentAccountLinker(AccUtilitiesItemType type) {}
		@Override public void visitOther(AccUtilitiesItemType type) {}
		@Override public void visitInfoMessage(AccUtilitiesItemType type) {}
		@Override public void visitErrorMessage(AccUtilitiesItemType type) {}
		@Override public void visitCustomerAccount(AccUtilitiesItemType type) {}
		@Override public void visitSupplierAccount(AccUtilitiesItemType type) {}
		@Override public void visitCreditorAccount(AccUtilitiesItemType type) {}
		@Override public void visitEmptyEntry(AccUtilitiesItemType type) {}
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {}
		@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
		@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitAccountChange(AccUtilitiesItemType type) {}
		@Override public void visitOutOfDateEntry(AccUtilitiesItemType type) {}
		
		@Override public void visitInvoiceIntegrity(AccUtilitiesItemType type) {
			InlineLabel msgLabel = new InlineLabel(item.getMessage());

			AonTableButton fixButton = new AonTableButton("Arreglar",AON.CSS.aonIconFix());
			fixButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					SERVICE.invoiceIntegrityFix(domainName, user, item.getDomain(), item.getInvoice().getId(), new AsyncCallback<AccUtilitiesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							openFootPanelIfNeeded();
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(AccUtilitiesResult result) {
							if (result.hasErrorMessages()) {
								msgLabel.setText(result.getFirstMessage());
								fixButton.addStyleName(AON.CSS.aonColorRed());
								fixButton.setVisible(false);
							} else {
								run();
							}
						}
					});
				}
			});

			log.addRow()
				.addCell( new InlineLabel(item.getInvoice().getReferenceCode()))
				.addCell( new InlineLabel(AON.DATE_FORMAT.format(item.getInvoice().getIssueDate()) ))
				.addCell( new InlineLabel(item.getInvoice().getRegistryDocument()))
				.addCell( new InlineLabel(item.getInvoice().getRegistryName()))
				.addCell(msgLabel,AON.CSS.aonFlexGrow1())
				.addCell(fixButton);	
			
		}

	}
	
}
