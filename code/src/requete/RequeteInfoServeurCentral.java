package requete;

import java.io.IOException;

import commun.InfoUtilisateur;
import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief Cette classe permet de créer un Thread qui va demander à un serveur la liste des fichiers qu'il contient.
 */
public class RequeteInfoServeurCentral extends Requete {

	private GestionnaireFichier gestionnaireDeFichiers;
	private InfoUtilisateur infoUtilisateur;
	
	/**
	 * @brief constructeur de la classe RequeteListe
	 * @param adresseServeur adresse et port du serveur de la forme "IP:PORT".
	 */
	public RequeteInfoServeurCentral(String adresseServeur, GestionnaireFichier gestionnaireDeFichiers) {
		super(adresseServeur);
		this.gestionnaireDeFichiers = gestionnaireDeFichiers;
		this.infoUtilisateur = new InfoUtilisateur(adresseServeur.split(":")[0], Integer.parseInt(adresseServeur.split(":")[1]));
	}

	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// preparer les infos à envoyer au serveur central
		this.gestionnaireDeFichiers.remplirListeDesFichiers(this.infoUtilisateur);
		
		// envoyer la requête pour lister les fichiers.
		envoyerRequete("LISTE");
		// lire la réponse du serveur.
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la reponse du serveur.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		
		// tant que la socket n'est pas fermée, tenter de lire la réponse du serveur
		try {
			while ((marqueur = this.inStream.read(donnee) )!= -1) {
				resultat += new String(donnee, 0, marqueur);
				// si le message est complétement lue, retourner le resultat.
				if (donnee[marqueur] == 0) {
					break;
				}
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à la reception de la liste des fichiers");
		}
		Messages.getInstance().ecrireMessage(resultat);
		// fermer le Thread.
		terminer();
	}

}
