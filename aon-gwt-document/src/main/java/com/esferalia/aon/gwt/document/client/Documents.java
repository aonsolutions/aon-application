package com.esferalia.aon.gwt.document.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.document.client.css.AonGwtDocumentResources;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.CategoryList;
import com.esferalia.aon.gwt.document.shared.Dialog;
import com.esferalia.aon.gwt.document.shared.DisclosureImages;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Emessage;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.FilterUtil;
import com.esferalia.aon.gwt.document.shared.Init;
import com.esferalia.aon.gwt.document.shared.Lists;
import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.gwt.document.shared.PasswordGenerator;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.SearchInfo;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TagList;
import com.esferalia.aon.gwt.document.shared.TreeDriveInfo;
import com.esferalia.aon.gwt.viewer.client.Viewer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ScrollPanel;
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
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.elemental.Function;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperMenu;

import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.MultiUploader;
import gwtupload.client.SingleUploader;


public class Documents implements EntryPoint {
	
	private static final String SILENT = "silent";
	private static final int DEFAULT_ZOOM = 130;

	
	/**
	 * A scrolling pager that automatically increases the range every time the
	 * scroll bar reaches the bottom.
	 */
	static class ShowMorePager extends AbstractPager {

		/**
		 * The default increment size.
		 */
		private static final int DEFAULT_INCREMENT = 20;

		/**
		 * The increment size.
		 */
		private int incrementSize = DEFAULT_INCREMENT;

		/**
		 * The last scroll position.
		 */
		private int lastScrollPos = 0;

		/**
		 * The scrollable panel.
		 */
		private final ScrollPanel scrollPanel;

		CustomDataGrid<?> dataGridAux;
		
		/**
		 * Construct a new {@link ShowMorePager}.
		 */
		
		public ShowMorePager(CustomDataGrid<?> dataGrid) {
			setDisplay(dataGrid);

			this.scrollPanel = (ScrollPanel) dataGrid.getScrollPanel();
			dataGridAux = dataGrid;
			// Handle scroll events.
			scrollPanel.addScrollHandler(new ScrollHandler() {
				CustomDataGrid<?> dataGrid = dataGridAux;
				@Override
				public void onScroll(ScrollEvent event) {
					// If scrolling up, ignore the event.
					int oldScrollPos = ShowMorePager.this.lastScrollPos;
					ShowMorePager.this.lastScrollPos = scrollPanel
							.getVerticalScrollPosition();
					if (oldScrollPos >= ShowMorePager.this.lastScrollPos) {
						return;
					}

					int maxScrollTop = scrollPanel
							.getMaximumVerticalScrollPosition();

					

					if (ShowMorePager.this.lastScrollPos >= maxScrollTop) {
						// We are near the end, so increase the page size.
						int incrementSize = getIncrementSize();
						Range range = getDisplay().getVisibleRange();
						// We are near the end, so increase the page size.
						int newPageSize = range.getLength() + incrementSize;
						Integer rowCount = dataGrid.getRowCount();
						if(rowCount > range.getLength()){
							if(rowCount <= newPageSize)
								getDisplay().setVisibleRange(0, rowCount);
							else getDisplay().setVisibleRange(0, newPageSize);
						}
					}
				}
			});
		}

		/**
		 * Get the number of rows by which the range is increased when the
		 * scrollbar reaches the bottom.
		 * 
		 * @return the increment size
		 */
		int getIncrementSize() {
			return incrementSize;
		}

		@Override
		protected void onRangeOrRowCountChanged() {
		}

	}


	class DocumentContextMenu extends ContextMenu {

