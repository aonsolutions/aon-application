package com.esferalia.aon.occam.test.accounting.entry;


import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class InsertTest extends AbstractOccamTest {
	
	@Test
	public void test() throws ParseException {
		Date now = new Date();
		int year = AonDateUtils.getYear(now);
		AccountPeriod period = ACCOUNTING.getPeriod(ctx, now );
		
		
		AccountEntry ori = new AccountEntry()
				.setDomain(ctx.getDomainId())
				.setPeriod(period.getId())
				.setEntryType( AccountEntryType.MANUAL )
				.setConfidential(false);
		
		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2001C01065609",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000002"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2001C01065609",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000002"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2001C01065609",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000021",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000021"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000021",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000021"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000021",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000021"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","N/Fra: CENIT/000022",605,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000022"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000022",0,105,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000022"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000022",0,500,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000022"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000002",11752.13,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000002"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000002",0,2039.63,"430000003","AON SOLUTIONS S.L.","E-2020/000002"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000002",0,9712.5,"430000003","AON SOLUTIONS S.L.","E-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020000301",0,642.05,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000001"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020000301",111.43,0,"410000024","AUSARTA Prima S.L.","R-2020/000001"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020000301",530.62,0,"410000024","AUSARTA Prima S.L.","R-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000025","IVAN RODRIGO LONDOÑO","Pago Fra: 0239-19",168.19,0,"572000001","Banco SANTANDER","R-2019/000139"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO IVAN RODRIGO LONDONO B...",0,168.19,"410000025","IVAN RODRIGO LONDOÑO","R-2019/000139"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2001C01065609",98.99,0,"572000001","Banco SANTANDER","R-2020/000002"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: CENIT/000021",0,2480.5,"572000001","Banco SANTANDER","E-CENIT/000021"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",2480.5,0,"430000003","AON SOLUTIONS S.L.","E-CENIT/000021"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000001",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000001"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000001",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000001"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000001",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-9042",0,1.75,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000003"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-9042",0.3,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000003"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-9042",1.45,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000003"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3679225619",31.2,0,"572000001","Banco SANTANDER","R-2019/000140"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *GSUITE_translo...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2019/000140"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 971222000842-5",142.61,0,"572000001","Banco SANTANDER","R-2019/000141"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *ADS9398321100,...",0,142.61,"410000018","GOOGLE Ireland Limited","R-2019/000141"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3676692905",7.88,0,"572000001","Banco SANTANDER","R-2019/000142"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *CLOUD_018CA0-0...",0,7.88,"410000018","GOOGLE Ireland Limited","R-2019/000142"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00028880-0120",0,85.53,"628100000","GASTO TELEFONO","R-2020/000006"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00028880-0120",14.84,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000006"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00028880-0120",70.69,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000006"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020000301",642.05,0,"572000001","Banco SANTANDER","R-2020/000001"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO AUSARTA ENERGIA Nº REC...",0,642.05,"410000024","AUSARTA Prima S.L.","R-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-9042",1.75,0,"572000001","Banco SANTANDER","R-2020/000003"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA AWS EMEA, aws.amazon.c...",0,1.75,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000003"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: DP-666D656D594871 [UDEMY]",0,9.99,"629200000","CURSOS Y GASTOS DE FORMACION","R-2020/000004"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: DP-666D656D594871 [UDEMY]",1.73,0,"410000036","ACREEDORES (varios)","R-2020/000004"))
			.addDetail( getAccountEntryDetail("629200000","CURSOS Y GASTOS DE FORMACION","S/Fra: DP-666D656D594871 [UDEMY]",8.26,0,"410000036","ACREEDORES (varios)","R-2020/000004"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: 202764058579",0,1.21,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000005"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 202764058579",0.21,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000005"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 202764058579",1,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000005"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 3021/19",36.3,0,"572000001","Banco SANTANDER","R-2019/000137"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2019/000137"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: DP-666D656D594871",9.99,0,"572000001","Banco SANTANDER","R-2020/000004"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA UDEMY: ONLINE COURSES,...",0,9.99,"410000036","ACREEDORES (varios)","R-2020/000004"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: 202764058579",1.21,0,"572000001","Banco SANTANDER","R-2020/000005"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,1.21,"410000005","1&1 Internet España, S.L.U.","R-2020/000005"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","GESTION DEVOLUCIONES - INTERI...",0,1870,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","GESTION DEVOLUCIONES - INTERI...",1870,0,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","GESTION DE DEVOLUCIONES -EXTE...",0,3575,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","GESTION DE DEVOLUCIONES -EXTE...",3575,0,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","RECIBO DAMOS SOLUCIONES INFOR...",3575,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO DAMOS SOLUCIONES INFOR...",0,3575,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","RECIBO DAMOS SOLUCIONES INFOR...",1870,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO DAMOS SOLUCIONES INFOR...",0,1870,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("470820193","LANBIDE Formación DUAL (2019)","TRANSFERENCIA DE LANBIDE-SERV...",0,600,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE LANBIDE-SERV...",600,0,"470820193","LANBIDE Formación DUAL (2019)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","Pago Fra: E1AL00028880-0120",85.53,0,"572000001","Banco SANTANDER","R-2020/000006"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ORANGE ESPAGNE S.A. Nº...",0,85.53,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000006"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Modelo 110 2019/4T",9023.81,0,"475000000","Hª Pª Acreedora por IMPUESTOS (IVA e IRPF)",""))
			.addDetail( getAccountEntryDetail("475000000","Hª Pª Acreedora por IMPUESTOS (IVA e IRPF)","Modelo 110 2019/4T",0,9023.81,"475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","S/Fra: 2020/000138",0,91.96,"623000002","ASESORIA FISCAL/LABORAL/CONTABLE","R-2020/000011"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000138",15.96,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000011"))
			.addDetail( getAccountEntryDetail("623000002","ASESORIA FISCAL/LABORAL/CONTABLE","S/Fra: 2020/000138",76,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000011"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000002",0,11752.13,"572000001","Banco SANTANDER","E-2020/000002"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",11752.13,0,"430000003","AON SOLUTIONS S.L.","E-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000001",0,18150,"572000001","Banco SANTANDER","E-2020/000001"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",18150,0,"430000003","AON SOLUTIONS S.L.","E-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000035","DECOFEL ALAVA S.L.","Pago Fra: 2575 (R:2858)",2582.91,0,"572000001","Banco SANTANDER","R-2019/000116"))
			.addDetail( getAccountEntryDetail("410000033","GPL DESARROLLOS Y SOLUCIONES S.L (NUBOH)","Pago Fra: AON/000033 (R:2858)",3630,0,"572000001","Banco SANTANDER","R-2019/000099"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Pagos ENERO",0,6212.91,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Cuota ENERO",0,1333.33,"520000003","Préstamo LOCAL",""))
			.addDetail( getAccountEntryDetail("520000003","Préstamo LOCAL","Cuota ENERO",1259.63,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","Cuota ENERO",73.7,0,"520000003","Préstamo LOCAL",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("470820192","HAZITEK (2019)","29/01/2020TRANSFERENCIA DE AD...",0,26047.67,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","29/01/2020TRANSFERENCIA DE AD...",26047.67,0,"470820192","HAZITEK (2019)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("475000000","Hª Pª Acreedora por IMPUESTOS (IVA e IRPF)","Pago Fra: Mod.110 - 2019 / 4T",9023.81,0,"572000001","Banco SANTANDER","Mod.110 - 2019 / 4T"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","30/01/2020RECIBO D.F.A.-DIREC...",0,9023.81,"475000000","Hª Pª Acreedora por IMPUESTOS (IVA e IRPF)","Mod.110 - 2019 / 4T"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",2401.28,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",703.24,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",4854.5,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",1931.14,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",1130.33,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",2407.15,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/01/2020 (R:2...",1808.95,0,"572000001","Banco SANTANDER","NÓMINA - 31/01/2020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Nóminas ENERO 2020",0,16534.03,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",134.16,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,134.16,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",4912.58,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,4912.58,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3691745512",0,31.2,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000007"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 3691745512",6.55,0,"410000018","GOOGLE Ireland Limited","R-2020/000007"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 3691745512",0,6.55,"410000018","GOOGLE Ireland Limited","R-2020/000007"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 3691745512",31.2,0,"410000018","GOOGLE Ireland Limited","R-2020/000007"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","S/Fra: 0096/20",0,36.3,"628100000","GASTO TELEFONO","R-2020/000008"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 0096/20",6.3,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000008"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: 0096/20",30,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000008"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 971222000842-6",0,148.44,"627000001","PUBLICIDAD Y PROPAGANDA","R-2020/000012"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 971222000842-6",31.17,0,"410000018","GOOGLE Ireland Limited","R-2020/000012"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 971222000842-6",0,31.17,"410000018","GOOGLE Ireland Limited","R-2020/000012"))
			.addDetail( getAccountEntryDetail("627000001","PUBLICIDAD Y PROPAGANDA","S/Fra: 971222000842-6",148.44,0,"410000018","GOOGLE Ireland Limited","R-2020/000012"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","S/Fra: 00000199",0,170.04,"629200003","Servicios de  Limpieza","R-2020/000013"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 00000199",29.51,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000013"))
			.addDetail( getAccountEntryDetail("629200003","Servicios de  Limpieza","S/Fra: 00000199",140.53,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000013"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3693389370",0,0.26,"627000001","PUBLICIDAD Y PROPAGANDA","R-2020/000014"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 3693389370",0,0.05,"410000018","GOOGLE Ireland Limited","R-2020/000014"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 3693389370",0.05,0,"410000018","GOOGLE Ireland Limited","R-2020/000014"))
			.addDetail( getAccountEntryDetail("627000001","PUBLICIDAD Y PROPAGANDA","S/Fra: 3693389370",0.26,0,"410000018","GOOGLE Ireland Limited","R-2020/000014"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("640000000","Sueldos y salarios.","Nóminas 31/01/2020",8563.34,0,"","",""))
			.addDetail( getAccountEntryDetail("642000000","Seguridad Social a cargo de la empresa.","Nóminas 31/01/2020",1200.24,0,"","",""))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Nóminas 31/01/2020",0,3415.7,"","",""))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","Nóminas 31/01/2020",0,5211.8,"","",""))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Nóminas 31/01/2020",0,16534.03,"","",""))
			.addDetail( getAccountEntryDetail("640000001","Sueldos y Salarios desarrollo Plataforma tEDI.center","Nóminas 31/01/2020",12257.69,0,"","",""))
			.addDetail( getAccountEntryDetail("642000001","S.S. desarrollo Plataforma tEDI.center","Nóminas 31/01/2020",3140.26,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000039","KGM Marketing s.l.u","S/Fra: 7315",0,35453,"629000000","GASTOS VARIOS","R-2020/000017"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 7315",6153,0,"410000039","KGM Marketing s.l.u","R-2020/000017"))
			.addDetail( getAccountEntryDetail("629000000","GASTOS VARIOS","S/Fra: 7315",29300,0,"410000039","KGM Marketing s.l.u","R-2020/000017"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra: 2020/000003",1524.6,0,"700000000","Ventas de mercaderías.","E-2020/000003"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000003",0,264.6,"430000006","UDAPA, S.COOP.","E-2020/000003"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra: 2020/000003",0,1260,"430000006","UDAPA, S.COOP.","E-2020/000003"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000023",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000023"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000023",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000023"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000023",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000023"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","N/Fra: CENIT/000024",605,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000024"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000024",0,105,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000024"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000024",0,500,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000024"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000004",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000004"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000004",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000004"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000004",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000004"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2002C00876797",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000010"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2002C00876797",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000010"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2002C00876797",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000010"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000005",11797.5,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000005"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000005",0,2047.5,"430000003","AON SOLUTIONS S.L.","E-2020/000005"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000005",0,9750,"430000003","AON SOLUTIONS S.L.","E-2020/000005"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2002C00876797",98.99,0,"572000001","Banco SANTANDER","R-2020/000010"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000010"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","Pago Fra: 2020/000138",91.96,0,"572000001","Banco SANTANDER","R-2020/000011"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ZARATE ASESORES,S.L. N...",0,91.96,"410000021","ZARATE ASESORES, S.L.","R-2020/000011"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-35201",0,1.8,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000018"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-35201",0.31,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000018"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-35201",1.49,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000018"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020002764",0,600.63,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000009"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020002764",104.24,0,"410000024","AUSARTA Prima S.L.","R-2020/000009"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020002764",496.39,0,"410000024","AUSARTA Prima S.L.","R-2020/000009"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3691745512",31.2,0,"572000001","Banco SANTANDER","R-2020/000007"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *GSUITE_translo...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2020/000007"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 971222000842-6",148.44,0,"572000001","Banco SANTANDER","R-2020/000012"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE*ADS9398321100, ...",0,148.44,"410000018","GOOGLE Ireland Limited","R-2020/000012"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3693389370",0.26,0,"572000001","Banco SANTANDER","R-2020/000014"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE*CLOUD 018CA0-0C...",0,0.26,"410000018","GOOGLE Ireland Limited","R-2020/000014"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","Pago Fra: 00000199",170.04,0,"572000001","Banco SANTANDER","R-2020/000013"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO LIMPIEZAS MARI CARMEN ...",0,170.04,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000013"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000005","CRC INDUSTRIES IBERIA SLU","N/Fra: CENIT/000025",72.6,0,"","","E-CENIT/000025"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000025",0,12.6,"430000005","CRC INDUSTRIES IBERIA SLU","E-CENIT/000025"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000025",0,60,"430000005","CRC INDUSTRIES IBERIA SLU","E-CENIT/000025"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00029247-0220",0,59.46,"628100000","GASTO TELEFONO","R-2020/000016"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00029247-0220",7.75,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000016"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00029247-0220",51.71,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000016"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-35201",1.8,0,"572000001","Banco SANTANDER","R-2020/000018"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","03/02/2020COMPRA AWS EMEA, aw...",0,1.8,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000018"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000005","CRC INDUSTRIES IBERIA SLU","Cobro Fra: CENIT/000025",0,72.6,"572000001","Banco SANTANDER","E-CENIT/000025"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","06/02/2020TRANSFERENCIA DE CR...",72.6,0,"430000005","CRC INDUSTRIES IBERIA SLU","E-CENIT/000025"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020002764",600.63,0,"572000001","Banco SANTANDER","R-2020/000009"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","06/02/2020RECIBO ENERGIA ELEC...",0,600.63,"410000024","AUSARTA Prima S.L.","R-2020/000009"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: B85049435",0,1.21,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000015"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: B85049435",0.21,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000015"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: B85049435",1,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000015"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 0096/20",36.3,0,"572000001","Banco SANTANDER","R-2020/000008"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000008"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("629000000","GASTOS VARIOS","COMPRA INTERNET EN ORANGE AMO...",192.66,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA INTERNET EN ORANGE AMO...",0,192.66,"629000000","GASTOS VARIOS",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000039","KGM Marketing s.l.u","Pago Fra: 7315",5453,0,"572000001","Banco SANTANDER","R-2020/000017"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA A FAVOR DE KGM ...",0,5453,"410000039","KGM Marketing s.l.u","R-2020/000017"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: B85049435",1.21,0,"572000001","Banco SANTANDER","R-2020/000015"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,1.21,"410000005","1&1 Internet España, S.L.U.","R-2020/000015"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","Pago Fra: E1AL00029247-0220",59.46,0,"572000001","Banco SANTANDER","R-2020/000016"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ORANGE ESPAGNE S.A. Nº...",0,59.46,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000016"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","LIQUIDACION POR EMISION",0.36,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","LIQUIDACION POR EMISION",0,0.36,"410000027","Banco Santander, S.A. (Acreedor)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","Cobro Fra: CENIT/000022 (R:2859)",0,605,"572000001","Banco SANTANDER","E-CENIT/000022"))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Cobro Fra: 2020/000003 (R:2859)",0,1524.6,"572000001","Banco SANTANDER","E-2020/000003"))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","Cobro Fra: CENIT/000024 (R:2859)",0,605,"572000001","Banco SANTANDER","E-CENIT/000024"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","BPM + UDAPA (ene-feb)",2734.6,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","S/Fra: F2020050ZBBBCZD",0,0.36,"626000000","Servicios Bancarios y similares","R-2020/000043"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: F2020050ZBBBCZD",0.06,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000043"))
			.addDetail( getAccountEntryDetail("626000000","Servicios Bancarios y similares","S/Fra: F2020050ZBBBCZD",0.3,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000043"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000035","DECOFEL ALAVA S.L.","Pago Fra: 2575 (R:2868)",2582.92,0,"572000001","Banco SANTANDER","R-2019/000116"))
			.addDetail( getAccountEntryDetail("410000033","GPL DESARROLLOS Y SOLUCIONES S.L (NUBOH)","Pago Fra: AON/000033 (R:2868)",3630,0,"572000001","Banco SANTANDER","R-2019/000099"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Pagos Finales GPL y DECOFEL",0,6212.92,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: CENIT/000023",0,2480.5,"572000001","Banco SANTANDER","E-CENIT/000023"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",2480.5,0,"430000003","AON SOLUTIONS S.L.","E-CENIT/000023"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000004",0,18150,"572000001","Banco SANTANDER","E-2020/000004"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",18150,0,"430000003","AON SOLUTIONS S.L.","E-2020/000004"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000005",0,11797.5,"572000001","Banco SANTANDER","E-2020/000005"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",11797.5,0,"430000003","AON SOLUTIONS S.L.","E-2020/000005"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","S/Fra: 2020/000242",0,91.96,"623000002","ASESORIA FISCAL/LABORAL/CONTABLE","R-2020/000023"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000242",15.96,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000023"))
			.addDetail( getAccountEntryDetail("623000002","ASESORIA FISCAL/LABORAL/CONTABLE","S/Fra: 2020/000242",76,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000023"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra: 2020/000006",2413.95,0,"700000000","Ventas de mercaderías.","E-2020/000006"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000006",0,418.95,"430000006","UDAPA, S.COOP.","E-2020/000006"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra: 2020/000006",0,1995,"430000006","UDAPA, S.COOP.","E-2020/000006"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","S/Fra: 0419/20",0,36.3,"628100000","GASTO TELEFONO","R-2020/000019"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 0419/20",6.3,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000019"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: 0419/20",30,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000019"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",1130.33,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",4788,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",703.24,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",2407.15,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",2401.28,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",1931.14,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 29/02/2020 (R:2...",1039.98,0,"572000001","Banco SANTANDER","NÓMINA - 29/02/2020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Nominas FEBRERO",0,15698.56,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",134.16,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,134.16,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",5077.64,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,5077.64,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra ABONO: R2020/000001",0,190.58,"700000000","Ventas de mercaderías.","E-R2020/000001"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra ABONO: R2020/000001",33.08,0,"430000006","UDAPA, S.COOP.","E-R2020/000001"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra ABONO: R2020/000001",157.5,0,"430000006","UDAPA, S.COOP.","E-R2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","S/Fra: 00000393",0,170.04,"629200003","Servicios de  Limpieza","R-2020/000020"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 00000393",29.51,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000020"))
			.addDetail( getAccountEntryDetail("629200003","Servicios de  Limpieza","S/Fra: 00000393",140.53,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000020"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3703915879",0,31.2,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000021"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 3703915879",6.55,0,"410000018","GOOGLE Ireland Limited","R-2020/000021"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 3703915879",0,6.55,"410000018","GOOGLE Ireland Limited","R-2020/000021"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 3703915879",31.2,0,"410000018","GOOGLE Ireland Limited","R-2020/000021"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 971222000842-7",0,260.8,"627000001","PUBLICIDAD Y PROPAGANDA","R-2020/000024"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 971222000842-7",0,54.77,"410000018","GOOGLE Ireland Limited","R-2020/000024"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 971222000842-7",54.77,0,"410000018","GOOGLE Ireland Limited","R-2020/000024"))
			.addDetail( getAccountEntryDetail("627000001","PUBLICIDAD Y PROPAGANDA","S/Fra: 971222000842-7",260.8,0,"410000018","GOOGLE Ireland Limited","R-2020/000024"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","S/Fra: A20/000003",0,513,"623000000","Servicios de Profesionales Independientes","R-2020/000030"))
			.addDetail( getAccountEntryDetail("475100002","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Profesionales)","S/Fra: A20/000003",0,31.5,"410000040","JOSEP COMAS ARNAU","R-2020/000030"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: A20/000003",94.5,0,"410000040","JOSEP COMAS ARNAU","R-2020/000030"))
			.addDetail( getAccountEntryDetail("623000000","Servicios de Profesionales Independientes","S/Fra: A20/000003",450,0,"410000040","JOSEP COMAS ARNAU","R-2020/000030"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("640000000","Sueldos y salarios.","Nóminas 29/02/2020",10191.17,0,"","",""))
			.addDetail( getAccountEntryDetail("642000000","Seguridad Social a cargo de la empresa.","Nóminas 29/02/2020",1334.37,0,"","",""))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Nóminas 29/02/2020",0,3482.2,"","",""))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","Nóminas 29/02/2020",0,4751.8,"","",""))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Nóminas 29/02/2020",0,16467.53,"","",""))
			.addDetail( getAccountEntryDetail("640000001","Sueldos y Salarios desarrollo Plataforma tEDI.center","Nóminas 29/02/2020",10629.86,0,"","",""))
			.addDetail( getAccountEntryDetail("642000001","S.S. desarrollo Plataforma tEDI.center","Nóminas 29/02/2020",2546.13,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000026",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000026"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000026",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000026"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000026",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000026"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","N/Fra: CENIT/000027",605,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000027"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000027",0,105,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000027"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000027",0,500,"430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","E-CENIT/000027"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000007",12211.93,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000007"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000007",0,2119.43,"430000003","AON SOLUTIONS S.L.","E-2020/000007"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000007",0,10092.5,"430000003","AON SOLUTIONS S.L.","E-2020/000007"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000008",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000008"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000008",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000008"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000008",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000008"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2003C00565599",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000026"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2003C00565599",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000026"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2003C00565599",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000026"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2003C00565599",98.99,0,"572000001","Banco SANTANDER","R-2020/000026"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000026"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-65886",0,0.71,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000022"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-65886",0.12,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000022"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-65886",0.59,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000022"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 971222000842-7",260.8,0,"572000001","Banco SANTANDER","R-2020/000024"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *ADS9398321100,...",0,260.8,"410000018","GOOGLE Ireland Limited","R-2020/000024"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3703915879",31.2,0,"572000001","Banco SANTANDER","R-2020/000021"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *GSUITE_translo...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2020/000021"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","Pago Fra: 2020/000242",91.96,0,"572000001","Banco SANTANDER","R-2020/000023"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ZARATE ASESORES,S.L. N...",0,91.96,"410000021","ZARATE ASESORES, S.L.","R-2020/000023"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","COMPRA GOOGLE *CLOUD_018CA0-0...",0.25,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *CLOUD_018CA0-0...",0,0.25,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020005220",0,642.05,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000025"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020005220",111.43,0,"410000024","AUSARTA Prima S.L.","R-2020/000025"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020005220",530.62,0,"410000024","AUSARTA Prima S.L.","R-2020/000025"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","Pago Fra: 00000393",170.04,0,"572000001","Banco SANTANDER","R-2020/000020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO LIMPIEZAS MARI CARMEN ...",0,170.04,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000020"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","cuota FEBRERO",0,1333.33,"520000003","Préstamo LOCAL",""))
			.addDetail( getAccountEntryDetail("520000003","Préstamo LOCAL","cuota FEBRERO",1269.21,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","cuota FEBRERO",64.12,0,"520000003","Préstamo LOCAL",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-65886",0.71,0,"572000001","Banco SANTANDER","R-2020/000022"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA AWS EMEA, aws.amazon.c...",0,0.71,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000022"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00029557-0320",0,39.64,"628100000","GASTO TELEFONO","R-2020/000029"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00029557-0320",6.88,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000029"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00029557-0320",32.76,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000029"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: R/01409",0,204.01,"629200002","DIETAS (desayunos, comidas, cenas)","G-2020/000001"))
			.addDetail( getAccountEntryDetail("629200002","DIETAS (desayunos, comidas, cenas)","S/Fra: R/01409",204.01,0,"410000036","ACREEDORES (varios)","G-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra ABONO: PR2020006310",817.8,0,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000027"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra ABONO: PR2020006310",0,141.93,"410000024","AUSARTA Prima S.L.","R-2020/000027"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra ABONO: PR2020006310",0,675.87,"410000024","AUSARTA Prima S.L.","R-2020/000027"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020005220",642.05,0,"572000001","Banco SANTANDER","R-2020/000025"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO AUSARTA ENERGIA Nº REC...",0,642.05,"410000024","AUSARTA Prima S.L.","R-2020/000025"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: R/01409",204.01,0,"572000001","Banco SANTANDER","G-2020/000001"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSACCION CONTACTLESS EN HO...",0,204.01,"410000036","ACREEDORES (varios)","G-2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: 202764569725",0,13.31,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000028"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 202764569725",2.31,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000028"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 202764569725",11,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000028"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000006","AON SOLUTIONS S.L.","Pago Fra: 2019/000120",10387.85,0,"572000001","Banco SANTANDER","R-2019/000120"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO AON SOLUTIONS Nº RECIB...",0,10387.85,"410000006","AON SOLUTIONS S.L.","R-2019/000120"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 0419/20",36.3,0,"572000001","Banco SANTANDER","R-2020/000019"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000019"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Cobro Fra: 2020/000006 (R:2878)",0,2413.95,"572000001","Banco SANTANDER","E-2020/000006"))
			.addDetail( getAccountEntryDetail("430000004","BUSINESS PROCESS MANAGEMENT SYSTEMS, S.L.","Cobro Fra: CENIT/000027 (R:2878)",0,605,"572000001","Banco SANTANDER","E-CENIT/000027"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","UDAPAFebrero + BPM Marzo",3018.95,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","LIQUIDACION POR EMISION",12.26,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","LIQUIDACION POR EMISION",0,12.26,"410000027","Banco Santander, S.A. (Acreedor)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: DP-666D6972584865676C...]",0,21.98,"629000000","GASTOS VARIOS","G-2020/000002"))
			.addDetail( getAccountEntryDetail("629000000","GASTOS VARIOS","S/Fra: DP-666D6972584865676C...]",21.98,0,"410000036","ACREEDORES (varios)","G-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","S/Fra: F2020071YBBBDXV",0,12.26,"626000000","Servicios Bancarios y similares","R-2020/000044"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: F2020071YBBBDXV",2.13,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000044"))
			.addDetail( getAccountEntryDetail("626000000","Servicios Bancarios y similares","S/Fra: F2020071YBBBDXV",10.13,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000044"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Abono Fra: R2020/000001",157.5,0,"572000001","Banco SANTANDER","E-R2020/000001"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA A FAVOR DE UDAP...",0,157.5,"430000006","UDAPA, S.COOP.","E-R2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Abono Fra: R2020/000001",33.08,0,"572000001","Banco SANTANDER","E-R2020/000001"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Transferencia A Favor De Udap...",0,33.08,"430000006","UDAPA, S.COOP.","E-R2020/000001"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: DP-666D697258486567...",21.98,0,"572000001","Banco SANTANDER","G-2020/000002"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA UDEMY: ONLINE COURSES,...",0,21.98,"410000036","ACREEDORES (varios)","G-2020/000002"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000028","AYUNTAMIENTO de Vitoria-Gasteiz","S/Fra: Consumo Agua 2019/4T",0,60.61,"628000001","SUMINISTROS (Electricidad, Agua...)","G-2020/000004"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: Consumo Agua 2019/4T",60.61,0,"410000028","AYUNTAMIENTO de Vitoria-Gasteiz","G-2020/000004"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000028","AYUNTAMIENTO de Vitoria-Gasteiz","S/Fra: Canon Agua 2019/4T",0,1.44,"628000001","SUMINISTROS (Electricidad, Agua...)","G-2020/000005"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: Canon Agua 2019/4T",1.44,0,"410000028","AYUNTAMIENTO de Vitoria-Gasteiz","G-2020/000005"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: 202764569725",13.31,0,"572000001","Banco SANTANDER","R-2020/000028"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,13.31,"410000005","1&1 Internet España, S.L.U.","R-2020/000028"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Abono Fra: PR2020006310",0,817.8,"572000001","Banco SANTANDER","R-2020/000027"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AUSARTA ENER...",817.8,0,"410000024","AUSARTA Prima S.L.","R-2020/000027"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","Pago Fra: E1AL00029557-0320",39.64,0,"572000001","Banco SANTANDER","R-2020/000029"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ORANGE ESPAGNE S.A. Nº...",0,39.64,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000029"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: 301/20 [Betean Audito...]",0,980.1,"620000001","Gastos de TERCEROS para la Plataforma tEDI.center","R-2020/000031"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 301/20 [Betean Audito...]",170.1,0,"410000036","ACREEDORES (varios)","R-2020/000031"))
			.addDetail( getAccountEntryDetail("620000001","Gastos de TERCEROS para la Plataforma tEDI.center","S/Fra: 301/20 [Betean Audito...]",810,0,"410000036","ACREEDORES (varios)","R-2020/000031"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000039","KGM Marketing s.l.u","Pago Fra: 7315 (R:2879)",15000,0,"572000001","Banco SANTANDER","R-2020/000017"))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","Pago Fra: A20/000003 (R:2879)",513,0,"572000001","Banco SANTANDER","R-2020/000030"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Pagos JComas + KGM",0,15513,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: CENIT/000026",0,2480.5,"572000001","Banco SANTANDER","E-CENIT/000026"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",2480.5,0,"430000003","AON SOLUTIONS S.L.","E-CENIT/000026"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000007",0,12211.93,"572000001","Banco SANTANDER","E-2020/000007"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",12211.93,0,"430000003","AON SOLUTIONS S.L.","E-2020/000007"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000008",0,18150,"572000001","Banco SANTANDER","E-2020/000008"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",18150,0,"430000003","AON SOLUTIONS S.L.","E-2020/000008"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","S/Fra: 2020/000356",0,91.96,"623000002","ASESORIA FISCAL/LABORAL/CONTABLE","R-2020/000033"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000356",15.96,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000033"))
			.addDetail( getAccountEntryDetail("623000002","ASESORIA FISCAL/LABORAL/CONTABLE","S/Fra: 2020/000356",76,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000033"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: 301/20",980.1,0,"572000001","Banco SANTANDER","R-2020/000031"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA A FAVOR DE BETE...",0,980.1,"410000036","ACREEDORES (varios)","R-2020/000031"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("629200000","CURSOS Y GASTOS DE FORMACION","COMPRA UDEMY: ONLINE COURSES,...",12.99,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA UDEMY: ONLINE COURSES,...",0,12.99,"629200000","CURSOS Y GASTOS DE FORMACION",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",1130.33,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",4788,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",703.24,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",2407.15,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",2401.28,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",1931.14,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/03/2020 (R:2...",1662.32,0,"572000001","Banco SANTANDER","NÓMINA - 31/03/2020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Nominas MARZO 2020",0,16320.9,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",4617.64,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,4617.64,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",134.16,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,134.16,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","RECIBO Inst. Vasco de Finanza...",473.96,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO Inst. Vasco de Finanza...",0,473.96,"662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Cuota MARZO",0,1333.33,"520000003","Préstamo LOCAL",""))
			.addDetail( getAccountEntryDetail("520000003","Préstamo LOCAL","Cuota MARZO",1269.62,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","Cuota MARZO",63.71,0,"520000003","Préstamo LOCAL",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","S/Fra: 00000583",0,85.01,"629200003","Servicios de  Limpieza","R-2020/000032"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 00000583",14.75,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000032"))
			.addDetail( getAccountEntryDetail("629200003","Servicios de  Limpieza","S/Fra: 00000583",70.26,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000032"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000011 (R:2883)",0,12402.5,"572000001","Banco SANTANDER","E-2020/000011"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Anticipo Factura 2020/0011 (AON)",12402.5,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","LIQUIDACION ANTICIPO CREDITO,...",33.08,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","LIQUIDACION ANTICIPO CREDITO,...",0,33.08,"662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: 522446",34.56,0,"572000001","Banco SANTANDER","G-2020/000003"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA ELEMENTOR PRO, WILMING...",0,34.56,"410000036","ACREEDORES (varios)","G-2020/000003"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra: 2020/000009",2604.53,0,"700000000","Ventas de mercaderías.","E-2020/000009"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000009",0,452.03,"430000006","UDAPA, S.COOP.","E-2020/000009"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra: 2020/000009",0,2152.5,"430000006","UDAPA, S.COOP.","E-2020/000009"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3716943396",0,31.2,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000035"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 3716943396",6.55,0,"410000018","GOOGLE Ireland Limited","R-2020/000035"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 3716943396",0,6.55,"410000018","GOOGLE Ireland Limited","R-2020/000035"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 3716943396",31.2,0,"410000018","GOOGLE Ireland Limited","R-2020/000035"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","S/Fra: 0740/20",0,36.3,"628100000","GASTO TELEFONO","R-2020/000036"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 0740/20",6.3,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000036"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: 0740/20",30,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000036"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 971222000842-8",0,16.73,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000039"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 971222000842-8",3.51,0,"410000018","GOOGLE Ireland Limited","R-2020/000039"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","S/Fra: 971222000842-8",0,3.51,"410000018","GOOGLE Ireland Limited","R-2020/000039"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 971222000842-8",16.73,0,"410000018","GOOGLE Ireland Limited","R-2020/000039"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","S/Fra: A20/000005",0,1047.38,"623000000","Servicios de Profesionales Independientes","R-2020/000042"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: A20/000005",192.94,0,"410000040","JOSEP COMAS ARNAU","R-2020/000042"))
			.addDetail( getAccountEntryDetail("475100002","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Profesionales)","S/Fra: A20/000005",0,64.31,"410000040","JOSEP COMAS ARNAU","R-2020/000042"))
			.addDetail( getAccountEntryDetail("623000000","Servicios de Profesionales Independientes","S/Fra: A20/000005",918.75,0,"410000040","JOSEP COMAS ARNAU","R-2020/000042"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("640000000","Sueldos y salarios.","Nóminas 31/03/2020",10833.04,0,"","",""))
			.addDetail( getAccountEntryDetail("642000000","Seguridad Social a cargo de la empresa.","Nóminas 31/03/2020",986.03,0,"","",""))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Nóminas 31/03/2020",0,3482.2,"","",""))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","Nóminas 31/03/2020",0,4464.3,"","",""))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Nóminas 31/03/2020",0,16467.53,"","",""))
			.addDetail( getAccountEntryDetail("640000001","Sueldos y Salarios desarrollo Plataforma tEDI.center","Nóminas 31/03/2020",9987.99,0,"","",""))
			.addDetail( getAccountEntryDetail("642000001","S.S. desarrollo Plataforma tEDI.center","Nóminas 31/03/2020",2606.97,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000028",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000028"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000028",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000028"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000028",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000028"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000010",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000010"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000010",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000010"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000010",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000010"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000011",12402.5,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000011"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000011",0,2152.5,"430000003","AON SOLUTIONS S.L.","E-2020/000011"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000011",0,10250,"430000003","AON SOLUTIONS S.L.","E-2020/000011"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2004C00829563",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000034"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2004C00829563",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000034"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2004C00829563",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000034"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","Pago Fra: 00000583",85.01,0,"572000001","Banco SANTANDER","R-2020/000032"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO LIMPIEZAS MARI CARMEN ...",0,85.01,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","R-2020/000032"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","Pago Fra: 2020/000356",91.96,0,"572000001","Banco SANTANDER","R-2020/000033"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ZARATE ASESORES,S.L. N...",0,91.96,"410000021","ZARATE ASESORES, S.L.","R-2020/000033"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2004C00829563",98.99,0,"572000001","Banco SANTANDER","R-2020/000034"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000034"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: 522446",0,34.56,"629200000","CURSOS Y GASTOS DE FORMACION","G-2020/000003"))
			.addDetail( getAccountEntryDetail("629200000","CURSOS Y GASTOS DE FORMACION","S/Fra: 522446",34.56,0,"410000036","ACREEDORES (varios)","G-2020/000003"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000039","KGM Marketing s.l.u","Pago Fra: 7315",15000,0,"572000001","Banco SANTANDER","R-2020/000017"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Transferencia A KGM Marketing...",0,15000,"410000039","KGM Marketing s.l.u","R-2020/000017"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("470820191","ALAVA INNOVA (2019)","Transferencia AFA/DFA - Conce...",0,10190.28,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Transferencia AFA/DFA - Conce...",10190.28,0,"470820191","ALAVA INNOVA (2019)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Cobro Fra: 2020/000009 (R:2886)",0,2604.53,"572000001","Banco SANTANDER","E-2020/000009"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","UDAPA Marzo",2604.53,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-78885",0,0.7,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000037"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-78885",0.12,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000037"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-78885",0.58,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000037"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020007536",0,621.35,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000038"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020007536",107.84,0,"410000024","AUSARTA Prima S.L.","R-2020/000038"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020007536",513.51,0,"410000024","AUSARTA Prima S.L.","R-2020/000038"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("626000000","Servicios Bancarios y similares","Liquidacion Por Emision",10.1,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Liquidacion Por Emision",0,10.1,"626000000","Servicios Bancarios y similares",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3716943396",31.2,0,"572000001","Banco SANTANDER","R-2020/000035"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *GSUITE_translo...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2020/000035"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","COMPRA GOOGLE *CLOUD_018CA0-0...",0.27,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *CLOUD_018CA0-0...",0,0.27,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 971222000842-8",16.73,0,"572000001","Banco SANTANDER","R-2020/000039"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *ADS9398321100,...",0,16.73,"410000018","GOOGLE Ireland Limited","R-2020/000039"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00029820-0420",0,38.68,"628100000","GASTO TELEFONO","R-2020/000040"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00029820-0420",6.71,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000040"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00029820-0420",31.97,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000040"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020007536",621.35,0,"572000001","Banco SANTANDER","R-2020/000038"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ENERGIA ELECTRICA N? R...",0,621.35,"410000024","AUSARTA Prima S.L.","R-2020/000038"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: 14.438",66.55,0,"572000001","Banco SANTANDER","R-2019/000138"))
			.addDetail( getAccountEntryDetail("410000028","AYUNTAMIENTO de Vitoria-Gasteiz","Pago Fra: Consumo Agua 2019/4T",60.61,0,"572000001","Banco SANTANDER","G-2020/000004"))
			.addDetail( getAccountEntryDetail("410000028","AYUNTAMIENTO de Vitoria-Gasteiz","Pago Fra: Canon Agua 2019/4T",1.44,0,"572000001","Banco SANTANDER","G-2020/000005"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Anticipos A.Ibaiondo: Ayuntam...",0,128.6,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-78885",0.7,0,"572000001","Banco SANTANDER","R-2020/000037"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Compra Aws Emea, Aws.Amazon.C...",0,0.7,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000037"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: 202764862615",0,13.31,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000041"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 202764862615",2.31,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000041"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 202764862615",11,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000041"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: 202764862615",13.31,0,"572000001","Banco SANTANDER","R-2020/000041"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,13.31,"410000005","1&1 Internet España, S.L.U.","R-2020/000041"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","TRANSFERENCIA A FAVOR DE SERG...",146.63,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA A FAVOR DE SERG...",0,146.63,"465000000","Remuneraciones pendientes de pago.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","TRANSFERENCIA A FAVOR DE SERG...",768.97,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA A FAVOR DE SERG...",0,768.97,"465000000","Remuneraciones pendientes de pago.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","Pago Fra: E1AL00029820-0420",38.68,0,"572000001","Banco SANTANDER","R-2020/000040"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ORANGE ESPAGNE S.A. Nº...",0,38.68,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000040"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","S/Fra: 2020/000469",0,91.96,"623000002","ASESORIA FISCAL/LABORAL/CONTABLE","R-2020/000045"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000469",15.96,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000045"))
			.addDetail( getAccountEntryDetail("623000002","ASESORIA FISCAL/LABORAL/CONTABLE","S/Fra: 2020/000469",76,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000045"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","Pago Fra: A20/000005 (R:2891)",1047.38,0,"572000001","Banco SANTANDER","R-2020/000042"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Josep Marzo",0,1047.38,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000010",0,18150,"572000001","Banco SANTANDER","E-2020/000010"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",18150,0,"430000003","AON SOLUTIONS S.L.","E-2020/000010"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: CENIT/000028",0,2480.5,"572000001","Banco SANTANDER","E-CENIT/000028"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",2480.5,0,"430000003","AON SOLUTIONS S.L.","E-CENIT/000028"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","Pago Fra: 2020/000469",91.96,0,"572000001","Banco SANTANDER","R-2020/000045"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ZARATE ASESORES,S.L. N...",0,91.96,"410000021","ZARATE ASESORES, S.L.","R-2020/000045"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Cuota ABRIL",0,1333.33,"520000003","Préstamo LOCAL",""))
			.addDetail( getAccountEntryDetail("520000003","Préstamo LOCAL","Cuota ABRIL",1261.27,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","Cuota ABRIL",72.06,0,"520000003","Préstamo LOCAL",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",1130.33,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",4788,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",694.58,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",2407.15,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",2401.28,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",1931.14,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 30/04/2020 (R:2...",1808.95,0,"572000001","Banco SANTANDER","NÓMINA - 30/04/2020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Nominas ABRIL /2020",0,16458.87,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",4330.14,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,4330.14,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","RECIBO TGSS. COTIZACION 001 R...",134.16,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO TGSS. COTIZACION 001 R...",0,134.16,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 0740/20",36.3,0,"572000001","Banco SANTANDER","R-2020/000036"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000036"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","S/Fra: 0993/20",0,36.3,"628100000","GASTO TELEFONO","R-2020/000046"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 0993/20",6.3,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000046"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: 0993/20",30,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000046"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("640000000","Sueldos y salarios.","Nóminas 30/04/2020",20821.07,0,"","",""))
			.addDetail( getAccountEntryDetail("642000000","Seguridad Social a cargo de la empresa.","Nóminas 30/04/2020",4350.67,0,"","",""))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Nóminas 30/04/2020",0,3482.2,"","",""))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","Nóminas 30/04/2020",0,5230.63,"","",""))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Nóminas 30/04/2020",0,16458.91,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3726380014",0,31.2,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000048"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 3726380014",31.2,0,"410000018","GOOGLE Ireland Limited","R-2020/000048"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra: 2020/000012",2032.8,0,"700000000","Ventas de mercaderías.","E-2020/000012"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000012",0,352.8,"430000006","UDAPA, S.COOP.","E-2020/000012"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra: 2020/000012",0,1680,"430000006","UDAPA, S.COOP.","E-2020/000012"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2005C00522212",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000050"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2005C00522212",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000050"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2005C00522212",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000050"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000029",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000029"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000029",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000029"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000029",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000029"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000013",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000013"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000013",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000013"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000013",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000013"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000014",12160.5,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000014"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000014",0,2110.5,"430000003","AON SOLUTIONS S.L.","E-2020/000014"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000014",0,10050,"430000003","AON SOLUTIONS S.L.","E-2020/000014"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-120168",0,0.72,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000049"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-120168",0.12,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000049"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-120168",0.6,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000049"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020010104",0,642.05,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000047"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020010104",111.43,0,"410000024","AUSARTA Prima S.L.","R-2020/000047"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020010104",530.62,0,"410000024","AUSARTA Prima S.L.","R-2020/000047"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2005C00522212",98.99,0,"572000001","Banco SANTANDER","R-2020/000050"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000050"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","RECIBO LIMPIEZAS MARI CARMEN ...",85.01,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO LIMPIEZAS MARI CARMEN ...",0,85.01,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.","GESTION DE DEVOLUCIONES -EXTE...",0,85.01,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","GESTION DE DEVOLUCIONES -EXTE...",85.01,0,"410000038","LIMPIEZAS MARI CARMEN ESTEBAN E HIJO S.L.",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3726380014",31.2,0,"572000001","Banco SANTANDER","R-2020/000048"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE*GSUITE TRANSLOG...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2020/000048"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Cobro Fra: 2020/000012 (R:2895)",0,2032.8,"572000001","Banco SANTANDER","E-2020/000012"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","UDAPA ABRIL",2032.8,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","LIQUIDACION POR EMISION",8.03,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","LIQUIDACION POR EMISION",0,8.03,"410000027","Banco Santander, S.A. (Acreedor)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020010104",642.05,0,"572000001","Banco SANTANDER","R-2020/000047"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ENERGIA ELECTRICA Nº R...",0,642.05,"410000024","AUSARTA Prima S.L.","R-2020/000047"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-120168",0.72,0,"572000001","Banco SANTANDER","R-2020/000049"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA AWS EMEA, aws.amazon.c...",0,0.72,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000049"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","S/Fra: A20/000007",0,342,"623000000","Servicios de Profesionales Independientes","R-2020/000051"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: A20/000007",63,0,"410000040","JOSEP COMAS ARNAU","R-2020/000051"))
			.addDetail( getAccountEntryDetail("475100002","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Profesionales)","S/Fra: A20/000007",0,21,"410000040","JOSEP COMAS ARNAU","R-2020/000051"))
			.addDetail( getAccountEntryDetail("623000000","Servicios de Profesionales Independientes","S/Fra: A20/000007",300,0,"410000040","JOSEP COMAS ARNAU","R-2020/000051"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00030141-0520",0,39.14,"628100000","GASTO TELEFONO","R-2020/000052"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00030141-0520",6.79,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000052"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00030141-0520",32.35,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000052"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: 202765148820",0,13.31,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000053"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 202765148820",2.31,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000053"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 202765148820",11,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000053"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: 202765148820",13.31,0,"572000001","Banco SANTANDER","R-2020/000053"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,13.31,"410000005","1&1 Internet España, S.L.U.","R-2020/000053"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 0993/20",36.3,0,"572000001","Banco SANTANDER","R-2020/000046"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000046"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("730000000","Gastos de PERSONAL para la Plataforma tEDI.center","Gastos Personal ENERO",0,15397.95,"206002020","Desarrollo tEDI.center (2020)",""))
			.addDetail( getAccountEntryDetail("206002020","Desarrollo tEDI.center (2020)","Gastos Personal ENERO",15397.95,0,"730000000","Gastos de PERSONAL para la Plataforma tEDI.center",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("730000000","Gastos de PERSONAL para la Plataforma tEDI.center","Gastos Personal FEBRERO",0,13175.99,"206002020","Desarrollo tEDI.center (2020)",""))
			.addDetail( getAccountEntryDetail("206002020","Desarrollo tEDI.center (2020)","Gastos Personal FEBRERO",13175.99,0,"730000000","Gastos de PERSONAL para la Plataforma tEDI.center",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("730000000","Gastos de PERSONAL para la Plataforma tEDI.center","Gastos Personal MARZO",0,12594.96,"206002020","Desarrollo tEDI.center (2020)",""))
			.addDetail( getAccountEntryDetail("206002020","Desarrollo tEDI.center (2020)","Gastos Personal MARZO",12594.96,0,"730000000","Gastos de PERSONAL para la Plataforma tEDI.center",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","Pago Fra: E1AL00030141-0520",39.14,0,"572000001","Banco SANTANDER","R-2020/000052"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ORANGE ESPAGNE S.A. Nº...",0,39.14,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000052"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000007","GRUPO EMPRESARIAL DE GESTION ADIGEST, S.L.","Pago Fra: A 2019474 (R:2900)",4502.52,0,"572000001","Banco SANTANDER","R-2019/000134"))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","Pago Fra: A20/000007 (R:2900)",342,0,"572000001","Banco SANTANDER","R-2020/000051"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Pagos Mayo",0,4844.52,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000013",0,18150,"572000001","Banco SANTANDER","E-2020/000013"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",18150,0,"430000003","AON SOLUTIONS S.L.","E-2020/000013"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: 2020/000014",0,12160.5,"572000001","Banco SANTANDER","E-2020/000014"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",12160.5,0,"430000003","AON SOLUTIONS S.L.","E-2020/000014"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","Cobro Fra: CENIT/000029",0,2480.5,"572000001","Banco SANTANDER","E-CENIT/000029"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSFERENCIA DE AON SOLUTION...",2480.5,0,"430000003","AON SOLUTIONS S.L.","E-CENIT/000029"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Cuota MAYO",0,1333.33,"520000003","Préstamo LOCAL",""))
			.addDetail( getAccountEntryDetail("520000003","Préstamo LOCAL","Cuota MAYO",1261.82,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("662000000","INTERESES DE DEUDAS CON ENTIDADES DE CRÉDITO","Cuota MAYO",71.51,0,"520000003","Préstamo LOCAL",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","S/Fra: 2020/000571",0,91.96,"623000002","ASESORIA FISCAL/LABORAL/CONTABLE","R-2020/000054"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000571",15.96,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000054"))
			.addDetail( getAccountEntryDetail("623000002","ASESORIA FISCAL/LABORAL/CONTABLE","S/Fra: 2020/000571",76,0,"410000021","ZARATE ASESORES, S.L.","R-2020/000054"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",1130.33,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",4788,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",694.58,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",648.72,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",2407.15,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",1931.14,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",1808.95,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Pago NÓMINA - 31/05/2020 (R:2...",2362.7,0,"572000001","Banco SANTANDER","NÓMINA - 31/05/2020"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Nominas MAYO /2020",0,16420.29,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","29/05/2020RECIBO TGSS. COTIZA...",134.16,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","29/05/2020RECIBO TGSS. COTIZA...",0,134.16,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","29/05/2020RECIBO TGSS. COTIZA...",5077.64,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","29/05/2020RECIBO TGSS. COTIZA...",0,5077.64,"476000000","S.S. ACREEDORA",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("640000000","Sueldos y salarios.","Nóminas 31/05/2020",20769.93,0,"","",""))
			.addDetail( getAccountEntryDetail("642000000","Seguridad Social a cargo de la empresa.","Nóminas 31/05/2020",4316.37,0,"","",""))
			.addDetail( getAccountEntryDetail("475100000","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)","Nóminas 31/05/2020",0,3476.57,"","",""))
			.addDetail( getAccountEntryDetail("476000000","S.S. ACREEDORA","Nóminas 31/05/2020",0,5189.39,"","",""))
			.addDetail( getAccountEntryDetail("465000000","Remuneraciones pendientes de pago.","Nóminas 31/05/2020",0,16420.34,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","N/Fra: CENIT/000030",121,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000030"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000030",0,21,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000030"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000030",0,100,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000030"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","Cobro Fra: CENIT/000030",0,121,"570000000","Caja, euros.","E-CENIT/000030"))
			.addDetail( getAccountEntryDetail("570000000","Caja, euros.","Cobro Fra: CENIT/000030",121,0,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000030"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","S/Fra: 1285/20",0,36.3,"628100000","GASTO TELEFONO","R-2020/000055"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 1285/20",6.3,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000055"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: 1285/20",30,0,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000055"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","N/Fra: CENIT/000031",121,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000031"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000031",0,21,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000031"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000031",0,100,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000031"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","Cobro Fra: CENIT/000031",0,121,"570000000","Caja, euros.","E-CENIT/000031"))
			.addDetail( getAccountEntryDetail("570000000","Caja, euros.","Cobro Fra: CENIT/000031",121,0,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000031"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","S/Fra: 3740847342",0,31.2,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000056"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 3740847342",31.2,0,"410000018","GOOGLE Ireland Limited","R-2020/000056"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","S/Fra: 2006C01020440",0,98.99,"623000003","ALARMA Y SERVICIO DE VIGILANCIA","R-2020/000057"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2006C01020440",17.18,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000057"))
			.addDetail( getAccountEntryDetail("623000003","ALARMA Y SERVICIO DE VIGILANCIA","S/Fra: 2006C01020440",81.81,0,"410000022","SECURITAS DIRECT España, SAU","R-2020/000057"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000022","SECURITAS DIRECT España, SAU","Pago Fra: 2006C01020440",98.99,0,"572000001","Banco SANTANDER","R-2020/000057"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO SECURITAS DIRECT ESPAN...",0,98.99,"410000022","SECURITAS DIRECT España, SAU","R-2020/000057"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","S/Fra: EREES20-136291",0,0.71,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000058"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: EREES20-136291",0.12,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000058"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: EREES20-136291",0.59,0,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000058"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: CENIT/000032",2480.5,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000032"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000032",0,430.5,"430000003","AON SOLUTIONS S.L.","E-CENIT/000032"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000032",0,2050,"430000003","AON SOLUTIONS S.L.","E-CENIT/000032"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000016",18150,0,"705000001","Servicios SAT aonSolutions","E-2020/000016"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000016",0,3150,"430000003","AON SOLUTIONS S.L.","E-2020/000016"))
			.addDetail( getAccountEntryDetail("705000001","Servicios SAT aonSolutions","N/Fra: 2020/000016",0,15000,"430000003","AON SOLUTIONS S.L.","E-2020/000016"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000003","AON SOLUTIONS S.L.","N/Fra: 2020/000017",12402.5,0,"705000001","Servicos Comerciales aonSolutions","E-2020/000017"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000017",0,2152.5,"430000003","AON SOLUTIONS S.L.","E-2020/000017"))
			.addDetail( getAccountEntryDetail("705000001","Servicos Comerciales aonSolutions","N/Fra: 2020/000017",0,10250,"430000003","AON SOLUTIONS S.L.","E-2020/000017"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000021","ZARATE ASESORES, S.L.","Pago Fra: 2020/000571",91.96,0,"572000001","Banco SANTANDER","R-2020/000054"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ZARATE ASESORES,S.L. N...",0,91.96,"410000021","ZARATE ASESORES, S.L.","R-2020/000054"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000018","GOOGLE Ireland Limited","Pago Fra: 3740847342",31.2,0,"572000001","Banco SANTANDER","R-2020/000056"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA GOOGLE *GSUITE_translo...",0,31.2,"410000018","GOOGLE Ireland Limited","R-2020/000056"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","S/Fra: PR2020012716",0,621.35,"628000001","SUMINISTROS (Electricidad, Agua...)","R-2020/000059"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: PR2020012716",107.84,0,"410000024","AUSARTA Prima S.L.","R-2020/000059"))
			.addDetail( getAccountEntryDetail("628000001","SUMINISTROS (Electricidad, Agua...)","S/Fra: PR2020012716",513.51,0,"410000024","AUSARTA Prima S.L.","R-2020/000059"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","Pago Fra: EREES20-136291",0.71,0,"572000001","Banco SANTANDER","R-2020/000058"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA AWS EMEA, aws.amazon.c...",0,0.71,"410000016","AMAZON WEB SERVICES EMEA SARL (SUCURSAL EN ESPAÑA)","R-2020/000058"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000041","EROSKI S.C.","S/Fra: 06000070",0,34.68,"629000001","Material de OFICINA","R-2020/000060"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 06000070",5.63,0,"410000041","EROSKI S.C.","R-2020/000060"))
			.addDetail( getAccountEntryDetail("629000001","Material de OFICINA","S/Fra: 06000070",29.05,0,"410000041","EROSKI S.C.","R-2020/000060"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000020","ORANGE ESPAGNE, S.A.U","S/Fra: E1AL00030498-0620",0,41.39,"628100000","GASTO TELEFONO","R-2020/000061"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: E1AL00030498-0620",7.18,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000061"))
			.addDetail( getAccountEntryDetail("628100000","GASTO TELEFONO","S/Fra: E1AL00030498-0620",34.21,0,"410000020","ORANGE ESPAGNE, S.A.U","R-2020/000061"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","Cobro Fra: CENIT/000033",0,121,"570000000","Caja, euros.","E-CENIT/000033"))
			.addDetail( getAccountEntryDetail("570000000","Caja, euros.","Cobro Fra: CENIT/000033",121,0,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000033"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000024","AUSARTA Prima S.L.","Pago Fra: PR2020012716",621.35,0,"572000001","Banco SANTANDER","R-2020/000059"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO ENERGIA ELECTRICA Nº R...",0,621.35,"410000024","AUSARTA Prima S.L.","R-2020/000059"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000041","EROSKI S.C.","Pago Fra: 06000070",34.68,0,"572000001","Banco SANTANDER","R-2020/000060"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","TRANSACCION CONTACTLESS EN ER...",0,34.68,"410000041","EROSKI S.C.","R-2020/000060"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","S/Fra: 202765432267",0,13.31,"628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","R-2020/000062"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 202765432267",2.31,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000062"))
			.addDetail( getAccountEntryDetail("628000000","SUMINISTROS DE INTERNET (Dominios, Alojamiento, Certificados)","S/Fra: 202765432267",11,0,"410000005","1&1 Internet España, S.L.U.","R-2020/000062"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000040","JOSEP COMAS ARNAU","S/Fra: A20/000009",0,299.25,"623000000","Servicios de Profesionales Independientes","R-2020/000063"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: A20/000009",55.13,0,"410000040","JOSEP COMAS ARNAU","R-2020/000063"))
			.addDetail( getAccountEntryDetail("475100002","H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Profesionales)","S/Fra: A20/000009",0,18.38,"410000040","JOSEP COMAS ARNAU","R-2020/000063"))
			.addDetail( getAccountEntryDetail("623000000","Servicios de Profesionales Independientes","S/Fra: A20/000009",262.5,0,"410000040","JOSEP COMAS ARNAU","R-2020/000063"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","S/Fra: F2020127YBBBFHT",0,8.03,"626000000","Servicios Bancarios y similares","R-2020/000064"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: F2020127YBBBFHT",1.39,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000064"))
			.addDetail( getAccountEntryDetail("626000000","Servicios Bancarios y similares","S/Fra: F2020127YBBBFHT",6.64,0,"410000027","Banco Santander, S.A. (Acreedor)","R-2020/000064"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000023","APSER DATA ENGINEERING, S.L.","Pago Fra: 1285/20",36.3,0,"572000001","Banco SANTANDER","R-2020/000055"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO APPSER DATA ENGINEERIN...",0,36.3,"410000023","APSER DATA ENGINEERING, S.L.","R-2020/000055"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000007","NUCAP EUROPE, S.L.","N/Fra: CENIT/000033",121,0,"705100000","Servicios COWORKING - Alquiler espacio","E-CENIT/000033"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: CENIT/000033",0,21,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000033"))
			.addDetail( getAccountEntryDetail("705100000","Servicios COWORKING - Alquiler espacio","N/Fra: CENIT/000033",0,100,"430000007","NUCAP EUROPE, S.L.","E-CENIT/000033"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","N/Fra: 2020/000015",2541,0,"700000000","Ventas de mercaderías.","E-2020/000015"))
			.addDetail( getAccountEntryDetail("477000000","H.P. IVA REPERCUTIDO","N/Fra: 2020/000015",0,441,"430000006","UDAPA, S.COOP.","E-2020/000015"))
			.addDetail( getAccountEntryDetail("700000000","Ventas de mercaderías.","N/Fra: 2020/000015",0,2100,"430000006","UDAPA, S.COOP.","E-2020/000015"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000037","MEDIA MARK VITORIA-GASTEIZ..., S.A.","Pago Fra: 60501325",12.49,0,"570000000","Caja, euros.","R-2020/000065"))
			.addDetail( getAccountEntryDetail("570000000","Caja, euros.","Pago Fra: 60501325",0,12.49,"410000037","MEDIA MARK VITORIA-GASTEIZ..., S.A.","R-2020/000065"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","S/Fra: ORD_276_2020/0017656",0,799,"629000001","Material de OFICINA","R-2020/000066"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: ORD_276_2020/0017656",138.67,0,"410000036","ACREEDORES (varios)","R-2020/000066"))
			.addDetail( getAccountEntryDetail("629000001","Material de OFICINA","S/Fra: ORD_276_2020/0017656",660.33,0,"410000036","ACREEDORES (varios)","R-2020/000066"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("430000006","UDAPA, S.COOP.","Cobro Fra: 2020/000015 (R:2905)",0,2541,"572000001","Banco SANTANDER","E-2020/000015"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","UDAPA MAYO",2541,0,"","",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000027","Banco Santander, S.A. (Acreedor)","Liquidacion Por Emision",9.87,0,"572000001","Banco SANTANDER",""))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","Liquidacion Por Emision",0,9.87,"410000027","Banco Santander, S.A. (Acreedor)",""))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","S/Fra: 2020/000629",0,6050,"620000001","Gastos de TERCEROS para la Plataforma tEDI.center","R-2020/000067"))
			.addDetail( getAccountEntryDetail("472000000","H.P. IVA SOPORTADO.","S/Fra: 2020/000629",1050,0,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","R-2020/000067"))
			.addDetail( getAccountEntryDetail("620000001","Gastos de TERCEROS para la Plataforma tEDI.center","S/Fra: 2020/000629",5000,0,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","R-2020/000067"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000036","ACREEDORES (varios)","Pago Fra: ORD_276_2020/0017656",799,0,"572000001","Banco SANTANDER","R-2020/000066"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","COMPRA IKEA NORTE, SAN SEBAST...",0,799,"410000036","ACREEDORES (varios)","R-2020/000066"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","Pago Fra: 2020/000629",6050,0,"572000001","Banco SANTANDER","R-2020/000067"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO DAMOS SOLUCIONES INFOR...",0,6050,"410000008","DAMOS SOLUCIONES INFORMATICAS, S.L.","R-2020/000067"))
			);

		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( AonRandom.getRandomYearDay(year))
			.addDetail( getAccountEntryDetail("410000005","1&1 Internet España, S.L.U.","Pago Fra: 202765432267",13.31,0,"572000001","Banco SANTANDER","R-2020/000062"))
			.addDetail( getAccountEntryDetail("572000001","Banco SANTANDER","RECIBO 1y1 IONOS Espana S.L.U...",0,13.31,"410000005","1&1 Internet España, S.L.U.","R-2020/000062"))
			);

		
	}
	
	private static AccountEntryDetail getAccountEntryDetail(
			String accountCode,
			String accountDescription,
			String concept, 
			double debit,
			double credit, 
			String balancingAccountCode,
			String balancingAccountDescription,
			String documentNumber) {
		Account account = ACCOUNTING.getAccount(ctx,accountCode);
		if (account == null) {
			account = ACCOUNTING.save(DOMAIN_NAME,DOMAIN_ID,USER, new Account()
				.setDomain(DOMAIN_ID)
				.setCode(accountCode)
				.setDescription(accountDescription)
				.setActive(true));
		}
		Account balancingAccount = null;
		if (AonStringUtils.isNotBlank(balancingAccountCode)) {
			balancingAccount = ACCOUNTING.getAccount(ctx,balancingAccountCode);
			if (balancingAccount == null) {
				balancingAccount = ACCOUNTING.save(DOMAIN_NAME,DOMAIN_ID,USER, new Account()
						.setDomain(DOMAIN_ID)
						.setCode(balancingAccountCode)
						.setDescription(balancingAccountDescription)
						.setActive(true));
			}
		}
		return new AccountEntryDetail()
			.setAccount( account.getId() )
			.setAccountCode(account.getCode())
			.setAccountDescription(account.getDescription())
			.setConcept(concept)
			.setDebit(debit)
			.setCredit(credit)
			.setBalancingAccount(balancingAccount == null ? null : balancingAccount.getId())
			.setBalancingAccountCode(balancingAccount == null ? null : balancingAccount.getCode())
			.setBalancingAccountDescription(balancingAccount == null ? null : balancingAccount.getDescription())
			.setDocumentNumber(documentNumber);
	}	
	
}

