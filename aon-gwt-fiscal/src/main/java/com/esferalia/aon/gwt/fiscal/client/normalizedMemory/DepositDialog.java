package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.SingleUploader;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class DepositDialog extends CustomDialogB {
	
	interface Binder extends UiBinder<Widget, DepositDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true) FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	@UiField(provided = true) VerticalPanel vp;
	String typeAux;
	public DepositDialog(String title, String type, Enterprise enterprise, String url, Vector<MemoryTemplate> mts) {
		setCaption(title);
		label = new Label();
		flex_table = new FlexTable();
		vp = new VerticalPanel();
		typeAux = type;
		switch (type) {
		case "new": newDeposit(enterprise);break;
		case "new2": newDeposit2();break;
		case "import": importar(enterprise, url);break;
		case "importText": importarTextos(mts);break;
		case "importAll": importAll(mts, enterprise, url); break;
		case "delete": label.setText("Esta seguro de eliminar el Deposito");
		default:
			break;
		}
		
		setWidget(binder.createAndBindUi(this));
		
		if(type.equals("new")) accept_button.setText("Nuevo");
		if(type.equals("new2")) accept_button.setText("Nuevo");
		if(type.equals("import")) accept_button.setText("Importar");
		if(type.equals("importText")) accept_button.setText("Importar");
		if(type.equals("importAll")) accept_button.setText("Importar");
		if(type.equals("delete")) accept_button.setText("Eliminar");
		accept_button.setVisible(true);
		accept_button.addClickHandler(new ClickHandler() {
			String type = typeAux;
			@Override
			public void onClick(ClickEvent event) {
				if(type.equals("new")){
					ListBox lb = (ListBox)flex_table.getWidget(0, 1);
					TextBox tb1 = (TextBox) flex_table.getWidget(1, 1);
					TextBox tb2 = (TextBox) flex_table.getWidget(4, 1);

					if(!lb.getSelectedItemText().equals("-") && 
						!tb1.getText().equals("") && !tb2.getText().equals("")){
						onAccept();	
					}
					else{
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
				if(type.equals("delete")){
					onAccept();
				}
				if(type.equals("importAll")){
					onAccept();
				}
				if(type.equals("import")){
					onAccept();
				}
				if(type.equals("importText")){
					ListBox lb =(ListBox) flex_table.getWidget(0, 1);
					if(!lb.getSelectedItemText().equals("-")){
						onAccept();
					}
					else {
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
				if(type.equals("new2")){
					ListBox lb = (ListBox)flex_table.getWidget(0, 1);
					TextBox tb1 = (TextBox) flex_table.getWidget(1, 1);
					if(!lb.getSelectedItemText().equals("-") && 
							!tb1.getText().equals("")){
							onAccept();	
					}
					else{
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
			}
		});
		
		cancel_button.setText("Cancelar");
		cancel_button.setVisible(true);
		cancel_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	private void newDeposit(Enterprise enterprise) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();		
		lb.addItem("Abreviado");
		lb.addItem("Pymes");

		flex_table.setWidget(0, 0, new Label("Tipo de Dep\u00f3sito"));
		flex_table.setWidget(0, 1, lb);
		
		TextBox tb1 = new TextBox();
		tb1.setText(enterprise.getName());
		tb1.setStyleName("aon-inputText");
		flex_table.setWidget(1, 0, new Label("Nombre del Dep\u00f3sito"));
		flex_table.setWidget(1, 1, tb1);
		
		TextBox tb2 = new TextBox();
		tb2.setText(enterprise.getDocument());
		tb2.setStyleName("aon-inputText");
		tb2.setEnabled(false);
		flex_table.setWidget(2, 0, new Label("CIF"));
		flex_table.setWidget(2, 1, tb2);
		
		TextBox tb3 = new TextBox();
		tb3.setText("2014");
		tb3.setStyleName("aon-inputText");
		tb3.setEnabled(false);
		flex_table.setWidget(3, 0, new Label("Ejercicio"));
		flex_table.setWidget(3, 1, tb3);
		
		TextBox tb4 = new TextBox();
		tb4.setStyleName("aon-inputText");
		flex_table.setWidget(4, 0, new Label("Descripci\u00f3n"));
		flex_table.setWidget(4, 1,tb4);
		
		flexTableCss();
	}
	
	private void newDeposit2() {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb = new ListBox();
		
		lb.addItem("Abreviado");
		lb.addItem("Pymes");

		//lb.addItem("-");
		//lb.addItem("Abreviado");
		//lb.addItem("Pymes");

		flex_table.setWidget(0, 0, new Label("Tipo de Deposito"));
		flex_table.setWidget(0, 1, lb);
		
		TextBox tb1 = new TextBox();
		tb1.setStyleName("aon-inputText");
		flex_table.setWidget(1, 0, new Label("Nombre del Deposito"));
		flex_table.setWidget(1, 1, tb1);
		
		flexTableCss();
	}
	
	private void importar(Enterprise enterprise,String url){
		
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		
		
		flex_table.setWidget(0, 0, new Label("Fichero"));
		flex_table.setWidget(0, 1, newUploader(url+"gwt_deposit_upload?domain_id="+enterprise.getDomain()+
																		"&cif="+enterprise.getDocument()));
		
		
		flexTableCss();
	}
	
	Vector<MemoryTemplate> mtsAux;
	Enterprise enterpriseAux;	
	private void importAll(Vector<MemoryTemplate> mts, Enterprise enterprise, String url){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		final ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem("Balance");
		lb.addItem("Perdidas y ganancias");
		lb.addItem("ECPN");
		lb.addItem("Memoria predefinida");
		lb.addItem("Memoria");
		
		mtsAux = mts;urlAux = url; enterpriseAux = enterprise;
		lb.addChangeHandler(new ChangeHandler() {
			Vector<MemoryTemplate> mts = mtsAux;
			String url = urlAux;
			Enterprise enterprise = enterpriseAux;
			@Override
			public void onChange(ChangeEvent event) {
				if(lb.getSelectedItemText().equals("Balance")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem("2013");
					lb2.addItem("2014");
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);					
				}
				if(lb.getSelectedItemText().equals("Perdidas y ganancias")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem("2013");
					lb2.addItem("2014");
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);	
				}
				if(lb.getSelectedItemText().equals("ECPN")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem("2013");
					lb2.addItem("2014");
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);	
				}
				if(lb.getSelectedItemText().equals("Memoria predefinida")){
					ListBox lb1 = new ListBox();
					if(mts.size()>1) lb1.addItem("-");
					for (MemoryTemplate memoryTemplate : mts) {
						lb1.addItem(memoryTemplate.getName());
					}
					flex_table.setWidget(1, 0, new Label("Memoria Predefinida"));
					flex_table.setWidget(1, 1, lb1);
					flexTableCss();
					flex_table.removeRow(2);
				}

				if(lb.getSelectedItemText().equals("Memoria")){
					ListBox lb1 = new ListBox();
					//lb1.addItem("2013");
					lb1.addItem("2014");
					flex_table.setWidget(1, 0, new Label("Ejercicio"));
					flex_table.setWidget(1,	1, lb1);	
					
					flex_table.setWidget(2, 0, new Label("Fichero"));
					flex_table.setWidget(2, 1, newUploader(url+"gwt_deposit_upload?domain_id="+enterprise.getDomain()+
																					"&cif="+enterprise.getDocument()));
					
				}
				if(lb.getSelectedItemText().equals("-")){
					flex_table.removeRow(1);
					flex_table.removeRow(2);
				}
				flexTableCss();
			}
		});
		flex_table.setWidget(0, 0, new Label("Que"));
		flex_table.setWidget(0,	1, lb);
	
		
		flexTableCss();
	}
	
	private void importarTextos(Vector<MemoryTemplate> mts) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);

		ListBox lb = new ListBox();
		if(mts.size()>1) lb.addItem("-");
		for (MemoryTemplate memoryTemplate : mts) {
			lb.addItem(memoryTemplate.getName());
		}
		flex_table.setWidget(0, 0, new Label("Textos de la Memoria"));
		flex_table.setWidget(0, 1, lb);
		
		
		flexTableCss();

	}
	
	
	long progress = 10;
	SingleUploader up;
	String urlAux;
	private SingleUploader newUploader(String url) {

		
		SingleUploader upload=  new SingleUploader(FileInputType.BROWSER_INPUT);
		
		
		upload.setAutoSubmit(true);
        upload.setServletPath(url);
        
        upload.getForm().getWidget().getElement().getChild(1).removeFromParent();
        upload.getForm().setAction(url);
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(true);
        
        up = upload; urlAux = url;
        upload.addOnCancelUploadHandler(new OnCancelUploaderHandler() {
        	SingleUploader upload = up; String url = urlAux;
        	@Override
			public void onCancel(IUploader uploader) {
        		//Window.alert("lalalala error");
        		SingleUploader upload1 = newUploader(url);
        		flex_table.setWidget(0, 1, upload1);
        		// reset out of TemplatesServlet!!!
        		
			}
		});
        
        upload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
        	SingleUploader upload = up;
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
        	SingleUploader upload = up;
			@Override
			public void onStart(IUploader uploader) {
				upload.getStatusWidget().setVisible(true);
				//Window.alert("start");
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
        	SingleUploader upload = up;
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
        	SingleUploader upload = up;
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