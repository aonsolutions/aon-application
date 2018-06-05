package net.aonsolutions.aon.gwt.aio.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.document.client.Documents;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.DepositEntryPoint;
import com.esferalia.aon.gwt.issues.client.Issues;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.template.client.Templates;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.aio.shared.Modules;
import net.aonsolutions.aon.gwt.communication.client.Communication;
import net.aonsolutions.aon.gwt.invoice.client.Invoice;
import net.aonsolutions.aon.gwt.sii.client.Sii;
import net.aonsolutions.aon.gwt.seres.client.Seres;
import net.aonsolutions.aon.gwt.udapa.client.Udapa;
import net.aonsolutions.aon.gwt.warehouse.client.Warehouse;

public class Aio implements EntryPoint {
	
	final IAioAsync impl = GWT.create(IAio.class);

	private Issues issues;
	private Documents documents;
	//private Documental documental;
	
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	public static native String getSubEntryPoint()
	/*-{
		return $wnd.getSubEntryPoint();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override public void onSuccess(AonData result) {
				selection(entryPoint, result);
			}
			
			@Override public void onFailure(Throwable arg0) {}
		});
		
	}
	
	private void selection(String entryPoint, AonData aonData) {
		switch (entryPoint) {
		case Modules.ISSUES:
			JsAio.addOnBeforeUnloadHandler(this);
			JsAio.addOnReloadHandler(this);
			GWT.runAsync(Issues.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					issues = new Issues(aonData);
					issues.onModuleLoad();
				}
			});
			break;
		case Modules.DOCUMENT:
			JsAio.addOnBeforeUnloadHandler(this);
			JsAio.addOnReloadHandler(this);
			GWT.runAsync(Documents.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					documents = new Documents(aonData);
					documents.onModuleLoad();
					// NUEVO DOCUMENTAL CON POLYMEROS
					//documental = new Documental(aonData);
					//documental.onModuleLoad();
				}
			});		
			break;
		case Modules.STAT:
			GWT.runAsync(MainEntryPoint.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					MainEntryPoint stat = new MainEntryPoint(aonData);
					stat.onModuleLoad(getSubEntryPoint());
				}
			});		
			break;
		case Modules.WAREHOUSE:
			GWT.runAsync(Warehouse.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Warehouse warehouse = new Warehouse(aonData);
					warehouse.onModuleLoad(getSubEntryPoint());
				}
			});		
			break;
		case Modules.DUMP_FORM:
			GWT.runAsync(com.esferalia.aon.gwt.dump.client.MainEntryPoint.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					com.esferalia.aon.gwt.dump.client.MainEntryPoint dump = new com.esferalia.aon.gwt.dump.client.MainEntryPoint();
					dump.onModuleLoad();
				}
			});		
			break;
		case Modules.UDAPA:
			GWT.runAsync(Udapa.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Udapa(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.TEMPLATES:
			GWT.runAsync(Templates.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Templates(aonData).onModuleLoad(getSubEntryPoint());
				}
			});		
			break;
		case Modules.SII:
			GWT.runAsync(Sii.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Sii(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.INVOICE:
			GWT.runAsync(Invoice.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Invoice(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.SERES:
			GWT.runAsync(Seres.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Seres(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.COMMUNICATION:
			GWT.runAsync(Communication.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Communication(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.DEPOSIT:
			GWT.runAsync(DepositEntryPoint.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new DepositEntryPoint(aonData).onModuleLoad(getSubEntryPoint());
				}
			});		
			break;
		default:
			break;
		}
	}
	
	public void onBeforeUnload(){
		impl.selectedMenu(new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void onReload(){
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		if(entryPoint.equals(Modules.ISSUES)) issues.remove();
		if(entryPoint.equals(Modules.DOCUMENT)) documents.remove();
		onModuleLoad();
	}
	
}
