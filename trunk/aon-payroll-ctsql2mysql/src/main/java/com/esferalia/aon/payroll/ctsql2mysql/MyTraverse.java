package com.esferalia.aon.payroll.ctsql2mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;

public class MyTraverse extends DefaultCtsqlDBVisitor {

    final static Logger LOGGER =
            LoggerFactory.getLogger("DefaultCtsqlDBVisitor");
    
    
    protected void info(String format, Object ... args){
            LOGGER.info(format, args);
    }

	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		/*
		long start = System.currentTimeMillis();
		ctsqlDB.visitPersona(this);
		// elapsed time in milliseconds
		long elapsed = System.currentTimeMillis() - start;
		info("visitPersons take {} milliseconds.", elapsed );
		long start = System.currentTimeMillis();
		ctsqlDB.visitDelegacion(this);
		long elapsed = System.currentTimeMillis() - start;
		info("visitDelegacion take {} milliseconds.", elapsed );
		*/

		long start = System.currentTimeMillis();
		ctsqlDB.visitDomicilio(this);
		long elapsed = System.currentTimeMillis() - start;
		info("visitDomicilio take {} milliseconds.", elapsed );

		start = System.currentTimeMillis();
		ctsqlDB.visitEmprper(this);
		elapsed = System.currentTimeMillis() - start;
		info("visitEmprper take {} milliseconds.", elapsed );
	}
	
	public void visitPersona(Persona persona) throws SQLException { 
		info("visitPersona({})", persona.getCdg());
	}

	@Override
	public void visitDelegacion(AbstractCtsqlDB.Delegacion delegacion)
			throws SQLException {
		info("visitDelegacion({})", delegacion.getCdg());
		delegacion.visitCliente_delegacion(this);
	}
	
	@Override
	public void visitCliente_delegacion(Cliente cliente, Delegacion delegacion) 
	throws SQLException {
		info("\tvisitCliente_delegacion({})", cliente.getCdg());
		cliente.visitRel_emp_cli(this); 
	}
	
	@Override
	public void visitRel_emp_cli(Emprnif emprnif, Cliente cliente) throws SQLException {
		info("\t\tvisitRel_emp_cli({})", emprnif.getCdg());
		emprnif.visitEmpract_emprnif(this);
	}

	@Override
	public void visitEmpract_emprnif(Empract empract, Emprnif emprnif) throws SQLException {
		info("\t\t\tvisitEmpract_emprnif({})", empract.getCdg());
		empract.visitEmprccc_empract(this);
	}

	@Override
	public void visitEmprccc_empract(Emprccc emprccc, Empract empract) throws SQLException {
		info("\t\t\t\tEmprccc_empract({})", emprccc.getCdg());
	}
	
	@Override
	public void visitDomicilio(Domicilio domicilio) throws SQLException {
		info("visitDomicilio({})", domicilio.getCdg());
		domicilio.visitEmprdom_domicilio(this);
	}
	
	@Override
	public void visitEmprdom_domicilio(Emprdom emprdom, Domicilio domicilio ) throws SQLException {
		info("\tvisitEmprdom_domicilio({})", emprdom.getCdg());
	}
	
	
	@Override
	public void visitEmprper(Emprper emprper) throws SQLException {
		emprper.visitTrabajo_emprper(this);
		emprper.visitRel_pcp_epp(this);
		emprper.visitRel_dto_per(this);
		emprper.visitRel_nom_per(this);
		emprper.visitRel_pex_per(this);
	}

	@Override
	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper )
			throws SQLException {
	}
	
	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
	}

	@Override
	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper) throws SQLException {
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper) throws SQLException {
		nomina.visitRel_nmd_nom(this);
		nomina.visitRel_dto_nom(this);
	}
	
	@Override
	public void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)throws SQLException {
	}
	
	
	@Override
	public void visitRel_dto_nom(Nomdto nomdto, Nomina nomina) throws SQLException {
	}
	
	@Override
	public void visitRel_pex_per(Nominaex nominaex, Emprper emprper) throws SQLException {
		nominaex.visitNomdtoex_nominaex(this);
		
		return ;
	}
	
	@Override
	public void visitNomdtoex_nominaex(Nomdtoex nomdtoex, Nominaex nominaex)
			throws SQLException {
	}


}
