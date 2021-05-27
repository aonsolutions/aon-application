package com.esferalia.aon.gwt.api.client.product;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsPaturpatProductInfo extends JavaScriptObject {

	protected JsPaturpatProductInfo() {}
	
	public static final ProvidesKey<JsPaturpatProductInfo> PROVIDES_KEY = new ProvidesKey<JsPaturpatProductInfo>() {
		@Override
		public Object getKey(JsPaturpatProductInfo ppi) {
			return 1;
		}
	};
	
	public final native Integer getProduct() /*-{
		return this.product;
	}-*/;
	
	public final native Integer getItem() /*-{
		return this.item;
	}-*/;
	
	public final native String getFilm() /*-{
		return this.film;
	}-*/;
	
	public final native String getType() /*-{
		return this.tipo;
	}-*/;
	
	public final native String getCaliber() /*-{
		return this.calibre;
	}-*/;
	
	public final native String getExpiration() /*-{
		return this.caducidad;
	}-*/;
	
	public final native String getCutting() /*-{
		return this.corte;
	}-*/;
	
	public final native String getCriba() /*-{
		return this.criba;	
	}-*/;
	
	public final native String getEscaldado() /*-{
		return this.escaldado;	
	}-*/;
	
	public final native String getSalt() /*-{
		return this.sal_en_balsa;	
	}-*/;
	
	public final native String getCod() /*-{
		return this.programa_codificador;	
	}-*/;
	
	public final native String getLiqGob() /*-{
		return this.liquido_gobierno;	
	}-*/;
	
	public final native String getPasteurization() /*-{
		return this.pasteurizacion;	
	}-*/;
	
	public final native String getClient() /*-{
		return this.cliente;	
	}-*/;
}
