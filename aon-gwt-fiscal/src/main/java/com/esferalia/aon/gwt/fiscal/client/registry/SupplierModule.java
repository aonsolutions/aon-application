package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSupplierFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRegistryStatusVisitor;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SupplierModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	private static final String FINANCE_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/FinanceReportExcelPrint";
	
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static class SupplierRow {
		private int row;
		private Supplier supplier;
		
		private SupplierRow( int row, Supplier supplier) {
			this.row = row;
			this.supplier = supplier;
		}
		private int getRow() {
			return row;
		}
		private Supplier getSupplier() {
			return supplier;
		}
	}
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	private int autoWidth; 
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private ScrollPanel extraInfoContainer;
	
	private LinkedHashMap<Integer,SupplierRow> suppliers = new LinkedHashMap<Integer,SupplierRow>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<Integer>();
	
	private SupplierModuleSearchPanel searchPanel;
	private AonToolbar toolbar;
	private AonToolbarButton addButton;
	private AonToolbarButton searchButton;
	private AonToolbarButton exportButton;
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private InlineLabel selectedCount;
	private boolean minimizedByUser;
	private int extraInfoTabIndex;
	
	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RegistryModuleOptions options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final RegistryModuleOptions opt ) {
		AON.ensureInjected();

		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);

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
	
	private void loadModule( final RegistryModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		searchPanel = new SupplierModuleSearchPanel(opt);
		dockLayoutPanel.addNorth(searchPanel, RegistryModuleSearchPanel.HEIGHT);
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
						search(opt, searchPanel.getParams( opt ),offset.getValue());
					}
				}
			}
		});
		searchPanel.addValueChangeHandler( new ValueChangeHandler<RegistryParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<RegistryParams> event) {
				RegistryParams params = event.getValue();
				search( opt, params );
			}
		});
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	search(opt, searchPanel.getParams( opt ));		
	        }
	    });		
	}

	private static enum COLS {
		  CHK(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
	    , STA(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, SEC(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, DCT("Tipo"				, 50 ,AON.CSS.aonTextLeft())
		, DCC("Pais"				, 40 ,AON.CSS.aonTextLeft())
		, DOC("N\u00BA Documento"		, 130,AON.CSS.aonTextLeft())
		, AUTO(AON.MSG.name()			, 0  ,AON.CSS.aonTextLeft())
		, ALS(AON.MSG.alias()			, 200,AON.CSS.aonTextLeft())
	    , ACT(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
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
		
		autoWidth = container.getOffsetWidth() - 80;
		for ( COLS col : COLS.values()) {
			if (col != COLS.AUTO ) {
				autoWidth -= (col.getColWidth() + 2); 
			}
		}
		
		selectedCount = new InlineLabel();
		AonDisplayGridHeaderRow headerRow = tab.addHeaderRow();
		for ( COLS col : COLS.values()) {
			Label label = col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() );
			if (col == COLS.AUTO ) {
				label.setWidth(autoWidth + "px");
			} else {
				label.setWidth(col.getColWidth()  + "px");
			}
			headerRow.addCell(label,col.getCellStyleClass());
		}
		return tab;
	}

	private Widget getToolbarPanel(final RegistryModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.supplier());

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
				final AonSimpleDialog dialog = new AonSimpleDialog();
				dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
				dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
				dialog.setCaption(AON.MSG.creditor());
				AonSupplierFullPanel creditorPanel = new AonSupplierFullPanel(opt, SupplierFull.initialize(opt.getDomain()), new AonRegistryFullPanelCallback<SupplierFull>() {
					
					@Override
					public void setFocus(boolean b) {
						// callback.setFocus(b);
					}
					
					@Override
					public void onError(Throwable caught) {
						showError(caught.getMessage());
					};
					
					@Override
					public void onCancel() {
						dialog.hide();		
					}
					
					@Override
					public void onAccept(SupplierFull rf) {
						dialog.hide();
					}

					@Override
					public void onDocumenthanged(SupplierFull registryFull) {
					}
				});
				dialog.add( creditorPanel );
				dialog.center();
				dialog.show();
				
				Scheduler.get().scheduleDeferred(new Command() {
			        public void execute() {
			        	creditorPanel.setFocus(true);
			        }
			    });		
				
			}
		});
		toolbar.add(addButton);

		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				enableMoreData();
				container.clear();
				tab = getTable();
				container.add(tab);
				offset.setValue(0);
				search(opt, searchPanel.getParams( opt ), offset.getValue());
			}
		});
		toolbar.add(searchButton);

		exportButton = new AonToolbarButton( AON.MSG.export(), AON.CSS.aonIconExcel() );
		exportButton.setEnabled(false);
		exportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + FINANCE_REPORT_EXCEL_PRINT);
				registryParamsHidden.setValue(JsonParams.convert(searchPanel.getParams( opt )));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
