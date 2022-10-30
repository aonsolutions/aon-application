package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;

class ConsoleDomainTableRow extends AonDisplayGridRow {
	
	private static final Logger ROW_LOGGER = Logger.getLogger(ConsoleDomainTableRow.class.getName());
	static {
		ROW_LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private ConsoleDomainTableCallback callback;
	private Integer id;
	private JsConsoleDomain domain;
	
	private CheckBox checkBox;
	private InlineLabel counterLabel;
	private InlineLabel idLabel;
	private InlineLabel typeLabel;
	private AonTableButton activeButton;
	private InlineLabel domManagementLabel;
	private InlineLabel childCountLabel;
	private InlineLabel parentIdLabel;
	private InlineLabel heredityLabel;
	private InlineLabel userLabel;
	private Anchor nameAnchor;
	private InlineLabel descriptionLabel;
	private InlineLabel lastAccessLabel;
	private AonDateBox expirationDateBox;
	private AonDisplayTable buttons = new AonDisplayTable();
	private AonTableButton deleteButton;
	private AonTableButton validateButton;
	private AonTableButton infoButton;
	private AonTableButton remoteAccessButton;
	private AonTableButton duplicateButton;
	private AonTableButton editButton;
	private Anchor dumpAnchor;
	
	
	
	public ConsoleDomainTableRow(ConsoleDomainTableCallback callback,JsConsoleDomain domain) {
		this.callback = callback;
		this.domain = domain; 
		id = AonNumberUtils.toInteger("" + domain.getId());
		
		checkBox = new CheckBox();
		checkBox.addClickHandler(e -> callback.check(this));
		
		counterLabel = new InlineLabel("" + (this.callback.addCount()));

		String domainIdString = AonNumberUtils.emptyIfNull(id);
		idLabel = new InlineLabel( domainIdString );
		
		typeLabel = new InlineLabel( AonStringUtils.defaultIfBlank( DomainType.getName(domain.getDomainType())));
		
		activeButton = new AonTableButton("Inactivo",AON.CSS.aonIconToggleOff());
		activeButton.addClickHandler(e -> changeActive(domain));
		
		domManagementLabel = new InlineLabel();
		domManagementLabel.setTitle( "Puede crear dominios" );
		domManagementLabel.setStyleName(AON.CSS.aonIconLabel());
		
		childCountLabel = new InlineLabel();
		if (domain.hasChild()) {
			Integer childCount = domain.getChildCount() == null ? null : AonNumberUtils.toInteger("" + domain.getChildCount());
			Integer activeChildCount = domain.getActiveChildCount() == null ? null : AonNumberUtils.toInteger("" + domain.getActiveChildCount());
			activeChildCount = AonNumberUtils.zeroIfNull(activeChildCount);
			String child = AonNumberUtils.toString(childCount) + " ( " + activeChildCount + " )";
			childCountLabel.setText(child);
		}
		
		
		Integer parentId = domain.getParentId() == null ? null : AonNumberUtils.toInteger("" + domain.getParentId());
		String parentIdString = AonNumberUtils.emptyIfNull(parentId);
		parentIdLabel = new InlineLabel( parentIdString );
		
		heredityLabel = new InlineLabel();
		heredityLabel.setTitle( "Herencia de datos" );
		heredityLabel.setStyleName(AON.CSS.aonIconLabel());

		Integer definedUsers = AonNumberUtils.toInteger("" + domain.getDefinedUsers());
		String definedUsersString = AonNumberUtils.emptyIfNull(definedUsers);
		Integer maxDefinedUsers = (domain.getMaxDefinedUsers() != null)
			?AonNumberUtils.toInteger("" + domain.getMaxDefinedUsers())
			:0;
		String maxDefinedUsersString = AonNumberUtils.emptyIfNull(maxDefinedUsers);
		userLabel = new InlineLabel( definedUsersString + " / " + maxDefinedUsersString );
		
		nameAnchor = new Anchor(domain.getName());
		nameAnchor.setStyleName(AON.CSS.aonClickableLabel());
		nameAnchor.setHref("https://" + domain.getName());
		nameAnchor.setTarget("_blank");
		
		descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
		descriptionLabel.setTitle(domain.getDescription());
		
		String lastAccessDate = getTime(domain.getLastAccessDate());
		lastAccessLabel = new InlineLabel( lastAccessDate );
		
		expirationDateBox = new AonDateBox();
		expirationDateBox.setValue(domain.getExpirationDate());
		expirationDateBox.addValueChangeHandler(e -> changeExpirationDate(domain));
		
		
		deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());		 
		deleteButton.addClickHandler(e -> delete(domain, null));
		
		validateButton = new AonTableButton(AON.MSG.validateAction(), AON.CSS.aonIconValid());
		validateButton.addClickHandler(e -> AonConfirmDialog.showConfirm("Proceder con la validaci\u00F3n de integridad referencial del dominio " 
				+ id 
				+ " - " + domain.getName() 
				+ " ("+ domain.getDescription() +")."
			, () -> validate(domain)));
		
		infoButton = new AonTableButton("Resumen contrataci\u00F3n", AON.CSS.aonIconInfo());
		infoButton.addClickHandler(e -> callback.onInfo(id));
					
		
		remoteAccessButton = new AonTableButton("Acceso remoto", AON.CSS.aonIconWrench());
		remoteAccessButton.addClickHandler(e -> callback.onRemoteAccess(id, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.showError( "No se pudo modificar el acceso remoto. ("+ caught.getMessage() +")");
			}

			@Override
			public void onSuccess(String result) {
				if (AonStringUtils.isBlank(result)) {
					decorateRemoteAccess(false);	
				} else {
					String msg =  "Desea navegar a " + domain.getName() 
						+ "?. Puede acceder con uno de los siguientes usuarios " + result;
					AonConfirmDialog.showConfirm("Pregunta", msg
						, () -> Window.open("https://" + domain.getName(), "", ""));
					decorateRemoteAccess(true);
				}
			}
		}));
		
		duplicateButton= new AonTableButton(AON.MSG.duplicate(), AON.CSS.aonIconCopy());		 
		duplicateButton.addClickHandler(e -> duplicate(domain));

		editButton = new AonTableButton( "Editar datos" , AON.CSS.aonIconEdit());		 
		editButton.addClickHandler(e -> callback.onEditDomain(id, domain.getDescription()));
		
		
		String url = "https://dumpDomain.com"
			+ "?schema="+callback.getSchema()
			+ "&domainId="+domain.getId()
			+ "&domainName="+domain.getName();	
		dumpAnchor = new Anchor();
		dumpAnchor.setStyleName(AON.CSS.aonIconLabel());
		dumpAnchor.addStyleName(AON.CSS.aonIconDownload());
		dumpAnchor.setHref( url );
		dumpAnchor.setTarget("_blank");
		
		
		buttons.addRow()
			.addCell(deleteButton)
			.addCell(validateButton)
			.addCell(infoButton)
		;
		
		decorateRow(domain);
		
		this
			.addCell( checkBox , AON.CSS.aonTextCenter())
			.addCell( counterLabel, AON.CSS.aonTextCenter())
			.addCell( idLabel, AON.CSS.aonTextCenter())
			.addCell( typeLabel , AON.CSS.aonTextCenter())
			.addCell( activeButton , AON.CSS.aonTextCenter())
			.addCell( domManagementLabel , AON.CSS.aonTextCenter())
			.addCell( childCountLabel , AON.CSS.aonTextCenter())
			.addCell( parentIdLabel, AON.CSS.aonTextCenter())
			.addCell( heredityLabel , AON.CSS.aonTextCenter())
			.addCell( userLabel , AON.CSS.aonTextCenter())
			.addCell( nameAnchor )
			.addCell( descriptionLabel )
			.addCell( lastAccessLabel )
			.addCell( expirationDateBox )
			.addCell( buttons )
			.addCell( remoteAccessButton )
			.addCell( editButton )
			.addCell( duplicateButton )
			.addCell( dumpAnchor );
			
		
		checkBox.setEnabled( callback.isAdvancedMode() );
		checkBox.setVisible( callback.isAdvancedMode() );
		editButton.setEnabled( callback.isAdvancedMode() );
		editButton.setVisible( callback.isAdvancedMode() );
		dumpAnchor.setEnabled( callback.isAdvancedMode() );
		dumpAnchor.setVisible( callback.isAdvancedMode() );
		duplicateButton.setEnabled( callback.isAdvancedMode() );
		duplicateButton.setVisible( callback.isAdvancedMode() );
		
	}

	public Integer getId() {
		return id;
	}

	private String getTime( Date date) {
		return (date ==null?"":AON.TIME_FORMAT.format(date));
	}
	
	private boolean canRun() {
		return !callback.isRunning();
	}
	private boolean canRunElseNotify() {
		if (!canRun()) {
			AonMessageDialog.show("AVISO","Hay una proceso ejecut\u00E1ndose. Un momento, por favor.");
			return false;
		}
		return true;
	}

	private void decorateRow(JsConsoleDomain domain) {
		
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
		Integer maxDefinedUsers = (domain.getMaxDefinedUsers() != null)
			?AonNumberUtils.toInteger("" + domain.getMaxDefinedUsers())
			:0;
		if ( AonNumberUtils.compare( definedUsers , maxDefinedUsers) < 0) {
			userLabel.setStyleName(AON.CSS.aonColorRed());
		}
		decorateRemoteAccess( domain.isRemoteAccessEnabled() );
		deleteButton.setEnabled(!domain.isActive() && !domain.hasChild());
		
		checkBox.setEnabled(deleteButton.isEnabled());
		infoButton.setVisible(domain.isParent() || domain.isStandalone());
		
	}
	
	private void decorateRemoteAccess(boolean remoteAccessEnabled) {
		if (remoteAccessEnabled) {
			remoteAccessButton.setTitle("Acceso remoto HABILITADO");
			remoteAccessButton.addStyleName(AON.CSS.aonIconRedWrench());
			remoteAccessButton.removeStyleName(AON.CSS.aonIconWrench());
		} else {
			remoteAccessButton.setTitle("Acceso remoto DESHABILITADO");
			remoteAccessButton.addStyleName(AON.CSS.aonIconWrench());
			remoteAccessButton.removeStyleName(AON.CSS.aonIconRedWrench());
		}
	}

	// -----------------------------------------------------------------------
	// 																[VALIDATE]
	// -----------------------------------------------------------------------
	private void validate(JsConsoleDomain domain) {
		if (canRunElseNotify()) {
			Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
			String name = domain.getName();
			String description = domain.getDescription();
			callback.onValidate(domainId,name, description, new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.setRunning(false);
					callback.showError( "No se pudo validar el dominio. ("+ caught.getMessage() +")");
				}

				@Override
				public void onSuccess(Boolean result) {
					callback.setRunning(false);
				}
			});
		}
	}

	// -----------------------------------------------------------------------
	// 														   [CHANGE ACTIVE]
	// -----------------------------------------------------------------------
	private String getChangeActiveMessage(JsConsoleDomain domain) {
		return  "Se va a proceder al cambio de estado del dominio " 
			+ domain.getId() 
			+ " - " + domain.getName() 
			+ " ("+ domain.getDescription() +").";
	}
	
	private void changeActive(JsConsoleDomain domain) {
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
	
	private void doChangeActive(JsConsoleDomain domain) {
		callback.setRunning(true);
		Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
		callback.onChangeActive( domainId , !domain.isActive() , new AsyncCallback<Domain>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.setRunning(false);
				callback.showError( "No se pudo cambiar el estado del dominio. ("+ caught.getMessage() +")");
			}

			@Override
			public void onSuccess(Domain result) {
				callback.setRunning(false);
				domain.setActive(result.isActive());
				decorateRow(domain);
			}
		});
	}
	
	// -----------------------------------------------------------------------
	// 												  [CHANGE EXPIRATION DATE]
	// -----------------------------------------------------------------------
	private String getExpirationDateChangeMessage(JsConsoleDomain domain) {
		return  "Se va a proceder al cambio de fecha de expiraci\u00F3n del dominio " 
			+ domain.getId() 
			+ " - " + domain.getName() 
			+ " ("+ domain.getDescription() +").";
	}

	private void changeExpirationDate(JsConsoleDomain domain) {
		if (canRunElseNotify()) {
			AonConfirmDialog.showConfirm(getExpirationDateChangeMessage(domain) , () -> {
				callback.setRunning(true);
				Date expiredDate = expirationDateBox.getValue();
				Integer domainId = AonNumberUtils.toInteger("" +  domain.getId());
				callback.onChangeExpirationDate( domainId, expiredDate , new AsyncCallback<Domain>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.setRunning(false);
						callback.showError( "No se pudo cambiar la fecha de expiraci\u00F3n del dominio. ("+ caught.getMessage() +")");
					}
	
					@Override
					public void onSuccess(Domain result) {
						callback.setRunning(false);
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
	private String getDeleteMessage(JsConsoleDomain domain) {
		return  "Se va a proceder al BORRADO del dominio "  
			+ domain.getId() 
			+ " - " + domain.getName() 
			+ " ("+ domain.getDescription() +").";
	}
	
	void doDelete(String tabLabel, AsyncCallback<Boolean> cbk) {
		Integer domainId = AonNumberUtils.toInteger("" +  this.domain.getId());
		String tab =  (AonStringUtils.isBlank( tabLabel))
			?AonStringUtils.abbreviate(domain.getDescription(), 30)
			:tabLabel;
		decorateRowAsPendingDeleted("PENDIENTE");
		callback.onDelete( domainId , tab, cbk);
	}
	
	private void delete(JsConsoleDomain domain,String tabLabel) {
		if (canRunElseNotify()) {
			AonConfirmDialog.showConfirm("\u00A1\u00A1ESTE PROCESO ES IRREVERSIBLE!!",
				getDeleteMessage(domain) , () -> {
					callback.setRunning(true);
					String tab =  (AonStringUtils.isBlank( tabLabel))
						?AonStringUtils.abbreviate(domain.getDescription(), 30)
						:tabLabel;
					doDelete(tab, new DeleteAsyncCallback(ConsoleDomainTableRow.this,true));
			});
		}
	}
	
	static class DeleteAsyncCallback implements AsyncCallback<Boolean> {
		
		private ConsoleDomainTableRow row;
		private boolean manageRunning;
		
		public DeleteAsyncCallback( ConsoleDomainTableRow row, boolean manageRunning ) {
			this.manageRunning = manageRunning;
			this.row = row;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			if (manageRunning) row.callback.setRunning(false);
			row.callback.showError( "No se pudo borrar el dominio. ("+ caught.getMessage() +")");
			row.decorateRowAsPending();
		}
		
		@Override
		public void onSuccess(Boolean result) {
			if (manageRunning) row.callback.setRunning(false);
			if (result == null || !result ) {
				row.callback.showInfo( "No se pudo borrar el dominio. (Unknown cause)");
				row.decorateRowAsPending();
			} else {
				row.decorateRowAsDeleted();
			}
		}
	}
	
	// -----------------------------------------------------------------------
	// 												  			   [DUPLICATE]
	// -----------------------------------------------------------------------
	private void duplicate(JsConsoleDomain domain) {
		if (canRunElseNotify()) {
			ConsoleDomainIsolateDialog dialog = new ConsoleDomainIsolateDialog(domain, callback);
			dialog.center();
			dialog.show();
		}
	}
	
	private void decorateRowAsPending() {
		typeLabel.removeStyleName(AON.CSS.aonTextLineThrough());
		parentIdLabel.removeStyleName(AON.CSS.aonTextLineThrough());
		nameAnchor.removeStyleName(AON.CSS.aonTextLineThrough());
		lastAccessLabel.removeStyleName(AON.CSS.aonTextLineThrough());
		expirationDateBox.setEnabled(true);
		decorateRow(domain);
		buttons.clear();
		buttons.addRow()
			.addCell(deleteButton)
			.addCell(validateButton)
			.addCell(infoButton)
		;
	}

	private void decorateRowAsPendingDeleted(String msg) {
		expirationDateBox.setEnabled(false);
		activeButton.setEnabled(false);
		remoteAccessButton.setVisible(true);
		buttons.clear();
		Label deletedLabel = new Label(msg);
		deletedLabel.setStyleName(AON.CSS.aonColorOrange());
		deletedLabel.addStyleName(AON.CSS.aonBold());
		buttons.add(deletedLabel);
	}

	private void decorateRowAsDeleted() {
		checkBox.setValue(false);
		checkBox.setEnabled(false);
		typeLabel.addStyleName(AON.CSS.aonTextLineThrough());
		parentIdLabel.addStyleName(AON.CSS.aonTextLineThrough());
		nameAnchor.addStyleName(AON.CSS.aonTextLineThrough());
		lastAccessLabel.addStyleName(AON.CSS.aonTextLineThrough());
		expirationDateBox.setEnabled(false);
		activeButton.setEnabled(false);
		remoteAccessButton.setVisible(false);
		buttons.clear();
		Label deletedLabel = new Label("BORRADO");
		deletedLabel.setStyleName(AON.CSS.aonColorRed());
		deletedLabel.addStyleName(AON.CSS.aonBold());
		buttons.add(deletedLabel);
	}

	
}
