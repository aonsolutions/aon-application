package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
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

public class Model131NewDeclarationPopup extends NewDeclarationPopup<Mod131,Model131ModuleOptions>{

	public Model131NewDeclarationPopup(Mod131 mod131,Model131Callback callback) {
		super(mod131,false,callback);
	public Model131NewDeclarationPopup(Mod131 mod131,boolean reset, IFiscalModelCallback<Mod131> callback) {
		super(reset, callback);
	}
	
	@Override
	protected void paintAdministration(Mod131 mod131) {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
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
		final DocumentTextBox documentBox = new DocumentTextBox();
		final Label nameLabel = new Label( AON.MSG.nameCompanyName());
		final TextBox nameBox = new TextBox();
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		final Label surnameLabel = new Label( AON.MSG.surname());
		final TextBox surnameBox = new TextBox();
		surnameBox.setStyleName(AON.AON_CSS.aonInputText());
		final CheckBox regularHome = new CheckBox();
		
		if (mod131.getDeponents() != null && mod131.getDeponents().size() > 1) {
			tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.deponents()));
			tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
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
			deponentBox.addChangeHandler( new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
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
						regularHome.setValue( mod131.getAmount(Mod130Key.P2)==1?true:false );
					}
				}
			});	
		}

		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.document()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		documentBox.setValue(mod131.getDocument());
		documentBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod131.setDocument( documentBox.getValue() );
			}
		});
		tab.setWidget(row, 1, documentBox);
		row++;
		
		nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
				?AON.MSG.nameCompanyName()
				:AON.MSG.name());
		nameBox.setValue(mod131.getName());		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, nameLabel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		nameBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod131.setName( nameBox.getValue() );
			}
		});
		tab.setWidget(row, 1, nameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, surnameLabel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		surnameBox.setValue(mod131.getSurname());
		surnameBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod131.setSurname(surnameBox.getValue() );
			}
		});
		tab.setWidget(row, 1, surnameBox);
		row++;
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		regularHome.setValue( mod131.getAmount(Mod131Key.P2)==1?true:false );
		regularHome.setText(AON.MSG.regularHomePayments());
		regularHome.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mod131.putAmount(Mod131Key.P2,regularHome.getValue()?1.0:0.0);
			}
		});
		tab.setWidget(row, 0, regularHome);
	}

}
