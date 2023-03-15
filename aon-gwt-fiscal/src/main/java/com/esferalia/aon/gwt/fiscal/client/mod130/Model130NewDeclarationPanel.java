package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model130NewDeclarationPanel extends DockLayoutPanel {
	
	protected final FlexTable tab = new FlexTable();
	private AdministrationListBox admonList;
	private AonIntegerBox yearBox;	
	private PeriodListBox periodList;
	private CheckBox replacement;
	private CheckBox complementary;
	private ListBox deponentBox;
	private AonDocumentTextBox documentBox;
	private AonTextBox nameBox;
	private AonTextBox surnameBox;
	private AonDoubleBox percentBox;
	private CheckBox regularHome;
	private ListBox regimeList;

	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	public Model130NewDeclarationPanel(Mod130 model,Model130Callback callback) {
		super( Unit.PX );
		
		addNorth(headerPanel, AonFiscalModelHeader.HEIGTH);

		AonToolbar toolbar = new AonToolbar(AON.MSG.newDeclaration());
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.backAction(),AON.CSS.aonIconBack());
		cancelButton.addClickHandler(event ->  callback.onCancel(model) );
		toolbar.add(cancelButton);
		addNorth(toolbar, AonToolbar.HEIGTH);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		add(scrollPanel);
		
		rootPanel = new FlowPanel(); 
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(rootPanel);
		
		paint(model,callback);
	}
	
	private void registerHandlers(Mod130 model, Model130Callback callback) {
		admonList = new AdministrationListBox();
		yearBox = new AonIntegerBox();	
		periodList = new PeriodListBox(false);
		replacement = new CheckBox(AON.MSG.replacement());
		complementary = new CheckBox(AON.MSG.complementary());
		deponentBox = new ListBox();
		documentBox = new AonDocumentTextBox();
		
		nameBox = new AonTextBox();
		surnameBox = new AonTextBox();
		percentBox = new AonDoubleBox();
		regularHome = new CheckBox(AON.MSG.regularHomePayments());
		regimeList = new ListBox();
		
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			regimeList.setVisible(model.isAEAT());
			percentBox.setVisible(model.isAEAT());
			regularHome.setVisible(model.isAEAT());
			initialize(model, callback );
		});
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> {
			model.setYear(yearBox.getValue());
			initialize(model, callback );
		});

		periodList.addChangeHandler( event -> {
			model.setPeriod( periodList.getValue());
			initialize(model, callback );
		});
		
		complementary.addClickHandler(event -> {
			model.setComplementary(complementary.getValue());
			replacement.setEnabled(!complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
				replacement.setValue(false);
			}
			
		});

		replacement.addClickHandler(event -> {
			model.setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (AonEnumUtils.getBoolean(replacement.getValue())) {
				complementary.setValue(false);
			}
		});
		
		regimeList.addItem(IRPFRegime.NORMAL.getName());
		regimeList.addItem(IRPFRegime.SIMPLIFIED.getName());
		regimeList.addChangeHandler(event -> {
			model.setRegime(regimeList.getSelectedIndex() == 1?IRPFRegime.SIMPLIFIED:IRPFRegime.NORMAL);
		});
		if (AonCollectionUtils.isNotEmpty(model.getDeponents())) {
			int d = 0;
			for ( FiscalModel fm : model.getDeponents().values() ) {
				deponentBox.addItem(fm.getFullName(), fm.getDocument());
				if (AonStringUtils.equals(fm.getDocument(), model.getDocument())) {
					deponentBox.setSelectedIndex( d );
				}
				d++;
			}
			deponentBox.addChangeHandler( event -> {
				Mod130 fm = model.getDeponents().get(deponentBox.getSelectedValue());
				if (fm != null) {
					model.setDocument(fm.getDocument());
					model.setSurname(fm.getSurname());
					model.setName(fm.getName());
					model.setStreetInitial(fm.getStreetInitial());
					model.setStreetName(fm.getStreetName());
					model.setStreetNumber(fm.getStreetNumber());
					model.setStreetStair(fm.getStreetStair());
					model.setStreetFloor(fm.getStreetFloor());
					model.setStreetDoor(fm.getStreetDoor());
					model.setPhone(fm.getPhone());
					model.setTown(fm.getTown());
					model.setProvince(fm.getProvince());
					model.setZip(fm.getZip());
					model.setAdmonAeat(fm.getAdmonAeat());
					model.setContactPerson(fm.getContactPerson());
					model.setContactPhone(fm.getContactPhone());
					model.setContactCellular(fm.getContactCellular());
					model.setContactEmail(fm.getContactEmail());
					model.putAmount(Mod130Key.P1, fm.getAmount(Mod130Key.P1) );
					model.setRegime(fm.getRegime() );
					model.putAmount(Mod130Key.P2, fm.getAmount(Mod130Key.P2) );
					
					documentBox.setValue(model.getDocument());				
					nameBox.setValue(model.getName());
					surnameBox.setValue(model.getSurname());
					if (model.isAEAT()) {
						percentBox.setValue( model.getAmount(Mod130Key.P1));
						regularHome.setValue( model.getAmount(Mod130Key.P2)==1 );
					}
				}
			});	
		}
		documentBox.addChangeHandler( event -> {
			model.setDocument( documentBox.getValue() );
			initialize(model, callback );
		});
		nameBox.addChangeHandler( event -> {
			model.setName( nameBox.getValue() );
		});
		surnameBox.addChangeHandler( event -> {
			model.setSurname(surnameBox.getValue() );
		});
		percentBox.addChangeHandler( event -> {
			if (percentBox.getValue() > 100) {
				percentBox.setValue(100.0);
			}
			if (percentBox.getValue() < 0) {
				percentBox.setValue(0.0);
			}
			model.putAmount(Mod130Key.P1,percentBox.getValue());
		});
		regularHome.addClickHandler( event -> {
			model.putAmount(Mod130Key.P2,regularHome.getValue().booleanValue()?1.0:0.0);
		});		
	}
	
	private void paint(Mod130 model, Model130Callback callback) {
		callback.hideError();

		registerHandlers(model,callback);
		
		headerPanel.setWidget(new AonFiscalModelHeader(model));

		rootPanel.clear();
		
		paintMessages(model,callback);
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate(model);
		tab.addRow()
			.addCell(new Label(AON.MSG.administration()),AON.CSS.aonTableLabel(),AON.CSS.aonWidth120() )
			.addCell(admonList,AON.CSS.aonWidth400());
		tab.addRow()
			.addCell(new Label(AON.MSG.year()),AON.CSS.aonTableLabel())
			.addCell(yearBox);
		tab.addRow()
			.addCell(new Label(AON.MSG.period()),AON.CSS.aonTableLabel())
			.addCell(periodList);
		if (model.isComplementaryDeclarationAvailable()) {
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(complementary);
		}
		if (model.isReplacementDeclarationAvailable() ) {
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(replacement);
		}
		tab.addRow()
			.addCell(new Label(AON.MSG.regime()),AON.CSS.aonTableLabel())
			.addCell(regimeList);
			
		if (model.getDeponents() != null && model.getDeponents().size() > 1) {
			tab.addRow()
				.addCell(new Label(AON.MSG.definedDeponents()),AON.CSS.aonTableLabel())
				.addCell(deponentBox);
		}
		tab.addRow()
			.addCell(new Label(AON.MSG.document()),AON.CSS.aonTableLabel())
			.addCell(documentBox);
		tab.addRow()
			.addCell(new Label(AON.MSG.name()),AON.CSS.aonTableLabel())
			.addCell(nameBox);
		tab.addRow()
			.addCell(new Label(AON.MSG.surname()),AON.CSS.aonTableLabel())
			.addCell(surnameBox);
		tab.addRow()
			.addCell(new Label(AON.MSG.partPercent()),AON.CSS.aonTableLabel())
			.addCell(percentBox);
		tab.addRow()
			.addCell(new Label(),AON.CSS.aonTableLabel())
			.addCell(regularHome);
		
		rootPanel.add(getButtonsPanel(model,callback));
	}

	private void paintMessages(Mod130 model, Model130Callback callback) {
		if (model.getMessages() != null && !model.getMessages().isEmpty()) {
			FlowPanel messages = new FlowPanel();
			messages.setStyleName( AON.CSS.aonTextCenter() );
			messages.addStyleName( AON.CSS.aonMarginBottom() );
			messages.addStyleName( AON.CSS.aonBorder());
			messages.addStyleName( AON.CSS.aonPadding());
			messages.addStyleName( AON.CSS.aonBackgroundHighlightedOrange());
			for (String msg : model.getMessages()) {
				Label message = new Label(msg);
				message.setStyleName(AON.CSS.aonLabelWithIcon());
				message.addStyleName(AON.CSS.aonIconWarning());
				message.addStyleName(AON.CSS.aonBold());
				messages.add(message);
			}
			rootPanel.add(messages);
		}
		
	}
	
	private FlowPanel getButtonsPanel(Mod130 model, final Model130Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			callback.onAccept(model);
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> callback.onCancel(model));
		buttonsPanel.add(cancelButton);
		return buttonsPanel;
	}
	
	private void populate(Mod130 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		regimeList.setSelectedIndex((model.getRegime() == IRPFRegime.SIMPLIFIED)?1:0);
		documentBox.setValue(model.getDocument());
		nameBox.setValue(model.getName());
		surnameBox.setValue(model.getSurname());
		percentBox.setValue( model.getAmount(Mod130Key.P1));
		regularHome.setValue( model.getAmount(Mod130Key.P2)==1);
	}
		
	private void initialize(Mod130 model, Model130Callback callback) {
		Model130.SERVICE.initialize(callback.getOptions().getOccam(),model,
			new AsyncCallback<Mod130>() {
				@Override
				public void onSuccess(Mod130 m130) {
					paint(m130,callback);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					callback.showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
				}
			}
		);
	}
}
