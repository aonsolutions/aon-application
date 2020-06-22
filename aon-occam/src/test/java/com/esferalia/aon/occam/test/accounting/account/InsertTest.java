package com.esferalia.aon.occam.test.accounting.account;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;

public class InsertTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		Account[] accounts = {
				new Account().setDomain(DOMAIN_ID).setCode("600000000").setDescription("Compras de mercader\u00EDas.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("607000000").setDescription("Trabajos realizados por OTRAS EMPRESAS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("620000001").setDescription("Gastos de TERCEROS para la Plataforma tEDI.center").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("621000001").setDescription("ALQUILER OFICINA - Duque de Wellington 52").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("622000001").setDescription("REPARACION Y CONSERVACION OFICINA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("623000000").setDescription("Servicios de Profesionales Independientes").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("623000001").setDescription("NOTARIAS Y REGISTROS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("623000002").setDescription("ASESORIA FISCAL/LABORAL/CONTABLE").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("623000003").setDescription("ALARMA Y SERVICIO DE VIGILANCIA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("624000001").setDescription("Gastos de Correo y Mensajer\u00EDa").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("625000000").setDescription("SEGURO OFICINA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("625000001").setDescription("SEGUROS PRESTAMOS (Garant\u00EDas)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("626000000").setDescription("Servicios Bancarios y similares").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("627000001").setDescription("PUBLICIDAD Y PROPAGANDA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("628000000").setDescription("SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("628000001").setDescription("SUMINISTROS (Electricidad, Agua...)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("6281").setDescription("TELEFONO").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("628100000").setDescription("GASTO TELEFONO").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629000000").setDescription("GASTOS VARIOS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629000001").setDescription("Material de OFICINA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("6291").setDescription("CURSOS Y GASTOS DE FORMACION").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629100000").setDescription("CURSOS Y GASTOS DE FORMACION").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("6292").setDescription("TRANSPORTES Y DIETAS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629200001").setDescription("TRANSPORTES (autob\u00FAs, avi\u00F3n, taxi etc...)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629200002").setDescription("DIETAS (desayunos, comidas, cenas)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("629200003").setDescription("Servicios de  Limpieza").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("630100000").setDescription("IMPUESTO DIFERIDO").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("631000000").setDescription("Otros Tributos").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("634100001").setDescription("Ajustes negativos en IVA de activo corriente.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("640000000").setDescription("Sueldos y salarios.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("640000001").setDescription("Sueldos y Salarios desarrollo Plataforma tEDI.center").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("641000000").setDescription("Indemnizaciones.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("642000000").setDescription("Seguridad Social a cargo de la empresa.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("642000001").setDescription("S.S. desarrollo Plataforma tEDI.center").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("662000000").setDescription("INTERESES DE DEUDAS CON ENTIDADES DE CR\u00C9DITO").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("6625").setDescription("INTERESES DE DEUDAS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("662500000").setDescription("INTERESES DE DEUDAS CON LA HACIENDA P\u00DABLICA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("662500001").setDescription("INTERESES DE DEUDAS CON LA SEGURIDAD SOCIAL").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("669000000").setDescription("Otros gastos financieros.").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("680602018").setDescription("Amortizaci\u00F3n Desarrollo tEDI.center (2018)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("680602019").setDescription("Amortizaci\u00F3n Desarrollo tEDI.center (2019)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("681100001").setDescription("Amortizaci\u00F3n OFICINA").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("681500001").setDescription("Amortizaci\u00F3n REFORMA LOCAL OFICINA (2019)").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("681700001").setDescription("Amortizaci\u00F3n Equipos puestos de trabajo").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("7051").setDescription("VENTAS SERVICIOS").setActive(true),
				new Account().setDomain(DOMAIN_ID).setCode("7053").setDescription("VENTAS SERVICIOS EXT").setActive(true),
				
		};
		System.out.println("START");
		for (Account account :  accounts) {
			Account acc = ACCOUNTING.getAccount(ctx, account.getCode());
			if (acc == null) {
				System.out.println(account.getCode());
				account = ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, account);		
			}
		}
	}
}
