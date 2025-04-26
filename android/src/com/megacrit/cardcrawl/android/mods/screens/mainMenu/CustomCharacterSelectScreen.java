package com.megacrit.cardcrawl.android.mods.screens.mainMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.android.mods.Loader;
import com.megacrit.cardcrawl.android.mods.ModInfo;
import com.megacrit.cardcrawl.android.mods.interfaces.IUIElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;

import java.util.ArrayList;
import java.util.Comparator;

public class CustomCharacterSelectScreen extends CharacterSelectScreen {
    private int optionsPerIndex = 4;
    private int selectIndex = 0;
    private int maxSelectIndex;
    private int optionsIndex;
    private CustomCharacterSelectScreen.LeftOptionsButton leftArrow;
    private CustomCharacterSelectScreen.RightOptionsButton rightArrow;
    private ArrayList<CharacterOption> allOptions;

    public CustomCharacterSelectScreen() {
        this.leftArrow = new CustomCharacterSelectScreen.LeftOptionsButton("images/ui/tinyLeftArrow.png", (int)(425.0F * Settings.scale), (int)((float)(Settings.isFourByThree ? 244 : 180) * Settings.scale));
        this.rightArrow = new CustomCharacterSelectScreen.RightOptionsButton("images/ui/tinyRightArrow.png", (int)(1425.0F * Settings.scale), (int)((float)(Settings.isFourByThree ? 244 : 180) * Settings.scale));
        this.updateOptionsIndex();
        this.allOptions = new ArrayList<>();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.allOptions.clear();

        for (ModInfo info : Loader.MODINFOS) {
            if (info.isLoaded) {
                try {
                    this.options.addAll(BaseMod.generateCharacterOptions(info.modId));
                } catch (ReflectionException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.options.sort(Comparator.comparing((o) -> o.name));

        this.allOptions.addAll(this.options);

        if (this.allOptions.size() == this.optionsPerIndex + 1) {
            ++this.optionsPerIndex;
        }

        this.selectIndex = 0;
        this.updateOptionsIndex();
        this.maxSelectIndex = (int)Math.ceil((double)((float)this.allOptions.size() / (float)this.optionsPerIndex)) - 1;
        this.options = new ArrayList<>(this.allOptions.subList(0, Math.min(this.optionsPerIndex, this.allOptions.size())));
        this.positionButtons();
    }

    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.selectIndex < this.maxSelectIndex) {
            this.rightArrow.render(sb);
        }

        if (this.selectIndex != 0) {
            this.leftArrow.render(sb);
        }
    }

    public void update() {
        super.update();
        if (this.selectIndex < this.maxSelectIndex) {
            this.rightArrow.update();
        }

        if (this.selectIndex != 0) {
            this.leftArrow.update();
        }
    }

    private void positionButtons() {
        int count = this.options.size();
        float offsetX = (float)Settings.WIDTH / 2.0F - (float)this.optionsPerIndex / 2.0F * 220.0F * Settings.scale + 110.0F * Settings.scale;

        for(int i = 0; i < count; ++i) {
            this.options.get(i).hb.move(offsetX + (float)i * 220.0F * Settings.scale, (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale);
        }

        this.leftArrow.move(offsetX - 220.0F * Settings.scale, (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale);
        this.rightArrow.move(offsetX + (float)count * 220.0F * Settings.scale, (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale);
    }

    private void setCurrentOptions(boolean rightClicked) {
        if (rightClicked && this.selectIndex < this.maxSelectIndex) {
            ++this.selectIndex;
        } else if (!rightClicked && this.selectIndex > 0) {
            --this.selectIndex;
        }

        this.updateOptionsIndex();
        int endIndex = this.optionsIndex + this.optionsPerIndex;
        this.options = new ArrayList<>(this.allOptions.subList(this.optionsIndex, Math.min(this.allOptions.size(), endIndex)));
        this.options.forEach((o) -> o.selected = false);
        this.positionButtons();
    }

    private void updateOptionsIndex() {
        this.optionsIndex = this.optionsPerIndex * this.selectIndex;
    }

    private class RightOptionsButton implements IUIElement {
        private Texture arrow;
        private int x;
        private int y;
        private int w;
        private int h;
        private Hitbox hitbox;

        public RightOptionsButton(String imgUrl, int x, int y) {
            this.arrow = ImageMaster.loadImage(imgUrl);
            this.x = x;
            this.y = y;
            this.w = (int)(Settings.scale * (float)this.arrow.getWidth());
            this.h = (int)(Settings.scale * (float)this.arrow.getHeight());
            this.hitbox = new Hitbox((float)x, (float)y, (float)this.w, (float)this.h);
        }

        public void move(float newX, float newY) {
            this.x = (int)(newX - (float)this.w / 2.0F);
            this.y = (int)(newY - (float)this.h / 2.0F);
            this.hitbox.move(newX, newY);
        }

        public void render(SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            sb.draw(this.arrow, (float)this.x, (float)this.y, (float)this.w, (float)this.h);
            this.hitbox.render(sb);
        }

        public void update() {
            this.hitbox.update();
            if (this.hitbox.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                CustomCharacterSelectScreen.this.setCurrentOptions(true);
            }

        }

        public int renderLayer() {
            return 0;
        }

        public int updateOrder() {
            return 0;
        }
    }

    private class LeftOptionsButton implements IUIElement {
        private Texture arrow;
        private int x;
        private int y;
        private int w;
        private int h;
        private Hitbox hitbox;

        public LeftOptionsButton(String imgUrl, int x, int y) {
            this.arrow = ImageMaster.loadImage(imgUrl);
            this.x = x;
            this.y = y;
            this.w = (int)(Settings.scale * (float)this.arrow.getWidth());
            this.h = (int)(Settings.scale * (float)this.arrow.getHeight());
            this.hitbox = new Hitbox((float)x, (float)y, (float)this.w, (float)this.h);
        }

        public void move(float newX, float newY) {
            this.x = (int)(newX - (float)this.w / 2.0F);
            this.y = (int)(newY - (float)this.h / 2.0F);
            this.hitbox.move(newX, newY);
        }

        public void render(SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            sb.draw(this.arrow, (float)this.x, (float)this.y, (float)this.w, (float)this.h);
            this.hitbox.render(sb);
        }

        public void update() {
            this.hitbox.update();
            if (this.hitbox.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                CustomCharacterSelectScreen.this.setCurrentOptions(false);
            }

        }

        public int renderLayer() {
            return 0;
        }

        public int updateOrder() {
            return 0;
        }
    }
}
