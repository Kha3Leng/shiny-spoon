package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class ShowListController implements Initializable {
	@FXML
	private Label info;
	@FXML
	private Label songCount;
	@FXML
	private Label shit;
	
	@FXML
	private Button save;
	@FXML
	private ListView<String> listview = new ListView<>();
	MainController hi = new MainController();
	private final Image musicIcon = new Image("/icons/shitpost.png");

	ObservableList<String> itemset = FXCollections.observableArrayList();

	private void init() {
		int len = MainController.songList.size();

		if (len < 2)
			songCount.setText("Only " + len + " song is available.");
		else {
			info.setText(MainController.title + " by " + MainController.artist);
			songCount.setText("Total of " + len + " songs are available.");
		}
	}

	private void savePlaylist(File path) throws IOException {
		System.out.println("Shit");
		shit.setText("");
		// FileWriter fileWriter = new FileWriter("C:\\users\\user\\desktop\\" +
		// savedFile.getText());

		PrintWriter printWriter = new PrintWriter(path);
		printWriter.println("\r\n" + "  _   _   _   _   _   _   _  \r\n" + " / \\ / \\ / \\ / \\ / \\ / \\ / \\ \r\n"
				+ "( w | e | l | c | o | m | e )\r\n" + " \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \r\n" + "\r\n" + "\r\n"
				+ "");
		Iterator<String> tr = MainController.songList.iterator();
		System.out.println("Please work : " + MainController.songList);
		int j = 0;
		while (tr.hasNext()) {
			System.out.println("yeah?");
			printWriter.println(++j + " : " + tr.next());
		}
		System.out.println("Whatt happneend here?");
		printWriter.close();
		printWriter.flush();
		shit.setTextFill(Color.WHITE);
		shit.setText("Successfully saved...");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		Iterator<String> itr = MainController.songList.iterator();
		while (itr.hasNext())
			itemset.add(itr.next());

		listview.setItems(itemset);

		listview.setCellFactory(param -> new ListCell<String>() {
			private ImageView view = new ImageView();

			public void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					view.setImage(musicIcon);
					setText(name);
					setGraphic(view);
				}
			}
		});
		init();

		// save = new Button();
		save.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);
			File selectedDirectory = fileChooser.showSaveDialog(NewStage.subStage);
			try {
				if (selectedDirectory != null)
					savePlaylist(selectedDirectory);
			} catch (IOException e1) { // TODO Auto-generated catch block e1.printStackTrace(); }

				// songCount.setText(selectedDirectory.toString());
			}
		});

		System.out.println("Hey " + new MainController().minSupp);
		System.out.println("Hey " + MainController.songList);
		// MainController.songList.clear();
	}

}
