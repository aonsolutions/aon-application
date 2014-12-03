package com.esferalia.aon.gwt.document.client;

import gwtupload.client.SingleUploader;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.calendar.View;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.Dialog;
import com.esferalia.aon.gwt.document.shared.DisclosureImages;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
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

		public DocumentContextMenu() {
			
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
	
	DocumentContextMenu contextMenu = new DocumentContextMenu();
	
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

	@UiField
	InlineHTML html;
	
	@UiField
	Button filterButton;
	
	@UiField
	Button advanceSearch;

	@UiField(provided=true)
	DisclosurePanel epanel;

	@UiField(provided=true)
	ScrollPanel treepanel;

	@UiField(provided=true)
	DisclosurePanel dpanel;

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	StackLayoutPanel stack1;

	@UiField
	Button allButton;

	@UiField
	Button serviConveniosButton;

	@UiField							
	Button newFile;

	@UiField
	Button editFile;
	
	@UiField
	Button delFile;
	
	@UiField(provided = true)
	DataGrid<FileInfo> dataGrid;

	@UiField(provided = true)
	TextBox searchBox;
	
	@UiField(provided = true)
	SuggestBox enterpriseSearchBox;
	
	@UiField
	Button searchButton;
	
	@UiField
	Button eSearchButton;

	Boolean gConnection;
	
	private void init() {
		getSons();
		
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
	public void onModuleLoad() {
		stack1 = new StackLayoutPanel(Unit.EM);
		treepanel = new ScrollPanel();
		//DisclosureImages di = new DisclosureImages();
		//dpanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Categor\u00edas");
		//epanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Etiquetas");
		
			
			

	}
	Category cAux;
	Tag tAux;
	public void Load() {
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
			b.addClickHandler(new ClickHandler() {
					Category c = cAux;
					@Override
					public void onClick(ClickEvent event) {
						SearchInfo si = new SearchInfo();
						si.setCategory(c.getName());
						idoc.searchFile2(si, docs.getEfiles(),
								new AsyncCallback<FilterUtil>() {
									@Override
									public void onSuccess(FilterUtil result) {
										// TODO AÑADIR IU FILTER
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
										Window.alert(result.getCategory());

										html.setText(result.getCategory());
										html.setVisible(true);
										filterButton.setVisible(true);
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
		dpanel.add(vcat);
		
		VerticalPanel vtag =  new VerticalPanel();
		for(Tag t : lists.getTagList().getList()){
			tAux=t;
			Button b =new Button(t.getName()); 
			b.setStyleName("aon-editDataTable-button aon-icon-tag");
			b.addClickHandler(new ClickHandler() {
				Tag t=tAux;	
				@Override
					public void onClick(ClickEvent event) {
						SearchInfo si = new SearchInfo();
						Vector<String> v = new Vector<String>();
						v.add(t.getName());
						si.setTag(v);
						idoc.searchFile2(si, docs.getEfiles(),
								new AsyncCallback<FilterUtil>() {
									@Override
									public void onSuccess(FilterUtil result) {
										// TODO AÑADIR IU FILTER
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
										Window.alert(result.getTag());
										html.setText(result.getTag());
										html.setVisible(true);
										
										filterButton.setVisible(true);

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
		epanel.add(vtag);
		
		searchBox = new TextBox();
		searchBox.addBitlessDomHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				searchButton.click();
			}
		}, ChangeEvent.getType());
		
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					event.preventDefault();
					if(KeyCodes.KEY_DELETE != event.getNativeEvent().getKeyCode() && KeyCodes.KEY_BACKSPACE != event.getNativeEvent().getKeyCode()){
						char c = (char) event.getNativeEvent().getCharCode();
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
					}
					searchButton.click();
				}
			}
		});
		
		enterpriseSearchBox = new SuggestBox(Utils.createOracle(getSons()));
		if (getSons().size() == 1) enterpriseSearchBox.setEnabled(false);
		enterpriseSearchBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				eSearchButton.click();
			}
		});

		DefaultKeyboardSelectionHandler<FileInfo> selHandler = new DefaultKeyboardSelectionHandler<FileInfo>(dataGrid){
		
			@Override
			public void onCellPreview(CellPreviewEvent<FileInfo> event) {
				 
				
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
   			        Integer subrow = event.getContext().getSubIndex();
   			        dataGrid.setKeyboardSelectedRow(relRow, subrow, true);

   					NativeEvent nativeEvent = event.getNativeEvent();
   					
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

 		
		dataGrid.addBitlessDomHandler(new DragOverHandler() {
			
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
				*/
				
			}
		}, DropEvent.getType());
		
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
	
	MultiWordSuggestOracle oracleSons;
	MultiWordSuggestOracle createOracle(Vector<String> l) {
		oracleSons = new MultiWordSuggestOracle();

		for (String s : l) {
			oracleSons.add(s);
		}
		return oracleSons;
	}

	private Boolean esta(String s) {
		if(s.equals(""))return true;
		for (String string : getSons()) {
			if(s.equals(string)){
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
		Dialog d = new Dialog("edit", "Editar Archivo", "Cancelar", true, "Editar", true);
		d.setLists(lists);
		d.setFileInfo(fi);

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
            	if(!db.getTextBox().getText().equals(""))
            		fileInfo.setDate(db.getValue());
            	
            	// Categoria - Category
            	ListBox lb1 = (ListBox)grid.getWidget(4, 1);
            	for (Category c : lists.getCategoryList().getList()) {
            		
            		String s1 = lb1.getItemText(lb1.getSelectedIndex());
            		if(s1.substring(0, 1).equals(Character.toString((char)9650)) || s1.substring(0, 1).equals(Character.toString((char)9660)) )
            			s1 = s1.substring(1);
            		if(c.getName().equals(s1)){
						fileInfo.setCategory(c.getId());
					}
				}
            	
            	// Ambito - Scope
            	ListBox lb3 = (ListBox)grid.getWidget(6, 1);
            	for (Scope s : lists.getScopeList().getList()) {
            		String s3 = lb3.getItemText(lb1.getSelectedIndex());
            		if(s3.substring(0, 1).equals(Character.toString((char)9650)) || s3.substring(0, 1).equals(Character.toString((char)9660)) )
            			s3 = s3.substring(1);
					if(s.getName().equals(s3)){
						fileInfo.setScope(s);
					}
				}	            	
            	
            	// Etiquetas - Tags
            	Vector<Tag> tags = new Vector<Tag>();
            	VerticalPanel vp = (VerticalPanel)grid.getWidget(5, 1);
            	for(Integer i = 0 ;i<vp.getWidgetCount();i++){
            		HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            		ListBox lb = (ListBox)hp.getWidget(0);
            		for (Tag t : lists.getTagList().getList()) {
                		String s2 = lb.getItemText(lb.getSelectedIndex());
                		if(s2.substring(0, 1).equals(Character.toString((char)9650)) || s2.substring(0, 1).equals(Character.toString((char)9660)) )
                			s2 = s2.substring(1);
						if(t.getName().equals(s2)){
							fileInfo.setTags(tags);
						}
					}
            	}
            	
            	idoc.editFile(fileInfo,new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
				hide();
				vertical = new VerticalPanel();
				Vector<FileInfo> aux = new Vector<FileInfo>();
				for (FileInfo f : docs.getFiles()) {
					if(f.getFileId() == fileInfo.getFileId())
						aux.add(fileInfo);
					else aux.add(f);		
				}
				docs.setFiles(aux);
				aux = new Vector<FileInfo>();
				for (FileInfo f : docs.getEfiles()) {
					if(f.getFileId() == fileInfo.getFileId())
						aux.add(fileInfo);
					else aux.add(f);		
				}
				docs.setEfiles(aux);
				aux = new Vector<FileInfo>();
				for (FileInfo f : docs.getFilter()) {
					if(f.getFileId() == fileInfo.getFileId())
						aux.add(fileInfo);
					else aux.add(f);		
				}
				docs.setFilter(aux);
				aux= new Vector<FileInfo>();
				for (FileInfo f : dataProvider.getList()) {
					if(f.getFileId() == fileInfo.getFileId()){
					aux.add(fileInfo);
					}
					else{
						aux.add(f);
					}
				}
				dataProvider = new ListDataProvider<FileInfo>(aux);
				dataProvider.addDataDisplay(dataGrid); 
				dataGrid.redraw();
			}
			@Override
			protected void onCancel() {
				hide();
				vertical = new VerticalPanel();				
			}
		};
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
		Dialog d = new Dialog("delete", "Borrar Archivo", "Cancelar", true, "Borrar", true);
		d.setFileInfo(fi);
		
		popup2 = new DocumentsDialog(d) {
			
			@Override
			protected void onCancel() {
				hide();				
			}
			
			@Override
			protected void onAccept() {
				hide();
				idoc.removeFile(getFileInfo(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
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
						dataGrid.redraw();
					}
				} );
			}
		};
		popup2.setGlassEnabled(true);
		popup2.show();
	
	}
	
	
	public Vector<String> getSons() {
		if(sons==null){
		idoc.getSons(new AsyncCallback<Vector<String>>() {
			
			@Override
			public void onSuccess(Vector<String> result) {
				sons= result;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		}
		return sons;
	}
	
	
	
	Vector<String> sons = null;
	FileUpload fuchange;
	SingleUploader fuchange2;
	
	@UiHandler("newFile")
	void XXXXXX(ClickEvent event) {
		newFile(null);
	}
	
	FileInfo finsert;
	DocumentsDialog popup2;
	
	private void newFile(SingleUploader up) {	
		Dialog d = new Dialog("new","Nuevo Archivo","Cancelar",true,"Guardar",true);
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
    						s2= s2.substring(0);
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
            		else fi.setDomain(sb.getText());
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
									if(result.getDomain().equals(docs.getFilter().get(0).getDomain())){
										aux = new Vector<FileInfo>();
										for (FileInfo f : docs.getEfiles()) {
											aux.add(f);		
										}
										aux.add(result);
										docs.setEfiles(aux);

										
										if(html.isVisible()){
											Integer i = 0;
											Window.alert(html.getText());
											Window.alert(result.getTags().get(i).getName());
											
											while(i<result.getTags().size() && !html.getText().equals(result.getTags().get(i).getName())){
												i++;
											}
											Window.alert("asdas");
											if(!html.isVisible() || (html.isVisible() && (html.getText().equals(result.getCategoryStr()) || result.getTags().size()>i))){
												Window.alert("entraHTML");
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
		popup2.setGlassEnabled(true);
		popup2.show();
	}
	
	Lists lists = new Lists();
	FlexTable grid;
	HorizontalPanel h2;
	
	@UiHandler("advanceSearch")
	void advancedSearch(ClickEvent event) {
		search();
//		html.setVisible(false);
//		filterButton.setVisible(false);
	}

	VerticalPanel vertical = new VerticalPanel();
	Vector<FileInfo> searchs;

	@UiHandler("searchButton")
	void XXXXX(ClickEvent event) {
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
						dataGrid.redraw();
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}
	
	
	@UiHandler("eSearchButton")
	void sbutton(ClickEvent event) {
		html.setVisible(false);
		filterButton.setVisible(false);
		String searchStr = enterpriseSearchBox.getText();
		
		idoc.eSearchFile(docs.getFiles(),searchStr,
				new AsyncCallback<Vector<FileInfo>>() {

					@Override
					public void onSuccess(Vector<FileInfo> result) {
						docs.setEfiles(result);
						docs.setFilter(result);
						searchs = result;
						dataProvider = new ListDataProvider<FileInfo>(result);
						dataProvider.addDataDisplay(dataGrid);
						dataGrid.redraw();
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}
	

	@UiHandler("allButton")
	void getAllAttach(ClickEvent event) {
		html.setVisible(false);
		filterButton.setVisible(false);
		if(docs.getEfiles()==null){
		idoc.getAllFiles(new AsyncCallback<Document>() {

			@Override
			public void onSuccess(Document result) {
				docs = result;
				addDataDisplay(dataGrid);
				isServiconvenios=false;
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
			dataGrid.redraw();
		}
		docs.setFilter(docs.getEfiles());

	}

	Boolean isServiconvenios=false;
	@UiHandler("serviConveniosButton")
	void getServiConveniosAttach(ClickEvent event) {
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
				sb.appendHtmlConstant("<g:Button class=\"aon-editDataTable-button "
						+ object.getIcon() + "\" >"+"&nbsp;&nbsp;"+object.getTitle());
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
		
		/** Size Column **/
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

		dataGrid.getColumnSortList().push(sizeColumn);
		dataGrid.addColumn(sizeColumn, "Tama\u00f1o");

		dataGrid.setColumnWidth(sizeColumn, 10, Unit.PCT);

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

		dataGrid.getColumnSortList().push(tagColumn);
		dataGrid.addColumn(tagColumn, "Etiquetas");
		
		
		
		dataGrid.setColumnWidth(tagColumn, 16, Unit.PCT);

		/** Download Column **/
		
		Column<FileInfo,String> downloadColumn = new Column<FileInfo, String>(new ButtonCell()) {
			
			@Override
			public void render(Context context, FileInfo object,
					SafeHtmlBuilder sb) {
				   sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-mail-save\" tabindex=\"-1\">");
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
		
		dataGrid.addColumn(downloadColumn, "Archivo");
		dataGrid.setColumnWidth(downloadColumn, 10, Unit.PCT);
		
	}
	Vector<TreeDriveInfo> vtree;
	
	private void download(FileInfo object){
		String driveId="";
		if(object.getDriveId()!=null)driveId= object.getDriveId();
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download/"
                + "?file_id=" + Integer.toString(object.getFileId())
                + "&drive_id=" +URL.encode(driveId)
                + "&mimetype=" +object.getMimetype();
        Window.open( fileDownloadURL, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");
	}
	
	private void search() {
		Dialog d = new Dialog("search", "Busqueda Avanzada", "Cancelar", true,
				"Buscar", true);
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
						si.setDomain(tb0.getText());
				}else si.setDomain(null);
				
				TextBox tb1 = (TextBox) grid.getWidget(1, 1);
				if ("".equals(tb1.getText()))
					si.setName(null);
				else
					si.setName(tb1.getText());

				CheckBox cb = (CheckBox) grid.getWidget(2, 1);
				;
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
								if(!docs.getEfiles().get(0).getDomain().equals(result.get(0).getDomain())){
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
				"Compartir", true);
		dId = object.getDriveId();
		popup2 = new DocumentsDialog(d) {
			@Override
			protected void onAccept() {
				TextBox tb = (TextBox) grid.getWidget(0, 1);
				// TODO CHECK EMAIL
				hide();
				idoc.share(tb.getText(), dId, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
					}

					@Override
					public void onFailure(Throwable caught) {
					}
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
				"Salir", true);
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
				treepanel.add(tree);
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
		dataGrid.redraw();
	}
	Viewer viewer;
	
	private  void getAsHTMl(FileInfo object, Integer num) {
		viewer = new Viewer(dataProvider.getList(),num,object){
			@Override
			protected void onDownload() {
				download(fileInfo);
			}
			@Override
			protected void onPrint() {	
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
		idoc.getAsHTML(object, 100 , new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				viewer.container.setHTML(result);
				if(viewer.num.equals(viewer.list.size()-1))viewer.next.setVisible(false);
				if(viewer.num.equals(0)) viewer.prev.setVisible(false);
				viewer.show();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
}
