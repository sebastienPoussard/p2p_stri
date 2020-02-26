package gestionnaireRequete;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import commun.Messages;

/**
 * @brief Cette classe gere les requêtes d'un client 
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
		// ouvrir les fluxs
		this.streamOut = new BufferedOutputStream(socService.getOutputStream());
		this.streamIn = new BufferedInputStream(socService.getInputStream());
		this.objOut = new ObjectOutputStream(socService.getOutputStream());
		this.objIn = new ObjectInputStream(socService.getInputStream());
		// affecter le gestionnaire de fichier
	}

	
	/**
	 * @brief methode pour lancer le traitement des requêtes d'un client
	 */
	@Override
	public void run() {
		try {
			// tant que le client souhaite un service et que la connexion est ouverte.
			while ((this.requete = lireRequeteClient()) != "STOP" && !this.socService.isClosed()) {
				Messages.getInstance().ecrireMessage("requête reçue : "+this.requete);
				servirClient(this.requete);
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Une requête client à échouée, fermeture ");
			// fermer le thread en cas d'erreur sur les socket.
			return;
		}
	}
	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException 
	 */
	protected abstract void servirClient(String requete) throws IOException;	

	/**
	 * @brief Cette méthode va lire une requête envoyée par le client.
	 * @return retourne la requête sous forme de chaine de caractères.
	 * @throws IOException  léve une exception si la fonction n'arrive pas à lire la requête envoyé par le client.
	 */
	protected String lireRequeteClient() throws IOException {
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		
		// tant que la socket n'est pas fermée, tenter de lire la requête client.
		while ((marqueur = streamIn.read(donnee) )!= -1) {
			resultat += new String(donnee, 0, marqueur);
			// si le message est complétement lue, retourner le resultat.
			if (donnee[marqueur] == 0) {
				break;
			}
		}
		// si la connexion est rompue, fermer la socket.
		if (marqueur == -1) {
			this.socService.close();
		}
		return resultat;
	}
}
