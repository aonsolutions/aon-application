package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.SingleUploader;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.node.D2DepositTreeObject;
import com.esferalia.aon.gwt.fiscal.client.tree.node.DigitalDepositFreeTextTreeNode;
import com.esferalia.aon.gwt.fiscal.client.tree.node.DigitalDepositTreeNode;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit2014;
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
	
	//@UiField Button importSocietyButton;

	@UiField Button importAllButton;
	
	
	@UiField
	Anchor download;							
	
	
	
	
	// selected item content panel
	@UiField
	FlowPanel pagesPanel;
	SingleUploader upload;
	Enterprise enterprise;
	//String page;
	Boolean textMode;
	MemoryTemplate memoryTemplate;
	
	Map<String, String> d2Deposit2014;
	Map<String, String> d2Deposit2014Draft;
	
	DigitalDepositTreeNode digitalDepositTreeNode;
	DigitalDepositFreeTextTreeNode digitalDepositFreeTextTreeNode;
	

	FiscalTree fiscalTree;
	
	
	//-------------------- Constructors
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas está creado.
	 * @param enterprise
	 * @param page
	 * @param ddtn
	 * @param ft
	 */
	public NormalizedMemory(DigitalDepositTreeNode ddtn, FiscalTree ft) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
        deleteButton = new Button();
		cancelButton = new Button();
		enterprise = ddtn.getD2Deposit2014().getEnterprise();
		//this.page = page;
		this.textMode = false;
		digitalDepositTreeNode = ddtn;
		fiscalTree = ft;
		
		headerPanel = new SimplePanel();
		pagesPanel = new FlowPanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Deposito");
		deleteButton.setVisible(true);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		inma.getSchema(enterprise.getDocument(), enterprise.getDomain(), textMode,new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				d2Deposit2014 = result;
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
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
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas está creado,
	 * Memoria predefinida (solo texto libre).
	 * @param enterprise
	 * @param page
	 * @param mt
	 */
	public NormalizedMemory(Enterprise enterprise,MemoryTemplate mt) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		newButton = new Button();	
		importButton = new Button();
       	importTextButton = new Button();
        deleteButton = new Button();
        generateFileButton = new Button();
		cancelButton = new Button();
		this.enterprise = enterprise;
		//this.page = page;
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
		//deleteButton.setVisible(true);
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

	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas no está creado.
	 * @param type
	 * @param ddtn
	 * @param e
	 */
	public NormalizedMemory(Boolean type, DigitalDepositTreeNode ddtn) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		
		RESOURCES.css().ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		digitalDepositTreeNode = ddtn;
		this.textMode = false;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		enterprise = ddtn.getD2Deposit2014().getEnterprise();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		if(type){
			newButton.setVisible(true);
			saveButton.setVisible(false);
			importTextButton.setVisible(false);
			generateFileButton.setVisible(false);
		}
		
		depositType.setText("Abreviado");
	}
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas no está creado,
	 * Memoria predefinida (solo texto libre).
	 * @param type
	 * @param ddtn
	 * @param e
	 */
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
		importButton.setVisible(false);
		importTextButton.setVisible(false);
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
	
	
	//@UiHandler("importSocietyButton")
	void onSocietyButtonClick(ClickEvent event) {
		
		inma.importSocietyValues(enterprise.getDocument(), enterprise.getDomain(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				update();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		
		
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
					ListBox lb = (ListBox) flex_table.getWidget(0, 1);
					TextBox tb = (TextBox) flex_table.getWidget(1, 1);
					hide();
					
					inma.createD2Deposit(enterprise.getDomain(), enterprise.getId(), tb.getValue(), lb.getSelectedItemText() , new AsyncCallback<Map<String, String>>() {

								@Override
								public void onFailure(Throwable caught) {
									
								}

								@Override
								public void onSuccess(Map<String, String> result) {
									d2Deposit2014 = result;
									digitalDepositTreeNode.getD2Deposit2014().setMap(result);
									digitalDepositTreeNode.getD2Deposit2014().setMapDraft(result);
									digitalDepositTreeNode.items();
									newButton.setVisible(false);
									saveButton.setVisible(true);
									saveButton.setEnabled(false);
									generateFileButton.setVisible(true);									
								}
					});
					
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
					
					digitalDepositTreeNode.getD2Deposit2014().setModify(false);
					digitalDepositTreeNode.getD2Deposit2014().setMap(digitalDepositTreeNode.getD2Deposit2014().getMapDraft());
					
					saveButton.setEnabled(false);
					cancelButton.setVisible(false);
					
					update();
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
					digitalDepositTreeNode.getD2Deposit2014().setModify(false);
					digitalDepositTreeNode.getD2Deposit2014().setMapDraft(null);

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
	
	
	@UiHandler("importAllButton")
	void onImportAllButtonClick(ClickEvent event){
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
										"Importar", "importAll", enterprise,
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
										
										if(t.equals("Balance")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(),digitalDepositTreeNode.getD2Deposit2014().getMap(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {
													digitalDepositTreeNode.getD2Deposit2014().setMap(result);
													digitalDepositTreeNode.getD2Deposit2014().setMapDraft(result);		
													update();
												}
											
											});
											
										
											
										}
										else if(t.equals("Perdidas y ganancias")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(), digitalDepositTreeNode.getD2Deposit2014().getMap(), new AsyncCallback<Map<String,String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String , String> result) {
													digitalDepositTreeNode.getD2Deposit2014().setMap(result);
													digitalDepositTreeNode.getD2Deposit2014().setMapDraft(result);		
													update();
												}
											
											});
											
										}
										else if(t.equals("ECPN")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(), digitalDepositTreeNode.getD2Deposit2014().getMap(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {
													digitalDepositTreeNode.getD2Deposit2014().setMap(result);
													digitalDepositTreeNode.getD2Deposit2014().setMapDraft(result);		
													update();
												}
											
											});
											
										}
										else if(t.equals("Memoria predefinida")){
									
											ListBox lb1 = (ListBox) flex_table.getWidget(1,1);
											String text = lb1.getSelectedItemText();
											MemoryTemplate m = new MemoryTemplate();
											for (MemoryTemplate mt : vector) {
												if (mt.getName().equals(text))
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
										else if(t.equals("Memoria")){
											ListBox ej = (ListBox) flex_table.getWidget(1, 1);
											String ejercicio = ej.getSelectedItemText();
											
											inma.saveDeposit(enterprise.getDocument(), enterprise.getDomain(),false, new AsyncCallback<Void>() {
												
												@Override
												public void onSuccess(Void result) {
													update();
												}
												
												@Override
												public void onFailure(Throwable caught) {
													
												}
											});
										}
										
									}
								};
								popup.addStyleName("gwt-PopupPanel-template");
								popup.setGlassEnabled(true);
								popup.show();
							}

							@Override
							public void onFailure(Throwable caught) {
								
							}
							
				});
			}
		});
	}
	
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
		if(!textMode){
			DepositDialog popup = new DepositDialog(
					"Borrar Deposito", "delete", enterprise,
					"", null) {
			
				@Override
				protected void onCancel() {
					hide();

				}

				@Override
				protected void onAccept() {
					hide();
					inma.delete(enterprise.getDomain(), enterprise.getDocument(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Void result) {
				
							digitalDepositTreeNode.removeItems();
							
							digitalDepositTreeNode.select(fiscalTree);
						}
					});
			}
			
			};
			popup.addStyleName("gwt-PopupPanel-template");
			popup.setGlassEnabled(true);
			popup.show();
		
		}
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
		PageAbs p = (PageAbs) pagesPanel.getWidget(0);
		p.dump(digitalDepositTreeNode.getD2Deposit2014());



		/*switch (page) {
		case "IDA":
			Header1 hA1 = (Header1) pagesPanel.getWidget(0);
			hA1.dump(new D2DepositTreeObject(), page);
			break;
		case "IDP":
			Header1 hP1 = (Header1) pagesPanel.getWidget(0);
			hP1.dump(new D2DepositTreeObject(), page);
			break;
		case "BA":
			PageH2 hA2 = (PageH2) pagesPanel.getWidget(0);
			hA2.dump(hA2.d2DepositObject, page);
			break;
		case "BP":
			PageH2 hP2 = (PageH2) pagesPanel.getWidget(0);
			hP2.dump(hP2.d2DepositObject, page);
			break;
		case "PA":
			PageH3 hA3 = (PageH3) pagesPanel.getWidget(0);
			hA3.dump(hA3.d2DepositObject, page);
			break;
		case "PP":
			PageH3 hP3 = (PageH3) pagesPanel.getWidget(0);
			hP3.dump(hP3.d2DepositObject, page);
			break;
		case "PNA":
			PageH4 hA4 = (PageH4) pagesPanel.getWidget(0);
			hA4.dump(hA4.d2DepositObject, page);
			break;
		case "PNP":
			PageH4 hP4 = (PageH4) pagesPanel.getWidget(0);
			hP4.dump(hP4.d2DepositObject, page);
			break;

		case "IMA":
			PageH5 hA5 = (PageH5) pagesPanel.getWidget(0);
			hA5.dump(new D2DepositTreeObject(), page);
			break;
		case "IMP":
			PageH5 hP5 = (PageH5) pagesPanel.getWidget(0);
			hP5.dump(new D2DepositTreeObject(), page);
			break;

		case "MAT1":
			FreeText ftA = (FreeText) pagesPanel.getWidget(0);
			ftA.dump(ftA.d2DepositObject, page);
			break;
		case "MPT1":
			FreeText ftP = (FreeText) pagesPanel.getWidget(0);
			ftP.dump(ftP.d2DepositObject, page);
			break;

		case "MAT2":
			FreeText ftA2 = (FreeText) pagesPanel.getWidget(0);
			ftA2.dump(ftA2.d2DepositObject, page);
			break;
		case "MPT2":
			FreeText ftP2 = (FreeText) pagesPanel.getWidget(0);
			ftP2.dump(ftP2.d2DepositObject, page);
			break;

		case "MAT3":
			FreeText ftA3 = (FreeText) pagesPanel.getWidget(0);
			ftA3.dump(ftA3.d2DepositObject, page);
			break;
		case "MPT3":
			FreeText ftP3 = (FreeText) pagesPanel.getWidget(0);
			ftP3.dump(ftP3.d2DepositObject, page);
			break;

		case "MA3":
			PageM3_2 pA32 = (PageM3_2) pagesPanel.getWidget(0);
			pA32.dump(pA32.d2DepositObject, page);
			break;
		case "MP3":
			PageM3_2 pP32 = (PageM3_2) pagesPanel.getWidget(0);
			pP32.dump(pP32.d2DepositObject, page);
			break;

		case "MAT4":
			FreeText ftA4 = (FreeText) pagesPanel.getWidget(0);
			ftA4.dump(ftA4.d2DepositObject, page);
			break;
		case "MPT4":
			FreeText ftP4 = (FreeText) pagesPanel.getWidget(0);
			ftP4.dump(ftP4.d2DepositObject, page);
			break;

		case "MAT5":
			FreeText ftA5 = (FreeText) pagesPanel.getWidget(0);
			ftA5.dump(ftA5.d2DepositObject, page);;
			break;
		case "MPT5":
			FreeText ftP5 = (FreeText) pagesPanel.getWidget(0);
			ftP5.dump(ftP5.d2DepositObject, page);
			break;

		case "MA5":
			PageM5_2 pA52 = (PageM5_2) pagesPanel.getWidget(0);
			pA52.dump(pA52.d2DepositObject, page);
			break;
		case "MP5":
			PageM5_2 pP52 = (PageM5_2) pagesPanel.getWidget(0);
			pP52.dump(pP52.d2DepositObject, page);
			break;

		case "MAT6":
			FreeText ftA6 = (FreeText) pagesPanel.getWidget(0);
			ftA6.dump(ftA6.d2DepositObject, page);
			break;
		case "MPT6":
			FreeText ftP6 = (FreeText) pagesPanel.getWidget(0);
			ftP6.dump(ftP6.d2DepositObject, page);
			break;

		case "MA6":
			PageM6_2 pA62= (PageM6_2) pagesPanel.getWidget(0);
			pA62.dump(pA62.d2DepositObject, page);
			break;
		case "MP6":
			PageM6_2 pP62= (PageM6_2) pagesPanel.getWidget(0);
			pP62.dump(pP62.d2DepositObject, page);
			break;
			
		case "MAT7":
			FreeText ftA7 = (FreeText) pagesPanel.getWidget(0);
			ftA7.dump(ftA7.d2DepositObject, page);
			break;
		case "MPT7":
			FreeText ftP7 = (FreeText) pagesPanel.getWidget(0);
			ftP7.dump(ftP7.d2DepositObject, page);
			break;

		case "MA7":
			PageM7_2 pA72 = (PageM7_2) pagesPanel.getWidget(0);
			pA72.dump(pA72.d2DepositObject, page);
			break;
		case "MP7":
			PageM7_2 pP72 = (PageM7_2) pagesPanel.getWidget(0);
			pP72.dump(pP72.d2DepositObject, page);
			break;

		case "MAT8":
			FreeText ftA8 = (FreeText) pagesPanel.getWidget(0);
			ftA8.dump(ftA8.d2DepositObject, page);
			break;
		case "MPT8":
			FreeText ftP8 = (FreeText) pagesPanel.getWidget(0);
			ftP8.dump(ftP8.d2DepositObject, page);
			break;

		case "MAT9":
			FreeText ftA9 = (FreeText) pagesPanel.getWidget(0);
			ftA9.dump(ftA9.d2DepositObject, page);
			break;
		case "MPT9":
			FreeText ftP9 = (FreeText) pagesPanel.getWidget(0);
			ftP9.dump(ftP9.d2DepositObject, page);
			break;

		case "MA10":
			PageM10 pA10 = (PageM10) pagesPanel.getWidget(0);
			pA10.dump(new D2DepositTreeObject(), page);
			break;
		case "MP10":
			PageM10 pP10 = (PageM10) pagesPanel.getWidget(0);
			pP10.dump(new D2DepositTreeObject(), page);
			break;

		case "MAT11":
			FreeText ftA11 = (FreeText) pagesPanel.getWidget(0);
			ftA11.dump(ftA11.d2DepositObject, page);
			break;
		case "MPT11":
			FreeText ftP11 = (FreeText) pagesPanel.getWidget(0);
			ftP11.dump(ftP11.d2DepositObject, page);
			break;

		case "MA11":
			PageM11_2 pA112 = (PageM11_2) pagesPanel.getWidget(0);
			pA112.dump(new D2DepositTreeObject(), page);
			break;
		case "MP11":
			PageM11_2 pP112 = (PageM11_2) pagesPanel.getWidget(0);
			pP112.dump(new D2DepositTreeObject(), page);
			break;

		case "MAT12":
			FreeText ftA12 = (FreeText) pagesPanel.getWidget(0);
			ftA12.dump(ftA12.d2DepositObject, page);
			break;
		case "MPT12":
			FreeText ftP12 = (FreeText) pagesPanel.getWidget(0);
			ftP12.dump(ftP12.d2DepositObject, page);
			break;

		case "MA12":
			PageM12_2 pA122 = (PageM12_2) pagesPanel.getWidget(0);
			pA122.dump(new D2DepositTreeObject(), page);
			break;
		case "MP12":
			PageM12_2 pP122 = (PageM12_2) pagesPanel.getWidget(0);
			pP122.dump(new D2DepositTreeObject(), page);
			break;

		case "MAT13":
			FreeText ftA13 = (FreeText) pagesPanel.getWidget(0);
			ftA13.dump(ftA13.d2DepositObject, page);
			break;
		case "MPT13":
			FreeText ftP13 = (FreeText) pagesPanel.getWidget(0);
			ftP13.dump(ftP13.d2DepositObject, page);
			break;

		case "MA13":
			PageM13_2 pA132 = (PageM13_2) pagesPanel.getWidget(0);
			pA132.dump(new D2DepositTreeObject(), page);
			break;
		case "MP13":
			PageM13_2 pP132 = (PageM13_2) pagesPanel.getWidget(0);
			pP132.dump(new D2DepositTreeObject(), page);
			break;

		case "MAT14":
			FreeText ftA14 = (FreeText) pagesPanel.getWidget(0);
			ftA14.dump(ftA14.d2DepositObject, page);
			break;
		case "MPT14":
			FreeText ftP14 = (FreeText) pagesPanel.getWidget(0);
			ftP14.dump(ftP14.d2DepositObject, page);
			break;

		case "MA14":
			PageM14_2 pA142 = (PageM14_2) pagesPanel.getWidget(0);
			pA142.dump(new D2DepositTreeObject(), page);
			break;
		case "MP14":
			PageM14_2 pP142 = (PageM14_2) pagesPanel.getWidget(0);
			pP142.dump(new D2DepositTreeObject(), page);
			break;

		case "MA15":
			PageM15 pA15 = (PageM15) pagesPanel.getWidget(0);
			pA15.dump(new D2DepositTreeObject(), page);
			break;
		case "MP15":
			PageM15 pP15 = (PageM15) pagesPanel.getWidget(0);
			pP15.dump(new D2DepositTreeObject(), page);
			break;

		case "A":
			PageF1 pf1 = (PageF1) pagesPanel.getWidget(0);
			pf1.dump(new D2DepositTreeObject(), page);
			break;
		case "PR":
			PageF2 pf2 = (PageF2)  pagesPanel.getWidget(0);
			pf2.dump(new D2DepositTreeObject(), page);
			break;
		case "H":
			PageF3  pf3 = (PageF3)  pagesPanel.getWidget(0);
			pf3.dump(new D2DepositTreeObject(), page);
			break;
		default:
			break;
		}
		*/
	}
	
	public void paintHeaderTable(String text){//,String type) {
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
		
		headerTable.setWidget(0, 2, new Label(""));//type));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerTable.setWidget(1, 0, new Label("2014"));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerPanel.setWidget(headerTable);
	}

	//-------------------- Getters & Setters
	
	public Map<String, String> getD2Deposit2014() {
		return d2Deposit2014;
	}

	public void setD2Deposit2014(Map<String, String> d2Deposit2014) {
		this.d2Deposit2014 = d2Deposit2014;
	}

	public DigitalDepositTreeNode getDigitalDepositTreeNode() {
		return digitalDepositTreeNode;
	}

	public void setDigitalDepositTreeNode(
			DigitalDepositTreeNode digitalDepositTreeNode) {
		this.digitalDepositTreeNode = digitalDepositTreeNode;
	}
	
	
}
