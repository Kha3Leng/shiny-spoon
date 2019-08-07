package application;

import java.io.IOException; 
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

public class MainController extends Application implements Initializable {
	@FXML
	private Label intro;
	@FXML
	private CustomTextField input;
	@FXML
	private Label header;
	@FXML
	public Label suggestions;

	// private static final Image image = new Image("/icons/search_icon.png");
	private static final Image image = new Image("/icons/iconfinder.png");
	String track, songid;
	public static String title, artist;
	public int sid, minSupp = 1, supp;
	Connection connection;
	Statement preparedStatement;
	ResultSet resultSet;

	// -fx-background-color: linear-gradient(to top right, #7ed56f, #28b485);
	Random rnd = new Random();
	// ArrayList<String> suggestion = new ArrayList<String>();
	Set<String> suggestion = new LinkedHashSet<String>();
	ArrayList<String> showSuggestion = new ArrayList<String>(suggestion);

	public static Set<String> songList = new LinkedHashSet<>();

	public MainController() {
		connection = (Connection) ConnectionUtil.connectdb();
	}

	public void autoSuggestion() {
		String sql = "SELECT title as track FROM song;";
		try {
			preparedStatement = connection.createStatement();
			resultSet = preparedStatement.executeQuery(sql);
			int i = 0;
			while (resultSet.next())// && resultSet.getString("track").length() < 173) { //
			{
				// if (resultSet.getString("track").length() < 200)
				suggestion.add(resultSet.getString("track"));

				if (i == 3332)
					break;
				else
					suggestion.add(resultSet.getString("track"));
				i++;
			}
			System.out.println("size : " + suggestion.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Iterator<String> itr = s.iterator(); while(itr.hasNext()) {
		 * System.out.println("set : "+itr.next()); }
		 */
		// System.out.println("size : "+s.size());
	}

	public void acceptInput() {
		track = input.getText();

		String sql = "SELECT songid, title, artistname FROM song WHERE title like \"" + track + "\"";

		try {

			preparedStatement = connection.createStatement();
			resultSet = preparedStatement.executeQuery(sql);
			if (resultSet.next() == true) {
				songid = resultSet.getString(1);
				title = resultSet.getString("title");
				artist = resultSet.getString("artistname");

				sql = "select sid from songg where songid like \"" + songid + "\"";
				resultSet = preparedStatement.executeQuery(sql);
				if (resultSet.next()) {
					sid = resultSet.getInt("sid");
				}

				sql = "select supp from conff where songid=" + sid + ";";
				resultSet = preparedStatement.executeQuery(sql);
				if (resultSet.next()) {
					supp = resultSet.getInt(1);
				}

				if (supp >= minSupp) {
					generatePlaylist();
				} else {

					System.out.println("No");
					System.out.println("SOrry there is no other song.");
					Popup sad = new Popup();
					sad.text = "not many people listen to it :| ";
					sad.notification(Pos.CENTER, sad.graphic, sad.text);
					sad.builder.show();
				}
			} else {
				System.out.println("no track with this in my database");
				Popup sad = new Popup();
				sad.text = "TRACK UNKNOWN";
				sad.notification(Pos.CENTER, sad.graphic, sad.text);
				sad.builder.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void generatePlaylist() throws IOException {

		Set<Integer> itemset = new LinkedHashSet<>();
		String sql = "select l2 from sndpass where l1=" + sid + " and " + "supp" + ">=" + minSupp + " order by supp";
		try {
			System.out.println("YEST");
			preparedStatement = connection.createStatement();
			resultSet = preparedStatement.executeQuery(sql);
			while (resultSet.next()) {
				itemset.add(resultSet.getInt("l2"));

				/////////////////////////////////////
				////// log file//////////////////////
				////////////////////////////////////
				System.out.println("L2 : " + resultSet.getInt("l2"));
				System.out.println("itemset " + itemset);
				System.out.println("---------------------------------------------------------");
				System.out.println("\n size" + itemset.size());
				////////////////////////////////////
				////// log file//////////////////////
				////////////////////////////////////

			}

			sql = "select l1 from sndpass where l2=" + sid + " and " + "supp" + ">=" + minSupp + " order by supp/"
					+ supp;

			preparedStatement = connection.createStatement();
			resultSet = preparedStatement.executeQuery(sql);

			while (resultSet.next()) {
				itemset.add(resultSet.getInt("l1"));
				System.out.println("itemset " + itemset);
				System.out.println("--------------------------------");
			}

			Iterator<Integer> itr = itemset.iterator();
			while (itr.hasNext()) {
				sql = "Select concat(s.title,\" - \",s.artistname) as info" + " from song s, songg g where "
						+ "s.songid=g.songid and g.sid=" + itr.next();

				preparedStatement = connection.createStatement();
				resultSet = preparedStatement.executeQuery(sql);

				if (resultSet.next()) {
					this.songList.add(resultSet.getString("info"));
					System.out.println("song list : " + songList);
					System.out.println("--------------------------------");
				}
			}
		} catch (Exception e) {
			// NULL POINTER EXCEPTION HERE! BUT SIMPLY BREAK
			System.out.println(e.getMessage());
		}

		System.out.println(songList);

		if (itemset.size() != 0)
			new NewStage();
		else {
			System.out.println("SOrry there is no other song.");
			Popup sad = new Popup();
			sad.notification(Pos.CENTER, sad.graphic, sad.text);
			sad.builder.show();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
	}
	// -fx-background-color: linear-gradient(to top right, #7ed56f, #28b485);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stubautoSuggestion(suggestion);
		// autoSuggestion(suggestion);

		autoSuggestion();
		Platform.runLater(() -> Main.root.requestFocus());
		
		TextFields.bindAutoCompletion(input, suggestion).setMinWidth(500);;
		input.setRight(new ImageView(image));
		System.out.println("waht the size" + showSuggestion.size());
		Iterator<String> itr = suggestion.iterator();
		while (itr.hasNext()) {
			showSuggestion.add(itr.next());
		}
		System.out.println("should workd" + showSuggestion.size());

		updateSuggestion(showSuggestion);

		
	}

	private void updateSuggestion(ArrayList<String> al) {

		KeyFrame keyFrame = new KeyFrame(Duration.seconds(5), event -> {
			System.out.println("SHow... eg");
			int index = rnd.nextInt((3299 - 0) + 1) + 0;
			suggestions.setText("e.g., " + al.get(index));

		}

		);
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);
		// if you want to limit the number of cycles use
		// timeline.setCycleCount(100);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();

	}

}

class Popup {
	Notifications builder;
	String text = "NO ASSOCIATED SONGS....";
	private final Image shit = new Image("/icons/iconfinder_holidays_cat_1459447.png");
	Node graphic = new ImageView(shit);

	Popup() throws IOException {
		Stage subStage = new Stage();
		subStage.setTitle("Your Playlist");
	}

	public void notification(Pos pos, Node graphic, String text) {
		builder = Notifications.create().title("").text(text).graphic(graphic).hideAfter(Duration.seconds(2))
				.position(pos);
	}
}

class NewStage {
	public static Stage subStage;

	NewStage() throws IOException {
		subStage = new Stage();
		subStage.initStyle(StageStyle.UTILITY);
		Stage s = Main.getPrimaryStage();
		subStage.initOwner(s);
		subStage.setTitle("Your Playlist");
		AnchorPane root = FXMLLoader.load(getClass().getResource("/application/ShowList.fxml"));

		subStage.getIcons().add(Main.applicationIcon);
		// subStage.initModality(Modality.WINDOW_MODAL);
		// root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, 510, 560);
		scene.getStylesheets().add(getClass().getResource("/application/shit.css").toExternalForm());
		subStage.setScene(scene);
		subStage.show();
	}
}
