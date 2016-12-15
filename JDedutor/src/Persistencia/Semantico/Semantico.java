/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Semantico;

import Collections.PilhaEscopo;
import Collections.TipoToken;
import Persistencia.Main;
import Telas.MainTela;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Shu
 */
public class Semantico {

    protected static Semantico analisador;
    private ArrayList<TipoToken> tokens;
    private Stack<TipoToken> novasLinhas;
    private Stack<TipoToken> pilhaLinha;
    private Stack<TipoToken> pilha;
    private ArrayList<TipoToken> tokensTela;
    private Stack<PilhaEscopo> escopo;
    private ArrayList<String> regras;
    private int subescopo;
    private int indexAInserir;

    private MainTela tela;

    private Semantico(Main main) {
        this.novasLinhas = new Stack<>();
        this.regras = new ArrayList<>();
        this.pilhaLinha = new Stack<>();
        this.pilha = new Stack<>();
        this.tokensTela = new ArrayList<>();
        this.escopo = new Stack<>();
        this.tokens = main.getTokensSemantico();
        this.tela = main.getTela();

        //this.tokensPorLinha(main);
    }

    public static Semantico getAnalisador(Main main) {
        if (analisador == null) {
            return new Semantico(main);
        }
        return analisador;
    }

    //externo
    public void executarSemantico() {
        IRegras dn = new DuplaNegacao();
        IRegras mp = new ModusPonens();
        IRegras mt = new ModusTolens();

        /*int tam = 0;
         this.tokensTela = dn.isRegra(tokens, this.regras);

         tam = tokensTela.get(tokensTela.size() - 1).getLinha();
         this.tokensTela.add(new TipoToken("EOF", "FIM", tam + 1));
         this.tokensTela = mp.isRegra(tokensTela, this.regras);

         tam = tokensTela.get(tokensTela.size() - 1).getLinha();
         this.tokensTela.add(new TipoToken("EOF", "FIM", tam + 1));
         this.tokensTela = mt.isRegra(tokensTela, this.regras);*/
        this.preparaPilhas();
        int tam = tokens.size();
        this.tokensTela = dn.isRegra(tokens, this.regras);

        if (tokensTela.size() != tam) {
            tam = tokensTela.get(tokensTela.size() - 1).getLinha();
            this.tokensTela.add(new TipoToken("EOF", "FIM", tam + 1));
            tam++;
        }
        this.tokensTela = mp.isRegra(tokensTela, this.regras);

        if (tokensTela.size() != tam) {
        tam = tokensTela.get(tokensTela.size() - 1).getLinha();
        this.tokensTela.add(new TipoToken("EOF", "FIM", tam + 1));
        }
        this.tokensTela = mt.isRegra(tokensTela, this.regras);

        //EXIBIR NA TELA
        String tokensTelaString = "";
        for (int i = 0; i < this.tokensTela.size(); i++) {
            if (!this.tokensTela.get(i).getNome().equals("EOF")) {
                if (i + 1 < this.tokensTela.size() && this.tokensTela.get(i).getLinha() == this.tokensTela.get(i + 1).getLinha()) {
                    tokensTelaString += this.tokensTela.get(i).getNome() + " ";
                } else {
                    tokensTelaString += this.tokensTela.get(i).getNome() + "\n";
                }
            }
        }

        String regrasTela = "";
        while (!this.regras.isEmpty()) {
            regrasTela += this.regras.get(0) + "\n";
            this.regras.remove(0);
        }

        this.tela.getRespostajTextArea().setText(tokensTelaString);
        this.tela.getRegrasTextArea().setText(regrasTela);
    }

    public PilhaEscopo getEscopoByID(char chave) {
        for (PilhaEscopo e : this.escopo) {
            if (e.getChave() == chave) {
                return e;
            }
        }
        return null;
    }

    public void adicionarPilha() {

        if (this.pilhaLinha.peek().getNome().equals(")")) {
            this.pilha.push(this.pilhaLinha.peek());
            this.pilhaLinha.pop();
            subescopo = 1;
            this.criaEscopo();
        } else {
            this.pilha.push(pilhaLinha.peek());
            pilhaLinha.pop();
        }

    }

