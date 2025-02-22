/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
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

package meteor.plugins.grounditems;

import java.awt.Color;
import meteor.config.Alpha;
import meteor.config.Config;
import meteor.config.ConfigGroup;
import meteor.config.ConfigItem;
import meteor.config.ConfigSection;
import meteor.config.Range;
import meteor.config.Units;
import meteor.plugins.grounditems.config.DespawnTimerMode;
import meteor.plugins.grounditems.config.ItemHighlightMode;
import meteor.plugins.grounditems.config.MenuHighlightMode;
import meteor.plugins.grounditems.config.PriceDisplayMode;
import meteor.plugins.grounditems.config.ValueCalculationMode;

@ConfigGroup("grounditems")
public interface GroundItemsConfig extends Config {

  @ConfigSection(
      name = "Item Lists",
      keyName = "itemLists",
      description = "The highlighted and hidden item lists",
      position = 0,
      closedByDefault = true
  )
  String itemLists = "Item Lists";

  @ConfigItem(
      keyName = "highlightedItems",
      name = "Highlighted Items",
      description = "Configures specifically highlighted ground items. Format: (item), (item)",
      position = 0,
      section = itemLists
  )
  default String getHighlightItems() {
    return "";
  }

  @ConfigItem(
      keyName = "highlightedItems",
      name = "",
      description = ""
  )
  void setHighlightedItem(String key);

  @ConfigItem(
      keyName = "hiddenItems",
      name = "Hidden Items",
      description = "Configures hidden ground items. Format: (item), (item)",
      position = 1,
      section = itemLists
  )
  default String getHiddenItems() {
    return "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed";
  }

  @ConfigItem(
      keyName = "hiddenItems",
      name = "",
      description = ""
  )
  void setHiddenItems(String key);

  @ConfigItem(
      keyName = "showHighlightedOnly",
      name = "Show Highlighted items only",
      description = "Configures whether or not to draw items only on your highlighted list",
      position = 2
  )
  default boolean showHighlightedOnly() {
    return false;
  }

  @ConfigItem(
      keyName = "dontHideUntradeables",
      name = "Do not hide untradeables",
      description = "Configures whether or not untradeable items ignore hiding under settings",
      position = 3
  )
  default boolean dontHideUntradeables() {
    return true;
  }

  @ConfigItem(
      keyName = "showMenuItemQuantities",
      name = "Show Menu Item Quantities",
      description = "Configures whether or not to show the item quantities in the menu",
      position = 4
  )
  default boolean showMenuItemQuantities() {
    return true;
  }

  @ConfigItem(
      keyName = "recolorMenuHiddenItems",
      name = "Recolor Menu Hidden Items",
      description = "Configures whether or not hidden items in right-click menu will be recolored",
      position = 5
  )
  default boolean recolorMenuHiddenItems() {
    return false;
  }

  @ConfigItem(
      keyName = "highlightTiles",
      name = "Highlight Tiles",
      description = "Configures whether or not to highlight tiles containing ground items",
      position = 6
  )
  default boolean highlightTiles() {
    return false;
  }

  @ConfigItem(
      keyName = "priceDisplayMode",
      name = "Price Display Mode",
      description = "Configures which price types are shown alongside ground item name",
      position = 9
  )
  default PriceDisplayMode priceDisplayMode() {
    return PriceDisplayMode.BOTH;
  }

  @ConfigItem(
      keyName = "itemHighlightMode",
      name = "Item Highlight Mode",
      description = "Configures how ground items will be highlighted",
      position = 10
  )
  default ItemHighlightMode itemHighlightMode() {
    return ItemHighlightMode.BOTH;
  }

  @ConfigItem(
      keyName = "menuHighlightMode",
      name = "Menu Highlight Mode",
      description = "Configures what to highlight in right-click menu",
      position = 11
  )
  default MenuHighlightMode menuHighlightMode() {
    return MenuHighlightMode.NAME;
  }

  @ConfigItem(
      keyName = "highlightValueCalculation",
      name = "Highlight Value Calculation",
      description = "Configures which coin value is used to determine highlight color",
      position = 12
  )
  default ValueCalculationMode valueCalculationMode() {
    return ValueCalculationMode.HIGHEST;
  }

