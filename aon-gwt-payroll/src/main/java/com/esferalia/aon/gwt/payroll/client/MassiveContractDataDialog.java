package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class MassiveContractDataDialog extends AonCustomDialog {

	// ------------------------------------------------- UIBinder

	interface MassiveContractDataDialogUIBinder extends UiBinder<Widget, MassiveContractDataDialog> {}

	private static final MassiveContractDataDialogUIBinder binder = GWT.create(MassiveContractDataDialogUIBinder.class);

	// ------------------------------------------------- UIFileds

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	HTMLPanel dataPanel;

	@UiField
	HTMLPanel buttonsPanel;

	// ------------------------------------------------- Variables

	private SuggestBox cnoSB;

	// ------------------------------------------------- Constructor

	protected MassiveContractDataDialog(String contractDataType) {

		setCaption(contractDataType);

		setWidget(binder.createAndBindUi(this));
		
		initView();
		getButtonsPanel();
		showDialog();
	}
	
	private void initView() {
		initializeCNOSuggest();
	}
	
	private void initializeCNOSuggest() {
		List<String> cnoSuggest = new ArrayList<>();
		for(Entry<String, CNO> entry : onGetCNOs().entrySet())
			cnoSuggest.add(entry.getKey() + " - " + entry.getValue().getTitle());
		
		cnoSuggest.sort((o1, o2) -> o1.compareTo(o2));

		cnoSB = new SuggestBox();
		
		MultiWordSuggestOracle orclCnaes = (MultiWordSuggestOracle) cnoSB.getSuggestOracle();
		orclCnaes.addAll(cnoSuggest);
		orclCnaes.setDefaultSuggestionsFromText(cnoSuggest);
		cnoSB.setAutoSelectEnabled(true);
		cnoSB.getElement().setPropertyString("placeholder", "C\u00f3digo CNO... (Ctrl + espacio para ver sugerencias)");
		
		cnoSB.getValueBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cnoSB.setText("");
				cnoSB.showSuggestionList();
			} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cnoSB.hideSuggestionList();
		});
		
		dataPanel.add(cnoSB);
	}

	// ------------------------------------------------- Auxiliar Methods

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	public String getCNOCode() {
		return AonStringUtils.isBlank(cnoSB.getValue()) ? null : cnoSB.getValue().split(" - ")[0];
	}

	// ------------------------------------------------- ButtonsPanel

	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());

		buttonsPanel.add(closeBtnDialog);

		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			onAccept();
		});

		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept();

	protected abstract Map<String, CNO> onGetCNOs();

}
