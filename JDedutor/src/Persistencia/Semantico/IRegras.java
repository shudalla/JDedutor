/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Semantico;

import Collections.PilhaEscopo;
import Collections.TipoToken;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Shu
 */
public abstract class IRegras {

    public abstract ArrayList<TipoToken> isRegra(ArrayList<TipoToken> tokens, ArrayList<String> regras);

    public abstract void executar();

    public abstract void adicionarPilha();

    public abstract void aplicaRegra();

    public abstract void aplicaRegraSub(char chave);

    public abstract void atualizaTokens();

    public abstract void atualizaTokensSub(char chave);

    public abstract void atualizaTokensTela();

    public abstract void atualizasSubescopoTela(char chave);

    public abstract void criaEscopo();

    public abstract void criaSubescopo(char chave);

    public abstract void gravaPreConse(int index, Stack<TipoToken> pilha);

    public abstract void gravaPreConseSubescopo(char chave, int preConse);

    public abstract PilhaEscopo getEscopoByID(char chave);

    public abstract Boolean jaContemLinha(ArrayList<TipoToken> linha);

    public abstract void limpaTela();

    public abstract void limpaEscopo(char c);

    public abstract void limpaEscopo();

    public abstract void limpaPilha();
    
    public abstract void limpaPilha(Stack<TipoToken> pilha);

    public abstract boolean verificaRegraEscopo(char chave);

    public abstract boolean temIgual();

    public abstract ArrayList<TipoToken> ordenaTokens();
    

}
