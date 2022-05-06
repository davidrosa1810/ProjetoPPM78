import Configs._
import GroupUsers._
import IOUtils._
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.{Insets, Pos}
import javafx.scene._
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class Main extends Application {

  /*
    Additional information about JavaFX basic concepts (e.g. Stage, Scene) will be provided in week7
   */
  override def start(stage: Stage): Unit = {

    //Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    val worldRoot: Group = new Group(camVolume, lineX, lineY, lineZ)

    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.DARKSLATEGRAY)
    subScene.setCamera(camera)

    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-100)
    cameraView.getT.setY(-500)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

    StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
    StackPane.setMargin(cameraView, new Insets(5))

    if(params.getRaw.get(0).toInt==1) {

      val oct1 = menu(fileLoader(worldRoot), worldRoot)

      worldRoot.getChildren.add(cameraTransform)



      // Scene - defines what is rendered (in this case the subScene and the cameraView)
      val root = new StackPane(subScene, cameraView)
      subScene.widthProperty.bind(root.widthProperty)
      subScene.heightProperty.bind(root.heightProperty)

      val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

      //setup and start the Stage
      stage.setTitle("PPM Project 21/22")
      stage.setScene(scene)
      stage.show

      scene.setOnKeyPressed(e => {
        if (e.getCode == KeyCode.UP) {
          camVolume.setTranslateZ(camVolume.getTranslateZ + 2)
        }
        else if (e.getCode == KeyCode.DOWN) {
          camVolume.setTranslateZ(camVolume.getTranslateZ - 2)
        }
        else if (e.getCode == KeyCode.LEFT) {
          camVolume.setTranslateX(camVolume.getTranslateX - 2)
        }
        else if (e.getCode == KeyCode.RIGHT) {
          camVolume.setTranslateX(camVolume.getTranslateX + 2)
        }
        changePartitionsColor(oct1, worldRoot)
      })
    }

    else if(params.getRaw.get(0).toInt==2){
      println("inserir gui aqui")




      stage.setTitle("My Hello World App")
      val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))
      val mainViewRoot: Parent = fxmlLoader.load()


      val root = new StackPane(subScene, cameraView, mainViewRoot)
      subScene.widthProperty.bind(root.widthProperty)
      subScene.heightProperty.bind(root.heightProperty)
      StackPane.setAlignment(mainViewRoot, Pos.TOP_LEFT)

      val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)
      stage.setScene(scene)
      stage.show()


    }

  }

  override def init(): Unit = {
    println("init")
  }

  override def stop(): Unit = {
    println("stopped")
  }
}

object TextBased {

  def main(args:Array[String]): Unit = {
    Application.launch(classOf[Main], "1")
  }

}

object GUI {

  def main(args:Array[String]): Unit = {
    Application.launch(classOf[Main], "2")
  }
}
