function updateEditorContent( sTextAreaName ) {
	try {
		FCKeditorAPI.GetInstance(sTextAreaName).UpdateLinkedField();
	} catch ( e ) {
	}
}	