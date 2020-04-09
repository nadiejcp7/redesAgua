/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentes;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class Tanque {

    final float z, x, y;
    private final float d, hMax, g;
    private float h, caudalEntrada, caudalPrevio;
    private List<Tubo> salida;
    String nombre;

    public Tanque(String nombre, float vol, float hMax, float h, float x, float y, float z) {
        this.nombre = nombre;
        this.z = z;
        this.x = x;
        this.y = y;
        this.d = (float) (Math.sqrt(vol / (hMax * Math.PI)) * 2);
        this.hMax = hMax;
        this.h = h;
        this.g = 9.8F;
        salida = new ArrayList<>();
        caudalEntrada = caudalPrevio = 0;
    }

    public Tanque(String[] tanque) {
        this(tanque[0], Float.parseFloat(tanque[8]), Float.parseFloat(tanque[7]), Float.parseFloat(tanque[6]),
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
     * @param entrada el caudal que ingresa al tanque en m3/s
     */
    public void agregarEntrada(float entrada) {
        caudalEntrada += entrada;
    }

    public double volume() {
        if (h > 0) {
            return Math.pow(d / 2, 2) * Math.PI * h;
        }
        return 0;
    }

    public void iniciar() {
        final int segundos = 5;
        Timer t = new Timer();
        TimerTask tt1 = new TimerTask() {
            @Override
            public void run() {
                float caudalSalida = 0;
                float velocidad = velocidadSalida(0);
                for (Tubo tubo : salida) {
                    caudalSalida += Math.pow(tubo.diametro() / 2, 2) * velocidad;
                }
                h += segundos * (caudalEntrada - caudalSalida) / (Math.PI * Math.pow(d / 2, 2));
                if (h < 0) {
                    h = 0;
                } else if (h >= hMax) {
                    h = hMax;
                    caudalPrevio = caudalEntrada;
                    caudalEntrada = 0;
                } else {
                    if (caudalEntrada == 0) {
                        caudalEntrada = caudalPrevio;
                    }
                }
            }
        };
        t.scheduleAtFixedRate(tt1, 0, segundos * 1000);
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

}
