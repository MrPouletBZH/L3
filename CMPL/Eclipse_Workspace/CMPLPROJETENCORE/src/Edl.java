import java.io.*;

 /**
 * 
 * @author Mozet Paul, Meauzé Baptiste
 * @version 2022
 *
 */


public class Edl {

	// nombre max de modules, taille max d'un code objet d'une unite
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de references externes (REF) et de points d'entree (DEF)
	// pour une unite
	private static final int MAXREF = 10, MAXDEF = 10;

	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;

	// valeurs possibles du vecteur de translation
	private static final int TRANSDON=1,TRANSCODE=2,REFEXT=3;

	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];

	//TODO : declarations de variables A COMPLETER SI BESOIN
	static int ipo, nMod, nbErr;
	static String nomProg;

	static int[] transDon = new int[6];
	static int[] transCode = new int[6];

	// utilitaire de traitement des erreurs
	// ------------------------------------
	static void erreur(int te, String m) {
		System.out.println(m);
		if (te == FATALE) {
			System.out.println("ABANDON DE L'EDITION DE LIENS");
			System.exit(1);
		}
		nbErr = nbErr + 1;
	}

	// utilitaire de remplissage de la table des descripteurs tabDesc
	// --------------------------------------------------------------
	static void lireDescripteurs() {
		String s;
		System.out.println("les noms doivent etre fournis sans suffixe");
		System.out.print("nom du programme : ");
		s = Lecture.lireString();
		tabDesc[0] = new Descripteur();
		tabDesc[0].lireDesc(s);
		if (!tabDesc[0].getUnite().equals("programme"))
			erreur(FATALE, "programme attendu");
		nomProg = s;

		nMod = 0;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				nMod++;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);

				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			}
		}
	}


	static void constMap() {
		// f2 = fichier executable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		if (f2 == null)
			erreur(FATALE, "creation du fichier " + nomProg
					+ ".map impossible");
		// pour construire le code concatene de toutes les unités
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];
		
		//TODO : ... A COMPLETER ...
		// 
		//

		Ecriture.fermer(f2);

		// creation du fichier en mnemonique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println("");
		nbErr = 0;

		// Phase 1 de l'edition de liens
		// -----------------------------
		lireDescripteurs();		//TODO : lecture des descripteurs a completer si besoin
		
		transCode[0] = 0;
		transDon[0] = 0;

		int decalCode, decalDon;
		for (int i = 1; i<nMod; i++){
			decalCode = 0;
			decalDon = 0;
			for (int j = 0; j<i; j++){
				decalDon+=tabDesc[j].getTailleGlobaux();
				decalCode+=tabDesc[j].getTailleCode();
			}
			transDon[i] = decalDon;
			transCode[i] = decalCode;
		}

		//TODO : ... A COMPLETER ...
		// 
		// 
		//

		if (nbErr > 0) {
			System.out.println("programme executable non produit");
			System.exit(1);
		}

		// Phase 2 de l'edition de liens
		// -----------------------------
		constMap();				//TODO : ... A COMPLETER ...
		System.out.println("Edition de liens terminee");
	}
}