package common;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @brief Cette classe gere le fichier partagés. 
 */
public class GestionnaireFichier {
	
	File dossierDePartage;			// dossier contenat les fichiers partagés
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminFichiers chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminFichiers) {
		this.dossierDePartage = new File(cheminFichiers);
		Messages.getInstance().ecrireMessage("Le serveur partage le dossier : "+cheminFichiers);
	}
	
	
	public String listeFichiers() {
		String resultat = "";
		// recuperer la liste des fichier dans le dossier
		File[] listeDeFichiers = this.dossierDePartage.listFiles();
		for (File fichier : listeDeFichiers) {
			// ajouter le nom du fichier
			resultat+= fichier.getName()+" ";
			// ajouter la taille convertie du fichier
			long poids = fichier.length();
			if (poids >= 1000 && poids < 1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/1000.0) + " Ko";
			} else if ( poids >= 1000*1000 && poids < 1000*1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/(1000.0*1000.0)) + " Mo";
			} else if ( poids >= 1000*1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/(1000.0*1000.0*1000.0)) + " Go";
			} else {
				resultat += poids + " octets";
			}
			resultat += "\n";
		}
		return resultat;
	}
	
	
	
	
	

}
