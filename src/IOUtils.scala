import Configs._
import GroupUsers._
import Pure._
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, Shape3D}
import javafx.scene.{Group, Node}

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.io.Source
import scala.io.StdIn.{readInt, readLine}

object IOUtils {

  def readFromFile(file: String,universe: Box,group:Group,depth:Int=Integer.MAX_VALUE):Octree[Placement] = {
    val bufferedSource = Source.fromFile(file)
    for (line <- bufferedSource.getLines) {
      val linha = line.split(" ")
      if (linha(0) == "Cylinder" || linha(0) == "Box") {
        val shape = if (linha(0) == "Cylinder") new Cylinder(0.5, 1, 10) else new Box(1, 1, 1)
        val translacoes = (linha(2), linha(3), linha(4))
        shape.setTranslateX(translacoes._1.toDouble)
        shape.setTranslateY(translacoes._2.toDouble)
        shape.setTranslateZ(translacoes._3.toDouble)
        val scale = (linha(5), linha(6), linha(7))
        shape.setScaleX(scale._1.toDouble)
        shape.setScaleY(scale._2.toDouble)
        shape.setScaleZ(scale._3.toDouble)
        val color = linha(1).substring(1, linha(1).length - 1).split(",")
        val color2 = new PhongMaterial()
        color2.setDiffuseColor(Color.rgb(color(0).toInt, color(1).toInt, color(2).toInt))
        shape.setMaterial(color2)
        if (universe.getBoundsInParent.contains(shape.getBoundsInParent) && !checkIntersects(shape, getObjects(false,group))) {
          group.getChildren.add(shape)
        }
      }
      else println("Objeto desconhecido: " + linha(0))
    }
    createOctree(group,maxLevel = Some(depth))
  }

  def writeToFile(file: String,oct:Octree[Placement],group:Group) = {
    val pw = new PrintWriter(new File(file))
    val placement = octreeToList(oct).head
    val depth = calculateDepth(placement,getObjects(false,group)).max
    pw.write(depth + "\n" + placement + "\n")
    @tailrec
    def objectPrinter(objs:List[Node]):Any = {
      objs match{
        case Nil => Nil
        case x::xs => val shape = if(x.isInstanceOf[Box]) "Box" else "Cylinder"
        pw.write(shape + " (" + (x.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getRed*255).toInt + "," + (x.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getGreen*255).toInt + "," + (x.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getBlue*255).toInt + ") " + x.asInstanceOf[Shape3D].getTranslateX.toInt + " " + x.asInstanceOf[Shape3D].getTranslateY.toInt + " " + x.asInstanceOf[Shape3D].getTranslateZ.toInt + " " + x.asInstanceOf[Shape3D].getScaleX.toInt + " " + x.asInstanceOf[Shape3D].getScaleY.toInt + " " + x.asInstanceOf[Shape3D].getScaleZ.toInt + "\n")
        objectPrinter(xs)
      }
    }
    objectPrinter(getObjects(false,group))
    pw.close()
  }

  def makeOctreeFromFile(file: String,group:Group):Octree[Placement] = {
    val bufferedSource = Source.fromFile(file).getLines().toList
    val depth = bufferedSource.head
    val placement = bufferedSource.tail.head
    @tailrec
    def rec(x:List[String]):List[Node] = {
      x match {
        case Nil => Nil
        case elem :: rest => val linha = elem.split(" ")
          val shape = if (linha(0) == "Cylinder") new Cylinder(0.5, 1, 10) else new Box(1, 1, 1)
          val translacoes = (linha(2), linha(3), linha(4))
          shape.setTranslateX(translacoes._1.toDouble)
          shape.setTranslateY(translacoes._2.toDouble)
          shape.setTranslateZ(translacoes._3.toDouble)
          val scale = (linha(5), linha(6), linha(7))
          shape.setScaleX(scale._1.toDouble)
          shape.setScaleY(scale._2.toDouble)
          shape.setScaleZ(scale._3.toDouble)
          val color = linha(1).substring(1, linha(1).length - 1).split(",")
          val color2 = new PhongMaterial()
          color2.setDiffuseColor(Color.rgb(color(0).toInt, color(1).toInt, color(2).toInt))
          shape.setMaterial(color2)
          group.getChildren.add(shape)
          rec(rest)
      }
    }
    rec(bufferedSource.tail.tail)
    val point:Point = (placement.split(",")(0).substring(2).toDouble,placement.split(",")(1).toDouble,placement.split(",")(2).substring(0,placement.split(",")(2).length-1).toDouble)
    val plac:Placement = (point,placement.split(')')(1).substring(1).toDouble)
    createOctree(group,plac,Some(depth.toInt))
  }

  @tailrec
  def fileLoader(group:Group):Octree[Placement] = {
    println("Escolha uma opção")
    println("1-Fazer load do ficheiro 'output.txt'" + "\n" +
      "2-Usar os objetos do 'configs.txt'")
    val fileOption = readLine
    fileOption.toInt match{
      case 1 => makeOctreeFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/output.txt",group)
      case 2 => println("Qual o limite de profundidade da árvore?(Se não quiser especificar clique Enter)")
        group.getChildren.add(wiredBox)
        try {
          val depthOption = readInt
          if(depthOption < 0) throw new NumberFormatException
          else readFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/configs.txt",wiredBox,group,depthOption)
        }
        catch {
          case _:java.lang.NumberFormatException | _:java.io.EOFException => println("Opção inválida, a avançar com a dimensão da menor partição espacial")
            readFromFile(s"${System.getProperty("user.home")}/IdeaProjects/ProjetoPPM/configs.txt", wiredBox, group)
        }
      case _ => fileLoader(group)
    }
  }
}
