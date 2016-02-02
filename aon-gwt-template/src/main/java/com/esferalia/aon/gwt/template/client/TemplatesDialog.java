package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.gwt.template.shared.WorkPlace;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

public abstract class TemplatesDialog extends CustomDialogB {
	
	final ITemplateAsync item = GWT.create(ITemplate.class);
	public static final String PRODUCT = "Producto";
	public static final String STOCK = "Stock";
	public static final String FEE = "Cuota";
	public static final String CONSUMPTION = "Consumo";
	public static final String CLOSED_INVENTORY = "Inventario Cerrado";
	public static final String VALUED_INVENTORY = "Inventario Valorado";
	
	interface Binder extends UiBinder<Widget, TemplatesDialog>{
		
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField(provided = true)
	protected FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	@UiField(provided = true) VerticalPanel vp;
	
	Integer column = 1;
	HandlerRegistration handler;
	TemplateInfo ti;
	Dialog d;
	TemplateList tlist;
	Vector<Warehouse> w;
	Vector<Series> series;
	Boolean closed;
	
	public TemplatesDialog(Dialog dialog) {
		if(dialog.isClosed() != null) closed = dialog.isClosed();
		setCaption(dialog.getTitle());
		if(dialog.getTemplateList()!= null) tlist = dialog.getTemplateList();
		if(dialog.getWarehouses() != null) w = dialog.getWarehouses();
		if(dialog.getSeries2() != null) series = dialog.getSeries2();
		label = new Label();
		flex_table = new FlexTable();
		vp = new VerticalPanel();
		ti = dialog.getTemplateInfo();
		build(dialog);
		d = dialog;
		setWidget(binder.createAndBindUi(this));
		
		accept_button.setText(dialog.getAccept());
		accept_button.setVisible(dialog.getBoolAccept());
		if(dialog.getType().equals("editEcommerceTemplate") || dialog.getType().equals("importEcommerceTemplate")){
			accept_button.setEnabled(false);
		}
		accept_button.addClickHandler(new ClickHandler() {
			Dialog dialog = d;
			@Override
			public void onClick(ClickEvent event) {
			
				if(dialog.getType().equals("exportCatalogue")){
					ListBox lb1 = (ListBox) flex_table.getWidget(0, 1);
					ListBox lb2 = null;
					if(!lb1.getSelectedItemText().equals("-")) lb2 = (ListBox) flex_table.getWidget(1, 1);
					if(lb1.getSelectedItemText().equals("-") || lb2.getSelectedItemText().equals("-")){
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
					else{
						onAccept();
					}
				}
				else if(dialog.getType().contains("import") || dialog.getType().contains("export") 
						|| dialog.getType().equals("editEcommerceTemplate")){
					if(dialog.getType().equals("importResponse")){
						onAccept();
					}
					else{
						ListBox lb1 = (ListBox) flex_table.getWidget(0, 1);
						Label label1 = (Label) flex_table.getWidget(0, 0);
						if(label1.getText().equals("Plantilla")){
							if(lb1.getSelectedItemText().equals("-")){
								label.setText("*Es necesario seleccionar una plantilla.");
								label.setStyleName("aon-check-template");
							}
							else onAccept();
						}
						else onAccept();
					}
				}
				else{
					if(dialog.getType().equals("new")){
						onAccept();
					}
					else if(dialog.getType().equals("deleteTag") || dialog.getType().equals("editTag")){
						onAccept();
					}
					else if(FeeUtils.feeCheck(dialog,flex_table) || StockUtils.stockCheck(dialog,flex_table) || ProductUtils.productCheck(dialog,flex_table) 
							|| ConsumptionUtils.consumptionCheck(dialog, flex_table)|| InventoryUtils.inventoryCheck(dialog, flex_table) 
							|| dialog.getType().equals("delete") ||  dialog.getType().equals("deleteEcommerce") || dialog.getType().contains("import") 
							|| dialog.getType().contains("export")){
						onAccept();
					}
					else {
						label.setText("*Faltan columnas por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
			}
		});
		
		cancel_button.setText(dialog.getCancel());
		cancel_button.setVisible(dialog.getBoolCancel());
		cancel_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	private void build(Dialog dialog) {
		switch (dialog.getType()) {
		case "new": newTemplate();break;
		case "edit": editTemplate(dialog);break;
		case "editTag": editTag(dialog);break;
		case "import": importar(dialog);break;
		case "export": exportar(dialog);break;
		case "delete": deleteTemplate(dialog.getTemplateInfo().getName());break;
		case "deleteTag": deleteTag(dialog.getTag().getName());break;
		case "deleteEcommerce": deleteTemplate(dialog.getEcommerceProduct().getTemplate().getType());break;
		case "importProduct": importProduct(dialog.getUrl(),dialog.getTemplateList());break;
		case "importStock": importStock(dialog);break;
		case "importTransferStock": importTransferStock(dialog);break;
		case "importResponse": importResponse(dialog.getError());break;
		case "exportProduct": exportProduct(dialog.getTemplateList());break;
		case "exportStock": exportStock(dialog.getTemplateList());break;
		case "exportTransferStock": exportTransferStock(dialog.getTemplateList());break;
		case "importFee": importFee(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportFee": exportFee(dialog.getTemplateList());break;
		case "exportCatalogue": exportCatalogue(dialog.getTemplateList());break;
		case "importProposal": importProposal(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportProposal": exportProposal(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportConsumption": exportConsumption(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportInventory": exportInventory(dialog);break;
		case "exportIncome": exportIncome(dialog);break;
		case "importEcommerceTemplate": importEcommerce(dialog);break;
		case "editEcommerceTemplate": editEcommerce(dialog);break;
		case "exportEcommerce":exportEcommerce(dialog);break;
		default:
			break;
		}
	} 
	
	
	
	private void importar(Dialog dialog){
		dialogAux = dialog;
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem("Producto");
		lb.addItem("Stock");
		lb.addItem("Asignar Cuotas");
		
		lb.addChangeHandler(new ChangeHandler() {
			Dialog dialog = dialogAux;
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				switch (lb.getSelectedItemText()) {
				case "Producto": importProduct(dialog.getUrl(),dialog.getTemplateList());break;
				case "Stock": item.getWarehouses(getDomain(), new AsyncCallback<Vector<Warehouse>>() {
					
					@Override
					public void onSuccess(Vector<Warehouse> result) {
						dialog.setWarehouses(result);
						
						importStock(dialog);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});break;
				case "Asignar Cuotas": importFee(dialog.getUrl(),dialog.getTemplateList());break;
				default:
					break;
				}
			}
		});
		
		flex_table.setWidget(0, 0, new Label("Tipo"));
		flex_table.setWidget(0, 1, lb);
		flexTableCss();
	}
	
	private void importEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox ecommerceListBox = new ListBox();
		for(Ecommerce ecommerce : Ecommerce.values()){
			ecommerceListBox.addItem(ecommerce.getName(), ecommerce.getOrdinalStr());
		}
		Label label1 = new Label("Ecommerce");
		label1.addStyleName("aon-input-required");
		flex_table.setWidget(0, 0, label1);
		flex_table.setWidget(0, 1, ecommerceListBox);
		
		ListBox sellerListBox = new ListBox();
		sellerListBox.addItem("-","-1");
		for(Seller seller : dialog.getSellerList())
			sellerListBox.addItem(seller.getRegistryName(), seller.getId().toString());
		Label label2 = new Label("Vendedor");
		label2.addStyleName("aon-input-required");
		flex_table.setWidget(1, 0, label2);
		flex_table.setWidget(1, 1, sellerListBox);
		
		TextBox typeTextBox = new TextBox();
		typeTextBox.setStyleName("aon-inputText");
		Label label3 = new Label("Tipo");
		label3.addStyleName("aon-input-required");
		flex_table.setWidget(2, 0, label3);
		flex_table.setWidget(2, 1, typeTextBox);
		
		ListBox tagListBox = new ListBox();
		tagListBox.addItem("-");
		for(Tag tag : dialog.getTagList())
			tagListBox.addItem(tag.getName(), tag.getId().toString());
		Label label4 = new Label("Etiqueta");
		label4.addStyleName("aon-input-required");
		flex_table.setWidget(3, 0, label4);
		flex_table.setWidget(3, 1, tagListBox);

		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		Label label5 = new Label("Archivo");
		label5.addStyleName("aon-input-required");
		flex_table.setWidget(4, 0, label5);
		flex_table.setWidget(4, 1, upload);
		
		flexTableCss();
	}
	
	private void exportEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox typeListBox = new ListBox();
		typeListBox.addItem("-","-1");
		for(String type : dialog.getTypeList())
			typeListBox.addItem(type, type);		
		flex_table.setWidget(0, 0, new Label("Tipo"));
		flex_table.setWidget(0, 1, typeListBox);

		flexTableCss();
	}
	
	private void editEcommerce(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox ecommerceListBox = new ListBox();

		String value = "";
		if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.AMAZON.getName()))
			value = Ecommerce.AMAZON.getOrdinalStr();
		else if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.EBAY.getName()))
			value = Ecommerce.EBAY.getOrdinalStr();
		else if(dialog.getEcommerceProduct().getTemplate().getEcommerce().equals(Ecommerce.GENERIC.getName()))
			value = Ecommerce.GENERIC.getOrdinalStr();
		
		ecommerceListBox.addItem(dialog.getEcommerceProduct().getTemplate().getEcommerce(), value);
		ecommerceListBox.setEnabled(false);
		Label label1 = new Label("Ecommerce");
		label1.addStyleName("aon-input-required");
		flex_table.setWidget(0, 0, label1);
		flex_table.setWidget(0, 1, ecommerceListBox);
		
		ListBox sellerListBox = new ListBox();
		sellerListBox.addItem("-","-1");
		Integer index = 1;
		for(Seller seller : dialog.getSellerList()){
			sellerListBox.addItem(seller.getRegistryName(), seller.getId().toString());	
			if(dialog.getEcommerceProduct().getTemplate().getSeller().equals(seller.getRegistryName()))
				sellerListBox.setSelectedIndex(index);
			index++;
		}	
		Label label2 = new  Label("Vendedor");
		label2.addStyleName("aon-input-required");
		flex_table.setWidget(1, 0, label2);
		flex_table.setWidget(1, 1, sellerListBox);
		
		TextBox typeTextBox = new TextBox();
		typeTextBox.setText(dialog.getEcommerceProduct().getTemplate().getType());
		typeTextBox.setEnabled(false);
		typeTextBox.setStyleName("aon-inputText");
		Label label3 = new Label("Tipo");
		label3.addStyleName("aon-input-required");
		flex_table.setWidget(2, 0, label3);
		flex_table.setWidget(2, 1, typeTextBox);
		
		ListBox tagListBox = new ListBox();
		tagListBox.addItem("-");
		Integer index2 = 1;
		for(Tag tag : dialog.getTagList()){
			tagListBox.addItem(tag.getName(), tag.getId().toString());
			if(dialog.getEcommerceProduct().getTemplate().getTag().equals(tag.getName()))
				tagListBox.setSelectedIndex(index2);
			index2++;
		}
		Label label4 = new Label("Etiqueta");
		label4.addStyleName("aon-input-required");
		flex_table.setWidget(3, 0, label4);
		flex_table.setWidget(3, 1, tagListBox);
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		Label label5 = new Label("Archivo");
		label5.addStyleName("aon-input-required");
		flex_table.setWidget(4, 0, label5);
		flex_table.setWidget(4, 1, upload);
		flexTableCss();
	}
	
	Dialog dialogAux;
	private void exportar(Dialog dialog){
		dialogAux = dialog;
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem("Producto");
		lb.addItem("Stock");
		lb.addItem("Catalogo");
		
		lb.addChangeHandler(new ChangeHandler() {
			Dialog dialog = dialogAux;
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				switch (lb.getSelectedItemText()) {
				case "Producto": exportProduct(dialog.getTemplateList());break;
				case "Stock": exportStock(dialog.getTemplateList());break;
				case "Catalogo": exportCatalogue(dialog.getTemplateList());break;
				default:
					break;
				}
			}
		});
		
		flex_table.setWidget(0, 0, new Label("Tipo"));
		flex_table.setWidget(0, 1, lb);
	
		flexTableCss();
	}
	
	private void exportProposal(String url,TemplateList templates){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Stock"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}
	
	private void exportIncome(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : dialog.getTemplateList().getList()){
			if(ti.getType().equals("Stock"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}
	
	private void exportConsumption(String url,TemplateList templates){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Consumo"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);

		
		flexTableCss();
	}

	private void exportInventory(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		
		lb.addItem("-");
		
		for(TemplateInfo ti : dialog.getTemplateList().getList()){
			if(closed && ti.getType().equals("Inventario Cerrado"))
				lb.addItem(ti.getName());
			if(!closed && ti.getType().equals("Inventario Valorado"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		flexTableCss();
	}

	
	private void importProposal(String url,TemplateList templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Stock") )
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
		flexTableCss();
	}
	
	private void exportCatalogue(TemplateList templateList) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb0 = new ListBox();
		lb0.addItem("-");
		
		for(TemplateInfo t : templateList.getList()){
			if(t.getType().equals("Stock"))
				lb0.addItem(t.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb0);
		
		item.getWorkplaces(getDomain(), new AsyncCallback<Vector<WorkPlace>>() {
			
			@Override
			public void onSuccess(Vector<WorkPlace> result) {

				ListBox lb1 = new ListBox();
				lb1.addItem("-");
				for(WorkPlace w : result){
					lb1.addItem(w.getName());
				}
				lbaux = lb1;
				lb1.addChangeHandler(new ChangeHandler() {
					ListBox lb = lbaux;
					@Override
					public void onChange(ChangeEvent event) {
						item.getDepartments(getDomain(), lb.getItemText(lb.getSelectedIndex()), new AsyncCallback<LinkedList<Department>>() {
							
							@Override
							public void onSuccess(LinkedList<Department> result) {
								ListBox lb2 = new ListBox();
								lb2.addItem("-");
								for(Department w : result){
									lb2.addItem(w.getName());
								}
								
								flex_table.setWidget(2, 0, new Label("Departamento"));
								flex_table.setWidget(2, 1, lb2);
								
								flexTableCss();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						} );
						
					}
				});
				flex_table.setWidget(1, 0, new Label("Lugar de Trabajo"));
				flex_table.setWidget(1, 1, lb1);
				
				flexTableCss();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}

	protected abstract void onAccept();
	
	protected abstract void onCancel();

	private void newTemplate() {
		flexTable();
	}
	
	private void editTemplate(Dialog dialog) {
		flexTableEdit();
	}
	
	private void editTag(Dialog dialog){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setText(dialog.getTag().getName());
		flex_table.setWidget(0, 0, new Label("Nombre"));
		flex_table.setWidget(0, 1, tb);
		flexTableCss();
	}
	
	private void deleteTemplate(String name) {
		label.setText("Est\u00e1s seguro de eliminar la plantilla " + name);
	}
	
	private void deleteTag(String name) {
		label.setText("Est\u00e1s seguro de eliminar la plantilla " + name);
	}
	
	private void importResponse(Error error){
		
		if(!error.getError()){
			for(String s : error.getTextError()){
				if(vp.getWidgetCount()< 10){
					Label l = new Label(s);
					l.setStyleName("aon-check-template");
					vp.add(l);
				}
					
			}
			//label.setText(error.getTextError());
	
		}
		else label.setText("Se ha importado correctamente");
	}
	
	private void exportProduct(TemplateList templates){
		
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Producto"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		flexTableCss();
	}
	
	private void exportFee(TemplateList templates){
		//TODO 
	}
	
	private void exportTransferStock(TemplateList templates){
		//TODO 
	}
	
	private void exportStock(TemplateList templates){
		//TODO 
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Stock"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		if(w.size()>1){
			lb2.addItem("-");
			for(Warehouse wh : w){
				lb2.addItem(wh.getName());
			}
		}
		else {
			for(Warehouse wh : w){
				lb2.addItem(wh.getName());
			}
			lb2.setEnabled(false);
		}
		flex_table.setWidget(1, 0, new Label("Almac\u00e9n"));
		flex_table.setWidget(1, 1, lb2);
		
		CheckBox cb = new CheckBox();
		cb.setValue(true);
		
		flex_table.setWidget(2, 0, cb);		
		flex_table.setWidget(2, 1, new Label("Incluir \u00FAnicamente productos con cantidad distinta de cero"));
		
		flexTableCss();
	}
	
	private void importProduct(String url,TemplateList templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Producto"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
		item.getProductRoles(getDomain(), new AsyncCallback<LinkedList<String>>() {
			
			@Override
			public void onSuccess(LinkedList<String> result) {
				ListBox lb2 = new ListBox();
				lb2.addItem("Compra-Venta", "0");
				for (String role : result) {
					if(role.equals("Compra"))
						lb2.addItem("Compra" , "1");
					if(role.equals("Venta"))
						lb2.addItem("Venta" , "2");
				}
				flex_table.setWidget(2, 0, new Label("Tipo"));
				flex_table.setWidget(2, 1, lb2);				
				flexTableCss();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		flexTableCss();
	}
	
	private void importFee(String url,TemplateList templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			if(ti.getType().equals("Cuota"))
				lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url, 1);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
		CheckBox cb = new CheckBox();
		cb.setValue(true);
		
		flex_table.setWidget(2, 0, cb);		
		flex_table.setWidget(2, 1, new Label("Ignorar clientes inactivos"));

		flexTableCss();
	}
	
	private void importStock(Dialog dialog) {
		
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : dialog.getTemplateList().getList()){
			if(ti.getType().equals("Stock"))
				lb.addItem(ti.getName());
		}

		
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		lb2.addItem(dialog.getWarehouseName());
		lb2.setEnabled(false);
		
		flex_table.setWidget(1, 0, new Label("Almacen"));
		flex_table.setWidget(1, 1,lb2 );
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 4);
		flex_table.setWidget(4, 0, new Label("Archivo"));
		flex_table.setWidget(4, 1, upload);
		
		flexTableCss();
	}
	private Boolean esta(Series series,Vector<Series> series2){
		for (Series series3 : series2) {
			if(series3.getName().equals(series.getName()))
				return true;
		}
		return false;
	}
	private void importTransferStock(Dialog dialog) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : dialog.getTemplateList().getList()){
			if(ti.getType().equals("Stock"))
				lb.addItem(ti.getName());
		}

		
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		for(Warehouse wh : w){
			lb2.addItem(wh.getName());
		}
		lb2.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(1, 1);
				if(!lb.getSelectedItemText().equals("-")){
				item.getSeries(getDomain(), lb.getSelectedItemText(), new AsyncCallback<Vector<Series>>() {
					
					@Override
					public void onSuccess(Vector<Series> result) {
						
						ListBox lb4 = (ListBox) flex_table.getWidget(2, 1);
						String w2 = lb4.getSelectedItemText();
						
						if(!w2.equals("-")){
							
							series = new Vector<Series>();
							series.addAll(result);
							item.getSeries(getDomain(), w2, new AsyncCallback<Vector<Series>>() {
								Vector<Series> series2 = series;
								@Override
								public void onSuccess(Vector<Series> result) {
									
									for (Series series : result) {
										if(!esta(series, series2)){
											series2.add(series);
										}
									}
									ListBox lb3 = new ListBox();
									if(series2.size()>1)lb3.addItem("-");
									for(Series s : series2){
										lb3.addItem(s.getName());
									}
									flex_table.setWidget(3, 1,lb3 );
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
						else{
							ListBox lb3 = new ListBox();
							if(result.size()>1) lb3.addItem("-");
							for(Series s : result){
								lb3.addItem(s.getName());
							}
							flex_table.setWidget(3, 1,lb3 );
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				}
			}
		});
		
		ListBox lb2Aux = new ListBox();
		lb2Aux.addItem(dialog.getWarehouses().get(0).getName());
		lb2Aux.setEnabled(false);
		flex_table.setWidget(1, 0, new Label("Almac\u00e9n Origen"));
		flex_table.setWidget(1, 1,lb2Aux );
		
		
		ListBox lb4 = new ListBox();
		lb4.addItem("-");
		for(Warehouse wh : w){
			lb4.addItem(wh.getName());
		}
		lb4.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(2, 1);
				item.getSeries(getDomain(), lb.getSelectedItemText(), new AsyncCallback<Vector<Series>>() {
					
					@Override
					public void onSuccess(Vector<Series> result) {
						
						ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
						String w2 = lb2.getSelectedItemText();
						
						if(!w2.equals("-")){
							series = new Vector<Series>();
							series.addAll(result);
							item.getSeries(getDomain(), w2, new AsyncCallback<Vector<Series>>() {
								Vector<Series> series2 = series;
								@Override
								public void onSuccess(Vector<Series> result) {
									for (Series series : result) {
										if(!esta(series, series2)){
											series2.add(series);
										}
									}
									ListBox lb3 = new ListBox();
									if(series2.size()>1)lb3.addItem("-");
									for(Series s : series2){
										lb3.addItem(s.getName());
									}
									flex_table.setWidget(3, 1,lb3 );
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
						else{
							ListBox lb3 = new ListBox();
							if(result.size()>1) lb3.addItem("-");
							for(Series s : result){
								lb3.addItem(s.getName());
							}
							flex_table.setWidget(3, 1,lb3 );
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		});
		
		
		ListBox lb4Aux = new ListBox();
		lb4Aux.addItem(dialog.getWarehouses().get(1).getName());
		lb4Aux.setEnabled(false);
		flex_table.setWidget(2, 0, new Label("Almac\u00e9n Destino"));
		flex_table.setWidget(2, 1,lb4Aux );
		
		ListBox lb3 = new ListBox();
		lb3.addItem("-");
		/*for(String s : series){
			lb3.addItem(s);
		}*/
		ListBox lb3Aux = new ListBox();
		lb3Aux.addItem(dialog.getSeries2().get(0).getName());
		lb3Aux.setEnabled(false);
		flex_table.setWidget(3, 0, new Label("Serie"));
		flex_table.setWidget(3, 1,lb3Aux );
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(4, 0, new Label("Comentarios"));
		flex_table.setWidget(4, 1, tb);
		
		SingleUploader upload = newUploader(null, dialog.getUrl(), 5);
		flex_table.setWidget(5, 0, new Label("Archivo"));
		flex_table.setWidget(5, 1, upload);
		
		flexTableCss();
	}
	
	PopupPanel popup;
	ListBox lbaux;
	public void flexTable() {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(0, 0, new Label("Nombre"));
		flex_table.setWidget(0, 1, tb);
		flex_table.setWidget(0, 2, new Label(""));
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem(PRODUCT);
		lb.addItem(STOCK);
		lb.addItem(FEE);
		lb.addItem(CONSUMPTION);
		lb.addItem(CLOSED_INVENTORY);
		lb.addItem(VALUED_INVENTORY);

		lbaux = lb;
		lb.addChangeHandler(new ChangeHandler() {
			ListBox lb = lbaux;
			@Override
			public void onChange(ChangeEvent event) {
				column = 1;
				for(Integer i = flex_table.getRowCount(); i>2;i--)
					flex_table.removeRow(i-1);
				
				// Genera la lista de atributos del tipo de plantilla seleccionada.
				
				
				Integer index = mandatoryWidget(2, lb.getSelectedItemText());
				
				optionalListBox(index, lb.getSelectedItemText());	
				
				ListBox lb2 = new ListBox();
				for(Integer k = 0;k< list_box.getItemCount();k++)
					lb2.addItem(list_box.getItemText(k));
				
				handler = lb2.addChangeHandler(changeHandler());

				flex_table.setWidget(index, 0, new Label("Columna" + " " + Integer.toString(column)));
				flex_table.setWidget(index, 1, lb2);
				flex_table.setWidget(index, 2, new Label(""));
				
				flex_table.getCellFormatter().setStyleName(index, 0,
						"aon-panelGrid-odd");
				flex_table.getCellFormatter().setStyleName(index, 1,
						"aon-panelGrid-even");
				flex_table.getCellFormatter().setStyleName(index, 2,
						"aon-panelGrid-aux");	
			}
		});
		flex_table.setWidget(1, 0, new Label("Tipo"));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		info.addMouseOverHandler(new MouseOverHandler() {
			ListBox lb = lbaux;
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Boolean b = true;
				VerticalPanel vp = new VerticalPanel();
				vp.addStyleName("aon-info-content-template");
				if(lb.getItemText(lb.getSelectedIndex()).equals("Producto")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Nombre");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r2 = new Label("C\u00f3digo");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r3 = new Label("Precio Coste");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Precio Venta Base");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Stock")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					//Label r2 = new Label("Almac\u00e9n Destino");r2.addStyleName("aon-info-rest-template");
					//vp.add(r2);
					Label r3 = new Label("Cantidad");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Cuota")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Cliente");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r2 = new Label("Producto");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r3 = new Label("Cantidad");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Precio");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					Label r5 = new Label("Descuento");r5.addStyleName("aon-info-rest-template");
					vp.add(r5);
					Label r6 = new Label("Fecha Inicio");r6.addStyleName("aon-info-rest-template");
					vp.add(r6);
					Label r7 = new Label("Fecha Facturaci\u00f3n");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Consumo")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r3 = new Label("Inicial");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Compras");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					Label r2 = new Label("Ventas");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r5 = new Label("Final");r5.addStyleName("aon-info-rest-template");
					vp.add(r5);
					Label r6 = new Label("Traspaso");r6.addStyleName("aon-info-rest-template");
					vp.add(r6);
					Label r7 = new Label("Precio");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
					Label r8 = new Label("Valor Consumo");r8.addStyleName("aon-info-rest-template");
					vp.add(r8);
					Label r9 = new Label("Consumo");r9.addStyleName("aon-info-rest-template");
					vp.add(r9);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).contains("Inventario")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r3 = new Label("Categor\u00eda");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Inventario");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					closed = lb.getItemText(lb.getSelectedIndex()).equals("Inventario Cerrado");
					if(closed){
						Label r5 = new Label("Recuento");r5.addStyleName("aon-info-rest-template");
						vp.add(r5);
					}
					else{
						Label r6 = new Label("Coste");r6.addStyleName("aon-info-rest-template");
						vp.add(r6);
						Label r7 = new Label("Total");r7.addStyleName("aon-info-rest-template");
						vp.add(r7);
					}
				}
				
				else b = false;
				if(b){
					popup = new PopupPanel();
					popup.setWidget(vp);
					popup.setStyleName("aon-popup-aux-template");
					popup.setPopupPosition(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
					popup.show();
				}
			}
		});
		info.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				popup.hide();
			}
		});
		
		flex_table.setWidget(1, 2, info);
		
		flexTableCss();
		flex_table.getCellFormatter().setStyleName(0, 2,
				"aon-panelGrid-aux");	
		flex_table.getCellFormatter().setStyleName(1, 2,
				"aon-panelGrid-aux");	

	}
	
	public void flexTableEdit() {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setText(ti.getName());
		flex_table.setWidget(0, 0, new Label("Nombre"));
		flex_table.setWidget(0, 1, tb);
		flex_table.setWidget(0, 2, new Label(""));
		ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem("Producto");
		lb.addItem("Stock");
		lb.addItem("Cuota");
		lb.addItem("Consumo");
		lb.addItem("Inventario Cerrado");
		lb.addItem("Inventario Valorado");
		for(Integer i = 0;i< lb.getItemCount();i++){
			if(lb.getItemText(i).equals(ti.getType())){
				lb.setSelectedIndex(i);
			}
		}
		lb.setEnabled(false);
		flex_table.setWidget(1, 0, new Label("Tipo"));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		lbaux = lb;
		info.addMouseOverHandler(new MouseOverHandler() {
			ListBox lb = lbaux;
			@Override
			public void onMouseOver(MouseOverEvent event) {
				VerticalPanel vp = new VerticalPanel();

				if(lb.getItemText(lb.getSelectedIndex()).equals("Producto")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Nombre");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r2 = new Label("C\u00f3digo");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r3 = new Label("Precio Coste");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Precio Venta Base");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Stock")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					//Label r2 = new Label("Almac\u00e9n Destino");r2.addStyleName("aon-info-rest-template");
					//vp.add(r2);
					Label r3 = new Label("Cantidad");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Cuota")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Cliente");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r2 = new Label("Producto");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r3 = new Label("Cantidad");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Precio");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					Label r5 = new Label("Descuento");r5.addStyleName("aon-info-rest-template");
					vp.add(r5);
					Label r6 = new Label("Fecha Inicio");r6.addStyleName("aon-info-rest-template");
					vp.add(r6);
					Label r7 = new Label("Fecha Facturaci\u00f3n");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Consumo")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r3 = new Label("Inicial");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Compras");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					Label r2 = new Label("Ventas");r2.addStyleName("aon-info-rest-template");
					vp.add(r2);
					Label r5 = new Label("Final");r5.addStyleName("aon-info-rest-template");
					vp.add(r5);
					Label r6 = new Label("Traspaso");r6.addStyleName("aon-info-rest-template");
					vp.add(r6);
					Label r7 = new Label("Precio");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
					Label r8 = new Label("Valor Consumo");r8.addStyleName("aon-info-rest-template");
					vp.add(r8);
					Label r9 = new Label("Consumo");r9.addStyleName("aon-info-rest-template");
					vp.add(r9);
				}
				else if(lb.getItemText(lb.getSelectedIndex()).contains("Inventario")){
					Label title = new Label("Columnas Obligatorias:");
					title.addStyleName("aon-info-title-template");
					vp.add(title);
					Label r1 = new Label("Producto");r1.addStyleName("aon-info-rest-template");
					vp.add(r1);
					Label r3 = new Label("Categor\u00eda");r3.addStyleName("aon-info-rest-template");
					vp.add(r3);
					Label r4 = new Label("Inventario");r4.addStyleName("aon-info-rest-template");
					vp.add(r4);
					lb.getItemText(lb.getSelectedIndex()).equals("Inventario Cerrado");
					if(closed){
						Label r5 = new Label("Recuento");r5.addStyleName("aon-info-rest-template");
						vp.add(r5);
					}
					else{
						Label r6 = new Label("Coste");r6.addStyleName("aon-info-rest-template");
						vp.add(r6);
						Label r7 = new Label("Total");r7.addStyleName("aon-info-rest-template");
						vp.add(r7);
					}
				}
				popup = new PopupPanel();
				popup.setWidget(vp);
				popup.setStyleName("aon-popup-aux-template");
				popup.setPopupPosition(event.getNativeEvent().getClientX(),event.getNativeEvent().getClientY());
				popup.show();
			}
		});
		info.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				popup.hide();
			}
		});
		flex_table.setWidget(1, 2, info);
		listBox(ti.getType());
		listBoxEdit(ti.getType());
		Integer cont = 0;
		for(Integer j = 0; j< ti.getColumns().size();j++){
			column = j+1;
			ListBox lb2 = new ListBox();
			Integer num = null;
			for(Integer k = 0;k< list_box_edit.getItemCount();k++){
				lb2.addItem(list_box_edit.getItemText(k));
				if(lb2.getItemText(k).equals(ti.getColumns().get(j))){
					lb2.setSelectedIndex(k);
					cont++;
					if(cont < ti.getColumns().size())list_box.removeItem(k+1);
					
					num = k;
				}
			}
			/*for(Integer h = 0;h<list_box.getItemCount();h++){
				String s = lb2.getItemText(lb2.getSelectedIndex());
				if(list_box.getItemText(h).equals(s)){
					list_box.removeItem(h);
				}
			}*/
			if(num!=null) list_box_edit.removeItem(num);
			flex_table.setWidget(j+2, 0, new Label("Columna" + " " + Integer.toString(j+1)));
			flex_table.setWidget(j+2, 1, lb2);
			if(j != ti.getColumns().size()-1){

				flex_table.setWidget(j+2, 2, new Label(""));
				lb2.setEnabled(false);
			}
			else{
				if(column <max-1){
					handler = lb2.addChangeHandler(changeHandler());
					HorizontalPanel hp = new HorizontalPanel();
					if(j!=0){
						Button b = new Button("");
						b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
						b.addClickHandler(removeClickHandler());
						hp.add(b);
					}
					Button b2 = new Button("");
					b2.setStyleName("aon-finding-toolbar-item-template aon-search-add");
					b2.addClickHandler(addClickHandler());
					hp.add(b2);
					flex_table.setWidget(j+2, 2,hp);
					
				}
				else{
					Button b = new Button("");
					b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
					b.addClickHandler(removeClickHandler());
					flex_table.setWidget(j+2, 2,b);
				}
			}
		}
		

		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					flex_table.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
			flex_table.getCellFormatter().setStyleName(i, 2,
					"aon-panelGrid-aux");
		}
	}

	public ChangeHandler changeHandler(){
		ChangeHandler ch = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handler();
			}
		};
		return ch;
	}

	
	private ClickHandler removeClickHandler() {
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				flex_table.removeRow(column+1);
				column--;
				ListBox l =(ListBox)flex_table.getWidget(column+1, 1);
				l.setEnabled(true);

				list_box = new ListBox();
				list_box.addItem("-");
				for (Integer j = 0; j<l.getItemCount();j++) {
					list_box.addItem(l.getItemText(j));	
				}
				
				handler = l.addChangeHandler(changeHandler());
				HorizontalPanel h = new HorizontalPanel();
				if(flex_table.getRowCount()>3){
					Button b = new Button("");
					b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
					b.addClickHandler(removeClickHandler());
					h.add(b);
				}
				Button b2 = new Button("");
				b2.setStyleName("aon-finding-toolbar-item-template aon-search-add");
				b2.addClickHandler(addClickHandler());
				h.add(b2);
				
				flex_table.setWidget(column+1,2, h);
				flex_table.getCellFormatter().setStyleName(column+1, 2,
						"aon-panelGrid-aux");	
			}
		};
		return ch;
	}
	
	private ClickHandler addClickHandler(){
		ClickHandler ch = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				handler();
			}
		};
		return ch;
	}
	
