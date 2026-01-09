package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.Widget;

class InvoiceRegistrySuggestionDisplay extends DefaultSuggestionDisplay {
	
    private Widget suggestionMenu;

    @Override
    protected Widget decorateSuggestionList(Widget suggestionList) {
        suggestionMenu = suggestionList;
        return suggestionList;
    }
    
    @Override
	protected void moveSelectionDown() {
		super.moveSelectionDown();
		scrollSelectedItemIntoView();
	}

	@Override
	protected void moveSelectionUp() {
		super.moveSelectionUp();
		scrollSelectedItemIntoView();
	}
	
    private void scrollSelectedItemIntoView() {
        NodeList<Node> trList = suggestionMenu.getElement().getChild(1).getChild(0).getChildNodes();
        for (int trIndex = 0; trIndex < trList.getLength(); ++trIndex) {
            Element trElement = (Element) trList.getItem(trIndex);
            if (((Element)trElement.getChild(0)).getClassName().contains("selected")) {
                trElement.scrollIntoView();
                break;
            }
        }
    }
}
