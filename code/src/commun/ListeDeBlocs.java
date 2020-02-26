package commun;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

public class ListeDeBlocs implements Serializable {
	
	private static final long serialVersionUID = -5513855359356595609L;
	private TreeSet<Integer> listeDesBlocs;
	private long tailleDuFichier;
	

	public ListeDeBlocs() {
		this.listeDesBlocs = new TreeSet<Integer>();
		this.tailleDuFichier = -1;
	}
	
	public long obtenirTailleDuFichier() {
		return tailleDuFichier;
	}
	
	/**
	 * TODO : renvoyer erreur si < 0
	 * 
	 * @param tailleDuFichier
	 */
	public void definirTailleDuFichier(long tailleDuFichier) {
		if (tailleDuFichier >= 0) {
			this.tailleDuFichier = tailleDuFichier;
		} else {
			this.tailleDuFichier = -1;
		}
	}
	public void ajouterUnBloc(int numero) {
		this.listeDesBlocs.add(numero);
	}
	
	public void ajouterIntervalle(int debut, int fin) {
		for (int i = debut; i <= fin; i++) {
			this.listeDesBlocs.add(i);
		}
	}
	
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
