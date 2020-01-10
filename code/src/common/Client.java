package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @brief Cette classe gère une connexion à un serveur. 
 */
public class Client {

	private Socket socket;						// socket de connexion au client.
	private BufferedInputStream streamIn;		// flux de données entrants
	private BufferedOutputStream streamOut;		// flux de données sortants (requêtes)

	/**
	 * @brief Constructeur de Client, connecte le client au serveur.
	 * @param ip IP du serveur
	 * @param port Port du serveur
	 * @throws UnknownHostException Cette exception est levée quand l'adresse du serveur n'as pu être résolue.
	 * @throws IOException Cette exception est levée en cas d'erreur à la connexion du serveur.
	 */
	public Client(String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket("localhost", 8080);
		this.streamIn = new BufferedInputStream(this.socket.getInputStream());
		this.streamOut = new BufferedOutputStream(this.socket.getOutputStream());
	}

	/**
	 * @brief traite la demande d'un client.
	 * @param choix choix du client 1 : lister les fichier, 2 télécharger un fichier
	 */
	public void traiter(int choix) {
		switch (choix) {
		case 1:
			// afficher la liste des fichiers en partage sur le serveur
			try {
				this.streamOut.write("LISTE".getBytes());
				this.streamOut.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
			int marqueur;							// marqueur de position dans le buffer "donnee".
			String resultat = "";
			
			// tant que la socket n'est pas fermée, tenter de lire la requête client.
			try {
				while ((marqueur = this.streamIn.read(donnee) )!= -1) {
					resultat += new String(donnee, 0, marqueur);
					// si le message est complétement lue, retourner le resultat.
					if (donnee[marqueur] == 0) {
						break;
					}
				}
				// si la connexion est rompue, fermer la socket.
				if (marqueur == -1) {
					this.streamIn.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Messages.getInstance().ecrireMessage(resultat);
			break;
		case 2:
			break;
		case 3:
			break;
		default:
			Messages.getInstance().ecrireMessage("Commande non valable.");
		}
	}

	public void terminer() {
		try {
			this.socket.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Erreur à la fermeture de la connexion.");
			e.printStackTrace();
		}
	}


}
