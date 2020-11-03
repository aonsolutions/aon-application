package solutions.aon.in.invoice.tedi;

public class TediInvoiceContext {

	private String name;
	private String document;
	
	public String getDocument() {
		return document;
	}
	public TediInvoiceContext setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public TediInvoiceContext setName(String name) {
		this.name = name;
		return this;
	}
	
}
