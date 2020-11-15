package com.esferalia.aon.occam.test.aherse;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashMap;

import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgifesMod182 {

	private static class Registry {
		private String document;
		private String name;
		private String city;
		private String zip;
		private String province;
		private double amount;
		
		public String getDocument() {
			return document;
		}
		public Registry setDocument(String document) {
			this.document = document;
			return this;
		}
		public String getName() {
			return name;
		}
		public Registry setName(String name) {
			this.name = name;
			return this;
		}
		public String getCity() {
			return city;
		}
		public Registry setCity(String city) {
			this.city = city;
			return this;
		}
		public String getZip() {
			return zip;
		}
		public Registry setZip(String zip) {
			this.zip = zip;
			return this;
		}
		public String getProvince() {
			return province;
		}
		public Registry setProvince(String province) {
			this.province = province;
			return this;
		}
		public double getAmount() {
			return amount;
		}
		public Registry setAmount(double amount) {
			this.amount = amount;
			return this;
		}
		
	} 
	
	public static void main(String[] args) throws IOException {
		// FileWriter writer = new FileWriter("/home/ecastellano/TRABAJO/AHERSE/Model182.txt");
		PrintWriter writer = new PrintWriter("/home/ecastellano/TRABAJO/AHERSE/Model182P.txt","ISO-8859-1");
		
		HashMap<String,Registry> map = new HashMap<String,Registry>(); 
		FileReader r = new FileReader("/home/ecastellano/TRABAJO/AHERSE/Model182.csv");
		LineNumberReader reader = new LineNumberReader(r);
		int i = 0;
		double total = 0.0;
		while (reader.ready()) {
			String line = reader.readLine();
			Registry reg = parseLine( line );
			String doc = reg.getDocument();
			if (map.containsKey(doc) ) {
				map.get(doc).setAmount(AonMathUtils.round(map.get(doc).getAmount() + reg.getAmount() ));
			} else {
				map.put(doc,reg);
			}
			total = AonMathUtils.round(total + reg.getAmount());
			i++;
		}
		reader.close();
		System.out.println( "Nº líneas...: " + i + " Total ..: " + total) ;

		i = 0;
		total = 0.0;
		for (Registry reg : map.values()) {
			if ( AonMathUtils.isGreatherThanZero(reg.getAmount())) {
				++i;
				total = AonMathUtils.round(total + reg.getAmount());
			}
		}
		System.out.println( "Nº líneas...: " + i + " Total ..: " + total) ;
		
		StringBuffer buf = new StringBuffer();
		buf.append("1");
		buf.append("182");
		buf.append("2019");
		buf.append("G20124749");
		buf.append(AonFiscalFileUtils.text("AGIFES",(57-18+1)));
		buf.append("T");
		buf.append(AonFiscalFileUtils.text("943474337",(67-59+1)));
		buf.append(AonFiscalFileUtils.text("AHERSE",(107-68+1)));
		buf.append(AonFiscalFileUtils.zeros((120-108+1)));
		buf.append("  ");
		buf.append(AonFiscalFileUtils.zeros((135-123+1)));
		buf.append(AonFiscalFileUtils.unsigned(i,(144-136+1)));
		buf.append(AonFiscalFileUtils.unsigned(total,(159-145+1),2));
		buf.append("1");
		buf.append(AonFiscalFileUtils.spaces((169-161+1)));
		buf.append(AonFiscalFileUtils.spaces((209-170+1)));
		buf.append(AonFiscalFileUtils.spaces((237-210+1)));
		buf.append(AonFiscalFileUtils.spaces((500-238+1)));
		writer.write(buf.toString());
		writer.write("\r\n");
		
		for (Registry reg : map.values()) {
			if ( AonMathUtils.isGreatherThanZero(reg.getAmount())) {
				buf = new StringBuffer();
				buf.append("2");
				buf.append("182");
				buf.append("2019");
				buf.append("G20124749");
				buf.append(AonFiscalFileUtils.document(reg.getDocument()));
				buf.append(AonFiscalFileUtils.spaces(35-27+1));
				buf.append(AonFiscalFileUtils.text(AonFiscalFileUtils.changeInvalidCharacters(reg.getName()),(75-36+1)));
				buf.append(AonFiscalFileUtils.unsigned( getProvince(reg.getProvince()),(77-76+1)));
				buf.append("A");
				buf.append(AonFiscalFileUtils.unsigned(20.0,(83-79+1) ,2));
				buf.append(AonFiscalFileUtils.unsigned(reg.getAmount(),(96-84+1) ,2));
				buf.append(AonFiscalFileUtils.spaces(104-97+1));
				buf.append("F");
				buf.append(" ");
				buf.append(AonFiscalFileUtils.unsigned(0,(110-107+1) ,2));
				buf.append(" ");
				buf.append(AonFiscalFileUtils.spaces(131-112+1));
				buf.append("0");
				buf.append(AonFiscalFileUtils.spaces(500-133+1));
				writer.write(buf.toString());
				writer.write("\r\n");
			} else {
				System.out.println( reg.getDocument() + " " + reg.getName() + " " + reg.getAmount() );		
			}
		}
		writer.flush();
		writer.close();

		System.out.println( "Nº líneas...: " + i);			
	}

	private static Registry parseLine(String line) {
		String[] tokens = AonStringUtils.splitPreserveAllTokens(line, '|');
		return new Registry()
			.setDocument(tokens[0])
			.setName(tokens[1])
			.setCity(tokens[2])
			.setZip(tokens[3])
			.setProvince(tokens[4])
			.setAmount(AonNumberUtils.todouble(tokens[7]))
			;
	}
	
	private static int getProvince(String prov) {
		if (AonStringUtils.equals(prov, "GUIPUZCOA")) {
			return 20;
		} else if (AonStringUtils.equals(prov, "MADRID")) {
			return 28;
		} else if (AonStringUtils.equals(prov, "NAVARRA")) {
			return 31;
		} else if (AonStringUtils.equals(prov, "ZARAGOZA")) {
			return 50;
		} else if (AonStringUtils.equals(prov, "VIZCAYA")) {
			return 48;			
		} else {
			return 20;
		}

	}
}






