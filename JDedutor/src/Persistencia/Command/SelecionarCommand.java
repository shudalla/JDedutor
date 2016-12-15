/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Command;

import Persistencia.Arquivo.Arquivo;
import Persistencia.Main;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Shu
 */
public class SelecionarCommand implements ICommand {

    @Override
    public void executar(Main main) {
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(fileNameExtensionFilter);

        chooser.setDialogTitle("Selecionar arquivo");

        int resposta = chooser.showOpenDialog(main.getTela());

        if (resposta == JFileChooser.APPROVE_OPTION) {
            
            Arquivo.getArquivo(chooser.getSelectedFile().getAbsolutePath());
       
            main.getTela().getRespostajTextArea().setEnabled(true);
            main.getTela().getPremissasjEditorPane().setEnabled(true);
            main.getTela().getLimparjButton().setEnabled(true);
            main.getTela().getOkjButton().setEnabled(true);
            main.getTela().getRegrasTextArea().setEnabled(true);
            
            main.setPremissas(Arquivo.getArquivo().getLinhasCodigoBuffer());
            
            main.getTela().getPremissasjEditorPane().setText(main.getPremissas().toString());
            
            String texto = "";
            for (String premissa : main.getPremissas()) {
                if(premissa != null && !premissa.equals("EOF")){
                    texto+=premissa+"\n";
                }
                
            }
            main.setTexto(texto);
            main.getTela().getPremissasjEditorPane().setText(main.getTexto());
        }

    }

}
