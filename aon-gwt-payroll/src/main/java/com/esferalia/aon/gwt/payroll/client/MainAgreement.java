package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAgreementsToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.client.Agreements.Toolbar;
import com.esferalia.aon.gwt.payroll.client.AgreementsCleanDialog.AgreementCleanType;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

//------------------------------------------- EditionListener

interface EditionListener2 {
	
	void onAgreementCopy(Agreement agreement);
	
	void onAgreementPaste(Agreement agreement);
	
	void onAgreementDelete(Agreement agreement);
	
	void onViewAgreements(Agreement agreement, Boolean allAgreements);

}

public class MainAgreement extends MainEntryPoint implements Listener,
	EditionListener2, Agreements.Toolbar, AonAgreementsToolbar.Listener {
	
	// ------------------------------------------- SettingsContextMenu
	
	class EmptyTrashCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementsCleanDialog dialog = new AgreementsCleanDialog(AgreementCleanType.DELETED) {
				
				@Override
				public void onAccept() {
					mainTrashAgreement.getAgreements().getAgreements(s -> {});
				}
			};
			
			dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
			dialog.addStyleName(AON.CSS.aonDialogZIndex());
		}
	}
	
	class UnusedCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementsCleanDialog dialog = new AgreementsCleanDialog(AgreementCleanType.UNUSED) {
				
				@Override
				public void onAccept() {
					mainTrashAgreement.getAgreements().getAgreements(s -> {});
				}
			};
			
			dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
			dialog.addStyleName(AON.CSS.aonDialogZIndex());
		}
	}
	
	class SettingsContextMenu extends ContextMenu {
		
		private MenuItem emptyTrash;
		private MenuItem unused;
		
		public SettingsContextMenu() {
			
			emptyTrash = addItem("Ver papelera convenios", new EmptyTrashCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			emptyTrash.ensureDebugId("emptyTrash");
			
			unused = addItem("Ver convenios en desuso", new UnusedCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			unused.ensureDebugId("peculiarities");
			
		}
		
	}
	
	// ------------------------------------------- ScheduledCommand
	
	class NewAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			MainAgreement.this.agreements.addNewItemTree();
		}
	}
	
	class CopyAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			for(EditionListener2 listener : editionsListener)
				listener.onAgreementCopy(agreement);
		}
	}
	
	class PasteAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			for(EditionListener2 listener : editionsListener)
				listener.onAgreementPaste(agreement);
		}
	}
	
	class DeleteAgreementCommand implements ScheduledCommand  {
		@Override
		public void execute() {
			for(EditionListener2 listener : editionsListener)
				listener.onAgreementDelete(agreement);
		}
	}
	
	class MoveAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Subir convenio", new HTML("\u00BFDesea subir el convenio seleccionado al dominio padre\u003F"));
			dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
			dialog.addStyleName(AON.CSS.aonDialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					moveAgreement2Parent(MainAgreement.this.agreement);
				}
			});
		}
		
		private void moveAgreement2Parent(Agreement agreement) {
			MainAgreement.this.agreements.getAgreementsTree().getEnterpriseService()
				.moveAgreement2Parent(agreement, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Ooopsss ...." + caught.getMessage()) ;
				}

				@Override
				public void onSuccess(Void result) {
					Window.alert("Convenio movido correctamente");
					MainAgreement.this.agreements.reloadAgreements(finish -> {
						if(MainAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
							showAgreementMessage();
						else
							showAgreementContainer();
					});
				}
			});
		}
	}
	
	class MoveDownAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Descargar convenio", new HTML("\u00BFDesea descargar el convenio seleccionado al dominio en el que se encuentra\u003F La descarga incluye la actualizaci\u00f3n de los contratos (de este dominio) que estaban asociados al convenio antiguo."));
			dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
			dialog.addStyleName(AON.CSS.aonDialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					moveAgreement2Child(MainAgreement.this.agreement);
				}
			});
		}
		
		private void moveAgreement2Child(Agreement agreement) {
			AonMessagePanel.showLoading(messagePanel, "Descargando convenio al dominio...");
			MainAgreement.this.agreements.getAgreementsTree().getEnterpriseService()
				.moveAgreement2Child(agreement.getId(), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Opss... " + caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					AonMessagePanel.showSuccess(messagePanel, "Convenio descargado correctamente");
					MainAgreement.this.agreements.reloadAgreements(finish -> {
						if(MainAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
							showAgreementMessage();
						else
							showAgreementContainer();
					});
				}
			});
		}
	}
	
	// ------------------------------------------- ContextMenu
	
	class AgreementContextMenu extends ContextMenu {
		
		private Agreement agreementCopy = null;		
		private MenuItem newItem = null;
		private MenuItem copyItem = null;
		private MenuItem pasteItem = null;
		private MenuItem moveItem = null;
		private MenuItem moveDownItem = null;
		private MenuItem deleteItem = null;
		
		public AgreementContextMenu() {
			
			newItem = addItem("Nuevo", new NewAgreementCommand(), 
					AON.CSS.aonIconAdd(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			newItem.ensureDebugId("newItem");
			addSeparator();
			
			copyItem = addItem("Copiar", new CopyAgreementCommand(), 
					AON.CSS.aonIconCopy(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			copyItem.ensureDebugId("copyItem");
			
			pasteItem = addItem("Pegar", new PasteAgreementCommand(), 
					AON.CSS.aonIconPaste(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			pasteItem.ensureDebugId("pasteItem");
			
			deleteItem = addItem("Eliminar", new DeleteAgreementCommand(), 
					AON.CSS.aonIconDelete(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());			
			deleteItem.ensureDebugId("deleteItem");
			
			moveItem = addItem("Mover a..", new MoveAgreementCommand(), 
					AON.CSS.aonIconMoveTo(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			moveItem.ensureDebugId("moveItem");
			moveItem.setTitle("Mover convenio al dominio padre");
			moveItem.setVisible(false);
			
			moveDownItem = addItem("Descargar a..", new MoveDownAgreementCommand(), 
					AON.CSS.aonIconMoveDown(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			moveDownItem.ensureDebugId("moveItem");
			moveDownItem.setTitle("Descargar convenio al dominio hijo (incluye contratos asociados)");
			moveDownItem.setVisible(false);

		}
		
		public void setAgreementCopy(Agreement agreement) {
			this.agreementCopy = agreement;
		}
		
		public Agreement getAgreementCopy() {
			return this.agreementCopy;
		}
		
		public void setVisibleCopyItem(Integer id) {
			this.copyItem.setEnabled(true);
			this.copyItem.setTitle("");

			if ( id < 0) {
				this.copyItem.setEnabled(false);
				this.copyItem.setTitle("No es posible copiar "
						+ "un convenio no guardado.");
			}
		}
		
		public void setVisibleMoveItem(boolean visible) {
			this.moveItem.setVisible(visible);
		}
		
		public void setVisibleMoveDownItem(boolean visible) {
			this.moveDownItem.setVisible(visible);
		}

		public void setVisibleDeleteItem(boolean visible) {
			this.deleteItem.setVisible(visible);
		}
	}
	
	// ------------------------------------------- UiFields
	
	private DeckPanel deckPanel;
	private AonCustomDockLayout splitLayoutPanel;
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private AgreementPreview agreementPreview;
	private HTMLPanel agreementMessage;
	
	private AonAgreementsToolbar toolbar;

	private Agreements agreements;
	private MainTrashAgreement mainTrashAgreement;
	
	// ------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private static final String AGREEMENT = "c-agreement";
	
	private Integer parentDomain;
	private Integer domain;
	private Storage storage;
	private Agreement agreement;
	private AgreementContextMenu contextMenu;
	
	private List<EditionListener2> editionsListener;
	
	private List<Listener> listeners;
	private List<Toolbar> toolbars;
	
	private DomainUserRoles userRoles;
	
	private SettingsContextMenu settingsContextMenu;
	
	private AgreementInfo agreementSelected;
	
	// ------------------------------------------- ModuleLoad
	
	@Override
	public void onModuleLoad() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		
		deckPanel = new DeckPanel();
		
		splitLayoutPanel = new AonCustomDockLayout("Convenios") {
			@Override
			protected void onClearFilter() {}
		};
		splitLayoutPanel.hideSearchWidget();
		
		toolbar = new AonAgreementsToolbar(splitLayoutPanel);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		messagePanel = new HTMLPanel("");
		
		container.add(messagePanel);
		AonMessagePanel.hideMessage(messagePanel);
		
		mainTrashAgreement = new MainTrashAgreement() {
			
			@Override
			public void onBackButtonClick() {
				showAgreements();
				mainTrashAgreement.hasTrashAgreements(hasTrashAgreements -> MainAgreement.this.toolbar.setTrashAgreementWarn(hasTrashAgreements));
				selectAgreementFirstItem();
			}
			
		};
		
		agreementPreview = new AgreementPreview() {
			
			@Override
			protected void reloadAgreement() {
				getAgreement(agreementSelected.getId(), agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPreview.resetSelectedDate();
					agreementPreview.setAgreementPreview(agreementSelected);
				});
			}
			
			@Override
			public void onSaved(Consumer<Void> finish) {
				saveAgreement(agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPreview.setAgreementPreview(agreementSelected);
					agreementPreview.showSuccess("Convenio", "Convenio guardado correctamente");
					finish.accept(null);
				}, error -> agreementPreview.showError("Error guardando", error.getMessage()));
			}
		};
		container.add(agreementPreview);
		
		agreementMessage = new HTMLPanel("");
		agreementMessage.addStyleName(AON.CSS.aonItemFlex());
		agreementMessage.addStyleName(AON.CSS.aonDisplayFlexCenter());
		
		Label empty = new Label("SELECCIONE UN CONVENIO PARA SER VISUALIZADO");
		empty.getElement().getStyle().setProperty("font-weight", "bold");
		empty.getElement().getStyle().setProperty("margin-top", "1rem");
		agreementMessage.add(empty);
		
		container.add(agreementMessage);
		
		agreements = new Agreements();
		agreements.addStyleName("aon-MainAgreement-Agreements");
		
		// LocalStorage getItems
		this.storage = Storage.getLocalStorageIfSupported();

		// Add the outerpanel to the RootLayoutPanel, so that it will be displayed.
		
		deckPanel.setAnimationEnabled(true);
		
		this.settingsContextMenu = new SettingsContextMenu();
		this.editionsListener = new LinkedList<>();
		this.agreement = null;
		this.parentDomain = null;
		this.domain = null;
		this.contextMenu = new AgreementContextMenu();
		this.agreements.addToolbar(this);
		this.agreements.addListener(this);
		
		this.toolbars = new LinkedList<>();
		this.listeners = new LinkedList<>();
		
		this.userRoles = new DomainUserRoles();
		
		addToolbar(this);
		toolbar.addListener(this);
		
		addEditionOptions(this);
		
		showAgreementMessage();
		
		if(storage.getItem(AGREEMENT) != null) {
			Integer id = Integer.parseInt(storage.getItem(AGREEMENT));
			Agreement agreementAux = new Agreement();
			agreementAux.setId(id);
			contextMenu.setAgreementCopy(agreementAux);
			contextMenu.setVisible(true);
		}	
		
		agreements.getAgreementsTree().getEnterpriseService().getParentDomain(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				// Not use here
			}

			@Override
			public void onSuccess(Integer parentDomain) {
				MainAgreement.this.parentDomain = parentDomain;	
				agreements.getAgreementsTree().getEnterpriseService().getDomain(new AsyncCallback<Integer>() {
					
					@Override
					public void onFailure(Throwable caught) {
						// Not use here
					}
					
					@Override
					public void onSuccess(Integer domain) {
						MainAgreement.this.domain = domain;	

						agreements.getAgreementsTree().getEnterpriseService().getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
							
							@Override
							public void onSuccess(DomainUserRoles result) {
								userRoles = result;
								toolbar.setVisibleImportButton(true);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								// Not use here
							}
							
						});
					}
				});
			}
		});
		
		mainTrashAgreement.hasTrashAgreements(hasTrashAgreements -> toolbar.setTrashAgreementWarn(hasTrashAgreements));
		
		splitLayoutPanel.add(container);
		splitLayoutPanel.addWest(agreements, 335);
		
		deckPanel.add(splitLayoutPanel);
		deckPanel.add(mainTrashAgreement);
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(deckPanel);
		
		showAgreements();
	}

	private void showAgreements() {
		deckPanel.showWidget(0);
	}
	
	private void showTrashAgreements() {
		deckPanel.showWidget(1);
	}
	
	public void selectAgreementFirstItem() {
		agreements.getAgreements(s -> getAgreementsTree().getTree().setSelectedItem(getAgreementsTree().getTree().getItem(0), true));
	}
	
	// ------------------------------------------- Agreements.Listener

	@Override
	public void onAgreementSelected(Agreement agreement) {
		this.agreement = agreement;		
		this.contextMenu.setVisibleCopyItem(agreement.getId());		
		
		if(null != agreement.getDomain()) {
			this.contextMenu.setVisibleMoveItem( (parentDomain != null) && parentDomain.intValue() != agreement.getDomain().intValue() && agreement.getDomain().intValue() != 0);
			this.contextMenu.setVisibleMoveDownItem(agreement.getDomain().intValue() != 0 && domain != agreement.getDomain().intValue());
			this.contextMenu.setVisibleDeleteItem(0 != agreement.getDomain().intValue() && (parentDomain == null || ((parentDomain != null) && parentDomain.intValue() != agreement.getDomain().intValue())));
			this.agreements.setVisibleDraftButton(0 != agreement.getDomain().intValue() && (parentDomain == null || ((parentDomain != null) && parentDomain.intValue() != agreement.getDomain().intValue())));
			
			// TODO: read only
			this.agreementPreview.setReadOnly(0 == agreement.getDomain().intValue() || (parentDomain != null && parentDomain.intValue() == agreement.getDomain().intValue()));
		}
		
//		agreementPreview.showLoading("Actualizando versi\u00f3n convenio...");
//		fixAgreement(
//				agreement.getId(), 
//				s -> {
//					agreementPreview.showLoading("Cargando convenio...");
//					getAgreement(agreement.getId(), agreeementInfo -> {
//						agreementSelected = agreeementInfo;
//						showAgreementContainer();
//						agreementPreview.resetSelectedDate();
//						agreementPreview.setAgreementPreview(agreeementInfo);
//					});
//					
//					checkAgreementIntegrity(agreement.getId(), 
//						integrity -> {
//							if(integrity.hasMessages())
//								new AgreementIntegrityDialog(agreement.getId(), null);
//						}
//					);
//				}, 
//				err -> {
//					agreementPreview.showLoading("Cargando convenio...");
//					getAgreement(agreement.getId(), agreeementInfo -> {
//						agreementSelected = agreeementInfo;
//						showAgreementContainer();
//						agreementPreview.resetSelectedDate();
//						agreementPreview.setAgreementPreview(agreeementInfo);
//					});
//					new AgreementIntegrityDialog(agreement.getId(), err.getMessage());
//				});
		
		agreementPreview.showLoading("Cargando convenio...");
		getAgreement(agreement.getId(), agreeementInfo -> {
			agreementSelected = agreeementInfo;
			showAgreementContainer();
			agreementPreview.resetSelectedDate();
			agreementPreview.setAgreementPreview(agreeementInfo);
		});
		
	}
	
	public void addEditionOptions(EditionListener2 listener) {
		editionsListener.add(listener);
	}

	public void removeEditionListener(EditionListener2 listener) {
		editionsListener.remove(listener);
	}

	@Override
	public void onNewAgreement(Agreement agreement) {
		// Not use here
	}

	@Override
	public void onMoveToTrash(Agreement agreement) {
		for(EditionListener2 listener : editionsListener)
			listener.onAgreementDelete(agreement);
	}

	@Override
	public void onCopyAgreement(Agreement agreement) {
		for(EditionListener2 listener : editionsListener)
			listener.onAgreementPaste(agreement);
	}

	@Override
	public void onPasteAgreement(Agreement agreement) {
		for(EditionListener2 listener : editionsListener)
			listener.onAgreementCopy(agreement);
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement, ContextMenuEvent event) {
		// Not use here
	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		for(EditionListener2 listener : editionsListener)
			listener.onAgreementCopy(agreement);
	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		for(EditionListener2 listener : editionsListener) 
			listener.onAgreementPaste(agreement);
	}

	@Override
	public void onAgreementSupr(Agreement agreement) {
		for(EditionListener2 listener : editionsListener)
			listener.onAgreementDelete(agreement);
	}

	@Override
	public void onAgreementCopy(Agreement agreement) {	
		
		contextMenu.setAgreementCopy(agreement);	
		
		storage.setItem(AGREEMENT, agreement.getId().toString());
	}

	@Override
	public void onAgreementPaste(Agreement agreementCopy) {
		
		agreementCopy = contextMenu.getAgreementCopy();
		
		if(agreement != null) {
			agreements.getAgreementsTree().getEnterpriseService().copyAgreement(agreementCopy, 
					new AsyncCallback<Agreement>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Oopss. Estamos corrigiendolo.");
				}

				@Override
				public void onSuccess(Agreement result) {
					agreementPreview.showSuccess("", "Convenio duplicado correctamente");
					agreements.reloadAgreements(finish -> {
						if(MainAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
							showAgreementMessage();
						else
							showAgreementContainer();
					});			
				}
			});
		}
	}

	@Override
	public void onAgreementDelete(Agreement agreement) {
		if(agreement.getHasContract()) {
			agreements.getAgreementsTree().getEnterpriseService().getDeleteAgreementMessage(
					agreement, new AsyncCallback<String>() {
	
						@Override
						public void onFailure(Throwable caught) {
							// Not use here
						}
	
						@Override
						public void onSuccess(String message) {
							AonDialog dialog = new AonDialog("BORRADO", new HTML(message));
							dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
							dialog.addStyleName(AON.CSS.aonDialogZIndex());
							dialog.confirm(new AonAcceptDialogCallback() {
								
								@Override
								public void onCancel() {
									// Not use here
								}
								
								@Override
								public void onAccept() {
									agreementPreview.showLoading("Borrando convenio ...");
									agreements.getAgreementsTree().getEnterpriseService().updateAgreementId(
											agreement, new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											agreementPreview.showError("Borrado", "No ha sido posible enviar el Convenio a la papelera.");
										}

										@Override
										public void onSuccess(Void result) {
											agreementPreview.showSuccess("Borrado", "Convenio enviado a la papelera");
											MainAgreement.this.agreements.resetTypeView();
											MainAgreement.this.agreements.reloadAgreements(finish -> {
												if(MainAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
													showAgreementMessage();
												else
													showAgreementContainer();
											});
											mainTrashAgreement.hasTrashAgreements(hasTrashAgreements -> toolbar.setTrashAgreementWarn(hasTrashAgreements));
										}
									});
								}
							});
						}});	
		} else {
			String message = "Este convenio ser\u00E1 eliminado de forma permanente.<br>\u00BFDesea eliminar el convenio de <b>" + agreement.getDescription() + "</b>?";
			AonDialog dialog = new AonDialog("BORRADO", new HTML(message));
			dialog.setGlassStyleName(AON.CSS.aonDialogGlass());
			dialog.addStyleName(AON.CSS.aonDialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					agreements.getAgreementsTree().getEnterpriseService().deleteAgreement(
							agreement, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							agreementPreview.showError("Borrado", "No ha sido posible eliminar el Convenio.");
						}

						@Override
						public void onSuccess(Void result) {
							agreementPreview.showSuccess("Borrado", "Convenio borrado correctamente");
							MainAgreement.this.agreements.setViewAgreements(false);
							MainAgreement.this.agreements.resetTypeView();
							MainAgreement.this.agreements.reloadAgreements(finish -> {
								if(MainAgreement.this.agreements.getAgreementsTree().getTree().getItemCount() == 0)
									showAgreementMessage();
								else
									showAgreementContainer();
							});
						}
					});
				}
			});
		}
		
	}
	
	@Override
	public void onCollapseMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 0);
		//splitLayoutPanel.animate(500);
	}

	@Override
	public void onShowMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 335);
		//splitLayoutPanel.animate(500);
	}
	
	// ------------------------------------------- Toolbar methods
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
	}

	@Override
	public void onCollapseMenuButtonClick(ClickEvent event) {
		splitLayoutPanel.setWidgetSize(agreements, 0);
		//splitLayoutPanel.animate(500);
		Timer timer = new Timer() {
			@Override
			public void run() {
				agreementPreview.setTablesWidthCollapseMenu();
				agreementPreview.setIsOpenCollapse(false);
			}
		};
		timer.schedule(500);
	}

	@Override
	public void onShowMenuButtonClick(ClickEvent event) {
		splitLayoutPanel.setWidgetSize(agreements, 335);
		//splitLayoutPanel.animate(500);
		Timer timer = new Timer() {
			@Override
			public void run() {
				agreementPreview.setTablesWidth();
				agreementPreview.setIsOpenCollapse(true);
			}
		};
		timer.schedule(500);
	}

	@Override
	public void onTrashListButtonClick(ClickEvent event) {
		showTrashAgreements();
		mainTrashAgreement.selectFirstItem();
	}

	@Override
	public void onImportButtonClick(ClickEvent event) {
		ServiAgreementDialog serviAgreementDialog = new ServiAgreementDialog() {

			@Override
			protected void onAccept(String serviAgreementCode, List<Integer> selectedDates) {
				agreementPreview.showLoading("Descargando convenio desde ServiConvenios...");
				
				getAgreementsTree().getEnterpriseService().getServiAgreement(serviAgreementCode, selectedDates,
						new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						agreementPreview.showError("Error importaci\u00f3n", caught.getMessage());
					}

					@Override
					public void onSuccess(Integer importedAgreementId) {
						agreementPreview.showLoading("Cargando visualizaci\u00F3n convenio...");
						agreements.getAgreementsAndSelectImported(
								importedAgreementId, 
								s -> AonMessagePanel.hideMessage(messagePanel));
					}
				});
			}
			
		};
		
		serviAgreementDialog.setGlassStyleName(AON.CSS.aonDialogGlass());
		serviAgreementDialog.addStyleName(AON.CSS.aonDialogZIndex());
		serviAgreementDialog.setMessageVisible(!userRoles.isConvenios());
	}

	private AgreementsTree getAgreementsTree() {
		return this.agreements.agreementsTree;
	}

	@Override
	public void onCollapseMenuClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}

	@Override
	public void onViewAgreements(Agreement agreement, Boolean allAgreements) {
		for(EditionListener2 listener : editionsListener)
			listener.onViewAgreements(agreement, allAgreements);
	}

	@Override
	public void onSettingsButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		
		if (nativeEvent.getCtrlKey() || nativeEvent.getMetaKey()) {
	        openIntegrityAgreement();
	        return;
	    }
		 
		//int offset = 225;
		int offset = 0;
		settingsContextMenu.setPopupPosition(nativeEvent.getClientX() - offset, nativeEvent.getClientY());
		settingsContextMenu.show();
		
	}
	
	private void openIntegrityAgreement() {
		new AgreementIntegrityDialog();
	}

	private void getAgreement(Integer agreementId, Consumer<AgreementInfo> success) {
		impl.getAgreementInfo(agreementId, false, new AsyncCallback<AgreementInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				agreementPreview.showError("Error carga convenio", caught.getMessage());
			}

			@Override
			public void onSuccess(AgreementInfo agreement) {
				success.accept(agreement);
			}
			
		});
	}
	
	private void fixAgreement(Integer agreementId, Consumer<Void> success, Consumer<Throwable> error) {
		impl.fixAgreementIntegrity(agreementId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
//				agreementPreview.showError("Error versi\u00f3n convenio", caught.getMessage());
				error.accept(caught);
			}

			@Override
			public void onSuccess(Void end) {
				success.accept(end);
			}
			
		});
	}
	
	private void checkAgreementIntegrity(Integer agreementId, Consumer<AgreementIntegrity> consumer) {
		impl.checkAgreementIntegrity(agreementId, new AsyncCallback<AgreementIntegrity>() {
			
			@Override
			public void onSuccess(AgreementIntegrity agreementIntegrityDB) {
				consumer.accept(agreementIntegrityDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error comprobando integridad : " + caught.getMessage());
			}
		});
	}
	
	private void saveAgreement(Consumer<AgreementInfo> success,  Consumer<Throwable> failure) {
		impl.setAgreementInfo(agreementSelected, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				getAgreement(agreementSelected.getId(), agreementInfo -> success.accept(agreementInfo));
			}
			
		});
	}
	
	// ------------------------------------ Main view
	
	private void showAgreementMessage() {
		agreementPreview.getElement().getStyle().setDisplay(Display.NONE);
		agreementMessage.getElement().getStyle().clearDisplay();
	}
	
	private void showAgreementContainer() {
		agreementMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementPreview.getElement().getStyle().clearDisplay();
	}
	
}
