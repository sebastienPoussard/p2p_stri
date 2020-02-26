package requete;

import java.io.IOException;

import commun.InfoUtilisateur;
import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief Cette classe permet de créer un Thread qui va demander au serveur central s'il à un fichier.
 * le serveur central renvoie la liste des utilisateurs possédant le fichier ainsi que pour chaqu'un 
 * la liste des blocs.
 * Puis le thread se connecte aux differents serveur et télécharge les blocs.
 */
public class RequeteInfoServeurCentralTelechargerFichier extends Requete {

	private GestionnaireFichier gestionnaireDeFichiers;
	private String nomDuFichier;
	
	/**
	 * @brief constructeur de la classe RequeteListe
	 * @param adresseServeurCentral adresse et port du serveur de la forme "IP:PORT".
	 */
	public RequeteInfoServeurCentralTelechargerFichier(String adresseServeurCentral, 
			GestionnaireFichier gestionnaireDeFichiers, String nomDuFichier) {
		// connexion au serveur central.
		super(adresseServeurCentral);
		this.nomDuFichier = nomDuFichier;
		this.gestionnaireDeFichiers = gestionnaireDeFichiers;
	}

	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête pour obtenir la liste des utilisateurs qui detiennent le fichier recherché.
		envoyerRequete("FICHIER "+this.nomDuFichier);
		// lire la réponse du serveur.
		
		
		
		// fermer le Thread.
		terminer();
	}

}
