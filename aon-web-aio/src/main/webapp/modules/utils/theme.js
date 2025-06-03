let manualOverride  = localStorage.getItem('theme-mode'); // 'light' | 'dark' | 'auto' | null
const darkQuery     = window.matchMedia('(prefers-color-scheme: dark)');
const root          = document.documentElement;

const themes = [
  {
    hostnameIncludes: 'ayudat.aon.solutions',
    themeLight: 'theme-ayudat',
    themeDark: 'theme-ayudat-dark'
  },
  {
    hostnameIncludes: 'infoautonomo.aon.solutions',
    themeLight: 'theme-infoautonomo',
    themeDark: 'theme-infoautonomo-dark'
  }
];

const getThemeClass = (isDark) => {
  const hostname = window.location.hostname;
  for (const t of themes) {
    if (hostname.includes(t.hostnameIncludes)) {
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
