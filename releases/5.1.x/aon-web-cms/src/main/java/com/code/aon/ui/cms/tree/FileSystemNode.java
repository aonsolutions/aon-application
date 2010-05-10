package com.code.aon.ui.cms.tree;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class FileSystemNode {

    private static final FileSystemNode[] CHILDREN_ABSENT = new FileSystemNode[0];
    
    private static Comparator<FileSystemNode> COMPARATOR = new Comparator<FileSystemNode>() {
    	
    	public int compare(FileSystemNode fsn1, FileSystemNode fsn2) {
    		String name1 = fsn1.getPath().getName();
    		String name2 = fsn2.getPath().getName();
    		return name1.compareToIgnoreCase(name2);
    	}    	
    	
	};
	
    private File path;
    private FileSystemNode[] children;

    public FileSystemNode( File path ) {
    	this.path = path;
    }

    public synchronized FileSystemNode[] getNodes() {
        if (children == null) {
	    	if (path.isDirectory()) {
	    		File[] nodes = path.listFiles(new FileFilter() {
	                public boolean accept(File path) { return path.isDirectory(); }
	            });
                children = new FileSystemNode[nodes.length];
                for (int i = 0; i < nodes.length; i++) {
                    children[i] = new FileSystemNode( nodes[i] );
                }
                Arrays.sort(children, COMPARATOR);
            } else {
                children = CHILDREN_ABSENT;
            }
        }
        return children;
    }

    public File getPath() {
		return path;
	}

	@Override
	public String toString() {
		return this.path.getName();
	}

}