package ru.geekbrains.dungeon.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import ru.geekbrains.dungeon.GameController;
import ru.geekbrains.dungeon.GameMap;

public class Hero extends Unit {
    float movementTime;
    float movementMaxTime;
    int targetX, targetY;
    int skill;
    int mv;
    int prCellX;
    int prCellY;

    public Hero(TextureAtlas atlas, GameController gc) {
        super(gc, 1, 1, 10, 5);
        this.texture = atlas.findRegion("knight");
        this.textureHp = atlas.findRegion("hp");
        this.movementMaxTime = 0.2f;
        this.targetX = cellX;
        this.targetY = cellY;
        this.prCellX = cellX;
        this.prCellY = cellY;
        this.skill = 0;
        mv = mvMax;
    }

    public int getSkill() {
        return skill;
    }

    public void update(float dt) {
        checkMovement(dt);
    }

    public boolean isStayStill() {
        return cellY == targetY && cellX == targetX;
    }

    public void checkMovement(float dt) {

        if (Gdx.input.justTouched() && isStayStill()) {
            if (Math.abs(gc.getCursorX() - cellX) + Math.abs(gc.getCursorY() - cellY) == 1) {
                targetX = gc.getCursorX();
                targetY = gc.getCursorY();
            }
        }

        Monster m = gc.getMonsterController().getMonsterInCell(targetX, targetY);
        if (m != null) {
            targetX = cellX;
            targetY = cellY;
            if (m.takeDamage(1)) {
                ++skill;
            }
            this.takeDamage(1, 0.25f);
        }

        if (!gc.getGameMap().isCellPassable(targetX, targetY)) {
            targetX = cellX;
            targetY = cellY;
        }

        if (!isStayStill()) {
            movementTime += dt;
            if (movementTime > movementMaxTime) {
                movementTime = 0;
                cellX = targetX;
                cellY = targetY;
            }
        }

        // Считаем шаги (Правильно я понял, что шаги не считаются при ударе монстра?)
        if (prCellX != cellX || prCellY != cellY) {
            --mv;
            prCellX = cellX;
            prCellY = cellY;

            if (mv <= 0) {
                mv = mvMax;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float px = cellX * GameMap.CELL_SIZE;
        float py = cellY * GameMap.CELL_SIZE;
        if (!isStayStill()) {
            px = cellX * GameMap.CELL_SIZE + (targetX - cellX) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
            py = cellY * GameMap.CELL_SIZE + (targetY - cellY) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
        }
        batch.draw(texture, px, py);
        batch.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        batch.draw(textureHp, px + 1, py + 51, 58, 10);
        batch.setColor(0.7f, 0.0f, 0.0f, 1.0f);
        batch.draw(textureHp, px + 2, py + 52, 56, 8);
        batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        batch.draw(textureHp, px + 2, py + 52, (float) hp / hpMax * 56, 8);

        // Шкала количества шагов
        batch.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        batch.draw(textureHp, px + 1, py + 1, 58, 10);
        batch.setColor(0.7f, 0.0f, 0.0f, 1.0f);
        batch.draw(textureHp, px + 2, py + 2, 56, 8);
        batch.setColor(0.0f, 0.0f, 1.0f, 1.0f);
        batch.draw(textureHp, px + 2, py + 2, (float) mv / mvMax * 56, 8);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);



    }
}
