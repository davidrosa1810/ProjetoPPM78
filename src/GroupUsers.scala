import Configs._
import IOUtils.writeToFile
import Pure._
import javafx.scene.shape.{Box, Cylinder, DrawMode}
import javafx.scene.{Group, Node}

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.jdk.CollectionConverters._


object GroupUsers {

  def getCamera(group:Group):Node = {
    group.getChildren.asScala.toList.filter(x => x.isInstanceOf[Cylinder] && x.asInstanceOf[Cylinder].getDrawMode==DrawMode.LINE).head
  }

  def getObjects(parts:Boolean, group:Group):List[Node] = {
    val boxes = if(parts){
      group.getChildren.asScala.toList.filter(x => x.isInstanceOf[Box] && (x.asInstanceOf[Box].getDrawMode==DrawMode.FILL || (x.asInstanceOf[Box].getMaterial!=redMaterial && x.asInstanceOf[Box].getDrawMode==DrawMode.LINE)))
    } else group.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Box] && x.asInstanceOf[Box].getDrawMode!=DrawMode.LINE)
    val cylinders = group.getChildren.asScala.toList.filter(x=> x.isInstanceOf[Cylinder] && x.asInstanceOf[Cylinder].getDrawMode!=DrawMode.LINE)
    val objects = (boxes foldRight cylinders) (_ :: _)
    objects
  }

  def maximumDepth(x:Placement,group:Group):Int = {
      if(calculateDepth(x,getContainedObjects(makeBox(x),getObjects(false,group),Nil)).isEmpty) -1
      else calculateDepth(x,getContainedObjects(makeBox(x),getObjects(false,group),Nil)).max
  }

  def minimumDepth(x:Placement,group:Group):Int = {
    if(calculateDepth(x,getContainedObjects(makeBox(x),getObjects(false,group),Nil)).isEmpty) -1
    else calculateDepth(x,getContainedObjects(makeBox(x),getObjects(false,group),Nil)).min
  }

  def createOctree(group:Group,placement1:Placement=((0, 0, 0), 32),maxLevel:Option[Int]):Octree[Placement] = {
    if(maxLevel != null)
      makeNode(placement1,minimumDepth(placement1,group),maxLevel.get,group)
    else {
      makeNode(placement1,minimumDepth(placement1,group),maximumDepth(placement1,group),group)
    }
  }

  def makeNode(placement:Placement, depth:Int, maxLevel:Int, group:Group):Octree[Placement] = {
    val box = makeBox(placement)
    if(checkContains(box,getObjects(false,group))){
      if(!checkIntersects(getCamera(group),List(box))) box.setMaterial(blueMaterial)
      group.getChildren.add(box)
      if(depth==0 || maxLevel == 0){
        OcLeaf(placement,getContainedObjects(box,getObjects(false,group),Nil):List[Node])
      }
      else{
        OcNode(placement,
          makeNode((placement._1,placement._2/2),minimumDepth((placement._1,placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1+placement._2/2,placement._1._2,placement._1._3),placement._2/2),minimumDepth(((placement._1._1+placement._2/2,placement._1._2,placement._1._3),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1,placement._1._2+placement._2/2,placement._1._3),placement._2/2),minimumDepth(((placement._1._1,placement._1._2+placement._2/2,placement._1._3),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3),placement._2/2),minimumDepth(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1,placement._1._2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(((placement._1._1,placement._1._2,placement._1._3+placement._2/2),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1+placement._2/2,placement._1._2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(((placement._1._1+placement._2/2,placement._1._2,placement._1._3+placement._2/2),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(((placement._1._1,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),group),maxLevel-1,group),
          makeNode(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),minimumDepth(((placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3+placement._2/2),placement._2/2),group),maxLevel-1,group))
      }
    }
    else OcEmpty
  }

  def getPartition(placement:Placement,group:Group):Node = {
    val translations = (placement._1._1+placement._2/2,placement._1._2+placement._2/2,placement._1._3+placement._2/2)
    val list = group.getChildren.asScala.toList.filter(x=>x.isInstanceOf[Box] && x.asInstanceOf[Box].getDrawMode==DrawMode.LINE && x.asInstanceOf[Box].getMaterial!=redMaterial)
    val box = list.filter(x=>x.asInstanceOf[Box].getTranslateX==translations._1 && x.asInstanceOf[Box].getTranslateY==translations._2 && x.asInstanceOf[Box].getTranslateZ==translations._3).head
    box
  }

  def changePartitionsColor[A](tree:Octree[A],group:Group):Any = {
    val camera = getCamera(group)
    tree match {
      case OcNode(plac, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => {
        val box = getPartition(plac.asInstanceOf[Placement],group)
        if (camera.getBoundsInParent.intersects(box.asInstanceOf[Box].getBoundsInParent)) {
          box.asInstanceOf[Box].setMaterial(whiteMaterial)
        }
        else {
          box.asInstanceOf[Box].setMaterial(blueMaterial)
        }
        changePartitionsColor[A](up_00,group)
        changePartitionsColor[A](up_01,group)
        changePartitionsColor[A](up_10,group)
        changePartitionsColor[A](up_11,group)
        changePartitionsColor[A](down_00,group)
        changePartitionsColor[A](down_01,group)
        changePartitionsColor[A](down_10,group)
        changePartitionsColor[A](down_11,group)
      }
      case OcLeaf(section) => {
        val box = getPartition(section.asInstanceOf[Section]._1,group)
        if (camera.getBoundsInParent.intersects(box.asInstanceOf[Box].getBoundsInParent)) {
          box.asInstanceOf[Box].setMaterial(whiteMaterial)
        }
        else {
          box.asInstanceOf[Box].setMaterial(blueMaterial)
        }
      }
      case _ => Nil
    }
  }

  def scaleOctree(fact:Double, oct:Octree[Placement], group:Group):Octree[Placement] = {
    @tailrec
    def scaleObjects(x:List[Node]):Any = {
      x match{
        case Nil => Nil
        case y::ys => {
          val newCoords = (y.getTranslateX*fact,y.getTranslateY*fact,y.getTranslateZ*fact)
          y.setTranslateX(newCoords._1)
          y.setTranslateY(newCoords._2)
          y.setTranslateZ(newCoords._3)
          y.setScaleX(y.getScaleX*fact)
          y.setScaleY(y.getScaleY*fact)
          y.setScaleZ(y.getScaleZ*fact)
        }
          scaleObjects(ys)
      }
    }
    def scale[A](tree:Octree[Placement]):Octree[Placement] = {
      def scalePlacement(plac: => Placement):Placement = {
        ((plac._1._1*fact, plac._1._2*fact, plac._1._3*fact), plac._2*fact)
      }
      tree match {
        case OcNode(placement, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => {
          val placement2 = scalePlacement(placement)
          OcNode(placement2, scale(up_00), scale(up_01), scale(up_10), scale(up_11), scale(down_00), scale(down_01), scale(down_10), scale(down_11))
        }
        case OcLeaf(section) => {
          val sec2 = scalePlacement(section.asInstanceOf[Section]._1)
          OcLeaf((sec2,section.asInstanceOf[Section]._2))
        }
        case OcEmpty => OcEmpty
      }
    }
    scaleObjects(getObjects(true,group))
    val tree = scale(oct)
    changePartitionsColor(tree,group)
    tree
  }

  @tailrec
  def menu(tree:Octree[Placement], group:Group):Octree[Placement] = {
    println("Escolha uma opção")
    println("1-Scale2X" + "\n" +
      "2-Scale0.5X" + "\n" +
      "3-Sépia" + "\n" +
      "4-GreenRemove" + "\n" +
      "5-Abrir janela de visualização")
    val option = readLine
    option.toInt match{
      case 1 => menu(scaleOctree(2,tree,group),group)
      case 2 => menu(scaleOctree(0.5,tree,group),group)
      case 3 => menu(mapColourEffect(sepia,tree),group)
      case 4 => menu(mapColourEffect(greenRemove,tree),group)
      case 5 => writeToFile("output.txt",tree,group)
        tree
      case _ => println("Opção inválida, escolha um número de 1 a 5")
        menu(tree,group)
    }
  }
}
