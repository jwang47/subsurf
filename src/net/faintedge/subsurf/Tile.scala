package net.faintedge.subsurf
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import net.faintedge.util.WorldHelper
import org.jbox2d.dynamics.BodyDef
import org.newdawn.fizzy.World
import org.newdawn.fizzy.Body
import org.newdawn.fizzy.StaticBody
import org.newdawn.fizzy.Rectangle
import org.newdawn.fizzy.DynamicBody

class Tile(var typ: Int) {

}

case class TileInfo(val typ: Int, val name: String, val renderer: TileRenderer) {

}

abstract class TileRenderer {
  def render(x: Float, y: Float, width: Int, height: Int)
}

class ImageTileRenderer(image: Image) extends TileRenderer {
  def render(x: Float, y: Float, width: Int, height: Int) {
    image.draw(x - width / 2, y - height / 2, width, height)
  }
}

class NullTileRenderer extends TileRenderer {
  def render(x: Float, y: Float, width: Int, height: Int) {}
}

class TilePage(val width: Int, val height: Int, val tileWidth: Int = 15, val tileHeight: Int = 15) {
  var tiles = Util.ofDim[Tile](width, height, () => new Tile(0))

  def totalWidth = tileWidth * width
  def totalHeight = tileHeight * height

  def setTile(row: Int, col: Int, typ: Int) = tiles(col)(row).typ = typ
  def getTileType(row: Int, col: Int): Int = tiles(col)(row).typ
}

class PhysicsTilePage(width: Int, height: Int, world: World, tileWidth: Int = 15, tileHeight: Int = 15) extends TilePage(width, height, tileWidth, tileHeight) {
  val tileBodies = Array.ofDim[Body[Tile]](width, height)
  val borderHorizontalThickness = tileHeight
  val borderVerticalThickness = tileWidth
  val borderHorizontalShape = new Rectangle(totalWidth, tileHeight)
  val borderVerticalShape = new Rectangle(tileWidth, totalHeight)

  val borderBodies = Array[Body[PhysicsTilePage]](
    new StaticBody[PhysicsTilePage](borderHorizontalShape, -totalWidth / 2 - tileWidth / 2, totalHeight / 2 - tileHeight / 2),
    new StaticBody[PhysicsTilePage](borderHorizontalShape, -totalWidth / 2 - tileWidth / 2, -totalHeight / 2 - borderHorizontalThickness - tileHeight / 2),
    new StaticBody[PhysicsTilePage](borderVerticalShape, -totalWidth / 2 - borderVerticalThickness - tileWidth / 2, -totalHeight / 2 - tileHeight / 2),
    new StaticBody[PhysicsTilePage](borderVerticalShape, totalWidth / 2 - tileWidth + tileWidth / 2, -totalHeight / 2 - tileHeight / 2))
    
  for(borderBody <- borderBodies) {
    borderBody.setUserData(this)
    borderBody.setRestitution(GameConstants.DEFAULT_TILE_RESTITUTION)
    world.add(borderBody)
  }
  //  world.add(new StaticBody(borderHorizontalShape, -totalWidth/2 - tileWidth/2, totalHeight/2 - tileHeight/2))
  //  world.add(new StaticBody(borderHorizontalShape, -totalWidth/2 - tileWidth/2, -totalHeight/2 - borderHorizontalThickness - tileHeight/2))
  //  world.add(new StaticBody(borderVerticalShape, -totalWidth/2 - borderVerticalThickness - tileWidth/2, -totalHeight/2  - tileHeight/2))
  //  world.add(new StaticBody(borderVerticalShape, totalWidth/2 - tileWidth + tileWidth/2, -totalHeight/2  - tileHeight/2))

  override def setTile(row: Int, col: Int, typ: Int) {
    if (tiles(col)(row).typ == 0 && typ != 0) {
      // create new static body for tile of type typ
      assert(tileBodies(col)(row) == null)
      val shape = new Rectangle(tileWidth, tileHeight)
      val body = new StaticBody[Tile](shape,
        col * tileWidth - totalWidth / 2 - tileWidth / 2,
        row * tileHeight - totalHeight / 2 - tileHeight / 2)
      body.setRestitution(GameConstants.DEFAULT_TILE_RESTITUTION)
      body.setUserData(tiles(col)(row))
      world.add(body)
      tileBodies(col)(row) = body
    } else if (tiles(col)(row).typ != 0 && typ == 0) {
      // destroy static body at row,col
      assert(tileBodies(col)(row) != null)
      world.remove(tileBodies(col)(row))
      tileBodies(col)(row) = null
    }
    super.setTile(row, col, typ)
  }
}

class TilePageRenderer(page: TilePage) {
  def tileWidth = page.tileWidth
  def tileHeight = page.tileHeight
  def totalWidth = page.totalWidth
  def totalHeight = page.totalHeight

  def getTilePosition(row: Int, col: Int): Tuple2[Int, Int] = {
    (col * tileWidth - totalWidth / 2, row * tileHeight - totalHeight / 2)
  }

  def render(g: Graphics) {
    for (col <- 0 until page.width) {
      for (row <- 0 until page.height) {
        val tileInfo = TileData.tileInfos(page.getTileType(row, col))
        val tilePosition = getTilePosition(row, col)
        tileInfo.renderer.render(tilePosition._1, tilePosition._2, tileWidth, tileHeight)
      }
    }
  }

  def fillTile(g: Graphics, row: Int, col: Int, color: Color) {
    val tilePosition = getTilePosition(row, col)
    g.setColor(color)
    g.fillRect(tilePosition._1 - tileWidth / 2, tilePosition._2 - tileHeight / 2, tileWidth, tileHeight)
  }
}

object TileData {
  val tileInfos = Array(
    TileInfo(0, "empty", new NullTileRenderer()),
    TileInfo(1, "mud", new ImageTileRenderer(new Image("data/30.png"))),
    TileInfo(2, "stone", new ImageTileRenderer(new Image("data/21.png"))))
}