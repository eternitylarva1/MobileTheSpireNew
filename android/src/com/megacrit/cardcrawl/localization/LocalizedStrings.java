package com.megacrit.cardcrawl.localization;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.android.SpireAndroidLogger;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.core.Settings;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LocalizedStrings {
    private static final SpireAndroidLogger logger = SpireAndroidLogger.getLogger(LocalizedStrings.class);
    private static final String LOCALIZATION_DIR = "localization";
    public static String PERIOD = null;
    private static Map<String, MonsterStrings> monsters;
    private static Map<String, PowerStrings> powers;
    private static Map<String, CardStrings> cards;
    private static Map<String, RelicStrings> relics;
    private static Map<String, EventStrings> events;
    private static Map<String, PotionStrings> potions;
    private static Map<String, CreditStrings> credits;
    private static Map<String, TutorialStrings> tutorials;
    private static Map<String, KeywordStrings> keywords;
    private static Map<String, ScoreBonusStrings> scoreBonuses;
    private static Map<String, CharacterStrings> characters;
    private static Map<String, UIStrings> ui;
    private static Map<String, OrbStrings> orb;
    private static Map<String, StanceStrings> stance;
    public static Map<String, RunModStrings> mod;
    private static Map<String, BlightStrings> blights;
    private static Map<String, AchievementStrings> achievements;
    public static String break_chars = null;

    public LocalizedStrings() {
        long startTime = System.currentTimeMillis();
        Gson gson = new Gson();
        String langPackDir;
        switch(Settings.language) {
            case ENG:
                langPackDir = "localization" + File.separator + "eng";
                break;
            case DUT:
                langPackDir = "localization" + File.separator + "dut";
                break;
            case EPO:
                langPackDir = "localization" + File.separator + "epo";
                break;
            case PTB:
                langPackDir = "localization" + File.separator + "ptb";
                break;
            case ZHS:
                langPackDir = "localization" + File.separator + "zhs";
                break;
            case ZHT:
                langPackDir = "localization" + File.separator + "zht";
                break;
            case FRA:
                langPackDir = "localization" + File.separator + "fra";
                break;
            case DEU:
                langPackDir = "localization" + File.separator + "deu";
                break;
            case GRE:
                langPackDir = "localization" + File.separator + "gre";
                break;
            case IND:
                langPackDir = "localization" + File.separator + "ind";
                break;
            case ITA:
                langPackDir = "localization" + File.separator + "ita";
                break;
            case JPN:
                if (Settings.isConsoleBuild) {
                    langPackDir = "localization" + File.separator + "jpn";
                } else {
                    langPackDir = "localization" + File.separator + "jpn2";
                }
                break;
            case KOR:
                langPackDir = "localization" + File.separator + "kor";
                break;
            case NOR:
                langPackDir = "localization" + File.separator + "nor";
                break;
            case POL:
                langPackDir = "localization" + File.separator + "pol";
                break;
            case RUS:
                langPackDir = "localization" + File.separator + "rus";
                break;
            case SPA:
                langPackDir = "localization" + File.separator + "spa";
                break;
            case SRP:
                langPackDir = "localization" + File.separator + "srp";
                break;
            case SRB:
                langPackDir = "localization" + File.separator + "srb";
                break;
            case THA:
                langPackDir = "localization" + File.separator + "tha";
                break;
            case TUR:
                langPackDir = "localization" + File.separator + "tur";
                break;
            case UKR:
                langPackDir = "localization" + File.separator + "ukr";
                break;
            case VIE:
                langPackDir = "localization" + File.separator + "vie";
                break;
            case WWW:
                langPackDir = "localization" + File.separator + "www";
                break;
            default:
                langPackDir = "localization" + File.separator + "www";
        }

        String monsterPath = langPackDir + File.separator + "monsters.json";
        Type monstersType = (new TypeToken<Map<String, MonsterStrings>>() {
        }).getType();
        monsters = gson.fromJson(loadJson(monsterPath), monstersType);
        String powerPath = langPackDir + File.separator + "powers.json";
        Type powerType = (new TypeToken<Map<String, PowerStrings>>() {
        }).getType();
        powers = gson.fromJson(loadJson(powerPath), powerType);
        String cardPath = langPackDir + File.separator + "cards.json";
        Type cardType = (new TypeToken<Map<String, CardStrings>>() {
        }).getType();
        cards = gson.fromJson(loadJson(cardPath), cardType);
        String relicPath = langPackDir + File.separator + "relics.json";
        Type relicType = (new TypeToken<Map<String, RelicStrings>>() {
        }).getType();
        relics = gson.fromJson(loadJson(relicPath), relicType);
        String eventPath = langPackDir + File.separator + "events.json";
        Type eventType = (new TypeToken<Map<String, EventStrings>>() {
        }).getType();
        events = gson.fromJson(loadJson(eventPath), eventType);
        String potionPath = langPackDir + File.separator + "potions.json";
        Type potionType = (new TypeToken<Map<String, PotionStrings>>() {
        }).getType();
        potions = gson.fromJson(loadJson(potionPath), potionType);
        String creditPath = langPackDir + File.separator + "credits.json";
        Type creditType = (new TypeToken<Map<String, CreditStrings>>() {
        }).getType();
        credits = gson.fromJson(loadJson(creditPath), creditType);
        String tutorialsPath = langPackDir + File.separator + "tutorials.json";
        Type tutorialType = (new TypeToken<Map<String, TutorialStrings>>() {
        }).getType();
        tutorials = gson.fromJson(loadJson(tutorialsPath), tutorialType);
        String keywordsPath = langPackDir + File.separator + "keywords.json";
        Type keywordsType = (new TypeToken<Map<String, KeywordStrings>>() {
        }).getType();
        keywords = gson.fromJson(loadJson(keywordsPath), keywordsType);
        String scoreBonusesPath = langPackDir + File.separator + "score_bonuses.json";
        Type scoreBonusType = (new TypeToken<Map<String, ScoreBonusStrings>>() {
        }).getType();
        scoreBonuses = gson.fromJson(loadJson(scoreBonusesPath), scoreBonusType);
        String characterPath = langPackDir + File.separator + "characters.json";
        Type characterType = (new TypeToken<Map<String, CharacterStrings>>() {
        }).getType();
        characters = gson.fromJson(loadJson(characterPath), characterType);
        String uiPath = langPackDir + File.separator + "ui.json";
        Type uiType = (new TypeToken<Map<String, UIStrings>>() {
        }).getType();
        ui = gson.fromJson(loadJson(uiPath), uiType);
        PERIOD = this.getUIString("Period").TEXT[0];
        String orbPath = langPackDir + File.separator + "orbs.json";
        Type orbType = (new TypeToken<Map<String, OrbStrings>>() {
        }).getType();
        orb = gson.fromJson(loadJson(orbPath), orbType);
        String stancePath = langPackDir + File.separator + "stances.json";
        Type stanceType = (new TypeToken<Map<String, StanceStrings>>() {
        }).getType();
        stance = gson.fromJson(loadJson(stancePath), stanceType);
        String modPath = langPackDir + File.separator + "run_mods.json";
        Type modType = (new TypeToken<Map<String, RunModStrings>>() {
        }).getType();
        mod = gson.fromJson(loadJson(modPath), modType);
        String blightPath = langPackDir + File.separator + "blights.json";
        Type blightType = (new TypeToken<Map<String, BlightStrings>>() {
        }).getType();
        blights = gson.fromJson(loadJson(blightPath), blightType);
        String achievePath = langPackDir + File.separator + "achievements.json";
        Type achieveType = (new TypeToken<Map<String, AchievementStrings>>() {
        }).getType();
        achievements = gson.fromJson(loadJson(achievePath), achieveType);
        String lineBreakPath = langPackDir + File.separator + "line_break.json";
        if (Gdx.files.internal(lineBreakPath).exists()) {
            break_chars = Gdx.files.internal(lineBreakPath).readString(StandardCharsets.UTF_8.name());
        }
        BaseMod.receiveEditStrings();
        BaseMod.initializeUnderscorePotionIDs();
        BaseMod.initializeUnderscoreEventIDs();
        logger.info("Loc Strings load time: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public PowerStrings getPowerStrings(String powerName) {
        if (powers.containsKey(powerName)) {
            return powers.get(powerName);
        } else {
            logger.info("[ERROR] PowerString: " + powerName + " not found");
            return PowerStrings.getMockPowerString();
        }
    }

    public MonsterStrings getMonsterStrings(String monsterName) {
        if (monsters.containsKey(monsterName)) {
            return monsters.get(monsterName);
        } else {
            logger.info("[ERROR] MonsterString: " + monsterName + " not found");
            return MonsterStrings.getMockMonsterString();
        }
    }

    public EventStrings getEventString(String eventName) {
        if (events.containsKey(eventName)) {
            return events.get(eventName);
        } else {
            logger.info("[ERROR] EventString: " + eventName + " not found");
            return EventStrings.getMockEventString();
        }
    }

    public PotionStrings getPotionString(String potionName) {
        if (potions.containsKey(potionName)) {
            return potions.get(potionName);
        } else {
            logger.info("[ERROR] PotionString: " + potionName + " not found");
            return PotionStrings.getMockPotionString();
        }
    }

    public CreditStrings getCreditString(String creditName) {
        if (credits.containsKey(creditName)) {
            return credits.get(creditName);
        } else {
            logger.info("[ERROR] CreditString: " + creditName + " not found");
            return CreditStrings.getMockCreditString();
        }
    }

    public TutorialStrings getTutorialString(String tutorialName) {
        if (tutorials.containsKey(tutorialName)) {
            return tutorials.get(tutorialName);
        } else {
            logger.info("[ERROR] TutorialString: " + tutorialName + " not found");
            return TutorialStrings.getMockTutorialString();
        }
    }

    public KeywordStrings getKeywordString(String keywordName) {
        return (KeywordStrings)keywords.get(keywordName);
    }

    public CharacterStrings getCharacterString(String characterName) {
        return characters.get(characterName);
    }

    public UIStrings getUIString(String uiName) {
        return (UIStrings)ui.get(uiName);
    }

    public OrbStrings getOrbString(String orbName) {
        if (orb.containsKey(orbName)) {
            return orb.get(orbName);
        } else {
            logger.info("[ERROR] OrbStrings: " + orbName + " not found");
            return OrbStrings.getMockOrbString();
        }
    }

    public StanceStrings getStanceString(String stanceName) {
        if (stance.containsKey(stanceName)) {
            return stance.get(stanceName);
        } else {
            logger.info("[ERROR] StanceStrings: " + stanceName + " not found");
            return StanceStrings.getMockStanceString();
        }

    }

    public RunModStrings getRunModString(String modName) {
        if (mod.containsKey(modName)) {
            return mod.get(modName);
        } else {
            logger.info("[ERROR] RunModStrings: " + modName + " not found");
            return RunModStrings.getMockModString();
        }
    }

    public BlightStrings getBlightString(String blightName) {
        if (blights.containsKey(blightName)) {
            return blights.get(blightName);
        } else {
            logger.info("[ERROR] BlightStrings: " + blightName + " not found");
            return BlightStrings.getBlightOrbString();
        }
    }

    public ScoreBonusStrings getScoreString(String scoreName) {
        if (scoreBonuses.containsKey(scoreName)) {
            return scoreBonuses.get(scoreName);
        } else {
            logger.info("[ERROR] ScoreBonusStrings: " + scoreName + " not found");
            return ScoreBonusStrings.getScoreBonusString();
        }
    }

    public AchievementStrings getAchievementString(String achievementName) {
        return achievements.get(achievementName);
    }

    public CardStrings getCardStrings(String cardName) {
        if (cards.containsKey(cardName)) {
            return cards.get(cardName);
        } else {
            logger.info("[ERROR] CardString: " + cardName + " not found");
            return CardStrings.getMockCardString();
        }
    }

    public static String[] createMockStringArray(int size) {
        String[] retVal = new String[size];

        for(int i = 0; i < retVal.length; ++i) {
            retVal[i] = "[MISSING_" + i + "]";
        }

        return retVal;
    }

    public RelicStrings getRelicStrings(String relicName) {
        return relics.get(relicName);
    }

    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(StandardCharsets.UTF_8.name());
    }
}

