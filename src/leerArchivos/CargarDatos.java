package leerArchivos;

import java.io.File;
import componentes.Nodo;
import componentes.Tubo;
import componentes.Tanque;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class CargarDatos {

    /**
     *
     * @param f archivo con la informacion sobre nodos
     * @param separador separador de caracteres
     * @return un List con todos los nodos en el archivo
     * @throws IOException si no existe un archivo en la ruta especificada
     */
    public static List<Nodo> nodos(File f, String separador) throws IOException {
        String[][] nodo = new ImportarFile(f, separador).enCaracteres();
        List<Nodo> nodos = new ArrayList<>();
        if (nodo != null) {
            for (int i = 1; i < nodo.length; i++) {
                Nodo n = new Nodo(nodo[i]);
                float caudal = 0f;
                float pressure = 0f;
                float caudalSalida = 0f;
                if (nodo[i].length > 4 && !nodo[i][4].isEmpty()) {
                    caudal = Float.parseFloat(nodo[i][4]);
                }
                if (nodo[i].length > 5 && !nodo[i][5].isEmpty()) {
                    pressure = Float.parseFloat(nodo[i][5]);
                }
                if (nodo[i].length > 6 && !nodo[i][6].isEmpty()) {
                    caudalSalida = Float.parseFloat(nodo[i][6]);
                }
                n.agregarEntradaConstante(caudal, pressure);
                n.agregarSalidaConstante(caudalSalida);
                nodos.add(n);
            }
        }
        return nodos;
    }

    /**
     *
     * @param f archivo con la informacion sobre los tanques del sistema
     * @param separador separador de caracteres
     * @return un List con los tanques en la red
     * @throws IOException Si el archivo no se encuentra en la ruta especificada
     */
    public static List<Tanque> tanques(File f, String separador) throws IOException {
        String[][] tanque = new ImportarFile(f, separador).enCaracteres();
        List<Tanque> tanques = new ArrayList<>();
        if (tanque != null) {
            for (int i = 1; i < tanque.length; i++) {
                tanques.add(new Tanque(tanque[i]));
            }
        }
        return tanques;
    }

    /**
     *
     * @param f archivo con la informacion sobre los tubos del sistema
     * @param separador separador de caracteres
     * @param nodos un List con los nodos del sistema
     * @param tanques un List con los tanques del sistema
     * @return un List de los tubos del sistema, con sus respectivas conexiones
     * @throws IOException Si la ruta especificada no apunta a ningun archivo
     * @throws Exception Si algun tubo no tiene principio o no tiene fin
     */
    public static List<Tubo> tubos(File f, String separador, List<Nodo> nodos, List<Tanque> tanques) throws IOException, Exception {
        String[][] tubo = new ImportarFile(f, separador).enCaracteres();
        List<Tubo> tubos = new ArrayList<>();
        if (tubo != null && !nodos.isEmpty()) {
            for (int i = 1; i < tubo.length; i++) {
                float k[] = extraerPerdidasLocales(tubo[i][6]);
                Nodo inicio = extraerNodo(tubo[i][1], nodos);
                Nodo fin = extraerNodo(tubo[i][2], nodos);
                Tanque inicio1 = null;
                Tanque fin1 = null;
                if (inicio == null) {
                    inicio1 = extraerTanque(tubo[i][1], tanques);
                }
                if (fin == null) {
                    fin1 = extraerTanque(tubo[i][2], tanques);
                }
                Tubo t = new Tubo(tubo[i][0], Float.parseFloat(tubo[i][4]), Float.parseFloat(tubo[i][5]), k,
                        Float.parseFloat(tubo[i][3]), inicio, fin, inicio1, fin1);
                tubos.add(t);
            }
            agregarTubosNodos(nodos, tubos);
            agregarTubosTanques(tanques, tubos);
        }
        return tubos;
    }

    /**
     * Usar este metodo, si no se han hecho las conexiones con el metodo tubos()
     *
     * @param nodos un List con los nodos del sistema
     * @param tubos un List con los tubos del sistema
     * @return los nodos con sus respectivas conexiones
     */
    public static List<Nodo> agregarTubosNodos(List<Nodo> nodos, List<Tubo> tubos) {
        if (nodos == null || tubos == null) {
            return null;
        }
        for (int i = 0; i < nodos.size(); i++) {
            Nodo n = nodos.remove(0);
            String nombre = n.nombre();
            for (Tubo t : tubos) {
                if (t.inicio().equals(nombre)) {
                    n.agregarSalida(t);
                } else if (t.fin().equals(nombre)) {
                    n.agregarEntrada(t);
                }
            }
            nodos.add(n);
        }
        return nodos;
    }

    /**
     * Usar este metodo, si no se han hecho las conexiones con el metodo tubos()
     *
     * @param tanques un List con los tanques del sistema
     * @param tubos un List con los tubos del sistema
     * @return los tanques con sus respectivas conexiones
     */
    public static List<Tanque> agregarTubosTanques(List<Tanque> tanques, List<Tubo> tubos) {
        if (tanques == null || tubos == null) {
            return null;
        }
        for (int i = 0; i < tanques.size(); i++) {
            Tanque tanque = tanques.remove(0);
            String nombre = tanque.nombre();
            for (Tubo t : tubos) {
                if (t.inicio().equals(nombre)) {
                    tanque.agregarSalida(t);
                } else if (t.fin().equals(nombre)) {
                    tanque.agregarEntrada(t);
                }
            }
            tanques.add(tanque);
        }
        return tanques;
    }

    private static float[] extraerPerdidasLocales(String tubo) {
        if (tubo.equals("NN")) {
            return null;
        }
        String[] values = tubo.split("/");
        float[] k = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            k[i] = Float.parseFloat(values[i]);
        }
        return k;
    }

    private static Nodo extraerNodo(String tubo, List<Nodo> nodos) {
        if (nodos != null) {
            for (Nodo n : nodos) {
                if (n.nombre().equals(tubo)) {
                    return n;
                }
            }
        }
        return null;
    }

    private static Tanque extraerTanque(String tubo, List<Tanque> tanques) {
        if (tanques != null) {
            for (Tanque t : tanques) {
                if (t.nombre().equals(tubo)) {
                    return t;
                }
            }
        }
        return null;
    }

}
