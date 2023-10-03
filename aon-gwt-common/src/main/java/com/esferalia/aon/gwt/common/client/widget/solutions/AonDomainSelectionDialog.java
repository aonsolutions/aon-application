package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonDomainSelectionDialog extends AonCustomDialog {
	
	private Button acceptBtnDialog;
	private List<DomainCompany> companies;
	private DomainCompany domainCompany;
	
	private boolean selectAll = false;
	
	public AonDomainSelectionDialog(String caption, List<DomainCompany> companies) {
		this.setCaption(caption);
		this.showCloseButton(true);
		
		setWidth("300px");
		
		this.companies = companies;
		
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
		});
		
		domainSuggestBox.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				domainSuggestBox.showSuggestionList();
			}
		});
		
		HTMLPanel rowPanel = new HTMLPanel("");
		rowPanel.addStyleName(AON.CSS.aonItemFlex());
		
		CheckBox selectAllDomainsCB = new CheckBox();
		selectAllDomainsCB.addClickHandler(e -> {
			selectAll = selectAllDomainsCB.getValue();
			domainSuggestBox.setEnabled(!selectAll);
			
		});
		Label selectAllDomainsL = new Label("Seleccionar todos los dominios");
		
		rowPanel.add(selectAllDomainsCB);
		rowPanel.add(selectAllDomainsL);
		
		content.add(domainSuggestBox);
		content.add(rowPanel);
	    
	    HTMLPanel buttonsPanel = new HTMLPanel("");
	    buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
	    
	    acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAccept(domainCompany, selectAll);
			hide();
		});

		buttonsPanel.add(acceptBtnDialog);
	    
		content.add(buttonsPanel);
		
		return content;
	}
	
	private void getCompanyDomain(String domainDescription) {
		domainCompany = companies.stream().filter(company -> AonStringUtils.equalsIgnoreCase(company.getDomain().getDescription(), domainDescription)).findFirst().get();
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	protected abstract void onAccept(DomainCompany domainCompany, boolean selectAll);
	
}
