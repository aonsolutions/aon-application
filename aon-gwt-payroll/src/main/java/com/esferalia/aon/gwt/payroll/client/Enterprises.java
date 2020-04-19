package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Enterprises extends ResizeComposite implements
		SelectionHandler<TreeItem>, 
		ContextMenuHandler{

	interface Listener {

		void onCCCSelected(CCC ccc);

		void onActivitySelected(Activity workplace);

		void onEnterpriseSelected(Enterprise enterprise);

		void onEnterprisesSelected(List<Enterprise> enterprises);

		void onCCCContextMenu(CCC ccc, ContextMenuEvent event);

		void onActivityContextMenu(Activity activity, ContextMenuEvent event);

		void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event);

		void onEnterprisesContextMenu(List<Enterprise> enterprises, ContextMenuEvent event);

		void onEnterprises(List<Enterprise> enterprises);
	}

	interface Binder extends UiBinder<Widget, Enterprises> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;

//	@UiField
//	OptionsToolbar toolbar;

	private Images images;
	private List<Listener> listeners;
	private DomainEnterprisesServiceAsync enterprisesService;

	private boolean inactive = false;

	public Enterprises() {

		images = GWT.create(Images.class);
		listeners = new LinkedList<Enterprises.Listener>();

		enterprisesService = DomainEnterprisesServiceAsync.newInstance();		
				
		initWidget(binder.createAndBindUi(this));

		tree.addSelectionHandler(this);
		tree.addDomHandler(this, ContextMenuEvent.getType());

		enterprisesService.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(List<Enterprise> enterprises) {
					Enterprises.this.onEnterprises(enterprises);
			}

		});

	}
	
	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void refresh() {
		setSelected(getSelected(), true);
	}
	
	public TreeItem getSelected() {
		return tree.getSelectedItem();
	}
	
	public void setSelected(TreeItem treeItem, boolean fireEvents) {
		tree.setSelectedItem(treeItem, fireEvents);
	}
	// -
	
	protected void onEnterprise(Enterprise enterprise, TreeItem rootItem) {

		clearEnterprise(enterprise);
		
		
		final TreeItem enterpriseItem = addImageItem(rootItem,
				enterprise.getName(), images.enterprise());

		enterpriseItem.setUserObject(enterprise);
		
		for (Activity activity: enterprise.getActivities()) {

			TreeItem activityItem = addImageItem(enterpriseItem, 
					activity.getDescription(),
					images.ine());
			activityItem.setUserObject(activity);
			
			for ( CCC ccc : activity.getCccs() ) {
				TreeItem cccItem = addImageItem(activityItem, ccc.getCode(),
						images.segsocial());
				cccItem.setUserObject(ccc);
			}
			
		}


		enterpriseItem.setState(true, true);

		scrollPanel.scrollToLeft();


	}

	protected void onEnterprises(List<Enterprise> enterprises) {
		

		TreeItem enterprisesItem = new TreeItem(imageItemHTML(images.enterprises(), "EMPRESAS"));
		enterprisesItem.setUserObject(enterprises);
		tree.addItem(enterprisesItem);
		
		for (Enterprise enterprise : enterprises)
			Enterprises.this.onEnterprise(enterprise, enterprisesItem);
		
		enterprisesItem.setState(true, true);
		tree.setSelectedItem(enterprisesItem);

		onEnterpr1ses(enterprises);
	}

	public void clearEnterprise(Enterprise enterprise) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem treeItem = tree.getItem(i);
			if (enterprise.equals(treeItem.getUserObject())) {
				tree.removeItem(treeItem);
				return;
			}
		}
	}


	// From SelectionHandler<TreeItem>
	@Override
	@SuppressWarnings("unchecked")
	public void onSelection(SelectionEvent<TreeItem> event) {

		TreeItem item = event.getSelectedItem();
		Object userObject = item.getUserObject();

		// TODO : I know that's so ugly and not Object oriented. But
		// it's much more clear than anything else. I promise
		// to change ( even improve ) it soon.
		if (userObject instanceof Enterprise) {
			onEnterpriseSelected((Enterprise) userObject);
		} else if (userObject instanceof Activity) {
			onActivitySelected((Activity) userObject);
		} else if (userObject instanceof CCC ) {
			onCCCSelected((CCC) userObject);
		} else if (userObject instanceof List<?>) {
			onEnterprisesSelected((List<Enterprise>) userObject);
		} 
	}



	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// stop the browser from opening the context menu
		event.preventDefault();
		event.stopPropagation();

		TreeItem item = tree.getSelectedItem();
		Object userObject = item.getUserObject();
		// TODO : I know that's so ugly and not Object oriented. But
		// it's much more clear than anything else. I promise
		// to change ( even improve ) it soon.
		if (userObject instanceof Enterprise) {
			onEnterpiseContextMenu((Enterprise) userObject, event);
		} else if (userObject instanceof Activity) {
			onActivityContextMenu((Activity) userObject, event);
		} else if (userObject instanceof CCC) {
			onCCCContextMenu((CCC) userObject, event);
		} else if (userObject instanceof List<?>) {
			onEnterpisesContextMenu((List<Enterprise>) userObject, event);
		} 

	}

	public boolean elementInViewport(Element el) {

		int elTop = el.getAbsoluteTop();
		int elLeft = el.getAbsoluteLeft();
		int elWidth = el.getOffsetWidth();
		int elHeight = el.getOffsetHeight();

		int windowTop = Window.getScrollTop();
		int windowLeft = Window.getScrollLeft();
		int windowWidth = Window.getClientWidth();
		int windowHeight = Window.getClientHeight();

		return elTop < (windowTop + windowHeight)
				&& elLeft < (windowLeft + windowWidth)
				&& (elTop + elHeight) > windowTop
				&& (elLeft + elWidth) > windowLeft;

	}
	
	public Enterprise getEnterprise(Workplace workplace) {
		for ( Enterprise enterprise : getEnterprises() ){ 
			if ( enterprise.getWorkplaces().contains(workplace) ){
				return enterprise;
			}
		}
		throw new NoSuchElementException();
	}

	public Enterprise getEnterprise(Activity activity) {
		for ( Enterprise enterprise : getEnterprises() ){ 
			if ( enterprise.getActivities().contains(activity) ){
				return enterprise;
			}
		}
		throw new NoSuchElementException();
	}

	public Enterprise getEnterprise(CCC ccc) {
		for ( Enterprise enterprise : getEnterprises() ){ 
			for (Activity activity : enterprise.getActivities()){
				if ( activity.getCccs().contains(ccc)) {
					return enterprise;
				}
			}
		}
		throw new NoSuchElementException();
	}

	protected Collection<Enterprise> getEnterprises( ) {
		List<Enterprise> enterprises = new ArrayList<Enterprise>();
		for ( int i = 0; i < tree.getItemCount(); i++)
			enterprises.addAll(getEnterprises(tree.getItem(i)));
		return enterprises;
	}

	// ------------------------------------------------------------------------


	private void onCCCSelected(CCC ccc) {
		for (Listener listener : listeners) {
			listener.onCCCSelected(ccc);
		}
	}

	private void onActivitySelected(Activity activity) {
		for (Listener listener : listeners) {
			listener.onActivitySelected(activity);
		}
	}

	private void onEnterpriseSelected(Enterprise enterprise) {
		for (Listener listener : listeners) {
			listener.onEnterpriseSelected(enterprise);
		}
	}

	private void onEnterprisesSelected(List<Enterprise> enterprises) {
		for (Listener listener : listeners) {
			listener.onEnterprisesSelected(enterprises);
		}
	}

	private void onCCCContextMenu(CCC ccc,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onCCCContextMenu(ccc, event);
		}
	}

	private void onActivityContextMenu(Activity activity,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onActivityContextMenu(activity, event);
		}
	}

	private void onEnterpiseContextMenu(Enterprise enterprise,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEnterpriseContextMenu(enterprise, event);
		}
	}

	private void onEnterpisesContextMenu(List<Enterprise> enterprises,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEnterprisesContextMenu(enterprises, event);
		}
	}

	private void onEnterpr1ses(List<Enterprise> enterprises) {
		for (Listener listener : listeners) {
			listener.onEnterprises(enterprises);
		}
	}

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private TreeItem addImageItem(TreeItem root, String title,
			ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.addItem(item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private SafeHtml imageItemHTML(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.appendEscaped(title);
		return builder.toSafeHtml();
	}



	private boolean isWorkPlaceVisible(Workplace workplace) {
		return workplace.isActive() || inactive;
	}
	
	
	private Collection<Enterprise> getEnterprises(TreeItem treeItem) {
		Object userObject = treeItem.getUserObject();
		if ( userObject instanceof Enterprise )
			return Collections.singleton((Enterprise)userObject);

		List<Enterprise> enterprises = new ArrayList<Enterprise>();
		for ( int i = 0; i < treeItem.getChildCount(); i++)
			enterprises.addAll(getEnterprises(treeItem.getChild(i)));
		return enterprises;
	}
}
