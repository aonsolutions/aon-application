package solutions.aon.in.invoice.img.tedi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum TestTemplates {
//		_7 ("/solutions/aon/in/invoice/img/7.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 07, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; }
//			@Override public int getDatesNumber(){ return 1; }
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 35.80; }
//		},
		AREAS ("/solutions/aon/in/invoice/img/Areas.jpg") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 07, 07, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; } // TODO: TPV01130111 -> V01130111 is valid ?
			@Override public int getDatesNumber(){ return 1; } 
			@Override public int getAmountNumber(){ return -1; }
			@Override public Double getTotal(){ return 14.85; } 
		},
//		ARTEPAN ("/solutions/aon/in/invoice/img/artepan.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 10, 26, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; } 
//			@Override public int getDatesNumber(){ return 1; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 2.90; } 
//		},
//		ERKIAGA ("/solutions/aon/in/invoice/img/ERKIAGA.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; } 
//			@Override public int getDatesNumber(){ return 1; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 11.30; } 
//		},
//		GASOLINERA ("/solutions/aon/in/invoice/img/Gasolinera.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; } 
//			@Override public int getDatesNumber(){ return 1; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 60.27; } 
//		},
//		GINOS ("/solutions/aon/in/invoice/img/ginos.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 10, 25, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 0; } 
//			@Override public int getDatesNumber(){ return 1; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 28.90; } 
//		},

		TRANSLOGIA ("/solutions/aon/in/invoice/img/Translogia.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 10, 25, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 0; } 
			@Override public int getDatesNumber(){ return 1; } 
			@Override public int getAmountNumber(){ return -1; }
			@Override public Double getTotal(){ return 28.90; } 
		},

// ----------------------------------------------------------------------------
// FAILED
//
//		FARMACIA ("/solutions/aon/in/invoice/img/FARMACIA.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; } 
//			@Override public int getDatesNumber(){ return 2; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 11.45; } 
//		},
//
//		FORUM ("/solutions/aon/in/invoice/img/FORUM.jpg") {
//			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 7, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//			@Override public int getDocumentsNumber(){ return 1; } 
//			@Override public int getDatesNumber(){ return 1; } 
//			@Override public int getAmountNumber(){ return -1; }
//			@Override public Double getTotal(){ return 13.29; } 
//		},
//	MEDIAMARKT ("/solutions/aon/in/invoice/img/mediamarkt.jpg") {
//		@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 10, 25, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
//		@Override public int getDocumentsNumber(){ return 0; } 
//		@Override public int getDatesNumber(){ return 0; } 
//		@Override public int getAmountNumber(){ return -1; }
//		@Override public Double getTotal(){ return 28.90; } 
//	},

		;

		private String file;
		
		private TestTemplates(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
		
		public abstract int getDocumentsNumber();
		public abstract int getDatesNumber();
		public abstract int getAmountNumber();
		public abstract Date getIssueDate();
		public abstract Double getTotal();
	}
