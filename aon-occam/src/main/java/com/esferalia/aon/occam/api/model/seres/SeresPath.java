package com.esferalia.aon.occam.api.model.seres;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum SeresPath {

	ENVIO_BINARI_090001("/envio/binari_090001"),
	ENVIO_COACSU_D96A("/envio/coacsu_d96a"),
	ENVIO_DESADV_D01B("/envio/desadv_d01b"),
	ENVIO_DESADV_D96A("/envio/desadv_d96a"),
	ENVIO_GENRAL_D96A("/envio/genral_d96a"),
	ENVIO_HANMOV_D96A("/envio/hanmov_d96a"),
	ENVIO_INVOIC_D01B("/envio/invoic_d01b"),
	ENVIO_INVOIC_D93A("/envio/invoic_d93a"),
	ENVIO_INVOIC_D96A("/envio/invoic_d96a"),
	ENVIO_ORDRSP_D96A("/envio/ordrsp_d96a"),
	ENVIO_PRICAT_D96A("/envio/pricat_d96a"),
	RECEPCION_BINARI_090001("/recepcion/binari_090001"),
	RECEPCION_DELFOR_1911("/recepcion/delfor_1911"),
	RECEPCION_GENRAL_D96A("/recepcion/genral_d96a"),
	RECEPCION_INSDES_D96A("/recepcion/insdes_d96a"),
	RECEPCION_INVOIC_D93A("/recepcion/invoic_d93a"),
	RECEPCION_INVRPT_D96A("/recepcion/invrpt_d96a"),
	RECEPCION_ORDERS_D01B("/recepcion/orders_d01b"),
	RECEPCION_ORDERS_D93A("/recepcion/orders_d93a"),
	RECEPCION_ORDERS_D96A("/recepcion/orders_d96a"),	
	RECEPCION_RECADV_D01B("/recepcion/recadv_d01b"),
	RECEPCION_RECADV_D96A("/recepcion/recadv_d96a"),
	RECEPCION_SLSRPT_D96A("/recepcion/slsrpt_d96a");

	String path;
	
	private SeresPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public static SeresPath safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (SeresPath rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getPath()))
				return rs;
		}
		return null;
	}
	
	
}

