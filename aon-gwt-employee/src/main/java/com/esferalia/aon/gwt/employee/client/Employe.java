package com.esferalia.aon.gwt.employee.client;

import com.esferalia.aon.gwt.employee.shared.Salary;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Employe implements EntryPoint, Salaries.Handler {

	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();

		@Source("richCss/images/aon-header/aon-menuBar.png")
		ImageResource menuBar();
	}

	interface Binder extends UiBinder<DockLayoutPanel, Employe> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Salaries salaries;
	@UiField
	EmployeeDetail employeDetail;

	private EmployeesServiceAsync employeesService;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
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

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);

		salaries.addHandler(this);
	}

	@Override
	public void onSalarySelected(Salary salary) {
		
		employeDetail.getSalaryReceipt().setReports(new SalaryReportsModel(salary));

	}

	private class SalaryReportsModel extends AbstractReportsModel<IReport>
			implements IReport {

		private Salary salary;

		public SalaryReportsModel(Salary salary) {
			this.salary = salary;
			first();
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public IReport current() {
			return this;
		}
		
		@Override
		public void print() {
			// TODO Hey here is someting that need be implemented.
		}
		
		@Override
		public void download() {
			// TODO Hey here is someting that need be implemented.
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			employeesService.getSalaryReceiptHTML(salary, zoom, callback);
		}
	}

}
