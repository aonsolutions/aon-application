package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public class SiiMain extends AonTemplate2{

	final ISiiAsync impl = GWT.create(ISii.class);
	private API API;
	HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	private Administration administration;

	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public SiiMain(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}
	
	private void startApplication() {
		impl.getAdministration(aonData.getDomain(), aonData.getUser().getLogin(), new AsyncCallback<Administration>() {
			
			@Override
			public void onSuccess(Administration result) {
				administration = result;
				initializeFilterMap();
				toolbar();
				westContent();
				content();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void initializeFilterMap() {
		filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("fe_emitidas");
		filterMap.put("sii",list);
		
		Date date = new Date(2017-1900, 6, 1);
		if(administration.equals(Administration.ALAVA) || administration.equals(Administration.BIZKAIA)
				|| administration.equals(Administration.GIPUZKOA) || administration.equals(Administration.NAVARRA)) {
			date = new Date(2018-1900, 0, 1);
		}
		
		list = new LinkedList<>();
		list.add(Long.toString(date.getTime()));
		filterMap.put("from",list);
		
		list = new LinkedList<>();
		list.add("true");
		filterMap.put("pending", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("sent", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("sent_error", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("error", list);
		
		list = new LinkedList<>();
		list.add("false");
		filterMap.put("anulada", list);
	}
	
	Button sendAll;
	Button send;
	Button baja;
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Suministro Inmediato de Informacion") {};
		sendAll = toolbar.addButton("Enviar Todo", AON.AON_CSS.aonIconSave());
		sendAll.setVisible(true);
		sendAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendAllSii(getFilterMap().get("sii").get(0));
			}
		});
		
		send = toolbar.addButton("Enviar", AON.AON_CSS.aonIconSave());
		send.setVisible(false);
		send.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				
				SimpleLayoutPanel slp = p.getContent();
				InvoiceGrid ig = (InvoiceGrid) slp.getWidget();
				ig.sendSii(getFilterMap().get("sii").get(0)); // TODO
			}
		});
		
		baja = toolbar.addButton("Anular", "aon-icon-removed");
		baja.setVisible(false);
		baja.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
				
				SimpleLayoutPanel slp = p.getContent();
				InvoiceGrid ig = (InvoiceGrid) slp.getWidget();
				ig.anular(getFilterMap().get("sii").get(0)); // TODO
			}
		});
	
		setToolbar(toolbar);
	}
	
	public Button getSendAll() {
		return sendAll;
	}
	
	public Button getSend() {
		return send;
	}	
	
	public Button getBaja() {
		return baja;
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		setWestContent(new ConfigurationPanel(this));
	}
	
	public void menuSelection(String option, String title, Boolean checkVisible){
		sendAll.setVisible(true);
		send.setVisible(false);
		baja.setVisible(false);
		LinkedList<String> list = new LinkedList<>();
		list.add(option);
		getFilterMap().put("sii",list);
		SiiPrincipal p = (SiiPrincipal) getContent().getWidget();
		FilterPanel fp = (FilterPanel) p.getNorthContent().getWidget();
		fp.setCheckVisible(checkVisible);
		fp.setTitle(title);
		p.gridContent();
	}
	

	private void content() {
		setContent(new SiiPrincipal(this));
	}

	
	public void sendAllSii(String sii){
		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.add(new Label("Tipo de Operacion"));
		ListBox lb0 = new ListBox();
		lb0.addItem("Articulo 70, apartado uno, n\u00famero 7\u00BA, Ley del Impuesto(Ley 37/1992)", "A");
		lb0.addItem("Articulo 16, apartado 2\u00BA, Ley del Impuesto(Ley 37/1992)", "B");
		hp0.add(lb0);
		if("intracomunitarias".equalsIgnoreCase(sii)){
			vp.add(hp0);
		}
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l = new Label("NIF");
		l.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l.setVisible(false);
		TextBox t = new TextBox();t.setStyleName(AON.AON_CSS.aonInputText());
		t.setVisible(false);
		hp3.add(l);
		hp3.add(t);
		
	
		
		CheckBox cb = new CheckBox("Por terceros");
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				l.setVisible(cb.getValue());
				t.setVisible(cb.getValue());	
			}
		});
		vp.add(cb);
		vp.add(hp3);
		
		AonDialog dialog = new AonDialog("Enviar Facturas", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> sendMap =  new HashMap<>();
				LinkedList<String> list = new LinkedList<>();
				
				sendMap.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("suministro");
		    	sendMap.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	sendMap.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	sendMap.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add(sii);
		    	sendMap.put("option", list);
		    	hide();
		    	
		    	list = new LinkedList<>();
		    	list.add(lb0.getSelectedValue());
		    	sendMap.put("tipo_operacion", list);
	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t.getValue() : "false");
		    	sendMap.put("terceros", list);

		    	resultPanel(sendMap);
			}
		};
		dialog.center();
	}
	
	private Boolean isSendAllCancel = false;
	private void resultPanel(HashMap<String, LinkedList<String>> sendMap) {
		ScrollPanel sp = new ScrollPanel();
		sp.setHeight("400px");
		sp.setWidth("350px");
		VerticalPanel vp = new VerticalPanel();
		sp.add(vp);
		AonDialog dialog = new AonDialog("Resultado", sp) {
			
			@Override
			protected void onCancel() {
				isSendAllCancel = true;
				hide();
				content();
			}
			
			@Override
			protected void onAccept() {
				hide();
				content();
			}
		};
		dialog.setAutoHideEnabled(false);
		dialog.getAccept().setVisible(false);
		dialog.center();
		HashMap<String , LinkedList<String>> map = getFilterMap();
		resultPanel(map, 1, vp, sendMap, dialog);
	}
	
	private void resultPanel(HashMap<String, LinkedList<String>> map, Integer page, VerticalPanel vp, HashMap<String, LinkedList<String>> sendMap, AonDialog d) {
		LinkedList<String> list = new LinkedList<>();
		list.add(page +"");
		map.put("page", list);
		
		list = new LinkedList<>();
		list.add("1");
		map.put("per_page", list);
		getAPI().getFinance().getInvoices(map, new AsyncCallback<JSON<JsInvoice>>() {
			
			@Override
			public void onSuccess(JSON<JsInvoice> result) {
				LinkedList<String> list = new LinkedList<>();
				list.add(result.getData().get(0).getId() + "");
				sendMap.put("id", list);
	
				getAPI().getFinance().sendSii(sendMap, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result2) {	
						result2.getData().stream().forEach(r -> {
							Label label = new Label(r.getName());
							String str = r.getId() + "";
							String color = "red";
							if(str.equals("200")) color = "green";
							else if(str.substring(0, 1).equals("2")) color = "orange";
							label.getElement().getStyle().setColor(color);
							vp.add(label);
						});
						if(isSendAllCancel) {
							isSendAllCancel = false;
						} else if(result.getData().length() < 1) {
							d.getAccept().setVisible(true);
						} else resultPanel(map, page, vp, sendMap, d);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}
}
