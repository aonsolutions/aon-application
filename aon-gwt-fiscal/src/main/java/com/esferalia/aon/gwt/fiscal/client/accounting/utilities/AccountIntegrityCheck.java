package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountIntegritItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AccountIntegrityCheck extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	 
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected AccountIntegrityCheck(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		content = new SimpleLayoutPanel();
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Chequeo de integridad cuentas contables.";
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
		
		final Button refresh = new Button();
		refresh.setText(AON.MSG.refresh());
		refresh.setTitle(AON.MSG.refresh());
		refresh.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		refresh.addStyleName(AON.AON_CSS.aonIconRedo());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		buttonContainer.add(refresh);
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
		
		SERVICE.accountIntegrity(domainName, user, domain, new AsyncCallback<AccUtilitiesResult>(){

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
		FlowPanel log = new FlowPanel();
		log.setStyleName(AON.AON_CSS.aonWidth98Percent());
		log.addStyleName(AON.AON_CSS.aonBlockCenter());
		log.addStyleName(AON.AON_CSS.aonMarginTop());
		log.addStyleName(AON.AON_CSS.aonMarginBottom());
		log.addStyleName(AON.AON_CSS.aonFixedFont());
		log.addStyleName(AON.AON_CSS.aonFontMedium());
		log.addStyleName(AON.AON_CSS.aonNowrap());
		if (result != null && !result.isEmpty()) {
			String lastDomain = null;
			DisclosurePanel disclosurePanel = null;
			FlowPanel domainPanel = null;
			for (IAccUtilitiesItem item : result.getItems()) {
				if (!AonStringUtils.equals(lastDomain, item.getDomainName())) {
					if (disclosurePanel != null) {
						String header = lastDomain + " (" + domainPanel.getWidgetCount() + ")";
						disclosurePanel.getHeaderTextAccessor().setText(header);
						disclosurePanel.getHeader().addStyleName(AON.AON_CSS.aonFixedFont());
						disclosurePanel.getHeader().addStyleName(AON.AON_CSS.aonFontMedium());
						log.add(disclosurePanel);
					}
					
					disclosurePanel = new DisclosurePanel(item.getDomainName());
					if (AonStringUtils.isBlank(lastDomain)) disclosurePanel.setOpen(true);
					lastDomain = item.getDomainName();
					domainPanel = new FlowPanel();
					disclosurePanel.add(domainPanel);
					disclosurePanel.addStyleName(AON.AON_CSS.aonMarginTop());
					disclosurePanel.addStyleName(AON.AON_CSS.aonFixedFont());
					disclosurePanel.addStyleName(AON.AON_CSS.aonFontMedium());
					disclosurePanel.addStyleName(AON.AON_CSS.aonNowrap());
				}
				item.getType().visit( new AccountIntegritVisitor(domainPanel,(AccUtilitiesAccountIntegritItem) item) );
			}
			if (disclosurePanel != null) {
				String header = lastDomain + " (" + domainPanel.getWidgetCount() + ")";
				disclosurePanel.getHeaderTextAccessor().setText(header);
				disclosurePanel.getHeader().addStyleName(AON.AON_CSS.aonFixedFont());
				disclosurePanel.getHeader().addStyleName(AON.AON_CSS.aonFontMedium());
				log.add(disclosurePanel);
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}
	
	private class AccountIntegritVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlowPanel domainPanel;
		private AccUtilitiesAccountIntegritItem item;
		
		public AccountIntegritVisitor(FlowPanel domainPanel, AccUtilitiesAccountIntegritItem item) {
			this.domainPanel = domainPanel;
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
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {
			FlowPanel itemPanel = new FlowPanel();
			InlineLabel msgLabel = new InlineLabel(item.getMessage());
			itemPanel.add(msgLabel);

			InlineLabel removeLabel = new InlineLabel("Arreglar");
			removeLabel.setTitle("Arreglar");
			removeLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			removeLabel.addStyleName(AON.AON_CSS.aonIconDelete());
			removeLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			removeLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			itemPanel.add(removeLabel);
			removeLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					SERVICE.accountIntegrityFix(domainName, user, item.getDomain(), item.getAccount(), new AsyncCallback<AccUtilitiesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							openFootPanelIfNeeded();
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(AccUtilitiesResult result) {
							run();
						}
					});
				}
			});

			domainPanel.add(itemPanel);
		}
	}
	
}