	private void handler(){
		ListBox lbc = (ListBox) flex_table.getWidget(column+1, 1);
		Integer i = lbc.getSelectedIndex();
		Boolean libre = lbc.getItemText(i).equals("Texto Libre");
		if(lbc.getItemText(0).equals("-")){
			lbc.removeItem(0);
			if(!libre){
				list_box.removeItem(i);
			}
		}
		else{
			if(!libre)
				list_box.removeItem(i+1);
		}
		
		handler.removeHandler();
		
		ListBox lb = new ListBox();
		for(Integer k = 0;k< list_box.getItemCount();k++){
			lb.addItem(list_box.getItemText(k));
		}
		handler  = lb.addChangeHandler(changeHandler());
		if(column < max-1){
			lbc.setEnabled(false);
			flex_table.setWidget(column+1, 2, new Label(""));

			column++;
			Button b = new Button("");
			b.setStyleName("aon-finding-toolbar-item-template aon-search-minus");
			b.addClickHandler(removeClickHandler());
		
			flex_table.setWidget(column+1, 0, new Label("Columna" + " " + Integer.toString(column)));
			flex_table.setWidget(column+1, 1, lb);
			flex_table.setWidget(column+1, 2, b);
		
			flex_table.getCellFormatter().setStyleName(column+1, 0,
				"aon-panelGrid-odd");
			flex_table.getCellFormatter().setStyleName(column+1, 1,
				"aon-panelGrid-even");
			flex_table.getCellFormatter().setStyleName(column+1, 2,
					"aon-panelGrid-aux");				
			
		}
	}
	
	
	ListBox list_box;
	Integer max;
	private void listBox(String type){
		Vector<String> v = new Vector<String>();
		if(type.equals("Producto"))
			v = ProductUtils.productList();
		else if(type.equals("Stock"))
			v = StockUtils.stockList();
		else if(type.equals("Cuota"))
			v = FeeUtils.feeList();
		else if(type.equals("Consumo"))
			v = ConsumptionUtils.consumptionList();
		else if(type.equals("Inventario Cerrado"))
			v = InventoryUtils.inventoryCloseList();
		else if(type.equals("Inventario Valorado"))
			v = InventoryUtils.inventoryList();
		list_box = new ListBox();
		list_box.addItem("-");
		for(String s : v){
			list_box.addItem(s);
		}
		max= list_box.getItemCount();
	}
	
