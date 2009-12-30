/**
 * 
 */
package es.code.ecm.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 05/12/2007
 *
 */
public class RepositoryInfo {

	private Properties props;
	private List<String> workspaces = new ArrayList<String>();

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * Returns a list of workspaces.
	 * 
	 * @return
	 */
	public List<String> getWorkspaces() {
		return workspaces;
	}

	/**
	 * Assigns the list of workspaces.
	 * 
	 * @param workspaces
	 */
	public void setWorkspaces(List<String> workspaces) {
		this.workspaces = workspaces;
	}

	/**
	 * Adds a new workspace identifier to the list.
	 * 
	 * @param workspaceId
	 */
	public void addWorkspace(String workspaceId) {
		this.workspaces.add(workspaceId);
	}

}
