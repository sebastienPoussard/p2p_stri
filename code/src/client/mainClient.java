package client;

import java.util.ArrayList;
import java.util.Scanner;


import common.GestionnaireFichier;
import common.Messages;
import requete.RequeteListe;
import requete.RequeteTelechargerFichier;

public class mainClient {
	
	public static void main(String[] args) {
		
		// récuperer IP et PORT
		ArrayList<String[]> listeDesServeurs;							// liste des serveurs.
		String adresseDossierTelechargements = "/tmp/downloads/";		// adresse du dossier qui va recevoir les fichiers du serveur.
		GestionnaireFichier gestionnaireFichier;					// gestionnaire de fichier
		Scanner scanner;
		
		// créer la liste des serveurs.
		String serveur1 = "localhost:8080";
		String serveur2 = "localhost:8081";
		
		// ouvrir le gestionnaire de fichier 
		gestionnaireFichier = new GestionnaireFichier(null, adresseDossierTelechargements);
		
		// traiter les demandes du client.
		scanner = new Scanner(System.in);
		String reponse;
		String[] choix;
		// tant que l'utilisateur ne choisit pas de quitter l'application, récuperer son choix et effectuer le traitement
		do {
			Messages.getInstance().ecrireMessage("#################### MENU ##############################");
			Messages.getInstance().ecrireMessage("# 1                : obtenir la liste des fichiers     #");
			Messages.getInstance().ecrireMessage("# 2 <nom_fichier>  : telecharger fichier               #");
			Messages.getInstance().ecrireMessage("# 3 <nom_fichier>  : télécharger depuis plusieurs srv  #");
			Messages.getInstance().ecrireMessage("# 4                : quitter le programme              #");
			Messages.getInstance().ecrireMessage("########################################################");
			reponse = scanner.nextLine();
			choix = reponse.split(" ");
			// traiter le choix du client.
			switch (choix[0]) {
			case "1":
				RequeteListe requeteListe = new RequeteListe(serveur1);
				Thread threadListe = new Thread(requeteListe);
				threadListe.run();
				break;
			case "2":
				RequeteTelechargerFichier requeteTelecharger = new RequeteTelechargerFichier(serveur1, choix[1], gestionnaireFichier);
				Thread threadTelecharger = new Thread(requeteTelecharger);
				threadTelecharger.run();
				break;
			case "3":
				
				break;
			case "4":
				Messages.getInstance().ecrireMessage("Fermeture de l'application...");
				break;
			default:
				Messages.getInstance().ecrireMessage("Commande incorrecte");
				break;
			}
		} while  (!choix[0].equals("4"));
		scanner.close();
		Messages.getInstance().ecrireMessage("Client terminé.");
	}
}