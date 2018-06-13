package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.client.Deposit;
import com.esferalia.aon.gwt.fiscal.deposit.client.DigitalDepositFreeTextTreeNode;
import com.esferalia.aon.gwt.fiscal.deposit.client.DigitalDepositTreeNode;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import gwtupload.client.SingleUploader;

public class NormalizedMemory extends ResizeComposite {
	
	interface NormalizedMemoryBinder extends UiBinder<Widget, NormalizedMemory> {
	}
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	private static final NormalizedMemoryBinder MODEL_NORMALIZED_MEMORY_BINDER = GWT
			.create(NormalizedMemoryBinder.class);

	private NormalizedMemory normalizedMemory;
	private D2Deposit d2Deposit;
	
	@UiField Label depositType;
	@UiField Button newButton;
	@UiField Button saveButton;
	@UiField Button cancelButton;
	@UiField Button deleteButton;
	@UiField Button importButton;
	@UiField Button generateFileButton;
	@UiField Button importTextButton;
	@UiField SimplePanel headerPanel;
	@UiField Button importAllButton;
	@UiField Button downloadButton;
	@UiField Button downloadButtonPdf;
	@UiField Anchor download;							
	@UiField FlowPanel pagesPanel;
	
	SingleUploader upload;
	Enterprise enterprise;
	Boolean textMode;
	MemoryTemplate memoryTemplate;
	Integer year;
	DigitalDepositTreeNode digitalDepositTreeNode;
	DigitalDepositFreeTextTreeNode digitalDepositFreeTextTreeNode;
	Deposit deposit;
	AonData aonData;
	
	public void setImportAllButton(Button importAllButton) {
		this.importAllButton = importAllButton;
	}
	
	//-------------------- Constructors
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas está creado.
	 * @param enterprise
	 * @param page
	 * @param ddtn
	 * @param ft
	 */
	public NormalizedMemory(AonData aonData, DigitalDepositTreeNode ddtn, Deposit deposit) {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		saveButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
        deleteButton = new Button();
		cancelButton = new Button();
		importAllButton = new Button();
		downloadButton = new Button();
		downloadButtonPdf = new Button();
		
		enterprise = ddtn.getD2Deposit().getEnterprise();
		year = ddtn.getD2Deposit().getYear();
		this.textMode = false;
		digitalDepositTreeNode = ddtn;
		this.deposit = deposit;	
		this.aonData = aonData;
		headerPanel = new SimplePanel();
		pagesPanel = new FlowPanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		depositType.setText("Deposito");
		deleteButton.setVisible(true);
		importAllButton.setVisible(true);
		downloadButton.setVisible(true);
		downloadButtonPdf.setVisible(false); // TODO cambiar a true para activar descarga en pdf
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		d2Deposit = ddtn.getD2Deposit();
		
		saveButton.setEnabled(d2Deposit.getModify());
		cancelButton.setVisible(d2Deposit.getModify());
		
/*		inma.isModify(enterprise.getDocument(), year, new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				saveButton.setEnabled(result);
				cancelButton.setVisible(result);		
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});*/
	}
	
