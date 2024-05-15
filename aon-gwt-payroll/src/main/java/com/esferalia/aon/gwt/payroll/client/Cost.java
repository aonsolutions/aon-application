 
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.ExcelType.COMPLETE;
import static com.esferalia.aon.gwt.payroll.shared.ExcelType.SUMMARY;
import static com.esferalia.aon.gwt.payroll.shared.SistemaREDService.EMPLOYEES;
import static com.esferalia.aon.gwt.payroll.shared.SistemaREDService.SISTEMA_RED_URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.server.SistemaREDServlet;
import com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService;
import com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDProgess;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.gwt.payroll.shared.CostExcelService;
import com.esferalia.aon.gwt.payroll.shared.EnterprisePayrollPDFService;
import com.esferalia.aon.gwt.payroll.shared.ExcelType;
import com.esferalia.aon.gwt.payroll.shared.RemunerationRecordService;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;
import net.aonsolutions.gwt.pdfjs.client.FullViewer.ViewerDefaultScale;

public class Cost extends ResizeComposite {
	
	// ----------------------------------------------- Static Variables 

	private static final Logger LOGGER = Logger.getLogger(Cost.class.getName());
	
	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH);

	// ----------------------------------------------- Listener 
	
	static interface Listener {
		void onStartSLD();
		void onFinishSLD();
		void onProgressSLD(String message, double progress);
		void onPublish(CostDocuments documents, String type);
		void onNoSex(String naf, String name);
		void onGeneratingDocument();
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
		private PopupPanel ppp = null;
		
		void removePpp() {
			if (ppp != null) {
				ppp.hide();
			}
		}
		
		public ExcelMenu() {
			
			LinkedHashSet <Integer> availableYears = new LinkedHashSet<Integer>();
			
			
			excel = addItem("Microsoft Excel (.xls)", new ExcelCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			excel.ensureDebugId("excel");
			
			excelComplete = addItem("Microsoft Excel (.xls, detallado)", new ExcelCompleteCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			excelComplete.ensureDebugId("excelComplete");
			
			csv = addItem("Valores separados por comas (.csv)", new CSVCCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			csv.ensureDebugId("csv");
			
			addSeparator();
			
				MenuItem summaryItem = addItem("Resumen Anual Agregado (.xsl, mensual)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
				summaryItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null && !availableYears.isEmpty()) {
						
						aggregatedAnnualSummary = new ContextMenu();
						for (Integer year : availableYears) {
							aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.MONTHLY),
									AON.CSS.aonIconExcel(), AON.CSS.aonContextMenuItem());
						}
						aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					ppp = new PopupPanel(true);
					ppp.add(aggregatedAnnualSummary);
					ppp.setPopupPosition(summaryItem.getAbsoluteLeft() + summaryItem.getOffsetWidth(), summaryItem.getAbsoluteTop());
					ppp.show();
				});
				
				
				
				MenuItem summaryQuarterlyItem = addItem("Resumen Anual Agregado (.xsl, trimestral)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
				summaryQuarterlyItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null && !availableYears.isEmpty()) {
						
						aggregatedAnnualSummary = new ContextMenu();
						for (Integer year : availableYears) {
							aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.QUARTERLY),
									AON.CSS.aonIconExcel(), AON.CSS.aonContextMenuItem());
						}
						aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					ppp = new PopupPanel(true);
					ppp.add(aggregatedAnnualSummary);
					ppp.setPopupPosition(summaryQuarterlyItem.getAbsoluteLeft() + summaryQuarterlyItem.getOffsetWidth(), summaryQuarterlyItem.getAbsoluteTop());
					ppp.show();
				});
				
				
				MenuItem recordItem = addItem("Registro Retributivo (.xsl)", () -> {},
						AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
				
				recordItem.setScheduledCommand(() -> {
					availableYears.clear();
					if (costDocuments != null && costDocuments.getCosts() != null) {
						costDocuments.getCosts()
						.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
					}
					
					if (availableYears != null && !availableYears.isEmpty()) {
						
						remunerationRecord = new ContextMenu();
						for (Integer year : availableYears) {
							remunerationRecord.addItem(String.valueOf(year), () -> printRemunerationRecord(year),
									AON.CSS.aonIconExcel(), AON.CSS.aonContextMenuItem());
						}
						remunerationRecord.ensureDebugId("aggregatedAnnualSummary");
						
					
					}
					
					ppp = new PopupPanel(true);
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
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			salary.ensureDebugId("salary");
			
			extra = addItem(Salary.Type.EXTRA.getDescription(), new ExtraCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			extra.ensureDebugId("extra");
			
			settle = addItem(Salary.Type.SETTLE.getDescription(), new SettleCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			settle.ensureDebugId("settle");
			
			delay = addItem(Salary.Type.DELAY.getDescription(), new DelayCommand(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			delay.ensureDebugId("delay");
			
			addSeparator();
			
			l00 = addItem(Salary.Type.L00.getDescription(), new L00Command(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			l00.ensureDebugId("l00");
			
			l13 = addItem(Salary.Type.L13.getDescription(), new L13Command(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			l13.ensureDebugId("l13");

			l03 = addItem(Salary.Type.L03.getDescription(), new L03Command(), 
					"", AON.AON_ICON_CMD_BUTTON, AON.CSS.aonContextMenuItem());
			
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
	
	DockLayoutPanel dockLayoutPanel;
	FlowPanel mainPanel;
	FullViewer fullPdfViewer;
	
	// ----------------------------------------------- Variables

	private CostDocuments costDocuments;
	
	private List<Listener> listeners;
	
	private AonToolbar toolbar;
	private AonToolbarButton excelBtn;
	private AonToolbarButton pdfBtn;
	private AonToolbarButton publishBtn;
	private AonToolbarButton bidoqBtn;
	private ListBox dateListBox = new ListBox();
//	private AonToolbarButton zoomInBtn;
//	private AonToolbarButton zoomOutBtn;
	private AonToolbarButton seeBtn;
	private AonToolbarButton tgssBtn;
	
	private ExcelMenu excelMenu;
	private SeeMenu seeMenu;
	
	
	// ----------------------------------------------- Constructor
	public Cost() {

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		toolbar = getToolbarPanel();
		mainPanel = new FlowPanel();
		toolbar.add(mainPanel);
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		fullPdfViewer = new FullViewer( ViewerDefaultScale.PAGE_WIDTH );
		dockLayoutPanel.add(fullPdfViewer);
		initWidget(dockLayoutPanel);
		
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

	void onProgressSLD(String message, double progress) {
		for (Listener listener : listeners)
			listener.onProgressSLD(message, progress);
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
		costDocuments.getAsHTML(0, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				fullPdfViewer.open(html);
				//pdfViewer.setDocument(html, zoom/100d);
				syncTypeMenuItems();
			}

			@Override
			public void onFailure(Throwable caught) {}
		});				
	}

	private void syncCostDateListBox() {
		dateListBox.clear();
		List<Date> dates = new ArrayList<>();
		for (com.esferalia.aon.gwt.payroll.shared.Cost cost : costDocuments.getCosts()) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dates.add(date);
		}
		
		// Sort descending
		Collections.sort(dates, Collections.reverseOrder());
		
		dates.forEach(date -> dateListBox.addItem(DATE_FORMAT.format(date)));
		
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
		if (isMenuItemChecked(seeMenu.getL00()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L00.ordinal())));
		if (isMenuItemChecked(seeMenu.getL03()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L03.ordinal())));
		if (isMenuItemChecked(seeMenu.getL13()))
			flowPanel.add(new Hidden(CostExcelService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L13.ordinal())));
		
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
		listeners.forEach(l -> l.onGeneratingDocument());
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "remuneration_record/Registro_Retributivo_" + year);
		
		FormPanel formPanel = new FormPanel(/*"_blank"*/);
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(event -> {
			
			JSONObject json = new JSONObject(JsonUtils.safeEval(event.getResults()));
			JSONValue noSex = json.get("noSex");
			JSONArray noSexArr = new JSONArray(JsonUtils.safeEval(noSex.toString()));
			
			
			for (int i=0; i<noSexArr.size(); i++) {
				
				JSONObject unsexed = new JSONObject(JsonUtils.safeEval(noSexArr.get(i).toString()));
				
				LOGGER.info(unsexed.toString());
				String name = unsexed.get("name").isString().stringValue();
				String nss = unsexed.get("nss").isString().stringValue();
				
				listeners.forEach(l -> l.onNoSex(nss, name));
			}
			String base64Excel = json.get("excel").isString().stringValue();
			
//			if (base64Excel.charAt(0) == '\"' && base64Excel.charAt(base64Excel.length() - 1) == '\"') {
//				base64Excel = base64Excel.substring(1, base64Excel.length() - 1);
//			}
//			
			
			String url = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + base64Excel;
			
			LOGGER.info(url);
			
			Window.open(url, "Registro Retributivo", "");
			
		});
		
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
	
	public void __syncCalcs () {
		
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

	public void syncCalcs () {
		
		com.esferalia.aon.gwt.payroll.shared.Cost cost = costDocuments.geCurrentCost();
		
		StringBuilder requestDataBuilder = new StringBuilder();
		
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser());
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL());
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.DATE.name() + "=" + "01" + "/" + AonStringUtils.leftPad(Integer.toString(cost.getMonth()+1), 2 , "0") + "/" +cost.getYear());

		// Send request to server and catch any errors.
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SISTEMA_RED_URL + "/" + SistemaREDServlet.CALCS);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
		
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				
				if (state == XMLHttpRequest.DONE) {
					onFinishSLD();
					costDocuments.addType(Salary.Type.L00);
					setCheckedStyle(seeMenu.getL00(), true);
					costDocuments.addType(Salary.Type.L13);
					setCheckedStyle(seeMenu.getL13(), true);
					costDocuments.addType(Salary.Type.L03);
					setCheckedStyle(seeMenu.getL03(), true);
					getAsHTML();
				} else if ( state == XMLHttpRequest.LOADING ) {
					try {
						JsArray<JsSistemaREDProgess> jsSistemaREDProgesses = eval("("+ xhr.getResponseText() +"])");
						int last = jsSistemaREDProgesses.length() -1 ;
						JsSistemaREDProgess jsSistemaREDProgess = jsSistemaREDProgesses.get(last);
						onProgressSLD(
								"Sincronizado "
								+ " " + jsSistemaREDProgess.getEmployeeName() 
								+ " ( " + jsSistemaREDProgess.getProgress() 
								+ " de " + jsSistemaREDProgess.getTotal()
								+ " )"
								, jsSistemaREDProgess.getProgress() / jsSistemaREDProgess.getTotal()
								);
						if (jsSistemaREDProgess.getProgress() >=  jsSistemaREDProgess.getTotal() ) {
							onFinishSLD();
							costDocuments.addType(Salary.Type.L00);
							setCheckedStyle(seeMenu.getL00(), true);
							costDocuments.addType(Salary.Type.L13);
							setCheckedStyle(seeMenu.getL13(), true);
							costDocuments.addType(Salary.Type.L03);
							setCheckedStyle(seeMenu.getL03(), true);
							getAsHTML();
						}
					} catch ( Exception e ) {
						
					}
				} 
	
			}
		});
		
		xhr.send(requestDataBuilder.toString());		
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
		
//		zoomOutBtn = new AonToolbarButton( "Reducir", AON.CSS.aonIconZoomOut() );
//		zoomOutBtn.addClickHandler(e -> {
//			onZoomOut(e);
//		});
//		toolbar.add(zoomOutBtn);
		
//		zoomInBtn = new AonToolbarButton( "Ampliar", AON.CSS.aonIconZoomIn() );
//		zoomInBtn.addClickHandler(e -> {
//			onZoomIn(e);
//		});
//		toolbar.add(zoomInBtn);
		
		tgssBtn = new AonToolbarButton( "TGSS", AON.CSS.aonIconTgss() );
		tgssBtn.addClickHandler(e -> {
			onTGSS(e);
		});
		toolbar.add(tgssBtn);

		return toolbar;

	}
	
	// ----------------------------------------------- Toolbar.Methods

	HandlerRegistration handler;
	HandlerRegistration handler2;
	private void onExcel(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		excelMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		excelMenu.show();
		
		NodeList<Element> iframes = Document.get().getElementsByTagName("iframe");
		
		if (iframes != null && iframes.getLength() > 0) {
			IFrameElement iframe = (IFrameElement) Document.get().getElementsByTagName("iframe").getItem(0);
			
			if (iframe != null) {			
				Element outerContainer = iframe.getContentDocument().getElementById("outerContainer");
				
				if (outerContainer != null) {					
					Event.sinkEvents(outerContainer, Event.ONCLICK);
					Event.setEventListener(outerContainer, event -> {
						if (excelMenu != null) {							
							excelMenu.removePpp();
							excelMenu.hide();
						}
					});
				}
				
			}			
		}
		
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

//	private void onZoomIn(ClickEvent e) {
//		zoom = Math.max(MIN_ZOOM, zoom + ZOOM_STEP);
//		getAsHTML();
//	}
//
//	private void onZoomOut(ClickEvent e) {
//		zoom = Math.min(MAX_ZOOM, zoom - ZOOM_STEP);
//		getAsHTML();
//	}
	
	private void onView(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		seeMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		seeMenu.show();
	}

	private void onTGSS(ClickEvent e) {
		syncCalcs();
	}
	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;
}
