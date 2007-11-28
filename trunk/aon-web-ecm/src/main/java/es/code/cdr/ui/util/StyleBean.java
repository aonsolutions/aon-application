/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package es.code.cdr.ui.util;

import es.code.cdr.ui.navigation.TreeNavigation;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The StyleBean class is the backing bean which manages the demonstrations'
 * active theme.  There are currently two themes supported by the bean; XP and
 * Royale. </p>
 * <p/>
 * <p>The webpages' style attributes are modified by changing link in the header
 * of the HTML document.  The selectInputDate and tree components' styles are
 * changed by changing the location of their image src directories.</p>
 *
 * @since 0.3.0
 */
public class StyleBean {

    // possible theme choices
    private final String XP = "xp";
    private final String ROYALE = "royale";

    // default theme
    private String currentStyle = XP;
    private String tempStyle = XP;

    // available style list
    private ArrayList styleList;

    // default theme image directory for selectinputdate and theme.
    private String imageDirectory = "/xmlhttp/css/xp/css-images/";

    // navigation tree reference for updating folder icons
    TreeNavigation treeNav;

    // folder icons for the respective themes
    public static final String XP_BRANCH_EXPANDED_ICON = "/xmlhttp/css/xp/css-images/tree_folder_open.gif";
    public static final String XP_BRANCH_CONTRACTED_ICON = "/xmlhttp/css/xp/css-images/tree_folder_close.gif";
    public static final String ROYALE_BRANCH_EXPANDED_ICON = "/xmlhttp/css/royale/css-images/tree_folder_open.gif";
    public static final String ROYALE_BRANCH_CONTRACTED_ICON = "/xmlhttp/css/royale/css-images/tree_folder_close.gif";

    /**
     * Creates a new instance of the StyleBean.
     */
    public StyleBean() {
        // initialize the style list
        styleList = new ArrayList();
        styleList.add(new SelectItem(XP, XP));
        styleList.add(new SelectItem(ROYALE, ROYALE));
    }

    /**
     * Gets the current style.
     *
     * @return current style
     */
    public String getCurrentStyle() {
        return currentStyle;
    }

    /**
     * Sets the current style of the application to one of the predetermined
     * themes.
     *
     * @param currentStyle
     */
    public void setCurrentStyle(String currentStyle) {
        this.tempStyle = currentStyle;
    }

    /**
     * Gets the html needed to insert a valid css link tag.
     *
     * @return the tag information needed for a valid css link tag
     */
    public String getStyle() {
        return "<link rel=\"stylesheet\" type=\"text/css\" href=\"xmlhttp/css/" +
               currentStyle + "/" + currentStyle + ".css" + "\"></link>";
    }

    /**
     * Gets the image directory to use for the selectinputdate and tree
     * theming.
     *
     * @return image directory used for theming
     */
    public String getImageDirectory() {
        return imageDirectory;
    }

    /**
     * Applies temp style to to the current style and image directory and
     * manually refreshes the icons in the navigation tree. The page will reload
     * based on navigation rules to ensure the theme is applied; this is
     * necessary because of difficulties encountered by updating the stylesheet
     * reference within the <HEAD> of the document.
     *
     * @return the reload navigation attribute
     */
    public String changeStyle() {
        currentStyle = tempStyle;
        imageDirectory = "/xmlhttp/css/" + currentStyle + "/css-images/";

        // manually update the icons in the navigation tree
        treeNav.refreshIcons(currentStyle);

        return "reload";
    }

    /**
     * Gets a list of available theme names that can be applied.
     *
     * @return available theme list
     */
    public List getStyleList() {
        return styleList;
    }

    /**
     * Provides the StyleBean with a reference to the TreeNavigation, enabling
     * the StyleBean to use <code>refreshIcons()</code> to change the navigation
     * icons based on the theme.
     *
     * @param treeNav
     */
    public void registerTree(TreeNavigation treeNav) {

        this.treeNav = treeNav;
    }
}