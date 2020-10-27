package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoiceFinanceTrackingPanel;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FinanceModule extends MainEntryPoint {
	
	protected interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);

	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration configuration;
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private FlexTable tab;
	private SplitLayoutPanel splitLayoutPanel;
	private MinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private SimplePanel errorsContainer;
	private ErrorPanel errors;
	private ScrollPanel extraInfoContainer;	
	
	private boolean minimizedByUser;
	private int errorLogTabIndex;
	private int extraInfoTabIndex;
	
	private FormPanel diskForm;
	private Hidden vatParamsHidden;
	private Hidden domainIdHidden;
	private Hidden domainNameHidden;
	private Hidden userHidden;

	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		this.domainName= getCurrentDomainName();
		this.domain = getCurrentDomain();
		this.user = getCurrentUser();

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		root.add(dockLayoutPanel);

		COMMON_SERVICE.getAonConfiguration(domainName,domain,user,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						FinanceModule.this.configuration = result;
						loadModule();					
					}

					@Override
					public void onFailure(Throwable caught) {
						dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
					}
				});
	}
	
	private void loadModule() {
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		FinanceModuleSearchPanel searchPanel = new FinanceModuleSearchPanel(domainName, domain, user, configuration);
		searchPanel.addValueChangeHandler( new ValueChangeHandler<FinanceParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FinanceParams> event) {
				FinanceParams params = event.getValue();
				enableMoreData();
				container.clear();
				tab = getTable();
				container.add(tab);
				offset.setValue(0);
				search(params, offset.getValue());
			}
		});
		dockLayoutPanel.addNorth(searchPanel, 110);
		
		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
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
						search(searchPanel.getParams(),offset.getValue());
					}
				}
			}
		});
		
		
		
	}

	private static enum COLS {
		  TYP(AonStringUtils.EMPTY		,"20px" ,AON.AON_CSS.aonTextCenter())
		, STA(AON.MSG.status()			,"50px" ,AON.AON_CSS.aonTextCenter())
		, DDT("F. Vto."					,"50px" ,AON.AON_CSS.aonTextCenter())
		, DOC("N\u00BA Documento"		,"100px",AON.AON_CSS.aonTextLeft())
		, INV("N\u00BA Factura"			,"150px",AON.AON_CSS.aonTextLeft())
		, TIT("Titular"					,"100px",AON.AON_CSS.aonTextLeft())
		, TIB(""						,"auto" ,AON.AON_CSS.aonTextLeft())
		, PAY("Forma pago"				,"150px",AON.AON_CSS.aonTextLeft())
		, AMO("Importe"					,"150px",AON.AON_CSS.aonTextRight())
		, INF(""						,"20px" ,AON.AON_CSS.aonTextRight())
		, AUD(""						,"20px" ,AON.AON_CSS.aonTextRight())
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
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
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonDataTable());
		
		for ( COLS col : COLS.values()) {
			tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth());
			tab.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.AON_CSS.aonDataTableHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.AON_CSS.aonNowrap());
			}
		}
		return tab;
	}

	private Widget getToolbarPanel() {
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
		toolbar.setWidget(0, 0, new Label("Cartera de cobros y pagos"));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button excel = new Button();
		excel.setText(AON.MSG.export());
		excel.setTitle(AON.MSG.export());
		excel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		excel.addStyleName(AON.AON_CSS.aonIconExcel());
		excel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				submitForm("");
			}
		});
		buttonContainer.add(excel);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		vatParamsHidden = new Hidden("vatParams");
		formFlowPanel.add(vatParamsHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden("user");
		formFlowPanel.add(userHidden);
		buttonContainer.add(diskForm);
		
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	private void submitForm(String action) {
		Window.alert("Opc\u00F3n no disponible");
//		diskForm.setAction(GWT.getHostPageBaseURL() + action);
//		vatParamsHidden.setValue(JsonParams.convert(getWidgetParams()));
//		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
//		domainNameHidden.setValue(getCurrentDomainName());
//		userHidden.setValue(getCurrentUser());
//		diskForm.submit();
	}
	
	private MinimizePanel getMinimizePanel() {
		footPanel = new MinimizePanel();
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
		footPanel.setStyleName(AON.AON_CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		footPanel.add(tabLayout);
		int tabIndex = 0;
		
		errorsContainer = new SimplePanel();
		tabLayout.add(errorsContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.notifications(), AON.AON_CSS.aonIconError()));
		errorLogTabIndex = tabIndex;
		tabIndex++;
		
		extraInfoContainer = new ScrollPanel();
		tabLayout.add(extraInfoContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.additionalData(), AON.AON_CSS.aonIconInfo()));
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
	private int getErrorLogTabIndex(){
		return errorLogTabIndex;
	}
	private int getExtraInfoTabIndex(){
		return extraInfoTabIndex;
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

	private void search(FinanceParams params, final int ofs) {
		if (!isMoreData()) return; 

		FINANCE_SERVICE.getFinances(domainName,domain, user, params, ofs, limit
				, new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						errors = new ErrorPanel();
						errorsContainer.setWidget(errors);
						if (result != null && !result.isEmpty()) {
							result.forEach( finance -> addRow(finance));
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							FlowPanel line = new FlowPanel();
							InlineLabel label = new InlineLabel(AON.MSG.noData());
							line.add(label);
							container.add(line);
							disableMoreData();
						}
						enableSearch();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						FlowPanel line = new FlowPanel();
						line.setStyleName(AON.AON_CSS.aonInfoMessageBlock());
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.add(line);
						showError(caught.getMessage());
						enableSearch();
					}
				});
		
	}
	
	public void addExtraInfo( String htmlText) {
		HTMLPanel panel = new HTMLPanel(htmlText);
		addExtraInfo(panel);
	}
	
	public void addExtraInfo( Widget widget) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(getExtraInfoTabIndex());
		extraInfoContainer.setWidget(widget);
		extraInfoContainer.scrollToTop();
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		errors.showError(msg);
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			int effectiveHeigth = 5;
			splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
			splitLayoutPanel.animate(300, new AnimationCallback() {

				@Override
				public void onLayout(Layer layer, double progress) {
				}

				@Override
				public void onAnimationComplete() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							tabLayout.selectTab(FinanceModule.this.getErrorLogTabIndex());
						}
					});
				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					tabLayout.selectTab(FinanceModule.this.getErrorLogTabIndex());
				}
			});
		}
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

