/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Collections;

import java.util.Stack;

/**
 *
 * @author Shu
 */
public class PilhaEscopo {
    private char chave;
    private Stack<TipoToken> escopo = new Stack<>();
    
    public PilhaEscopo(){}
    
    public PilhaEscopo( TipoToken token){
        this.escopo.push(token);
    }

    public char getChave() {
        return chave;
    }

    public void setChave(char chave) {
        this.chave = chave;
    }

    

    public Stack<TipoToken> getEscopo() {
        return escopo;
    }

    public void setEscopo(Stack<TipoToken> escopo) {
        this.escopo = escopo;
    }

    
}
