package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class JSF extends HTML {
	
	/**
	 * Sets the URL of the HTML to be displayed within the widget.
	 * 
	 * @param url the frame's new URL
	 */
	public void setUrl(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(url));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (Response.SC_OK == response.getStatusCode()) {
						// Process the response in response.getText()
						JSF.this.setHTML(response.getText());
					} else {
						// Handle the error. Can get the status text from
						// response.getStatusText()
					}
				}
				
				

				@Override
				public void onError(Request request, Throwable t) {
					// TODO Auto-generated method stub
					// Couldn't connect to server (could be timeout, SOP
					// violation, etc.)
					Window.alert(t.getLocalizedMessage());
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			// Couldn't connect to server
			Window.alert(e.getLocalizedMessage());
		}
	}

}
