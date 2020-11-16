package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog.AonMessageDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import es.translogia.tedi.ewok.TediRegistry;
import net.aonsolutions.gwt.pdfjs.client.FullViewer;
import net.aonsolutions.gwt.pdfjs.client.FullViewer.ViewerDefaultScale;

public class RawdocModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	
	private static RawdocServiceAsync RAWDOC_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private FlexTable tab;
	private int autoWidth; 
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private ScrollPanel extraInfoContainer;
	private SimpleLayoutPanel viewerContainer;
	
	private AonToolbar toolbar;
	private AonToolbarButton searchButton;
	private AonToolbarButton inboxButton;
	private AonToolbarButton rejectedButton;
	private AonToolbarButton draftButton;
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private boolean minimizedByUser;
	private int extraInfoTabIndex;
	private int viewerTabIndex;
	
	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RawdocModuleOptions options = new RawdocModuleOptions()
				.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
			.setParams(new RawdocParams()
					.setDomain(getCurrentDomain())
					.setDomainName(getCurrentDomainName())
					.setStatus(RawdocStatus.INBOX))
		;
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final RawdocModuleOptions opt ) {
		AON.ensureInjected();

		RawdocServiceAsync rawdocServiceRaw = GWT.create(RawdocService.class);
		RAWDOC_SERVICE = new RawdocServiceAsyncDecorator(rawdocServiceRaw);

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
	
	private void loadModule( final RawdocModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		progressContainer.setVisible(false);
		progressContainer.add(progress);
		dockLayoutPanel.addNorth(progressContainer, 5);
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
		centerPanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = centerPanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = centerPanel.getWidget().getOffsetHeight() - centerPanel.getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(opt, opt.getParams(),offset.getValue());
					}
				}
			}

		});
		search(opt);
	}

	private static enum COLS {
		  NAT(AonStringUtils.EMPTY		, 50 ,AON.CSS.aonTextCenter())
		, TYP(AonStringUtils.EMPTY		, 50 ,AON.CSS.aonTextCenter())
		, STA(AON.MSG.status()			, 50 ,AON.CSS.aonTextCenter())
		, INV("N\u00BA Factura"			, 150,AON.CSS.aonTextLeft())
		, IID("F. Fra."					, 75 ,AON.CSS.aonTextCenter())
		, TIT("Titular"					, 100,AON.CSS.aonTextLeft())
		, AUTO(""						, 0  ,AON.CSS.aonTextLeft())
		, AMO("Importe"					, 80 ,AON.CSS.aonTextRight())
		, HST(""						, 20 ,AON.CSS.aonTextLeft())
		, ACT(""						, 100 ,AON.CSS.aonTextLeft())
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

	protected FlexTable getTable() {
		tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonGrid());
		
		autoWidth = container.getOffsetWidth() - 36;
		for ( COLS col : COLS.values()) {
			if (col != COLS.AUTO ) {
				autoWidth -= (col.getColWidth() + 2); 
			}
		}
		for ( COLS col : COLS.values()) {
			if (col == COLS.AUTO ) {
				tab.getColumnFormatter().setWidth(col.ordinal(), autoWidth + "px");
			} else {
				tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
			tab.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
		return tab;
	}

	private Widget getToolbarPanel(final RawdocModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.rawdocModule());

		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search( opt );
			}

		});
		toolbar.add(searchButton);

		inboxButton = new AonToolbarButton( AON.MSG.inbox(), AON.CSS.aonIconInbox() );
		inboxButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search( opt , RawdocStatus.INBOX);
			}
		});
		toolbar.add(inboxButton);

		rejectedButton = new AonToolbarButton( AON.MSG.rejectedDocs(), AON.CSS.aonIconReject() );
		rejectedButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search( opt , RawdocStatus.REJECTED);
			}
		});
		toolbar.add(rejectedButton);

		draftButton = new AonToolbarButton( AON.MSG.draftDocs(), AON.CSS.aonIconDraft() );
		draftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search( opt , RawdocStatus.DRAFT);
			}
		});
		toolbar.add(draftButton);

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
				openFootPanel(5);
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

		viewerContainer = new SimpleLayoutPanel();
		tabLayout.add(viewerContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.documentViewer(), AON.CSS.aonIconViewDocument()));
		viewerTabIndex = tabIndex;
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

	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private void search(RawdocModuleOptions opt, RawdocStatus status) {
		opt.setParams(
			new RawdocParams()
				.setDomain(opt.getDomain())
				.setDomainName(opt.getDomainName())
				.setStatus(status)
		);
		search(opt);
	}

	protected void search(final RawdocModuleOptions opt) {
		enableMoreData();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, opt.getParams(), offset.getValue());
	}

	private void search(final RawdocModuleOptions opt, RawdocParams params, final int ofs) {
		if (!isMoreData()) return; 
		RAWDOC_SERVICE.getRawdocs(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, limit
				, new AsyncCallback<LinkedList<Rawdoc>>() {
					
					@Override
					public void onSuccess(LinkedList<Rawdoc> result) {
						if (result != null && !result.isEmpty()) {
							result.forEach( rawdoc -> paintRow(opt,rawdoc));
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							Label label = new Label(AON.MSG.noData());
							label.setStyleName(AON.CSS.aonBlockMessage());
							label.addStyleName(AON.CSS.aonBlockInfoMessage());
							label.addStyleName(AON.CSS.aonMarginTop());
							container.add(label);
							disableMoreData();
						}
						enableSearch();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
		
	}
	
	public void showViewer( String url ) {
		openFootPanelIfNeeded( 3 );
		tabLayout.selectTab(viewerTabIndex);
		FullViewer viewer = new FullViewer(url, ViewerDefaultScale.PAGE_WIDTH);
		viewerContainer.setWidget(viewer);
	}

	public void addExtraInfo( String htmlText) {
		HTMLPanel panel = new HTMLPanel(htmlText);
		addExtraInfo(panel);
	}
	
	public void addExtraInfo( Widget widget) {
		openFootPanelIfNeeded(5);
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
		openFootPanelIfNeeded(5);
	}
	
	private void openFootPanelIfNeeded( int effectiveHeigth) {
		if (!minimizedByUser && splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel(effectiveHeigth);
		}
	}
	private void openFootPanel(int effectiveHeigth) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}

	private void paintRow(final RawdocModuleOptions opt, Rawdoc rawdoc) {
		int row = tab.getRowCount();
		paintRow(opt, rawdoc, row);
	}
	
	private void paintRow(final RawdocModuleOptions opt, Rawdoc rawdoc, int row) {
		int col = 0;
		
		Label nature = new Label(rawdoc.getNature() == null?"":rawdoc.getNature().getDescription());
		Label type = new Label(rawdoc.getType() == null?"":rawdoc.getType().getDescription());
		Label status = new Label(rawdoc.getStatus() == null?"":rawdoc.getStatus().getDescription());

		AonTableButton logButton = new AonTableButton(AON.MSG.tracking(), AON.CSS.aonIconHistory());
		logButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addExtraInfo( new RawdocLogPanel( rawdoc.getLog()));
			}
		});
		
		FlowPanel actions = new FlowPanel();
		actions.setStyleName(AON.CSS.aonTextCenter());
		AonTableButton accountEntry = new AonTableButton(AON.MSG.acceptInvoice(), AON.CSS.aonIconAccept());
		accountEntry.setVisible(rawdoc.getStatus() == RawdocStatus.INBOX);
		accountEntry.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				RAWDOC_SERVICE.parse(opt.getDomainName(), opt.getDomain(), opt.getUser(), rawdoc.getId() , new AsyncCallback<TediResult>() {

					@Override
					public void onSuccess(TediResult result) {
						CustomPopup entryDialog = new CustomPopup();
						entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
						entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
						entryDialog.setAnimationEnabled(true);
						entryDialog.setGlassEnabled(true);
						entryDialog.setModal(true);
						entryDialog.setCaption(AON.MSG.accountEntries());
						AccountEntryModuleTEDI module = new AccountEntryModuleTEDI();
						module.onModuleLoad(new AccountEntryModuleOptions()
								.setParentWidget(entryDialog)
								.setDomainName(opt.getDomainName())
								.setDomain(opt.getDomain())
								.setUser(opt.getUser())
								.setConfiguration(opt.getConfiguration())
								.setAccountingInvoice(result.getAccountingInvoice())
								.setTediResult(result)
								.setBackButtonVisible(false)
								.setSessionLogTabVisible(false)
								.setPreviewSectionVisible(true)
								.setBalancesSectionVisible(false)
								.setStatementTabVisible(false)
								.setJournalTabVisible(false)
								.setExtraInfoTabVisible(false)
								.setExternalCallback(new ModuleCallback() {

									@Override
									public void onRemove(IAccountEntryWrapper removed) {
										entryDialog.hide();
									}

									@Override
									public void onFailure(Throwable caught) {
										entryDialog.hide();
									}

									@Override
									public void onExit() {
										entryDialog.hide();
									}

									@Override
									public void onChange(IAccountEntryWrapper changed) {
										entryDialog.hide();
										result.setAon((AccountingInvoice) changed);
										actions.clear();
										Label r = new Label(" CONTABILIZADO");
										r.setStyleName(AON.CSS.aonColorGreen());
										r.addStyleName(AON.CSS.aonBold());
										actions.add( r );
									}
								}));
						entryDialog.center();
						entryDialog.show();
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog msg = new AonMessageDialog();
						msg.show("ERROR", "Se ha producido un error al intentar mostrar el documento de la factura.", new AonMessageDialogCallback() {
							@Override
							public void onAccept() {}
						});
					}
				});
			}
		});
		
		
		accountEntry.getElement().getStyle().setMarginRight(5, Unit.PX);
		actions.add(accountEntry);

		AonTableButton delete = new AonTableButton(AON.MSG.draftDocs(), AON.CSS.aonIconDelete());
		delete.setVisible(rawdoc.getStatus() == RawdocStatus.INBOX || rawdoc.getStatus() == RawdocStatus.REJECTED);
		delete.getElement().getStyle().setMarginRight(5, Unit.PX);
		delete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDraftAction(), new AonConfirmDialogCallback() {

					@Override
					public void onCancel() {}

					@Override
					public void onAccept() {
							RAWDOC_SERVICE.toDraft(opt.getDomainName(),opt.getDomain(),opt.getUser(), rawdoc.getId()
									, new AsyncCallback<Void>() {
										
										@Override
										public void onSuccess(Void result) {
											actions.clear();
											Label r = new Label(" PAPELERA");
											r.setStyleName(AON.CSS.aonColorRed());
											r.addStyleName(AON.CSS.aonBold());
											actions.add( r );
										}
										
										@Override
										public void onFailure(Throwable caught) {
											showError(caught.getMessage());
										}
									});
					}
				});
			}
		});
		actions.add(delete);
		
		AonTableButton reject = new AonTableButton(AON.MSG.reject(), AON.CSS.aonIconReject());
		reject.setVisible(rawdoc.getStatus() == RawdocStatus.INBOX);
		reject.getElement().getStyle().setMarginRight(5, Unit.PX);
		reject.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonCustomDialog toast = new AonCustomDialog();
				toast.setCaption(AON.MSG.rejectReason());
				FlowPanel reasonPanel = new FlowPanel();
				reasonPanel.setStyleName(AON.CSS.aonTextCenter());
				reasonPanel.addStyleName(AON.CSS.aonPadding());
				TextArea reason = new TextArea();
				reason.setWidth("400px");
				reason.setHeight("100px");
				reason.addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
							toast.hide();
						}
					}
				});

		    	FlowPanel buttons = new FlowPanel();
		    	buttons.setStyleName(AON.CSS.aonTextCenter());
		    	buttons.addStyleName(AON.CSS.aonMarginTop());
		    	
		    	final Button okButton = new Button();
		    	okButton.setStyleName(AON.CSS.aonOkButton());
		    	okButton.setText( AON.MSG.accept());
		    	okButton.addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
							toast.hide();
						}
					}
				});
		    	okButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if (AonStringUtils.isBlank( reason.getValue() )) {
							AonMessageDialog msg = new AonMessageDialog();
							msg.show("ERROR", "Debe indicar una raz\u00F3n para proceder a rechazar el documento.", new AonMessageDialogCallback() {
								@Override
								public void onAccept() {}
							});
						} else {
							okButton.setEnabled(false);
							toast.hide();
							RAWDOC_SERVICE.toRejected(opt.getDomainName(),opt.getDomain(),opt.getUser(), rawdoc.getId(), reason.getValue()
									, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									actions.clear();
									Label r = new Label(" RECHAZADA");
									r.setStyleName(AON.CSS.aonColorRed());
									r.addStyleName(AON.CSS.aonBold());
									actions.add( r );
								}
								
								@Override
								public void onFailure(Throwable caught) {
									showError(caught.getMessage());
								}
							});
						}
					}
				});
		    	buttons.add(okButton);
		    	
		    	final Button cancelButton = new Button();
		    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
		    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		    	cancelButton.setText( AON.MSG.cancelAction());
		    	cancelButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						cancelButton.setEnabled(false);
						toast.hide();
					}
				});
		    	cancelButton.addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
							toast.hide();
						}
					}
				});
		    	buttons.add(cancelButton);
				
				reasonPanel.add(reason);
				reasonPanel.add(buttons);
				toast.add(reasonPanel);

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						reason.setFocus(true);
					}
				});
				toast.center();
				toast.show();
			}
		});
		actions.add(reject);
		
		AonTableButton restore = new AonTableButton(AON.MSG.restoreAction(),
				rawdoc.getStatus() == RawdocStatus.REJECTED
					?AON.CSS.aonIconRestoreRejected()
					:AON.CSS.aonIconRestoreDeleted());
		restore.setVisible(rawdoc.getStatus() == RawdocStatus.DRAFT || rawdoc.getStatus() == RawdocStatus.REJECTED);
		restore.getElement().getStyle().setMarginRight(5, Unit.PX);
		restore.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm((rawdoc.getStatus() == RawdocStatus.REJECTED
						?AON.MSG.confirmRestoreRejected()
						:AON.MSG.confirmRestoreAction()), new AonConfirmDialogCallback() {

					@Override
					public void onAccept() {
						RAWDOC_SERVICE.toInbox(opt.getDomainName(),opt.getDomain(),opt.getUser(), rawdoc.getId()
								, new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {
										actions.clear();
										Label r = new Label(" INBOX");
										r.setStyleName(AON.CSS.aonColorGreen());
										r.addStyleName(AON.CSS.aonBold());
										actions.add( r );
									}
									
									@Override
									public void onFailure(Throwable caught) {
										showError(caught.getMessage());
									}
								});
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		});
		actions.add(restore);

		AonTableButton deleteForever = new AonTableButton(AON.MSG.deleteForeverAction(),AON.CSS.aonIconDeleteForever());
		deleteForever.setVisible(rawdoc.getStatus() == RawdocStatus.DRAFT);
		deleteForever.getElement().getStyle().setMarginRight(5, Unit.PX);
		deleteForever.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDeleteForever(), new AonConfirmDialogCallback() {

					@Override
					public void onAccept() {
						RAWDOC_SERVICE.delete(opt.getDomainName(),opt.getDomain(),opt.getUser(), rawdoc.getId()
								, new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {
										actions.clear();
										Label r = new Label(" ELIMINADO ");
										r.setStyleName(AON.CSS.aonColorRed());
										r.addStyleName(AON.CSS.aonBold());
										actions.add( r );
									}
									
									@Override
									public void onFailure(Throwable caught) {
										showError(caught.getMessage());
									}
								});
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		});
		actions.add(deleteForever);

		AonTableButton viewDoc = new AonTableButton(AON.MSG.attach(), AON.CSS.aonIconPdf());
		viewDoc.getElement().getStyle().setMarginRight(5, Unit.PX);
		viewDoc.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String params = "domain="+ opt.getDomain() 
					+ "&id=" +  rawdoc.getId();
				params = RawdocModule.b64encode(params);
				String url = URL.encode(GWT.getModuleBaseURL() + "ms/download_rawdoc" 
						+ "/" + opt.getDomainName() 
						+ "/" + opt.getUser() 
						+ "/" +  params);
				showViewer(url);
			}
		});
		
		
		
		actions.add(viewDoc);

		Label invReference = new Label();
		Label invDate = new Label();
		Label regDoc  = new Label();
		Label regName = new Label();
		Label amount =  new Label();
		if (rawdoc.getTediInvoice() != null) {
			invReference.setText(rawdoc.getTediInvoice().getReference());
			Date date = rawdoc.getTediInvoice().getDate();
			invDate.setText(date == null ? "" :AON.DATE_FORMAT.format(rawdoc.getTediInvoice().getDate()));
			TediRegistry registry = rawdoc.getTediInvoice().getRegistry();
			if (registry != null) {
				regDoc  = new Label( registry.getDocument());
				regName = new Label( registry.getName());
			}
			Double total = rawdoc.getTediInvoice().getTotal();
			amount.setText( total == null? "" : AON.FMT.format(rawdoc.getTediInvoice().getTotal()));	
		}

		tab.setWidget(row, col, nature);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextCenter() );
		++col;
		tab.setWidget(row, col, type);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextCenter() );
		++col;
		tab.setWidget(row, col, status);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextCenter() );
		++col;
		tab.setWidget(row, col, invReference);
		++col;
		tab.setWidget(row, col, invDate);
		++col;
		tab.setWidget(row, col, regDoc);
		++col;
		regName.setWidth(autoWidth + "px");
		regName.setStyleName(AON.CSS.aonTruncate());
		tab.setWidget(row, col, regName);
		++col;
		tab.setWidget(row, col, amount);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight() );
		++col;
		if ( AonStringUtils.isBlank(rawdoc.getLog())) {
			Label l = new Label();
			l.setStyleName(AON.CSS.aonIconLabel());
			tab.setWidget(row, col, l);
		} else {
			tab.setWidget(row, col, logButton);
		}
		++col;
		tab.setWidget(row, col, actions);
		++col;
	}

	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;	
	
}		
