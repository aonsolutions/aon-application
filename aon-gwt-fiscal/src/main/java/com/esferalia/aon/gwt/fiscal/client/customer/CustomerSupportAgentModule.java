package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomerSupportAgentModule extends MainEntryPoint {

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;
	
	private AonToolbar toolbar;
	private AonToolbarButton syncSupportAgent;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel scrollContainer;
	
	// Api
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = false;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RegistryModuleOptions options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final RegistryModuleOptions opt) {
		AON.ensureInjected();
		
		this.options = opt;
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.options.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();
		
		loadModule();
	}

	private void loadModule() {
		dockLayoutPanel.clear();
		
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		container.add(scrollPanel);
		
		scrollContainer = new HTMLPanel("");
		scrollContainer.addStyleName(AON.CSS.aonFlexColumn());
		scrollContainer.getElement().getStyle().setProperty("padding", "0 1rem");
		scrollPanel.add(scrollContainer);
		
		dockLayoutPanel.add(container);
		
		initialMessage();
	}

	private void initialMessage() {
		Label message = new Label("Sincronice los agentes de soporte con sus clientes");
		message.getElement().getStyle().setProperty("text-align", "center");
		message.getElement().getStyle().setProperty("font-size", ".9rem");
		message.getElement().getStyle().setProperty("font-weight", "bold");
		
		scrollContainer.add(message);
	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Agentes Soporte / Clientes");
		
		syncSupportAgent = new AonToolbarButton("Sincronizar Agentes Soporte", AON.CSS.aonIconSync());
		syncSupportAgent.addClickHandler(e -> syncCustomerSupportAgent());
		
		toolbar.add(syncSupportAgent);
	}

	private void syncCustomerSupportAgent() {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando agentes de soporte con dominio del cliente...");
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/customer-support-agent/";
		
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", options.getDomainName());
		headers.put("domain_login", options.getUser());
		headers.put("domain_id", String.valueOf(options.getDomain()));
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", SESSION_API);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		                String responseBody = response.getText();
		                AonMessagePanel.showSuccess(messagePanel, "Sincronizaci\u00f3n finalizada correctamente. Compruebe el log generado.");
		                JSONValue json = JSONParser.parseStrict(responseBody);
		                printSuccessMessages(json.isObject().get("success").isArray());
		                printErrorsMessages(json.isObject().get("error").isArray());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException exception) {
			AonMessagePanel.showError(messagePanel, exception.getMessage());
		}
		
	}

	private void printSuccessMessages(JSONArray successArr) {
		scrollContainer.clear();
		
		Label message = new Label("Sincronizaciones realizadas");
		message.getElement().getStyle().setProperty("font-size", ".9rem");
		message.getElement().getStyle().setProperty("font-weight", "bold");
		
		scrollContainer.add(message);
		
		for(int i=0; i < successArr.size(); i++) {
			HTMLPanel success = new HTMLPanel(successArr.get(i).isObject().get("success").isString().stringValue());
			success.getElement().getStyle().setProperty("padding-left", "1rem");
			scrollContainer.add(success);
		}
	}

	private void printErrorsMessages(JSONArray errorArr) {
		Label message = new Label("Errores encontrados");
		message.getElement().getStyle().setProperty("font-size", ".9rem");
		message.getElement().getStyle().setProperty("font-weight", "bold");
		
		scrollContainer.add(message);
		
		for(int i=0; i < errorArr.size(); i++) {
			HTMLPanel error = new HTMLPanel(errorArr.get(i).isObject().get("error").isString().stringValue());
			error.getElement().getStyle().setProperty("padding-left", "1rem");
			scrollContainer.add(error);
		}
	}
	
}
