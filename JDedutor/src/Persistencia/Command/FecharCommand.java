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
public class FecharCommand implements ICommand{

    @Override
    public void executar(Main main) {
        main.getTela().dispose();
        System.exit(0);
    }
    
}
