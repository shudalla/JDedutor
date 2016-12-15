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
public class ModusPonens extends IRegras {

    private ArrayList<TipoToken> tokens;
    private Stack<TipoToken> pilhaLinha = new Stack<>();
    private Stack<TipoToken> pilha = new Stack<>();
    private Stack<PilhaEscopo> escopo = new Stack<>();
    private ArrayList<TipoToken> tokensTela = new ArrayList<>();
    private ArrayList<TipoToken> precedente;
    private ArrayList<TipoToken> consequente;
    private ArrayList<String> regras;
    private int subescopo;
    private int novoPrecedente = 0;
    private int linhaEliminadaSeta = 0;

    public ModusPonens() {
    }

    @Override
    public ArrayList<TipoToken> isRegra(ArrayList<TipoToken> tokens, ArrayList<String> regras) {
        this.tokens = tokens;
        this.regras = regras;
        int totalLinhas = this.tokens.get(this.tokens.size() - 1).getLinha();
        int j = 0;

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
        this.tokensTela = this.ordenaTokens();
        return this.tokensTela;
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

    @Override
    public void executar() {
        if (this.tokens.get(this.tokens.size() - 1).getLinha() > 1) {

            //while (!this.pilha.empty()) {
            for (TipoToken token : pilha) {

                if (token.getNome().equals("->")) {

                    this.gravaPreConse(pilha.indexOf(token), this.pilha);
                    if (!this.jaContemLinha(this.consequente)) {

                        if (this.temIgual()) {

                            System.out.println("tem igual");
                            this.aplicaRegra();
                            this.limpaTela();
                            this.limpaPilha();
                            this.isRegra(this.ordenaTokens(), this.regras);
                            break;
                        } else {

                            this.ordenaTokens();

                        }

                    } else {
                        break;
                    }

                } else if (token.getTipo().equals("ESCOPO")) {
                    //agora eu consumo o escopo e depois a pilha continua 
                    char chave = token.getNome().charAt(0);

                    if (this.verificaRegraEscopo(chave)) {
                        if (this.temIgual()) {

                            System.out.println("tem igual");
                            this.aplicaRegraSub(chave);
                            this.limpaTela();
                            this.limpaPilha();
                            this.isRegra(this.ordenaTokens(), this.regras);
                            break;

                        } else {
                            this.ordenaTokens();
                            // break;
                        }
                    }

                }
            }
        }

        this.limpaPilha();
    }

    @Override
    public void atualizaTokens() {

        //para colocar no token precisa ser um fo ao contrario
        //for (TipoToken token : this.pilha) {
        for (int i = this.pilha.size() - 1; i >= 0; i--) {
            if (this.pilha.get(i).getTipo().equals("ESCOPO")) {
                this.atualizaTokensSub(this.pilha.get(i).getNome().charAt(0));
            } else {
                this.tokens.add(this.pilha.get(i));
            }

        }

        // }
    }

    @Override
    public void atualizaTokensSub(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (TipoToken token : e.getEscopo()) {

            if (token.getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = token.getNome().charAt(0);
                this.atualizaTokensSub(novachave);
            } else {
                this.tokens.add(token);
            }
        }

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
    public void gravaPreConse(int index, Stack<TipoToken> pilha) {
        precedente = new ArrayList<>();
        consequente = new ArrayList<>();
        PilhaEscopo e = new PilhaEscopo();
        this.linhaEliminadaSeta = pilha.get(index).getLinha();

        index = pilha.size() - 1;

        while (!pilha.get(index).getNome().equals("->")) {
            if (pilha.get(index).getTipo().equals("ESCOPO")) {
                e = this.getEscopoByID(pilha.get(index).getNome().charAt(0));
                index--;
                this.gravaPreConseSubescopo(e.getChave(), 1);

            }
            this.precedente.add(new TipoToken(
                    pilha.get(index).getNome(),
                    pilha.get(index).getTipo(),
                    pilha.get(index).getLinha()));
            index--;
        }

        //PARA REMOVER O "->"
        index--;

        while (index >= 0) {
            if (pilha.get(index).getTipo().equals("ESCOPO")) {
                e = this.getEscopoByID(pilha.get(index).getNome().charAt(0));
                index--; //marcar escopo visitado
                this.gravaPreConseSubescopo(e.getChave(), 2);

            }
            this.consequente.add(new TipoToken(
                    pilha.get(index).getNome(),
                    pilha.get(index).getTipo(),
                    pilha.get(index).getLinha()));
            index--;
        }

    }

    @Override
    public void gravaPreConseSubescopo(char chave, int preConse) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);
        int index = e.getEscopo().size() - 1;

        while (index >= 0) {

            if (e.getEscopo().get(index).getTipo().equals("ESCOPO")) {

                // marcar como escopo acabado 
                char novachave = e.getEscopo().get(index).getNome().charAt(0);
                index--;
                this.gravaPreConseSubescopo(novachave, preConse);
            } else if (preConse == 1) {
                this.precedente.add(e.getEscopo().get(index));
            } else {
                this.consequente.add(e.getEscopo().get(index));
            }

            index--;

        }

    }

