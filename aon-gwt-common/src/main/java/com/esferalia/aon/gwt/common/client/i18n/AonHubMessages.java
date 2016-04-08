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
	// ¿ --> \u00BF ! --> \u0021 ¡ --> \u00A1
	
	@DefaultMessage("Incidencias")
	String issues();
	
	@DefaultMessage("AVISO")
	String newIssue();
	
	@DefaultMessage("ETIQUETAS")
	String labels();
  	
 	@DefaultMessage("Criterio de b\u00FAsqueda")
	String searchCriteria();
  	
 	@DefaultMessage("Mostrar a partir de ")
	String showFrom(); 	

 	@DefaultMessage("Abiertas")
	String openIssues();
 	
 	@DefaultMessage("Cerradas")
	String closedIssues();

 	@DefaultMessage("Todas")
	String allIssues();

 	@DefaultMessage("T\u00EDtulo")
	String title();
 	
 	@DefaultMessage("Remitente")
	String sender();
  	
 	@DefaultMessage("Grabar")
	String save();
	
 	@DefaultMessage("Tipo Aviso")
	String noticeType();
 	
 	@DefaultMessage("Prioridad")
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
	
	@DefaultMessage("VOLVER")
	String back();
	
	@DefaultMessage("FAQ")
	String FAQ();
}
