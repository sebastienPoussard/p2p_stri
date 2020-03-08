package gestionnaireRequete;

import java.io.IOException;
import java.net.Socket;

import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief Cette classe gere les requêtes d'un client pour la fonctionnalité "Serveur" de chaque client.
 */
public class GestionnaireRequetesServeur extends GestionnaireRequetes {

	private GestionnaireFichier gestionnaireFichier;	// gestionnaire de fichier
	
	/**
	 * @brief constructeur de GestionnaireRequetesServeur.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetesServeur(Socket socService, GestionnaireFichier gestionnaireFichier) throws IOException {
		super(socService);
		this.gestionnaireFichier = gestionnaireFichier;
	}

	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	@Override
	protected void servirClient(String requete) throws IOException {
		// parser la requête.
		String[] tableauRequete = requete.split(" ");
		String nomFichier = tableauRequete[1];
		int numeroBloc = Integer.parseInt(tableauRequete[2]);
		// verifier que l'on à bien une demande correcte.
		switch(tableauRequete[0]) {
		case "TELECHARGER_BLOC":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "à télécharger le bloc "+numeroBloc+" du fichier "+nomFichier);
			// recuperer les données à envoyer
			byte donnees[] = this.gestionnaireFichier.obtenirDonnees(nomFichier, numeroBloc);
			// envoyer le bloc de données
			this.streamOut.write(donnees);
			this.streamOut.flush();
			this.streamOut.close();
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": TELECHARGER_BLOC <nomDuFichier> <numeroDuBloc> ");
			break;
		}
	}
}
	

