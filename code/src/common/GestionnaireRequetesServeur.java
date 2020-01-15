package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @brief Cette classe gere les requêtes d'un client 
 */
public class GestionnaireRequetesServeur implements Runnable {

	private Socket socService;							// socket de la connexion.
	private BufferedInputStream streamIn;				// flux de requêtes client.
	private BufferedOutputStream streamOut;				// flux de réponse du serveur au client.
	private String requete;								// requete du client.
	private GestionnaireFichier gestionnaireFichier;	// gestionnaire de fichier
	
	/**
	 * @brief constructeur de GestionnaireRequetesServeur.
	 * @param socService la socket de service qui permet de communiquer avec le client.
	 * @throws IOException exception qui survient à la création du stream
	 */
	public GestionnaireRequetesServeur(Socket socService, GestionnaireFichier gestionnaireFichier) throws IOException {
		this.socService = socService;
		// ouvrir les fluxs
		this.streamIn = new BufferedInputStream(socService.getInputStream());
		this.streamOut = new BufferedOutputStream(socService.getOutputStream());
		// affecter le gestionnaire de fichier
		this.gestionnaireFichier = gestionnaireFichier;
	}

	
	/**
	 * @brief methode pour lancer le traitement des requêtes d'un client
	 */
	@Override
	public void run() {
		try {
			// tant que le client souhaite un service et que la connexion est ouverte.
			while ((this.requete = lireRequeteClient()) != "STOP" && !this.socService.isClosed()) {
				Messages.getInstance().ecrireMessage("requête reçue : "+this.requete);
				servirClient(this.requete);
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Une requête client à échouée, fermeture ");
			// fermer le thread en cas d'erreur sur les socket.
			return;
		}
	}
	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	private void servirClient(String requete) throws IOException {
		String[] tableauRequete = requete.split(" ");
		switch(tableauRequete[0]) {
		case "LISTE":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+
					" demande la liste des fichiers");
			// envoyer la liste des fichiers sur le serveur.
			this.streamOut.write(this.gestionnaireFichier.listeFichiers().getBytes());
			this.streamOut.flush();
			break;
		case "TELECHARGER":
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
		case "TELECHARGER_BLOC":
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

	/**
	 * @brief permet d'envoyer le contenu d'un stream de fichier sur le stream de la socket client.
	 * @param streamFichier stream d'un fichier.
	 */
	private void envoyerFichier(FileInputStream streamFichier) {
		int taille;
		byte[] buffer = new byte[1024];
		try {
			// si le stream du fichier est null, c'est à dire si le fichier n'à pas été trouvé par le gestionnaire
			// de fichier, envoyer une réponse d'erreur au client.
			if (streamFichier == null ) {
				this.streamOut.write("FICHIER INEXISTANT".getBytes());
				this.streamOut.flush();
			} else {
				// sinon envoyer le fichier par blocs
				while ((taille = streamFichier.read(buffer) ) > 0 ) {
					this.streamOut.write(buffer, 0, taille);
				}
				this.streamOut.flush();
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à l'envoie du fichier au client.");
		}
	}
	
	/**
	 * @brief envoie une partie d'un fichier de l'octet debut à l'octet fin.
	 * @param streamFichier stream du fichier à envoyer
	 * @param debut numero du premier bloc à envoyr
	 * @param fin numero du dernier bloc à envoyer
	 */
	private void envoyerfichier(FileInputStream streamFichier, String debutString, String finString, long tailleFichier) {
		long debut;		// numéro du début du bloc à envoyer.
		long fin;		// numéro du dernier bloc à envoyer.
		long taille;		// taille envoyé par passage.
		// convertir le parametre de début.
		
		if (debutString.equals("START")) {
			debut = 0;
		} else {
			debut = Long.parseLong(debutString);
		}
		// convertir le parametre de fin.
		if (finString.equals("END")) {
			fin = tailleFichier;
		} else {
			fin = Long.parseLong(finString);
		}
		long tailleRestantAEnvoyer = fin - debut;
		byte[] buffer = new byte[1024];
		// si le stream du fichier est null, c'est à dire si le fichier n'à pas été trouvé par le gestionnaire
		// de fichier, envoyer une réponse d'erreur au client.
		long envoyer =0;
		try {
			if (streamFichier == null) {
				this.streamOut.write("FICHIER INEXISTANT".getBytes());
				this.streamOut.flush();
			} else {
				// avancer jusqu'au début du bloc à envoyer
				if (debut != 0) {
					streamFichier.skip(debut);
				}
				// envoyer le bloc de fichier
				while ((taille = streamFichier.read(buffer) ) > 0 && tailleRestantAEnvoyer > 0 ) {
					this.streamOut.write(buffer, 0, (int)Long.min(taille, tailleRestantAEnvoyer));
					tailleRestantAEnvoyer -= Long.min(taille, tailleRestantAEnvoyer);
				}
				this.streamOut.flush();
			}
		} catch (IOException e) {
		}
		Messages.getInstance().ecrireMessage("bloc du fichier envoyé");
	}

	/**
	 * @brief Cette méthode va lire une requête envoyée par le client.
	 * @return retourne la requête sous forme de chaine de caractères.
	 * @throws IOException  léve une exception si la fonction n'arrive pas à lire la requête envoyé par le client.
	 */
	private String lireRequeteClient() throws IOException {
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		
		// tant que la socket n'est pas fermée, tenter de lire la requête client.
		while ((marqueur = streamIn.read(donnee) )!= -1) {
			resultat += new String(donnee, 0, marqueur);
			// si le message est complétement lue, retourner le resultat.
			if (donnee[marqueur] == 0) {
				break;
			}
		}
		// si la connexion est rompue, fermer la socket.
		if (marqueur == -1) {
			this.socService.close();
		}
		return resultat;
	}
}
