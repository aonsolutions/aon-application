<?xml version="1.0" encoding="ISO-8859-1"?>
<report xmlns="http://www.inteco.es/xbrl/pgc07/interfazES"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.inteco.es/xbrl/pgc07/interfazES
	http://www.inteco.es/xbrl/pgc07/interfazES/pgc07-io-interface.xsd"
	id="${moduleId}" date="${reportDate?string("yyyy-MM-dd'T'HH:mm:ss")}">

    <entity id="${nombreEmpresa}" uri="http://www.icac.meh.es/xbrl/test"/>
    
	<#include "xml_apartado0.ftl">

	<#list 0..(ejercicios-1) as i>
		<module id="${module}" reportingDateStart="${inicioPeriodo(i)}" reportingDateEnd="${finPeriodo(i)}" baseUnit="euro" baseDecimals="2">
			<#assign periodIndex=i>
			<#include template>
		</module>
	</#list>
</report>
	