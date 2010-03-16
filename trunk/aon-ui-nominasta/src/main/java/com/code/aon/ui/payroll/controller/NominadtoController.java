package com.code.aon.ui.payroll.controller;


import java.sql.Date;
import com.code.aon.payroll.resultados.nomina.Nomdto;
import com.code.aon.payroll.resultados.nomina.Nomina;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class NominadtoController extends LinesController {


	
	
	
	
    
	public void generateCdg() {

		Integer cdg = ((Nomina) (FormUtil
				.getController(IPayrollConstants.NOMINA_CONTROLLER_NAME))
				.getTo()).getCdg();

		((Nomdto) getTo()).getId().setLinea((Integer.parseInt(Utils.maxCode("Nomdto", "id.linea")) + 1));
		((Nomdto) getTo()).getId().setCdg(cdg);

		
		Date d= new Date(1,1 ,2009);
		
		((Nomdto) getTo()).setFecnew(d);
		((Nomdto) getTo()).setFecmod(d);
		((Nomdto) getTo()).setHornew(d);
		((Nomdto) getTo()).setHormod(d);
		
	}

}
