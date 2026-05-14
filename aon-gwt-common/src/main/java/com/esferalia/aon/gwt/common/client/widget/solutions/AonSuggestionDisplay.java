package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.Widget;

public class AonSuggestionDisplay extends DefaultSuggestionDisplay {
	
	private static final int Z_INDEX = 4;
	private Widget suggestionMenu;
    
    public AonSuggestionDisplay() {
    	super();
	}
    
    @Override
    protected PopupPanel createPopup() {
    	PopupPanel popupPanel = super.createPopup();
    	popupPanel.setStyleName(AON.CSS.aonSuggestBoxPopup());
    	popupPanel.getElement().getStyle().setZIndex(Z_INDEX);
    	popupPanel.getElement().getStyle().setProperty("min-width", "250px");
    	return popupPanel;
    }
    
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
            Element trElement = (Element)trList.getItem(trIndex);
            if (((Element)trElement.getChild(0)).getClassName().contains("selected")) {
                trElement.scrollIntoView();
                trElement.setScrollLeft(0);
                getPopupPanel().getElement().setScrollLeft(0);
                break;
            }
        }
    }
    
    public void hideSuggestionList() {
    	this.hideSuggestions();
    }
}
