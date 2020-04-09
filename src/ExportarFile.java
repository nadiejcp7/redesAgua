/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leerArchivos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Jairo Cabrera Pino
 */
public class ExportarFile {

    private final String ruta, delimitador;
    boolean tieneDelimitador = false;

    public ExportarFile(String ruta) {
        this.ruta = ruta;
        this.delimitador = "";
        File f = new File(ruta);
        if (f.exists()) {
            f.delete();
        }
    }

    public ExportarFile(String ruta, String delimitador) {
        this.ruta = ruta;
        this.delimitador = delimitador;
        File f = new File(ruta);
        if (f.exists()) {
            f.delete();
        }
        tieneDelimitador = true;
    }

    public void exportarDatos(String[][] datos) {
        if (tieneDelimitador) {
            FileWriter fichero = null;
            try {
                fichero = new FileWriter(ruta);
                PrintWriter pw = new PrintWriter(fichero);
                for (String[] dato1 : datos) {
                    String linea = "";
                    for (String dato : dato1) {
                        linea = linea + dato + delimitador;
                    }
                    pw.println(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != fichero) {
                        fichero.close();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public void exportarDatos(double[][] datos) {
        if (tieneDelimitador) {
            String[][] letras = new String[datos.length][max(datos)];
            for (int i = 0; i < letras.length; i++) {
                for (int j = 0; j < letras[i].length; j++) {
                    letras[i][j] = String.valueOf(datos[i][j]);
                }
            }
            exportarDatos(letras);
        }
    }

    public void exportarDatos(String[] datos) {
        FileWriter fichero = null;
        try {
            fichero = new FileWriter(ruta);
            PrintWriter pw = new PrintWriter(fichero);
            for (String dato : datos) {
                pw.println(dato);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (fichero != null) {
                    fichero.close();
                }
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }

    public void exportarDatos(List<String> datos) {
        FileWriter fichero = null;
        try {
            fichero = new FileWriter(ruta);
            PrintWriter pw = new PrintWriter(fichero);
            for (String dato : datos) {
                pw.println(dato);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }

    private int max(double[][] datos) {
        int numero = datos[0].length;
        for (int i = 1; i < datos.length; i++) {
            int longitud = datos[i].length;
            if (longitud > numero) {
                numero = longitud;
            }
        }
        return numero;
    }
}
