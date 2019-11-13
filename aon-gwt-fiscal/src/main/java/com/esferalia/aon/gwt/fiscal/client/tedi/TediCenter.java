package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CloseTab;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

import es.translogia.tedi.ewok.TediInvoiceStatus;
import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class TediCenter extends MainEntryPoint {

	public static native boolean isTediSnapshot()
	/*-{
		return $wnd.isTediSnapshot();
	}-*/;
    
    private static class TediResultProvidesKey implements ProvidesKey<TediResult> {
		@Override
		public Object getKey(TediResult result) {
			return (result != null && result.getTedi() != null) ? result.getTedi().getUuid() : null;
		}
	}
    
    private static class CompanyProvidesKey implements ProvidesKey<TediCompanyResult> {
		@Override
		public Object getKey(TediCompanyResult result) {
			return (result != null) ? result.getCompany().getDocument() : null;
		}
	}

	private static TediServiceAsync SERVICE;
	
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	private HashMap<String,Widget> tabs = new HashMap<String,Widget>();

	private AonConfiguration configuration;
;
	private TabLayoutPanel mainTabLayoutPanel;

	private FlowPanel toolbarPanel;
	private Button acceptAll;
	private	Viewer pdfViewer = new Viewer();	
	
	private LinkedList<TediResult> resultList;
	private TediInvoiceTable table = new TediInvoiceTable(new TediResultProvidesKey());
	
	private LinkedList<TediCompanyResult> companyList;
	private Integer start = 0; 
	private TediCompanyTable companyTable;
	
	public TediCenter(String domainName, int domain, String user) {
		currentDomainName = domainName;
		currentDomain = domain;
		currentUser = user;
	}

	private int getDomain() {
		return currentDomain;
	}

	private String getUser() {
		return currentUser;
	}

	private String getDomainName() {
		return currentDomainName;
	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		
		SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();
		mainPanel.setStyleName(AON.AON_CSS.aonSelector());
		mainTabLayoutPanel = new TabLayoutPanel(30,Unit.PX);
		mainPanel.add(mainTabLayoutPanel);
		root.add(mainPanel);

		SERVICE.getAonConfiguration(getDomainName(), getUser(), getDomain(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				configuration = result;
				
				if(configuration.getChildDomains().size() > 0) {
					companyListPanel();
				} else {
					companyInvoicePanel(configuration.getCompany());
				}			
			}

			@Override
			public void onFailure(Throwable caught) {
				showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
			}
		});
	}
	
	private void companyListPanel() {
		companyTable = new TediCompanyTable(new CompanyProvidesKey());
		start = 0;
		DockLayoutPanel dockLayoutPanel2 = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel2.addStyleName(AON.AON_CSS.aonMarginTop());
		dockLayoutPanel2.addNorth(getToolbarPanel(false, ""), 25);
		SimpleLayoutPanel sidebarContent2 = new SimpleLayoutPanel();
		dockLayoutPanel2.add(sidebarContent2);
		mainTabLayoutPanel.add(dockLayoutPanel2,new CloseTab(AON.MSG.companyList(), false));
		ScrollPanel tableScrollPanel2 = new ScrollPanel();
		tableScrollPanel2.setStyleName(AON.AON_CSS.aonScrollArea());
		sidebarContent2.add(tableScrollPanel2);
		tableScrollPanel2.add(companyTable);
		
		
		tableScrollPanel2.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = tableScrollPanel2.getElement().getScrollTop();
				Integer offsetHeight = tableScrollPanel2.getElement().getOffsetHeight();
				Integer physicalSize = tableScrollPanel2.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;

				if(scrollTop >= maxScrollPosition && start < companyList.size()){
					Integer s = start;
					Integer e = start + 50;
					companyTable.setRowData(companyList.subList(0, companyList.size() < e ? companyList.size() : e ));
					start = companyList.size() < e ? companyList.size() : e;
					getCountInbox(s);
				}
			}
		}, ScrollEvent.getType());
		
		
		companyTable.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				TediCompanyResult company = companyTable.getSelected();
				onSelect( company.getCompany() );
			}
		});
		SERVICE.getCompanies(getDomainName(), getUser(), getDomain(), isTediSnapshot(),
			new AsyncCallback<LinkedList<TediCompanyResult>>() {
				@Override
				public void onSuccess(LinkedList<TediCompanyResult> results) {
					companyList = results;
					
					if (companyList == null) {
						companyList  = new LinkedList<TediCompanyResult>();
					}
					companyTable.setRowData(companyList.subList(start, companyList.size() < 50 ? companyList.size() : 50 ));
					start = companyList.size() < 50 ? companyList.size() : 50;
					getCountInbox(0);
				}

				@Override
				public void onFailure(Throwable caught) {
					showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
				}

			});
	}
	
	private void getCountInbox(Integer index) {
		if(index < start) {
			SERVICE.getCountInboxInvoices(getDomainName(), getUser(), getDomain(), isTediSnapshot(), companyList.get(index).getCompany(), new AsyncCallback<Integer>() {
			
				@Override
				public void onSuccess(Integer result) {
					companyList.get(index).setInboxCount(result);
					companyTable.setRowData(index, companyList.subList(index, index + 1));
					getCountInbox(index + 1);
				}
			
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		}
	}
	
	
	private void companyInvoicePanel(Company company) {
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		dockLayoutPanel.addNorth(getToolbarPanel(true, company.getName()), 25);
		SimpleLayoutPanel sidebarContent = new SimpleLayoutPanel();
		dockLayoutPanel.add(sidebarContent);
		mainTabLayoutPanel.add(dockLayoutPanel,new CloseTab(AON.MSG.invoiceList(), false));
		ScrollPanel tableScrollPanel = new ScrollPanel();
		tableScrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		sidebarContent.add(tableScrollPanel);
		tableScrollPanel.add(table);
	
		table.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				TediResult inv = table.getSelected();
				onSelect( inv );
			}
		});
		table.addRangeChangeHandler( new RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				SERVICE.getVerifiedInvoices(getDomainName(), getUser(), getDomain(), isTediSnapshot(), company,
					new AsyncCallback<LinkedList<TediResult>>() {

						@Override
						public void onSuccess(LinkedList<TediResult> results) {
							resultList = results;
							if (resultList == null) {
								resultList  = new LinkedList<TediResult>();
							}
							table.setRowData(resultList);
						}

						@Override
						public void onFailure(Throwable caught) {
							showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
						}

					});
			}
		});
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	private void onSelect(Company company) {
		for(Integer i =  mainTabLayoutPanel.getWidgetCount() - 1;  i > 0; i--) {
			mainTabLayoutPanel.remove(i);
		}
		table = new TediInvoiceTable(new TediResultProvidesKey());
		companyInvoicePanel(company);
		mainTabLayoutPanel.selectTab(1);
	}
	
	private void onSelect(TediResult result) {
		onSelect(result,false);
	}
	private void onSelect(TediResult result, boolean forceRefresh) {
		SplitLayoutPanel tediSplitPanel;
		boolean paint = false;
		if (tabs.containsKey(result.getTedi().getUuid())) {
			tediSplitPanel = (SplitLayoutPanel) tabs.get( result.getTedi().getUuid() );
			if (forceRefresh) {
				tediSplitPanel.clear();
				paint = true;
			}
			mainTabLayoutPanel.selectTab( tediSplitPanel );	
		} else {
			paint = true;
			tediSplitPanel = new SplitLayoutPanel(10);
			String tabLabel = AonStringUtils. trim(AonStringUtils.abbreviate(
					(result.getInvoice().getType() != null ? result.getInvoice().getType().getDescription() : " ")
					+ " " + AonStringUtils.defaultString(result.getTedi().getRname()) , 25));
			CloseTab closeTab = new CloseTab(tabLabel, true);
			closeTab.addCloseHandler(new CloseHandler<Integer>() {
				@Override
				public void onClose(CloseEvent<Integer> event) {
					mainTabLayoutPanel.remove(tediSplitPanel);
					tabs.remove( result.getTedi().getUuid());
				}
			});
			mainTabLayoutPanel.add(tediSplitPanel,closeTab);
			tabs.put( result.getTedi().getUuid(),tediSplitPanel);
			mainTabLayoutPanel.selectTab(mainTabLayoutPanel.getWidgetCount() - 1);
		}
		if (paint) {
			if (result.hasPDFAttach() || result.hasImageAttach()) {
				SimpleLayoutPanel attachContainer = new SimpleLayoutPanel();
				tediSplitPanel.addEast(attachContainer, 350);
				tediSplitPanel.setWidgetToggleDisplayAllowed(attachContainer, true);
				paintAttach( result , attachContainer);
			}
			SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
			tediSplitPanel.add(contentPanel);
			if (result.isImportable()) {
				paintInvoice(contentPanel,result);
			} else if (result.getInvoice().isUndeductible() 
				&& result.getInvoice().getRegistry() == null
				&& result.getInvoice().getIssueDate() != null
				&& (result.getInvoice().getDetails() == null || result.getInvoice().getDetails().size() == 0)) {
				paintInvoice(contentPanel,result);
			} else {
				paintProblemsWidget(contentPanel, result );
			}
		}
	}
	
	protected void showMessage(String msg) {
		MessageDialog.error(msg);
	}

	private Widget getToolbarPanel(Boolean isInvoicePanel, String title) {
		toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label("tEDI center" + (isInvoicePanel ? " - " + title : "")));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());

		final Button clean = new Button();
		clean.setVisible(isInvoicePanel);
		clean.setText(AON.MSG.refresh());
		clean.setTitle(AON.MSG.refresh());
		clean.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		clean.addStyleName(AON.AON_CSS.aonIconRefresh());
		clean.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onRefresh( );
			}
		});
		buttonContainer.add(clean);

		final Button sync = new Button();
		sync.setVisible(!isInvoicePanel);
		sync.setText("Sincronizar");
		sync.setTitle("Sincronizar");
		sync.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		sync.addStyleName(AON.AON_CSS.aonIconRefresh());
		sync.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onSync( );
			}
		});
		buttonContainer.add(sync);
		
		acceptAll = new Button();
		acceptAll.setVisible(false);
		acceptAll.setText(AON.MSG.accept());
		acceptAll.setTitle(AON.MSG.accept());
		acceptAll.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		acceptAll.addStyleName(AON.AON_CSS.aonIconRefresh());
		acceptAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onAcceptAll();
			}
		});
		buttonContainer.add(acceptAll);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	private Widget getAttachToolbarPanel(final String url) {
		toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(AON.MSG.attach()));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());
		
		Anchor download = new Anchor(AON.MSG.download(),url,"_blank");
		download.setTitle(AON.MSG.download());
		download.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		download.addStyleName(AON.AON_CSS.aonIconNewWindow());
		buttonContainer.add(download);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
 
	private void onRefresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	private void onSync() {
		SERVICE.tediSync(getDomainName(), getUser(), getDomain(), isTediSnapshot(), new AsyncCallback<LinkedList<TediCompanyResult>>() {
			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(LinkedList<TediCompanyResult> results) {
				companyList = results;
				
				if (companyList == null) {
					companyList  = new LinkedList<TediCompanyResult>();
				}
			
				companyTable.setRowData(companyList.subList(start, companyList.size() < 50 ? companyList.size() : 50 ));
				start = companyList.size() < 50 ? companyList.size() : 50;
				getCountInbox(0);
			}
		});
	}
	
	
	protected void onAcceptAll() {
		LinkedList<TediResult> accepted = new LinkedList<TediResult>();
		for (TediResult result : resultList) {
			if (result.isChecked()) {
				accepted.add(result);
			}
		}
		onAcceptAll(accepted);
	}

	private void onAcceptAll(LinkedList<TediResult> accepted) {
		SERVICE.putInvoices(getDomainName(), getUser(), getDomain(),isTediSnapshot(), accepted,
				new AsyncCallback<LinkedList<TediResult>>() {

					@Override
					public void onSuccess(LinkedList<TediResult> list) {
						if (list != null) {
							for (TediResult result : list ) {
								SplitLayoutPanel tabPanel = (SplitLayoutPanel) tabs.get(result.getTedi().getUuid() );
								tabs.remove( result.getTedi().getUuid());
								mainTabLayoutPanel.remove(tabPanel);
							}
						}
						onRefresh();
					}

					@Override
					public void onFailure(Throwable caught) {
						showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}

				});
	}

	protected void onAccept(TediResult result) {
		result.getTedi().setOldStatus(result.getTedi().getStatus());
		result.getTedi().setStatus(TediInvoiceStatus.accepted);
		SERVICE.putInvoice(getDomainName(), getUser(), getDomain(), isTediSnapshot(),result.getTedi(), new AsyncCallback<TediResult>() {

			@Override
			public void onSuccess(TediResult result) {
				SimpleLayoutPanel tabPanel = (SimpleLayoutPanel) tabs.get(result.getTedi().getUuid() );
				tabs.remove( result.getTedi().getUuid());
				mainTabLayoutPanel.remove(tabPanel);
				onRefresh( );
			}

			@Override
			public void onFailure(Throwable caught) {
				showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
			}

		});
	}

	private String getBackgroundColor(TediLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#e6ffe6";
		} else if (curLevel == TediLevel.INF) {
			color = "#e7f5fe";
		} else if (curLevel == TediLevel.WRN) {
			color = "#ffbf80";
		} else if (curLevel == TediLevel.ERR) {
			color = "#ffc2b3";
		}
		return color;
	}

	private void paintInvoice(SimplePanel container, TediResult result) {
		if (result.isImportable()) {
			paintAccountEntryModule(container, result );
		}
		
		if (!result.isImportable()) {
			if (result.getInvoice().isUndeductible() 
			&& result.getInvoice().getRegistry() == null
			&& result.getInvoice().getIssueDate() != null
			&& (result.getInvoice().getDetails() == null || result.getInvoice().getDetails().size() == 0)) {
				paintAccountEntryModule(container, result );
			}
		}
	};
	
	private void paintAccountEntryModule(SimplePanel container, TediResult result) {	
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad( new AccountEntryModuleOptions()
			.setParentWidget(container)
			.setDomainName( getCurrentDomainName() )
			.setUser(getCurrentUser())
			.setDomain( getCurrentDomain())
			.setConfiguration( configuration)
			.setAccountingInvoice( ai)
			.setBackButtonVisible(false)
			.setSessionLogTabVisible(false)
			.setPreviewSectionVisible(true)
			.setBalancesSectionVisible(false)
			.setStatementTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback(new ModuleCallback<AccountEntry>() {

					@Override
					public void onRemove(AccountEntry removed) {
					}

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onExit() {
					}

					@Override
					public void onChange(AccountEntry changed) {
						result.setAon(null);
						LinkedList<TediResult> accepted = new LinkedList<TediResult>();
						accepted.add(result);
						onAcceptAll(accepted);
						// onRefresh( );
					}
				})
			);
	}
	
	private void paintAttach(TediResult result, SimpleLayoutPanel container) {
		if (result.hasPDFAttach()) {
			DockLayoutPanel attachSplit = new DockLayoutPanel( Unit.PX );
			container.setWidget(attachSplit);
			ScrollPanel scrollPanel = new ScrollPanel();
			attachSplit.add(scrollPanel);
			
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.add(pdfViewer);
			scrollPanel.setWidget(verticalPanel);
			pdfViewer.addStyleName(AON.AON_CSS.aonWidthAll());
			pdfViewer.addStyleName(AON.AON_CSS.aonHeightAll());
			SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(), isTediSnapshot(),
					result.getTedi().getUuid(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					attachSplit.addNorth(getAttachToolbarPanel(result), 25);
					pdfViewer.setDocument(result, 1.0);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
				}
			});
		}
		if (result.hasImageAttach()) {
			DockLayoutPanel attachSplit = new DockLayoutPanel( Unit.PX );
			container.setWidget(attachSplit);
			ScrollPanel scrollPanel = new ScrollPanel();
			attachSplit.add(scrollPanel);
			Image image = new Image();
			image.setStyleName(AON.AON_CSS.aonWidthAll());
			image.addStyleName(AON.AON_CSS.aonHeightAll());
			scrollPanel.setWidget(image);
			SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(),isTediSnapshot(),
					result.getTedi().getUuid(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					attachSplit.addNorth(getAttachToolbarPanel(result), 25);
					image.setUrl(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
				}
			});
		}
	}

	private void paintProblemsWidget(SimpleLayoutPanel contentPanel, TediResult result) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		scrollPanel.setWidget(mainPanel);
		if (result.getMessages() != null && result.getMessages().size() > 0) {
			mainPanel.setStyleName(AON.AON_CSS.aonMarginTop5());
			mainPanel.addStyleName(AON.AON_CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.AON_CSS.aonSimpleBorder());
			mainPanel.addStyleName(AON.AON_CSS.aonFixedFont());
			for (TediError error : result.getMessages()) {
				FlowPanel flowPanel = new FlowPanel();
				InlineLabel colorLabel = new InlineLabel("");
				colorLabel.setStyleName(AON.AON_CSS.aonPaddingLeft());
				colorLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
				flowPanel.add(colorLabel);
	
				InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
				errLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				errLabel.addStyleName(AON.AON_CSS.aonBold());
				flowPanel.add(errLabel);
	
				InlineLabel msgLabel = new InlineLabel(error.getMessage());
				msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
				msgLabel.addStyleName(AON.AON_CSS.aonBorderBottomImportant());
				flowPanel.add(msgLabel);
				
				if (error.canBeFixed()) {
					SimplePanel container = new SimplePanel();
					container.setStyleName(AON.AON_CSS.aonMarginTop());
					container.addStyleName(AON.AON_CSS.aonMarginBottom());
					flowPanel.add(container);			
					TediContextVisitor tediContextVisitor = new TediContextVisitor(getDomainName(), getDomain(), configuration, container);
					error.getContext().getKey().visit(tediContextVisitor, new ICallback() {

						@Override
						public TediResult getResult() {
							return result;
						}
						@Override
						public AonConfiguration getConfiguration() {
							return configuration;
						}
						
						@Override
						public void onCancel() {

						}

						@Override
						public void onAccept(TediResult result) {
							SERVICE.validateInvoice(getDomainName(), getUser(), getDomain(), isTediSnapshot(), result,
									new AsyncCallback<TediResult>() {

										@Override
										public void onSuccess(TediResult result) {
											onSelect(result, true);
										}

										@Override
										public void onFailure(Throwable caught) {
											showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
										}
									});
						}
					});
				}
				mainPanel.add(flowPanel);
			}
		}
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonSimpleBorder());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMargin());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button rejectButton = new Button("Rechazar factura");
		rejectButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		rejectButton.addStyleName(AON.AON_CSS.aonIconDelete());
		rejectButton.addStyleName(AON.AON_CSS.aonMarginTop());
		rejectButton.addStyleName(AON.AON_CSS.aonMarginBottom());
		rejectButton.setTitle("Rechazar factura");
		rejectButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm("Desea rechazar la factura?", new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
						// Nothing
					}
					
					@Override
					public void onAccept() {
						SERVICE.rejectInvoice(getDomainName(), getUser(), getDomain(),isTediSnapshot(), result.getTedi(),
								new AsyncCallback<TediResult>() {

									@Override
									public void onSuccess(TediResult result ) {
										mainTabLayoutPanel.remove(contentPanel);
										tabs.remove( result.getTedi().getUuid());
										onRefresh();
									}

									@Override
									public void onFailure(Throwable caught) {
										showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
									}

								});
					}
				});
			}
		});
		buttonsPanel.add(rejectButton);
		mainPanel.add(buttonsPanel);
		contentPanel.setWidget( scrollPanel );
	}
	
}