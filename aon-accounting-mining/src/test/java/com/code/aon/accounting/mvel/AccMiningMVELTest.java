
package com.code.aon.accounting.mvel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;
import com.esferalia.aon.accounting.mining.server.AccMiningMVELContext;
import com.esferalia.aon.accounting.mining.server.IAccMiningKeyAccept;
import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.accounting.mining.shared.AccMiningParameters;
import com.esferalia.aon.accounting.mining.shared.AccMiningUtils;
import com.esferalia.aon.accounting.mining.sql.SQLAccounting;
import com.esferalia.aon.gwt.common.sql.SQLUtils;

public class AccMiningMVELTest  {
	public enum BalanceKey {
		 B101("B101","BAL"	,true	,true	,true ,"ACTIVO NO CORRIENTE (N, A, P)","B102 + B111 + B115 + B118 + B126 + B134 + B135")
		,B102("B102","BAL"	,true	,true	,true ,"Inmovilizado intangible (N, A, P)","sd({20,280,290})")
		,B103("B103","BAL"	,true	,false	,false,"Desarrollo (N)","sd({201,2801,2901})")
		,B104("B104","BAL"	,true	,false	,false,"Concesiones (N)","sd({202,2802,2902})")
		,B105("B105","BAL"	,true	,false	,false,"Patentes, licencias, marcas y similares (N)","sd({203,2803,2903})")
		,B106("B106","BAL"	,true	,true	,true ,"Fondo de comercio (N, A, P)","sd({204})")
		,B107("B107","BAL"	,true	,false	,false,"Aplicaciones informáticas (N)","sd({206,2806,2906})")
		,B108("B108","BAL"	,true	,false	,false,"Investigación (N)","sd({200,2800})")
		,B109("B109","BAL"	,true	,false	,false,"Otro inmovilizado intangible (N)","sd({205,209,2805,2905})")
		,B110("B110","BAL"	,false	,true	,true ,"Resto (A, P)",null)
		,B111("B111","BAL"	,true	,true	,true ,"Inmovilizado material (N, A, P)","B112 + B113 + B114")
		,B112("B112","BAL"	,true	,false	,false,"Terrenos y construcciones (N)","sd({210,211,2811,2910,2911})")
		,B113("B113","BAL"	,true	,false	,false,"Instalaciones técnicas y otro inmovilizado material (N)","sd({212,213,214,215,216,217,218,219,2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919})")
		,B114("B114","BAL"	,true	,false	,false,"Inmovilizado en curso y anticipos (N)","sd({23})")
		,B115("B115","BAL"	,true	,true	,true ,"Inversiones inmobiliarias (N, A, P)","sd({22,282,292})")
		,B116("B116","BAL"	,true	,false	,false,"Terrenos (N)","sd({220,2920})")
		,B117("B117","BAL"	,true	,false	,false,"Construcciones (N)","sd({221,282,2921})")
		,B118("B118","BAL"	,true	,true	,true ,"Inversiones en empresas del grupo y asociadas a largo plazo (N, A, P)","sd({2403,2404,2413,2414,2423,2424,2493,2494,293,2943,2944,2953,2954})+B122+B123+B124+B125")
		,B119("B119","BAL"	,true	,true	,true ,"Instrumentos de patrimonio (N, A, P)","sd({2403,2404,2493,2494,293})")
		,B120("B120","BAL"	,true	,false	,false,"Créditos a empresas (N)","sd({2423,2424,2953,2954})")
		,B121("B121","BAL"	,true	,false	,false,"Valores representativos de deuda (N)","sd({2413,2414,2943,2944})")
		,B122("B122","BAL"	,true	,false	,false,"Derivados (N)",null)
		,B123("B123","BAL"	,true	,false	,false,"Otros activos financieros (N)",null)
		,B124("B124","BAL"	,true	,false	,false,"Otras inversiones (N)",null)
		,B125("B125","BAL"	,false	,true	,true ,"Resto (A, P)",null)
		,B126("B126","BAL"	,true	,true	,true ,"Inversiones financieras a largo plazo (N, A, P)","sd({2405,2415,2425,250,251,252,253,254,255,257,258,26,2495,259,2935,2945,2955,296,297,298})+B132")
		,B127("B127","BAL"	,true	,true	,true ,"Instrumentos de patrimonio (N, A, P)","sd({2405,250,2495,259})")
		,B128("B128","BAL"	,true	,false	,false,"Créditos a terceros (N)","sd({2425,252,253,54,2955,298})")
		,B129("B129","BAL"	,true	,false	,false,"Valores representativos de deuda (N)","sd({2415,251,2945,297})")
		,B130("B130","BAL"	,true	,false	,false,"Derivados (N)","sd({255})")
		,B131("B131","BAL"	,true	,false	,false,"Otros activos financieros (N)","sd({258,26})")
		,B132("B132","BAL"	,true	,false	,false,"Otras inversiones (N)","sd({257})")
		,B133("B133","BAL"	,false	,true	,true ,"Resto (A, P)",null)
		,B134("B134","BAL"	,true	,true	,true ,"Activos por impuesto diferido (N, A, P)","sd({474})")
		,B135("B135","BAL"	,true	,true	,true ,"Deudores comerciales no corrientes (N, A, P)",null)
		,B136("B136","BAL"	,true	,true	,true ,"ACTIVO CORRIENTE (N, A, P)","B137 + B138 + B149 + B160 + B168 + B176 + B177")
		,B137("B137","BAL"	,true	,true	,false,"Activos no corrientes mantenidos para la venta (N, A)","sd({580,581,582,583,584,599})")
		,B138("B138","BAL"	,true	,true	,true ,"Existencias (N, A, P)","sd({30,31,32,33,34,35,36,407,39})")
		,B139("B139","BAL"	,true	,false	,false,"Comerciales (N)","sd({30,390})")
		,B140("B140","BAL"	,true	,false	,false,"Materias primas y otros aprovisionamientos (N)","sd({31,32,391,392})")
		,B141("B141","BAL"	,true	,false	,false,"Productos en curso (N)","sd({33,34,393,394})")
		,B142("B142","BAL"	,true	,false	,false,"De ciclo largo de producción (N)",null)
		,B143("B143","BAL"	,true	,false	,false,"De ciclo corto de producción (N)",null)
		,B144("B144","BAL"	,true	,false	,false,"Productos terminados (N)","sd({35,395})")
		,B145("B145","BAL"	,true	,false	,false,"De ciclo largo de producción (N)",null)
		,B146("B146","BAL"	,true	,false	,false,"De ciclo corto de producción (N)",null)
		,B147("B147","BAL"	,true	,false	,false,"Subproductos, residuos y materiales recuperados (N)","sd({36,396})")
		,B148("B148","BAL"	,true	,false	,false,"Anticipos a proveedores (N)","sd({407})")
		,B149("B149","BAL"	,true	,true	,true ,"Deudores comerciales y otras cuentas a cobrar (N, A, P)","sd({430,431,432,433,434,435,436,5580,44,460,470,471,472,544,437,490,493})")
		,B150("B150","BAL"	,true	,true	,true ,"Clientes por ventas y prestaciones de servicios (N, A, P)","sd({430,431,432,435,436,437,490,4935})")
		,B151("B151","BAL"	,true	,true	,true ,"Clientes por ventas y prestaciones de servicios a largo plazo (N, A, P)",null)
		,B152("B152","BAL"	,true	,true	,true ,"Clientes por ventas y prestaciones de servicios a corto plazo (N, A, P)",null)
		,B153("B153","BAL"	,true	,false	,false,"Clientes empresas del grupo y asociadas (N)","sd({433,434,4933,4934})")
		,B154("B154","BAL"	,true	,false	,false,"Deudores varios (N)","sd({44})")
		,B155("B155","BAL"	,true	,false	,false,"Personal (N)","sd({460,544})")
		,B156("B156","BAL"	,true	,false	,false,"Activos por impuesto corriente (N)","sd({4709})")
		,B157("B157","BAL"	,true	,false	,false,"Otros créditos con las Administraciones públicas (N)","sd({4700,4708,471,472})")
		,B158("B158","BAL"	,true	,true	,true ,"Accionistas (socios) por desembolsos exigidos (N, A, P)","sd({5580})")
		,B159("B159","BAL"	,false	,true	,true ,"Otros deudores (A, P)","sd({44,460,470,471,472,544})")
		,B160("B160","BAL"	,true	,true	,true ,"Inversiones en empresas del grupo y asociadas a corto plazo (N, A, P)","sd({5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354,5523,5524,5393,5394,593,5943,5944,5953,5954})+B164+B166")
		,B161("B161","BAL"	,true	,true	,true ,"Instrumentos de patrimonio (N, A, P)","sd({5303,5304,5393,5394,593})")
		,B162("B162","BAL"	,true	,false	,false,"Créditos a empresas (N)","sd({5323,5324,5343,5344,5953,5954})")
		,B163("B163","BAL"	,true	,false	,false,"Valores representativos de deuda (N)","sd({5313,5314,5333,5334,5943,5944})")
		,B164("B164","BAL"	,true	,false	,false,"Derivados (N)",null)
		,B165("B165","BAL"	,true	,false	,false,"Otros activos financieros (N)","sd({5353,5354,5523,5524})")
		,B166("B166","BAL"	,true	,false	,false,"Otras inversiones (N)",null)
		,B167("B167","BAL"	,false	,true	,true ,"Resto (A, P)",null)
		,B168("B168","BAL"	,true	,true	,true ,"Inversiones financieras a corto plazo (N, A, P)","sd({5305,5315,5325,5335,5345,5355,540,541,542,543,545,546,547,548,5525,5590,5593,565,566,5395,549,5935,5945,5955,596,597,598})+sdPositivo({551})+B174")
		,B169("B169","BAL"	,true	,true	,true ,"Instrumentos de patrimonio (N, A, P)","sd({5305,540,5395,549})")
		,B170("B170","BAL"	,true	,false	,false,"Créditos a empresas (N)","sd({5325,5345,542,543,547,5955,598})")
		,B171("B171","BAL"	,true	,false	,false,"Valores representativos de deuda (N)","sd({5315,5335,541,546,5945,597})")
		,B172("B172","BAL"	,true	,false	,false,"Derivados (N)","sd({5590,5593})")
		,B173("B173","BAL"	,true	,false	,false,"Otros activos financieros (N)","sd({5355,545,548,5525,565,566})+sdPositivo({551})")
		,B174("B174","BAL"	,true	,false	,false,"Otras inversiones (N)",null)
		,B175("B175","BAL"	,false	,true	,true ,"Resto (A, P)",null)
		,B176("B176","BAL"	,true	,true	,true ,"Periodificaciones a corto plazo (N, A, P)","sd({480,567})")
		,B177("B177","BAL"	,true	,true	,true ,"Efectivo y otros activos líquidos equivalentes (N, A, P)","sd({57})")
		,B178("B178","BAL"	,true	,false	,false,"Tesorería (N)","sd({570,571,572,573,574,575})")
		,B179("B179","BAL"	,true	,false	,false,"Otros activos líquidos equivalentes (N)","sd({576})")
		,B180("B180","BAL"	,true	,true	,true ,"TOTAL ACTIVO (N, A, P)","B101 + B136")
		,B185("B185","BAL"	,true	,true	,true ,"PATRIMONIO NETO (N, A, P)","B186 + B202 + B208 + B209")
		,B186("B186","BAL"	,true	,true	,true ,"Fondos propios (N, A, P)","B187 + B190 + B191 + B194 + B195 + B198 + B199 + B200 + B201")
		,B187("B187","BAL"	,true	,true	,true ,"Capital (N, A, P)","B188 + B189")
		,B188("B188","BAL"	,true	,true	,true ,"Capital escriturado (N, A, P)","sa({100,101,102})")
		,B189("B189","BAL"	,true	,true	,true ,"(Capital no exigido) (N, A, P)","sd({1030,1040})")
		,B190("B190","BAL"	,true	,true	,true ,"Prima de emisión (N, A, P)","sa({110})")
		,B191("B191","BAL"	,true	,true	,true ,"Reservas (N, A, P)","sa({112,113,114,115,119})")
		,B192("B192","BAL"	,true	,false	,false,"Legal y estatutarias (N)","sa({112,1141})")
		,B193("B193","BAL"	,true	,false	,false,"Otras reservas (N)","sa({113,1140,1142,1143,1144,115,119})")
		,B194("B194","BAL"	,true	,true	,true ,"(Acciones y participaciones en patrimonio propias) (N, A, P)","sd({108,109})")
		,B195("B195","BAL"	,true	,true	,true ,"Resultados de ejercicios anteriores (N, A, P)","sa({120})-sd({121})")
		,B196("B196","BAL"	,true	,false	,false,"Remanente (N)","sa({120})")
		,B197("B197","BAL"	,true	,false	,false,"(Resultados negativos de ejercicios anteriores) (N)","sd({121})")
		,B198("B198","BAL"	,true	,true	,true ,"Otras aportaciones de socios (N, A, P)","sa({118})")
		,B199("B199","BAL"	,true	,true	,true ,"Resultado del ejercicio (N, A, P)","sa({129})")
		,B200("B200","BAL"	,true	,true	,true ,"(Dividendo a cuenta) (N, A, P)","sd({557})")
		,B201("B201","BAL"	,true	,true	,false,"Otros instrumentos de patrimonio neto (N, A)","sa({111})")
		,B202("B202","BAL"	,true	,true	,false,"Ajustes por cambio de valor (N, A)","sa({133,1340,135,136,137})")
		,B203("B203","BAL"	,true	,false	,false,"Activos financieros disponibles para la venta (N)","sa({133})")
		,B204("B204","BAL"	,true	,false	,false,"Operaciones de cobertura (N)","sa({1340})")
		,B205("B205","BAL"	,true	,false	,false,"Activos no corrientes y pasivos vinculados, mantenidos para la venta (N)","sa({136})")
		,B206("B206","BAL"	,true	,false	,false,"Diferencia de conversión (N)","sa({135})")
		,B207("B207","BAL"	,true	,false	,false,"Otros (N)","sa({137})")
		,B208("B208","BAL"	,false	,false	,true ,"Ajustes en patrimonio neto (P)",null)
		,B209("B209","BAL"	,true	,true	,true ,"Subvenciones, donaciones y legados recibidos (N, A, P)","sa({130,131,132})")
		,B210("B210","BAL"	,true	,true	,true ,"PASIVO NO CORRIENTE (N, A, P)","B211 + B216 + B223 + B224 + B225 + B226 + B227")
		,B211("B211","BAL"	,true	,true	,true ,"Provisiones a largo plazo (N, A, P)","sa({14})")
		,B212("B212","BAL"	,true	,false	,false,"Obligaciones por prestaciones a largo plazo al personal (N)","sa({140})")
		,B213("B213","BAL"	,true	,false	,false,"Actuaciones medioambientales (N)","sa({145})")
		,B214("B214","BAL"	,true	,false	,false,"Provisiones por reestructuración (N)","sa({146})")
		,B215("B215","BAL"	,true	,false	,false,"Otras provisiones (N)","sa({141,142,143,147})")
		,B216("B216","BAL"	,true	,true	,true ,"Deudas a largo plazo (N, A, P)","sa({1605,1615,1625,1635,17,180,185,189})")
		,B217("B217","BAL"	,true	,false	,false,"Obligaciones y otros valores negociables (N)","sa({177,178,179})")
		,B218("B218","BAL"	,true	,true	,true ,"Deudas con entidades de crédito (N, A, P)","sa({1605,170})")
		,B219("B219","BAL"	,true	,true	,true ,"Acreedores por arrendamiento financiero (N, A, P)","sa({1625,174})")
		,B220("B220","BAL"	,true	,false	,false,"Derivados (N)","sa({176})")
		,B221("B221","BAL"	,true	,false	,false,"Otros pasivos financieros (N)","sa({1615,1635,171,172,173,175,180,185,189})")
		,B222("B222","BAL"	,false	,true	,true ,"Otras deudas a largo plazo (A, P)","sa({1615,1635,171,172,173,175,176,177,178,179,180,185,189})")
		,B223("B223","BAL"	,true	,true	,true ,"Deudas con empresas del grupo y asociadas a largo plazo (N, A, P)","sa({1603,1604,1613,1614,1623,1624,1633,1634})")
		,B224("B224","BAL"	,true	,true	,true ,"Pasivos por impuesto diferido (N, A, P)","sa({479})")
		,B225("B225","BAL"	,true	,true	,true ,"Periodificaciones a largo plazo (N, A, P)","sa({181})")
		,B226("B226","BAL"	,true	,true	,true ,"Acreedores comerciales no corrientes (N, A, P)",null)
		,B227("B227","BAL"	,true	,true	,true ,"Deuda con características especiales a largo plazo (N, A, P)","sa({15})")
		,B228("B228","BAL"	,true	,true	,true ,"PASIVO CORRIENTE (N, A, P)","B229 + B230 + B231 + B238 + B239 + B250 + B251")
		,B229("B229","BAL"	,true	,true	,false,"Pasivos vinculados con activos no corr. mantenidos para la venta(N, A)","sa({585,586,587,588,589})")
		,B230("B230","BAL"	,true	,true	,true ,"Provisiones a corto plazo (N, A, P)","sa({499,529})")
		,B231("B231","BAL"	,true	,true	,true ,"Deudas a corto plazo (N, A, P)","sa({5105,520,527,5125,524,194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,5525,555,5565,5566,5595,5598,560,561,569})+saPositivo({551})-sd({1034,1044,190,192})")
		,B232("B232","BAL"	,true	,false	,false,"Obligaciones y otros valores negociables (N)","sa({500,501,505,506})")
		,B233("B233","BAL"	,true	,true	,true ,"Deudas con entidades de crédito (N, A, P)","sa({5105,520,527})")
		,B234("B234","BAL"	,true	,true	,true ,"Acreedores por arrendamiento financiero (N, A, P)","sa({5125,524})")
		,B235("B235","BAL"	,true	,false	,false,"Derivados (N)","sa({5595,5598})")
		,B236("B236","BAL"	,true	,false	,false,"Otros pasivos financieros (N)","sa({194,509,5115,5135,5145,521,522,523,525,526,528,5525,555,5565,5566,560,561,569})+saPositivo({551})-sd({1034,1044,190,192})")
		,B237("B237","BAL"	,false	,true	,true ,"Otras deudas a corto plazo (A, P)","sa({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,525,555,5565,5566,5595,5598,560,561,569})+saPositivo({551})-sd({1034,1044,190,192})")
		,B238("B238","BAL"	,true	,true	,true ,"Deudas con empresas del grupo y asociadas a corto plazo (N, A, P)","sa({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564})")
		,B239("B239","BAL"	,true	,true	,true ,"Acreedores comerciales y otras cuentas a pagar (N, A, P)","sa({400,401,403,404,405,41,438,465,466,475,476,477})-sd({406})")
		,B240("B240","BAL"	,true	,true	,true ,"Proveedores (N, A, P)","sa({400,401,405})-sd({406})")
		,B241("B241","BAL"	,true	,true	,true ,"Proveedores a largo plazo (N, A, P)",null)
		,B242("B242","BAL"	,true	,true	,true ,"Proveedores a corto plazo (N, A, P)",null)
		,B243("B243","BAL"	,true	,false	,false,"Proveedores, empresas del grupo y asociadas (N)","sa({403,404})")
		,B244("B244","BAL"	,true	,false	,false,"Acreedores varios (N)","sa({41})")
		,B245("B245","BAL"	,true	,false	,false,"Personal (remuneraciones pendientes de pago) (N)","sa({465,466})")
		,B246("B246","BAL"	,true	,false	,false,"Pasivos por impuesto corriente (N)","sa({4752})")
		,B247("B247","BAL"	,true	,false	,false,"Otras deudas con las Administraciones públicas (N)","sa({4750,4751,4758,476,477})")
		,B248("B248","BAL"	,true	,false	,false,"Anticipos de clientes (N)","sa({438})")
		,B249("B249","BAL"	,false	,true	,true ,"Otros acreedores (A, P)","sa({41,438,465,466,475,476,477})")
		,B250("B250","BAL"	,true	,true	,true ,"Periodificaciones a corto plazo (N, A, P)","sa({485,568})")
		,B251("B251","BAL"	,true	,true	,true ,"Deuda con características especiales a corto plazo (N, A, P)","sa({502,507})")
		,B252("B252","BAL"	,true	,true	,true ,"TOTAL PATRIMONIO NETO Y PASIVO (N, A, P)","B185 + B210 + B228")
		
		
		,P255("P255","PYG"	,true	,true	,true,"Importe neto de la cifra de negocios (N, A, P)","sa({700,701,702,703,704,705,706,708,709})")
		,P256("P256","PYG"	,true	,false	,false,"Ventas (N)","sa({700,701,702,703,704,706,708,709})")
		,P257("P257","PYG"	,true	,false	,false,"Prestaciones de servicios (N)","sa({705})")
		,P258("P258","PYG"	,true	,true	,true,"Variación de existencias de productos terminados y en curso de fabricación (N, A, P)","sa({71,7930,6930})")
		,P259("P259","PYG"	,true	,true	,true,"Trabajos realizados por la empresa para su activo (N, A, P)","sa({73})")
		,P260("P260","PYG"	,true	,true	,true,"Aprovisionamientos (N, A, P)","sa({606,608,609,61,7931,7932,7933,600,601,602,607,6931,6932,6933})")
		,P261("P261","PYG"	,true	,false	,false,"Consumo de mercaderías (N)","sa({6060,6080,6090,610,600})")
		,P262("P262","PYG"	,true	,false	,false,"Consumo de materias primas y otras materias consumibles (N)","sa({6061,6062,6081,6082,6091,6092,611,612,601,602})")
		,P263("P263","PYG"	,true	,false	,false,"Trabajos realizados por otras empresas (N)","sa({607})")
		,P264("P264","PYG"	,true	,false	,false,"Deterioro de mercaderías, materias primas y otros aprovisionamientos (N)","sa({7931,7932,7933,6931,6932,6933})")
		,P265("P265","PYG"	,true	,true	,true,"Otros ingresos de explotación (N, A, P)","sa({740,747,75})")
		,P266("P266","PYG"	,true	,true	,true,"Ingresos accesorios y otros de gestión corriente (N, A, P)","sa({75})")
		,P267("P267","PYG"	,true	,true	,true,"Ingresos por arrendamientos (N, A, P)","sa({752})")
		,P268("P268","PYG"	,true	,true	,true,"Resto (N, A, P)",null)
		,P269("P269","PYG"	,true	,true	,true,"Subvenciones de explotación incorporadas al resultado del ejercicio (N, A, P)","sa({740,747})")
		,P270("P270","PYG"	,true	,true	,true,"Gastos de personal (N, A, P)","sa({7950,7957,640,641,6450,642,643,649,644,6457})")
		,P271("P271","PYG"	,true	,true	,true,"Sueldos y salarios (N, A, P)","sa({640})")
		,P273("P273","PYG"	,true	,true	,true,"Indemnizaciones (N, A, P)","sa({641})")
		,P274("P274","PYG"	,true	,true	,true,"Seguridad Social a cargo de la empresa (N, A, P)","sa({642})")
		,P275("P275","PYG"	,true	,true	,true,"Retribuciones a largo plazo mediante sistemas de aportaciones o prestación definida (N, A, P)","sa({643})")
		,P276("P276","PYG"	,true	,true	,true,"Retribuciones mediante instrumentos de patrimonio (N, A, P)","sa({6450})")
		,P277("P277","PYG"	,true	,true	,true,"Otros gastos sociales (N, A, P)","sa({649})")
		,P278("P278","PYG"	,true	,true	,false,"Provisiones (N, A)","sa({7950,7957,644,6457})")
		,P279("P279","PYG"	,true	,true	,true,"Otros gastos de explotación (N, A, P)","sa({636,639,794,7954,62,631,634,65,694,695})")
		,P280("P280","PYG"	,true	,false	,false,"Servicios exteriores (N)","sa({62})")
		,P281("P281","PYG"	,true	,false	,false,"Tributos (N)","sa({636,639,631,634})")
		,P282("P282","PYG"	,true	,false	,false,"Pérdidas, deterioro y variación de provisiones por operaciones comerciales (N)","sa({794,7954,650,694,695})")
		,P283("P283","PYG"	,true	,false	,false,"Otros gastos de gestión corriente (N)","sa({651,659})")
		,P284("P284","PYG"	,true	,true	,true,"Amortización del inmovilizado (N, A, P)","sa({68})")
		,P285("P285","PYG"	,true	,true	,true,"Imputación de subvenciones de inmovilizado no financiero y otras (N, A, P)","sa({746})")
		,P286("P286","PYG"	,true	,true	,true,"Excesos de provisiones (N, A, P)","sa({7951,7952,7955,7956})")
		,P287("P287","PYG"	,true	,true	,true,"Deterioro y resultado por enajenaciones del inmovilizado (N, A, P)","sa({770,771,772,790,791,792,670,671,672,690,691,692})")
		,P288("P288","PYG"	,true	,true	,true,"Deterioro y pérdidas (N, A, P)","sa({790,791,792,690,691,692})")
		,P289("P289","PYG"	,true	,true	,true,"Deterioros (N, A, P)","sa({690,691,692})")
		,P290("P290","PYG"	,true	,true	,true,"Reversión de deterioros (N, A, P)","sa({790,791,792})")
		,P291("P291","PYG"	,true	,true	,true,"Resultados por enajenaciones y otras (N, A, P)","sa({770,771,772,670,671,672})")
		,P292("P292","PYG"	,true	,true	,true,"Beneficios (N, A, P)","sa({770,771,772})")
		,P293("P293","PYG"	,true	,true	,true,"Pérdidas (N, A, P)","sa({670,671,672})")
		,P294("P294","PYG"	,true	,true	,false,"Diferencia negativa de combinaciones de negocio (N, A)","sa({774})")
		,P295("P295","PYG"	,true	,true	,true,"Otros resultados (N, A, P)","sa({778,678})")
		,P296("P296","PYG"	,true	,true	,true,"RESULTADO DE EXPLOTACIÓN (N, A, P)","P255 + P258 + P259 + P260 + P265 + P270 + P279 + P284 + P285 + P286 + P287 + P294 + P295")
		,P297("P297","PYG"	,true	,true	,true,"Ingresos financieros (N, A, P)","sa({746,760,761,762,767,769})")
		,P298("P298","PYG"	,true	,true	,true,"De participaciones en instrumentos de patrimonio (N, A, P)","P299 + P300")
		,P299("P299","PYG"	,true	,true	,true,"En empresas del grupo y asociadas (N, A, P)","sa({7600,7601})")
		,P300("P300","PYG"	,true	,true	,true,"En terceros (N, A, P)","sa({7602,7603})")
		,P301("P301","PYG"	,true	,true	,true,"De valores negociables y otros instrumentos financieros (N, A, P)","P302 + P303")
		,P302("P302","PYG"	,true	,true	,true,"De empresas del grupo y asociadas (N, A, P)","sa({7610,7611,76200,76201,76210,76211})")
		,P303("P303","PYG"	,true	,true	,true,"De terceros (N, A, P)","sa({7612,7613,76202,76203,76212,76213,767,769})")
		,P304("P304","PYG"	,true	,true	,true,"Imputación de subvenciones, donaciones y legados de carácter financiero (N, A, P)","sa({746})")
		,P305("P305","PYG"	,true	,true	,true,"Gastos financieros (N, A, P)","sa({660,661,662,664,665,669})")
		,P306("P306","PYG"	,true	,true	,true,"Por deudas con empresas del grupo y asociadas (N, A, P)","sa({6610,6611,6615,6616,6620,6621,6640,6641,6650,6651,6654,6655})")
		,P307("P307","PYG"	,true	,true	,true,"Por deudas con terceros (N, A, P)","sa({6612,6613,6617,6618,6622,6623,6624,6642,6643,6652,6653,6656,6657,669})")
		,P308("P308","PYG"	,true	,true	,true,"Por actualización de provisiones (N, A, P)","sa({660})")
		,P309("P309","PYG"	,true	,true	,true,"Variación del valor razonable en instrumentos financieros (N, A, P)","sa({763,663})")
		,P310("P310","PYG"	,true	,false	,false,"Cartera de negociación y otros (N)","sa({7630,7631,7633,6630,6631,6633})")
		,P311("P311","PYG"	,true	,false	,false,"Imputación al resultado del ejercicio por activos financieros disponibles para la venta (N)","sa({7632,6632})")
		,P312("P312","PYG"	,true	,true	,true,"Diferencias de cambio (N, A, P)","sa({768,668})")
		,P313("P313","PYG"	,true	,true	,true,"Deterioro y resultado por enajenación de instrumentos financieros (N, A, P)","sa({766,773,775,796,797,798,799,666,667,673,675,696,697,698,699})")
		,P314("P314","PYG"	,true	,true	,true,"Deterioros y pérdidas (N, A, P)","sa({796,797,798,799,696,697,698,699})")
		,P315("P315","PYG"	,true	,true	,true,"Deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)",null)
		,P316("P316","PYG"	,true	,true	,true,"Deterioros, Otras empresas (N, A, P)",null)
		,P317("P317","PYG"	,true	,true	,true,"Reversión de deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)",null)
		,P318("P318","PYG"	,true	,true	,true,"Reversión de deterioros, otras empresas (N, A, P)",null)
		,P319("P319","PYG"	,true	,true	,true,"Resultados por enajenación y otras (N, A, P)","sa({766,773,775,666,667,673,675})")
		,P320("P320","PYG"	,true	,true	,true,"Beneficios, empresas del grupo y asociadas a largo plazo (N, A, P)",null)
		,P321("P321","PYG"	,true	,true	,true,"Beneficios, otras empresas (N, A, P)",null)
		,P322("P322","PYG"	,true	,true	,true,"Pérdidas, empresas del grupo y asociadas a largo plazo (N, A, P)",null)
		,P323("P323","PYG"	,true	,true	,true,"Pérdidas, otras empresas (N, A, P)",null)
		,P329("P329","PYG"	,true	,true	,true,"Otros ingresos y gastos de carácter financiero (N, A, P)","P330 + P331 + P332")
		,P330("P330","PYG"	,true	,true	,true,"Incorporación al activo de gastos financieros (N, A, P)",null)
		,P331("P331","PYG"	,true	,true	,true,"Ingresos financieros derivados de convenios de acreedores (N, A, P)",null)
		,P332("P332","PYG"	,true	,true	,true,"Resto de ingresos y gastos (N, A, P)",null)
		,P324("P324","PYG"	,true	,true	,true,"RESULTADO FINANCIERO (N, A, P)","P297 + P305 + P309 + P312 + P313 + P329")
		,P325("P325","PYG"	,true	,true	,true,"RESULTADO ANTES DE IMPUESTOS (N, A, P)","P296 + P324")
		,P326("P326","PYG"	,true	,true	,true,"Impuestos sobre beneficios (N, A, P)","sa({6301,638,6300,633})")
		,P327("P327","PYG"	,true	,true	,true,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS (N, A, P) (2)","P325 + P326")
		,P328("P328","PYG"	,true	,false	,false,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS (N)",null)
		,P500("P500","PYG"	,true	,true	,true,"RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS (N, A, P)","P327 + P328")
		
		,T500("T500","PAT"	,true	,true	,false,"RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS (N, A)","P500")
		,T336("T336","PAT"	,true	,true	,false,"Por valoración de instrumentos financieros (N, A)","sa({900,991,992})+sd({800,89})+T338")
		,T337("T337","PAT"	,true	,false	,false,"Activos financieros disponibles para la venta (N)","sa({900,991,992})+sd({800,89})")
		,T338("T338","PAT"	,true	,false	,false,"Otros ingresos/gastos (N)",null)
		,T339("T339","PAT"	,true	,true	,false,"Por coberturas de flujos de efectivo (N, A)","sa({910})+sd({810})")
		,T340("T340","PAT"	,true	,true	,false,"Subvenciones, donaciones y legados recibidos (N, A)","sa({94})")
		,T341("T341","PAT"	,true	,true	,false,"Por ganancias y pérdidas actuariales y otros ajustes (N, A)","sa({95})+sd({85})")
		,T342("T342","PAT"	,true	,true	,false,"Por activos no corrientes y pasivos vinculados, mantenidos pra la venta (N, A)","sa({900})+sd({860})")
		,T343("T343","PAT"	,true	,true	,false,"Diferencias de conversión (N, A)","sa({920})+sd({820})")
		,T344("T344","PAT"	,true	,true	,false,"Efecto impositivo (N, A)","sa({8301,834,835,838})+sd({8300,833})")
		,T345("T345","PAT"	,true	,true	,false,"Total ingresos y gastos imputados directamente en el patrimonio neto (N, A)","T336 + T339 + T340 + T341 + T342 + T343 + T344")
		,T346("T346","PAT"	,true	,true	,false,"Por valoración de instrumentos financieros (N, A)","sa({902,993,994})+sd({802})+T348")
		,T347("T347","PAT"	,true	,false	,false,"Activos financieros disponibles para la venta (N)","sa({902,993,994})+sd({802})")
		,T348("T348","PAT"	,true	,false	,false,"Otros ingresos/gastos (N)",null)
		,T349("T349","PAT"	,true	,true	,false,"Por coberturas de flujo de efectivo (N, A)","sa({912})+sd({812})")
		,T350("T350","PAT"	,true	,true	,false,"Subvenciones, donaciones y legados recibidos (N, A)","sd({84})")
		,T351("T351","PAT"	,true	,true	,false,"Por activos no corrientes y pasivos vinculados, mantenidos para la venta (N, A)","sa({902})+sd({862})")
		,T352("T352","PAT"	,true	,true	,false,"Diferencias de conversión (N, A)","sa({921})+sd({821})")
		,T353("T353","PAT"	,true	,true	,false,"Efecto impositivo (N, A)","sa({8301})+sd({836,837})")
		,T354("T354","PAT"	,true	,true	,false,"Total transferencias a la cuenta de pérdidas y ganancias (N, A)","T346 + T349 + T350 + T351 + T352 + T353")
		,T355("T355","PAT"	,true	,true	,false,"TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (N, A)","P500 + T345 + T354")
		;
		
		private String code;
		private String type;
		private boolean normal;
		private boolean abbreviated;
		private boolean pymes;
		private String description;
		private String expression;
		
		private BalanceKey(String code,String type,boolean normal,boolean abbreviated,boolean pymes,String description,String expression) {
			this.code = code;
			this.type = type;
			this.normal=normal;
			this.abbreviated=abbreviated;
			this.pymes=pymes;
			this.description=description;
			this.expression = expression;
		}

		public String getCode() {
			return code;
		}
		public String getType() {
			return type;
		}
		public boolean isNormal() {
			return normal;
		}
		public boolean isAbbreviated() {
			return abbreviated;
		}

		public boolean isPymes() {
			return pymes;
		}

		public String getDescription() {
			return description;
		}

		public void setCode(String code) {
			this.code = code;
		}
		public String getComputeExpression() {
			return expression;
		}

		public Object getDefaultValue() {
			return new Double(0);
		}

		public String getActiveExpression() {
			return null;
		}

		public String getInitialExpression() {
			return null;
		}

	}
	
	public static void main1(String[] args) throws IOException {
		InputStreamReader r = new InputStreamReader(new FileInputStream("/home/ecastellano/aeat/SOCIEDADES/patrimonio1.txt"),"UTF-8");
		LineNumberReader reader = new LineNumberReader(r);
		while (reader.ready()) {
			String line = reader.readLine();
			if (StringUtils.isNotEmpty(line)){
				String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "|");
				String description = tokens[0].trim();
				String key = "T" + StringUtils.strip(tokens[1].trim(), "[]");
				String expression = tokens[2].trim();
				boolean normal = false;
				boolean abbreviated = false;
				boolean pymes = false;
				if ( StringUtils.contains(description, "(N, A, P)") ) {
					normal = true;
					abbreviated = true;
					pymes = true;
				} else if ( StringUtils.contains(description, "(N, A)") ) {
					normal = true;
					abbreviated = true;
				} else if ( StringUtils.contains(description, "(A, P)") ) {
					abbreviated = true;
					pymes = true;
				} else if (StringUtils.contains(description, "(N)") ){
					normal = true;	
				} else if (StringUtils.contains(description, "(P)") ) {
					pymes = true;	
				}
				if (expression.contains("+")) {
					expression = expression.replace("[", "T");
					expression = expression.replace("]", "");
				} else {
					List<String> sd = new LinkedList<String>();
					List<String> sa = new LinkedList<String>();
					List<String> in = new LinkedList<String>();
					String[] tok = StringUtils.splitByWholeSeparatorPreserveAllTokens(expression, ",");
					for (String t : tok ) {
						String a = StringUtils.trim(t);
						if (a.startsWith("(")) {
							a= a.replace("(", "");
							a= a.replace(")", "");
							sd.add(a);
						} else if (a.startsWith("[")) {
							a= a.replace("[", "");
							a= a.replace("]", "");
							in.add(a);
						} else {
							sa.add(a);
						}
					}
					String debitBalance = null;
					String creditBalance = null;
					String innerBalance = null;
					
					boolean first = true;
					for (String a : sa) {
						if (first) {
							first = false;
							debitBalance = "sa({";
						} else {
							debitBalance = debitBalance + ",";	
						}
						debitBalance = debitBalance + a;
					}
					if (debitBalance != null) {
						debitBalance = debitBalance + "})";
					}
					
					first = true;
					for (String a : sd) {
						if (first) {
							first = false;
							creditBalance = (debitBalance==null?"":"+") + "sd({";
						} else {
							creditBalance = creditBalance + ",";	
						}
						creditBalance = creditBalance + a;
					}
					if (creditBalance != null) {
						creditBalance = creditBalance + "})";
					}

					first = true;
					for (String a : in) {
						if (first) {
							first = false;
							innerBalance = (debitBalance==null && creditBalance==null?"":"+");
						} else {
							innerBalance = innerBalance + "+";	
						}
						innerBalance = innerBalance + "T" + a;
					}

					expression = 
							(debitBalance==null?"":debitBalance) 
							+ (creditBalance==null?"":creditBalance)
							+ (innerBalance==null?"":innerBalance);
				}
				
				
				System.out.println("," +  key + "("
						+ "\"" + key + "\""
						+ ",\"PAT\""
						+ "\t," + normal 
						+ "\t," + abbreviated
						+ "\t," + pymes
						+ ",\"" + description + "\"" 
						+ (StringUtils.isBlank(expression)?",null":",\"" + expression + "\"")
						+ ")"
						);	
				
			}
		}
		reader.close();
		
	}

	
	public static void main(String[] args) throws ParseException, AonConnectionException, AccMiningException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		AccMiningParameters params = new AccMiningParameters();
		String domainName = "mac.ecastellano.dev";
		params.setDomain(526);
		params.setYear(2013);
		params.setStartDate( sdf.parse("01/01/2013"));
		params.setEndDate( sdf.parse("31/12/2013"));
		System.out.println("[START]");
		System.out.println("Year: " + params.getYear() );
		System.out.println("From: " + params.getStartDate());
		System.out.println("To: " + params.getEndDate() );
		Date now = new Date();
		Connection c = null;
		DecimalFormat nf = new DecimalFormat("#,##0.0#;(#,##0.0#)"); 
		try {
			c = AonDataSource.getInstance().getConnection(domainName);
			
			AccMiningMVELContext ctx = new AccMiningMVELContext( new IAccMiningKeyAccept() {
				
				@Override
				public boolean acceptKey(Object key) {
					return true;
				}
			});
			ctx.setAccounts( SQLAccounting.getAccountBalances(c, params) );
			//ctx.setKeys( BalanceKey.values() );
			for (BalanceKey bk : BalanceKey.values() ) {
				if (bk.isNormal() && "BAL".equals(bk.getType())) {
					Double d = (Double) ctx.get( bk.getCode() );
					System.out.println( bk.getCode() 
							+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
							+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
				}
			}
		} finally {
			SQLUtils.closeQuietly(c);
		}
		
		System.out.println("[END] " + ((new Date()).getTime() - now.getTime()) + "Ms.");
	}
	

}

/*			


System.out.println(" **************************************** "   );
System.out.println(" ***** BALANCE DE SITUACION (NORMAL) **** "   );
System.out.println(" **************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isNormal() && "BAL".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();


System.out.println(" ******************************************* "   );
System.out.println(" ***** BALANCE DE SITUACION (ABREVIADO) **** "   );
System.out.println(" ******************************************* "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isAbbreviated()  && "BAL".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();


System.out.println(" *************************************** "   );
System.out.println(" ***** BALANCE DE SITUACION (PYMES) **** "   );
System.out.println(" *************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isPymes() && "BAL".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();

System.out.println(" *************************************************** "   );
System.out.println(" ***** BALANCE DE PÉRDIDAS Y GANANCIAS (NORMAL) **** "   );
System.out.println(" *************************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isNormal() && "PYG".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();


System.out.println(" ****************************************************** "   );
System.out.println(" ***** BALANCE DE PÉRDIDAS Y GANANCIAS (ABREVIADO) **** "   );
System.out.println(" ****************************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isAbbreviated()  && "PYG".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();


System.out.println(" ************************************************** "   );
System.out.println(" ***** BALANCE DE PÉRDIDAS Y GANANCIAS (PYMES) **** "   );
System.out.println(" ************************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isPymes() && "PYG".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();

System.out.println(" *********************************************************** "   );
System.out.println(" ***** ESTADO DE CAMBIOS EN EL PATRIMONIO NETO (NORMAL) **** "   );
System.out.println(" *********************************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isNormal() && "PAT".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();


System.out.println(" *************************************************************** "   );
System.out.println(" ***** ESTADO DE CAMBIOS EN EL PATRIMONIO NETO (ABREEVIADO) **** "   );
System.out.println(" *************************************************************** "   );
for (BalanceKey bk : BalanceKey.values() ) {
	if (bk.isAbbreviated()  && "PAT".equals(bk.getType())) {
		Double d = (Double) ctx.get( bk.getCode());
		System.out.println( bk.getCode() 
				+ "\t" + StringUtils.leftPad(StringUtils.abbreviate(bk.getDescription(), 60), 61)
				+ "\t" + StringUtils.leftPad(nf.format(AccMiningUtils.round( d )),20) );
	}
}
System.out.println();
System.out.println();
System.out.println();
*/
