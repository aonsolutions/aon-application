package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.type.CNAE2009ToCNAE2025;
import com.esferalia.aon.occam.api.model.type.CNAE2025;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AonCnae2025Panel extends AonCustomDialog implements HasSelectionHandlers<CNAE2025>{

	private SimplePanel scroll;

	public AonCnae2025Panel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		showCloseButton(true);

		scroll = new ScrollPanel();
		scroll.setWidth("550px");
		scroll.setHeight("500px");
		scroll.setStyleName(AON.CSS.aonPaddingLeft());
		scroll.addStyleName(AON.CSS.aonPaddingRight());
		this.setWidget(scroll);
	}
	
	public void onShow() {
		paint();
		center();
		show();
	}
	
	public void onShowCnae2009ToCnae2025(String code2009) {
		if (paintCnae2009ToCnae2025(code2009)) {
			center();
			show();
		}
	}
	
	private void paint() {
		setCaption("C.N.A.E. 2025");
		AonDisplayGrid table = new AonDisplayGrid();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()))
			.getElement().getStyle().setFontSize(13, Unit.PX);

		for (CNAE2025 cnae :  CNAE2025.values()) {
			AonDisplayGridRow row = table.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addCell( new Label(cnae.getCode()), AON.CSS.aonBold())
			   .addCell( new Label(cnae.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
			row.addClickHandler(event -> {
				hide();
				SelectionEvent.fire(AonCnae2025Panel.this, cnae );
			});	
		}
		scroll.setWidget(table);
	}
	
	private boolean paintCnae2009ToCnae2025(String code2009) {
		setCaption("CORRESPONDENCIA C.N.A.E. 2009 Y C.N.A.E. 2025");
		showCloseButton(false);
		AonDisplayGrid table = new AonDisplayGrid();
		table.addStyleName(AON.CSS.aonWidthAll());
		table.addHeaderRow()
			.addCell(new Label("CNAE09"))
			.addCell(new Label("CNAE25"))
			.addCell(new Label("Descripci\u00F3n 2025"));

		int count = 0;
		CNAE2025 cnae2025 = null;
		
		CNAE2009ToCNAE2025 cnae2009to2025 = CNAE2009ToCNAE2025.valueOfCode(code2009);
		
		if (cnae2009to2025 != null) {
			for (String code2025 : cnae2009to2025.getCode2025()) {
				count++;
				CNAE2025 cnae2025bis = CNAE2025.valueOfCode(code2025);
				cnae2025 = cnae2025bis;
				AonDisplayGridRow row = table.addRow();
				row.addStyleName(AON.CSS.aonClickable());
				row.addCell( new Label(cnae2009to2025.getCode2009()), AON.CSS.aonItalic(), AON.CSS.aonBold())
				   .addCell( new Label(cnae2025bis.getCode()), AON.CSS.aonBold())
				   .addCell( new Label(cnae2025bis.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
				row.addClickHandler(event -> {
					hide();
					SelectionEvent.fire(AonCnae2025Panel.this, cnae2025bis);
				});
			}
			if (count == 1) {
				SelectionEvent.fire(AonCnae2025Panel.this, cnae2025);
				return false; // Solo hay un elemento que se devuelve directamente
			} else {
				scroll.setWidget(table);
				return true; // Se muestra la lista para seleccionar
			}
		} else {
			// Si no se encuentra el CNAE2009 en la correspondencia con el CNAE2025, se saca la lista de todos los CNAE-2025
			paint();
			return true;
		}
		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<CNAE2025> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

}
