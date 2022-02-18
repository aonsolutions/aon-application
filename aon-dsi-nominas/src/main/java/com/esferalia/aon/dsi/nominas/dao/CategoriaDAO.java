package com.esferalia.aon.dsi.nominas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.model.Antiguedad;
import com.esferalia.aon.dsi.nominas.model.Categoria;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CategoriaDAO {
	
	// Devuelve las categorías de un convenio
	public static LinkedList<Categoria> select(Connection dsiConn, String codConvenio) throws SQLException {
		
		String sql = "SELECT * FROM FNCATEGO WHERE F20CODCON = ?";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Categoria> categorias = new LinkedList<Categoria>();
			
			prepareStatement.setString(1, codConvenio);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {   
                	if (AonStringUtils.isNotBlank(rs.getString("F20CODCAT")))
	                    categorias.add(new Categoria()
	                    				.setCodcat(rs.getString("F20CODCAT"))
	                    				.setNomcat(rs.getString("F20NOMCAT"))
	                    				.setHorsem(rs.getDouble("F20HORSEM"))
	                    				.setTipoAntig(rs.getString("F20ANTIG"))
	                    				.setConceptos(getConceptos(dsiConn, rs.getString("F20CODCON"), rs.getString("F20CODCAT")))
	                    				.setPagas(getPagas(dsiConn, rs.getString("F20CODCON"), rs.getString("F20CODCAT")))
	                    				.setAntiguedades(getAntiguedades(dsiConn, rs.getString("F20CODCON"), rs.getString("F20CODCAT")))                    
	                    				);
                }                
            }
            return categorias;
        }		
		
	}
	
	// Devuelve todos los conceptos del convenio y categoria que se le pasa
	private static LinkedList<Concepto> getConceptos(Connection dsiConn, String codConvenio, String codCategoria) throws SQLException {
		
		String sql = "SELECT * FROM FNZCONCE WHERE F21CODCON = ? AND F21CODCAT = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Concepto> conceptos = new LinkedList<Concepto>();
			
			prepareStatement.setString(1, codConvenio);
			prepareStatement.setString(2, codCategoria);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21CLAVE")))
	                    conceptos.add(new Concepto()                    		
					                    .setClave(rs.getString("F21CLAVE"))
					                	.setNombre(rs.getString("F21NOMBRE"))
					                	.setSs(rs.getString("F21SEGSOC"))
					                	.setIrpf(rs.getString("F21IRPF"))
					                	.setPag(rs.getString("F21PAGAS"))
					                	.setEnf(rs.getString("F21ENFER"))
					                	.setAcc(rs.getString("F21ACCID"))
					                	.setTipo(rs.getString("F21TIPOCON"))
					                	.setClaveCRA(rs.getString("F21CLAVECRA"))
					                	.setImporte(rs.getDouble("F21IMPOR"))
					                	.setCobro(rs.getString("F21TIPO")));
                }                
            }
            return conceptos;
        }
		
	}
	
	// Devuelve todas las pagas extras del convenio y categoria que se le pasa
	private static LinkedList<Paga> getPagas(Connection dsiConn, String codConvenio, String codCategoria) throws SQLException {
		
		String sql = "SELECT * FROM FNZPAGAS WHERE F21CODCON = ? AND F21CODCAT = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Paga> pagas = new LinkedList<Paga>();
			
			prepareStatement.setString(1, codConvenio);
			prepareStatement.setString(2, codCategoria);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F21MES")))
	                    pagas.add(new Paga()
		                    		.setMes(rs.getString("F21MES"))
				                	.setImporte(rs.getDouble("F21VALOR"))
				                	.setDescripcion(rs.getString("F21DESCRI"))
				                	.setDiaInicio(rs.getString("F21DIADES"))
				                	.setMesInicio(rs.getString("F21MESDES"))
				                	.setAnoInicio(rs.getString("F21ANNODES")) 
				                	.setDiaFin(rs.getString("F21DIAHAS"))
				                	.setMesFin(rs.getString("F21MESHAS"))
				                	.setAnoFin(rs.getString("F21ANNOHAS"))
				                	.setTipo(rs.getString("F21TIPO")));
                }                
            }
            return pagas;
        }
		
	}
	
	// Devuelve toda la tabla de antigüedad del convenio y categoria que se le pasa
	private static LinkedList<Antiguedad> getAntiguedades(Connection dsiConn, String codConvenio, String codCategoria) throws SQLException {
		
		String sql = "SELECT * FROM FNZANTIG WHERE F21CODCON = ? AND F21CODCAT = ? ORDER BY F21ANNOS";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Antiguedad> antiguedades = new LinkedList<Antiguedad>();
			
			prepareStatement.setString(1, codConvenio);
			prepareStatement.setString(2, codCategoria);
						
			try (final ResultSet rs = prepareStatement.executeQuery()) {
				while (rs.next()) {
					if (AonStringUtils.isNotBlank(rs.getString("F21ANNOS"))) 
						antiguedades.add(new Antiguedad()
											.setAnos(rs.getString("F21ANNOS"))
											.setImporte(rs.getDouble("F21VALOR"))
											.setTipo(rs.getString("F21TIPO")));
				}
			}
            return antiguedades;
        }
		
	}	

}
