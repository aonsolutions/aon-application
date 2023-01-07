package net.aonsolutions.aon.gwt.warehouse.client.elaboration.nuevo;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import net.aonsolutions.aon.gwt.warehouse.client.ElaborationService;
import net.aonsolutions.aon.gwt.warehouse.client.ElaborationServiceAsync;
import net.aonsolutions.aon.gwt.warehouse.client.ElaborationServiceAsyncDecorator;
import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

public class ElaborationGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, ElaborationGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/DataGrid.css")
		Style dataGridStyle();
	}

	private static final ElaborationServiceAsync ELABORATION_SERVICE;
	static {
		ElaborationServiceAsync raw = GWT.create(ElaborationService.class);
		ELABORATION_SERVICE = new ElaborationServiceAsyncDecorator(raw); 
	}
	
	
	@UiField(provided = true) CustomDataGrid<Elaboration> dataGrid;

	Integer cont = 0;

	boolean isFechaIVA = false;
	
	private ElaborationParams filterParams;
	private Occam occam;
	
	public ElaborationParams getFilterParams() {
		if(filterParams == null) filterParams = new ElaborationParams(); 
		return filterParams;
	}
	
	public int getPage() {
		return getFilterParams().getPage();
	}
	
	public void setPage(int page) {
		getFilterParams().setPage(page);
	}

	public static final ProvidesKey<Elaboration> PROVIDES_KEY = new ProvidesKey<Elaboration>() {
		@Override
		public Object getKey(Elaboration invoice) {
			return invoice == null ? null : invoice.getId();
		}
	};
	
	public void getElaborations(AsyncCallback<List<Elaboration>> callback) {
		ELABORATION_SERVICE.getElaborations(occam, getFilterParams(), callback);
	}
	
	public void setFilterParams(ElaborationParams filterParams) {
		this.filterParams = filterParams.setPage(1);	
		getElaborations(new AsyncCallback<List<Elaboration>>() {

			@Override
			public void onSuccess(List<Elaboration> result) {
				addDataDisplay(dataGrid, result);
				dataGrid.redraw();
			}

			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void setList(List<Elaboration> list) {
		addDataDisplay(dataGrid, list);
		dataGrid.redraw();
	}
	
	public ElaborationGrid(Occam occam, ElaborationParams filterParams) {
		this.filterParams = filterParams;
		this.occam = occam;
		
		dataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, resources, PROVIDES_KEY);
		dataGrid.getElement().getStyle().setMarginLeft(10, Unit.PX);
		dataGrid.getElement().getStyle().setMarginRight(10, Unit.PX);
		dataGrid.getElement().getStyle().setMarginBottom(10, Unit.PX);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {

			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					setPage(getPage() + 1);
					getElaborations(new AsyncCallback<List<Elaboration>>() {

						@Override
						public void onSuccess(List<Elaboration> result) {
							dataProvider.getList().addAll(result);
							dataGrid.redraw();
						}

						@Override public void onFailure(Throwable caught) {}
					});
					
				}
			}
		});
		dataGrid.addHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(cont < 2){
					dataGrid.redraw();
					cont++;
				}
			}
		}, MouseOverEvent.getType());

		getElaborations(new AsyncCallback<List<Elaboration>>() {
			
			@Override
			public void onSuccess(List<Elaboration> result) {
				load(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
					
			}
		});

		initWidget(binder.createAndBindUi(this));
	}

	private void load(List<Elaboration> list) {
		DefaultKeyboardSelectionHandler<Elaboration> selHandler = new DefaultKeyboardSelectionHandler<Elaboration>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<Elaboration> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 Elaboration object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(Elaboration f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }

//					parent.getSend().setVisible(selFiles.size() > 0);
//					String sii = parent.getFilterMap().get("sii").get(0);
//					parent.getBaja().setVisible(selFiles.size() > 0 && !"cp_cobros_pagos".equals(sii)
//							&& !"cp_cobros".equals(sii) && !"cp_pagos".equals(sii));
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<Elaboration> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<Invoice> selectionModel = new SingleSelectionModel<Invoice>(
	//			Invoice.PROVIDES_KEY);
		final MultiSelectionModel<Elaboration> selectionModel = new MultiSelectionModel<>(PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<Elaboration> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}

	//------------------------------ DataGrid Utils

	private ListDataProvider<Elaboration> dataProvider = new ListDataProvider<>();

	public void addDataDisplay(HasData<Elaboration> display, List<Elaboration> list) {
		dataProvider = new ListDataProvider<>(list);
		dataProvider.addDataDisplay(display);
	}

	private ListHandler<Elaboration> getSortHandler() {
		return new ListHandler<Elaboration>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<Elaboration> aux  = super.getList();
				List<Elaboration> aux2 = new LinkedList<>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(final MultiSelectionModel<Elaboration> selectionModel, ListHandler<Elaboration> sortHandler) {

		/** Reference Column **/
		Column<Elaboration, String> referenceColumn = new Column<Elaboration, String>(new TextCell()) {

			@Override
			public String getValue(Elaboration object) {
				String reference = AonStringUtils.isBlank(object.getSeries()) ? Integer.toString(object.getNumber()) : object.getSeries() + "/" + object.getNumber();
				return AonStringUtils.isBlank(object.getReferenceCode()) || "null".equalsIgnoreCase(object.getReferenceCode())
					? reference : object.getReferenceCode();
			}

		};
		referenceColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		referenceColumn.setSortable(true);
		sortHandler.setComparator(referenceColumn,new Comparator<Elaboration>() {

			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				return o1.getReferenceCode().compareTo(o2.getReferenceCode());
			}
		});
		dataGrid.getColumnSortList().push(referenceColumn);
		
		/** Description Column **/
		Column<Elaboration,String> descriptionColumn = new Column<Elaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(Elaboration object) {
				return object.getDescription();
			}
		};
		
		descriptionColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		descriptionColumn.setSortable(true); 
		sortHandler.setComparator(descriptionColumn,new Comparator<Elaboration>() {
			
			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		
		/** Quantity Column **/
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		Column<Elaboration, Number> quantityColumn = new Column<Elaboration, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(Elaboration object) {
				return object.getQuantity()!=null ? object.getQuantity() : null;
			}
		};
		
		quantityColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		quantityColumn.setSortable(true); 
		sortHandler.setComparator(quantityColumn,new Comparator<Elaboration>() {
			
			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				return o1.getQuantity().compareTo(o2.getQuantity());
			}
		});
		
		/** Warehouse Column **/
		Column<Elaboration,String> warehouseColumn = new Column<Elaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(Elaboration object) {
				return object.getWarehouse()!=null?object.getWarehouse().getName():" ";
			}
		};
		
		warehouseColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		warehouseColumn.setSortable(true); 
		sortHandler.setComparator(warehouseColumn,new Comparator<Elaboration>() {
			
			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				String w1 = o1.getWarehouse()!=null?o1.getWarehouse().getName():"";
				String w2 = o2.getWarehouse()!=null?o2.getWarehouse().getName():"";
				return w1.compareTo(w2);
			}
		});
		
		/** DATE Column **/
		Column<Elaboration,String> dateColumn = new Column<Elaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(Elaboration object) {
				return AonDateUtils.formatDate(object.getDate());
			}
		};
		
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<Elaboration>() {
			
			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		
		/** Status Column **/
		Column<Elaboration,String> statusColumn = new Column<Elaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(Elaboration object) {
				return object.getStatus() != null ? object.getStatus().getName() : "";
			}
		};
		
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<Elaboration>() {
			
			@Override
			public int compare(Elaboration o1, Elaboration o2) {
				return o1.getStatus().getName().compareTo(o2.getStatus().getName());
			}
		});

		dataGrid.addColumn(referenceColumn, AON.MSG.reference());
		dataGrid.addColumn(descriptionColumn, AON.MSG.description());
		dataGrid.addColumn(quantityColumn, AON.MSG.quantity());
		dataGrid.addColumn(warehouseColumn, "Almacen");
		dataGrid.addColumn(dateColumn, AON.MSG.date());
		dataGrid.addColumn(statusColumn, AON.MSG.status());
		
		dataGrid.setColumnWidth(referenceColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(descriptionColumn, 40, Unit.PCT);
		dataGrid.setColumnWidth(quantityColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(warehouseColumn, 20, Unit.PCT);
		dataGrid.setColumnWidth(dateColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	}
	
}
