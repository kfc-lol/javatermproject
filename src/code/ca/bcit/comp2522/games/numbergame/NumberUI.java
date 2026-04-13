package ca.bcit.comp2522.games.numbergame;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Unified UI manager for the Number Game.
 * Handles both the main menu scene and the game scene.
 * Provides scene switching and state management.
 *
 * @author Kian Castro
 * @version 1.0
 */
public class NumberUI
{

    private static final int COLUMNS = 5;
    private static final int ROWS = 4;

    // ---------------------------------------------------------------
    //  Stylesheet
    // ---------------------------------------------------------------
    private static final String CSS_PATH = "/ca/bcit/comp2522/games/numbergame/numbergame.css";

    // ---------------------------------------------------------------
    //  Scene dimensions
    // ---------------------------------------------------------------
    private static final int SCENE_MENU_WIDTH  = 680;
    private static final int SCENE_MENU_HEIGHT = 620;
    private static final int SCENE_GAME_WIDTH  = 680;
    private static final int SCENE_GAME_HEIGHT = 580;

    // ---------------------------------------------------------------
    //  Grid structure
    // ---------------------------------------------------------------
    private static final int GRID_COLS      = 5;
    private static final int GRID_GAP       = 8;
    private static final int GRID_CELL_COUNT = Game.getGridSize();

    // ---------------------------------------------------------------
    //  Cell button dimensions
    // ---------------------------------------------------------------
    private static final int CELL_BTN_WIDTH  = 106;
    private static final int CELL_BTN_HEIGHT = 72;

    // ---------------------------------------------------------------
    //  Cell position label offsets
    // ---------------------------------------------------------------
    private static final int POS_LABEL_OFFSET_X = 6;
    private static final int POS_LABEL_OFFSET_Y = 4;

    // ---------------------------------------------------------------
    //  Animation durations (milliseconds)
    // ---------------------------------------------------------------
    private static final int ANIM_FADE_IN_MS   = 350;
    private static final int ANIM_FLASH_MS     = 120;
    private static final int ANIM_FLASH_SCALE  = 2;
    private static final int ANIM_SLIDE_MS     = 320;
    private static final int LOSS_DELAY_MS     = 1200;

    // ---------------------------------------------------------------
    //  Flash scale factor
    // ---------------------------------------------------------------
    private static final double FLASH_SCALE_FACTOR = 1.12;

    // ---------------------------------------------------------------
    //  End-panel expand height
    // ---------------------------------------------------------------
    private static final int END_PANEL_HEIGHT = 160;

    // ---------------------------------------------------------------
    //  Animation start/end values
    // ---------------------------------------------------------------
    private static final double ANIM_OPACITY_START = 0.0;
    private static final double ANIM_OPACITY_END   = 1.0;
    private static final double ANIM_SCALE_START   = 1.0;
    private static final double ANIM_SLIDE_START   = 0.0;

    // ---------------------------------------------------------------
    //  Sentinel / offset values
    // ---------------------------------------------------------------
    private static final int NO_ATTEMPTED_NUMBER  = -1;
    private static final int NEXT_ROUND_OFFSET    = 1;
    private static final int CELL_DISPLAY_OFFSET  = 1;

    // ---------------------------------------------------------------
    //  UI text literals
    // ---------------------------------------------------------------
    private static final String TEXT_GAME_TITLE      = "NUMBER GAME";
    private static final String TEXT_MENU_SUBTITLE   = ROWS + " × " + COLUMNS + "  •  " + GRID_CELL_COUNT + " NUMBERS  •  ASCENDING ORDER";
    private static final String TEXT_HOW_TO_PLAY     = "How to play";
    private static final String MIN_RANDOM_NUM       = "1";
    private static final String MAX_RANDOM_NUM       = "1000";

