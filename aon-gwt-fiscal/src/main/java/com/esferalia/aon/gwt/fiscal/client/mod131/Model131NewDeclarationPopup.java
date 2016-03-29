package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
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

public class Model131NewDeclarationPopup extends NewDeclarationPopup<Mod131>{

	public Model131NewDeclarationPopup(IFiscalModelCallback<Mod131> callback) {
		super(callback);
	}
	
	@Override
	protected void paintAdministration() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		callback.getFiscalModel().setAdministration( Administration.COMMON_TERRITORY );
		replacement.setVisible(callback.getFiscalModel().isReplacementDeclarationAvailable());
		complementary.setVisible(callback.getFiscalModel().isComplementaryDeclarationAvailable());
		previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		tab.setWidget(row, 1, new Label(Administration.COMMON_TERRITORY.getDescription()));
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
		final CheckBox regularHome = new CheckBox();
		
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
					Mod131 fm = callback.getFiscalModel().getDeponents().get(deponentBox.getSelectedValue());
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
						callback.getFiscalModel().putAmount(Mod131Key.P2, fm.getAmount(Mod130Key.P2) );
						
						documentBox.setValue(callback.getFiscalModel().getDocument());				
						nameLabel.setText((AonDocumentUtil.isEntity( documentBox.getValue() ))
								?AON.MSG.nameCompanyName()
								:AON.MSG.name());
						nameBox.setValue(callback.getFiscalModel().getName());
						surnameBox.setValue(callback.getFiscalModel().getSurname());
						regularHome.setValue( callback.getFiscalModel().getAmount(Mod130Key.P2)==1?true:false );
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
		
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		regularHome.setValue( callback.getFiscalModel().getAmount(Mod131Key.P2)==1?true:false );
		regularHome.setText(AON.MSG.regularHomePayments());
		regularHome.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().putAmount(Mod131Key.P2,regularHome.getValue()?1.0:0.0);
			}
		});
		tab.setWidget(row, 0, regularHome);
	}

}
