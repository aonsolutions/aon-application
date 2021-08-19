package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanel;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateJournalItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class JournalRegenerator extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	 
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected JournalRegenerator(String domainName, String user, Domain domain) {
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
		return AonStringUtils.BULLET + " Regenerar el n\u00FAmero de diario";
	}

	public void run() {
		SERVICE.getJournalRegenerationInfo(domainName, user, domain.getId(), new AsyncCallback<AccUtilitiesResult>() {

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(caught.getMessage());
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				container.setWidget(paintResults(result));
			}
		});
	}
	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonGrid());
		tab.addStyleName(AON.CSS.aonBlockCenter());

		tab.getColumnFormatter().setWidth(0, "100px");
		tab.getColumnFormatter().setWidth(1, "100px");
		tab.getColumnFormatter().setWidth(2, "auto");
		tab.getColumnFormatter().setWidth(3, "20px");
		tab.getColumnFormatter().setWidth(4, "80px");
		tab.getColumnFormatter().setWidth(5, "90px");

		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear()));
		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonGridHeader());

		tab.setWidget(0, 1, new Label(AON.MSG.status()));
		tab.getCellFormatter().setStyleName(0, 1, AON.CSS.aonGridHeader());
		
		tab.setWidget(0, 2, new Label(AON.MSG.message()));
		tab.getCellFormatter().setStyleName(0, 2, AON.CSS.aonGridHeader());
		
		tab.setWidget(0, 3, new Label("Acciones disponibles"));
		tab.getCellFormatter().setStyleName(0, 3, AON.CSS.aonGridHeader());
		tab.getFlexCellFormatter().setColSpan(0, 3, 3);
		
		int row = 1;
		for (IAccUtilitiesItem it :  result.getItems()) {
			AccUtilitiesRegenerateJournalItem item = (AccUtilitiesRegenerateJournalItem) it;
			tab.setWidget(row, 0, new Label(item.getAccountPeriod().getName()));
			tab.setWidget(row, 1, new Label(item.getAccountPeriod().getStatus().getDescription()));
			tab.setWidget(row, 2, new Label(item.getMessage()));
			
			InlineLabel reg = new InlineLabel();
			reg.setStyleName(AON.CSS.aonIconLabel());
			if (item.isRegenerable() ) {
				reg.addStyleName(AON.CSS.aonIconWarning());
			} else {
				reg.addStyleName(AON.CSS.aonIconValid());
			}
			tab.setWidget(row, 3, reg);
			
			AonTextButton view = new AonTextButton("Ver", AON.CSS.aonIconSearch());
			tab.setWidget(row, 4, view);
			view.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					JournalPanel journalPanel = new JournalPanel(
						new AccountingReportModuleOptions()
							.setDomainName(domainName)
							.setDomain(domain.getId())
							.setUser(user)
						,new AccountEntryParams()
							.setDomain(item.getDomain())
							.setPeriod(item.getAccountPeriod().getId()));
					showResults(journalPanel);
				}
			});
			
			AonTextButton regenerate = new AonTextButton("Regenerar", AON.CSS.aonIconFix());
			regenerate.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (item.getAccountPeriod().getStatus() != AccountPeriodStatus.ACTIVE) {
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm("Confirme","El ejericio que pretende regenerar se encuentra en estado "
								+ item.getAccountPeriod().getStatus().getDescription()
								+ " Si continua, se modificar\u00E1n los n\u00FAmeros de asientos \u00BFDesea continuar?"
						, new AonConfirmDialogCallback() {
							
							@Override public void onCancel() {}
							
							@Override
							public void onAccept() {
								regenerate();
							}
						});
					} else {
						regenerate();
					}
				}

				private void regenerate() {
					SERVICE.regenerateJournal(domainName, user, domain.getId(), item.getAccountPeriod().getId(), new AsyncCallback<AccUtilitiesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(AccUtilitiesResult result) {
							if (result.getItems() != null && result.getItems().size()>0) {
								showInfoPanel(result.getItems().get(0).getMessage());
							}
							run();
						}
					});
				}
			});
			tab.setWidget(row, 5, regenerate);
			
			row++;
		};
		return tab;
	}
}