	ListBox list_box_edit;
	private void listBoxEdit(String type){
		Vector<String> v = new Vector<String>();
		if(type.equals("Producto"))
			v = ProductUtils.productList();
		else if(type.equals("Stock"))
			v = StockUtils.stockList();
		else if(type.equals("Cuota"))
			v = FeeUtils.feeList();
		else if(type.equals("Consumo"))
			v = ConsumptionUtils.consumptionList();
		else if(type.equals("Inventario Valorado"))
			v = InventoryUtils.inventoryList();
		else if(type.equals("Inventario Cerrado"))
			v = InventoryUtils.inventoryCloseList();
		list_box_edit = new ListBox();
		for(String s : v){
			list_box_edit.addItem(s);
		}
	}
	
	private void optionalListBox(Integer index, String type){
		Vector<String> v = new Vector<String>();
		if(type.equals("Producto"))
			v = ProductUtils.productOptionalList();
		else if(type.equals("Stock"))
			v = StockUtils.stockOptionalList();
		else if(type.equals("Cuota"))
			v = FeeUtils.feeOptionalList();
		else if(type.equals("Consumo"))
			v = ConsumptionUtils.consumptionOptionalList();
		else if(type.equals("Inventario Cerrado"))
			v = InventoryUtils.inventoryCloseOptionalList();
		else if(type.equals("Inventario Valorado"))
			v = InventoryUtils.inventoryOptionalList();
		list_box = new ListBox();
		list_box.addItem("-");
		for(String s : v){
			list_box.addItem(s);
		}
		max= list_box.getItemCount() + index -2;
	}
	
