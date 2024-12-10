package com.esferalia.aon.gwt.template.client.marketplace;


import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.client.ITemplateAsync;
import com.esferalia.aon.gwt.template.client.ProgressBarDialog;
import com.esferalia.aon.gwt.template.client.TemplatesDialog;
import com.esferalia.aon.gwt.template.client.Utils;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
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
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ProductTemplates  extends ResizeComposite{
	
	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);
	final ITemplateAsync item = GWT.create(ITemplate.class);
	
	interface ProductTemplatesBinder extends UiBinder<Widget, ProductTemplates> {
	
	}
	
	private static final ProductTemplatesBinder binder = GWT.create(ProductTemplatesBinder.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	class DocumentContextMenu extends ContextMenu {
		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				EcommerceProduct object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				edit(object);
			};
		};
		
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				EcommerceProduct object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				delete(object);
			};
		};
		
		/*ScheduledCommand downloadCommand = new ScheduledCommand() {
			public void execute() {	
				EcommerceProduct object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				download(object);
			};
		};*/
		
		private MenuItem editItem;
		private MenuItem removeItem;
		//private MenuItem downloadItem;
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
		
		public DocumentContextMenu(EcommerceProduct object){
			editItem = addItem("Editar", editCommand,
				"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
			editItem.setEnabled(true);
			removeItem = addItem("Borrar", removeCommand,
				AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
			removeItem.setEnabled(true);
			/*addSeparator();
			downloadItem = addItem("Descargar",downloadCommand,
				"aon-icon-google-drive-excel",AON.AON_ICON_CMD_BUTTON);
			downloadItem.setEnabled(true);
			*/
		}

		@Override
		public void show() {
			super.show();
		}
	}
	
	@UiField(provided = true) DataGrid<EcommerceProduct> dataGrid; 
	@UiField Button new_button;
	@UiField Button edit_button;
	@UiField Button delete_button;
	@UiField Button export_button;
	@UiField(provided = true) TextBox nameSearchBox;
	@UiField(provided = true) TextBox typeSearchBox;
	@UiField Button nameSearchButton;
	@UiField Button typeSearchButton;
	
	AonData aonData;
	
	List<EcommerceProduct> templateList;
	ProgressBarDialog pbd;

	
	public ProductTemplates(AonData aonData, List<EcommerceProduct> templateList) {
		this.aonData = aonData;
		setTemplateList(templateList);
		
		dataGrid = new DataGrid<EcommerceProduct>(Integer.MAX_VALUE, resources); 
		new_button = new Button();
		edit_button = new Button();
		delete_button = new Button();
		export_button = new Button();
		nameSearchBox = new TextBox();
		typeSearchBox = new TextBox();
		nameSearchButton = new Button();
		typeSearchButton = new Button();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		load();
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain() {
		return getAonData().getDomain();
	}
	
	public User getUser() {
		return getAonData().getUser();
	}
	
	private void load() {
		loadSearchBox();
		loadDataGrid();
	}
	
	//------------------------------ Actions
	
	@UiHandler("nameSearchButton")
	void namesbutton(ClickEvent event) {
		typeSearchBox.setText("");
		String searchStr = nameSearchBox.getText();
		Vector<EcommerceProduct> vaux = new Vector<EcommerceProduct>();
		vaux.addAll(getTemplateList());
		//dataProvider.getList().stream().forEach(f-> vaux.add(f));
 		
		impl.searchNameTemplate(searchStr, vaux, new AsyncCallback<Vector<EcommerceProduct>>() {

			@Override
			public void onSuccess(Vector<EcommerceProduct> result) {
				dataProvider = new ListDataProvider<EcommerceProduct>(result);
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
		Vector<EcommerceProduct> vaux = new Vector<EcommerceProduct>();
		vaux.addAll(getTemplateList()); 		
		impl.searchTypeTemplate(searchStr, vaux, new AsyncCallback<Vector<EcommerceProduct>>() {

			@Override
			public void onSuccess(Vector<EcommerceProduct> result) {
				dataProvider = new ListDataProvider<EcommerceProduct>(result);
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
		ecommerce();
	}
	public void ecommerce(){
		item.getSellerList(getDomain(), getUser(), new AsyncCallback<List<Seller>>() {
			
			@Override
			public void onSuccess(List<Seller> result) {
				importEcommerce(result, null);				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	public void ecommerce(final EcommerceProduct object){
		item.getSellerList(getDomain(), getUser(), new AsyncCallback<List<Seller>>() {
			
			@Override
			public void onSuccess(List<Seller> result) {
				importEcommerce(result, object);				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void importEcommerce(final List<Seller> sellerList,final EcommerceProduct object) {
		impl.getMarketplaceTagList(getDomain(), getUser(), new AsyncCallback<LinkedList<Tag>>() {
	
			@Override
			public void onSuccess(LinkedList<Tag> result) {
				Dialog d;
				if(object != null)
					d = new Dialog("Editar Plantilla Ecommerce", "Editar", true, "Cancelar", true, "editEcommerceTemplate");
				else
					d = new Dialog("Importar Plantilla Ecommerce","Importar",true,"Cancelar",true,"importEcommerceTemplate");
				
				d.setUrl(GWT.getModuleBaseURL())
					.setTagList(result)
					.setSellerList(sellerList)
					.setEcommerceProduct(object);
				
				TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						
						Upload upload = new Upload() {
							
							@Override
							protected void onUpload(String data, String type) {
								hide();
								pbd = new ProgressBarDialog(2.0, 1.0) {
								};
								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.show();
								ListBox ecommerceListBox = (ListBox) flex_table.getWidget(0, 1);
								Integer ordinal = Integer.parseInt(ecommerceListBox.getSelectedValue());
								Ecommerce ecommerce = Ecommerce.values()[ordinal];
								
								ListBox sellerListBox = (ListBox) flex_table.getWidget(1, 1);
								Seller seller = new Seller();
								seller.setName(sellerListBox.getSelectedItemText());
								seller.setId(Integer.parseInt(sellerListBox.getSelectedValue()));

								TextBox typeTextBox = (TextBox) flex_table.getWidget(2, 1);
								String type2 = typeTextBox.getText();

								ListBox tagListBox = (ListBox) flex_table.getWidget(3, 1);
								Tag tag = new Tag();
								if(!tagListBox.getSelectedItemText().equals("-"))
									tag.setName(tagListBox.getSelectedItemText())
										.setId(Integer.parseInt(tagListBox.getSelectedValue()));

								item.executeExcelEcommerce(getDomain(), getUser(), ecommerce, seller, type2, tag, data, new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {		
										pbd.hide();
										Dialog d2 = new Dialog("Importar Plantilla Ecommerce","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();	
												refreshDataGrid();
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
							}
						};
						upload.upload();
					}
				};		
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("export_button")
	void exportButton(ClickEvent event){
		exportEcommerce();
	}
	
	private void exportEcommerce(){
		item.getTypeList(getDomain(), getUser(), new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				Dialog d = new Dialog("Exportar Productos Ecommerce","Exportar",true,"Cancelar",true,"exportEcommerce");
				d.setUrl(GWT.getModuleBaseURL());
				d.setTypeList(result);
				TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						ListBox listBox = (ListBox) flex_table.getWidget(0, 1);
						
						String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_amazon_product/"
				            	+ "?domain_id=" + getDomain().getId()
				            	+ "&username="+ getUser().getLogin()
				            	+ "&description=" + listBox.getSelectedItemText();
						Window.open( fileDownloadURL, "_blank",null);
					}
				};
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();			
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	
	@UiHandler("edit_button")
	void editButton(ClickEvent event){
		EcommerceProduct object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		edit(object);
	}
	
	@UiHandler("delete_button")
	void deleteButton(ClickEvent event){
		EcommerceProduct object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		delete(object);
	}	
	
	private void delete(final EcommerceProduct object){
		Dialog d = new Dialog("Eliminar Plantilla Ecommerce","Borrar",true,"Cancelar",true,"deleteEcommerce");
		d.setEcommerceProduct(object);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				String description = object.getTemplate().getEcommerce() + "-"
						+ object.getTemplate().getType();
				impl.deleteTemplate(getDomain(), getUser(), description, new AsyncCallback<Void>() {
			
					@Override
					public void onSuccess(Void result) {
						getTemplateList().remove(object);
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

	private void edit(EcommerceProduct object){
		ecommerce(object);
	}
	
	/*private void download(EcommerceProduct object){
		Window.alert("download");
	}*/
	
	//------------------------------ SearchBox Utils
	
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
	}
	
	//------------------------------ DataGrid Utils

	private void refreshDataGrid(){
		impl.getProductTemplatesList(getDomain(), getUser(), new AsyncCallback<List<EcommerceProduct>>() {
			
			@Override
			public void onSuccess(List<EcommerceProduct> result) {
				setTemplateList(result);
				addDataDisplay(dataGrid);
				//dataGrid = new DataGrid<EcommerceProduct>(Integer.MAX_VALUE, resources); 
				//loadDataGrid();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void loadDataGrid(){
		DefaultKeyboardSelectionHandler<EcommerceProduct> selHandler = getSelHandler();	
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		addDataDisplay(dataGrid);
		ListHandler<EcommerceProduct> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<EcommerceProduct> selectionModel = new SingleSelectionModel<EcommerceProduct>();
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<EcommerceProduct> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	private DefaultKeyboardSelectionHandler<EcommerceProduct> getSelHandler(){
		return new DefaultKeyboardSelectionHandler<EcommerceProduct>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<EcommerceProduct> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    edit_button.setVisible(true);
				    delete_button.setVisible(true); 
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
	}
	
	private ListDataProvider<EcommerceProduct> dataProvider = new ListDataProvider<EcommerceProduct>();

	public void addDataDisplay(HasData<EcommerceProduct> display) {
		dataProvider = new ListDataProvider<EcommerceProduct>(getTemplateList());
		dataProvider.addDataDisplay(display);
	}

	private ListHandler<EcommerceProduct> getSortHandler() {
		return new ListHandler<EcommerceProduct>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<EcommerceProduct> aux = super.getList();
				List<EcommerceProduct> aux2 = new Vector<EcommerceProduct>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
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

	private class ActionHasCell implements
			HasCell<EcommerceProduct, EcommerceProduct> {
		private ActionCell<EcommerceProduct> cell;
		String s;

		public ActionHasCell(String text, Delegate<EcommerceProduct> delegate) {
			s = text;
			cell = new ActionCell<EcommerceProduct>(text, delegate) {
				String text = s;

				@Override
				public void render(
						com.google.gwt.cell.client.Cell.Context context,
						EcommerceProduct value, SafeHtmlBuilder sb) {
					/*if (text.equals("download")) {
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-google-drive-excel\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
					}*/
					if (text.equals("edit")) {
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-edit\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
					}
					if (text.equals("delete")) {
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
					}
				}
			};
		}

		@Override
		public Cell<EcommerceProduct> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<EcommerceProduct, EcommerceProduct> getFieldUpdater() {
			return null;
		}

		@Override
		public EcommerceProduct getValue(EcommerceProduct object) {
			return object;
		}
	}

	private void initTableColumns(
			final SelectionModel<EcommerceProduct> selectionModel,
			ListHandler<EcommerceProduct> sortHandler) {

		initContextMenu();
		List<HasCell<EcommerceProduct, ?>> cells = new LinkedList<HasCell<EcommerceProduct, ?>>();

		cells.add(new ActionHasCell("edit", new Delegate<EcommerceProduct>() {

			@Override
			public void execute(EcommerceProduct object) {
				// EDIT CODE
				edit(object);
			}
		}));

		cells.add(new ActionHasCell("delete", new Delegate<EcommerceProduct>() {

			@Override
			public void execute(EcommerceProduct object) {
				// DELETE CODE
				delete(object);
			}
		}));

		/*cells.add(new ActionHasCell("download",
				new Delegate<EcommerceProduct>() {

					@Override
					public void execute(EcommerceProduct object) {
						// DOWNLOAD CODE
						download(object);
					}
				}));
		 */
		CompositeCell<EcommerceProduct> cell = new CompositeCell<EcommerceProduct>(
				cells);
		/** Name Column **/
		Column<EcommerceProduct, String> nameColumn = new Column<EcommerceProduct, String>(
				new TextCell()) {

			@Override
			public void render(Context context, EcommerceProduct object,
					SafeHtmlBuilder sb) {

				sb.appendHtmlConstant("<span>" + object.getTemplate().getType()
						+ "</span>");

			}

			@Override
			public String getValue(EcommerceProduct object) {

				return object.getTemplate().getType();
			}

		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn,
				new Comparator<EcommerceProduct>() {

					@Override
					public int compare(EcommerceProduct o1, EcommerceProduct o2) {
						return o1.getTemplate().getType()
								.compareTo(o2.getTemplate().getType());
					}
				});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Nombre");
		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);
		
		
		/** Seller Column **/
		Column<EcommerceProduct, String> sellerColumn = new Column<EcommerceProduct, String>(
				new TextCell()) {

			@Override
			public String getValue(EcommerceProduct object) {
				return object.getTemplate().getSeller();
			}

		};
		sellerColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		sellerColumn.setSortable(true);
		sortHandler.setComparator(sellerColumn,
				new Comparator<EcommerceProduct>() {

					@Override
					public int compare(EcommerceProduct o1, EcommerceProduct o2) {
						return o1.getTemplate().getSeller()
								.compareTo(o2.getTemplate().getSeller());
					}
				});
		dataGrid.getColumnSortList().push(sellerColumn);
		dataGrid.addColumn(sellerColumn, "Vendedor");
		dataGrid.setColumnWidth(sellerColumn, 30, Unit.PCT);

		/** Type Column **/
		Column<EcommerceProduct, String> typeColumn = new Column<EcommerceProduct, String>(
				new TextCell()) {

			@Override
			public String getValue(EcommerceProduct object) {
				return object.getTemplate().getEcommerce();
			}
		};

		typeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		typeColumn.setSortable(true);
		sortHandler.setComparator(typeColumn,
				new Comparator<EcommerceProduct>() {

					@Override
					public int compare(EcommerceProduct o1, EcommerceProduct o2) {
						return o1.getTemplate().getEcommerce()
								.compareTo(o2.getTemplate().getEcommerce());
					}
				});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, "Tipo");
		dataGrid.setColumnWidth(typeColumn, 15, Unit.PCT);

		/** Tag Column **/
		Column<EcommerceProduct, String> tagColumn = new Column<EcommerceProduct, String>(
				new TextCell()) {

			@Override
			public String getValue(EcommerceProduct object) {
				return object.getTemplate().getTag();
			}

		};
		tagColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		tagColumn.setSortable(true);
		sortHandler.setComparator(tagColumn,
				new Comparator<EcommerceProduct>() {

					@Override
					public int compare(EcommerceProduct o1, EcommerceProduct o2) {
						return o1.getTemplate().getTag()
								.compareTo(o2.getTemplate().getTag());
					}
				});
		dataGrid.getColumnSortList().push(tagColumn);
		dataGrid.addColumn(tagColumn, "Etiqueta");
		dataGrid.setColumnWidth(tagColumn, 30, Unit.PCT);
		
		/** ACTION Column **/
		Column<EcommerceProduct, EcommerceProduct> actionColumn = new Column<EcommerceProduct, EcommerceProduct>(cell) {

			@Override
			public EcommerceProduct getValue(EcommerceProduct object) {
				return object;
			}
		};
		actionColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(actionColumn, "Acciones");
		dataGrid.setColumnWidth(actionColumn, 15, Unit.PCT);
	}

	
	//------------------------------ Getters & Setters
	
	public List<EcommerceProduct> getTemplateList(){
		return templateList;
	}
	
	public void setTemplateList(List<EcommerceProduct> templateList){
		this.templateList = templateList;
	}
	
}
