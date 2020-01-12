package common;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @brief Cette classe gere le dossier contenant les fichiers partagés ainsi que le dossier de téléchargement.
 */
public class GestionnaireFichier {
	
	private File dossierDePartage;			// dossier contenant les fichiers partagés.
	private File dossierDeTelechargements;	// dossier ocntenant les fichiers téléchargés.
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminDossierPartages, String cheminDossierTelechargements) {
		// pour l'instant (client et serveur sont séparés) on fait les construction en fonction de si
		// le serveur ou le client requpete la création de l'objet.
		if (cheminDossierPartages != null ) {
			this.dossierDePartage = new File(cheminDossierPartages);
			Messages.getInstance().ecrireMessage("Le serveur partage le dossier : "+cheminDossierPartages);
		}
		if (cheminDossierTelechargements != null ) {
			this.dossierDeTelechargements = new File(cheminDossierTelechargements);
			Messages.getInstance().ecrireMessage("dossier de téléchargement : "+cheminDossierTelechargements);
		}
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
