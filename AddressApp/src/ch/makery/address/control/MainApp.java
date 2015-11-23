package ch.makery.address.control;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.controlsfx.dialog.Dialogs;

import ch.makery.address.model.Person;
import ch.makery.address.model.PersonListWrapper;
import ch.makery.address.view.BirthdayStatisticsController;
import ch.makery.address.view.PersonEditDialogController;
import ch.makery.address.view.PersonOverviewController;
import ch.makery.address.view.RootLayoutController;
import ch.makery.address.view.SequentialTransitionEx;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	// ... AFTER THE OTHER VARIABLES ...

	/**
	 * The data as an observable list of Persons.
	 */
	private ObservableList<Person> personData = FXCollections.observableArrayList();

	/**
	 * Constructor
	 */
	public MainApp() {
		// Add some sample data
		personData.add(new Person("Hans", "Muster"));
		personData.add(new Person("Ruth", "Mueller"));
		personData.add(new Person("Heinz", "Kurz"));
		personData.add(new Person("Cornelia", "Meier"));
		personData.add(new Person("Werner", "Meyer"));
		personData.add(new Person("Lydia", "Kunz"));
		personData.add(new Person("Anna", "Best"));
		personData.add(new Person("Stefan", "Meier"));
		personData.add(new Person("Martin", "Mueller"));

	}

	/**
	 * Returns the data as an observable list of Persons.
	 * 
	 * @return
	 */
	public ObservableList<Person> getPersonData() {
		return personData;
	}

	// ... THE REST OF THE CLASS ...

	@Override
	public void start(Stage primaryStage) throws InvocationTargetException {
		this.primaryStage = primaryStage;

		this.primaryStage.setTitle("AddressApp");
		this.primaryStage.getIcons().add(new Image("file:resources/images/AddressApp.png"));

		initRootLayout();
		primaryStage.setMaximized(true);
		showPersonOverview();

	}

	/*
	 * Initializes the root layout.
	 */
	public void initRootLayout() throws InvocationTargetException {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/makery/address/view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			/*
			 * Rectangle time Group root = new Group(); Rectangle colors = new
			 * Rectangle(scene.getWidth(), scene.getHeight(), new
			 * LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
			 * Stop[]{ new Stop(0, Color.web("#f8bd55")), new Stop(0.14,
			 * Color.web("#c0fe56")), new Stop(0.28, Color.web("#5dfbc1")), new
			 * Stop(0.43, Color.web("#64c2f8")), new Stop(0.57,
			 * Color.web("#be4af7")), new Stop(0.71, Color.web("#ed5fc2")), new
			 * Stop(0.85, Color.web("#ef504c")), new Stop(1,
			 * Color.web("#f2660f")),}));
			 * colors.widthProperty().bind(scene.widthProperty());
			 * colors.heightProperty().bind(scene.heightProperty());
			 * root.getChildren().add(colors);
			 * rootLayout.getChildren().add(colors);
			 * System.out.println(rootLayout.getChildren().toString());
			 */
			primaryStage.setScene(scene);

			primaryStage.show();

			// Give the controller access to the main app.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Try to load last opened person file.
		File file = getPersonFilePath();
		if (file != null)
			loadPersonDataFromFile(file);
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showPersonOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/makery/address/view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();
			System.out.println(personOverview.getChildren().toString());

			// Set person overview into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Opens a dialog to edit details for the specified person. If the user
	 * clicks OK, the changes are saved into the provided person object and true
	 * is returned.
	 * 
	 * @param person
	 *            the person object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPersonEditDialog(Person person) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/makery/address/view/PersonEditDialog.fxml"));
			BorderPane page = (BorderPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			dialogStage.setResizable(false);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the person file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getPersonFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);

		} else {
			return null;
		}
	}

	public void setPersonFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			primaryStage.setTitle("AddressApp - " + file.getName());
			;
		} else {
			prefs.remove("filePath");

			// Update the stage title.
			primaryStage.setTitle("AddressApp");
		}
	}

	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 * 
	 * @param file
	 */
	public void loadPersonDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

			personData.clear();
			personData.addAll(wrapper.getPersons());

		} catch (Exception e) {
			Dialogs.create().title("Error").masthead("Couldn't load data from :\n" + file.getPath()).showException(e);
		}
	}

	/**
	 * Saves the current person data to the specified file.
	 * 
	 * @param file
	 */

	public void savePersonDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			PersonListWrapper wrapper = new PersonListWrapper();
			wrapper.setPersons(personData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

			setPersonFilePath(file);

		} catch (Exception e) {
			Dialogs.create().title("Error").masthead("Couldn't load data from :\n" + file.getPath()).showException(e);
		}
	}

	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showBirthdayStatistics() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/makery/address/view/BirthdayStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Birthday Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the persons into the controller.
			BirthdayStatisticsController controller = loader.getController();
			controller.setPersonData(personData);

			dialogStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initUI(Stage stage) {

		Pane root = new Pane();

		Rectangle rect = new Rectangle(50, 50, 30, 30);
		rect.setArcHeight(10);
		rect.setArcWidth(10);
		rect.setFill(Color.CADETBLUE);

		RotateTransition rottr = new RotateTransition(Duration.millis(2000), rect);
		rottr.setByAngle(180);
		rottr.setCycleCount(2);
		rottr.setAutoReverse(true);

		ScaleTransition sctr = new ScaleTransition(Duration.millis(2000), rect);
		sctr.setByX(2);
		sctr.setByY(2);
		sctr.setCycleCount(2);
		sctr.setAutoReverse(true);

		FillTransition fltr = new FillTransition(Duration.millis(2000), rect, Color.CADETBLUE, Color.STEELBLUE);
		fltr.setCycleCount(2);
		fltr.setAutoReverse(true);

		root.getChildren().add(rect);

		SequentialTransition str = new SequentialTransition();
		str.getChildren().addAll(rottr, sctr, fltr);

		str.play();

		Scene scene = new Scene(root, 300, 250);

		stage.setTitle("SequentialTransition");
		stage.setScene(scene);
		stage.show();
	}

}
