/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Command;

import Persistencia.Main;

/**
 *
 * @author Shu
 */
public class LimparCommand implements ICommand{

    @Override
    public void executar(Main main) {
        main.getTela().getPremissasjEditorPane().setText("");
        main.getTela().getRespostajTextArea().setText("");
        main.setTexto("");
        main.setResultado("");
        main.setTokens(null);
        main.getTela().getRegrasTextArea().setText("");
    }
    
}
