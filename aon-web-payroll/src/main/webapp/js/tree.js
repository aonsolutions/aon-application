var treeOffset;

function storeTreeOffset() {
	treeOffset = jQuery('.treeLeftContent').scrollTop();
}

function goToTreeOffset() {
	jQuery('.treeLeftContent').scrollTop(treeOffset);
}