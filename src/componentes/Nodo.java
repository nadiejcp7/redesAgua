/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class Nodo {

    private float presion, Qin;
    private List<Tubo> entrada, salida;
    private List<Float> entradaConstante, salidaConstante;
    private final String nombre;
    final float z, x, y, g;
    private float[] caudal;
    private final long code;
    private boolean iniciado;

    /**
     *
     * @param nombre nombre del nodo
     * @param x coordenada x
     * @param y coordenada y
     * @param z coordenada z Se puede usar cualquier sistema de coordenadas,
     * siempre y cuando todos los nodos estén dentro del mismo sistema
     */
    public Nodo(String nombre, float x, float y, float z) {
        this.z = z;
        this.x = x;
        this.y = y;
        this.g = 9.8F;
        this.nombre = nombre;
        entrada = new ArrayList<>();
        salida = new ArrayList<>();
        entradaConstante = new ArrayList<>();
        salidaConstante = new ArrayList<>();
        presion = 0;
        this.code = Tubo.generarCode();
        iniciado = false;
    }

    /**
     *
     * @param nodo el vector debe estar en este orden nombre, x, y, z
     */
    public Nodo(String[] nodo) {
        this(nodo[0], Float.parseFloat(nodo[1]), Float.parseFloat(nodo[2]), Float.parseFloat(nodo[3]));
    }

    /**
     *
     * @return la presión en el nodo en m.
     */
    public float presion() {
        return presion;
    }

    /**
     * Agrega un tubo por donde sale caudal del nodo.
     *
     * @param t el tubo por donde sale caudal.
     */
    public void agregarSalida(Tubo t) {
        salida.add(t);
    }

    /**
     * Agrega un tubo por donde ingresa caudal al nodo.
     *
     * @param t el tubo por donde entra caudal.
     */
    public void agregarEntrada(Tubo t) {
        entrada.add(t);
    }

    /**
     * Agrega una entrada de agua al nodo.
     *
     * @param valor caudal de entrada.
     * @param pressure presión, poner 0 si no se conoce
     */
    public void agregarEntradaConstante(float valor, float pressure) {
        entradaConstante.add(valor);
        if (presion < pressure) {
            presion = pressure;
        }
    }

    /**
     * Agrega una salida del nodo.
     *
     * @param valor caudal de salida.
     */
    public void agregarSalidaConstante(float valor) {
        salidaConstante.add(valor);
    }

    /**
     *
     * @return el nombre de este nodo.
     */
    public String nombre() {
        return nombre;
    }

    /**
     *
     * @return coordenada z o altura
     */
    public float z() {
        return z;
    }

    /**
     *
     * @return coordenada x
     */
    public float x() {
        return x;
    }

    /**
     *
     * @return coordenada y
     */
    public float y() {
        return y;
    }

    /**
     *
     * @param code el código del tubo de salida
     * @return el caudal de salida
     */
    public float caudalSalida(long code) {
        if (caudal == null) {
            return 0;
        }
        for (int i = 0; i < salida.size(); i++) {
            if (salida.get(i).code() == code) {
                return caudal[i];
            }
        }
        return 0;
    }

    /**
     * Emplea el método Hardy Cross
     */
    public void calcular() {
        int total = salida.size();
        tomarDatos();
        if (Qin <= 0) {
            for (int i = 0; i < caudal.length; i++) {
                caudal[i] = 0;
            }
        } else {
            if (total > 0) {
                if (salidaConstante.isEmpty() && salida.size() == 1) {
                    caudal[0] = Qin;
                } else {
                    List< List<Tubo>> tubos = encontrarTubos();
                    if (tubos.isEmpty()) {
                        distribuir();
                    } else {
                        for (List<Tubo> tubo : tubos) {
                            int total1 = tubo.size();
                            float[] caudales = new float[total1];
                            float[] k = new float[total1];
                            float[] diametros = new float[total1];
                            float[] longitudes = new float[total1];
                            List<float[]> data = new ArrayList<>();
                            for (int j = 0; j < total1; j++) {
                                Tubo t = tubo.get(j);
                                diametros[j] = t.diametro();
                                longitudes[j] = t.longitud();
                                float[] cc = t.xyzInicio();
                                if (!existe(data, cc, 3)) {
                                    data.add(cc);
                                }
                                cc = t.xyzFin();
                                if (!existe(data, cc, 3)) {
                                    data.add(cc);
                                }
                            }
                            float[] centro = promedio(data);
                            for (int j = 0; j < total1; j++) {
                                Tubo t = tubo.get(j);
                                caudales[j] = t.caudal() * t.orientacion(centro);
                                t.perdidas(Math.abs(caudales[j]));
                                k[j] = (float) (8 * t.f() * longitudes[j] / (Math.pow(diametros[j], 5) * g * Math.pow(Math.PI, 2)));
                            }
                            double num = 2;
                            int contador = 0;
                            double tolerancia = Math.pow(10, -5);
                            while (contador < 40) {
                                float f1 = 0;
                                float f2 = 0;
                                for (int j = 0; j < total1; j++) {
                                    f1 += k[j] * caudales[j] * Math.abs(caudales[j]);
                                    f2 += num * k[j] * Math.abs(caudales[j]);
                                }
                                float cambioCaudal = -f1 / f2;
                                float sumaPerdidas = 0;
                                for (int j = 0; j < total1; j++) {
                                    Tubo t = tubo.get(j);
                                    caudales[j] = caudales[j] + cambioCaudal;
                                    t.perdidas(Math.abs(caudales[j]));
                                    double per = t.hf();
                                    if (caudales[j] < 0) {
                                        per = -1 * per;
                                    }
                                    sumaPerdidas += per;
                                    k[j] = (float) (8 * t.f() * longitudes[j] / (Math.pow(diametros[j], 5) * g * Math.pow(Math.PI, 2)));
                                }
                                contador++;
                                if (Math.abs(sumaPerdidas) < tolerancia) {
                                    contador = 40;
                                }
                            }
                            for (int i = 0; i < salida.size(); i++) {
                                caudal[i] = buscarCaudal(salida.get(i), tubo, caudales);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param data base de datos
     * @param cc coordenadas a comparar si existe en la base de datos
     * @param n cuantos valores del vector quiero comparar n < cc.length @return
     * verdadero si cc está dentro de data, falso si no está
     */
    private boolean existe(List<float[]> data, float[] cc, int n) {
        if (data.isEmpty()) {
            return false;
        }
        for (float[] cc1 : data) {
            boolean igual = true;
            for (int j = 0; j < n; j++) {
                if (cc[j] != cc1[j]) {
                    igual = false;
                }
            }
            if (igual) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param data son las coordenadas de varios puntos
     * @return promedio entre las coordenadas, devuelve las coordenadas de un
     * punto de referencia
     */
    private float[] promedio(List<float[]> data) {
        float[] promedio = new float[2];
        for (float[] cc : data) {
            promedio[0] += cc[0];
            promedio[1] += cc[1];
        }
        promedio[0] = promedio[0] / data.size();
        promedio[1] = promedio[1] / data.size();
        if (existe(data, promedio, 2)) {
            promedio[0]++;
        }
        return promedio;
    }

    /**
     * Collecta los datos del caudal que entra, que sale y la presión.
     */
    private void tomarDatos() {
        Qin = 0;
        for (Tubo t : entrada) {
            Qin += t.caudal();
            float pf = t.presionFinal();
            if (!iniciado) {
                iniciado = true;
                presion = pf;
            }
        }
        for (float entradaConstante1 : entradaConstante) {
            Qin += entradaConstante1;
        }
        for (float salidaConstante1 : salidaConstante) {
            Qin -= salidaConstante1;
        }
    }

    /**
     *
     * @return los tubos que salen de este nodo
     */
    public List<Tubo> salida() {
        return salida;
    }

    /**
     *
     * @return encuentras las redes de forma cuadrada
     */
    private List<List<Tubo>> encontrarTubos() {
        List<List<Tubo>> tubos = new ArrayList<>();
        List<Tubo[]> cercanos = agrupar();
        for (Tubo[] t : cercanos) {
            boolean esCerrado = false;
            List<Tubo> tubos2 = new ArrayList<>();
            List<Tubo> tuboSalida2 = new ArrayList<>();
            List<float[]> data = new ArrayList<>();
            for (Tubo t1 : t) {
                tubos2.add(t1);
                for (Tubo t11 : t1.salidaTubosFin()) {
                    tuboSalida2.add(t11);
                }
                float[] cc = t1.xyzFin();
                data.add(cc);
            }
            for (Tubo tuboSalida21 : tuboSalida2) {
                float[] cc = tuboSalida21.xyzFin();
                Tubo t1 = repetido(data, cc, tubos2.size(), tuboSalida2);
                if (t1 == null) {
                    data.add(cc);
                } else {
                    esCerrado = true;
                    if (!existe(tubos2, t1)) {
                        tubos2.add(t1);
                    }
                    if (!existe(tubos2, tuboSalida21)) {
                        tubos2.add(tuboSalida21);
                    }
                }
            }
            if (esCerrado) {
                tubos.add(tubos2);
            }
        }
        return tubos;
    }

    private Tubo repetido(List<float[]> data, float[] cc, int ini, List<Tubo> tubos) {
        for (int i = 0; i < data.size(); i++) {
            boolean igual = true;
            float[] cc1 = data.get(i);
            for (int j = 0; j < 3; j++) {
                if (cc[j] != cc1[j]) {
                    igual = false;
                }
            }
            if (igual) {
                return tubos.get(i - ini);
            }
        }
        return null;
    }

    /**
     * Agrupa en parejas todos los tubos que salen del nodo. Ejemplo: si existen
     * tres tubos 1, 2 y 3 entonces agrupa 1-2,1-3,2-3
     *
     * @return nos devuelve un list con varios vectores de longitud 2.
     */
    private List<Tubo[]> agrupar() {
        List<Tubo[]> agrupados = new ArrayList<>();
        for (int n = 0; n < salida.size(); n++) {
            for (int i = n + 1; i < salida.size(); i++) {
                agrupados.add(new Tubo[]{salida.get(i), salida.get(n)});
            }
        }
        return agrupados;
    }

    /**
     *
     * @param get el tubo del que se desconoce el caudal
     * @param tubo la lista de tubos disponible
     * @param caudales el valor de los caudales
     * @return el caudal para el tubo get
     */
    private float buscarCaudal(Tubo get, List<Tubo> tubo, float[] caudales) {
        for (int i = 0; i < tubo.size(); i++) {
            if (get.code() == tubo.get(i).code()) {
                return Math.abs(caudales[i]);
            }
        }
        return 0;
    }

    /**
     *
     * @param tubos2 lista de tubos
     * @param get tubo a determinar si está dentro de la lista
     * @return verdadero si el código del tubo existe dentro de la lista de
     * tubos.
     */
    private boolean existe(List<Tubo> tubos2, Tubo get) {
        for (Tubo t : tubos2) {
            if (t.code() == get.code()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Llama al método tomarDatos y distribuir, en ese orden.
     */
    public void distribuirCaudales() {
        tomarDatos();
        distribuir();
    }

    /**
     *
     * @return el código del nodo.
     */
    public long code() {
        return code;
    }

    /**
     * Método para distribuir los caudales de salida según el área del tubo de
     * salida.
     */
    private void distribuir() {
        double areaTotal = 0;
        for (Tubo salida1 : salida) {
            areaTotal += Math.pow(salida1.diametro() / 2, 2) * Math.PI;
        }
        caudal = new float[salida.size()];
        for (int i = 0; i < caudal.length; i++) {
            caudal[i] = (float) (Math.pow(salida.get(i).diametro() / 2, 2) * Math.PI * Qin / areaTotal);
        }
    }
}
