package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.Date;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountingPeriodModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	private static AccountingPeriodServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private AonCards container;
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private ScrollPanel extraInfoContainer;
	
	private AonToolbar toolbar;
	private AonToolbarButton addButton;
	private AonToolbarButton searchButton;

	private boolean minimizedByUser;
	private int extraInfoTabIndex;
	
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

		AccountingPeriodServiceAsync serviceRaw = GWT.create(AccountingPeriodService.class);
		SERVICE = new AccountingPeriodServiceAsyncDecorator(serviceRaw);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
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
		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		centerLayoutPanel = new SimpleLayoutPanel();
		container = new AonCards();;
		centerLayoutPanel.setWidget(container );
		splitLayoutPanel.add(centerLayoutPanel);
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	search(opt);		
	        }
	    });		
	}

	private Widget getToolbarPanel(final AccountingPeriodModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.accountingPeriods());

		addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AonCard card = addCard(opt, new AccountPeriod().setDomain(opt.getDomain()));
				card.setFocus(true);
			}
		});
		toolbar.add(addButton);

		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		toolbar.add(searchButton);

		return toolbar;
	}
	
	private AccountingPeriodCard addCard(final AccountingPeriodModuleOptions opt, AccountPeriod accotunPeriod) {
		AccountingPeriodCard card = new AccountingPeriodCard(opt, accotunPeriod);
		container.addCard( card );
		return card;
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(new MinimizeHandler() {
			
			@Override
			public void onMinimize(MinimizeEvent event) {
				minimizedByUser = true;
				closeFootPanel();
			}
		});
		footPanel.addMaximizeHandler(new MaximizeHandler() {
			
			@Override
			public void onMaximize(MaximizeEvent event) {
				openFootPanel();
			}
		});
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		footPanel.add(tabLayout);
		int tabIndex = 0;
		
		extraInfoContainer = new ScrollPanel();
		tabLayout.add(extraInfoContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.additionalData(), AON.CSS.aonIconInfo()));
		extraInfoTabIndex = tabIndex;
		tabIndex++;


		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				minimizedByUser = false;
				openFootPanelIfNeeded();
			}
		});
		return footPanel; 
	}

	protected void search(final AccountingPeriodModuleOptions opt) {
		toolbar.hideMessages();
		container.clear();
		SERVICE.getPeriods(opt.getDomainName(),opt.getDomain(),opt.getUser()
				, new AsyncCallback<LinkedList<AccountPeriod>>() {
					
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
	
	public void addExtraInfo( String htmlText) {
		HTMLPanel panel = new HTMLPanel(htmlText);
		addExtraInfo(panel);
	}
	
	public void addExtraInfo( Widget widget) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(extraInfoTabIndex);
		extraInfoContainer.setWidget(widget);
		extraInfoContainer.scrollToTop();
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanelIfNeeded() {
		if (!minimizedByUser && splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void openFootPanel() {
		int effectiveHeigth = 5;
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}
	
	private class AccountingPeriodCard extends AonCard {

		private AonTextBox title = new AonTextBox();
		
		private AccountingPeriodCard(final AccountingPeriodModuleOptions opt, AccountPeriod accountPeriod) {
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setValue(accountPeriod.getName());
			title.addValueChangeHandler(new ValueChangeHandler<String>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					accountPeriod.setName(title.getValue());
				}
			});
			
			this.setTitle(title);
			
			FlowPanel body = new FlowPanel();
			
			FlowPanel initiationPanel = new FlowPanel();
			InlineLabel initiationLabel = new InlineLabel(AON.MSG.initiationDate());
			initiationLabel.setStyleName(AON.CSS.aonTableLabel());
			AonDateBox initiationBox = new AonDateBox();
			initiationBox.addStyleName(AON.CSS.aonBorderNone());
			initiationBox.addStyleName(AON.CSS.aonMarginLeft());
			initiationBox.setValue(accountPeriod.getInitiationDate());
			initiationBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					accountPeriod.setInitiationDate(initiationBox.getValue());
				}
			});
			initiationPanel.add(initiationLabel);
			initiationPanel.add(initiationBox);
			
			FlowPanel deadlinePanel = new FlowPanel();
			InlineLabel deadlineLabel = new InlineLabel(AON.MSG.deadline());
			deadlineLabel.setStyleName(AON.CSS.aonTableLabel());
			AonDateBox deadlineBox = new AonDateBox();
			deadlineBox.addStyleName(AON.CSS.aonBorderNone());
			deadlineBox.addStyleName(AON.CSS.aonMarginLeft());
			deadlineBox.setValue(accountPeriod.getDeadline());
			deadlineBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					accountPeriod.setDeadline(deadlineBox.getValue());
				}
			});
			deadlinePanel.add(deadlineLabel);
			deadlinePanel.add(deadlineBox);
			
			FlowPanel statusPanel = new FlowPanel();
			InlineLabel statusLabel = new InlineLabel(AON.MSG.status());
			statusLabel.setStyleName(AON.CSS.aonTableLabel());
			InlineLabel statusValue = new InlineLabel(accountPeriod.getStatus()==null?"---":accountPeriod.getStatus().getDescription());
			statusValue.addStyleName(AON.CSS.aonMarginLeft());
			statusPanel.add(statusLabel);
			statusPanel.add(statusValue);
			
			body.add(initiationPanel);
			body.add(deadlinePanel);
			body.add(statusPanel);
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					saveButton.setEnabled(false);
					toolbar.hideMessages();
					SERVICE.save(opt.getDomainName(), opt.getDomain(), opt.getUser(), accountPeriod, new AsyncCallback<AccountPeriod>() {
						
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
				}
			});

			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.setTabIndex(-2);
			deleteButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
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
								SERVICE.delete(opt.getDomainName(), opt.getDomain(), opt.getUser(), accountPeriod, new AsyncCallback<Void>() {
									
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
				}
			});
			getMenuPanel().add(saveButton);
			getMenuPanel().add(deleteButton);
		}
		
		@Override
		public void setFocus(boolean focused) {
			title.setFocus(focused);
		}
	}
}		
