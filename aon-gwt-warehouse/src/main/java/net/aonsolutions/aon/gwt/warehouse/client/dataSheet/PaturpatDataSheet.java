package net.aonsolutions.aon.gwt.warehouse.client.dataSheet;

import java.util.HashMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.product.JsPaturpatProductInfo;
import com.esferalia.aon.gwt.api.client.product.JsProduct;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PaturpatDataSheet extends Composite{
	
	interface Binder extends UiBinder<Widget, PaturpatDataSheet> {}
	private static final Binder binder = GWT.create(Binder.class);
	private DataSheet parent;
	public JsProduct product;

	// hashMap || jsonobject
	HashMap<String, String> map = new HashMap<>();
	
	@UiField Label productCode;
	@UiField Label productName;
	
	@UiField TextBox film;
	@UiField TextBox expiration;
	@UiField TextBox type;
	@UiField TextBox caliber;
	@UiField TextBox cut;
	@UiField TextBox criba;
	@UiField TextBox escaldado;
	@UiField TextBox sal;
	@UiField TextBox cod;
	@UiField TextBox liqGob;
	@UiField TextBox past;	
	@UiField TextBox client;

	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public AonData getAonData() {
		return parent.getAonData();
	}
		
	public HashMap<String, String> getMap() {
		return map;
	}

	public PaturpatDataSheet(DataSheet parent, JsProduct product) {
		this.parent = parent;
		
		initWidget(binder.createAndBindUi(this));
		productCode.setText(product.getCode());
		productName.setText(product.getName());
	
		getAPI().getProduct().getPaturpatItemInfo(product.getId(), new AsyncCallback<JSON<JsPaturpatProductInfo>>() {
			
			@Override
			public void onSuccess(JSON<JsPaturpatProductInfo> result) {
				JsPaturpatProductInfo js = result.getData().get(0);
				
				film.setText(js.getFilm());
				film.addChangeHandler(getChangeHandler(js, "film"));
				expiration.setText(js.getExpiration());
				expiration.addChangeHandler(getChangeHandler(js, "caducidad"));
				type.setText(js.getType());
				type.addChangeHandler(getChangeHandler(js, "tipo"));
				caliber.setText(js.getCaliber());
				caliber.addChangeHandler(getChangeHandler(js, "calibre"));
				cut.setText(js.getCutting());
				cut.addChangeHandler(getChangeHandler(js, "corte"));
				criba.setText(js.getCriba());
				criba.addChangeHandler(getChangeHandler(js, "criba"));
				escaldado.setText(js.getEscaldado());
				escaldado.addChangeHandler(getChangeHandler(js, "escaldado"));
				sal.setText(js.getSalt());
				sal.addChangeHandler(getChangeHandler(js, "sal_en_balsa"));
				cod.setText(js.getCod());
				cod.addChangeHandler(getChangeHandler(js, "programa_codificador"));
				liqGob.setText(js.getLiqGob());
				liqGob.addChangeHandler(getChangeHandler(js, "liquido_gobierno"));
				past.setText(js.getPasteurization());
				past.addChangeHandler(getChangeHandler(js, "pasteurizacion"));
				client.setText(js.getClient());
				client.addChangeHandler(getChangeHandler(js, "cliente"));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
	}
	
	public ChangeHandler getChangeHandler(JsPaturpatProductInfo js, String alias) {
		return new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JSONObject json = new JSONObject();
				json.put("attribute", new JSONString(alias));
				json.put("product", new JSONString(js.getProduct() + ""));
				json.put("item", new JSONString(js.getItem() + ""));
				json.put("value", new JSONString(getValue(alias)));
				String requestData = JsonUtils.stringify(json.getJavaScriptObject());
				getAPI().getProduct().insertPaturpatItemInfo(requestData);
			}
		};
	}
	
	private String getValue(String alias) {
		if("film".equals(alias)) return film.getValue();
		else if("caducidad".equals(alias)) return expiration.getValue();
		else if("tipo".equals(alias)) return type.getValue();
		else if("calibre".equals(alias)) return caliber.getValue();
		else if("corte".equals(alias)) return cut.getValue();
		else if("criba".equals(alias)) return criba.getValue();
		else if("escaldado".equals(alias)) return escaldado.getValue();
		else if("sal_en_balsa".equals(alias)) return sal.getValue();
		else if("programa_codificador".equals(alias)) return cod.getValue();
		else if("liquido_gobierno".equals(alias)) return liqGob.getValue();
		else if("pasteurizacion".equals(alias)) return past.getValue();
		else if("cliente".equals(alias)) return client.getValue();

		return "";
	}
}
