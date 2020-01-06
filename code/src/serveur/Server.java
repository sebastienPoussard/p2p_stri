package serveur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.Dossier;
import common.Messages;

public class Server {

	public static void main(String[] args) {
		
		// ######################  VARIABLES  ######################
		int port = 0;		// port d'écoute du serveur
		Dossier dossier;	// dossier comportant les fichier à partager
		Messages message = new Messages();
		String cheminFichiers = "/tmp/files/";
		
		
		// ##################  LANCER LE SERVEUR  ###################
		message.ecrire("Lancement du serveur sur le port "+port+" ...");
		try {
			// creation du registre
			Registry registre = LocateRegistry.createRegistry(port);
			
			// créer l'objet dossier qui est accessible à distance pour les clients puis le lier au registre
			dossier = new Dossier(cheminFichiers);
			registre.bind("dossier", dossier);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.ecrire("Serveur pret à servir des fichiers dans "+cheminFichiers);
		


	}

}