	long progress = 10;
	Integer rowAux;
	String urlAux;
	
	
	public  SingleUploader newUploader(SingleUploader up,String url, Integer row){
		rowAux = row;urlAux = url;
		final SingleUploader upload;
       	if(up==null){
       		 upload=  new SingleUploader(FileInputType.BROWSER_INPUT.with(FileInputType.LABEL.getInstance()));
       	}
       	else{
       		 upload = up;
       	}
       	upload.setAutoSubmit(true);
        upload.setServletPath(url + "/gwt_upload");
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url + "/gwt_upload");
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
       
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	Integer row = rowAux;
        	String url = urlAux;
        	Dialog dialog = d;
        	@Override
			public void onCancel(IUploader uploader) {
        		//Window.alert("lalalala error");
        		SingleUploader upload = newUploader(null, url,row);
        		flex_table.setWidget(row, 1, upload);
        		if(dialog.getType().equals("editEcommerceTemplate") || dialog.getType().equals("importEcommerceTemplate")){
        			accept_button.setEnabled(false);
        		}
        		// reset out of TemplatesServlet!!!
        		
			}
		});
        
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
				//Window.alert("finish");o
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
				accept_button.setEnabled(true);
			}
		});
        upload.getForm().addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				upload.getForm().getWidget().getElement().getChild(0).removeFromParent();				
			}
		});
        
        return upload;
	}
	
	
	public void flexTableCss(){
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					flex_table.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	}
	
	public Integer mandatoryWidget(Integer index, String type) {
		switch (type) {
		case PRODUCT:
			setLabel(index, ProductUtils.PRODUCT_NAME);
			setLabel(index+1, ProductUtils.PRODUCT_CODE);
			setLabel(index+2, ProductUtils.PRODUCT_PRICE_COST);
			setLabel(index+3, ProductUtils.PRODUCT_SALE_BASE);
			return index+4;
		case STOCK:
			setLabel(index, StockUtils.STOCK_PRODUCT);
			setLabel(index+1, StockUtils.STOCK_QUANTITY);
			return index+2;
		case FEE:
			setLabel(index, FeeUtils.FEE_CLIENT);
			setLabel(index+1, FeeUtils.FEE_PRODUCT);
			setLabel(index+2, FeeUtils.FEE_QUANTITY);
			setLabel(index+3, FeeUtils.FEE_PRICE);
			setLabel(index+4, FeeUtils.FEE_DISCOUNT);
			setLabel(index+5, FeeUtils.FEE_START_DATE);
			setLabel(index+6, FeeUtils.FEE_BILLING_DATE);
			return index+7;
		case CONSUMPTION:
			setLabel(index, ConsumptionUtils.CONSUMPTION_PRODUCT);
			setLabel(index+1, ConsumptionUtils.CONSUMPTION_INITIAL);
			setLabel(index+2, ConsumptionUtils.CONSUMPTION_PURCHASES);
			setLabel(index+3, ConsumptionUtils.CONSUMPTION_SALES);
			setLabel(index+4, ConsumptionUtils.CONSUMPTION_FINAL);
			setLabel(index+5, ConsumptionUtils.CONSUMPTION_TRANSFER);
			//setLabel(index+6, ConsumptionUtils.CONSUMPTION_PRICE);
			setLabel(index+6, ConsumptionUtils.CONSUMPTION_CONSUMPTION_VALUE);
			setLabel(index+7, ConsumptionUtils.CONSUMPTION_CONSUMPTION);
			return index+8;
		case CLOSED_INVENTORY:
			setLabel(index, InventoryUtils.INVENTORY_PRODUCT);
			setLabel(index+1, InventoryUtils.INVENTORY_CATEGORY);
			setLabel(index+2, InventoryUtils.INVENTORY_INVENTORY);
			setLabel(index+3, InventoryUtils.INVENTORY_COUNT);
			return index+4;
		case VALUED_INVENTORY:
			setLabel(index, InventoryUtils.INVENTORY_PRODUCT);
			setLabel(index+1, InventoryUtils.INVENTORY_CATEGORY);
			setLabel(index+2, InventoryUtils.INVENTORY_INVENTORY);
			setLabel(index+3, InventoryUtils.INVENTORY_COST);
			setLabel(index+4, InventoryUtils.INVENTORY_TOTAL);
			return index+5;
		default:
			return index;
		}
	}
	
	public void setLabel(Integer index, String name){
		flex_table.setWidget(index, 0, new Label("Columna" + " " + Integer.toString(column)));
		flex_table.setWidget(index, 1, new Label(name));
		flex_table.setWidget(index, 2, new Label(""));
		
		flex_table.getCellFormatter().setStyleName(index, 0,
				"aon-panelGrid-odd");
		flex_table.getCellFormatter().setStyleName(index, 1,
				"aon-panelGrid-even");
		flex_table.getCellFormatter().setStyleName(index, 2,
				"aon-panelGrid-aux");	
		column++;
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
}