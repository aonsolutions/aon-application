<!DOCTYPE hibernate-configuration PUBLIC
	"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
	"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>

	<session-factory>

		<property name="dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>
		<property name="show_sql">true</property>		
		<property name="connection.url">jdbc:mysql://127.0.0.1:3306/aon_reveng_${buildNumber}?autoReconnect=true</property>		
		<property name="connection.driver_class">com.mysql.jdbc.Driver</property>		
		<property name="connection.username">dbuser</property>		
		<property name="connection.password">serubd2000</property>		
		
		<mapping package="com.code.aon.common.dao.hibernate.type"/>

<#list pojos as pojo>
		<mapping class="${pojo}"/>
</#list>
		
	</session-factory>

</hibernate-configuration>
