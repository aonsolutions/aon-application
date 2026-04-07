package com.esferalia.aon.dsi.nominas.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.dsi.nominas.Traspaso;
import com.esferalia.aon.dsi.nominas.model.Convenio;
import com.esferalia.aon.watson.server.AonDateUtils;

@Disabled
public class TestParadox {
	
//	VARIOS TEST PARA CONEXION PARADOX NOMINAS OMEGA

	private final String OMEGA_CONNECTION_STRING = "jdbc:paradox:/cometa32/nomin/003";
	
	private Connection conn;

	@BeforeEach
	public void connect() throws Exception {
		conn = Traspaso.getDsiConnection(OMEGA_CONNECTION_STRING);
	}

	@AfterEach
	public void closeConnection() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}

    public void showCount(String table) throws SQLException {
        		 
        try (final Statement statement = conn.createStatement()) {
            try (final ResultSet rs = statement.executeQuery("SELECT * FROM "+table)) {        	
                int total = 0;
                while (rs.next()) {
                    total++;
                    
                }
                System.out.println("TOTAL " + table + " = " +total);
            }
        }
        
//        try (final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM "+table)) {
//                try (final ResultSet rs = preparedStatement.executeQuery()) {        	
//                int total = 0;
//                while (rs.next()) {
//                    total++;                    
//                }
//                System.out.println("TOTAL " + table + " = " +total);
//            }        	
//              
//        }

    }	
	
	@Disabled
	@Test
    public void testShowCount() throws SQLException {
		
		showCount("FNCALENC");
		showCount("FNCALENL");
		
		showCount("FNCONVEN");
		showCount("FNCCONCE");
		showCount("FNCPAGAS");
		showCount("FNCANTIG");
		showCount("FNCCOMPL");
		
		showCount("FNCATEGO");
		showCount("FNZCONCE");
		showCount("FNZPAGAS");
		showCount("FNZANTIG");
		
		showCount("FNEMPRES"); // **
		showCount("FNECENTR");
		showCount("FNEBANCO");
		showCount("FNECONCE");
		showCount("FNEPAGAS");
		showCount("FNEANTIG");
		showCount("FNECOMPL");
		
		showCount("FNTRABAJ"); // **
		showCount("FNTRABA2");
		showCount("FNTCONCE");
		showCount("FNTPAGAS");
		showCount("FNTANTIG");
		
		showCount("FNNCOMPC"); // **
		showCount("FNNCOMPL");

		conn.close();
		conn = DriverManager.getConnection(OMEGA_CONNECTION_STRING+"/2021");

		showCount("FNNOMINC"); // **
		showCount("FNNOMINL");
		
		System.out.println("** FIN **");
		
    }
	
	@Disabled
	@Test
    public void testNullJoin() throws SQLException {
		
		//String sql = "SELECT * FROM FNEMPRES WHERE F20FBAJA IS NULL";
		//String sql = "SELECT * FROM FNTRABAJ WHERE F20FBAJA IS NULL";
		
		String sql = "SELECT * FROM FNTRABAJ AS T "
				+ "LEFT JOIN FNTRABA2 AS T2 ON T2.F20SSCODEM=T.F20SSCODEM AND T2.F20SSNUMEM=T.F20SSNUMEM AND T2.F20SSCOD=T.F20SSCOD AND T2.F20SSNUM=T.F20SSNUM AND T2.F20FALTA=T.F20FALTA "                    
				+ "WHERE T.F20FBAJA IS NULL OR F20FBAJA >= '2021-01-01'";  
		
        try (final Statement statement = conn.createStatement()) {
            try (final ResultSet rs = statement.executeQuery(sql)) {        	
                int total = 0;
                while (rs.next()) {
                	System.out.println(rs.getString("F20NOMBREC")+"  IBAN: "+rs.getString("F20IBAN"));
                    total++;
                }
                System.out.println("TOTAL REGISTROS = " +total);
            }
        }
        
    }
	
	@Disabled
    @Test
    public void testWithParameters() throws SQLException {
		
		String sql = "SELECT * FROM FNTRABAJ AS T "
				+ "LEFT JOIN FNTRABA2 AS T2 ON T2.F20SSCODEM=T.F20SSCODEM AND T2.F20SSNUMEM=T.F20SSNUMEM AND T2.F20SSCOD=T.F20SSCOD AND T2.F20SSNUM=T.F20SSNUM AND T2.F20FALTA=T.F20FALTA "                    
				+ "WHERE T.F20FBAJA IS NULL OR F20FBAJA >= ?";  
    	
        try (final PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
        	preparedStatement.setDate(1, AonDateUtils.toSql(AonDateUtils.getDate(2021, 0, 1)));
            try (final ResultSet rs = preparedStatement.executeQuery()) {
            	int total = 0;
            	while (rs.next()) {
                	System.out.println(rs.getString("F20NOMBREC")+"  IBAN: "+rs.getString("F20IBAN"));
                    total++;
                }
            	System.out.println("TOTAL REGISTROS = " +total);
            }
            
        }
    }
	
	@Disabled
    @Test
    public void testConvenios() throws SQLException {
		
		try (final Statement statement = conn.createStatement()) {
			LinkedList<Convenio> convenios = new LinkedList<Convenio>();
			String sql = "SELECT DISTINCT FNCONVEN.* FROM FNCONVEN " +
			             "INNER JOIN FNEMPRES ON FNEMPRES.F20CONVEN=FNCONVEN.F20CODIGO " +
					     "WHERE F20FBAJA IS NULL " +
			             "ORDER BY F20CODIGO";			
            try (final ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {                    
                    convenios.add(new Convenio()
                    				.setCodigo(rs.getString("F20CODIGO"))
                    				.setNombre(rs.getString("F20NOMBRE"))
                    				.setTc2conv(rs.getString("F20TC2CONV"))
                    				.setFecha(rs.getDate("F20FECHA"))
                    				.setHorsem(rs.getDouble("F20HORSEM"))
                    				.setTipoAntig(rs.getString("F20ANTIG"))
                    				.setTipoComple(rs.getString("F20COMPLE"))
                    				);
                }                
            }
            int totalConvenios = 0;
            for (Convenio convenio : convenios) {
            	System.out.println(convenio);
            	totalConvenios++;
            }            
            System.out.println("TOTAL CONVENIOS = " + totalConvenios);
        }
		
	}

}
