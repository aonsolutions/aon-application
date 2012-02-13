package com.esferalia.aon.gwt.employee.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Employee implements EntryPoint {

	
	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();
		
		@Source("richCss/images/aon-header/aon-menuBar.png")
		ImageResource menuBar();
	}



	interface Binder extends UiBinder<DockLayoutPanel, Employee> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField Employees employees;
	@UiField Documents documents;
	@UiField EmployeeDetail employeeDetail;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		
		Window.alert("Hello");

		// Inject rich styles.
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();

		// Create the UI defined in Mail.ui.xml.
		DockLayoutPanel outer = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		
		employees.setEmployeeDetail(employeeDetail);
		documents.setEmployeeDetail(employeeDetail);
	}
}
