package com.code.aon.aio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jooq.tools.csv.CSVReader;

import com.code.aon.common.enumeration.MimeType;

public class Serviconvenios2SQL {

	
	public static void main(String[] args) {
		CSVReader reader = null;
		try {
			String RPATH = "/home/ecastellano/TRABAJO/serviconvenios/convenios.csv";
			String WPATH = "/home/ecastellano/TRABAJO/serviconvenios/convenios.sql";
			FileOutputStream fos = new FileOutputStream(WPATH);
			OutputStreamWriter fosWriter = new OutputStreamWriter(fos, Charset.forName("UTF-8") ); 
			PrintWriter writer = new PrintWriter(fosWriter);
			writer.println( "SET @DOMAIN=3;");
			writer.println( "SET @REGISTRY=(SELECT MIN(registry) FROM company WHERE domain = @DOMAIN);");
			writer.println( "SET @CATEGORY=NULL;");
			writer.println( "SET @DATA=NULL;");
			writer.println( "SET @TYPE=3;");
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			FileInputStream fis = new FileInputStream(RPATH);
			InputStreamReader fileReader = new InputStreamReader(fis, Charset.forName("UTF-8") );
			reader = new CSVReader(fileReader,';');
			Map<String,Integer> tagMap = new TreeMap<String, Integer>(); 
			Map<String,String[]> map = new TreeMap<String, String[]>();
			int tagCounter = -1000;
			int attachTagCounter = -1000;
			while (reader.hasNext() ) {
				String[] tokens = reader.readNext();
				if (tokens != null) {
					String file = tokens[6];
					if ( map.containsKey(file) ) {
						Date fileDate = format.parse( tokens[4] );
						Date mapDate = format.parse( map.get(file)[4] );
						if (mapDate.before(fileDate)) {
							map.put(file, tokens);	
						}
					} else {
						map.put(file, tokens);
					}
					String tag = tokens[1];
					if (!tagMap.containsKey(tag)) {
						tagMap.put(tag, tagCounter);
						--tagCounter;
					}
					String tag2 = tokens[2];
					if (!tagMap.containsKey(tag2)) {
						tagMap.put(tag2, tagCounter);
						--tagCounter;
					}
				}
			}
			for (Entry<String,Integer> entry: tagMap.entrySet()) {
				writer.println( String.format("INSERT INTO tag "
						+ "(id,domain,name,type) VALUES (%d,@DOMAIN,'%s',0);"
						,entry.getValue(),entry.getKey()));
			}
			for (String[] tokens : map.values()) {
				String convenio = tokens[0];
				String tag1 = tokens[1];
				String tag2 = tokens[2];
				String description = tokens[3];
				if (description.trim().length() > 64) {
					description = description.substring(0,64);
				}
				Date date1 = format.parse( tokens[4] );
				String file = tokens[6];
				int id = Integer.parseInt( file.substring(1, 8) );
				writer.println( String.format("INSERT INTO rattach "
							+"(id,domain,registry,category,mimeType,description,data,type"
							+ ",scope,security_level,attach_date,drive_id,dparent_id)"
							+" VALUES"
							+"(%d,@DOMAIN,@REGISTRY,@CATEGORY,%d,'%s',@DATA,@TYPE,null,0,'%s',null,null);"
							,(id*-1)
							,MimeType.MIME_PDF.ordinal()
							,description.trim()
							,sqlFormat.format(date1)
							));
				Integer tagKey = tagMap.get(tag1);
				writer.println( String.format("INSERT INTO rattach_tag "
						+ "(id,domain,rattach,tag)"
						+" VALUES"
						+"(%d,@DOMAIN,%d,%d);"
						,--attachTagCounter
						,(id*-1)
						,tagKey));
				tagKey = tagMap.get(tag2);
				writer.println( String.format("INSERT INTO rattach_tag "
						+ "(id,domain,rattach,tag)"
						+" VALUES"
						+"(%d,@DOMAIN,%d,%d);"
						,--attachTagCounter
						,(id*-1)
						,tagKey));				
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
	}
	
	
}
