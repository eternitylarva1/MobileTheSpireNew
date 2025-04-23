package com.megacrit.cardcrawl.core;

import android.os.Environment;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.megacrit.cardcrawl.android.SpireAndroidLogger;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.android.mods.ModSaves;
import com.megacrit.cardcrawl.android.mods.abstracts.CustomSavableRaw;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.integrations.DistributorFactory;
import com.megacrit.cardcrawl.integrations.PublisherIntegration;
import com.megacrit.cardcrawl.integrations.SteelSeries;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.metrics.BotDataUploader;
import com.megacrit.cardcrawl.metrics.MetricData;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamUtils;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.daily.TimeHelper;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.controller.SteamInputHandler;
import com.megacrit.cardcrawl.helpers.input.DevInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.splash.SplashScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.trials.AbstractTrial;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import de.robojumper.ststwitch.TwitchConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class CardCrawlGame implements ApplicationListener {
    public static String VERSION_NUM = "[V2.2] (12-22-2020)";
    public static String TRUE_VERSION_NUM = "2020-12-22";
    private OrthographicCamera camera;
    public static FitViewport viewport;
    public static PolygonSpriteBatch psb;
    private SpriteBatch sb;
    public static GameCursor cursor;
    public static boolean isPopupOpen = false;
    public static int popupMX;
    public static int popupMY;
    public static ScreenShake screenShake = new ScreenShake();
    public static AbstractDungeon dungeon;
    public static MainMenuScreen mainMenuScreen;
    public static SplashScreen splashScreen;
    public static DungeonTransitionScreen dungeonTransitionScreen;
    public static CancelButton cancelButton;
    public static MusicMaster music;
    public static SoundMaster sound;
    public static GameTips tips;
    public static SingleCardViewPopup cardPopup;
    public static SingleRelicViewPopup relicPopup;
    private FPSLogger fpsLogger = new FPSLogger();
    public boolean prevDebugKeyDown = false;
    public static String nextDungeon;
    public static CardCrawlGame.GameMode mode;
    public static boolean startOver;
    private static boolean queueCredits;
    public static boolean playCreditsBgm;
    public static boolean MUTE_IF_BG;
    public static AbstractPlayer.PlayerClass chosenCharacter;
    public static boolean loadingSave;
    public static SaveFile saveFile;
    public static Prefs saveSlotPref;
    public static Prefs playerPref;
    public static int saveSlot;
    public static String playerName;
    public static String alias;
    public static CharacterManager characterManager;
    public static int monstersSlain;
    public static int elites1Slain;
    public static int elites2Slain;
    public static int elites3Slain;
    public static int elitesModdedSlain;
    public static int champion;
    public static int perfect;
    public static boolean overkill;
    public static boolean combo;
    public static boolean cheater;
    public static int goldGained;
    public static int cardsPurged;
    public static int potionsBought;
    public static int mysteryMachine;
    public static float playtime;
    public static boolean stopClock;
    public static SkeletonRendererDebug debugRenderer;
    public static AbstractTrial trial;
    public static Scanner sControllerScanner;
    SteamInputHandler sInputHandler = null;
    Thread sInputThread = null;
    private SteamUtils clientUtils;
    private static Color screenColor;
    public static float screenTimer;
    public static float screenTime;
    private static boolean fadeIn;
    public static MetricData metricData;
    public static PublisherIntegration publisherIntegration;
    public static SteelSeries steelSeries;
    public static LocalizedStrings languagePack;
    private boolean displayCursor = true;
    public static boolean displayVersion;
    public static String preferenceDir;
    private static final SpireAndroidLogger logger;

    public CardCrawlGame(String prefDir) {
        preferenceDir = prefDir;
    }

    @Override
    public void create() {
        Settings.WIDTH = Gdx.graphics.getWidth();
        Settings.HEIGHT = Gdx.graphics.getHeight();
        java.io.File crashLog = new java.io.File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/SpireCrashLog.txt"));
        if (crashLog.exists()) {
            crashLog.delete();
        }

        if (Settings.isAlpha) {
            TRUE_VERSION_NUM = TRUE_VERSION_NUM + " ALPHA";
            VERSION_NUM = VERSION_NUM + " ALPHA";
        } else if (Settings.isBeta) {
            VERSION_NUM = VERSION_NUM + " BETA";
        }

        try {
            TwitchConfig.createConfig();
            //BuildSettings buildSettings = new BuildSettings(Gdx.files.internal("build.properties").reader());
            //logger.info("DistributorPlatform=" + buildSettings.getDistributor());
            logger.info("isModded=" + Settings.isModded);
            logger.info("isBeta=" + Settings.isBeta);
            publisherIntegration = DistributorFactory.getEnabledDistributor("wegame"); //避免显示排行榜，如果标记为steam平台就会显示排行榜
            this.saveMigration();
            saveSlotPref = SaveHelper.getPrefs("STSSaveSlots");
            saveSlot = saveSlotPref.getInteger("DEFAULT_SLOT", 0);
            playerPref = SaveHelper.getPrefs("STSPlayer");
            playerName = saveSlotPref.getString(SaveHelper.slotName("PROFILE_NAME", saveSlot), "");
            if (playerName.equals("")) {
                playerName = playerPref.getString("name", "");
            }

            alias = playerPref.getString("alias", "");
            if (alias.equals("")) {
                alias = generateRandomAlias();
                playerPref.putString("alias", alias);
                playerPref.flush();
            }
            Settings.initialize(false);
            BaseMod.initialize();
            Settings.isConsoleBuild = true;
            this.camera = new OrthographicCamera((float)Settings.WIDTH, (float)Settings.HEIGHT);
            if (Settings.VERT_LETTERBOX_AMT == 0 && Settings.HORIZ_LETTERBOX_AMT == 0) {
                this.camera.position.set(this.camera.viewportWidth / 2.0F, this.camera.viewportHeight / 2.0F, 0.0F);
                this.camera.update();
                viewport = new FitViewport((float)Settings.WIDTH, (float)Settings.HEIGHT, this.camera);
                viewport.apply();
            } else {
                this.camera.position.set((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, 0.0F);
                this.camera.update();
                viewport = new FitViewport((float)Settings.WIDTH, (float)(Settings.M_H - Settings.HEIGHT / 2), this.camera);
            }

            languagePack = new LocalizedStrings();
            cardPopup = new SingleCardViewPopup();
            relicPopup = new SingleRelicViewPopup();
            if (Settings.IS_FULLSCREEN) {
                this.resize(Settings.M_W, Settings.M_H);
            }

            //Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/blank.png")), 0, 0));
            this.sb = new SpriteBatch();
            psb = new PolygonSpriteBatch();
            music = new MusicMaster();
            sound = new SoundMaster();
            AbstractCreature.initialize();
            AbstractCard.initialize();

            GameDictionary.initialize();
            BaseMod.receiveEditKeywords();

            ImageMaster.initialize();
            AbstractPower.initialize();
            FontHelper.initialize();
            AbstractCard.initializeDynamicFrameWidths();
            UnlockTracker.initialize();
            CardLibrary.initialize();
            RelicLibrary.initialize();
            BaseMod.initializeUnderscoreRelicIDs();
            InputHelper.initialize();
            TipTracker.initialize();
            ModHelper.initialize();
            ShaderHelper.initializeShaders();
            steelSeries = new SteelSeries();
            cursor = new GameCursor();
            metricData = new MetricData();
            cancelButton = new CancelButton();
            tips = new GameTips();
            characterManager = new CharacterManager();
            UnlockTracker.retroactiveUnlock();
            splashScreen = new SplashScreen();
            mode = GameMode.SPLASH;
            CInputHelper.loadSettings();
            BaseMod.receivePostInitialize();
        } catch (Exception e) {
            logger.info("Exception occurred in CardCrawlGame create method!");
            e.printStackTrace();
            writeExceptionToFile(e);
            //ExceptionHandler.handleException(var2, logger);
            Gdx.app.exit();
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeExceptionToFile(Exception e) {
        java.io.File crashLog = new java.io.File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/SpireCrashLog.txt"));
        try {
            if (!crashLog.exists()) {
                crashLog.createNewFile();
            }
            OutputStream os = new FileOutputStream(crashLog);
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(e.toString() + "\n");
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                writer.write("\tat " + stackTraceElement.toString() + "\n");
            }
            Throwable cause = e.getCause();
            while (cause != null) {
                writer.write("Caused by: ");
                writer.write(cause.toString() + "\n");
                stackTraceElements = cause.getStackTrace();
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    writer.write("\tat " + stackTraceElement.toString() + "\n");
                }
                cause = cause.getCause();
            }
            writer.flush();
            writer.close();
            os.flush();
            os.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void reloadPrefs() {
        playerPref = SaveHelper.getPrefs("STSPlayer");
        alias = playerPref.getString("alias", "");
        if (alias.equals("")) {
            alias = generateRandomAlias();
            playerPref.putString("alias", alias);
        }

        music.fadeOutBGM();
        mainMenuScreen.fadeOutMusic();
        InputActionSet.prefs = SaveHelper.getPrefs("STSInputSettings");
        InputActionSet.load();
        CInputActionSet.prefs = SaveHelper.getPrefs("STSInputSettings_Controller");
        CInputActionSet.load();
        characterManager = new CharacterManager();
        Settings.initialize(true);
        UnlockTracker.initialize();
        CardLibrary.resetForReload();
        CardLibrary.initialize();
        RelicLibrary.resetForReload();
        RelicLibrary.initialize();
        TipTracker.initialize();
        logger.info("TEXTURE COUNT: " + Texture.getNumManagedTextures());
        screenColor.a = 0.0F;
        screenTime = 0.01F;
        screenTimer = 0.01F;
        fadeIn = false;
        startOver = true;
    }

    public void saveMigration() {
        if (!SaveHelper.saveExists()) {
            Preferences p = Gdx.app.getPreferences("STSPlayer");
            if (p.getString("name", "").equals("") && Gdx.app.getPreferences("STSDataVagabond").getLong("PLAYTIME") == 0L) {
                logger.info("New player, no migration.");
                return;
            }

            logger.info("Migrating Save...");
            this.migrateHelper("STSPlayer");
            this.migrateHelper("STSUnlocks");
            this.migrateHelper("STSUnlockProgress");
            this.migrateHelper("STSTips");
            this.migrateHelper("STSSound");
            this.migrateHelper("STSSeenRelics");
            this.migrateHelper("STSSeenCards");
            this.migrateHelper("STSSeenBosses");
            this.migrateHelper("STSGameplaySettings");
            this.migrateHelper("STSDataVagabond");
            this.migrateHelper("STSDataTheSilent");
            this.migrateHelper("STSAchievements");
            if (MathUtils.randomBoolean(0.5F)) {
                logger.warn("Save Migration");
            }
        } else {
            logger.info("No migration");
        }

    }

    public void migrateHelper(String file) {
        Preferences p = Gdx.app.getPreferences(file);
        Prefs p2 = SaveHelper.getPrefs(file);
        Map<String, ?> map = p.get();

        for (Map.Entry<String, ?> o : map.entrySet()) {
            p2.putString(o.getKey(), p.getString(o.getKey()));
        }

        p2.flush();
    }

    @Override
    public void render() {
        BaseMod.publishPreRender(camera);
        try {
            TimeHelper.update();
            if (Gdx.graphics.getRawDeltaTime() > 0.1F) {
                return;
            }

            CInputHelper.initializeIfAble();
            this.update();
            this.sb.setProjectionMatrix(this.camera.combined);
            psb.setProjectionMatrix(this.camera.combined);
            Gdx.gl.glClearColor(1, 1, 1, 1);
            if(!this.sb.isDrawing()) {
                this.sb.begin();
            }
            this.sb.setColor(Color.WHITE);
            switch(mode.name) {
                case "SPLASH":
                    splashScreen.render(this.sb);
                    break;
                case "CHAR_SELECT":
                    mainMenuScreen.render(this.sb);
                    break;
                case "GAMEPLAY":
                    if (dungeonTransitionScreen != null) {
                        dungeonTransitionScreen.render(this.sb);
                    } else if (dungeon != null) {
                        dungeon.render(this.sb);
                    }
                case "DUNGEON_TRANSITION":
                    break;
                default:
                    logger.info("Unknown Game Mode: " + mode.name);
            }

            DrawMaster.draw(this.sb);
            BaseMod.receiveRender(sb);
            if (cardPopup.isOpen) {
                cardPopup.render(this.sb);
            }

            if (relicPopup.isOpen) {
                relicPopup.render(this.sb);
            }

            TipHelper.render(this.sb);
            if (mode != GameMode.SPLASH) {
                this.renderBlackFadeScreen(this.sb);
                if (this.displayCursor) {
                    if (isPopupOpen) {
                        InputHelper.mX = popupMX;
                        InputHelper.mY = popupMY;
                    }

                    //cursor.render(this.sb);
                }
            }
            /*
            if (Settings.HORIZ_LETTERBOX_AMT != 0) {
                this.sb.setColor(Color.BLACK);
                this.sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)(-Settings.HORIZ_LETTERBOX_AMT));
                this.sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, (float)Settings.HEIGHT, (float)Settings.WIDTH, (float)Settings.HORIZ_LETTERBOX_AMT);
            } else if (Settings.VERT_LETTERBOX_AMT != 0) {
                this.sb.setColor(Color.BLACK);
                this.sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)(-Settings.VERT_LETTERBOX_AMT), (float)Settings.HEIGHT);
                this.sb.draw(ImageMaster.WHITE_SQUARE_IMG, (float)Settings.WIDTH, 0.0F, (float)Settings.VERT_LETTERBOX_AMT, (float)Settings.HEIGHT);
            }
*/
            BaseMod.receivePostRender(sb);
            this.sb.end();
        } catch (Exception e) {
            logger.info("Exception occurred in CardCrawlGame render method!");
            e.printStackTrace();
            writeExceptionToFile(e);
            //ExceptionHandler.handleException(var2, logger);
            Gdx.app.exit();
        }

    }

    private void renderBlackFadeScreen(SpriteBatch sb) {
        sb.setColor(screenColor);
        if (screenColor.a < 0.55F && !mainMenuScreen.bg.activated) {
            mainMenuScreen.bg.activated = true;
        }

        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
    }

    public void updateFade() {
        if (screenTimer != 0.0F) {
            screenTimer -= Gdx.graphics.getDeltaTime();
            if (screenTimer < 0.0F) {
                screenTimer = 0.0F;
            }

            if (fadeIn) {
                screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, 1.0F - screenTimer / screenTime);
            } else {
                screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, 1.0F - screenTimer / screenTime);
                if (startOver && screenTimer == 0.0F) {
                    if (AbstractDungeon.scene != null) {
                        AbstractDungeon.scene.fadeOutAmbiance();
                    }

                    long startTime = System.currentTimeMillis();
                    AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
                    AbstractDungeon.reset();
                    FontHelper.cardTitleFont.getData().setScale(1.0F);
                    AbstractRelic.relicPage = 0;
                    SeedPanel.textField = "";
                    ModHelper.setModsFalse();
                    SeedHelper.cachedSeed = null;
                    Settings.seed = null;
                    Settings.seedSet = false;
                    Settings.specialSeed = null;
                    Settings.isTrial = false;
                    Settings.isDailyRun = false;
                    Settings.isEndless = false;
                    Settings.isFinalActAvailable = false;
                    Settings.hasRubyKey = false;
                    Settings.hasEmeraldKey = false;
                    Settings.hasSapphireKey = false;
                    CustomModeScreen.finalActAvailable = false;
                    trial = null;
                    logger.info("Dungeon Reset: " + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
                    ShopScreen.resetPurgeCost();
                    tips.initialize();
                    metricData.clearData();
                    logger.info("Shop Screen Rest, Tips Initialize, Metric Data Clear: " + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
                    UnlockTracker.refresh();
                    logger.info("Unlock Tracker Refresh:  " + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
                    mainMenuScreen = new MainMenuScreen();
                    mainMenuScreen.bg.slideDownInstantly();
                    saveSlotPref.putFloat(SaveHelper.slotName("COMPLETION", saveSlot), UnlockTracker.getCompletionPercentage());
                    saveSlotPref.putLong(SaveHelper.slotName("PLAYTIME", saveSlot), UnlockTracker.getTotalPlaytime());
                    saveSlotPref.flush();
                    logger.info("New Main Menu Screen: " + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
                    CardHelper.clear();
                    mode = CardCrawlGame.GameMode.CHAR_SELECT;
                    nextDungeon = "Exordium";
                    dungeonTransitionScreen = new DungeonTransitionScreen("Exordium");
                    TipTracker.refresh();
                    logger.info("[GC] BEFORE: " + String.valueOf(SystemStats.getUsedMemory()));
                    System.gc();
                    logger.info("[GC] AFTER: " + String.valueOf(SystemStats.getUsedMemory()));
                    logger.info("New Transition Screen, Tip Tracker Refresh: " + (System.currentTimeMillis() - startTime) + "ms");
                    startTime = System.currentTimeMillis();
                    fadeIn(2.0F);
                    if (queueCredits) {
                        queueCredits = false;
                        mainMenuScreen.creditsScreen.open(playCreditsBgm);
                        mainMenuScreen.hideMenuButtons();
                    }
                }
            }
        }

    }

    public static void fadeIn(float duration) {
        screenColor.a = 1.0F;
        screenTime = duration;
        screenTimer = duration;
        fadeIn = true;
    }

    public static void fadeToBlack(float duration) {
        screenColor.a = 0.0F;
        screenTime = duration;
        screenTimer = duration;
        fadeIn = false;
    }

    public static void startOver() {
        startOver = true;
        fadeToBlack(2.0F);
    }

    public static void startOverButShowCredits() {
        startOver = true;
        queueCredits = true;
        doorUnlockScreenCheck();
        fadeToBlack(2.0F);
    }

    private static void doorUnlockScreenCheck() {
        DoorUnlockScreen.show = false;
        if (!Settings.isStandardRun()) {
            logger.info("[INFO] Non-Standard Run, no check for door.");
        } else {
            switch(AbstractDungeon.player.chosenClass.name()) {
                case "IRONCLAD":
                    if (!playerPref.getBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", false)) {
                        logger.info("[INFO] Ironclad Victory: Show Door.");
                        playerPref.putBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", true);
                        DoorUnlockScreen.show = true;
                    } else {
                        logger.info("[INFO] Ironclad Already Won: No Door.");
                    }
                    break;
                case "THE_SILENT":
                    if (!playerPref.getBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", false)) {
                        logger.info("[INFO] Silent Victory: Show Door.");
                        playerPref.putBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", true);
                        DoorUnlockScreen.show = true;
                    } else {
                        logger.info("[INFO] Silent Already Won: No Door.");
                    }
                    break;
                case "DEFECT":
                    if (!playerPref.getBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", false)) {
                        logger.info("[INFO] Defect Victory: Show Door.");
                        playerPref.putBoolean(AbstractDungeon.player.chosenClass.name() + "_WIN", true);
                        DoorUnlockScreen.show = true;
                    } else {
                        logger.info("[INFO] Defect Already Won: No Door.");
                    }
            }

            if (DoorUnlockScreen.show) {
                playerPref.flush();
            }

        }
    }

    public static void resetScoreVars() {
        monstersSlain = 0;
        elites1Slain = 0;
        elites2Slain = 0;
        elites3Slain = 0;
        if (dungeon != null) {
            AbstractDungeon.bossCount = 0;
        }

        champion = 0;
        perfect = 0;
        overkill = false;
        combo = false;
        goldGained = 0;
        cardsPurged = 0;
        potionsBought = 0;
        mysteryMachine = 0;
        playtime = 0.0F;
        stopClock = false;
    }

    public void update() {
        //cursor.update();
        BaseMod.receivePreUpdate();
        screenShake.update(viewport);
        if (mode != GameMode.SPLASH) {
            this.updateFade();
        }

        music.update();
        sound.update();
        if (steelSeries.isEnabled) {
            steelSeries.update();
        }

        if (Settings.isDebug) {
            if (DevInputActionSet.toggleCursor.isJustPressed()) {
                this.displayCursor = !this.displayCursor;
            } else if (DevInputActionSet.toggleVersion.isJustPressed()) {
                displayVersion = !displayVersion;
            }
        }

        if (this.sInputHandler != null) {
            this.processUpdate();
        } else if (CInputHelper.controller != null) {
            CInputHelper.updateFirst();
        }

        InputHelper.updateFirst();
        if (cardPopup.isOpen) {
            cardPopup.update();
        }

        if (relicPopup.isOpen) {
            relicPopup.update();
        }

        if (isPopupOpen) {
            popupMX = InputHelper.mX;
            popupMY = InputHelper.mY;
            InputHelper.mX = -9999;
            InputHelper.mY = -9999;
        }

        switch(mode.name) {
            case "SPLASH":
                splashScreen.update();
                if (splashScreen.isDone) {
                    mode = CardCrawlGame.GameMode.CHAR_SELECT;
                    splashScreen = null;
                    mainMenuScreen = new MainMenuScreen();
                }
                break;
            case "CHAR_SELECT":
                if (mainMenuScreen == null) mainMenuScreen = new MainMenuScreen();
                mainMenuScreen.update();
                if (mainMenuScreen.fadedOut) {
                    AbstractDungeon.pathX = new ArrayList<>();
                    AbstractDungeon.pathY = new ArrayList<>();
                    if (trial == null && Settings.specialSeed != null) {
                        trial = TrialHelper.getTrialForSeed(SeedHelper.getString(Settings.specialSeed));
                    }

                    if (loadingSave) {
                        ModHelper.setModsFalse();
                        AbstractDungeon.player = createCharacter(chosenCharacter);
                        this.loadPlayerSave(AbstractDungeon.player);
                    } else {
                        Settings.setFinalActAvailability();
                        logger.info("FINAL ACT AVAILABLE: " + Settings.isFinalActAvailable);
                        if (trial != null) {
                            Settings.isTrial = true;
                            Settings.isDailyRun = false;
                            this.setupTrialMods(trial, chosenCharacter);
                            this.setupTrialPlayer(trial);
                        } else {
                            if (Settings.isDailyRun) {
                                AbstractDungeon.ascensionLevel = 0;
                                AbstractDungeon.isAscensionMode = false;
                            }

                            AbstractDungeon.player = createCharacter(chosenCharacter);
                            Iterator var1 = AbstractDungeon.player.relics.iterator();

                            while(var1.hasNext()) {
                                AbstractRelic r = (AbstractRelic)var1.next();
                                r.updateDescription(AbstractDungeon.player.chosenClass);
                                r.onEquip();
                            }

                            var1 = AbstractDungeon.player.masterDeck.group.iterator();

                            while(var1.hasNext()) {
                                AbstractCard c = (AbstractCard)var1.next();
                                if (c.rarity != AbstractCard.CardRarity.BASIC) {
                                    CardHelper.obtain(c.cardID, c.rarity, c.color);
                                }
                            }
                        }
                    }

                    mode = CardCrawlGame.GameMode.GAMEPLAY;
                    nextDungeon = "Exordium";
                    dungeonTransitionScreen = new DungeonTransitionScreen("Exordium");
                    if (loadingSave) {
                        dungeonTransitionScreen.isComplete = true;
                    } else {
                        monstersSlain = 0;
                        elites1Slain = 0;
                        elites2Slain = 0;
                        elites3Slain = 0;
                    }
                }
                break;
            case "GAMEPLAY":
                if (dungeonTransitionScreen != null) {
                    dungeonTransitionScreen.update();
                    if (dungeonTransitionScreen.isComplete) {
                        dungeonTransitionScreen = null;
                        if (loadingSave) {
                            this.getDungeon(saveFile.level_name, AbstractDungeon.player, saveFile);
                            this.loadPostCombat(saveFile);
                            if (!saveFile.post_combat) {
                                loadingSave = false;
                            }
                        } else {
                            this.getDungeon(nextDungeon, AbstractDungeon.player);
                            if (!nextDungeon.equals("Exordium") || Settings.isShowBuild || !TipTracker.tips.get("NEOW_SKIP")) {
                                AbstractDungeon.dungeonMapScreen.open(true);
                                TipTracker.neverShowAgain("NEOW_SKIP");
                            }
                        }
                    }
                } else if (dungeon != null) {
                    dungeon.update();
                } else {
                    logger.info("Eh-?");
                }

                if (dungeon != null && AbstractDungeon.isDungeonBeaten && AbstractDungeon.fadeColor.a == 1.0F) {
                    dungeon = null;
                    AbstractDungeon.scene.fadeOutAmbiance();
                    dungeonTransitionScreen = new DungeonTransitionScreen(nextDungeon);
                }
            case "DUNGEON_TRANSITION":
                break;
            default:
                logger.info("Unknown Game Mode: " + mode.name);
        }

        this.updateDebugSwitch();
        BaseMod.receivePostUpdate();
        InputHelper.updateLast();
        if (CInputHelper.controller != null) {
            CInputHelper.updateLast();
        }

        if (Settings.isInfo) {
            this.fpsLogger.log();
        }
    }

    private void processUpdate() {
        if (SteamInputHandler.setHandle != null) {
            for(int i = 0; i < SteamInputHandler.numControllers; ++i) {
                SteamControllerHandle handle = SteamInputHandler.controllerHandles[i];
                SteamInputHandler.controller.activateActionSet(handle, SteamInputHandler.setHandle);
                if (SteamInputHandler.digitalActionHandle != null) {
                    SteamInputHandler.controller.getDigitalActionData(handle, SteamInputHandler.digitalActionHandle, SteamInputHandler.digitalActionData);
                    if (SteamInputHandler.digitalActionData.getActive() && SteamInputHandler.digitalActionData.getState()) {
                        logger.info("  digital action: " + SteamNativeHandle.getNativeHandle(SteamInputHandler.digitalActionHandle));
                    }
                }

                if (SteamInputHandler.analogActionHandle != null) {
                    SteamInputHandler.controller.getAnalogActionData(handle, SteamInputHandler.analogActionHandle, SteamInputHandler.analogActionData);
                    if (SteamInputHandler.analogActionData.getActive()) {
                        float x = SteamInputHandler.analogActionData.getX();
                        float y = SteamInputHandler.analogActionData.getY();
                        SteamController.SourceMode mode = SteamInputHandler.analogActionData.getMode();
                        if (Math.abs(x) > 1.0E-4F && Math.abs(y) > 0.001F) {
                            logger.info("  analog action: " + SteamInputHandler.analogActionData.getX() + ", " + SteamInputHandler.analogActionData.getY() + ", " + mode.name());
                        }
                    }
                }
            }

        }
    }

    private void setupTrialMods(AbstractTrial trial, AbstractPlayer.PlayerClass chosenClass) {
        if (trial.useRandomDailyMods()) {
            long sourceTime = System.nanoTime();
            Random rng = new Random(sourceTime);
            Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
            ModHelper.setTodaysMods(Settings.seed, chosenClass);
        } else if (trial.dailyModIDs() != null) {
            ModHelper.setMods(trial.dailyModIDs());
            ModHelper.clearNulls();
        }

    }

    private void setupTrialPlayer(AbstractTrial trial) {
        AbstractDungeon.player = trial.setupPlayer(createCharacter(chosenCharacter));
        if (!trial.keepStarterRelic()) {
            AbstractDungeon.player.relics.clear();
        }

        Iterator var2 = trial.extraStartingRelicIDs().iterator();

        String cardID;
        while(var2.hasNext()) {
            cardID = (String)var2.next();
            AbstractRelic relic = RelicLibrary.getRelic(cardID);
            relic.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), false);
            AbstractDungeon.relicsToRemoveOnStart.add(relic.relicId);
        }

        var2 = AbstractDungeon.player.relics.iterator();

        while(var2.hasNext()) {
            AbstractRelic r = (AbstractRelic)var2.next();
            r.updateDescription(AbstractDungeon.player.chosenClass);
            r.onEquip();
        }

        if (!trial.keepsStarterCards()) {
            AbstractDungeon.player.masterDeck.clear();
        }

        var2 = trial.extraStartingCardIDs().iterator();

        while(var2.hasNext()) {
            cardID = (String)var2.next();
            AbstractDungeon.player.masterDeck.addToTop(CardLibrary.getCard(cardID).makeCopy());
        }

    }

    private void loadPostCombat(SaveFile saveFile) {
        if (saveFile.post_combat) {
            AbstractDungeon.getCurrRoom().isBattleOver = true;
            AbstractDungeon.overlayMenu.hideCombatPanels();
            AbstractDungeon.loading_post_combat = true;
            AbstractDungeon.getCurrRoom().smoked = saveFile.smoked;
            AbstractDungeon.getCurrRoom().mugged = saveFile.mugged;
            if (AbstractDungeon.getCurrRoom().event != null) {
                AbstractDungeon.getCurrRoom().event.postCombatLoad();
            }

            if (AbstractDungeon.getCurrRoom().monsters != null) {
                AbstractDungeon.getCurrRoom().monsters.monsters.clear();
                AbstractDungeon.actionManager.actions.clear();
            }

            if (!saveFile.smoked) {

                for (RewardSave i : saveFile.combat_rewards) {
                    String var4 = i.type;
                    byte var5 = -1;
                    switch (var4.hashCode()) {
                        case -1929101933:
                            if (var4.equals("POTION")) {
                                var5 = 3;
                            }
                            break;
                        case -866293372:
                            if (var4.equals("EMERALD_KEY")) {
                                var5 = 6;
                            }
                            break;
                        case -759508872:
                            if (var4.equals("STOLEN_GOLD")) {
                                var5 = 4;
                            }
                            break;
                        case -706635454:
                            if (var4.equals("SAPPHIRE_KEY")) {
                                var5 = 5;
                            }
                            break;
                        case 2061072:
                            if (var4.equals("CARD")) {
                                var5 = 0;
                            }
                            break;
                        case 2193504:
                            if (var4.equals("GOLD")) {
                                var5 = 1;
                            }
                            break;
                        case 77859667:
                            if (var4.equals("RELIC")) {
                                var5 = 2;
                            }
                    }

                    switch (var5) {
                        case 0:
                            break;
                        case 1:
                            AbstractDungeon.getCurrRoom().addGoldToRewards(i.amount);
                            break;
                        case 2:
                            AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic(i.id).makeCopy());
                            break;
                        case 3:
                            AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion(i.id));
                            break;
                        case 4:
                            AbstractDungeon.getCurrRoom().addStolenGoldToRewards(i.amount);
                            break;
                        case 5:
                            AbstractDungeon.getCurrRoom().addSapphireKey(AbstractDungeon.getCurrRoom().rewards.get(AbstractDungeon.getCurrRoom().rewards.size() - 1));
                            break;
                        case 6:
                            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.getCurrRoom().rewards.get(AbstractDungeon.getCurrRoom().rewards.size() - 1), RewardItem.RewardType.EMERALD_KEY));
                            break;
                        default:
                            logger.info("Loading unknown type: " + i.type);
                    }
                }
            }

            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                AbstractDungeon.scene.fadeInAmbiance();
                music.silenceTempBgmInstantly();
                music.silenceBGMInstantly();
                AbstractMonster.playBossStinger();
            } else if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
                AbstractDungeon.scene.fadeInAmbiance();
                music.fadeOutTempBGM();
            }

            saveFile.post_combat = false;
        }

    }

    private void loadPlayerSave(AbstractPlayer p) {
        saveFile = SaveAndContinue.loadSaveFile(p.chosenClass);
        AbstractDungeon.loading_post_combat = false;
        Settings.seed = saveFile.seed;
        Settings.isFinalActAvailable = saveFile.is_final_act_on;
        Settings.hasRubyKey = saveFile.has_ruby_key;
        Settings.hasEmeraldKey = saveFile.has_emerald_key;
        Settings.hasSapphireKey = saveFile.has_sapphire_key;
        Settings.isDailyRun = saveFile.is_daily;
        if (Settings.isDailyRun) {
            Settings.dailyDate = saveFile.daily_date;
        }

        Settings.specialSeed = saveFile.special_seed;
        Settings.seedSet = saveFile.seed_set;
        Settings.isTrial = saveFile.is_trial;
        if (Settings.isTrial) {
            ModHelper.setTodaysMods(Settings.seed, AbstractDungeon.player.chosenClass);
            AbstractPlayer.customMods = saveFile.custom_mods;
        } else if (Settings.isDailyRun) {
            ModHelper.setTodaysMods(Settings.specialSeed, AbstractDungeon.player.chosenClass);
        }

        AbstractPlayer.customMods = saveFile.custom_mods;
        if (AbstractPlayer.customMods == null) {
            AbstractPlayer.customMods = new ArrayList<>();
        }

        p.currentHealth = saveFile.current_health;
        p.maxHealth = saveFile.max_health;
        p.gold = saveFile.gold;
        p.displayGold = p.gold;
        p.masterHandSize = saveFile.hand_size;
        p.potionSlots = saveFile.potion_slots;
        if (p.potionSlots == 0) {
            p.potionSlots = 3;
        }

        p.potions.clear();

        int index;
        for(index = 0; index < p.potionSlots; ++index) {
            p.potions.add(new PotionSlot(index));
        }

        p.masterMaxOrbs = saveFile.max_orbs;
        p.energy = new EnergyManager(saveFile.red + saveFile.green + saveFile.blue);
        monstersSlain = saveFile.monsters_killed;
        elites1Slain = saveFile.elites1_killed;
        elites2Slain = saveFile.elites2_killed;
        elites3Slain = saveFile.elites3_killed;
        goldGained = saveFile.gold_gained;
        champion = saveFile.champions;
        perfect = saveFile.perfect;
        combo = saveFile.combo;
        overkill = saveFile.overkill;
        mysteryMachine = saveFile.mystery_machine;
        playtime = (float)saveFile.play_time;
        AbstractDungeon.ascensionLevel = saveFile.ascension_level;
        AbstractDungeon.isAscensionMode = saveFile.is_ascension_mode;
        p.masterDeck.clear();

        for (CardSave s : saveFile.cards) {
            logger.info(s.id + ", " + s.upgrades);
            p.masterDeck.addToTop(CardLibrary.getCopy(s.id, s.upgrades, s.misc));
        }

        Settings.isEndless = saveFile.is_endless_mode;
        index = 0;
        p.blights.clear();
        String s;
        Iterator var9;
        if (saveFile.blights != null) {
            for(var9 = saveFile.blights.iterator(); var9.hasNext(); ++index) {
                s = (String)var9.next();
                AbstractBlight blight = BlightHelper.getBlight(s);
                if (blight != null) {
                    int incrementAmount = saveFile.endless_increments.get(index);

                    for(int i = 0; i < incrementAmount; ++i) {
                        blight.incrementUp();
                    }

                    blight.setIncrement(incrementAmount);
                    blight.instantObtain(AbstractDungeon.player, index, false);
                }
            }

            if (saveFile.blight_counters != null) {
                index = 0;

                for(var9 = saveFile.blight_counters.iterator(); var9.hasNext(); ++index) {
                    Integer i = (Integer)var9.next();
                    (p.blights.get(index)).setCounter(i);
                    (p.blights.get(index)).updateDescription(p.chosenClass);
                }
            }
        }

        p.relics.clear();
        index = 0;

        for(var9 = saveFile.relics.iterator(); var9.hasNext(); ++index) {
            s = (String)var9.next();
            AbstractRelic r = RelicLibrary.getRelic(s).makeCopy();
            r.instantObtain(p, index, false);
            if (index < saveFile.relic_counters.size()) {
                r.setCounter(saveFile.relic_counters.get(index));
            }

            r.updateDescription(p.chosenClass);
        }

        index = 0;

        for(var9 = saveFile.potions.iterator(); var9.hasNext(); ++index) {
            s = (String)var9.next();
            AbstractPotion potion = PotionHelper.getPotion(s);
            if (potion != null) {
                AbstractDungeon.player.obtainPotion(index, potion);
            }
        }

        AbstractCard tmpCard = null;
        Iterator var13;
        AbstractCard i;
        if (saveFile.bottled_flame != null) {
            var13 = AbstractDungeon.player.masterDeck.group.iterator();

            while(var13.hasNext()) {
                i = (AbstractCard)var13.next();
                if (i.cardID.equals(saveFile.bottled_flame)) {
                    tmpCard = i;
                    if (i.timesUpgraded == saveFile.bottled_flame_upgrade && i.misc == saveFile.bottled_flame_misc) {
                        break;
                    }
                }
            }

            if (tmpCard != null) {
                tmpCard.inBottleFlame = true;
                ((BottledFlame)AbstractDungeon.player.getRelic("Bottled Flame")).card = tmpCard;
                ((BottledFlame)AbstractDungeon.player.getRelic("Bottled Flame")).setDescriptionAfterLoading();
            }
        }

        tmpCard = null;
        if (saveFile.bottled_lightning != null) {
            var13 = AbstractDungeon.player.masterDeck.group.iterator();

            while(var13.hasNext()) {
                i = (AbstractCard)var13.next();
                if (i.cardID.equals(saveFile.bottled_lightning)) {
                    tmpCard = i;
                    if (i.timesUpgraded == saveFile.bottled_lightning_upgrade && i.misc == saveFile.bottled_lightning_misc) {
                        break;
                    }
                }
            }

            if (tmpCard != null) {
                tmpCard.inBottleLightning = true;
                ((BottledLightning)AbstractDungeon.player.getRelic("Bottled Lightning")).card = tmpCard;
                ((BottledLightning)AbstractDungeon.player.getRelic("Bottled Lightning")).setDescriptionAfterLoading();
            }
        }

        tmpCard = null;
        if (saveFile.bottled_tornado != null) {
            var13 = AbstractDungeon.player.masterDeck.group.iterator();

            while(var13.hasNext()) {
                i = (AbstractCard)var13.next();
                if (i.cardID.equals(saveFile.bottled_tornado)) {
                    tmpCard = i;
                    if (i.timesUpgraded == saveFile.bottled_tornado_upgrade && i.misc == saveFile.bottled_tornado_misc) {
                        break;
                    }
                }
            }

            if (tmpCard != null) {
                tmpCard.inBottleTornado = true;
                ((BottledTornado)AbstractDungeon.player.getRelic("Bottled Tornado")).card = tmpCard;
                ((BottledTornado)AbstractDungeon.player.getRelic("Bottled Tornado")).setDescriptionAfterLoading();
            }
        }

        if (saveFile.daily_mods != null && saveFile.daily_mods.size() > 0) {
            ModHelper.setMods(saveFile.daily_mods);
        }

        metricData.clearData();
        metricData.campfire_rested = saveFile.metric_campfire_rested;
        metricData.campfire_upgraded = saveFile.metric_campfire_upgraded;
        metricData.purchased_purges = saveFile.metric_purchased_purges;
        metricData.potions_floor_spawned = saveFile.metric_potions_floor_spawned;
        metricData.current_hp_per_floor = saveFile.metric_current_hp_per_floor;
        metricData.max_hp_per_floor = saveFile.metric_max_hp_per_floor;
        metricData.gold_per_floor = saveFile.metric_gold_per_floor;
        metricData.path_per_floor = saveFile.metric_path_per_floor;
        metricData.path_taken = saveFile.metric_path_taken;
        metricData.items_purchased = saveFile.metric_items_purchased;
        metricData.items_purged = saveFile.metric_items_purged;
        metricData.card_choices = saveFile.metric_card_choices;
        metricData.event_choices = saveFile.metric_event_choices;
        metricData.damage_taken = saveFile.metric_damage_taken;
        metricData.boss_relics = saveFile.metric_boss_relics;
        if (saveFile.metric_potions_obtained != null) {
            metricData.potions_obtained = saveFile.metric_potions_obtained;
        }

        if (saveFile.metric_relics_obtained != null) {
            metricData.relics_obtained = saveFile.metric_relics_obtained;
        }

        if (saveFile.metric_campfire_choices != null) {
            metricData.campfire_choices = saveFile.metric_campfire_choices;
        }

        if (saveFile.metric_item_purchase_floors != null) {
            metricData.item_purchase_floors = saveFile.metric_item_purchase_floors;
        }

        if (saveFile.metric_items_purged_floors != null) {
            metricData.items_purged_floors = saveFile.metric_items_purged_floors;
        }

        if (saveFile.neow_bonus != null) {
            metricData.neowBonus = saveFile.neow_bonus;
        }

        if (saveFile.neow_cost != null) {
            metricData.neowCost = saveFile.neow_cost;
        }
        ModSaves.ArrayListOfJsonElement modCardSaves = CardCrawlGame.saveFile.modCardSaves;
        int j = 0;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof CustomSavableRaw) {
                ((CustomSavableRaw)card).onLoadRaw((modCardSaves == null || j >= modCardSaves.size()) ? null : modCardSaves.get(j));
            }
            j++;
        }
        ModSaves.ArrayListOfJsonElement cardModifierSaves = CardCrawlGame.saveFile.cardModifierSaves;
        //TODO
        ModSaves.ArrayListOfJsonElement modRelicSaves = CardCrawlGame.saveFile.modRelicSaves;
        j = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof CustomSavableRaw) {
                ((CustomSavableRaw)relic).onLoadRaw((modRelicSaves == null || j >= modRelicSaves.size()) ? null : modRelicSaves.get(j));
            }
            j++;
        }
        ModSaves.ArrayListOfJsonElement modPotionSaves = CardCrawlGame.saveFile.modPotionSaves;
        j = 0;
        for (AbstractPotion potion : AbstractDungeon.player.potions) {
            if (potion instanceof CustomSavableRaw) {
                ((CustomSavableRaw)potion).onLoadRaw((modPotionSaves == null || j >= modPotionSaves.size()) ? null : modPotionSaves.get(j));
            }
            j++;
        }
        ModSaves.ArrayListOfString seenEventSaves = CardCrawlGame.saveFile.eventSaves;
        if (seenEventSaves != null) {
            AbstractDungeon.player.seenEvents.addAll(seenEventSaves);
        }
        ModSaves.HashMapOfJsonElement modSaves = CardCrawlGame.saveFile.modSaves;
        for (Map.Entry<String, CustomSavableRaw> field : BaseMod.getSaveFields().entrySet()) {
            field.getValue().onLoadRaw((modSaves == null) ? null : modSaves.get(field.getKey()));
        }
    }

    private static AbstractPlayer createCharacter(AbstractPlayer.PlayerClass selection) {
        AbstractPlayer p = characterManager.recreateCharacter(selection);
        for (AbstractCard c : p.masterDeck.group) {
            UnlockTracker.markCardAsSeen(c.cardID);
        }

        return p;
    }

    private void updateDebugSwitch() {
        if (Settings.isDev) {
            if (DevInputActionSet.toggleDebug.isJustPressed()) {
                Settings.isDebug = !Settings.isDebug;
            } else if (DevInputActionSet.toggleInfo.isJustPressed()) {
                Settings.isInfo = !Settings.isInfo;
            } else if (Settings.isDebug && DevInputActionSet.uploadData.isJustPressed()) {
                RelicLibrary.uploadRelicData();
                CardLibrary.uploadCardData();
                MonsterHelper.uploadEnemyData();
                PotionHelper.uploadPotionData();
                ModHelper.uploadModData();
                BlightHelper.uploadBlightData();
                BotDataUploader.uploadKeywordData();
            } else if (Settings.isDebug) {
                if (DevInputActionSet.hideTopBar.isJustPressed()) {
                    Settings.hideTopBar = !Settings.hideTopBar;
                } else if (DevInputActionSet.hidePopUps.isJustPressed()) {
                    Settings.hidePopupDetails = !Settings.hidePopupDetails;
                } else if (DevInputActionSet.hideRelics.isJustPressed()) {
                    Settings.hideRelics = !Settings.hideRelics;
                } else if (DevInputActionSet.hideCombatLowUI.isJustPressed()) {
                    Settings.hideLowerElements = !Settings.hideLowerElements;
                } else if (DevInputActionSet.hideCards.isJustPressed()) {
                    Settings.hideCards = !Settings.hideCards;
                } else if (!DevInputActionSet.hideEndTurnButton.isJustPressed()) {
                    if (DevInputActionSet.hideCombatInfo.isJustPressed()) {
                        Settings.hideCombatElements = !Settings.hideCombatElements;
                    }

                } else {
                    Settings.hideEndTurn = !Settings.hideEndTurn;
                    if (AbstractDungeon.getMonsters() != null) {
                        Iterator var1 = AbstractDungeon.getMonsters().monsters.iterator();

                        while(var1.hasNext()) {
                            AbstractMonster m = (AbstractMonster)var1.next();
                            m.damage(new DamageInfo(AbstractDungeon.player, m.currentHealth, DamageInfo.DamageType.HP_LOSS));
                        }

                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    public AbstractDungeon getDungeon(String key, AbstractPlayer p) {
        byte var4 = -1;
        switch(key.hashCode()) {
            case -1887678253:
                if (key.equals("Exordium")) {
                    var4 = 0;
                }
                break;
            case 313705820:
                if (key.equals("TheCity")) {
                    var4 = 1;
                }
                break;
            case 791401920:
                if (key.equals("TheBeyond")) {
                    var4 = 2;
                }
                break;
            case 884969688:
                if (key.equals("TheEnding")) {
                    var4 = 3;
                }
        }

        switch(var4) {
            case 0:
                ArrayList<String> emptyList = new ArrayList();
                return new Exordium(p, emptyList);
            case 1:
                return new TheCity(p, AbstractDungeon.specialOneTimeEventList);
            case 2:
                return new TheBeyond(p, AbstractDungeon.specialOneTimeEventList);
            case 3:
                return new TheEnding(p, AbstractDungeon.specialOneTimeEventList);
            default:
                return null;
        }
    }

    public AbstractDungeon getDungeon(String key, AbstractPlayer p, SaveFile saveFile) {
        byte var5 = -1;
        switch(key.hashCode()) {
            case -1887678253:
                if (key.equals("Exordium")) {
                    var5 = 0;
                }
                break;
            case 313705820:
                if (key.equals("TheCity")) {
                    var5 = 1;
                }
                break;
            case 791401920:
                if (key.equals("TheBeyond")) {
                    var5 = 2;
                }
                break;
            case 884969688:
                if (key.equals("TheEnding")) {
                    var5 = 3;
                }
        }

        switch(var5) {
            case 0:
                return new Exordium(p, saveFile);
            case 1:
                return new TheCity(p, saveFile);
            case 2:
                return new TheBeyond(p, saveFile);
            case 3:
                return new TheEnding(p, saveFile);
            default:
                return null;
        }
    }

    @Override
    public void pause() {
        logger.info("PAUSE()");
        Settings.isControllerMode = false;
        if (MUTE_IF_BG && mainMenuScreen != null) {
            Settings.isBackgrounded = true;
            if (mode == CardCrawlGame.GameMode.CHAR_SELECT) {
                mainMenuScreen.muteAmbienceVolume();
            } else if (AbstractDungeon.scene != null) {
                AbstractDungeon.scene.muteAmbienceVolume();
            }
        }

    }

    @Override
    public void resume() {
        logger.info("RESUME()");
        if (MUTE_IF_BG && mainMenuScreen != null) {
            Settings.isBackgrounded = false;
            if (mode == CardCrawlGame.GameMode.CHAR_SELECT) {
                mainMenuScreen.updateAmbienceVolume();
            } else if (AbstractDungeon.scene != null) {
                AbstractDungeon.scene.updateAmbienceVolume();
            }
        }

    }

    @Override
    public void dispose() {
        logger.info("Game shutting down...");
        logger.info("Flushing saves to disk...");
        AsyncSaver.shutdownSaveThread();
        if (SteamInputHandler.alive) {
            logger.info("Shutting down controller handler...");
            SteamInputHandler.alive = false;
            this.clientUtils.dispose();
            SteamInputHandler.controller.shutdown();
        }

        logger.info("Shutting down publisher integrations...");
        publisherIntegration.dispose();
        logger.info("Flushing logs to disk. Clean shutdown successful.");
    }

    public static String generateRandomAlias() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder retVal = new StringBuilder();

        for(int i = 0; i < 16; ++i) {
            retVal.append(alphabet.charAt(MathUtils.random(0, alphabet.length() - 1)));
        }

        return retVal.toString();
    }

    public static boolean isInARun() {
        return mode == CardCrawlGame.GameMode.GAMEPLAY && AbstractDungeon.player != null && !AbstractDungeon.player.isDead;
    }

    public static Texture getSaveSlotImg() {
        switch(saveSlot) {
            case 0:
                return ImageMaster.PROFILE_A;
            case 1:
                return ImageMaster.PROFILE_B;
            case 2:
                return ImageMaster.PROFILE_C;
            default:
                return ImageMaster.PROFILE_A;
        }
    }

    static {
        mode = CardCrawlGame.GameMode.CHAR_SELECT;
        startOver = false;
        queueCredits = false;
        playCreditsBgm = false;
        MUTE_IF_BG = false;
        chosenCharacter = null;
        saveFile = null;
        saveSlot = 0;
        monstersSlain = 0;
        elites1Slain = 0;
        elites2Slain = 0;
        elites3Slain = 0;
        elitesModdedSlain = 0;
        champion = 0;
        perfect = 0;
        overkill = false;
        combo = false;
        cheater = false;
        goldGained = 0;
        cardsPurged = 0;
        potionsBought = 0;
        mysteryMachine = 0;
        playtime = 0.0F;
        stopClock = false;
        trial = null;
        screenColor = Color.BLACK.cpy();
        screenTimer = 2.0F;
        screenTime = 2.0F;
        fadeIn = true;
        displayVersion = true;
        preferenceDir = null;
        logger = SpireAndroidLogger.getLogger(CardCrawlGame.class);
    }

    public static class GameMode {
        public static final GameMode CHAR_SELECT = new GameMode("CHAR_SELECT");
        public static final GameMode GAMEPLAY = new GameMode("GAMEPLAY");
        public static final GameMode DUNGEON_TRANSITION = new GameMode("DUNGEON_TRANSITION");
        public static final GameMode SPLASH = new GameMode("SPLASH");
        public final String name;

        private GameMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static GameMode add(String name) {
            return new GameMode(name);
        }
    }
}
