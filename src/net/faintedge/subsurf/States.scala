package net.faintedge.subsurf

import net.faintedge.fiber.controls._
import net.faintedge.fiber.physics.Physics
import net.faintedge.fiber.{Entity, GameState, Application}
import net.faintedge.subsurf.TileInfo
import org.jbox2d.dynamics.Filter
import org.newdawn.slick.{Color, Image, GameContainer}
import org.newdawn.slick.state.StateBasedGame
import net.faintedge.fiber.EntityConversions._

class PlayState(app: Application) extends GameState(app) {

  val backgroundCategory = 1
  val defaultCategory = 2

  val defaultFilter = new Filter()
  defaultFilter.categoryBits = defaultCategory
  defaultFilter.maskBits = backgroundCategory | defaultCategory

  val bgFilter = new Filter()
  bgFilter.categoryBits = backgroundCategory
  bgFilter.maskBits = backgroundCategory | defaultCategory

  var player: Entity = null
  var tileMap: TileMap = null

  override def init(gc: GameContainer, game: StateBasedGame) {
    initWorld
    initUi

    app.cam.follow(player)
  }

  private def initUi {
    //root += new Entity("selector", Array(new TileSelector(tileMap)))
  }

  private def initWorld {
    val coinImage = new Image("data/coin.png")
    player = new Entity("player", Array(new Transform(), new Physics(filter = defaultFilter), new ForceControl(), new RectangleForm(10, 10), new RectangleRender()))

    val tileData = Array(
      TileInfo(0, "empty", new NullTileRenderer()),
      TileInfo(1, "mud", new ImageTileRenderer(new Image("data/30.png"))),
      TileInfo(2, "stone", new ImageTileRenderer(new Image("data/21.png"))))

    val tileMapEntity = new Entity("tile page", Array(new Transform(), new TileMap(bgFilter, tileData, 1, 2)))
    val tileMapOption = tileMapEntity.getControl(classOf[TileMap])
    tileMap = tileMapOption.get
    for (col <- 0 until tileMap.totalWidthInTiles; row <- 0 until tileMap.totalHeightInTiles) {
      if (Math.random < 0.1) {
        tileMap.setTile(row, col, 1)
      }
    }

    root += new Entity("coin", Array(new Transform(), new Physics(fixedRotation = true, filter = defaultFilter), new CircleForm(coinImage.getHeight / 2), new ImageRender(coinImage)))
    root += new Entity("test box", Array(new Transform(0, 0, (Math.PI / 4).toFloat), new Physics(filter = defaultFilter), new RectangleForm(10, 10), new RectangleRender(Color.white)))
    root += new Entity("test circle", Array(new Transform(), new Physics(filter = defaultFilter), new CircleForm(10), new CircleRender(Color.white)))
    root += player
    root += tileMapEntity
  }
}