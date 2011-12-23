	private static final long serialVersionUID = 1L;

<#foreach field in pojo.getAllPropertiesIterator()>
	${pojo.getFieldModifiers(field)} ${pojo.getJavaTypeName(field, jdk5)} ${field.name}<#if pojo.hasFieldInitializor(field, jdk5)> = ${pojo.getFieldInitialization(field, jdk5)}</#if>;
</#foreach>
