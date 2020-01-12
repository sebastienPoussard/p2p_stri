package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @brief Cette classe gere les requêtes d'un client 
 */
public class GestionnaireClient implements Runnable {

	private Socket socService;							// socket de la connexion.
	private BufferedInputStream streamIn;				// flux de requêtes client.
	private BufferedOutputStream streamOut;				// flux de réponse du serveur au client.
	private String requete;								// requete du client.
	private GestionnaireFichier gestionnaireFichier;	// gestionnaire de fichier
	
	
	/**
	 * @brief constructeur de GestionnaireClient.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireClient(Socket socService, GestionnaireFichier gestionnaireFichier) throws IOException {
		this.socService = socService;
		// ouvrir les fluxs
		this.streamIn = new BufferedInputStream(socService.getInputStream());
		this.streamOut = new BufferedOutputStream(socService.getOutputStream());
		// affecter le gestionnaire de fichier
		this.gestionnaireFichier = gestionnaireFichier;
	}

	
	/**
	 * @brief methode pour lancer le traitement des requêtes d'un client
	 */
	@Override
	public void run() {
		try {
			// tant que le client souhaite un service et que la connexion est ouverte.
			while ((this.requete = lireRequeteClient()) != "STOP" && !this.socService.isClosed()) {
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
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	private void servirClient(String requete) throws IOException {
		switch (requete) {
			case "LISTE":
				// renvoyer la liste des fichiers sur le serveur
				Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande la liste des fichiers");
				envoyerListe(this.gestionnaireFichier.listeFichiers());
				break;
			case "TELECARGER":
				// telcharger un fichier
				break;
			default:
				Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
						+ ": LISTE, TELECHARGER ");
		}
	}

	
	/**
	 * @brief envoie au client la liste des fichiers partagés par le serveur.
	 * @param listeFichiers liste des fichiers (nom, taille) partagés par le serveur.
	 * @throws IOException exception levée si on arrive pas à envoyer des données au client.
	 */
	private void envoyerListe(String listeFichiers) throws IOException {
		this.streamOut.write(listeFichiers.getBytes());
		this.streamOut.flush();
	}


	/**
	 * @brief Cette méthode va lire une requête envoyée par le client.
	 * @return retourne la requête sous forme de chaine de caractères.
	 * @throws IOException  léve une exception si la fonction n'arrive pas à lire la requête envoyé par le client.
	 */
	private String lireRequeteClient() throws IOException {
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
