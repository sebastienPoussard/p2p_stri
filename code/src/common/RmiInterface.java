package common;

public interface RmiInterface extends java.rmi.Remote {
	
	// methdode pour télécharger un fichier depuis le serveur
	public byte[] telechargerFichier(String nomFichier);
	public String listeDesFichiers();

}