    public void criaEscopo() {
        char chave = ' ';
        if (this.escopo.isEmpty()) {
            this.escopo.push(new PilhaEscopo());
            this.escopo.peek().setChave('A');
            chave = this.escopo.peek().getChave();
            this.pilha.push(new TipoToken(chave + "", "ESCOPO", 0));
        } else {
            chave = this.escopo.peek().getChave();
            chave++;
            this.escopo.push(new PilhaEscopo());
            this.escopo.peek().setChave(chave);
            this.pilha.push(new TipoToken(chave + "", "ESCOPO", 0));
        }

        PilhaEscopo e = this.getEscopoByID(chave);

        //abre um parenteses que ja foi colocado na pilha e retirado da pilha linha
        while (!this.pilhaLinha.isEmpty() && !this.pilhaLinha.peek().getNome().equals("(")) {

            if (this.pilhaLinha.peek().getNome().equals(")")) {
                this.subescopo++;
                //coloco o ) no escopo para comecar o proximo
                e.getEscopo().push(this.pilhaLinha.peek());
                this.pilhaLinha.pop();
                chave = this.escopo.peek().getChave();
                chave++;
                e.getEscopo().push(new TipoToken(chave + "", "ESCOPO", 0));
                this.criaSubescopo(chave);
            } else {
                e.getEscopo().push(this.pilhaLinha.peek());
                this.pilhaLinha.pop();
            }
        }

        //fecha o ultimo parenteses da pilha
        if (!this.pilhaLinha.isEmpty()) {
            this.subescopo--;
            this.pilha.push(this.pilhaLinha.peek());
            this.pilhaLinha.pop();
        }

    }

    public void criaSubescopo(char chave) {
        this.escopo.push(new PilhaEscopo());
        this.escopo.peek().setChave(chave);
        PilhaEscopo e = this.getEscopoByID(chave);

        while (!this.pilhaLinha.isEmpty() && !this.pilhaLinha.peek().getNome().equals("(")) {

            if (this.pilhaLinha.peek().getNome().equals(")")) {
                this.subescopo++;
                //coloco o ) no escopo para comecar o proximo
                e.getEscopo().push(this.pilhaLinha.peek());
                this.pilhaLinha.pop();
                chave++;
                e.getEscopo().push(new TipoToken(chave + "", "ESCOPO", 0));
                this.criaSubescopo(chave);
            } else {
                //pego o escopo atual para analisar
                e.getEscopo().push(this.pilhaLinha.peek());
                this.pilhaLinha.pop();
            }
        }

        //fecha o ultimo parenteses da pilha
        if (!this.pilhaLinha.isEmpty()) {
            this.subescopo--;
            this.escopo.get(subescopo - 1).getEscopo().push(this.pilhaLinha.peek());
            this.pilhaLinha.pop();

        }

    }

    public void preparaPilhas() {
        int totalLinhas = this.tokens.get(this.tokens.size() - 1).getLinha();
        int j = 0;
        List<TipoToken> tokensAux = new ArrayList<>();

        //para que possamos alterear e atualizar o vetor de tokens enquanto o avaliamos
        tokensAux = (List<TipoToken>) (this.tokens.clone());

        for (int i = 1; i <= totalLinhas; i++) {
            while (tokensAux.size() > j && tokensAux.get(j).getLinha() == i) {
                this.pilhaLinha.push(new TipoToken(tokensAux.get(j).getNome(), tokensAux.get(j).getTipo(), i));
                j++;
            }
            if (!this.pilhaLinha.isEmpty()
                    && !this.pilhaLinha.peek().getNome().equals("EOF")) {

                while (!this.pilhaLinha.empty()) {
                    this.adicionarPilha();
                }

                this.testaSimplifica(pilha, 0);
                this.limpaParenteses(pilha);
                this.limpaPilha();
                this.limpaEscopo();

            }
        }
        this.tokensTela = this.ordenaTokens();

    }

    public void testaSimplifica(Stack<TipoToken> pilha, int indexFinal) {
        if (this.pilha.size() > 2) {

            for (int i = indexFinal; i < pilha.size(); i++) {

                if (pilha.get(i).getNome().equals("^")) {
                    this.simplifica(pilha, 0);
                    break;

                }

            }
        }
    }

