package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

//////////////////////////////////////////////////////////////////////////////////////////
//                                                                                      //
// IMPORTANTE:                                                                          //   
// Además de este enumerado, tambien existe el enumerado Mod2002023KeyDC que lleva      //
// las casillas del detalle de las correcciones al resultado contable, que no se han    //
// podido poner aqui, porque son muchas casillas (todas las correcciones al resultado   //
// contable llevan 5 casillas de detalle a partir de 2019) y hacen que este enumerado   //
// genere un error de compilación por tener demasiados elementos.                       //
//                                                                                      //
//////////////////////////////////////////////////////////////////////////////////////////

public enum Mod2002023Key implements IMod200Key {

// --------------- PAGINA 1 --------------- // 	
	
	// CODIGO ESPECIALES	
     X0000 // Tipo de ejercicio
    ,X0001 // Realiza actividades agrícolas y/o ganaderas
    
	// CARACTERES DE LA DECLARACION
	// Tipo de Entidad     		
	,C0001	,C0013	,C0032
	,C0002	,C0014  ,C0036
	,C0080	,C0017  ,C0048
	,C0003	,C0018  ,C0058
	,C0008  ,C0019	,C0060  
	,C0004	,C0021  ,C0066
	,C0005	,C0023  ,C0078
	,C0011	,C0024  ,C0056
			,C0025	,C0083			
			,C0031	  
		
	// Regímenes aplicables
	,C0006	,C0049	,C0046 
	,C0015  ,C0035  ,C0012 ,C0012R 
	,C0079  ,C0029  ,C0064
	,C0022  ,C0069  ,C0057 
	,C0028  ,C0033  ,C0062 
	,C0047  ,C0034  ,C0020 
	        ,C0038
	        
	// Otros caracteres
	,C0007	,C0030	,C0059		
	,C0009  ,C0039  ,C0065
	,C0010  ,C0043  ,C0084   
	,C0081  ,C0045  ,C0072
	,C0082	,C0063  ,C0073
	,C0016  ,C0071  ,C0037 		
	,C0026  ,C0070  ,C0044
	,C0027    		,C0074

	// Importe neto de la cifra de negocios (INCN) de los doce meses anteriores a la fecha de inicio del período impositivo
	// 1 - INCN inferior a 20 millones de euros					
	// 2 - INCN de al menos 20 millones de euros pero inferior a 60 millones de euros					
	// 3 - INCN de al menos 60 millones de euros					
	,VOLOPE
	
	// ESTADOS DE CUENTAS
	,C0050	,C0075	,C0053
	,C0051  ,C0076  ,C0054
	,C0052  ,C0077  ,C0055  
	
	,C0061
	,C0068
	
	// PERSONAL ASALARIADO
	,C0041	,C0042
	
// --------------- PAGINA 2 --------------- //	
	
	// PARTICIPACIONES DIRECTAS (Totales)
	,P1501
	,P1502
	,P1503
	,P1504
	,P1506
	,P1809
	,P1810
	,P1507
	,P1508

	,POR51 // Suma de porcentajes de participación de personas o entidades en el capital de la declarante inferiores al 5% o al 1% si se trata de valores que coticen en un mercado secundario organizado
	,PORES // Suma de porcentajes de participaciones en situaciones especiales 
	
// --------------- PAGINA 3 --------------- //
	
	// BALANCE: ACTIVO (I)
	,BA101
	,BA102
	,BA103
	,BA104
	,BA105
	,BA106
	,BA107
	,BA108
	,BA700
	,BA109
	,BA110
	,BA111
	,BA112
	,BA113
	,BA114
	,BA115
	,BA116
	,BA117
	,BA118
	,BA119
	,BA120
	,BA121
	,BA122
	,BA123
	,BA124
	,BA125
	,BA126
	,BA127
	,BA128
	,BA129
	,BA130
	,BA131
	,BA132
	,BA133
	,BA134
	,BA135
	,BA136
	,BA137
	,BA138
	,BA139
	,BA140
	,BA141
	,BA142
	,BA143
	,BA144
	,BA145
	,BA146
	,BA147
	,BA148
	,BA701

// --------------- PAGINA 4 --------------- //
	
	// BALANCE: ACTIVO (II)
	,BA149
	,BA150
	,BA151
	,BA152
	,BA153
	,BA154
	,BA155
	,BA156
	,BA157
	,BA158
	,BA159
	,BA160
	,BA161
	,BA162
	,BA163
	,BA164
	,BA165
	,BA166
	,BA167
	,BA168
	,BA169
	,BA170
	,BA171
	,BA172
	,BA173
	,BA174
	,BA175
	,BA176
	,BA177
	,BA178
	,BA179
	,BA180
	
// --------------- PAGINA 5 --------------- //	
	
	// BALANCE: PATRIMONIO NETO Y PASIVO (I)
	,BP185
	,BP186
	,BP187
	,BP188
	,BP189
	,BP190
	,BP191
	,BP192
	,BP193
	,BP702
	,BP1001
	,BP1002
	,BP712	
	,BP194
	,BP195
	,BP196
	,BP197
	,BP198
	,BP199
	,BP200
	,BP201
	,BP202
	,BP203
	,BP204
	,BP205
	,BP206
	,BP207
	,BP208
	,BP209
	,BP210
	,BP211
	,BP212
	,BP213
	,BP214
	,BP215
	,BP216
	,BP217
	,BP218
	,BP219
	,BP220
	,BP221
	,BP222
	,BP223
	,BP224
	,BP225
	,BP226
	,BP227
	
// --------------- PAGINA 6 --------------- //	
	
	// BALANCE: PATRIMONIO NETO Y PASIVO (II)	
	,BP228
	,BP229
	,BP230
	,BP703
	,BP704
	,BP231
	,BP232
	,BP233
	,BP234
	,BP235
	,BP236
	,BP237
	,BP238
	,BP239
	,BP240
	,BP241
	,BP242
	,BP243
	,BP244
	,BP245
	,BP246
	,BP247
	,BP248
	,BP249
	,BP250
	,BP251
	,BP252
	
// --------------- PAGINA 7 --------------- //	
	
	// CUENTA DE PERDIDAS Y GANANCIAS (I)
	,PG255
	,PG256
	,PG257
	,PG711
	,PG705
	,PG706
	,PG707
	,PG708
	,PG258
	,PG259
	,PG260
	,PG261
	,PG760
	,PG761
	,PG262
	,PG762
	,PG763
	,PG263
	,PG264
	,PG265
	,PG266
	,PG267
	,PG268
	,PG269
	,PG270
	,PG271
	,PG273
	,PG274
	,PG275
	,PG276
	,PG277
	,PG278
	,PG279
	,PG280
	,PG253
	,PG254
	,PG281
	,PG282
	,PG283
	,PG709
	,PG284
	,PG285
	,PG286
	,PG287
	,PG288
	,PG289
	,PG290
	,PG291
	,PG292
	,PG293
	,PG710
	,PG294
	,PG295
	,PG296
	
// --------------- PAGINA 8 --------------- //	
	
	// CUENTA DE PERDIDAS Y GANANCIAS (II)
	
	,PG297
	,PG298
	,PG299
	,PG300
	,PG301
	,PG302
	,PG303
	,PG304
	,PG305
	,PG306
	,PG307
	,PG308
	,PG309
	,PG310
	,PG311
	,PG312
	,PG313
	,PG314
	,PG315
	,PG316
	,PG317
	,PG318
	,PG319
	,PG320
	,PG321
	,PG322
	,PG323
	,PG329
	,PG330
	,PG331
	,PG332
	,PG324
	,PG325
	,PG326
	,PG327
	,PG328
	,PG500
	
// --------------- PAGINA 9 --------------- //	
	
	// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
	,T0500
	,T0336
	,T0337
	,T0338
	,T0339
	,T0340
	,T0341
	,T0342
	,T0343
	,T0344
	,T0345
	,T0346
	,T0347
	,T0348
	,T0349
	,T0350
	,T0351
	,T0352
	,T0353
	,T0354
	,T0355
	
// --------------- PAGINAS 10 Y 11 --------------- //	
	
	// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
	,TC380	,TC381	,TC382	,TC383	,TC384	,TC385	,TC386		,TC387	,TC388	,TC389	,TC390	,TC391	,TC392	,TC393
	,TC394	,TC395	,TC396	,TC397	,TC398	,TC399	,TC400	    ,TC401	,TC402	,TC403	,TC404	,TC405	,TC406	,TC407
	,TC408	,TC409	,TC410	,TC411	,TC412	,TC413	,TC414	    ,TC415	,TC416	,TC417	,TC418	,TC419	,TC420	,TC421
	,TC422	,TC423	,TC424	,TC425	,TC426	,TC427	,TC428	    ,TC429	,TC430	,TC431	,TC432	,TC433	,TC434	,TC435
	,TC436	,TC437	,TC438	,TC439	,TC440	,TC441	,TC442	    ,TC443	,TC444	,TC445	,TC446			,TC448	,TC449	
	,TC450	,TC451	,TC452	,TC453	,TC454	,TC455	,TC456	    ,TC457	,TC458					,TC461	,TC462	,TC463	
	,TC464	,TC465	,TC466	,TC467	,TC468	,TC469	,TC470	    ,TC471	,TC472					,TC475	,TC476	,TC477	
	,TC478	,TC479	,TC480	,TC481	,TC482	,TC483	,TC484	    ,TC485	,TC486					,TC489	,TC490	,TC491
	,TC492	,TC493	,TC494	,TC495	,TC496	,TC497	,TC498	    ,TC499	,TC502					,TC503	,TC504	,TC505
	,TC506	,TC507	,TC508	,TC509	,TC510	,TC511	,TC512	    ,TC513	,TC514	,TC515	,TC516	,TC517	,TC518	,TC519
	,TC520	,TC521	,TC522	,TC523	,TC524	,TC525	,TC526	    ,TC527	,TC528	,TC529	,TC530	,TC531	,TC532	,TC533
	,TC534	,TC535	,TC536	,TC537	,TC538	,TC539	,TC540	    ,TC541	,TC542	,TC543	,TC544	,TC545	,TC546	,TC547
	,TC548	,TC549	,TC550	,TC551	,TC552	,TC553	,TC554	    ,TC555	,TC556	,TC557	,TC558			,TC560	,TC561
	,TC562	,TC563	,TC564	,TC565	,TC566	,TC567	,TC568	    ,TC569	,TC570	,TC571	,TC572			,TC574	,TC575
	,TC576	,TC577	,TC578	,TC579	,TC580	,TC581	,TC582	    ,TC583	,TC584	,TC585	,TC586			,TC588	,TC589
	,TC590	,TC591	,TC592	,TC593	,TC594	,TC595	,TC596	    ,TC597	,TC598	,TC599	,TC600			,TC602	,TC603
	,TC604	,TC605	,TC606	,TC607	,TC608	,TC609	,TC610	    ,TC611	,TC612	,TC613	,TC614	,TC615	,TC616	,TC617
	,TC618	,TC619	,TC620	,TC621	,TC622	,TC623	,TC624	    ,TC625	,TC626	,TC627	,TC628	,TC629	,TC630	,TC631
	,TC715	,TC716	,TC717	,TC718	,TC719	,TC720	,TC721	    ,TC722	,TC723	,TC724	,TC725	,TC726	,TC727	,TC728
	,TC729	,TC730	,TC731	,TC732	,TC733	,TC734	,TC735	    ,TC736	,TC737	,TC738	,TC739	,TC740	,TC741	,TC742
	,TC632	,TC633	,TC634	,TC635	,TC636	,TC637	,TC638	    ,TC639	,TC640	,TC641	,TC642	,TC643	,TC644	,TC645
	
// --------------- PAGINAS 12 Y 13 --------------- //	
	
	// LIQUIDACION 
	
	// Resultado de la Cuenta de Perdidas y Ganancias
	,LQ500
	,LQ301	,LQ302
	,LQ501
	,LQ1230	,LQ1231
	
	// Correcciones al resultado de la cuenta de pérdidas y ganancias (excluida la corrección por IS)
	// I=Aumentos, D=Disminuciones
	,I0355	,D0356
	,I0357	,D0358
	,I0359	,D0360
	,I0225	,D0226
	,I1514	,D0272
	,I0361	,D0362
	,I0303	,D0304
			,D0505
	,I1005	,D1006
	,I0305	,D0306
	,I0307	,D0308
	,I1003	,D1004
	,I0309	,D0310
	,I0514	,D0509
	,I0516	,D0551
	,I0321	,D0322
	,I0415	,D0211
	,I0331	,D0332
	,I0325	,D0326
	,I0327	,D0328
	,I0416	,D0543
	,I0335	,D0336
	,I0337	,D0338
			,D0368	
	,I1002
	,I1815
	,I0343
	,I0339
	,I1816
	,I0341	,D0342
	,I0508
	,I1817
	,I2469	,D2470
	,I0333	,D0334
	,I1807	,D1811
	,I1808	,D1812
	,I1813	,D1814
	,I2311
	,I0363	,D0364
	,I0345	,D0346
	,I1818	,D1819
	,I0371
	,I0347	,D0348
	,I1011	,D1012	
	,I1572	,D1573
	,I1574	,D1575	
	,I1015	,D1016
			,D0370
			,D2181
			,D1764
			,D1765
	,I2182	,D2183
	,I2184	,D2185
	,I2186	,D2187
	,I2188	,D2189
	,I0256	,D0278
	,I1822	,D0372
	,I0373	,D0374
	,I0340	,D1589
	,I0351
	,I0375	,D0376
	,I1320	,D1321
	,I0184	,D0544
	,I1022	,D1023
	,I1018	,D1019
	,I1275	,D1276
	,I0377	,D0378
	,I0379	,D0380
	,I0381	,D0382
	,I0383	,D0384
	,I0387	,D0388
	,I0311	,D0312
	,I0313	,D0314
	,I0323	,D0324
	,I0317	,D0318
	,I0385	,D0386
	,I0389	,D0390
			,D0396	
	,I0397	,D0398	
	,I0250	,D0251	
	,I0391	,D0392
			,D0400	
	,I0403	,D0404 // Reserva para inversiones en Canarias
	,I0518	,D0519
			,D1824
	,I1009	,D1013
	,I1905	,D1906
	,I0510	,D0512
	,I0329	,D0330
	,I0365	,D1026
			,D1014
	,I0409	,D0410
	,I0411	,D0412
	,I1027	,D1028
	,I0413	,D0414
	,I0417	,D0418
	
	// Entidades navieras en regimen de tributacion en funcion del tonelaje
	,LQ578
	,LQ579
	
	// Entidades que forman parte de grupos de consolidación fiscal	
	,LQ1029
	,LQ1030
	,LQ1031
	
	// Base imponible
	,LQ550
	
	// Según el formato del fichero aqui hay dos casillas mas sin numeración (se usan en SOCIMIS)
	,LQ550TG  // Parte de la base imponible del período impositivo que tributa al tipo general (antes de compensación de bases imponibles negativas) 
	,LQ550T0  // Parte de la base imponible del período impositivo que tributa al tipo del 0% (antes de compensación de bases imponibles negativas)
	
