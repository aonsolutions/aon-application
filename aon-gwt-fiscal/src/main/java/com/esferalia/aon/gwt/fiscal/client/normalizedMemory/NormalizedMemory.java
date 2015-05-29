package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class NormalizedMemory extends MainEntryPoint {

	interface NormalizedMemoryBinder extends UiBinder<Widget, NormalizedMemory> {
	}

	private static final NormalizedMemoryBinder MODEL_NORMALIZED_MEMORY_BINDER = GWT
			.create(NormalizedMemoryBinder.class);

	private NormalizedMemory normalizedMemory;
	private FiscalServiceAsync fiscalService;
	private final static FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private final static AonResources RESOURCES = GWT.create(AonResources.class);

	private Memory memory;
	
	
	FormPanel diskForm;
	Hidden mod390Hidden;
	
	@UiField
	FlowPanel menuPanel;
	
	@UiField
	Button newButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button generateFileButton;
	
	@UiField
	Anchor download;							
	
	@UiField
	FlowPanel formContainer;
	
	@UiField
	FileUpload fileUpload;
	
	
	@UiField
	Tree mainTree;
	@UiField
	TreeItem header1;
	@UiField
	TreeItem header2;
	@UiField
	TreeItem header3;
	@UiField
	TreeItem header4;
	@UiField
	TreeItem header5;
	@UiField
	TreeItem paragraph1;
	@UiField
	TreeItem paragraph2;
	@UiField
	TreeItem paragraph3_1;
	@UiField
	TreeItem paragraph3_2;
	@UiField
	TreeItem paragraph4;
	@UiField
	TreeItem paragraph5_1;
	@UiField
	TreeItem paragraph5_2;
	@UiField
	TreeItem paragraph6_1;
	@UiField
	TreeItem paragraph6_2;
	@UiField
	TreeItem paragraph7_1;
	@UiField
	TreeItem paragraph7_2;
	@UiField
	TreeItem paragraph8;
	@UiField
	TreeItem paragraph9;
	@UiField
	TreeItem paragraph10;
	@UiField
	TreeItem paragraph11_1;
	@UiField
	TreeItem paragraph11_2;
	@UiField
	TreeItem paragraph12_1;
	@UiField
	TreeItem paragraph12_2;
	@UiField
	TreeItem paragraph13_1;
	@UiField
	TreeItem paragraph13_2;
	@UiField
	TreeItem paragraph14_1;
	@UiField
	TreeItem paragraph14_2;
	@UiField
	TreeItem paragraph15;
	@UiField
	TreeItem footer1;
	@UiField
	TreeItem footer2;
	@UiField
	TreeItem footer3;

	// collapse tree items
	@UiField
	TreeItem memoryItem;
	@UiField
	TreeItem paragraph3;
	@UiField
	TreeItem paragraph5;
	@UiField
	TreeItem paragraph6;
	@UiField
	TreeItem paragraph7;
	@UiField
	TreeItem paragraph11;
	@UiField
	TreeItem paragraph12;
	@UiField
	TreeItem paragraph13;
	@UiField
	TreeItem paragraph14;
	
	// selected item content panel
	@UiField
	FlowPanel pagesPanel;
	
	
	private Header1 header1Page;
	private Header2 header2Page;
	private Header3 header3Page;
	private Header4 header4Page;
	private Header5 header5Page;
	private FreeText paragraph1Page;
	private FreeText paragraph2Page;
	private FreeText paragraph3_1Page;
	private Paragraph3_2 paragraph3_2Page;
	private FreeText paragraph4Page;
	private FreeText paragraph5_1Page;
	private Paragraph5_2 paragraph5_2Page;
	private FreeText paragraph6_1Page;
	private Paragraph6_2 paragraph6_2Page;
	private FreeText paragraph7_1Page;
	private Paragraph7_2 paragraph7_2Page;
	private FreeText paragraph8Page;
	private FreeText paragraph9Page;
	private Paragraph10 paragraph10Page;
	private FreeText paragraph11_1Page;
	private Paragraph11_2 paragraph11_2Page;
	private FreeText paragraph12_1Page;
	private Paragraph12_2 paragraph12_2Page;
	private FreeText paragraph13_1Page;
	private Paragraph13_2 paragraph13_2Page;
	private FreeText paragraph14_1Page;
	private Paragraph14_2 paragraph14_2Page;
	private Paragraph15 paragraph15Page;
	private Footer1 footer1Page;
	private Footer2 footer2Page;
	private Footer3 footer3Page;
	
	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		
		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		
		
		newButton.setVisible(false);
		saveButton.setVisible(true);
		cancelButton.setVisible(false);
		deleteButton.setVisible(false);
		generateFileButton.setVisible(false);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		mod390Hidden = new Hidden("mod390");
		diskForm.add(mod390Hidden);
//		formContainer.add(diskForm);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		// show tree items expanded
		memoryItem.setState(true);
		paragraph3.setState(true);
		paragraph5.setState(true);
		paragraph6.setState(true);
		paragraph7.setState(true);
		paragraph11.setState(true);
		paragraph12.setState(true);
		paragraph13.setState(true);
		paragraph14.setState(true);
		
		
		header1Page = new Header1();
		header2Page = new Header2();
		header3Page = new Header3();
		header4Page = new Header4();
		header5Page = new Header5();
		paragraph1Page = new FreeText("Apartado 1: Actividad de la empresa", true);
		paragraph2Page = new FreeText("Apartado 2: Bases de presentacion de las cuentas anuales", true);
		paragraph3_1Page = new FreeText("Apartado 3: Aplicacion de resultados", true);
		paragraph3_2Page = new Paragraph3_2();
		paragraph4Page = new FreeText("Apartado 4: Normas de registro y valoracion", true);
		paragraph5_1Page = new FreeText("Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias", true);
		paragraph5_2Page = new Paragraph5_2();
		paragraph6_1Page = new FreeText("Apartado 6: Activos financieros", true);
		paragraph6_2Page = new Paragraph6_2();
		paragraph7_1Page = new FreeText("Apartado 7: Pasivos financieros", true);
		paragraph7_2Page = new Paragraph7_2();
		paragraph8Page = new FreeText("Apartado 8: Fondos propios", true);
		paragraph9Page = new FreeText("Apartado 9: Situacion fiscal", true);
		paragraph10Page = new Paragraph10();
		paragraph11_1Page = new FreeText("Apartado 11: Subvenciones, donaciones y legados", true);
		paragraph11_2Page = new Paragraph11_2();
		paragraph12_1Page = new FreeText("Apartado 12: Operaciones con partes vinculadas", true);
		paragraph12_2Page = new Paragraph12_2();
		paragraph13_1Page = new FreeText("Apartado 13: Otra informacion", true);
		paragraph13_2Page = new Paragraph13_2();
		paragraph14_1Page = new FreeText("Apartado 14: Informacion sobre medio ambiente", true);
		paragraph14_2Page = new Paragraph14_2();
		paragraph15Page = new Paragraph15();
		footer1Page = new Footer1();
		footer2Page = new Footer2();
		footer3Page = new Footer3();
		
	}
	

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;



	private void addErrorMessage(String msg) {
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
	}
	
	

	
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		Window.alert("onNewButtonClick");
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		Window.alert("onSaveButtonClick");
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		Window.alert("onCancelButtonClick");
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		Window.alert("onDeleteButtonClick");
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert("Se va a proceder a la generaci\u00F3n del fichero.\n"
				+ " Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n"
				+ " El fichero se genera a partir de los datos guardados.");
	}
	
	@UiHandler("download")
	void onDownloadClick(ClickEvent event) {
		Window.alert("onDownloadClick");
	}
	
	
