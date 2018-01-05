package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.seres.JsAttachFile;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

import net.aonsolutions.aon.gwt.seres.shared.IngenetAttachStatus;

public class ContentGridIngenet extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, ContentGridIngenet> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsAttachFile> dataGrid; 
	
	private SeresPrincipal parent;
	private Integer cont = 0;
	
	private API getAPI() {
		return parent.getAPI();
	}
	
	public ContentGridIngenet(SeresPrincipal parent) {
		this(parent, new LinkedList<>());	
	}

	public ContentGridIngenet(SeresPrincipal parent, List<JsAttachFile> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsAttachFile>(Integer.MAX_VALUE, resources,
				JsAttachFile.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				// TODO
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = 2;
					if(parent.getFilterMap().containsKey("page")){
						page = Integer.parseInt(parent.getFilterMap().get("page").get(0)) + 1;
					}
					LinkedList<String> list = new LinkedList<>();
					list.add(page +"");
					parent.getFilterMap().put("page", list);
					parent.reloadContentGrid();
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
	
	LinkedList<JsAttachFile> selFiles = new LinkedList<>();
	
	private void load(List<JsAttachFile> list) {
		DefaultKeyboardSelectionHandler<JsAttachFile> selHandler = new DefaultKeyboardSelectionHandler<JsAttachFile>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsAttachFile> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsAttachFile object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(JsAttachFile f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }
					 
					selFiles = new LinkedList<>();
					for(JsAttachFile f :dataProvider.getList()){
						if(dataGrid.getSelectionModel().isSelected(f)){
							selFiles.add(f);
						}
					}
					
					boolean enable = selFiles.size() > 0;
					if(parent.command.equals(parent.parent.INGENET_DELIVERY)){
						parent.getProcessAll().setVisible(enable);
					}
					
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsAttachFile> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		final MultiSelectionModel<JsAttachFile> selectionModel = new MultiSelectionModel<JsAttachFile>(JsAttachFile.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsAttachFile> createCheckboxManager());
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsAttachFile> dataProvider = new ListDataProvider<JsAttachFile>();
	
	public void addAll(List<JsAttachFile> list){
		dataProvider.getList().addAll(list);
	}

	public void addDataDisplay(HasData<JsAttachFile> display, List<JsAttachFile> list) {
		dataProvider = new ListDataProvider<JsAttachFile>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsAttachFile> getSortHandler() {
		return new ListHandler<JsAttachFile>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsAttachFile> aux  = super.getList();
				List<JsAttachFile> aux2 = new LinkedList<JsAttachFile>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final MultiSelectionModel<JsAttachFile> selectionModel, ListHandler<JsAttachFile> sortHandler) {
		
		/** Check Column **/
		Column<JsAttachFile, Boolean> checkColumn = new Column<JsAttachFile, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {
			
			@Override
			public Boolean getValue(JsAttachFile object) {
				return selectionModel.isSelected(object);
			}
		};
		
		/** Date Column **/
		Column<JsAttachFile, String> dateColumn = new Column<JsAttachFile, String>(new TextCell()) {

			@Override
			public String getValue(JsAttachFile object) {
				return object.getCreationDate();
			}
		};
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsAttachFile>() {
			
			@Override
			public int compare(JsAttachFile o1, JsAttachFile o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		/** Description Column **/
		Column<JsAttachFile, String> descriptionColumn = new Column<JsAttachFile, String>(new TextCell()) {

			@Override
			public String getValue(JsAttachFile object) {
				return object.getDescription();
			}
		};
		descriptionColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		descriptionColumn.setSortable(true); 
		sortHandler.setComparator(descriptionColumn,new Comparator<JsAttachFile>() {
			
			@Override
			public int compare(JsAttachFile o1, JsAttachFile o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
	
		/** Status Column **/
		Column<JsAttachFile, String> statusColumn = new Column<JsAttachFile, String>(new TextCell()) {
			@Override	
			public void render(Context context, JsAttachFile object, SafeHtmlBuilder sb) {
				String icon = AON.AON_CSS.aonIconPointLightGreen();
				if(object.getType()==IngenetAttachStatus.REQUEST.ordinal()) icon = AON.AON_CSS.aonIconQuestion();
				else if(object.getType()==IngenetAttachStatus.OK.ordinal()) icon = AON.AON_CSS.aonIconPointGreen();
				else if(object.getType()==IngenetAttachStatus.FAIL.ordinal()) icon= AON.AON_CSS.aonIconPointRed();
				
				String description = "-";
				for(IngenetAttachStatus status: IngenetAttachStatus.values())
					if(object.getType()==status.ordinal()) description = status.getDescription();	
				
				sb.appendHtmlConstant("<g:Label class=\""+ icon + "\" style=\"padding-left: 16px;\" >"+ "&nbsp;&nbsp;" + description);
			}
			
			@Override
			public String getValue(JsAttachFile object) {
				for(IngenetAttachStatus status: IngenetAttachStatus.values())
					if(object.getType()==status.ordinal()) return status.getDescription();
				return "-";
			}
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<JsAttachFile>() {
			
			@Override
			public int compare(JsAttachFile o1, JsAttachFile o2) {
				return Integer.compare(o1.getType(), o2.getType());
			}
		});
		
		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
		dataGrid.addColumn(dateColumn, AON.MSG.date());
		dataGrid.addColumn(descriptionColumn, AON.MSG.description());
		dataGrid.addColumn(statusColumn, AON.MSG.status());
		
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.getColumnSortList().push(descriptionColumn);
		dataGrid.getColumnSortList().push(statusColumn);
		
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		dataGrid.setColumnWidth(dateColumn, 25, Unit.PCT);
		dataGrid.setColumnWidth(descriptionColumn, 60, Unit.PCT);
		dataGrid.setColumnWidth(statusColumn, 15, Unit.PCT);		
	}
	
	public final class CheckboxHeader extends Header<Boolean> {

	    private final MultiSelectionModel<JsAttachFile> selectionModel;
	    private final ListDataProvider<JsAttachFile> provider;

	    public CheckboxHeader(MultiSelectionModel<JsAttachFile> selectionModel,
	    		ListDataProvider<JsAttachFile> provider) {
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
	        
	        if(parent.command.equals(parent.parent.INGENET_DELIVERY)){
				parent.getProcessAll().setVisible(isChecked);
			}
	        
	        for (JsAttachFile element : provider.getList()) {
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
	
	
	public void send(String action) {		
	}
	
	public void retrieve(String action) {
	}
	
	public void process(String action) {
		if(action.equals(parent.parent.INGENET_DELIVERY)){
			processIngenetDeliveries();
		}
	}

	
	private void processIngenetDeliveries() {
		HashMap<String, LinkedList<String>> map =  new HashMap<>();
		LinkedList<String> list = selFiles.stream().map(s -> s.getId() + "").collect(Collectors.toCollection(LinkedList::new));
		map.put("id_list", list);
		selFiles.clear();
//		parent.gridContent();
		parent.parent.cleanToolbarButtons();
		
		FlowPanel panel = new FlowPanel();
		panel.add(new Label("Procesando datos"));
		AonToast toast = new AonToast();
		toast.show(AON.MSG.information(), panel);
		
		getAPI().getSeres().processIngenetAttach(map, new AsyncCallback<JSON<JsObject>>() {
			@Override
			public void onSuccess(JSON<JsObject> result) {
				VerticalPanel vp = new VerticalPanel();
				result.getData().stream().forEach(r -> {
					Label label = new Label(""+r.getName());
					String str = r.getId() + "";
					String color = "black";
					if(str.equals("0"))
						color = "red";
					else if(str.equals("1"))
						color = "orange";
					label.getElement().getStyle().setColor(color);
					vp.add(label);
				});
				parent.errorPanel.setWidget(vp);
				parent.tabLayout.selectTab(0);
				parent.openFootPanel();
				
//				selFiles.clear();
				parent.gridContent();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
	}
	
	private ContentGridIngenet getMe() {
		return this;
	}
	
}
