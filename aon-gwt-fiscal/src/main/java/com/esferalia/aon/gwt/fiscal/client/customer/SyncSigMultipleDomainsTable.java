package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.registry.DomainCustomerSync;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class SyncSigMultipleDomainsTable extends ScrollPanel {

	private static final Logger LOGGER = Logger.getLogger(SyncSigMultipleDomainsTable.class.getName());
	static { LOGGER.addHandler(new ConsoleLogHandler()); }

	private AonCustomTable tab;
	
	private Customer customer;
	private List<DomainSigAddInfo> customerRaddInfo;
	private List<DomainCompany> customerDomain;
	
	private CustomerApi customerApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = true;

	private static enum COLS {

		ID("Id", "4rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		SCH("Esquema", "12rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		URL("URL", "-moz-available", "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		TYP("Tipo Dom.", "5rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ACS("Cliente", "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ICL("Sincronizaci\u00f3n", "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center; text-align: center;"),
		INF("", "3rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center; text-align: center;"),
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public SyncSigMultipleDomainsTable(Customer customer, List<DomainSigAddInfo> customerRaddInfo,  List<DomainCompany> customerDomain) {
		this.customer = customer;
		this.customerRaddInfo = customerRaddInfo;
		this.customerDomain = customerDomain;
		
		this.customerApi = new CustomerApi(SESSION_API);
		
		setWidth("100%");

		paintTable();
	}

	private void paintTable() {
		tab = new AonCustomTable();
		setWidget(tab);

		paintHeader();
		paintBody();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values()) {
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		}
	}

	private void paintBody() {
		if(customerRaddInfo.isEmpty() && customerDomain.isEmpty())
			paintEmptyRow();
		else {
			if(!customerDomain.isEmpty()) {
				customerDomain.forEach(cd -> {
					DomainCustomerSync dcs = new DomainCustomerSync()
							.setSchema(cd.getSchema())
							.setDomainName(cd.getDomain().getName())
							.setDomainId(cd.getDomain().getId())
							.setType(null == cd.getDomain().getDomainType() ? "" : cd.getDomain().getDomainType().getName())
							.setAonCustomer(cd.getDomain().getAonCustomer())
							.setHasAonCustomer(null != cd.getDomain().getAonCustomer())
							.setHasRaddInfo(customerRaddInfo.stream().filter(cr -> AonStringUtils.equalsIgnoreCase(cr.getDomainId(), cd.getDomain().getId().toString())).findAny().isPresent())
							.setRegistry(customer.getId())
							;
					
					paintRow(dcs);
				});
			}
			
			if(!customerRaddInfo.isEmpty()) {
				for(DomainSigAddInfo domainSigAddInfo : customerRaddInfo) {
					if(customerDomain.stream().filter(cd -> cd.getDomain().getId().equals(Integer.parseInt(domainSigAddInfo.getDomainId()))).findAny().isPresent())
						continue;
					
					DomainCustomerSync dcs = new DomainCustomerSync()
							.setSchema(domainSigAddInfo.getDomainSchema())
							.setDomainName(domainSigAddInfo.getDomainName())
							.setDomainId(AonStringUtils.isBlank(domainSigAddInfo.getDomainId()) ? null : Integer.parseInt(domainSigAddInfo.getDomainId()))
							.setType(domainSigAddInfo.getDomainType())
							.setAonCustomer(null)
							.setHasAonCustomer(customerDomain.stream().filter(cd -> AonStringUtils.equalsIgnoreCase(domainSigAddInfo.getDomainId(), cd.getDomain().getId().toString())).findAny().isPresent())
							.setHasRaddInfo(true)
							.setRegistry(customer.getId())
							;
					
					paintRow(dcs);
				}
			}
			
		}
	}

	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();

		Label name = new Label("No existen dominios para el cliente");
		name.setTitle("No existen dominios para el cliente");
		tab.addInlineStyle(name, COLS.SCH.getStyles());
		tab.addRow(row, name, COLS.SCH.getColWidth());
	}

	private void paintRow(DomainCustomerSync domainCustomerSync) {
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint =  "/ms/api/domain/aon-customer-domain/" + domainCustomerSync.getDomainName();
		
		customerApi.getAonCustomerDomain(host, endPoint, new AsyncCallback<Domain>() {
			
			@Override
			public void onSuccess(Domain domainAonCustomer) {
				
				HTMLPanel row = tab.createRow();
				row.addDomHandler(e -> onClickRow(domainCustomerSync), ClickEvent.getType());
				
				Label domainId = new Label(domainCustomerSync.getDomainId().toString());
				tab.addInlineStyle(domainId, COLS.ID.getStyles());
				tab.addRow(row, domainId, COLS.ID.getColWidth());

				Label schema = new Label(domainCustomerSync.getSchema());
				schema.setTitle(domainCustomerSync.getSchema());
				tab.addInlineStyle(schema, COLS.SCH.getStyles());
				tab.addRow(row, schema, COLS.SCH.getColWidth());
				
				Label name = new Label(domainCustomerSync.getDomainName());
				name.setTitle(domainCustomerSync.getDomainName());
				tab.addInlineStyle(name, COLS.URL.getStyles());
				tab.addRow(row, name, COLS.URL.getColWidth());
				
				Label type = new Label(domainCustomerSync.getType());
				type.setTitle(domainCustomerSync.getType());
				tab.addInlineStyle(type, COLS.TYP.getStyles());
				tab.addRow(row, type, COLS.TYP.getColWidth());
				
				Label aonCustomer = new Label(null == domainCustomerSync.getAonCustomer() ? "" : domainCustomerSync.getAonCustomer().toString());
				aonCustomer.setTitle(domainCustomerSync.getType());
				tab.addInlineStyle(aonCustomer, COLS.ACS.getStyles());
				tab.addRow(row, aonCustomer, COLS.ACS.getColWidth());
				
				HTMLPanel infoPanel = new HTMLPanel(AonStringUtils.EMPTY);
				infoPanel.addStyleName(AON.CSS.aonItemFlex());
				AonTableButton infoCustomer = new AonTableButton(
						"RAddInfo",
						domainCustomerSync.isHasRaddInfo() ? AON.CSS.aonIconPersonCheck() : AON.CSS.aonIconPersonAlert()
				);
				infoCustomer.getElement().getStyle().setProperty("background-size", "22px");
				infoPanel.add(infoCustomer);
				AonTableButton infoDomain = new AonTableButton(
						"AonCustomer",
						domainCustomerSync.isHasAonCustomer() ? AON.CSS.aonIconWork() : AON.CSS.aonIconEnterpriseOff()
				);
				infoDomain.getElement().getStyle().setProperty("background-size", "22px");
				infoPanel.add(infoDomain);
				tab.addInlineStyle(infoPanel, COLS.ICL.getStyles());
				tab.addRow(row, infoPanel, COLS.ICL.getColWidth());
				
				HTMLPanel messagesPanel = new HTMLPanel(AonStringUtils.EMPTY);
				messagesPanel.addStyleName(AON.CSS.aonItemFlex());
				if(needInfoShow(domainCustomerSync, domainAonCustomer)) {
					AonTableButton messages = new AonTableButton(
							getInfoMessage(domainCustomerSync, domainAonCustomer),
							AON.CSS.aonIconInfo()
					);
					messages.getElement().getStyle().setProperty("background-size", "22px");
					messagesPanel.add(messages);
				}
				tab.addInlineStyle(messagesPanel, COLS.INF.getStyles());
				tab.addRow(row, messagesPanel, COLS.INF.getColWidth());
				
			}
			
			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	private boolean needInfoShow(DomainCustomerSync domainCustomerSync, Domain domainAonCustomer) {
		return (null == domainAonCustomer || !domainAonCustomer.getAonCustomer().equals(domainCustomerSync.getAonCustomer()))
			|| (customer.getStatus().equals(RegistryStatus.BLOCKED) && null == domainAonCustomer.getExpirationDate())
			|| (customer.getStatus().equals(RegistryStatus.INACTIVE) && (null == domainAonCustomer.getExpirationDate() || domainAonCustomer.isActive()))
			
			;
	}
	
	private String getInfoMessage(DomainCustomerSync domainCustomerSync, Domain domainAonCustomer) {
		if(null == domainAonCustomer || !domainAonCustomer.getAonCustomer().equals(domainCustomerSync.getAonCustomer()))
			return "El cliente del dominio no coincide con el seleccionado";
		else if(customer.getStatus().equals(RegistryStatus.BLOCKED) && null == domainAonCustomer.getExpirationDate())
			return "El cliente esta bloqueado pero el dominio no tiene fecha de expiraci\u00f3n";
		else if(customer.getStatus().equals(RegistryStatus.INACTIVE) && (null == domainAonCustomer.getExpirationDate() || domainAonCustomer.isActive()))
			return "El cliente esta inactivo pero el dominio esta activo o no tiene fecha de expiraci\u00f3n";
		
		return null;
	}
	
	protected abstract void onClickRow(DomainCustomerSync domainCustomerSync);

}
