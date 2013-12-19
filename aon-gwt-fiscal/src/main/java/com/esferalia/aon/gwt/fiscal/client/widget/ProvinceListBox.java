package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.fiscal.client.FiscalEnum.Province;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceListBox extends ListBox {

	
	public ProvinceListBox() {
		setWidth("120px");
		FiscalMessages msgs = (FiscalMessages) GWT.create(FiscalMessages.class);
		
		this.addItem( msgs.provinceName( Province.DESCONOCIDO));
		this.addItem( msgs.provinceName( Province.ARABA));
		this.addItem( msgs.provinceName( Province.ALBACETE));
		this.addItem( msgs.provinceName( Province.ALICANTE));
		this.addItem( msgs.provinceName( Province.ALMERIA));
		this.addItem( msgs.provinceName( Province.AVILA));
		this.addItem( msgs.provinceName( Province.BADAJOZ));
		this.addItem( msgs.provinceName( Province.ILLES_BALEARS));
		this.addItem( msgs.provinceName( Province.BARCELONA));
		this.addItem( msgs.provinceName( Province.BURGOS));
		this.addItem( msgs.provinceName( Province.CACERES));
		this.addItem( msgs.provinceName( Province.CADIZ));
		this.addItem( msgs.provinceName( Province.CASTELLON));
		this.addItem( msgs.provinceName( Province.CIUDAD_REAL));
		this.addItem( msgs.provinceName( Province.CORDOBA));
		this.addItem( msgs.provinceName( Province.A_CORUNA));
		this.addItem( msgs.provinceName( Province.CUENCA));
		this.addItem( msgs.provinceName( Province.GIRONA));
		this.addItem( msgs.provinceName( Province.GRANADA));
		this.addItem( msgs.provinceName( Province.GUADALAJARA));
		this.addItem( msgs.provinceName( Province.GIPUZKOA));
		this.addItem( msgs.provinceName( Province.HUELVA));
		this.addItem( msgs.provinceName( Province.HUESCA));
		this.addItem( msgs.provinceName( Province.JAEN));
		this.addItem( msgs.provinceName( Province.LEON));
		this.addItem( msgs.provinceName( Province.LLEIDA));
		this.addItem( msgs.provinceName( Province.LA_RIOJA));
		this.addItem( msgs.provinceName( Province.LUGO));
		this.addItem( msgs.provinceName( Province.MADRID));
		this.addItem( msgs.provinceName( Province.MALAGA));
		this.addItem( msgs.provinceName( Province.MURCIA));
		this.addItem( msgs.provinceName( Province.NAVARRA));
		this.addItem( msgs.provinceName( Province.OURENSE));
		this.addItem( msgs.provinceName( Province.ASTURIAS));
		this.addItem( msgs.provinceName( Province.PALENCIA));
		this.addItem( msgs.provinceName( Province.LAS_PALMAS));
		this.addItem( msgs.provinceName( Province.PONTEVEDRA));
		this.addItem( msgs.provinceName( Province.SALAMANCA));
		this.addItem( msgs.provinceName( Province.TENERIFE));
		this.addItem( msgs.provinceName( Province.CANTABRIA));
		this.addItem( msgs.provinceName( Province.SEGOVIA));
		this.addItem( msgs.provinceName( Province.SEVILLA));
		this.addItem( msgs.provinceName( Province.SORIA));
		this.addItem( msgs.provinceName( Province.TARRAGONA));
		this.addItem( msgs.provinceName( Province.TERUEL));
		this.addItem( msgs.provinceName( Province.TOLEDO));
		this.addItem( msgs.provinceName( Province.VALENCIA));
		this.addItem( msgs.provinceName( Province.VALLADOLID));
		this.addItem( msgs.provinceName( Province.BIZKAIA));
		this.addItem( msgs.provinceName( Province.ZAMORA));
		this.addItem( msgs.provinceName( Province.ZARAGOZA));
		this.addItem( msgs.provinceName( Province.CEUTA));
		this.addItem( msgs.provinceName( Province.MELILLA));
		this.addItem( msgs.provinceName( Province.NO_RESIDENTE));
		
	}
	
}
