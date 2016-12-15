/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia.Lexico;

import Collections.TipoToken;
import Persistencia.Main;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


/**
 *
 * @author Shu
 */
public class Lexico {
    private static Lexer lexer;
    protected static Lexico analisador;
    
    
    private Lexico(Main main){
        try {
            this.executarLexico(main);
        } catch (IOException ex) {
            ex.getMessage();
          
        }
    }
            
    public static Lexico getAnalisador(Main main) {
        if(analisador== null)
            return new Lexico(main);
        return analisador;
    }
    
    public ArrayList<TipoToken> executarLexico(Main main) throws IOException{
        ArrayList<TipoToken> tokens = new ArrayList<>();
        String resultado = "";
      
        for(int i = 0; i<main.getPremissas().size();i++){
            lexer = new Lexer(new StringReader(main.getPremissas().get(i)));
            if(lexer == null){
                throw new IOException("Falha ao executar arquivo Lexer");
            }else{
                while(true){
                   Token token = lexer.yylex();
                   if(token == null){
                       main.setResultado(resultado);
                       
                       break;    
                   }

                    switch(token){

                       
                        case SIMBOLO:
                            tokens.add(new TipoToken(lexer.yytext(),"SIMBOLO" , i+1));
                            break;
                        case SE:
                            tokens.add(new TipoToken(lexer.yytext(),"SE" , i+1));
                            break;
                        case SE_SOMENTE:
                            tokens.add(new TipoToken(lexer.yytext(),"SE_SOMENTE" , i+1));
                            break;
                        case PARENTESES_ABRE:
                            tokens.add(new TipoToken(lexer.yytext(),"PARENTESES_ABRE" , i+1));
                            break;
                        case PARENTESES_FECHA:
                            tokens.add(new TipoToken(lexer.yytext(),"PARENTESES_FECHA" , i+1));
                            break;
                        case NEGACAO:
                            tokens.add(new TipoToken(lexer.yytext(),"NEGACAO" , i+1));
                            break;
                        case DUPLA_NEGACAO:
                            tokens.add(new TipoToken(lexer.yytext(),"DUPLA_NEGACAO" , i+1));
                            break;
                        case ELOGICO:
                            tokens.add(new TipoToken(lexer.yytext(),"ELOGICO" , i+1));
                            break;
                        case OULOGICO:
                            tokens.add(new TipoToken(lexer.yytext(),"OUOGICO" , i+1));
                            break;
                        case EOF:
                            tokens.add(new TipoToken(lexer.yytext(),"FIM" , i+1));
                            break;
                        case ERROR:
                            resultado = resultado + "Erro, símbolo ' "+ lexer.yytext() +" ' não reconhecido \n";
                            //tokens.add(new TipoToken(lexer.yytext(),"PREMISSA" , i+1));'
                            break;
                            
                        default:
                           // resultado += "" + lexer.lexeme +"" ;

                   } 

                }
            }
        }
        return tokens;

    }
    
}

