package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountingPeriodModule  implements EntryPoint {
	
	private static final AccountingPeriodServiceAsync SERVICE;
	static {
		AccountingPeriodServiceAsync serviceRaw = GWT.create(AccountingPeriodService.class);
		SERVICE = new AccountingPeriodServiceAsyncDecorator(serviceRaw);
	}
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	
	private DockLayoutPanel dockLayoutPanel;
	private AonCards container;
	private AonToolbar toolbar;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		AccountingPeriodModuleOptions options = new AccountingPeriodModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final AccountingPeriodModuleOptions opt ) {
		AON.ensureInjected();

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getOccam(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void loadModule( final AccountingPeriodModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		SimpleLayoutPanel centerLayoutPanel = new SimpleLayoutPanel();
		container = new AonCards();
		centerLayoutPanel.setWidget(container );
		splitLayoutPanel.add(centerLayoutPanel);
		Scheduler.get().scheduleDeferred(() -> search(opt));		
	}

	private Widget getToolbarPanel(final AccountingPeriodModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.accountingPeriods());

		AonToolbarButton addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(event -> {
			AonCard card = addCard(opt, new AccountPeriod().setDomain(opt.getDomain()));
			card.setFocus(true);
		});
		toolbar.add(addButton);

		AonToolbarButton searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(event -> search(opt));
		toolbar.add(searchButton);

		return toolbar;
	}
	
	private AccountingPeriodCard addCard(final AccountingPeriodModuleOptions opt, AccountPeriod accotunPeriod) {
		AccountingPeriodCard card = new AccountingPeriodCard(opt, accotunPeriod);
		container.addCard( card );
		return card;
	}
	
	protected void search(final AccountingPeriodModuleOptions opt) {
		toolbar.hideMessages();
		container.clear();
		SERVICE.getPeriods(opt.getOccam(), new AsyncCallback<LinkedList<AccountPeriod>>() {
			@Override
			public void onSuccess(LinkedList<AccountPeriod> result) {
				if (result != null && !result.isEmpty()) {
					for ( AccountPeriod accountPeriod : result ) {
						addCard(opt, accountPeriod );
					}
				} else {
					Label label = new Label(AON.MSG.noData());
					label.setStyleName(AON.CSS.aonBlockMessage());
					label.addStyleName(AON.CSS.aonBlockInfoMessage());
					label.addStyleName(AON.CSS.aonMarginTop());
					container.add(label);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}
	
	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}

	private class AccountingPeriodCard extends AonCard {

		private AonTextBox title = new AonTextBox();
		
		private AccountingPeriodCard(final AccountingPeriodModuleOptions opt, AccountPeriod accountPeriod) {
			addStyleName("aon-accounting-period-card");
			
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setValue(accountPeriod.getName());
			title.addValueChangeHandler(event -> accountPeriod.setName(title.getValue()));
			title.addStyleName("aon-accounting-period-title-input");
			
			this.setTitle(title);
			
			FlowPanel body = new FlowPanel();
			
			FlowPanel initiationPanel = new FlowPanel();
			InlineLabel initiationLabel = new InlineLabel(AON.MSG.initiationDate());
			initiationLabel.setStyleName(AON.CSS.aonTableLabel());
			AonDateBox initiationBox = new AonDateBox();
			initiationBox.addStyleName(AON.CSS.aonBorderNone());
			initiationBox.addStyleName(AON.CSS.aonMarginLeft());
			initiationBox.setValue(accountPeriod.getInitiationDate());
			initiationBox.addValueChangeHandler(event -> accountPeriod.setInitiationDate(initiationBox.getValue()));
			initiationPanel.add(initiationLabel);
			initiationPanel.add(initiationBox);
			
			FlowPanel deadlinePanel = new FlowPanel();
			InlineLabel deadlineLabel = new InlineLabel(AON.MSG.deadline());
			deadlineLabel.setStyleName(AON.CSS.aonTableLabel());
			AonDateBox deadlineBox = new AonDateBox();
			deadlineBox.addStyleName(AON.CSS.aonBorderNone());
			deadlineBox.addStyleName(AON.CSS.aonMarginLeft());
			deadlineBox.setValue(accountPeriod.getDeadline());
			deadlineBox.addValueChangeHandler(event -> accountPeriod.setDeadline(deadlineBox.getValue()));
			deadlinePanel.add(deadlineLabel);
			deadlinePanel.add(deadlineBox);
			
			FlowPanel statusPanel = new FlowPanel();
			InlineLabel statusLabel = new InlineLabel(AON.MSG.status());
			statusLabel.setStyleName(AON.CSS.aonTableLabel());
			ListBox status = new ListBox();
			
			status.addItem("------","");
			status.setStyleName( AON.CSS.aonMarginLeft());
			status.addStyleName( AON.CSS.aonBorderNone());
			int i = 1;
			for (AccountPeriodStatus d : AccountPeriodStatus.values()) {
				status.addItem("   " + AonStringUtils.HYPHEN + " " + d.getDescription(), d.toString());
				i++;
				if (d == accountPeriod.getStatus()) {
					status.setSelectedIndex( i - 1 );
				}
			}
			status.addChangeHandler( e -> {
				int x = status.getSelectedIndex();
				if (x == 0) {
					accountPeriod.setStatus( null );	
				} else {
					x--;
					accountPeriod.setStatus( AccountPeriodStatus.values()[x] );
				}
			});
			statusPanel.add(statusLabel);
			statusPanel.add(status);
			
			body.add(initiationPanel);
			body.add(deadlinePanel);
			body.add(statusPanel);
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler( event -> {
				saveButton.setEnabled(false);
				toolbar.hideMessages();
				SERVICE.save(opt.getOccam(), accountPeriod, new AsyncCallback<AccountPeriod>() {
					
					@Override
					public void onSuccess(AccountPeriod accountPeriod) {
						saveButton.setEnabled(true);
						search(opt);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						saveButton.setEnabled(true);
						toolbar.showErrorMessage(caught.getMessage());
					}
				});
			});

			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.setTabIndex(-2);
			deleteButton.addClickHandler( event -> {
				if ( accountPeriod.getId() == null) {
					search(opt);
				} else {
					deleteButton.setEnabled(false);
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							deleteButton.setEnabled(true);
						}
						
						@Override
						public void onAccept() {
							toolbar.hideMessages();
							SERVICE.delete(opt.getOccam(), accountPeriod, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void voidd) {
									deleteButton.setEnabled(true);
									search(opt);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									deleteButton.setEnabled(true);
									toolbar.showErrorMessage(caught.getMessage());
								}
							});
						}
					});
				}
			});
			getMenuPanel().add(saveButton);
			getMenuPanel().add(deleteButton);
			getMenuPanel().addStyleName("aon-accounting-period-buttons");
		}
		
		@Override
		public void setFocus(boolean focused) {
			title.setFocus(focused);
		}
	}
}		