    private static final String TEXT_HOW_TO_BODY =
        "The game reveals one random number (" + MIN_RANDOM_NUM + "–" + MAX_RANDOM_NUM + ") at a time.\n"
        + "Click any empty cell to place it.\n"
        + "All 20 numbers must end up in ascending order left-to-right.\n"
        + "A bad placement ends the round immediately!";
    private static final String TEXT_SESSION_SCORE   = "Session Score";
    private static final String TEXT_PLAY_BTN        = "PLAY";
    private static final String TEXT_CUE             = "PLACE THIS NUMBER";
    private static final String TEXT_HINT_DEFAULT    = "Click any empty cell to place the number above.";
    private static final String TEXT_TRY_AGAIN       = "TRY AGAIN";
    private static final String TEXT_QUIT            = "QUIT";
    private static final String TEXT_WIN_HEADLINE    = "YOU WIN!";
    private static final String TEXT_LOSS_HEADLINE   = "GAME OVER";
    private static final String TEXT_WIN_REASON      = "All numbers placed in perfect ascending order!";
    private static final String TEXT_ROUND_ONE       = "Round 1";

    // ---------------------------------------------------------------
    //  CSS style-class names
    // ---------------------------------------------------------------
    private static final String CSS_MENU_ROOT        = "menu-root";
    private static final String CSS_MENU_TITLE       = "menu-title";
    private static final String CSS_MENU_SUBTITLE    = "menu-subtitle";
    private static final String CSS_HEADER           = "header";
    private static final String CSS_HEADER_TITLE     = "header-title";
    private static final String CSS_HEADER_ROUND     = "header-round";
    private static final String CSS_CARD             = "card";
    private static final String CSS_CARD_HEADING     = "card-heading";
    private static final String CSS_CARD_BODY        = "card-body";
    private static final String CSS_CARD_HIGHLIGHT   = "card-highlight";
    private static final String CSS_CURRENT_PANEL    = "current-panel";
    private static final String CSS_CUE_LABEL        = "cue-label";
    private static final String CSS_CURRENT_NUMBER   = "current-number";
    private static final String CSS_STATUS_BAR       = "status-bar";
    private static final String CSS_PROGRESS         = "progress-label";
    private static final String CSS_HINT             = "hint-label";
    private static final String CSS_HINT_WARNING     = "hint-label-warning";
    private static final String CSS_CELL_EMPTY       = "cell-empty";
    private static final String CSS_CELL_FILLED      = "cell-filled";
    private static final String CSS_POS_LABEL        = "cell-position-label";
    private static final String CSS_BTN_PRIMARY      = "btn-primary";
    private static final String CSS_BTN_SECONDARY    = "btn-secondary";
    private static final String CSS_END_PANEL        = "end-panel";
    private static final String CSS_END_HEADLINE     = "end-headline";
    private static final String CSS_END_REASON       = "end-reason";
    private static final String CSS_END_STATS        = "end-stats";

    // ---------------------------------------------------------------
    //  Scene-property keys
    // ---------------------------------------------------------------
    private static final String PROP_PROGRESS_LBL = "progressLbl";
    private static final String PROP_HINT_LBL     = "hintLbl";
    public static final int FIRST_SCENE = 0;

    // ---------------------------------------------------------------
    //  Fields
    // ---------------------------------------------------------------
    private final Stage       stage;
    private final NumberScore tracker;
    private Scene menuScene;
    private Scene gameScene;

    /**
     * Construct a NumberUI with the given stage and score tracker.
     *
     * @param stageParam   the primary JavaFX stage
     * @param trackerParam the shared score accumulator
     */
    public NumberUI(final Stage stageParam,
                    final NumberScore trackerParam)
    {
        this.stage   = stageParam;
        this.tracker = trackerParam;
    }

    /**
     * Show the main menu scene.
     *
     * @param scoreSummary optional summary shown when returning from a game session;
     *                     pass null or empty string to omit
     */
    public void showMenu(final String scoreSummary)
    {
        menuScene = createMenuScene(scoreSummary);
        stage.setScene(menuScene);
    }

    /**
     * Show the game scene.
     */
    public void showGame()
    {
        gameScene = createGameScene();
        stage.setScene(gameScene);
    }

    // ---------------------------------------------------------------
    //  Scene builders
    // ---------------------------------------------------------------