	,LQ1032   // Reserva de capitalizacion
	
	,LQ541	  // R.Esp.Navieras en Canarias: Parte de la base imponible que proceda de la realización de actividades a las que se aplica el régimen especial
	,LQ564	  // R.Esp.Navieras en Canarias: Parte de la base imponible que proceda de la realización del resto de actividades
	
	,LQ547    // Compensación de bases imponibles negativas periodos anteriores
	
	,LQ1887   // R.Esp.Navieras en Canarias: Compensación de bases imponibles negativas períodos anteriores de la parte de base imponible régimen especial
	,LQ1890	  // R.Esp.Navieras en Canarias: Compensación de bases imponibles negativas períodos anteriores de la parte de base imponible resto de actividades		
	
	,LQ552    // Base imponible
	,LQ1033	,LQ1034 // Solo entidades de reducida dimensión (Reserva de Nivelación)
	
// --------------- PAGINA 14 --------------- //	

	// LIQUIDACION (III)

	,LQ1330 // Base imponible despues de la reserva de nivelación
	
	// Cooperativas
	,LQ553
	,LQ554

	// Agrupaciones interés económico y UTE's		
	,LQ555
	,LQ556
	
	// Entidades ZEC
	,LQ559
	
	// Solo SOCIMIS
	,LQ520
	,LQ521
	
	// Rentas que no limitan las compensación de bases imponibles y cuotas negativas
	,LQ545
	,LQ1509
	
	// Régimen especial de buques y empresas navieras en Canarias
	,LQ1576
	,LQ1577	
	
	// Tipo de Gravamen
	,LQ558
	
	// Sólo cooperativas
	,LQ560
	,LQ210	,LQ480
	,LQ408	,LQ1037
	,LQ593
	,LQ1510
	,LQ561
	,LQ1285	,LQ1286
	,LQ1331
		
	// Cuota integra		
	,LQ562
	,LQ1038
	
	// Bonificaciones y deducciones por doble imposición. Cuota integra ajustada positiva.
	,BN567
	,BN568
	,BN563
	,BN566
	,BN576
	,BN569
	,BN570  // DI interna de periodos anteriores aplicada en el ejercicio (art.30 RDL 4/2004)
	,BN1344 // DI interna de periodos anteriores aplicada en el ejercicio (DT 23ª.1 LIS)
	,BN1280 // DI interna generada y aplicada en el ejercicio (DT 23ª.1 LIS)
	,BN572  // DI internacional de periodos anteriores aplicada en el ejercicio (art.31 y 32 RDL 4/2004)
	,BN571  // DI internacional de periodos anteriores aplicada en el ejercicio (art. 31 y 32 LIS)
	,BN573  // DI internacional generada y aplicada en el ejercicio (art. 31 y 32 LIS)
	,BN575
	,BN577
	,BN581
	,BN582
	
	// Otras deducciones. Cuota líquida positiva
	,BN583
	,BN585  // Deducción DT 24ª.7 LIS, art. 42 RDL 4/2004
	,BN584  // Deducciones DT 24ª.1 LIS
	,BN588  // Deducciones con límite del Capítulo IV Título VI y DT 24.3 LIS		
	,BN1039 // Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
	,BN1039M // Importe máximo que desea aplicar
	,BN2314 // Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
	,BN2314M // Importe máximo que desea aplicar
	,BN2315 // Deducción por inversiones realizadas por las autoridades portuarias (art. 38 bis LIS)
	,BN565  // Deducción donaciones a entidades sin fines de lucro (Ley 49/2002)		
	,BN590  // Deducciones Inversión Canarias
	,BN399  // Deducciones específicas de las entidades sometidas a normativa foral
	,BN082  // Deducciones I + D + i excluidas de límite. Opción art. 39.2 LIS
	,BN1040 // Deducción por reversión de medidas temporales DT 37ª.1 LIS
	,BN1041 // Deducción por reversión de medidas temporales DT 37ª.2 LIS
	,BN619  // Cuota líquida mínima (art. 30 bis.2 LIS)
	,BN592  // Cuota líquida 
	
// --------------- PAGINA 14 BIS --------------- //
	
	// LIQUIDACION (IV)
	
	// Cuota del ejercicio a ingresar o a devolver
	,BN1785	,BN1786
	,BN1787	,BN1788
	,BN1789	,BN1790
	,BN1791	,BN1792
	,BN1793	,BN1794
	,BN1795	,BN1796	
	,BN597	,BN1797
	,BN1798	,BN1799
	,LQ1766	,LQ1784
	,BN599	,BN600
	
	// Pagos fraccionados. Cuota diferencial
	,BN601	,BN602
	,BN603	,BN604
	,BN605	,BN606
	,BN611	,BN612
	
	// Resultado de la autoliquidacion
			,BN615	,BN616
			,BN633	,BN642
			,BN617	,BN618			
	,BN1234B,BN083	,BN1332
	,BN1892 ,BN1042	,BN1333
	,BN1319 ,BN1893	,BN1881
			,LQ1586 ,LQ1587
			
	// Líquido a ingresar o a devolver
	,LQ1578 ,LQ1583
	,LQ1584 ,LQ1585
	,BN621	,BN622		
	
	// Opción de fraccionamiento en supuestos de cambios de residencia
	,LQ1588 ,LQ2480
	,LQ2481 ,LQ2482
	,LQ2483 ,LQ2484
	,LQ2485 ,LQ2486
	,LQ2487 ,LQ2488
	,LQ2489 ,LQ3242	
	
	// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria		
	,LM150	,BN1020	,BN1043
	,LM506	,BN1021	,BN1044
	,LQ3243 ,LQ3244 ,LQ3245
	,LQ3317 ,LQ3318 ,LQ3319
	,LQ3320 ,LQ2490 ,LQ2491
	,LQ2492 ,LQ2493 ,LQ2494
	
// --------------- PAGINA 15 --------------- //
	
	// Desglose LQ547 - Compensación de bases imponibles negativas periodos anteriores 	
	,LQ640	,LQ641	,LQ548
	,LQ643	,LQ644	,LQ645
	,LQ646	,LQ647	,LQ648
	,LQ649	,LQ650	,LQ651
	,LQ652	,LQ653	,LQ654
	,LQ655	,LQ656	,LQ657
	,LQ658	,LQ659	,LQ660
	,LQ661	,LQ662	,LQ663
	,LQ664	,LQ665	,LQ666
	,LQ667	,LQ668	,LQ669
	,LQ743	,LQ747	,LQ748
	,LQ275	,LQ276	,LQ277
	,LQ608	,LQ609	,LQ610
	,LQ704	,LQ705	,LQ706
	,LQ013	,LQ014	,LQ015
	,LQ725	,LQ726	,LQ727
	,LQ534	,LQ535	,LQ536
	,LQ607	,LQ675	,LQ699
	,LQ1045	,LQ1046	,LQ1047
	,LQ1519	,LQ1520	,LQ1521
	,LQ1592	,LQ1593	,LQ1594
	,LQ1825	,LQ1826	,LQ1827
	,LQ2193	,LQ2194	,LQ2195
	,LQ194	,LQ195	,LQ196
	,LQ151	,LQ152	,LQ164	
	,LQ2316	,LQ2317	,LQ2318	
	,LQ670			,LQ671	
	,LQ1048			,LQ1049
	
