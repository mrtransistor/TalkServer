import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class AdminWindow {

	/** Textfeld für Adminausgaben */
	JTextArea printAreaAdminBox;
	/** Scrollpane für Textfeld */
	JScrollPane textScroll;
	
	/**
	 * Konstruktor von AdminWindow.
	 * Zeichnet auschließlich das AdminWindow.
	 */
	public AdminWindow() {
		drawAdminWindow();
	}
	/**
	 * Zeichne Adminbox
	 */
	private void drawAdminWindow() {
		JFrame adminFrame = new JFrame("AdminBox");
		adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		printAreaAdminBox = new JTextArea();
		printAreaAdminBox.setSize(300, 200);
		printAreaAdminBox.setVisible(true);
		textScroll = new JScrollPane(printAreaAdminBox);
		textScroll.setVisible(true);
		adminFrame.add(textScroll);
		adminFrame.setSize(300, 200);
		adminFrame.setLocation(725, 265);
		adminFrame.setVisible(true);
	}
	
	/**
	 * Zeigt Adminausgaen in der AdminBox an
	 * @param text
	 */
	public void showMessageAdmin(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						printAreaAdminBox.append("\n" + text);
					}
				}
			);
	}
}
