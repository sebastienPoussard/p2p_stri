package common;

import java.io.IOException;
import java.net.ServerSocket;

/** @brief Cette classe permet de gérer les sockets
 */
public class SocketService {
	
	/** @brief Cette fonction permet permet d'ouvrir une socket de rendez vous sur un port donnée
	 * @param port le numéro du port qui sera ouvert pour le socket de rendez vous
	 * @return retourne la socket en cas de réussite.
	 */
	public ServerSocket ouvrirSocketRDV(int port) {
		
		ServerSocket res = null;
		try {
			res = new ServerSocket(port);
		} catch (IOException e) {
			Messages.ecrireErreur("La socket de rendez vous n'a pas pu être crée");
			e.printStackTrace();
			System.exit(0);
		}
		return res;
	}

}