	// Desglose LQ1887, LQ1890: Régimen especial de buques y empresas navieras en Canarias: desglose de la compensación de bases imponibles negativas
	,LQ168 	,LQ172 	,LQ173
	,LQ175 	,LQ176 	,LQ177
	,LQ178 	,LQ179 	,LQ198
	,LQ202 	,LQ214 	,LQ215
	,LQ1886  		,LQ1888
	,LQ1889 		,LQ1891
	,LQ216 	,LQ243 	,LQ265
	,LQ266 			,LQ267
	,LQ290 	,LQ2465	,LQ344
	
// --------------- PAGINA 15 BIS --------------- //
	
	// Desglose BN570 - Deducciones doble imposición interna RDLeg. 4/2004
	,BN104	,BN105	,BN846	,BN847	,BN848
	,BN106	,BN107	,BN282	,BN283	,BN284
	,BN108	,BN109	,BN702	,BN703	,BN707
	,BN110	,BN111	,BN071	,BN187	,BN300
	,BN112	,BN113	,BN025	,BN026	,BN027
	,BN114	,BN115	,BN714	,BN715	,BN716
	,BN735	,BN920	,BN736	,BN737	,BN738
	,BN116			,BN117			,BN118
	,BN103A
	
	// Desglose BN1344 - Deducciones doble imposición interna periodos anteriores (DT 23ª.1 LIS)
	,BN101	,BN102	,BN119	,BN120	,BN121
	,BN122	,BN123	,BN124	,BN125	,BN126
	,BN1595	,BN1596	,BN1597	,BN1598	,BN1599
	,BN1828	,BN1829	,BN1830	,BN1831	,BN1832
	,BN2196	,BN2197	,BN2198	,BN2199	,BN2200
	,BN2319	,BN2320	,BN2321	,BN2322	,BN2323
	,BN199 	,BN203 	,BN204 	,BN205 	,BN206
	,BN394 	,BN436 	,BN437 	,BN438 	,BN2076  
	,BN1342			,BN1343			,BN1345
	,BN103B
	
	// Desglose BN1280 - DI interna generada y aplicada en el ejercicio (DT 23ª.1 LIS)
	,BN127	,BN128	,BN129
	,BN1346			,BN1347
	
	// Desglose BN572 - Deducciones doble imposición internacional RDLeg. 4/2004
	,BN153	,BN728	,BN637	,BN638	,BN639
	,BN154	,BN729	,BN849	,BN894	,BN197
	,BN155	,BN730	,BN285	,BN286	,BN287
	,BN156	,BN731	,BN825	,BN826	,BN827
	,BN157	,BN732	,BN001	,BN002	,BN003
	,BN158	,BN733	,BN028	,BN029	,BN030
	,BN159	,BN734	,BN717	,BN718	,BN719
	,BN720	,BN721	,BN722	,BN723	,BN724
	,BN739	,BN921	,BN740	,BN741	,BN742
	,BN134	,BN926	,BN135	,BN136	,BN137
	,BN160			,BN161			,BN162
	,BN103C
 
// --------------- PAGINA 16 --------------- //
	
	// Desglose BN571 - Deducciones doble imposición internacional periodos anteriores (LIS)
	,BN1054	,BN1050	,BN1051	,BN1052	,BN1053
	,BN1348	,BN1349	,BN1350	,BN1351	,BN1352
	,BN1770	,BN1771	,BN1772	,BN1773	,BN1774
	,BN1833	,BN1834	,BN1835	,BN1836	,BN1837
	,BN2201	,BN2202	,BN2203	,BN2204	,BN2205
	,BN2324	,BN2325	,BN2326	,BN2327	,BN2328
	,BN207 	,BN208 	,BN209 	,BN212 	,BN213
	,BN490 	,BN491 	,BN492 	,BN493 	,BN620
	,BN131			,BN132			,BN133
	,BN103D
	
	// Desglose BN573 - DI internacional generada y aplicada en el ejercicio (LIS)
	,BN163	,BN165	,BN166
	,BN167	,BN169	,BN170
	,BN171			,BN174

	 // Desglose BN585 - Deducción DT 24ª.7 LIS, art. 42 RDLeg. 4/2004		
	,BN004	,BN005	
	,BN031	,BN032	,BN033
	,BN022	,BN023	,BN024
	,BN040	,BN041	,BN042
	,BN138	,BN139	,BN140
	,BN141	,BN142	,BN143
	,BN188	,BN189	,BN190
	,BN803	,BN804	,BN805
	,BN1055	,BN1056	,BN1057
	,BN700	,BN708	,BN709
	,BN1353	,BN1354	,BN1355
	,BN1775	,BN1776	,BN1777
	,BN1838	,BN1839	,BN1840
	,BN2206	,BN2207	,BN2208
	,BN2329	,BN2330	,BN2331	
	,BN249	,BN252	,BN253
	,BN696	,BN697	,BN710
	,BN841			,BN843
	
	// Desglose BN584 - Deducciones DT 24ª.1 LIS
	,BN749	,BN750	
	,BN752	,BN753	,BN754
	,BN755	,BN756	,BN757
	,BN758	,BN759	,BN760
	,BN761	,BN762	,BN763
	,BN744	,BN745	,BN746
	,BN779	,BN783	,BN784
	,BN764			,BN765
	
// --------------- PAGINA 16 BIS --------------- //	
	
	 // Desglose BN590 - Deducciones Inversión Canarias
	,BN854	,BN855	,BN1356
	,BN857	,BN858	,BN859
	,BN860	,BN861	,BN862
	,BN863	,BN864	,BN865
	,BN883	,BN884	,BN885
	,BN785	,BN789	,BN790
	,BN1357	,BN1358	,BN1359
	,BN1778	,BN1779	,BN1780
	,BN852	,BN853	,BN856
	,BN2116	,BN2117	,BN2118
	,BN2209	,BN2210	,BN2211
	,BN2332	,BN2333	,BN2334
	,BN237	,BN238	,BN239
	,BN711	,BN712	,BN2077  
	,BN2335	,BN2336	,BN2337
	,BN2338	,BN2339	,BN2340
	,BN2341	,BN2342	,BN2343
	,BN2344	,BN2345	,BN2346
	,BN244	,BN245	,BN2497
	,BN2078 ,BN1913 ,BN766  		
	,BN880	,BN881	
	,BN866	,BN867	,BN870
	,BN939	,BN940	,BN941
	,BN191	,BN192	,BN193
	,BN613	,BN614	,BN701
	,BN200	,BN257	,BN011
	,BN037	,BN038	,BN039
	,BN044	,BN045	,BN046
	,BN528	,BN529	,BN530
	,BN144	,BN145	,BN146
	,BN147	,BN148	,BN149
	,BN240	,BN241	,BN242
	,BN1058	,BN1059	,BN1060
	,BN791	,BN802	,BN806
	,BN1781	,BN1782	,BN1783
	,BN2122	,BN2123	,BN2124
	,BN2212	,BN2213	,BN2214
	,BN2347	,BN2348	,BN2349	
	,BN217	,BN218	,BN219
	,BN767	,BN768	,BN769
	,BN2119	,BN2120	,BN2121
	,BN2125	,BN2126	,BN2127
	,BN2215	,BN2216	,BN2217
	,BN2350	,BN2351	,BN2352
	,BN220	,BN221	,BN222
	,BN770	,BN771	,BN774
	,BN886  		,BN887
	
	// Deducciones inversión en Canarias con límites incrementados (continuación)	
	,BN2287
	,BN2288
	,BN2495
	,BN2079
	,BN2496
	,BN2080
	
// --------------- PAGINAS 17, 18 Y 18 BIS --------------- //
	
