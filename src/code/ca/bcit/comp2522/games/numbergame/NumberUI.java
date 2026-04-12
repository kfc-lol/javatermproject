package ca.bcit.comp2522.games.numbergame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Unified UI manager for the Number Game.
 * Handles both the main menu scene and the game scene.
 * Provides scene switching and state management.
 *
 * @author Kian Castro
 * @version 1.o
 */
public class NumberUI {

    // ---------------------------------------------------------------
    //  Color Palette (Black & White Theme)
    // ---------------------------------------------------------------
    private static final String BG_DARK      = "#000000";   // Black background
    private static final String BG_PANEL     = "#1a1a1a";   // Dark gray panels
    private static final String TEXT_PRIMARY = "#ffffff";   // White text
    private static final String TEXT_MUTED   = "#cccccc";   // Light gray text
    private static final String BORDER_COLOR = "#333333";   // Dark gray borders
    private static final String ACCENT_RED   = "#ffffff";   // White (for accents)

    // ---------------------------------------------------------------
    //  Font Constants
    // ---------------------------------------------------------------
    private static final String FONT_FAMILY = "Courier New";
    private static final int FONT_SIZE_TITLE = 52;
    private static final int FONT_SIZE_SUBTITLE = 22;
    private static final int FONT_SIZE_BODY = 14;
    private static final int FONT_SIZE_SMALL = 12;

    private final Stage stage;
    private final NumberScore tracker;
    private Scene menuScene;
    private Scene gameScene;

    /**
     * Construct a NumberUI with the given stage and score tracker.
     *
     * @param stageParam the primary JavaFX stage
     * @param trackerParam the shared score accumulator
     */
    public NumberUI(Stage stageParam, NumberScore trackerParam) {
        this.stage = stageParam;
        this.tracker = trackerParam;
    }

    /**
     * Show the main menu scene.
     *
     * @param scoreSummary optional summary shown when returning from a game session;
     *                     pass null or empty string to omit
     */
    public void showMenu(String scoreSummary) {
        menuScene = createMenuScene(scoreSummary);
        stage.setScene(menuScene);
    }

    /**
     * Show the game scene.
     */
    public void showGame() {
        gameScene = createGameScene();
        stage.setScene(gameScene);
    }

