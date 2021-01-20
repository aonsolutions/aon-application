package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ImportContent extends Composite {
	final ITemplateAsync item = GWT.create(ITemplate.class);
	
	interface PageBinder extends UiBinder<Widget, ImportContent> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField HTMLPanel htmlPanel;
	@UiField ListBox typeList;
	@UiField Button importButton;
	
	AonData aonData;	
	ProgressBarDialog pbd;
	LinkedList<String> verror = new LinkedList<>();
	LinkedList<String> werror = new LinkedList<>();

	private AonData getAonData() {
		return aonData;
	}
	
	private Domain getDomain() {
		return getAonData().getDomain();
	}
	
	private User getUser() {
		return getAonData().getUser();
	}
	
	
	public ImportContent(AonData aonData) {
		this.aonData = aonData;
		importButton = new Button();
		initWidget(pageBinder.createAndBindUi(this));
		init();
	}
	
	private void init() {
		typeList.addItem("Facturas", ImportType.INVOICE.name());
		typeList.addItem("Clientes, Proveedores y Acreedores", ImportType.REGISTRY.name());
		typeList.addItem("Plan General Contable",ImportType.PGC.name());
		typeList.addItem("Libro Diario", ImportType.DIARY.name());
		typeList.addItem("Cuotas", ImportType.FEE.name());
		typeList.setSelectedIndex(0);
	}
	
	@UiHandler("importButton")
	void importAction(ClickEvent event) {
		ImportType type = ImportType.valueOf(typeList.getSelectedValue());
		importation(type);
	}	
	
	private void importation(ImportType type){		
		Dialog d = new Dialog("Importar " + type.getName(),"Importar",true,"Cancelar",true,"importOnly");
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(aonData, d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				pbd = new ProgressBarDialog("Procesando Excel...") {};
				pbd.addStyleName("gwt-PopupPanel-template");
				pbd.setGlassEnabled(true);
				pbd.show();
						
				item.executeExcel(getDomain(), getUser(), null, type, null, null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
					@Override
					public void onSuccess(Integer result) {
						pbd.completed();
						pbd.hide();
						pbd = new ProgressBarDialog("Importando "+ type.getName() + "...") {};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						insert(type, 0, result);
					}
						
					@Override
					public void onFailure(Throwable caught) {}
				});	
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.center();
	}

	private void insert(ImportType type, Integer index, Integer lines) {
		AsyncCallback<Error> callback = new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insert(type, result.getLine() + 1, lines);
				} else {
					pbd.completed();
					pbd.hide();
					Error error = new Error();
					error.setError(verror.size() == 0);
					error.setTextError(verror);
					error.setTextWarning(werror);
					Dialog dialog = new Dialog("Importar " + type.getName(),"Aceptar",true,"Cancelar",false,"importResponse");
					dialog.setError(error);
					TemplatesDialog popup2 = new TemplatesDialog(getAonData(), dialog){

						@Override
						protected void onAccept() {
							verror = new LinkedList<>();
							werror = new LinkedList<>();
							hide();			
						}

						@Override
						protected void onCancel() {
							verror = new LinkedList<>();
							werror = new LinkedList<>();
							hide();
						}
					};
					popup2.addStyleName("gwt-PopupPanel-template");
					popup2.setGlassEnabled(true);
					popup2.center();
				}
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};
		
		if (ImportType.INVOICE.equals(type)) {
			item.insertInvoices(getDomain(), getUser(), index, callback);
		} else if(ImportType.DIARY.equals(type)) {
			item.insertDiary(getDomain(), getUser(), index, callback);
		} else if(ImportType.PGC.equals(type)) {
			item.insertPGC(getDomain(), getUser(), index, callback);
		} else if(ImportType.REGISTRY.equals(type)) {
			item.insertRegistries(getDomain(), getUser(), index, callback);
		} else if(ImportType.FEE.equals(type)) {
			item.insertFee(getDomain(), getUser(), index, callback);
		}
	}
	
}
