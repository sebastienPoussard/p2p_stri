package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;

/**
 * @brief Gere les requêtes d'un client 
 */
public class GestionnaireClient implements Runnable {

	private Socket socService;					// socket de la connexion.
	private BufferedInputStream streamIn;		// flux de données entrant.
	private BufferedOutputStream streamOut;		// flux de données sortant.
	
	/**
	 * @brief constructeur de GestionnaireClient.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireClient(Socket socService) throws IOException {
		this.socService = socService;
		// ouvrir les flux de données entrant et sortant.
		this.streamIn = new BufferedInputStream(this.socService.getInputStream());
		this.streamOut = new BufferedOutputStream(this.socService.getOutputStream());
	}

	/**
	 * @brief methode pour lancer le traitement des requêtes d'un client
	 */
	@Override
	public void run() {
		String requete = "vide";
		while(true) {
			try {
				requete = new String(streamIn.readAllBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(requete);
		}
		
		// tanque le client à des requêtes
		// effectuer la requête du client
	}
}
