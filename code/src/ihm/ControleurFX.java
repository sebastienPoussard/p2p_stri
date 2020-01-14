package ihm;

import MainApp.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public abstract class ControleurFX {
	
	protected MainApp mainApp;
	protected Stage fenetre;
	
	@FXML
	protected Label menu;
	
	@FXML
	public void retourPageAccueil() {
		this.mainApp.showPage("accueil");
	}
	
	public void setMainApp(MainApp main) {
		this.mainApp = main;
	}
	
	public void setFenetre(Stage fenetre) {
		this.fenetre = fenetre;
	}
}
