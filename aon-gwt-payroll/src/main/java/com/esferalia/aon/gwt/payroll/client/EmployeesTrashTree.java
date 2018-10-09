package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class EmployeesTrashTree extends Composite implements KeyDownHandler {

	interface Listener {

		void onEmployeeItemSelected(Employee employee);
		
		void onSuprKeyDown(Employee employee);
	}

	private static EmployeesTrashTreeUiBinder uiBinder = GWT
			.create(EmployeesTrashTreeUiBinder.class);

	interface EmployeesTrashTreeUiBinder extends
			UiBinder<Widget, EmployeesTrashTree> {
	}
	
	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	@UiField
	Tree tree;
	
	private Images images;

	private List<Listener> listeners;
	private DomainEmployeesServiceAsync employeesService;

	public EmployeesTrashTree() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);

		this.listeners = new ArrayList<Listener>();
		
		tree.addKeyDownHandler(this);
		
		employeesService = DomainEmployeesServiceAsync.newInstance();
	}
	
	public DomainEmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}
	
	public void clear() {
		tree.clear();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		Object object = event.getSelectedItem().getUserObject();
		
		if(object instanceof Employee)
			onEmployeeItemSelected((Employee) object);
	}
	
	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		Object object = tree.getSelectedItem().getUserObject();
		
		if(keyCode == KeyCodes.KEY_DELETE && 
				object instanceof Employee) {
			onSuprKeyDown((Employee) object);
		}
		
	}
	
	private void onEmployeeItemSelected(Employee employee) {
		for(Listener listener : listeners)
			listener.onEmployeeItemSelected(employee);
	}
	
	private void onSuprKeyDown(Employee employee) {
		for(Listener listener : listeners)
			listener.onSuprKeyDown(employee);
	}
	
	public void addEmployeeItem(Employee employee) {
		
		StringBuffer text = new StringBuffer(employee.getFullname());
		if (employee.getEndDate() != null) {
			text.append(" (");
			text.append(END_DATE_FORMAT.format(employee.getEndDate()));
			text.append(")");
		}
				
		final TreeItem item = new TreeItem(imageItemHTML(images.oldemployee(), 
				text.toString()));		
		item.setUserObject(employee);		
		tree.addItem(item);		
	}
	
	public void removeEmployeeItem(Employee employee) {
		
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


}
