package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.mod200.client.AonFiscalModelHeader;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model2002020 extends DockLayoutPanel {
	
	public static final int BOX_LENGTH = 5;
	
	protected interface Model200PageCallback {	
		public Mod2002020Object getMod200Object();
		public void markAsDirty(); 
	}
	
	private PageAbs[] PAGES = new PageAbs[20];
	private int P00 = 0;
	
	protected Mod2002020Object mod200Object;
	private Model200Callback mod200Callback;
	
	AonToolbarButton initializeButton = new AonToolbarButton("");
	AonToolbarButton saveButton = new AonToolbarButton("");
	AonToolbarButton removeButton = new AonToolbarButton("");
	AonToolbarButton resetButton = new AonToolbarButton("");
	AonToolbarButton importAccountingButton = new AonToolbarButton("");
	AonToolbarButton aeatAccountingFileButton = new AonToolbarButton("");
	AonToolbarButton aeatFileButton = new AonToolbarButton("");
	AonToolbarButton aeatPrintButton = new AonToolbarButton("");

	SimpleLayoutPanel pageContainer = new SimpleLayoutPanel();
	
	FormPanel diskForm;
	Hidden modIdHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	Hidden userHidden;

	private Model200ModuleOptions options;
	
	private PopupPanel popup;
	protected final InlineLabel dirtyLabel = new InlineLabel();
	private boolean dirty;
	
	public Model2002020(Model200Callback mod200Callback, Mod2002020 mod200) {
		super(Unit.PX);
		
		popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		
		this.mod200Callback = mod200Callback;
		this.options = mod200Callback.getOptions();
		
		AON.ensureInjected();

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		modIdHidden = new Hidden("modId");
		formFlowPanel.add(modIdHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden("user");
		formFlowPanel.add(userHidden);		
		
		mod200Object = new Mod2002020Object(options, mod200);
		
		popup.center();
		
		clear();
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(mod200);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbar(), AonToolbar.HEIGTH);
//		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH); // Este toolbar por ahora no se usa en el Modelo 200
		addWest(getLinksPanel(), 300);
		add(pageContainer);		
		
		dump((mod200Object.getMod200().getId() == null));
		popup.hide();
		if (mod200Object.getMod200().getId() == null) 
			markAsDirty();
		
	}
	
	private class PopupAsyncCallback implements AsyncCallback<Mod2002020> {
		PopupPanel popup;
		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

		@Override
		public void onSuccess(Mod2002020 result) {
			popup.hide();
		}

		@Override
		public void onFailure(Throwable caught) {
			popup.hide();
		}
		
	}

	private void dump( boolean charactersEnabled) {
		ensurePage(P00,new Model200PageCallback(){

			@Override
			public Mod2002020Object getMod200Object() {
				return mod200Object;
			}

			@Override
			public void markAsDirty() {
				Model2002020.this.markAsDirty();
			}
			
		}).dump();
		pageContainer.setWidget(getPage(P00));
		refreshButtonsVisibility();
		((Page00) getPage(P00)).enableCharacters( charactersEnabled );
	}
	
	private void refreshButtonsVisibility() {
		initializeButton.setVisible(!mod200Object.isInitialized());
		importAccountingButton.setVisible(mod200Object.isInitialized());
		saveButton.setVisible(mod200Object.isInitialized());
		removeButton.setVisible(mod200Object.getMod200().getId() != null);
		resetButton.setVisible(mod200Object.getMod200().getId() != null);
		aeatAccountingFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatAccountingFileButton.setEnabled(!isDirty());
		aeatFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatFileButton.setEnabled(!isDirty());
		aeatPrintButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatPrintButton.setEnabled(!isDirty());
	}
	
	private void populatePages() {
		for (PageAbs page : PAGES) {
			if (page != null) page.populate();
		}
	}
	
	private void submitForm(String action) {
		
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
        modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
        domainIdHidden.setValue(String.valueOf(options.getDomain()));
        domainNameHidden.setValue(options.getDomainName());
        userHidden.setValue(options.getUser());
        diskForm.submit();
		
	}
	
	private class WestFocusPanel extends FocusPanel {
		
		public WestFocusPanel(int pag, String label) {
			super();
			
			setStyleName(AON.CSS.aonWidthAll());
			FlowPanel container = new FlowPanel();
			container.setWidth("95%");
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonClickableBlock());
			container.addStyleName(AON.CSS.aonFlexBlock());
			container.addStyleName(AON.CSS.aonBorder());
			container.getElement().getStyle().setMarginTop(5.0, Unit.PX);
			container.getElement().getStyle().setProperty("min-height", "30px");
			
			InlineLabel cardLabel = new InlineLabel( label );
			cardLabel.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
			container.add( cardLabel );
			setWidget(container);
			
			addClickHandler( new  ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int realPag = (pag - 1);
					if (realPag > 0) {
						checkAndShowPage(realPag);	
					} else {
						showPage(realPag);
					}
					
				}
			});
		}
		
		private void showPage(int page) {
			FlowPanel parent = 	(FlowPanel) getParent();
			for (int i = 0 ; i < parent.getWidgetCount(); i++) {
				parent.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthGray());
			}
			
			PageAbs pageAbs = ensurePage(page, new Model200PageCallback(){
													@Override 
													public Mod2002020Object getMod200Object() {
														return mod200Object;
													}
							
													@Override
													public void markAsDirty() {
														Model2002020.this.markAsDirty();						
													}
												});
			if (pageAbs.isAvailable()) {
				pageAbs.dump();
				pageContainer.setWidget(pageAbs);
				addStyleName(AON.CSS.aonBackgroundLigthGray());
			} else {
				AonMessageDialog.warning(AON.MSG.pageNotAvailable());
			}
			
		}
		
		private void checkAndShowPage(int page) {

			if (mod200Object.isInitialized() ) {
				showPage(page);
			} else {
				AonMessageDialog.warning(AON.MSG.mustInitialzeMod200());
			}
		}
	}

	private PageAbs getPage( int i) {
		return PAGES[i];
	}
	
	private PageAbs ensurePage(int i,Model200PageCallback cbk) {
		if (PAGES[i] == null) {
			if (i ==  0) PAGES[i] = new Page00(cbk); 
			if (i ==  1) PAGES[i] = new Page01(cbk); 
			if (i ==  2) PAGES[i] = new Page02(cbk); 
			if (i ==  3) PAGES[i] = new Page03(cbk); 
			if (i ==  4) PAGES[i] = new Page04(cbk); 
			if (i ==  5) PAGES[i] = new Page05(cbk); 
			if (i ==  6) PAGES[i] = new Page06(cbk); 
			if (i ==  7) PAGES[i] = new Page07(cbk); 
			if (i ==  8) PAGES[i] = new Page08(cbk); 
			if (i ==  9) PAGES[i] = new Page09(cbk); 
			if (i == 10) PAGES[i] = new Page10(cbk); 
			if (i == 11) PAGES[i] = new Page11(cbk);
			if (i == 12) PAGES[i] = new Page12(cbk); 
			if (i == 13) PAGES[i] = new Page13(cbk);
			if (i == 14) PAGES[i] = new Page14(cbk); 
			if (i == 15) PAGES[i] = new Page15(cbk); 
			if (i == 16) PAGES[i] = new Page16(cbk);
			if (i == 17) PAGES[i] = new Page17(cbk); 
			if (i == 18) PAGES[i] = new Page18(cbk); 
			if (i == 19) PAGES[i] = new Page19(cbk); 
			if (i == 20) PAGES[i] = new Page20(cbk); 
		}
		return getPage(i);
	}
	
	private Widget getToolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(mod200Object.getMod200().getDocument(),AonStringUtils.SPACE, mod200Object.getMod200().getFullName()));
		
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack() );
		if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> {
			if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
				mod200Callback.getOptions().getExternalCallback().onExit(mod200Object.getMod200());
			} else {
				mod200Callback.onCancel(mod200Object.getMod200());
			}
		});
		toolbarPanel.add(cancelButton);
		
		initializeButton = new AonToolbarButton(AON.MSG.continueAction(), AON.CSS.aonIconSave());
		initializeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			popup.center();
			getPage(P00).populate( );
			mod200Object.initializeMod200(new AsyncCallback<Mod2002020>() {
				@Override
				public void onSuccess(Mod2002020 result) {
					popup.hide();
					dump(false);
					markAsDirty();
				}
				
				@Override
				public void onFailure(Throwable e) {
					popup.hide();
					mod200Callback.showError(e.getMessage());
				}
			});
		});
		toolbarPanel.add(initializeButton);
		
		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(event -> {
			saveButton.setEnabled(false);
			mod200Callback.cleanErrorPanel();
			
			PopupAsyncCallback callback = new PopupAsyncCallback(){
				@Override
				public void onSuccess(Mod2002020 result) {
					super.onSuccess(result);
					saveButton.setEnabled(true);
					setDirty(false);
				}
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					mod200Callback.showError(caught.getMessage());
					saveButton.setEnabled(true);
				}
			};
			popup.center();
			callback.setPopup(popup);
			populatePages();
			try {
				mod200Object.save(callback);
			} catch (IllegalArgumentException e) {
				refreshButtonsVisibility();
				popup.hide();
				AonMessageDialog.error(e.getMessage());
			}
		});
		toolbarPanel.add(saveButton);
		
		removeButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		removeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
					popup.center();
					try {
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								popup.hide();
								//mod200Callback.removed();
								mod200Callback.onRemove(mod200Object.getMod200());
							}
							
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								AonMessageDialog.error("No se han podido borrar los datos.");
							}
						});
					} catch (IllegalArgumentException e) {
						popup.hide();
						AonMessageDialog.error("No se han podido borrar los datos.");
					}
				}
			});
		});
		toolbarPanel.add(removeButton);
		
		resetButton = new AonToolbarButton(AON.MSG.resetAction(), AON.CSS.aonIconRefresh());
		resetButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
					popup.center();
					try {
						// Primero borramos el modelo actual
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								// Si todo ha ido bien, creamos el nuevo modelo
								Model200.getMod2002020Service().createMod2002020(options.getOccam(), 2020
										, new AsyncCallback<Mod2002020>() {

											@Override
											public void onSuccess(Mod2002020 mod200) {
												popup.hide();
												mod200Callback.reset(mod200);
												markAsDirty();
											}

											@Override
											public void onFailure(Throwable caught) {
												popup.hide();
												AonMessageDialog.error("No se ha podido inicializar el modelo.");
												mod200Callback.showError(caught.getMessage());
											}
										});							
							}
							
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								AonMessageDialog.error("No se han podido borrar los datos.");								
								mod200Callback.showError(caught.getMessage());
							}
						});
					} catch (IllegalArgumentException e) {
						popup.hide();
						AonMessageDialog.error("No se han podido borrar los datos.");
						mod200Callback.showError(e.getMessage());
					}
				}
			});
		});
		toolbarPanel.add(resetButton);
		
		// Importar XML con información contable (formato AEAT)  
			
		importAccountingButton = new AonToolbarButton(AON.MSG.importAccounting(), AON.CSS.aonIconUpload());
		importAccountingButton.addClickHandler(event -> {
			importAccountingButton.setEnabled(false);
			mod200Callback.cleanErrorPanel();			
			Upload upload = new Upload() {
				
				@Override
				protected void onUpload(String data, String type) {
					mod200Object.fillMod2002020AccountingData(options.getDomainName(), options.getDomain(), options.getUser(), data, new AsyncCallback<Mod2002020>() {
						@Override public void onSuccess(Mod2002020 result) {	
							markAsDirty();
						}
						@Override public void onFailure(Throwable caught) {
							mod200Callback.showError(caught.getMessage());
						}
					});				
				}
			};
			upload.upload();
			importAccountingButton.setEnabled(true);
		});
		toolbarPanel.add(importAccountingButton);
		
		// Exportar XML con información contable (formato AEAT)
		
		aeatAccountingFileButton = new AonToolbarButton(AON.MSG.aeatAccountingFile(), AON.CSS.aonIconDownload());
		aeatAccountingFileButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmAccountingFileMod200(),
				new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020AccountingFile");
					}
				}
			);
		});
		toolbarPanel.add(aeatAccountingFileButton);
		
		// Fichero AEAT para presentación		

		aeatFileButton = new AonToolbarButton(AON.MSG.generateFile(), AON.CSS.aonIconAeat());
		aeatFileButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmAeatFileMod200(),
				new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020File");
					}
				}
			);
		});
		toolbarPanel.add(aeatFileButton);

		// Borrador AEAT: Invocación al Servicio de Validación y Prueba

		aeatPrintButton = new AonToolbarButton(AON.MSG.validatePrintViaAeat(), AON.CSS.aonIconAeatBw());
		aeatPrintButton.addClickHandler(event -> {
				mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmAeatPrintMod200(),
				new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020Print");
					}
				}
			);
		});
		toolbarPanel.add(aeatPrintButton);
		
		// Marca "Cambios sin guardar"
		
		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		styleDirtyLabel();
		marksPanels.add(dirtyLabel);

		toolbarPanel.getMessagePanel().add(marksPanels);		

		toolbarPanel.add(diskForm);		
		return toolbarPanel;
	}
	
	private Widget getLinksPanel() {
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel linkContainer = new FlowPanel();
		linkContainer.setStyleName(AON.CSS.aonPaddingLeft());
		 
		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification() + ", Estados de Cuentas, Personal Asalariado, Caracteres de la declaraci\u00F3n"	 ));
		linkContainer.add(new WestFocusPanel( 2,AON.MSG.administratorPage()	 ));
		linkContainer.add(new WestFocusPanel( 3,AON.MSG.participations2019() ));
		linkContainer.add(new WestFocusPanel( 4,AON.MSG.balanceActivo()		 ));
		linkContainer.add(new WestFocusPanel( 5,AON.MSG.balancePasivoAbbrv()	 ));
		linkContainer.add(new WestFocusPanel( 6,AON.MSG.pyg() 				 ));
		linkContainer.add(new WestFocusPanel( 7,AON.MSG.patrimonioIngresosAbbrv() ));
		linkContainer.add(new WestFocusPanel( 8,AON.MSG.patrimonioCambios()	 ));
		linkContainer.add(new WestFocusPanel( 9,AON.MSG.liquidacionI() + ": Resultado PyG, Cifra de negocios, Correcciones"));
		linkContainer.add(new WestFocusPanel(10,AON.MSG.liquidacionII() + ": Base imponible, Cuota \u00EDntegra"));
		linkContainer.add(new WestFocusPanel(11,AON.MSG.liquidacionIII() + ": Bonificaciones, Deducciones por doble imposici\u00F3n"));
		linkContainer.add(new WestFocusPanel(12,AON.MSG.liquidacionIV() + ": Otras deducciones"));
		linkContainer.add(new WestFocusPanel(13,AON.MSG.liquidacionV() + ": Cuota del ejercicio, Pagos fraccionados, L\u00EDquido a ingresar o devolver"));
		linkContainer.add(new WestFocusPanel(14,AON.MSG.combinedTaxationAbbrv()));
		linkContainer.add(new WestFocusPanel(15,"Aplicaci\u00F3n de resultados / Documentaci\u00F3n previa"));
		linkContainer.add(new WestFocusPanel(16,AON.MSG.deducibleLimitationAbbrv()));
		linkContainer.add(new WestFocusPanel(17,AON.MSG.page17()		 	 ));
		linkContainer.add(new WestFocusPanel(18,AON.MSG.page18()		 	 ));
		linkContainer.add(new WestFocusPanel(19,"U.T.E.S."));
		linkContainer.add(new WestFocusPanel(20,AON.MSG.bussinessAmount()	 ));
		linkContainer.add(new WestFocusPanel(21,AON.MSG.idDocument()		 ));		
		// PAGINA AGENCIA TRIBUTARIA CON INFO, FICHERO Y BORRADOR - POR AHORA SE PONEN 
		// LOS BOTONES COMO ESTABAN ANTES, PUES EN LOS OTROS MODELOS SE ESTA LLAMANDO A UNA CLASE
		// DE AON-GWT-FISCAL LA CUAL LLAMA A VARIAS CLASES DEL MISMO PROYECTO, ADEMAS SE REQUIERE
		// QUE YA ESTE DESAROLLADO LO DEL ESTADO DEL MODELO (FINALIZADO, ENVIADO, ETC..) QUE SE HARA 
		// EN EL MODELO 200 DE 2021
		
		scrollPanel.add(linkContainer);
		return scrollPanel;
		
	}
	
