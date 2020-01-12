package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @brief Cette classe gère les fonctionnalitées de serveur.
 */
public class Serveur {

	private int port;										// port sur lequelle le serveur écoute.
	private String adresseDossierPartage;					// adresse du dossier de partage.
	private GestionnaireFichier gestionnaireFichier;		// gestionnaire de fichier.
	
	/**
	 * @brief constructeur de la classe.
	 * @param port port d'écoute du serveur.
	 * @param adresseDossierPartage adresse du dossier qui contient les fichier partagés par le serveur.
	 */
	public Serveur(int port, String adresseDossierPartage) {
		this.port = port;
		this.adresseDossierPartage = adresseDossierPartage;
	}

	/**
	 * @brief Cette méthode permet de lancer le serveur
	 * @details cette méthode lance le serveur en ouvrant la socket de rendez-vous, si le port est déjà occupé alors
	 * l'erreur est remonté au main.
	 * La méthode va par la suite attendre la venue d'un client puis transferer chaque connexion au gestionnaire de client
	 * pour qu'il traite les requête de chaque client dans un Thread unique.
	 */
	public void lancer() throws IOException {

		ServerSocket socRDV;
		boolean continuer = true;
		
		Messages.getInstance().ecrireMessage("Lancement du serveur...");
		// charger le dossier de partage.
		this.gestionnaireFichier = new GestionnaireFichier(this.adresseDossierPartage, null);
		// ouvre une socket de rendez-vous.
		socRDV = new ServerSocket(port);
		Messages.getInstance().ecrireMessage("Le serveur écoute sur le port : "+port);
		// attendre les clients
		while(continuer) {
			try {
				Socket socService = socRDV.accept();
				Messages.getInstance().ecrireMessage("Nouvelle connexion d'un client("+socService.getRemoteSocketAddress().toString()
						+ ") ouverture du port "+socService.getPort()+" pour servir le client.");
				// quand un nouveau client se connecte, donner le traitement au gestionnaire de client
				// qui est un nouveau thread.
				GestionnaireClient gestionnaireClient = new GestionnaireClient(socService, this.gestionnaireFichier);
				Thread thread = new Thread(gestionnaireClient);
				thread.start();
			} catch (IOException e) {
				Messages.getInstance().ecrireErreur("La connexion d'un client à échoué");
			}
		}
		// fermer la socket de rdv
		socRDV.close();
		// fermer le programme 
	}

}
