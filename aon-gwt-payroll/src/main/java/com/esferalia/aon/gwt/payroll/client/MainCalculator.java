package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class MainCalculator extends MainEntryPoint implements CalculateService {
	
	private static DateTimeFormat MONTH_DATE_TIME_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	static interface Binder extends UiBinder<Widget, MainCalculator> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	// ------------------------------------------------------------------------
	
	public static DateTimeFormat DATE_FORMAT = DateTimeFormat
	.getFormat(CalculateService.DATE_FORMAT_PATTERN);

	public static String CALC_URL = URL.encode(GWT.getModuleBaseURL() + "calculate");

	public static final byte DUPLICATE_OPTION = 0x04;

	public static final byte OVERWRITE_OPTION = 0x02;

	public static final byte SAVE_OPTION = 0x01;

	public static <T extends HasId<?>> void calculate(Salary.Type salaryType, Date startDate,
			Date endDate, Date issueDate, String itemClass, Set<T> items, int optionsBits, Integer extra,
			final AsyncCallback<JsSalaryResult> callback) {
		calculate(salaryType, startDate, endDate, issueDate, startDate, endDate, itemClass, items, optionsBits, extra, callback);
	}

	public static <T extends HasId<?>> void calculate(Salary.Type salaryType, Date startDate,
			Date endDate, Date issueDate, Date startCheckDate, Date endCheckDate, 
			String itemClass, Set<T> items, Integer extra, int optionsBits, 
			final AsyncCallback<JsSalaryResult> callback) {
	
		StringBuffer requestDataBuffer = new StringBuffer();
	
		requestDataBuffer
			.append("&" + SALARY_TYPE + "=" + salaryType.name() );
		
		if ( extra != null ) 
			requestDataBuffer
			.append("&" + EXTRA + "=" + Integer.toString(extra) );
			

		requestDataBuffer
				.append("&" + START_DATE + "=" + DATE_FORMAT.format(startDate));
		requestDataBuffer
				.append("&" + END_DATE + "=" + DATE_FORMAT.format(endDate));
		requestDataBuffer
				.append("&" + ISSUE_DATE + "=" + DATE_FORMAT.format(issueDate));
		requestDataBuffer
				.append("&" + START_CHECK_DATE + "=" + DATE_FORMAT.format(startCheckDate));
		requestDataBuffer
				.append("&" + END_CHECK_DATE + "=" + DATE_FORMAT.format(endCheckDate));
	
		for (T item : items)
			requestDataBuffer.append("&" + itemClass + "=" + item.getId());
	
		if ((optionsBits & SAVE_OPTION) > 0)
			requestDataBuffer.append("&" + SAVE + "=" + Boolean.toString(true));
		if ((optionsBits & OVERWRITE_OPTION) > 0)
			requestDataBuffer
					.append("&" + OVERWRITE + "=" + Boolean.toString(true));
		else if ((optionsBits & DUPLICATE_OPTION) > 0)
			requestDataBuffer
					.append("&" + DUPLICATE + "=" + Boolean.toString(true));
	
		// Send request to server and catch any errors.
	
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", CALC_URL);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
	
			private int loaded = 0;
	
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.LOADING
						|| state == XMLHttpRequest.DONE) {
	
					String text = xhr.getResponseText();
	
					try {
						for (JsSalaryResult result = read(
								text); text != null; result = read(text))
							callback.onSuccess(result);
					} catch (IndexOutOfBoundsException e) {
					}
				}
	
			}
	
			private JsSalaryResult read(String text) {
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
	
		xhr.send(requestDataBuffer.toString());
	}

	@UiField
	Button calcButton;

	@UiField
	MinimizePanel footPanel;

	@UiField
	ResultsPanel resultsPanel;

	@UiField
	MonthListBox monthListBox;
	

	@UiField
	ListBox dbMonthListBox;

	@UiField
	CheckBox saveCheckBox;

	@UiField
	RadioButton keepRadioButton;
	@UiField
	RadioButton overwriteRadioButton;
