package atlas.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import atlas.controller.Controller;
import atlas.model.Body;
import atlas.utils.Pair;
import atlas.utils.Units;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ViewImpl implements View {

	private static View view;
	// Visualization fields
	private double scale = 1.4000000000000000E-9;
	private Pair<Double, Double> translate = new Pair<>(new Double(0), new Double(0));
	private SceneMain mainScene;
	private SceneLoading loadingScene;

	// inputs fields
	private Optional<Body> selectedBody = Optional.empty();
	private boolean lockedCamera = false;
	private Optional<MouseEvent> lastMouseEvent;
	private List<Body> bodyList;
	private Optional<Pair<Double, Double>> mousePos = Optional.empty();

	private Controller ctrl;
	private Stage stage;

	public ViewImpl(Controller c, Stage primaryStage) {
		view = this;
		this.ctrl = c;
		this.stage = primaryStage;
		this.mainScene = new SceneMain();
		this.loadingScene = new SceneLoading();
		this.stage.setScene(loadingScene);
		this.stage.getIcons().add(SceneLoading.LOGO.getImage());
		primaryStage.setOnCloseRequest(e -> {
			onClose();
			e.consume();
		});
		primaryStage.show();
	}

	public static View getView() {
		if (view == null) {
			throw new IllegalStateException("View not initialized! ERROR");
		}
		return view;
	}
	
  public void onClose() {
		Alert alert = new Alert(Alert.AlertType.WARNING, "Do you really want to exit?", ButtonType.YES,
				ButtonType.NO);
		alert.initOwner(this.stage);
		alert.setTitle("Exit ATLAS");
		alert.setHeaderText(null);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.YES) {
			Platform.exit();
			System.exit(0);
		}
	}

	@Override
	public void render(List<Body> b, String time, int fps) {
		this.bodyList = b;
		if (mainScene != null) {
			Platform.runLater(() -> {

				synchronized (b) {
					mainScene.draw(b, scale, translate, time, fps);
				}
				if (!isMainScene()) {
					this.switchToMainScene();
				}
			});
		}
	}

	@Override
	public void notifyObservers(SimEvent event) {
		this.ctrl.update(event);
	}

	@Override
	public Controller getController() {
		if (this.ctrl == null) {
			throw new IllegalStateException();
		}
		return this.ctrl;
	}

	@Override
	public Optional<Body> getSelectedBody() {
		return this.selectedBody;
	}

	@Override
	public void setSelectedBody(Body body) { // ToChangeName
		System.out.println("Step 2 nextbody toadd");
		this.selectedBody = Optional.ofNullable(body);

	}

	@Override
	public boolean isCameraLocked() {
		return this.lockedCamera;
	}

	@Override
	public void setCameraLocked(boolean b) {
		this.lockedCamera = b;
	}

	@Override
	public Optional<MouseEvent> getLastMouseEvent() {
		return this.lastMouseEvent;
	}

	@Override
	public Optional<Pair<Integer, Units>> getSpeedInfo() {
		CruiseControl c = mainScene.cruise;
		Pair<Integer, Units> p = null;
		Pair<String, Units> tmp = this.mainScene.cruise.getSpeed();
		try {
			if(!Arrays.asList(Units.values()).contains(tmp.getY())) {
				throw new IllegalArgumentException();
			}
			p = new Pair<>(Integer.parseInt(tmp.getX()), tmp.getY());
		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Input error, please check.", ButtonType.OK);
			alert.setTitle("Invalid input");
			alert.setHeaderText(null);
			alert.showAndWait();
		}
		return Optional.ofNullable(p);
	}

	@Override
	public Optional<Body> getBodyUpdateInfo() {
		return this.mainScene.infoMenu.extractInfo();
	}

	@Override
	public Optional<String> getSaveName() {
		return InputDialog.getSaveName(this.stage, "Save" + new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss").format(new Date()));
	}

	@Override
	public Optional<File> getLoadFile(String title, String action, Map<File, List<File>> files) {
		return InputDialog.loadFile(this.stage, title, action, files);
	}

	@Override
	public void resetViewLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void updateReferce(Pair<Double, Double> translate, double scale) {
		this.scale = scale;
		this.translate = translate;
	}

	@Override
	public Pair<Double, Double> getReference() {
		return this.translate;
	}

	@Override
	public boolean isMainScene() {
		return this.stage.getScene().equals(mainScene);
	}

	@Override
	public void switchToMainScene() {
		this.stage.setScene(mainScene);
	}

	@Override
	public void switchToLoadingScene() {
		this.stage.setScene(loadingScene);
	}

	@Override
	public List<Body> getBodies() {
		return this.bodyList;
	}

	@Override
	public void deleteNextBody() {
		this.selectedBody = Optional.empty();

	}

	@Override
	public Pair<Double, Double> getWindow() {
		return new Pair<>(this.mainScene.renderPanel.getWidth() / 2, this.mainScene.renderPanel.getHeight() / 2);
	}
	
	

	@Override
	public void setFullScreen(boolean full) {
		this.stage.setFullScreen(full);		
	}

	@Override
	public void setMousePos(Pair<Double, Double> coordinates) {
		this.mousePos = Optional.of(coordinates);
	}

	@Override
	public Pair<Double, Double> getMousePos() {
		return this.mousePos.get();
	}
}
