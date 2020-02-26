package gestionnaireRequete;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

/**
 * @brief Cette classe gere les requêtes d'un client 
 */
public class GestionnaireRequetesServeurCentral extends GestionnaireRequetes {

	private HashMap<String, InfoUtilisateur> BD;
	private HashMap<String, Long> listeDesFichiers;
	
	/**
	 * @brief constructeur de GestionnaireRequetesServeur.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetesServeurCentral(Socket socService, HashMap<String, InfoUtilisateur> BD) throws IOException {
		super(socService);
		this.BD = BD;
		this.listeDesFichiers = new HashMap<String, Long>();
	}

	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	@Override
	protected synchronized void servirClient(String requete) throws IOException {
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
					// reconstruire la liste des fichiers.
					this.listeDesFichiers = construireListeDesFichiers();
					System.out.println("construction des fichier vide ? "+this.listeDesFichiers.isEmpty());
				}
			} catch (ClassNotFoundException e) {
				Messages.getInstance().ecrireErreur("Impossible de récuperer les informations sur l'utilisateur.");
			}
			break;
		case "LISTE":
			// message d'information);

			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "la liste des fichiers");
			// envoyer la liste des fichiers
			System.out.println("la liste à envoyer est null ?"+this.listeDesFichiers.isEmpty());
			this.objOut.writeObject(this.listeDesFichiers);
			this.objOut.flush();
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": UPDATE,  ");
			break;
		}
	}
	/**
	 * TODO a terminer
	 * @return
	 */
	private HashMap<String, Long> construireListeDesFichiers() {
		HashMap<String, Long> res = new HashMap<String, Long>();
		// parcourir les informations de chaque utilisateur
		Iterator iterateur = this.BD.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry utilisateurEntree = (Entry)iterateur.next();
			InfoUtilisateur utilisateur = (InfoUtilisateur) utilisateurEntree.getValue();
			// obtenir la liste des fichiers complet de l'utilisateur.
			HashMap<String, Long> fichiersComplets = utilisateur.obtenirLaListeDesFichiersComplets();
			// parcourir les fichier de l'utilisateur.
			Iterator iterateurFichiers = fichiersComplets.entrySet().iterator();
			while (iterateurFichiers.hasNext()) {
				Map.Entry<String, Long> unFichier = (Map.Entry<String, Long>)iterateurFichiers.next();
				// si le fichier n'existe pas dans la liste de fichier, l'ajouter.
				if (!res.containsKey(unFichier.getKey())) {
					res.put(unFichier.getKey(), unFichier.getValue());
				}
			}
		}
		return res;
	}
}	
