/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Sintatico;


import Telas.MainTela;

/**
 *
 * @author Shu
 */
public class ErroSintatico {
    private static ErroSintatico es;
    
    private String resposta = "";
    private ErroSintatico(){}

    public static ErroSintatico getEs() {
        if(es==null){
            es = new ErroSintatico();
        }
        return es;
    }

    
    
    
    public void erro(int n, MainTela view, int linha){
        
        
        switch(n){
            case 1:
                resposta +=  "Erro: '(' esperado Linha: "+ linha+" \n";
                break;
            case 2:
                resposta += "Erro: ')' experado "+ linha+" \n";
                break;
            case 3:
                resposta += "Erro: Sentença esperada : "+ linha+" \n";
                break;
            case 4:
                resposta += "Erro: Conectivo esperado: "+ linha+" \n";
                break;
           /*
            case 5:
                resposta +=  "Erro: '.' esperado Linha: "+ linha+" \n";
                break;
            case 6:
                resposta +=  "Erro: import esperado Linha: "+ linha+" \n";
                break;
            case 7:
                resposta += "Erro: { esperado Linha: "+ linha+" \n";
                break;
            case 8:
                resposta += "Erro: } esperado Linha: "+ linha+" \n";
                break;
                    */

        }
        
        view.getRespostajTextArea().setText(resposta);
        
         
    
    }
    
}
