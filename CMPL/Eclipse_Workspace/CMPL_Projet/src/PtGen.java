/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libClass_Projet)     *
 *       complement à l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compile, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valEnt = valeur du dernier nombre entier lu (item nbentier)   *
 *     int UtilLex.numIdCourant = code du dernier identificateur lu (item ident) *
 *                                                                               *
 *                                                                               *
 *   methodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arret compilation       *
 *     String UtilLex.chaineIdent(int numId) delivre l'ident de codage numId     *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

/**
 * classe de mise en oeuvre du compilateur
 * =======================================
 * (verifications semantiques + production du code objet)
 * 
 * @author Girard, Masson, Perraudeau
 *
 */

public class PtGen {
    

    // constantes manipulees par le compilateur
    // ----------------------------------------

	private static final int 
	
	// taille max de la table des symboles
	MAXSYMB=300,

	// codes MAPILE :
	RESERVER=1,EMPILER=2,CONTENUG=3,AFFECTERG=4,OU=5,ET=6,NON=7,INF=8,
	INFEG=9,SUP=10,SUPEG=11,EG=12,DIFF=13,ADD=14,SOUS=15,MUL=16,DIV=17,
	BSIFAUX=18,BINCOND=19,LIRENT=20,LIREBOOL=21,ECRENT=22,ECRBOOL=23,
	ARRET=24,EMPILERADG=25,EMPILERADL=26,CONTENUL=27,AFFECTERL=28,
	APPEL=29,RETOUR=30,

	// codes des valeurs vrai/faux
	VRAI=1, FAUX=0,

    // types permis :
	ENT=1,BOOL=2,NEUTRE=3,

	// categories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

    //valeurs possible du vecteur de translation 
    TRANSDON=1,TRANSCODE=2,REFEXT=3;


