package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Agreements extends ResizeComposite {

	interface Images extends ClientBundle {
		ImageResource agreement();
	}

	interface Listener {
		void onAgreementSelected(Agreement agreement);
	}

	interface Binder extends UiBinder<Widget, Agreements> {
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

	private Images images;

	private List<Listener> listeners;

	private EnterprisesServiceAsync enterprisesService;

	public Agreements() {
		images = GWT.create(Images.class);
		listeners = new LinkedList<Listener>();
		initWidget(binder.createAndBindUi(this));

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
						int item2Select  = -1 ;
						for (int i = 0; i < agreements.size(); i++) {
							Agreement agreement  = agreements.get(i);
							String description = agreement.getDescription();
							if (agreement.isRedefined()) { 
								description = "*" + description;
							}

							TreeItem treeItem = new TreeItem(imageItemSafeHtml(
									images.agreement(), description));
							treeItem.setUserObject(agreement);

							if (agreement.isRedefined()) {
								if ( item2Select == -1 ) item2Select = i;
								treeItem.addStyleName("gwt-TreeItem-highlight");
							}
							if ( agreement.hasEmployees() ){
								if ( item2Select == -1 ) item2Select = i;
								treeItem.addStyleName("gwt-TreeItem-highlight");
							}

							tree.addItem(treeItem);

						}
						// Select the first one.
						if (tree.getItemCount() > 0)
							tree.setSelectedItem(tree.getItem(Math.max(item2Select, 0)), true);
					}

				});
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	// -------------------------------------------------------------- UiHandlers

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Agreement agreement = (Agreement) selectedItem.getUserObject();
		fireAgreementSelected(agreement);

	}

	// --------------------------------------------------------- Private methods

	private void fireAgreementSelected(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSelected(agreement);
	}

	/**
	 * Generates SageHtml for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemSafeHtml(ImageResource imageProto,
			String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.appendEscaped(" " + title);
		return builder.toSafeHtml();
	}

}
