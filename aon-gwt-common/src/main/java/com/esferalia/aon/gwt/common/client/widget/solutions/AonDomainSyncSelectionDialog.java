package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonDomainSyncSelectionDialog extends AonCustomDialog {
	
	private Button acceptBtnDialog;
	private List<DomainCompany> companies;
	private DomainCompany selectedDomain;
	private Customer customer;
	
	private List<CheckBox> checkBoxes = new ArrayList<>();
	
	public AonDomainSyncSelectionDialog(String caption, List<DomainCompany> companies, Customer customer) {
		this.setCaption(caption);
		this.showCloseButton(true);
		
		this.companies = companies;
		this.customer = customer;
		
		this.add(createContent());
		
		showDialog();
	}

	private Widget createContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setPadding(1, Unit.EM);
		
		Label selectLabel = new Label("Seleccione un " + this.getCaption() + ":");
		selectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		content.add(selectLabel);
		
		SuggestBox domainSuggestBox = new SuggestBox();
		domainSuggestBox.setHeight("2em");
		domainSuggestBox.getElement().getStyle().setProperty("padding", "0");
		domainSuggestBox.getElement().getStyle().setProperty("width", "99%");
		domainSuggestBox.setAutoSelectEnabled(false);
		domainSuggestBox.getElement().setPropertyString("placeholder", "Dominio: busque por descripci\u00f3n");
		
		List<String> suggestions = companies.stream().map(company -> company.getDomain().getDescription()).collect(Collectors.toList());
		
		MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) domainSuggestBox.getSuggestOracle();
		orclSb.clear();
		orclSb.addAll(suggestions);
		orclSb.setDefaultSuggestionsFromText(suggestions);
		domainSuggestBox.showSuggestionList();
		
		domainSuggestBox.addSelectionHandler(e -> {
			domainSuggestBox.hideSuggestionList();
			getCompanyDomain(domainSuggestBox.getValue());
			checkBoxes.forEach(cb -> {
				cb.setValue(false);
				cb.setEnabled(false);
			});
			
		});
		
		domainSuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				domainSuggestBox.showSuggestionList();
			} else if(AonStringUtils.isBlank(domainSuggestBox.getValue())) {
				checkBoxes.forEach(cb -> {
					cb.setValue(false);
					cb.setEnabled(true);
				});
			}
		});
		
		content.add(domainSuggestBox);
		content.add(createSuggestDomains());
	    
	    HTMLPanel buttonsPanel = new HTMLPanel("");
	    buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
	    
	    acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAccept(selectedDomain);
			hide();
		});

		buttonsPanel.add(acceptBtnDialog);
	    
		content.add(buttonsPanel);
		
		return content;
	}
	
	private Widget createSuggestDomains() {
		HTMLPanel panel = new HTMLPanel("");
		panel.addStyleName(AON.CSS.aonFlexColumn());
		
		if(null == companies || companies.isEmpty()) {
			Label emptySuggest = new Label("No existen sugerencias, por favor use el buscador para encontrar el dominio.");
			panel.add(emptySuggest);
		} else {
//			companies.forEach(domainCompanyIt -> {
//				Window.alert("domainCompanyIt.getCompany().getDocument() : " + domainCompanyIt.getCompany().getDocument() + "\ncustomer.getDocument() : " + customer.getDocument() + "\n" + AonStringUtils.isNotBlank(domainCompanyIt.getCompany().getDocument()) + " && " + AonStringUtils.equalsIgnoreCase(domainCompanyIt.getCompany().getDocument(), customer.getDocument()));
//				Window.alert("domainCompanyIt.getDomain().getName() : " + domainCompanyIt.getDomain().getName() + "\ngetCustomerAlias() : " + getCustomerAlias() + "\n" + AonStringUtils.isNotBlank(domainCompanyIt.getDomain().getName()) + " && " + AonStringUtils.containsIgnoreCase(domainCompanyIt.getDomain().getName(), getCustomerAlias()));
//			});
			
			List<DomainCompany> filterCompanies = companies.stream().filter(
					domainCompanyIt -> (AonStringUtils.isNotBlank(domainCompanyIt.getCompany().getDocument()) && AonStringUtils.equalsIgnoreCase(domainCompanyIt.getCompany().getDocument(), customer.getDocument())) || 
					(AonStringUtils.isNotBlank(domainCompanyIt.getDomain().getName()) && AonStringUtils.containsIgnoreCase(domainCompanyIt.getDomain().getName(), getCustomerAlias())))
					.collect(Collectors.toList());
			
			if(filterCompanies.isEmpty()) {
				Label emptySuggest = new Label("No existen sugerencias, por favor use el buscador para encontrar el dominio.");
				panel.add(emptySuggest);
			} else {
				Label suggest = new Label("Seleccione un dominio de los sugueridos o use el buscador.");
				panel.add(suggest);
				
				for(DomainCompany company : filterCompanies) {
					HTMLPanel rowPanel = new HTMLPanel("");
					rowPanel.addStyleName(AON.CSS.aonItemFlex());
					
					CheckBox cb = new CheckBox();
					checkBoxes.add(cb);
					cb.addValueChangeHandler(e -> {
						selectedDomain = company;
						checkBoxes.stream().filter(cbIt -> !cb.equals(cbIt)).forEach(cbIt -> cbIt.setValue(false));
					});
					
					Label domainName = new Label(company.getDomain().getDescription());
					
					rowPanel.add(cb);
					rowPanel.add(domainName);
					panel.add(rowPanel);
				}
			}
		}
		
		return panel;
	}
	
	private String getCustomerAlias() {
		if (AonStringUtils.isBlank(customer.getAlias())) return null;
		
		if(AonStringUtils.contains(customer.getAlias(), "."))
			return customer.getAlias().split("\\.")[0];
		
		return customer.getAlias();
	}

	private void getCompanyDomain(String domainDescription) {
		selectedDomain = companies.stream().filter(company -> AonStringUtils.equalsIgnoreCase(company.getDomain().getDescription(), domainDescription)).findFirst().get();
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	protected abstract void onAccept(DomainCompany domainCompany);
	
}
