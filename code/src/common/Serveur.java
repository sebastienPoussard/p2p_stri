package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @brief Cette classe gère les fonctionnalitées de serveur.
 */
public class Serveur {

	private int port;
	
	/**
	 * @brief constructeur de la classe.
	 * @param port port d'écoute du serveur.
	 */
	public Serveur(int port) {
		this.port = port;
	}

	/**
	 * @brief Cette méthode permet de lancer le serveur
	 * @details cette méthode lance le serveur en ouvrant la socket de rendez-vous, si le port est déjà occupé alors
	 * l'erreur est remonté au main.
	 * La méthode va par la suite attendre la venue d'un client puis transferer chaque connexion au gestionnaire de client.
	 */
	public void lancer() throws IOException {

		ServerSocket socRDV;
		boolean continuer = true;
		
		// ouvre une socket de rendez-vous.
		Messages.getInstance().ecrireMessage("Lancement du serveur...");
		socRDV = new ServerSocket(port);
		Messages.getInstance().ecrireMessage("Le serveur écoute sur le port "+port);
		
		
		// attendre les clients
		while(continuer) {
			try {
				Socket socService;
				socService = socRDV.accept();
				
				// quand un nouveau client se connecte, donner le traitement au gestionnaire de client
				GestionnaireClient gestionnaireClient = new GestionnaireClient();
				gestionnaireClient.traiterClient(socService);
			} catch (IOException e) {
				e.printStackTrace();
				Messages.getInstance().ecrireErreur("La connexion d'un client à échoué");
			}
			
		}
		// fermer la socket de rdv
		socRDV.close();
		// fermer le programme 
	}

}