  @Range(
      textInput = true
  )
  @ConfigItem(
      keyName = "hideUnderValue",
      name = "Hide under value",
      description = "Configures hidden ground items under both GE and HA value",
      position = 13
  )
  default int getHideUnderValue() {
    return 0;
  }

  @Alpha
  @ConfigItem(
      keyName = "defaultColor",
      name = "Default items",
      description = "Configures the color for default, non-highlighted items",
      position = 14
  )
  default Color defaultColor() {
    return Color.WHITE;
  }

  @Alpha
  @ConfigItem(
      keyName = "highlightedColor",
      name = "Highlighted items",
      description = "Configures the color for highlighted items",
      position = 15
  )
  default Color highlightedColor() {
    return Color.decode("#AA00FF");
  }

  @Alpha
  @ConfigItem(
      keyName = "hiddenColor",
      name = "Hidden items",
      description = "Configures the color for hidden items in right-click menu and when holding ALT",
      position = 16
  )
  default Color hiddenColor() {
    return Color.GRAY;
  }

  @Alpha
  @ConfigItem(
      keyName = "lowValueColor",
      name = "Low value items",
      description = "Configures the color for low value items",
      position = 17
  )
  default Color lowValueColor() {
    return Color.decode("#66B2FF");
  }

  @Range(
    textInput = true
  )
  @ConfigItem(
      keyName = "lowValuePrice",
      name = "Low value price",
      description = "Configures the start price for low value items",
      position = 18
  )
  default int lowValuePrice() {
    return 20000;
  }

  @Alpha
  @ConfigItem(
      keyName = "mediumValueColor",
      name = "Medium value items",
      description = "Configures the color for medium value items",
      position = 19
  )
  default Color mediumValueColor() {
    return Color.decode("#99FF99");
  }

  @Range(
      textInput = true
  )
  @ConfigItem(
      keyName = "mediumValuePrice",
      name = "Medium value price",
      description = "Configures the start price for medium value items",
      position = 20
  )
  default int mediumValuePrice() {
    return 100000;
  }

  @Alpha
  @ConfigItem(
      keyName = "highValueColor",
      name = "High value items",
      description = "Configures the color for high value items",
      position = 21
  )
  default Color highValueColor() {
    return Color.decode("#FF9600");
  }

  @Range(
      textInput = true
  )
  @ConfigItem(
      keyName = "highValuePrice",
      name = "High value price",
      description = "Configures the start price for high value items",
      position = 22
  )
  default int highValuePrice() {
    return 1000000;
  }

  @Alpha
  @ConfigItem(
      keyName = "insaneValueColor",
      name = "Insane value items",
      description = "Configures the color for insane value items",
      position = 23
  )
  default Color insaneValueColor() {
    return Color.decode("#FF66B2");
  }

  @Range(
      textInput = true
  )
  @ConfigItem(
      keyName = "insaneValuePrice",
      name = "Insane value price",
      description = "Configures the start price for insane value items",
      position = 24
  )
  default int insaneValuePrice() {
    return 10000000;
  }

  @ConfigItem(
      keyName = "onlyShowLoot",
      name = "Only show loot",
      description = "Only shows drops from NPCs and players",
      position = 25
  )
  default boolean onlyShowLoot() {
    return false;
  }

  @Range(
      max = 750
  )
  @ConfigItem(
      keyName = "doubleTapDelay",
      name = "Double-tap delay",
      description = "Delay for the double-tap ALT to hide ground items. 0 to disable.",
      position = 26
  )
  @Units(Units.MILLISECONDS)
  default int doubleTapDelay() {
    return 250;
  }

  @ConfigItem(
      keyName = "collapseEntries",
      name = "Collapse ground item menu",
      description = "Collapses ground item menu entries together and appends count",
      position = 27
  )
  default boolean collapseEntries() {
    return false;
  }

  @ConfigItem(
      keyName = "groundItemTimers",
      name = "Despawn timer",
      description = "Shows despawn timers for items you've dropped and received as loot",
      position = 28
  )
  default DespawnTimerMode groundItemTimers() {
    return DespawnTimerMode.OFF;
  }

  @ConfigItem(
      keyName = "textOutline",
      name = "Text Outline",
      description = "Use an outline around text instead of a text shadow",
      position = 29
  )
  default boolean textOutline() {
    return true;
  }

}
