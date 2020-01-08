package serveur;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.SocketService;

/**
 * @brief Classe qui permet l'execution du serveur de transfert de fichiers.
 */
public class Serveur {

	public static void main(String[] args) {
		
		// ############### VARIABLES ###############
		ServerSocket socRDV;			// socket de rendez vous pour les nouveaux clients
		Socket socService;				// socket de service pour traiter un client
		ObjectInputStream inStream;		// datastream entrant
		ObjectOutputStream outStream;	// datastream sortant
		String buffer;					// buffer de reception de données
		int port ;						// port d'écoute du serveur
		
		// ############### LANCEMENT DU SERVER ###############
		socRDV = SocketService.ouvrirSocketRDV(port);
		
		
// CODE SAMPLE 
//			try {
//				// tenter ouverture de la socket de service
//				while (true) {
//						socService = socRDV.accept();
//						System.out.println("Nouveau client !");
//						// traitement du client
//						inStream = new ObjectInputStream(socService.getInputStream());
//						outStream = new ObjectOutputStream(socService.getOutputStream());
//						System.out.println("Prêt pour communiquer...");
//						try {
//							// lire la requête envoyée par le client
//							Object o = inStream.readObject();
//							if (o instanceof String ) {
//								buffer = (String)o;
//								// séparer en differentes parties 
//								String[] cmd = buffer.split(" ");
//								String resultat;
//								switch (cmd[0]) {
//								case "CREATION":
//									// creation d'un compte à partir d'un id et d'une somme initiale
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.creation(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "POSITION":
//									// renvoie la somme du compte identifié par id
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										resultat = "OK " + Float.toString(banque.position(Integer.parseInt(cmd[1])));
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "AJOUT":
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.ajout(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "RETRAIT":
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.retrait(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								default:
//									resultat = "ERREUR commande invalide";
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			socRDV.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
