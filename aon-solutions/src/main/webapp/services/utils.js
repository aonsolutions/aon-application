
export const isMobile = () => {
  return window.innerWidth <= 850 && window.innerHeight <= 850;
}

export const clearElement = (id) => {
  document.getElementById(id).innerHTML = '';
}
