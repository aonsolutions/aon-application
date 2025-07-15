
export const loadCustomView = (customCssUrl) => {
  return new Promise((resolve, reject) => {
      try {
          loadLink(customCssUrl, 'stylesheet', 'text/css');
          let tries = 0;
          let interval = setInterval(() => {
              if ( tries++ > 5 ) {
                  resolve();
                  clearInterval(interval);
              }
          }, 200);

      } catch ( err ) {
          reject(new Error(`Something was wrong with theme '${customCssUrl}'`));
      }
  });
};

export const favicon = () => {
	let favicon = getComputedStyle(document.body).getPropertyValue('--favicon');
	if ( favicon ) {
		loadLink(favicon, 'icon', 'image/x-icon')
		.then( faviconLink  => {
            favicon += `?v=${Date.now()}`;
			faviconLink.href = favicon;
		});
	}
};

export const title = () => {
	let title = getComputedStyle(document.body).getPropertyValue('--title').trim();
	if (title) {
		document.title = title;
	}
};

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
};
