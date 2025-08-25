package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;

public class AonProjectTasExcelDialog extends AonCustomDialog {

	private HTMLPanel main = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel message = new HTMLPanel(AonStringUtils.EMPTY);
	
	private HTMLPanel container = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomDateBox start = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox end = new AonCustomDateBox("F. Fin");
	
	private HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private Button exportButton;
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private Integer domain;
	private String domainName;
	private String user;
	
	public AonProjectTasExcelDialog(String domainName, Integer domain, String user ) {
		setCaption("Exportar Ordenes Reparacion (Excel)");
		
		this.showCloseButton(true);
		
		this.domain = domain;
		this.domainName = domainName;
		this.user = user;
		
		main.addStyleName(AON.CSS.aonItemFlex());
		main.addStyleName(AON.CSS.aonFlexColumn());
		main.getElement().getStyle().setProperty("margin-top", "1rem");
		
		message.setWidth("100%");
		main.add(message);
		
		container.addStyleName(AON.CSS.aonItemFlex());
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem 0");
		container.setWidth("90%");
		
		container.add(start);
		container.add(end);
		
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		
		exportButton = new Button();
		exportButton.setStyleName(AON.CSS.aonIconExcel());
		addExportButtonStyle();
		exportButton.setText( AON.MSG.export());
    	
		exportButton.addClickHandler(e -> {
			if(null == start.getValue()) AonMessagePanel.showWarning(message, "La F.Inicio es obligatoria");
			else exportTas();
    	});
		
		buttonsPanel.add(exportButton);
		
		container.add(buttonsPanel);
		
		main.add(container);
		
		this.add(main);
		
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	private void addExportButtonStyle() {
		exportButton.getElement().getStyle().setProperty("background-repeat", "no-repeat");
		exportButton.getElement().getStyle().setProperty("background-color", "transparent");
		exportButton.getElement().getStyle().setProperty("border", "1px solid #bfbfbf");
		exportButton.getElement().getStyle().setProperty("border-radius", "5px");
		exportButton.getElement().getStyle().setProperty("padding", ".5rem 1rem .5rem 2rem");
		exportButton.getElement().getStyle().setProperty("background-position", "5px center");
		exportButton.getElement().getStyle().setProperty("cursor", "pointer");
	}
	
	private void exportTas() {
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + "ms/api/projectTasExcel");
		diskForm.addSubmitCompleteHandler(ev -> container.remove(diskForm));
		
		Hidden domainIdHidden = new Hidden("domainId", domain.toString());
		Hidden domainNameHidden = new Hidden("domainName", domainName);
		Hidden userHidden = new Hidden("login", user);
		Hidden startDateHidden = new Hidden("startDate", formatFullDate.format(start.getValue()));
		Hidden endDateHiddenHidden = new Hidden("endDate", formatFullDate.format(null == end.getValue() ? new Date() : end.getValue()));
		
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(startDateHidden);
		formFlowPanel.add(endDateHiddenHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);

		container.add(diskForm);
		
		diskForm.submit();
		
		hide();
		
		clickGarage();
	}
	
	public static native void clickGarage() /*-{
	    // Intentar dentro del iframe primero
	    var el = $doc.getElementById("aonContent:mainMenuForm:menu_garage");
	    if (el) {
	        el.click();
	    } else {
	        // Buscar en el DOM del padre
	        var parentEl = $wnd.parent.document.querySelector("#aonMenuBar-garage a");
	        if (parentEl) {
	            parentEl.click();
	        }
	    }
	}-*/;


		
}
