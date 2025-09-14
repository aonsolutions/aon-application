package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class ActivitySummary extends AonCustomDockLayout {

	// ----- LOGGER

	private static final Logger LOGGER = Logger.getLogger(ActivitySummary.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	// ----- DockLayoutPanel

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private SimpleLayoutPanel centerPanel;

	private AonCustomListBox period = new AonCustomListBox("Periodo");
	private AonCustomDateBox start = new AonCustomDateBox("Desde");
	private AonCustomDateBox end = new AonCustomDateBox("Hasta");

	private AonCustomMultiSelectBox contractType = new AonCustomMultiSelectBox("Contratos");
	private AonCustomMultiSelectBox salaryType = new AonCustomMultiSelectBox("Tipo Recibo");
	private AonCustomMultiSelectBox itType = new AonCustomMultiSelectBox("Tipo IT");

	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");

	private AonToolbarButton excelExport;

	// ----- Variables Tabla

	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;

	private ActivitySummaryParams params;

	private static enum ENTERPRISES_COLS {
		DES(AON.MSG.description(), "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ALT("Ini. Contrato", "7rem", "text-align: center;"), BAJ("Fin Contrato", "7rem", "text-align: center;"),
		SAL("Nominas", "5rem", "text-align: center;"), EXT("Extras", "5rem", "text-align: center;"),
		SET("Finiquitos", "5rem", "text-align: center;"), DEL("Atrasos", "5rem", "text-align: center;"),
		ECA("IT EC/AN", "5rem", "text-align: center;"), ATE("IT AT/EP", "5rem", "text-align: center;"),
		MPP("IT M/P", "5rem", "text-align: center;"), OTH("IT Otros", "5rem", "text-align: center;"),
		BUT("", "3rem", "");

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private ENTERPRISES_COLS(String headerLabel, String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private ENTERPRISES_COLS(String headerLabel, String colWidth, String cellStyleClass) {
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

	// ----- Variables

	private final DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static CommonServiceAsync COMMON_SERVICE;

	private Domain domain = null;

	private CustomerApi customerApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = false;

	private String user;

	// ----- Constructor

	public ActivitySummary(String user) {
		super("Resumen Actividad");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.customerApi = new CustomerApi(SESSION_API);
		this.user = user;

		addButtonsToolbar();

		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por nombre ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if (AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if (AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});

		period.addItem("Mes actual");
		period.addItem("Mes anterior");
		period.addItem("\u00daltimo trimestre");
		period.addItem("\u00daltimo cuatrimestre");
		period.addItem("\u00daltimo a\u00f1o");
		period.addItem("Personalizado");
		period.addChangeHandler(e -> {
			updateDates();
			onSearch();
		});
		period.getListBox().setSelectedIndex(0);
		addFilterWidget(period);

		HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		start.setValue(DateUtils.getFirstDayOfMonth());
		end.setValue(DateUtils.getLastDayOfMonth());
		start.addValueChangeHandler(e -> onSearch());
		end.addValueChangeHandler(e -> onSearch());
		datesPanel.add(start);
		datesPanel.add(end);
		addFilterWidget(datesPanel);

		// contractType
		Set<String> contractOptions = new LinkedHashSet<String>();
		contractOptions.add("Altas");
		contractOptions.add("Bajas");
		contractType.setOptions(contractOptions);
		contractType.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				onSearch();
			}
		});
		addFilterWidget(contractType);

		// salaryType
		Set<String> salaryOptions = new LinkedHashSet<String>();
		salaryOptions.add("Nomina");
		salaryOptions.add("Extra");
		salaryOptions.add("Finiquito");
		salaryOptions.add("Atrasos");
		salaryType.setOptions(salaryOptions);
		salaryType.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				onSearch();
			}
		});
		salaryType.setSelectedOptions(salaryOptions);
		addFilterWidget(salaryType);

		// itType
		Set<String> itOptions = new LinkedHashSet<String>();
		itOptions.add("IT EC/AN");
		itOptions.add("IT AT/EP");
		itOptions.add("IT M/P");
		itOptions.add("IT Otros");
		itType.setOptions(itOptions);
		itType.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				onSearch();
			}
		});
		itType.setSelectedOptions(itOptions);
		addFilterWidget(itType);

		sort.addItem("Nombre", "name");
		sort.addItem("Alta", "start");
		sort.addItem("Bajas", "end");
		sort.addItem("Nomina", "salary");
		sort.addItem("Extra", "extra");
		sort.addItem("Finiquito", "settle");
		sort.addItem("Atrasos", "delay");
		sort.addItem("IT EC/AN", "it_ecan");
		sort.addItem("IT AT/EP", "it_atep");
		sort.addItem("IT M/P", "it_mp");
		sort.addItem("IT Otros", "it_other");
		sort.getListBox().addChangeHandler(event -> onSearch());

		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());

		addSortWidget(sort);
		addSortWidget(asc);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);

		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin", "0 1rem 1rem");

		container.add(centerPanel);

		add(container);
	}

	public void showCustomerDomainInfo(Integer customerId, String customerName, Domain customerDomain) {
		centerPanel.clear();
		AonMessagePanel.hideMessage(messagePanel);

		this.domain = customerDomain;

		getToolbar().setTitle("Act. Laboral " + customerName);

		onSearch();
	}

	private void showEmptyInfo() {
		centerPanel.clear();
		FlowPanel line = new FlowPanel();
		InlineLabel label = new InlineLabel(AON.MSG.noData());
		line.add(label);
		centerPanel.clear();
		centerPanel.add(line);
	}

	private void updateDates() {
		int selectedIndex = period.getListBox().getSelectedIndex();
		Date startDate;
		Date endDate = DateUtils.getLastDayOfMonth();

		switch (selectedIndex) {
		case 0: // Mes actual
			startDate = DateUtils.getFirstDayOfMonth();
			break;
		case 1: // Mes anterior
			startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -1));
			endDate = DateUtils.getLastDayOfMonth(DateUtils.addMonths2Date(new Date(), -1));
			break;
		case 2: // Último trimestre
			startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -3));
			break;
		case 3: // Último cuatrimestre
			startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -4));
			break;
		case 4: // Último año
			startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -12));
			break;
		case 5: // Personalizado
			startDate = DateUtils.getFirstDayOfMonth();
			break;
		default:
			startDate = DateUtils.getFirstDayOfMonth();
			break;
		}

		start.setValue(startDate);
		end.setValue(endDate);

		start.setEnable(selectedIndex > 4);
		end.setEnable(selectedIndex > 4);
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		start.setValue(DateUtils.getFirstDayOfMonth());
		end.setValue(DateUtils.getLastDayOfMonth());
		contractType.setSelectedOptions(Collections.emptySet());

		Set<String> salaryOptions = new LinkedHashSet<String>();
		salaryOptions.add("Nomina");
		salaryOptions.add("Extra");
		salaryOptions.add("Finiquito");
		salaryOptions.add("Atrasos");
		salaryType.setSelectedOptions(salaryOptions);

		Set<String> itOptions = new LinkedHashSet<String>();
		itOptions.add("IT EC/AN");
		itOptions.add("IT AT/EP");
		itOptions.add("IT M/P");
		itOptions.add("IT Otros");
		itType.setSelectedOptions(itOptions);

		onSearch();
	}

	private void addButtonsToolbar() {
		excelExport = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconExcel());
		excelExport.addClickHandler(e -> {
			String fileDownloadURL = GWT.getModuleBaseURL() + "/download_activitySummary/" + "?domainName="
					+ domain.getName() + "&user=" + "" + "&startDate=" + start.getValue().getTime() + "&endDate="
					+ end.getValue().getTime() + "&starts=" + contractType.getSelectedOptions().contains("Altas")
					+ "&ends=" + contractType.getSelectedOptions().contains("Bajas") + "&salary="
					+ salaryType.getSelectedOptions().contains("Nomina") + "&salaryExtra="
					+ salaryType.getSelectedOptions().contains("Extra") + "&salarySettle="
					+ salaryType.getSelectedOptions().contains("Finiquito") + "&salaryOther="
					+ salaryType.getSelectedOptions().contains("Atrasos") + "&itCommonDisease="
					+ itType.getSelectedOptions().contains("IT EC/AN") + "&itOccupationalDisease="
					+ itType.getSelectedOptions().contains("IT AT/EP") + "&itMaternity="
					+ itType.getSelectedOptions().contains("IT M/P") + "&itOther="
					+ itType.getSelectedOptions().contains("IT Otros");
			Window.open(fileDownloadURL, "_blank", null);
		});

		addToolbarButton(excelExport);
	}

	private void onSearch() {
		getActivitySummaryParams();
		search();
	}

	private void search() {
		centerPanel.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);

		paintHeader(tab, domain.isParent());

		centerPanel.setWidget(tableScrollPanel);

		searchData();
	}

	private void paintHeader(AonCustomTable tab, boolean isParent) {
		tab.createHeader();

		for (ENTERPRISES_COLS col : ENTERPRISES_COLS.values())
			if (isParent && ENTERPRISES_COLS.BUT == col)
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
			else if (ENTERPRISES_COLS.BUT != col)
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}

	private void searchData() {
		getList(params, activitySummaries -> {
			boolean something = false;
			for (ActivitySummaryObject activitySummary : activitySummaries) {
				something = true;
				paintRow(tab, activitySummary, domain.isParent());
			}
			paintFooter(tab, activitySummaries, domain.isParent());

			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				centerPanel.clear();
				centerPanel.add(line);
			}

		});
	}

	private void paintRow(AonCustomTable tab, ActivitySummaryObject activitySummary, boolean isParent) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);

		AonTableButton button;
		button = new AonTableButton("Contratos", AON.CSS.aonIconInfo());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(e -> {
			e.stopPropagation();
			openChildsDialog(activitySummary.getFullname(), activitySummary);
		});
		buttonContainer.add(button);

		HTMLPanel row = tab.createRow();

		if (domain.isParent())
			row.addDomHandler(e -> openChildsDialog(activitySummary.getFullname(), activitySummary),
					ClickEvent.getType());

		Label name = new Label(activitySummary.getFullname());
		name.setTitle(activitySummary.getFullname());
		tab.addInlineStyle(name, ENTERPRISES_COLS.DES.getCellStyleClass());
		tab.addRow(row, name, ENTERPRISES_COLS.DES.getColWidth());

		Label contractStart = new Label(isParent
				? (null == activitySummary.getStartCount() ? "-" : activitySummary.getStartCount().toString())
				: (null == activitySummary.getStartDate() ? "" : formatDate.format(activitySummary.getStartDate())));
		tab.addInlineStyle(contractStart, ENTERPRISES_COLS.ALT.getCellStyleClass());
		tab.addRow(row, contractStart, ENTERPRISES_COLS.ALT.getColWidth());

		Label contractEnd = new Label(isParent
				? (null == activitySummary.getEndCount() ? "-" : activitySummary.getEndCount().toString())
				: (null == activitySummary.getEndDate() ? "" : formatDate.format(activitySummary.getEndDate())));
		tab.addInlineStyle(contractEnd, ENTERPRISES_COLS.BAJ.getCellStyleClass());
		tab.addRow(row, contractEnd, ENTERPRISES_COLS.BAJ.getColWidth());

		Label salary = new Label(
				null == activitySummary.getSalaryCount() ? "-" : activitySummary.getSalaryCount().toString());
		tab.addInlineStyle(salary, ENTERPRISES_COLS.SAL.getCellStyleClass());
		tab.addRow(row, salary, ENTERPRISES_COLS.SAL.getColWidth());

		Label extra = new Label(
				null == activitySummary.getSalaryExtraCount() ? "-" : activitySummary.getSalaryExtraCount().toString());
		tab.addInlineStyle(extra, ENTERPRISES_COLS.EXT.getCellStyleClass());
		tab.addRow(row, extra, ENTERPRISES_COLS.EXT.getColWidth());

		Label settle = new Label(null == activitySummary.getSalarySettleCount() ? "-"
				: activitySummary.getSalarySettleCount().toString());
		tab.addInlineStyle(settle, ENTERPRISES_COLS.SET.getCellStyleClass());
		tab.addRow(row, settle, ENTERPRISES_COLS.SET.getColWidth());

		Label delay = new Label(
				null == activitySummary.getSalaryOtherCount() ? "-" : activitySummary.getSalaryOtherCount().toString());
		tab.addInlineStyle(delay, ENTERPRISES_COLS.DEL.getCellStyleClass());
		tab.addRow(row, delay, ENTERPRISES_COLS.DEL.getColWidth());

		Label commonDisease = new Label(null == activitySummary.getItCommonDiseaseCount() ? "-"
				: activitySummary.getItCommonDiseaseCount().toString());
		tab.addInlineStyle(commonDisease, ENTERPRISES_COLS.ECA.getCellStyleClass());
		tab.addRow(row, commonDisease, ENTERPRISES_COLS.ECA.getColWidth());

		Label occupationalDisease = new Label(null == activitySummary.getItOccupationalDiseaseCount() ? "-"
				: activitySummary.getItOccupationalDiseaseCount().toString());
		tab.addInlineStyle(occupationalDisease, ENTERPRISES_COLS.ATE.getCellStyleClass());
		tab.addRow(row, occupationalDisease, ENTERPRISES_COLS.ATE.getColWidth());

		Label maternity = new Label(
				null == activitySummary.getItMaternityCount() ? "-" : activitySummary.getItMaternityCount().toString());
		tab.addInlineStyle(maternity, ENTERPRISES_COLS.MPP.getCellStyleClass());
		tab.addRow(row, maternity, ENTERPRISES_COLS.MPP.getColWidth());

		Label other = new Label(
				null == activitySummary.getItOtherCount() ? "-" : activitySummary.getItOtherCount().toString());
		tab.addInlineStyle(other, ENTERPRISES_COLS.OTH.getCellStyleClass());
		tab.addRow(row, other, ENTERPRISES_COLS.OTH.getColWidth());

		if (isParent)
			tab.addRow(row, buttonContainer, ENTERPRISES_COLS.BUT.getColWidth());
	}

	private void paintFooter(AonCustomTable tab, List<ActivitySummaryObject> activitySummaries, boolean isParent) {
		tab.createFooter();

		tab.addFooter(new Label(isParent ? AonStringUtils.EMPTY : ("Contratos: " + activitySummaries.size())),
				ENTERPRISES_COLS.DES.getColWidth(), ENTERPRISES_COLS.DES.getCellStyleClass());

		tab.addFooter(new Label(isParent ? AonStringUtils.EMPTY : ("Altas: " + getAltas(activitySummaries))),
				ENTERPRISES_COLS.ALT.getColWidth(), ENTERPRISES_COLS.ALT.getCellStyleClass());
		tab.addFooter(new Label(isParent ? AonStringUtils.EMPTY : ("Bajas: " + getBajas(activitySummaries))),
				ENTERPRISES_COLS.BAJ.getColWidth(), ENTERPRISES_COLS.BAJ.getCellStyleClass());

		int salaryCount = activitySummaries.stream().mapToInt(
				activitySummary -> null == activitySummary.getSalaryCount() ? 0 : activitySummary.getSalaryCount())
				.sum();
		int extraCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getSalaryExtraCount() ? 0
						: activitySummary.getSalaryExtraCount())
				.sum();
		int settleCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getSalarySettleCount() ? 0
						: activitySummary.getSalarySettleCount())
				.sum();
		int delayCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getSalaryOtherCount() ? 0
						: activitySummary.getSalaryOtherCount())
				.sum();

		tab.addFooter(new Label(salaryCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.SAL.getColWidth(),
				ENTERPRISES_COLS.SAL.getCellStyleClass());
		tab.addFooter(new Label(extraCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.EXT.getColWidth(),
				ENTERPRISES_COLS.EXT.getCellStyleClass());
		tab.addFooter(new Label(settleCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.SET.getColWidth(),
				ENTERPRISES_COLS.SET.getCellStyleClass());
		tab.addFooter(new Label(delayCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.DEL.getColWidth(),
				ENTERPRISES_COLS.DEL.getCellStyleClass());

		int commonDiseaseCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getItCommonDiseaseCount() ? 0
						: activitySummary.getItCommonDiseaseCount())
				.sum();
		int occupationalDiseaseCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getItOccupationalDiseaseCount() ? 0
						: activitySummary.getItOccupationalDiseaseCount())
				.sum();
		int meternityCount = activitySummaries.stream()
				.mapToInt(activitySummary -> null == activitySummary.getItMaternityCount() ? 0
						: activitySummary.getItMaternityCount())
				.sum();
		int otherCount = activitySummaries.stream().mapToInt(
				activitySummary -> null == activitySummary.getItOtherCount() ? 0 : activitySummary.getItOtherCount())
				.sum();

		tab.addFooter(new Label(commonDiseaseCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.ECA.getColWidth(),
				ENTERPRISES_COLS.ECA.getCellStyleClass());
		tab.addFooter(new Label(occupationalDiseaseCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.ATE.getColWidth(),
				ENTERPRISES_COLS.ATE.getCellStyleClass());
		tab.addFooter(new Label(meternityCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.MPP.getColWidth(),
				ENTERPRISES_COLS.MPP.getCellStyleClass());
		tab.addFooter(new Label(otherCount + AonStringUtils.EMPTY), ENTERPRISES_COLS.OTH.getColWidth(),
				ENTERPRISES_COLS.OTH.getCellStyleClass());

		if (isParent)
			tab.addFooter(new Label(AonStringUtils.EMPTY), ENTERPRISES_COLS.BUT.getColWidth(),
					ENTERPRISES_COLS.BUT.getCellStyleClass());
	}

	private String getAltas(List<ActivitySummaryObject> activitySummaries) {
		if (null != start.getValue() && null != end.getValue()) {
			long altas = activitySummaries.stream()
					.filter(activitySummary -> ge(activitySummary.getStartDate(), start.getValue())
							&& le(activitySummary.getStartDate(), end.getValue()))
					.count();
			return Long.toString(altas);
		}

		return "N/D";
	}

	private String getBajas(List<ActivitySummaryObject> activitySummaries) {
		if (null != start.getValue() && null != end.getValue()) {
			long altas = activitySummaries.stream()
					.filter(activitySummary -> null != activitySummary.getEndDate()
							&& ge(activitySummary.getEndDate(), start.getValue())
							&& le(activitySummary.getEndDate(), end.getValue()))
					.count();
			return Long.toString(altas);
		}

		return "N/D";
	}

	private boolean ge(Date date, Date date2) {
		return date.after(date2) || date.equals(date2);
	}

	private boolean le(Date date, Date date2) {
		return date.before(date2) || date.equals(date2);
	}

	private void getActivitySummaryParams() {
		this.params = new ActivitySummaryParams()
				.setDescription(getSearchTextBox().getValue())
				.setStart(start.getValue()).setEnd(end.getValue())
				.setStartContract(contractType.getSelectedOptions().contains("Altas"))
				.setEndContract(contractType.getSelectedOptions().contains("Bajas"))
				.setSalary(salaryType.getSelectedOptions().contains("Nomina"))
				.setExtra(salaryType.getSelectedOptions().contains("Extra"))
				.setSettle(salaryType.getSelectedOptions().contains("Finiquito"))
				.setDelay(salaryType.getSelectedOptions().contains("Atrasos"))
				.setItCD(itType.getSelectedOptions().contains("IT EC/AN"))
				.setItOD(itType.getSelectedOptions().contains("IT AT/EP"))
				.setItMP(itType.getSelectedOptions().contains("IT M/P"))
				.setItOT(itType.getSelectedOptions().contains("IT Otros")).setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE);
	}

	private void openChildsDialog(String enterpriseName, ActivitySummaryObject enterpriseSummary) {
		ActivitySummaryParams params = new ActivitySummaryParams()
				.setChildomain(enterpriseSummary.getId())
				.setDescription(AonStringUtils.EMPTY).setStart(start.getValue()).setEnd(end.getValue())
				.setStartContract(contractType.getSelectedOptions().contains("Altas"))
				.setEndContract(contractType.getSelectedOptions().contains("Bajas"))
				.setSalary(salaryType.getSelectedOptions().contains("Nomina"))
				.setExtra(salaryType.getSelectedOptions().contains("Extra"))
				.setSettle(salaryType.getSelectedOptions().contains("Finiquito"))
				.setDelay(salaryType.getSelectedOptions().contains("Atrasos"))
				.setItCD(itType.getSelectedOptions().contains("IT EC/AN"))
				.setItOD(itType.getSelectedOptions().contains("IT AT/EP"))
				.setItMP(itType.getSelectedOptions().contains("IT M/P"))
				.setItOT(itType.getSelectedOptions().contains("IT Otros")).setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE);

		getList(params, activitySummaries -> {
			SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
			centerPanel.setHeight("20rem");
			centerPanel.getElement().getStyle().setProperty("margin", "1rem");

			AonCustomTable tab = new AonCustomTable();
			ScrollPanel tableScrollPanel = new ScrollPanel(tab);

			paintHeader(tab, false);
			activitySummaries.forEach(activitySummary -> paintRow(tab, activitySummary, false));
			paintFooter(tab, activitySummaries, false);

			centerPanel.setWidget(tableScrollPanel);

			AonCustomDialog dialog = new AonCustomDialog();
			dialog.showCloseButton(true);
			dialog.setCaption(enterpriseName);
			dialog.setHeight("25rem");
			dialog.setWidth("75rem");
			dialog.add(centerPanel);
			dialog.showLoaded();
		});
	}

	private void getList(ActivitySummaryParams params, Consumer<List<ActivitySummaryObject>> success) {
		if (getCurrentIsSig()) {
			String host = isLocalDev ? "localhost:8080" : "aon.solutions";
			String endPoint = "/ms/api/domain/customer-summary-activity/";

			HashMap<String, String> header = new HashMap<>();
			header.put("domain_name", domain.getName());
			header.put("domain_id", domain.getId().toString());
			header.put("domain_login", user);
			
			JSONObject body = new JSONObject();
			if(null != params.getChildomain())
				body.put("childomain", new JSONNumber(params.getChildomain()));

			body.put("description", new JSONString(params.getDescription()));

			body.put("start", new JSONString(formatDate.format(params.getStart())));
			body.put("end", new JSONString(formatDate.format(params.getEnd())));

			body.put("startContract", new JSONString(Boolean.toString(params.isStartContract())));
			body.put("endContract", new JSONString(Boolean.toString(params.isEndContract())));
			body.put("salary", new JSONString(Boolean.toString(params.isSalary())));
			body.put("settle", new JSONString(Boolean.toString(params.isSettle())));
			body.put("extra", new JSONString(Boolean.toString(params.isExtra())));

			body.put("itCD", new JSONString(Boolean.toString(params.isItCD())));
			body.put("itOD", new JSONString(Boolean.toString(params.isItOD())));
			body.put("itMP", new JSONString(Boolean.toString(params.isItMP())));
			body.put("itOT", new JSONString(Boolean.toString(params.isItOT())));

			body.put("orderBy", new JSONString(params.getOrderBy()));
			body.put("asc", new JSONString(Boolean.toString(params.isAsc())));
			body.put("offset", new JSONNumber(params.getOffset()));
			body.put("limit", new JSONNumber(params.getLimit()));

			body.put("user", new JSONString(user));
			body.put("domainName", new JSONString(domain.getName()));
			body.put("domainId", new JSONNumber(domain.getId()));
			
			this.customerApi.getCustomerActivitySummary(host, endPoint, header, body,
					new AsyncCallback<List<ActivitySummaryObject>>() {

						@Override
						public void onSuccess(List<ActivitySummaryObject> result) {
							success.accept(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel,
									"Obtenci\u00f3n resumen Sig : " + caught.getMessage());
						}
					});
		} else {
			COMMON_SERVICE.getActivitySummary(domain.getName(), user, params,
					new AsyncCallback<List<ActivitySummaryObject>>() {

						@Override
						public void onSuccess(List<ActivitySummaryObject> result) {
							success.accept(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Obtenci\u00f3n resumen : " + caught.getMessage());
						}

					});
		}
	}

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;
}