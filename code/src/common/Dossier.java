package common;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Cette classe est un objet représentant un dossier contenant les fichiers à télécharger.
// Serializable pour être transferer sur le réseau.
// extends UnicastRemoteObject pour que l'object soit accessible à distance via RMI
// implemente les méthodes de RmiInterface (télécharger et lister les fichiers)
public class Dossier extends UnicastRemoteObject implements RmiInterface, Serializable {
	
	public Dossier(String adresse) throws RemoteException {
		// création du dossier de téléchargement
		File dossier = new File(adresse);
		// ?????????
	}

	@Override
	public byte[] telechargerFichier(String nomFichier) {
		return null;
	}

	@Override
	public String listeDesFichiers() {
		return null;
	}

}
