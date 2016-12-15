/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Arquivo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shu
 */
public class Arquivo {

    private File arq;
    private static Arquivo arquivo;
    private String path;

    private Arquivo(String path) {
        // this.arq = new File("src/Persistencia/teste.txt");
        this.arq = new File(path);

    }

    public static Arquivo getArquivo(String path) {
       // if (arquivo == null) {
            return arquivo = new Arquivo(path);
        //}
        
    }

    public static Arquivo getArquivo() {
        return arquivo;
    }

    public ArrayList<String> getLinhasCodigo() throws FileNotFoundException {
        Scanner scan = new Scanner(arq);
        ArrayList<String> linhaCodigo = new ArrayList<>();

        while (scan.hasNextLine()) {
            String linha = scan.nextLine();
            Scanner scanline = new Scanner(linha);
            linhaCodigo.add(linha);

        }
        linhaCodigo.add("EOF");

        if (linhaCodigo == null) {
            throw new FileNotFoundException("Erro ao pegar linhas do aquivo");
        }

        return linhaCodigo;
    }

    public ArrayList<String> getLinhasCodigoBuffer() {

        FileReader fileReader;
        ArrayList<String> linhaCodigo = new ArrayList<>();
        String linha = null;

        try {
            fileReader = new FileReader(this.arq);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                linha = bufferedReader.readLine();
                linhaCodigo.add(linha);
            }
            linhaCodigo.add("EOF");
            bufferedReader.close();
            fileReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            new FileNotFoundException("Erro ao pegar linhas do aquivo");

        } catch (IOException ex) {
            Logger.getLogger(Arquivo.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

        return linhaCodigo;
    }

    public void addCodigoNoArquivo(String codigo) throws IOException {
        FileWriter w = new FileWriter(arq, false);
        BufferedWriter bf = new BufferedWriter(w);
        bf.write(codigo);
        bf.newLine();
        bf.close();

    }

    public File getArq() {
        return arq;
    }

    public void setArq(File arq) {
        this.arq = arq;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
