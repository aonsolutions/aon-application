package com.esferalia.aon.gwt.fiscal.client.target;

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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.target.TargetParams;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProcessTargetEnterpriseDialog extends AonCustomDialog {

	// UI
	private HTMLPanel body = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel container = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);

	private HTMLPanel contentPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private ScrollPanel scrollPanel;
	private HTMLPanel targetContainer = new HTMLPanel(AonStringUtils.EMPTY);

	private AonCustomListBox supportSeller;
	private AonCustomListBox commercialSeller;

	// Variables
	private static CommonServiceAsync COMMON_SERVICE;

	private TargetParams params;
	private TargetFull target;

	private Domain parentDomain;
	private List<Seller> supportSellers;

	// Enterprise customer created
	String enterpriseCustomerId = null;
	
	public ProcessTargetEnterpriseDialog(TargetFull target, List<RegistrySeller> targetSellers, TargetParams params) {
		super();

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.params = params;
		this.target = target;

		// Caption
		setCaption("Crear Empresa (C. Potencial)");

		getElement().getStyle().setProperty("min-width", "35rem");

		showCloseButton(true);

		getSupportSellers(supportSellersIt -> {
			getParentDomain(parentDomainIt -> {
				checkCustomer(customer -> {
					// Customer do not exist
					if(!(null != customer && null != customer.getId())) {
						initView();

						center();
						show();
					}
				});
			});
		});
	}

	private void initView() {
		body.clear();
		body.addStyleName(AON.CSS.aonFlexColumn2());
		body.getElement().getStyle().setProperty("width", "100%");
		body.getElement().getStyle().setProperty("height", "100%");
		body.getElement().getStyle().setProperty("overflow", "auto");

		messagePanel.setWidth("100%");
		body.add(messagePanel);

		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem 1rem 0 1rem");

		body.add(container);

		contentPanel.getElement().getStyle().setProperty("padding", "0 1rem 1rem 1rem");
		body.add(contentPanel);

		initSale();

		body.add(createFixButton());

		add(body);
	}

	private void initSale() {
		if (null != contentPanel)
			contentPanel.clear();

		targetContainer.clear();
		targetContainer.addStyleName(AON.CSS.aonItemFlex());
		targetContainer.addStyleName(AON.CSS.aonFlexColumn2());
		targetContainer.setWidth("100%");
		targetContainer.getElement().getStyle().setProperty("max-height", "27.5rem");

		AonCustomTextBox document = new AonCustomTextBox("Documento");
		document.setWidth("6rem");
		document.setEnable(false);
		document.setValue(target.getRegistry().getDocument());

		AonCustomTextBox name = new AonCustomTextBox("Nombre");
		name.setEnable(false);
		name.setValue(target.getRegistry().getName());

		AonCustomTextBox registry = new AonCustomTextBox("Registry");
		registry.setWidth("6rem");
		registry.setEnable(false);
		registry.setValue(target.getId().toString());

		targetContainer.add(createRow(document, name, registry));

		Optional<RegistryAddress> customerAddress = target.getAddresses().stream().filter(address -> address.isMain())
				.findFirst();

		if (customerAddress.isEmpty())
			AonMessagePanel.showError(messagePanel,
					"El cliente potencial no tiene definidad una direcci\u00f3n principal");
		else {
			AonCustomTextBox streetType = new AonCustomTextBox("T. Via");
			streetType.setWidth("4rem");
			streetType.setEnable(false);
			streetType.setValue(null == customerAddress.get().getStreetType() ? "" : customerAddress.get().getStreetType().getAeatCode());

			AonCustomTextBox address = new AonCustomTextBox("Direcci\u00f3n");
			address.setEnable(false);
			address.setValue(customerAddress.get().getAddress());

			AonCustomTextBox number = new AonCustomTextBox("N\u00ba");
			number.setWidth("4rem");
			number.setEnable(false);
			number.setValue(customerAddress.get().getNumber());

			targetContainer.add(createRow(streetType, address, number));

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

			targetContainer.add(createRow(zip, geozone, city));
		}

		Optional<RegistryMedia> celularOpt = target.getMedias().stream()
				.filter(media -> media.getMedia().equals(MediaType.CELLULAR)).findFirst();

		AonCustomTextBox phone = new AonCustomTextBox("Telefono");
		phone.setEnable(false);
		phone.setValue(celularOpt.isPresent() ? celularOpt.get().getValue() : null);

		Optional<RegistryMedia> emailOpt = target.getMedias().stream()
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();

		if (emailOpt.isEmpty())
			AonMessagePanel.showError(messagePanel,
					"No existe direcci\u00f3n de correo. Por favor rellenela en la ficha del cliente potencial antes de continuar");

		AonCustomTextBox email = new AonCustomTextBox("Email");
		email.setEnable(false);
		email.setValue(emailOpt.isPresent() ? emailOpt.get().getValue() : null);

		targetContainer.add(createRow(phone, email));

		if (null == parentDomain || AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()))
			AonMessagePanel.showError(messagePanel, "El dominio padre no tiene definido el sufijo para los hijos");

		AonCustomTextBox url = new AonCustomTextBox("URL");
		url.setEnable(false);
		url.setValue(target.getRegistry().getDocument().toLowerCase() + "-"
				+ (null == parentDomain || AonStringUtils.isBlank(parentDomain.getSubDomainSuffix())
						? parentDomain.getName()
						: parentDomain.getSubDomainSuffix()));

		targetContainer.add(createRow(url, null));

		supportSeller = new AonCustomListBox("Agente de soporte (con usuario en Entorno Padre)");
		supportSeller.clearItems();
		supportSeller.addItem("-", "");
		supportSellers.forEach(seller -> supportSeller.addItem(seller.getName(), seller.getId().toString()));

		targetContainer.add(createRow(supportSeller, null));
		
		commercialSeller = new AonCustomListBox("Agente comercial (con usuario en Entorno Padre)");
		commercialSeller.clearItems();
		commercialSeller.addItem("-", "");
		supportSellers.forEach(seller -> commercialSeller.addItem(seller.getName(), seller.getId().toString()));
		
		if(null != target.getRegistry().getCreationUser()) {
			getSellerByUserLogin(seller -> {
				if(null != seller && null != seller.getId()) commercialSeller.setValue(seller.getId().toString());
			});
		}

		targetContainer.add(createRow(commercialSeller, null));

		scrollPanel = new ScrollPanel(targetContainer);
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
		processSaleBtn.setStyleName(AON.CSS.aonIconDomainAdd());
		processSaleBtn.addStyleName(AON.CSS.aonButtonIconText());
		processSaleBtn.setText("Crear Empresa");
		processSaleBtn.getElement().getStyle().setProperty("padding-left", "1.5rem");
		processSaleBtn.getElement().getStyle().setProperty("background-position-x", "3px");
		processSaleBtn.addClickHandler(e -> createEnterprise());

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

	private void createEnterprise() {

		// Check required info

		Optional<RegistryAddress> targetAddress = target.getAddresses().stream().filter(address -> address.isMain())
				.findFirst();
		Optional<RegistryMedia> emailOpt = target.getMedias().stream()
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
		Optional<RegistryMedia> phoneOpt = target.getMedias().stream()
				.filter(media -> media.getMedia().equals(MediaType.CELLULAR)).findFirst();

		if (AonStringUtils.isBlank(target.getRegistry().getDocument()))
			AonMessagePanel.showError(messagePanel, "El cliente potencial no tiene documento definido");
		else if (targetAddress.isEmpty())
			AonMessagePanel.showError(messagePanel,
					"El cliente potencial no tiene definidad una direcci\u00f3n principal");
		else if (emailOpt.isEmpty())
			AonMessagePanel.showError(messagePanel,
					"No existe direcci\u00f3n de correo. Por favor rellenala en la ficha del cliente potencial antes de continuar");
		else if (AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()))
			AonMessagePanel.showError(messagePanel, "El dominio padre no tiene definido el sufijo para los hijos");
		else if (AonStringUtils.isBlank(supportSeller.getValue()))
			AonMessagePanel.showError(messagePanel, "El agente de soporte es obligatorio");
		else {

			AonMessagePanel.showLoading(messagePanel, "Generando una empresa para el cliente potencial..");

			String host = Window.Location.getHost();
			String endPoint = "/ms/api/registry-creation-enterprise/";

			HashMap<String, String> headers = new HashMap<>();
			headers.put("domain_name", params.getDomainName());
			headers.put("domain_login", params.getUser());
			headers.put("domain_id", String.valueOf(params.getDomain()));

			JSONObject body = new JSONObject();

			body.put("name", new JSONString(target.getRegistry().getName()));
			body.put("document", new JSONString(target.getRegistry().getDocument()));

			body.put("streetType", new JSONString(null == targetAddress.get().getStreetType() ? "" : targetAddress.get().getStreetType().getAeatCode()));
			body.put("address", new JSONString(targetAddress.get().getAddress()));
			body.put("number", new JSONString(targetAddress.get().getNumber()));
			body.put("zip", new JSONString(targetAddress.get().getZip()));
			body.put("geozoneCode", new JSONString(targetAddress.get().getGeozoneCode()));
			body.put("city", new JSONString(targetAddress.get().getCity()));

			body.put("phone", new JSONString(phoneOpt.isEmpty() ? "" : phoneOpt.get().getValue()));
			body.put("email", new JSONString(emailOpt.get().getValue()));

			body.put("registry", new JSONString(target.getId().toString()));

			body.put("sellerSupport", new JSONString(supportSeller.getValue()));
			body.put("sellerCommercial", new JSONString(commercialSeller.getValue()));

			body.put("source", new JSONString("TARGET"));

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
							message = null != messageValue ? messageValue.isString().stringValue()
									: "Error desconocido";

							JSONValue customerValue = jsonObject.get("customer");
							enterpriseCustomerId = null != customerValue ? customerValue.isString().stringValue()
									: null;
						}

						if (response.getStatusCode() == 400) {
							AonMessagePanel.showError(messagePanel, message);
						} else {
							AonMessagePanel.showSuccess(messagePanel, message);
							center();

							Timer timer = new Timer() {
								@Override
								public void run() {
									hide();
									onSaleProcess(enterpriseCustomerId);
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

	private void getSupportSellers(Consumer<List<Seller>> success) {
		COMMON_SERVICE.getTaskHolderUsers(params.getDomainName(), params.getDomain(), params.getUser(),
				new AsyncCallback<List<Seller>>() {

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

	private void getParentDomain(Consumer<Domain> success) {
		COMMON_SERVICE.getParentDomain(params.getDomainName(), params.getDomain(), params.getUser(), target.getDomain(),
				new AsyncCallback<Domain>() {

					@Override
					public void onSuccess(Domain parentDomainDb) {
						parentDomain = parentDomainDb;
						success.accept(parentDomain);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel,
								"Error direcciones del cliente: " + caught.getMessage());
					}
				});
	}
	
	// Check if exist a current user by document for duplicate target
	private String htmlMessage = null;
	
	private void checkCustomer(Consumer<Customer> success) {
		COMMON_SERVICE.getCustomerByDocument(params.getDomainName(), params.getDomain(), params.getUser(), target.getRegistry().getDocument(),
				new AsyncCallback<Customer>() {

			@Override
			public void onSuccess(Customer customer) {
				if(null != customer && null != customer.getId()) {
					htmlMessage = "Existe un cliente, asociado a otro cliente potencial, con este documento.";
					
					COMMON_SERVICE.getCompanyByDocument(params.getDomainName(), params.getDomain(), params.getUser(), target.getRegistry().getDocument(),
							new AsyncCallback<Company>() {

								@Override
								public void onFailure(Throwable arg0) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void onSuccess(Company company) {
									htmlMessage += (null == company || null == company.getId())
											? "<br>No se ha encontrado una empresa con el documento " + target.getRegistry().getDocument()
											: "<br>Se ha encontado una empresa con el documento " + company.getDocument() + " (" + company.getDomain().getName() + ")";
								
									AonDialog dialog = new AonDialog("Cliente existente", new HTML(htmlMessage));
									dialog.info();
									
									success.accept(customer);
									
								}
								
					});
					
//					COMMON_SERVICE.getTargetFull(params.getDomainName(), params.getDomain(), params.getUser(), customer.getId(),
//							new AsyncCallback<TargetFull>() {
//
//						@Override
//						public void onSuccess(TargetFull targetFullDb) {
//							target = targetFullDb;
//							success.accept(customer);
//						}
//
//						@Override
//						public void onFailure(Throwable caught) {
//							AonMessagePanel.showError(messagePanel,
//									"Cliente existente, error targetFull: " + caught.getMessage());
//						}
//					});
					
				} else
					success.accept(customer);
			}

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel,
						"Error direcciones del cliente: " + caught.getMessage());
			}
		});
	}

	private void getSellerByUserLogin(Consumer<Seller> success) {
		COMMON_SERVICE.getSellerByUserLogin(params.getDomainName(), params.getDomain(), params.getUser(), target.getId(), new AsyncCallback<Seller>() {

			@Override
			public void onFailure(Throwable arg0) {
				success.accept(null);
			}

			@Override
			public void onSuccess(Seller seller) {
				success.accept(seller);
			}}
		);
	}
	
	public static native void onSaleProcess(String data) /*-{
		$wnd.top.postMessage(
		  { type: "CUSTOMER_ENTERPRISE_DONE", payload: data },
		  "*"
		);

		//window.parent.postMessage({ type: "CUSTOMER_ENTERPRISE_DONE", payload: data }, "*");
	}-*/;

	public abstract void onSaleProcess();

}
