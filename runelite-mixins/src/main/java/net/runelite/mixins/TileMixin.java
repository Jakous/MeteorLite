/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.mixins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meteor.events.AutomationMouseMoved;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.mixins.FieldHook;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.*;
import org.sponge.util.Logger;

@Mixin(RSTile.class)
public abstract class TileMixin implements RSTile
{
  @Shadow("client")
  private static RSClient client;

  @Inject
  private static RSGameObject lastGameObject;

  @Inject
  private static RSNodeDeque[][][] lastGroundItems = new RSNodeDeque[Constants.MAX_Z][Constants.SCENE_SIZE][Constants.SCENE_SIZE];

  @Inject
  private WallObject previousWallObject;

  @Inject
  private DecorativeObject previousDecorativeObject;

  @Inject
  private GroundObject previousGroundObject;

  @Inject
  private RSGameObject[] previousGameObjects;

  @Shadow("objDefCache")
  public static HashMap<Integer, RSObjectComposition> objDefCache;

  @Inject
  @Override
  public WorldPoint getWorldLocation()
  {
    return WorldPoint.fromScene(client, getX(), getY(), getPlane());
  }

  @Inject
  @Override
  public Point getSceneLocation()
  {
    return new Point(getX(), getY());
  }

  @Inject
  @Override
  public LocalPoint getLocalLocation()
  {
    return LocalPoint.fromScene(getX(), getY());
  }

  @Inject
  @Override
  public boolean hasLineOfSightTo(Tile other)
  {
    // Thanks to Henke for this method :)

    if (this.getPlane() != other.getPlane())
    {
      return false;
    }

    CollisionData[] collisionData = client.getCollisionMaps();
    if (collisionData == null)
    {
      return false;
    }

    int z = this.getPlane();
    int[][] collisionDataFlags = collisionData[z].getFlags();

    Point p1 = this.getSceneLocation();
    Point p2 = other.getSceneLocation();
    if (p1.getX() == p2.getX() && p1.getY() == p2.getY())
    {
      return true;
    }

    int dx = p2.getX() - p1.getX();
    int dy = p2.getY() - p1.getY();
    int dxAbs = Math.abs(dx);
    int dyAbs = Math.abs(dy);

    int xFlags = CollisionDataFlag.BLOCK_LINE_OF_SIGHT_FULL;
    int yFlags = CollisionDataFlag.BLOCK_LINE_OF_SIGHT_FULL;
    if (dx < 0)
    {
      xFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_EAST;
    }
    else
    {
      xFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_WEST;
    }
    if (dy < 0)
    {
      yFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_NORTH;
    }
    else
    {
      yFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_SOUTH;
    }

    if (dxAbs > dyAbs)
    {
      int x = p1.getX();
      int yBig = p1.getY() << 16; // The y position is represented as a bigger number to handle rounding
      int slope = (dy << 16) / dxAbs;
      yBig += 0x8000; // Add half of a tile
      if (dy < 0)
      {
        yBig--; // For correct rounding
      }
      int direction = dx < 0 ? -1 : 1;

      while (x != p2.getX())
      {
        x += direction;
        int y = yBig >>> 16;
        if ((collisionDataFlags[x][y] & xFlags) != 0)
        {
          // Collision while traveling on the x axis
          return false;
        }
        yBig += slope;
        int nextY = yBig >>> 16;
        if (nextY != y && (collisionDataFlags[x][nextY] & yFlags) != 0)
        {
          // Collision while traveling on the y axis
          return false;
        }
      }
    }
    else
    {
      int y = p1.getY();
      int xBig = p1.getX() << 16; // The x position is represented as a bigger number to handle rounding
      int slope = (dx << 16) / dyAbs;
      xBig += 0x8000; // Add half of a tile
      if (dx < 0)
      {
        xBig--; // For correct rounding
      }
      int direction = dy < 0 ? -1 : 1;

      while (y != p2.getY())
      {
        y += direction;
        int x = xBig >>> 16;
        if ((collisionDataFlags[x][y] & yFlags) != 0)
        {
          // Collision while traveling on the y axis
          return false;
        }
        xBig += slope;
        int nextX = xBig >>> 16;
        if (nextX != x && (collisionDataFlags[nextX][y] & xFlags) != 0)
        {
          // Collision while traveling on the x axis
          return false;
        }
      }
    }

    // No collision
    return true;
  }

  @Inject
  @Override
  public List<TileItem> getGroundItems()
  {
    ItemLayer layer = this.getItemLayer();
    if (layer == null)
    {
      return null;
    }

    List<TileItem> result = new ArrayList<TileItem>();
    Node node = layer.getBottom();
    while (node instanceof RSTileItem)
    {
      RSTileItem item = (RSTileItem) node;
      item.setX(getX());
      item.setY(getY());
      result.add(item);
      node = node.getNext();
    }
    return result;
  }

