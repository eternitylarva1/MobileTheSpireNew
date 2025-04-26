//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.megacrit.cardcrawl.helpers;

import com.megacrit.cardcrawl.android.mods.utils.ConditionalEvent;
import com.megacrit.cardcrawl.android.mods.utils.EventUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.events.beyond.MoaiHead;
import com.megacrit.cardcrawl.events.beyond.MysteriousSphere;
import com.megacrit.cardcrawl.events.beyond.SecretPortal;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.events.beyond.TombRedMask;
import com.megacrit.cardcrawl.events.beyond.WindingHalls;
import com.megacrit.cardcrawl.events.city.Addict;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.events.city.Beggar;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.city.CursedTome;
import com.megacrit.cardcrawl.events.city.DrugDealer;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.events.city.Ghosts;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.events.city.MaskedBandits;
import com.megacrit.cardcrawl.events.city.Nest;
import com.megacrit.cardcrawl.events.city.TheJoust;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.events.city.TheMausoleum;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.events.exordium.BigFish;
import com.megacrit.cardcrawl.events.exordium.Cleric;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.events.exordium.GoldenIdolEvent;
import com.megacrit.cardcrawl.events.exordium.GoldenWing;
import com.megacrit.cardcrawl.events.exordium.GoopPuddle;
import com.megacrit.cardcrawl.events.exordium.LivingWall;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.events.exordium.Sssserpent;
import com.megacrit.cardcrawl.events.shrines.AccursedBlacksmith;
import com.megacrit.cardcrawl.events.shrines.Bonfire;
import com.megacrit.cardcrawl.events.shrines.Designer;
import com.megacrit.cardcrawl.events.shrines.Duplicator;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.events.shrines.FountainOfCurseRemoval;
import com.megacrit.cardcrawl.events.shrines.GoldShrine;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.events.shrines.GremlinWheelGame;
import com.megacrit.cardcrawl.events.shrines.Lab;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.events.shrines.PurificationShrine;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.events.shrines.UpgradeShrine;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.events.shrines.WomanInBlue;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.megacrit.cardcrawl.android.mods.utils.EventUtils.eventIDs;

public class EventHelper {
    private static final Logger logger = LogManager.getLogger(EventHelper.class.getName());
    private static final float BASE_ELITE_CHANCE = 0.1F;
    private static final float BASE_MONSTER_CHANCE = 0.1F;
    private static final float BASE_SHOP_CHANCE = 0.03F;
    private static final float BASE_TREASURE_CHANCE = 0.02F;
    private static final float RAMP_ELITE_CHANCE = 0.1F;
    private static final float RAMP_MONSTER_CHANCE = 0.1F;
    private static final float RAMP_SHOP_CHANCE = 0.03F;
    private static final float RAMP_TREASURE_CHANCE = 0.02F;
    private static final float RESET_ELITE_CHANCE = 0.0F;
    private static final float RESET_MONSTER_CHANCE = 0.1F;
    private static final float RESET_SHOP_CHANCE = 0.03F;
    private static final float RESET_TREASURE_CHANCE = 0.02F;
    private static float ELITE_CHANCE = 0.1F;
    private static float MONSTER_CHANCE = 0.1F;
    private static float SHOP_CHANCE = 0.03F;
    public static float TREASURE_CHANCE = 0.02F;
    private static ArrayList<Float> saveFilePreviousChances;
    private static String saveFileLastEventChoice;

    public EventHelper() {
    }

    public static RoomResult roll() {
        return roll(AbstractDungeon.eventRng);
    }

