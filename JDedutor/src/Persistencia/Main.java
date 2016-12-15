/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;


import Collections.TipoToken;
import Persistencia.Command.ExecutarCommand;
import Persistencia.Command.FecharCommand;
import Persistencia.Command.ICommand;
import Persistencia.Command.LimparCommand;
import Persistencia.Command.SelecionarCommand;
import Telas.MainTela;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Shu
 */
public class Main {

    private MainTela tela;
    private String texto;
    private String resultado = "";
    private ArrayList<String> premissas;
    private ArrayList<TipoToken> tokens;
    private ArrayList<TipoToken> tokensSemantico;

    public Main(MainTela tela) {
        this.tela = tela;

        jsyntaxpane.DefaultSyntaxKit.initKit();
        this.tela.getPremissasjEditorPane().setContentType("text/java");
        this.tela.setVisible(true);


        this.desabilitar();
        
        //comandos
        this.tela.getFecharjButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fechar();

            }

        });
        
        
        this.tela.getSelecionarjButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    desabilitar();
                    limpar();
                    selecionar();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.out.println(ex.getStackTrace());
                }

            }

        });

        this.tela.getOkjButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    executar();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.out.println(ex.getStackTrace());
                }

            }

        });

        this.tela.getLimparjButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                desabilitar();
                limpar();

            }

        });
    }

    public MainTela getTela() {
        return tela;
    }

    private void fechar() {
        ICommand cmd = new FecharCommand();
        cmd.executar(this);
    }

    private void limpar() {
        ICommand cmd = new LimparCommand();
        cmd.executar(this);
    }
    
    private void selecionar(){
        ICommand cmd = new SelecionarCommand();
        cmd.executar(this);
    }

    public String getTexto() {
        return texto;
    }

    
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void desabilitar(){
        
        this.tela.getRespostajTextArea().setEnabled(false);
        this.tela.getPremissasjEditorPane().setEnabled(false);
        this.tela.getLimparjButton().setEnabled(false);
        this.tela.getRegrasTextArea().setEnabled(false);
        this.tela.getOkjButton().setEnabled(false);

    
    }
    
    
    private void executar() throws Exception {
        if (this.tela.getPremissasjEditorPane().getText().isEmpty()) {
            JOptionPane.showMessageDialog(tela, "Campo Vazio \nInsira a premissa desejada");
            throw new Exception("Campo Vazio");
        } else {
            this.tela.getRespostajTextArea().setEnabled(true);
            ICommand cmd = new ExecutarCommand();
            cmd.executar(this);
        }
    }
   
    
     public void addTabelaToken(ArrayList<TipoToken> tokens) {
        TipoToken token;
        DefaultTableModel model = (javax.swing.table.DefaultTableModel) this.tela.getTabelajTable().getModel();

        while (this.tela.getTabelajTable().getRowCount() > 0) {
            model.removeRow(0);
        }

        for (TipoToken token1 : tokens) {
            token = token1;
            model.addRow(new Object[]{token.getLinha(), token.getNome(), token.getTipo()});
        }

    }
     
    public void setTokens(ArrayList<TipoToken> tokens) {
        this.tokens = tokens;
    }

    public ArrayList<String> getPremissas() {
        return premissas;
    }

    public void setPremissas(ArrayList<String> premissas) {
        this.premissas = premissas;
    }
    

    public ArrayList<TipoToken> getTokens() {
        return tokens;
    }

    public void setTokensSemantico(ArrayList<TipoToken> tokens) {
        this.tokensSemantico = tokens;
    }

    public ArrayList<TipoToken> getTokensSemantico() {
        return tokensSemantico;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

   

}
