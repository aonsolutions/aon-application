package com.esferalia.aon.dsi.nominas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.model.Calendario;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CalendarioDAO {
	
	// Devuelve todos los calendarios
	public static LinkedList<Calendario> select(Connection dsiConn) throws SQLException {
		
		try (final Statement statement = dsiConn.createStatement()) {
			LinkedList<Calendario> calendarios = new LinkedList<Calendario>();
			String sql = "SELECT * FROM FNCALENC ORDER BY F20CODIGO";			
            try (final ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {                    
                	if (AonStringUtils.isNotBlank(rs.getString("F20CODIGO")))
                		calendarios.add(new Calendario()
                    				.setCodigo(rs.getString("F20CODIGO"))
                    				.setNombre(rs.getString("F20NOMBRE"))
                    				.setFestivos(getFestivos(dsiConn, rs.getString("F20CODIGO")))
                    				);
                }                
            }
            return calendarios;
        }
		
	}
	
	// Devuelve todos los festivos del calendario que se le pasa
	private static LinkedList<Date> getFestivos(Connection dsiConn, String codCalendario) throws SQLException {
		
		String sql = "SELECT * FROM FNCALENL WHERE F20CODIGO = ? ORDER BY F20FESTIVO";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Date> festivos = new LinkedList<Date>();
			
			prepareStatement.setString(1, codCalendario);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F20CODIGO")))
	                    festivos.add(rs.getDate("F20FESTIVO"));
                }                
            }
            return festivos;
        }
		
	}

}
