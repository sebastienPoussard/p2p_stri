package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import common.Client;
import common.GestionnaireFichier;
import common.Messages;

public class mainClient {
	
	public static void main(String[] args) {
		
		// récuperer IP et PORT
		String ip = "localhost";								// IP du serveur.
		int port = 8080;										// Port du serveur.
		Client client = null;									// une connexion client au serveur.
		String adresseDossierTelechargements = "/tmp/rcv/";		// adresse du dossier qui va recevoir les fichiers du serveur.
		GestionnaireFichier gestionnaireFichier;				// gestionnaire de fichier
		
		// ouvrir le gestionnaire de fichier 
		gestionnaireFichier = new GestionnaireFichier(null, adresseDossierTelechargements);
		
		// connecter le client au serveur
		try {
			client = new Client(ip, port, gestionnaireFichier);
			Messages.getInstance().ecrireMessage("Connexion au serveur "+ip+":"+port+" avec succès");
			// traiter les demandes du client.
			Scanner scanner = new Scanner(System.in);
			String choix;
			// tant que l'utilisateur ne choisit pas de quitter l'application, récuperer son choix et effectuer le traitement
			do {
				Messages.getInstance().ecrireMessage("#################### MENU ##########################");
				Messages.getInstance().ecrireMessage("# 1               : Obtenir la liste des fichiers  #");
				Messages.getInstance().ecrireMessage("# 2 <nom_fichier> : telecharger le fichier         #");
				Messages.getInstance().ecrireMessage("# 3               : quitter le programme           #");
				Messages.getInstance().ecrireMessage("####################################################");
				choix = scanner.nextLine();
				client.traiter(choix);
			} while  (!choix.equals("3"));
		} catch (UnknownHostException e) {
			Messages.getInstance().ecrireErreur("Connexion impossible au serveur, l'adresse serveur n'a pas pu être résolue");
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Problème à la connexion au serveur.");
		}
		client.terminer();
		Messages.getInstance().ecrireMessage("Client terminé.");
		
		
		
		// tant que le client n'arréte pas
		// envoyer une requête au serveur
		// lire la réponse
		
	}
}
		
		
		
//		// TEST DE CLIENT POUR VALIDER FONCTIONNEMENT DU SERVEUR
//		
//		Socket s = new Socket("localhost", 8080);
//		BufferedOutputStream b = new BufferedOutputStream(s.getOutputStream());
//		BufferedInputStream ib = new BufferedInputStream(s.getInputStream());
//		
//		b.write("LISTE".getBytes());
//		b.flush();
//		TimeUnit.SECONDS.sleep(3);
//		
//		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
//		int marqueur;							// marqueur de position dans le buffer "donnee".
//		String resultat = "";
//		
//		// tant que la socket n'est pas fermée, tenter de lire la requête client.
//		while ((marqueur = ib.read(donnee) )!= -1) {
//			resultat += new String(donnee, 0, marqueur);
//			// si le message est complétement lue, retourner le resultat.
//			if (donnee[marqueur] == 0) {
//				break;
//			}
//		}
//		// si la connexion est rompue, fermer la socket.
//		if (marqueur == -1) {
//			s.close();
//		}
//		Messages.getInstance().ecrireMessage(resultat);
//				
//
//		s.close();
//		
//		// FIN TEST
//		
//	}
//	
//
//}


		
		
