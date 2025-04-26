package com.megacrit.cardcrawl.android.mods;

import android.os.Build;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.AndroidLauncher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.android.SpireAndroidLogger;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomCard;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomCutscene;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomSavableRaw;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomUnlock;
import com.megacrit.cardcrawl.android.mods.helpers.AddEventParams;
import com.megacrit.cardcrawl.android.mods.helpers.CardColorBundle;
import com.megacrit.cardcrawl.android.mods.helpers.Keyword;
import com.megacrit.cardcrawl.android.mods.interfaces.*;
import com.megacrit.cardcrawl.android.mods.utils.EventUtils;
import com.megacrit.cardcrawl.android.mods.utils.ReflectionHacks;
import com.megacrit.cardcrawl.android.mods.utils.Types;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.badlogic.gdx.utils.reflect.ClassReflection.getField;

public class BaseMod {
    public static boolean modSettingsUp;
    private static List<PostInitializeSubscriber> postInitializeSubscribers = new ArrayList<>();
    private static List<PostRenderSubscriber> postRenderSubscribers = new ArrayList<>();
    private static List<PostUpdateSubscriber> postUpdateSubscribers = new ArrayList<>();
    private static List<PreUpdateSubscriber> preUpdateSubscribers = new ArrayList<>();
    private static List<EditCardsSubscriber> editCardsSubscribers = new ArrayList<>();
    private static List<EditStringsSubscriber> editStringsSubscribers = new ArrayList<>();
    private static List<EditKeywordsSubscriber> editKeywordsSubscribers = new ArrayList<>();
    private static List<PostEnergyRechargeSubscriber> postEnergyRechargeSubscribers = new ArrayList<>();
    private static List<EditCharactersSubscriber> editCharactersSubscribers = new ArrayList<>();
    private static List<EditRelicsSubscriber> editRelicsSubscribers = new ArrayList<>();
    private static List<RenderSubscriber> renderSubscribers = new ArrayList<>();
    private static List<SetUnlocksSubscriber> setUnlocksSubscribers = new ArrayList<>();
    private static List<ModelRenderSubscriber> modelRenderSubscribers = new ArrayList<>();
    private static List<PreRenderSubscriber> preRenderSubscribers = new ArrayList<>();
    private static List<StartGameSubscriber> startGameSubscribers = new ArrayList<>();
    private static List<StartActSubscriber> startActSubscribers = new ArrayList<>();
    private static List<PostBattleSubscriber> postBattleSubscribers = new ArrayList<>();
    private static List<PostCreateStartingDeckSubscriber> postCreateStartingDeckSubscribers = new ArrayList<>();
    private static List<OnCardUseSubscriber> onCardUseSubscribers = new ArrayList<>();
    public static HashMap<String, String> underScoreCardIDs;
    public static HashMap<String, String> underScorePotionIDs;
    public static HashMap<String, String> underScoreEventIDs;
    public static HashMap<String, String> underScoreRelicIDs;
    public static HashMap<String, String> underScorePowerIDs;
    public static HashMap<String, String> underScoreEncounterIDs;
    private static Map<AbstractCard.CardColor, CardColorBundle> colorBundleMap = new LinkedHashMap<>();
    private static Map<AbstractPlayer.PlayerClass, CustomUnlock> unlockBundleMap = new HashMap<>();
    private static List<CustomCard> cardsToAdd = new ArrayList<>();
    private static HashMap<AbstractPlayer.PlayerClass, ArrayList<String>> unlockCards = new HashMap<>();
    private static Map<Type, String> typeMaps;
    private static HashMap<String, Class<? extends AbstractPower>> powerMap;
    private static Map<Type, Type> typeTokens;
    public static ArrayList<String> encounterList;
    public static final SpireAndroidLogger logger = SpireAndroidLogger.getLogger(BaseMod.class);
    public static Map<AbstractPlayer.PlayerClass, CustomCutscene> cutsceneMap = new HashMap<>();
    private static HashMap<String, String> keywordUniqueNames = new HashMap<>();
    private static HashMap<String, String> keywordUniquePrefixes = new HashMap<>();
    private static HashMap<String, String> keywordProperNames = new HashMap<>();
    private static HashMap<String, String> customMonsterNames = new HashMap<>();
    private static HashMap<String, BaseMod.GetMonsterGroup> customMonsters = new HashMap<>();
    private static HashMap<String, List<MonsterInfo>> customMonsterEncounters = new HashMap<>();
    private static HashMap<String, List<MonsterInfo>> customStrongMonsterEncounters = new HashMap<>();
    private static HashMap<String, List<MonsterInfo>> customEliteEncounters = new HashMap<>();
    private static HashMap<String, List<BaseMod.BossInfo>> customBosses = new HashMap<>();
    private static Map<AbstractCard.CardColor, Map<String, AbstractRelic>> customRelicPools = new HashMap<>();
    private static HashMap<String, CustomSavableRaw> customSaveFields = new HashMap<>();
    private static ArrayList<ModBadge> modBadges = new ArrayList<>();
    public static DevConsole console;
    private static Map<AbstractPlayer.PlayerClass, String> playerSelectButtonMap = new HashMap<>();
    private static Map<AbstractPlayer.PlayerClass, String> playerPortraitMap = new HashMap<>();
    private static HashMap<AbstractCard.CardColor, Integer> colorCardCountMap = new HashMap<>();
    private static HashMap<AbstractCard.CardColor, Integer> colorCardSeenCountMap = new HashMap<>();
    private static Map<AbstractPlayer.PlayerClass, String> customModeCharacterButtonMap = new HashMap<>();
    public static final List<AbstractGameAction> actionQueue = new ArrayList<>();
    private static ArrayList<ISubscriber> toRemove = new ArrayList<>();
    private static TextureRegion animationTextureRegion;
    private static Environment animationEnvironment;
    private static FrameBuffer animationBuffer;
    private static Texture animationTexture;
    private static OrthographicCamera animationCamera;
    private static ModelBatch batch;

