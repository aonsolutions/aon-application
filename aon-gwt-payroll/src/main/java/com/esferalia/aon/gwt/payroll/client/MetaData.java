package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MetaData extends ResizeComposite {

	interface Listener {
		void onBonusConceptSelected(Bonus bonus);

		void onPaymentConceptSelected(Payment payment);

		void onDeductionConceptSelected(Deduction deduction);

	}


	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<img src=\"{0}\"/>")
		SafeHtml img(SafeUri uri);

		@SafeHtmlTemplates.Template("<span class=\"{0} aon-icon-commandButton\">{1}</span>")
		SafeHtml menuItem(String style, String text);

	}

	public static class Resources {

		private static final Images IMAGES = GWT.create(Images.class);
		private static final Templates TEMPLATES = GWT.create(Templates.class);

		public String concept() {
			return TEMPLATES.img(IMAGES.concept().getSafeUri()).asString();
		}

		public String payment() {
			return TEMPLATES.img(IMAGES.payment().getSafeUri()).asString();
		}

		public String deduction() {
			return TEMPLATES.img(IMAGES.deduction().getSafeUri()).asString();
		}

		public String segsocial() {
			return TEMPLATES.img(IMAGES.segsocial().getSafeUri()).asString();
		}

	}

	interface Binder extends UiBinder<Widget, MetaData> {
	}

	private static final Binder binder = GWT.create(Binder.class);

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

	@UiField
	TreeItem bonusConceptsTreeItem;
	@UiField
	TreeItem paymentConceptsTreeItem;
	@UiField
	TreeItem deductionConceptsTreeItem;

	private PopupPanel newPopup;
	private List<Listener> listeners;
	private DomainEnterprisesServiceAsync enterprisesService;

	public MetaData() {
		listeners = new LinkedList<Listener>();
		initEnterprisesService();
		initWidget(binder.createAndBindUi(this));
		addBonusConcepts();
		addPaymentConcepts();
		addDeductionConcepts();
		initNewPopupMenu();
		//initContextMenu();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	// ------------------------------------------------------------ UiHandlers

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object userObject = selectedItem.getUserObject();
		if (userObject == null)
			return;
		else if (userObject instanceof Bonus)
			onBonusConceptTreeItemSelected((Bonus) userObject);
		else if (userObject instanceof Payment) // null
			onPaymentConceptTreeItemSelected((Payment) userObject);
		else if (userObject instanceof Deduction)
			onDeductionConceptTreeItemSelected((Deduction) userObject);
	}

	@UiHandler("newButton")
	void onNewButtonClicked(ClickEvent event) {
		int left = newButton.getAbsoluteLeft();
		int top = newButton.getAbsoluteTop() + newButton.getOffsetHeight();
		newPopup.setPopupPosition(left, top);
		newPopup.show();

	}

	@UiHandler("collapseAllButton")
	void onColapseAllButtonClicked(ClickEvent event) {

	}

	// -------------------------------------------------------- Private methods

	private void addPaymentConcepts() {
		class PaymentConceptsCallback implements AsyncCallback<List<Payment>> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Payment> result) {
				for (Payment payment : result) {
					addPaymentConceptItem(payment);
				}
			}

		}
		enterprisesService.getPaymentConcepts(0, -1,
				new PaymentConceptsCallback());
	}

	private void addBonusConcepts() {
		class BonusConceptsCallback implements AsyncCallback<List<Bonus>> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Bonus> result) {
				for (Bonus bonus : result) {
					addBonusConceptItem(bonus);
				}
			}

		}
		enterprisesService.getBonusConcepts(0, -1, new BonusConceptsCallback());
	}

	private void addDeductionConcepts() {
		class DeductionConceptsCallback implements
				AsyncCallback<List<Deduction>> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Deduction> result) {
				for (Deduction deduction : result) {
					addDeductionConceptItem(deduction);
				}
			}

		}
		enterprisesService.getDeductionConcepts(0, -1,
				new DeductionConceptsCallback());
	}

	private void initEnterprisesService() {
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	}

	private void onBonusConceptTreeItemSelected(Bonus bonus) {
		for (Listener listener : listeners)
			listener.onBonusConceptSelected(bonus);
	}

	private void onDeductionConceptTreeItemSelected(Deduction deduction) {
		for (Listener listener : listeners)
			listener.onDeductionConceptSelected(deduction);
	}

	private void onPaymentConceptTreeItemSelected(Payment payment) {
		for (Listener listener : listeners)
			listener.onPaymentConceptSelected(payment);
	}

	private void initContextMenu() {

		class AgreementContextMenu extends ContextMenu {

			ScheduledCommand newCommand = new ScheduledCommand() {
				public void execute() {
				};
			};
			ScheduledCommand copyCommand = new ScheduledCommand() {
				public void execute() {
				};
			};
			ScheduledCommand pasteCommand = new ScheduledCommand() {
				public void execute() {
				};
			};
			ScheduledCommand deleteCommand = new ScheduledCommand() {
				public void execute() {
				};
			};

			private MenuItem copyItem;
			private MenuItem pasteItem;
			private MenuItem deleteItem;

			public AgreementContextMenu() {

				addItem("Nuevo", newCommand, AON.AON_ICON_RESET,
						AON.AON_ICON_CMD_BUTTON);
				addSeparator();
				copyItem = addItem("Copiar", copyCommand, AON.AON_ICON_COPY,
						AON.AON_ICON_CMD_BUTTON);
				copyItem.setEnabled(false);
				pasteItem = addItem("Pegar", pasteCommand,
						AON.AON_ICON_CLIPBOARD, AON.AON_ICON_CMD_BUTTON);
				pasteItem.setEnabled(false);
				deleteItem = addItem("Borrar", deleteCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				deleteItem.setEnabled(false);

			}

			@Override
			public void show() {
				sync();
				super.show();
			}

			private void sync() {
			}

		}
		;

		final AgreementContextMenu contextMenu = new AgreementContextMenu();

		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
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

	private void initNewPopupMenu() {

		newPopup = new PopupPanel();

		MenuBar menuBar = new MenuBar(true);

		MenuItem newPaymentConceptMenuItem = new MenuItem(
				Resources.TEMPLATES.menuItem(AON.AON_ICON_PAYNNENT, "Devengo"),
				new Command() {
					@Override
					public void execute() {
						Payment payment = newPaymentConcept();
						TreeItem item = addPaymentConceptItem(payment);
						setSelectedItem(item, true);
						newPopup.hide();
					}
				});
		menuBar.addItem(newPaymentConceptMenuItem);

		MenuItem newDeductionConceptMenuItem = new MenuItem(
				Resources.TEMPLATES.menuItem(AON.AON_ICON_DEDUCTION,
						"Deducci\u00f3n"), new Command() {
					@Override
					public void execute() {
						Deduction deduction = newDeductionConcept();
						TreeItem item = addDeductionConceptItem(deduction);
						setSelectedItem(item, true);
						newPopup.hide();
					}
				});
		menuBar.addItem(newDeductionConceptMenuItem);

		MenuItem newBonusConceptMenuItem = new MenuItem(
				Resources.TEMPLATES.menuItem(AON.AON_ICON_BONUS,
						"Bonificaci\u00f3n"), new Command() {
					@Override
					public void execute() {
						Bonus bonus = newBonusConcept();
						TreeItem item = addBonusConceptItem(bonus);
						setSelectedItem(item, true);
						newPopup.hide();
					}
				});
		menuBar.addItem(newBonusConceptMenuItem);

		newPopup.add(menuBar);
		newPopup.setStyleName("gwt-MenuBarPopup");
		newPopup.setAutoHideEnabled(true);

	}

	private void setSelectedItem(TreeItem item, boolean fireEvents) {
		item.getParentItem().setState(true);
		tree.setSelectedItem(item, fireEvents);
	}

	private TreeItem addBonusConceptItem(Bonus bonus) {
		TreeItem item = new TreeItem(imageItemSafeHtml(
				Resources.IMAGES.segsocial(), bonus));
		item.setUserObject(bonus);
		bonusConceptsTreeItem.addItem(item);
		return item;
	}

	private TreeItem addPaymentConceptItem(Payment payment) {
		TreeItem item = new TreeItem(imageItemSafeHtml(
				Resources.IMAGES.payment(), payment));
		item.setUserObject(payment);
		paymentConceptsTreeItem.addItem(item);
		return item;
	}

	private TreeItem addDeductionConceptItem(Deduction deduction) {
		TreeItem item = new TreeItem(imageItemSafeHtml(
				Resources.IMAGES.deduction(), deduction));
		item.setUserObject(deduction);
		deductionConceptsTreeItem.addItem(item);
		return item;
	}

	// ------------------------------------------------------------------------

	private static synchronized Deduction newDeductionConcept() {
		Deduction deduction = new Deduction();
		deduction.setDescription("DEDUCCI\u00d3N NO GUARDADA");
		return deduction;
	}

	private static synchronized Bonus newBonusConcept() {
		Bonus bonus = new Bonus();
		bonus.setDescription("BONIFICACI\u00d3N NO GUARDADA");
		return bonus;
	}

	private static synchronized Payment newPaymentConcept() {
		Payment payment = new Payment();
		payment.setDescription("DEVENGO NO GUARDADO");
		payment.setIrpfExpression("_P");
		payment.setQuoteExpression("_P");
		return payment;
	}

	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			Item<?> item) {
		String description = item.getDescription();
		description = description != null ? description.replaceAll(
				"@\\{([^\\}]*)\\}", "") : "";

		StringBuffer str = new StringBuffer(description);

		if (StringUtils.isBlank(item.getName()))
			;
		else if (str.length() > 0)
			str.append(" (" + item.getName() + ")");
		else
			str.append(item.getName());

		return imageItemSafeHtml(imageProto, str.toString());
	}

	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			Bonus bonus) {
		return imageItemSafeHtml(imageProto, bonus.getDescription());
	}

	/**
	 * Generates SafeHtml for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			String str) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.appendEscaped(" " + str);
		return builder.toSafeHtml();
	}

}
