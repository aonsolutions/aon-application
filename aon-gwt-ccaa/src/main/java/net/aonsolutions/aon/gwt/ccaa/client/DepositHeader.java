package net.aonsolutions.aon.gwt.ccaa.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class DepositHeader extends SimpleLayoutPanel {
	
	public static final int HEIGTH = 60;
	
	public DepositHeader(String type, Integer year) {
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
			AON.CSS.aonCcaaBackgroundColor(),	
			AON.CSS.aonColorWhite(),
			AON.CSS.aonBold(),
			AON.CSS.aonTextCenter(),
			AON.CSS.aonFontLarger(),
		};
		
		AonDisplayTableRow row = header.addRow();
		
		// Administration LOGO

		
		
		Image logo = new Image(AON.AON_RESOURCES.aonRegistroMercantilImage().getSafeUri());
		logo.getElement().getStyle().setHeight(HEIGTH - 15.0, Unit.PX);
		AonDisplayTableCell logoCell = row.addCell();
		logoCell.addStyleName(AON.CSS.aonTextCenter());
		logoCell.getElement().getStyle().setWidth(HEIGTH - 20.0, Unit.PX);
		logoCell.add(logo);

		
		// MODEL CODE
		AonDisplayTableCell modelCodeCell = row.addCell( styles);
		modelCodeCell.getElement().getStyle().setWidth(80, Unit.PX);
		modelCodeCell.getElement().getStyle().setProperty("border-radius", "8px");
		Label modelCode = new Label( "CCAA" );
		modelCode.setStyleName(AON.CSS.aonFontLarger());
		modelCodeCell.add(modelCode);

		// MODEL NAME
		AonDisplayTableCell modelNameCell = row.addCell(styles);
		modelNameCell.getElement().getStyle().setProperty("border-radius", "8px");
		modelNameCell.setWidth("auto");
		Label modelName = new Label("Dep\u00f3sito de Cuentas Anuales");
		modelNameCell.add(modelName);
		
		// MODEL NAME
		AonDisplayTableCell yearPeriodCell = row.addCell( styles);
		yearPeriodCell.getElement().getStyle().setProperty("border-radius", "8px");
		yearPeriodCell.getElement().getStyle().setWidth(80, Unit.PX);
		Label modelYear = new Label(year != null ? AonNumberUtils.toString(year) : "20XX");
		yearPeriodCell.add(modelYear);
		Label modelPeriod = new Label(type);
		yearPeriodCell.add(modelPeriod);
		
		setWidget(header);
	}
	
}
