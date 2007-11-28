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

package es.code.cdr.ui.navigation;

import com.icesoft.faces.component.tree.Tree;
import es.code.cdr.ui.util.StyleBean;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Enumeration;

/**
 * <p>The TreeNavigation class is the backing bean for the showcase navigation
 * tree on the left hand side of the application. Each node in the tree is made
 * up of a PageContent which is responsible for the navigation action when a
 * tree node is selected.</p>
 * <p/>
 * <p>When the Tree component binding takes place the tree nodes are initialized
 * and the tree is built.  Any addition to the tree navigation must be made to
 * this class.</p>
 *
 * @since 0.3.0
 */
public class TreeNavigation {

    // binding to component
    private Tree treeComponent;

    // bound to components value attribute
    private DefaultTreeModel model;

    // root node of tree, for delayed construction
    private DefaultMutableTreeNode rootTreeNode;

    // map of all navigation backing beans.
    private NavigationBean navigationBean;

    // initialization flag
    private boolean isInitiated;

    // folder icons for branch nodes
    private String themeBranchContractedIcon;
    private String themeBranchExpandedIcon;

    /**
     * Default constructor of the tree.  The root node of the tree is created at
     * this point.
     */
    public TreeNavigation() {
        // build root node so that children can be attached
        PageContentBean rootObject = new PageContentBean();
        rootObject
                .setMenuDisplayText("menuDisplayText.componentSuiteMenuGroup");
        rootObject.setMenuContentTitle(
                "submenuContentTitle.componentSuiteMenuGroup");
        rootObject.setMenuContentInclusionFile("./content/splashComponents.jspx");
        rootObject.setTemplateName("splashComponentsPanel");
        rootObject.setNavigationSelection(navigationBean);
        rootObject.setPageContent(true);
        rootTreeNode = new DefaultMutableTreeNode(rootObject);
        rootObject.setWrapper(rootTreeNode);

        model = new DefaultTreeModel(rootTreeNode);

        // StyleBean binding
        Application application =
                FacesContext.getCurrentInstance().getApplication();
        StyleBean styleBean =
                ((StyleBean) application.createValueBinding("#{styleBean}").
                        getValue(FacesContext.getCurrentInstance()));

        // provide StyleBean with reference to the tree navigation for folder icon updates
        styleBean.registerTree(this);

        // xp folders (default theme)
        themeBranchContractedIcon = StyleBean.XP_BRANCH_CONTRACTED_ICON;
        themeBranchExpandedIcon = StyleBean.XP_BRANCH_EXPANDED_ICON;
    }

    /**
     * Iterates over each node in the navigation tree and sets the icon based on
     * the current theme. This is necessary for the change to register with the
     * component.
     *
     * @param currentStyle the theme on which the folder icons are based
     */
    public void refreshIcons(String currentStyle) {

        // set the folder icon based on the StyleBean theme
        if (currentStyle.equals("xp")) {
            themeBranchContractedIcon = StyleBean.XP_BRANCH_CONTRACTED_ICON;
            themeBranchExpandedIcon = StyleBean.XP_BRANCH_EXPANDED_ICON;
        } else if (currentStyle.equals("royale")) {
            themeBranchContractedIcon = StyleBean.ROYALE_BRANCH_CONTRACTED_ICON;
            themeBranchExpandedIcon = StyleBean.ROYALE_BRANCH_EXPANDED_ICON;
        }
        // invalid theme
        else {
            return;
        }

        // get each tree node for iteration
        Enumeration enumTree = rootTreeNode.depthFirstEnumeration();
        PageContentBean temp = null;

        // set the icon on each tree node
        while (enumTree.hasMoreElements()) {
            temp = ((PageContentBean) ((DefaultMutableTreeNode) enumTree
                    .nextElement()).getUserObject());

            if (temp != null) {
                temp.setBranchContractedIcon(themeBranchContractedIcon);
                temp.setBranchExpandedIcon(themeBranchExpandedIcon);
            }
        }
    }

