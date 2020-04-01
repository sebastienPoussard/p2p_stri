package terminalClient;

import java.util.Scanner;

import commun.Messages;
import gestionnaireRequete.GestionnaireDeTelechargement;
import requete.RequeteListe;

public class TerminalMain {

	public static void main(String[] args) {
		//variable de lancements
		int portServeur = 8081;										// port du serveur de partage.
		String adresseDossierTelechargement = "/tmp/downloads1";		// adresse du dossier de fichiers téléchargés.
		Scanner scanner;											// scanner pr lire les E/S de l'utilisateur.
		String ipServeurCentral;									// ip du serveur central.
		int portServeurCentral;										// port du serveur central.
		String adresseServeurCentral;
		
		//créer le gestionnaire de fichiers
		GestionnaireFichier gestionnaireDeFichier = new GestionnaireFichier(adresseDossierTelechargement);
		
		//lancer le serveur.
		//###############################################################################
		// instancier les valeurs du serveur central.
		ipServeurCentral = "localhost";
		portServeurCentral = 9090;
		adresseServeurCentral = ipServeurCentral+":"+portServeurCentral;
		// lancer le serveur de partage.
		Serveur serveur = new Serveur(portServeur, gestionnaireDeFichier, ipServeurCentral, portServeurCentral);
		Thread serveurThread = new Thread(serveur);
		serveurThread.start();
		
		//lancer le client
		//###############################################################################
				
		scanner = new Scanner(System.in);
		String reponse;
		String[] choix;
		// tant que l'utilisateur ne choisit pas de quitter l'application, récuperer son choix et effectuer le traitement
		do {
			Messages.getInstance().ecrireMessage("#################### MENU ##############################");
			Messages.getInstance().ecrireMessage("# 1                : obtenir la liste des fichiers     #");
			Messages.getInstance().ecrireMessage("# 2 <nom_fichier>  : telecharger fichier               #");
			Messages.getInstance().ecrireMessage("# 3                : quitter le programme              #");
			Messages.getInstance().ecrireMessage("########################################################");
			reponse = scanner.nextLine();
			choix = reponse.split(" ");
			// traiter le choix du client.
			switch (choix[0]) {
			case "1":
				RequeteListe requeteListe = new RequeteListe(adresseServeurCentral);
				Thread threadListe = new Thread(requeteListe);
				threadListe.start();
				break;
			case "2":
				GestionnaireDeTelechargement gestionnaire = new GestionnaireDeTelechargement(adresseServeurCentral, choix[1], gestionnaireDeFichier);
				Thread thread = new Thread(gestionnaire);
				thread.start();
				break;
			case "3":
				Messages.getInstance().ecrireMessage("Fermeture de l'application...");
				break;
			default:
				Messages.getInstance().ecrireMessage("Commande incorrecte");
				break;
			}
		} while  (!choix[0].equals("3"));
		// fermer les différents éléments
		scanner.close();
		serveurThread.interrupt();
		Messages.getInstance().ecrireMessage("Client terminé.");
	}
}
