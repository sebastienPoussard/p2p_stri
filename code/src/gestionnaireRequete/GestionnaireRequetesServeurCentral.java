package gestionnaireRequete;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import centralServer.ListeDesFichiersComplets;
import centralServer.ListeDesInfoUtilisateur;
import centralServer.Ratios;
import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

/**
 * @brief Cette classe gere les requêtes d'un client vers le serveur central.
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
					ListeDesInfoUtilisateur.getInstance().ajouterUtilisateur(infos.getIp()+":"+infos.getPort(), infos);
					// ajouter les fichiers complets à la liste des fichiers complet.
					ajouterFichiersCompletsALaListe(infos);
				}
			} catch (ClassNotFoundException e) {
				Messages.getInstance().ecrireErreur("Impossible de récuperer les informations sur l'utilisateur.");
			}
			break;
		// l'utilisateur souhaite obtenir la liste des fichiers téléchargeables.
		case "LISTE":
			// message d'information;
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "la liste des fichiers");
			// envoyer la liste des fichiers
			this.objOut.writeObject(ListeDesFichiersComplets.getInstance().obtenirListeDesFichiersComplets());
			this.objOut.flush();
			break;
		// l'utilisateur souhaite télécharger un fichier, il veut la liste des utilisateur ayant ce fichier.
		case "DOWNLOAD":
			String portClient = tableauRequete[1];
			String fichier = tableauRequete[2];
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande"
					+ " la listes des utilisateurs possédant le fichier "+fichier);
			// verifier que l'utilisateur à un ratio correcte.
			String ipClient = this.socService.getRemoteSocketAddress().toString().split(":")[0];
			ipClient = ipClient.split("/")[1];
			String adresseClient = ipClient+":"+portClient;
			// si le ratio est bon envoyer les informations à l'utilisateur
			if (Ratios.getInstance().ratioEstBon(adresseClient)) {
				// créer la listes d'information à renvoyer à l'utilisateur.
				HashMap<String, ListeDeBlocs> listeUtilisateursAyantLeFichier = ListeDesInfoUtilisateur.getInstance().obtenirLaListeDesUtilisateursAyantLeFichier(fichier);
				// envoyer la listes des utilisateurs ayant le fichier.
				this.objOut.writeObject(listeUtilisateursAyantLeFichier);
				this.objOut.flush();
			} else {
				// sinon envoyer un message d'erreur pour signaler au client que son ration n'est pas suffisant pour télécharger.
				this.objOut.writeObject("KO!");
			}
			break;
		// quand l'utilisateur envoie une confirmation qu'il à reçue des données d'un autre serveur.
		case "RECUE":
			// récuperer les adresse.
			String adresseReceveur = tableauRequete[1];
			String adresseEmetteur = tableauRequete[2];
			// récuperer la quantitée et la convertir en Ko.
			long quantitee = (Long.parseLong(tableauRequete[3]))/1000;
			System.out.println("emetteur : "+adresseEmetteur);
			System.out.println("qté (Ko): "+quantitee);
			System.out.println("receveur : "+adresseReceveur);
			// ajouter les informations aux Ratios
			Ratios.getInstance().ajouterInfos(adresseReceveur, adresseEmetteur, quantitee);
			System.out.println("ratios : "+Ratios.getInstance().ratioEstBon(adresseEmetteur));
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": UPDATE, LISTE, DOWNLOAD");
			break;
		}
	}
	
	/**
	 * @brief permet de mettre à jour la liste des fichier complets quand un utilisateur envoie un UPDATE.
	 * @param infos les infos de l'utilisateur.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
