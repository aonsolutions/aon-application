package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

@Deprecated
public class OLDModel130NewDeclarationPopup extends NewDeclarationPopup<Mod130,Model130ModuleOptions>{
	
	public OLDModel130NewDeclarationPopup(Mod130 mod130, Model130Callback callback) {
		super(mod130, callback);
	}

	@Override
	protected void paintAdministration(Mod130 mod130) {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));

		admonList.setSelectedIndex( mod130.getAdministration().ordinal());
		admonList.addChangeHandler( event -> {
			mod130.setAdministration( admonList.getValue() );
			replacement.setVisible(mod130.isReplacementDeclarationAvailable());
			complementary.setVisible(mod130.isComplementaryDeclarationAvailable());
			previousLabel.setVisible(mod130.isReplacedNumberAvailable());
			previous.setVisible(mod130.isReplacedNumberAvailable());
		});
		tab.setWidget(row, 1, admonList);
		row++;
	}
	
	@Override
	protected void paintModelSpecificPanel(Mod130 mod130) {
		final ListBox deponentBox = new ListBox();
		final AonDocumentTextBox documentBox = new AonDocumentTextBox();
		final Label nameLabel = new Label( AON.MSG.nameCompanyName());
		final AonTextBox nameBox = new AonTextBox();
		final Label surnameLabel = new Label( AON.MSG.surname());
		final AonTextBox surnameBox = new AonTextBox();
		final Label percentLabel = new Label(AON.MSG.partPercent());
		final AonDoubleBox percentBox = new AonDoubleBox();
		final CheckBox regularHome = new CheckBox();
		final Label regimeLabel = new Label(AON.MSG.regime());
		final ListBox regimeList = new ListBox();
		
		admonList.addChangeHandler( event -> {
			regimeLabel.setVisible(mod130.isAEAT());
			regimeList.setVisible(mod130.isAEAT());
			percentLabel.setVisible(mod130.isAEAT());
			percentBox.setVisible(mod130.isAEAT());
			regularHome.setVisible(mod130.isAEAT());
		});
	
		regimeLabel.setVisible(mod130.isAEAT());
		regimeList.setVisible(mod130.isAEAT());
		percentLabel.setVisible(mod130.isAEAT());
		percentBox.setVisible(mod130.isAEAT());
		regularHome.setVisible(mod130.isAEAT());

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, regimeLabel);
		regimeList.addItem(IRPFRegime.NORMAL.getName());
		regimeList.addItem(IRPFRegime.SIMPLIFIED.getName());
		regimeList.setSelectedIndex((mod130.getRegime() == IRPFRegime.SIMPLIFIED)?1:0);
		regimeList.addChangeHandler(event -> mod130.setRegime(
				regimeList.getSelectedIndex() == 1?
						IRPFRegime.SIMPLIFIED:IRPFRegime.NORMAL
				));
		tab.setWidget(row, 1, regimeList);
		row++;
		
		if (mod130.getDeponents() != null && mod130.getDeponents().size() > 1) {
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
			tab.setWidget(row, 0, new Label(AON.MSG.deponents()));
			tab.setWidget(row, 1, deponentBox);
			row++;
			int d = 0;
			for ( FiscalModel fm : mod130.getDeponents().values() ) {
				deponentBox.addItem(fm.getFullName(), fm.getDocument());
				if (AonStringUtils.equals(fm.getDocument(), mod130.getDocument())) {
					deponentBox.setSelectedIndex( d );
				}
				d++;
			}
			deponentBox.addChangeHandler( event -> {
				Mod130 fm = mod130.getDeponents().get(deponentBox.getSelectedValue());
				if (fm != null) {
					mod130.setDocument(fm.getDocument());
					mod130.setSurname(fm.getSurname());
					mod130.setName(fm.getName());
					mod130.setStreetInitial(fm.getStreetInitial());
					mod130.setStreetName(fm.getStreetName());
					mod130.setStreetNumber(fm.getStreetNumber());
					mod130.setStreetStair(fm.getStreetStair());
					mod130.setStreetFloor(fm.getStreetFloor());
					mod130.setStreetDoor(fm.getStreetDoor());
					mod130.setPhone(fm.getPhone());
					mod130.setTown(fm.getTown());
					mod130.setProvince(fm.getProvince());
					mod130.setZip(fm.getZip());
					mod130.setAdmonAeat(fm.getAdmonAeat());
					mod130.setContactPerson(fm.getContactPerson());
					mod130.setContactPhone(fm.getContactPhone());
					mod130.setContactCellular(fm.getContactCellular());
					mod130.setContactEmail(fm.getContactEmail());
					mod130.putAmount(Mod130Key.P1, fm.getAmount(Mod130Key.P1) );
					mod130.setRegime(fm.getRegime() );
					mod130.putAmount(Mod130Key.P2, fm.getAmount(Mod130Key.P2) );
					
					documentBox.setValue(mod130.getDocument());				
					nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
							?AON.MSG.nameCompanyName()
							:AON.MSG.name());
					nameBox.setValue(mod130.getName());
					surnameBox.setValue(mod130.getSurname());
					if (mod130.isAEAT()) {
						percentBox.setValue( mod130.getAmount(Mod130Key.P1));
						regularHome.setValue( mod130.getAmount(Mod130Key.P2)==1 );
					}
				}
			});	
		}

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.document()));
		documentBox.setValue(mod130.getDocument());
		documentBox.addChangeHandler( event -> mod130.setDocument( documentBox.getValue() ));
		tab.setWidget(row, 1, documentBox);
		row++;
		
		nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
				?AON.MSG.nameCompanyName()
				:AON.MSG.name());
		nameBox.setValue(mod130.getName());		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, nameLabel);
		nameBox.addChangeHandler( event -> mod130.setName( nameBox.getValue() ));
		tab.setWidget(row, 1, nameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, surnameLabel);
		surnameBox.setValue(mod130.getSurname());
		surnameBox.addChangeHandler( event -> mod130.setSurname(surnameBox.getValue() ));
		tab.setWidget(row, 1, surnameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, percentLabel );
		percentBox.setValue( mod130.getAmount(Mod130Key.P1));
		percentBox.addChangeHandler( event -> {
			if (percentBox.getValue() > 100) {
				percentBox.setValue(100.0);
			}
			if (percentBox.getValue() < 0) {
				percentBox.setValue(0.0);
			}
			mod130.putAmount(Mod130Key.P1,percentBox.getValue());
		});
		tab.setWidget(row, 1, percentBox);
		row++;
		
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		regularHome.setValue( mod130.getAmount(Mod130Key.P2)==1);
		regularHome.setText(AON.MSG.regularHomePayments());
		regularHome.addClickHandler( event -> mod130.putAmount(Mod130Key.P2,regularHome.getValue().booleanValue()?1.0:0.0));
		tab.setWidget(row, 0, regularHome);
	}

}
