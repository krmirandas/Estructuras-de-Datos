package mx.unam.ciencias.edd;

/**
 * Clase para manipular arreglos genéricos de elementos comparables.
 */
public class Arreglos {

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param a un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void quickSort(T[] a) {
        quickSort(a, 0, a.length-1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] a, int ini, int fin) {
        if(fin <= ini) return;
        //Recordando que el pivote, puede ser una posicion al azar, se toma el pivote al inicio.
        int i = ini + 1; 
        int j = fin;
        while (i < j)
            if (a[i].compareTo(a[ini]) > 0 && a[j].compareTo(a[ini]) <= 0)
                intercambia(a, i++, j--);
            else if (a[i].compareTo(a[ini]) <= 0)
                i++;
            else
                j--;
        if(a[i].compareTo(a[ini]) > 0)
            i--;
        intercambia(a, i, ini);
        quickSort(a, ini, i-1);
        quickSort(a, i+1, fin);
    }

    private static <T extends Comparable<T>> void intercambia(T[] a, int i, int j){
        if(i == j) return;
        T aux = a[i];
        a[i] = a[j];
        a[j] = aux;
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param a un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void selectionSort(T[] a) {
    	int min = -1;
        for (int i = 0; i < a.length-1; i++){
        	min = i;
            for (int j = i+1; j < a.length; j++)
                if (a[j].compareTo(a[min]) < 0)
					min = j;
            intercambia(a, i, min);
        }
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param a el arreglo dónde buscar.
     * @param e el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int busquedaBinaria(T[] a, T e) {
        return busquedaBinaria(a, e, 0, a.length-1);
    }

    public static <T extends Comparable<T>> int busquedaBinaria(T[] a, T e, int ini, int fin) {
        //Caso base, se llega a que el inicio y el final, son iguales, se comparar si son iguales.
        if(ini == fin){
            if(e.compareTo(a[ini]) == 0)
                return ini;
            return -1;
        }
        //Se obtiene la la posicion de la mitad del arreglo 
        int mitad = (int) (ini + (fin-ini)/ 2);
        //Si es menor busca del intervalo del incio hasta la mitad (Recordando que la "mitad") se le resta 1.
        if(e.compareTo(a[mitad]) <=  0)
            return busquedaBinaria(a, e, ini, mitad);
        return busquedaBinaria(a, e, mitad+1, fin);
    }
}
