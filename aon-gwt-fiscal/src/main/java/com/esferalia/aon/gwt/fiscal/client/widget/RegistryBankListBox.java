package com.esferalia.aon.gwt.fiscal.client.widget;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class RegistryBankListBox extends ListBox {

	private LinkedList<RegistryBank> banks;
	
	public RegistryBankListBox() {
		
	}
	
	public RegistryBankListBox( LinkedList<RegistryBank> banks) {
		setBanks( banks );
	}

	public void setBanks(LinkedList<RegistryBank> banks) {
		this.banks = banks;
		clear();
		addItem("----");
		if (banks != null) {
			for (RegistryBank bank : banks ) {
				addItem((AonStringUtils.isNotBlank(bank.getAlias())
					?("("+bank.getAlias()+") - ")
					:AonStringUtils.EMPTY)
					+ bank.getBankAccount()
					, AonNumberUtils.toString( bank.getId()));	
			}
		}
	}

	public void setValue(String bankAccount ) {
		setSelectedIndex(0);
		if (banks != null) {
			int i = 1;
			for (RegistryBank bank : banks ) {
				if (AonStringUtils.equals(bank.getBankAccount(), bankAccount)) {
					setSelectedIndex(i);
				}
			}
		}
	}
	
	public RegistryBank getValue() {
		int i = getSelectedIndex() - 1;
		if ( i>=0) {
			return banks.get(i);
		}
		return null;
	}
	
}