    private Scene createMenuScene(final String scoreSummary)
    {
        final VBox root;
        final Label title;
        final Label subtitle;
        final VBox card;
        final Button playBtn;
        final Scene scene;

        root = new VBox();
        root.getStyleClass().add(CSS_MENU_ROOT);

        title = new Label(TEXT_GAME_TITLE);
        title.getStyleClass().add(CSS_MENU_TITLE);

        subtitle = new Label(TEXT_MENU_SUBTITLE);
        subtitle.getStyleClass().add(CSS_MENU_SUBTITLE);

        card = buildCard(TEXT_HOW_TO_PLAY, TEXT_HOW_TO_BODY);

        playBtn = new Button(TEXT_PLAY_BTN);
        playBtn.getStyleClass().add(CSS_BTN_PRIMARY);
        playBtn.setOnAction(e -> showGame());

        root.getChildren().addAll(title, subtitle, card);

        if (scoreSummary != null && !scoreSummary.isBlank())
        {
            VBox scorePanel = buildCard(TEXT_SESSION_SCORE, scoreSummary);
            scorePanel.getStyleClass().add(CSS_CARD_HIGHLIGHT);
            root.getChildren().add(scorePanel);
        }

        root.getChildren().add(playBtn);

        scene = new Scene(root, SCENE_MENU_WIDTH, SCENE_MENU_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        return scene;
    }

    private Scene createGameScene()
    {
        // ---------------------------------------------------------------
        //  Declarations
        // ---------------------------------------------------------------
        final NumberGame game;

        final Slot[]  slots;
        final Scene[] sceneHolder;

        final Label titleLbl;
        final Label roundLbl;
        final HBox  header;

        final Label cueLabel;
        final Label currentNumLbl;
        final VBox  currentPanel;

        final Button[] cells;
        final GridPane grid;

        final Label progressLbl;
        final Label hintLbl;
        final VBox  status;

        final VBox root;
        final Scene scene;

        // ---------------------------------------------------------------
        //  Initialization
        // ---------------------------------------------------------------
        game = new NumberGame();
        game.startGame();

        slots = new Slot[GRID_CELL_COUNT];
        sceneHolder = new Scene[1];

        for (int i = 0; i < GRID_CELL_COUNT; i++)
        {
            slots[i] = new Slot();
        }

        // ---- Header bar ------------------------------------------------
        titleLbl = new Label(TEXT_GAME_TITLE);
        titleLbl.getStyleClass().add(CSS_HEADER_TITLE);

        roundLbl = new Label(roundLabel(tracker));
        roundLbl.getStyleClass().add(CSS_HEADER_ROUND);

        header = new HBox(titleLbl, createHorizontalSpacer(), roundLbl);
        header.getStyleClass().add(CSS_HEADER);
        header.setAlignment(Pos.CENTER_LEFT);

        // ---- Current-number display ------------------------------------
        cueLabel = new Label(TEXT_CUE);
        cueLabel.getStyleClass().add(CSS_CUE_LABEL);

        currentNumLbl = new Label(String.valueOf(game.getCurrentNumber()));
        currentNumLbl.getStyleClass().add(CSS_CURRENT_NUMBER);

        currentPanel = new VBox(cueLabel, currentNumLbl);
        currentPanel.getStyleClass().add(CSS_CURRENT_PANEL);

        // ---- Grid ------------------------------------------------------
        cells = new Button[GRID_CELL_COUNT];
        grid  = new GridPane();

        grid.setHgap(GRID_GAP);
        grid.setVgap(GRID_GAP);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < GRID_CELL_COUNT; i++)
        {
            final int idx;
            final Button btn;
            final Label posLbl;
            final StackPane cell;

            idx = i;

            btn = new Button();
            btn.getStyleClass().add(CSS_CELL_EMPTY);
            btn.setPrefSize(CELL_BTN_WIDTH, CELL_BTN_HEIGHT);

            posLbl = new Label(String.valueOf(i + CELL_DISPLAY_OFFSET));
            posLbl.getStyleClass().add(CSS_POS_LABEL);

            cell = new StackPane(btn, posLbl);
            StackPane.setAlignment(posLbl, Pos.TOP_LEFT);

            posLbl.setTranslateX(POS_LABEL_OFFSET_X);
            posLbl.setTranslateY(POS_LABEL_OFFSET_Y);

            btn.setOnAction(e -> handleCellClick(
                idx, game, tracker, cells, slots, currentNumLbl, roundLbl, sceneHolder));

            cells[i] = btn;
            grid.add(cell, i % GRID_COLS, i / GRID_COLS);
        }

        // ---- Status bar ------------------------------------------------
        progressLbl = new Label(progressText(game));
        progressLbl.getStyleClass().add(CSS_PROGRESS);

        hintLbl = new Label(TEXT_HINT_DEFAULT);
        hintLbl.getStyleClass().add(CSS_HINT);

        status = new VBox(progressLbl, hintLbl);
        status.getStyleClass().add(CSS_STATUS_BAR);

        // ---- Root ------------------------------------------------------
        root = new VBox(header, currentPanel, grid, status);

        scene = new Scene(root, SCENE_GAME_WIDTH, SCENE_GAME_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        sceneHolder[FIRST_SCENE] = scene;

        scene.getProperties().put(PROP_PROGRESS_LBL, progressLbl);
        scene.getProperties().put(PROP_HINT_LBL, hintLbl);

        return scene;
    }

    // ---------------------------------------------------------------
    //  Click handler
    // ---------------------------------------------------------------

    private void handleCellClick(
        final int         idx,
        final NumberGame  game,
        final NumberScore tracker,
        final Button[]    cells,
        final Slot[]      slots,
        final Label       currentNumLbl,
        final Label       roundLbl,
        final Scene[]     sceneHolder)
    {
        final Slot slot;
        slot = slots[idx];

        if (slot.isOccupied())
        {
            return;
        }

        final int numBeforePlacement;
        final boolean accepted;

        numBeforePlacement = game.getCurrentNumber();
        accepted = game.placeNumber(idx);

        if (!accepted)
        {
            return;
        }

        final Scene scene ;
        final Label progressLbl;
        final Label hintLbl;

        scene = sceneHolder[ FIRST_SCENE ];
        progressLbl= (Label) scene.getProperties().get(PROP_PROGRESS_LBL);
        hintLbl= (Label) scene.getProperties().get(PROP_HINT_LBL);

        slot.placeValue(numBeforePlacement);
        markCellFilled(cells[idx], numBeforePlacement);
        flashCell(cells[idx]);

        if (game.isWon())
        {
            disableAllCells(cells);
            tracker.recordWin(game.getSuccessfulPlacements());
            roundLbl.setText(roundLabel(tracker));
            showEndPanel(game, tracker, NO_ATTEMPTED_NUMBER);

        }
        else if (game.isGameOver())
        {
            final int nextNumber;
            nextNumber = game.getCurrentNumber();

            currentNumLbl.setText(String.valueOf(nextNumber));
            progressLbl.setText(progressText(game));
            hintLbl.setText("No valid cell exists for " + nextNumber + "!");
            hintLbl.getStyleClass().setAll(CSS_HINT_WARNING);

            fadeIn(currentNumLbl);
            disableAllCells(cells);

            final PauseTransition pause;
            pause = new PauseTransition(Duration.millis(LOSS_DELAY_MS));
            pause.setOnFinished(e -> {
                tracker.recordLoss(game.getSuccessfulPlacements());
                roundLbl.setText(roundLabel(tracker));
                showEndPanel(game, tracker, nextNumber);
            });
            pause.play();

        }
        else
        {
            currentNumLbl.setText(String.valueOf(game.getCurrentNumber()));
            progressLbl.setText(progressText(game));
            hintLbl.setText(TEXT_HINT_DEFAULT);
            hintLbl.getStyleClass().setAll(CSS_HINT);
            fadeIn(currentNumLbl);
        }
    }

    // ---------------------------------------------------------------
    //  End panel
    // ---------------------------------------------------------------

    private void showEndPanel(final NumberGame  game,
                              final NumberScore tracker,
                              final int         attemptedNumber)
    {
        final boolean won;

        final Label headline;
        final String headlineText;

        final String reasonText;
        final Label reasonLbl;
        final Label statsLbl;

        final Button tryAgainBtn;
        final Button quitBtn;

        final HBox buttons;
        final VBox panel;

        final Scene scene;
        final VBox newRoot;

        final TranslateTransition slide;
        final FadeTransition fade;
        final ParallelTransition animation;

        won = game.isWon();

        if (won)
        {
            headlineText = TEXT_WIN_HEADLINE;
        }
        else
        {
            headlineText = TEXT_LOSS_HEADLINE;
        }

        headline = new Label(headlineText);
        headline.getStyleClass().add(CSS_END_HEADLINE);

        if (won)
        {
            reasonText = TEXT_WIN_REASON;
        }
        else
        {
            reasonText = "No valid slot for " + attemptedNumber
                         + " — ascending order cannot be maintained.";
        }

        reasonLbl = new Label(reasonText);
        reasonLbl.getStyleClass().add(CSS_END_REASON);

        statsLbl = new Label(
            "Placed: " + game.getSuccessfulPlacements() + " / " + GRID_CELL_COUNT
            + "   •   " + tracker.getSummary());
        statsLbl.getStyleClass().add(CSS_END_STATS);

        tryAgainBtn = new Button(TEXT_TRY_AGAIN);
        tryAgainBtn.getStyleClass().add(CSS_BTN_SECONDARY);
        tryAgainBtn.setOnAction(e -> showGame());

        quitBtn = new Button(TEXT_QUIT);
        quitBtn.getStyleClass().add(CSS_BTN_SECONDARY);
        quitBtn.setOnAction(e -> showMenu(tracker.getSummary()));

        buttons = new HBox(GRID_GAP, tryAgainBtn, quitBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        panel = new VBox(headline, reasonLbl, statsLbl, buttons);
        panel.getStyleClass().add(CSS_END_PANEL);

        scene  = stage.getScene();
        newRoot = new VBox(scene.getRoot(), panel);
        scene.setRoot(newRoot);

        stage.setHeight(stage.getHeight() + END_PANEL_HEIGHT);

        panel.setTranslateY(END_PANEL_HEIGHT);
        panel.setOpacity(ANIM_OPACITY_START);

        slide = new TranslateTransition(Duration.millis(ANIM_SLIDE_MS), panel);
        slide.setToY(ANIM_SLIDE_START);

        fade = new FadeTransition(Duration.millis(ANIM_SLIDE_MS), panel);
        fade.setToValue(ANIM_OPACITY_END);

        animation = new ParallelTransition(slide, fade);
        animation.play();
    }

    // ---------------------------------------------------------------
    //  UI helpers
    // ---------------------------------------------------------------

    private void fadeIn(final Label label)
    {
        label.setOpacity(ANIM_OPACITY_START);
        final FadeTransition ft;
        ft = new FadeTransition(Duration.millis(ANIM_FADE_IN_MS), label);
        ft.setToValue(ANIM_OPACITY_END);
        ft.play();
    }

    private void flashCell(final Button btn)
    {
        final ScaleTransition st;
        st = new ScaleTransition(Duration.millis(ANIM_FLASH_MS), btn);
        st.setFromX(ANIM_SCALE_START);
        st.setFromY(ANIM_SCALE_START);
        st.setToX(FLASH_SCALE_FACTOR);
        st.setToY(FLASH_SCALE_FACTOR);
        st.setAutoReverse(true);
        st.setCycleCount(ANIM_FLASH_SCALE);
        st.play();
    }

    private void markCellFilled(final Button btn, final int num)
    {
        btn.setText(String.valueOf(num));
        btn.getStyleClass().setAll(CSS_CELL_FILLED);
    }

    private void disableAllCells(final Button[] cells)
    {
        for (final Button c : cells) c.setDisable(true);
    }

    private String progressText(final NumberGame game)
    {
        return "Progress: " + game.getSuccessfulPlacements() + " / " + GRID_CELL_COUNT + " placed";
    }

    private String roundLabel(final NumberScore t)
    {
        if (!t.hasGames())
        {
            return TEXT_ROUND_ONE;
        }
        return "Round " + (t.getTotalGames() + NEXT_ROUND_OFFSET)
               + "  •  W: " + t.getWins() + "  L: " + t.getLosses();
    }

    private VBox buildCard(final String heading,
                           final String body)
    {
        final Label h;
        final Label b;
        final VBox box;

        h = new Label(heading.toUpperCase());
        h.getStyleClass().add(CSS_CARD_HEADING);

        b = new Label(body);
        b.getStyleClass().add(CSS_CARD_BODY);

        box = new VBox(h, b);
        box.getStyleClass().add(CSS_CARD);
        return box;
    }

    private Region createHorizontalSpacer()
    {
        final Region region;
        region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
}