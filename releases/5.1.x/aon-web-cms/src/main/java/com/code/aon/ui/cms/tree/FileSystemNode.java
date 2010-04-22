package com.code.aon.ui.cms.tree;

import java.io.File;
import java.io.FileFilter;

public class FileSystemNode {

    private static final FileSystemNode[] CHILDREN_ABSENT = new FileSystemNode[0];
	
    private File path;
    private FileSystemNode[] children;
    private FileSystemBean bean_;

    public FileSystemNode(File path, FileSystemBean bean_) {
    	this.path = path;
        this.bean_ = bean_;
    }

    public synchronized FileSystemNode[] getNodes() {
        if (children == null) {
	    	if (path.isDirectory()) {
	    		File[] nodes = path.listFiles(new FileFilter() {
	                public boolean accept(File path) { return path.isDirectory(); }
	            });
                children = new FileSystemNode[nodes.length];
                for (int i = 0; i < nodes.length; i++) {
                    children[i] = new FileSystemNode(nodes[i], this.bean_);
                }
            } else {
                children = CHILDREN_ABSENT;
            }
        }
        return children;
    }

    public File getPath() {
		return path;
	}

	public void onSelectFolder(){
		bean_.setSelected( getPath() );
    }

	@Override
	public String toString() {
		return this.path.getName();
	}

}