package net.faintedge.subsurf
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import net.faintedge.fiber.shape.Rectangle
import net.faintedge.fiber.Control
import net.faintedge.fiber.Renderable
import org.jbox2d.dynamics.BodyType
import net.faintedge.fiber.physics.PhysicsManager
import org.jbox2d.dynamics.Filter

class Tile(var typ: Int)
case class TileInfo(val typ: Int, val name: String, val renderer: TileRenderer)

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

class TilePage(val tileData: Array[TileInfo], val width: Int, val height: Int, val tileWidth: Int = 15, val tileHeight: Int = 15) extends Control with Renderable {
  var tiles = Util.ofDim[Tile](width, height, () => new Tile(0))

  def totalWidth = tileWidth * width
  def totalHeight = tileHeight * height

  def setTile(row: Int, col: Int, typ: Int) = tiles(col)(row).typ = typ
  def getTileType(row: Int, col: Int): Int = tiles(col)(row).typ

  override def render(gc: GameContainer, g: Graphics) {
    for (col <- 0 until width; row <- 0 until height) {
      val tileInfo = tileData(getTileType(row, col))
      val tilePosition = getTilePosition(row, col)
      tileInfo.renderer.render(tilePosition._1, tilePosition._2, tileWidth, tileHeight)
    }
  }

  def getTilePosition(row: Int, col: Int): Tuple2[Int, Int] = {
    (col * tileWidth - totalWidth / 2, row * tileHeight - totalHeight / 2)
  }

  def fillTile(g: Graphics, row: Int, col: Int, color: Color) {
    val tilePosition = getTilePosition(row, col)
    g.setColor(color)
    g.fillRect(tilePosition._1 - tileWidth / 2, tilePosition._2 - tileHeight / 2, tileWidth, tileHeight)
  }
}

class PhysicsTilePage(val tileFilter: Filter, tileData: Array[TileInfo], width: Int, height: Int, tileWidth: Int = 15, tileHeight: Int = 15)
  extends TilePage(tileData, width, height, tileWidth, tileHeight) {
  def world = PhysicsManager.world

  val tileBodies = Array.ofDim[Body](width, height)
  val borderHorizontalShape = new Rectangle(totalWidth, tileHeight)
  val borderVerticalShape = new Rectangle(tileWidth, totalHeight)

  val borderBodies = Array(
    PhysicsHelper.newBody(world, BodyType.STATIC, borderHorizontalShape, this, -tileWidth / 2, totalHeight / 2, filter = tileFilter),
    PhysicsHelper.newBody(world, BodyType.STATIC, borderHorizontalShape, this, -tileWidth / 2, -totalHeight / 2 - tileHeight, filter = tileFilter),
    PhysicsHelper.newBody(world, BodyType.STATIC, borderVerticalShape, this, -totalWidth / 2 - tileWidth, -tileHeight / 2, filter = tileFilter),
    PhysicsHelper.newBody(world, BodyType.STATIC, borderVerticalShape, this, totalWidth / 2, -tileHeight / 2, filter = tileFilter))

  override def setTile(row: Int, col: Int, typ: Int) {
    if (tiles(col)(row).typ == 0 && typ != 0) {
      // create new static body for tile of type typ
      assert(tileBodies(col)(row) == null)
      val shape = new Rectangle(tileWidth, tileHeight)
      val body = PhysicsHelper.newBody(world, BodyType.STATIC, shape, tiles(col)(row),
        col * tileWidth - totalWidth / 2,
        row * tileHeight - totalHeight / 2,
        filter = tileFilter)
      tileBodies(col)(row) = body
    } else if (tiles(col)(row).typ != 0 && typ == 0) {
      // destroy static body at row,col
      assert(tileBodies(col)(row) != null)
      world.destroyBody(tileBodies(col)(row))
      tileBodies(col)(row) = null
    }
    super.setTile(row, col, typ)
  }
}