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
public class DuplaNegacao extends IRegras {

    private ArrayList<TipoToken> tokens;
    private Stack<TipoToken> pilhaLinha = new Stack<>();
    private Stack<TipoToken> pilha = new Stack<>();
    private Stack<PilhaEscopo> escopo = new Stack<>();
    private ArrayList<TipoToken> tokensTela = new ArrayList<>();
    private ArrayList<String> regras = new ArrayList<>();
    private int subescopo;
    private int indexAInserir;

    public DuplaNegacao() {
    }

    @Override
    public ArrayList<TipoToken> isRegra(ArrayList<TipoToken> tokens, ArrayList<String> regras) {
        this.tokens = tokens;
        this.regras = regras;
        int j = 0;

        int totalLinhas = this.tokens.get(this.tokens.size() - 2).getLinha();

        // PEGAR TOKENS POR LINHA
        for (int i = 1; i <= totalLinhas; i++) {
            while (tokens.size() > j && this.tokens.get(j).getLinha() == i) {
                this.pilhaLinha.push(new TipoToken(this.tokens.get(j).getNome(), this.tokens.get(j).getTipo(), i));
                j++;
            }
            if (!this.pilhaLinha.peek().getNome().equals("EOF")) {

                while (!this.pilhaLinha.empty()) {
                    this.adicionarPilha();
                }

                this.executar();
                this.limpaPilha();
                this.limpaEscopo();

            }
        }

        return this.tokensTela;
    }

    @Override
    public void executar() {

        for (TipoToken token : this.pilha) {
            if ("DUPLA_NEGACAO".equals(token.getTipo())) {
                this.regras.add(token.getLinha() + "     Dupla Negação");
                this.atualizaTokens();
                this.ordenaTokens();
                break;
            } else if ("ESCOPO".equals(token.getTipo())) {
                this.escopo(token.getNome().charAt(0));
            }

        }

        //this.atualizaTokensTela();
        this.tokensTela = this.ordenaTokens();

    }

    @Override
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

    @Override
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

    @Override
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

    public void escopo(char chave) {
        //chamar aaqui o método de get por id
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (TipoToken token : e.getEscopo()) {

            if (token.getTipo().equals("ESCOPO")) {

                this.escopo(token.getNome().charAt(0));

            } else if (token.getTipo().equals("DUPLA_NEGACAO")) {

                this.regras.add(token.getLinha() + "     Dupla Negação");
                if (!this.jaContemLinha(e.getEscopo())) {
                    this.atualizaTokens();
                    this.ordenaTokens();

                }
                break;
            }

        }

    }

    @Override
    public void atualizaTokens() {
        int ultimaLinha = this.tokens.get(tokens.size() - 1).getLinha();
        for (int i = this.pilha.size() - 1; i >= 0; i--) {
            if (this.pilha.get(i).getTipo().equals("ESCOPO")) {
                this.atualizaTokensSub(this.pilha.get(i).getNome().charAt(0), ultimaLinha);
            } else {
                if (!this.pilha.get(i).getTipo().equals("DUPLA_NEGACAO")) {
                    this.tokens.add(new TipoToken(
                            this.pilha.get(i).getNome(),
                            this.pilha.get(i).getTipo(),
                            ultimaLinha));
                }
            }

        }

    }