    public static void initialize() {
        initializeTypeMaps();
        initializePowerMap();
        initializeUnderscorePowerIDs();
        console = new DevConsole();
    }

    public static void initializeTypeMaps() {
        logger.info("initializeTypeMaps");
        typeMaps = new HashMap<>();
        typeTokens = new HashMap<>();
        Field[] fields = LocalizedStrings.class.getDeclaredFields();
        if (Build.VERSION.SDK_INT >= 28) {
            for (Field f : fields) {
                Type type = f.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type[] typeArgs = pType.getActualTypeArguments();
                    if (typeArgs.length == 2 && typeArgs[0].equals(String.class) && typeArgs[1].getTypeName().startsWith("com.megacrit.cardcrawl.localization.") && typeArgs[1].getTypeName().endsWith("Strings")) {
                        logger.info("Registered " + typeArgs[1].getTypeName().replace("com.megacrit.cardcrawl.localization.", ""));
                        typeMaps.put(typeArgs[1], f.getName());
                        ParameterizedType p = Types.newParameterizedTypeWithOwner(null, Map.class, new Type[]{String.class, typeArgs[1]});
                        typeTokens.put(typeArgs[1], p);
                    }
                }
            }
        } else {
            for (Field f : fields) {
                Type type = f.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type[] typeArgs = pType.getActualTypeArguments();
                    switch (f.getName()) {
                        case "monsters":
                            logger.info("Registered MonsterStrings");
                            break;
                        case "powers":
                            logger.info("Registered PowerStrings");
                            break;
                        case "cards":
                            logger.info("Registered CardStrings");
                            break;
                        case "relics":
                            logger.info("Registered RelicStrings");
                            break;
                        case "events":
                            logger.info("Registered EventStrings");
                            break;
                        case "potions":
                            logger.info("Registered PotionStrings");
                            break;
                        case "credits":
                            logger.info("Registered CreditStrings");
                            break;
                        case "tutorials":
                            logger.info("Registered TutorialStrings");
                            break;
                        case "keywords":
                            logger.info("Registered KeywordStrings");
                            break;
                        case "scoreBonuses":
                            logger.info("Registered ScoreBonusStrings");
                            break;
                        case "characters":
                            logger.info("Registered CharacterStrings");
                            break;
                        case "ui":
                            logger.info("Registered UIStrings");
                            break;
                        case "orb":
                            logger.info("Registered OrbStrings");
                            break;
                        case "stance":
                            logger.info("Registered StanceStrings");
                            break;
                        case "mod":
                            logger.info("Registered RunModStrings");
                            break;
                        case "blights":
                            logger.info("Registered BlightStrings");
                            break;
                        case "achievements":
                            logger.info("Registered AchievementStrings");
                            break;
                        default:
                            continue;
                    }
                    typeMaps.put(typeArgs[1], f.getName());
                    ParameterizedType p = Types.newParameterizedTypeWithOwner(null, Map.class, new Type[]{String.class, typeArgs[1]});
                    typeTokens.put(typeArgs[1], p);
                }
            }
        }
    }

    public static void initializeUnderscoreCardIDs() {
        logger.info("initializeUnderscoreCardIDs");
        underScoreCardIDs = new HashMap<>();

        for (String key : CardLibrary.cards.keySet()) {
            if (key.contains(" ")) {
                underScoreCardIDs.put(key.replace(' ', '_'), key);
            }
        }
    }

    public static void initializeUnderscorePotionIDs() {
        logger.info("initializeUnderscorePotionIDs");
        underScorePotionIDs = new HashMap<>();
        Map<String, PotionStrings> potions = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "potions");
        if (potions != null) {
            for (String key : potions.keySet()) {
                if (key.contains(" ")) {
                    underScorePotionIDs.put(key.replace(' ', '_'), key);
                }
            }
        }
    }

    public static void initializeUnderscoreEventIDs() {
        logger.info("initializeUnderscoreEventIDs");
        underScoreEventIDs = new HashMap<>();
        Map<String, EventStrings> events = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "events");
        if (events != null) {
            for (String key : events.keySet()) {
                if (key.contains(" ")) {
                    underScoreEventIDs.put(key.replace(' ', '_'), key);
                }
            }
        }
        EventUtils.loadBaseEvents();
    }

    public static void initializeUnderscoreRelicIDs() {
        logger.info("initializeUnderscoreRelicIDs");
        underScoreRelicIDs = new HashMap<>();
        for (String id : listAllRelicIDs()) {
            if (id.contains(" ")) {
                underScoreRelicIDs.put(id.replace(' ', '_'), id);
            }
        }
    }

    private static void initializePowerMap() {
        logger.info("initializePowerMap");
        powerMap = new HashMap<>();
        //TODO
        powerMap.put(InvinciblePower.POWER_ID, InvinciblePower.class);
    }

    public static void initializeUnderscorePowerIDs() {
        logger.info("initializeUnderscorePowerIDs");
        underScorePowerIDs = new HashMap<>();

        for (String key : powerMap.keySet()) {
            if (key.contains(" ")) {
                underScorePowerIDs.put(key.replace(' ', '_'), key);
            }
        }

    }

    static void initializeEncounters() {
        logger.info("initializeEncounters");
        encounterList = new ArrayList<>();

        try {
            Field[] fields = MonsterHelper.class.getDeclaredFields();

            for (Field f : fields) {
                if (f.getType() == String.class) {
                    int mods = f.getModifiers();
                    if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && Modifier.isFinal(mods)) {
                        encounterList.add((String) f.get(null));
                    }
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        underScoreEncounterIDs = new HashMap<>();

        for (String id : encounterList) {
            if (id.contains(" ")) {
                underScoreEncounterIDs.put(id.replace(' ', '_'), id);
            }
        }

    }

    public static void subscribe(ISubscriber subscriber) {
        subscribeIfInstance(subscriber);
    }

    public static void unsubscribe(ISubscriber subscriber) {
        unsubscribeIfInstance(subscriber);
    }

    public static void addColor(AbstractCard.CardColor cardColor, CardColorBundle bundle) {
        colorBundleMap.put(cardColor, bundle);
    }

    public static void addColor(CardColorBundle bundle) {
        colorBundleMap.put(bundle.cardColor, bundle);
    }

    public static Map<AbstractCard.CardColor, CardColorBundle> getColorBundleMap() {
        return colorBundleMap;
    }

    public static void addCard(CustomCard card) {
        cardsToAdd.add(card);
    }

    public static void addEvent(String eventID, Class<? extends AbstractEvent> eventClass) {
        addEvent((new AddEventParams.Builder(eventID, eventClass)).create());
    }

    public static void addEvent(String eventID, Class<? extends AbstractEvent> eventClass, String dungeonID) {
        addEvent((new AddEventParams.Builder(eventID, eventClass)).dungeonID(dungeonID).create());
    }

    public static void addEvent(AddEventParams params) {
        EventUtils.registerEvent(params.eventID, params.eventClass, params.playerClass, params.dungeonIDs.toArray(new String[0]), params.spawnCondition, params.overrideEventID, params.bonusCondition, params.eventType);
    }

    public static void addPotion(String potionId, AbstractPotion potionToAdd) {
        addPotion(potionId, potionToAdd, null);
    }

    public static void addPotion(String potionId, AbstractPotion potionToAdd, AbstractPlayer.PlayerClass playerClass) {
        PotionHelper.potionMap.put(potionId, potionToAdd);
        List<String> potionList = PotionHelper.characterPotions.getOrDefault(playerClass, null);
        if (potionList == null) {
            potionList = new ArrayList<>();
        }
        potionList.add(potionId);
        PotionHelper.characterPotions.put(playerClass, potionList);
    }

    public static void addRelic(AbstractRelic relic) {
        RelicLibrary.add(relic);
    }

    public static void addRelicToCustomPool(AbstractRelic relic, AbstractCard.CardColor color) {
        RelicLibrary.addModColor(relic, color);
    }

    public static void addPower(Class<? extends AbstractPower> powerClass, String powerID) {
        powerMap.put(powerID, powerClass);
        if (powerID.contains(" ")) {
            underScorePowerIDs.put(powerID.replace(' ', '_'), powerID);
        }
    }

    public static void loadCustomStrings(Type stringType, String jsonString) {
        loadJsonStrings(stringType, jsonString);
    }

    public static void loadCustomStringsFile(String modId, Class<?> stringType, String filepath) {
        loadJsonStrings(stringType, AssetLoader.getString(modId, filepath));
    }

    public static void addKeyword(Keyword keyword) {
        addKeyword(keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
    }

    public static void addKeyword(String modId, Keyword keyword) {
        addKeyword(modId, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
    }

    public static void addKeyword(String proper, String[] names, String description) {
        addKeyword(null, proper, names, description);
    }

    public static void addKeyword(String modID, String proper, String[] names, String description) {
        String uniqueParent;
        int var6;
        int var7;
        String name;
        String[] var9;
        if (modID != null && !modID.isEmpty()) {
            if (!modID.endsWith(":")) {
                modID = modID + ":";
            }

            uniqueParent = names[0];

            for(int i = 0; i < names.length; ++i) {
                names[i] = modID + names[i];
            }

            var9 = names;
            var6 = names.length;

            for(var7 = 0; var7 < var6; ++var7) {
                name = var9[var7];
                keywordUniqueNames.put(name, uniqueParent);
                keywordUniquePrefixes.put(name, modID);
            }
        }

        uniqueParent = names[0];
        if (proper != null) {
            keywordProperNames.put(uniqueParent, proper);
        }

        var9 = names;
        var6 = names.length;

        for(var7 = 0; var7 < var6; ++var7) {
            name = var9[var7];
            GameDictionary.keywords.put(name, description);
            GameDictionary.parentWord.put(name, uniqueParent);
        }
    }

    public static String getKeywordProper(String keyword) {
        return keywordProperNames.get(keyword);
    }

    public static String getKeywordUnique(String keyword) {
        return keywordUniqueNames.get(keyword);
    }

    public static boolean keywordIsUnique(String keyword) {
        return keywordUniqueNames.containsKey(keyword);
    }

    public static String getKeywordPrefix(String keyword) {
        return keywordUniquePrefixes.get(keyword);
    }

    public static String getKeywordTitle(String keyword) {
        keyword = GameDictionary.parentWord.get(keyword);
        String title = getKeywordProper(keyword);
        return title != null ? title : TipHelper.capitalize(keyword);
    }

    public static void addCutscene(CustomCutscene cutscene) {
        cutsceneMap.put(cutscene.getPlayerClass(), cutscene);
    }

    public static CustomUnlock getCustomUnlockBunle(AbstractPlayer.PlayerClass p) {
        return unlockBundleMap.getOrDefault(p, null);
    }

    public static void addCustomUnlock(AbstractPlayer.PlayerClass p, CustomUnlock unlock) {
        unlockBundleMap.put(p, unlock);
        for (int i=0;i<5;i++) {
            for (AbstractUnlock unlock1 : unlock.getUnlockBundle(i)) {
                if (unlock1.type == AbstractUnlock.UnlockType.CARD) {
                    if (!unlockCards.containsKey(p)) {
                        unlockCards.put(p, new ArrayList<>());
                    }
                    unlockCards.get(p).add(unlock1.key);
                }
            }
        }
    }

    public static Class<? extends AbstractPower> getPowerClass(String powerID) {
        return powerMap.get(powerID);
    }

    public static Set<String> getPowerKeys() {
        return powerMap.keySet();
    }

    public static void addMonster(String encounterID, BaseMod.GetMonster monster) {
        addMonster(encounterID, () -> new MonsterGroup(monster.get()));
    }

    public static void addMonster(String encounterID, String name, BaseMod.GetMonster monster) {
        addMonster(encounterID, name, () -> new MonsterGroup(monster.get()));
    }

    public static void addMonster(String encounterID, BaseMod.GetMonsterGroup group) {
        addMonster(encounterID, autoCalculateMonsterName(group), group);
    }

    public static void addMonster(String encounterID, String name, BaseMod.GetMonsterGroup group) {
        customMonsters.put(encounterID, group);
        customMonsterNames.put(encounterID, name);
        encounterList.add(encounterID);
        if (encounterID.contains(" ")) {
            underScoreEncounterIDs.put(encounterID.replace(' ', '_'), encounterID);
        }
    }

    public static MonsterGroup getMonster(String encounterID) {
        BaseMod.GetMonsterGroup getter = customMonsters.get(encounterID);
        return getter == null ? null : getter.get();
    }

    public static String getMonsterName(String encounterID) {
        return customMonsterNames.getOrDefault(encounterID, "");
    }

    public static void addMonsterEncounter(String dungeonID, MonsterInfo encounter) {
        if (!customMonsterEncounters.containsKey(dungeonID)) {
            customMonsterEncounters.put(dungeonID, new ArrayList<>());
        }

        customMonsterEncounters.get(dungeonID).add(encounter);
    }

    public static List<MonsterInfo> getMonsterEncounters(String dungeonID) {
        if (customMonsterEncounters.containsKey(dungeonID)) {
            return customMonsterEncounters.get(dungeonID);
        }
        return new ArrayList<>();
    }

    public static void addStrongMonsterEncounter(String dungeonID, MonsterInfo encounter) {
        if (!customStrongMonsterEncounters.containsKey(dungeonID)) {
            customStrongMonsterEncounters.put(dungeonID, new ArrayList<>());
        }

        customStrongMonsterEncounters.get(dungeonID).add(encounter);
    }

    public static List<MonsterInfo> getStrongMonsterEncounters(String dungeonID) {
        return customStrongMonsterEncounters.containsKey(dungeonID) ? customStrongMonsterEncounters.get(dungeonID) : new ArrayList<>();
    }

    public static void addEliteEncounter(String dungeonID, MonsterInfo encounter) {
        if (!customEliteEncounters.containsKey(dungeonID)) {
            customEliteEncounters.put(dungeonID, new ArrayList<>());
        }

        customEliteEncounters.get(dungeonID).add(encounter);
    }

    public static List<MonsterInfo> getEliteEncounters(String dungeonID) {
        return customEliteEncounters.containsKey(dungeonID) ? customEliteEncounters.get(dungeonID) : new ArrayList<>();
    }

    public static boolean customMonsterExists(String encounterID) {
        return customMonsters.containsKey(encounterID);
    }

    private static String autoCalculateMonsterName(BaseMod.GetMonsterGroup group) {
        StringBuilder ret = new StringBuilder();
        if (AbstractDungeon.monsterRng == null) {
            Settings.seed = 0L;
            AbstractDungeon.generateSeeds();
        }

        MonsterGroup monsters = group.get();
        boolean first = true;

        for (AbstractMonster monster : monsters.monsters) {
            if (!first) {
                ret.append(", ");
            }

            first = false;
            ret.append(monster.name);
        }

        return ret.toString();
    }

    public static void addBoss(String dungeon, String bossID, String mapIcon, String mapIconOutline) {
        if (!customBosses.containsKey(dungeon)) {
            customBosses.put(dungeon, new ArrayList<>());
        }

        BaseMod.BossInfo info = new BaseMod.BossInfo(bossID, mapIcon, mapIconOutline);
        customBosses.get(dungeon).add(info);
    }

    public static List<String> getBossIDs(String dungeonID) {
        return customBosses.containsKey(dungeonID) ? (customBosses.get(dungeonID)).stream().map((info) -> info.id).collect(Collectors.toList()) : new ArrayList<>();
    }

    public static BaseMod.BossInfo getBossInfo(String bossID) {
        if (bossID == null) {
            return null;
        } else {

            for (List<BaseMod.BossInfo> o : customBosses.values()) {
                for (BaseMod.BossInfo info : o) {
                    if (bossID.equals(info.id)) {
                        return info;
                    }
                }
            }

            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadJsonStrings(Type stringType, String jsonString) {
        if (Build.VERSION.SDK_INT >= 28) {
            logger.info("loadJsonStrings: " + stringType.getTypeName());
        }
        String typeMap = typeMaps.get(stringType);
        Type typeToken = typeTokens.get(stringType);
        Map localizationStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, typeMap);
        Gson gson = new Gson();
        Map map = new HashMap(gson.fromJson(jsonString, typeToken));
        if (!stringType.equals(CardStrings.class) && !stringType.equals(RelicStrings.class)) {
            localizationStrings.putAll(map);
        } else {
            Map map2 = new HashMap();
            for (Object k : map.keySet()) {
                map2.put(k, map.get(k));
            }

            localizationStrings.putAll(map2);
        }
        try {
            Field field = LocalizedStrings.class.getDeclaredField(typeMap);
            field.setAccessible(true);
            field.set(null, localizationStrings);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void registerModBadge(String modId, Texture t, String name, String author, String desc, ModPanel settingsPanel) {
        logger.info("registerModBadge : " + name);
        int modBadgeCount = modBadges.size();
        int col = modBadgeCount % 16;
        int row = modBadgeCount / 16;
        float x = 640.0F * Settings.scale + col * 40.0F * Settings.scale;
        float y = 250.0F * Settings.scale - row * 40.0F * Settings.scale;

        ModBadge badge = new ModBadge(t, x, y, name, author, desc, settingsPanel);

        modBadges.add(badge);
    }

    private static void subscribeIfInstance(ISubscriber subscriber) {
        if (subscriber instanceof PostInitializeSubscriber) {
            postInitializeSubscribers.add((PostInitializeSubscriber) subscriber);
        }
        if (subscriber instanceof PostRenderSubscriber) {
            postRenderSubscribers.add((PostRenderSubscriber) subscriber);
        }
        if (subscriber instanceof PostUpdateSubscriber) {
            postUpdateSubscribers.add((PostUpdateSubscriber) subscriber);
        }
        if (subscriber instanceof PreUpdateSubscriber) {
            preUpdateSubscribers.add((PreUpdateSubscriber) subscriber);
        }
        if (subscriber instanceof EditCardsSubscriber) {
            editCardsSubscribers.add((EditCardsSubscriber) subscriber);
        }
        if (subscriber instanceof EditStringsSubscriber) {
            editStringsSubscribers.add((EditStringsSubscriber) subscriber);
        }
        if (subscriber instanceof EditKeywordsSubscriber) {
            editKeywordsSubscribers.add((EditKeywordsSubscriber) subscriber);
        }
        if (subscriber instanceof PostEnergyRechargeSubscriber) {
            postEnergyRechargeSubscribers.add((PostEnergyRechargeSubscriber) subscriber);
        }
        if (subscriber instanceof EditCharactersSubscriber) {
            editCharactersSubscribers.add((EditCharactersSubscriber) subscriber);
        }
        if (subscriber instanceof EditRelicsSubscriber) {
            editRelicsSubscribers.add((EditRelicsSubscriber) subscriber);
        }
        if (subscriber instanceof RenderSubscriber) {
            renderSubscribers.add((RenderSubscriber) subscriber);
        }
        if (subscriber instanceof SetUnlocksSubscriber) {
            setUnlocksSubscribers.add((SetUnlocksSubscriber) subscriber);
        }
        if (subscriber instanceof ModelRenderSubscriber) {
            modelRenderSubscribers.add((ModelRenderSubscriber) subscriber);
        }
        if (subscriber instanceof PreRenderSubscriber) {
            preRenderSubscribers.add((PreRenderSubscriber) subscriber);
        }
        if (subscriber instanceof StartGameSubscriber) {
            startGameSubscribers.add((StartGameSubscriber) subscriber);
        }
        if (subscriber instanceof StartActSubscriber) {
            startActSubscribers.add((StartActSubscriber) subscriber);
        }
        if (subscriber instanceof PostBattleSubscriber) {
            postBattleSubscribers.add((PostBattleSubscriber) subscriber);
        }
        if (subscriber instanceof PostCreateStartingDeckSubscriber) {
            postCreateStartingDeckSubscribers.add((PostCreateStartingDeckSubscriber) subscriber);
        }
        if (subscriber instanceof OnCardUseSubscriber) {
            onCardUseSubscribers.add((OnCardUseSubscriber) subscriber);
        }
    }

    private static void unsubscribeIfInstance(ISubscriber subscriber) {
        if (subscriber instanceof PostInitializeSubscriber) {
            postInitializeSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PostRenderSubscriber) {
            postRenderSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PostUpdateSubscriber) {
            postUpdateSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PreUpdateSubscriber) {
            preUpdateSubscribers.remove(subscriber);
        }
        if (subscriber instanceof EditCardsSubscriber) {
            editCardsSubscribers.remove(subscriber);
        }
        if (subscriber instanceof EditStringsSubscriber) {
            editStringsSubscribers.remove(subscriber);
        }
        if (subscriber instanceof EditKeywordsSubscriber) {
            editKeywordsSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PostEnergyRechargeSubscriber) {
            postEnergyRechargeSubscribers.remove(subscriber);
        }
        if (subscriber instanceof EditCharactersSubscriber) {
            editCharactersSubscribers.remove(subscriber);
        }
        if (subscriber instanceof EditRelicsSubscriber) {
            editRelicsSubscribers.remove(subscriber);
        }
        if (subscriber instanceof RenderSubscriber) {
            renderSubscribers.remove(subscriber);
        }
        if (subscriber instanceof SetUnlocksSubscriber) {
            setUnlocksSubscribers.remove(subscriber);
        }
        if (subscriber instanceof ModelRenderSubscriber) {
            modelRenderSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PreRenderSubscriber) {
            preRenderSubscribers.remove(subscriber);
        }
        if (subscriber instanceof StartGameSubscriber) {
            startGameSubscribers.remove(subscriber);
        }
        if (subscriber instanceof StartActSubscriber) {
            startActSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PostBattleSubscriber) {
            postBattleSubscribers.remove(subscriber);
        }
        if (subscriber instanceof PostCreateStartingDeckSubscriber) {
            postCreateStartingDeckSubscribers.remove(subscriber);
        }
        if (subscriber instanceof OnCardUseSubscriber) {
            onCardUseSubscribers.remove(subscriber);
        }
    }

    public static void unsubscribe(ISubscriber sub, Class<? extends ISubscriber> removalClass) {
        if (removalClass.equals(PostEnergyRechargeSubscriber.class)) {
            postEnergyRechargeSubscribers.remove(sub);
        } else if (removalClass.equals(PostInitializeSubscriber.class)) {
            postInitializeSubscribers.remove(sub);
        } else if (removalClass.equals(RenderSubscriber.class)) {
            renderSubscribers.remove(sub);
        } else if (removalClass.equals(PreRenderSubscriber.class)) {
            preRenderSubscribers.remove(sub);
        } else if (removalClass.equals(PostRenderSubscriber.class)) {
            postRenderSubscribers.remove(sub);
        } else if (removalClass.equals(ModelRenderSubscriber.class)) {
            modelRenderSubscribers.remove(sub);
        } else if (removalClass.equals(PreUpdateSubscriber.class)) {
            preUpdateSubscribers.remove(sub);
        } else if (removalClass.equals(PostUpdateSubscriber.class)) {
            postUpdateSubscribers.remove(sub);
        } else if (removalClass.equals(EditCardsSubscriber.class)) {
            editCardsSubscribers.remove(sub);
        } else if (removalClass.equals(EditRelicsSubscriber.class)) {
            editRelicsSubscribers.remove(sub);
        } else if (removalClass.equals(EditCharactersSubscriber.class)) {
            editCharactersSubscribers.remove(sub);
        } else if (removalClass.equals(EditStringsSubscriber.class)) {
            editStringsSubscribers.remove(sub);
        } else if (removalClass.equals(EditKeywordsSubscriber.class)) {
            editKeywordsSubscribers.remove(sub);
        } else if (removalClass.equals(SetUnlocksSubscriber.class)) {
            setUnlocksSubscribers.remove(sub);
        } else if (removalClass.equals(StartGameSubscriber.class)) {
            startGameSubscribers.remove(sub);
        } else if (removalClass.equals(StartActSubscriber.class)) {
            startActSubscribers.remove(sub);
        } else if (removalClass.equals(PostBattleSubscriber.class)) {
            postBattleSubscribers.remove(sub);
        } else if (removalClass.equals(PostCreateStartingDeckSubscriber.class)) {
            postCreateStartingDeckSubscribers.remove(sub);
        } else if (removalClass.equals(OnCardUseSubscriber.class)) {
            onCardUseSubscribers.remove(sub);
        }
    }

    public static void unsubscribeLater(ISubscriber sub) {
        toRemove.add(sub);
    }

    public static ArrayList<String> listAllRelicIDs() {
        Set<String> relicIDs = new HashSet<>();
        HashMap<String, AbstractRelic> sharedRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "sharedRelics");
        if (sharedRelics != null) {
            relicIDs.addAll(sharedRelics.keySet());
        }

        HashMap<String, AbstractRelic> redRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "redRelics");
        if (redRelics != null) {
            relicIDs.addAll(redRelics.keySet());
        }

        HashMap<String, AbstractRelic> greenRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "greenRelics");
        if (greenRelics != null) {
            relicIDs.addAll(greenRelics.keySet());
        }

        HashMap<String, AbstractRelic> blueRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "blueRelics");
        if (blueRelics != null) {
            relicIDs.addAll(blueRelics.keySet());
        }

        HashMap<String, AbstractRelic> purpleRelics = ReflectionHacks.getPrivateStatic(RelicLibrary.class, "purpleRelics");
        if (purpleRelics != null) {
            relicIDs.addAll(purpleRelics.keySet());
        }

        if (getAllCustomRelics() != null) {

            for (Map<String, AbstractRelic> o : getAllCustomRelics().values()) {
                if (o != null) {
                    relicIDs.addAll(o.keySet());
                }
            }
        }

        return new ArrayList<>(relicIDs);
    }

    public static Map<AbstractCard.CardColor, Map<String, AbstractRelic>> getAllCustomRelics() {
        return customRelicPools;
    }

    public static void receivePostInitialize() {
        logger.info("receivePostInitialize...");
        for (PostInitializeSubscriber subscriber : postInitializeSubscribers) {
            subscriber.receivePostInitialize();
        }
        initializeEncounters();
        animationCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        animationCamera.near = 1.0F;
        animationCamera.far = 300.0F;
        animationCamera.position.z = 200.0F;
        animationCamera.update();
        batch = new ModelBatch();
        animationEnvironment = new Environment();
        animationEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0F, 1.0F, 1.0F, 1.0F));
        animationBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    public static void receivePostRender(SpriteBatch sb) {
        for (PostRenderSubscriber subscriber : postRenderSubscribers) {
            subscriber.receivePostRender(sb);
        }
    }

    public static void receivePostUpdate() {
        for (PostUpdateSubscriber subscriber : postUpdateSubscribers) {
            subscriber.receivePostUpdate();
        }
        if (!actionQueue.isEmpty()) {
            AbstractGameAction action = actionQueue.get(0);
            action.update();
            if (action.isDone) {
                actionQueue.remove(0);
            }
        }
    }

    public static void receivePreUpdate() {
        for (PreUpdateSubscriber subscriber : preUpdateSubscribers) {
            subscriber.receivePreUpdate();
        }
    }

    public static void receiveEditCards() {
        logger.info("Begin edit cards...");
        for (EditCardsSubscriber subscriber : editCardsSubscribers) {
            subscriber.receiveEditCards();
        }
        for (CustomCard card : cardsToAdd) {
            CardLibrary.add(card);
        }
        initializeUnderscoreCardIDs();
        cardsToAdd.clear();
        logger.info("Done editing cards.");
    }

    public static void receiveEditStrings() {
        logger.info("Begin edit strings...");
        for (EditStringsSubscriber subscriber : editStringsSubscribers) {
            subscriber.receiveEditStrings();
        }
        logger.info("Done edit strings.");
    }

    public static void receiveEditKeywords() {
        logger.info("Begin edit keywords...");
        for (EditKeywordsSubscriber subscriber : editKeywordsSubscribers) {
            subscriber.receiveEditKeywords();
        }
        logger.info("Done edit keywords.");
    }

    public static void receivePostEnergyRecharge() {
        for (PostEnergyRechargeSubscriber subscriber : postEnergyRechargeSubscribers) {
            subscriber.receivePostEnergyRecharge();
        }
    }

    public static void receiveEditCharacters() {
        logger.info("Begin edit characters...");
        for (EditCharactersSubscriber subscriber : editCharactersSubscribers) {
            subscriber.receiveEditCharacters();
        }
        logger.info("Done edit characters.");
    }

    public static void receiveEditRelics() {
        logger.info("Begin edit relics...");
        for (EditRelicsSubscriber subscriber : editRelicsSubscribers) {
            subscriber.receiveEditRelics();
        }
        logger.info("Done edit relics.");
    }

    public static void receiveSetUnlocks() {
        logger.info("Begin set unlocks...");
        for (SetUnlocksSubscriber subscriber : setUnlocksSubscribers) {
            subscriber.receiveSetUnlocks();
        }
        for (Map.Entry<AbstractPlayer.PlayerClass, CustomUnlock> e : unlockBundleMap.entrySet()) {
            CustomUnlock customUnlock = e.getValue();
            for (int i=0;i<5;i++) {
                List<AbstractUnlock> bundle = customUnlock.getUnlockBundle(i);
                for (AbstractUnlock unlock : bundle) {
                    if (unlock.type == AbstractUnlock.UnlockType.CARD) {
                        UnlockTracker.addCard(unlock.key);
                    } else if (unlock.type == AbstractUnlock.UnlockType.RELIC) {
                        UnlockTracker.addRelic(unlock.key);
                    }
                }
            }
        }
        logger.info("Done set unlocks.");
    }

    public static void receiveRender(SpriteBatch sb) {
        for (RenderSubscriber subscriber : renderSubscribers) {
            subscriber.receiveRender(sb);
        }
    }

    public static void publishAnimationRender(SpriteBatch sb) {
        if (modelRenderSubscribers.size() > 0) {
            sb.end();
            CardCrawlGame.psb.begin();
            CardCrawlGame.psb.setBlendFunction(1, 771);
            CardCrawlGame.psb.draw(animationTextureRegion, 0.0F, 0.0F);
            CardCrawlGame.psb.setBlendFunction(770, 771);
            CardCrawlGame.psb.end();
            sb.begin();
        }
    }

    public static void publishPreRender(OrthographicCamera camera) {
        for (PreRenderSubscriber sub : preRenderSubscribers) {
            sub.receiveCameraRender(camera);
        }

        if (modelRenderSubscribers.size() > 0) {

            animationBuffer.begin();
            Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            Gdx.gl.glClear(16384);
            batch.begin(animationCamera);

            for (ModelRenderSubscriber sub : modelRenderSubscribers) {
                sub.receiveModelRender(batch, animationEnvironment);
            }

            batch.end();
            animationBuffer.end();
            animationTexture = animationBuffer.getColorBufferTexture();
            animationTextureRegion = new TextureRegion(animationTexture);
            animationTextureRegion.flip(false, true);
        }

        unsubscribeLaterHelper(PreRenderSubscriber.class);
        unsubscribeLaterHelper(ModelRenderSubscriber.class);
    }

    private static void unsubscribeLaterHelper(Class<? extends ISubscriber> removalClass) {
        for (ISubscriber sub : toRemove) {
            if (removalClass.isInstance(sub)) {
                unsubscribe(sub, removalClass);
            }
        }

    }

    public static <T> void addSaveField(String key, CustomSavableRaw saveField) {
        customSaveFields.put(key, saveField);
    }

    public static Map<String, CustomSavableRaw> getSaveFields() {
        return customSaveFields;
    }

    public static void addCharacter(AbstractPlayer character, String selectButtonPath, String portraitPath, AbstractPlayer.PlayerClass characterID, String customModeButtonPath) {
       if(CardCrawlGame.characterManager==null){
           CardCrawlGame.characterManager = new CharacterManager();
       }
        CardCrawlGame.characterManager.getAllCharacters().add(character);
        playerSelectButtonMap.put(characterID, selectButtonPath);
        customModeCharacterButtonMap.put(characterID, customModeButtonPath);
        playerPortraitMap.put(characterID, portraitPath);
    }

    public static void addCharacter(AbstractPlayer character, String selectButtonPath, String portraitPath, AbstractPlayer.PlayerClass characterID) {
        addCharacter(character, selectButtonPath, portraitPath, characterID, null);
    }

    public static ArrayList<CharacterOption> generateCharacterOptions(String modId) throws ReflectionException, NoSuchFieldException, IllegalAccessException {
        ArrayList<CharacterOption> options = new ArrayList<>();
        for (AbstractPlayer character : getModdedCharacters()) {
            CharacterOption option = new CharacterOption(character.getLocalizedCharacterName(), CardCrawlGame.characterManager.recreateCharacter(character.chosenClass),
                    AssetLoader.getTexture(modId, playerSelectButtonMap.get(character.chosenClass)),
                    AssetLoader.getTexture(modId, playerPortraitMap.get(character.chosenClass)));
            if(AndroidLauncher.getField1(option.getClass(), "buttonImg", option)!=null) {
                options.add(option);
            }
        }
        Collections.sort(options, new Comparator<CharacterOption>() {
            @Override
            public int compare(CharacterOption o1, CharacterOption o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return options;
    }

    public static List<AbstractPlayer> getModdedCharacters() {
        return CardCrawlGame.characterManager.getAllCharacters().subList(4, CardCrawlGame.characterManager.getAllCharacters().size());
    }

    public static String getCustomModePlayerButton(AbstractPlayer.PlayerClass playerClass) {
        return customModeCharacterButtonMap.get(playerClass);
    }

    public static ArrayList<String> getUnlockCards(AbstractPlayer.PlayerClass c) {
        return unlockCards.get(c);
    }

    public static String colorString(String input, String colorValue) {
        StringBuilder retVal = new StringBuilder();
        Scanner s = new Scanner(input);

        while(s.hasNext()) {
            retVal.append("[").append(colorValue).append("]").append(s.next());
            retVal.append(" ");
        }

        s.close();
        return retVal.toString().trim();
    }

    public static String getPlayerButton(AbstractPlayer.PlayerClass chosenClass) {
        return playerSelectButtonMap.get(chosenClass);
    }

    public static String getPlayerPortrait(AbstractPlayer.PlayerClass chosenClass) {
        return playerPortraitMap.get(chosenClass);
    }

    public static void incrementCardCount(AbstractCard.CardColor color) {
        Integer count = colorCardCountMap.get(color);

        if (count != null) {
            colorCardCountMap.put(color, count + 1);
        } else {
            colorCardCountMap.put(color, 0);
        }
    }

    public static void incrementSeenCardCount(AbstractCard.CardColor color) {
        Integer count = colorCardSeenCountMap.get(color);
        if (count != null) {
            colorCardSeenCountMap.put(color, count + 1);
        } else {
            colorCardSeenCountMap.put(color, 0);
        }
    }

    public static int getCardCount(AbstractCard.CardColor color) {
        Integer count = colorCardCountMap.get(color);
        return count == null ? -1 : count;
    }

    public static int getSeenCardCount(AbstractCard.CardColor color) {
        Integer count = colorCardSeenCountMap.get(color);
        return count == null ? -1 : count;
    }

    public static void publishStartGame() {
        logger.info("publishStartGame");

        for (StartGameSubscriber sub : startGameSubscribers) {
            sub.receiveStartGame();
        }

        unsubscribeLaterHelper(StartGameSubscriber.class);
    }

    public static void publishStartAct() {
        logger.info("publishStartAct");
        for (StartActSubscriber sub : startActSubscribers) {
            sub.receiveStartAct();
        }
        unsubscribeLaterHelper(StartActSubscriber.class);
    }

    public static void publishPostBattle(AbstractRoom battleRoom) {
        logger.info("publish post combat");

        for (PostBattleSubscriber sub : postBattleSubscribers) {
            sub.receivePostBattle(battleRoom);
        }
        unsubscribeLaterHelper(PostBattleSubscriber.class);
    }

    public static void publishPostCreateStartingDeck(AbstractPlayer.PlayerClass chosenClass, CardGroup masterDeck) {
        logger.info("postCreateStartingDeck for: " + chosenClass);

        for (PostCreateStartingDeckSubscriber sub : postCreateStartingDeckSubscribers) {
            logger.info("postCreateStartingDeck modifying starting deck for: " + sub);
            sub.receivePostCreateStartingDeck(chosenClass, masterDeck);
        }

        StringBuilder logString = new StringBuilder("postCreateStartingDeck adding [ ");
        for (AbstractCard card : masterDeck.group) {
            logString.append(card.cardID).append(" ");
        }
        logString.append("]");
        logger.info(logString.toString());

        unsubscribeLaterHelper(PostCreateStartingDeckSubscriber.class);
    }

    public static void publishOnCardUse(AbstractCard card) {
        logger.info("publish on card use: " + ((card == null) ? "null" : card.cardID));

        for (OnCardUseSubscriber sub : onCardUseSubscribers) {
            sub.receiveCardUsed(card);
        }
        unsubscribeLaterHelper(OnCardUseSubscriber.class);
    }

    public static class BossInfo {
        public final String id;
        private final String bossMap;
        private final String bossMapOutline;

        private BossInfo(String id, String mapIcon, String mapIconOutline) {
            this.id = id;
            this.bossMap = mapIcon;
            this.bossMapOutline = mapIconOutline;
        }

        public Texture loadBossMap() {
            return ImageMaster.loadImage(this.bossMap);
        }

        public Texture loadBossMapOutline() {
            return ImageMaster.loadImage(this.bossMapOutline);
        }
    }

    public interface GetMonster {
        AbstractMonster get();
    }

    public interface GetMonsterGroup {
        MonsterGroup get();
    }
}
