package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;

public enum Mod2002019CorrectionKey implements Serializable, IMod200KeysProvider  {

	 C01(Mod2002019Key.I0355,Mod2002019Key.D0356, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2501, Mod2002019KeyDC.DC2502, Mod2002019KeyDC.DC2503, Mod2002019KeyDC.DC2504, Mod2002019KeyDC.DC2505}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2506, Mod2002019KeyDC.DC2507, Mod2002019KeyDC.DC2508, Mod2002019KeyDC.DC2509, Mod2002019KeyDC.DC2510}, "Cambio de criterios contables (art. 11.3.2o LIS)") 
	,C02(Mod2002019Key.I0357,Mod2002019Key.D0358, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2511, Mod2002019KeyDC.DC2512, Mod2002019KeyDC.DC2513, Mod2002019KeyDC.DC2514, Mod2002019KeyDC.DC2515}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2516, Mod2002019KeyDC.DC2517, Mod2002019KeyDC.DC2518, Mod2002019KeyDC.DC2519, Mod2002019KeyDC.DC2520}, "Operaciones a plazos (art. 11.4 LIS)")
    ,C03(Mod2002019Key.I0359,Mod2002019Key.D0360, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2521, Mod2002019KeyDC.DC2522, Mod2002019KeyDC.DC2523, Mod2002019KeyDC.DC2524, Mod2002019KeyDC.DC2525}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2526, Mod2002019KeyDC.DC2527, Mod2002019KeyDC.DC2528, Mod2002019KeyDC.DC2529, Mod2002019KeyDC.DC2530}, "Reversi\u00F3n del deterioro del valor de los elementos patrimoniales (art. 11.6 LIS)") 
	,C04(Mod2002019Key.I0225,Mod2002019Key.D0226, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2531, Mod2002019KeyDC.DC2532, Mod2002019KeyDC.DC2533, Mod2002019KeyDC.DC2534, Mod2002019KeyDC.DC2535}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2536, Mod2002019KeyDC.DC2537, Mod2002019KeyDC.DC2538, Mod2002019KeyDC.DC2539, Mod2002019KeyDC.DC2540}, "Rentas negativas arts. 11.9, 10 y 11 LIS")
	,C05(Mod2002019Key.I1514,Mod2002019Key.D0272, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2541, Mod2002019KeyDC.DC2542, Mod2002019KeyDC.DC2543, Mod2002019KeyDC.DC2544, Mod2002019KeyDC.DC2545}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2546, Mod2002019KeyDC.DC2547, Mod2002019KeyDC.DC2548, Mod2002019KeyDC.DC2549, Mod2002019KeyDC.DC2550}, "Ajustes por rentas derivadas de operaciones con quita o espera (art. 11.13 LIS) ")
	,C06(Mod2002019Key.I0361,Mod2002019Key.D0362, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2551, Mod2002019KeyDC.DC2552, Mod2002019KeyDC.DC2553, Mod2002019KeyDC.DC2554, Mod2002019KeyDC.DC2555}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2556, Mod2002019KeyDC.DC2557, Mod2002019KeyDC.DC2558, Mod2002019KeyDC.DC2559, Mod2002019KeyDC.DC2560}, "Otras diferencias de imputaci\u00F3n temporal de ingresos y gastos (art. 11 LIS)")
	,C07(Mod2002019Key.I0303,Mod2002019Key.D0304, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2561, Mod2002019KeyDC.DC2562, Mod2002019KeyDC.DC2563, Mod2002019KeyDC.DC2564, Mod2002019KeyDC.DC2565}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2566, Mod2002019KeyDC.DC2567, Mod2002019KeyDC.DC2568, Mod2002019KeyDC.DC2569, Mod2002019KeyDC.DC2570}, "Diferencias entre amortizaci\u00F3n contable y fiscal (arts. 12.1 LIS)")
	,C08(null 				,Mod2002019Key.D0505, null                                                                                                                                   	   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2576, Mod2002019KeyDC.DC2577, Mod2002019KeyDC.DC2578, Mod2002019KeyDC.DC2579, Mod2002019KeyDC.DC2580}, "Deducci\u00F3n del 30% importe gastos de amortiz. contable (excluidas emp. reducida dimensi\u00F3n) (art. 7 Ley 16/2012)")
	,C09(Mod2002019Key.I1005,Mod2002019Key.D1006, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2581, Mod2002019KeyDC.DC2582, Mod2002019KeyDC.DC2583, Mod2002019KeyDC.DC2584, Mod2002019KeyDC.DC2585}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2586, Mod2002019KeyDC.DC2587, Mod2002019KeyDC.DC2588, Mod2002019KeyDC.DC2589, Mod2002019KeyDC.DC2590}, "Amortizaci\u00F3n del inmovilizado intangible y fondo de comercio (art. 12.2 LIS) y amortizaci\u00F3n de la DT 13a.1 LIS") 
	,C10(Mod2002019Key.I0305,Mod2002019Key.D0306, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2591, Mod2002019KeyDC.DC2592, Mod2002019KeyDC.DC2593, Mod2002019KeyDC.DC2594, Mod2002019KeyDC.DC2595}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2596, Mod2002019KeyDC.DC2597, Mod2002019KeyDC.DC2598, Mod2002019KeyDC.DC2599, Mod2002019KeyDC.DC2600}, "Amortizaci\u00F3n de inmovilizado afecto a actividades de investigaci\u00F3n y desarrollo (art. 12.3 b) LIS)")
	,C11(Mod2002019Key.I0307,Mod2002019Key.D0308, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2601, Mod2002019KeyDC.DC2602, Mod2002019KeyDC.DC2603, Mod2002019KeyDC.DC2604, Mod2002019KeyDC.DC2605}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2606, Mod2002019KeyDC.DC2607, Mod2002019KeyDC.DC2608, Mod2002019KeyDC.DC2609, Mod2002019KeyDC.DC2610}, "Libertad de amortizaci\u00F3n de gastos de investigaci\u00F3n y desarrollo (art. 12.3 c) LIS)")
	,C12(Mod2002019Key.I1003,Mod2002019Key.D1004, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2611, Mod2002019KeyDC.DC2612, Mod2002019KeyDC.DC2613, Mod2002019KeyDC.DC2614, Mod2002019KeyDC.DC2615}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2616, Mod2002019KeyDC.DC2617, Mod2002019KeyDC.DC2618, Mod2002019KeyDC.DC2619, Mod2002019KeyDC.DC2620}, "Libertad de amortizaci\u00F3n inmovilizado material nuevo (arts. 12.3 e) LIS)")
	,C13(Mod2002019Key.I0309,Mod2002019Key.D0310, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2621, Mod2002019KeyDC.DC2622, Mod2002019KeyDC.DC2623, Mod2002019KeyDC.DC2624, Mod2002019KeyDC.DC2625}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2626, Mod2002019KeyDC.DC2627, Mod2002019KeyDC.DC2628, Mod2002019KeyDC.DC2629, Mod2002019KeyDC.DC2630}, "Otros supuestos de libertad de amortizaci\u00F3n (arts. 12.3 a) y d) LIS)")
	,C14(Mod2002019Key.I0514,Mod2002019Key.D0509, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2631, Mod2002019KeyDC.DC2632, Mod2002019KeyDC.DC2633, Mod2002019KeyDC.DC2634, Mod2002019KeyDC.DC2635}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2636, Mod2002019KeyDC.DC2637, Mod2002019KeyDC.DC2638, Mod2002019KeyDC.DC2639, Mod2002019KeyDC.DC2640}, "Libertad de amortizaci\u00F3n con mantenimiento de empleo (RDL 6/2010 y DT 13a.2)") 
	,C15(Mod2002019Key.I0516,Mod2002019Key.D0551, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2641, Mod2002019KeyDC.DC2642, Mod2002019KeyDC.DC2643, Mod2002019KeyDC.DC2644, Mod2002019KeyDC.DC2645}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2646, Mod2002019KeyDC.DC2647, Mod2002019KeyDC.DC2648, Mod2002019KeyDC.DC2649, Mod2002019KeyDC.DC2650}, "Libertad de amortizaci\u00F3n sin mantenimiento de empleo (RDL 13/2010 y DT 13a.2)")
	,C16(Mod2002019Key.I0321,Mod2002019Key.D0322, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2651, Mod2002019KeyDC.DC2652, Mod2002019KeyDC.DC2653, Mod2002019KeyDC.DC2654, Mod2002019KeyDC.DC2655}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2656, Mod2002019KeyDC.DC2657, Mod2002019KeyDC.DC2658, Mod2002019KeyDC.DC2659, Mod2002019KeyDC.DC2660}, "P\u00E9rdidas por deterioro del art. 13.1 LIS no afectada por el art. 11.12 ni por DT 33a.1 LIS")
	,C17(Mod2002019Key.I0415,Mod2002019Key.D0211, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2661, Mod2002019KeyDC.DC2662, Mod2002019KeyDC.DC2663, Mod2002019KeyDC.DC2664, Mod2002019KeyDC.DC2665}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2666, Mod2002019KeyDC.DC2667, Mod2002019KeyDC.DC2668, Mod2002019KeyDC.DC2669, Mod2002019KeyDC.DC2670}, "P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS) a los que se refi ere el art. 11.12 y DT 33a.1 LIS.") 
	,C18(Mod2002019Key.I0331,Mod2002019Key.D0332, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2671, Mod2002019KeyDC.DC2672, Mod2002019KeyDC.DC2673, Mod2002019KeyDC.DC2674, Mod2002019KeyDC.DC2675}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2676, Mod2002019KeyDC.DC2677, Mod2002019KeyDC.DC2678, Mod2002019KeyDC.DC2679, Mod2002019KeyDC.DC2680}, "P\u00E9rdidas por deterioro de IM, inversiones inmobiliarias e II, incluido el fondo de comercio (art. 13.2 a) y DT 15 LIS)")
	,C19(Mod2002019Key.I0325,Mod2002019Key.D0326, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2681, Mod2002019KeyDC.DC2682, Mod2002019KeyDC.DC2683, Mod2002019KeyDC.DC2684, Mod2002019KeyDC.DC2685}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2686, Mod2002019KeyDC.DC2687, Mod2002019KeyDC.DC2688, Mod2002019KeyDC.DC2689, Mod2002019KeyDC.DC2690}, "Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (art. 13.2 b) LIS)")
	,C20(Mod2002019Key.I1518,Mod2002019Key.D0394, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2691, Mod2002019KeyDC.DC2692, Mod2002019KeyDC.DC2693, Mod2002019KeyDC.DC2694, Mod2002019KeyDC.DC2695}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2696, Mod2002019KeyDC.DC2697, Mod2002019KeyDC.DC2698, Mod2002019KeyDC.DC2699, Mod2002019KeyDC.DC2700}, "Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (DT 16a.1 y 2 LIS)")
	,C21(Mod2002019Key.I0333,Mod2002019Key.D0334, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2701, Mod2002019KeyDC.DC2702, Mod2002019KeyDC.DC2703, Mod2002019KeyDC.DC2704, Mod2002019KeyDC.DC2705}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2706, Mod2002019KeyDC.DC2707, Mod2002019KeyDC.DC2708, Mod2002019KeyDC.DC2709, Mod2002019KeyDC.DC2710}, "Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (DT 16a.3 LIS)") 
	,C22(Mod2002019Key.I0327,Mod2002019Key.D0328, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2711, Mod2002019KeyDC.DC2712, Mod2002019KeyDC.DC2713, Mod2002019KeyDC.DC2714, Mod2002019KeyDC.DC2715}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2716, Mod2002019KeyDC.DC2717, Mod2002019KeyDC.DC2718, Mod2002019KeyDC.DC2719, Mod2002019KeyDC.DC2720}, "P\u00E9rdidas por deterioro de valores representativos de deuda (art. 13.2 c) LIS y DT 15a LIS)")
	,C23(Mod2002019Key.I0416,Mod2002019Key.D0543, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2721, Mod2002019KeyDC.DC2722, Mod2002019KeyDC.DC2723, Mod2002019KeyDC.DC2724, Mod2002019KeyDC.DC2725}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2726, Mod2002019KeyDC.DC2727, Mod2002019KeyDC.DC2728, Mod2002019KeyDC.DC2729, Mod2002019KeyDC.DC2730}, "Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS)")
	,C24(Mod2002019Key.I0335,Mod2002019Key.D0336, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2731, Mod2002019KeyDC.DC2732, Mod2002019KeyDC.DC2733, Mod2002019KeyDC.DC2734, Mod2002019KeyDC.DC2735}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2736, Mod2002019KeyDC.DC2737, Mod2002019KeyDC.DC2738, Mod2002019KeyDC.DC2739, Mod2002019KeyDC.DC2740}, "Gastos y provisiones por pensiones no afectados por el art. 11.12 LIS (arts. 14.1, 14.6 y 14.8 LIS)")
	,C25(Mod2002019Key.I0337,Mod2002019Key.D0338, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2741, Mod2002019KeyDC.DC2742, Mod2002019KeyDC.DC2743, Mod2002019KeyDC.DC2744, Mod2002019KeyDC.DC2745}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2746, Mod2002019KeyDC.DC2747, Mod2002019KeyDC.DC2748, Mod2002019KeyDC.DC2749, Mod2002019KeyDC.DC2750}, "Otras provisiones no deducibles fiscalmente (art. 14 LIS) no afectadas por el art. 11.12 LIS")
	,C26(null				,Mod2002019Key.D0368, null                                                                                                                                         , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2756, Mod2002019KeyDC.DC2757, Mod2002019KeyDC.DC2758, Mod2002019KeyDC.DC2759, Mod2002019KeyDC.DC2760}, "Subvenciones p\u00FAblicas incluidas en el resultado del ejercicio, no integrables en la base imponible (art. 14.8 LIS)")
	,C27(Mod2002019Key.I1002,null				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2761, Mod2002019KeyDC.DC2762, Mod2002019KeyDC.DC2763, Mod2002019KeyDC.DC2764, Mod2002019KeyDC.DC2765}, null                                                                                                                                   , "Gastos no deducibles por considerarse retribuci\u00F3n de fondos propios (art. 15 a) LIS)")
	,C28(Mod2002019Key.I1815,null               , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2771, Mod2002019KeyDC.DC2772, Mod2002019KeyDC.DC2773, Mod2002019KeyDC.DC2774, Mod2002019KeyDC.DC2775}, null                                                                                                                                   , "Multas, sanciones y otros (art. 15 c) LIS)")
	,C29(Mod2002019Key.I0343,null				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2781, Mod2002019KeyDC.DC2782, Mod2002019KeyDC.DC2783, Mod2002019KeyDC.DC2784, Mod2002019KeyDC.DC2785}, null                                                                                                                                   , "P\u00E9rdidas del juego (art. 15 d) LIS)")
	,C30(Mod2002019Key.I0339,null				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2791, Mod2002019KeyDC.DC2792, Mod2002019KeyDC.DC2793, Mod2002019KeyDC.DC2794, Mod2002019KeyDC.DC2795}, null                                                                                                                                   , "Gastos por donativos y liberalidades (art. 15 e) LIS)")
	,C31(Mod2002019Key.I1816,null               , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2801, Mod2002019KeyDC.DC2802, Mod2002019KeyDC.DC2803, Mod2002019KeyDC.DC2804, Mod2002019KeyDC.DC2805}, null                                                                                                                                   , "Gastos de actuaciones contrarias al ordenamiento jur\u00EDdico (art. 15 f) LIS)")
	,C32(Mod2002019Key.I0341,Mod2002019Key.D0342, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2811, Mod2002019KeyDC.DC2812, Mod2002019KeyDC.DC2813, Mod2002019KeyDC.DC2814, Mod2002019KeyDC.DC2815}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2816, Mod2002019KeyDC.DC2817, Mod2002019KeyDC.DC2818, Mod2002019KeyDC.DC2819, Mod2002019KeyDC.DC2820}, "Operaciones realizadas con para\u00EDsos fiscales (art. 15 g) LIS)")
	,C33(Mod2002019Key.I0508,null				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2821, Mod2002019KeyDC.DC2822, Mod2002019KeyDC.DC2823, Mod2002019KeyDC.DC2824, Mod2002019KeyDC.DC2825}, null                                                                                                                                   , "Gastos financieros derivados de deudas con entidades del grupo (art. 15 h) LIS)")
	,C34(Mod2002019Key.I1817,null               , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2831, Mod2002019KeyDC.DC2832, Mod2002019KeyDC.DC2833, Mod2002019KeyDC.DC2834, Mod2002019KeyDC.DC2835}, null                                                                                                                                   , "Gastos derivados de la extinci\u00F3n de la relaci\u00F3n laboral o mercantil (art. 15 i) LIS)")	
	,C35(Mod2002019Key.I1009,Mod2002019Key.D1010, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2841, Mod2002019KeyDC.DC2842, Mod2002019KeyDC.DC2843, Mod2002019KeyDC.DC2844, Mod2002019KeyDC.DC2845}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2846, Mod2002019KeyDC.DC2847, Mod2002019KeyDC.DC2848, Mod2002019KeyDC.DC2849, Mod2002019KeyDC.DC2850}, "Gastos correspondientes a operaciones realizadas con personas o entidades vinculadas (art. 15 j) LIS)")
	,C36(Mod2002019Key.I1807,Mod2002019Key.D1811, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2851, Mod2002019KeyDC.DC2852, Mod2002019KeyDC.DC2853, Mod2002019KeyDC.DC2854, Mod2002019KeyDC.DC2855}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2856, Mod2002019KeyDC.DC2857, Mod2002019KeyDC.DC2858, Mod2002019KeyDC.DC2859, Mod2002019KeyDC.DC2860}, "P\u00E9rdidas por deterioro de valorres repr. de partic. en el capital o fondos propios (art. 15 k) LIS)")
	,C37(Mod2002019Key.I1808,Mod2002019Key.D1812, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2861, Mod2002019KeyDC.DC2862, Mod2002019KeyDC.DC2863, Mod2002019KeyDC.DC2864, Mod2002019KeyDC.DC2865}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2866, Mod2002019KeyDC.DC2867, Mod2002019KeyDC.DC2868, Mod2002019KeyDC.DC2869, Mod2002019KeyDC.DC2870}, "Disminuci\u00F3n de valor originada por criterio de valor razonable (art. 15 l) LIS)")
	,C38(Mod2002019Key.I1813,Mod2002019Key.D1814, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2871, Mod2002019KeyDC.DC2872, Mod2002019KeyDC.DC2873, Mod2002019KeyDC.DC2874, Mod2002019KeyDC.DC2875}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2876, Mod2002019KeyDC.DC2877, Mod2002019KeyDC.DC2878, Mod2002019KeyDC.DC2879, Mod2002019KeyDC.DC2880}, "Deuda tributaria de actos jur\u00EDdicos documentados (ITP y AJD) (art. 15 m) LIS)")
	,C39(Mod2002019Key.I0363,Mod2002019Key.D0364, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2881, Mod2002019KeyDC.DC2882, Mod2002019KeyDC.DC2883, Mod2002019KeyDC.DC2884, Mod2002019KeyDC.DC2885}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2886, Mod2002019KeyDC.DC2887, Mod2002019KeyDC.DC2888, Mod2002019KeyDC.DC2889, Mod2002019KeyDC.DC2890}, "Ajustes por la limitaci\u00F3n en la deducibilidad en gastos financieros (art. 16 LIS)")
	,C40(Mod2002019Key.I0345,Mod2002019Key.D0346, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2891, Mod2002019KeyDC.DC2892, Mod2002019KeyDC.DC2893, Mod2002019KeyDC.DC2894, Mod2002019KeyDC.DC2895}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2896, Mod2002019KeyDC.DC2897, Mod2002019KeyDC.DC2898, Mod2002019KeyDC.DC2899, Mod2002019KeyDC.DC2900}, "Revalorizaciones contables (art. 17.1 LIS)")
	,C41(Mod2002019Key.I1818,Mod2002019Key.D1819, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2901, Mod2002019KeyDC.DC2902, Mod2002019KeyDC.DC2903, Mod2002019KeyDC.DC2904, Mod2002019KeyDC.DC2905}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2906, Mod2002019KeyDC.DC2907, Mod2002019KeyDC.DC2908, Mod2002019KeyDC.DC2909, Mod2002019KeyDC.DC2910}, "Operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos (art. 17.2 LIS)")	
	,C42(Mod2002019Key.I0371,null				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2911, Mod2002019KeyDC.DC2912, Mod2002019KeyDC.DC2913, Mod2002019KeyDC.DC2914, Mod2002019KeyDC.DC2915}, null                                                                                                                                   , "SICAV: Reducciones de capital y distribuci\u00F3n de la prima de emisi\u00F3n (art. 17.6 LIS)")	
	,C43(Mod2002019Key.I0347,Mod2002019Key.D0348, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2921, Mod2002019KeyDC.DC2922, Mod2002019KeyDC.DC2923, Mod2002019KeyDC.DC2924, Mod2002019KeyDC.DC2925}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2926, Mod2002019KeyDC.DC2927, Mod2002019KeyDC.DC2928, Mod2002019KeyDC.DC2929, Mod2002019KeyDC.DC2930}, "Transmisiones lucrativas y societarias: aplicaci\u00F3n del valor de mercado (art. 17.4 LIS)")
	,C44(Mod2002019Key.I1011,Mod2002019Key.D1012, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2931, Mod2002019KeyDC.DC2932, Mod2002019KeyDC.DC2933, Mod2002019KeyDC.DC2934, Mod2002019KeyDC.DC2935}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2936, Mod2002019KeyDC.DC2937, Mod2002019KeyDC.DC2938, Mod2002019KeyDC.DC2939, Mod2002019KeyDC.DC2940}, "Operaciones vinculadas: aplicaci\u00F3n del valor de mercado (art. 18 LIS )")
	,C45(Mod2002019Key.I1013,Mod2002019Key.D1014, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2941, Mod2002019KeyDC.DC2942, Mod2002019KeyDC.DC2943, Mod2002019KeyDC.DC2944, Mod2002019KeyDC.DC2945}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2946, Mod2002019KeyDC.DC2947, Mod2002019KeyDC.DC2948, Mod2002019KeyDC.DC2949, Mod2002019KeyDC.DC2950}, "Cambios de residencia y otras operaciones del art. 19 LIS")
	,C46(Mod2002019Key.I1015,Mod2002019Key.D1016, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2951, Mod2002019KeyDC.DC2952, Mod2002019KeyDC.DC2953, Mod2002019KeyDC.DC2954, Mod2002019KeyDC.DC2955}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2956, Mod2002019KeyDC.DC2957, Mod2002019KeyDC.DC2958, Mod2002019KeyDC.DC2959, Mod2002019KeyDC.DC2960}, "Efectos de la valoraci\u00F3n contable diferente a la fiscal (art. 20 LIS)")
	,C47(null				,Mod2002019Key.D0370, null                                                                                                                                   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2966, Mod2002019KeyDC.DC2967, Mod2002019KeyDC.DC2968, Mod2002019KeyDC.DC2969, Mod2002019KeyDC.DC2970}, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades residentes (art. 21.1 LIS)")
	,C48(null				,Mod2002019Key.D2181, null                                                                                                                                   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2976, Mod2002019KeyDC.DC2977, Mod2002019KeyDC.DC2978, Mod2002019KeyDC.DC2979, Mod2002019KeyDC.DC2980}, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades no residentes (art. 21.1 LIS)")
	,C49(Mod2002019Key.I2182,Mod2002019Key.D2183, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2981, Mod2002019KeyDC.DC2982, Mod2002019KeyDC.DC2983, Mod2002019KeyDC.DC2984, Mod2002019KeyDC.DC2985}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2986, Mod2002019KeyDC.DC2987, Mod2002019KeyDC.DC2988, Mod2002019KeyDC.DC2989, Mod2002019KeyDC.DC2990}, "Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades residentes (art. 21.3 LIS)")
	,C50(Mod2002019Key.I2184,Mod2002019Key.D2185, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2991, Mod2002019KeyDC.DC2992, Mod2002019KeyDC.DC2993, Mod2002019KeyDC.DC2994, Mod2002019KeyDC.DC2995}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC2996, Mod2002019KeyDC.DC2997, Mod2002019KeyDC.DC2998, Mod2002019KeyDC.DC2999, Mod2002019KeyDC.DC3000}, "Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades no residentes (art. 21.3 LIS)")
	,C51(Mod2002019Key.I2186,Mod2002019Key.D2187, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3001, Mod2002019KeyDC.DC3002, Mod2002019KeyDC.DC3003, Mod2002019KeyDC.DC3004, Mod2002019KeyDC.DC3005}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3006, Mod2002019KeyDC.DC3007, Mod2002019KeyDC.DC3008, Mod2002019KeyDC.DC3009, Mod2002019KeyDC.DC3010}, "Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3 LIS distintos a transmisiones de valores de entidades, entidades residentes (art. 21.3 LIS)")
	,C52(Mod2002019Key.I2188,Mod2002019Key.D2189, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3011, Mod2002019KeyDC.DC3012, Mod2002019KeyDC.DC3013, Mod2002019KeyDC.DC3014, Mod2002019KeyDC.DC3015}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3016, Mod2002019KeyDC.DC3017, Mod2002019KeyDC.DC3018, Mod2002019KeyDC.DC3019, Mod2002019KeyDC.DC3020}, "Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3 LIS distintos a transmisiones de valores de entidades, entidades no residentes (art. 21.3 LIS)")
	,C53(Mod2002019Key.I0256,Mod2002019Key.D0278, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3021, Mod2002019KeyDC.DC3022, Mod2002019KeyDC.DC3023, Mod2002019KeyDC.DC3024, Mod2002019KeyDC.DC3025}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3026, Mod2002019KeyDC.DC3027, Mod2002019KeyDC.DC3028, Mod2002019KeyDC.DC3029, Mod2002019KeyDC.DC3030}, "Exenci\u00F3n de rentas en el extranjero (art. 22 LIS)")
	,C54(Mod2002019Key.I1822,Mod2002019Key.D0372, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3031, Mod2002019KeyDC.DC3032, Mod2002019KeyDC.DC3033, Mod2002019KeyDC.DC3034, Mod2002019KeyDC.DC3035}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3036, Mod2002019KeyDC.DC3037, Mod2002019KeyDC.DC3038, Mod2002019KeyDC.DC3039, Mod2002019KeyDC.DC3040}, "Reducci\u00F3n de rentas procedentes de determinados activos intangibles (art. 23 y DT 20a LIS)")
	,C55(Mod2002019Key.I0373,Mod2002019Key.D0374, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3041, Mod2002019KeyDC.DC3042, Mod2002019KeyDC.DC3043, Mod2002019KeyDC.DC3044, Mod2002019KeyDC.DC3045}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3046, Mod2002019KeyDC.DC3047, Mod2002019KeyDC.DC3048, Mod2002019KeyDC.DC3049, Mod2002019KeyDC.DC3050}, "Obra ben\u00E9fico-social de las cajas de ahorro y fundaciones bancarias (art. 24 LIS)")
	,C56(Mod2002019Key.I0340,Mod2002019Key.D1589, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3051, Mod2002019KeyDC.DC3052, Mod2002019KeyDC.DC3053, Mod2002019KeyDC.DC3054, Mod2002019KeyDC.DC3055}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3056, Mod2002019KeyDC.DC3057, Mod2002019KeyDC.DC3058, Mod2002019KeyDC.DC3059, Mod2002019KeyDC.DC3060}, "Impuesto extranjero soportado por el contribuyente, no deducible por afectar a rentas con deducci\u00F3n por doble imposici\u00F3n (art. 31.2 LIS)")
	,C57(Mod2002019Key.I0351,null 				, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3061, Mod2002019KeyDC.DC3062, Mod2002019KeyDC.DC3063, Mod2002019KeyDC.DC3064, Mod2002019KeyDC.DC3065}, null                                                                                                                                   , "Impuesto extranjero sobre los beneficios con cargo a los cuales se pagan los dividendos objeto de deducci\u00F3n por doble imposici\u00F3n internacional (art. 32.1 LIS)")
	,C58(Mod2002019Key.I0375,Mod2002019Key.D0376, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3071, Mod2002019KeyDC.DC3072, Mod2002019KeyDC.DC3073, Mod2002019KeyDC.DC3074, Mod2002019KeyDC.DC3075}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3076, Mod2002019KeyDC.DC3077, Mod2002019KeyDC.DC3078, Mod2002019KeyDC.DC3079, Mod2002019KeyDC.DC3080}, "Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico (Cap. II, T\u00EDt. VII LIS)")
	,C59(Mod2002019Key.I1320,Mod2002019Key.D1321, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3081, Mod2002019KeyDC.DC3082, Mod2002019KeyDC.DC3083, Mod2002019KeyDC.DC3084, Mod2002019KeyDC.DC3085}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3086, Mod2002019KeyDC.DC3087, Mod2002019KeyDC.DC3088, Mod2002019KeyDC.DC3089, Mod2002019KeyDC.DC3090}, "Uni\u00F3n temporal de empresas, ajustes del art. 45.1 LIS")
	,C60(Mod2002019Key.I0184,Mod2002019Key.D0544, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3091, Mod2002019KeyDC.DC3092, Mod2002019KeyDC.DC3093, Mod2002019KeyDC.DC3094, Mod2002019KeyDC.DC3095}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3096, Mod2002019KeyDC.DC3097, Mod2002019KeyDC.DC3098, Mod2002019KeyDC.DC3099, Mod2002019KeyDC.DC3100}, "Uni\u00F3n temporal de empresas, ajustes por rentas exentas de UTE que opera en el extranjero (art. 45.2 LIS)")
	,C61(Mod2002019Key.I1022,Mod2002019Key.D1023, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3101, Mod2002019KeyDC.DC3102, Mod2002019KeyDC.DC3103, Mod2002019KeyDC.DC3104, Mod2002019KeyDC.DC3105}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3106, Mod2002019KeyDC.DC3107, Mod2002019KeyDC.DC3108, Mod2002019KeyDC.DC3109, Mod2002019KeyDC.DC3110}, "Uni\u00F3n temporal de empresas, ajustes por rentas exentas por participar en el extranjero en f\u00F3rmulas de colaboraci\u00F3n an\u00E1logas a las UTE (art. 45.2 LIS)")
	,C62(Mod2002019Key.I1018,Mod2002019Key.D1019, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3111, Mod2002019KeyDC.DC3112, Mod2002019KeyDC.DC3113, Mod2002019KeyDC.DC3114, Mod2002019KeyDC.DC3115}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3116, Mod2002019KeyDC.DC3117, Mod2002019KeyDC.DC3118, Mod2002019KeyDC.DC3119, Mod2002019KeyDC.DC3120}, "Uni\u00F3n temporal de empresas, ajustes por criterios de imputaci\u00F3n temporal (art. 46.2 LIS)")
	,C63(Mod2002019Key.I1275,Mod2002019Key.D1276, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3121, Mod2002019KeyDC.DC3122, Mod2002019KeyDC.DC3123, Mod2002019KeyDC.DC3124, Mod2002019KeyDC.DC3125}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3126, Mod2002019KeyDC.DC3127, Mod2002019KeyDC.DC3128, Mod2002019KeyDC.DC3129, Mod2002019KeyDC.DC3130}, "Bases imp. negativas generadas dentro del grupo fi scal por la ent. transmitida y que hayan sido compensadas (art. 62.2 LIS)")
	,C64(Mod2002019Key.I0377,Mod2002019Key.D0378, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3131, Mod2002019KeyDC.DC3132, Mod2002019KeyDC.DC3133, Mod2002019KeyDC.DC3134, Mod2002019KeyDC.DC3135}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3136, Mod2002019KeyDC.DC3137, Mod2002019KeyDC.DC3138, Mod2002019KeyDC.DC3139, Mod2002019KeyDC.DC3140}, "Sociedades y fondos de capital-riesgo y sociedades de desarrollo industrial regional (cap\u00EDtulo IV del t\u00EDtulo VII LIS)")
	,C65(Mod2002019Key.I0379,Mod2002019Key.D0380, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3141, Mod2002019KeyDC.DC3142, Mod2002019KeyDC.DC3143, Mod2002019KeyDC.DC3144, Mod2002019KeyDC.DC3145}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3146, Mod2002019KeyDC.DC3147, Mod2002019KeyDC.DC3148, Mod2002019KeyDC.DC3149, Mod2002019KeyDC.DC3150}, "Valoraci\u00F3n de bienes y derechos. R\u00E9gimen especial operaciones reestructuraci\u00F3n (cap\u00EDtulo VII del t\u00EDtulo VII LIS)")
	,C66(Mod2002019Key.I0381,Mod2002019Key.D0382, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3151, Mod2002019KeyDC.DC3152, Mod2002019KeyDC.DC3153, Mod2002019KeyDC.DC3154, Mod2002019KeyDC.DC3155}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3156, Mod2002019KeyDC.DC3157, Mod2002019KeyDC.DC3158, Mod2002019KeyDC.DC3159, Mod2002019KeyDC.DC3160}, "Miner\u00EDa e hidrocarburos: factor agotamiento (arts. 91 y 95 LIS)")
	,C67(Mod2002019Key.I0383,Mod2002019Key.D0384, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3161, Mod2002019KeyDC.DC3162, Mod2002019KeyDC.DC3163, Mod2002019KeyDC.DC3164, Mod2002019KeyDC.DC3165}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3166, Mod2002019KeyDC.DC3167, Mod2002019KeyDC.DC3168, Mod2002019KeyDC.DC3169, Mod2002019KeyDC.DC3170}, "Hidrocarburos: Amortizaci\u00F3n de inversiones intangibles y gastos de investigaci\u00F3n (art. 99 LIS)") 
	,C68(Mod2002019Key.I0387,Mod2002019Key.D0388, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3171, Mod2002019KeyDC.DC3172, Mod2002019KeyDC.DC3173, Mod2002019KeyDC.DC3174, Mod2002019KeyDC.DC3175}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3176, Mod2002019KeyDC.DC3177, Mod2002019KeyDC.DC3178, Mod2002019KeyDC.DC3179, Mod2002019KeyDC.DC3180}, "Transparencia fiscal internacional (art. 100 LIS)")
	,C69(Mod2002019Key.I0311,Mod2002019Key.D0312, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3181, Mod2002019KeyDC.DC3182, Mod2002019KeyDC.DC3183, Mod2002019KeyDC.DC3184, Mod2002019KeyDC.DC3185}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3186, Mod2002019KeyDC.DC3187, Mod2002019KeyDC.DC3188, Mod2002019KeyDC.DC3189, Mod2002019KeyDC.DC3190}, "Empresas de reducida dimensi\u00F3n: libertad de amortizaci\u00F3n (art. 102 LIS)")  
	,C70(Mod2002019Key.I0313,Mod2002019Key.D0314, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3191, Mod2002019KeyDC.DC3192, Mod2002019KeyDC.DC3193, Mod2002019KeyDC.DC3194, Mod2002019KeyDC.DC3195}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3196, Mod2002019KeyDC.DC3197, Mod2002019KeyDC.DC3198, Mod2002019KeyDC.DC3199, Mod2002019KeyDC.DC3200}, "Empresas de reducida dimensi\u00F3n: amortizaci\u00F3n acelerada (art. 103 LIS)")
	,C71(Mod2002019Key.I0323,Mod2002019Key.D0324, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3201, Mod2002019KeyDC.DC3202, Mod2002019KeyDC.DC3203, Mod2002019KeyDC.DC3204, Mod2002019KeyDC.DC3205}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3206, Mod2002019KeyDC.DC3207, Mod2002019KeyDC.DC3208, Mod2002019KeyDC.DC3209, Mod2002019KeyDC.DC3210}, "Empresas de reducida dimensi\u00F3n: p\u00E9rdidas por deterioro cr\u00E9ditos insolvencias (art. 104 LIS)") 
	,C72(Mod2002019Key.I0317,Mod2002019Key.D0318, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3211, Mod2002019KeyDC.DC3212, Mod2002019KeyDC.DC3213, Mod2002019KeyDC.DC3214, Mod2002019KeyDC.DC3215}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3216, Mod2002019KeyDC.DC3217, Mod2002019KeyDC.DC3218, Mod2002019KeyDC.DC3219, Mod2002019KeyDC.DC3220}, "Arrendamiento financiero: r\u00E9gimen especial (art. 106 LIS)")
	,C73(Mod2002019Key.I0385,Mod2002019Key.D0386, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3221, Mod2002019KeyDC.DC3222, Mod2002019KeyDC.DC3223, Mod2002019KeyDC.DC3224, Mod2002019KeyDC.DC3225}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3226, Mod2002019KeyDC.DC3227, Mod2002019KeyDC.DC3228, Mod2002019KeyDC.DC3229, Mod2002019KeyDC.DC3230}, "R\u00E9gimen fiscal entidades de tenencia de valores extranjeros (cap\u00EDtulo XIII del t\u00EDtulo VII LIS)")
	,C74(Mod2002019Key.I0389,Mod2002019Key.D0390, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3231, Mod2002019KeyDC.DC3232, Mod2002019KeyDC.DC3233, Mod2002019KeyDC.DC3234, Mod2002019KeyDC.DC3235}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3236, Mod2002019KeyDC.DC3237, Mod2002019KeyDC.DC3238, Mod2002019KeyDC.DC3239, Mod2002019KeyDC.DC3240}, "R\u00E9gimen de entidades parcialmente exentas (cap\u00EDtulo XIV del t\u00EDtulo VII LIS)")
	,C75(null				,Mod2002019Key.D0396, null                                                                                                                                   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3246, Mod2002019KeyDC.DC3247, Mod2002019KeyDC.DC3248, Mod2002019KeyDC.DC3249, Mod2002019KeyDC.DC3250}, "Montes vecinales en mano com\u00FAn (cap\u00EDtulo XV del t\u00EDtulo VII LIS)")
	,C76(Mod2002019Key.I0397,Mod2002019Key.D0398, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3251, Mod2002019KeyDC.DC3252, Mod2002019KeyDC.DC3253, Mod2002019KeyDC.DC3254, Mod2002019KeyDC.DC3255}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3256, Mod2002019KeyDC.DC3257, Mod2002019KeyDC.DC3258, Mod2002019KeyDC.DC3259, Mod2002019KeyDC.DC3260}, "R\u00E9gimen de entidades navieras en funci\u00F3n del tonelaje (cap\u00EDtulo XVI del t\u00EDtulo VII LIS)")
	,C77(Mod2002019Key.I0250,Mod2002019Key.D0251, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3261, Mod2002019KeyDC.DC3262, Mod2002019KeyDC.DC3263, Mod2002019KeyDC.DC3264, Mod2002019KeyDC.DC3265}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3266, Mod2002019KeyDC.DC3267, Mod2002019KeyDC.DC3268, Mod2002019KeyDC.DC3269, Mod2002019KeyDC.DC3270}, "Aportaciones y colaboraci\u00F3n a favor de entidades sin fines lucrativos")
	,C78(Mod2002019Key.I0391,Mod2002019Key.D0392, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3271, Mod2002019KeyDC.DC3272, Mod2002019KeyDC.DC3273, Mod2002019KeyDC.DC3274, Mod2002019KeyDC.DC3275}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3276, Mod2002019KeyDC.DC3277, Mod2002019KeyDC.DC3278, Mod2002019KeyDC.DC3279, Mod2002019KeyDC.DC3280}, "R\u00E9gimen fiscal entidades sin fines lucrativos (Ley 49/2002)")
	,C79(null				,Mod2002019Key.D0400, null                                                                                                                                   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3286, Mod2002019KeyDC.DC3287, Mod2002019KeyDC.DC3288, Mod2002019KeyDC.DC3289, Mod2002019KeyDC.DC3290}, "Cooperativas: Fondo de reserva obligatorio (Ley 20/1990)")
	,C80(Mod2002019Key.I0403,Mod2002019Key.D0404, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3291, Mod2002019KeyDC.DC3292, Mod2002019KeyDC.DC3293, Mod2002019KeyDC.DC3294, Mod2002019KeyDC.DC3295}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3296, Mod2002019KeyDC.DC3297, Mod2002019KeyDC.DC3298, Mod2002019KeyDC.DC3299, Mod2002019KeyDC.DC3300}, "Reserva para inversiones en Canarias (Ley 19/1994)")
	,C81(Mod2002019Key.I0518,Mod2002019Key.D0519, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3301, Mod2002019KeyDC.DC3302, Mod2002019KeyDC.DC3303, Mod2002019KeyDC.DC3304, Mod2002019KeyDC.DC3305}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3306, Mod2002019KeyDC.DC3307, Mod2002019KeyDC.DC3308, Mod2002019KeyDC.DC3309, Mod2002019KeyDC.DC3310}, "Exenci\u00F3n transmisi\u00F3n bienes inmuebles (DA 6a LIS)")
	,C82(null				,Mod2002019Key.D1824, null                                                                                                                                   , new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3316, Mod2002019KeyDC.DC3317, Mod2002019KeyDC.DC3318, Mod2002019KeyDC.DC3319, Mod2002019KeyDC.DC3320}, "Rentas procedentes de transmisi\u00F3n de inmovilizado obtenidas por las Autoridades Portuarias (DA 68\u00AA Ley 6/2018)")
	,C83(Mod2002019Key.I0510,Mod2002019Key.D0512, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3321, Mod2002019KeyDC.DC3322, Mod2002019KeyDC.DC3323, Mod2002019KeyDC.DC3324, Mod2002019KeyDC.DC3325}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3326, Mod2002019KeyDC.DC3327, Mod2002019KeyDC.DC3328, Mod2002019KeyDC.DC3329, Mod2002019KeyDC.DC3330}, "Operaciones a plazos (DT 1a LIS)")
	,C84(Mod2002019Key.I0329,Mod2002019Key.D0330, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3331, Mod2002019KeyDC.DC3332, Mod2002019KeyDC.DC3333, Mod2002019KeyDC.DC3334, Mod2002019KeyDC.DC3335}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3336, Mod2002019KeyDC.DC3337, Mod2002019KeyDC.DC3338, Mod2002019KeyDC.DC3339, Mod2002019KeyDC.DC3340}, "Adquisici\u00F3n de participaciones en entidades no residentes (DT 14a LIS) (hasta el 21/12/07)")
	,C85(Mod2002019Key.I0365,Mod2002019Key.D1026, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3341, Mod2002019KeyDC.DC3342, Mod2002019KeyDC.DC3343, Mod2002019KeyDC.DC3344, Mod2002019KeyDC.DC3345}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3346, Mod2002019KeyDC.DC3347, Mod2002019KeyDC.DC3348, Mod2002019KeyDC.DC3349, Mod2002019KeyDC.DC3350}, "Reinversi\u00F3n de beneficios extraordinarios (DT 24a LIS)")
	,C86(Mod2002019Key.I2129,Mod2002019Key.D2130, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3351, Mod2002019KeyDC.DC3352, Mod2002019KeyDC.DC3353, Mod2002019KeyDC.DC3354, Mod2002019KeyDC.DC3355}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3356, Mod2002019KeyDC.DC3357, Mod2002019KeyDC.DC3358, Mod2002019KeyDC.DC3359, Mod2002019KeyDC.DC3360}, "Ajustes por la primera aplicaci\u00F3n de la Circular 4/2017 del Banco de Espa\u00F1a, a entidades de cr\u00E9dito")	
	,C87(Mod2002019Key.I0409,Mod2002019Key.D0410, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3361, Mod2002019KeyDC.DC3362, Mod2002019KeyDC.DC3363, Mod2002019KeyDC.DC3364, Mod2002019KeyDC.DC3365}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3366, Mod2002019KeyDC.DC3367, Mod2002019KeyDC.DC3368, Mod2002019KeyDC.DC3369, Mod2002019KeyDC.DC3370}, "Entidades en r\u00E9g. de atribuci\u00F3n de rentas const. en el extranj. con presencia en territ. espa\u00F1ol (art. 38 TRLIRNR)") 
	,C88(Mod2002019Key.I0411,Mod2002019Key.D0412, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3371, Mod2002019KeyDC.DC3372, Mod2002019KeyDC.DC3373, Mod2002019KeyDC.DC3374, Mod2002019KeyDC.DC3375}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3376, Mod2002019KeyDC.DC3377, Mod2002019KeyDC.DC3378, Mod2002019KeyDC.DC3379, Mod2002019KeyDC.DC3380}, "Correcciones espec\u00EDficas de entidades sometidas a la normativa foral")
	,C89(Mod2002019Key.I1027,Mod2002019Key.D1028, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3381, Mod2002019KeyDC.DC3382, Mod2002019KeyDC.DC3383, Mod2002019KeyDC.DC3384, Mod2002019KeyDC.DC3385}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3386, Mod2002019KeyDC.DC3387, Mod2002019KeyDC.DC3388, Mod2002019KeyDC.DC3389, Mod2002019KeyDC.DC3390}, "Eliminaciones pendientes de incorporar de sociedades que dejen de pertenecer a un grupo")
	,C90(Mod2002019Key.I0413,Mod2002019Key.D0414, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3391, Mod2002019KeyDC.DC3392, Mod2002019KeyDC.DC3393, Mod2002019KeyDC.DC3394, Mod2002019KeyDC.DC3395}, new Mod2002019KeyDC[]{Mod2002019KeyDC.DC3396, Mod2002019KeyDC.DC3397, Mod2002019KeyDC.DC3398, Mod2002019KeyDC.DC3399, Mod2002019KeyDC.DC3400}, "Otras correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	;
		 
    private String description;
    private Mod2002019Key increase;
    private Mod2002019Key decrease;
    private Mod2002019KeyDC[] detailIncrease;
    private Mod2002019KeyDC[] detailDecrease;
    
    private Mod2002019Key[] keys;
    private CorrectionDetail[] detail; 

	private Mod2002019CorrectionKey(Mod2002019Key increase, Mod2002019Key decrease, Mod2002019KeyDC[] detailIncrease, Mod2002019KeyDC[] detailDecrease, String description) {
		this.increase = increase;
		this.decrease = decrease;
		this.detailIncrease = detailIncrease;
		this.detailDecrease = detailDecrease;
		this.description = description;
		
		keys = new Mod2002019Key[]{increase,decrease};
		
		// Añadimos las casillas de detalle, de cada una de las casillas de las correcciones al resultado contable
		detail = null;
		for (int i=0; i<CorrectionDetail.DETAIL_DESCRIPTIONS.length; i++) {
			
			Mod2002019KeyDC ik = null;
			Mod2002019KeyDC dk = null;

			if (increase != null && detailIncrease != null) {		
				ik = detailIncrease[i];
			}
			
			if (decrease != null && detailDecrease != null) {				
				dk = detailDecrease[i];				
			}			

			if (ik!=null || dk!=null) {
				if (detail == null) 
			    	detail = new CorrectionDetail[CorrectionDetail.DETAIL_DESCRIPTIONS.length];
				
				detail[i] = new CorrectionDetail();
				if (ik!=null) detail[i].setIncrease(ik);
				if (dk!=null) detail[i].setDecrease(dk);					
				detail[i].setDescription(CorrectionDetail.DETAIL_DESCRIPTIONS[i]);				
			}
		}
	}
	
	public CorrectionDetail[] getDetail() {
		return detail;
	}		
	
	public String getDescription() {
		return description;
	}
	
	public Mod2002019Key getIncrease() {
		return increase;
	}
	
	public Mod2002019Key getDecrease() {
		return decrease;
	}
	
	public Mod2002019KeyDC[] getDetailIncrease() {
		return detailIncrease;
	}

	public Mod2002019KeyDC[] getDetailDecrease() {
		return detailDecrease;
	}

	public boolean isIncreaseEnabled() {
		return (getIncrease() != null);
	}
	
	public boolean isDecreaseEnabled() {
		return (getDecrease() != null);
	}
	
	@Override
	public Mod2002019Key[] getKeys() {
		return keys; 
	}
	
