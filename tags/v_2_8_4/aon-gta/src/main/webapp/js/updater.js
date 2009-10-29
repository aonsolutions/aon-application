function doRewrite(){
	link = document.getElementById("actionElement");
	if ( link ) {
		finish = document.getElementById("status").value;
		if (finish=='0' || 
			finish=='1' ||
			finish=='2'){
			link.onclick();
		}
	}
}
