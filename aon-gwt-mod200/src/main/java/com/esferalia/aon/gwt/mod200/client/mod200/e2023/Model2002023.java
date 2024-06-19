package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.mod200.client.AonFiscalModelHeader;
import com.esferalia.aon.gwt.mod200.client.FiscalModelUtils;
import com.esferalia.aon.gwt.mod200.client.mod200.IModel200PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class Model2002023 extends DockLayoutPanel {
	
	protected interface Model2002023PageCallback extends IModel200PageCallback {	
		public Mod2002023Object getMod200Object();
	}
	
	private PageAbs[] pages = new PageAbs[23];
	private WestFocusPanel westFocusPanelAEAT = null;
	
	protected Mod2002023Object mod200Object;
	private Model200Callback mod200Callback;
	
	AonToolbarButton initializeButton;
	AonToolbarButton saveButton;
	AonToolbarButton removeButton;
	AonToolbarButton resetButton;
	AonToolbarButton aeatButton;  // Boton AEAT muestra la página de la Agencia Tributaria
	AonToolbarButton commentsButton;
	AonToolbarButton auditButton;
	
	AonToast commentsToast = null;

	// Estado 
	AonToolbarButton markAsFinishedButton;
	AonToolbarButton markAsSentButton;
	AonToolbarButton markAsPendingButton;
	Label statusLabel;
	SimpleLayoutPanel pageContainer = new SimpleLayoutPanel();
	
	private Model200ModuleOptions options;
	
	private PopupPanel popup;
	protected final InlineLabel dirtyLabel = new InlineLabel();
	private boolean dirty;
	
	public Model2002023(Model200Callback mod200Callback, Mod2002023 mod200) {
		
		super(Unit.PX);
		
		popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		
		this.mod200Callback = mod200Callback;
		this.options = mod200Callback.getOptions();
		
		AON.ensureInjected();

		mod200Object = new Mod2002023Object(options, mod200);
		
		popup.center();
		
		clear();
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(mod200);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbar(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH); // Estado
		addWest(getLinksPanel(), 300);
		add(pageContainer);		
		
		dumpP00();
		popup.hide();
		if (mod200Object.getMod200().getId() == null) 
			markAsDirty();
		
	}
	
	private class PopupAsyncCallback implements AsyncCallback<Mod2002023> {
		PopupPanel popup;
		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

		@Override
		public void onSuccess(Mod2002023 result) {
			popup.hide();
		}

		@Override
		public void onFailure(Throwable caught) {
			popup.hide();
		}
		
	}

	private void dumpP00() {
		ensurePage(0).dump();		
		pageContainer.setWidget(getPage(0));
		refreshButtonsVisibility();
	}
	
	private void refreshButtonsVisibility() {
		
		boolean isNotNew = !mod200Object.getMod200().isNew();
		FiscalStatus status = mod200Object.getMod200().getStatus();
		
		initializeButton.setVisible(!mod200Object.isInitialized());
		saveButton.setVisible(mod200Object.isInitialized() && !mod200Object.getMod200().isFinished() && !mod200Object.getMod200().isSent());
		removeButton.setVisible(isNotNew && !mod200Object.getMod200().isFinished() && !mod200Object.getMod200().isSent());
		resetButton.setVisible(isNotNew && !mod200Object.getMod200().isFinished() && !mod200Object.getMod200().isSent());
		aeatButton.setVisible(mod200Object.isInitialized());
		commentsButton.setVisible(isNotNew);
		auditButton.setVisible(isNotNew);
		
		// Estado
		markAsFinishedButton.setVisible(isNotNew &&
				(status == FiscalStatus.PENDING || status == FiscalStatus.CUSTOMER_CHECK || status == FiscalStatus.MISSING));
		markAsSentButton.setVisible(isNotNew && (status == FiscalStatus.FINISHED));
		markAsPendingButton.setVisible(isNotNew &&
				(status == FiscalStatus.FINISHED || status == FiscalStatus.BATCHED || status == FiscalStatus.SENT || status == FiscalStatus.CUSTOMER_CHECK || status == FiscalStatus.BLOCKED));
		
		saveButton.setEnabled(true);
		
		// Estado		
		markAsFinishedButton.setEnabled(!isDirty());
		markAsSentButton.setEnabled(!isDirty());
		markAsPendingButton.setEnabled(!isDirty());
		styleStatusLabel();
		
		// Pagina Agencia Tributaria
		if (pages[pages.length-1] != null)
			pages[pages.length-1].setEnabled();
		
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
			
			addClickHandler(event -> {
				int realPag = (pag - 1);
				if (realPag > 0) {
					checkAndShowPage(realPag);	
				} else {
					showPage(realPag);
				}
			});
		}
		
		private void showPage(int page) {
			
			FlowPanel parent = 	(FlowPanel) getParent();
			for (int i = 0 ; i < parent.getWidgetCount(); i++) {
				parent.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthGray());
			}			
			PageAbs pageAbs = ensurePage(page);			
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
		return pages[i];
	}
	
	private PageAbs ensurePage(int i) {
		
		Model2002023PageCallback cbk = new Model2002023PageCallback(){

			@Override
			public Mod2002023Object getMod200Object() {
				return mod200Object;
			}

			@Override
			public void markAsDirty() {
				Model2002023.this.markAsDirty();
			}
			
			@Override
			public String getCheckAction() {
				return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002023CheckAEAT";
			}

			@Override
			public String getCheckDataResponseDataAction() {
				return GWT.getHostPageBaseURL() +"/aon_gwt_mod200/ms/Mod2002023CheckDataResponseData";
			}

			@Override
			public IFiscalModel getModel() {
				return mod200Object.getMod200();
			}

			@Override
			public String getDownloadFileAction() {
				return "/aon_gwt_mod200/ms/Model2002023File";
			}

			@Override
			public void showError(String msg) {
				mod200Callback.showError(msg);
			}

			@Override
			public String getExportAccountingAction() {
				return "/aon_gwt_mod200/ms/Model2002023AccountingFile";
			}

			@Override
			public String getModelInformationURL() {
				return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GE04.shtml";
			}

			@Override
			public Model200ModuleOptions getOptions() {
				return Model2002023.this.options;
			}

			@Override
			public String getValidatePrintAction() {
				return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002023ValidatePrintAEAT";
			}

			@Override
			public String getSendAction() {
				return GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/Mod2002023SendAEAT";
			}

			@Override
			public void sendSuccessfully() {
				// Recargar el modelo una vez que se ha enviado correctamente a la AEAT (Se habrá grabado el estado y el número de justificante)
				Model200.getMod2002023Service().getMod2002023ById(options.getOccam(), mod200Object.getMod200().getId()
						, new AsyncCallback<Mod2002023>() {

							@Override
							public void onSuccess(Mod2002023 mod200) {
								mod200Object.setMod200(mod200);
								refreshButtonsVisibility();
							}

							@Override
							public void onFailure(Throwable caught) {
								// do nothing
							}
						});				
			}

			@Override
			public boolean isDirty() {
				return Model2002023.this.isDirty();
			}

			@Override
			public void importAccountingFile() {
				mod200Callback.cleanErrorPanel();			
				Upload upload = new Upload() {
					
					@Override
					protected void onUpload(String data, String type) {
						mod200Object.fillMod2002023AccountingData(data, new AsyncCallback<Mod2002023>() {
							@Override public void onSuccess(Mod2002023 result) {	
								Model2002023.this.markAsDirty();
							}
							@Override public void onFailure(Throwable caught) {
								mod200Callback.showError(caught.getMessage());
							}
						});				
					}
				};
				upload.upload();
			}

			@Override
			public boolean validateBalance() {
				// Devuelve false si el balance está descuadrado por un importe menor o igual de 10 euros
				double diferencia = AonMathUtils.absRounded(mod200Object.getDoubleValue(Mod2002023Key.BA180) - mod200Object.getDoubleValue(Mod2002023Key.BP252));				
				return (diferencia == 0.0 || diferencia > 10.0);
			}

		};		
		
		if (pages[i] == null) {
			if (i ==  0) pages[i] = new Page00(cbk);
			if (i ==  1) pages[i] = new Page01(cbk);
			if (i ==  2) pages[i] = new Page02(cbk); 
			if (i ==  3) pages[i] = new Page03(cbk); 
			if (i ==  4) pages[i] = new Page04(cbk); 
			if (i ==  5) pages[i] = new Page05(cbk); 
			if (i ==  6) pages[i] = new Page06(cbk); 
			if (i ==  7) pages[i] = new Page07(cbk); 
			if (i ==  8) pages[i] = new Page08(cbk); 
			if (i ==  9) pages[i] = new Page09(cbk); 
			if (i == 10) pages[i] = new Page10(cbk); 
			if (i == 11) pages[i] = new Page11(cbk); 
			if (i == 12) pages[i] = new Page12(cbk);
			if (i == 13) pages[i] = new Page13(cbk); 
			if (i == 14) pages[i] = new Page14(cbk);
			if (i == 15) pages[i] = new Page15(cbk); 
			if (i == 16) pages[i] = new Page16(cbk); 
			if (i == 17) pages[i] = new Page17(cbk);
			if (i == 18) pages[i] = new Page18(cbk); 
			if (i == 19) pages[i] = new Page19(cbk); 
			if (i == 20) pages[i] = new Page20(cbk); 
			if (i == 21) pages[i] = new Page21(cbk);
			if (i == 22) pages[i] = new PageAEAT(cbk);
		}
		return getPage(i);
	}
	
	private Widget getToolbar() {
		
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(mod200Object.getMod200().getDocument(), AonStringUtils.SPACE, mod200Object.getMod200().getFullName()));
		
		// Cancelar (botón para volver atrás)
		
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack() );
		if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> {
			if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
				mod200Callback.getOptions().getExternalCallback().onExit(mod200Object.getMod200());
			} else {
				if (commentsToast != null) {
					commentsToast.hide();					
				}					
				mod200Callback.onCancel(mod200Object.getMod200());
			}
		});
		toolbarPanel.add(cancelButton);
		
		// Continuar (es el botón que sale al añadir el modelo)
		
		initializeButton = new AonToolbarButton(AON.MSG.continueAction(), AON.CSS.aonIconStart());
		initializeButton.setText(AON.MSG.continueAction());		
		initializeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			popup.center();
			mod200Object.initializeMod200(new AsyncCallback<Mod2002023>() {
				@Override
				public void onSuccess(Mod2002023 result) {
					popup.hide();
					dumpP00();
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
		
		// Guardar
		
		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(event -> {
			saveButton.setEnabled(false);
			mod200Callback.cleanErrorPanel();
			
			PopupAsyncCallback callback = new PopupAsyncCallback(){
				@Override
				public void onSuccess(Mod2002023 result) {
					super.onSuccess(result);
					setDirty(false);					
					// Quitar style aonChanged de los DoubleBox
					for (PageAbs page : pages) 						
						if (page != null) 
							page.removeAonChanged();
					refreshButtonsVisibility();
					cleanAEATViewers();
				}
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					mod200Callback.showError(caught.getMessage());
					refreshButtonsVisibility();
				}
			};
			popup.center();
			callback.setPopup(popup);
			try {
				mod200Object.save(callback);
			} catch (IllegalArgumentException e) {
				refreshButtonsVisibility();
				popup.hide();
				AonMessageDialog.error(e.getMessage());
			}
		});
		toolbarPanel.add(saveButton);
		
		// Borrar 
		
		removeButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		removeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
					// DO NOTHING
				}

				@Override
				public void onAccept() {
					popup.center();
					try {
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								popup.hide();
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
		
		// Inicializar modelo
		
		resetButton = new AonToolbarButton(AON.MSG.resetAction(), AON.CSS.aonIconRefresh());
		resetButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
					// DO NOTHING
				}

				@Override
				public void onAccept() {
					popup.center();
					try {
						// Primero borramos el modelo actual
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								// Si el borrado ha ido bien, creamos el nuevo modelo
								Model200.getMod2002023Service().createMod2002023(options.getOccam(), 2023
										, new AsyncCallback<Mod2002023>() {

											@Override
											public void onSuccess(Mod2002023 mod200) {
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
		
		// Botón Agencia Tributaria		

		aeatButton = new AonToolbarButton("Agencia Tributaria", AON.CSS.aonIconAeat());
		aeatButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			westFocusPanelAEAT.checkAndShowPage(pages.length-1);  // La página de la Agencia Tributaria, es la última			
		});
		toolbarPanel.add(aeatButton);
		
		// Comentarios
		
		commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
		commentsButton.addClickHandler( event -> {
			if (commentsToast == null || commentsToast.getParent() == null) {
				commentsToast = new AonToast();
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(mod200Object.getMod200().getAdministration()) );
				commentPanel.addStyleName(AON.CSS.aonHeightAll());
				commentPanel.addStyleName(AON.CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(event1 -> {
					mod200Object.getMod200().setComments(event1.getValue());
					styleCommentsButton();
					Model200.MOD200_SERVICE.saveComments(mod200Callback.getOptions().getOccam(), mod200Object.getMod200(), new AsyncCallback<Mod200>() {
						@Override
						public void onSuccess(Mod200 result) {						
							commentsToast.hide();
						}
	
						@Override
						public void onFailure(Throwable caught) {						
							commentsToast.hide();
							mod200Callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
				});
				comment.setText(mod200Object.getMod200().getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				commentsToast.show(AON.MSG.comments(), commentPanel);
			}
		});
		toolbarPanel.add(commentsButton);
		styleCommentsButton();
		
		// Auditoria
		auditButton = new AonToolbarButton(AON.MSG.audit(), AON.CSS.aonIconAudit());
		auditButton.addClickHandler( event -> audit());
		toolbarPanel.add(auditButton);
		
		// Marca "Cambios sin guardar"
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		styleDirtyLabel();
		toolbarPanel.getMessagePanel().add(dirtyLabel);		

		return toolbarPanel;
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod200Object.getMod200().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(mod200Object.getMod200().getComments());
	}
	
	private Widget getLinksPanel() {
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel linkContainer = new FlowPanel();
		linkContainer.setStyleName(AON.CSS.aonPaddingLeft());
		linkContainer.addStyleName(AON.CSS.aonPaddingBottom());
		 
		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification() + ", Estados de Cuentas, Caracteres de la Declaraci\u00F3n"));
		linkContainer.add(new WestFocusPanel( 2,"Cifra de Negocios, Personal Asalariado, Secretario, Grupo Fiscal o Mercantil"));
		linkContainer.add(new WestFocusPanel( 3,"Representantes, Administradores, Titular Real"));
		linkContainer.add(new WestFocusPanel( 4,"Participaciones, Entidades menores, Informaci\u00F3n detalle EP, Socios SICAV"));
		linkContainer.add(new WestFocusPanel( 5,AON.MSG.balanceActivo()));
		linkContainer.add(new WestFocusPanel( 6,AON.MSG.balancePasivo()));
		linkContainer.add(new WestFocusPanel( 7,AON.MSG.pyg()));
		linkContainer.add(new WestFocusPanel( 8,AON.MSG.patrimonioIngresos()));
		linkContainer.add(new WestFocusPanel( 9,AON.MSG.patrimonioCambios()));
		linkContainer.add(new WestFocusPanel(10,AON.MSG.liquidacionI() + ": Resultado PyG, Correcciones"));
		linkContainer.add(new WestFocusPanel(11,AON.MSG.liquidacionII() + ": Base imponible, Cuota \u00EDntegra"));
		linkContainer.add(new WestFocusPanel(12,AON.MSG.liquidacionIII() + ": Bonificaciones, Deducciones por doble imposici\u00F3n"));
		linkContainer.add(new WestFocusPanel(13,AON.MSG.liquidacionIV() + ": Otras deducciones, Cuota L\u00EDquida, Producciones cinematogr\u00E1ficas"));
		linkContainer.add(new WestFocusPanel(14,AON.MSG.liquidacionV() + ": Cuota del ejercicio, Pagos fraccionados, L\u00EDquido a ingresar o devolver"));
		linkContainer.add(new WestFocusPanel(15,AON.MSG.combinedTaxationAbbrv()));
		linkContainer.add(new WestFocusPanel(16,"Aplicaci\u00F3n resultados, Documentaci\u00F3n previa"));
		linkContainer.add(new WestFocusPanel(17,AON.MSG.deducibleLimitation()));
		linkContainer.add(new WestFocusPanel(18,"R\u00E9gimen especial Canarias, R\u00E9gimen especial Illes Balears"));
		linkContainer.add(new WestFocusPanel(19,"Dotaciones por deterioro, Conversi\u00F3n de activos"));
		linkContainer.add(new WestFocusPanel(20,"Agrupaciones de inter\u00E9s econ\u00F3mico y UTES (r\u00E9gimen especial), Part\u00EDcipes de AIE y UTES"));
		linkContainer.add(new WestFocusPanel(21,"Comunicaci\u00F3n importe neto cifra de negocios: Grupos de sociedades, No residentes"));
		linkContainer.add(new WestFocusPanel(22,AON.MSG.idDocument()));		

		westFocusPanelAEAT = new WestFocusPanel(23,"Agencia Tributaria");
		linkContainer.add(westFocusPanelAEAT);
		
		scrollPanel.add(linkContainer);
		return scrollPanel;
		
	}
	
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
	
	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(mod200Object.getMod200());
	}
	
	private AonToolbar getDeclarationToolbarPanel() {
		AonToolbar decToolbar = new AonToolbar();
				
		// Botón "Finalizar"
		
		markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler(event -> {
			markAsFinishedButton.setEnabled(false);
			changeStatus(FiscalStatus.FINISHED);
		});
		decToolbar.add(markAsFinishedButton);

		// Botón "Marcar como Presentado"
		
		markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler(event -> {
			markAsSentButton.setEnabled(false);
			changeStatus(FiscalStatus.SENT);
		});
		decToolbar.add(markAsSentButton);
		
		// Botón "Reabrir"
		
		markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler(event -> {
			markAsPendingButton.setEnabled(false);
			changeStatus(FiscalStatus.PENDING);
		});
		decToolbar.add(markAsPendingButton);
		
		// Label "Estado"		
		statusLabel = new Label();
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	private void changeStatus(FiscalStatus newStatus) {
		try {
			mod200Object.getMod200().setStatus(newStatus);
			saveButton.click();			
		} finally {
			refreshButtonsVisibility();
			for (PageAbs page : pages) 						
				if (page != null) {
					page.setEnabled();
				}			
		}		
	}
	
	protected void styleStatusLabel() {
		statusLabel.setText(mod200Object.getMod200().getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod200Object.getMod200().getStatus()));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod200Object.getMod200().getStatus()));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	
	protected void cleanAEATViewers() {
		// Pagina Agencia Tributaria
		if (pages[pages.length-1] != null)
			((PageAEAT)pages[pages.length-1]).cleanViewers();
	}

}