    public static RoomResult roll(Random eventRng) {
        saveFilePreviousChances = getChances();
        float roll = eventRng.random();
        logger.info("Rolling for room type... EVENT_RNG_COUNTER: " + AbstractDungeon.eventRng.counter);
        boolean forceChest = false;
        if (AbstractDungeon.player.hasRelic("Tiny Chest")) {
            AbstractRelic r = AbstractDungeon.player.getRelic("Tiny Chest");
            ++r.counter;
            if (r.counter == 4) {
                r.counter = 0;
                r.flash();
                forceChest = true;
            }
        }

        logger.info("ROLL: " + roll);
        logger.info("ELIT: " + ELITE_CHANCE);
        logger.info("MNST: " + MONSTER_CHANCE);
        logger.info("SHOP: " + SHOP_CHANCE);
        logger.info("TRSR: " + TREASURE_CHANCE);
        int eliteSize = 0;
        if (ModHelper.isModEnabled("DeadlyEvents")) {
            eliteSize = (int)(ELITE_CHANCE * 100.0F);
        }

        if (AbstractDungeon.floorNum < 6) {
            eliteSize = 0;
        }

        int monsterSize = (int)(MONSTER_CHANCE * 100.0F);
        int shopSize = (int)(SHOP_CHANCE * 100.0F);
        if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            shopSize = 0;
        }

        int treasureSize = (int)(TREASURE_CHANCE * 100.0F);
        int fillIndex = 0;
        RoomResult[] possibleResults = new RoomResult[100];
        Arrays.fill(possibleResults, EventHelper.RoomResult.EVENT);
        if (ModHelper.isModEnabled("DeadlyEvents")) {
            Arrays.fill(possibleResults, Math.min(99, fillIndex), Math.min(100, fillIndex + eliteSize), EventHelper.RoomResult.ELITE);
            fillIndex += eliteSize;
            Arrays.fill(possibleResults, Math.min(99, fillIndex), Math.min(100, fillIndex + eliteSize), EventHelper.RoomResult.ELITE);
            fillIndex += eliteSize;
        }

        Arrays.fill(possibleResults, Math.min(99, fillIndex), Math.min(100, fillIndex + monsterSize), EventHelper.RoomResult.MONSTER);
        fillIndex += monsterSize;
        Arrays.fill(possibleResults, Math.min(99, fillIndex), Math.min(100, fillIndex + shopSize), EventHelper.RoomResult.SHOP);
        fillIndex += shopSize;
        Arrays.fill(possibleResults, Math.min(99, fillIndex), Math.min(100, fillIndex + treasureSize), EventHelper.RoomResult.TREASURE);
        RoomResult choice = possibleResults[(int)(roll * 100.0F)];
        if (forceChest) {
            choice = EventHelper.RoomResult.TREASURE;
        }

        if (choice == EventHelper.RoomResult.ELITE) {
            ELITE_CHANCE = 0.0F;
            if (ModHelper.isModEnabled("DeadlyEvents")) {
                ELITE_CHANCE = 0.1F;
            }
        } else {
            ELITE_CHANCE += 0.1F;
        }

        if (choice == EventHelper.RoomResult.MONSTER) {
            if (AbstractDungeon.player.hasRelic("Juzu Bracelet")) {
                AbstractDungeon.player.getRelic("Juzu Bracelet").flash();
                choice = EventHelper.RoomResult.EVENT;
            }

            MONSTER_CHANCE = 0.1F;
        } else {
            MONSTER_CHANCE += 0.1F;
        }

        if (choice == EventHelper.RoomResult.SHOP) {
            SHOP_CHANCE = 0.03F;
        } else {
            SHOP_CHANCE += 0.03F;
        }

        if (Settings.isEndless && AbstractDungeon.player.hasBlight("MimicInfestation")) {
            if (choice == EventHelper.RoomResult.TREASURE) {
                if (AbstractDungeon.player.hasRelic("Juzu Bracelet")) {
                    AbstractDungeon.player.getRelic("Juzu Bracelet").flash();
                    choice = EventHelper.RoomResult.EVENT;
                } else {
                    choice = EventHelper.RoomResult.ELITE;
                }

                TREASURE_CHANCE = 0.02F;
                if (ModHelper.isModEnabled("DeadlyEvents")) {
                    TREASURE_CHANCE += 0.02F;
                }
            }
        } else if (choice == EventHelper.RoomResult.TREASURE) {
            TREASURE_CHANCE = 0.02F;
        } else {
            TREASURE_CHANCE += 0.02F;
            if (ModHelper.isModEnabled("DeadlyEvents")) {
                TREASURE_CHANCE += 0.02F;
            }
        }

