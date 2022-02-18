package com.esferalia.aon.dsi.nominas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.model.Antiguedad;
import com.esferalia.aon.dsi.nominas.model.Complemento;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Convenio;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConvenioDAO {
	
	// Devuelve todos los convenios (que están en uso por las empresas que se traspasaran)
	public static LinkedList<Convenio> select(Connection dsiConn) throws SQLException {
		
		try (final Statement statement = dsiConn.createStatement()) {
			LinkedList<Convenio> convenios = new LinkedList<Convenio>();
			String sql = "SELECT DISTINCT FNCONVEN.* FROM FNCONVEN " +
			             "INNER JOIN FNEMPRES ON FNEMPRES.F20CONVEN=FNCONVEN.F20CODIGO " +
					     "WHERE F20FBAJA IS NULL " +
			             "ORDER BY F20CODIGO";			

            try (final ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                	if (AonStringUtils.isNotBlank(rs.getString("F20CODIGO")))
	                    convenios.add(new Convenio()
	                    				.setCodigo(rs.getString("F20CODIGO"))
	                    				.setNombre(rs.getString("F20NOMBRE"))
	                    				.setTc2conv(rs.getString("F20TC2CONV"))
	                    				.setFecha(rs.getDate("F20FECHA"))
	                    				.setHorsem(rs.getDouble("F20HORSEM"))
	                    				.setTipoAntig(rs.getString("F20ANTIG"))
	                    				.setTipoComple(rs.getString("F20COMPLE"))
	                    				.setConceptos(getConceptos(dsiConn, rs.getString("F20CODIGO")))
	                    				.setPagas(getPagas(dsiConn, rs.getString("F20CODIGO")))
	                    				.setAntiguedades(getAntiguedades(dsiConn, rs.getString("F20CODIGO")))
	                    				.setComplementos(getComplementos(dsiConn, rs.getString("F20CODIGO")))
	                    				.setCategorias(CategoriaDAO.select(dsiConn, rs.getString("F20CODIGO")))
	                    				);
                }                
            }
            return convenios;
        }
		
	}
	
	// Devuelve todos los conceptos del convenio que se le pasa
	private static LinkedList<Concepto> getConceptos(Connection dsiConn, String codConvenio) throws SQLException {
		
		String sql = "SELECT * FROM FNCCONCE WHERE F21CODIGO = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Concepto> conceptos = new LinkedList<Concepto>();
			
			prepareStatement.setString(1, codConvenio);
						
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
					                	.setClaveCRA(rs.getString("F21CLAVECRA")));
                }                
            }
            return conceptos;
        }
		
	}
	
	// Devuelve todas las pagas extras del convenio que se le pasa
	private static LinkedList<Paga> getPagas(Connection dsiConn, String codConvenio) throws SQLException {
		
		String sql = "SELECT * FROM FNCPAGAS WHERE F21CODIGO = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Paga> pagas = new LinkedList<Paga>();
			
			prepareStatement.setString(1, codConvenio);
						
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
	
	// Devuelve toda la tabla de antigüedad del convenio que se le pasa
	private static LinkedList<Antiguedad> getAntiguedades(Connection dsiConn, String codConvenio) throws SQLException {
		
		String sql = "SELECT * FROM FNCANTIG WHERE F21CODIGO = ? ORDER BY F21ANNOS";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Antiguedad> antiguedades = new LinkedList<Antiguedad>();
			
			prepareStatement.setString(1, codConvenio);
						
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
	
	// Devuelve todos los complementos por enfermedad/accidente del convenio que se le pasa
	private static LinkedList<Complemento> getComplementos(Connection dsiConn, String codConvenio) throws SQLException {
		
		String sql = "SELECT * FROM FNCCOMPL WHERE F21CODIGO = ? ORDER BY F21NORDEN";
		try (final PreparedStatement prepareStatement = dsiConn.prepareStatement(sql)) {
			LinkedList<Complemento> complementos = new LinkedList<Complemento>();
			
			prepareStatement.setString(1, codConvenio);
						
            try (final ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {  
                	if (AonStringUtils.isNotBlank(rs.getString("F21TIPO")))
	                    complementos.add(new Complemento()
				                    		.setTipo(rs.getString("F21TIPO"))
				                    		.setPorcentaje(rs.getInt("F21VALOR"))
				                    		.setDiaDesde(rs.getInt("F21DIADES"))
				                    		.setDiaHasta(rs.getInt("F21DIAHAS"))
						                	.setCalculo(rs.getString("F21CONCEP")));
                }                
            }
            return complementos;
        }
		
	}

}