//	@UiHandler("fileUploadForm")
//	public void onSubmit(SubmitEvent event) {
//		Window.alert("file upload submited");
//	}
//	
//	@UiHandler("fileUploadForm")
//	void onSubmitComplete(SubmitCompleteEvent event) {
//		Window.alert("file upload submit completed");
//	}
	
//	@UiHandler("uploadButton")
//	public void onUploadButtonClick(ClickEvent event) {
//		Window.alert("uploadButton click");
//         //get the filename to be uploaded
//		String filename = fileUpload.getFilename();
//		if (filename.length() == 0) {
//			Window.alert("No File Specified!");
//		} else {
//            //submit the form
//			fileUploadForm.submit();			          
//		}				
//	}

	@UiHandler("fileUpload")
	public void onChange(ChangeEvent event) {
		
		// filename of selected file
//		String fileName = fileUpload.getFilename();
//		Window.alert(fileName);
		
		fiscalService.readMemory(memory, new AsyncCallback<Memory>() {
			
			@Override
			public void onSuccess(Memory result) {
				Window.alert("Fichero procesado correctamente");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se ha podido procesar el fichero" + "\n" + caught.getMessage());
			}
		});
		
	}
	
	
	@UiHandler("mainTree")
	public void onSelection(SelectionEvent<TreeItem> event) {
		pagesPanel.clear();
		
		TreeItem item = (TreeItem) event.getSelectedItem();
		if(item.equals(header1)){
			pagesPanel.add(header1Page);
		} else if(item.equals(header2)){
			pagesPanel.add(header2Page);
		} else if(item.equals(header3)){
			pagesPanel.add(header3Page);
		} else if(item.equals(header4)){
			pagesPanel.add(header4Page);
		} else if(item.equals(header5)){
			pagesPanel.add(header5Page);
		} else if(item.equals(paragraph1)){
			pagesPanel.add(paragraph1Page);
		} else if(item.equals(paragraph2)){
			pagesPanel.add(paragraph2Page);
		} else if(item.equals(paragraph3_1)){
			pagesPanel.add(paragraph3_1Page);
		} else if(item.equals(paragraph3_2)){
			pagesPanel.add(paragraph3_2Page);
		} else if(item.equals(paragraph4)){
			pagesPanel.add(paragraph4Page);
		} else if(item.equals(paragraph5_1)){
			pagesPanel.add(paragraph5_1Page);
		} else if(item.equals(paragraph5_2)){
			pagesPanel.add(paragraph5_2Page);
		} else if(item.equals(paragraph6_1)){
			pagesPanel.add(paragraph6_1Page);
		} else if(item.equals(paragraph6_2)){
			pagesPanel.add(paragraph6_2Page);
		} else if(item.equals(paragraph7_1)){
			pagesPanel.add(paragraph7_1Page);
		} else if(item.equals(paragraph7_2)){
			pagesPanel.add(paragraph7_2Page);
		} else if(item.equals(paragraph8)){
			pagesPanel.add(paragraph8Page);
		} else if(item.equals(paragraph9)){
			pagesPanel.add(paragraph9Page);
		} else if(item.equals(paragraph10)){
			pagesPanel.add(paragraph10Page);
		} else if(item.equals(paragraph11_1)){
			pagesPanel.add(paragraph11_1Page);
		} else if(item.equals(paragraph11_2)){
			pagesPanel.add(paragraph11_2Page);
		} else if(item.equals(paragraph12_1)){
			pagesPanel.add(paragraph12_1Page);
		} else if(item.equals(paragraph12_2)){
			pagesPanel.add(paragraph12_2Page);
		} else if(item.equals(paragraph13_1)){
			pagesPanel.add(paragraph13_1Page);
		} else if(item.equals(paragraph13_2)){
			pagesPanel.add(paragraph13_2Page);
		} else if(item.equals(paragraph14_1)){
			pagesPanel.add(paragraph14_1Page);
		} else if(item.equals(paragraph14_2)){
			pagesPanel.add(paragraph14_2Page);
		} else if(item.equals(paragraph15)){
			pagesPanel.add(paragraph15Page);
		} else if(item.equals(footer1)){
			pagesPanel.add(footer1Page);
		} else if(item.equals(footer2)){
			pagesPanel.add(footer2Page);
		} else if(item.equals(footer3)){
			pagesPanel.add(footer3Page);
		} else{
			pagesPanel.add(new Label(item.getText()));
		}
		
	}
	

	private void applySelectedStyle(FocusPanel panel) {
		panel.getElement().getStyle().setBackgroundColor("#999");
		panel.getElement().getStyle().setColor("white");
	}
	
}
