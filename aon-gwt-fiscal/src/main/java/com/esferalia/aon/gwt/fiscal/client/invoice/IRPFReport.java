package com.esferalia.aon.gwt.fiscal.client.invoice;


import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportService;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class IRPFReport extends MainEntryPoint {

	private static CommonServiceAsync commonService;
	private static AccountingReportServiceAsync SERVICE;
	
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	
	private AonConfiguration configuration;
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel content;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel summaryContent;
	private SimpleLayoutPanel resultsContent;
	
	private IntegerBox year;
	private PeriodListBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private ListBox output;

	private AccountingRegistryBox registry;
	
	private WithholdingTypeListBox withholdingType;
	private ListBox activity;
	private ListBox rectificationType;
	private ListBox orderBy;
	private ListBox groupByNif;
	
	private NumberFormat formatter;
	
	interface SafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final SafeTemplate template = GWT.create(SafeTemplate.class);

	public IRPFReport(String domainName, int domain, String user) {
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
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		AccountingReportServiceAsync serviceRaw = GWT.create(AccountingReportService.class);
		SERVICE = new AccountingReportServiceAsyncDecorator(serviceRaw);
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
		
		formatter = NumberFormat.getDecimalFormat();
		formatter.overrideFractionDigits(2, 2);
		
		commonService.getAonConfiguration(getDomainName(),
				getDomain(), getCurrentUser(),
				new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						configuration = result;
						dockLayoutPanel.addNorth(getFilterPanel(), 135);
						content = new SimpleLayoutPanel();
						content.setStyleName(AON.AON_CSS.aonSelector());
						tabLayout = new TabLayoutPanel(26, Unit.PX);
						tabLayout.setWidth("100%");
						tabLayout.addSelectionHandler( new SelectionHandler<Integer>() {
							
							@Override
							public void onSelection(SelectionEvent<Integer> event) {
								if (event.getSelectedItem() == 0 && resultsContent != null) {
									resultsContent.clear();	
								} else if (event.getSelectedItem() == 1 && resultsContent.getWidget() == null) {
									onSearch();	
								}
							}
						});
						
						summaryContent = new SimpleLayoutPanel();
						tabLayout.add(summaryContent, template.tab(AON.MSG.summary(), AON.AON_CSS.aonIconModel()));
						
						resultsContent = new SimpleLayoutPanel();
						tabLayout.add(resultsContent, template.tab(AON.MSG.informationBreakdown(), AON.AON_CSS.aonIconInfo()));
						
						content.setWidget(tabLayout);
						dockLayoutPanel.add(content);
						initialize();
					}

					@Override
					public void onFailure(Throwable caught) {
						dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
					}
				});

	}

	
	protected void initialize() {
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		fillDates();
		orderBy.setSelectedIndex(0);
		groupByNif.setSelectedIndex(0);
		output.setSelectedIndex(0);
		withholdingType.setSelectedIndex(0);
		
		if (configuration != null && configuration.hasActivities()) {
			activity.setSelectedIndex(0);
		}
		registry.setValue((AccountingRegistry) null,false);
		rectificationType.setSelectedIndex(0);
		onSearch();
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
		toolbar.setWidget(0, 0, new Label("Tabla I.R.P.F."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button clean = new Button();
		clean.setText(AON.MSG.clean());
		clean.setTitle(AON.MSG.clean());
		clean.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		clean.addStyleName(AON.AON_CSS.aonIconDelete());
		clean.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				initialize();
			}
		});
		buttonContainer.add(clean);
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	private Widget getFilterPanel() {
		year = new IntegerBox();
		year.setMaxLength(4);
		year.setVisibleLength(5);
		year.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				fillDates();
				onSearch();
			}
		});
		
		period = new PeriodListBox();
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fillDates();
				onSearch();
			}
		});
		
		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onSearch();
			}
		});
		
		output = new ListBox();
		output.addItem("-- Todas --");
		output.addItem(AON.MSG.inputInvoices());
		output.addItem(AON.MSG.outputInvoices());
		output.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		groupByNif = new ListBox();
		groupByNif.addItem("No", "");
		groupByNif.setSelectedIndex(0);
		groupByNif.addItem("S\u00ED");
		groupByNif.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		withholdingType = new WithholdingTypeListBox("-- Todos --");
		withholdingType.setWidth("100px");
		withholdingType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		if (configuration != null && configuration.hasActivities()) {
			activity = new ListBox();
			activity.setWidth("120px");
			activity.addItem("-- Todas --", "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : configuration.getActivities()) {
				activity.addItem(ea.getDescription() + (ea.getIae() == null?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
			activity.addItem("-- Sin actividad --", "-1");
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch();
				}
			});
		}

		registry = new AccountingRegistryBox(getDomainName(), getDomain(), getCurrentUser()); 
		registry.setRequired(false);
		registry.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				onSearch();
			}
		});
		
		rectificationType = new ListBox();
		rectificationType.setWidth("100px");
		rectificationType.addItem("-- Todas --");
		rectificationType.addItem("Ni rectificativa ni rectificada");
		rectificationType.addItem(RectificationType.NORMAL_RECTIFIER.getDescription());
		
		rectificationType.addItem(RectificationType.SPECIAL_RECTIFIER.getDescription());
		rectificationType.getElement().<SelectElement>cast().getOptions().getItem(3).setDisabled(true);
		
		rectificationType.addItem(RectificationType.RECTIFIED.getDescription());
		rectificationType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});
		
		// Desplegable Ordenar por...
		orderBy = new ListBox();
		orderBy.setWidth("200px");
		orderBy.addItem("Fecha IVA", "");
		orderBy.setSelectedIndex(0);
		orderBy.addItem("Fecha de Factura");
		orderBy.addItem("N\u00famero de Documento");
		orderBy.addItem("N\u00famero de Factura");
		orderBy.addItem("Nombre de Cliente/Proveedor/Acreedor");
		orderBy.addItem("NIF/DNI de Cliente/Proveedor/Acreedor");
		orderBy.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		tab.getColumnFormatter().setWidth(0, "auto");
		tab.getColumnFormatter().setWidth(1, "30px;");

		FlowPanel filterPanel = new FlowPanel();
		tab.setWidget(0, 0, filterPanel);
		
		// ---------------------------------------------------------------- FIRST ROW
		FlowPanel firstRowPanel = new FlowPanel();
		firstRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(firstRowPanel);
		
		Label yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		yearLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(yearLabel);
		firstRowPanel.add(year);
		
		Label periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		periodLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(periodLabel);
		firstRowPanel.add(period);
		
		Label dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		dateLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(dateLabel);
		firstRowPanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		firstRowPanel.add(to);
		firstRowPanel.add(toDate);
		
		Label outputLabel = new InlineLabel(AON.MSG.invoices());
		outputLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		outputLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(outputLabel);
		toDate.addStyleName(AON.AON_CSS.aonMarginRight());
		firstRowPanel.add(output);
		
		Label groupByNifLabel = new InlineLabel("Agrupar por NIF/Raz\u00F3n social");
		groupByNifLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		groupByNifLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(groupByNif);
		output.addStyleName(AON.AON_CSS.aonMarginRight());
		firstRowPanel.add(groupByNifLabel);
		

		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel secondRowPanel = new FlowPanel();
		secondRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(secondRowPanel);


		Label titularLabel = new InlineLabel(AON.MSG.titular());
		titularLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		titularLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		secondRowPanel.add(titularLabel);
		secondRowPanel.add(registry);

		// ---------------------------------------------------------------- THIRD ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		thirdRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(thirdRowPanel);
		
		Label withholdingTypeLabel = new InlineLabel(AON.MSG.withholdingType());
		withholdingTypeLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		withholdingTypeLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(withholdingTypeLabel);
		thirdRowPanel.add(withholdingType);

		Label rectifiedLabel = new InlineLabel(AON.MSG.rectified());
		rectifiedLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		rectifiedLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(rectifiedLabel);
		rectificationType.addStyleName(AON.AON_CSS.aonMarginRight());
		thirdRowPanel.add(rectificationType);

		if (configuration != null && configuration.hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
			activityLabel.addStyleName(AON.AON_CSS.aonFontSmall());
			thirdRowPanel.add(activityLabel);
			thirdRowPanel.add(activity);
		}
		
		Label orderbyLabel = new InlineLabel("Ordenar por...");
		orderbyLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		orderbyLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		thirdRowPanel.add(orderbyLabel);
		orderBy.addStyleName(AON.AON_CSS.aonMarginRight());
		thirdRowPanel.add(orderBy);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		return scrollPanel;
	}

	protected void fillDates() {
		Integer y = year.getValue();
		Period p = period.getValue();
		if ( y == null) {
			fromDate.setValue(null,false);
			toDate.setValue(null,false);
		} else {
			if (p == null) {
				fromDate.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDate.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDate.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDate.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}

	protected void onSearch() {
		if (tabLayout.getSelectedIndex() == 0) {
			refreshSummary(getWidgetParams());
		} else {
			refreshResults(getWidgetParams() );
		}
	}
	
	private IRPFParams getWidgetParams() {
		IRPFParams params = new IRPFParams()
			.setDomain(getDomain())
			.setRegistry(registry.getId())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			;
		if (configuration != null && configuration.hasActivities() && activity.getSelectedIndex() > 0) {
			params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
		}
		params.setWithholdingType(withholdingType.getValue());
		params.setOrderBy(orderBy.getSelectedIndex());	
		params.setGroupByNif(groupByNif.getSelectedIndex());

		if (output.getSelectedIndex() == 1) params.setOutput(false);
		if (output.getSelectedIndex() == 2) params.setOutput(true);
		
		if (rectificationType.getSelectedIndex() > 0) {
			params.setRectificationType(RectificationType.values()[rectificationType.getSelectedIndex() - 1]);	
		}
		return params;
	}

	private void refreshAndSeeResults(IRPFParams params) {
		if (tabLayout.getSelectedIndex() == 0) {
			tabLayout.setAnimationDuration(300);
			tabLayout.selectTab(1,false);
		}
		refreshResults(params);
	}
	
	private void refreshResults(IRPFParams params) {
		resultsContent.clear();
		resultsContent.setWidget(new IRPFReportPanel(getDomainName(), getUser(), getDomain(), params, null, null));		
	}
	private void refreshSummary(IRPFParams params) {
		
		summaryContent.clear();
		ScrollPanel scroll = new ScrollPanel();			
		summaryContent.setWidget(scroll);
		SERVICE.getIrpfBreakdownSummary(getDomainName(), getUser(), getDomain(), params 
				, new AsyncCallback<LinkedList<IrpfBreakdown>>() {
			
			@Override
			public void onSuccess(LinkedList<IrpfBreakdown> result) {
				TreeMap<WithholdingType,TreeMap<Double,Pair<IrpfBreakdown, IrpfBreakdown>>> map = 
						new TreeMap<WithholdingType,TreeMap<Double,Pair<IrpfBreakdown, IrpfBreakdown>>>();
				for (IrpfBreakdown irpf : result){
					TreeMap<Double,Pair<IrpfBreakdown,IrpfBreakdown>> block = map.get(irpf.getWithholdingType());
					if (block == null) {
						block = new TreeMap<Double, Pair<IrpfBreakdown,IrpfBreakdown>>();
						map.put(irpf.getWithholdingType(), block);
					}
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
				tab.setStyleName(AON.AON_CSS.aonBlockCenter());
				tab.addStyleName(AON.AON_CSS.aonMarginTop());
				tab.addStyleName(AON.AON_CSS.aonBorderCollapse());
				
				tab.getColumnFormatter().setStyleName(0, AON.AON_CSS.aonWidth150());
				
				tab.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(2, AON.AON_CSS.aonWidth60());
				tab.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonWidth120());
				
				tab.getColumnFormatter().setStyleName(4, AON.AON_CSS.aonWidth20());
				
				tab.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(6, AON.AON_CSS.aonWidth60());
				tab.getColumnFormatter().setStyleName(7, AON.AON_CSS.aonWidth120());
				tab.getColumnFormatter().setStyleName(8, AON.AON_CSS.aonWidth120());
				
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
					typeLabel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							refreshAndSeeResults(getWidgetParams().setWithholdingType(type));
						}
					});
					tab.setWidget(row,0, typeLabel);
					int rowspan = map.get(type).values().size();
					tab.getFlexCellFormatter().setRowSpan(row, 0, rowspan+1);
					
					tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonBold());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFontBig());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonSimpleBorder());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonClickableBlock());
					tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBackgroundDisabled());
					
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
							ClickHandler leftClickHandler = new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									refreshAndSeeResults(getWidgetParams()
										.setWithholdingType(type)
										.setPercent(pair.getLeft().getPercent())
										.setOutput(true));
								}
							};
							typeOutputBase = typeOutputBase + pair.getLeft().getBase();
							typeOutputQuota = typeOutputQuota + pair.getLeft().getQuota();
							
							Label baseLabel = addCell(tab, row, (col+1) , formatter.format( pair.getLeft().getBase()));
							tab.getCellFormatter().addStyleName(row, (col+1), AON.AON_CSS.aonClickableBlock());
							baseLabel.addClickHandler(leftClickHandler);
							
							Label percentLabel = addCell(tab, row, (col+2) , formatter.format( pair.getLeft().getPercent()) );
							tab.getCellFormatter().addStyleName(row, (col+2), AON.AON_CSS.aonClickableBlock());
							percentLabel.addClickHandler(leftClickHandler);
							
							Label quotaLabel = addCell(tab, row, (col+3) , formatter.format( pair.getLeft().getQuota()));
							tab.getCellFormatter().addStyleName(row, (col+3), AON.AON_CSS.aonClickableBlock());
							quotaLabel.addClickHandler(leftClickHandler);
							
						} else {
							addCell(tab, row, (col+1) , "");
							addCell(tab, row, (col+2) , "");
							addCell(tab, row, (col+3) , "");
						}
						
						if (pair.getRight() != null) {
							typeInputBase = typeInputBase + pair.getRight().getBase();
							typeInputQuota = typeInputQuota + pair.getRight().getQuota();
							typeInputDeductibleQuota = typeInputDeductibleQuota + pair.getRight().getDeductibleQuota();
							ClickHandler rightClickHandler = new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									refreshAndSeeResults(getWidgetParams()
										.setWithholdingType(type)
										.setPercent(pair.getRight().getPercent())
										.setOutput(false));
								}
							};
							Label baseLabel = addCell(tab, row, (col+5) , formatter.format( pair.getRight().getBase()));
							tab.getCellFormatter().addStyleName(row, (col+5), AON.AON_CSS.aonClickableBlock());
							baseLabel.addClickHandler(rightClickHandler);
							
							Label percentLabel = addCell(tab, row, (col+6) , formatter.format( pair.getRight().getPercent()) );
							tab.getCellFormatter().addStyleName(row, (col+6), AON.AON_CSS.aonClickableBlock());
							percentLabel.addClickHandler(rightClickHandler);
							
							Label quotaLabel = addCell(tab, row, (col+7) , formatter.format( pair.getRight().getQuota()));
							tab.getCellFormatter().addStyleName(row, (col+7), AON.AON_CSS.aonClickableBlock());
							quotaLabel.addClickHandler(rightClickHandler);
							
							Label deductibleQuotaLabel = addCell(tab, row, (col+8) , formatter.format( pair.getRight().getDeductibleQuota()));
							tab.getCellFormatter().addStyleName(row, (col+8), AON.AON_CSS.aonClickableBlock());
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
				row = paintEmptyRow(tab, row);
				 
				scroll.setWidget( tab );		
			}
			@Override
			public void onFailure(Throwable caught) {
				Label error = new Label(AON.MSG.unexpectedError(caught.getMessage()));
				error.setStyleName(AON.AON_CSS.aonMargin());
				error.addStyleName(AON.AON_CSS.aonColorRed());
				error.addStyleName(AON.AON_CSS.aonBold());
				scroll.setWidget(error);
			}
			
			private void paintTableHeader(FlexTable tab) {
				Label outputLabel = new Label( AON.MSG.outputInvoices() );
				tab.setWidget(0,1, outputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 1, 3);
				tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFontBig());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonSimpleBorder());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonBackgroundDisabled());
				outputLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						IRPFParams params = getWidgetParams();
						params.setOutput(true);
						refreshAndSeeResults(params);
					}
				});
				
				Label inputLabel = new Label( AON.MSG.inputInvoices() );
				tab.setWidget(0,3, inputLabel);
				tab.getFlexCellFormatter().setColSpan(0, 3, 4);
				tab.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonFontBig());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonSimpleBorder());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonBackgroundDisabled());
				inputLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						IRPFParams params = getWidgetParams();
						params.setOutput(false);
						refreshAndSeeResults(params);
					}
				});
	
				Label outputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,1, outputBaseLabel);
				tab.getCellFormatter().setStyleName(1, 1, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonSimpleBorder());
				
				Label outputPercentLabel = new Label( "%" );
				tab.setWidget(1,2, outputPercentLabel);
				tab.getCellFormatter().setStyleName(1, 2, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 2, AON.AON_CSS.aonSimpleBorder());
	
				Label outputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,3, outputQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 3, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 3, AON.AON_CSS.aonSimpleBorder());
	
				Label inputBaseLabel = new Label( AON.MSG.taxableBase() );
				tab.setWidget(1,5, inputBaseLabel);
				tab.getCellFormatter().setStyleName(1, 5, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 5, AON.AON_CSS.aonSimpleBorder());
	
				Label inputPercentLabel = new Label( "%" );
				tab.setWidget(1,6, inputPercentLabel);
				tab.getCellFormatter().setStyleName(1, 6, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 6, AON.AON_CSS.aonSimpleBorder());
	
				Label inputQuotaLabel = new Label( AON.MSG.quota() );
				tab.setWidget(1,7, inputQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 7, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonTextCenter());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 7, AON.AON_CSS.aonSimpleBorder());

				Label inputDeductibleQuotaLabel = new Label( AON.MSG.dedQuota() );
				tab.setWidget(1,8, inputDeductibleQuotaLabel);
				tab.getCellFormatter().setStyleName(1, 8, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonBackgroundDisabled());
				tab.getCellFormatter().addStyleName(1, 8, AON.AON_CSS.aonSimpleBorder());
			}
			
			private int paintEmptyRow(FlexTable tab, int row) {
				tab.setWidget(row, 0, new Label());
				tab.getFlexCellFormatter().setColSpan(row, 0, 9);
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonHeight5());
				return ++row;
			}
			
			private Label addCell(FlexTable tab, int row, int col, String text) {
				Label label = new Label(text);
				tab.setWidget(row, col, label);
				tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonPaddingRight());
				tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonSimpleBorder());
				return label;
			}
			
			private int paintTotal(FlexTable tab, int row, WithholdingType type,double typeOutputBase, double typeOutputQuota,
					double typeInputBase, double typeInputQuota, double typeInputDeductibleQuota) {
				int col = type==null?1:0;
				ClickHandler leftClickHandler = new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						refreshAndSeeResults(getWidgetParams().setWithholdingType(type).setOutput(true));
					}
				}; 
				Label obl = addCell(tab, row, col+0 , formatter.format( typeOutputBase));
				obl.addClickHandler(leftClickHandler);
				Label oql = addCell(tab, row, col+2 , formatter.format( typeOutputQuota));
				oql.addClickHandler(leftClickHandler);
				
				ClickHandler rightClickHandler = new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						refreshAndSeeResults(getWidgetParams().setWithholdingType(type).setOutput(false));
					}
				}; 
				Label ibl = addCell(tab, row, col+4 , formatter.format( typeInputBase));
				ibl.addClickHandler(rightClickHandler);
				Label iql = addCell(tab, row, col+6 , formatter.format( typeInputQuota));
				iql.addClickHandler(rightClickHandler);
				Label idql = addCell(tab, row, col+7 , formatter.format( typeInputDeductibleQuota));
				idql.addClickHandler(rightClickHandler);
				
				tab.getCellFormatter().addStyleName(row, col+0, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+0, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+2, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+2, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+4, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+4, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+6, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+6, AON.AON_CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, col+7, AON.AON_CSS.aonClickableBlock());
				tab.getCellFormatter().addStyleName(row,col+7, AON.AON_CSS.aonBold());
				return ++row;
			}
			
		});		
	}
}
