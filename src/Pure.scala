import Configs._
import javafx.scene.Node
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, DrawMode, Shape3D}

import scala.annotation.tailrec

object Pure{

  @tailrec
  def checkIntersects(obj:Node, objs:List[Node]):Boolean = {
    objs match{
      case Nil => false
      case y::ys =>{
        if(obj.getBoundsInParent.intersects(y.asInstanceOf[Shape3D].getBoundsInParent)) true
        else checkIntersects(obj,ys)
      }
    }
  }

  def makeBox(plac:Placement):Box = {
    val box = new Box(plac._2, plac._2, plac._2)
    box.setTranslateX(plac._2 / 2 + plac._1._1)
    box.setTranslateY(plac._2 / 2 + plac._1._2)
    box.setTranslateZ(plac._2 / 2 + plac._1._3)
    box.setDrawMode(DrawMode.LINE)
    box.setMaterial(whiteMaterial)
    box
  }

  def caixinhasMagicas(outerPlacement:Placement):List[Box] = {
    val dim = outerPlacement._2/2
    val part1 = makeBox(outerPlacement._1,dim)
    val part2 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3),dim)
    val part3 = makeBox((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3),dim)
    val part4 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3),dim)
    val part5 = makeBox((outerPlacement._1._1,outerPlacement._1._2,outerPlacement._1._3+dim),dim)
    val part6 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2,outerPlacement._1._3+dim),dim)
    val part7 = makeBox((outerPlacement._1._1,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim)
    val part8 = makeBox((outerPlacement._1._1+dim,outerPlacement._1._2+dim,outerPlacement._1._3+dim),dim)
    part1::part2::part3::part4::part5::part6::part7::part8::Nil
  }

  @tailrec
  def depth(node:Node, i:Int, caixas:List[Box]):Int = {
    caixas match{
      case Nil => 0
      case x::xs => {
        if(x.getBoundsInParent.contains(node.getBoundsInParent)) {
          val placement = ((x.getTranslateX-x.getWidth/2,x.getTranslateY-x.getWidth/2,x.getTranslateZ-x.getWidth/2),x.getWidth)
          depth(node,i+1,caixinhasMagicas(placement))
        }
        else if(x.getBoundsInParent.intersects(node.getBoundsInParent)){
          i
        }
        else depth(node,i,xs)
      }
    }
  }

  def calculateDepth(placement:Placement,objs:List[Node]):List[Int] = {
    objs match{
      case Nil => Nil
      case x::xs => depth(x,0,caixinhasMagicas(placement))::calculateDepth(placement,xs)
    }
  }

  @tailrec
  def checkContains(obj:Node, objs:List[Node]):Boolean = {
    objs match{
      case Nil => false
      case y::ys => {
        if(obj.getBoundsInParent.contains(y.asInstanceOf[Shape3D].getBoundsInParent)) {
          true
        }
        else checkContains(obj,ys)
      }
    }
  }

  @tailrec
  def getContainedObjects(obj:Node, objs:List[Node], objs2:List[Node]):List[Node] = {
    objs match{
      case Nil => objs2
      case y::ys => {
        if(obj.getBoundsInParent.contains(y.asInstanceOf[Shape3D].getBoundsInParent)) getContainedObjects(obj,ys,y::objs2)
        else getContainedObjects(obj,ys,objs2)
      }
    }
  }

  def mapColourEffect(corezinhas: Color => Color, oct:Octree[Placement]):Octree[Placement]= {
    oct match{
      case OcNode(placement, up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11)=>{
        OcNode(placement, mapColourEffect(corezinhas, up_00),
          mapColourEffect(corezinhas, up_01),
          mapColourEffect(corezinhas, up_10),
          mapColourEffect(corezinhas, up_11),
          mapColourEffect(corezinhas, down_00),
          mapColourEffect(corezinhas, down_01),
          mapColourEffect(corezinhas, down_10),
          mapColourEffect(corezinhas, down_11))
      }


      case OcLeaf(section) => {
        section.asInstanceOf[Section]._2.map(a => {
          val phongMaterial = new PhongMaterial()
          phongMaterial.setDiffuseColor(corezinhas(a.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor))
          a.asInstanceOf[Shape3D].setMaterial(phongMaterial)
        })
        OcLeaf(section)
      }
      case OcEmpty => OcEmpty

    }
  }

  def sepia(color: Color):Color={
    val r = if((color.getRed*255*0.4 + color.getGreen*255*0.77 +  color.getBlue*255*0.2)<255)
      color.getRed*255*0.4 + color.getGreen*255*0.77 +  color.getBlue*255*0.2 else 255
    val g = if((color.getRed*255*0.35 + color.getGreen*255*0.69 +  color.getBlue*255*0.17)<255)
      color.getRed*255*0.35 + color.getGreen*255*0.69 +  color.getBlue*255*0.17 else 255
    val b = if((color.getRed*255*0.27 + color.getGreen*255*0.53 +  color.getBlue*255*0.13)<255)
      color.getRed*255*0.27 + color.getGreen*255*0.53 +  color.getBlue*255*0.13 else 255
    Color.rgb(r.toInt,g.toInt,b.toInt)
  }

  def greenRemove(color: Color): Color ={
    Color.rgb((color.getRed*255).toInt,0, (color.getBlue*255).toInt)
  }

  def octreeToList(oct:Octree[Placement]):List[Placement] = {
    oct match{
      case OcEmpty => Nil
      case OcLeaf(section) => section.asInstanceOf[Section]._1 :: Nil
      case OcNode(placement,up_00, up_01, up_10, up_11, down_00, down_01, down_10, down_11) => placement::octreeToList(up_00):::octreeToList(up_01):::octreeToList(up_10):::octreeToList(up_11):::octreeToList(down_00):::octreeToList(down_01):::octreeToList(down_10):::octreeToList(down_11)
    }
  }

}

