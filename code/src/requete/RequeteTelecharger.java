package requete;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief cette classe va télécharger un bloc de fichier depuis un serveur.
 */
public class RequeteTelecharger extends Requete {

	private int numeroDuBloc;									// le numéro du bloc à télécharger.
	private GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	private RandomAccessFile fichier;							// le fichier.
	private String nomFichier;									// le nom du fichier.
	private String adresseServeurCentral;						// adresse du serveur central.
	private String adresseServeur;								// adresse du serveur chez qui on télécharge.
	private int portServeur;									// port du serveur de l'utilisateur.
	
	/**
	 * @brief constructeur de la classe permettant de télécharger une bloc d'un fichier depuis un serveur.
	 * @param adresseServeur adresse du serveur et son port en format <IP>:<PORT>.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 * @param numeroDuBloc numéro du bloc du fichier que l'on souhaite télécharger.
	 */
	public RequeteTelecharger(String adresseServeurCentral ,String adresseServeur, RandomAccessFile fichier, String nomFichier,
			GestionnaireFichier gestionnaireFichier, int numeroDuBloc, int portServeur) {
		super(adresseServeur);
		this.adresseServeurCentral = adresseServeurCentral;
		this.adresseServeur = adresseServeur;
		this.numeroDuBloc = numeroDuBloc;
		this.gestionnaireFichier = gestionnaireFichier;
		this.fichier = fichier;
		this.nomFichier = nomFichier;
		this.portServeur = portServeur;
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête au serveur.
		envoyerRequete("TELECHARGER_BLOC "+this.nomFichier+" "+this.numeroDuBloc);
		// télécharger le bloc du fichier.
		byte[] buffer;
		ByteArrayOutputStream donnees = new ByteArrayOutputStream();
		int tailleLue = 0;		// le nombre de byte lues.
		int tailleTotale = 0;	// le nombre de byte totaux lues.
		do {
			buffer = new byte[GestionnaireFichier.TAILLEDEBLOC];
			try {
				tailleLue = this.inStream.read(buffer);
				if (tailleLue == -1)
					break;
				donnees.write(buffer, 0, tailleLue);
				tailleTotale += tailleLue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (donnees.size() < GestionnaireFichier.TAILLEDEBLOC || tailleLue != -1);
		// écrire les données lues dans le fichier.
		this.gestionnaireFichier.ecrire(donnees.toByteArray(), tailleTotale, this.fichier, this.numeroDuBloc);
		Messages.getInstance().ecrireMessage("Téléchargement du bloc "+this.numeroDuBloc+" du fichier "+this.nomFichier+" terminé !");
		// prevenir le serveur central que le serveur à envoyé des données
		String adresseLocale = this.socket.getInetAddress().getHostAddress()+":"+this.portServeur;
		RequeteEnvoieMessage requete = new RequeteEnvoieMessage(adresseServeurCentral, "RECUE "+adresseLocale+" "+this.adresseServeur+" "+tailleTotale );
		Thread th = new Thread(requete);
		th.start();
		// fermer le Thread.
		terminer();
	}
}
