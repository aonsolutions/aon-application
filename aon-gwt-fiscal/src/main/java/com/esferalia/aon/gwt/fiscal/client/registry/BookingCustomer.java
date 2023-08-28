package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class BookingCustomer extends HTMLPanel {
	
	private HTMLPanel container;
	private HTMLPanel customerPanel;
	private HTMLPanel domainsPanel;
	
	private Customer customer;
	private List<DomainCompany> customerDomains;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public BookingCustomer() {
		super("");
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		
		customerPanel = new HTMLPanel("");
		customerPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainsPanel = new HTMLPanel("");
		domainsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(customerPanel);
		container.add(domainsPanel);
		
		this.add(container);
	}
	
	public void setBookingCustomer(Customer customer, List<DomainCompany> customerDomains) {
		this.customer = customer;
		this.customerDomains = customerDomains;
		
		this.customerPanel.clear();
		this.domainsPanel.clear();
		
		initializeCustomer();
		initializeDomains();
	}

	private void initializeCustomer() {
		HTMLPanel namePanel = new HTMLPanel("");
		namePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label descriptionLabel = new Label("Descripci\u00f3n");
		customerLabelStyle(descriptionLabel);
		
		Label description = new Label(this.customer.getName() + " (" + this.customer.getDocument() + ")");
		
		namePanel.add(descriptionLabel);
		namePanel.add(description);
		customerPanel.add(namePanel);
		
		HTMLPanel domainPanel = new HTMLPanel("");
		domainPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label domainLabel = new Label("Dominio");
		customerLabelStyle(domainLabel);
		
		Label domain = new Label(this.customer.getDomain().getName()  + " (Cod.: " + this.customer.getId().toString() + ")");
		
		domainPanel.add(domainLabel);
		domainPanel.add(domain);
		customerPanel.add(domainPanel);
		
		HTMLPanel infoPanel = new HTMLPanel("");
		infoPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label infoLabel = new Label("Estado");
		customerLabelStyle(infoLabel);
		
		Label info = new Label(getCustomerStatus(this.customer.getStatus()) + " (" + (this.customer.isBillable() ? "Facturable" : "No Facturable") + ")");
		
		infoPanel.add(infoLabel);
		infoPanel.add(info);
		customerPanel.add(infoPanel);
	}
	
	private String getCustomerStatus(RegistryStatus registryStatus) {
		switch (registryStatus) {
			case ACTIVE:
				return "Activo";
			case INACTIVE:
				return "Inactivo";
			default:
				return "Bloqueado";
		}
	}

	private void initializeDomains() {
		Label domainsLabel = new Label("Dominios");
		customerLabelStyle(domainsLabel);
		
		domainsPanel.add(domainsLabel);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.getElement().getStyle().setProperty("max-height", "300px");
		
		Grid domainTable = new Grid(0, 10);
		domainTable.clear();
		domainTable.setWidth("100%");

		int row = domainTable.insertRow(domainTable.getRowCount());

		Label id = new Label("ID");
		Label eschema = new Label("ESQUEMA");
		Label type = new Label("TIPO");
		Label name = new Label("NOMBRE");
		Label description = new Label("DESCRIPCION");
		Label document = new Label("DOCUMENTO");
		Label status = new Label("ESTADO");
		Label expire = new Label("EXPIRA");
		Label lastAccess = new Label("ULT. ACCESO");
		Label url = new Label("");
		
		id.addStyleName(AON.CSS.aonHeaderTable());
		eschema.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		description.addStyleName(AON.CSS.aonHeaderTable());
		document.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		expire.addStyleName(AON.CSS.aonHeaderTable());
		lastAccess.addStyleName(AON.CSS.aonHeaderTable());
		url.addStyleName(AON.CSS.aonHeaderTable());

		domainTable.setWidget(row, 0, id);
		domainTable.setWidget(row, 1, eschema);
		domainTable.setWidget(row, 2, type);
		domainTable.setWidget(row, 3, name);
		domainTable.setWidget(row, 4, description);
		domainTable.setWidget(row, 5, document);
		domainTable.setWidget(row, 6, status);
		domainTable.setWidget(row, 7, expire);
		domainTable.setWidget(row, 8, lastAccess);
		domainTable.setWidget(row, 9, url);
		
		domainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		
		domainTable.getColumnFormatter().getElement(0).getStyle().setWidth(50, Unit.PX);
		domainTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		domainTable.getColumnFormatter().getElement(2).getStyle().setWidth(80, Unit.PX);

		domainTable.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(6).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(9).getStyle().setWidth(25, Unit.PX);
		
		scrollPanel.add(domainTable);
		
		for(DomainCompany domainCompany : customerDomains) {
			int newRow = domainTable.insertRow(domainTable.getRowCount());
			
			Label idLabel = new Label(domainCompany.getDomain().getId().toString());
			Label schemaLabel = new Label(domainCompany.getSchema());
			Label domainTypeLabel  = new Label(domainCompany.getDomain().getDomainType().getName());
			Label nameLabel = new Label(domainCompany.getDomain().getName());
			Label descriptionLabel = new Label(domainCompany.getDomain().getDescription());
			Label documentLabel = new Label(domainCompany.getCompany().getDocument());
			Label statusLabel = new Label(domainCompany.getDomain().getAonStatus().getName());
			Label expirationLabel = new Label(formatDate(domainCompany.getDomain().getExpirationDate()));
			Label lastAccessLabel = new Label(formatDate(domainCompany.getDomain().getLastAccessDate()));
			
			domainTable.setWidget(newRow, 0, idLabel);
			domainTable.setWidget(newRow, 1, schemaLabel);
			domainTable.setWidget(newRow, 2, domainTypeLabel);
			domainTable.setWidget(newRow, 3, nameLabel);
			domainTable.setWidget(newRow, 4, descriptionLabel);
			domainTable.setWidget(newRow, 5, documentLabel);
			domainTable.setWidget(newRow, 6, statusLabel);
			domainTable.setWidget(newRow, 7, expirationLabel);
			domainTable.setWidget(newRow, 8, lastAccessLabel);
			
			AonTableButton urlBtn = new AonTableButton("Ir a", AON.CSS.aonIconSend());
			urlBtn.addClickHandler(e -> {
				Window.open("https://" + domainCompany.getDomain().getName(), "_blank", "");
			});
			
			domainTable.setWidget(newRow, 9, urlBtn);
			
			if (newRow % 2 == 0) {
				idLabel.addStyleName(AON.CSS.aonOddTableRow());
				schemaLabel.addStyleName(AON.CSS.aonOddTableRow());
				domainTypeLabel.addStyleName(AON.CSS.aonOddTableRow());
				nameLabel.addStyleName(AON.CSS.aonOddTableRow());
				descriptionLabel.addStyleName(AON.CSS.aonOddTableRow());
				documentLabel.addStyleName(AON.CSS.aonOddTableRow());
				statusLabel.addStyleName(AON.CSS.aonOddTableRow());
				expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
				lastAccessLabel.addStyleName(AON.CSS.aonOddTableRow());
				urlBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				domainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
				domainTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
			}
			
			domainTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
			domainTable.getCellFormatter().getElement(newRow, 9).getStyle().setTextAlign(TextAlign.CENTER);
			
			domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		}
		
		domainsPanel.add(scrollPanel);
		
	}
	
	private void customerLabelStyle(Widget widget) {
		widget.getElement().getStyle().setProperty("min-width", "7rem");
		widget.getElement().getStyle().setProperty("font-weight", "bold");
		widget.getElement().getStyle().setProperty("text-transform", "uppercase");
	}
	
	private String formatDate(Date date) {
		if(null == date) return "";
		return formatDate.format(date);
	}
	
}
