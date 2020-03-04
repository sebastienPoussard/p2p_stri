package gestionnaireRequete;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * @brief Cette classe gere les requêtes d'un client 
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
		// affecter le gestionnaire de fichier
		this.gestionnaireFichier = gestionnaireFichier;
	}

	
	/**
	 * @brief cette fonction répond aux demande du client.
	 * @param requete requete demandé par l'utilisateur
	 * @throws IOException exception levée par la méthode envoyerListe()
	 */
	@Override
	protected void servirClient(String requete) throws IOException {
		String[] tableauRequete = requete.split(" ");
		String nomFichier = tableauRequete[1];
		int numeroBloc = Integer.parseInt(tableauRequete[2]);
		switch(tableauRequete[0]) {
		case "TELECHARGER_BLOC":
			// message d'information
			Messages.getInstance().ecrireMessage("Utilisateur "+this.socService.getInetAddress()+" demande "
					+ "à télécharger le bloc "+numeroBloc+" du fichier "+nomFichier);
			// recuperer le stream du fichier.
			RandomAccessFile fichier = this.gestionnaireFichier.rechercherFichier(nomFichier);
			FileChannel channel = fichier.getChannel();
			// positionner le channel sur le bloc désiré.
			channel.position((numeroBloc-1)*GestionnaireFichier.TAILLEDEBLOC);
			// envoyer le bloc
			envoyerfichier(channel);
			break;
		default:
			// si le message n'est pas correcte.
			Messages.getInstance().ecrireErreur("La requête client ne correspond pas au bon standard"
					+ ": TELECHARGER_BLOC <nomDuFichier> <numeroDuBloc> ");
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
	private void envoyerfichier(FileChannel channel) {
		byte[] buffer = new byte[GestionnaireFichier.TAILLEDEBLOC];
		ByteBuffer bytebuffer = ByteBuffer.wrap(buffer);
		try {
			// lire la partie à envoyer
			channel.read(bytebuffer);
			// puis l'envoyer.
			this.streamOut.write(bytebuffer.array(), 0, GestionnaireFichier.TAILLEDEBLOC);
			
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur à l'envoie du bloc...");
		}
		
		
	}
}