	// Desglose BN588 - Deducciones con límite del Capítulo IV Título VI y DT 24.3 LIS
	,BN288	,BN289	
	,BN466	,BN467	,BN468
	,BN061	,BN498	,BN586
	,BN472	,BN473	,BN478
	,BN180	,BN181	,BN182
	,BN531	,BN532	,BN533
	,BN945	,BN946	,BN947
	,BN960	,BN961	,BN962
	,BN183	,BN185	,BN186
	,BN966	,BN967	,BN968
	,BN457	,BN458	,BN459
	,BN460	,BN461	,BN462
	,BN1063	,BN1064	,BN1065
	,BN1066	,BN1067	,BN1068
	,BN1069	,BN1070	,BN1071
	,BN2294 ,BN2295 ,BN2296
	,BN986	,BN810	,BN507
	,BN557  ,BN591	,BN594
	,BN2081 ,BN2082 ,BN2083  
	,BN2297 ,BN2298 ,BN2299
	,BN1617	,BN1618	,BN1619
	,BN1620	,BN1621	,BN1622
	,BN2084 ,BN2085 ,BN2086  
	,BN2300 ,BN2500 ,BN2087
	,BN1850	,BN1851	,BN1852
	,BN1853	,BN1854	,BN1855		
	,BN2088 ,BN2089 ,BN2090  
	,BN2091 ,BN2092 ,BN2093
	,BN2221	,BN2222	,BN2223
	,BN2224	,BN2225	,BN2226
	,BN1916 ,BN1917 ,BN1918
	,BN2094 ,BN2095 ,BN2096
	,BN2356	,BN2357	,BN2358
	,BN2359	,BN2360	,BN2361
	,BN1919 ,BN1920 ,BN1921
	,BN2097 ,BN2098 ,BN2099	
	,BN228  ,BN229  ,BN230
	,BN234  ,BN235  ,BN236
	,BN1922 ,BN1923 ,BN1924
	,BN2145 ,BN2146 ,BN2448
	,BN780  ,BN781 	,BN782
	,BN786 	,BN787 	,BN788
	,BN1925 ,BN1926 ,BN1927
	,BN2449 ,BN2450 ,BN2461	
	,BN1363	,BN1364	,BN1365
	,BN1366	,BN1367	,BN1368
	,BN1928 ,BN1929 ,BN1930
	,BN828  ,BN829	,BN830
	,BN798	,BN799	,BN800
	,BN096	,BN698	,BN713
	,BN807	,BN808	,BN809
	,BN2462	,BN2463	,BN2464	
	,BN1075	,BN1076	,BN1077
	,BN2455	,BN2456	,BN2457
	,BN795	,BN796	,BN797
	,BN792 	,BN793 	,BN794
	,BN549	,BN888	,BN889		
	,BN1369	,BN1370	,BN1371
	,BN2190	,BN2191	,BN2192
	,BN1626	,BN1627	,BN1628
	,BN1638	,BN1639	,BN1640
	,BN1707	,BN1708	,BN1709	
	,BN1907	,BN1908	,BN1909  
	,BN1910	,BN1911	,BN1912  
	,BN1934	,BN1935	,BN1936
	,BN2362	,BN2363	,BN2364
	,BN2365	,BN2366	,BN2367
	,BN2368	,BN2369	,BN2370
	,BN2371	,BN2372	,BN2373
	,BN2374	,BN2375	,BN2376
	,BN2377	,BN2378	,BN2379
	,BN254  ,BN255  ,BN258
	,BN259  ,BN260  ,BN261
	,BN262  ,BN263  ,BN264	
	,BN268  ,BN269  ,BN270
	,BN271  ,BN273  ,BN274
	,BN291  ,BN292  ,BN293
	,BN294  ,BN295  ,BN296
	,BN297  ,BN298  ,BN299
	,BN315  ,BN316  ,BN319
	,BN320  ,BN349  ,BN350
	,BN352  ,BN353  ,BN354
	,BN366  ,BN367  ,BN369
	,BN395  ,BN401  ,BN405
	,BN406  ,BN407  ,BN419
	,BN422  ,BN423  ,BN424
	,BN425  ,BN428  ,BN429
	,BN430  ,BN431  ,BN432
	,BN433  ,BN434  ,BN435	
	,BN439  ,BN440  ,BN441
	,BN452  ,BN453  ,BN454
	,BN455  ,BN456  ,BN463
	,BN464  ,BN469  ,BN470
	,BN471  ,BN479  ,BN481
	,BN499  ,BN502  ,BN503
	,BN504  ,BN511  ,BN513
	,BN522  ,BN523  ,BN537
	,BN540  ,BN542  ,BN595
	,BN596  ,BN801  ,BN811
	,BN812  ,BN816  ,BN817
	,BN2458 ,BN2459 ,BN2460	
	,BN874  ,BN875  ,BN877
	,BN878  ,BN879  ,BN882
	,BN902  ,BN906  ,BN1870
	,BN1914 ,BN955  ,BN956  
	,BN957  ,BN1087 ,BN1088
	,BN1089 ,BN1110 ,BN1141
	,BN1142 ,BN1144 ,BN1145
	,BN1146 ,BN1150 ,BN1151
	,BN1152 ,BN1153 ,BN1154
	,BN1155 ,BN1156 ,BN1157
	,BN1871 ,BN1180 ,BN1195
	,BN1197 ,BN1207 ,BN1208
	,BN1217 ,BN1218 ,BN1219
	,BN1220 ,BN1221 ,BN1222
	,BN1223 ,BN1229 ,BN1232
	,BN1233 ,BN1235 ,BN1236
	,BN1237 ,BN1238 ,BN1239
	,BN1261 ,BN1262 ,BN1263
	,BN1264 ,BN1265 ,BN1266
	,BN1267 ,BN1268 ,BN1269
	,BN1272 ,BN1273 ,BN1274
	,BN1277 ,BN1278 ,BN1279
	,BN1281 ,BN1282 ,BN1283
	,BN1883 ,BN1884 ,BN1885
	,BN1683	,BN1684	,BN1685
	,BN634	,BN635	,BN636		
	,BN831      	,BN832
	
	// Desglose BN2315: Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS)
	,BN1284 ,BN1287 ,BN1288
	,BN1289 ,BN1290 ,BN1291
	,BN1292 ,BN1293 ,BN1294
	,BN1295 ,BN1296 ,BN1297
	,BN1298  		,BN1304
	
	// Desglose BN1039 y BN1892: Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
	,BN1931 ,BN1932 ,BN1933 ,BN1937
	,BN1938 ,BN1939 ,BN1940 ,BN1941
	,BN1942 ,BN1943 ,BN1944 ,BN1945
	,BN1946 ,BN1947 ,BN1948 ,BN1949
	,BN2109 ,BN2110 ,BN2111 ,BN2112
	,BN2128 ,BN2129 ,BN2130 ,BN2131
	,BN2132 ,BN2133 ,BN2134 ,BN2135
	,BN2136 ,BN2137 ,BN2138 ,BN2139
	,BN2140 ,BN2141 ,BN2142 ,BN2143
	,BN2144  				,BN2147
	
	// Desglose BN2314 y BN1319: Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
	,BN2148 ,BN2149 ,BN2150 ,BN2151
	,BN2152 ,BN2153 ,BN2154 ,BN2155
	,BN2156 ,BN2157 ,BN2158 ,BN2159
	,BN2160 ,BN2161 ,BN2162 ,BN2163
	,BN2164 ,BN2165 ,BN2166 ,BN2167
	,BN2168 ,BN2169 ,BN2170 ,BN2171
	,BN2172 ,BN2173 ,BN2174 ,BN2175
	,BN1309 ,BN1310 ,BN1311 ,BN1312
	,BN1313 ,BN1314 ,BN1315 ,BN1316
	,BN1317  				,BN1322
	
// --------------- PAGINA 18 TER --------------- //
	
	// Desglose BN565 - Deducción donaciones a entidades sin fines de lucro (Ley 49/2002)
	
