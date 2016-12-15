package Persistencia.Lexico;

import static Persistencia.Lexico.Token.*;
%%
%class Lexer
%type Token

L = [a-zA-Z_]
D = [0-9]

WHITE=[ \t\r\n]
%{
public String lexeme;
%}
%%
{WHITE} {/*Ignore*/}


"->"    {return SE;}
"<->"   {return SE_SOMENTE;}
( "~~"|"~ ~" )     { return DUPLA_NEGACAO;}
"~"     { return NEGACAO;}
"^"    {return ELOGICO;}
"v"   {return OULOGICO;}


/*Separadores */
"("      { return PARENTESES_ABRE;}
")"      { return PARENTESES_FECHA;}


/* Caracteres Especiais */
/*(\b | "\t" | "\n" | "\f")   { return ESPECIAL;} */

(EOF) {return EOF;}

{L}({L}|{D})* {lexeme=yytext(); return SIMBOLO;}


. {return ERROR;}