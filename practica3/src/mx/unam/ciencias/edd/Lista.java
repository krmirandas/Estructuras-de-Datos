package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas implementan la interfaz {@link Iterable}, y por lo tanto se
 * pueden recorrer usando la estructura de control <em>for-each</em>. Las listas
 * no aceptan a <code>null</code> como elemento.</p>
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase Nodo privada para uso interno de la clase Lista. */
    private class Nodo {
        public T elemento;
        public Nodo anterior;
        public Nodo siguiente;

        public Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase Iterador privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        public Lista<T>.Nodo anterior;
        public Lista<T>.Nodo siguiente;

        public Iterador() {
            start();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            if (siguiente == null) throw new NoSuchElementException();
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            if (anterior == null) throw new NoSuchElementException();
            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            anterior = null;
            siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            anterior = rabo;
            siguiente = null;
        }

        /* No implementamos este método. */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    public int getElementos() {
        return getLongitud();
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    public boolean esVacio() {
        return longitud < 1;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. Después de llamar este
     * método, el iterador apunta a la cabeza de la lista. El método es idéntico
     * a {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agrega(T elemento) {
        agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último. Después de llamar este
     * método, el iterador apunta a la cabeza de la lista.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        if(elemento == null) throw new IllegalArgumentException();
        
        Nodo nodoAux = new Nodo(elemento);
        if(longitud < 1){
            rabo = cabeza = nodoAux;
        }else{
            rabo.siguiente = nodoAux;
            nodoAux.anterior = rabo;
            rabo = nodoAux;
        }
        longitud++;
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último. Después de llamar este
     * método, el iterador apunta a la cabeza de la lista.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if(elemento == null) throw new IllegalArgumentException();
        
        Nodo nodoAux = new Nodo(elemento);
        if(longitud < 1){
            cabeza = rabo = nodoAux;
        }else{
            cabeza.anterior = nodoAux;
            nodoAux.siguiente = cabeza;
            cabeza = nodoAux; 
        }
        longitud++;
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica. Si un elemento de la lista es
     * modificado, el iterador se mueve al primer elemento.
     * @param elemento el elemento a eliminar.
     */
    public void elimina(T elemento) {
        Nodo nodo = buscaNodo(cabeza, elemento);
        if(nodo == null){
            return;
        } else if(cabeza == rabo){
            cabeza = rabo = null;
        } else if(cabeza == nodo){
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
        } else if(rabo == nodo){
            rabo = rabo.anterior;
            rabo.siguiente = null;
        } else {
            nodo.siguiente.anterior = nodo.anterior;
            nodo.anterior.siguiente = nodo.siguiente; 
        }
        longitud--;
    }

    private Nodo buscaNodo(Nodo a, T elemento) {
        if(a == null) return null;
        if(a.elemento.equals(elemento)) return a;
        return buscaNodo(a.siguiente, elemento);
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if(longitud < 1) throw new NoSuchElementException();
        
        T eAux = cabeza.elemento;
        
        if(longitud == 1){
            cabeza = rabo = null;
        } else {
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
        }
        longitud--;
        return eAux;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if(rabo == null) throw new NoSuchElementException();
        
        T eAux = rabo.elemento;

        if(longitud == 1){
            rabo = cabeza = null;
        } else{
            rabo = rabo.anterior;
            rabo.siguiente = null;
        }

        longitud--;
        return eAux;
    }

    /**
     * Nos dice si un elemento está en la lista. El iterador no se mueve.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <tt>true</tt> si <tt>elemento</tt> está en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(T elemento) {
        return buscaNodo(cabeza, elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> listAux = new Lista<>();
        return reversa(listAux, cabeza);
    }

    private Lista<T> reversa(Lista<T> list, Nodo nodo){
        if(nodo == null) return list;
        list.agregaInicio(nodo.elemento);
        return reversa(list, nodo.siguiente);
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Lista<T> listAux = new Lista<>();
        return copia(listAux, cabeza);
    }

    /* Método auxiliar recursivo para copia. */
    private Lista<T> copia(Lista<T> list, Nodo nodo) {
        if(nodo == null) return list;
        list.agregaFinal(nodo.elemento);
        return copia(list, nodo.siguiente);
    }

    /**
     * Limpia la lista de elementos. El llamar este método es equivalente a
     * eliminar todos los elementos de la lista.
     */
    public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if(longitud < 1) throw new NoSuchElementException();
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if(longitud < 1) throw new NoSuchElementException();
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if(i < 0 || i >= getLongitud()) throw new ExcepcionIndiceInvalido();
        return get(i, cabeza, 0);        
    }

    private T get(int i, Nodo nodo, int j){
        if(i == j) return nodo.elemento;
        return get(i, nodo.siguiente, j+1);
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si
     *         el elemento no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        return indiceDe(0, cabeza, elemento);
    }

    private int indiceDe(int i, Nodo nodo, T elemento) {
        if(nodo == null) return -1;
        if(nodo.elemento.equals(elemento)) return i;

        return indiceDe(i+1, nodo.siguiente, elemento);
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param l la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>> Lista<T> mergeSort(Lista<T> l) {
        if(l.cabeza == l.rabo) return l.copia();
        Lista<T> li = new Lista<>();
        Lista<T> ld = new Lista<>();
        int c = 0;
        for (T el : l) {
            //Se hace una REFERENCIA a la lista deseada para agregar elementos. 
            Lista<T> ll = (c++ < l.getLongitud()/2) ? li : ld;
            ll.agrega(el);
        }
        return merge(mergeSort(li), mergeSort(ld));
    }


    private static <T extends Comparable<T>> Lista<T> merge(Lista<T> ld, Lista<T> li) {
        Lista<T>.Nodo ni = li.cabeza;
        Lista<T>.Nodo nd = ld.cabeza;
        Lista<T> l = new Lista<>();
        
        while(ni != null && nd != null)
            if(ni.elemento.compareTo(nd.elemento) < 0){
                l.agrega(ni.elemento);
                ni = ni.siguiente;
            }else{
                l.agrega(nd.elemento);
                nd = nd.siguiente;
            }
        //En el caso de que en una lista ya no haya elementos, se agregan los
        //restantes de la otra lista la lista principal.
        Lista<T>.Nodo n = ni != null ? ni : nd;
        while(n != null){
            l.agrega(n.elemento);
            n = n.siguiente;
        }
        return l;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param l la lista donde se buscará.
     * @param e el elemento a buscar.
     * @return <tt>true</tt> si e está contenido en la lista,
     *         <tt>false</tt> en otro caso.
     */
    public static <T extends Comparable<T>> boolean busquedaLineal(Lista<T> l, T e) {
        for (T el : l)
            if(el.compareTo(e) == 0) return true;
        return false;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        StringBuilder strBldr = new StringBuilder();
        strBldr.append("[");
        Nodo nodo = cabeza;
        while(nodo != null){
            strBldr.append(nodo.elemento.toString());
            nodo = nodo.siguiente;
            if(nodo != null) strBldr.append(", ");
        }
        strBldr.append("]");
        return strBldr.toString();
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param o el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la lista es igual al objeto recibido;
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object o) {
        if (!(o instanceof Lista)) return false;

        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)o;
        if(lista.getLongitud() != longitud) return false;
        
        Nodo a1 = cabeza;
        Nodo a2 = lista.cabeza;
        while(a1 != null && a2 != null){
            if(!a1.elemento.equals(a2.elemento)) return false;
            a1 = a1.siguiente;
            a2 = a2.siguiente;
        }
        return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }
}