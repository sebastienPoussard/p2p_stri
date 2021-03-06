package terminalClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import commun.Messages;
import gestionnaireRequete.GestionnaireRequetesServeur;

/**
 * @brief Cette classe gère les fonctionnalitées de serveur d'un client.
 */
public class Serveur implements Runnable {

	private int port;										// port sur lequelle le serveur écoute.
	private GestionnaireFichier gestionnaireFichier;		// gestionnaire de fichier.
	private String ipServeurCentral;						// ip du serveur central.
	private int portServeurCentral;							// port du serveur central.
	
	/**
	 * @brief constructeur de la classe
	 * @param port le port d'écoute de la fonctionnalité serveur du client.
	 * @param gestionnaireDeFichier le gestionnaire de fichier.
	 * @param ipServeurCentral l'ip du serveur central.
	 * @param portServeurCentral le port du serveur central.
	 */
	public Serveur(int port, GestionnaireFichier gestionnaireDeFichier, String ipServeurCentral, int portServeurCentral) {
		this.port = port;
		this.ipServeurCentral = ipServeurCentral;
		this.portServeurCentral = portServeurCentral;
		this.gestionnaireFichier = gestionnaireDeFichier;
	}

	/**
	 * @brief méthode pour lancer le Thread.
	 * @details cette méthode lance le serveur en ouvrant la socket de rendez-vous,
	 * la méthode va par la suite attendre la venue d'un client puis transferer chaque connexion au gestionnaire de client
	 * pour qu'il traite les requête de chaque client dans un Thread unique.
	 */
	@Override
	public void run() {

		ServerSocket socRDV;				// socket de rendez-vous pour accepter les nouveaux clients.
		boolean continuer = true;			// continuer ou non le serveur.	
		
		Messages.getInstance().ecrireMessage("Lancement du serveur...");
		// ouvre une socket de rendez-vous.
		try {
			socRDV = new ServerSocket(port);
			Messages.getInstance().ecrireMessage("Le serveur écoute sur le port : "+port);
			// si le serveur à réussit à démarrer, démarrer le thread qui envoie les infos de 
			// l'utilisateur au serveur central.
			GestionnaireInfosUtilisateur gestionnaireInfosUtilisateur = new GestionnaireInfosUtilisateur(this.gestionnaireFichier, this.ipServeurCentral, this.portServeurCentral, this.port);
			Thread threadInfosUtilisateur = new Thread(gestionnaireInfosUtilisateur);
			threadInfosUtilisateur.start();
			// attendre les clients
			while(continuer) {
				try {
					Socket socService = socRDV.accept();
					Messages.getInstance().ecrireMessage("Nouvelle connexion d'un client("+socService.getRemoteSocketAddress().toString()
							+ ") ouverture du port "+socService.getPort()+" pour servir le client.");
					// quand un nouveau client se connecte, donner le traitement au gestionnaire de client
					// qui est un nouveau thread.
					GestionnaireRequetesServeur gestionnaireRequeteServeur = new GestionnaireRequetesServeur(socService, this.gestionnaireFichier);
					Thread thread = new Thread(gestionnaireRequeteServeur);
					thread.start();
				} catch (IOException e) {
					Messages.getInstance().ecrireErreur("La connexion d'un client à échoué");
				}
			}
			try {
				// fermer la socket de rdv
				socRDV.close();
			} catch (IOException e) {
				Messages.getInstance().ecrireErreur("echec de la fermeture de la socket de rendez vous.");
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Echec à l'ouverture du port d'écoute");
		}
	}
}
