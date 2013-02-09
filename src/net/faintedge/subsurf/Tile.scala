package net.faintedge.subsurf
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Filter
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import net.faintedge.fiber.physics.PhysicsManager
import net.faintedge.fiber.shape.Rectangle
import net.faintedge.fiber.controls.Render

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

class TileMap(val tileFilter: Filter, val tileData: Array[TileInfo],
    val width: Int, val height: Int,
    val pageWidth: Int = 32, val pageHeight: Int = 32,
    val tileWidth: Int = 15, val tileHeight: Int = 15) extends Render {
  
  var pages = Util.ofDim[PhysicsTilePage](width, height, (col, row) =>
    new PhysicsTilePage(col * pageWidth * tileWidth - totalWidth/2 + (pageWidth * tileWidth)/2, row * pageHeight * tileHeight - totalHeight/2 + (pageHeight * tileHeight)/2,
        tileFilter, tileData, pageWidth, pageHeight, tileWidth, tileHeight))
  
  val borderHorizontalShape = new Rectangle(totalWidth, tileHeight)
  val borderVerticalShape = new Rectangle(tileWidth, totalHeight)

  val borderBodies = Array(
      PhysicsHelper.newBody(world, BodyType.STATIC, borderHorizontalShape, this, -tileWidth / 2, totalHeight / 2, filter = tileFilter),
      PhysicsHelper.newBody(world, BodyType.STATIC, borderHorizontalShape, this, -tileWidth / 2, -totalHeight / 2 - tileHeight, filter = tileFilter),
      PhysicsHelper.newBody(world, BodyType.STATIC, borderVerticalShape, this, -totalWidth / 2 - tileWidth, -tileHeight / 2, filter = tileFilter),
      PhysicsHelper.newBody(world, BodyType.STATIC, borderVerticalShape, this, totalWidth / 2, -tileHeight / 2, filter = tileFilter))
        
  def world = PhysicsManager.world
  def totalWidthInTiles = width * pageWidth
  def totalHeightInTiles = height * pageHeight
  def totalWidth = width * pageWidth * tileWidth
  def totalHeight = height * pageHeight * tileHeight
  
  override def subRender(gc: GameContainer, g: Graphics) {
    for (pagesCol <- 0 until width; pagesRow <- 0 until height) {
      pages(pagesCol)(pagesRow).subRender(gc, g)
    }
  }
  
  private def getPagePosition(pagesRow: Int, pagesCol: Int): (Int, Int) = {
    (pagesCol * pageWidth * tileWidth - totalWidth / 2, -(pagesRow * pageHeight * tileHeight - totalHeight / 2))
  }
  
  def pageForTile(tileRow: Int, tileCol: Int) = pages(tileCol/pageWidth)(tileRow/pageHeight)
  def getTile(tileRow: Int, tileCol: Int) = pageForTile(tileRow, tileCol).tiles(tileCol%pageWidth)(tileRow%pageHeight)
  
  def setTile(tileRow: Int, tileCol: Int, typ: Int) = pageForTile(tileRow, tileCol).setTile(tileCol%pageWidth, tileRow%pageHeight, typ)
  def getTileType(tileRow: Int, tileCol: Int): Int = getTile(tileRow, tileCol).typ
  
}

class TilePage(val xOffset: Float, val yOffset: Float,
    val tileData: Array[TileInfo], val width: Int, val height: Int,
    val tileWidth: Int = 15, val tileHeight: Int = 15) extends Render {
  
  var tiles = Util.ofDim[Tile](width, height, (col, row) => new Tile(0))

  def totalWidth = tileWidth * width
  def totalHeight = tileHeight * height

  def setTile(row: Int, col: Int, typ: Int) = tiles(col)(row).typ = typ
  def getTileType(row: Int, col: Int): Int = tiles(col)(row).typ

  override def subRender(gc: GameContainer, g: Graphics) {
    g.translate(xOffset, yOffset)
    for (col <- 0 until width; row <- 0 until height) {
      val tileInfo = tileData(getTileType(row, col))
      val tilePosition = getTilePosition(row, col)
      tileInfo.renderer.render(tilePosition._1, tilePosition._2, tileWidth, tileHeight)
    }
    g.translate(-xOffset, -yOffset)
  }

  private def getTilePosition(row: Int, col: Int): (Int, Int) = {
    (col * tileWidth - totalWidth / 2, -(row * tileHeight - totalHeight / 2))
  }

  def fillTile(g: Graphics, row: Int, col: Int, color: Color) {
    val tilePosition = getTilePosition(row, col)
    g.setColor(color)
    g.fillRect(tilePosition._1 - tileWidth / 2, tilePosition._2 - tileHeight / 2, tileWidth, tileHeight)
  }
}

class PhysicsTilePage(xOffset: Float, yOffset: Float,
    val tileFilter: Filter, tileData: Array[TileInfo],
    width: Int, height: Int, tileWidth: Int = 15, tileHeight: Int = 15)
  extends TilePage(xOffset, yOffset, tileData, width, height, tileWidth, tileHeight) {
  
  def world = PhysicsManager.world
  val tileBodies = Array.ofDim[Body](width, height)

  override def setTile(row: Int, col: Int, typ: Int) {
    if (tiles(col)(row).typ == 0 && typ != 0) {
      // create new static body for tile of type typ
      assert(tileBodies(col)(row) == null)
      val shape = new Rectangle(tileWidth, tileHeight)
      val body = PhysicsHelper.newBody(world, BodyType.STATIC, shape, tiles(col)(row),
        xOffset + col * tileWidth - totalWidth / 2,
        -yOffset + row * tileHeight - totalHeight / 2,
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