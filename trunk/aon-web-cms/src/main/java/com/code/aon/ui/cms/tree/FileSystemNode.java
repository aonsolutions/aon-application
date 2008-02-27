package com.code.aon.ui.cms.tree;

import java.io.File;
import java.io.FileFilter;

public class FileSystemNode {

    private String path;
    private static FileSystemNode[] CHILDREN_ABSENT = new FileSystemNode[0];
    private FileSystemNode[] children;
    private String shortPath;
    private FileSystemBean bean_;

    public FileSystemNode(String path, FileSystemBean bean_) {
        this.path = path.replace('\\', '/');
        int idx = this.path.lastIndexOf('/');
        if (idx != -1) {
            shortPath = this.path.substring(idx + 1);
        } else {
            shortPath = this.path;
        }
        this.bean_ = bean_;
    }

    public synchronized FileSystemNode[] getNodes() {
        if (children == null) {
    		File dir = new File(path);
	    	if (dir.isDirectory()) {
	    		File[] nodes = dir.listFiles(new FileFilter() {
	                public boolean accept(File path) { return path.isDirectory(); }
	            });
                children = new FileSystemNode[nodes.length];
                for (int i = 0; i < nodes.length; i++) {
                    String nodePath = nodes[i].toString();
                    if (nodePath.endsWith("/")) {
                        nodePath = nodePath.substring(0, nodePath.length() - 1);
                    }
                    children[i] = new FileSystemNode(nodePath, this.bean_);
                }
            } else {
                children = CHILDREN_ABSENT;
            }
        }
        return children;
    }

    public String toString() {
        return shortPath;
    }
    
    public String getPath() {
		return path;
	}

	public void onSelectFolder(){
		bean_.setSelected(this);
    }

}