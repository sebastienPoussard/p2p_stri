package terminalClient;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import common.GestionnaireFichier;
import common.Messages;
import common.Serveur;
import requete.RequeteListe;
import requete.RequeteTelechargerBlocFichier;
import requete.RequeteTelechargerFichier;

public class TerminalMain {

	public static void main(String[] args) {
		//variable de lancements
		int portServeur = 8081;										// port du serveur de partage
		String adresseDossierPartage = "/tmp/share";				// adresse du dossier de fichiers partagés
		String adresseDossierTelechargement = "/tmp/dowloads";		// adresse du dossier de fichiers téléchargés
		
		
		//lancer le serveur.
		//###############################################################################
		// lancer le serveur de partage.
		Serveur serveur = new Serveur(portServeur, adresseDossierPartage);
		Thread serveurThread = new Thread(serveur);
		serveurThread.start();
		
		//lancer le client
		//###############################################################################
		// récuperer IP et PORT
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
						// répartire la téléchargement entre plusieurs serveurs gerés par plusieurs threads.
						RequeteTelechargerBlocFichier requete1 = new RequeteTelechargerBlocFichier(serveur1, choix[1], gestionnaireFichier, "START", "10000");
						Thread thread1 = new Thread(requete1);
						RequeteTelechargerBlocFichier requete2 = new RequeteTelechargerBlocFichier(serveur2, choix[1], gestionnaireFichier, "10000", "END");
						Thread thread2 = new Thread(requete2);
						thread1.start();
						thread2.start();
						// attendre que les threads se terminent (il ne reste plus que le main).
						while (Thread.activeCount() != 1) {
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (InterruptedException e) {
								Messages.getInstance().ecrireErreur("echec à l'attente que tous les Thread soit terminés");
							}
						}
						// quand tous les blocs du fichier sont téléchargés, réassembler le fichier final.
						try {
							gestionnaireFichier.reformer(choix[1]);
						} catch (IOException e) {
							Messages.getInstance().ecrireErreur("Echec à la reformation du fichier final "+choix[1]); 
						}
						Messages.getInstance().ecrireMessage("Fichier "+choix[1]+" téléchargé en bloc avec succés !");
						break;
					case "4":
						Messages.getInstance().ecrireMessage("Fermeture de l'application...");
						break;
					default:
						Messages.getInstance().ecrireMessage("Commande incorrecte");
						break;
					}
				} while  (!choix[0].equals("4"));
				// fermer les différents éléments
				scanner.close();
				serveurThread.interrupt();
				Messages.getInstance().ecrireMessage("Client terminé.");
	}
}
