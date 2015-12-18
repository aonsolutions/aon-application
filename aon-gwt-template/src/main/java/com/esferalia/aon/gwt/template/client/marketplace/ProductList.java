package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.Utils;
import com.esferalia.aon.gwt.template.shared.Product;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ProductList extends ResizeComposite{
	
	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);
	
	private static final ProductListBinder binder = GWT.create(ProductListBinder.class);
	
	interface ProductListBinder extends UiBinder<Widget, ProductList> {
		
	}

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/template/client/datagrid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<Product> dataGrid;
	@UiField(provided = true) TextBox nameSearchBox;
	@UiField Button nameSearchButton;
	@UiField(provided = true) TextBox codeSearchBox;
	@UiField Button codeSearchButton;
	
	private List<Product> list;
	private String login;
	private ListDataProvider<Product> dataProvider = new ListDataProvider<Product>();

	
	public ProductList(List<Product> list, String login) {
		setList(list);
		setLogin(login);
		
		dataGrid = new DataGrid<Product>(Integer.MAX_VALUE, resources); 
		
		nameSearchBox = new TextBox();
		nameSearchButton = new Button();

		codeSearchBox = new TextBox();
		codeSearchButton = new Button();
		
		
//		// Create some advanced options
////	    HorizontalPanel genderPanel = new HorizontalPanel();
////	    String[] genderOptions = constants.cwDisclosurePanelFormGenderOptions();
////	    for (int i = 0; i < genderOptions.length; i++) {
////	      genderPanel.add(new RadioButton("gender", genderOptions[i]));
////	    }
////	    Grid advancedOptions = new Grid(2, 2);
////	    advancedOptions.setCellSpacing(6);
////	    advancedOptions.setHTML(0, 0, "Option 1");
////	    advancedOptions.setWidget(0, 1, new TextBox());
////	    advancedOptions.setHTML(1, 0, "Option 2");
////	    advancedOptions.setWidget(1, 1, new TextBox());
//
//	    // Add advanced options to form in a disclosure panel
//	    DisclosurePanel advancedDisclosure = new DisclosurePanel("Opciones avanzadas");
//	    advancedDisclosure.setAnimationEnabled(true);
////	    advancedDisclosure.ensureDebugId("cwDisclosurePanel");
//	    advancedDisclosure.setContent(advancedOptions);
//	    layout.setWidget(3, 0, advancedDisclosure);
////	    cellFormatter.setColSpan(3, 0, 2);
		
		

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		load();
	}
	
	private void load() {
		loadSearchBox();
		loadDataGrid();
	}
	
	//------------------------------ Actions
	
	@UiHandler("nameSearchButton")
	void namebutton(ClickEvent event) {
		String searchStr = nameSearchBox.getText();
		Vector<Product> vaux = new Vector<Product>();
		vaux.addAll(getList());
 		
		impl.searchProductByName(searchStr, vaux, new AsyncCallback<Vector<Product>>() {
			@Override
			public void onSuccess(Vector<Product> result) {
				dataProvider = new ListDataProvider<Product>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}
			@Override
			public void onFailure(Throwable caught) {
		
			}
		});
		
	}

	@UiHandler("codeSearchButton")
	void codebutton(ClickEvent event) {
		String searchStr = codeSearchBox.getText();
		Vector<Product> vaux = new Vector<Product>();
		vaux.addAll(getList());
		
		impl.searchProductByName(searchStr, vaux, new AsyncCallback<Vector<Product>>() {
			@Override
			public void onSuccess(Vector<Product> result) {
				dataProvider = new ListDataProvider<Product>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
	}
	
	
	//------------------------------ DataGrid Utils
	
	private void loadSearchBox(){
		nameSearchBox.addBitlessDomHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				nameSearchButton.click();
			}
		}, ChangeEvent.getType());
		nameSearchBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					nameSearchButton.click();
				}
			}
		});

		codeSearchBox.addBitlessDomHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				codeSearchButton.click();
			}
		}, ChangeEvent.getType());
		codeSearchBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					codeSearchButton.click();
				}
			}
		});
	}
	
	private void loadDataGrid(){
		final SingleSelectionModel<Product> selectionModel = new SingleSelectionModel<Product>();
		dataGrid.setSelectionModel(selectionModel);
		dataGrid.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				Product selected = selectionModel.getSelectedObject();
				if (selected != null) {
				    new ProductValuesDialog(selected, getLogin()).show();
				}
			}
		}, DoubleClickEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		dataProvider = new ListDataProvider<Product>(getList());
		dataProvider.addDataDisplay(dataGrid);
		ListHandler<Product> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		
		initTableColumns(selectionModel, sortHandler);
	}
	
	
	private ListHandler<Product> getSortHandler() {
		return new ListHandler<Product>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<Product> aux = super.getList();
				List<Product> aux2 = new Vector<Product>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(
			final SelectionModel<Product> selectionModel,
			ListHandler<Product> sortHandler) {
		
		/**
		 * Select Column
		 */
//		Column<Product, String> selectColumn = new Column<Product, String>(new ButtonCell()) {
//			@Override
//			public String getValue(Product object) {
//				return "->";
//			}
//		};
//		selectColumn.setFieldUpdater(new FieldUpdater<Product, String>() {
//			public void update(int index, Product object, String value) {
//				Window.alert("You clicked: " + value);
//			}
//		});
//		selectColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
//		dataGrid.addColumn(selectColumn, "", "");
//		dataGrid.setColumnWidth(selectColumn, 40, Unit.PX);
		
		/**
		 * Code Column
		 */
		Column<Product, String> codeColumn = new Column<Product, String>(
				new TextCell()) {
			@Override
			public void render(Context context, Product object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" + object.getCode() + "</span>");
			}
			@Override
			public String getValue(Product object) {
				return object.getCode();
			}
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true);
		sortHandler.setComparator(codeColumn,
				new Comparator<Product>() {
					@Override
					public int compare(Product o1, Product o2) {
						return o1.getCode().compareTo(o2.getCode());
					}
				});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "C\u00f3digo");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);
		
		/** 
		 * Name Column 
		 */
		Column<Product, String> nameColumn = new Column<Product, String>(
				new TextCell()) {
			@Override
			public String getValue(Product object) {
				return object.getName();
			}
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn,
				new Comparator<Product>() {
					@Override
					public int compare(Product o1, Product o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Nombre");
		dataGrid.setColumnWidth(nameColumn, 85, Unit.PCT);
	
	}
	
	
	//------------------------------ Getters & Setters
	
	public List<Product> getList(){
		return list;
	}
	
	public void setList(List<Product> list){
		this.list = list;
	}
	
	public String getLogin(){
		return login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	
}