    /**
     * Utility method to build the entire navigation tree.
     */
    private void init() {
        // set init flag
        isInitiated = true;

        if (rootTreeNode != null) {

            // get the navigation bean from the faces context
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Object navigationObject =
                    facesContext.getApplication()
                            .createValueBinding("#{navigation}")
                            .getValue(facesContext);


            if (navigationObject != null &&
                navigationObject instanceof NavigationBean) {

                // set bean callback for root
                PageContentBean branchObject =
                        (PageContentBean) rootTreeNode.getUserObject();

                // assign the initialized navigation bean, so that we can enable panel navigation
                navigationBean = (NavigationBean) navigationObject;

                // set this node as the default page to view
                navigationBean.setSelectedPanel(
                        (PageContentBean) rootTreeNode.getUserObject());
                branchObject.setNavigationSelection(navigationBean);

                /**
                 * Generate the backing bean for each tree node and put it all together
                 */

                // theme menu item
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.themesSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.themesSubmenuItem");
                branchObject.setMenuContentInclusionFile("./content/splashThemes.jspx");
                branchObject.setTemplateName("splashThemesPanel");
                branchObject.setNavigationSelection(navigationBean);
                DefaultMutableTreeNode branchNode =
                        new DefaultMutableTreeNode(branchObject);
                branchObject.setLeaf(
                        true); // leaf nodes must be explicitly set - support lazy loading
                branchObject.setWrapper(branchNode);
                // finally add the new custom component branch
                rootTreeNode.add(branchNode);

                // Component menu item
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "menuDisplayText.componentsMenuGroup");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.componentsMenuGroup");
                branchObject.setTemplateName("splashComponentsPanel");
                branchObject.setNavigationSelection(navigationBean);
                branchObject.setPageContent(false);
                branchNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(branchNode);
                // finally add the new custom component branch
                rootTreeNode.add(branchNode);

                // component menu -> Text Entry
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.textFieldsSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.textFieldsSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/textFields.jspx");
                branchObject.setTemplateName("textFieldsContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                DefaultMutableTreeNode leafNode =
                        new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Selection
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.selectionTagsSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.selectionTagsSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/selectionTags.jspx");
                branchObject.setTemplateName("selectionTagsContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Button & Links
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.buttonsAndLinksSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.buttonsAndLinksSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/buttonsAndLinks.jspx");
                branchObject.setTemplateName("buttonsAndLinksContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Auto Complete
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.autoCompleteSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.autoCompleteSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/autoComplete.jspx");
                branchObject.setTemplateName("autoCompleteContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Drag and Drop
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.dragDropSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.dragDropSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/dragDrop.jspx");
                branchObject.setTemplateName("dragDropContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Calendar
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.selectInputDateComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.selectInputDateComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/selectInputDate.jspx");
                branchObject.setTemplateName("selectInputDateContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Tree
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.treeComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.treeComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/tree.jspx");
                branchObject.setTemplateName("treeContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> MenuBar
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.menuBarSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.menuBarSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/menuBar.jspx");
                branchObject.setTemplateName("menuBarContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                //component menu -> Effects
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.effectsSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.effectsSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/effects.jspx");
                branchObject.setTemplateName("effectsContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                //component menu -> Connection Status
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.connectionStatusSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.connectionStatusSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/connectionStatus.jspx");
                branchObject.setTemplateName("connectionStatusContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Table
                branchObject = new PageContentBean();
                branchObject.setExpanded(false);
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.tableComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.tableComponentSubmenuItem");
                branchObject.setTemplateName("splashTablesPanel");
                branchObject.setNavigationSelection(navigationBean);
                branchObject.setPageContent(false);
                DefaultMutableTreeNode branchNode2 =
                        new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(branchNode2);
                // finally add the new custom component branch
                branchNode.add(branchNode2);

                // component menu -> Table -> Table
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.tableComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.tableComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/table.jspx");
                branchObject.setTemplateName("tableContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Table -> Columns
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.columnsComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.columnsComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/tableColumns.jspx");
                branchObject.setTemplateName("columnsContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Table -> Sortable Header
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.dataSortHeaderComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.dataSortHeaderComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/commandSortHeader.jspx");
                branchObject.setTemplateName("commandSortHeaderContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Table -> Data Header
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.dataScrollerComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.dataScrollerComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/dataPaginator.jspx");
                branchObject.setTemplateName("tablePaginatorContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Table -> TableExpandable
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.tableExpandableComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.tableExpandableComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/tableExpandable.jspx");
                branchObject.setTemplateName("tableExpandableContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Table -> TableRowSelec5tion
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.tableRowSelectionComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.tableRowSelectionComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/tableRowSelection.jspx");
                branchObject.setTemplateName("tableRowSelectionContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode2.add(leafNode);

                // component menu -> Progress Indicator
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.outputProgressComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.outputProgressComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/outputProgress.jspx");
                branchObject.setTemplateName("outputProgressContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> File Upload
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.inputFileComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.inputFileComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/inputFile.jspx");
                branchObject.setTemplateName("inputFileContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Chart
                branchObject = new PageContentBean();
                branchObject.setExpanded(false);
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.chartComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.chartComponentSubmenuItem");
                branchObject.setTemplateName("splashChartsPanel");
                branchObject.setNavigationSelection(navigationBean);
                branchObject.setPageContent(false);
                DefaultMutableTreeNode branchNode3 =
                        new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(branchNode3);
                // finally add the new custom component branch
                branchNode.add(branchNode3);

                // component menu -> Chart -> Chart
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.chartComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.chartComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/outputChart/chart.jspx");
                branchObject.setTemplateName("chartPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode3.add(leafNode);

                // component menu -> Chart -> Dynamic Chart
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.dynamicChartComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.dynamicChartComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/outputChart/dynamicChart.jspx");
                branchObject.setTemplateName("dynamicChartPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode3.add(leafNode);

                // component menu -> Chart -> Combined Chart
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.combinedChartComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.combinedChartComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./components/outputChart/combinedChart.jspx");
                branchObject.setTemplateName("combinedChartPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode3.add(leafNode);

                // Layout Panels menu item
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.layoutPanelMenuGroup");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.layoutPanelMenuGroup");
                branchObject.setTemplateName("splashLayoutsPanelsPanel");
                branchObject.setNavigationSelection(navigationBean);
                branchObject.setPageContent(false);
                branchNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(branchNode);
                // finally add the new custom component branch
                rootTreeNode.add(branchNode);

                // Layout Panels menu -> Border Panel
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.borderLayoutComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.borderLayoutComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/panelBorder.jspx");
                branchObject.setTemplateName("panelBorderContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // Layout Panels menu -> Panel stack
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.panelStackComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.panelStackComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/panelStack.jspx");
                branchObject.setTemplateName("panelStackContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // Layout Panels menu -> Tab Set Panel
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.tabbedComponentSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.tabbedComponentSubmenuItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/panelTabSet.jspx");
                branchObject.setTemplateName("tabbedPaneContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                //component menu -> Popup Panel
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.panelPopupSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.panelPopupSubmenuItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/panelPopup.jspx");
                branchObject.setTemplateName("panelPopupContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Panel Series
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.listSubmenuItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.listSubmenuItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/panelSeries.jspx");
                branchObject.setTemplateName("listContentPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Panel Series
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.positionedPanelItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.positionedPanelItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/positionedPanel.jspx");
                branchObject.setTemplateName("positionPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);

                // component menu -> Accordion Series
                branchObject = new PageContentBean();
                branchObject.setMenuDisplayText(
                        "submenuDisplayText.accordionPanelItem");
                branchObject.setMenuContentTitle(
                        "submenuContentTitle.accordionPanelItem");
                branchObject.setMenuContentInclusionFile("./layoutPanels/accordionPanel-facelets.jspx");
                branchObject.setTemplateName("accordionPanel");
                branchObject.setNavigationSelection(navigationBean);
                leafNode = new DefaultMutableTreeNode(branchObject);
                branchObject.setWrapper(leafNode);
                branchObject.setLeaf(true);
                // finally add the new custom component branch
                branchNode.add(leafNode);
            }

        }

    }

    /**
     * Gets the default tree model.  This model is needed for the value
     * attribute of the tree component.
     *
     * @return default tree model used by the navigation tree
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * Sets the default tree model.
     *
     * @param model new default tree model
     */
    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

    /**
     * Gets the tree component binding.
     *
     * @return tree component binding
     */
    public Tree getTreeComponent() {
        return treeComponent;
    }

    /**
     * Sets the tree component binding.
     *
     * @param treeComponent tree component to bind to
     */
    public void setTreeComponent(Tree treeComponent) {
        this.treeComponent = treeComponent;
        if (!isInitiated) {
            init();
        }
    }
}