<#if ejb3>
<#if pojo.hasIdentifierProperty()>
<#if property.equals(clazz.identifierProperty)>
${pojo.generateAnnIdGenerator()}
</#if>
</#if>
<#if c2h.isManyToOne(property)>	${pojo.generateManyToOneAnnotation(property)}
${pojo.generateJoinColumnsAnnotation(property, cfg)}
<#elseif c2h.isCollection(property)>
${pojo.generateCollectionAnnotation(property, cfg)}
<#else>
<#if !pojo.generateBasicAnnotation(property).trim().equals("")>
${pojo.generateBasicAnnotation(property)}
</#if>
${pojo.generateAnnColumnAnnotation(property)}
</#if>
</#if>
