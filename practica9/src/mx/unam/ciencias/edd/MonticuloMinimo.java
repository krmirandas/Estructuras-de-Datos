package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>). Podemos crear un montículo
 * mínimo con <em>n</em> elementos en tiempo <em>O</em>(<em>n</em>), y podemos
 * agregar y actualizar elementos en tiempo <em>O</em>(log <em>n</em>). Eliminar
 * el elemento mínimo también nos toma tiempo <em>O</em>(log <em>n</em>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T> {

    /* Clase privada para iteradores de montículos. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice < siguiente && arbol[indice] != null;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if (hasNext())
                return arbol[indice++];
            throw new NoSuchElementException();
        }

        /* No lo implementamos: siempre lanza una excepción. */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* El siguiente índice dónde agregar un elemento. */
    private int siguiente;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] creaArregloGenerico(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Lista)}, pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = creaArregloGenerico(1);
    }

    /**
     * Constructor para montículo mínimo que recibe una lista. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param lista la lista a partir de la cuál queremos construir el
     *              montículo.
     */
    public MonticuloMinimo(Lista<T> lista) {
        siguiente = lista.getElementos();
        arbol = creaArregloGenerico(lista.getElementos());
        int i = 0;
        for (T e : lista) {
            arbol[i] = e;
            arbol[i].setIndice(i);
            ++i;
        }
        for (int j = (siguiente - 1) / 2; j >= 0; j--) {
            minHeapify(j);
        }
    }
    private void minHeapify(int i) {
        int izq = i * 2 + 1;
        int der = i * 2 + 2;

        if (izq >= siguiente && der >= siguiente)
            return;

        int menor = getMenor(izq, der);
        menor = getMenor(i, menor);

        if (menor != i) {
            T aux = arbol[i];

            arbol[i] = arbol[menor];
            arbol[i].setIndice(i);

            arbol[menor] = aux;
            arbol[menor].setIndice(menor);

            minHeapify(menor);
        }
    }

    private int getMenor(int a, int b) {
        if (b >= siguiente)
            return a;
        else if (arbol[a].compareTo(arbol[b]) < 0)
            return a;
        else
            return b;
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
        if (siguiente >= arbol.length) {
            T[] nArbol = creaArregloGenerico(arbol.length * 2);
            for (int i = 0; i < arbol.length; i++) {
                nArbol[i] = arbol[i];
            }
            arbol = nArbol;
        }
        arbol[siguiente] = elemento;
        arbol[siguiente].setIndice(siguiente);

        recorreParaArriba(siguiente++);
    }

    private void recorreParaArriba(int i) {
        int padre = (i - 1) / 2;
        int menor = i;

        if (padre >= 0 && arbol[padre].compareTo(arbol[i]) > 0)
            menor = padre;

        if (menor != i) {
            T aux = arbol[i];

            arbol[i] = arbol[padre];
            arbol[i].setIndice(i);

            arbol[padre] = aux;
            arbol[padre].setIndice(padre);

            recorreParaArriba(menor);
        }
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    public T elimina() {
        if (esVacio())
            throw new IllegalStateException();
        T e = arbol[0];
        //JAVA TRABAJA CON REFERENCIAS        
        //Se intercambia el ultimo con el que esta hasta arriba
        //Se hace minHeapify() ya que no hay padre y siempre es para abajo.
        intercambia(e, arbol[--siguiente]);
        arbol[siguiente].setIndice(-1);
        arbol[siguiente] = null;
        minHeapify(0);
        return e;
    }

    private void intercambia(T a, T b) {
        int i_a = a.getIndice();
        int i_b = b.getIndice();
        arbol[i_a] = b;
        arbol[i_b] = a;

        arbol[i_a].setIndice(i_a);
        arbol[i_b].setIndice(i_b);
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
        if (elemento == null)
            return;

        int indice = elemento.getIndice();
        //Se tomara el ultimo y ademas ahora el ultimo apuntara al ultimo elemento del arreglo
        //no al siguiente del ultimo.
        intercambia(arbol[indice], arbol[--siguiente]);

        arbol[siguiente].setIndice(-1);
        arbol[siguiente] = null;
        reordena(arbol[indice]);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (T e : arbol)
            if (e.equals(elemento))
                return true;;
        return false;
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <tt>true</tt> si ya no hay elementos en el montículo,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean esVacio() {
        return siguiente == 0;
    }

    /**
      * Reordena un elemento en el árbol.
      * @param elemento el elemento que hay que reordenar.
      */
    public void reordena(T elemento) {
        if (elemento == null)
            return ;
        int i = elemento.getIndice();
        recorreParaArriba(i);
        minHeapify(i);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return siguiente;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    public T get(int i) {
        if (i < 0 || i >= siguiente)
            throw new NoSuchElementException();
        return arbol[i];
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS
    .     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
