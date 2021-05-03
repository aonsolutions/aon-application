package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementComunicaInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementComunica extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static AgreementComunicaUiBinder uiBinder = GWT.create(AgreementComunicaUiBinder.class);

	interface AgreementComunicaUiBinder extends UiBinder<Widget, AgreementComunica> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexEvenly();
		String headerStyle();
	}
	
	@UiField
	HTMLPanel centerContainer;
	
	@UiField
	Grid agreementDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid agreementDataTable;
	
	@UiField
	HTMLPanel footerOptionsToolbar;
	
	private Integer newId = -1;
	
	private Map<String, String> agreementMap;
	
	// --------------------------------------------------	 CONSTRUCTOR	--------------------------------------------------------

	public AgreementComunica() {
		initWidget(uiBinder.createAndBindUi(this));
		initFooterOptionsToolbar();
		initPreview();
	}
	
	// --------------------------------------------------	   PREVIEW		--------------------------------------------------------
	
	public void setServiAgreements(Map<String, String> serviAgreements) {
		agreementMap = new HashMap<String, String>();
		agreementMap.putAll(serviAgreements);
	}
	
	public void resetPreview() {
		initPreview();
	}
	
	private void initPreview() {
		agreementDataTableHeader.clear();
		agreementDataTableHeader.resize(0, 0);
		agreementDataTable.clear();
		agreementDataTable.resize(0, 0);
		agreementDataTableHeader.resizeColumns(3);
		agreementDataTable.resizeColumns(3);
		
		paintHeader();
		calculateScrollPanelHeight();
		setColumnWidth();
	}
	
	private void paintHeader() {
		int row = agreementDataTableHeader.insertRow(agreementDataTableHeader.getRowCount());
		Label description = new Label("DESCRIPCI\u00D3N");
		Label ssNumber = new Label("C\u00D3DIGO SS");
		Label blank = new Label("");
		
		description.addStyleName(style.headerStyle());
		ssNumber.addStyleName(style.headerStyle());
		
		agreementDataTableHeader.setWidget(row, 0, description);
		agreementDataTableHeader.setWidget(row, 1, ssNumber);
		agreementDataTableHeader.setWidget(row, 2, blank);
	}
	
	private void calculateScrollPanelHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight/3) + "px");
	}
	
	private void setColumnWidth() {
		agreementDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PCT);
		agreementDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(30, Unit.PCT);
		agreementDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(5, Unit.PCT);
		
		agreementDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PCT);
		agreementDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(30, Unit.PCT);
		agreementDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(5, Unit.PCT);
	}
	
	// --------------------------------------------------	   INSERT ROWS		--------------------------------------------------------
	
	public void insertRow(AgreementComunicaInfo agreementComunicaInfo) {
		int row = agreementDataTable.insertRow(agreementDataTable.getRowCount());
		
		SuggestBox descriptionSB = createAgreementSB();
		descriptionSB.getElement().getStyle().setWidth(95, Unit.PCT);
		
		TextBox ssNumberTB = new TextBox();
		
		descriptionSB.addValueChangeHandler(e -> {
			String suggest = descriptionSB.getValue();
			
			String description = getServiAgreementDescriptionByKey(suggest);
			description = AonStringUtils.isBlank(description) ? descriptionSB.getValue() : description;
			descriptionSB.setValue(description);
			
			String ssNumber = getServiAgreementSSNumberByKey(suggest);
			ssNumber = AonStringUtils.isBlank(ssNumber) ? ssNumberTB.getValue() : ssNumber;
			ssNumberTB.setValue(ssNumber);
 			
			onInsertAgreement(agreementComunicaInfo.getId(), description, ssNumber);
		});
		descriptionSB.addSelectionHandler(e -> {
			String suggest = descriptionSB.getValue();
			
			String description = getServiAgreementDescriptionByKey(suggest);
			description = AonStringUtils.isBlank(description) ? descriptionSB.getValue() : description;
			descriptionSB.setValue(description);
			
			String ssNumber = getServiAgreementSSNumberByKey(suggest);
			ssNumber = AonStringUtils.isBlank(ssNumber) ? ssNumberTB.getValue() : ssNumber;
			ssNumberTB.setValue(ssNumber);
 			
			onInsertAgreement(agreementComunicaInfo.getId(), description, ssNumber);
		});
		descriptionSB.setValue(agreementComunicaInfo.getDescription());
		
		
		ssNumberTB.addValueChangeHandler(e -> {
			String newSSNumber = e.getValue();
			if(AonStringUtils.isNotBlank(newSSNumber)) {
				onInsertAgreement(agreementComunicaInfo.getId(), descriptionSB.getValue(), newSSNumber.trim());
			}
			
		});
		ssNumberTB.setValue(agreementComunicaInfo.getSSNumber());
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flexEvenly());
		
		AonTableButton delete = new AonTableButton("Eliminar Convenio", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteAgreement(agreementComunicaInfo.getId());
				initPreview();
				onInsertRows();
			}
		});
		buttonsPanel.add(delete);
			
		agreementDataTable.setWidget(row, 0, descriptionSB);
		agreementDataTable.setWidget(row, 1, ssNumberTB);
		agreementDataTable.setWidget(row, 2, buttonsPanel);
		
	}
	
	private SuggestBox createAgreementSB() {
		SuggestBox agreement = new SuggestBox();
		List<String> agreementsSuggest = new ArrayList<String>();
		for(String agreementInfo : agreementMap.keySet())
			agreementsSuggest.add(agreementInfo+"");
		MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) agreement.getSuggestOracle();
		orclAgreements.addAll(agreementsSuggest);
		agreement.setAutoSelectEnabled(true);
		return agreement;
	}

	public Integer insertNewRow(Integer newId) {
		int row = agreementDataTable.insertRow(agreementDataTable.getRowCount());
		this.newId = newId;
		this.newId--;
		
		SuggestBox descriptionSB = createAgreementSB();
		descriptionSB.getElement().getStyle().setWidth(95, Unit.PCT);
		
		TextBox ssNumberTB = new TextBox();
		
		descriptionSB.addValueChangeHandler(e -> {
			String suggest = descriptionSB.getValue();
			
			String description = getServiAgreementDescriptionByKey(suggest);
			description = AonStringUtils.isBlank(description) ? descriptionSB.getValue() : description;
			descriptionSB.setValue(description);
			
			String ssNumber = getServiAgreementSSNumberByKey(suggest);
			ssNumber = AonStringUtils.isBlank(ssNumber) ? ssNumberTB.getValue() : ssNumber;
			ssNumberTB.setValue(ssNumber);
 			
			onInsertAgreement(newId, description, ssNumber);
		});
		
		descriptionSB.addSelectionHandler(e -> {
			String suggest = descriptionSB.getValue();
			
			String description = getServiAgreementDescriptionByKey(suggest);
			description = AonStringUtils.isBlank(description) ? descriptionSB.getValue() : description;
			descriptionSB.setValue(description);
			
			String ssNumber = getServiAgreementSSNumberByKey(suggest);
			ssNumber = AonStringUtils.isBlank(ssNumber) ? ssNumberTB.getValue() : ssNumber;
			ssNumberTB.setValue(ssNumber);
 			
			onInsertAgreement(newId, description, ssNumber);
		});	
		
		ssNumberTB.addValueChangeHandler(e -> {
			String newSSNumber = e.getValue();
			if(AonStringUtils.isNotBlank(newSSNumber)) {
				onInsertAgreement(newId, descriptionSB.getValue(), newSSNumber.trim());
			}
			
		});
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flexEvenly());
		
		AonTableButton delete = new AonTableButton("Eliminar Convenio", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteAgreement(newId);
				initPreview();
				onInsertRows();
			}
		});
		buttonsPanel.add(delete);
			
		agreementDataTable.setWidget(row, 0, descriptionSB);
		agreementDataTable.setWidget(row, 1, ssNumberTB);
		agreementDataTable.setWidget(row, 2, buttonsPanel);
		
		onInsertRow();
		
		return newId;
	}
	
	// --------------------------------------------------	   AUX METHODS		--------------------------------------------------------
	
	public int getRowCount() {
		return agreementDataTable.getRowCount();
	}

	public Widget getWidget(int row, int column) {
		return agreementDataTable.getWidget(row, column);
	}
	
	public String getServiAgreementSSNumberByKey(String key) {
		for(Entry<String, String> entry : agreementMap.entrySet()) {
			if(AonStringUtils.equalsIgnoreCase(entry.getKey(), key)) {
				String entrySSNumber = entry.getKey().split(" - ")[0].trim();
				return entrySSNumber;
			}
		}
		return "";
	}
	
	public String getServiAgreementDescriptionByKey(String key) {
		for(Entry<String, String> entry : agreementMap.entrySet()) {
			if(AonStringUtils.equalsIgnoreCase(entry.getKey(), key)) {
				String entryDescription = entry.getKey().split(" - ")[1].trim();
				entryDescription = entryDescription.split(" \\[")[0].trim();
				return entryDescription;
			}
		}
		return "";
	}

	// --------------------------------------------------	   ABSTRACT METHODS		--------------------------------------------------------
	
	protected abstract void onInsertRow();
	
	protected abstract void onInsertRows();

	protected abstract void onDeleteAgreement(Integer agreementId);

	protected abstract void onInsertAgreement(Integer agreementId, String description, String ssNumber);

	// --------------------------------------------------	   FOOTER PANEL		--------------------------------------------------------
	
	private void initFooterOptionsToolbar() {
		footerOptionsToolbar.clear();
		
		AonTableButton newCCCBtn = new AonTableButton("Nuevo Convenio",  AON.CSS.aonIconAdd());
		newCCCBtn.addClickHandler(e -> {
			onAddNewAgreement(e);
		});
		
		footerOptionsToolbar.add(newCCCBtn);
	}

	private void onAddNewAgreement(ClickEvent e) {
		if(0 != agreementDataTable.getRowCount()) {
			SuggestBox descriptionSB = (SuggestBox) agreementDataTable.getWidget(0, 0);
			String description = descriptionSB.getValue();
			TextBox ssNumberTB = (TextBox) agreementDataTable.getWidget(0, 1);
			String ssNumber = ssNumberTB.getValue();
			if(AonStringUtils.isNotBlank(description) && AonStringUtils.isNotBlank(ssNumber)) {
				this.newId = insertNewRow(this.newId);
			}
		}else
			this.newId = insertNewRow(this.newId);
	}

}
