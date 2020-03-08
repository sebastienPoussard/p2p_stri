package gestionnaireRequete;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import commun.Messages;

/**
 * @brief Cette classe abstrait permet de gérer les requêtes d'un client.
 */
public abstract class GestionnaireRequetes implements Runnable {

	protected Socket socService;							// socket de la connexion.
	protected BufferedInputStream streamIn;					// flux de requêtes client.
	protected BufferedOutputStream streamOut;				// flux de réponse du serveur au client.
	protected ObjectInputStream objIn;						// stream pour reçevoit les objets.
	protected ObjectOutputStream objOut;					// stream pour envoyer des objets.
	protected String requete;								// requete du client.
	
	/**
	 * @brief constructeur de GestionnaireRequetes.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetes(Socket socService) throws IOException {
		this.socService = socService;
		// ouvrir les fluxs.
		this.streamOut = new BufferedOutputStream(socService.getOutputStream());
		this.streamIn = new BufferedInputStream(socService.getInputStream());
		this.objOut = new ObjectOutputStream(socService.getOutputStream());
		this.objIn = new ObjectInputStream(socService.getInputStream());
	}

	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		try {
			// tant que le client souhaite un service et que la connexion est ouverte.
			while (!((this.requete = lireRequeteClient()).equals("STOP"))) {
				Messages.getInstance().ecrireMessage("requête reçue : "+this.requete);
				servirClient(this.requete);
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Une requête client à échouée, fermeture ");
		}
	}
	
	/**
	 * @brief cette fonction abstraite répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exceptions qui peuvent être remontées en cas de problème.
	 */
	protected abstract void servirClient(String requete) throws IOException;	

	/**
	 * @brief Cette méthode va lire une requête envoyée par le client.
	 * @return retourne la requête sous forme de chaine de caractères.
	 * @throws IOException  léve une exception si la fonction n'arrive pas à lire la requête envoyé par le client.
	 */
	protected String lireRequeteClient() throws IOException {
		if (this.socService.isClosed()) {
			return "STOP";
		}
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		// tant que la socket n'est pas fermée, tenter de lire la requête client.
		while ((marqueur = streamIn.read(donnee) )!= -1) {
			if (marqueur != 0) {
				resultat += new String(donnee, 0, marqueur);
			}
			else {
				continue;
			}
			// si le message est complétement lue, retourner le resultat.
			if (donnee[marqueur] == 0) {
				break;
			}
		}
		// si la connexion est rompue, fermer la socket.
		if (marqueur == -1) {
			this.socService.close();
		}
		if (resultat.isEmpty()) {
			return "STOP";
		} else {
			return resultat;
		}
	}
}