//	@UiField
//	RadioButton duplicateRadioButton;
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;

	/**
	 * The enterprises DataGrid.
	 */
	@UiField(provided=true)
	CustomDataGrid<Enterprise> enterprisesDataGrid;
	
	private SelectAllHeader<Enterprise> enterprisesSelectAllHeader;
	private MultiSelectionModel<Enterprise> enterprisesSelectionModel;

	private ListDataProvider<JsSalaryResult> resultsDataProvider;
	
	private EnterprisesServiceAsync enterprisesService;

	private List<Cost> costs;


	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(
				GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();

		// Create & setup enterprise DataGrid 
		createEnterprisesDataGrid();
		
		Widget ui = binder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync gwtEnterprisesService = GWT
				.create(EnterprisesService.class);
		
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				gwtEnterprisesService);
		
		initAsyncEnterprisesProvider();

		clearDbMonthsListBox();
		monthListBox.setSelectedMonth(new Date());
		
		
		SalaryResults results = new SalaryResults();
		resultsDataProvider = new ListDataProvider<JsSalaryResult>();
		results.setDataProvider(resultsDataProvider);
		results.addSelectionHandler(new SelectionHandler<JsSalaryResult>() {
			@Override
			public void onSelection(SelectionEvent<JsSalaryResult> event) {
				//TODO:
			}
		});

		resultsPanel.setWidget(results);
		
	}


	

	// -------------------------------------------------------------------------

	@UiHandler("calcButton")
	void onCalcButtonClicked(ClickEvent click) {
		clear();
		calculate();
		if (!isResultsPanelVisible())
			showResultsPanel();
	}

	@UiHandler("saveCheckBox")
	void onSaveClicked(ClickEvent event) {
		keepRadioButton.setEnabled(saveCheckBox.getValue());
		overwriteRadioButton.setEnabled(saveCheckBox.getValue());
	}

	@UiHandler("dbMonthListBox")
	void onDbMonthListBoxChanges(ChangeEvent event) {
		Date checkMonth = getCheckMonth();
		if ( checkMonth == null ) {
			calcButton.setText("Calcular");
			saveCheckBox.setEnabled(true);
		}else {
			calcButton.setText("Comparar");
			saveCheckBox.setEnabled(false);
			saveCheckBox.setValue(false);
			overwriteRadioButton.setValue(false);
		}
	}
	// -------------------------------------------------------------------------
	
	private void clear(){
		resultsDataProvider.getList().clear();
	}

	private void calculate() {

		int optionsBits = 0x00;
		if (saveCheckBox.getValue())
			optionsBits |= SAVE_OPTION;
		if (overwriteRadioButton.getValue())
			optionsBits |= OVERWRITE_OPTION;


		Date month = monthListBox.getSelectedMonth();
		Date startDate = DateUtils.getFirstDayOfMonth(month);
		Date endDate = DateUtils.getLastDayOfMonth(month);
		Date issueDate = DateUtils.getLastDayOfMonth(month);
		
		Date checkMonth = getCheckMonth();
		Date startCheckDate = checkMonth == null ? startDate : DateUtils.getFirstDayOfMonth(checkMonth);
		Date endCheckDate = checkMonth == null ? endDate :  DateUtils.getLastDayOfMonth(checkMonth);

		Set<Enterprise> enterprises = enterprisesSelectionModel.getSelectedSet();
		
		calculate(Salary.Type.SALARY, startDate, endDate, issueDate, startCheckDate, endCheckDate, ENPERPRISES, enterprises, null,optionsBits,  new AsyncCallback<JsSalaryResult>(){
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(JsSalaryResult result) {
				MainCalculator.this.resultsDataProvider.getList().add(result);
			}
			
		});


	}

	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
		
	private void initAsyncEnterprisesProvider() {
		// It's a bit tricky, you're free to change, but look it it's pretty
		// isn't it.
		(new AsyncDataProvider<Enterprise>() {
			// called when the table requests a new range of data. You can push
			// data back to the displays using.
			@Override
			protected void onRangeChanged(HasData<Enterprise> display) {
				// Get the new range.
				final Range range = display.getVisibleRange();
				// Query the data asynchronously.
				enterprisesService.getEnterprises(range.getStart(),
						range.getLength(),
						new AsyncCallback<List<Enterprise>>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(List<Enterprise> enterprises) {
								// Push the data to the displays.
								// AsyncDataProvider will only update
								// displays that are within range of the data.								
								updateRowData(range.getStart(), enterprises);
							}
						});
			}
		}).addDataDisplay(enterprisesDataGrid);
	}

	private void onSelectionChange() {

		List<Integer> enterpriseIds = getSelectedEnterprisesIds();
		
		if (enterpriseIds.isEmpty() && !isAllEnterprisesSelected() ) {
			calcButton.setEnabled(false);
			Set<Date> dbMonths = Collections.emptySet();
			monthListBox.setHighLightMonths(dbMonths);
			clearDbMonthsListBox();

		} else {
			calcButton.setEnabled(true);

			enterprisesService.getEnterprisesCosts(enterpriseIds,
					new AsyncCallback<List<Cost>>() {
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							dbMonthListBox.clear();
						}

						@Override
						public void onSuccess(List<Cost> costs) {
							syncDbMonthsListBox(costs);
							SortedSet<Date> dbMonthsSet = getMonthsSet(costs,
									Salary.Type.SALARY);
							monthListBox.setHighLightMonths(dbMonthsSet);
							monthListBox.setSelectedMonth(monthListBox
									.getSelectedMonth());
							monthListBox.setSelectedMonth(monthListBox
									.getSelectedMonth());

						}
					});
		}

	}

	private void clearDbMonthsListBox() {
		dbMonthListBox.clear();
		dbMonthListBox.addItem(
				"No existen n\u00f3minas para las empresas seleccionadas", "");
		dbMonthListBox.setSelectedIndex(0);

	}

	private void syncDbMonthsListBox(List<Cost> costs) {
		this.costs = costs;
		dbMonthListBox.clear();
		dbMonthListBox.addItem("Ninguna", "");
		for (Cost cost : costs) {
			dbMonthListBox.addItem(MONTH_DATE_TIME_FORMAT
					.format(getMonth(cost))
					+ " ( "
					+ cost.getSalariesCount()
					+ " ) ");
		}
		dbMonthListBox.setSelectedIndex(0);
	}

	private Date getCheckMonth() {
		int index = dbMonthListBox.getSelectedIndex();
		return index == 0 ? null : getMonth(costs.get(index - 1));
	}

	
	private boolean isAllEnterprisesSelected() {
		return enterprisesSelectAllHeader.getValue();
	}

	private List<Integer> getSelectedEnterprisesIds() {
		Set<Enterprise> enterprises = enterprisesSelectionModel.getSelectedSet();
		List<Integer> enterpriseIds = new ArrayList<Integer>();
		for (Enterprise enterprise : enterprises)
			enterpriseIds.add(enterprise.getId());
		return enterpriseIds;
	}
	

	private void createEnterprisesDataGrid() {
		/*
		 * Set a key provider that provides a unique key for each item.
		 */
		ProvidesKey<Enterprise> keyProvider = HasIdKeyProvider.getKeyProvider();
		enterprisesDataGrid = new CustomDataGrid<Enterprise>(Integer.MAX_VALUE, keyProvider);
		
		/*
		 * Do not refresh the headers & footers every time the dataGrid is updated. .
		 */
		enterprisesDataGrid.setAutoHeaderRefreshDisabled(true);
		enterprisesDataGrid.setAutoFooterRefreshDisabled(true);

		// Set the message to display when the table is empty.
		// TODO : selectDataGrid.setEmptyTableWidget(new Label());

		// Add a selection model to handle user selection.
		enterprisesSelectionModel = new MultiSelectionModel<Enterprise>(keyProvider);
		enterprisesDataGrid.setSelectionModel(enterprisesSelectionModel,
				DefaultSelectionEventManager.<Enterprise> createCheckboxManager(0));
		enterprisesSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				MainCalculator.this.onSelectionChange();
			}
		});

		// Checkbox column. This table will uses a checkbox column for
		// selection.
		Column<Enterprise, Boolean> checkColumn = new Column<Enterprise, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(Enterprise object) {
				return MainCalculator.this.enterprisesSelectionModel.isSelected(object);
			}
		};

		enterprisesSelectAllHeader = new SelectAllHeader<Enterprise>(enterprisesSelectionModel, enterprisesDataGrid);
		enterprisesDataGrid.addColumn(checkColumn, enterprisesSelectAllHeader);
		enterprisesDataGrid.setColumnWidth(checkColumn, "40px");
		
		// Full CCC.
		Column<Enterprise, String> nameColumn = new Column<Enterprise, String>(
				new TextCell()) {
			@Override
			public String getValue(Enterprise enterprise) {
				return enterprise.getName();
			}
		};
		
		enterprisesDataGrid.addColumn(nameColumn, "Empresa");
		
		
		enterprisesDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		enterprisesDataGrid.getElement().getStyle()
				.setPropertyPx("minHeight", Window.getClientHeight() / 3);
		enterprisesDataGrid.setWidth("100%");

	}
	




	private static Date getMonth(Cost cost) {
		return new Date(cost.getYear() - 1900, cost.getMonth() + 1, 0);
	}

	private static SortedSet<Date> getMonthsSet(Collection<Cost> costs,
			Salary.Type... types) {
		SortedSet<Date> months = new TreeSet<Date>();
		for (Cost cost : costs) {
			if (getCount(cost, types) > 0)
				months.add(getMonth(cost));
		}
		return months;
	}

	private static int getCount(Cost cost, Salary.Type... types) {
		int count = 0;
		for (Type type : types)
			count += getCount(cost, type);
		return count;
	}

	private static int getCount(final Cost cost, Salary.Type type) {

		return type.accept(new TypeVisitor<Integer>() {

			@Override
			public Integer visitSalary(Type type) {
				return cost.getSalariesCount();
			}

			@Override
			public Integer visitExtra(Type type) {
				return cost.getExtrasCount();
			}

			@Override
			public Integer visitSettle(Type type) {
				return cost.getSettlesCount();
			}

			@Override
			public Integer visitDelay(Type type) {
				return cost.getDelaysCount();
			}

			@Override
			public Integer visitNotEnjoyedVacations(Type type) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
	}	
}
