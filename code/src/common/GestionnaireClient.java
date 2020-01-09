package common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @brief Gere les requêtes d'un client 
 */
public class GestionnaireClient implements Runnable {

	private Socket socService;					// socket de la connexion.
	private BufferedInputStream streamIn;		// flux de requêtes client.
	private String requete;						// requete du client.
	
	/**
	 * @brief constructeur de GestionnaireClient.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireClient(Socket socService) throws IOException {
		this.socService = socService;
		// ouvrir le flux de requête clients.
		this.streamIn = new BufferedInputStream(socService.getInputStream());
	}

	/**
	 * @brief methode pour lancer le traitement des requêtes d'un client
	 */
	@Override
	public void run() {
		
		System.out.println(lireRequeteClient());
//		try {
//			byte[] donnee = new byte[1024];
//			int marqueur;
//			
//			// tant que le client ne ferme pas la socket, servir les requêtes du client.
//			while ((marqueur = streamIn.read(donnee) )!= -1) {
//				this.requete += new String(donnee, 0, marqueur);
//				// si le message est complétement lue, afficher le résultat.
//				if (donnee[marqueur] == 0) {
//					Messages.getInstance().ecrireMessage(requete);
//				}
//			}
//		} catch (IOException e) {
//			Messages.getInstance().ecrireErreur("probléme à la lecture de la requête client.");
//			e.printStackTrace();
//		}
	}
	
	/**
	 * @brief Cette méthode va lire une requête envoyée par le client.
	 * @return retourne la requête sous forme de chaine de caractères.
	 */
	private String lireRequeteClient() {
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		
		try {
			// tant que la socket n'est pas fermée, tenter de lire la requête client.
			while ((marqueur = streamIn.read(donnee) )!= -1) {
				this.requete += new String(donnee, 0, marqueur);
				// si le message est complétement lue, retourner le resultat.
				if (donnee[marqueur] == 0) {
					return requete;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
