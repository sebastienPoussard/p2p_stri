package gestionnaireRequete;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


import commun.InfoUtilisateur;
import commun.Messages;

/**
 * @brief Cette classe gere les requêtes d'un client 
 */
public class GestionnaireRequetesServeurCentral extends GestionnaireRequetes {

	private HashMap<String, InfoUtilisateur> BD;
	
	/**
	 * @brief constructeur de GestionnaireRequetesServeur.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetesServeurCentral(Socket socService, HashMap<String, InfoUtilisateur> BD) throws IOException {
		super(socService);
		this.BD = BD;
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
					// les ajouter à la Base de Données.
					System.out.println(infos);
					this.BD.put(infos.getIp(), infos);
				}
			} catch (ClassNotFoundException e) {
				Messages.getInstance().ecrireErreur("Impossible de récuperer les informations sur l'utilisateur.");
			}
			break;
//		case "LISTE":
//			// message d'information
//			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
//					+ "à télécharger le fichier : "+tableauRequete[1]);
//			// envoyer le fichier.
//			try {
//				envoyerFichier(this.gestionnaireFichier.rechercherFichier(tableauRequete[1]));
//				Messages.getInstance().ecrireMessage("Fichier "+tableauRequete[1]+" envoyé");
//			} catch (FileNotFoundException e) {
//				Messages.getInstance().ecrireErreur("Le fichier à partager "+tableauRequete[1]+" n'a pas été trouvé");
//			}
//			break;
//		case "C":
//			// message d'information
//			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
//					+ "à télécharger le fichier : "+tableauRequete[1]+" du bloc "+tableauRequete[2]+
//					" à "+tableauRequete[3]);
//			// envoyer le bloc
//			envoyerfichier(this.gestionnaireFichier.rechercherFichier(tableauRequete[1]), 
//					tableauRequete[2], tableauRequete[3],
//					this.gestionnaireFichier.tailleFichier(tableauRequete[1]));
//			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": UPDATE,  ");
			break;
		}
	}
}	
