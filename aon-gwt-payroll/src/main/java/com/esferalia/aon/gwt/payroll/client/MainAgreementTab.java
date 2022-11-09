package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAgreementsToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.client.Agreements.Listener;
import com.esferalia.aon.gwt.payroll.client.Agreements.Toolbar;
import com.esferalia.aon.gwt.payroll.client.AgreementsCleanDialog.AgreementCleanType;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

//------------------------------------------- EditionListener

interface EditionListener2 {
	
	void onAgreementCopy(Agreement agreement);
	
	void onAgreementPaste(Agreement agreement);
	
	void onAgreementDelete(Agreement agreement);
	
	void onViewAgreements(Agreement agreement, Boolean allAgreements);

}

public class MainAgreementTab extends MainEntryPoint implements Listener,
	EditionListener2, Agreements.Toolbar, AonAgreementsToolbar.Listener {
	
	// ------------------------------------------- UndoManager Listener
	
	static class DraftObjectListener implements UndoManager.Listener {

		private TreeItem treeItem;
		private AgreementDraftObject draftObject;

		public DraftObjectListener(TreeItem treeItem,
				AgreementDraftObject draftObject) {
			this.treeItem = treeItem;
			this.draftObject = draftObject;
		}

		@Override
		public void onChange(@SuppressWarnings("rawtypes") UndoManager undoManager) {
			ImageResource resource = AgreementsTree.getImageResource(
					draftObject.canUndo(), draftObject.hasErrors(),
					draftObject.hasWarnings());
			treeItem.setHTML(AgreementsTree.imageItemSafeHtml(resource,
					draftObject.getDescription()));
		}

	}
	
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
			
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
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
			
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
		}
	}
	
	class SettingsContextMenu extends ContextMenu {
		
		private MenuItem emptyTrash;
		private MenuItem unused;
		
		public SettingsContextMenu() {
			
			emptyTrash = addItem("Ver papelera convenios", new EmptyTrashCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			emptyTrash.ensureDebugId("emptyTrash");
			
			unused = addItem("Ver convenios en desuso", new UnusedCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			unused.ensureDebugId("peculiarities");
			
		}
		
	}

	// ------------------------------------------- UiBinder
	
	static interface Binder extends UiBinder<Widget, MainAgreementTab> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------------- ScheduledCommand
	
	class NewAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			MainAgreementTab.this.agreements.addNewItemTree();
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
			if(Window.confirm("\u00BFDesea subir el convenio seleccionado al dominio padre\u003F"))
				moveAgreement2Parent(MainAgreementTab.this.agreement);
		}
		
		private void moveAgreement2Parent(Agreement agreement) {
			MainAgreementTab.this.agreements.getAgreementsTree().getEnterpriseService()
				.moveAgreement2Parent(agreement, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Ooopsss ...." + caught.getMessage()) ;
				}

				@Override
				public void onSuccess(Void result) {
					Window.alert("Convenio movido correctamente");
					MainAgreementTab.this.agreements.reloadAgreements();
				}
			});
		}
	}
	
	class MoveDownAgreementCommand implements ScheduledCommand {
		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Descargar convenio", new HTML("\u00BFDesea descargar el convenio seleccionado al dominio en el que se encuentra\u003F La descarga incluye la actualizaci\u00f3n de los contratos (de este dominio) que estaban asociados al convenio antiguo."));
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					moveAgreement2Child(MainAgreementTab.this.agreement);
				}
			});
		}
		
		private void moveAgreement2Child(Agreement agreement) {
			AonMessagePanel.showLoading(messagePanel, "Descargando convenio al dominio...");
			MainAgreementTab.this.agreements.getAgreementsTree().getEnterpriseService()
				.moveAgreement2Child(agreement.getId(), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Opss... " + caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					AonMessagePanel.showSuccess(messagePanel, "Convenio descargado correctamente");
					MainAgreementTab.this.agreements.reloadAgreements();
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
					AON.CSS.aonIconAdd(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newItem.ensureDebugId("newItem");
			addSeparator();
			
			copyItem = addItem("Copiar", new CopyAgreementCommand(), 
					AON.CSS.aonIconCopy(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			copyItem.ensureDebugId("copyItem");
			
			pasteItem = addItem("Pegar", new PasteAgreementCommand(), 
					AON.CSS.aonIconPaste(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			pasteItem.ensureDebugId("pasteItem");
			
			deleteItem = addItem("Eliminar", new DeleteAgreementCommand(), 
					AON.CSS.aonIconDelete(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());			
			deleteItem.ensureDebugId("deleteItem");
			
			moveItem = addItem("Mover a..", new MoveAgreementCommand(), 
					AON.CSS.aonIconMoveTo(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			moveItem.ensureDebugId("moveItem");
			moveItem.setTitle("Mover convenio al dominio padre");
			moveItem.setVisible(false);
			
			moveDownItem = addItem("Descargar a..", new MoveDownAgreementCommand(), 
					AON.CSS.aonIconMoveDown(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
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
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String borderR();
		String cmdBtn();
		String dialogGlass();
		String dialogZIndex();
		String tabSelected();
		String tabNotSelected();
	}
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	DockLayoutPanel splitLayoutPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel agreementContainer;
	
	@UiField
	Label agreementTab;
	
	@UiField
	Label levelTab;
	
	@UiField
	Label salaryTableTab;
	
	@UiField
	Label paymentTab;
	
	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	AonAgreementsToolbar toolbar;

	@UiField
	Agreements agreements;
	
	@UiField (provided = true)
	AgreementPreview agreementPreview;
	
	@UiField (provided = true)
	AgreementLevelTab agreementLevelTab;
	
	@UiField (provided = true)
	AgreementSalaryTableTab agreementSalaryTableTab;
	
	@UiField (provided = true)
	AgreementPaymentTab agreementPaymentTab;
	
	@UiField
	HTMLPanel agreementMessage;
	
	@UiField(provided = true)
	MainTrashAgreement mainTrashAgreement;
	
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
		
		mainTrashAgreement = new MainTrashAgreement() {
			
			@Override
			public void onBackButtonClick() {
				getAgreements();
				showAgreements();
			}
			
		};
		
		agreementPreview = new AgreementPreview() {
			
			@Override
			protected void reloadAgreement() {
				getAgreement(agreementSelected.getId(), agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPreview.setAgreementPreview(agreementSelected);
				});
			}
			
			@Override
			public void onSaved() {
				saveAgreement(agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPreview.setAgreementPreview(agreementSelected);
					agreementPreview.showSuccess("Convenio", "Convenio guardado correctamente");
				}, error -> agreementPreview.showError("Error guardando", error.getMessage()));
			}
		};
		
		agreementLevelTab = new AgreementLevelTab() {
			
			@Override
			public void onSaved() {
				saveAgreement(agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementLevelTab.setAgreementLevel(agreementSelected);
					agreementLevelTab.showSuccess("Convenio", "Convenio guardado correctamente");
				}, error -> agreementLevelTab.showError("Error guardando", error.getMessage()));
			}
			
		};
		
		agreementSalaryTableTab = new AgreementSalaryTableTab() {
			
			@Override
			public void onSaved() {
				saveAgreement(agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementSalaryTableTab.setAgreementSalaryTable(agreementSelected);
					agreementSalaryTableTab.showSuccess("Convenio", "Convenio guardado correctamente");
				}, error -> agreementSalaryTableTab.showError("Error guardando", error.getMessage()));
			}
			
			@Override
			public void getVariables(AgreementInfo agreement, Consumer<AgreementInfo> success) {
				getVariablesBD(agreement, variables -> {
					agreement.setAllVariables(variables);
					success.accept(agreement);
				});
			}
			
		};
		
		agreementPaymentTab = new AgreementPaymentTab() {
			
			@Override
			public void onSaved() {
				saveAgreement(agreementInfo -> {
					agreementSelected = agreementInfo;
					agreementPaymentTab.setAgreementPayment(agreementSelected);
					agreementPaymentTab.showSuccess("Convenio", "Convenio guardado correctamente");
				}, error -> agreementPaymentTab.showError("Error guardando", error.getMessage()));
			}
		};
		
		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		
		// LocalStorage getItems
		this.storage = Storage.getLocalStorageIfSupported();

		// Add the outerpanel to the RootLayoutPanel, so that it will be displayed.
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
		
		deckPanel.setAnimationEnabled(true);
		mainDeckPanel.setAnimationEnabled(true);
		mainDeckPanel.setHeight((Window.getClientHeight() - 185) + "px");
		showAgreements();
		
		agreements.addStyleName(style.borderR());
		
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
				MainAgreementTab.this.parentDomain = parentDomain;	
				agreements.getAgreementsTree().getEnterpriseService().getDomain(new AsyncCallback<Integer>() {
					
					@Override
					public void onFailure(Throwable caught) {
						// Not use here
					}
					
					@Override
					public void onSuccess(Integer domain) {
						MainAgreementTab.this.domain = domain;	

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
		
	}

	private void showAgreements() {
		deckPanel.showWidget(0);
	}
	
	private void showTrashAgreements() {
		deckPanel.showWidget(1);
	}
	
	// ------------------------------------------- Agreements.Listener

	@Override
	public void onAgreementSelected(Agreement agreement) {
		this.agreement = agreement;		
		this.contextMenu.setVisibleCopyItem(agreement.getId());		
		
		this.contextMenu.setVisibleMoveItem( (parentDomain != null) && 
				parentDomain.intValue() != agreement.getDomain().intValue() && agreement.getDomain().intValue() != 0);
		
		this.contextMenu.setVisibleMoveDownItem(agreement.getDomain().intValue() != 0 && domain != agreement.getDomain().intValue());
		
		agreementPreview.showLoading("Cargando convenio...");
		getAgreement(agreement.getId(), agreeementInfo -> {
			agreementSelected = agreeementInfo;
			showAgreementContainer();
			selectAgreementTab();
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
					agreements.reloadAgreements();			
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
							dialog.setGlassStyleName(style.dialogGlass());
							dialog.addStyleName(style.dialogZIndex());
							dialog.confirm(new AonAcceptDialogCallback() {
								
								@Override
								public void onCancel() {
									// Not use here
								}
								
								@Override
								public void onAccept() {
									agreements.getAgreementsTree().getEnterpriseService().updateAgreementId(
											agreement, new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("No ha sido posible enviar el Convenio a la papelera.");
										}

										@Override
										public void onSuccess(Void result) {
											MainAgreementTab.this.agreements.resetTypeView();
											MainAgreementTab.this.agreements.reloadAgreements();
//											showSelectAgreementMessage();
										}
									});
								}
							});
						}});	
		} else {
			String message = "Este convenio ser\u00E1 eliminado de forma permanente.<br>\u00BFDesea eliminar el convenio de <b>" + agreement.getDescription() + "</b>?";
			AonDialog dialog = new AonDialog("BORRADO", new HTML(message));
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
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
							Window.alert("No ha sido posible eliminar el Convenio.");
						}

						@Override
						public void onSuccess(Void result) {
							MainAgreementTab.this.agreements.setViewAgreements(false);
							MainAgreementTab.this.agreements.resetTypeView();
							MainAgreementTab.this.agreements.reloadAgreements();
//							showSelectAgreementMessage();
						}
					});
				}
			});
		}
		
	}
	
	@Override
	public void onCollapseMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 0);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onShowMenuButtonClick() {
		splitLayoutPanel.setWidgetSize(agreements, 350);
		splitLayoutPanel.animate(500);
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
		splitLayoutPanel.animate(500);
		Timer timer = new Timer() {
			@Override
			public void run() {
				agreementPreview.setTablesWidthCollapseMenu();
			}
		};
		timer.schedule(500);
	}

	@Override
	public void onShowMenuButtonClick(ClickEvent event) {
		splitLayoutPanel.setWidgetSize(agreements, 350);
		splitLayoutPanel.animate(500);
		Timer timer = new Timer() {
			@Override
			public void run() {
				agreementPreview.setTablesWidth();
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
						AonDialog dialog = new AonDialog("Error", new HTML(caught.getMessage()));
						dialog.warning();
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
		
		serviAgreementDialog.setGlassStyleName(style.dialogGlass());
		serviAgreementDialog.addStyleName(style.dialogZIndex());
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
		int offset = 225;
		settingsContextMenu.setPopupPosition(nativeEvent.getClientX() - offset, nativeEvent.getClientY());
		settingsContextMenu.show();
		
	}
	
	private void getAgreement(Integer agreementId, Consumer<AgreementInfo> success) {
		impl.getAgreementInfo(agreementId, new AsyncCallback<AgreementInfo>() {

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
	
	private void getVariablesBD(AgreementInfo agreement, Consumer<Set<String>> success) {
		impl.getAgreementVariables(agreement, new AsyncCallback<Set<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}

			@Override
			public void onSuccess(Set<String> variables) {
				success.accept(variables);
			}
			
		});
	}
	
	// ------------------------------------ Main view
	
	private void showAgreementMessage() {
		agreementContainer.getElement().getStyle().setDisplay(Display.NONE);
		agreementMessage.getElement().getStyle().clearDisplay();
	}
	
	private void showAgreementContainer() {
		agreementMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementContainer.getElement().getStyle().clearDisplay();
	}

	// ------------------------------------ Tab Selection
	
	@UiHandler("agreementTab")
	void onAgreementTabClick(ClickEvent event) {
		if(!handleSave()) selectAgreementTab();
		else showWarnSave(accept -> selectAgreementTab());
	}
	
	@UiHandler("paymentTab")
	void onPaymentTabClick(ClickEvent event) {
		if(!handleSave()) selectPaymentTab();
		else showWarnSave(accept -> selectPaymentTab());
	}
	
	@UiHandler("salaryTableTab")
	void onSalaryTableTabClick(ClickEvent event) {
		if(!handleSave()) selectSalaryTableTab();
		else showWarnSave(accept -> selectSalaryTableTab());
	}
	
	@UiHandler("levelTab")
	void onLevelTabClick(ClickEvent event) {
		if(!handleSave()) selectLevelTab();
		else showWarnSave(accept -> selectLevelTab());
	}
	
	private boolean handleSave() {
		int widgetIdx = mainDeckPanel.getVisibleWidget();
		switch (widgetIdx) {
		case 1:
			return agreementLevelTab.hasChange();
		case 2:
			return agreementSalaryTableTab.hasChange();
		case 3:
			return agreementPaymentTab.hasChange();
		default:
			return false;
		}
	}
	
	private void showWarnSave(Consumer<Void> accept) {
		AonDialog warnDialog = new AonDialog("Cambios sin guardar", new HTMLPanel("Esta abandonando una pesta\u00f1a con cambios no guardados. \u00bfEst\u00e1 seguro de que desea continuar sin guardar\u003f"));
		warnDialog.setGlassStyleName(style.dialogGlass());
		warnDialog.addStyleName(style.dialogZIndex());
		warnDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				// Recargando el convenio para omitir cambios
				getAgreement(agreementSelected.getId(), agreementInfo -> {
					agreementSelected = agreementInfo;
					accept.accept(null);
				});
				
				// NO recargando el convenio para omitir cambios
				// accept.accept(null);
			}
		});
	}
	
	private void selectAgreementTab() {
		agreementTab.removeStyleName(style.tabNotSelected());
		agreementTab.addStyleName(style.tabSelected());
		
		mainDeckPanel.showWidget(0);
		
		levelTab.removeStyleName(style.tabSelected());
		levelTab.addStyleName(style.tabNotSelected());
		salaryTableTab.removeStyleName(style.tabSelected());
		salaryTableTab.addStyleName(style.tabNotSelected());
		paymentTab.removeStyleName(style.tabSelected());
		paymentTab.addStyleName(style.tabNotSelected());
		
		agreementPreview.setAgreementPreview(agreementSelected);
		agreementPreview.setHasChange(false);
	}
	
	private void selectLevelTab() {
		levelTab.removeStyleName(style.tabNotSelected());
		levelTab.addStyleName(style.tabSelected());
		
		mainDeckPanel.showWidget(1);
		
		agreementTab.removeStyleName(style.tabSelected());
		agreementTab.addStyleName(style.tabNotSelected());
		salaryTableTab.removeStyleName(style.tabSelected());
		salaryTableTab.addStyleName(style.tabNotSelected());
		paymentTab.removeStyleName(style.tabSelected());
		paymentTab.addStyleName(style.tabNotSelected());
		
		agreementLevelTab.setAgreementLevel(agreementSelected);
		agreementLevelTab.setHasChange(false);
	}
	
	private void selectSalaryTableTab() {
		salaryTableTab.removeStyleName(style.tabNotSelected());
		salaryTableTab.addStyleName(style.tabSelected());
		
		mainDeckPanel.showWidget(2);
		
		agreementTab.removeStyleName(style.tabSelected());
		agreementTab.addStyleName(style.tabNotSelected());
		levelTab.removeStyleName(style.tabSelected());
		levelTab.addStyleName(style.tabNotSelected());
		paymentTab.removeStyleName(style.tabSelected());
		paymentTab.addStyleName(style.tabNotSelected());
		
		agreementSalaryTableTab.setAgreementSalaryTable(agreementSelected);
		agreementSalaryTableTab.setHasChange(false);
	}
	
	private void selectPaymentTab() {
		paymentTab.removeStyleName(style.tabNotSelected());
		paymentTab.addStyleName(style.tabSelected());
		
		mainDeckPanel.showWidget(3);
		
		agreementTab.removeStyleName(style.tabSelected());
		agreementTab.addStyleName(style.tabNotSelected());
		levelTab.removeStyleName(style.tabSelected());
		levelTab.addStyleName(style.tabNotSelected());
		salaryTableTab.removeStyleName(style.tabSelected());
		salaryTableTab.addStyleName(style.tabNotSelected());
		
		agreementPaymentTab.setAgreementPayment(agreementSelected);
		agreementPaymentTab.setHasChange(false);
	}
	
}
