package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.Window;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronLabelElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperDropdownMenuElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperMenuElement;
import com.vaadin.polymer.paper.PaperProgressElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;

public class MainEntryPoint implements EntryPoint {

	interface CodeMirrorResources extends ClientBundle {
		@NotStrict
		@Source("codemirror.css")
		CssResource css();
	}

	@Override
	public void onModuleLoad() {
		
		if (!supportWebComponents()){
			__onModuleLoad();
			return;
		}
		
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC,
				PaperToggleButtonElement.SRC,
				PaperSliderElement.SRC,
				IronLabelElement.SRC,
				PaperDropdownMenuElement.SRC,
				PaperMenuElement.SRC,
				PaperItemElement.SRC,
				PaperProgressElement.SRC
		)
		);
		
		Polymer.whenReady(o -> {
			__onModuleLoad();
			return null;
		});

	}

	private void __onModuleLoad() {
		ensureGwtSelector();
		String entryPoint = getParameter(GWT.getModuleName(),
				Constants.ENTRY_POINT_PARAM);

		if (entryPoint.equalsIgnoreCase(Constants.ENTERPRISE_SITE_ENTRY_POINT)) {
			runAsync( EnterpriseSite.class, new EnterpriseSite());
		}else if (entryPoint
				.equalsIgnoreCase(Constants.EMPLOYEE_TREE_ENTRY_POINT)) {
			runAsync( EmployeeTree.class, new EmployeeTree() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_CALCULATOR_ENTRY_POINT)) {
			runAsync( MainCalculator.class, new MainCalculator() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_AGREEMENT_ENTRY_POINT)) {
			runAsync( MainAgreement.class, new MainAgreement());
		}else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_TRASH_ENTRY_POINT)) {
			runAsync(  MainTrash.class, new MainTrash() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_CRETA_ENTRY_POINT)) {
			runAsync( MainCreta.class, new MainCreta() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_AFI_ENTRY_POINT)) {
			runAsync( AgrarianAFI.class, new AgrarianAFI() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_CRA_ENTRY_POINT)) {
			runAsync( MainCRA.class, new MainCRA() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_CONTRATA_ENTRY_POINT)) {
			runAsync(MainContrataContract.class,  new MainContrataContract() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_IT_ENTRY_POINT)) {
			runAsync( MainContrataIT.class, new MainContrataIT() );
		} else if (entryPoint
				.equalsIgnoreCase(Constants.MAIN_DIGITAL_CERTIFICATES_ENTRY_POINT)) {
			runAsync(MainDigitalCertificates.class, new MainDigitalCertificates());
		} else if (entryPoint.equalsIgnoreCase(Constants.MAIN_CCC_ENTRY_POINT)) {
			runAsync(MainCCC.class, new MainCCC());
		} else if (entryPoint.equalsIgnoreCase(Constants.MAIN_CONFIG_COMUNICA_ENTRY_POINT)) {
			runAsync( MainConfigComunica.class, new MainConfigComunica());
		} else if (entryPoint
				.equalsIgnoreCase(Constants.ACTIVITY_SUMMARY_ENTRY_POINT)) {
			runAsync(ActivitySummary.class, new ActivitySummary() );
		}
	}
	

	public static void runAsync (Class<?> name,EntryPoint entryPoint) {
		if (name == MainCalculator.class ) {
			GWT.runAsync(MainCalculator.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == EnterpriseSite.class ) {
			GWT.runAsync(EnterpriseSite.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == EmployeeTree.class ) {
			GWT.runAsync(EmployeeTree.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainAgreement.class ) {
			GWT.runAsync(MainAgreement.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainTrash.class ) {
			GWT.runAsync(MainTrash.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainCreta.class ) {
			GWT.runAsync(MainCreta.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		}  else if (name == AgrarianAFI.class ) {
			GWT.runAsync(AgrarianAFI.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainCRA.class ) {
			GWT.runAsync(MainCRA.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainContrataContract.class ) {
			GWT.runAsync(MainContrataContract.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainContrataIT.class ) {
			GWT.runAsync(MainContrataIT.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainDigitalCertificates.class ) {
			GWT.runAsync(MainDigitalCertificates.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainCCC.class ) {
			GWT.runAsync(MainCCC.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else if (name == MainConfigComunica.class ) {
			GWT.runAsync(MainConfigComunica.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else {
			Window.alert("Modulo desconcido '" + name +"'");
		}
	}

	public static void runAsync (Class<?> name, Runnable runnable) {
		GWT.runAsync(name, new RunAsyncCallback() {
			
			@Override
			public void onSuccess() {
				runnable.run();
			}
			
			@Override
			public void onFailure(Throwable reason) {
                Window.alert("Error al cargar");
			}
		});
	}

	/**
	 * Fetches a parameter passed to the module's nocache script.
	 * 
	 * @param moduleName
	 *            the module's name.
	 * @param parameterName
	 *            the name of the parameter to fetch.
	 * @return the value of the parameter, or <code>null</code> if it was not
	 *         found.
	 */
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for (var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (StringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}
	
	public static native boolean supportWebComponents ()/*-{
		try {
			var object = document.createComment("")
			var nativePrototype = Object.getPrototypeOf(object);
			var descr = Object.getOwnPropertyDescriptor(Element.prototype, "classList");
			Object.defineProperty(HTMLElement.prototype, "classList", descr);
			return true;
		} catch(ex) {
			return false;
		}
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	

}
