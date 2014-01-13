package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuItem;
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

	interface Images extends ClientBundle {
		ImageResource concept();

		ImageResource payment();

		ImageResource deduction();

		ImageResource segsocial();
	}

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<img src=\"{0}\"/>")
		SafeHtml img(SafeUri uri);
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
	Button viewButton;
	@UiField
	Button collapseAllButton;

	@UiField
	TreeItem bonusConceptsTreeItem;
	@UiField
	TreeItem paymentConceptsTreeItem;
	@UiField
	TreeItem deductionConceptsTreeItem;

	private List<Listener> listeners;
	private EnterprisesServiceAsync enterprisesService;

	public MetaData() {
		listeners = new LinkedList<Listener>();
		initEnterprisesService();
		initWidget(binder.createAndBindUi(this));
		addBonusConcepts();
		addPaymentConcepts();
		addDeductionConcepts();
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
		if ( userObject == null )
			return;
		else if ( userObject instanceof Bonus )
			onBonusConceptTreeItemSelected((Bonus) userObject ); 
		else if ( userObject instanceof Payment) // null
			onPaymentConceptTreeItemSelected((Payment) userObject);
		else if ( userObject instanceof Deduction )
			onDeductionConceptTreeItemSelected((Deduction) userObject ); 
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
					TreeItem item = new TreeItem(imageItemSafeHtml(
							Resources.IMAGES.payment(), payment));
					item.setUserObject(payment);
					paymentConceptsTreeItem.addItem(item);
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
					TreeItem item = new TreeItem(imageItemSafeHtml(
							Resources.IMAGES.segsocial(), bonus));
					item.setUserObject(bonus);
					bonusConceptsTreeItem.addItem(item);
				}
			}

		}
		enterprisesService.getBonusConcepts(0, -1,
				new BonusConceptsCallback());
	}

	private void addDeductionConcepts() {
		class DeductionConceptsCallback implements AsyncCallback<List<Deduction>> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<Deduction> result) {
				for (Deduction deduction : result) {
					TreeItem item = new TreeItem(imageItemSafeHtml(
							Resources.IMAGES.deduction(), deduction));
					item.setUserObject(deduction);
					deductionConceptsTreeItem.addItem(item);
				}
			}

		}
		enterprisesService.getDeductionConcepts(0, -1,
				new DeductionConceptsCallback());
	}

	private void initEnterprisesService() {
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync enterprisesServiceRaw = GWT
				.create(EnterprisesService.class);
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				enterprisesServiceRaw);
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
	
	private void initContextMenu () {
		
		
		class TypeContextMenu extends ContextMenu {
			ScheduledCommand newCommand = new ScheduledCommand(){
				public void execute() {
				};
			};
			

			public TypeContextMenu() {
				addItem("Nuevo", newCommand, AON.AON_ICON_RESET,
						AON.AON_ICON_CMD_BUTTON);
			}
		};
		
		final TypeContextMenu contextMenu = new TypeContextMenu();
		
		ContextMenuHandler contextMenuHandler = new  ContextMenuHandler(){
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				// stop the browser from opening the context menu
				event.preventDefault();
				event.stopPropagation();

				NativeEvent nativeEvent = event.getNativeEvent();
				
				TreeItem item = tree.getSelectedItem();
				Object userObject = item.getUserObject();
				// TODO : I know that's so ugly and not Object oriented. But
				// it's much more clear than anything else. I promise
				// to change ( even improve ) it soon.
				if ( userObject instanceof Enterprise) {
					
				}

				contextMenu.setPopupPosition(nativeEvent.getClientX(),
						nativeEvent.getClientY());

				contextMenu.show();
			}
			
		};
		
		tree.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
		
		
	}

	// ------------------------------------------------------------------------
	
	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			Item<?> item) {
		StringBuffer str = new StringBuffer(item.getDescription());
		if ( str.length() > 0 )
			str.append(" ("+ item.getName() + ")");
		else
			str.append(item.getName());
		
		return imageItemSafeHtml(imageProto, str.toString() );
	}

	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			Bonus bonus) {
		return imageItemSafeHtml(imageProto, bonus.getDescription() );
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
