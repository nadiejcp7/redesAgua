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
    public int numCols = 0, numRows = 0;
    private String[][] archivo;
    private boolean abierto = false;
    private final File documento;

    public ImportarFile(File file, String delimitador) throws IOException {
        this.documento = file;
        this.delimitador = delimitador;
        abrir();
    }

    private void abrir() throws IOException {
        List<String> lineas = new ArrayList<>();
        if (documento != null) {
            try (FileReader fr = new FileReader(documento)) {
                BufferedReader reader = new BufferedReader(fr);
                String linea = reader.readLine();
                while (linea != null) {
                    lineas.add(linea);
                    linea = reader.readLine();
                }
                abierto = true;
            }
            if (!lineas.isEmpty() && !delimitador.isEmpty()) {
                numRows = lineas.size();
                numCols = max(lineas);
                if (numCols > 0) {
                    aVector(lineas);
                }
            }
        }
    }

    private int max(List<String> lineas) {
        int numero = lineas.get(0).split(delimitador).length;
        for (int i = 1; i < lineas.size(); i++) {
            int longitud = lineas.get(i).split(delimitador).length;
            if (longitud > numero) {
                numero = longitud;
            }
        }
        return numero;
    }

    private void aVector(List<String> lineas) {
        archivo = new String[numRows][numCols];
        for (int i = 0; i < archivo.length; i++) {
            archivo[i] = lineas.get(i).split(delimitador);
        }
    }

    public String[][] enCaracteres() {
        if (abierto) {
            if (!delimitador.isEmpty()) {
                return archivo;
            }
        }
        return null;
    }

}
