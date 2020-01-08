package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @brief Gere les requêtes d'un client 
 */
public class GestionnaireClient implements Runnable {

	private Socket socService;					// socket de la connexion.
	private BufferedInputStream streamIn;		// flux de données entrant.
	private BufferedOutputStream streamOut;		// flux de données sortant.
	
	public GestionnaireClient(Socket socService) throws IOException {
		this.socService = socService;
		// ouvrir les flux de données entrant et sortant.
		// !!!!!!!!! attention au renvoi d'erreur, à modifier !!!!!!!!!!!
		this.streamIn = new BufferedInputStream(this.socService.getInputStream());
		this.streamOut = new BufferedOutputStream(this.socService.getOutputStream());
	}

	@Override
	public void run() {
		// ouvrir les stream
		
		// tanque le client à des requêtes
		// effectuer la requête du client
	}

}
