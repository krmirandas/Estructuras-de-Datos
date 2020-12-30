package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase privada para iteradores de árboles binarios completos. */
    private class Iterador implements Iterator<T> {

        private Cola<ArbolBinario<T>.Vertice> cola;

        /* Constructor que recibe la raíz del árbol. */
        public Iterador() {
            cola = new Cola<>();
            if(raiz != null)
                cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el elemento siguiente. */
        @Override public T next() {
            //Se toma una referencia del elemento que esta en la cabeza de la cola.
            Vertice vertice = cola.saca();
            //Luego se iran metiendo.
            if (vertice.hayIzquierdo())
                cola.mete(vertice.izquierdo);
            if (vertice.hayDerecho())
                cola.mete(vertice.derecho);
            return vertice.get();
        }

        /* No lo implementamos: siempre lanza una excepción. */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();

        Vertice el = new Vertice(elemento);
        if (raiz == null)
            raiz = ultimoAgregado = el;
        else {
            //Se hace una referencia a la raiz.
            Vertice aux = raiz;
            Cola<ArbolBinario<T>.Vertice> cola = new Cola<>();
            //Metemos la referencia a la cola.
            cola.mete(aux);
            while (!cola.esVacia()) {
                aux = cola.saca();
                //Si el elemento que sacamos de la cola, osea aux, sus hijos
                //son nulos los pondremos como hijo izquierdo o derecho.
                if (!aux.hayIzquierdo() || !aux.hayDerecho()) {
                    el.padre = aux;
                    if (!aux.hayIzquierdo())
                        ultimoAgregado = aux.izquierdo = el;
                    else if (!aux.hayDerecho())
                        ultimoAgregado = aux.derecho = el;
                    //Decimos quien es el ultimo agregado.
                    break;
                }
                //Si no son nulos significa que sigue teniendo hijos, mete a
                //los vertices izquierdos y derechos.
                cola.mete(aux.izquierdo);
                cola.mete(aux.derecho);
            }//end while
        }
        elementos++;
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        if (elemento == null)
            return;
        //Variable booleana que me servira para indicar cuando el elemento se elimino.
        boolean eliminado = false;
        //Variable que apunta a la raiz
        Vertice vertice = raiz;
        //Auxiliar para tener el penultimo elemento, su funcion es que sobre
        //cada iteracion hace que apunte al vertice en turno, para de esta
        //forma en la ultima iteracion tener el penultimo elemento.
        Vertice nuevoUltimo = null;
        Cola<ArbolBinario<T>.Vertice> cola = new Cola<>();
        //Metemos la raiz a la cola
        cola.mete(vertice);
        while (!cola.esVacia()) {
            vertice = cola.saca();
            //Vemos si el elemento es igual, en caso de serlo solo
            //intercambiamos el contenido del actual con el ultimoAgregado
            if (vertice.get().equals(elemento) && !eliminado) {
                vertice.elemento = ultimoAgregado.elemento;
                eliminado = true;
                elementos--;
            }
            //Validacion para obtener el penultimo elemento en la penultima iteracion.
            if (vertice != ultimoAgregado)
                nuevoUltimo = vertice;
            //Si ya fue eliminado y va en la ultima iteracion.
            if (eliminado && vertice == ultimoAgregado) {
                //Elimina si hay un solo elemento.
                if (!vertice.hayPadre())
                    raiz = ultimoAgregado = null;
                else {
                    //Si el arbol tiene mas de un elemento.
                    ultimoAgregado = nuevoUltimo;
                    if (esHijoIzquierdo(vertice))
                        vertice.padre.izquierdo = null;
                    else
                        vertice.padre.derecho = null;
                }
            }
            //En el caso de que no lo haya encontrado, mete a sus hijos.
            //En la cola cuando de le pasa un elemento nulo solo hace un return.
            if(vertice.izquierdo != null)
                cola.mete(vertice.izquierdo);
            if(vertice.derecho != null)
                cola.mete(vertice.derecho);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
