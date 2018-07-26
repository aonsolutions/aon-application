package com.esferalia.aon.gwt.fiscal.client.invoice;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class OperationReport extends MainEntryPoint {

	private static final String OPERATION_EXCEL_REPORT_PRINT = "/aon_gwt_fiscal/roms/OperationReportExcelPrint";
	
	private static CommonServiceAsync commonService;
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	
	private AonConfiguration configuration;
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel content;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel ivaContent;
	private SimpleLayoutPanel irpfContent;
	
	private ListBox type;  // Compras y Gastos / Ventas e Ingresos
	private IntegerBox year;
	private PeriodListBox period;
	private DateBoxEx fromDate;
	private DateBoxEx toDate;

	private ListBox activity;
	int indexMainActivity = 0;
	
	private NumberFormat formatter;
	
	FormPanel diskForm;
	Hidden operationParamsHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	Hidden userHidden;
	
	interface SafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final SafeTemplate template = GWT.create(SafeTemplate.class);

	public OperationReport(String domainName, int domain, String user) {
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
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayoutPanel);
		
		formatter = NumberFormat.getDecimalFormat();
		formatter.overrideFractionDigits(2, 2);
		
		commonService.getAonConfiguration(getDomainName(),
				getDomain(),
				new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						configuration = result;
						dockLayoutPanel.addNorth(getFilterPanel(), 90);
						content = new SimpleLayoutPanel();
						content.setStyleName(AON.AON_CSS.aonSelector());
						tabLayout = new TabLayoutPanel(26, Unit.PX);
						tabLayout.setWidth("100%");
						tabLayout.addSelectionHandler( new SelectionHandler<Integer>() {
							
							@Override
							public void onSelection(SelectionEvent<Integer> event) {
								if (event.getSelectedItem() == 0) {
									ivaContent.clear();
								} else if (event.getSelectedItem() == 1) {
									irpfContent.clear();
								}
								onSearch();	
							}
						});

						ivaContent = new SimpleLayoutPanel();
						tabLayout.add(ivaContent, template.tab("Listado IVA", AON.AON_CSS.aonIconModel()));
						
						irpfContent = new SimpleLayoutPanel();
						tabLayout.add(irpfContent, template.tab("Listado IRPF", AON.AON_CSS.aonIconModel()));
												
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
		type.setSelectedIndex(0);
		year.setValue(DateUtils.getYear(),false);
		period.setSelectedIndex(0);
		fillDates();
		
		if (configuration != null && configuration.hasActivities()) {
			activity.setSelectedIndex(indexMainActivity);
		}
		
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
		toolbar.setWidget(0, 0, new Label("Panel de Compras y Gastos / Ventas e Ingresos"));
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

		final Button export = new Button();
		export.setText(AON.MSG.export());
		export.setTitle(AON.MSG.export());
		export.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		export.addStyleName(AON.AON_CSS.aonIconExcel());
		export.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				submitForm(OPERATION_EXCEL_REPORT_PRINT);
			}
		});
		buttonContainer.add(export);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		operationParamsHidden = new Hidden("operationParams");
		formFlowPanel.add(operationParamsHidden);
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
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		operationParamsHidden.setValue(JsonParams.convert(getWidgetParams()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());		
		userHidden.setValue(getCurrentUser());
		diskForm.submit();
	}
	
	private Widget getFilterPanel() {
		type = new ListBox();
		type.addItem("Compras y Gastos", "");
		type.addItem("Ventas e Ingresos");
		type.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fillDates();
				onSearch();
			}
		});
		
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
	
		if (configuration != null && configuration.hasActivities()) {
			activity = new ListBox();
			activity.setWidth("300px");
			int i = 0;
			for (EnterpriseActivity ea : configuration.getActivities()) {
				
				activity.addItem(ea.getDescription() + (ea.getIae() == null?"":(" ("+ea.getEpigraph()+")")), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
					indexMainActivity = i; // Se quedará marcada la actividad principal, por defecto
				}
				i++;
			}
			activity.setSelectedIndex(indexMainActivity);
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onSearch();
				}
			});
		}

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
		
		Label typeLabel = new InlineLabel("Tipo");
		typeLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
		typeLabel.addStyleName(AON.AON_CSS.aonFontSmall());
		firstRowPanel.add(typeLabel);
		firstRowPanel.add(type);
		
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

		// ---------------------------------------------------------------- SECOND ROW
		FlowPanel thirdRowPanel = new FlowPanel();
		thirdRowPanel.addStyleName(AON.AON_CSS.aonMarginTop5());
		filterPanel.add(thirdRowPanel);
		
		if (configuration != null && configuration.hasActivities()) {
			InlineLabel activityLabel = new InlineLabel(AON.MSG.activity());
			activityLabel.setStyleName(AON.AON_CSS.aonPanelGridOdd());
			activityLabel.addStyleName(AON.AON_CSS.aonFontSmall());
			thirdRowPanel.add(activityLabel);
			thirdRowPanel.add(activity);
		}

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
			refreshIVA(getWidgetParams());
		} else if (tabLayout.getSelectedIndex() == 1) {
			refreshIRPF(getWidgetParams() );
		}
	}
	
	private OperationParams getWidgetParams() {
		OperationParams params = new OperationParams()
			.setDomain(getDomain())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			;
		if (configuration != null && configuration.hasActivities() ) {
			params.setActivity( AonNumberUtils.toInteger( activity.getSelectedValue()));
			params.setActivityDescription( activity.getSelectedItemText() == null ? "" : activity.getSelectedItemText().replace("*",""));
		}
		if (type.getSelectedIndex() == 0) {
			params.setExpenses(true);
		} else {
			params.setExpenses(false);
		}
		
		return params;
	}
	
	private void refreshIVA(OperationParams params) {
		ivaContent.clear();
		params.setIrpf(false);
		ivaContent.setWidget(new OperationReportPanel(getDomainName(), getUser(), getDomain(), params));	
	}
	private void refreshIRPF(OperationParams params) {
		irpfContent.clear();
		params.setIrpf(true);
		irpfContent.setWidget(new OperationReportPanel(getDomainName(), getUser(), getDomain(), params));
	}
		
}
