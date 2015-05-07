package com.esferalia.aon.gwt.template.client;



import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.template.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.ExportInfo;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
		
		//private MenuItem viewItem;
		private MenuItem editItem;
		private MenuItem removeItem;
		private MenuItem downloadItem;
		private Integer heigth;
		private Integer width;

		
		public Integer getHeigth() {
			heigth = 74;
			return heigth;
		}

		public void setHeigth(Integer heigth) {
			this.heigth = heigth;
		}

		public Integer getWidth() {
			width = 117;
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}
		
		public DocumentContextMenu(TemplateInfo object){
			if(!object.getIsParent()){
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
	@UiField Button import_button;
	@UiField Button export_button;
	@UiField Button edit_button;
	@UiField Button delete_button;
	@UiField(provided = true) TextBox nameSearchBox;
	@UiField(provided = true) TextBox typeSearchBox;
	@UiField Button nameSearchButton;
	@UiField Button typeSearchButton;
	
	//@UiField(provided = true)  FlexTable flex_table;
	
	Integer column = 1;
	ListBox list_box = new ListBox();
	TemplateList template_list;
	TemplatesDialog popup;
	Integer domainId;
	
	
	public Templates() {
		item.initAux(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				item.getTemplates(new AsyncCallback<TemplateList>() {
					
					@Override
					public void onSuccess(TemplateList result) {
						template_list = result;
						domainId = result.getDomainId();
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
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		boolean silent = Boolean.parseBoolean(getParameter(GWT.getModuleName(), SILENT));
		
		if (silent){

			exportProduct(this);
			exportStock(this);
			exportTransferStock(this);
			exportFee(this);
			exportProductx(this);
			exportStockx(this);
			exportStockx2(this);
			exportTransferStockx(this);
			exportFeex(this);
			exportCataloguex(this);
			exportProposal(this);
			exportProposalx(this);	
		}
	}
	
	public void Load() {
		nameSearchBox = new TextBox();
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
		
		typeSearchBox = new TextBox();
		typeSearchBox.addBitlessDomHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				typeSearchButton.click();
			}
		}, ChangeEvent.getType());
		
		typeSearchBox.addKeyUpHandler(new KeyUpHandler() {
		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					typeSearchButton.click();
				}
			}
		});
		
		
		DefaultKeyboardSelectionHandler<TemplateInfo> selHandler = new DefaultKeyboardSelectionHandler<TemplateInfo>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<TemplateInfo> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    TemplateInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    if(object.getIsParent()){
				    	edit_button.setVisible(false);
				    	delete_button.setVisible(false);
				    }
				    else{
				    	edit_button.setVisible(true);
				    	delete_button.setVisible(true);
				    }
				}
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
   					NativeEvent nativeEvent = event.getNativeEvent();

			    	DocumentContextMenu contextMenu = new DocumentContextMenu(dataProvider.getList().get(dataGrid.getKeyboardSelectedRow()));

			    	Integer heigth = contextMenu.getHeigth();
			    	Integer width = contextMenu.getWidth();
			    	
			    	if(nativeEvent.getClientY()>590){
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-width,
   								nativeEvent.getClientY()-heigth);
   						else contextMenu.setPopupPosition(nativeEvent.getClientX(),
   								nativeEvent.getClientY()-heigth);
   					}
   					else{
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-width,
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
	
	private class ActionHasCell implements HasCell<TemplateInfo, TemplateInfo> {
	    private ActionCell<TemplateInfo> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<TemplateInfo> delegate) {
	    	s = text;
	        cell = new ActionCell<TemplateInfo>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			TemplateInfo value, SafeHtmlBuilder sb) {
	        		if(text.equals("download")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-google-drive-excel\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		if(text.equals("edit")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-edit\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		if(text.equals("delete")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<TemplateInfo> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<TemplateInfo, TemplateInfo> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public TemplateInfo getValue(TemplateInfo object) {
	        return object;
	    }
	}
	
	private void initTableColumns(
			final SelectionModel<TemplateInfo> selectionModel,
			ListHandler<TemplateInfo> sortHandler) {
		
		initContextMenu();
		
		List<HasCell<TemplateInfo, ?>> cells = new LinkedList<HasCell<TemplateInfo, ?>>();
	    
	    
		cells.add(new ActionHasCell("edit", new Delegate<TemplateInfo>() {

	        @Override
	        public void execute(TemplateInfo object) {
	           // EDIT CODE
	        	edit(object);
	        }
	    }));
	    cells.add(new ActionHasCell("delete", new Delegate<TemplateInfo>() {

	        @Override
	        public void execute(TemplateInfo object) {
	            // DELETE CODE
	        	delete(object);
	        }
	    }));
	    cells.add(new ActionHasCell("download", new Delegate<TemplateInfo>() {
	    	
	        @Override
	        public void execute(TemplateInfo object) {
	            // DOWNLOAD CODE
	        	download(object);
	        }
	    }));
		
		
		CompositeCell<TemplateInfo> cell = new CompositeCell<TemplateInfo>(cells);
		/** Name Column **/
		Column<TemplateInfo, String> nameColumn = new Column<TemplateInfo, String>(
				new TextCell()) {
			
			@Override
			public void render(Context context, TemplateInfo object,
					SafeHtmlBuilder sb) {
				if(object.getIsParent()){
					sb.appendHtmlConstant("<span style= 'padding-right: 5px;'>"+object.getName()+"</span><span title='Documento heredado' class='aon-editDataTable-button aon-icon-shield'>&nbsp;</span>");
				}
				else {
					sb.appendHtmlConstant("<span>"+object.getName()+"</span>");
				}
			}
			
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
		Column<TemplateInfo,TemplateInfo> downloadColumn = 	new Column<TemplateInfo, TemplateInfo>(cell){

			@Override
			public TemplateInfo getValue(TemplateInfo object) {
				return object;
			}
		};
		
		/*Column<TemplateInfo,String> downloadColumn = new Column<TemplateInfo, String>(new ButtonCell()) {
			
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
		};*/
		downloadColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		/*downloadColumn.setFieldUpdater(new FieldUpdater<TemplateInfo, String>() {
			@Override
			public void update(int index, TemplateInfo object, String value) {
				download(object);
			}
		});*/	
		dataGrid.addColumn(downloadColumn, "Acciones");//("+dataProvider.getList().size()+")");
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
				Integer aux = dataGrid.getKeyboardSelectedRow();
				
				TemplateInfo ti2 = dataProvider.getList().get(aux);
				
				
				TextBox tb = (TextBox) flex_table.getWidget(0, 1);
				ti2.setName(tb.getText());

				ListBox lb = (ListBox) flex_table.getWidget(1, 1);
				ti2.setType(lb.getItemText(lb.getSelectedIndex()));
				
				ti2.sethasWarehouse(false);
				Vector<String> v = new Vector<String>();
				Integer i = 2;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getItemText(lbn.getSelectedIndex()));
					if(lbn.getItemText(lbn.getSelectedIndex()).equals("Almac\u00e9n Destino"))
						ti2.sethasWarehouse(true);
					i++;
				}
				ti2.setColumns(v);
				ti =  ti2;
				item.editTemplate(ti2, new AsyncCallback<TemplateInfo>() {
					@Override
					public void onSuccess(TemplateInfo result) { 
						Integer index = dataGrid.getKeyboardSelectedRow();
						template_list.getList().set(index, result);
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
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
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	private void importFee(){
		Dialog d = new Dialog("Importar Cuotas","Importar",true,"Cancelar",true,"importFee");
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
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Cuota")){
						ti = t;
					}
				}
				
				item.executeExcel3(ti, new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
							hide();
							if(result !=-1){
								pbd = new ProgressBarDialog(result.doubleValue(), 0.86) {
									
								};

								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.show();
								item.insertFee(new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
										pbd.hide();
										Dialog d2 = new Dialog("Importar Cuotas","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.toString());
									}
								});
							}
					}
					@Override
					public void onFailure(Throwable caught) {}
					
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	private void exportFee(){
		//TODO
	}
	
	private void importProduct(){
		Dialog d = new Dialog("Importar Productos","Importar",true,"Cancelar",true,"importProduct");
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
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Producto")){
						ti = t;
					}
				}
				item.executeExcel2(ti, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
							hide();
							if(result !=-1){
								pbd = new ProgressBarDialog(result.doubleValue(), 0.86) {
									
								};

								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.show();
								
								item.insertProduct(new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {
										pbd.hide();
										Dialog d2 = new Dialog("Importar Productos","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.toString());
									}
								});
							}					
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}

	private void exportProducts(ExportInfo ei){
		Dialog d = new Dialog("Exportar Productos","Descargar",true,"Cancelar",true,"exportProduct");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		eiAux = ei;
		TemplatesDialog popup = new TemplatesDialog(d) {
			ExportInfo ei = eiAux;
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {

				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Producto")){
						ti = t;
					}
				}
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_product/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName()
		            	+ "&domain_id=" + domainId)
						+ "&description="+ei.getName()
						+ "&code="+ei.getCode()
						+ "&category="+ei.getCategory()
						+ "&tags="+ei.getTags()
						+ "&vat="+ei.getVat()
						+ "&retention="+ei.getRetention()
						+ "&purchaseAccount="+ei.getPurchaseAccount()
						+ "&salesAccount="+ei.getSalesAccount()
						+ "&serializable="+ei.getSerializable()
						+ "&inventoriable="+ei.getInventoriable()
						+ "&manufactured="+ei.getManufactured()
						+ "&composition="+ei.getComposition()
						+ "&statuses="+ei.getStatuses()
						+ "&types="+ei.getTypes()
						+ "&brand="+ei.getBrand();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
				// llamar  servlet de descarga para krear excel con todos losproductos
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	ProgressBarDialog pbd;
	Integer inventoryId;
	private void importStock(Integer inventory, String warehouse, Vector<Series> series){
		inventoryId =  inventory;
		Dialog d = new Dialog("Importar Stock","Importar",true,"Cancelar",true,"importStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		d.setWarehouseName(warehouse);
		d.setSeries2(series);
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
				String  warehouse = "";
				ListBox  lb2 = (ListBox) flex_table.getWidget(1, 1);
				warehouse = lb2.getItemText(lb2.getSelectedIndex());				
				String series = "";
				ListBox lb3 = (ListBox) flex_table.getWidget(2, 1);
				series = lb3.getItemText(lb3.getSelectedIndex());
				String comments = "";
				TextBox tb = (TextBox) flex_table.getWidget(3, 1);
				comments = tb.getText();
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				item.executeExcel(inventoryId, ti, warehouse,null, series, comments,false,-1,new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
						hide();
						if(result !=-1){
						pbd = new ProgressBarDialog(result.doubleValue(),0.4) {
							
						};
			
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						item.insertStock(new AsyncCallback<Error>() {
							@Override
							public void onSuccess(Error result) {
								pbd.hide();
								Dialog d2 = new Dialog("Importar Stock","Aceptar",true,"Cancelar",false,"importResponse");
								d2.setError(result);
								TemplatesDialog popup2 = new TemplatesDialog(d2){

									@Override
									protected void onAccept() {
										hide();
										refreshInventoryDetail();
									
									}

									@Override
									protected void onCancel() {
										hide();
									}
								};
								popup2.addStyleName("gwt-PopupPanel-template");
								popup2.setGlassEnabled(true);
								popup2.show();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.toString());
							}
						});
						}
						else{
							item.insertStock(new AsyncCallback<Error>() {
								@Override
								public void onSuccess(Error result) {
									Dialog d2 = new Dialog("Importar Stock","Aceptar",true,"Cancelar",false,"importResponse");
									d2.setError(result);
									TemplatesDialog popup2 = new TemplatesDialog(d2){

										@Override
										protected void onAccept() {
											hide();			
										}

										@Override
										protected void onCancel() {
											hide();
										}
									};
									popup2.addStyleName("gwt-PopupPanel-template");
									popup2.setGlassEnabled(true);
									popup2.show();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.toString());
								}
							});
						}
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
				
				/*item.insertStock(ti, warehouse, series, comments, new AsyncCallback<com.esferalia.aon.gwt.template.shared.Error>() {
				
					@Override
					public void onSuccess(com.esferalia.aon.gwt.template.shared.Error result) {
							hide();
							Dialog d2 = new Dialog("Importar Stock","Aceptar",true,"Cancelar",false,"importResponse");
							d2.setError(result);
							TemplatesDialog popup2 = new TemplatesDialog(d2){

								@Override
								protected void onAccept() {

									hide();			
								}

								@Override
								protected void onCancel() {

									hide();
								}
							
							};

							popup2.addStyleName("gwt-PopupPanel-template");
							popup2.setGlassEnabled(true);
							popup2.show();						

					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				*/
			}
		};

		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();

	}
	Integer num;
	private void importTransferStock( Vector<Warehouse> w, Vector<Series> series, Integer number){
		num = number;
		Dialog d = new Dialog("Traspaso entre almacenes","Importar",true,"Cancelar",true,"importTransferStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		d.setWarehouses(w);
		d.setSeries2(series);
		TemplatesDialog popup = new TemplatesDialog(d) {
			Integer number = num;
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				String  warehouse2 = "";
				ListBox  lb2 = (ListBox) flex_table.getWidget(1, 1);
				warehouse2 = lb2.getItemText(lb2.getSelectedIndex());
				
				String  warehouse = "";
				ListBox  lb4 = (ListBox) flex_table.getWidget(2, 1);
				warehouse = lb4.getItemText(lb4.getSelectedIndex());				
				
				String series = "";
				ListBox lb3 = (ListBox) flex_table.getWidget(3, 1);
				series = lb3.getItemText(lb3.getSelectedIndex());
				String comments = "";
				TextBox tb = (TextBox) flex_table.getWidget(4, 1);
				comments = tb.getText();
		
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				item.executeExcel(0,ti, warehouse, warehouse2, series, comments,true,number, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
						
						hide();
						
						if(result !=-1){
							pbd = new ProgressBarDialog(result.doubleValue(),0.101) {

							};
							pbd.addStyleName("gwt-PopupPanel-template");
							pbd.setGlassEnabled(true);
							pbd.show();
							item.insertTransferStock(new AsyncCallback<Error>() {
								@Override
								public void onSuccess(Error result) {
									pbd.hide();
									Dialog d2 = new Dialog("Traspaso entre almacenes","Aceptar",true,"Cancelar",false,"importResponse");
									d2.setError(result);
									TemplatesDialog popup2 = new TemplatesDialog(d2){
										
										@Override
										protected void onAccept() {
											hide();
											refreshTransferStock();
										}

										@Override
										protected void onCancel() {
											hide();
										}	
									};
									popup2.addStyleName("gwt-PopupPanel-template");
									popup2.setGlassEnabled(true);
									popup2.show();
								}
							
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.toString());
								}	
							});
						}
						else{
							item.insertTransferStock(new AsyncCallback<Error>() {
								@Override
								public void onSuccess(Error result) {
									Dialog d2 = new Dialog("Importar Stock","Aceptar",true,"Cancelar",false,"importResponse");
									d2.setError(result);
									TemplatesDialog popup2 = new TemplatesDialog(d2){

										@Override
										protected void onAccept() {
											hide();			
										}

										@Override
										protected void onCancel() {
											hide();
										}
									};
									popup2.addStyleName("gwt-PopupPanel-template");
									popup2.setGlassEnabled(true);
									popup2.show();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.toString());
								}
							});
						}
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
			}
		};

		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();

	}
	ExportInfo eiAux;
	private void exportStocks(Vector<Warehouse> w,ExportInfo ei){
		eiAux = ei;
		Dialog d = new Dialog("Exportar Stock","Descargar",true,"Cancelar",true,"exportStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		d.setWarehouses(w);
		TemplatesDialog popup = new TemplatesDialog(d) {
			ExportInfo ei = eiAux;
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {

				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
				String warehouse = lb2.getItemText(lb2.getSelectedIndex());
				
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_stock/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName())
		            	+ "&domain_id=" + domainId
		            	+ "&warehouse=" + warehouse
		            	+ "&category="+ei.getCategory()
		            	+ "&brand="+ei.getBrand()
		            	+ "&code="+ei.getCode()
		            	+ "&description="+ei.getDescription()
		            	+ "&stock="+ei.getStock()
						+ "&barcode="+ei.getBarcode()
						+ "&provider="+ei.getProvider()
						+ "&tags="+ei.getTags()
						+ "&statuses="+ei.getStatuses()
						+ "&types="+ei.getTypes()
						+ "&quantity="+ei.getQuantity();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
				// llamar  servlet de descarga para krear excel con todos losproductos
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportTransferStocks(Vector<Warehouse> w){
		Dialog d = new Dialog("Exportar Stock","Descargar",true,"Cancelar",true,"exportStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		d.setWarehouses(w);
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
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
				String warehouse = lb2.getItemText(lb2.getSelectedIndex());
				
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_stock/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName())
		            	+ "&domain_id=" + domainId
		            	+ "&warehouse=" + warehouse;
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
				// llamar  servlet de descarga para krear excel con todos losproductos
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportCatalogue(){
		Dialog d = new Dialog("Exportar Catalogo","Descargar",true,"Cancelar",true,"exportCatalogue");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(template_list);
		TemplatesDialog popup = new TemplatesDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb0 = (ListBox) flex_table.getWidget(0,1);
				String template = lb0.getSelectedItemText();
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb1 = (ListBox) flex_table.getWidget(1, 1);
				String workplace = lb1.getSelectedItemText();
				
				ListBox lb2;
				String department = "-";
				if(workplace != "-"){
					lb2 = (ListBox) flex_table.getWidget(2, 1);
					department = lb2.getSelectedItemText();
				}

				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_catalogue/"
		            	+ "?&domain_id=" + domainId
		            	+ "&workplace=" + workplace
		            	+ "&department="+ department
		            	+ "&template_id="+ti.getId();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
				// llamar  servlet de descarga para krear excel con todos losproductos
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	Integer proposalId;
	Integer workplaceId;
	private void importProposal(Integer proposal, Integer workplace) {
		proposalId = proposal;
		workplaceId = workplace;
		Dialog d = new Dialog("Importar Solicitudes de Compra","Importar",true,"Cancelar",true,"importProposal");
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
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				item.executeExcelProposal(ti, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
							hide();
							if(result !=-1){
								pbd = new ProgressBarDialog(result.doubleValue(), 0.86) {
									
								};
									
								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.show();
								
								item.insertProposal(proposalId,workplaceId,new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {
										pbd.hide();
										Dialog d2 = new Dialog("Importar Solicitudes de Compra","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(d2){

											@Override
											protected void onAccept() {
												hide();	
												refreshProposalDetail();
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.toString());
									}
								});
							}		
							else{
								item.insertProposal(proposalId,workplaceId,new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {
										Dialog d2 = new Dialog("Importar Solicitudes de Compra","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.toString());
									}
								});
							}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportProposal(Integer proposal){
		proposalId = proposal;
		Dialog d = new Dialog("Exportar Compra","Descargar",true,"Cancelar",true,"exportProposal");
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
				
				for(TemplateInfo t : tlist.getList()) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_proposal/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&domain_id=" + domainId
		            	+ "&proposal=" + proposalId;
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	//------------------------------ ui handlers
	
	@UiHandler("nameSearchButton")
	void namesbutton(ClickEvent event) {
		typeSearchBox.setText("");
		String searchStr = nameSearchBox.getText();
		Vector<TemplateInfo> vaux = new Vector<TemplateInfo>();
		vaux.addAll(template_list.getList());
		//dataProvider.getList().stream().forEach(f-> vaux.add(f));
 		
		item.searchNameTemplate(searchStr, vaux,
				new AsyncCallback<Vector<TemplateInfo>>() {

					@Override
					public void onSuccess(Vector<TemplateInfo> result) {
						dataProvider = new ListDataProvider<TemplateInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						dataGrid.redraw();
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}
	
	@UiHandler("typeSearchButton")
	void typesbutton(ClickEvent event) {
		nameSearchBox.setText("");
		String searchStr = typeSearchBox.getText();
		Vector<TemplateInfo> vaux = new Vector<TemplateInfo>();
		vaux.addAll(template_list.getList());
		//dataProvider.getList().stream().forEach(f-> vaux.add(f));
 		
		item.searchTypeTemplate(searchStr, vaux,
				new AsyncCallback<Vector<TemplateInfo>>() {

					@Override
					public void onSuccess(Vector<TemplateInfo> result) {
						dataProvider = new ListDataProvider<TemplateInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						dataGrid.redraw();
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}
	
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
				
				ti.sethasWarehouse(false);
				Vector<String> v = new Vector<String>();
				Integer i = 2;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getItemText(lbn.getSelectedIndex()));
					if(lbn.getItemText(lbn.getSelectedIndex()).equals("Almac\u00e9n Destino"))
						ti.sethasWarehouse(true);
					
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
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	@UiHandler("import_button")
	void importButton(ClickEvent event){
		Dialog d = new Dialog("Importar","Importar",true,"Cancelar",true,"import");
		d.setTemplateList(template_list);
		popup = new TemplatesDialog(d) {

			@Override
			protected void onAccept() {
				hide();
			}

			@Override
			protected void onCancel() {
				hide();
			}
			
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	@UiHandler("export_button")
	void exportButton(ClickEvent event){
		Dialog d = new Dialog("Exportar","Exportar",true,"Cancelar",true,"export");
		d.setTemplateList(template_list);
		popup = new TemplatesDialog(d) {

			@Override
			protected void onAccept() {
				hide();
			}

			@Override
			protected void onCancel() {
				hide();
			}
			
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	@UiHandler("edit_button")
	void editButton(ClickEvent event){
		TemplateInfo object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		edit(object);
	}
	
	@UiHandler("delete_button")
	void deleteButton(ClickEvent event){
		TemplateInfo object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		delete(object);
	}
	
	// ------------------------------------------------------------------------
	
	public void product(){
			importProduct();
	}

	public static native void exportProduct(Templates thiz) /*-{
    	$wnd.product = function() {
    		thiz.@com.esferalia.aon.gwt.template.client.Templates::product(*)();
    	}
	}-*/;
	
	public void productx(String code,String name,String category,String tags,String brand,String vat,String retention,String purchaseAccount,String  salesAccount,String  serializable, String inventoriable, String manufactured, String composition,  String statuses, String types){
		//Window.alert(code +" - "+name+" - "+category+" - "+tags+" - "+brand+" - "+vat+" - "+retention+" - "+purchaseAccount+" - "+salesAccount+" - "+serializable+" - "+inventoriable+" - "+manufactured+" - "+composition+" - "+statuses+" - "+types); 
		ExportInfo ei = new ExportInfo();
		ei.setCode(code);ei.setName(name);ei.setCategory(category);ei.setTags(tags);ei.setBrand(brand);ei.setVat(vat);ei.setRetention(retention);ei.setPurchaseAccount(purchaseAccount);ei.setSalesAccount(salesAccount);ei.setSerializable(serializable);ei.setInventoriable(inventoriable);ei.setManufactured(manufactured);ei.setComposition(composition);ei.setStatuses(statuses);ei.setTypes(types);
		exportProducts(ei);
	}

	public static native void exportProductx(Templates thiz) /*-{
		$wnd.productx = function(code,name,category,tags,brand,vat,retention,purchaseAccount, salesAccount, serializable, inventoriable,manufactured, composition, statuses, types) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::productx(*)(code,name,category,tags,brand,vat,retention,purchaseAccount, salesAccount, serializable, inventoriable,manufactured, composition, statuses, types);
		}
	}-*/;
	
	Vector<Warehouse> ws;
	String w;
	Integer inventory;
	public void stock(String warehouse, String inventoryId){
		w = warehouse;
		inventory = Integer.parseInt(inventoryId);
		item.getSeries(warehouse, new AsyncCallback<Vector<Series>>() {
			String warehouse = w;
			Integer inventoryId = inventory;
			@Override
			public void onSuccess(Vector<Series> result) {
				importStock(inventoryId, warehouse, result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public static native void refreshInventoryDetail() /*-{
		$wnd.refreshInventoryDetail();
	}-*/;


	public static native void refreshProposalDetail() /*-{	
		$wnd.refreshProposalDetail();
	}-*/;
	
	public static native void refreshTransferStock() /*-{
		$wnd.refreshWarehouseTransfer();
	}-*/;
	
	public static native void exportStock(Templates thiz) /*-{
		$wnd.stock = function(warehouse, inventoryId) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stock(*)(warehouse,inventoryId);
		}
	}-*/;
	
	String wAux;
	public void stockx(String warehouse, String quantity, String code, String name, String barcode, String provider,String brand, String category,String statuses, String types, String tags){
		//Window.alert(warehouse +" - "+ quantity+" - "+code+" - "+name+" - "+barcode+" - "+provider+" - "+brand+" - "+category+" - "+statuses+" - "+types+" - "+tags); 
		ExportInfo ei = new ExportInfo();
		ei.setWarehouse(warehouse);ei.setQuantity(quantity);ei.setCode(code); ei.setName(name);ei.setBarcode(barcode);ei.setProvider(provider);ei.setBrand(brand);ei.setCategory(category);ei.setStatuses(statuses);ei.setTypes(types);ei.setTags(tags);
		ei.setDescription(name);
		eiAux=ei;
		wAux = warehouse;
		item.getWarehouses(new AsyncCallback<Vector<Warehouse>>() {
			ExportInfo ei = eiAux; String warehouse =  wAux;
			@Override
			public void onSuccess(Vector<Warehouse> result) {
				Vector<Warehouse> v = new Vector<Warehouse>();
				if(!warehouse.equals("") && !warehouse.equals("null") && warehouse != null){
					for (Warehouse w : result) {
						if(w.getId() == Integer.parseInt(warehouse))
							v.add(w);
					}				
				}
				else v = result;
				exportStocks(v,ei);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public static native void exportStockx(Templates thiz) /*-{

		$wnd.stockx = function(warehouse, quantity, code,name,barcode, provider, brand, category,statuses, types, tags) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stockx(*)(warehouse, quantity, code,name,barcode, provider, brand, category,statuses, types, tags);
		}
	}-*/;
	
	
	public void stockx2(String warehouse,String category, String brand,String code,String description,String stock){
		ExportInfo ei = new ExportInfo();
		ei.setCategory(category);
		ei.setBrand(brand);
		ei.setCode(code);
		ei.setDescription(description);
		ei.setStock(stock);
		
		Vector<Warehouse> v = new Vector<Warehouse>();
		Warehouse w = new Warehouse();
		w.setName(warehouse);
		v.add(w);
		exportStocks(v,ei);		
	}
	
	public static native void exportStockx2(Templates thiz) /*-{
		$wnd.stockx2 = function(warehouse, category, brand, code, description, stock) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stockx2(*)(warehouse, category, brand, code, description, stock);
		}
	}-*/;
	
	public void fee(){
		importFee();
	}

	public static native void exportFee(Templates thiz) /*-{
		$wnd.fee = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::fee(*)();
		}
	}-*/;

	public void feex(){
		exportFee();
	}
	
	public static native void exportFeex(Templates thiz) /*-{
		$wnd.feex = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::feex(*)();
		}
	}-*/;
	public void transferStock(String sourceWarehouse,String targetWarehouse, String serie, String number){
		Warehouse w1 = new Warehouse();
		w1.setName(sourceWarehouse);
		Warehouse w2 = new Warehouse();
		w2.setName(targetWarehouse);
		Vector<Warehouse> warehouses = new Vector<Warehouse>();
		warehouses.add(0,w1);warehouses.add(1,w2);
		Vector<Series> series = new Vector<Series>();
		Series s = new Series();
		s.setName(serie);
		series.add(s);
		importTransferStock(warehouses,series, Integer.parseInt(number));		
	}

	public static native void exportTransferStock(Templates thiz) /*-{
		$wnd.transferStock = function(source,target,serie, number) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::transferStock(*)(source,target,serie,number);
		}
	}-*/;
	
	public void transferStockx(){
		item.getWarehouses(new AsyncCallback<Vector<Warehouse>>() {
			
			@Override
			public void onSuccess(Vector<Warehouse> result) {
				exportTransferStocks(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public static native void exportTransferStockx(Templates thiz) /*-{
		$wnd.transferStockx = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::transferStockx(*)();
		}
	}-*/;
	
	public void cataloguex(){
		exportCatalogue();
	}
	
	public static native void exportCataloguex(Templates thiz) /*-{
		$wnd.cataloguex = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::cataloguex(*)();
		}
	}-*/;
	
	public void proposal(String proposal, String workplace){
		importProposal(Integer.parseInt(proposal), Integer.parseInt(workplace));
	}

	public static native void exportProposal(Templates thiz) /*-{
		$wnd.proposal = function(proposal, workplace) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::proposal(*)(proposal, workplace);
		}
	}-*/;
	
	public void proposalx(String proposal){
		exportProposal(Integer.parseInt(proposal));
	}

	public static native void exportProposalx(Templates thiz) /*-{
		$wnd.proposalx = function(proposal) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::proposalx(*)(proposal);
		}
	}-*/;
}

