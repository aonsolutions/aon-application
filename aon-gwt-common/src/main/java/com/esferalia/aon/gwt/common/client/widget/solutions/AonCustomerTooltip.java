package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AonCustomerTooltip extends DecoratedPopupPanel {

	private HTMLPanel mainContainer;
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private List<User> users = new ArrayList<>();
	
	public AonCustomerTooltip() {
		mainContainer = new HTMLPanel("");
		mainContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		scrollPanel = new ScrollPanel();
		scrollPanel.getElement().getStyle().setProperty("max-height", "300px");
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		
		scrollPanel.add(container);
		
		mainContainer.add(scrollPanel);
		
		this.add(mainContainer);
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	private final native void copyToClipboard(String text) /*-{
		 var textField = $doc.createElement('textarea');
        textField.value = text;
        $doc.body.appendChild(textField);
        textField.select();
        $doc.execCommand('copy');
        $doc.body.removeChild(textField);
	}-*/;
	
	public void showTooltip(final int clientX, final int clientY) {
		fillUserList();
		showToolTip(clientX, clientY);
	}	
	
	private void fillUserList() {
		container.clear();
		
		this.users.forEach(user -> {
			HTMLPanel row = new HTMLPanel("");
			row.addStyleName(AON.CSS.aonItemFlex());
			
			Label name = new Label("(" + user.getLogin() + ") " + user.getName());
			
			AonTableButton copy = new AonTableButton("Copiar login", AON.CSS.aonIconCopy());
			copy.addClickHandler(e -> {
				copyToClipboard(user.getLogin());
				hide();
			});
			
			row.add(copy);
			row.add(name);
			
			container.add(row);
		});
	}

	private void showToolTip(final int clientX, final int clientY) {

		try {

			setPopupPositionAndShow(new PopupPanel.PositionCallback() {

				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {

					int windowWidth = Window.getClientWidth();
					int popupX = clientX - offsetWidth / 3;
					int popupY = clientY;

					if (popupX + offsetWidth >= windowWidth - offsetWidth/2)
						popupX -= popupX + offsetWidth*1.2 - windowWidth;

					if (clientY + offsetHeight >= Window.getClientHeight()) {
						popupY = popupY - offsetHeight;
					}

					setPopupPosition(popupX, popupY);
				}
			});

			show();

		} catch (Throwable ex) {
			Window.alert("Error " + ex.getStackTrace() + " " + ex.getMessage());
		}
	}
	
}
