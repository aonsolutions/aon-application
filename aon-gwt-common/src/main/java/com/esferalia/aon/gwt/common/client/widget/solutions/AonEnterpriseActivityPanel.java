package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.Cnae;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonEnterpriseActivityPanel extends HTMLPanel {

	// Callback

	public static interface AonEnterpriseActivityPanelCallback {
		void onAccept(Activity enterpriseActivity);

		void onCancel();

		void onLoadedEnd();
		
		boolean existsMainEnterpriseActivity(Integer currentEnterpriseActivityId);
		
		boolean isEmptyActivities();
	}

	// CommonService

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	// Variables

	private final static String EMPTY_STRING = "";

	private String domainName;
	private Integer domainId;
	private String user;

	private AonEnterpriseActivityPanelCallback callback;
	private Activity enterpriseActivity;

	// Wrokplace Info

	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);

	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomToogleButton principal = new AonCustomToogleButton("Principal");
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	
	private AonCustomListBox ivaLB = new AonCustomListBox("Regimen IVA");
	private AonCustomListBox irpfLB = new AonCustomListBox("IRPF");
	private AonCustomToogleButton equivalence = new AonCustomToogleButton("R. Equivalencia");
	private AonCustomDateBox endDate = new AonCustomDateBox("F. Fin");
	
	private AonCustomListBox ivaExemptLB = new AonCustomListBox("Causa Exenci\u00f3n IVA");

	private AonCustomSuggestBox cnae25SB = new AonCustomSuggestBox("CNAE 2025");
	private AonCustomTextBox cnae = new AonCustomTextBox("CNAE 2009 (Registro Anterior)");
	
	private AonCustomSuggestBox iaeSB = new AonCustomSuggestBox("IAE");

	private List<Cnae> cnae2025List;
	private List<Cnae2009> cnae2009List;
	private List<Iae> iaeList;

	// Constructor

	public AonEnterpriseActivityPanel(String domainName, Integer domain, String user, Integer registry,
			AonEnterpriseActivityPanelCallback callback) {
		super(EMPTY_STRING);

		this.enterpriseActivity = new Activity().setDomain(domain).setEnterprise(registry);

		aonEnterpriseActivityPanel(domainName, domain, user, callback);
	}

	public AonEnterpriseActivityPanel(String domainName, Integer domain, String user, Activity enterpriseActivity,
			AonEnterpriseActivityPanelCallback callback) {
		super(EMPTY_STRING);

		this.enterpriseActivity = enterpriseActivity;

		aonEnterpriseActivityPanel(domainName, domain, user, callback);
	}

	private void aonEnterpriseActivityPanel(String domainName, Integer domain, String user,
			AonEnterpriseActivityPanelCallback callback) {
		initializeCommonService();

		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;

		this.callback = callback;

		getContextInfo(end -> {
			show();
			callback.onLoadedEnd();
		});
	}

	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);

		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");

		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());

		principal.getElement().getStyle().setProperty("max-width", "8rem");
		startDate.getElement().getStyle().setProperty("max-width", "8rem");
		
		if(callback.isEmptyActivities()) {
			principal.setValue(true);
			principal.setEnable(false);
		}

		row.add(description);
		row.add(startDate);
		row.add(principal);
		container.add(row);
		
		// Row 1
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		ivaLB.clearItems();
		for (int i = 0; i < VATRegime.values().length; i++)
			ivaLB.addItem(VATRegime.values()[i].getDescription(), VATRegime.values()[i].name());
		
		irpfLB.clearItems();
		for (int i = 0; i < IRPFRegime.values().length; i++)
			irpfLB.addItem(IRPFRegime.values()[i].getDescription(), IRPFRegime.values()[i].name());

		equivalence.getElement().getStyle().setProperty("max-width", "8rem");
		endDate.getElement().getStyle().setProperty("max-width", "8rem");
		
		row2.add(ivaLB);
		row2.add(irpfLB);
		row2.add(endDate);
		row2.add(equivalence);
		container.add(row2);
		
		// Row 
		HTMLPanel row_ = new HTMLPanel(EMPTY_STRING);
		row_.setStyleName(AON.CSS.aonItemFlex());

		ivaExemptLB.clearItems();
		ivaExemptLB.addItem("-", "");
		for (int i = 0; i < VATExemptionCause.values().length; i++)
			ivaExemptLB.addItem(VATExemptionCause.values()[i].getDescription(), VATExemptionCause.values()[i].name());
		
		row_.add(ivaExemptLB);
		container.add(row_);

		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		initializeCnae25();

		row3.add(cnae25SB);
		container.add(row3);
		
		//  Row
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		//initializeCnae();
		
		//cnaeSB.setEnable(false);
		cnae.setEnable(false);

		//row4.add(cnaeSB);
		row4.add(cnae);
		container.add(row4);
		
		// Third Row
		HTMLPanel row5 = new HTMLPanel(EMPTY_STRING);
		row5.setStyleName(AON.CSS.aonItemFlex());
		
		initializeIae();

		row5.add(iaeSB);
		container.add(row5);
		
		Label cccTitle = new Label("Cuentas de Cotizaci\u00f3n" + (enterpriseActivity.getId() != null && !enterpriseActivity.getCccs().isEmpty() ? " (" + enterpriseActivity.getCccs().size() + ")" : ""));
		cccTitle.getElement().getStyle().setProperty("font-size", "1rem");
		cccTitle.getElement().getStyle().setProperty("font-weight", "700");
		cccTitle.getElement().getStyle().setProperty("color", "#5f6368");
		container.add(cccTitle);
		
		EnterpriseCCCTable cccTable = new EnterpriseCCCTable(this.domainId, this.enterpriseActivity) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		container.add(cccTable);
	
		ivaLB.addChangeHandler(e -> checkIvaExemptVisibility(row_));

		// Fill info
		if (enterpriseActivity.getId() != null) {
			description.setValue(enterpriseActivity.getDescription());
			principal.setValue(enterpriseActivity.isPrincipal());
			cnae25SB.setValue(null == enterpriseActivity.getCnae25() ? "" : enterpriseActivity.getCnae25Code() + " - " +  enterpriseActivity.getCnae25Description());
			
			if(null != enterpriseActivity.getCnae2509Code()) {
				cnae25SB.addButton(new AonTableButton(AonStringUtils.isBlank(enterpriseActivity.getCnae2509Code()) ? "" : "CNAE 2009: " + enterpriseActivity.getCnae2509Code() + " - " +  enterpriseActivity.getCnae2509Description(), AON.CSS.aonIconInfo()));
			}
			
			cnae.setValue(AonStringUtils.isBlank(enterpriseActivity.getCnaeCode()) ? "" : enterpriseActivity.getCnaeCode() + " - " +  enterpriseActivity.getCnaeDescription());
			
			if(AonStringUtils.equalsIgnoreCase(enterpriseActivity.getCnae2509Code(), enterpriseActivity.getCnaeCode()))
				cnae.getElement().getStyle().setDisplay(Display.NONE);
			
			iaeSB.setValue(null == enterpriseActivity.getIae() ? "" :  enterpriseActivity.getIae().getSection() + " - " +  enterpriseActivity.getIae().getEpigraph() + " : " +  enterpriseActivity.getIae().getTitle());
			ivaLB.setValue(enterpriseActivity.getVatRegime().name());
			ivaExemptLB.setValue(null != enterpriseActivity.getVatExemptionCause() ? enterpriseActivity.getVatExemptionCause().name() : "");
			equivalence.setValue(enterpriseActivity.isSurcharge());
			irpfLB.setValue(enterpriseActivity.getIrpfRegime().name());
			startDate.setValue(enterpriseActivity.getStartDate());
			endDate.setValue(enterpriseActivity.getEndDate());
		}
		
		checkIvaExemptVisibility(row_);

		// Buttons
		container.add(createButtonsPanel());
		add(container);
	}
	
	private void checkIvaExemptVisibility(HTMLPanel row) {
		VATRegime vatRegime = VATRegime.safeValueOf(ivaLB.getValue());
		if(null != vatRegime && vatRegime.isExempt()) {
			row.getElement().getStyle().clearDisplay();
		} else {
			row.getElement().getStyle().setDisplay(Display.NONE);
			ivaExemptLB.setValue("");
		}
	}

	private void initializeCnae25() {
		List<String> cnaeDescriptions = new ArrayList<>();
		cnae2025List.forEach(c -> cnaeDescriptions.add(c.getCode() + " - " + c.getTitle()));
		cnaeDescriptions.sort((o1, o2) -> o1.compareTo(o2));

		AonCustomSuggestOracle oracleCnae25 = new AonCustomSuggestOracle();
		oracleCnae25.setData(cnaeDescriptions);

		cnae25SB = new AonCustomSuggestBox("CNAE 2025", oracleCnae25);
		
		cnae25SB.setAutoSelectEnabled(true);
		cnae25SB.setPlaceHolder("CNAE... (Ctrl + espacio para ver sugerencias)");

		cnae25SB.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cnae25SB.setValue(AonStringUtils.EMPTY);
				cnae25SB.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cnae25SB.hideSuggestionList();
		});
		
		cnae25SB.getSuggestBox().addSelectionHandler(e -> {
			Cnae cnae25 = getCnae25();
			if(null != cnae25)
				cnae.setValue(cnae25.getCode09() + " - " +  cnae25.getTitle09());
			
			cnae25SB.removeButton();
			cnae25SB.addButton(new AonTableButton("CNAE 2009: " + cnae25.getCode09() + " - " +  cnae25.getTitle09(), AON.CSS.aonIconInfo()));
		});
	}

	private void initializeIae() {
		List<String> iaeDescriptions = new ArrayList<>();
		iaeList.forEach(i -> iaeDescriptions.add(i.getSection() + " - " + i.getEpigraph() + " : " + i.getTitle()));
		iaeDescriptions.sort((o1, o2) -> o1.compareTo(o2));

		AonCustomSuggestOracle oracleIae = new AonCustomSuggestOracle();
		oracleIae.setData(iaeDescriptions);

		iaeSB = new AonCustomSuggestBox("IAE", oracleIae);
		
		iaeSB.setAutoSelectEnabled(true);
		iaeSB.setPlaceHolder("IAE... (Ctrl + espacio para ver sugerencias)");

		iaeSB.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				iaeSB.setValue(AonStringUtils.EMPTY);
				iaeSB.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				iaeSB.hideSuggestionList();
		});
	}

	private Iae getIae() {
		String iaeSBValue = iaeSB.getValue();

		for (Iae i : iaeList) {
			if (AonStringUtils.equalsIgnoreCase(i.getSection() + " - " + i.getEpigraph() + " : " + i.getTitle(), iaeSBValue))
				return i;
		}

		return null;
	}

	private Cnae getCnae25() {
		String cnaeSBValue = cnae25SB.getValue();

		for (Cnae c : cnae2025List) {
			if (AonStringUtils.equalsIgnoreCase(c.getCode() + " - " + c.getTitle(), cnaeSBValue))
				return c;
		}

		return null;
	}
	
	private Cnae2009 getCnae(Cnae cnae25) {
		if(null == cnae25 || cnae25.getCode09() == null) return null;
		
		String cnae09Code = cnae25.getCode09();

		for (Cnae2009 c : cnae2009List) {
			if (AonStringUtils.equalsIgnoreCase(c.getCode(), cnae09Code))
				return c;
		}

		return null;
	}

	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");

		Button okButton = new Button();
		okButton.setStyleName(AON.CSS.aonOkButton());
		okButton.setText(AON.MSG.accept());
		okButton.addClickHandler(e -> {
			okButton.setEnabled(false);
			
			if(callback.existsMainEnterpriseActivity(enterpriseActivity.getId()) && principal.getValue()) {
				
				AonDialog dialog = new AonDialog("Cambio de actividad principal",
						new HTML("Ya existe una actividad principal. Si establece esta actividad como principal, la actividad que actualmente es principal dejara de serlo. \u00bfDesea continuar\u003f"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						okButton.setEnabled(true);
						return;
					}

					@Override
					public void onAccept() {
						saveActivity(okButton);
					}
				});
			} 
			else saveActivity(okButton);

			
		});
		buttonsPanel.add(okButton);

		final Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.addClickHandler(e -> {
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
		buttonsPanel.add(cancelButton);

		return buttonsPanel;
	}
	
	private void saveActivity(Button okButton) {
		enterpriseActivity
			.setStartDate(startDate.getValue())
			.setEndDate(endDate.getValue())
			.setDescription(description.getValue())
			.setPrincipal(principal.getValue())
			.setVatRegime(VATRegime.safeValueOf(ivaLB.getValue()))
			.setVatExemptionCause(AonStringUtils.isBlank(ivaExemptLB.getValue()) ? null : VATExemptionCause.safeValueOf(ivaExemptLB.getValue()))
			.setSurcharge(equivalence.getValue())
			.setIrpfRegime(IRPFRegime.safeValueOf(irpfLB.getValue()))
			;
	
	
		Cnae cnae25 = getCnae25();
		if(null != cnae25) {
			enterpriseActivity.setCnae25(cnae25.getId());
			enterpriseActivity.setCnae25Code(cnae25.getCode());
			enterpriseActivity.setCnae25Description(cnae25.getTitle());
		} else {
			enterpriseActivity.setCnae25(null);
			enterpriseActivity.setCnae25Code(null);
			enterpriseActivity.setCnae25Description(null);
		}
		
		Cnae2009 cnae = getCnae(cnae25);
		if(null != cnae) {
			enterpriseActivity.setCnae(cnae.getId());
			enterpriseActivity.setCnaeCode(cnae.getCode());
			enterpriseActivity.setCnaeDescription(cnae.getTitle());
		} else {
			enterpriseActivity.setCnae(null);
			enterpriseActivity.setCnaeCode(null);
			enterpriseActivity.setCnaeDescription(null);
		}
		
		Iae iae = getIae();
		enterpriseActivity.setIae(iae);
	
		commonService.saveEnterpriseActivity(domainName, domainId, user, enterpriseActivity,
				new AsyncCallback<Activity>() {
	
					@Override
					public void onSuccess(Activity result) {
						callback.onAccept(result);
					}
	
					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, error.getMessage());
						okButton.setEnabled(true);
					}
	
				});
	}

	private void getContextInfo(Consumer<Void> end) {
		commonService.getCnae2009List(domainName, domainId, user, new AsyncCallback<List<Cnae2009>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error CNAE 2009: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Cnae2009> cnae2009DB) {
				cnae2009List = cnae2009DB;
				
				commonService.getCnae2025List(domainName, domainId, user, new AsyncCallback<List<Cnae>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error CNAE 2025: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<Cnae> cnae2025DB) {
						cnae2025List = cnae2025DB;

						commonService.getIaeList(domainName, domainId, user, new AsyncCallback<List<Iae>>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error IAE: " + caught.getMessage());
							}

							@Override
							public void onSuccess(List<Iae> iaeDB) {
								iaeList = iaeDB;
								end.accept(null);
							}

						});
					}

				});
				
			}

		});
	}

}
