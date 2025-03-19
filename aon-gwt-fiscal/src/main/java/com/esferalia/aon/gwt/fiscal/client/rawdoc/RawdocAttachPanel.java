package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScalableImage;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;
import net.aonsolutions.gwt.pdfjs.client.FullViewer.ViewerDefaultScale;

class RawdocAttachPanel extends SimpleLayoutPanel {
	interface RawdocAttachPanelCallback {
		void closeAttach();
		boolean openAttach();
	}
	
	private FlowPanel attachPanelTableCell2;
	
	public void showViewer(MimeType mimeType, String url,RawdocAttachPanelCallback cbk) {
		FlowPanel attachPanelTable = new FlowPanel();
		attachPanelTable.setStyleName(AON.CSS.aonDisplayTable());
		attachPanelTable.addStyleName(AON.CSS.aonWidthAll());
		attachPanelTable.setHeight("100%");
		
		FlowPanel attachPanelTableRow = new FlowPanel();
		attachPanelTableRow.addStyleName(AON.CSS.aonWidthAll());
		attachPanelTableRow.setHeight("100%");
		attachPanelTableRow.setStyleName(AON.CSS.aonDisplayTableRow());
		attachPanelTable.add(attachPanelTableRow);
		
		FlowPanel attachPanelTableCell1 = new FlowPanel();
		attachPanelTableCell1.setHeight("100%");
		attachPanelTableCell1.setStyleName(AON.CSS.aonDisplayTableCell());
		attachPanelTableRow.add(attachPanelTableCell1);
		
		attachPanelTableCell1.setWidth("20px");
		VerticalPanel buttons = new VerticalPanel();
		buttons.setHeight("100%");
		buttons.setStyleName(AON.CSS.aonFlexBlock());
		attachPanelTableCell1.add(buttons);
		
		AonTableButton attachCloseButton = new AonTableButton("Cerrar documento adjunto",AON.CSS.aonIconRight());
		buttons.add(attachCloseButton);

		AonTableButton attachOpenButton = new AonTableButton("Ver documento adjunto",AON.CSS.aonIconLeft());
		buttons.add(attachOpenButton);
		
		attachCloseButton.addClickHandler( event -> cbk.closeAttach());
		
		attachOpenButton.addClickHandler( event -> cbk.openAttach());

		attachPanelTableCell2= new FlowPanel();
		attachPanelTableCell2.setStyleName(AON.CSS.aonDisplayTableCell());
		attachPanelTableCell2.setHeight("100%");
		attachPanelTableRow.add(attachPanelTableCell2);
		
		this.setWidget(attachPanelTable);		

		
		cbk.openAttach();
		
		if ( mimeType != null && mimeType.isPDF()) {
			FullViewer viewer = new FullViewer(url, ViewerDefaultScale.PAGE_WIDTH);
			attachPanelTableCell2.add(viewer);
		} else if ( mimeType != null && mimeType.isImage()) {
			AonScalableImage scalableImage = new AonScalableImage();
			scalableImage.setImage( url );
			attachPanelTableCell2.add(scalableImage);
			
		} else {
			ScrollPanel labelPanel = new ScrollPanel();
			Label unknown = new Label("No se ha podido determinar un visor para este tipo de documento.");
			unknown.setStyleName(AON.CSS.aonBlockMessage());
			unknown.addStyleName(AON.CSS.aonBlockInfoMessage());
			unknown.addStyleName(AON.CSS.aonMargin());
			labelPanel.setWidget(unknown);
			attachPanelTableCell2.add(labelPanel);
		}
	}

}