//	public static void main(String[] args) {
//		int i = 0;
//		int d = 0;
//		for (Mod2002019CorrectionKey k : Mod2002019CorrectionKey.values()) {
//			System.out.println(k.getIncrease()+"   "+k.getDecrease());
//			
//			CorrectionDetail[] p = k.getDetail();
//			if (p!=null)
//			for (int j=0;j<p.length;j++) {
//				if (p[j]!=null)
//				  System.out.println(p[j].getKeys()[0]+ " " +p[j].getKeys()[1]+ " "+ p[j].getDescription());
//			}
//			
//			if (k.getIncrease() != null) i++;
//			if (k.getDecrease() != null) d++;
//		}
//		System.out.println( "I ..: " + i);
//		System.out.println( "D ..: " + d);
//	}

}

final class CorrectionDetail implements Serializable, IMod200KeysProvider {
	
	private static final long serialVersionUID = 791258471554632660L;
	
	public static String[] DETAIL_DESCRIPTIONS = new String[] {			
			 "Correcciones del ejercicio - Permanentes"
			,"Correcciones del ejercicio - Temporarias (con origen en el ejercicio)" 
			,"Correcciones del ejercicio - Temporarias (con origen en ejercicios anteriores)"
			,"Saldo pendiente a fin de ejercicio - Temporarias (con origen en el ejercicio)"
			,"Saldo pendiente a fin de ejercicio - Temporarias (con origen en ejercicios anteriores)"
		};	
	
	private Mod2002019KeyDC increase;
	private Mod2002019KeyDC decrease;
	private String description;

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public IMod200Key[] getKeys() {
		return new Mod2002019KeyDC[]{increase,decrease};
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setIncrease(Mod2002019KeyDC increase) {
		this.increase = increase;
	}

	public void setDecrease(Mod2002019KeyDC decrease) {
		this.decrease = decrease;
	}
	
}


