package commun;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @brief Cette classe représente une liste de bloc d'un fichier d'un utilisateur
 */
public class ListeDeBlocs implements Serializable {
	
	private static final long serialVersionUID = -5513855359356595609L;
	private TreeSet<Integer> listeDesBlocs;				// on stoque la liste des blocs dans un TreeSet.
	private long tailleDuFichier;						// la taille du fichier si celui-ci est complet.
	
	/**
	 * @brief constructeur de la classe.
	 */
	public ListeDeBlocs() {
		this.listeDesBlocs = new TreeSet<Integer>();
		// par defaut on ne connait pas la taille du fichier, donc -1
		this.tailleDuFichier = -1;
	}
	
	/**
	 * @brief permet d'obtenir la taille du fichier
	 * @return renvoie la taille du fichier en byte, renvoie -1 si la taille n'est pas connue.
	 */
	public long obtenirTailleDuFichier() {
		return tailleDuFichier;
	}
	
	/**
	 * @brief permet de savoir si le fichier à un bloc.
	 * @param numeroDuBloc le numéro du bloc à tester.
	 * @return
	 */
	public boolean detientLeBloc(int numeroDuBloc) {
		if (this.listeDesBlocs.contains(numeroDuBloc) || this.tailleDuFichier != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @brief permet de définir la taille du fichier si celui-ci est complet.
	 * @param tailleDuFichier la taille en nombre de byte du fichier.
	 */
	public void definirTailleDuFichier(long tailleDuFichier) {
		if (tailleDuFichier >= 0) {
			this.tailleDuFichier = tailleDuFichier;
		} else {
			this.tailleDuFichier = -1;
		}
	}
	
	/**
	 * @brief ajouter un nouveau bloc à la liste des blocs.
	 * @param numero le numéro du bloc à ajouter à la liste.
	 */
	public void ajouterUnBloc(int numero) {
		this.listeDesBlocs.add(numero);
	}
	
	/**
	 * @brief permet d'afficher la liste des blocs, utilisé pour le debug ou l'affichage d'informations.
	 */
	@Override
	public String toString() {
		String res = "";
		Iterator<Integer> iterateur = this.listeDesBlocs.iterator();
		while (iterateur.hasNext()) {
			res += iterateur.next()+",";
		}
		return res;
	}
	
	

}
