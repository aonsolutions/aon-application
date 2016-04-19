package com.esferalia.aon.gwt.office.client.notification;

import com.google.gwt.i18n.client.Messages;

public interface NotificationMessages extends Messages {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF ! --> \u0021 ¡ --> \u00A1
	
	
	@DefaultMessage("Configurar Notificaciones")
	String notificationConfiguration();
	
	@DefaultMessage("Cuenta de correo")
	String emailAccount();

	@DefaultMessage("Firma de correo")
	String emailSign();
	
	@DefaultMessage("Incluir logo en la cabecera")
	String logoInclude();

	@DefaultMessage("Notificar autom\u00e1ticamente por correo electr\u00f3nico")
	String autoNotify();
	
	@DefaultMessage("Incluir historial completo en respuesta")
	String historyInclude();
	
	@DefaultMessage("Incluir en BCC")
	String bccInclude();
	
	@DefaultMessage("No enviar ninguna notificaci\u00f3n")
	String notSendNotify();

	@DefaultMessage("Estado de pruebas")
	String testStatus();
	
	@DefaultMessage("Entorno de producci\u00f3n REAL")
	String realStatus();

	@DefaultMessage("Modo")
	String mode();
}
