package com.mygdx.magicstorm.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.magicstorm.Card;
import com.mygdx.magicstorm.Deck;
import com.mygdx.magicstorm.Enemies.Enemy;
import com.mygdx.magicstorm.Enemies.Goblin;
import com.mygdx.magicstorm.hero.Hero;
import com.mygdx.magicstorm.MagicStorm;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class MainGameScreen implements Screen {
    final MagicStorm game;
    Stage stage;
    private final MainGameScreen mainGameScreen = this;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private ArrayList<Actor> cardsDrawn;
    boolean touched = false;
    private Vector2 touchPos;
    MainPauseScreen pauseScreen;
    VictoryScreen victoryScreen;
    SpriteBatch batch = new SpriteBatch();
    boolean cardSelected = false;
    private ScreenViewport viewport;

    private Group group = new Group();

    private Hero hero;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Enemy currentEnemy;

    private boolean availableRewards;


    Group group2;
    Card selectedCard;
    int cardNo = 3;
    private Hero player;
    Card card1;
    Card card2;
    Card card3;
    Card drawnCard;
    int handSize;
    int spaceAtEachSide;
    int cardWidth;
    int cardHeight;
    int selectedCardX;
    int selectedCardY;
    boolean enemyTurn;
    boolean startOfBattle;
    boolean startOfTurn;
    Deck deck;
    private Random rand = new Random();

    public enum State {
        PAUSE,
        PLAYERTURN,
        STARTTURN,
        ENEMYTURN,
        VICTORY,
        RESUME
    }

    private State state = State.STARTTURN;

    public MainGameScreen(MagicStorm game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Group group1 = new Group();
        startOfBattle = true;
        enemyTurn = false;
        startOfTurn = true;
        availableRewards = true;
        Goblin goblin = new Goblin(10, 10);
        Hero hero = new Hero();
        card1 = new Card("attack", 10);
        card2 = new Card("attack", 10);
        card3 = new Card("defend", 5);
        Image background = new Image(new Texture(Gdx.files.internal("background.jpg")));
        Image endTurnButton = new Image(new Texture(Gdx.files.internal("endTurn.png")));
        Image mana = new Image(new Texture(Gdx.files.internal("Mana.png")));
        Image enemyTurn = new Image(new Texture(Gdx.files.internal("EnemyTurn.png")));
        Image startTurn = new Image(new Texture(Gdx.files.internal("startTurn.png")));
        Image nextStage = new Image(new Texture(Gdx.files.internal("nextStage.png")));
        Image rewardsButton = new Image(new Texture(Gdx.files.internal("rewardsButton.png")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hero.setName("hero");
        background.setName("background");
        goblin.setName("goblin");
        Goblin goblin1 = new Goblin(15,15);
        Goblin goblin2 = new Goblin(10,10);
        goblin1.setName("goblin1");
        goblin2.setName("goblin2");
        endTurnButton.setName("endTurnButton");
        mana.setName("mana");
        enemyTurn.setName("enemyTurn");
        startTurn.setName("startTurn");
        nextStage.setName("nextStage");
        rewardsButton.setName("rewardsButton");
        deck = new Deck(10, 10);
        deck.setName("deck");
        // order actors are drawn in

        group1.addActor(background);
        group1.addActor(mana);
        group1.addActor(hero);
        group1.addActor(card1);
        group1.addActor(card2);
        group1.addActor(card3);
        group1.addActor(goblin);
        group1.addActor(endTurnButton);
        group1.addActor(enemyTurn);
        group1.addActor(startTurn);
        group1.addActor(deck);
        group1.addActor(nextStage);
        group1.addActor(rewardsButton);

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cardNo = cards.size();

        currentEnemy = goblin;
        enemies.add(goblin1);
        enemies.add(goblin2);

        stage.addActor(group1);
        hero.setPosition(0, stage.getHeight() / 3);
        hero.setHpBarPos(hero.getX(), ((stage.getHeight() / 3) - 30));
        hero.setArmorBarPos(hero.getX(), ((stage.getHeight() / 3) - 60));



        endTurnButton.setPosition((stage.getWidth() * 4 / 5), stage.getHeight() * 1 / 10);
        mana.setPosition((stage.getWidth() * 1 / 5), stage.getHeight() * 1 / 10);
        enemyTurn.setPosition(stage.getWidth() * 1 / 4, stage.getHeight() * 3 / 4);
        enemyTurn.addAction(sequence(fadeOut(0f)));
        startTurn.setPosition(stage.getWidth() * 1 / 4, stage.getHeight() * 3 / 4);
        startTurn.addAction(sequence(fadeOut(0f)));
        nextStage.setPosition(stage.getWidth()* 4/5, stage.getHeight() * 1/5);
        nextStage.addAction(sequence(fadeOut(0f)));
        rewardsButton.setPosition(stage.getWidth()* 4/5, stage.getHeight() * 4/5);
        rewardsButton.addAction(fadeOut(0f));
        hero.setHpBarPos(0, (int) ((stage.getHeight() / 3) - 30));
        deck.setPosition(50, 50);

        cardWidth = (int) card1.getWidth();
        cardHeight = (int) card1.getHeight();
        shuffle();

        currentEnemy.setPosition(stage.getWidth() - currentEnemy.getWidth(), stage.getHeight() / 3);
        currentEnemy.setHpBarPos((int) (stage.getWidth() - currentEnemy.getWidth()), (int) ((stage.getHeight() / 3) - 30));


        Gdx.input.setInputProcessor(this.game);


    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        pauseScreen = new MainPauseScreen(this.game, this);
        victoryScreen = new VictoryScreen(this.game, this);
        Group group = (Group) stage.getActors().first();
        final Hero hero = group.findActor("hero");
        final Goblin goblin = group.findActor("goblin");
        Actor endTurn = group.findActor("enemyTurn");
        final Actor startTurn = group.findActor("startTurn");
        final Actor nextStage = group.findActor("nextStage");
        final Actor endTurnButton = group.findActor("endTurnButton");
        nextStage.setTouchable(Touchable.disabled);
        Deck deck = group.findActor("deck");
        Actor rewardsButton = group.findActor("rewardsButton");
        rewardsButton.setTouchable(Touchable.disabled);
        switch (state) {
            case PLAYERTURN:
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();
                if(currentEnemy.isDead()) {

                    if (availableRewards) {
                        rewardsButton.addAction(fadeIn(0f));
                        rewardsButton.setTouchable(Touchable.enabled);
                    } else {
                        nextStage.addAction(fadeIn(0f));
                        nextStage.setTouchable(Touchable.enabled);
                    }
                    endTurnButton.setTouchable(Touchable.disabled);
                    endTurnButton.addAction(fadeOut(0f));

                }
                //mouse click
                if (Gdx.input.isTouched() && !enemyTurn && !startOfTurn) {
                    Vector2 coord = stage.screenToStageCoordinates(new Vector2((float) Gdx.input.getX(), (float) Gdx.input.getY()));
                    final Actor hitActor = stage.hit(coord.x, coord.y, true);
                    if (hitActor != null) {
                        if ((hitActor instanceof Card && !cardSelected)) {
                            cardSelected = true;
                            selectedCard = (Card) hitActor;
                            selectedCardX = (int) selectedCard.getX();
                            selectedCardY = (int) selectedCard.getY();
                            RunnableAction ra = new RunnableAction();
                            ra.setRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    hitActor.setPosition((float) ((stage.getWidth() / 2) - ((cardWidth * 1.5) / 2)), (float) ((stage.getHeight()) / 2 - ((cardHeight * 1.5) / 2)));
                                    hitActor.setScale(1.5f);
                                }
                            });
                            hitActor.addAction(ra);

                        } else if (hitActor instanceof Card && !hitActor.equals(selectedCard) && cardSelected) {
                            selectedCard.setPosition(selectedCardX, selectedCardY);
                            selectedCard.setScale(1f);
                            selectedCard = (Card) hitActor;
                            selectedCardX = (int) selectedCard.getX();
                            selectedCardY = (int) selectedCard.getY();
                            RunnableAction ra = new RunnableAction();
                            ra.setRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    hitActor.setPosition((float) ((stage.getWidth() / 2) - ((cardWidth * 1.5) / 2)), (float) ((stage.getHeight()) / 2 - ((cardHeight * 1.5) / 2)));
                                    hitActor.setScale(1.5f);
                                }
                            });
                            hitActor.addAction(ra);

                        } else if ((hitActor instanceof Enemy && cardSelected && selectedCard.getName().equals("attack"))) {
                            selectedCard.dealDamage(currentEnemy);
                            cardSelected = false;
                            selectedCard.remove();
                            cardNo -= 1;
                            cards.remove(selectedCard);
                            shuffle();

                        } else if ((hitActor instanceof Hero && cardSelected && selectedCard.getName().equals("defend"))) {
                            selectedCard.addDefence(hero);
                            cardSelected = false;
                            selectedCard.remove();
                            cardNo -= 1;
                            cards.remove(selectedCard);
                            shuffle();

                        } else if (hitActor.getName().equals("endTurnButton")) {
                            enemyTurn = true;
                            this.state = State.ENEMYTURN;
                        } else if (hitActor.getName().equals("rewardsButton")) {
                            availableRewards = false;
                            rewardsButton.setTouchable(Touchable.disabled);
                            rewardsButton.addAction(fadeOut(0f));
                        } else if (hitActor.getName().equals("nextStage")) {

                            if (enemies.size() <= 0) {
                                this.state = State.VICTORY;
                                game.setScreen(victoryScreen);
                            }
                            else {
                                int random = rand.nextInt(enemies.size());
                                currentEnemy = enemies.remove(random);
                                stage.addActor(currentEnemy);
                                currentEnemy.draw(batch, delta);
                                currentEnemy.setPosition(stage.getWidth() - currentEnemy.getWidth(), stage.getHeight() / 3);
                                currentEnemy.setHpBarPos((int) (stage.getWidth() - currentEnemy.getWidth()), (int) ((stage.getHeight() / 3) - 30));
                                nextStage.addAction(fadeOut(0f));
                                startOfBattle = true;
                                this.state = State.STARTTURN;
                            }
                        }
                    }
                }

                if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                    selectedCard.setPosition(selectedCardX, selectedCardY);
                    selectedCard.setScale(1f);
                    cardSelected = false;
                }
                break;
            case ENEMYTURN:
                startTurn.addAction(fadeOut(0f));
                endTurn.addAction(sequence(fadeIn(1f), fadeOut(1f)));
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if (!currentEnemy.isDead()) {
                            currentEnemy.attack(hero);
                        }
                        enemyTurn = false;
                    }
                }, 3);
                this.state = State.STARTTURN;
                break;
            case STARTTURN:
                startOfTurn = true;
                if (startOfBattle) {
                    drawCard(2);
                    availableRewards = true;
                    startOfBattle = false;
                    startTurn.addAction(sequence(fadeIn(1f), fadeOut(1f)));
                    startOfTurn = false;
                    endTurnButton.setTouchable(Touchable.enabled);
                    endTurnButton.addAction(fadeIn(0f));
                } else {
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            startTurn.addAction(sequence(fadeIn(1f), fadeOut(1f)));
                            hero.setMana(5);
                            drawCard(2);
                            startOfTurn = false;
                        }
                    }, 4);
                }

                this.state = State.PLAYERTURN;
                //draw 2
                break;
            case PAUSE:
                break;
            case VICTORY:
                break;
            case RESUME:
                Gdx.graphics.setContinuousRendering(true);
                if (enemyTurn) {
                    this.state = State.ENEMYTURN;
                } else {
                    this.state = State.PLAYERTURN;
                }
                break;
            default:
                this.state = State.PLAYERTURN;
                break;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            resume();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            pause();
        }
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        this.state = State.PAUSE;
        game.setScreen(pauseScreen);
    }

    @Override
    public void resume() {
        this.state = State.PLAYERTURN;
        game.setScreen(game.getScreen());
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    //aligns hand with centre of screen
    public void shuffle() {
        handSize = (int) (cardNo * cardWidth);
        spaceAtEachSide = (int) ((stage.getWidth() - handSize) / 2);
        for (int i = 0; i < cards.size(); i++) {
            final Actor tempActor = cards.get(i);
            tempActor.setPosition(spaceAtEachSide, 0);
            spaceAtEachSide += 120;
        }
    }

    public void drawCard(int numberDrawn) {
        for (int i = 0; i < numberDrawn; i++) {
            if (deck.getSize() > 0) {
                drawnCard = deck.drawCard();
                cards.add(drawnCard);
                stage.addActor(drawnCard);
                cardNo += 1;
            }
            else break;
        }
        shuffle();
    }
}
