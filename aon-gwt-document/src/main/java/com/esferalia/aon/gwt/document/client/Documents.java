package com.esferalia.aon.gwt.document.client;

import gwtupload.client.DecoratedFileUpload.FileUploadWithMouseEvents;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.DisclosureImages;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;


public class Documents extends Composite implements EntryPoint {

	private void initContextMenu() {

		class DocumentContextMenu extends ContextMenu {

			ScheduledCommand newCommand = new ScheduledCommand() {
				public void execute() {

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

				};
			};
			
			ScheduledCommand shareCommand = new ScheduledCommand() {
				public void execute() {

				};
			};
			
			ScheduledCommand infoCommand = new ScheduledCommand() {
				public void execute() {

				};
			};

			private MenuItem editItem;
			private MenuItem removeItem;

			public DocumentContextMenu() {
				
				editItem = addItem("Editar", editCommand,
						AON.AON_ICON_EDIT_ADD, AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);

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
		;

		final DocumentContextMenu contextMenu = new DocumentContextMenu();

		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				// stop the browser from opening the context menu
				event.preventDefault();
				event.stopPropagation();

				NativeEvent nativeEvent = event.getNativeEvent();
				contextMenu.setPopupPosition(nativeEvent.getClientX(),
						nativeEvent.getClientY());
				contextMenu.show();
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
	
	@UiField
	Button searchButton;

	Boolean gConnection;
	
	private void init() {
		
		
		
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

		if(docs.getFiles()==null||docs.getFiles().isEmpty()){
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
						idoc.searchFile(si, docs.getFiles(),
								new AsyncCallback<Vector<FileInfo>>() {
									@Override
									public void onSuccess(Vector<FileInfo> result) {
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
						idoc.searchFile(si, docs.getFiles(),
								new AsyncCallback<Vector<FileInfo>>() {
									@Override
									public void onSuccess(Vector<FileInfo> result) {
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

		dataGrid = new DataGrid<FileInfo>(Integer.MAX_VALUE, resources,
				FileInfo.PROVIDES_KEY);
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
		dataGrid.setEmptyTableWidget(new Label("No hay ningún archivo."));
		
		addDataDisplay(dataGrid);
		
		ListHandler<FileInfo> sortHandler = getSortHandler();
				//docs.getFiles());
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
 					Window.alert(aux.get(aux.size()-1-i).getTitle()+" - "+ aux2.get(i).getTitle() );
 				} 				
				dataProvider.setList(aux2);
				
			}

		};
	}

	private ListDataProvider<FileInfo> dataProvider = new ListDataProvider<FileInfo>();

	public void addDataDisplay(HasData<FileInfo> display) {
		dataProvider = new ListDataProvider<FileInfo>(docs.getFiles());
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
		
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists.getScopeList().getList()) {
			lb3.addItem(s.getName());
		}
		for (Tag t : lists.getTagList().getList()) {
			lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler2());
		
		popup = new DialogBox();
		//popup.getCaption().setText("EDITAR ARCHIVO");
		popup.getCaption().setHTML("<div id=\"aonContent:corporateIdentityAttachForm:j_id3147\" class=\"aon-popupWindow-title\"><span class=\"aon-outputText\">EDITAR ARCHIVO</span></div>");

		VerticalPanel v = new VerticalPanel();
		v.setSpacing(6);
		
		final SingleUploader upload = newUploader(up);
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	
			@Override
			public void onCancel(IUploader uploader) {
				final SingleUploader upload3 = newUploader(null) ;
				grid.setWidget(1, 1, upload3);
			}
		});
	    		
		grid = new FlexTable();
		
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);

		TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
		tb1.setText(fi.getTitle());
		grid.setWidget(0, 1, tb1);

		grid.setWidget(1, 0, new Label("Archivo"));
		grid.setWidget(1, 1, upload);
		
		CheckBox checkBox = new CheckBox();
		grid.setWidget(2, 0, new Label("Confidencial"));
		checkBox.setValue(fi.getConfidential());
		grid.setWidget(2, 1, checkBox);
		
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		dateBox.setValue(fi.getDate());
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		for (int i = 0; i<lb1.getItemCount();i++) {
			if(lb1.getItemText(i).equals(fi.getCategoryStr())){
				lb1.setItemSelected(i, true);
			}
		}
		grid.setWidget(4, 1, lb1);

		if(lb2.getItemCount() <= 2){
			grid.setWidget(5, 0, new Label("Etiqueta"));
			for (int i = 0; i<lb2.getItemCount();i++) {
				if(lb2.getItemText(i).equals(fi.getTagsStr())){
					lb2.setItemSelected(i, true);
				}
			}
			grid.setWidget(5, 1, lb2);	
		}
		else{
			Integer size = fi.getTags().size();
			
			if(size>1){
				ListBox[] lbs = new ListBox[size];
				for(Integer k = 0; k< size ; k++){
					lbs[k] = new ListBox();
					lbs[k].addItem("-");
					for (Tag t : lists.getTagList().getList()) {
						lbs[k].addItem(t.getName());
					}
					HorizontalPanel hp = new HorizontalPanel();
					for (int i = 0; i<lbs[k].getItemCount();i++) {
						if(lbs[k].getItemText(i).equals(fi.getTags().get(k).getName())){
							lbs[k].setItemSelected(i, true);
						}
					}
					
					
					if(k== size-1){
						hp.add(lbs[k]);
						Button bMenos = new Button();
						bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
						bMenos.addClickHandler(menosHandler2());
						hp.add(bMenos);
						if(size < lists.getTagList().getList().size()){
							Button mas = new Button("");
							mas.setStyleName("aon-finding-toolbar-item aon-search-add");
							mas.addClickHandler(masHandler2());
							hp.add(mas);
						}
					}
					else{
						lbs[k].setEnabled(false);
						hp.add(lbs[k]);
					}
					hp.addStyleName("aon-gwt-tags-popup");

					vertical.add(hp);
					
				}

			}
			else{
				h2 = new HorizontalPanel();
				for (int i = 0; i<lb2.getItemCount();i++) {
					if(lb2.getItemText(i).equals(fi.getTagsStr())){
						lb2.setItemSelected(i, true);
					}
				}
				h2.add(lb2);
				if(lb2.getSelectedIndex()!=0){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler2());
					h2.add(mas);
				}
				vertical.add(h2);
			}
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vertical);
		}
		
		grid.setWidget(6, 0, new Label("\u00c1mbito"));
		if(fi.getScope()!=null){
		for (int i = 0; i<lb3.getItemCount();i++) {
			if(lb3.getItemText(i).equals(fi.getScope().getName())){
				lb3.setItemSelected(i, true);
			}
		}
		}
		grid.setWidget(6, 1, lb3);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
		v.add(grid);
		Label l = new Label("");
		v.add(l);
		HorizontalPanel h = new HorizontalPanel();

		Button b = new Button("CANCELAR");
		
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
				vertical = new VerticalPanel();
			}
		});
		
		Button b2 = new Button("EDITAR");
		b2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
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
					if(c.getName().equals(lb1.getItemText(lb1.getSelectedIndex()))){
						fileInfo.setCategory(c.getId());
					}
				}
            	
            	// Ambito - Scope
            	ListBox lb3 = (ListBox)grid.getWidget(6, 1);
            	for (Scope s : lists.getScopeList().getList()) {
					if(s.getName().equals(lb3.getItemText(lb3.getSelectedIndex()))){
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
						if(t.getName().equals(lb.getValue(lb.getSelectedIndex()))){
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
				popup.hide();
				vertical = new VerticalPanel();
				Vector<FileInfo> aux = new Vector<FileInfo>();
				for (FileInfo f : docs.getFiles()) {
					if(f.getFileId() == fileInfo.getFileId())
						aux.add(fileInfo);
					else aux.add(f);		
				}
				docs.setFiles(aux);
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
		});
		b.setStyleName("aon-commandButton");
		b2.setStyleName("aon-commandButton");
		h.add(b);
		h.add(b2);
		v.add(h);
		v.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_RIGHT);
		popup.setGlassEnabled(true);
		popup.add(v);

		/*
		 * HorizontalPanel hp = new HorizontalPanel(); Button b = new
		 * Button("Cancelar"); Button b2 = new Button("Guardar");
		 * hp.add(b);hp.add(b2); popup.add(hp);
		 */
		popup.show();
	}
	
	
	@UiHandler("delFile")
	void del(ClickEvent event){
		removeFile();
	}
	
	public void removeFile(){
		Integer n =dataGrid.getKeyboardSelectedRow();
		FileInfo fi = dataProvider.getList().get(n);
		Boolean bool = Window.confirm("Estas seguro de eliminar el archivo "+fi.getTitle());
		if(bool){
			idoc.removeFile(fi, new AsyncCallback<Void>() {
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
	
	DialogBox popup;
	
	Vector<String> sons = null;
	FileUpload fuchange;
	SingleUploader fuchange2;
	@UiHandler("newFile")
	void XXXXXX(ClickEvent event) {
		newFile2(null);
	}
	
	long progress = 10;
	FileInfo finsert;
	private void newFile2(SingleUploader up) {
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists.getScopeList().getList()) {
			lb3.addItem(s.getName());
		}
		for (Tag t : lists.getTagList().getList()) {
			lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler2());
		
		popup = new DialogBox();
		//popup.getCaption().setText("NUEVO ARCHIVO");
		popup.getCaption().setHTML("<div  class=\"aon-popupWindow-title\"><span class=\"aon-outputText\">NUEVO ARCHIVO</span></div>");
		VerticalPanel v = new VerticalPanel();
		v.setSpacing(6);
		
		final SingleUploader upload = newUploader(up);
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	
			@Override
			public void onCancel(IUploader uploader) {
				final SingleUploader upload3 = newUploader(null) ;
				grid.setWidget(1, 1, upload3);
			}
		});
		
		grid = new FlexTable();

		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);

		final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		tb1.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				tb1.setStyleName("aon-inputText");
			}
		});
		grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(0, 1, tb1);

		grid.setWidget(1, 0, new Label("Archivo"));
		grid.setWidget(1, 1, upload);
		
		CheckBox checkBox = new CheckBox();
		grid.setWidget(2, 0, new Label("Confidencial"));
		grid.setWidget(2, 1, checkBox);
		
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		grid.setWidget(4, 1, lb1);

		if(lb2.getItemCount() <= 2){
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, lb2);	
		}
		else{
			
			h2 = new HorizontalPanel();
			
			h2.add(lb2);
		
			vertical.add(h2);
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vertical);
			
		}
		
		grid.setWidget(6, 0, new Label("\u00c1mbito"));
		grid.setWidget(6, 1, lb3);
		
		if(getSons().size() != 0){
			final SuggestBox sb = new SuggestBox(createOracle(getSons()));
			sb.setStyleName("aon-inputText");
			sb.addBitlessDomHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					sb.setStyleName("aon-inputText");
				}
			},ChangeEvent.getType());
			grid.setWidget(7, 0, new Label("Empresas"));
			grid.setWidget(7, 1, sb);
		}
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
		v.add(grid);
		Label l = new Label("");
		v.add(l);
		HorizontalPanel h = new HorizontalPanel();

		Button b = new Button("CANCELAR");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
				vertical = new VerticalPanel();
			}
		});
		
		Button b2 = new Button("GUARDAR");
		b2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//upload.getForm().submit();
				
				FileInfo fi = new  FileInfo();

            	// Descripción - Description
            	TextBox tb = (TextBox)grid.getWidget(0, 1);
            	
            	fi.setTitle(tb.getText());
            	
            	// Confidencial - Confidential
            	CheckBox cb = (CheckBox)grid.getWidget(2, 1);
            	fi.setConfidential(cb.getValue());
            	            	
            	// Fecha - Date
            	DateBox db = (DateBox)grid.getWidget(3, 1);
            	fi.setDate(db.getValue());
            	
            	// Categoria - Category
            	ListBox lb1 = (ListBox)grid.getWidget(4, 1);
            	for (Category c : lists.getCategoryList().getList()) {
					if(c.getName().equals(lb1.getItemText(lb1.getSelectedIndex()))){
						fi.setCategory(c.getId());
					}
				}
            	// Ambito - Scope
            	ListBox lb3 = (ListBox)grid.getWidget(6, 1);
            	for (Scope s : lists.getScopeList().getList()) {
					if(s.getName().equals(lb3.getItemText(lb3.getSelectedIndex()))){
						fi.setScope(s);
					}
				}	
            	
            	// Etiquetas - Tags
            	Vector<Tag> tags = new Vector<Tag>();
            	VerticalPanel vp = (VerticalPanel)grid.getWidget(5, 1);
            	for(Integer i = 0 ;i<vp.getWidgetCount();i++){
            		HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            		ListBox lb = (ListBox)hp.getWidget(0);
            		for (Tag t : lists.getTagList().getList()) {
						if(t.getName().equals(lb.getValue(lb.getSelectedIndex()))){
			            	tags.add(t);
						}
					}
            	}
            	fi.setTags(tags);

            	// Dominio - Domain
            	SuggestBox sb = (SuggestBox)grid.getWidget(7, 1);
            	if(!esta(sb.getText())){
	            	fi.setDomain("false"); 
            	}
            	else fi.setDomain(sb.getText()); 
            	finsert= fi;
                idoc.newFile(fi,new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							popup.hide();
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
									aux= new Vector<FileInfo>();
									for (FileInfo f : dataProvider.getList()) {
										aux.add(f);
									}
									aux.add(result);
									dataProvider = new ListDataProvider<FileInfo>(aux);
									dataProvider.addDataDisplay(dataGrid); 
									dataGrid.redraw();
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
										final SingleUploader upload2 = newUploader(null);
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
		});
		b.setStyleName("aon-commandButton");
		b2.setStyleName("aon-commandButton");
		h.add(b);
		h.add(b2);
		v.add(h);
		v.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_RIGHT);
		popup.setGlassEnabled(true);
		popup.add(v);

		/*
		 * HorizontalPanel hp = new HorizontalPanel(); Button b = new
		 * Button("Cancelar"); Button b2 = new Button("Guardar");
		 * hp.add(b);hp.add(b2); popup.add(hp);
		 */
		popup.show();
	}
	
	public SingleUploader newUploader(SingleUploader up){
		final SingleUploader upload;
       	if(up==null){
       		 upload=  new SingleUploader(FileInputType.BROWSER_INPUT.with(FileInputType.LABEL.getInstance()));
       	}
       	else{
       		 upload = up;
       	}
       	upload.setAutoSubmit(true);
        upload.setServletPath(GWT.getModuleBaseURL() + "/gwt_upload");
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(GWT.getModuleBaseURL() + "/gwt_upload");
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
        
        FileUploadWithMouseEvents a = new FileUploadWithMouseEvents(){
      
        };
        
        upload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
		
			@Override
			public void onStatusChanged(IUploader uploader) {
				if(upload.getStatus() != Status.SUCCESS){
			
					upload.getStatusWidget().setProgress(progress, 100);
			
				}
				else{
					upload.getStatusWidget().setProgress(100, 100);
				}	
				progress=progress+20;
				//upload.addStatusBar(uploader.getStatusWidget());
			}
		});

        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
			
			@Override
			public void onStart(IUploader uploader) {
				upload.getStatusWidget().setVisible(true);
				//Window.alert("start");
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			
			@Override
			public void onFinish(IUploader uploader) {
				upload.getStatusWidget().setProgress(100, 100);
				//Window.alert("finish");
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
			}
		});
        upload.getForm().addFormHandler(new FormHandler() {
			
			@Override
			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();
			}
			
			@Override
			public void onSubmit(FormSubmitEvent event) {}
		});
        
        return upload;
	}
	
	Lists lists = new Lists();
	FlexTable grid;
	HorizontalPanel h2;
	
	@UiHandler("advanceSearch")
	void advancedSearch(ClickEvent event) {
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		ListBox lb3 = new ListBox();
		lb3.addItem("-");

		for (Scope s : lists.getScopeList().getList()) {
			lb3.addItem(s.getName());
		}
		for (Tag t : lists.getTagList().getList()) {
			lb2.addItem(t.getName());
		}
		for (Category c : lists.getCategoryList().getList()) {
			lb1.addItem(c.getName());
		}
		lb2.addChangeHandler(OneHandler());
		
		popup = new DialogBox();
		popup.getCaption().setHTML("<div id=\"aonContent:corporateIdentityAttachForm:j_id3147\" class=\"aon-popupWindow-title\"><span class=\"aon-outputText\">B&Uacute;SQUEDA AVANZADA</span></div>");

		VerticalPanel v = new VerticalPanel();
		v.setSpacing(6);
		grid = new FlexTable();

		grid.setStyleName("aon-panelGrid");
		grid.setWidth("400px");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);

		TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
		grid.setWidget(0, 0, new Label("Descripci\u00f3n"));
		grid.setWidget(0, 1, tb1);

		grid.setWidget(2, 0, new Label("Confidencial"));
		grid.setWidget(2, 1, new CheckBox());
		
	    DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
	    DateBox dateBox = new DateBox();
	    dateBox.setStyleName("aon-inputText");
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    dateBox.getDatePicker().setYearArrowsVisible(true);
		grid.setWidget(3, 0, new Label("Fecha"));
		grid.setWidget(3, 1, dateBox);

		grid.setWidget(4, 0, new Label("Categor\u00eda"));
		grid.setWidget(4, 1, lb1);

		if(lb2.getItemCount() <= 2){
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, lb2);	
		}
		else{
			
			h2 = new HorizontalPanel();
			
			h2.add(lb2);
			
			vertical.add(h2);
			grid.setWidget(5, 0, new Label("Etiqueta"));
			grid.setWidget(5, 1, vertical);
			
		}
		grid.setWidget(6, 0, new Label("\u00c1mbito"));
		grid.setWidget(6, 1, lb3);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
		v.add(grid);
		Label l = new Label("");
		v.add(l);
		HorizontalPanel h = new HorizontalPanel();

		Button b = new Button("CANCELAR");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
				vertical = new VerticalPanel();
			}
		});

		Button b2 = new Button("BUSCAR");
		b2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SearchInfo si = new SearchInfo();
				TextBox tb = (TextBox) grid.getWidget(0, 1);
				if ("".equals(tb.getText()))
					si.setName(null);
				else
					si.setName(tb.getText());

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
					}
				}
				if ("-".equals(s1) || s1 == null)
					si.setCategory(null);
				else
					si.setCategory(s1);

				VerticalPanel vp = (VerticalPanel) grid.getWidget(5,1);
				
				Vector<String> v = new Vector<String>();
				Vector<String> v2 = new Vector<String>();
 				HorizontalPanel hp = (HorizontalPanel) vp.getWidget(0);
				ListBox lb2 = (ListBox) hp.getWidget(0);
				
				String s2 = null;
				for (int i = 0; i < lb2.getItemCount(); i++) {
					if (lb2.isItemSelected(i)) {
						s2 = lb2.getValue(i);
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

						String s= null;
						for (int j = 0; j < lb.getItemCount(); j++) {
							if (lb.isItemSelected(j)) {
								s = lb.getValue(j);
							}
						}
						String ss= null;
						for (int k = 0; k < lbyo.getItemCount(); k++) {
							if (lbyo.isItemSelected(k)) {
								ss = lbyo.getValue(k);
							}
						}
						if(!s.equals("-")){
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
					}
				}
				if ("-".equals(s3) || s3 == null)
					si.setScope(null);
				else
					si.setScope(s3);
				idoc.searchFile(si, docs.getFiles(),
						new AsyncCallback<Vector<FileInfo>>() {

							@Override
							public void onSuccess(Vector<FileInfo> result) {
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
				
				popup.hide();
				vertical = new VerticalPanel();
			}
		});
		b.setStyleName("aon-commandButton");
		b2.setStyleName("aon-commandButton");
		h.add(b);
		h.add(b2);
		v.add(h);
		v.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_RIGHT);
		popup.setGlassEnabled(true);
		popup.add(v);

		/*
		 * HorizontalPanel hp = new HorizontalPanel(); Button b = new
		 * Button("Cancelar"); Button b2 = new Button("Guardar");
		 * hp.add(b);hp.add(b2); popup.add(hp);
		 */
		popup.show();

	}

	VerticalPanel vertical = new VerticalPanel();

	public ChangeHandler OneHandler(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 1){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler());
					h2.add(mas);
				}
				if(s.equals("-")){
					h2.remove(1);
				}				
			}
		};
		return ch;

	}
	
	public ChangeHandler OneHandler2(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 1){
					Button mas = new Button("");
					mas.setStyleName("aon-finding-toolbar-item aon-search-add");
					mas.addClickHandler(masHandler2());
					h2.add(mas);
				}
				if(s.equals("-")){
					h2.remove(1);
				}				
			}
		};
		return ch;

	}
	
	public ChangeHandler TwoHandler(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(1);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 3){
					Button bMas = new Button();
					bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
					bMas.addClickHandler(masHandler());
					
					if(vertical.getWidgetCount()<lists.getTagList().getLength()){
						h2.add(bMas);
					}
				}
				if(s.equals("-")){
					h2.remove(3);
				}
			}
		};
		return ch;

	}
	
	public ChangeHandler TwoHandler2(){
		ChangeHandler ch = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) h2.getWidget(0);
				String s = null;
				for (int i = 0; i < lb.getItemCount(); i++) {
					if (lb.isItemSelected(i)) {
						s = lb.getValue(i);
					}
				}
				if(h2.getWidgetCount() == 2){
					Button bMas = new Button();
					bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
					bMas.addClickHandler(masHandler2());
					
					if(vertical.getWidgetCount()<lists.getTagList().getLength()){
						h2.add(bMas);
					}
				}
				if(s.equals("-")){
					h2.remove(2);
				}
			}
		};
		return ch;

	}
	
	public ClickHandler masHandler() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				
				lb2.addChangeHandler(TwoHandler());
				
				lb2.addItem("-");
				
				for (Tag t : lists.getTagList().getList()) {
					lb2.addItem(t.getName());
				}
				ListBox AndOr = new ListBox();
				AndOr.addItem("Y");
				AndOr.addItem("O");
				h2 = new HorizontalPanel();
				h2.add(AndOr);
				h2.add(lb2);
				
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler());
				h2.add(bMenos);
				
				vertical.add(h2);
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
				if(p.getWidgetCount()==2){
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					hp.remove(1);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(1);
					l.setEnabled(false);
					hp.remove(3);
					hp.remove(2);
				}
				h2.addStyleName("aon-gwt-tags-popup");
				p.add(h2);
				
				grid.setWidget(5, 1, p);
				popup.show();
			}
		};
		
		return ch;
	}
	
	public ClickHandler masHandler2() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListBox lb2 = new ListBox();
				
				lb2.addChangeHandler(TwoHandler2());
				
				lb2.addItem("-");
				
				for (Tag t : lists.getTagList().getList()) {
					lb2.addItem(t.getName());
				}
				
				h2 = new HorizontalPanel();
				h2.add(lb2);
				
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler2());
				h2.add(bMenos);
				
				vertical.add(h2);
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
				
					ListBox l = (ListBox)hp.getWidget(0);
					l.setEnabled(false);
					if(p.getWidgetCount()==2){
						hp.remove(1);
					}
					else {
						hp.remove(2);
						hp.remove(1);
					}
				
				h2.addStyleName("aon-gwt-tags-popup");
				p.add(h2);
				
				grid.setWidget(5, 1, p);
				popup.show();
			}
		};
		
		return ch;
	}
	
	public ClickHandler menosHandler() {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				Button bMas = new Button();
				bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
				bMas.addClickHandler(masHandler());
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler());
				
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
		
				if(p.getWidgetCount()==2){
						ListBox l = (ListBox)hp.getWidget(0);
						l.setEnabled(true);
						hp.add(bMas);
				}
				else {
					ListBox l = (ListBox)hp.getWidget(1);
					l.setEnabled(true);
					hp.add(bMenos);
					hp.add(bMas);		
				}
				p.remove(p.getWidgetCount()-1);
				//vertical.remove(vertical.getWidgetCount()-1);
				grid.setWidget(5, 1, p);
				popup.show();
				
			}
		};
		
		return ch;
	}

	public ClickHandler menosHandler2() {
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				Button bMas = new Button();
				bMas.setStyleName("aon-finding-toolbar-item aon-search-add");
				bMas.addClickHandler(masHandler2());
				Button bMenos = new Button();
				bMenos.setStyleName("aon-finding-toolbar-item aon-search-minus");
				bMenos.addClickHandler(menosHandler2());
				
				VerticalPanel p = (VerticalPanel)grid.getWidget(5,1);
				HorizontalPanel hp =(HorizontalPanel)p.getWidget(p.getWidgetCount()-2);
		
				ListBox l = (ListBox)hp.getWidget(0);
				l.setEnabled(true);
				if(p.getWidgetCount()==2){
						hp.add(bMas);
				}
				else {
					hp.add(bMenos);
					hp.add(bMas);		
				}
				p.remove(p.getWidgetCount()-1);
				//vertical.remove(vertical.getWidgetCount()-1);
				grid.setWidget(5, 1, p);
				popup.show();
				
			}
		};
		
		return ch;
	}

	Vector<FileInfo> searchs;

	@UiHandler("searchButton")
	void XXXXX(ClickEvent event) {
		String searchStr = searchBox.getText();
		idoc.searchFile(searchStr, docs.getFiles(),
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

	@UiHandler("allButton")
	void getAllAttach(ClickEvent event) {
		if(docs.getFiles()==null){
		idoc.getAllFiles(new AsyncCallback<Document>() {

			@Override
			public void onSuccess(Document result) {
				docs = result;
				addDataDisplay(dataGrid);
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
			dataGrid.redraw();
		}
	}

	@UiHandler("serviConveniosButton")
	void getServiConveniosAttach(ClickEvent event) {
		
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
	}

	private void initTableColumns(
			final SelectionModel<FileInfo> selectionModel,
			ListHandler<FileInfo> sortHandler) {
			/*dataGrid.sinkEvents(Event.ONCONTEXTMENU);
					
			dataGrid.addDomHandler(new ContextMenuHandler() {
	    
	        @Override
	        public void onContextMenu(ContextMenuEvent evt) {
	            ContextMenu cm = new ContextMenu();
	            
	        	evt.preventDefault();
	        	//evt.stopPropagation();
	        	
	        	//Integer n = dataGrid.getKeyboardSelectedRow();
	        	MenuBar contextMenu = new MenuBar();
	        	cm.addItem("Editar", contextMenu);
	        	cm.addItem("Borrar", contextMenu);
	        	cm.setPopupPosition(evt.getNativeEvent().getClientX(), evt.getNativeEvent().getClientY());

	        	cm.show();
	        	
	        }

	    }, ContextMenuEvent.getType());*/
		
		initContextMenu();
		/** Name Column **/
		/*Column<FileInfo, String> nameColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				return object.getTitle();
			}
		};*/
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
				sb.appendHtmlConstant("<g:Button id=\"downloadFile\" class=\"aon-editDataTable-button aon-icon-mail-save\" >"
				/*+ "</g:Button><g:Button id=\"editFile\" class=\"aon-editDataTable-button aon-icon-edit\"></g:Button>"
			+ "<g:Button id=\"removeFile\" class=\"aon-editDataTable-button aon-icon-delete\"></g:Button>"*/);
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
				String driveId="";
				if(object.getDriveId()!=null)driveId= object.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download"
                        + "?file_id=" + Integer.toString(object.getFileId())
                        + "&drive_id=" +URL.encode(driveId);
		        Window.open( fileDownloadURL, "_blank","status=0,toolbar=0,menubar=0,location=0");
		      
		       
			}
		});
		
		dataGrid.addColumn(downloadColumn, "Archivo");
		dataGrid.setColumnWidth(downloadColumn, 3, Unit.PX);
		
	}
	Vector<TreeDriveInfo> vtree;
	
	
	private void sons(TreeItem parent,Vector<TreeDriveInfo> sons) {
		Window.alert("sons");
		for (TreeDriveInfo f : sons) {
			 TreeItem ti = new TreeItem();
			 ti = parent.addTextItem(f.getParent().getTitle());
			 sons(ti,f.getSons());
		}
	}
	public void myDrive(){
		idoc.myDrive("", new AsyncCallback<Vector<TreeDriveInfo>>() {
			
			@Override
			public void onSuccess(Vector<TreeDriveInfo> result) {
				 Tree tree = new Tree();
			
				 for (TreeDriveInfo f : result) {		
					 TreeItem ti = new TreeItem();
					 ti = tree.addTextItem(f.getParent().getTitle());
					 sons(ti,f.getSons());
				}			
				 treepanel.add(tree);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});

		 

	}


}