    // utilitaires de controle de type
    // -------------------------------
    /**
     * verification du type entier de l'expression en cours de compilation 
     * (arret de la compilation sinon)
     */
	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression entiere attendue");
	}
	/**
	 * verification du type booleen de l'expression en cours de compilation 
	 * (arret de la compilation sinon)
	 */
	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression booleenne attendue");
	}

    // pile pour gerer les chaines de reprise et les branchements en avant
    // -------------------------------------------------------------------

    private static TPileRep pileRep;  


    // production du code objet en memoire
    // -----------------------------------

    private static ProgObjet po;
    
    
    // COMPILATION SEPAREE 
    // -------------------
    //
    /** 
     * modification du vecteur de translation associe au code produit 
     * + incrementation attribut nbTransExt du descripteur
     *  NB: effectue uniquement si c'est une reference externe ou si on compile un module
     * @param valeur : TRANSDON, TRANSCODE ou REFEXT
     */
    private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}    
    // descripteur associe a un programme objet (compilation separee)
    private static Descripteur desc;

     
    // autres variables fournies
    // -------------------------
    
 // MERCI de renseigner ici un nom pour le trinome, constitue EXCLUSIVEMENT DE LETTRES
    public static String trinome="Meauzé Baptiste Mozet Paul"; 
    
    private static int tCour; // type de l'expression compilee
    private static int vCour; // sert uniquement lors de la compilation d'une valeur (entiere ou boolenne)

	private static int i;
	private static int identCour;
  
   
    // TABLE DES SYMBOLES
    // ------------------
    //
    private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];
    
    // it = indice de remplissage de tabSymb
    // bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;

	// indice de la dernière constante
	private static int itConst;
	private static int varLocCount;
	private static int paramCount;

	
	/** 
	 * utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numIdCourant) dans tabSymb
	 * 
	 * @param borneInf : recherche de l'indice it vers borneInf (=1 si recherche dans tout tabSymb)
	 * @return : indice de l'ident courant (de code UtilLex.numIdCourant) dans tabSymb (O si absence)
	 */
	private static int presentIdent(int borneInf) {
		int i = it;
		while (i >= borneInf && tabSymb[i].code != UtilLex.numIdCourant)
			i--;
		if (i >= borneInf)
			return i;
		else
			return 0;
	}

	/**
	 * utilitaire de placement des caracteristiques d'un nouvel ident dans tabSymb
	 * 
	 * @param code : UtilLex.numIdCourant de l'ident
	 * @param cat : categorie de l'ident parmi CONSTANTE, VARGLOBALE, PROC, etc.
	 * @param type : ENT, BOOL ou NEUTRE
	 * @param info : valeur pour une constante, ad d'exécution pour une variable, etc.
	 */
	private static void placeIdent(int code, int cat, int type, int info) {
		if (it == MAXSYMB)
			UtilLex.messErr("debordement de la table des symboles");
		it = it + 1;
		tabSymb[it] = new EltTabSymb(code, cat, type, info);
	}

	/**
	 *  utilitaire d'affichage de la table des symboles
	 */
	private static void afftabSymb() { 
		System.out.println("       code           categorie      type    info");
		System.out.println("      |--------------|--------------|-------|----");
		for (int i = 1; i <= it; i++) {
			if (i == bc) {
				System.out.print("bc=");
				Ecriture.ecrireInt(i, 3);
			} else if (i == it) {
				System.out.print("it=");
				Ecriture.ecrireInt(i, 3);
			} else
				Ecriture.ecrireInt(i, 6);
			if (tabSymb[i] == null)
				System.out.println(" reference NULL");
			else
				System.out.println(" " + tabSymb[i]);
		}
		System.out.println();
	}
    

	/**
	 *  initialisations A COMPLETER SI BESOIN
	 *  -------------------------------------
	 */
	public static void initialisations() {
	
		// indices de gestion de la table des symboles
		it = 0;
		bc = 1;
		itConst = 0;
		identCour = 0;
		varLocCount = 0;
		paramCount = 0;

		// pile des reprises pour compilation des branchements en avant
		pileRep = new TPileRep(); 
		// programme objet = code Mapile de l'unite en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unite en cours de compilation
		desc = new Descripteur();
		
		// initialisation necessaire aux attributs lexicaux
		UtilLex.initialisation();
	
		// initialisation du type de l'expression courante
		tCour = NEUTRE;



	} // initialisations

	/**
	 *  code des points de generation A COMPLETER
	 *  -----------------------------------------
	 * @param numGen : numero du point de generation a executer
	 */
	public static void pt(int numGen) {
	
		switch (numGen) {
		case 0:
			initialisations();
			break;
			
		case 1:
			tCour = BOOL;
			vCour = FAUX;
			break;
			
		case 2:
			tCour = BOOL;
			vCour = VRAI;
			break;
			
		case 3:
			tCour = ENT;
			vCour = -UtilLex.valEnt;
			break;
			
		case 4:
			tCour = ENT;
			vCour = UtilLex.valEnt;
			break;
			
		case 5:
			i = presentIdent(1);
			if (i == 0) {
				UtilLex.messErr("ident n'existe pas dans tabSymb");
			} else {
				switch (tabSymb[i].categorie) {
					case CONSTANTE :
						po.produire(EMPILER);
						po.produire(tabSymb[i].info);
						break;
					case VARGLOBALE :
						po.produire(CONTENUG);
						po.produire(tabSymb[i].info);
						break;
					case VARLOCALE :
						po.produire(CONTENUL);
						po.produire(tabSymb[i].info);
						po.produire(0);
						break;
					case PARAMFIXE : 
						po.produire(CONTENUL);
						po.produire(tabSymb[i].info);
						po.produire(0);
						break;
					case PARAMMOD :
						po.produire(CONTENUL);
						po.produire(tabSymb[i].info);
						po.produire(1);
						break;
				}

				tCour = tabSymb[i].type;
			}
			break;

		case 51:
			po.produire(EMPILER);
			po.produire(vCour);
			break;

		case 6: 
			if(tCour != ENT) {
				UtilLex.messErr("Type mismatch: Integer required for multiplication");
			} else {
				po.produire(MUL);
			}
			break;

		case 7:
			if (tCour != ENT)
				UtilLex.messErr("Type mismatch: Integer required for division");
			else
				po.produire(DIV);
			break;

		case 8:
			if (tCour != ENT)
				UtilLex.messErr("Type mismatch: Integer required for addition");
			else
				po.produire(ADD);
			break;

		case 9: 
			if (tCour != ENT)
				UtilLex.messErr("Type mismatch: Integer required for subtraction");
			else
				po.produire(SOUS);
			break;

		case 10: 
			verifEnt();
			po.produire(EG);
			tCour = BOOL;
			break;

		case 11: 
			verifEnt();
			po.produire(DIFF);
			tCour = BOOL;
			break;

		case 12: 
			verifEnt();
			po.produire(SUP);
			tCour = BOOL;
			break;

		case 13: 
			verifEnt();
			po.produire(SUPEG);
			tCour = BOOL;
			break;

		case 14: 	
			verifEnt();
			po.produire(INF);
			tCour = BOOL;
			break;

		case 15: 
			verifEnt();
			po.produire(INFEG);
			tCour = BOOL;
			break;

		case 16:
			verifBool();
			po.produire(NON);
			break;

		case 17:
			verifBool();
			po.produire(ET);
			break;

		case 18:
			verifBool();
			po.produire(OU);
			break;

		case 19:
			tCour = NEUTRE;
			break;

		case 20:
			i = presentIdent(bc);
			if (i == 0) {
				if (bc == 1){
					placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, vCour);
					itConst++;
				} else {
					placeIdent(UtilLex.numIdCourant, CONSTANTE, tCour, vCour);
				}
			} else {
				UtilLex.messErr("Ident already exists");
			}
			break;

		case 21:
			i = presentIdent(bc);
			if (i == 0) {
				if (bc == 1)
					placeIdent(UtilLex.numIdCourant, VARGLOBALE, tCour, it-itConst);
				else{
					placeIdent(UtilLex.numIdCourant, VARLOCALE, tCour, tabSymb[bc-1].info + 2 + varLocCount);
					varLocCount ++;
				}
			} else {
				UtilLex.messErr("Ident already exists");
			}
			break;

		case 22:
			tCour = ENT;
			break;
		
		case 23:
			tCour = BOOL;
			break;
		
		case 24:
			i = presentIdent(bc);
			if (i == 0) {
				UtilLex.messErr("ident n'existe pas dans tabSymb");
			} else {
				if (tabSymb[i].type == ENT)
					po.produire(LIRENT);
				else if (tabSymb[i].type == BOOL)
					po.produire(LIREBOOL);
				else						
					UtilLex.messErr("Type " + tabSymb[i].type + " can't be read");

				switch (tabSymb[i].categorie) {
					case CONSTANTE :
						UtilLex.messErr("Constant can't be read");
						break;
					case VARGLOBALE :
						po.produire(AFFECTERG);
						po.produire(tabSymb[i].info);
						break;
					case VARLOCALE :
						po.produire(AFFECTERL);
						po.produire(tabSymb[i].info);
						po.produire(0);
						break;
					case PARAMFIXE : 
						UtilLex.messErr("Fixe Parameter can't be read");
						break;
					case PARAMMOD :
						po.produire(AFFECTERL);
						po.produire(tabSymb[i].info);
						po.produire(1);
						break; 
				}
			}
			break;
		
		case 25:
			if (tCour == ENT)
				po.produire(ECRENT);
			else if (tCour == BOOL)
				po.produire(ECRBOOL);
			else
				UtilLex.messErr("Type " + tCour + " can't be write");

			break;
		
		case 26:
			if(bc==1){
				po.produire(RESERVER);
				po.produire(it-itConst);
			}
			else {
				po.produire(RESERVER);
				po.produire(varLocCount);
				varLocCount = 0;
			}
			break;
		
		case 27:
			switch (tabSymb[identCour].categorie){
				case VARLOCALE:  
					po.produire(AFFECTERL);	 
					po.produire(tabSymb[identCour].info);
					po.produire(0);
					break;
				case VARGLOBALE: 
					po.produire(AFFECTERG);
					po.produire(tabSymb[identCour].info);
					break;
				case PARAMMOD:
					po.produire(AFFECTERL);	 
					po.produire(tabSymb[identCour].info);
					po.produire(1);
					break;
				default: UtilLex.messErr("Can't affect");
			}
			break;
		
		case 28:
			identCour = presentIdent(itConst+1);
			break;

		case 29:
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;

		case 30:
			po.produire(BINCOND);
			po.produire(0);
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			pileRep.empiler(po.getIpo());
			break;

		case 31:
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;

		case 32:
			pileRep.empiler(po.getIpo()+1);
			break;

		case 33:
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;

		case 34:
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			break;

		case 35:
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;

		case 351:
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			break;

		case 36:
			pileRep.empiler(0);
			break;

		case 37:
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			pileRep.empiler(po.getIpo());
			break;

		case 38:
			int ind = pileRep.depiler();
			while ( ind != 0 ) {
				int tmp = po.getElt(ind);
				po.modifier(ind, po.getIpo()+1);
				ind = tmp;
			}
			break;

		case 39:
			po.produire(BINCOND);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;

		case 40:
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;

		case 41:
			i = presentIdent(1);
			if (i == 0) {
				placeIdent(UtilLex.numIdCourant, PROC, NEUTRE, po.getIpo()+1);
				placeIdent(-1, PRIVEE, NEUTRE, 0);	
				bc = it+1;			
			} else {
				UtilLex.messErr("Ident already exists");
			}
			break;

		case 42:
			i = presentIdent(bc);
			if (i == 0) {
				placeIdent(UtilLex.numIdCourant, PARAMFIXE, tCour, tabSymb[bc-1].info);
				tabSymb[bc-1].info++;
			} else {
				UtilLex.messErr("Ident already exists");
			}
			break;
		
		case 43:
			i = presentIdent(bc);
			if (i == 0) {
				placeIdent(UtilLex.numIdCourant, PARAMMOD, tCour, tabSymb[bc-1].info);
				tabSymb[bc-1].info++;
			} else {
				UtilLex.messErr("Ident already exists");
			}
			break;

		case 44:
			for(int i=it; i>=bc; i--) {
				if(tabSymb[i].categorie == VARLOCALE || tabSymb[i].categorie == CONSTANTE) {
					tabSymb[i] = null;
					it--;
				}
				else {
					tabSymb[i].code = -1;
				}
			}
			po.produire(RETOUR);
			po.produire(it-bc+1);

			bc = 1;
			break;

		case 45:
			i = presentIdent(1);
			if (i == 0) {
				UtilLex.messErr("ident n'existe pas dans tabSymb");
			} else {
				switch (tabSymb[i].categorie) {
					case CONSTANTE :
						UtilLex.messErr("Constant can't be used as a modular parameter");
						break;
					case VARGLOBALE :
						po.produire(EMPILERADG);
						po.produire(tabSymb[i].info);
						break;
					case VARLOCALE :
						po.produire(EMPILERADL);
						po.produire(tabSymb[i].info);
						po.produire(0);
						break;
					case PARAMFIXE : 
						UtilLex.messErr("Fixe Parameter can't be used as a modular parameter");
						break;
					case PARAMMOD :
						po.produire(EMPILERADL);
						po.produire(tabSymb[i].info);
						po.produire(1);
						break;
				}

				paramCount++;
				tCour = tabSymb[i].type;
			}
			break;

		case 46:
			po.produire(APPEL);
			po.produire(tabSymb[identCour].info);
			po.produire(paramCount);

			tabSymb[identCour+1].info = paramCount;
			paramCount = 0;
			break;

		case 47:
			paramCount++;
			break;

		case 48:
			break;

		case 49:
			break;

		case 50:
			break;

		case 52:
			break;

		case 53:
			break;

		case 54:
			break;

        case 255 : 
			po.produire(ARRET);

			po.constGen();
			po.constObj();
			afftabSymb(); // affichage de la table des symboles en fin de compilation
			break;

		default:
			System.out.println("Point de generation non prevu dans votre liste");
			break;

		}
	}
}
    
    
    
    
    
    
    
    
    
    
    
    
    
 
