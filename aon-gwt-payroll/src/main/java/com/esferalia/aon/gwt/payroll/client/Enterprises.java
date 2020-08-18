package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.CretaDetail.isChecked;
import static com.esferalia.aon.gwt.payroll.client.CretaDetail.setCheckedStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Enterprises extends ResizeComposite implements
		SelectionHandler<TreeItem>, 
		OptionsToolbar.Listener,
		ContextMenuHandler {

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

	@UiField
	OptionsToolbar toolbar;
	
	private PopupPanel viewPopupPanel;
	private MenuItem viewErrorCCCsMenuItem; 
	private MenuItem viewSuccessCCCsMenuItem; 
	private MenuItem viewEmployeesCCCsMenuItem; 
	private MenuItem viewNoEmployeesCCCsMenuItem; 

	private Images images;
	private List<Listener> listeners;
	private DomainEnterprisesServiceAsync enterprisesService;

	private boolean inactive = false;

	public Enterprises() {

		images = GWT.create(Images.class);
		listeners = new LinkedList<Enterprises.Listener>();

		enterprisesService = DomainEnterprisesServiceAsync.newInstance();		
				
		initWidget(binder.createAndBindUi(this));
		
		initToolbar();
		

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

	private void initToolbar() {
		toolbar.addListener(this);
		
		toolbar.setVisibleSearchTextBox(false);
		toolbar.setVisibleViewButton(false);
		toolbar.setVisibleNewButton(false);
		toolbar.setVisiblePasteButton(false);
		toolbar.setVisibleDraftButton(false);		
		toolbar.setVisibleCopyButton(false);
		
		Button viewButton = toolbar.getViewButton();
		viewPopupPanel = new PopupPanel() ;
		viewPopupPanel.setAutoHideEnabled(true);
		MenuBar viewMenuBar = new CretaDetail.ViewMenuBar(true);
		
		viewErrorCCCsMenuItem = 
		new MenuItem(new SafeHtmlBuilder().appendEscaped("CCCs Err\u00f3neos").toSafeHtml());
		viewErrorCCCsMenuItem.setScheduledCommand(() -> {
			setCheckedStyle(viewErrorCCCsMenuItem, !isChecked(viewErrorCCCsMenuItem));
			filter();
		});
		viewMenuBar.addItem(viewErrorCCCsMenuItem);		
		viewSuccessCCCsMenuItem = 
		new MenuItem(new SafeHtmlBuilder().appendEscaped("CCCs V\u00e1lidos").toSafeHtml());
		viewSuccessCCCsMenuItem.setScheduledCommand(() -> {
			setCheckedStyle(viewSuccessCCCsMenuItem, !isChecked(viewSuccessCCCsMenuItem));
			filter();
		});
		setCheckedStyle(viewSuccessCCCsMenuItem, true);
		viewMenuBar.addItem(viewSuccessCCCsMenuItem);
		
		viewMenuBar.addSeparator();
		
		viewNoEmployeesCCCsMenuItem = 
		new MenuItem(new SafeHtmlBuilder().appendEscaped("Sin Empleados").toSafeHtml());
		viewNoEmployeesCCCsMenuItem.setScheduledCommand(() -> {
			setCheckedStyle(viewNoEmployeesCCCsMenuItem, !isChecked(viewNoEmployeesCCCsMenuItem));
			filter();
		});
		viewMenuBar.addItem(viewNoEmployeesCCCsMenuItem);
		viewEmployeesCCCsMenuItem = 
		new MenuItem(new SafeHtmlBuilder().appendEscaped("Con Empleados").toSafeHtml());
		viewEmployeesCCCsMenuItem.setScheduledCommand(() -> {
			setCheckedStyle(viewEmployeesCCCsMenuItem, !isChecked(viewEmployeesCCCsMenuItem));
			filter();
		});
		setCheckedStyle(viewEmployeesCCCsMenuItem, true);
		viewMenuBar.addItem(viewEmployeesCCCsMenuItem);
		
		

		viewPopupPanel.add(viewMenuBar);
		
		viewButton.addClickHandler((e) -> {
			viewPopupPanel.setPopupPosition(
					viewButton.getAbsoluteLeft(), 
					viewButton.getAbsoluteTop() + viewButton.getOffsetHeight());
			viewPopupPanel.show();
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
		
//		if ( !hasEmployees(enterprise) ) 
//			return;
		
		final TreeItem enterpriseItem = addImageItem(rootItem,
				enterprise.getName(), images.enterprise());

		enterpriseItem.setUserObject(enterprise);
		
		for (Activity activity: enterprise.getActivities()) {

			TreeItem activityItem = addImageItem(enterpriseItem, 
					activity.getDescription(),
					images.ine());
			activityItem.setUserObject(activity);
			
			for ( CCC ccc : activity.getCccs() ) {
				TreeItem cccItem = addImageItem(activityItem, ccc.getCode(),getImage(ccc)
						);
				cccItem.setUserObject(ccc);
			}
			
		}


		//enterpriseItem.setState(true, true);

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

		filter();	
		toolbar.setVisibleViewButton(true);
		toolbar.setVisibleSearchTextBox(true);

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
	
	// ------------------------------------------------------------------------
	@Override
	public void onNewButtonClick(ClickEvent event){
	}
	
	@Override
	public void onPasteButtonClick(ClickEvent event){
	}
	
	@Override
	public void onCopyButtonClick(ClickEvent event){
	}
	
	@Override
	public void onDraftButtonClick(ClickEvent event){
	}
			
	@Override
	public void onKeyUpSearchTextBox(KeyUpEvent event){
		filter();
	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event){
	}	
	
	// ------------------------------------------------------------------------
	

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
	
	private void filter() {
		filter(toolbar.getSearchTextBox().getValue());
	}

	private void filter(String pattern) {
		for (int i = 0; i < tree.getItemCount(); i++)
			filterEnterprises(pattern, tree.getItem(0));
		
	}


	private void filterEnterprises(String pattern, TreeItem enterprisesItem) {
		
		for ( int i = 0; i < enterprisesItem.getChildCount(); i++ ) {
			TreeItem enterpriseItem = enterprisesItem.getChild(i);
			
			Enterprise enterprise = ( Enterprise ) enterpriseItem.getUserObject();
			String name = enterprise.getName();
			
			boolean visible = 
			filterEnterprise(enterpriseItem)
			&& (AonStringUtils.isBlank(pattern) 
			|| AonStringUtils.containsIgnoreCase(name, pattern)
			|| filterActivities(pattern, enterpriseItem)
			);	
			
			enterpriseItem.setVisible(visible);
			enterpriseItem.setState(visible);
		}
	}
	
	private boolean filterEnterprise(TreeItem enterpriseItem) {
		boolean found = false;
		for ( int i = 0; i < enterpriseItem.getChildCount(); i++ ) {
			TreeItem activityItem = enterpriseItem.getChild(i);
			boolean visible = filterActivity(activityItem);
			activityItem.setVisible(visible);
			activityItem.setState(visible);
			found |= visible;
		}
		return found;
		
	}

	private boolean filterActivity( TreeItem activityItem) {
		
		boolean found = false;
		for ( int i = 0; i < activityItem.getChildCount(); i++ ) {
			TreeItem cccItem = activityItem.getChild(i);
			CCC ccc = ( CCC ) cccItem.getUserObject();
			boolean checkCCC = checkCCC(ccc);
			boolean hasEmployees = hasEmployees(ccc);
			boolean visible = 
					((checkCCC && isChecked(viewSuccessCCCsMenuItem))
					|| ( !checkCCC && isChecked(viewErrorCCCsMenuItem)))
					&& ((hasEmployees && isChecked(viewEmployeesCCCsMenuItem))
					|| ( !hasEmployees && isChecked(viewNoEmployeesCCCsMenuItem)))
					;	
			cccItem.setVisible(visible);
			found |= visible;
		}

		return found;

	}

	private boolean filterActivities(String pattern, TreeItem enterpriseItem) {
		
		if (!AonStringUtils.isNumeric(pattern)) 
			return false;
		
		boolean found = false;
		for ( int i = 0; i < enterpriseItem.getChildCount(); i++ ) {
			TreeItem activityItem = enterpriseItem.getChild(i);
			
			boolean visible = 
			activityItem.isVisible() 
			&& filterCCCs(pattern, activityItem);

			activityItem.setVisible(visible);
			activityItem.setState(visible);
			found |= visible;
		}
		return found;
	}

	private boolean filterCCCs(String pattern, TreeItem activityItem) {
		
		boolean found = false;
		for ( int i = 0; i < activityItem.getChildCount(); i++ ) {
			TreeItem cccItem = activityItem.getChild(i);
			CCC ccc = ( CCC ) cccItem.getUserObject();
			boolean visible = 
			cccItem.isVisible()		
			&& AonStringUtils.containsIgnoreCase(ccc.getCode(), pattern);	
			cccItem.setVisible(visible);
			found |= visible;
		}

		return found;

	}
	
	protected static boolean checkCCC(CCC ccc ) {
		String code = ccc.getCode();
		if ( AonStringUtils.isBlank(code)) {
			return false;
		}
		if ( !AonStringUtils.isNumeric(code) ) {
			return false;
		}
		if ( AonStringUtils.trim(code).length() != 11 ) {
			return false;
		}
		
		int provincia  = Integer.parseInt(AonStringUtils.substring(code, 0, 2));
		int numero = Integer.parseInt(AonStringUtils.substring(code, 2,9));
		int control = Integer.parseInt(AonStringUtils.substring(code, -2));
//		log ( provincia + " = " + (provincia >= 1  && provincia <= 52 ));
//		log(numero + " % " + 97 + " = " + control + ", "+ (numero % 97 == control));
		return provincia >= 1  
				&& provincia <= 52 
//				&& (numero % 97 == control)
				;
	}
		
	protected static boolean hasEmployees(Enterprise enterprise) {
		return enterprise.getActivities().stream().flatMap(a -> a.getCccs().stream()).collect(Collectors.summingInt(ccc -> ccc.getEmployees().size())) > 0 ;
	}
	
	protected static boolean hasEmployees(CCC ccc) {
		return ccc.getEmployees().size() > 0 ;
	}


	private static native void log(String message)  /*-{
		console.log( message );
	}-*/;
	
	private  ImageResource getImage(CCC ccc) {
		if ( !checkCCC(ccc))
			return images.warn();
		else if ( !hasEmployees(ccc) )
			return images.aon_icon_okwarning();
		else 
			return images.segsocial();
	}
	

}