//		toolbar.add(exportButton);

		checkAll  = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(false);
		checkAll .addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, true );
			}
		});
		toolbar.add(checkAll );
		
		uncheckAll  = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(false);
		uncheckAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, false );
			}
		});
		toolbar.add(uncheckAll);

		return toolbar;
	}
	
	protected void checkAll(final RegistryModuleOptions opt, boolean check) {
		for (SupplierRow supplierRow : suppliers.values()) {
			supplierRow.getSupplier().setSelected(check);
			manageSelection(supplierRow.getSupplier());
			AonDisplayGridRow row = (AonDisplayGridRow) tab.getWidget(supplierRow.getRow());
			AonDisplayGridCell cell = (AonDisplayGridCell) row.getWidget(0);
			Widget w = cell.getWidget(0);
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}
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

	protected void search(final RegistryModuleOptions opt,RegistryParams params) {
		enableMoreData();
		suppliers.clear();
		clearSelection();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, params, offset.getValue());
	}

	private void search(final RegistryModuleOptions opt, RegistryParams params, final int ofs) {
		if (!isMoreData()) return; 
		SERVICE.getSuppliers(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, limit
				, new AsyncCallback<LinkedList<Supplier>>() {
					
					@Override
					public void onSuccess(LinkedList<Supplier> result) {
						checkAll.setEnabled(false);
						uncheckAll.setEnabled(false);
						exportButton.setEnabled(false);
						if (result != null && !result.isEmpty()) {
							result.forEach( supplier -> paintRow(opt,supplier));
							offset.setValue(ofs + result.size());
							enableMoreData();
							checkAll.setEnabled(true);
							uncheckAll.setEnabled(true);
							exportButton.setEnabled(true);
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

	private void paintRow(final RegistryModuleOptions opt, Supplier supplier) {
		int row = tab.getWidgetCount();
		suppliers.put(supplier.getId(), new SupplierRow(row, supplier));
		paintRow(opt, supplier, row);
	}
	
	private void paintRow(final RegistryModuleOptions opt, Supplier supplier, int row) {
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction()
				, selectedItems.contains(supplier.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		checkButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedItems.contains(supplier.getId())) {
					supplier.setSelected(false);
					manageSelection( supplier );
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					supplier.setSelected(true);
					manageSelection( supplier );
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}
			}
		});

		Label confidential = new Label();
		if (supplier.isConfidential()) {
			confidential.setTitle(AON.MSG.confidential());
			confidential.setStyleName(AON.CSS.aonIconLabel());
			confidential.addStyleName(AON.CSS.aonIconConfidential());
		}

		Label documentTypeLabel  = new Label( supplier.getDocumentType() == null ? AonStringUtils.EMPTY : supplier.getDocumentType().getDescription());
		Label documentCountryLabel  = new Label( supplier.getDocumentCountry() == null ? AonStringUtils.EMPTY : supplier.getDocumentCountry().getIso2());
		Label documentLabel  = new Label( supplier.getDocument() );
		Label nameLabel  = new Label( supplier.getName() );
		Label aliasLabel  = new Label( supplier.getAlias() );
		
		Label status = new Label();
		status.setTitle(supplier.getStatus() == null ?"":supplier.getStatus().getDescription());
		status.setStyleName(AON.CSS.aonIconLabel());
		supplier.getStatus().visit( new IRegistryStatusVisitor() {
			@Override
			public void visitActive() {
				status.addStyleName(AON.CSS.aonIconValid());
			}
			
			@Override
			public void visitInactive() {
				status.addStyleName(AON.CSS.aonIconInvalid());
			}
			
			@Override
			public void visitBlocked() {
				status.addStyleName(AON.CSS.aonIconBlock());
			}
		});

		AonTableButton detailsButton = new AonTableButton(AON.MSG.seeDetail(),AON.CSS.aonIconMoreVertical() );
		detailsButton.addStyleName(AON.CSS.aonClickable());
		detailsButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SERVICE.getSupplierFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), supplier.getId(), new AsyncCallback<SupplierFull>() {
					
					@Override
					public void onSuccess(SupplierFull result) {
						final AonSimpleDialog dialog = new AonSimpleDialog();
						dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
						dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
						dialog.setCaption(AON.MSG.supplier());
						AonSupplierFullPanel supplierPanel = new AonSupplierFullPanel(opt, result, new AonRegistryFullPanelCallback<SupplierFull>() {
							
							@Override
							public void setFocus(boolean b) {
								// callback.setFocus(b);
							}
							
							@Override
							public void onError(Throwable caught) {
								showError(caught.getMessage());
							};
							
							@Override
							public void onCancel() {
								dialog.hide();		
							}
							
							@Override
							public void onAccept(SupplierFull rf) {
								dialog.hide();
							}
							@Override
							public void onDocumenthanged(SupplierFull registryFull) {
							}
							
						});
						dialog.add( supplierPanel );
						dialog.center();
						dialog.show();
						
						Scheduler.get().scheduleDeferred(new Command() {
					        public void execute() {
					        	supplierPanel.setFocus(true);
					        }
					    });		
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());	
					}
				});					
			}
		});
		
		tab.addRow().addCell(checkButton)
			.addCell(status)
			.addCell(confidential)
			.addCell(documentTypeLabel)
			.addCell(documentCountryLabel)
			.addCell(documentLabel)
			.addCell(nameLabel)
			.addCell(aliasLabel)
			.addCell(detailsButton);
	}
	

	private void clearSelection() {
		selectedItems.clear();
	}
	private void manageSelection(Supplier supplier) {
		if (supplier.isSelected()) {
			selectedItems.add(supplier.getId());
		} else {
			selectedItems.remove(supplier.getId());
		}
		refreshIcons();
	}
	
	private void refreshIcons() {
		selectedCount.setText( (selectedItems.size() > 0)?  AonNumberUtils.toString(selectedItems.size()) :""); 
	}
}		
