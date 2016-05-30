package com.esferalia.aon.gwt.payroll.client;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;



public class ActivitySummary extends MainEntryPoint {
	
	final String DATE_FORMAT = "dd/MM/yyyy";
	
	final ActivitySummaryServiceAsync impl = GWT.create(ActivitySummaryService.class);

	static interface Binder extends UiBinder<Widget, ActivitySummary> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/DataGrid.css")
		Style dataGridStyle();
	}

	
	@UiField(provided = true) DateBoxEx startDate;
	@UiField(provided = true) DateBoxEx endDate;
	@UiField(provided = true) CheckBox startChk;
	@UiField(provided = true) CheckBox endChk;
	
	@UiField(provided = true) CheckBox salaryChk;
	@UiField(provided = true) CheckBox salaryExtraChk;
	@UiField(provided = true) CheckBox salarySettleChk;
	@UiField(provided = true) CheckBox salaryOtherChk;
	
	@UiField(provided = true) CheckBox itCommonDiseaseChk;
	@UiField(provided = true) CheckBox itOccupationalDiseaseChk;
	@UiField(provided = true) CheckBox itMaternityChk;
	@UiField(provided = true) CheckBox itOtherChk;
	
	@UiField(provided = true) Button searchButton;
	@UiField(provided = true) DataGrid<ActivitySummaryObject> dataGrid;
	
	@UiField(provided = true) Button exportButton;
	
	
	private List<ActivitySummaryObject> summaryList;
	private ListDataProvider<ActivitySummaryObject> dataProvider;
	private final Set<Integer> showingContracts = new HashSet<Integer>();
	private List<ActivitySummaryObject> contractSummaryList;
	private int selectedIndex;
	
	private Column<ActivitySummaryObject, String> selectColumn;
	private Column<ActivitySummaryObject, String> nameColumn;
	private Column<ActivitySummaryObject, String> startCountColumn;
	private Column<ActivitySummaryObject, String> endCountColumn;
	
	private Column<ActivitySummaryObject, String> salaryCountColumn;
	private Column<ActivitySummaryObject, String> salaryExtraCountColumn;
	private Column<ActivitySummaryObject, String> salarySettleCountColumn;
	private Column<ActivitySummaryObject, String> salaryOtherCountColumn;

	private Column<ActivitySummaryObject, String> itCommonDiseaseCountColumn;
	private Column<ActivitySummaryObject, String> itOccupationalDiseaseCountColumn;
	private Column<ActivitySummaryObject, String> itMaternityCountColumn;
	private Column<ActivitySummaryObject, String> itOtherCountColumn;
	
	private Integer domainId = null;
	private Integer parentDomainId = null;
	private String domainName = null;
	
	@Override
	public void onModuleLoad() {

		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		startDate = new DateBoxEx();
		endDate = new DateBoxEx();
		
		startChk = new CheckBox();
		endChk = new CheckBox();
		
		salaryChk = new CheckBox();
		salaryExtraChk = new CheckBox();
		salarySettleChk = new CheckBox();
		salaryOtherChk = new CheckBox();
		
		itCommonDiseaseChk = new CheckBox();
		itOccupationalDiseaseChk = new CheckBox();
		itMaternityChk = new CheckBox();
		itOtherChk = new CheckBox();
		
		searchButton = new Button("Buscar");
		exportButton = new Button("Exportar");
		dataGrid = new DataGrid<ActivitySummaryObject>(Integer.MAX_VALUE, resources); 
		
		dataGrid.setHeaderBuilder(new CustomHeaderBuilder());
		dataGrid.setTableBuilder(new CustomTableBuilder());
		dataGrid.setFooterBuilder(new CustomFooterBuilder());
		
		Widget ui = binder.createAndBindUi(this);
		

		RootLayoutPanel.get("rootPanel").add(ui);
		
		initSearchBox();
		
		impl.getDomainId(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Integer pDomainId) {
				domainId = pDomainId;
			}
		});
		impl.getDomainName(new AsyncCallback<String>() {
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
			
			@Override
			public void onSuccess(String pDomainName) {
				domainName = pDomainName;
			}
		});
		impl.getParentDomainId(new AsyncCallback<Integer>() {
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
			
			@Override
			public void onSuccess(Integer pParentDomainId) {
				parentDomainId = pParentDomainId;
				loadData();
				loadDataGrid(parentDomainId==null);
			}
		});
		
	}
	
	private List<ActivitySummaryObject> getSummaryList() {
		return summaryList;
	}
	
	private void initSearchBox(){
		selectedIndex=-1;
		showingContracts.clear();
		if(contractSummaryList != null){
			contractSummaryList.clear();
		}
		
		Date start = new Date();
		CalendarUtil.setToFirstDayOfMonth(start);
		startDate.setValue(start);
	
		Date end = new Date();
		CalendarUtil.setToFirstDayOfMonth(end);
		CalendarUtil.addMonthsToDate(end, 1);
		CalendarUtil.addDaysToDate(end, -1);
		endDate.setValue(end);
		
		startChk.setValue(false);
		endChk.setValue(false);
		salaryChk.setValue(true);
		salaryExtraChk.setValue(true);
		salarySettleChk.setValue(true);
		salaryOtherChk.setValue(true);
		itCommonDiseaseChk.setValue(true);
		itOccupationalDiseaseChk.setValue(true);
		itMaternityChk.setValue(true);
		itOtherChk.setValue(true);
		
		searchButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if(startDate.getValue()==null || endDate.getValue()==null){
					Window.alert("Se deben seleccionar las fechas de inicio y fin");
				} else {
					loadData();
				}
			}
		}, ClickEvent.getType());
	}
	
	private void loadData() {
		 
		summaryList = new LinkedList<>();
		dataProvider = new ListDataProvider<>();
		
		impl.getActivitySummary(domainName, parentDomainId==null, domainId, startDate.getValue(), endDate.getValue(),
				startChk.getValue(), endChk.getValue(),
				salaryChk.getValue(), salaryExtraChk.getValue(), salarySettleChk.getValue(), salaryOtherChk.getValue(), 
				itCommonDiseaseChk.getValue(), itOccupationalDiseaseChk.getValue(), itMaternityChk.getValue(), itOtherChk.getValue(),
				new AsyncCallback<List<ActivitySummaryObject>>() {
					@Override
					public void onSuccess(List<ActivitySummaryObject> result) {
						summaryList = result;
						dataProvider = new ListDataProvider<ActivitySummaryObject>(
								result);
						dataProvider.addDataDisplay(dataGrid);
						dataGrid.redraw();
						dataGrid.redrawFooters();
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
		
	}
	
	private void loadDataGrid(boolean isParent){
		final SingleSelectionModel<ActivitySummaryObject> selectionModel = new SingleSelectionModel<ActivitySummaryObject>();
		dataGrid.setSelectionModel(selectionModel);
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No se encontraron datos."));
		dataProvider = new ListDataProvider<ActivitySummaryObject>(getSummaryList());
		dataProvider.addDataDisplay(dataGrid);
		ListHandler<ActivitySummaryObject> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		
		initTableColumns(isParent, sortHandler);
	}
	
	@UiHandler("exportButton")
	void exportButton(ClickEvent event){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/download_activitySummary/"
            + "?domainId=" + domainId
            + "&parentDomainId=" + parentDomainId
	        + "&startDate=" + startDate.getValue().getTime()
	        + "&endDate=" + endDate.getValue().getTime()
			+ "&starts=" + startChk.getValue()
	        + "&ends=" + endChk.getValue()
	        + "&salary=" + salaryChk.getValue()
	        + "&salaryExtra=" + salaryExtraChk.getValue()
	        + "&salarySettle=" + salarySettleChk.getValue()
	        + "&salaryOther=" + salaryOtherChk.getValue()
	        + "&itCommonDisease=" + itCommonDiseaseChk.getValue()
	        + "&itOccupationalDisease=" + itOccupationalDiseaseChk.getValue()
	        + "&itMaternity=" + itMaternityChk.getValue()
	        + "&itOther=" + itOtherChk.getValue()
            ;
		Window.open(fileDownloadURL, "_blank", null);
	}
	
	
	private ListHandler<ActivitySummaryObject> getSortHandler() {
		return new ListHandler<ActivitySummaryObject>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<ActivitySummaryObject> aux = super.getList();
				List<ActivitySummaryObject> aux2 = new Vector<ActivitySummaryObject>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
				}
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void redrawSelectedRow(Integer id){
		impl.getActivitySummary(domainName, false, id, startDate.getValue(), endDate.getValue(),
				startChk.getValue(), endChk.getValue(),
				salaryChk.getValue(), salaryExtraChk.getValue(), salarySettleChk.getValue(), salaryOtherChk.getValue(), 
				itCommonDiseaseChk.getValue(), itOccupationalDiseaseChk.getValue(), itMaternityChk.getValue(), itOtherChk.getValue(),
				new AsyncCallback<List<ActivitySummaryObject>>() {
			@Override
			public void onSuccess(List<ActivitySummaryObject> result) {
				contractSummaryList = result;						
				dataGrid.redrawRow(selectedIndex);
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void initTableColumns(boolean isParent, ListHandler<ActivitySummaryObject> sortHandler) {
		
		/**
		 * Selected Column
		 */
		if(isParent) {
			SafeHtmlRenderer<String> anchorRenderer = new AbstractSafeHtmlRenderer<String>() {
				@Override
				public SafeHtml render(String object) {
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.appendHtmlConstant("(<a href=\"javascript:;\">")
						.appendEscaped(object).appendHtmlConstant("</a>)");
					return sb.toSafeHtml();
				}
			};
			selectColumn = new Column<ActivitySummaryObject, String>(
					new ClickableTextCell(anchorRenderer)) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return showingContracts.contains(object.getId()) ? " - " : " + ";
				}
			};
			selectColumn.setFieldUpdater(new FieldUpdater<ActivitySummaryObject, String>() {
				@Override
				public void update(int index, ActivitySummaryObject object, String value) {
					selectedIndex = index;
					if (showingContracts.contains(object.getId())) {
						showingContracts.remove(object.getId());
					} else {
						showingContracts.add(object.getId());
					}
//					dataGrid.redrawRow(index);
					
					redrawSelectedRow(object.getId());
				}
			});
		} else {
			selectColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return "";
				}
			};
		}
		dataGrid.addColumn(selectColumn);
		dataGrid.setColumnWidth(selectColumn, 5, Unit.EM);
		
		/**
		 * Name Column
		 */
		nameColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public void render(Context context, ActivitySummaryObject object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" + object.getFullname() + "</span>");
			}
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getFullname();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn,
				new Comparator<ActivitySummaryObject>() {
					@Override
					public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
						return o1.getFullname().compareTo(o2.getFullname());
					}
				});
		dataGrid.addColumn(nameColumn);
		dataGrid.setColumnWidth(nameColumn, 80, Unit.PCT);
		dataGrid.getColumnSortList().push(nameColumn);
		
		/**
		 * Start Column
		 */
		if(isParent) {
			startCountColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return String.valueOf(object.getStartCount());
				}
			};
		} else {
			startCountColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					if(object!=null && object.getStartDate()!=null){
						return DateTimeFormat.getFormat("dd/MM/yyyy").format(object.getStartDate());
					}
					return "-";
				}
			};
		}
		dataGrid.addColumn(startCountColumn);
		dataGrid.setColumnWidth(startCountColumn, 10, Unit.EM);
		
		/**
		 * End Column
		 */
		if(isParent) {
			endCountColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return String.valueOf(object.getEndCount());
				}
			};
		} else {
			endCountColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					if(object!=null && object.getEndDate()!=null){
						return DateTimeFormat.getFormat("dd/MM/yyyy").format(object.getEndDate());
					}
					return "-";
				}
			};
		}
		dataGrid.addColumn(endCountColumn);
		dataGrid.setColumnWidth(endCountColumn, 10, Unit.EM);
		
		/**
		 * SalaryCount Column
		 */
		salaryCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryCount()!=null?object.getSalaryCount().toString():"0";
			}
		};
		dataGrid.addColumn(salaryCountColumn);
		dataGrid.setColumnWidth(salaryCountColumn, 7, Unit.EM);
		
		/**
		 * SalaryExtraCount Column
		 */
		salaryExtraCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryExtraCount()!=null?object.getSalaryExtraCount().toString():"0";
			}
		};
		dataGrid.addColumn(salaryExtraCountColumn);
		dataGrid.setColumnWidth(salaryExtraCountColumn, 7, Unit.EM);
		
		/** 
		 * SalarySettleCount Column
		 */
		salarySettleCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalarySettleCount()!=null?object.getSalarySettleCount().toString():"0";
			}
		};
		dataGrid.addColumn(salarySettleCountColumn);
		dataGrid.setColumnWidth(salarySettleCountColumn, 7, Unit.EM);
		
		/**
		 * SalaryOtherCount Column
		 */
		salaryOtherCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryOtherCount()!=null?object.getSalaryOtherCount().toString():"0";
			}
		};
		dataGrid.addColumn(salaryOtherCountColumn);
		dataGrid.setColumnWidth(salaryOtherCountColumn, 7, Unit.EM);
		
		/** 
		 * IT Common Disease Column 
		 */
		itCommonDiseaseCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItCommonDiseaseCount()!=null?object.getItCommonDiseaseCount().toString():"0";
			}
		};
		dataGrid.addColumn(itCommonDiseaseCountColumn);
		dataGrid.setColumnWidth(itCommonDiseaseCountColumn, 7, Unit.EM);
		
		/**
		 * IT Occupational Disease Column
		 */
		itOccupationalDiseaseCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItOccupationalDiseaseCount()!=null?object.getItOccupationalDiseaseCount().toString():"0";
			}
		};
		dataGrid.addColumn(itOccupationalDiseaseCountColumn);
		dataGrid.setColumnWidth(itOccupationalDiseaseCountColumn, 7, Unit.EM);
		
		/**
		 * IT Maternity Column
		 */
		itMaternityCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItMaternityCount()!=null?object.getItMaternityCount().toString():"0";
			}
		};
		dataGrid.addColumn(itMaternityCountColumn);
		dataGrid.setColumnWidth(itMaternityCountColumn, 7, Unit.EM);

		/**
		 * IT Other Column
		 */
		itOtherCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItOtherCount()!=null?object.getItOtherCount().toString():"0";
			}
		};
		dataGrid.addColumn(itOtherCountColumn);
		dataGrid.setColumnWidth(itOtherCountColumn, 7, Unit.EM);
	}
	
	
	
	
	/**
	 * CUSTOM HEADER
	 */
	private class CustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<ActivitySummaryObject> {

		private Header<String> selectHeader = new TextHeader("");
		private Header<String> nameHeader = new TextHeader("Nombre");
		private Header<String> startHeader = new TextHeader("Inicio contr.");
		private Header<String> endHeader = new TextHeader("Fin contr.");
		private Header<String> salaryHeader = new TextHeader("Nominas");
		private Header<String> salaryExtraHeader = new TextHeader("Extras");
		private Header<String> salarySettleHeader = new TextHeader("Finiquitos");
		private Header<String> salaryOtherHeader = new TextHeader("Otros");
//		private Header<String> itCommonDiseaseHeader = new TextHeader("<span title=\"Enfermedad comun - Accidente no laboral\">IT EC/AN</span>");
		private Header<String> itCommonDiseaseHeader = new TextHeader("IT EC/AN");
//		private Header<String> itOccupationalDiseaseHeader = new TextHeader("<span title=\"Accidente de trabajo - Enfermedad profesional\">IT AT/EP</span>");
		private Header<String> itOccupationalDiseaseHeader = new TextHeader("IT AT/EP");
//		private Header<String> itMaternityHeader = new TextHeader("<span title=\"Maternidad - Paternidad\">IT M/P</span>");
		private Header<String> itMaternityHeader = new TextHeader("IT M/P");
		private Header<String> itOtherHeader = new TextHeader("IT Otros");
		
		public CustomHeaderBuilder() {
			super(dataGrid, false);
			setSortIconStartOfLine(false);
		}

		@Override
		protected boolean buildHeaderOrFooterImpl() {

			TableRowBuilder tr = startRow();
			tr.style().trustedBorderColor("#BDBDBD").endStyle();

			TableCellBuilder th = tr.startTH().colSpan(2);
			th.endTH();

			// Contract group header.
			th = tr.startTH().colSpan(2);
			th.text("Altas/Bajas").endTH();
			
			// Salary group header.
			th = tr.startTH().colSpan(4);
			th.text("Recibos").endTH();
			
			// IT group header.
			th = tr.startTH().colSpan(4);
			th.text("IT").endTH();

			// Get information about the sorted column.
			ColumnSortList sortList = dataGrid.getColumnSortList();
			ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null : sortList.get(0);
			Column<?, ?> sortedColumn = (sortedInfo == null) ? null : sortedInfo.getColumn();
			boolean isSortAscending = (sortedInfo == null) ? false : sortedInfo.isAscending();

			// Add column headers.
			tr = startRow();
			tr.style().trustedBackgroundColor("#BDBDBD").endStyle();
			buildHeader(tr, selectHeader, selectColumn, sortedColumn, isSortAscending, true, false);
			buildHeader(tr, nameHeader, nameColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, startHeader, startCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, endHeader, endCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, salaryHeader, salaryCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, salaryExtraHeader, salaryExtraCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, salarySettleHeader, salarySettleCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, salaryOtherHeader, salaryOtherCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, itCommonDiseaseHeader, itCommonDiseaseCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, itOccupationalDiseaseHeader, itOccupationalDiseaseCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, itMaternityHeader, itMaternityCountColumn, sortedColumn, isSortAscending, false, false);
			buildHeader(tr, itOtherHeader, itOtherCountColumn, sortedColumn, isSortAscending, false, true);
			tr.endTR();

			return true;
		}

		private void buildHeader(TableRowBuilder out, Header<?> header,
				Column<ActivitySummaryObject, ?> column, Column<?, ?> sortedColumn,
				boolean isSortAscending, boolean isFirst, boolean isLast) {
			boolean isSorted = (sortedColumn == column);

			// Create the table cell.
			TableCellBuilder th = out.startTH();

			// Associate the cell with the column to enable sorting of the column.
			enableColumnHandlers(th, column);

			// Render the header.
			Context context = new Context(0, 2, header.getKey());
			renderSortableHeader(th, context, header, isSorted, isSortAscending);

			// End the table cell.
			th.endTH();
		}
	}
	
	/**
	 * CUSTOM FOOTER
	 */
	private class CustomFooterBuilder extends
			AbstractHeaderOrFooterBuilder<ActivitySummaryObject> {

		public CustomFooterBuilder() {
			super(dataGrid, true);
		}

		@Override
		protected boolean buildHeaderOrFooterImpl() {
			String footerStyle = AON.AON_CSS.aonBorderTop();
			String centerColumnStyles = " " + AON.AON_TEXT_CENTER;

			List<ActivitySummaryObject> items = dataGrid.getVisibleItems();

			TableRowBuilder tr = startRow();
			tr.style().trustedBackgroundColor("#BDBDBD").endStyle();
			tr.startTH().colSpan(1).className(footerStyle).endTH();
			
			/** name */
			renderCell(tr, footerStyle, String.valueOf(getTotalCountEmployee(items)));
			/** start */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountStartEmployee(items)));
			/** end */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountEndEmployee(items)));
			
			/** salary */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountSalary(items)));
			/** salary extra */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountExtraSalary(items)));
			/** salary settle */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountSettleSalary(items)));
			/** salary other */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountOtherSalary(items)));
			
			/** IT EC-AN */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountITCommonDiseaseSalary(items)));
			/** IT AT-EP */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountITOccupationalDiseaseSalary(items)));
			/** IT M-P */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountITMaternitySalary(items)));
			/** IT other */
			renderCell(tr, footerStyle + centerColumnStyles, String.valueOf(getTotalCountITOtherSalary(items)));

			tr.endTR();

			return true;
		}
		
		private void renderCell(TableRowBuilder tr, String footerStyle, String rowValue){
			TableCellBuilder th = tr.startTH().className(footerStyle).align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			th.text(rowValue);
			th.endTH();
		}
		
		private Integer getTotalCountEmployee(List<ActivitySummaryObject> items){
			return items.size();
		}
		
		private Integer getTotalCountStartEmployee(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getStartCount()!=null){
					count += items.get(i).getStartCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountEndEmployee(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getEndCount()!=null){
					count += items.get(i).getEndCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getSalaryCount()!=null){
					count += items.get(i).getSalaryCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountExtraSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getSalaryExtraCount()!=null){
					count += items.get(i).getSalaryExtraCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountSettleSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getSalarySettleCount()!=null){
					count += items.get(i).getSalarySettleCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountOtherSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getSalaryOtherCount()!=null){
					count += items.get(i).getSalaryOtherCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountITCommonDiseaseSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getItCommonDiseaseCount()!=null){
					count += items.get(i).getItCommonDiseaseCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountITOccupationalDiseaseSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getItOccupationalDiseaseCount()!=null){
					count += items.get(i).getItOccupationalDiseaseCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountITMaternitySalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getItMaternityCount()!=null){
					count += items.get(i).getItMaternityCount();
				}
			}
			return count;
		}
		
		private Integer getTotalCountITOtherSalary(List<ActivitySummaryObject> items){
			int count=0;
			for (Integer i = 0; i < items.size(); i++) {
				if(items.get(i).getItOtherCount()!=null){
					count += items.get(i).getItOtherCount();
				}
			}
			return count;
		}
		
	}
	
	/**
	 * CUSTOM CELLTABLE
	 */
	private class CustomTableBuilder extends AbstractCellTableBuilder<ActivitySummaryObject> {

		private final String childCell = " ";
		private final String selectedRowStyle;
		private final String selectedCellStyle;

		public CustomTableBuilder() {
			super(dataGrid);

			// Cache styles for faster access.
			AbstractCellTable.Style style = dataGrid.getResources().style();
			selectedRowStyle = " " + style.selectedRow();
			selectedCellStyle = " " + style.selectedRowCell();
		}

		@Override
		public void buildRowImpl(ActivitySummaryObject rowValue, int absRowIndex) {
			buildContractRow(rowValue, absRowIndex, false);

//			int pendingSettle = (rowValue.getEndCount()!=null && rowValue.getSalarySettleCount()!=null)?(rowValue.getEndCount() - rowValue.getSalarySettleCount()):0;
//			if (pendingSettle > 0) {
//				TableRowBuilder row = startRow();
//				TableCellBuilder td = row.startTD().colSpan(12).className(cellStyle);
//				td.style().trustedBackgroundColor("#FBEFEF").endStyle();
//				td.text("Finiquitos pendientes de generar.").endTD();
//				row.endTR();
//			}

			if (showingContracts.contains(rowValue.getId())) {				
				for (ActivitySummaryObject contract : contractSummaryList) {
					buildContractRow(contract, 0, true);
				}
			}
		}

		private void buildContractRow(ActivitySummaryObject rowValue, int absRowIndex, boolean isInnerRow) {
			// Calculate the row styles.
			SelectionModel<? super ActivitySummaryObject> selectionModel = dataGrid.getSelectionModel();
			boolean isSelected = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
			boolean isEven = absRowIndex % 2 == 0;
			String separatorColumnStyles = " " + AON.AON_CSS.aonBorderRight() + " ";
			String centerColumnStyles = " " + AON.AON_TEXT_CENTER;
			StringBuilder trClasses = new StringBuilder("");
			if (isSelected) {
				trClasses.append(selectedRowStyle);
			} else if(!isInnerRow && isEven){
				trClasses.append(" "+AON.AON_DATA_TABLE_ROW_EVEN);
			} else if(!isInnerRow && !isEven){
				trClasses.append(" "+AON.AON_DATA_TABLE_ROW_ODD);
			}

			// Calculate the cell styles.
			String cellStyles = "";
			if (isSelected) {
				cellStyles += selectedCellStyle;
			}
			if (isInnerRow) {
				cellStyles += childCell;
			}

			TableRowBuilder tr = startRow();
			tr.className(trClasses.toString());
			if(isInnerRow){
				tr.style().fontStyle(FontStyle.ITALIC).trustedBackgroundColor("#EFF5FB").endStyle();
			}

			String innerRowValue = null;
			
			/** select column */
			innerRowValue = "";
			renderCell(tr, cellStyles, isInnerRow, 0, selectColumn, innerRowValue, rowValue);
			/** name */
			innerRowValue = rowValue.getFullname();
			renderCell(tr, cellStyles + separatorColumnStyles, isInnerRow, 1, nameColumn, innerRowValue, rowValue);
			
			/** start */
			innerRowValue = rowValue.getStartDate()!=null?DateTimeFormat.getFormat("dd/MM/yyyy").format(rowValue.getStartDate()):"-";
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 2, startCountColumn, innerRowValue, rowValue);
			/** end */
			innerRowValue = rowValue.getEndDate()!=null?DateTimeFormat.getFormat("dd/MM/yyyy").format(rowValue.getEndDate()):"-";
			renderCell(tr, cellStyles + centerColumnStyles + separatorColumnStyles, isInnerRow, 3, endCountColumn, innerRowValue, rowValue);
			
			/** salary */
			innerRowValue = String.valueOf(rowValue.getSalaryCount()!=null?rowValue.getSalaryCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 4, salaryCountColumn, innerRowValue, rowValue);
			/** salary extra */
			innerRowValue = String.valueOf(rowValue.getSalaryExtraCount()!=null?rowValue.getSalaryExtraCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 5, salaryExtraCountColumn, innerRowValue, rowValue);
			/** salary settle */
			innerRowValue = String.valueOf(rowValue.getSalarySettleCount()!=null?rowValue.getSalarySettleCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 6, salarySettleCountColumn, innerRowValue, rowValue);
			/** salary other */
			innerRowValue = String.valueOf(rowValue.getSalaryOtherCount()!=null?rowValue.getSalaryOtherCount():0);
			renderCell(tr, cellStyles + centerColumnStyles + separatorColumnStyles, isInnerRow, 7, salaryOtherCountColumn, innerRowValue, rowValue);
			
			/** it EC-AN */
			innerRowValue = String.valueOf(rowValue.getItCommonDiseaseCount()!=null?rowValue.getItCommonDiseaseCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 8, itCommonDiseaseCountColumn, innerRowValue, rowValue);
			/** it AT-EP */
			innerRowValue = String.valueOf(rowValue.getItOccupationalDiseaseCount()!=null?rowValue.getItOccupationalDiseaseCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 9, itOccupationalDiseaseCountColumn, innerRowValue, rowValue);
			/** it M-P */
			innerRowValue = String.valueOf(rowValue.getItMaternityCount()!=null?rowValue.getItMaternityCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 10, itMaternityCountColumn, innerRowValue, rowValue);
			/** it other */
			innerRowValue = String.valueOf(rowValue.getItOtherCount()!=null?rowValue.getItOtherCount():0);
			renderCell(tr, cellStyles + centerColumnStyles, isInnerRow, 11, itOtherCountColumn, innerRowValue, rowValue);

			tr.endTR();
		}
		
		private void renderCell(TableRowBuilder row, String cellStyles, boolean isInnerRow, int colNum, Column<ActivitySummaryObject, String> col, String innerValue, ActivitySummaryObject rowValue){
			TableCellBuilder td = row.startTD().className(cellStyles);
			if (isInnerRow) {
				td.text(innerValue!=null?innerValue:"-");
			} else {
				renderCell(td, createContext(colNum), col, rowValue);
			}
			td.endTD();
		}
	}

}
