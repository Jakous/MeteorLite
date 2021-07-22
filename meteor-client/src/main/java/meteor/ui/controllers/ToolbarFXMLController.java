package meteor.ui.controllers;

import static meteor.MeteorLite.injector;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javax.inject.Inject;
import meteor.MeteorLite;
import meteor.eventbus.EventBus;
import net.runelite.api.Client;

public class ToolbarFXMLController {

  @Inject
  Client client;

  @Inject
  EventBus eventBus;

  @FXML
  private JFXButton pluginsButton;
  @FXML
  private JFXButton optionsButton;
  @FXML
  private Text title;

  @FXML
  protected void handlePluginsPressed(ActionEvent event) {
    MeteorLite.togglePluginsPanel();
  }

  @FXML
  protected void handleOptionsPressed(ActionEvent event) {
  }

  @FXML
  public void initialize() {
    injector.injectMembers(this);
    eventBus.register(this);
  }
}