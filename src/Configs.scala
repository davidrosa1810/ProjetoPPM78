import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Line}
import javafx.scene.transform.Rotate
import javafx.scene.{Node, PerspectiveCamera}


object Configs {

  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node])  //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))

  val whiteMaterial = new PhongMaterial()
  whiteMaterial.setDiffuseColor(Color.rgb(255,255,255))

  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150,0,0))

  val greenMaterial = new PhongMaterial()
  greenMaterial.setDiffuseColor(Color.rgb(0,255,0))

  val blueMaterial = new PhongMaterial()
  blueMaterial.setDiffuseColor(Color.rgb(0,0,150))

  val lineX = new Line(0, 0, 200, 0)
  lineX.setStroke(Color.GREEN)

  val lineY = new Line(0, 0, 0, 200)
  lineY.setStroke(Color.YELLOW)

  val lineZ = new Line(0, 0, 200, 0)
  lineZ.setStroke(Color.LIGHTSALMON)
  lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

  val camVolume = new Cylinder(10, 50, 10)
  camVolume.setTranslateX(1)
  camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
  camVolume.setMaterial(blueMaterial)
  camVolume.setDrawMode(DrawMode.LINE)

  val wiredBox = new Box(32, 32, 32)
  wiredBox.setTranslateX(16)
  wiredBox.setTranslateY(16)
  wiredBox.setTranslateZ(16)
  wiredBox.setMaterial(redMaterial)
  wiredBox.setDrawMode(DrawMode.LINE)


  val camera = new PerspectiveCamera(true)

  val cameraTransform = new CameraTransformer
  cameraTransform.setTranslate(0, 0, 0)
  cameraTransform.getChildren.add(camera)
  camera.setNearClip(0.1)
  camera.setFarClip(10000.0)

  camera.setTranslateZ(-500)
  camera.setFieldOfView(20)
  cameraTransform.ry.setAngle(-45.0)
  cameraTransform.rx.setAngle(-45.0)
}
