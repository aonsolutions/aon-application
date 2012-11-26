package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.dom.client.Style.Unit.PX;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

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

	interface Binder extends UiBinder<Widget, Employee> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField Employees employees;
	@UiField EmployeeDetail employeeDetail;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		
		//Window.alert("This method constructs the application user interface by instantiating controls and hooking up event handler.");
		
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		// Window.enableScrolling(false);
		// Window.setMargin("0px");

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		// RootPanel root = RootPanel.get("rootPanel");
		root.add(ui);
		

		employees.setEmployeeDetail(employeeDetail);
	}

	/**
	 * Fetches a parameter passed to the module's nocache script.
	 * 
	 * @param moduleName
	 *            the module's name.
	 * @param parameterName
	 *            the name of the parameter to fetch.
	 * @return the value of the parameter, or <code>null</code> if it was not
	 *         found.
	 */
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for ( var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var parameters = scripts[i].src.match(/\w+=\w+/g);
				for ( var j = 0; j < parameters.length; ++j) {
					var keyvalue = parameters[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

	public static class RootLayoutPanel extends LayoutPanel {

		public static RootLayoutPanel get(String id) {
			RootLayoutPanel rootLayoutPanel = new RootLayoutPanel();
			RootPanel.get(id).add(rootLayoutPanel);
			return rootLayoutPanel;
		}

		private RootLayoutPanel() {
			Window.addResizeHandler(new ResizeHandler() {
				public void onResize(ResizeEvent event) {
					RootLayoutPanel.this.onResize();
				}
			});
		}

		@Override
		protected void onLoad() {
			super.onLoad();
			fillParent();
		}

		private void fillParent() {
			
			Element elem = getElement();
			Element parent = elem.getParentElement();
			
			int top = parent.getOffsetTop();
			int left = parent.getOffsetLeft();
			
			Style style = elem.getStyle();
			style.setPosition(Position.ABSOLUTE);
			style.setLeft(left, PX);
			style.setTop(top, PX);
			style.setRight(0, PX);
			style.setBottom(0, PX);
		}

	}

}
