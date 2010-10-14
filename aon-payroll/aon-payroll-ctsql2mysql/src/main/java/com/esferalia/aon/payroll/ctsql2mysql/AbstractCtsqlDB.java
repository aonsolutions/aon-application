package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.math.BigDecimal;

/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

/**
* AbstractCtsqlDB
* 
*/
public class AbstractCtsqlDB {
	
	

	private Connection ctsqlConnection;


	
	/**
	 * Action
	 * 
	 */
	public class Action {
		
		private ResultSet rs;
		
		private Action (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Indica si la Accion esta o no dentro del menu
		 * @return the column 'menu' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMenu()
		throws SQLException {
			return rs.getInt("menu");
		}
		/**
		 * Nombre de la Accion
		 * @return the column 'name' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getName()
		throws SQLException {
			return rs.getString("name");
		}
		/**
		 * Aplicacion a la que pertenece la Accion
		 * @return the column 'application_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getApplication_id()
		throws SQLException {
			return rs.getInt("application_id");
		}

		public void visitAction_denied(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM action_denied WHERE" 
					+ " action_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Action_denied action_denied = new Action_denied(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAction_denied(action_denied, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Action_denied
	 * 
	 */
	public class Action_denied {
		
		private ResultSet rs;
		
		private Action_denied (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Identificador de la Acción
		 * @return the column 'action_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAction_id()
		throws SQLException {
			return rs.getInt("action_id");
		}
		/**
		 * Identificador del Usuario
		 * @return the column 'user_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getUser_id()
		throws SQLException {
			return rs.getInt("user_id");
		}

	}
	
	
	/**
	 * Action_entry
	 * 
	 */
	public class Action_entry {
		
		private ResultSet rs;
		
		private Action_entry (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Fecha de ejecucción
		 * @return the column 'executiondate' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Timestamp getExecutiondate()
		throws SQLException {
			return rs.getTimestamp("executiondate");
		}
		/**
		 * Identificador de la Acción
		 * @return the column 'action_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAction_id()
		throws SQLException {
			return rs.getInt("action_id");
		}
		/**
		 * Identificador de la Sesión
		 * @return the column 'session_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSession_id()
		throws SQLException {
			return rs.getInt("session_id");
		}

	}
	
	
	/**
	 * Action_favorite
	 * 
	 */
	public class Action_favorite {
		
		private ResultSet rs;
		
		private Action_favorite (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Posición dentro de las Acciones Favoritas
		 * @return the column 'position' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPosition()
		throws SQLException {
			return rs.getInt("position");
		}
		/**
		 * Identificador de la Acción
		 * @return the column 'action_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAction_id()
		throws SQLException {
			return rs.getInt("action_id");
		}
		/**
		 * Identificador del Usuario
		 * @return the column 'user_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getUser_id()
		throws SQLException {
			return rs.getInt("user_id");
		}

	}
	
	
	/**
	 * Admon
	 * 
	 */
	public class Admon {
		
		private ResultSet rs;
		
		private Admon (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Administracion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Administracion
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " codadm = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr11x(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr11x WHERE" 
					+ " codadm = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr11x impr11x = new Impr11x(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr11x(impr11x, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr190 WHERE" 
					+ " codadm = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr190 impr190 = new Impr190(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr190(impr190, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Ajustes
	 * 
	 */
	public class Ajustes {
		
		private ResultSet rs;
		
		private Ajustes (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Application
	 * 
	 */
	public class Application {
		
		private ResultSet rs;
		
		private Application (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Nivel de auditoria
		 * @return the column 'audit_level' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAudit_level()
		throws SQLException {
			return rs.getInt("audit_level");
		}
		/**
		 * Nombre de la Aplicacion
		 * @return the column 'name' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getName()
		throws SQLException {
			return rs.getString("name");
		}

		public void visitSession(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM session WHERE" 
					+ " application_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Session session = new Session(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitSession(session, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAction(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM action WHERE" 
					+ " application_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Action action = new Action(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAction(action, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Autbases
	 * 
	 */
	public class Autbases {
		
		private ResultSet rs;
		
		private Autbases (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Autónomo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha inicio
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha fin
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Base
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * Cuota
		 * @return the column 'cuota' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota()
		throws SQLException {
			return rs.getBigDecimal("cuota");
		}

	}
	
	
	/**
	 * Automat
	 * 
	 */
	public class Automat {
		
		private ResultSet rs;
		
		private Automat (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Fichero Automatico
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Descripcion de Fichero Automatico
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo (Empresas/Trabajadores)
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}

		public void visitLinautom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linautom WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linautom linautom = new Linautom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinautom(linautom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Autonomos
	 * 
	 */
	public class Autonomos {
		
		private ResultSet rs;
		
		private Autonomos (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código autónomo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código persona
		 * @return the column 'persona' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPersona()
		throws SQLException {
			return rs.getInt("persona");
		}
		/**
		 * Fecha inicio gestión
		 * @return the column 'fecinigestion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinigestion()
		throws SQLException {
			return rs.getDate("fecinigestion");
		}
		/**
		 * Fecha fin gestión
		 * @return the column 'fecfingestion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfingestion()
		throws SQLException {
			return rs.getDate("fecfingestion");
		}
		/**
		 * Honorarios
		 * @return the column 'honorarios' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHonorarios()
		throws SQLException {
			return rs.getBigDecimal("honorarios");
		}
		/**
		 * Observaciones
		 * @return the column 'observaciones' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getObservaciones()
		throws SQLException {
			return rs.getString("observaciones");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Código Entidad
		 * @return the column 'entidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEntidad()
		throws SQLException {
			return rs.getString("entidad");
		}
		/**
		 * Código Sucursal
		 * @return the column 'sucursal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSucursal()
		throws SQLException {
			return rs.getString("sucursal");
		}
		/**
		 * Dígito Control
		 * @return the column 'dc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc()
		throws SQLException {
			return rs.getString("dc");
		}
		/**
		 * Código Cuenta Cliente
		 * @return the column 'cuenta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCuenta()
		throws SQLException {
			return rs.getString("cuenta");
		}
		/**
		 * Tipo autónomo
		 * @return the column 'tipautonomo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipautonomo()
		throws SQLException {
			return rs.getString("tipautonomo");
		}
		/**
		 * Fecha constitución
		 * @return the column 'fecconstitucion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecconstitucion()
		throws SQLException {
			return rs.getDate("fecconstitucion");
		}
		/**
		 * Fecha alta autónomo
		 * @return the column 'fecalta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecalta()
		throws SQLException {
			return rs.getDate("fecalta");
		}
		/**
		 * Mutua
		 * @return the column 'mutua' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMutua()
		throws SQLException {
			return rs.getString("mutua");
		}
		/**
		 * Código registro
		 * @return the column 'codregistro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodregistro()
		throws SQLException {
			return rs.getString("codregistro");
		}
		/**
		 * Descripción registro
		 * @return the column 'desregistro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesregistro()
		throws SQLException {
			return rs.getString("desregistro");
		}
		/**
		 * Tomo
		 * @return the column 'tomo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTomo()
		throws SQLException {
			return rs.getString("tomo");
		}
		/**
		 * Libro
		 * @return the column 'libro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLibro()
		throws SQLException {
			return rs.getString("libro");
		}
		/**
		 * Folio
		 * @return the column 'folio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFolio()
		throws SQLException {
			return rs.getString("folio");
		}
		/**
		 * Sección
		 * @return the column 'seccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSeccion()
		throws SQLException {
			return rs.getString("seccion");
		}
		/**
		 * Hoja
		 * @return the column 'hoja' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHoja()
		throws SQLException {
			return rs.getString("hoja");
		}
		/**
		 * Otros
		 * @return the column 'otros' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtros()
		throws SQLException {
			return rs.getString("otros");
		}
		/**
		 * Base mínima
		 * @return the column 'baseminima' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseminima()
		throws SQLException {
			return rs.getBigDecimal("baseminima");
		}
		/**
		 * Base elegida
		 * @return the column 'baseelegida' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseelegida()
		throws SQLException {
			return rs.getBigDecimal("baseelegida");
		}
		/**
		 * Base máxima
		 * @return the column 'basemaxima' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasemaxima()
		throws SQLException {
			return rs.getBigDecimal("basemaxima");
		}
		/**
		 * Solicita incremento automático
		 * @return the column 'incremento' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIncremento()
		throws SQLException {
			return rs.getString("incremento");
		}
		/**
		 * I.T. Accidente trabajo
		 * @return the column 'incapacidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIncapacidad()
		throws SQLException {
			return rs.getString("incapacidad");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

		public void visitAutbases(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autbases WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autbases autbases = new Autbases(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutbases(autbases, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Avisos
	 * 
	 */
	public class Avisos {
		
		private ResultSet rs;
		
		private Avisos (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Aviso
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Texto del Aviso
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Fecha del Aviso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Tipo Aviso Trabajador
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}

	}
	
	
	/**
	 * Basecoti
	 * 
	 */
	public class Basecoti {
		
		private ResultSet rs;
		
		private Basecoti (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Grupo Tarifa
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Grupo Tarifa
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo de Prorrateo
		 * @return the column 'indpro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndpro()
		throws SQLException {
			return rs.getString("indpro");
		}

		public void visitLinbasec(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linbasec WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linbasec linbasec = new Linbasec(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinbasec(linbasec, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCategoria(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM categoria WHERE" 
					+ " codbas = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Categoria categoria = new Categoria(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCategoria(categoria, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codbas = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " tarifa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCostes(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM costes WHERE" 
					+ " codbas = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Costes costes = new Costes(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCostes(costes, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Bonifica
	 * 
	 */
	public class Bonifica {
		
		private ResultSet rs;
		
		private Bonifica (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Codigo de Bonificacion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Concesion Bonificacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Terminacion Bonificacion
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Numero Horas Formacion
		 * @return the column 'horas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHoras()
		throws SQLException {
			return rs.getInt("horas");
		}
		/**
		 * Importe Bonificacion Directo
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Tipo importe
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Prorrateo
		 * @return the column 'prorrateo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProrrateo()
		throws SQLException {
			return rs.getString("prorrateo");
		}

	}
	
	
	/**
	 * Calculo
	 * 
	 */
	public class Calculo {
		
		private ResultSet rs;
		
		private Calculo (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Anio
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Dia
		 * @return the column 'dia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDia()
		throws SQLException {
			return rs.getInt("dia");
		}
		/**
		 * Indicador Especial I.R.P.F.
		 * @return the column 'indirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndirpf()
		throws SQLException {
			return rs.getString("indirpf");
		}
		/**
		 * Indicador de modificacion segun indicador de irpf
		 * @return the column 'aplicado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAplicado()
		throws SQLException {
			return rs.getString("aplicado");
		}
		/**
		 * Retribucion Periodo Anterior
		 * @return the column 'retr_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_ant()
		throws SQLException {
			return rs.getBigDecimal("retr_ant");
		}
		/**
		 * Retribucion Acumulada Complementos Fijos
		 * @return the column 'retr_acu_fij' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_acu_fij()
		throws SQLException {
			return rs.getBigDecimal("retr_acu_fij");
		}
		/**
		 * Retribucion Acumulada Complementos Variables
		 * @return the column 'retr_acu_var' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_acu_var()
		throws SQLException {
			return rs.getBigDecimal("retr_acu_var");
		}
		/**
		 * Retribucion Prevista Complementos Fijos
		 * @return the column 'retr_pre_fij' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_pre_fij()
		throws SQLException {
			return rs.getBigDecimal("retr_pre_fij");
		}
		/**
		 * Retribucion Prevista Complementos Variables
		 * @return the column 'retr_pre_var' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_pre_var()
		throws SQLException {
			return rs.getBigDecimal("retr_pre_var");
		}
		/**
		 * Retribucion Estimada Ejercicio Actual
		 * @return the column 'retr_estimada' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_estimada()
		throws SQLException {
			return rs.getBigDecimal("retr_estimada");
		}
		/**
		 * Retribucion a considerar
		 * @return the column 'retr_consid' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetr_consid()
		throws SQLException {
			return rs.getBigDecimal("retr_consid");
		}
		/**
		 * Aportacion S.S. Acumulada
		 * @return the column 'imp_acu_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_acu_ss()
		throws SQLException {
			return rs.getBigDecimal("imp_acu_ss");
		}
		/**
		 * Aportacion S.S. Prevista
		 * @return the column 'imp_pre_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pre_ss()
		throws SQLException {
			return rs.getBigDecimal("imp_pre_ss");
		}
		/**
		 * Minoracion Rentas Irregulares
		 * @return the column 'imp_irreg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_irreg()
		throws SQLException {
			return rs.getBigDecimal("imp_irreg");
		}
		/**
		 * Minoracion Rentas Trabajo
		 * @return the column 'imp_rentas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_rentas()
		throws SQLException {
			return rs.getBigDecimal("imp_rentas");
		}
		/**
		 * Minoracion Personal
		 * @return the column 'imp_personal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_personal()
		throws SQLException {
			return rs.getBigDecimal("imp_personal");
		}
		/**
		 * Minoracion Familiar
		 * @return the column 'imp_familiar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_familiar()
		throws SQLException {
			return rs.getBigDecimal("imp_familiar");
		}
		/**
		 * Mas de 2 descendientes
		 * @return the column 'imp_descen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_descen()
		throws SQLException {
			return rs.getBigDecimal("imp_descen");
		}
		/**
		 * Pension Compensatoria
		 * @return the column 'imp_pension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pension()
		throws SQLException {
			return rs.getBigDecimal("imp_pension");
		}
		/**
		 * Minoracion SS
		 * @return the column 'imp_css' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_css()
		throws SQLException {
			return rs.getBigDecimal("imp_css");
		}
		/**
		 * Minoracion por Ascendientes
		 * @return the column 'imp_ascen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ascen()
		throws SQLException {
			return rs.getBigDecimal("imp_ascen");
		}
		/**
		 * Base Calculo Retencion
		 * @return the column 'base_calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_calculo()
		throws SQLException {
			return rs.getBigDecimal("base_calculo");
		}
		/**
		 * Anualidades por Alimentos
		 * @return the column 'imp_anualid' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_anualid()
		throws SQLException {
			return rs.getBigDecimal("imp_anualid");
		}
		/**
		 * Cuota Calculo Retencion
		 * @return the column 'cuota_calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_calculo()
		throws SQLException {
			return rs.getBigDecimal("cuota_calculo");
		}
		/**
		 * Cuota Anualidades
		 * @return the column 'cuota_anualid' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_anualid()
		throws SQLException {
			return rs.getBigDecimal("cuota_anualid");
		}
		/**
		 * Retencion I.R.P.F. acumulada
		 * @return the column 'irpf_acu' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_acu()
		throws SQLException {
			return rs.getBigDecimal("irpf_acu");
		}
		/**
		 * Tipo I.R.P.F. calculado
		 * @return the column 'irpf_cal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_cal()
		throws SQLException {
			return rs.getBigDecimal("irpf_cal");
		}
		/**
		 * I.R.P.F. asignado
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * I.R.P.F. Anterior
		 * @return the column 'irpf_anterior' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_anterior()
		throws SQLException {
			return rs.getBigDecimal("irpf_anterior");
		}
		/**
		 * I.R.P.F. Anterior Procede de Regularizacion
		 * @return the column 'regula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRegula()
		throws SQLException {
			return rs.getString("regula");
		}
		/**
		 * Dias de contrato
		 * @return the column 'diascont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiascont()
		throws SQLException {
			return rs.getInt("diascont");
		}
		/**
		 * Indicador de hijos
		 * @return the column 'hijos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHijos()
		throws SQLException {
			return rs.getString("hijos");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Minoracion Foral por Minusvalia
		 * @return the column 'minforal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMinforal()
		throws SQLException {
			return rs.getBigDecimal("minforal");
		}
		/**
		 * Discapacidad trabajador activos
		 * @return the column 'imp_discapacidadt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_discapacidadt()
		throws SQLException {
			return rs.getBigDecimal("imp_discapacidadt");
		}
		/**
		 * Cuidado de hijos
		 * @return the column 'imp_cuidadohijo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_cuidadohijo()
		throws SQLException {
			return rs.getBigDecimal("imp_cuidadohijo");
		}
		/**
		 * Discapacidad
		 * @return the column 'imp_discapacidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_discapacidad()
		throws SQLException {
			return rs.getBigDecimal("imp_discapacidad");
		}
		/**
		 * Pensionistas
		 * @return the column 'imp_pensionista' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pensionista()
		throws SQLException {
			return rs.getBigDecimal("imp_pensionista");
		}
		/**
		 * Prolongacion actividad laboral
		 * @return the column 'imp_prolongacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_prolongacion()
		throws SQLException {
			return rs.getBigDecimal("imp_prolongacion");
		}
		/**
		 * Movilidad geográfica
		 * @return the column 'imp_movilidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_movilidad()
		throws SQLException {
			return rs.getBigDecimal("imp_movilidad");
		}
		/**
		 * Por asistencia
		 * @return the column 'imp_asistencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_asistencia()
		throws SQLException {
			return rs.getBigDecimal("imp_asistencia");
		}
		/**
		 * Retención Anual Nueva
		 * @return the column 'retanualn' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetanualn()
		throws SQLException {
			return rs.getBigDecimal("retanualn");
		}
		/**
		 * Retención Anual Base
		 * @return the column 'retanualb' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetanualb()
		throws SQLException {
			return rs.getBigDecimal("retanualb");
		}
		/**
		 * Diferencia Retención
		 * @return the column 'difret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDifret()
		throws SQLException {
			return rs.getBigDecimal("difret");
		}
		/**
		 * IRPF Anual
		 * @return the column 'irpfanual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpfanual()
		throws SQLException {
			return rs.getBigDecimal("irpfanual");
		}

	}
	
	
	/**
	 * Calen
	 * 
	 */
	public class Calen {
		
		private ResultSet rs;
		
		private Calen (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo
		 * @return the column 'codigo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodigo()
		throws SQLException {
			return rs.getString("codigo");
		}
		/**
		 * Empresa
		 * @return the column 'empresa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmpresa()
		throws SQLException {
			return rs.getString("empresa");
		}
		/**
		 * Domicilio
		 * @return the column 'domct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDomct()
		throws SQLException {
			return rs.getString("domct");
		}
		/**
		 * $column.remarks
		 * @return the column 'cp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCp()
		throws SQLException {
			return rs.getString("cp");
		}
		/**
		 * Municipio
		 * @return the column 'municipio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMunicipio()
		throws SQLException {
			return rs.getString("municipio");
		}
		/**
		 * Niss
		 * @return the column 'niss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNiss()
		throws SQLException {
			return rs.getString("niss");
		}
		/**
		 * Actividad
		 * @return the column 'actividad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getActividad()
		throws SQLException {
			return rs.getString("actividad");
		}
		/**
		 * Convcol
		 * @return the column 'convcol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConvcol()
		throws SQLException {
			return rs.getString("convcol");
		}
		/**
		 * Hor 1
		 * @return the column 'hor1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor1()
		throws SQLException {
			return rs.getString("hor1");
		}
		/**
		 * Hor 2
		 * @return the column 'hor2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor2()
		throws SQLException {
			return rs.getString("hor2");
		}
		/**
		 * Hor 3
		 * @return the column 'hor3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor3()
		throws SQLException {
			return rs.getString("hor3");
		}
		/**
		 * Hor 4
		 * @return the column 'hor4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor4()
		throws SQLException {
			return rs.getString("hor4");
		}
		/**
		 * Hor 5
		 * @return the column 'hor5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor5()
		throws SQLException {
			return rs.getString("hor5");
		}
		/**
		 * Hor 6
		 * @return the column 'hor6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor6()
		throws SQLException {
			return rs.getString("hor6");
		}
		/**
		 * Hor 7
		 * @return the column 'hor7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor7()
		throws SQLException {
			return rs.getString("hor7");
		}
		/**
		 * Hor 8
		 * @return the column 'hor8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor8()
		throws SQLException {
			return rs.getString("hor8");
		}
		/**
		 * Hor 9
		 * @return the column 'hor9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHor9()
		throws SQLException {
			return rs.getString("hor9");
		}
		/**
		 * Fieloc 1
		 * @return the column 'fieloc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFieloc1()
		throws SQLException {
			return rs.getString("fieloc1");
		}
		/**
		 * Fieloc 2
		 * @return the column 'fieloc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFieloc2()
		throws SQLException {
			return rs.getString("fieloc2");
		}
		/**
		 * C
		 * @return the column 'c' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getC()
		throws SQLException {
			return rs.getString("c");
		}

	}
	
	
	/**
	 * Calendar
	 * 
	 */
	public class Calendar {
		
		private ResultSet rs;
		
		private Calendar (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Calendario
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha de Calendario
		 * @return the column 'feccal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccal()
		throws SQLException {
			return rs.getDate("feccal");
		}
		/**
		 * Tipo Dia
		 * @return the column 'tipdia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipdia()
		throws SQLException {
			return rs.getString("tipdia");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Codigo de Domicilio
		 * @return the column 'domicilio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDomicilio()
		throws SQLException {
			return rs.getInt("domicilio");
		}

	}
	
	
	/**
	 * Calfiniquito
	 * 
	 */
	public class Calfiniquito {
		
		private ResultSet rs;
		
		private Calfiniquito (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Código de Centro de Trabajo
		 * @return the column 'coddom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddom()
		throws SQLException {
			return rs.getInt("coddom");
		}
		/**
		 * Código de Trabajador
		 * @return the column 'codtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodtra()
		throws SQLException {
			return rs.getInt("codtra");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Causa de Baja
		 * @return the column 'causa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa()
		throws SQLException {
			return rs.getString("causa");
		}

	}
	
	
	/**
	 * Categoria
	 * 
	 */
	public class Categoria {
		
		private ResultSet rs;
		
		private Categoria (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Codigo de Nivel Retributivo
		 * @return the column 'nivel' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNivel()
		throws SQLException {
			return rs.getString("nivel");
		}
		/**
		 * Codigo de Categoria Laboral
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Categoria
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Grupo Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Epigrafe Accidentes Trabajo
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * Codigo Nacional O.
		 * @return the column 'cno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCno()
		throws SQLException {
			return rs.getString("cno");
		}

	}
	
	
	/**
	 * Cliente
	 * 
	 */
	public class Cliente {
		
		private ResultSet rs;
		
		private Cliente (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Cliente
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Delegacion
		 * @return the column 'coddlg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddlg()
		throws SQLException {
			return rs.getInt("coddlg");
		}
		/**
		 * Descripcion de Cliente
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Alias Breve
		 * @return the column 'alias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlias()
		throws SQLException {
			return rs.getString("alias");
		}
		/**
		 * Fecha Inicio Relacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Relacion
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Persona de Contacto
		 * @return the column 'persona' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPersona()
		throws SQLException {
			return rs.getString("persona");
		}
		/**
		 * Telefono
		 * @return the column 'telefono' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono()
		throws SQLException {
			return rs.getString("telefono");
		}
		/**
		 * 2º Telefono
		 * @return the column 'telefono2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono2()
		throws SQLException {
			return rs.getString("telefono2");
		}
		/**
		 * 3º Telefono
		 * @return the column 'telefono3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono3()
		throws SQLException {
			return rs.getString("telefono3");
		}
		/**
		 * Fax
		 * @return the column 'fax' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFax()
		throws SQLException {
			return rs.getString("fax");
		}
		/**
		 * Direccion E-Mail
		 * @return the column 'email' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmail()
		throws SQLException {
			return rs.getString("email");
		}
		/**
		 * Tipo Documento
		 * @return the column 'inddoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddoc()
		throws SQLException {
			return rs.getString("inddoc");
		}
		/**
		 * Pais Emisor
		 * @return the column 'paiemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaiemi()
		throws SQLException {
			return rs.getString("paiemi");
		}
		/**
		 * Numero Documento
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Tipo de Empresario
		 * @return the column 'tipemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipemp()
		throws SQLException {
			return rs.getString("tipemp");
		}
		/**
		 * Observaciones
		 * @return the column 'obscli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getObscli()
		throws SQLException {
			return rs.getString("obscli");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Indicador de Inactivo
		 * @return the column 'inactivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInactivo()
		throws SQLException {
			return rs.getString("inactivo");
		}
		/**
		 * Calendario Laboral
		 * @return the column 'indcal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcal()
		throws SQLException {
			return rs.getString("indcal");
		}
		/**
		 * Nomina de Empresa
		 * @return the column 'indnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndnom()
		throws SQLException {
			return rs.getString("indnom");
		}
		/**
		 * Estudio Costes
		 * @return the column 'indcoste' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcoste()
		throws SQLException {
			return rs.getString("indcoste");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Cliente solo asesoria
		 * @return the column 'soloases' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSoloases()
		throws SQLException {
			return rs.getString("soloases");
		}
		/**
		 * Envio de Seguros Sociales
		 * @return the column 'envioss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEnvioss()
		throws SQLException {
			return rs.getString("envioss");
		}

		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitDomicilio(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM domicilio WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Domicilio domicilio = new Domicilio(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitDomicilio(domicilio, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprdom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprdom WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprdom emprdom = new Emprdom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprdom(emprdom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprban WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprban emprban = new Emprban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprban(emprban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprlban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprlban WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprlban emprlban = new Emprlban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprlban(emprlban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAvisos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM avisos WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Avisos avisos = new Avisos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAvisos(avisos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitVariaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM variaciones WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Variaciones variaciones = new Variaciones(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitVariaciones(variaciones, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM regidocu WHERE" 
					+ " codcli = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Regidocu regidocu = new Regidocu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRegidocu(regidocu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Cnae
	 * 
	 */
	public class Cnae {
		
		private ResultSet rs;
		
		private Cnae (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Título de la actividad económica
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Ocupaciones
		 * @return the column 'ocupacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOcupacion()
		throws SQLException {
			return rs.getString("ocupacion");
		}

		public void visitLincnae(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lincnae WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lincnae lincnae = new Lincnae(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLincnae(lincnae, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Cnae2009
	 * 
	 */
	public class Cnae2009 {
		
		private ResultSet rs;
		
		private Cnae2009 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Título de la actividad económica
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Ocupaciones
		 * @return the column 'ocupacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOcupacion()
		throws SQLException {
			return rs.getString("ocupacion");
		}

		public void visitLincnae2009(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lincnae2009 WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lincnae2009 lincnae2009 = new Lincnae2009(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLincnae2009(lincnae2009, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Colectivos
	 * 
	 */
	public class Colectivos {
		
		private ResultSet rs;
		
		private Colectivos (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Clave
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Denominación larga
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Denominación corta
		 * @return the column 'descripcorta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcorta()
		throws SQLException {
			return rs.getString("descripcorta");
		}

		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " colectivo = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Complemento
	 * 
	 */
	public class Complemento {
		
		private ResultSet rs;
		
		private Complemento (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Complemento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Tipo de Cotizacion
		 * @return the column 'tipcot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcot()
		throws SQLException {
			return rs.getString("tipcot");
		}
		/**
		 * Descripcion de Complemento
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Descripcion Abreviada de Complemento
		 * @return the column 'desabr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesabr()
		throws SQLException {
			return rs.getString("desabr");
		}
		/**
		 * Tipo de Complemento
		 * @return the column 'tipcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcom()
		throws SQLException {
			return rs.getString("tipcom");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Indicador de Complemento
		 * @return the column 'indcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcom()
		throws SQLException {
			return rs.getString("indcom");
		}
		/**
		 * Dinerario o en Especie
		 * @return the column 'dinesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDinesp()
		throws SQLException {
			return rs.getString("dinesp");
		}

		public void visitPagaext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM pagaext WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Pagaext pagaext = new Pagaext(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPagaext(pagaext, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPercniv(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM percniv WHERE" 
					+ " codcom = ?  "  + "AND" 					+ " codcomapl = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Percniv percniv = new Percniv(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPercniv(percniv, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominaex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominaex WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominaex nominaex = new Nominaex(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominaex(nominaex, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPercep(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM percep WHERE" 
					+ " codcom = ?  "  + "AND" 					+ " comapl = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Percep percep = new Percep(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPercep(percep, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinipext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipext WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipext finipext = new Finipext(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipext(finipext, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinipextdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipextdf WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipextdf finipextdf = new Finipextdf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipextdf(finipextdf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinipextnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipextnu WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipextnu finipextnu = new Finipextnu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipextnu(finipextnu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitLinplus(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linplus WHERE" 
					+ " codcom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linplus linplus = new Linplus(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinplus(linplus, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Complevar
	 * 
	 */
	public class Complevar {
		
		private ResultSet rs;
		
		private Complevar (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Código Percepcion
		 * @return the column 'percep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPercep()
		throws SQLException {
			return rs.getInt("percep");
		}

	}
	
	
	/**
	 * Comunica
	 * 
	 */
	public class Comunica {
		
		private ResultSet rs;
		
		private Comunica (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha de Comunicacion
		 * @return the column 'feccom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccom()
		throws SQLException {
			return rs.getDate("feccom");
		}
		/**
		 * Fecha de Efecto
		 * @return the column 'fecefe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecefe()
		throws SQLException {
			return rs.getDate("fecefe");
		}
		/**
		 * Anio de Nacimiento
		 * @return the column 'anionac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnionac()
		throws SQLException {
			return rs.getInt("anionac");
		}
		/**
		 * Situacion Familiar
		 * @return the column 'sitfam' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSitfam()
		throws SQLException {
			return rs.getString("sitfam");
		}
		/**
		 * Nif del Conyuge
		 * @return the column 'nifcony' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNifcony()
		throws SQLException {
			return rs.getString("nifcony");
		}
		/**
		 * Otras Situaciones
		 * @return the column 'otras' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtras()
		throws SQLException {
			return rs.getString("otras");
		}
		/**
		 * Minusvalia Declarante
		 * @return the column 'xminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getXminus()
		throws SQLException {
			return rs.getString("xminus");
		}
		/**
		 * Percibe Pension
		 * @return the column 'pension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPension()
		throws SQLException {
			return rs.getString("pension");
		}
		/**
		 * Importe Pension Compensatoria
		 * @return the column 'imp_pension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pension()
		throws SQLException {
			return rs.getBigDecimal("imp_pension");
		}
		/**
		 * Importe Anualidades Alimentos
		 * @return the column 'imp_anual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_anual()
		throws SQLException {
			return rs.getBigDecimal("imp_anual");
		}
		/**
		 * Importe Reduccion Rentas Irregulares
		 * @return the column 'imp_irreg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_irreg()
		throws SQLException {
			return rs.getBigDecimal("imp_irreg");
		}
		/**
		 * Importe Cotizacines S.S., Mutualidades, etc
		 * @return the column 'imp_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ss()
		throws SQLException {
			return rs.getBigDecimal("imp_ss");
		}
		/**
		 * Porcentaje de I.R.P.F. solicitado
		 * @return the column 'solicita' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSolicita()
		throws SQLException {
			return rs.getBigDecimal("solicita");
		}
		/**
		 * Retribucion Estimada Anio Actual
		 * @return the column 'imp_retr_est' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_retr_est()
		throws SQLException {
			return rs.getBigDecimal("imp_retr_est");
		}
		/**
		 * Tipo de documento del conyuge
		 * @return the column 'tipodoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipodoc()
		throws SQLException {
			return rs.getString("tipodoc");
		}
		/**
		 * Movilidad geográfica
		 * @return the column 'movilidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getMovilidad()
		throws SQLException {
			return rs.getDate("movilidad");
		}
		/**
		 * Prolongación actividad laboral
		 * @return the column 'prolongacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProlongacion()
		throws SQLException {
			return rs.getString("prolongacion");
		}
		/**
		 * Pagos por adquisición/rehabilitación vivienda habitual financiac
		 * @return the column 'hipoteca' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHipoteca()
		throws SQLException {
			return rs.getString("hipoteca");
		}

		public void visitLincomun(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lincomun WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " feccom = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setDate(2, this.getFeccom() ); 
				rs = stmt.executeQuery();
				Lincomun lincomun = new Lincomun(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLincomun(lincomun, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Comunidad
	 * 
	 */
	public class Comunidad {
		
		private ResultSet rs;
		
		private Comunidad (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Comunidad
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Comunidad
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Codigo de Pais
		 * @return the column 'paicom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaicom()
		throws SQLException {
			return rs.getString("paicom");
		}

		public void visitProvincia(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM provincia WHERE" 
					+ " compro = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Provincia provincia = new Provincia(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitProvincia(provincia, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Config
	 * 
	 */
	public class Config {
		
		private ResultSet rs;
		
		private Config (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Ejercicio
		 * @return the column 'ejercicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getEjercicio()
		throws SQLException {
			return rs.getInt("ejercicio");
		}

	}
	
	
	/**
	 * Convenio
	 * 
	 */
	public class Convenio {
		
		private ResultSet rs;
		
		private Convenio (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Convenio
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Convenio
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Indicador Dias Descuento
		 * @return the column 'inddia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddia()
		throws SQLException {
			return rs.getString("inddia");
		}
		/**
		 * Tipo de Convenio
		 * @return the column 'tipcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcon()
		throws SQLException {
			return rs.getString("tipcon");
		}

		public void visitPagaext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM pagaext WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Pagaext pagaext = new Pagaext(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPagaext(pagaext, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNivel(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nivel WHERE" 
					+ " codcon = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nivel nivel = new Nivel(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNivel(nivel, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCategoria(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM categoria WHERE" 
					+ " codcon = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Categoria categoria = new Categoria(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCategoria(categoria, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPercniv(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM percniv WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Percniv percniv = new Percniv(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPercniv(percniv, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmpract(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM empract WHERE" 
					+ " convenio = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Empract empract = new Empract(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmpract(empract, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprctra(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprctra WHERE" 
					+ " codcon = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprctra emprctra = new Emprctra(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprctra(emprctra, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codcon = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Costes
	 * 
	 */
	public class Costes {
		
		private ResultSet rs;
		
		private Costes (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Simulación
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Número Secuencial
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Codigo Trabajador
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Anio de Nacimiento
		 * @return the column 'anionac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnionac()
		throws SQLException {
			return rs.getInt("anionac");
		}
		/**
		 * Situacion Familiar
		 * @return the column 'sitfam' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSitfam()
		throws SQLException {
			return rs.getString("sitfam");
		}
		/**
		 * Minusvalia Declarante
		 * @return the column 'xminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getXminus()
		throws SQLException {
			return rs.getString("xminus");
		}
		/**
		 * Importe Pension Compensatoria
		 * @return the column 'imp_pension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pension()
		throws SQLException {
			return rs.getBigDecimal("imp_pension");
		}
		/**
		 * Importe Anualidades Alimentos
		 * @return the column 'imp_anual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_anual()
		throws SQLException {
			return rs.getBigDecimal("imp_anual");
		}
		/**
		 * Importe Reduccion Rentas Irregulares
		 * @return the column 'imp_irreg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_irreg()
		throws SQLException {
			return rs.getBigDecimal("imp_irreg");
		}
		/**
		 * Importe Cotizacines S.S., Mutualidades, etc
		 * @return the column 'imp_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ss()
		throws SQLException {
			return rs.getBigDecimal("imp_ss");
		}
		/**
		 * Porcentaje de I.R.P.F. solicitado
		 * @return the column 'solicita' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSolicita()
		throws SQLException {
			return rs.getBigDecimal("solicita");
		}
		/**
		 * Porcentaje Anterior como mínimo
		 * @return the column 'minimo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMinimo()
		throws SQLException {
			return rs.getString("minimo");
		}
		/**
		 * Aprendiz
		 * @return the column 'aprendiz' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAprendiz()
		throws SQLException {
			return rs.getString("aprendiz");
		}
		/**
		 * Contrato Temporal
		 * @return the column 'temporal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTemporal()
		throws SQLException {
			return rs.getString("temporal");
		}
		/**
		 * Alto cargo
		 * @return the column 'alto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlto()
		throws SQLException {
			return rs.getString("alto");
		}
		/**
		 * Grupo de Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Epigrafe Accidentes de Trabajo
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'procot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcot()
		throws SQLException {
			return rs.getString("procot");
		}
		/**
		 * Prorrateo Retribucion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Numero de Autorizacion Pluriempleo
		 * @return the column 'plunumaut' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPlunumaut()
		throws SQLException {
			return rs.getString("plunumaut");
		}
		/**
		 * Pluriempleo: % sobre Tope Minimo Cotizacion
		 * @return the column 'pluprcmin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPluprcmin()
		throws SQLException {
			return rs.getBigDecimal("pluprcmin");
		}
		/**
		 * Pluriempleo: % sobre Tope Maximo Cotizacion
		 * @return the column 'pluprcmax' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPluprcmax()
		throws SQLException {
			return rs.getBigDecimal("pluprcmax");
		}
		/**
		 * Minutos Jornada Semanal Real
		 * @return the column 'semana' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSemana()
		throws SQLException {
			return rs.getInt("semana");
		}
		/**
		 * Minutos Jornada Semanal Tiempo Parcial
		 * @return the column 'semanatp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSemanatp()
		throws SQLException {
			return rs.getInt("semanatp");
		}
		/**
		 * Cantidad Minutos/Dias Cotizacion Tiempo Parcial
		 * @return the column 'cantp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCantp()
		throws SQLException {
			return rs.getInt("cantp");
		}
		/**
		 * Indicador Tiempo de Contrato
		 * @return the column 'indtp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndtp()
		throws SQLException {
			return rs.getString("indtp");
		}
		/**
		 * Asimilado a % Cotizacion
		 * @return the column 'codpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpct()
		throws SQLException {
			return rs.getString("codpct");
		}
		/**
		 * Bruto Actual
		 * @return the column 'imp_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_actual()
		throws SQLException {
			return rs.getBigDecimal("imp_actual");
		}
		/**
		 * Importe Extrasalariales Actual
		 * @return the column 'imp_ext_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ext_actual()
		throws SQLException {
			return rs.getBigDecimal("imp_ext_actual");
		}
		/**
		 * Seguridad Social Actual
		 * @return the column 'ss_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_actual()
		throws SQLException {
			return rs.getBigDecimal("ss_actual");
		}
		/**
		 * Retenido IRPF Actual
		 * @return the column 'irpf_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_actual()
		throws SQLException {
			return rs.getBigDecimal("irpf_actual");
		}
		/**
		 * Neto Actual
		 * @return the column 'neto_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNeto_actual()
		throws SQLException {
			return rs.getBigDecimal("neto_actual");
		}
		/**
		 * Seguridad Social Empresa Actual
		 * @return the column 'ss_emp_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_emp_actual()
		throws SQLException {
			return rs.getBigDecimal("ss_emp_actual");
		}
		/**
		 * Coste Total Actual
		 * @return the column 'coste_actual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoste_actual()
		throws SQLException {
			return rs.getBigDecimal("coste_actual");
		}
		/**
		 * Bruto Supuesto
		 * @return the column 'imp_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_supuesto()
		throws SQLException {
			return rs.getBigDecimal("imp_supuesto");
		}
		/**
		 * Importe Extrasalariales Supuesto
		 * @return the column 'imp_ext_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ext_supuesto()
		throws SQLException {
			return rs.getBigDecimal("imp_ext_supuesto");
		}
		/**
		 * Seguridad Social Supuesto
		 * @return the column 'ss_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_supuesto()
		throws SQLException {
			return rs.getBigDecimal("ss_supuesto");
		}
		/**
		 * Retenido IRPF Supuesto
		 * @return the column 'irpf_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_supuesto()
		throws SQLException {
			return rs.getBigDecimal("irpf_supuesto");
		}
		/**
		 * Neto Supuesto
		 * @return the column 'neto_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNeto_supuesto()
		throws SQLException {
			return rs.getBigDecimal("neto_supuesto");
		}
		/**
		 * Seguridad Social Empresa Supuesto
		 * @return the column 'ss_emp_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_emp_supuesto()
		throws SQLException {
			return rs.getBigDecimal("ss_emp_supuesto");
		}
		/**
		 * Coste Total Supuesto
		 * @return the column 'coste_supuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoste_supuesto()
		throws SQLException {
			return rs.getBigDecimal("coste_supuesto");
		}
		/**
		 * Bruto Diferencia
		 * @return the column 'imp_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_diferencia()
		throws SQLException {
			return rs.getBigDecimal("imp_diferencia");
		}
		/**
		 * Importe Extrasalariales Diferencia
		 * @return the column 'imp_ext_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ext_diferencia()
		throws SQLException {
			return rs.getBigDecimal("imp_ext_diferencia");
		}
		/**
		 * Seguridad Social Diferencia
		 * @return the column 'ss_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_diferencia()
		throws SQLException {
			return rs.getBigDecimal("ss_diferencia");
		}
		/**
		 * Retenido IRPF Diferencia
		 * @return the column 'irpf_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf_diferencia()
		throws SQLException {
			return rs.getBigDecimal("irpf_diferencia");
		}
		/**
		 * Neto Diferencia
		 * @return the column 'neto_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNeto_diferencia()
		throws SQLException {
			return rs.getBigDecimal("neto_diferencia");
		}
		/**
		 * Seguridad Social Empresa Diferencia
		 * @return the column 'ss_emp_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSs_emp_diferencia()
		throws SQLException {
			return rs.getBigDecimal("ss_emp_diferencia");
		}
		/**
		 * Coste Total Diferencia
		 * @return the column 'coste_diferencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoste_diferencia()
		throws SQLException {
			return rs.getBigDecimal("coste_diferencia");
		}

		public void visitLcomunica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lcomunica WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setInt(2, this.getNumero() ); 
				rs = stmt.executeQuery();
				Lcomunica lcomunica = new Lcomunica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLcomunica(lcomunica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitLbonifica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lbonifica WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setInt(2, this.getNumero() ); 
				rs = stmt.executeQuery();
				Lbonifica lbonifica = new Lbonifica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLbonifica(lbonifica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Cuota
	 * 
	 */
	public class Cuota {
		
		private ResultSet rs;
		
		private Cuota (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Hasta importe
		 * @return the column 'hasta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta()
		throws SQLException {
			return rs.getBigDecimal("hasta");
		}
		/**
		 * Importe
		 * @return the column 'pesetas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPesetas()
		throws SQLException {
			return rs.getBigDecimal("pesetas");
		}
		/**
		 * Resto base hasta importe
		 * @return the column 'resto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getResto()
		throws SQLException {
			return rs.getBigDecimal("resto");
		}
		/**
		 * Porcentaje a aplicar
		 * @return the column 'porcentaje' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPorcentaje()
		throws SQLException {
			return rs.getBigDecimal("porcentaje");
		}

	}
	
	
	/**
	 * Cuota_01
	 * 
	 */
	public class Cuota_01 {
		
		private ResultSet rs;
		
		private Cuota_01 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Sin Hijos
		 * @return the column 'h_0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_0()
		throws SQLException {
			return rs.getBigDecimal("h_0");
		}
		/**
		 * Con 1 Hijo
		 * @return the column 'h_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_1()
		throws SQLException {
			return rs.getBigDecimal("h_1");
		}
		/**
		 * Con 2 Hijos
		 * @return the column 'h_2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_2()
		throws SQLException {
			return rs.getBigDecimal("h_2");
		}
		/**
		 * Con 3 Hijos
		 * @return the column 'h_3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_3()
		throws SQLException {
			return rs.getBigDecimal("h_3");
		}
		/**
		 * Con 4 Hijos
		 * @return the column 'h_4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_4()
		throws SQLException {
			return rs.getBigDecimal("h_4");
		}
		/**
		 * Con 5 Hijos
		 * @return the column 'h_5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_5()
		throws SQLException {
			return rs.getBigDecimal("h_5");
		}
		/**
		 * Con 6 Hijos o mas
		 * @return the column 'h_6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_6()
		throws SQLException {
			return rs.getBigDecimal("h_6");
		}

	}
	
	
	/**
	 * Cuota_20
	 * 
	 */
	public class Cuota_20 {
		
		private ResultSet rs;
		
		private Cuota_20 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Sin Hijos
		 * @return the column 'h_0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_0()
		throws SQLException {
			return rs.getBigDecimal("h_0");
		}
		/**
		 * Con 1 Hijo
		 * @return the column 'h_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_1()
		throws SQLException {
			return rs.getBigDecimal("h_1");
		}
		/**
		 * Con 2 Hijos
		 * @return the column 'h_2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_2()
		throws SQLException {
			return rs.getBigDecimal("h_2");
		}
		/**
		 * Con 3 Hijos
		 * @return the column 'h_3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_3()
		throws SQLException {
			return rs.getBigDecimal("h_3");
		}
		/**
		 * Con 4 Hijos
		 * @return the column 'h_4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_4()
		throws SQLException {
			return rs.getBigDecimal("h_4");
		}
		/**
		 * Con 5 Hijos
		 * @return the column 'h_5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_5()
		throws SQLException {
			return rs.getBigDecimal("h_5");
		}
		/**
		 * Con 6 Hijos o mas
		 * @return the column 'h_6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_6()
		throws SQLException {
			return rs.getBigDecimal("h_6");
		}

	}
	
	
	/**
	 * Cuota_31
	 * 
	 */
	public class Cuota_31 {
		
		private ResultSet rs;
		
		private Cuota_31 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Sin Hijos
		 * @return the column 'h_0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_0()
		throws SQLException {
			return rs.getBigDecimal("h_0");
		}
		/**
		 * Con 1 Hijo
		 * @return the column 'h_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_1()
		throws SQLException {
			return rs.getBigDecimal("h_1");
		}
		/**
		 * Con 2 Hijos
		 * @return the column 'h_2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_2()
		throws SQLException {
			return rs.getBigDecimal("h_2");
		}
		/**
		 * Con 3 Hijos
		 * @return the column 'h_3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_3()
		throws SQLException {
			return rs.getBigDecimal("h_3");
		}
		/**
		 * Con 4 Hijos
		 * @return the column 'h_4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_4()
		throws SQLException {
			return rs.getBigDecimal("h_4");
		}
		/**
		 * Con 5 Hijos
		 * @return the column 'h_5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_5()
		throws SQLException {
			return rs.getBigDecimal("h_5");
		}
		/**
		 * Con 6 Hijos
		 * @return the column 'h_6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_6()
		throws SQLException {
			return rs.getBigDecimal("h_6");
		}
		/**
		 * Con 7 Hijos
		 * @return the column 'h_7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_7()
		throws SQLException {
			return rs.getBigDecimal("h_7");
		}
		/**
		 * Con 8 Hijos
		 * @return the column 'h_8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_8()
		throws SQLException {
			return rs.getBigDecimal("h_8");
		}
		/**
		 * Con 9 Hijos
		 * @return the column 'h_9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_9()
		throws SQLException {
			return rs.getBigDecimal("h_9");
		}
		/**
		 * Con 10 Hijos o mas
		 * @return the column 'h_10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_10()
		throws SQLException {
			return rs.getBigDecimal("h_10");
		}

	}
	
	
	/**
	 * Cuota_48
	 * 
	 */
	public class Cuota_48 {
		
		private ResultSet rs;
		
		private Cuota_48 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Sin Hijos
		 * @return the column 'h_0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_0()
		throws SQLException {
			return rs.getBigDecimal("h_0");
		}
		/**
		 * Con 1 Hijo
		 * @return the column 'h_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_1()
		throws SQLException {
			return rs.getBigDecimal("h_1");
		}
		/**
		 * Con 2 Hijos
		 * @return the column 'h_2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_2()
		throws SQLException {
			return rs.getBigDecimal("h_2");
		}
		/**
		 * Con 3 Hijos
		 * @return the column 'h_3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_3()
		throws SQLException {
			return rs.getBigDecimal("h_3");
		}
		/**
		 * Con 4 Hijos
		 * @return the column 'h_4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_4()
		throws SQLException {
			return rs.getBigDecimal("h_4");
		}
		/**
		 * Con 5 Hijos
		 * @return the column 'h_5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_5()
		throws SQLException {
			return rs.getBigDecimal("h_5");
		}
		/**
		 * Con 6 Hijos o mas
		 * @return the column 'h_6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getH_6()
		throws SQLException {
			return rs.getBigDecimal("h_6");
		}

	}
	
	
	/**
	 * Datosafi
	 * 
	 */
	public class Datosafi {
		
		private ResultSet rs;
		
		private Datosafi (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo Secuencial
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo Trabajador
		 * @return the column 'codtrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodtrab()
		throws SQLException {
			return rs.getInt("codtrab");
		}
		/**
		 * Password Actual
		 * @return the column 'passact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPassact()
		throws SQLException {
			return rs.getString("passact");
		}
		/**
		 * Provincia C.C.C.
		 * @return the column 'provrrss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvrrss()
		throws SQLException {
			return rs.getString("provrrss");
		}
		/**
		 * Numero C.C.C.
		 * @return the column 'numrrss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumrrss()
		throws SQLException {
			return rs.getString("numrrss");
		}
		/**
		 * Tipo identificacion empresa
		 * @return the column 'tipemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipemp()
		throws SQLException {
			return rs.getString("tipemp");
		}
		/**
		 * Pais empresa
		 * @return the column 'paisemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaisemp()
		throws SQLException {
			return rs.getString("paisemp");
		}
		/**
		 * Provincia CCC
		 * @return the column 'provccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvccc()
		throws SQLException {
			return rs.getString("provccc");
		}
		/**
		 * Numero CCC
		 * @return the column 'numccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumccc()
		throws SQLException {
			return rs.getString("numccc");
		}
		/**
		 * Numero identificacion empresa
		 * @return the column 'numidemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumidemp()
		throws SQLException {
			return rs.getString("numidemp");
		}
		/**
		 * Indicador razon social
		 * @return the column 'indrs' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndrs()
		throws SQLException {
			return rs.getString("indrs");
		}
		/**
		 * Codigo Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Razon social
		 * @return the column 'razon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRazon()
		throws SQLException {
			return rs.getString("razon");
		}
		/**
		 * Provincia N.A.F.
		 * @return the column 'provnaf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvnaf()
		throws SQLException {
			return rs.getString("provnaf");
		}
		/**
		 * Numero N.A.F.
		 * @return the column 'numnaf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumnaf()
		throws SQLException {
			return rs.getString("numnaf");
		}
		/**
		 * Tipo identificacion IPF
		 * @return the column 'tipidipf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipidipf()
		throws SQLException {
			return rs.getString("tipidipf");
		}
		/**
		 * Pais IPF
		 * @return the column 'paisipf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaisipf()
		throws SQLException {
			return rs.getString("paisipf");
		}
		/**
		 * Nacionalidad
		 * @return the column 'nacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNacion()
		throws SQLException {
			return rs.getString("nacion");
		}
		/**
		 * Primer apellido
		 * @return the column 'apel1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApel1()
		throws SQLException {
			return rs.getString("apel1");
		}
		/**
		 * Segundo apellido
		 * @return the column 'apel2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApel2()
		throws SQLException {
			return rs.getString("apel2");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Tipo Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque
		 * @return the column 'bloque' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getBloque()
		throws SQLException {
			return rs.getString("bloque");
		}
		/**
		 * Escalera
		 * @return the column 'escalera' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEscalera()
		throws SQLException {
			return rs.getString("escalera");
		}
		/**
		 * Piso
		 * @return the column 'piso' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPiso()
		throws SQLException {
			return rs.getString("piso");
		}
		/**
		 * Puerta
		 * @return the column 'puerta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPuerta()
		throws SQLException {
			return rs.getString("puerta");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Municipio-Entidad
		 * @return the column 'munic' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMunic()
		throws SQLException {
			return rs.getString("munic");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Accion
		 * @return the column 'accion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAccion()
		throws SQLException {
			return rs.getString("accion");
		}
		/**
		 * Situacion
		 * @return the column 'situacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSituacion()
		throws SQLException {
			return rs.getString("situacion");
		}
		/**
		 * Fecha Real
		 * @return the column 'fecreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecreal()
		throws SQLException {
			return rs.getDate("fecreal");
		}
		/**
		 * Grupo cotizacion
		 * @return the column 'grucot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getGrucot()
		throws SQLException {
			return rs.getString("grucot");
		}
		/**
		 * Epigrafe de A.T.
		 * @return the column 'epigat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEpigat()
		throws SQLException {
			return rs.getString("epigat");
		}
		/**
		 * Contrato de trabajo
		 * @return the column 'contrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContrab()
		throws SQLException {
			return rs.getString("contrab");
		}
		/**
		 * Coeficiente tiempo parcial
		 * @return the column 'coeftp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCoeftp()
		throws SQLException {
			return rs.getString("coeftp");
		}
		/**
		 * Indicador impresion
		 * @return the column 'indimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndimp()
		throws SQLException {
			return rs.getString("indimp");
		}
		/**
		 * Dias trabajados
		 * @return the column 'diastrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDiastrab()
		throws SQLException {
			return rs.getString("diastrab");
		}
		/**
		 * Coeficiente de permanencia
		 * @return the column 'coefper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoefper()
		throws SQLException {
			return rs.getBigDecimal("coefper");
		}
		/**
		 * Fecha Nacimiento
		 * @return the column 'fecnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnac()
		throws SQLException {
			return rs.getDate("fecnac");
		}
		/**
		 * Sexo
		 * @return the column 'sexo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSexo()
		throws SQLException {
			return rs.getString("sexo");
		}
		/**
		 * Categoria profesional
		 * @return the column 'catprof' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCatprof()
		throws SQLException {
			return rs.getString("catprof");
		}
		/**
		 * Numero identificacion trabajador
		 * @return the column 'alfaclave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlfaclave()
		throws SQLException {
			return rs.getString("alfaclave");
		}
		/**
		 * Entidad menor
		 * @return the column 'entidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEntidad()
		throws SQLException {
			return rs.getString("entidad");
		}
		/**
		 * Colectivo del trabajador
		 * @return the column 'coltrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getColtrab()
		throws SQLException {
			return rs.getString("coltrab");
		}
		/**
		 * Tipo empresario
		 * @return the column 'calemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCalemp()
		throws SQLException {
			return rs.getString("calemp");
		}
		/**
		 * Regimen S.S.
		 * @return the column 'regss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRegss()
		throws SQLException {
			return rs.getString("regss");
		}
		/**
		 * Relacion Laboral de Caracter Especial
		 * @return the column 'rlabcaresp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRlabcaresp()
		throws SQLException {
			return rs.getString("rlabcaresp");
		}
		/**
		 * Provincia de S.S. del Trabajador Sustituido
		 * @return the column 'provsstrasust' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvsstrasust()
		throws SQLException {
			return rs.getString("provsstrasust");
		}
		/**
		 * Numero de S.S. del Trabajador Sustituido
		 * @return the column 'numsstrasust' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumsstrasust()
		throws SQLException {
			return rs.getString("numsstrasust");
		}
		/**
		 * Causa de Sustitucion
		 * @return the column 'causasust' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausasust()
		throws SQLException {
			return rs.getString("causasust");
		}
		/**
		 * Autorización para trabajar
		 * @return the column 'autorizacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAutorizacion()
		throws SQLException {
			return rs.getString("autorizacion");
		}
		/**
		 * Fecha autorización
		 * @return the column 'fecaut' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecaut()
		throws SQLException {
			return rs.getDate("fecaut");
		}
		/**
		 * Fecha de Inicio de Contrato
		 * @return the column 'fecinicont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicont()
		throws SQLException {
			return rs.getDate("fecinicont");
		}
		/**
		 * Indicador de subrogacion parcial
		 * @return the column 'indsubrog' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndsubrog()
		throws SQLException {
			return rs.getString("indsubrog");
		}
		/**
		 * Empresa Origen del Contrato
		 * @return the column 'emporigen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmporigen()
		throws SQLException {
			return rs.getString("emporigen");
		}
		/**
		 * Trabajadores con exclusion de cotizacion
		 * @return the column 'excoti' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getExcoti()
		throws SQLException {
			return rs.getString("excoti");
		}
		/**
		 * Provincia del CCC del empresario usuario
		 * @return the column 'provempusu' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvempusu()
		throws SQLException {
			return rs.getString("provempusu");
		}
		/**
		 * Numero de CCC del empresario usuario
		 * @return the column 'numempusu' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumempusu()
		throws SQLException {
			return rs.getString("numempusu");
		}
		/**
		 * Contrato Interno
		 * @return the column 'contrint' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContrint()
		throws SQLException {
			return rs.getString("contrint");
		}
		/**
		 * Pariente 1º o 2º Grado
		 * @return the column 'pariente' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPariente()
		throws SQLException {
			return rs.getString("pariente");
		}
		/**
		 * Denominación de la Explotación
		 * @return the column 'embarcacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmbarcacion()
		throws SQLException {
			return rs.getString("embarcacion");
		}
		/**
		 * Ocupación
		 * @return the column 'ocupacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOcupacion()
		throws SQLException {
			return rs.getString("ocupacion");
		}

	}
	
	
	/**
	 * Delegacion
	 * 
	 */
	public class Delegacion {
		
		private ResultSet rs;
		
		private Delegacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Delegacion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Nombre de Delegacion
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Telefono de Delegacion
		 * @return the column 'telefono' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono()
		throws SQLException {
			return rs.getString("telefono");
		}

		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " coddlg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Detalle
	 * 
	 */
	public class Detalle {
		
		private ResultSet rs;
		
		private Detalle (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Tabla del campo
		 * @return the column 'tabla' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTabla()
		throws SQLException {
			return rs.getString("tabla");
		}
		/**
		 * Campo de la base de datos
		 * @return the column 'campo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCampo()
		throws SQLException {
			return rs.getString("campo");
		}
		/**
		 * Descripcion del campo
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

	}
	
	
	/**
	 * Divisa
	 * 
	 */
	public class Divisa {
		
		private ResultSet rs;
		
		private Divisa (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Divisa
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Divisa
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo de Redondeo
		 * @return the column 'redondeo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRedondeo()
		throws SQLException {
			return rs.getInt("redondeo");
		}
		/**
		 * Tipo de Mascara para impresion de valores 0
		 * @return the column 'mask1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMask1()
		throws SQLException {
			return rs.getInt("mask1");
		}
		/**
		 * Tipo de Mascara para impresion de valores sin 0
		 * @return the column 'mask2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMask2()
		throws SQLException {
			return rs.getInt("mask2");
		}

		public void visitLin_divisa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lin_divisa WHERE" 
					+ " divisa_final = ?  "  + "AND" 					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lin_divisa lin_divisa = new Lin_divisa(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLin_divisa(lin_divisa, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominaexdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominaexdf WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominaexdf nominaexdf = new Nominaexdf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominaexdf(nominaexdf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominaex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominaex WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominaex nominaex = new Nominaex(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominaex(nominaex, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNomina(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomina WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nomina nomina = new Nomina(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomina(nomina, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquito(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquito WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquito finiquito = new Finiquito(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquito(finiquito, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquitodf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquitodf WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquitodf finiquitodf = new Finiquitodf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquitodf(finiquitodf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquitonu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquitonu WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquitonu finiquitonu = new Finiquitonu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquitonu(finiquitonu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr11x(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr11x WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr11x impr11x = new Impr11x(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr11x(impr11x, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr190 WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr190 impr190 = new Impr190(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr190(impr190, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominadf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominadf WHERE" 
					+ " divisa = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominadf nominadf = new Nominadf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominadf(nominadf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Domicilio
	 * 
	 */
	public class Domicilio {
		
		private ResultSet rs;
		
		private Domicilio (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Codigo de Domicilio
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Persona de Contacto
		 * @return the column 'persona' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPersona()
		throws SQLException {
			return rs.getString("persona");
		}
		/**
		 * Telefono
		 * @return the column 'telefono' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono()
		throws SQLException {
			return rs.getString("telefono");
		}
		/**
		 * 2º Telefono
		 * @return the column 'telefono2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono2()
		throws SQLException {
			return rs.getString("telefono2");
		}
		/**
		 * 3º Telefono
		 * @return the column 'telefono3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono3()
		throws SQLException {
			return rs.getString("telefono3");
		}
		/**
		 * Fax
		 * @return the column 'fax' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFax()
		throws SQLException {
			return rs.getString("fax");
		}
		/**
		 * Direccion E-Mail
		 * @return the column 'email' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmail()
		throws SQLException {
			return rs.getString("email");
		}
		/**
		 * Primera Linea de Datos
		 * @return the column 'linea1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLinea1()
		throws SQLException {
			return rs.getString("linea1");
		}
		/**
		 * Segunda Linea de Datos
		 * @return the column 'linea2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLinea2()
		throws SQLException {
			return rs.getString("linea2");
		}
		/**
		 * Aclaracion a Domicliio
		 * @return the column 'aclaracion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAclaracion()
		throws SQLException {
			return rs.getString("aclaracion");
		}
		/**
		 * Codigo BBX
		 * @return the column 'codnsz' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodnsz()
		throws SQLException {
			return rs.getString("codnsz");
		}

		public void visitEmprctra(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprctra WHERE" 
					+ " domicilio = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprctra emprctra = new Emprctra(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprctra(emprctra, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprdom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprdom WHERE" 
					+ " coddom = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprdom emprdom = new Emprdom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprdom(emprdom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " domicilio = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitVariaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM variaciones WHERE" 
					+ " domicilio = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Variaciones variaciones = new Variaciones(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitVariaciones(variaciones, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPrestaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM prestaciones WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Prestaciones prestaciones = new Prestaciones(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPrestaciones(prestaciones, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " domicilio = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Elemcoti
	 * 
	 */
	public class Elemcoti {
		
		private ResultSet rs;
		
		private Elemcoti (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Elemento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Elemento
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitLinelem(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linelem WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linelem linelem = new Linelem(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinelem(linelem, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Elemirpf
	 * 
	 */
	public class Elemirpf {
		
		private ResultSet rs;
		
		private Elemirpf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Elemento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Elemento
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Porcentaje a aplicar
		 * @return the column 'porcentaje' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPorcentaje()
		throws SQLException {
			return rs.getString("porcentaje");
		}

		public void visitLinirpf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linirpf WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linirpf linirpf = new Linirpf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinirpf(linirpf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Embargo
	 * 
	 */
	public class Embargo {
		
		private ResultSet rs;
		
		private Embargo (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Comienzo Embargo
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Código de Persona
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Concepto de Embargo
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Nominas a las que afecta
		 * @return the column 'afecta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAfecta()
		throws SQLException {
			return rs.getString("afecta");
		}
		/**
		 * Importe Embargo
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Empract
	 * 
	 */
	public class Empract {
		
		private ResultSet rs;
		
		private Empract (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Actividad
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo BBX
		 * @return the column 'codnsz' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodnsz()
		throws SQLException {
			return rs.getString("codnsz");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Convenio
		 * @return the column 'convenio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConvenio()
		throws SQLException {
			return rs.getString("convenio");
		}
		/**
		 * Descripcion de Actividad
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Alias Breve
		 * @return the column 'alias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlias()
		throws SQLException {
			return rs.getString("alias");
		}
		/**
		 * Fecha Inicial Relacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Final Relacion
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Actividad Economica
		 * @return the column 'acteco' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getActeco()
		throws SQLException {
			return rs.getString("acteco");
		}
		/**
		 * Epigrafe IAE
		 * @return the column 'epiiae' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEpiiae()
		throws SQLException {
			return rs.getString("epiiae");
		}
		/**
		 * Codigo Nacional Actividad Economica - 1993 Rev.1
		 * @return the column 'cnae' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCnae()
		throws SQLException {
			return rs.getString("cnae");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Codigo Nacional Actividad Economica - 2009
		 * @return the column 'cnae2009' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCnae2009()
		throws SQLException {
			return rs.getString("cnae2009");
		}
		/**
		 * Sistema Red
		 * @return the column 'indred' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndred()
		throws SQLException {
			return rs.getString("indred");
		}
		/**
		 * Prestacion IT a cargo de Mutua
		 * @return the column 'indmutua' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndmutua()
		throws SQLException {
			return rs.getString("indmutua");
		}
		/**
		 * Impresion TC1 Separado
		 * @return the column 'indtc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndtc1()
		throws SQLException {
			return rs.getString("indtc1");
		}
		/**
		 * Calendario Laboral
		 * @return the column 'indcal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcal()
		throws SQLException {
			return rs.getString("indcal");
		}
		/**
		 * Nomina de Empresa
		 * @return the column 'indnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndnom()
		throws SQLException {
			return rs.getString("indnom");
		}
		/**
		 * Estudio Costes
		 * @return the column 'indcoste' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcoste()
		throws SQLException {
			return rs.getString("indcoste");
		}
		/**
		 * Envio de Seguros Sociales
		 * @return the column 'envioss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEnvioss()
		throws SQLException {
			return rs.getString("envioss");
		}
		/**
		 * Cotiza a la Fundacion Laboral de la Construccion
		 * @return the column 'flc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFlc()
		throws SQLException {
			return rs.getString("flc");
		}
		/**
		 * Colabora con la Seguridad Social
		 * @return the column 'colss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getColss()
		throws SQLException {
			return rs.getString("colss");
		}
		/**
		 * Ingreso R.Especie A/C Empresa
		 * @return the column 'ingespemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIngespemp()
		throws SQLException {
			return rs.getString("ingespemp");
		}
		/**
		 * Modalidad de Pago
		 * @return the column 'modpago' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getModpago()
		throws SQLException {
			return rs.getString("modpago");
		}
		/**
		 * Tipo Nómina Gráfica
		 * @return the column 'tiponomina' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTiponomina()
		throws SQLException {
			return rs.getString("tiponomina");
		}
		/**
		 * Indicador de Logo
		 * @return the column 'indlogo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndlogo()
		throws SQLException {
			return rs.getString("indlogo");
		}
		/**
		 * Indicador de Firma
		 * @return the column 'indfirma' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndfirma()
		throws SQLException {
			return rs.getString("indfirma");
		}
		/**
		 * Régimen
		 * @return the column 'indregimen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndregimen()
		throws SQLException {
			return rs.getString("indregimen");
		}
		/**
		 * Servicio de prevención
		 * @return the column 'prevencion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPrevencion()
		throws SQLException {
			return rs.getString("prevencion");
		}

		public void visitEmprctra(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprctra WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprctra emprctra = new Emprctra(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprctra(emprctra, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprccc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprccc WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprccc emprccc = new Emprccc(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprccc(emprccc, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprccos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprccos WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprccos emprccos = new Emprccos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprccos(emprccos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprdom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprdom WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprdom emprdom = new Emprdom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprdom(emprdom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprlban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprlban WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprlban emprlban = new Emprlban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprlban(emprlban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAvisos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM avisos WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Avisos avisos = new Avisos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAvisos(avisos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitVariaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM variaciones WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Variaciones variaciones = new Variaciones(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitVariaciones(variaciones, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM regidocu WHERE" 
					+ " codact = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Regidocu regidocu = new Regidocu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRegidocu(regidocu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " actividad = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Emprban
	 * 
	 */
	public class Emprban {
		
		private ResultSet rs;
		
		private Emprban (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Registro
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Entidad
		 * @return the column 'codent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodent()
		throws SQLException {
			return rs.getString("codent");
		}
		/**
		 * Codigo de Sucursal
		 * @return the column 'codsuc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodsuc()
		throws SQLException {
			return rs.getString("codsuc");
		}
		/**
		 * Digito de Control Cuenta Bancaria
		 * @return the column 'dc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc()
		throws SQLException {
			return rs.getString("dc");
		}
		/**
		 * Numero Cuenta Bancaria
		 * @return the column 'numcta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumcta()
		throws SQLException {
			return rs.getString("numcta");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}

		public void visitEmprlban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprlban WHERE" 
					+ " codcta = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprlban emprlban = new Emprlban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprlban(emprlban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Emprccc
	 * 
	 */
	public class Emprccc {
		
		private ResultSet rs;
		
		private Emprccc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Actividad
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Tipo Cuenta Cotizacion
		 * @return the column 'tipccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipccc()
		throws SQLException {
			return rs.getString("tipccc");
		}
		/**
		 * Numero Cuenta Cotizacion
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Mutua Patronal
		 * @return the column 'mutuaccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMutuaccc()
		throws SQLException {
			return rs.getString("mutuaccc");
		}
		/**
		 * Indica si cotiza o no a S.S.
		 * @return the column 'indss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndss()
		throws SQLException {
			return rs.getString("indss");
		}
		/**
		 * Codigo Convenio Colectivo TC2
		 * @return the column 'concol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcol()
		throws SQLException {
			return rs.getString("concol");
		}
		/**
		 * Seguro convenio
		 * @return the column 'seguro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSeguro()
		throws SQLException {
			return rs.getString("seguro");
		}

		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " codact = ?  "  + "AND" 					+ " codccc = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setString(2, this.getTipccc() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Emprccos
	 * 
	 */
	public class Emprccos {
		
		private ResultSet rs;
		
		private Emprccos (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo Centro de Coste
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Descripcion del Centro de Coste
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " codcco = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Emprctra
	 * 
	 */
	public class Emprctra {
		
		private ResultSet rs;
		
		private Emprctra (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Domicilio
		 * @return the column 'domicilio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDomicilio()
		throws SQLException {
			return rs.getInt("domicilio");
		}
		/**
		 * Superficie
		 * @return the column 'superficie' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSuperficie()
		throws SQLException {
			return rs.getInt("superficie");
		}
		/**
		 * Potencia electrica
		 * @return the column 'pelectri' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPelectri()
		throws SQLException {
			return rs.getBigDecimal("pelectri");
		}
		/**
		 * Horario
		 * @return the column 'horario' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHorario()
		throws SQLException {
			return rs.getString("horario");
		}
		/**
		 * Maquinaria instalada
		 * @return the column 'maquina' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMaquina()
		throws SQLException {
			return rs.getString("maquina");
		}
		/**
		 * Toxicos
		 * @return the column 'toxicos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getToxicos()
		throws SQLException {
			return rs.getString("toxicos");
		}
		/**
		 * Indicador Dias Descuento
		 * @return the column 'inddia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddia()
		throws SQLException {
			return rs.getString("inddia");
		}
		/**
		 * Codigo de Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Representante Legal Trabajadores
		 * @return the column 'represen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRepresen()
		throws SQLException {
			return rs.getString("represen");
		}
		/**
		 * Minutos Jornada Semanal
		 * @return the column 'jornada' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getJornada()
		throws SQLException {
			return rs.getInt("jornada");
		}
		/**
		 * Calendario Laboral
		 * @return the column 'indcal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcal()
		throws SQLException {
			return rs.getString("indcal");
		}
		/**
		 * Nomina de Empresa
		 * @return the column 'indnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndnom()
		throws SQLException {
			return rs.getString("indnom");
		}
		/**
		 * Estudio Costes
		 * @return the column 'indcoste' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcoste()
		throws SQLException {
			return rs.getString("indcoste");
		}
		/**
		 * Fiestas Locales
		 * @return the column 'fiestas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFiestas()
		throws SQLException {
			return rs.getString("fiestas");
		}
		/**
		 * Envio de Seguros Sociales
		 * @return the column 'envioss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEnvioss()
		throws SQLException {
			return rs.getString("envioss");
		}

	}
	
	
	/**
	 * Emprdom
	 * 
	 */
	public class Emprdom {
		
		private ResultSet rs;
		
		private Emprdom (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Registro
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Tipo de Domicilio
		 * @return the column 'tipdom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipdom()
		throws SQLException {
			return rs.getString("tipdom");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Codigo de Domicilio
		 * @return the column 'coddom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddom()
		throws SQLException {
			return rs.getInt("coddom");
		}

	}
	
	
	/**
	 * Empresa
	 * 
	 */
	public class Empresa {
		
		private ResultSet rs;
		
		private Empresa (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Nombre de la empresa
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * $column.remarks
		 * @return the column 'pathlog' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPathlog()
		throws SQLException {
			return rs.getString("pathlog");
		}

	}
	
	
	/**
	 * Emprlban
	 * 
	 */
	public class Emprlban {
		
		private ResultSet rs;
		
		private Emprlban (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Registro
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Cuenta
		 * @return the column 'codcta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcta()
		throws SQLException {
			return rs.getInt("codcta");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Tipo de Cuenta
		 * @return the column 'tipcta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcta()
		throws SQLException {
			return rs.getString("tipcta");
		}

	}
	
	
	/**
	 * Emprnif
	 * 
	 */
	public class Emprnif {
		
		private ResultSet rs;
		
		private Emprnif (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Empresa
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Fecha Inicio Relacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Relacion
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Descripcion de Empresa
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Alias Breve Empresa
		 * @return the column 'alias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlias()
		throws SQLException {
			return rs.getString("alias");
		}
		/**
		 * Tipo Documento
		 * @return the column 'inddoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddoc()
		throws SQLException {
			return rs.getString("inddoc");
		}
		/**
		 * Pais Emisor
		 * @return the column 'paiemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaiemi()
		throws SQLException {
			return rs.getString("paiemi");
		}
		/**
		 * Numero Documento
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Representante
		 * @return the column 'representante' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRepresentante()
		throws SQLException {
			return rs.getString("representante");
		}
		/**
		 * Cargo Representante
		 * @return the column 'cargo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCargo()
		throws SQLException {
			return rs.getString("cargo");
		}
		/**
		 * Fecha Nacimiento Representante
		 * @return the column 'fecnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnac()
		throws SQLException {
			return rs.getDate("fecnac");
		}
		/**
		 * Numero Documento Representante
		 * @return the column 'nrodocrep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNrodocrep()
		throws SQLException {
			return rs.getString("nrodocrep");
		}
		/**
		 * Administracion Hacienda
		 * @return the column 'codadm' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodadm()
		throws SQLException {
			return rs.getString("codadm");
		}
		/**
		 * Tipo de Empresario
		 * @return the column 'tipempr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipempr()
		throws SQLException {
			return rs.getString("tipempr");
		}
		/**
		 * Sexo
		 * @return the column 'sexo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSexo()
		throws SQLException {
			return rs.getString("sexo");
		}
		/**
		 * Fecha Constitucion / Nacimiento
		 * @return the column 'feccon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccon()
		throws SQLException {
			return rs.getDate("feccon");
		}
		/**
		 * Observaciones
		 * @return the column 'obsnif' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getObsnif()
		throws SQLException {
			return rs.getString("obsnif");
		}
		/**
		 * Datos de Inscripcion en el Registro
		 * @return the column 'datreg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDatreg()
		throws SQLException {
			return rs.getString("datreg");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Tipo Documento Representante
		 * @return the column 'tipdocrep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipdocrep()
		throws SQLException {
			return rs.getString("tipdocrep");
		}
		/**
		 * Pais Emisor Documento Representante
		 * @return the column 'paidocrep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaidocrep()
		throws SQLException {
			return rs.getString("paidocrep");
		}
		/**
		 * Indicador IRPF
		 * @return the column 'indirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndirpf()
		throws SQLException {
			return rs.getString("indirpf");
		}
		/**
		 * Calendario Laboral
		 * @return the column 'indcal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcal()
		throws SQLException {
			return rs.getString("indcal");
		}
		/**
		 * Nomina de Empresa
		 * @return the column 'indnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndnom()
		throws SQLException {
			return rs.getString("indnom");
		}
		/**
		 * Estudio Costes
		 * @return the column 'indcoste' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcoste()
		throws SQLException {
			return rs.getString("indcoste");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Envio de Seguros Sociales
		 * @return the column 'envioss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEnvioss()
		throws SQLException {
			return rs.getString("envioss");
		}
		/**
		 * Concierto Economico
		 * @return the column 'cecon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCecon()
		throws SQLException {
			return rs.getString("cecon");
		}
		/**
		 * Modalidad declaraciones de impuestos
		 * @return the column 'modimpuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getModimpuesto()
		throws SQLException {
			return rs.getString("modimpuesto");
		}

		public void visitEmpract(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM empract WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Empract empract = new Empract(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmpract(empract, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprctra(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprctra WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprctra emprctra = new Emprctra(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprctra(emprctra, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprdom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprdom WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprdom emprdom = new Emprdom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprdom(emprdom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprlban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprlban WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprlban emprlban = new Emprlban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprlban(emprlban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitOtrperc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM otrperc WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Otrperc otrperc = new Otrperc(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitOtrperc(otrperc, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAvisos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM avisos WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Avisos avisos = new Avisos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAvisos(avisos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr11x(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr11x WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr11x impr11x = new Impr11x(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr11x(impr11x, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr190 WHERE" 
					+ " codemp = ?  "  + "AND" 					+ " codemp = ?  "  + "AND" 					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setInt(2, this.getCdg() ); 
				stmt.setInt(3, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr190 impr190 = new Impr190(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr190(impr190, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitVariaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM variaciones WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Variaciones variaciones = new Variaciones(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitVariaciones(variaciones, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM regidocu WHERE" 
					+ " codemp = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Regidocu regidocu = new Regidocu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRegidocu(regidocu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRem_cert_empr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM rem_cert_empr WHERE" 
					+ " empresa = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Rem_cert_empr rem_cert_empr = new Rem_cert_empr(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRem_cert_empr(rem_cert_empr, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Emprper
	 * 
	 */
	public class Emprper {
		
		private ResultSet rs;
		
		private Emprper (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Persona
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Codigo BBX
		 * @return the column 'codnsz' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodnsz()
		throws SQLException {
			return rs.getString("codnsz");
		}
		/**
		 * Fecha de Alta
		 * @return the column 'fecalt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecalt()
		throws SQLException {
			return rs.getDate("fecalt");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecbaj()
		throws SQLException {
			return rs.getDate("fecbaj");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Centro de Trabajo
		 * @return the column 'domicilio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDomicilio()
		throws SQLException {
			return rs.getInt("domicilio");
		}
		/**
		 * Cuenta Cotizacion
		 * @return the column 'codccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodccc()
		throws SQLException {
			return rs.getString("codccc");
		}
		/**
		 * Centro de Coste
		 * @return the column 'codcco' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcco()
		throws SQLException {
			return rs.getInt("codcco");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Contrato temporal
		 * @return the column 'contr_temp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContr_temp()
		throws SQLException {
			return rs.getString("contr_temp");
		}
		/**
		 * Mayores de 65 años y mas de 35 años Cotizados
		 * @return the column 'mayor65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMayor65()
		throws SQLException {
			return rs.getString("mayor65");
		}
		/**
		 * Insertado en AFI
		 * @return the column 'afi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAfi()
		throws SQLException {
			return rs.getString("afi");
		}
		/**
		 * Tipo Contrato Agrario
		 * @return the column 'indagrario' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndagrario()
		throws SQLException {
			return rs.getString("indagrario");
		}
		/**
		 * Artista
		 * @return the column 'indgrupo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndgrupo()
		throws SQLException {
			return rs.getString("indgrupo");
		}
		/**
		 * Pariente 1º o 2º Grado
		 * @return the column 'pariente' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPariente()
		throws SQLException {
			return rs.getString("pariente");
		}

		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPercep(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM percep WHERE" 
					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Percep percep = new Percep(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPercep(percep, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitBonifica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM bonifica WHERE" 
					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Bonifica bonifica = new Bonifica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitBonifica(bonifica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAvisos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM avisos WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Avisos avisos = new Avisos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAvisos(avisos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNomina(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomina WHERE" 
					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nomina nomina = new Nomina(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomina(nomina, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabinci(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabinci WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabinci trabinci = new Trabinci(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabinci(trabinci, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominait(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominait WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominait nominait = new Nominait(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominait(nominait, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabdto WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabdto trabdto = new Trabdto(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabdto(trabdto, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitParteit(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM parteit WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Parteit parteit = new Parteit(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitParteit(parteit, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitParteitnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM parteitnu WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Parteitnu parteitnu = new Parteitnu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitParteitnu(parteitnu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominaitnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominaitnu WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominaitnu nominaitnu = new Nominaitnu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominaitnu(nominaitnu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquito(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquito WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquito finiquito = new Finiquito(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquito(finiquito, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquitodf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquitodf WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquitodf finiquitodf = new Finiquitodf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquitodf(finiquitodf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFiniquitonu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finiquitonu WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finiquitonu finiquitonu = new Finiquitonu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFiniquitonu(finiquitonu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitComunica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM comunica WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Comunica comunica = new Comunica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitComunica(comunica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCalculo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM calculo WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Calculo calculo = new Calculo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCalculo(calculo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominadf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominadf WHERE" 
					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominadf nominadf = new Nominadf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominadf(nominadf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmbargo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM embargo WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Embargo embargo = new Embargo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmbargo(embargo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominaex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominaex WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominaex nominaex = new Nominaex(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominaex(nominaex, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM regidocu WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Regidocu regidocu = new Regidocu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRegidocu(regidocu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPrcdivtrab(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM prcdivtrab WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Prcdivtrab prcdivtrab = new Prcdivtrab(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPrcdivtrab(prcdivtrab, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRemesa_parte_it(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM remesa_parte_it WHERE" 
					+ " empleado = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Remesa_parte_it remesa_parte_it = new Remesa_parte_it(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRemesa_parte_it(remesa_parte_it, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitRem_cert_empr_det(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM rem_cert_empr_det WHERE" 
					+ " empleado = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Rem_cert_empr_det rem_cert_empr_det = new Rem_cert_empr_det(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRem_cert_empr_det(rem_cert_empr_det, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Entidad
	 * 
	 */
	public class Entidad {
		
		private ResultSet rs;
		
		private Entidad (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Entidad
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Entidad
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitSucursal(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM sucursal WHERE" 
					+ " codent = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Sucursal sucursal = new Sucursal(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitSucursal(sucursal, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprban WHERE" 
					+ " codent = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprban emprban = new Emprban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprban(emprban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codent = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " entidad = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " entidad = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Epigrafe
	 * 
	 */
	public class Epigrafe {
		
		private ResultSet rs;
		
		private Epigrafe (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Epigrafe
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Epigrafe
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitLinepigr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linepigr WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linepigr linepigr = new Linepigr(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinepigr(linepigr, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCategoria(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM categoria WHERE" 
					+ " codepi = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Categoria categoria = new Categoria(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCategoria(categoria, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codepi = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " epigrafe = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCostes(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM costes WHERE" 
					+ " codepi = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Costes costes = new Costes(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCostes(costes, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Exclusion
	 * 
	 */
	public class Exclusion {
		
		private ResultSet rs;
		
		private Exclusion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Situacion del trabajador
		 * @return the column 'situacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSituacion()
		throws SQLException {
			return rs.getString("situacion");
		}
		/**
		 * Numero de hijos del trabajador
		 * @return the column 'hijos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHijos()
		throws SQLException {
			return rs.getString("hijos");
		}
		/**
		 * Importe correspondiente
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Finidto
	 * 
	 */
	public class Finidto {
		
		private ResultSet rs;
		
		private Finidto (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Descuento
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Descuento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Finidtodf
	 * 
	 */
	public class Finidtodf {
		
		private ResultSet rs;
		
		private Finidtodf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Descuento
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Descuento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Finidtonu
	 * 
	 */
	public class Finidtonu {
		
		private ResultSet rs;
		
		private Finidtonu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito Nuevo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Descuento
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Descuento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Finindem
	 * 
	 */
	public class Finindem {
		
		private ResultSet rs;
		
		private Finindem (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Indemnizacion
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Indemnizacion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Sujeto a I.R.P.F (S/N)
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIrpf()
		throws SQLException {
			return rs.getString("irpf");
		}

	}
	
	
	/**
	 * Finindemdf
	 * 
	 */
	public class Finindemdf {
		
		private ResultSet rs;
		
		private Finindemdf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Indemnizacion
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Indemnizacion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Sujeto a I.R.P.F. (S/N)
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIrpf()
		throws SQLException {
			return rs.getString("irpf");
		}

	}
	
	
	/**
	 * Finindemnu
	 * 
	 */
	public class Finindemnu {
		
		private ResultSet rs;
		
		private Finindemnu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito Nuevo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Texto Indemnizacion
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}
		/**
		 * Importe Indemnizacion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Sujeto a I.R.P.F. (S/N)
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIrpf()
		throws SQLException {
			return rs.getString("irpf");
		}

	}
	
	
	/**
	 * Finipext
	 * 
	 */
	public class Finipext {
		
		private ResultSet rs;
		
		private Finipext (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Inicio Devengo Paga Extra
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Devengo Paga Extra
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Importe Parte Proporcional
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}

	}
	
	
	/**
	 * Finipextdf
	 * 
	 */
	public class Finipextdf {
		
		private ResultSet rs;
		
		private Finipextdf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Inicio Devengo Paga Extra
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Devengo Paga Extra
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Importe Parte Proporcional
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}

	}
	
	
	/**
	 * Finipextnu
	 * 
	 */
	public class Finipextnu {
		
		private ResultSet rs;
		
		private Finipextnu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Finiquito Nuevo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Inicio Devengo Paga Extra
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Devengo Paga Extra
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Importe Parte Proporcional
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}

	}
	
	
	/**
	 * Finiquito
	 * 
	 */
	public class Finiquito {
		
		private ResultSet rs;
		
		private Finiquito (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecbaj()
		throws SQLException {
			return rs.getDate("fecbaj");
		}
		/**
		 * Causa de Baja
		 * @return the column 'causa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa()
		throws SQLException {
			return rs.getString("causa");
		}
		/**
		 * Fecha Desde Vacaciones
		 * @return the column 'vacfecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getVacfecini()
		throws SQLException {
			return rs.getDate("vacfecini");
		}
		/**
		 * Importe Vacaciones
		 * @return the column 'vacimporte' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getVacimporte()
		throws SQLException {
			return rs.getBigDecimal("vacimporte");
		}
		/**
		 * Total Conceptos Finiquito
		 * @return the column 'total_conceptos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_conceptos()
		throws SQLException {
			return rs.getBigDecimal("total_conceptos");
		}
		/**
		 * Base I.R.P.F.
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * % I.R.P.F.
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe Retenido I.R.P.F.
		 * @return the column 'importe_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf");
		}
		/**
		 * Importe Liquido
		 * @return the column 'liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiquido()
		throws SQLException {
			return rs.getBigDecimal("liquido");
		}
		/**
		 * Importe Indemnizaciones no sujetas a I.R.P.F.
		 * @return the column 'importesin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportesin()
		throws SQLException {
			return rs.getBigDecimal("importesin");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Codigo de Finiquito
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Calculo o Simulacion
		 * @return the column 'simula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSimula()
		throws SQLException {
			return rs.getString("simula");
		}
		/**
		 * Fecha de Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Días vacaciones
		 * @return the column 'diasvac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasvac()
		throws SQLException {
			return rs.getInt("diasvac");
		}
		/**
		 * Coste S.S. empresa
		 * @return the column 'costessemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCostessemp()
		throws SQLException {
			return rs.getBigDecimal("costessemp");
		}
		/**
		 * Grupo de tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Base Contingencias Generales
		 * @return the column 'basecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecg()
		throws SQLException {
			return rs.getBigDecimal("basecg");
		}
		/**
		 * % Contingencias Generales
		 * @return the column 'prccg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrccg()
		throws SQLException {
			return rs.getBigDecimal("prccg");
		}
		/**
		 * Importe Contingencias Generales
		 * @return the column 'importecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportecg()
		throws SQLException {
			return rs.getBigDecimal("importecg");
		}
		/**
		 * Base Accidente de Trabajo
		 * @return the column 'baseacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc()
		throws SQLException {
			return rs.getBigDecimal("baseacc");
		}
		/**
		 * % Accidente de Trabajo
		 * @return the column 'prcacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcacc()
		throws SQLException {
			return rs.getBigDecimal("prcacc");
		}
		/**
		 * Importe Accidente de Trabajo
		 * @return the column 'importeacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporteacc()
		throws SQLException {
			return rs.getBigDecimal("importeacc");
		}

		public void visitFinipext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipext WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipext finipext = new Finipext(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipext(finipext, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinindem(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finindem WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finindem finindem = new Finindem(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinindem(finindem, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinidto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finidto WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finidto finidto = new Finidto(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinidto(finidto, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Finiquitodf
	 * 
	 */
	public class Finiquitodf {
		
		private ResultSet rs;
		
		private Finiquitodf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecbaj()
		throws SQLException {
			return rs.getDate("fecbaj");
		}
		/**
		 * Causa de Baja
		 * @return the column 'causa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa()
		throws SQLException {
			return rs.getString("causa");
		}
		/**
		 * Fecha Desde Vacaciones
		 * @return the column 'vacfecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getVacfecini()
		throws SQLException {
			return rs.getDate("vacfecini");
		}
		/**
		 * Importe Vacaciones
		 * @return the column 'vacimporte' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getVacimporte()
		throws SQLException {
			return rs.getBigDecimal("vacimporte");
		}
		/**
		 * Total Conceptos Finiquito
		 * @return the column 'total_conceptos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_conceptos()
		throws SQLException {
			return rs.getBigDecimal("total_conceptos");
		}
		/**
		 * Base I.R.P.F.
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * % I.R.P.F.
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe Retenido I.R.P.F.
		 * @return the column 'importe_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf");
		}
		/**
		 * Importe Liquido
		 * @return the column 'liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiquido()
		throws SQLException {
			return rs.getBigDecimal("liquido");
		}
		/**
		 * Importe Indemnizaciones no sujetas a I.R.P.F.
		 * @return the column 'importesin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportesin()
		throws SQLException {
			return rs.getBigDecimal("importesin");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Codigo de Finiquito de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de la Nomina Resumen de Atrasos
		 * @return the column 'cdgnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdgnom()
		throws SQLException {
			return rs.getInt("cdgnom");
		}
		/**
		 * Fecha de Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Días vacaciones
		 * @return the column 'diasvac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasvac()
		throws SQLException {
			return rs.getInt("diasvac");
		}
		/**
		 * Coste S.S. empresa
		 * @return the column 'costessemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCostessemp()
		throws SQLException {
			return rs.getBigDecimal("costessemp");
		}
		/**
		 * Grupo de tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Base Contingencias Generales
		 * @return the column 'basecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecg()
		throws SQLException {
			return rs.getBigDecimal("basecg");
		}
		/**
		 * % Contingencias Generales
		 * @return the column 'prccg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrccg()
		throws SQLException {
			return rs.getBigDecimal("prccg");
		}
		/**
		 * Importe Contingencias Generales
		 * @return the column 'importecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportecg()
		throws SQLException {
			return rs.getBigDecimal("importecg");
		}
		/**
		 * Base Accidente de Trabajo
		 * @return the column 'baseacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc()
		throws SQLException {
			return rs.getBigDecimal("baseacc");
		}
		/**
		 * % Accidente de Trabajo
		 * @return the column 'prcacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcacc()
		throws SQLException {
			return rs.getBigDecimal("prcacc");
		}
		/**
		 * Importe Accidente de Trabajo
		 * @return the column 'importeacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporteacc()
		throws SQLException {
			return rs.getBigDecimal("importeacc");
		}

		public void visitFinipextdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipextdf WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipextdf finipextdf = new Finipextdf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipextdf(finipextdf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinindemdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finindemdf WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finindemdf finindemdf = new Finindemdf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinindemdf(finindemdf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinidtodf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finidtodf WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finidtodf finidtodf = new Finidtodf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinidtodf(finidtodf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Finiquitonu
	 * 
	 */
	public class Finiquitonu {
		
		private ResultSet rs;
		
		private Finiquitonu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecbaj()
		throws SQLException {
			return rs.getDate("fecbaj");
		}
		/**
		 * Causa de Baja
		 * @return the column 'causa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa()
		throws SQLException {
			return rs.getString("causa");
		}
		/**
		 * Fecha Desde Vacaciones
		 * @return the column 'vacfecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getVacfecini()
		throws SQLException {
			return rs.getDate("vacfecini");
		}
		/**
		 * Importe Vacaciones
		 * @return the column 'vacimporte' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getVacimporte()
		throws SQLException {
			return rs.getBigDecimal("vacimporte");
		}
		/**
		 * Total Conceptos Finiquito
		 * @return the column 'total_conceptos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_conceptos()
		throws SQLException {
			return rs.getBigDecimal("total_conceptos");
		}
		/**
		 * Base I.R.P.F.
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * % I.R.P.F.
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe Retenido I.R.P.F.
		 * @return the column 'importe_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf");
		}
		/**
		 * Importe Liquido
		 * @return the column 'liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiquido()
		throws SQLException {
			return rs.getBigDecimal("liquido");
		}
		/**
		 * Importe Indemnizaciones no sujetas a I.R.P.F.
		 * @return the column 'importesin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportesin()
		throws SQLException {
			return rs.getBigDecimal("importesin");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Codigo de Finiquito Nuevo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Calculo o simulacion
		 * @return the column 'simula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSimula()
		throws SQLException {
			return rs.getString("simula");
		}
		/**
		 * Fecha de Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Días vacaciones
		 * @return the column 'diasvac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasvac()
		throws SQLException {
			return rs.getInt("diasvac");
		}
		/**
		 * Coste S.S. empresa
		 * @return the column 'costessemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCostessemp()
		throws SQLException {
			return rs.getBigDecimal("costessemp");
		}
		/**
		 * Grupo de tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Base Contingencias Generales
		 * @return the column 'basecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecg()
		throws SQLException {
			return rs.getBigDecimal("basecg");
		}
		/**
		 * % Contingencias Generales
		 * @return the column 'prccg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrccg()
		throws SQLException {
			return rs.getBigDecimal("prccg");
		}
		/**
		 * Importe Contingencias Generales
		 * @return the column 'importecg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImportecg()
		throws SQLException {
			return rs.getBigDecimal("importecg");
		}
		/**
		 * Base Accidente de Trabajo
		 * @return the column 'baseacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc()
		throws SQLException {
			return rs.getBigDecimal("baseacc");
		}
		/**
		 * % Accidente de Trabajo
		 * @return the column 'prcacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcacc()
		throws SQLException {
			return rs.getBigDecimal("prcacc");
		}
		/**
		 * Importe Accidente de Trabajo
		 * @return the column 'importeacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporteacc()
		throws SQLException {
			return rs.getBigDecimal("importeacc");
		}

		public void visitFinipextnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finipextnu WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finipextnu finipextnu = new Finipextnu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinipextnu(finipextnu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinindemnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finindemnu WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finindemnu finindemnu = new Finindemnu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinindemnu(finindemnu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitFinidtonu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM finidtonu WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Finidtonu finidtonu = new Finidtonu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitFinidtonu(finidtonu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Httaviso
	 * 
	 */
	public class Httaviso {
		
		private ResultSet rs;
		
		private Httaviso (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Hoja de Trabajo del Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Orden del Aviso
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Fecha del Aviso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Tipo de Aviso
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Texto del Aviso
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}

	}
	
	
	/**
	 * Httbonificacion
	 * 
	 */
	public class Httbonificacion {
		
		private ResultSet rs;
		
		private Httbonificacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Hoja de Trabajo del Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Orden de la Bonificación
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Código de Bonificación
		 * @return the column 'bonificacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getBonificacion()
		throws SQLException {
			return rs.getInt("bonificacion");
		}
		/**
		 * Fecha Concesión Bonificación
		 * @return the column 'fecinicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicio()
		throws SQLException {
			return rs.getDate("fecinicio");
		}
		/**
		 * Fecha Terminación Bonificación
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Número Horas Formación
		 * @return the column 'horas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHoras()
		throws SQLException {
			return rs.getInt("horas");
		}
		/**
		 * Importe Bonificación Directo
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Httcomplemento
	 * 
	 */
	public class Httcomplemento {
		
		private ResultSet rs;
		
		private Httcomplemento (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Hoja de Trabajo del Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Orden del Complemento
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Código de Complemento
		 * @return the column 'complemento' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getComplemento()
		throws SQLException {
			return rs.getString("complemento");
		}
		/**
		 * Forma de Cálculo
		 * @return the column 'calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCalculo()
		throws SQLException {
			return rs.getString("calculo");
		}
		/**
		 * Mes a Aplicar
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Tipo de Cotización
		 * @return the column 'cotizacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCotizacion()
		throws SQLException {
			return rs.getString("cotizacion");
		}
		/**
		 * Importe Complemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * % Garantizado I.L.T.
		 * @return the column 'garantizado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getGarantizado()
		throws SQLException {
			return rs.getBigDecimal("garantizado");
		}
		/**
		 * Redondeo Paga Extra
		 * @return the column 'redondeo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRedondeo()
		throws SQLException {
			return rs.getString("redondeo");
		}

	}
	
	
	/**
	 * Httincidencia
	 * 
	 */
	public class Httincidencia {
		
		private ResultSet rs;
		
		private Httincidencia (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Hoja de Trabajo del Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Orden de la Otra Incidencia
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecinicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicio()
		throws SQLException {
			return rs.getDate("fecinicio");
		}
		/**
		 * Fecha Fin Incidencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de Incidencia
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Horas, Dias
		 * @return the column 'cantidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCantidad()
		throws SQLException {
			return rs.getInt("cantidad");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Httrabajador
	 * 
	 */
	public class Httrabajador {
		
		private ResultSet rs;
		
		private Httrabajador (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Hoja de Trabajo del Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Actividad
		 * @return the column 'actividad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getActividad()
		throws SQLException {
			return rs.getInt("actividad");
		}
		/**
		 * Centro de Trabajo
		 * @return the column 'domicilio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDomicilio()
		throws SQLException {
			return rs.getInt("domicilio");
		}
		/**
		 * Primer Apellido
		 * @return the column 'apellido1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellido1()
		throws SQLException {
			return rs.getString("apellido1");
		}
		/**
		 * Segundo Apellido
		 * @return the column 'apellido2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellido2()
		throws SQLException {
			return rs.getString("apellido2");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Tipo Documento
		 * @return the column 'inddoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddoc()
		throws SQLException {
			return rs.getString("inddoc");
		}
		/**
		 * Pais Emisor
		 * @return the column 'paiemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaiemi()
		throws SQLException {
			return rs.getString("paiemi");
		}
		/**
		 * Numero Documento
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Lugar de Nacimiento
		 * @return the column 'lugnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLugnac()
		throws SQLException {
			return rs.getString("lugnac");
		}
		/**
		 * Provincia de Nacimiento
		 * @return the column 'pronac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPronac()
		throws SQLException {
			return rs.getString("pronac");
		}
		/**
		 * Fecha de Nacimiento
		 * @return the column 'fecnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnac()
		throws SQLException {
			return rs.getDate("fecnac");
		}
		/**
		 * Nombre del Padre
		 * @return the column 'padre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPadre()
		throws SQLException {
			return rs.getString("padre");
		}
		/**
		 * Nombre de la Madre
		 * @return the column 'madre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMadre()
		throws SQLException {
			return rs.getString("madre");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Estado Civil
		 * @return the column 'estado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEstado()
		throws SQLException {
			return rs.getString("estado");
		}
		/**
		 * Cuenta de Cotización
		 * @return the column 'ccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCcc()
		throws SQLException {
			return rs.getString("ccc");
		}
		/**
		 * Profesión
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Categoría Laboral
		 * @return the column 'categoria' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCategoria()
		throws SQLException {
			return rs.getString("categoria");
		}
		/**
		 * Descripción de Categoria
		 * @return the column 'descategoria' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescategoria()
		throws SQLException {
			return rs.getString("descategoria");
		}
		/**
		 * Grupo de Tarifa
		 * @return the column 'tarifa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTarifa()
		throws SQLException {
			return rs.getString("tarifa");
		}
		/**
		 * Epígrafe Accidente de Trabajo
		 * @return the column 'epigrafe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEpigrafe()
		throws SQLException {
			return rs.getString("epigrafe");
		}
		/**
		 * Código Nacional de Ocupación
		 * @return the column 'cno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCno()
		throws SQLException {
			return rs.getString("cno");
		}
		/**
		 * Nivel Retributibo
		 * @return the column 'nivel' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNivel()
		throws SQLException {
			return rs.getString("nivel");
		}
		/**
		 * Prorrateo Cotización
		 * @return the column 'cotizacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCotizacion()
		throws SQLException {
			return rs.getString("cotizacion");
		}
		/**
		 * Prorrateo Retribución
		 * @return the column 'retribucion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRetribucion()
		throws SQLException {
			return rs.getString("retribucion");
		}
		/**
		 * Actualizar Percepciones según Convenio
		 * @return the column 'actualizar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getActualizar()
		throws SQLException {
			return rs.getString("actualizar");
		}
		/**
		 * Fecha de Alta
		 * @return the column 'fecalta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecalta()
		throws SQLException {
			return rs.getDate("fecalta");
		}
		/**
		 * Fecha de Baja
		 * @return the column 'fecbaja' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecbaja()
		throws SQLException {
			return rs.getDate("fecbaja");
		}
		/**
		 * Fecha de Antigüedad
		 * @return the column 'fecantiguedad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecantiguedad()
		throws SQLException {
			return rs.getDate("fecantiguedad");
		}
		/**
		 * Número Seguridad Social
		 * @return the column 'numeross' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumeross()
		throws SQLException {
			return rs.getString("numeross");
		}
		/**
		 * Número de Matrícula
		 * @return the column 'matricula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMatricula()
		throws SQLException {
			return rs.getInt("matricula");
		}
		/**
		 * Código de Contrato
		 * @return the column 'contrato' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContrato()
		throws SQLException {
			return rs.getString("contrato");
		}
		/**
		 * Código Contrato TC2
		 * @return the column 'contratotc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContratotc2()
		throws SQLException {
			return rs.getString("contratotc2");
		}
		/**
		 * Fecha Inicio Contrato
		 * @return the column 'fecinicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicio()
		throws SQLException {
			return rs.getDate("fecinicio");
		}
		/**
		 * Fecha Fin Contrato
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Duración Contrato en Días
		 * @return the column 'diascontrato' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiascontrato()
		throws SQLException {
			return rs.getInt("diascontrato");
		}
		/**
		 * Entidad Bancaria
		 * @return the column 'entidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEntidad()
		throws SQLException {
			return rs.getString("entidad");
		}
		/**
		 * Sucursal Bancaria
		 * @return the column 'sucursal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSucursal()
		throws SQLException {
			return rs.getString("sucursal");
		}
		/**
		 * Digito Control
		 * @return the column 'dc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc()
		throws SQLException {
			return rs.getString("dc");
		}
		/**
		 * Número Cuenta Bancaria
		 * @return the column 'cuenta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCuenta()
		throws SQLException {
			return rs.getString("cuenta");
		}
		/**
		 * Indicador IRPF
		 * @return the column 'indirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndirpf()
		throws SQLException {
			return rs.getString("indirpf");
		}
		/**
		 * % Retención IRPF en Nómina
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Marca Trabajador Especial
		 * @return the column 'especial' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEspecial()
		throws SQLException {
			return rs.getString("especial");
		}
		/**
		 * Descontar Días Incidencias
		 * @return the column 'dtoincidencia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDtoincidencia()
		throws SQLException {
			return rs.getString("dtoincidencia");
		}
		/**
		 * Descontar Días IT
		 * @return the column 'dtoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDtoit()
		throws SQLException {
			return rs.getString("dtoit");
		}
		/**
		 * Coeficiente Reductor Salarial
		 * @return the column 'coeficiente' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoeficiente()
		throws SQLException {
			return rs.getBigDecimal("coeficiente");
		}
		/**
		 * Minutos Jornada Semana Real
		 * @return the column 'jornada' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getJornada()
		throws SQLException {
			return rs.getInt("jornada");
		}
		/**
		 * Indicador Tiempo de Contrato
		 * @return the column 'tiempoparcial' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTiempoparcial()
		throws SQLException {
			return rs.getString("tiempoparcial");
		}
		/**
		 * Minutos Jornada Semanal Tiempo Parcial
		 * @return the column 'jornadatp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getJornadatp()
		throws SQLException {
			return rs.getInt("jornadatp");
		}
		/**
		 * Minutos/Días Cotización Tiempo Parcail
		 * @return the column 'minutosdiastp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMinutosdiastp()
		throws SQLException {
			return rs.getInt("minutosdiastp");
		}
		/**
		 * Base Cálculo Antigüedad
		 * @return the column 'baseantiguedad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseantiguedad()
		throws SQLException {
			return rs.getBigDecimal("baseantiguedad");
		}
		/**
		 * Número de Autorización de Pluriempleo
		 * @return the column 'pluriempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPluriempleo()
		throws SQLException {
			return rs.getString("pluriempleo");
		}
		/**
		 * Pluriempleo: % sobre Tope Mínimo Cotización
		 * @return the column 'minpluriempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMinpluriempleo()
		throws SQLException {
			return rs.getBigDecimal("minpluriempleo");
		}
		/**
		 * Pluriempleo: % sobre Tope Máximo Cotización
		 * @return the column 'maxpluriempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxpluriempleo()
		throws SQLException {
			return rs.getBigDecimal("maxpluriempleo");
		}
		/**
		 * Fecha Autorización de Pluriempleo
		 * @return the column 'fecpluriempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecpluriempleo()
		throws SQLException {
			return rs.getDate("fecpluriempleo");
		}

		public void visitHttbonificacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httbonificacion WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httbonificacion httbonificacion = new Httbonificacion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttbonificacion(httbonificacion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttaviso(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httaviso WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httaviso httaviso = new Httaviso(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttaviso(httaviso, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttcomplemento(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httcomplemento WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httcomplemento httcomplemento = new Httcomplemento(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttcomplemento(httcomplemento, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttincidencia(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httincidencia WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httincidencia httincidencia = new Httincidencia(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttincidencia(httincidencia, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Impr11x
	 * 
	 */
	public class Impr11x {
		
		private ResultSet rs;
		
		private Impr11x (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de 11X
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Tipo de Impreso
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Administracion Hacienda
		 * @return the column 'codadm' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodadm()
		throws SQLException {
			return rs.getString("codadm");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Anio de Devengo
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Numero Trimestre -- 110
		 * @return the column 'trimestre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTrimestre()
		throws SQLException {
			return rs.getInt("trimestre");
		}
		/**
		 * Numero de Mes -- 111
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Trabajo-Dinerario-Num. Perceptores
		 * @return the column 'tradinper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTradinper()
		throws SQLException {
			return rs.getInt("tradinper");
		}
		/**
		 * Trabajo-Dinerario-Importe
		 * @return the column 'tradinimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTradinimp()
		throws SQLException {
			return rs.getBigDecimal("tradinimp");
		}
		/**
		 * Trabajo-Dinerario-Retencion
		 * @return the column 'tradinret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTradinret()
		throws SQLException {
			return rs.getBigDecimal("tradinret");
		}
		/**
		 * Trabajo-Especie-Num. Perceptores
		 * @return the column 'traespper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTraespper()
		throws SQLException {
			return rs.getInt("traespper");
		}
		/**
		 * Trabajo-Especie-Importe
		 * @return the column 'traespimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTraespimp()
		throws SQLException {
			return rs.getBigDecimal("traespimp");
		}
		/**
		 * Trabajo-EspecieRetencion
		 * @return the column 'traespret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTraespret()
		throws SQLException {
			return rs.getBigDecimal("traespret");
		}
		/**
		 * Act.Profes.-Dinerario-Num. Perceptores
		 * @return the column 'actdinper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getActdinper()
		throws SQLException {
			return rs.getInt("actdinper");
		}
		/**
		 * Act.Profes.-Dinerario-Importe
		 * @return the column 'actdinimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getActdinimp()
		throws SQLException {
			return rs.getBigDecimal("actdinimp");
		}
		/**
		 * Act.Profes.-Dinerario-Retencion
		 * @return the column 'actdinret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getActdinret()
		throws SQLException {
			return rs.getBigDecimal("actdinret");
		}
		/**
		 * Act.Profes.-Especie-Num. Perceptores
		 * @return the column 'actespper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getActespper()
		throws SQLException {
			return rs.getInt("actespper");
		}
		/**
		 * Act.Profes.-Especie-Importe
		 * @return the column 'actespimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getActespimp()
		throws SQLException {
			return rs.getBigDecimal("actespimp");
		}
		/**
		 * Act.Profes.-EspecieRetencion
		 * @return the column 'actespret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getActespret()
		throws SQLException {
			return rs.getBigDecimal("actespret");
		}
		/**
		 * Premios-Dinerario-Num. Perceptores
		 * @return the column 'predinper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPredinper()
		throws SQLException {
			return rs.getInt("predinper");
		}
		/**
		 * Premios-Dinerario-Importe
		 * @return the column 'predinimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPredinimp()
		throws SQLException {
			return rs.getBigDecimal("predinimp");
		}
		/**
		 * Premios-Dinerario-Retencion
		 * @return the column 'predinret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPredinret()
		throws SQLException {
			return rs.getBigDecimal("predinret");
		}
		/**
		 * Premios-Especie-Num. Perceptores
		 * @return the column 'preespper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPreespper()
		throws SQLException {
			return rs.getInt("preespper");
		}
		/**
		 * Premios-Especie-Importe
		 * @return the column 'preespimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPreespimp()
		throws SQLException {
			return rs.getBigDecimal("preespimp");
		}
		/**
		 * Premios-EspecieRetencion
		 * @return the column 'preespret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPreespret()
		throws SQLException {
			return rs.getBigDecimal("preespret");
		}
		/**
		 * Total Liquidacion
		 * @return the column 'liqtotal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiqtotal()
		throws SQLException {
			return rs.getBigDecimal("liqtotal");
		}
		/**
		 * Forma de Pago
		 * @return the column 'fpago' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFpago()
		throws SQLException {
			return rs.getString("fpago");
		}
		/**
		 * Entidad Bancaria
		 * @return the column 'entidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEntidad()
		throws SQLException {
			return rs.getString("entidad");
		}
		/**
		 * Sucursal Bancaria
		 * @return the column 'sucursal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSucursal()
		throws SQLException {
			return rs.getString("sucursal");
		}
		/**
		 * Digito Control
		 * @return the column 'dc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc()
		throws SQLException {
			return rs.getString("dc");
		}
		/**
		 * Numero de Cuenta
		 * @return the column 'cuenta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCuenta()
		throws SQLException {
			return rs.getString("cuenta");
		}
		/**
		 * Fecha Calculo
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Derechos-Imagen-Num. Perceptores
		 * @return the column 'imgper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getImgper()
		throws SQLException {
			return rs.getInt("imgper");
		}
		/**
		 * Derechos-Imagen-Importe
		 * @return the column 'imgimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImgimp()
		throws SQLException {
			return rs.getBigDecimal("imgimp");
		}
		/**
		 * Derechos-Imagen-Retencion
		 * @return the column 'imgret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImgret()
		throws SQLException {
			return rs.getBigDecimal("imgret");
		}
		/**
		 * Modalidad declaraciones de impuestos
		 * @return the column 'modimpuesto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getModimpuesto()
		throws SQLException {
			return rs.getString("modimpuesto");
		}
		/**
		 * Número de referencia completo
		 * @return the column 'nrc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNrc()
		throws SQLException {
			return rs.getString("nrc");
		}
		/**
		 * Fecha de remesa o impresión
		 * @return the column 'fecremimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecremimp()
		throws SQLException {
			return rs.getDate("fecremimp");
		}

	}
	
	
	/**
	 * Impr190
	 * 
	 */
	public class Impr190 {
		
		private ResultSet rs;
		
		private Impr190 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de 190
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Administracion Hacienda
		 * @return the column 'codadm' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodadm()
		throws SQLException {
			return rs.getString("codadm");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Anio de Devengo
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Numero Perceptores
		 * @return the column 'num_percep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_percep()
		throws SQLException {
			return rs.getInt("num_percep");
		}
		/**
		 * Importe Percepciones
		 * @return the column 'imp_percep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_percep()
		throws SQLException {
			return rs.getBigDecimal("imp_percep");
		}
		/**
		 * Importe Retenciones
		 * @return the column 'imp_retenc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_retenc()
		throws SQLException {
			return rs.getBigDecimal("imp_retenc");
		}
		/**
		 * Fecha de Proceso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * $column.remarks
		 * @return the column 'disco' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDisco()
		throws SQLException {
			return rs.getString("disco");
		}
		/**
		 * Descuadrado
		 * @return the column 'descuadrado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescuadrado()
		throws SQLException {
			return rs.getString("descuadrado");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}

		public void visitLin190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lin190 WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lin190 lin190 = new Lin190(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLin190(lin190, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Lbonifica
	 * 
	 */
	public class Lbonifica {
		
		private ResultSet rs;
		
		private Lbonifica (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Simulación
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Número Secuencial
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Codigo de Bonificacion
		 * @return the column 'codbon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodbon()
		throws SQLException {
			return rs.getInt("codbon");
		}
		/**
		 * Fecha Concesion Bonificacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Terminacion Bonificacion
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Numero Horas Formacion
		 * @return the column 'horas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHoras()
		throws SQLException {
			return rs.getInt("horas");
		}
		/**
		 * Importe Bonificacion Directo
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Lcomunica
	 * 
	 */
	public class Lcomunica {
		
		private ResultSet rs;
		
		private Lcomunica (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Simulación
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Número Secuencial
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Numero de Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Anio de Nacimiento Hijo
		 * @return the column 'anionac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnionac()
		throws SQLException {
			return rs.getInt("anionac");
		}
		/**
		 * Minusvalia Hijo
		 * @return the column 'xminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getXminus()
		throws SQLException {
			return rs.getString("xminus");
		}
		/**
		 * Descendiente o ascendiente
		 * @return the column 'des_asc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDes_asc()
		throws SQLException {
			return rs.getString("des_asc");
		}
		/**
		 * Descendiente por entero
		 * @return the column 'descen_ent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescen_ent()
		throws SQLException {
			return rs.getString("descen_ent");
		}
		/**
		 * Convivencia
		 * @return the column 'conviv' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getConviv()
		throws SQLException {
			return rs.getInt("conviv");
		}

	}
	
	
	/**
	 * Lin190
	 * 
	 */
	public class Lin190 {
		
		private ResultSet rs;
		
		private Lin190 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de 190
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Linea
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * NIF Perceptor
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Clave Percepcion
		 * @return the column 'clave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getClave()
		throws SQLException {
			return rs.getString("clave");
		}
		/**
		 * Subclave Percepcion
		 * @return the column 'subclave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSubclave()
		throws SQLException {
			return rs.getString("subclave");
		}
		/**
		 * Percepcion Dineraria
		 * @return the column 'imp_per_din' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_per_din()
		throws SQLException {
			return rs.getBigDecimal("imp_per_din");
		}
		/**
		 * Importe Retencion Dineraria
		 * @return the column 'imp_ret_din' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ret_din()
		throws SQLException {
			return rs.getBigDecimal("imp_ret_din");
		}
		/**
		 * Importe Percepcion En Especie
		 * @return the column 'imp_per_esp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_per_esp()
		throws SQLException {
			return rs.getBigDecimal("imp_per_esp");
		}
		/**
		 * Importe Ingresos a Cuenta
		 * @return the column 'imp_ing_cta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ing_cta()
		throws SQLException {
			return rs.getBigDecimal("imp_ing_cta");
		}
		/**
		 * Importe Ingresos Repercutidos
		 * @return the column 'imp_ing_rep' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_ing_rep()
		throws SQLException {
			return rs.getBigDecimal("imp_ing_rep");
		}
		/**
		 * Ejercicio Devengo
		 * @return the column 'devengo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDevengo()
		throws SQLException {
			return rs.getInt("devengo");
		}
		/**
		 * Anio Nacimiento
		 * @return the column 'anionac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnionac()
		throws SQLException {
			return rs.getInt("anionac");
		}
		/**
		 * Grado de Minusvalia
		 * @return the column 'xminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getXminus()
		throws SQLException {
			return rs.getInt("xminus");
		}
		/**
		 * Situacion Familiar
		 * @return the column 'sitfam' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSitfam()
		throws SQLException {
			return rs.getInt("sitfam");
		}
		/**
		 * NIF Conyuge
		 * @return the column 'nifcony' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNifcony()
		throws SQLException {
			return rs.getString("nifcony");
		}
		/**
		 * Hijos menores de 3 anios
		 * @return the column 'hijo_3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHijo_3()
		throws SQLException {
			return rs.getInt("hijo_3");
		}
		/**
		 * Hijos entre 3 y 16
		 * @return the column 'hijo_16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHijo_16()
		throws SQLException {
			return rs.getInt("hijo_16");
		}
		/**
		 * Hijos entre 16 y 25
		 * @return the column 'hijo_25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHijo_25()
		throws SQLException {
			return rs.getInt("hijo_25");
		}
		/**
		 * Hijos Discapacitados entre 33 y 65
		 * @return the column 'minus_33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMinus_33()
		throws SQLException {
			return rs.getInt("minus_33");
		}
		/**
		 * Hijos Discapacitados mas de 65
		 * @return the column 'minus_65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMinus_65()
		throws SQLException {
			return rs.getInt("minus_65");
		}
		/**
		 * Numero total de hijos
		 * @return the column 'hijos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHijos()
		throws SQLException {
			return rs.getInt("hijos");
		}
		/**
		 * Tipo de Relacion
		 * @return the column 'relacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRelacion()
		throws SQLException {
			return rs.getInt("relacion");
		}
		/**
		 * Importe Reducciones
		 * @return the column 'imp_reducc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_reducc()
		throws SQLException {
			return rs.getBigDecimal("imp_reducc");
		}
		/**
		 * Importe Gastos
		 * @return the column 'imp_gastos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_gastos()
		throws SQLException {
			return rs.getBigDecimal("imp_gastos");
		}
		/**
		 * Importe Pension Compensatoria
		 * @return the column 'imp_pension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_pension()
		throws SQLException {
			return rs.getBigDecimal("imp_pension");
		}
		/**
		 * Importe Anualidades
		 * @return the column 'imp_anual' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImp_anual()
		throws SQLException {
			return rs.getBigDecimal("imp_anual");
		}
		/**
		 * Nombre y Apellidos
		 * @return the column 'nomapel' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomapel()
		throws SQLException {
			return rs.getString("nomapel");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Ceuta / Melilla
		 * @return the column 'c_m' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getC_m()
		throws SQLException {
			return rs.getString("c_m");
		}
		/**
		 * Total Descendientes por Entero
		 * @return the column 'descentero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescentero()
		throws SQLException {
			return rs.getInt("descentero");
		}
		/**
		 * Total Ascendientes
		 * @return the column 'totalasc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTotalasc()
		throws SQLException {
			return rs.getInt("totalasc");
		}
		/**
		 * Total Ascendentes por Entero
		 * @return the column 'ascentero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscentero()
		throws SQLException {
			return rs.getInt("ascentero");
		}
		/**
		 * Ascendientes Discapacitados entre 33 y 65
		 * @return the column 'ascminus_33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscminus_33()
		throws SQLException {
			return rs.getInt("ascminus_33");
		}
		/**
		 * Ascendientes Discapacitados mas de 65
		 * @return the column 'ascminus_65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscminus_65()
		throws SQLException {
			return rs.getInt("ascminus_65");
		}
		/**
		 * Movilidad geográfica
		 * @return the column 'movilidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getMovilidad()
		throws SQLException {
			return rs.getDate("movilidad");
		}
		/**
		 * Prolongación actividad laboral
		 * @return the column 'prolongacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProlongacion()
		throws SQLException {
			return rs.getString("prolongacion");
		}
		/**
		 * Descendientes Menores de 3 años
		 * @return the column 'descme3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescme3()
		throws SQLException {
			return rs.getInt("descme3");
		}
		/**
		 * Descendientes Menores de 3 años Enteros
		 * @return the column 'descme3e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescme3e()
		throws SQLException {
			return rs.getInt("descme3e");
		}
		/**
		 * Descendientes Mayores de 3 años
		 * @return the column 'descma3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescma3()
		throws SQLException {
			return rs.getInt("descma3");
		}
		/**
		 * Descendientes Mayores de 3 años Enteros
		 * @return the column 'descma3e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescma3e()
		throws SQLException {
			return rs.getInt("descma3e");
		}
		/**
		 * Descendientes Discapacitados >=33% <65%
		 * @return the column 'descdi33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdi33()
		throws SQLException {
			return rs.getInt("descdi33");
		}
		/**
		 * Descendientes Discapacitados >=33% <65% Enteros
		 * @return the column 'descdi33e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdi33e()
		throws SQLException {
			return rs.getInt("descdi33e");
		}
		/**
		 * Descendientes Discapacitados Movilidad Reducida
		 * @return the column 'descdimr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdimr()
		throws SQLException {
			return rs.getInt("descdimr");
		}
		/**
		 * Descendientes Discapacitados Movilidad Reducida Enteros
		 * @return the column 'descdimre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdimre()
		throws SQLException {
			return rs.getInt("descdimre");
		}
		/**
		 * Descendientes Discapacitados >65%
		 * @return the column 'descdi65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdi65()
		throws SQLException {
			return rs.getInt("descdi65");
		}
		/**
		 * Descendientes Discapacitados >65% Enteros
		 * @return the column 'descdi65e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDescdi65e()
		throws SQLException {
			return rs.getInt("descdi65e");
		}
		/**
		 * Ascendientes Menores de 75 años
		 * @return the column 'ascme75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscme75()
		throws SQLException {
			return rs.getInt("ascme75");
		}
		/**
		 * Ascendientes Menores de 75 años Enteros
		 * @return the column 'ascme75e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscme75e()
		throws SQLException {
			return rs.getInt("ascme75e");
		}
		/**
		 * Ascendientes Mayores de 75 años
		 * @return the column 'ascma75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscma75()
		throws SQLException {
			return rs.getInt("ascma75");
		}
		/**
		 * Ascendientes Mayores de 75 años Enteros
		 * @return the column 'ascma75e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscma75e()
		throws SQLException {
			return rs.getInt("ascma75e");
		}
		/**
		 * Ascendientes Discapacitados >=33% <65%
		 * @return the column 'ascdi33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdi33()
		throws SQLException {
			return rs.getInt("ascdi33");
		}
		/**
		 * Ascendientes Discapacitados >=33% <65% Enteros
		 * @return the column 'ascdi33e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdi33e()
		throws SQLException {
			return rs.getInt("ascdi33e");
		}
		/**
		 * Ascendientes Discapacitados Movilidad Reducida
		 * @return the column 'ascdimr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdimr()
		throws SQLException {
			return rs.getInt("ascdimr");
		}
		/**
		 * Ascendientes Discapacitados Movilidad Reducida Enteros
		 * @return the column 'ascdimre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdimre()
		throws SQLException {
			return rs.getInt("ascdimre");
		}
		/**
		 * Ascendientes Discapacitados >65%
		 * @return the column 'ascdi65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdi65()
		throws SQLException {
			return rs.getInt("ascdi65");
		}
		/**
		 * Ascendientes Discapacitados >65% Enteros
		 * @return the column 'ascdi65e' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAscdi65e()
		throws SQLException {
			return rs.getInt("ascdi65e");
		}

	}
	
	
	/**
	 * Lin_divisa
	 * 
	 */
	public class Lin_divisa {
		
		private ResultSet rs;
		
		private Lin_divisa (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Divisa Origen
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Codigo de Divisa Destino
		 * @return the column 'divisa_final' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa_final()
		throws SQLException {
			return rs.getString("divisa_final");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Unidades de la divisa final
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe de divisa origen para las unidades finales
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Linautom
	 * 
	 */
	public class Linautom {
		
		private ResultSet rs;
		
		private Linautom (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Fichero Automatico
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Empresa/Trabajador
		 * @return the column 'codigo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodigo()
		throws SQLException {
			return rs.getInt("codigo");
		}

	}
	
	
	/**
	 * Linbasec
	 * 
	 */
	public class Linbasec {
		
		private ResultSet rs;
		
		private Linbasec (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Grupo Tarifa
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Minimo Cotizacion Contingencias Generales
		 * @return the column 'mincot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMincot()
		throws SQLException {
			return rs.getBigDecimal("mincot");
		}
		/**
		 * Maximo Cotizacion Contingencias Generales
		 * @return the column 'maxcot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxcot()
		throws SQLException {
			return rs.getBigDecimal("maxcot");
		}
		/**
		 * Minimo Dia Tiempo Parcial
		 * @return the column 'mindia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMindia()
		throws SQLException {
			return rs.getBigDecimal("mindia");
		}
		/**
		 * Minimo Hora Tiempo Parcial
		 * @return the column 'minhor' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMinhor()
		throws SQLException {
			return rs.getBigDecimal("minhor");
		}
		/**
		 * Jormada Díaria Agraria
		 * @return the column 'jordiaagr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getJordiaagr()
		throws SQLException {
			return rs.getBigDecimal("jordiaagr");
		}
		/**
		 * Mínimo Día Artistas Grupo I
		 * @return the column 'mindiaart1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMindiaart1()
		throws SQLException {
			return rs.getBigDecimal("mindiaart1");
		}
		/**
		 * Mínimo Día Artistas Grupo II
		 * @return the column 'mindiaart2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMindiaart2()
		throws SQLException {
			return rs.getBigDecimal("mindiaart2");
		}
		/**
		 * A Cuenta Dia Artistas
		 * @return the column 'acdiaart' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getAcdiaart()
		throws SQLException {
			return rs.getBigDecimal("acdiaart");
		}

	}
	
	
	/**
	 * Lincalcu
	 * 
	 */
	public class Lincalcu {
		
		private ResultSet rs;
		
		private Lincalcu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Anio
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Dia de calculo de I.R.P.F.
		 * @return the column 'dia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDia()
		throws SQLException {
			return rs.getInt("dia");
		}
		/**
		 * Linea de elemento
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Codigo Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Fecha de Inicio del complemento
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Importe Unitario
		 * @return the column 'importe_uni' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_uni()
		throws SQLException {
			return rs.getBigDecimal("importe_uni");
		}
		/**
		 * Meses /Dias
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Lincnae
	 * 
	 */
	public class Lincnae {
		
		private ResultSet rs;
		
		private Lincnae (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Inicio de vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin de vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * % I.T.
		 * @return the column 'pctit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctit()
		throws SQLException {
			return rs.getBigDecimal("pctit");
		}
		/**
		 * % I.M.S.
		 * @return the column 'pctims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctims()
		throws SQLException {
			return rs.getBigDecimal("pctims");
		}
		/**
		 * % Total
		 * @return the column 'pcttotal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPcttotal()
		throws SQLException {
			return rs.getBigDecimal("pcttotal");
		}

	}
	
	
	/**
	 * Lincnae2009
	 * 
	 */
	public class Lincnae2009 {
		
		private ResultSet rs;
		
		private Lincnae2009 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Inicio de vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin de vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * % I.T.
		 * @return the column 'pctit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctit()
		throws SQLException {
			return rs.getBigDecimal("pctit");
		}
		/**
		 * % I.M.S.
		 * @return the column 'pctims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctims()
		throws SQLException {
			return rs.getBigDecimal("pctims");
		}
		/**
		 * % Total
		 * @return the column 'pcttotal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPcttotal()
		throws SQLException {
			return rs.getBigDecimal("pcttotal");
		}

	}
	
	
	/**
	 * Lincomun
	 * 
	 */
	public class Lincomun {
		
		private ResultSet rs;
		
		private Lincomun (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha de Comunicacion
		 * @return the column 'feccom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccom()
		throws SQLException {
			return rs.getDate("feccom");
		}
		/**
		 * Numero de Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Anio de Nacimiento Hijo
		 * @return the column 'anionac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnionac()
		throws SQLException {
			return rs.getInt("anionac");
		}
		/**
		 * Año adopcion/acogida
		 * @return the column 'anioaco' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnioaco()
		throws SQLException {
			return rs.getInt("anioaco");
		}
		/**
		 * Minusvalia Hijo
		 * @return the column 'xminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getXminus()
		throws SQLException {
			return rs.getString("xminus");
		}
		/**
		 * Descendiente o ascendiente
		 * @return the column 'des_asc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDes_asc()
		throws SQLException {
			return rs.getString("des_asc");
		}
		/**
		 * Descendiente por entero
		 * @return the column 'descen_ent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescen_ent()
		throws SQLException {
			return rs.getString("descen_ent");
		}
		/**
		 * Convivencia
		 * @return the column 'conviv' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getConviv()
		throws SQLException {
			return rs.getInt("conviv");
		}

	}
	
	
	/**
	 * Linelem
	 * 
	 */
	public class Linelem {
		
		private ResultSet rs;
		
		private Linelem (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Elemento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Dato Principal
		 * @return the column 'dato1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDato1()
		throws SQLException {
			return rs.getBigDecimal("dato1");
		}
		/**
		 * Dato Secundario
		 * @return the column 'dato2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDato2()
		throws SQLException {
			return rs.getBigDecimal("dato2");
		}

	}
	
	
	/**
	 * Linepigr
	 * 
	 */
	public class Linepigr {
		
		private ResultSet rs;
		
		private Linepigr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Epigrafe
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Indicador I.T. (% - Fijo )
		 * @return the column 'indit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndit()
		throws SQLException {
			return rs.getString("indit");
		}
		/**
		 * % o Importe I.T.
		 * @return the column 'canit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCanit()
		throws SQLException {
			return rs.getBigDecimal("canit");
		}
		/**
		 * Indicador I.M.S. (% - Fijo )
		 * @return the column 'indipm' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndipm()
		throws SQLException {
			return rs.getString("indipm");
		}
		/**
		 * % o Importe I.M.S.
		 * @return the column 'canipm' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCanipm()
		throws SQLException {
			return rs.getBigDecimal("canipm");
		}

	}
	
	
	/**
	 * Linirpf
	 * 
	 */
	public class Linirpf {
		
		private ResultSet rs;
		
		private Linirpf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Elemento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Hasta importe o superior
		 * @return the column 'hasta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta()
		throws SQLException {
			return rs.getBigDecimal("hasta");
		}
		/**
		 * Importe del Elemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Linmutua
	 * 
	 */
	public class Linmutua {
		
		private ResultSet rs;
		
		private Linmutua (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Comisión Accidente de Trabajo
		 * @return the column 'prcacctrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcacctrab()
		throws SQLException {
			return rs.getBigDecimal("prcacctrab");
		}
		/**
		 * Comisión IT
		 * @return the column 'prcit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcit()
		throws SQLException {
			return rs.getBigDecimal("prcit");
		}

	}
	
	
	/**
	 * Linocupacion
	 * 
	 */
	public class Linocupacion {
		
		private ResultSet rs;
		
		private Linocupacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Inicio de vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin de vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * % I.T.
		 * @return the column 'pctit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctit()
		throws SQLException {
			return rs.getBigDecimal("pctit");
		}
		/**
		 * % I.M.S.
		 * @return the column 'pctims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctims()
		throws SQLException {
			return rs.getBigDecimal("pctims");
		}
		/**
		 * % Total
		 * @return the column 'pcttotal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPcttotal()
		throws SQLException {
			return rs.getBigDecimal("pcttotal");
		}

	}
	
	
	/**
	 * Linpercepcion
	 * 
	 */
	public class Linpercepcion {
		
		private ResultSet rs;
		
		private Linpercepcion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Percepción
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecinicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicio()
		throws SQLException {
			return rs.getDate("fecinicio");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Salario Base
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Complemento Nocturnidad
		 * @return the column 'nocturno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNocturno()
		throws SQLException {
			return rs.getBigDecimal("nocturno");
		}
		/**
		 * Complemento Empresa
		 * @return the column 'empresa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getEmpresa()
		throws SQLException {
			return rs.getBigDecimal("empresa");
		}

	}
	
	
	/**
	 * Linplus
	 * 
	 */
	public class Linplus {
		
		private ResultSet rs;
		
		private Linplus (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Plus
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inivio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Importe Plus
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Código Complemento Equivalente
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}

	}
	
	
	/**
	 * Linporco
	 * 
	 */
	public class Linporco {
		
		private ResultSet rs;
		
		private Linporco (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Porcentaje
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * % Empresa
		 * @return the column 'pctemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPctemp()
		throws SQLException {
			return rs.getBigDecimal("pctemp");
		}
		/**
		 * % Trabajador
		 * @return the column 'pcttra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPcttra()
		throws SQLException {
			return rs.getBigDecimal("pcttra");
		}
		/**
		 * % Total
		 * @return the column 'pcttot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPcttot()
		throws SQLException {
			return rs.getBigDecimal("pcttot");
		}

	}
	
	
	/**
	 * Linprestacion
	 * 
	 */
	public class Linprestacion {
		
		private ResultSet rs;
		
		private Linprestacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Domicilio
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Prestación J.T.
		 * @return the column 'jefe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getJefe()
		throws SQLException {
			return rs.getBigDecimal("jefe");
		}
		/**
		 * Prestación 2º J.T.
		 * @return the column 'jefe2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getJefe2()
		throws SQLException {
			return rs.getBigDecimal("jefe2");
		}
		/**
		 * Prestación Cajero
		 * @return the column 'cajero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCajero()
		throws SQLException {
			return rs.getBigDecimal("cajero");
		}

	}
	
	
	/**
	 * Lintc2
	 * 
	 */
	public class Lintc2 {
		
		private ResultSet rs;
		
		private Lintc2 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de TC2
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Codigo de Persona
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Tipo contrato TC2
		 * @return the column 'codtc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodtc2()
		throws SQLException {
			return rs.getString("codtc2");
		}
		/**
		 * Epigrafe
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * Numero Dias Horas
		 * @return the column 'numdh' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumdh()
		throws SQLException {
			return rs.getInt("numdh");
		}
		/**
		 * Clave
		 * @return the column 'clave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getClave()
		throws SQLException {
			return rs.getString("clave");
		}
		/**
		 * Importe de la Base
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * Situaciones Especiales
		 * @return the column 'sitesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSitesp()
		throws SQLException {
			return rs.getString("sitesp");
		}
		/**
		 * Dias Deduccion / Compensacion
		 * @return the column 'dc_dias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDc_dias()
		throws SQLException {
			return rs.getInt("dc_dias");
		}
		/**
		 * Clave Deduccion / Compensacion
		 * @return the column 'dc_clave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc_clave()
		throws SQLException {
			return rs.getString("dc_clave");
		}
		/**
		 * Importe Deduccion / Compensacion
		 * @return the column 'dc_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDc_importe()
		throws SQLException {
			return rs.getBigDecimal("dc_importe");
		}
		/**
		 * Fecha Deduccion / Compensacion
		 * @return the column 'dc_fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getDc_fecha()
		throws SQLException {
			return rs.getDate("dc_fecha");
		}
		/**
		 * Indicador de Documento
		 * @return the column 'inddoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddoc()
		throws SQLException {
			return rs.getString("inddoc");
		}
		/**
		 * Numero de Documento
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Alias TC2
		 * @return the column 'aliastc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAliastc2()
		throws SQLException {
			return rs.getString("aliastc2");
		}
		/**
		 * Numero Afiliacion S.S.
		 * @return the column 'numss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumss()
		throws SQLException {
			return rs.getString("numss");
		}

	}
	
	
	/**
	 * Lintc2epi
	 * 
	 */
	public class Lintc2epi {
		
		private ResultSet rs;
		
		private Lintc2epi (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de TC2
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Epigrafe
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Porcentaje I.T.
		 * @return the column 'prcit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcit()
		throws SQLException {
			return rs.getBigDecimal("prcit");
		}
		/**
		 * Cuota I.T.
		 * @return the column 'cuotait' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuotait()
		throws SQLException {
			return rs.getBigDecimal("cuotait");
		}
		/**
		 * Porcentaje I.M.S.
		 * @return the column 'prcims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcims()
		throws SQLException {
			return rs.getBigDecimal("prcims");
		}
		/**
		 * Cuota I.M.S.
		 * @return the column 'cuotaims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuotaims()
		throws SQLException {
			return rs.getBigDecimal("cuotaims");
		}

	}
	
	
	/**
	 * Linvariables
	 * 
	 */
	public class Linvariables {
		
		private ResultSet rs;
		
		private Linvariables (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Categoria Laboral
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecinicio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicio()
		throws SQLException {
			return rs.getDate("fecinicio");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Importe Nocturnidad
		 * @return the column 'nocturnidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNocturnidad()
		throws SQLException {
			return rs.getBigDecimal("nocturnidad");
		}
		/**
		 * Importe Plus Transporte
		 * @return the column 'transporte' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTransporte()
		throws SQLException {
			return rs.getBigDecimal("transporte");
		}
		/**
		 * Importe Festivos
		 * @return the column 'festivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getFestivo()
		throws SQLException {
			return rs.getBigDecimal("festivo");
		}
		/**
		 * Importe Festivos Especiales
		 * @return the column 'festivoesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getFestivoesp()
		throws SQLException {
			return rs.getBigDecimal("festivoesp");
		}
		/**
		 * Importe Domingos
		 * @return the column 'domingo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDomingo()
		throws SQLException {
			return rs.getBigDecimal("domingo");
		}
		/**
		 * % Horas Extras Diurnas
		 * @return the column 'diurna' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDiurna()
		throws SQLException {
			return rs.getBigDecimal("diurna");
		}
		/**
		 * % Horas Extras Nocturnas
		 * @return the column 'nocturna' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNocturna()
		throws SQLException {
			return rs.getBigDecimal("nocturna");
		}
		/**
		 * % Horas Extras Festivas Diurnas
		 * @return the column 'festdiurna' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getFestdiurna()
		throws SQLException {
			return rs.getBigDecimal("festdiurna");
		}
		/**
		 * % Horas Extras Festivas Nocturnas
		 * @return the column 'festnocturna' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getFestnocturna()
		throws SQLException {
			return rs.getBigDecimal("festnocturna");
		}

	}
	
	
	/**
	 * Masivo
	 * 
	 */
	public class Masivo {
		
		private ResultSet rs;
		
		private Masivo (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'cdg1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg1()
		throws SQLException {
			return rs.getString("cdg1");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg2()
		throws SQLException {
			return rs.getString("cdg2");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg3()
		throws SQLException {
			return rs.getString("cdg3");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg4()
		throws SQLException {
			return rs.getString("cdg4");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg5()
		throws SQLException {
			return rs.getString("cdg5");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg6()
		throws SQLException {
			return rs.getString("cdg6");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg7()
		throws SQLException {
			return rs.getString("cdg7");
		}
		/**
		 * $column.remarks
		 * @return the column 'cdg8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg8()
		throws SQLException {
			return rs.getString("cdg8");
		}
		/**
		 * $column.remarks
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * $column.remarks
		 * @return the column 'dato' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDato()
		throws SQLException {
			return rs.getString("dato");
		}

	}
	
	
	/**
	 * Minor_01
	 * 
	 */
	public class Minor_01 {
		
		private ResultSet rs;
		
		private Minor_01 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Minoracion por Minusvalia
		 * @return the column 'min_minus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_minus()
		throws SQLException {
			return rs.getBigDecimal("min_minus");
		}
		/**
		 * Minoracion por Minusvalia con Ayuda
		 * @return the column 'min_ayuda' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_ayuda()
		throws SQLException {
			return rs.getBigDecimal("min_ayuda");
		}
		/**
		 * Minoracion por Gran Minusvalia
		 * @return the column 'min_granminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_granminus()
		throws SQLException {
			return rs.getBigDecimal("min_granminus");
		}

	}
	
	
	/**
	 * Minor_20
	 * 
	 */
	public class Minor_20 {
		
		private ResultSet rs;
		
		private Minor_20 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Minoracion por Minusvalia
		 * @return the column 'min_minus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_minus()
		throws SQLException {
			return rs.getBigDecimal("min_minus");
		}
		/**
		 * Minoracion por Minusvalia con Ayuda
		 * @return the column 'min_ayuda' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_ayuda()
		throws SQLException {
			return rs.getBigDecimal("min_ayuda");
		}
		/**
		 * Minoracion por Gran Minusvalia
		 * @return the column 'min_granminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_granminus()
		throws SQLException {
			return rs.getBigDecimal("min_granminus");
		}

	}
	
	
	/**
	 * Minor_31
	 * 
	 */
	public class Minor_31 {
		
		private ResultSet rs;
		
		private Minor_31 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Minoracion por Minusvalia
		 * @return the column 'min_minus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_minus()
		throws SQLException {
			return rs.getBigDecimal("min_minus");
		}
		/**
		 * Minoracion por Gran Minusvalia
		 * @return the column 'min_granminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_granminus()
		throws SQLException {
			return rs.getBigDecimal("min_granminus");
		}

	}
	
	
	/**
	 * Minor_48
	 * 
	 */
	public class Minor_48 {
		
		private ResultSet rs;
		
		private Minor_48 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Tramo
		 * @return the column 'num_tramo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNum_tramo()
		throws SQLException {
			return rs.getInt("num_tramo");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Desde Importe
		 * @return the column 'desde_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getDesde_importe()
		throws SQLException {
			return rs.getBigDecimal("desde_importe");
		}
		/**
		 * Hasta Importe
		 * @return the column 'hasta_importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHasta_importe()
		throws SQLException {
			return rs.getBigDecimal("hasta_importe");
		}
		/**
		 * Minoracion por Minusvalia
		 * @return the column 'min_minus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_minus()
		throws SQLException {
			return rs.getBigDecimal("min_minus");
		}
		/**
		 * Minoracion por Minusvalia con Ayuda
		 * @return the column 'min_ayuda' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_ayuda()
		throws SQLException {
			return rs.getBigDecimal("min_ayuda");
		}
		/**
		 * Minoracion por Gran Minusvalia
		 * @return the column 'min_granminus' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMin_granminus()
		throws SQLException {
			return rs.getBigDecimal("min_granminus");
		}

	}
	
	
	/**
	 * Minora
	 * 
	 */
	public class Minora {
		
		private ResultSet rs;
		
		private Minora (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Orden de Minoración
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}

	}
	
	
	/**
	 * Mutua
	 * 
	 */
	public class Mutua {
		
		private ResultSet rs;
		
		private Mutua (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Mutua
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Mutua
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitEmprccc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprccc WHERE" 
					+ " mutuaccc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprccc emprccc = new Emprccc(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprccc(emprccc, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitLinmutua(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linmutua WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linmutua linmutua = new Linmutua(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinmutua(linmutua, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " mutua = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nacion
	 * 
	 */
	public class Nacion {
		
		private ResultSet rs;
		
		private Nacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nacionalidad
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Nacionalidad
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM persona WHERE" 
					+ " nacion = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Persona persona = new Persona(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPersona(persona, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nivel
	 * 
	 */
	public class Nivel {
		
		private ResultSet rs;
		
		private Nivel (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Codigo de Nivel Retributivo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}

		public void visitPercniv(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM percniv WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " nivel = ?  " 				);
				stmt.setString(1, this.getCodcon() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Percniv percniv = new Percniv(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPercniv(percniv, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nomdfdev
	 * 
	 */
	public class Nomdfdev {
		
		private ResultSet rs;
		
		private Nomdfdev (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Codigo Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Unidades Complemento
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe Unitario
		 * @return the column 'impuni' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpuni()
		throws SQLException {
			return rs.getBigDecimal("impuni");
		}
		/**
		 * Importe Complemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Indicador de Complemento
		 * @return the column 'indcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcom()
		throws SQLException {
			return rs.getString("indcom");
		}

	}
	
	
	/**
	 * Nomdfdto
	 * 
	 */
	public class Nomdfdto {
		
		private ResultSet rs;
		
		private Nomdfdto (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Concepto de Minoracion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Importe Minoracion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Nomdfdtoex
	 * 
	 */
	public class Nomdfdtoex {
		
		private ResultSet rs;
		
		private Nomdfdtoex (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Paga
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Concepto de Minoracion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Importe Minoracion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Nomdto
	 * 
	 */
	public class Nomdto {
		
		private ResultSet rs;
		
		private Nomdto (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Concepto de Minoracion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Importe Minoracion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Nomdtoex
	 * 
	 */
	public class Nomdtoex {
		
		private ResultSet rs;
		
		private Nomdtoex (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Paga
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Numero de Orden
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Concepto de Minoracion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Importe Minoracion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Nomina
	 * 
	 */
	public class Nomina {
		
		private ResultSet rs;
		
		private Nomina (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Mes de Nomina
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Anio de Nomina
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Orden dentro del Mes - Anio
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Tipo de Nomina
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Nombre de Actividad
		 * @return the column 'nomemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomemp()
		throws SQLException {
			return rs.getString("nomemp");
		}
		/**
		 * Fecha de Emision
		 * @return the column 'fecemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecemi()
		throws SQLException {
			return rs.getDate("fecemi");
		}
		/**
		 * Nombre de Trabajador
		 * @return the column 'nomper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomper()
		throws SQLException {
			return rs.getString("nomper");
		}
		/**
		 * Datos Direccion
		 * @return the column 'direccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDireccion()
		throws SQLException {
			return rs.getString("direccion");
		}
		/**
		 * Datos Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Descripcion Categoria
		 * @return the column 'descat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescat()
		throws SQLException {
			return rs.getString("descat");
		}
		/**
		 * Profesion
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Numero de Matricula
		 * @return the column 'nummat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNummat()
		throws SQLException {
			return rs.getInt("nummat");
		}
		/**
		 * Fecha de Antiguedad
		 * @return the column 'fecant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecant()
		throws SQLException {
			return rs.getDate("fecant");
		}
		/**
		 * Inicio Periodo Nomina
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Periodo Nomina
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Numero de Dias Periodo Nomina
		 * @return the column 'diasnomina' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasnomina()
		throws SQLException {
			return rs.getInt("diasnomina");
		}
		/**
		 * Total Devengos
		 * @return the column 'total_devengos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_devengos()
		throws SQLException {
			return rs.getBigDecimal("total_devengos");
		}
		/**
		 * Total a Deducir
		 * @return the column 'total_deducir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_deducir()
		throws SQLException {
			return rs.getBigDecimal("total_deducir");
		}
		/**
		 * Total Liquido
		 * @return the column 'total_liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_liquido()
		throws SQLException {
			return rs.getBigDecimal("total_liquido");
		}
		/**
		 * Fecha de Cobro
		 * @return the column 'feccob' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccob()
		throws SQLException {
			return rs.getDate("feccob");
		}
		/**
		 * Contingencias Comunes
		 * @return the column 'base_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_concom()
		throws SQLException {
			return rs.getBigDecimal("base_concom");
		}
		/**
		 * Accidentes Trabajo
		 * @return the column 'base_acctra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acctra()
		throws SQLException {
			return rs.getBigDecimal("base_acctra");
		}
		/**
		 * Prorrata Pagas Extras
		 * @return the column 'base_proext' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_proext()
		throws SQLException {
			return rs.getBigDecimal("base_proext");
		}
		/**
		 * Contingencias Comunes IT
		 * @return the column 'base_con_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_it()
		throws SQLException {
			return rs.getBigDecimal("base_con_it");
		}
		/**
		 * Accidentes Trabajo IT
		 * @return the column 'base_acc_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_it()
		throws SQLException {
			return rs.getBigDecimal("base_acc_it");
		}
		/**
		 * Contingencias Comunes Maternidad
		 * @return the column 'base_con_mat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_mat()
		throws SQLException {
			return rs.getBigDecimal("base_con_mat");
		}
		/**
		 * Accidentes Trabajo Maternidad
		 * @return the column 'base_acc_mat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_mat()
		throws SQLException {
			return rs.getBigDecimal("base_acc_mat");
		}
		/**
		 * Contingencias Comunes Maternidad No Aporta
		 * @return the column 'base_con_mat_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_mat_no()
		throws SQLException {
			return rs.getBigDecimal("base_con_mat_no");
		}
		/**
		 * Accidentes Trabajo no Aporta
		 * @return the column 'base_acc_mat_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_mat_no()
		throws SQLException {
			return rs.getBigDecimal("base_acc_mat_no");
		}
		/**
		 * Fondo Garantia Salarial
		 * @return the column 'base_fogasa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_fogasa()
		throws SQLException {
			return rs.getBigDecimal("base_fogasa");
		}
		/**
		 * Formacion Profesional
		 * @return the column 'base_fp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_fp()
		throws SQLException {
			return rs.getBigDecimal("base_fp");
		}
		/**
		 * Desempleo
		 * @return the column 'base_desempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_desempleo()
		throws SQLException {
			return rs.getBigDecimal("base_desempleo");
		}
		/**
		 * Horas Estras Estructurales
		 * @return the column 'base_hextras' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hextras()
		throws SQLException {
			return rs.getBigDecimal("base_hextras");
		}
		/**
		 * Horas Extras No Extructurales
		 * @return the column 'base_hextras_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hextras_no()
		throws SQLException {
			return rs.getBigDecimal("base_hextras_no");
		}
		/**
		 * Exceso Extrasalariales
		 * @return the column 'base_exceso' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_exceso()
		throws SQLException {
			return rs.getBigDecimal("base_exceso");
		}
		/**
		 * No cotiza a S.S.
		 * @return the column 'base_nocotiza' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_nocotiza()
		throws SQLException {
			return rs.getBigDecimal("base_nocotiza");
		}
		/**
		 * Base en Especie
		 * @return the column 'base_especie' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_especie()
		throws SQLException {
			return rs.getBigDecimal("base_especie");
		}
		/**
		 * Base IRPF
		 * @return the column 'base_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf()
		throws SQLException {
			return rs.getBigDecimal("base_irpf");
		}
		/**
		 * IRPF en Especie
		 * @return the column 'base_irpf_especie' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_especie()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_especie");
		}
		/**
		 * IRPF no Cotiza
		 * @return the column 'base_irpf_nocotiza' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_nocotiza()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_nocotiza");
		}
		/**
		 * Horas Complementarias
		 * @return the column 'base_horascom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_horascom()
		throws SQLException {
			return rs.getBigDecimal("base_horascom");
		}
		/**
		 * Percepcion por Desempleo
		 * @return the column 'base_perdes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_perdes()
		throws SQLException {
			return rs.getBigDecimal("base_perdes");
		}
		/**
		 * Remuneracion
		 * @return the column 'remuneracion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRemuneracion()
		throws SQLException {
			return rs.getBigDecimal("remuneracion");
		}
		/**
		 * Base IT
		 * @return the column 'base_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_it()
		throws SQLException {
			return rs.getBigDecimal("base_it");
		}
		/**
		 * Total 1
		 * @return the column 'total_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_1()
		throws SQLException {
			return rs.getBigDecimal("total_1");
		}
		/**
		 * Grupo de Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Contingencias Generales
		 * @return the column 'base_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_cg()
		throws SQLException {
			return rs.getBigDecimal("base_cg");
		}
		/**
		 * Accidentes Trabajo - Enfermedad Profesional
		 * @return the column 'base_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc()
		throws SQLException {
			return rs.getBigDecimal("base_acc");
		}
		/**
		 * Porcentaje Contingencias Generales
		 * @return the column 'prc_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_cg()
		throws SQLException {
			return rs.getBigDecimal("prc_cg");
		}
		/**
		 * Porcentaje Accidentes
		 * @return the column 'prc_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_acc()
		throws SQLException {
			return rs.getBigDecimal("prc_acc");
		}
		/**
		 * Porcentaje Horas Extras Estructurales
		 * @return the column 'prc_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hex()
		throws SQLException {
			return rs.getBigDecimal("prc_hex");
		}
		/**
		 * Porcentaje Horas Extras NO Estructurales
		 * @return the column 'prc_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hexno()
		throws SQLException {
			return rs.getBigDecimal("prc_hexno");
		}
		/**
		 * Importe Contingencias Comunes
		 * @return the column 'importe_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cg()
		throws SQLException {
			return rs.getBigDecimal("importe_cg");
		}
		/**
		 * Importe Accidentes Trabajo
		 * @return the column 'importe_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_acc()
		throws SQLException {
			return rs.getBigDecimal("importe_acc");
		}
		/**
		 * Importe Horas Extras Estructurales
		 * @return the column 'importe_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_hex()
		throws SQLException {
			return rs.getBigDecimal("importe_hex");
		}
		/**
		 * Importe Horas Extras NO Estructurales
		 * @return the column 'importe_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_hexno()
		throws SQLException {
			return rs.getBigDecimal("importe_hexno");
		}
		/**
		 * Tope Minimo para C.G.
		 * @return the column 'mincg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMincg()
		throws SQLException {
			return rs.getBigDecimal("mincg");
		}
		/**
		 * Tope Maximo para C.G.
		 * @return the column 'maxcg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxcg()
		throws SQLException {
			return rs.getBigDecimal("maxcg");
		}
		/**
		 * Tope Minimo para Accidentes
		 * @return the column 'minacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMinacc()
		throws SQLException {
			return rs.getBigDecimal("minacc");
		}
		/**
		 * Tope Maximo para Accidentes
		 * @return the column 'maxacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxacc()
		throws SQLException {
			return rs.getBigDecimal("maxacc");
		}
		/**
		 * Cuota Total de la Emrpesa
		 * @return the column 'cuota_empresa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_empresa()
		throws SQLException {
			return rs.getBigDecimal("cuota_empresa");
		}
		/**
		 * Accidentes Trabajo Sin Horas Extras
		 * @return the column 'base_acc_sin_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_sin_hex()
		throws SQLException {
			return rs.getBigDecimal("base_acc_sin_hex");
		}
		/**
		 * Importe Cuotas Deducciones
		 * @return the column 'importe_cuotas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cuotas()
		throws SQLException {
			return rs.getBigDecimal("importe_cuotas");
		}
		/**
		 * Porcentaje IRPF
		 * @return the column 'prc_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_irpf()
		throws SQLException {
			return rs.getBigDecimal("prc_irpf");
		}
		/**
		 * Importe IRPF
		 * @return the column 'importe_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Dias Trabajados
		 * @return the column 'diastrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiastrab()
		throws SQLException {
			return rs.getInt("diastrab");
		}
		/**
		 * Dias Efectivos
		 * @return the column 'diasefec' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasefec()
		throws SQLException {
			return rs.getInt("diasefec");
		}
		/**
		 * Base Calculo Antiguedad
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Prorrateo Retribucion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'procot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcot()
		throws SQLException {
			return rs.getString("procot");
		}
		/**
		 * Codigo Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Asimilado a % Cotizacion
		 * @return the column 'codpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpct()
		throws SQLException {
			return rs.getString("codpct");
		}
		/**
		 * Fecha Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Tipo de divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Base Imponible IRPF de ejercicios anteriores
		 * @return the column 'base_irpf_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_ant()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_ant");
		}
		/**
		 * Importe de IRPF de ejercicios anteriores
		 * @return the column 'importe_irpf_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf_ant()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf_ant");
		}
		/**
		 * Importe de cuotas S.S. de ejercicios anteriores
		 * @return the column 'importe_cuotas_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cuotas_ant()
		throws SQLException {
			return rs.getBigDecimal("importe_cuotas_ant");
		}
		/**
		 * Base de Contingencias Generales en Pesetas
		 * @return the column 'base_cg_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_cg_pts()
		throws SQLException {
			return rs.getBigDecimal("base_cg_pts");
		}
		/**
		 * Base de Accidentes de Trabajo en Pesetas
		 * @return the column 'base_acc_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_pts()
		throws SQLException {
			return rs.getBigDecimal("base_acc_pts");
		}
		/**
		 * Base de Accidentes de Trabajo sin Horas Extras en Pesetas
		 * @return the column 'base_acc_sin_h_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_sin_h_pts()
		throws SQLException {
			return rs.getBigDecimal("base_acc_sin_h_pts");
		}

		public void visitNomdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomdto WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nomdto nomdto = new Nomdto(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomdto(nomdto, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNominadev(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nominadev WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nominadev nominadev = new Nominadev(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNominadev(nominadev, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPrcdivnom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM prcdivnom WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Prcdivnom prcdivnom = new Prcdivnom(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPrcdivnom(prcdivnom, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nominadev
	 * 
	 */
	public class Nominadev {
		
		private ResultSet rs;
		
		private Nominadev (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Codigo Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Unidades Complemento
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe Unitario
		 * @return the column 'impuni' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpuni()
		throws SQLException {
			return rs.getBigDecimal("impuni");
		}
		/**
		 * Importe Complemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Indicador de Complemento
		 * @return the column 'indcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcom()
		throws SQLException {
			return rs.getString("indcom");
		}
		/**
		 * Tipo de complemento
		 * @return the column 'tipcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcom()
		throws SQLException {
			return rs.getString("tipcom");
		}
		/**
		 * Retribucion dineraria o en especie
		 * @return the column 'dinesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDinesp()
		throws SQLException {
			return rs.getString("dinesp");
		}

	}
	
	
	/**
	 * Nominadf
	 * 
	 */
	public class Nominadf {
		
		private ResultSet rs;
		
		private Nominadf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina de Diferencias
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Mes de Nomina
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Anio de Nomina
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Orden dentro del Mes - Anio
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Tipo de Nomina
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Nombre de Actividad
		 * @return the column 'nomemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomemp()
		throws SQLException {
			return rs.getString("nomemp");
		}
		/**
		 * Fecha de Emision
		 * @return the column 'fecemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecemi()
		throws SQLException {
			return rs.getDate("fecemi");
		}
		/**
		 * Nombre de Trabajador
		 * @return the column 'nomper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomper()
		throws SQLException {
			return rs.getString("nomper");
		}
		/**
		 * Datos Direccion
		 * @return the column 'direccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDireccion()
		throws SQLException {
			return rs.getString("direccion");
		}
		/**
		 * Datos Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Descripcion Categoria
		 * @return the column 'descat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescat()
		throws SQLException {
			return rs.getString("descat");
		}
		/**
		 * Profesion
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Numero de Matricula
		 * @return the column 'nummat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNummat()
		throws SQLException {
			return rs.getInt("nummat");
		}
		/**
		 * Fecha de Antiguedad
		 * @return the column 'fecant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecant()
		throws SQLException {
			return rs.getDate("fecant");
		}
		/**
		 * Inicio Periodo Nomina
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Periodo Nomina
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Numero de Dias Periodo Nomina
		 * @return the column 'diasnomina' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasnomina()
		throws SQLException {
			return rs.getInt("diasnomina");
		}
		/**
		 * Total Devengos
		 * @return the column 'total_devengos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_devengos()
		throws SQLException {
			return rs.getBigDecimal("total_devengos");
		}
		/**
		 * Total a Deducir
		 * @return the column 'total_deducir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_deducir()
		throws SQLException {
			return rs.getBigDecimal("total_deducir");
		}
		/**
		 * Total Liquido
		 * @return the column 'total_liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_liquido()
		throws SQLException {
			return rs.getBigDecimal("total_liquido");
		}
		/**
		 * Fecha de Cobro
		 * @return the column 'feccob' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccob()
		throws SQLException {
			return rs.getDate("feccob");
		}
		/**
		 * Contingencias Comunes
		 * @return the column 'base_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_concom()
		throws SQLException {
			return rs.getBigDecimal("base_concom");
		}
		/**
		 * Accidentes Trabajo
		 * @return the column 'base_acctra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acctra()
		throws SQLException {
			return rs.getBigDecimal("base_acctra");
		}
		/**
		 * Prorrata Pagas Extras
		 * @return the column 'base_proext' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_proext()
		throws SQLException {
			return rs.getBigDecimal("base_proext");
		}
		/**
		 * Contingencias Comunes IT
		 * @return the column 'base_con_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_it()
		throws SQLException {
			return rs.getBigDecimal("base_con_it");
		}
		/**
		 * Accidentes Trabajo IT
		 * @return the column 'base_acc_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_it()
		throws SQLException {
			return rs.getBigDecimal("base_acc_it");
		}
		/**
		 * Contingencias Comunes Maternidad
		 * @return the column 'base_con_mat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_mat()
		throws SQLException {
			return rs.getBigDecimal("base_con_mat");
		}
		/**
		 * Accidentes Trabajo Maternidad
		 * @return the column 'base_acc_mat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_mat()
		throws SQLException {
			return rs.getBigDecimal("base_acc_mat");
		}
		/**
		 * Contingencias Comunes Maternidad No Aporta
		 * @return the column 'base_con_mat_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_con_mat_no()
		throws SQLException {
			return rs.getBigDecimal("base_con_mat_no");
		}
		/**
		 * Accidentes Trabajo no Aporta
		 * @return the column 'base_acc_mat_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_mat_no()
		throws SQLException {
			return rs.getBigDecimal("base_acc_mat_no");
		}
		/**
		 * Fondo Garantia Salarial
		 * @return the column 'base_fogasa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_fogasa()
		throws SQLException {
			return rs.getBigDecimal("base_fogasa");
		}
		/**
		 * Formacion Profesional
		 * @return the column 'base_fp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_fp()
		throws SQLException {
			return rs.getBigDecimal("base_fp");
		}
		/**
		 * Desempleo
		 * @return the column 'base_desempleo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_desempleo()
		throws SQLException {
			return rs.getBigDecimal("base_desempleo");
		}
		/**
		 * Horas Estras Estructurales
		 * @return the column 'base_hextras' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hextras()
		throws SQLException {
			return rs.getBigDecimal("base_hextras");
		}
		/**
		 * Horas Extras No Extructurales
		 * @return the column 'base_hextras_no' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hextras_no()
		throws SQLException {
			return rs.getBigDecimal("base_hextras_no");
		}
		/**
		 * Exceso Extrasalariales
		 * @return the column 'base_exceso' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_exceso()
		throws SQLException {
			return rs.getBigDecimal("base_exceso");
		}
		/**
		 * No cotiza a S.S.
		 * @return the column 'base_nocotiza' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_nocotiza()
		throws SQLException {
			return rs.getBigDecimal("base_nocotiza");
		}
		/**
		 * Base en Especie
		 * @return the column 'base_especie' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_especie()
		throws SQLException {
			return rs.getBigDecimal("base_especie");
		}
		/**
		 * Base IRPF
		 * @return the column 'base_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf()
		throws SQLException {
			return rs.getBigDecimal("base_irpf");
		}
		/**
		 * IRPF en Especie
		 * @return the column 'base_irpf_especie' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_especie()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_especie");
		}
		/**
		 * IRPF no Cotiza
		 * @return the column 'base_irpf_nocotiza' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_nocotiza()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_nocotiza");
		}
		/**
		 * Horas Complementarias
		 * @return the column 'base_horascom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_horascom()
		throws SQLException {
			return rs.getBigDecimal("base_horascom");
		}
		/**
		 * Percepcion por Desempleo
		 * @return the column 'base_perdes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_perdes()
		throws SQLException {
			return rs.getBigDecimal("base_perdes");
		}
		/**
		 * Remuneracion
		 * @return the column 'remuneracion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRemuneracion()
		throws SQLException {
			return rs.getBigDecimal("remuneracion");
		}
		/**
		 * Base IT
		 * @return the column 'base_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_it()
		throws SQLException {
			return rs.getBigDecimal("base_it");
		}
		/**
		 * Total 1
		 * @return the column 'total_1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_1()
		throws SQLException {
			return rs.getBigDecimal("total_1");
		}
		/**
		 * Grupo de Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Contingencias Generales
		 * @return the column 'base_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_cg()
		throws SQLException {
			return rs.getBigDecimal("base_cg");
		}
		/**
		 * Accidentes Trabajo - Enfermedad Profesional
		 * @return the column 'base_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc()
		throws SQLException {
			return rs.getBigDecimal("base_acc");
		}
		/**
		 * Porcentaje Contingencias Generales
		 * @return the column 'prc_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_cg()
		throws SQLException {
			return rs.getBigDecimal("prc_cg");
		}
		/**
		 * Porcentaje Accidentes
		 * @return the column 'prc_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_acc()
		throws SQLException {
			return rs.getBigDecimal("prc_acc");
		}
		/**
		 * Porcentaje Horas Extras Estructurales
		 * @return the column 'prc_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hex()
		throws SQLException {
			return rs.getBigDecimal("prc_hex");
		}
		/**
		 * Porcentaje Horas Extras NO Estructurales
		 * @return the column 'prc_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hexno()
		throws SQLException {
			return rs.getBigDecimal("prc_hexno");
		}
		/**
		 * Importe Contingencias Comunes
		 * @return the column 'importe_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cg()
		throws SQLException {
			return rs.getBigDecimal("importe_cg");
		}
		/**
		 * Importe Accidentes Trabajo
		 * @return the column 'importe_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_acc()
		throws SQLException {
			return rs.getBigDecimal("importe_acc");
		}
		/**
		 * Importe Horas Extras Estructurales
		 * @return the column 'importe_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_hex()
		throws SQLException {
			return rs.getBigDecimal("importe_hex");
		}
		/**
		 * Importe Horas Extras NO Estructurales
		 * @return the column 'importe_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_hexno()
		throws SQLException {
			return rs.getBigDecimal("importe_hexno");
		}
		/**
		 * Tope Minimo para C.G.
		 * @return the column 'mincg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMincg()
		throws SQLException {
			return rs.getBigDecimal("mincg");
		}
		/**
		 * Tope Maximo para C.G.
		 * @return the column 'maxcg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxcg()
		throws SQLException {
			return rs.getBigDecimal("maxcg");
		}
		/**
		 * Tope Minimo para Accidentes
		 * @return the column 'minacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMinacc()
		throws SQLException {
			return rs.getBigDecimal("minacc");
		}
		/**
		 * Tope Maximo para Accidentes
		 * @return the column 'maxacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getMaxacc()
		throws SQLException {
			return rs.getBigDecimal("maxacc");
		}
		/**
		 * Cuota Total de la Emrpesa
		 * @return the column 'cuota_empresa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_empresa()
		throws SQLException {
			return rs.getBigDecimal("cuota_empresa");
		}
		/**
		 * Accidentes Trabajo Sin Horas Extras
		 * @return the column 'base_acc_sin_hex' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_sin_hex()
		throws SQLException {
			return rs.getBigDecimal("base_acc_sin_hex");
		}
		/**
		 * Importe Cuotas Deducciones
		 * @return the column 'importe_cuotas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cuotas()
		throws SQLException {
			return rs.getBigDecimal("importe_cuotas");
		}
		/**
		 * Porcentaje IRPF
		 * @return the column 'prc_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_irpf()
		throws SQLException {
			return rs.getBigDecimal("prc_irpf");
		}
		/**
		 * Importe IRPF
		 * @return the column 'importe_irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Dias Trabajados
		 * @return the column 'diastrab' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiastrab()
		throws SQLException {
			return rs.getInt("diastrab");
		}
		/**
		 * Dias Efectivos
		 * @return the column 'diasefec' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasefec()
		throws SQLException {
			return rs.getInt("diasefec");
		}
		/**
		 * Base Calculo Antiguedad
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Prorrateo Retribucion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'procot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcot()
		throws SQLException {
			return rs.getString("procot");
		}
		/**
		 * Codigo Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Asimilado a % Cotizacion
		 * @return the column 'codpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpct()
		throws SQLException {
			return rs.getString("codpct");
		}
		/**
		 * Fecha Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Tipo de divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Base Imponible IRPF de ejercicios anteriores
		 * @return the column 'base_irpf_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_irpf_ant()
		throws SQLException {
			return rs.getBigDecimal("base_irpf_ant");
		}
		/**
		 * Importe de IRPF de ejercicios anteriores
		 * @return the column 'importe_irpf_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_irpf_ant()
		throws SQLException {
			return rs.getBigDecimal("importe_irpf_ant");
		}
		/**
		 * Importe de cuotas S.S. de ejercicios anteriores
		 * @return the column 'importe_cuotas_ant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_cuotas_ant()
		throws SQLException {
			return rs.getBigDecimal("importe_cuotas_ant");
		}
		/**
		 * Base de Contingencias Generales en Pesetas
		 * @return the column 'base_cg_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_cg_pts()
		throws SQLException {
			return rs.getBigDecimal("base_cg_pts");
		}
		/**
		 * Base de Accidentes de Trabajo en Pesetas
		 * @return the column 'base_acc_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_pts()
		throws SQLException {
			return rs.getBigDecimal("base_acc_pts");
		}
		/**
		 * Base de Accidentes de Trabajo sin Horas Extras en Pesetas
		 * @return the column 'base_acc_sin_h_pts' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc_sin_h_pts()
		throws SQLException {
			return rs.getBigDecimal("base_acc_sin_h_pts");
		}
		/**
		 * Codigo de Nomina Resumen de Atrasos
		 * @return the column 'cdgnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdgnom()
		throws SQLException {
			return rs.getInt("cdgnom");
		}

		public void visitNomdfdev(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomdfdev WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nomdfdev nomdfdev = new Nomdfdev(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomdfdev(nomdfdev, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitNomdfdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomdfdto WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Nomdfdto nomdfdto = new Nomdfdto(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomdfdto(nomdfdto, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nominaex
	 * 
	 */
	public class Nominaex {
		
		private ResultSet rs;
		
		private Nominaex (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Paga
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Anio Paga Extra
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes Paga Extra
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Inicio Periodo Paga
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Periodo Paga
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Importe Paga Extra
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * % de I.R.P.F.
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe I.R.P.F.
		 * @return the column 'impirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpirpf()
		throws SQLException {
			return rs.getBigDecimal("impirpf");
		}
		/**
		 * Importe Liquido
		 * @return the column 'liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiquido()
		throws SQLException {
			return rs.getBigDecimal("liquido");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Fecha de Cobro
		 * @return the column 'feccob' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccob()
		throws SQLException {
			return rs.getDate("feccob");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Fecha Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Fecha de emision
		 * @return the column 'fecemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecemi()
		throws SQLException {
			return rs.getDate("fecemi");
		}
		/**
		 * Nombre Actividad
		 * @return the column 'nomemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomemp()
		throws SQLException {
			return rs.getString("nomemp");
		}
		/**
		 * Nombre Trabajador
		 * @return the column 'nomper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomper()
		throws SQLException {
			return rs.getString("nomper");
		}
		/**
		 * Direccion
		 * @return the column 'direccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDireccion()
		throws SQLException {
			return rs.getString("direccion");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Descripcion Categoria
		 * @return the column 'descat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescat()
		throws SQLException {
			return rs.getString("descat");
		}
		/**
		 * Profesion
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Numero matricula
		 * @return the column 'nummat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNummat()
		throws SQLException {
			return rs.getInt("nummat");
		}
		/**
		 * Fecha de Antiguedad
		 * @return the column 'fecant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecant()
		throws SQLException {
			return rs.getDate("fecant");
		}
		/**
		 * Total a deducir
		 * @return the column 'total_deducir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_deducir()
		throws SQLException {
			return rs.getBigDecimal("total_deducir");
		}

		public void visitNomdtoex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomdtoex WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setInt(2, this.getNumero() ); 
				rs = stmt.executeQuery();
				Nomdtoex nomdtoex = new Nomdtoex(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomdtoex(nomdtoex, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nominaexdf
	 * 
	 */
	public class Nominaexdf {
		
		private ResultSet rs;
		
		private Nominaexdf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Paga
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Anio Paga Extra
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes Paga Extra
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Inicio Periodo Paga
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fin Periodo Paga
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Importe Paga Extra
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * % de I.R.P.F.
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe I.R.P.F.
		 * @return the column 'impirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpirpf()
		throws SQLException {
			return rs.getBigDecimal("impirpf");
		}
		/**
		 * Importe Liquido
		 * @return the column 'liquido' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiquido()
		throws SQLException {
			return rs.getBigDecimal("liquido");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Fecha de Cobro
		 * @return the column 'feccob' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccob()
		throws SQLException {
			return rs.getDate("feccob");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Fecha Cobro Real
		 * @return the column 'feccobreal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeccobreal()
		throws SQLException {
			return rs.getDate("feccobreal");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Fecha de emision
		 * @return the column 'fecemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecemi()
		throws SQLException {
			return rs.getDate("fecemi");
		}
		/**
		 * Nombre Actividad
		 * @return the column 'nomemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomemp()
		throws SQLException {
			return rs.getString("nomemp");
		}
		/**
		 * Nombre Trabajador
		 * @return the column 'nomper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomper()
		throws SQLException {
			return rs.getString("nomper");
		}
		/**
		 * Direccion
		 * @return the column 'direccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDireccion()
		throws SQLException {
			return rs.getString("direccion");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Descripcion Categoria
		 * @return the column 'descat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescat()
		throws SQLException {
			return rs.getString("descat");
		}
		/**
		 * Profesion
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Numero matricula
		 * @return the column 'nummat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNummat()
		throws SQLException {
			return rs.getInt("nummat");
		}
		/**
		 * Fecha de Antiguedad
		 * @return the column 'fecant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecant()
		throws SQLException {
			return rs.getDate("fecant");
		}
		/**
		 * Total a deducir
		 * @return the column 'total_deducir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getTotal_deducir()
		throws SQLException {
			return rs.getBigDecimal("total_deducir");
		}
		/**
		 * Codigo de Nomina Resumen de Atrasos
		 * @return the column 'cdgnom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdgnom()
		throws SQLException {
			return rs.getInt("cdgnom");
		}

		public void visitNomdfdtoex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomdfdtoex WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " numero = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setInt(2, this.getNumero() ); 
				rs = stmt.executeQuery();
				Nomdfdtoex nomdfdtoex = new Nomdfdtoex(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitNomdfdtoex(nomdfdtoex, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Nominait
	 * 
	 */
	public class Nominait {
		
		private ResultSet rs;
		
		private Nominait (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'feciniit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeciniit()
		throws SQLException {
			return rs.getDate("feciniit");
		}
		/**
		 * Fecha Inicio Incidencia Periodo Nomina
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Incidencia Periodo Nomina
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de I.T.
		 * @return the column 'tipoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipoit()
		throws SQLException {
			return rs.getString("tipoit");
		}
		/**
		 * Dias I.T.
		 * @return the column 'diasit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasit()
		throws SQLException {
			return rs.getInt("diasit");
		}
		/**
		 * Dias Prestacion I.T. Seg.Social
		 * @return the column 'dias_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias_ss()
		throws SQLException {
			return rs.getInt("dias_ss");
		}
		/**
		 * Dias Prestacion I.T. Empresa
		 * @return the column 'diasemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasemp()
		throws SQLException {
			return rs.getInt("diasemp");
		}
		/**
		 * Dias sin Prestacion Dineraria
		 * @return the column 'diasin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasin()
		throws SQLException {
			return rs.getInt("diasin");
		}
		/**
		 * Dias Prestacion 60 % S.S.
		 * @return the column 'dias60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias60()
		throws SQLException {
			return rs.getInt("dias60");
		}
		/**
		 * Dias Prestacion 75 % S.S.
		 * @return the column 'dias75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias75()
		throws SQLException {
			return rs.getInt("dias75");
		}
		/**
		 * Importe Prestacion I.T. Seg. Social
		 * @return the column 'pts_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPts_ss()
		throws SQLException {
			return rs.getBigDecimal("pts_ss");
		}
		/**
		 * Importe Prestacion I.T. Empresa
		 * @return the column 'ptsemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPtsemp()
		throws SQLException {
			return rs.getBigDecimal("ptsemp");
		}
		/**
		 * Base Contingencias Generales I.T.
		 * @return the column 'basecon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecon()
		throws SQLException {
			return rs.getBigDecimal("basecon");
		}
		/**
		 * Base Accidentes Trabajo I.T.
		 * @return the column 'baseacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc()
		throws SQLException {
			return rs.getBigDecimal("baseacc");
		}
		/**
		 * Total dias de I.T.
		 * @return the column 'totaldias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTotaldias()
		throws SQLException {
			return rs.getInt("totaldias");
		}
		/**
		 * Indicador de registro simulado
		 * @return the column 'simula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSimula()
		throws SQLException {
			return rs.getString("simula");
		}
		/**
		 * Base C.G. Total
		 * @return the column 'basecon_total' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecon_total()
		throws SQLException {
			return rs.getBigDecimal("basecon_total");
		}
		/**
		 * Base ACC. Total
		 * @return the column 'baseacc_total' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc_total()
		throws SQLException {
			return rs.getBigDecimal("baseacc_total");
		}
		/**
		 * Riesgo Embarazo
		 * @return the column 'riesgo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRiesgo()
		throws SQLException {
			return rs.getString("riesgo");
		}

	}
	
	
	/**
	 * Nominaitnu
	 * 
	 */
	public class Nominaitnu {
		
		private ResultSet rs;
		
		private Nominaitnu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'feciniit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeciniit()
		throws SQLException {
			return rs.getDate("feciniit");
		}
		/**
		 * Fecha Inicio Incidencia Periodo Nomina
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Incidencia Periodo Nomina
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de I.T.
		 * @return the column 'tipoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipoit()
		throws SQLException {
			return rs.getString("tipoit");
		}
		/**
		 * Dias I.T.
		 * @return the column 'diasit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasit()
		throws SQLException {
			return rs.getInt("diasit");
		}
		/**
		 * Dias Prestacion I.T. Seg.Social
		 * @return the column 'dias_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias_ss()
		throws SQLException {
			return rs.getInt("dias_ss");
		}
		/**
		 * Dias Prestacion I.T. Empresa
		 * @return the column 'diasemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasemp()
		throws SQLException {
			return rs.getInt("diasemp");
		}
		/**
		 * Dias sin Prestacion Dineraria
		 * @return the column 'diasin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasin()
		throws SQLException {
			return rs.getInt("diasin");
		}
		/**
		 * Dias prestacion 60 % S.S.
		 * @return the column 'dias60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias60()
		throws SQLException {
			return rs.getInt("dias60");
		}
		/**
		 * Dias Prestacion 75 % S.S.
		 * @return the column 'dias75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias75()
		throws SQLException {
			return rs.getInt("dias75");
		}
		/**
		 * Importe Prestacion I.T. Seg. Social
		 * @return the column 'pts_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPts_ss()
		throws SQLException {
			return rs.getBigDecimal("pts_ss");
		}
		/**
		 * Importe Prestacion I.T. Empresa
		 * @return the column 'ptsemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPtsemp()
		throws SQLException {
			return rs.getBigDecimal("ptsemp");
		}
		/**
		 * Base Contingencias Generales I.T.
		 * @return the column 'basecon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecon()
		throws SQLException {
			return rs.getBigDecimal("basecon");
		}
		/**
		 * Base Accidentes Trabajo I.T.
		 * @return the column 'baseacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc()
		throws SQLException {
			return rs.getBigDecimal("baseacc");
		}
		/**
		 * Total dias de I.T.
		 * @return the column 'totaldias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTotaldias()
		throws SQLException {
			return rs.getInt("totaldias");
		}
		/**
		 * Indicador de registro simulado
		 * @return the column 'simula' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSimula()
		throws SQLException {
			return rs.getString("simula");
		}
		/**
		 * Base C.G. Total
		 * @return the column 'basecon_total' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasecon_total()
		throws SQLException {
			return rs.getBigDecimal("basecon_total");
		}
		/**
		 * Base ACC. Total
		 * @return the column 'baseacc_total' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseacc_total()
		throws SQLException {
			return rs.getBigDecimal("baseacc_total");
		}
		/**
		 * Riesgo Embarazo
		 * @return the column 'riesgo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRiesgo()
		throws SQLException {
			return rs.getString("riesgo");
		}

	}
	
	
	/**
	 * Nszadmh
	 * 
	 */
	public class Nszadmh {
		
		private ResultSet rs;
		
		private Nszadmh (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sah0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSah0()
		throws SQLException {
			return rs.getString("sah0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sah1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSah1()
		throws SQLException {
			return rs.getString("sah1");
		}

	}
	
	
	/**
	 * Nszanex
	 * 
	 */
	public class Nszanex {
		
		private ResultSet rs;
		
		private Nszanex (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'san0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSan0()
		throws SQLException {
			return rs.getString("san0");
		}
		/**
		 * $column.remarks
		 * @return the column 'san1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSan1()
		throws SQLException {
			return rs.getString("san1");
		}

	}
	
	
	/**
	 * Nszavis
	 * 
	 */
	public class Nszavis {
		
		private ResultSet rs;
		
		private Nszavis (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sav0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSav0()
		throws SQLException {
			return rs.getString("sav0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sav1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSav1()
		throws SQLException {
			return rs.getString("sav1");
		}

	}
	
	
	/**
	 * Nszbanc
	 * 
	 */
	public class Nszbanc {
		
		private ResultSet rs;
		
		private Nszbanc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sbk0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSbk0()
		throws SQLException {
			return rs.getString("sbk0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sbk1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSbk1()
		throws SQLException {
			return rs.getString("sbk1");
		}

	}
	
	
	/**
	 * Nszbase
	 * 
	 */
	public class Nszbase {
		
		private ResultSet rs;
		
		private Nszbase (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sbc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSbc0()
		throws SQLException {
			return rs.getString("sbc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sbc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSbc1()
		throws SQLException {
			return rs.getString("sbc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNbc0()
		throws SQLException {
			return rs.getBigDecimal("nbc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNbc1()
		throws SQLException {
			return rs.getBigDecimal("nbc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNbc2()
		throws SQLException {
			return rs.getBigDecimal("nbc2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbc3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNbc3()
		throws SQLException {
			return rs.getBigDecimal("nbc3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbc4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNbc4()
		throws SQLException {
			return rs.getBigDecimal("nbc4");
		}

	}
	
	
	/**
	 * Nszbolc
	 * 
	 */
	public class Nszbolc {
		
		private ResultSet rs;
		
		private Nszbolc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sbl0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSbl0()
		throws SQLException {
			return rs.getString("sbl0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl0()
		throws SQLException {
			return rs.getInt("nbl0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl1()
		throws SQLException {
			return rs.getInt("nbl1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl2()
		throws SQLException {
			return rs.getInt("nbl2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl3()
		throws SQLException {
			return rs.getInt("nbl3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl4()
		throws SQLException {
			return rs.getInt("nbl4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl5()
		throws SQLException {
			return rs.getInt("nbl5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl6()
		throws SQLException {
			return rs.getInt("nbl6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl7()
		throws SQLException {
			return rs.getInt("nbl7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl8()
		throws SQLException {
			return rs.getInt("nbl8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl9()
		throws SQLException {
			return rs.getInt("nbl9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl10()
		throws SQLException {
			return rs.getInt("nbl10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl11()
		throws SQLException {
			return rs.getInt("nbl11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl12()
		throws SQLException {
			return rs.getInt("nbl12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl13()
		throws SQLException {
			return rs.getInt("nbl13");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl14()
		throws SQLException {
			return rs.getInt("nbl14");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl15()
		throws SQLException {
			return rs.getInt("nbl15");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl16()
		throws SQLException {
			return rs.getInt("nbl16");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl17()
		throws SQLException {
			return rs.getInt("nbl17");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl18()
		throws SQLException {
			return rs.getInt("nbl18");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl19()
		throws SQLException {
			return rs.getInt("nbl19");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl20()
		throws SQLException {
			return rs.getInt("nbl20");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl21()
		throws SQLException {
			return rs.getInt("nbl21");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl22()
		throws SQLException {
			return rs.getInt("nbl22");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl23()
		throws SQLException {
			return rs.getInt("nbl23");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl24()
		throws SQLException {
			return rs.getInt("nbl24");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl25()
		throws SQLException {
			return rs.getInt("nbl25");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl26()
		throws SQLException {
			return rs.getInt("nbl26");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl27()
		throws SQLException {
			return rs.getInt("nbl27");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl28()
		throws SQLException {
			return rs.getInt("nbl28");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl29()
		throws SQLException {
			return rs.getInt("nbl29");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl30()
		throws SQLException {
			return rs.getInt("nbl30");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl31' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl31()
		throws SQLException {
			return rs.getInt("nbl31");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl212' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl212()
		throws SQLException {
			return rs.getInt("nbl212");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl33()
		throws SQLException {
			return rs.getInt("nbl33");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl34' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl34()
		throws SQLException {
			return rs.getInt("nbl34");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl35' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl35()
		throws SQLException {
			return rs.getInt("nbl35");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl36' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl36()
		throws SQLException {
			return rs.getInt("nbl36");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl37' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl37()
		throws SQLException {
			return rs.getInt("nbl37");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl38' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl38()
		throws SQLException {
			return rs.getInt("nbl38");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl39' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl39()
		throws SQLException {
			return rs.getInt("nbl39");
		}
		/**
		 * $column.remarks
		 * @return the column 'nbl40' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNbl40()
		throws SQLException {
			return rs.getInt("nbl40");
		}

	}
	
	
	/**
	 * Nszboni
	 * 
	 */
	public class Nszboni {
		
		private ResultSet rs;
		
		private Nszboni (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'stb0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStb0()
		throws SQLException {
			return rs.getString("stb0");
		}
		/**
		 * $column.remarks
		 * @return the column 'stb1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStb1()
		throws SQLException {
			return rs.getString("stb1");
		}
		/**
		 * $column.remarks
		 * @return the column 'stb2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStb2()
		throws SQLException {
			return rs.getString("stb2");
		}
		/**
		 * $column.remarks
		 * @return the column 'stb3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStb3()
		throws SQLException {
			return rs.getString("stb3");
		}
		/**
		 * $column.remarks
		 * @return the column 'stb4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStb4()
		throws SQLException {
			return rs.getString("stb4");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntb0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtb0()
		throws SQLException {
			return rs.getBigDecimal("ntb0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntb1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtb1()
		throws SQLException {
			return rs.getBigDecimal("ntb1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntb2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtb2()
		throws SQLException {
			return rs.getBigDecimal("ntb2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntb3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtb3()
		throws SQLException {
			return rs.getBigDecimal("ntb3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntb4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtb4()
		throws SQLException {
			return rs.getBigDecimal("ntb4");
		}

	}
	
	
	/**
	 * Nszcala
	 * 
	 */
	public class Nszcala {
		
		private ResultSet rs;
		
		private Nszcala (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sca0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSca0()
		throws SQLException {
			return rs.getString("sca0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sca1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSca1()
		throws SQLException {
			return rs.getString("sca1");
		}
		/**
		 * $column.remarks
		 * @return the column 'sca2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSca2()
		throws SQLException {
			return rs.getString("sca2");
		}

	}
	
	
	/**
	 * Nszcatg
	 * 
	 */
	public class Nszcatg {
		
		private ResultSet rs;
		
		private Nszcatg (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'scg0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg0()
		throws SQLException {
			return rs.getString("scg0");
		}
		/**
		 * $column.remarks
		 * @return the column 'scg1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg1()
		throws SQLException {
			return rs.getString("scg1");
		}
		/**
		 * $column.remarks
		 * @return the column 'scg2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg2()
		throws SQLException {
			return rs.getString("scg2");
		}
		/**
		 * $column.remarks
		 * @return the column 'scg3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg3()
		throws SQLException {
			return rs.getString("scg3");
		}
		/**
		 * $column.remarks
		 * @return the column 'scg4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg4()
		throws SQLException {
			return rs.getString("scg4");
		}
		/**
		 * $column.remarks
		 * @return the column 'scg5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScg5()
		throws SQLException {
			return rs.getString("scg5");
		}

	}
	
	
	/**
	 * Nszcdtr
	 * 
	 */
	public class Nszcdtr {
		
		private ResultSet rs;
		
		private Nszcdtr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'scd0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScd0()
		throws SQLException {
			return rs.getString("scd0");
		}
		/**
		 * $column.remarks
		 * @return the column 'scd1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScd1()
		throws SQLException {
			return rs.getString("scd1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcd0()
		throws SQLException {
			return rs.getInt("ncd0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcd1()
		throws SQLException {
			return rs.getInt("ncd1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcd2()
		throws SQLException {
			return rs.getInt("ncd2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNcd3()
		throws SQLException {
			return rs.getBigDecimal("ncd3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcd4()
		throws SQLException {
			return rs.getInt("ncd4");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncd5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcd5()
		throws SQLException {
			return rs.getInt("ncd5");
		}

	}
	
	
	/**
	 * Nszcere
	 * 
	 */
	public class Nszcere {
		
		private ResultSet rs;
		
		private Nszcere (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'scr0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScr0()
		throws SQLException {
			return rs.getString("scr0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr0()
		throws SQLException {
			return rs.getInt("ncr0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr1()
		throws SQLException {
			return rs.getInt("ncr1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr2()
		throws SQLException {
			return rs.getInt("ncr2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr3()
		throws SQLException {
			return rs.getInt("ncr3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr4()
		throws SQLException {
			return rs.getInt("ncr4");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncr5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNcr5()
		throws SQLException {
			return rs.getInt("ncr5");
		}

	}
	
	
	/**
	 * Nszcoco
	 * 
	 */
	public class Nszcoco {
		
		private ResultSet rs;
		
		private Nszcoco (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'scn0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScn0()
		throws SQLException {
			return rs.getString("scn0");
		}
		/**
		 * $column.remarks
		 * @return the column 'scn1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScn1()
		throws SQLException {
			return rs.getString("scn1");
		}
		/**
		 * $column.remarks
		 * @return the column 'scn2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScn2()
		throws SQLException {
			return rs.getString("scn2");
		}
		/**
		 * $column.remarks
		 * @return the column 'scn3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScn3()
		throws SQLException {
			return rs.getString("scn3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncn0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNcn0()
		throws SQLException {
			return rs.getBigDecimal("ncn0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ncn1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNcn1()
		throws SQLException {
			return rs.getBigDecimal("ncn1");
		}

	}
	
	
	/**
	 * Nszcomp
	 * 
	 */
	public class Nszcomp {
		
		private ResultSet rs;
		
		private Nszcomp (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'scp0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScp0()
		throws SQLException {
			return rs.getString("scp0");
		}
		/**
		 * $column.remarks
		 * @return the column 'scp1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScp1()
		throws SQLException {
			return rs.getString("scp1");
		}
		/**
		 * $column.remarks
		 * @return the column 'scp2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScp2()
		throws SQLException {
			return rs.getString("scp2");
		}
		/**
		 * $column.remarks
		 * @return the column 'scp3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getScp3()
		throws SQLException {
			return rs.getString("scp3");
		}

	}
	
	
	/**
	 * Nszcont
	 * 
	 */
	public class Nszcont {
		
		private ResultSet rs;
		
		private Nszcont (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'stc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStc0()
		throws SQLException {
			return rs.getString("stc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'stc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStc1()
		throws SQLException {
			return rs.getString("stc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'stc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStc2()
		throws SQLException {
			return rs.getString("stc2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtc0()
		throws SQLException {
			return rs.getInt("ntc0");
		}

	}
	
	
	/**
	 * Nszconv
	 * 
	 */
	public class Nszconv {
		
		private ResultSet rs;
		
		private Nszconv (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sco0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSco0()
		throws SQLException {
			return rs.getString("sco0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sco1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSco1()
		throws SQLException {
			return rs.getString("sco1");
		}
		/**
		 * $column.remarks
		 * @return the column 'sco2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSco2()
		throws SQLException {
			return rs.getString("sco2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco0()
		throws SQLException {
			return rs.getInt("nco0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco1()
		throws SQLException {
			return rs.getInt("nco1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco2()
		throws SQLException {
			return rs.getInt("nco2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco3()
		throws SQLException {
			return rs.getInt("nco3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco4()
		throws SQLException {
			return rs.getInt("nco4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco5()
		throws SQLException {
			return rs.getInt("nco5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco6()
		throws SQLException {
			return rs.getInt("nco6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco7()
		throws SQLException {
			return rs.getInt("nco7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco8()
		throws SQLException {
			return rs.getInt("nco8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nco9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNco9()
		throws SQLException {
			return rs.getInt("nco9");
		}

	}
	
	
	/**
	 * Nszcopa
	 * 
	 */
	public class Nszcopa {
		
		private ResultSet rs;
		
		private Nszcopa (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'spg0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg0()
		throws SQLException {
			return rs.getString("spg0");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg1()
		throws SQLException {
			return rs.getString("spg1");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg2()
		throws SQLException {
			return rs.getString("spg2");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg3()
		throws SQLException {
			return rs.getString("spg3");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg4()
		throws SQLException {
			return rs.getString("spg4");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg5()
		throws SQLException {
			return rs.getString("spg5");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg6()
		throws SQLException {
			return rs.getString("spg6");
		}
		/**
		 * $column.remarks
		 * @return the column 'spg7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpg7()
		throws SQLException {
			return rs.getString("spg7");
		}

	}
	
	
	/**
	 * Nszdcpr
	 * 
	 */
	public class Nszdcpr {
		
		private ResultSet rs;
		
		private Nszdcpr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sdc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdc0()
		throws SQLException {
			return rs.getString("sdc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdc1()
		throws SQLException {
			return rs.getString("sdc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc0()
		throws SQLException {
			return rs.getBigDecimal("ndc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc1()
		throws SQLException {
			return rs.getInt("ndc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc2()
		throws SQLException {
			return rs.getInt("ndc2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc3()
		throws SQLException {
			return rs.getInt("ndc3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc4()
		throws SQLException {
			return rs.getBigDecimal("ndc4");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc5()
		throws SQLException {
			return rs.getInt("ndc5");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc6()
		throws SQLException {
			return rs.getBigDecimal("ndc6");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc7()
		throws SQLException {
			return rs.getBigDecimal("ndc7");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc8()
		throws SQLException {
			return rs.getBigDecimal("ndc8");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc9()
		throws SQLException {
			return rs.getBigDecimal("ndc9");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc10()
		throws SQLException {
			return rs.getBigDecimal("ndc10");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc11()
		throws SQLException {
			return rs.getBigDecimal("ndc11");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc12()
		throws SQLException {
			return rs.getBigDecimal("ndc12");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc13()
		throws SQLException {
			return rs.getBigDecimal("ndc13");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc14()
		throws SQLException {
			return rs.getBigDecimal("ndc14");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc15()
		throws SQLException {
			return rs.getBigDecimal("ndc15");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc16()
		throws SQLException {
			return rs.getBigDecimal("ndc16");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc17()
		throws SQLException {
			return rs.getBigDecimal("ndc17");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc18()
		throws SQLException {
			return rs.getBigDecimal("ndc18");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc19()
		throws SQLException {
			return rs.getBigDecimal("ndc19");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc20()
		throws SQLException {
			return rs.getBigDecimal("ndc20");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc21()
		throws SQLException {
			return rs.getBigDecimal("ndc21");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc22()
		throws SQLException {
			return rs.getBigDecimal("ndc22");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc23()
		throws SQLException {
			return rs.getBigDecimal("ndc23");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc24()
		throws SQLException {
			return rs.getBigDecimal("ndc24");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc25()
		throws SQLException {
			return rs.getBigDecimal("ndc25");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc26()
		throws SQLException {
			return rs.getBigDecimal("ndc26");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc27()
		throws SQLException {
			return rs.getBigDecimal("ndc27");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc28()
		throws SQLException {
			return rs.getBigDecimal("ndc28");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc29()
		throws SQLException {
			return rs.getBigDecimal("ndc29");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc30()
		throws SQLException {
			return rs.getBigDecimal("ndc30");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc31' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc31()
		throws SQLException {
			return rs.getBigDecimal("ndc31");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc32' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc32()
		throws SQLException {
			return rs.getBigDecimal("ndc32");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc33()
		throws SQLException {
			return rs.getBigDecimal("ndc33");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc34' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc34()
		throws SQLException {
			return rs.getInt("ndc34");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc35' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc35()
		throws SQLException {
			return rs.getInt("ndc35");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc36' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc36()
		throws SQLException {
			return rs.getInt("ndc36");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc37' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc37()
		throws SQLException {
			return rs.getInt("ndc37");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc38' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc38()
		throws SQLException {
			return rs.getInt("ndc38");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc39' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc39()
		throws SQLException {
			return rs.getInt("ndc39");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc40' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc40()
		throws SQLException {
			return rs.getInt("ndc40");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc41' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc41()
		throws SQLException {
			return rs.getInt("ndc41");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc42' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc42()
		throws SQLException {
			return rs.getInt("ndc42");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc43' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc43()
		throws SQLException {
			return rs.getInt("ndc43");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc44' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc44()
		throws SQLException {
			return rs.getInt("ndc44");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc45' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc45()
		throws SQLException {
			return rs.getInt("ndc45");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc46' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc46()
		throws SQLException {
			return rs.getInt("ndc46");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc47' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc47()
		throws SQLException {
			return rs.getInt("ndc47");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc48' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc48()
		throws SQLException {
			return rs.getInt("ndc48");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc49' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc49()
		throws SQLException {
			return rs.getInt("ndc49");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc50' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc50()
		throws SQLException {
			return rs.getInt("ndc50");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc51' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc51()
		throws SQLException {
			return rs.getInt("ndc51");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc52' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc52()
		throws SQLException {
			return rs.getInt("ndc52");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc53' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc53()
		throws SQLException {
			return rs.getInt("ndc53");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc54' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc54()
		throws SQLException {
			return rs.getInt("ndc54");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc55' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc55()
		throws SQLException {
			return rs.getInt("ndc55");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc56' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc56()
		throws SQLException {
			return rs.getInt("ndc56");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc57' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc57()
		throws SQLException {
			return rs.getInt("ndc57");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc58' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc58()
		throws SQLException {
			return rs.getInt("ndc58");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc59' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc59()
		throws SQLException {
			return rs.getInt("ndc59");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc60()
		throws SQLException {
			return rs.getInt("ndc60");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc61' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc61()
		throws SQLException {
			return rs.getInt("ndc61");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc62' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc62()
		throws SQLException {
			return rs.getInt("ndc62");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc63' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc63()
		throws SQLException {
			return rs.getInt("ndc63");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc64' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc64()
		throws SQLException {
			return rs.getBigDecimal("ndc64");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc65()
		throws SQLException {
			return rs.getInt("ndc65");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc66' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc66()
		throws SQLException {
			return rs.getInt("ndc66");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc67' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNdc67()
		throws SQLException {
			return rs.getBigDecimal("ndc67");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc68' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc68()
		throws SQLException {
			return rs.getInt("ndc68");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc69' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc69()
		throws SQLException {
			return rs.getInt("ndc69");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc70' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc70()
		throws SQLException {
			return rs.getInt("ndc70");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc71' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc71()
		throws SQLException {
			return rs.getInt("ndc71");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc72' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc72()
		throws SQLException {
			return rs.getInt("ndc72");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc73' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc73()
		throws SQLException {
			return rs.getInt("ndc73");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc74' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc74()
		throws SQLException {
			return rs.getInt("ndc74");
		}
		/**
		 * $column.remarks
		 * @return the column 'ndc75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNdc75()
		throws SQLException {
			return rs.getInt("ndc75");
		}

	}
	
	
	/**
	 * Nszdomi
	 * 
	 */
	public class Nszdomi {
		
		private ResultSet rs;
		
		private Nszdomi (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sdm0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm0()
		throws SQLException {
			return rs.getString("sdm0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm1()
		throws SQLException {
			return rs.getString("sdm1");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm2()
		throws SQLException {
			return rs.getString("sdm2");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm3()
		throws SQLException {
			return rs.getString("sdm3");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm4()
		throws SQLException {
			return rs.getString("sdm4");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm5()
		throws SQLException {
			return rs.getString("sdm5");
		}
		/**
		 * $column.remarks
		 * @return the column 'sdm6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSdm6()
		throws SQLException {
			return rs.getString("sdm6");
		}

	}
	
	
	/**
	 * Nszempr
	 * 
	 */
	public class Nszempr {
		
		private ResultSet rs;
		
		private Nszempr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sem0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSem0()
		throws SQLException {
			return rs.getString("sem0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sem1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSem1()
		throws SQLException {
			return rs.getString("sem1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem0()
		throws SQLException {
			return rs.getInt("nem0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem1()
		throws SQLException {
			return rs.getInt("nem1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem2()
		throws SQLException {
			return rs.getInt("nem2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem3()
		throws SQLException {
			return rs.getInt("nem3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem4()
		throws SQLException {
			return rs.getInt("nem4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem5()
		throws SQLException {
			return rs.getInt("nem5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem6()
		throws SQLException {
			return rs.getInt("nem6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem7()
		throws SQLException {
			return rs.getInt("nem7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem8()
		throws SQLException {
			return rs.getInt("nem8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem9()
		throws SQLException {
			return rs.getInt("nem9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem10()
		throws SQLException {
			return rs.getInt("nem10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem11()
		throws SQLException {
			return rs.getInt("nem11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem12()
		throws SQLException {
			return rs.getInt("nem12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem13()
		throws SQLException {
			return rs.getInt("nem13");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem14()
		throws SQLException {
			return rs.getInt("nem14");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem15()
		throws SQLException {
			return rs.getInt("nem15");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem16()
		throws SQLException {
			return rs.getInt("nem16");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem17()
		throws SQLException {
			return rs.getInt("nem17");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem18()
		throws SQLException {
			return rs.getInt("nem18");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem19()
		throws SQLException {
			return rs.getInt("nem19");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem20()
		throws SQLException {
			return rs.getInt("nem20");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem21()
		throws SQLException {
			return rs.getInt("nem21");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem22()
		throws SQLException {
			return rs.getInt("nem22");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem23()
		throws SQLException {
			return rs.getInt("nem23");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem24()
		throws SQLException {
			return rs.getInt("nem24");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem25()
		throws SQLException {
			return rs.getInt("nem25");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem26()
		throws SQLException {
			return rs.getInt("nem26");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem27()
		throws SQLException {
			return rs.getInt("nem27");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem28()
		throws SQLException {
			return rs.getInt("nem28");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem29()
		throws SQLException {
			return rs.getInt("nem29");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem30()
		throws SQLException {
			return rs.getInt("nem30");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem31' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem31()
		throws SQLException {
			return rs.getInt("nem31");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem32' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem32()
		throws SQLException {
			return rs.getInt("nem32");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem33()
		throws SQLException {
			return rs.getInt("nem33");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem34' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem34()
		throws SQLException {
			return rs.getInt("nem34");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem35' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem35()
		throws SQLException {
			return rs.getInt("nem35");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem36' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem36()
		throws SQLException {
			return rs.getInt("nem36");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem37' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem37()
		throws SQLException {
			return rs.getInt("nem37");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem38' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem38()
		throws SQLException {
			return rs.getInt("nem38");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem39' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem39()
		throws SQLException {
			return rs.getInt("nem39");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem40' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem40()
		throws SQLException {
			return rs.getInt("nem40");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem41' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem41()
		throws SQLException {
			return rs.getInt("nem41");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem42' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem42()
		throws SQLException {
			return rs.getInt("nem42");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem43' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem43()
		throws SQLException {
			return rs.getInt("nem43");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem44' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem44()
		throws SQLException {
			return rs.getInt("nem44");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem45' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem45()
		throws SQLException {
			return rs.getInt("nem45");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem46' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem46()
		throws SQLException {
			return rs.getInt("nem46");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem47' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem47()
		throws SQLException {
			return rs.getInt("nem47");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem48' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem48()
		throws SQLException {
			return rs.getInt("nem48");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem49' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem49()
		throws SQLException {
			return rs.getInt("nem49");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem50' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem50()
		throws SQLException {
			return rs.getInt("nem50");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem51' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem51()
		throws SQLException {
			return rs.getInt("nem51");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem52' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem52()
		throws SQLException {
			return rs.getInt("nem52");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem53' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem53()
		throws SQLException {
			return rs.getInt("nem53");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem54' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem54()
		throws SQLException {
			return rs.getInt("nem54");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem55' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem55()
		throws SQLException {
			return rs.getInt("nem55");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem56' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem56()
		throws SQLException {
			return rs.getInt("nem56");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem57' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem57()
		throws SQLException {
			return rs.getInt("nem57");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem58' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem58()
		throws SQLException {
			return rs.getInt("nem58");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem59' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem59()
		throws SQLException {
			return rs.getInt("nem59");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem60()
		throws SQLException {
			return rs.getInt("nem60");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem61' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem61()
		throws SQLException {
			return rs.getInt("nem61");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem62' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem62()
		throws SQLException {
			return rs.getInt("nem62");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem63' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem63()
		throws SQLException {
			return rs.getInt("nem63");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem64' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem64()
		throws SQLException {
			return rs.getInt("nem64");
		}
		/**
		 * $column.remarks
		 * @return the column 'nem65' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNem65()
		throws SQLException {
			return rs.getInt("nem65");
		}

	}
	
	
	/**
	 * Nszepig
	 * 
	 */
	public class Nszepig {
		
		private ResultSet rs;
		
		private Nszepig (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sea0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSea0()
		throws SQLException {
			return rs.getString("sea0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sea1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSea1()
		throws SQLException {
			return rs.getString("sea1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nea0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNea0()
		throws SQLException {
			return rs.getBigDecimal("nea0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nea1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNea1()
		throws SQLException {
			return rs.getBigDecimal("nea1");
		}

	}
	
	
	/**
	 * Nszfini
	 * 
	 */
	public class Nszfini {
		
		private ResultSet rs;
		
		private Nszfini (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sfq0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSfq0()
		throws SQLException {
			return rs.getString("sfq0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sfq1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSfq1()
		throws SQLException {
			return rs.getString("sfq1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq0()
		throws SQLException {
			return rs.getInt("nfq0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq1()
		throws SQLException {
			return rs.getInt("nfq1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq2()
		throws SQLException {
			return rs.getInt("nfq2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq3()
		throws SQLException {
			return rs.getInt("nfq3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq4()
		throws SQLException {
			return rs.getInt("nfq4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq5()
		throws SQLException {
			return rs.getInt("nfq5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq6()
		throws SQLException {
			return rs.getInt("nfq6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq7()
		throws SQLException {
			return rs.getInt("nfq7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq8()
		throws SQLException {
			return rs.getInt("nfq8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq9()
		throws SQLException {
			return rs.getInt("nfq9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq10()
		throws SQLException {
			return rs.getInt("nfq10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq11()
		throws SQLException {
			return rs.getInt("nfq11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq12()
		throws SQLException {
			return rs.getInt("nfq12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nfq13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNfq13()
		throws SQLException {
			return rs.getInt("nfq13");
		}

	}
	
	
	/**
	 * Nszilte
	 * 
	 */
	public class Nszilte {
		
		private ResultSet rs;
		
		private Nszilte (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sil0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSil0()
		throws SQLException {
			return rs.getString("sil0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sil1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSil1()
		throws SQLException {
			return rs.getString("sil1");
		}
		/**
		 * $column.remarks
		 * @return the column 'sil2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSil2()
		throws SQLException {
			return rs.getString("sil2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNil0()
		throws SQLException {
			return rs.getInt("nil0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNil1()
		throws SQLException {
			return rs.getInt("nil1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNil2()
		throws SQLException {
			return rs.getBigDecimal("nil2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNil3()
		throws SQLException {
			return rs.getBigDecimal("nil3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNil4()
		throws SQLException {
			return rs.getBigDecimal("nil4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNil5()
		throws SQLException {
			return rs.getBigDecimal("nil5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nil6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNil6()
		throws SQLException {
			return rs.getBigDecimal("nil6");
		}

	}
	
	
	/**
	 * Nszinci
	 * 
	 */
	public class Nszinci {
		
		private ResultSet rs;
		
		private Nszinci (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sin0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSin0()
		throws SQLException {
			return rs.getString("sin0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin0()
		throws SQLException {
			return rs.getInt("nin0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin1()
		throws SQLException {
			return rs.getInt("nin1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin2()
		throws SQLException {
			return rs.getInt("nin2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin3()
		throws SQLException {
			return rs.getInt("nin3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin4()
		throws SQLException {
			return rs.getInt("nin4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin5()
		throws SQLException {
			return rs.getInt("nin5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin6()
		throws SQLException {
			return rs.getInt("nin6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin7()
		throws SQLException {
			return rs.getInt("nin7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin8()
		throws SQLException {
			return rs.getInt("nin8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin9()
		throws SQLException {
			return rs.getInt("nin9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin10()
		throws SQLException {
			return rs.getInt("nin10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nin11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNin11()
		throws SQLException {
			return rs.getInt("nin11");
		}

	}
	
	
	/**
	 * Nszmest
	 * 
	 */
	public class Nszmest {
		
		private ResultSet rs;
		
		private Nszmest (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'snp0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSnp0()
		throws SQLException {
			return rs.getString("snp0");
		}
		/**
		 * $column.remarks
		 * @return the column 'snp1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSnp1()
		throws SQLException {
			return rs.getString("snp1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp0()
		throws SQLException {
			return rs.getInt("nnp0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp1()
		throws SQLException {
			return rs.getInt("nnp1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp2()
		throws SQLException {
			return rs.getInt("nnp2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp3()
		throws SQLException {
			return rs.getInt("nnp3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp4()
		throws SQLException {
			return rs.getInt("nnp4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp5()
		throws SQLException {
			return rs.getInt("nnp5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp6()
		throws SQLException {
			return rs.getInt("nnp6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp7()
		throws SQLException {
			return rs.getInt("nnp7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp8()
		throws SQLException {
			return rs.getInt("nnp8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp9()
		throws SQLException {
			return rs.getInt("nnp9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp10()
		throws SQLException {
			return rs.getInt("nnp10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp11()
		throws SQLException {
			return rs.getInt("nnp11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp12()
		throws SQLException {
			return rs.getInt("nnp12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp13()
		throws SQLException {
			return rs.getInt("nnp13");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp14()
		throws SQLException {
			return rs.getInt("nnp14");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp15()
		throws SQLException {
			return rs.getInt("nnp15");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp16()
		throws SQLException {
			return rs.getInt("nnp16");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp17()
		throws SQLException {
			return rs.getInt("nnp17");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp18()
		throws SQLException {
			return rs.getInt("nnp18");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp19()
		throws SQLException {
			return rs.getInt("nnp19");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp20()
		throws SQLException {
			return rs.getInt("nnp20");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp21()
		throws SQLException {
			return rs.getInt("nnp21");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp22()
		throws SQLException {
			return rs.getInt("nnp22");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp23()
		throws SQLException {
			return rs.getInt("nnp23");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp24()
		throws SQLException {
			return rs.getInt("nnp24");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp25()
		throws SQLException {
			return rs.getInt("nnp25");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp26()
		throws SQLException {
			return rs.getInt("nnp26");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp27()
		throws SQLException {
			return rs.getInt("nnp27");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNnp28()
		throws SQLException {
			return rs.getBigDecimal("nnp28");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp29()
		throws SQLException {
			return rs.getInt("nnp29");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp30()
		throws SQLException {
			return rs.getInt("nnp30");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp31' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNnp31()
		throws SQLException {
			return rs.getBigDecimal("nnp31");
		}
		/**
		 * $column.remarks
		 * @return the column 'nnp32' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNnp32()
		throws SQLException {
			return rs.getInt("nnp32");
		}

	}
	
	
	/**
	 * Nszmupa
	 * 
	 */
	public class Nszmupa {
		
		private ResultSet rs;
		
		private Nszmupa (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'smu0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSmu0()
		throws SQLException {
			return rs.getString("smu0");
		}
		/**
		 * $column.remarks
		 * @return the column 'smu1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSmu1()
		throws SQLException {
			return rs.getString("smu1");
		}

	}
	
	
	/**
	 * Nszodet
	 * 
	 */
	public class Nszodet {
		
		private ResultSet rs;
		
		private Nszodet (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sod0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSod0()
		throws SQLException {
			return rs.getString("sod0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sod1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSod1()
		throws SQLException {
			return rs.getString("sod1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nod0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNod0()
		throws SQLException {
			return rs.getInt("nod0");
		}

	}
	
	
	/**
	 * Nszotpe
	 * 
	 */
	public class Nszotpe {
		
		private ResultSet rs;
		
		private Nszotpe (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sop0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSop0()
		throws SQLException {
			return rs.getString("sop0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sop1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSop1()
		throws SQLException {
			return rs.getString("sop1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nop0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNop0()
		throws SQLException {
			return rs.getInt("nop0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nop1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNop1()
		throws SQLException {
			return rs.getInt("nop1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nop2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNop2()
		throws SQLException {
			return rs.getInt("nop2");
		}

	}
	
	
	/**
	 * Nszpaga
	 * 
	 */
	public class Nszpaga {
		
		private ResultSet rs;
		
		private Nszpaga (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'spe0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpe0()
		throws SQLException {
			return rs.getString("spe0");
		}
		/**
		 * $column.remarks
		 * @return the column 'npe0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpe0()
		throws SQLException {
			return rs.getInt("npe0");
		}
		/**
		 * $column.remarks
		 * @return the column 'npe1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpe1()
		throws SQLException {
			return rs.getInt("npe1");
		}
		/**
		 * $column.remarks
		 * @return the column 'npe2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpe2()
		throws SQLException {
			return rs.getInt("npe2");
		}

	}
	
	
	/**
	 * Nszpeop
	 * 
	 */
	public class Nszpeop {
		
		private ResultSet rs;
		
		private Nszpeop (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'spp0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpp0()
		throws SQLException {
			return rs.getString("spp0");
		}
		/**
		 * $column.remarks
		 * @return the column 'spp1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpp1()
		throws SQLException {
			return rs.getString("spp1");
		}
		/**
		 * $column.remarks
		 * @return the column 'npp0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpp0()
		throws SQLException {
			return rs.getInt("npp0");
		}
		/**
		 * $column.remarks
		 * @return the column 'npp1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpp1()
		throws SQLException {
			return rs.getInt("npp1");
		}
		/**
		 * $column.remarks
		 * @return the column 'npp2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpp2()
		throws SQLException {
			return rs.getInt("npp2");
		}
		/**
		 * $column.remarks
		 * @return the column 'npp3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNpp3()
		throws SQLException {
			return rs.getInt("npp3");
		}

	}
	
	
	/**
	 * Nszpoco
	 * 
	 */
	public class Nszpoco {
		
		private ResultSet rs;
		
		private Nszpoco (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'spz0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpz0()
		throws SQLException {
			return rs.getString("spz0");
		}
		/**
		 * $column.remarks
		 * @return the column 'spz1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpz1()
		throws SQLException {
			return rs.getString("spz1");
		}
		/**
		 * $column.remarks
		 * @return the column 'npz0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNpz0()
		throws SQLException {
			return rs.getBigDecimal("npz0");
		}
		/**
		 * $column.remarks
		 * @return the column 'npz1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNpz1()
		throws SQLException {
			return rs.getBigDecimal("npz1");
		}
		/**
		 * $column.remarks
		 * @return the column 'npz2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNpz2()
		throws SQLException {
			return rs.getBigDecimal("npz2");
		}
		/**
		 * $column.remarks
		 * @return the column 'npz3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNpz3()
		throws SQLException {
			return rs.getBigDecimal("npz3");
		}

	}
	
	
	/**
	 * Nszprov
	 * 
	 */
	public class Nszprov {
		
		private ResultSet rs;
		
		private Nszprov (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'spr0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpr0()
		throws SQLException {
			return rs.getString("spr0");
		}
		/**
		 * $column.remarks
		 * @return the column 'spr1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSpr1()
		throws SQLException {
			return rs.getString("spr1");
		}

	}
	
	
	/**
	 * Nszrari
	 * 
	 */
	public class Nszrari {
		
		private ResultSet rs;
		
		private Nszrari (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sra0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSra0()
		throws SQLException {
			return rs.getString("sra0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra0()
		throws SQLException {
			return rs.getInt("nra0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra1()
		throws SQLException {
			return rs.getInt("nra1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra2()
		throws SQLException {
			return rs.getInt("nra2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra3()
		throws SQLException {
			return rs.getInt("nra3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra4()
		throws SQLException {
			return rs.getInt("nra4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra5()
		throws SQLException {
			return rs.getInt("nra5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra6()
		throws SQLException {
			return rs.getInt("nra6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra7()
		throws SQLException {
			return rs.getInt("nra7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra8()
		throws SQLException {
			return rs.getInt("nra8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra9()
		throws SQLException {
			return rs.getInt("nra9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra10()
		throws SQLException {
			return rs.getInt("nra10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra11()
		throws SQLException {
			return rs.getInt("nra11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra12()
		throws SQLException {
			return rs.getInt("nra12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra13()
		throws SQLException {
			return rs.getInt("nra13");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra14()
		throws SQLException {
			return rs.getInt("nra14");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra15()
		throws SQLException {
			return rs.getInt("nra15");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra16()
		throws SQLException {
			return rs.getInt("nra16");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra17()
		throws SQLException {
			return rs.getInt("nra17");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra18()
		throws SQLException {
			return rs.getInt("nra18");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra19()
		throws SQLException {
			return rs.getInt("nra19");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra20()
		throws SQLException {
			return rs.getInt("nra20");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra21()
		throws SQLException {
			return rs.getInt("nra21");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra22()
		throws SQLException {
			return rs.getInt("nra22");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra23()
		throws SQLException {
			return rs.getInt("nra23");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra24()
		throws SQLException {
			return rs.getInt("nra24");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra25()
		throws SQLException {
			return rs.getInt("nra25");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra26()
		throws SQLException {
			return rs.getInt("nra26");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra27()
		throws SQLException {
			return rs.getInt("nra27");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra28()
		throws SQLException {
			return rs.getInt("nra28");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra29()
		throws SQLException {
			return rs.getInt("nra29");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra30()
		throws SQLException {
			return rs.getInt("nra30");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra32' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra32()
		throws SQLException {
			return rs.getInt("nra32");
		}
		/**
		 * $column.remarks
		 * @return the column 'nra33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNra33()
		throws SQLException {
			return rs.getInt("nra33");
		}

	}
	
	
	/**
	 * Nszreac
	 * 
	 */
	public class Nszreac {
		
		private ResultSet rs;
		
		private Nszreac (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'sre0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSre0()
		throws SQLException {
			return rs.getString("sre0");
		}
		/**
		 * $column.remarks
		 * @return the column 'sre1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSre1()
		throws SQLException {
			return rs.getString("sre1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre0()
		throws SQLException {
			return rs.getInt("nre0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre1()
		throws SQLException {
			return rs.getInt("nre1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre2()
		throws SQLException {
			return rs.getInt("nre2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre3()
		throws SQLException {
			return rs.getInt("nre3");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre4()
		throws SQLException {
			return rs.getInt("nre4");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre5()
		throws SQLException {
			return rs.getInt("nre5");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre6()
		throws SQLException {
			return rs.getInt("nre6");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre7()
		throws SQLException {
			return rs.getInt("nre7");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre8()
		throws SQLException {
			return rs.getInt("nre8");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre9()
		throws SQLException {
			return rs.getInt("nre9");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre10()
		throws SQLException {
			return rs.getInt("nre10");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre11()
		throws SQLException {
			return rs.getInt("nre11");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre12()
		throws SQLException {
			return rs.getInt("nre12");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre13()
		throws SQLException {
			return rs.getInt("nre13");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre14()
		throws SQLException {
			return rs.getInt("nre14");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre15()
		throws SQLException {
			return rs.getInt("nre15");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre16()
		throws SQLException {
			return rs.getInt("nre16");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre17()
		throws SQLException {
			return rs.getInt("nre17");
		}
		/**
		 * $column.remarks
		 * @return the column 'nre18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNre18()
		throws SQLException {
			return rs.getInt("nre18");
		}

	}
	
	
	/**
	 * Nszrece
	 * 
	 */
	public class Nszrece {
		
		private ResultSet rs;
		
		private Nszrece (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'src0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSrc0()
		throws SQLException {
			return rs.getString("src0");
		}
		/**
		 * $column.remarks
		 * @return the column 'src1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSrc1()
		throws SQLException {
			return rs.getString("src1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nrc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNrc0()
		throws SQLException {
			return rs.getInt("nrc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nrc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNrc1()
		throws SQLException {
			return rs.getInt("nrc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nrc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNrc2()
		throws SQLException {
			return rs.getInt("nrc2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nrc3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNrc3()
		throws SQLException {
			return rs.getInt("nrc3");
		}

	}
	
	
	/**
	 * Nszregi
	 * 
	 */
	public class Nszregi {
		
		private ResultSet rs;
		
		private Nszregi (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'srg0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSrg0()
		throws SQLException {
			return rs.getString("srg0");
		}
		/**
		 * $column.remarks
		 * @return the column 'srg1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSrg1()
		throws SQLException {
			return rs.getString("srg1");
		}

	}
	
	
	/**
	 * Nsztido
	 * 
	 */
	public class Nsztido {
		
		private ResultSet rs;
		
		private Nsztido (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'std0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStd0()
		throws SQLException {
			return rs.getString("std0");
		}
		/**
		 * $column.remarks
		 * @return the column 'std1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStd1()
		throws SQLException {
			return rs.getString("std1");
		}

	}
	
	
	/**
	 * Nsztrab
	 * 
	 */
	public class Nsztrab {
		
		private ResultSet rs;
		
		private Nsztrab (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'str0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStr0()
		throws SQLException {
			return rs.getString("str0");
		}
		/**
		 * $column.remarks
		 * @return the column 'str1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStr1()
		throws SQLException {
			return rs.getString("str1");
		}
		/**
		 * $column.remarks
		 * @return the column 'str2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getStr2()
		throws SQLException {
			return rs.getString("str2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr0()
		throws SQLException {
			return rs.getBigDecimal("ntr0");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr1()
		throws SQLException {
			return rs.getBigDecimal("ntr1");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr2()
		throws SQLException {
			return rs.getBigDecimal("ntr2");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr3()
		throws SQLException {
			return rs.getBigDecimal("ntr3");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr4' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr4()
		throws SQLException {
			return rs.getBigDecimal("ntr4");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr5' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr5()
		throws SQLException {
			return rs.getBigDecimal("ntr5");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr6' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr6()
		throws SQLException {
			return rs.getBigDecimal("ntr6");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr7' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr7()
		throws SQLException {
			return rs.getBigDecimal("ntr7");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr8' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr8()
		throws SQLException {
			return rs.getBigDecimal("ntr8");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr9' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr9()
		throws SQLException {
			return rs.getBigDecimal("ntr9");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr10' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr10()
		throws SQLException {
			return rs.getBigDecimal("ntr10");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr11' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr11()
		throws SQLException {
			return rs.getBigDecimal("ntr11");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr12' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr12()
		throws SQLException {
			return rs.getBigDecimal("ntr12");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr13' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr13()
		throws SQLException {
			return rs.getBigDecimal("ntr13");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr14' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr14()
		throws SQLException {
			return rs.getInt("ntr14");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr15' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr15()
		throws SQLException {
			return rs.getInt("ntr15");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr16' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr16()
		throws SQLException {
			return rs.getInt("ntr16");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr17' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr17()
		throws SQLException {
			return rs.getInt("ntr17");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr18' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr18()
		throws SQLException {
			return rs.getInt("ntr18");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr19' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr19()
		throws SQLException {
			return rs.getInt("ntr19");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr20' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr20()
		throws SQLException {
			return rs.getInt("ntr20");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr21' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr21()
		throws SQLException {
			return rs.getInt("ntr21");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr22' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr22()
		throws SQLException {
			return rs.getInt("ntr22");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr23' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr23()
		throws SQLException {
			return rs.getInt("ntr23");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr24' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr24()
		throws SQLException {
			return rs.getInt("ntr24");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr25' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr25()
		throws SQLException {
			return rs.getInt("ntr25");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr26' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr26()
		throws SQLException {
			return rs.getInt("ntr26");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr27' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr27()
		throws SQLException {
			return rs.getInt("ntr27");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr28' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr28()
		throws SQLException {
			return rs.getInt("ntr28");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr29' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr29()
		throws SQLException {
			return rs.getInt("ntr29");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr30' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr30()
		throws SQLException {
			return rs.getBigDecimal("ntr30");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr31' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr31()
		throws SQLException {
			return rs.getInt("ntr31");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr32' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr32()
		throws SQLException {
			return rs.getInt("ntr32");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr33' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr33()
		throws SQLException {
			return rs.getInt("ntr33");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr34' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr34()
		throws SQLException {
			return rs.getInt("ntr34");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr35' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr35()
		throws SQLException {
			return rs.getInt("ntr35");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr36' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr36()
		throws SQLException {
			return rs.getBigDecimal("ntr36");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr37' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr37()
		throws SQLException {
			return rs.getBigDecimal("ntr37");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr38' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getNtr38()
		throws SQLException {
			return rs.getBigDecimal("ntr38");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr39' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr39()
		throws SQLException {
			return rs.getInt("ntr39");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr40' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr40()
		throws SQLException {
			return rs.getInt("ntr40");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr41' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr41()
		throws SQLException {
			return rs.getInt("ntr41");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr42' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr42()
		throws SQLException {
			return rs.getInt("ntr42");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr43' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr43()
		throws SQLException {
			return rs.getInt("ntr43");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr44' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr44()
		throws SQLException {
			return rs.getInt("ntr44");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr45' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr45()
		throws SQLException {
			return rs.getInt("ntr45");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr46' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr46()
		throws SQLException {
			return rs.getInt("ntr46");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr47' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr47()
		throws SQLException {
			return rs.getInt("ntr47");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr48' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr48()
		throws SQLException {
			return rs.getInt("ntr48");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr49' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr49()
		throws SQLException {
			return rs.getInt("ntr49");
		}
		/**
		 * $column.remarks
		 * @return the column 'ntr50' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNtr50()
		throws SQLException {
			return rs.getInt("ntr50");
		}

	}
	
	
	/**
	 * Nszunco
	 * 
	 */
	public class Nszunco {
		
		private ResultSet rs;
		
		private Nszunco (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * $column.remarks
		 * @return the column 'suc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSuc0()
		throws SQLException {
			return rs.getString("suc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'suc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSuc1()
		throws SQLException {
			return rs.getString("suc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nuc0' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNuc0()
		throws SQLException {
			return rs.getInt("nuc0");
		}
		/**
		 * $column.remarks
		 * @return the column 'nuc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNuc1()
		throws SQLException {
			return rs.getInt("nuc1");
		}
		/**
		 * $column.remarks
		 * @return the column 'nuc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNuc2()
		throws SQLException {
			return rs.getInt("nuc2");
		}
		/**
		 * $column.remarks
		 * @return the column 'nuc3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNuc3()
		throws SQLException {
			return rs.getInt("nuc3");
		}

	}
	
	
	/**
	 * Ocupacion
	 * 
	 */
	public class Ocupacion {
		
		private ResultSet rs;
		
		private Ocupacion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Exclusivo a CNAE
		 * @return the column 'exclusivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getExclusivo()
		throws SQLException {
			return rs.getString("exclusivo");
		}

		public void visitLinocupacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linocupacion WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linocupacion linocupacion = new Linocupacion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinocupacion(linocupacion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Opercepciones
	 * 
	 */
	public class Opercepciones {
		
		private ResultSet rs;
		
		private Opercepciones (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Persona
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Importe Percepción
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Base Imponible
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * Porcentaje Retención
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Importe Retención
		 * @return the column 'retencion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetencion()
		throws SQLException {
			return rs.getBigDecimal("retencion");
		}
		/**
		 * Aportacion S.S.
		 * @return the column 'aportass' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getAportass()
		throws SQLException {
			return rs.getBigDecimal("aportass");
		}

	}
	
	
	/**
	 * Opfile
	 * 
	 */
	public class Opfile {
		
		private ResultSet rs;
		
		private Opfile (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * N.I.F.
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Primer Apellido
		 * @return the column 'apellido1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellido1()
		throws SQLException {
			return rs.getString("apellido1");
		}
		/**
		 * Segundo Apellido
		 * @return the column 'apellido2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellido2()
		throws SQLException {
			return rs.getString("apellido2");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Importe de Percepcion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Importe Retencion /Ingreso a Cuenta
		 * @return the column 'retencion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetencion()
		throws SQLException {
			return rs.getBigDecimal("retencion");
		}
		/**
		 * Aportacion a Seguridad Social
		 * @return the column 'segsocial' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getSegsocial()
		throws SQLException {
			return rs.getBigDecimal("segsocial");
		}
		/**
		 * Fecha de Nacimiento
		 * @return the column 'fecnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnac()
		throws SQLException {
			return rs.getDate("fecnac");
		}

	}
	
	
	/**
	 * Otrperc
	 * 
	 */
	public class Otrperc {
		
		private ResultSet rs;
		
		private Otrperc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Percepcion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Persona
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Fecha de Pago
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Concepto de Percepcion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Clave de Percepcion
		 * @return the column 'clave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getClave()
		throws SQLException {
			return rs.getString("clave");
		}
		/**
		 * Importe de Percepcion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Base Imponible
		 * @return the column 'base' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase()
		throws SQLException {
			return rs.getBigDecimal("base");
		}
		/**
		 * Porcentaje de Retencion
		 * @return the column 'prcret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrcret()
		throws SQLException {
			return rs.getBigDecimal("prcret");
		}
		/**
		 * Importe Retencion /Ingreso a Cuenta
		 * @return the column 'retencion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetencion()
		throws SQLException {
			return rs.getBigDecimal("retencion");
		}
		/**
		 * Aportacion a Seguridad Social
		 * @return the column 'aporta_ss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getAporta_ss()
		throws SQLException {
			return rs.getBigDecimal("aporta_ss");
		}
		/**
		 * Ano Devengo
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Ingreso a Cuenta por
		 * @return the column 'ingreso' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIngreso()
		throws SQLException {
			return rs.getString("ingreso");
		}
		/**
		 * Subclave
		 * @return the column 'subclave' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSubclave()
		throws SQLException {
			return rs.getString("subclave");
		}
		/**
		 * Naturaleza de Retribucion
		 * @return the column 'natret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNatret()
		throws SQLException {
			return rs.getString("natret");
		}

	}
	
	
	/**
	 * Pagaext
	 * 
	 */
	public class Pagaext {
		
		private ResultSet rs;
		
		private Pagaext (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Convenio
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Periodo de Devengo Desde (DDMM)
		 * @return the column 'perini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPerini()
		throws SQLException {
			return rs.getString("perini");
		}
		/**
		 * Indicador Anio Desde
		 * @return the column 'indini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndini()
		throws SQLException {
			return rs.getString("indini");
		}
		/**
		 * Periodo Devengo Hasta (DDMM)
		 * @return the column 'perfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPerfin()
		throws SQLException {
			return rs.getString("perfin");
		}
		/**
		 * Indicador Anio Hasta
		 * @return the column 'indfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndfin()
		throws SQLException {
			return rs.getString("indfin");
		}
		/**
		 * Fecha de Cobro (DDMM)
		 * @return the column 'feccob' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFeccob()
		throws SQLException {
			return rs.getString("feccob");
		}
		/**
		 * Tipo de Prorrateo
		 * @return the column 'prorat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProrat()
		throws SQLException {
			return rs.getString("prorat");
		}

	}
	
	
	/**
	 * Pais
	 * 
	 */
	public class Pais {
		
		private ResultSet rs;
		
		private Pais (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Pais
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Pais
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitComunidad(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM comunidad WHERE" 
					+ " paicom = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Comunidad comunidad = new Comunidad(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitComunidad(comunidad, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " paiemi = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " paiemi = ?  "  + "AND" 					+ " paidocrep = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM persona WHERE" 
					+ " paiemi = ?  "  + "AND" 					+ " painac = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Persona persona = new Persona(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPersona(persona, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " paiemi = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Parteconf
	 * 
	 */
	public class Parteconf {
		
		private ResultSet rs;
		
		private Parteconf (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Número parte confirmación
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Número Colegiado
		 * @return the column 'numcol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumcol()
		throws SQLException {
			return rs.getString("numcol");
		}
		/**
		 * C.I.A.S.
		 * @return the column 'cias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCias()
		throws SQLException {
			return rs.getString("cias");
		}
		/**
		 * Fecha parte confirmación
		 * @return the column 'fecconf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecconf()
		throws SQLException {
			return rs.getDate("fecconf");
		}
		/**
		 * Parte procesado
		 * @return the column 'parproc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getParproc()
		throws SQLException {
			return rs.getString("parproc");
		}

	}
	
	
	/**
	 * Parteit
	 * 
	 */
	public class Parteit {
		
		private ResultSet rs;
		
		private Parteit (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Número Colegiado Baja
		 * @return the column 'numcolbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumcolbaj()
		throws SQLException {
			return rs.getString("numcolbaj");
		}
		/**
		 * C.I.A.S. Baja
		 * @return the column 'ciasbaj' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCiasbaj()
		throws SQLException {
			return rs.getString("ciasbaj");
		}
		/**
		 * Baja procesada
		 * @return the column 'bajproc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getBajproc()
		throws SQLException {
			return rs.getString("bajproc");
		}
		/**
		 * Fecha Fin Incidencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Número Colegiado Alta
		 * @return the column 'numcolalt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumcolalt()
		throws SQLException {
			return rs.getString("numcolalt");
		}
		/**
		 * C.I.A.S. Alta
		 * @return the column 'ciasalt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCiasalt()
		throws SQLException {
			return rs.getString("ciasalt");
		}
		/**
		 * Alta procesada
		 * @return the column 'altproc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAltproc()
		throws SQLException {
			return rs.getString("altproc");
		}
		/**
		 * Tipo de I.T.
		 * @return the column 'tipoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipoit()
		throws SQLException {
			return rs.getString("tipoit");
		}
		/**
		 * Recaida de Anterior I.T.
		 * @return the column 'recaida' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRecaida()
		throws SQLException {
			return rs.getString("recaida");
		}
		/**
		 * Si recaida, Fecha Inicial primera I.T.
		 * @return the column 'feciniori' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeciniori()
		throws SQLException {
			return rs.getDate("feciniori");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Base Retribucion Periodo Anterior
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Dias Periodo Anterior
		 * @return the column 'diasant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasant()
		throws SQLException {
			return rs.getInt("diasant");
		}
		/**
		 * Base Reguladora Diaria
		 * @return the column 'baseregdia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseregdia()
		throws SQLException {
			return rs.getBigDecimal("baseregdia");
		}
		/**
		 * Base Diaria contingencias Generales I.T.
		 * @return the column 'basediacg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediacg()
		throws SQLException {
			return rs.getBigDecimal("basediacg");
		}
		/**
		 * Base Diaria Accidentes Trabajo I.T.
		 * @return the column 'basediaacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediaacc()
		throws SQLException {
			return rs.getBigDecimal("basediaacc");
		}
		/**
		 * Prestacion Diaria 60 %
		 * @return the column 'prest60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest60()
		throws SQLException {
			return rs.getBigDecimal("prest60");
		}
		/**
		 * Prestacion Diaria 75 %
		 * @return the column 'prest75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest75()
		throws SQLException {
			return rs.getBigDecimal("prest75");
		}
		/**
		 * Parte Procesado (S/N)
		 * @return the column 'procesado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcesado()
		throws SQLException {
			return rs.getString("procesado");
		}
		/**
		 * Riesgo Embarazo
		 * @return the column 'riesgo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRiesgo()
		throws SQLException {
			return rs.getString("riesgo");
		}
		/**
		 * $column.remarks
		 * @return the column 'causa_alta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa_alta()
		throws SQLException {
			return rs.getString("causa_alta");
		}
		/**
		 * Fecha de accidente de trabajo o enfermedad profesional
		 * @return the column 'fecha_at' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha_at()
		throws SQLException {
			return rs.getDate("fecha_at");
		}

		public void visitParteconf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM parteconf WHERE" 
					+ " cdg = ?  "  + "AND" 					+ " fecini = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				stmt.setDate(2, this.getFecini() ); 
				rs = stmt.executeQuery();
				Parteconf parteconf = new Parteconf(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitParteconf(parteconf, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Parteitnu
	 * 
	 */
	public class Parteitnu {
		
		private ResultSet rs;
		
		private Parteitnu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Incidencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de I.T.
		 * @return the column 'tipoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipoit()
		throws SQLException {
			return rs.getString("tipoit");
		}
		/**
		 * Recaida de Anterior I.T.
		 * @return the column 'recaida' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRecaida()
		throws SQLException {
			return rs.getString("recaida");
		}
		/**
		 * Si recaida, Fecha Inicial primera I.T.
		 * @return the column 'feciniori' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFeciniori()
		throws SQLException {
			return rs.getDate("feciniori");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Base Retribucion Periodo Anterior
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Dias Periodo Anterior
		 * @return the column 'diasant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasant()
		throws SQLException {
			return rs.getInt("diasant");
		}
		/**
		 * Base Reguladora Diaria
		 * @return the column 'baseregdia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseregdia()
		throws SQLException {
			return rs.getBigDecimal("baseregdia");
		}
		/**
		 * Base Diaria contingencias Generales I.T.
		 * @return the column 'basediacg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediacg()
		throws SQLException {
			return rs.getBigDecimal("basediacg");
		}
		/**
		 * Base Diaria Accidentes Trabajo I.T.
		 * @return the column 'basediaacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediaacc()
		throws SQLException {
			return rs.getBigDecimal("basediaacc");
		}
		/**
		 * Prestacion Diaria 60 %
		 * @return the column 'prest60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest60()
		throws SQLException {
			return rs.getBigDecimal("prest60");
		}
		/**
		 * Prestacion Diaria 75 %
		 * @return the column 'prest75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest75()
		throws SQLException {
			return rs.getBigDecimal("prest75");
		}
		/**
		 * Parte Procesado (S/N)
		 * @return the column 'procesado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcesado()
		throws SQLException {
			return rs.getString("procesado");
		}
		/**
		 * Riesgo Embarazo
		 * @return the column 'riesgo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRiesgo()
		throws SQLException {
			return rs.getString("riesgo");
		}

	}
	
	
	/**
	 * Percep
	 * 
	 */
	public class Percep {
		
		private ResultSet rs;
		
		private Percep (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Numero de Percepcion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Fecha Retroactividad
		 * @return the column 'fecret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecret()
		throws SQLException {
			return rs.getDate("fecret");
		}
		/**
		 * Codigo Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Descripcion Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Descripcion Abreviada Complemento
		 * @return the column 'desabr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesabr()
		throws SQLException {
			return rs.getString("desabr");
		}
		/**
		 * Forma de Calculo
		 * @return the column 'calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCalculo()
		throws SQLException {
			return rs.getString("calculo");
		}
		/**
		 * Tipo Cotizacion
		 * @return the column 'tipcot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcot()
		throws SQLException {
			return rs.getString("tipcot");
		}
		/**
		 * Unidades Complemento
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe Unitario
		 * @return the column 'impuni' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpuni()
		throws SQLException {
			return rs.getBigDecimal("impuni");
		}
		/**
		 * Importe Complemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Mes a Aplicar Complemento (0 Todos )
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * % Garantizado I.L.T.
		 * @return the column 'garilt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getGarilt()
		throws SQLException {
			return rs.getBigDecimal("garilt");
		}
		/**
		 * Complemento para aplicar % ( forma calculo 5 )
		 * @return the column 'comapl' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getComapl()
		throws SQLException {
			return rs.getString("comapl");
		}
		/**
		 * Redondeo Paga Extra
		 * @return the column 'redext' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRedext()
		throws SQLException {
			return rs.getString("redext");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Indicador de Complemento
		 * @return the column 'indcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcom()
		throws SQLException {
			return rs.getString("indcom");
		}
		/**
		 * Tipo de Complemento
		 * @return the column 'tipcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcom()
		throws SQLException {
			return rs.getString("tipcom");
		}
		/**
		 * Retribucion dineraria o en especie
		 * @return the column 'dinesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDinesp()
		throws SQLException {
			return rs.getString("dinesp");
		}

	}
	
	
	/**
	 * Percepcion
	 * 
	 */
	public class Percepcion {
		
		private ResultSet rs;
		
		private Percepcion (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Percepción
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripción Percepción
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Tipo Percepción
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}

		public void visitLinpercepcion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linpercepcion WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linpercepcion linpercepcion = new Linpercepcion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinpercepcion(linpercepcion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Percniv
	 * 
	 */
	public class Percniv {
		
		private ResultSet rs;
		
		private Percniv (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Convenio
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Codigo de Nivel Retributivo
		 * @return the column 'nivel' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNivel()
		throws SQLException {
			return rs.getString("nivel");
		}
		/**
		 * Codigo de Complemento
		 * @return the column 'codcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcom()
		throws SQLException {
			return rs.getString("codcom");
		}
		/**
		 * Descripcion de Complemento
		 * @return the column 'descom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescom()
		throws SQLException {
			return rs.getString("descom");
		}
		/**
		 * Descripcion Abreviada de Complemento
		 * @return the column 'desabr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesabr()
		throws SQLException {
			return rs.getString("desabr");
		}
		/**
		 * Tipo Cotizacion
		 * @return the column 'tipcot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcot()
		throws SQLException {
			return rs.getString("tipcot");
		}
		/**
		 * Forma de Calculo
		 * @return the column 'calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCalculo()
		throws SQLException {
			return rs.getString("calculo");
		}
		/**
		 * Mes a Aplicar el Complemento ( 0 todos )
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Unidades Complemento
		 * @return the column 'unidades' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidades()
		throws SQLException {
			return rs.getBigDecimal("unidades");
		}
		/**
		 * Importe Unitario
		 * @return the column 'impuni' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpuni()
		throws SQLException {
			return rs.getBigDecimal("impuni");
		}
		/**
		 * Importe Complemento
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * % Garantizado I.L.T.
		 * @return the column 'garilt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getGarilt()
		throws SQLException {
			return rs.getBigDecimal("garilt");
		}
		/**
		 * Redondeo Paga Extra
		 * @return the column 'redext' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRedext()
		throws SQLException {
			return rs.getString("redext");
		}
		/**
		 * Complemento sobre el que se aplica (forma calculo 5)
		 * @return the column 'codcomapl' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcomapl()
		throws SQLException {
			return rs.getString("codcomapl");
		}
		/**
		 * Fijo o Variable
		 * @return the column 'fijovar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFijovar()
		throws SQLException {
			return rs.getString("fijovar");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Indicador de Complemento
		 * @return the column 'indcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndcom()
		throws SQLException {
			return rs.getString("indcom");
		}
		/**
		 * Tipo de Complemento
		 * @return the column 'tipcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipcom()
		throws SQLException {
			return rs.getString("tipcom");
		}
		/**
		 * Dinerario o en Especie
		 * @return the column 'dinesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDinesp()
		throws SQLException {
			return rs.getString("dinesp");
		}

	}
	
	
	/**
	 * Perfil
	 * 
	 */
	public class Perfil {
		
		private ResultSet rs;
		
		private Perfil (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Perfil Usuario
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Autorización
		 * @return the column 'autoriza' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAutoriza()
		throws SQLException {
			return rs.getString("autoriza");
		}
		/**
		 * SILCON
		 * @return the column 'silcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSilcon()
		throws SQLException {
			return rs.getString("silcon");
		}
		/**
		 * Password actual
		 * @return the column 'actpwd' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getActpwd()
		throws SQLException {
			return rs.getString("actpwd");
		}
		/**
		 * Password nuevo
		 * @return the column 'newpwd' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNewpwd()
		throws SQLException {
			return rs.getString("newpwd");
		}
		/**
		 * Fecha de Autorizacion
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Nombre del remitente
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Servidor SMTP
		 * @return the column 'servidorsmtp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getServidorsmtp()
		throws SQLException {
			return rs.getString("servidorsmtp");
		}
		/**
		 * Puerto SMTP
		 * @return the column 'puertosmtp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getPuertosmtp()
		throws SQLException {
			return rs.getInt("puertosmtp");
		}
		/**
		 * Servidor POP3
		 * @return the column 'servidorpop3' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getServidorpop3()
		throws SQLException {
			return rs.getString("servidorpop3");
		}
		/**
		 * Autenticación
		 * @return the column 'autenticacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAutenticacion()
		throws SQLException {
			return rs.getString("autenticacion");
		}
		/**
		 * Usuario
		 * @return the column 'usuario' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getUsuario()
		throws SQLException {
			return rs.getString("usuario");
		}
		/**
		 * Contraseña
		 * @return the column 'contrasena' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getContrasena()
		throws SQLException {
			return rs.getString("contrasena");
		}
		/**
		 * Correo electrónico
		 * @return the column 'correo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCorreo()
		throws SQLException {
			return rs.getString("correo");
		}

	}
	
	
	/**
	 * Persona
	 * 
	 */
	public class Persona {
		
		private ResultSet rs;
		
		private Persona (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Persona
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Tipo Documento
		 * @return the column 'inddoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddoc()
		throws SQLException {
			return rs.getString("inddoc");
		}
		/**
		 * Pais Emisor
		 * @return the column 'paiemi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPaiemi()
		throws SQLException {
			return rs.getString("paiemi");
		}
		/**
		 * Numero Documento
		 * @return the column 'numdoc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumdoc()
		throws SQLException {
			return rs.getString("numdoc");
		}
		/**
		 * Primer Apellido/Razon Social
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Segundo Apellido
		 * @return the column 'apellido2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellido2()
		throws SQLException {
			return rs.getString("apellido2");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Alias
		 * @return the column 'alias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAlias()
		throws SQLException {
			return rs.getString("alias");
		}
		/**
		 * Alias TC2
		 * @return the column 'aliastc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAliastc2()
		throws SQLException {
			return rs.getString("aliastc2");
		}
		/**
		 * Tipo de Via
		 * @return the column 'tipovia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipovia()
		throws SQLException {
			return rs.getString("tipovia");
		}
		/**
		 * Nombre de Via
		 * @return the column 'nomvia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNomvia()
		throws SQLException {
			return rs.getString("nomvia");
		}
		/**
		 * Numero
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero()
		throws SQLException {
			return rs.getString("numero");
		}
		/**
		 * Bloque, Escalera, Piso, Puerta
		 * @return the column 'otrdir' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOtrdir()
		throws SQLException {
			return rs.getString("otrdir");
		}
		/**
		 * Codigo Postal
		 * @return the column 'codpos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpos()
		throws SQLException {
			return rs.getString("codpos");
		}
		/**
		 * Localidad
		 * @return the column 'localidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLocalidad()
		throws SQLException {
			return rs.getString("localidad");
		}
		/**
		 * Provincia
		 * @return the column 'provincia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProvincia()
		throws SQLException {
			return rs.getString("provincia");
		}
		/**
		 * Telefono
		 * @return the column 'telefono' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTelefono()
		throws SQLException {
			return rs.getString("telefono");
		}
		/**
		 * Fax
		 * @return the column 'fax' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getFax()
		throws SQLException {
			return rs.getString("fax");
		}
		/**
		 * E-Mail
		 * @return the column 'email' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEmail()
		throws SQLException {
			return rs.getString("email");
		}
		/**
		 * Lugar de Nacimiento
		 * @return the column 'lugnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLugnac()
		throws SQLException {
			return rs.getString("lugnac");
		}
		/**
		 * Provincia de Nacimiento
		 * @return the column 'pronac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPronac()
		throws SQLException {
			return rs.getString("pronac");
		}
		/**
		 * Pais de Nacimiento
		 * @return the column 'painac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPainac()
		throws SQLException {
			return rs.getString("painac");
		}
		/**
		 * Fecha de Nacimiento
		 * @return the column 'fecnac' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnac()
		throws SQLException {
			return rs.getDate("fecnac");
		}
		/**
		 * Nombre del Padre
		 * @return the column 'padre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPadre()
		throws SQLException {
			return rs.getString("padre");
		}
		/**
		 * Nombre de la Madre
		 * @return the column 'madre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMadre()
		throws SQLException {
			return rs.getString("madre");
		}
		/**
		 * Nacionalidad
		 * @return the column 'nacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNacion()
		throws SQLException {
			return rs.getString("nacion");
		}
		/**
		 * Numero Seguridad Social
		 * @return the column 'numss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumss()
		throws SQLException {
			return rs.getString("numss");
		}
		/**
		 * Estado Civil
		 * @return the column 'estciv' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEstciv()
		throws SQLException {
			return rs.getString("estciv");
		}
		/**
		 * Observaciones Persona
		 * @return the column 'obsper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getObsper()
		throws SQLException {
			return rs.getString("obsper");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Sexo
		 * @return the column 'sexo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSexo()
		throws SQLException {
			return rs.getString("sexo");
		}

		public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprper WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprper emprper = new Emprper(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprper(emprper, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitOtrperc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM otrperc WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Otrperc otrperc = new Otrperc(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitOtrperc(otrperc, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitLintc2(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lintc2 WHERE" 
					+ " codper = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lintc2 lintc2 = new Lintc2(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLintc2(lintc2, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " persona = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Pluses
	 * 
	 */
	public class Pluses {
		
		private ResultSet rs;
		
		private Pluses (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Plus
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripción de Plus
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitLinplus(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linplus WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linplus linplus = new Linplus(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinplus(linplus, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Porcoti
	 * 
	 */
	public class Porcoti {
		
		private ResultSet rs;
		
		private Porcoti (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Porcentaje
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Orden de Porcentaje
		 * @return the column 'ordpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrdpct()
		throws SQLException {
			return rs.getInt("ordpct");
		}
		/**
		 * Descripcion de Porcentaje
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitLinporco(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linporco WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linporco linporco = new Linporco(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinporco(linporco, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTipocont(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM tipocont WHERE" 
					+ " codpct = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Tipocont tipocont = new Tipocont(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTipocont(tipocont, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codpct = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCostes(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM costes WHERE" 
					+ " codpct = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Costes costes = new Costes(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCostes(costes, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Prcdivnom
	 * 
	 */
	public class Prcdivnom {
		
		private ResultSet rs;
		
		private Prcdivnom (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Nomina
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Porcentaje
		 * @return the column 'prc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc()
		throws SQLException {
			return rs.getBigDecimal("prc");
		}
		/**
		 * Texto
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}

	}
	
	
	/**
	 * Prcdivtrab
	 * 
	 */
	public class Prcdivtrab {
		
		private ResultSet rs;
		
		private Prcdivtrab (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Porcentaje
		 * @return the column 'prc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc()
		throws SQLException {
			return rs.getBigDecimal("prc");
		}
		/**
		 * Texto
		 * @return the column 'texto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTexto()
		throws SQLException {
			return rs.getString("texto");
		}

	}
	
	
	/**
	 * Prestaciones
	 * 
	 */
	public class Prestaciones {
		
		private ResultSet rs;
		
		private Prestaciones (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Centro de Trabajo
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}

		public void visitLinprestacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linprestacion WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linprestacion linprestacion = new Linprestacion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinprestacion(linprestacion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Printers
	 * 
	 */
	public class Printers {
		
		private ResultSet rs;
		
		private Printers (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Impresora
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Impresora
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

	}
	
	
	/**
	 * Procesos
	 * 
	 */
	public class Procesos {
		
		private ResultSet rs;
		
		private Procesos (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Delegacion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Tipo de Proceso
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Fecha del Proceso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Hora del Proceso
		 * @return the column 'hora' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHora()
		throws SQLException {
			return rs.getTime("hora");
		}

	}
	
	
	/**
	 * Provincia
	 * 
	 */
	public class Provincia {
		
		private ResultSet rs;
		
		private Provincia (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Provincia
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Provincia
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Codigo de Comunidad
		 * @return the column 'compro' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCompro()
		throws SQLException {
			return rs.getString("compro");
		}

		public void visitDelegacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM delegacion WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Delegacion delegacion = new Delegacion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitDelegacion(delegacion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitDomicilio(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM domicilio WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Domicilio domicilio = new Domicilio(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitDomicilio(domicilio, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM persona WHERE" 
					+ " provincia = ?  "  + "AND" 					+ " pronac = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Persona persona = new Persona(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPersona(persona, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr11x(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr11x WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr11x impr11x = new Impr11x(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr11x(impr11x, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitImpr190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM impr190 WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Impr190 impr190 = new Impr190(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitImpr190(impr190, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitOpfile(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM opfile WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Opfile opfile = new Opfile(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitOpfile(opfile, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " pronac = ?  "  + "AND" 					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " provincia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Regidocu
	 * 
	 */
	public class Regidocu {
		
		private ResultSet rs;
		
		private Regidocu (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Registro de Documento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Codigo de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Tipo de Documento
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Fecha de Efecto
		 * @return the column 'fechaefe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFechaefe()
		throws SQLException {
			return rs.getDate("fechaefe");
		}
		/**
		 * Fecha de Entrega
		 * @return the column 'fechaent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFechaent()
		throws SQLException {
			return rs.getDate("fechaent");
		}
		/**
		 * Fecha de Presentacion
		 * @return the column 'fechapre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFechapre()
		throws SQLException {
			return rs.getDate("fechapre");
		}
		/**
		 * Lugar de Presentacion
		 * @return the column 'lugar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLugar()
		throws SQLException {
			return rs.getString("lugar");
		}
		/**
		 * Observaciones
		 * @return the column 'observa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getObserva()
		throws SQLException {
			return rs.getString("observa");
		}
		/**
		 * Fecha de Envio
		 * @return the column 'fechaenv' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFechaenv()
		throws SQLException {
			return rs.getDate("fechaenv");
		}
		/**
		 * Tipo de Domicilio
		 * @return the column 'tipdom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipdom()
		throws SQLException {
			return rs.getString("tipdom");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Rem_cert_empr
	 * 
	 */
	public class Rem_cert_empr {
		
		private ResultSet rs;
		
		private Rem_cert_empr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico del certificado de empresa de la remesa
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Identificador unico de la empresa
		 * @return the column 'empresa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getEmpresa()
		throws SQLException {
			return rs.getInt("empresa");
		}
		/**
		 * Fecha de la ultima remesa en la que fue incluido
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Estado del certificado correspondiente a la ultima respuesta
		 * @return the column 'estado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getEstado()
		throws SQLException {
			return rs.getInt("estado");
		}
		/**
		 * Huella digital del archivo de respuesta
		 * @return the column 'huella' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHuella()
		throws SQLException {
			return rs.getString("huella");
		}

	}
	
	
	/**
	 * Rem_cert_empr_det
	 * 
	 */
	public class Rem_cert_empr_det {
		
		private ResultSet rs;
		
		private Rem_cert_empr_det (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico del certificado de empresa de la remesa
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Identificador unico del certificado de empresa
		 * @return the column 'rem_cert_empr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRem_cert_empr()
		throws SQLException {
			return rs.getInt("rem_cert_empr");
		}
		/**
		 * Identificador unico del empleado
		 * @return the column 'empleado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getEmpleado()
		throws SQLException {
			return rs.getInt("empleado");
		}
		/**
		 * Fecha de baja del empleado
		 * @return the column 'fecha_baja' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha_baja()
		throws SQLException {
			return rs.getDate("fecha_baja");
		}
		/**
		 * Causa de la suspension del empleado
		 * @return the column 'causa_suspension' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa_suspension()
		throws SQLException {
			return rs.getString("causa_suspension");
		}

	}
	
	
	/**
	 * Remesa_inss
	 * 
	 */
	public class Remesa_inss {
		
		private ResultSet rs;
		
		private Remesa_inss (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico de la Remesa
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Fecha de la remesa
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Hora de la remesa
		 * @return the column 'hora' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHora()
		throws SQLException {
			return rs.getTime("hora");
		}

		public void visitRemesa_parte_it(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM remesa_parte_it WHERE" 
					+ " remesa_inss = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Remesa_parte_it remesa_parte_it = new Remesa_parte_it(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRemesa_parte_it(remesa_parte_it, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Remesa_parte_it
	 * 
	 */
	public class Remesa_parte_it {
		
		private ResultSet rs;
		
		private Remesa_parte_it (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico del parte de it de la remesa
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Identificador unico de la Remesa
		 * @return the column 'remesa_inss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRemesa_inss()
		throws SQLException {
			return rs.getInt("remesa_inss");
		}
		/**
		 * Codigo de Trabajador
		 * @return the column 'empleado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getEmpleado()
		throws SQLException {
			return rs.getInt("empleado");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecha_baja' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha_baja()
		throws SQLException {
			return rs.getDate("fecha_baja");
		}
		/**
		 * Fecha del parte
		 * @return the column 'fecha_parte' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha_parte()
		throws SQLException {
			return rs.getDate("fecha_parte");
		}
		/**
		 * Número Colegiado
		 * @return the column 'numero_colegiado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumero_colegiado()
		throws SQLException {
			return rs.getString("numero_colegiado");
		}
		/**
		 * C.I.A.S.
		 * @return the column 'cias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCias()
		throws SQLException {
			return rs.getString("cias");
		}
		/**
		 * Tipo del parte de it, alta, baja o confirmacion
		 * @return the column 'tipo_parte_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTipo_parte_it()
		throws SQLException {
			return rs.getInt("tipo_parte_it");
		}
		/**
		 * Número parte confirmación
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Tipo de I.T.
		 * @return the column 'tipo_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTipo_it()
		throws SQLException {
			return rs.getInt("tipo_it");
		}
		/**
		 * Recaida de Anterior I.T.
		 * @return the column 'recaida' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRecaida()
		throws SQLException {
			return rs.getInt("recaida");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'prorrateo_coti' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getProrrateo_coti()
		throws SQLException {
			return rs.getInt("prorrateo_coti");
		}
		/**
		 * Base Retribucion Periodo Anterior
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Dias Periodo Anterior
		 * @return the column 'diasant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasant()
		throws SQLException {
			return rs.getInt("diasant");
		}
		/**
		 * Base Reguladora Diaria
		 * @return the column 'baseregdia' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseregdia()
		throws SQLException {
			return rs.getBigDecimal("baseregdia");
		}
		/**
		 * Base Diaria contingencias Generales I.T.
		 * @return the column 'basediacg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediacg()
		throws SQLException {
			return rs.getBigDecimal("basediacg");
		}
		/**
		 * Base Diaria Accidentes Trabajo I.T.
		 * @return the column 'basediaacc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBasediaacc()
		throws SQLException {
			return rs.getBigDecimal("basediaacc");
		}
		/**
		 * Prestacion Diaria 60 %
		 * @return the column 'prest60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest60()
		throws SQLException {
			return rs.getBigDecimal("prest60");
		}
		/**
		 * Prestacion Diaria 75 %
		 * @return the column 'prest75' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrest75()
		throws SQLException {
			return rs.getBigDecimal("prest75");
		}
		/**
		 * Parte Procesado (S/N)
		 * @return the column 'procesado' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getProcesado()
		throws SQLException {
			return rs.getInt("procesado");
		}
		/**
		 * Riesgo Embarazo
		 * @return the column 'riesgo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRiesgo()
		throws SQLException {
			return rs.getInt("riesgo");
		}

	}
	
	
	/**
	 * Remesaafi
	 * 
	 */
	public class Remesaafi {
		
		private ResultSet rs;
		
		private Remesaafi (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Código de Centro de Trabajo
		 * @return the column 'coddom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddom()
		throws SQLException {
			return rs.getInt("coddom");
		}
		/**
		 * Código de Trabajador
		 * @return the column 'codtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodtra()
		throws SQLException {
			return rs.getInt("codtra");
		}
		/**
		 * Fecha Alta
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Fecha de Proceso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Accion
		 * @return the column 'accion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAccion()
		throws SQLException {
			return rs.getString("accion");
		}
		/**
		 * Situacion
		 * @return the column 'situacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSituacion()
		throws SQLException {
			return rs.getString("situacion");
		}
		/**
		 * Orden de Accion
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Tipo de remesa
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}

	}
	
	
	/**
	 * Remesainss
	 * 
	 */
	public class Remesainss {
		
		private ResultSet rs;
		
		private Remesainss (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Código de Centro de Trabajo
		 * @return the column 'coddom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddom()
		throws SQLException {
			return rs.getInt("coddom");
		}
		/**
		 * Código de Trabajador
		 * @return the column 'codtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodtra()
		throws SQLException {
			return rs.getInt("codtra");
		}
		/**
		 * Fecha
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Accion
		 * @return the column 'accion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAccion()
		throws SQLException {
			return rs.getString("accion");
		}
		/**
		 * Causa
		 * @return the column 'causa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCausa()
		throws SQLException {
			return rs.getString("causa");
		}
		/**
		 * Fecha Inicio parte
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}

	}
	
	
	/**
	 * Session
	 * 
	 */
	public class Session {
		
		private ResultSet rs;
		
		private Session (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Fecha de finalización
		 * @return the column 'enddate' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Timestamp getEnddate()
		throws SQLException {
			return rs.getTimestamp("enddate");
		}
		/**
		 * IP Remota
		 * @return the column 'remote_address' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRemote_address()
		throws SQLException {
			return rs.getString("remote_address");
		}
		/**
		 * Equipo Remoto
		 * @return the column 'remote_host' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRemote_host()
		throws SQLException {
			return rs.getString("remote_host");
		}
		/**
		 * Identificador web de la sesión
		 * @return the column 'session_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSession_id()
		throws SQLException {
			return rs.getString("session_id");
		}
		/**
		 * Fecha de inicio
		 * @return the column 'startdate' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Timestamp getStartdate()
		throws SQLException {
			return rs.getTimestamp("startdate");
		}
		/**
		 * Identificador de la Aplicación
		 * @return the column 'application_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getApplication_id()
		throws SQLException {
			return rs.getInt("application_id");
		}
		/**
		 * Identificador del Usuario
		 * @return the column 'user_id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getUser_id()
		throws SQLException {
			return rs.getInt("user_id");
		}

		public void visitAction_entry(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM action_entry WHERE" 
					+ " session_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Action_entry action_entry = new Action_entry(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAction_entry(action_entry, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Sincomun
	 * 
	 */
	public class Sincomun {
		
		private ResultSet rs;
		
		private Sincomun (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha de proceso
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}

	}
	
	
	/**
	 * Sucursal
	 * 
	 */
	public class Sucursal {
		
		private ResultSet rs;
		
		private Sucursal (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Entidad
		 * @return the column 'codent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodent()
		throws SQLException {
			return rs.getString("codent");
		}
		/**
		 * Codigo de Sucursal
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Domicilio de Sucursal
		 * @return the column 'domsuc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDomsuc()
		throws SQLException {
			return rs.getString("domsuc");
		}
		/**
		 * Municipio de Sucursal
		 * @return the column 'munsuc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMunsuc()
		throws SQLException {
			return rs.getString("munsuc");
		}
		/**
		 * Codigo Postal de Sucursal
		 * @return the column 'cpsuc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCpsuc()
		throws SQLException {
			return rs.getString("cpsuc");
		}

		public void visitEmprban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprban WHERE" 
					+ " codent = ?  "  + "AND" 					+ " codsuc = ?  " 				);
				stmt.setString(1, this.getCodent() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprban emprban = new Emprban(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprban(emprban, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codent = ?  "  + "AND" 					+ " codsuc = ?  " 				);
				stmt.setString(1, this.getCodent() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " entidad = ?  "  + "AND" 					+ " sucursal = ?  " 				);
				stmt.setString(1, this.getCodent() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " entidad = ?  "  + "AND" 					+ " sucursal = ?  " 				);
				stmt.setString(1, this.getCodent() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tc1
	 * 
	 */
	public class Tc1 {
		
		private ResultSet rs;
		
		private Tc1 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de TC1
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Tipo Cuenta Cotizacion
		 * @return the column 'codccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodccc()
		throws SQLException {
			return rs.getString("codccc");
		}
		/**
		 * Numero Trabajadores
		 * @return the column 'numtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumtra()
		throws SQLException {
			return rs.getInt("numtra");
		}
		/**
		 * Mes Desde
		 * @return the column 'desde_mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDesde_mes()
		throws SQLException {
			return rs.getInt("desde_mes");
		}
		/**
		 * Anio Desde
		 * @return the column 'desde_anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDesde_anio()
		throws SQLException {
			return rs.getInt("desde_anio");
		}
		/**
		 * Mes Hasta
		 * @return the column 'hasta_mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHasta_mes()
		throws SQLException {
			return rs.getInt("hasta_mes");
		}
		/**
		 * Anio Hasta
		 * @return the column 'hasta_anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHasta_anio()
		throws SQLException {
			return rs.getInt("hasta_anio");
		}
		/**
		 * Mutua Patronal
		 * @return the column 'mutuaccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMutuaccc()
		throws SQLException {
			return rs.getString("mutuaccc");
		}
		/**
		 * Base Contingencias Comunes
		 * @return the column 'base_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_concom()
		throws SQLException {
			return rs.getBigDecimal("base_concom");
		}
		/**
		 * % Contingencias Comunes
		 * @return the column 'prc_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_concom()
		throws SQLException {
			return rs.getBigDecimal("prc_concom");
		}
		/**
		 * Cuota Contingencias Comunes
		 * @return the column 'cuota_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_concom()
		throws SQLException {
			return rs.getBigDecimal("cuota_concom");
		}
		/**
		 * Base Horas Extras No Estruct.
		 * @return the column 'base_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hexno()
		throws SQLException {
			return rs.getBigDecimal("base_hexno");
		}
		/**
		 * % Horas Extras No Estruct.
		 * @return the column 'prc_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hexno()
		throws SQLException {
			return rs.getBigDecimal("prc_hexno");
		}
		/**
		 * Cuota Horas Extras No Estruct.
		 * @return the column 'cuota_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_hexno()
		throws SQLException {
			return rs.getBigDecimal("cuota_hexno");
		}
		/**
		 * Base Horas Extras Estruct.
		 * @return the column 'base_hexest' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hexest()
		throws SQLException {
			return rs.getBigDecimal("base_hexest");
		}
		/**
		 * % Horas Extras Estruct.
		 * @return the column 'prc_hexest' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_hexest()
		throws SQLException {
			return rs.getBigDecimal("prc_hexest");
		}
		/**
		 * Cuota Horas Extras Estruct.
		 * @return the column 'cuota_hexest' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_hexest()
		throws SQLException {
			return rs.getBigDecimal("cuota_hexest");
		}
		/**
		 * Reducciones IT
		 * @return the column 'base_redit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_redit()
		throws SQLException {
			return rs.getBigDecimal("base_redit");
		}
		/**
		 * Reducciones Contingencias Comunes
		 * @return the column 'base_redcc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_redcc()
		throws SQLException {
			return rs.getBigDecimal("base_redcc");
		}
		/**
		 * Suma Bases Reducciones
		 * @return the column 'base_reducc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_reducc()
		throws SQLException {
			return rs.getBigDecimal("base_reducc");
		}
		/**
		 * Liquido Cotizaciones Generales
		 * @return the column 'liq_cotgen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiq_cotgen()
		throws SQLException {
			return rs.getBigDecimal("liq_cotgen");
		}
		/**
		 * Base Accidentes Trabajo
		 * @return the column 'base_acctra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acctra()
		throws SQLException {
			return rs.getBigDecimal("base_acctra");
		}
		/**
		 * Suma Cuotas IT
		 * @return the column 'cuotas_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuotas_it()
		throws SQLException {
			return rs.getBigDecimal("cuotas_it");
		}
		/**
		 * Suma Cuotas IMS
		 * @return the column 'cuotas_ims' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuotas_ims()
		throws SQLException {
			return rs.getBigDecimal("cuotas_ims");
		}
		/**
		 * Suma cuotas IT e IMS
		 * @return the column 'cuotas_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuotas_acc()
		throws SQLException {
			return rs.getBigDecimal("cuotas_acc");
		}
		/**
		 * Compensacion IT, Acc.Tra. Enf.Prof.
		 * @return the column 'comp_it' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getComp_it()
		throws SQLException {
			return rs.getBigDecimal("comp_it");
		}
		/**
		 * Liquido Acc. Trabajo
		 * @return the column 'liq_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiq_acc()
		throws SQLException {
			return rs.getBigDecimal("liq_acc");
		}
		/**
		 * % Desempleo
		 * @return the column 'prc_desem' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_desem()
		throws SQLException {
			return rs.getBigDecimal("prc_desem");
		}
		/**
		 * Cuota Desempleo
		 * @return the column 'cuota_desem' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_desem()
		throws SQLException {
			return rs.getBigDecimal("cuota_desem");
		}
		/**
		 * Bonif. Reducc. INEM
		 * @return the column 'red_inem' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRed_inem()
		throws SQLException {
			return rs.getBigDecimal("red_inem");
		}
		/**
		 * Liquido Otras Cotizaciones
		 * @return the column 'liq_otras' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getLiq_otras()
		throws SQLException {
			return rs.getBigDecimal("liq_otras");
		}
		/**
		 * Base Recargo de Mora
		 * @return the column 'base_mora' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_mora()
		throws SQLException {
			return rs.getBigDecimal("base_mora");
		}
		/**
		 * % recargo de mora
		 * @return the column 'prc_mora' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_mora()
		throws SQLException {
			return rs.getBigDecimal("prc_mora");
		}
		/**
		 * Cuota Recargo de mora
		 * @return the column 'cuota_mora' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_mora()
		throws SQLException {
			return rs.getBigDecimal("cuota_mora");
		}
		/**
		 * Resultado TC1
		 * @return the column 'importe_tc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte_tc1()
		throws SQLException {
			return rs.getBigDecimal("importe_tc1");
		}
		/**
		 * Base Aportacion Servicios Comunes
		 * @return the column 'base_servcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_servcom()
		throws SQLException {
			return rs.getBigDecimal("base_servcom");
		}
		/**
		 * Porcentaje Aportacion Servicios Comunes
		 * @return the column 'prc_servcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_servcom()
		throws SQLException {
			return rs.getBigDecimal("prc_servcom");
		}
		/**
		 * Cuota Aportacion Servicios Comunes
		 * @return the column 'cuota_servcom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_servcom()
		throws SQLException {
			return rs.getBigDecimal("cuota_servcom");
		}
		/**
		 * Base Deducciones Colaboracion Voluntaria
		 * @return the column 'base_dedcol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_dedcol()
		throws SQLException {
			return rs.getBigDecimal("base_dedcol");
		}
		/**
		 * Porcentaje Deducciones Colaboracion Voluntaria
		 * @return the column 'prc_dedcol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_dedcol()
		throws SQLException {
			return rs.getBigDecimal("prc_dedcol");
		}
		/**
		 * Cuota Deducciones Colaboracion Voluntaria
		 * @return the column 'cuota_dedcol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_dedcol()
		throws SQLException {
			return rs.getBigDecimal("cuota_dedcol");
		}
		/**
		 * Numero Empresa Persona
		 * @return the column 'numero' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumero()
		throws SQLException {
			return rs.getInt("numero");
		}
		/**
		 * Codigo Persona
		 * @return the column 'codper' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodper()
		throws SQLException {
			return rs.getInt("codper");
		}
		/**
		 * Dias Alta
		 * @return the column 'dias' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDias()
		throws SQLException {
			return rs.getInt("dias");
		}
		/**
		 * Grupo Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Contrato TC2
		 * @return the column 'codtc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodtc2()
		throws SQLException {
			return rs.getString("codtc2");
		}
		/**
		 * Epigrafe
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * Numero de Horas
		 * @return the column 'horas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHoras()
		throws SQLException {
			return rs.getInt("horas");
		}
		/**
		 * Dias IT
		 * @return the column 'diasit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasit()
		throws SQLException {
			return rs.getInt("diasit");
		}
		/**
		 * Dias Maternidad
		 * @return the column 'diasmat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiasmat()
		throws SQLException {
			return rs.getInt("diasmat");
		}
		/**
		 * Base Accidentes
		 * @return the column 'base_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acc()
		throws SQLException {
			return rs.getBigDecimal("base_acc");
		}
		/**
		 * Fecha Concesion Bonif.
		 * @return the column 'fecha' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecha()
		throws SQLException {
			return rs.getDate("fecha");
		}
		/**
		 * Situaciones Especiales
		 * @return the column 'sitesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSitesp()
		throws SQLException {
			return rs.getString("sitesp");
		}
		/**
		 * Nombre
		 * @return the column 'nombre' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNombre()
		throws SQLException {
			return rs.getString("nombre");
		}
		/**
		 * Apellidos
		 * @return the column 'apellidos' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getApellidos()
		throws SQLException {
			return rs.getString("apellidos");
		}
		/**
		 * Indicador de impresion de tc2 1
		 * @return the column 'mostrar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMostrar()
		throws SQLException {
			return rs.getString("mostrar");
		}
		/**
		 * Clave Reducción
		 * @return the column 'cdgred' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdgred()
		throws SQLException {
			return rs.getString("cdgred");
		}
		/**
		 * Desglose de cuotas
		 * @return the column 'desglose' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesglose()
		throws SQLException {
			return rs.getString("desglose");
		}
		/**
		 * Comision Mutua
		 * @return the column 'comision' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getComision()
		throws SQLException {
			return rs.getString("comision");
		}
		/**
		 * Tipo de TC1
		 * @return the column 'tipotc1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipotc1()
		throws SQLException {
			return rs.getString("tipotc1");
		}
		/**
		 * Codigo de TC2
		 * @return the column 'tc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getTc2()
		throws SQLException {
			return rs.getInt("tc2");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Código Otros Conceptos
		 * @return the column 'cdg_otrcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg_otrcon()
		throws SQLException {
			return rs.getString("cdg_otrcon");
		}
		/**
		 * Base Otros Conceptos
		 * @return the column 'base_otrcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_otrcon()
		throws SQLException {
			return rs.getBigDecimal("base_otrcon");
		}
		/**
		 * % Otros Conceptos
		 * @return the column 'prc_otrcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_otrcon()
		throws SQLException {
			return rs.getBigDecimal("prc_otrcon");
		}
		/**
		 * Cuota Otros Conceptos
		 * @return the column 'cuota_otrcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_otrcon()
		throws SQLException {
			return rs.getBigDecimal("cuota_otrcon");
		}
		/**
		 * Base Contingencias Comunes Cotización Empresarial
		 * @return the column 'base_concom_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_concom_ce()
		throws SQLException {
			return rs.getBigDecimal("base_concom_ce");
		}
		/**
		 * % Contingencias Comunes Cotización Empresarial
		 * @return the column 'prc_concom_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_concom_ce()
		throws SQLException {
			return rs.getBigDecimal("prc_concom_ce");
		}
		/**
		 * Cuota Contingencias Comunes Cotización Empresarial
		 * @return the column 'cuota_concom_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_concom_ce()
		throws SQLException {
			return rs.getBigDecimal("cuota_concom_ce");
		}
		/**
		 * Base Desempleo Cotización Empresarial
		 * @return the column 'base_desem_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_desem_ce()
		throws SQLException {
			return rs.getBigDecimal("base_desem_ce");
		}
		/**
		 * % Desempleo Cotización Empresarial
		 * @return the column 'prc_desem_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_desem_ce()
		throws SQLException {
			return rs.getBigDecimal("prc_desem_ce");
		}
		/**
		 * Cuota Desempleo Cotización Empresarial
		 * @return the column 'cuota_desem_ce' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCuota_desem_ce()
		throws SQLException {
			return rs.getBigDecimal("cuota_desem_ce");
		}
		/**
		 * Base Desempleo
		 * @return the column 'base_desem' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_desem()
		throws SQLException {
			return rs.getBigDecimal("base_desem");
		}
		/**
		 * Horas Complementarias
		 * @return the column 'horcomp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHorcomp()
		throws SQLException {
			return rs.getInt("horcomp");
		}
		/**
		 * Importe Horas Complementarias
		 * @return the column 'impcomp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpcomp()
		throws SQLException {
			return rs.getBigDecimal("impcomp");
		}
		/**
		 * Horas Presenciales
		 * @return the column 'horpres' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHorpres()
		throws SQLException {
			return rs.getInt("horpres");
		}
		/**
		 * Importe Horas Presenciales
		 * @return the column 'imppres' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImppres()
		throws SQLException {
			return rs.getBigDecimal("imppres");
		}
		/**
		 * Horas Distancia
		 * @return the column 'hordist' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getHordist()
		throws SQLException {
			return rs.getInt("hordist");
		}
		/**
		 * Importe Horas Distancia
		 * @return the column 'impdist' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImpdist()
		throws SQLException {
			return rs.getBigDecimal("impdist");
		}
		/**
		 * Régimen
		 * @return the column 'indregimen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndregimen()
		throws SQLException {
			return rs.getString("indregimen");
		}

	}
	
	
	/**
	 * Tc2
	 * 
	 */
	public class Tc2 {
		
		private ResultSet rs;
		
		private Tc2 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de TC2
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Codigo de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Tipo Cuenta Cotizacion
		 * @return the column 'codccc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodccc()
		throws SQLException {
			return rs.getString("codccc");
		}
		/**
		 * Convenio Colectivo
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Numero Trabajadores
		 * @return the column 'numtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNumtra()
		throws SQLException {
			return rs.getInt("numtra");
		}
		/**
		 * Mes
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Anio
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes
		 * @return the column 'mesref' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMesref()
		throws SQLException {
			return rs.getInt("mesref");
		}
		/**
		 * Año
		 * @return the column 'anioref' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnioref()
		throws SQLException {
			return rs.getInt("anioref");
		}
		/**
		 * Tipo Liquidacion
		 * @return the column 'tipo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipo()
		throws SQLException {
			return rs.getString("tipo");
		}
		/**
		 * Contingencias Comunes
		 * @return the column 'base_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_concom()
		throws SQLException {
			return rs.getBigDecimal("base_concom");
		}
		/**
		 * Accidentes Trabajo
		 * @return the column 'base_acctra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_acctra()
		throws SQLException {
			return rs.getBigDecimal("base_acctra");
		}
		/**
		 * Horas Extraordinarias Fuerza Mayor ( no Estruc )
		 * @return the column 'base_hexno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hexno()
		throws SQLException {
			return rs.getBigDecimal("base_hexno");
		}
		/**
		 * Horas Extraordinarias Estructurales
		 * @return the column 'base_hexest' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_hexest()
		throws SQLException {
			return rs.getBigDecimal("base_hexest");
		}
		/**
		 * Contig. Comunes Cot. Empresarial
		 * @return the column 'base_cccemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_cccemp()
		throws SQLException {
			return rs.getBigDecimal("base_cccemp");
		}
		/**
		 * Otras Cotizaciones Cot. Empresarial
		 * @return the column 'base_occemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBase_occemp()
		throws SQLException {
			return rs.getBigDecimal("base_occemp");
		}
		/**
		 * Compensaciones IT Enfermedad Comun
		 * @return the column 'comp_ecal' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getComp_ecal()
		throws SQLException {
			return rs.getBigDecimal("comp_ecal");
		}
		/**
		 * Compensaciones IT Accidentes Trabajo
		 * @return the column 'comp_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getComp_acc()
		throws SQLException {
			return rs.getBigDecimal("comp_acc");
		}
		/**
		 * Reducciones contingencias Comunes
		 * @return the column 'red_concom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRed_concom()
		throws SQLException {
			return rs.getBigDecimal("red_concom");
		}
		/**
		 * Bonif. Redudcc. INEM
		 * @return the column 'red_inem' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRed_inem()
		throws SQLException {
			return rs.getBigDecimal("red_inem");
		}
		/**
		 * Indicador de Impresion
		 * @return the column 'imprime' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getImprime()
		throws SQLException {
			return rs.getString("imprime");
		}
		/**
		 * Divisa
		 * @return the column 'divisa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDivisa()
		throws SQLException {
			return rs.getString("divisa");
		}
		/**
		 * Se ha insertado en el fichero FAN
		 * @return the column 'tc2red' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTc2red()
		throws SQLException {
			return rs.getString("tc2red");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Régimen
		 * @return the column 'indregimen' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndregimen()
		throws SQLException {
			return rs.getString("indregimen");
		}

		public void visitLintc2epi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lintc2epi WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lintc2epi lintc2epi = new Lintc2epi(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLintc2epi(lintc2epi, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitLintc2(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM lintc2 WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Lintc2 lintc2 = new Lintc2(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLintc2(lintc2, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipaut
	 * 
	 */
	public class Tipaut {
		
		private ResultSet rs;
		
		private Tipaut (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Tipo de Autorización
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Tipo de Autorización
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " autorizacion = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipboni
	 * 
	 */
	public class Tipboni {
		
		private ResultSet rs;
		
		private Tipboni (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Bonificacion
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Descripcion de Bonificacion
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Forma de Calculo
		 * @return the column 'calculo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCalculo()
		throws SQLException {
			return rs.getString("calculo");
		}
		/**
		 * % Bonificacion Contingencias Generales
		 * @return the column 'prc_cg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_cg()
		throws SQLException {
			return rs.getBigDecimal("prc_cg");
		}
		/**
		 * % Bonificacion Accidentes
		 * @return the column 'prc_acc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_acc()
		throws SQLException {
			return rs.getBigDecimal("prc_acc");
		}
		/**
		 * % bonificacion Base Conjunto
		 * @return the column 'prc_accfgs' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrc_accfgs()
		throws SQLException {
			return rs.getBigDecimal("prc_accfgs");
		}
		/**
		 * Bonificación S.S.
		 * @return the column 'boniss' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getBoniss()
		throws SQLException {
			return rs.getString("boniss");
		}
		/**
		 * Mayores de 60 años y mas de 5 años de Antigüedad
		 * @return the column 'mayor60' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMayor60()
		throws SQLException {
			return rs.getString("mayor60");
		}
		/**
		 * Real Decreto Ley 5/2006
		 * @return the column 'rdl052006' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRdl052006()
		throws SQLException {
			return rs.getString("rdl052006");
		}
		/**
		 * Restar I.T.
		 * @return the column 'restait' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRestait()
		throws SQLException {
			return rs.getString("restait");
		}

		public void visitBonifica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM bonifica WHERE" 
					+ " cdg = ?  " 				);
				stmt.setInt(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Bonifica bonifica = new Bonifica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitBonifica(bonifica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipcotc2
	 * 
	 */
	public class Tipcotc2 {
		
		private ResultSet rs;
		
		private Tipcotc2 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Contrato TC2
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Contrato TC2
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Descripcion Abreviada de Contrato TC2
		 * @return the column 'desabr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesabr()
		throws SQLException {
			return rs.getString("desabr");
		}
		/**
		 * Antiguo Codigo de Contrato TC2
		 * @return the column 'cdgant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdgant()
		throws SQLException {
			return rs.getString("cdgant");
		}

		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codtc2 = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " contratotc2 = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipdoc
	 * 
	 */
	public class Tipdoc {
		
		private ResultSet rs;
		
		private Tipdoc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Tipo de Documento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Tipo de Documento
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " inddoc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " inddoc = ?  "  + "AND" 					+ " tipdocrep = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				stmt.setString(2, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM persona WHERE" 
					+ " inddoc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Persona persona = new Persona(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPersona(persona, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitComunica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM comunica WHERE" 
					+ " tipodoc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Comunica comunica = new Comunica(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitComunica(comunica, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " inddoc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipempr
	 * 
	 */
	public class Tipempr {
		
		private ResultSet rs;
		
		private Tipempr (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Tipo de Empresario
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Tipo de Empresario
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " tipemp = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM emprnif WHERE" 
					+ " tipempr = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Emprnif emprnif = new Emprnif(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitEmprnif(emprnif, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipinc
	 * 
	 */
	public class Tipinc {
		
		private ResultSet rs;
		
		private Tipinc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Incidencia
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Incidencia
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Indica si resta dias en el calculo de la nomina
		 * @return the column 'indresta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndresta()
		throws SQLException {
			return rs.getString("indresta");
		}
		/**
		 * Indica si Descuenta Dias en Paga Extra
		 * @return the column 'inddto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddto()
		throws SQLException {
			return rs.getString("inddto");
		}

		public void visitTrabinci(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabinci WHERE" 
					+ " codinc = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabinci trabinci = new Trabinci(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabinci(trabinci, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipocnae
	 * 
	 */
	public class Tipocnae {
		
		private ResultSet rs;
		
		private Tipocnae (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Título de la actividad económica
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

	}
	
	
	/**
	 * Tipocnae2009
	 * 
	 */
	public class Tipocnae2009 {
		
		private ResultSet rs;
		
		private Tipocnae2009 (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * CNAE
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Título de la actividad económica
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Sección
		 * @return the column 'seccion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getSeccion()
		throws SQLException {
			return rs.getString("seccion");
		}

	}
	
	
	/**
	 * Tipocont
	 * 
	 */
	public class Tipocont {
		
		private ResultSet rs;
		
		private Tipocont (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Contrato Interno
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Contrato Interno
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Asimilado a % Cotizacion
		 * @return the column 'codpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpct()
		throws SQLException {
			return rs.getString("codpct");
		}
		/**
		 * Desempleado
		 * @return the column 'desemple' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDesemple()
		throws SQLException {
			return rs.getString("desemple");
		}
		/**
		 * Mujer subrepresentada
		 * @return the column 'mujersub' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getMujersub()
		throws SQLException {
			return rs.getString("mujersub");
		}
		/**
		 * Incapacitado readmitido
		 * @return the column 'incaread' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIncaread()
		throws SQLException {
			return rs.getString("incaread");
		}
		/**
		 * Primer trabajador contratado por autonomo
		 * @return the column 'primertra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPrimertra()
		throws SQLException {
			return rs.getString("primertra");
		}
		/**
		 * Grado de minusvalia
		 * @return the column 'gradomin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getGradomin()
		throws SQLException {
			return rs.getInt("gradomin");
		}
		/**
		 * Exclusion social
		 * @return the column 'excsocial' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getExcsocial()
		throws SQLException {
			return rs.getString("excsocial");
		}

		public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM trabajo WHERE" 
					+ " codcont = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Trabajo trabajo = new Trabajo(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitTrabajo(trabajo, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " contrato = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tiposdoc
	 * 
	 */
	public class Tiposdoc {
		
		private ResultSet rs;
		
		private Tiposdoc (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Registro de Documento
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Registro de Documentos
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

	}
	
	
	/**
	 * Tipovia
	 * 
	 */
	public class Tipovia {
		
		private ResultSet rs;
		
		private Tipovia (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Tipo de Via
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Tipo de Via
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitDelegacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM delegacion WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Delegacion delegacion = new Delegacion(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitDelegacion(delegacion, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM cliente WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Cliente cliente = new Cliente(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitCliente(cliente, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitDomicilio(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM domicilio WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Domicilio domicilio = new Domicilio(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitDomicilio(domicilio, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM persona WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Persona persona = new Persona(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitPersona(persona, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitOpfile(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM opfile WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Opfile opfile = new Opfile(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitOpfile(opfile, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM httrabajador WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Httrabajador httrabajador = new Httrabajador(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitHttrabajador(httrabajador, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM autonomos WHERE" 
					+ " tipovia = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Autonomos autonomos = new Autonomos(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAutonomos(autonomos, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Tipreg
	 * 
	 */
	public class Tipreg {
		
		private ResultSet rs;
		
		private Tipreg (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Tipo de Registro
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripcion de Tipo de Registro
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM regidocu WHERE" 
					+ " tipo = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Regidocu regidocu = new Regidocu(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitRegidocu(regidocu, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Trabajadores
	 * 
	 */
	public class Trabajadores {
		
		private ResultSet rs;
		
		private Trabajadores (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Código de Centro de Trabajo
		 * @return the column 'coddom' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCoddom()
		throws SQLException {
			return rs.getInt("coddom");
		}
		/**
		 * Código de Trabajador
		 * @return the column 'codtra' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodtra()
		throws SQLException {
			return rs.getInt("codtra");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Campo
		 * @return the column 'campo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCampo()
		throws SQLException {
			return rs.getString("campo");
		}

	}
	
	
	/**
	 * Trabajo
	 * 
	 */
	public class Trabajo {
		
		private ResultSet rs;
		
		private Trabajo (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Fecha de Antiguedad
		 * @return the column 'fecant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecant()
		throws SQLException {
			return rs.getDate("fecant");
		}
		/**
		 * Cuenta de Cargo
		 * @return the column 'ctacar' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCtacar()
		throws SQLException {
			return rs.getString("ctacar");
		}
		/**
		 * Profesion
		 * @return the column 'profesion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProfesion()
		throws SQLException {
			return rs.getString("profesion");
		}
		/**
		 * Codigo de Convenio
		 * @return the column 'codcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcon()
		throws SQLException {
			return rs.getString("codcon");
		}
		/**
		 * Categoria Laboral
		 * @return the column 'codcat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcat()
		throws SQLException {
			return rs.getString("codcat");
		}
		/**
		 * Nivel Retributivo
		 * @return the column 'nivel' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNivel()
		throws SQLException {
			return rs.getString("nivel");
		}
		/**
		 * Descripcion Categoria
		 * @return the column 'descat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescat()
		throws SQLException {
			return rs.getString("descat");
		}
		/**
		 * Grupo de Tarifa
		 * @return the column 'codbas' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodbas()
		throws SQLException {
			return rs.getString("codbas");
		}
		/**
		 * Epigrafe
		 * @return the column 'codepi' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodepi()
		throws SQLException {
			return rs.getString("codepi");
		}
		/**
		 * % Retencion IRPF en Nomina
		 * @return the column 'irpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIrpf()
		throws SQLException {
			return rs.getBigDecimal("irpf");
		}
		/**
		 * Codigo Nacional Ocupaciones
		 * @return the column 'cno' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCno()
		throws SQLException {
			return rs.getString("cno");
		}
		/**
		 * Prorrateo Cotizacion
		 * @return the column 'procot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProcot()
		throws SQLException {
			return rs.getString("procot");
		}
		/**
		 * Prorrateo Retribucion
		 * @return the column 'proret' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getProret()
		throws SQLException {
			return rs.getString("proret");
		}
		/**
		 * Numero de Matricula
		 * @return the column 'nummat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getNummat()
		throws SQLException {
			return rs.getInt("nummat");
		}
		/**
		 * Codigo de Contrato
		 * @return the column 'codcont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcont()
		throws SQLException {
			return rs.getString("codcont");
		}
		/**
		 * Tipo Contrato TC2
		 * @return the column 'codtc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodtc2()
		throws SQLException {
			return rs.getString("codtc2");
		}
		/**
		 * Descripcion Contrato TC2
		 * @return the column 'destc2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDestc2()
		throws SQLException {
			return rs.getString("destc2");
		}
		/**
		 * Fecha Inicio Contrato
		 * @return the column 'fecinicont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecinicont()
		throws SQLException {
			return rs.getDate("fecinicont");
		}
		/**
		 * Fecha Fin Contrato
		 * @return the column 'fecfincont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfincont()
		throws SQLException {
			return rs.getDate("fecfincont");
		}
		/**
		 * Duracion Contrato (Dias)
		 * @return the column 'diascont' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiascont()
		throws SQLException {
			return rs.getInt("diascont");
		}
		/**
		 * Autorización para trabajar
		 * @return the column 'autorizacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAutorizacion()
		throws SQLException {
			return rs.getString("autorizacion");
		}
		/**
		 * Fecha autorización
		 * @return the column 'fecaut' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecaut()
		throws SQLException {
			return rs.getDate("fecaut");
		}
		/**
		 * Entidad Bancaria
		 * @return the column 'codent' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodent()
		throws SQLException {
			return rs.getString("codent");
		}
		/**
		 * Sucursal Bancaria
		 * @return the column 'codsuc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodsuc()
		throws SQLException {
			return rs.getString("codsuc");
		}
		/**
		 * Numero Cuenta Bancaria
		 * @return the column 'numcta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getNumcta()
		throws SQLException {
			return rs.getString("numcta");
		}
		/**
		 * Numero de Autorizacion Pluriempleo
		 * @return the column 'plunumaut' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getPlunumaut()
		throws SQLException {
			return rs.getString("plunumaut");
		}
		/**
		 * Fecha Autorizacion Pluriempleo
		 * @return the column 'plufecaut' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getPlufecaut()
		throws SQLException {
			return rs.getDate("plufecaut");
		}
		/**
		 * Pluriempleo: % sobre Tope Minimo Cotizacion
		 * @return the column 'pluprcmin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPluprcmin()
		throws SQLException {
			return rs.getBigDecimal("pluprcmin");
		}
		/**
		 * Pluriempleo: % sobre Tope Maximo Cotizacion
		 * @return the column 'pluprcmax' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPluprcmax()
		throws SQLException {
			return rs.getBigDecimal("pluprcmax");
		}
		/**
		 * Coeficiente Reductor Salarios
		 * @return the column 'coered' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getCoered()
		throws SQLException {
			return rs.getBigDecimal("coered");
		}
		/**
		 * Minutos Jornada Semanal Real
		 * @return the column 'semana' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSemana()
		throws SQLException {
			return rs.getInt("semana");
		}
		/**
		 * Minutos Jornada Semanal Tiempo Parcial
		 * @return the column 'semanatp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSemanatp()
		throws SQLException {
			return rs.getInt("semanatp");
		}
		/**
		 * Cantidad Minutos/Dias Cotizacion Tiempo Parcial
		 * @return the column 'cantp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCantp()
		throws SQLException {
			return rs.getInt("cantp");
		}
		/**
		 * Base Calculo Antiguedad
		 * @return the column 'baseant' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getBaseant()
		throws SQLException {
			return rs.getBigDecimal("baseant");
		}
		/**
		 * Toma Fecha Alta como Fecha Antiguedad para Pagas
		 * @return the column 'indalt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndalt()
		throws SQLException {
			return rs.getString("indalt");
		}
		/**
		 * Descontar Dias IT
		 * @return the column 'inddtoit' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddtoit()
		throws SQLException {
			return rs.getString("inddtoit");
		}
		/**
		 * Descontar Dias Incidencias
		 * @return the column 'inddtootr' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getInddtootr()
		throws SQLException {
			return rs.getString("inddtootr");
		}
		/**
		 * Asimilado a % Cotizacion
		 * @return the column 'codpct' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodpct()
		throws SQLException {
			return rs.getString("codpct");
		}
		/**
		 * Indicador Tiempo de Contrato
		 * @return the column 'indtp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndtp()
		throws SQLException {
			return rs.getString("indtp");
		}
		/**
		 * Indicador IRPF
		 * @return the column 'indirpf' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndirpf()
		throws SQLException {
			return rs.getString("indirpf");
		}
		/**
		 * Codigo Convenio Colectivo TC2
		 * @return the column 'concol' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getConcol()
		throws SQLException {
			return rs.getInt("concol");
		}
		/**
		 * Actualizar Percepciones segun Convenio
		 * @return the column 'indactcon' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndactcon()
		throws SQLException {
			return rs.getString("indactcon");
		}
		/**
		 * Marca Trabajador Especial
		 * @return the column 'especial' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getEspecial()
		throws SQLException {
			return rs.getString("especial");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Digito de Control Cuenta Bancaria
		 * @return the column 'dc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDc()
		throws SQLException {
			return rs.getString("dc");
		}
		/**
		 * Historico de Modificaciones
		 * @return the column 'historico' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getHistorico()
		throws SQLException {
			return rs.getString("historico");
		}
		/**
		 * Res. y Per. Ceuta Melilla
		 * @return the column 'indceutamelilla' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndceutamelilla()
		throws SQLException {
			return rs.getString("indceutamelilla");
		}
		/**
		 * Colectivo
		 * @return the column 'colectivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getColectivo()
		throws SQLException {
			return rs.getString("colectivo");
		}
		/**
		 * Relación Laboral
		 * @return the column 'relacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getRelacion()
		throws SQLException {
			return rs.getString("relacion");
		}
		/**
		 * Ocupación 1993 Rev.1
		 * @return the column 'ocupacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOcupacion()
		throws SQLException {
			return rs.getString("ocupacion");
		}
		/**
		 * Ocupación 2009
		 * @return the column 'ocupacion2009' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getOcupacion2009()
		throws SQLException {
			return rs.getString("ocupacion2009");
		}
		/**
		 * Indicador de tipo de tiempo de contrato
		 * @return the column 'tipotp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getTipotp()
		throws SQLException {
			return rs.getString("tipotp");
		}
		/**
		 * Dias de tiempo de contrato
		 * @return the column 'diastp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiastp()
		throws SQLException {
			return rs.getInt("diastp");
		}

	}
	
	
	/**
	 * Trabdto
	 * 
	 */
	public class Trabdto {
		
		private ResultSet rs;
		
		private Trabdto (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Numero de Orden
		 * @return the column 'orden' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getOrden()
		throws SQLException {
			return rs.getInt("orden");
		}
		/**
		 * Fecha Inicio Vigencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Vigencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Numero de Linea en Nomina
		 * @return the column 'linea' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getLinea()
		throws SQLException {
			return rs.getInt("linea");
		}
		/**
		 * Concepto de Minoracion
		 * @return the column 'concepto' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getConcepto()
		throws SQLException {
			return rs.getString("concepto");
		}
		/**
		 * Nominas a las que afecta
		 * @return the column 'afecta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAfecta()
		throws SQLException {
			return rs.getString("afecta");
		}
		/**
		 * Importe Minoracion
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}
		/**
		 * Importe a Indicar
		 * @return the column 'indimp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getIndimp()
		throws SQLException {
			return rs.getString("indimp");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}

	}
	
	
	/**
	 * Trabinci
	 * 
	 */
	public class Trabinci {
		
		private ResultSet rs;
		
		private Trabinci (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Codigo de Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Fecha Inicio Incidencia
		 * @return the column 'fecini' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecini()
		throws SQLException {
			return rs.getDate("fecini");
		}
		/**
		 * Fecha Fin Incidencia
		 * @return the column 'fecfin' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecfin()
		throws SQLException {
			return rs.getDate("fecfin");
		}
		/**
		 * Tipo de Incidencia
		 * @return the column 'codinc' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodinc()
		throws SQLException {
			return rs.getString("codinc");
		}
		/**
		 * Horas, Dias
		 * @return the column 'cantidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCantidad()
		throws SQLException {
			return rs.getInt("cantidad");
		}
		/**
		 * Fecha Creacion Fila
		 * @return the column 'fecnew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecnew()
		throws SQLException {
			return rs.getDate("fecnew");
		}
		/**
		 * Hora Creacion Fila
		 * @return the column 'hornew' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHornew()
		throws SQLException {
			return rs.getTime("hornew");
		}
		/**
		 * Fecha Modificacion Fila
		 * @return the column 'fecmod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Date getFecmod()
		throws SQLException {
			return rs.getDate("fecmod");
		}
		/**
		 * Hora Modificacion Fila
		 * @return the column 'hormod' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Time getHormod()
		throws SQLException {
			return rs.getTime("hormod");
		}
		/**
		 * Importe
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Unidades
	 * 
	 */
	public class Unidades {
		
		private ResultSet rs;
		
		private Unidades (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Unidades
		 * @return the column 'unidad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getUnidad()
		throws SQLException {
			return rs.getBigDecimal("unidad");
		}
		/**
		 * Importe Unitario
		 * @return the column 'importe' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getImporte()
		throws SQLException {
			return rs.getBigDecimal("importe");
		}

	}
	
	
	/**
	 * Usuario
	 * 
	 */
	public class Usuario {
		
		private ResultSet rs;
		
		private Usuario (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Identificador unico
		 * @return the column 'id' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getId()
		throws SQLException {
			return rs.getInt("id");
		}
		/**
		 * Nombre del Usuario
		 * @return the column 'name' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getName()
		throws SQLException {
			return rs.getString("name");
		}
		/**
		 * Login del Usuario
		 * @return the column 'login' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getLogin()
		throws SQLException {
			return rs.getString("login");
		}
		/**
		 * Indica si el Usuario esta disponible o no
		 * @return the column 'available' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAvailable()
		throws SQLException {
			return rs.getInt("available");
		}
		/**
		 * Indica si el Usuario requiere validacion o no de la clave hardwa
		 * @return the column 'validate' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getValidate()
		throws SQLException {
			return rs.getInt("validate");
		}
		/**
		 * Campo alfanumerico donde se guarda la ultima clave hardware gene
		 * @return the column 'aon_key' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getAon_key()
		throws SQLException {
			return rs.getString("aon_key");
		}
		/**
		 * Estado del Usuario con respecto a su primera validacion de la cl
		 * @return the column 'status' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getStatus()
		throws SQLException {
			return rs.getInt("status");
		}

		public void visitSession(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM session WHERE" 
					+ " user_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Session session = new Session(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitSession(session, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAction_favorite(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM action_favorite WHERE" 
					+ " user_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Action_favorite action_favorite = new Action_favorite(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAction_favorite(action_favorite, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
		public void visitAction_denied(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM action_denied WHERE" 
					+ " user_id = ?  " 				);
				stmt.setInt(1, this.getId() ); 
				rs = stmt.executeQuery();
				Action_denied action_denied = new Action_denied(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitAction_denied(action_denied, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Variables
	 * 
	 */
	public class Variables {
		
		private ResultSet rs;
		
		private Variables (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código de Categoria Laboral
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCdg()
		throws SQLException {
			return rs.getString("cdg");
		}
		/**
		 * Descripción de Categoria
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}

		public void visitLinvariables(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
			ResultSet 			rs 	= null;
			PreparedStatement	stmt 	= null;
			boolean				resume 	= true;
	
			try {
				stmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM linvariables WHERE" 
					+ " cdg = ?  " 				);
				stmt.setString(1, this.getCdg() ); 
				rs = stmt.executeQuery();
				Linvariables linvariables = new Linvariables(rs); 
				while ( resume && rs.next() ) {
					resume = ctsqlDBVisitor.visitLinvariables(linvariables, this);
				}
			}
			finally {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
			}
		}
	}
	
	
	/**
	 * Variaciones
	 * 
	 */
	public class Variaciones {
		
		private ResultSet rs;
		
		private Variaciones (ResultSet rs) 
		throws SQLException {
			this.rs =rs;
		}
		
		/**
		 * Código Trabajador
		 * @return the column 'cdg' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCdg()
		throws SQLException {
			return rs.getInt("cdg");
		}
		/**
		 * Año de Variación
		 * @return the column 'anio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getAnio()
		throws SQLException {
			return rs.getInt("anio");
		}
		/**
		 * Mes de Variación
		 * @return the column 'mes' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMes()
		throws SQLException {
			return rs.getInt("mes");
		}
		/**
		 * Código de Cliente
		 * @return the column 'codcli' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodcli()
		throws SQLException {
			return rs.getInt("codcli");
		}
		/**
		 * Código de Empresa
		 * @return the column 'codemp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodemp()
		throws SQLException {
			return rs.getInt("codemp");
		}
		/**
		 * Código de Actividad
		 * @return the column 'codact' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getCodact()
		throws SQLException {
			return rs.getInt("codact");
		}
		/**
		 * Código de Domicilio
		 * @return the column 'domicilio' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDomicilio()
		throws SQLException {
			return rs.getInt("domicilio");
		}
		/**
		 * Código de Categoría
		 * @return the column 'codcat' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getCodcat()
		throws SQLException {
			return rs.getString("codcat");
		}
		/**
		 * Apellidos y Nombre
		 * @return the column 'descripcion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public String getDescripcion()
		throws SQLException {
			return rs.getString("descripcion");
		}
		/**
		 * Horas Extras
		 * @return the column 'horaext' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getHoraext()
		throws SQLException {
			return rs.getBigDecimal("horaext");
		}
		/**
		 * Inventario de Tienda
		 * @return the column 'inventariot' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getInventariot()
		throws SQLException {
			return rs.getInt("inventariot");
		}
		/**
		 * Medio Festivo
		 * @return the column 'mediofestivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getMediofestivo()
		throws SQLException {
			return rs.getInt("mediofestivo");
		}
		/**
		 * Festivo
		 * @return the column 'festivo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getFestivo()
		throws SQLException {
			return rs.getInt("festivo");
		}
		/**
		 * Sustitución 2º Jefe de Tienda
		 * @return the column 'sustitucion2jt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSustitucion2jt()
		throws SQLException {
			return rs.getInt("sustitucion2jt");
		}
		/**
		 * Sustitución Jefe de Tienda
		 * @return the column 'sustitucionjt' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getSustitucionjt()
		throws SQLException {
			return rs.getInt("sustitucionjt");
		}
		/**
		 * Cajero más rápido
		 * @return the column 'rapidez1' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRapidez1()
		throws SQLException {
			return rs.getInt("rapidez1");
		}
		/**
		 * Cajero segundo más rápido
		 * @return the column 'rapidez2' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRapidez2()
		throws SQLException {
			return rs.getInt("rapidez2");
		}
		/**
		 * Impedir un Robo
		 * @return the column 'robo' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getRobo()
		throws SQLException {
			return rs.getInt("robo");
		}
		/**
		 * Días de Enfermedad
		 * @return the column 'diaenfermedad' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getDiaenfermedad()
		throws SQLException {
			return rs.getInt("diaenfermedad");
		}
		/**
		 * Inventario de Almacén
		 * @return the column 'inventarioa' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public Integer getInventarioa()
		throws SQLException {
			return rs.getInt("inventarioa");
		}
		/**
		 * Retribución en Especie
		 * @return the column 'retribucionesp' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getRetribucionesp()
		throws SQLException {
			return rs.getBigDecimal("retribucionesp");
		}
		/**
		 * Ingreso a Cuenta
		 * @return the column 'ingresocta' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIngresocta()
		throws SQLException {
			return rs.getBigDecimal("ingresocta");
		}
		/**
		 * Prestación
		 * @return the column 'prestacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getPrestacion()
		throws SQLException {
			return rs.getBigDecimal("prestacion");
		}
		/**
		 * Incorporación
		 * @return the column 'incorporacion' value; if the value is SQL NULL, the value returned is null
		 * @throws SQLException
		 */
		public BigDecimal getIncorporacion()
		throws SQLException {
			return rs.getBigDecimal("incorporacion");
		}

	}
	


	public void visitAction(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Action" );
			Action action = new Action(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAction(action);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAction_denied(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Action_denied" );
			Action_denied action_denied = new Action_denied(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAction_denied(action_denied);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAction_entry(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Action_entry" );
			Action_entry action_entry = new Action_entry(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAction_entry(action_entry);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAction_favorite(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Action_favorite" );
			Action_favorite action_favorite = new Action_favorite(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAction_favorite(action_favorite);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAdmon(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Admon" );
			Admon admon = new Admon(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAdmon(admon);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAjustes(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Ajustes" );
			Ajustes ajustes = new Ajustes(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAjustes(ajustes);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitApplication(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Application" );
			Application application = new Application(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitApplication(application);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAutbases(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Autbases" );
			Autbases autbases = new Autbases(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAutbases(autbases);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAutomat(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Automat" );
			Automat automat = new Automat(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAutomat(automat);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAutonomos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Autonomos" );
			Autonomos autonomos = new Autonomos(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAutonomos(autonomos);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitAvisos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Avisos" );
			Avisos avisos = new Avisos(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitAvisos(avisos);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitBasecoti(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Basecoti" );
			Basecoti basecoti = new Basecoti(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitBasecoti(basecoti);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitBonifica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Bonifica" );
			Bonifica bonifica = new Bonifica(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitBonifica(bonifica);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCalculo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Calculo" );
			Calculo calculo = new Calculo(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCalculo(calculo);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCalen(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Calen" );
			Calen calen = new Calen(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCalen(calen);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCalendar(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Calendar" );
			Calendar calendar = new Calendar(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCalendar(calendar);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCalfiniquito(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Calfiniquito" );
			Calfiniquito calfiniquito = new Calfiniquito(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCalfiniquito(calfiniquito);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCategoria(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Categoria" );
			Categoria categoria = new Categoria(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCategoria(categoria);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCliente(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cliente" );
			Cliente cliente = new Cliente(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCliente(cliente);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCnae(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cnae" );
			Cnae cnae = new Cnae(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCnae(cnae);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCnae2009(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cnae2009" );
			Cnae2009 cnae2009 = new Cnae2009(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCnae2009(cnae2009);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitColectivos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Colectivos" );
			Colectivos colectivos = new Colectivos(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitColectivos(colectivos);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitComplemento(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Complemento" );
			Complemento complemento = new Complemento(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitComplemento(complemento);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitComplevar(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Complevar" );
			Complevar complevar = new Complevar(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitComplevar(complevar);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitComunica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Comunica" );
			Comunica comunica = new Comunica(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitComunica(comunica);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitComunidad(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Comunidad" );
			Comunidad comunidad = new Comunidad(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitComunidad(comunidad);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitConfig(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Config" );
			Config config = new Config(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitConfig(config);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitConvenio(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Convenio" );
			Convenio convenio = new Convenio(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitConvenio(convenio);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCostes(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Costes" );
			Costes costes = new Costes(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCostes(costes);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCuota(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cuota" );
			Cuota cuota = new Cuota(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCuota(cuota);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCuota_01(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cuota_01" );
			Cuota_01 cuota_01 = new Cuota_01(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCuota_01(cuota_01);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCuota_20(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cuota_20" );
			Cuota_20 cuota_20 = new Cuota_20(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCuota_20(cuota_20);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCuota_31(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cuota_31" );
			Cuota_31 cuota_31 = new Cuota_31(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCuota_31(cuota_31);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitCuota_48(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Cuota_48" );
			Cuota_48 cuota_48 = new Cuota_48(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitCuota_48(cuota_48);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitDatosafi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Datosafi" );
			Datosafi datosafi = new Datosafi(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitDatosafi(datosafi);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitDelegacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Delegacion" );
			Delegacion delegacion = new Delegacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitDelegacion(delegacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitDetalle(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Detalle" );
			Detalle detalle = new Detalle(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitDetalle(detalle);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitDivisa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Divisa" );
			Divisa divisa = new Divisa(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitDivisa(divisa);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitDomicilio(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Domicilio" );
			Domicilio domicilio = new Domicilio(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitDomicilio(domicilio);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitElemcoti(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Elemcoti" );
			Elemcoti elemcoti = new Elemcoti(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitElemcoti(elemcoti);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitElemirpf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Elemirpf" );
			Elemirpf elemirpf = new Elemirpf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitElemirpf(elemirpf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmbargo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Embargo" );
			Embargo embargo = new Embargo(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmbargo(embargo);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmpract(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Empract" );
			Empract empract = new Empract(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmpract(empract);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprban" );
			Emprban emprban = new Emprban(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprban(emprban);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprccc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprccc" );
			Emprccc emprccc = new Emprccc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprccc(emprccc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprccos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprccos" );
			Emprccos emprccos = new Emprccos(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprccos(emprccos);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprctra(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprctra" );
			Emprctra emprctra = new Emprctra(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprctra(emprctra);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprdom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprdom" );
			Emprdom emprdom = new Emprdom(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprdom(emprdom);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmpresa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Empresa" );
			Empresa empresa = new Empresa(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmpresa(empresa);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprlban(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprlban" );
			Emprlban emprlban = new Emprlban(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprlban(emprlban);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprnif(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprnif" );
			Emprnif emprnif = new Emprnif(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprnif(emprnif);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEmprper(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Emprper" );
			Emprper emprper = new Emprper(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEmprper(emprper);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEntidad(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Entidad" );
			Entidad entidad = new Entidad(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEntidad(entidad);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitEpigrafe(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Epigrafe" );
			Epigrafe epigrafe = new Epigrafe(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitEpigrafe(epigrafe);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitExclusion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Exclusion" );
			Exclusion exclusion = new Exclusion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitExclusion(exclusion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinidto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finidto" );
			Finidto finidto = new Finidto(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinidto(finidto);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinidtodf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finidtodf" );
			Finidtodf finidtodf = new Finidtodf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinidtodf(finidtodf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinidtonu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finidtonu" );
			Finidtonu finidtonu = new Finidtonu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinidtonu(finidtonu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinindem(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finindem" );
			Finindem finindem = new Finindem(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinindem(finindem);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinindemdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finindemdf" );
			Finindemdf finindemdf = new Finindemdf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinindemdf(finindemdf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinindemnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finindemnu" );
			Finindemnu finindemnu = new Finindemnu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinindemnu(finindemnu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinipext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finipext" );
			Finipext finipext = new Finipext(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinipext(finipext);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinipextdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finipextdf" );
			Finipextdf finipextdf = new Finipextdf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinipextdf(finipextdf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFinipextnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finipextnu" );
			Finipextnu finipextnu = new Finipextnu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFinipextnu(finipextnu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFiniquito(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finiquito" );
			Finiquito finiquito = new Finiquito(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFiniquito(finiquito);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFiniquitodf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finiquitodf" );
			Finiquitodf finiquitodf = new Finiquitodf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFiniquitodf(finiquitodf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitFiniquitonu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Finiquitonu" );
			Finiquitonu finiquitonu = new Finiquitonu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitFiniquitonu(finiquitonu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitHttaviso(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Httaviso" );
			Httaviso httaviso = new Httaviso(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitHttaviso(httaviso);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitHttbonificacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Httbonificacion" );
			Httbonificacion httbonificacion = new Httbonificacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitHttbonificacion(httbonificacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitHttcomplemento(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Httcomplemento" );
			Httcomplemento httcomplemento = new Httcomplemento(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitHttcomplemento(httcomplemento);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitHttincidencia(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Httincidencia" );
			Httincidencia httincidencia = new Httincidencia(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitHttincidencia(httincidencia);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitHttrabajador(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Httrabajador" );
			Httrabajador httrabajador = new Httrabajador(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitHttrabajador(httrabajador);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitImpr11x(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Impr11x" );
			Impr11x impr11x = new Impr11x(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitImpr11x(impr11x);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitImpr190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Impr190" );
			Impr190 impr190 = new Impr190(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitImpr190(impr190);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLbonifica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lbonifica" );
			Lbonifica lbonifica = new Lbonifica(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLbonifica(lbonifica);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLcomunica(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lcomunica" );
			Lcomunica lcomunica = new Lcomunica(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLcomunica(lcomunica);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLin190(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lin190" );
			Lin190 lin190 = new Lin190(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLin190(lin190);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLin_divisa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lin_divisa" );
			Lin_divisa lin_divisa = new Lin_divisa(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLin_divisa(lin_divisa);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinautom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linautom" );
			Linautom linautom = new Linautom(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinautom(linautom);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinbasec(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linbasec" );
			Linbasec linbasec = new Linbasec(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinbasec(linbasec);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLincalcu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lincalcu" );
			Lincalcu lincalcu = new Lincalcu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLincalcu(lincalcu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLincnae(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lincnae" );
			Lincnae lincnae = new Lincnae(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLincnae(lincnae);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLincnae2009(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lincnae2009" );
			Lincnae2009 lincnae2009 = new Lincnae2009(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLincnae2009(lincnae2009);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLincomun(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lincomun" );
			Lincomun lincomun = new Lincomun(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLincomun(lincomun);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinelem(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linelem" );
			Linelem linelem = new Linelem(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinelem(linelem);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinepigr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linepigr" );
			Linepigr linepigr = new Linepigr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinepigr(linepigr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinirpf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linirpf" );
			Linirpf linirpf = new Linirpf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinirpf(linirpf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinmutua(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linmutua" );
			Linmutua linmutua = new Linmutua(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinmutua(linmutua);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinocupacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linocupacion" );
			Linocupacion linocupacion = new Linocupacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinocupacion(linocupacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinpercepcion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linpercepcion" );
			Linpercepcion linpercepcion = new Linpercepcion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinpercepcion(linpercepcion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinplus(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linplus" );
			Linplus linplus = new Linplus(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinplus(linplus);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinporco(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linporco" );
			Linporco linporco = new Linporco(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinporco(linporco);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinprestacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linprestacion" );
			Linprestacion linprestacion = new Linprestacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinprestacion(linprestacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLintc2(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lintc2" );
			Lintc2 lintc2 = new Lintc2(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLintc2(lintc2);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLintc2epi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Lintc2epi" );
			Lintc2epi lintc2epi = new Lintc2epi(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLintc2epi(lintc2epi);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitLinvariables(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Linvariables" );
			Linvariables linvariables = new Linvariables(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitLinvariables(linvariables);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMasivo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Masivo" );
			Masivo masivo = new Masivo(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMasivo(masivo);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMinor_01(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Minor_01" );
			Minor_01 minor_01 = new Minor_01(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMinor_01(minor_01);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMinor_20(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Minor_20" );
			Minor_20 minor_20 = new Minor_20(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMinor_20(minor_20);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMinor_31(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Minor_31" );
			Minor_31 minor_31 = new Minor_31(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMinor_31(minor_31);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMinor_48(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Minor_48" );
			Minor_48 minor_48 = new Minor_48(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMinor_48(minor_48);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMinora(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Minora" );
			Minora minora = new Minora(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMinora(minora);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitMutua(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Mutua" );
			Mutua mutua = new Mutua(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitMutua(mutua);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nacion" );
			Nacion nacion = new Nacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNacion(nacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNivel(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nivel" );
			Nivel nivel = new Nivel(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNivel(nivel);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomdfdev(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomdfdev" );
			Nomdfdev nomdfdev = new Nomdfdev(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomdfdev(nomdfdev);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomdfdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomdfdto" );
			Nomdfdto nomdfdto = new Nomdfdto(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomdfdto(nomdfdto);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomdfdtoex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomdfdtoex" );
			Nomdfdtoex nomdfdtoex = new Nomdfdtoex(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomdfdtoex(nomdfdtoex);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomdto" );
			Nomdto nomdto = new Nomdto(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomdto(nomdto);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomdtoex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomdtoex" );
			Nomdtoex nomdtoex = new Nomdtoex(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomdtoex(nomdtoex);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNomina(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nomina" );
			Nomina nomina = new Nomina(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNomina(nomina);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominadev(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominadev" );
			Nominadev nominadev = new Nominadev(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominadev(nominadev);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominadf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominadf" );
			Nominadf nominadf = new Nominadf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominadf(nominadf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominaex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominaex" );
			Nominaex nominaex = new Nominaex(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominaex(nominaex);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominaexdf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominaexdf" );
			Nominaexdf nominaexdf = new Nominaexdf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominaexdf(nominaexdf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominait(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominait" );
			Nominait nominait = new Nominait(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominait(nominait);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNominaitnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nominaitnu" );
			Nominaitnu nominaitnu = new Nominaitnu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNominaitnu(nominaitnu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszadmh(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszadmh" );
			Nszadmh nszadmh = new Nszadmh(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszadmh(nszadmh);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszanex(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszanex" );
			Nszanex nszanex = new Nszanex(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszanex(nszanex);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszavis(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszavis" );
			Nszavis nszavis = new Nszavis(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszavis(nszavis);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszbanc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszbanc" );
			Nszbanc nszbanc = new Nszbanc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszbanc(nszbanc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszbase(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszbase" );
			Nszbase nszbase = new Nszbase(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszbase(nszbase);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszbolc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszbolc" );
			Nszbolc nszbolc = new Nszbolc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszbolc(nszbolc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszboni(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszboni" );
			Nszboni nszboni = new Nszboni(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszboni(nszboni);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcala(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcala" );
			Nszcala nszcala = new Nszcala(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcala(nszcala);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcatg(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcatg" );
			Nszcatg nszcatg = new Nszcatg(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcatg(nszcatg);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcdtr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcdtr" );
			Nszcdtr nszcdtr = new Nszcdtr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcdtr(nszcdtr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcere(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcere" );
			Nszcere nszcere = new Nszcere(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcere(nszcere);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcoco(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcoco" );
			Nszcoco nszcoco = new Nszcoco(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcoco(nszcoco);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcomp(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcomp" );
			Nszcomp nszcomp = new Nszcomp(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcomp(nszcomp);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcont(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcont" );
			Nszcont nszcont = new Nszcont(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcont(nszcont);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszconv(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszconv" );
			Nszconv nszconv = new Nszconv(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszconv(nszconv);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszcopa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszcopa" );
			Nszcopa nszcopa = new Nszcopa(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszcopa(nszcopa);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszdcpr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszdcpr" );
			Nszdcpr nszdcpr = new Nszdcpr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszdcpr(nszdcpr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszdomi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszdomi" );
			Nszdomi nszdomi = new Nszdomi(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszdomi(nszdomi);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszempr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszempr" );
			Nszempr nszempr = new Nszempr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszempr(nszempr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszepig(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszepig" );
			Nszepig nszepig = new Nszepig(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszepig(nszepig);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszfini(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszfini" );
			Nszfini nszfini = new Nszfini(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszfini(nszfini);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszilte(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszilte" );
			Nszilte nszilte = new Nszilte(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszilte(nszilte);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszinci(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszinci" );
			Nszinci nszinci = new Nszinci(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszinci(nszinci);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszmest(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszmest" );
			Nszmest nszmest = new Nszmest(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszmest(nszmest);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszmupa(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszmupa" );
			Nszmupa nszmupa = new Nszmupa(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszmupa(nszmupa);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszodet(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszodet" );
			Nszodet nszodet = new Nszodet(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszodet(nszodet);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszotpe(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszotpe" );
			Nszotpe nszotpe = new Nszotpe(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszotpe(nszotpe);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszpaga(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszpaga" );
			Nszpaga nszpaga = new Nszpaga(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszpaga(nszpaga);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszpeop(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszpeop" );
			Nszpeop nszpeop = new Nszpeop(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszpeop(nszpeop);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszpoco(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszpoco" );
			Nszpoco nszpoco = new Nszpoco(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszpoco(nszpoco);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszprov(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszprov" );
			Nszprov nszprov = new Nszprov(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszprov(nszprov);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszrari(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszrari" );
			Nszrari nszrari = new Nszrari(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszrari(nszrari);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszreac(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszreac" );
			Nszreac nszreac = new Nszreac(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszreac(nszreac);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszrece(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszrece" );
			Nszrece nszrece = new Nszrece(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszrece(nszrece);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszregi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszregi" );
			Nszregi nszregi = new Nszregi(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszregi(nszregi);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNsztido(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nsztido" );
			Nsztido nsztido = new Nsztido(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNsztido(nsztido);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNsztrab(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nsztrab" );
			Nsztrab nsztrab = new Nsztrab(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNsztrab(nsztrab);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitNszunco(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Nszunco" );
			Nszunco nszunco = new Nszunco(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitNszunco(nszunco);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitOcupacion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Ocupacion" );
			Ocupacion ocupacion = new Ocupacion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitOcupacion(ocupacion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitOpercepciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Opercepciones" );
			Opercepciones opercepciones = new Opercepciones(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitOpercepciones(opercepciones);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitOpfile(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Opfile" );
			Opfile opfile = new Opfile(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitOpfile(opfile);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitOtrperc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Otrperc" );
			Otrperc otrperc = new Otrperc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitOtrperc(otrperc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPagaext(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Pagaext" );
			Pagaext pagaext = new Pagaext(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPagaext(pagaext);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPais(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Pais" );
			Pais pais = new Pais(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPais(pais);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitParteconf(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Parteconf" );
			Parteconf parteconf = new Parteconf(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitParteconf(parteconf);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitParteit(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Parteit" );
			Parteit parteit = new Parteit(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitParteit(parteit);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitParteitnu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Parteitnu" );
			Parteitnu parteitnu = new Parteitnu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitParteitnu(parteitnu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPercep(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Percep" );
			Percep percep = new Percep(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPercep(percep);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPercepcion(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Percepcion" );
			Percepcion percepcion = new Percepcion(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPercepcion(percepcion);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPercniv(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Percniv" );
			Percniv percniv = new Percniv(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPercniv(percniv);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPerfil(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Perfil" );
			Perfil perfil = new Perfil(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPerfil(perfil);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPersona(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Persona" );
			Persona persona = new Persona(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPersona(persona);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPluses(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Pluses" );
			Pluses pluses = new Pluses(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPluses(pluses);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPorcoti(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Porcoti" );
			Porcoti porcoti = new Porcoti(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPorcoti(porcoti);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPrcdivnom(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Prcdivnom" );
			Prcdivnom prcdivnom = new Prcdivnom(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPrcdivnom(prcdivnom);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPrcdivtrab(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Prcdivtrab" );
			Prcdivtrab prcdivtrab = new Prcdivtrab(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPrcdivtrab(prcdivtrab);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPrestaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Prestaciones" );
			Prestaciones prestaciones = new Prestaciones(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPrestaciones(prestaciones);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitPrinters(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Printers" );
			Printers printers = new Printers(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitPrinters(printers);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitProcesos(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Procesos" );
			Procesos procesos = new Procesos(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitProcesos(procesos);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitProvincia(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Provincia" );
			Provincia provincia = new Provincia(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitProvincia(provincia);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRegidocu(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Regidocu" );
			Regidocu regidocu = new Regidocu(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRegidocu(regidocu);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRem_cert_empr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Rem_cert_empr" );
			Rem_cert_empr rem_cert_empr = new Rem_cert_empr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRem_cert_empr(rem_cert_empr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRem_cert_empr_det(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Rem_cert_empr_det" );
			Rem_cert_empr_det rem_cert_empr_det = new Rem_cert_empr_det(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRem_cert_empr_det(rem_cert_empr_det);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRemesa_inss(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Remesa_inss" );
			Remesa_inss remesa_inss = new Remesa_inss(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRemesa_inss(remesa_inss);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRemesa_parte_it(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Remesa_parte_it" );
			Remesa_parte_it remesa_parte_it = new Remesa_parte_it(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRemesa_parte_it(remesa_parte_it);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRemesaafi(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Remesaafi" );
			Remesaafi remesaafi = new Remesaafi(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRemesaafi(remesaafi);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitRemesainss(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Remesainss" );
			Remesainss remesainss = new Remesainss(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitRemesainss(remesainss);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitSession(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Session" );
			Session session = new Session(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitSession(session);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitSincomun(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Sincomun" );
			Sincomun sincomun = new Sincomun(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitSincomun(sincomun);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitSucursal(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Sucursal" );
			Sucursal sucursal = new Sucursal(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitSucursal(sucursal);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTc1(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tc1" );
			Tc1 tc1 = new Tc1(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTc1(tc1);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTc2(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tc2" );
			Tc2 tc2 = new Tc2(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTc2(tc2);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipaut(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipaut" );
			Tipaut tipaut = new Tipaut(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipaut(tipaut);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipboni(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipboni" );
			Tipboni tipboni = new Tipboni(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipboni(tipboni);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipcotc2(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipcotc2" );
			Tipcotc2 tipcotc2 = new Tipcotc2(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipcotc2(tipcotc2);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipdoc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipdoc" );
			Tipdoc tipdoc = new Tipdoc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipdoc(tipdoc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipempr(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipempr" );
			Tipempr tipempr = new Tipempr(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipempr(tipempr);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipinc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipinc" );
			Tipinc tipinc = new Tipinc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipinc(tipinc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipocnae(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipocnae" );
			Tipocnae tipocnae = new Tipocnae(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipocnae(tipocnae);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipocnae2009(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipocnae2009" );
			Tipocnae2009 tipocnae2009 = new Tipocnae2009(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipocnae2009(tipocnae2009);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipocont(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipocont" );
			Tipocont tipocont = new Tipocont(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipocont(tipocont);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTiposdoc(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tiposdoc" );
			Tiposdoc tiposdoc = new Tiposdoc(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTiposdoc(tiposdoc);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipovia(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipovia" );
			Tipovia tipovia = new Tipovia(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipovia(tipovia);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTipreg(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Tipreg" );
			Tipreg tipreg = new Tipreg(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTipreg(tipreg);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTrabajadores(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Trabajadores" );
			Trabajadores trabajadores = new Trabajadores(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTrabajadores(trabajadores);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTrabajo(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Trabajo" );
			Trabajo trabajo = new Trabajo(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTrabajo(trabajo);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTrabdto(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Trabdto" );
			Trabdto trabdto = new Trabdto(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTrabdto(trabdto);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitTrabinci(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Trabinci" );
			Trabinci trabinci = new Trabinci(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitTrabinci(trabinci);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitUnidades(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Unidades" );
			Unidades unidades = new Unidades(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitUnidades(unidades);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitUsuario(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Usuario" );
			Usuario usuario = new Usuario(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitUsuario(usuario);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitVariables(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Variables" );
			Variables variables = new Variables(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitVariables(variables);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public void visitVariaciones(CtsqlDBVisitor ctsqlDBVisitor) throws SQLException{
		ResultSet rs 	= null;
		Statement stmt 	= null;
		boolean	resume 	= true;

		try {
			
			stmt = ctsqlConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Variaciones" );
			Variaciones variaciones = new Variaciones(rs); 
			while ( resume && rs.next() ) {
				resume = ctsqlDBVisitor.visitVariaciones(variaciones);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}



	public AbstractCtsqlDB( Connection ctsqlConnection) {
		this.ctsqlConnection = ctsqlConnection;
	}





}		
	
	
