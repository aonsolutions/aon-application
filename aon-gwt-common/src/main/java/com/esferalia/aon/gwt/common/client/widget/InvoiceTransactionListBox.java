package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceTransactionListBox extends ListBox {

	public InvoiceTransactionListBox() {
		this("------", false);
	}	
	
	public InvoiceTransactionListBox(boolean forCanarias) {
		this("------", forCanarias);
	}	

	public InvoiceTransactionListBox(String firstItemLabel) {
		this(firstItemLabel, false);
	}
	
	public InvoiceTransactionListBox(String firstItemLabel, boolean forCanarias) {
		setWidth("120px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		int i = 1;
		for (InvoiceTransactionType d : InvoiceTransactionType.values()) {
			String desc = d.getDescription(); 
			if ( forCanarias ) {
				desc = AonStringUtils.remove(desc, "Canarias, ");
			}
			addItem(desc);
			if ( forCanarias && d == InvoiceTransactionType.INTRACOMMUNITY ) {
					getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled", "disabled");
			}
			i++;
		}
	}

	public void setValue(InvoiceTransactionType transaction) {
		setSelectedIndex(transaction==null?0:transaction.ordinal()+1);
	}

	public InvoiceTransactionType getValue() {
		return getSelectedIndex()==0?null:InvoiceTransactionType.values()[getSelectedIndex()-1];
	}
	
}

