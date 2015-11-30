package ch.makery.address.view;

import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;

import org.controlsfx.dialog.Dialogs;

import ch.makery.address.control.MainApp;
import ch.makery.address.model.Person;
import ch.makery.address.util.DateUtil;

/**
 * Controlador de la clase PersonOverview.
 * 
 * @author Miguel Halys
 * @author https://github.com/migueldam91
 * @version 1.0
 */
public class PersonOverviewController {
	@FXML
	private TableView<Person> personTable;
	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> lastNameColumn;

	/**
	 * param rect,str,sctr,rottr y filtr variables necesarias para la animación
	 * 
	 */
	private Rectangle rect;
	private SequentialTransition str;
	private ScaleTransition sctr;
	private RotateTransition rottr;
	private FillTransition fltr;
	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label streetLabel;
	@FXML
	private Label postalCodeLabel;
	@FXML
	private Label cityLabel;
	@FXML
	private Label birthdayLabel;

	@FXML
	private SplitPane mainSplitPane;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public PersonOverviewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws Exception
	 */
	@FXML
	private void initialize() throws Exception {
		// Initialize the person table with the two columns.
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
		showPersonDetails(null);
		personTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));
		Pane panelPrueba = (Pane) mainSplitPane.getItems().get(1).lookup("#panelPrueba");
		
		//Cojo el panel de la derecha,  hijo del splitpane.
		System.out.println(mainSplitPane.getItems().toString());
		System.out.println(mainSplitPane.getItems().get(1).getId());

		
		rect = drawRectangle(Color.ALICEBLUE);
		rottr = getRotateTransition(rect, 500);
		sctr = getScaleTranstion(300, rect);
		fltr = getFillTransition(0, rect, Color.AQUA, Color.BISQUE);
		str = new SequentialTransition();
		str.getChildren().addAll(rottr, sctr, fltr);
		panelPrueba.getChildren().add(rect);
		
		double tamanioYpanelPrueba = rect.getBoundsInParent().getHeight();
		double tamanioXpanelPrueba = rect.getBoundsInParent().getWidth();
		panelPrueba.getChildren().get(0).setLayoutY(-tamanioYpanelPrueba);
		panelPrueba.getChildren().get(0).setLayoutX(-tamanioXpanelPrueba);

	}

	/**
	 * It is called to draw a <b>rectangle</b>
	 * 
	 * @param Color desired color for the rectangle
	 */
	public Rectangle drawRectangle(Color color) {
		Rectangle rect = new Rectangle(50, 50, 30, 30);
		rect.setArcHeight(10);
		rect.setArcWidth(10);
		rect.setFill(Color.CADETBLUE);
		return rect;
	}

	private void setRectangleColor(Rectangle rect, Color color) {
		rect.setFill(color);
	}

	/**
	 * It is called to rotate the rectangle
	 * 
	 * @param rect
	 *            The rectangle to be rotated
	 */
	public RotateTransition getRotateTransition(Rectangle rect, int duracion) {
		RotateTransition rottr = new RotateTransition(Duration.millis(duracion), rect);
		rottr.setByAngle(180);
		rottr.setCycleCount(3);
		rottr.setAutoReverse(true);
		return rottr;

	}

	/**
	 * @param duracion
	 *            determines the time it takes to rotate
	 * @param rect
	 *            determines which rectangle is going to be rotated
	 */
	private ScaleTransition getScaleTranstion(int duracion, Rectangle rect) {
		sctr = new ScaleTransition(Duration.millis(duracion), rect);
		sctr.setByX(1);
		sctr.setByY(1);
		sctr.setCycleCount(2);
		sctr.setAutoReverse(true);
		return sctr;
	}

	/**
	 * @param duracion
	 *            determines the time it takes to rotate
	 * @param rect
	 *            determines which rectangle is going to be rotated
	 * @param color1
	 *            color which the rectangle turns from
	 * @param color2
	 *            color which the rectangle turns to
	 */
	private FillTransition getFillTransition(int duracion, Rectangle rect, Color color1, Color color2) {
		fltr = new FillTransition(Duration.millis(duracion), rect, color1, color2);
		fltr.setCycleCount(1);
		fltr.setAutoReverse(false);
		return fltr;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		personTable.setItems(mainApp.getPersonData());

	}

	/**
	 * Fills all text fields to show details about the person. If the specified
	 * person is null, all text fields are cleared.
	 * 
	 * @param person
	 *            the person or null
	 */
	private void showPersonDetails(Person person) {
		if (person != null) {
			// Fill the labels with info from the person object.
			firstNameLabel.setText(person.getFirstName());
			lastNameLabel.setText(person.getLastName());
			streetLabel.setText(person.getStreet());
			postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
			cityLabel.setText(person.getCity());
			birthdayLabel.setText(DateUtil.format(person.getBirthday()));
		} else {
			// Person is null, remove all the text.
			firstNameLabel.setText("");
			lastNameLabel.setText("");
			streetLabel.setText("");
			postalCodeLabel.setText("");
			cityLabel.setText("");
			birthdayLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeletePerson() {
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		rect.setFill(Color.RED);
		if (selectedIndex >= 0) {
			personTable.getItems().remove(selectedIndex);
		} else {
			Dialogs di = Dialogs.create();
			di.title("No selection").masthead("No person selected").message("Please select a person in the table")
					.showWarning();
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new person.
	 */
	@FXML
	private void handleNewPerson() {
		Person tempPerson = new Person();
		boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
		if (okClicked) {
			mainApp.getPersonData().add(tempPerson);
			fltr = getFillTransition(500, rect, Color.AQUA, Color.GREEN);
			animarRectangulo(rottr, str, sctr, fltr);
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 */
	@FXML
	private void handleDoubleClick() {

		personTable.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					handleEditPerson();
				}
			}
		});
	}

	private void animarRectangulo(RotateTransition rottr, SequentialTransition st, ScaleTransition sctr,
			FillTransition fltr) {
		st.getChildren().removeAll(st.getChildren());
		st.getChildren().addAll(rottr, sctr, fltr);
		st.play();
	}

	@FXML
	private void handleEditPerson() {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {

			boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
			if (okClicked) {
				showPersonDetails(selectedPerson);
				fltr = getFillTransition(500, rect, Color.AQUA, Color.GREEN);
				animarRectangulo(rottr, str, sctr, fltr);

			} else {
				fltr = getFillTransition(500, rect, Color.AQUA, Color.AQUA);
				animarRectangulo(rottr, str, sctr, fltr);

			}

		} else {
			// Nothing selected.
			Dialogs.create().title("No Selection").masthead("No Person Selected")
					.message("Please select a person in the table.").showWarning();
		}
	}

}