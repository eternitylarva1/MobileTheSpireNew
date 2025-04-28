package com.megacrit.cardcrawl.unlock;

import com.megacrit.cardcrawl.android.SpireAndroidLogger;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomUnlock;
import com.megacrit.cardcrawl.android.mods.helpers.CountModdedUnlockCards;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.unlock.AbstractUnlock.UnlockType;
import com.megacrit.cardcrawl.unlock.cards.defect.EchoFormUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.HyperbeamUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.MeteorStrikeUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.NovaUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.ReboundUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.RecycleUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.SunderUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.TurboUnlock;
import com.megacrit.cardcrawl.unlock.cards.defect.UndoUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.EvolveUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.ExhumeUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.HavocUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.HeavyBladeUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.ImmolateUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.LimitBreakUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.SentinelUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.SpotWeaknessUnlock;
import com.megacrit.cardcrawl.unlock.cards.ironclad.WildStrikeUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.AccuracyUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.BaneUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.CatalystUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.CloakAndDaggerUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.ConcentrateUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.CorpseExplosionUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.GrandFinaleUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.SetupUnlock;
import com.megacrit.cardcrawl.unlock.cards.silent.StormOfSteelUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.AlphaUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.BlasphemyUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.ClarityUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.DevotionUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.ForeignInfluenceUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.ForesightUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.MentalFortressUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.ProstrateUnlock;
import com.megacrit.cardcrawl.unlock.cards.watcher.WishUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.CablesUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.DataDiskUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.EmotionChipUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.RunicCapacitorUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.TurnipUnlock;
import com.megacrit.cardcrawl.unlock.relics.defect.VirusUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.BlueCandleUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.DeadBranchUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.OmamoriUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.PrayerWheelUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.ShovelUnlock;
import com.megacrit.cardcrawl.unlock.relics.ironclad.SingingBowlUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.ArtOfWarUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.CourierUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.DuvuDollUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.PandorasBoxUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.SmilingMaskUnlock;
import com.megacrit.cardcrawl.unlock.relics.silent.TinyChestUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.AkabekoUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.CeramicFishUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.CloakClaspUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.StrikeDummyUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.TeardropUnlock;
import com.megacrit.cardcrawl.unlock.relics.watcher.YangUnlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class UnlockTracker {
    private static final SpireAndroidLogger logger = SpireAndroidLogger.getLogger(UnlockTracker.class);
    public static Prefs unlockPref;
    public static Prefs seenPref;
    public static Prefs betaCardPref;
    public static Prefs bossSeenPref;
    public static Prefs relicSeenPref;
    public static Prefs achievementPref;
    public static Prefs unlockProgress;
    public static HashMap<String, String> unlockReqs = new HashMap<>();
    public static ArrayList<String> lockedCards = new ArrayList<>();
    public static ArrayList<String> lockedCharacters = new ArrayList<>();
    public static ArrayList<String> lockedLoadouts = new ArrayList<>();
    public static ArrayList<String> lockedRelics = new ArrayList<>();
    public static int lockedRedCardCount;
    public static int unlockedRedCardCount;
    public static int lockedGreenCardCount;
    public static int unlockedGreenCardCount;
    public static int lockedBlueCardCount;
    public static int unlockedBlueCardCount;
    public static int lockedPurpleCardCount;
    public static int unlockedPurpleCardCount;
    public static int lockedRelicCount;
    public static int unlockedRelicCount;
    private static final int STARTING_UNLOCK_COST = 300;

    public UnlockTracker() {
    }

    public static void initialize() {
        achievementPref = SaveHelper.getPrefs("STSAchievements");
        unlockPref = SaveHelper.getPrefs("STSUnlocks");
        unlockProgress = SaveHelper.getPrefs("STSUnlockProgress");
        seenPref = SaveHelper.getPrefs("STSSeenCards");
        betaCardPref = SaveHelper.getPrefs("STSBetaCardPreference");
        bossSeenPref = SaveHelper.getPrefs("STSSeenBosses");
        relicSeenPref = SaveHelper.getPrefs("STSSeenRelics");
        refresh();
    }

    public static void retroactiveUnlock() {
        ArrayList<String> cardKeys = new ArrayList<>();
        ArrayList<String> relicKeys = new ArrayList<>();
        ArrayList<AbstractUnlock> bundle = new ArrayList<>();
        appendRetroactiveUnlockList(PlayerClass.IRONCLAD, unlockProgress.getInteger(PlayerClass.IRONCLAD.toString() + "UnlockLevel", -1), bundle, cardKeys, relicKeys);
        appendRetroactiveUnlockList(PlayerClass.THE_SILENT, unlockProgress.getInteger(PlayerClass.THE_SILENT.toString() + "UnlockLevel", -1), bundle, cardKeys, relicKeys);
        appendRetroactiveUnlockList(PlayerClass.DEFECT, unlockProgress.getInteger(PlayerClass.DEFECT.toString() + "UnlockLevel", -1), bundle, cardKeys, relicKeys);
        appendRetroactiveUnlockList(PlayerClass.WATCHER, unlockProgress.getInteger(PlayerClass.WATCHER.toString() + "UnlockLevel", -1), bundle, cardKeys, relicKeys);
        for (int i=4;i<CardCrawlGame.characterManager.getAllCharacters().size();i++) {
            PlayerClass p = CardCrawlGame.characterManager.getAllCharacters().get(i).chosenClass;
            appendRetroactiveUnlockList(p, unlockProgress.getInteger(p.toString() + "UnlockLevel", -1), bundle, cardKeys, relicKeys);
        }
        boolean changed = false;
        Iterator var4 = cardKeys.iterator();

        String k;
        while(var4.hasNext()) {
            k = (String)var4.next();
            if (unlockPref.getInteger(k) != 2) {
                unlockPref.putInteger(k, 2);
                changed = true;
                logger.info("RETROACTIVE CARD UNLOCK:  " + k);
            }
        }

        var4 = relicKeys.iterator();

        while(var4.hasNext()) {
            k = (String)var4.next();
            if (unlockPref.getInteger(k) != 2) {
                unlockPref.putInteger(k, 2);
                changed = true;
                logger.info("RETROACTIVE RELIC UNLOCK: " + k);
            }
        }

        if (isCharacterLocked("Watcher") && !isCharacterLocked("Defect") && (isAchievementUnlocked("RUBY") || isAchievementUnlocked("EMERALD") || isAchievementUnlocked("SAPPHIRE"))) {
            unlockPref.putInteger("Watcher", 2);
            lockedCharacters.remove("Watcher");
            changed = true;
        }

        if (changed) {
            logger.info("RETRO UNLOCKED, SAVING");
            unlockPref.flush();
        }

    }

    private static void appendRetroactiveUnlockList(PlayerClass c, int lvl, ArrayList<AbstractUnlock> bundle, ArrayList<String> cardKeys, ArrayList<String> relicKeys) {
        while(lvl > 0) {
            bundle = getUnlockBundle(c, lvl - 1);

            for (AbstractUnlock u : bundle) {
                if (u.type == UnlockType.RELIC) {
                    logger.info(u.key + " should be unlocked.");
                    relicKeys.add(u.key);
                } else if (u.type == UnlockType.CARD) {
                    logger.info(u.key + " should be unlocked.");
                    cardKeys.add(u.key);
                }
            }

            --lvl;
        }

    }

    public static void refresh() {
        lockedCards.clear();
        lockedCharacters.clear();
        lockedLoadouts.clear();
        lockedRelics.clear();
        addCard("Havoc");
        addCard("Sentinel");
        addCard("Exhume");
        addCard("Wild Strike");
        addCard("Evolve");
        addCard("Immolate");
        addCard("Heavy Blade");
        addCard("Spot Weakness");
        addCard("Limit Break");
        addCard("Concentrate");
        addCard("Setup");
        addCard("Grand Finale");
        addCard("Cloak And Dagger");
        addCard("Accuracy");
        addCard("Storm of Steel");
        addCard("Bane");
        addCard("Catalyst");
        addCard("Corpse Explosion");
        addCard("Rebound");
        addCard("Undo");
        addCard("Echo Form");
        addCard("Turbo");
        addCard("Sunder");
        addCard("Meteor Strike");
        addCard("Hyperbeam");
        addCard("Recycle");
        addCard("Core Surge");
        addCard("Prostrate");
        addCard("Blasphemy");
        addCard("Devotion");
        addCard("ForeignInfluence");
        addCard("Alpha");
        addCard("MentalFortress");
        addCard("SpiritShield");
        addCard("Wish");
        addCard("Wireheading");
        addCharacter("The Silent");
        addCharacter("Defect");
        addCharacter("Watcher");
        addRelic("Omamori");
        addRelic("Prayer Wheel");
        addRelic("Shovel");
        addRelic("Art of War");
        addRelic("The Courier");
        addRelic("Pandora's Box");
        addRelic("Blue Candle");
        addRelic("Dead Branch");
        addRelic("Singing Bowl");
        addRelic("Du-Vu Doll");
        addRelic("Smiling Mask");
        addRelic("Tiny Chest");
        addRelic("Cables");
        addRelic("DataDisk");
        addRelic("Emotion Chip");
        addRelic("Runic Capacitor");
        addRelic("Turnip");
        addRelic("Symbiotic Virus");
        addRelic("Akabeko");
        addRelic("Yang");
        addRelic("CeramicFish");
        addRelic("StrikeDummy");
        addRelic("TeardropLocket");
        addRelic("CloakClasp");
        BaseMod.receiveSetUnlocks();
        countUnlockedCards();
    }

    public static int incrementUnlockRamp(int currentCost) {
        switch(currentCost) {
            case 300:
                return 750;
            case 500:
                return 1000;
            case 750:
                return 1000;
            case 1000:
                return 1500;
            case 1500:
                return 2000;
            case 2000:
                return 2500;
            case 2500:
                return 3000;
            case 3000:
                return 3000;
            case 4000:
                return 4000;
            default:
                return currentCost + 250;
        }
    }

    public static void resetUnlockProgress(PlayerClass c) {
        unlockProgress.putInteger(c.toString() + "UnlockLevel", 0);
        unlockProgress.putInteger(c.toString() + "Progress", 0);
        unlockProgress.putInteger(c.toString() + "CurrentCost", 300);
        unlockProgress.putInteger(c.toString() + "TotalScore", 0);
        unlockProgress.putInteger(c.toString() + "HighScore", 0);
    }

    public static int getUnlockLevel(PlayerClass c) {
        return unlockProgress.getInteger(c.toString() + "UnlockLevel", 0);
    }

    public static int getCurrentProgress(PlayerClass c) {
        return unlockProgress.getInteger(c.toString() + "Progress", 0);
    }

    public static int getCurrentScoreCost(PlayerClass c) {
        return unlockProgress.getInteger(c.toString() + "CurrentCost", 300);
    }

    public static void addScore(PlayerClass c, int scoreGained) {
        String key_unlock_level = c.toString() + "UnlockLevel";
        String key_progress = c.toString() + "Progress";
        String key_current_cost = c.toString() + "CurrentCost";
        String key_total_score = c.toString() + "TotalScore";
        String key_high_score = c.toString() + "HighScore";
        logger.info("Keys");
        logger.info(key_unlock_level);
        logger.info(key_progress);
        logger.info(key_current_cost);
        logger.info(key_total_score);
        logger.info(key_high_score);
        int p = unlockProgress.getInteger(key_progress, 0);
        p += scoreGained;
        int total;
        int highscore;
        if (p >= unlockProgress.getInteger(key_current_cost, 300)) {
            logger.info("[DEBUG] Level up!");
            total = unlockProgress.getInteger(key_unlock_level, 0);
            ++total;
            unlockProgress.putInteger(key_unlock_level, total);
            p -= unlockProgress.getInteger(key_current_cost, 300);
            unlockProgress.putInteger(key_progress, p);
            logger.info("[DEBUG] Score Progress: " + key_progress);
            highscore = unlockProgress.getInteger(key_current_cost, 300);
            unlockProgress.putInteger(key_current_cost, incrementUnlockRamp(highscore));
            if (p > unlockProgress.getInteger(key_current_cost, 300)) {
                unlockProgress.putInteger(key_progress, unlockProgress.getInteger(key_current_cost, 300) - 1);
                logger.info("Overfloat maxes out next level");
            }
        } else {
            unlockProgress.putInteger(key_progress, p);
        }

        total = unlockProgress.getInteger(key_total_score, 0);
        total += scoreGained;
        unlockProgress.putInteger(key_total_score, total);
        logger.info("[DEBUG] Total score: " + total);
        highscore = unlockProgress.getInteger(key_high_score, 0);
        if (scoreGained > highscore) {
            unlockProgress.putInteger(key_high_score, scoreGained);
            logger.info("[DEBUG] New high score: " + scoreGained);
        }

        unlockProgress.flush();
    }

    public static void countUnlockedCards() {
        ArrayList<String> tmp = new ArrayList<>();
        int count = 0;
        tmp.add("Havoc");
        tmp.add("Sentinel");
        tmp.add("Exhume");
        tmp.add("Wild Strike");
        tmp.add("Evolve");
        tmp.add("Immolate");
        tmp.add("Heavy Blade");
        tmp.add("Spot Weakness");
        tmp.add("Limit Break");
        Iterator var2 = tmp.iterator();

        String s;
        while(var2.hasNext()) {
            s = (String)var2.next();
            if (!isCardLocked(s)) {
                ++count;
            }
        }

        lockedRedCardCount = tmp.size();
        unlockedRedCardCount = count;
        tmp.clear();
        count = 0;
        tmp.add("Concentrate");
        tmp.add("Setup");
        tmp.add("Grand Finale");
        tmp.add("Cloak And Dagger");
        tmp.add("Accuracy");
        tmp.add("Storm of Steel");
        tmp.add("Bane");
        tmp.add("Catalyst");
        tmp.add("Corpse Explosion");
        var2 = tmp.iterator();

        while(var2.hasNext()) {
            s = (String)var2.next();
            if (!isCardLocked(s)) {
                ++count;
            }
        }

        lockedGreenCardCount = tmp.size();
        unlockedGreenCardCount = count;
        tmp.clear();
        count = 0;
        tmp.add("Rebound");
        tmp.add("Undo");
        tmp.add("Echo Form");
        tmp.add("Turbo");
        tmp.add("Sunder");
        tmp.add("Meteor Strike");
        tmp.add("Hyperbeam");
        tmp.add("Recycle");
        tmp.add("Core Surge");
        var2 = tmp.iterator();

        while(var2.hasNext()) {
            s = (String)var2.next();
            if (!isCardLocked(s)) {
                ++count;
            }
        }

        lockedBlueCardCount = tmp.size();
        unlockedBlueCardCount = count;
        tmp.clear();
        count = 0;
        tmp.add("Prostrate");
        tmp.add("Blasphemy");
        tmp.add("Devotion");
        tmp.add("ForeignInfluence");
        tmp.add("Alpha");
        tmp.add("MentalFortress");
        tmp.add("SpiritShield");
        tmp.add("Wish");
        tmp.add("Wireheading");
        var2 = tmp.iterator();

        while(var2.hasNext()) {
            s = (String)var2.next();
            if (!isCardLocked(s)) {
                ++count;
            }
        }

        lockedPurpleCardCount = tmp.size();
        unlockedPurpleCardCount = count;
        tmp.clear();
        count = 0;
        tmp.add("Omamori");
        tmp.add("Prayer Wheel");
        tmp.add("Shovel");
        tmp.add("Art of War");
        tmp.add("The Courier");
        tmp.add("Pandora's Box");
        tmp.add("Blue Candle");
        tmp.add("Dead Branch");
        tmp.add("Singing Bowl");
        tmp.add("Du-Vu Doll");
        tmp.add("Smiling Mask");
        tmp.add("Tiny Chest");
        tmp.add("Cables");
        tmp.add("DataDisk");
        tmp.add("Emotion Chip");
        tmp.add("Runic Capacitor");
        tmp.add("Turnip");
        tmp.add("Symbiotic Virus");
        tmp.add("Akabeko");
        tmp.add("Yang");
        tmp.add("CeramicFish");
        tmp.add("StrikeDummy");
        tmp.add("TeardropLocket");
        tmp.add("CloakClasp");
        var2 = tmp.iterator();

        while(var2.hasNext()) {
            s = (String)var2.next();
            if (!isRelicLocked(s)) {
                ++count;
            }
        }

        lockedRelicCount = tmp.size();
        unlockedRelicCount = count;
        logger.info("RED UNLOCKS:   " + unlockedRedCardCount + "/" + lockedRedCardCount);
        logger.info("GREEN UNLOCKS: " + unlockedGreenCardCount + "/" + lockedGreenCardCount);
        logger.info("BLUE UNLOCKS: " + unlockedBlueCardCount + "/" + lockedBlueCardCount);
        logger.info("PURPLE UNLOCKS: " + unlockedPurpleCardCount + "/" + lockedPurpleCardCount);
        logger.info("RELIC UNLOCKS: " + unlockedRelicCount + "/" + lockedRelicCount);
        logger.info("CARDS SEEN:    " + seenPref.get().keySet().size() + "/" + CardLibrary.totalCardCount);
        logger.info("RELICS SEEN:   " + relicSeenPref.get().keySet().size() + "/" + RelicLibrary.totalRelicCount);
        if (CardCrawlGame.characterManager != null && CountModdedUnlockCards.enabled) {
            logger.info("Counting modded unlocks.");

            StringBuilder unlockData;
            for(Iterator var1 = BaseMod.getModdedCharacters().iterator(); var1.hasNext(); logger.info(unlockData.toString())) {
                AbstractPlayer p = (AbstractPlayer)var1.next();
                unlockData = new StringBuilder(p.chosenClass.name().toUpperCase() + " UNLOCKS:\t");
                ArrayList<String> lockedCards = BaseMod.getUnlockCards(p.chosenClass);
                int unlockCount = 0;
                if (lockedCards == null) {
                    unlockData.append("0/0");
                    CountModdedUnlockCards.unlockedCardCounts.put(p.chosenClass, 0);
                    CountModdedUnlockCards.lockedCardCounts.put(p.chosenClass, 0);
                } else {
                    for (String id : lockedCards) {
                        if (!UnlockTracker.isCardLocked(id)) {
                            ++unlockCount;
                        }
                    }

                    unlockData.append(unlockCount).append("/").append(lockedCards.size());
                    CountModdedUnlockCards.unlockedCardCounts.put(p.chosenClass, unlockCount);
                    CountModdedUnlockCards.lockedCardCounts.put(p.chosenClass, lockedCards.size());
                }
            }
        }
    }

    public static String getCardsSeenString() {
        return CardLibrary.seenRedCards + CardLibrary.seenGreenCards + CardLibrary.seenBlueCards + CardLibrary.seenPurpleCards + CardLibrary.seenColorlessCards + CardLibrary.seenCurseCards + "/" + CardLibrary.totalCardCount;
    }

    public static String getRelicsSeenString() {
        return RelicLibrary.seenRelics + "/" + RelicLibrary.totalRelicCount;
    }

    public static void addCard(String key) {
        if (unlockPref.getString(key).equals("true")) {
            unlockPref.putInteger(key, 2);
            logger.info("Converting " + key + " from bool to int");
            unlockPref.flush();
        } else if (unlockPref.getString(key).equals("false")) {
            unlockPref.putInteger(key, 0);
            logger.info("Converting " + key + " from bool to int");
            unlockPref.flush();
        }

        if (unlockPref.getInteger(key, 0) != 2) {
            lockedCards.add(key);
        }

    }

    public static void addCharacter(String key) {
        if (unlockPref.getString(key).equals("true")) {
            unlockPref.putInteger(key, 2);
            logger.info("Converting " + key + " from bool to int");
            unlockPref.flush();
        } else if (unlockPref.getString(key).equals("false")) {
            unlockPref.putInteger(key, 0);
            logger.info("Converting " + key + " from bool to int");
            unlockPref.flush();
        }

        if (unlockPref.getInteger(key, 0) != 2) {
            lockedCharacters.add(key);
        }

    }

    public static void addRelic(String key) {
        if (unlockPref.getInteger(key, 0) != 2) {
            lockedRelics.add(key);
        }

    }

    public static void unlockAchievement(String key) {
        if (!Settings.isModded && !Settings.isShowBuild && Settings.isStandardRun()) {
            CardCrawlGame.publisherIntegration.unlockAchievement(key);
            if (!achievementPref.getBoolean(key, false)) {
                achievementPref.putBoolean(key, true);
                logger.info("Achievement Unlocked: " + key);
            }

            if (allAchievementsExceptPlatinumUnlocked() && !isAchievementUnlocked("ETERNAL_ONE")) {
                CardCrawlGame.publisherIntegration.unlockAchievement("ETERNAL_ONE");
                achievementPref.putBoolean("ETERNAL_ONE", true);
                logger.info("Achievement Unlocked: ETERNAL_ONE");
            }

            achievementPref.flush();
        }
    }

    public static boolean allAchievementsExceptPlatinumUnlocked() {
        return achievementPref.data.entrySet().size() >= 45;
    }

    public static boolean isAchievementUnlocked(String key) {
        return achievementPref.getBoolean(key, false);
    }

    public static void unlockLuckyDay() {
        if (!Settings.isModded) {
            String key = "LUCKY_DAY";
            CardCrawlGame.publisherIntegration.unlockAchievement(key);
            if (!achievementPref.getBoolean(key, false)) {
                achievementPref.putBoolean(key, true);
                achievementPref.flush();
                logger.info("Achievement Unlocked: " + key);
            }

        }
    }

    public static void hardUnlock(String key) {
        if (!Settings.isShowBuild) {
            if (unlockPref.getInteger(key, 0) == 1) {
                unlockPref.putInteger(key, 2);
                unlockPref.flush();
                logger.info("Hard Unlock: " + key);
            }

        }
    }

    public static void hardUnlockOverride(String key) {
        if (!Settings.isShowBuild) {
            unlockPref.putInteger(key, 2);
            unlockPref.flush();
            logger.info("Hard Unlock: " + key);
        }
    }

    public static boolean isCardLocked(String key) {
        return lockedCards.contains(key);
    }

    public static void unlockCard(String key) {
        seenPref.putInteger(key, 1);
        seenPref.flush();
        unlockPref.putInteger(key, 2);
        unlockPref.flush();
        lockedCards.remove(key);
        if (CardLibrary.getCard(key) != null) {
            CardLibrary.getCard(key).isSeen = true;
            CardLibrary.getCard(key).unlock();
        }

    }

    public static boolean isCharacterLocked(String key) {
        if (key.equals("The Silent") && Settings.isDemo) {
            return false;
        } else {
            return !Settings.isAlpha && lockedCharacters.contains(key);
        }
    }

    public static boolean isAscensionUnlocked(AbstractPlayer p) {
        int victories = StatsScreen.getVictory(p.getCharStat());
        if (victories > 0) {
            if (!achievementPref.getBoolean("ASCEND_0", false)) {
                unlockAchievement("ASCEND_0");
            }

            if (!achievementPref.getBoolean("ASCEND_10", false)) {
                StatsScreen.retroactiveAscend10Unlock(p.getPrefs());
            }

            if (!achievementPref.getBoolean("ASCEND_20", false)) {
                StatsScreen.retroactiveAscend20Unlock(p.getPrefs());
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean isRelicLocked(String key) {
        return lockedRelics.contains(key);
    }

    public static void markCardAsSeen(String key) {
        if (CardLibrary.getCard(key) != null && !CardLibrary.getCard(key).isSeen) {
            CardLibrary.getCard(key).isSeen = true;
            seenPref.putInteger(key, 1);
            seenPref.flush();
        } else {
            logger.info("Already seen: " + key);
        }

    }

    public static boolean isCardSeen(String key) {
        return seenPref.getInteger(key, 0) != 0;
    }

    public static void markRelicAsSeen(String key) {
        if (RelicLibrary.getRelic(key) != null && !RelicLibrary.getRelic(key).isSeen) {
            RelicLibrary.getRelic(key).isSeen = true;
            relicSeenPref.putInteger(key, 1);
            relicSeenPref.flush();
        } else if (Settings.isDebug) {
            logger.info("Already seen: " + key);
        }

    }

    public static boolean isRelicSeen(String key) {
        return relicSeenPref.getInteger(key, 0) == 1;
    }

    public static void markBossAsSeen(String originalName) {
        if (bossSeenPref.getInteger(originalName) != 1) {
            bossSeenPref.putInteger(originalName, 1);
            bossSeenPref.flush();
        }
    }

    public static boolean isBossSeen(String key) {
        return bossSeenPref.getInteger(key, 0) == 1;
    }

    public static ArrayList<AbstractUnlock> getUnlockBundle(PlayerClass c, int unlockLevel) {
        ArrayList<AbstractUnlock> tmpBundle = new ArrayList<>();
        switch(c.name()) {
            case "IRONCLAD":
                switch(unlockLevel) {
                    case 0:
                        tmpBundle.add(new HeavyBladeUnlock());
                        tmpBundle.add(new SpotWeaknessUnlock());
                        tmpBundle.add(new LimitBreakUnlock());
                        return tmpBundle;
                    case 1:
                        tmpBundle.add(new OmamoriUnlock());
                        tmpBundle.add(new PrayerWheelUnlock());
                        tmpBundle.add(new ShovelUnlock());
                        return tmpBundle;
                    case 2:
                        tmpBundle.add(new WildStrikeUnlock());
                        tmpBundle.add(new EvolveUnlock());
                        tmpBundle.add(new ImmolateUnlock());
                        return tmpBundle;
                    case 3:
                        tmpBundle.add(new HavocUnlock());
                        tmpBundle.add(new SentinelUnlock());
                        tmpBundle.add(new ExhumeUnlock());
                        return tmpBundle;
                    case 4:
                        tmpBundle.add(new BlueCandleUnlock());
                        tmpBundle.add(new DeadBranchUnlock());
                        tmpBundle.add(new SingingBowlUnlock());
                        return tmpBundle;
                    default:
                        return tmpBundle;
                }
            case "THE_SILENT":
                switch(unlockLevel) {
                    case 0:
                        tmpBundle.add(new BaneUnlock());
                        tmpBundle.add(new CatalystUnlock());
                        tmpBundle.add(new CorpseExplosionUnlock());
                        return tmpBundle;
                    case 1:
                        tmpBundle.add(new DuvuDollUnlock());
                        tmpBundle.add(new SmilingMaskUnlock());
                        tmpBundle.add(new TinyChestUnlock());
                        return tmpBundle;
                    case 2:
                        tmpBundle.add(new CloakAndDaggerUnlock());
                        tmpBundle.add(new AccuracyUnlock());
                        tmpBundle.add(new StormOfSteelUnlock());
                        return tmpBundle;
                    case 3:
                        tmpBundle.add(new ArtOfWarUnlock());
                        tmpBundle.add(new CourierUnlock());
                        tmpBundle.add(new PandorasBoxUnlock());
                        return tmpBundle;
                    case 4:
                        tmpBundle.add(new ConcentrateUnlock());
                        tmpBundle.add(new SetupUnlock());
                        tmpBundle.add(new GrandFinaleUnlock());
                        return tmpBundle;
                    default:
                        return tmpBundle;
                }
            case "DEFECT":
                switch(unlockLevel) {
                    case 0:
                        tmpBundle.add(new ReboundUnlock());
                        tmpBundle.add(new UndoUnlock());
                        tmpBundle.add(new EchoFormUnlock());
                        return tmpBundle;
                    case 1:
                        tmpBundle.add(new TurboUnlock());
                        tmpBundle.add(new SunderUnlock());
                        tmpBundle.add(new MeteorStrikeUnlock());
                        return tmpBundle;
                    case 2:
                        tmpBundle.add(new HyperbeamUnlock());
                        tmpBundle.add(new RecycleUnlock());
                        tmpBundle.add(new NovaUnlock());
                        return tmpBundle;
                    case 3:
                        tmpBundle.add(new CablesUnlock());
                        tmpBundle.add(new TurnipUnlock());
                        tmpBundle.add(new RunicCapacitorUnlock());
                        return tmpBundle;
                    case 4:
                        tmpBundle.add(new EmotionChipUnlock());
                        tmpBundle.add(new VirusUnlock());
                        tmpBundle.add(new DataDiskUnlock());
                        return tmpBundle;
                    default:
                        return tmpBundle;
                }
            case "WATCHER":
                switch(unlockLevel) {
                    case 0:
                        tmpBundle.add(new ProstrateUnlock());
                        tmpBundle.add(new BlasphemyUnlock());
                        tmpBundle.add(new DevotionUnlock());
                        break;
                    case 1:
                        tmpBundle.add(new ForeignInfluenceUnlock());
                        tmpBundle.add(new AlphaUnlock());
                        tmpBundle.add(new MentalFortressUnlock());
                        break;
                    case 2:
                        tmpBundle.add(new ClarityUnlock());
                        tmpBundle.add(new WishUnlock());
                        tmpBundle.add(new ForesightUnlock());
                        break;
                    case 3:
                        tmpBundle.add(new AkabekoUnlock());
                        tmpBundle.add(new YangUnlock());
                        tmpBundle.add(new CeramicFishUnlock());
                        break;
                    case 4:
                        tmpBundle.add(new StrikeDummyUnlock());
                        tmpBundle.add(new TeardropUnlock());
                        tmpBundle.add(new CloakClaspUnlock());
                    default:
                        return tmpBundle;
                }
            default:
                CustomUnlock customUnlock = BaseMod.getCustomUnlockBunle(c);
                if (customUnlock != null) {
                    tmpBundle = customUnlock.getUnlockBundle(unlockLevel);
                }
                if (tmpBundle.isEmpty()) {
                    logger.info("No custom unlock bundle found for " + c.toString());
                    return null;
                }
                return tmpBundle;
        }
    }

    public static void addCardUnlockToList(HashMap<String, AbstractUnlock> map, String key, AbstractUnlock unlock) {
        if (isCardLocked(key)) {
            map.put(key, unlock);
        }

    }

    public static void addRelicUnlockToList(HashMap<String, AbstractUnlock> map, String key, AbstractUnlock unlock) {
        if (isRelicLocked(key)) {
            map.put(key, unlock);
        }

    }

    public static float getCompletionPercentage() {
        float totalPercent = 0.0F;
        totalPercent += getAscensionProgress() * 0.3F;
        totalPercent += getUnlockProgress() * 0.25F;
        totalPercent += getAchievementProgress() * 0.35F;
        totalPercent += getSeenCardsProgress() * 0.05F;
        totalPercent += getSeenRelicsProgress() * 0.05F;
        return totalPercent * 100.0F;
    }

    private static float getAscensionProgress() {
        ArrayList<Prefs> allCharacterPrefs = CardCrawlGame.characterManager.getAllPrefs();
        int sum = 0;

        Prefs p;
        for(Iterator var2 = allCharacterPrefs.iterator(); var2.hasNext(); sum += p.getInteger("ASCENSION_LEVEL", 0)) {
            p = (Prefs)var2.next();
        }

        float retVal = (float)sum / 60.0F;
        logger.info("Ascension Progress: " + retVal);
        if (retVal > 1.0F) {
            retVal = 1.0F;
        }

        return retVal;
    }

    private static float getUnlockProgress() {
        int sum = Math.min(getUnlockLevel(PlayerClass.IRONCLAD), 5);
        sum += Math.min(getUnlockLevel(PlayerClass.THE_SILENT), 5);
        sum += Math.min(getUnlockLevel(PlayerClass.DEFECT), 5);
        sum += Math.min(getUnlockLevel(PlayerClass.WATCHER), 5);
        float retVal = (float)sum / 15.0F;
        logger.info("Unlock IC: " + getUnlockLevel(PlayerClass.IRONCLAD));
        logger.info("Unlock Silent: " + getUnlockLevel(PlayerClass.THE_SILENT));
        logger.info("Unlock Defect: " + getUnlockLevel(PlayerClass.DEFECT));
        logger.info("Unlock Watcher: " + getUnlockLevel(PlayerClass.WATCHER));
        logger.info("Unlock Progress: " + retVal);
        if (retVal > 1.0F) {
            retVal = 1.0F;
        }

        return retVal;
    }

    private static float getAchievementProgress() {
        int sum = 0;
        Iterator var1 = StatsScreen.achievements.items.iterator();

        while(var1.hasNext()) {
            AchievementItem item = (AchievementItem)var1.next();
            if (item.isUnlocked) {
                ++sum;
            }
        }

        float retVal = (float)sum / (float)StatsScreen.achievements.items.size();
        logger.info("Achievement Progress: " + retVal);
        if (retVal > 1.0F) {
            retVal = 1.0F;
        }

        return retVal;
    }

    private static float getSeenCardsProgress() {
        int sum = 0;
        Iterator var1 = CardLibrary.cards.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<String, AbstractCard> c = (Entry)var1.next();
            if (((AbstractCard)c.getValue()).isSeen) {
                ++sum;
            }
        }

        float retVal = (float)sum / (float)CardLibrary.cards.size();
        logger.info("Seen Cards Progress: " + retVal);
        if (retVal > 1.0F) {
            retVal = 1.0F;
        }

        return retVal;
    }

    private static float getSeenRelicsProgress() {
        float retVal = (float)RelicLibrary.seenRelics / (float)RelicLibrary.totalRelicCount;
        logger.info("Seen Relics Progress: " + retVal);
        if (retVal > 1.0F) {
            retVal = 1.0F;
        }

        return retVal;
    }

    public static long getTotalPlaytime() {
        return Settings.totalPlayTime;
    }
}
