package commun;

import java.util.TreeSet;

public class ListeDeBlocs {
	
	private TreeSet<Integer> listeDesBlocs;
	
	public ListeDeBlocs() {
		this.listeDesBlocs = new TreeSet<Integer>();
	}
	
	public void ajouterUnBloc(int numero) {
		this.listeDesBlocs.add(numero);
	}
	
	public void ajouterIntervalle(int debut, int fin) {
		for (int i = debut; i <= fin; i++) {
			this.listeDesBlocs.add(i);
		}
	}
	
	

}
