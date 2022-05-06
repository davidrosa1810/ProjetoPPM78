import javafx.fxml.FXML
import javafx.scene.control.{Button, ToggleButton, Tooltip}
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.text._


class Controller{

  @FXML
  private var button1: Button =_
  @FXML
  private  var buttonscale1: Button =_
  @FXML
  private  var buttonscale2: Button =_
  @FXML
  private var tooltip1: Tooltip =_
  @FXML
  private var image1: ImageView =_
  @FXML
  private var text1: Text =_
  @FXML
  private var text2: Text =_
  @FXML
  private var text3: Text =_
  @FXML
  private var toggleButton1: ToggleButton =_

  @FXML
  private var borderPane1: BorderPane =_



  def onButton1Clicked(): Unit={
    println("Hello World")
  }

  def onButtonScale1Clicked(): Unit = {
    //GroupUsers.scaleOctree(0.5,oct, worldRoot1)

  }

  def onButtonScale2Clicked(): Unit = {
    //GroupUsers.scaleOctree(2,oct1, worldRoot)
  }

  def changeFontHeretica(): Unit = {
    text1.setFont(Font.font("Heretica", FontWeight.BOLD, FontPosture.REGULAR, 20))
    text2.setFont(Font.font("Heretica", FontWeight.BOLD, FontPosture.REGULAR, 30))
    text3.setFont(Font.font("Heretica", FontWeight.BOLD, FontPosture.REGULAR, 10))

  }

  def clickToggle(): Unit = {
    if(toggleButton1.isSelected)
      println("ola")
    else
      println("adeus")
  }

  def startVisualize(): Unit = {
    borderPane1.setVisible(false)
  }
/*

stylesheets="@style.css"

  def lightmode(): Unit = {
    subscene.setFill(Color.WHITE)
  }

  def darkmode(): Unit = {
    subscene.setFill(Color.DARKSLATEGRAY)
  }
  */
}
