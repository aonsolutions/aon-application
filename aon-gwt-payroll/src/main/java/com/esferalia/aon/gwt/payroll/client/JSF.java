package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class JSF extends HTML {

	/**
	 * The glass element.
	 */
	private Element glass;

	/**
	 * A boolean indicating that a glass element should be used.
	 */
	private boolean isGlassEnabled;

	private String glassStyleName = "gwt-PopupPanelGlass";

	public JSF() {
		super();
	}

	/**
	 * Sets the URL of the HTML to be displayed within the widget.
	 * 
	 * @param url
	 *            the frame's new URL
	 */
	public void setUrl(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(url));
		try {
			AON.start();
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (Response.SC_OK == response.getStatusCode()) {
						// Process the response in response.getText()

						String html = response.getText();
						JSF.this.setHTML(html);
						AON.stop();
					} else {
						// Handle the error. Can get the status text from
						// response.getStatusText()
						AON.fail();
					}
				}

				@Override
				public void onError(Request request, Throwable t) {
					// TODO Auto-generated method stub
					// Couldn't connect to server (could be timeout, SOP
					// violation, etc.)
					AON.fail();
					Window.alert(t.getLocalizedMessage());
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			// Couldn't connect to server
			Window.alert(e.getLocalizedMessage());
		}
	}

	public void setGlassEnabled(boolean enabled) {
		this.isGlassEnabled = enabled;
		if (enabled && glass == null) {
			glass = Document.get().createDivElement();
			glass.setClassName(glassStyleName);

			glass.getStyle().setPosition(Position.ABSOLUTE);
			glass.getStyle().setLeft(0, Unit.PX);
			glass.getStyle().setTop(0, Unit.PX);
			glass.getStyle().setProperty("visibility",
					true ? "visible" : "hidden");

		}
	}

}