  @FieldHook("boundaryObject")
  @Inject
  public void wallObjectChanged(int idx)
  {
    WallObject previous = previousWallObject;
    RSBoundaryObject current = (RSBoundaryObject) getWallObject();

    previousWallObject = current;

    if (current != null)
    {
      int plane = getRenderLevel();

      if ((client.getTileSettings()[1][getX()][getY()] & 2) == 2)
      {
        plane--;
      }

      current.setPlane(plane);
    }

    if (current == null && previous != null)
    {
      WallObjectDespawned wallObjectDespawned = new WallObjectDespawned();
      wallObjectDespawned.setTile(this);
      wallObjectDespawned.setWallObject(previous);
      client.getCallbacks().post(wallObjectDespawned);
      objDefCache.remove(previous.getId());
    }
    else if (current != null && previous == null)
    {
      WallObjectSpawned wallObjectSpawned = new WallObjectSpawned();
      wallObjectSpawned.setTile(this);
      wallObjectSpawned.setWallObject(current);
      client.getCallbacks().post(wallObjectSpawned);
    }
    else if (current != null)
    {
      WallObjectChanged wallObjectChanged = new WallObjectChanged();
      wallObjectChanged.setTile(this);
      wallObjectChanged.setPrevious(previous);
      wallObjectChanged.setWallObject(current);
      client.getCallbacks().post(wallObjectChanged);
    }
  }

  @FieldHook("wallDecoration")
  @Inject
  public void decorativeObjectChanged(int idx)
  {
    DecorativeObject previous = previousDecorativeObject;
    RSWallDecoration current = (RSWallDecoration) getDecorativeObject();

    previousDecorativeObject = current;

    if (current != null)
    {
      int plane = getRenderLevel();

      if ((client.getTileSettings()[1][getX()][getY()] & 2) == 2)
      {
        plane--;
      }

      current.setPlane(plane);
    }

    if (current == null && previous != null)
    {
      DecorativeObjectDespawned decorativeObjectDespawned = new DecorativeObjectDespawned();
      decorativeObjectDespawned.setTile(this);
      decorativeObjectDespawned.setDecorativeObject(previous);
      client.getCallbacks().post(decorativeObjectDespawned);
      objDefCache.remove(previous.getId());
    }
    else if (current != null && previous == null)
    {
      DecorativeObjectSpawned decorativeObjectSpawned = new DecorativeObjectSpawned();
      decorativeObjectSpawned.setTile(this);
      decorativeObjectSpawned.setDecorativeObject(current);
      client.getCallbacks().post(decorativeObjectSpawned);
    }
    else if (current != null)
    {
      DecorativeObjectChanged decorativeObjectChanged = new DecorativeObjectChanged();
      decorativeObjectChanged.setTile(this);
      decorativeObjectChanged.setPrevious(previous);
      decorativeObjectChanged.setDecorativeObject(current);
      client.getCallbacks().post(decorativeObjectChanged);
    }
  }

  @FieldHook("floorDecoration")
  @Inject
  public void groundObjectChanged(int idx)
  {
    GroundObject previous = previousGroundObject;
    RSFloorDecoration current = (RSFloorDecoration) getGroundObject();

    previousGroundObject = current;

    if (current != null)
    {
      int plane = getRenderLevel();

      if ((client.getTileSettings()[1][getX()][getY()] & 2) == 2)
      {
        plane--;
      }

      current.setPlane(plane);
    }

    if (current == null && previous != null)
    {
      GroundObjectDespawned groundObjectDespawned = new GroundObjectDespawned();
      groundObjectDespawned.setTile(this);
      groundObjectDespawned.setGroundObject(previous);
      client.getCallbacks().post(groundObjectDespawned);
      objDefCache.remove(previous.getId());
    }
    else if (current != null && previous == null)
    {
      GroundObjectSpawned groundObjectSpawned = new GroundObjectSpawned();
      groundObjectSpawned.setTile(this);
      groundObjectSpawned.setGroundObject(current);
      client.getCallbacks().post(groundObjectSpawned);
    }
    else if (current != null)
    {
      GroundObjectChanged groundObjectChanged = new GroundObjectChanged();
      groundObjectChanged.setTile(this);
      groundObjectChanged.setPrevious(previous);
      groundObjectChanged.setGroundObject(current);
      client.getCallbacks().post(groundObjectChanged);
    }
  }

