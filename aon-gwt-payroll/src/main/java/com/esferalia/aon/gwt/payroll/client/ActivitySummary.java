package com.esferalia.aon.gwt.payroll.client;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.ListDataProvider;
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
	@UiField(provided = true) DataGrid<ActivitySummaryObject> dataGrid;
	
	
	private List<ActivitySummaryObject> summaryList;
	private ListDataProvider<ActivitySummaryObject> dataProvider;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(
				MainEntryPoint.CodeMirrorResources.class).css()
				.ensureInjected();

		startDate = new DateBoxEx();
		endDate = new DateBoxEx();
		dataGrid = new DataGrid<ActivitySummaryObject>(Integer.MAX_VALUE, resources); 
		
		Widget ui = binder.createAndBindUi(this);
		

		RootLayoutPanel.get("rootPanel").add(ui);
		
		initSearchBox();
		loadData();
		

		impl.getParentDomain(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Integer parentDomain) {
				loadDataGrid(parentDomain==null);
			}
		});
		
	}
	
	private List<ActivitySummaryObject> getSummaryList() {
		return summaryList;
	}
	
	private void initSearchBox(){
		Date start = new Date();
		CalendarUtil.setToFirstDayOfMonth(start);
		startDate.setValue(start);
	
		Date end = new Date();
		CalendarUtil.setToFirstDayOfMonth(end);
		CalendarUtil.addMonthsToDate(end, 1);
		CalendarUtil.addDaysToDate(end, -1);
		endDate.setValue(end);
		
		startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				loadData();
			}
		});
		startDate.addDomHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				loadData();
			}
		}, BlurEvent.getType());
		
		endDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				loadData();
			}
		});
		endDate.addDomHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				loadData();
			}
		}, BlurEvent.getType());
	}
	
	private void loadData() {
		 
		summaryList = new LinkedList<>();
		dataProvider = new ListDataProvider<>();
		
		impl.getActivitySummary(startDate.getValue(), endDate.getValue(), new AsyncCallback<List<ActivitySummaryObject>>() {
			@Override
			public void onSuccess(List<ActivitySummaryObject> result) {
				summaryList = result;
				dataProvider = new ListDataProvider<ActivitySummaryObject>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
				dataGrid.redrawFooters();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}
	
	
	private void loadDataGrid(boolean isParent){
		final SingleSelectionModel<ActivitySummaryObject> selectionModel = new SingleSelectionModel<ActivitySummaryObject>();
		dataGrid.setSelectionModel(selectionModel);
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay datos."));
		dataProvider = new ListDataProvider<ActivitySummaryObject>(getSummaryList());
		dataProvider.addDataDisplay(dataGrid);
		ListHandler<ActivitySummaryObject> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		
		initTableColumns(isParent, sortHandler);
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

	private void initTableColumns(boolean isParent, ListHandler<ActivitySummaryObject> sortHandler) {
		
		/**
		 * Name Column
		 */
		Column<ActivitySummaryObject, String> nameColumn = new Column<ActivitySummaryObject, String>(
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
		SafeHtmlHeader nameHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant(isParent?"Empresa":"Trabajador"));
		nameHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> nameFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountEmployee());
			}
		};
		dataGrid.addColumn(nameColumn, nameHeader, nameFooter);
		dataGrid.setColumnWidth(nameColumn, 80, Unit.PCT);
		dataGrid.getColumnSortList().push(nameColumn);
		
		if(!isParent) {
			/**
			 * Start Column
			 */
			Column<ActivitySummaryObject, Date> startDateColumn = new Column<ActivitySummaryObject, Date>(
					new DateCell(DateTimeFormat.getFormat(DATE_FORMAT))) {
				@Override
				public Date getValue(ActivitySummaryObject object) {
					return object.getStartDate();
				}
			};
			startDateColumn.setSortable(true);
			sortHandler.setComparator(startDateColumn,
					new Comparator<ActivitySummaryObject>() {
				@Override
				public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
			});
			SafeHtmlHeader startDateHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Alta"));
			startDateHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
			dataGrid.addColumn(startDateColumn, startDateHeader, null);
			dataGrid.setColumnWidth(startDateColumn, 15, Unit.EM);
			
			/**
			 * End Column
			 */
			Column<ActivitySummaryObject, Date> endDateColumn = new Column<ActivitySummaryObject, Date>(
					new DateCell(DateTimeFormat.getFormat(DATE_FORMAT))) {
				@Override
				public Date getValue(ActivitySummaryObject object) {
					return object.getEndDate();
				}
			};
			endDateColumn.setSortable(true);
			sortHandler.setComparator(endDateColumn,
					new Comparator<ActivitySummaryObject>() {
				@Override
				public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
					return o1.getEndDate().compareTo(o2.getEndDate());
				}
			});
			SafeHtmlHeader endDateHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Baja"));
			endDateHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
			dataGrid.addColumn(endDateColumn, endDateHeader, null);
			dataGrid.setColumnWidth(endDateColumn, 15, Unit.EM);
		
		} else {
			
			/**
			 * Start Column
			 */
			Column<ActivitySummaryObject, String> startDateColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return String.valueOf(object.getStartCount());
				}
			};
			startDateColumn.setSortable(true);
			sortHandler.setComparator(startDateColumn,
					new Comparator<ActivitySummaryObject>() {
				@Override
				public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
			});
			SafeHtmlHeader startHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Altas"));
			startHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
			Header<String> startFooter = new Header<String>(new TextCell()) {
				@Override
				public String getValue() {
					return String.valueOf(getTotalCountStartEmployee());
				}
			};
			dataGrid.addColumn(startDateColumn, startHeader, startFooter);
			dataGrid.setColumnWidth(startDateColumn, 15, Unit.EM);
			
			/**
			 * End Column
			 */
			Column<ActivitySummaryObject, String> endDateColumn = new Column<ActivitySummaryObject, String>(
					new TextCell()) {
				@Override
				public String getValue(ActivitySummaryObject object) {
					return String.valueOf(object.getEndCount());
				}
			};
			endDateColumn.setSortable(true);
			sortHandler.setComparator(endDateColumn,
					new Comparator<ActivitySummaryObject>() {
				@Override
				public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
					return o1.getEndDate().compareTo(o2.getEndDate());
				}
			});
			SafeHtmlHeader endHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Bajas"));
			endHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
			Header<String> endFooter = new Header<String>(new TextCell()) {
				@Override
				public String getValue() {
					return String.valueOf(getTotalCountEndEmployee());
				}
			};
			dataGrid.addColumn(endDateColumn, endHeader, endFooter);
			dataGrid.setColumnWidth(endDateColumn, 15, Unit.EM);
		}
		
		/**
		 * SalaryCount Column
		 */
		Column<ActivitySummaryObject, String> salaryCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryCount()!=null?object.getSalaryCount().toString():"0";
			}
		};
		salaryCountColumn.setSortable(true);
		sortHandler.setComparator(salaryCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getSalaryCount().compareTo(o2.getSalaryCount());
			}
		});
		SafeHtmlHeader salaryCountHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Nom."));
		salaryCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> salaryCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountSalary());
			}
		};
		dataGrid.addColumn(salaryCountColumn, salaryCountHeader, salaryCountFooter);
		dataGrid.setColumnWidth(salaryCountColumn, 7, Unit.EM);
		
		/**
		 * SalaryExtraCount Column
		 */
		Column<ActivitySummaryObject, String> salaryExtraCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryExtraCount()!=null?object.getSalaryExtraCount().toString():"0";
			}
		};
		salaryExtraCountColumn.setSortable(true);
		sortHandler.setComparator(salaryExtraCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getSalaryExtraCount().compareTo(o2.getSalaryExtraCount());
			}
		});
		SafeHtmlHeader salaryExtraCountHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Ext."));
		salaryExtraCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> salaryExtraCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountExtraSalary());
			}
		};
		dataGrid.addColumn(salaryExtraCountColumn, salaryExtraCountHeader, salaryExtraCountFooter);
		dataGrid.setColumnWidth(salaryExtraCountColumn, 7, Unit.EM);
		
		/**
		 * SalaryOtherCount Column
		 */
		Column<ActivitySummaryObject, String> salaryOtherCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getSalaryOtherCount()!=null?object.getSalaryOtherCount().toString():"0";
			}
		};
		salaryOtherCountColumn.setSortable(true);
		sortHandler.setComparator(salaryOtherCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getSalaryOtherCount().compareTo(o2.getSalaryOtherCount());
			}
		});
		SafeHtmlHeader salaryOtherCountHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("Otros"));
		salaryOtherCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> salaryOtherCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountOtherSalary());
			}
		};
		dataGrid.addColumn(salaryOtherCountColumn, salaryOtherCountHeader, salaryOtherCountFooter);
		dataGrid.setColumnWidth(salaryOtherCountColumn, 7, Unit.EM);
		
		/**
		 * IT Common Disease Column
		 */
		Column<ActivitySummaryObject, String> itCommonDiseaseCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItCommonDiseaseCount()!=null?object.getItCommonDiseaseCount().toString():"0";
			}
		};
		itCommonDiseaseCountColumn.setSortable(true);
		sortHandler.setComparator(itCommonDiseaseCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getItCommonDiseaseCount().compareTo(o2.getItCommonDiseaseCount());
			}
		});
		SafeHtmlHeader itCommonDiseaseCountHeader = new SafeHtmlHeader(
				SafeHtmlUtils.fromTrustedString("<span title=\"Enfermedad comun - Accidente no laboral\">IT EC/AN</span>"));
		itCommonDiseaseCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> itCommonDiseaseCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountITCommonDiseaseSalary());
			}
		};
		dataGrid.addColumn(itCommonDiseaseCountColumn, itCommonDiseaseCountHeader, itCommonDiseaseCountFooter);
		dataGrid.setColumnWidth(itCommonDiseaseCountColumn, 7, Unit.EM);
		
		/**
		 * IT Occupational Disease Column
		 */
		Column<ActivitySummaryObject, String> itOccupationalDiseaseCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItOccupationalDiseaseCount()!=null?object.getItOccupationalDiseaseCount().toString():"0";
			}
		};
		itOccupationalDiseaseCountColumn.setSortable(true);
		sortHandler.setComparator(itOccupationalDiseaseCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getItOccupationalDiseaseCount().compareTo(o2.getItOccupationalDiseaseCount());
			}
		});
		SafeHtmlHeader itOccupationalDiseaseCountHeader = new SafeHtmlHeader(
				SafeHtmlUtils.fromTrustedString("<span title=\"Accidente de trabajo - Enfermedad profesional\">IT AT/EP</span>"));
		itOccupationalDiseaseCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> itOccupationalDiseaseCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountITOccupationalDiseaseSalary());
			}
		};
		dataGrid.addColumn(itOccupationalDiseaseCountColumn, itOccupationalDiseaseCountHeader, itOccupationalDiseaseCountFooter);
		dataGrid.setColumnWidth(itOccupationalDiseaseCountColumn, 7, Unit.EM);
		
		/**
		 * IT Maternity Column
		 */
		Column<ActivitySummaryObject, String> itMaternityCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItMaternityCount()!=null?object.getItMaternityCount().toString():"0";
			}
		};
		itMaternityCountColumn.setSortable(true);
		sortHandler.setComparator(itMaternityCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getItMaternityCount().compareTo(o2.getItMaternityCount());
			}
		});
		SafeHtmlHeader itMaternityCountHeader = new SafeHtmlHeader(
				SafeHtmlUtils.fromTrustedString("<span title=\"Maternidad - Paternidad\">IT M/P</span>"));
		itMaternityCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> itMaternityCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountITMaternitySalary());
			}
		};
		dataGrid.addColumn(itMaternityCountColumn, itMaternityCountHeader, itMaternityCountFooter);
		dataGrid.setColumnWidth(itMaternityCountColumn, 7, Unit.EM);

		/**
		 * IT Other Column
		 */
		Column<ActivitySummaryObject, String> itOtherCountColumn = new Column<ActivitySummaryObject, String>(
				new TextCell()) {
			@Override
			public String getValue(ActivitySummaryObject object) {
				return object.getItOtherCount()!=null?object.getItOtherCount().toString():"0";
			}
		};
		itOtherCountColumn.setSortable(true);
		sortHandler.setComparator(itOtherCountColumn,
				new Comparator<ActivitySummaryObject>() {
			@Override
			public int compare(ActivitySummaryObject o1, ActivitySummaryObject o2) {
				return o1.getItOtherCount().compareTo(o2.getItOtherCount());
			}
		});
		SafeHtmlHeader itOtherCountHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("IT Otros"));
		itOtherCountHeader.setHeaderStyleNames(AON.AON_TEXT_LEFT);
		Header<String> itOtherCountFooter = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return String.valueOf(getTotalCountITOtherSalary());
			}
		};
		dataGrid.addColumn(itOtherCountColumn, itOtherCountHeader, itOtherCountFooter);
		dataGrid.setColumnWidth(itOtherCountColumn, 7, Unit.EM);
	}
	
	private Integer getTotalCountEmployee(){
		return getSummaryList().size();
	}
	
	private Integer getTotalCountStartEmployee(){
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getStartCount()!=null){
				count += getSummaryList().get(i).getStartCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountEndEmployee(){
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getEndCount()!=null){
				count += getSummaryList().get(i).getEndCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountSalary(){
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getSalaryCount()!=null){
				count += getSummaryList().get(i).getSalaryCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountExtraSalary(){
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getSalaryExtraCount()!=null){
				count += getSummaryList().get(i).getSalaryExtraCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountOtherSalary(){
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getSalaryOtherCount()!=null){
				count += getSummaryList().get(i).getSalaryOtherCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountITCommonDiseaseSalary() {
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getItCommonDiseaseCount()!=null){
				count += getSummaryList().get(i).getItCommonDiseaseCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountITOccupationalDiseaseSalary() {
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getItOccupationalDiseaseCount()!=null){
				count += getSummaryList().get(i).getItOccupationalDiseaseCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountITMaternitySalary() {
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getItMaternityCount()!=null){
				count += getSummaryList().get(i).getItMaternityCount();
			}
		}
		return count;
	}
	
	private Integer getTotalCountITOtherSalary() {
		int count=0;
		for (Integer i = 0; i < getSummaryList().size(); i++) {
			if(getSummaryList().get(i).getItOtherCount()!=null){
				count += getSummaryList().get(i).getItOtherCount();
			}
		}
		return count;
	}

}
