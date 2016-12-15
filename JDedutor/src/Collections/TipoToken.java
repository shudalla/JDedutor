/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Collections;

/**
 *
 * @author Shu
 */
public class TipoToken {
    private final String nome;
    private int linha;
    private final String tipo;
    
    public TipoToken(String nome, String tipo, int linha){
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
    }

    public int getLinha() {
        return linha;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }
    

    public void setLinha(int linha){
        this.linha = linha;
    }

    
    
}
