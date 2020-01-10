package serveur;
import java.io.IOException;

import common.Messages;
import common.Serveur;

/**
 * @brief Classe qui permet l'execution du serveur de transfert de fichiers.
 */
public class MainServeur {

	public static void main(String[] args) {
		
		// récuperer le port.
		int port = 8080;
		// récuperer le dossier l'adresse du dossier contenant les fichiers partagés.
		String adresseDossierPartage = "/tmp/share";
		// lancer le serveur.
		Serveur serveur = new Serveur(port, adresseDossierPartage);
		try {
			serveur.lancer();
		} catch (IOException e) {
			e.printStackTrace();
			Messages.getInstance().ecrireErreur("Echec du lancement du serveur, le port est peut être déjà utilisé");
		}
	}
}
