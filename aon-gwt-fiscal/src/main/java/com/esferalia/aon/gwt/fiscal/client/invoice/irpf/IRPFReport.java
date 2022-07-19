package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;


import java.util.LinkedList;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class IRPFReport extends MainEntryPoint {

	private static final String IRPF_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/roms/IrpfReportExcelPrint";
	private static final String IRPF_PDF_REPORT_PRINT = "/aon_gwt_fiscal/roms/IrpfReportPDFPrint";

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	static final IrpfReportServiceAsync SERVICE;
	static {
		IrpfReportServiceAsync serviceRaw = GWT.create(IrpfReportService.class);
		SERVICE = new IrpfReportServiceAsyncDecorator(serviceRaw);
	}
	
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel summaryContent;
	private SimpleLayoutPanel resultsContent;
	private SimpleLayoutPanel filterPanelContainer;
	private IRPFReportFilterPanel filterPanel;
	
	private FormPanel diskForm;
	private Hidden irpfParamsHidden;
	private Hidden domainIdHidden;
	private Hidden domainNameHidden;
	private Hidden userHidden;
	
	private NumberFormat formatter;

	private IrpfReportModuleOptions options;
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				onModuleLoad( new IrpfReportModuleOptions()
					.setParentWidget(root)
					.setDomainName(getCurrentDomainName())
					.setDomain(getCurrentDomain())
					.setUser(getCurrentUser())
					.setConfiguration(config)
				);
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Modelo 303"));
			}
		});
	}

	private IrpfReportModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new IrpfReportModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(IrpfReportModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		options.getParentWidget().add(dockLayoutPanel);
		
		formatter = NumberFormat.getDecimalFormat();
		formatter.overrideFractionDigits(2, 2);

		filterPanelContainer = new SimpleLayoutPanel();
		initializeFilterPanel(options);
		dockLayoutPanel.addNorth( filterPanelContainer, 115);
		SimpleLayoutPanel content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		tabLayout.addSelectionHandler( event -> {
			if (event.getSelectedItem() == 0 && resultsContent != null) {
				resultsContent.clear();	
			} else if (event.getSelectedItem() == 1 && resultsContent.getWidget() == null) {
				onSearch();	
			}
		});
						
		summaryContent = new SimpleLayoutPanel();
		tabLayout.add(summaryContent, AON.MSG.summary());
		
		resultsContent = new SimpleLayoutPanel();
		tabLayout.add(resultsContent, AON.MSG.informationBreakdown());
		
		content.setWidget(tabLayout);
		dockLayoutPanel.add(content);
		
		onSearch();
	}
	
	private Widget getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar("Tabla I.R.P.F.");
		
		final AonToolbarButton pdf = new AonToolbarButton(AON.MSG.print(), AON.CSS.aonIconPdf());
		pdf.addClickHandler(event -> submitForm(IRPF_PDF_REPORT_PRINT));
		toolbarPanel.add(pdf);

		final AonToolbarButton excel = new AonToolbarButton(AON.MSG.export(), AON.CSS.aonIconExcel());
		excel.addClickHandler(event -> submitForm(IRPF_EXCEL_REPORT_PRINT));
		toolbarPanel.add(excel);

		final AonToolbarButton clean = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		clean.addClickHandler(event -> {
			initializeFilterPanel(options);
			onSearch();
		});
		toolbarPanel.add(clean);
	
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refresh.addClickHandler(event -> onSearch() );
		toolbarPanel.add(refresh);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		irpfParamsHidden = new Hidden(IRequestParamsNames.IRPF_PARAMS);
		formFlowPanel.add(irpfParamsHidden);
		domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden(IRequestParamsNames.USER);
		formFlowPanel.add(userHidden);
		toolbarPanel.add(diskForm);

		return toolbarPanel;
	}
	
	private void initializeFilterPanel(IrpfReportModuleOptions options) {
		filterPanel = new IRPFReportFilterPanel(options);
		filterPanel.addValueChangeHandler( event -> onSearch());
		filterPanelContainer.setWidget(filterPanel);
	}
	
	protected void onSearch() {
		if (tabLayout.getSelectedIndex() == 0) {
			refreshSummary(filterPanel.getParams(getOptions()));
		} else {
			refreshResults(filterPanel.getParams(getOptions()));
		}
	}
	
	private void refreshAndSeeResults(IRPFParams params) {
		if (tabLayout.getSelectedIndex() == 0) {
			tabLayout.selectTab(1,false);
		}
		refreshResults(params);
	}
	
	private void refreshResults(IRPFParams params) {
		resultsContent.clear();
		filterPanel.setValue( params );
		resultsContent.setWidget(new IRPFReportPanel(getOptions(), params, null, null));		
	}
	
	private void refreshSummary(IRPFParams params) {
		summaryContent.clear();
		ScrollPanel scroll = new ScrollPanel();			
		summaryContent.setWidget(scroll);
		SERVICE.getIrpfBreakdownSummary(getOptions().getOccam(), params 
				, new AsyncCallback<LinkedList<IrpfBreakdown>>() {
			
			@Override
			public void onSuccess(LinkedList<IrpfBreakdown> result) {
				TreeMap<WithholdingType,TreeMap<Double,Pair<IrpfBreakdown, IrpfBreakdown>>> map = new TreeMap<>();
				for (IrpfBreakdown irpf : result){
					TreeMap<Double,Pair<IrpfBreakdown,IrpfBreakdown>> block = map.computeIfAbsent(irpf.getWithholdingType(), k -> new TreeMap<>());
					Pair<IrpfBreakdown,IrpfBreakdown> line = block.get(irpf.getPercent());
					if (line == null) {
						line = Pair.of(irpf.isSales()?irpf:null, irpf.isSales()?null:irpf);
					} else {
						line = Pair.of(irpf.isSales()?irpf:line.getLeft(), irpf.isSales()?line.getRight():irpf);
					}
					block.put(irpf.getPercent(), line);					
				}
				
				FlexTable tab = new FlexTable();
				tab.setCellPadding(0);
				tab.setCellSpacing(0);
				tab.setStyleName(AON.CSS.aonBlockCenter());
				tab.addStyleName(AON.CSS.aonMarginTop());
				tab.getElement().getStyle().setProperty("border-collapse", "collapse");
				
				tab.getColumnFormatter().setStyleName(0, AON.CSS.aonWidth150());
				
				tab.getColumnFormatter().setStyleName(1, AON.CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(2, AON.CSS.aonWidth80());
				tab.getColumnFormatter().setStyleName(3, AON.CSS.aonWidth120());
				
				tab.getColumnFormatter().setStyleName(4, AON.CSS.aonWidth20());
				
				tab.getColumnFormatter().setStyleName(5, AON.CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(6, AON.CSS.aonWidth80());
				tab.getColumnFormatter().setStyleName(7, AON.CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(8, AON.CSS.aonWidth120());
				
				paintTableHeader(tab);

				double outputBase = 0;
				double outputQuota = 0;
				double inputBase = 0;
				double inputQuota = 0;
				double inputDeductibleQuota = 0;

				int row = 2;
				for (WithholdingType type :  map.keySet() ) {
					row = paintEmptyRow(tab,row);
					
					Label typeLabel = new Label(type.getDescription());
					typeLabel.addClickHandler(event -> refreshAndSeeResults(filterPanel.getParams(getOptions())
							.setWithholdingType(type)
							.setPercent(null)
							.setOutput(null)
							));
					tab.setWidget(row,0, typeLabel);
					int rowspan = map.get(type).values().size();
					tab.getFlexCellFormatter().setRowSpan(row, 0, rowspan+1);
					
					tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonBold());
					tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonTextCenter());
					tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonFontLarger());
					tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonBorder());
					tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonClickableBlock());
					tab.getCellFormatter().addStyleName(row, 0, AON.CSS.aonBackgroundLigthGray());
					
					boolean first = true;

					double typeOutputBase = 0;
					double typeOutputQuota = 0;
					double typeInputBase = 0;
					double typeInputQuota = 0;
					double typeInputDeductibleQuota = 0;

					for (Pair<IrpfBreakdown,IrpfBreakdown> pair : map.get(type).values() ) {
						int col = first? 0 : -1;
						first = false;
						if (pair.getLeft() != null) {
							ClickHandler leftClickHandler = event -> refreshAndSeeResults(filterPanel.getParams(getOptions())
								.setWithholdingType(type)
								.setPercent(pair.getLeft().getPercent() )
								.setOutput(true));
							typeOutputBase = typeOutputBase + pair.getLeft().getBase();
							typeOutputQuota = typeOutputQuota + pair.getLeft().getQuota();
							
							Label baseLabel = addCell(tab, row, (col+1) , formatter.format( pair.getLeft().getBase()));

							Label quotaLabel = addCell(tab, row, (col+3) , formatter.format( pair.getLeft().getQuota()));
							
							if (!AonMathUtils.isLessThanZero( pair.getLeft().getPercent())) {
								tab.getCellFormatter().addStyleName(row, (col+1), AON.CSS.aonClickableBlock());
								baseLabel.addClickHandler(leftClickHandler);
								
								Label percentLabel = addCell(tab, row, (col+2) , formatter.format( pair.getLeft().getPercent()) );
								tab.getCellFormatter().addStyleName(row, (col+2), AON.CSS.aonClickableBlock());
								percentLabel.addClickHandler(leftClickHandler);
								
								tab.getCellFormatter().addStyleName(row, (col+3), AON.CSS.aonClickableBlock());
								quotaLabel.addClickHandler(leftClickHandler);
							} else {
								addCell(tab, row, (col+2) , "---" );
							}
							
							
						} else {
							addCell(tab, row, (col+1) , "");
							addCell(tab, row, (col+2) , "");
							addCell(tab, row, (col+3) , "");
						}
						
						if (pair.getRight() != null) {
							typeInputBase = typeInputBase + pair.getRight().getBase();
							typeInputQuota = typeInputQuota + pair.getRight().getQuota();
							typeInputDeductibleQuota = typeInputDeductibleQuota + pair.getRight().getDeductibleQuota();
							ClickHandler rightClickHandler = event -> refreshAndSeeResults(filterPanel.getParams(getOptions())
								.setWithholdingType(type)
								.setPercent(pair.getRight().getPercent())
								.setOutput(false));
							Label baseLabel = addCell(tab, row, (col+5) , formatter.format( pair.getRight().getBase()));
							Label quotaLabel = addCell(tab, row, (col+7) , formatter.format( pair.getRight().getQuota()));
							
							if (!AonMathUtils.isLessThanZero( pair.getRight().getPercent())) {
								tab.getCellFormatter().addStyleName(row, (col+5), AON.CSS.aonClickableBlock());
								baseLabel.addClickHandler(rightClickHandler);
								
								Label percentLabel = addCell(tab, row, (col+6) , formatter.format( pair.getRight().getPercent()) );
								tab.getCellFormatter().addStyleName(row, (col+6), AON.CSS.aonClickableBlock());
								percentLabel.addClickHandler(rightClickHandler);
								
								tab.getCellFormatter().addStyleName(row, (col+7), AON.CSS.aonClickableBlock());
								quotaLabel.addClickHandler(rightClickHandler);
							} else {
								addCell(tab, row, (col+6) , "---" );
							}
							
							
							Label deductibleQuotaLabel = addCell(tab, row, (col+8) , formatter.format( pair.getRight().getDeductibleQuota()));
							tab.getCellFormatter().addStyleName(row, (col+8), AON.CSS.aonClickableBlock());
							deductibleQuotaLabel.addClickHandler(rightClickHandler);
							
						} else {
							addCell(tab, row, (col+5) , "");
							addCell(tab, row, (col+6) , "");
							addCell(tab, row, (col+7) , "");
							addCell(tab, row, (col+8) , "");
						}
						++row;
					}
						
					row = paintTotal( tab, row,type,typeOutputBase,typeOutputQuota,typeInputBase,typeInputQuota,typeInputDeductibleQuota);					
					
					outputBase = outputBase + typeOutputBase;
					outputQuota = outputQuota  + typeOutputQuota; 
					inputBase = inputBase + typeInputBase;
					inputQuota = inputQuota + typeInputQuota;
					inputDeductibleQuota = inputDeductibleQuota + typeInputDeductibleQuota;
				}
				row = paintEmptyRow(tab, row);
				row = paintTotal( tab, row,null,outputBase,outputQuota,inputBase,inputQuota,inputDeductibleQuota);
				paintEmptyRow(tab, row);
				scroll.setWidget( tab );		
			}
			@Override
			public void onFailure(Throwable caught) {
				Label error = new Label(AON.MSG.unexpectedError(caught.getMessage()));
				error.setStyleName(AON.CSS.aonMargin());
				error.addStyleName(AON.CSS.aonColorRed());
				error.addStyleName(AON.CSS.aonBold());
				scroll.setWidget(error);
			}
			
			private void paintTableHeader(FlexTable tab) {
				Label outputLabel = new Label( AON.MSG.outputInvoices() );
				tab.setWidget(0,1, outputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 1, 3);
				tab.getCellFormatter().setStyleName(0, 1, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonFontLarger());
				tab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonBackgroundLigthGray());
				tab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonBorder());
				tab.getCellFormatter().addStyleName(0, 1, AON.CSS.aonClickableBlock());
				outputLabel.addClickHandler(event -> refreshAndSeeResults(filterPanel.getParams(getOptions())
					.setWithholdingType(null)
					.setPercent(null)
					.setOutput(true)));
				
				Label inputLabel = new Label( AON.MSG.inputInvoices() );
				tab.setWidget(0,3, inputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 3, 4);
				tab.getCellFormatter().setStyleName(0, 3, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 3, AON.CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 3, AON.CSS.aonFontLarger());
				tab.getCellFormatter().addStyleName(0, 3, AON.CSS.aonBorder());
				tab.getCellFormatter().addStyleName(0, 3, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(0, 3, AON.CSS.aonBackgroundLigthGray());
				inputLabel.addClickHandler(event -> 
					refreshAndSeeResults(filterPanel.getParams(getOptions())
						.setWithholdingType(null)
						.setPercent(null)
						.setOutput(false)));
	
				Label outputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,1, outputBaseLabel);
				decorateCell( tab, 1, 1 );
				
				Label outputPercentLabel = new Label( "%" );
				tab.setWidget(1,2, outputPercentLabel);
				decorateCell( tab, 1, 2 );
	
				Label outputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,3, outputQuotaLabel);
				decorateCell( tab, 1, 3 );
				
				Label inputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,5, inputBaseLabel);
				decorateCell( tab, 1, 5 );				
	
				Label inputPercentLabel = new Label( "%" );
				tab.setWidget(1,6, inputPercentLabel);
				decorateCell( tab, 1, 6 );
				
				Label inputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,7, inputQuotaLabel);
				decorateCell( tab, 1, 7 );

				Label inputDeductibleQuotaLabel = new Label( AON.MSG.dedQuota() );
				tab.setWidget(1,8, inputDeductibleQuotaLabel);
				decorateCell( tab, 1, 8 );
			}
			
			private void decorateCell(FlexTable tab, int row, int col) {
				tab.getCellFormatter().setStyleName( row, col, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName( row, col, AON.CSS.aonTextRight());
				tab.getCellFormatter().addStyleName( row, col, AON.CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName( row, col, AON.CSS.aonBackgroundLigthGray());
				tab.getCellFormatter().addStyleName( row, col, AON.CSS.aonBorder());
			}
			
			private int paintEmptyRow(FlexTable tab, int row) {
				tab.setWidget(row, 0, new Label());
				tab.getFlexCellFormatter().setColSpan(row, 0, 9);
				tab.getRowFormatter().getElement(row).getStyle().setHeight(5, Unit.PX);
				return ++row;
			}
			
			private Label addCell(FlexTable tab, int row, int col, String text) {
				Label label = new Label(text);
				tab.setWidget(row, col, label);
				tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBorder());
				return label;
			}
			
			private int paintTotal(FlexTable tab, int row, WithholdingType type,double typeOutputBase, double typeOutputQuota,
					double typeInputBase, double typeInputQuota, double typeInputDeductibleQuota) {
				int col = type==null?1:0;
				ClickHandler leftClickHandler = event -> refreshAndSeeResults(filterPanel.getParams(getOptions()).setWithholdingType(type).setOutput(true)); 
				Label obl = addCell(tab, row, col+0 , formatter.format( typeOutputBase));
				obl.addClickHandler(leftClickHandler);
				Label oql = addCell(tab, row, col+2 , formatter.format( typeOutputQuota));
				oql.addClickHandler(leftClickHandler);
				
				ClickHandler rightClickHandler = event -> refreshAndSeeResults(filterPanel.getParams(getOptions()).setWithholdingType(type).setOutput(false)); 
				Label ibl = addCell(tab, row, col+4 , formatter.format( typeInputBase));
				ibl.addClickHandler(rightClickHandler);
				Label iql = addCell(tab, row, col+6 , formatter.format( typeInputQuota));
				iql.addClickHandler(rightClickHandler);
				Label idql = addCell(tab, row, col+7 , formatter.format( typeInputDeductibleQuota));
				idql.addClickHandler(rightClickHandler);
				
				tab.getCellFormatter().addStyleName(row, col+0, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+0, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+2, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+2, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+4, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+4, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+6, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+6, AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+7, AON.CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+7, AON.CSS.aonBold());
				return ++row;
			}
			
		});		
	}
	
	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		irpfParamsHidden.setValue(JsonParams.convert(filterPanel.getParams(getOptions())));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		userHidden.setValue(getCurrentUser());
		diskForm.submit();
	}

	public static void run() {
		GWT.runAsync(IRPFReport.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("Listado IRPF"));
			}

			@Override
			public void onSuccess() {
				IRPFReport irpfReport = new IRPFReport();
				irpfReport.onModuleLoad();
			}
			
		});
	}
	
}
