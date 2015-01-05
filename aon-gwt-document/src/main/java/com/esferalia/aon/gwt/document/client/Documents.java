package com.esferalia.aon.gwt.document.client;

import gwtupload.client.SingleUploader;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.CategoryList;
import com.esferalia.aon.gwt.document.shared.Dialog;
import com.esferalia.aon.gwt.document.shared.DisclosureImages;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TagList;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;


public class Documents extends Composite implements EntryPoint {

	class DocumentContextMenu extends ContextMenu {

		ScheduledCommand viewCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				getAsHTMl(object,dataGrid.getKeyboardSelectedRow());
			};
		};		

		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				editFile(null);
			};
		};
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				removeFile();
			};
		};
		
		ScheduledCommand downloadCommand = new ScheduledCommand() {
			public void execute() {	
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				download(object);
			};
		};
		
		ScheduledCommand shareCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				share(object);
			};
		};
		
		ScheduledCommand infoCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				info(object);
			};
		};

		private MenuItem viewItem;
		private MenuItem editItem;
		private MenuItem removeItem;
		private MenuItem shareItem;
		private MenuItem downloadItem;
		private MenuItem infoItem;

		public MenuItem getDownloadItem() {
			return downloadItem;
		}
		public void setDownloadItem(MenuItem downloadItem) {
			this.downloadItem = downloadItem;
		}
		
		public MenuItem getEditItem() {
			return editItem;
		}
		public void setEditItem(MenuItem editItem) {
			this.editItem = editItem;
		}
		
		public DocumentContextMenu(Boolean permiso){
			if(permiso){
				viewItem = addItem("Visualizar",viewCommand,
						"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
				viewItem.setEnabled(true);
				addSeparator();
				downloadItem = addItem("Descargar",downloadCommand,
						"aon-icon-mail-save",AON.AON_ICON_CMD_BUTTON);
				downloadItem.setEnabled(true);
				infoItem = addItem("Detalles",infoCommand,
						"aon-icon-info",AON.AON_ICON_CMD_BUTTON);
				infoItem.setEnabled(true);
			}
			else{
				viewItem = addItem("Visualizar",viewCommand,
						"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
				viewItem.setEnabled(true);
				addSeparator();
				editItem = addItem("Editar", editCommand,
						"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);
				addSeparator();
				shareItem = addItem("Compartir",shareCommand,
						"aon-icon-google-drive",AON.AON_ICON_CMD_BUTTON);
				shareItem.setEnabled(true);
				downloadItem = addItem("Descargar",downloadCommand,
						"aon-icon-mail-save",AON.AON_ICON_CMD_BUTTON);
				downloadItem.setEnabled(true);
				infoItem = addItem("Detalles",infoCommand,
						"aon-icon-info",AON.AON_ICON_CMD_BUTTON);
				infoItem.setEnabled(true);
			}
		}
		public DocumentContextMenu() {
			
		}

		@Override
		public void show() {
			//sync();
			super.show();
		}

		//private void sync() {
			//Agreement agreement = Agreements.this.getSelectedAgreement();
			
			//deleteItem.setEnabled(agreement.canDelete());
		//}

	}
	
	
	class TagContextMenu extends ContextMenu {
		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				editTag(tag);
			};
		};
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				removeTag(tag);
			};
		};

		private MenuItem editItem;
		private MenuItem removeItem;
		private Tag tag;
		public TagContextMenu(Tag t){
			tag = t;
			if(t.getIsParent()){
				Dialog d = new Dialog("alert", "Permisos", "Cancelar", false, "Volver", true,false);
				DocumentsDialog popup2 = new DocumentsDialog(d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
					}
				};
			
				popup2.setGlassEnabled(true);
				popup2.show();
			}
			else{
				editItem = addItem("Editar", editCommand,
						"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);
			}
		}
		public TagContextMenu() {}

		@Override
		public void show() {
			super.show();
		}
	}
	
	
	class CategoryContextMenu extends ContextMenu {
		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				editCategory(category);
			};
		};
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				removeCategory(category);
			};
		};

		private MenuItem editItem;
		private MenuItem removeItem;
		private Category category;
		public CategoryContextMenu(Category c){
			category = c;
			if(c.getIsParent()){
				Dialog d = new Dialog("alert", "Permisos", "Cancelar", false, "Volver", true,false);
				DocumentsDialog popup2 = new DocumentsDialog(d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
					}
				};
			
				popup2.setGlassEnabled(true);
				popup2.show();			}
			else{
				
				editItem = addItem("Editar", editCommand,
						"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);
			}
		}
		public CategoryContextMenu() {
			
		}

		@Override
		public void show() {
			//sync();
			super.show();
		}

		//private void sync() {
			//Agreement agreement = Agreements.this.getSelectedAgreement();
			
			//deleteItem.setEnabled(agreement.canDelete());
		//}

	}
	
	//DocumentContextMenu contextMenu = new DocumentContextMenu();
	
	private void initContextMenu() {
		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
				//FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				
			}

		};

		dataGrid.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
		
	}
	
	Document docs = new Document();
	final IDocumentAsync idoc = GWT.create(IDocument.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/document/client/datagrid.css")
		Style dataGridStyle();
	}


	
	public Documents() {
		init();
	}

	interface Binder extends UiBinder<Widget, Documents> {

	}
	
	

	private static final Binder binder = GWT.create(Binder.class);
	/*
	 * @UiField ShowMorePagerPanel pagerPanel;
	 * 
	 * @UiField RangeLabelPager rangeLabelPager;
	 */

	@UiField SimplePanel sp;
	
	@UiField Button upDrive;
	
	@UiField InlineHTML html;
	
	
	@UiField Button filterButton;
	
	@UiField Button advanceSearch;

	@UiField(provided=true) DisclosurePanel epanel;

	
	//@UiField(provided=true)
	//ScrollPanel treepanel;

	@UiField(provided=true) DisclosurePanel dpanel;
	
	
	@UiField SplitLayoutPanel splitLayoutPanel;

	@UiField StackLayoutPanel stack1;

	@UiField Button allButton;

	@UiField Button serviConveniosButton;

	@UiField Button newFile;

	@UiField Button editFile;
	
	@UiField Button delFile;
	
	@UiField Button reset;
	
	@UiField(provided = true) DataGrid<FileInfo> dataGrid;

	@UiField(provided = true) TextBox searchBox;
	
	@UiField(provided = true) SuggestBox enterpriseSearchBox;
	
	@UiField Button searchButton;
	
	@UiField Button eSearchButton;
	
	@UiField Button tagButton;
	
	@UiField Button catButton;
	

	Boolean gConnection;
	Boolean documentManager;
	private void init() {
		idoc.initAux(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				documentManager = result;

				getSons();
				if(docs.getEfiles()==null||docs.getEfiles().isEmpty()){
					idoc.getAllFiles(new AsyncCallback<Document>() {
					
					@Override
					public void onSuccess(Document result) {
						docs = result;

						idoc.getLists(new AsyncCallback<Lists>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.toString());
							}

							@Override
							public void onSuccess(Lists result) {
								lists = result;
								DisclosureImages di = new DisclosureImages();
								dpanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Categor\u00edas");
								epanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Etiquetas");
								Load();
							

							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {

						Window.alert(caught.toString() + ": "
								+ caught.getCause().toString());
					}
				});
				}
				
			}
			@Override
			public void onFailure(Throwable caught) {}
		});

		idoc.isGconnection(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				gConnection = result;
				
				if(result){
					myDrive();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}
		});

	}
	
	@Override
	public void onModuleLoad() {
		stack1 = new StackLayoutPanel(Unit.EM);
	
		//treepanel = new ScrollPanel();
		//DisclosureImages di = new DisclosureImages();
		//dpanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Categor\u00edas");
		//epanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Etiquetas");
	
		

		

	}
	Category cAux;
	Tag tAux;
	
	public void Load() {

		tree();
		getSons();

		/** CATEGORIES **/
	/*	filterButton = new Button();
	filterButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		*/
		VerticalPanel vcat =  new VerticalPanel();
		for(Category c : lists.getCategoryList().getList()){
			
			cAux= c;
			Button b =new Button(c.getName()); 
			b.setStyleName("aon-editDataTable-button aon-icon-category");
			b.addDomHandler(new ContextMenuHandler() {
				Category c = cAux;
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					event.preventDefault();
					event.stopPropagation();
					NativeEvent nativeEvent = event.getNativeEvent();
					CategoryContextMenu ccm = new CategoryContextMenu(c);
					ccm.setPopupPosition(nativeEvent.getClientX(),
							nativeEvent.getClientY());
					ccm.show();
				}
			}, ContextMenuEvent.getType());
			b.addClickHandler(new ClickHandler() {
					Category c = cAux;
					@Override
					public void onClick(ClickEvent event) {
						editFile.setVisible(false);
						delFile.setVisible(false);
						SearchInfo si = new SearchInfo();
						si.setCategory(c.getName());
						idoc.searchFile2(si, docs.getEfiles(),
								new AsyncCallback<FilterUtil>() {
									@Override
									public void onSuccess(FilterUtil result) {
										searchs = result.getFiles();
										docs.setFilter(result.getFiles());
										dataProvider = new ListDataProvider<FileInfo>(
												searchs);
										dataProvider.addDataDisplay(dataGrid);
										isServiconvenios=false;
										/*filterLabel= new Label();
										filterLabel.setStyleName("aon-icon-category");
										filterLabel.setText(cAux.getName());
										*/

										html.setText(result.getCategory());
										html.setVisible(true);
										filterButton.setVisible(true);
										updateDatagridColumns();
										dataGrid.redraw();
									}
									@Override
									public void onFailure(Throwable caught) {
									}
								});
					}
				});
			vcat.add(b); 
		}
		//dpanel.add(vcat);
		dpanel.setContent(vcat);
		VerticalPanel vtag =  new VerticalPanel();
		for(Tag t : lists.getTagList().getList()){
			tAux=t;
			Button b =new Button(t.getName()); 
			b.setStyleName("aon-editDataTable-button aon-icon-tag");
			b.addDomHandler(new ContextMenuHandler() {
				Tag t=tAux;	
				@Override
				public void onContextMenu(ContextMenuEvent event) {
					event.preventDefault();
					event.stopPropagation();
					NativeEvent nativeEvent =  event.getNativeEvent();
					TagContextMenu tcm = new TagContextMenu(t);
					tcm.setPopupPosition(nativeEvent.getClientX(),
								nativeEvent.getClientY());
					tcm.show();
				}
			}, ContextMenuEvent.getType());
			b.addClickHandler(new ClickHandler() {
				Tag t=tAux;	
				@Override
					public void onClick(ClickEvent event) {
						editFile.setVisible(false);
						delFile.setVisible(false);
						SearchInfo si = new SearchInfo();
						Vector<String> v = new Vector<String>();
						v.add(t.getName());
						si.setTag(v);
						idoc.searchFile2(si, docs.getEfiles(),
								new AsyncCallback<FilterUtil>() {
									@Override
									public void onSuccess(FilterUtil result) {
										searchs = result.getFiles();
										docs.setFilter(result.getFiles());
										dataProvider = new ListDataProvider<FileInfo>(
												searchs);
										dataProvider.addDataDisplay(dataGrid);
										isServiconvenios=false;
										/*filterLabel= new Label();
										filterLabel.setStyleName("aon-icon-tag");
										filterLabel.setText(tAux.getName());
										*/
										html.setText(result.getTag());
										html.setVisible(true);
										
										filterButton.setVisible(true);
										
										updateDatagridColumns();
										dataGrid.redraw();
									}
									@Override
									public void onFailure(Throwable caught) {
									}
								});
					}
				});
			vtag.add(b); 
		}
		//epanel.add(vtag);
		epanel.setContent(vtag);
		
		searchBox = new TextBox();
		searchBox.addBitlessDomHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				searchButton.click();
			}
		}, ChangeEvent.getType());
		
		searchBox.addKeyUpHandler(new KeyUpHandler() {
		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					/*event.getNativeEvent().preventDefault();
					if(KeyCodes.KEY_DELETE != event.getNativeEvent().getKeyCode() && KeyCodes.KEY_BACKSPACE != event.getNativeEvent().getKeyCode()){
						/*char c = (char) event.getNativeEvent().getCharCode();
						searchBox.setText(searchBox.getText()+Character.toString(c));
					}
					else{
						Integer pos = searchBox.getCursorPos();
						String str = searchBox.getText();
						Integer length = str.length();
						if(KeyCodes.KEY_DELETE == event.getNativeEvent().getKeyCode()){
							if(pos<str.length()){
								if(pos.equals(0))
									searchBox.setText(str.substring(pos+1));
								else if(pos.equals(length-1))
									searchBox.setText(str.substring(0, pos));
								else
									searchBox.setText(str.substring(0,pos)+str.substring(pos+1));						
								searchBox.setCursorPos(pos);
							}
						}
						if(KeyCodes.KEY_BACKSPACE == event.getNativeEvent().getKeyCode()){
							if(pos>0 && pos<=length){
								if(pos.equals(1))
									searchBox.setText(str.substring(pos));
								else if(pos.equals(length))
									searchBox.setText(str.substring(0,pos-1));
								else
									searchBox.setText(str.substring(0,pos-1)+str.substring(pos));		
								searchBox.setCursorPos(pos-1);
							}
						}
					}*/
					searchButton.click();
				}
			}
		});
		
		enterpriseSearchBox = new SuggestBox(Utils.createOracle(getSons()));
		if (getSons().size() == 1) {
			enterpriseSearchBox.setEnabled(false);	
			
			//reset.setVisible(false);
		}
		enterpriseSearchBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				eSearchButton.click();
			}
		});

		DefaultKeyboardSelectionHandler<FileInfo> selHandler = new DefaultKeyboardSelectionHandler<FileInfo>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<FileInfo> event) {
				if(KeyCodes.KEY_DOWN == event.getNativeEvent().getKeyCode()
					|| KeyCodes.KEY_LEFT == event.getNativeEvent().getKeyCode()
					|| KeyCodes.KEY_RIGHT == event.getNativeEvent().getKeyCode()
					|| KeyCodes.KEY_UP == event.getNativeEvent().getKeyCode()){

			    	FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
			    	if(!isServiconvenios && !object.getIsParent() && documentManager){
						editFile.setVisible(true);
						delFile.setVisible(true);
					}
			    	else{
			    		editFile.setVisible(false);
						delFile.setVisible(false);
			    	}
			   
				}

				
			    if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
			    	Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
			    	FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
			    	if(!isServiconvenios && !object.getIsParent() && documentManager){
						editFile.setVisible(true);
						delFile.setVisible(true);
					}
			    	else{
			    		editFile.setVisible(false);
						delFile.setVisible(false);
			    	}
				}
				
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
   					NativeEvent nativeEvent = event.getNativeEvent();
			    	FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());

			    	DocumentContextMenu contextMenu = new DocumentContextMenu(isServiconvenios || object.getIsParent() || !documentManager);
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
				//else super.onCellPreview(event);	
				
			}
		};
		
 		dataGrid = new DataGrid<FileInfo>(Integer.MAX_VALUE, resources,
				FileInfo.PROVIDES_KEY);
 		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
 	
		/*dataGrid.addBitlessDomHandler(new DragOverHandler() {
			
			@Override
			public void onDragOver(DragOverEvent event) {
				event.preventDefault();
				dataGrid.addStyleName("aon-dataGrid-dragOver");
				dataGrid.redraw();
			}
		}, DragOverEvent.getType());
		
		dataGrid.addBitlessDomHandler(new DragLeaveHandler() {
			
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				 
				dataGrid.removeStyleName("aon-dataGrid-dragOver");
				dataGrid.redraw();				
			}
		}, DragLeaveEvent.getType());
		
		
	
		dataGrid.addBitlessDomHandler(new DropHandler() {
			
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				
				Window.alert("subir archivo");
			
				
			/*	FileUpload fu = FileUpload();
				JavaScriptObject jso = event.getNativeEvent().cast();
				//fu.onBrowserEvent((Event) event.getNativeEvent());
				
				SingleUploader si = new SingleUploader();
				si.onBrowserEvent((Event) event.getNativeEvent());
				newFile2(si);
				
				
			}
		}, DropEvent.getType());
		*/
		dataGrid.setWidth("100%");
		
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		
		addDataDisplay(dataGrid);
		
		ListHandler<FileInfo> sortHandler = getSortHandler();
				//docs.getFil());
		dataGrid.addColumnSortHandler(sortHandler);
		
		
		final SingleSelectionModel<FileInfo> selectionModel = new SingleSelectionModel<FileInfo>(
				FileInfo.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<FileInfo> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		
		initTableColumns(selectionModel, sortHandler);
		
		
		

		
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		// Create the UI defined in DSIImportForm.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		// pagerPanel.setDisplay(dataGrid);
		// rangeLabelPager.setDisplay(dataGrid);
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		if(gConnection){
			stack1.getHeaderWidget(1).setVisible(true);
			stack1.getWidget(1).setVisible(true);
		}
		else {
			stack1.getHeaderWidget(1).setVisible(false);
			stack1.getWidget(1).setVisible(false);
		}		
	}
	
	
	
	private ListHandler<FileInfo> getSortHandler() {
		return new ListHandler<FileInfo>(dataProvider.getList()){
	
			
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<FileInfo> aux  = super.getList();
				List<FileInfo> aux2 = new Vector<FileInfo>();
 				for(Integer i = 0 ; i< aux.size()-1;i++){
 					aux2.set(i, aux.get(aux.size()-1-i ));
 				} 				
				dataProvider.setList(aux2);
				
			}

		};
	}

	private ListDataProvider<FileInfo> dataProvider = new ListDataProvider<FileInfo>();

	public void addDataDisplay(HasData<FileInfo> display) {
		dataProvider = new ListDataProvider<FileInfo>(docs.getEfiles());
		dataProvider.addDataDisplay(display);
		
	}
	
	AonSuggestOracle oracleSons;
	AonSuggestOracle createOracle(Vector<String> l) {
		oracleSons = new AonSuggestOracle();

		for (String s : l) {
			oracleSons.add(s);
		}
		return oracleSons;
	}

	private Boolean esta(String s) {
		if(s.equals(""))return true;
		for (Domain string : getSons()) {
			if(s.equals(string.getName())){
				return true;
			}
		}
		
		return false;
	}
	
	@UiHandler("editFile")
	void edit(ClickEvent event){
		editFile(null);
	}

	public void editFile(SingleUploader up) {
		Integer n =dataGrid.getKeyboardSelectedRow();
		FileInfo fi = dataProvider.getList().get(n);
		if(!fi.getIsDrive() && !fi.getIsParent() && !isServiconvenios){
			Dialog d = new Dialog("edit", "Editar Archivo", "Cancelar", true, "Editar", true,son);
			d.setLists(lists);
			d.setFileInfo(fi);
			d.setUpload(up);
			d.setBaseUrl(GWT.getModuleBaseURL());
			popup2 = new DocumentsDialog(d){
				@Override
				protected void onAccept() {
					Integer n =dataGrid.getKeyboardSelectedRow();
        			FileInfo fileInfo = dataProvider.getList().get(n);
            		// Descripción - Description
            		TextBox tb = (TextBox)grid.getWidget(0, 1);
            		if(!tb.getText().equals(""))
            			fileInfo.setTitle(tb.getText());
            		// Confidencial - Confidential
            		CheckBox cb = (CheckBox)grid.getWidget(2, 1);
            		fileInfo.setConfidential(cb.getValue());
            		// Fecha - Date
            		DateBox db = (DateBox)grid.getWidget(3, 1);
            		if(!db.getTextBox().getText().equals("")){
            			Date d = db.getValue();
            			fileInfo.setDate(d);
            			String year="";
            			if(d.getYear()>100) year = "20"+Integer.toString(d.getYear()).substring(1); 
            			else year = "19"+Integer.toString(d.getYear());
            			fileInfo.setDateStr(Integer.toString(d.getDate())+"-"+Integer.toString(d.getMonth())+"-"+year);
            		}
            		else {
            			fileInfo.setDate(null);
            			fileInfo.setDateStr("-");
            		}
            		// Categoria - Category
            		ListBox lb1 = (ListBox)grid.getWidget(4, 1);
            		for (Category c : lists.getCategoryList().getList()) {
            			String s1 = lb1.getItemText(lb1.getSelectedIndex());
            			if(s1.substring(0, 1).equals(Character.toString((char)9650)) || s1.substring(0, 1).equals(Character.toString((char)9660)) )
            				s1 = s1.substring(1);
            			if(c.getName().equals(s1)){
							fileInfo.setCategory(c.getId());
							fileInfo.setCategoryStr(s1);
						}
            			if(s1.equals("-")){
							fileInfo.setCategory(null);
							fileInfo.setCategoryStr(s1);
            			}
					}
            		// Ambito - Scope
            		ListBox lb3 = (ListBox)grid.getWidget(6, 1);

            		for (Scope s : lists.getScopeList().getList()) {
            			String s3 = lb3.getItemText(lb3.getSelectedIndex());
            			if(s3.substring(0, 1).equals(Character.toString((char)9650)) || s3.substring(0, 1).equals(Character.toString((char)9660)) )
            				{
            				s3 = s3.substring(1);
            					
            				}
            			if(s.getName().equals(s3)){
							fileInfo.setScope(s);
						}
					}	            	
            		// Etiquetas - Tags
            		Vector<Tag> tags = new Vector<Tag>();
            		VerticalPanel vp = (VerticalPanel)grid.getWidget(5, 1);
            		String str="";
            		for(Integer i = 0 ;i<vp.getWidgetCount();i++){
            			HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            			ListBox lb = (ListBox)hp.getWidget(0);
            			for (Tag t : lists.getTagList().getList()) {
                			String s2 = lb.getItemText(lb.getSelectedIndex());
                			if(s2.substring(0, 1).equals(Character.toString((char)9650)) || s2.substring(0, 1).equals(Character.toString((char)9660)) )
                				s2 = s2.substring(1);
							if(t.getName().equals(s2)){
								tags.add(t);
								str = str+t.getName()+", ";
							}
						}
            		}
            		if(tags.size()<1)
            			fileInfo.setTagsStr("-");
            		else fileInfo.setTagsStr(str.substring(0,str.length()-2));
            		fileInfo.setTags(tags);
            		
            		/*Window.alert(fileInfo.getAonType()+"/n"
            				+Integer.toString(fileInfo.getCategory())+"/n" 
            				+fileInfo.getConf().toString()+"/n"
            				+fileInfo.getDate()+"/n"
            				+fileInfo.getScope().toString()+"/n"
            				);*/
            		idoc.editFile(fileInfo,new AsyncCallback<FileInfo>() {
						@Override
						public void onSuccess(FileInfo result) {
							vertical = new VerticalPanel();
							Vector<FileInfo> aux = new Vector<FileInfo>();
							for (FileInfo f : docs.getFiles()) {
								if(f.getFileId() == result.getFileId())
									aux.add(result);
								else aux.add(f);		
							}
							docs.setFiles(aux);
							aux = new Vector<FileInfo>();
							for (FileInfo f : docs.getEfiles()) {
								if(f.getFileId() == result.getFileId())
									aux.add(result);
								else aux.add(f);		
							}
							docs.setEfiles(aux);
							aux = new Vector<FileInfo>();
							for (FileInfo f : docs.getFilter()) {
								if(f.getFileId() == result.getFileId())
									aux.add(result);
								else aux.add(f);		
							}
							docs.setFilter(aux);
							aux= new Vector<FileInfo>();
							for (FileInfo f : dataProvider.getList()) {
								if(f.getFileId() == result.getFileId()){
								aux.add(result);
								}
								else{
									aux.add(f);
								}
							}
							
							dataProvider = new ListDataProvider<FileInfo>(aux);
							dataProvider.addDataDisplay(dataGrid);
							updateDatagridColumns();
							dataGrid.redraw();
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
					hide();
					
				}
				@Override
				protected void onCancel() {
					hide();
					vertical = new VerticalPanel();				
				}
			};

		}
		else{
			Dialog d = new Dialog("alert", "Editar Archivo", "Cancelar", false, "Volver", true,son);
			d.setFileInfo(fi);
			popup2 = new DocumentsDialog(d) {
				
				@Override
				protected void onCancel() {
					hide();
				}
				
				@Override
				protected void onAccept() {
					hide();
				}
			};
		}
		popup2.setGlassEnabled(true);
		popup2.show();
	}

	@UiHandler("delFile")
	void del(ClickEvent event){
		removeFile();
	}
	
	
	public void removeFile(){
		Integer n =dataGrid.getKeyboardSelectedRow();
		FileInfo fi  = dataProvider.getList().get(n);
		if(!fi.getIsParent() && !isServiconvenios){
		Dialog d = new Dialog("delete", "Borrar Archivo", "Cancelar", true, "Borrar", true,son);
		d.setFileInfo(fi);
		
		popup2 = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();				
			}
			
			@Override
			protected void onAccept() {
				hide();
				if(getFileInfo().getIsDrive()){
					idoc.deleteMydrive(getFileInfo(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							//TODO Actualizar datagrid!!!
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				else idoc.removeFile(getFileInfo(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {}
					@Override
					public void onSuccess(Void result) {
						Integer n =dataGrid.getKeyboardSelectedRow();
						FileInfo fi = dataProvider.getList().get(n);
						Vector<FileInfo> l = new Vector<FileInfo>();
						for (Integer i = 0; i<docs.getFiles().size();i++) {
							if(docs.getFiles().get(i).getFileId() != fi.getFileId()){
								l.add(docs.getFiles().get(i));
							}
						}
						docs.setFiles(l);
						l = new Vector<FileInfo>();
						for (Integer i = 0; i<docs.getEfiles().size();i++) {
							if(docs.getEfiles().get(i).getFileId() != fi.getFileId()){
								l.add(docs.getEfiles().get(i));
							}
						}
						docs.setEfiles(l);
						l = new Vector<FileInfo>();
						for (Integer i = 0; i<docs.getFilter().size();i++) {
							if(docs.getFilter().get(i).getFileId() != fi.getFileId()){
								l.add(docs.getFilter().get(i));
							}
						}
						docs.setFilter(l);
						l = new Vector<FileInfo>();
						for(Integer j = 0 ; j<dataProvider.getList().size();j++){
							if(dataProvider.getList().get(j).getFileId() != fi.getFileId()){
								l.add(dataProvider.getList().get(j));
							}
						}
						dataProvider = new ListDataProvider<FileInfo>(l);
						dataProvider.addDataDisplay(dataGrid);
						updateDatagridColumns();
						dataGrid.redraw();
						
					}
				} );
			}
		};

	}
		else{
			Dialog d = new Dialog("alert", "Borrar Archivo", "Cancelar", false, "Volver", true,son);
			d.setFileInfo(fi);
			popup2 = new DocumentsDialog(d){
				@Override
				protected void onAccept() {
					hide();
				}
				@Override
				protected void onCancel() {
					hide();					
				}
			};
		}
		popup2.setGlassEnabled(true);
		popup2.show();
	
	}
	
	
	public Vector<Domain> getSons() {
		if(sons==null){
		idoc.getSons(new AsyncCallback<Vector<Domain>>() {
			
			@Override
			public void onSuccess(Vector<Domain> result) {
				sons= result;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		}
		return sons;
	}
	
	
	
	Vector<Domain> sons = null;
	FileUpload fuchange;
	SingleUploader fuchange2;
	
	@UiHandler("newFile")
	void XXXXXX(ClickEvent event) {
		newFile(null);
	}
	
	FileInfo finsert;
	DocumentsDialog popup2;
	
	private void newFile(SingleUploader up) {	
		if(!documentManager){
			Dialog d = new Dialog("alert","Nuevo Archivo","Cancelar",false,"Volver",true,son);
			popup2 = new DocumentsDialog(d) {
				
				@Override
				protected void onCancel() {
					hide();
				}
				
				@Override
				protected void onAccept() {
					hide();
				}
			};

		}
		else{
		Dialog d = new Dialog("new","Nuevo Archivo","Cancelar",true,"Guardar",true,son);
		d.setBaseUrl(GWT.getModuleBaseURL());
		d.setLists(lists);
		d.setSons(getSons());
		d.setUpload(up);
		popup2 = new DocumentsDialog(d){ 
			@Override
			protected void onAccept() {				
	//upload.getForm().submit();
				
				FileInfo fi = new  FileInfo();
            	// Descripción - Description
            	TextBox tb = (TextBox)grid.getWidget(1, 1);
            	fi.setTitle(tb.getText());
            	// Confidencial - Confidential
            	CheckBox cb = (CheckBox)grid.getWidget(3, 1);
            	fi.setConfidential(cb.getValue());
            	// Fecha - Date
            	DateBox db = (DateBox)grid.getWidget(4, 1);
            	fi.setDate(db.getValue());
            	// Categoria - Category
            	fi.setCategory(-1);
            	ListBox lb1 = (ListBox)grid.getWidget(5, 1);
            	for (Category c : lists.getCategoryList().getList()) {
            		String s1 = lb1.getItemText(lb1.getSelectedIndex());
            		if(s1.substring(0, 1).equals(Character.toString((char)9650)))
            			s1 = s1.substring(1);
					if(c.getName().equals(s1)){
						fi.setCategory(c.getId());
					}
				}
            	for (Category c : lists.getCategoryListSon().getList()) {
            		String s1 = lb1.getItemText(lb1.getSelectedIndex());
					if(s1.substring(0, 1).equals(Character.toString((char)9660))){
						s1= s1.substring(1);
						if(c.getName().equals(s1))
							fi.setCategory(c.getId());
					}
				}
            	// Ambito - Scope
            	fi.setScope(new Scope(-1));
            	ListBox lb3 = (ListBox)grid.getWidget(7, 1);
            	for (Scope s : lists.getScopeList().getList()) {
            		String s3 = lb3.getItemText(lb3.getSelectedIndex());
            		if(s3.substring(0, 1).equals(Character.toString((char)9650)))
            			s3 = s3.substring(1);
					if(s.getName().equals(s3)){
						fi.setScope(s);
					}
				}
            	for (Scope scope : lists.getScopeListSon().getList()) {
            		String s3 = lb3.getItemText(lb3.getSelectedIndex());
					if(s3.substring(0, 1).equals(Character.toString((char)9660))){
						s3= s3.substring(0);
						if(scope.getName().equals(s3))
							fi.setScope(scope);
					}
				}
            	
            	// Etiquetas - Tags
            	Vector<Tag> tags = new Vector<Tag>();

            	VerticalPanel vp = (VerticalPanel)grid.getWidget(6, 1);

            	for(Integer i = 0 ;i<vp.getWidgetCount();i++){
            		HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            		ListBox lb = (ListBox)hp.getWidget(0);
            		for (Tag t : lists.getTagList().getList()) {
                		String s2 = lb.getItemText(lb.getSelectedIndex());
                		if(s2.substring(0, 1).equals(Character.toString((char)9650)))
                			s2 = s2.substring(1);
						if(t.getName().equals(s2)){
			            	tags.add(t);
						}
					}
                	for (Tag t : lists.getTagListSon().getList()) {
                		String s2 = lb.getItemText(lb.getSelectedIndex());
    					if(s2.substring(0, 1).equals(Character.toString((char)9660))){
    						s2= s2.substring(1);
    						if(t.getName().equals(s2))
    							tags.add(t);
    					}
    				}
            	}
            	fi.setTags(tags);
            	SuggestBox sb = null;
            	// Dominio - Domain
            	if (getSons().size()!=1){
            		 sb = (SuggestBox)grid.getWidget(0, 1);
            		if(!esta(sb.getText())){
            			fi.setDomain("false"); 
            		}
            		else fi.setDomain(Utils.getOracleString(sb.getText()));
				}
            	else fi.setDomain("");
            	finsert= fi;
                idoc.newFile(fi,new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							hide();
							idoc.insertFile(finsert, new AsyncCallback<FileInfo>() {

								@Override
								public void onFailure(Throwable caught) {}
								
								@Override
								public void onSuccess(FileInfo result) {
									Vector<FileInfo> aux = new Vector<FileInfo>();
									for (FileInfo f : docs.getFiles()) {
										aux.add(f);		
									}
									aux.add(result);
									docs.setFiles(aux);
									Integer index=0;
									while(docs.getFilter().get(index).getIsParent()){
										index++;
									}
									if(result.getDomain().equals(docs.getFilter().get(index).getDomain())){
										aux = new Vector<FileInfo>();
										for (FileInfo f : docs.getEfiles()) {
											aux.add(f);		
										}
										aux.add(result);
										docs.setEfiles(aux);

								
										if(html.isVisible()){
											Integer i = 0;

											while(i<result.getTags().size() && !html.getText().equals(result.getTags().get(i).getName())){
												i++;
											}
											if(!html.isVisible() || (html.isVisible() && (html.getText().equals(result.getCategoryStr()) || result.getTags().size()>i))){
												aux= new Vector<FileInfo>();
												for (FileInfo f : docs.getFilter()) {
													aux.add(f);
												}
												aux.add(result);
												docs.setFilter(aux);
												aux= new Vector<FileInfo>();
												for (FileInfo f : dataProvider.getList()) {
													aux.add(f);
												}
												aux.add(result);
												dataProvider = new ListDataProvider<FileInfo>(aux);
												dataProvider.addDataDisplay(dataGrid); 
												updateDatagridColumns();
												dataGrid.redraw();
											}
										}
										else{
											aux= new Vector<FileInfo>();
											for (FileInfo f : docs.getFilter()) {
												aux.add(f);
											}
											aux.add(result);
											docs.setFilter(aux);
											aux= new Vector<FileInfo>();
											for (FileInfo f : dataProvider.getList()) {
												aux.add(f);
											}
											aux.add(result);
											dataProvider = new ListDataProvider<FileInfo>(aux);
											dataProvider.addDataDisplay(dataGrid); 
											updateDatagridColumns();
											dataGrid.redraw();
										}
									}
								}
							});
							vertical = new VerticalPanel();
						}
						else{
														
							TextBox tb =(TextBox) grid.getWidget(0, 1);
							SuggestBox sb =(SuggestBox) grid.getWidget(7, 1);
							if(tb.getText().equals("")) grid.getWidget(0, 1).addStyleName("dateBoxFormatError");
							else grid.getWidget(0, 1).setStyleName("aon-inputText");
							if(!esta(sb.getText())) grid.getWidget(7, 1).addStyleName("dateBoxFormatError");
							else grid.getWidget(7, 1).setStyleName("aon-inputText");
							idoc.check(new AsyncCallback<Boolean>() {
														
								@Override
								public void onSuccess(Boolean result) {
									if(result){
										final SingleUploader upload2 = newUploader(null,GWT.getModuleBaseURL());
										upload2.addBitlessDomHandler(new ChangeHandler() {
											
											@Override
											public void onChange(ChangeEvent event) {
												upload2.removeStyleName("dateBoxFormatError");
												upload2.addStyleName("aon-inputTextBackground");
											}
										},ChangeEvent.getType());
										
										upload2.addStyleName("dateBoxFormatError");
										
										grid.setWidget(1, 1, upload2);
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
			
			@Override
			protected void onCancel() {
				hide();
				vertical = new VerticalPanel();			
			}
		};
		}
		popup2.setGlassEnabled(true);
		popup2.show();
	
	}
	
	Lists lists =new Lists();
	FlexTable grid;
	HorizontalPanel h2;
	
	@UiHandler("advanceSearch")
	void advancedSearch(ClickEvent event) {
		editFile.setVisible(false);
		delFile.setVisible(false);
		search();
//		html.setVisible(false);
//		filterButton.setVisible(false);
	}

	VerticalPanel vertical = new VerticalPanel();
	Vector<FileInfo> searchs;

	@UiHandler("searchButton")
	void XXXXX(ClickEvent event) {
		editFile.setVisible(false);
		delFile.setVisible(false);
		String searchStr = searchBox.getText();
		Vector<FileInfo> vaux = new Vector<FileInfo>();
		if(isServiconvenios) vaux = docs.getServiconvenios();
		else vaux = docs.getFilter();
		//dataProvider.getList().stream().forEach(f-> vaux.add(f));
 		
		idoc.searchFile(searchStr, vaux,
				new AsyncCallback<Vector<FileInfo>>() {

					@Override
					public void onSuccess(Vector<FileInfo> result) {
						searchs = result;
						dataProvider = new ListDataProvider<FileInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						updateDatagridColumns();
						dataGrid.redraw();
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}
	
	Boolean son = false;
	@UiHandler("eSearchButton")
	void sbutton(ClickEvent event) {
		html.setVisible(false);
		filterButton.setVisible(false);
		String searchStr = Utils.getOracleString(enterpriseSearchBox.getText());
		son = !searchStr.equals(docs.getDomain());
		idoc.eSearchFile(docs.getFiles(),searchStr,new AsyncCallback<Vector<FileInfo>>() {
					@Override
					public void onSuccess(Vector<FileInfo> result) {
						docs.setEfiles(result);
						docs.setFilter(result);
						isServiconvenios =false;
						searchs = result;
						dataProvider = new ListDataProvider<FileInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						updateDatagridColumns();
						dataGrid.redraw();
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	

	@UiHandler("allButton")
	void getAllAttach(ClickEvent event) {
		editFile.setVisible(false);
		delFile.setVisible(false);
		html.setVisible(false);
		filterButton.setVisible(false);
		if(docs.getEfiles()==null){
		idoc.getAllFiles(new AsyncCallback<Document>() {

			@Override
			public void onSuccess(Document result) {
				docs = result;
				addDataDisplay(dataGrid);
				isServiconvenios=false;
				updateDatagridColumns();
				dataGrid.redraw();
			}


			@Override
			public void onFailure(Throwable caught) {

				Window.alert(caught.toString() + ": "
						+ caught.getCause().toString());
			}
		});
		}
		else{
			addDataDisplay(dataGrid);
			isServiconvenios=false;
			updateDatagridColumns();
			dataGrid.redraw();
		}
		docs.setFilter(docs.getEfiles());

	}

	Boolean isServiconvenios=false;
	@UiHandler("serviConveniosButton")
	void getServiConveniosAttach(ClickEvent event) {
		editFile.setVisible(false);
		delFile.setVisible(false);
		isServiconvenios= true;
		html.setVisible(false);
		filterButton.setVisible(false);
		if(docs.getServiconvenios().isEmpty()){
			idoc.getServiConveniosFiles(new AsyncCallback<Vector<FileInfo>>() {
			
			@Override
			public void onSuccess(Vector<FileInfo> result) {
				docs.setServiconvenios(result);
				dataProvider = new ListDataProvider<FileInfo>(docs.getServiconvenios());
				dataProvider.addDataDisplay(dataGrid);
				updateDatagridColumns();
				dataGrid.redraw();
			}

			@Override
			public void onFailure(Throwable caught) {

				Window.alert(caught.toString() + ": "
						+ caught.getCause().toString());
			}
		});}
		else{
			dataProvider = new ListDataProvider<FileInfo>(docs.getServiconvenios());
			dataProvider.addDataDisplay(dataGrid);
			updateDatagridColumns();
			dataGrid.redraw();
		}
		docs.setFilter(docs.getServiconvenios());
	}
	
	private void initTableColumns(
			final SelectionModel<FileInfo> selectionModel,
			ListHandler<FileInfo> sortHandler) {
		initContextMenu();
		/** Name Column **/
		Column<FileInfo, String> nameColumn = new Column<FileInfo, String>(
				new ButtonCell()) {

			@Override
			public void render(Context context, FileInfo object,
					SafeHtmlBuilder sb) {
				if(object.getIsParent()){
					sb.appendHtmlConstant("<g:Button class=\"aon-editDataTable-button "
						+ object.getIcon() + "\" >"+"&nbsp;&nbsp;"+object.getTitle()
						+"</g:Button><span title='Documento heredado' class='aon-editDataTable-button aon-icon-shield'>&nbsp;</span>");
				}
				else {
					sb.appendHtmlConstant("<g:Button class=\"aon-editDataTable-button "
						+ object.getIcon() + "\" >"+"&nbsp;&nbsp;"+object.getTitle());
				}
				
			}
			@Override
			public String getValue(FileInfo object) {
				return object.getTitle();
			}
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		 nameColumn.setSortable(true); 
		 sortHandler.setComparator(nameColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		 
			nameColumn.setFieldUpdater(new FieldUpdater<FileInfo, String>() {
				@Override
				public void update(int index, FileInfo object, String value) {
					
				}
				
			});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Descripci\u00f3n");

		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);

		/** Category Column **/
		Column<FileInfo, String> categoryColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				return object.getCategoryStr();
			}
		};
		 categoryColumn.setSortable(true); 
		 sortHandler.setComparator(categoryColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getCategoryStr().compareTo(o2.getCategoryStr());
			}
		});

		dataGrid.getColumnSortList().push(categoryColumn);
		
		dataGrid.addColumn(categoryColumn, "Categor\u00eda");

		dataGrid.setColumnWidth(categoryColumn, 16, Unit.PCT);

		/** Date Column **/
		Column<FileInfo, String> dateColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				return object.getDateStr();
			}
		};
		
		 dateColumn.setSortable(true); 
		 sortHandler.setComparator(dateColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				String date1;
				String date2;
				if(o1.getDate() == null) date1 = "-";
				else date1 = o1.getDate().toString();
				if(o2.getDate() == null) date2 = "-";
				else date2 = o2.getDate().toString();
				return date1.compareTo(date2);
			}
		});
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.addColumn(dateColumn, "Fecha");

		dataGrid.setColumnWidth(dateColumn, 13, Unit.PCT);
		
		/** Confidential Column **/
		Column<FileInfo, Boolean> confColumn = new Column<FileInfo, Boolean>(new CheckboxCell(){
			@Override
			public void onBrowserEvent(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Boolean value, NativeEvent event,
					ValueUpdater<Boolean> valueUpdater) {

			}
			
			@Override
			public boolean isEditing(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Boolean value) {
				return false;
			}
			
		}){
			@Override
			public Boolean getValue(FileInfo object) {
				return object.getConfidential();
			}
			
		};
		
		
	SafeHtml sh = new SafeHtml() {
		
		@Override
		public String asString() {
			return "<span class='aon-icon-confidential' style='padding-left:15px' title='Confidencial'>&nbsp;</span>";
		}
	};
		dataGrid.addColumn(confColumn,sh);
		dataGrid.setColumnWidth(confColumn, 5 , Unit.PCT);
		
		/** Size Column **/
		Column<FileInfo, String> sizeColumn = getSizeColumn(sortHandler);
		dataGrid.getColumnSortList().push(sizeColumn);
		dataGrid.addColumn(sizeColumn, "Tama\u00f1o("+getTotalSize()+")");
		dataGrid.setColumnWidth(sizeColumn, 13, Unit.PCT);

		/** Tag Column **/
		Column<FileInfo, String> tagColumn = getTagColumn(sortHandler);
		dataGrid.getColumnSortList().push(tagColumn);
		dataGrid.addColumn(tagColumn, "Etiquetas");	
		dataGrid.setColumnWidth(tagColumn, 16, Unit.PCT);

		/** Download Column **/
		Column<FileInfo,String> downloadColumn = getDownloadColumn();
		dataGrid.addColumn(downloadColumn, "Archivo("+dataProvider.getList().size()+")");
		dataGrid.setColumnWidth(downloadColumn, 10, Unit.PCT);
	}
	Vector<TreeDriveInfo> vtree;
	
	private void download(FileInfo object){
		if(!object.getIsGdocs()){
			String driveId="";
			if(object.getDriveId()!=null)driveId= object.getDriveId();
			String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download/"
                	+ "?file_id=" + Integer.toString(object.getFileId())
                	+ "&drive_id=" +URL.encode(driveId)
                	+ "&mimetype=" +object.getMimetype()
                	+ "&isdrive=" +object.getIsDrive();
			Window.open( fileDownloadURL, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");
		}
	}
	
	private void print(FileInfo object){
		String driveId="";
		if(object.getDriveId()!=null)driveId= object.getDriveId();
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_print/"
                + "?file_id=" + Integer.toString(object.getFileId())
                + "&drive_id=" +URL.encode(driveId)
                + "&mimetype=" +object.getMimetype();
		PrintWindow.open(fileDownloadURL, "_blank", null);
	}
	
	private void search() {
		Dialog d = new Dialog("search", "Busqueda Avanzada", "Cancelar", true,
				"Buscar", true,son);
		d.setLists(lists);
		d.setSons(getSons());
		popup2 = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
				vertical = new VerticalPanel();
			}
			
			@Override
			protected void onAccept() {
				html.setVisible(false);
				filterButton.setVisible(false);
				SearchInfo si = new SearchInfo();
				if(getSons().size()!=1){
					SuggestBox tb0 = (SuggestBox) grid.getWidget(0, 1);
					if ("".equals(tb0.getText()))
						si.setDomain(null);
					else
						si.setDomain(Utils.getOracleString(tb0.getText()));
				}else si.setDomain(null);
				
				TextBox tb1 = (TextBox) grid.getWidget(1, 1);
				if ("".equals(tb1.getText()))
					si.setName(null);
				else
					si.setName(tb1.getText());

				CheckBox cb = (CheckBox) grid.getWidget(2, 1);
				si.setConfidential(cb.getValue());

				DateBox tb2 = (DateBox) grid.getWidget(3, 1);
				if ("".equals(tb2.getTextBox().getText()))
					si.setDate(null);
				else
					si.setDate(tb2.getTextBox().getText());

				ListBox lb1 = (ListBox) grid.getWidget(4, 1);
				String s1 = null;
				for (int i = 0; i < lb1.getItemCount(); i++) {
					if (lb1.isItemSelected(i)) {
						s1 = lb1.getValue(i);
						if(s1.substring(0, 1).equals(Character.toString((char)9660)) || s1.substring(0, 1).equals(Character.toString((char)9650)))
							s1= s1.substring(1);
					}
				}
				if ("-".equals(s1) || s1 == null)
					si.setCategory(null);
				else
					si.setCategory(s1);
// TODO		
 
				VerticalPanel vp = (VerticalPanel) grid.getWidget(5, 1);

				Vector<String> v = new Vector<String>();
				Vector<String> v2 = new Vector<String>();
				HorizontalPanel hp = (HorizontalPanel) vp.getWidget(0);

				ListBox lb2 = (ListBox) hp.getWidget(0);

				String s2 = null;
				for (int i = 0; i < lb2.getItemCount(); i++) {

					if (lb2.isItemSelected(i)) {

						s2 = lb2.getValue(i);

						if(s2.substring(0, 1).equals(Character.toString((char)9660))|| s2.substring(0, 1).equals(Character.toString((char)9650)))
							s2= s2.substring(1);

					}
				}

				if ("-".equals(s2) || s2 == null)

					si.setTag(null);
					
				else {
					v.add(s2);

					for (int i = 1; i < vp.getWidgetCount(); i++) {
						HorizontalPanel hp2 = (HorizontalPanel) vp.getWidget(i);
						ListBox lbyo = (ListBox) hp2.getWidget(0);
						ListBox lb = (ListBox) hp2.getWidget(1);

						String s = null;
						for (int j = 0; j < lb.getItemCount(); j++) {
							if (lb.isItemSelected(j)) {
								s = lb.getValue(j);
								if(s.substring(0, 1).equals(Character.toString((char)9660))|| s.substring(0, 1).equals(Character.toString((char)9650)))
									s= s.substring(1);
							}
						}
						String ss = null;
						for (int k = 0; k < lbyo.getItemCount(); k++) {
							if (lbyo.isItemSelected(k)) {
								ss = lbyo.getValue(k);
							}
						}
						if (!s.equals("-")) {
							v2.add(ss);
							v.add(s);
						}
					}
					si.setTag(v);
					si.setYoTag(v2);
				}

				ListBox lb3 = (ListBox) grid.getWidget(6, 1);
				String s3 = null;
				for (int i = 0; i < lb3.getItemCount(); i++) {
					if (lb3.isItemSelected(i)) {
						s3 = lb3.getValue(i);
						if(s3.substring(0, 1).equals(Character.toString((char)9660)) || s3.substring(0, 1).equals(Character.toString((char)9650)))
							s3= s3.substring(1);
					}
				}
				if ("-".equals(s3) || s3 == null)
					si.setScope(null);
				else
					si.setScope(s3);
				Vector<FileInfo> vaux = new Vector<FileInfo>();
				if (isServiconvenios)
					vaux = docs.getServiconvenios();
				else
					vaux = docs.getEfiles();

				idoc.searchFile(si, vaux,docs.getFiles(),
							new AsyncCallback<Vector<FileInfo>>() {

							@Override
							public void onSuccess(Vector<FileInfo> result) {
								if( !result.isEmpty() && !docs.getEfiles().get(0).getDomain().equals(result.get(0).getDomain())){
									idoc.eSearchFile(docs.getFiles(), result.get(0).getDomain(), new AsyncCallback<Vector<FileInfo>>() {
										
										@Override
										public void onSuccess(Vector<FileInfo> result) {
											docs.setEfiles(result);
											docs.setFilter(result);
										}
										
										@Override
										public void onFailure(Throwable caught) {
										
										}
									});
								}
								searchs = result;
								dataProvider = new ListDataProvider<FileInfo>(
										searchs);
								dataProvider.addDataDisplay(dataGrid);
								updateDatagridColumns();
								dataGrid.redraw();

							}

							@Override
							public void onFailure(Throwable caught) {

							}
						});
				hide();
				vertical = new VerticalPanel();
			}
		};
		popup2.setGlassEnabled(true);
		popup2.show();

	}
	
	String dId;

	private void share(FileInfo object) {
		Dialog d = new Dialog("share", "Compartir Archivo", "Cancelar", true,
				"Compartir", true,son);
		d.setFileInfo(object);
		popup2 = new DocumentsDialog(d) {
			@Override
			protected void onAccept() {
				TextBox tb = (TextBox) grid.getWidget(0, 1);
				// TODO CHECK EMAIL
				hide();
				if(getFileInfo().getIsDrive()){
					idoc.shareMydrive(tb.getText(), getFileInfo().getDriveId(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				else idoc.share(tb.getText(), getFileInfo().getDriveId(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}

			@Override
			protected void onCancel() {
				hide();
			}
		};
		popup2.setGlassEnabled(true);
		popup2.show();
	}

	private void info(FileInfo object) {
		Dialog d = new Dialog("info", "Detalles Archivo", "Cancelar", false,
				"Salir", true,son);
		d.setFileInfo(object);
		popup2 = new DocumentsDialog(d) {

			@Override
			protected void onCancel() {
				hide();
			}

			@Override
			protected void onAccept() {
				hide();
			}
		};
		popup2.setGlassEnabled(true);
		popup2.setAnimationEnabled(false);
		popup2.setModal(false);
		popup2.show();
	}

	private void sons(TreeItem parent, Vector<TreeDriveInfo> sons) {
		for (TreeDriveInfo f : sons) {
			TreeItem ti = new TreeItem();
			ti = parent.addTextItem(f.getParent().getTitle());
			sons(ti, f.getSons());
		}
	}
	
	String rootId;
	Tree t ;
	public void tree(){
		sp = new SimplePanel();
		t = new Tree();
		
		idoc.getRootId(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				rootId = result;
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		idoc.drive(new TreeMap<String, List<FileInfo>>(),"",new AsyncCallback<TreeMap<String,List<FileInfo>>>() {
			@Override
			public void onSuccess(TreeMap<String, List<FileInfo>> result) {
				TreeItem ti = new TreeItem();
				Button button = new Button("Mi Unidad");
				button.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder-root");
				button.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						idoc.getDriveFile(rootId, new AsyncCallback<Vector<FileInfo>>() {
							@Override
							public void onSuccess(Vector<FileInfo> result) {

								docs.setFilter(result);
								dataProvider = new ListDataProvider<FileInfo>(result);
								dataProvider.addDataDisplay(dataGrid);
								updateDatagridColumns();
								dataGrid.redraw();
							}
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				});
				ti = t.addItem(button);
				treeSons(ti,result,rootId);
				sp.add(t);
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}
	FileInfo auxiliarf;
	TreeItem tiaux;
	FileInfo faux;
	public void treeSons(TreeItem t,TreeMap<String, List<FileInfo>> folders , String id){
		Integer i = 0;
		for (FileInfo f : folders.get(id)) {
			auxiliarf = f;
			TreeItem ti = new TreeItem();
			Button button = new Button(f.getTitle());
			button.setStyleName("aon-editDataTable-button aon-icon-google-drive-folder");
			button.addClickHandler(new ClickHandler() {
				FileInfo f = auxiliarf;
				@Override
				public void onClick(ClickEvent event) {
					idoc.getDriveFile(f.getDriveId(), new AsyncCallback<Vector<FileInfo>>() {
						@Override
						public void onSuccess(Vector<FileInfo> result) {

							docs.setFilter(result);
							dataProvider = new ListDataProvider<FileInfo>(result);
							dataProvider.addDataDisplay(dataGrid);
							updateDatagridColumns();
							dataGrid.redraw();
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
			});
			ti = t.addItem(button);
			ti.setTitle(f.getDriveId());
			tiaux = ti;
			faux= f;
			idoc.drive(folders, f.getDriveId(), new AsyncCallback<TreeMap<String,List<FileInfo>>>() {
				TreeItem ti = tiaux;
				FileInfo f = faux;
				@Override
				public void onSuccess(TreeMap<String, List<FileInfo>> result) {
					treeSons(ti, result, f.getDriveId());
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
			/*if(folders.containsKey(f.getDriveId())){
				treeSons(ti, folders, f.getDriveId());
			}*/
			i++;
		}
	}
	public void myDrive() {
		idoc.myDrive("", new AsyncCallback<Vector<TreeDriveInfo>>() {

			@Override
			public void onSuccess(Vector<TreeDriveInfo> result) {
				Tree tree = new Tree();

				for (TreeDriveInfo f : result) {
					TreeItem ti = new TreeItem();
					ti = tree.addTextItem(f.getParent().getTitle());
					sons(ti, f.getSons());
				}
				//treepanel.add(tree);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});

	}
	@UiHandler("filterButton")
	void close(ClickEvent event){
		html.setVisible(false);
		filterButton.setVisible(false);
		if(!isServiconvenios)
			addDataDisplay(dataGrid);
		else {
			dataProvider = new ListDataProvider<FileInfo>(docs.getServiconvenios());
			dataProvider.addDataDisplay(dataGrid); 
		}
		updateDatagridColumns();
		dataGrid.redraw();
	}
	Viewer viewer;
	private  void getAsHTMl(FileInfo object, Integer num) {
		viewer = new Viewer(dataProvider.getList(),num,object,100){
			@Override
			protected void onDownload() {
				download(fileInfo);
			}
			@Override
			protected void onPrint() {	
				print(fileInfo);
			}
			@Override
			protected void onShare() {
				share(fileInfo);
			};
			@Override
			protected void onChange() {
				title.setText(fileInfo.getTitle());
				title.addStyleName(fileInfo.getIcon());
				idoc.getAsHTML(fileInfo, 100 , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						viewer.container.setHTML(result);
						viewer.show();
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
			@Override
			protected void onZoomPlus() {					
				idoc.getAsHTML(fileInfo, zoom , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						viewer.container.setHTML(result);	
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
			@Override
			protected void onZoomMinus() {	
				idoc.getAsHTML(fileInfo, zoom , new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						viewer.container.setHTML(result);	
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		viewer.auxiliar.addBitlessDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				viewer.menu.setVisible(true);
				if(viewer.num < dataProvider.getList().size()-1) viewer.next.setVisible(true);
				if(viewer.num != 0)viewer.prev.setVisible(true);
			}
		}, MouseOverEvent.getType());
		
		viewer.auxiliar.addBitlessDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(event.getClientY()>60){
					viewer.menu.setVisible(false);
					viewer.next.setVisible(false);
					viewer.prev.setVisible(false);
				}
			}
		}, MouseOutEvent.getType());
		
		viewer.auxiliar2.addBitlessDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				viewer.menu.setVisible(true);
				if(viewer.num < dataProvider.getList().size()-1) viewer.next.setVisible(true);
				if(viewer.num != 0)viewer.prev.setVisible(true);			}
		}, MouseOverEvent.getType());
		
		viewer.auxiliar2.addBitlessDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(event.getClientX()>150){
					viewer.menu.setVisible(false);
					viewer.next.setVisible(false);
					viewer.prev.setVisible(false);
				}
			}
		}, MouseOutEvent.getType());
		
		viewer.auxiliar3.addBitlessDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				
				viewer.menu.setVisible(true);
				if(viewer.num < dataProvider.getList().size()-1) viewer.next.setVisible(true);
				if(viewer.num != 0)viewer.prev.setVisible(true);			}
		}, MouseOverEvent.getType());
		
		viewer.auxiliar3.addBitlessDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {		
				if(event.getClientX()<Window.getClientWidth()-150){	
					viewer.menu.setVisible(false);
					viewer.next.setVisible(false);
					viewer.prev.setVisible(false);
				}
			}
		}, MouseOutEvent.getType());
		
		viewer.title.setText(object.getTitle());
		viewer.title.addStyleName(object.getIcon());
		if(viewer.num.equals(viewer.list.size()-1))viewer.next.setVisible(false);
		if(viewer.num.equals(0)) viewer.prev.setVisible(false);
		viewer.show();
		idoc.getAsHTML(object, 100 , new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				viewer.container.setHTML(result);	
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("reset")
	void reset(ClickEvent event){
		editFile.setVisible(false);
		delFile.setVisible(false);
		html.setVisible(false);
		filterButton.setVisible(false);
		if(getSons().size()==1){
			dataProvider = new ListDataProvider<FileInfo>(docs.getEfiles());
			dataProvider.addDataDisplay(dataGrid);
			updateDatagridColumns();
			dataGrid.redraw();
		}
		else{
		String searchStr = docs.getDomain();
		son =false;
		enterpriseSearchBox.setText("");
		idoc.eSearchFile(docs.getFiles(),searchStr,new AsyncCallback<Vector<FileInfo>>() {
					@Override
					public void onSuccess(Vector<FileInfo> result) {
						docs.setEfiles(result);
						docs.setFilter(result);
						searchs = result;
						dataProvider = new ListDataProvider<FileInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						updateDatagridColumns();
						dataGrid.redraw();
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
		}
	}
	
	@UiHandler("upDrive")
	void upDrive(ClickEvent event){
		Dialog d = new Dialog("upload", "Subir Archivo", "Cancelar", true,
				"Subir", true,son);
		d.setBaseUrl(GWT.getModuleBaseURL());

		popup2 = new DocumentsDialog(d){ 
			@Override
			protected void onAccept() {
				hide();
				FileInfo fi = new FileInfo();
            	TextBox tb = (TextBox)grid.getWidget(1, 1);
            	fi.setTitle(tb.getText());
            	
            	
            	idoc.upload(fi,driveId, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						//TODO Actualizar datagrid!!!
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
			@Override
			protected void onCancel() {
				hide();
			}
		
		};
		popup2.setGlassEnabled(true);
		popup2.setAnimationEnabled(false);
		popup2.setModal(false);
		popup2.show();
	}
	
	private Column<FileInfo,String> getDownloadColumn() {
			Column<FileInfo,String> downloadColumn = new Column<FileInfo, String>(new ButtonCell()) {
			
			@Override
			public void render(Context context, FileInfo object,
					SafeHtmlBuilder sb) {
				if(object.getIsDrive() && object.getIsGdocs())
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-mail-save-black-white\" tabindex=\"-1\">");				
				else sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-mail-save\" tabindex=\"-1\">");
				    /*if (data != null) {
				      sb.append(data);
				    }*/
				    sb.appendHtmlConstant("</button>");				
				/*sb.appendHtmlConstant("<button id=\"downloadFile\" class=\"aon-editDataTable-button aon-icon-mail-save\" >"
				+ "</g:Button><g:Button id=\"editFile\" class=\"aon-editDataTable-button aon-icon-edit\"></g:Button>"
			+ "<g:Button id=\"removeFile\" class=\"aon-editDataTable-button aon-icon-delete\"></g:Button>");*/
			}
			
			@Override
			public String getValue(FileInfo object) {
				// The value to display in the button.
				return "";
			}

			
		};
		
		
		downloadColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

		
		downloadColumn.setFieldUpdater(new FieldUpdater<FileInfo, String>() {
			@Override
			public void update(int index, FileInfo object, String value) {
				download(object);
			}
		});
		return downloadColumn;
	}
	String sizestr;
	private String getTotalSize(){
		Integer size=0;
		for (FileInfo fi : dataProvider.getList()) {
			size = size + fi.getSize();
		} 
		//Window.alert(size.toString());
		
		/*idoc.sizeToString(size, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				sizestr = result;
			}
			@Override
			public void onFailure(Throwable caught) {}
		});*/
		return byteCountToDisplaySize(size);
	}
	private Column<FileInfo,String> getSizeColumn(ListHandler<FileInfo> sortHandler){
 
		
		Column<FileInfo, String> sizeColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				return object.getSizeStr();
			}
		};
		 sizeColumn.setSortable(true); 
		 sortHandler.setComparator(sizeColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getSize().compareTo(o2.getSize());
			}
		});
		 return sizeColumn;
	}
	
	private Column<FileInfo,String>  getTagColumn(ListHandler<FileInfo> sortHandler){
		/** Tag Column **/
		Column<FileInfo, String> tagColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				return object.getTagsStr();
			}
			
		};
		
		 tagColumn.setSortable(true); 
		 sortHandler.setComparator(tagColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				return o1.getTagsStr().compareTo(o2.getTagsStr());
			}
		});
		return tagColumn;
	}
	
	private void updateDatagridColumns(){
		dataGrid.removeColumn(6);
		dataGrid.removeColumn(5);
		dataGrid.removeColumn(4);
		
		dataGrid.getColumnSortList().push(getSizeColumn(getSortHandler()));
		dataGrid.addColumn(getSizeColumn(getSortHandler()), "Tama\u00f1o("+getTotalSize()+")");
		dataGrid.setColumnWidth(getSizeColumn(getSortHandler()), 13, Unit.PCT);
		
		dataGrid.getColumnSortList().push(getTagColumn(getSortHandler()));
		dataGrid.addColumn(getTagColumn(getSortHandler()), "Etiquetas");
		dataGrid.setColumnWidth(getTagColumn(getSortHandler()), 16, Unit.PCT);
		
		
		dataGrid.addColumn(getDownloadColumn(), "Archivo("+dataProvider.getList().size()+")");
		dataGrid.setColumnWidth(getDownloadColumn(), 10, Unit.PCT);
	}
	
	 public static String byteCountToDisplaySize(long size) {
	        String displaySize;
	        
	        final long ONE_KB = 1024;
	        final long ONE_MB = ONE_KB * ONE_KB;
	        final long ONE_GB = ONE_KB * ONE_MB;
	        
	        if (size / ONE_GB > 0) {
	            displaySize = String.valueOf(size / ONE_GB) + " GB";
	        } else if (size / ONE_MB > 0) {
	            displaySize = String.valueOf(size / ONE_MB) + " MB";
	        } else if (size / ONE_KB > 0) {
	            displaySize = String.valueOf(size / ONE_KB) + " KB";
	        } else {
	            displaySize = String.valueOf(size) + " bytes";
	        }
	        return displaySize;
	    }
	 Tag tagAux;
	 String tagname;
	 @UiHandler("tagButton")
	 void tagb(ClickEvent event){
		 epanel.setOpen(true);
		 TextBox tb = new TextBox();
		 tb.setStyleName("aon-inputText");
		 tb.addFocusListener(new FocusListener() {
				
				@Override
				public void onLostFocus(Widget sender) {
					VerticalPanel v = (VerticalPanel) epanel.getContent();
					v.remove(v.getWidgetCount() - 1);
				}
				
				@Override
				public void onFocus(Widget sender) {
					
				}
			});
		 
		 tb.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					VerticalPanel v = (VerticalPanel) epanel.getContent();
					TextBox t =  (TextBox) v.getWidget(v.getWidgetCount() - 1);
					tagname = t.getText();
					idoc.newTag(tagname, new AsyncCallback<Tag>() {
						@Override
						public void onSuccess(Tag result) {
							lists.getTagList().getList().add(result);
							VerticalPanel v = (VerticalPanel) epanel.getContent();
							tagAux = result;
							Button b =new Button(tagname); 
							b.setStyleName("aon-editDataTable-button aon-icon-tag");
							b.addDomHandler(new ContextMenuHandler() {
								Tag t=tagAux;	
								@Override
								public void onContextMenu(ContextMenuEvent event) {
									event.preventDefault();
									event.stopPropagation();
									NativeEvent nativeEvent =  event.getNativeEvent();
									TagContextMenu tcm = new TagContextMenu(t);
									tcm.setPopupPosition(nativeEvent.getClientX(),
												nativeEvent.getClientY());
									tcm.show();
								}
							}, ContextMenuEvent.getType());
							b.addClickHandler(new ClickHandler() {
								String s=tagname;	
								@Override
									public void onClick(ClickEvent event) {
										editFile.setVisible(false);
										delFile.setVisible(false);
										SearchInfo si = new SearchInfo();
										Vector<String> v = new Vector<String>();
										v.add(s);
										si.setTag(v);
										idoc.searchFile2(si, docs.getEfiles(),
												new AsyncCallback<FilterUtil>() {
													@Override
													public void onSuccess(FilterUtil result) {
														searchs = result.getFiles();
														docs.setFilter(result.getFiles());
														dataProvider = new ListDataProvider<FileInfo>(
																searchs);
														dataProvider.addDataDisplay(dataGrid);
														isServiconvenios=false;
														/*filterLabel= new Label();
														filterLabel.setStyleName("aon-icon-tag");
														filterLabel.setText(tAux.getName());
														*/
														html.setText(result.getTag());
														html.setVisible(true);
														
														filterButton.setVisible(true);
														
														updateDatagridColumns();
														dataGrid.redraw();
													}
													@Override
													public void onFailure(Throwable caught) {
													}
												});
									}
								});
							v.remove(v.getWidgetCount() - 1);
							v.add(b);
						}
						@Override
						public void onFailure(Throwable caught) {}
					});

				}
			}
		});
		 
		 VerticalPanel vp = (VerticalPanel) epanel.getContent();
		 vp.add(tb);
		 tb.getElement().focus();
		 
	 }
	 Category catAux;
	 String catname;
	 @UiHandler("catButton")
	 void categoryb(ClickEvent event){
		 dpanel.setOpen(true);
		 TextBox tb = new TextBox();
		 tb.setStyleName("aon-inputText");
		 tb.addFocusListener(new FocusListener() {
			
			@Override
			public void onLostFocus(Widget sender) {
				VerticalPanel v = (VerticalPanel) dpanel.getContent();
				v.remove(v.getWidgetCount() - 1);
			}
			
			@Override
			public void onFocus(Widget sender) {
				
			}
		});
		 tb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					VerticalPanel v = (VerticalPanel) dpanel.getContent();
					TextBox t =  (TextBox) v.getWidget(v.getWidgetCount() - 1);
					catname = t.getText();
					idoc.newCategory(catname, new AsyncCallback<Category>() {
						@Override
						public void onSuccess(Category result) {
							lists.getCategoryList().getList().add(result);
							VerticalPanel v = (VerticalPanel) dpanel.getContent();
							catAux = result;
							Button b =new Button(catname); 
							b.setStyleName("aon-editDataTable-button aon-icon-category");
							b.addDomHandler(new ContextMenuHandler() {
								Category c = catAux;
								@Override
								public void onContextMenu(ContextMenuEvent event) {
									event.preventDefault();
									event.stopPropagation();
									NativeEvent nativeEvent = event.getNativeEvent();
									CategoryContextMenu ccm = new CategoryContextMenu(c);
									ccm.setPopupPosition(nativeEvent.getClientX(),
											nativeEvent.getClientY());
									ccm.show();
								}
							}, ContextMenuEvent.getType());
							b.addClickHandler(new ClickHandler() {
									String s = catname;
									@Override
									public void onClick(ClickEvent event) {
										editFile.setVisible(false);
										delFile.setVisible(false);
										SearchInfo si = new SearchInfo();
										si.setCategory(s);
										idoc.searchFile2(si, docs.getEfiles(),
												new AsyncCallback<FilterUtil>() {
													@Override
													public void onSuccess(FilterUtil result) {
														searchs = result.getFiles();
														docs.setFilter(result.getFiles());
														dataProvider = new ListDataProvider<FileInfo>(
																searchs);
														dataProvider.addDataDisplay(dataGrid);
														isServiconvenios=false;
														/*filterLabel= new Label();
														filterLabel.setStyleName("aon-icon-category");
														filterLabel.setText(cAux.getName());
														*/

														html.setText(result.getCategory());
														html.setVisible(true);
														filterButton.setVisible(true);
														updateDatagridColumns();
														dataGrid.redraw();
													}
													@Override
													public void onFailure(Throwable caught) {
													}
												});
									}
								});
							v.remove(v.getWidgetCount() - 1);
							v.add(b);
						}
						@Override
						public void onFailure(Throwable caught) {}
					});

				}
			}
		});
		 VerticalPanel vp = (VerticalPanel) dpanel.getContent();
		 vp.add(tb);
		 tb.getElement().focus();

		 //dpanel.setContent(vp);
		 
	 }
	 String oldName;
	 String newName;
	 private void editTag(Tag tag) {
		 Dialog d = new Dialog("edit2", "Editar Etiqueta", "Cancelar", true, "Editar", true,false);
		 d.setTag(tag);
		 oldName = tag.getName();
		 DocumentsDialog popup = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				TextBox tb = (TextBox)grid.getWidget(0, 1);
				tag.setName(tb.getText());
				newName= tb.getText();
				idoc.editTag(tag.getName(),tag.getId(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							VerticalPanel v = (VerticalPanel) epanel.getContent();
							for (int i = 0; i<v.getWidgetCount() ; i++) {
								Button b = (Button) v.getWidget(i);
								if(b.getText().equals(oldName)){
									b.setText(newName);
								}
							}
							for (FileInfo f : docs.getEfiles()) {
								String s="";
								Integer size = f.getTags().size();
								for(Tag t : f.getTags()){
									if(size<=1 && t.getName().equals(oldName)){
										t.setName(newName);
										f.setTagsStr(newName);
									}
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}
									else{
										t.setName(newName);
										s = s + newName+", ";
									}
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));
							}
							for (FileInfo f : docs.getFiles()) {
								String s="";
								Integer size = f.getTags().size();
								for(Tag t : f.getTags()){
									if(size<=1 && t.getName().equals(oldName)){
										t.setName(newName);
										f.setTagsStr(newName);
									}
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}
									else{
										t.setName(newName);
										f.getTags().add(tag);										s = s + newName+", ";
									}
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));
							}
							for (FileInfo f : docs.getFilter()) {
								String s="";
								Integer size = f.getTags().size();
								for(Tag t : f.getTags()){
									if(size<=1 && t.getName().equals(oldName)){
										t.setName(newName);
										f.setTagsStr(newName);
									}
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}
									else{
										t.setName(newName);
										s = s + newName+", ";
									}
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));
							}
							dataProvider = new ListDataProvider<FileInfo>(docs.getFilter());
							dataProvider.addDataDisplay(dataGrid);
							/*for (FileInfo f : dataProvider.getList()) {
								String s="";
								for(Tag t : f.getTags()){
									if(f.getTags().size()<=1 && t.getName().equals(oldName))
										f.setTagsStr(newName);
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}
									else{
										t.setName(newName);
										s = s + newName+", ";
									}
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));							
							}*/
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
	 
	 private void removeTag(Tag tag) {
		 Dialog d = new Dialog("delete2", "Borrar Etiqueta", "Cancelar", true, "Borrar", true,false);
		 d.setTag(tag);
		 oldName = tag.getName();

		 DocumentsDialog popup = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				
				Vector<Tag> v = new Vector<Tag>();
				for(Integer i = 0; i< lists.getTagList().getList().size();i++){
					if(!lists.getTagList().getList().get(i).getName().equals(oldName))
						v.add(lists.getTagList().getList().get(i));
				}
				TagList tagList = new TagList();
				tagList.setList(v);
				lists.setTagList(tagList);

				v = new Vector<Tag>();
				for(Integer i = 0; i< lists.getTagListSon().getList().size();i++){
					if(!lists.getTagListSon().getList().get(i).getName().equals(oldName))
						v.add(lists.getTagListSon().getList().get(i));
				}
				tagList = new TagList();
				tagList.setList(v);
				lists.setTagListSon(tagList);
				
				idoc.deleteTag(tag.getId(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							
							VerticalPanel v = (VerticalPanel) epanel.getContent();
							for (int i = 0; i<v.getWidgetCount() ; i++) {
								Button b = (Button) v.getWidget(i);
								if(b.getText().equals(oldName)){
									v.remove(i);
								}
							}
							for (FileInfo f : docs.getFiles()) {
								String s="";
								Integer size = f.getTags().size();
								for(Tag t : f.getTags()){
									if(size<=1 && t.getName().equals(oldName))
										f.setTagsStr("-");
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}	
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));
							}
							for (FileInfo f : docs.getEfiles()) {
								String s="";
								Integer size = f.getTags().size();
								for(Tag t : f.getTags()){
									if(size<=1 && t.getName().equals(oldName))
										f.setTagsStr("-");
									
									else if(!t.getName().equals(oldName))
										s = s+t.getName()+", ";
										
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));
							}
							
							for (FileInfo f : dataProvider.getList()) {
								String s="";
								for(Tag t : f.getTags()){
									if(f.getTags().size()<=1 && t.getName().equals(oldName))
										f.setTagsStr("-");
									else if(!t.getName().equals(oldName)){
										s = s+t.getName()+", ";
									}	
								}
								if(!s.equals("")) f.setTagsStr(s.substring(0,s.length()-2 ));							
							}
							
							
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
	 
	 private void editCategory(Category category) {
		
		 Dialog d = new Dialog("edit2", "Editar Categor�a", "Cancelar", true, "Editar", true,false);
		 d.setCat(category);
		 oldName = category.getName();
		 DocumentsDialog popup = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				TextBox tb = (TextBox)grid.getWidget(0, 1);
				cat.setName(tb.getText());
				newName= tb.getText();
				idoc.editCategory(cat.getName(),cat.getId(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							VerticalPanel v = (VerticalPanel) dpanel.getContent();
							for (int i = 0; i<v.getWidgetCount() ; i++) {
								Button b = (Button) v.getWidget(i);
								if(b.getText().equals(oldName)){
									b.setText(newName);
								}
							}
							for (FileInfo f : docs.getFiles()) {
								if(f.getCategoryStr().equals(oldName))
									f.setCategoryStr(newName);
							}
							for (FileInfo f : dataProvider.getList()) {
								if(f.getCategoryStr().equals(oldName))
									f.setCategoryStr(newName);
							}
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
	 Vector<Category> catList = new Vector<Category>();
	 private void removeCategory(Category category) {
		 catList = lists.getCategoryList().getList();
		 Dialog d = new Dialog("delete2", "Borrar Etiqueta", "Cancelar", true, "Borrar", true,false);
		 d.setCat(category);
		 oldName = category.getName();

		 DocumentsDialog popup = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				Vector<Category> v = new Vector<Category>();
				for(Integer i = 0; i< lists.getCategoryList().getList().size();i++){
					if(!lists.getCategoryList().getList().get(i).getName().equals(oldName))
						v.add(lists.getCategoryList().getList().get(i));
				}
				CategoryList categoryList = new CategoryList();
				categoryList.setList(v);
				lists.setCategoryList(categoryList);

				v = new Vector<Category>();
				for(Integer i = 0; i< lists.getCategoryListSon().getList().size();i++){
					if(!lists.getCategoryListSon().getList().get(i).getName().equals(oldName))
						v.add(lists.getCategoryListSon().getList().get(i));
				}
				categoryList = new CategoryList();
				categoryList.setList(v);
				lists.setCategoryListSon(categoryList);

				 idoc.deleteCategory(cat.getId(),new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							
							VerticalPanel v = (VerticalPanel) dpanel.getContent();
							for (int i = 0; i<v.getWidgetCount() ; i++) {
								Button b = (Button) v.getWidget(i);
								if(b.getText().equals(oldName)){
									v.remove(i);
								}
							}
							for (FileInfo f : docs.getFiles()) {
								if(f.getCategoryStr().equals(oldName))
									f.setCategoryStr("-");
							}
							for (FileInfo f : dataProvider.getList()) {
								if(f.getCategoryStr().equals(oldName))
									f.setCategoryStr("-");
							}

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
}
