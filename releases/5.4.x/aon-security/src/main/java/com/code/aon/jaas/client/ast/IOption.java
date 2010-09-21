/**
 * 
 */
package com.code.aon.jaas.client.ast;

import java.util.Properties;


/**
 * @author Consulting & Development. Iñaki Ayerbe - 07/05/2007
 *
 */
public interface IOption extends INode {

	String getName();

	String getValue();

	Properties toProperties();
}
