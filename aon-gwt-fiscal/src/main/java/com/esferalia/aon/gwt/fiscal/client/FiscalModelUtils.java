package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class FiscalModelUtils {

	public static void paintHeaderTable(SimplePanel headerPanel, FiscalModel fm) {
		Administration admon = (fm == null?Administration.COMMON_TERRITORY:fm.getAdministration());
		Period period = fm.getPeriod();
		
		headerPanel.clear();
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(getAdministrationImage(admon));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(fm.getModel().getName(admon,period))); 
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		headerTable.setWidget(0, 2, new Label(AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		headerTable.setWidget(0, 3, new Label(""+fm.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 3, getAdministrationBG(admon));
		
		headerTable.setWidget(1, 0, new Label(fm.getPeriod().getDescription()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG(admon));

		headerPanel.setWidget(headerTable);
	}

	public static String getAdministrationBG(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonFiscalArabaBg();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonFiscalBizkaiaBg();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonFiscalGipuzkoaBg();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonFiscalNavarraBg();
		} 
		return AON.AON_CSS.aonFiscalAeatBg();
	}

	public static String getAdministrationImage(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonArabaHeaderImage();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonBizkaiaHeaderImage();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonGipuzkoaHeaderImage();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonNavarraHeaderImage();
		} 
		return AON.AON_CSS.aonAeatHeaderImage();
	}
	
	public static String getAdministrationIcon(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonIconAraba();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaia();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoa();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarra();
		} 
		return AON.AON_CSS.aonIconAeat();
	}

	public static ImageResource getAdministrationIconResource(Administration adm) {
		if (adm ==Administration.ALAVA) {
			return AON.AON_RESOURCES.aonIconAraba();	
		} else if (adm ==Administration.BIZKAIA) {
			return AON.AON_RESOURCES.aonIconBizkaia();
		} else if (adm ==Administration.GIPUZKOA) {
			return AON.AON_RESOURCES.aonIconGipuzkoa();
		} else if (adm ==Administration.NAVARRA) {
			return AON.AON_RESOURCES.aonIconNavarra();
		} 
		return AON.AON_RESOURCES.aonAeat();
	}
}
