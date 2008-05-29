function applyEditor( sBasePath, sTextAreaName, configPath, toolBar, height, width ) {
	var oFCKeditor = new FCKeditor( sTextAreaName ) ;
	oFCKeditor.BasePath = sBasePath;
	if ( configPath ) {
		oFCKeditor.Config['CustomConfigurationsPath']= configPath;
	}
	oFCKeditor.ToolbarSet= toolBar;
	if ( height ) {
		oFCKeditor.Height = height;
	}
	if ( width ) {
		oFCKeditor.Width = width;
	}
	oFCKeditor.ReplaceTextarea();
}

function updateEditorContent( sTextAreaName ) {
	try {
		FCKeditorAPI.GetInstance(sTextAreaName).UpdateLinkedField();
	} catch ( e ) {
	}
}	