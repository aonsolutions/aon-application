var treeOffset = 0;

function storeTreeOffset() {
	treeOffset = jQuery('.treeLeftContent').scrollTop();
}

function goToTreeOffset() {
	var activeTop = jQuery('.aon-tree-node-active').position().top;
	var activeHeight = jQuery('.aon-tree-node-active').outerHeight();
	var height = jQuery('.treeLeftContent').innerHeight();
	var offset = activeTop+activeHeight-height+150;
	if ( offset > treeOffset ) {
		jQuery('.treeLeftContent').scrollTop(offset);
	} else {
		jQuery('.treeLeftContent').scrollTop(treeOffset);
	}
}