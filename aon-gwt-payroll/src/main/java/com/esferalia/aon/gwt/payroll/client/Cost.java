 
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.ExcelType.COMPLETE;
import static com.esferalia.aon.gwt.payroll.shared.ExcelType.SUMMARY;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.server.SistemaREDServlet;
import com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService;
import com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params;
import com.esferalia.aon.gwt.payroll.shared.CostExcelService;
import com.esferalia.aon.gwt.payroll.shared.EnterprisePayrollPDFService;
import com.esferalia.aon.gwt.payroll.shared.ExcelType;
import com.esferalia.aon.gwt.payroll.shared.RemunerationRecordService;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class Cost extends ResizeComposite {
	
	// ----------------------------------------------- Static Variables 

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 115;
	
	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH);

	// ----------------------------------------------- UiBinder 

	interface Binder extends UiBinder<Widget, Cost> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- Listener 
	
	static interface Listener {
		void onStartSLD();
		void onFinishSLD();
		void onPublish(CostDocuments documents, String type);
	}
	
	// ----------------------------------------------- ScheduledCommand (Excel)
	
	class ExcelCommand implements ScheduledCommand {

		@Override
		public void execute() {
			printExcel(SUMMARY);
		}
	}
	
	class ExcelCompleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			printExcel(COMPLETE);
		}
	}
	
	class CSVCCommand implements ScheduledCommand {

		@Override
		public void execute() {
			printCSV();
		}
	}
	
	
	class ExcelMenu extends ContextMenu {
				
		private MenuItem excel = null;
		private MenuItem excelComplete = null;
		private MenuItem csv = null;
		private ContextMenu aggregatedAnnualSummary = null;
		private ContextMenu remunerationRecord = null;
		
		
		public ExcelMenu() {
			
			LinkedHashSet <Integer> availableYears = new LinkedHashSet<Integer>();
			
			
			excel = addItem("Microsoft Excel (.xls)", new ExcelCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			excel.ensureDebugId("excel");
			
			excelComplete = addItem("Microsoft Excel (.xls, detallado)", new ExcelCompleteCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			excelComplete.ensureDebugId("excelComplete");
			
			csv = addItem("Valores separados por comas (.csv)", new CSVCCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			csv.ensureDebugId("csv");
			
			addSeparator();
			
				MenuItem summaryItem = addItem("Resumen Anual Agregado (.xsl, mensual)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
				summaryItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null & !availableYears.isEmpty()) {
						
						aggregatedAnnualSummary = new ContextMenu();
						for (Integer year : availableYears) {
							aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.MONTHLY),
									AON.CSS.aonIconExcel(), style.cmd_btn());
						}
						aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					PopupPanel ppp = new PopupPanel(true);
					ppp.add(aggregatedAnnualSummary);
					ppp.setPopupPosition(summaryItem.getAbsoluteLeft() + summaryItem.getOffsetWidth(), summaryItem.getAbsoluteTop());
					ppp.show();
				});
				
				
				
				MenuItem summaryQuarterlyItem = addItem("Resumen Anual Agregado (.xsl, trimestral)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
				summaryQuarterlyItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null & !availableYears.isEmpty()) {
						
						aggregatedAnnualSummary = new ContextMenu();
						for (Integer year : availableYears) {
							aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.QUARTERLY),
									AON.CSS.aonIconExcel(), style.cmd_btn());
						}
						aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					PopupPanel ppp = new PopupPanel(true);
					ppp.add(aggregatedAnnualSummary);
					ppp.setPopupPosition(summaryQuarterlyItem.getAbsoluteLeft() + summaryQuarterlyItem.getOffsetWidth(), summaryQuarterlyItem.getAbsoluteTop());
					ppp.show();
				});
				
				
				MenuItem recordItem = addItem("Registro Retributivo (.xsl)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
				recordItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null & !availableYears.isEmpty()) {
						
						remunerationRecord = new ContextMenu();
						for (Integer year : availableYears) {
							remunerationRecord.addItem(String.valueOf(year), () -> printRemunerationRecord(year),
									AON.CSS.aonIconExcel(), style.cmd_btn());
						}
						remunerationRecord.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					PopupPanel ppp = new PopupPanel(true);
					ppp.add(remunerationRecord);
					ppp.setPopupPosition(recordItem.getAbsoluteLeft() + recordItem.getOffsetWidth(), recordItem.getAbsoluteTop());
					ppp.show();
				});
			
		}
		
		
	}
	
	// ----------------------------------------------- ScheduledCommand (See Type) 
	
	class SalaryCommand implements ScheduledCommand {
		@Override
		public void execute() {}
	}
	
	class ExtraCommand implements ScheduledCommand {
		@Override
		public void execute() {}
	}
	
	class SettleCommand implements ScheduledCommand {
		@Override
		public void execute() {}
	}
	
	class DelayCommand implements ScheduledCommand {
		@Override
		public void execute() {}
	}
	
	class L00Command implements ScheduledCommand {
		@Override
		public void execute() {}
	}

	class L13Command implements ScheduledCommand {
		@Override
		public void execute() {}
	}

	class L03Command implements ScheduledCommand {
		@Override
		public void execute() {}
	}

	class SeeMenu extends ContextMenu {
				
		private MenuItem salary = null;
		private MenuItem extra = null;
		private MenuItem settle = null;
		private MenuItem delay = null;
		
		private MenuItem l00 = null;
		private MenuItem l13 = null;
		private MenuItem l03 = null;

		public SeeMenu() {
			
			salary = addItem(Salary.Type.SALARY.getDescription(), new SalaryCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			salary.ensureDebugId("salary");
			
			extra = addItem(Salary.Type.EXTRA.getDescription(), new ExtraCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			extra.ensureDebugId("extra");
			
			settle = addItem(Salary.Type.SETTLE.getDescription(), new SettleCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			settle.ensureDebugId("settle");
			
			delay = addItem(Salary.Type.DELAY.getDescription(), new DelayCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			delay.ensureDebugId("delay");
			
			addSeparator();
			
			l00 = addItem(Salary.Type.L00.getDescription(), new L00Command(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			l00.ensureDebugId("l00");
			
			l13 = addItem(Salary.Type.L13.getDescription(), new L13Command(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			l13.ensureDebugId("l13");

			l03 = addItem(Salary.Type.L03.getDescription(), new L03Command(), 
					"", AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			l03.ensureDebugId("l03");
}

		public MenuItem getSalary() {
			return salary;
		}

		public MenuItem getExtra() {
			return extra;
		}

		public MenuItem getSettle() {
			return settle;
		}

		public MenuItem getDelay() {
			return delay;
		}
		
		public MenuItem getL00() {
			return l00;
		}

		public MenuItem getL13() {
			return l13;
		}

		public MenuItem getL03() {
			return l03;
		}
	}
	
	// ----------------------------------------------- ScheduledCommand (Type)

	private class TypeCommand implements ScheduledCommand {

		private Salary.Type type;
		private MenuItem menuItem;

		private TypeCommand(MenuItem menuItem, Salary.Type type) {
			this.type = type;
			this.menuItem = menuItem;
			this.menuItem.setScheduledCommand(this);
		}

		@Override
		public void execute() {
			try {
				if (!costDocuments.containsType(type)) {
					costDocuments.addType(type);
					setCheckedStyle(menuItem, true);
				} else {
					costDocuments.removeType(type);
					setCheckedStyle(menuItem, false);
				}
				getAsHTML();
			} catch (Exception e) {
				Window.alert(e.getMessage());
			}
		}

	}
	
	// ----------------------------------------------- UiFields
	
	@UiField
	static
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	FlowPanel mainPanel;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	SplitLayoutPanel containerSplitLayoutPanel;
	
	@UiField
	Viewer pdfViewer;
	
	// ----------------------------------------------- Variables

	private int zoom = DEFAULT_ZOOM;

	private CostDocuments costDocuments;
	
	private List<Listener> listeners;
	
	private AonToolbar toolbar;
	private AonToolbarButton excelBtn;
	private AonToolbarButton pdfBtn;
	private AonToolbarButton publishBtn;
	private AonToolbarButton bidoqBtn;
	private ListBox dateListBox = new ListBox();
	private AonToolbarButton zoomInBtn;
	private AonToolbarButton zoomOutBtn;
	private AonToolbarButton seeBtn;
	private AonToolbarButton tgssBtn;
	
	private ExcelMenu excelMenu;
	private SeeMenu seeMenu;
	
	
	// ----------------------------------------------- Constructor
	public Cost() {
		toolbar = getToolbarPanel();

		initWidget(binder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		seeMenu = new SeeMenu();
		
		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSalaryDateChanged();
			}
		});
		

		new TypeCommand(seeMenu.getSalary(), Salary.Type.SALARY);
		new TypeCommand(seeMenu.getExtra(), Salary.Type.EXTRA);
		new TypeCommand(seeMenu.getSettle(), Salary.Type.SETTLE);
		new TypeCommand(seeMenu.getDelay(), Salary.Type.DELAY);
		
		new TypeCommand(seeMenu.getL00(), Salary.Type.L00);
		new TypeCommand(seeMenu.getL13(), Salary.Type.L13);
		new TypeCommand(seeMenu.getL03(), Salary.Type.L03);

		listeners = new LinkedList<Listener>(); 

//		publishBtn.setVisible(!Wnd.getCurrentDomainNameURL().contains("ayudat"));
		bidoqBtn.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));
		
		excelMenu = new ExcelMenu();
		
	}

	// ----------------------------------------------- Cost.Methods
	
	public void setCostDocuments(CostDocuments costDocuments) {

		this.costDocuments = costDocuments;
		onCostDocumentsChanged();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void onStartSLD() {
		for (Listener listener : listeners)
			listener.onStartSLD();
	}

	void onFinishSLD() {
		for (Listener listener : listeners)
			listener.onFinishSLD();
	}

	void onPublish(CostDocuments documents, String type) {
		for (Listener listener : listeners)
			listener.onPublish(documents, type);
	}
	
	private void onCostDocumentsChanged() {
		getAsHTML();
		syncFormatsButtons();
		syncCostDateListBox();
	}
	
	private void onSalaryDateChanged() {
		int selected = dateListBox.getSelectedIndex();
		costDocuments.setCurrentIndex(selected);
		getAsHTML();
	}
	
	// ----------------------------------------------- Cost.Auxiliar Methods
	
	private void getAsHTML() {
		costDocuments.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				pdfViewer.setDocument(html, zoom/100d);
				syncTypeMenuItems();
			}

			@Override
			public void onFailure(Throwable caught) {}
		});				
	}

	private void syncCostDateListBox() {
		dateListBox.clear();
		for (com.esferalia.aon.gwt.payroll.shared.Cost cost : costDocuments
				.getCosts()) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dateListBox.addItem(DATE_FORMAT.format(date));
		}
		dateListBox.setSelectedIndex(costDocuments.getCurrentIndex());
	}

	private void syncTypeMenuItems() {
		setCheckedStyle(seeMenu.getSalary(),
				costDocuments.containsType(Salary.Type.SALARY));
		setCheckedStyle(seeMenu.getExtra(),
				costDocuments.containsType(Salary.Type.EXTRA));
		setCheckedStyle(seeMenu.getSettle(),
				costDocuments.containsType(Salary.Type.SETTLE));
		setCheckedStyle(seeMenu.getDelay(),
				costDocuments.containsType(Salary.Type.DELAY));

		setCheckedStyle(seeMenu.getL00(),
				costDocuments.containsType(Salary.Type.L00));
		setCheckedStyle(seeMenu.getL13(),
				costDocuments.containsType(Salary.Type.L13));
		setCheckedStyle(seeMenu.getL03(),
				costDocuments.containsType(Salary.Type.L03));
	}

	private void syncFormatsButtons() {
		String formats[] = costDocuments.getSupportedFormats();
		Arrays.sort(formats);

		excelBtn.setVisible(Arrays.binarySearch(formats, "xls") >= 0);
	}

	private void setCheckedStyle(MenuItem menuItem, boolean checked) {
		if (checked) {
			menuItem.addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			menuItem.removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}
	
	private boolean isMenuItemChecked(MenuItem salary) {
		return AonStringUtils.containsIgnoreCase(salary.getStyleName(), STYLENAME_CHECKED_ITEM);
	}

	// ----------------------------------------------- Export Methods
	
	private void printExcel (ExcelType excelType) {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_excel/"
				+ costDocuments.geCurrentCost().getMonth() + "_" + costDocuments.geCurrentCost().getYear() + "_"
				+ costDocuments.geCurrentCost().getEnterpriseId() + "_" + costDocuments.geCurrentCost().getWorkplaceId() + "."
				+ "xsl");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(CostExcelService.Params.MONTH.getName()
				, String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden(CostExcelService.Params.YEAR.getName()
				, String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden(CostExcelService.Params.ENTERPRISE.getName()
				, String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden(CostExcelService.Params.WORKPLACE.getName()
				, String.valueOf(cost.getWorkplaceId())));
		flowPanel.add(new Hidden(CostExcelService.Params.EXCEL_TYPE.getName()
				, excelType.name()));
		flowPanel.add(new Hidden(CostExcelService.Params.DOMAIN.getName()
				, Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(CostExcelService.Params.USER.getName()
				, Wnd.getCurrentUser()));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.DELAY.ordinal())));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
		
	}
	
	private void printCSV () {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_csv/"
				+ costDocuments.geCurrentCost().getMonth() + "_" + costDocuments.geCurrentCost().getYear() + "_"
				+ costDocuments.geCurrentCost().getEnterpriseId() + "_" + costDocuments.geCurrentCost().getWorkplaceId() + "."
				+ "csv");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(Params.MONTH.getName(), String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden(Params.YEAR.getName(), String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden(Params.ENTERPRISE.getName(), String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden(Params.WORKPLACE.getName(), String.valueOf(cost.getWorkplaceId())));
		flowPanel.add(new Hidden(Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(Params.USER.getName(), Wnd.getCurrentUser()));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.DELAY.ordinal())));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
		
	}
	
	public void printPDF () {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_pdf/"
		+ costDocuments.geCurrentCost().getMonth() + "_" + costDocuments.geCurrentCost().getYear() + "_"
		+ costDocuments.geCurrentCost().getEnterpriseId() + "_" + costDocuments.geCurrentCost().getWorkplaceId() + "."
		+ "pdf");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.MONTH.getName()
				, String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.YEAR.getName()
				, String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.ENTERPRISE.getName()
				, String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.WORKPLACE.getName()
				, String.valueOf(cost.getWorkplaceId())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.DOMAIN.getName()
				, Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.USER.getName()
				, Wnd.getCurrentUser()));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.DELAY.ordinal())));

		if (isMenuItemChecked(seeMenu.getL00()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L00.ordinal())));
		if (isMenuItemChecked(seeMenu.getL13()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L13.ordinal())));
		if (isMenuItemChecked(seeMenu.getL03()))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L03.ordinal())));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
	}
	public void printAggregatedAnnualSummary (Integer year, AggregatedAnnualSummaryService.SummaryType type) {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "AggregatedAnnualSummary/Resumen_Anual_Agregado_" + year);
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.YEAR.getName(), String.valueOf(year)));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.ENTERPRISE.getName(), String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.WORKPLACE.getName(), String.valueOf(cost.getWorkplaceId())));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.COMPLETE.getName(), "true"));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.USER.getName(), Wnd.getCurrentUser()));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.TYPE.getName(), type.name()));
		
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
	}
	
	public void printRemunerationRecord (Integer year) {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "remuneration_record/Registro_Retributivo_" + year);
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(RemunerationRecordService.Params.YEAR.getName(), String.valueOf(year)));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.ENTERPRISE.getName(), String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.USER.getName(), Wnd.getCurrentUser()));
		
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
	}
	
	public void syncCalcs () {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "seg-social/" + SistemaREDServlet.CALCS);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(SistemaREDService.Parameter.USER.name(), Wnd.getCurrentUser()));
		flowPanel.add(new Hidden(SistemaREDService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(SistemaREDService.Parameter.DATE.name(),"01" + "/" + AonStringUtils.leftPad(Integer.toString(cost.getMonth()+1), 2 , "0") + "/" +cost.getYear()));
		
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(event -> {
			onFinishSLD();
			
			mainPanel.remove(formPanel);
			
			costDocuments.addType(Salary.Type.L00);
			setCheckedStyle(seeMenu.getL00(), true);
			costDocuments.addType(Salary.Type.L13);
			setCheckedStyle(seeMenu.getL13(), true);
			costDocuments.addType(Salary.Type.L03);
			setCheckedStyle(seeMenu.getL03(), true);

			getAsHTML();
		});
		
		
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
		
		onStartSLD();
		
	}

	// ----------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Costes");
		
		excelBtn = new AonToolbarButton( "Excel", AON.CSS.aonIconExcel() );
		excelBtn.addClickHandler(e -> {
			onExcel(e);
		});
		toolbar.add(excelBtn);
		
		pdfBtn = new AonToolbarButton( "PDF", AON.CSS.aonIconPdf() );
		pdfBtn.addClickHandler(e -> {
			onPDF(e);
		});
		toolbar.add(pdfBtn);
		
		publishBtn = new AonToolbarButton( "Publicar", AON.CSS.aonIconDrive() );
		publishBtn.addClickHandler(e -> {
			onPublish(e);
		});
		publishBtn.setVisible(false);
		toolbar.add(publishBtn);
		
		bidoqBtn = new AonToolbarButton( "Publicar", "aon-icon-bidoq" );
		bidoqBtn.addClickHandler(e -> {
			onBidoq(e);
		});
		toolbar.add(bidoqBtn);
		
		seeBtn = new AonToolbarButton( "Ver", AON.CSS.aonIconVisibility() );
		seeBtn.addClickHandler(e -> {
			onView(e);
		});
		toolbar.add(seeBtn);
		
		toolbar.add(dateListBox);
		
		zoomOutBtn = new AonToolbarButton( "Reducir", AON.CSS.aonIconZoomOut() );
		zoomOutBtn.addClickHandler(e -> {
			onZoomOut(e);
		});
		toolbar.add(zoomOutBtn);
		
		zoomInBtn = new AonToolbarButton( "Ampliar", AON.CSS.aonIconZoomIn() );
		zoomInBtn.addClickHandler(e -> {
			onZoomIn(e);
		});
		toolbar.add(zoomInBtn);
		
		tgssBtn = new AonToolbarButton( "TGSS", AON.CSS.aonIconTgss() );
		tgssBtn.addClickHandler(e -> {
			onTGSS(e);
		});
		toolbar.add(tgssBtn);

		return toolbar;

	}
	
	// ----------------------------------------------- Toolbar.Methods

	private void onExcel(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		excelMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		excelMenu.show();
	}
	
	private void onPDF(ClickEvent e) {
		printPDF();
	}

	private void onPublish(ClickEvent e) {
		onPublish(costDocuments, "drive");
	}
	
	private void onBidoq(ClickEvent e) {
		onPublish(costDocuments, "bidoq");
	}

	private void onZoomIn(ClickEvent e) {
		zoom = Math.max(MIN_ZOOM, zoom + ZOOM_STEP);
		getAsHTML();
	}

	private void onZoomOut(ClickEvent e) {
		zoom = Math.min(MAX_ZOOM, zoom - ZOOM_STEP);
		getAsHTML();
	}
	
	private void onView(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		seeMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		seeMenu.show();
	}

	private void onTGSS(ClickEvent e) {
		syncCalcs();
	}
	
}