	 // Donaciones de carácter general
	
	,BN904	,BN905	
	,BN990	,BN991	,BN992
	,BN997	,BN998	,BN999
	,BN246	,BN247	,BN248
	,BN818 	,BN819 	,BN820
	,BN993 	,BN994 	,BN995
	,BN821 	,BN833 	,BN834
	,BN1434 ,BN1435 ,BN1436
	,BN835 	,BN836 	,BN837
	,BN1718 ,BN1719 ,BN1720
	,BN838 	,BN839 	,BN840
	,BN1950 ,BN1951 ,BN1952
	,BN842 	,BN844 	,BN845
	,BN2227 ,BN2228 ,BN2229
	,BN868 	,BN869 	,BN871
	,BN2380 ,BN2381 ,BN2382
	,BN872 	,BN873 	,BN2498
	,BN2499	,BN876 	,BN890
	,BN891 	,BN892 	,BN893
	,BN1323 ,BN1324 ,BN1325
	,BN1326 ,BN1327 ,BN1328	
	,BN1689 ,BN1690 ,BN1691
	,BN1692 ,BN1693 ,BN1694
	,BN1695 ,BN1696 ,BN1697
	,BN1698 ,BN1699 ,BN1700
	
	 // Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
	
	,BN899  ,BN901  
	,BN903  ,BN917  ,BN929
	,BN930  ,BN931  ,BN932
	,BN933  ,BN934  ,BN942
	,BN943  ,BN944  ,BN948
	,BN949  ,BN950  ,BN951
	,BN952  ,BN953  ,BN954
	,BN2472 ,BN2473 ,BN2474
	,BN958  ,BN959  ,BN963
	,BN964  ,BN965  ,BN969
	,BN970  ,BN971  ,BN972
	,BN973  ,BN975  ,BN979
	,BN980  ,BN981  ,BN982
	,BN983  ,BN984  ,BN985
	,BN1000 ,BN1001 ,BN1007
	,BN1008 ,BN1017 ,BN1024
	,BN1025 ,BN1035 ,BN1036
	,BN1061 ,BN1062 ,BN1072
	,BN1073 ,BN1074 ,BN1078
	,BN1329 ,BN1372 ,BN1373
	,BN1374 ,BN1375 ,BN1376	
	,BN1701 ,BN1702 ,BN1703
	,BN1704 ,BN1705 ,BN1706
	,BN1729 ,BN2475 ,BN2476	
	,BN1079 ,BN1080 ,BN1081
	
	 // Total deducciones a entidades sin fines de lucro (Ley 49/2002)
	
	,BN598 			,BN895
	
	 // Base de la deducción por donaciones a entidades sin fines de lucro del período impositivo

	,BN974
	
// --------------- PAGINA 19 --------------- //	
	
	// Desglose BN1040 - Deducción por reversión de medidas temporales DT 37ª.1 LIS
	,BN1166	,BN1167	,BN1437	,BN1169
	,BN1438	,BN1439	,BN1440	,BN1441
	,BN1442	,BN1443	,BN1444	,BN1445
	,BN1721	,BN1722	,BN1723	,BN1724
	,BN1953	,BN1954	,BN1955	,BN1956
	,BN2230	,BN2231	,BN2232	,BN2233
	,BN2383	,BN2384	,BN2385	,BN2386
	,BN1082	,BN1083	,BN1084	,BN1085	
	,BN1377 ,BN1378 ,BN1379 ,BN1380
	,BN1170	,BN1171	        ,BN1173
	
    // Deducción por reversión de medidas temporales DT 37ª.2 LIS
	,BN1178	,BN1179	,BN1446	,BN1181
	,BN1447	,BN1448	,BN1449	,BN1450
	,BN1451	,BN1452	,BN1453	,BN1454
	,BN1725	,BN1726	,BN1727	,BN1728
	,BN1957	,BN1958	,BN1959	,BN1960
	,BN2234	,BN2235	,BN2236	,BN2237
	,BN2387	,BN2388	,BN2389	,BN2390
	,BN1086	,BN2477	,BN2478	,BN2479
	,BN1381 ,BN1382 ,BN1383 ,BN1384
	,BN1182	,BN1183      	,BN1185
	
	// Desglose BN082 - Deducciones I + D + i excluidas de límite. Opción art. 39.2 LIS
    // Segun el fichero hay una ultima columna sin numeración (Deducción resto del grupo)
    // importe de la deducción aplicada o abonada por el resto de las sociedades del grupo (según el PADIS se utiliza para calcular los límites cuando esta marcada caracteres 39)
	,BN918	,BN919	,BN574	,BN580	,BN580R
	,BN589	,BN976	,BN977	,BN978	,BN978R
	,BN822	,BN823	,BN824	,BN231	,BN231R
	,BN232	,BN233	,BN850	,BN851	,BN851R
	,BN1123	,BN1124	,BN1125	,BN1126	,BN1126R
	,BN1127	,BN1128	,BN1129	,BN1130	,BN1130R	
	,BN1426	,BN1427	,BN1428	,BN1429	,BN1429R	
	,BN1430	,BN1431	,BN1432	,BN1433	,BN1433R
	,BN1710	,BN1711	,BN1712	,BN1713	,BN1713R
	,BN1714	,BN1715	,BN1716	,BN1717	,BN1717R
	,BN1968	,BN1969	,BN1970	,BN1971	,BN1971R
	,BN1972	,BN1973	,BN1974	,BN1975	,BN1975R
	,BN2245	,BN2246	,BN2247	,BN2248	,BN2248R
	,BN2249	,BN2250	,BN2251	,BN2252	,BN2252R
	,BN2391	,BN2392	,BN2393	,BN2394	,BN2394R 
	,BN2395	,BN2396	,BN2397	,BN2398	,BN2398R
	,BN1090 ,BN1091 ,BN1092 ,BN1093 ,BN1093R
	,BN1094 ,BN1095 ,BN1096 ,BN1097 ,BN1097R	
	,BN1385 ,BN1386 ,BN1387 ,BN1388 ,BN1388R
	,BN1389 ,BN1390 ,BN1391 ,BN1392 ,BN1392R
	,BN517	,BN081	        ,BN1234A
	
	// Detalle (totales) de las correcciones al resultado contable
	,DC2305	,DC2306
	,DC2301 ,DC2302
	,DC2303 ,DC2304 
	,DC2307 ,DC2308 
	,I0417B ,D0418B 
	,DC2309 ,DC2310
	
// --------------- PAGINA 20 --------------- //	

	// LIMITACION EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS. Art. 16 LIS
	// Límite art.16.5, 67 b) o 83 LIS
	,LM1240
	,LM1241
	,LM1242
	,LM1243
	,LM1244
	// Límite art.16.1 y 16.2 LIS
	,LM1245
	,LM1246
	,LM1247
	,LM1248
	,LM1249
	,LM1250
	,LM1251
	,LM1252
	,LM1253
	,LM1254
	,LM1255
	,LM1256
	,LM1257
	,LM1258
	,LM1259
	,LM1260
	
	// LIMITACIÓN EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS. GASTOS FINANCIEROS PENDIENTES DE DEDUCIR
			,LM1188	,LM1189			,LM1191
			,LM1193	,LM1194			,LM1196
			,LM1198	,LM1199			,LM1201
	,LM1202	,LM1203	,LM1204	,LM1205	,LM1206
	,LM1462	,LM1463	,LM1209	,LM1210	,LM1211
	,LM1736	,LM1737	,LM1464	,LM1465	,LM1466
	,LM1977	,LM1978	,LM1738	,LM1739	,LM1740	
	,LM2253	,LM2254	,LM1979	,LM1980	,LM1981
	,LM2399	,LM2400	,LM2255	,LM2256	,LM2257	
	,LM1098	,LM1099	,LM2401	,LM2402	,LM2403
	,LM1393	,LM1394	,LM1100	,LM1101	,LM1102
					,LM1395	,LM1396	,LM1397
	,LM1212	,LM1213	,LM1214	,LM1215	,LM1216
	
