package requete;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * 
 * @brief cette classe va télécharger un bloc de fichier depuis un serveur.
 */
public class RequeteTelechargerBlocFichier extends Requete {

	private String nomFichier;									// nom du fichier à télécharger.
	private GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	private String blocDebut;									// numéro du premier bloc à télécharger.
	private String blocFin;										// numéro du dernier bloc à télécharger.
	private FileOutputStream streamFichier;						// le flux pour écrire dans le fichier.
	
	/**
	 * @brief télécharge une partie d'un fichier.
	 * @param adresseServeur adresse du serveur et son port en format IP:PORT.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 * @param blocDebut numéro du premier bloc à télécharger.
	 * @param blocFin numéro du dernier bloc à télécharger.
	 */
	public RequeteTelechargerBlocFichier(String adresseServeur, String nomFichier,
			GestionnaireFichier gestionnaireFichier, String blocDebut, String blocFin) {
		super(adresseServeur);
		this.nomFichier = nomFichier;
		this.gestionnaireFichier = gestionnaireFichier;
		this.blocDebut = blocDebut;
		this.blocFin = blocFin;
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête au serveur.
		envoyerRequete("TELECHARGER_BLOC "+this.nomFichier+" "+this.blocDebut+" "+this.blocFin);
		// créer le dossier pour stocker les fichier temporaire
		this.gestionnaireFichier.creerDossierTemporaire(this.nomFichier);
		// créer le fichier temporaire contenant les blocs souhaités
		
		try {
			this.streamFichier = this.gestionnaireFichier.creerFichierTemporaire(this.nomFichier, 
					this.blocDebut, this.blocFin);
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la création du fichier");
		}
		// télécharger le bloc du fichier.
		int marqueur;						// marqueur de positon dans le buffer de "données".
		byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
		try {
			while ((marqueur = this.inStream.read(donnee)) > 0) {
				streamFichier.write(donnee, 0, marqueur);
				// si c'est le dernier bloc, sortir de la boucle
				if (marqueur < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur pendant le téléchargement du bloc de fichier "+this.nomFichier
					+":"+this.blocDebut+"-"+this.blocFin);
		}
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("le bloc de fichier "+this.nomFichier+":"+this.blocDebut+
					"-"+this.blocFin+" n'as pas été correctement fermé.");
		}
		Messages.getInstance().ecrireMessage("Téléchargement du bloc "+this.nomFichier+":"+this.blocDebut+
				"-"+this.blocFin+" terminé !");
		// fermer le Thread.
		terminer();
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à la fermeture du fichier");
		}
	}
}
