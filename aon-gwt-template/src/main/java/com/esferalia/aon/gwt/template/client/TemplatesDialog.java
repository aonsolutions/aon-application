package com.esferalia.aon.gwt.template.client;




import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
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

public abstract class TemplatesDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, TemplatesDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	

	@UiField(provided = true) FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	
	Integer column = 1;
	HandlerRegistration handler;
	TemplateInfo ti;
	Dialog d;
	TemplateList tlist;
	
	public TemplatesDialog(Dialog dialog) {
		setCaption(dialog.getTitle());
		if(dialog.getTemplateList()!= null) tlist = dialog.getTemplateList();
		label = new Label();
		flex_table = new FlexTable();
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
				if(productCheck() || dialog.getType().equals("delete") || dialog.getType().equals("import"))
					onAccept();
				else {
					label.setText("*Faltan columnas por a\u00f1adir");
					label.setStyleName("aon-check-template");
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
		case "import": importProduct(dialog.getUrl(),dialog.getTemplateList());break;
		default:
			break;
	}
	}
	protected abstract void onAccept();
	
	protected abstract void onCancel();

	private void newTemplate() {
		listBox();
		flexTable();
	}
	
	private void editTemplate(Dialog dialog) {
		listBox();
		listBoxEdit();
		flexTableEdit();
	}
	
	private void deleteTemplate(String name) {
		label.setText("Est\u00e1s seguro de eliminar la plantilla " + name);
	}
	
	private void importProduct(String url,TemplateList templates) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		lb.addItem("-");
		for(TemplateInfo ti : templates.getList()){
			lb.addItem(ti.getName());
		}
		flex_table.setWidget(0, 0, new Label("Plantilla"));
		flex_table.setWidget(0, 1, lb);
		
		SingleUploader upload = newUploader(null, url);
		flex_table.setWidget(1, 0, new Label("Archivo"));
		flex_table.setWidget(1, 1, upload);
		
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
	
	PopupPanel popup;
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
		lb.addItem("Producto");
		flex_table.setWidget(1, 0, new Label("Tipo"));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		info.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				VerticalPanel vp = new VerticalPanel();
				vp.add(new Label("  Columnas Obligatorias:"));
				vp.add(new Label("     Nombre"));
				vp.add(new Label("     C\u00f3digo"));
				vp.add(new Label("     Precio Coste"));
				vp.add(new Label("     Precio Venta Base"));
			
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

		ListBox lb2 = new ListBox();
		for(Integer k = 0;k< list_box.getItemCount();k++){
			lb2.addItem(list_box.getItemText(k));
		}
	
		handler = lb2.addChangeHandler(changeHandler());
		
		flex_table.setWidget(2, 0, new Label("Columna" + " " + Integer.toString(column)));
		flex_table.setWidget(2, 1, lb2);
		flex_table.setWidget(2, 2, new Label(""));
		

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
		flex_table.getCellFormatter().setStyleName(0, 2,
				"aon-panelGrid-aux");	
		flex_table.getCellFormatter().setStyleName(1, 2,
				"aon-panelGrid-aux");	
		flex_table.getCellFormatter().setStyleName(2, 2,
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
		lb.addItem("Producto");
		for(Integer i = 0;i< lb.getItemCount();i++){
			if(lb.getItemText(i).equals(ti.getType())){
				lb.setSelectedIndex(i);
			}
		}
		flex_table.setWidget(1, 0, new Label("Tipo"));
		flex_table.setWidget(1, 1, lb);
		Button info = new Button("");
		info.setStyleName("aon-finding-toolbar-item-template aon-icon-info");
		info.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				VerticalPanel vp = new VerticalPanel();
				vp.add(new Label("  Columnas Obligatorias:"));
				vp.add(new Label("     Nombre"));
				vp.add(new Label("     C\u00f3digo"));
				vp.add(new Label("     Precio Coste"));
				vp.add(new Label("     Precio Venta Base"));
			
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

		for(Integer j = 0; j< ti.getColumns().size();j++){
			column = j+1;
			ListBox lb2 = new ListBox();
			Integer num = null;
			for(Integer k = 0;k< list_box_edit.getItemCount();k++){
				lb2.addItem(list_box_edit.getItemText(k));
				if(lb2.getItemText(k).equals(ti.getColumns().get(j))){
					lb2.setSelectedIndex(k);
					num = k;
				}
			}
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
		if(lbc.getItemText(0).equals("-")){
			lbc.removeItem(0);
			list_box.removeItem(i);
		}
		else{
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
	private void listBox(){
		Vector<String> v = productList();
		list_box = new ListBox();
		list_box.addItem("-");
		for(String s : v){
			list_box.addItem(s);
		}
		max= list_box.getItemCount();
	}
	
	ListBox list_box_edit;
	private void listBoxEdit(){
		Vector<String> v = productList();
		list_box_edit = new ListBox();
		for(String s : v){
			list_box_edit.addItem(s);
		}
	}
	
	private Vector<String> productList(){
		Vector<String> v = new Vector<String>();
		v.add("Nombre");
		v.add("C\u00f3digo");
		v.add("Precio Coste");
		v.add("Precio Venta Base");
		v.add("Categor\u00eda");
		v.add("Marca");
		v.add("Etiqueta");
		v.add("Tipo");
		v.add("IVA");
		v.add("IRPF");
		v.add("Inventoriable");
		v.add("Producto Compuesto");
		v.add("Precio Composici\u00f3n");
		v.add("Estado");
		v.add("C\u00f3digo de Barras");
		v.add("Descripci\u00f3n");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");
		return v;
		
	}
	
	private Boolean productCheck() {
		Integer num = 0;
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(esta(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 4;
	}
	
	private Boolean esta(String s) {
		switch (s) {
		case "Nombre": return true;
		case "C\u00f3digo" : return true;
		case "Precio Coste" : return true;
		case "Precio Venta Base" : return true;
		}
		return false;
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
}