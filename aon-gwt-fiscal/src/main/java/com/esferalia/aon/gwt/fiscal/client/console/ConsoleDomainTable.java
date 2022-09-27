package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

class ConsoleDomainTable extends FlowPanel implements HasSelectionHandlers<JsDomain>{
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private ConsoleDomainTableCallback callback;
	private final AonDisplayGrid grid;
	private boolean running;
	private int count;
	
	interface ConsoleDomainTableCallback {
		public void showError(String message);
		public void showInfo(String message);
		public void onDelete(Integer domainId, String descrption, AsyncCallback<Boolean> cbk);
		public void onInfo(Integer domainId);
		public void onChangeActive(Integer domainId, boolean active, AsyncCallback<Domain> cbk);
		public void onChangeExpirationDate(Integer domainId, Date expireDate, AsyncCallback<Domain> cbk);
		public void onValidate(Integer domainId, String name, String descrption, AsyncCallback<Boolean> cbk);
	}
	
	
	ConsoleDomainTable(ConsoleDomainTableCallback callback) {
		this.callback = callback;
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
		add( grid );
	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label("#"),AON.CSS.aonWidth20())
			.addCell(new Label("ID"),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.type()),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
			.addCell(new Label("Act."),AON.CSS.aonWidth20())
			.addCell(new Label("Crea"),AON.CSS.aonWidth20())
			.addCell(new Label("Padre"),AON.CSS.aonWidth40())
			.addCell(new Label("Her."),AON.CSS.aonWidth20())
			.addCell(new Label("Usr."),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.name()),AON.CSS.aonWidthAuto())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidth150(), AON.CSS.aonNowrap())		
			.addCell(new Label("\u00FAlt. Acceso"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label("Expira"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label(""),AON.CSS.aonWidth100())
		;
	}
	
	public void addRow(JsDomain domain) {
		CheckBox checkBox = new CheckBox();
		checkBox.addClickHandler(e -> SelectionEvent.fire(ConsoleDomainTable.this, domain));
		
		InlineLabel typeLabel = new InlineLabel( domain.getDomainType()==null?"":domain.getDomainType().getName() );
		InlineLabel idLabel = new InlineLabel( domain.getId()==null?"":""+domain.getId() );
		InlineLabel parentIdLabel = new InlineLabel( domain.getParentId()==null?"":""+domain.getParentId() );
		InlineLabel nameLabel = new InlineLabel(domain.getName());
		InlineLabel lastAccessLabel = new InlineLabel( domain.getLastAccessDate()==null?"":AON.TIME_FORMAT.format(domain.getLastAccessDate()));
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());		 
		AonTableButton active = new AonTableButton(
			domain.isActive()?"Activo":"Inactivo"
			,domain.isActive()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff()		
		);
	
		active.addClickHandler(e -> {
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Se va a proceder al cambio de estado del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						running = true;
						Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
						callback.onChangeActive( domainId , !domain.isActive() , new AsyncCallback<Domain>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo cambiar el estado del dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Domain result) {
								running = false;
								domain.setActive(result.isActive());
								deleteButton.setEnabled(!result.isActive());
								if (result.isActive()) {
									active.removeStyleName(AON.CSS.aonIconToggleOff());
									active.addStyleName(AON.CSS.aonIconToggleOn());
									active.setTitle("Activo");
								} else {
									active.removeStyleName(AON.CSS.aonIconToggleOn());
									active.addStyleName(AON.CSS.aonIconToggleOff());
									active.setTitle("Inactivo");
								}
							}
							
						});
					}
				}
			});
		});
		
		InlineLabel domManagement = new InlineLabel();
		domManagement.setTitle( "Puede crear dominios" );
		domManagement.setStyleName(AON.CSS.aonIconLabel());
		domManagement.addStyleName( domain.isDomainManagement()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );
		
		InlineLabel heredity = new InlineLabel();
		heredity.setTitle( "Herencia de datos" );
		heredity.setStyleName(AON.CSS.aonIconLabel());
		heredity.addStyleName( domain.isEnableHeredity()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );
		
		int users = domain.getDefinedUsers() == null? 0 : AonNumberUtils.toInteger("" +  domain.getDefinedUsers());
		int maxUser = domain.getMaxDefinedUsers() == null? 0 : AonNumberUtils.toInteger("" +  domain.getMaxDefinedUsers());
		InlineLabel userLabel = new InlineLabel( users + " / " + maxUser );
		if ( users > maxUser) {
			userLabel.setStyleName(AON.CSS.aonColorRed());
		}

		InlineLabel descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
		descriptionLabel.setTitle(domain.getDescription());
		
		AonDateBox expiredDateBox = new AonDateBox();
		expiredDateBox.setValue(domain.getExpirationDate());
		expiredDateBox.addValueChangeHandler(e ->{
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Se va a proceder al cambio de fecha de expiraci\u00F3n " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						running = true;
						Date expiredDate = expiredDateBox.getValue();
						Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
						callback.onChangeExpirationDate( domainId, expiredDate , new AsyncCallback<Domain>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo cambiar la fecha de expiraci\u00F3n del dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Domain result) {
								running = false;
								expiredDateBox.setValue(result.getExpirationDate());
								expiredDateBox.addStyleName(AON.CSS.aonValueChanged());
								new Timer() {
									@Override
									public void run() {
										expiredDateBox.removeStyleName(AON.CSS.aonValueChanged());
									}
								}.schedule(3000);
							}
						});
					}
				}
			});
		});

		AonDisplayTable buttons = new AonDisplayTable();
		deleteButton.setEnabled(!domain.isActive());
		deleteButton.addClickHandler(e -> {
			if (running) {
				AonMessageDialog.show("AVISO","Hay un proceso en ejecuci\u00F3n. Espere un momento, por favor.");
			} else {
				AonConfirmDialog acd = new AonConfirmDialog();
				acd.confirm("\u00A1\u00A1ESTE PROCESO ES IRREVERSIBLE!!",
						"Se va a proceder al BORRADO del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
						, new AonConfirmDialogCallback() {
					@Override 
					public void onCancel() {
						// Nothing
					}
					
					@Override
					public void onAccept() {
						if (!running) {
							running = true;
							Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
							callback.onDelete( domainId , domain.getDescription(), new AsyncCallback<Boolean>() {
								@Override
								public void onFailure(Throwable caught) {
									running = false;
									callback.showError( "No se pudo borrar el dominio. ("+ caught.getMessage() +")");
								}
								
								@Override
								public void onSuccess(Boolean result) {
									running = false;
									if (result == null || !result ) {
										callback.showInfo( "No se pudo borrar el dominio. (Unknown cause)");
									} else {
										typeLabel.addStyleName(AON.CSS.aonTextLineThrough());
										parentIdLabel.addStyleName(AON.CSS.aonTextLineThrough());
										nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
										lastAccessLabel.addStyleName(AON.CSS.aonTextLineThrough());
										expiredDateBox.setEnabled(false);
										active.setEnabled(false);
										buttons.clear();
										Label deletedLabel = new Label("BORRADO");
										deletedLabel.setStyleName(AON.CSS.aonColorRed());
										deletedLabel.addStyleName(AON.CSS.aonBold());
										buttons.add(deletedLabel);
									}
								}
								
							});
						}
					}
				});
			}
		});

		AonTableButton validateButton = new AonTableButton(AON.MSG.validateAction(), AON.CSS.aonIconValid());
		validateButton.addClickHandler(e -> {
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Proceder con la validaci\u00F3n de integridad referencial del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
						String name = domain.getName();
						String description = domain.getDescription();
						callback.onValidate(domainId,name, description, new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo validar el dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Boolean result) {
								running = false;
							}
						});
					} else {
						AonMessageDialog.show("AVISO","Hay una validaci\u00F3n ejecut\u00E1ndose. Un momento, por favor.");
					}
				}
			});
		});
		
		AonDisplayTableRow buttonsRow = buttons.addRow();
		buttonsRow
			.addCell(deleteButton)
			.addCell(validateButton)
			;
		
		if (domain.isParent() || domain.isStandalone()) {
			AonTableButton infoButton = new AonTableButton("Resumen contrataci\u00F3n", AON.CSS.aonIconInfo());
			infoButton.addClickHandler(e -> callback.onInfo(AonNumberUtils.toInteger("" +  domain.getId())));
			buttonsRow.addCell(infoButton);
		}
		
		grid.addRow()
			.addCell( checkBox , AON.CSS.aonTextCenter())
			.addCell( new InlineLabel("" + (++count)), AON.CSS.aonTextCenter())
			.addCell( idLabel, AON.CSS.aonTextCenter())
			.addCell( typeLabel , AON.CSS.aonTextCenter())
			.addCell( active , AON.CSS.aonTextCenter())
			.addCell( domManagement , AON.CSS.aonTextCenter())
			.addCell( parentIdLabel, AON.CSS.aonTextCenter())
			.addCell( heredity , AON.CSS.aonTextCenter())
			.addCell( userLabel , AON.CSS.aonTextCenter())
			.addCell( nameLabel )
			.addCell( descriptionLabel )
			.addCell( lastAccessLabel )
			.addCell( expiredDateBox )
			.addCell( buttons )
			;
	}

	public void addFooterRow() {
		// Nothing
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsDomain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
