package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
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
		final DocumentTextBox documentBox = new DocumentTextBox();
		final Label nameLabel = new Label( AON.MSG.nameCompanyName());
		final TextBox nameBox = new TextBox();
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		final Label surnameLabel = new Label( AON.MSG.surname());
		final TextBox surnameBox = new TextBox();
		surnameBox.setStyleName(AON.AON_CSS.aonInputText());
		final DoubleBox percentBox = new DoubleBox();
		final CheckBox regularHome = new CheckBox();
		
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
		tab.setWidget(row, 0, new Label(AON.MSG.partPercent()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		percentBox.setValue( callback.getFiscalModel().getAmount(Mod131Key.P1));
		percentBox.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (percentBox.getValue() > 100) {
					percentBox.setValue(100.0);
				}
				if (percentBox.getValue() < 0) {
					percentBox.setValue(0.0);
				}
				callback.getFiscalModel().putAmount(Mod131Key.P1,percentBox.getValue());
			}
		});
		tab.setWidget(row, 1, percentBox);
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
