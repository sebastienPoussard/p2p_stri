package requete;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import common.GestionnaireFichier;
import common.Messages;

/**
 * @brief cette classe va télécharger un fichier depuis un serveur.
 */
public class RequeteTelechargerFichier extends Requete {

	private String nomFichier;									// nom du fichier à télécharger.
	private GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	private FileOutputStream streamFichier;						// le flux pour écrire dans le fichier.
	
	/**
	 * @brief télécharge un fichier depuis un serveur.
	 * @param adresseServeur adresse du serveur et son port en format IP:PORT.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 */
	public RequeteTelechargerFichier(String adresseServeur, String nomFichier, GestionnaireFichier gestionnaireFichier) {
		super(adresseServeur);
		this.nomFichier = nomFichier;
		this.gestionnaireFichier = gestionnaireFichier;
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête pour télécharger le fichier.
		envoyerRequete("TELECHARGER "+this.nomFichier);
		// créer le fichier vide en local.
		try {
			FileOutputStream streamFichier = this.gestionnaireFichier.creerFichier(this.nomFichier);
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("erreur à la création du fichier dans le dossier téléchargement "+this.nomFichier);
		}
		// télécharger le fichier.
		int marqueur;						// marqueur de positon dans le buffer de "données".
		byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
		try {
			while ((marqueur = this.inStream.read(donnee)) > 0) {
				streamFichier.write(donnee, 0, marqueur);
				// si c'est le dernier bloc, sortir de la boucle
				if (marqueur < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur pendant le téléchargement du fichier.");
		}
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("le fichier télécharger n'as pas été correctement fermé.");
		}
		Messages.getInstance().ecrireMessage("Téléchargement de "+this.nomFichier+" terminé !");
		// fermer le Thread.
		terminer();
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Echec à la fermeture du fichier");
		}
	}
}
