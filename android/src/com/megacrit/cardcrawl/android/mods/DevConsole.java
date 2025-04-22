package com.megacrit.cardcrawl.android.mods;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.android.SpireAndroidLogger;
import com.megacrit.cardcrawl.android.mods.devCommands.ConsoleCommand;
import com.megacrit.cardcrawl.android.mods.helpers.input.ScrollInputProcessor.TextInput;
import com.megacrit.cardcrawl.android.mods.interfaces.*;
import com.megacrit.cardcrawl.android.mods.utils.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DevConsole implements PostEnergyRechargeSubscriber, PostInitializeSubscriber, PostRenderSubscriber, PostUpdateSubscriber, TextReceiver {
    private static final int HISTORY_SIZE = 10;
    public static final float CONSOLE_X = 200.0F;
    public static final float CONSOLE_Y = 200.0F;
    public static final float CONSOLE_W = 800.0F;
    public static final float CONSOLE_H = 40.0F;
    public static final float CONSOLE_PAD_X = 15.0F;
    public static final int CONSOLE_TEXT_SIZE = 30;
    private static final int MAX_LINES = 8;
    public static final String PATTERN = "[\\s]+";
    public static final String PROMPT = "$> ";
    public static BitmapFont consoleFont = null;
    public static Texture consoleBackground = null;
    public static boolean infiniteEnergy = false;
    public static boolean forceUnlocks = false;
    public static int unlockLevel = -1;
    public static boolean visible = false;
    public static int toggleKey = 68;
    public static String currentText = "";
    private static boolean wasBackspace = false;
    public static int priorKey = Input.Keys.SEMICOLON;
    public static int nextKey = Input.Keys.PLUS;
    public static ArrayList<String> log;
    public static ArrayList<Boolean> prompted;
    public static DevConsole.PriorCommandsList priorCommands;
    public static int commandPos;
    private static final SpireAndroidLogger logger = SpireAndroidLogger.getLogger(DevConsole.class);
    public Hitbox callOutHb;


    public DevConsole() {
        BaseMod.subscribe(this);
        priorCommands = new DevConsole.PriorCommandsList();
        commandPos = -1;
        log = new ArrayList<>(priorCommands);
        prompted = new ArrayList<>(Collections.nCopies(log.size(), true));
        ConsoleCommand.initialize();
        callOutHb = new Hitbox(230.0F * Settings.xScale, 110.0F * Settings.yScale);
    }

    public static void execute() {
        String[] tokens = currentText.trim().split("[\\s]+");
        if (priorCommands.size() == 0 || !(priorCommands.get(0)).equals(currentText)) {
            priorCommands.add(0, currentText);
        }

        log.add(0, currentText);
        prompted.add(0, true);
        commandPos = -1;
        currentText = "";
        if (tokens.length >= 1) {
            for(int i = 0; i < tokens.length; ++i) {
                tokens[i] = tokens[i].trim();
            }

            ConsoleCommand.execute(tokens);
        }
    }

    public static void couldNotParse() {
        log("could not parse previous command");
    }

    public static void log(String text) {
        log.add(0, text);
        prompted.add(0, false);
    }

    public static void log(Collection<?> list) {
        for (Object o : list) {
            log(o.toString());
        }
    }

    @Override
    public void receivePostInitialize() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Kreon-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(30.0F * Settings.scale);
        consoleFont = generator.generateFont(parameter);
        generator.dispose();
        consoleBackground = ImageMaster.loadImage("images/ConsoleBackground.png");
        AutoComplete.postInit();
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (visible && consoleFont != null) {
            int sizeToDraw = log.size() + 1;
            if (sizeToDraw > 8) {
                sizeToDraw = 8;
            }
            sb.setColor(Color.WHITE);
            sb.draw(consoleBackground, 200.0F * Settings.scale, 800.0F * Settings.scale, 800.0F * Settings.scale, 40.0F * Settings.scale + 30.0F * Settings.scale * (float)(sizeToDraw - 1));
            if (AutoComplete.enabled) {
                AutoComplete.render(sb);
            }

            float x = 200.0F * Settings.scale + 15.0F * Settings.scale;
            float y = 800.0F * Settings.scale + (float)Math.floor((double)(30.0F * Settings.scale));
            consoleFont.draw(sb, "$> " + currentText, x, y);

            for(int i = 0; i < sizeToDraw - 1; ++i) {
                y += (float)Math.floor((double)(30.0F * Settings.scale));
                consoleFont.draw(sb, (prompted.get(i) ? "$> " : "") + log.get(i), x, y);
            }
        }
        Texture buttonImg;
        if (!this.callOutHb.clickStarted) {
            buttonImg = ImageMaster.END_TURN_BUTTON_GLOW;
        } else {
            buttonImg = ImageMaster.END_TURN_BUTTON;
        }/*
        sb.setColor(Color.WHITE);
        sb.draw(buttonImg, callOutHb.x, callOutHb.y - 64.0F, 128.0F, 128.0F, 256.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 256, 256, false, false);
        FontHelper.renderFontCentered(sb, FontHelper.panelEndTurnFont, "dev", callOutHb.x + 128.0F, callOutHb.y + 64.0F - 3.0F * Settings.scale, Settings.GOLD_COLOR);
        callOutHb.render(sb);*/
    }

    @Override
    public void receivePostUpdate() {
        /*
        callOutHb.move(1640.0F * Settings.xScale, 810.0F * Settings.yScale);
        callOutHb.update();*/
        if (Gdx.input.justTouched()) {
            if (callOutHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                AutoComplete.reset();
                if (visible) {
                    currentText = "";
                    commandPos = -1;
                    TextInput.stopTextReceiver(this);
                } else {
                    TextInput.startTextReceiver(this);
                    Gdx.input.setOnscreenKeyboardVisible(true);
                    if (AutoComplete.enabled) {
                        AutoComplete.suggest(false);
                    }
                }

                visible = !visible;
            }
        }
    }

    @Override
    public void receivePostEnergyRecharge() {
        if (infiniteEnergy) {
            EnergyPanel.setEnergy(9999);
        }
    }

    @Override
    public String getCurrentText() {
        return currentText;
    }

    @Override
    public void setText(String updatedText) {
        currentText = updatedText;
        AutoComplete.suggest(wasBackspace);
        wasBackspace = false;
    }

    @Override
    public boolean isDone() {
        return !visible;
    }

    @Override
    public boolean onKeyDown(int keycode) {
        if (AutoComplete.enabled) {
            if (keycode == priorKey) {
                if (Gdx.input.isKeyPressed(AutoComplete.selectKey)) {
                    AutoComplete.selectUp();
                } else if (commandPos + 1 < priorCommands.size()) {
                    ++commandPos;
                    currentText = priorCommands.get(commandPos);
                    AutoComplete.resetAndSuggest();
                }

                return true;
            }

            if (keycode == nextKey) {
                if (Gdx.input.isKeyPressed(AutoComplete.selectKey)) {
                    AutoComplete.selectDown();
                } else {
                    if (commandPos - 1 < 0) {
                        currentText = "";
                        commandPos = -1;
                    } else {
                        --commandPos;
                        currentText = priorCommands.get(commandPos);
                    }

                    AutoComplete.resetAndSuggest();
                }

                return true;
            }

            if (keycode == AutoComplete.fillKey1 || keycode == AutoComplete.fillKey2) {
                AutoComplete.fillInSuggestion();
                return true;
            }

            if (keycode == AutoComplete.deleteTokenKey) {
                AutoComplete.removeOneTokenUsingSpaceAndIdDelimiter();
                if (AutoComplete.enabled) {
                    AutoComplete.suggest(false);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keycode) {
        if (keycode == 66) {
            if (currentText.length() > 0) {
                execute();
                if (AutoComplete.enabled) {
                    AutoComplete.reset();
                    AutoComplete.suggest(false);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPushBackspace() {
        wasBackspace = true;
        return false;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return consoleFont.getData().hasGlyph(c) && (Input.Keys.toString(toggleKey) == null || !Input.Keys.toString(toggleKey).equals("" + c));
    }

    public static class PriorCommandsList extends ArrayList<String> {
        private static final String HISTORY_LOCATION = SpireConfig.makeFilePath("BaseMod", "console-history", "txt");

        public PriorCommandsList() {
            try {
                List<String> list = Files.readAllLines(Paths.get(HISTORY_LOCATION), StandardCharsets.UTF_8);
                this.addAll(list);
            } catch (IOException var2) {
                DevConsole.logger.error("Failed to load dev console history: " + var2);
            }

        }

        private void saveHistory() {
            try {
                Files.write(Paths.get(HISTORY_LOCATION), this.subList(0, Math.min(10, this.size())), StandardCharsets.UTF_8);
            } catch (IOException var2) {
                DevConsole.logger.error("Failed to save dev console history: " + var2);
            }

        }

        public boolean add(String s) {
            boolean ret = super.add(s);
            this.saveHistory();
            return ret;
        }

        public void add(int index, String element) {
            super.add(index, element);
            this.saveHistory();
        }

        public void clear() {
            super.clear();
            this.saveHistory();
        }
    }
}
