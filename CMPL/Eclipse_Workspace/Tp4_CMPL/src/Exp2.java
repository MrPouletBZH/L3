/**
 * La classe Exp2 correspond au programme principal d'un analyseur syntaxique d'expressions par ANTLR
 * 		+ traitement des expressions dirige par la syntaxe (affichages) -> cf poly p8
 * 
 * -----programme fourni aux etudiants qui ne doivent pas le modifier-----
 * Ce programme contient le main qui demande le nom du texte source que l'on souhaite analyser,
 * et lance son analyse (appelee compilation) a partir de l'axiome "unite" de la grammaire Exp2.g
 * Il est prevu de pouvoir analyser plusieurs textes source de suite (arret par un retour-a-la-ligne RC)
 * 
 * @author Girard, Masson, Perraudeau
 * janvier 2022 
 *
 */

import java.io.*;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

class Exp2 {
	/** nom du fichier source a analyser (a "compiler") */
	public static String nomSource; 
	
	private static void UneCompilation (String nomDuSource ) {
		try {
			ANTLRFileStream input = new ANTLRFileStream(nomDuSource);
			/** production d'un flot d'unites lexicales par le Exp2Lexer */
			Exp2Lexer lexer = new Exp2Lexer(input);			
			CommonTokenStream token_stream = new CommonTokenStream(lexer); 
			/** analyse syntaxique par Exp2Parser */
			Exp2Parser parser = new Exp2Parser(token_stream); 
			/** point de générations des initialisations */
			PtGen2.pt(0); 
			/** analyse du texte source en entree a partir de l'axiome "unite" */
			parser.unite(); 

		 } catch (FileNotFoundException fnf) {
			 /** cas ou le fichier source n'existe pas */
			System.out.println("exception: " + fnf); 
		}
		catch (RecognitionException re) {
			/** erreur de nature syntaxique detectee par le parser */
	 	    System.out.println("Recognition exception: " + re); 
		}
		// System.err.println dirige ses impressions sur une autre sortie et les messages ont alors tendance a se superposer
		catch (IOException exc) {
			System.err.println("IO exception: " + exc);
		} 	
	} 
	
	
	public static void main(String [] args) {
		System.out.println("Premier exercice du poly : " + PtGen2.trinome);
		System.out.println("----------------------------------------");
		System.out.println();
		
		do 
		 {
			/** lecture du nom de fichier en entree */
			System.out.println(); 
		    System.out.print("Donnez le nom du fichier que vous souhaitez compiler :  (RC si termine) ");
		    nomSource=Lecture.lireString();  
		    System.out.println();
		    
		    if (!nomSource.equals("")) {
		    	/** traitement d'une compilation */
		    	UneCompilation (nomSource); 
		    } 
		    System.out.println();
		 }
		while (!nomSource.equals("")); 	
		System.out.println("\n \n Merci pour votre patience, " + PtGen2.trinome + ", et a bientot !!!");	
	} 
} 