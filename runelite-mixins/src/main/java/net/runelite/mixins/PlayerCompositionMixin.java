package net.runelite.mixins;

import net.runelite.api.kit.KitType;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.rs.api.RSPlayerComposition;

@Mixin(RSPlayerComposition.class)
public abstract class PlayerCompositionMixin implements RSPlayerComposition
{
  @Inject
  @Override
  public int getEquipmentId(KitType type)
  {
    if (type.ordinal() > 11)
    {
      return -1;
    }

    int id = getEquipmentIds()[type.getIndex()];
    if (id < 512)
    {
      return -1; // not an item
    }
    return id - 512;
  }

  @Inject
  @Override
  public int getKitId(KitType type)
  {
    if (type.ordinal() > 11)
    {
      return -1;
    }

    int id = getEquipmentIds()[type.getIndex()];
    if (id < 256 || id >= 512)
    {
      return -1; // not a kit
    }
    return id - 256;
  }
}