// LO DEL ESTADO AUN NO ESTÁ EN EL MODELO 200, ASI QUE POR AHORA NO SE PONEN LOS BOTONES QUE CAMBIAN EL ESTADO	
//	private AonToolbar getDeclarationToolbarPanel() {
//		AonToolbar decToolbar = new AonToolbar();
//		
////		AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
////		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
////		markAsFinishedButton.setVisible(!getModel().isNew() &&
////				(getModel().getStatus() == FiscalStatus.PENDING 
////				|| getModel().getStatus() == FiscalStatus.MISSING));
////		markAsFinishedButton.addClickHandler(event -> {
////			markAsFinishedButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsFinishedButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsFinishedButton);
//
////		AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
////		markAsSentButton.setText(markAsSentButton.getTitle());
////		markAsSentButton.setVisible(!getModel().isNew() && (getModel().isFinished()));		
////		markAsSentButton.addClickHandler(event -> {
////			markAsSentButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.SENT, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsSentButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsSentButton);
//		
////		AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
////		markAsPendingButton.setText(markAsPendingButton.getTitle());
////		markAsPendingButton.setVisible(!getModel().isNew() 
////				&& (getModel().isFinished() 
////				|| getModel().getStatus() == FiscalStatus.BATCHED 
////				|| getModel().isSent()));
////		markAsPendingButton.addClickHandler(event -> {
////			markAsPendingButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsPendingButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsPendingButton);
//
//		FlowPanel marksPanels = new FlowPanel();
//		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
//
//		// LO DE COMPLEMENTARIA SE PUEDE VER EN LA PAGINA DE IDENTIFICACION ASI QUE TAMPOCO SE PONE
//		// ADEMAS SE PERMITE PONER O QUITAR LO DE LA COMPLEMENTARIA A DECISION DEL USUARIO
////		InlineLabel replacedLabel = new InlineLabel();		
////		if (getModel().isComplementary()) {
////			replacedLabel.setText(AON.MSG.complementary());
////			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
////			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
////			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
////		}
////		marksPanels.add(replacedLabel);
//
//		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
//		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
//		dirtyLabel.setTitle("Cambios sin guardar");
//		styleDirtyLabel();
//		marksPanels.add(dirtyLabel);
//
//		decToolbar.getMessagePanel().add(marksPanels);
//		
//		Label statusLabel = new Label();
//		statusLabel.setText(mod200.getStatus() == null?"PENDIENTE":mod200.getStatus().getName());
//		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod200.getStatus() ));
//		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod200.getStatus() ));
//		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
//		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
//		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
//		statusLabel.addStyleName(AON.CSS.aonTextCenter());
//		statusLabel.addStyleName(AON.CSS.aonBorder());
//		statusLabel.addStyleName(AON.CSS.aonNowrap());
//		
//		decToolbar.setTitle(statusLabel);
//		return decToolbar;
//	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
	}
	
	protected void markAsDirty() {
		setDirty(true);
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
		refreshButtonsVisibility();
	}

}