//	private class FinanceRow {
//		private FinanceRow() {
//			
//		}
//	}
	private void addRow(Finance finance) {
		int row = tab.getRowCount();
		int col = 0;
		
		Label payment = new Label();
		payment.setTitle(finance.isPayment()?"Pago":"Cobro");
		payment.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		payment.addStyleName(finance.isPayment()?AON.AON_CSS.aonIconPointOrange():AON.AON_CSS.aonIconPointLightGreen());
		Label status = new Label();
		status.setText(finance.getFinanceStatus() == null?"":finance.getFinanceStatus().getDescription());
		finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
			@Override
			public void visitSettled() {
				status.setStyleName(AON.AON_CSS.aonColoRoyalblue());
			}
			
			@Override
			public void visitReturned() {
				status.setStyleName(AON.AON_CSS.aonColorRed());
				status.addStyleName(AON.AON_CSS.aonBold());
			}
			
			@Override
			public void visitPending() {
				status.setStyleName(AON.AON_CSS.aonColorRed());
			}
			
			@Override
			public void visitPaid() {
				status.setStyleName(AON.AON_CSS.aonColorGreen());
			}
			
			@Override
			public void visitBatched() {
				status.setStyleName(AON.AON_CSS.aonColorGreen());
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
		}
		Label regDoc  = new Label( finance.getRegistryDocument());
		Label regName = new Label( finance.getRegistryName());
		Label payMethod = new Label( finance.getPayMethodName());
		Label amount = new Label(AON.FMT.format(finance.getAmount()));
		
		tab.setWidget(row, col, payment);
		++col;
		tab.setWidget(row, col, status);
		++col;
		tab.setWidget(row, col, dueDate);
		++col;
		tab.setWidget(row, col, numDoc);
		++col;
		tab.setWidget(row, col, invReference);
		++col;
		tab.setWidget(row, col, regDoc);
		++col;
		tab.setWidget(row, col, regName);
		++col;
		tab.setWidget(row, col, payMethod);
		++col;
		tab.setWidget(row, col, amount);
		tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight() );
		++col;
		
		
		// *************************************************************************
		// *******															 *******
		// *******				TRACKING INFO BUTTON		 				 *******
		// *******															 *******
		// *************************************************************************
		Button trackingButton = new Button();
		tab.setWidget(row, col, trackingButton);
		++col;
			
		trackingButton.setTitle( AON.MSG.tracking() );
		trackingButton.setStyleName(AON.AON_CSS.aonIconInfo());
		trackingButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		trackingButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		trackingButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FINANCE_SERVICE.getFinanceTracking(
						 FinanceModule.this.domainName
						,FinanceModule.this.domain
						,FinanceModule.this.user, finance.getId()
						,new AsyncCallback<LinkedList<FinanceTracking>>() {

							@Override
							public void onFailure(Throwable caught) {
								Label label = new Label("Se ha producido un error al recuperar el historial del vencimiento. ["+caught.getMessage()+"]"); 
								FinanceModule.this.addExtraInfo(label);
							}

							@Override
							public void onSuccess(LinkedList<FinanceTracking> list) {
								if (list == null || list.size() == 0) {
									Label label = new Label("No existen movimientos registrados del vencimiento.");
									label.setStyleName(AON.AON_CSS.aonInfoMessageBlock());
									FinanceModule.this.addExtraInfo(label);
								} else {
									InvoiceFinanceTrackingPanel trackingPanel = new InvoiceFinanceTrackingPanel(list);
									trackingPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
										@Override
										public void onSelection(AccountEntrySelectionEvent event) {
//											AccountEntrySelectionEvent.fire( InvoiceFinancePanel.this, event.getSelectedItem(), null);
										}
									});
									FinanceModule.this.addExtraInfo(trackingPanel);
								}
							}
					
				});						
			}
		});
	}
	
}		
