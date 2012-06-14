package com.code.aon.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

public class DataToSQL {

	private static void writeRegistrySQL( BufferedWriter out, String value ) throws IOException {
		String registry = "INSERT INTO `registry` (`domain`, `document`, `type`, `name`) VALUES (@Domain, '', 1, \"" + value + "\" );";
		out.write(registry);
		out.newLine();
		out.write("SET @Registry = (SELECT LAST_INSERT_ID());");
		out.newLine();		
	}

	private static void writeTargetSQL( BufferedWriter out ) throws IOException {
		out.write( "INSERT INTO `target` (`registry`, `domain`, `scope`) VALUES ( @Registry, @Domain, @Scope );" );
		out.newLine();		
	}

	private static void writeAddressSQL( BufferedWriter out, RawTarget target ) throws IOException {
		StringBuffer address = new StringBuffer();
		address.append( "INSERT INTO `raddress` " );
		address.append( "(`domain`, `registry`, `geozone`, `street_type`, `address`, `zip`, `city`" );
		if (! StringUtils.isEmpty(target.getAddressNumber()) ) {
			address.append( ", `number`" );
		}
		if (! StringUtils.isEmpty(target.getAddress2()) ) {
			address.append( ", `address2`" );
		}
		if (! StringUtils.isEmpty(target.getAddress3()) ) {
			address.append( ", `address3`" );
		}
		address.append( ") VALUES ( @Domain, @Registry, @Geozone, " );
		address.append( "\"" + target.getStreetType() + "\", " );
		address.append( "\"" + target.getAddress() + "\", " );
		address.append( "\"" + target.getZip() + "\", " );
		address.append( "\"" + target.getCity() + "\" " );
		if (! StringUtils.isEmpty(target.getAddressNumber()) ) {
			address.append( ", \"" + target.getAddressNumber() + "\" " );
		}
		if (! StringUtils.isEmpty(target.getAddress2()) ) {
			address.append( ", \"" + target.getAddress2() + "\" " );
		}
		if (! StringUtils.isEmpty(target.getAddress3()) ) {
			address.append( ", \"" + target.getAddress3() + "\" " );
		}
		address.append( ");" );
		out.write(address.toString());
		out.newLine();
		out.write("SET @RegistryAddress = (SELECT LAST_INSERT_ID());");
		out.newLine();
	}
	
	private static void writePhoneSQL( BufferedWriter out, String value ) throws IOException {
		if (! StringUtils.isEmpty(value) ) {
			out.write("INSERT INTO `rmedia` (`domain`, `registry`, `media`, `raddress`, `value`) VALUES ( @Domain, @Registry, 1, @RegistryAddress, \"" + value + "\" );");
			out.newLine();			
		}
	}

	private static void writeEmailSQL( BufferedWriter out, String value ) throws IOException {
		if (! StringUtils.isEmpty(value) ) {
			out.write("INSERT INTO `rmedia` (`domain`, `registry`, `media`, `raddress`, `value`) VALUES ( @Domain, @Registry, 4, @RegistryAddress, \"" + value + "\" );");
			out.newLine();			
		}
	}

	private static void writeNoteSQL( BufferedWriter out, String label, String value ) throws IOException {
		if (! StringUtils.isEmpty(value) ) {
			out.write("INSERT INTO `raddinfo` (`domain`, `registry`, `value_date`, `attribute`, `value`) VALUES ( @Domain, @Registry, CURRENT_DATE(), \"" + label +"\", \"" + value +"\" );");
			out.newLine();			
		}
	}
	
	private static void writeSegmentSQL( BufferedWriter out ) throws IOException {
		out.write( "INSERT INTO `rsegment` (`registry`, `domain`, `segment`) VALUES ( @Registry, @Domain, @Segment1 );" );
		out.newLine();		
		out.write( "INSERT INTO `rsegment` (`registry`, `domain`, `segment`) VALUES ( @Registry, @Domain, @Segment2 );" );
		out.newLine();		
	}
	
	private static void writeSQL( BufferedWriter out, RawTarget target ) throws IOException {
		writeRegistrySQL(out, target.getName());
		writeTargetSQL(out);
		writeAddressSQL(out, target);
		writePhoneSQL(out, target.getPhone1());
		writePhoneSQL(out, target.getPhone2());
		writeEmailSQL(out, target.getEmail1());
		writeEmailSQL(out, target.getEmail2());
		writeNoteSQL(out, "ID", target.getId());
		writeNoteSQL(out, "CNAE", target.getCnaeCode());
		writeSegmentSQL(out);
		out.newLine();
	}

	private static void writeDeleteSQL( BufferedWriter out, RawTarget target ) throws IOException {
		for( int i = 11919; i < 15806; i++ ) {
		// out.write("SET @Registry = (SELECT registry FROM `raddinfo` WHERE value = \"" + target.getId() + "\" AND domain = @Domain);");
		out.write("SET @Registry = " + i + ";" );
		out.newLine();		
		out.write("DELETE FROM `rsegment` WHERE registry = @Registry;");
		out.newLine();		
		out.write("DELETE FROM `rmedia` WHERE registry = @Registry;");
		out.newLine();		
		out.write("DELETE FROM `raddress` WHERE registry = @Registry;");
		out.newLine();		
		out.write("DELETE FROM `target` WHERE registry = @Registry;");
		out.newLine();		
		out.write("DELETE FROM `raddinfo` WHERE registry = @Registry;");
		out.newLine();		
		out.write("DELETE FROM `registry` WHERE id = @Registry;");
		out.newLine();
		out.newLine();			
		}
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File( "\\Parte4.txt" );
		File file2 = new File( "\\Parte4.sql" );
		File file3 = new File( "\\Parte4.delete.sql" );
		BufferedWriter out1 = new BufferedWriter(new FileWriter(file2));
		BufferedWriter out2 = new BufferedWriter(new FileWriter(file3));
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		while( it.hasNext() ) {
			String line = it.nextLine();
			String[] values = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t");
			RawTarget target = new RawTarget(values);
			writeSQL(out1, target);
			// writeDeleteSQL(out2, target);
		}
		writeDeleteSQL(out2, null);
		IOUtils.closeQuietly(out1);
		IOUtils.closeQuietly(out2);
	}

}