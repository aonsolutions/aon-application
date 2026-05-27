 
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.ExcelType.COMPLETE;
import static com.esferalia.aon.gwt.payroll.shared.ExcelType.SUMMARY;
import static com.esferalia.aon.gwt.payroll.shared.SistemaREDService.SISTEMA_RED_URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params;
import com.esferalia.aon.gwt.payroll.shared.CostExcelService;
import com.esferalia.aon.gwt.payroll.shared.CostParams;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterprisePayrollPDFService;
import com.esferalia.aon.gwt.payroll.shared.ExcelType;
import com.esferalia.aon.gwt.payroll.shared.RemunerationRecordService;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDProgess;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class CostWidget extends AonCustomDockLayout {
	
	// ----------------------------------------------- Static Variables 

	private static final Logger LOGGER = Logger.getLogger(CostWidget.class.getName());
	
	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH);

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
	
	class SummaryCommand implements ScheduledCommand {

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
			
			excel = addItem("Microsoft Excel (.xls)", new ExcelCommand(), AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			excel.ensureDebugId("excel");
			
			excelComplete = addItem("Microsoft Excel (.xls, detallado)", new ExcelCompleteCommand(), AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			excelComplete.ensureDebugId("excelComplete");
			
			csv = addItem("Valores separados por comas (.csv)", new CSVCCommand(), AON.CSS.aonIconExcel(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			csv.ensureDebugId("csv");
			
			addSeparator();
			
			MenuItem summaryItem = addItem("Resumen Anual Agregado (.xsl, mensual)", () -> {}, AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			summaryItem.setScheduledCommand(() -> {
				availableYears.clear();
				if (costs != null) {
					costs.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
				}
				
				if (availableYears != null && !availableYears.isEmpty()) {
					
					aggregatedAnnualSummary = new ContextMenu();
					for (Integer year : availableYears)
						aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.MONTHLY), AON.CSS.aonIconExcel(), AON.CSS.aonCmdItem());
					
					aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
					
				}
				
				ppp = new PopupPanel(true);
				ppp.add(aggregatedAnnualSummary);
				ppp.setPopupPosition(summaryItem.getAbsoluteLeft() + summaryItem.getOffsetWidth(), summaryItem.getAbsoluteTop());
				ppp.show();
			});
				
				
				
			MenuItem summaryQuarterlyItem = addItem("Resumen Anual Agregado (.xsl, trimestral)", () -> {}, AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			summaryQuarterlyItem.setScheduledCommand(() -> {
				availableYears.clear();
				if (costs != null) {
					costs.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
				}
				
				if (availableYears != null && !availableYears.isEmpty()) {
					
					aggregatedAnnualSummary = new ContextMenu();
					for (Integer year : availableYears) 
						aggregatedAnnualSummary.addItem(String.valueOf(year), () -> printAggregatedAnnualSummary(year, AggregatedAnnualSummaryService.SummaryType.QUARTERLY), AON.CSS.aonIconExcel(), AON.CSS.aonCmdItem());
					
					aggregatedAnnualSummary.ensureDebugId("aggregatedAnnualSummary");
					
				}
				
				ppp = new PopupPanel(true);
				ppp.add(aggregatedAnnualSummary);
				ppp.setPopupPosition(summaryQuarterlyItem.getAbsoluteLeft() + summaryQuarterlyItem.getOffsetWidth(), summaryQuarterlyItem.getAbsoluteTop());
				ppp.show();
			});
				
				
			MenuItem recordItem = addItem("Registro Retributivo (.xsl)", () -> {}, AON.CSS.aonIconRight(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			recordItem.setScheduledCommand(() -> {
				availableYears.clear();
				if (costs != null) {
					costs.stream()
						.map(c -> c.getYear())
						.sorted(Comparator.reverseOrder())
						.forEach(y -> availableYears.add(y));
				}
				
				if (availableYears != null && !availableYears.isEmpty()) {
					
					remunerationRecord = new ContextMenu();
					for (Integer year : availableYears) 
						remunerationRecord.addItem(String.valueOf(year), () -> printRemunerationRecord(year), AON.CSS.aonIconExcel(), AON.CSS.aonCmdItem());
					
					remunerationRecord.ensureDebugId("aggregatedAnnualSummary");
					
				}
				
				ppp = new PopupPanel(true);
				ppp.add(remunerationRecord);
				ppp.setPopupPosition(recordItem.getAbsoluteLeft() + recordItem.getOffsetWidth(), recordItem.getAbsoluteTop());
				ppp.show();
			});
			
		}	
	}
	
	// ----------------------------------------------- UiFields
	
	private AonToolbarButton bidoqBtn;
	
	private AonCustomListBox period = new AonCustomListBox("Tipo Periodo");
	private AonCustomListBox period2 = new AonCustomListBox("Periodo");
	
	private HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomListBox start = new AonCustomListBox("Desde");
	private AonCustomListBox end = new AonCustomListBox("Hasta");
	
	private AonCustomListBox detail = new AonCustomListBox("Detalle");
	private AonCustomListBox workplace = new AonCustomListBox("Centro Trabajo");
	
	private AonCustomMultiSelectBox salaryType = new AonCustomMultiSelectBox("Tipo Recibo");
	
	private HTMLPanel container = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private FullViewer fullPdfViewer = new FullViewer();
	private ExcelMenu excelMenu;
	
	// ----------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private DomainUserRoles dur;
	
	private CostParams params;
	private Enterprise enterpriseId;
	private Workplace workplaceId;

//	private CostDocuments costDocuments;
	private List<Cost> costs;
	private List<Workplace> workplaces;
	
	// ----------------------------------------------- Constructor
	
	public CostWidget() {
		super("Costes");
		
		excelMenu = new ExcelMenu();
		params = new CostParams();
		
		hideToolbarFilterMessages();
//		showSeachButton();
		
		addButtonsToolbar();
		
		period.addItem("Mensual");
        period.addItem("Personalizado");
		period.getListBox().setSelectedIndex(0);
        period.addChangeHandler(e -> {
        	period2.setVisible(period.getListBox().getSelectedIndex() == 0);
        	datesPanel.setVisible(period.getListBox().getSelectedIndex() == 1);
//        	start.setVisible(period.getListBox().getSelectedIndex() == 1);
//        	end.setVisible(period.getListBox().getSelectedIndex() == 1);
        	onSearch();
        });
		addFilterWidget(period);
		
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		start.addChangeHandler(e -> onSearch());
		end.addChangeHandler(e -> onSearch());
		
//		start.setVisible(false);
//		end.setVisible(false);
//		addFilterWidget(start);
//		addFilterWidget(end);
		
		datesPanel.add(start);
		datesPanel.add(end);
		datesPanel.setVisible(false);
		

		insertWidgetAfterSearchButton(datesPanel);
		insertWidgetAfterSearchButton(period2);
//		addFilterWidget(period2);
		period2.addChangeHandler(e -> onSearch());
		
		detail.clearItems();
		detail.addItem("Agrupar Centro Trabajo", "true");
		detail.addItem("Con trabajadores", "false");
		detail.setValue("false");
		addFilterWidget(detail);
		detail.addChangeHandler(e -> onSearch());
		
		workplace.clearItems();
		workplace.addItem("Todos", "");
		workplace.setVisible(false);
		addFilterWidget(workplace);
		workplace.addChangeHandler(e -> {
			workplaceId = AonStringUtils.isBlank(workplace.getValue()) ? new Workplace() : workplaces.stream().filter(wp -> wp.getId() == Integer.parseInt(workplace.getValue())).findFirst().get();
			onSearch();
		});
		
		// salaryType
		Set<String> salaryOptions = new LinkedHashSet<String>();
		salaryOptions.add("Nomina");
		salaryOptions.add("Extra");
		salaryOptions.add("Finiquito");
		salaryOptions.add("Atrasos");
		salaryOptions.add("Tramitaci\u00f3n");
		salaryOptions.add("L00");
		salaryOptions.add("L02");
		salaryOptions.add("L03");
		salaryOptions.add("L13");
		salaryType.setOptions(salaryOptions);
		salaryType.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
            	onSearch();
            }
        });
		// Select all salary types by default
		salaryType.setSelectedOptions(salaryOptions);
		addFilterWidget(salaryType);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);
		
		container.add(fullPdfViewer);
		
		add(container);
	}
	
	private void addButtonsToolbar() {
		AonExpandButton excelBtn = new AonExpandButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel()) {
			
			@Override
			public void onExpandClick(ClickEvent e) {
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
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				printExcel(SUMMARY);
			}
		};
		
		AonToolbarButton pdfBtn = new AonToolbarButton(AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		pdfBtn.addClickHandler(e -> printPDF());
		
		AonToolbarButton tgssBtn = new AonToolbarButton("C\u00e1lculos del SISTEMA RED ( Remesas SLD, Sistema de Liquidaci\u00f3n Directa )", AON.CSS.aonIconTgss());
		tgssBtn.addClickHandler(e -> syncCalcs());
		
		bidoqBtn = new AonToolbarButton("Publicar Bidoq",  "aon-icon-bidoq");
		bidoqBtn.setVisible(false);
		
		addToolbarButton(excelBtn);
		addToolbarButton(pdfBtn);
		addToolbarButton(bidoqBtn);
		addToolbarButton(tgssBtn);
	}
	
	@Override
	protected void onClearFilter() {
		Set<String> salaryOptions = new LinkedHashSet<String>();
		salaryOptions.add("Nomina");
		salaryOptions.add("Extra");
		salaryOptions.add("Finiquito");
		salaryOptions.add("Atrasos");
		salaryOptions.add("L00");
		salaryOptions.add("L03");
		salaryOptions.add("L13");
		salaryType.setSelectedOptions(salaryOptions);
		
		period.getListBox().setSelectedIndex(0);
		period2.getListBox().setSelectedIndex(0);
		
		start.getListBox().setSelectedIndex(0);
		end.getListBox().setSelectedIndex(0);
		
		detail.setValue("false");
		
		if(workplace.isVisible()) {
			workplace.setValue("");
			workplace.getListBox().fireEvent(new com.google.gwt.event.dom.client.ChangeEvent() {});
		} else
			period.getListBox().fireEvent(new com.google.gwt.event.dom.client.ChangeEvent() {});
	}
	
	private void onSearch() {
		getWidgetParams();
		service.getCostReceiptHTML(params, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				fullPdfViewer.open(html);
			}

			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void getWidgetParams() {
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		
		this.params = new CostParams()
				.setStart(startDate)
				.setEnd(endDate)
				.setSalary(salaryType.getSelectedOptions().contains("Nomina"))
				.setExtra(salaryType.getSelectedOptions().contains("Extra"))
				.setSettle(salaryType.getSelectedOptions().contains("Finiquito"))
				.setDelay(salaryType.getSelectedOptions().contains("Atrasos"))
				.setProcedural(salaryType.getSelectedOptions().contains("Tramitaci\u00f3n"))
				.setL00(salaryType.getSelectedOptions().contains("L00"))
				.setL02(salaryType.getSelectedOptions().contains("L02"))
				.setL03(salaryType.getSelectedOptions().contains("L03"))
				.setL13(salaryType.getSelectedOptions().contains("L13"))
				.setGroupByWorkplace(Boolean.valueOf(detail.getValue()))
				
				.setEnterprise(enterpriseId.getId())
				.setWorkplace(workplaceId.getId())
				;
	}
	
	// ----------------------------------------------- Cost.Methods
	
	private void onPublish(Enterprise enterprise, Workplace workplace, int month, int year, String type) {
		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.MONTH + "=" + month);
		requestDataBuffer.append("&" + ShareService.YEAR + "=" + year);
		if (null != workplace)
			requestDataBuffer.append("&" + ShareService.WORKPLACE + "=" + workplace.getId());
		else
			requestDataBuffer.append("&" + ShareService.ENTERPRISE + "=" + enterprise.getId());
		requestDataBuffer.append("&type=" + type);
		requestDataBuffer.append("&enterpriseName=" + enterprise.getName());
		if (null != workplace && AonStringUtils.isNotBlank(workplace.getDescription()))
			requestDataBuffer.append("&workplaceName=" + workplace.getDescription());
		// Send request to server and catch any errors.
		share(requestDataBuffer.toString());
	}
	
	private void share(String requestData) {
		// Send request to server and catch any errors.
		String SHARE_URL = URL.encode(GWT.getModuleBaseURL() + "share");

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SHARE_URL);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {

					String text = xhr.getResponseText();

					try {
						for (JsShareResult result = read(text); text != null; result = read(text))
							AonMessagePanel.showInfo(messagePanel, result.getDescription());
					} catch (IndexOutOfBoundsException e) {}
				}
			}

			private JsShareResult read(String text) {
				for (int begin = loaded; begin < text.length(); begin++) {
					if (text.charAt(begin) == '{') {
						loaded = findEnd(text, begin + 1) + 1;
						String json = text.substring(begin, loaded);
						return JsonUtils.safeEval(json);
					}
				}
				throw new IndexOutOfBoundsException();
			}

			private int findEnd(String text, int start) {
				for (int end = start; end < text.length(); end++) {
					switch (text.charAt(end)) {
					case '}':
						return end;
					case '{':
						end = findEnd(text, end + 1);
					}
				}
				throw new IndexOutOfBoundsException();
			}

		});

		xhr.send(requestData);

	}

	// ----------------------------------------------- Export Methods
	
	private void printExcel (ExcelType excelType) {
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_excel/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(CostExcelService.Params.MONTH.getName()
				, String.valueOf(startDate.getMonth())));
		flowPanel.add(new Hidden(CostExcelService.Params.YEAR.getName()
				, String.valueOf((startDate.getYear() + 1900))));
		
		flowPanel.add(new Hidden(CostExcelService.Params.MONTH.getName() + "End"
				, String.valueOf(endDate.getMonth())));
		flowPanel.add(new Hidden(CostExcelService.Params.YEAR.getName()+ "End"
				, String.valueOf((endDate.getYear() + 1900))));
		
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.ENTERPRISE.getName()
				, null == this.enterpriseId.getId() ? "" : String.valueOf(this.enterpriseId.getId())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.WORKPLACE.getName()
				, null == this.workplaceId.getId() ? "" : String.valueOf(this.workplaceId.getId())));
		flowPanel.add(new Hidden(CostExcelService.Params.EXCEL_TYPE.getName()
				, excelType.name()));
		flowPanel.add(new Hidden(CostExcelService.Params.DOMAIN.getName()
				, Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(CostExcelService.Params.USER.getName()
				, Wnd.getCurrentUser()));
		
		if (salaryType.getSelectedOptions().contains("Nomina"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SALARY.ordinal())));
		if (salaryType.getSelectedOptions().contains("Extra"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (salaryType.getSelectedOptions().contains("Finiquito"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (salaryType.getSelectedOptions().contains("Atrasos"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.DELAY.ordinal())));

		if (salaryType.getSelectedOptions().contains("L00"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L00.ordinal())));
		if (salaryType.getSelectedOptions().contains("L13"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L13.ordinal())));
		if (salaryType.getSelectedOptions().contains("L03"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L03.ordinal())));
		
		flowPanel.add(new Hidden("groupByWorkplace", detail.getValue()));
		
		flowPanel.add(new Hidden("enterpriseName", this.enterpriseId.getName()));
		if (null != workplaceId && AonStringUtils.isNotBlank(workplaceId.getDescription()))
			flowPanel.add(new Hidden("workplaceName", workplaceId.getDescription()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			container.remove(formPanel);
		});
		
		container.add(formPanel);
		
		formPanel.submit();
		
	}
	
	private void printCSV () {
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_csv/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(Params.MONTH.getName(), String.valueOf(startDate.getMonth())));
		flowPanel.add(new Hidden(Params.YEAR.getName(), String.valueOf((startDate.getYear() + 1900))));
		flowPanel.add(new Hidden(Params.MONTH.getName() + "End", String.valueOf(endDate.getMonth())));
		flowPanel.add(new Hidden(Params.YEAR.getName() + "End", String.valueOf((endDate.getYear() + 1900))));
		flowPanel.add(new Hidden(Params.ENTERPRISE.getName(), null == this.enterpriseId.getId() ? "" : String.valueOf(this.enterpriseId.getId())));
		flowPanel.add(new Hidden(Params.WORKPLACE.getName(),null == this.workplaceId.getId() ? "" : String.valueOf(this.workplaceId.getId())));
		flowPanel.add(new Hidden(Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(Params.USER.getName(), Wnd.getCurrentUser()));
		
		if (salaryType.getSelectedOptions().contains("Nomina"))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.SALARY.ordinal())));
		if (salaryType.getSelectedOptions().contains("Extra"))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (salaryType.getSelectedOptions().contains("Finiquito"))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (salaryType.getSelectedOptions().contains("Atrasos"))
			flowPanel.add(new Hidden(Params.FILTER.getName(), String.valueOf(Salary.Type.DELAY.ordinal())));
		
		flowPanel.add(new Hidden("enterpriseName", this.enterpriseId.getName()));
		if (null != workplaceId && AonStringUtils.isNotBlank(workplaceId.getDescription()))
			flowPanel.add(new Hidden("workplaceName", workplaceId.getDescription()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			container.remove(formPanel);
		});
		
		container.add(formPanel);
		
		formPanel.submit();
		
	}
	
	public void printPDF () {
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_pdf/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.MONTH.getName()
				, String.valueOf(startDate.getMonth())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.YEAR.getName()
				, String.valueOf((startDate.getYear() + 1900))));
		
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.MONTH.getName() + "End"
				, String.valueOf(endDate.getMonth())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.YEAR.getName()+ "End"
				, String.valueOf((endDate.getYear() + 1900))));
		
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.ENTERPRISE.getName()
				, null == this.enterpriseId.getId() ? "" : String.valueOf(this.enterpriseId.getId())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.WORKPLACE.getName()
				, null == this.workplaceId.getId() ? "" : String.valueOf(this.workplaceId.getId())));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.DOMAIN.getName()
				, Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.USER.getName()
				, Wnd.getCurrentUser()));
		
		if (salaryType.getSelectedOptions().contains("Nomina"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SALARY.ordinal())));
		if (salaryType.getSelectedOptions().contains("Extra"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.EXTRA.ordinal())));
		if (salaryType.getSelectedOptions().contains("Finiquito"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.SETTLE.ordinal())));
		if (salaryType.getSelectedOptions().contains("Atrasos"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.DELAY.ordinal())));

		if (salaryType.getSelectedOptions().contains("L00"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L00.ordinal())));
		if (salaryType.getSelectedOptions().contains("L13"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L13.ordinal())));
		if (salaryType.getSelectedOptions().contains("L03"))
			flowPanel.add(new Hidden(EnterprisePayrollPDFService.Params.FILTER.getName()
					, String.valueOf(Salary.Type.L03.ordinal())));
		
		flowPanel.add(new Hidden("groupByWorkplace", detail.getValue()));
		
		flowPanel.add(new Hidden("enterpriseName", this.enterpriseId.getName()));
		if (null != workplaceId && AonStringUtils.isNotBlank(workplaceId.getDescription()))
			flowPanel.add(new Hidden("workplaceName", workplaceId.getDescription()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			container.remove(formPanel);
		});
		
		container.add(formPanel);
		
		formPanel.submit();
	}
	public void printAggregatedAnnualSummary (Integer year, AggregatedAnnualSummaryService.SummaryType type) {
		String printURL = URL.encode(GWT.getModuleBaseURL() + "AggregatedAnnualSummary/Resumen_Anual_Agregado_" + year);
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.YEAR.getName(), String.valueOf(year)));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.ENTERPRISE.getName(), null == this.enterpriseId.getId() ? "" : String.valueOf(this.enterpriseId.getId())));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.WORKPLACE.getName(), null == this.workplaceId.getId() ? "" : String.valueOf(this.workplaceId.getId())));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.COMPLETE.getName(), "true"));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.USER.getName(), Wnd.getCurrentUser()));
		flowPanel.add(new Hidden(AggregatedAnnualSummaryService.Params.TYPE.getName(), type.name()));
		flowPanel.add(new Hidden("enterpriseName", this.enterpriseId.getName()));
		if (null != workplaceId && AonStringUtils.isNotBlank(workplaceId.getDescription()))
			flowPanel.add(new Hidden("workplaceName", workplaceId.getDescription()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			container.remove(formPanel);
		});
		
		container.add(formPanel);
		
		formPanel.submit();
	}
	
	public void printRemunerationRecord (Integer year) {
		AonMessagePanel.showLoading(messagePanel, "Se est\u00E1 generando su informe. Por favor, espere unos segundos...");
		
		String printURL = URL.encode(GWT.getModuleBaseURL() + "remuneration_record/Registro_Retributivo_" + year);
		
		FormPanel formPanel = new FormPanel(/*"_blank"*/);
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(event -> {
			
			JSONObject json = new JSONObject(JsonUtils.safeEval(event.getResults()));
			JSONValue noSex = json.get("noSex");
			JSONArray noSexArr = new JSONArray(JsonUtils.safeEval(noSex.toString()));
			
			if(noSexArr.size() > 0) {
				HTMLPanel messagePanelNoSex = new HTMLPanel(AonStringUtils.EMPTY);
				String message = "";
				
				for (int i=0; i<noSexArr.size(); i++) {
					JSONObject unsexed = new JSONObject(JsonUtils.safeEval(noSexArr.get(i).toString()));
					
					LOGGER.info(unsexed.toString());
					String name = unsexed.get("name").isString().stringValue();
					String nss = unsexed.get("nss").isString().stringValue();
					
					message += "ADVERTENCIA: Sexo no definido - NAF: " + nss + ", Nombre: "+ name + "<br>";
				}
				
				messagePanelNoSex = new HTMLPanel(message);
				new AonDialog("Registro Retributivo", messagePanelNoSex).warning();
				
			}
			
			String base64Excel = json.get("excel").isString().stringValue();
			
			String url = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + base64Excel;
			
			LOGGER.info(url);
			
			AonMessagePanel.hideMessage(messagePanel);
			
			Window.open(url, "Registro Retributivo", "");
			
			container.remove(formPanel);
		});
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(RemunerationRecordService.Params.YEAR.getName(), String.valueOf(year)));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.ENTERPRISE.getName(), null == this.enterpriseId.getId() ? "" : String.valueOf(this.enterpriseId.getId())));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(RemunerationRecordService.Params.USER.getName(), Wnd.getCurrentUser()));
		flowPanel.add(new Hidden("enterpriseName", this.enterpriseId.getName()));
		if (null != workplaceId && AonStringUtils.isNotBlank(workplaceId.getDescription()))
			flowPanel.add(new Hidden("workplaceName", workplaceId.getDescription()));
		
		formPanel.add(flowPanel);
		
		container.add(formPanel);
		
		formPanel.submit();
	}

	public void syncCalcs () {
		AonMessagePanel.showLoading(messagePanel, "Consultando C\u00e1lculos del SISTEMA RED ( Remesas SLD, Sistema de Liquidaci\u00f3n Directa )");
		
		StringBuilder requestDataBuilder = new StringBuilder();
		
		Date date = getStartDate();
		
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser());
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL());
		requestDataBuilder
		.append("&" + SistemaREDService.Parameter.DATE.name() + "=" + "01" + "/" + AonStringUtils.leftPad(Integer.toString(date.getMonth()+1), 2 , "0") + "/" + (date.getYear() + 1900));

		// Send request to server and catch any errors.
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SISTEMA_RED_URL + "/" + SistemaREDService.CALCS);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
		
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				
				if (state == XMLHttpRequest.DONE) {
					AonMessagePanel.hideMessage(messagePanel);
					onSearch();
				} else if ( state == XMLHttpRequest.LOADING ) {
					try {
						JsArray<JsSistemaREDProgess> jsSistemaREDProgesses = eval("("+ xhr.getResponseText() +"])");
						int last = jsSistemaREDProgesses.length() -1 ;
						JsSistemaREDProgess jsSistemaREDProgess = jsSistemaREDProgesses.get(last);
						AonMessagePanel.showLoading(messagePanel, 
							"Sincronizado "
									+ " " + jsSistemaREDProgess.getEmployeeName() 
									+ " ( " + jsSistemaREDProgess.getProgress() 
									+ " de " + jsSistemaREDProgess.getTotal()
									+ " ) " +  jsSistemaREDProgess.getProgress() / jsSistemaREDProgess.getTotal() + "%"
						);
						
						if (jsSistemaREDProgess.getProgress() >=  jsSistemaREDProgess.getTotal() ) {
							AonMessagePanel.hideMessage(messagePanel);
							onSearch();
						}
					} catch ( Exception e ) {
						
					}
				} 
	
			}
		});
		
		xhr.send(requestDataBuilder.toString());
	}
	
	public void setWorkplace(Enterprise enterprise, Workplace workplace) {
		this.enterpriseId = enterprise;
		this.workplaceId = workplace;
		employeesService.getWorkplaceCosts(this.workplaceId.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}

			@Override
			public void onSuccess(List<Cost> costsDb) {
				costs = costsDb;
				fillPeriods();
				
				service.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
					
					@Override
					public void onSuccess(DomainUserRoles durDb) {
						dur = durDb;
						if(null != dur && dur.isBidoq()){
							bidoqBtn.setVisible(null != dur && dur.isBidoq());
							bidoqBtn.setEnabled(null != dur && dur.isBidoq());
				    	}
						onSearch();
					}

					@Override
					public void onFailure(Throwable caught) {}
					
				});
			}
		});
	}
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterpriseId = enterprise;
		this.workplaceId = new Workplace();
		employeesService.getEnterpriseCosts(this.enterpriseId.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}

			@Override
			public void onSuccess(List<Cost> costsDb) {
				costs = costsDb;
				fillPeriods();
				
				service.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
					
					@Override
					public void onSuccess(DomainUserRoles durDb) {
						dur = durDb;
						if(null != dur && dur.isBidoq()){
							bidoqBtn.setVisible(null != dur && dur.isBidoq());
							bidoqBtn.setEnabled(null != dur && dur.isBidoq());
				    	}
						
						service.getWorkplaces(new AsyncCallback<List<Workplace>>() {
							
							@Override
							public void onSuccess(List<Workplace> workplacesDb) {
								workplaces = workplacesDb;
								
								workplace.clearItems();
								workplace.addItem("Todos", "");
								workplaces.stream().forEach(workplaceIt -> workplace.addItem(workplaceIt.getDescription(), workplaceIt.getId().toString()));
								workplace.setVisible(true);
								onSearch();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
							
						});
						
						
					}

					@Override
					public void onFailure(Throwable caught) {}
					
				});
			}
		});
	}

	private void fillPeriods() {
		List<Date> dates = new ArrayList<>();
		for (Cost cost : costs) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dates.add(date);
		}
		
		// Sort descending
		Collections.sort(dates, Collections.reverseOrder());
		
		period2.clearItems();
		start.clearItems();
		end.clearItems();
		
		dates.forEach(date -> period2.addItem(DATE_FORMAT.format(date)));
		dates.forEach(date -> start.addItem(DATE_FORMAT.format(date)));
		dates.forEach(date -> end.addItem(DATE_FORMAT.format(date)));
	}
	
	// ----------------------------------------------- Toolbar.Methods

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
	
	private Date getStartDate() {
		if(period.getListBox().getSelectedIndex() == 0) {
			return  DateUtils.getFirstDayOfMonth(DATE_FORMAT.parse(period2.getValue()));
		} else {
			return  DateUtils.getFirstDayOfMonth(DATE_FORMAT.parse(start.getValue()));
		}
	}
	
	private Date getEndDate() {
		if(period.getListBox().getSelectedIndex() == 0) {
			return  DateUtils.getLastDayOfMonth(DATE_FORMAT.parse(period2.getValue()));
		} else {
			return  DateUtils.getLastDayOfMonth(DATE_FORMAT.parse(end.getValue()));
		}
	}
	
	private void onPDF(ClickEvent e) {
		printPDF();
	}
	
	private void onBidoq(ClickEvent e) {
		Date date = getStartDate();
		
		onPublish(this.enterpriseId, this.workplaceId, date.getMonth(), date.getYear() + 1900, "bidoq");
	}
	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

}