	/**
	 * Constructor NormalizedMemory,
	 * El deposito de cuentas está creado,
	 * Memoria predefinida (solo texto libre).
	 * @param enterprise
	 * @param page
	 * @param mt
	 */
	public NormalizedMemory(AonData aonData, Enterprise enterprise,MemoryTemplate mt, DigitalDepositFreeTextTreeNode ddtn) {
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
		downloadButton = new Button();
		downloadButtonPdf = new Button();
		
		this.enterprise = enterprise;
		this.digitalDepositFreeTextTreeNode = ddtn;
		//this.page = page;
		this.textMode = true;
		this.memoryTemplate = mt;
		this.aonData = aonData;
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
		downloadButton.setVisible(false);
		downloadButtonPdf.setVisible(false);
		deleteButton.setVisible(true);

		inma.isModify(mt.getId().toString(), year, new AsyncCallback<Boolean>() {
			
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
	public NormalizedMemory(AonData aonData, Boolean type, DigitalDepositTreeNode ddtn, Deposit deposit) {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		depositType = new Label();
		newButton = new Button();
		importButton = new Button();
       	importTextButton = new Button();
		saveButton = new Button();
		generateFileButton = new Button();
		importAllButton = new Button();
		downloadButton = new Button();
		downloadButtonPdf = new Button();
		digitalDepositTreeNode = ddtn;
		this.textMode = false;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		enterprise = ddtn.getD2Deposit().getEnterprise();
		year = ddtn.getD2Deposit().getYear();
		this.deposit = deposit;
		this.aonData = aonData;
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		importAllButton.setVisible(false);
		downloadButton.setVisible(false);
		downloadButtonPdf.setVisible(false);
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
	public NormalizedMemory(AonData aonData, Boolean type, DigitalDepositFreeTextTreeNode ddtn, Enterprise e) {
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
		downloadButton = new Button();
		downloadButtonPdf = new Button();
		this.textMode = true;
		this.aonData = aonData;
		enterprise = e;
		pagesPanel = new FlowPanel();
		headerPanel = new SimplePanel();
		
		Widget ui = MODEL_NORMALIZED_MEMORY_BINDER.createAndBindUi(this);
		initWidget(ui);
		importButton.setVisible(false);
		importTextButton.setVisible(false);
		importAllButton.setVisible(false);
		downloadButton.setVisible(false);
		downloadButtonPdf.setVisible(false);
		if(type){
			importButton.setVisible(false);
			importTextButton.setVisible(false);
			newButton.setVisible(true);
			saveButton.setVisible(false);
			generateFileButton.setVisible(false);
		}
		
		depositType.setText("Deposito");
	}

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		String url = GWT.getModuleBaseURL()+"gwt_deposit_upload";
		DepositDialog popup ;
		if(textMode){
			 popup = new DepositDialog("Nuevo Deposito","new2", enterprise,url,null, false, null, year) {
				
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					hide();
					TextBox tb = (TextBox) flex_table.getWidget(0, 1);
					
					inma.createTextMemory(enterprise.getDomain(),tb.getValue(),year, new AsyncCallback<MemoryTemplate>() {
						
						@Override
						public void onSuccess(MemoryTemplate result) {
							D2Deposit ddto = new D2Deposit()
									.setDomain(new Domain().setId(enterprise.getDomain()))
									.setEnterprise(enterprise)
									.setYear(year);
							result.setD2Deposit(ddto);
							digitalDepositFreeTextTreeNode.items(result);							
						}
						
						@Override
						public void onFailure(Throwable caught) {	}
					});
				}
			};
		}
		else{
			popup = new DepositDialog("Nuevo Deposito","new", enterprise,url,null, false, null, year) {
			
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
									Domain domain = new Domain().setName(getCurrentDomainName()).setId(enterprise.getDomain());
									d2Deposit = new D2Deposit(domain, enterprise.getDocument(), enterprise.getName());
									d2Deposit.setMap(result);
									d2Deposit.setMapDraft(result);
									digitalDepositTreeNode.setIsMa(false);
									digitalDepositTreeNode.setIsMemory(false);
									digitalDepositTreeNode.getD2Deposit().setMap(result);
									digitalDepositTreeNode.getD2Deposit().setMapDraft(result);
									digitalDepositTreeNode.items();
									digitalDepositTreeNode.setState(true);
									newButton.setVisible(false);
									saveButton.setVisible(true);
									saveButton.setEnabled(false);
									deleteButton.setVisible(true);
									generateFileButton.setVisible(true);		
									importAllButton.setVisible(true);
									downloadButton.setVisible(true);
									downloadButtonPdf.setVisible(false); // TODO cambiar a true para activar descarga en pdf
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
			inma.saveDeposit(enterprise.getDocument(),enterprise.getDomain(), d2Deposit, false, year, new AsyncCallback<Void>() {
			
				@Override
				public void onSuccess(Void result) {	
					digitalDepositTreeNode.getD2Deposit().setModify(false);
					digitalDepositTreeNode.getD2Deposit().setMap(digitalDepositTreeNode.getD2Deposit().getMapDraft());
					
					d2Deposit.setModify(false);
					d2Deposit.setMap(d2Deposit.getMapDraft());
					
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
		DepositDialog popup = new DepositDialog("Importar Deposito","import", enterprise, GWT.getModuleBaseURL(), null, false, null, year) {

			@Override
			protected void onAccept() {
				inma.saveDeposit(enterprise.getDocument(), enterprise.getDomain(),false, year, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						update();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
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
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {		
		if(textMode){
			inma.clearSession(memoryTemplate.getId().toString(), year, new AsyncCallback<Void>() {
				
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
			digitalDepositTreeNode.getD2Deposit().setModify(false);
			digitalDepositTreeNode.getD2Deposit().setMapDraft(null);
			
			d2Deposit.setMapDraft(d2Deposit.getMap());
			d2Deposit.setModify(false);
			
			saveButton.setEnabled(false);
			cancelButton.setVisible(false);
		
			update();
		
			
		}
	}
	
	Vector<MemoryTemplate> mts ;
	
	
	@UiHandler("importAllButton")
	void onImportAllButtonClick(ClickEvent event){
		inma.getDigitalDepositTemplates(aonData.getDomain().getParentId(), year, new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {
				String url = GWT.getModuleBaseURL();
				mts = result;
				DepositDialog popup = new DepositDialog("Importar", "importAll", enterprise, url, result, false, null, year) {
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
						
						if(t.equals("Memoria predefinida")){
							
							ListBox lb1 = (ListBox) flex_table.getWidget(1,1);
							String text = lb1.getSelectedItemText();
							MemoryTemplate m = new MemoryTemplate();
							for (MemoryTemplate mt : vector) {
								if (mt.getName().equals(text))
									m = mt;
							}
							inma.updateTexts(aonData, m, d2Deposit.getMapDraft(),new AsyncCallback<Map<String, String>>() {
						
								@Override	
								public void onFailure(
										Throwable caught) {
								}
								
								@Override
								public void onSuccess(Map<String, String> result) {
									d2Deposit.setMapDraft(result);
									saveButton.setEnabled(true);
									cancelButton.setEnabled(true);
									update();
								}
							});
						} else {
							String ejercicio = "";
							if(t.equals("Balance (I.S.)")){
								ListBox ej = (ListBox) flex_table.getWidget(2, 1);
								ejercicio = ej.getSelectedItemText();
								
							} else if(t.equals("Perdidas y ganancias (I.S.)")){
								ListBox ej = (ListBox) flex_table.getWidget(2, 1);
								ejercicio = ej.getSelectedItemText();
							} else if(t.equals("ECPN (I.S.)")){
								ListBox ej = (ListBox) flex_table.getWidget(2, 1);
								ejercicio = ej.getSelectedItemText();
							} else if(t.equals("Memoria (Deposito.xml)")){
								ListBox ej = (ListBox) flex_table.getWidget(1, 1);
								ejercicio = ej.getSelectedItemText();
							}
							inma.importAll(aonData, t, ejercicio, null, enterprise.getDocument(), d2Deposit.getMapDraft(), getYear(), new AsyncCallback<Map<String, String>>() {	
								
								@Override public void onFailure(Throwable caught) {}
							
								@Override
								public void onSuccess(Map<String, String> result) {
									d2Deposit.setMapDraft(result);
									saveButton.setEnabled(true);
									cancelButton.setEnabled(true);
									if(t.equals("Memoria (Deposito.xml)")) paintHeaderTable("Cuentas Anuales", result.get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
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

			@Override public void onFailure(Throwable caught) {}	
		});
	}
	
	@UiHandler("importTextButton")
	void onImportTextButtonClick(ClickEvent event) {
		inma.getDigitalDepositTemplates(aonData.getDomain().getParentId(), year, new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {
				String url = GWT.getModuleBaseURL()
						+ "gwt_deposit_upload";
				mts = result;
				DepositDialog popup = new DepositDialog("Importar Textos", "importText", enterprise, url, result, false, null, year) {
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
						inma.updateTexts(aonData, m, d2Deposit.getMapDraft(),
								new AsyncCallback<Map<String, String>>() {
							
							@Override
							public void onFailure(Throwable caught) {
								
							}

							@Override
							public void onSuccess(Map<String, String> result) {
								d2Deposit.setMapDraft(result);
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

			@Override public void onFailure(Throwable caught) {}
		});
			
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if(!textMode){
			DepositDialog popup = new DepositDialog(
					"Borrar Deposito", "delete", enterprise,
					"", null, false, null, year) {
			
				@Override
				protected void onCancel() {
					hide();
				}

				@Override
				protected void onAccept() {
					hide();
					inma.delete(aonData, year, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Void result) {
							digitalDepositTreeNode.removeItems();
							digitalDepositTreeNode.select(deposit);
							
							//deposit.getNewContextMenu().addDeposit(year);
							//deposit.getToolbar().setVisible(true);
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
					"", null, false, null, year) {
			
				@Override
				protected void onCancel() {
					hide();
				}

				@Override
				protected void onAccept() {
					hide();
					inma.deleteFreeText(enterprise.getDomain(), memoryTemplate.getId(), year, new AsyncCallback<Void>() {
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
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_deposit/"
		            	+ "?domain_id=" + Integer.toString(enterprise.getDomain())
		            	+ "&year="+ year;
				Window.open( fileDownloadURL, "_blank",null);
			}
		});
	}
	
	@UiHandler("download")
	void onDownloadClick(ClickEvent event) {
		
	}
	
	public void setPagesPanel(Widget widget){
		if(!textMode){
			PageAbs w = (PageAbs) widget;
			w.dump(d2Deposit);
		}
		if(pagesPanel.getWidgetCount() > 0){
			pagesPanel.remove(0);
		}
		pagesPanel.add(widget);
	}

	
	public void setPagesPanel(Widget widget, Boolean nuevo){
		if(!nuevo && !textMode){
			PageAbs w = (PageAbs) widget;
			w.dump(d2Deposit);
		}
		if(pagesPanel.getWidgetCount() > 0) pagesPanel.remove(0);
		pagesPanel.add(widget);
	}
	
	
	
	public void update(){
		PageAbs p = (PageAbs) pagesPanel.getWidget(0);
		p.dump(d2Deposit);
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
	
	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}

	public void setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}

	public DigitalDepositTreeNode getDigitalDepositTreeNode() {
		return digitalDepositTreeNode;
	}

	public void setDigitalDepositTreeNode(
			DigitalDepositTreeNode digitalDepositTreeNode) {
		this.digitalDepositTreeNode = digitalDepositTreeNode;
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
	
	// -------------------- DOWNLOAD
	
	@UiHandler("downloadButton")
	void downExcel(ClickEvent event) {		
		download("excel");
	}
	
	@UiHandler("downloadButtonPdf")
	void downPdf(ClickEvent event) {
		download("pdf");
	}
	
	private void download(String format){
		DownloadDialog dd = new DownloadDialog(digitalDepositTreeNode.getIsMemory(), digitalDepositTreeNode.getIsMa(), getD2Deposit().getYear()) {
			
			@Override 
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				String options = "";
				for(Integer i = 1; i < flex_table.getRowCount(); i++){
					CheckBox cb = (CheckBox) flex_table.getWidget(i, 1);
					options = options + (cb.getValue() ? "T":"F");
				}
			
				String fileDownloadURL = GWT.getModuleBaseURL() + "/CCAAPrint"
	                	+ "?schemaId=" + String.valueOf(1)
	                	+ "&domainId=" + Integer.toString(enterprise.getDomain())
	                	+ "&domainName=" + getCurrentDomainName()
	                	+ "&cif=" + enterprise.getDocument()
						+ "&razonSocial=" + enterprise.getName()
						+ "&year=" + String.valueOf(year)
						+ "&type=" + depositType.getText()
						+ "&options=" + options
						+ "&format=" + format
						+ "&isMemory=" + digitalDepositTreeNode.getIsMemory();
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};
		dd.addStyleName("gwt-PopupPanel-template");
		dd.setGlassEnabled(true);
		dd.show();
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
}
