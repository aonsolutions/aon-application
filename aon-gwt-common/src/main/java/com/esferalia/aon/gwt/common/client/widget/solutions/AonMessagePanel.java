package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class AonMessagePanel extends FlowPanel {
	
    	// ------------------------------------------------ Variables
	
	private HTMLPanel closePanel;
	private HTMLPanel messagePanel;
	private static Panel panel;
	
	// ------------------------------------------------ Constructor
	
	protected AonMessagePanel() {
		super();
		this.addStyleName(AON.CSS.aonMessagePanel());
		createMessagePanel();
		createClosePanel();
	}
	
	protected void createMessagePanel() {
		messagePanel = new HTMLPanel("");
		messagePanel.addStyleName(AON.CSS.aonMessageContainer());
		add(messagePanel);
	}

	protected void createClosePanel() {
		closePanel = new HTMLPanel("");
		closePanel.addStyleName(AON.CSS.aonMessageClosePanel());
		
		AonButton closeButton = new AonButton("Cerrar", AON.CSS.aonIconCloseWhite());
		closeButton.addClickHandler(e -> {
			fadeOut(panel, this);
		});
		closeButton.addStyleName(AON.CSS.aonMessageClose());
		closePanel.add(closeButton);
		add(closePanel);
	}
	
	// ------------------------------------------------ AonMessagePanel static
	
	private static AonMessagePanel getMainPanel() {
		return new AonMessagePanel();
	}
	
	// ------------------------------------------------ Show Error
	
	public static <T> void showError(Panel panelIn, T ...messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonErrorMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static <T> void showError(Panel panelIn, Map<String, T> messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonErrorMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static void showError(Panel panelIn, Widget errorWidget) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonErrorMessagePanel();
		addMessageWidget(aonMessagePanel, errorWidget);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static void showError(Panel panelIn, String html) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonErrorMessagePanel();
		addMessageWidget(aonMessagePanel, new HTMLPanel(html));
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	private static AonMessagePanel getAonErrorMessagePanel() {
		AonMessagePanel aonMessagePanel = getMainPanel();
		addErrorStyle(aonMessagePanel);
		showClosePanel(aonMessagePanel, true);
		return aonMessagePanel;
	}
	
	// ------------------------------------------------ Show Info
	
	
	public static <T> void showInfo(Panel panelIn, T ...messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonInfoMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanelTimerLong(panel, aonMessagePanel);
	}
	
	public static <T> void showInfo(Panel panelIn, Map<String, T> messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonInfoMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanelTimerLong(panel, aonMessagePanel);
	}
	
	public static void showInfo(Panel panelIn, Widget errorWidget) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonInfoMessagePanel();
		addMessageWidget(aonMessagePanel, errorWidget);
		showAndAddMainPanelTimerLong(panel, aonMessagePanel);
	}
	
	public static void showInfo(Panel panelIn, String html) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonInfoMessagePanel();
		addMessageWidget(aonMessagePanel, new HTMLPanel(html));
		showAndAddMainPanelTimerLong(panel, aonMessagePanel);
	}
	
	private static AonMessagePanel getAonInfoMessagePanel() {
		AonMessagePanel aonMessagePanel = getMainPanel();
		addInfoStyle(aonMessagePanel);
		showClosePanel(aonMessagePanel, false);
		return aonMessagePanel;
	}
	
	// ------------------------------------------------ Show Warning
	
	public static <T> void showWarning(Panel panelIn, T ...messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonWarninMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static <T> void showWarning(Panel panelIn, Map<String, T> messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonWarninMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static void showWarning(Panel panelIn, Widget errorWidget) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonWarninMessagePanel();
		addMessageWidget(aonMessagePanel, errorWidget);
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	public static void showWarning(Panel panelIn, String html) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonWarninMessagePanel();
		addMessageWidget(aonMessagePanel, new HTMLPanel(html));
		showAndAddMainPanel(panel, aonMessagePanel);
	}
	
	private static AonMessagePanel getAonWarninMessagePanel() {
		AonMessagePanel aonMessagePanel = getMainPanel();
		addWarningStyle(aonMessagePanel);
		showClosePanel(aonMessagePanel, true);
		return aonMessagePanel;
	}
	
	// ------------------------------------------------ Show Success
	
	public static <T> void showSuccess(Panel panelIn, T ...messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonSuccessMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanelTimer(panel, aonMessagePanel);
	}

	public static <T> void showSuccess(Panel panelIn, Map<String, T> messages) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonSuccessMessagePanel();
		fillMessages(aonMessagePanel, messages);
		showAndAddMainPanelTimer(panel, aonMessagePanel);
	}
	
	public static void showSuccess(Panel panelIn, Widget errorWidget) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonSuccessMessagePanel();
		addMessageWidget(aonMessagePanel, errorWidget);
		showAndAddMainPanelTimer(panel, aonMessagePanel);
	}
	
	public static void showSuccess(Panel panelIn, String html) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonSuccessMessagePanel();
		addMessageWidget(aonMessagePanel, new HTMLPanel(html));
		showAndAddMainPanelTimer(panel, aonMessagePanel);
	}
	
	private static AonMessagePanel getAonSuccessMessagePanel() {
		AonMessagePanel aonMessagePanel = getMainPanel();
		addSuccessStyle(aonMessagePanel);
		showClosePanel(aonMessagePanel, false);
		return aonMessagePanel;
	}
	
	// ------------------------------------------------ Show Loading
	
	public static <T> Label showLoading(Panel panelIn, T message) {
		panel = panelIn;
		clearEntryPanel(panel);
		AonMessagePanel aonMessagePanel = getAonInfoMessagePanel();
		Label meesageLabel = fillLoadingMessages(aonMessagePanel, message);
		showAndAddMainPanel(panel, aonMessagePanel);
		return meesageLabel;
	}
	
	// ------------------------------------------------ Hide Message
	
	public static void hideMessage(Panel panel) {
		clearEntryPanel(panel);
		panel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------------ Auxiliar Methods
	
	private static void clearEntryPanel(Panel panel) {
		panel.clear();
	}
	
	private static void addMessageWidget(AonMessagePanel aonMessagePanel, Widget widget) {
		aonMessagePanel.messagePanel.add(widget);
	}
	
	private static void showAndAddMainPanel(Panel panel, AonMessagePanel aonMessagePanel) {
		panel.add(aonMessagePanel);
		panel.getElement().getStyle().clearDisplay();
		fadeIn(aonMessagePanel);
	}
	
	private static void showClosePanel(AonMessagePanel aonMessagePanel, boolean visible) {
		aonMessagePanel.closePanel.setVisible(visible);
	}
	
	private static void showAndAddMainPanelTimer(Panel panel, AonMessagePanel aonMessagePanel) {
		panel.add(aonMessagePanel);
		panel.getElement().getStyle().clearDisplay();
		fadeIn(aonMessagePanel);
		Timer timer = new Timer() {
		     @Override
		     public void run() {
		    	 fadeOut(panel, aonMessagePanel);
		     }
		};
		timer.schedule(2500);
	}
	
	private static void showAndAddMainPanelTimerLong(Panel panel, AonMessagePanel aonMessagePanel) {
		panel.add(aonMessagePanel);
		panel.getElement().getStyle().clearDisplay();
		fadeIn(aonMessagePanel);
		Timer timer = new Timer() {
		     @Override
		     public void run() {
		    	 fadeOut(panel, aonMessagePanel);
		     }
		};
		timer.schedule(3500);
	}
	
	private static void fadeOut(Panel panel, AonMessagePanel aonMessagePanel) {
		new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	        	aonMessagePanel.getElement().getStyle().setOpacity( 1.0 - progress );
	        }

	        @Override
	        protected void onComplete() {
	        	aonMessagePanel.getElement().getStyle().setDisplay(Display.NONE);
	        	aonMessagePanel.removeFromParent();
	        	panel.getElement().getStyle().setDisplay(Display.NONE);
	        }
	    }.run( 400 );
	}
	
	private static void fadeIn(AonMessagePanel aonMessagePanel) {
		new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	        	aonMessagePanel.getElement().getStyle().setOpacity( 0.0 + progress );
	        }

	        @Override
	        protected void onComplete() {
	        	aonMessagePanel.getElement().getStyle().setDisplay(Display.FLEX);
	        }
	    }.run( 400 );
	}

	// ------------------------------------------------ Fill Messages List
		
	private static <T> void fillMessages(AonMessagePanel aonMessagePanel, T[] messages) {
		if(messages.length != 0) {
			for(int i=0; i<messages.length; i++) {
				HTMLPanel messageRowPanel = new HTMLPanel("");
				messageRowPanel.addStyleName(AON.CSS.aonMessageRow());
				Label messageText = newLabel(messages[i]);
				messageRowPanel.add(messageText);
				aonMessagePanel.messagePanel.add(messageRowPanel);
			}
		}
	}
	
	private static <T> void fillMessages(AonMessagePanel aonMessagePanel, Map<String, T> messages) {
		int index = 0;
		for(Entry<String, T> entry : messages.entrySet()) {
			HTMLPanel messageRowPanel = new HTMLPanel("");
			messageRowPanel.addStyleName(AON.CSS.aonMessageRow());
			Label titleText = new Label(entry.getKey());
			titleText.addStyleName(AON.CSS.aonMessageTitle());
			Label messageText = newLabel(entry.getValue());
			messageText.ensureDebugId("message_" + index);
			messageRowPanel.add(titleText);
			messageRowPanel.add(messageText);
			aonMessagePanel.messagePanel.add(messageRowPanel);
			index++;
		}
	}
	
	private static <T> Label fillLoadingMessages(AonMessagePanel aonMessagePanel, T message) {
		HTMLPanel messageRowPanel = new HTMLPanel("");
		messageRowPanel.addStyleName(AON.CSS.aonMessageRow());
		
		AonTableButton loading = new AonTableButton("", AON.CSS.aonIconRenewWhite());
		loading.addStyleName(AON.CSS.aonSpin());
		
		Label messageText = newLabel(message);
		
		messageRowPanel.add(loading);
		messageRowPanel.add(messageText);
		aonMessagePanel.messagePanel.add(messageRowPanel);
		
		return messageText;
	}
	
	
	private static <T>  Label newLabel(T t) {
	    if (t instanceof SafeHtml) {
		return new HTML((SafeHtml) t);
	    } else {
		String text = t == null ? "" : t.toString();
		return new Label(text);
	    }
	}
	
	// ------------------------------------------------ Styles Methods
	
	private static void addErrorStyle(AonMessagePanel aonMessagePanel) {
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageWarning());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageInfo());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageSuccess());
		aonMessagePanel.addStyleName(AON.CSS.aonMessageError());
	}
	
	private static void addInfoStyle(AonMessagePanel aonMessagePanel) {
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageError());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageWarning());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageSuccess());
		aonMessagePanel.addStyleName(AON.CSS.aonMessageInfo());
	}

	private static void addWarningStyle(AonMessagePanel aonMessagePanel) {
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageError());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageInfo());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageSuccess());
		aonMessagePanel.addStyleName(AON.CSS.aonMessageWarning());
	}
	
	private static void addSuccessStyle(AonMessagePanel aonMessagePanel) {
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageError());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageWarning());
		aonMessagePanel.removeStyleName(AON.CSS.aonMessageInfo());
		aonMessagePanel.addStyleName(AON.CSS.aonMessageSuccess());
	}
	
}
