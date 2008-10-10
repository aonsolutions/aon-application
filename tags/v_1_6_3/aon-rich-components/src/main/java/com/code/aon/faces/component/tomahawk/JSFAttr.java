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


/**
 * Constant declarations for JSF tags
 * @author Anton Koinov (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface JSFAttr
{
    //~ Static fields/initializers -----------------------------------------------------------------

    // Common Attributes
    String   ID_ATTR                        = "id";
    String   VALUE_ATTR                     = "value";
    String   BINDING_ATTR                   = "binding";
    String   STYLE_ATTR                     = "style";
    String   STYLE_CLASS_ATTR               = "styleClass";
    String   ESCAPE_ATTR                    = "escape";
    String   FORCE_ID_ATTR                  = "forceId";
    String   FORCE_ID_INDEX_ATTR            = "forceIdIndex";
    String   ACCEPTCHARSET_ATTR             = "acceptcharset";

    // Common Output Attributes
    String   FOR_ATTR                       = "for";
    String   CONVERTER_ATTR                 = "converter";

    // Ouput_Time Attributes
    String   TIME_STYLE_ATTR                = "timeStyle";
    String   TIMEZONE_ATTR                  = "timezone";

    // Common Input Attributes
    String   REQUIRED_ATTR                  = "required";
    String   VALIDATOR_ATTR                 = "validator";
    String   DISABLED_ATTR                  = "disabled";

    // Input_Secret Attributes
    String   REDISPLAY_ATTR                 = "redisplay";

    // Input_Checkbox Attributes
    String   LAYOUT_ATTR                    = "layout";

    // Select_Menu Attributes
    String   SIZE_ATTR                     = "size";

    // SelectMany Checkbox List/ Select One Radio Attributes
    String BORDER_ATTR                 = "border";
    String DISABLED_CLASS_ATTR         = "disabledClass";
    String ENABLED_CLASS_ATTR          = "enabledClass";

    // Common Command Attributes
    /**@deprecated */
    String   COMMAND_CLASS_ATTR           = "commandClass";
    String   LABEL_ATTR                   = "label";
    String   IMAGE_ATTR                   = "image";
    String   ACTION_ATTR                 = "action";
    String   IMMEDIATE_ATTR              = "immediate";


    // Command_Button Attributes
    String   TYPE_ATTR                    = "type";

    // Common Panel Attributes
    /**@deprecated */
    String   PANEL_CLASS_ATTR       = "panelClass";
    String   FOOTER_CLASS_ATTR      = "footerClass";
    String   HEADER_CLASS_ATTR      = "headerClass";
    String   COLUMN_CLASSES_ATTR    = "columnClasses";
    String   ROW_CLASSES_ATTR       = "rowClasses";

    // Panel_Grid Attributes
    String   COLUMNS_ATTR          = "columns";
    String   COLSPAN_ATTR          = "colspan"; // extension

    // UIMessage and UIMessages attributes
    String SHOW_SUMMARY_ATTR            = "showSummary";
    String SHOW_DETAIL_ATTR             = "showDetail";
    String GLOBAL_ONLY_ATTR             = "globalOnly";

    // HtmlOutputMessage attributes
    String ERROR_CLASS_ATTR            = "errorClass";
    String ERROR_STYLE_ATTR            = "errorStyle";
    String FATAL_CLASS_ATTR            = "fatalClass";
    String FATAL_STYLE_ATTR            = "fatalStyle";
    String INFO_CLASS_ATTR             = "infoClass";
    String INFO_STYLE_ATTR             = "infoStyle";
    String WARN_CLASS_ATTR             = "warnClass";
    String WARN_STYLE_ATTR             = "warnStyle";
    String TITLE_ATTR                  = "title";
    String TOOLTIP_ATTR                = "tooltip";

    // GraphicImage attributes
    String URL_ATTR                    = "url";

    // UISelectItem attributes
    String ITEM_DISABLED_ATTR          = "itemDisabled";
    String ITEM_DESCRIPTION_ATTR       = "itemDescription";
    String ITEM_LABEL_ATTR             = "itemLabel";
    String ITEM_VALUE_ATTR             = "itemValue";

    // UIData attributes
    String ROWS_ATTR                   = "rows";
    String VAR_ATTR                    = "var";
    String FIRST_ATTR                  = "first";

    // dataTable (extended) attributes
    String ROW_ID                      = "org.apache.myfaces.dataTable.ROW_ID";
    String ROW_STYLECLASS_ATTR         = "org.apache.myfaces.dataTable.ROW_STYLECLASS";
    String ROW_STYLE_ATTR              = "org.apache.myfaces.dataTable.ROW_STYLE";

    // Alternate locations (instead of using AddResource)
    String JAVASCRIPT_LOCATION         = "org.apache.myfaces.JAVASCRIPT_LOCATION";
    String IMAGE_LOCATION              = "org.apache.myfaces.IMAGE_LOCATION";
    String STYLE_LOCATION              = "org.apache.myfaces.STYLE_LOCATION";



    //~ Myfaces Extensions -------------------------------------------------------------------------------

    // UISortData attributes
    String COLUMN_ATTR                 = "column";
    String ASCENDING_ATTR              = "ascending";
    
    // HtmlSelectManyCheckbox attributes
    String LAYOUT_WIDTH_ATTR           = "layoutWidth";

}