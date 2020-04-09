package leerArchivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class ImportarFile {

    private final String delimitador;
    private List<String> lineas;
    public int numCols;
    public int numRows;
    private String[][] archivo;
    private boolean abierto = false;
    private final File documento;
    boolean esMatrix;

    public ImportarFile(String ruta) throws IOException {
        this(ruta, "");
    }

    public ImportarFile(File file) throws IOException {
        this(file, "");
    }

    public ImportarFile(String ruta, String delimitador) throws IOException {
        this(new File(ruta), delimitador);
    }

    public ImportarFile(File file, String delimitador) throws IOException {
        documento = file;
        this.delimitador = delimitador;
        esMatrix = !delimitador.isEmpty();
        numRows = 0;
        numCols = 0;
        abrir();
    }

    private void abrir() throws IOException {
        lineas = new ArrayList<>();
        try (FileReader fr = new FileReader(documento)) {
            BufferedReader reader = new BufferedReader(fr);
            String linea = reader.readLine();
            while (linea != null) {
                lineas.add(linea);
                linea = reader.readLine();
            }
            abierto = true;
        }
        if (!lineas.isEmpty() && esMatrix) {
            numRows = lineas.size();
            numCols = max();
            if (numCols > 0) {
                aVector();
            }
        }
    }

    private int max() {
        int numero = lineas.get(0).split(delimitador).length;
        for (int i = 1; i < lineas.size(); i++) {
            int longitud = lineas.get(i).split(delimitador).length;
            if (longitud > numero) {
                numero = longitud;
            }
        }
        return numero;
    }

    private void aVector() {
        archivo = new String[numRows][numCols];
        for (int i = 0; i < archivo.length; i++) {
            archivo[i] = lineas.get(i).split(delimitador);
        }
    }

    public String[][] enCaracteres() {
        if (abierto) {
            if (esMatrix) {
                return archivo;
            }
        }
        return null;
    }

    public List<String> leer() {
        if (abierto) {
            return lineas;
        }
        return null;
    }

    public String[] lineas() {
        if (lineas.isEmpty()) {
            return null;
        }
        String[] linea = new String[lineas.size()];
        for (int i = 0; i < linea.length; i++) {
            linea[i] = lineas.get(i);
        }
        return linea;
    }

    public double[][] enNumeros(int inicioRow, int inicioCol, int finRow, int finCol) {
        if (abierto && !lineas.isEmpty()) {
            if (!esMatrix) {
                return null;
            }
            double[][] numeros = new double[numRows - inicioRow][numCols - inicioCol];
            for (int i = inicioRow; i < finRow; i++) {
                for (int j = inicioCol; j < finCol; j++) {
                    numeros[i - inicioRow][j - inicioCol] = Double.parseDouble(archivo[i][j]);
                }
            }
            return numeros;
        }
        return null;
    }

}
