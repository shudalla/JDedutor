/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Sintatico;

import Collections.TipoToken;
import Persistencia.Main;
import Telas.MainTela;
import java.util.ArrayList;

/**
 *
 * @author Shu
 */
public final class Sintatico {

    protected static Sintatico analisador;
    private ArrayList<TipoToken> tokens;
    private TipoToken ta;
    private MainTela tela;
    private ArrayList<TipoToken> tokensLinha = new ArrayList<>();

    private Sintatico(Main main) {
        this.tokens = main.getTokens();
        this.tela = main.getTela();
       
        this.tokensPorLinha();

    }

    public static Sintatico getAnalisador(Main main) {
        if (analisador == null) {
            return new Sintatico(main);
        }
        return analisador;
    }

    public boolean hasNextLinha() {
        return !this.tokensLinha.isEmpty();
    }

    public int getTokenLinha() {
        return this.tokens.get(0).getLinha();
    }

    public void executarSintatico() {
        while (!this.tokensLinha.isEmpty()) {

            if (this.tokensLinha.get(0).getNome().equals("(")) {
                this.escopo();
                // break;
            } else if (this.tokensLinha.get(0).getNome().equals(")")) {
                while (this.hasNextLinha()) {
                    this.tokensLinha.remove(0);
                }
                ErroSintatico.getEs().erro(1, this.tela, this.getTokenLinha());
            } else {
                this.sentenca();
            }
        }

    }

    public void sentenca() {
        if (this.tokensLinha.isEmpty()) {
            ErroSintatico.getEs().erro(3, this.tela, this.getTokenLinha());

        } else if (this.tokensLinha.get(0).getNome().equals("VERDADEIRO")
                || this.tokensLinha.get(0).getNome().equals("FALSO")
                || this.tokensLinha.get(0).getTipo().equals("SIMBOLO")) {

            this.sentencaAtomica();

        } else if (this.tokensLinha.get(0).getTipo().equals("DUPLA_NEGACAO")
                || this.tokensLinha.get(0).getTipo().equals("NEGACAO")) {

            //this.ta = this.tokensLinha.get(0);
            //this.tokensLinha.remove(0);
            this.sentencaComplexa();

        } else {
            while (this.hasNextLinha()) {
                this.tokensLinha.remove(0);
            }
            ErroSintatico.getEs().erro(3, this.tela, this.getTokenLinha());
        }

    }

    public void sentencaAtomica() {
        // System.out.println("Sentenca atomica");

        if (this.tokensLinha.get(0).getNome().equals("VERDADEIRO")
                || this.tokensLinha.get(0).getNome().equals("FALSO")
                || this.tokensLinha.get(0).getTipo().equals("SIMBOLO")) {

            this.ta = this.tokensLinha.get(0);
            this.tokensLinha.remove(0);

            if (!this.tokensLinha.isEmpty()
                    && (this.tokensLinha.get(0).getNome().equals("v")
                    || this.tokensLinha.get(0).getNome().equals("^")
                    || this.tokensLinha.get(0).getNome().equals("->")
                    || this.tokensLinha.get(0).getTipo().equals("SE_SOMENTE"))) {
                this.sentencaComplexa();

            } else {
                if (!this.tokensLinha.isEmpty()) {
                    ErroSintatico.getEs().erro(4, this.tela, this.getTokenLinha());
                }

            }

        }

    }

    public void sentencaComplexa() {

        //removendo o simbulo
        if (this.tokensLinha.get(0).getTipo().equals("DUPLA_NEGACAO")
                || this.tokensLinha.get(0).getTipo().equals("NEGACAO")) {
            this.ta = this.tokensLinha.get(0);
            this.tokensLinha.remove(0);

            if (!this.tokensLinha.isEmpty() && this.tokensLinha.get(0).getNome().equals("(")) {
                this.escopo();
            }

        } else if (!this.tokensLinha.isEmpty() && (this.tokensLinha.get(0).getNome().equals("v")
                || this.tokensLinha.get(0).getNome().equals("^")
                || this.tokensLinha.get(0).getNome().equals("->")
                || this.tokensLinha.get(0).getNome().equals("<->"))) {

            this.ta = this.tokensLinha.get(0);
            this.tokensLinha.remove(0);

            if (this.tokensLinha.isEmpty()
                    || this.tokensLinha.get(0).getNome().equals("v")
                    || this.tokensLinha.get(0).getNome().equals("^")
                    || this.tokensLinha.get(0).getNome().equals("->")
                    || this.tokensLinha.get(0).getNome().equals("<->")
                    || this.tokensLinha.get(0).getNome().equals(")")
                    || this.tokensLinha.get(0).getLinha() != this.ta.getLinha()) {

                while (this.hasNextLinha()) {
                    this.tokensLinha.remove(0);
                }
                ErroSintatico.getEs().erro(3, this.tela, this.getTokenLinha());

            } else {
                if (this.tokensLinha.get(0).getNome().equals("(")) {
                    this.escopo();
                } else {
                    this.sentenca();
                }
            }

        }

    }

    public void escopo() {
        //System.out.println("Escopo");

        if (this.tokensLinha.get(0).getNome().equals("(")) {
            this.ta = this.tokensLinha.get(0);
            this.tokensLinha.remove(0);
            if (!this.tokensLinha.isEmpty()) {

                if (this.tokens.get(0).getNome().equals(")")) {
                    this.ta = this.tokensLinha.get(0);
                    this.tokensLinha.remove(0);

                } else {
                    int i = 0;
                    while (this.hasNextLinha() && i < this.tokensLinha.size() && !this.tokensLinha.get(i).getNome().equals(")")) {
                        i++;
                    }

                    if (this.tokensLinha.isEmpty() || i >= this.tokensLinha.size()) {

                        ErroSintatico.getEs().erro(2, this.tela, this.ta.getLinha());

                    } else if (this.tokensLinha.get(i).getNome().equals(")")) {
                        this.ta = this.tokensLinha.get(i);

                        this.tokensLinha.remove(i);

                    } else if (!this.tokensLinha.get(i).getNome().equals(")")) {
                        while (this.hasNextLinha()) {
                            this.tokensLinha.remove(0);
                        }
                        ErroSintatico.getEs().erro(2, this.tela, (tokensLinha.get(0).getTipo().equals("EOF") ? this.ta.getLinha() : this.getTokenLinha()));
                    }

                }
            }

        } else {
            while (this.hasNextLinha()) {
                this.tokensLinha.remove(0);
            }
            ErroSintatico.getEs().erro(1, this.tela, (tokens.get(0).getTipo().equals("EOF") ? this.ta.getLinha() : this.getTokenLinha()));
        }

    }

    public void tokensPorLinha() {
        int totalLinhas = this.tokens.get(this.tokens.size() - 2).getLinha();
        int j = 0;

        for (int i = 1; i <= totalLinhas; i++) {
            //int j = 0;
            while (this.tokens.get(j).getLinha() == i) {
                this.tokensLinha.add(new TipoToken(this.tokens.get(j).getNome(), this.tokens.get(j).getTipo(), i));
                j++;
            }
            if (!this.tokensLinha.get(0).getNome().equals("EOF")) {
                this.executarSintatico();
            }

        }

    }

}
