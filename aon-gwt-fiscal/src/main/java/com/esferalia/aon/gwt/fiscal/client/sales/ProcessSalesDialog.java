package com.esferalia.aon.gwt.fiscal.client.sales;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.sales.SalesParams;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProcessSalesDialog extends AonCustomDialog {
	
	// UI
	private HTMLPanel body = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel container = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private HTMLPanel contentPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private ScrollPanel scrollPanel;
	private HTMLPanel saleContainer = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomListBox supportSeller;
	private AonCustomListBox feePeriod;
	private AonCustomListBox feeWorkplace;

	// Variables
	private static CommonServiceAsync COMMON_SERVICE;
	
	private SalesParams params;
	private Sales sale;
	
	private Domain parentDomain;
	private List<Seller> supportSellers;
	private List<RegistryAddress> customerAddresses;
	private List<RegistryMedia> customerMedias;
	private List<Workplace> workplaces;
	
	public ProcessSalesDialog( Integer saleId, SalesParams params ) {
		super();
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.sale = new Sales().setId(saleId);
		
		// Style for glass dialog
		getElement().getStyle().setProperty("z-index", "8");
		setGlassStyleName(AON.CSS.aonDialogGlass());
		
		// Caption
		setCaption("Procesar Pedido");
		
		showCloseButton(true);
		
		getSale(saleIt -> {
			getSupportSellers(supportSellersIt -> {
				getCustomerAddresses(customerAddressesIt -> {
					getRegistryMedias(customerMediasIt -> {
						getParentDomain(parentDomainIt -> {
							getWorkplaces(workplacesIt -> {
								initView();
								
								center();
								show();
								
							});
						});
					});
				});
			});
		});
	}
	
	private void initView() {
		body.clear();
		body.addStyleName(AON.CSS.aonFlexColumn());
		body.getElement().getStyle().setProperty("width", "100%");
		body.getElement().getStyle().setProperty("height", "100%");
		body.getElement().getStyle().setProperty("overflow", "auto");
		
		messagePanel.setWidth("100%");
		body.add(messagePanel);
		
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem 1rem 0 1rem");
		
		body.add(container);
		
		contentPanel.getElement().getStyle().setProperty("padding", "0 1rem 1rem 1rem");
		body.add(contentPanel);
		
		initSale();
		
		body.add(createFixButton());
		
		add(body);
	}
	
	private void initSale() {
		if(null != contentPanel) contentPanel.clear();
		
		saleContainer.clear();
		saleContainer.addStyleName(AON.CSS.aonItemFlex());
		saleContainer.addStyleName(AON.CSS.aonFlexColumn());
		saleContainer.setWidth("100%");
		saleContainer.getElement().getStyle().setProperty("max-height", "27.5rem");
		
		AonCustomTextBox document = new AonCustomTextBox("Documento");
		document.setWidth("6rem");
		document.setEnable(false);
		document.setValue(sale.getCustomer().getDocument());
		
		AonCustomTextBox name = new AonCustomTextBox("Nombre");
		name.setEnable(false);
		name.setValue(sale.getCustomer().getName());
		
		AonCustomTextBox registry = new AonCustomTextBox("Registry");
		registry.setWidth("6rem");
		registry.setEnable(false);
		registry.setValue(sale.getCustomer().getId().toString());
		
		saleContainer.add(createRow(document, name, registry));
		
		Optional<RegistryAddress> customerAddress = customerAddresses.stream().filter(address -> address.isMain()).findFirst();
		
		if(customerAddress.isEmpty()) AonMessagePanel.showError(messagePanel, "El cliente no tiene definidad una direcci\u00f3n principal");
		else {
			AonCustomTextBox streetType = new AonCustomTextBox("T. Via");
			streetType.setWidth("4rem");
			streetType.setEnable(false);
			streetType.setValue(customerAddress.get().getStreetType().getAeatCode());
			
			AonCustomTextBox address = new AonCustomTextBox("Direcci\u00f3n");
			address.setEnable(false);
			address.setValue(customerAddress.get().getAddress());
			
			AonCustomTextBox number = new AonCustomTextBox("N\u00ba");
			number.setWidth("4rem");
			number.setEnable(false);
			number.setValue(customerAddress.get().getNumber());
			
			saleContainer.add(createRow(streetType, address, number));
			
			AonCustomTextBox zip = new AonCustomTextBox("C.P.");
			zip.setWidth("4rem");
			zip.setEnable(false);
			zip.setValue(customerAddress.get().getZip());
			
			AonCustomTextBox geozone = new AonCustomTextBox("Provincia");
			geozone.setEnable(false);
			geozone.setValue(customerAddress.get().getGeozoneName());
			
			AonCustomTextBox city = new AonCustomTextBox("Localidad");
			city.setEnable(false);
			city.setValue(customerAddress.get().getCity());
			
			saleContainer.add(createRow(zip, geozone, city));
		}
		
		Optional<RegistryMedia> celularOpt = customerMedias.stream().filter(media -> media.getMedia().equals(MediaType.CELLULAR)).findFirst();
		
		AonCustomTextBox phone = new AonCustomTextBox("Telefono");
		phone.setEnable(false);
		phone.setValue(celularOpt.isPresent() ? celularOpt.get().getValue() : null);
		
		Optional<RegistryMedia> emailOpt = customerMedias.stream().filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		
		if(emailOpt.isEmpty()) AonMessagePanel.showError(messagePanel, "No existe direcci\u00f3n de correo. Por favor rellenala en la ficha del cliente antes de continuar");
		
		AonCustomTextBox email = new AonCustomTextBox("Email");
		email.setEnable(false);
		email.setValue(emailOpt.isPresent() ? emailOpt.get().getValue() : null);
		
		saleContainer.add(createRow(phone, email));
		
		if(null == parentDomain || AonStringUtils.isBlank(parentDomain.getSubDomainSuffix())) AonMessagePanel.showError(messagePanel, "El dominio padre no tiene definido el sufijo para los hijos");
		
		AonCustomTextBox url = new AonCustomTextBox("URL");
		url.setEnable(false);
		url.setValue(sale.getCustomer().getDocument().toLowerCase() + "-" + (null == parentDomain || AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()) ? parentDomain.getName() : parentDomain.getSubDomainSuffix()) );
		
		saleContainer.add(createRow(url, null));
		
		supportSeller = new AonCustomListBox("Agente de soporte (con usuario)");
		supportSeller.clearItems();
		supportSeller.addItem("-", "");
		supportSellers.forEach(seller -> supportSeller.addItem(seller.getName(), seller.getId().toString()));
		
		AonCustomTextBox commercialSeller = new AonCustomTextBox("Agente Comercial");
		commercialSeller.setEnable(false);
		commercialSeller.setValue(sale.getSeller().getName());
		
		saleContainer.add(createRow(supportSeller, commercialSeller));
		
		feePeriod = new AonCustomListBox("Periodo Cuotas");
		feePeriod.clearItems();
		feePeriod.addItem("Sin periodo", "0");
		feePeriod.addItem("Mensual", "1");
		feePeriod.addItem("Bimensual", "2");
		feePeriod.addItem("Trimestral", "3");
		feePeriod.addItem("Cuatrimestral", "4");
		feePeriod.addItem("Semestral", "5");
		feePeriod.addItem("Anual", "6");
		feePeriod.setValue("1");
		
		feeWorkplace = new AonCustomListBox("C. Trabajo");
		feeWorkplace.clearItems();
		feeWorkplace.addItem("-", "");
		workplaces.forEach(w -> feeWorkplace.addItem(w.getDescription(), w.getId().toString()));
		
		saleContainer.add(createRow(feePeriod, feeWorkplace));
		
		scrollPanel = new ScrollPanel(saleContainer);
		scrollPanel.setWidth("100%");
		
		contentPanel.add(scrollPanel);
		
		center();
	}

	private Widget createFixButton() {
		HTMLPanel buttonPanel = new HTMLPanel(AonStringUtils.EMPTY);
		buttonPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonPanel.getElement().getStyle().setProperty("padding", "0 1rem 1rem");
		buttonPanel.getElement().getStyle().setProperty("gap", "1rem");
		
		Button processSaleBtn = new Button();
		processSaleBtn.setStyleName(AON.CSS.aonIconEmit());
		processSaleBtn.addStyleName(AON.CSS.aonButtonIconText());
		processSaleBtn.setText("Procesar Pedido");
		processSaleBtn.addClickHandler(e -> processSale());
		
		buttonPanel.add(processSaleBtn);
		
		return buttonPanel;
	}

	private FlowPanel createRow(Widget widget1, Widget widget2) {
		FlowPanel row = createFlexPanel();
		row.add(widget1);
		if (widget2 != null) {
			row.add(widget2);
		}
		return row;
	}
	
	private FlowPanel createRow(Widget widget1, Widget widget2, Widget widget3) {
		FlowPanel row = createFlexPanel();
		row.add(widget1);
		if (widget2 != null) {
			row.add(widget2);
		}
		if (widget3 != null) {
			row.add(widget3);
		}
		return row;
	}
	
	private FlowPanel createFlexPanel() {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.CSS.aonItemFlex());
		panel.setWidth("100%");
		return panel;
	}
	
	private void processSale() {
		
		// Check required info
		
		Optional<RegistryAddress> customerAddress = customerAddresses.stream().filter(address -> address.isMain()).findFirst();
		Optional<RegistryMedia> emailOpt = customerMedias.stream().filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		Optional<RegistryMedia> phoneOpt = customerMedias.stream().filter(media -> media.getMedia().equals(MediaType.CELLULAR)).findFirst();
		
		if(AonStringUtils.isBlank(sale.getCustomer().getDocument())) AonMessagePanel.showError(messagePanel, "El cliente no tiene documento definido");
		else if(customerAddress.isEmpty()) AonMessagePanel.showError(messagePanel, "El cliente no tiene definidad una direcci\u00f3n principal");
		else if(emailOpt.isEmpty()) AonMessagePanel.showError(messagePanel, "No existe direcci\u00f3n de correo. Por favor rellenala en la ficha del cliente antes de continuar");
		else if(AonStringUtils.isBlank(parentDomain.getSubDomainSuffix())) AonMessagePanel.showError(messagePanel, "El dominio padre no tiene definido el sufijo para los hijos");
		else if(AonStringUtils.isBlank(supportSeller.getValue())) AonMessagePanel.showError(messagePanel, "El agente de soporte es obligatorio");
		else if(AonStringUtils.isBlank(feeWorkplace.getValue())) AonMessagePanel.showError(messagePanel, "El centro de trabajo para la creaci\u00f3n de cuotas es requerido");
		else {
			
			AonMessagePanel.showLoading(messagePanel, "Procesando pedido para generar una empresa..");
			
			String host = Window.Location.getHost();
			String endPoint = "/ms/api/registry-creation-enterprise/";
			
			HashMap<String, String> headers = new HashMap<>();
			headers.put("domain_name", params.getDomainName());
			headers.put("domain_login", params.getUser());
			headers.put("domain_id", String.valueOf(params.getDomain()));
			
			JSONObject body = new JSONObject();
			
			body.put("name", new JSONString(sale.getCustomer().getName()));
			body.put("document", new JSONString(sale.getCustomer().getDocument()));
			
			body.put("streetType", new JSONString(customerAddress.get().getStreetType().getAeatCode()));
			body.put("address", new JSONString(customerAddress.get().getAddress()));
			body.put("number", new JSONString(customerAddress.get().getNumber()));
			body.put("zip", new JSONString(customerAddress.get().getZip()));
			body.put("geozoneCode", new JSONString(customerAddress.get().getGeozoneCode()));
			body.put("city", new JSONString(customerAddress.get().getCity()));
			
			body.put("phone", new JSONString(phoneOpt.isEmpty() ? "" : phoneOpt.get().getValue()));
			body.put("email", new JSONString(emailOpt.get().getValue()));
			
			body.put("registry", new JSONString(sale.getCustomer().getId().toString()));
			
			body.put("sellerSupport", new JSONString(supportSeller.getValue()));
			body.put("sellerCommercial", new JSONString(sale.getSeller().getId().toString()));
			
			body.put("saleId", new JSONString(sale.getId().toString()));
			
			body.put("feePeriod", new JSONString(feePeriod.getValue()));
			body.put("feeWorkplace", new JSONString(feeWorkplace.getValue()));
			
			body.put("source", new JSONString("SALE"));
			
			// Create a URL builder and add query parameters
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
			urlBuilder.setHost(host); 
			urlBuilder.setPath(endPoint);
			
			// Create the request builder with the complete URL
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
//			requestBuilder.setHeader("session_id", AonStringUtils.isBlank(sessionId) ? "AONd95770f269e711eb94390242ac130002" : sessionId);
			
			headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
			
			try {
			    // Send the request
			    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
			        public void onResponseReceived(Request request, Response response) {
			        	JSONValue jsonValue = JSONParser.parseStrict(response.getText());
			        	String message = "";
		        	    if (jsonValue != null && jsonValue.isObject() != null) {
		        	        JSONObject jsonObject = jsonValue.isObject();
		        	        
		        	        JSONValue messageValue = jsonObject.get("message");
		        	        message = null != messageValue ? messageValue.isString().stringValue() : "Error desconocido";
		        	    }
			        	
			        	if(response.getStatusCode() == 400) {
			        		AonMessagePanel.showError(messagePanel, message);
			        	} else {
			        		AonMessagePanel.showSuccess(messagePanel, message);
			        		center();
			        		
			        		Timer timer = new Timer() {
				       		     @Override
				       		     public void run() {
				       		    	hide();
				       		    	onSaleProcess();
				       		     }
				       		};
				       		timer.schedule(2500);
				        	
			        	}
			        }

					public void onError(Request request, Throwable exception) {
						Window.alert(exception.getMessage());
			        }
			    });
			} catch (RequestException exception) {
				Window.alert("Catch : " + exception.getMessage());
			}
			
		}
	}
	
	private void getSale(Consumer<Sales> success) {
		COMMON_SERVICE.getSale(params.getDomainName(), params.getDomain(), params.getUser(), sale.getId(), new AsyncCallback<Sales>() {
			
			@Override
			public void onSuccess(Sales saleDB) {
				sale = saleDB;
				success.accept(sale);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error tarifas: " + caught.getMessage());
			}
		});
	}
	
	private void getSupportSellers(Consumer<List<Seller>> success) {
		COMMON_SERVICE.getTaskHolderUsers(params.getDomainName(), params.getDomain(), params.getUser(), new AsyncCallback<List<Seller>>() {
			
			@Override
			public void onSuccess(List<Seller> supportSellersDb) {
				supportSellers = supportSellersDb;
				success.accept(supportSellers);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error agentes de soporte: " + caught.getMessage());
			}
		});
	}
	
	private void getCustomerAddresses(Consumer<List<RegistryAddress>> success) {
		COMMON_SERVICE.getRegistryAddresses(params.getDomainName(), params.getDomain(), params.getUser(), sale.getCustomer().getId(), new AsyncCallback<List<RegistryAddress>>() {
			
			@Override
			public void onSuccess(List<RegistryAddress> customerAddressesDb) {
				customerAddresses = customerAddressesDb;
				success.accept(customerAddresses);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error direcciones del cliente: " + caught.getMessage());
			}
		});
	}
	
	private void getRegistryMedias(Consumer<List<RegistryMedia>> success) {
		COMMON_SERVICE.getRegistryMedias(params.getDomainName(), params.getDomain(), params.getUser(), sale.getCustomer().getId(), new AsyncCallback<List<RegistryMedia>>() {
			
			@Override
			public void onSuccess(List<RegistryMedia> customerMediasDb) {
				customerMedias = customerMediasDb;
				success.accept(customerMedias);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error direcciones del cliente: " + caught.getMessage());
			}
		});
	}
	
	private void getParentDomain(Consumer<Domain> success) {
		COMMON_SERVICE.getParentDomain(params.getDomainName(), params.getDomain(), params.getUser(), sale.getDomain(), new AsyncCallback<Domain>() {
			
			@Override
			public void onSuccess(Domain parentDomainDb) {
				parentDomain = parentDomainDb;
				success.accept(parentDomain);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error direcciones del cliente: " + caught.getMessage());
			}
		});
	}
	
	private void getWorkplaces(Consumer<List<Workplace>> success) {
		COMMON_SERVICE.getWorkplaces(params.getDomainName(), params.getDomain(), params.getUser(), new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> workplacesDb) {
				workplaces = workplacesDb;
				success.accept(workplaces);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error direcciones del cliente: " + caught.getMessage());
			}
		});
	}
	
	public abstract void onSaleProcess();
	
}
