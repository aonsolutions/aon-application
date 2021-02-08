package solutions.aon.in.invoice.pdf.tedi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum TestTemplates {
		AMAZON_1 ("/solutions/aon/in/invoice/pdf/AMAZON_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 14.00; }
		},
		AMAZON_2 ("/solutions/aon/in/invoice/pdf/AMAZON_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 2, 18, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 13; }
			@Override public Double getTotal(){ return 58.88; }
		},
		AMAZON_3 ("/solutions/aon/in/invoice/pdf/AMAZON_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 4, 26, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 31.70; }
		},
		AMAZON_4 ("/solutions/aon/in/invoice/pdf/AMAZON_4.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 8, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 259.82; }
		},
		AMAZON_5 ("/solutions/aon/in/invoice/pdf/AMAZON_5.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 2, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 13.45; }
		},
		AMAZON_6 ("/solutions/aon/in/invoice/pdf/AMAZON_6.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 12, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 19.90; }
		},
		AMAZON_7 ("/solutions/aon/in/invoice/pdf/AMAZON_7.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 4, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 5; }
			@Override public Double getTotal(){ return 18.98; }
		},
		AON_01_AUSARTA ("/solutions/aon/in/invoice/pdf/AON-01-AUSARTA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 2, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 4; }
			@Override public int getAmountNumber(){ return 64; }
			@Override public Double getTotal(){ return 641.77; }
		},
		AON_02_BNP ("/solutions/aon/in/invoice/pdf/AON-02-BNP.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 10; }
			@Override public Double getTotal(){ return 120.94; }
		},
		AON_04_BIP_DRIVE ("/solutions/aon/in/invoice/pdf/AON-04-BIP-DRIVE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public Double getTotal(){ return 2.11; }
		},
		AON_05_BIP_DRIVE ("/solutions/aon/in/invoice/pdf/AON-05-BIP-DRIVE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 9, 30, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 46.74; }
		},
		AON_06_PSA ("/solutions/aon/in/invoice/pdf/AON-06-PSA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 17, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 747.60; }
		},
		AON_06_TRANSLOGIA ("/solutions/aon/in/invoice/pdf/AON-06-TRANSLOGIA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 2, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 2480.5; }
		},
		AON_07_TRANSLOGIA ("/solutions/aon/in/invoice/pdf/AON-07-TRANSLOGIA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 8, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public Double getTotal(){ return 2091.0; }
		},
		AON_08_TOLEDO ("/solutions/aon/in/invoice/pdf/AON-08-TOLEDO.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 13, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 223.85; }
		},
		AON_09_VODAFONE ("/solutions/aon/in/invoice/pdf/AON-09-VODAFONE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 11, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 6; }
			@Override public int getAmountNumber(){ return 17; }
			@Override public Double getTotal(){ return 105.5; }
		},
		AON_10_VUELING ("/solutions/aon/in/invoice/pdf/AON-10-VUELING.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 17; }
			@Override public Double getTotal(){ return 163.98; }
		},
		AYSER_1 ("/solutions/aon/in/invoice/pdf/AYSER_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 4; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public Double getTotal(){ return 86.83; }
		},
		DOS_IVAS_1 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 29, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 13; }
			@Override public Double getTotal(){ return 6549.72; }
		},
		DOS_IVAS_2 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 6, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 19; }
			@Override public Double getTotal(){ return 309.57; }
		},
		DOS_IVAS_3 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 12, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 23; }
			@Override public Double getTotal(){ return 349.28; }
		},
		IBERDROLA_1 ("/solutions/aon/in/invoice/pdf/IBERDROLA_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 50; }
			@Override public Double getTotal(){ return 835.95; }
		},
		IBERDROLA_2 ("/solutions/aon/in/invoice/pdf/IBERDROLA_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 48; }
			@Override public Double getTotal(){ return 585.87; }
		},
 		MOVISTAR_1 ("/solutions/aon/in/invoice/pdf/MOVISTAR_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 20.66; }
		},
		MOVISTAR_2 ("/solutions/aon/in/invoice/pdf/MOVISTAR_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 9, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 100.18; }
		},
		MOVISTAR_3 ("/solutions/aon/in/invoice/pdf/MOVISTAR_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 4; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 84.58; }
		},
		MOVISTAR_4 ("/solutions/aon/in/invoice/pdf/MOVISTAR_4.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 5; }
			@Override public Double getTotal(){ return 89.12; }
		},
		MOVISTAR_5 ("/solutions/aon/in/invoice/pdf/MOVISTAR_5.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 5; }
			@Override public Double getTotal(){ return 87.99; }
		},
		MOVISTAR_6 ("/solutions/aon/in/invoice/pdf/MOVISTAR_6.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public Double getTotal(){ return 121.59; }
		},
		MOVISTAR_7 ("/solutions/aon/in/invoice/pdf/MOVISTAR_7.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 9, 17, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 19; }
			@Override public Double getTotal(){ return 258.50; }
		},
		NATURGY_1 ("/solutions/aon/in/invoice/pdf/NATURGY_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 11; }
			@Override public int getAmountNumber(){ return 57; }
			@Override public Double getTotal(){ return 429.86; }
		},
		NATURGY_2 ("/solutions/aon/in/invoice/pdf/NATURGY_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 9; }
			@Override public int getAmountNumber(){ return 45; }
			@Override public Double getTotal(){ return 421.39; }
		},
		NATURGY_3 ("/solutions/aon/in/invoice/pdf/NATURGY_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 8, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 8; }
			@Override public int getAmountNumber(){ return 45; }
			@Override public Double getTotal(){ return -428.46; }
		},
		ORANGE_1 ("/solutions/aon/in/invoice/pdf/ORANGE_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 9, 5, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 113; }
			@Override public Double getTotal(){ return 247.61; } 
		},
		ORANGE_2 ("/solutions/aon/in/invoice/pdf/ORANGE_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 10, 5, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 127; }
			@Override public Double getTotal(){ return 151.71; }
		},
		ORANGE_3 ("/solutions/aon/in/invoice/pdf/ORANGE_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 5, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 120; }
			@Override public Double getTotal(){ return 146.62; }
		},
		RETENCION_1 ("/solutions/aon/in/invoice/pdf/RETENCION_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 9, 5, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public Double getTotal(){ return 867.00; }
		},
		AON_2021_02_03_AMAZON ("/solutions/aon/in/invoice/pdf/AON-2021-02-03-AMAZON.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2021, 2, 3, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 53; }
			@Override public Double getTotal(){ return 1609.64; }
		},
		AON_2021_01_29_TERMOFUEL ("/solutions/aon/in/invoice/pdf/AON-2021-01-29-TERMOFUEL.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2021, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 4; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public Double getTotal(){ return 556.65; }
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