        return choice;
    }

    public static void resetProbabilities() {
        saveFilePreviousChances = null;
        ELITE_CHANCE = 0.0F;
        MONSTER_CHANCE = 0.1F;
        SHOP_CHANCE = 0.03F;
        TREASURE_CHANCE = 0.02F;
    }

    public static void setChances(ArrayList<Float> chances) {
        ELITE_CHANCE = (Float)chances.get(0);
        MONSTER_CHANCE = (Float)chances.get(1);
        SHOP_CHANCE = (Float)chances.get(2);
        TREASURE_CHANCE = (Float)chances.get(3);
    }

    public static ArrayList<Float> getChances() {
        ArrayList<Float> chances = new ArrayList();
        chances.add(ELITE_CHANCE);
        chances.add(MONSTER_CHANCE);
        chances.add(SHOP_CHANCE);
        chances.add(TREASURE_CHANCE);
        return chances;
    }

    public static ArrayList<Float> getChancesPreRoll() {
        return saveFilePreviousChances != null ? saveFilePreviousChances : getChances();
    }

    public static String getMostRecentEventID() {
        return saveFileLastEventChoice;
    }

    public static AbstractEvent getEvent(String key) {




        saveFileLastEventChoice = key;
        switch (key) {
            case "Accursed Blacksmith":
                return new AccursedBlacksmith();
            case "Bonfire Elementals":
                return new Bonfire();
            case "Fountain of Cleansing":
                return new FountainOfCurseRemoval();
            case "Designer":
                return new Designer();
            case "Duplicator":
                return new Duplicator();
            case "Lab":
                return new Lab();
            case "Match and Keep!":
                return new GremlinMatchGame();
            case "Golden Shrine":
                return new GoldShrine();
            case "Purifier":
                return new PurificationShrine();
            case "Transmorgrifier":
                return new Transmogrifier();
            case "Wheel of Change":
                return new GremlinWheelGame();
            case "Upgrade Shrine":
                return new UpgradeShrine();
            case "FaceTrader":
                return new FaceTrader();
            case "NoteForYourself":
                return new NoteForYourself();
            case "WeMeetAgain":
                return new WeMeetAgain();
            case "The Woman in Blue":
                return new WomanInBlue();
            case "Big Fish":
                return new BigFish();
            case "The Cleric":
                return new Cleric();
            case "Dead Adventurer":
                return new DeadAdventurer();
            case "Golden Wing":
                return new GoldenWing();
            case "Golden Idol":
                return new GoldenIdolEvent();
            case "World of Goop":
                return new GoopPuddle();
            case "Forgotten Altar":
                return new ForgottenAltar();
            case "Scrap Ooze":
                return new ScrapOoze();
            case "Liars Game":
                return new Sssserpent();
            case "Living Wall":
                return new LivingWall();
            case "Mushrooms":
                return new Mushrooms();
            case "N'loth":
                return new Nloth();
            case "Shining Light":
                return new ShiningLight();
            case "Vampires":
                return new Vampires();
            case "Ghosts":
                return new Ghosts();
            case "Addict":
                return new Addict();
            case "Back to Basics":
                return new BackToBasics();
            case "Beggar":
                return new Beggar();
            case "Cursed Tome":
                return new CursedTome();
            case "Drug Dealer":
                return new DrugDealer();
            case "Knowing Skull":
                return new KnowingSkull();
            case "Masked Bandits":
                return new MaskedBandits();
            case "Nest":
                return new Nest();
            case "The Library":
                return new TheLibrary();
            case "The Mausoleum":
                return new TheMausoleum();
            case "The Joust":
                return new TheJoust();
            case "Colosseum":
                return new Colosseum();
            case "Mysterious Sphere":
                return new MysteriousSphere();
            case "SecretPortal":
                return new SecretPortal();
            case "Tomb of Lord Red Mask":
                return new TombRedMask();
            case "Falling":
                return new Falling();
            case "Winding Halls":
                return new WindingHalls();
            case "The Moai Head":
                return new MoaiHead();
            case "SensoryStone":
                return new SensoryStone();
            case "MindBloom":
                return new MindBloom();
            default:
                logger.info("---------------------------\nERROR: Unspecified key: " + key + " in EventHelper.\n---------------------------");
                if (eventIDs.contains(key)) {
                    return EventUtils.getEvent(key);
                }
                return null;
        }
    }

    public static String getEventName(String key) {
        switch (key) {
            case "Accursed Blacksmith":
                return AccursedBlacksmith.NAME;
            case "Bonfire Elementals":
                return Bonfire.NAME;
            case "Fountain of Cleansing":
                return FountainOfCurseRemoval.NAME;
            case "Designer":
                return Designer.NAME;
            case "Duplicator":
                return Duplicator.NAME;
            case "Lab":
                return Lab.NAME;
            case "Match and Keep!":
                return GremlinMatchGame.NAME;
            case "Golden Shrine":
                return GoldShrine.NAME;
            case "Purifier":
                return PurificationShrine.NAME;
            case "Transmorgrifier":
                return Transmogrifier.NAME;
            case "Wheel of Change":
                return GremlinWheelGame.NAME;
            case "Upgrade Shrine":
                return UpgradeShrine.NAME;
            case "FaceTrader":
                return FaceTrader.NAME;
            case "NoteForYourself":
                return NoteForYourself.NAME;
            case "WeMeetAgain":
                return WeMeetAgain.NAME;
            case "The Woman in Blue":
                return WomanInBlue.NAME;
            case "Big Fish":
                return BigFish.NAME;
            case "The Cleric":
                return Cleric.NAME;
            case "Dead Adventurer":
                return DeadAdventurer.NAME;
            case "Golden Wing":
                return GoldenWing.NAME;
            case "Golden Idol":
                return GoldenIdolEvent.NAME;
            case "World of Goop":
                return GoopPuddle.NAME;
            case "Forgotten Altar":
                return ForgottenAltar.NAME;
            case "Scrap Ooze":
                return ScrapOoze.NAME;
            case "Liars Game":
                return Sssserpent.NAME;
            case "Living Wall":
                return LivingWall.NAME;
            case "Mushrooms":
                return Mushrooms.NAME;
            case "N'loth":
                return Nloth.NAME;
            case "Shining Light":
                return ShiningLight.NAME;
            case "Vampires":
                return Vampires.NAME;
            case "Ghosts":
                return Ghosts.NAME;
            case "Addict":
                return Addict.NAME;
            case "Back to Basics":
                return BackToBasics.NAME;
            case "Beggar":
                return Beggar.NAME;
            case "Cursed Tome":
                return CursedTome.NAME;
            case "Drug Dealer":
                return DrugDealer.NAME;
            case "Knowing Skull":
                return KnowingSkull.NAME;
            case "Masked Bandits":
                return MaskedBandits.NAME;
            case "Nest":
                return Nest.NAME;
            case "The Library":
                return TheLibrary.NAME;
            case "The Mausoleum":
                return TheMausoleum.NAME;
            case "The Joust":
                return TheJoust.NAME;
            case "Colosseum":
                return Colosseum.NAME;
            case "Mysterious Sphere":
                return MysteriousSphere.NAME;
            case "SecretPortal":
                return SecretPortal.NAME;
            case "Tomb of Lord Red Mask":
                return TombRedMask.NAME;
            case "Falling":
                return Falling.NAME;
            case "Winding Halls":
                return WindingHalls.NAME;
            case "The Moai Head":
                return MoaiHead.NAME;
            case "SensoryStone":
                return SensoryStone.NAME;
            case "MindBloom":
                return MindBloom.NAME;
            default:
                return "";
        }
    }

    public static enum RoomResult {
        EVENT,
        ELITE,
        TREASURE,
        SHOP,
        MONSTER;

        private RoomResult() {
        }
    }
}
