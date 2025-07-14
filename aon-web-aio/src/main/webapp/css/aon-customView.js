
export const loadCustomView = (customCssUrl) => {
	return loadLink(customCssUrl, 'stylesheet', 'text/css');
}

export const favicon = () => {
	let favicon = getComputedStyle(document.body).getPropertyValue('--favicon');
	if ( favicon ) {
		loadLink(favicon, 'icon', 'image/x-icon')
		.then( faviconLink  => {
			faviconLink.href = favicon;
		}).catch( err => {
			faviconLink.href = favicon; // Fallback if the link fails to load
		});
	}
}

export const title = () => {
	let title = getComputedStyle(document.body).getPropertyValue('--title').trim();
	if (title) {
		document.title = title;
	}
}

export const loadLink = (url, rel, type) => new Promise((resolve, reject) => {
    const link = document.createElement('link');
	link.onerror = reject;
	link.onload = () => resolve(link);
	link.href = url;
	link.rel = rel || "stylesheet";
	link.type = type || "text/css";

	document.head.appendChild(link);
	
	
		
});

export const loadImg = (src) => {
	return new Promise((resolve, reject) => {
        const img = document.createElement('img');
		img.onerror = reject;
        img.onload = () => resolve(img);
        img.src = src;
		document.body.appendChild(img);
    });
}