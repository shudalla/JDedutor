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
public interface ICommand {
    public abstract void executar(Main main);
}
