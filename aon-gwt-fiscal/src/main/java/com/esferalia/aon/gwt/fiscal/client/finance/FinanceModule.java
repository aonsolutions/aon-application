package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSettleDateDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.finance.FinancePayPanel.FinancePayPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceReturnPanel.FinanceReturnPanelCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.user.client.ui.Widget;

public class FinanceModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	
	private static final String FINANCE_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/FinanceReportExcelPrint/print";
	private static final String FINANCE_REPORT_EXCEL_PAYMENT = "/aon_gwt_fiscal/roms/FinanceReportExcelPrint/payments";
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static class FinanceRow {
		private int row;
		private HTMLPanel rowPanel;
		private Finance finance;
		private FinanceRow( int row, HTMLPanel rowPanel, Finance finance) {
			this.row = row;
			this.rowPanel = rowPanel;
			this.finance = finance;
		}
		private int getRow() {
			return row;
		}
		private HTMLPanel getRowPanel() {
			return rowPanel;
		}
		private Finance getFinance() {
			return finance;
		}
	}
	
	private AonCustomDockLayout dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private HTMLPanel messagePanel = new HTMLPanel("");
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonCustomTable table;
	
	private SplitLayoutPanel splitLayoutPanel;
	private ScrollPanel extraInfoContainer = new ScrollPanel();
	
	private LinkedHashMap<Integer,FinanceRow> finances = new LinkedHashMap<Integer,FinanceRow>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<Integer>();
	
	private AonToolbarButton exportButton;
	private AonToolbarButton settleAllButton;
	private AonToolbarButton unSettleAllButton;
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	private AonToolbarButton settleSalariesButton;
	private AonToolbarButton excelExportlButton;
	
	// ------ Filter
	private AonCustomDateBox fromInvoiceDate = new AonCustomDateBox("F. Inicio Factura");
	private AonCustomDateBox toInvoiceDate = new AonCustomDateBox("F. Fin Factura");
	
	private AonCustomDateBox fromDueDate = new AonCustomDateBox("F. Inicio Vto.");
	private AonCustomDateBox toDueDate = new AonCustomDateBox("F. Fin Vto.");
	
	private AonCustomListBox confidential = new AonCustomListBox("Confidecial");
	private AonCustomListBox payment = new AonCustomListBox("Tipo");
	private AonCustomNumberBox amount;
	private AonCustomTextBox concept;
	private AonCustomTextBox referenceCode = new AonCustomTextBox("N\u00BA Factura");
	private AonCustomListBox payMethod = new AonCustomListBox("Forma de pago");
	
	AonCustomMultiSelectBox statusMultiSelectBox = new AonCustomMultiSelectBox("Estado");
	// ------
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private InlineLabel selectedCount;
	
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

		dockLayoutPanel = new AonCustomDockLayout(this.isPayroll ? "Vencimientos N\u00f3minas" : AON.MSG.financeModule()) {
			
			@Override
			protected void onClearFilter() {
				enableMoreData();
				container.clear();
				getTable();
				container.add(table);
				offset.setValue(0);
				initialize(opt);
				search(opt, getParams(opt), offset.getValue());
			}
		};
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
		getToolbarPanel( opt );
		createFilter(opt);
		
		progressContainer.setVisible(false);
		progressContainer.add(progress);
		dockLayoutPanel.addNorth(progressContainer, 5);
		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.getElement().getStyle().setProperty("padding", "0 1.5em 1.5em 1.5em");
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
						search(opt, getParams( opt ),offset.getValue());
					}
				}
			}
		});
		
		// Auto search first time
		enableMoreData();
		container.clear();
		container.add(messagePanel);
		getTable();
		container.add(table);
		offset.setValue(0);
		search(opt, getParams( opt ), offset.getValue());
		
	}

	private void createFilter(FinanceModuleOptions opt) {
		dockLayoutPanel.setSearchPlaceholder("Filtrar por titular...");
		
		dockLayoutPanel.addKeyUpHandler(e -> {
			String value = dockLayoutPanel.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				search( opt, getParams(opt) );
			} else if(AonStringUtils.isBlank(value)) {
				search( opt, getParams(opt) );
			}
		});
		
		amount = new AonCustomNumberBox("Importe");
		
		concept = new AonCustomTextBox(this.isPayroll ? "Concepto" : "N\u00BA Documento");
		
		amount.addValueChangeHandler(e -> search(opt, getParams(opt)));
		
		concept.addValueChangeHandler(e -> search(opt, getParams(opt)));
		fromInvoiceDate.addValueChangeHandler(e -> search(opt, getParams(opt)));
		toInvoiceDate.addValueChangeHandler(e -> search(opt, getParams(opt)));
		fromDueDate.addValueChangeHandler(e -> search(opt, getParams(opt)));
		toDueDate.addValueChangeHandler(e -> search(opt, getParams(opt)));
		
		confidential.addItem( "NO confidenciales", "0" );
		confidential.addItem( "Confidenciales", "1" );
		confidential.addItem("Todos", "2");
		confidential.addChangeHandler(e -> search(opt, getParams(opt)));
		
		referenceCode.addValueChangeHandler(e -> search(opt, getParams(opt)));
		
		payMethod.addItem("----", "");
		opt.getConfiguration().getPayMethods().forEach(pm -> {
			payMethod.addItem(pm.getName(), AonNumberUtils.toString(pm.getId()));
		});
		payMethod.addChangeHandler(e -> search(opt, getParams(opt)));
		
		payment.addItem("Todos","");
		payment.addItem("Pago" ,"Pago");
		payment.addItem("Cobro", "Cobro");
		payment.addChangeHandler(e -> search(opt, getParams(opt)));
		
		dockLayoutPanel.addFilterWidget(amount);
		
		if(!this.isPayroll) dockLayoutPanel.addFilterWidget(payment);
		
		// Status
		Set<String> options = new LinkedHashSet<String>();
		options.add("Pendiente");
		options.add("Remesado");
		options.add("Devuelto");
		options.add("Pagado");
		options.add("Saldado");
		statusMultiSelectBox.setOptions(options);
		
		statusMultiSelectBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
