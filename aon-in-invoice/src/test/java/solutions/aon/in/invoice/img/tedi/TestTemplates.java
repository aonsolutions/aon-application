package solutions.aon.in.invoice.img.tedi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum TestTemplates {
	T_01_RESTAURANTE_7 ("/solutions/aon/in/invoice/img/01_RESTAURANTE_7.jpg") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 07, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return -1; }
			@Override public Double getTotal(){ return 35.80; }
		},
	T_02_QUINTANAPALLA_AREAS ("/solutions/aon/in/invoice/img/02_QUINTANAPALLA_AREAS.jpg") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 07, 07, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; } // TODO: TPV01130111 -> V01130111 is valid ?
			@Override public int getDatesNumber(){ return 1; } 
			@Override public int getAmountNumber(){ return -1; }
			@Override public Double getTotal(){ return 14.85; } 
	},
	T_03_ERKIAGA ("/solutions/aon/in/invoice/img/03_ERKIAGA.jpg") {
		@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public int getDocumentsNumber(){ return 1; } 
		@Override public int getDatesNumber(){ return 1; } 
		@Override public int getAmountNumber(){ return -1; }
		@Override public Double getTotal(){ return 11.30; } 
	},
	T_04_ARTEPAN ("/solutions/aon/in/invoice/img/04_ARTEPAN.jpg") {
		@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 10, 26, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public int getDocumentsNumber(){ return 1; } 
		@Override public int getDatesNumber(){ return 1; } 
		@Override public int getAmountNumber(){ return -1; }
		@Override public Double getTotal(){ return 2.90; } 
	},
	T_05_GASOLINERA ("/solutions/aon/in/invoice/img/05_GASOLINERA.jpg") {
		@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public int getDocumentsNumber(){ return 1; } 
		@Override public int getDatesNumber(){ return 1; } 
		@Override public int getAmountNumber(){ return -1; }
		@Override public Double getTotal(){ return 60.27; } 
	},
	T_06_PUERTA_DE_BILBAO ("/solutions/aon/in/invoice/img/06_PUERTA_DE_BILBAO.jpg") {
		@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 12, 3, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public int getDocumentsNumber(){ return 1; } 
		@Override public int getDatesNumber(){ return 1; } 
		@Override public int getAmountNumber(){ return -1; }
		@Override public Double getTotal(){ return 40.8; } 
	},
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
