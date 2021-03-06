package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.Alumno;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerAddPayment {

	@FXML
	private TextField textAmount;
	@FXML
	private TextField textDate;
	@FXML
	private ListView<Alumno> listVAlumnos;
	@FXML
	private Button bAdd;
	@FXML
	private Button bBack;
	
	private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private int rsInsert;
	
	private Alumno alumno;
	
	private ObservableList<Alumno> listAlumnos =  FXCollections.observableArrayList();
	
	public void initialize() {
		String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        
		
        bAdd.setOnAction(e -> addPayment());
        bBack.setOnAction(e -> back());
        
        //Se establece la conexion con la base de datos
        con = ControllerDB.getConnection();
        
        //Se crea una coleccion de objetos Alumno
        String query = "SELECT * FROM usuario  WHERE tipo_usuario = 'Alumno'";
        try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				alumno = new Alumno(rs.getString("nombre_usuario"),rs.getString("nombre"),rs.getString("apellidos"),
						rs.getString("DNI"),rs.getString("tipo_usuario"),rs.getString("sexo"), rs.getString("telefono"), rs.getString("id_grupo"), rs.getString("evaluacion"));
				
				listAlumnos.add(alumno);
			}
			listVAlumnos.setItems(listAlumnos);
			
			listVAlumnos.setCellFactory(param -> new ListCell<Alumno>() {
				protected void updateItem(Alumno a, boolean empty) {
			        super.updateItem(a, empty);
			        if (empty || a == null || a.getNombre() == null) {
			        	setText("");
			        } else {
			        	setText(a.getUsername());
			        }
				}
			});
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
    public void cambio(Parent newRoot) {
    	Stage primaryStage = (Stage) bAdd.getScene().getWindow();
		primaryStage.getScene().setRoot(newRoot);	
    }
	
	public void addPayment() {		
		String query = "INSERT INTO `pago` (`cantidad`, `fecha`, `dni_alumno`) VALUES " + "('"+ textAmount.getText() +"', '"+ textDate.getText() +"', '"+
						listVAlumnos.getSelectionModel().getSelectedItem().getDNI()+"')";
		
		try {
			stmt = con.createStatement();
			rsInsert = stmt.executeUpdate(query);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void back() {
		try {
			con.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Parent newRoot;
		try {
			newRoot = FXMLLoader.load(getClass().getResource("/View/Administrador.fxml"));
			Stage primaryStage = (Stage) bBack.getScene().getWindow();
			primaryStage.setTitle("Administrador");
			primaryStage.getScene().setRoot(newRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
