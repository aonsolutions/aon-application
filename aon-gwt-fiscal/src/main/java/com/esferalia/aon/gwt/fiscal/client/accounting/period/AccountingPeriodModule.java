package com.esferalia.aon.gwt.fiscal.client.accounting.period;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
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
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Callback;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
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
	
	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	private static AccountingPeriodServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
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
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		splitLayoutPanel.add(centerLayoutPanel);
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	search(opt);		
	        }
	    });		
	}

	private static enum COLS {
		  CHK (AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, NAME(AON.MSG.name()			, 100,AON.CSS.aonTextCenter())
		, FROM(AON.MSG.initiationDate()	, 120,AON.CSS.aonTextCenter())
		, TO  (AON.MSG.deadline()		, 120,AON.CSS.aonTextCenter())
		, TYP (AON.MSG.status()			, 100,AON.CSS.aonTextCenter())
	    , ACT (AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,int colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,int colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public int getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	protected AonDisplayGrid getTable() {
		tab = new AonDisplayGrid();
		tab.setStyleName(AON.CSS.aonGrid());
		tab.addStyleName(AON.CSS.aonNoPadding());
		tab.addStyleName(AON.CSS.aonBlockCenter());

		AonDisplayGridHeaderRow headerRow = tab.addHeaderRow();
		for ( COLS col : COLS.values()) {
			Label label = new Label( col.getHeaderLabel() );
			label.setWidth(col.getColWidth()  + "px");
			headerRow.addCell(label,col.getCellStyleClass());
		}
		return tab;
	}

	private Widget getToolbarPanel(final AccountingPeriodModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.accountingPeriods());

		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden registryParamsHidden = new Hidden(IRequestParamsNames.REGISTRY_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(registryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				paintRow(opt, new AccountPeriod().setDomain(opt.getDomain()));
			}
		});
		toolbar.add(addButton);

		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				tab = getTable();
				container.add(tab);
				search(opt);
			}
		});
		toolbar.add(searchButton);

		return toolbar;
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
		container.clear();
		tab = getTable();
		container.add(tab);
		SERVICE.getPeriods(opt.getDomainName(),opt.getDomain(),opt.getUser()
				, new AsyncCallback<LinkedList<AccountPeriod>>() {
					
					@Override
					public void onSuccess(LinkedList<AccountPeriod> result) {
						if (result != null && !result.isEmpty()) {
							result.forEach( accountPeriod -> paintRow(opt,accountPeriod));
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

	private void paintRow(final AccountingPeriodModuleOptions opt, AccountPeriod accountPeriod) {
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		
		FlowPanel buttonContainer = new FlowPanel();

		AonTextBox nameBox = new AonTextBox();
		nameBox.setStyleName(AON.CSS.aonBorderNone());
		nameBox.addStyleName(AON.CSS.aonWidthAll());
		nameBox.setValue(accountPeriod.getName());
		
		AonDateBox initiationBox = new AonDateBox();
		initiationBox.addStyleName(AON.CSS.aonBorderNone());
		initiationBox.addStyleName(AON.CSS.aonWidthAll());
		initiationBox.setValue(accountPeriod.getInitiationDate());
		
		AonDateBox deadlineBox = new AonDateBox();
		deadlineBox.addStyleName(AON.CSS.aonBorderNone());
		deadlineBox.addStyleName(AON.CSS.aonWidthAll());
		deadlineBox.setValue(accountPeriod.getDeadline());
		
		Label statusLabel = new Label(accountPeriod.getStatus() == null ? "" :accountPeriod.getStatus().getDescription());
		
		Callback<AccountPeriod, Throwable> callback = new Callback<AccountPeriod, Throwable>() {

			@Override
			public void onFailure(Throwable reason) {
				toolbar.showErrorMessage(reason.getMessage());
			}

			@Override
			public void onSuccess(AccountPeriod result) {
				nameBox.setValue(nameBox.getValue());
				initiationBox.setValue(initiationBox.getValue());
				deadlineBox.setValue(deadlineBox.getValue());
				statusLabel.setText( accountPeriod.getStatus() == null ? "" :accountPeriod.getStatus().getDescription() );
			}
		};
		
		nameBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				accountPeriod.setName(nameBox.getValue());
				accountPeriod.setInitiationDate(initiationBox.getValue());
				accountPeriod.setDeadline(deadlineBox.getValue());
				save(opt,accountPeriod,msg, callback);
			}
		});
		initiationBox.addValueChangeHandler( new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				accountPeriod.setName(nameBox.getValue());
				accountPeriod.setInitiationDate(initiationBox.getValue());
				accountPeriod.setDeadline(deadlineBox.getValue());
				save(opt,accountPeriod,msg, callback);
			}
		});
		deadlineBox.addValueChangeHandler( new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				accountPeriod.setName(nameBox.getValue());
				accountPeriod.setInitiationDate(initiationBox.getValue());
				accountPeriod.setDeadline(deadlineBox.getValue());
				save(opt,accountPeriod,msg, callback);
			}
		});
		
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteButton.setEnabled(false);
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}
					
					@Override
					public void onAccept() {
						SERVICE.delete(opt.getDomainName(), opt.getDomain(), opt.getUser(), accountPeriod, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void voidd) {
								search(opt);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								toolbar.showErrorMessage(caught.getMessage());
							}
						});
					}
				});
				
			}
		});
		buttonContainer.add(deleteButton);
		
		tab.addRow().addCell(msg)
			.addCell(nameBox)
			.addCell(initiationBox)
			.addCell(deadlineBox)
			.addCell(statusLabel)
			.addCell(buttonContainer);
	}

	private void save(AccountingPeriodModuleOptions opt, AccountPeriod accountPeriod, Label msg, Callback<AccountPeriod, Throwable> callback) {
		SERVICE.save(opt.getDomainName(), opt.getDomain(), opt.getUser(), accountPeriod, new AsyncCallback<AccountPeriod>() {
			
			@Override
			public void onSuccess(AccountPeriod result) {
				accountPeriod.setId( result.getId());
				accountPeriod.setName( result.getName());
				accountPeriod.setInitiationDate( result.getInitiationDate());
				accountPeriod.setDeadline(result.getDeadline());
				accountPeriod.setStatus( result.getStatus());
				msg.addStyleName(AON.CSS.aonIconValid());
				new Timer() {
					@Override
					public void run() {
						msg.removeStyleName(AON.CSS.aonIconValid());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	
}		
