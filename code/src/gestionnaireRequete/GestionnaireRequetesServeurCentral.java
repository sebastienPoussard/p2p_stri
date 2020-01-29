package gestionnaireRequete;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import commun.ListeDeBlocs;
import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief Cette classe gere les requêtes d'un client 
 */
public class GestionnaireRequetesServeurCentral extends GestionnaireRequetes {

	/**
	 * @brief constructeur de GestionnaireRequetesServeur.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetesServeurCentral(Socket socService) throws IOException {
		super(socService);
	}

	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	@Override
	protected void servirClient(String requete) throws IOException {
		String[] tableauRequete = requete.split(" ");
		switch(tableauRequete[0]) {
		case "A":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+
					" demande la liste des fichiers");
			// envoyer la liste des fichiers sur le serveur.
			ListeDeBlocs l = new ListeDeBlocs();
			this.streamOut.write(this.gestionnaireFichier.listeFichiers().getBytes());
			this.streamOut.flush();
			break;
		case "B":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "à télécharger le fichier : "+tableauRequete[1]);
			// envoyer le fichier.
			try {
				envoyerFichier(this.gestionnaireFichier.rechercherFichier(tableauRequete[1]));
				Messages.getInstance().ecrireMessage("Fichier "+tableauRequete[1]+" envoyé");
			} catch (FileNotFoundException e) {
				Messages.getInstance().ecrireErreur("Le fichier à partager "+tableauRequete[1]+" n'a pas été trouvé");
			}
			break;
		case "C":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "à télécharger le fichier : "+tableauRequete[1]+" du bloc "+tableauRequete[2]+
					" à "+tableauRequete[3]);
			// envoyer le bloc
			envoyerfichier(this.gestionnaireFichier.rechercherFichier(tableauRequete[1]), 
					tableauRequete[2], tableauRequete[3],
					this.gestionnaireFichier.tailleFichier(tableauRequete[1]));
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": LISTE, TELECHARGER, TELECHARGER_BLOC ");
			break;
		}
	}
}	
