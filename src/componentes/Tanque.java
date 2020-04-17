package componentes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class Tanque {

    final float z, x, y;
    private final float d, hMax, g;
    private float h, entradaCaudal;
    private List<Tubo> salida, entrada;
    String nombre;

    public Tanque(String nombre, float vol, float hMax, float h, float x, float y, float z) {
        this.nombre = nombre;
        this.z = z;
        this.x = x;
        this.y = y;
        this.d = (float) (Math.sqrt(vol / (hMax * Math.PI)) * 2);
        this.hMax = hMax;
        this.h = h;
        this.g = 9.80665F;
        salida = new ArrayList<>();
        entrada = new ArrayList<>();
        entradaCaudal = 0;
    }

    public Tanque(String[] tanque) {
        this(tanque[0], Float.parseFloat(tanque[6]), Float.parseFloat(tanque[5]), Float.parseFloat(tanque[4]),
                Float.parseFloat(tanque[1]), Float.parseFloat(tanque[2]), Float.parseFloat(tanque[3]));
    }

    /**
     * @param altura representa la altura a la que está la parte inferior de la
     * tuberia, la base del tanque representa un valor de 0.
     * @return Da la velocidad con la que sale un fluido a una altura dada.
     */
    public float velocidadSalida(float altura) {
        if (h > altura) {
            return (float) Math.sqrt(2 * g * (h - altura));
        }
        return 0F;
    }

    public void agregarSalida(Tubo t) {
        salida.add(t);
    }

    /**
     *
     * @param t el tubo que ingresa al tanque
     */
    public void agregarEntrada(Tubo t) {
        entrada.add(t);
    }

    public float volumeMaximo() {
        return (float) (Math.pow(d / 2, 2) * Math.PI * hMax);
    }

    public double volumeAgua() {
        if (h > 0) {
            return Math.pow(d / 2, 2) * Math.PI * h;
        }
        return 0;
    }

    public void iniciar(int segundos) {
        float caudalSalida = 0;
        float velocidad = velocidadSalida(0);
        for (Tubo tubo : salida) {
            caudalSalida += tubo.area() * velocidad;
        }
        float caudalEntrada = entradaCaudal;
        for (Tubo t : entrada) {
            caudalEntrada += t.caudal();
        }
        h += segundos * (caudalEntrada - caudalSalida) / (Math.PI * Math.pow(d / 2, 2));
    }

    public float presion() {
        if (h <= 0) {
            return 0;
        }
        return 10 + h;
    }

    public float z() {
        return z;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public String nombre() {
        return nombre;
    }

    public List<Tubo> salida() {
        return salida;
    }

    public void agregarEntradaConstante(float f) {
        entradaCaudal += f;
    }

}
