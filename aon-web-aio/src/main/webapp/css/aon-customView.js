
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
  let favicon = getComputedStyle(document.body).getPropertyValue('--favicon').trim();
  if ( favicon ) {
    loadLink('', 'icon', 'image/x-icon').then( faviconLink  => {
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
    document.head.appendChild(link);
    link.onload = resolve(link);
    link.onerror = reject;
    link.href = url;
    link.rel = rel || "stylesheet";
    link.type = type || "text/css";
});