    public void simplifica(Stack<TipoToken> pilha, int indexFinal) {
        int i;
        Stack<TipoToken> pilhaAux = new Stack<>();
        int ultimaLinha = this.tokens.get(this.tokens.size() - 1).getLinha();

        for (i = indexFinal; i < pilha.size(); i++) {

            if (pilha.get(i).getNome().equals("^")) {
                //indexFinal = i;
                int j = i - 1;
                //grava o token depois
                while (j >= indexFinal) {

                    if (pilha.get(j).getTipo().equals("ESCOPO")) {

                        PilhaEscopo e = this.getEscopoByID(pilha.get(j).getNome().charAt(0));
                        this.simplificaSubEscopo(pilha.get(j).getNome().charAt(0), pilhaAux);
                    } else {
                        pilhaAux.add(new TipoToken(pilha.get(j).getNome(), pilha.get(j).getTipo(), ultimaLinha + 1));

                    }
                    j--;

                }
                if (!pilhaAux.isEmpty()) {
                    // this.limpaParenteses(pilhaAux);

                    if (!this.jaContemLinha(pilhaAux)) {
                        for (TipoToken pilhaAux1 : pilhaAux) {
                            this.tokens.add(new TipoToken(pilhaAux1.getNome(), pilhaAux1.getTipo(), ultimaLinha + 1));

                        }
                        this.regras.add(this.pilha.get(0).getLinha() + "     Simplificação");

                    }
                    this.simplifica(pilha, i + 1);
                    break;
                }

            }
        }

        if (i == pilha.size()) {
            int x;
            Stack<TipoToken> pilhaAux2 = new Stack<>();
            for (x = pilha.size() - 1; x >= indexFinal; x--) {
                if (pilha.get(x).getTipo().equals("ESCOPO")) {
                    this.simplificaSubEscopo(pilha.get(x).getNome().charAt(0), pilhaAux2);

                } else {
                    pilhaAux2.add(new TipoToken(pilha.get(x).getNome(), pilha.get(x).getTipo(), ultimaLinha + 1));
                    //this.tokens.add(new TipoToken(pilha.get(x).getNome(), pilha.get(x).getTipo(), ultimaLinha + 1));
                }
            }

            // this.limpaParenteses(pilhaAux2);
            if (!this.jaContemLinha(pilhaAux2)) {
                //break;

                for (TipoToken pilhaAux1 : pilhaAux2) {
                    this.tokens.add(new TipoToken(pilhaAux1.getNome(), pilhaAux1.getTipo(), ultimaLinha + 1));
                }
                this.regras.add(this.pilha.get(0).getLinha() + "     Simplificação");

            }

        }

    }

    public void simplificaSubEscopo(char chave, Stack<TipoToken> pilha) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        int ultimaLinha = this.tokens.get(this.tokens.size() - 1).getLinha();

