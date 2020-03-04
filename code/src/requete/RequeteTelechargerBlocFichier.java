package requete;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * 
 * @brief cette classe va télécharger un bloc de fichier depuis un serveur.
 */
public class RequeteTelechargerBlocFichier extends Requete {

	private String nomFichier;									// nom du fichier à télécharger.
	private FileChannel channel;								// channel pour écrire le téléchargement.
	private int numeroDuBloc;									// le numéro du bloc à télécharger.
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
			FileChannel channel, int numeroDuBloc) {
		super(adresseServeur);
		this.nomFichier = nomFichier;
		this.numeroDuBloc = numeroDuBloc;
		this.channel = channel;
		try {
			this.channel.position((numeroDuBloc-1)*GestionnaireFichier.TAILLEDEBLOC);
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Erreur au positionnement du FileChannel pour le bloc "+this.numeroDuBloc+" du fichier "+this.nomFichier);
		}
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
		int marqueur;						// marqueur de positon dans le buffer de "données".
		byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
		try {
			while ((marqueur = this.inStream.read(donnee)) > 0) {
				this.channel.write(ByteBuffer.wrap(donnee));
				// si c'est le dernier bloc, sortir de la boucle
				if (marqueur < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur pendant le téléchargement du bloc "+this.numeroDuBloc+" du fichier "+this.nomFichier);
		}
		Messages.getInstance().ecrireMessage("Téléchargement du bloc "+this.numeroDuBloc+" du fichier "+this.nomFichier+" terminé !");
		// fermer le Thread.
		terminer();
	}
}
