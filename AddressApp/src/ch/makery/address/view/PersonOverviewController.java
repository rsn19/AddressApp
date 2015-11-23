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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;

import org.controlsfx.dialog.Dialogs;

import ch.makery.address.control.MainApp;
import ch.makery.address.model.Person;
import ch.makery.address.util.DateUtil;

public class PersonOverviewController {
	@FXML
	private TableView<Person> personTable;
	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> lastNameColumn;

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

	// Object to be created
	public SequentialTransitionEx stex;

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

		System.out.println(mainSplitPane.getItems().toString());
		System.out.println(mainSplitPane.getItems().get(1).getId());
		Pane panelPrueba = (Pane) mainSplitPane.getItems().get(1).lookup("#panelPrueba");

		// Instance and generate the rectangle
		Rectangle rect = drawRectangle(Color.ALICEBLUE);
		RotateTransition rottr = setRotateTransition(rect);
		// Method to resize in an animation the rectangle
		ScaleTransition sctr = new ScaleTransition(Duration.millis(3000), rect);
		sctr.setByX(1);
		sctr.setByY(1);
		sctr.setCycleCount(1);
		sctr.setAutoReverse(true);
		// Method to recolor in an animation the rectangle
		FillTransition fltr = new FillTransition(Duration.millis(2000), rect, Color.CADETBLUE, Color.DIMGREY);
		fltr.setCycleCount(2);
		fltr.setAutoReverse(true);

		SequentialTransition str = new SequentialTransition();
		
		str.getChildren().addAll(rottr, sctr, fltr);
		panelPrueba.getChildren().add(rect);
		
		double tamanioYpanelPrueba = rect.getBoundsInParent().getHeight();
		double tamanioXpanelPrueba = rect.getBoundsInParent().getWidth();
		panelPrueba.getChildren().get(0).setLayoutY(-tamanioYpanelPrueba);
		panelPrueba.getChildren().get(0).setLayoutX(-tamanioXpanelPrueba);

		panelPrueba.getOnMouseClicked();
		str.play();

	}
	
	

	/**
	 * It is called to draw a rectangle
	 * 
	 * @param Color
	 *            desired color for the rectangle
	 */
	public Rectangle drawRectangle(Color color) {
		Rectangle rect = new Rectangle(50, 50, 30, 30);
		rect.setArcHeight(10);
		rect.setArcWidth(10);
		rect.setFill(Color.CADETBLUE);
		return rect;
	}
	/**
	 * It is called to rotate the rectangle
	 * 
	 * @param rect
	 *            The rectangle to be rotated
	 */
	public RotateTransition setRotateTransition(Rectangle rect) {
		RotateTransition rottr = new RotateTransition(Duration.millis(2000), rect);
		rottr.setByAngle(180);
		rottr.setCycleCount(3);
		rottr.setAutoReverse(true);
		return rottr;

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

		if (selectedIndex >= 0) {
			personTable.getItems().remove(selectedIndex);
		} else {
			Dialogs di = Dialogs.create();
			di.title("No selection").masthead("No person selected").message("Please select a person in the table")
					.showWarning();
			/*
			 * Dialogs.create() .title("No selection") .masthead(
			 * "No person selected") .message(
			 * "Please select a person in the table") .showWarning();
			 */
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
		}
	}

	@FXML
	private void handlePanelPrueba() {

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

	@FXML
	private void handleEditPerson() {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {
			boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
			if (okClicked) {
				showPersonDetails(selectedPerson);
			}

		} else {
			// Nothing selected.
			Dialogs.create().title("No Selection").masthead("No Person Selected")
					.message("Please select a person in the table.").showWarning();
		}
	}

}