package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.SiiService;
import com.esferalia.aon.gwt.fiscal.client.SiiServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.SiiServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvestAssetParams;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
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

public abstract class InvestAssetGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, InvestAssetGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/DataGrid.css")
		Style dataGridStyle();
	}

	private static final SiiServiceAsync SII_SERVICE;
	static {
		SiiServiceAsync siiServiceRaw = GWT.create(SiiService.class);
		SII_SERVICE = new SiiServiceAsyncDecorator(siiServiceRaw); 
	}
	
	
	@UiField(provided = true) CustomDataGrid<InvestAsset> dataGrid;

	Integer cont = 0;

	boolean isFechaIVA = true;
	
	private InvestAssetParams filterParams;
	private FiscalModelModuleOptions<FiscalModel> options;
	
	public InvestAssetParams getFilterParams() {
		if(filterParams == null) filterParams = new InvestAssetParams(); 
		return filterParams;
	}
	
	public int getPage() {
		return getFilterParams().getPage();
	}
	
	public void setPage(int page) {
		getFilterParams().setPage(page);
	}

	public static final ProvidesKey<InvestAsset> PROVIDES_KEY = new ProvidesKey<InvestAsset>() {
		@Override
		public Object getKey(InvestAsset investAsset) {
			return investAsset == null ? null : investAsset.getId();
		}
	};
	
	public void getInvestAssets(AsyncCallback<List<InvestAsset>> callback) {
//		SII_SERVICE.getInvestAssets(options.getDomainName(), options.getDomain(), options.getUser(), getFilterParams(), callback);
	}
	
	public void setFilterParams(InvestAssetParams filterParams) {
		this.filterParams = filterParams.setPage(1);	
		getInvestAssets(new AsyncCallback<List<InvestAsset>>() {

			@Override
			public void onSuccess(List<InvestAsset> result) {
				selFiles = new LinkedList<>();
				select(selFiles);
				addDataDisplay(dataGrid, result);
				dataGrid.redraw();
			}

			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void setList(List<InvestAsset> list) {
		selFiles = new LinkedList<>();
		select(selFiles);
		addDataDisplay(dataGrid, list);
		dataGrid.redraw();
	}
	
	public InvestAssetGrid(FiscalModelModuleOptions<FiscalModel> options, InvestAssetParams filterParams) {
		this.filterParams = filterParams;
		this.options = options;
		
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
					getInvestAssets(new AsyncCallback<List<InvestAsset>>() {

						@Override
						public void onSuccess(List<InvestAsset> result) {
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

		getInvestAssets(new AsyncCallback<List<InvestAsset>>() {
			
			@Override
			public void onSuccess(List<InvestAsset> result) {
				load(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
					
			}
		});

		initWidget(binder.createAndBindUi(this));
	}

	LinkedList<InvestAsset> selFiles = new LinkedList<>();
	private void load(List<InvestAsset> list) {
		DefaultKeyboardSelectionHandler<InvestAsset> selHandler = new DefaultKeyboardSelectionHandler<InvestAsset>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<InvestAsset> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 InvestAsset object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(InvestAsset f : dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }

					selFiles = new LinkedList<>();
					for(InvestAsset f :dataProvider.getList()){
						if(dataGrid.getSelectionModel().isSelected(f)){
							selFiles.add(f);
						}
					}
					select(selFiles);
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
		ListHandler<InvestAsset> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<InvestAsset> selectionModel = new SingleSelectionModel<InvestAsset>(
	//			InvestAsset.PROVIDES_KEY);
		final MultiSelectionModel<InvestAsset> selectionModel = new MultiSelectionModel<>(PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<InvestAsset> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}

	//------------------------------ DataGrid Utils

	private ListDataProvider<InvestAsset> dataProvider = new ListDataProvider<>();

	public void addDataDisplay(HasData<InvestAsset> display, List<InvestAsset> list) {
		dataProvider = new ListDataProvider<>(list);
		dataProvider.addDataDisplay(display);
	}

	private ListHandler<InvestAsset> getSortHandler() {
		return new ListHandler<InvestAsset>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<InvestAsset> aux  = super.getList();
				List<InvestAsset> aux2 = new LinkedList<>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(final MultiSelectionModel<InvestAsset> selectionModel, ListHandler<InvestAsset> sortHandler) {

		/** Check Column **/
		Column<InvestAsset, Boolean> checkColumn = new Column<InvestAsset, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {

			@Override
			public Boolean getValue(InvestAsset object) {
				return selectionModel.isSelected(object);
			}
		};

		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
		dataGrid.setColumnWidth(checkColumn, 3, Unit.PCT);

		/** code Column **/
		Column<InvestAsset, String> codeColumn = new Column<InvestAsset, String>(new TextCell()) {

			@Override
			public void render(Context context, InvestAsset object, SafeHtmlBuilder sb) {
				String icon = "unarchive";
				if(InvoiceType.PURCHASE.equals(object.getType()) || InvoiceType.EXPENSES.equals(object.getType()))
					icon = "archive";
				else if(InvoiceType.UNDEDUCTIBLE.equals(object.getType()))
					icon = "receipt";
				sb.appendHtmlConstant( "<i class=\"material-icons\" style='font-size:16px;position:absolute;'>" + icon + "</i>" +  "<span style='padding-left:20px;'>" + object.getDescription());		
			}
			
			@Override
			public String getValue(InvestAsset object) {
				return "";
			}

		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true);
		sortHandler.setComparator(codeColumn,new Comparator<InvestAsset>() {

			@Override
			public int compare(InvestAsset o1, InvestAsset o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "Referencia");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);

		/** VAT DATE Column **/
		Column<InvestAsset, String> taxDateColumn = new Column<InvestAsset, String>(new TextCell()) {

			@Override
			public String getValue(InvestAsset object) {
				return "";
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true);
		sortHandler.setComparator(taxDateColumn,new Comparator<InvestAsset>() {

			@Override
			public int compare(InvestAsset o1, InvestAsset o2) {
				return 1;
			}
		});
		dataGrid.getColumnSortList().push(taxDateColumn);
		dataGrid.addColumn(taxDateColumn, "Fecha Fact.");
		dataGrid.setColumnWidth(taxDateColumn, 7.5, Unit.PCT);

		/** VAT DATE Column **/
		Column<InvestAsset, String> creationDateColumn = new Column<InvestAsset, String>(new TextCell()) {

			@Override
			public String getValue(InvestAsset object) {
					return "";
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true);
		sortHandler.setComparator(taxDateColumn,new Comparator<InvestAsset>() {

			@Override
			public int compare(InvestAsset o1, InvestAsset o2) {
				return 1;
			}
		});
		dataGrid.getColumnSortList().push(creationDateColumn);
		dataGrid.addColumn(creationDateColumn, "Fecha Op.");
		dataGrid.setColumnWidth(creationDateColumn, 7.5, Unit.PCT);

		/** Contraparte Column **/
		Column<InvestAsset, String> contraparteColumn = new Column<InvestAsset, String>(new TextCell()) {

			@Override
			public String getValue(InvestAsset object) {
				return "";
			}
		};
		contraparteColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		contraparteColumn.setSortable(true);
		sortHandler.setComparator(contraparteColumn,new Comparator<InvestAsset>() {

			@Override
			public int compare(InvestAsset o1, InvestAsset o2) {
				return 1;
			}
		});
		dataGrid.getColumnSortList().push(contraparteColumn);
		dataGrid.addColumn(contraparteColumn, "Contraparte");
		dataGrid.setColumnWidth(contraparteColumn, 15, Unit.PCT);

		/** Status Column **/

		Column<InvestAsset, String> statusColumn = new Column<InvestAsset, String>(new TextCell()) {
			@Override
			public void render(Context context, InvestAsset object, SafeHtmlBuilder sb) {
				String color = "gray";
				String status = "Pendiente";
//				if(object.getInvoiceInfo().getStatus().isAccepted()) {
//					color = "green";
//					status = "Aceptada";
//				} else if(object.getInvoiceInfo().getStatus().isAcceptedWithErrors()) {
//					color = "orange";
//					status = "Aceptada con Errores";
//				} else if(object.getInvoiceInfo().getStatus().isWrong()) {
//					color = "red";
//					status = "Incorrecta";
//				} else if(object.getInvoiceInfo().getStatus().isAnnulled()) {
//					color = "red";
//					status = "Anulada";
//				}
				sb.appendHtmlConstant( "<i class=\"material-icons\" style='font-size:16px;position:absolute;color:"+ color +";'>circle</i>" +  "<span style='padding-left:20px;'>"+ status);
			}

			@Override
			public String getValue(InvestAsset object) {
				return ""; 
			}

		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true);
		sortHandler.setComparator(statusColumn,new Comparator<InvestAsset>() {

			@Override
			public int compare(InvestAsset o1, InvestAsset o2) {
				return 1;
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);

		/** Info Column **/

		List<HasCell<InvestAsset, ?>> cells = new LinkedList<HasCell<InvestAsset, ?>>();

		cells.add(new ActionHasCell("info", new Delegate<InvestAsset>() {
	        @Override public void execute(InvestAsset object) {
	        	info(object.getId(), object.getDescription());
	        }
	    }));

		cells.add(new ActionHasCell("download", new Delegate<InvestAsset>() {
	        @Override public void execute(InvestAsset object) {
	        	download(object);
	        }
	    }));

		CompositeCell<InvestAsset> cell = new CompositeCell<InvestAsset>(cells);

		Column<InvestAsset,InvestAsset> infoColumn = 	new Column<InvestAsset, InvestAsset>(cell){

			@Override
			public InvestAsset getValue(InvestAsset object) {
				return object;
			}
		};
		infoColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(infoColumn, " ");
		dataGrid.setColumnWidth(infoColumn, 6, Unit.PCT);
	}

	public abstract void info(Integer invoice, String reference);
	public abstract void download(InvestAsset object);
	public abstract void select(List<InvestAsset> selFiles);
	


	public final class CheckboxHeader extends Header {

	    private final MultiSelectionModel<InvestAsset> selectionModel;
	    private final ListDataProvider<InvestAsset> provider;

	    public CheckboxHeader(MultiSelectionModel<InvestAsset> selectionModel,
	    		ListDataProvider<InvestAsset> provider) {
	        super(new CheckboxCell());
	        this.selectionModel = selectionModel;
	        this.provider = provider;
	    }

	    @Override
	    public Boolean getValue() {
	        boolean allItemsSelected = selectionModel.getSelectedSet().size() == provider
	                .getList().size();
	        return allItemsSelected;
	    }

	    @Override
	    public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
	        InputElement input = elem.getFirstChild().cast();
	        Boolean isChecked = input.isChecked();
//	        parent.getSend().setVisible(isChecked);
//	        parent.getBaja().setVisible(isChecked);
	        for (InvestAsset element : provider.getList()) {
	            selectionModel.setSelected(element, isChecked);
	            if(isChecked){
					selFiles.add(element);
				}
	        }
	        if(!isChecked){
	        	selFiles = new LinkedList<>();
	        }
	    }
	}

	private class ActionHasCell implements HasCell<InvestAsset, InvestAsset> {
	    private ActionCell<InvestAsset> cell;
	    String s;

	    public ActionHasCell(String text, Delegate<InvestAsset> delegate) {
	    	s = text;
	        cell = new ActionCell<InvestAsset>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,InvestAsset value, SafeHtmlBuilder sb) {
	        		if(text.equals("info")){
	        			String icon = "aon-icon-info";
	        			sb.appendHtmlConstant("<button  alt=\""+ "Informaci�n SII" +"\" type=\"button\" class=\"aon-editDataTable-button " + icon + "\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        		if(text.equals("download")){
	        			String icon = "aon-icon-download";
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button " + icon + "\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        	}
	        };

	    }

		@Override
		public InvestAsset getValue(InvestAsset object) {
			return object;
		}


		@Override
		public Cell<InvestAsset> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<InvestAsset, InvestAsset> getFieldUpdater() {
			return null;
		}
	}
}
