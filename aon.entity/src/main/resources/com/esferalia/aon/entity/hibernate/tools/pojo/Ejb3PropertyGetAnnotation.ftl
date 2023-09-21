<#if ejb3>
<#if pojo.hasIdentifierProperty()>
<#if property.equals(clazz.identifierProperty)>
<#if hasRegistryPrimaryKeyJoinColumn>    @${pojo.importType("jakarta.persistence.Id")} 
	@jakarta.persistence.GeneratedValue(generator="registry_id")
	@org.hibernate.annotations.GenericGenerator(name="registry_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="registry")})
<#elseif hasProjectPrimaryKeyJoinColumn>    @${pojo.importType("jakarta.persistence.Id")} 
	@jakarta.persistence.GeneratedValue(generator="project_id")
	@org.hibernate.annotations.GenericGenerator(name="project_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="project")})
<#elseif hasAssetPrimaryKeyJoinColumn>    @${pojo.importType("jakarta.persistence.Id")} 
	@jakarta.persistence.GeneratedValue(generator="asset_id")
	@org.hibernate.annotations.GenericGenerator(name="asset_id", strategy="foreign", parameters = {@org.hibernate.annotations.Parameter(name="property", value="asset")})
<#else>
${pojo.generateAnnIdGenerator()}
	</#if>
</#if>
</#if>
<#if property.getName()=="system" && pojo.getDeclarationName()=="GeoZoneDB">
    @Column(name="`system`", nullable=false)
<#elseif property.getName()=="product" && pojo.getDeclarationName()=="ItemDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}
    @jakarta.persistence.JoinColumn(name="product", nullable=false)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="child" && pojo.getDeclarationName()=="GeoTreeDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}
	@${pojo.importType("jakarta.persistence.OneToOne")}(cascade={${pojo.importType("jakarta.persistence.CascadeType")}.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade(value={org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    @jakarta.persistence.JoinColumn(name="child", nullable = false)
<#elseif property.getName()=="workPlace" && pojo.getDeclarationName()=="HotelDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}
    @jakarta.persistence.JoinColumn(name="workplace", nullable = false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="payMethodTypeDetail" && pojo.getDeclarationName()=="PayMethodTypeDetailAccountDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}(fetch=${pojo.importType("jakarta.persistence.FetchType")}.EAGER)
    @jakarta.persistence.JoinColumn(name="pm_type_detail", nullable=false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="finance" && pojo.getDeclarationName()=="PrepaymentDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}(fetch=${pojo.importType("jakarta.persistence.FetchType")}.EAGER)
    @jakarta.persistence.JoinColumn(name="finance", nullable=false)
	@${pojo.importType("com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull")}
<#elseif property.getName()=="content" && pojo.getDeclarationName()=="MessageDB">	@${pojo.importType("jakarta.persistence.ManyToOne")}(cascade = {${pojo.importType("jakarta.persistence.CascadeType")}.ALL} )
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
	@jakarta.persistence.JoinColumn(name="message_content")
<#elseif property.getName()=="rattach" && pojo.getDeclarationName()=="WebInfoPageResourceDB">	@${pojo.importType("jakarta.persistence.OneToOne")}(fetch = ${pojo.importType("jakarta.persistence.FetchType")}.EAGER)
	@${pojo.importType("jakarta.persistence.JoinColumn")}( name="rattach" )
<#elseif property.getName()=="webInfoPage" && pojo.getDeclarationName()=="WebInfoPageDetailDB">	@${pojo.importType("jakarta.persistence.OneToOne")}(fetch = ${pojo.importType("jakarta.persistence.FetchType")}.EAGER)	
	@${pojo.importType("jakarta.persistence.JoinColumn")}( name="web_info_page",nullable=false )
<#elseif property.getName()=="address" && pojo.getDeclarationName()=="WorkPlaceDB">	@${pojo.importType("jakarta.persistence.OneToOne")}(cascade={${pojo.importType("jakarta.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@jakarta.persistence.JoinColumn(name="address", nullable = false)
<#elseif property.getName()=="enterprise" && pojo.getDeclarationName()=="EnterpriseDataDB">	@${pojo.importType("jakarta.persistence.OneToOne")}
	@jakarta.persistence.JoinColumn(name="enterprise", nullable=false)
<#elseif property.getName()=="enterprise" && pojo.getDeclarationName()=="EnterpriseActivityDB">	@${pojo.importType("jakarta.persistence.OneToOne")}
    @jakarta.persistence.JoinColumn(name="enterprise", nullable = false, updatable = false )
<#elseif property.getName()=="activity" && pojo.getDeclarationName()=="EnterpriseCCCDB">	@${pojo.importType("jakarta.persistence.OneToOne")}
	@jakarta.persistence.JoinColumn(name="enterprise_activity", nullable = false)
<#elseif property.getName()=="registry" && hasRegistryPrimaryKeyJoinColumn>	@${pojo.importType("jakarta.persistence.OneToOne")}(cascade={${pojo.importType("jakarta.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}	
<#elseif property.getName()=="project" && hasProjectPrimaryKeyJoinColumn>	@${pojo.importType("jakarta.persistence.OneToOne")}(cascade={${pojo.importType("jakarta.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}	
<#elseif property.getName()=="asset" && hasAssetPrimaryKeyJoinColumn>	@${pojo.importType("jakarta.persistence.OneToOne")}(cascade={${pojo.importType("jakarta.persistence.CascadeType")}.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@${pojo.importType("jakarta.persistence.PrimaryKeyJoinColumn")}	
<#elseif c2h.isManyToOne(property)>	${pojo.generateManyToOneAnnotation(property)}
${pojo.generateJoinColumnsAnnotation(property, cfg)}
<#elseif c2h.isCollection(property)>
${pojo.generateCollectionAnnotation(property, cfg)}
<#else>
<#if !pojo.generateBasicAnnotation(property).trim().equals("")>
${pojo.generateBasicAnnotation(property)}
</#if>
${pojo.generateAnnColumnAnnotation(property)}
<#if property.getType().getName()=="text" || property.getType().getName()=="binary">
	@${pojo.importType("jakarta.persistence.Lob")}
</#if>
<#if property.getType().getName()=="com.code.aon.config.BankAccount">
	@${pojo.importType("org.hibernate.annotations.Type")}(type="com.code.aon.config.hibernate.BankAccountType")
</#if>
<#if property.getType().getName()=="com.code.aon.product.util.DiscountExpression">
	@${pojo.importType("org.hibernate.annotations.Type")}(type="com.code.aon.product.util.DiscountExpressionUserType")
</#if>
<#if aonExporter.isStringEnum(property)>
	@${pojo.importType("org.hibernate.annotations.Type")}(type = "stringEnum", parameters = { @${pojo.importType("org.hibernate.annotations.Parameter")}(name = "enumClassname", value = "${property.getType().getName()}") })
</#if>
</#if>
</#if>
