package com.esferalia.aon.gwt.template.client;




import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.template.shared.Department;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.gwt.template.shared.WorkPlace;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class TemplatesDialog extends CustomDialogB {
	
	final ITemplateAsync item = GWT.create(ITemplate.class);
	
	interface Binder extends UiBinder<Widget, TemplatesDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	

	@UiField(provided = true) FlexTable flex_table;
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
	public TemplatesDialog(Dialog dialog) {
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
				else{
				
					if(FeeUtils.feeCheck(dialog,flex_table) || StockUtils.stockCheck(dialog,flex_table) || ProductUtils.productCheck(dialog,flex_table) || dialog.getType().equals("delete") || dialog.getType().contains("import") 
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
		case "delete": deleteTemplate(dialog.getTemplateInfo().getName());break;
		case "importProduct": importProduct(dialog.getUrl(),dialog.getTemplateList());break;
		case "importStock": importStock(dialog);break;
		case "importTransferStock": importTransferStock(dialog.getUrl(),dialog.getTemplateList());break;
		case "importResponse": importResponse(dialog.getError());break;
		case "exportProduct": exportProduct(dialog.getTemplateList());break;
		case "exportStock": exportStock(dialog.getTemplateList());break;
		case "exportTransferStock": exportTransferStock(dialog.getTemplateList());break;
		case "importFee": importFee(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportFee": exportFee(dialog.getTemplateList());break;
		case "exportCatalogue": exportCatalogue(dialog.getTemplateList());break;
		case "importProposal": importProposal(dialog.getUrl(),dialog.getTemplateList());break;
		case "exportProposal": exportProposal(dialog.getUrl(),dialog.getTemplateList());break;
		default:
			break;
		}
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

	
	private void importProposal(String url,TemplateList templates) {
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
		
		SingleUploader upload = newUploader(null, url);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
		flexTableCss();
	}
	
	private void exportCatalogue(TemplateList templateList) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		item.getWorkplaces(new AsyncCallback<Vector<WorkPlace>>() {
			
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
						item.getDepartments(lb.getItemText(lb.getSelectedIndex()), new AsyncCallback<Vector<Department>>() {
							
							@Override
							public void onSuccess(Vector<Department> result) {
								ListBox lb2 = new ListBox();
								lb2.addItem("-");
								for(Department w : result){
									lb2.addItem(w.getName());
								}
								
								flex_table.setWidget(1, 0, new Label("Departamento"));
								flex_table.setWidget(1, 1, lb2);
								
								flexTableCss();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						} );
						
					}
				});
				flex_table.setWidget(0, 0, new Label("Lugar de Trabajo"));
				flex_table.setWidget(0, 1, lb1);
				
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
	
	private void deleteTemplate(String name) {
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
		
		SingleUploader upload = newUploader(null, url);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
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
		
		SingleUploader upload = newUploader(null, url);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
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

		ListBox lb3 = new ListBox();
		if(series.size()>1){

			lb3.addItem("-");
		}
		for(Series s : series){

			lb3.addItem(s.getName());
		}
		flex_table.setWidget(2, 0, new Label("Serie"));
		flex_table.setWidget(2, 1,lb3 );
		

		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(3, 0, new Label("Comentarios"));
		flex_table.setWidget(3, 1, tb);
		
		SingleUploader upload = newUploader(null, dialog.getUrl());
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
	private void importTransferStock(String url,TemplateList templates) {
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
		lb2.addItem("-");
		for(Warehouse wh : w){
			lb2.addItem(wh.getName());
		}
		lb2.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(1, 1);
				if(!lb.getSelectedItemText().equals("-")){
				item.getSeries(lb.getSelectedItemText(), new AsyncCallback<Vector<Series>>() {
					
					@Override
					public void onSuccess(Vector<Series> result) {
						
						ListBox lb4 = (ListBox) flex_table.getWidget(2, 1);
						String w2 = lb4.getSelectedItemText();
						
						if(!w2.equals("-")){
							
							series = new Vector<Series>();
							series.addAll(result);
							item.getSeries(w2, new AsyncCallback<Vector<Series>>() {
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
		
		flex_table.setWidget(1, 0, new Label("Almac\u00e9n Origen"));
		flex_table.setWidget(1, 1,lb2 );
		
		
		ListBox lb4 = new ListBox();
		lb4.addItem("-");
		for(Warehouse wh : w){
			lb4.addItem(wh.getName());
		}
		lb4.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox)flex_table.getWidget(2, 1);
				item.getSeries(lb.getSelectedItemText(), new AsyncCallback<Vector<Series>>() {
					
					@Override
					public void onSuccess(Vector<Series> result) {
						
						ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
						String w2 = lb2.getSelectedItemText();
						
						if(!w2.equals("-")){
							series = new Vector<Series>();
							series.addAll(result);
							item.getSeries(w2, new AsyncCallback<Vector<Series>>() {
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
		
		flex_table.setWidget(2, 0, new Label("Almac\u00e9n Destino"));
		flex_table.setWidget(2, 1,lb4 );
		
		ListBox lb3 = new ListBox();
		lb3.addItem("-");
		/*for(String s : series){
			lb3.addItem(s);
		}*/
		flex_table.setWidget(3, 0, new Label("Serie"));
		flex_table.setWidget(3, 1,lb3 );
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		flex_table.setWidget(4, 0, new Label("Comentarios"));
		flex_table.setWidget(4, 1, tb);
		
		SingleUploader upload = newUploader(null, url);
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
		lb.addItem("Producto");
		lb.addItem("Stock");
		lb.addItem("Cuota");
		lbaux = lb;
		lb.addChangeHandler(new ChangeHandler() {
			ListBox lb = lbaux;
			@Override
			public void onChange(ChangeEvent event) {
				column = 1;
				for(Integer i = flex_table.getRowCount(); i>2;i--)
					flex_table.removeRow(i-1);
				
				if(lb.getItemText(lb.getSelectedIndex()).equals("Producto"))
					listBox("Producto");
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Stock"))
					listBox("Stock");
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Cuota"))
					listBox("Cuota");
				
				ListBox lb2 = new ListBox();
				for(Integer k = 0;k< list_box.getItemCount();k++)
					lb2.addItem(list_box.getItemText(k));
				
			
				handler = lb2.addChangeHandler(changeHandler());
				
				flex_table.setWidget(2, 0, new Label("Columna" + " " + Integer.toString(column)));
				flex_table.setWidget(2, 1, lb2);
				flex_table.setWidget(2, 2, new Label(""));
				
				flex_table.getCellFormatter().setStyleName(2, 0,
						"aon-panelGrid-odd");
				flex_table.getCellFormatter().setStyleName(2, 1,
						"aon-panelGrid-even");
				flex_table.getCellFormatter().setStyleName(2, 2,
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
					Label r1 = new Label("Product");r1.addStyleName("aon-info-rest-template");
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
					Label r7 = new Label("Fecha Facturación");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
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
					vp.add(new Label("Columnas Obligatorias:"));
					vp.add(new Label("Nombre"));
					vp.add(new Label("C\u00f3digo"));
					vp.add(new Label("Precio Coste"));
					vp.add(new Label("Precio Venta Base"));
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Stock")){
					vp.add(new Label("Columnas Obligatorias:"));
					vp.add(new Label("Product"));
					vp.add(new Label("Almac\u00e9n Destino"));
					vp.add(new Label("Cantidad"));
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
					Label r7 = new Label("Fecha Facturación");r7.addStyleName("aon-info-rest-template");
					vp.add(r7);
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
		list_box_edit = new ListBox();
		for(String s : v){
			list_box_edit.addItem(s);
		}
	}
	
	long progress = 10;
	public  SingleUploader newUploader(SingleUploader up,String url){
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
}