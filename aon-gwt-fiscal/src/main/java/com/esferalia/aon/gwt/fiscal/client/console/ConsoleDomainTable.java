package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DomainType;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;

class ConsoleDomainTable extends AonDisplayGrid implements HasSelectionHandlers<JsDomain>{
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private ConsoleDomainTableCallback callback;
	private boolean running;
	private int count = 0;
	
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
		this.addStyleName(AON.CSS.aonMarginTop());
		this.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
	}
	
	private void paintHeader() {
		this.addHeaderRow()
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
		this.add( new ConsoleDomainTableRow(domain) );
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsDomain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	// -----------------------------------------------------------------------
	// 																	 [ROW]
	// -----------------------------------------------------------------------
	private static final Logger ROW_LOGGER = Logger.getLogger(ConsoleDomainTableRow.class.getName());
	static {
		ROW_LOGGER.addHandler( new ConsoleLogHandler() );
	}

	class ConsoleDomainTableRow extends AonDisplayGridRow {
		private CheckBox checkBox;
		private InlineLabel counterLabel;
		private InlineLabel idLabel;
		private InlineLabel typeLabel;
		private AonTableButton activeButton;
		private InlineLabel domManagementLabel;
		private InlineLabel parentIdLabel;
		private InlineLabel heredityLabel;
		private InlineLabel userLabel;
		private InlineLabel nameLabel;
		private InlineLabel descriptionLabel;
		private InlineLabel lastAccessLabel;
		private AonDateBox expirationDateBox;
		private AonDisplayTable buttons = new AonDisplayTable();
		private AonTableButton deleteButton;
		private AonTableButton validateButton;
		private AonTableButton infoButton;
		
		public ConsoleDomainTableRow(JsDomain domain) {
			checkBox = new CheckBox();
			checkBox.addClickHandler(e -> SelectionEvent.fire(ConsoleDomainTable.this, domain));
			
			counterLabel = new InlineLabel("" + (++count));

			ROW_LOGGER.info("6");
			Integer domainId = AonNumberUtils.toInteger("" + domain.getId());
			String domainIdString = AonNumberUtils.emptyIfNull(domainId);
			idLabel = new InlineLabel( domainIdString );
			
			ROW_LOGGER.info("7");
			typeLabel = new InlineLabel( AonStringUtils.defaultIfBlank( DomainType.getName(domain.getDomainType())));
			
			ROW_LOGGER.info("8");
			activeButton = new AonTableButton("Inactivo",AON.CSS.aonIconToggleOff());
			activeButton.addClickHandler(e -> changeActive(domain));
			
			ROW_LOGGER.info("9");
			domManagementLabel = new InlineLabel();
			domManagementLabel.setTitle( "Puede crear dominios" );
			domManagementLabel.setStyleName(AON.CSS.aonIconLabel());
			
			ROW_LOGGER.info("10");
			Integer parentId = domain.getParentId() == null ? null : AonNumberUtils.toInteger("" + domain.getParentId());
			String parentIdString = AonNumberUtils.emptyIfNull(parentId);
			parentIdLabel = new InlineLabel( parentIdString );
			
			ROW_LOGGER.info("11");
			heredityLabel = new InlineLabel();
			heredityLabel.setTitle( "Herencia de datos" );
			heredityLabel.setStyleName(AON.CSS.aonIconLabel());

			ROW_LOGGER.info("12");
			Integer definedUsers = AonNumberUtils.toInteger("" + domain.getDefinedUsers());
			String definedUsersString = AonNumberUtils.emptyIfNull(definedUsers);
			Integer maxDefinedUsers = AonNumberUtils.toInteger("" + domain.getMaxDefinedUsers());
			String maxDefinedUsersString = AonNumberUtils.emptyIfNull(maxDefinedUsers);
			userLabel = new InlineLabel( definedUsersString + " / " + maxDefinedUsersString );
			
			ROW_LOGGER.info("13");
			nameLabel = new InlineLabel(domain.getName());
			
			ROW_LOGGER.info("14");
			descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
			descriptionLabel.setTitle(domain.getDescription());
			
			ROW_LOGGER.info("15");
			String lastAccessDate = getTime(domain.getLastAccessDate());
			lastAccessLabel = new InlineLabel( lastAccessDate );
			
			ROW_LOGGER.info("16");
			expirationDateBox = new AonDateBox();
			expirationDateBox.setValue(domain.getExpirationDate());
			expirationDateBox.addValueChangeHandler(e -> changeExpirationDate(domain));
			
			
			deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());		 
			deleteButton.addClickHandler(e -> delete(domain));
			
			validateButton = new AonTableButton(AON.MSG.validateAction(), AON.CSS.aonIconValid());
			validateButton.addClickHandler(e -> AonConfirmDialog.showConfirm("Proceder con la validaci\u00F3n de integridad referencial del dominio " 
					+ domain.getId() 
					+ " - " + domain.getName() 
					+ " ("+ domain.getDescription() +")."
				, () -> validate(domain)));
			
			infoButton = new AonTableButton("Resumen contrataci\u00F3n", AON.CSS.aonIconInfo());
			infoButton.addClickHandler(e -> callback.onInfo(AonNumberUtils.toInteger("" +  domain.getId())));
						
			buttons.addRow()
				.addCell(deleteButton)
				.addCell(validateButton)
				.addCell(infoButton)
			;
			
			ROW_LOGGER.info("DECORATE");
			decorateRow(domain);
			
			this
				.addCell( checkBox , AON.CSS.aonTextCenter())
				.addCell( counterLabel, AON.CSS.aonTextCenter())
				.addCell( idLabel, AON.CSS.aonTextCenter())
				.addCell( typeLabel , AON.CSS.aonTextCenter())
				.addCell( activeButton , AON.CSS.aonTextCenter())
				.addCell( domManagementLabel , AON.CSS.aonTextCenter())
				.addCell( parentIdLabel, AON.CSS.aonTextCenter())
				.addCell( heredityLabel , AON.CSS.aonTextCenter())
				.addCell( userLabel , AON.CSS.aonTextCenter())
				.addCell( nameLabel )
				.addCell( descriptionLabel )
				.addCell( lastAccessLabel )
				.addCell( expirationDateBox )
				.addCell( buttons )
			;

			
		}
		
		private String getTime( Date date) {
			return (date ==null?"":AON.TIME_FORMAT.format(date));
		}
		
		private boolean canRun() {
			return !running;
		}
		private boolean canRunElseNotify() {
			if (!canRun()) {
				AonMessageDialog.show("AVISO","Hay una validaci\u00F3n ejecut\u00E1ndose. Un momento, por favor.");
				return false;
			}
			return true;
		}

		private void decorateRow(JsDomain domain) {
			
			if (domain.isActive()) {
				activeButton.setTitle("Activo");
				activeButton.addStyleName(AON.CSS.aonIconToggleOn());
				activeButton.removeStyleName(AON.CSS.aonIconToggleOff());
			} else {
				activeButton.setTitle("Inactivo");
				activeButton.addStyleName(AON.CSS.aonIconToggleOff());
				activeButton.removeStyleName(AON.CSS.aonIconToggleOn());
			}
			if (domain.isDomainManagement()) {
				domManagementLabel.setTitle("Puede crear dominios");
				domManagementLabel.addStyleName( AON.CSS.aonIconToggleOn());
				domManagementLabel.removeStyleName( AON.CSS.aonIconToggleOff());
			} else {
				domManagementLabel.setTitle("NO Puede crear dominios");
				domManagementLabel.addStyleName( AON.CSS.aonIconToggleOff());
				domManagementLabel.removeStyleName( AON.CSS.aonIconToggleOn());
			}
			if (domain.isEnableHeredity()) {
				heredityLabel.setTitle("Herencia habilitada");
				heredityLabel.addStyleName( AON.CSS.aonIconToggleOn());
				heredityLabel.removeStyleName( AON.CSS.aonIconToggleOff() );
			} else {
				heredityLabel.setTitle("Herencia deshabilitada");
				heredityLabel.addStyleName( AON.CSS.aonIconToggleOff() );
				heredityLabel.removeStyleName( AON.CSS.aonIconToggleOn());
			}
			
			Integer definedUsers = AonNumberUtils.toInteger("" + domain.getDefinedUsers());
			Integer maxDefinedUsers = AonNumberUtils.toInteger("" + domain.getMaxDefinedUsers());
			if ( AonNumberUtils.compare( definedUsers , maxDefinedUsers) < 0) {
				userLabel.setStyleName(AON.CSS.aonColorRed());
			}
			
			deleteButton.setEnabled(!domain.isActive());
			
			checkBox.setEnabled(deleteButton.isEnabled());
			infoButton.setVisible(domain.isParent() || domain.isStandalone());
			
		}
		
		// -----------------------------------------------------------------------
		// 																[VALIDATE]
		// -----------------------------------------------------------------------
		private void validate(JsDomain domain) {
			if (canRunElseNotify()) {
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
			}
		}

		// -----------------------------------------------------------------------
		// 														   [CHANGE ACTIVE]
		// -----------------------------------------------------------------------
		private String getChangeActiveMessage(JsDomain domain) {
			return  "Se va a proceder al cambio de estado del dominio " 
				+ domain.getId() 
				+ " - " + domain.getName() 
				+ " ("+ domain.getDescription() +").";
		}
		
		private void changeActive(JsDomain domain) {
			if (canRunElseNotify()) {
				if (domain.getLastAccessDate() != null ) {
					Date today = new Date();
					CalendarUtil.addDaysToDate(today, -90);
					if (domain.getLastAccessDate().after(today)) {
						AonConfirmDialog.showConfirm(getChangeActiveMessage(domain),() -> doChangeActive( domain ));
					} else {
						doChangeActive( domain );
					}
				} else {
					doChangeActive( domain );
				}
			}
		}
		
		private void doChangeActive(JsDomain domain) {
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
					decorateRow(domain);
				}
			});
		}
		
		// -----------------------------------------------------------------------
		// 												  [CHANGE EXPIRATION DATE]
		// -----------------------------------------------------------------------
		private String getExpirationDateChangeMessage(JsDomain domain) {
			return  "Se va a proceder al cambio de fecha de expiraci\u00F3n del dominio " 
				+ domain.getId() 
				+ " - " + domain.getName() 
				+ " ("+ domain.getDescription() +").";
		}

		private void changeExpirationDate(JsDomain domain) {
			if (canRunElseNotify()) {
				AonConfirmDialog.showConfirm(getExpirationDateChangeMessage(domain) , () -> {
					running = true;
					Date expiredDate = expirationDateBox.getValue();
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
							expirationDateBox.setValue(result.getExpirationDate());
							expirationDateBox.addStyleName(AON.CSS.aonValueChanged());
							new Timer() {
								@Override
								public void run() {
									expirationDateBox.removeStyleName(AON.CSS.aonValueChanged());
								}
							}.schedule(3000);
						}
					});
				});
			}
		}
		
		// -----------------------------------------------------------------------
		// 												  				  [DELETE]
		// -----------------------------------------------------------------------
		private String getDeleteMessage(JsDomain domain) {
			return  "Se va a proceder al BORRADO del dominio "  
				+ domain.getId() 
				+ " - " + domain.getName() 
				+ " ("+ domain.getDescription() +").";
		}
		
		private void delete(JsDomain domain) {
			if (canRunElseNotify()) {
				AonConfirmDialog.showConfirm("\u00A1\u00A1ESTE PROCESO ES IRREVERSIBLE!!",
					getDeleteMessage(domain) , () -> {
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
									expirationDateBox.setEnabled(false);
									activeButton.setEnabled(false);
									buttons.clear();
									Label deletedLabel = new Label("BORRADO");
									deletedLabel.setStyleName(AON.CSS.aonColorRed());
									deletedLabel.addStyleName(AON.CSS.aonBold());
									buttons.add(deletedLabel);
								}
							}
						});
				});
			}
		}
	}
	
}
