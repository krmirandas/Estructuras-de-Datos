package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase privada para iteradores de gráficas. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Grafica<T>.Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().getElemento();
        }

        /* No lo implementamos: siempre lanza una excepción. */
        @Override public void remove() {
            throw new UnsupportedOperationException("Eliminar con el iterador " +
                                                    "no está soportado");
        }
    }

    /* Vecinos para gráficas; un vecino es un vértice y el peso de la arista que
     * los une. Implementan VerticeGrafica. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vecino del vértice. */
        public Grafica<T>.Vertice vecino;
        /* El peso de vecino conectando al vértice con el vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Grafica<T>.Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T getElemento() {
            return vecino.getElemento();
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.getColor();
        }

        /* Define el color del vecino. */
        @Override public void setColor(Color color) {
            vecino.setColor(color);
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /* Vertices para gráficas; implementan la interfaz ComparableIndexable y
     * VerticeGrafica */
    private class Vertice implements VerticeGrafica<T>,
        ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* La lista de vecinos del vértice. */
        public Lista<Grafica<T>.Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            this.color = Color.NINGUNO;
            this.vecinos = new Lista<>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T getElemento() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Define el color del vértice. */
        @Override public void setColor(Color color) {
            this.color = color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            if (distancia > vertice.distancia)
                return 1;
            else if (distancia < vertice.distancia)
                return -1;
            return 0;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue de la vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Lista<>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null || contiene(elemento))
            throw new IllegalArgumentException();
        vertices.agrega(new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la vecino que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        if (a.equals(b) || sonVecinos(a, b))
            throw new IllegalArgumentException("a y b ya están conectados, o a es igual a b");
        va.vecinos.agrega(new Vecino(vb, 1));
        vb.vecinos.agrega(new Vecino(va, 1));
        aristas++;
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b, double peso) {
        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        if (a.equals(b) || sonVecinos(a, b) || peso < 1)
            throw new IllegalArgumentException("a y b ya están conectados, o a es igual a b");
        va.vecinos.agrega(new Vecino(vb, peso));
        vb.vecinos.agrega(new Vecino(va, peso));
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        if (a.equals(b) || !sonVecinos(a, b))
            throw new IllegalArgumentException("a o b no están conectados.");
        Vecino ve_ab = null, ve_ba = null;
        for (Vecino ve : va.vecinos) {
            if (ve.vecino.equals(vb)) {
                ve_ab = ve;
            }
        }
        for (Vecino ve : vb.vecinos) {
            if (ve.vecino.equals(va)) {
                ve_ba = ve;
            }
        }
        va.vecinos.elimina(ve_ab);
        vb.vecinos.elimina(ve_ba);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la gráfica,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice v : vertices)
            if (v.getElemento().equals(elemento))
                return true;
        return false;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        if (!contiene(elemento))
            throw new NoSuchElementException("El elemento no está contenido en la gráfica.");
        Vertice v = (Vertice) vertice(elemento);
        for (Vertice ver : vertices)
            for (Vecino vec : ver.vecinos)
                if (vec.vecino.equals(v)) {
                    ver.vecinos.elimina(vec);
                    aristas--;
                }
        vertices.elimina(v);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <tt>true</tt> si a y b son vecinos, <tt>false</tt> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        if (!this.contiene(a) || !this.contiene(b))
            throw new NoSuchElementException("a o b no son elementos de la gráfica");
        Vertice va = (Vertice)vertice(a);
        Vertice vb = (Vertice)vertice(b);
        for (Vecino ve : va.vecinos)
            if (ve.vecino.equals(vb))
                return true;
        return false;
    }

    /**
     * Regresa el peso de la vecino que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la vecino que comparten los vértices que contienen a
     *         los elementos recibidos, o -1 si los elementos no están
     *         conectados.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public double getPeso(T a, T b) {
        if (!contiene(a) || !contiene(b))
            throw new NoSuchElementException();
        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        if (va == vb)
            throw new IllegalArgumentException();
        for (Vecino ve : va.vecinos)
            if (ve.vecino.equals(vb))
                return ve.peso;
        return -1;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        for (Vertice e : vertices)
            if (e.getElemento().equals(elemento))
                return e;
        throw new NoSuchElementException();
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (VerticeGrafica<T> v : vertices)
            accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        recorrido(elemento, accion, new Cola<Grafica<T>.Vertice>());
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        recorrido(elemento, accion, new Pila<Grafica<T>.Vertice>());
    }

    private void recorrido(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Grafica<T>.Vertice> metesaca) {
        if (!contiene(elemento))
            throw new NoSuchElementException("El elemento no está en la gráfica.");
        Vertice v = (Vertice) vertice(elemento);
        metesaca.mete(v);
        while (!metesaca.esVacia()) {
            Vertice vt = metesaca.saca();
            vt.setColor(Color.ROJO);
            accion.actua(vt);
            for (Vecino ve : vt.vecinos) {
                if (ve.vecino.color != Color.ROJO) {
                    ve.setColor(Color.ROJO);
                    metesaca.mete(ve.vecino);
                }
            }
        }
        paraCadaVertice(vertice -> vertice.setColor(Color.NINGUNO));
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacio() {
        return vertices.getElementos() == 0;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <tt>a</tt> y
     *         <tt>b</tt>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException();
        Vertice vo = (Vertice)vertice(origen);
        Vertice vd = (Vertice)vertice(destino);
        preparaVertices(vo);
        creaMonticuloYCalculaDistancias(false);
        return reversaTrayectoria(vo, vd, false);
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <tt>origen</tt> y
     *         el vértice <tt>destino</tt>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException();
        Vertice vo = (Vertice)vertice(origen);
        Vertice vd = (Vertice)vertice(destino);
        preparaVertices(vo);
        creaMonticuloYCalculaDistancias(true);
        return reversaTrayectoria(vo, vd, true);
    }

    /**
     * Método que prepara los vértices; A todos los vértices se les pondrá
     * distancia infinita, excepto con el que se empezará, que es el vértice origen.
     * @param vo El vertice origen.
     *
     */
    private void preparaVertices(Vertice vo) {
        for (Vertice v : vertices)
            v.distancia = Double.POSITIVE_INFINITY;
        vo.distancia = 0;
    }

    /**
     * El método crea un montículo, para ser utilizado, recordando que un montículo obtener
     * el elemento menor es O(1). Se saca el elemento menor (que en el primer caso es el de
     * distancia 0 (osea el origen)), y se ira calulando el valor de la distancia e cada
     * vértice.
     * @param esDijkstra Cuando es <b>true</b> significa que se usará el algorítmo de
     *                   Dijkstra y para calcular trayectoria mínima se usara el peso
     *                   de la arista.
     *                   Cuando es <b>false</b> significa que se hará la trayectoría
     *                   mínima, esto es que para cada vertice se irá aumentando en una
     *                   unidad.
     */
    private void creaMonticuloYCalculaDistancias(boolean esDijkstra) {
        MonticuloMinimo<Vertice> monticulo = new MonticuloMinimo<>(vertices);
        while (!monticulo.esVacio()) {
            Vertice vAux = monticulo.elimina();
            //ve.peso es la aristas abajo.
            for (Vecino ve : vAux.vecinos) {
                if (ve.vecino.distancia == Double.POSITIVE_INFINITY ||
                        vAux.distancia + ve.peso < ve.vecino.distancia) {
                    ve.vecino.distancia = vAux.distancia + (esDijkstra ? ve.peso : 1);
                    monticulo.reordena(ve.vecino);
                }
            }
        }
    }

    /**
     * Método que devuelve la trayectoria para cada algorítmo.
     * Primero se verificará si la trayectoria pertenece a una gráfica conexa. Si sí lo
     * comple se irá reconstruyendo la trayectoria del vertice destino al vertice origen.
     * @param vo El vértice de origen.
     * @param vd El vértice de destino.
     * @param esDijkstra Cuando es <b>true</b> significa que se usará el algorítmo de
     *                   Dijkstra y para calcular trayectoria mínima se usara el peso
     *                   de la arista.
     *                   Cuando es <b>false</b> significa que se hará la trayectoría
     *                   mínima, esto es que para cada vertice se irá restando en una
     *                   unidad.
     */
    private Lista<VerticeGrafica<T>> reversaTrayectoria(Vertice vo, Vertice vd, boolean esDijkstra) {
        Lista<VerticeGrafica<T>> l = new Lista<>();
        //Validacion para saber si nuestra gráfica fue conexa.
        if (vd.distancia != Double.POSITIVE_INFINITY) {
            Vertice vAux = vd;
            while (vAux != vo) {
                for (Vecino ve : vAux.vecinos) {
                    if (vAux.distancia - (esDijkstra ? ve.peso : 1) == ve.vecino.distancia) {
                        l.agregaInicio(vAux);
                        vAux = ve.vecino;
                        break;
                    }
                }
                if (vAux == vo) {
                    l.agregaInicio(vo);
                }
            }
        }
        return l;
    }

}
