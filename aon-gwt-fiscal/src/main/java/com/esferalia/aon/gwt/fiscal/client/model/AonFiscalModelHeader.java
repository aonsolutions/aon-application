package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AonFiscalModelHeader extends SimpleLayoutPanel {
	public static final int HEIGTH = 60;
	
	public AonFiscalModelHeader( IFiscalModel m) {
		AonDisplayTable header = new AonDisplayTable();
		header.addStyleName(AON.CSS.aonBlockCenter());
		header.getElement().getStyle().setWidth(98, Unit.PCT);
		header.getElement().getStyle().setHeight(HEIGTH-12, Unit.PX);
		header.getElement().getStyle().setBorderColor("white");
		header.getElement().getStyle().setBorderWidth(1, Unit.PX);
		header.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		header.getElement().getStyle().setProperty("border-collapse","separate");
		header.getElement().getStyle().setProperty("border-spacing","2px");
		
		String[] styles = new String[]{
			FiscalModelUtils.getAdministrationBackgroundStyle(m.getAdministration()),
			(m.getAdministration() == Administration.CANARIAS?AON.CSS.aonColorBlack():AON.CSS.aonColorWhite()),
			AON.CSS.aonBold(),
			AON.CSS.aonTextCenter(),
			AON.CSS.aonFontLarger(),
		};
		
		AonDisplayTableRow row = header.addRow();
		
		// Administration LOGO
		Image logo = new Image( FiscalModelUtils.getAdministrationIconDataResource(m.getAdministration()).getSafeUri() );
		logo.getElement().getStyle().setHeight(HEIGTH - 15.0, Unit.PX);
		AonDisplayTableCell logoCell = row.addCell();
		logoCell.addStyleName(AON.CSS.aonTextCenter());
		logoCell.getElement().getStyle().setWidth(HEIGTH - 20.0, Unit.PX);
		logoCell.add(logo);

		
		// MODEL CODE
		AonDisplayTableCell modelCodeCell = row.addCell( styles);
		modelCodeCell.getElement().getStyle().setWidth(80, Unit.PX);
		modelCodeCell.getElement().getStyle().setProperty("border-radius", "8px");
		Label modelCode = new Label( FiscalModelUtils.getModelName(m) );
		modelCode.setStyleName(AON.CSS.aonFontLarger());
		modelCodeCell.add(modelCode);

		// MODEL NAME
		AonDisplayTableCell modelNameCell = row.addCell(styles);
		modelNameCell.getElement().getStyle().setProperty("border-radius", "8px");
		modelNameCell.setWidth("auto");
		Label modelName = new Label( AON.MSG.fiscalModelDescriptionlong(m.getModel() ));
		modelNameCell.add(modelName);
		
		
		// MODEL NAME
		AonDisplayTableCell yearPeriodCell = row.addCell( styles);
		yearPeriodCell.getElement().getStyle().setProperty("border-radius", "8px");
		yearPeriodCell.getElement().getStyle().setWidth(80, Unit.PX);
		Label modelYear = new Label( AonNumberUtils.toString( m.getYear() ));
		yearPeriodCell.add(modelYear);
		Label modelPeriod = new Label( m.getPeriod()==null?"----":FiscalModelUtils.getPeriodDescription(m));
		yearPeriodCell.add(modelPeriod);
		
		setWidget(header);
	}
	
}
