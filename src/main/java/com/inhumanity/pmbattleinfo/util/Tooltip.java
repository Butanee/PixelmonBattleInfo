package com.inhumanity.pmbattleinfo.util;

import com.inhumanity.pmbattleinfo.config.ClientConfig;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.Effectiveness;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Tooltip {
    private static final int gradient = Color.BLUE.getRGB();
    private static final int background = Color.BLACK.getRGB();
    private static final int alpha = 225;

    public static int getGradient() { return gradient; }
    public static int getBackground() { return background; }
    public static int getAlpha() { return alpha; }

    public static Collection<ITextComponent> getTooltip(final PixelmonClientData PCD) {
        Collection<ITextComponent> res = new ArrayList<>();
        if (PCD == null) {
            return res;
        }

        final PixelmonClientData[] ourPCDs = ClientProxy.battleManager.displayedOurPokemon;

        boolean isEnemy = Arrays.stream(ClientProxy.battleManager.displayedEnemyPokemon).anyMatch(p -> p.pokemonUUID == PCD.pokemonUUID);
        boolean isOurs = (ourPCDs != null) && (!isEnemy && Arrays.stream(ourPCDs).anyMatch(p -> p.pokemonUUID == PCD.pokemonUUID));

        boolean hasMultipleOurs = (ourPCDs != null && ourPCDs.length > 1);

        final boolean configKnowMoveset = ClientConfig.battleKnowEnemyMoveset.get();

        HashMap<TooltipValues, ITextComponent> attributes = new HashMap<>();

        // Name Heading
        TextFormatting nameColor = (isOurs ? TextFormatting.AQUA : (isEnemy ? TextFormatting.RED : TextFormatting.GREEN ));
        attributes.put(TooltipValues.Name, newSTC(getNameToUse(PCD)).withStyle(nameColor).withStyle(TextFormatting.BOLD));

        // General Attributes
            attributes.put(TooltipValues.Types, newSTC("属性: " + String.join(", ", PCD.getBaseStats().getTypes().stream().map(Element::getLocalizedName).toArray(String[]::new))));
            attributes.put(TooltipValues.Palette, newSTC("闪光: " + PCD.palette));
//            attributes.put(TooltipValues.Weight, newSTC("Weight: " + PCD.getBaseStats().getWeight()));
            attributes.put(TooltipValues.Form, newSTC("形态: " + PCD.form));

        // Add Ownership / Config determined Attributes
        if (isOurs && !hasMultipleOurs) {
            attributes.putAll(getTooltipOurs(PCD));
        } else {
            attributes.putAll(getTooltipEnemyAlly(PCD, isEnemy));
        }

        // CREATE TOOLTIP
        res.add(attributes.get(TooltipValues.Name));
        res.add(attributes.get(TooltipValues.HP));
        res.add(attributes.get(TooltipValues.Types));
        res.add(attributes.get(TooltipValues.Ability));
        res.add(attributes.get(TooltipValues.HeldItem));
        res.add(attributes.get(TooltipValues.Form));
        res.add(attributes.get(TooltipValues.Palette));
//        res.add(attributes.get(TooltipValues.Weight));

        // Show Moveset of current Pixelmon if User owns multiple displayed Pixelmon
        // OR if it is an Enemy or an Ally
        // OR if User is spectating a battle
        if ((isOurs && hasMultipleOurs) || (!isOurs && configKnowMoveset) || isSpectating()) {
            res.add(newSTC("")); // Spacer

            String title = (isEnemy ? "对您有效的招式" : "Moves");
            res.add(newSTC(title).withStyle(TextFormatting.GOLD).withStyle(TextFormatting.BOLD));

            // Add moves if they exist
            if (attributes.get(TooltipValues.Move1) != null) {
                res.add(attributes.get(TooltipValues.Move1));
                res.add(attributes.get(TooltipValues.Move1Info));
            }
            if (attributes.get(TooltipValues.Move2) != null) {
                res.add(attributes.get(TooltipValues.Move2));
                res.add(attributes.get(TooltipValues.Move2Info));
            }
            if (attributes.get(TooltipValues.Move3) != null) {
                res.add(attributes.get(TooltipValues.Move3));
                res.add(attributes.get(TooltipValues.Move3Info));
            }
            if (attributes.get(TooltipValues.Move4) != null) {
                res.add(attributes.get(TooltipValues.Move4));
                res.add(attributes.get(TooltipValues.Move4Info));
            }
        }

        return res;
    }

    private static HashMap<TooltipValues, ITextComponent> getTooltipOurs(final PixelmonClientData ours) {
        HashMap<TooltipValues, ITextComponent> res = new HashMap<>();

            res.put(TooltipValues.HP, newSTC(String.format("生命: %d / %d", ours.health.intValue(), ours.maxHealth)));
            res.put(TooltipValues.Ability, newSTC("特性: " + ours.moveset.getAbility().getLocalizedName()));
            res.put(TooltipValues.HeldItem, newSTC("持有物" + ours.heldItem.getLocalizedName()));

        // HP: curr / max
        // Ability
        // Held Item
        return res;
    }

    private static HashMap<TooltipValues, ITextComponent> getTooltipEnemyAlly(final PixelmonClientData AllyOrEnemy, boolean isEnemy) {
        HashMap<TooltipValues, ITextComponent> res = new HashMap<>();

        final boolean configHealth = ClientConfig.battleKnowEnemyHP.get();
        final boolean configAbility = ClientConfig.battleKnowEnemyAbility.get();
        final boolean configHeldItem = ClientConfig.battleKnowEnemyHeldItem.get();
        final boolean configKnowMoveset = ClientConfig.battleKnowEnemyMoveset.get();

        String hpCurr = (!configHealth ? "?" : String.valueOf(AllyOrEnemy.health.intValue()));
        String hpMax = (!configHealth ? "?" : String.valueOf(AllyOrEnemy.maxHealth));
        String ability = (!configAbility ? "?" : AllyOrEnemy.moveset.getAbility().getLocalizedName());
        String heldItemName = (!configHeldItem ? "?" : AllyOrEnemy.heldItem.getLocalizedName());

            res.put(TooltipValues.HP, newSTC(String.format("生命: %s / %s", hpCurr, hpMax)));
            res.put(TooltipValues.Ability, newSTC("特性: " + ability));
            res.put(TooltipValues.HeldItem, newSTC("持有物: " + heldItemName));

        // If moveset config on
        if (configKnowMoveset) {
            Moveset moveset = AllyOrEnemy.moveset;

            int i = 1;
            for (Attack atk : moveset.attacks) {
                if (atk != null && atk.getMove() != null) {
                    int ppCurr = atk.pp;
                    String atkType = atk.getType().getLocalizedName();
                    String atkName = atk.getMove().getAttackName();

                    TooltipValues enumMove = null;
                    TooltipValues enumMoveInfo = null;

                    switch (i) {
                        case 1:
                            enumMove = TooltipValues.Move1;
                            enumMoveInfo = TooltipValues.Move1Info;
                            break;
                        case 2:
                            enumMove = TooltipValues.Move2;
                            enumMoveInfo = TooltipValues.Move2Info;
                            break;
                        case 3:
                            enumMove = TooltipValues.Move3;
                            enumMoveInfo = TooltipValues.Move3Info;
                            break;
                        case 4:
                            enumMove = TooltipValues.Move4;
                            enumMoveInfo = TooltipValues.Move4Info;
                            break;
                    }

                    String atkCategory = atk.getAttackCategory().name();
                        switch (atkCategory) {
                            case "PHYSICAL":    atkCategory = "物理";    break;
                            case "SPECIAL":     atkCategory = "特殊";    break;
                            case "STATUS":      atkCategory = "变化";    break;
                            default:            atkCategory = "-";
                    }
                    int atkPower = atk.movePower;
                    String atkAccuracy = (atk.getMove().getAccuracy() == -1 ? "-" : String.valueOf(atk.getMove().getAccuracy()));

                    TextFormatting color = getMoveTextColor(isEnemy, atk.getType());

                        res.put(enumMove, newSTC(String.format("%2dpp, %s : %s", ppCurr, atkType, atkName)).withStyle(color));
                        res.put(enumMoveInfo, newSTC(String.format("   类型: %s, 威力: %d, 命中: %s", atkCategory, atkPower, atkAccuracy)).withStyle(color));
                }

                i++;
            }
        }

        // HP: curr / max   (CONFIG)
        // Ability          (CONFIG)
        // Held Item        (CONFIG)
        // Move Info P1     (CONFIG)
        // Move Info P2     (^ SAME CONFIG)
        return res;
    }

    private static StringTextComponent newSTC(String str) {
        return new StringTextComponent(str);
    }

    private static String getNameToUse(final PixelmonClientData PCD) {
        String nameDisplay = PCD.getDisplayName();
        String nameSpecies = PCD.species.getLocalizedName();

        String name;
        if (nameDisplay.equals(nameSpecies))    name = nameDisplay;
        else if (RemoveNicknames.getConfig())   name = nameSpecies;
        else                                    name = String.format("%s (%s)", nameDisplay, nameSpecies);

        return name;
    }

    private static TextFormatting getMoveTextColor(boolean isEnemy, Element atkType) {
        TextFormatting color = TextFormatting.WHITE;

        // If PCD is enemy, get effectiveness to Our
        // Otherwise, get effectiveness to Enemy because it is Our
        if (isEnemy)
            color = getMultiplierColor(Element.getTotalEffectiveness(getOurTypes(), atkType, isInverseBattle()));
        else if (isSpectating())
            color = getMultiplierColor(Element.getTotalEffectiveness(getEnemyTypes(), atkType, isInverseBattle()));

        return color;
    }

    private static TextFormatting getMultiplierColor(double m) {
        if      (m == Effectiveness.Max.value) {
            return TextFormatting.DARK_GREEN;
        } else if (m == Effectiveness.Super.value) {
            return TextFormatting.GREEN;
        } else if (m == Effectiveness.Normal.value) {
            return TextFormatting.WHITE;
        } else if (m == Effectiveness.Not.value) {
            return TextFormatting.RED;
        } else if (m == Effectiveness.Barely.value) {
            return TextFormatting.DARK_RED;
        } else {
            return TextFormatting.DARK_GRAY;
        }
    }

    private static boolean isInverseBattle() {
        return ClientProxy.battleManager.rules.hasClause(BattleClauseRegistry.INVERSE_BATTLE);
    }

    private static boolean isSpectating() {
        return ClientProxy.battleManager.isSpectating;
    }

    private static List<Element> getOurTypes() {
        return ClientProxy.battleManager.displayedOurPokemon[0].getBaseStats().getTypes();
    }

    private static List<Element> getEnemyTypes() {
        return ClientProxy.battleManager.displayedEnemyPokemon[0].getBaseStats().getTypes();
    }
}
