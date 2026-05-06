package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.ui.HTMLPanel;
import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class AonAttachPreviewPanel extends HTMLPanel {
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private FullViewer viewer = new FullViewer();
	
	private Attach attach;
	
	// Constructor
	
	public AonAttachPreviewPanel(Attach attach) {
		super(EMPTY_STRING);
		
		this.attach = attach;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		try {
			String base64Pdf = encodeBase64(toBinaryString(attach.getData()));
			String dataUri = "data:application/pdf;base64," + base64Pdf;
			viewer.open(dataUri);


		} catch (Exception e) {
		    AonMessagePanel.showError(messagePanel, "Error archivo : " + e.getMessage());
		}

		
		container.add(viewer);
		
		add(container);	
	}

	private static native String encodeBase64(String data) /*-{
	    return btoa(data);
	}-*/;
	
	private static String toBinaryString(byte[] bytes) {
	    char[] chars = new char[bytes.length];
	    for (int i = 0; i < bytes.length; i++) {
	        chars[i] = (char) (bytes[i] & 0xFF);
	    }
	    return new String(chars);
	}


}
