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
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public abstract class InvoiceGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, InvoiceGrid> {
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
	
	
	@UiField(provided = true) CustomDataGrid<Invoice> dataGrid;

	Integer cont = 0;

	boolean isFechaIVA = false;
	
	private InvoiceParams filterParams;
	private FiscalModelModuleOptions<FiscalModel> options;
	
	public InvoiceParams getFilterParams() {
		if(filterParams == null) filterParams = new InvoiceParams(); 
		return filterParams;
	}
	
	public int getPage() {
		return getFilterParams().getPage();
	}
	
	public void setPage(int page) {
		getFilterParams().setPage(page);
	}

	public static final ProvidesKey<Invoice> PROVIDES_KEY = new ProvidesKey<Invoice>() {
		@Override
		public Object getKey(Invoice invoice) {
			return invoice == null ? null : invoice.getId();
		}
	};
	
	public void getInvoices(AsyncCallback<List<Invoice>> callback) {
		SII_SERVICE.getInvoices(options.getDomainName(), options.getDomain(), options.getUser(), getFilterParams(), callback);
	}
	
	public void setFilterParams(InvoiceParams filterParams) {
		this.filterParams = filterParams.setPage(1);	
		getInvoices(new AsyncCallback<List<Invoice>>() {

			@Override
			public void onSuccess(List<Invoice> result) {
				selFiles = new LinkedList<>();
				select(selFiles);
				addDataDisplay(dataGrid, result);
				dataGrid.redraw();
			}

			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void setList(List<Invoice> list) {
		selFiles = new LinkedList<>();
		select(selFiles);
		addDataDisplay(dataGrid, list);
		dataGrid.redraw();
	}
	
	public InvoiceGrid(FiscalModelModuleOptions<FiscalModel> options, InvoiceParams filterParams) {
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
					getInvoices(new AsyncCallback<List<Invoice>>() {

						@Override
						public void onSuccess(List<Invoice> result) {
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

		getInvoices(new AsyncCallback<List<Invoice>>() {
			
			@Override
			public void onSuccess(List<Invoice> result) {
				load(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
					
			}
		});

		initWidget(binder.createAndBindUi(this));
	}

	LinkedList<Invoice> selFiles = new LinkedList<>();
	private void load(List<Invoice> list) {
		DefaultKeyboardSelectionHandler<Invoice> selHandler = new DefaultKeyboardSelectionHandler<Invoice>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<Invoice> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 Invoice object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(Invoice f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }

					selFiles = new LinkedList<>();
					for(Invoice f :dataProvider.getList()){
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
		ListHandler<Invoice> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<Invoice> selectionModel = new SingleSelectionModel<Invoice>(
	//			Invoice.PROVIDES_KEY);
		final MultiSelectionModel<Invoice> selectionModel = new MultiSelectionModel<>(PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<Invoice> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}

	//------------------------------ DataGrid Utils

	private ListDataProvider<Invoice> dataProvider = new ListDataProvider<Invoice>();

	public void addDataDisplay(HasData<Invoice> display, List<Invoice> list) {
		dataProvider = new ListDataProvider<>(list);
		dataProvider.addDataDisplay(display);
	}

	private ListHandler<Invoice> getSortHandler() {
		return new ListHandler<Invoice>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<Invoice> aux  = super.getList();
				List<Invoice> aux2 = new LinkedList<>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(final MultiSelectionModel<Invoice> selectionModel, ListHandler<Invoice> sortHandler) {

		/** Check Column **/
		Column<Invoice, Boolean> checkColumn = new Column<Invoice, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {

			@Override
			public Boolean getValue(Invoice object) {
				return selectionModel.isSelected(object);
			}
		};

		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel));
		dataGrid.setColumnWidth(checkColumn, 3, Unit.PCT);
		
		/** code Column **/
		Column<Invoice, String> codeColumn = new Column<Invoice, String>(new TextCell()) {

			@Override
			public void render(Context context, Invoice object, SafeHtmlBuilder sb) {
				String icon = "unarchive";
				if(InvoiceType.PURCHASE.equals(object.getType()) || InvoiceType.EXPENSES.equals(object.getType()))
					icon = "archive";
				else if(InvoiceType.UNDEDUCTIBLE.equals(object.getType()))
					icon = "receipt";
				
				String reference = AonStringUtils.isBlank(object.getSeries()) ? Integer.toString(object.getNumber()) : object.getSeries() + "/" + object.getNumber();
				reference = AonStringUtils.isBlank(object.getReferenceCode()) || "null".equalsIgnoreCase(object.getReferenceCode())
					? reference : object.getReferenceCode();
				sb.appendHtmlConstant( "<i class=\"material-icons\" style='font-size:16px;position:absolute;'>" + icon + "</i>" +  "<span style='padding-left:20px;'>"+ reference);		
			}
			
			@Override
			public String getValue(Invoice object) {
				String reference = AonStringUtils.isBlank(object.getSeries()) ? Integer.toString(object.getNumber()) : object.getSeries() + "/" + object.getNumber();
				return AonStringUtils.isBlank(object.getReferenceCode()) || "null".equalsIgnoreCase(object.getReferenceCode())
					? reference : object.getReferenceCode();
			}

		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true);
		sortHandler.setComparator(codeColumn,new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				return o1.getReferenceCode().compareTo(o2.getReferenceCode());
			}
		});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "Referencia");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);
		/** VAT DATE Column **/
		Column<Invoice, String> taxDateColumn = new Column<Invoice, String>(new TextCell()) {

			@Override
			public String getValue(Invoice object) {
				return AonDateUtils.formatDate(object.getIssueDate());
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true);
		sortHandler.setComparator(taxDateColumn,new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				return AonDateUtils.formatDate(o1.getIssueDate()).compareTo(AonDateUtils.formatDate(o2.getIssueDate()));
			}
		});
		dataGrid.getColumnSortList().push(taxDateColumn);
		dataGrid.addColumn(taxDateColumn, "Fecha Fact.");
		dataGrid.setColumnWidth(taxDateColumn, 7.5, Unit.PCT);

		/** VAT DATE Column **/
		Column<Invoice, String> creationDateColumn = new Column<Invoice, String>(new TextCell()) {

			@Override
			public String getValue(Invoice object) {
				if(isFechaIVA) {
					return object.getTaxDate() != null ? AonDateUtils.formatDate(object.getTaxDate()) : "-";
				} else return object.getCreationDate() != null ? AonDateUtils.formatDate(object.getCreationDate()) : "-";
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true);
		sortHandler.setComparator(taxDateColumn,new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				if(isFechaIVA) {
					return AonDateUtils.formatDate(o1.getTaxDate()).compareTo(AonDateUtils.formatDate(o2.getTaxDate()));
				} else return AonDateUtils.formatDate(o1.getCreationDate()).compareTo(AonDateUtils.formatDate(o2.getCreationDate()));
			}
		});
		dataGrid.getColumnSortList().push(creationDateColumn);
		dataGrid.addColumn(creationDateColumn, "Fecha Op.");
		dataGrid.setColumnWidth(creationDateColumn, 7.5, Unit.PCT);

		/** Contraparte Column **/
		Column<Invoice, String> contraparteColumn = new Column<Invoice, String>(new TextCell()) {

			@Override
			public String getValue(Invoice object) {
				return object.getRegistryName();
			}
		};
		contraparteColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		contraparteColumn.setSortable(true);
		sortHandler.setComparator(contraparteColumn,new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				return o1.getRegistryName().compareTo(o2.getRegistryName());
			}
		});
		dataGrid.getColumnSortList().push(contraparteColumn);
		dataGrid.addColumn(contraparteColumn, "Contraparte");
		dataGrid.setColumnWidth(contraparteColumn, 15, Unit.PCT);
		
		/** Status Column **/

		Column<Invoice, String> statusColumn = new Column<Invoice, String>(new TextCell()) {
			@Override
			public void render(Context context, Invoice object, SafeHtmlBuilder sb) {
				if(object.getInvoiceInfo() != null && !object.getInvoiceInfo().isEmpty()) {
					String color = "gray";
					String status = "Pendiente";
					if(object.getInvoiceInfo().getStatus().isAccepted()) {
						color = "green";
						status = "Aceptada";
					} else if(object.getInvoiceInfo().getStatus().isAcceptedWithErrors()) {
						color = "orange";
						status = "Aceptada con Errores";
					} else if(object.getInvoiceInfo().getStatus().isWrong()) {
						color = "red";
						status = "Incorrecta";
					} else if(object.getInvoiceInfo().getStatus().isAnnulled()) {
						color = "red";
						status = "Anulada";
					}
					sb.appendHtmlConstant( "<i class=\"material-icons\" style='font-size:16px;position:absolute;color:"+ color +";'>circle</i>" +  "<span style='padding-left:20px;'>"+ status);
				} else sb.appendHtmlConstant("-");
			}

			@Override
			public String getValue(Invoice object) {
				return object.getSiiStatus();
			}

		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true);
		sortHandler.setComparator(statusColumn,new Comparator<Invoice>() {

			@Override
			public int compare(Invoice o1, Invoice o2) {
				return o1.getSiiStatus().compareTo(o2.getSiiStatus());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);

		/** Info Column **/

		List<HasCell<Invoice, ?>> cells = new LinkedList<HasCell<Invoice, ?>>();

		cells.add(new ActionHasCell("info", new Delegate<Invoice>() {
	        @Override public void execute(Invoice object) {
	        	info(object.getId(), object.getReferenceCode());
	        }
	    }));

		cells.add(new ActionHasCell("download", new Delegate<Invoice>() {
	        @Override public void execute(Invoice object) {
	        	download(object);
	        }
	    }));

		CompositeCell<Invoice> cell = new CompositeCell<Invoice>(cells);

		Column<Invoice,Invoice> infoColumn = 	new Column<Invoice, Invoice>(cell){

			@Override
			public Invoice getValue(Invoice object) {
				return object;
			}
		};
		infoColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(infoColumn, " ");
		dataGrid.setColumnWidth(infoColumn, 6, Unit.PCT);
	}

	public abstract void info(Integer invoice, String reference);
	public abstract void download(Invoice object);
	public abstract void select(LinkedList<Invoice> selFiles);
	


	public final class CheckboxHeader extends Header {

	    private final MultiSelectionModel<Invoice> selectionModel;

	    public CheckboxHeader(MultiSelectionModel<Invoice> selectionModel) {
	        super(new CheckboxCell());
	        this.selectionModel = selectionModel;
	    }

	    @Override
	    public Boolean getValue() {
	        boolean allItemsSelected = selectionModel.getSelectedSet().size() == dataProvider
	                .getList().size();
	        return allItemsSelected;
	    }

	    @Override
	    public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
	        InputElement input = elem.getFirstChild().cast();
	        Boolean isChecked = input.isChecked();
//	        parent.getSend().setVisible(isChecked);
//	        parent.getBaja().setVisible(isChecked);
	        for (Invoice element : dataProvider.getList()) {
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

	private class ActionHasCell implements HasCell<Invoice, Invoice> {
	    private ActionCell<Invoice> cell;
	    String s;

	    public ActionHasCell(String text, Delegate<Invoice> delegate) {
	    	s = text;
	        cell = new ActionCell<Invoice>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,Invoice value, SafeHtmlBuilder sb) {
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
		public Invoice getValue(Invoice object) {
			return object;
		}


		@Override
		public Cell<Invoice> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<Invoice, Invoice> getFieldUpdater() {
			return null;
		}
	}
}
