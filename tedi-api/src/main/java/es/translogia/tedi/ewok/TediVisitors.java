package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediVisitors implements Serializable {
	
	private static final long serialVersionUID = -6168794006118890419L;
	
	private TediVisitors() {
		
	}

	public static interface TediInvoiceTypeVisitor {
		void visitEMITIDA();
		void visitRECIBIDA();
		void visitTICKET();
	}

}
