package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.ExcelType.COMPLETE;
import static com.esferalia.aon.gwt.payroll.shared.ExcelType.SUMMARY;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ExcelType;
import com.esferalia.aon.gwt.payroll.shared.Salary;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

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
		
		public ExcelMenu() {
			
			excel = addItem("Microsoft Excel (.xls)", new ExcelCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			excel.ensureDebugId("excel");
			
			excelComplete = addItem("Microsoft Excel (.xls, detallado)", new ExcelCompleteCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			excelComplete.ensureDebugId("excelComplete");
			
			csv = addItem("Valores separados por comas (.csv)", new CSVCCommand(), 
					AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			csv.ensureDebugId("csv");
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
	
	class SeeMenu extends ContextMenu {
				
		private MenuItem salary = null;
		private MenuItem extra = null;
		private MenuItem settle = null;
		private MenuItem delay = null;
		
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
	
	private ExcelMenu excelMenu;
	private SeeMenu seeMenu;
	
	// ----------------------------------------------- Constructor
	public Cost() {
		toolbar = getToolbarPanel();

		initWidget(binder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		excelMenu = new ExcelMenu();
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
		
		listeners = new LinkedList<Listener>(); 

//		publishBtn.setVisible(!Wnd.getCurrentDomainNameURL().contains("ayudat"));
		bidoqBtn.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));
		
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
		flowPanel.add(new Hidden("month", String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden("year", String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden("enterpriseId", String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden("workplaceId", String.valueOf(cost.getWorkplaceId())));
		flowPanel.add(new Hidden("excelType", excelType.name()));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.DELAY.ordinal())));
		
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
		flowPanel.add(new Hidden("month", String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden("year", String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden("enterpriseId", String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden("workplaceId", String.valueOf(cost.getWorkplaceId())));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.DELAY.ordinal())));
		
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
		flowPanel.add(new Hidden("month", String.valueOf(cost.getMonth())));
		flowPanel.add(new Hidden("year", String.valueOf(cost.getYear())));
		flowPanel.add(new Hidden("enterpriseId", String.valueOf(cost.getEnterpriseId())));
		flowPanel.add(new Hidden("workplaceId", String.valueOf(cost.getWorkplaceId())));
		
		if (isMenuItemChecked(seeMenu.getSalary()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SALARY.ordinal())));
		if (isMenuItemChecked(seeMenu.getExtra()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (isMenuItemChecked(seeMenu.getSettle()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (isMenuItemChecked(seeMenu.getDelay()))
			flowPanel.add(new Hidden("filter", String.valueOf(Salary.Type.DELAY.ordinal())));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainPanel.remove(formPanel);
		});
		
		mainPanel.add(formPanel);
		
		formPanel.submit();
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

}