//            	Window.alert(statusMultiSelectBox.getSelectedOptions().isEmpty() ? "No hay nada seleccionado" : String.join(", ", statusMultiSelectBox.getSelectedOptions()));
            	search(opt, getParams(opt));
            }
        });
		
		dockLayoutPanel.addFilterWidget(statusMultiSelectBox);
		
		if(!this.isPayroll) {
			HTMLPanel invoiceDatePanel = new HTMLPanel("");
			invoiceDatePanel.setStyleName(AON.CSS.aonItemFlex());
			
			invoiceDatePanel.add(fromInvoiceDate);
			invoiceDatePanel.add(toInvoiceDate);
			
			dockLayoutPanel.addFilterWidget(invoiceDatePanel);
		}
		
		HTMLPanel dueDatePanel = new HTMLPanel("");
		dueDatePanel.setStyleName(AON.CSS.aonItemFlex());
		
		dueDatePanel.add(fromDueDate);
		dueDatePanel.add(toDueDate);
		
		dockLayoutPanel.addFilterWidget(dueDatePanel);
		
		if(!this.isPayroll)
			dockLayoutPanel.addFilterWidget(referenceCode);
		
		dockLayoutPanel.addFilterWidget(concept);
		
		dockLayoutPanel.addFilterWidget(payMethod);
		
		if (opt.getConfiguration() != null && opt.getConfiguration().getUser() != null && opt.getConfiguration().getUser().hasConfidentialityRole())
			dockLayoutPanel.addFilterWidget(confidential);
		
		initialize(opt);
	}

	private FinanceParams getParams(FinanceModuleOptions opt) {
		boolean confidentiality = opt.getConfiguration() != null 
				&& opt.getUser() != null 
				&& opt.getConfiguration().getUser().hasConfidentialityRole();
		
		Boolean isPayment = AonStringUtils.isBlank(payment.getValue()) ? null : (AonStringUtils.equals(payment.getValue(), "Pago") ? true : false);
		
		FinanceParams params = new FinanceParams()
			.setDomain(opt.getDomain())
			.setDescription(dockLayoutPanel.getSearchTextBox().getValue())
			.setFromInvoiceDate(fromInvoiceDate.getValue())
			.setToInvoiceDate(toInvoiceDate.getValue())
			.setFromDueDate(fromDueDate.getValue())
			.setToDueDate(toDueDate.getValue())
			.setConcept(concept.getValue())
			.setReferenceCode(referenceCode.getValue())
			.setPayMethod(AonStringUtils.isNotBlank(payMethod.getValue()) ? Integer.parseInt(payMethod.getValue()) : null)
			.setPayment(isPayment)
			.setPending(statusMultiSelectBox.getSelectedOptions().contains("Pendiente"))
			.setBatched(statusMultiSelectBox.getSelectedOptions().contains("Remesado"))
			.setReturned(statusMultiSelectBox.getSelectedOptions().contains("Devuelto"))
			.setPaid(statusMultiSelectBox.getSelectedOptions().contains("Pagado"))
			.setSettled(statusMultiSelectBox.getSelectedOptions().contains("Saldado"))
			.setSecurityLevel(AonStringUtils.isNotBlank(confidential.getValue()) ? SecurityLevel.safeValueOf(Integer.parseInt(confidential.getValue())) : SecurityLevel.OFFICIAL)
			.setHasConfidentialityRole(confidentiality)
			.setIsPayroll(this.isPayroll)
			;
		
		if(amount.isBetweenNumbers()) {
			params
				.setAmount(null)
				.setGTAmount(amount.getGTValue())
				.setLTAmount(amount.getLTValue())
				.setBetweenNumbers(amount.isBetweenNumbers())
				;
		} else {
			params
				.setAmount(amount.getValue())
				.setGTAmount(null)
				.setLTAmount(null)
				.setBetweenNumbers(amount.isBetweenNumbers())
				;
		}
		
		
		
		return params;
	}
	
	public boolean isSettledChecked() {
		return statusMultiSelectBox.getSelectedOptions().contains("Saldado");
	}

	private void initialize(FinanceModuleOptions opt) {
		amount.setValue(null);
		fromInvoiceDate.setValue(null);
		toInvoiceDate.setValue(null);
		fromDueDate.setValue(null);
		toDueDate.setValue(null);
		payMethod.setValue("");
		confidential.setValue("2");
		concept.setValue(null);
		
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Pendiente");
		if(!this.isPayroll) selectedOptions.add("Devuelto");
		statusMultiSelectBox.setSelectedOptions(selectedOptions);
	}

	private static enum COLS {
		
		TYP(AonStringUtils.EMPTY		, "2rem" 			,"")
		, CHK(AonStringUtils.EMPTY		, "2rem" 			,"")
		, DDT("F. Vto."					, "6rem" 			,"")
		, DOC("N\u00BA Documento"		, "8rem"			,"")
		, INV("N\u00BA Factura"			, "7rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, IID("F. Fra."					, "6rem" 			,"")
		, TIT("CIF/NIF/NIE"				, "6rem"			,"")
		, AUTO("Titular"				, "-moz-available"  ,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PYM("Forma pago"				, "10.5rem"			,"")
		, AMO("Importe"					, "7rem" 			,"")
		, STA(AON.MSG.status()			, "6rem"			,"")
		, ACT(AON.MSG.actions()			, "6rem"			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel, String colWidth, String cellStyleClass) {
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
	
	private static enum PAYROLL_COLS {
		  TYP(AonStringUtils.EMPTY		, "2rem" 			,"")
		, CHK(AonStringUtils.EMPTY		, "2rem" 			,"")
		, DDT("F. Vto."					, "6rem" 			,"")
		, DOC("Concepto"				, "8rem"			,"")
		, TIT("CIF/NIF/NIE"				, "6rem"			,"")
		, AUTO("Titular"				, "-moz-available"  ,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PYM("Forma pago"				, "10.5rem"			,"")
		, AMO("Importe"					, "7rem" 			,"")
		, STA(AON.MSG.status()			, "6rem"			,"")
		, ACT(AON.MSG.actions()			, "6rem"			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;
		
		private PAYROLL_COLS(String headerLabel, String colWidth, String cellStyleClass) {
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

	protected AonCustomTable getTable() {
		table = new AonCustomTable();
		table.createHeader();
		
		selectedCount = new InlineLabel();
		selectedCount.addStyleName("minWidth_2");
		
		if(this.isPayroll) {
			for ( PAYROLL_COLS col : PAYROLL_COLS.values())
				if(col.equals(PAYROLL_COLS.TYP) || col.equals(PAYROLL_COLS.CHK)) {
					Label label = new Label( AonStringUtils.isBlank(col.getHeaderLabel()) ? "" : col.getHeaderLabel() );
					label.addStyleName("minWidth_2");
					table.addHeader(col.equals(PAYROLL_COLS.CHK) ? selectedCount : label, "1rem", col.getCellStyleClass());
				}
				
				else table.addHeader(new Label( AonStringUtils.isBlank(col.getHeaderLabel()) ? "" : col.getHeaderLabel() ), col.getColWidth(), col.getCellStyleClass());
		} else {
			for ( COLS col : COLS.values())
				if(col.equals(COLS.TYP) || col.equals(COLS.CHK)) {
					Label label = new Label( AonStringUtils.isBlank(col.getHeaderLabel()) ? "" : col.getHeaderLabel() );
					label.addStyleName("minWidth_2");
					table.addHeader(col.equals(COLS.CHK) ? selectedCount : label, "1rem", col.getCellStyleClass());
				}
				
				else table.addHeader(new Label( AonStringUtils.isBlank(col.getHeaderLabel()) ? "" : col.getHeaderLabel() ), col.getColWidth(), col.getCellStyleClass());
		}
		
		return table;
	}

	private void getToolbarPanel(final FinanceModuleOptions opt) {
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
		dockLayoutPanel.addToolbarButton(diskForm);
		
		exportButton = new AonToolbarButton( AON.MSG.export(), AON.CSS.aonIconExcel() );
		exportButton.setEnabled(false);
		exportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + FINANCE_REPORT_EXCEL_PRINT);
				financeParamsHidden.setValue(JsonParams.convert(getParams( opt )));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
			}
		});
		dockLayoutPanel.addToolbarButton(exportButton);

		checkAll  = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(false);
		checkAll .addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, true );
			}
		});
		dockLayoutPanel.addToolbarButton(checkAll);
		
		uncheckAll  = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(false);
		uncheckAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, false );
			}
		});
		dockLayoutPanel.addToolbarButton(uncheckAll);

		settleAllButton = new AonToolbarButton( AON.MSG.settleSelected(), AON.CSS.aonIconFinanceSettle() );
		settleAllButton.setEnabled(selectedItems.size()>0);
		settleAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.settleAllFinanceAction( selectedItems.size() ), new AonConfirmDialogCallback(){
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
										updateStatusField(financeRow.getRowPanel(), fin);
										updateButtonsField(financeRow.getRowPanel(), fin, opt);
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
		dockLayoutPanel.addToolbarButton(settleAllButton);
		
		unSettleAllButton = new AonToolbarButton("Eliminar movimientos saldados", AON.CSS.aonIconFinanceUndo() );
		unSettleAllButton.setVisible(selectedItems.size()>0 && isSettledChecked());
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
											updateStatusField(financeRow.getRowPanel(), fin);
											updateButtonsField(financeRow.getRowPanel(), fin, opt);
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
		dockLayoutPanel.addToolbarButton(unSettleAllButton);
		
		if(this.isPayroll) {
			settleSalariesButton = new AonToolbarButton("Vencimiento de n\u00f3minas", AON.CSS.aonIconRebaseEdit());
			settleSalariesButton.addClickHandler(e -> {
				new AonSettleDateDialog("Generar vencimientos de n\u00f3minas", "Fecha venicimiento de n\u00f3minas:") {
					
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
								getTable();
								container.add(table);
								offset.setValue(0);
								search(opt, getParams( opt ), offset.getValue());
							}});
					}
				};
			});
			dockLayoutPanel.addToolbarButton(settleSalariesButton);
		}
		
		excelExportlButton = new AonToolbarButton("Informe de plazos de pago", AON.CSS.aonIconExcel());
		excelExportlButton.setEnabled(selectedItems.size()>0);
		excelExportlButton.addClickHandler(e -> {
			long notSelectedFinances = finances.values().stream().filter(financesRow -> financesRow.getFinance().isSelected()).count();
			
			if(notSelectedFinances == 0) {
				AonDialog warningDialog = new AonDialog("Informe de plazos de pago",
						new HTML("No se puede generar el informe sin seleccionar vencimientos. Por favor revise los vencimientos seleccionadas."));

				warningDialog.warning();
			} else if(selectedItems.size() > 0){
				diskForm.setAction(GWT.getHostPageBaseURL() + FINANCE_REPORT_EXCEL_PAYMENT);
				financeParamsHidden.setValue(JsonParams.convert(getParams( opt )));
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
				
				excelExportlButton.setEnabled(false);
				checkAll( opt, false );
			}
		});
		dockLayoutPanel.addToolbarButton(excelExportlButton);
	}
	
	protected void checkAll(final FinanceModuleOptions opt, boolean check) {
		for (FinanceRow financeRow : finances.values()) {
			financeRow.getFinance().setSelected(check);
			manageSelection(financeRow.getFinance());
			Widget w = table.getWidget(financeRow.getRow(), 1);
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}
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
		getTable();
		container.add(table);
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
	
	public void addExtraInfo( Widget widget) {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption("Datos adicionales");
		extraInfoContainer.setWidget(widget);
		extraInfoContainer.scrollToTop();
		extraInfoContainer.getElement().getStyle().setProperty("padding", ".5rem");
		dialog.setWidget(extraInfoContainer);
		dialog.center();
		dialog.show();
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		AonMessagePanel.showError(messagePanel, msg);
	}

	private void paintRow(final FinanceModuleOptions opt, Finance finance) {
		int row = table.getRowsCount();
		paintRow(opt, finance, row);
	}
	
	private void paintRow(final FinanceModuleOptions opt, Finance finance, int rowX) {
		
		HTMLPanel row = table.createRow();
		
		finances.put(finance.getId(), new FinanceRow(rowX, row, finance));
		
		AonTableButton paymentButton = new AonTableButton(finance.isPayment() ? "Pago" : "Cobro", finance.isPayment() ? AON.CSS.aonIconFinanceOut() : AON.CSS.aonIconFinanceIn());
		table.addRow(row, paymentButton, COLS.TYP.getColWidth());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedItems.contains(finance.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
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
		}); 
		
		table.addRow(row, ( !isPayroll || finance.hasSalary() ? checkButton : new Label() ), ( !isPayroll || finance.hasSalary() ? COLS.CHK.getColWidth() : "1rem" ));
		
		Label dueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));
		table.addRow(row, dueDate, COLS.DDT.getColWidth());
		
		Label numDoc  = new Label();
		Label invReference = new Label();
		Label invDate = new Label();
		if (finance.getInvoice() != null) {
			numDoc.setText(finance.getInvoice().getDocumentNumber());
			invDate.setText(AON.DATE_FORMAT.format(finance.getInvoice().getIssueDate()));
			invReference.setText(finance.getInvoice().getReferenceCode());
			invReference.setTitle(finance.getInvoice().getReferenceCode());
			
			table.addInlineStyle(invReference, COLS.INV.getCellStyleClass());
		} else {
			numDoc.setText(isPayroll ? finance.getConcept() : finance.getRegistry().getDocument());
		}
		table.addRow(row, numDoc, COLS.DOC.getColWidth());
		
		if(!isPayroll) {
			table.addRow(row, invReference, COLS.INV.getColWidth());
			table.addRow(row, invDate, COLS.IID.getColWidth());
		}
		
		Label regDoc  = new Label( finance.getRegistryDocument());
		table.addRow(row, regDoc, COLS.TIT.getColWidth());
		
		String rname = AonStringUtils.isBlank(finance.getRegistryName()) ? finance.getRegistryName() : (finance.getInvoice() != null ? finance.getInvoice().getRegistryName() : "");
		if(this.isPayroll) rname = finance.getRegistry().getName();
		
		Label regName = new Label(rname);
		regName.setTitle(rname);
		table.addInlineStyle(regName, COLS.AUTO.getCellStyleClass());
		
		if(isPayroll && !finance.hasSalary()) {
			regName.addStyleName(AON.CSS.aonColorRed());
			regName.setTitle("Este vencimiento esta asociado a una nomina inexistente");
		}
		
		table.addRow(row, regName, COLS.AUTO.getColWidth());
		
		Label payMethod = new Label(finance.getPayMethodName());
		table.addRow(row, payMethod, COLS.PYM.getColWidth());
		
		Label amount = new Label(formatToEuro(finance.getAmount() + finance.getExpenses()));
		if(0.00 != finance.getExpenses()) {
			amount.setStyleName(AON.CSS.aonColorRed());
			amount.setTitle("Importe: " + AON.FMT.format(finance.getAmount()) + ", Gastos: " + AON.FMT.format(finance.getExpenses()));
		}
		amount.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		table.addRow(row, amount, COLS.AMO.getColWidth());
		
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
		table.addRow(row, status, COLS.STA.getColWidth());
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		addButtons(buttonContainer, row, finance, opt);
		table.addRow(row, buttonContainer, COLS.ACT.getColWidth());
	}
	
	private void addButtons(FlowPanel buttonContainer, HTMLPanel row, Finance finance, FinanceModuleOptions opt) {
		// *************************************************************************
		// *******															 *******
		// *******				TRACKING INFO BUTTON		 				 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton trackingButton = new AonTableButton(AON.MSG.tracking(), AON.CSS.aonIconHistory() );
		trackingButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				FinanceModule.FINANCE_SERVICE.getFinanceTracking(opt.getDomainName()
						,opt.getDomain()
						,opt.getUser(), finance.getId()
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
									label.setStyleName(AON.CSS.aonBlockMessage());
									label.addStyleName(AON.CSS.aonBlockInfoMessage());
									FinanceModule.this.addExtraInfo(label);
								} else {
									AonCustomTrackingPanel trackingPanel = new AonCustomTrackingPanel(list);
									FinanceModule.this.addExtraInfo(trackingPanel);
								}
							}
					
				});						
			}
		});
		trackingButton.addStyleName(AON.CSS.aonCustomRowButtom());
		buttonContainer.add(trackingButton);
		
		// *************************************************************************
		// *******															 *******
		// *******				PAY BUTTON		 				 			 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton payButton = null;
		if (finance.isFullPending() && finance.getId() != null && !isPayroll) {
			payButton = new AonTableButton(AON.MSG.toPay(), AON.CSS.aonIconFinancePay() );
			final AonCustomDialog dialog = new AonCustomDialog();
			String suffix = ( finance.isPayment()?" PAGO":" COBRO");
			dialog.setCaption(AON.MSG.payFinance() + suffix );
			payButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					FinancePayPanel payPanel = new FinancePayPanel();
					payPanel.show(opt.getDomainName()
							,opt.getDomain()
							,opt.getUser()
							,opt.getConfiguration(), finance,
							new FinancePayPanelCallback() {

								@Override
								public void onCancel() {
									dialog.hide();
								}

								@Override
								public void onAccept(FinanceTracking tracking) {
									dialog.hide();
									FinanceModule.FINANCE_SERVICE.payFinance(opt.getDomainName()
											,opt.getDomain()
											,opt.getUser(),tracking
											,new AsyncCallback<FinanceTracking>() {

												@Override
												public void onFailure(Throwable caught) {
													AonMessageDialog.error("Se ha producido un error al pagar el vencimiento. ["+caught.getMessage()+"]");
												}

												@Override
												public void onSuccess(FinanceTracking tracking) {
													updateStatusField(row, tracking.getFinance());
													updateButtonsField(row, tracking.getFinance(), opt);
												}
											});
								}
							});
					dialog.setWidget(payPanel);
					dialog.center();
					dialog.show();
				}
			});
			payButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(payButton);
		}
		
		// *************************************************************************
		// *******															 *******
		// *******				SETTLE BUTTON		 				 		 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton settleButton = null;
		if (finance.isFullPending() && finance.getId() != null && (!isPayroll || finance.hasSalary())) {
			settleButton = new AonTableButton(AON.MSG.toSettle(), AON.CSS.aonIconFinanceSettle() );
			settleButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.settleFinanceAction(), new AonConfirmDialogCallback(){

						@Override
						public void onAccept() {
							FinanceModule.FINANCE_SERVICE.settleFinance(opt.getDomainName()
									,opt.getDomain()
									,opt.getUser(), finance.getId()
									,new AsyncCallback<Finance>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessageDialog.error("Se ha producido un error al saldar el vencimiento. ["+caught.getMessage()+"]");
								}

								@Override
								public void onSuccess(Finance fin) {
									updateStatusField(row, fin);
									updateButtonsField(row, fin, opt);
								}

							});						
						}

						@Override
						public void onCancel() {
						}
					});
				}
			});
			settleButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(settleButton);
		}
		
		// *************************************************************************
		// *******															 *******
		// *******				UNSETTLE BUTTON		 				 		 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton undoButton = null;
		boolean canUndo = (finance.isSettled() && finance.getFinanceGroup() == null) || finance.isPaid() || finance.isReturned(); 
		if (canUndo && finance.getId() != null) {
			undoButton = new AonTableButton(AON.MSG.undoFinanceLastTracking(), AON.CSS.aonIconFinanceUndo() );
			undoButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.undoFinanceAction(), new AonConfirmDialogCallback(){

						@Override
						public void onAccept() {
							FinanceModule.FINANCE_SERVICE.undoFinance(opt.getDomainName()
									,opt.getDomain()
									,opt.getUser(), finance.getId()
									,new AsyncCallback<Finance>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessageDialog.error("Se ha producido un error al marcar el vencimiento como pendiente. ["+caught.getMessage()+"]"); 
								}

								@Override
								public void onSuccess(Finance fin) {
									updateStatusField(row, fin);
									updateButtonsField(row, fin, opt);
								}
							});						
						}

						@Override
						public void onCancel() {
						}
					});
				}
			});
			undoButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(undoButton);
		}

		// *************************************************************************
		// *******															 *******
		// *******				RETURN BUTTON		 				 			 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton returnButton = null;
		if (finance.isPaid() && finance.getId() != null) {
			returnButton = new AonTableButton(AON.MSG.toReturn(), AON.CSS.aonIconFinanceReturn() );
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption(AON.MSG.returnFinance());
			returnButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					FinanceReturnPanel returnPanel = new FinanceReturnPanel();
					returnPanel.show(opt.getDomainName()
							,opt.getDomain()
							,opt.getUser(),opt.getConfiguration(), finance,
							new FinanceReturnPanelCallback() {

								@Override
								public void onCancel() {
									dialog.hide();
								}

								@Override
								public void onAccept(FinanceTracking tracking) {
									dialog.hide();
									FinanceModule.FINANCE_SERVICE.returnFinance(opt.getDomainName()
											,opt.getDomain()
											,opt.getUser(), tracking
											,new AsyncCallback<FinanceTracking>() {

												@Override
												public void onFailure(Throwable caught) {
													AonMessageDialog.error("Se ha producido un devolver el pagar del vencimiento. ["+caught.getMessage()+"]");
												}

												@Override
												public void onSuccess(FinanceTracking tracking) {
													updateStatusField(row, tracking.getFinance());
													updateButtonsField(row, tracking.getFinance(), opt);
												}
											});
								}
							});
					dialog.setWidget(returnPanel);
					dialog.center();
					dialog.show();
				}
			});
			returnButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(returnButton);
		}

		// *************************************************************************
		// *******															 *******
		// *******				GROUP BUTTON		 				 		 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton  groupedButton = null; 
		if (finance.isSettled() && finance.getFinanceGroup() != null) {
			groupedButton = new AonTableButton(AON.MSG.financeGrouped(), AON.CSS.aonIconFinanceGroup());
			groupedButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					AonMessageDialog.show("No se puede deshacer.","El vencimiento pertence a una agrupaci\u00F3n de vencimientos");
				}
			});
			groupedButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(groupedButton);
		}
		
		if(isPayroll && finance.isPending()) {
			// *************************************************************************
			// *******															 *******
			// *******				DELETE BUTTON		 				 		 *******
			// *******															 *******
			// *************************************************************************
			AonTableButton  deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(e -> {
				e.stopPropagation();
				FinanceModule.FINANCE_SERVICE.getFinanceTracking(opt.getDomainName()
						,opt.getDomain()
						,opt.getUser(), finance.getId()
						,new AsyncCallback<LinkedList<FinanceTracking>>() {
							@Override
							public void onFailure(Throwable caught) {
								Label label = new Label("Se ha producido un error al recuperar el historial del vencimiento para el borrado. ["+caught.getMessage()+"]"); 
								FinanceModule.this.addExtraInfo(label);
							}

							@Override
							public void onSuccess(LinkedList<FinanceTracking> list) {
								if (list == null || list.size() == 0) {
									AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Vencimiento",
											new HTML("Se va a proceder a eliminar el vencimiento <b>" + finance.getRegistryDocument() + " - " + AON.DATE_FORMAT.format(finance.getDueDate()) + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

									deleteDialog.confirm(new AonAcceptDialogCallback() {

										@Override
										public void onCancel() {
											// Nothing to do here
										}

										@Override
										public void onAccept() {
											FinanceModule.FINANCE_SERVICE.deleteFinance(opt.getDomainName()
													,opt.getDomain()
													,opt.getUser()
													,finance.getId()
													,new AsyncCallback<Void>() {

														@Override
														public void onFailure(Throwable caught) {
															AonMessageDialog.error("Se ha producido un error al borrar el vencimiento. ["+caught.getMessage()+"]");
														}

														@Override
														public void onSuccess(Void success) {
															enableMoreData();
															container.clear();
															getTable();
															container.add(table);
															offset.setValue(0);
															search(opt, getParams( opt ), offset.getValue());
														}
													});
										}
									});
									
								} else {
									AonMessageDialog.error("No se puede borrar un vencimiento que tiene movimientos.");
								}
							}
					
				});	
				
			});
			deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(deleteButton);
		}
	}

	private void updateStatusField(HTMLPanel row, Finance finance) {
		Label status = (Label) row.getWidget(isPayroll ? 8 : 10);
		status.setText(finance.getFinanceStatus() == null ? "" : finance.getFinanceStatus().getDescription());
		
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
	}

	private void updateButtonsField(HTMLPanel row, Finance finance, FinanceModuleOptions opt) {
		FlowPanel buttonsPanel = (FlowPanel) row.getWidget(isPayroll ? 9 : 11);
		buttonsPanel.clear();
		addButtons(buttonsPanel, row, finance, opt);
	}

	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
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
		unSettleAllButton.setVisible(selectedItems.size()>0 && isSettledChecked());
		unSettleAllButton.setEnabled(selectedItems.size()>0 && isSettledChecked());
		excelExportlButton.setEnabled(selectedItems.size()>0);
		selectedCount.setText( (selectedItems.size() > 0)?  AonNumberUtils.toString(selectedItems.size()) :""); 
	}
}		
