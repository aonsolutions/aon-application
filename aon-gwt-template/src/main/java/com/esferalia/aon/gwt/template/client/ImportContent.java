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
		typeList.addItem("Facturas","invoice");
		typeList.addItem("Clientes, Proveedores y Acreedores","registry");
		typeList.addItem("Plan General Contable","pgc");
		typeList.addItem("Libro Diario","diary");
		typeList.setSelectedIndex(0);
	}
	
	@UiHandler("importButton")
	void importAction(ClickEvent event) {
		String value = typeList.getSelectedValue();
		if("registry".equals(value)) {
			importRegistry();
		} else if("invoice".equals(value)) {
			importInvoices();
		} else if("diary".equals(value)) {
			importDiary();
		} else if("pgc".equals(value)) {
			importPGC();
		}
	}	

	private void importRegistry() {
		Dialog d = new Dialog("Importar Registry","Importar",true,"Cancelar",true,"importOnly");
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(aonData, d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.REGISTRY, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Registry","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertRegistries(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
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
	
	private void importPGC() {
		Dialog d = new Dialog("Importar Plan General Contable","Importar",true,"Cancelar",true,"importOnly");
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(aonData, d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.PGC, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Plan General Contable","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertPGC(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
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
	
	private void importDiary() {
		Dialog d = new Dialog("Importar Diario","Importar",true,"Cancelar",true,"importOnly");
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(aonData, d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.DIARY, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Diario","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertDiary(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
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
	
	private void importInvoices(){		
		Dialog d = new Dialog("Importar Invoice","Importar",true,"Cancelar",true,"importOnly");
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(aonData, d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer lines) {
						hide();

						pbd = new ProgressBarDialog(lines.doubleValue(), 0.46, "Procesando Excel...") {};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.INVOICE, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								pbd.completed();
								pbd.hide();
								pbd = new ProgressBarDialog("Importando Facturas...") {};
								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.show();
								insertInvoice(0, lines);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
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

	private void insertInvoice(Integer index, Integer lines) {
		item.insertInvoices(getDomain(), getUser(), index, new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertInvoice(result.getLine() + 1, lines);
				} else {
					pbd.completed();
					pbd.hide();
					Error error = new Error();
					error.setError(verror.size() == 0);
					error.setTextError(verror);
					Dialog dialog = new Dialog("Importar Facturas","Aceptar",true,"Cancelar",false,"importResponse");
					dialog.setError(error);
					TemplatesDialog popup2 = new TemplatesDialog(getAonData(), dialog){

						@Override
						protected void onAccept() {
							hide();			
						}

						@Override
						protected void onCancel() {
							hide();
						}
					};
					popup2.addStyleName("gwt-PopupPanel-template");
					popup2.setGlassEnabled(true);
					popup2.show();
				}
			}
			
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		});
	}
}
