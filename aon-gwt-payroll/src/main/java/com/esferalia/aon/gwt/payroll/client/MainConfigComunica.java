package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementComunicaInfo;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunicaInfo;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainConfigComunica extends MainEntryPoint{
	
	private class WorkplaceComunicaWidgetImpl extends WorkplaceComunica {

		@Override
		protected void onInsertRow() {}
		
		@Override
		protected void onInsertRows() {
			for(WorkplaceComunicaInfo workplaceComunicaInfo : mainConfigComunicaObject.getWorkplaces())
				workplaceComunicaWidget.insertRow(workplaceComunicaInfo);
		}

		@Override
		protected void onDeleteWorkplace(Integer workplaceId) {
			mainConfigComunicaObject.deleteWorkplace(workplaceId);
		}

		@Override
		protected void onInsertWorkplace(Integer workplaceId, String description, Integer addressId) {
			mainConfigComunicaObject.insertWorkplace(workplaceId, description, addressId);
		}
		
	}
	
	private class CCCWidgetImpl extends CCC {

		@Override
		protected void onInsertRow() {}
		
		@Override
		protected void onInsertRows() {
			mainConfigComunicaObject.getCCCs().forEach(ccc -> cccWidget.insertRow(ccc));
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			mainConfigComunicaObject.deleteCCC(cccId);
		}

		@Override
		protected void onInsertCCC(EnterpriseCCC ccc) {
			mainConfigComunicaObject.insertCCC(ccc);
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return mainConfigComunicaObject.getActivities();
		}

		@Override
		protected void fireWarningMessage(Map<String, String> warningMap) {
			// Nothing to do here
		}
		
	}
	
	private class AgreementComunicaWidgetImpl extends AgreementComunica {

		@Override
		protected void onInsertRow() {}
		
		@Override
		protected void onInsertRows() {
			for(AgreementComunicaInfo agreementComunicaInfo : mainConfigComunicaObject.getAgreements())
				agreementComunicaWidget.insertRow(agreementComunicaInfo);
		}

		@Override
		protected void onDeleteAgreement(Integer agreementId) {
			mainConfigComunicaObject.deleteAgreement(agreementId);
		}

		@Override
		protected void onInsertAgreement(Integer agreementId, String description, String ssNumber) {
			mainConfigComunicaObject.insertAgreement(agreementId, description, ssNumber);
		}
		
	}

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainConfigComunica> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	TabLayoutPanel tabLayOutPanel;
	
	@UiField (provided = true)
	WorkplaceComunica workplaceComunicaWidget;
	
	@UiField (provided = true)
	CCC cccWidget;
	
	@UiField (provided = true)
	AgreementComunica agreementComunicaWidget;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private MainConfigComunicaObject mainConfigComunicaObject;
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public MainConfigComunica() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		toolbar = getToolbarPanel();
		workplaceComunicaWidget = new WorkplaceComunicaWidgetImpl();
		cccWidget = new CCCWidgetImpl();
		agreementComunicaWidget = new AgreementComunicaWidgetImpl();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		tabLayOutPanel.setAnimationDuration(1000);
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainConfigComunicaObject());
	}

	public void onModuleLoad(MainConfigComunicaObject mainConfigComunicaObject) {
		this.mainConfigComunicaObject = mainConfigComunicaObject;
		this.mainConfigComunicaObject.getComunicaEnterpriseSettings(s -> {
			workplaceComunicaWidget.setAddresses(mainConfigComunicaObject.getAddresses());
			workplaceComunicaWidget.onInsertRows();
			cccWidget.setDomain(mainConfigComunicaObject.getDomain());
			cccWidget.onInsertRows();
			agreementComunicaWidget.setServiAgreements(mainConfigComunicaObject.getServiAgreements());
			agreementComunicaWidget.onInsertRows();
		}, f -> {});
	}

	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Configuraci" + String.valueOf("\u00F3") + "n Comunic@");

		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.addClickHandler(e -> {
			onAccept(e);
		});
		toolbar.add(accept);

		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		this.mainConfigComunicaObject.setComunicaEnterpriseSettings(s -> {
			workplaceComunicaWidget.resetPreview();
			workplaceComunicaWidget.onInsertRows();
			cccWidget.resetPreview();
			cccWidget.onInsertRows();
			agreementComunicaWidget.resetPreview();
			agreementComunicaWidget.onInsertRows();
		}, f -> {});
	}

}
