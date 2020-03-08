package centralServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import commun.Messages;
import gestionnaireRequete.GestionnaireRequetesServeurCentral;

public class ServeurCentral {

	private int port;									// port d'écoute du serveur central.
	private ServerSocket socRDV;						// socket de rendez-vous.
	private boolean continuer;							// continuer le traitement.
	
	/**
	 * @brief constructeur de ServeurCentral
	 * @param port port sur lequel va écouter le serveur central.
	 */
	public ServeurCentral(int port) {
		this.port = port;
		this.continuer = true;
	}

	public void lancer() {
		// ouvrir la socket de rendez-vous
		try {
			socRDV = new ServerSocket(port);
			Messages.getInstance().ecrireMessage("Le serveur central écoute sur le port : "+port);
			// attendre les clients
			while(continuer) {
				try {
					Socket socService = socRDV.accept();
					Messages.getInstance().ecrireMessage("Nouvelle connexion d'un client("+socService.getRemoteSocketAddress().toString()
							+ ") ouverture du port "+socService.getPort()+" pour servir le client.");
					// quand un nouveau client se connecte, donner le traitement au gestionnaire de client.
					GestionnaireRequetesServeurCentral gestionnaireRequeteServeurCentral = new GestionnaireRequetesServeurCentral(socService);
					Thread thread = new Thread(gestionnaireRequeteServeurCentral);
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
