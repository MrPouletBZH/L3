// Grammaire du langage PROJET
// CMPL L3info 
// Nathalie Girard, Anne Grazon, Veronique Masson
// il convient d'y inserer les appels a {PtGen.pt(k);}
// relancer Antlr apres chaque modification et raffraichir le projet Eclipse le cas echeant

// attention l'analyse est poursuivie apres erreur si l'on supprime la clause rulecatch

grammar projet;

options {
  language=Java; k=1;
 }

@header {           
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
} 


// partie syntaxique :  description de la grammaire //
// les non-terminaux doivent commencer par une minuscule


@members {

 
// variables globales et methodes utiles a placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse a la premiere erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  : ( unitprog  EOF
      |    unitmodule  EOF )
      {PtGen.pt(255);}
  ;
  
unitprog
  : 'programme' ident ':'  
     declarations  
     corps { System.out.println("succes, arret de la compilation "); }
  ;
  
unitmodule
  : 'module' ident ':' 
     declarations   
  ;
  
declarations
  : partiedef? partieref? consts? vars? decprocs? 
  ;
  
partiedef
  : 'def' ident  (',' ident )* ptvg
  ;
  
partieref: 'ref'  specif (',' specif)* ptvg
  ;
  
specif  : ident  ( 'fixe' '(' type  ( ',' type  )* ')' )? 
                 ( 'mod'  '(' type  ( ',' type  )* ')' )? 
  ;
  
consts  : 'const' ( ident  '=' valeur {PtGen.pt(20);} ptvg )+ 
  ;
  
vars  : 'var' ( type ident {PtGen.pt(21);} ( ','  ident {PtGen.pt(21);} )* ptvg  )+ {PtGen.pt(26);}
  ;
  
type  : 'ent'  {PtGen.pt(22);}
  |     'bool' {PtGen.pt(23);}
  ;
  
decprocs: (decproc ptvg)+
  ;
  
decproc :  'proc'  ident  parfixe? parmod? consts? vars? corps 
  ;
  
ptvg  : ( ';'
  |  )
  {PtGen.pt(19);}
  ;
  
corps : 'debut' instructions 'fin'
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident  ( ',' ident  )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident  ( ',' ident  )*
  ;
  
instructions
  : instruction ( ';' instruction)*
  ;
  
instruction
  : inssi
  | inscond
  | boucle
  | lecture
  | ecriture
  | affouappel
  |
  ;
  
inssi : 'si' expression {PtGen.pt(29);} 'alors' instructions ('sinon' {PtGen.pt(30);} instructions)? 'fsi' {PtGen.pt(31);}
  ;
  
inscond : 'cond'  expression  ':' instructions 
          (','  expression  ':' instructions )* 
          ('aut'  instructions |  ) 
          'fcond' 
  ;
  
boucle  : 'ttq' {PtGen.pt(32);} expression {PtGen.pt(33);} 'faire' instructions 'fait' {PtGen.pt(34);}
  ;
  
lecture: 'lire' '(' ident {PtGen.pt(24);} ( ',' ident {PtGen.pt(24);} )* ')' 
  ;
  
ecriture: 'ecrire' '(' expression {PtGen.pt(25);} ( ',' expression {PtGen.pt(25);} )* ')'
   ;
  
affouappel
  : ident  ({PtGen.pt(28);} ':=' expression {PtGen.pt(27);}
            |   (effixes (effmods)?)?  
           )
  ;
  
effixes : '(' (expression  (',' expression  )*)? ')'
  ;
  
effmods :'(' (ident  (',' ident  )*)? ')'
  ; 
  
expression: {PtGen.pt(19);} (exp1) ('ou'  exp1 {PtGen.pt(18);} )*  
  ;
  
exp1  : exp2 ('et'  exp2 {PtGen.pt(17);} )*
  ;
  
exp2  : 'non' exp2 {PtGen.pt(16);}
  | exp3  
  ;
  
exp3  : exp4 
  ( '='   exp4 {PtGen.pt(10);}
  | '<>'  exp4 {PtGen.pt(11);}
  | '>'   exp4 {PtGen.pt(12);}
  | '>='  exp4 {PtGen.pt(13);}
  | '<'   exp4 {PtGen.pt(14);}
  | '<='  exp4 {PtGen.pt(15);}
  ) ?
  ;
  
exp4  : exp5 
        ('+'  exp5 {PtGen.pt(8);}
        |'-'  exp5 {PtGen.pt(9);}
        )*
  ;
  
exp5  : primaire 
        (    '*'   primaire {PtGen.pt(6);}
          | 'div'  primaire {PtGen.pt(7);}
        )*
  ;
  
primaire: valeur {PtGen.pt(51);}
  | ident {PtGen.pt(5);}
  | '(' expression ')'
  ;
  
valeur  : nbentier {PtGen.pt(4);}
  | '+' nbentier {PtGen.pt(4);}
  | '-' nbentier {PtGen.pt(3);}
  | 'vrai' {PtGen.pt(2);}
  | 'faux' {PtGen.pt(1);}
  ;

// partie lexicale  : cette partie ne doit pas etre modifiee  //
// les unites lexicales de ANTLR doivent commencer par une majuscule
// Attention : ANTLR n'autorise pas certains traitements sur les unites lexicales, 
// il est alors ncessaire de passer par un non-terminal intermediaire 
// exemple : pour l'unit lexicale INT, le non-terminal nbentier a du etre introduit
 
      
nbentier  :   INT { UtilLex.valEnt = Integer.parseInt($INT.text);}; // mise a jour de valEnt

ident : ID  { UtilLex.traiterId($ID.text); } ; // mise a jour de numIdCourant
     // tous les identificateurs seront places dans la table des identificateurs, y compris le nom du programme ou module
     // (NB: la table des symboles n'est pas geree au niveau lexical mais au niveau du compilateur)
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' |'\r')+ {skip();} ; // definition des "blocs d'espaces"
RC  :   ('\n') {UtilLex.incrementeLigne(); skip() ;} ; // definition d'un unique "passage a la ligne" et comptage des numeros de lignes

COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caracteres entouree d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractere diese sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;	   



	   
