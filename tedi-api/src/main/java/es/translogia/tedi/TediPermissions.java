package es.translogia.tedi;

import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

public class TediPermissions {

	Boolean admin = false;
	Boolean readerUser = false;
	Boolean writerUser = false;
	  
	Boolean readerCompany = false;
	Boolean writerCompany = false;
	
	Boolean readerInvoiceIssued = false;
	Boolean writerInvoiceIssued = false;
	Boolean globalInvoiceIssued = false;
	
	Boolean readerInvoiceReceived = false;
	Boolean writerInvoiceReceived = false;
	Boolean globalInvoiceReceived = false;

	Boolean readerInvoiceTicket = false;
	Boolean writerInvoiceTicket = false;
	Boolean globalInvoiceTicket = false;
	
	public TediPermissions(JSONArray array) {
		StreamSupport.stream(array.spliterator(), false).forEach(r->{
			JSONObject json = (JSONObject) r;
			if(json.get("tag").equals("admin")) {
				this.admin = json.getBoolean("status");
			} else if(json.get("tag").equals("user")) {
				this.readerUser = json.getBoolean("reader");
				this.writerUser = json.getBoolean("writer");
			} else if(json.get("tag").equals("company")) {
				this.readerCompany = json.getBoolean("reader");
				this.writerCompany = json.getBoolean("writer");
			}  else if(json.get("tag").equals("invoice_issued")) {
				this.readerInvoiceIssued = json.getBoolean("reader");
				this.writerInvoiceIssued = json.getBoolean("writer");
				this.globalInvoiceIssued = json.getBoolean("global");
			} else if(json.get("tag").equals("invoice_received")) {
				this.readerInvoiceReceived = json.getBoolean("reader");
				this.writerInvoiceReceived = json.getBoolean("writer");
				this.globalInvoiceReceived = json.getBoolean("global");
			} else if(json.get("tag").equals("invoice_ticket")) {
				this.readerInvoiceTicket = json.getBoolean("reader");
				this.writerInvoiceTicket = json.getBoolean("writer");
				this.globalInvoiceTicket = json.getBoolean("global");
			}
		});
	}
	
	public Boolean isAdmin() {
		return admin;
	}
	
	public Boolean isReaderUser() {
		return readerUser;
	}
	
	public Boolean isWriterUser() {
		return writerUser;
	}
	
	public Boolean isReaderCompany() {
		return readerCompany;
	}
	
	public Boolean isWriterCompany() {
		return writerCompany;
	}
	
	public Boolean isReaderInvoiceIssued() {
		return readerInvoiceIssued;
	}
	
	public Boolean isWriterInvoiceIssued() {
		return writerInvoiceIssued;
	}
	
	public Boolean isGlobalInvoiceIssued() {
		return globalInvoiceIssued;
	}
	
	public Boolean isReaderInvoiceReceived() {
		return readerInvoiceReceived;
	}
	
	public Boolean isWriterInvoiceReceived() {
		return writerInvoiceReceived;
	}
	
	public Boolean isGlobalInvoiceReceived() {
		return globalInvoiceReceived;
	}
	
	public Boolean isReaderInvoiceTicket() {
		return readerInvoiceTicket;
	}
	
	public Boolean isWriterInvoiceTicket() {
		return writerInvoiceTicket;
	}
	
	public Boolean isGlobalInvoiceTicket() {
		return globalInvoiceTicket;
	}
	
	public JSONArray getJSON() {
		JSONObject jsonUser = new JSONObject()
				.put("global", false)
				.put("icon", "supervisor_account")
				.put("name", "Gestión de Usuario")
				.put("reader", isReaderUser())
				.put("status", isReaderUser() || isWriterUser())
				.put("tag", "user")
				.put("writer", isWriterUser());

		JSONObject jsonCompany = new JSONObject()
				.put("global", false)
				.put("icon", "business")
				.put("name", "Gestión de Empresas")
				.put("reader", isReaderCompany())
				.put("status", isReaderCompany() || isWriterCompany())
				.put("tag", "company")
				.put("writer", isWriterCompany());

		JSONObject jsonInvoiceIssued = new JSONObject()
				.put("global", isGlobalInvoiceIssued())
				.put("name", "Gestión de Facturas Emitidas")
				.put("reader", isReaderInvoiceIssued())
				.put("status", isReaderInvoiceIssued() || isWriterInvoiceIssued())
				.put("tag", "invoice_issued")
				.put("writer", isWriterInvoiceIssued());
		
		JSONObject jsonInvoiceReceived = new JSONObject()
				.put("global", isGlobalInvoiceReceived())
				.put("name", "Gestión de Facturas Recibidas")
				.put("reader", isReaderInvoiceReceived())
				.put("status", isReaderInvoiceReceived() || isWriterInvoiceReceived())
				.put("tag", "invoice_received")
				.put("writer", isWriterInvoiceReceived());
		
		JSONObject jsonInvoiceTicket = new JSONObject()
				.put("global", isGlobalInvoiceTicket())
				.put("name", "Gestión de Facturas Emitidas")
				.put("reader", isReaderInvoiceTicket())
				.put("status", isReaderInvoiceTicket() || isWriterInvoiceTicket())
				.put("tag", "invoice_ticket")
				.put("writer", isWriterInvoiceTicket());
		
		JSONObject jsonAdmin= new JSONObject()
				.put("name", "Administrador")
				.put("reader", isAdmin())
				.put("status",isAdmin())
				.put("tag", "invoice_issued")
				.put("writer", isAdmin());
		
		return new JSONArray()
				.put(jsonUser)
				.put(jsonCompany)
				.put(jsonInvoiceIssued)
				.put(jsonInvoiceReceived)
				.put(jsonInvoiceTicket)
				.put(jsonAdmin);
	}
}
