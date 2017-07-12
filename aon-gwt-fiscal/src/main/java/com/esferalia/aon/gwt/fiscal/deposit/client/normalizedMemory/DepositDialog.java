package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.HashMap;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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

public abstract class DepositDialog extends CustomDialogB {
	
	private static final String DIALOG_NEW = "new";
	private static final String DIALOG_NEW2 = "new2";
	private static final String DIALOG_IMPORT = "import";
	private static final String DIALOG_IMPORT_TEXT = "importText";
	private static final String DIALOG_IMPORT_ALL = "importAll";
	private static final String DIALOG_DELETE = "delete";
	private static final String DIALOG_EXPORT = "export";



	interface Binder extends UiBinder<Widget, DepositDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true)
	protected FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	@UiField(provided = true) VerticalPanel vp;
	String typeAux;
	
	public DepositDialog(String title, String type, Enterprise enterprise, String url, Vector<MemoryTemplate> mts, Boolean ej, String[] exercises, Integer year) {
		setCaption(title);
		label = new Label();
		flex_table = new FlexTable();
		vp = new VerticalPanel();
		typeAux = type;
		switch (type) {
		case DIALOG_NEW: newDeposit(enterprise, ej, year);break;
		case DIALOG_NEW2: newDeposit2();break;
		case DIALOG_IMPORT: label.setText("Al importar un archivo se eliminar\u00e1n todos los datos referentes a la memoria normalizada.");
							importar(enterprise, url);break;
		case DIALOG_IMPORT_TEXT: importarTextos(mts);break;
		case DIALOG_IMPORT_ALL: importAll(mts, enterprise, url,year); break;
		case DIALOG_DELETE: label.setText("Est\u00e1 seguro de eliminar el Deposito");break;
		case DIALOG_EXPORT: exportar(exercises);break;
		default:
			break;
		}
		setWidget(binder.createAndBindUi(this));
		
		if(type.equals(DIALOG_NEW)) accept_button.setText("Nuevo");
		if(type.equals(DIALOG_NEW2)) accept_button.setText("Nuevo");
		if(type.equals(DIALOG_IMPORT)) accept_button.setText("Importar");
		if(type.equals(DIALOG_IMPORT_TEXT)) accept_button.setText("Importar");
		if(type.equals(DIALOG_IMPORT_ALL)) accept_button.setText("Importar");
		if(type.equals(DIALOG_DELETE)) accept_button.setText("Eliminar");
		if(type.equals(DIALOG_EXPORT)) accept_button.setText("Exportar");
		accept_button.setVisible(true);
		accept_button.addClickHandler(new ClickHandler() {
			String type = typeAux;
			@Override
			public void onClick(ClickEvent event) {
				if(type.equals(DIALOG_NEW)){
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
				if(type.equals(DIALOG_DELETE) || type.equals(DIALOG_IMPORT_ALL) 
						|| type.equals(DIALOG_IMPORT) || type.equals(DIALOG_EXPORT)){
					onAccept();
				}
				if(type.equals(DIALOG_IMPORT_TEXT)){
					ListBox lb =(ListBox) flex_table.getWidget(0, 1);
					if(!lb.getSelectedItemText().equals("-")){
						onAccept();
					}
					else {
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
				if(type.equals(DIALOG_NEW2)){
					TextBox tb1 = (TextBox) flex_table.getWidget(0, 1);
					if(!tb1.getText().equals("")){
							onAccept();	
					}
					else{
						label.setText("*Faltan datos por a\u00f1adir");
						label.setStyleName("aon-check-template");
					}
				}
			}
		});
		if(type.equals(DIALOG_EXPORT))
			cancel_button.setVisible(false);
		else {
			cancel_button.setText("Cancelar");
			cancel_button.setVisible(true);
			cancel_button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onCancel();
				}
			});
		}
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	private void newDeposit(Enterprise enterprise, Boolean ej, Integer year) {
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
		
		ListBox  lb3 = new ListBox();
		lb3.addItem("-");
		lb3.addItem(year.toString());
		lb3.setSelectedIndex(1);
		lb3.setStyleName("aon-inputText");
		lb3.setEnabled(ej);
		flex_table.setWidget(3, 0, new Label("Ejercicio"));
		flex_table.setWidget(3, 1, lb3);
		
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

	
		TextBox tb1 = new TextBox();
		tb1.setStyleName("aon-inputText");
		flex_table.setWidget(0, 0, new Label("Nombre de la Plantilla"));
		flex_table.setWidget(0, 1, tb1);
		
		flexTableCss();
	}
	
	private void exportar(String[] exercises){
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("100%");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		
		flex_table.setWidget(0, 0, new Label("Ejercicio"));
		ListBox listBox = new ListBox();
		if(exercises.length == 0) listBox.addItem("-");
		for (String exercise : exercises) {
			listBox.addItem(exercise);
		}
		flex_table.setWidget(0,	1, listBox);
		
		
		flexTableCss();
	}
	
	private void importar(Enterprise enterprise,String url){
		
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("100%");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		
		flex_table.setWidget(0, 0, new Label("Fichero"));
		flex_table.setWidget(0, 1, newUploader(url+"gwt_deposit_upload?domain_id="+enterprise.getDomain()));
		
		
		flexTableCss();
	}
	
	Vector<MemoryTemplate> mtsAux;
	Enterprise enterpriseAux;	
	private void importAll(Vector<MemoryTemplate> mts, Enterprise enterprise, String url, final Integer year){
		
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		final ListBox lb = new ListBox();
		lb.addItem("-");
		lb.addItem("Memoria predefinida");
		lb.addItem("Balance (I.S.)");
		lb.addItem("Perdidas y ganancias (I.S.)");
		if(year < 2016){ // TODO && !ABREVIATE && !PYMES
			lb.addItem("ECPN (I.S.)");
		}
		lb.addItem("Memoria (Deposito.xml)");
		
		mtsAux = mts;urlAux = url; enterpriseAux = enterprise;
		lb.addChangeHandler(new ChangeHandler() {
			Vector<MemoryTemplate> mts = mtsAux;
			String url = urlAux;
			Enterprise enterprise = enterpriseAux;
			@Override
			public void onChange(ChangeEvent event) {
				Integer lastYear = year -1;
				if(lb.getSelectedItemText().equals("Balance (I.S.)")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem(lastYear.toString());
					lb2.addItem(year.toString());
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);					
				}
				if(lb.getSelectedItemText().equals("Perdidas y ganancias (I.S.)")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem(lastYear.toString());
					lb2.addItem(year.toString());
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);	
				}
				if(lb.getSelectedItemText().equals("ECPN (I.S.)")){
					ListBox lb1 = new ListBox();
					lb1.addItem("Sociedades");
					//lb1.addItem("D2");
					//lb1.addItem("Contabilidad");
					flex_table.setWidget(1, 0, new Label("De"));
					flex_table.setWidget(1,	1, lb1);
					
					ListBox lb2 = new ListBox();
					lb2.addItem(lastYear.toString());
					lb2.addItem(year.toString());
					flex_table.setWidget(2, 0, new Label("Ejercicio"));
					flex_table.setWidget(2,	1, lb2);	
				}
				if(lb.getSelectedItemText().equals("Memoria predefinida")){
					ListBox lb1 = new ListBox();
					lb1.addItem("-");
					for (MemoryTemplate memoryTemplate : mts) {
						lb1.addItem(memoryTemplate.getName());
					}
					lb1.addChangeHandler(freeTextImport());
					flex_table.setWidget(1, 0, new Label("Memoria Predefinida"));
					flex_table.setWidget(1, 1, lb1);
					flexTableCss();
					flex_table.removeRow(2);
				}

				if(lb.getSelectedItemText().equals("Memoria (Deposito.xml)")){
					ListBox lb1 = new ListBox();
					//lb1.addItem("2013");
					lb1.addItem(year.toString());
					flex_table.setWidget(1, 0, new Label("Ejercicio"));
					flex_table.setWidget(1,	1, lb1);	
					
					flex_table.setWidget(2, 0, new Label("Fichero"));
					flex_table.setWidget(2, 1, newUploader(url+"gwt_deposit_upload?domain_id="+enterprise.getDomain()));
					
				}
				if(lb.getSelectedItemText().equals("Documento Memoria")){
					
					flex_table.setWidget(1, 0, new Label("Memoria"));
					flex_table.setWidget(1, 1, newUploader(url+"gwt_deposit_upload?domain_id="+enterprise.getDomain()));
				}
				if(lb.getSelectedItemText().equals("-")){
					flex_table.removeRow(1);
					flex_table.removeRow(2);
				}
				flexTableCss();
			}
		});
		flex_table.setWidget(0, 0, new Label("Seleccione"));
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
        	String url = urlAux;
        	@Override
			public void onCancel(IUploader uploader) {
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
			}
		});
        
        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
        	SingleUploader upload = up;
			@Override
			public void onFinish(IUploader uploader) {
				upload.getStatusWidget().setProgress(100, 100);
				upload.getStatusWidget().setStatus(Status.DONE);
				upload.getStatusWidget().setVisible(true);
				progress = 0;		
			}
		});
        
        upload.getForm().addSubmitCompleteHandler(new SubmitCompleteHandler() {
        	SingleUploader upload = up;
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
	
	private HashMap<D2DepositKey, Boolean> freeTextMap;
	
	public HashMap<D2DepositKey, Boolean> getFreeTextMap() {
		return freeTextMap;
	}

	public void setFreeTextMap(HashMap<D2DepositKey, Boolean> freeTextMap) {
		this.freeTextMap = freeTextMap;
	}

	private ChangeHandler freeTextImport(){
		return new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				FreeTextImportDialog dialog = new FreeTextImportDialog() {
					
					@Override protected void onCancel() {
						hide();
					}
					
					@Override protected void onAccept() {
						setFreeTextMap(new HashMap<D2DepositKey, Boolean>());
						for(Integer i = 0; i < 13; i++){
							CheckBox cb = (CheckBox)flex_table.getWidget(i+1, 1);
							getFreeTextMap().put(getD2DepositKey(i), cb.getValue());
						}
						hide();
					}
				};
				dialog.addStyleName("gwt-PopupPanel-template");
				dialog.setGlassEnabled(true);
				dialog.show();
			}
		};
	}
}