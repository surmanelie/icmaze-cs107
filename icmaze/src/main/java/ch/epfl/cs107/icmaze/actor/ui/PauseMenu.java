package ch.epfl.cs107.icmaze.actor.ui;

import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.Color;

public class PauseMenu implements Graphics {

    // Dimensions en unités monde
    private static final float MENU_SIZE = 6f;
    private static final float TITLE_SIZE = 0.55f;
    private static final float TEXT_SIZE = 0.4f;

    // Profondeurs
    private static final int DEPTH_BACKGROUND = 3000;
    private static final int DEPTH_TEXT = 3001;

    @Override
    public void draw(Canvas canvas) {
        // 1. Récupérer le centre de la caméra (référence spatiale unique)
        Vector center = canvas.getPosition();

        // 2. Calculer l'ancre du fond pour qu'il soit bien CENTRÉ
        // ImageGraphics dessine depuis l'ancre vers le haut-droite (par défaut)
        // Donc pour centrer, on décale l'ancre de la moitié de la taille vers le
        // bas-gauche
        Vector bgAnchor = center.sub(new Vector(MENU_SIZE / 2, MENU_SIZE / 2));

        ImageGraphics background = new ImageGraphics(
                ResourcePath.getSprite("cellOver"),
                MENU_SIZE,
                MENU_SIZE,
                new RegionOfInterest(0, 0, 64, 64),
                bgAnchor,
                1f, // Alpha
                DEPTH_BACKGROUND);
        background.draw(canvas);

        // 3. Dessiner le Titre (centré haut)
        TextGraphics title = new TextGraphics("PAUSE", TITLE_SIZE, Color.WHITE);
        title.setAnchor(center.add(new Vector(-1.0f, 1.2f))); // Ajustement visuel relatif au centre
        title.setDepth(DEPTH_TEXT);
        title.draw(canvas);

        // 4. Instruction Reprendre (milieu)
        TextGraphics resume = new TextGraphics("P : Reprendre", TEXT_SIZE, Color.WHITE);
        resume.setAnchor(center.add(new Vector(-1.4f, -0.2f)));
        resume.setDepth(DEPTH_TEXT);
        resume.draw(canvas);

        // 5. Instruction Reset (bas)
        TextGraphics reset = new TextGraphics("R : Reset", TEXT_SIZE, Color.WHITE);
        reset.setAnchor(center.add(new Vector(-1.4f, -1.0f)));
        reset.setDepth(DEPTH_TEXT);
        reset.draw(canvas);
    }
}
