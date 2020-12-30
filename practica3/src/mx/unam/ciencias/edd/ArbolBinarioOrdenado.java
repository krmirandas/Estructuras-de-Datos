package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>> extends ArbolBinario<T> {

    /* Clase privada para iteradores de árboles binarios ordenados. */
    private class Iterador implements Iterator<T> {

        /* Pila para emular la pila de ejecución. */
        private Pila<ArbolBinario<T>.Vertice> pila;

        /* Construye un iterador con el vértice recibido. */
        public Iterador() {
            start();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return !pila.esVacia();

        }
        /* Regresa el siguiente elemento del árbol en orden. */
        @Override public T next() {
            Vertice vertice = pila.saca();
            T e = vertice.get();
            vertice = vertice.derecho;
            while (vertice != null) {
                pila.mete(vertice);
                vertice = vertice.izquierdo;
            }
            return e;
        }

        private void start(){
            pila = new Pila<>();
            pila.mete(raiz);
            Vertice vertice = raiz;
            while (vertice.hayIzquierdo()) {
                pila.mete(vertice.izquierdo);
                vertice = vertice.izquierdo;
            }
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
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (raiz == null)
            raiz = ultimoAgregado = nuevoVertice(elemento);
        else
            agrega(raiz, elemento);
        elementos++;
    }

    private void agrega(Vertice vertice, T elemento) {
        if (elemento.compareTo(vertice.get()) < 0)
            if (!vertice.hayIzquierdo()) {
                Vertice verticeNuevo = nuevoVertice(elemento);
                verticeNuevo.padre = vertice;
                vertice.izquierdo = ultimoAgregado = verticeNuevo;
            } else
                agrega(vertice.izquierdo, elemento);
        else
            if (!vertice.hayDerecho()) {
                Vertice verticeNuevo = nuevoVertice(elemento);
                verticeNuevo.padre = vertice;
                vertice.derecho = ultimoAgregado = verticeNuevo;
            } else
                agrega(vertice.derecho, elemento);
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice vertice = busca(raiz, elemento);

        if (vertice == null)
            return;
        //Cuando es una arbol que tiene izquierda, me tomo el maximo subarbol
        //izquierdo para intercambiar los contenidos con el maximo y el
        //elemento a eliminar, para no lidiar con las referencias. Y de esta
        //forma cuando el vertice que quiero eliminar o es hoja o es esta en un "chorizo".
        if (vertice.hayIzquierdo()) {
            //Vertice auxiliar que apunte a el elemento a eliminar.
            Vertice aux = vertice;
            //Vertice a eliminar igual al maximo subarbol.
            vertice = maximoEnSubarbol(vertice.izquierdo);
            //Intercambio elementos
            aux.elemento = vertice.elemento;
        }
        //Este caso contempla cuando es hoja o raiz sin hijos.
        if (esHoja(vertice))
            //Si es la raiz, pone todo en null.
            if (vertice == raiz)
                raiz = ultimoAgregado = null;
        //En otro caso solomante corta la conexion con las hojas.
            else if (esHijoIzquierdo(vertice))
                vertice.padre.izquierdo = vertice.padre = null;
            else
                vertice.padre.derecho = vertice.padre = null;
        //En el caso de que es todo un "chorizo" con hijo(s) izquierdos.
        else if (vertice.hayIzquierdo())
            //En este caso solamente sube y elimina el elemento que esta en la raiz.
            if (vertice == raiz) {
                raiz = vertice.izquierdo;
                raiz.padre = null;
            } else {
                //El que se quiere elimiar esta entre vertices.
                vertice.izquierdo.padre = vertice.padre;
                if (esHijoIzquierdo(vertice))
                    vertice.padre.izquierdo = vertice.izquierdo;
                else
                    vertice.padre.derecho = vertice.izquierdo;
            }
        //En el caso de que se todo un "chorizo" con hijo(s) derechos.
        else
            //En este caso solamente sube y elimina el elemento que esta en la raiz.
            if (vertice == raiz) {
                raiz = raiz.derecho;
                raiz.padre = null;
            } else {
                //El que se quiere elimiar esta entre vertices.
                vertice.derecho.padre = vertice.padre;
                if (esHijoIzquierdo(vertice))
                    vertice.padre.izquierdo = vertice.derecho;
                else
                    vertice.padre.derecho = vertice.derecho;
            }
        elementos--;
    }

    /**
     * Busca recursivamente un elemento, a partir del vértice recibido.
     * @param vertice el vértice a partir del cuál comenzar la búsqueda. Puede
     *                ser <code>null</code>.
     * @param elemento el elemento a buscar a partir del vértice.
     * @return el vértice que contiene el elemento a buscar, si se encuentra en
     *         el árbol; <code>null</code> en otro caso.
     */
    @Override protected Vertice busca(Vertice vertice, T elemento) {
        if (vertice == null || elemento == null)
            return null;
        if (elemento.compareTo(vertice.get()) == 0)
            return vertice;
        if (elemento.compareTo(vertice.get()) < 0)
            return busca(vertice.izquierdo, elemento);
        return busca(vertice.derecho, elemento);
    }

    /**
     * Regresa el vértice máximo en el subárbol cuya raíz es el vértice que
     * recibe.
     * @param vertice el vértice raíz del subárbol del que queremos encontrar el
     *                máximo.
     * @return el vértice máximo el subárbol cuya raíz es el vértice que recibe.
     */
    protected Vertice maximoEnSubarbol(Vertice vertice) {
        while (vertice.hayDerecho())
            vertice = vertice.derecho;
        return vertice;
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> v) {
        if (v == null || !v.hayIzquierdo())
            return;

        Vertice vertice = vertice(v);
        Vertice verticeIzq = vertice.izquierdo;

        verticeIzq.padre = vertice.padre;
        if(!esRaiz(vertice))
            if(esHijoIzquierdo(vertice))
                vertice.padre.izquierdo = verticeIzq;
            else
                vertice.padre.derecho = verticeIzq;
        else
            raiz = verticeIzq;

        vertice.izquierdo = verticeIzq.derecho;
        if(verticeIzq.hayDerecho())
            verticeIzq.derecho.padre = vertice;

        verticeIzq.derecho = vertice;
        vertice.padre = verticeIzq;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> v) {
        if (v == null || !v.hayDerecho())
            return;
        Vertice vertice = vertice(v);
        //Se toma al hijo derecho del vertice a girar, siempre se tiene a ese hijo
        //por que si no lo tiene no se podra girar.
        Vertice verticeDer = vertice.derecho;
        //Hacemos que nuestro verticeDer apunte al padre del vertice
        //de esta forma no nos preocupamos si es raiz o no.
        verticeDer.padre = vertice.padre;

        if (!esRaiz(vertice))
            //Si no es raiz solo enlazamos del lado de donde venga. 
            if (esHijoIzquierdo(vertice))
                vertice.padre.izquierdo = verticeDer;
            else
                vertice.padre.derecho = verticeDer;
        //En caso que sea raiz
        else
            raiz = verticeDer;

        //El vertice a girar su hijo derecho es el vertice izquierdo del
        //verticeDer.izquierdo, tambien no nos preocupamos si es null.
        vertice.derecho = verticeDer.izquierdo;
        if (verticeDer.hayIzquierdo())
            //Si verticeDer si tiene hijo izquierdo entonces lo enlazamos con el vertice.
            verticeDer.izquierdo.padre = vertice;
        //Al final solo le enlazamos al verticeDer su hijo derecho el 
        //vertice el cual se iba a girar.
        verticeDer.izquierdo = vertice;
        vertice.padre = verticeDer;
    }
}
