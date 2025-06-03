let manualOverride  = localStorage.getItem('theme-mode'); // 'light' | 'dark' | 'auto' | null
const darkQuery     = window.matchMedia('(prefers-color-scheme: dark)');
const root          = document.documentElement;
const fakeHost      = 'b72384936-ayudat.aonsolutions.org'; // sólo en local

const themes = [
  {
    hostnameIncludes: ['ayudat.aon.solutions'],
    themeLight      : 'theme-ayudat',
    themeDark       : 'theme-ayudat-dark'
  },
  {
    hostnameIncludes: ['infoautonomos.aon.solutions', fakeHost],
    themeLight      : 'theme-infoautonomos',
    themeDark       : 'theme-infoautonomos-dark'
  },
  {
    hostnameIncludes: ['openges.aon.solutions'],
    themeLight      : 'theme-openges',
    themeDark       : 'theme-openges-dark'
  },
  {
    hostnameIncludes: ['etl.aon.solutions'],
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

const applyFavicon = (themeClass) => {
  const raw = getComputedStyle(document.body).getPropertyValue(`--favicon-${themeClass}`).trim();
  if (!raw) return;
  const url = raw.replace(/^url\((['"]?)(.*?)\1\)$/, '$2');
  if (!url) return;

  let link = document.querySelector("link[rel~='icon']");
  if (!link) {
    link = document.createElement('link');
    link.rel = 'icon';
    document.head.appendChild(link);
  }
  link.href = `${url}?v=${Date.now()}`;
};

export const applyTitle = (themeClass) => {
  let title = getComputedStyle(document.body).getPropertyValue(`--title-${themeClass}`).trim();
  if (title) {
    document.title = title;
  }
};

const getEffectiveMode = () => {
  if (manualOverride === 'light') return false;
  if (manualOverride === 'dark') return true;
  return darkQuery.matches; // automático
};

const applyTheme = () => {
  const isDark = getEffectiveMode();
  const themeClass = getThemeClass(isDark);
  clearThemeClasses();
  root.classList.add(themeClass);
  applyFavicon(themeClass);
  applyTitle(themeClass);
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
};

export {
  loadTheme,
  applyTheme,
  setupThemeToggleButtons,
  highlightActiveTheme
};
