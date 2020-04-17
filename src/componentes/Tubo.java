package componentes;

import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class Tubo {

    private final float longitud, rugosidad, d, p, u, g, area;
    private float f, v, Q, reynolds;
    private final float[] k;
    private final Nodo fin, inicio;
    private final String nombre;
    private final Tanque fin1, inicio1;
    private final long code;

    /**
     *
     * @param nombre el nombre de la tuberia
     * @param l longitud en metros
     * @param rugosidad rugosidad, para PVC 0.0015
     * @param k coeficientes de perdidas locales
     * @param d diametro en metros
     * @param inicio nodo donde nace la tuberia
     * @param fin nodo donde llega la tuberia
     * @param inicio1 tanque donde nace la tuberia
     * @param fin1 tanque donde muere la tuberia
     * @throws java.lang.Exception si no se asigna un inicio o un final
     */
    public Tubo(String nombre, float l, float rugosidad, float[] k, float d, Nodo inicio, Nodo fin, Tanque inicio1, Tanque fin1) throws Exception {
        this.nombre = nombre;
        this.longitud = l;
        this.rugosidad = rugosidad;
        this.k = k;
        this.d = d;
        this.Q = (float) (Math.pow(d / 2, 2) * Math.PI * v);
        this.p = (float) 997.2;
        this.u = (float) 0.000911;
        this.inicio = inicio;
        this.g = (float) 9.8;
        this.fin = fin;
        this.inicio1 = inicio1;
        this.fin1 = fin1;
        this.area = (float) (Math.pow(d / 2, 2) * Math.PI);
        this.code = generarCode();
        if (inicio == null && inicio1 == null) {
            throw new Exception("Tubería debe tener un nodo o tanque de inicio.");
        }
        if (fin == null && fin1 == null) {
            throw new Exception("Tubería debe tener un nodo o tanque al final.");
        }
    }

    public float f() {
        return f;
    }

    /**
     *
     * @return Devuelve el valor de perdida por friccion en metros.
     */
    public float hf() {
        float tolerancia = (float) Math.pow(10, -7);
        float diferencia = Float.MAX_VALUE;
        float mayor = Float.MAX_VALUE;
        float menor = 0;
        if (reynolds < 2300) {
            f = 64 / reynolds;
        } else {
            while (diferencia > tolerancia) {
                f = menor + (mayor - menor) / 2;
                float valor1 = (float) (1 / Math.sqrt(f));
                float valor2 = (float) (2 * Math.log10(rugosidad / (d * 3.7) + 2.51 / (reynolds * Math.sqrt(f))));
                float resultado = valor1 + valor2;
                if (resultado < 0) {
                    mayor = f;
                } else {
                    menor = f;
                }
                diferencia = Math.abs(resultado);
            }
        }
        return (float) (f * longitud * Math.pow(v, 2) / (d * 2 * g));
    }

    /**
     *
     * @return Devuelve el valor de perdida por codos, valvulas, etc en metros.
     */
    public float hk() {
        if (k == null || k.length == 0) {
            return 0;
        }
        double total = 0;
        for (int i = 0; i < k.length; i++) {
            total += k[i];
        }
        return (float) (total * Math.pow(v, 2) / (2 * g));
    }

    /**
     *
     * @return Valor que gana o pierde un fluido cuando sube o baja por una
     * tuberia, viene en m.
     */
    public float hf2() {
        if (inicio != null) {
            if (fin != null) {
                return fin.z() - inicio.z();
            }
            return fin1.z() - inicio.z();
        } else {
            if (fin != null) {
                return fin.z() - inicio1.z();
            }
            return fin1.z() - inicio1.z();
        }
    }

    /**
     * Q = A * v = pi*r2*v
     *
     * @return devuelve el caudal que pasa por la tuberia m3/s
     */
    public float caudal() {
        encontrarValores();
        return Q;
    }

    /**
     *
     * @return diámetro de la tubería en m.
     */
    public float diametro() {
        return d;
    }

    /**
     *
     * @return devuelve la velocidad que pasa un flujo por esta tuberia m/s
     */
    public double velocidad() {
        encontrarValores();
        return v;
    }

    /**
     *
     * @return devuelve la presion en columna de agua (m) al final del tubo
     */
    public float presionFinal() {
        encontrarValores();
        float perdidas = perdidas(Q);
        if (inicio != null) {
            float h = inicio.presion();
            if (h == 0) {
                return 0;
            }
            return h - perdidas;
        } else {
            float h = inicio1.presion();
            if (h == 0) {
                return 0;
            }
            return h - perdidas;
        }
    }

    /**
     *
     * @return el nombre del tubo
     */
    public String nombre() {
        return nombre;
    }

    /**
     * Busca la velocidad y el caudal del tubo
     */
    private void encontrarValores() {
        if (inicio != null) {
            Q = inicio.caudalSalida(code);
            this.v = Q / area;
        } else {
            v = inicio1.velocidadSalida(0);
            this.Q = area * v;
        }
    }

    /**
     *
     * @return código del tubo
     */
    public long code() {
        return code;
    }

    /**
     *
     * @return longitud del tubo en m.
     */
    public float longitud() {
        return longitud;
    }

    /**
     *
     * @param Q caudal
     * @return pérdidas del tubo con un caudal Q.
     */
    public float perdidas(float Q) {
        this.Q = Q;
        this.v = (float) (Q / (Math.PI * Math.pow(d / 2, 2)));
        reynolds = p * this.v * d / u;
        float hf = hf();
        float hf2 = hf2();
        float hk = hk();
        return hf + hf2 + hk;
    }

    /**
     *
     * @return coordenadas del nodo o tanque donde termina esta tubería
     */
    public float[] xyzFin() {
        if (fin != null) {
            return new float[]{fin.x(), fin.y(), fin.z() + fin.presion()};
        } else {
            return new float[]{fin1.x(), fin1.y(), fin1.z() + fin1.presion()};
        }
    }

    /**
     *
     * @return coordenadas del nodo o tanque donde inicia esta tubería
     */
    public float[] xyzInicio() {
        if (inicio != null) {
            return new float[]{inicio.x(), inicio.y(), inicio.presion() + inicio.z()};
        } else {
            return new float[]{inicio1.x(), inicio1.y(), inicio1.z() + inicio1.presion()};
        }
    }

    /**
     *
     * @param centro coordenadas de un punto de referencia
     * @return -1 si el flujo por esta tubería circula en sentido antihorario, 1
     * si no.
     */
    public int orientacion(float[] centro) {
        float[] coordenadasFin = xyzFin();
        float[] coordenadasInicio = xyzInicio();
        float[] ai = calcular(coordenadasInicio, centro, hipotenusa(coordenadasInicio, centro));
        float[] af = calcular(coordenadasFin, centro, hipotenusa(coordenadasFin, centro));
        int flujo = 1;
        if (coordenadasFin[2] > coordenadasInicio[2]) {
            flujo = -1;
        }
        if (ai[1] == 1 && af[1] == 4) {
            return flujo * 1;
        }
        if (ai[1] > af[1]) {
            return flujo * 1;
        } else {
            if (ai[0] > af[0]) {
                return flujo * 1;
            } else if (ai[0] <= Math.PI / 2 && af[0] >= 3 * Math.PI / 2) {
                return flujo * 1;
            }
        }
        return flujo * -1;
    }

    private float hipotenusa(float[] coordenadasFin, float[] centro) {
        double num = Math.pow(coordenadasFin[0] - centro[0], 2) + Math.pow(coordenadasFin[1] - centro[1], 2);
        return (float) Math.sqrt(num);
    }

    private float[] calcular(float[] coordenadasFin, float[] centro, float hipotenusa) {
        int cuadrante = 1;
        float angulo = 0;
        if (coordenadasFin[0] > centro[0]) {
            if (coordenadasFin[1] > centro[1]) {
                cuadrante = 1;
                angulo = (float) Math.asin((coordenadasFin[1] - centro[1]) / hipotenusa);
            } else if (coordenadasFin[1] < centro[1]) {
                cuadrante = 4;
                angulo = (float) (2 * Math.PI + Math.asin((coordenadasFin[1] - centro[1]) / hipotenusa));
            }
        } else if (coordenadasFin[0] == centro[0]) {
            if (coordenadasFin[1] > centro[1]) {
                cuadrante = 2;
                angulo = (float) (Math.PI / 2);
            } else if (coordenadasFin[1] < centro[1]) {
                cuadrante = 4;
                angulo = (float) (3 * Math.PI / 2);
            }
        } else {
            if (coordenadasFin[1] > centro[1]) {
                cuadrante = 2;
                angulo = (float) (Math.PI / 2 - Math.asin((coordenadasFin[0] - centro[0]) / hipotenusa));
            } else if (coordenadasFin[1] < centro[1]) {
                cuadrante = 3;
                angulo = (float) (Math.PI - Math.asin((coordenadasFin[1] - centro[1]) / hipotenusa));
            } else {
                cuadrante = 3;
                angulo = (float) Math.PI;
            }
        }
        return new float[]{angulo, cuadrante};
    }

    /**
     *
     * @return nombre del nodo o tanque donde inicia la tubería
     */
    public String inicio() {
        if (inicio != null) {
            return inicio.nombre();
        } else {
            return inicio1.nombre();
        }
    }

    /**
     *
     * @return nombre del nodo o tanque donde termina la tubería
     */
    public String fin() {
        if (fin != null) {
            return fin.nombre();
        } else {
            return fin1.nombre();
        }
    }

    /**
     *
     * @return los tubos que salen del nodo donde termina esta tubería
     */
    public List<Tubo> salidaTubosFin() {
        if (fin != null) {
            return fin.salida();
        } else {
            return fin1.salida();
        }
    }

    /**
     *
     * @return genera un codigo de 10 dígitos
     */
    public static long generarCode() {
        String code = "";
        for (int i = 0; i < 10; i++) {
            int value = (int) (Math.random() * 9);
            code += String.valueOf(value);
        }
        return Long.parseLong(code);
    }

    /**
     *
     * @return area del tubo en m2
     */
    public float area() {
        return area;
    }
}
