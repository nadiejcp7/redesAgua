package main;

import leerArchivos.CargarDatos;
import componentes.Nodo;
import componentes.Tanque;
import componentes.Tubo;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Jairo Cabrera Pino
 */
 
public class Inicio {

    List<Nodo> nodos;
    List<Tubo> tubos;
    List<Tanque> tanques;

    public Inicio(String[] data) throws Exception {
        File[] f = new File(data[0]).listFiles();
        nodos = CargarDatos.nodos(findFile("nodes", f), data[1]);
        tanques = CargarDatos.tanques(findFile("tanks", f), data[1]);
        tubos = CargarDatos.tubos(findFile("pipelines", f), data[1], nodos, tanques);
    }

    private void executeTask() {
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

    private void executeTaskTanque(int segundos) {
        for (Nodo n : nodos) {
            n.calcular();
        }
        for (Tanque ta : tanques) {
            //print parameters you need
            System.out.println("Tank: " + ta.nombre() + " - Water volume: " + ta.volumeAgua() + " m3.");
        }
        for (Tubo t : tubos) {
            //print parameters you need
            System.out.println("Pipeline: " + t.nombre() + " - Friction losses: " + t.hf() + " m. Flow: " + t.caudal());
        }
        for (Nodo n : nodos) {
            //print parameters you need to evaluate
            System.out.println("Node " + n.nombre() + " - Pressure: " + n.presion() + " m.");
        }
        for (Tanque n : tanques) {
            n.iniciar(segundos);
        }
        System.out.println("");
    }

    public void ejercicio() throws Exception {
        if(tubos.isEmpty() || nodos.isEmpty()){
            throw new Exception("Files where pipelines or nodes info are corrupted or do not exist");
        }
        for (Tubo tu1 : tubos) {
            tu1.caudal();
        }
        for (Nodo n : nodos) {
            n.distribuirCaudales();
            for (Tubo tu1 : tubos) {
                tu1.caudal();
            }
        }
        final int seconds = 5;
        Timer timer1 = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (tanques.isEmpty()) {
                    executeTask();
                } else {
                    executeTaskTanque(seconds);
                }
            }
        };
        //executes task every 'segundos' seconds
        timer1.scheduleAtFixedRate(tt, 0, seconds * 1000);
    }

    private File findFile(String name, File[] f) {
        for (File f1 : f) {
            if (f1.getName().startsWith(name)) {
                return f1;
            }
        }
        return null;
    }
 
    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            throw new Exception("There is not enough arguments for this program to start. You need to load directory where files are located");
        } else {
            Inicio inicio = new Inicio(args);
            inicio.ejercicio();
        }
    }
}
