/*********************************************************************************
 *   polyP8-exoExp : PtGen2 donne le code des traitements a effectuer            *
 *                   lors de l'analyse d'une expression                          *
 *   squelette de PtGen2 fourni aux etudiants, a completer pour grammaire  Exp2  *
 *   nom de l'expression analysee, sans suffixe : String    nomSource            *
 *   ----------------------------                                                *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int PtGen2.valEnt = valeur du dernier nombre entier lu (item nbentier)    *
 *     int PtGen2.idLu = chaine du dernier identificateur lu (item ident)        *
 *                                                                               *
 *                          N. GIRARD - V.MASSON - L. PERRAUDEAU                 *
 *********************************************************************************/


import java.io.*;

public class PtGen2 {


	public static String trinome = "Meauzé Baptiste Mozet Paul";	//TODO
	public static int valEnt;
	public static String idLu;

	// initialisations  a  completer si besoin
	// ---------------------------------------
	// TODO

	// code des points de generation a completer
	// -------------------------------------
	public static void pt(int numGen) {
		switch (numGen) {  
		case 0: break;
		case 1: System.out.println("empiler " + valEnt);
				break;
		case 2: System.out.println("contenug " + idLu);
				break;
		case 3: System.out.println("mul");
				break;
		case 4: System.out.println("div");
				break;
		case 5: System.out.println("add");
				break;
		case 6: System.out.println("sous");
				break;



		default : System.out.println("Point de generation non prevu dans votre liste");break;

		}
	}
}
