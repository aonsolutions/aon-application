package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class Model130NewDeclarationPopup extends NewDeclarationPopup<Mod130>{

	public Model130NewDeclarationPopup(IFiscalModelCallback<Mod130> callback) {
		super(callback);
	}
	
	@Override
	protected void paintAdministration() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());

		admonList.setSelectedIndex( callback.getFiscalModel().getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setAdministration( admonList.getValue() );
				replacement.setVisible(callback.getFiscalModel().isReplacementDeclarationAvailable());
				complementary.setVisible(callback.getFiscalModel().isComplementaryDeclarationAvailable());
				previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;
	}
	
	@Override
	protected void paintModelSpecificPanel() {
		final ListBox deponentBox = new ListBox();
		final DocumentTextBox documentBox = new DocumentTextBox();
		final Label nameLabel = new Label( AON.MSG.nameCompanyName());
		final TextBox nameBox = new TextBox();
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		final Label surnameLabel = new Label( AON.MSG.surname());
		final TextBox surnameBox = new TextBox();
		surnameBox.setStyleName(AON.AON_CSS.aonInputText());
		final Label percentLabel = new Label(AON.MSG.partPercent());
		final DoubleBox percentBox = new DoubleBox();
		final CheckBox regularHome = new CheckBox();
		final Label regimeLabel = new Label(AON.MSG.regime());
		final ListBox regimeList = new ListBox();
		
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				regimeLabel.setVisible(callback.getFiscalModel().isAEAT());
				regimeList.setVisible(callback.getFiscalModel().isAEAT());
				percentLabel.setVisible(callback.getFiscalModel().isAEAT());
				percentBox.setVisible(callback.getFiscalModel().isAEAT());
				regularHome.setVisible(callback.getFiscalModel().isAEAT());
			}
		});
	
		regimeLabel.setVisible(callback.getFiscalModel().isAEAT());
		regimeList.setVisible(callback.getFiscalModel().isAEAT());
		percentLabel.setVisible(callback.getFiscalModel().isAEAT());
		percentBox.setVisible(callback.getFiscalModel().isAEAT());
		regularHome.setVisible(callback.getFiscalModel().isAEAT());

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, regimeLabel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		regimeList.addItem(IRPFRegime.NORMAL.getName());
		regimeList.addItem(IRPFRegime.SIMPLIFIED.getName());
		regimeList.setSelectedIndex((callback.getFiscalModel().getRegime() == IRPFRegime.SIMPLIFIED)?1:0);
		regimeList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setRegime(
						regimeList.getSelectedIndex() == 1?
								IRPFRegime.SIMPLIFIED:IRPFRegime.NORMAL
						);
			}
		});
		tab.setWidget(row, 1, regimeList);
		row++;
		
		if (callback.getFiscalModel().getDeponents() != null && callback.getFiscalModel().getDeponents().size() > 1) {
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.deponents()));
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			tab.setWidget(row, 1, deponentBox);
			row++;
			int d = 0;
			for ( FiscalModel fm : callback.getFiscalModel().getDeponents().values() ) {
				deponentBox.addItem(fm.getFullName(), fm.getDocument());
				if (AonStringUtils.equals(fm.getDocument(), callback.getFiscalModel().getDocument())) {
					deponentBox.setSelectedIndex( d );
				}
				d++;
			}
			deponentBox.addChangeHandler( new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					Mod130 fm = callback.getFiscalModel().getDeponents().get(deponentBox.getSelectedValue());
					if (fm != null) {
						callback.getFiscalModel().setDocument(fm.getDocument());
						callback.getFiscalModel().setSurname(fm.getSurname());
						callback.getFiscalModel().setName(fm.getName());
						callback.getFiscalModel().setStreetInitial(fm.getStreetInitial());
						callback.getFiscalModel().setStreetName(fm.getStreetName());
						callback.getFiscalModel().setStreetNumber(fm.getStreetNumber());
						callback.getFiscalModel().setStreetStair(fm.getStreetStair());
						callback.getFiscalModel().setStreetFloor(fm.getStreetFloor());
						callback.getFiscalModel().setStreetDoor(fm.getStreetDoor());
						callback.getFiscalModel().setPhone(fm.getPhone());
						callback.getFiscalModel().setTown(fm.getTown());
						callback.getFiscalModel().setProvince(fm.getProvince());
						callback.getFiscalModel().setZip(fm.getZip());
						callback.getFiscalModel().setAdmonAeat(fm.getAdmonAeat());
						callback.getFiscalModel().setContactPerson(fm.getContactPerson());
						callback.getFiscalModel().setContactPhone(fm.getContactPhone());
						callback.getFiscalModel().setContactCellular(fm.getContactCellular());
						callback.getFiscalModel().setContactEmail(fm.getContactEmail());
						callback.getFiscalModel().putAmount(Mod130Key.P1, fm.getAmount(Mod130Key.P1) );
						callback.getFiscalModel().setRegime(fm.getRegime() );
						callback.getFiscalModel().putAmount(Mod130Key.P2, fm.getAmount(Mod130Key.P2) );
						
						documentBox.setValue(callback.getFiscalModel().getDocument());				
						nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
								?AON.MSG.nameCompanyName()
								:AON.MSG.name());
						nameBox.setValue(callback.getFiscalModel().getName());
						surnameBox.setValue(callback.getFiscalModel().getSurname());
						if (callback.getFiscalModel().isAEAT()) {
							percentBox.setValue( callback.getFiscalModel().getAmount(Mod130Key.P1));
							regularHome.setValue( callback.getFiscalModel().getAmount(Mod130Key.P2)==1?true:false );
						}
					}
				}
			});	
		}

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.document()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		documentBox.setValue(callback.getFiscalModel().getDocument());
		documentBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setDocument( documentBox.getValue() );
			}
		});
		tab.setWidget(row, 1, documentBox);
		row++;
		
		nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
				?AON.MSG.nameCompanyName()
				:AON.MSG.name());
		nameBox.setValue(callback.getFiscalModel().getName());		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, nameLabel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		nameBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setName( nameBox.getValue() );
			}
		});
		tab.setWidget(row, 1, nameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, surnameLabel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		surnameBox.setValue(callback.getFiscalModel().getSurname());
		surnameBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setSurname(surnameBox.getValue() );
			}
		});
		tab.setWidget(row, 1, surnameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, percentLabel );
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		percentBox.setValue( callback.getFiscalModel().getAmount(Mod130Key.P1));
		percentBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (percentBox.getValue() > 100) {
					percentBox.setValue(100.0);
				}
				if (percentBox.getValue() < 0) {
					percentBox.setValue(0.0);
				}
				callback.getFiscalModel().putAmount(Mod130Key.P1,percentBox.getValue());
			}
		});
		tab.setWidget(row, 1, percentBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		regularHome.setValue( callback.getFiscalModel().getAmount(Mod130Key.P2)==1?true:false );
		regularHome.setText(AON.MSG.regularHomePayments());
		regularHome.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().putAmount(Mod130Key.P2,regularHome.getValue()?1.0:0.0);
			}
		});
		tab.setWidget(row, 0, regularHome);
	}

}
