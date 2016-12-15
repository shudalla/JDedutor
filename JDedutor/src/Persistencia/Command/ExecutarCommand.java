/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Command;


import Persistencia.Lexico.Lexico;
import Persistencia.Main;
import Persistencia.Semantico.Semantico;
import Persistencia.Sintatico.Sintatico;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;



/**
 *
 * @author Shu
 */
public class ExecutarCommand implements ICommand{

    @Override
    public void executar(Main main) {
       
        try {
           // main.setTexto(main.getTela().getPremissajTextArea().getText());
            //main.criarPremissas();
            main.setTokens(Lexico.getAnalisador(main).executarLexico(main));
            main.setTokensSemantico(Lexico.getAnalisador(main).executarLexico(main));
            
            if("".equals(main.getResultado())){
       
                main.addTabelaToken(main.getTokens());
                Sintatico.getAnalisador(main).executarSintatico();
                
                if(main.getTela().getRespostajTextArea().getText().equals("")){
                    Semantico.getAnalisador(main).executarSemantico();
                }
               
            }else{
                main.getTela().getRespostajTextArea().setText(main.getResultado());
            }
            
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    
    }
    
    
}