  @FieldHook("gameObjects")
  @Inject
  public void gameObjectsChanged(int idx)
  {
    if (idx == -1) // this happens from the field assignment
    {
      return;
    }

    if (previousGameObjects == null)
    {
      previousGameObjects = new RSGameObject[5];
    }

    // Previous game object
    RSGameObject previous = previousGameObjects[idx];

    // GameObject that was changed.
    RSGameObject current = (RSGameObject) getGameObjects()[idx];

    // Update previous object to current
    previousGameObjects[idx] = current;

    if (current != null)
    {
      int plane = getRenderLevel();

      if ((client.getTileSettings()[1][getX()][getY()] & 2) == 2)
      {
        plane--;
      }

      current.setPlane(plane);
    }

    // Duplicate event, return
    if (current == previous)
    {
      return;
    }

    // actors, projectiles, and graphics objects are added and removed from the scene each frame as GameObjects,
    // so ignore them.
    boolean currentInvalid = false, prevInvalid = false;
    if (current != null)
    {
      RSRenderable renderable = current.getRenderable();
      currentInvalid = renderable instanceof RSActor || renderable instanceof RSProjectile || renderable instanceof RSGraphicsObject;
      currentInvalid |= current.getStartX() != this.getX() || current.getStartY() != this.getY();
    }

    if (previous != null)
    {
      RSRenderable renderable = previous.getRenderable();
      prevInvalid = renderable instanceof RSActor || renderable instanceof RSProjectile || renderable instanceof RSGraphicsObject;
      prevInvalid |= previous.getStartX() != this.getX() || previous.getStartY() != this.getY();
    }

    Logger logger = client.getLogger();
    if (current == null)
    {
      if (prevInvalid)
      {
        return;
      }

      GameObjectDespawned gameObjectDespawned = new GameObjectDespawned();
      gameObjectDespawned.setTile(this);
      gameObjectDespawned.setGameObject(previous);
      client.getCallbacks().post(gameObjectDespawned);
      objDefCache.remove(previous.getId());
    }
    else if (previous == null)
    {
      if (currentInvalid)
      {
        return;
      }

      GameObjectSpawned gameObjectSpawned = new GameObjectSpawned();
      gameObjectSpawned.setTile(this);
      gameObjectSpawned.setGameObject(current);
      client.getCallbacks().post(gameObjectSpawned);
    }
    else
    {
      if (currentInvalid && prevInvalid)
      {
        return;
      }

      GameObjectChanged gameObjectsChanged = new GameObjectChanged();
      gameObjectsChanged.setTile(this);
      gameObjectsChanged.setOldObject(previous);
      gameObjectsChanged.setNewObject(current);
      client.getCallbacks().post(gameObjectsChanged);
    }
  }

  @FieldHook("itemLayer")
  @Inject
  public void itemLayerChanged(int idx)
  {
    int x = getX();
    int y = getY();
    int z = client.getPlane();
    RSNodeDeque[][][] groundItemDeque = client.getGroundItemDeque();

    RSNodeDeque oldQueue = lastGroundItems[z][x][y];
    RSNodeDeque newQueue = groundItemDeque[z][x][y];

    if (client.getGameState() != GameState.LOGGED_IN)
    {
      lastGroundItems[z][x][y] = newQueue;
      client.setLastItemDespawn(null);
      return;
    }

    if (oldQueue != newQueue)
    {
      if (oldQueue != null)
      {
        // despawn everything in old ..
        RSNode head = oldQueue.getSentinel();
        for (RSNode cur = head.getNext(); cur != head; cur = cur.getNext())
        {
          RSTileItem item = (RSTileItem) cur;
          ItemDespawned itemDespawned = new ItemDespawned(this, item);
          client.getCallbacks().post(itemDespawned);
        }
      }
      lastGroundItems[z][x][y] = newQueue;
    }

    RSTileItem lastUnlink = client.getLastItemDespawn();
    if (lastUnlink != null)
    {
      client.setLastItemDespawn(null);
    }

    RSItemLayer itemLayer = (RSItemLayer) getItemLayer();
    if (itemLayer == null)
    {
      if (lastUnlink != null)
      {
        ItemDespawned itemDespawned = new ItemDespawned(this, lastUnlink);
        client.getCallbacks().post(itemDespawned);
      }
      return;
    }

    if (newQueue == null)
    {
      if (lastUnlink != null)
      {
        ItemDespawned itemDespawned = new ItemDespawned(this, lastUnlink);
        client.getCallbacks().post(itemDespawned);
      }
      return;
    }

    // The new item gets added to either the head, or the tail, depending on its price
    RSNode head = newQueue.getSentinel();
    RSTileItem current = null;
    RSNode next = head.getPrevious();
    //boolean forward = false;
    if (head != next)
    {
      RSTileItem prev = (RSTileItem) next;
      if (x != prev.getX() || y != prev.getY())
      {
        current = prev;
      }
    }

    RSNode previous = head.getNext();
    if (current == null && head != previous)
    {
      RSTileItem n = (RSTileItem) previous;
      if (x != n.getX() || y != n.getY())
      {
        current = n;
        //forward = true;
      }
    }

    if (lastUnlink != null && lastUnlink != next && lastUnlink != previous)
    {
      ItemDespawned itemDespawned = new ItemDespawned(this, lastUnlink);
      client.getCallbacks().post(itemDespawned);
    }

    if (current != null)
    {
      current.setX(x);
      current.setY(y);
      ItemSpawned event = new ItemSpawned(this, current);
      client.getCallbacks().post(event);
    }
  }

  @Override
  @Inject
  public void walkHere() {
    client.interact(0, MenuAction.WALK.getId(), getX(), getY());
  }
}