        for (int i = e.getEscopo().size() - 1; i >= 0; i--) {
            if (e.getEscopo().get(i).getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = e.getEscopo().get(i).getNome().charAt(0);
                this.simplificaSubEscopo(novachave, pilha);
            } else {
                pilha.add(new TipoToken(e.getEscopo().get(i).getNome(), e.getEscopo().get(i).getTipo(), ultimaLinha + 1));
            }
        }

    }

    public void atualizaTokens() {
        int linhaAInserir = this.pilha.peek().getLinha();
        indexAInserir = 0;

        //acha a linha a ser removida
        while (this.tokens.get(indexAInserir).getLinha() != linhaAInserir) {
            indexAInserir++;
        }
        //remove a linha
        while (this.tokens.get(indexAInserir).getLinha() == linhaAInserir) {
            this.tokens.remove(indexAInserir);
        }

        //para colocar no token precisa ser um for ao contrario
        for (int i = this.pilha.size() - 1; i >= 0; i--) {
            if (this.pilha.get(i).getTipo().equals("ESCOPO")) {
                this.atualizaTokensSub(this.pilha.get(i).getNome().charAt(0));
            } else {
                this.tokens.add(indexAInserir++, this.pilha.get(i));
            }

        }

    }

    public void atualizaTokensSub(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (TipoToken token : e.getEscopo()) {

            if (token.getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = token.getNome().charAt(0);
                this.atualizaTokensSub(novachave);
            } else {
                this.tokens.add(indexAInserir++, token);
            }
        }

    }

    public ArrayList<TipoToken> ordenaTokens() {
        int i = 0;
        ArrayList<TipoToken> tokensOrdenados = new ArrayList<>();

        while (!this.tokens.get(i).getNome().equals("EOF")) {
            i++;
        }

        this.tokens.remove(i);
        i = 1;
        int linhaAtual = tokens.get(0).getLinha();
        for (TipoToken tokensOrdenado : this.tokens) {
            if (tokensOrdenado.getLinha() == linhaAtual) {
                tokensOrdenado.setLinha(i);
            } else {
                i++;
                linhaAtual = tokensOrdenado.getLinha();
                tokensOrdenado.setLinha(i);
            }

        }
        this.tokens.add(new TipoToken("EOF", "FIM", i + 1));

        return tokens;
    }

    public Boolean jaContemLinha(Stack<TipoToken> linha) {
        int totalLinhas = 0;
        //descobrir a maior linha 
        for (TipoToken token : tokens) {
            if (token.getLinha() > totalLinhas) {
                totalLinhas = token.getLinha();

            }
        }
        //para desconsiderar o EOF
        totalLinhas--;
        String consequenteString = "";
        int j = 0;

        for (TipoToken linha1 : linha) {
            consequenteString += linha1.getNome();
        }

        String tokensLinha = "";
        //para saber de qual linha começar a pesquisar
        int i = 0;
        for (TipoToken token : tokens) {
            if (token.getLinha() != 0) {
                i = token.getLinha();
                break;
            }
        }

        for (TipoToken token : tokens) {
            if (token.getLinha() != pilha.get(0).getLinha()) {//&& !token.getNome().equals("EOF")) {

                if (token.getLinha() == i) {
                    tokensLinha += token.getNome();
                } else {

                    if (tokensLinha.equalsIgnoreCase(consequenteString)) {

                        return true;
                    }

                    i++;
                    tokensLinha = "";
                    tokensLinha += token.getNome();
                }

            }

        }

        return false;
    }

    public void limpaParenteses(Stack<TipoToken> pilha) {
        int i = pilha.size() - 1;

        if (!this.escopo.empty() && pilha.size() > 2) {
            //caso a pilha ja comece num escopo desnecessário
            if ((pilha.size() == 3 && pilha.get(1).getTipo().equals("ESCOPO"))
                    || pilha.size() < 3) {
                pilha.get(1).setLinha(-1);
                this.limpaParentesesSubescopo(pilha.get(1).getNome().charAt(0));

                PilhaEscopo e = new PilhaEscopo();
                e = this.getEscopoByID(pilha.get(1).getNome().charAt(0));
                if (pilha.get(1).getLinha() == -1) {
                    this.limpaPilha();

                    while (!e.getEscopo().empty()) {
                        pilha.add(new TipoToken(
                                e.getEscopo().get(0).getNome(),
                                e.getEscopo().get(0).getTipo(),
                                e.getEscopo().get(0).getLinha()));
                        e.getEscopo().remove(0);
                    }

                    this.atualizaTokens();
                }

            } else {
                //caso no decorrer da pilha eu tenha parenteses
                while (i >= 0) {

                    if (pilha.get(i).getTipo().equals("ESCOPO")) {
                        pilha.get(i).setLinha(-1);
                        this.limpaParentesesSubescopo(pilha.get(i).getNome().charAt(0));
                        pilha.get(i).setLinha(pilha.get(0).getLinha());

                        //verificar se o A ja na pilha é desnecessario
                        PilhaEscopo e = new PilhaEscopo();
                        e = this.getEscopoByID(pilha.get(i).getNome().charAt(0));
                        if ((e.getEscopo().size() == 3 && e.getEscopo().get(1).getTipo().equals("ESCOPO"))
                                || e.getEscopo().size() < 3) {
                            //removendo os parenteses desnecessarios
                            pilha.remove(i - 1);
                            pilha.remove(i);
                            i--;
                            this.atualizaTokens();
                        }

                    }
                    i--;

                }

            }
        }

    }

    public void limpaParentesesSubescopo(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (TipoToken token : e.getEscopo()) {

            if (e.getEscopo().size() == 3 && token.getTipo().equals("ESCOPO")) {
                token.setLinha(-1);
                this.limpaParentesesSubescopo(token.getNome().charAt(0));
                break;
            } else if (token.getTipo().equals("ESCOPO")) {
                this.limpaParentesesSubescopo(token.getNome().charAt(0));
            }
        }

        //achando o primeiro escopo desnecessário
        for (PilhaEscopo es : this.escopo) {
            //para casos de (A) com A escopo
            if (es.getEscopo().size() == 3) {
                if (es.getEscopo().get(1).getLinha() == -1) {
                    //para o primeiro onde será inserido o ultimo escopo pesquisado no while anterior
                    //verifica se há algo para ser recuperado no escopo

                    this.limpaEscopo(es.getChave());

                    while (!e.getEscopo().empty()) {
                        es.getEscopo().add(new TipoToken(
                                e.getEscopo().get(0).getNome(),
                                e.getEscopo().get(0).getTipo(),
                                e.getEscopo().get(0).getLinha()));
                        e.getEscopo().remove(0);
                    }

                    //limpa os outros escopos nao utieis mais 
                    int i = this.escopo.size() - 1;
                    //uso i>0 prar que nao remova o escopo A neste momento
                    while (i > 0) {
                        if (this.escopo.get(i).getEscopo().empty()) {
                            this.escopo.remove(i);
                        } else if (this.escopo.get(i).getEscopo().size() > 3
                                && this.escopo.get(i).getEscopo().get(1).getLinha() != -1) {
                            this.escopo.remove(i);
                        }
                        i--;
                    }
                    break;
                }
            }
        }

    }

    public void limpaPilha() {
        while (!this.pilha.isEmpty()) {
            this.pilha.remove(0);
        }
    }

    public void limpaPilha(Stack<TipoToken> pilha) {
        while (!pilha.isEmpty()) {
            pilha.remove(0);
        }
    }

    public void limpaEscopo(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        while (!e.getEscopo().empty()) {
            e.getEscopo().remove(0);
        }

    }

    public void limpaEscopo() {
        //LIMPANDO ESCOPO
        while (!this.escopo.empty()) {
            this.escopo.pop();
        }
    }

}
