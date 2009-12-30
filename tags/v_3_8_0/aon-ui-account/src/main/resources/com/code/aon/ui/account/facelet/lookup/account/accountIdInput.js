function validateOnChange(field) {
	if (field.value.indexOf(".") != -1) {
		a = field.value.split(".");
		if (a.length > 1 ) {
			p = a[0];
			s = a[1];
			le = p.length + s.length;
			for (i=le;i<12;i++) {
				p += "0";
			}
			field.value = p + s;
		}
	}
}

function activateSuggestionBox(field) {
	if (field.value.indexOf('.') > 0) return false;
	if (field.value.indexOf('*') > 0) return true;
	return false;
}
