package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSettleDateDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FinanceModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	private static final String FINANCE_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/FinanceReportExcelPrint";
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static class FinanceRow {
		private int row;
		private Finance finance;
		private FinanceRow( int row, Finance finance) {
			this.row = row;
			this.finance = finance;
		}
		private int getRow() {
			return row;
		}
		private Finance getFinance() {
			return finance;
		}
	}
	
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
	
	private LinkedHashMap<Integer,FinanceRow> finances = new LinkedHashMap<Integer,FinanceRow>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<Integer>();
	
	private FinanceModuleSearchPanel searchPanel;
	private AonToolbar toolbar;
	private AonToolbarButton searchButton;
	private AonToolbarButton exportButton;
	private AonToolbarButton settleAllButton;
	private AonToolbarButton unSettleAllButton;
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	private AonToolbarButton settleSalariesButton;
	
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
	
	// Vencimiento nominas
	private boolean isPayroll = false;
	
	public void setIsPayroll(boolean isPayroll) {
		this.isPayroll = isPayroll;
	}
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		FinanceModuleOptions options = new FinanceModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final FinanceModuleOptions opt ) {
		AON.ensureInjected();

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

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
	
	private void loadModule( final FinanceModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		searchPanel = new FinanceModuleSearchPanel(opt, this.isPayroll);
		dockLayoutPanel.addNorth(searchPanel, FinanceModuleSearchPanel.HEIGHT);
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
		searchPanel.addValueChangeHandler( new ValueChangeHandler<FinanceParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FinanceParams> event) {
				toolbar.hideMessages();
				FinanceParams params = event.getValue();
				search( opt, params );
			}
		});
		
		// Auto search first time
		if(this.isPayroll) {
			enableMoreData();
			container.clear();
			tab = getTable();
			container.add(tab);
			offset.setValue(0);
			search(opt, searchPanel.getParams( opt ), offset.getValue());
		}
	}

	private static enum COLS {
		  TYP(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, CHK(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, DDT("F. Vto."					, 75 ,AON.CSS.aonTextCenter())
		, DOC("N\u00BA Documento"		, 100,AON.CSS.aonTextLeft())
		, INV("N\u00BA Factura"			, 150,AON.CSS.aonTextLeft())
		, IID("F. Fra."					, 75 ,AON.CSS.aonTextCenter())
		, TIT("CIF/NIF/NIE"				, 100,AON.CSS.aonTextLeft())
		, AUTO("Titular"				, 0  ,AON.CSS.aonTextLeft())
		, PYM("Forma pago"				, 150,AON.CSS.aonTextLeft())
		, AMO("Importe"					, 80 ,AON.CSS.aonTextRight())
		, STA(AON.MSG.status()			, 50 ,AON.CSS.aonTextCenter())
		, ACT(AON.MSG.actions()			, 150,AON.CSS.aonTextCenter())
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
	
	private static enum PAYROLL_COLS {
		  TYP(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, CHK(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, DDT("F. Vto."					, 75 ,AON.CSS.aonTextCenter())
		, DOC("Concepto"				, 100,AON.CSS.aonTextLeft())
		, TIT("CIF/NIF/NIE"				, 100,AON.CSS.aonTextLeft())
		, AUTO("Titular"				, 0  ,AON.CSS.aonTextLeft())
		, PYM("Forma pago"				, 150,AON.CSS.aonTextLeft())
		, AMO("Importe"					, 80 ,AON.CSS.aonTextRight())
		, STA(AON.MSG.status()			, 50 ,AON.CSS.aonTextCenter())
		, ACT(AON.MSG.actions()			, 150,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private PAYROLL_COLS(String headerLabel,int colWidth) {
			this(headerLabel, colWidth, null);
		}

		private PAYROLL_COLS(String headerLabel,int colWidth,String cellStyleClass) {
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
		
		if(isPayroll) {
			for ( PAYROLL_COLS col : PAYROLL_COLS.values()) {
				if (col != PAYROLL_COLS.AUTO ) {
					autoWidth -= (col.getColWidth() + 2); 
				}
			}
		} else {
			for ( COLS col : COLS.values()) {
				if (col != COLS.AUTO ) {
					autoWidth -= (col.getColWidth() + 2); 
				}
			}
		}
		
		
		selectedCount = new InlineLabel();
		
		if(isPayroll) {
			for ( PAYROLL_COLS col : PAYROLL_COLS.values()) {
				if (col == PAYROLL_COLS.AUTO ) {
					tab.getColumnFormatter().setWidth(col.ordinal(), autoWidth + "px");
				} else {
					tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
				}
				
				tab.setWidget(0, col.ordinal(), col == PAYROLL_COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() ));
				
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
				if ( col.getCellStyleClass() != null) {
					tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
					tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
				}
			}
		} else {
			for ( COLS col : COLS.values()) {
				if (col == COLS.AUTO ) {
					tab.getColumnFormatter().setWidth(col.ordinal(), autoWidth + "px");
				} else {
					tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
				}
				
				tab.setWidget(0, col.ordinal(), col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() ));
				
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
				if ( col.getCellStyleClass() != null) {
					tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
					tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
				}
			}
		}
		
		return tab;
	}

	private Widget getToolbarPanel(final FinanceModuleOptions opt) {
		toolbar = new AonToolbar(this.isPayroll ? "Vencimientos N\u00f3minas" : AON.MSG.financeModule());

		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden financeParamsHidden = new Hidden(IRequestParamsNames.FINANCE_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(financeParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toolbar.hideMessages();
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
				financeParamsHidden.setValue(JsonParams.convert(searchPanel.getParams( opt )));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		toolbar.add(exportButton);

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

		settleAllButton = new AonToolbarButton( AON.MSG.settleSelected(), AON.CSS.aonIconFinanceSettle() );
		settleAllButton.setEnabled(selectedItems.size()>0);
		settleAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm(AON.MSG.settleAllFinanceAction( selectedItems.size() ), new ConfirmDialogCallback(){
					@Override
					public void onAccept() {
						
						progressContainer.setVisible(true);
						progressContainer.setWidth("90%");
						progress.setStyleName(AON.CSS.aonPaddingLeft());
						progress.addStyleName(AON.CSS.aonPaddingRight());
						progress.addStyleName(AON.CSS.aonMarginLeft());
						progress.addStyleName(AON.CSS.aonMarginRight());
						progressContainer.getElement().getStyle().setBorderColor("RoyalBlue");
						progressContainer.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
						progressContainer.getElement().getStyle().setBorderWidth(1, Unit.PX);
						progress.getElement().getStyle().setBackgroundColor("RoyalBlue");
						progress.setHeight("5px");
						progress.setWidth("0px");
						final MutableInt p = new MutableInt(0);
						for (Integer id : selectedItems) {
							FinanceRow financeRow = finances.get(id);
							if (financeRow != null && financeRow.getFinance().isFullPending() && financeRow.getFinance().getId() != null) {
								FINANCE_SERVICE.settleFinance(opt.getDomainName()
										,opt.getDomain()
										,opt.getUser(), financeRow.getFinance().getId()
										,new AsyncCallback<Finance>() {

									@Override
									public void onFailure(Throwable caught) {
										progress();
										showError("Se ha producido un error al saldar el vencimiento. ["+caught.getMessage()+"]");
									}

									@Override
									public void onSuccess(Finance fin) {
										progress();
										paintRow(opt, fin, financeRow.getRow());
									}
									
									private void progress() {
										p.add(1);
										int prg = ( p.getValue() * 100 / selectedItems.size());
										progress.setWidth(prg + "%");
										if (AonNumberUtils.equals(p.getValue(),selectedItems.size())) {
											progressContainer.setVisible(false);					
										}
									}
								});						
							}
						}
					}
					
					@Override
					public void onCancel() {
					}
				});
			}
		});
		toolbar.add(settleAllButton);
		
		unSettleAllButton = new AonToolbarButton("Eliminar movimientos saldados", AON.CSS.aonIconFinanceUndo() );
		unSettleAllButton.setVisible(selectedItems.size()>0 && searchPanel.isSettledChecked());
		unSettleAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				long notSettledFinances = finances.values().stream().filter(financesRow -> financesRow.getFinance().isSelected() && !financesRow.getFinance().isSettled()).count();
				
				if(notSettledFinances > 0) {
					AonDialog warningDialog = new AonDialog("Eliminar movimientos saldados",
							new HTML("No se pueden eliminar los movimientos saldados de los vencimientos cuyo estado sea distinto de <b>Saldado</b>. Por favor revise los vencimientos seleccionadas."));

					warningDialog.warning();
				} else if(selectedItems.size() > 0){
					AonDialog deleteDialog = new AonDialog("Eliminar movimientos saldados",
							new HTML("Se va a proceder a elimiar los moviminetos saldados de <b>" + selectedItems.size() + "</b> vencimiento(s).<br>\u00bfEsta seguro que desea proceder\u003f. Este proceso ser\u00e1 irreversible"));

					deleteDialog.confirm(new AonAcceptDialogCallback() {

						@Override
						public void onCancel() {
							// Nothing to do here
						}

						@Override
						public void onAccept() {
							unSettleAllButton.setEnabled(false);
							
							progressContainer.setVisible(true);
							progress.setStyleName(AON.CSS.aonPaddingLeft());
							progress.addStyleName(AON.CSS.aonPaddingRight());
							progress.addStyleName(AON.CSS.aonMarginRight());
							progressContainer.getElement().getStyle().setBorderColor("RoyalBlue");
							progressContainer.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
							progressContainer.getElement().getStyle().setBorderWidth(1, Unit.PX);
							progressContainer.getElement().getStyle().setProperty("margin", "0 1rem");
							progress.getElement().getStyle().setBackgroundColor("RoyalBlue");
							progress.setHeight("5px");
							progress.setWidth("0px");
							final MutableInt p = new MutableInt(0);
							for (Integer id : selectedItems) {
								FinanceRow financeRow = finances.get(id);
								if (financeRow != null && financeRow.getFinance().isSettled() && financeRow.getFinance().getId() != null) {
									FINANCE_SERVICE.unSettleFinance(opt.getDomainName()
											,opt.getDomain()
											,opt.getUser(), financeRow.getFinance().getId()
											,new AsyncCallback<Finance>() {

										@Override
										public void onFailure(Throwable caught) {
											progress();
											showError("Se ha producido un error al eliminar el movimiento saldado del vencimiento. ["+caught.getMessage()+"]");
										}

										@Override
										public void onSuccess(Finance fin) {
											progress();
											paintRow(opt, fin, financeRow.getRow());
										}
										
										private void progress() {
											p.add(1);
											int prg = ( p.getValue() * 100 / selectedItems.size());
											progress.setWidth(prg + "%");
											if (AonNumberUtils.equals(p.getValue(),selectedItems.size())) {
												progressContainer.setVisible(false);	
												unSettleAllButton.setEnabled(true);
											}
										}
									});					
								}
							}
						}
					});
				}
			}
		});
		toolbar.add(unSettleAllButton);
		
		if(this.isPayroll) {
			settleSalariesButton = new AonToolbarButton("Vencimiento de n\u00f3minas", AON.CSS.aonIconRebaseEdit());
			settleSalariesButton.addClickHandler(e -> {
				new AonSettleDateDialog("Generar vencimientos de n\u00f3minas") {
					
					@Override
					protected void onAccept(Date date) {
						FINANCE_SERVICE.createSettleSalaries(opt.getDomainName(), opt.getDomain(), opt.getUser(), date, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								showError(caught.getMessage());
							}

							@Override
							public void onSuccess(Void arg0) {
								enableMoreData();
								container.clear();
								tab = getTable();
								container.add(tab);
								offset.setValue(0);
								search(opt, searchPanel.getParams( opt ), offset.getValue());
							}});
					}
				};
			});
			toolbar.add(settleSalariesButton);
		}
	
		return toolbar;
	}
	
	protected void checkAll(final FinanceModuleOptions opt, boolean check) {
		for (FinanceRow financeRow : finances.values()) {
			financeRow.getFinance().setSelected(check);
			manageSelection(financeRow.getFinance());
			Widget w = tab.getWidget(financeRow.getRow(), 1);
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

	protected void search(final FinanceModuleOptions opt,FinanceParams params) {
		enableMoreData();
		finances.clear();
		clearSelection();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, params, offset.getValue());
	}

	private void search(final FinanceModuleOptions opt, FinanceParams params, final int ofs) {
		if (!isMoreData()) return; 
		FINANCE_SERVICE.getFinances(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, limit
				, new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						checkAll.setEnabled(false);
						uncheckAll.setEnabled(false);
						exportButton.setEnabled(false);
						if (result != null && !result.isEmpty()) {
							result.forEach( finance -> paintRow(opt,finance));
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
						checkAll.setEnabled(true);
						uncheckAll.setEnabled(true);
						exportButton.setEnabled(true);
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

	private void paintRow(final FinanceModuleOptions opt, Finance finance) {
		int row = tab.getRowCount();
		finances.put(finance.getId(), new FinanceRow(row, finance));
		paintRow(opt, finance, row);
	}
	
	private void paintRow(final FinanceModuleOptions opt, Finance finance, int row) {
		int col = 0;
		
		Label payment = new Label();
		payment.setTitle(finance.isPayment()?"Pago":"Cobro");
		payment.setStyleName(AON.CSS.aonIconLabel());
		payment.addStyleName(finance.isPayment()?AON.CSS.aonIconFinanceOut():AON.CSS.aonIconFinanceIn());
		
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction()
				, selectedItems.contains(finance.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		checkButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedItems.contains(finance.getId())) {
					finance.setSelected(false);
					manageSelection( finance );
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					finance.setSelected(true);
					manageSelection( finance );
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}
			}
		});

		Label status = new Label();
		status.setText(finance.getFinanceStatus() == null?"":finance.getFinanceStatus().getDescription());
		finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
			@Override
			public void visitSettled() {
				status.setStyleName(AON.CSS.aonColorBlue());
			}
			
			@Override
			public void visitReturned() {
				status.setStyleName(AON.CSS.aonColorRed());
				status.addStyleName(AON.CSS.aonBold());
			}
			
			@Override
			public void visitPending() {
				status.setStyleName(AON.CSS.aonColorRed());
			}
			
			@Override
			public void visitPaid() {
				status.setStyleName(AON.CSS.aonColorGreen());
			}
			
			@Override
			public void visitBatched() {
				status.setStyleName(AON.CSS.aonColorGreen());
			}
		});
		Label dueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));
		Label numDoc  = new Label();
		Label invReference = new Label();
		Label invDate = new Label();
		if (finance.getInvoice() != null) {
			numDoc.setText(finance.getInvoice().getDocumentNumber());
			invDate.setText(AON.DATE_FORMAT.format(finance.getInvoice().getIssueDate()));
			invReference.setText(finance.getInvoice().getReferenceCode());
		} else {
			numDoc.setText(isPayroll ? finance.getConcept() : finance.getRegistry().getDocument());
		}
		Label regDoc  = new Label( finance.getRegistryDocument());
	
		String rname = AonStringUtils.isBlank(finance.getRegistryName()) 
				? finance.getRegistryName() : (finance.getInvoice() != null ? finance.getInvoice().getRegistryName() : ""); 
		
		if(this.isPayroll) rname = finance.getRegistry().getName();
		
		Label regName = new Label(rname);
		regName.setTitle(rname);
		
		Label payMethod = new Label(finance.getPayMethodName());
		Label amount = new Label(AON.FMT.format(finance.getAmount()));
		
		FinanceActionsPanel actionsPanel = new FinanceActionsPanel(finance, this.isPayroll, new FinanceModuleCallback() {
			
			@Override
			public FinanceModuleOptions getOptions() {
				return opt;
			}
			
			@Override
			public void addExtraInfo(Widget widget) {
				FinanceModule.this.addExtraInfo(widget);				
			}

			@Override
			public void updateAndRefresh(Finance finance) {
				FinanceModule.this.paintRow(opt, finance, row);
			}

			@Override
			public FinanceServiceAsync getFinanceService() {
				return FinanceModule.FINANCE_SERVICE;
			}
			
			@Override
			public void refresh() {
				toolbar.hideMessages();
				enableMoreData();
				container.clear();
				tab = getTable();
				container.add(tab);
				offset.setValue(0);
				search(opt, searchPanel.getParams( opt ), offset.getValue());
			}
		});

		tab.setWidget(row, col, payment);
		++col;
		tab.setWidget(row, col, checkButton);
		++col;
		tab.setWidget(row, col, dueDate);
		++col;
		tab.setWidget(row, col, numDoc);
		++col;
		if(!isPayroll) {
			tab.setWidget(row, col, invReference);
			++col;
			tab.setWidget(row, col, invDate);
			++col;
		}
		tab.setWidget(row, col, regDoc);
		++col;
		regName.setWidth(autoWidth + "px");
		regName.setStyleName(AON.CSS.aonTruncate());
		tab.setWidget(row, col, regName);
		++col;
		tab.setWidget(row, col, payMethod);
		++col;
		tab.setWidget(row, col, amount);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight() );
		++col;
		tab.setWidget(row, col, status);
		++col;
		tab.setWidget(row, col, actionsPanel);
	}
	
	private void clearSelection() {
		selectedItems.clear();
	}
	private void manageSelection(Finance finance) {
		if (finance.isSelected()) {
			selectedItems.add(finance.getId());
		} else {
			selectedItems.remove(finance.getId());
		}
		refreshIcons();
	}
	private void refreshIcons() {
		settleAllButton.setEnabled(selectedItems.size()>0);
		unSettleAllButton.setVisible(selectedItems.size()>0 && searchPanel.isSettledChecked());
		unSettleAllButton.setEnabled(selectedItems.size()>0 && searchPanel.isSettledChecked());
		selectedCount.setText( (selectedItems.size() > 0)?  AonNumberUtils.toString(selectedItems.size()) :""); 
	}
}		