		ScheduledCommand viewCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object;
				if(selFiles.size() == 1) object = selFiles.get(0);
				else object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				Vector<FileInfo> l = new Vector<FileInfo>();
				l.addAll(dataProvider.getList());
				getViewer(object,dataGrid.getKeyboardSelectedRow(),selFiles.size()>1 ? selFiles : l );
				//getAsHTMl(object,dataGrid.getKeyboardSelectedRow(),selFiles.size()>1 ? selFiles : dataProvider.getList() );
			};
		};		

		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {			
				editFile(null,selFiles.size()>1);
			};
		};
		
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				removeFile(selFiles.size()>1);
			};
		};
		
		ScheduledCommand batchCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object;
				if(selFiles.size() == 1) object = selFiles.get(0);
				else object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				addToLote(object,selFiles.size()>1);
			};
		};
		
		ScheduledCommand downloadCommand = new ScheduledCommand() {
			public void execute() {	
				FileInfo object;
				if(selFiles.size() == 1) object = selFiles.get(0);
				else object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				download(object,selFiles.size()>1);
			};
		};
		
		ScheduledCommand shareCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object;
				if(selFiles.size() == 1) object = selFiles.get(0);
				else object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				share(object,selFiles.size()>1);
			};
		};
		
		ScheduledCommand infoCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				info(object);
			};
		};
		
		ScheduledCommand decompressCommand = new ScheduledCommand() {
			public void execute() {
				FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				decompress(object);
			};
		};

		private MenuItem viewItem;
		private MenuItem editItem;
		private MenuItem removeItem;
		private MenuItem batchItem;
		private MenuItem shareItem;
		private MenuItem downloadItem;
		private MenuItem infoItem;
		private MenuItem decompressItem;
		private Integer number = 0;
		private Integer heigth;
		private Integer width;
		private Boolean loteBool = false;
		private Boolean copyBool = false;

		public Integer getHeigth() {
			switch (number) {
			case 8:
				heigth = 180;
				break;
			case 7: 
				heigth = 160;
				break;
			case 6:
				heigth = 140;
				break;
			case 3:
				heigth = 74;
				break;
			case 2:
				heigth = 54;
				break;
			default:
				heigth = 0;
				break;
			}
			return heigth;
		}

		public void setHeigth(Integer heigth) {
			this.heigth = heigth;
		}

		public Integer getWidth() {
			if(loteBool) width = 135;
			else if(copyBool) width = 123;
			else width = 117;
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getNumber(){
			return number;
		}
	
		public void setNumber(Integer number){
			this.number = number;
		}
		 
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
		
		public MenuItem getDecompressItem() {
			return decompressItem;
		}
		public void setDecompressItem(MenuItem decompressItem) {
			this.decompressItem = decompressItem;
		}
		
		public DocumentContextMenu(Boolean par, Boolean lot,FileInfo object,Boolean permiso){

			if(par || permiso){
				viewItem = addItem("Visualizar",viewCommand,
						"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
				viewItem.setEnabled(true);
				number++;
				addSeparator();
				downloadItem = addItem("Descargar",downloadCommand,
						"aon-icon-mail-save",AON.AON_ICON_CMD_BUTTON);
				downloadItem.setEnabled(true);
				number++;
				if(selFiles.size() <= 1){
					infoItem = addItem("Detalles",infoCommand,
							"aon-icon-info",AON.AON_ICON_CMD_BUTTON);
					infoItem.setEnabled(true);
					number++;
				}
			}
			else{
				viewItem = addItem("Visualizar",viewCommand,
						"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
				viewItem.setEnabled(true);number++;
				addSeparator();
				editItem = addItem("Editar", editCommand,
						"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);number++;
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);number++;
				if(!estaLote(object) && !lot){
					batchItem = addItem("A\u00f1adir al Lote", batchCommand,
							"aon-icon-version", AON.AON_ICON_CMD_BUTTON);
					batchItem.setEnabled(true);number++;loteBool = true;
				}
				addSeparator();
				if(selFiles.size() <= 1){
					/*//Window.alert(JSDocuments.isIE()+"");
					if(JSDocuments.isIE()){
						copyLinkItem = addItem("Copiar Link",copyLinkCommand,
								"aon-icon-copy",AON.AON_ICON_CMD_BUTTON);
						copyLinkItem.setEnabled(true);number++;copyBool = true;
					}*/
				}
				shareItem = addItem("Compartir",shareCommand,
						"aon-icon-google-drive",AON.AON_ICON_CMD_BUTTON);
				shareItem.setEnabled(true);number++;
				downloadItem = addItem("Descargar",downloadCommand,
						"aon-icon-mail-save",AON.AON_ICON_CMD_BUTTON);
				downloadItem.setEnabled(true);number++;
				
				if(object.getMimetype().equals(MimeType.ZIP.value())){
					decompressItem = addItem("Descomprimir", decompressCommand,
						"aon-icon-google-drive-zip",AON.AON_ICON_CMD_BUTTON);
					decompressItem.setEnabled(true);number++;
				}
				
				if(selFiles.size() <= 1){
					addSeparator();
					infoItem = addItem("Detalles",infoCommand,
							"aon-icon-info",AON.AON_ICON_CMD_BUTTON);
					infoItem.setEnabled(true);number++;
				}
			}
		}
		public DocumentContextMenu() {
			
		}

		@Override
		public void show() {
			super.show();
		}
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
				d.setIsNextButton(false);
				DocumentsDialog popup2 = new DocumentsDialog(d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
					}

					@Override
					protected void onNext() {}
				};
				popup2.addStyleName("gwt-PopupPanel-document");
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
				d.setIsNextButton(false);
				DocumentsDialog popup2 = new DocumentsDialog(d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
					}

					@Override
					protected void onNext() {}
				};
				popup2.addStyleName("gwt-PopupPanel-document");
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
		
		public CategoryContextMenu() {}

		@Override
		public void show() {
			super.show();
		}
	}
		
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
		searchDomain = docs.getDomain();
	}

	interface Binder extends UiBinder<Widget, Documents> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField PaperItem allFilesPaper;
	// @UiField PaperItem serviConveniosPaper;
	@UiField PaperItem systemPaper;
	@UiField PaperItem lotePaper;
	@UiField PaperMenu lateralMenuPaper;
	
	@UiField(provided = true) HorizontalPanel prueba2;
	@UiField SimplePanel sp;
	@UiField Button upDrive;
	@UiField Button cleanFilterButton;
	@UiField Button advanceSearch;
	@UiField(provided=true) DisclosurePanel epanel;
	@UiField(provided=true) DisclosurePanel dpanel;
	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField StackLayoutPanel stack1;
	@UiField HorizontalPanel ftoolbar;
	@UiField Label sConvenios;
	@UiField Button newFile;
	@UiField Button editFile;
	@UiField Button delFile;
	@UiField Button optionFile;
	@UiField Button reset;
	@UiField(provided = true) DataGrid<FileInfo> dataGrid;
	@UiField(provided = true) TextBox searchBox;
	@UiField(provided = true) SuggestBox enterpriseSearchBox;
	@UiField Button searchButton;
	@UiField Button eSearchButton;
	@UiField Button tagButton;
	@UiField Button catButton;
	@UiField HorizontalPanel gestionLote;
	@UiField HorizontalPanel gestionDocs;
	@UiField Button send;
	@UiField Button clean;

	Boolean gConnection;
	Boolean documentManager;
	Boolean confidentialUser;
	Boolean systemMessage;
	Vector<FileInfo> selFiles;
	ShowMorePager showMorePager;
	
	private void init() {
		initializeCargando();
		if(!Boolean.parseBoolean(getParameter(GWT.getModuleName(), SILENT))){
			pop = new PopupPanel();
			pop.setStyleName("aon-outputConnectionStatus-start");
			pop.setPopupPosition(25, 5);
			pop.show();
		}
		idoc.initAux(getDomain(),new AsyncCallback<Init>() {
			@Override
			public void onSuccess(Init result) {
				documentManager = result.getVector().get(0);
				confidentialUser = result.getVector().get(1);
				systemMessage = result.getVector().get(2);
				
				if(!documentManager){
					// desactivar lotebutton
				}
				getSons();
				if(docs.getEfiles()==null||docs.getEfiles().isEmpty()){
					idoc.getAllFiles(getDomain(),new AsyncCallback<Document>() {
					
					@Override
					public void onSuccess(Document result) {
						docs = result;
						
						idoc.getLists(getDomain(),new AsyncCallback<Lists>() {

							@Override
							public void onFailure(Throwable caught) {
								String head = "com.esferalia.aon.gwt.document.client.Documents"
										+ " - init() - getLists";
								print(head, caught.getMessage());
							}

							@Override
							public void onSuccess(Lists result) {
								pop.hide();
								lists = result;
								DisclosureImages di = new DisclosureImages();
								dpanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Categor\u00edas");
								dpanel.getHeader().setStyleName("headerTextDisclosure-document");
								epanel = new DisclosurePanel(di.getClosed(), di.getOpen(), "Etiquetas");
								Load();
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - init() - getAllFiles";
						print(head, caught.getMessage());
					}
				});
				}
				
			}
			@Override
			public void onFailure(Throwable caught) {
				String head = "com.esferalia.aon.gwt.document.client.Documents"
						+ " - init() - initAux";
				print(head, caught.getMessage());
			}
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
				String head = "com.esferalia.aon.gwt.document.client.Documents"
						+ " - init() - isGconnection";
				print(head, caught.getMessage());
			}
		});
	}
	
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList("iron-icons/iron-icons.html"), new Function() {
			@Override public Object call(Object arg) {
				return null;
			}
		});
	
		GWT.<AonGwtDocumentResources> create(AonGwtDocumentResources.class).css().ensureInjected();
		boolean silent = Boolean.parseBoolean(getParameter(GWT.getModuleName(), SILENT));
		if ( ! silent ){			
			stack1 = new StackLayoutPanel(Unit.EM);
			prueba2 = new HorizontalPanel();
		}
		else {
			// Inject rich styles
			exportPreview(this);
		}
	}
	Category cAux;
	Tag tAux;
	
	public void Load() {
		tree();
		getSons();

		/** CATEGORIES **/
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
			b.addClickHandler(categoryClickHandler(b, cAux));
			vcat.add(b); 
		}

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
			b.addClickHandler(tagClickHandler(b,tAux));
			vtag.add(b); 
		}

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
					searchButton.click();
				}
			}
		});
		
		enterpriseSearchBox = new SuggestBox(Utils.createOracle(getSons()));
		if (getSons().size() == 1) {
			enterpriseSearchBox.setEnabled(false);	
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
			    	if(!isServiconvenios && !isLote && !object.getIsParent() && documentManager){
						editFile.setVisible(true);
						delFile.setVisible(true);
						optionFile.setVisible(true);
					}
			    	else{
			    		editFile.setVisible(false);
						delFile.setVisible(false);
						optionFile.setVisible(false);
			    	}
				}
				
			    if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
			    	Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
			    	FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
			    	if(!isServiconvenios && !isLote && !object.getIsParent() && documentManager){
						editFile.setVisible(true);
						delFile.setVisible(true);
						optionFile.setVisible(true);
					}
			    	else{
			    		editFile.setVisible(false);
						delFile.setVisible(false);
						optionFile.setVisible(false);
			    	}
		
			    	if(event.getColumn() == 0){
			    		if(dataGrid.getSelectionModel().isSelected(object))
			    			dataGrid.getSelectionModel().setSelected(object, false);
			    		else dataGrid.getSelectionModel().setSelected(object, true);
			    	}else if(event.getColumn() != 0){
			    		dataGrid.getSelectionModel().setSelected(object, true);
			    		for(Integer i = 0;i< dataProvider.getList().size();i++){
			    			if(!dataProvider.getList().get(i).equals(object))
			    				dataGrid.getSelectionModel().setSelected(dataProvider.getList().get(i), false);
			    		}
			    	}
			    }
				
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
   					NativeEvent nativeEvent = event.getNativeEvent();
			    	FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());

			    	if(!dataGrid.getSelectionModel().isSelected(object)){
		    			for(Integer i = 0;i< dataProvider.getList().size();i++){
			    			if(!dataProvider.getList().get(i).equals(object))
			    				dataGrid.getSelectionModel().setSelected(dataProvider.getList().get(i), false);
			    		}
		    			dataGrid.getSelectionModel().setSelected(object, true);
			    	}
					selFiles = new Vector<FileInfo>();
					Boolean lot = false;
					Boolean par = false;
					for(FileInfo f :dataProvider.getList()){
						if(dataGrid.getSelectionModel().isSelected(f)){
							selFiles.add(f);
							if(estaLote(f))lot=true;
							if(f.getIsParent()) par = true;
						}
					}
					
					if(selFiles.size() == 1) object = selFiles.get(0);
			    	DocumentContextMenu contextMenu = new DocumentContextMenu(par,lot,object ,isLote || isServiconvenios || object.getIsParent() || !documentManager);

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
	
 		dataGrid = new CustomDataGrid<FileInfo>(30,
				FileInfo.PROVIDES_KEY);
 	
 		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setWidth("100%");
		dataGrid.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(false);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		
		if(docs.getIsServiconvenios()){
			//
			dataProvider = new ListDataProvider<FileInfo>(docs.getServiconvenios());
			dataProvider.addDataDisplay(dataGrid);
		}
		else addDataDisplay(dataGrid);
		
		ListHandler<FileInfo> sortHandler = getSortHandler();
				//docs.getFil());
		dataGrid.addColumnSortHandler(sortHandler);
		
		final SelectionModel<FileInfo> selectionModel = new MultiSelectionModel<FileInfo>(FileInfo.PROVIDES_KEY);
 		//final SingleSelectionModel<FileInfo> selectionModel = new SingleSelectionModel<FileInfo>(FileInfo.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<FileInfo> createCheckboxManager());
		//dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
		showMorePager = new ShowMorePager((CustomDataGrid<FileInfo>) dataGrid);
	
		// Inject rich styles.
		Widget ui = binder.createAndBindUi(this);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.addDomHandler(new MouseOverHandler(){

			@Override
			public void onMouseOver(MouseOverEvent event) {
				Element element = (Element) dataGrid.getElement().getLastChild();
				String top = element.getStyle().getTop();
				if(top.equals("0px")){
					dataGrid.redraw();
				}
			}
			
		}, MouseOverEvent.getType());
		root.add(ui);
		allFilesPaper.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				allFilesClickAction();
			}
		});

		/* SERVICONVENIOS
		serviConveniosPaper.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				serviconveniosClickAction();
			}
		});
		*/
		lotePaper.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				loteClickAction();
			}
		});
		
	//	systemPaper.setDisabled(!systemMessage);
		systemPaper.setVisible(systemMessage);
		systemPaper.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				systemClickAction();
			}
		});
		
		if(!documentManager){
			lotePaper.setVisible(false);
			newFile.setVisible(false);
			ftoolbar.setVisible(false);
		}
		if(gConnection){
			stack1.getHeaderWidget(1).setVisible(true);
			stack1.getWidget(1).setVisible(true);
		}
		else {
			stack1.getHeaderWidget(1).setVisible(false);
			stack1.getWidget(1).setVisible(false);
		}		
		if(docs.getIsServiconvenios()){
			isServiconvenios = true;
			newFile.setVisible(false); 
			sConvenios.setVisible(true);
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
		selFiles = new Vector<FileInfo>();
		for(FileInfo f :dataProvider.getList()){
			if(dataGrid.getSelectionModel().isSelected(f)){
				selFiles.add(f);
			}
		}
		
		editFile(null,selFiles.size()>1);
	}
	
	Boolean mult;
	public void editFile(SingleUploader up,Boolean multiple) {
		FileInfo fi;
		if(selFiles.size() == 1) fi = selFiles.get(0);
		else fi= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		if(!fi.getIsDrive() && !fi.getIsParent() && !isServiconvenios && !isLote){
			Dialog d = new Dialog("edit", "Editar Archivo", "Cancelar", true, "Grabar", true,son);
			d.setLists(lists);
			d.setFileInfo(fi);
			d.setUpload(up);
			d.setBaseUrl(GWT.getModuleBaseURL());
			d.setIsNextButton(false);
			d.setMultiple(multiple);
			d.setConfidentialUser(confidentialUser);
			mult = multiple;
			popup2 = new DocumentsDialog(d){
				Boolean multiple = mult;
				//Vector<FileInfo> vectorAux;
				//FileInfo fiAux;
				@Override
				protected void onAccept() {
					final String dialogCode = getWindowCode();
					cargando.show();
					Integer n =dataGrid.getKeyboardSelectedRow();
        			FileInfo fileInfo = dataProvider.getList().get(n);
        			if (multiple) fileInfo = new FileInfo();
            		// Descripción - Description
        			if(!multiple){
        				TextBox tb = (TextBox)grid.getWidget(0, 1);
            			if(!tb.getText().equals(""))
            				fileInfo.setTitle(tb.getText());
        			}
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
            			
            			fileInfo.setDateStr(Utils.getDay(d.getDate())+"-"+Utils.getMonth(d.getMonth())+"-"+year);
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
            		
            		Vector<FileInfo> fvector = null;
            		if(multiple) fvector = selFiles;
            		
					idoc.editFile(getDomain(), dialogCode, fileInfo,fvector,new AsyncCallback<Vector<FileInfo>>() {
						@Override
						public void onSuccess(Vector<FileInfo> result) {
							cargando.hide();
							vertical = new VerticalPanel();
							Vector<FileInfo> aux = new Vector<FileInfo>();
							for (FileInfo f : docs.getFiles()) {
								Boolean boolAdd = false;
								for(FileInfo f1 : result){
									if(f.getFileId() == f1.getFileId()){
										aux.add(f1);
										boolAdd= true;
									}
								}
								if(!boolAdd) aux.add(f);
							}
							docs.setFiles(aux);
							aux = new Vector<FileInfo>();
							for (FileInfo f : docs.getEfiles()) {
								Boolean boolAdd = false;
								
								for(FileInfo f1 : result){
									if(f.getFileId() == f1.getFileId()){
										aux.add(f1);
										boolAdd= true;
									}
								}
								if(!boolAdd) aux.add(f);
							}
							docs.setEfiles(aux);
							aux = new Vector<FileInfo>();
							
							for (FileInfo f : docs.getFilter()) {
								Boolean boolAdd = false;
								
								for(FileInfo f1 : result){
									if(f.getFileId() == f1.getFileId()){
										aux.add(f1);
										boolAdd= true;
									}
								}
								if(!boolAdd) aux.add(f);	
							}
							
							docs.setFilter(aux);
							aux= new Vector<FileInfo>();
							for (FileInfo f : dataProvider.getList()) {
								Boolean boolAdd = false;
								
								for(FileInfo f1 : result){
									if(f.getFileId() == f1.getFileId()){
										aux.add(f1);
										boolAdd= true;
									}
								}
								if(!boolAdd) aux.add(f);
								dataGrid.getSelectionModel().setSelected(f, false);
							}
							
							dataProvider = new ListDataProvider<FileInfo>(aux);
							dataProvider.addDataDisplay(dataGrid);
							updateDatagridColumns();
							dataGrid.redraw();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							cargando.hide();
							String head = "com.esferalia.aon.gwt.document.client.Documents"
									+ " - editFile(SingleUploader up,Boolean multiple) - editFile";
							print(head, caught.getMessage());
						}
					});
					
					hide();
					
				}
				@Override
				protected void onCancel() {
					final String dialogCode = getWindowCode();
					hide();
					idoc.clearOuts(dialogCode, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {}
						
						@Override
						public void onFailure(Throwable caught) {
							String head = "com.esferalia.aon.gwt.document.client.Documents"
									+ " - editFile(SingleUploader up,Boolean multiple) - clearOuts";
							print(head, caught.getMessage());
						}
					});
					vertical = new VerticalPanel();				
				}
				@Override
				protected void onNext() {}
			};

		}
		else{
			Dialog d = new Dialog("alert", "Editar Archivo", "Cancelar", false, "Volver", true,son);
			d.setFileInfo(fi);
			d.setIsNextButton(false);
			popup2 = new DocumentsDialog(d) {
				
				@Override
				protected void onCancel() {
					hide();
				}
				
				@Override
				protected void onAccept() {
					hide();
				}

				@Override
				protected void onNext() {}
			};
		}
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.show();
	}

	@UiHandler("optionFile")
	void option(ClickEvent event){
		FileInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		selFiles = new Vector<FileInfo>();
		Boolean lot = false;
		Boolean par = false;
		for(FileInfo f :dataProvider.getList()){
			if(dataGrid.getSelectionModel().isSelected(f)){
				selFiles.add(f);
				if(estaLote(f))lot=true;
				if(f.getIsParent()) par = true;
			}
		}
		if(selFiles.size() == 1) object = selFiles.get(0);
    	DocumentContextMenu contextMenu = new DocumentContextMenu(par,lot,object ,isLote || isServiconvenios || object.getIsParent() || !documentManager);
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
	
	@UiHandler("delFile")
	void del(ClickEvent event){
		selFiles = new Vector<FileInfo>();
		for(FileInfo f :dataProvider.getList()){
			if(dataGrid.getSelectionModel().isSelected(f)){
				selFiles.add(f);
			}
		}

		removeFile(selFiles.size()>1);
	}
	
	
	public void removeFile(Boolean multiple){
		FileInfo fi;
		if(selFiles.size() == 1) fi = selFiles.get(0);
		else fi= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		if(!fi.getIsParent() && !isServiconvenios && !isLote){
		Dialog d = new Dialog("delete", "Borrar Archivo", "Cancelar", true, "Borrar", true,son);
		d.setFileInfo(fi);
		d.setIsNextButton(false);
		d.setMultiple(multiple);
		if(multiple) d.setNum(selFiles.size());
		mult = multiple;
		popup2 = new DocumentsDialog(d) {
			Boolean multiple = mult;
			
			@Override
			protected void onCancel() {
				hide();				
			}
			
			@Override
			protected void onAccept() {
				cargando.show();
				hide();
				Vector<FileInfo> fvector = new Vector<FileInfo>();
				if(getFileInfo().getIsDrive()){
					if(multiple) 
						fvector = selFiles;
					else fvector.add(getFileInfo());
					idoc.deleteMydrive(fvector, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							cargando.hide();
						}
						@Override
						public void onFailure(Throwable caught) {
							String head = "com.esferalia.aon.gwt.document.client.Documents"
									+ " - removeFile(Boolean multiple) - deleteMydrive";
							print(head, caught.getMessage());
						}
					});
				}
				else{ 
					if(multiple) 
						fvector = selFiles;
					else fvector.add(getFileInfo());
					
					idoc.removeFile(getDomain(),fvector,new AsyncCallback<Void>() {
						Boolean multiple = mult;
						@Override
						public void onFailure(Throwable caught) {
							String head = "com.esferalia.aon.gwt.document.client.Documents"
									+ " - removeFile(Boolean multiple) - removeFile";
							print(head, caught.getMessage());
						}
						@Override
						public void onSuccess(Void result) {
							cargando.hide();
							Integer n =dataGrid.getKeyboardSelectedRow();
							FileInfo fi = dataProvider.getList().get(n);
							Vector<FileInfo> fvector = new Vector<FileInfo>();
							if(multiple) fvector = selFiles;
							else fvector.add(fi);
							Vector<FileInfo> l = new Vector<FileInfo>();
							for (Integer i = 0; i<docs.getFiles().size();i++) {
								Boolean boolAdd = false;
								for(FileInfo f : fvector){
									if(docs.getFiles().get(i).getFileId() == f.getFileId()){
										boolAdd = true;
									}
								}
								if(!boolAdd) l.add(docs.getFiles().get(i));
							}
							docs.setFiles(l);
							l = new Vector<FileInfo>();
							for (Integer i = 0; i<docs.getEfiles().size();i++) {
								Boolean boolAdd = false;
								for(FileInfo f : fvector){
									if(docs.getEfiles().get(i).getFileId() == f.getFileId()){
										boolAdd = true;
									}	
								}
								if(!boolAdd)l.add(docs.getEfiles().get(i));
							}
							docs.setEfiles(l);
							l = new Vector<FileInfo>();
							for (Integer i = 0; i<docs.getFilter().size();i++) {
								Boolean boolAdd = false;
								for(FileInfo f : fvector){
									if(docs.getFilter().get(i).getFileId() == f.getFileId()){
										boolAdd = true;
									}
								}
								if(!boolAdd) l.add(docs.getFilter().get(i));
							}
							docs.setFilter(l);
							l = new Vector<FileInfo>();
							
							for(Integer j = 0 ; j<dataProvider.getList().size();j++){
								Boolean boolAdd = false;
								for(FileInfo f : fvector){
									if(dataProvider.getList().get(j).getFileId() == f.getFileId()){
										boolAdd = true;
									}	
								}
								if(!boolAdd) l.add(dataProvider.getList().get(j));
								dataGrid.getSelectionModel().setSelected(dataProvider.getList().get(j), false);
							}
								
							dataProvider = new ListDataProvider<FileInfo>(l);
							dataProvider.addDataDisplay(dataGrid);
							updateDatagridColumns();
							dataGrid.redraw();
						}
					} );
				}
			}

			@Override
			protected void onNext() {}
		};

	}
		else{
			Dialog d = new Dialog("alert", "Borrar Archivo", "Cancelar", false, "Volver", true,son);
			d.setFileInfo(fi);
			d.setIsNextButton(false);
			popup2 = new DocumentsDialog(d){
				@Override
				protected void onAccept() {
					hide();
				}
				@Override
				protected void onCancel() {
					hide();					
				}
				@Override
				protected void onNext() {}
			};
		}
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.show();
	
	}
	
	
	public LinkedList<Domain> getSons() {
		if(sons==null){
		idoc.getSons(getDomain(),new AsyncCallback<LinkedList<Domain>>() {
			
			@Override
			public void onSuccess(LinkedList<Domain> result) {
				sons= result;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				String head = "com.esferalia.aon.gwt.document.client.Documents"
						+ " - getSons() - getSons";
				print(head, caught.getMessage());
			}
		});
		}
		return sons;
	}
	
	
	
	LinkedList<Domain> sons = null;
	FileUpload fuchange;
	SingleUploader fuchange2;
	
	@UiHandler("newFile")
	void XXXXXX(ClickEvent event) {
		newFile(null);
	}
	
	FileInfo finsert;
	DocumentsDialog popup2;
	SingleUploader upAux;
	
	private void newFile(SingleUploader up) {	
		if(!documentManager){
			Dialog d = new Dialog("alert","Nuevo Archivo","Cancelar",false,"Volver",true,son);
			d.setIsNextButton(false);
			popup2 = new DocumentsDialog(d) {
				
				@Override
				protected void onCancel() {
					hide();
				}
				
				@Override
				protected void onAccept() {
					hide();
				}

				@Override
				protected void onNext() {}
			};

		}
		else{
		Dialog d = new Dialog("new","Nuevo Archivo","Cancelar",true,"Guardar",true,son);
		d.setBaseUrl(GWT.getModuleBaseURL());
		d.setLists(lists);
		d.setSons(getSons());
		d.setUpload(up);
		d.setNextButtonName("Guardar & Continuar");
		d.setIsNextButton(true);
		d.setConfidentialUser(confidentialUser);
		popup2 = new DocumentsDialog(d){ 
			
			@Override
			protected void onAccept() {		
				final String dialogCode = getWindowCode();
				Integer auxNum = 1;
				FileInfo fi = new  FileInfo();
            	// Descripción - Description
	            
				if(num <= 1){
					TextBox tb = (TextBox)grid.getWidget(1, 1);
            		fi.setTitle(tb.getText());
            		auxNum = 0;
				}
            	// Confidencial - Confidential
	            
				if(confidentialUser){
					CheckBox cb = (CheckBox)grid.getWidget(3-auxNum, 1);
            		fi.setConfidential(cb.getValue());
				}
				else fi.setConfidential(false);
            	// Fecha - Date
	            
            	DateBox db = (DateBox)grid.getWidget(4-auxNum, 1);
            	fi.setDate(db.getValue());
            	// Categoria - Category
                
            	fi.setCategory(-1);
            	ListBox lb1 = (ListBox)grid.getWidget(5-auxNum, 1);
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
            	ListBox lb3 = (ListBox)grid.getWidget(7-auxNum, 1);
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

            	VerticalPanel vp = (VerticalPanel)grid.getWidget(6-auxNum, 1);

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
            	
            	
            	// Dominio - Domain
                
            	if (getSons().size()!=1){
            		SuggestBox sb = (SuggestBox)grid.getWidget(0, 1);
            		if(!esta(Utils.getOracleString(sb.getText()))){
            			fi.setDomain("false"); 
            		}
            		else fi.setDomain(Utils.getOracleString(sb.getText()));
            	}
            	else fi.setDomain("");
            	MultiUploader mupload = (MultiUploader) grid.getWidget(2, 1);
				mupload.reset();
            	finsert= fi;
                idoc.newFile(dialogCode, fi,new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							hide();
							cargando.show();
							// lo inserta en bd, y luego el proceso lo sube a drive.
							idoc.insertFileBD(getDomain(), dialogCode, finsert, new AsyncCallback<Vector<FileInfo>>() {

								@Override
								public void onFailure(Throwable caught) {
									String head = "com.esferalia.aon.gwt.document.client.Documents"
											+ " - newFile(SingleUploader up) - insertFile";
									print(head, caught.getMessage());
								}
										
								@Override
								public void onSuccess(Vector<FileInfo> result) {
									cargando.hide();
									Vector<FileInfo> aux = new Vector<FileInfo>();
									for (FileInfo f : docs.getFiles()) {
										aux.add(f);		
									}
									
									aux.addAll(result);
									docs.setFiles(aux);
									Integer index=0;
									
									while(index < docs.getFilter().size() && docs.getFilter().get(index).getIsParent()){
										index++;
									}
									
									if(docs.getFilter().size() == 0 || result.get(0).getDomain().equals(docs.getFilter().get(index).getDomain())){
										
										aux = new Vector<FileInfo>();
										for (FileInfo f : docs.getEfiles()) {
											aux.add(f);		
										}
										aux.addAll(result);
										docs.setEfiles(aux);
										
										if(hasFilter()){
											FileInfo fi = result.get(0);
											Boolean showCatfilter = false;
											if(categoryFilterList != null && categoryFilterList.size()>0){
												if(contains(fi.getCategoryStr(), categoryFilterList))
													showCatfilter = true;
											} else showCatfilter = true;
													
											Boolean showTagfilter = false;
											if(tagFilterList != null && tagFilterList.size()>0){
												for(Tag tag : fi.getTags()){
													if(contains(tag.getName(), tagFilterList))
														showTagfilter = true;
												}		
											} else showTagfilter = true;
											if(showTagfilter && showCatfilter){
												aux= new Vector<FileInfo>();
												for (FileInfo f : docs.getFilter()) {
													aux.add(f);
												}
												aux.addAll(result);
												docs.setFilter(aux);
												aux= new Vector<FileInfo>();
												for (FileInfo f : dataProvider.getList()) {
													aux.add(f);
													dataGrid.getSelectionModel().setSelected(f, false);
												}
												aux.addAll(result);
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
											aux.addAll(result);
											docs.setFilter(aux);
											aux= new Vector<FileInfo>();
											for (FileInfo f : dataProvider.getList()) {
												aux.add(f);
												dataGrid.getSelectionModel().setSelected(f, false);	

											}

											aux.addAll(result);
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
								public void onFailure(Throwable caught) {
									String head = "com.esferalia.aon.gwt.document.client.Documents"
											+ " - newFile(SingleUploader up) - check";
									print(head, caught.getMessage());
								}
							});
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						cargando.hide();
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - newFile(SingleUploader up) - newFile";
						print(head, caught.getMessage());
					}
				});
			}
			
			@Override
			protected void onCancel() {
				MultiUploader mupload = (MultiUploader) grid.getWidget(2, 1);
				mupload.reset();
				final String dialogCode = getWindowCode();
				hide();
				idoc.clearOuts(dialogCode, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {}
					
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - newFile(SingleUploader up) - oncancel - clearOuts";
						print(head, caught.getMessage());
					}
				});
				
				vertical = new VerticalPanel();			
			}

			@Override
			protected void onNext() {
				final String dialogCode = getWindowCode();
				Integer auxNum = 1;
				FileInfo fi = new  FileInfo();
            	// Descripción - Description
				if(num <= 1){
					TextBox tb = (TextBox)grid.getWidget(1, 1);
            		fi.setTitle(tb.getText());
            		auxNum = 0;
				}
            	// Confidencial - Confidential
				if(confidentialUser){
					CheckBox cb = (CheckBox)grid.getWidget(3-auxNum, 1);
            		fi.setConfidential(cb.getValue());
				}
				else fi.setConfidential(false);
            	// Fecha - Date
            	DateBox db = (DateBox)grid.getWidget(4-auxNum, 1);
            	fi.setDate(db.getValue());
            	// Categoria - Category
            	String categoryString="";
            	fi.setCategory(-1);
            	ListBox lb1 = (ListBox)grid.getWidget(5-auxNum, 1);
            	for (Category c : lists.getCategoryList().getList()) {
            		String s1 = lb1.getItemText(lb1.getSelectedIndex());
            		if(s1.substring(0, 1).equals(Character.toString((char)9650)))
            			s1 = s1.substring(1);
            		
					if(c.getName().equals(s1)){
						categoryString = lb1.getItemText(lb1.getSelectedIndex());
						fi.setCategory(c.getId());
					}
				}
            	for (Category c : lists.getCategoryListSon().getList()) {
            		String s1 = lb1.getItemText(lb1.getSelectedIndex());
					if(s1.substring(0, 1).equals(Character.toString((char)9660))){
						s1= s1.substring(1);
						if(c.getName().equals(s1)){
							categoryString = lb1.getItemText(lb1.getSelectedIndex());
							fi.setCategory(c.getId());
						}
					}
				}
            	// Ambito - Scope
            	String scopeString = "";
            	fi.setScope(new Scope(-1));
            	ListBox lb3 = (ListBox)grid.getWidget(7-auxNum, 1);
            	for (Scope s : lists.getScopeList().getList()) {
            		String s3 = lb3.getItemText(lb3.getSelectedIndex());
            		if(s3.substring(0, 1).equals(Character.toString((char)9650)))
            			s3 = s3.substring(1);
					if(s.getName().equals(s3)){
						scopeString = lb3.getItemText(lb3.getSelectedIndex());
						fi.setScope(s);
					}
				}
            	for (Scope scope : lists.getScopeListSon().getList()) {
            		String s3 = lb3.getItemText(lb3.getSelectedIndex());
					if(s3.substring(0, 1).equals(Character.toString((char)9660))){
						s3= s3.substring(0);
						if(scope.getName().equals(s3)){
							scopeString = lb3.getItemText(lb3.getSelectedIndex());
							fi.setScope(scope);
						}
					}
				}
            	
            	// Etiquetas - Tags
            	Vector<Tag> tags = new Vector<Tag>();

            	VerticalPanel vp = (VerticalPanel)grid.getWidget(6-auxNum, 1);
            	Vector<String> tagsString = new Vector<String>();
            	for(Integer i = 0 ;i<vp.getWidgetCount();i++){
            		HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            		ListBox lb = (ListBox)hp.getWidget(0);
            		for (Tag t : lists.getTagList().getList()) {
                		String s2 = lb.getItemText(lb.getSelectedIndex());
                		if(s2.substring(0, 1).equals(Character.toString((char)9650)))
                			s2 = s2.substring(1);
						if(t.getName().equals(s2)){
							tagsString.add(lb.getItemText(lb.getSelectedIndex()));
			            	tags.add(t);
						}
					}
                	for (Tag t : lists.getTagListSon().getList()) {
                		String s2 = lb.getItemText(lb.getSelectedIndex());
    					if(s2.substring(0, 1).equals(Character.toString((char)9660))){
    						s2= s2.substring(1);
    						if(t.getName().equals(s2)){
    							tagsString.add(lb.getItemText(lb.getSelectedIndex()));
    							tags.add(t);
    						}
    					}
    				}
            	}
            	fi.setTags(tags);
            	SuggestBox sb = null;
            	// Dominio - Domain
            	if (getSons().size()!=1){
            		 sb = (SuggestBox)grid.getWidget(0, 1);
            		if(!esta(Utils.getOracleString(sb.getText()))){
            			fi.setDomain("false"); 
            		}
            		else fi.setDomain(Utils.getOracleString(sb.getText()));
				}
            	else fi.setDomain("");
            	MultiUploader mupload1 = (MultiUploader) grid.getWidget(2, 1);
				mupload1.reset();
            	finsert= fi;
                idoc.newFile(dialogCode, fi,new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							cargando.show();
							// LO INSERTA EN BD, Y LUEGO EL PROCESO LO SUBE A DRIVE!!!
							idoc.insertFileBD(getDomain(), dialogCode, finsert, new AsyncCallback<Vector<FileInfo>>() {

								@Override
								public void onFailure(Throwable caught) {}
								
								@Override
								public void onSuccess(Vector<FileInfo> result) {
									cargando.hide();
									Vector<FileInfo> aux = new Vector<FileInfo>();
									for (FileInfo f : docs.getFiles()) {
										aux.add(f);		
									}
									aux.addAll(result);
									docs.setFiles(aux);
									Integer index=0;
									while(docs.getFilter().get(index).getIsParent()){
										index++;
									}
									if(result.get(0).getDomain().equals(docs.getFilter().get(index).getDomain())){
										aux = new Vector<FileInfo>();
										for (FileInfo f : docs.getEfiles()) {
											aux.add(f);		
										}
										aux.addAll(result);
										docs.setEfiles(aux);
										
										if(hasFilter()){
											FileInfo fi = result.get(0);
											Boolean showCatfilter = false;
											if(categoryFilterList != null && categoryFilterList.size()>0){
												if(contains(fi.getCategoryStr(), categoryFilterList))
													showCatfilter = true;
											}else showCatfilter = true;
													
											Boolean showTagfilter = false;
											if(tagFilterList != null && tagFilterList.size()>0){
												for(Tag tag : fi.getTags()){
													if(contains(tag.getName(), tagFilterList))
														showTagfilter = true;
												}
											}else showTagfilter = true;
											
											if(showTagfilter && showCatfilter){
												aux= new Vector<FileInfo>();
												for (FileInfo f : docs.getFilter()) {
													aux.add(f);
												}
												aux.addAll(result);
												docs.setFilter(aux);
												aux= new Vector<FileInfo>();
												for (FileInfo f : dataProvider.getList()) {
													aux.add(f);
													dataGrid.getSelectionModel().setSelected(f, false);
												}
												aux.addAll(result);
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
											aux.addAll(result);
											docs.setFilter(aux);
											aux= new Vector<FileInfo>();
											for (FileInfo f : dataProvider.getList()) {
												aux.add(f);
												dataGrid.getSelectionModel().setSelected(f, false);
											}
											aux.addAll(result);
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
						cargando.hide();
					}
				});
                
                setWindowCode(PasswordGenerator.getPassword(10));
            	
            	if(auxNum == 1){
					Label l1 = (Label) grid.getWidget(1, 0);
					Label l3 = (Label) grid.getWidget(3, 0);
					Widget w3 = grid.getWidget(3, 1);					
					Label l4 = (Label) grid.getWidget(4, 0);
					Widget w4 = grid.getWidget(4, 1);
					Label l5 = (Label) grid.getWidget(5, 0);
					Widget w5 = grid.getWidget(5, 1);
					Label l6 = (Label) grid.getWidget(6, 0);
					Widget w6 = grid.getWidget(6, 1);
					
					if(confidentialUser){
						Label l2 = (Label) grid.getWidget(2, 0);
						Widget w2 = grid.getWidget(2, 1);
						grid.setWidget(3, 0, l2);
						grid.setWidget(3, 1, w2);
					}
					
					grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
					
					grid.setWidget(2, 0, l1);

					grid.setWidget(4, 0, l3);
					grid.setWidget(4, 1, w3);
					
					grid.setWidget(5, 0, l4);
					grid.setWidget(5, 1, w4);
					
					grid.setWidget(6, 0, l5);
					grid.setWidget(6, 1, w5);
					
					grid.setWidget(7, 0, l6);
					grid.setWidget(7, 1, w6);
					grid.getCellFormatter().setStyleName(7, 0,
							"aon-panelGrid-odd");
					grid.getCellFormatter().setStyleName(7, 1,
							"aon-panelGrid-even");
            	}
            	MultiUploader mupload = new MultiUploader();
        		num = 0;
        		mupload.setAutoSubmit(true);
                mupload.setServletPath( url + "/gwt_document_multiple_upload");
                
                mupload.setMaximumFiles(5);
                mupload.setFileInputPrefix(getWindowCode());
                mupload.setTitle("multipleUploadFormElement");
        		mupload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
        			
        			@Override
        			public void onFinish(IUploader uploader) {
        				num ++;
        				if(num == 1){
        					String s = uploader.getFileInput().getFilenames().get(0);
        					Integer pos = s.lastIndexOf(".");
        					TextBox tb = (TextBox) grid.getWidget(1, 1);
        					if(tb.getText().equals("")){
        						tb.setText(s.substring(0, pos));
        					}
        				} else if(num == 2) grid.removeRow(1);
        			}
        		});
        		
        		mupload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        			
        			@Override
        			public void onCancel(IUploader uploader) {
        				num--;
        				if(num == 1){
        					Label l1 = (Label) grid.getWidget(1, 0);
        					Widget w1 = grid.getWidget(1, 1);
        					
        					Label l3 = (Label) grid.getWidget(3, 0);
        					Widget w3 = grid.getWidget(3, 1);					
        					Label l4 = (Label) grid.getWidget(4, 0);
        					Widget w4 = grid.getWidget(4, 1);
        					Label l5 = (Label) grid.getWidget(5, 0);
        					Widget w5 = grid.getWidget(5, 1);
        					Label l6 = (Label) grid.getWidget(6, 0);
        					Widget w6 = grid.getWidget(6, 1);
        				
        					if(confidentialUser){
        						Label l2 = (Label) grid.getWidget(2, 0);
            					Widget w2 = grid.getWidget(2, 1);
            					grid.setWidget(3, 0, l2);
            					grid.setWidget(3, 1, w2);
        					}
        					
        					final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
        					tb1.setWidth("100%");
        					tb1.addChangeHandler(new ChangeHandler() {
        						@Override
        						public void onChange(ChangeEvent event) {
        							tb1.setStyleName("aon-inputText");
        						}
        					});
        					grid.setWidget(1, 0, new Label("Descripci\u00f3n"));
        					grid.setWidget(1, 1, tb1);
        					
        					grid.setWidget(2, 0, l1);
        					grid.setWidget(2, 1, w1);
        					

        					
        					grid.setWidget(4, 0, l3);
        					grid.setWidget(4, 1, w3);
        					
        					grid.setWidget(5, 0, l4);
        					grid.setWidget(5, 1, w4);
        					
        					grid.setWidget(6, 0, l5);
        					grid.setWidget(6, 1, w5);
        					
        					grid.setWidget(7, 0, l6);
        					grid.setWidget(7, 1, w6);
        					
        					grid.getCellFormatter().setStyleName(7, 0,
        							"aon-panelGrid-odd");
        					grid.getCellFormatter().setStyleName(7, 1,
        							"aon-panelGrid-even");
        				}
        			}
        		});
        		
                grid.setWidget(2, 1, mupload);
                
                final TextBox tb1 = new TextBox();tb1.setStyleName("aon-inputText");
                tb1.setWidth("100%");
        		tb1.addChangeHandler(new ChangeHandler() {
        			@Override
        			public void onChange(ChangeEvent event) {
        				tb1.setStyleName("aon-inputText");
        			}
        		});
        		grid.setWidget(1, 1, tb1);
            	
        		if(!scopeString.substring(0, 1).equals(Character.toString((char)9660)) 
        				&& !categoryString.substring(0, 1).equals(Character.toString((char)9660)) 
        				&& !isSontag(tagsString)){
        			sb.setText("");
    				removeFilter(lb3);
    				removeFilter(lb1);
                	for(Integer i = 0 ;i<vp.getWidgetCount();i++){
                		HorizontalPanel hp = (HorizontalPanel)vp.getWidget(i);
            			ListBox lb = (ListBox)hp.getWidget(0);
    					removeFilter(lb);
                	}

        		}
			}
		};
		}
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.show();
	
	}
	private Boolean isSontag(Vector<String> v){
		for (String string : v) {
			if(string.substring(0, 1).equals(Character.toString((char)9660))){
				return true;
			}
		}
		return false;
	}
	Lists lists =new Lists();
	FlexTable grid;
	HorizontalPanel h2;
	
	@UiHandler("advanceSearch")
	void advancedSearch(ClickEvent event) {
		gestionLote.setVisible(false);
		gestionDocs.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);

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
		optionFile.setVisible(false);
		
 		String searchStr = searchBox.getText();
 		Vector<FileInfo> vaux = new Vector<FileInfo>();
 		if(isServiconvenios) vaux = docs.getServiconvenios();
 		else if(isLote) vaux = lote;
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
		removeFilterCat();
		removeFilterTag();
		removeFilterItems();
		
		String searchStr = Utils.getOracleString(enterpriseSearchBox.getText());
		son = !searchStr.equals(docs.getDomain());
		newFilterCat(searchStr);
		newFilterTag(searchStr);
		searchDomain = searchStr;
		idoc.eSearchFile(docs.getFiles(),searchStr,new AsyncCallback<Vector<FileInfo>>() {
			@Override
			public void onSuccess(Vector<FileInfo> result) {
				gestionLote.setVisible(false);
				gestionDocs.setVisible(true);
				docs.setEfiles(result);
				docs.setFilter(result);
				isServiconvenios =false;
				isLote = false;
				searchs = result;
				for(FileInfo f : dataProvider.getList()){
					dataGrid.getSelectionModel().setSelected(f, false);
				}
				dataProvider = new ListDataProvider<FileInfo>(result);
				dataProvider.addDataDisplay(dataGrid);
				updateDatagridColumns();
				dataGrid.redraw();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});	
	}
	
	private void newFilterCat(String domain){
		VerticalPanel v = (VerticalPanel) dpanel.getContent();
		for(Category c : lists.getCategoryListSon().getList()){
			if(c.getDomain().equals(domain)){
				catname = c.getName();
				Button b =new Button(Character.toString((char)9660)+c.getName()); 
				b.setStyleName("aon-editDataTable-button aon-icon-category");
				catAux = c ;
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
				b.addClickHandler(categoryClickHandler(b, c));
				v.add(b);
			}
		}
	}
	private void newFilterTag(String domain){
		VerticalPanel v = (VerticalPanel) epanel.getContent();
		for(Tag t : lists.getTagListSon().getList()){
			if(t.getDomain().equals(domain)){
				tagname = t.getName();
				Button b =new Button(Character.toString((char)9660)+t.getName()); 
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
				b.addClickHandler(tagClickHandler(b, t));
				v.add(b);
			}}
		
	}
	private void removeFilterCat(){
		VerticalPanel v = (VerticalPanel) dpanel.getContent();
		for (int i = v.getWidgetCount()-1 ; i>=0 ; i--) {
			Button b = (Button) v.getWidget(i);
			if(b.getText().substring(0, 1).equals(Character.toString((char)9660))){
				v.remove(i);
			}
		}
	}
	private void removeFilterTag(){
		VerticalPanel v = (VerticalPanel) epanel.getContent();
		for (int i = v.getWidgetCount()-1 ; i>=0 ; i--) {
			Button b = (Button) v.getWidget(i);
			if(b.getText().substring(0, 1).equals(Character.toString((char)9660))){
				v.remove(i);
			}
		}	
	}
	
	public void allFilesClickAction(){
		newFile.setVisible(true); sConvenios.setVisible(false);
		gestionLote.setVisible(false);
		gestionDocs.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);
		removeFilterItems();
		if(docs.getEfiles()==null ){
					
			idoc.getAllFiles(getDomain(), new AsyncCallback<Document>() {
				
				@Override
				public void onSuccess(Document result) {
					docs = result;
					for(FileInfo f : dataProvider.getList()){
						dataGrid.getSelectionModel().setSelected(f, false);
					}
					addDataDisplay(dataGrid);
					isServiconvenios=false;
					isLote = false;
					updateDatagridColumns();
					dataGrid.redraw();
				}	


				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		else{
			addDataDisplay(dataGrid);
			
			Integer rowCount = showMorePager.getDisplay().getRowCount();
			if(dataProvider.getList().size() == rowCount){
				showMorePager.setRangeLimited(false);
				
			}
			isServiconvenios=false;
			isLote = false;
			updateDatagridColumns();
			dataGrid.redraw();
		}
		docs.setFilter(docs.getEfiles());		
	}

	Boolean isServiconvenios=false;
	Boolean conf;
	PopupPanel pop;
	
	private void serviconveniosClickAction(){
		pop = new PopupPanel();
		pop.setStyleName("aon-outputConnectionStatus-start");
		pop.setPopupPosition(25, 5);
		pop.show();
		newFile.setVisible(false); sConvenios.setVisible(true);
		gestionLote.setVisible(false);
		gestionDocs.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);
		isServiconvenios= true;
		isLote = false;
		removeFilterItems();
		if(docs.getServiconvenios().isEmpty()){
			idoc.getServiConveniosFiles(getDomain(),new AsyncCallback<Vector<FileInfo>>() {
			
			@Override
			public void onSuccess(Vector<FileInfo> result) {
				pop.hide();
				docs.setServiconvenios(result);

				/*for(FileInfo f : dataProvider.getList()){
					dataGrid.getSelectionModel().setSelected(f, false);
				}*/
				dataProvider = new ListDataProvider<FileInfo>(result);
				dataProvider.addDataDisplay(dataGrid);
				updateDatagridColumns();
				dataGrid.redraw();
			}

			@Override
			public void onFailure(Throwable caught) {}
		});}
		else{
			pop.hide();
			for(FileInfo f : dataProvider.getList()){
				dataGrid.getSelectionModel().setSelected(f, false);
			}
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
		/** Check Column **/
	
		 Column<FileInfo, Boolean> checkColumn =
			        new Column<FileInfo, Boolean>(new CheckboxCell(true, true){
			        	
			        	@Override
			        	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context
			        		, Element parent, Boolean value, NativeEvent event
			        		, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {}
			        	
			        }){
			          @Override
			          public Boolean getValue(FileInfo object) {
			            // Get the value from the selection model.
			            return selectionModel.isSelected(object);
			          }
			        };
			    dataGrid.addColumn(checkColumn);
			    dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		
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
						+ object.getIcon() + "\" >"+"&nbsp;&nbsp;"+ object.getTitle());
				}
				;
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
					Vector<FileInfo> v = new Vector<FileInfo>();
					v.addAll(dataProvider.getList());
					getViewer(object,dataGrid.getKeyboardSelectedRow(), v);
					//getAsHTMl(object,dataGrid.getKeyboardSelectedRow(),dataProvider.getList());
				}
				
			});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Descripci\u00f3n");

		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);

		/** Scope Column **/
		Column<FileInfo, String> scopeColumn = new Column<FileInfo, String>(
				new TextCell()) {
			@Override
			public String getValue(FileInfo object) {
				if(object.getScope() ==  null)
					return "-";
				return object.getScope().getName();
			}
		};
		scopeColumn.setSortable(true); 
		 sortHandler.setComparator(scopeColumn,new Comparator<FileInfo>() {
			
			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				String a;String b;
				if(o1.getScope() == null)
					a = "-";
				else a = o1.getScope().getName();
				if(o2.getScope() == null)
					b = "-";
				else b = o2.getScope().getName();
				return a.compareTo(b);
			}
		});

		dataGrid.getColumnSortList().push(scopeColumn);
		
		dataGrid.addColumn(scopeColumn, "\u00c1mbito");

		dataGrid.setColumnWidth(scopeColumn, 16, Unit.PCT);
		
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
		if(confidentialUser){
			Column<FileInfo,SafeHtml> confColumn2 = new Column<FileInfo, SafeHtml>(new SafeHtmlCell()){
		
				@Override
				public SafeHtml getValue(FileInfo object) {
					conf = object.getConfidential();
					SafeHtml sh  =new SafeHtml() {
						Boolean confidential = conf;
						@Override
						public String asString() {
							String s;
							if(confidential){
								s = "<span class='aon-icon-check-black'  style='padding-left:16px'> &nbsp;</span>";
							}
							else {
								s = "<span style='padding-left:6px'> - </span>";
							}
							return s;
						}
					};
				
					return sh;
				}
			};
		
			SafeHtml sh = new SafeHtml() {
		
				@Override
				public String asString() {
					return "<span class='aon-icon-confidential' style='padding-left:15px' title='Confidencial'>&nbsp;</span>";
				}
			};
			dataGrid.addColumn(confColumn2,sh);
			dataGrid.setColumnWidth(confColumn2, 5 , Unit.PCT);
		}
		/** Size Column **/
		Column<FileInfo, String> sizeColumn = getSizeColumn(sortHandler);
		dataGrid.getColumnSortList().push(sizeColumn);
		dataGrid.addColumn(sizeColumn, "Tama\u00f1o");//("+getTotalSize()+")");
		dataGrid.setColumnWidth(sizeColumn, 13, Unit.PCT);

		/** Tag Column **/
		Column<FileInfo, String> tagColumn = getTagColumn(sortHandler);
		dataGrid.getColumnSortList().push(tagColumn);
		dataGrid.addColumn(tagColumn, "Etiquetas");	
		dataGrid.setColumnWidth(tagColumn, 16, Unit.PCT);

		/** Download Column **/
		/*Column<FileInfo,String> downloadColumn = getDownloadColumn();
		dataGrid.addColumn(downloadColumn, "Archivo");//("+dataProvider.getList().size()+")");
		dataGrid.setColumnWidth(downloadColumn, 10, Unit.PCT);
		*/
		String s = getTotalSize()+" / "+dataProvider.getList().size()+" Archivos";
		Label label = new Label(s);
		label.setStyleName("aon-bold aon-padding-top-3px");
		prueba2.add(label);
	}
	Vector<TreeDriveInfo> vtree;

	private void download(FileInfo object,Boolean multiple){
	

		idoc.downloadMultiple(selFiles, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		if(!object.getIsGdocs()){
			String driveId="";
			if(object.getDriveId()!=null)driveId= object.getDriveId();
			String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_document_download/"
                	+ "?file_id=" + Integer.toString(object.getFileId())
                	+ "&drive_id=" +URL.encode(driveId)
                	+ "&mimetype=" +object.getMimetype()
                	+ "&isdrive=" +object.getIsDrive()
					+ "&ismultiple="+multiple
					+ "&domain_id="+object.getDomainId()
					+ "&title="+object.getTitle();
			Window.open( fileDownloadURL, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");
		}
		
	}
	
	private void print(FileInfo object){
		idoc.downloadMultiple(selFiles, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		String driveId="";
		if(object.getDriveId()!=null)driveId= object.getDriveId();
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_print/"
                + "?file_id=" + Integer.toString(object.getFileId())
                + "&drive_id=" +URL.encode(driveId)
                + "&mimetype=" +object.getMimetype()
                + "&domain_id="+object.getDomainId()
                + "&title="+object.getTitle();
		PrintWindow.open(fileDownloadURL, "_blank", null);
	}
	
	String searchDomain;
	private void search() {
		Dialog d = new Dialog("search", "Busqueda Avanzada", "Cancelar", true,
				"Buscar", true,son);
		d.setLists(lists);
		d.setSons(getSons());
		d.setSearchDomain(searchDomain);
		d.setIsNextButton(false);
		d.setConfidentialUser(confidentialUser);
		d.setIsServiconvenios(isServiconvenios);
		popup2 = new DocumentsDialog(d) {
			@Override
			protected void onCancel() {
				hide();
				vertical = new VerticalPanel();
			}
			
			@Override
			protected void onAccept() {	
				removeFilterItems();
				SearchInfo si = new SearchInfo();
				if(getSons().size()!=1 && !isServiconvenios){
					SuggestBox tb0 = (SuggestBox) grid.getWidget(0, 1);
					if ("".equals(tb0.getText()))
						si.setDomain(null);
					else{
						String domain = Utils.getOracleString(tb0.getText());
						si.setDomain(domain);
						searchDomain = domain;
						removeFilterCat();
						removeFilterTag();
						newFilterCat(domain);
						newFilterTag(domain);				
					}
				}else si.setDomain(null);
				TextBox tb1 = (TextBox) grid.getWidget(1, 1);
				if ("".equals(tb1.getText()))
					si.setName(null);
				else
					si.setName(tb1.getText());

				ListBox lbc = (ListBox) grid.getWidget(2, 1);
				for(Integer i = 0; i <lbc.getItemCount();i++){
					if (lbc.isItemSelected(i)) {
						if(lbc.getValue(i).equals("Si")) 
							si.setConfidential(true);
						else if(lbc.getValue(i).equals("No"))
							si.setConfidential(false);
						else si.setConfidential(null);

					}
				}
				DateBox tb2 = (DateBox) grid.getWidget(3, 1);
				if ("".equals(tb2.getTextBox().getText()))
					si.setDate(null);
				else
					si.setDate(tb2.getTextBox().getText());

				VerticalPanel categoryVerticalPanel = (VerticalPanel) grid.getWidget(4, 1);
				LinkedList<String> categorySearchList = new LinkedList<String>();
				for(Integer i = 0; i < categoryVerticalPanel.getWidgetCount(); i++){
					HorizontalPanel categoryHorizontalPanel= (HorizontalPanel) categoryVerticalPanel.getWidget(i);
					ListBox lb1 = (ListBox) categoryHorizontalPanel.getWidget(0);
					if(!lb1.getSelectedItemText().equals("-")){
						String s1 = lb1.getSelectedItemText();
						if(s1.substring(0, 1).equals(Character.toString((char)9660)) || s1.substring(0, 1).equals(Character.toString((char)9650)))
							s1= s1.substring(1);
						categorySearchList.add(s1);
					}
				}
				si.setCategoryList(categorySearchList);

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
				else if(isLote)
					vaux = lote;
				else
					vaux = docs.getEfiles();
				idoc.searchFile(si, vaux,docs.getFiles(), new AsyncCallback<Vector<FileInfo>>() {

					@Override
					public void onSuccess(Vector<FileInfo> result) {
						if(!isServiconvenios && !result.isEmpty() && !docs.getEfiles().get(0).getDomain().equals(result.get(0).getDomain())){
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
						for(FileInfo f : dataProvider.getList()){
							dataGrid.getSelectionModel().setSelected(f, false);
						}
						dataProvider = null;
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

			@Override
			protected void onNext() {}
		};
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.show();
	}
	
	String dId;

	private void share(FileInfo object,Boolean multiple) {
		Dialog d = new Dialog("share", "Compartir Archivo", "Cancelar", true,
				"Compartir", true,son);
		d.setFileInfo(object);
		d.setIsNextButton(false);
		d.setMultiple(multiple);
		mult = multiple;
		popup2 = new DocumentsDialog(d) {
			Boolean multiple = mult;
			@Override
			protected void onAccept() {
				TextBox tb = (TextBox) grid.getWidget(0, 1);
				hide();
				Vector<FileInfo> fvector = new Vector<FileInfo>();
				if(multiple) fvector = selFiles;
				else  fvector.add(getFileInfo());
				
				if(getFileInfo().getIsDrive()){
					idoc.shareMydrive(tb.getText(), fvector, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				else idoc.share(getDomain(), tb.getText(), fvector, new AsyncCallback<Void>() {
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

			@Override
			protected void onNext() {}
		};
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.show();
	}

	private void info(FileInfo object) {
		Dialog d = new Dialog("info", "Detalles Archivo", "Cancelar", false,
				"Salir", true,son);
		d.setFileInfo(object);
		d.setIsNextButton(false);
		d.setBaseUrl(GWT.getModuleBaseURL());
		d.setConfidentialUser(confidentialUser);
		popup2 = new DocumentsDialog(d) {

			@Override
			protected void onCancel() {
				hide();
			}

			@Override
			protected void onAccept() {
				hide();
			}

			@Override
			protected void onNext() {}
		};
		popup2.addStyleName("gwt-PopupPanel-document");
		popup2.setGlassEnabled(true);
		popup2.setAnimationEnabled(false);
		popup2.setModal(false);
		popup2.show();
	}

	private void decompress(final FileInfo object){
		cargando.show();
		idoc.decompress(getDomain(), object, new AsyncCallback<LinkedList<FileInfo>>() {
			
			@Override
			public void onSuccess(LinkedList<FileInfo> result) {
				cargando.hide();
				dataProvider.getList().addAll(result);
				updateDatagridColumns();
				dataGrid.redraw();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				cargando.hide();
			}
		});
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
						gestionLote.setVisible(false);
						gestionDocs.setVisible(true);
						idoc.getDriveFile(rootId, new AsyncCallback<Vector<FileInfo>>() {
							@Override
							public void onSuccess(Vector<FileInfo> result) {
								
								docs.setFilter(result);
								for(FileInfo f : dataProvider.getList()){
									dataGrid.getSelectionModel().setSelected(f, false);

								}
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
					gestionLote.setVisible(false);
					gestionDocs.setVisible(true);
					idoc.getDriveFile(f.getDriveId(), new AsyncCallback<Vector<FileInfo>>() {
						@Override
						public void onSuccess(Vector<FileInfo> result) {

							docs.setFilter(result);
							for(FileInfo f : dataProvider.getList()){
								dataGrid.getSelectionModel().setSelected(f, false);
							}
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
	
	private void getViewer(final Attach attach, final Integer index, final LinkedList<Attach> attachList){
		Viewer.getViewer(attach, index, attachList);
	}
	
	private void getViewer(final FileInfo fileInfo) {
		Attach attach = new Attach()
				.setId(fileInfo.getFileId())
				.setAttachType(AttachType.REGISTRY)
				.setDescription(fileInfo.getTitle())
				.setDomain(new Domain().setName(fileInfo.getDomain()).setId(fileInfo.getDomainId()))
				.setDriveId(fileInfo.getDriveId())
				.setIcon(fileInfo.getIcon())
				.setMimeType(MimeType.values()[fileInfo.getMimetype()]);
		
		Viewer.getViewer(attach);
	}
	
	private void getViewer(final FileInfo fileInfo, final Integer index, Vector<FileInfo> viewList) {
		idoc.getLink(getDomain(), fileInfo, GWT.getModuleBaseURL(), new AsyncCallback<String>() {
				
			@Override
			public void onSuccess(String result) {
				Window.open(result, "_blank", null);
			}
			
			@Override
				public void onFailure(Throwable caught) {}
			});
		
		/* VISOR GWT
		
		idoc.getAttachList(viewList, new AsyncCallback<LinkedList<Attach>>() {
			
			@Override
			public void onSuccess(LinkedList<Attach> result) {
				Viewer.getViewer(result.get(index), index, result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
				Window.alert(caught.getMessage());
			}
		});
		
		*/
		
		
	//	}
		/*
		Attach attach = new Attach()
				.setId(fileInfo.getFileId())
				.setAttachType(AttachType.REGISTRY)
				.setDescription(fileInfo.getTitle())
				.setDomain(new Domain().setName(fileInfo.getDomain()).setId(fileInfo.getDomainId()))
				.setDriveId(fileInfo.getDriveId())
				.setIcon(fileInfo.getIcon())
				.setMimeType(MimeType.values()[fileInfo.getMimetype()]);
		
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		for (FileInfo fileInfo2 : viewList) {
			attachList.add(new Attach()
				.setId(fileInfo2.getFileId())
				.setAttachType(AttachType.REGISTRY)
				.setDescription(fileInfo2.getTitle())
				.setDomain(new Domain().setName(fileInfo2.getDomain()).setId(fileInfo2.getDomainId()))
				.setDriveId(fileInfo2.getDriveId())
				.setIcon(fileInfo2.getIcon())
				.setMimeType(MimeType.values()[fileInfo2.getMimetype()]));
		}	
		Viewer.getViewer(attach, index, attachList);*/
	}

	@UiHandler("reset")
	void reset(ClickEvent event){
		reset();
	}
	
	private void reset() {
		searchBox.setText("");
		lateralMenuPaper.setSelected("0");
		removeFilterCat();
		removeFilterTag();
		searchDomain = docs.getDomain();
		newFile.setVisible(true); sConvenios.setVisible(false);
		gestionLote.setVisible(false);
		gestionDocs.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);
		removeFilterItems();

		if(getSons().size()==1){
			for(FileInfo f : dataProvider.getList()){
				dataGrid.getSelectionModel().setSelected(f, false);
			}
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
					for(FileInfo f : dataProvider.getList()){
						dataGrid.getSelectionModel().setSelected(f, false);
					}
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
		d.setIsNextButton(false);
		popup2 = new DocumentsDialog(d){ 
			@Override
			protected void onAccept() {
				hide();
				FileInfo fi = new FileInfo();
            	TextBox tb = (TextBox)grid.getWidget(1, 1);
            	fi.setTitle(tb.getText());
            	
            	
            	idoc.upload(fi,driveId, new AsyncCallback<Void>() {
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
			@Override
			protected void onNext() {}
		
		};
		popup2.addStyleName("gwt-PopupPanel-document");
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
				download(object,false);
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
		/*dataGrid.removeColumn(6);
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
	*/
		String s = getTotalSize()+" / "+dataProvider.getList().size()+" Archivos";
		Label l1 = (Label)prueba2.getWidget(0);
		l1.setText(s);
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
		 tb.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				VerticalPanel v = (VerticalPanel) epanel.getContent();
				v.remove(v.getWidgetCount() - 1);				
			}
		 });

		 tb.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					VerticalPanel v = (VerticalPanel) epanel.getContent();
					TextBox t =  (TextBox) v.getWidget(v.getWidgetCount() - 1);
					tagname = t.getText();
					idoc.newTag(getDomain(), tagname, new AsyncCallback<Tag>() {
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
							b.addClickHandler(tagClickHandler(b, tagAux));
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
		 tb.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				VerticalPanel v = (VerticalPanel) dpanel.getContent();
				v.remove(v.getWidgetCount() - 1);	
			}
		 });
		 tb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					VerticalPanel v = (VerticalPanel) dpanel.getContent();
					TextBox t =  (TextBox) v.getWidget(v.getWidgetCount() - 1);
					catname = t.getText();
					idoc.newCategory(getDomain(), catname,new AsyncCallback<Category>() {
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
							b.addClickHandler(categoryClickHandler(b,catAux));
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
		 Dialog d = new Dialog("edit2", "Editar Etiqueta", "Cancelar", true, "Grabar", true,false);
		 d.setTag(tag);
		 d.setIsNextButton(false);
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
				idoc.editTag(getDomain(), tag.getName(),tag.getId(), new AsyncCallback<Void>() {
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
						for(FileInfo f : dataProvider.getList()){
							dataGrid.getSelectionModel().setSelected(f, false);
						}
						dataProvider = new ListDataProvider<FileInfo>(docs.getFilter());
						dataProvider.addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
							
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - editTag(Tag tag) - editTag";
						print(head, caught.getMessage());
					}
				});
			}
			@Override
			protected void onNext() {}
		};
		popup.addStyleName("gwt-PopupPanel-document");
		popup.setGlassEnabled(true);
		popup.show();
	}
	 
	 private void removeTag(Tag tag) {
		 Dialog d = new Dialog("delete2", "Borrar Etiqueta", "Cancelar", true, "Borrar", true,false);
		 d.setTag(tag);
		 d.setIsNextButton(false);
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
				
				idoc.deleteTag(getDomain(), tag.getId(), new AsyncCallback<Void>() {
						
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
						public void onFailure(Throwable caught) {
							String head = "com.esferalia.aon.gwt.document.client.Documents"
									+ " - removeTag(Tag tag) - deleteTag";
							print(head, caught.getMessage());
						}
				});
			}

			@Override
			protected void onNext() {}
		};
		popup.addStyleName("gwt-PopupPanel-document");
		popup.setGlassEnabled(true);
		popup.show();
	
		
	}
	 
	 private void editCategory(Category category) {
		
		 Dialog d = new Dialog("edit2", "Editar Categor�a", "Cancelar", true, "Grabar", true,false);
		 d.setCat(category);
		 d.setIsNextButton(false);
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
				
				idoc.editCategory(getDomain(), cat.getName(),cat.getId(), new AsyncCallback<Void>() {
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
							dataGrid.getSelectionModel().setSelected(f, false);
							
						}
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - editCategory(Category category) - editCategory";
						print(head, caught.getMessage());
					}
				});			
			}

			@Override
			protected void onNext() {}
		};
		popup.addStyleName("gwt-PopupPanel-document");
		popup.setGlassEnabled(true);
		popup.show();
	}
	 Vector<Category> catList = new Vector<Category>();
	 private void removeCategory(Category category) {
		 catList = lists.getCategoryList().getList();
		 Dialog d = new Dialog("delete2", "Borrar Etiqueta", "Cancelar", true, "Borrar", true,false);
		 d.setCat(category);
		 d.setIsNextButton(false);
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

				idoc.deleteCategory(getDomain(), cat.getId(),new AsyncCallback<Void>() {
							
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
							dataGrid.getSelectionModel().setSelected(f, false);
						}
						
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - removeCategory(Category category) - deleteCategory";
						print(head, caught.getMessage());
					}
				});			
			}

			@Override
			protected void onNext() {}
		};
		popup.addStyleName("gwt-PopupPanel-document");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	public  void addToLote(FileInfo fi,Boolean multiple) {
			Vector<FileInfo> fvector = new Vector<FileInfo>();
			if(multiple) fvector = selFiles;
			else fvector.add(fi);
			lote.addAll(fvector);
	}
	
	Boolean isLote = false;
	Vector<FileInfo> lote = new Vector<FileInfo>();

	private void loteClickAction() {
		gestionLote.setVisible(true);
		gestionDocs.setVisible(false);
		clean.setVisible(true);
		send.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);
		isLote= true;
		isServiconvenios = false;
		removeFilterItems();
		for(FileInfo f : dataProvider.getList()){
			dataGrid.getSelectionModel().setSelected(f, false);
		}
		dataProvider = new ListDataProvider<FileInfo>(lote);
		dataProvider.addDataDisplay(dataGrid);
		updateDatagridColumns();
		dataGrid.redraw();
	}
	
	private void systemClickAction() {
		newFile.setVisible(false);
		sConvenios.setVisible(false);
		gestionLote.setVisible(false);
		gestionDocs.setVisible(true);
		editFile.setVisible(false);
		delFile.setVisible(false);
		optionFile.setVisible(false);
		
		idoc.getSystemFiles(getDomain(), new AsyncCallback<LinkedList<FileInfo>>() {
			@Override
			public void onSuccess(LinkedList<FileInfo> result) {
				removeFilterItems();
				for(FileInfo f : dataProvider.getList()){
					dataGrid.getSelectionModel().setSelected(f, false);
				}
				dataProvider = new ListDataProvider<FileInfo>(result);
				dataProvider.addDataDisplay(dataGrid);
				updateDatagridColumns();
				dataGrid.redraw();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		
	}
	
	public Boolean estaLote(FileInfo fi){
		if(lote!=null){
			for (FileInfo f : lote) {
				if(fi.getFileId() == f.getFileId())
					return true;
			}
		}
		return false;
	}
	
	@UiHandler("clean")
	void cleanLote(ClickEvent event) {
		lote = new Vector<FileInfo>();
		for(FileInfo f : dataProvider.getList()){
			dataGrid.getSelectionModel().setSelected(f, false);
		}
		dataProvider = new ListDataProvider<FileInfo>(lote);
		dataProvider.addDataDisplay(dataGrid);
		updateDatagridColumns();
		dataGrid.redraw();	
	}

	@UiHandler("send")
	void sendLote(ClickEvent event) {
		
		idoc.getMailAccounts(getDomain(), new AsyncCallback<MailAccountList>() {
			
			@Override
			public void onSuccess(MailAccountList result) {
				SendEmailDialog sed = new SendEmailDialog(result,lote,GWT.getModuleBaseURL(),gConnection) {

					@Override
					protected void onAccept(String s) {
						final String dialogCode = getWindowCode();
						hide();	
						
						Emessage em = new Emessage();
						SuggestBox sb1 = (SuggestBox) grid.getWidget(1, 1);
						em.setRecipientsTo(sb1.getText());
						
						SuggestBox sb2 = (SuggestBox) grid.getWidget(2, 1);
						em.setRecipientsBcc(sb2.getText());
						
						SuggestBox sb3 = (SuggestBox) grid.getWidget(3, 1);
						em.setRecipientsCc(sb3.getText());
						
						TextBox tb4 = (TextBox) grid.getWidget(4, 1);
						em.setSubject(tb4.getText());
						
						RichTextArea ra = (RichTextArea) textEditor.getWidget(1, 0);
						em.setContent(ra.getHTML());
						
						em.setFiles(lote);
						if(s.equals("mail"))
						idoc.sendEmail(getDomain(), dialogCode, ma, em, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
						
						else if(s.equals("gmail"))
						idoc.sendGmail(getDomain(), dialogCode, ma, em, new AsyncCallback<Void>() {
							
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
				sed.addStyleName("gwt-PopupPanel-document");
				sed.setGlassEnabled(true);
				sed.show();					
			}
			
			@Override
			public void onFailure(Throwable caught) {
				String head = "com.esferalia.aon.gwt.document.client.Documents"
						+ " - sendLote(ClickEvent event) - getMailAccounts";
				print(head, caught.getMessage());
			}
		});
	}
	
	// ------------------------------------------------------------------------
	
	public void preview( int index, JsFileInfo [] files){
		Vector<FileInfo> viewList = new Vector<FileInfo>(files.length);
		for (int i = 0; i < files.length; i++) {
			FileInfo fileInfo = new FileInfo();
			JsFileInfo jsFileInfo = files[i];
			fileInfo.setTitle(jsFileInfo.getTitle());
			fileInfo.setIcon(jsFileInfo.getIcon());
			fileInfo.setFileId(jsFileInfo.getFileId());
			fileInfo.setMimetype(jsFileInfo.getMimetype());
			fileInfo.setDriveId(jsFileInfo.getDriveId());
			fileInfo.setDomain(getDomain().getName());
			fileInfo.setDomainId(getDomain().getId());
			viewList.add(fileInfo);
		}
		getViewer(viewList.get(index), index, viewList);
	}

	public static native void exportPreview(Documents thiz) /*-{
    	$wnd.preview = function(index, files) {
    		thiz.@com.esferalia.aon.gwt.document.client.Documents::preview(*)(index, files);
    	}
	}-*/;
	
	public void remove() {
		splitLayoutPanel.removeFromParent();
	}

	
	private Domain getDomain(){
		return new Domain().setId(getCurrentDomain()).setName(getCurrentDomainName());
	}
	
	private static void print(String head, String msg) {
		final IDocumentAsync DOC_IMPL = GWT.create(IDocument.class);
		DOC_IMPL.print(head, msg, new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	Cargando cargando;
	private void initializeCargando(){
		cargando = new Cargando() {}; 
		cargando.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// Nothing						
			}
		});
		cargando.setAutoHideEnabled(false);
		cargando.addStyleName("cargando-document");
		cargando.setGlassEnabled(true);
	}
	

	LinkedList<String> categoryFilterList;
	LinkedList<String> tagFilterList;
	
	Button categoryButtonAuxiliar;
	private ClickHandler categoryClickHandler(Button button, final Category category){
		categoryButtonAuxiliar = button;
		return new ClickHandler() {
			Button button = categoryButtonAuxiliar;
			@Override 
			public void onClick(ClickEvent event) {
				newFile.setVisible(true); sConvenios.setVisible(false);
				gestionLote.setVisible(false);
				gestionDocs.setVisible(true);
				editFile.setVisible(false);
				delFile.setVisible(false);
				optionFile.setVisible(false);
				if(categoryFilterList == null) categoryFilterList = new LinkedList<String>();
				if(!categoryFilterList.contains(category.getName())){
					button.removeStyleName("aon-icon-category");
					button.addStyleName("aon-icon-check-black");
					button.addStyleName(AON.AON_BOLD);
					categoryFilterList.add(category.getName());
				} else {
					button.removeStyleName("aon-icon-check-black");
					button.removeStyleName(AON.AON_BOLD);
					button.addStyleName("aon-icon-category");
					categoryFilterList.remove(category.getName());
				}
				
				SearchInfo si = new SearchInfo()
					.setCategoryList(categoryFilterList)
					.setTagList(tagFilterList);
				
				idoc.searchFile2(si, docs.getEfiles(), new AsyncCallback<FilterUtil>() {
					@Override 
					public void onSuccess(FilterUtil result) {
						searchs = result.getFiles();
						docs.setFilter(result.getFiles());
						for(FileInfo f : dataProvider.getList()){
							dataGrid.getSelectionModel().setSelected(f, false);
						}
						dataProvider = new ListDataProvider<FileInfo>(searchs);
						dataProvider.addDataDisplay(dataGrid);
						isServiconvenios=false;
						isLote = false;
						updateDatagridColumns();
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
							+ " - Load() - searchFile2";
						print(head, caught.getMessage());
					}
				});		
			}		
		};
	}
	
	Button tagButtonAuxiliar;
	private ClickHandler tagClickHandler(Button button, final Tag tag){
		tagButtonAuxiliar = button;
		return new ClickHandler() {
			Button button = tagButtonAuxiliar;
			@Override
			public void onClick(ClickEvent event) {
				newFile.setVisible(true); sConvenios.setVisible(false);
				gestionLote.setVisible(false);
				gestionDocs.setVisible(true);
				editFile.setVisible(false);
				delFile.setVisible(false);
				optionFile.setVisible(false);
				if(tagFilterList == null) tagFilterList = new LinkedList<String>();
				if(!tagFilterList.contains(tag.getName())){
					button.removeStyleName("aon-icon-tag");
					button.addStyleName("aon-icon-check-black");
					button.addStyleName(AON.AON_BOLD);
					tagFilterList.add(tag.getName());
				} else{
					button.removeStyleName("aon-icon-check-black");
					button.removeStyleName(AON.AON_BOLD);
					button.addStyleName("aon-icon-tag");
					tagFilterList.remove(tag.getName());
				}
	
				SearchInfo si = new SearchInfo()
					.setCategoryList(categoryFilterList)
					.setTagList(tagFilterList);
			
				idoc.searchFile2(si, docs.getEfiles(), new AsyncCallback<FilterUtil>() {
					@Override
					public void onSuccess(FilterUtil result) {
						searchs = result.getFiles();
						docs.setFilter(result.getFiles());
						for(FileInfo f : dataProvider.getList()){
							dataGrid.getSelectionModel().setSelected(f, false);
						}
						dataProvider = new ListDataProvider<FileInfo>(searchs);
						dataProvider.addDataDisplay(dataGrid);
						isServiconvenios=false;
						isLote = false;
						updateDatagridColumns();
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						String head = "com.esferalia.aon.gwt.document.client.Documents"
								+ " - Load() - searchFile2";
						print(head, caught.getMessage());
					}
				});
			}						
		};
	}
	
	private Boolean contains(String string, LinkedList<String> list){
		for(String s : list){
			if(s.equals(string)) return true;
		}
		return false;
	}
	
	private Boolean hasFilter(){
		return (categoryFilterList != null && categoryFilterList.size()>1) || (tagFilterList != null && tagFilterList.size() >1);
	}
	
	@UiHandler("cleanFilterButton")
	void cleanFilterButtonClickAction(ClickEvent event){
		reset();
	}
	
	private void removeFilterItems() {
		tagFilterList = new LinkedList<String>();
		categoryFilterList = new LinkedList<String>();
		VerticalPanel tagPanel = (VerticalPanel) epanel.getContent();
		for(Integer i = 0; i < tagPanel.getWidgetCount(); i++){
			Button button = (Button) tagPanel.getWidget(i);
			button.removeStyleName("aon-icon-check-black");
			button.removeStyleName(AON.AON_BOLD);
			button.addStyleName("aon-icon-tag");
		}
		
		VerticalPanel categoryPanel = (VerticalPanel) dpanel.getContent();
		for(Integer i = 0; i < categoryPanel.getWidgetCount(); i++){
			Button button = (Button) categoryPanel.getWidget(i);
			button.removeStyleName("aon-icon-check-black");
			button.removeStyleName(AON.AON_BOLD);
			button.addStyleName("aon-icon-category");
		}
	}
}