    /**
     * Create the main menu scene.
     *
     * @param scoreSummary optional summary text
     * @return the menu Scene
     */
    private Scene createMenuScene(String scoreSummary) {
        // ---- Root layout ------------------------------------------------
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // ---- Title ------------------------------------------------------
        Label title = new Label("NUMBER GAME");
        title.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_TITLE));
        title.setTextFill(Color.web(TEXT_PRIMARY));

        Label subtitle = new Label("4 × 5  •  20 NUMBERS  •  ASCENDING ORDER");
        subtitle.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web(TEXT_MUTED));
        subtitle.setStyle("-fx-letter-spacing: 3px;");

        // ---- Description card -------------------------------------------
        VBox card = buildCard(
            "How to play",
            "The game reveals one random number (1–1000) at a time.\n"
            + "Click any empty cell to place it.\n"
            + "All 20 numbers must end up in ascending order left-to-right.\n"
            + "A bad placement ends the round immediately!"
        );

        // ---- Score panel (shown on return from game) --------------------
        VBox scorePanel = new VBox();
        if (scoreSummary != null && !scoreSummary.isBlank()) {
            scorePanel = buildCard("Session Score", scoreSummary);
            scorePanel.setStyle(scorePanel.getStyle()
                                + "-fx-border-color: " + TEXT_PRIMARY + ";");
        }

        // ---- Play button ------------------------------------------------
        Button playBtn = buildPrimaryButton("▶  PLAY");
        playBtn.setOnAction(e -> showGame());

        // ---- Assemble ---------------------------------------------------
        root.getChildren().addAll(title, subtitle, card);
        if (scoreSummary != null && !scoreSummary.isBlank()) {
            root.getChildren().add(scorePanel);
        }
        root.getChildren().add(playBtn);

        return new Scene(root, 680, 620);
    }

    /**
     * Create the game scene.
     *
     * @return the game Scene
     */
    private Scene createGameScene() {
        NumberGame game = new NumberGame();
        game.startGame();

        // Create slots for each cell in the grid
        Slot[] slots = new Slot[20];
        for (int i = 0; i < 20; i++) {
            slots[i] = new Slot();
        }

        // Mutable scene reference used by inner helpers
        Scene[] sceneHolder = new Scene[1];

        // ---- Header bar ------------------------------------------------
        Label titleLbl = new Label("NUMBER GAME");
        titleLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_SUBTITLE));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label roundLbl = new Label(roundLabel(tracker));
        roundLbl.setFont(Font.font(FONT_FAMILY, 13));
        roundLbl.setTextFill(Color.web(TEXT_MUTED));

        HBox header = new HBox(titleLbl, createHorizontalSpacer(), roundLbl);
        header.setPadding(new Insets(16, 24, 12, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: transparent transparent " + BORDER_COLOR + " transparent;");

        // ---- Current-number display ------------------------------------
        Label cueLabel = new Label("PLACE THIS NUMBER");
        cueLabel.setFont(Font.font(FONT_FAMILY, 12));
        cueLabel.setTextFill(Color.web(TEXT_MUTED));

        Label currentNumLbl = new Label(String.valueOf(game.getCurrentNumber()));
        currentNumLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 64));
        currentNumLbl.setTextFill(Color.web(TEXT_PRIMARY));

        VBox currentPanel = new VBox(4, cueLabel, currentNumLbl);
        currentPanel.setAlignment(Pos.CENTER);
        currentPanel.setPadding(new Insets(18, 0, 14, 0));

        // ---- 4 × 5 button grid -----------------------------------------
        Button[] cells = new Button[20];
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(8, 24, 8, 24));

        final int COLS = 5;
        for (int i = 0; i < 20; i++) {
            final int idx = i;
            Button btn = buildCellButton("", false);
            btn.setPrefSize(106, 72);

            // Position label (1-20) in small text inside the button
            Label posLbl = new Label(String.valueOf(i + 1));
            posLbl.setFont(Font.font(FONT_FAMILY, 9));
            posLbl.setTextFill(Color.web("#444c56"));

            StackPane cell = new StackPane(btn, posLbl);
            StackPane.setAlignment(posLbl, Pos.TOP_LEFT);
            posLbl.setTranslateX(6);
            posLbl.setTranslateY(4);

            btn.setOnAction(e -> handleCellClick(
                idx, game, tracker, cells, slots, currentNumLbl,
                roundLbl, sceneHolder));

            cells[i] = btn;
            grid.add(cell, i % COLS, i / COLS);
        }

        // ---- Status bar ------------------------------------------------
        Label progressLbl = new Label(progressText(game));
        progressLbl.setFont(Font.font(FONT_FAMILY, 13));
        progressLbl.setTextFill(Color.web(TEXT_MUTED));

        Label hintLbl = new Label("Click any empty cell to place the number above.");
        hintLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 12));
        hintLbl.setTextFill(Color.web(TEXT_PRIMARY));

        VBox status = new VBox(4, progressLbl, hintLbl);
        status.setAlignment(Pos.CENTER);
        status.setPadding(new Insets(10, 24, 20, 24));

        // ---- Root layout -----------------------------------------------
        VBox root = new VBox(header, currentPanel, grid, status);
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        Scene scene = new Scene(root, 680, 580);
        sceneHolder[0] = scene;

        // Attach live references so the click handler can mutate them
        scene.getProperties().put("progressLbl", progressLbl);
        scene.getProperties().put("hintLbl", hintLbl);

        return scene;
    }

    /**
     * Handle a cell click in the game grid.
     */
    private void handleCellClick(
        int idx,
        NumberGame game,
        NumberScore tracker,
        Button[] cells,
        Slot[] slots,
        Label currentNumLbl,
        Label roundLbl,
        Scene[] sceneHolder) {

        // Get the slot for this cell
        Slot slot = slots[idx];

        // Slot already occupied – silently ignore
        if (slot.isOccupied()) return;

        int numBeforePlacement = game.getCurrentNumber();
        boolean accepted = game.placeNumber(idx);
        // ---- Out-of-order placement – silently ignore, wait for valid placement ----
        if (!accepted) {
            return;
        }

        // ---- Retrieve labels stored in scene properties ----------------
        Scene scene = sceneHolder[0];
        Label progressLbl = (Label) scene.getProperties().get("progressLbl");
        Label hintLbl = (Label) scene.getProperties().get("hintLbl");

        // ---- Successful placement -----------------------------------------------
        // Place the value in the slot
        slot.placeValue(numBeforePlacement);

        updateCell(cells[idx], numBeforePlacement, false);
        flashCell(cells[idx]);

        // ---- Check if game just ended due to no valid placements ----
        if (game.isGameOver() && !game.isWon()) {
            // No valid slots remain for the next number
            disableAllCells(cells);
            tracker.recordLoss(game.getSuccessfulPlacements());
            roundLbl.setText(roundLabel(tracker));
            showEndDialog(game, tracker, game.getCurrentNumber());
        } else if (game.isWon()) {
            // All numbers placed successfully
            disableAllCells(cells);
            tracker.recordWin(game.getSuccessfulPlacements());
            roundLbl.setText(roundLabel(tracker));
            showEndDialog(game, tracker, -1);
        } else {
            // ---- Round still in progress --------------------------------
            currentNumLbl.setText(String.valueOf(game.getCurrentNumber()));
            progressLbl.setText(progressText(game));
            hintLbl.setText("Click any empty cell to place the number above.");

            // Animate the new number dropping in
            currentNumLbl.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(350), currentNumLbl);
            ft.setToValue(1.0);
            ft.play();
        }
    }

    /**
     * Show the end-of-round dialog.
     * @param attemptedNumber the next number that has no valid slots (or -1 if won)
     */
    private void showEndDialog(NumberGame game, NumberScore tracker, int attemptedNumber) {
        boolean won = game.isWon();

        // ---- Overlay pane ----------------------------------------------
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(13,17,23,0.82);");

        // ---- Dialog card -----------------------------------------------
        String headlineText = won ? "🎉  YOU WIN!" : "💀  GAME OVER";
        Label headline = new Label(headlineText);
        headline.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 36));
        headline.setTextFill(Color.web(TEXT_PRIMARY));

        String reasonText;
        if (won) {
            reasonText = "All 20 numbers placed in perfect ascending order!";
        } else {
            // Only reached here if no valid slots remain for the next number
            reasonText = "No valid slot remains for number " + attemptedNumber + ".\n"
                       + "Cannot maintain ascending order with remaining empty cells.";
        }

        Label reasonLbl = new Label(reasonText);
        reasonLbl.setFont(Font.font(FONT_FAMILY, 14));
        reasonLbl.setTextFill(Color.web(TEXT_MUTED));
        reasonLbl.setWrapText(true);
        reasonLbl.setMaxWidth(400);
        reasonLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label placementLbl = new Label(
            "Placements this round: " + game.getSuccessfulPlacements() + " / 20");
        placementLbl.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 15));
        placementLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Separator sep = new Separator();
        sep.setMaxWidth(380);

        Label summaryLbl = new Label(tracker.getSummary());
        summaryLbl.setFont(Font.font(FONT_FAMILY, 13));
        summaryLbl.setTextFill(Color.web(TEXT_MUTED));
        summaryLbl.setWrapText(true);
        summaryLbl.setMaxWidth(400);
        summaryLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // ---- Buttons ---------------------------------------------------
        Button tryAgainBtn = buildSecondaryButton("↺  TRY AGAIN", TEXT_PRIMARY);
        tryAgainBtn.setOnAction(e -> showGame());

        Button quitBtn = buildSecondaryButton("⬡  QUIT", ACCENT_RED);
        quitBtn.setOnAction(e -> showMenu(tracker.getSummary()));

        HBox buttons = new HBox(20, tryAgainBtn, quitBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox dialog = new VBox(16,
                               headline, reasonLbl, placementLbl, sep, summaryLbl, buttons);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(36, 40, 36, 40));
        dialog.setMaxWidth(480);
        dialog.setStyle(
            "-fx-background-color: " + BG_PANEL + ";"
            + "-fx-border-color: " + BORDER_COLOR + ";"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 12;"
            + "-fx-background-radius: 12;");

        overlay.getChildren().add(dialog);

        // Animate the dialog in
        dialog.setOpacity(0);
        dialog.setScaleX(0.85);
        dialog.setScaleY(0.85);

        // Get the current scene and replace its root with an overlay wrapper
        Scene scene = stage.getScene();
        VBox existingRoot = (VBox) scene.getRoot();
        StackPane wrapper = new StackPane(existingRoot, overlay);
        scene.setRoot(wrapper);

        FadeTransition fade = new FadeTransition(Duration.millis(280), dialog);
        fade.setToValue(1.0);
        ScaleTransition scale = new ScaleTransition(Duration.millis(280), dialog);
        scale.setToX(1.0);
        scale.setToY(1.0);
        ParallelTransition pt = new ParallelTransition(fade, scale);
        pt.play();
    }

    /**
     * Flash animation for a cell button.
     */
    private void flashCell(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.12);
        st.setToY(1.12);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    /**
     * Disable all cell buttons.
     */
    private void disableAllCells(Button[] cells) {
        for (Button c : cells) c.setDisable(true);
    }

    /**
     * Generate progress text.
     */
    private String progressText(NumberGame game) {
        int placed = game.getSuccessfulPlacements();
        return "Progress: " + placed + " / 20 placed";
    }

    /**
     * Generate round label.
     */
    private String roundLabel(NumberScore t) {
        if (!t.hasGames()) return "Round 1";
        return "Round " + (t.getTotalGames() + 1)
               + "  •  W: " + t.getWins() + "  L: " + t.getLosses();
    }

    // ---------------------------------------------------------------
    //  UI Helper Methods
    // ---------------------------------------------------------------

    /**
     * Build a styled card with heading and body text.
     */
    private VBox buildCard(String heading, String body) {
        Label h = new Label(heading.toUpperCase());
        h.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 13));
        h.setTextFill(Color.web(TEXT_PRIMARY));

        Label b = new Label(body);
        b.setFont(Font.font(FONT_FAMILY, FONT_SIZE_BODY));
        b.setTextFill(Color.web(TEXT_PRIMARY));
        b.setWrapText(true);
        b.setMaxWidth(520);
        b.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);

        VBox box = new VBox(8, h, b);
        box.setPadding(new Insets(18, 24, 18, 24));
        box.setMaxWidth(560);
        box.setStyle(
            "-fx-background-color: " + BG_PANEL + ";"
            + "-fx-border-color: "     + BORDER_COLOR + ";"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;");
        return box;
    }

    /**
     * Build a primary action button (solid background).
     */
    private Button buildPrimaryButton(final String text) {
        final Button btn = new Button(text);
        btn.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
        btn.setTextFill(Color.web(BG_DARK));
        btn.setPrefWidth(220);
        btn.setPrefHeight(48);
        String baseStyle =
            "-fx-background-color: " + TEXT_PRIMARY + ";"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(baseStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    /**
     * Build a secondary button (outline style).
     */
    private Button buildSecondaryButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 15));
        btn.setTextFill(Color.web(color));
        btn.setPrefWidth(180);
        btn.setPrefHeight(44);
        String baseStyle =
            "-fx-background-color: transparent;"
            + "-fx-border-color: " + color + ";"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(baseStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    /**
     * Build a grid cell button with styling.
     */
    private Button buildCellButton(String text, boolean filled) {
        Button btn = new Button(text);
        btn.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 22));
        btn.setTextFill(Color.web(TEXT_PRIMARY));
        applyCellStyle(btn, filled, false);
        return btn;
    }

    /**
     * Apply styling to a cell button based on its state.
     */
    private void applyCellStyle(Button btn, boolean filled, boolean invalid) {
        String bg     = invalid ? "#3d1b1b"
            : filled  ? "#1b2d4f"
            :           "#1c2128";
        String border = invalid ? ACCENT_RED
            : filled  ? TEXT_PRIMARY
            :           BORDER_COLOR;
        btn.setStyle(
            "-fx-background-color: " + bg + ";"
            + "-fx-border-color: " + border + ";"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;"
            + (filled || invalid ? "" : "-fx-cursor: hand;"));
    }

    /**
     * Update a cell button with text and styling.
     */
    private void updateCell(Button btn, int num, boolean invalid) {
        btn.setText(String.valueOf(num));
        applyCellStyle(btn, !invalid, invalid);
        btn.setTextFill(Color.web(invalid ? TEXT_MUTED : TEXT_PRIMARY));
    }

    /**
     * Create a horizontal spacer that grows to fill available space.
     */
    private Region createHorizontalSpacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}