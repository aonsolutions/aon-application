package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.SingleUploader;

import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.fiscal.deposit.client.D2DepositTreeObject;
import com.esferalia.aon.gwt.fiscal.deposit.client.Deposit;
import com.esferalia.aon.gwt.fiscal.deposit.client.DigitalDepositFreeTextTreeNode;
import com.esferalia.aon.gwt.fiscal.deposit.client.DigitalDepositTreeNode;
import com.esferalia.aon.gwt.fiscal.deposit.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
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
	private D2Deposit2014 d2Deposit2014;
	
	
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

	public void setImportAllButton(Button importAllButton) {
		this.importAllButton = importAllButton;
	}
	// selected item content panel
	@UiField
	FlowPanel pagesPanel;
	SingleUploader upload;
	Enterprise enterprise;
	//String page;
	Boolean textMode;
	MemoryTemplate memoryTemplate;
	
	Integer year;

	
	DigitalDepositTreeNode digitalDepositTreeNode;
	DigitalDepositFreeTextTreeNode digitalDepositFreeTextTreeNode;
	

	Deposit deposit;
	
	
	//-------------------- Constructors
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas está creado.
	 * @param enterprise
	 * @param page
	 * @param ddtn
	 * @param ft
	 */
	public NormalizedMemory(DigitalDepositTreeNode ddtn, Deposit deposit) {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
        deleteButton = new Button();
		cancelButton = new Button();
		importAllButton = new Button();
		enterprise = ddtn.getD2Deposit2014().getEnterprise();
		year = ddtn.getD2Deposit2014().getYear();
		//this.page = page;
		this.textMode = false;
		digitalDepositTreeNode = ddtn;
		this.deposit = deposit;		

		headerPanel = new SimplePanel();
		pagesPanel = new FlowPanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Deposito");
		deleteButton.setVisible(true);
		importAllButton.setVisible(true);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		d2Deposit2014 = D2DepositTreeObjectToD2Deposit2014(ddtn.getD2Deposit2014());
		/*inma.getSchema(enterprise.getDocument(), enterprise.getDomain(), textMode,new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				d2Deposit2014 = result;
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});*/
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
	public NormalizedMemory(Enterprise enterprise,MemoryTemplate mt, DigitalDepositFreeTextTreeNode ddtn) {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		newButton = new Button();	
		importButton = new Button();
       	importTextButton = new Button();
        deleteButton = new Button();
        generateFileButton = new Button();
		cancelButton = new Button();
		importAllButton = new Button();
		this.enterprise = enterprise;
		this.digitalDepositFreeTextTreeNode = ddtn;
		//this.page = page;
		this.textMode = true;
		this.memoryTemplate = mt;
		
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Deposito");
		newButton.setVisible(true);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		generateFileButton.setVisible(false);
		importAllButton.setVisible(false);
		deleteButton.setVisible(true);
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
	public NormalizedMemory(Boolean type, DigitalDepositTreeNode ddtn, Deposit deposit) {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		importAllButton = new Button();
		digitalDepositTreeNode = ddtn;
		this.textMode = false;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		enterprise = ddtn.getD2Deposit2014().getEnterprise();
		year = ddtn.getD2Deposit2014().getYear();
		this.deposit = deposit;
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		importAllButton.setVisible(false);
		if(type){
			newButton.setVisible(true);
			saveButton.setVisible(false);
			importTextButton.setVisible(false);
			generateFileButton.setVisible(false);
		}
		
		depositType.setText("Deposito");
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
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importButton = new Button();
		importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		digitalDepositFreeTextTreeNode = ddtn;
		importAllButton = new Button();
		this.textMode = true;
		
		enterprise = e;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		importAllButton.setVisible(false);
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
			 popup = new DepositDialog("Nuevo Deposito","new2", enterprise,url,null, false, null) {
				
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					hide();
					TextBox tb = (TextBox) flex_table.getWidget(0, 1);
					
					inma.createTextMemory(enterprise.getDomain(),tb.getValue(),new AsyncCallback<MemoryTemplate>() {
						
						@Override
						public void onSuccess(MemoryTemplate result) {
							D2DepositTreeObject ddto = new D2DepositTreeObject(enterprise, 2014);
							result.setD2Deposit2014(ddto);
							digitalDepositFreeTextTreeNode.items(result);							
						}
						
						@Override
						public void onFailure(Throwable caught) {	}
					});
				}
			};
		}
		else{
			popup = new DepositDialog("Nuevo Deposito","new", enterprise,url,null, false, null) {
			
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					ListBox lb = (ListBox) flex_table.getWidget(0, 1);
					TextBox tb = (TextBox) flex_table.getWidget(1, 1);
					hide();
					
					inma.createD2Deposit(enterprise.getDomain(), enterprise.getId(), tb.getValue(), lb.getSelectedItemText() , year, new AsyncCallback<Map<String, String>>() {

								@Override
								public void onFailure(Throwable caught) {
									
								}

								@Override
								public void onSuccess(Map<String, String> result) {
									d2Deposit2014 = new D2Deposit2014(enterprise.getDomain(), enterprise.getDocument());
									d2Deposit2014.setMap(result);
									d2Deposit2014.setMapDraft(result);
									digitalDepositTreeNode.setIsMa(false);
									digitalDepositTreeNode.setIsMemory(false);
									digitalDepositTreeNode.getD2Deposit2014().setMap(result);
									digitalDepositTreeNode.getD2Deposit2014().setMapDraft(result);
									digitalDepositTreeNode.items();
									digitalDepositTreeNode.setState(true);
									newButton.setVisible(false);
									saveButton.setVisible(true);
									saveButton.setEnabled(false);
									deleteButton.setVisible(true);
									generateFileButton.setVisible(true);		
									importAllButton.setVisible(true);
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
			inma.saveDeposit(memoryTemplate.getId().toString(),enterprise.getDomain(),textMode, year, new AsyncCallback<Void>() {
				
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
			// NUEVO SAVE
			inma.saveDeposit(enterprise.getDocument(),enterprise.getDomain(), d2Deposit2014, false, new AsyncCallback<Void>() {
			
				@Override
				public void onSuccess(Void result) {
					
					digitalDepositTreeNode.getD2Deposit2014().setModify(false);
					digitalDepositTreeNode.getD2Deposit2014().setMap(digitalDepositTreeNode.getD2Deposit2014().getMapDraft());
					
					d2Deposit2014.setModify(false);
					d2Deposit2014.setMap(d2Deposit2014.getMapDraft());
					
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
		
		//String url = GWT.getModuleBaseURL()+"gwt_deposit_upload";
		DepositDialog popup = new DepositDialog("Importar Deposito","import", enterprise, GWT.getModuleBaseURL(), null, false, null) {

			@Override
			protected void onAccept() {
				inma.saveDeposit(enterprise.getDocument(), enterprise.getDomain(),false, year, new AsyncCallback<Void>() {
					
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
			digitalDepositTreeNode.getD2Deposit2014().setModify(false);
			digitalDepositTreeNode.getD2Deposit2014().setMapDraft(null);
			
			d2Deposit2014.setMapDraft(d2Deposit2014.getMap());
			d2Deposit2014.setModify(false);
			
			saveButton.setEnabled(false);
			cancelButton.setVisible(false);
		
			update();
		
			
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
								String url = GWT.getModuleBaseURL();
										//+ "gwt_deposit_upload";
								mts = result;
								DepositDialog popup = new DepositDialog(
										"Importar", "importAll", enterprise,
										url, result, false, null) {
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
										
										if(t.equals("Balance (I.S.)")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(),d2Deposit2014.getMapDraft(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {		
													d2Deposit2014.setMapDraft(result);
													saveButton.setEnabled(true);
													cancelButton.setEnabled(true);
													update();
												}
											
											});
											
										
											
										}
										else if(t.equals("Perdidas y ganancias (I.S.)")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(), d2Deposit2014.getMapDraft(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {
													d2Deposit2014.setMapDraft(result);
													saveButton.setEnabled(true);
													cancelButton.setEnabled(true);
													update();
												}
											
											});
											
										}
										else if(t.equals("ECPN (I.S.)")){
											ListBox ej = (ListBox) flex_table.getWidget(2, 1);
											String ejercicio = ej.getSelectedItemText();
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(), d2Deposit2014.getMapDraft(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {
													d2Deposit2014.setMapDraft(result);
													saveButton.setEnabled(true);
													cancelButton.setEnabled(true);
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
													enterprise.getDocument(), d2Deposit2014.getMapDraft(), 
													new AsyncCallback<Map<String, String>>() {

														@Override
														public void onFailure(
																Throwable caught) {
														}

														@Override
														public void onSuccess(Map<String, String> result) {
															d2Deposit2014.setMapDraft(result);
 															/*for(String obj : result.keySet()){
																if(d2Deposit2014.getMapDraft().containsKey(obj)) digitalDepositTreeNode.getD2Deposit2014().getMapDraft().remove(obj);
																d2Deposit2014.getMapDraft().put(obj, result.get(obj));
						
															}*/
 															saveButton.setEnabled(true);
 															cancelButton.setEnabled(true);
															update();
														}
													});
										}
										else if(t.equals("Memoria (Deposito.xml)")){
											ListBox ej = (ListBox) flex_table.getWidget(1, 1);
											String ejercicio = ej.getSelectedItemText();
											
											inma.importAll(t, ejercicio, null, enterprise.getDomain(), enterprise.getDocument(), d2Deposit2014.getMapDraft(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onFailure(
														Throwable caught) {
												}

												@Override
												public void onSuccess(Map<String, String> result) {
													d2Deposit2014.setMapDraft(result);
													saveButton.setEnabled(true);
													cancelButton.setEnabled(true);
													paintHeaderTable("Cuentas Anuales", result.get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
													update();
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
										url, result, false, null) {
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
												enterprise.getDocument(), d2Deposit2014.getMapDraft(),
												new AsyncCallback<Map<String, String>>() {

													@Override
													public void onFailure(Throwable caught) {
														
													}

													@Override
													public void onSuccess(Map<String, String> result) {
														d2Deposit2014.setMapDraft(result);
														saveButton.setEnabled(true);
														cancelButton.setEnabled(true);
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
					"", null, false, null) {
			
				@Override
				protected void onCancel() {
					hide();
				}

				@Override
				protected void onAccept() {
					hide();
					inma.delete(enterprise.getDomain(), enterprise.getDocument(), year, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Void result) {
							digitalDepositTreeNode.removeItems();
							digitalDepositTreeNode.select(deposit);
						}
					});
				}
			
			};
			popup.addStyleName("gwt-PopupPanel-template");
			popup.setGlassEnabled(true);
			popup.show();
		}
		else{
			DepositDialog popup = new DepositDialog(
					"Borrar Deposito", "delete", enterprise,
					"", null, false, null) {
			
				@Override
				protected void onCancel() {
					hide();
				}

				@Override
				protected void onAccept() {
					hide();
					inma.deleteFreeText(enterprise.getDomain(), memoryTemplate.getId(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Void result) {
							for(Integer i = 0; i< digitalDepositFreeTextTreeNode.getChildCount(); i++){
								if(digitalDepositFreeTextTreeNode.getChild(i).getTitle().equals(memoryTemplate.getId().toString())){
									digitalDepositFreeTextTreeNode.removeItem(digitalDepositFreeTextTreeNode.getChild(i));
								}
							}
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
		
		inma.getDepositExercises(enterprise.getDomain(), new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String[] result) {
				DepositDialog popup = new DepositDialog(
						"Exportar Deposito", "export", enterprise,
						"", null, false, result) {
					@Override
					protected void onAccept() {
						hide();
						ListBox listBox = (ListBox) flex_table.getWidget(0, 1);
						String ejercicio = listBox.getSelectedItemText();
						String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_deposit/"
				            	+ "?domain_id=" + Integer.toString(enterprise.getDomain())
				            	+ "&year="+ ejercicio;
						Window.open( fileDownloadURL, "_blank",null);
					}

					@Override
					protected void onCancel() {
						hide();
					}	
				};
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();
			}
		});
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
		if(!textMode){
			PageAbs w = (PageAbs) widget;
			w.dump(d2Deposit2014);
		}
		if(pagesPanel.getWidgetCount() > 0)pagesPanel.remove(0);
		pagesPanel.add(widget);
	}

	
	public void setPagesPanel(Widget widget, Boolean nuevo){
		if(!nuevo && !textMode){
			PageAbs w = (PageAbs) widget;
			w.dump(d2Deposit2014);
		}
		if(pagesPanel.getWidgetCount() > 0) pagesPanel.remove(0);
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
	
	public void update(){
		PageAbs p = (PageAbs) pagesPanel.getWidget(0);
		p.dump(d2Deposit2014);
	}
	
	public void paintHeaderTable(String text,String type,String year) {
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
		
		headerTable.setWidget(0, 2, new Label(type));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerTable.setWidget(1, 0, new Label(year));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalRegistroMercantil());
		
		headerPanel.setWidget(headerTable);
	}

	//-------------------- Getters & Setters
	
	public D2Deposit2014 getD2Deposit2014() {
		return d2Deposit2014;
	}

	public void setD2Deposit2014(D2Deposit2014 d2Deposit2014) {
		this.d2Deposit2014 = d2Deposit2014;
	}

	public DigitalDepositTreeNode getDigitalDepositTreeNode() {
		return digitalDepositTreeNode;
	}

	public void setDigitalDepositTreeNode(
			DigitalDepositTreeNode digitalDepositTreeNode) {
		this.digitalDepositTreeNode = digitalDepositTreeNode;
	}
	
	public D2Deposit2014 D2DepositTreeObjectToD2Deposit2014(D2DepositTreeObject ddto){
		D2Deposit2014 d2 =  new D2Deposit2014(ddto.getDomain(), ddto.getEnterprise().getDocument());
		d2.setMap(ddto.getMap());
		d2.setMapDraft(ddto.getMapDraft());
		d2.setYear(ddto.getYear());
		return d2;
		
	}
	
	public Label getDepositType() {
		return depositType;
	}

	public void setDepositType(Label depositType) {
		this.depositType = depositType;
	}

	public Button getNewButton() {
		return newButton;
	}

	public void setNewButton(Button newButton) {
		this.newButton = newButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(Button saveButton) {
		this.saveButton = saveButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}

	public Button getImportButton() {
		return importButton;
	}

	public void setImportButton(Button importButton) {
		this.importButton = importButton;
	}

	public Button getImportTextButton() {
		return importTextButton;
	}

	public void setImportTextButton(Button importTextButton) {
		this.importTextButton = importTextButton;
	}

	public Button getImportAllButton() {
		return importAllButton;
	}
	
	public void setGenerateFileButton(Button generateFileButton) {
		this.generateFileButton = generateFileButton;
	}

	public Button getGenerateFileButton() {
		return generateFileButton;
	}
	
}
