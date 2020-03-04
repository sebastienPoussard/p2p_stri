package gestionnaireRequete;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import centralServer.ListeDesFichiersComplets;
import centralServer.ListeDesInfoUtilisateur;
import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

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
		// l'utilisateur souhaite envoyer ses informations.
		case "UPDATE":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+
					" envoie ses informations.");
			// recuperer les informations de l'utilisateur.
			Object obj;
			try {
				obj = this.objIn.readObject();
				if (obj instanceof InfoUtilisateur) {
					InfoUtilisateur infos = (InfoUtilisateur) obj;
					// ajouter les infos utilisateur à la liste des infos utilisateur.
					System.out.println(infos);
					ListeDesInfoUtilisateur.getInstance().ajouterUtilisateur(infos.getIp(), infos);
					// ajouter les fichiers complets à la liste des fichiers complet.
					ajouterFichiersCompletsALaListe(infos);
				}
			} catch (ClassNotFoundException e) {
				Messages.getInstance().ecrireErreur("Impossible de récuperer les informations sur l'utilisateur.");
			}
			break;
		case "LISTE":
			// message d'information;
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "la liste des fichiers");
			// envoyer la liste des fichiers
			this.objOut.writeObject(ListeDesFichiersComplets.getInstance().obtenirListeDesFichiersComplets());
			this.objOut.flush();
			break;
		case "DOWNLOAD":
			String fichier = tableauRequete[1];
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande"
					+ " la listes des utilisateurs possédant le fichier "+fichier);
			// créer la listes d'information à renvoyer à l'utilisateur.
			HashMap<String, ListeDeBlocs> listeUtilisateursAyantLeFichier = ListeDesInfoUtilisateur.getInstance().obtenirLaListeDesUtilisateursAyantLeFichier(fichier);
			// envoyer la listes des utilisateurs ayant le fichier.
			this.objOut.writeObject(listeUtilisateursAyantLeFichier);
			this.objOut.flush();
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": UPDATE, LISTE, DOWNLOAD");
			break;
		}
	}
	/**
	 * @param infos
	 */
	private void ajouterFichiersCompletsALaListe(InfoUtilisateur infos) {
		// parcourir les informations de chaque utilisateur
		HashMap<String, Long> listeDesFichiersComplets = infos.obtenirLaListeDesFichiersComplets();
		Iterator iterateur = listeDesFichiersComplets.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry<String, Long> fichier = (Entry<String, Long>) iterateur.next();
			ListeDesFichiersComplets.getInstance().ajouterFichier(fichier.getKey(), fichier.getValue());
		}
	}
}	
