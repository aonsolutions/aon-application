package com.esferalia.aon.gwt.common.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface AonHubMessages extends Messages {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	
	@DefaultMessage("N\u00FAmero")
	String number();
	
	@DefaultMessage("Identificaci\u00F3n")
	String login();
	
	@DefaultMessage("Notificaciones")
	String notifications();
 	
 	@DefaultMessage("Repositorios")
	String repositories();
 	
 	@DefaultMessage("Search AonHub")
	String searchAonHub();
 	
 	@DefaultMessage("Nuevo")
	String newIssue();
 	
 	@DefaultMessage("Pendientes")
	String openedIssues();
 	
 	@DefaultMessage("Cerrados")
	String closedIssues();

 	@DefaultMessage("Todos")
	String allIssues();

 	@DefaultMessage("Eliminados")
	String deletedIssues();

 	@DefaultMessage("Consultas")
	String questionIssues();

 	@DefaultMessage("Errores")
	String errorIssues();

 	@DefaultMessage("FAQs")
	String faqsIssues();
 	
 	@DefaultMessage("Etiquetas")
	String labelsIssues();
 	
 	@DefaultMessage("T\u00EDtulo")
	String title();
 	
 	@DefaultMessage("Remite")
	String sender();
 	
 	@DefaultMessage("Escribir")
	String write();
 	
 	@DefaultMessage("Grabar")
	String save();
	
 	@DefaultMessage("Tipo Aviso")
	String noticeType();
 	
 	@DefaultMessage("Priority")
	String priority();

 	@DefaultMessage("Usuario")
 	String userSender();
 	
 	@DefaultMessage("Compa\u00F1\u00EDa")
 	String company();
 	
 	@DefaultMessage("Contacto")
 	String contact();
 	
 	@DefaultMessage("Destinatario")
 	String recipient();
 	
 	@DefaultMessage("Grupo de trabajo")
 	String workgroup();
 	
 	@DefaultMessage("T\u00EDtulo del nuevo aviso")
 	String titleNewNotice();
 	
 	@DefaultMessage("Prioridad que desea dar al aviso")
 	String titlePriority();
 	
 	@DefaultMessage("Remitente del aviso. Seleccione la persona")
 	String titleRemite();
 	
 	@DefaultMessage("Compa\u00F1\u00EDa donde trabaja el remitente del aviso")
 	String titleCompany();
 	
 	@DefaultMessage("Telefono de contacto")
 	String titlePhone();
 	
 	@DefaultMessage("Recurso asignado en el asunto")
 	String titleRecipient();
 	
 	@DefaultMessage("Grupo de trabajo asignado al aviso")
 	String titleWorkgroup();
}