    @Override
    public boolean temIgual() {

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

        for (TipoToken consequente1 : this.precedente) {
            consequenteString += consequente1.getNome();
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

            if (token.getLinha() == i) {
                tokensLinha += token.getNome();
            } else {

                if (tokensLinha.equalsIgnoreCase(consequenteString)) {

                    this.regras.add(linhaEliminadaSeta + "," + i + "     Modus Ponens");

                    return true;
                }

                i++;
                tokensLinha = "";
                tokensLinha += token.getNome();
            }

        }
        return false;
    }

    @Override
    public void aplicaRegra() {
        int ultimaLinha = this.tokens.get(tokens.size()-1).getLinha();
        
        while (!this.consequente.isEmpty()) {
            this.tokens.add(new TipoToken(
                    this.consequente.get(0).getNome(),
                    this.consequente.get(0).getTipo(),
                    ultimaLinha));
            this.consequente.remove(0);
        }

    }

    @Override
    public void aplicaRegraSub(char chave) {
        int ultimaLinha = this.tokens.get(tokens.size()-1).getLinha();
        
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        while (!e.getEscopo().empty()) {
            e.getEscopo().pop();
        }

        while (!this.consequente.isEmpty()) {
            e.getEscopo().add(new TipoToken(
                    this.consequente.get(0).getNome(),
                    this.consequente.get(0).getTipo(),
                    ultimaLinha));
            this.consequente.remove(0);
        }

        this.atualizaTokens();

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
    public boolean verificaRegraEscopo(char chave) {
        PilhaEscopo e = new PilhaEscopo();
        e = this.getEscopoByID(chave);

        for (TipoToken token : e.getEscopo()) {

            if (token.getTipo().equals("ESCOPO")) {

                this.verificaRegraEscopo(token.getNome().charAt(0));

            } else if (token.getNome().equals("->")) {

                this.gravaPreConse(e.getEscopo().indexOf(token), e.getEscopo());
                return true;

            }
        }
        return false;
    }

    @Override
    public Boolean jaContemLinha(ArrayList<TipoToken> linha) {
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
            //   if (token.getLinha() != linha.get(0).getLinha()) {

            if (token.getLinha() == i) {
                tokensLinha += token.getNome();
            } else {

                if (tokensLinha.equalsIgnoreCase(consequenteString)) {
                    
                    if (i != linha.get(0).getLinha()) {
                        
                        return true;
                    }

                }

                i++;
                tokensLinha = "";
                tokensLinha += token.getNome();
            }

          //  }
        }

        return false;
    }

    @Override
    public void limpaTela() {
        while (!this.tokensTela.isEmpty()) {
            this.tokensTela.remove(0);
        }
    }

    @Override
    public void limpaPilha() {
        while (!this.pilha.isEmpty()) {
            this.pilha.remove(0);
        }
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

    public ArrayList<TipoToken> getProcedente() {
        return precedente;
    }

    public void setProcedente(ArrayList<TipoToken> precedente) {
        this.precedente = precedente;
    }

    public ArrayList<TipoToken> getConsequente() {
        return consequente;
    }

    public void setConsequente(ArrayList<TipoToken> consequente) {
        this.consequente = consequente;
    }

    public ArrayList<String> getRegras() {
        return regras;
    }

    public void setRegras(ArrayList<String> regras) {
        this.regras = regras;
    }

    public ArrayList<TipoToken> getTokens() {
        return tokens;
    }

    @Override
    public void limpaPilha(Stack<TipoToken> pilha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
