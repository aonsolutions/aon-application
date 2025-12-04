package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.customer.CustomerSyncLog;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class MassiveCustomerDomainSyncPanel extends HTMLPanel {
	private static CommonServiceAsync COMMON_SERVICE;
	private static final Logger LOGGER = Logger.getLogger(MassiveCustomerDomainSyncPanel.class.getName());
	
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}
	
	private HTMLPanel messagePanel = new HTMLPanel("");
	private SimplePanel centerPanel;
	private CustomersLinkedParams params;
	private CustomerApi customerApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	
	// Componentes para el log
	private VerticalPanel logPanel;
	private ScrollPanel scrollPanel;
	private Button copyButton;
	private StringBuilder logContent;
	private List<CustomerSyncLog> logEntries;
	private int currentIndex = 0;
	private List<Customer> customersList;
	
	public MassiveCustomerDomainSyncPanel(CustomersLinkedParams params) {
		super(AonStringUtils.EMPTY);
		
		this.customerApi = new CustomerApi(SESSION_API);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "0 1rem");
		
		this.params = params;
		this.logContent = new StringBuilder();
		this.logEntries = new ArrayList<>();
		
		add(messagePanel);
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		// Configurar el panel de log
		setupLogPanel();
		
		add(centerPanel);
		
		syncMassive();
	}
	
	private void setupLogPanel() {
		VerticalPanel container = new VerticalPanel();
		container.setWidth("100%");
		
		copyButton = new Button("Copiar Log");
		copyButton.setVisible(false);
		copyButton.addClickHandler(event -> copyLogToClipboard());
		copyButton.getElement().getStyle().setProperty("marginBottom", "10px");
		container.add(copyButton);
		
		logPanel = new VerticalPanel();
		logPanel.setWidth("100%");
		logPanel.getElement().getStyle().setProperty("backgroundColor", "#eee");
		logPanel.getElement().getStyle().setProperty("padding", "10px");
		logPanel.getElement().getStyle().setProperty("fontFamily", "monospace");
		logPanel.getElement().getStyle().setProperty("fontSize", "12px");
		
		scrollPanel = new ScrollPanel(logPanel);
		scrollPanel.setHeight("400px");
		scrollPanel.setWidth("100%");
		
		container.add(scrollPanel);
		centerPanel.setWidget(container);
	}
	
	public void syncMassive() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo clientes sin vinculación...");
		
		addLog("=== Iniciando sincronización masiva ===");
		
		params.setSig(false);
		params.setOffset(0);
		params.setLimit(Integer.MAX_VALUE);
		
		COMMON_SERVICE.getCustomersNotLinked(params, new AsyncCallback<List<Customer>>() {
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo clientes : " + caught.getMessage());
				addLog("ERROR: " + caught.getMessage());
				copyButton.setVisible(true);
			}
			
			@Override
			public void onSuccess(List<Customer> customers) {
				customersList = new ArrayList<>(customers);
				AonMessagePanel.showSuccess(messagePanel, "Se encontraron " + customersList.size() + " clientes para sincronizar");
				addLog("Total de clientes a sincronizar: " + customersList.size());
				addLog("-----------------------------------");
				
				// Iniciar la sincronización secuencial
				currentIndex = 0;
				syncNextCustomer();
			}
		});
	}
	
	private void syncNextCustomer() {
		if (currentIndex >= customersList.size()) {
			// Finalización
			addLog("-----------------------------------");
			addLog("=== Sincronización completada ===");
			AonMessagePanel.showSuccess(messagePanel, "Sincronización completada exitosamente");
			copyButton.setVisible(true);
			onEndSuccessSync();
			return;
		}
		
		Customer customer = customersList.get(currentIndex);
		int customerNumber = currentIndex + 1;
		
		addLog("\n[" + customerNumber + "/" + customersList.size() + "] Sincronizando cliente ID: " + customer.getId());
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/registry-creation-enterprise/syncMassiveCustomerDomain";
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONString(customer.getId().toString()));
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", params.getDomainName());
		headers.put("domain_id", params.getDomainId().toString());
		headers.put("domain_login", params.getUser());
		
		customerApi.syncCustomerDomain(host, endPoint, headers, body, new AsyncCallback<CustomerSyncLog>() {
			
			@Override
			public void onSuccess(CustomerSyncLog result) {
				logEntries.add(result);
				
				String messageType = result.getMessageType();
				String messageValue = result.getMessage();
				
//				Window.alert("type" + messageType);
				
				switch (messageType) {
					case "error":
						addLog("  ✗ ERROR: " + messageValue);
						break;
					case "warning":
						addLog("  - WARNING: " + messageValue);
						break;
					case "success":
						addLog("  ✓ ÉXITO: " + messageValue);
						break;
					default:
						addLog("  ✓ ERROR NO TIPIFICADO: " + messageValue);
				}
				
				// Continuar con el siguiente cliente
				currentIndex++;
				syncNextCustomer();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				String errorMsg = caught.getMessage();
				addLog("  ✗ ERROR: " + errorMsg);
				
				// Continuar con el siguiente cliente incluso si hay error
				currentIndex++;
				syncNextCustomer();
			}
		});
	}
	
	private void addLog(String message) {
		HTML logEntry = new HTML(escapeHtml(message));
		logEntry.getElement().getStyle().setProperty("whiteSpace", "pre-wrap");
		logPanel.add(logEntry);
		
		logContent.append(message).append("\n");
		
		// Auto-scroll hacia abajo
		scrollPanel.scrollToBottom();
		
		LOGGER.info(message);
	}
	
	private String escapeHtml(String text) {
		return text.replace("&", "&amp;")
				   .replace("<", "&lt;")
				   .replace(">", "&gt;")
				   .replace("\"", "&quot;")
				   .replace("'", "&#39;");
	}
	
	private void copyLogToClipboard() {
		// Generar CSV con formato de pipes
		StringBuilder csv = new StringBuilder();
		
		// Cabecera
		csv.append("ID Cliente | Nombre Cliente | Documento Cliente | ID Empresa | Nombre Empresa | Documento Empresa | Tipo Mensaje | Mensaje\n");
		
		// Datos
		for (CustomerSyncLog entry : logEntries) {
			csv.append(entry.getCustomerId()).append(" | ")
			   .append(entry.getCustomerName()).append(" | ")
			   .append(entry.getCustomerDocument()).append(" | ")
			   .append(entry.getEnterpriseId()).append(" | ")
			   .append(entry.getEnterpriseName()).append(" | ")
			   .append(entry.getEnterpriseDocument()).append(" | ")
			   .append(entry.getMessageType()).append(" | ")
			   .append(entry.getMessage()).append("\n");
		}
		
		boolean success = copyToClipboardNative(csv.toString());
		if (success)
			AonMessagePanel.showSuccess(messagePanel, "Log copiado al portapapeles en formato CSV (separado por |)");
		else
			AonMessagePanel.showError(messagePanel, "No se pudo copiar el log. Por favor, selecciona y copia manualmente.");
	}

	private native boolean copyToClipboardNative(String text) /*-{
		try {
			var textArea = $doc.createElement('textarea');
			textArea.value = text;
			textArea.style.position = 'fixed';
			textArea.style.top = '0';
			textArea.style.left = '0';
			textArea.style.opacity = '0';
			$doc.body.appendChild(textArea);
			textArea.focus();
			textArea.select();
			
			var successful = $doc.execCommand('copy');
			$doc.body.removeChild(textArea);
			
			return successful;
		} catch (err) {
			console.error('Error al copiar:', err);
			return false;
		}
	}-*/;
	
	protected abstract void onEndSuccessSync();
}