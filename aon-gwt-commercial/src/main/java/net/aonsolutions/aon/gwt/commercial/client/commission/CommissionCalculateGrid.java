package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.commercial.JsCommission;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
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
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class CommissionCalculateGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, CommissionCalculateGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	} 
	
	@UiField(provided = true) CustomDataGrid<JsCommission> dataGrid; 
	
	CommissionCalculate parent;
	Integer cont = 0;

	public CommissionCalculateGrid(CommissionCalculate parent, LinkedList<JsCommission> list) {

		this.parent = parent;
		dataGrid = new CustomDataGrid<JsCommission>(Integer.MAX_VALUE, resources, JsCommission.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){

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
		load(list);	
		initWidget(binder.createAndBindUi(this));
	}	
	
	private void load(LinkedList<JsCommission> list) {
		DefaultKeyboardSelectionHandler<JsCommission> selHandler = new DefaultKeyboardSelectionHandler<JsCommission>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsCommission> event) {
				
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsCommission> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsCommission> selectionModel = new SingleSelectionModel<JsCommission>(
 				JsCommission.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsCommission> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsCommission> dataProvider = new ListDataProvider<JsCommission>();

	public void addDataDisplay(HasData<JsCommission> display, LinkedList<JsCommission> list) {
		dataProvider = new ListDataProvider<JsCommission>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsCommission> getSortHandler() {
		return new ListHandler<JsCommission>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsCommission> aux  = super.getList();
				List<JsCommission> aux2 = new LinkedList<JsCommission>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsCommission> selectionModel, ListHandler<JsCommission> sortHandler) {

		/** COMERCIAL / COMMERCIAL **/
		Column<JsCommission, String> commercialColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getSeller();
			}
		
		};
		commercialColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		commercialColumn.setSortable(true); 
		sortHandler.setComparator(commercialColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getSeller().compareTo(o2.getSeller());
			}
		});
		dataGrid.getColumnSortList().push(commercialColumn);
		dataGrid.addColumn(commercialColumn, "COMERCIAL");
		dataGrid.setColumnWidth(commercialColumn, 40, Unit.PCT);

		
		/** PRESUPUESTO / OFFER **/
		Column<JsCommission, String> offerColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getDescription();
			}
		
		};
		offerColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		offerColumn.setSortable(true); 
		sortHandler.setComparator(offerColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		dataGrid.getColumnSortList().push(offerColumn);
		dataGrid.addColumn(offerColumn, "PRESUPUESTO");
		dataGrid.setColumnWidth(offerColumn, 25, Unit.PCT);
		
		/** PRODUCTO / PRODUCT **/
		Column<JsCommission, String> productColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getProduct();
			}
		
		};
		productColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		productColumn.setSortable(true); 
		sortHandler.setComparator(productColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getProduct().compareTo(o2.getProduct());
			}
		});
		dataGrid.getColumnSortList().push(productColumn);
		dataGrid.addColumn(productColumn, "PRODUCT");
		dataGrid.setColumnWidth(productColumn, 40, Unit.PCT);
		
		/** BASE IMPONIBLE / TAXABLE BASE **/
		Column<JsCommission, String> baseColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getBase() + "";
			}
		
		};
		baseColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		baseColumn.setSortable(true); 
		sortHandler.setComparator(baseColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getBase().compareTo(o2.getBase());
			}
		});
		dataGrid.getColumnSortList().push(baseColumn);
		dataGrid.addColumn(baseColumn, "BASE IMPONIBLE");
		dataGrid.setColumnWidth(baseColumn, 30, Unit.PCT);
		
		/** PORCENTAJE / PERCENTAGE **/
		Column<JsCommission, String> percentageColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getPercentage() + "";
			}
		
		};
		percentageColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		percentageColumn.setSortable(true); 
		sortHandler.setComparator(percentageColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getPercentage().compareTo(o2.getPercentage());
			}
		});
		dataGrid.getColumnSortList().push(percentageColumn);
		dataGrid.addColumn(percentageColumn, "PORCENTAJE");
		dataGrid.setColumnWidth(percentageColumn, 25, Unit.PCT);
		
	    
		/** IMPORTE / AMOUNT **/
		
		Column<JsCommission, String> amountColumn = new Column<JsCommission, String>(new EditTextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getAmount() + "";
			}
		
		};
		amountColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		amountColumn.setSortable(true);
		sortHandler.setComparator(amountColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getAmount().compareTo(o2.getAmount());
			}
		});
		dataGrid.getColumnSortList().push(amountColumn);
		dataGrid.addColumn(amountColumn, "IMPORTE");

		amountColumn.setFieldUpdater(new FieldUpdater<JsCommission, String>() {
	          @Override
	          public void update(int index, JsCommission object, String value) {
	            // Called when the user changes the value.
	          }
	    });
		dataGrid.setColumnWidth(amountColumn, 20, Unit.PCT);
		
		/** ESTADO / STATUS **/
		Column<JsCommission, String> statusColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getStatus();
			}
		
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true);
		sortHandler.setComparator(statusColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getStatus().compareTo(o2.getStatus());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "ESTADO");
		dataGrid.setColumnWidth(statusColumn, 20, Unit.PCT);
	}
}
