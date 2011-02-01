/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.code.aon.faces.component.tomahawk;

import javax.faces.component.UIComponent;

/**
 * Simple read-only wrapper JavaBean which encapsulates the <code>UIComponent</code> that represents 
 * the form and the name of the Formular.
 */
public class FormInfo
{
	private final UIComponent form;
	private final String formName;
	
	/**
   * 
   * @param form <code>UIComponent</code> that represents the used form
   * @param formName the name of the form.
	 */
  public FormInfo(final UIComponent form, final String formName)
	{
		this.form = form;
		this.formName = formName;
	}

	public UIComponent getForm()
	{
		return form;
	}

	public String getFormName()
	{
		return formName;
	}
}
