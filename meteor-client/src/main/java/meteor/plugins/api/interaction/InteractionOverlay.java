package meteor.plugins.api.interaction;

import meteor.input.MouseListener;
import meteor.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

public class InteractionOverlay extends Overlay implements MouseListener {
	private final InteractionConfig config;

	private Point lastClick;
	private Point lastMove;

	@Inject
	public InteractionOverlay(InteractionConfig config) {
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D g) {
		if (config.drawMouse()) {
			g.setFont(new Font("Tahoma", Font.BOLD, 18));

			if (lastClick != null) {
				OverlayUtil.renderTextLocation(g, new net.runelite.api.Point(lastClick.x - (g.getFont().getSize() / 3), lastClick.y + (g.getFont().getSize() / 3)), "X", Color.WHITE);
			}

			if (lastMove != null) {
				OverlayUtil.renderTextLocation(g, new net.runelite.api.Point(lastMove.x - (g.getFont().getSize() / 3), lastMove.y + (g.getFont().getSize() / 3)), "X", Color.GREEN);
			}
		}

		return null;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent) {
		lastClick = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent) {
		lastClick = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent) {
		lastClick = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent) {
		lastMove = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent) {
		lastMove = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent) {
		lastMove = mouseEvent.getPoint();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent) {
		lastMove = mouseEvent.getPoint();
		return mouseEvent;
	}
}
