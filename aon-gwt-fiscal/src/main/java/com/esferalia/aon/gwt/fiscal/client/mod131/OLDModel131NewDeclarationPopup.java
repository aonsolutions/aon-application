package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class OLDModel131NewDeclarationPopup extends NewDeclarationPopup<Mod131,Model131ModuleOptions>{

	public OLDModel131NewDeclarationPopup(Mod131 mod131,Model131Callback callback) {
		super(mod131,callback);
	}
	
	@Override
	protected void paintAdministration(Mod131 mod131) {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		mod131.setAdministration( Administration.COMMON_TERRITORY );
		replacement.setVisible(mod131.isReplacementDeclarationAvailable());
		complementary.setVisible(mod131.isComplementaryDeclarationAvailable());
		previousLabel.setVisible(mod131.isReplacedNumberAvailable());
		previous.setVisible(mod131.isReplacedNumberAvailable());
		tab.setWidget(row, 1, new Label(Administration.COMMON_TERRITORY.getDescription()));
		row++;
	}
	
	@Override
	protected void paintModelSpecificPanel(Mod131 mod131) {
		final ListBox deponentBox = new ListBox();
		final AonDocumentTextBox documentBox = new AonDocumentTextBox();
		final Label nameLabel = new Label( AON.MSG.nameCompanyName());
		final AonTextBox nameBox = new AonTextBox();
		final Label surnameLabel = new Label( AON.MSG.surname());
		final AonTextBox surnameBox = new AonTextBox();
		final CheckBox regularHome = new CheckBox();
		
		if (mod131.getDeponents() != null && mod131.getDeponents().size() > 1) {
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
			tab.setWidget(row, 0, new Label(AON.MSG.deponents()));
			tab.setWidget(row, 1, deponentBox);
			row++;
			int d = 0;
			for ( FiscalModel fm : mod131.getDeponents().values() ) {
				deponentBox.addItem(fm.getFullName(), fm.getDocument());
				if (AonStringUtils.equals(fm.getDocument(), mod131.getDocument())) {
					deponentBox.setSelectedIndex( d );
				}
				d++;
			}
			deponentBox.addChangeHandler( event -> {
				Mod131 fm = mod131.getDeponents().get(deponentBox.getSelectedValue());
				if (fm != null) {
					mod131.setDocument(fm.getDocument());
					mod131.setSurname(fm.getSurname());
					mod131.setName(fm.getName());
					mod131.setStreetInitial(fm.getStreetInitial());
					mod131.setStreetName(fm.getStreetName());
					mod131.setStreetNumber(fm.getStreetNumber());
					mod131.setStreetStair(fm.getStreetStair());
					mod131.setStreetFloor(fm.getStreetFloor());
					mod131.setStreetDoor(fm.getStreetDoor());
					mod131.setPhone(fm.getPhone());
					mod131.setTown(fm.getTown());
					mod131.setProvince(fm.getProvince());
					mod131.setZip(fm.getZip());
					mod131.setAdmonAeat(fm.getAdmonAeat());
					mod131.setContactPerson(fm.getContactPerson());
					mod131.setContactPhone(fm.getContactPhone());
					mod131.setContactCellular(fm.getContactCellular());
					mod131.setContactEmail(fm.getContactEmail());
					mod131.putAmount(Mod131Key.P2, fm.getAmount(Mod130Key.P2) );
					
					documentBox.setValue(mod131.getDocument());				
					nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
							?AON.MSG.nameCompanyName()
							:AON.MSG.name());
					nameBox.setValue(mod131.getName());
					surnameBox.setValue(mod131.getSurname());
					regularHome.setValue( AonNumberUtils.equals( mod131.getAmount(Mod130Key.P2) , 1));
				}
			});	
		}

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.document()));
		documentBox.setValue(mod131.getDocument());
		documentBox.addChangeHandler( event -> mod131.setDocument( documentBox.getValue() ));
		tab.setWidget(row, 1, documentBox);
		row++;
		
		nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
				?AON.MSG.nameCompanyName()
				:AON.MSG.name());
		nameBox.setValue(mod131.getName());		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, nameLabel);
		nameBox.addChangeHandler( event -> mod131.setName( nameBox.getValue() ));
		tab.setWidget(row, 1, nameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, surnameLabel);
		surnameBox.setValue(mod131.getSurname());
		surnameBox.addChangeHandler( event -> mod131.setSurname(surnameBox.getValue() ));
		tab.setWidget(row, 1, surnameBox);
		row++;
		
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		regularHome.setValue( AonNumberUtils.equals(mod131.getAmount(Mod131Key.P2) , 1 ));
		regularHome.setText(AON.MSG.regularHomePayments());
		regularHome.addClickHandler( event -> mod131.putAmount(Mod131Key.P2,regularHome.getValue().booleanValue()?1.0:0.0));
		tab.setWidget(row, 0, regularHome);
	}

}
