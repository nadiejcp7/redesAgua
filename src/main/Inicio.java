package main;

import componentes.Nodo;
import componentes.Tanque;
import componentes.Tubo;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Jairo Cabrera Pino
 */
 
public class Inicio {

    static List<Nodo> nodos;
    static List<Tubo> tubos;
    static List<Tanque> tanques;

    public static void main(String[] args) throws InterruptedException, IOException, Exception {
        //Simple way to create a pipeline network
        //See files nodos.csv (for nodes) and tubos.csv (for pipelines) in resources
        nodos = CargarDatos.nodos("Nodos.csv", ",");
        tubos = CargarDatos.tubos("Tubos.csv", ",", nodos, null);
        for (Nodo n : nodos) {
            n.distribuirCaudales();
            for (Tubo tu1 : tubos) {
                tu1.caudal();
            }
        }
        final int segundos = 5;
        //execute the following just if you need to monitore in real time. For simple calculations, just run executeTask() method
        Timer timer = new Timer();
        TimerTask tt1 = new TimerTask() {
            @Override
            public void run() {
                executeTask();
            }
        };
        //executes task every 'segundos' seconds
        timer.scheduleAtFixedRate(tt1, 0, segundos * 1000);

        //if you have tanks, run as follows.
        nodos = CargarDatos.nodos("Nodos.csv", ",");
        tanques = CargarDatos.tanques("Tanques.csv", ",");//See file tanques.csv (for tanks) in resources
        tubos = CargarDatos.tubos("Tubos.csv", ",", nodos, tanques);
        for (Tanque n : tanques) {
            n.iniciar(segundos);
        }
        for (Nodo n : nodos) {
            n.distribuirCaudales();
            for (Tubo tu1 : tubos) {
                tu1.caudal();
            }
        }
        final int seconds = 5;
        //execute the following just if you need to monitore in real time. For simple calculations, just run executeTask() method
        Timer timer1 = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                executeTaskTanque(seconds);
            }
        };
        //executes task every 'segundos' seconds
        timer1.scheduleAtFixedRate(tt, 0, seconds * 1000);
    }

    private static void executeTask() {
        for (Nodo n : nodos) {
            n.calcular();
        }
        for (Tubo t : tubos) {
            //print parameters you need
            System.out.println("Pipeline: " + t.nombre() + " - Friction losses: " + t.hf() + " m.");
        }
        for (Nodo n : nodos) {
            //print parameters you need to evaluate
            System.out.println("Node " + n.nombre() + " - Pressure: " + n.presion() + " m.");
        }
        System.out.println("");
    }
    
    private static void executeTaskTanque(int segundos) {
        for (Tanque n : tanques) {
            n.iniciar(segundos);
        }
        for (Nodo n : nodos) {
            n.calcular();
        }
        for (Tanque ta : tanques) {
            //print parameters you need
            System.out.println("Tank: " + ta.nombre() + " - Water volume: " + ta.volumeAgua() + " m.");
        }
        for (Tubo t : tubos) {
            //print parameters you need
            System.out.println("Pipeline: " + t.nombre() + " - Friction losses: " + t.hf() + " m.");
        }
        for (Nodo n : nodos) {
            //print parameters you need to evaluate
            System.out.println("Node " + n.nombre() + " - Pressure: " + n.presion() + " m.");
        }
        System.out.println("");
    }
   
}
    
