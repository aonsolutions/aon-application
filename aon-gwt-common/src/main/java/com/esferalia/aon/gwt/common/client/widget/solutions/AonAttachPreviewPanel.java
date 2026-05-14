package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AonAttachPreviewPanel extends AonCustomDialog {

    public AonAttachPreviewPanel(Attach attach) {
    	super();

        setStyleName(AON.CSS.aonFlexColumn2());
        getElement().getStyle().setProperty("z-index", "75");
        getElement().getStyle().setProperty("padding", ".5rem");
        getElement().getStyle().setProperty("background-color", "white");
        getElement().getStyle().setProperty("border-radius", "10px");
        
        setAutoHideEnabled(true);

        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("85vw");
        panel.setHeight("95vh");

        // Crear iframe
        Frame iframe = new Frame();
        iframe.setWidth("100%");
        iframe.setHeight("95vh");
        iframe.getElement().getStyle().setProperty("border-radius", "10px");

        try {
            String base64 = encodeBase64(toBinaryString(attach.getData()));
            String dataUri = "data:application/pdf;base64," + base64;
            iframe.setUrl(dataUri);

        } catch (Exception e) {
            panel.add(new Label("Error cargando PDF: " + e.getMessage()));
        }

        panel.add(iframe);
        add(panel);
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
