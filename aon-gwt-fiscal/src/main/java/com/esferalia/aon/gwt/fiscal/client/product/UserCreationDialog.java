package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class UserCreationDialog extends AonCustomDialog {

	private static CommonServiceAsync COMMON_SERVICE;

	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private HTMLPanel generalInfoContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox surname = new AonCustomTextBox("Apellidos");
	private AonCustomTextBox email = new AonCustomTextBox("Email");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	
	private HTMLPanel buttonsContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private Button acceptButton;
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer officeDomainId;
	
	private ProductBooking product;
	private Integer customerRelatedRegistry;
	
	// Api
	private BookingApi bookingApi;
	private String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = true;

	public UserCreationDialog(String domainName, int domainId, String user, int officeDomainId, Integer customerRelatedRegistry, ProductBooking product) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		this.officeDomainId = officeDomainId;
		this.product = product;
		this.customerRelatedRegistry = customerRelatedRegistry;
		
		this.bookingApi = new BookingApi(this.SESSION_API);
		
		setCaption("Nuevo Usuario");
		showCloseButton(true);
		
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		content.setWidth("32rem");
		
		content.add(messagePanel);
		
		initGeneralInfo();
		initButtons();
		
		setWidget(content);
		center();
		show();
		
		getCloseButton().addClickHandler(e -> onCancel());
	}

	private void initGeneralInfo() {
		generalInfoContent.addStyleName(AON.CSS.aonFlexColumn());
		generalInfoContent.getElement().getStyle().setProperty("padding", "0 1rem");
		
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		row.add(name);
		row.add(surname);
		
		HTMLPanel row2 = new HTMLPanel(AonStringUtils.EMPTY);
		row2.addStyleName(AON.CSS.aonItemFlex());
		row2.add(email);
		row2.add(document);
		
		generalInfoContent.add(row);
		generalInfoContent.add(row2);
		
		content.add(generalInfoContent);
		
	}

	private void initButtons() {
		buttonsContent.addStyleName(AON.CSS.aonItemFlex());
		buttonsContent.addStyleName(AON.CSS.aonDisplayFlexCenter());
		buttonsContent.setWidth("100%");
		buttonsContent.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText("Confirmar");
		acceptButton.addClickHandler(e -> {
			// Avisar y actualizar datos
			String errorMessage = emptyInfo();
			if(AonStringUtils.isNotBlank(errorMessage)) AonMessagePanel.showError(messagePanel, errorMessage);
			else warnDialog();
		});
		
		buttonsContent.add(acceptButton);
		
		content.add(buttonsContent);
	}
	
	private String emptyInfo() {
		if(AonStringUtils.isBlank(name.getValue())) return "El campo nombre es obligatorio";
		if(AonStringUtils.isBlank(email.getValue())) return "El campo email es obligatorio";
		if(AonStringUtils.isBlank(document.getValue())) return "El campo documento es obligatorio";
		
		return null;
	}
	
	private void warnDialog() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		
		HTMLPanel dialogContent = new HTMLPanel("");
		dialogContent.addStyleName(AON.CSS.aonFlexColumn());
		dialogContent.getElement().getStyle().setProperty("padding", "1rem");
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
		buttonsPanel.setWidth("100%");
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButton());
		acceptBtnDialog.setText("Continuar");
		buttonsPanel.add(acceptBtnDialog);
		
		HTMLPanel messagePanel = new HTMLPanel("");
		messagePanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton info = new AonTableButton("Creaci\u00f3n Usuario", AON.CSS.aonIconInfo());
		
		String messageContent = "Se proceder\u00e1 a contratar y crear un usuario nuevo.";
		
		HTMLPanel message = new HTMLPanel(messageContent);
		
		messagePanel.add(info);
		messagePanel.add(message);
		dialogContent.add(messagePanel);
		
		acceptBtnDialog.addClickHandler(ev -> {
			dialog.hide();
			createUser(end -> {
				hide();
				onEnd();
			});
		});
		
		dialogContent.add(buttonsPanel);
		
		dialog.setCaption("Creaci\u00f3n Usuario");
		dialog.add(dialogContent);
		dialog.center();
		dialog.show();
	}
	
	private void createUser(Consumer<Void> end) {
		
		AonMessagePanel.showLoading(messagePanel, "Creando nuevo usuario en el dominio...");
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "ms/api/user/bookingUser";
		
		JSONObject body = new JSONObject();
		body.put("domainName", new JSONString(domainName));
		body.put("domainId", new JSONString(domainId.toString()));
		body.put("user", new JSONString(user));
		body.put("name", new JSONString(name.getValue()));
		body.put("surname", new JSONString(surname.getValue()));
		body.put("document", new JSONString(document.getValue()));
		body.put("email", new JSONString(email.getValue()));
		
		// TODO: confirmar esta informacion
		body.put("portal", new JSONString(Boolean.TRUE.toString()));
		body.put("shared", new JSONString(Boolean.FALSE.toString()));
		
		JSONArray roles = new JSONArray();
		roles.set(0, new JSONString("ENTERPRISE"));
		body.put("roles", roles);
		
		createFee(product, finish -> {
			
			bookingApi.syncSupportAgentCustomer(host, endPoint, body, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void success) {
					AonMessagePanel.showSuccess(messagePanel, "Usuario creado correctamente.");
	            	Timer timer = new Timer() {
	           		     @Override
	           		     public void run() {
				            	end.accept(null);
	           		     }
	           		};
	           		timer.schedule(1500);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AonDialog errorDialog = new AonDialog("Error creando usuario", new HTMLPanel(caught.getMessage()));
					errorDialog.info();
				}
			});
			
		});
	}
	
	private void createFee(ProductBooking product, Consumer<Void> end) {
		Fee newFee = new Fee()
				.setDomain(new Domain().setId(officeDomainId))
				.setProject(new Project())
				.setItem(new OldItem().setId(product.getItem().getId()).setBarcode(product.getItem().getBarcode()))
				.setDescription(product.getName())
				.setQuantity(1.00)
				.setPrice(product.getItem().getPrice())
				.setStartDate(new Date())
				.setBillingDate(DateUtils.getLastDayOfMonth())
				.setPeriod(BillingPeriod.MONTHLY)
				;
		
		COMMON_SERVICE.createBookingProduct(domainName, domainId, user, customerRelatedRegistry, product, newFee, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error contrataci\u00f3n: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void arg0) {
				end.accept(arg0);
			}
		});
	}

	protected abstract void onEnd();
	protected abstract void onCancel();

}
