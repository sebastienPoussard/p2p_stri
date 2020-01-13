package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @brief Cette classe gère une connexion à un serveur. 
 */
public class Client {

	private Socket socket;								// socket de connexion au client.
	private BufferedInputStream streamIn;				// flux de données entrants
	private BufferedOutputStream streamOut;				// flux de données sortants (requêtes)
	private GestionnaireFichier gestionnaireFichier;	// gestionnaire de fichiers.

	/**
	 * @brief Constructeur de Client, connecte le client au serveur.
	 * @param ip IP du serveur
	 * @param port Port du serveur
	 * @throws UnknownHostException Cette exception est levée quand l'adresse du serveur n'as pu être résolue.
	 * @throws IOException Cette exception est levée en cas d'erreur à la connexion du serveur.
	 */
	public Client(String ip, int port, GestionnaireFichier gestionnaireFichier) throws UnknownHostException, IOException {
		this.socket = new Socket("localhost", 8080);
		this.streamIn = new BufferedInputStream(this.socket.getInputStream());
		this.streamOut = new BufferedOutputStream(this.socket.getOutputStream());
		this.gestionnaireFichier = gestionnaireFichier;
	}

	/**
	 * @brief traite la demande d'un client.
	 * @param choix choix du client 1 : lister les fichier, 2 télécharger un fichier
	 */
	public void traiter(String choix) {
		switch (choix.substring(0, 1)) {
		case "1":
			// afficher la liste des fichiers en partage sur le serveur
			listeFichier();
			break;
		case "2":
			// télécharger un fichier
			telecharger(choix.substring(1).strip());
			break;
		case "3":
			// terminer la connexion
			terminer();
			break;
		default:
			Messages.getInstance().ecrireMessage("Commande non valable.");
		}
	}

	/**
	 * @brief telecharger un fichier depuis le serveur.
	 * @param nomFichier nom du fichier à télécharger.
	 */
	private void telecharger(String nomFichier) {
		Messages.getInstance().ecrireMessage("Téléchargement du fichier : \""+nomFichier+"\" ...");
		// envoyer l'instruction au serveur.
		try {
			this.streamOut.write(("TELECHARGER "+nomFichier).getBytes());
			this.streamOut.flush();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à l'envoie de la commande TELECHARGER");
		}
		// créer le fichier en local.
		FileOutputStream streamFichier = null;
		try {
			streamFichier = this.gestionnaireFichier.creerFichier(nomFichier);
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("erreur à la création du fichier dans le dossier téléchargement "+nomFichier);
		}
		// télécharger le fichier.
		int marqueur;						// marqueur de positon dans le buffer de "données".
		byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
		try {
			while ((marqueur = this.streamIn.read(donnee)) != -1) {
				streamFichier.write(donnee);
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur pendant le téléchargement du fichier.");
		}
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("le fichier télécharger n'as pas été correctement fermé.");
		}
		Messages.getInstance().ecrireMessage("Téléchargement de "+nomFichier+" terminé !");
	}

	/**
	 * @brief permet de lire la liste des fichiers partagés par le serveur.
	 */
	private void listeFichier() {
		try {
			this.streamOut.write("LISTE".getBytes());
			this.streamOut.flush();
		} catch (IOException e1) {
			Messages.getInstance().ecrireErreur("echec à l'envoie de la commande LISTE");
		}
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la reponse du serveur.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		
		// tant que la socket n'est pas fermée, tenter de lire la réponse du serveur
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
			Messages.getInstance().ecrireErreur("echec à la reception de la liste des fichiers");
		}
		Messages.getInstance().ecrireMessage(resultat);
	}

	/**
	 * @brief Cette méthode permet de termier la connexion et de fermer la socket.
	 */
	public void terminer() {
		try {
			this.socket.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Erreur à la fermeture de la connexion.");
		}
	}


}
