import { getCustomViewConfiguration, getCustomViewImage } from '../../services/customViewService';
import { AonElement } from '../../components/AonElement';

let manualOverride  = localStorage.getItem('theme-mode'); // 'light' | 'dark' | 'auto' | null
const darkQuery     = window.matchMedia('(prefers-color-scheme: dark)');
const root          = document.documentElement;
const fakeHost      = 'b72384936-ayudat.aonsolutions.org'; // s�lo en local

const themes = [
  {
    hostnameIncludes: ['-ayudat.', '/ayudat.', '-leevy', '/leevy', fakeHost],
    themeLight      : 'theme-ayudat',
    themeDark       : 'theme-ayudat-dark'
  },
  {
    hostnameIncludes: ['-infoautonomos.', '/infoautonomos.'],
    themeLight      : 'theme-infoautonomos',
    themeDark       : 'theme-infoautonomos-dark'
  },
  {
    hostnameIncludes: ['-openges.', '/openges.'],
    themeLight      : 'theme-openges',
    themeDark       : 'theme-openges-dark'
  },
  {
    hostnameIncludes: ['-etl.', '/etl.'],
    themeLight      : 'theme-etl',
    themeDark       : 'theme-etl-dark'
  }
];

const getThemeClass = (isDark) => {
  const hostname = window.location.hostname;
  for (const t of themes) {
    const match = Array.isArray(t.hostnameIncludes)
      ? t.hostnameIncludes.some(h => hostname.includes(h))
      : hostname.includes(t.hostnameIncludes);
    if (match) {
      return isDark ? t.themeDark : t.themeLight;
    }
  }
  return isDark ? 'theme-dark' : 'theme-light';
};


const clearThemeClasses = () => {
  [...root.classList].filter(cls => cls.startsWith('theme-')).forEach(cls => root.classList.remove(cls));
};

const applyFavicon = (themeClass, favicon = "") => {
  if (favicon === "") {
    // Intentar cogerlo del CSS si no viene en config
    const raw = getComputedStyle(document.body).getPropertyValue(`--favicon-${themeClass}`).trim();
    if (raw) favicon = raw.replace(/^url\((['"]?)(.*?)\1\)$/, '$2');
    if (favicon) {
      setFavicon(favicon);
    }
  } else {
    // Si viene del CustomView => usar getCustomViewImage
    getCustomViewImage(favicon).then(url => {
      setFavicon(url);
    });
  }
};

const setFavicon = (url) => {
  if (!url) return;
  let link = document.querySelector("link[rel~='icon']");
  if (!link) {
    link = document.createElement('link');
    link.rel = 'icon';
    document.head.appendChild(link);
  }
  link.href = `${url}?v=${Date.now()}`;
};


export const applyTitle = (themeClass, title = "") => {
  if (!title) {
    title = getComputedStyle(document.body).getPropertyValue(`--title-${themeClass}`).trim();
  }

  if (title) {
    document.title = title;
  }
};

export const applyLogoHeader = (themeClass, imageLogoHeader = "") => {
  waitForElement('#aon-logo').then((divLogo) => {  
    if (imageLogoHeader === "") {
      const headerLogo = (AonElement.isMobile() || AonElement.isMobileResolution())
        ? `--logoHeaderMobile-${themeClass}`
        : `--logoHeader-${themeClass}`;
      const raw = getComputedStyle(document.body).getPropertyValue(headerLogo).trim();
      if (raw) imageLogoHeader = raw.replace(/^url\((['"]?)(.*?)\1\)$/, '$2');
      if (imageLogoHeader && divLogo) { 
        divLogo.style.backgroundImage = 'url(' + imageLogoHeader + ')';
      }
    } else {
      getCustomViewImage(imageLogoHeader).then(url => {
        divLogo.style.backgroundImage = `url(${url})`;
      });
    }
  });
};

export const applyLogo = (themeClass, imageLogo = "") => {
  waitForElement('#logoSVG').then((divLogo) => {  
    if (imageLogo === "") {
      const raw = getComputedStyle(document.body).getPropertyValue(`--logo-${themeClass}`).trim();
      if (raw) imageLogo = raw.replace(/^url\((['"]?)(.*?)\1\)$/, '$2');
      if (imageLogo && divLogo) { 
        divLogo.style.backgroundImage = 'url(' + imageLogo + ')';
      }
    } else {
      getCustomViewImage(imageLogo).then(url => {
        divLogo.style.backgroundImage = `url(${url})`;
      });
    }
  });
};

const getEffectiveMode = () => {
  if (manualOverride === 'light') return false;
  if (manualOverride === 'dark') return true;
  return darkQuery.matches; // autom�tico
};

const applyTheme = () => {
  let favicon       = "";
  let title         = "";
  let logoHeader    = "";
  let logo          = "";
  const isDark      = getEffectiveMode();
  const themeClass  = getThemeClass(isDark);

  clearThemeClasses();
  root.classList.add(themeClass);
  getCustomViewConfiguration().then(res => {
    if(Object.keys(res).length > 0){
      favicon    = isDark ? res.images?.["favicon-darksvg"] : res.images?.["faviconsvg"];
      title      = res.params?.["AON_CUSTOMIZE_TITLE"];
      if (AonElement.isMobile() || AonElement.isMobileResolution()) {
        logoHeader = res.images?.["login-logo-dark"]
      } else {
        logoHeader = res.images?.["header-logo-dark"];
      }
      logo       = isDark ? res.images?.["login-logo-dark"] : res.images?.["aon-login-logo"];
    }
    // Aplicamos el estilo del Custom, si no tenemos el del sass 
    applyFavicon(themeClass, favicon);
    applyTitle(themeClass, title);
    applyLogoHeader(themeClass, logoHeader);
    applyLogo(themeClass, logo);
  }).catch(err => console.error("Error getCustomViewImage:", err));  
};

// Permite actualizar botones activos visualmente
const highlightActiveTheme = () => {
  const current = localStorage.getItem('theme-mode') || 'auto';
  document.querySelectorAll('[data-theme-mode]').forEach(btn => {
    btn.classList.toggle('active', btn.getAttribute('data-theme-mode') === current);
  });
};

const setupThemeToggleButtons = () => {
  document.querySelectorAll('[data-theme-mode]').forEach(btn => {
    btn.addEventListener('click', () => {
      const mode = btn.getAttribute('data-theme-mode');
      manualOverride = mode;
      localStorage.setItem('theme-mode', mode);
      applyTheme();
      highlightActiveTheme();
    });
  });

  highlightActiveTheme();
};

const loadTheme = async () => {
  applyTheme();

  darkQuery.addEventListener('change', () => {
    if (manualOverride === 'auto' || !manualOverride) {
      applyTheme();
    }
  });

  let lastIsMobile = AonElement.isMobile() || AonElement.isMobileResolution();

  window.addEventListener('resize', () => {
    const isMobileNow = AonElement.isMobile() || AonElement.isMobileResolution();
    if (isMobileNow !== lastIsMobile) {
      lastIsMobile = isMobileNow;
      applyTheme();
    }
  });
};

export {
  loadTheme,
  applyTheme,
  setupThemeToggleButtons,
  highlightActiveTheme
};