    public void atualizaTokensSub(char chave, int ultimaLinha) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (int i = e.getEscopo().size() - 1; i >= 0; i--) {

            if (e.getEscopo().get(i).getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = e.getEscopo().get(i).getNome().charAt(0);
                this.atualizaTokensSub(novachave, ultimaLinha);
            } else {
                if (!e.getEscopo().get(i).getTipo().equals("DUPLA_NEGACAO")) {
                    this.tokens.add(new TipoToken(
                            e.getEscopo().get(i).getNome(),
                            e.getEscopo().get(i).getTipo(),
                            ultimaLinha));

                }

            }
        }

    }

    @Override
    public void atualizaTokensTela() {
        PilhaEscopo e = new PilhaEscopo();

        while (!this.pilha.empty()) {

            if (this.pilha.peek().getTipo().equals("ESCOPO")) {
                e = this.getEscopoByID(this.pilha.peek().getNome().charAt(0));
                this.pilha.pop(); //marcar escopo visitado

                while (!e.getEscopo().empty()) {

                    if (e.getEscopo().peek().getTipo().equals("ESCOPO")) {

                        this.atualizasSubescopoTela(e.getEscopo().peek().getNome().charAt(0));
                        //this.tokensTela.add(this.pilha.peek());
                        //this.pilha.pop(); // para tirar o parenteses ) e coloca-lo na tela
                        e.getEscopo().pop();
                    } else {

                        this.tokensTela.add(e.getEscopo().peek());
                        e.getEscopo().pop();
                    }

                }
                this.escopo.remove(e);

            } else if (this.pilha.peek().getTipo().equals("NADA")) {
                this.pilha.pop();
            } else {
                this.tokensTela.add(this.pilha.peek());
                this.pilha.pop();

            }

        }

    }

    @Override
    public void atualizasSubescopoTela(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        while (!e.getEscopo().empty()) {

            if (e.getEscopo().peek().getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = e.getEscopo().peek().getNome().charAt(0);
                e.getEscopo().pop();
                this.atualizasSubescopoTela(novachave);
            } else {
                if (e.getEscopo().peek().getTipo().equals("NADA")) {
                    e.getEscopo().pop();
                } else {
                    this.tokensTela.add(e.getEscopo().peek());
                }

                e.getEscopo().pop();
            }

        }
        this.escopo.remove(e);

    }

    @Override
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

    @Override
    public PilhaEscopo getEscopoByID(char chave) {
        for (PilhaEscopo e : this.escopo) {
            if (e.getChave() == chave) {
                return e;
            }
        }
        return null;
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
            //  if (token.getLinha() != pilha.get(0).getLinha()) {//&& !token.getNome().equals("EOF")) {

            if (token.getLinha() == i) {
                tokensLinha += token.getNome();
            } else {

                if (i != linha.get(i).getLinha()) {

                    return true;
                }
                i++;
                tokensLinha = "";
                tokensLinha += token.getNome();
            }

            //}
        }

        return false;
    }

    @Override
    public void limpaEscopo(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        while (!e.getEscopo().empty()) {
            e.getEscopo().remove(0);
        }

    }

    @Override
    public void limpaEscopo() {
        //LIMPANDO ESCOPO
        while (!this.escopo.empty()) {
            this.escopo.pop();
        }
    }

    public ArrayList<TipoToken> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<TipoToken> tokens) {
        this.tokens = tokens;
    }

    public Stack<TipoToken> getPilhaLinha() {
        return pilhaLinha;
    }

    public void setPilhaLinha(Stack<TipoToken> pilhaLinha) {
        this.pilhaLinha = pilhaLinha;
    }

    public Stack<TipoToken> getPilha() {
        return pilha;
    }

    public void setPilha(Stack<TipoToken> pilha) {
        this.pilha = pilha;
    }

    public Stack<PilhaEscopo> getEscopo() {
        return escopo;
    }

    public void setEscopo(Stack<PilhaEscopo> escopo) {
        this.escopo = escopo;
    }

    public ArrayList<TipoToken> getTokensTela() {
        return tokensTela;
    }

    public void setTokensTela(ArrayList<TipoToken> tokensTela) {
        this.tokensTela = tokensTela;
    }

    public ArrayList<String> getRegras() {
        return regras;
    }

    public void setRegras(ArrayList<String> regras) {
        this.regras = regras;
    }

    @Override
    public boolean verificaRegraEscopo(char chave) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void limpaTela() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void limpaPilha() {
        while (!this.pilha.isEmpty()) {
            this.pilha.remove(0);
        }
    }

    @Override
    public void limpaPilha(Stack<TipoToken> pilha) {
        while (!pilha.isEmpty()) {
            pilha.remove(0);
        }
    }

    @Override
    public void aplicaRegra() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void aplicaRegraSub(char chave) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void atualizaTokensSub(char chave) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gravaPreConse(int index, Stack<TipoToken> pilha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gravaPreConseSubescopo(char chave, int preConse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean jaContemLinha(ArrayList<TipoToken> linha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean temIgual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
