package net.aonsolutions.aon.gwt.aio.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectTasExcelDialog;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.issues.client.Issues;
import com.esferalia.aon.gwt.issues.client.IssuesEntryPoint;
import com.esferalia.aon.gwt.marketing.client.commercial.SellerModule;
import com.esferalia.aon.gwt.marketing.client.commercial.SellerWorkloadModule;
import com.esferalia.aon.gwt.marketing.client.customer.CustomerNotesModule;
import com.esferalia.aon.gwt.marketing.client.marketing.campaign.MarketingCompaignModule;
import com.esferalia.aon.gwt.marketing.client.project.ProjectModule;
import com.esferalia.aon.gwt.marketing.client.question.QuestionModule;
import com.esferalia.aon.gwt.marketing.client.scope.ScopeModule;
import com.esferalia.aon.gwt.marketing.client.tag.TagModule;
import com.esferalia.aon.gwt.marketing.client.taskholder.TaskHolderModule;
import com.esferalia.aon.gwt.payroll.client.EmployeeTree;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.template.client.Templates;
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
import net.aonsolutions.aon.gwt.ccaa.client.DepositEntryPoint;
import net.aonsolutions.aon.gwt.commercial.client.Commercial;
import net.aonsolutions.aon.gwt.communication.client.Communication;
import net.aonsolutions.aon.gwt.seres.client.Seres;
import net.aonsolutions.aon.gwt.sii.client.Sii;
import net.aonsolutions.aon.gwt.udapa.client.Udapa;
import net.aonsolutions.aon.gwt.warehouse.client.Warehouse;

public class Aio implements EntryPoint {
	
	final IAioAsync impl = GWT.create(IAio.class);	

	public static native Boolean isAonSolutions()
	/*-{
		var newAon = $wnd.localStorage.getItem("aon_solutions"); 
		return newAon ? true : false;
	}-*/;
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
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
		if(typeof $wnd.getSubEntryPoint === 'function') {
			return $wnd.getSubEntryPoint();
		} else return null;
	}-*/;
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		ensureGwtSelector();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
			
			@Override public void onSuccess(AonData result) {
				result.setRootPanel(getRootPanel() != null ? getRootPanel() : "rootPanel");
				selection(entryPoint, result);
			}
			
			@Override public void onFailure(Throwable arg0) {}
		});
		
//		if(isAonSolutions()) {
//			impl.getAonDataToken(getCurrentDomainName(), Integer.toString(getCurrentDomain()), getToken(), new AsyncCallback<AonData>() {
//				
//				@Override public void onSuccess(AonData result) {
//					result.setRootPanel(getRootPanel() != null ? getRootPanel() : "rootPanel");
//					selection(entryPoint, result);
//				}
//				
//				@Override public void onFailure(Throwable arg0) {}
//			});
//		} else {
//			impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {
//				
//				@Override public void onSuccess(AonData result) {
//					result.setRootPanel(getRootPanel() != null ? getRootPanel() : "rootPanel");
//					selection(entryPoint, result);
//				}
//				
//				@Override public void onFailure(Throwable arg0) {}
//			});
//		}

		
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
					new IssuesEntryPoint(aonData).onModuleLoad(getSubEntryPoint());
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
					new Templates(aonData).onModuleLoad(getSubEntryPoint() != null ? getSubEntryPoint() : Modules.TEMPLATES);
				}
			});		
			break;
		case Modules.IMPORT:
			GWT.runAsync(Templates.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new Templates(aonData).onModuleLoad(Modules.IMPORT);
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
			
		case Modules.QUESTION_MODULE:
			GWT.runAsync(QuestionModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					QuestionModule questionModule = new QuestionModule();
					questionModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.MARKETING_CAMPAIGN_MODULE:
			GWT.runAsync(MarketingCompaignModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					MarketingCompaignModule marketingCampaignModule = new MarketingCompaignModule();
					marketingCampaignModule.onModuleLoad();
				}
			});		
			break;
		
		case Modules.SELLER_MODULE:
			GWT.runAsync(SellerModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					SellerModule sellerModule = new SellerModule();
					sellerModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.SELLER_WORKLOAD_MODULE:
			GWT.runAsync(SellerWorkloadModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					SellerWorkloadModule sellerWorkloadModule = new SellerWorkloadModule();
					sellerWorkloadModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.PROJECT_MODULE:
			GWT.runAsync(ProjectModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					ProjectModule projectModule = new ProjectModule();
					projectModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.CUSTOMER_NOTES_MODULE:
			GWT.runAsync(CustomerNotesModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					CustomerNotesModule customerNotesModule = new CustomerNotesModule();
					customerNotesModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.TASK_HOLDER_MODULE:
			GWT.runAsync(TaskHolderModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					TaskHolderModule taskHolderModule = new TaskHolderModule();
					taskHolderModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.SCOPE_MODULE:
			GWT.runAsync(ScopeModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					ScopeModule scopeModule = new ScopeModule();
					scopeModule.onModuleLoad();
				}
			});		
			break;
			
		case Modules.TAG_MODULE:
			GWT.runAsync(TagModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					TagModule tagModule = new TagModule();
					tagModule.onModuleLoad();
				}
			});		
			break;

			
		case Modules.PROJECT_TAS_EXCEL_MODULE:
			GWT.runAsync(TagModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					new AonProjectTasExcelDialog(getCurrentDomainName(), getCurrentDomain(), getCurrentUser());
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
	
	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}
}
