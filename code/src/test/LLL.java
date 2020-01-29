package test;

import commun.InfoUtilisateur;
import terminalClient.GestionnaireFichier;

public class LLL {

	public static void main(String[] args) {

		GestionnaireFichier g = new GestionnaireFichier("/home/seb/P2P");
		InfoUtilisateur i = new InfoUtilisateur("IP", 22);
		g.remplirListeDesFichiers(i);
	}

}
