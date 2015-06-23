package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.SingleUploader;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.tree.node.DigitalDepositFreeTextTreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.DigitalDepositTreeNode;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NormalizedMemory extends ResizeComposite {

	
	interface NormalizedMemoryBinder extends UiBinder<Widget, NormalizedMemory> {
	}
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	private static final NormalizedMemoryBinder MODEL_NORMALIZED_MEMORY_BINDER = GWT
			.create(NormalizedMemoryBinder.class);

	private NormalizedMemory normalizedMemory;
	private FiscalServiceAsync fiscalService;
	private final static FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private final static AonResources RESOURCES = GWT.create(AonResources.class);

	private Memory memory;

	
	
	@UiField Label depositType;
	
	@UiField
	Button newButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button importButton;
	
	@UiField
	Button generateFileButton;

	@UiField Button importTextButton;
	
	@UiField SimplePanel headerPanel;
	
	@UiField
	Anchor download;							
	

	
	
	// selected item content panel
	@UiField
	FlowPanel pagesPanel;
	SingleUploader upload;
	Enterprise enterprise;
	String page;
	Boolean textMode;
	MemoryTemplate memoryTemplate;
	
	DigitalDepositTreeNode digitalDepositTreeNode;
	DigitalDepositFreeTextTreeNode digitalDepositFreeTextTreeNode;
	
	
	public NormalizedMemory(Enterprise enterprise,String page) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		saveButton = new Button();
       
        
		cancelButton = new Button();
		this.enterprise = enterprise;
		this.page = page;
		this.textMode = false;
		
		headerPanel = new SimplePanel();
		pagesPanel = new FlowPanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Abreviado");
		
		inma.isModify(enterprise.getDocument(),new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				saveButton.setEnabled(result);
				cancelButton.setVisible(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	public NormalizedMemory(Enterprise enterprise,String page,MemoryTemplate mt) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		newButton = new Button();	
        importButton = new Button();
        importTextButton = new Button();
        generateFileButton = new Button();
		cancelButton = new Button();
		this.enterprise = enterprise;
		this.page = page;
		this.textMode = true;
		this.memoryTemplate = mt;
		
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Abreviado");
		newButton.setVisible(true);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		generateFileButton.setVisible(false);
		inma.isModify(mt.getId().toString(),new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				saveButton.setEnabled(result);
				cancelButton.setVisible(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	

	public NormalizedMemory(Boolean type, DigitalDepositTreeNode ddtn, Enterprise e) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		digitalDepositTreeNode = ddtn;
		this.textMode = false;
		enterprise = e;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		if(type){
			newButton.setVisible(true);
			saveButton.setVisible(false);
			importTextButton.setVisible(false);
			generateFileButton.setVisible(false);
		}
		
		depositType.setText("Abreviado");
	}
	
	public NormalizedMemory(Boolean type, DigitalDepositFreeTextTreeNode ddtn, Enterprise e) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importButton = new Button();
		importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		digitalDepositFreeTextTreeNode = ddtn;
		this.textMode = true;
		
		enterprise = e;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		if(type){
			importButton.setVisible(false);
			importTextButton.setVisible(false);
			newButton.setVisible(true);
			saveButton.setVisible(false);
			generateFileButton.setVisible(false);
		}
		
		depositType.setText("Abreviado");
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
		String url = GWT.getModuleBaseURL()+"gwt_deposit_upload";
		DepositDialog popup ;
		if(textMode){
			 popup = new DepositDialog("Nuevo Deposito","new2", enterprise,url,null) {
				
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					hide();
					TextBox tb = (TextBox) flex_table.getWidget(1, 1);
					
					inma.createTextMemory(enterprise.getDomain(),tb.getValue(),new AsyncCallback<MemoryTemplate>() {
						
						@Override
						public void onSuccess(MemoryTemplate result) {
							digitalDepositFreeTextTreeNode.items(result);							
						}
						
						@Override
						public void onFailure(Throwable caught) {	}
					});
					
		
				}
			};
		}
		else{
			popup = new DepositDialog("Nuevo Deposito","new", enterprise,url,null) {
			
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					hide();
					digitalDepositTreeNode.items();
					newButton.setVisible(false);
					saveButton.setVisible(true);
					saveButton.setEnabled(false);
					generateFileButton.setVisible(true);
				}
			};
		}
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		//Window.alert("onSaveButtonClick");
		if(textMode){
			inma.saveDeposit(memoryTemplate.getId().toString(),enterprise.getDomain(),textMode, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					saveButton.setEnabled(false);
					cancelButton.setVisible(false);
					update();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		else{
			inma.saveDeposit(enterprise.getDocument(),enterprise.getDomain(),false, new AsyncCallback<Void>() {
			
				@Override
				public void onSuccess(Void result) {
					saveButton.setEnabled(false);
					cancelButton.setVisible(false);
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
	}
	FormPanel form ;
	FileUpload a;
	
	@UiHandler("importButton")
	void onImportButtonClick(ClickEvent event) {
		String url = GWT.getModuleBaseURL()+"gwt_deposit_upload";
		DepositDialog popup = new DepositDialog("Importar Deposito","import", enterprise, GWT.getModuleBaseURL(), null) {

			@Override
			protected void onAccept() {
				inma.saveDeposit(enterprise.getDocument(), enterprise.getDomain(),false, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						update();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
				hide();
			}

			@Override
			protected void onCancel() {
				hide();
			}
			
		};
		
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
		
		/*form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL()+"/gwt_upload/");
		 a = new FileUpload();

		a.addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				Window.alert("UPLOAD");
			}
		});
		a.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Window.alert("uppp");
				Window.alert(a.getFilename());
				FileUpload a2 =(FileUpload) form.getWidget();
				Window.alert(a2.getFilename());
				
			}
		});
		a.click();
		
		form.setWidget(a);
		
		//form.add(a);*/
		

	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		//Window.alert("onCancelButtonClick");
		
		if(textMode){
			inma.clearSession(memoryTemplate.getId().toString(), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					saveButton.setEnabled(false);
					cancelButton.setVisible(false);
				
					update();
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		else{
			inma.clearSession(enterprise.getDocument(), new AsyncCallback<Void>() {
			
				@Override
				public void onSuccess(Void result) {
					saveButton.setEnabled(false);
					cancelButton.setVisible(false);
				
					update();
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
	}
	
	Vector<MemoryTemplate> mts ;
	
	
	@UiHandler("importTextButton")
	void onImportTextButtonClick(ClickEvent event) {
		
		//Window.alert("onImportTextButtonClick");
		inma.getParentDomain(enterprise.getDomain(), new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Integer result) {
				
				inma.getDigitalDepositTemplates(result,
						new AsyncCallback<Vector<MemoryTemplate>>() {

							@Override
							public void onSuccess(Vector<MemoryTemplate> result) {
								String url = GWT.getModuleBaseURL()
										+ "gwt_deposit_upload";
								mts = result;
								DepositDialog popup = new DepositDialog(
										"Importar Textos", "importText", enterprise,
										url, result) {
									Vector<MemoryTemplate> vector = mts;

									@Override
									protected void onCancel() {
										hide();

									}

									@Override
									protected void onAccept() {
										hide();
										ListBox lb = (ListBox) flex_table.getWidget(0,1);
										String t = lb.getSelectedItemText();
										MemoryTemplate m = new MemoryTemplate();
										for (MemoryTemplate mt : vector) {
											if (mt.getName().equals(t))
												m = mt;
										}
										inma.updateTexts(m, enterprise.getDomain(),
												enterprise.getDocument(),
												new AsyncCallback<Void>() {

													@Override
													public void onFailure(
															Throwable caught) {
													}

													@Override
													public void onSuccess(Void result) {
														update();
													}
												});
									}
								};

								popup.addStyleName("gwt-PopupPanel-template");
								popup.setGlassEnabled(true);
								popup.show();
							}

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub

							}
						});
				
			}
			
			
		});
		
			
			
		
		
	
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
	//	Window.alert("onDeleteButtonClick");
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_deposit/"
            	+ "?domain_id=" + Integer.toString(enterprise.getDomain());
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	@UiHandler("download")
	void onDownloadClick(ClickEvent event) {
		//Window.alert("onDownloadClick");
	}

	private void applySelectedStyle(FocusPanel panel) {
		panel.getElement().getStyle().setBackgroundColor("#999");
		panel.getElement().getStyle().setColor("white");
	}
	
	public void setPagesPanel(Widget widget){
		
		pagesPanel.add(widget);
	}
	
	
	private SingleUploader newUploader() {
		Button b= new Button();
		b.setStyleName("aon-finding-toolbar-item aon-icon-file-upload");
		b.setText("Importar");
	
		SingleUploader upload=  new SingleUploader(FileInputType.BROWSER_INPUT);
		
		String url = GWT.getModuleBaseURL()+"gwt_deposit_upload";
	 	//Window.alert(url);
		upload.setAutoSubmit(true);
        upload.setServletPath(url);
        upload.getFileInput().getWidget().setStyleName("aon-finding-toolbar-item aon-icon-file-upload");

        upload.getForm().setAction(url);
        upload.getForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        upload.getForm().setMethod(FormPanel.METHOD_POST);
        upload.setTitle("uploadFormElement");
        upload.avoidEmptyFiles(false);

        upload.addOnStartUploadHandler(new OnStartUploaderHandler() {
			
			@Override
			public void onStart(IUploader uploader) {

			}
		});

  

        upload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			@Override
			public void onFinish(IUploader uploader) {

			}
		});
        
        return upload;
		
	}
	
	
	private void update(){
		switch (page) {
		case "IDA":
			Header1 h1 = (Header1) pagesPanel.getWidget(0);
			h1.init();
			break;
		case "BA":
			PageH2 h2 = (PageH2) pagesPanel.getWidget(0);
			h2.dump(h2.d2DepositObject, "BA");
			break;
		case "PA":
			PageH3 h3 = (PageH3) pagesPanel.getWidget(0);
			h3.dump(h3.d2DepositObject, "PA");
			break;
		case "PNA":
			PageH4 h4 = (PageH4) pagesPanel.getWidget(0);
			h4.dump(h4.d2DepositObject, "PNA");
			break;
		case "IMA":
			Header5 h5 = (Header5) pagesPanel.getWidget(0);
			h5.init();
			break;
		case "MAT1":
			FreeText ft = (FreeText) pagesPanel.getWidget(0);
			ft.dump(ft.d2DepositObject, "MAT1");
			break;
		case "MAT2":
			FreeText ft2 = (FreeText) pagesPanel.getWidget(0);
			ft2.dump(ft2.d2DepositObject, "MAT2");
			break;
		case "MAT3":
			FreeText ft3 = (FreeText) pagesPanel.getWidget(0);
			ft3.dump(ft3.d2DepositObject, "MAT3");
			break;
		case "MA3":
			PageM3_2 p32 = (PageM3_2) pagesPanel.getWidget(0);
			p32.dump(p32.d2DepositObject, "MA3");
			break;
		case "MAT4":
			FreeText ft4 = (FreeText) pagesPanel.getWidget(0);
			ft4.dump(ft4.d2DepositObject, "MAT4");
			break;
		case "MAT5":
			FreeText ft5 = (FreeText) pagesPanel.getWidget(0);
			ft5.dump(ft5.d2DepositObject, "MAT5");;
			break;
		case "MA5":
			PageM5_2 p52 = (PageM5_2) pagesPanel.getWidget(0);
			p52.dump(p52.d2DepositObject, "MA3");;
			break;
		case "MAT6":
			FreeText ft6 = (FreeText) pagesPanel.getWidget(0);
			ft6.dump(ft6.d2DepositObject, "MAT6");
			break;
		case "MA6":
			Paragraph6_2 p62= (Paragraph6_2) pagesPanel.getWidget(0);
			p62.init();
			break;
			
		case "MAT7":
			FreeText ft7 = (FreeText) pagesPanel.getWidget(0);
			ft7.dump(ft7.d2DepositObject, "MAT7");
			break;
		case "MA7":
			Paragraph7_2 p72 = (Paragraph7_2) pagesPanel.getWidget(0);
			p72.init();
			break;
		case "MAT8":
			FreeText ft8 = (FreeText) pagesPanel.getWidget(0);
			ft8.dump(ft8.d2DepositObject, "MAT8");
			break;
		case "MAT9":
			FreeText ft9 = (FreeText) pagesPanel.getWidget(0);
			ft9.dump(ft9.d2DepositObject, "MAT9");
			break;
		case "MA10":
			Paragraph10 p10 = (Paragraph10) pagesPanel.getWidget(0);
			p10.init();
			break;
		case "MAT11":
			FreeText ft11 = (FreeText) pagesPanel.getWidget(0);
			ft11.dump(ft11.d2DepositObject, "MAT11");
			break;
		case "MA11":
			Paragraph11_2 p112 = (Paragraph11_2) pagesPanel.getWidget(0);
			p112.init();
			break;
		case "MAT12":
			FreeText ft12 = (FreeText) pagesPanel.getWidget(0);
			ft12.dump(ft12.d2DepositObject, "MAT12");
			break;
		case "MA12":
			Paragraph12_2 p122 = (Paragraph12_2) pagesPanel.getWidget(0);
			p122.init();
			break;
		case "MAT13":
			FreeText ft13 = (FreeText) pagesPanel.getWidget(0);
			ft13.dump(ft13.d2DepositObject, "MAT13");
			break;
		case "MA13":
			Paragraph13_2 p132 = (Paragraph13_2) pagesPanel.getWidget(0);
			p132.init();
			break;
		case "MAT14":
			FreeText ft14 = (FreeText) pagesPanel.getWidget(0);
			ft14.dump(ft14.d2DepositObject, "MAT14");
			break;
		case "MA14":
			Paragraph14_2 p142 = (Paragraph14_2) pagesPanel.getWidget(0);
			p142.init();
			break;
		case "MA15":
			Paragraph15 p15 = (Paragraph15) pagesPanel.getWidget(0);
			p15.init();
			break;
		default:
			break;
		}
	}
	
	public void paintHeaderTable(String text) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(AON.AON_CSS.aonRegistroMercantilImage());
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(text));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFiscalRegistroMercantil());
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label("D2"));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerTable.setWidget(1, 0, new Label("2014"));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerPanel.setWidget(headerTable);
	}
	

}
