package net.aonsolutions.aon.gwt.aio.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.DepositEntryPoint;
import com.esferalia.aon.gwt.issues.client.Issues;
import com.esferalia.aon.gwt.payroll.client.EmployeeTree;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.template.client.Templates;
import com.esferalia.aon.gwt.template.client.scope.ScopeMain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.aio.shared.Modules;
import net.aonsolutions.aon.gwt.commercial.client.Commercial;
import net.aonsolutions.aon.gwt.communication.client.Communication;
import net.aonsolutions.aon.gwt.document.client.Documental;
import net.aonsolutions.aon.gwt.seres.client.Seres;
import net.aonsolutions.aon.gwt.sii.client.Sii;
import net.aonsolutions.aon.gwt.udapa.client.Udapa;
import net.aonsolutions.aon.gwt.warehouse.client.Warehouse;

public class Aio implements EntryPoint {
	
	final IAioAsync impl = GWT.create(IAio.class);

	private Issues issues;
	private Documental documental;
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getDomainId()
	/*-{
		return $wnd.localStorage.getItem("aon_domain_id");
	}-*/;
	
	public static native String getDomainName()
	/*-{
		return $wnd.localStorage.getItem("aon_domain_name");
	}-*/;
	
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
		if(getToken() != null) {
			impl.getAonDataToken(getDomainName(), getDomainId(), getToken(), new AsyncCallback<AonData>() {
				
				@Override public void onSuccess(AonData result) {
					selection(entryPoint, result);
				}
				
				@Override public void onFailure(Throwable arg0) {}
			});
		} else {
			impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
				
				@Override public void onSuccess(AonData result) {
					selection(entryPoint, result);
				}
				
				@Override public void onFailure(Throwable arg0) {}
			});			
		}

		
	}
	
	private void selection(String entryPoint, AonData aonData) {
		export2JS(aonData);
		ensureGwtSelector();
		switch (entryPoint) {
		case Modules.ISSUES:
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
			GWT.runAsync(Documental.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					documental = new Documental(aonData);
					documental.onModuleLoad();
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
					stat.onModuleLoad(getToken() != null ? "StatControlPanel" : getSubEntryPoint());
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
		case Modules.QUALITY:
			GWT.runAsync(Udapa.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Udapa(aonData).onModuleLoad(getSubEntryPoint());
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
			
		case Modules.SCOPE:
			GWT.runAsync(ScopeMain.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new ScopeMain(aonData).onModuleLoad();
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
					new DepositEntryPoint(aonData).onModuleLoad(getToken() != null ? "deposit_new" : getSubEntryPoint());
				}
			});		
			break;
		case Modules.COMMISSION:
			GWT.runAsync(Commercial.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Commercial(aonData).onModuleLoad();
				}
			});		
			break;
		case Modules.EMPLOYEES:
			GWT.runAsync(EmployeeTree.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					
					EmployeeTree employeeTree = new EmployeeTree();
					employeeTree.onModuleLoad();
				}
			});		
			break;
			
		default:
			break;
		}
	}


	
	private static void export2JS(AonData aonData) {
		export2JS(aonData.getUser());
		export2JS(aonData.getDomain());
	}
//  getCurrentUser = function(){
//  return '#{domainSwitcher.currentUser}';
//  }
	private static native void export2JS(User user) /*-{
		$wnd.getCurrentUser = $entry(function() {
			return user.@com.esferalia.aon.occam.api.model.security.User::getLogin()();
		});
	}-*/;
//  getCurrentDomainNameURL = function(){
//  return '#{domainSwitcher.currentDomainNameURL}';
//  }
	private static native void export2JS(Domain domain) /*-{
		$wnd.getCurrentDomainNameURL = $entry(function() {
			return domain.@com.esferalia.aon.occam.api.model.Domain::getName()();
		});
	}-*/;

	private static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}

}
