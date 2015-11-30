package ch.makery.address.view;



import java.time.LocalDate;

import org.controlsfx.dialog.Dialogs;

import com.sun.glass.ui.Screen;

import ch.makery.address.model.Person;
import ch.makery.address.util.DateUtil;import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class PersonEditDialogController {
    /**
     * param ceiling el efecto onda sobre la imagen
     */
	private Ellipse ceiling;
	@FXML
	private ImageView imageViewEinstein;
	private ImageView ceiling_image;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField cityField;
    @FXML
    private DatePicker birthdayField;

    @FXML
    private BorderPane mainBorderPane;
    
    private static double TAMANO_PANTALLA=Screen.getMainScreen().getHeight();
    private Stage dialogStage;
    private Person person;
    private boolean okClicked = false;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	System.out.println(mainBorderPane.getChildren().size());
    	System.out.println(mainBorderPane.getChildren().get(1).toString());
    	//imageViewEinstein.setEffect(new GaussianBlur(10));
    	initializeCeiling(mainBorderPane);
    	imageViewEinstein.setClip(ceiling);
    	

    }

    
    private void initializeCeiling(BorderPane root) {
        ceiling = new Ellipse();
        ceiling.centerXProperty().bind(root.widthProperty().multiply(0.35));
        ceiling.centerYProperty().bind(root.heightProperty().multiply(0.1));
        ceiling.radiusXProperty().bind(root.widthProperty().multiply(0.5));
        ceiling.radiusYProperty().bind(root.heightProperty().multiply(0.25));
    }
    
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.setHeight(TAMANO_PANTALLA);
        this.dialogStage.centerOnScreen();
        this.dialogStage.setResizable(false);
        
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setPerson(Person person) {
        this.person = person;

        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        streetField.setText(person.getStreet());
        postalCodeField.setText(Integer.toString(person.getPostalCode()));
        cityField.setText(person.getCity());
        birthdayField.setOnAction(e->{
        	person.setBirthday(birthdayField.getValue());
        });
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            person.setFirstName(firstNameField.getText());
            person.setLastName(lastNameField.getText());
            person.setStreet(streetField.getText());
            person.setPostalCode(Integer.parseInt(postalCodeField.getText()));
            person.setCity(cityField.getText());
            person.setBirthday(birthdayField.getValue());

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "No valid first name!\n"; 
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n"; 
        }
        if (streetField.getText() == null || streetField.getText().length() == 0) {
            errorMessage += "No valid street!\n"; 
        }

        if (postalCodeField.getText() == null || postalCodeField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n"; 
        } else {
            // try to parse the postal code into an int.
            try {
                Integer.parseInt(postalCodeField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid postal code (must be an integer)!\n"; 
            }
        }

        if (cityField.getText() == null || cityField.getText().length() == 0) {
            errorMessage += "No valid city!\n"; 
        }

        if ( birthdayField.getValue() == null){
        	errorMessage += "Mete una fecha\n"; 
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Dialogs.create()
                .title("Invalid Fields")
                .masthead("Please correct invalid fields")
                .message(errorMessage)
                .showError();
            return false;
        }
    }
}