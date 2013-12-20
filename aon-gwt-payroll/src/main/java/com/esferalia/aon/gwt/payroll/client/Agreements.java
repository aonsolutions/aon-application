package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Agreements extends ResizeComposite {

	interface Images extends ClientBundle {
		ImageResource agreement();

		ImageResource agreement_warn();

		ImageResource agreement_error();

		ImageResource agreement_changed();

		ImageResource agreement_changed_warn();

		ImageResource agreement_changed_error();

	}

	private static final Images IMAGES = GWT.create(Images.class);

	private static final ImageResource RESOURCES[][][] = {
			{ { IMAGES.agreement(), IMAGES.agreement_warn() },
					{ IMAGES.agreement_error(), IMAGES.agreement_error() } },
			{
					{ IMAGES.agreement_changed(),
							IMAGES.agreement_changed_warn() },
					{ IMAGES.agreement_changed_error(),
							IMAGES.agreement_changed_error() } } };

	interface Listener {
		void onAgreementSelected(Agreement agreement);
	}

	interface Binder extends UiBinder<Widget, Agreements> {
	}

	private static final Binder BINDER = GWT.create(Binder.class);

	private static int newsIdCounter = -1;

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Button newButton;
	@UiField
	Button viewButton;
	@UiField
	Button collapseAllButton;
	
	
	private List<Listener> listeners;
	private EnterprisesServiceAsync enterprisesService;

	public Agreements() {
		listeners = new LinkedList<Listener>();
		initWidget(BINDER.createAndBindUi(this));

		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync enterprisesServiceRaw = GWT
				.create(EnterprisesService.class);
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				enterprisesServiceRaw);

		enterprisesService.getAgreements(0, 100,
				new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {
						int item2Select = -1;
						for (int i = 0; i < agreements.size(); i++) {

							Agreement agreement = agreements.get(i);
							addAgreementItem(agreement);

							if (agreement.isRedefined()) {
								if (item2Select == -1)
									item2Select = i;
							}
							if (agreement.hasEmployees()) {
								if (item2Select == -1)
									item2Select = i;
							}

						}
						// Select the first one.
						if (tree.getItemCount() > 0)
							tree.setSelectedItem(
									tree.getItem(Math.max(item2Select, 0)),
									true);
					}

				});
		initContextMenu();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}


	// ------------------------------------------------------------- UiHandlers

	@UiHandler("newButton")
	void onNewButtonClicked(ClickEvent event) {
		Agreement agreement = newAgreement();
		tree.setSelectedItem(addAgreementItem(agreement));
	}

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Agreement agreement = (Agreement) selectedItem.getUserObject();
		fireAgreementSelected(agreement);

	}

	// -------------------------------------------------------------- Protected
	// methods

	TreeItem getSelectedItem() {
		return tree.getSelectedItem();
	}

	// -------------------------------------------------------- Private methods

	private TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		if (agreement.isRedefined()) {
			description = "*" + description;
		}

		TreeItem treeItem = new TreeItem(imageItemSafeHtml(
				getImageResource(agreement), description));
		treeItem.setUserObject(agreement);

		if (agreement.isRedefined()) {
			treeItem.addStyleName("gwt-TreeItem-highlight");
		}
		if (agreement.hasEmployees()) {
			treeItem.addStyleName("gwt-TreeItem-highlight");
		}

		tree.addItem(treeItem);

		return treeItem;

	}

	private void fireAgreementSelected(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSelected(agreement);
	}

	private Agreement getSelectedAgreement() {
		TreeItem selectedItem = tree.getSelectedItem();
		return selectedItem != null ? (Agreement) selectedItem.getUserObject()
				: null;
	}

	private void initContextMenu () {
		
		
		class AgreementContextMenu extends ContextMenu {

			ScheduledCommand newCommand = new ScheduledCommand(){
				public void execute() {
					Agreement agreement = Agreements.newAgreement();
					Agreements.this.tree.setSelectedItem(addAgreementItem(agreement));
				};
			};
			ScheduledCommand copyCommand = new ScheduledCommand(){
				public void execute() {
				};
			};
			ScheduledCommand pasteCommand = new ScheduledCommand(){
				public void execute() {
				};
			};
			ScheduledCommand deleteCommand = new ScheduledCommand(){
				public void execute() {
					deleteAgreement(getSelectedAgreement());
				};
			};
			
			private MenuItem pasteItem;
			private MenuItem deleteItem;

			public AgreementContextMenu() {

				addItem("Nuevo", newCommand, AON.AON_ICON_RESET,
						AON.AON_ICON_CMD_BUTTON);
				addSeparator();
				addItem("Copiar", copyCommand ,
						AON.AON_ICON_COPY, AON.AON_ICON_CMD_BUTTON);
				pasteItem = addItem("Pegar", pasteCommand ,
						AON.AON_ICON_CLIPBOARD, AON.AON_ICON_CMD_BUTTON);
				pasteItem.setEnabled(false);
				deleteItem = addItem("Borrar", deleteCommand ,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				deleteItem.setEnabled(false);

			}
			
			@Override
			public void show() {
				sync();
				super.show();
			}
			
			private void sync(){
				Agreement agreement = Agreements.this.getSelectedAgreement();
				deleteItem.setEnabled(agreement.canDelete());
			}
			
		};
		
		final AgreementContextMenu contextMenu = new AgreementContextMenu();
		
		ContextMenuHandler contextMenuHandler = new  ContextMenuHandler(){
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				// stop the browser from opening the context menu
				event.preventDefault();
				event.stopPropagation();

				NativeEvent nativeEvent = event.getNativeEvent();
				contextMenu.setPopupPosition(nativeEvent.getClientX(),
						nativeEvent.getClientY());
				contextMenu.show();
			}
			
		};
		
		tree.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
		
		
	}
	
	private void deleteAgreement(Agreement agreement) {
		deleteTreeItem(getSelectedItem());
	}

	private void deleteTreeItem(TreeItem treeItem) {
		tree.removeItem(treeItem);
	}

	private Agreement pasteAgreement(Agreement agreement) {
		return null;
	}

	// ------------------------------------------------------------------------
	public static ImageResource getImageResource(boolean changes,
			boolean errors, boolean warns) {
		return RESOURCES[changes ? 1 : 0][errors ? 1 : 0][warns ? 1 : 0];
	}
	
	// ------------------------------------------------------------------------

	/**
	 * Generates SafeHtml for a tree item with an attached icon.
	 */
	static SafeHtml imageItemSafeHtml(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		if (title != null)
			builder.appendEscaped(" " + title);
		return builder.toSafeHtml();
	}

	// ------------------------------------------------------------------------
	
	


	private static synchronized Agreement newAgreement() {
		Agreement agreement = new Agreement();
		int newId = newsIdCounter--;
		agreement.setId(newId);
		agreement.setDescription("CONVENIO NO GUARDADO " + -newId);
		return agreement;
	}

 	private static ImageResource getImageResource(Agreement agreement) {
		return RESOURCES[0][0][agreement.hasLevelsWithoutCategories() ? 1 : 0];
	}


}
