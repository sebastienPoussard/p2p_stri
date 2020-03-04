package gestionnaireRequete;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.ListeDeBlocs;
import commun.Messages;
import requete.Requete;
import requete.RequeteTelechargerBlocFichier;
import terminalClient.GestionnaireFichier;

/**
 * 
 * @brief cette classe va télécharger un fichier depuis un ou plusieurs autres clients.
 */
public class GestionnaireDeTelechargement extends Requete {

	private String nomFichier;									// nom du fichier à télécharger.
	private GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	private HashMap<String, ListeDeBlocs> listeDesUtilisateursAyantLeFichier;
	private RandomAccessFile fluxFichier;
	private long tailleDuFichier;
	private int nbrDeBlocs;
	
	/**
	 * @brief télécharge un fichier chez un ou plusieurs serveurs.
	 * @param adresseServeurCentral adresse du serveur central et son port en format IP:PORT.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 */
	public GestionnaireDeTelechargement(String adresseServeurCentral, String nomFichier,
			GestionnaireFichier gestionnaireFichier) {
		super(adresseServeurCentral);
		this.nomFichier = nomFichier;
		this.gestionnaireFichier = gestionnaireFichier;
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur central et récuperer les fluxs.
		super.run();
		// envoyer la requête au serveur pour obtenir la listes des utilisateurs et les blocs qu'ils ont
		// pour le fichier que l'on souhaite télécharger.
		envoyerRequete("DOWNLOAD "+this.nomFichier);
		// lire la réponse du serveur central.
		String reponse = "";
		Object obj;
		try {
			obj = this.objIn.readObject();
			this.listeDesUtilisateursAyantLeFichier = (HashMap<String, ListeDeBlocs>)obj;
		} catch (IOException | ClassNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la reception de la liste de la liste des utilisateurs ayant le fichier.");
		}
		// construire le fichier "vide" dans le dossier des fichiers incomplets et récuperer le flux.
		try {
			this.tailleDuFichier = determinerTailleDuFichier(this.listeDesUtilisateursAyantLeFichier);
			this.fluxFichier = this.gestionnaireFichier.creerFichier(this.nomFichier, this.tailleDuFichier);
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la création du fichier vide "+this.nomFichier);
		}
		// calculer le nbr de blocs.
		float nombreDeBlocsFlottant = (this.tailleDuFichier/((float)GestionnaireFichier.TAILLEDEBLOC));
		this.nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
		System.out.println("nbr de blocs : "+this.nbrDeBlocs);
		// tenter de telecharger tous les blocs.
		int numeroBloc = 1;
		while (numeroBloc <= this.nbrDeBlocs) {
			boolean tourPourRien = true;
			Iterator iterateur = this.listeDesUtilisateursAyantLeFichier.entrySet().iterator();
			while (iterateur.hasNext()) {
				Map.Entry utilisateur = (Entry)iterateur.next();
				ListeDeBlocs blocs = (ListeDeBlocs) utilisateur.getValue();
				if (blocs.detientLeBloc(numeroBloc)) {
					// créer un nouveau channel pour que le thread écrive.
					FileChannel channel = this.fluxFichier.getChannel();
					// telecharger chez la personne le bloc
					String adresse = (String) utilisateur.getKey();
					RequeteTelechargerBlocFichier requete = new RequeteTelechargerBlocFichier(adresse, this.nomFichier, channel, numeroBloc);
					Thread thread = new Thread(requete);
					thread.start();
					// incrémenter le bloc à télécharger
					numeroBloc++;
					tourPourRien = false;
				}
			}
			// si le bloc n'est disponible chez personne, le sauter.
			if (tourPourRien == true) {
				numeroBloc++;
			}
		}
		// fermer le Thread.
		terminer();
	}
	
	/**
	 * @brief methode pour obtenir la taille du fichier.
	 * @param liste la liste des utilisateurs ayant le fichier.
	 * @return renvoie la taille du fichier final.
	 */
	private long determinerTailleDuFichier(HashMap<String, ListeDeBlocs> liste) {
		long res = 0;
		Iterator iterateur = liste.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry utilisateur = (Entry)iterateur.next();
			// obtenir la liste de bloc de l'utilisateur.
			ListeDeBlocs blocs  = (ListeDeBlocs) utilisateur.getValue();
			// récuperer la taille du fichier
			if (blocs.obtenirTailleDuFichier() != -1) {
				res = blocs.obtenirTailleDuFichier();
				break;
			}
		}
		return res;
	}
}