	// Pendiente de adición por límite beneficio operativo no aplicado	
		
	,LM1467	,LM1468	
	,LM1741	,LM1742	,LM1743
	,LM1982	,LM1983	,LM1984
	,LM2258	,LM2259	,LM2260
	,LM2404	,LM2405	,LM2406
	,LM1103	,LM1104	,LM1105
	,LM1398 ,LM1399 ,LM1400
	,LM538	,LM539	,LM546	

// --------------- PAGINA 20 BIS --------------- //
	
	// Desglose LQ1032 - Reserva de capitalizacion			
	,LQ1985	,LQ1986	
	,LQ2407	,LQ2408	,LQ2409
	,LQ1106	,LQ1107	,LQ1108
	,LQ1401 ,LQ1402 ,LQ1403
	,LQ1137			,LQ1139
	
	,LQ1140	
	
	// Desglose LQ1033, LQ1034 - Reserva de Nivelación		       
	,LQ1455	,LQ1456	,LQ1601	
	,LQ1961	,LQ1962	,LQ1602 ,LQ1963
	,LQ2238	,LQ2239	,LQ1603 ,LQ2240
	,LQ2410	,LQ2411	,LQ1604 ,LQ2412
	,LQ1109	,LQ1730	,LQ1605 ,LQ1111
	,LQ1406 ,LQ1404 ,LQ1405 ,LQ1407
	,LQ1034A 				,LQ1731
	,LQ1147  		,LQ1606 ,LQ1149	
	
	,LQ1458	,LQ1459	,LQ1460	,LQ1461
	,LQ1732	,LQ1733	,LQ1734	,LQ1735
	,LQ1964	,LQ1965	,LQ1966	,LQ1967
	,LQ2241	,LQ2242	,LQ2243	,LQ2244
	,LQ2413	,LQ2414	,LQ2415	,LQ2416
	,LQ1112	,LQ1113	,LQ1114	,LQ1115
	,LQ1872 ,LQ1410 ,LQ1411 ,LQ1412
	,LQ1158	,LQ1159	,LQ1160	,LQ1161
	
// --------------- PAGINA 20 TER --------------- //
	
	// CONVERSION DE ACTIVOS POR IMPUESTO DIFERIDO EN CREDITO EXIGIBLE FRENTE A LA ADMON. TRIBUTARIA (art. 130, DA 13ª Y DT 33ª LIS)
	
	// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS	
	,LM1524			,LM1525	,LM1526			,LM1527			,LM1528
	,LM1529	,LM1530	,LM1590	,LM1591	,LM1531	,LM1532	,LM1533	,LM1534
	,LM1535			,LM1536	,LM1537	,LM1538	,LM1539	,LM1540	,LM1541
	
	// Activos por impuesto diferido (AID). Art. 130 LIS	
	,LM1542	,LM1543	,LM1544	,LM1545	,LM1546	,LM1547	,LM1548	,LM1549	,LM1550	,LM1551
	,LM1552	,LM1553	,LM1554	,LM1555	,LM1556	,LM1753	,LM1557	,LM1558	,LM1559	,LM1560
	,LM1754	,LM1755	,LM1756	,LM1757	,LM1758	,LM1994	,LM1759	,LM1760	,LM1761	,LM1762
	,LM2100	,LM2101	,LM2102	,LM2103	,LM2104	,LM2267 ,LM2105	,LM2106	,LM2107	,LM2108	
	,LM2268	,LM2269	,LM2270	,LM2271	,LM2272 ,LM2417	,LM2273	,LM2274	,LM2275	,LM2276
	,LM2418	,LM2419	,LM2420	,LM2421	,LM2422	,LM1116	,LM2423	,LM2424	,LM2425	,LM2426
	,LM1117	,LM1118	,LM1119	,LM1120	,LM1121	,LM1413	,LM1122	,LM1131	,LM1132	,LM1133
	,LM1414 ,LM1415 ,LM1416 ,LM1417 ,LM1418 		,LM1419 ,LM1420 ,LM1421	,LM1422
	
	,LM1561, LM1562	,LM1563	,LM1564	,LM1565	,LM1566	,LM1567	,LM1568	,LM1569	,LM1570
	
	// Conversión de activos por impuesto diferido en crédito exigible frente a la Admón. tributaria
	,LM393 // Las casillas LM150 y LM506 están en la página 14 
	
	// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS)		
	,LM2277	,LM2278	,LM2279	
	,LM2427	,LM2428	,LM2429	,LM2430
	,LM1134	,LM1135	,LM1136	,LM1138
	,LM1423 ,LM1424 ,LM1425 ,LM1469
	,LM1579	,LM1580	,LM1581	,LM1582
	
// --------------- PAGINA 20 QUARTER --------------- //
	
	// APLICACION DE RESULTADOS
	// Base de Reparto
	,ID650
	,ID651
	,ID652
	,ID653
	// Aplicación
	,ID654
	,ID1270
	,ID1271
	,ID1522
	,ID655
	,ID656
	,ID658
	,ID659
	,ID660
	,ID662
	,ID664
	,ID665
	,ID666
	
	// Dotaciones por deterioro de créditos u otros activos derivados de las posibles insolvencias de los deudores no
    // vinculados con el contribuyente y otras del art. 11.12 LIS con posibilidad de conversión en crédito exigible
	,LM1473	,LM1408	,LM1474	,LM1475	,LM1476	,LM1409
	,LM1477	,LM1478	,LM1481	,LM1482	,LM1483	,LM1484
	,LM1485	,LM1486	,LM1487	,LM1488	,LM1489	,LM1490
	,LM1491	,LM1747	,LM1748	,LM1492	,LM1493	,LM1749
	,LM1750	,LM1988	,LM1989	,LM1751	,LM1752	,LM1990
	,LM1991 ,LM2261 ,LM2262 ,LM1992	,LM1993, LM2263
	,LM2264 ,LM2431 ,LM2432 ,LM2265	,LM2266, LM2433
	,LM2434 ,LM1143 ,LM1148	,LM2435	,LM2436, LM1192
	,LM1162 ,LM1470 ,LM1471 ,LM1163	,LM1164 ,LM1915
	,LM1479					,LM1480 ,LM1500
	,LM1494	,LM1495	,LM1496	,LM1497	,LM1498	,LM1499

// --------------- PAGINA 21 --------------- //
	
	// Comunicación del importe neto de la cifra de negocios
	,CN987  // Importe neto de la cifra de negocios del conjunto de las entidades del grupo
	,CN1897 // Importe neto de la cifra de negocios del conjunto de las actividades agrícolas y/o ganaderas
	,CN1901 // Otros ingresos de explotación de actividades agrícolas y/o ganaderas
	,CN988  // Importe neto cifra de negocios del conjunto de establecimientos permanentes de la misma persona física o entidad titular
	,CNEST  // Número de establecimientos permanentes a través de los que opera, en caso de persona física titular
	,CN989  // Importe neto de la cifra de negocios en el ejercicio, entidades que hayan marcado la clave de caracteres de la declaración [00003], [00004], [00024] ó [00025] 
	
	// Desglose LQ579 - Entidades navieras en regimen de tributacion en funcion del tonelaje
	,LQ0N1
	,LQ630
	,LQ631
	,LQ632
	
// --------------- PAGINA 22 --------------- //
	
