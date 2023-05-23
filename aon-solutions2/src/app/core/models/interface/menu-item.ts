export interface MenuItem { // Un interrogante en un parámetro indica que este es opcional
    root: boolean; // True si se encuentra en el menú principal o false si forma parte de un submenú
    text: string; // Campo de texto que se mostrará en el elemento del menú desplegable
    icon?: string; // Icono que complementa al texto en el caso de que sea necesario
    colorIcon?: string; // Color del icono anterior
    routerlink?: string; // Recibe una ruta a la que navegar en caso de click
    click?: any; // Recibe una función lambda que debe encontrarse en el componente padre desde donde se llame a este componente
    children?: MenuItem[]; // En caso de tener submenú se anida aqui de forma recursiva la misma estructura
}