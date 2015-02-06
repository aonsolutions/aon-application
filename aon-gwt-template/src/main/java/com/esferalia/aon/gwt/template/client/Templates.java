package com.esferalia.aon.gwt.template.client;



import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.template.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class Templates extends Composite implements EntryPoint {

	private static final String SILENT = "silent";
	
	class DocumentContextMenu extends ContextMenu {

		ScheduledCommand viewCommand = new ScheduledCommand() {
			public void execute() {
				//TODO VISUALIZAR EXCEL!!
			};
		};		

		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				edit(object);
			};
		};
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				delete(object);
			};
		};
		
		ScheduledCommand downloadCommand = new ScheduledCommand() {
			public void execute() {	
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				download(object);
			};
		};
		
		private MenuItem viewItem;
		private MenuItem editItem;
		private MenuItem removeItem;
		private MenuItem downloadItem;
		
		public DocumentContextMenu(){

			/*viewItem = addItem("Visualizar",viewCommand,
					"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
			viewItem.setEnabled(true);
			addSeparator();*/
			editItem = addItem("Editar", editCommand,
					"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
			editItem.setEnabled(true);
			removeItem = addItem("Borrar", removeCommand,
					AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
			removeItem.setEnabled(true);
			addSeparator();
			downloadItem = addItem("Descargar",downloadCommand,
					"aon-icon-google-drive-excel",AON.AON_ICON_CMD_BUTTON);
			downloadItem.setEnabled(true);	
		}

		@Override
		public void show() {
			super.show();
		}
	}
	
	final ITemplateAsync item = GWT.create(ITemplate.class);

	interface Binder extends UiBinder<Widget, Templates> {

	}
	
	private static final Binder binder = GWT.create(Binder.class);
	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/template/client/datagrid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<TemplateInfo> dataGrid; 
	@UiField Button new_button;
	//@UiField(provided = true)  FlexTable flex_table;
	
	Integer column = 1;
	ListBox list_box = new ListBox();
	TemplateList template_list;
	TemplatesDialog popup;
	
	public Templates() {
		item.initAux(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				item.getTemplates(new AsyncCallback<TemplateList>() {
					
					@Override
					public void onSuccess(TemplateList result) {
						template_list = result;
						Load();						
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				
				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	@Override
	public void onModuleLoad() {
		boolean silent = Boolean.parseBoolean(getParameter(GWT.getModuleName(), SILENT));
		
		if (silent){
			GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
			GWT.<AonResources> create(AonResources.class).css().ensureInjected();
			GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

			exportPreview(this);
		}
	}
	
	public void Load() {
		
		DefaultKeyboardSelectionHandler<TemplateInfo> selHandler = new DefaultKeyboardSelectionHandler<TemplateInfo>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<TemplateInfo> event) {
				
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
   					NativeEvent nativeEvent = event.getNativeEvent();

			    	DocumentContextMenu contextMenu = new DocumentContextMenu();
   					if(nativeEvent.getClientY()>590){
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-120,
   								nativeEvent.getClientY()-140);
   						else contextMenu.setPopupPosition(nativeEvent.getClientX(),
   								nativeEvent.getClientY()-140);
   					}
   					else{
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-120,
   								nativeEvent.getClientY());
   						else contextMenu.setPopupPosition(nativeEvent.getClientX(),
   								nativeEvent.getClientY());
   					}
   			        contextMenu.show();
				}				
			}
		};
		
		dataGrid = new DataGrid<TemplateInfo>(Integer.MAX_VALUE, resources,
				TemplateInfo.PROVIDES_KEY);
 		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());

		
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		
		addDataDisplay(dataGrid);
		
		ListHandler<TemplateInfo> sortHandler = getSortHandler();
				//docs.getFil());
		dataGrid.addColumnSortHandler(sortHandler);
		
		//final SelectionModel<TemplateInfo> selectionModel = new MultiSelectionModel<TemplateInfo>(TemplateInfo.PROVIDES_KEY);
 		final SingleSelectionModel<TemplateInfo> selectionModel = new SingleSelectionModel<TemplateInfo>(
				TemplateInfo.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<TemplateInfo> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		

		
		initTableColumns(selectionModel, sortHandler);
 	
		
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();		
		GWT.<AonGwtTemplateResources> create(AonGwtTemplateResources.class).css().ensureInjected();		
		
		Widget ui = binder.createAndBindUi(this);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}
	
	
	

	
//------------------------------ DataGrid Utils
	
	private ListDataProvider<TemplateInfo> dataProvider = new ListDataProvider<TemplateInfo>();

	public void addDataDisplay(HasData<TemplateInfo> display) {
		dataProvider = new ListDataProvider<TemplateInfo>(template_list.getList());
		dataProvider.addDataDisplay(display);
	}
	
	private ListHandler<TemplateInfo> getSortHandler() {
		return new ListHandler<TemplateInfo>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<TemplateInfo> aux  = super.getList();
				List<TemplateInfo> aux2 = new Vector<TemplateInfo>();
 				for(Integer i = 0 ; i< aux.size()-1;i++){
 					aux2.set(i, aux.get(aux.size()-1-i ));
 				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initContextMenu() {
		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();				
			}
		};

		dataGrid.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
		
	}
	
	private void initTableColumns(
			final SelectionModel<TemplateInfo> selectionModel,
			ListHandler<TemplateInfo> sortHandler) {
		
		initContextMenu();
		
		/** Name Column **/
		Column<TemplateInfo, String> nameColumn = new Column<TemplateInfo, String>(
				new TextCell()) {

			@Override
			public String getValue(TemplateInfo object) {
				return object.getName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<TemplateInfo>() {
			
			@Override
			public int compare(TemplateInfo o1, TemplateInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Nombre");

		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);
		
		/** Type Column **/
		Column<TemplateInfo,String> typeColumn = new Column<TemplateInfo, String>(new TextCell()) {
			
			@Override
			public String getValue(TemplateInfo object) {
				return object.getType();
			}
		};
		
		typeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		typeColumn.setSortable(true); 
		sortHandler.setComparator(typeColumn,new Comparator<TemplateInfo>() {
			
			@Override
			public int compare(TemplateInfo o1, TemplateInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, "Tipo");

		dataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);
		
		/** Download Column **/
		Column<TemplateInfo,String> downloadColumn = new Column<TemplateInfo, String>(new ButtonCell()) {
			
			@Override
			public void render(Context context, TemplateInfo object,
					SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-google-drive-excel\" tabindex=\"-1\">");
				sb.appendHtmlConstant("</button>");				
			}
			
			@Override
			public String getValue(TemplateInfo object) {
				return "";
			}
		};
		downloadColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		downloadColumn.setFieldUpdater(new FieldUpdater<TemplateInfo, String>() {
			@Override
			public void update(int index, TemplateInfo object, String value) {
				download(object);
			}
		});		
		dataGrid.addColumn(downloadColumn, "Descargar");//("+dataProvider.getList().size()+")");
		dataGrid.setColumnWidth(downloadColumn, 10, Unit.PCT);
	}
	
//------------------------------ Utils	
	
	private void download(TemplateInfo object) {
		String driveId="";
		if(object.getDriveId()!=null)driveId= object.getDriveId();
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download/"
            	+ "?id=" + Integer.toString(object.getId())
            	+ "&drive_id=" +URL.encode(driveId)
            	+ "&name=" +URL.encode(object.getName());
		Window.open( fileDownloadURL, "_blank",null);
	
	}
	
	private void edit(TemplateInfo object){
		//TODO Editar plantilla
		
		Dialog d = new Dialog("Editar Plantilla","Grabar",true,"Cancelar",true,"edit");
		d.setTemplateInfo(object);
		TemplatesDialog popup = new TemplatesDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				TemplateInfo ti2 = new TemplateInfo();
				ti2.setId(ti.getId());
				TextBox tb = (TextBox) flex_table.getWidget(0, 1);
				ti2.setName(tb.getText());

				ListBox lb = (ListBox) flex_table.getWidget(1, 1);
				ti2.setType(lb.getItemText(lb.getSelectedIndex()));

				Vector<String> v = new Vector<String>();
				Integer i = 2;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getItemText(lbn.getSelectedIndex()));
					i++;
				}
				ti2.setColumns(v);
				ti =  ti2;
				item.editTemplate(ti2, new AsyncCallback<Void>() {
					TemplateInfo templateInfo = ti;
					@Override
					public void onSuccess(Void result) {
						Integer index = 0;
						for(TemplateInfo t  :template_list.getList()){
							if(t.getId().equals(templateInfo.getId())){
								template_list.getList().set(index, templateInfo);
							}
							index++;
						}
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void delete(TemplateInfo object){
		Dialog d = new Dialog("Eliminar Plantilla","Borrar",true,"Cancelar",true,"delete");
		d.setTemplateInfo(object);
		TemplatesDialog popup = new TemplatesDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				item.deleteTemplate(ti, new AsyncCallback<Void>() {
					TemplateInfo templateInfo = ti;
					@Override
					public void onSuccess(Void result) {
						template_list.getList().remove(templateInfo);
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void importProduct(){
		Dialog d = new Dialog("Importar Productos","Importar",true,"Cancelar",true,"import");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		TemplatesDialog popup = new TemplatesDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
			
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				
				//TODO AÑADIR TODOS LOS ATRIBUTOS
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template)){
						ti = t;
					}
				}
				item.insertProducts(ti, new AsyncCallback<com.esferalia.aon.gwt.template.shared.Error>() {
					
					@Override
					public void onSuccess(com.esferalia.aon.gwt.template.shared.Error result) {
						if(result.getError()){
							hide();
							Window.alert(result.getTextError());
							
						}
						else{
							hide();
							Window.alert(result.getTextError());
							//label!!
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.setGlassEnabled(true);
		popup.show();
	}

	
//------------------------------ ui handlers
	
	@UiHandler("new_button")
	void newButton(ClickEvent event){
		Dialog d = new Dialog("Nueva Plantilla","Guardar",true,"Cancelar",true,"new");
		popup = new TemplatesDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				TemplateInfo ti = new TemplateInfo();

				TextBox tb = (TextBox) flex_table.getWidget(0, 1);
				ti.setName(tb.getText());

				ListBox lb = (ListBox) flex_table.getWidget(1, 1);
				ti.setType(lb.getItemText(lb.getSelectedIndex()));

				Vector<String> v = new Vector<String>();
				Integer i = 2;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getItemText(lbn.getSelectedIndex()));
					i++;
				}
				ti.setColumns(v);
				item.newTemplate(ti, new AsyncCallback<TemplateInfo>() {
					@Override
					public void onSuccess(TemplateInfo result) {
						template_list.getList().add(result);
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			
				
			}
		};
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	
	// ------------------------------------------------------------------------
	
	public void preview(){

		importProduct();
	}


	public static native void exportPreview(Templates thiz) /*-{
    	$wnd.preview = function() {
    		thiz.@com.esferalia.aon.gwt.template.client.Templates::preview(*)();
    	}
	}-*/;
}