	// Desglose I0403, D0404 - Régimen especial de la reserva para inversiones en Canarias (Ley 19/1994) 
	,RC097	,RC098	,RC047	,RC2438	
	,RC524	,RC525	,RC526	,RC2439	,RC527
	,RC922	,RC923	,RC924	,RC2440	,RC925
	,RC1165	,RC928	,RC938	,RC2441	,RC996
	,RC1744	,RC1168	,RC1172	,RC1174	,RC1175
			,RC1745 ,RC1746 ,RC1820 ,RC1821
	
	,RC927
	
	,RC2442					
	,RC2444					
	,RC2446					,RC2447
	,RC1176					,RC2451
	,RC1823					,RC1184
			,RC1523 ,RC130 	,RC1600
			
	// Desglose LQ553, LQ554 - Cooperativas - Determinacion de la base imponible 
	,CP0C1	,CP0E1
	,CP0C2	,CP0E2
	,CP0C3	,CP0E3
	,CP0C4	,CP0E4
			,CP0E5
	,CP0C6	,CP0E6
	,CP0C7	,CP0E7
	,CP0C8	,CP0E8
	,CP0C9	,CP0E9
	,CPC10	,CPE10
	,CPC11	,CPE11
	,CPC12	,CPE12
			
	// Desglose - LQ561 - Cooperativas - Compensación de cuotas por pérdidas de cooperativas 
	,LQ673	,LQ674	,LQ1224
	,LQ676	,LQ677	,LQ678
	,LQ679	,LQ680	,LQ681
	,LQ682	,LQ683	,LQ684
	,LQ685	,LQ686	,LQ687
	,LQ688	,LQ689	,LQ690
	,LQ691	,LQ692	,LQ693
	,LQ623	,LQ624	,LQ672
	,LQ279	,LQ280	,LQ281
	,LQ587	,LQ515	,LQ900
	,LQ059	,LQ099	,LQ100
	,LQ017	,LQ018	,LQ019
	,LQ772	,LQ773	,LQ777
	,LQ907	,LQ908	,LQ909
	,LQ910	,LQ911	,LQ912
	,LQ935	,LQ936	,LQ937
	,LQ1511	,LQ1512	,LQ1513
	,LQ1767	,LQ1768	,LQ1769
	,LQ2113	,LQ2114	,LQ2115
	,LQ2281	,LQ2282	,LQ2283
	,LQ2452	,LQ2453	,LQ2454
	,LQ1186	,LQ1187	,LQ1190
	,LQ1516 ,LQ1517 ,LQ1518
	,LQ694  		,LQ695
	,LQ1225  		,LQ1226
	
// --------------- PAGINA 23 --------------- //	
// No soportada en AON
	
// --------------- PAGINA 24 --------------- //	

	// Agrupaciones de interés económico y UTES (régimen especial)
	,UT060
	,UT500
	,UT1227
	,UT1228
	,UT552
	,UT1330
	
	,UTC01 // Base de las bonificaciones
	,UTC02 // Base total (excepto base de deducción por inversiones en elementos del inmovilizado material nuevos).
	,UTC03 // Base de deducción por inversiones en elementos del inmovilizado material nuevos
	,UT062 // Retenciones e ingresos a cuenta
	,UTC04 // Dividendos y participaciones en beneficios distribuidos con cargo a reservas - De ejercicios en los que la sociedad no haya tributado en el régimen especia
	,UTC05 // Dividendos y participaciones en beneficios distribuidos con cargo a reservas - De ejercicios en los que la sociedad haya tributado en el régimen especial
	
// --------------- PAGINA 25 --------------- //	
// No soportada en AON
	
// --------------- PAGINA 26 --------------- //	

	// TRIBUTACION CONJUNTA AL ESTADO Y A LAS ADMINISTRACIONES FORALES DEL PAIS VASCO Y NAVARRA	
	// Aplicación del concierto económico con la Comunidad Autónoma del País Vasco y del Convenio Económico entre el Estado y la Comunidad Foral de Navarra
	,TR050
	,TR051
	,TR052
	,TR053
	,TR054
	,TR055
	,TR056
	
	// Cálculo de los porcentajes de tributación a cada una de las Administraciones
	,TR626
	,TR627
	,TR628
	,TR629
	,TR625
	
	// Determinación del resultado de la autoliquidación en cada una de las Administraciones
	,TR420	,TR421	,TR426	,TR427	,TR600
	,TR402	,TR442	,TR443	,TR444	,TR602
	,TR445	,TR446	,TR447	,TR448	,TR604
	,TR449	,TR451	,TR450	,TR465	,TR606
	,TR474	,TR475	,TR476	,TR477	,TR612
	,TR482	,TR483	,TR484	,TR485	,TR616
	,TR913	,TR914	,TR915	,TR916	,TR642
	,TR486	,TR487	,TR488	,TR489	,TR618
	,TR1334	,TR1335	,TR1336	,TR1337	,TR1332
	,TR1338	,TR1339	,TR1340	,TR1341	,TR1333
	,TR1877 ,TR1878 ,TR1879 ,TR1880 ,TR1881
	,TR1624 ,TR1625 ,TR1629 ,TR1630 ,TR1587
	,TR1607 ,TR1608 ,TR1609 ,TR1610 ,TR1583
	,TR1611 ,TR1612 ,TR1613 ,TR1623 ,TR1585
	,TR494	,TR495	,TR496	,TR497	,TR622
	,TR1631 ,TR1632 ,TR1633 ,TR1634 ,TR2480
	,TR1635 ,TR1636 ,TR1637 ,TR1641 ,TR2482
	,TR1642 ,TR1643 ,TR1644 ,TR1645 ,TR2484
	,TR1646 ,TR1647 ,TR1648 ,TR1649 ,TR2486
	,TR1650 ,TR1651 ,TR1652 ,TR1653 ,TR2488
	,TR1654 ,TR1655 ,TR1656 ,TR1657 ,TR3242
	,TR1300	,TR1301	,TR1302	,TR1303	,TR1043
	,TR1305	,TR1306	,TR1307	,TR1308	,TR1044
	,TR1658 ,TR1659 ,TR1660 ,TR1661 ,TR3245
	,TR1662 ,TR1663 ,TR1664 ,TR1665 ,TR3319
	,TR1666 ,TR1667 ,TR1668 ,TR1669 ,TR2491
	,TR1670 ,TR1671 ,TR1672 ,TR1673 ,TR2494
	
	// Detalle de las correcciones al resultado contable (todas las casillas llevan 5 casillas de detalle)
	// SE PONEN EN OTRO ENUMERADO (Mod2002023KeyDC) PORQUE SI LOS AÑADIMOS AQUI TENEMOS ERROR DE CODE TOO LARGE
	
	;
	
	public String getCode() {
		
		String code = Mod2002023Code.CODE_MAP.get(this);
		
		// Si no está en CODE_MAP entonces que devuelva solo los caracteres numéricos
		// como código, es decir el código de la casilla
		if (code == null) {
			code = "";
			for(int i = 0; i < this.toString().length(); i++)
            {
               if (Character.isDigit(this.toString().charAt(i)))
                  code = code + this.toString().charAt(i);
            }						 
		}
		
		return code;		
	}
	
	public String getCode(Administration adm) {
		return getCode();		
	}
	
	public String getDescription() {
		return Mod2002023Description.DESCRIPTION_MAP.get(this);
	}
	
	public static Mod2002023Key safeValueOf(String str) {
		for (Mod2002023Key key : values()) {
			if(key.name().equalsIgnoreCase(str))
				return key;
		}
		return null;
	}
	
}

